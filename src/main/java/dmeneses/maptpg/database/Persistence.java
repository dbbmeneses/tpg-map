package dmeneses.maptpg.database;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import dmeneses.maptpg.datacollection.DeparturesCollector;
import dmeneses.maptpg.datacollection.StopsCollector;
import dmeneses.maptpg.datacollection.ThermometerCollector;
import dmeneses.maptpg.model.Departure;
import dmeneses.maptpg.model.Line;
import dmeneses.maptpg.model.Step;
import dmeneses.maptpg.model.Stop;
import dmeneses.maptpg.utils.TimeDiff;
import dmeneses.maptpg.utils.Tuple;


public class Persistence {
	private final static Logger log = Logger.getLogger(Persistence.class.getName());
	private List<Stop> stops = null;
	private List<Stop> physicalStops = null;
	private Map<Tuple<Line, Stop>, List<Departure>> depMap = null;
	private Map<Line, List<Step>> stepMap = null;
	
	public List<Stop> getPhysicalStops() {
		return physicalStops;
	}
	public Map<Tuple<Line, Stop>, List<Departure>> getDepMap() {
		return depMap;
	}
	
	public List<Stop> getStops() {
		return stops;
	}
	
	public Map<Line, List<Step>> getStepMap() {
		return stepMap;
	}

	public void cacheData() throws JAXBException {
		StopsCollector.savePhysicalStops(physicalStops);
		StopsCollector.saveAllStops(stops);
		
		for(Tuple<Line, Stop> t : depMap.keySet()) {
			List<Departure> departures = depMap.get(t);
			DeparturesCollector.saveAllNextDepartures(t.getSecond().getCode(), 
					t.getFirst().getCode(), t.getFirst().getDestinationCode(), departures);
		}
		
		for(Line l : stepMap.keySet()) {
			List<Step> steps = stepMap.get(l);
			ThermometerCollector.saveThermometer(l.getCode(),  l.getDestinationCode(), steps);
		}
	}
	
	public void loadCache() throws JAXBException, ParserConfigurationException, IOException, SAXException {
		loadCache(null);
	}
	public void loadCache(String lineCode) throws JAXBException, ParserConfigurationException, IOException, SAXException {
		log.info("Loading cached data");
		Date start = new Date();
		physicalStops = StopsCollector.loadPhysicalStops();
		stops = StopsCollector.loadAllStops();
		
		/*
		 * Organize per Line: lineMap will contain all stops for all lines (but no topology!).
		 * No WS calls.
		 */
		//int pairCount = 0;
		Map<Line, List<Stop>> lineMap = new HashMap<Line, List<Stop>>();
		for(Stop s : stops) {
			for(Line l: s.getLines()) {
				if(lineCode != null && !l.getCode().equals(lineCode)) {
					continue;
				}
				//pairCount++;
				//put in map
				if(!lineMap.containsKey(l)) {
					lineMap.put(l, new LinkedList<Stop>());
				}
				lineMap.get(l).add(s);
			}
		}
		
		log.info("There are " + lineMap.size() + " lines");
		
		/*
		 * Get all departures for each pair (stop,line).
		 */
		log.info("Loading departures...");
		int errorCount = 0;
		int noDepCount = 0;
		int depCount = 0;
		int i = 0;
		int p = stops.size() / 10;
		depMap = new HashMap<Tuple<Line, Stop>, List<Departure>>();
		for(Stop s : stops) {
			int progress = Math.round(100.0f * ((float) i / (float) stops.size()));
			if(i % p == 0) {
				log.info(progress + "%");
			}
			i++;
			
			List<Line> ls =  s.getLines().getItems();
			for(Line l : ls) {
				if(lineCode != null && !l.getCode().equals(lineCode)) {
					continue;
				}
				
				List<Departure> depList = new LinkedList<Departure>();
				try {
					depList = DeparturesCollector.loadAllNextDepartures(
						s.getCode(), l.getCode(), l.getDestinationCode());
				} 
				catch(Exception e) {
					errorCount++;
					log.warning("Couldn't load departure for " + 
							s.getName() + " / " + l.getCode() + " / " + l.getDestinationCode());
					log.warning(e.getMessage());
				}
				//some pairs might have empty departure list! (because of error or no data)
				if(depList.isEmpty()) {
					noDepCount++;
					//System.out.println("(Stop,Line) with no departures:" +
					//		s.getName() + " / " + l.getCode() + " / " + l.getDestinationCode());
				}
				depCount += depList.size();
				Tuple<Line, Stop> pair = new Tuple<Line, Stop>(l,s);
				depMap.put(pair, depList); 
			}
		}
		
		log.info("there are " + depMap.size() + " pairs (line,stop)");
		log.info("from which " + noDepCount + " have no departures");
		log.info("there were " + errorCount + " errors");
		log.info("there are " + depCount + " departures");
		

		/*
		 * Get topology of each line.
		 * Uses 1 WS call for each line. (Thermometer)
		 */
		stepMap = new HashMap<Line, List<Step>>();
		for(Line l : lineMap.keySet()) {
			if(lineCode != null && !l.getCode().equals(lineCode)) {
				continue;
			}
			List<Stop> stopList = lineMap.get(l);
			for(Stop s: stopList) {
				Tuple<Line, Stop> t = new Tuple<Line, Stop>(l,s);
				List<Departure> depList = depMap.get(t); //some pairs might have no departures!

				if(!depList.isEmpty()) {
					List<Step> stepList = ThermometerCollector.loadThermometer(l.getCode(), l.getDestinationCode());
					stepMap.put(l, stepList);
					break;
				}
			}
		}
		log.info("Loading took " + (new TimeDiff(start, new Date())));
	}
	
