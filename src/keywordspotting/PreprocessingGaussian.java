package keywordspotting;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.filter.Convolver;
import ij.plugin.filter.GaussianBlur;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;

public class PreprocessingGaussian {
	private int height;
	private int width;
	
	public PreprocessingGaussian(int height, int width) {
		this.height = height;
		this.width = width;
	}
	
	/**
	 * Apply smoothing and edge detection to all the images in folder source and
	 * stores the new images in folders destinationSmoothing and destinationEdges.
	 */
	public void process(String source, String destinationSmoothing, String destinationEdges) {
		File folder = new File(source);
		File[] sourceImages = folder.listFiles();
		
		for (int i = 0; i < sourceImages.length; i++) {
			String image = sourceImages[i].getName();
			gaussianSmoothing(image, source, destinationSmoothing, height, width);
			edgeDetection(image, source, destinationEdges, height, width);
		}
		
		System.out.println("Processed images stored in folders " + destinationSmoothing
				+ " and " + destinationEdges);
	}
	
	/**
	 * Extract the pixels features for all images in sourceSmoothing and
	 * sourceEdges folder. An image must have the same name in both folders.
	 * @throws IOException 
	 */
	public void extractFeatures(String sourceSmoothing, String sourceEdges, String outputFile)
			throws IOException {
		File folder = new File(sourceSmoothing);
		File[] files = folder.listFiles();
		
		List<String> lines = new ArrayList<String>();
		for (int i = 0; i < files.length; i++) {
			String fileName = files[i].getName();
			int idx = fileName.lastIndexOf(".");
			String s = fileName.substring(0, idx);
			
			ImagePlus imp = IJ.openImage(sourceSmoothing + "/" + fileName);
			ImageConverter converter = new ImageConverter(imp);
			converter.convertToGray8();
			ImageProcessor ip = imp.getProcessor();
			
			for (int x = 0; x < ip.getWidth(); x++) {
				for (int y = 0; y < ip.getHeight(); y++) {
					s += "," + ip.getPixel(x, y);
				}
			}
			
			imp = IJ.openImage(sourceEdges + "/" + fileName);
			converter = new ImageConverter(imp);
			converter.convertToGray8();
			ip = imp.getProcessor();
			
			for (int x = 0; x < ip.getWidth(); x++) {
				for (int y = 0; y < ip.getHeight(); y++) {
					s += "," + ip.getPixel(x, y);
				}
			}
			lines.add(s);
			imp.flush();
		}
		
		Path file = Paths.get(outputFile);
		Files.write(file, lines, Charset.forName("UTF-8"));
		System.out.println("Features extracted in file " + outputFile);
	}
	
	
	// PRIVATE METHODS
	
	/**
	 * Smooth the image with a Gaussian filter.
	 */
	private void gaussianSmoothing(String image, String source, String destination, int height, int width) {
		ImagePlus imp = IJ.openImage(source + "/" + image);
		ImageConverter converter = new ImageConverter(imp);
		converter.convertToGray8();
		ImageProcessor ip = imp.getProcessor();
		
		GaussianBlur g = new GaussianBlur();
		g.blurGaussian(ip, 4);
		
		ip = ip.resize(width, height, true);
		imp.setProcessor(ip);
		
		IJ.save(imp, destination + "/" + image);
		imp.flush();
    }
	
	/**
	 * Apply a Laplacian kernel for detecting edges.
	 */
	private void edgeDetection(String image, String source, String destination, int height, int width) {
		ImagePlus imp = IJ.openImage(source + "/" + image);
		ImageConverter converter = new ImageConverter(imp);
		converter.convertToGray8();
		ImageProcessor ip = imp.getProcessor();
		
		Convolver c = new Convolver();
		c.setNormalize(true);
		float[] kernel = {0, -1, 0, -1, 4, -1, 0, -1, 0};
		c.convolve(ip, kernel, 3, 3);
		
		ip = ip.resize(width, height, true);
		imp.setProcessor(ip);
		
		IJ.save(imp, destination + "/" + image);
		imp.flush();
    }
}
