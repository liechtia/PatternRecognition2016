package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;

import ks.datahandling.Image;

public class MainKeywordSpotting {

    public static void main(String[] args) {
        // TODO Auto-generated constructor stub
        File folderImages = new File("KeywordSpottingData/wordimages/clipped_skew/");
        String pathTrain = "KeywordSpottingData/task/train.txt";
        String pathValid = "KeywordSpottingData/task/valid.txt";
        
        ArrayList<String> trainFiles = readFile(new File(pathTrain));
        ArrayList<String> validFiles = readFile(new File(pathValid));
        
        HashMap<String, ArrayList<Image>> trainingImages =  new HashMap<String, ArrayList<Image>>();
        HashMap<String, ArrayList<Image>> validImages =  new HashMap<String, ArrayList<Image>>();
        
        File[] images = folderImages.listFiles();
        
        for(File imageFile : images)
        {
            String[] name = imageFile.getName().split(" ");
            String[] information = name[0].split("-");
            String doc = information[0];
            int line = Integer.parseInt(information[0]);
            int wordInLine = Integer.parseInt(information[1]);
            
            int  idx = name[1].lastIndexOf(".");
            
            String label = name[1].substring(0, idx);
            
            System.out.println("Doc: " + doc + " Label: " + label); 
            
            //Calculate features for the images
            Image im = new Image(label, imageFile, line, wordInLine);
            im.slidingWindow();
            
            //according to their doc number add the image to the hash map 
            if(trainFiles.contains(doc))
            {
               addImage(trainingImages, label, im);
            }
            else if(validFiles.contains(doc))
            {
                addImage(validImages, label, im);
            }

        }
       

    }
    
    private static void addImage(HashMap<String, ArrayList<Image>> list, String label, Image im)
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
