package dmeneses.maptpg.process;

import java.util.Date;


import com.javadocmd.simplelatlng.LatLng;

import dmeneses.maptpg.database.types.IDeparture;

public class TimeSpace implements Comparable<TimeSpace> {
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((location == null) ? 0 : location.hashCode());
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
		TimeSpace other = (TimeSpace) obj;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		return true;
	}

	protected LatLng location;
	protected Date time;
	private IDeparture previous;

	public IDeparture getPrevious() {
		return previous;
	}

	public void setPrevious(IDeparture previous) {
		this.previous = previous;
	}

	public TimeSpace(LatLng location, Date time) {
		super();
		this.location = location;
		this.time = time;
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

	@Override
	public int compareTo(TimeSpace other) {
		int c = time.compareTo(other.getTime());

		if(c != 0) {
			return c;
		}

		if(this.location.equals(other.location)) {
			return 0;
		}

		c = Double.compare(this.location.getLatitude(), other.location.getLatitude());
		if(c != 0) {
			return c;
		}
		
		c = Double.compare(this.location.getLongitude(), other.location.getLongitude());
		if(c != 0) {
			return c;
		}

		return 0;
	}

}
