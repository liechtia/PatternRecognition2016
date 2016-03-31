package main;

import java.io.FileReader;
import java.util.List;

import classifiers.mlp.MLP;
import weka.core.Instances;

public final class LauncherMLP {
	
	public static void main(String[] args) throws Exception {
		
		// Reading train set
		FileReader trainreader = new FileReader("data/small-train.arff");
		Instances train = new Instances(trainreader);
		train.setClassIndex(0);
		
		// Reading test set
		FileReader testreader = new FileReader("data/small-train.arff");
		Instances test = new Instances(testreader);
		test.setClassIndex(0);

		List<double[]> results = MLP.experimentEpochs(0.1, "10", 10, train, test);
		for (double[] r: results) {
			System.out.println((int) r[0] + "," + r[1] + "," + r[2]);	
		}
	}
}
