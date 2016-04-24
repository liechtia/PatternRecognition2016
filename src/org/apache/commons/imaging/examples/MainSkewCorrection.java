package org.apache.commons.imaging.examples;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.io.FilenameUtils;

public class MainSkewCorrection {
	
	
    public static void main(String[] args) throws ImageReadException, IOException {
    	
	 	final File folder = new File("KeywordSpottingData/wordimages/clipped_binary");
	 	
	 	 for (final File fileEntry : folder.listFiles()) {
	            if (fileEntry.isDirectory()) {
	            	System.out.println(fileEntry);
	            } else {
	                
	            	String imgFile;
	        		imgFile = folder+"/"+fileEntry.getName();
	        		
	        		if(!imgFile.contains(".png"))
	        		{
	        		    continue;
	        		}
	        		
	                BufferedImage img = Imaging.getBufferedImage(new File(imgFile));
	                if(img == null) {
	                    System.exit(-1);
	                }
	                System.out.println("Image name : " + imgFile);

	                System.out.println(" - page " + " width x height = " + img.getWidth() + " x " + img.getHeight());
	                double res = SkewCorrection.doIt(img);
	                System.out.println(" -- skew " + " :" + res);
	                
	                BufferedImage returnImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
	                Graphics2D g2d = returnImage.createGraphics();
	      
	                g2d.setPaint (Color.white);
	                g2d.fillRect ( 0, 0, returnImage.getWidth(), returnImage.getHeight() );
	                g2d.rotate(Math.toRadians(res), returnImage.getWidth() / 2, returnImage.getHeight() / 2);
	                g2d.drawImage(img, 0, 0, null);
	                g2d.dispose();
	                
		            // Save as PNG
		     
		            File file = new File("KeywordSpottingData/wordimages/clipped_skew/"+fileEntry.getName());
		
		            ImageIO.write(returnImage, "png", file);
 
    	            }
    	        }

    }
    
}