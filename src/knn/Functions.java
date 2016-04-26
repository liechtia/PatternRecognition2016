package knn;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Class contains a collection of function which were needed for the KNN and KMEANS programs
 * @author user
 *
 */
public class Functions {

    /**
     * Function to calculate the euclidean distance between two vectors 
     * @param vec1
     * @param vec2
     * @return
     */
    public static double euclideanDistance(double[] vec1, double[] vec2)
    {
        if(vec1.length != vec2.length)
        {
            System.out.println("Vectors have to have the same length!");
            return -1;
        }
        
        double sum = 0;
        for( int  i = 0; i < vec1.length; i++)
        {
            sum += (vec1[i]-vec2[i])*(vec1[i]-vec2[i]);
        }
        
        return sum;
        

    }
    
    
    /**
     * Function to read the csv file
     * @param file
     * @return
     * @throws FileNotFoundException
     */
    public static ArrayList<DataExample> readData(String file) throws FileNotFoundException
    {
      //Get scanner instance
        ArrayList<DataExample> vectors = new ArrayList<DataExample>(); 
        
         
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        try {
            line = br.readLine();
           
            while (line != null) {
                String[] s = line.split(",");
                double[] vector = new double[s.length];
                for (int i = 1; i < s.length; i++)
                {
                    vector[i] = Double.parseDouble(s[i]);
                  
                }
                DataExample data = new DataExample(Integer.parseInt(s[0]), vector);
                vectors.add(data);
           
                line = br.readLine();
            }
            
            br.close();
            
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return vectors;

       
    }
    
    
    /**
     * Calculates the manhatten distance between to vectors
     * @param vec1
     * @param vec2
     * @return the distance
     */
    public static int manhattenDistance(double[] vec1, double[] vec2)
    {

        if(vec1.length != vec2.length)
        {
            System.out.println("Vectors have to have the same length!");
            return -1;
        }
        
        int sum = 0;
        for( int  i = 0; i < vec1.length; i++)
        {
            if (vec1[i] > vec2[i]) sum += vec1[i]-vec2[i];
            else sum += vec2[i]- vec1[i];
        }
        
        return sum;
    }
    

    

  

}
