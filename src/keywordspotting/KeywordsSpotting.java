package keywordspotting;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.imageio.ImageIO;

import utils.Distance;

/**
 * Class to spot keyword images for a template image
 *
 */
public class KeywordsSpotting {    
    private String keyword;
    private KeywordImage imageFile; 
    private ArrayList<KeywordImage>  testset = new ArrayList<KeywordImage>();
    
    public String getKeyword() { return this.keyword;}
    
    public KeywordsSpotting(String id, String keyword, File imageFile) throws IOException {
        this.keyword = keyword;
        
        this.imageFile = new KeywordImage(id, keyword, imageFile, 0,0 );
        System.out.println(imageFile.getPath());
        getFeatures(this.imageFile); 
        normFeatures(this.imageFile);

    }
    
    /**
     * Read the files for the images
     * @param testsetPath
     */
    public void getTestKeywordImages(String testsetPath)
    {
       
        File[] images = (new File(testsetPath)).listFiles();

        for(File imageFile : images)
        { 
           if(!imageFile.getName().contains(".png")) continue; 

            String[] name = imageFile.getName().split(" ");
            String id = "";
            String label = "";

            
            if (name.length > 1)
            {
                int  idx = name[1].lastIndexOf(".");        
                label = name[1].substring(0, idx).toLowerCase();
                id = name[0];
            }
            else
            {
                int  idx = name[0].lastIndexOf(".");        
                id = name[0].substring(0, idx).toLowerCase();
            }
            
            if(label.lastIndexOf("-s_") >0 )
            {
                label = label.substring(0, label.lastIndexOf("-s_"));
            }
            

            KeywordImage ki = new KeywordImage(id, label, imageFile, 0,0);
            getFeatures(ki);
            normFeatures(ki);
            testset.add(ki);
        }
           
    }
    
    /**
     * Spot the keywords
     * The distance between the template and all other images in the test set is calculated and sorted in 
     * descending order
     * @return
     */
    public ArrayList<Distance> spotKeywords()
    {
        ArrayList<Distance> results = new ArrayList<Distance>();
        
        for(int j = 0; j < testset.size(); j++)
         {
            double distance;

                    distance = DTW.computeDTW(imageFile.array, testset.get(j).array, 10);
                
                
                                 
               Distance re = new Distance(testset.get(j), distance);
               results.add(re);
            }
        
    
        Collections.sort(results,  new CustomComparator());
        
        return results; 
        
    }
    

    /**
     * Norm the feature vectors
     * @param image
     */
    private static void normFeatures( KeywordImage image)
    {
            image.formFeatureArray();
            image.normFeatures();
    }
  

    /**
     * Calculate the feature vectors
     * @param im
     */
    private static void getFeatures(KeywordImage im)
    {
      
        BufferedImage image;
        try {
            image = ImageIO.read(im.getFile());    
            im.setImage(image);
            im.slidingWindow();

        } catch (IOException e) {
         
            e.printStackTrace();
        }
    
    }
}

class CustomComparator implements Comparator<Distance> {
    @Override
    public int compare(Distance o1, Distance o2) {
        if(o1.getScore() < o2.getScore()) return -1;
        if(o1.getScore() > o2.getScore()) return 1;
        
        return 0; 
    }
}
