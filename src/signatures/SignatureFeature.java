package signatures;

public class SignatureFeature{
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
}	
