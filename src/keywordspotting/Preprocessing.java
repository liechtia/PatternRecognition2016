package keywordspotting;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.apache.commons.imaging.examples.SkewCorrection;

import utils.ConnectedComponent;
import utils.Histogram;
import utils.LinearRegression;
import utils.Tuple;

public class Preprocessing {

    private String outputpath;
    private ArrayList<KeywordImage> images = new ArrayList<KeywordImage>();
    private ArrayList<Integer> baselines = new ArrayList<Integer>();
    
    public Preprocessing(String outputpath) {
        this.outputpath = outputpath;
    }
    
    public void addImage(KeywordImage image)
    {
        images.add(image);

        
    }
    
    public void writeImages() throws IOException
    {
        for(int i = 0; i < images.size(); i++)
        {
            File file = new File(this.outputpath + "/" + images.get(i).getFile().getName());
            ImageIO.write(images.get(i).getImage(), "png", file);
        }
      
        
    }
    
    public void correctSkewImages()
    {
        
        for(KeywordImage image : images)
        {
            BufferedImage img = image.getImage();
            image.setImage(correctSkew2(img));
        }
        

    }
    
    /**
     * Correct the skew angle
     * 1. Find all lowest pixel in the image
     * 2. Sort this one out which are away from the average than a certain trehsold (to get outliers away)
     * 3. Do linear regression on the resulting points --> y=a*x+b
     * 4. angle = atan(a) 
     * @param image
     * @return
     */
    private BufferedImage correctSkew2(BufferedImage image)
    {
        int black = new Color(0,0,0).getRGB();
        ArrayList<Tuple> lowestPixel = new ArrayList<Tuple>();
        
        for(int i = 0;i < image.getWidth(); i++)
        {
            for(int j = image.getHeight()-1; j>0; j--)
            {
                if(image.getRGB(i,j) == black)
                {
                    Tuple t = new Tuple(i,j);
                    lowestPixel.add(t);                  
                }
            }
        }
        
        
        int averageHeight  = 0;
        for(int i = 0; i < lowestPixel.size(); i++)
        {
            averageHeight += lowestPixel.get(i).y;
            
        }
        
        averageHeight = averageHeight / lowestPixel.size();
        
        int treshold = 10;
        for(int i = 0; i < lowestPixel.size(); i++)
        {
            if(Math.abs(lowestPixel.get(i).y -averageHeight) > treshold)
            {
                lowestPixel.remove(i);
            }
        }
        
        double[] x= new double[lowestPixel.size()];
        double[] y = new double[lowestPixel.size()];
        
        for(int i = 0; i < lowestPixel.size(); i++)
        {
            x[i] = lowestPixel.get(i).x;
            y[i] = lowestPixel.get(i).y;
        }
        
        LinearRegression lr = new LinearRegression(x,y);
        double a = lr.slope();
        
       baselines.add((int) lr.intercept()) ;
        
        double angle = Math.atan(a);

             
        BufferedImage returnImage = new BufferedImage(image.getWidth()+50, image.getHeight(), BufferedImage.TYPE_INT_RGB);
        
        Graphics2D g2d = returnImage.createGraphics();
        
        g2d.setPaint (Color.white);
        g2d.fillRect ( 0, 0, returnImage.getWidth(), returnImage.getHeight() );
        g2d.rotate(Math.toRadians(angle), returnImage.getWidth() / 2, returnImage.getHeight() / 2);
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();
        
        
        return returnImage;
    }
    
    

    private BufferedImage correctSkew(BufferedImage image)
    {
        
        double skewAngle =  SkewCorrection.doIt(image);
    
        
        BufferedImage returnImage = new BufferedImage(image.getWidth()+50, image.getHeight(), BufferedImage.TYPE_INT_RGB);
        
        Graphics2D g2d = returnImage.createGraphics();
        
        g2d.setPaint (Color.white);
        g2d.fillRect ( 0, 0, returnImage.getWidth(), returnImage.getHeight() );
        g2d.rotate(Math.toRadians(skewAngle), returnImage.getWidth() / 2, returnImage.getHeight() / 2);
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();
        
        return returnImage;
    }
    
    

    
    public void binarizeImages(int treshold)
    {

        
        for(KeywordImage image : images)
        {
            BufferedImage img = image.getImage();
            image.setImage(binarize(img, treshold));
        }
        
 
    }
    
    public void correctSlantImages(double[] angles)
    {
        
        for(KeywordImage image : images)
        {
            BufferedImage img = image.getImage();
            image.setImage(correctSlant(img, angles));
            
        }

    }
    
