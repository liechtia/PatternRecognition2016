package signatures;

import java.util.ArrayList;
import java.util.List;

import utils.DTWFeatureVector;
import utils.FeatureVector;
import utils.FtVector;

public class SignatureFeature implements FtVector{
    public double t;
    public double x;
    public double y;
    public double pressure;
    public double penup;
    public double azimuth;
    public double inclination;
    public double vx;
    public double vy;
    
    public double[] getAllFeatures() {
        //double[] allFeatures = new double[]{t, x, y, pressure, penup, azimuth, inclination, vx, vy};
        double[] allFeatures = new double[]{x, y, pressure, vx, vy};
        return allFeatures;
	}

	@Override
	public void normFeatures() {
		// unused in signature verification
	}
	
	@Override
	public ArrayList<Double> getFeatures() {
		// unused in signature verification
		return null;
	}
}	
