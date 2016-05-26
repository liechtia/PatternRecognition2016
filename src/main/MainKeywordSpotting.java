package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import utils.Distance;
import keywordspotting.KeywordsSpotting;

/**
 * Main class for the Keywordspotting task
 *
 */
public class MainKeywordSpotting {

    private final static String trainingImages = "KeywordSpottingData/wordimages/final_clipped";
    private final static String keywordsFile = "KeywordSpottingData_Test/task/keywords.txt";
    private final static String keywordsImages ="KeywordSpottingData_Test/wordimages/final_clipped"; 
    private final static String resultsFile ="results/keywordspotting.txt"; 
    private static BufferedWriter output = null;
    
    public static void main(String[] args) throws IOException {
        
        //File to write the results
        File file = new File(resultsFile);
        // if file doesnt exists, then create it
        if (!file.exists()) {
            file.createNewFile();
        }

        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        output = new BufferedWriter(fw);
        
        //Read the given keywords
        ArrayList<String> keywords = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new FileReader(keywordsFile));
        String line = null;
        
        while((line = br.readLine()) != null)
        {
            keywords.add(line); 
        }
        
        br.close();
        
        //for each keyword get the image and find all images int he validation set
        for(String keywordLine : keywords)
        {
            String id = keywordLine.split(",")[1];
            String k = keywordLine.split(",")[0];
            KeywordsSpotting ks = new KeywordsSpotting(id, k,  new File(trainingImages+ "/" + id+ " " + k + ".png"));
            ks.getTestKeywordImages(keywordsImages);
            ArrayList<Distance> d = ks.spotKeywords();
            writeResult(k, output, d);
        }
        output.close();

    }

    
    private static void writeResult(String keyword, BufferedWriter output, ArrayList<Distance> distances)
    {
        Distance d;
        double score;
        try {
            output.write(keyword + ", ");
            for(int i = 0; i < distances.size(); i++)
            {
                d= distances.get(i);
                score = Math.round(d.getScore()*100)/100.0d;
                output.write(d.getImage().getID() + ", " + score+", "); 
            }
            
            d=distances.get(distances.size()-1);
            score = Math.round(d.getScore()*100)/100.0d;
            output.write(d.getImage().getID() + ", " + score);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
   /* private double[] evaluate(String test, ArrayList<Distance> list)
    {
        int tp = 0;
        int fp = 0;
        int fn = 0;
        
        double prec;
        double recall; 
        
        if(!trainingHashMap.containsKey(test)) //ground truth = 0
        {
            if(list.size() == 0)
            {
                prec = 1;
                recall = 1;
            }
            else 
            {
                prec = 0;
                recall  = 0; 
            }   
        }
        else if(list.size() == 0)
        {
            recall = 0;
            prec = 0; 
        }
        else
        {
            for(Distance d : list)
            {
               if( d.getImage().getLabel().equals(test))
                {
                    tp++;
                }
               else
               {
                   fp++;
               }
            }
            
            fn = trainingHashMap.get(test).size() - tp; 
            recall = (tp /(tp+fn+0.0));
            prec = (tp /(fp+tp+0.0));
        }
        
        double[] re = {recall, prec};
        return re;
        
       
    }*/
   
    



}

