package dmeneses.maptpg;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import dmeneses.maptpg.database.DAO;
import dmeneses.maptpg.database.Persistence;
import dmeneses.maptpg.database.types.IDeparture;
import dmeneses.maptpg.process.Dijkstra;
import dmeneses.maptpg.process.Itinerary;
import dmeneses.maptpg.process.Itinerary.DATA_TYPE;


public class Test {

	/**
	 * @param args
	 * @throws SAXException 
	 * @throws IOException 
	 * @throws ParserConfigurationException 
	 * @throws JAXBException 
	 */
	public static void main(String[] args) throws JAXBException, ParserConfigurationException, IOException, SAXException {
		//final Logger log = Logger.getLogger(Test.class.getName());
		Persistence fetcher = new Persistence();
		fetcher.loadCache();
		DAO dao = new DAO(fetcher);
		Itinerary result;
		
		//test parameters
		LatLng src = new LatLng(46.20974027671904,6.14185631275177);
		LatLng dst1 = new LatLng(46.23978524570758,6.033785821055062);
		Calendar c = Calendar.getInstance();
		c.set(2013, 10, 8, 17, 0, 0);
		Date startDate = c.getTime();
		
		//test run
		Dijkstra dj = new Dijkstra(src, dst1, startDate, dao);
		dj.run();
		result = dj.getResult();
		
		//display results
		double destDist = LatLngTool.distance(result.getPath().getFirst().getLocation(),
				result.getDst(), LengthUnit.METER);
		double srcDist = LatLngTool.distance(result.getPath().getLast().getLocation(),
				result.getSrc(), LengthUnit.METER);
		double totalDist = LatLngTool.distance(result.getSrc(),
				result.getDst(), LengthUnit.METER);
		
		System.out.println("Source walking distance: " + destDist);
		System.out.println("Destination walking distance: " + srcDist);
		System.out.println("Total distance: " + totalDist);
		System.out.println("Total time: " + Double.toString(result.getData(DATA_TYPE.TIME)));
		System.out.println("Av speed: " + Double.toString(result.getData(DATA_TYPE.SPEED)));
		System.out.println("Normalized time: " + Double.toString(result.getData(DATA_TYPE.NORMALIZED_TIME)));
		System.out.println("Walk time: " + Double.toString(result.getData(DATA_TYPE.WALK_TIME)));
		System.out.println("Total steps: " + result.getData(DATA_TYPE.STEPS));
		
		for(IDeparture id : result.getPath()) {
			System.out.println(id.toString());
		}

	}

}
