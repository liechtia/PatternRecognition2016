package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import classifiers.SVM;
import utils.IO_Functions;
import weka.core.Instances;
import weka.core.Debug.Random;
import data.CrossValidationSet;
import data.Result;

import java.io.PrintStream;
import java.io.OutputStream;



public class FindBestParameters{

    private Instances train;
    private String kernel;
    
  
    public FindBestParameters(Instances train, String kernel ) {
        this.train = train; 
        this.kernel = kernel;
        
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
        System.out.println("Test from c=2^-5 to 2^15 and gamma=2^-15 to 2^5 with intervall 1");    
        
        //first test with a bigger grid
        //use c values from 2^cmin to 2^cmax with an intervall of 1 
       Result resultBigGrid = cv(k, cMin, cMax, gammaMin, gammaMax, 1);
       System.out.println("Best C-Value: 2^" + resultBigGrid.getPowC());
       System.out.println("Best Gamma-Value: 2^" + resultBigGrid.getPowGamma());
       System.out.println();
       System.out.println("Test finer grid");
      
       //make the grid finer --> take best power of 2 and test in the range -1, +1 
       Result resultFineGrid = cv(k, resultBigGrid.getPowC()-1, resultBigGrid.getPowC()+1, resultBigGrid.getPowGamma()-1, resultBigGrid.getPowGamma()+1, 0.25);
       System.out.println("Best C-Value: 2^" + resultFineGrid.getPowC());
       System.out.println("Best Gamma-Value: 2^" + resultFineGrid.getPowGamma());
       System.out.println();
        
        return resultFineGrid; 
    }
    
    /**
     * Function to run a cross-validations for all possible c-gamma-valuesfor one kernel 
     * @param k
     * @param cMin
     * @param cMax
     * @param gammaMin
     * @param gammaMax
     * @param intervall
     * @return
     */
    private Result cv(int k, double cMin, double cMax, double gammaMin, double gammaMax, double intervall)
    {
        CrossValidationSet[] cvSets =  generateCvSet(1,k); 
        ArrayList<Result> results = new ArrayList<Result>(); 
        
        //use threads to speed the process up
        Thread[] threads;
        int numberOfThreads = 5; 
        
        HashMap<Double,Float> cValues = new HashMap<Double, Float>();
        HashMap<Double,Float> gammaValues = new HashMap<Double, Float>();
        
        //calculate the c-values and gamma-values 
        //c-values are caluclated with 2^x with x elem [cMin;cMax]
        //gamma-values are calculated with 2^x with x elem [gammaMin; gammaMax]
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


        for(double cKey : cValues.keySet())
        {
            float c = cValues.get(cKey);
            double powC= cKey;
            
            
            Object[] keys = gammaValues.keySet().toArray(); 
            System.out.println("Pair C-Value: 2^" + powC + " with all gamma values");
            for(int i = 0; i < keys.length; i++)
            {

                //suppress output of the classifier
                PrintStream originalStream = System.out;
                PrintStream dummyStream    = new PrintStream(new OutputStream(){
                    public void write(int b) {
                    }
                });
                System.setOut(dummyStream);
                if(i+numberOfThreads > keys.length)
                    numberOfThreads = keys.length -i; 
                
                //let numberOfThreads cross-validation run in parallel 
                threads = new Thread[numberOfThreads];
                CrossValidation[] svms = new CrossValidation[numberOfThreads];
                for(int t =0 ; t < threads.length; t++ )
                {
                    CrossValidation cv1 = new CrossValidation(cvSets, kernel, c, powC, gammaValues.get(keys[i+t]), (double) keys[i+t]);
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
                        
                        results.add(new Result( svms[t].getC(),  svms[t].getPowC(),  svms[t].getGamma(), svms[t].getPowGamma(),  svms[t].getAcc())); 
                    }
                    System.setOut(originalStream);
  
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
            }

            }
        }
        
        Result result = evaluateResults(results);
        IO_Functions.printToFile(results, "svm/", kernel, k, train.numInstances());      
        
        return result; 
    }
    

    /**
     * Evaluate the results to find out which c-gamma-value combination is the best
     * @param results
     * @return
     */
    private Result evaluateResults(ArrayList<Result> results)
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
    
    
   
    
    


}


