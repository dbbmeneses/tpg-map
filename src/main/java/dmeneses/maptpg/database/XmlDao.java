package dmeneses.maptpg.database;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import org.xml.sax.SAXException;

import com.google.common.base.Stopwatch;

import dmeneses.maptpg.datacollection.DeparturesCollector;
import dmeneses.maptpg.datacollection.StopsCollector;
import dmeneses.maptpg.datacollection.ThermometerCollector;
import dmeneses.maptpg.datacollection.model.XMLDeparture;
import dmeneses.maptpg.datacollection.model.XMLLine;
import dmeneses.maptpg.datacollection.model.XMLStep;
import dmeneses.maptpg.datacollection.model.XMLStop;
import dmeneses.maptpg.utils.Tuple;

@Log4j2
@Getter
public class XmlDao {
	private List<XMLStop> stops = null;
	private List<XMLStop> physicalStops = null;
	private Map<Tuple<XMLLine, XMLStop>, List<XMLDeparture>> depMap = null;
	private Map<XMLLine, List<XMLStep>> stepMap = null;

	public void cacheData() throws JAXBException {
		log.debug("caching data");
		Stopwatch watch = Stopwatch.createStarted();
		
		StopsCollector.savePhysicalStops(physicalStops);
		StopsCollector.saveAllStops(stops);
		
		for(Tuple<XMLLine, XMLStop> t : depMap.keySet()) {
			List<XMLDeparture> departures = depMap.get(t);
			DeparturesCollector.saveAllNextDepartures(t.getSecond().getCode(), 
					t.getFirst().getCode(), t.getFirst().getDestinationCode(), departures);
		}
		
		for(XMLLine l : stepMap.keySet()) {
			List<XMLStep> steps = stepMap.get(l);
			ThermometerCollector.saveThermometer(l.getCode(),  l.getDestinationCode(), steps);
		}
		
		log.debug("caching done ({})", watch);
	}
	
	public void loadCache() throws JAXBException, ParserConfigurationException, IOException, SAXException {
		loadCache(null);
	}
	
