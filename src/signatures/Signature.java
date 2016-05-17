package signatures;

import java.util.List;

import utils.DTWFeatureVector;
import utils.FeatureVector;
import utils.FtVector;

public class Signature implements DTWFeatureVector {
	public int user;
	public int number;
	public List<FtVector> features;
	
	@Override
	public List<FtVector> getFeatureVectors() {
		return features;
	}
}
