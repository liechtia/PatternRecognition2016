package knn;
import knn.DataExample;

/**
 * Class to save the distance from one vector to another 
 * @author user
 *
 */
public class Distance {
    
    private double distance;
    private DataExample distanceTo;

    public Distance(DataExample distanceTo, Double distance) {
        this.distance = distance;
        this.distanceTo = distanceTo; 
    }
    
    public double getDistance() { return this.distance;}
    public void setDistance(double distance) { this.distance = distance;}
    
    public DataExample getDistanceTo() { return this.distanceTo;}
    public void setDistanceTo(DataExample distanceTo) { this.distanceTo = distanceTo;} 
    
 

}
