package old;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import keywordspotting.KeywordImage;

public class MainSlantCorrection {

    public static void main(String[] args) {
        // TODO Auto-generated constructor stub
        File folderImages = new File("KeywordSpottingData/wordimages/clipped_skew/");
        String outputpath = "KeywordSpottingData/wordimages/clipped_slant/";
        

        File[] images = folderImages.listFiles();
        
        double alphaMin = -45;
        double alphaMax = 45; 
        double intervall = 5;
        double[] angles= new double[(int) ((Math.abs(alphaMin)+Math.abs(alphaMax))/intervall+1)];
        
        for(int i = 0; i < angles.length; i++)
        {
            angles[i] = alphaMin +(intervall*i); 
        }

        for(File imageFile : images)
        {
            if(!imageFile.getName().contains(".png"))
                continue;
            
            System.out.println(imageFile.getName());
            SlantCorrection sc = new SlantCorrection(imageFile, outputpath);
            sc.slant(angles);
        }
    }
}
