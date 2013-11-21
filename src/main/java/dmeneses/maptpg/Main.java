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
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.javadocmd.simplelatlng.LatLng;

import dmeneses.maptpg.database.DAO;
import dmeneses.maptpg.database.Persistence;
import dmeneses.maptpg.database.ResultsManager;
import dmeneses.maptpg.datacollection.Collector;
import dmeneses.maptpg.image.Renderer;
import dmeneses.maptpg.image.Scale;
import dmeneses.maptpg.image.gradient.Gradient;
import dmeneses.maptpg.image.gradient.Gradients;
import dmeneses.maptpg.image.gradient.Gradients.GRADIENTS;
import dmeneses.maptpg.map.CircleF;
import dmeneses.maptpg.map.GoogleMapsProjection2;
import dmeneses.maptpg.map.MapTools;
import dmeneses.maptpg.map.PolygonF;
import dmeneses.maptpg.map.ShapeF;
import dmeneses.maptpg.map.UnionShape;
import dmeneses.maptpg.process.Itinerary;
import dmeneses.maptpg.process.Wrapper;
import dmeneses.maptpg.process.Itinerary.DATA_TYPE;
import dmeneses.maptpg.utils.TimeDiff;
import dmeneses.maptpg.utils.Tools;


public class Main {
	private final static Logger log = Logger.getLogger(Main.class.getName());
	/*
	 * Options
	 */
	//THESE CAN BE MODIFIED
	private static int imageSize = 1000;
	private static Itinerary.DATA_TYPE dataType = DATA_TYPE.TIME;
	private static Double maxScale = null;
	private static String sourceLocation = "CERN";
	private static int startHour = 15;
	private static GRADIENTS gradientType = GRADIENTS.LINEAR_HUE;
	private static String name = "output_" + Tools.getRandomHexString(8);
	private static int numPoints = 251;
	private static String loadPath = null; //"/tmp/results"
	
	//THESE CAN'T
	public static String kmlLocation = System.getProperty("user.home") + "/tpg/datastore/geneva.kml";
	public static String cacheRoot = System.getProperty("user.home") + "/tpg/datastore/";
	
	public static void setLoadPath(String loadPath) {
		Main.loadPath = loadPath;
	}

	public static void setImageSize(int imageSize) {
		Main.imageSize = imageSize;
	}
	public static void setDataType(Itinerary.DATA_TYPE dataType) {
		Main.dataType = dataType;
	}
	public static void setMaxScale(Double maxScale) {
		Main.maxScale = maxScale;
	}
	public static void setSourceLocation(String sourceLocation) {
		Main.sourceLocation = sourceLocation;
	}
	public static void setStartHour(int startHour) {
		Main.startHour = startHour;
	}
	public static void setGradientType(GRADIENTS gradientType) {
		Main.gradientType = gradientType;
	}
	public static void setName(String name) {
		Main.name = name;
	}
	public static void setNumPoints(int numPoints) {
		Main.numPoints = numPoints;
	}
	
	public static void generate() throws JAXBException, ParserConfigurationException, IOException, SAXException, URISyntaxException {
		/*
		 * Init stuff and validate data
		 */
		GoogleMapsProjection2 proj = new GoogleMapsProjection2();
		Date start;
		PolygonF poly = MapTools.loadKml(kmlLocation);
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
		
		LatLng src = locations.get(sourceLocation.replace(' ', '_'));
		if(src == null) {
			log.severe("Invalid source location: " + sourceLocation);
			return;
		}
		
		/*
		 * Get Points
		 */
		ShapeF circle = new CircleF(proj.fromLatLngToPoint(new LatLng(46.209418049302556, 6.121101379394531), 0), 0.12);
		ShapeF shape = new UnionShape(circle, poly);
		List<LatLng> dsts = MapTools.getPointList(shape.getSurroundingBox(), numPoints, numPoints);

		log.config("topLeft: "  + proj.fromPointToLatLng(shape.getSurroundingBox()[0], 0));
		log.config("bottomRight: "  + proj.fromPointToLatLng(shape.getSurroundingBox()[1], 0));

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
		if(loadPath == null) {
			start = new Date();
	
			Collector.setCacheRoot(cacheRoot);
			Calendar c = Calendar.getInstance();
			c.set(2013, 10, 8, startHour, 0, 0);
			Date startDate = c.getTime();
	
			itineraries = getItineraries(src, dsts, startDate);
	
			log.info("Done (" + new TimeDiff(start, new Date()) + ")");
	
			//
			// Save results
			//
			start = new Date();
			log.info("Saving results... ");
			ResultsManager.save(name + ".txt", itineraries);
			log.info("Wrote to " + name + ".txt");
			log.info("Done (" + new TimeDiff(start, new Date()) + ")");
		}
		else {
			itineraries = ResultsManager.load(loadPath);
		}
		
		/*
		 * Image generation
		 */
		start = new Date();
		log.info("Processing results... ");
		int side = (int) Math.sqrt(itineraries.size());
		int tile_size = imageSize/(side-1);

		log.fine(Integer.toString(side));
		Double[][] data = new Double[side][side];
		double max = Double.MIN_VALUE;
		double min = Double.MAX_VALUE;
		for(int i = 0; i < side; i++) {
			for(int j = 0; j<side; j++) {
				Itinerary it = itineraries.get(i*side+j);
				if(it != null) {
					double v = it.getData(dataType);
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
		log.config("min: " + min + " max: " + max);
		log.info("Done (" + new TimeDiff(start, new Date()) + ")");

		start = new Date();
		log.info("Creating images... ");

		if(maxScale != null) {
			max = maxScale;
		}
		Gradient g = Gradients.createGradient(gradientType, min, max);

		Renderer r = new Renderer();
		r.generateBilinear(data, g, tile_size, tile_size, shape); //laptop: 132ms
		r.export(name + ".png");
		log.info("Wrote to " + name + ".png");
		log.info("Done (" + new TimeDiff(start, new Date()) + ")");

		/*
		 * Create scale
		 */
		log.info("Generating scale");
		Scale scale = new Scale(g);
		scale.generate(name + "-scale.png", Itinerary.getLegend(dataType), true);
		log.info("Wrote to " + name + "-scale.png");

	}

	/**
	 * Calculates itineraries for a list of destinations. Some destinations might be null, in which case the 
	 * corresponding result will also be null.
	 * @param src
	 * @param dsts
	 * @param startTime
	 * @return
	 * @throws JAXBException
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
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
				int perc = (int) Math.round(100.0 * (float) progress / (float) wrappers.size());
				log.info(perc + "%");
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
