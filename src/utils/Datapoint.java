package utils;

/**
 * Class for a datapoint
 *
 */
public class Datapoint {

    //label for the class
    private int label;
    
    //values for the datapoint
    private int[] values;
 
    public Datapoint(int label, int[] values) {
        this.label = label;
        this.values = values;
    }
    
    public Datapoint(int sizeOfData) {
        values = new int[sizeOfData];
    }
    
    public void addValue(int idx, int value){values[idx] = value;}
    public void setLabel(int label) {this.label = label;}
    public void setValues(int[] values) {this.values = values;}

    public int getLabel(){return label;}
    public int[] getValues(){return values;}
    
}
