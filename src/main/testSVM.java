package main;

import java.util.ArrayList;

import classifiers.SVM;
import utils.Datapoint;
import utils.IO_Functions;
import utils.FeatureExtraction;

/**
 * Class to test the SVM classifier
 *
 */
public class testSVM {

        public static void main(String[] args) {
            String traningsFile = "data/train.csv";
            String testFile = "data/test.csv";
                    
           //read the files and convert to arff 
           String arffTrain = readDataAndConvertToArff(traningsFile);
           String arffTest = readDataAndConvertToArff(testFile); 
           
            
            callSVM(arffTrain, arffTest);
            
        }
        
        /**
         * Function init a SVM, set some parameters and than builds and evaluate the classifers 
         * @param trainFile
         * @param testFile
         */
        private static  void callSVM(String trainFile, String testFile)
        {
            SVM svm = new SVM(trainFile, testFile); 
            int c  = 1;
            int gamma = 0;
            
            //try different kernels and c and gamma values 
            svm.setParameters(svm.SVM_C_SVC, svm.KERNEL_RBF, c, gamma);
            svm.buildClassifier();
            svm.evaluateModel();
        }
        
        private static String readDataAndConvertToArff(String fileName)
        {
            int imageLength = 28;
            int imageHeight = 28;
            
            String file = fileName;
            ArrayList<Datapoint> datapoints = IO_Functions.readCsvFile(file, 0, ",");
            
      
            double[] reducesValues;
            for(int i = 0; i < datapoints.size(); i++)
            {
                //reduce the features for the image data 
                reducesValues = FeatureExtraction.extracImageFeatures(imageLength, imageHeight, datapoints.get(i).getValues());
                datapoints.get(i).setValues(reducesValues);
            }
            
            return IO_Functions.convertToArffFile(fileName, "svm", datapoints); 
            
        }

    }

