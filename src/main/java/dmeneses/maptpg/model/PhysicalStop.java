package dmeneses.maptpg.model;

import java.util.List;
import lombok.Data;
import com.javadocmd.simplelatlng.LatLng;

@Data
public class PhysicalStop {
	private String code;
	private String name;
	private LatLng location;
	private List<Line> lines;

	@Override
	public String toString() {
		return name;
	}
}
