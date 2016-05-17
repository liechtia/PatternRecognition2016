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
	public List<DTWResult> findSimilar(Map<String, ArrayList<DTWFeatureVector>> trainingImages, DTWFeatureVector testImage,
							int band) {
		List<DTWResult> result = new ArrayList<DTWResult>();
		
        for (Entry<String, ArrayList<DTWFeatureVector>> entry: trainingImages.entrySet()) {
        	List<DTWFeatureVector> images = entry.getValue();
        	for (DTWFeatureVector i: images) {
        		double distance = computeDTW(i, testImage, band);
        		result.add(new DTWResult(i, distance));
        	}
        }
        
        Collections.sort(result, new Comparator<DTWResult>() {
            @Override
            public int compare(DTWResult r1, DTWResult r2) {
                if (r1.getDistance() < r2.getDistance()) return -1;
                if (r1.getDistance() > r2.getDistance()) return 1;
                return 0;
            }
        });
        
        return result;
	}
	
	/**
	 * Given two images composed of normalized vectors,
	 * it computes the DTW distance between the images.
	 * The distance metric used is Euclidean distance.
	 */
	public static double computeDTW(DTWFeatureVector trainImage, DTWFeatureVector testImage,
							 int band) {
		List<FtVector> train = trainImage.getFeatureVectors();
		List<FtVector> test = testImage.getFeatureVectors();
		
		int n = train.size(); // The length of the sequences
		int m = test.size();
		double[][] distMatrix = new double[n][m];
		distMatrix[0][0] = calcDistance(train.get(0), test.get(0));
		
		Tuple[][] path = new Tuple[n][m];
		
		for (int i = 1; i < n; i++) {
			double dist = calcDistance(train.get(i), test.get(0));
			distMatrix[i][0] = distMatrix[i - 1][0] + dist;
		}
		
		for (int j = 1; j < m; j++) {
			double dist = calcDistance(train.get(0), test.get(j));
			distMatrix[0][j] = distMatrix[0][j - 1] + dist;
		}
		
		for (int i = 1; i < n; i++) {
			for (int j = 1; j < m; j++) {
				// Check if (i,j) is outside the band
			    if(! (j-band <= i) && !(i <= j+band))
			    {
			        continue;
			    }

			    
				double dist = calcDistance(train.get(i), test.get(j));
				
				double d1 = distMatrix[i - 1][j - 1];
				double d2 = distMatrix[i][j-1];
				double d3 = distMatrix[i-1][j];
				
				Tuple t  = new Tuple(0,0);
				double min = 0;
				if(d1 <= d2 && d1 <= d3)
				{
				    min = d1;
				    t.x = i-1;
				    t.y = j-1;
				   
				    path[i][j] = t;
				}
				
				if(d2 <= d1 && d2 <= d3)
                {
                    min = d2;
                    t.x = i;
                    t.y = j-1;
                    path[i][j] = t;
                }
				
				if(d3 <= d2 && d3 <= d2)
                {
                    min = d3;
                    t.x = i-1;
                    t.y = j;
                    path[i][j] = t;
                }

			
				
				distMatrix[i][j] = min + dist;
			}
		}
		
		//normalize the distance
		return distMatrix[n - 1][m - 1];
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
 	public static double calcDistance(FtVector ftVector, FtVector ftVector2) {
 		double[] v1 = ftVector.getAllFeatures();
		double[] v2 = ftVector2.getAllFeatures();
 		double distance = 0.0;
		for (int i = 0; i < v1.length; i++) {
			distance += (v1[i] - v2[i]) * (v1[i] - v2[i]);
		}
		return distance;
	}
}
