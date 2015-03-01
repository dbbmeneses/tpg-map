package dmeneses.maptpg.database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import lombok.extern.log4j.Log4j2;

import org.xml.sax.SAXException;

import com.google.common.base.Stopwatch;
import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import dmeneses.maptpg.database.types.IDeparture;
import dmeneses.maptpg.datacollection.model.XMLDeparture;
import dmeneses.maptpg.datacollection.model.XMLLine;
import dmeneses.maptpg.datacollection.model.XMLPhysicalStop;
import dmeneses.maptpg.datacollection.model.XMLStep;
import dmeneses.maptpg.datacollection.model.XMLStop;
import dmeneses.maptpg.utils.TimeDiff;
import dmeneses.maptpg.utils.Tools;
import dmeneses.maptpg.utils.Tuple;

@Log4j2
public class DAO {
	private List<XMLLine> lines;
	private List<XMLStop> stops;
	private List<IDeparture> departures;
	private Map<Tuple<XMLLine,XMLStop>, List<IDeparture>> departureMap;

	public DAO(DAO copy) {
		this.lines = copy.lines;
		this.stops = copy.stops;

		//deep copy of IDeparture
		departures = new ArrayList<IDeparture>(copy.departures.size());
		departureMap = new HashMap<Tuple<XMLLine,XMLStop>, List<IDeparture>>(copy.departureMap.size());

		Iterator<Entry<Tuple<XMLLine,XMLStop>, List<IDeparture>>> it = copy.departureMap.entrySet().iterator();
		while(it.hasNext()) {
			Entry<Tuple<XMLLine,XMLStop>, List<IDeparture>> e = it.next();
			List<IDeparture> idList = new ArrayList<IDeparture>(e.getValue().size());
			Iterator<IDeparture> listIt = e.getValue().iterator();
			while(listIt.hasNext()) {
				IDeparture newDep = new IDeparture(listIt.next());
				idList.add(newDep);
				departures.add(newDep);
			}

			departureMap.put(e.getKey(), idList);
		}
	}


	public DAO(XmlDao p) throws JAXBException, ParserConfigurationException, IOException, SAXException {
		log.info("Starting processing data");
		Stopwatch watch = Stopwatch.createStarted();

		/*
		 * merge stops info
		 */
		///TODO 3 stops missing from AllStops!!!
		List<XMLStop> physicalStops = p.getPhysicalStops();
		this.stops = p.getStops();

		Iterator<XMLStop> itStop = this.stops.iterator();
		while(itStop.hasNext()) {
			XMLStop s = itStop.next();
			boolean found = false;
			for(XMLStop ph : physicalStops) {
				if(s.equals(ph)) {
					found = true;

					s.setPhysicalStops(ph.getPhysicalStops());
					break;
				}
			}
			if(!found) {
				log.debug("Couldn't find physical stops for {} - Removing it", s);
				itStop.remove();
			}
		}

		/*
		 * Put stops in Line
		 */
		Map<XMLLine, List<XMLStep>> stepMap = p.getStepMap();
		lines = new ArrayList<XMLLine>(stepMap.size());

		for(XMLLine l : stepMap.keySet()) {
			List<XMLStep> steps = stepMap.get(l);
			List<XMLStop> stopList = new ArrayList<XMLStop>(steps.size());
			for(XMLStep s : steps) {
				boolean found = false;
				for(XMLStop stop : this.stops) {
					if(stop.equals(s.getStop())) {
						found = true;
						stopList.add(stop);
					}
				}

				if(!found) {
					log.debug("couldn't find stop {} in line {} - Step will not be in line", s.getStop(), l);
				}
			}
			if(stopList.isEmpty()) {
				log.debug("Line {} has no valid stops - won't be in list", l);
				continue;
			}
			l.setStops(stopList);
			lines.add(l);
		}

		/*
		 * Create IDeparture List and IDeparture Map
		 */
		Map<Tuple<XMLLine, XMLStop>, List<XMLDeparture>> orig_depMap = p.getDepMap();
		departureMap = new HashMap<Tuple<XMLLine,XMLStop>, List<IDeparture>>();
		departures = new ArrayList<IDeparture>();

		for(Tuple<XMLLine,XMLStop> t : orig_depMap.keySet()) {
			//find stop in our list
			boolean found = false;
			for(XMLStop stop : this.stops) {
				if(stop.equals(t.getSecond())) {
					found = true;
					t.setSecond(stop);
				}
			}
			if(!found) {
				log.debug("departure from {} with invalid stop - Skipping it", t);
				continue;
			}

			//find line in our list
			found = false;
			for(XMLLine line : this.lines) {
				if(line.equals(t.getFirst())) {
					found = true;
					t.setFirst(line);
				}
			}
			if(!found) {
				log.debug("departure from {} with invalid line - Skipping it", t);
				continue;
			}

			//validation done
			//Create IDeparture, by putting location and time into Departure
			List<XMLDeparture> dl = orig_depMap.get(t);
			List<IDeparture> idl = new ArrayList<IDeparture>(dl.size());
			for(XMLDeparture d : dl) {
				LatLng loc = getLocation(t.getSecond(), t.getFirst());
				IDeparture id = new IDeparture(t.getSecond(), t.getFirst(), d.getTimestamp(), loc);
				idl.add(id);
				departures.add(id);
			}

			Collections.sort(idl);
			departureMap.put(t, idl);
		}

		/*
		 * Estimate last stop arrivals
		 */
		for(XMLLine l : lines) {
			//get last and before-last stops
			XMLStop s = l.getStops().get(l.getStops().size()-1);
			XMLStop stopBefore = l.getStops().get(l.getStops().size()-2);

			//get time difference
			List<XMLStep> steps = stepMap.get(l);
			TimeDiff td = new TimeDiff(steps.get(steps.size()-2).getTimestamp(),
					steps.get(steps.size()-1).getTimestamp());
			LatLng loc = getLocation(s, l);

			//calculate all arrivals
			List<IDeparture> beforeList = departureMap.get(new Tuple<XMLLine, XMLStop>(l, stopBefore));
			List<IDeparture> list = new ArrayList<IDeparture>(beforeList.size());
			for(IDeparture depBefore : beforeList) {
				IDeparture id = new IDeparture(s, l, 
						Tools.addDateTimeDiff(depBefore.getTime(), td), loc);
				list.add(id);
			}
			//insert them
			departureMap.put(new Tuple<XMLLine, XMLStop>(l, s), list);
		}

		log.info("Processing took {}", watch);
	}

