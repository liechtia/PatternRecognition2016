package utils;

import java.util.Arrays;

/**
 * Class to extract featues
 *
 */
public class FeatureExtraction{

       /**
        * Function to extract features from an image 
        * @param length
        * @param height
        * @param values
        * @return
        */
    public static double[] extracImageFeatures(int length, int height, double[]values)
    {
        double[] col;
        double[] reducedValues = new double[4*length];
        int k = 0;
        for(int i = 0; i < length; i++)
        {
            col = new double[height];
            for(int j = 0; j < height; j++)
            {
                col[j] = values[i+j*4];
            }
            
            reducedValues[k] = getAverageGreyValue(col);
            reducedValues[k+1] = UpperMostPixel(col);
            reducedValues[k+2] = LowerMostPixel(col);
            reducedValues[k+3] = numberBWTransitions(col);
            k += 4;
            
        }         
         return reducedValues;
      
    }

    /**
     * Calculate the average grey value 
     * @param vector
     * @return
     */
    private  static double getAverageGreyValue(double [] vector)
    {
        double sum  = 0.0;
        for(int i = 0; i < vector.length; i++)
        {
            sum+= vector[i];
        }
        
        return sum/vector.length; 
    }
    
    /**
     * Calculate the upper most pixel
     * @param vector
     * @return
     */
    private static double UpperMostPixel(double[] vector)
    {
        double pixel = 0;
        int i = 0;
        while(pixel == 0 && i < vector.length)
        {
            pixel = vector[i];
            i++;
        }
        
        if(pixel == 0) return 0;
        else return i; 
    }
    
    /**
     * Calculate the lowerst pixel in a column
     * @param vector
     * @return
     */
    private static double LowerMostPixel(double [] vector)
    {
        double pixel = 0;
        int i = vector.length-1;
        while(pixel == 0 && i > 0)
        {
            pixel = vector[i];  
            i--;
        }
        
        return i; 
    }
    
    /**
     * Calculate the number of black and white transistions
     * @param vector
     * @return
     */
    private static double numberBWTransitions(double[] vector)
    {
        int transitions = 0; 
        boolean white = false;
        
        if(vector[0] == 0) white=true;
        else white = false; 
        
        
        
        for(int i = 0; i < vector.length; i++)
        {
            if(vector[i] == 0 && !white)
            {
                transitions += 1;
                white = true;
            }
            
            if(vector[i] != 0 && white)
            {
                transitions += 1;
                white = false;
            }
           
        }
        
        return transitions; 
    }


}
