package dmeneses.maptpg.process;

import java.util.Date;
import java.util.LinkedList;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import dmeneses.maptpg.database.types.IDeparture;


public class Itinerary {
	private LatLng src;
	private LatLng dst;
	private Date startTime;
	private Date endTime;
	private LinkedList<IDeparture> path;
	private int steps;
	private double walkTime;
	
	public Itinerary(LatLng src, LatLng dst, Date startTime, Date endTime,
			LinkedList<IDeparture> path, int steps) {
		super();
		this.src = src;
		this.dst = dst;
		this.startTime = startTime;
		this.endTime = endTime;
		this.path = path;
		this.steps = steps;
		
		if(path != null && !path.isEmpty()) {
			double t1 = ((double) path.getLast().getTime().getTime() - startTime.getTime())  / (60.0*1000.0);
			double t2 = ((double) endTime.getTime() - path.getFirst().getTime().getTime())  / (60.0*1000.0);

			this.walkTime = t1+t2;
		}
		else {
			this.walkTime =  ((double)endTime.getTime() - startTime.getTime()) / (60.0*1000.0);
		}

	}
	public Itinerary(LatLng src, LatLng dst, Date startTime, Date endTime, double walkTime, int steps) {
		super();
		this.src = src;
		this.dst = dst;
		this.startTime = startTime;
		this.endTime = endTime;
		this.walkTime = walkTime;
		this.steps = steps;
	}

	public LatLng getSrc() {
		return src;
	}
	public LatLng getDst() {
		return dst;
	}
	public int getSteps() {
		return steps;
	}
	public double getWalkTime() {
		return walkTime;
	}
	public Date getStartTime() {
		return startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public LinkedList<IDeparture> getPath() {
		return path;
	}

	public static String getLegend(DATA_TYPE type) {
		switch(type) {
		case TIME: 				// minutes
			return	"Total time to destination (minutes)";
			
		case WALK_TIME: 		// minutes
			return 	"Total time walking (minutes)";

		case NORMALIZED_TIME: 	// seconds per meter
			return "Normalized time per distance (seconds per meter)";
			
		case SPEED: 			// km/h
			return "Average straight line speed (km/h)";
		case STEPS:
			return "Number of lines used";
			
		default:
			return null;
		}
	}
	
	public double getData(DATA_TYPE type) {
		switch(type) {
		case TIME: 				// minutes
			return ((double)endTime.getTime() - startTime.getTime()) / (60.0*1000.0);
			
		case WALK_TIME: 		// minutes
			return this.walkTime;

		case NORMALIZED_TIME: 	// seconds per meter
			return ((double)(endTime.getTime() - startTime.getTime())) 
					/ (1000.0*LatLngTool.distance(src, dst, LengthUnit.METER));
			
		case SPEED: 			// km/h
			double t = ((double)endTime.getTime() - startTime.getTime()) / (60.0*60*1000.0);
			double d = LatLngTool.distance(src, dst, LengthUnit.METER) / 1000.0;
			return d/t;
		case STEPS:
			return steps;
			
		default:
			return 0.0;
		}
	}

	public enum DATA_TYPE { TIME, WALK_TIME, NORMALIZED_TIME, SPEED, STEPS };



}
