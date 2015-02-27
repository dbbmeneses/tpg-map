package dmeneses.maptpg.model;

import lombok.Data;

@Data
public class Location {
	private double latitude;
	private double longitude;
	private String referential;
}