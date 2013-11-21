package dmeneses.maptpg.model;

public class Location {
	private double latitude;
	private double longitude;
	private String referential;
	
	
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public String getReferential() {
		return referential;
	}
	public void setReferential(String referential) {
		this.referential = referential;
	}
}
