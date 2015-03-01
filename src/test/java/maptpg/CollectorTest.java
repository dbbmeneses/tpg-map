package maptpg;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import lombok.extern.log4j.Log4j2;
import objectexplorer.MemoryMeasurer;
import objectexplorer.ObjectGraphMeasurer;
import objectexplorer.ObjectGraphMeasurer.Footprint;

import org.apache.logging.log4j.core.util.FileUtils;
import org.junit.Test;
import org.xml.sax.SAXException;

import dmeneses.maptpg.datacollection.DeparturesCollector;
import dmeneses.maptpg.datacollection.StopsCollector;
import dmeneses.maptpg.datacollection.ThermometerCollector;
import dmeneses.maptpg.datacollection.model.XMLDeparture;
import dmeneses.maptpg.datacollection.model.XMLPhysicalStop;
import dmeneses.maptpg.datacollection.model.XMLStep;
import dmeneses.maptpg.datacollection.model.XMLStop;
import dmeneses.maptpg.utils.Tools;

@Log4j2
public class CollectorTest {
	@Test
	public void testStops() throws JAXBException, ParserConfigurationException, IOException, SAXException, URISyntaxException {
		List<XMLStop> allStops = StopsCollector.getAllStops(null, null, null, null, null);
		List<XMLStop> physicalStops = StopsCollector.getPhysicalStops(null, null);
		
		int numStops = 0;
		int numStopsLines = 0;
		
		for(XMLStop s : physicalStops) {
			for(XMLPhysicalStop ph : s.getPhysicalStops()) {
				numStops++;
				numStopsLines += ph.getLines().getItems().size();
			}
		}
		
		log.info("{} physical stops, {} pairs stop/line", numStops, numStopsLines);
		log.info("Memory stops: {}", Tools.readableFileSize(MemoryMeasurer.measureBytes(allStops)));
		log.info("Memory physical stops: {}", Tools.readableFileSize(MemoryMeasurer.measureBytes(physicalStops)));
	}
	
	@Test
	public void testThermometer() throws JAXBException, ParserConfigurationException, IOException, SAXException, URISyntaxException {
		List<XMLStep> steps = ThermometerCollector.getThermometer("43274");
		
		for(XMLStep s : steps) {
			log.debug(s);
		}
	}
	
	@Test
	public void testDepartures() throws JAXBException, ParserConfigurationException, IOException, SAXException, URISyntaxException {
		List<XMLDeparture> nextDepartures = DeparturesCollector.getNextDepartures("CERN");
		List<XMLDeparture> allNextDepartures = DeparturesCollector.getAllNextDepartures("CERN", "18", "PALETTES");
	
		log.debug(Tools.readableFileSize(MemoryMeasurer.measureBytes(allNextDepartures)));
		
		for(XMLDeparture d : allNextDepartures) {
			log.debug(d);
		}
	}
}	
