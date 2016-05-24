package main;

import java.io.IOException;

import keywordspotting.PreprocessingGaussian;

public class MainGaussianFeaturesExtraction {

	public static void main(String[] args) throws IOException {
		
		PreprocessingGaussian p = new PreprocessingGaussian(15, 100);
		p.process("KeywordSpottingData_Test/wordimages/final_clipped",
				"KeywordSpottingData_Test/wordimages/smoothing",
				"KeywordSpottingData_Test/wordimages/edges"
				);
		p.extractFeatures("KeywordSpottingData_Test/wordimages/smoothing",
				"KeywordSpottingData_Test/wordimages/edges",
				"KeywordSpottingData_Test/features/gaussian_features.txt"
				);
	}
}
