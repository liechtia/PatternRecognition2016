package keywordspotting;

import java.util.ArrayList;
import java.util.List;
import utils.FtVector;


/**
 * This class contains methods for computing the DTW distance between two sequences
 * of feature vectors. The distance metric used is Euclidean distance.
 */

public class DTW {
	
	/**
	 * Given two images composed of normalized vectors,
	 * it computes the DTW distance between the images.
	 * The distance metric used is Euclidean distance.
	 */
	public static double computeDTW(double[][] image1, double[][] image2, 
							 int band) {
		
	    int  n = image1.length;
        int m = image2.length;


        
        double[][] distances = new double[n][m];
        double[][] matrix = new double[n][m];
        
        for(int i =0 ; i < n; i++)
        {
            for(int j = 0; j < m; j++)
            {
                distances[i][j] = calcDistance(image1[i], image2[j]);
              
            }
          
        }
                
        matrix[0][0] = distances[0][0];
        
        for(int i = 1; i < n; i++)
        {
            matrix[i][0] = matrix[i-1][0] + distances[i][0];
 
        }
       
        for(int j = 1; j < m; j++)
        {
            matrix[0][j] = matrix[0][j-1] + distances[0][j];
        }

        for(int i =1 ; i < n; i++)
        {
            for(int j = 1; j < m; j++)
            {
                
                double minValue = Math.min(matrix[i][j-1], Math.min(matrix[i-1][j-1],  matrix[i-1][j]));
              
                 matrix[i][j] = minValue + distances[i][j];
                
                
            }

        }
        
        return matrix[n-1][m-1]/(n+m);
	}

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
 	public static double calcDistance(double[] v1, double[] v2) {

 	  
 		double distance = 0.0;
		for (int i = 0; i < v1.length; i++) {
			distance += (v1[i] - v2[i]) * (v1[i] - v2[i]);
		}
		return distance;
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
