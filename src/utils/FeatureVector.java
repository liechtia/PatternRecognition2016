package utils;
import java.util.ArrayList;

public class FeatureVector implements FtVector {

    private double gravityOfWindow = 0;     
    private double secondOrderMoment  = 0; 
    private double bwTransistions = 0;
    private double lowerContour = 0;    //lower word profile
    private double upperContour= 0;     //upper word profile
    private double fractionUcLc= 0;     
    private double lowerGradient= 0; 
    private double upperGradient= 0;
    private double verticalWordProfile = 0; 
    
    private double pixelsAboveBaseline = 0;
    private double pixelsBelowBaseline = 0;
    private double pixelsBetweenBaselines = 0; 

    private ArrayList<Double> features = new ArrayList<Double>();
    
  
    
    public double getVerticalWordProfile()
    {
        return this.verticalWordProfile;
    }
    
    public void setVerticalWordProfile(double x)
    {
        this.verticalWordProfile = x;
    }
    
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


    
    public void normFeatures(){

   
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
    


    
    @Override
    public double[] getAllFeatures() {
     
       double[] d = {verticalWordProfile, gravityOfWindow, secondOrderMoment, lowerContour,
               upperContour, lowerGradient, upperGradient, bwTransistions, fractionUcLc};
       
       return d;
    }
    
    

}
