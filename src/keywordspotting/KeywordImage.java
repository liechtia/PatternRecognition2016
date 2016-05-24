package keywordspotting;




import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import utils.DTWFeatureVector;
import utils.FeatureVector;
import utils.FtVector;

public class KeywordImage implements DTWFeatureVector{

    private String id; 
    private String label;
    private File file;
    private int line;
    private int wordInLine;
    private ArrayList<FtVector> features = new ArrayList<FtVector>();
    private BufferedImage image; 
    private ArrayList<ArrayList<Double>> featureList = new ArrayList<ArrayList<Double>>(); 
    private double meanOfStrokesPerPixel;
    
    public String getID()
    {
        return this.id;
    }
    
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
    
    public List<FtVector> getFeatureVectors()
    {
        return this.features;
    }
    
    public ArrayList<ArrayList<Double>> getFeatureList()
    {
        return this.featureList;
    }
    
    public KeywordImage(String id, String label) {
    	this.id = id;
    	this.label  = label;
    }
    
    public KeywordImage(String id, String label, File file, int line, int wordInLine) {
        this.label  = label;
        this.file =file;
        this.line = line;
        this.wordInLine = wordInLine;
        this.id = id; 
    }
    
    public KeywordImage(String label) {
        this.label  = label;
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

    public void setFeatures(ArrayList<FtVector> features) {
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
            
            features = new ArrayList<FtVector>();
            
            
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
        

    
    private FeatureVector getFeatures(int[] vector)
    {
        
        int verticalProjectionProfile = 0; //number of black pixels
        double gravityOfWindow = 0;
        double secondOrderMoment = 0;
        int upperContour=0;
        int lowerContour=0;      
        int bwTransitions = 0;

        int numberOfPixelsBetweenLcUc=0;
        
        
            
        boolean lc=false;
        boolean black=true;
        
        for(int i = 0; i < vector.length; i++)
        {
            if(vector[i]==1 && !lc)
            {
                lowerContour=i;
                verticalProjectionProfile++;
                
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
                verticalProjectionProfile++;
                
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
        //double fractionUcLc = numberOfPixelsBetweenLcUc /(vector.length+0.0);

        
        FeatureVector fV  = new FeatureVector();
        fV.setBwTransistions( bwTransitions);
        fV.setFractionUcLc(numberOfPixelsBetweenLcUc);
        fV.setLowerContour(lowerContour);
        fV.setUpperContour(upperContour);
        fV.setGravityOfWindow(gravityOfWindow/vector.length);
        fV.setSecondOrderMoment(secondOrderMoment/(vector.length*vector.length));
        fV.setVerticalWordProfile(verticalProjectionProfile);
        
       
        return fV;
        
    }
    
    
    public double[][] array;
    public void formFeatureArray()
    {        
        array = new double[features.size()][9];
        
        for(int i = 0;i  < features.size(); i++)
        {
            FtVector fv = this.features.get(i);
            double[] x = fv.getAllFeatures();
            array[i] = x;          
        }
    }
    
    public void normFeatures()
    {
            for(int i = 0; i < array.length; i++)
            {
               array[i] = normFeature(array[i]);
            }
    }
    
    private double[] normFeature(double[] x)
    {
        double max = 0; 
        double min = Integer.MAX_VALUE;
        
        for(int i = 0; i < x.length; i++)
        {
            if(x[i] > max)
            {
                max = x[i];
            }
            
            if(x[i] < min)
            {
                min = x[i];
            }
        }
        
        for(int i = 0; i < x.length; i++)
        {
           x[i] = (x[i]-min) / (max-min); 
        }
       
        
        return x; 
        
        
        
    }
    
    public static BufferedImage getImageFromArray(int[] pixels, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        WritableRaster raster = (WritableRaster) image.getData();
        raster.setPixels(0,0,width,height,pixels);
        return image;
    }

}
