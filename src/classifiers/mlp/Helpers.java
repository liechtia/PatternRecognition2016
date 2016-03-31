package classifiers.mlp;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import weka.classifiers.Classifier;

public class Helpers {

	public static void saveClassifier(Classifier cls, String str) throws Exception {
		ObjectOutputStream oos = new ObjectOutputStream(
				new FileOutputStream(str));
		oos.writeObject(cls);
		oos.flush();
		oos.close();
	}
	
	public static Classifier loadClassifier(String str) throws Exception {
		ObjectInputStream ois = new ObjectInputStream(
				new FileInputStream(str));
		Classifier cls = (Classifier) ois.readObject();
		ois.close();
		return cls;
	}
}
