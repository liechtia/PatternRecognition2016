package keywordspotting;




import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.util.ArrayList;

import utils.FeatureVector;

public class KeywordImage{

    private String label;
    private File file;
    private int line;
    private int wordInLine;
    private ArrayList<FeatureVector> features = new ArrayList<FeatureVector>();
    private BufferedImage image; 
    private ArrayList<ArrayList<Double>> featureList = new ArrayList<ArrayList<Double>>(); 
    private double meanOfStrokesPerPixel;
    
    public void setMeanOfStrokesPerPixel(double m)
    {
        this.meanOfStrokesPerPixel = m;
    }
    
    public double getMeanOfStrokesPerPixel()
    {
        return this.meanOfStrokesPerPixel; 
    }
    
    public BufferedImage getImage()
    {
        return this.image;
    }
   
    
    public String getLabel()
    {
        return this.label;
    }
    
    public ArrayList<FeatureVector> getFeatureVectors()
    {
        return this.features;
    }
    
    public ArrayList<ArrayList<Double>> getFeatureList()
    {
        return this.featureList;
    }
    
    public KeywordImage(String label, File file, int line, int wordInLine) {
        this.label  = label;
        this.file =file;
        this.line = line;
        this.wordInLine = wordInLine;
    }
    
    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getWordInLine() {
        return wordInLine;
    }

