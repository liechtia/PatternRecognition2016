package utils;
import java.util.Arrays;
import java.util.Collections;


public class Histogram {

    private final int peeks = 5;
    
    private double angle;
    private int[] histo;
    private int imageHeight;
    
    public Histogram(double angle, int[] histo, int imageHeight) {
        this.angle = angle;
        this.histo = histo;
        this.imageHeight = imageHeight; 
    }
    
    public double getAveragePeekHeight()
    {
        Integer[] peekHeights = new Integer[peeks];
        for(int i = 0; i < peekHeights.length; i++)
            peekHeights[i] = 0; 
        
        Arrays.sort(peekHeights, Collections.reverseOrder());

        for(int i = 0; i< histo.length; i++)
        {
            if(histo[i] > peekHeights[peeks-1] && histo[i] < imageHeight)
            {
                peekHeights[peeks-1] = histo[i];
                
                Arrays.sort(peekHeights, Collections.reverseOrder());       
                
     
            }
                
        }
        
        int sum = 0;
        for(int j = 0; j < peekHeights.length; j++)
            sum += peekHeights[j];
        
        
        return sum/peeks; 
    }

}
