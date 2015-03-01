package dmeneses.maptpg.model;

import java.util.Date;

import dmeneses.maptpg.datacollection.model.XMLPhysicalStop;

public class Node {
	XMLPhysicalStop stop;
	Date time;
	Arc wait;
	Arc departure;
}
