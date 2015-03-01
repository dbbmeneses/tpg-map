package dmeneses.maptpg.datacollection.model;

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
public class XMLLine {
	@XmlElement(name = "lineCode")
	private String code;
	private String destinationName;
	private String destinationCode;
	private List<XMLStop> stops;

	public XMLLine() {}

	public XMLLine(String code, String destinationCode, List<XMLStop> stops) {
		this.code = code;
		this.destinationCode = destinationCode;
		this.stops = stops;
	}

	@Override
	public String toString() {
		return code + " (" + destinationName + ")";
	}

}
