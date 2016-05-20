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
	
    public double[][] array;
    public double[][] formFeatureArray()
    {        
        array = new double[features.size()][features.get(1).getAllFeatures().length];
        
        for(int i = 0;i  < features.size(); i++)
        {
            FtVector fv = this.features.get(i);
            double[] x = fv.getAllFeatures();
            array[i] = x;          
        }
        
        return array;
    }
}
