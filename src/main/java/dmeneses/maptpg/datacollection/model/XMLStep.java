package dmeneses.maptpg.datacollection.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import lombok.Data;
import dmeneses.maptpg.datacollection.adapter.DateAdapter;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "step")
public class XMLStep {
	@XmlElement(name = "stop", type = XMLStop.class)
	private XMLStop stop;
	@XmlJavaTypeAdapter(DateAdapter.class)
	private Date timestamp;
	private String departureCode;
	private int arrivalTime;

	@Override
	public String toString() {
		return departureCode + "\t" + timestamp + "\t" + arrivalTime + "\t" + stop;
	}
}
