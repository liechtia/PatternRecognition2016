package utils;

import keywordspotting.KeywordImage;



public class Distance {

    
    double score = 0; 
    KeywordImage image;
   
    
    public Distance(KeywordImage image) {
        this.image= image;
    }
    
    public void addScore(double score)
    {
        this.score += score; 
    }
    
    public KeywordImage getImage() {  return this.image;}
    public double getScore() { return this.score;} 

}
