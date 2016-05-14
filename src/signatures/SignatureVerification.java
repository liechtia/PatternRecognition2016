package signatures;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import keywordspotting.DTW;
import keywordspotting.KeywordImage;
import utils.DTWResult;
import utils.Distance;
import utils.FeatureVector;
import utils.Tuple;

public class SignatureVerification {
	private int[] users;
	private GroundTruth[] gts;
	private Signature[] enrollment;
	private Signature[] verification;
	private SignatureResult[] results;
	
	public SignatureVerification() throws IOException{
		SignatureLoader loader = new SignatureLoader();
        users = loader.LoadUsers();
        gts = loader.LoadGroundTruth();
        enrollment = loader.LoadEnrollmentSignatures();
        verification = loader.LoadVerificationSignatures();
	}
	
	public void RunVerification(){
	   List<SignatureResult> res = new ArrayList();
       for(Signature verificationSig : verification){
    	   for(Signature enrollmentSig : enrollment){
    		   if (enrollmentSig.user == verificationSig.user){
    			   double score = computeSignatureDTW(enrollmentSig, verificationSig, 10);  
    			   res.add(new SignatureResult(enrollmentSig.user, enrollmentSig, verificationSig, score));
    		   }   
    	   }
        }
       
       results = new SignatureResult[res.size()];
       for(int i = 0;i < results.length;i++)
           results[i] = res.get(i);
       
       for(int i=0;i<users.length;i++){
    	   //todo: evaluation of results
       }
	}	
	
	public static double computeSignatureDTW(Signature enrollmentSig, Signature verificationSig, int band) {
		SignatureFeature[] enrollmentFt = enrollmentSig.features;
		SignatureFeature[] verificationFt = verificationSig.features;
		
		int n = enrollmentFt.length; // The length of the sequences
		int m = verificationFt.length;
		double[][] distMatrix = new double[n][m];
		distMatrix[0][0] = calcDistance(enrollmentFt[0], verificationFt[0]);
		
		Tuple[][] path = new Tuple[n][m];
		
		for (int i = 1; i < n; i++) {
			double dist = calcDistance(enrollmentFt[i], verificationFt[0]);
			distMatrix[i][0] = distMatrix[i - 1][0] + dist;
		}
		
		for (int j = 1; j < m; j++) {
			double dist = calcDistance(enrollmentFt[0], verificationFt[j]);
			distMatrix[0][j] = distMatrix[0][j - 1] + dist;
		}
		
		for (int i = 1; i < n; i++) {
			for (int j = 1; j < m; j++) {
				// Check if (i,j) is outside the band
			    if(! (j-band <= i) && !(i <= j+band))
			    {
			        continue;
			    }

			    
				double dist = calcDistance(enrollmentFt[i], verificationFt[j]);
				
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

 	public static double calcDistance(SignatureFeature vector1, SignatureFeature vector2) {
 		double[] v1 = vector1.getAllFeatures();
		double[] v2 = vector2.getAllFeatures();
 		double distance = 0.0;
		for (int i = 0; i < v1.length; i++) {
			distance += (v1[i] - v2[i]) * (v1[i] - v2[i]);
		}
		return distance;
	}
}