    public void cutColumnsImages()
    {  
        for(KeywordImage image : images)
        {
            BufferedImage img = image.getImage();
            image.setImage(cutColumns(img));
           
        }

    }
    
    public void getComponentsImages()
    {  
        for(KeywordImage image : images)
        {
            BufferedImage img = image.getImage();
            image.setImage(getConnectedComponents(img));
         }

    }
    
    public void scaleVerticalImages()
    {
        
        for(int i = 0;i < images.size(); i++)
        {
            KeywordImage image = images.get(i);
            System.out.println(image.getFile().getName());
            BufferedImage img = image.getImage();
            
            BufferedImage imNew = scaleVertical(img, i);
            if(imNew != null)
                image.setImage(imNew);
            else
                images.remove(i);
           
        }

    }
    

    private static  BufferedImage resizeImage(BufferedImage originalImage, int type, int width, int height){
        BufferedImage resizedImage = new BufferedImage(width, height, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();
            
        return resizedImage;
        }
    
    /**
     * TODO: scale the image vertical 
     * TRIED:
     * estimate lower and upper baseline
     * split image in the areas (top to upper baseline, upper baseline to lower baseline, lower baseine to bottom)
     * resize every area to 20 pixels 
     * @param imageClipped
     * @param img
     * @return
     */
    private BufferedImage scaleVertical(BufferedImage imageClipped, int idx)
    {
        int maxNumberOfBlackPixels = 0;
        int yMax = 0;
        //find peak line
         for(int i = 0; i < imageClipped.getHeight(); i++)
         { 
             int numberOfBlackPixels = 0;
             for(int j = 0; j < imageClipped.getWidth(); j++){
                 if(imageClipped.getRGB(j, i) != new Color(255, 255, 255).getRGB() )
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
         //System.out.println("ymax=" + yMax);
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
         int yMin = -1;
         for(int i = yMax; i >=0; i--)
         {
             int numberOfBlackPixels = 0;
             for(int j = 0; j < imageClipped.getWidth(); j++){
                 if(imageClipped.getRGB(j, i) !=new Color(255, 255, 255).getRGB() )
                 {
                     numberOfBlackPixels++;
                 }
             }
             
             if(numberOfBlackPixels >= 0 && numberOfBlackPixels < minNumberOfBlackPixels)
             {
                 yMin = i;
                 minNumberOfBlackPixels = numberOfBlackPixels;
                 //System.out.println(numberOfBlackPixels);
             }
         }
         //System.out.println("ymin=" + yMin);
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
         
        int  lowerBaseLineY = 0; //Pruning.getLowerBaseLine(imageClipped);
         
        for(int i = upperBaseLineY; i < imageClipped.getHeight(); i++)
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
                lowerBaseLineY = i;
            }
        }
        
      /*   System.out.println("height: " + imageClipped.getHeight());
         System.out.println("ymax  " +yMax);
         System.out.println("average " + average);
         System.out.println(upperBaseLineY);
         System.out.println(lowerBaseLineY);*/
         
         int type = imageClipped.getType() == 0? BufferedImage.TYPE_INT_ARGB : imageClipped.getType();
         int heightArea = 20;
         
         System.out.println(imageClipped.getHeight());
         System.out.println(upperBaseLineY);
         System.out.println(lowerBaseLineY);
         
         BufferedImage area1 ;
         if(upperBaseLineY > 0)
         {
             
              area1= resizeImage(imageClipped.getSubimage(0, 0, imageClipped.getWidth(), upperBaseLineY), type, imageClipped.getWidth(), heightArea);
         }
         else
         {
             return null;
         }
        
                 
         BufferedImage area2;
       
         
         if(upperBaseLineY > lowerBaseLineY)
             lowerBaseLineY = upperBaseLineY;
         
         if(imageClipped.getHeight() > upperBaseLineY + 1 +(lowerBaseLineY-upperBaseLineY) && ( lowerBaseLineY-upperBaseLineY) >0)
         {
              area2=resizeImage(imageClipped.getSubimage(0, upperBaseLineY+1, imageClipped.getWidth(), lowerBaseLineY-upperBaseLineY), type, imageClipped.getWidth(), heightArea);
         }
         else
         {
             System.out.println("no area2");
             area2 = new BufferedImage(imageClipped.getWidth(), heightArea, BufferedImage.TYPE_INT_ARGB); 
         }
              
         BufferedImage area3;
         if(lowerBaseLineY+1 <= imageClipped.getHeight() && (imageClipped.getHeight()-lowerBaseLineY-1) > 0)
         {
             area3= resizeImage(imageClipped.getSubimage(0, lowerBaseLineY+1, imageClipped.getWidth(), imageClipped.getHeight()-lowerBaseLineY-1), type, imageClipped.getWidth(), heightArea);
         }
         else
         {
             System.out.println("no area3");
             area3 = new BufferedImage(imageClipped.getWidth(), heightArea, BufferedImage.TYPE_INT_ARGB);
         }
             
           

         
         BufferedImage combined = new BufferedImage(imageClipped.getWidth(), 3*heightArea, BufferedImage.TYPE_INT_ARGB);

              // paint both images, preserving the alpha channels
              Graphics g = combined.getGraphics();
              g.setColor(Color.white);
              g.fillRect(0, 0, combined.getWidth(), 3*heightArea);
              g.setColor(Color.black);;
              g.drawImage(area1, 0, 0, null);
              g.drawImage(area2, 0, heightArea, null);
              g.drawImage(area3,0, 2*heightArea, null); 
  
         
       return combined; 
    }

    /**
     * Cuts the empty columns at the beginning  & end of an image
     * Cuts the empty rows at the top of the image 
     * @param img
     * @return
     */
    private BufferedImage cutColumns(BufferedImage img)
    {
            int widthBegin = -1;
            int heightBegin = -1;
            int lowerHeightBegin = -1;
            int widthEnd;
            widthEnd = -1;
           int white = new Color(255, 255, 255).getRGB();
                 
           //find first column which as a black pixel
            for(int i =0; i <  img.getWidth(); i++)
            {
               
                for(int j = 0; j < img.getHeight(); j++)
                {
                    int rgb = img.getRGB(i, j);
                    if(rgb != white)
                    {
                        widthBegin = i-1; 
                        break;
                    }            
               if(widthBegin != -1)
                {
                    break; 
                }
                }
            }

          //find last column which has a black pixel
            for(int i =widthBegin; i <  img.getWidth()-1; i++)
            {
                boolean foundBlackPixel = false;
                for(int j = 0; j < img.getHeight(); j++)
                {
                    int rgb = img.getRGB(i, j);

                    if(rgb != white)
                    {
                      foundBlackPixel = true; 
                        break;
                    }
                }
                
                if(foundBlackPixel)
                {
                   widthEnd = i+1;
                }
            }
            
            //find first row which has a black pixel
            for(int i =0; i <  img.getHeight(); i++)
            {        
                for(int j = 0; j < img.getWidth(); j++)
                {
                    int rgb = img.getRGB(j, i);
                    if(rgb != white )
                    {
                        heightBegin = i-1; 
                        break;
                    }
                }
                
                if(heightBegin != -1)
                {
                    break; 
                }
            }
           
          //find last row which has a black pixel
            for(int i =  img.getHeight()-1; i >= 0;  i--)
            {         
                for(int j = 0; j < img.getWidth(); j++)
                {
                    int rgb = img.getRGB(j, i);
                    if(rgb != white )
                    {
                    	lowerHeightBegin = i+1; 
                        break;
                    }
                }
                
                if(lowerHeightBegin != -1)
                {
                    break; 
                }
            }
            
            //cut the image
            int widthNew = widthEnd-widthBegin;
            int heightNew = img.getHeight() - heightBegin - (img.getHeight() - lowerHeightBegin);
            
            System.out.println(heightNew);
            System.out.println(heightBegin);
            System.out.println(lowerHeightBegin);
            //System.out.println(heightNew - lowerHeightBegin);
            System.out.println("--");
            BufferedImage imageClipped= img.getSubimage(widthBegin, heightBegin, widthNew, heightNew);
                
            return imageClipped;
    }
    
    /**
     * Correct the slant
     * 1. Rotate image whichone of the angles from the array
     * 2. Calculate resulting histogram 
     * 3. Histogram with the biggest peeks is the winner --> correct slant 
     * 4. Correct the slant 
     * @param img
     * @param angles
     * @return
     */
    private BufferedImage correctSlant( BufferedImage img, double[] angles)
    { 
            HashMap<Double, Double> peeks = new  HashMap<Double, Double>();
            HashMap<Double, BufferedImage> images =  new HashMap<Double, BufferedImage>();
  
            for(double angle : angles)
            {
                BufferedImage returnImage = new BufferedImage(img.getWidth()+50, img.getHeight(), BufferedImage.TYPE_INT_RGB);
                

                AffineTransform sat = AffineTransform.getShearInstance(Math.toRadians(angle),0);     
                
                Graphics2D g2d = returnImage.createGraphics();
                g2d.setPaint (Color.white);
                g2d.fillRect ( 0, 0, returnImage.getWidth(), returnImage.getHeight() );
       
                g2d.transform(sat);
                g2d.drawImage(img, 0, 0, null);
                g2d.dispose();
                
                images.put(angle, returnImage);
                
                int[] histogram = new int[returnImage.getWidth()];
                for(int i = 0; i < returnImage.getWidth(); i++)
                {              
                         histogram[i] = 0;
                 }
                
                for(int i = 0; i < returnImage.getWidth(); i++)
                {
                     for(int j = 0; j < returnImage.getHeight(); j++)
                     {
                         int b  = (returnImage.getRGB( i, j)  == 0xFFFFFFFF ? 0 : 1 );
                         
                         if(b == 1)
                         {
                             histogram[i]++;
                         }
                     }
                 }
                
                Histogram histo = new Histogram(angle, histogram, returnImage.getHeight());
                double av = histo.getAveragePeekHeight();
                peeks.put(angle, av);
            }
            
            //calculate max peeks 
            double max =0;
            double maxAngle = 0;
            for(double angle : peeks.keySet())
            {
                if(peeks.get(angle)> max)
                {
                    max = peeks.get(angle);
                    maxAngle = angle; 
                }
            }
            
            return images.get(maxAngle);
            
   
    }
    
    /**
     * Binarize the image 
     * Every pixel which is below the treshold get converted to black 
     * @param sourceImage
     * @param threshold
     * @return
     */
    private BufferedImage binarize(BufferedImage sourceImage, int threshold){
        BufferedImage outputImage = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), sourceImage.getType());
        for(int i=0;i<sourceImage.getWidth();i++) {
            for(int j=0;j<sourceImage.getHeight();j++) {
                int rgb =sourceImage.getRGB(i,j);

                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = (rgb >> 0) & 0xff;

                if(r>threshold||g>threshold||b>threshold)
                {
                    r=255;
                    g=255;
                    b=255;
                }
                else
                {
                    r=0;
                    g=0;
                    b=0;
                }

                rgb= (rgb & 0xff000000) | (r << 16) | (g << 8) | (b << 0);
                outputImage.setRGB(i, j, rgb);
            }
        }

