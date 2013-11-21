package dmeneses.maptpg.datacollection;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import dmeneses.maptpg.model.Line;
import dmeneses.maptpg.model.ListWrapper;
import dmeneses.maptpg.model.Step;
import dmeneses.maptpg.model.Stop;


public class ThermometerCollector extends Collector {
	public static void main(String[] args) throws IOException, JAXBException, ParserConfigurationException, SAXException, ParseException, URISyntaxException {
		String departureCode = "34411";
		
		List<Step> steps = getThermometer(departureCode);

		for(Step s : steps) {
			System.out.println(s.getStop() + " \t " + s.getDepartureCode() + " \t " + s.getTimestamp());
		}
		
		System.out.println("=========\n=========\n=========");
		saveThermometer("x","x", steps);
		steps = loadThermometer("x","x");
		
		for(Step s : steps) {
			System.out.println(s.getStop() + " \t " + s.getDepartureCode() + " \t " + s.getTimestamp());
		}

	}

	public static void saveThermometer(String lineCode, String destinationCode, List<Step> steps) throws JAXBException {
		String fileName = cacheRoot + "thermometer" + File.separator +
				"th-" + lineCode + "-" + destinationCode + ".xml";
		
		List<Class<?>> classes = new LinkedList<Class<?>>();
		classes.add(Step.class);
		classes.add(Stop.class);
		classes.add(Line.class);
		classes.add(ListWrapper.class);
		
		marshal(Step.class, classes, steps, "steps", fileName);
	}
	
	public static List<Step> loadThermometer(String lineCode, String destinationCode) throws JAXBException, ParserConfigurationException, IOException, SAXException {
		String fileName = cacheRoot + "thermometer" + File.separator +
				"th-" + lineCode + "-" + destinationCode + ".xml";
		
		List<Class<?>> classes = new LinkedList<Class<?>>();
		classes.add(Step.class);
		classes.add(Stop.class);
		classes.add(Line.class);
		classes.add(ListWrapper.class);

		return  unmarshal(Step.class, classes, fileName, "steps");
	}
	
	public static List<Step> getThermometer(String departureCode) throws JAXBException, ParserConfigurationException, IOException, SAXException, URISyntaxException {
		URI uri = new URI("http", "rtpi.data.tpg.ch", "/v1/GetThermometer.xml",
				"key=89c757c0-409e-11e3-821b-0002a5d5c51b"
				 + "&departureCode=" + departureCode, null);
		List<Class<?>> classes = new LinkedList<Class<?>>();
		classes.add(Step.class);
		classes.add(Stop.class);
		classes.add(Line.class);
		classes.add(ListWrapper.class);

		return  unmarshal(Step.class, classes, uri.toASCIIString(), "steps");
	}

	public static List<Step> getThermometerPhysicalStops(String departureCode) throws JAXBException, ParserConfigurationException, IOException, SAXException, URISyntaxException {
		URI uri = new URI("http", "rtpi.data.tpg.ch", "/v1/GetThermometerPhysicalStops.xml",
				"key=89c757c0-409e-11e3-821b-0002a5d5c51b"
				 + "&departureCode=" + departureCode, null);
		List<Class<?>> classes = new LinkedList<Class<?>>();
		classes.add(Step.class);
		classes.add(Stop.class);
		classes.add(Line.class);
		classes.add(ListWrapper.class);

		return  unmarshal(Step.class, classes, uri.toASCIIString(), "steps");
	}
}
