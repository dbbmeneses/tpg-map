package dmeneses.maptpg.process;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.javadocmd.simplelatlng.LatLng;

import dmeneses.maptpg.database.types.IDeparture;

@Data
@EqualsAndHashCode(of={"location", "time"})
public class TimeSpace implements Comparable<TimeSpace> {
	protected LatLng location;
	protected Date time;
	private IDeparture previous;

	public TimeSpace(LatLng location, Date time) {
		super();
		this.location = location;
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
