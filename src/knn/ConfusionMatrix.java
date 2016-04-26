package knn;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Class to calculate and store a confusion matrix
 * @author user
 *
 */
public class ConfusionMatrix {
    
    ArrayList<Integer> labels;
    int[][] values; 
    private int k;
    public int getK(){return this.k;}
    
    public ConfusionMatrix(int k,  ArrayList<Integer> labels) {
        this.labels = labels; 
        this.values = new int[labels.size()][labels.size()];
        this.k = k; 
        
        for(int  i =0; i < values.length; i++)
        {
            for(int j = 0; j < values.length; j++)
            {
                values[i][j] = 0; 
            }
        }
    }
    
    public void setValue(int row, int col)
    {
        values[row][col] += 1; 
    }
    
    /**
     * Calculate the accuracy for one k 
     * @return
     */
    public double calculateAccuracy()
    {
        double all = 0;
        double correctClassified = 0;
        
        for(int  i =0; i < values.length; i++)
        {
            for(int j = 0; j < values.length; j++)
            {
                if(j==i) correctClassified += values[i][j];
                
                all += values[i][j];
            }
        }
        
        return correctClassified / all; 
    }
    
    /**
     * Write confusion matrix in the output file
     * @param bw
     */
    public void printMatrix(BufferedWriter bw)
    {
        try {
            bw.write("  "); 
            for (int label : labels)
            { 
                    bw.write(String.format("%04d", label) + " ");
            }
            bw.newLine();
            
            for(int  i =0; i < values.length; i++)
            {
                bw.write(labels.get(i) + " ");
                for(int j = 0; j < values.length; j++)
                {
                    bw.write(String.format("%04d", values[i][j]) + " ");
                }
                bw.newLine();
            }
        
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

}
