package old;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import utils.Histogram;


public class SlantCorrection {

    File f;
    String outputpath;
    
    public SlantCorrection(File f, String outputpath) {
        this.f = f;
        this.outputpath = outputpath;

    }

    public void slant( double[] angles)
    {

        
        try {
            BufferedImage img = ImageIO.read(f);
            
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
            
                File file = new File(this.outputpath + "/" + f.getName());

                ImageIO.write(images.get(maxAngle), "png", file);
            
           
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
}
