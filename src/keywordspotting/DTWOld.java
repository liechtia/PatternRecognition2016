package keywordspotting;

import java.util.ArrayList;
import java.util.List;

import utils.FtVector;

public class DTWOld {

	/**
	 * Compute the distance between testImage and all the images in trainingImages.
	 * It returns all the images in trainingImages ordered by their ascending distance
	 * (thus by descending similarity) from testImage.
	 * 
	 * @param trainingImages The training data set composed of labeled images.
	 * @param testImage The image to be spotted.
	 * @param band The width for the Sakoe-Chiba band (a higher number means a wider band).
	 * @return A list of DTWResult objects. A DTWResult contains a training image and
	 * its computed distance to testImage. The results are ordered by ascending distance.
	 */
//	public List<DTWResult> findSimilar(Map<String, ArrayList<Image>> trainingImages, Image testImage,
//							int band) {
//		List<DTWResult> result = new ArrayList<DTWResult>();
//		
//        for (Entry<String, ArrayList<Image>> entry: trainingImages.entrySet()) {
//        	List<Image> images = entry.getValue();
//        	for (Image i: images) {
//        		double distance = computeDTW(i, testImage, band);
//        		result.add(new DTWResult(i, distance));
//        	}
//        }
//        
//        Collections.sort(result, new Comparator<DTWResult>() {
//            @Override
//            public int compare(DTWResult r1, DTWResult r2) {
//                if (r1.getDistance() < r2.getDistance()) return -1;
//                if (r1.getDistance() > r2.getDistance()) return 1;
//                return 0;
//            }
//        });
//        
//        return result;
//	}
	
	/**
	 * Given two images composed of normalized vectors,
	 * it computes the DTW distance between the images.
	 * The distance metric used is Euclidean distance.
	 */
	public double computeDTW(KeywordImage trainImage, KeywordImage testImage,
							 int band) {
		List<FtVector> train = trainImage.getFeatureVectors();
		List<FtVector> test = testImage.getFeatureVectors();
		
		int len = train.size(); // The length of the sequences
		double[][] distMatrix = new double[len][len];
		distMatrix[0][0] = calcDistance(train.get(0), test.get(0));
		
		for (int i = 1; i < band; i++) {
			double dist = calcDistance(train.get(i), test.get(0));
			distMatrix[i][0] = distMatrix[i - 1][0] + dist;
		}
		
		for (int j = 1; j < band; j++) {
			double dist = calcDistance(train.get(0), test.get(j));
			distMatrix[0][j] = distMatrix[0][j - 1] + dist;
		}
		
		for (int i = 1; i < len; i++) {
			for (int j = 1; j < len; j++) {
				// Check if (i,j) is outside the band
				if (Math.abs(i - j) >= band) {
					continue;
				}
				
				double dist = calcDistance(train.get(i), test.get(j));
				
				double d1 = distMatrix[i - 1][j - 1];
				double d2 = -1.0; // -1.0 means that the cell is outside the band
				double d3 = -1.0;
				
				if (Math.abs(i - (j - 1)) < band) {
					d2 = distMatrix[i][j - 1];
				}
				if (Math.abs((i - 1) - j) < band) {
					d3 = distMatrix[i - 1][j];
				}
				
				double min = d1;
				if (d2 != -1.0 && d2 < min) {
					min = d2;
				}
				if (d3 != -1.0 && d3 < min) {
					min = d3;
				}
				
				distMatrix[i][j] = min + dist;
			}
		}
		
		return distMatrix[len - 1][len - 1];
	}
	
	/**
	 * Compute Euclidean distance between two feature vectors of the same length.
	 */
 	public double calcDistance(FtVector vector1, FtVector vector2) {
 		ArrayList<Double> v1 = vector1.getFeatures();
 		ArrayList<Double> v2 = vector2.getFeatures();
 		double distance = 0.0;
		for (int i = 0; i < v1.size(); i++) {
			distance += (v1.get(i) - v2.get(i)) * (v1.get(i) - v2.get(i));
		}
		return Math.sqrt(distance);
	}
}
