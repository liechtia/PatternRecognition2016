package utils;
import java.util.ArrayList;

import knn.DataExample;



public class FeatureVector implements FtVector {

    private double gravityOfWindow = 0;
    private double secondOrderMoment  = 0; 
    private double bwTransistions = 0;
    private double lowerContour = 0;
    private double upperContour= 0;
    private double fractionUcLc= 0;
    private double weightOfWindow= 0;
    private double lowerGradient= 0; 
    private double upperGradient= 0;
    
    private double pixelsAboveBaseline = 0;
    private double pixelsBelowBaseline = 0;
    private double pixelsBetweenBaselines = 0; 
    
    public double getPixelsAboveBaseline() {
        return pixelsAboveBaseline;
    }

    public void setPixelsAboveBaseline(double pixelsAboveBaseline) {
        this.pixelsAboveBaseline = pixelsAboveBaseline;
    }

    public double getPixelsBelowBaseline() {
        return pixelsBelowBaseline;
    }

    public void setPixelsBelowBaseline(double pixelsBelowBaseline) {
        this.pixelsBelowBaseline = pixelsBelowBaseline;
    }

    public double getPixelsBetweenBaselines() {
        return pixelsBetweenBaselines;
    }

    public void setPixelsBetweenBaselines(double pixelsBetweenBaselines) {
        this.pixelsBetweenBaselines = pixelsBetweenBaselines;
    }

    public double getGravityOfWindow() {
        return gravityOfWindow;
    }

    public double getSecondOrderMoment() {
        return secondOrderMoment;
    }





    private ArrayList<Double> features;
  
    public double[] getAllFeatures() {
        double[] allFeatures = new double[]{bwTransistions, lowerContour, upperContour,
                fractionUcLc, gravityOfWindow, lowerGradient, upperGradient, secondOrderMoment, weightOfWindow,pixelsAboveBaseline, 
                pixelsBelowBaseline, pixelsBetweenBaselines};        
        return allFeatures;
    }
    
    public void normFeatures(){
        
        double[] features = getAllFeatures();
        
        double max = 0;
        double min = Double.MAX_VALUE; 
        
        if(features.length < 2)
        {
            return; 
        }
        
        for(int i = 0; i < features.length; i++)
        {
            if(features[i] > max)
                max = features[i];
            
            if(features[i] <min)
                min = features[i];
        }
        
              gravityOfWindow = (gravityOfWindow-min) / (max-min);
              secondOrderMoment  = (secondOrderMoment-min) / (max-min); 
              bwTransistions = (bwTransistions-min) / (max-min);
              lowerContour = (lowerContour-min) / (max-min);
              upperContour= (upperContour-min) / (max-min);
              fractionUcLc= (fractionUcLc-min) / (max-min);
              weightOfWindow= (weightOfWindow-min) / (max-min);
              lowerGradient=(lowerGradient-min) / (max-min);;   
              upperGradient= (upperGradient-min) / (max-min);
        
        
    }
    
    public void setGravityOfWindow(double gravityOfWindow) {
        this.gravityOfWindow = gravityOfWindow;
    }

    public void setSecondOrderMoment(double secondOrderMoment) {
        this.secondOrderMoment = secondOrderMoment;
    }

    public void setFeatures(ArrayList<Double> features) {
        this.features = features;
    }

    
    public ArrayList<Double> getFeatures()
    {
        return this.features; 
    }
    

    
    public double getLowerGradient()
    {
        return lowerGradient;
    }
    
    public double getUpperGradient()
    {
        return upperGradient;
    }

    public double getBwTransistions() {
        return bwTransistions;
    }

    public double getLowerContour() {
        return lowerContour;
    }

    public double getUpperContour() {
        return upperContour;
    }

    public double getFractionUcLc() {
        return fractionUcLc;
    }

    public double getWeightOfWindow() {
        return weightOfWindow;
    }

    public FeatureVector() {
        // TODO Auto-generated constructor stub
    }

    public void setBwTransistions(double bwTransistions) {
        this.bwTransistions = bwTransistions;
    }

    public void setLowerContour(double lowerContour) {
        this.lowerContour = lowerContour;
    }

    public void setUpperContour(double upperContour) {
        this.upperContour = upperContour;
    }

    public void setFractionUcLc(double fractionUcLc) {
        this.fractionUcLc = fractionUcLc;
    }

    public void setWeightOfWindow(double weightOfWindow) {
        this.weightOfWindow = weightOfWindow;
    }
    
    public void setLowerGradient(double lowerGradient)
    {
        this.lowerGradient = lowerGradient;
    }
    
    public void setUpperGradient(double upperGradient)
    {
        this.upperGradient = upperGradient;
    }
    
    


    
    public void setGradient(double lowerContour2, double upperContour2)
    {
        this.lowerGradient = this.lowerContour - lowerContour2;
        this.upperGradient = this.upperContour - upperContour2;
        
    }
    
    

}