	public void loadCache(String lineCode) throws JAXBException, ParserConfigurationException, IOException, SAXException {
		log.info("Loading cached data");
		Stopwatch watch = Stopwatch.createStarted();
		
		physicalStops = StopsCollector.loadPhysicalStops();
		stops = StopsCollector.loadAllStops();
		
		/*
		 * Organize per Line: lineMap will contain all stops for all lines (but no topology!).
		 * No WS calls.
		 */
		Map<XMLLine, List<XMLStop>> lineMap = new HashMap<XMLLine, List<XMLStop>>();
		for(XMLStop s : stops) {
			for(XMLLine l: s.getLines()) {
				if(lineCode != null && !l.getCode().equals(lineCode)) {
					continue;
				}
				if(!lineMap.containsKey(l)) {
					lineMap.put(l, new LinkedList<XMLStop>());
				}
				lineMap.get(l).add(s);
			}
		}
		
		log.info("There are {} lines", lineMap.size());
		
		/*
		 * Get all departures for each pair (stop,line).
		 */
		log.info("Loading departures...");
		int errorCount = 0;
		int noDepCount = 0;
		int depCount = 0;
		int i = 0;
		int p = stops.size() / 10;
		depMap = new HashMap<Tuple<XMLLine, XMLStop>, List<XMLDeparture>>();
		
		for(XMLStop s : stops) {
			int progress = Math.round(100.0f * ((float) i / (float) stops.size()));
			if(i % p == 0) {
				log.info(" {} %", progress);
			}
			i++;
			
			List<XMLLine> ls =  s.getLines().getItems();
			for(XMLLine l : ls) {
				if(lineCode != null && !l.getCode().equals(lineCode)) {
					continue;
				}
				
				List<XMLDeparture> depList = new LinkedList<XMLDeparture>();
				try {
					depList = DeparturesCollector.loadAllNextDepartures(
						s.getCode(), l.getCode(), l.getDestinationCode());
				} 
				catch(Exception e) {
					errorCount++;
					log.warn("Couldn't load departure for {} / {} / {}", s.getName(), l.getCode(), l.getDestinationCode());
					log.warn(e.getMessage());
				}
				//some pairs might have empty departure list! (because of error or no data)
				if(depList.isEmpty()) {
					noDepCount++;
					log.trace("(Stop,Line) with no departures: {} / {} / {}",
					  s.getName(), l.getCode(), l.getDestinationCode());
				}
				depCount += depList.size();
				Tuple<XMLLine, XMLStop> pair = new Tuple<XMLLine, XMLStop>(l,s);
				depMap.put(pair, depList); 
			}
		}
		
		log.info("there are {} pairs (line,stop)", depMap.size());
		log.info("from which {} have no departures", noDepCount);
		log.info("there were {} errors", errorCount);
		log.info("there are {} departures", depCount);
		
		/*
		 * Get topology of each line.
		 * Uses 1 WS call for each line. (Thermometer)
		 */
		stepMap = new HashMap<XMLLine, List<XMLStep>>();
		for(XMLLine l : lineMap.keySet()) {
			if(lineCode != null && !l.getCode().equals(lineCode)) {
				continue;
			}
			List<XMLStop> stopList = lineMap.get(l);
			for(XMLStop s: stopList) {
				Tuple<XMLLine, XMLStop> t = new Tuple<XMLLine, XMLStop>(l,s);
				List<XMLDeparture> depList = depMap.get(t); //some pairs might have no departures!

				if(!depList.isEmpty()) {
					List<XMLStep> stepList = ThermometerCollector.loadThermometer(l.getCode(), l.getDestinationCode());
					stepMap.put(l, stepList);
					break;
				}
			}
		}
		log.info("Loading took {}", watch);
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
		Map<XMLLine, List<XMLStop>> lineMap = new HashMap<XMLLine, List<XMLStop>>();
		for(XMLStop s : stops) {
			for(XMLLine l : s.getLines()) {
				if(lineCode != null && !l.getCode().equals(lineCode)) {
					continue;
				}
				//put in map
				if(!lineMap.containsKey(l)) {
					lineMap.put(l, new LinkedList<XMLStop>());
				}
				lineMap.get(l).add(s);
			}
		}
		
		log.info("there are {} lines", lineMap.size());

		/*
		 * Get all departures for each pair (stop,line).
		 * Uses 1 WS call for each pair. (AllDepartures)
		 */
		int i=0;
		depMap = new HashMap<Tuple<XMLLine, XMLStop>, List<XMLDeparture>>();
		for(XMLStop s : stops) {
			List<XMLLine> ls =  s.getLines().getItems();
			for(XMLLine l : ls) {
				if(lineCode != null && !l.getCode().equals(lineCode)) {
					continue;
				}
				List<XMLDeparture> depList = DeparturesCollector.getAllNextDepartures(
						s.getCode(), l.getCode(), l.getDestinationCode());
				//some pairs might have empty departure list!
				if(depList.isEmpty()) {
					log.debug("(Stop,Line) with no departures: {} / {} / {}", s.getName(), l.getCode(), l.getDestinationCode());
				}
				i += depList.size();
				Tuple<XMLLine, XMLStop> pair = new Tuple<XMLLine, XMLStop>(l,s);
				depMap.put(pair, depList); 
			}
		}
		
		log.info("there are {} pairs (line,stop)", depMap.size());
		log.info("there are {} departures", i);
		
		/*
		 * Get topology of each line.
		 * Uses 1 WS call for each line. (Thermometer)
		 */
		stepMap = new HashMap<XMLLine, List<XMLStep>>();
		for(XMLLine l : lineMap.keySet()) {
			if(lineCode != null && !l.getCode().equals(lineCode)) {
				continue;
			}
			List<XMLStop> stopList = lineMap.get(l);
			for(XMLStop s: stopList) {
				Tuple<XMLLine, XMLStop> t = new Tuple<XMLLine, XMLStop>(l, s);
				List<XMLDeparture> depList = depMap.get(t); //some pairs might have no departures!

				if(!depList.isEmpty()) {
					List<XMLStep> stepList = ThermometerCollector.getThermometer(depList.get(0).getCode());
					stepMap.put(l, stepList);
					break;
				}
			}
		}
	}
}
