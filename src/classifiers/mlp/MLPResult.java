package classifiers.mlp;

import weka.classifiers.Classifier;
import weka.classifiers.functions.MultilayerPerceptron;

/**
 * Used to store the result of an mlp training and evaluation.
 * It stores the following:
 * - errorRate: the error rate of the evaluation;
 * - summary: a summary string of the evaluation;
 * - model: the mlp model created by the training.
 */
public class MLPResult {
	private double errorRate;
	private String summary;
	private Classifier model;
	
	public MLPResult(double errorRate, String summary, Classifier model) {
		this.errorRate = errorRate;
		this.summary = summary;
		this.model = model;
	}
	
	public double getErrorRate() {
		return errorRate;
	}

	public String getSummary() {
		return summary;
	}

	public Classifier getModel() {
		return model;
	}
}
