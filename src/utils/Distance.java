package utils;

import keywordspotting.KeywordImage;



public class Distance {

    
    double score = 0; 
    KeywordImage image;
   
    
    public Distance(KeywordImage image, double distance) {
        this.image= image;
        this.score = distance;
    }
    

    
    public KeywordImage getImage() {  return this.image;}
    public double getScore() { return this.score;} 

}
