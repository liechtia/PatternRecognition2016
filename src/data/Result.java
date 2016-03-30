package data;

public class Result {

    private double c;
    private double gamma;
    private double powC;
    private double powGamma;
    private double acc;
    private int numberOfInstances;
    private boolean cv;
    private int cvFolds;
    
    public Result(double c, double powC, double gamma, double powGamma, double acc, int numberOfInstances) {
        
        this.c=c;
        this.gamma = gamma;
        this.acc = acc;
        this.numberOfInstances = numberOfInstances;
        
        this.powC = powC;
        this.powGamma = powGamma; 
        this.cv = false;
        this.cvFolds = 0; 
        
    }
    
    public Result(double c, double powC, double gamma, double powGamma, double acc, int numberOfInstances, int cvFolds)
    {
        this.c=c;
        this.gamma = gamma;
        this.powC = powC;
        this.powGamma =powGamma; 
        this.acc = acc;
        this.numberOfInstances = numberOfInstances;
        
        this.cv = true;
        this.cvFolds = cvFolds; 
    }

    public double getAcc() {
       return this.acc;
    }

    public double getC() {
        // TODO Auto-generated method stub
       return this.c;
    }

    public double getGamma()
    {
        return this.gamma;
    }
    
    public double getPowGamma(){
        return this.powGamma;
    }
    
    public double getPowC()
    {
        return this.powC;
    }
    
}
