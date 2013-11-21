package dmeneses.maptpg.datacollection.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.javadocmd.simplelatlng.LatLng;

public class CoordinateAdapter extends XmlAdapter<Object, LatLng> {	
	@Override
	public Object marshal(LatLng v) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document d = builder.newDocument();
		Element coordinates, latitude, longitude, referencial;
		
		coordinates = d.createElement("coordinates");
		latitude = d.createElement("latitude");
		longitude = d.createElement("longitude");
		referencial = d.createElement("referencial");
		
		coordinates.appendChild(latitude);
		coordinates.appendChild(longitude);
		coordinates.appendChild(referencial);
		
		latitude.setTextContent(Double.toString(v.getLatitude()));
		longitude.setTextContent(Double.toString(v.getLongitude()));
		referencial.setTextContent("WGS84");
		
		return coordinates;
	}

	@Override
	public LatLng unmarshal(Object obj) throws Exception {
		Element element = (Element) obj;

		Double latitude = null;
		Double longitude = null;

		NodeList nlLatitude = element.getElementsByTagName("latitude");
		NodeList nlLongitude = element.getElementsByTagName("longitude");

		if(nlLatitude.getLength() > 0) {
			latitude = Double.parseDouble(nlLatitude.item(0).getTextContent());
		}
		if(nlLongitude.getLength() > 0) {
			longitude = Double.parseDouble(nlLongitude.item(0).getTextContent());
		}

		if(latitude != null && longitude != null) {
			return new LatLng(latitude, longitude);
		}

		return null;
	}
}
