package main;

import java.io.FileReader;
import java.util.List;

import classifiers.mlp.MLP;
import weka.core.Instances;


/**
 * This is the entry point for running the MLP task.
 */

public final class MainMLP {
	
	public static void main(String[] args) throws Exception {
		
		// Reading train set
		FileReader trainreader = new FileReader("data/trainAll_mlp.csv.arff");
		Instances train = new Instances(trainreader);
		train.setClassIndex(train.numAttributes()-1);
		
		// Reading test set
		FileReader testreader = new FileReader("data/mnist_test_mlp.csv.arff");
		Instances test = new Instances(testreader);
		test.setClassIndex(test.numAttributes()-1);

		//List<double[]> results = MLP.experimentEpochs(0.1, "10", 10, train, test);
		//MLP.experimentLearningRate(new Double[]{0.1, 1.0, 0.1}, 80, 110, train, 4);
		//MLP.experimentRandomWeights(0.2, 80, 10, train, 4, new int[]{1, 10, 1});
		MLP.experimentTestSet(0.1, "80", 110, train, test, 4);
		//for (double[] r: results) {
		//	System.out.println((int) r[0] + "," + r[1] + "," + r[2]);	
		//}
	}
}
