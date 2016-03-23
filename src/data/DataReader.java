package data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import weka.core.Debug.Random;
import weka.core.Instances;

/**
 * class to retrieve data sets
 */
public class DataReader {

    private String trainFile;
    private String testFile;
    private Instances train;
    private Instances test;
    
    public DataReader(String trainFile, String testFile) {
        this.trainFile = trainFile;
        this.testFile = testFile;
        
        readArffFile(); 
    }
    
    /**
     * returns complete set of training data
     * @return
     */
    public Instances getTrainingData(){
    	return train;
    }
    
    /**
     * returns final test data
     * @return
     */
    public Instances getTestData(){
    	return test;
    }
    
    /**
     * function to return cross validation sets
     * @param seedValue
     * @param foldNumber
     * @return
     */
    public CrossValidationSet[] getCrossValidationData(int seedValue, int foldNumber){
    	int folds = foldNumber;
    	int seed = seedValue;
    	Random rand = new Random(seed);
    	Instances randData = new Instances(train);
    	randData.randomize(rand);
    	CrossValidationSet[] sets = new CrossValidationSet[folds];
    	
    	for (int n = 0; n < folds; n++) {
    	    Instances trainSet = randData.trainCV(folds, n);
    	    Instances testSet = randData.testCV(folds, n);
    	    sets[n] = new CrossValidationSet(trainSet, testSet);
    	}
    	
    	return sets;
    }
	
    /**
     * Function to read the arff file 
     */
    private void readArffFile()
    {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(trainFile));
            train = new Instances(reader);
            reader.close();
            
            reader = new BufferedReader(new FileReader(testFile));
            test = new Instances(reader);
            
            train.setClassIndex(train.numAttributes() -1); //class attribute is last index      
            test.setClassIndex(train.numAttributes()-1);
            reader.close();
            
        } catch ( IOException e) {
            System.out.println("An error occured while reading the arff file");
            System.out.println(e.getMessage());
            System.exit(-1);
        }  
    }
}
