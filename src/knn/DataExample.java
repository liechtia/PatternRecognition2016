package knn;

public class DataExample {

    private double[] values;
    private int label;
    private int cluster =-1; 
    
    public DataExample(int label, double[] values) {
        this.label= label;
        this.values = values; 

    }
    
    public double[] getValues() { return values;} 
    public int getLabel() { return label; }
    
    
    public void setCluster(int cluster) { this.cluster = cluster;}
    public int getCluster() { return this.cluster;}
    
    
   

}
