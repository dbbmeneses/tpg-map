package dmeneses.maptpg.database.types;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.javadocmd.simplelatlng.LatLng;

import dmeneses.maptpg.datacollection.model.XMLLine;
import dmeneses.maptpg.datacollection.model.XMLStop;
import dmeneses.maptpg.utils.Tuple;


/**
 * Note: this class has a natural ordering that is inconsistent with equals.
 * @author Duarte Meneses <duarte.meneses@cern.ch>
 *
 */
@Data
@EqualsAndHashCode(of = {"location", "position", "time"})
public class IDeparture implements Comparable<IDeparture>  {
	private LatLng location;
	private Date time;
	private Tuple<XMLLine, XMLStop> position;

	public IDeparture(XMLStop stop, XMLLine line, Date time, LatLng location) {
		this.time = time;
		this.location = location;
		this.position = new Tuple<XMLLine,XMLStop>(line, stop);
	}

	public IDeparture(IDeparture copy) {
		this.time = copy.time;
		this.position = copy.position;
		this.location = copy.location;
	}

	public XMLStop getStop() {
		return position.getSecond();
	}

	public void setStop(XMLStop stop) {
		this.position.setSecond(stop);
	}

	public XMLLine getLine() {
		return this.position.getFirst();
	}

	public void setLine(XMLLine line) {
		this.position.setFirst(line);
	}

	@Override
	public String toString() {
		return String.format("%-18s %-22s %-15s", position.getSecond(), position.getFirst(), time);
	}

	@Override
	public int compareTo(IDeparture o) {
		int c = time.compareTo(o.time);

		if (c != 0) {
			return c;
		}

		c = Double.compare(this.location.getLatitude(), o.location.getLatitude());
		if (c != 0) {
			return c;
		}

		c = Double.compare(this.location.getLongitude(), o.location.getLongitude());
		if (c != 0) {
			return c;
		}

		c = this.getLine().getCode().compareTo(o.getLine().getCode());
		if (c != 0) {
			return c;
		}

		c = this.getStop().getCode().compareTo(o.getStop().getCode());

		return c;
	}
}
