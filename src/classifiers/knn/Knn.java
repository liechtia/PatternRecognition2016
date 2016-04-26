package classifiers.knn;
import weka.classifiers.Classifier;
import weka.classifiers.lazy.IBk;
import weka.core.Instance;
import weka.core.Instances;

public class Knn {
    public void applyKnn() throws Exception {
        KnnDataLoader loader = new KnnDataLoader();
        Instances data = loader.getInstances();

        data.setClassIndex(data.numAttributes() - 1);

        Instance first = data.instance(0);
        Instance second = data.instance(1);
        data.delete(0);
        data.delete(1);

        Classifier ibk = new IBk();
        ibk.buildClassifier(data);

        double class1 = ibk.classifyInstance(first);
        double class2 = ibk.classifyInstance(second);

        System.out.println("first: " + class1 + "\nsecond: " + class2);
        System.in.read();
    }
}
