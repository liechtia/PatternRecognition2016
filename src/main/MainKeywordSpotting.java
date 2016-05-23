package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import utils.Distance;
import keywordspotting.KeywordsSpotting;

public class MainKeywordSpotting {

    private final static String trainingImages = "KeywordSpottingData/wordimages/final_clipped";
    private final static String keywordsFile = "KeywordSpottingData_Test/task/keywords.txt";
    private final static String keywordsImages ="KeywordSpottingData_Test/wordimages/final_clipped"; 
    private final static String resultsFile ="results/keywordspotting.txt"; 
    private static BufferedWriter output = null;
    
    public static void main(String[] args) throws IOException {
        

        File file = new File(resultsFile);

        // if file doesnt exists, then create it
        if (!file.exists()) {
            file.createNewFile();
        }

        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        output = new BufferedWriter(fw);
 
        
        ArrayList<String> keywords = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new FileReader(keywordsFile));
        String line = null;
        
        while((line = br.readLine()) != null)
        {
            keywords.add(line); 
        }
        
        double acc = 0;
        for(String keywordLine : keywords)
        {
            String id = keywordLine.split(",")[1];
            String k = keywordLine.split(",")[0];
            KeywordsSpotting ks = new KeywordsSpotting(id, k,  new File(trainingImages+ "/" + id+ " " + k + ".png"));
            ks.getTestKeywordImages(keywordsImages);
            ArrayList<Distance> d = ks.spotKeywords();
            writeResult(k, output, d);

            //copyFiles(k, d);
          
        }
        output.close();

    }
    
    private static void copyFiles(String keyword, ArrayList<Distance> distances) throws IOException
    {
        File dir = new File("results/images/" +keyword);
        dir.mkdir();
        
        for(int i = 0; i < 10; i++)
        {
            Distance d= distances.get(i);
            String name = i + "_" + d.getImage().getID();
            Files.copy(d.getImage().getFile().toPath(), (new File(dir+"/"+name)).toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
     
    }
    
    private static double evaluate(String keyword, ArrayList<Distance> distances)
    {
        int countAll = 0;
        int correct = 0;
        int j = 0;
        for(Distance d: distances)
        {            
            if(keyword.toLowerCase().contains(d.getImage().getLabel()))
            {
                countAll++;
                System.out.print(j + " ");
            }
            
            j++;
             
        }
        
        System.out.println();
        System.out.println(countAll);
        for(int i = 0; i < countAll; i++)
        {
            if(keyword.toLowerCase().contains(distances.get(i).getImage().getLabel()))
            {
                correct++;
            }
        }
        
        double acc = (correct) /(countAll+0.0);
        return acc; 
    }
    
   /* private void readData() throws IOException
    {
       
        File folderImages = new File("KeywordSpottingData/wordimages/final_clipped/");
        String pathTrain = "KeywordSpottingData/task/train.txt";
        String pathValid = "KeywordSpottingData/task/valid.txt";
        
      
        
        File file = new File("results/keywordspotting.txt");
        outputWithId = new BufferedWriter(new FileWriter(file));
        
        File file2 = new File("results/keywordspottingWithLabel.txt");
        outputWithLabel = new BufferedWriter(new FileWriter(file2));
        
        ArrayList<String> trainFiles = readFile(new File(pathTrain));
        ArrayList<String> validFiles = readFile(new File(pathValid));
        
        trainingImages =  new  ArrayList<KeywordImage>();
        validImages =  new ArrayList<KeywordImage>();
      
        
        File[] images = folderImages.listFiles();

        for(File imageFile : images)
        {     
            if(!imageFile.getName().contains(".png")) continue; 
            
            String[] name = imageFile.getName().split(" ");
            String[] information = name[0].split("-");
            String doc = information[0];
            int line = Integer.parseInt(information[0]);
            int wordInLine = Integer.parseInt(information[1]);
            
            int  idx = name[1].lastIndexOf(".");
            
            String label = name[1].substring(0, idx).toLowerCase();
            if(label.lastIndexOf("-s_") >0 )
            {
                label = label.substring(0, label.lastIndexOf("-s_"));
            }
            if(label.equals(""))
                continue; 
            
            //Calculate features for the images
            KeywordImage im = new KeywordImage(label, imageFile, line, wordInLine);
            //according to their doc number add the image to the hash map 
            if(trainFiles.contains(doc))
            {              
                trainingImages.add(im);  
                addImage(trainingHashMap, label, im); 
            }
            else if(validFiles.contains(doc))
            {
                validImages.add(im);
                addImage(validHashMap, label, im); 
            }

        }
        
        keywords = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new FileReader(keywordFile));
        String line = null;
        
        while((line = br.readLine()) != null)
        {
            keywords.add(line.toLowerCase()); 
        }
        
    }*/
        
   /* private void spotKeywords()
    {
      
        double[] avRecall = new double[treshold.length];
        double[] avPrec = new double[treshold.length];
        
        for(KeywordImage i : trainingImages)
        {
            getFeatures(i); 
        }
        normFeatures(trainingImages); 
        
        for(KeywordImage i : validImages)
        {
            getFeatures(i);
        }
        normFeatures(validImages);
        
        for(int k = 0; k < treshold.length; k++)
        {
            double t = treshold[k];
            System.out.println(t); 
            double recall = 0;
            double prec = 0; 
           int x = 0;
           
           for(String keyword : keywords)
           {
               x++;
               if(!validHashMap.containsKey(keyword))
                   {
                       continue;
                   }

                      System.out.println(keyword);
                      FindSimilarPictures similar = new FindSimilarPictures(validHashMap.get(keyword));
                      ArrayList<Distance> d = similar.findSimilarPictures(t, trainingImages);
                      
                  
                      
                   double[] eval = this.evaluate(keyword, d);
                   writeResult(outputWithId, outputWithLabel, keyword, d);
                   recall += eval[0];
                    prec += eval[1];       
               
           }
            
         
            avRecall[k] = recall / (x+0.0);
            avPrec[k] = prec / (x+0.0); 
            double f= 2*((avRecall[k]*avPrec[k])/(avRecall[k]+avPrec[k]));
            
            System.out.println("Recall: " + avRecall[k]);
            System.out.println("Prec: " + avPrec[k]);
            System.out.println("F-Measure: " + f);

            
        }
        
        
       
    }*/
    
    
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

