package utils;

import java.util.ArrayList;

/**
 * implemented by signature feature class and feature vector for keyword images so that the computedistance method in dtw can be used by both classes
 */
public interface FtVector {
	/**
	 * normalize the features
	 */
	void normFeatures();
	/**
	 * @return
	 * 
	 * get all features in an array
	 */
	double[] getAllFeatures();
	/**
	 * @return
	 * 
	 * get features as list
	 */
	ArrayList<Double> getFeatures();
}
