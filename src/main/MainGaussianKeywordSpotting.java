package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import keywordspotting.DTW;
import keywordspotting.KeywordImage;
import utils.DTWResult;
import utils.FeatureVector;
import utils.FtVector;

public class MainGaussianKeywordSpotting {

	private final static String TRAINING_IMAGES = "KeywordSpottingData/features/gaussian_features.txt";
    private final static String TEST_IMAGES = "KeywordSpottingData_Test/features/gaussian_features.txt";
    private final static String KEYWORDS_FILE = "KeywordSpottingData_Test/task/keywords.txt";
    private final static String RESULTS_FILE = "results/keywordspottingGaussian.txt"; 
	
    public static void main(String[] args) throws IOException {
        
        ArrayList<KeywordImage> trainingImages =  new  ArrayList<KeywordImage>();
        ArrayList<KeywordImage> testImages =  new ArrayList<KeywordImage>();
        
        // Load train images
        File file = new File(TRAINING_IMAGES);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null) {
        	
        	String[] values = line.split(",");
        	String[] informations = values[0].split(" ");
        	String id = informations[0];
        	String label = informations[1];
            
            KeywordImage im = new KeywordImage(id, label);
            
            // Load the features of the images
            ArrayList<FtVector> vectors = new ArrayList<FtVector>();
            ArrayList<Double> featureVector = new ArrayList<Double>();
            int count = 0;
            
            for (int i = 1; i < values.length; i++) {
            	double d = Double.parseDouble(values[i]);
            	featureVector.add(d);
            	count++;
            	if (count == 15) {
            		FeatureVector v = new FeatureVector();
            		v.setFeatures(featureVector);
            		vectors.add(v);
            		featureVector = new ArrayList<Double>();
            		count = 0;
            	}
			}
            if (count != 0) {
            	System.out.println("Error");
            }
            
            im.setFeatures(vectors);
            trainingImages.add(im);
        }
        br.close();
        
        
        // Load test images
        file = new File(TEST_IMAGES);
        br = new BufferedReader(new FileReader(file));
        while ((line = br.readLine()) != null) {
        	
        	String[] values = line.split(",");
        	String id = values[0];
            
            KeywordImage im = new KeywordImage(id, null);
            
            // Load the features of the images
            ArrayList<FtVector> vectors = new ArrayList<FtVector>();
            ArrayList<Double> featureVector = new ArrayList<Double>();
            int count = 0;
            
            for (int i = 1; i < values.length; i++) {
            	double d = Double.parseDouble(values[i]);
            	featureVector.add(d);
            	count++;
            	if (count == 15) {
            		FeatureVector v = new FeatureVector();
            		v.setFeatures(featureVector);
            		vectors.add(v);
            		featureVector = new ArrayList<Double>();
            		count = 0;
            	}
			}
            if (count != 0) {
            	System.out.println("Error");
            }
            
            im.setFeatures(vectors);
            testImages.add(im);
        }
        br.close();
         
        System.out.println("Features loaded.");
        
        ArrayList<String> keywords = new ArrayList<String>();
        br = new BufferedReader(new FileReader(KEYWORDS_FILE));
        line = null;
        
        while((line = br.readLine()) != null) {
            keywords.add(line);
        }
        
        FileWriter fw = new FileWriter(RESULTS_FILE);
		BufferedWriter bw = new BufferedWriter(fw);
        
        // For each keyword
        for (String k: keywords) {
        	
        	String[] split = k.split(",");
        	String label = split[0];
        	String id = split[1];
        	
        	// Search keyword in the train test
        	KeywordImage trainKeyword = null;
            for (KeywordImage t: trainingImages) {
            	if (t.getID().equals(id)) {
            		// Keyword found
            		trainKeyword = t;
            		break;
            	}
            }
            
            // Perform DTW with every image in the valid set
            List<DTWResult> results = new ArrayList<DTWResult>();
            
            for (KeywordImage v: testImages) {
            	DTW dtw = new DTW();
            	double res = dtw.computeDTW(trainKeyword, v, 10);
            	DTWResult result = new DTWResult(v, res);
            	results.add(result);
            }
            
            Collections.sort(results, new Comparator<DTWResult>() {
                @Override
                public int compare(DTWResult r1, DTWResult r2) {
                    if (r1.getDistance() < r2.getDistance()) return -1;
                    if (r1.getDistance() > r2.getDistance()) return 1;
                    return 0;
                }
            });
            
            // Write the results to the file
            writeToFile(bw, label, results);
        }
        System.out.println("Computation successfully completed.");
        bw.close();
    }

    private static void writeToFile(BufferedWriter bw, String keyword, List<DTWResult> results)
    		throws IOException {
    	String s = keyword;
    	for (DTWResult r: results) {
    		KeywordImage ki = (KeywordImage) r.getImage();
    		s += "," + ki.getID() + "," + (int) r.getDistance();
    	}
    	bw.write(s);
    	bw.newLine();
    }  
}
