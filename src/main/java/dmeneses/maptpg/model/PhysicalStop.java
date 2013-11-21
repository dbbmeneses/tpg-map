package dmeneses.maptpg.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.javadocmd.simplelatlng.LatLng;

import dmeneses.maptpg.datacollection.adapter.*;


@XmlRootElement(name = "physicalStop")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER) //all public fields serialized by default!
public class PhysicalStop {
	private String code;
	private String name;
	private LatLng location;
	private ListWrapper<Line> lines;
	
	@XmlElement(name = "connections", type=ListWrapper.class)
	public ListWrapper<Line> getLines() {
		return lines;
	}
	@XmlElement(name = "coordinates")
	@XmlJavaTypeAdapter(CoordinateAdapter.class)
	public LatLng getLocation() {
		return location;
	}
	@XmlElement(name = "physicalStopCode")
	public String getCode() {
		return code;
	}
	@XmlElement(name = "stopName")
	public String getName() {
		return name;
	}
	
	
	@Override 
	public String toString() {
		return name;
	}
	
	@Override
	public int hashCode() {
		return code.hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if(obj == null || this.getClass() != obj.getClass()) {
			return false;
		}
		
		final PhysicalStop other = (PhysicalStop) obj;
		
		if(this.code == other.code) {
			return true;
		}
		
		return false;
	}
	
	public void setLocation(LatLng location) {
		this.location = location;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setLines(ListWrapper<Line> lines) {
		this.lines = lines;
	}
	
	
}
