package main;

import java.io.OutputStream;
import java.io.PrintStream;

import weka.classifiers.Evaluation;
import classifiers.SVM;
import data.CrossValidationSet;


public class CrossValidation implements Runnable
{
     CrossValidationSet[] cvSets;
     double c;
     double gamma;
     double acc;
     double powC;
     double powGamma;
     int k; 
     private String kernel;
     
    public CrossValidation(CrossValidationSet[] cvSets, String kernel, double c, double powC, double gamma, double powGamma, int k)
    {
        this.cvSets = cvSets;
        this.c = c; 
        this.gamma = gamma;
        this.k = k; 
        
        this.powC = powC;
        this.powGamma = powGamma; 
        this.kernel = kernel;
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
    

    public double getPowC() {
        return this.powC;
    }
    
    public double getPowGamma()
    {
        return this.powGamma;
    }
    
    @Override
    public void run()
    {
        double accSum= 0; 
        System.out.println("c: " + c + " gamma: " + gamma);
        for(int i = 0; i < k; i++)
        {
            accSum += callSVM(cvSets[i], c, gamma);
        }
        
        this.acc = accSum/k; 

    }
    
    private double callSVM(CrossValidationSet set, double c, double gamma)
    {
        SVM svm = new SVM(set.getTrain(), set.getTest());  
        svm.setParameters(svm.SVM_C_SVC, kernel, c, gamma);
        svm.buildClassifier();
        Evaluation eval = svm.evaluateModel();
        double accN =eval.correct() / (eval.correct() + eval.incorrect());

           
        return accN;
    }

    
}