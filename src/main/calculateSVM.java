package main;

import java.io.OutputStream;
import java.io.PrintStream;

import classifiers.SVM;
import data.CrossValidationSet;


public class calculateSVM implements Runnable
{
     CrossValidationSet[] cvSets;
     double c;
     double gamma;
     double acc;
     double powC;
     double powGamma;
     int k; 
     
    public calculateSVM(CrossValidationSet[] cvSets, double c, double powC, double gamma, double powGamma, int k)
    {
        this.cvSets = cvSets;
        this.c = c; 
        this.gamma = gamma;
        this.k = k; 
        
        this.powC = powC;
        this.powGamma = powGamma; 
    }
    
    public double getAcc(){
        return this.acc;
    }
    
    public double getC() {
        return this.c;
    }
    
    public double getGamma()
    {
        return this.gamma; 
    }
    
    @Override
    public void run()
    {
        System.out.println("c: " + c + " gamma: " + gamma); 
        double accSum= 0; 
        for(int i = 0; i < k; i++)
        {
           
            accSum += callSVM(cvSets[i], c, gamma);
        }
        
        this.acc = accSum/k; 

    }
    
    private double callSVM(CrossValidationSet set, double c, double gamma)
    {
        SVM svm = new SVM(set.getTrain(), set.getTest());  
        
        
        
        //try different kernels and c and gamma values 
        svm.setParameters(svm.SVM_C_SVC, svm.KERNEL_RBF, c, gamma);
        svm.buildClassifier();
        double accN = svm.evaluateModel();
       
        
       
        return accN;
    }

    public double getPowC() {
        return this.powC;
    }
    
    public double getPowGamma()
    {
        return this.powGamma;
    }
    
}