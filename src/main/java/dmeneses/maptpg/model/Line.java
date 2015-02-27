package dmeneses.maptpg.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@XmlRootElement(name = "connection")
@XmlAccessorType(XmlAccessType.FIELD)
@EqualsAndHashCode(of = {"code", "destinationCode"})
public class Line {
	@XmlElement(name = "lineCode")
	private String code;
	private String destinationName;
	private String destinationCode;
	private List<Stop> stops;

	public Line() {}

	public Line(String code, String destinationCode, List<Stop> stops) {
		this.code = code;
		this.destinationCode = destinationCode;
		this.stops = stops;
	}

	@Override
	public String toString() {
		return code + " (" + destinationName + ")";
	}

}
