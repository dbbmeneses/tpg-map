package dmeneses.maptpg.datacollection;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import dmeneses.maptpg.datacollection.model.XMLDeparture;
import dmeneses.maptpg.datacollection.model.XMLLine;
import dmeneses.maptpg.datacollection.model.ListWrapper;

public class DeparturesCollector extends Collector {
	private static final String dir = CACHE_ROOT + "departure" + File.separator;
	private static final Class<?>[] classes = { XMLLine.class, XMLDeparture.class, ListWrapper.class };

	public static void saveAllNextDepartures(String stopCode, String lineCode, String destinationCode, List<XMLDeparture> departures) throws JAXBException {
		String fileName = dir + stopCode + "-" + lineCode + "-" + destinationCode + ".xml";
		marshal(XMLDeparture.class, classes, departures, "departures", fileName);
	}

	public static List<XMLDeparture> loadAllNextDepartures(String stopCode, String lineCode, String destinationCode) throws JAXBException, ParserConfigurationException, IOException, SAXException {
		String fileName = dir + stopCode + "-" + lineCode + "-" + destinationCode + ".xml";
		return  unmarshal(XMLDeparture.class, classes, fileName, "departures");
	}

	/**
	 * Returns departures within the next hour. Each departure may have a theoretical precision or a good
	 * precision. waitingTime can be "no more" if there were departures in the same day, to a specific
	 * line/destination, but there aren't anymore.
	 */
	public static List<XMLDeparture> getNextDepartures(String stopCode) throws JAXBException, ParserConfigurationException, IOException, SAXException {
		String url = "http://rtpi.data.tpg.ch/v1/GetNextDepartures.xml?key=89c757c0-409e-11e3-821b-0002a5d5c51b&stopCode=" + stopCode;
		return  unmarshal(XMLDeparture.class, classes, url, "departures");
	}

	public static List<XMLDeparture> getAllNextDepartures(String stopCode, String lineCode, String destinationCode) throws JAXBException, ParserConfigurationException, IOException, SAXException, URISyntaxException {
		URI uri = new URI("http", "rtpi.data.tpg.ch", "/v1/GetAllNextDepartures.xml",
				"key=89c757c0-409e-11e3-821b-0002a5d5c51b"
				 + "&stopCode=" + stopCode
				 + "&lineCode=" + lineCode
				 + "&destinationCode=" + destinationCode, null);

		return unmarshal(XMLDeparture.class, classes, uri.toASCIIString(), "departures");
	}
}
