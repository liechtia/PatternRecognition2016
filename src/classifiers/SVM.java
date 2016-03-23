package classifiers;

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
    
    public final SelectedTag  KERNEL_RBF = new SelectedTag(LibSVM.KERNELTYPE_RBF, LibSVM.TAGS_KERNELTYPE);
    public final SelectedTag  KERNEL_POLYNOMIAL = new SelectedTag(LibSVM.KERNELTYPE_POLYNOMIAL, LibSVM.TAGS_KERNELTYPE);
    public final SelectedTag  KERNEL_LINEAR = new SelectedTag(LibSVM.KERNELTYPE_LINEAR, LibSVM.TAGS_KERNELTYPE);
    public final SelectedTag  KERNEL_SIGMOID = new SelectedTag(LibSVM.KERNELTYPE_SIGMOID, LibSVM.TAGS_KERNELTYPE);   
    
    private Instances train;
    private Instances test;
    private LibSVM svm;
    
    public SVM(Instances trainInstances, Instances testInstances) {
    	train = trainInstances;
    	test = testInstances;
        svm = new LibSVM(); 
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
    public void  setParameters(SelectedTag svmType, SelectedTag kernelType,  double c, double gamma)
    {
        svm.setSVMType(svmType);
        svm.setKernelType(kernelType);
        svm.setDegree(3); //D
        svm.setGamma(gamma); //G
        svm.setCoef0(0); //R
        svm.setNu(0.5); //N
        svm.setCacheSize(40.0);
        svm.setSeed(1);                
        svm.setEps(0.00001);
        
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
    public void  setParameters(SelectedTag svmType, SelectedTag kernelType, double gamma, double c, int degree,  int coef, double nu, double cacheSize, int seed, double epsilon)
    {
        svm.setSVMType(svmType);
        svm.setKernelType(kernelType);
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
    public double evaluateModel()
    {
        try {
            Evaluation eval = new Evaluation(train);
            eval.evaluateModel(svm, test);            
            System.out.println(eval.toSummaryString("\nResults\n======\n", false));
            double total = eval.correct() + eval.incorrect();
            return total / eval.correct();
        } catch (Exception e) {
            System.out.println("An error occured while evaluating the model");
            System.out.println("Errormessage: " + e.getMessage());
            System.exit(-1);
        }
       
        return 0.0;
    }

}
