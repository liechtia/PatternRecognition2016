package knn;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import knn.DataExample;

/**
 * Class to test the implementation of the KNN class
 * @author Maria Schmidt
 *
 */
public class TestKNN {

    private static final int EUCLIDEAN = 0;
    private static final int MANHATTEN = 1;
    
    //k-Values which are tested by default
    private static int[] k = {1,3,5,10};


    public static void main(String[] args) {
       
        String trainFile = "";
        String testFile = "";
        
        if (args.length < 2)
        {
            System.out.println("Parameters are missing!");
            System.out.println("Call: java TestKNN pathToTrainFile pathToTestFile");
            System.exit(-1);
        }
        
        trainFile = args[0];
        testFile = args[1];
        
        if(!new File(trainFile).exists())
        {
            System.out.println("File " + trainFile + " not found!");
            System.exit(-1); 
        }
        
        if(!new File(testFile).exists())
        {
            System.out.println("File " + testFile + " not found!");
            System.exit(-1); 
        }
        
        if(!trainFile.substring(trainFile.indexOf(".")).equals("csv") )
        {
            System.out.println("Wrong fileformat! - csv file expected!");
            System.exit(-1);
        }
        
        if(!testFile.substring(trainFile.indexOf(".")).equals("csv") )
        {
            System.out.println("Wrong fileformat! - csv file expected!");
            System.exit(-1);
        }
        
        
        if (args.length > 2)
        {
            k = new int[args.length-2];
            for(int i = 0; i < k.length; i++)
            {
                try{
                    k[i] = Integer.parseInt(args[i+2]);
                }catch(NumberFormatException e)
                {
                    System.out.println("Just integer value for k are permitted!");
                    System.exit(-1);
                }
               
            }
        }
        
        System.out.println("########### K-Values which are used: " + Arrays.toString(k) + "###########");
        System.out.println(""); 
        
    
        
        
        try {
            ArrayList<DataExample> trainData = Functions.readData(trainFile);
            ArrayList<DataExample> testData = Functions.readData(testFile);
              
            System.out.println("########### Test euclidean distance ###########");
            testClassifier("confusionMatrixEuclidean", EUCLIDEAN, trainData, testData);            
            System.out.println("Test euclidean distance finished!"); 
            System.out.println("");
            
            System.out.println("########### Test manhatten distance ###########");
            testClassifier("confusionMatrixManhatten", MANHATTEN, trainData, testData); 
            System.out.println("Test manhatten distance finished!"); 
            System.out.println("");
        } catch (FileNotFoundException e) {
            
            System.exit(-1);
           // e.printStackTrace();
        }
      
    }
    

    /**
     * Method to test the classifiers
     * @param fileName: name of the outputfile
     * @param distanceMeasure: distance measure to use
     * @param trainData: ArrayList with the trainingdata
     * @param testData: ArrayList with the testdata
     */
    private static void testClassifier(String fileName, int distanceMeasure,  ArrayList<DataExample> trainData,  ArrayList<DataExample> testData)
    {
        ArrayList<Integer> labels = new ArrayList<Integer>();           //class labels
        ConfusionMatrix[] confusionMatrices = new ConfusionMatrix[k.length]; //confusion matrix
        ArrayList<double[]> testDataVector = new ArrayList<double[]>();           //array list with the vectors of the testdata
        int size = testData.size();
        
        //Array for the classification for all k 
        // number at position i is the class of the object classified with the i-th k
        int[] classification ;           
        double accuracy;
        
        File fout = new File(fileName);     //file to write the confusion matrix in
        FileOutputStream fos;                
        BufferedWriter bw;
      
        KNN knn = new KNN(trainData, distanceMeasure, k);
        
        //find all class labels
        for(int i = 0; i < trainData.size(); i++)
        {
            if(!labels.contains(trainData.get(i).getLabel()))
                    labels.add(trainData.get(i).getLabel()); 
        }
        Collections.sort(labels);
        
        //initialize the confusion matrix 
        for (int i = 0; i < confusionMatrices.length; i++)
        {
            confusionMatrices[i] = new ConfusionMatrix(k[i], labels); 
        }
        
        
        //get all vector of the testdata 
        for (DataExample data : testData)
        {
            testDataVector.add(data.getValues());
        }
        
       
       //Classify the testdata 
        for (int i = 0; i < testData.size(); i++)
        {
            
            if(i%1000==0) System.out.println("classified " + i + " of " + size + " datasets");
            
            classification = knn.classifyData( testData.get(i).getValues());
            
            //add classification to the confusion matrix 
            for(int j = 0; j < classification.length; j++)
            {
                confusionMatrices[j].setValue(testData.get(i).getLabel(), classification[j]);
            } 
        }
        
        System.out.println("");
       //write the confusion matrices in a file 
        //print out the accuracy for every k 
        try {
            fos = new FileOutputStream(fout);
            bw = new BufferedWriter(new OutputStreamWriter(fos));
        for(ConfusionMatrix conf :confusionMatrices )
        {
            accuracy = conf.calculateAccuracy();
            try {
                bw.write("Accuracy for k=" + conf.getK() + ":  " + accuracy*100);
                bw.newLine();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            conf.printMatrix(bw);
            bw.newLine();
           
            System.out.println("Accuracy for k=" + conf.getK() + ":  " + accuracy*100);
            
        }
        
        bw.close();
        
        }  catch (IOException e) {
            System.out.println("An error occured while writing the output file.");
            System.out.println("Errormessage: " + e.getMessage()); 
        }
    }
}
