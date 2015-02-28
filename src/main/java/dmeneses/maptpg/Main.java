package dmeneses.maptpg;


import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import lombok.extern.log4j.Log4j2;

import org.xml.sax.SAXException;

import com.google.common.base.Stopwatch;
import com.javadocmd.simplelatlng.LatLng;

import dmeneses.maptpg.config.Configuration;
import dmeneses.maptpg.database.DAO;
import dmeneses.maptpg.database.Persistence;
import dmeneses.maptpg.database.ResultsManager;
import dmeneses.maptpg.datacollection.Collector;
import dmeneses.maptpg.image.Renderer;
import dmeneses.maptpg.image.Scale;
import dmeneses.maptpg.image.gradient.Gradient;
import dmeneses.maptpg.image.gradient.GradientFactory;
import dmeneses.maptpg.map.CircleF;
import dmeneses.maptpg.map.GoogleMapsProjection2;
import dmeneses.maptpg.map.MapTools;
import dmeneses.maptpg.map.PolygonF;
import dmeneses.maptpg.map.ShapeF;
import dmeneses.maptpg.map.UnionShape;
import dmeneses.maptpg.process.Itinerary;
import dmeneses.maptpg.process.Wrapper;

@Log4j2
public class Main {
	public static void generate(Configuration config) throws JAXBException, ParserConfigurationException, IOException, SAXException, URISyntaxException {
		/*
		 * Init stuff and validate data
		 */
		GoogleMapsProjection2 proj = new GoogleMapsProjection2();
		PolygonF poly = MapTools.loadKml(Configuration.KML_LOCATION);
		Map<String, LatLng> locations = new HashMap<String, LatLng>();
		locations.put("Gare_Cornavin", new LatLng(46.20974027671904,6.14185631275177));
		locations.put("CERN", new LatLng(46.23345666795791,6.054754257202148));
		locations.put("Hôpital", new LatLng(46.193654020521876,6.149017810821533));
		locations.put("Meyrin", new LatLng(46.23317980458792, 6.079859733581543));
		locations.put("UniMail", new LatLng(46.19458572140302, 6.140531301498413));
		locations.put("Plainpalais", new LatLng(46.198083549211496, 6.140938997268677));
		locations.put("Carouge", new LatLng(46.18374584531457, 6.139490604400635));
		locations.put("Perle_du_Lac", new LatLng(46.220290673596956, 6.15259051322937));
		locations.put("Aéroport", new LatLng(46.23046112582612, 6.108779311180115));
		locations.put("UN", new LatLng(46.224050717186515, 6.139640808105469));

		LatLng src = locations.get(config.getSourceLocation().replace(' ', '_'));
		if(src == null) {
			log.error("Invalid source location: {}", config.getSourceLocation());
			return;
		}

		/*
		 * Get Points
		 */
		ShapeF circle = new CircleF(proj.fromLatLngToPoint(new LatLng(46.209418049302556, 6.121101379394531), 0), 0.12);
		ShapeF shape = new UnionShape(circle, poly);
		List<LatLng> dsts = MapTools.getPointList(shape.getSurroundingBox(), config.getNumPoints(), config.getNumPoints());

		log.info("topLeft: {}", proj.fromPointToLatLng(shape.getSurroundingBox()[0], 0));
		log.info("bottomRight: {}", proj.fromPointToLatLng(shape.getSurroundingBox()[1], 0));

		//double resX = LatLngTool.distance(proj.fromPointToLatLng(shape.getSurroundingBox()[0],
		//		new LatLng(proj.fromPointToLatLng(shape.getSurroundingBox()[0].getLatitude(),
		//				proj.fromPointToLatLng(shape.getSurroundingBox()[1], 0), LengthUnit.METER);
		//double resY = LatLngTool.distance(shape.getSurroundingBox()[0],
		//		new LatLng(proj.fromPointToLatLng(shape.getSurroundingBox()[1].getLatitude(),
		//				proj.fromPointToLatLng(shape.getSurroundingBox()[0].getLongitude()), LengthUnit.METER);
		//log.info(("Points resolution: " + resX + "m X " + resY + " m (X/Y)");


		/*
		 * Itinerary calculation
		 */
		List<Itinerary> itineraries = null;
		if(config.getLoadPath() == null) {
			Stopwatch watch = Stopwatch.createStarted();

			Collector.setCacheRoot(Configuration.CACHE_ROOT);
			Calendar c = Calendar.getInstance();
			c.set(2013, 10, 8, config.getStartHour(), 0, 0);
			Date startDate = c.getTime();

			itineraries = getItineraries(src, dsts, startDate);

			log.info("Done ({})", watch);

			//
			// Save results
			//
			watch.reset().start();
			log.info("Saving results... ");
			ResultsManager.save(config.getName() + ".txt", itineraries);
			log.info("Wrote to {}.txt", config.getName());
			log.info("Done ({})", watch);
		}
		else {
			itineraries = ResultsManager.load(config.getLoadPath());
		}

		/*
		 * Image generation
		 */
		Stopwatch watch = Stopwatch.createStarted();
		log.info("Processing results... ");
		int side = (int) Math.sqrt(itineraries.size());
		int tile_size = config.getImageSize()/(side-1);

		log.debug(Integer.toString(side));
		Double[][] data = new Double[side][side];
		double max = Double.MIN_VALUE;
		double min = Double.MAX_VALUE;
		for(int i = 0; i < side; i++) {
			for(int j = 0; j<side; j++) {
				Itinerary it = itineraries.get(i*side+j);
				if(it != null) {
					double v = it.getData(config.getDataType());
					if(v > max) {
						max = v;
					}
					if(v < min) {
						min = v;
					}

					data[i][j] = v;
				}
				else {
					data[i][j] = null;
				}
			}
		}
		log.debug("min: {}, max: {}", min, max);
		log.info("Done ({})", watch);

		watch.reset().start();
		log.info("Creating images... ");

		if(config.getMaxScale() != null) {
			max = config.getMaxScale();
		}
		Gradient g = GradientFactory.createGradient(config.getGradientType(), min, max);

		Renderer r = new Renderer();
		r.generateBilinear(data, g, tile_size, tile_size, shape); //laptop: 132ms
		r.export(config.getName() + ".png");
		log.info("Wrote to {}.png", config.getName());
		log.info("Done ({})", watch);

		/*
		 * Create scale
		 */
		log.info("Generating scale");
		Scale scale = new Scale(g);
		scale.generate(config.getName() + "-scale.png", Itinerary.getLegend(config.getDataType()), true);
		log.info("Wrote to {}-scale.png", config.getName());

	}

