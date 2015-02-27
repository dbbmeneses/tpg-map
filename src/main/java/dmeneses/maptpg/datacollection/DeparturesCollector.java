package dmeneses.maptpg.datacollection;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import dmeneses.maptpg.model.Departure;
import dmeneses.maptpg.model.Line;
import dmeneses.maptpg.model.ListWrapper;

public class DeparturesCollector extends Collector {
	private static final String dir = CACHE_ROOT + "departure" + File.separator;

	public static void saveAllNextDepartures(String stopCode, String lineCode, String destinationCode, List<Departure> departures) throws JAXBException {
		String fileName = dir + stopCode + "-" + lineCode + "-" + destinationCode + ".xml";

		List<Class<?>> classes = new LinkedList<Class<?>>();
		classes.add(Line.class);
		classes.add(Departure.class);
		classes.add(ArrayList.class);

		marshal(Departure.class, classes, departures, "departures", fileName);
	}

	public static List<Departure> loadAllNextDepartures(String stopCode, String lineCode, String destinationCode) throws JAXBException, ParserConfigurationException, IOException, SAXException {
		String fileName = dir + stopCode + "-" + lineCode + "-" + destinationCode + ".xml";

		List<Class<?>> classes = new LinkedList<Class<?>>();
		classes.add(Line.class);
		classes.add(Departure.class);
		classes.add(ListWrapper.class);

		return  unmarshal(Departure.class, classes, fileName, "departures");
	}

	/**
	 * Returns departures within the next hour. Each departure may have a theoretical precision or a good
	 * precision. waitingTime can be "no more" if there were departures in the same day, to a specific
	 * line/destination, but there aren't anymore.
	 * @param stopCode
	 * @return
	 * @throws JAXBException
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 */
	public static List<Departure> getNextDepartures(String stopCode) throws JAXBException, ParserConfigurationException, IOException, SAXException {
		String url = "http://rtpi.data.tpg.ch/v1/GetNextDepartures.xml?key=89c757c0-409e-11e3-821b-0002a5d5c51b&stopCode=" + stopCode;

		List<Class<?>> classes = new LinkedList<Class<?>>();
		classes.add(Line.class);
		classes.add(Departure.class);
		classes.add(ListWrapper.class);

		return  unmarshal(Departure.class, classes, url, "departures");
	}

	public static List<Departure> getAllNextDepartures(String stopCode, String lineCode, String destinationCode) throws JAXBException, ParserConfigurationException, IOException, SAXException, URISyntaxException {
		URI uri = new URI("http", "rtpi.data.tpg.ch", "/v1/GetAllNextDepartures.xml",
				"key=89c757c0-409e-11e3-821b-0002a5d5c51b"
				 + "&stopCode=" + stopCode
				 + "&lineCode=" + lineCode
				 + "&destinationCode=" + destinationCode, null);

		List<Class<?>> classes = new LinkedList<Class<?>>();
		classes.add(Line.class);
		classes.add(Departure.class);
		classes.add(ListWrapper.class);

		return unmarshal(Departure.class, classes, uri.toASCIIString(), "nextDepartures");
	}
}
