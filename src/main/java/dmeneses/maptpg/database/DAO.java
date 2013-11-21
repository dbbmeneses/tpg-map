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
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import dmeneses.maptpg.database.types.IDeparture;
import dmeneses.maptpg.model.Departure;
import dmeneses.maptpg.model.Line;
import dmeneses.maptpg.model.PhysicalStop;
import dmeneses.maptpg.model.Step;
import dmeneses.maptpg.model.Stop;
import dmeneses.maptpg.utils.TimeDiff;
import dmeneses.maptpg.utils.Tools;
import dmeneses.maptpg.utils.Tuple;


public class DAO {
	private final static Logger log = Logger.getLogger(DAO.class.getName());
	private List<Line> lines;
	private List<Stop> stops;
	private List<IDeparture> departures;
	private Map<Tuple<Line,Stop>, List<IDeparture>> departureMap;


	public DAO(DAO copy) {
		this.lines = copy.lines;
		this.stops = copy.stops;

		//deep copy of IDeparture
		departures = new ArrayList<IDeparture>(copy.departures.size());
		departureMap = new HashMap<Tuple<Line,Stop>, List<IDeparture>>(copy.departureMap.size());

		Iterator<Entry<Tuple<Line,Stop>, List<IDeparture>>> it = copy.departureMap.entrySet().iterator();
		while(it.hasNext()) {
			Entry<Tuple<Line,Stop>, List<IDeparture>> e = it.next();
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


	public DAO(Persistence p) throws JAXBException, ParserConfigurationException, IOException, SAXException {
		log.info("Starting processing data");
		Date start = new Date();

		/*
		 * merge stops info
		 */
		///TODO 3 stops missing from AllStops!!!
		List<Stop> physicalStops = p.getPhysicalStops();
		this.stops = p.getStops();

		Iterator<Stop> itStop = this.stops.iterator();
		while(itStop.hasNext()) {
			Stop s = itStop.next();
			boolean found = false;
			for(Stop ph : physicalStops) {
				if(s.equals(ph)) {
					found = true;

					s.setPhysicalStops(ph.getPhysicalStops());
					break;
				}
			}
			if(!found) {
				log.fine("Couldn't find physical stops for " + s + " - Removing it");
				itStop.remove();
			}
		}

		/*
		 * Put stops in Line
		 */
		Map<Line, List<Step>> stepMap = p.getStepMap();
		lines = new ArrayList<Line>(stepMap.size());

		for(Line l : stepMap.keySet()) {
			List<Step> steps = stepMap.get(l);
			List<Stop> stopList = new ArrayList<Stop>(steps.size());
			for(Step s : steps) {
				boolean found = false;
				for(Stop stop : this.stops) {
					if(stop.equals(s.getStop())) {
						found = true;
						stopList.add(stop);
					}
				}

				if(!found) {
					log.fine("couldn't find stop " + s.getStop() + " in line " 
							+ l + " - Step will not be in line");
				}
			}
			if(stopList.isEmpty()) {
				log.fine("Line " + l + " has no valid stops - won't be in list");
				continue;
			}
			l.setStops(stopList);
			lines.add(l);
		}

		/*
		 * Create IDeparture List and IDeparture Map
		 */
		Map<Tuple<Line, Stop>, List<Departure>> orig_depMap = p.getDepMap();
		departureMap = new HashMap<Tuple<Line,Stop>, List<IDeparture>>();
		departures = new ArrayList<IDeparture>();

		for(Tuple<Line,Stop> t : orig_depMap.keySet()) {
			//find stop in our list
			boolean found = false;
			for(Stop stop : this.stops) {
				if(stop.equals(t.getSecond())) {
					found = true;
					t.setSecond(stop);
				}
			}
			if(!found) {
				log.fine("departure from " + t + " with invalid stop - Skipping it");
				continue;
			}

			//find line in our list
			found = false;
			for(Line line : this.lines) {
				if(line.equals(t.getFirst())) {
					found = true;
					t.setFirst(line);
				}
			}
			if(!found) {
				log.fine("departure from " + t + " with invalid line - Skipping it");
				continue;
			}

			//validation done
			//Create IDeparture, by putting location and time into Departure
			List<Departure> dl = orig_depMap.get(t);
			List<IDeparture> idl = new ArrayList<IDeparture>(dl.size());
			for(Departure d : dl) {
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
		for(Line l : lines) {
			//get last and before-last stops
			Stop s = l.getStops().get(l.getStops().size()-1);
			Stop stopBefore = l.getStops().get(l.getStops().size()-2);

			//get time difference
			List<Step> steps = stepMap.get(l);
			TimeDiff td = new TimeDiff(steps.get(steps.size()-2).getTimestamp(),
					steps.get(steps.size()-1).getTimestamp());
			LatLng loc = getLocation(s, l);

			//calculate all arrivals
			List<IDeparture> beforeList = departureMap.get(new Tuple<Line, Stop>(l, stopBefore));
			List<IDeparture> list = new ArrayList<IDeparture>(beforeList.size());
			for(IDeparture depBefore : beforeList) {
				IDeparture id = new IDeparture(s, l, 
						Tools.addDateTimeDiff(depBefore.getTime(), td), loc);
				list.add(id);
			}
			//insert them
			departureMap.put(new Tuple<Line, Stop>(l, s), list);
		}

		log.info("Processing took " + (new TimeDiff(start, new Date())));

	}

	protected void setAllLines(List<Line> lines) {
		this.lines = lines;
	}

	protected void setAllStops(List<Stop> stops) {
		this.stops = stops;
	}

	protected void setAllDepartures(List<IDeparture> departures) {
		this.departures = departures;
	}

	public List<Line> getAllLines() {
		return Collections.unmodifiableList(lines);
	}

	public List<Stop> getAllStops() {
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
	public IDeparture getNextDeparture(Stop stop, Line line, Date time) {
		Tuple<Line,Stop> t = new Tuple<Line,Stop>(line, stop);
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
	public List<IDeparture> getAllDepartures(Stop stop, Line line) {
		Tuple<Line,Stop> t = new Tuple<Line,Stop>(line, stop);
		List<IDeparture> departures = departureMap.get(t);

		return departures;
	}

	/**
	 * Get all stops in a line that are after a given stop.
	 * @param stop
	 * @param line
	 * @return
	 */
	public List<Stop> getNextStops(Stop stop, Line line) {
		//TODO: check args
		List<Stop> stopsAfter = new LinkedList<Stop>();
		boolean found = false;

		for(Stop s : line.getStops()) {
			if(found) {
				stopsAfter.add(s);
			}

			if(s.equals(stop)) {
				found = true;
			}
		}

		return stopsAfter;
	}

	public Stop getNextStop(Stop stop, Line line) {
		//TODO: check args
		Stop nStop = null;
		boolean found = false;

		for(Stop s : line.getStops()) {
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


	public PhysicalStop getPhysicalStop(Stop stop, Line line) {
		if(stop.getPhysicalStops() == null) {
			return null;
		}
		for(PhysicalStop ph : stop.getPhysicalStops()) {
			if(ph.getLines().getItems().contains(line)) {
				return ph;
			}
		}

		return null;
	}

	public LatLng getLocation(Stop s, Line l) {
		if(s.getPhysicalStops() == null) {
			return null;
		}

		for(PhysicalStop ph : s.getPhysicalStops()) {
			if(ph.getLines().getItems().contains(l)) {
				return ph.getLocation();
			}
		}

		for(PhysicalStop ph : s.getPhysicalStops()) {
			return ph.getLocation();
		}

		return null;
	}

	public Stop getNearestStop(Line line, LatLng point) {
		return getNearestStop(line, point, line.getStops());
	}

	public Stop getNearestStop(Line line, LatLng point, List<Stop> stops) {
		Stop nearest = null;
		double minDistance = Double.MAX_VALUE;

		for(Stop s : stops) {
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
