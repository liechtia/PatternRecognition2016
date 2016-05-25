package classifiers.mlp;

import java.io.FileWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;

import weka.classifiers.Classifier;
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
	
	public static MLPResult runSeed(double learningRate, String hiddenLayer, int epochs,
			 Instances train, Instances test, int seed) throws Exception {
		// Create the weka mlp
		MultilayerPerceptron mlp = new MultilayerPerceptron();
		
		// Set parameters

		mlp.setSeed(seed);
	
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
	
	 public static List<MLPResult> run(double learningRate, String hiddenLayer, int epochs,
			  Instances data, int folds, boolean randomize, int seed)
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
		MLPResult r = MLP.runSeed(learningRate, hiddenLayer, epochs, train, test, seed);
		results.add(r);
		}
		
		// save result
		
		return results;
		}
			
	/**
	 * Experiment with random initialization of weights.
	 */
	 public static Map<Integer, List<MLPResult>> experimentRandomWeights(double learningRate,
			int hiddenLayer, int epochs, Instances data, int folds, int[] seed) throws Exception {
		Map<Integer,List<MLPResult>> results = new TreeMap<Integer,List<MLPResult>>();
		int start = seed[0]; //0.1 
		int stop = seed[1];  //1.0
		int increment = seed[2]; //0.1
		
		Random rand = new Random(0);
		data.randomize(rand);
		data.stratify(folds);
		
		for (int curseed = start; curseed <= stop; curseed += increment) {
			List<MLPResult> nResult = MLP.run(learningRate, String.valueOf(hiddenLayer), epochs, data,
											  folds, false, curseed);
			results.put(curseed, nResult);
		}
		
		// Save result
		List<String> lines = new ArrayList<String>();
		lines.add("# Experiment with cross-validation");
		lines.add("");
		lines.add("# Variable: learning rate");
		lines.add("# Epochs: " + epochs);
		lines.add("# Data set size: " + data.numInstances());
		lines.add("# Folds: " + folds);
		lines.add("");
		lines.add("nodes,error");
		saveResult("RandomWeights-test", lines, results);
		
		return results;
	}
	

	/**
	 * Experiment with different learning rates.
	 */
	public static Map<Double, List<MLPResult>> experimentLearningRate(Double[] learningRate,
			int hiddenLayer, int epochs, Instances data, int folds) throws Exception {
		Map<Double,List<MLPResult>> results = new TreeMap<Double,List<MLPResult>>();
		double start = learningRate[0]; //0.1 
		double stop = learningRate[1];  //1.0
		double increment = learningRate[2]; //0.1
		
		Random rand = new Random(0);
		data.randomize(rand);
		data.stratify(folds);
		
		for (double lrate = start; lrate <= stop; lrate += increment) {
			List<MLPResult> nResult = MLP.run(lrate, String.valueOf(hiddenLayer), epochs, data,
											  folds, false);
			results.put(lrate, nResult);
		}
		
		// Save result
		List<String> lines = new ArrayList<String>();
		lines.add("# Experiment with cross-validation");
		lines.add("");
		lines.add("# Variable: learning rate");
		lines.add("# Epochs: " + epochs);
		lines.add("# Data set size: " + data.numInstances());
		lines.add("# Folds: " + folds);
		lines.add("");
		lines.add("nodes,error");
		saveResultDouble("learningRate-best", lines, results);
		
		return results;
	}
	
	/**
	 * Experiment with different number of nodes in the hidden layer.
	 * In addition, it validates using stratified cross validation.
	 */
	public static Map<Integer,List<MLPResult>> experimentHiddenLayer(double learningRate,
			int[] hiddenLayers, int epochs, Instances data, int folds) throws Exception {
		Map<Integer,List<MLPResult>> results = new TreeMap<Integer,List<MLPResult>>();
		int start = hiddenLayers[0];
		int stop = hiddenLayers[1];
		int increment = hiddenLayers[2];
		
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
	
	/**
	 * Experiment with growing number of epochs. It trains the an mlp classifier
	 * and store the model everytime an epoch has passed.
	 * In addition, it validates using against a given test set.
	 */
	public static List<double[]> experimentEpochs(double learningRate,
			String hiddenLayer, int epochs, Instances train, Instances test) throws Exception {
		// Create the weka mlp
		MultilayerPerceptronCustom mlp = new MultilayerPerceptronCustom();
		
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
		
		// Validate
		List<double[]> results = new ArrayList<double[]>();
		for (int i = 1; i <= epochs; i++) {
			Classifier c = Helpers.loadClassifier("mlp/temp/" + i + ".model");
			Evaluation eval = new Evaluation(train);
			
			eval.evaluateModel(c, train);
			double trainError = eval.errorRate();
			
			eval.evaluateModel(c, test);
			double testError = eval.errorRate();
			
			results.add(new double[]{i,trainError,testError});
		}
		
		// Save result
		List<String> lines = new ArrayList<String>();
		lines.add("# Experiment with validation on train set and on a test set");
		lines.add("");
		lines.add("# Variable: number of training epochs passed from training start");
		lines.add("# Learning rate: " + learningRate);
		lines.add("# Nodes in the hidden layer: " + hiddenLayer);
		lines.add("# Epochs: " + epochs);
		lines.add("# Train set size: " + train.numInstances());
		lines.add("# Test set size: " + test.numInstances());
		lines.add("");
		lines.add("epochs,trainerror,testerror");
		saveResultEpochs("epochs", lines, results);
		
		return results;
	}
	
	
	/**
	 * Experiment with the best parameters in the test set
	 */
	public static void experimentTestSet(double learningRate,
			String hiddenLayer, int epochs, Instances train, Instances test, int seed) throws Exception {
		// Create the weka mlp
		MultilayerPerceptronCustom mlp = new MultilayerPerceptronCustom();
		
		// Set parameters
		mlp.setSeed(seed);
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
		
		// Validate
	
		Evaluation eval = new Evaluation(train);
		
		eval.evaluateModel(mlp, train);
		double trainError = eval.errorRate();
		
		eval.evaluateModel(mlp, test);
		double testError = eval.errorRate();
		
		
		FileWriter f1 = new FileWriter("results/mlp_results.txt");
		String newLine = System.getProperty("line.separator");

		for (int i = 0; i < test.numInstances(); i++) {
			double pred = mlp.classifyInstance(test.instance(i));
			f1.write(i +", "+ (int) pred + newLine);
			//System.out.print("ID: " + test.instance(i).value(0));
			System.out.println(i +", "+ (int) pred);
			}
		
		System.out.println("Train error: " + trainError);
		System.out.println("Test error: " + testError);
	}
	
	// Helper methods

	private static void saveResultDouble(String experiment, List<String> lines,
			   Map<Double,List<MLPResult>> results) throws Exception {
		String fileName = experiment;
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy-HHmmss");
		fileName += "-" + sdf.format(date);
		
		for (Entry<Double, List<MLPResult>> entry: results.entrySet()) {
		List<MLPResult> nResult = entry.getValue();
		for (MLPResult r: nResult) {
		lines.add(entry.getKey() + "," + r.getErrorRate());	
		}
		}
		Path file = Paths.get("mlp/experiments/" + fileName + ".csv");
		Files.write(file, lines, Charset.forName("UTF-8"));
		}
			
	private static void saveResult(String experiment, List<String> lines,
								   Map<Integer,List<MLPResult>> results) throws Exception {
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
	
	private static void saveResultEpochs(String experiment, List<String> lines,
								   List<double[]> results) throws Exception {
		String fileName = experiment;
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy-HHmmss");
		fileName += "-" + sdf.format(date);
		
		for (double[] r: results) {
			lines.add((int) r[0] + "," + r[1] + "," + r[2]);	
		}
		Path file = Paths.get("mlp/experiments/" + fileName + ".csv");
		Files.write(file, lines, Charset.forName("UTF-8"));
	}
	
	private static void saveModel() {
		//TODO:
	}
}
