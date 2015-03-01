package dmeneses.maptpg.datacollection;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import dmeneses.maptpg.datacollection.model.XMLLine;
import dmeneses.maptpg.datacollection.model.ListWrapper;
import dmeneses.maptpg.datacollection.model.XMLStep;
import dmeneses.maptpg.datacollection.model.XMLStop;


public class ThermometerCollector extends Collector {
	private final static String dir = CACHE_ROOT + "thermometer" + File.separator;
	private final static Class<?>[] classes = { XMLStep.class, XMLStop.class, XMLLine.class, ListWrapper.class };

	public static void saveThermometer(String lineCode, String destinationCode, List<XMLStep> steps) throws JAXBException {
		String fileName = dir + "th-" + lineCode + "-" + destinationCode + ".xml";
		marshal(XMLStep.class, classes, steps, "steps", fileName);
	}

	public static List<XMLStep> loadThermometer(String lineCode, String destinationCode) throws JAXBException, ParserConfigurationException, IOException, SAXException {
		String fileName = dir + "th-" + lineCode + "-" + destinationCode + ".xml";
		return  unmarshal(XMLStep.class, classes, fileName, "steps");
	}

	public static List<XMLStep> getThermometer(String departureCode) throws JAXBException, ParserConfigurationException, IOException, SAXException, URISyntaxException {
		URI uri = new URI("http", "rtpi.data.tpg.ch", "/v1/GetThermometer.xml",
				"key=89c757c0-409e-11e3-821b-0002a5d5c51b"
				 + "&departureCode=" + departureCode, null);
		
		return  unmarshal(XMLStep.class, classes, uri.toASCIIString(), "steps");
	}

	public static List<XMLStep> getThermometerPhysicalStops(String departureCode) throws JAXBException, ParserConfigurationException, IOException, SAXException, URISyntaxException {
		URI uri = new URI("http", "rtpi.data.tpg.ch", "/v1/GetThermometerPhysicalStops.xml",
				"key=89c757c0-409e-11e3-821b-0002a5d5c51b"
				 + "&departureCode=" + departureCode, null);
		
		return  unmarshal(XMLStep.class, classes, uri.toASCIIString(), "steps");
	}
}
