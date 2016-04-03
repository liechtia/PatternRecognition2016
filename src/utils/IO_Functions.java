package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;

import weka.classifiers.Evaluation;
import data.Result;

/**
 * Class to read and write to files

 *
 */
public class IO_Functions {

    /**
     * Read csv File
     * @param file
     * @param colWithClass: column in which the class is specified
     * @param delimiter: delimiter of the csv-File
     * @return
     */
    public static ArrayList<Datapoint> readCsvFile(String file, int colWithClass, String delimiter)
    {
        BufferedReader br;
        ArrayList<Datapoint> datapoints = new ArrayList<Datapoint>();
        Datapoint datapoint;  
        String line=null;
        String[] lineSplit = null;

            try {
                br = new BufferedReader(new FileReader(file));
                line = br.readLine();
                while(line != null)
                {
                    
                   
                    lineSplit = line.split(delimiter);
                    datapoint = new Datapoint(lineSplit.length-1); 
                    try{
                        datapoint.setLabel(Integer.parseInt(lineSplit[colWithClass]));
                        
                        int j = 0; 
                        for(int i = 0; i < lineSplit.length; i++)
                        {
                            if(i != colWithClass)
                            {
                                datapoint.addValue(j, Integer.parseInt(lineSplit[i]));
                                j++; 
                            }      
                        }
                        
                        datapoints.add(datapoint);
                        line = br.readLine();
                        
                        
                    }catch(NumberFormatException e)
                    {
                        System.out.println("Error while reading the file. Just numbers allowed as data points!");
                        System.exit(-1);
                    }
                
                }
                
                br.close();
            }catch (IOException e1) {
                System.out.println("An error occured while reading the file");
                System.out.println("Errormessage: " + e1.getMessage());
                System.exit(-1);
            }            
        return datapoints; 
    }

    
    /**
     * Convert a csv-File to an arff-File
     * @param outputFileName
     * @param relationName
     * @param datapoints
     * @return the name of the outputfile
     */
    public static String convertToArffFile(String outputFileName, String relationName, ArrayList<Datapoint> datapoints)
    {
        File fout;
        FileOutputStream fos;
        BufferedWriter bw;
        ArrayList<Integer> classes = new ArrayList<Integer>();
        
        for(int i = 0; i < datapoints.size(); i++)
        {
            if(!classes.contains(datapoints.get(i).getLabel()))
                classes.add(datapoints.get(i).getLabel());
        }
        
        Collections.sort(classes);
        
        try {
            fout = new File(outputFileName + ".arff");
            fos = new FileOutputStream(fout);
            bw = new BufferedWriter(new OutputStreamWriter(fos));
            
            bw.write("@RELATION " + relationName);
            bw.newLine();
            bw.newLine();
            
            for(int i = 0; i < datapoints.get(0).getValues().length; i++)
            {
                bw.write("@attribute " + i + " numeric");
                bw.newLine();
            }
            
          //  bw.write("@attribute class{");
            String classesString = "@attribute class{";
            for(int i = 0; i < classes.size()-1; i++)
            {
                classesString += classes.get(i) + ",";
               // bw.write(classes.get(i) + ",");
            }
            //int t= classes.get(classes.size()-1);
            //bw.write(classes.get(classes.size()-1));
            //bw.write("}");
            classesString += classes.get(classes.size()-1) +"}"; 
            bw.write(classesString);
            
            
            bw.newLine();
            bw.write("@data");
            bw.newLine();
            for(int i = 0; i < datapoints.size(); i++)
            {
                for(int j = 0;j < datapoints.get(i).getValues().length; j++)
                {
                    bw.write(datapoints.get(i).getValues()[j] + ",");
                   
                }
                
                bw.write(datapoints.get(i).getLabel() + "");
                bw.newLine();
            }
            
            bw.close(); 
            
        } catch (IOException e) {
            System.out.println("An error occured while writing the output arff file");
            System.exit(-1); 
        } 
        
        return outputFileName + ".arff";
    }
    
    /**
     * Function to write the final results to a file
     * @param eval - Evaluation of the classifier
     * @param filePath - the path to write the file to
     * @param kernel - the kernel which was used
     * @param c - the c-Value which was used
     * @param gamma - the gamma value which was used
     */
    public static void printFinalResult(Evaluation eval, String filePath, String kernel, double c, double gamma)
    {

        File fout;
        FileOutputStream fos;
        BufferedWriter bw;
        try {
            fout = new File(filePath + "FinalResult_" + kernel + ".txt");
            fos = new FileOutputStream(fout);
            bw = new BufferedWriter(new OutputStreamWriter(fos));
            
            bw.write("Kernel: " + kernel);
            bw.write("C-Value: 2^" + c);
            bw.write("Gamma-Value: 2^" + gamma);
            bw.newLine();
            bw.newLine();
            bw.write(eval.toSummaryString("\nResults\n======\n", false));
            bw.close();
        }
        catch (IOException e) {
            System.out.println("An error occured while writing the final result file");
            System.exit(-1); 
        } 
        
    }
        
   /**
    * Print the result of the cross-validation for one kernel to a file
    * @param results - The Results
    * @param filePath - the path to write the file to
    * @param kernel - The kernel which was used
    * @param cvFolds - how many cross-validation folds where used
    * @param numberOfInstances - how many instances for the cv where used 
    */
    public static void printToFile(ArrayList<Result> results, String filePath, String kernel, int cvFolds, int numberOfInstances){
        File fout;
        FileOutputStream fos;
        BufferedWriter bw;
        try {
            fout = new File(filePath + "/Result_" +kernel + "_" + cvFolds + "_" + numberOfInstances + ".txt");
            fos = new FileOutputStream(fout);
            bw = new BufferedWriter(new OutputStreamWriter(fos));
            bw.write("====================================================================================");
            bw.newLine();
            bw.write("Kernel: " + kernel);
            bw.newLine();
            bw.write("Cross Validation Folds: " + cvFolds);
            bw.newLine();
            bw.write("Number of instances: " + numberOfInstances); 
            bw.newLine();
            bw.write("====================================================================================");
            bw.newLine();
            bw.newLine();
            

            for (Result result : results){
  
                   bw.write(String.format("C: %f (2^%f)", result.getC(), result.getPowC()));
                   bw.newLine();
                   bw.write(String.format("Gamma: %f (2%f)", result.getGamma(), result.getPowGamma()));
                   bw.newLine();
                   bw.write(String.format("Accuracy: %f %%", result.getAcc()*100));
                   bw.newLine();
                   bw.newLine();

                }
                
            
            bw.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occured while writing the result file");
            System.out.println("Errormessage: " + e.getMessage());
            
        } catch (IOException e) {
            System.out.println("An error occured while writing the result file");
            System.out.println("Errormessage: " + e.getMessage());
        }
    }
}
