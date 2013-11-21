package dmeneses.maptpg.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "connection")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER) //all fields serialized!
public class Line {
	private String code;
	private String destinationName;
	private String destinationCode;
	private List<Stop> stops;
	
	public Line() {
	}
	public Line(String code, String destinationCode, List<Stop> stops) {
		this.code = code;
		this.destinationCode = destinationCode;
		this.stops = stops;
	}
	
	public List<Stop> getStops() {
		return stops;
	}
	public String getDestinationName() {
		return destinationName;
	}
	public String getDestinationCode() {
		return destinationCode;
	}

	public void setStops(List<Stop> stops) {
		this.stops = stops;
	}
	@XmlElement(name = "lineCode")
	public String getCode() {
		return code;
	}
	
	@Override 
	public String toString() {
		return code + " (" + destinationName + ")";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result
				+ ((destinationCode == null) ? 0 : destinationCode.hashCode());
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
		Line other = (Line) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (destinationCode == null) {
			if (other.destinationCode != null)
				return false;
		} else if (!destinationCode.equals(other.destinationCode))
			return false;
		return true;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	public void setDestinationName(String destinationName) {
		this.destinationName = destinationName;
	}
	public void setDestinationCode(String destinationCode) {
		this.destinationCode = destinationCode;
	}
}
