package main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import keywordspotting.KeywordImage;
import keywordspotting.Preprocessing;

/**
 * Class to preprocess the images 
 * @author admin
 *
 */
public class MainPreprocessing {

    private static final String folder ="KeywordSpottingData_Test";
    private static final String folderClipped = folder + "/wordimages/clipped/";
    private static final String folderClippedSave = folder + "/wordimages/final_clipped/";
    
    //treshold for when a grey pixel should be converted to a black pixel
    private static final  int tresholdBackground = 150;
    
    //angles to be tested for the slant correction
    private static final  double alphaMin = -90;
    private static final double alphaMax = 90; 
    private static final double intervall = 5;
    
    public static void main(String[] args) throws IOException {
        File folderImages = new File(folderClipped);
        File[] images = folderImages.listFiles();
        
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
    
            KeywordImage keywordimage = new KeywordImage("", imageFile.getName(), imageFile, 0, 0);
            
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
  
        
        System.out.println("Scale vertical");
        pr.scaleVerticalImages();

        System.out.println("Save preprocessed Images");
        pr.writeImages();
    }
 }
