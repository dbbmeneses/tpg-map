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
@XmlAccessorType(XmlAccessType.NONE)
public class Departure {
	@XmlElement(name = "departureCode")
	private String code;

	@XmlElement
	private int waitingTime;

	@XmlElement
	private int waitingTimeMillis;

	@XmlElement(name = "connection")
	private Line line;

	@XmlElement
	private String reliability;

	@XmlElement
	private String characteristics;

	@XmlElement
	@XmlJavaTypeAdapter(DateAdapter.class)
	private Date timestamp;
}
