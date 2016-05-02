package main;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.imageio.ImageIO;

import keywordspotting.KeywordImage;
import keywordspotting.KeywordsSpotting;

public class MainKeywordSpotting {

    public static void main(String[] args) throws IOException {
        // TODO Auto-generated constructor stub
        File folderImages = new File("KeywordSpottingData/wordimages/final_clipped/");
        String pathTrain = "KeywordSpottingData/task/train.txt";
        String pathValid = "KeywordSpottingData/task/valid.txt";
        
        ArrayList<String> trainFiles = readFile(new File(pathTrain));
        ArrayList<String> validFiles = readFile(new File(pathValid));
        
        ArrayList<KeywordImage> trainingImages =  new  ArrayList<KeywordImage>();
        ArrayList<KeywordImage> validImages =  new ArrayList<KeywordImage>();
        
        HashMap<String, ArrayList<KeywordImage>> trainingHashMap = new  HashMap<String, ArrayList<KeywordImage>>();
        HashMap<String, ArrayList<KeywordImage>> validHashMap = new  HashMap<String, ArrayList<KeywordImage>>();
        
        File[] images = folderImages.listFiles();
        
        System.out.println("Read files");
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
        
        ArrayList<String> keywords = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new FileReader("KeywordSpottingData/task/keywords.txt"));
        String line = null;
        
        while((line = br.readLine()) != null)
        {
            keywords.add(line.toLowerCase()); 
        }
        
        System.out.println("Get Features");
        calculateFeatures(validImages);
        calculateFeatures(trainingImages);

        ArrayList<KeywordsSpotting> classifications = new ArrayList<KeywordsSpotting>();

        int count = 0;
        int correct = 0;
        for(KeywordImage test : validImages)
        {
            //just classifiy keywords
            if(!keywords.contains(test.getLabel()))
            {
                continue;
            }
            
            //just classifiy keywords where we know that there are images in the validation set
            if(!validHashMap.containsKey(test.getLabel()))
                continue;
            
            System.out.println(test.getFile().getName());
            KeywordsSpotting ks = new KeywordsSpotting(test);
            ks.pruneImages(trainingImages);
            ks.runDtw();
            
            if(ks.getClassified().equals(ks.getTemplate().getLabel()))
            {
                correct++;
            }
            count++;

            classifications.add(ks); 
       
        }
        
        System.out.println(correct + " " + count);
        
        //write the results
        BufferedWriter output = null;
        File file = new File("example.txt");
        output = new BufferedWriter(new FileWriter(file));
         
        for(KeywordsSpotting k : classifications)
        {
            output.write(k.getTemplate().getLabel() + " " + k.getTemplate().getFile().getName());
            output.newLine();
            output.write("Classified as: " + k.getClassified());
            
            for(int i = 0; i < k.labels.size(); i++)
            {
                output.newLine();
                output.write(k.labels.get(i) + " "+ k.votes[i]);
            }
            output.newLine();
        }
        
        output.close();
        

    }
    
    private static void calculateFeatures( ArrayList<KeywordImage> images)
    {
        for(KeywordImage im : images)
        {
            getFeatures(im);
        }
    }
  

    private static void getFeatures(KeywordImage im)
    {
        BufferedImage image;
        try {
            image = ImageIO.read(im.getFile());    
            im.setImage(image);

        } catch (IOException e) {
         
            e.printStackTrace();
        }
    
    }
    private static void addImage(HashMap<String, ArrayList<KeywordImage>> list, String label, KeywordImage im)
    {
        if(list.containsKey(label))
        {
            list.get(label).add(im);
        }
        else
        {
            list.put(label, new ArrayList<>(Arrays.asList(im)));
        }
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
    



}
