package dmeneses.maptpg.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import dmeneses.maptpg.datacollection.adapter.DateAdapter;


@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER) //all fields serialized!
@XmlRootElement(name = "step")
public class Step {
	private Stop stop;
	private Date timestamp;
	private String departureCode;
	private int arrivalTime;
	
	@Override
	public String toString() {
		return departureCode + "\t" + timestamp + "\t" +
				arrivalTime + "\t" + stop;
	}
	
	public int getArrivalTime() {
		return arrivalTime;
	}
	@XmlJavaTypeAdapter(DateAdapter.class)
	public Date getTimestamp() {
		return timestamp;
	}
	public String getDepartureCode() {
		return departureCode;
	}
	@XmlElement(name = "stop", type=Stop.class)
	public Stop getStop() {
		return stop;
	}
	
	public void setArrivalTime(int arrivalTime) {
		this.arrivalTime = arrivalTime;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	public void setDepartureCode(String departureCode) {
		this.departureCode = departureCode;
	}
	public void setStop(Stop stop) {
		this.stop = stop;
	}
	

	
}
