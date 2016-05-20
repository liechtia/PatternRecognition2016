package keywordspotting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import utils.DTWFeatureVector;
import utils.DTWResult;
import utils.FeatureVector;
import utils.FtVector;
import utils.Tuple;

public class DTW {

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
	
	
	private static int findLengthOfPath(Tuple[][] path, int n, int m)
	{
	    int i = n-1;
	    int j = m-1;
	    
	    int lengthOfPath =0 ;
	    while(i != 0 && j != 0)
	    {
	        Tuple prev= path[i][j];

	        
	        i = prev.x;
	        
	        j = prev.y; 
	        lengthOfPath++;
	    }
	    
	    return lengthOfPath;
	    
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
}
