package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

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
            
            bw.write("@attribute class{");
            for(int i = 0; i < classes.size(); i++)
            {
                bw.write(classes.get(i) + ",");
            }
            bw.write("}");
            
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
}
