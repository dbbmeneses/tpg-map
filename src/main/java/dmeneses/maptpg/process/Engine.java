package dmeneses.maptpg.process;

import java.util.Date;

import com.javadocmd.simplelatlng.LatLng;

public interface Engine {
	public Itinerary query(LatLng source, LatLng dst, Date startTime);
	
	
}