	/**
	 * Calculates itineraries for a list of destinations. Some destinations might be null, in which case the
	 * corresponding result will also be null.
	 */
	public static ArrayList<Itinerary> getItineraries(LatLng src, List<LatLng> dsts, Date startTime) throws JAXBException, ParserConfigurationException, IOException, SAXException {
		Persistence fetcher = new Persistence();
		fetcher.loadCache();
		DAO dao = new DAO(fetcher);

		ArrayList<Itinerary> results = new ArrayList<Itinerary>(dsts.size());
		ExecutorService executor = Executors.newFixedThreadPool(6);
		List<Wrapper> wrappers = new ArrayList<Wrapper>(dsts.size());

		//create jobs for null destinations
		for(LatLng dst : dsts) {
			if(dst != null) {
				wrappers.add(new Wrapper(src, dst, startTime, dao));
			}
		}

		//start running jobs in separate threads
		for(Wrapper w : wrappers) {
			executor.execute(w);
		}

		//accept no more jobs
		executor.shutdown();

		//go through all destinations and get the results of the non-null ones
		int s = wrappers.size() / 10;
		int progress = 0;

		for(int i =0; i< dsts.size(); i++) {
			if(dsts.get(i) == null) {
				results.add(null);
				continue;
			}

			Wrapper w = wrappers.get(progress);
			results.add(w.getResult());
			progress++;

			if(progress % s == 0) {
				int perc = (int) Math.round(100.0 * progress / wrappers.size());
				log.info("{} %", perc);
			}
		}

		//make sure all threads are done
		boolean done = false;
		while(!done) {
			try {
				done = executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
			} catch (InterruptedException e) {}
		}

		return results;
	}
}
