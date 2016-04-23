package ks.datahandling;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import utils.FeatureVector;

public class Image {

    private String label;
    private File file;
    private int line;
    private int wordInLine;
    private ArrayList<FeatureVector> features = new ArrayList<FeatureVector>();
    
    public String getLabel()
    {
        return this.label;
    }
    
    public ArrayList<FeatureVector> getFeatures()
    {
        return this.features;
    }
    
    public Image(String label, File file, int line, int wordInLine) {
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

    public void slidingWindow()
    {
        try {
            BufferedImage image = ImageIO.read(file);
            int width = image.getWidth();
            int height = image.getHeight();
            
            System.out.println(width + " " + height);
            
            int[][] pixels = new int[width][height];
            int[] p = new int[(width*height)+1];

            for( int i = 0; i < width; i++ )
                for( int j = 0; j < height; j++ )
                {
                    pixels[i][j] = (image.getRGB( i, j)  == 0xFFFFFFFF ? 0 : 1 );
                }

        
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
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    private FeatureVector getFeatures(int[] vector)
    {
        int bwTransitions = 0;
        int lowerContour=-1;
        int upperContour=-1;
        int numberOfPixelsBetweenLcUc=0;
        int numberOfBlackPixels = 0;
        
            
        boolean lc=false;
        boolean black=true;
        
        for(int i = 0; i < vector.length; i++)
        {
            if(vector[i]==1 && !lc)
            {
                lowerContour=i;
                numberOfBlackPixels++;
                break;
            }

        }
        
        if (lowerContour == -1)
        {
            FeatureVector fV  = new FeatureVector();
            fV.setBwTransistions( 0);
            fV.setFractionOfBlackPixels(0);
            fV.setFractionUcLc(0);
            fV.setLowerContour(0);
            fV.setUpperContour(0);
        }
        
        for(int i = lowerContour+1; i < vector.length; i++)
        {
            if(vector[i]==1)
            {
                upperContour = i; 
                numberOfBlackPixels++;
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
        
        double fractionOfBlackPixels = numberOfBlackPixels / (vector.length+ 0.0); 
        double fractionUcLc = numberOfPixelsBetweenLcUc /(vector.length+0.0);

        
        FeatureVector fV  = new FeatureVector();
        fV.setBwTransistions( bwTransitions);
        fV.setFractionOfBlackPixels(fractionOfBlackPixels);
        fV.setFractionUcLc(fractionUcLc);
        fV.setLowerContour(lowerContour);
        fV.setUpperContour(upperContour);
        
        return fV;
        
    }
    
    public static BufferedImage getImageFromArray(int[] pixels, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        WritableRaster raster = (WritableRaster) image.getData();
        raster.setPixels(0,0,width,height,pixels);
        return image;
    }

}
