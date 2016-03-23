package data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * class to store the results
 */
public class Results {
	public double c; 
	public double gamma; 
	public double accuracy;
	public boolean finalSet; 
	public int kernel; //0 for rbf, 1 for linear
	
	public Results(double cValue, double gammaValue, double acc, boolean finalSetTaken, int kernelMode){
		this.c = cValue;
		this.gamma = gammaValue;
		this.accuracy = acc;
		this.finalSet = finalSetTaken;
		this.kernel = kernelMode;
	}	
	
	/**
	 * prints results to a file
	 * @param results
	 * @param filePath
	 */
	public static void printToFile(ArrayList<Results> results, String filePath){
		PrintWriter writer;
		try {
			File file = new File(filePath);			
			writer = new PrintWriter(file);

			for (Results result : results){
				if (!result.finalSet){
					if (result.kernel == 0){
						writer.println(String.format("C: %s - Gamma: %s - Accuracy: %s - Kernel: rbf", result.c, result.gamma, result.accuracy));
					}
					else{
						writer.println(String.format("C: %s - Gamma: %s - Accuracy: %s - Kernel: linear", result.c, result.gamma, result.accuracy));
					}
					
				}
				else{
					writer.println("***********************FINAL RESULT********************************");
					if (result.kernel == 0){
						writer.println(String.format("C: %s - Gamma: %s - Accuracy: %s - Kernel: rbf", result.c, result.gamma, result.accuracy));
					}
					else{
						writer.println(String.format("C: %s - Gamma: %s - Accuracy: %s - Kernel: linear", result.c, result.gamma, result.accuracy));
					}
					writer.println("*******************************************************************");
				}
			}
			
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
