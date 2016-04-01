package classifiers;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;

import weka.classifiers.Evaluation;
import weka.classifiers.functions.LibSVM;
import weka.core.Instances;
import weka.core.SelectedTag;

/**
 * Class for the SVM classifiers
 *
 */
public class SVM {

    //constants for the different svm types and kernels 
    public final SelectedTag SVM_C_SVC = new SelectedTag(LibSVM.SVMTYPE_C_SVC, LibSVM.TAGS_SVMTYPE);
    public final SelectedTag SVM_EPSILON_SVR = new SelectedTag(LibSVM.SVMTYPE_EPSILON_SVR, LibSVM.TAGS_SVMTYPE);
    public final SelectedTag SVM_ONE_CLASS= new SelectedTag(LibSVM.SVMTYPE_ONE_CLASS_SVM, LibSVM.TAGS_SVMTYPE);
    public final SelectedTag SVM_NU_SVC = new SelectedTag(LibSVM.SVMTYPE_NU_SVC, LibSVM.TAGS_SVMTYPE);
    public final SelectedTag SVM_NU_SVR = new SelectedTag(LibSVM.SVMTYPE_NU_SVR, LibSVM.TAGS_SVMTYPE);
    
    
    public static final String KERNEL_RBF_STRING = "RBF";
    public static final String KERNEL_POLYNOMIAL_STRING = "Polynomial";
    public static final String KERNEL_LINEAR_STRING = "Linear";
    public static final String KERNEL_SIGMOID_STRING = "Sigmoid";
    
    private final SelectedTag  KERNEL_RBF = new SelectedTag(LibSVM.KERNELTYPE_RBF, LibSVM.TAGS_KERNELTYPE);
    private final SelectedTag  KERNEL_POLYNOMIAL = new SelectedTag(LibSVM.KERNELTYPE_POLYNOMIAL, LibSVM.TAGS_KERNELTYPE);
    private final SelectedTag  KERNEL_LINEAR = new SelectedTag(LibSVM.KERNELTYPE_LINEAR, LibSVM.TAGS_KERNELTYPE);
    private final SelectedTag  KERNEL_SIGMOID = new SelectedTag(LibSVM.KERNELTYPE_SIGMOID, LibSVM.TAGS_KERNELTYPE);   
    
    
    
    private Instances train;
    private Instances test;
    private LibSVM svm;
    private String kernel; 
    
    
    
    public SVM(Instances trainInstances, Instances testInstances) {
    	train = trainInstances;
    	test = testInstances;
        svm = new LibSVM(); 
    }
    
    private SelectedTag selectKernel(String kernel)
    {
       
        if(kernel.equals(KERNEL_LINEAR_STRING))
            return KERNEL_LINEAR;
        
        if(kernel.equals(KERNEL_POLYNOMIAL_STRING))
            return KERNEL_POLYNOMIAL;
        
        if(kernel.equals(KERNEL_RBF_STRING))
            return KERNEL_RBF;
        
        
        if(kernel.equals(KERNEL_SIGMOID_STRING))
            return KERNEL_SIGMOID;
        
        
        return KERNEL_RBF; 
    }
    
    
    
    /**
     * Function to set paramters for the svm 
     * just c and gamma are set
     * the other are put to default
     * @param svmType
     * @param kernelType
     * @param c
     * @param gamma
     */
    public void  setParameters(SelectedTag svmType, String kernel,  double c, double gamma)
    {
        svm.setSVMType(svmType);
        svm.setKernelType(selectKernel(kernel));
        svm.setDegree(3); //D
        svm.setGamma(gamma); //G
        svm.setCoef0(0); //R
        svm.setNu(0.5); //N
        svm.setCacheSize(40.0);
        svm.setSeed(1);                
        svm.setEps(0.001);
        svm.setCost(c);
 
    }
    
    /**
     * Function to set parameter for the svm 
     * @param svmType
     * @param kernelType
     * @param gamma
     * @param c
     * @param degree
     * @param coef
     * @param nu
     * @param cacheSize
     * @param seed
     * @param epsilon
     */
    public void  setParameters(SelectedTag svmType, String kernel, double gamma, double c, int degree,  int coef, double nu, double cacheSize, int seed, double epsilon)
    {
        svm.setSVMType(svmType);
        svm.setKernelType(selectKernel(kernel));
        svm.setDegree(degree); //D
        svm.setGamma(gamma); //G
        svm.setCoef0(coef); //R
        svm.setNu(nu); //N
        svm.setCacheSize(cacheSize);
        svm.setSeed(seed);                
        svm.setEps(epsilon);
        svm.setCost(c);
        
      
        
    }
    
    /**
     * Function to buld the classifer
     */
    public void buildClassifier()
    {
        try {
            svm.buildClassifier(train);
        } catch (Exception e) {
            System.out.println("An error occured while the classfier was build");
            System.out.println("Errormessage: " + e.getMessage());
            System.exit(-1);
            
        }
    }
    
    
    /**
     * Function to evaluate the model 
     */
    public Evaluation evaluateModel()
    {
        try {

            Evaluation eval = new Evaluation(train);
            eval.evaluateModel(svm, test);
            System.out.println(eval.toSummaryString("\nResults\n======\n", false));
           
           return eval;
        } catch (Exception e) {
            
            System.out.println("An error occured while evaluating the model");
            System.out.println("Errormessage: " + e.getMessage());
            System.exit(-1);
        }
       
        return null;
    }

}
