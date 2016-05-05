package main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import keywordspotting.KeywordImage;
import keywordspotting.Preprocessing;

public class MainPreprocessing {

    private static final String folderClipped = "KeywordSpottingData/wordimages/clipped/";
    private static final String folderClippedSave = "KeywordSpottingData/wordimages/final_clipped/";
    
    public static void main(String[] args) throws IOException {
        File folderImages = new File(folderClipped);
        File[] images = folderImages.listFiles();
        
        int tresholdBackground = 170;
        double alphaMin = -45;
        double alphaMax = 60; 
        double intervall = 5;
        double[] angles= new double[(int) ((Math.abs(alphaMin)+Math.abs(alphaMax))/intervall+1)];
        
        for(int i = 0; i < angles.length; i++)
        {
            angles[i] = alphaMin +(intervall*i); 
        }
        Preprocessing pr = new Preprocessing(folderClippedSave);
        int i=0;
        for(File imageFile : images)
        {
            if(!imageFile.getName().contains(".png"))
                continue;
    
            KeywordImage keywordimage = new KeywordImage(imageFile.getName(), imageFile, 0, 0);
            
            BufferedImage image = ImageIO.read(imageFile);
            keywordimage.setImage(image);
            pr.addImage(keywordimage);
            i += 1;
        }
        
        System.out.println("Binarize");
        pr.binarizeImages(tresholdBackground);
        
       System.out.println("Skew Correction");
        pr.correctSkewImages();
        
    
        
        System.out.println("Slant Correction");
        pr.correctSlantImages(angles);
        
        System.out.println("Cut columns at beginning");
        pr.cutColumnsImages();
   
      //  pr.getComponentsImages();
        
        // System.out.println("Scale vertical");
        // pr.scaleVerticalImages();
        
 
        
        //System.out.println("Save preprocessed Images");
        pr.writeImages();
    }
 }