        return outputImage;
    
    }
    
   
    
    private BufferedImage getConnectedComponents(BufferedImage image)
    {
        ArrayList<ConnectedComponent> components = new ArrayList<ConnectedComponent>();
        
        
        BufferedImage outputImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        
        int black = new Color(0,0,0).getRGB(); 
        
        for(int i = 0;i  < image.getWidth(); i++)
        {
            for(int j =0; j < image.getHeight(); j++)
            {
                outputImage.setRGB(i, j, new Color(255,255,255).getRGB());
                

                if(image.getRGB(i, j)==black)
                {
                    boolean findComponent = false;
                    Tuple pixel = new Tuple(i,j);
                    
                    for(ConnectedComponent c : components)
                        {
                        
                           if(checkIfPixelsBelongsToComponent(c, pixel))
                            {
                                    c.pixels.add(pixel);
                                    c.pixelPlus();
                                    findComponent = true;
                                    break;
                                }
                        }
                    
                    if(!findComponent)
                    {
                        ConnectedComponent cc = new ConnectedComponent();
                        components.add(cc);
                        cc.pixels.add(pixel);
                        
                    }
                }
            }
        }


        
        for(ConnectedComponent c : components)
        {
            if(c.pixels.size() < 70){
                for(Tuple pixel : c.pixels)
                {
                   outputImage.setRGB(pixel.x, pixel.y, new Color(255,255,255).getRGB());
                }
            }
            
            else
            { 
                for(Tuple pixel : c.pixels)
                {
                   outputImage.setRGB(pixel.x, pixel.y, black);
                }
            }
          

        }
        
        return outputImage; 
        
    }
    
    
    private boolean checkIfPixelsBelongsToComponent(ConnectedComponent component, Tuple pixel)
    {
        for(Tuple t : component.pixels)
       {
            if(Math.abs(t.y-pixel.y) <= 2 && ((pixel.x - t.x) == 1))
            {
                return true;
            }
       }
        
        return false; 
    }
    


}

