package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
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

public class MainGaussian {

    public static void main(String[] args) throws IOException {
    	
        String pathTrain = "KeywordSpottingData/task/train.txt";
        String pathValid = "KeywordSpottingData/task/valid.txt";
        
        ArrayList<String> trainFiles = readFile(new File(pathTrain));
        ArrayList<String> validFiles = readFile(new File(pathValid));
        
        ArrayList<KeywordImage> trainingImages =  new  ArrayList<KeywordImage>();
        ArrayList<KeywordImage> validImages =  new ArrayList<KeywordImage>();
        
        File file = new File("KeywordSpottingData/features/scaled-features.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        int cc = 0;
        while ((line = br.readLine()) != null) {
        	
        	String[] values = line.split(",");
        	
        	String[] name = values[0].split(" ");
        	String[] information = name[0].split("-");
            String doc = information[0];
            
            String label = name[1];
            if (label.equals("")) {
            	continue;
            }
            
            
            KeywordImage im = new KeywordImage(label);
            
            // Features of the images
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
            
            if(trainFiles.contains(doc))
            {              
                trainingImages.add(im);  
            }
            else if(validFiles.contains(doc))
            {
                validImages.add(im);
            }
            cc++;
            //System.out.println(cc);
        }
        br.close();
         
        ArrayList<String> keywords = new ArrayList<String>();
        br = new BufferedReader(new FileReader("KeywordSpottingData/task/gau-keywords.txt"));
        line = null;
        
        while((line = br.readLine()) != null)
        {
            keywords.add(line);
        }
        
        System.out.println("Get Features");
        
        
        FileWriter fw = new FileWriter("results/keywordspottingGaussian.txt");
		BufferedWriter bw = new BufferedWriter(fw);
        
        // For each keyword
        for (String k: keywords) {
        	
        	// Search keyword in the train test
        	KeywordImage trainKeyword = null;
            for (KeywordImage t: trainingImages) {
            	if (t.getLabel().equals(k)) {
            		// Keyword found
            		trainKeyword = t;
            		break;
            	}
            }
            
            // Perform DTW with every image in the valid set
            List<DTWResult> results = new ArrayList<DTWResult>();
            
            for (KeywordImage v: validImages) {
            	DTW dtw = new DTW();
            	double res = dtw.computeDTW(trainKeyword, v, 10);
            	// System.out.println(res);
            	
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
            
            writeToFile(bw, k, results);
            
            System.out.println("------------------------------------------------");
            System.out.println("Keyword to be spotted: " + k);
            KeywordImage im = (KeywordImage) results.get(0).getImage();
            KeywordImage im2 = (KeywordImage) results.get(1).getImage();
            KeywordImage im3 = (KeywordImage) results.get(2).getImage();
            KeywordImage im4 = (KeywordImage) results.get(3).getImage();
            KeywordImage im5 = (KeywordImage) results.get(4).getImage();
            System.out.println("Best is: " + im.getLabel());
            System.out.println("2nd is: " + im2.getLabel());
            System.out.println("3rd is: " + im3.getLabel());
            System.out.println("4th is: " + im4.getLabel());
            System.out.println("5th is: " + im5.getLabel());
        }
        
        bw.close();
    }
          
    private static ArrayList<String> readFile(File f)
    {
        ArrayList<String> files = new ArrayList<String>();
        
        try {
            BufferedReader br= new BufferedReader(new FileReader(f));
            String line;
           
            while((line = br.readLine()) != null)
            {
                files.add(line);
            }
            
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return files;   
    }

    private static void writeToFile(BufferedWriter bw, String keyword, List<DTWResult> results)
    		throws IOException {
    	String s = keyword;
    	for (DTWResult r: results) {
    		KeywordImage ki = (KeywordImage) r.getImage();
    		s += ", " + ki.getLabel() + ", " + (int) r.getDistance();
    	}
    	bw.write(s);
    	bw.newLine();
    }  
}