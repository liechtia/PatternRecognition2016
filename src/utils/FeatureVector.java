package utils;

public class FeatureVector {

    private int bwTransistions;
    private int lowerContour;
    private int upperContour;
    private double fractionUcLc;
    private double fractionOfBlackPixels;
    private int lowerGradient; 
    private int upperGradient;
    
    public double[] getAllFeatures() {
    	double[] allFeatures = new double[]{bwTransistions, lowerContour, upperContour,
    			fractionUcLc, fractionOfBlackPixels, lowerGradient, upperGradient};
    	return allFeatures;
    }
    
    public int getLowerGradient()
    {
        return lowerGradient;
    }
    
    public int getUpperGradient()
    {
        return upperGradient;
    }

    public int getBwTransistions() {
        return bwTransistions;
    }

    public int getLowerContour() {
        return lowerContour;
    }

    public int getUpperContour() {
        return upperContour;
    }

    public double getFractionUcLc() {
        return fractionUcLc;
    }

    public double getFractionOfBlackPixels() {
        return fractionOfBlackPixels;
    }

    public FeatureVector() {
        // TODO Auto-generated constructor stub
    }

    public void setBwTransistions(int bwTransistions) {
        this.bwTransistions = bwTransistions;
    }

    public void setLowerContour(int lowerContour) {
        this.lowerContour = lowerContour;
    }

    public void setUpperContour(int upperContour) {
        this.upperContour = upperContour;
    }

    public void setFractionUcLc(double fractionUcLc) {
        this.fractionUcLc = fractionUcLc;
    }

    public void setFractionOfBlackPixels(double fractionOfBlackPixels) {
        this.fractionOfBlackPixels = fractionOfBlackPixels;
    }
    
    public void setGradient(int lowerContour2, int upperContour2)
    {
        this.lowerGradient = this.lowerContour - lowerContour2;
        this.upperGradient = this.upperGradient - upperContour2;
        
    }

}