	public void loadData() throws JAXBException, ParserConfigurationException, IOException, SAXException, URISyntaxException {
		loadData(null);
	}
	public void loadData(String lineCode) throws JAXBException, ParserConfigurationException, IOException, SAXException, URISyntaxException {
		/*
		 * Get all stops and physical stops.
		 * Uses 2 WS call. (AllStops and PhysicalStops).
		 */
		stops  = StopsCollector.getAllStops(null, null, null, null, null);
		physicalStops  = StopsCollector.getPhysicalStops(null, null);

		/*
		 * Organize per Line: lineMap will contain all stops for all lines (but no topology!).
		 * No WS calls.
		 */
		Map<Line, List<Stop>> lineMap = new HashMap<Line, List<Stop>>();
		for(Stop s : stops) {
			for(Line l : s.getLines()) {
				if(lineCode != null && !l.getCode().equals(lineCode)) {
					continue;
				}
				//put in map
				if(!lineMap.containsKey(l)) {
					lineMap.put(l, new LinkedList<Stop>());
				}
				lineMap.get(l).add(s);
			}
		}
		
		log.info("there are " + lineMap.size() + " lines");

		/*
		 * Get all departures for each pair (stop,line).
		 * Uses 1 WS call for each pair. (AllDepartures)
		 */
		int i=0;
		depMap = new HashMap<Tuple<Line, Stop>, List<Departure>>();
		for(Stop s : stops) {
			List<Line> ls =  s.getLines().getItems();
			for(Line l : ls) {
				if(lineCode != null && !l.getCode().equals(lineCode)) {
					continue;
				}
				List<Departure> depList = DeparturesCollector.getAllNextDepartures(
						s.getCode(), l.getCode(), l.getDestinationCode());
				//some pairs might have empty departure list!
				if(depList.isEmpty()) {
					log.fine("(Stop,Line) with no departures:" 
							+ s.getName() + " / " + l.getCode() + " / " + l.getDestinationCode());
				}
				i += depList.size();
				Tuple<Line, Stop> pair = new Tuple<Line, Stop>(l,s);
				depMap.put(pair, depList); 
			}
		}
		
		log.info("there are " + depMap.size() + " pairs (line,stop)");
		log.info("there are " + i + " departures");
		
		/*
		 * Get topology of each line.
		 * Uses 1 WS call for each line. (Thermometer)
		 */
		stepMap = new HashMap<Line, List<Step>>();
		for(Line l : lineMap.keySet()) {
			if(lineCode != null && !l.getCode().equals(lineCode)) {
				continue;
			}
			List<Stop> stopList = lineMap.get(l);
			for(Stop s: stopList) {
				Tuple<Line, Stop> t = new Tuple<Line, Stop>(l, s);
				List<Departure> depList = depMap.get(t); //some pairs might have no departures!

				if(!depList.isEmpty()) {
					List<Step> stepList = ThermometerCollector.getThermometer(depList.get(0).getCode());
					stepMap.put(l, stepList);
					break;
				}
			}
		}

	}
}
