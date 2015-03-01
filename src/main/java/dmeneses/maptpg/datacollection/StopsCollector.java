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
import dmeneses.maptpg.datacollection.model.XMLPhysicalStop;
import dmeneses.maptpg.datacollection.model.XMLStop;

public class StopsCollector extends Collector {
	private final static String dir = CACHE_ROOT + "stop" + File.separator;
	private final static Class<?>[] classes = { XMLStop.class, XMLLine.class, XMLPhysicalStop.class };

	public static void savePhysicalStops(List<XMLStop> stops) throws JAXBException {
		String fileName = dir + "physicalstops.xml";
		marshal(XMLStop.class, classes, stops, "stops", fileName);
	}

	public static List<XMLStop> loadPhysicalStops() throws JAXBException, ParserConfigurationException, IOException, SAXException {
		String fileName = dir + "physicalstops.xml";
		return  unmarshal(XMLStop.class, classes, fileName, "stops");
	}

	public static void saveAllStops(List<XMLStop> stops) throws JAXBException {
		String fileName = dir + "stops.xml";
		marshal(XMLStop.class, classes, stops, "stops", fileName);
	}

	public static List<XMLStop> loadAllStops() throws JAXBException, ParserConfigurationException, IOException, SAXException {
		String fileName = dir + "stops.xml";
		return  unmarshal(XMLStop.class, classes, fileName, "stops");
	}
	
	public static List<XMLStop> getPhysicalStops() throws JAXBException, ParserConfigurationException, IOException, SAXException, URISyntaxException {
		return getPhysicalStops(null, null);
	}
	
	public static List<XMLStop> getAllStops() throws JAXBException, ParserConfigurationException, IOException, SAXException, URISyntaxException {
		return getAllStops(null, null, null, null, null);
	}
	/**
	 * Gives all stops. If latitude/longitude is given, returns stops within 500m from the point,
	 * sorted by distance.
	 */
	public static List<XMLStop> getAllStops(String stopCode, String stopName,
			String line, Float latitude, Float longitude) throws JAXBException, ParserConfigurationException, IOException, SAXException, URISyntaxException {

		URI uri = new URI("http", "rtpi.data.tpg.ch", "/v1/GetStops.xml",
				"key=89c757c0-409e-11e3-821b-0002a5d5c51b"
						+ ((stopCode != null) ? "&stopCode=" + stopCode : "")
						+ ((stopName != null) ? "&stopName=" + stopName : "")
						+ ((line != null) ? "&line=" + line : "")
						+ ((latitude != null) ? "&latitude=" + latitude.toString() : "")
						+ ((longitude != null) ? "&longitude=" + longitude.toString() : ""),
				null);

		return unmarshal(XMLStop.class, classes, uri.toASCIIString(), "stops");
	}

	public static List<XMLStop> getPhysicalStops(String stopCode, String stopName) throws JAXBException, ParserConfigurationException, IOException, SAXException, URISyntaxException {
		URI uri = new URI("http", "rtpi.data.tpg.ch", "/v1/GetPhysicalStops.xml",
				"key=89c757c0-409e-11e3-821b-0002a5d5c51b"
						+ ((stopCode != null) ? "&stopCode=" + stopCode : "")
						+ ((stopName != null) ? "&stopName=" + stopName : ""),
				null);

		return unmarshal(XMLStop.class, classes, uri.toASCIIString(), "stops");
	}
}
