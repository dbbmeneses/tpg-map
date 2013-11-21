package dmeneses.maptpg.process;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import dmeneses.maptpg.database.DAO;
import dmeneses.maptpg.database.Persistence;
import dmeneses.maptpg.database.types.IDeparture;
import dmeneses.maptpg.model.Line;
import dmeneses.maptpg.model.Stop;
import dmeneses.maptpg.utils.Tuple;



public class DijkstraNodes {
	Stop start;
	Date startTime;
	DAO dao;
	Persistence persistence;
	Itinerary result;

	public static void main(String[] args) throws JAXBException, ParserConfigurationException, IOException, SAXException {
		Persistence fetcher = new Persistence();
		fetcher.loadCache();
		DAO dao = new DAO(fetcher);
		
		Calendar c = Calendar.getInstance();
		c.set(2013, 10, 8, 17, 0, 0);
		Date startDate = c.getTime();
		
		DijkstraNodes djn = new DijkstraNodes(startDate, dao);
		djn.run();
	}

	public Itinerary getResult() {
		return result;
	}

	public DijkstraNodes(Date startTime, DAO dao) {
		this.startTime = startTime;
		this.dao = dao;
	}
	
	public HashMap<Tuple<Tuple<Line,Stop>, Tuple<Line,Stop>>, Long> run() {
		HashMap<Tuple<Tuple<Line,Stop>, Tuple<Line,Stop>>, Long> results = 
				new HashMap<Tuple<Tuple<Line,Stop>, Tuple<Line,Stop>>, Long>();

		SortedSet<IDeparture> candidates;
		HashMap<Tuple<Line,Stop>,IDeparture> candidatesHash;
		HashSet<IDeparture> visited;
		HashMap<Tuple<Line,Stop>,IDeparture> visitedHash;

		int i=0;
		for(Line line : dao.getAllLines()) {
			System.out.println(i + " - line " + line + " " + results.size());
			i++;

			for(Stop stop : line.getStops()) {
				IDeparture start = dao.getNextDeparture(stop, line, startTime);

				if(start == null) {
					continue;
				}
				
				visited = new HashSet<IDeparture>(10000);
				visitedHash = new HashMap<Tuple<Line,Stop>,IDeparture>(10000);
				candidatesHash = new HashMap<Tuple<Line,Stop>,IDeparture>(10000);
				candidates = new TreeSet<IDeparture>();
				
				candidates.add(start);
				candidatesHash.put(start.getPosition(), start);

				for(;;) {
					if(candidates.isEmpty()) {
						break;
					}

					IDeparture best = candidates.first();					
					candidates.remove(best);
					candidatesHash.remove(best.getPosition());

					visited.add(best);
					visitedHash.put(best.getPosition(), best);
					
					Tuple<Tuple<Line,Stop>, Tuple<Line,Stop>> t = new 
							Tuple<Tuple<Line,Stop>, Tuple<Line,Stop>>(start.getPosition(), best.getPosition());
					results.put(t, best.getTime().getTime());

					SortedSet<IDeparture> neighbours = getNeighbours(best);

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
							else{
								it.remove();
							}
						}
					}

					/*
					 * merge 
					 */
					candidates.addAll(neighbours);
					for(IDeparture d : neighbours) {
						candidatesHash.put(d.getPosition(), d);
					}
				}
				//System.out.println("\t" + stop + " " + visited.size());
			}
		}
		
		return results;
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
		Stop next = dao.getNextStop(n.getStop(), n.getLine());
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
		List<Line> lines = n.getStop().getLines().getItems();

		for(Line l : lines) {
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
