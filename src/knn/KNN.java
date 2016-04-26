package knn;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Class for the KNN-Classifier
 * @author user
 *
 */
public class KNN {
    
    //comparator for the distance object
    static private Comparator<Distance> ascDistance;
    static {
        ascDistance = new Comparator<Distance>(){
            @Override
            public int compare(final Distance lhs,Distance rhs) {       
                if (lhs.getDistance() < rhs.getDistance()) return -1;
                if (lhs.getDistance() > rhs.getDistance()) return 1;
                
                return 0; 
              }
          };
    }
    
    private static final int EUCLIDEAN = 0;
    private static final int MANHATTEN = 1;
    private int[] k;
    private ArrayList<DataExample> trainData;
    private int distanceMeasure;
    
    /**
     * Constructor of the class which expect a array of k's
     * Classifying is done with all the k's
     * @param trainData: data vector to train the classifier
     * @param distanceMeasure: distance measure specification
     * @param k: array of k-values
     */
    public KNN( ArrayList<DataExample> trainData, int distanceMeasure, int[] k){
        this.k = k;  
        Arrays.sort(k); 
        this.trainData = trainData;
        this.distanceMeasure = distanceMeasure; 
    }
    
    /**
     * Constructor of the class which expect one k value
     * Classifying is done with all the k's
     * @param trainData: data vector to train the classifier
     * @param distanceMeasure: distance measure specification
     * @param k: value for k 
     */
    public KNN( ArrayList<DataExample> trainData, int distanceMeasure, int k){
        this.k = new int[1];
        this.k[0] = k; 
        
        this.trainData = trainData;
        this.distanceMeasure = distanceMeasure; 
 
    }
    
    /**
     * Function to classifiy one data items
     * @param vector: Vector of integer values which represent the data item
     * @return an array with the voted classes for every k-value 
     *         the array is sorted by the k-value 
     */
    public int[] classifyData(double[] vector)
    {
        //get the maximum value of k
        int maxK = k[k.length-1];
        
        //Array to save k distances 
        Distance[] distances = new Distance[maxK];
       
        //Get the distance for the vector to the first k train vectors and save it in the distances array
        for (int j = 0; j < maxK; j++)
        {
            double[] trainVector = trainData.get(j).getValues(); 
            double distance = -1;
            switch(this.distanceMeasure)
            {
                case EUCLIDEAN :  distance = Functions.euclideanDistance(vector, trainVector);
                case MANHATTEN : distance = Functions.manhattenDistance(vector, trainVector);
            }
         
            distances[j] = new Distance(trainData.get(j), distance);
        }
         
        //sort the array in ascending order
        //on the first spot we have not the most similiar object
        Arrays.sort(distances, ascDistance);

        //Calculate distances for the rest of the trainining samples
        for (int j = maxK; j < trainData.size(); j++)
        {
            DataExample x = trainData.get(j);
            double[] trainVector = x.getValues(); 
            double distance = -1;
            
            switch(this.distanceMeasure)
            {
                case EUCLIDEAN :  distance = Functions.euclideanDistance(vector, trainVector);
                case MANHATTEN : distance = Functions.manhattenDistance(vector, trainVector);
            }
           
           // if the new distance is smaller than the last on in the array
            // put this one in the array and sort the array new
            if (distance < distances[maxK-1].getDistance())
            {
                distances[maxK-1].setDistance( distance);
                distances[maxK-1].setDistanceTo(x);  
                Arrays.sort(distances, ascDistance);
            }
            
        }
        
        //now we have in distances the k-most similiar training samples
        //a vote gives us the class for our vector 
        //for every k in our array we do one vote
        int[] votesForK = new int[k.length]; 
        for (int i = 0; i < votesForK.length; i++)
        {
            int kValue = k[i];
            votesForK[i] = vote(Arrays.copyOfRange(distances, 0, kValue));
        }
        
        return votesForK; 
        
       
    }
    
    private static int vote(Distance[] distances)
    {
        int[] votes = {0,0,0,0,0,0,0,0,0,0};
        int labelMax = distances[0].getDistanceTo().getLabel(); 
        int countMax = 1; 
        votes[distances[0].getDistanceTo().getLabel()] += 1;

        for (int i = 1; i < distances.length; i++)
        {        
            int idx = distances[i].getDistanceTo().getLabel();
            votes[idx] += 1; 
            if (votes[idx] > countMax)
            {
                labelMax = distances[i].getDistanceTo().getLabel();
                countMax += 1; 
            }
        }

        
        return labelMax; 
    }
    
    

   
    
    

}
