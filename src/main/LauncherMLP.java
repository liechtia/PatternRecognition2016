package main;

import java.io.FileReader;
import java.util.Map;

import classifiers.mlp.MLP;
import classifiers.mlp.MLPResult;
import weka.core.Instances;

public final class LauncherMLP {
	
	public static void main(String[] args) throws Exception {
		
		// Reading train set
		FileReader trainreader = new FileReader("data/original-train.arff");
		Instances train = new Instances(trainreader);
		train.setClassIndex(0);
		
		// Reading test set
		FileReader testreader = new FileReader("data/original-test.arff");
		Instances test = new Instances(testreader);
		test.setClassIndex(0);

		Map<Integer,MLPResult> results = MLP.experimentEpochs(0.1, "80", 60, train, test);
		for (Map.Entry<Integer,MLPResult> entry: results.entrySet()) {
			MLPResult nResult = entry.getValue();
			System.out.println(entry.getKey() + " - " + nResult.getErrorRate());
		}
	}
}
