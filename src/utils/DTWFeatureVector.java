package utils;

import java.util.List;

/**
 * interface to make dtw method accept both keyword images and signatures
 */
public interface DTWFeatureVector {
	/**
	 * @return
	 * 
	 * returns feature vectors for the image/signature class
	 */
	List<FtVector> getFeatureVectors();
}
