package dmeneses.maptpg.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import lombok.Data;
import dmeneses.maptpg.datacollection.adapter.DateAdapter;

@Data
@XmlRootElement(name = "departure")
@XmlAccessorType(XmlAccessType.FIELD)
public class Departure {
	@XmlElement(name = "departureCode")
	private String code;

	private int waitingTime;
	private int waitingTimeMillis;

	@XmlElement(name = "connection")
	private Line line;

	private String reliability;
	private String characteristics;

	@XmlElement
	@XmlJavaTypeAdapter(DateAdapter.class)
	private Date timestamp;
}
