package dmeneses.maptpg.datacollection;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import dmeneses.maptpg.model.Line;
import dmeneses.maptpg.model.PhysicalStop;
import dmeneses.maptpg.model.Stop;

public class StopsCollector extends Collector {
	private final static String dir = CACHE_ROOT + "stop" + File.separator;

	public static void savePhysicalStops(List<Stop> stops) throws JAXBException {
		String fileName = dir + "physicalstops.xml";

		List<Class<?>> classes = new LinkedList<Class<?>>();
		classes.add(Stop.class);
		classes.add(Line.class);
		classes.add(PhysicalStop.class);

		marshal(Stop.class, classes, stops, "stops", fileName);
	}

	public static List<Stop> loadPhysicalStops() throws JAXBException, ParserConfigurationException, IOException, SAXException {
		String fileName = dir + "physicalstops.xml";

		List<Class<?>> classes = new LinkedList<Class<?>>();
		classes.add(Stop.class);
		classes.add(Line.class);
		classes.add(PhysicalStop.class);

		return  unmarshal(Stop.class, classes, fileName, "stops");
	}

	public static void saveAllStops(List<Stop> stops) throws JAXBException {
		String fileName = dir + "stops.xml";

		List<Class<?>> classes = new LinkedList<Class<?>>();
		classes.add(Stop.class);
		classes.add(Line.class);
		classes.add(PhysicalStop.class);

		marshal(Stop.class, classes, stops, "stops", fileName);
	}

	public static List<Stop> loadAllStops() throws JAXBException, ParserConfigurationException, IOException, SAXException {
		String fileName = dir + "stops.xml";

		List<Class<?>> classes = new LinkedList<Class<?>>();
		classes.add(Stop.class);
		classes.add(Line.class);
		classes.add(PhysicalStop.class);

		return  unmarshal(Stop.class, classes, fileName, "stops");
	}

	/**
	 * Gives all stops. If latitude/longitude is given, returns stops within 500m from the point,
	 * sorted by distance.
	 */
	public static List<Stop> getAllStops(String stopCode, String stopName,
			String line, Float latitude, Float longitude) throws JAXBException, ParserConfigurationException, IOException, SAXException, URISyntaxException {

		URI uri = new URI("http", "rtpi.data.tpg.ch", "/v1/GetStops.xml",
				"key=89c757c0-409e-11e3-821b-0002a5d5c51b"
						+ ((stopCode != null) ? "&stopCode=" + stopCode : "")
						+ ((stopName != null) ? "&stopName=" + stopName : "")
						+ ((line != null) ? "&line=" + line : "")
						+ ((latitude != null) ? "&latitude=" + latitude.toString() : "")
						+ ((longitude != null) ? "&longitude=" + longitude.toString() : ""),
				null);

		List<Class<?>> classes = new LinkedList<Class<?>>();
		classes.add(Stop.class);
		classes.add(Line.class);
		classes.add(PhysicalStop.class);

		return unmarshal(Stop.class, classes, uri.toASCIIString(), "stops");
	}

	public static List<Stop> getPhysicalStops(String stopCode, String stopName) throws JAXBException, ParserConfigurationException, IOException, SAXException, URISyntaxException {
		URI uri = new URI("http", "rtpi.data.tpg.ch", "/v1/GetPhysicalStops.xml",
				"key=89c757c0-409e-11e3-821b-0002a5d5c51b"
						+ ((stopCode != null) ? "&stopCode=" + stopCode : "")
						+ ((stopName != null) ? "&stopName=" + stopName : ""),
				null);

		List<Class<?>> classes = new LinkedList<Class<?>>();
		classes.add(Stop.class);
		classes.add(Line.class);
		classes.add(PhysicalStop.class);

		return unmarshal(Stop.class, classes, uri.toASCIIString(), "stops");
	}
}
