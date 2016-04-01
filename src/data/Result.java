package data;

public class Result {

    private double c;
    private double gamma;
    private double powC;
    private double powGamma;
    private double acc;
    

    public Result(double c, double powC, double gamma, double powGamma, double acc) {
        
        this.c=c;
        this.gamma = gamma;
        this.acc = acc;
        this.powC = powC;
        this.powGamma = powGamma; 
    
        
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
