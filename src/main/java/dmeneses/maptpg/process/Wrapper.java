package dmeneses.maptpg.process;

import java.util.Date;

import com.javadocmd.simplelatlng.LatLng;

import dmeneses.maptpg.database.DAO;


public class Wrapper implements Runnable {
	LatLng src;
	LatLng dst;
	Date startTime;
	DAO dao;
	Itinerary result;
	
	public Wrapper(LatLng src, LatLng dst, Date startTime, DAO dao) {
		this.src = src;
		this.dst = dst;
		this.startTime = startTime;
		this.dao = dao;
		this.result = null;
	}
	@Override
	public void run() {
		Dijkstra dj = new Dijkstra(src, dst, startTime, dao);
		dj.run();
		result = dj.getResult();
		synchronized(this) {
			this.notifyAll();
		}
	}
	
	public Itinerary getResult() {
		synchronized(this) {
			  while (result == null) {
			        try {
			            this.wait();
			        } catch (InterruptedException ie) { }
			    }
		}
		
		return result;

	}
}
