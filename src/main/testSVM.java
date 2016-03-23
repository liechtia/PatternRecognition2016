package main;

import java.util.ArrayList;

import classifiers.SVM;
import data.*;
import utils.Datapoint;
import utils.IO_Functions;
import utils.FeatureExtraction;

/**
 * Class to test the SVM classifier
 *
 */
public class testSVM {
        private static ArrayList<Results> results = new ArrayList<Results>();
        private static double[] cs = { 0.1, 0.2, 0.3, 0.4, 0.5, 1, 2, 4, 6, 8 };
        private static double[] gammas = { 0, 0.01, 0.02, 0.03, 0.04, 0.05, 0.06, 0.07, 0.08, 0.09, 0.1 };
	
        public static void main(String[] args) {
            String traningsFile = "data/train.csv";
            String testFile = "data/test.csv";
                    
           //read the files and convert to arff 
           String arffTrain = readDataAndConvertToArff(traningsFile);
           String arffTest = readDataAndConvertToArff(testFile); 
           
           callSvmToGetBestValues(arffTrain, arffTest);            
        }
        
        /**
         * Function init a SVM, set some parameters and than builds and evaluate the classifers 
         * @param trainFile
         * @param testFile
         */
        @SuppressWarnings("unused")
		private static  void callSVM(String trainFile, String testFile)
        {
        	DataReader reader = new DataReader(trainFile, testFile);
        	CrossValidationSet set = new CrossValidationSet(reader.getTrainingData(), reader.getTestData());
        	SVM svm = new SVM(set.getTrain(), set.getTest());  
            int c  = 1;
            int gamma = 0;
            
            //try different kernels and c and gamma values 
            svm.setParameters(svm.SVM_C_SVC, svm.KERNEL_RBF, c, gamma);
            svm.buildClassifier();
            svm.evaluateModel();
        }
        
        /**
         * iterates through n-folded cross validation set values for c and gamma as well as types of kernels to get best values
         * @param trainFile
         * @param testFile
         */
        private static  void callSvmToGetBestValues(String trainFile, String testFile)
        {        	      	
        	DataReader reader = new DataReader(trainFile, testFile);
        	CrossValidationSet finalSet = new CrossValidationSet(reader.getTrainingData(), reader.getTestData());
        	CrossValidationSet[] sets = reader.getCrossValidationData(1, 5);
        	int counter = 0;
        	
        	for (int kernel = 0; kernel < 2; kernel++){
        		for (double cValue : cs){
            		for (double gammaValue : gammas){              			          			
                        runCalculations(sets[counter], counter, kernel, cValue, gammaValue, false);
            			        			
            			if (counter == sets.length){
            				counter++;
            			}
            			else{
            				counter = 0;
            			}
            		}
        		} 
        		double bestC = 0.0;
        		double bestGamma = 0.0;
        		double bestAcc = 0.0;
        		
        		for (Results result : results){     			
        			if (result.kernel == kernel){
        				if (result.accuracy > bestAcc){
        					bestAcc = result.accuracy;
        					bestC = result.c;
        					bestGamma = result.gamma;
        				}
        			}
        		}
        		
        		runCalculations(finalSet, counter, kernel, bestC, bestGamma, true);
        	}
        	
        	//add your own folder for results here ;) 
        	Results.printToFile(results, "C:/Users/Andrea/Desktop/results.txt");
        }

		/**
		 * runs calculations for selected values
		 * @param set
		 * @param counter
		 * @param kernel
		 * @param cValue
		 * @param gammaValue
		 * @param finalRound
		 */
		private static void runCalculations(CrossValidationSet set, int counter, int kernel, double cValue,
				double gammaValue, boolean finalRound) {
			SVM svm = new SVM(set.getTrain(), set.getTest()); 
			if (kernel == 0){
				svm.setParameters(svm.SVM_C_SVC, svm.KERNEL_RBF, cValue, gammaValue);	
			}
			else{
				svm.setParameters(svm.SVM_C_SVC, svm.KERNEL_LINEAR, cValue, gammaValue);
			}
			
			svm.buildClassifier();
			double acc = svm.evaluateModel();
			
			results.add(new Results(cValue, gammaValue, acc, finalRound, kernel));
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

