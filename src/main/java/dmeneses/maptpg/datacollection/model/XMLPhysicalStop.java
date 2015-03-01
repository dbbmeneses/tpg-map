package dmeneses.maptpg.datacollection.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.javadocmd.simplelatlng.LatLng;

import dmeneses.maptpg.datacollection.adapter.CoordinateAdapter;

@EqualsAndHashCode(of = {"code"})
@XmlRootElement(name = "physicalStop")
@XmlAccessorType(XmlAccessType.FIELD) //all fields serialized by default!
@Data
public class XMLPhysicalStop {
	@XmlElement(name = "physicalStopCode")
	private String code;
	
	@XmlElement(name = "stopName")
	private String name;
	
	@XmlElement(name = "coordinates")
	@XmlJavaTypeAdapter(CoordinateAdapter.class)
	private LatLng location;
	
	@XmlElement(name = "connections", type=ListWrapper.class)
	private ListWrapper<XMLLine> lines;

	@Override
	public String toString() {
		return name;
	}
}
