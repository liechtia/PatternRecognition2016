package utils;

/**
 * Class for a datapoint
 *
 */
public class Datapoint {

    //label for the class
    private int label;
    
    //values for the datapoint
    private double[] values;
 
    public Datapoint(int label, double[] values) {
        this.label = label;
        this.values = values;
    }
    
    public Datapoint(int sizeOfData) {
        values = new double[sizeOfData];
    }
    
    public void addValue(int idx, double value){values[idx] = value;}
    public void setLabel(int label) {this.label = label;}
    public void setValues(double[] values) {this.values = values;}

    public int getLabel(){return label;}
    public double[] getValues(){return values;}
    
}
