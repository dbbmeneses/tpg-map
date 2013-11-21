package dmeneses.maptpg.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "stop")
public class Stop {
	private String code;
	private String name;
	/* this information must be filled manually. It doesn't come from the webservice if 
	 *  the physical stops are requested!
	 */ 
	private ListWrapper<Line> lines;
	private ListWrapper<PhysicalStop> physicalStops;
	
	@XmlElement(name = "physicalStops", type=ListWrapper.class)
	public ListWrapper<PhysicalStop> getPhysicalStops() {
		return physicalStops;
	}
	@XmlElement(name = "stopCode")
	public String getCode() {
		return code;
	}
	@XmlElement(name = "stopName")
	public String getName() {
		return name;
	}
	@XmlElement(name = "connections", type=ListWrapper.class)
	public ListWrapper<Line> getLines() {
		return lines;
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
		
		final Stop other = (Stop) obj;
		
		if(this.code.equals(other.code)) {
			return true;
		}
		
		return false;
	}
	@Override
	public String toString() {
		return this.name;
	}
	
	
	
	public void setName(String name) {
		this.name = name;
	}
	public void setPhysicalStops(ListWrapper<PhysicalStop> physicalStops) {
		this.physicalStops = physicalStops;
	}
	public void setLines(ListWrapper<Line> lines) {
		this.lines = lines;
	}
	public void setCode(String code) {
		this.code = code;
	}
}
