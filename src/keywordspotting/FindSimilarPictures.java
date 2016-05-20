package keywordspotting;

import java.util.ArrayList;
import java.util.Collections;

import utils.Distance;

public class FindSimilarPictures {


    private ArrayList<KeywordImage> validImages;
    
    public FindSimilarPictures(ArrayList<KeywordImage> validImages) {
       this.validImages = validImages;
    }
    
    public ArrayList<Distance> findSimilarPictures(double treshold, ArrayList<KeywordImage> trainingImages)
    {
        ArrayList<Distance> results = new ArrayList<Distance>();
        for(int i =0; i < validImages.size(); i++)
        {
            for(int j = 0; j < trainingImages.size(); j++)
            {
                boolean ratioArea = Pruning.getRatioOfArea(validImages.get(i).getImage(), trainingImages.get(j).getImage(), 2.0);
                boolean ratioAspect =  Pruning.getRatioOfAspect(validImages.get(i).getImage(), trainingImages.get(j).getImage(), 1.5);
                
                if(!ratioArea || !ratioAspect)
                {
                    continue;
                }
                    
                
              double distance = DTW.computeDTW(validImages.get(i).array, trainingImages.get(j).array, 10);                    
               Distance re = new Distance(trainingImages.get(j), distance);
               results.add(re);
            }
        }
    
        Collections.sort(results,  new CustomComparator());
           
        ArrayList<Distance> list = new ArrayList<Distance>();
        for(int i = 0; i < results.size(); i++)
        {
            if(results.get(i).getScore() <= treshold)
            {

                list.add(results.get(i));
            }
            else
            {
                break;
            }
        }
        
        return list;
    }

       
}
