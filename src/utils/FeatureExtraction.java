package utils;

import java.util.Arrays;

/**
 * Class to extract featues
 *
 */
public class FeatureExtraction{

       /**
        * Function to extract features from an image 
        * Use the number of black (grey) pixel in every column and every height
        * This reduced the number of features from length x height to length+height 
        * @param length
        * @param height
        * @param values
        * @return
        */
    public static int[] extracImageFeatures(int length, int height, int[]values)
    {
        int[] histogram = new int[length+height];
   
        int blackPixels = 0;
            
        for(int i = 0; i < length; i++)
        {
            for(int j = 0; j < height; j++)
            {
                if(values[i+j*length] != 0)
                {         
                    blackPixels += 1;
                }
            }
            histogram[i] = blackPixels;
            blackPixels = 0;
        }
        
        for(int i = 0; i < height; i++)
        {
            for(int j = 0; j < length; j++)
            {
                if(values[i*length+j] != 0)
                {         
                    blackPixels += 1;
                }
            }
            histogram[length+i] = blackPixels;
            blackPixels = 0;
        }
        

           
         return histogram;
      
    }

}
