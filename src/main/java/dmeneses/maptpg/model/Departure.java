package dmeneses.maptpg.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import dmeneses.maptpg.datacollection.adapter.DateAdapter;



@XmlRootElement(name = "departure")
@XmlAccessorType(XmlAccessType.NONE)
public class Departure {
	private String code;
	private int waitingTime;
	private int waitingTimeMillis;
	private Line line;
	private String reliability;
	private String characteristics;
	private Date timestamp;
	
	@XmlElement(name = "departureCode")
	public String getCode() {
		return code;
	}
	@XmlElement
	public int getWaitingTime() {
		return waitingTime;
	}
	@XmlElement
	public int getWaitingTimeMillis() {
		return waitingTimeMillis;
	}
	@XmlElement(name = "connection")
	public Line getLine() {
		return line;
	}
	@XmlElement
	public String getReliability() {
		return reliability;
	}
	@XmlElement
	public String getCharacteristics() {
		return characteristics;
	}
	@XmlElement
	@XmlJavaTypeAdapter(DateAdapter.class)
	public Date getTimestamp() {
		return timestamp;
	}
	
	@Override
	public String toString() {
		return code; 
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	public void setWaitingTime(int waitingTime) {
		this.waitingTime = waitingTime;
	}
	public void setWaitingTimeMillis(int waitingTimeMillis) {
		this.waitingTimeMillis = waitingTimeMillis;
	}
	public void setLine(Line line) {
		this.line = line;
	}
	public void setReliability(String reliability) {
		this.reliability = reliability;
	}
	public void setCharacteristics(String characteristics) {
		this.characteristics = characteristics;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	
}
