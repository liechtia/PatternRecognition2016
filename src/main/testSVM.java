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

           System.out.println("Test RBF Kernel:");
           callSvmToGetBestValues(arffTrain, arffTest, SVM.KERNEL_RBF_STRING);
           
           System.out.println("");
           System.out.println("Test Linear Kernel:");
           callSvmToGetBestValues(arffTrain, arffTest, SVM.KERNEL_LINEAR_STRING);
          

        }
        

        /**
         * iterates through n-folded cross validation set values for c and gamma  to get best values
         * @param trainFile - Path to the file with the training examples
         * @param testFile - Path to the file with the testing examples
         * @param kernel - Kernel to use 
         */
        private static  void callSvmToGetBestValues(String trainFile, String testFile, String kernel )
        {        	      	
        	DataReader reader = new DataReader(trainFile, testFile);
        	Instances instances  = reader.getKTrainData(1000);
        	FindBestParameters cv = new FindBestParameters(new Instances(instances), kernel);
        	Result result = cv.doCV(10);
        	
        	System.out.println("Test whole set with best parameters...this may take a while");
        	 
        	//suppress output of the classifier 
        	PrintStream originalStream = System.out;
            PrintStream dummyStream    = new PrintStream(new OutputStream(){
                 public void write(int b) {
                 }
             });
             System.setOut(dummyStream);
             
             
        	//test the best parameters on the whole set 
        	Instances train = reader.getTrainingData();
        	Instances test = reader.getTestData(); 
            SVM svm = new SVM(train, test);  
            double c  = result.getC();
            double gamma = result.getGamma();
            svm.setParameters(svm.SVM_C_SVC, kernel, c, gamma);
            svm.buildClassifier();
            System.setOut(originalStream);
            Evaluation eval = svm.evaluateModel();
            
            //write final results in a file 
            IO_Functions.printFinalResult(eval, "svm/", kernel, result.getPowC(), result.getPowGamma());
        	
        
        }

		
        /**
         * Read the data and convert it to an arff file 
         * @param fileName
         * @return
         */
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

