package dmeneses.maptpg.process;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import dmeneses.maptpg.database.DAO;
import dmeneses.maptpg.database.XmlDao;
import dmeneses.maptpg.database.types.IDeparture;
import dmeneses.maptpg.datacollection.model.XMLLine;
import dmeneses.maptpg.datacollection.model.XMLStop;
import dmeneses.maptpg.utils.Tools;
import dmeneses.maptpg.utils.Tuple;

/**
 * Implementation of an adapted Dijkstra algorithm to find the shortest path from source to destination.
 * @author Duarte Meneses <duarte.meneses@cern.ch>
 *
 */
@Log4j2
public class Dijkstra implements Runnable {
	LatLng src;
	LatLng dst;
	XMLStop start;
	Date startTime;
	DAO dao;
	XmlDao persistence;
	@Getter
	Itinerary result;

	public Dijkstra(LatLng src, LatLng dst, Date startTime, DAO dao) {
		this.src = src;
		this.dst = dst;
		this.startTime = startTime;
		this.dao = dao;
	}

	public void run() {
		SortedSet<IDeparture> candidates;
		HashMap<Tuple<XMLLine,XMLStop>,IDeparture> candidatesHash = new HashMap<Tuple<XMLLine,XMLStop>,IDeparture>(10000);
		HashSet<IDeparture> visited = new HashSet<IDeparture>(10000);
		HashMap<Tuple<XMLLine,XMLStop>,IDeparture> visitedHash = new HashMap<Tuple<XMLLine,XMLStop>,IDeparture>(10000);
		HashMap<IDeparture, IDeparture> history = new HashMap<IDeparture, IDeparture>(3000);
		HashMap<IDeparture, Integer> steps = new HashMap<IDeparture, Integer>(3000);

		TimeSpace destination = getWalkArrival(null, src, startTime);
		
		candidates = getStartNodes(startTime, destination.getTime());
		for(IDeparture d : candidates) {
			log.trace("start node: {}");
			
			candidatesHash.put(d.getPosition(), d);
			history.put(d, null);
			steps.put(d, 1);
		}

		while(true) {
			/*
			 * Pick best candidate and put it in the visited list, removing it from candidates
			 * Map is naturally sorted
			 */
			if(candidates.isEmpty()) {
				break;
			}

			IDeparture best = candidates.first();
			
			/*
			 * check if best next stop is worse than destination
			 */
			if(best.getTime().compareTo(destination.getTime()) > 0) {
				break;
			}
			
			candidates.remove(best);
			candidatesHash.remove(best.getPosition());

			visited.add(best);
			visitedHash.put(best.getPosition(), best);

			/*
			 * Search for new candidates
			 */
			SortedSet<IDeparture> neighbours = getNeighbours(best);

			/*
			 * Check if we have a faster walk time
			 */
			TimeSpace newArrival = getWalkArrival(best, best.getLocation(), best.getTime());
			if(newArrival.getTime().compareTo(destination.getTime()) < 0) {
				destination = newArrival;
			}

			/*
			 * if they are visited or better candidates, exclude them 
			 */
			Iterator<IDeparture> it = neighbours.iterator();
			while(it.hasNext()) {
				IDeparture d = it.next();
				
				IDeparture visit = visitedHash.get(d.getPosition());
				if(visit != null) {
					it.remove();
					continue;
				}

				IDeparture candidate = candidatesHash.get(d.getPosition());
				if(candidate != null) {
					if(candidate.getTime().after(d.getTime())) { //there is a newer one or same age
						candidatesHash.remove(d.getPosition()); //this one is better, remove old
						candidates.remove(candidate);
					}
					else if(candidate.getTime().equals(d.getTime())){ //same time, compare steps
						int s = d.getLine().equals(best.getLine()) ? steps.get(best) : steps.get(best) + 1;
						if(s < steps.get(candidate)) {
							candidatesHash.remove(d.getPosition()); //this one is better, remove old
							candidates.remove(candidate);
						}
						else {
							it.remove();
						}
					}
					else {
						it.remove();
					}
				}
			}

			/*
			 * merge 
			 */
			Integer s = steps.get(best);
			candidates.addAll(neighbours);
			for(IDeparture d : neighbours) {
				history.put(d, best);
				if(d.getLine().equals(best.getLine())) {
					steps.put(d, s);
				}
				else {
					steps.put(d,  s+1);
				}
				candidatesHash.put(d.getPosition(), d);
			}
		}

		
		LinkedList<IDeparture> path = new LinkedList<IDeparture>();
		int total_steps = 0;
		
		if(destination.getPrevious() != null) {
			total_steps = steps.get(destination.getPrevious());
			IDeparture c = destination.getPrevious();
			do {
				path.add(c);
				c = history.get(c);
			} while(c != null);
		}

		
		this.result = new Itinerary(src, dst, startTime, destination.time, path, total_steps);
	}
	
	private SortedSet<IDeparture> getStartNodes(Date startDate, Date walkArrival) {
		List<XMLLine> lines = dao.getAllLines();
		SortedSet<IDeparture> set = new TreeSet<IDeparture>();

		//distance to destination
		double totalDist = LatLngTool.distance(src, dst, LengthUnit.METER);

		//add closest of each line
		for(XMLLine l : lines) {
			XMLStop s = dao.getNearestStop(l, src);
			if(s == null) { //some lines have no stops, because they have no physical info
				continue;
			}

			LatLng loc = dao.getLocation(s, l);
			if(loc == null) {
				continue;
			}
			double dist = LatLngTool.distance(src, loc, LengthUnit.METER);

			if(dist > totalDist) {
				continue;
			}

			Date d = Tools.addDateTimeDiff(startDate, Tools.getWalkTime(dist));	
			IDeparture dep = dao.getNextDeparture(s, l, d);

			if(dep != null) {
				if(dep.getTime().before(walkArrival)) {
					set.add(dep);
				}
			}
			else {

			}
		}

		return set;
	}
	public TimeSpace getWalkArrival(IDeparture prev, LatLng loc, Date time) {
		double dist = LatLngTool.distance(loc, dst, LengthUnit.METER);
		Date arriveTime = Tools.addDateTimeDiff(time, Tools.getWalkTime(dist));
		TimeSpace ts = new TimeSpace(dst, arriveTime);
		ts.setPrevious(prev);

		return ts;
	}

	/**
	 * get neighbours of a Node and their link costs
	 * @return
	 */
	public SortedSet<IDeparture> getNeighbours(IDeparture n) {
		SortedSet<IDeparture> neighbours = new TreeSet<IDeparture>();
		///TODO dangerous assumption made that next departure is us coming!!
		/*
		 * Find next stop and its cost
		 */
		XMLStop next = dao.getNextStop(n.getStop(), n.getLine());
		if(next != null) {
			IDeparture dep = dao.getNextDeparture(next, n.getLine(), n.getTime());
			if(dep != null) {
				neighbours.add(dep);
			}
		}

		/*
		 * Find connecting lines and transition costs
		 */
		///TODO: implement minimum connection time??
		List<XMLLine> lines = n.getStop().getLines().getItems();

		for(XMLLine l : lines) {
			if(l.getCode().equals(n.getLine().getCode())) {
				continue;
			}

			IDeparture dep = dao.getNextDeparture(n.getStop(), l, n.getTime());
			if(dep == null) {
				continue;
			}

			neighbours.add(dep);
		}

		return neighbours;
	}
}
