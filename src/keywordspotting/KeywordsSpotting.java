package keywordspotting;


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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import javax.imageio.ImageIO;

import utils.Distance;


public class KeywordsSpotting {

   /* private KeywordImage template;
    BufferedImage imageTemplate;
    private int baselineTemplate;
    private String classified = "";
    private int countDescenderTemplate = 0; 
   
    public KeywordImage getTemplate() {return this.template;}
    
    public String getClassified(){return this.classified;}
    
   public ArrayList<String> labels;
   public   int[] votes;
    
   
   private  HashMap<String, ArrayList<KeywordImage>> trainingHashMap = new  HashMap<String, ArrayList<KeywordImage>>();
    private HashMap<String, ArrayList<KeywordImage>> validHashMap = new  HashMap<String, ArrayList<KeywordImage>>();
    private ArrayList<KeywordImage> files = new ArrayList<KeywordImage>();
    
    private BufferedWriter outputWithLabel = null;
    private  ArrayList<KeywordImage> trainingImages;
    private ArrayList<KeywordImage> validImages;
    
   
   private String keywordFile;
   private ArrayList<String> keywords;*/
    
    private String keyword;
    private KeywordImage imageFile; 
    private ArrayList<KeywordImage>  testset = new ArrayList<KeywordImage>();
    
    public KeywordsSpotting(String id, String keyword, File imageFile) throws IOException {
        this.keyword = keyword;
        
        this.imageFile = new KeywordImage(id, keyword, imageFile, 0,0 );
        System.out.println(imageFile.getPath());
        getFeatures(this.imageFile); 
        normFeatures(this.imageFile);

    }
    

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
    

    
    private static void normFeatures( KeywordImage image)
    {
            image.formFeatureArray();
            image.normFeatures();
    }
  

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

class CustomComparator implements Comparator<Distance> {
    @Override
    public int compare(Distance o1, Distance o2) {
        if(o1.getScore() < o2.getScore()) return -1;
        if(o1.getScore() > o2.getScore()) return 1;
        
        return 0; 
    }
}
