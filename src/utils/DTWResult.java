package utils;

import keywordspotting.KeywordImage;

public class DTWResult {
	private KeywordImage image;
	private double distance;
	
	public DTWResult(KeywordImage image, double distance) {
		this.image = image;
		this.distance = distance;
	}
	
	public KeywordImage getImage() {
		return image;
	}
	public void setImage(KeywordImage image) {
		this.image = image;
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
}
