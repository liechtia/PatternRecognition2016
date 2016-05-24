package main;

import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import classifiers.svm.DataReader;
import classifiers.svm.FindBestParameters;
import classifiers.svm.Result;
import classifiers.svm.SVM;
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

	
        public static void main(String[] args) throws IOException {
            String traningsFile = "data/trainAll.csv";
            String testFile = "data/test.csv";
            String classifyFile = "data/mnist_test.csv"; 
                    
           //read the files and convert to arff 
           String arffTrain = readDataAndConvertToArff(traningsFile, true);
           String arffTest = readDataAndConvertToArff(testFile, true); 
           
           //calcualtes the best parameters for the svm and tests it directly on the test set
           // did this already found out --> c=2^2.5 and gamma = 2^-9.75 are the best values
           //getBestParamtersForDataset(arffTrain, arffTest);
           
           double c = Math.pow(2, 2.5);
           double gamma = Math.pow(2, -9.75);
           classify(arffTrain, classifyFile,c, gamma);
           
        	
        }
        
        private static void getBestParamtersForDataset(String arffTrain, String arffTest)
        {
            try {
                callSvmToGetBestValues(arffTrain, arffTest, SVM.KERNEL_RBF_STRING);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        private static void classify(String arffTrain, String classifyFile, double c, double gamma) throws IOException
        {
            String arffClassify = readDataAndConvertToArff(classifyFile, false); 
            
            DataReader reader = new DataReader(arffTrain, arffClassify);
            
            Instances train = reader.getTrainingData();
            Instances classify = reader.getTestData(); 
            
            SVM svm = new SVM(train, classify);  
            svm.setParameters(svm.SVM_C_SVC,  SVM.KERNEL_RBF_STRING, c, gamma);
            svm.buildClassifier();
            
            FileWriter f1 = new FileWriter("results/svm_results.txt");
            String newLine = System.getProperty("line.separator");
            
            for (int i = 0; i < classify.numInstances(); i++) {
                double pred = 0;
                try {
                    pred = svm.getclassifier().classifyInstance(classify.instance(i));
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                
                f1.write(i +", "+ (int) pred + newLine);
            }
            
            f1.close();
            
        }
        

        /**
         * iterates through n-folded cross validation set values for c and gamma  to get best values
         * @param trainFile - Path to the file with the training examples
         * @param testFile - Path to the file with the testing examples
         * @param kernel - Kernel to use 
         * @throws IOException 
         */
        private static  void callSvmToGetBestValues(String trainFile, String testFile, String kernel ) throws IOException
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
            
    		

    		for (int i = 0; i < test.numInstances(); i++) {
    			double pred = 0;
				try {
					pred = svm.getclassifier().classifyInstance(test.instance(i));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			
    			//System.out.print("ID: " + test.instance(i).value(0));
    			//System.out.println(", predicted: " + test.classAttribute().value((int) pred));
    			}
            //write final results in a file 
            //IO_Functions.printFinalResult(eval, "svm/", kernel, result.getPowC(), result.getPowGamma());
        	
        
        }

		
        /**
         * Read the data and convert it to an arff file 
         * @param fileName
         * @return
         */
        private static String readDataAndConvertToArff(String fileName, boolean labeled)
        {
            int imageLength = 28;
            int imageHeight = 28;
            int size = 28*28;
            
            String file = fileName;
            ArrayList<Datapoint> datapoints = IO_Functions.readCsvFile(file, 0, ",", labeled, size ); 
      
            int[] reducesValues;
            for(int i = 0; i < datapoints.size(); i++)
            {
                
                if(datapoints.get(i).getValues().length < size)
                {
                    for(int j = datapoints.get(i).getValues().length; j <= size; j++)
                    {
                        datapoints.get(i).addValue(j, 0);
                    }
                 
                }
                
                
                
                //reduce the features for the image data 
                reducesValues = FeatureExtraction.extracImageFeatures(imageLength, imageHeight, datapoints.get(i).getValues());
               datapoints.get(i).setValues(reducesValues);
            }
            
            return IO_Functions.convertToArffFile(fileName, "svm", datapoints); 
            
        }

    }

