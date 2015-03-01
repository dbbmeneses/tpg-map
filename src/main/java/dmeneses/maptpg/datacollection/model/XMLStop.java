package dmeneses.maptpg.datacollection.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;
import lombok.EqualsAndHashCode;

@XmlRootElement(name = "stop")
@EqualsAndHashCode(of = {"code"})
@XmlAccessorType(XmlAccessType.NONE)
@Data
public class XMLStop {
	@XmlElement(name = "stopCode")
	private String code;

	@XmlElement(name = "stopName")
	private String name;

	/* this information must be filled manually. It doesn't come from the webservice if
	 *  the physical stops are requested!
	 */
	@XmlElement(name = "connections", type = ListWrapper.class)
	private ListWrapper<XMLLine> lines;

	@XmlElement(name = "physicalStops", type = ListWrapper.class)
	private ListWrapper<XMLPhysicalStop> physicalStops;

	@Override
	public String toString() {
		return this.name;
	}
}
