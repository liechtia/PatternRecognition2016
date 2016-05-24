package signatures;

import java.util.List;

import utils.DTWFeatureVector;
import utils.FeatureVector;
import utils.FtVector;

/**
 * class to represent a signature with its user, number and feature vector
 */
public class Signature implements DTWFeatureVector {
	public int user;
	public int number;
	public List<FtVector> features;
    public double[][] array;
	
	/* (non-Javadoc)
	 * @see utils.DTWFeatureVector#getFeatureVectors()
	 */
	@Override
	public List<FtVector> getFeatureVectors() {
		return features;
	}
	
    /**
     * @return
     * returns the signature as a double array
     */
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
