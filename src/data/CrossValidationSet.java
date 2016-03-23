package data;

import weka.core.Instances;

/**
 * class to store the different sets of training and test data
 */
public class CrossValidationSet{
	private Instances train;
	private Instances test;
	
    public CrossValidationSet(Instances train, Instances test) {
        this.train = train;
        this.test = test;
    }
	
	/**
	 * returns training set
	 * @return
	 */
	public Instances GetTrain(){
		return train;
	}
	
	/**
	 * returns test set
	 * @return
	 */
	public Instances GetTest(){
		return test;
	}
}
