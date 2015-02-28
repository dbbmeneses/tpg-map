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
import dmeneses.maptpg.model.Departure;
import dmeneses.maptpg.model.Step;
import dmeneses.maptpg.model.Stop;
import dmeneses.maptpg.utils.Tools;

@Log4j2
public class CollectorTest {
	@Test
	public void testStops() throws JAXBException, ParserConfigurationException, IOException, SAXException, URISyntaxException {
		List<Stop> allStops = StopsCollector.getAllStops(null, null, null, null, null);
		List<Stop> physicalStops = StopsCollector.getPhysicalStops(null, null);
		
		log.info("Memory stops: {}", Tools.readableFileSize(MemoryMeasurer.measureBytes(allStops)));
		log.info("Memory physical stops: {}", Tools.readableFileSize(MemoryMeasurer.measureBytes(physicalStops)));
	}
	
	@Test
	public void testo() throws JAXBException, ParserConfigurationException, IOException, SAXException, URISyntaxException {
		List<Step> steps = ThermometerCollector.getThermometer("43273");
		
		for(Step s : steps) {
			log.debug(s);
		}
	}
	
	@Test
	public void testDepartures() throws JAXBException, ParserConfigurationException, IOException, SAXException, URISyntaxException {
		List<Departure> nextDepartures = DeparturesCollector.getNextDepartures("CERN");
		List<Departure> allNextDepartures = DeparturesCollector.getAllNextDepartures("CERN", "18", "PALETTES");
	
		log.debug(Tools.readableFileSize(MemoryMeasurer.measureBytes(allNextDepartures)));
		
		for(Departure d : allNextDepartures) {
			log.debug(d);
		}
	}
}	
