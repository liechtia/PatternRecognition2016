package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import classifiers.SVM;
import weka.core.Instances;
import weka.core.Debug.Random;
import data.CrossValidationSet;
import data.Result;
import data.Results;

import java.io.PrintStream;
import java.io.OutputStream;

public class CrossValidation{

    private Instances train;
    
    
    
    public CrossValidation(Instances train ) {
        this.train = train; 
        System.out.println(train.numInstances());
    }
    
    private float pow(int a, double b)
    {
        float p = a;
        
        if(b<0)
        {
            return 1/(float)Math.pow(a, -1*b);
        }
        
        else return (float) Math.pow(a,b);
            
    }
    
    public Result doCV(int k)
    {
         int cMin = -5;
         int cMax = 15;
         int gammaMin = -15;
         int gammaMax = 5; 
        Result resultBigGrid = cv(k, cMin, cMax, gammaMin, gammaMax, 1);

       Result resultFineGrid = cv(k, resultBigGrid.getPowC()-1, resultBigGrid.getPowC()+1, resultBigGrid.getPowGamma()-1, resultBigGrid.getPowGamma()+1, 0.25);
      
        
        return resultFineGrid; 
    }
    
    private Result cv(int k, double cMin, double cMax, double gammaMin, double gammaMax, double intervall)
    {
        CrossValidationSet[] cvSets =  generateCvSet(1,k); 
        
        HashMap<Double,Float> cValues = new HashMap<Double, Float>();
        HashMap<Double,Float> gammaValues = new HashMap<Double, Float>();
        
        double j = cMin; 
        while(j <= cMax)
        {
            
            cValues.put(j, pow(2,j));
            j += intervall;
        }
        
        j = gammaMin;
        while (j <= gammaMax)
        {
            gammaValues.put(j, pow(2,j));
            j += intervall;
        }

        ArrayList<Result> results = new ArrayList<Result>(); 
        
      int numberOfThreads = 5; 
       
        for(double cKey : cValues.keySet())
        {
            float c = cValues.get(cKey);
            double powC= cKey;
            System.out.println(cKey);
            
            Object[] keys = gammaValues.keySet().toArray(); 
            for(int i = 0; i < keys.length; i++)
            {
              
                PrintStream originalStream = System.out;

                PrintStream dummyStream    = new PrintStream(new OutputStream(){
                    public void write(int b) {
                        //NO-OP
                    }
                });
                System.setOut(dummyStream);
                
                if(i+numberOfThreads > keys.length)
                    numberOfThreads = keys.length -i; 
                
                Thread[] threads = new Thread[numberOfThreads];
                calculateSVM[] svms = new calculateSVM[numberOfThreads];
                for(int t =0 ; t < threads.length; t++ )
                {
                    calculateSVM cv1 = new calculateSVM(cvSets, c, powC, gammaValues.get(keys[i+t]), (double) keys[i+t], k);
                    Thread t1 = new Thread(cv1);
                    t1.start();
                    threads[t] = t1; 
                    svms[t] = cv1; 
                }
                               
                i=i+numberOfThreads;
               
                try {
                    
                    for(int t = 0; t < threads.length; t++)
                    {
                        threads[t].join();
                        
                        results.add(new Result( svms[t].getC(),  svms[t].getPowC(),  svms[t].getGamma(), svms[t].getPowGamma(),  svms[t].getAcc(), k)); 
                    }
                   

                    
                    System.setOut(originalStream);

                    
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
              
                
            }

        }
        
        Result result = getBestParameters(results);
                
        System.out.println(result.getAcc());
        System.out.println(result.getC() + " (2^" + result.getPowC() + ")");
        System.out.println(result.getGamma()+ " (2^" + result.getPowGamma() + ")");
        
        return result;
        
        
    }
    

    
    private Result getBestParameters(ArrayList<Result> results)
    {
        double accBest = 0;
        Result resultBest = null; 
        
        for(Result result : results)
        {
            if(result.getAcc() > accBest)
            {
                resultBest = result;
                accBest = result.getAcc();
            }
        }
        
        return resultBest; 
    }
    
   
    
   
    

   

  
    
    /**
     * function to return cross validation sets
     * @param seedValue
     * @param foldNumber
     * @return
     */
    private CrossValidationSet[] generateCvSet(int seedValue, int foldNumber){
        int folds = foldNumber;
        int seed = seedValue;
        Random rand = new Random(seed);
        Instances randData = new Instances(train);
        randData.randomize(rand);
        CrossValidationSet[] sets = new CrossValidationSet[folds];
        
        for (int n = 0; n < folds; n++) {
            Instances trainSet = randData.trainCV(folds, n);
            Instances testSet = randData.testCV(folds, n);
            sets[n] = new CrossValidationSet(trainSet, testSet);
        }
        
        return sets;
    }
    
    
    /**
     * prints results to a file
     * @param results
     * @param filePath
     */
    public static void printToFile(ArrayList<Results> results, String filePath){
        PrintWriter writer;
        try {
            File file = new File(filePath);         
            writer = new PrintWriter(file);

            for (Results result : results){
                if (!result.finalSet){
                    if (result.kernel == 0){
                        writer.println(String.format("C: %s - Gamma: %s - Accuracy: %s - Kernel: rbf", result.c, result.gamma, result.accuracy));
                    }
                    else{
                        writer.println(String.format("C: %s - Gamma: %s - Accuracy: %s - Kernel: linear", result.c, result.gamma, result.accuracy));
                    }
                    
                }
                else{
                    writer.println("***********************FINAL RESULT********************************");
                    if (result.kernel == 0){
                        writer.println(String.format("C: %s - Gamma: %s - Accuracy: %s - Kernel: rbf", result.c, result.gamma, result.accuracy));
                    }
                    else{
                        writer.println(String.format("C: %s - Gamma: %s - Accuracy: %s - Kernel: linear", result.c, result.gamma, result.accuracy));
                    }
                    writer.println("*******************************************************************");
                }
            }
            
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    


}


