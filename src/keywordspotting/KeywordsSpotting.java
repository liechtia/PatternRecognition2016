package keywordspotting;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import utils.Distance;
import utils.FeatureVector;
import utils.FtVector;

public class KeywordsSpotting {

    private KeywordImage template;
    BufferedImage imageTemplate;
    private int baselineTemplate;
    private String classified = "";
    private int countDescenderTemplate = 0; 
   
    public KeywordImage getTemplate() {return this.template;}
    
    public String getClassified(){return this.classified;}
    
   public ArrayList<String> labels;
   public   int[] votes;
    
    private ArrayList<KeywordImage> files = new ArrayList<KeywordImage>();
    
    public KeywordsSpotting(KeywordImage template) {
       this.template = template;
       this.imageTemplate = template.getImage();

       baselineTemplate  = Pruning.getLowerBaseLine(imageTemplate);
       countDescenderTemplate = Pruning.countDescenders(template.getImage(), baselineTemplate);
       
    }
    
    /**
     * Run dtw with the test image and every image from the train set
     * Classifiy the image than with 5-knn 
     * 
     * TODO:
     * normalize the width?
     * align lowerbaseline of the images 
     */
    public void runDtw()
    {
          ArrayList<Distance> distances = new ArrayList<Distance>();     
         for(KeywordImage trainImage : files)
          {
             KeywordImage test = new KeywordImage(template.getLabel(), template.getFile(), 0,0);
             KeywordImage train = new KeywordImage(trainImage.getLabel(), trainImage.getFile(), 0,0);

             train.setImage(imageTemplate);
             test.setImage(trainImage.getImage());
              
              getFeatures(test);
              getFeatures(train);
              
             
              double score = DTW.computeDTW(train, test, 10);
                 
              Distance c = new Distance(trainImage);
                 c.addScore(score);
                 distances.add(c);
           }
          classified = vote(distances);
    }
    
    private void getFeatures(KeywordImage img)
    {
        img.slidingWindow(); 
        
       for(FtVector fv : img.getFeatureVectors())
        {
            fv.normFeatures();
        }
    }

    /**
     * Check the distances 
     * The five best candidates vote which word this image represents
     * @param distances
     * @return
     */
    private  String vote(ArrayList<Distance> distances)
    {
         Collections.sort(distances, new CustomComparator());
        labels = new  ArrayList<String>();
        votes = new int[5];
       
        int idxMaxVote = -1;
        int maxVote = 0;
        
        for(int i = 0; i <5;i++)
        {
            if(!labels.contains(distances.get(i).getImage().getLabel()))
            {
                int idx = labels.size();
                labels.add(distances.get(i).getImage().getLabel());
                votes[idx]=0;   
            }
        
            votes[labels.indexOf(distances.get(i).getImage().getLabel())]++; 
            
            if(votes[labels.indexOf(distances.get(i).getImage().getLabel())] > maxVote)
            {
                maxVote = votes[labels.indexOf(distances.get(i).getImage().getLabel())];
                idxMaxVote = labels.indexOf(distances.get(i).getImage().getLabel());
            }
    }
    
    return labels.get(idxMaxVote);
}
    
    /**
     * Delete all images which are probably not similiar to the  test image 
     * The deleting is done with:
     * ratio of the area
     * ratio of the aspect (width/length)

     * 
     * TODO:
     * get rid of comma, semikolon ect in the images...
     * @param images
     * @return
     */
    public int pruneImages(ArrayList<KeywordImage> images)
    {
        files = new ArrayList<KeywordImage>();
        int wrong = 0;
        for(KeywordImage imageFile : images)
        {
            files.add(imageFile);
           
           BufferedImage image = imageFile.getImage();
         
           
           boolean ratioArea = Pruning.getRatioOfArea(imageTemplate, image, 2.0);
           boolean ratioAspect =  Pruning.getRatioOfAspect(imageTemplate, image, 1.5);
           
           boolean prune = false;
           
           if(!ratioArea)
           {
               prune = true;
           }
           
           if(!ratioAspect)
               prune = true;
           
           int descenders = 0;
           if(!prune)
           {
               descenders = Pruning.countDescenders(image, baselineTemplate);

                   if(this.countDescenderTemplate != descenders)
                   {
                    prune =true;    
                   }
               
           }
           
           if(prune)
           {
               if(imageFile.getLabel().contains(this.template.getLabel()))
               {
                   wrong++;

               }
           }
           else
           {
               files.add(imageFile);
           }
        }
        
        return wrong;
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