	protected void setAllLines(List<XMLLine> lines) {
		this.lines = lines;
	}

	protected void setAllStops(List<XMLStop> stops) {
		this.stops = stops;
	}

	protected void setAllDepartures(List<IDeparture> departures) {
		this.departures = departures;
	}

	public List<XMLLine> getAllLines() {
		return Collections.unmodifiableList(lines);
	}

	public List<XMLStop> getAllStops() {
		return this.stops;
	}

	public List<IDeparture> getAllDepartures() {
		return this.departures;
	}

	/**
	 * Gets the next departure, for a particular stop and connection, after a specified time
	 * @param stop
	 * @param line
	 * @param time
	 * @return departure if it exists following the given criteria, otherwise null
	 */
	public IDeparture getNextDeparture(XMLStop stop, XMLLine line, Date time) {
		Tuple<XMLLine,XMLStop> t = new Tuple<XMLLine,XMLStop>(line, stop);
		List<IDeparture> departures = departureMap.get(t);

		if(departures == null) {
			return null;
		}

		for(IDeparture d : departures) {
			if(d.getTime().compareTo(time) > 0) {
				return d;
			}
		}

		return null;
	}

	/**
	 * Get All departures from a stop and line
	 * @param stop
	 * @param line
	 * @return
	 */
	public List<IDeparture> getAllDepartures(XMLStop stop, XMLLine line) {
		Tuple<XMLLine,XMLStop> t = new Tuple<XMLLine,XMLStop>(line, stop);
		List<IDeparture> departures = departureMap.get(t);

		return departures;
	}

	/**
	 * Get all stops in a line that are after a given stop.
	 * @param stop
	 * @param line
	 * @return
	 */
	public List<XMLStop> getNextStops(XMLStop stop, XMLLine line) {
		//TODO: check args
		List<XMLStop> stopsAfter = new LinkedList<XMLStop>();
		boolean found = false;

		for(XMLStop s : line.getStops()) {
			if(found) {
				stopsAfter.add(s);
			}

			if(s.equals(stop)) {
				found = true;
			}
		}

		return stopsAfter;
	}

	public XMLStop getNextStop(XMLStop stop, XMLLine line) {
		//TODO: check args
		XMLStop nStop = null;
		boolean found = false;

		for(XMLStop s : line.getStops()) {
			if(found) {
				nStop = s;
				break;
			}

			if(s.equals(stop)) {
				found = true;
			}
		}

		return nStop;
	}

	public XMLPhysicalStop getPhysicalStop(XMLStop stop, XMLLine line) {
		if(stop.getPhysicalStops() == null) {
			return null;
		}
		for(XMLPhysicalStop ph : stop.getPhysicalStops()) {
			if(ph.getLines().getItems().contains(line)) {
				return ph;
			}
		}

		return null;
	}

	public LatLng getLocation(XMLStop s, XMLLine l) {
		if(s.getPhysicalStops() == null) {
			return null;
		}

		for(XMLPhysicalStop ph : s.getPhysicalStops()) {
			if(ph.getLines().getItems().contains(l)) {
				return ph.getLocation();
			}
		}

		for(XMLPhysicalStop ph : s.getPhysicalStops()) {
			return ph.getLocation();
		}

		return null;
	}

	public XMLStop getNearestStop(XMLLine line, LatLng point) {
		return getNearestStop(line, point, line.getStops());
	}

	public XMLStop getNearestStop(XMLLine line, LatLng point, List<XMLStop> stops) {
		XMLStop nearest = null;
		double minDistance = Double.MAX_VALUE;

		for(XMLStop s : stops) {
			LatLng loc = getLocation(s, line);
			if(loc == null) {
				//TODO log warn?
				continue;
			}

			double dist = LatLngTool.distance(point, loc, LengthUnit.METER);
			if(dist < minDistance) {
				minDistance = dist;
				nearest = s;
			}
		}

		return nearest;
	}
}
