package dmeneses.maptpg.database.types;

import java.util.Date;

import com.javadocmd.simplelatlng.LatLng;

import dmeneses.maptpg.model.Line;
import dmeneses.maptpg.model.Stop;
import dmeneses.maptpg.utils.Tuple;


/**
 * Note: this class has a natural ordering that is inconsistent with equals.
 * @author Duarte Meneses <duarte.meneses@cern.ch>
 *
 */
public class IDeparture implements Comparable<IDeparture>  {
	private LatLng location;
	private Date time;
	private Tuple<Line, Stop> position;

	public IDeparture(Stop stop, Line line, Date time, LatLng location) {
		this.time = time;
		this.location = location;
		this.position = new Tuple<Line,Stop>(line, stop);
	}
	
	public IDeparture(IDeparture copy) {
		this.time = copy.time;
		this.position = copy.position;
		this.location = copy.location;
	}
	
	public Tuple<Line, Stop> getPosition() {
		return position;
	}

	public void setPosition(Tuple<Line, Stop> position) {
		this.position = position;
	}
	public LatLng getLocation() {
		return location;
	}
	public void setLocation(LatLng location) {
		this.location = location;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public Stop getStop() {
		return position.getSecond();
	}
	public void setStop(Stop stop) {
		this.position.setSecond(stop);
	}

	public Line getLine() {
		return this.position.getFirst();
	}
	public void setLine(Line line) {
		this.position.setFirst(line);
	}
	
	@Override
	public String toString() {
		return String.format("%-18s %-22s %-15s", position.getSecond(), position.getFirst(), time);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		
		result = prime * result
				+ ((location == null) ? 0 : location.hashCode());
		result = prime * result
				+ ((position == null) ? 0 : position.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IDeparture other = (IDeparture) obj;
		
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		if (position == null) {
			if (other.position != null)
				return false;
		} else if (!position.equals(other.position))
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		return true;
	}
	

	@Override
	public int compareTo(IDeparture o) {
		int c = time.compareTo(o.time);
		
		if(c != 0) {
			return c;
		}
		
		c = Double.compare(this.location.getLatitude(), o.location.getLatitude());
		if(c != 0) {
			return c;
		}
		
		c = Double.compare(this.location.getLongitude(), o.location.getLongitude());
		if(c != 0) {
			return c;
		}
		
		c = this.getLine().getCode().compareTo(o.getLine().getCode());
		if(c != 0) {
			return c;
		}
		
		c = this.getStop().getCode().compareTo(o.getStop().getCode());

		return c;

		
	}

}
