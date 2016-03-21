package classifiers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import utils.Datapoint;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LibSVM;
import weka.core.Instances;
import weka.core.SelectedTag;


public class SVM {

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
    private String trainFile;
    private String testFile;
    private LibSVM svm;
    
    public SVM(String trainFile, String testFile) {
        this.trainFile = trainFile;
        this.testFile = testFile;
        
        svm = new LibSVM(); 
        readArffFile(); 
    }
    
    private void readArffFile()
    {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(trainFile));
            train = new Instances(reader);
            reader.close();
            
            reader = new BufferedReader(new FileReader(testFile));
            test = new Instances(reader);
            
            train.setClassIndex(train.numAttributes() -1); //class attribute is last index      
            test.setClassIndex(train.numAttributes()-1);
            reader.close();
            
        } catch ( IOException e) {
            System.out.println("An error occured while reading the arff file");
            System.out.println(e.getMessage());
            System.exit(-1);
        }
       
    }
    
    
    public void  setParameters(SelectedTag svmType, SelectedTag kernelType,  int c, int gamma)
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
    
    public void  setParameters(SelectedTag svmType, SelectedTag kernelType, int gamma, int c, int degree,  int coef, double nu, double cacheSize, int seed, double epsilon)
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
    
    public void evaluateModel()
    {
        try {
            Evaluation eval = new Evaluation(train);
            eval.evaluateModel(svm, test);
            System.out.println(eval.toSummaryString("\nResults\n======\n", false));
        } catch (Exception e) {
            System.out.println("An error occured while evaluating the model");
            System.out.println("Errormessage: " + e.getMessage());
            System.exit(-1);
        }
       
    }

}
