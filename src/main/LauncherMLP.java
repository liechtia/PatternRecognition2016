package main;

import java.io.FileReader;

import classifiers.mlp.MLP;
import weka.core.Instances;

public final class LauncherMLP {
	
	public static void main(String[] args) throws Exception {
		
		// Reading training arff file
		FileReader trainreader = new FileReader("data/small-train.arff");
		Instances data = new Instances(trainreader);
		data.setClassIndex(0);
		
		MLP.expHiddenLayer(0.1, 1, data, 4, new int[]{10, 20, 5});
	}
}
