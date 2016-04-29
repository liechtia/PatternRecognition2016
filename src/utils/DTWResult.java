package utils;

import ks.datahandling.Image;

public class DTWResult {
	private Image image;
	private double distance;
	
	public DTWResult(Image image, double distance) {
		this.image = image;
		this.distance = distance;
	}
	
	public Image getImage() {
		return image;
	}
	public void setImage(Image image) {
		this.image = image;
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
}
