package classifiers.mlp;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import weka.classifiers.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instances;

public final class MLP {

	/**
	 * Build, train and evaluate an mlp. The evaluation is performed against a given test set.
	 * 
	 * @param learningRate Learning rate. This is the c parameter of the algorithm seen in class.
	 * @param hiddenLayer A string with a comma seperated list of numbers; each number represents
	 * the number of nodes of a distinct hidden layer. E.g., "10,40,10" creates a network with
	 * 3 hidden layers respectively composed by 10, 40 and 10 nodes.
	 * @param epochs Number of training epochs.
	 * @param train The train data set.
	 * @param test The test data set.
	 * @return The error rate of the evaluation against the test set.
	 */
	public static MLPResult run(double learningRate, String hiddenLayer, int epochs,
							 Instances train, Instances test) throws Exception {
		// Create the weka mlp
		MultilayerPerceptron mlp = new MultilayerPerceptron();
		
		// Set parameters
		
		// When validating (with Evaluation) it stops the validation if this nr
		// of consecutive errors is reached
		mlp.setValidationThreshold(100);
		mlp.setReset(false); // Don't allow reset
		mlp.setMomentum(0.0);
		mlp.setLearningRate(learningRate);
		mlp.setHiddenLayers(hiddenLayer);
		mlp.setTrainingTime(epochs);
		
		// Train the mlp
		mlp.buildClassifier(train);
		
		// Evaluate
		Evaluation eval = new Evaluation(train);
		eval.evaluateModel(mlp, test);
		
		// Print result
		System.out.println(eval.toSummaryString());
		
		return new MLPResult(eval.errorRate(), eval.toSummaryString(), mlp);
	}
	
	/**
	 * Run and cross-validate.
	 */
	public static List<MLPResult> run(double learningRate, String hiddenLayer, int epochs,
									  Instances data, int folds, boolean randomize)
											  throws Exception {
		List<MLPResult> results = new ArrayList<MLPResult>();
		
		if (randomize) {
			Random rand = new Random(0);
			data.randomize(rand);
			data.stratify(folds);
		}
		
		for (int f = 0; f < folds; f++) {
			Instances train = data.trainCV(4, f);
			Instances test = data.testCV(4, f);			
			MLPResult r = MLP.run(learningRate, hiddenLayer, epochs, train, test);
			results.add(r);
		}
		
		// save result
		
		return results;
	}
	
	/**
	 * Experiment with different number of nodes in the hidden layer.
	 * Stratified cross validation.
	 */
	public static Map<Integer,List<MLPResult>> expHiddenLayer(double learningRate, int epochs,
															  Instances data, int folds,
															  int[] hiddenLayer) throws Exception {
		Map<Integer,List<MLPResult>> results = new TreeMap<Integer,List<MLPResult>>();
		int start = hiddenLayer[0];
		int stop = hiddenLayer[1];
		int increment = hiddenLayer[2];
		
		Random rand = new Random(0);
		data.randomize(rand);
		data.stratify(folds);
		
		for (int nodes = start; nodes <= stop; nodes += increment) {
			List<MLPResult> nResult = MLP.run(learningRate, String.valueOf(nodes), epochs, data,
											  folds, false);
			results.put(nodes, nResult);
		}
		
		// Save result
		List<String> lines = new ArrayList<String>();
		lines.add("# Experiment with cross-validation");
		lines.add("");
		lines.add("# Variable: number of nodes in hidden layer");
		lines.add("# Learning rate: " + learningRate);
		lines.add("# Epochs: " + epochs);
		lines.add("# Data set size: " + data.numInstances());
		lines.add("# Folds: " + folds);
		lines.add("");
		lines.add("nodes,error");
		saveResult("hiddenlayer", lines, results);
		
		return results;
	}
	
	// Helper methods
	
	private static void saveResult(String experiment, List<String> lines,
								   Map<Integer,List<MLPResult>> results)
			throws Exception {
		String fileName = experiment;
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy-HHmmss");
		fileName += "-" + sdf.format(date);
		
		for (Map.Entry<Integer,List<MLPResult>> entry: results.entrySet()) {
			List<MLPResult> nResult = entry.getValue();
			for (MLPResult r: nResult) {
				lines.add(entry.getKey() + "," + r.getErrorRate());	
			}
		}
		Path file = Paths.get("mlp/experiments/" + fileName + ".csv");
		Files.write(file, lines, Charset.forName("UTF-8"));
	}
	
//	private static void saveModel() {
//		
//	}
}
