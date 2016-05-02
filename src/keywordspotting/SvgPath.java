package keywordspotting;

public class SvgPath {

    private int[] xValues;
    private int[] yValues;
    private int height;
    private int width; 
    private int startX;
    private int startY;
 
    
    public int[] getXValues() { return this.xValues;}
    public int[] getYValues() { return this.yValues;}
    public int getHeight() {return this.height;}
    public int getWidth() { return this.width;}
    public int getStartX() {return this.startX;}
    public int getStartY() { return this.startY;}
    
    public SvgPath(int height, int width, int[] xValues, int[] yValues) {
        this.height = height;
        this.width = width;
        this.xValues = xValues;
        this.yValues = yValues;
    }
    
    public void normValues(int startX, int startY)
    {
        this.startX = startX;
        this.startY = startY; 
        
        for(int i = 0; i < xValues.length; i++)
        {
            xValues[i] = xValues[i]-startX;
            yValues[i] = yValues[i] -startY;
        }
    }
 

}
