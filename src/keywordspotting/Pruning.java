package keywordspotting;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import utils.ConnectedComponent;
import utils.Tuple;

public class Pruning {

    private static BufferedImage normalizeHeight(BufferedImage image, int size, int baseline)
    {
        
        int type = image.getType() == 0? BufferedImage.TYPE_INT_ARGB : image.getType();
        BufferedImage area1 ;
        BufferedImage area2 ;
        area1 =resizeImage(image.getSubimage(0, 0, image.getWidth(), baseline), type, image.getWidth(), size);
        
        if(baseline+1 < image.getHeight())
        {
            area2 = image.getSubimage(0, baseline+1, image.getWidth(), image.getHeight()-baseline-1);
        }
        else
        {
            area2 = image.getSubimage(0, baseline, image.getWidth(), 1);
        }
        
        BufferedImage combined = new BufferedImage(image.getWidth(), area1.getHeight()+area2.getHeight(), BufferedImage.TYPE_INT_ARGB);

        // paint both images, preserving the alpha channels
        Graphics g = combined.getGraphics();
        g.drawImage(area1, 0, 0, null);
        g.drawImage(area2, 0, area1.getHeight(), null);
        
        return combined;


    }
    
    private static  BufferedImage resizeImage(BufferedImage originalImage, int type, int width, int height){
        BufferedImage resizedImage = new BufferedImage(width, height, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();
            
        return resizedImage;
        }
    
    public static boolean getRatioOfArea(BufferedImage imageTemplate, BufferedImage image, double beta)
    {
        double ratio = (imageTemplate.getHeight()*imageTemplate.getWidth())/(image.getHeight()*image.getWidth()+0.0);
        
        if(ratio <= beta && ratio >= 1.0/beta) return true;
        else return false;

    }
    
    public static boolean getRatioOfAspect(BufferedImage imageTemplate, BufferedImage image, double gamma)
    {
        double aspectTemplate = imageTemplate.getWidth() / (imageTemplate.getHeight()+0.0);
        double aspectImage = image.getWidth() / (image.getHeight()+0.0);
        
        double ratio = aspectTemplate/(aspectImage);
        
        if(ratio <= gamma && ratio >= 1.0/gamma) return true;
        else return false; 
        
    }
    
    
   
    
 
    
    public static int getLowerBaseLine(BufferedImage image)
    {
        int treshold = 150;
        int[] histogram = new int[image.getHeight()];
        
        int black = new Color(0, 0, 0).getRGB();
        
        
        for(int i = 0; i < image.getHeight();i++)
        {
            int sum = 0;
            for(int j =0 ; j < image.getWidth(); j++)
            {
                if(image.getRGB(j, i) == black)
                    sum++;
            }
            histogram[i] = sum;
        }
        
        int maxDiff = 0;
        int maxDiffIdx = -1;
        for(int i = 1 ; i < image.getHeight(); i++)
        {
            int diff=Math.abs(histogram[i-1]-histogram[i]);
            
            if(diff > maxDiff)
            {
                maxDiff = diff;
                maxDiffIdx = i;
            }
            
        }
        
        
        return maxDiffIdx;
        
        
    }
    
    public static int countDescenders(BufferedImage image, int baseline)
    {
        
        if((baseline+5) < image.getHeight())
            baseline = baseline+5;
        
        if(baseline >= image.getHeight())
            return 0;
                
        int black = new Color(0, 0, 0).getRGB();
        
        ArrayList<ConnectedComponent> des = new ArrayList<ConnectedComponent>();
        
        boolean bP = false;
        for(int i = 0; i < image.getWidth(); i++)
        { 
            if(image.getRGB(i, baseline) == black)
            {
                
                if(!bP)
                {
                    bP = true; 
                    ConnectedComponent d = new ConnectedComponent();
                    d.pixels.add(new Tuple(i, baseline));
                    des.add(d);
                }
                else
                {
                    des.get(des.size()-1).pixels.add(new Tuple(i, baseline));
                    des.get(des.size()-1).pixelPlus();
                }
            }
            else
            {
                bP = false; 
            }
        }
        

     
           for(int j = baseline; j < image.getHeight(); j++)
           {
               for(int i = 0; i < image.getWidth(); i++)
               {

                   if(image.getRGB(i, j) == black)
                   {
                       Tuple t = new Tuple(i,j);
                       
                       for(ConnectedComponent d : des)
                       {
                           if(checkIfPixelBelongToGroup(d,t))
                           {
                               d.pixels.add(t);
                               d.pixelPlus();
                               break;
                            }
                       }                   
                   }    
               }
           }
        int count = 0;
        for(ConnectedComponent d : des)
        {
           if(d.pixels.size() >= 30)
           {
               count++;
           }
        }
        
         return count;
    }
    
    public static boolean checkIfPixelBelongToGroup(ConnectedComponent d, Tuple pixel)
    {
        for(Tuple t : d.pixels)
        {
            if(Math.abs(t.x-pixel.x) <= 1)
            {
                return true; 
            }
        }
        
        return false;
    }
    
    
}
