package utils;

import keywordspotting.KeywordImage;

public class DTWResult {
	private DTWFeatureVector image;
	private double distance;
	
	public DTWResult(DTWFeatureVector i, double distance) {
		this.image = i;
		this.distance = distance;
	}
	
	public DTWFeatureVector getImage() {
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
