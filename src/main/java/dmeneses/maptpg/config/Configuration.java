package dmeneses.maptpg.config;

import lombok.Data;
import dmeneses.maptpg.image.gradient.GradientFactory.GRADIENTS;
import dmeneses.maptpg.process.Itinerary;
import dmeneses.maptpg.process.Itinerary.DATA_TYPE;
import dmeneses.maptpg.utils.Tools;

@Data
public class Configuration {
	//THESE CAN BE MODIFIED
	private int imageSize = 1000;
	private Itinerary.DATA_TYPE dataType = DATA_TYPE.TIME;
	private Double maxScale = null;
	private String sourceLocation = "CERN";
	private int startHour = 15;
	private GRADIENTS gradientType = GRADIENTS.LINEAR_HUE;
	private String name = "output_" + Tools.getRandomHexString(8);
	private int numPoints = 251;
	private String loadPath = null; //"/tmp/results"

	//THESE CAN'T
	public final static String KML_LOCATION = System.getProperty("user.home") + "/tpg/datastore/geneva.kml";
	public final static String CACHE_ROOT = System.getProperty("user.home") + "/tpg/datastore/";

}