    public void setWordInLine(int wordInLine) {
        this.wordInLine = wordInLine;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setFeatures(ArrayList<FeatureVector> features) {
        this.features = features;
    }
    
    public void setImage(BufferedImage image)
    {
        this.image = image;
    }

    public void slidingWindow()
    {
        /*   BufferedImage image = ImageIO.read(file);*/
            int width = image.getWidth();
            int height = image.getHeight();
            
            features = new ArrayList<FeatureVector>();
            
            
            int[][] pixels = new int[width][height];
            int[] p = new int[(width*height)+1];

            for( int i = 0; i < width; i++ )
                for( int j = 0; j < height; j++ )
                {
                    pixels[i][j] = (image.getRGB( i, j)  == 0xFFFFFFFF ? 0 : 1 );
                }

        
            //int[] baselines = findBaseLines(image);
            FeatureVector fV1 = getFeatures(pixels[0]);
        
            features.add(fV1);     
            for( int i = 1; i < width; i++ )
            {
                FeatureVector fV2 = getFeatures(pixels[i]);
                fV1.setGradient(fV2.getLowerContour(), fV2.getUpperContour());
                features.add(fV2);
                
                fV1= fV2; 
            }
            
            //last feature vector
            fV1.setGradient(0, 0);
          
    }
    
    private int[] findBaseLines(BufferedImage imageClipped)
    {
        int[] r = new int[2];
        
        int maxNumberOfBlackPixels = 0;
        int yMax = 0;
      //find peak line
        for(int i = 0; i < imageClipped.getHeight(); i++)
        { 
            int numberOfBlackPixels = 0;
            for(int j = 0; j < imageClipped.getWidth(); j++){
                if(imageClipped.getRGB(j, i) !=new Color(255, 255, 255).getRGB() )
                {
                    numberOfBlackPixels++;
                }
            }
            
            if(numberOfBlackPixels > maxNumberOfBlackPixels)
            {
                yMax = i; 
                maxNumberOfBlackPixels = numberOfBlackPixels;
            }
            
        }
        
        //calculate average number of pixels in each row
        int average  = 0;
        int numberOfRows = 0;
        int maxTransitions = 0; 
        for(int i = 0; i < imageClipped.getHeight(); i++)
        { 
            int numberOfBlackPixels = 0;
            boolean blackpixel = false;
            int transistions = 0;
            for(int j = 0; j < imageClipped.getWidth(); j++){
                if(imageClipped.getRGB(j, i) !=new Color(255, 255, 255).getRGB() )
                {
                    numberOfBlackPixels++;
                    
                    if(!blackpixel)
                    {
                        blackpixel = true;
                        transistions++;
                    }
                }
                else
                {
                    blackpixel = false; 
                }
            }
            
            if(transistions > maxTransitions)
            {
                maxTransitions = transistions; 
            }

            average += numberOfBlackPixels;

        }
       
        average =  average / imageClipped.getHeight();

            if(average == 0)
            {
                return null;
            }
        
        //find row with min number of black pixels before peak line
        int minNumberOfBlackPixels = Integer.MAX_VALUE;
        int yMin = 0;
        for(int i = 0; i < yMax; i++)
        {
            int numberOfBlackPixels = 0;
            for(int j = 0; j < imageClipped.getWidth(); j++){
                if(imageClipped.getRGB(j, i) !=new Color(255, 255, 255).getRGB() )
                {
                    numberOfBlackPixels++;
                }
            }
            
            if(numberOfBlackPixels > 0 && numberOfBlackPixels < minNumberOfBlackPixels)
            {
                yMin = i;
                minNumberOfBlackPixels = numberOfBlackPixels;
            }
        }
        
        //find row after yMin with more or equal average pixel 
        int upperBaseLineY = 0;
        for(int i = yMin; i < imageClipped.getHeight(); i++)
        {
            int numberOfBlackPixels = 0;
            for(int j = 0; j < imageClipped.getWidth(); j++){
                if(imageClipped.getRGB(j, i) !=new Color(255, 255, 255).getRGB() )
                {
                    numberOfBlackPixels++;
                }
            }
            
            if(numberOfBlackPixels >= average)
            {
                upperBaseLineY = i;
                break; 
            }
        }
        
        /************LOWER BASELINE**********************/
        int minPixY = imageClipped.getHeight()-1;
        minNumberOfBlackPixels = Integer.MAX_VALUE; 
        for(int i = yMax+1; i < imageClipped.getHeight(); i++) 
        {
                int numberOfBlackPixels = 0;
            for(int j = 0; j < imageClipped.getWidth(); j++){
                if(imageClipped.getRGB(j, i) !=new Color(255, 255, 255).getRGB() )
                {
                    numberOfBlackPixels++;
                }
            }
            
            if(numberOfBlackPixels > 0 && numberOfBlackPixels < minNumberOfBlackPixels)
            {
                minPixY = i;
                minNumberOfBlackPixels = numberOfBlackPixels;
            }
        }

       
        int avgPixY = 0;
        for(int i = minPixY; i > 0; i--)
        {
            int numberOfBlackPixels = 0;
            for(int j = 0; j < imageClipped.getWidth(); j++){
                if(imageClipped.getRGB(j, i) !=new Color(255, 255, 255).getRGB() )
                {
                    numberOfBlackPixels++;
                }
            }
            
            if(numberOfBlackPixels >= average)
            {
                avgPixY = i;
                break; 
            }
        }
        
        int lowerBaseLineY;
        if((minPixY-avgPixY) < (avgPixY - upperBaseLineY))
        {
            lowerBaseLineY = minPixY;
        }
        else{
            lowerBaseLineY=avgPixY; 
        }
        
        r[0] = upperBaseLineY;
        r[1] = lowerBaseLineY;
        return r; 
    }
    
    private int pixelsAboveBaseline(int[] vector, int upperBaseLine)
    {
        int pixel  = 0;
        for(int i = 0; i < upperBaseLine; i++)
        {
           if( vector[i]==0 )
               pixel++;
        }
        
        return pixel;
    }
    
    
    private int pixelsBetweenBaseline(int[] vector, int upperBaseLine, int lowerBaseLine)
    {
        int pixel  = 0;
        for(int i = upperBaseLine; i < lowerBaseLine; i++)
        {
           if( vector[i]==0 )
               pixel++;
        }
        
        return pixel;
    }
    
   
    
    private int pixelsUnderBaseline(int[] vector, int lowerBaseLine)
    {
        int pixel  = 0;
        for(int i = lowerBaseLine; i < vector.length; i++)
        {
           if( vector[i]==0 )
               pixel++;
        }
        
        return pixel;
    }
    
    private FeatureVector getFeatures(int[] vector)
    {
        
        int weightOfWindow = 0;
        double gravityOfWindow = 0;
        double secondOrderMoment = 0;
        int bwTransitions = 0;
        int lowerContour=-1;
        int upperContour=-1;
        int numberOfPixelsBetweenLcUc=0;
        
        
            
        boolean lc=false;
        boolean black=true;
        
        for(int i = 0; i < vector.length; i++)
        {
            if(vector[i]==1 && !lc)
            {
                lowerContour=i;
                weightOfWindow++;
                
                gravityOfWindow += (i+1)*1;
                secondOrderMoment += (i+1)*(i+1);
                        
                
                break;
            }

        }
                
        for(int i = lowerContour+1; i < vector.length; i++)
        {
            if(vector[i]==1)
            {
                upperContour = i; 
                weightOfWindow++;
                
                gravityOfWindow += (i+1)*1;
                secondOrderMoment += (i+1)*(i+1);
            }
            
            if(vector[i]==0 && black)
            {
                //b-w tranistion
                bwTransitions++;
                black = false;
            }
            else if(vector[i]==1 && !black)
            {
                black = true; 
            }
        }
        
        if(upperContour == -1)
            upperContour = lowerContour; 
        
        numberOfPixelsBetweenLcUc = upperContour - lowerContour; 
        
      //  double fractionOfBlackPixels = numberOfBlackPixels / (vector.length+ 0.0); 
        double fractionUcLc = numberOfPixelsBetweenLcUc /(vector.length+0.0);

        
        FeatureVector fV  = new FeatureVector();
        fV.setBwTransistions( bwTransitions);
        fV.setWeightOfWindow(weightOfWindow);
        fV.setFractionUcLc(fractionUcLc);
        fV.setLowerContour(lowerContour);
        fV.setUpperContour(upperContour);
        fV.setGravityOfWindow(gravityOfWindow/vector.length);
        fV.setSecondOrderMoment(secondOrderMoment/(vector.length*vector.length));
        
       
        return fV;
        
    }
    
    public static BufferedImage getImageFromArray(int[] pixels, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        WritableRaster raster = (WritableRaster) image.getData();
        raster.setPixels(0,0,width,height,pixels);
        return image;
    }

}
