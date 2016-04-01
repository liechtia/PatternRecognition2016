package main;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import classifiers.SVM;
import data.*;
import utils.Datapoint;
import utils.IO_Functions;
import utils.FeatureExtraction;
import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Class to test the SVM classifier
 *
 */
public class testSVM {

	
        public static void main(String[] args) {
            String traningsFile = "data/trainAll.csv";
            String testFile = "data/test.csv";
                    
           //read the files and convert to arff 
           String arffTrain = readDataAndConvertToArff(traningsFile);
           String arffTest = readDataAndConvertToArff(testFile); 
           
           double startTime = System.currentTimeMillis();
           callSvmToGetBestValues(arffTrain, arffTest);
           //callSVM(arffTrain, arffTest); 
           double endTime = System.currentTimeMillis();
           
           System.out.println((endTime-startTime)/1000.0/60);
        }
        

        /**
         * iterates through n-folded cross validation set values for c and gamma as well as types of kernels to get best values
         * @param trainFile
         * @param testFile
         */
        private static  void callSvmToGetBestValues(String trainFile, String testFile)
        {        	      	
        	DataReader reader = new DataReader(trainFile, testFile);
        	Instances instances  = reader.getKTrainData(1000);
        	FindBestParameters cv = new FindBestParameters(new Instances(instances), SVM.KERNEL_RBF_STRING);
        	Result result = cv.doCV(10);
        	
        	System.out.println("Test whole set with best parameters...this may take a while");
        	 PrintStream originalStream = System.out;

             PrintStream dummyStream    = new PrintStream(new OutputStream(){
                 public void write(int b) {
                     //NO-OP
                 }
             });
             System.setOut(dummyStream);
        	//test the best parameters on the whole set 
        	Instances train = reader.getTrainingData();
        	Instances test = reader.getTestData(); 
            SVM svm = new SVM(train, test);  
            double c  = result.getC();
            double gamma = result.getGamma();
            
           
            svm.setParameters(svm.SVM_C_SVC, svm.KERNEL_RBF_STRING, c, gamma);
            svm.buildClassifier();
            System.setOut(originalStream);
            Evaluation eval = svm.evaluateModel();
            
            IO_Functions.printFinalResult(eval, "data/", "RBF", result.getPowC(), result.getPowGamma());
        	
        
        }

		
        
        private static String readDataAndConvertToArff(String fileName)
        {
            int imageLength = 28;
            int imageHeight = 28;
            
            String file = fileName;
            ArrayList<Datapoint> datapoints = IO_Functions.readCsvFile(file, 0, ",");
            
      
            int[] reducesValues;
            for(int i = 0; i < datapoints.size(); i++)
            {
                //reduce the features for the image data 
                reducesValues = FeatureExtraction.extracImageFeatures(imageLength, imageHeight, datapoints.get(i).getValues());
               datapoints.get(i).setValues(reducesValues);
            }
            
            return IO_Functions.convertToArffFile(fileName, "svm", datapoints); 
            
        }

    }

