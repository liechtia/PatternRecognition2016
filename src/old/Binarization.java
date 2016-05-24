package old;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 *Class for binarizing the images for keyword spotting preprocessing
 */
public class Binarization {
    /**
     * @param images
     * @param saving
     * @param threshold
     * 
     * method to binarize all images and create the new files
     */
    public void binarizeImages(String images, String saving, int threshold) {
        File sourceFolder = new File(images);
        File destinationFolder = new File(saving);

        if(!destinationFolder.exists()){
            destinationFolder.mkdir();
        }

        File[] sourceImages = sourceFolder.listFiles();

        try{
            for (File file : sourceImages){
                if(!file.getName().contains("png"))
                {
                    continue;
                }
                
                BufferedImage image = ImageIO.read(file);
                BufferedImage binImage = binarize(image, threshold);

                File outputFile = new File(destinationFolder + "/" + file.getName());
                ImageIO.write(binImage, "png", outputFile);
                System.out.println(file.getName());
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * @param sourceImage
     * @param threshold
     * @return
     * 
     * binarizing method which computes black/white (true/false) per pixel
     */
    public BufferedImage binarize(BufferedImage sourceImage, int threshold){
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
}
