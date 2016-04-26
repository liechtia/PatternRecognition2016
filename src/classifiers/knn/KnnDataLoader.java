package classifiers.knn;

import weka.core.Instances;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

public class KnnDataLoader {
    public Instances getInstances() throws Exception {
        String arffPath = "KeywordSpottingData/wordimages/features.arff/";
        File arffFile = new File(arffPath);
        if (!arffFile.exists()){
            writeArff();
        }

        BufferedReader reader = new BufferedReader(new FileReader(arffFile));

        Instances data = new Instances(reader);

        return data;
    }
    public void writeArff() throws IOException {
        ImageFeatures[] imageFeatures = loadImages();
        String outputFileName = "KeywordSpottingData/wordimages/features.arff/";
        String relationName = "knn";
        File arffFile;
        BufferedWriter writer;
        ArrayList<String> classes = new ArrayList<String>();

        int longest = 0;
        for (ImageFeatures ft : imageFeatures) {
            if (ft.features.length > longest){
                longest = ft.features.length;
            }
        }

        for(int i = 0; i < imageFeatures.length; i++)
        {
            if(!classes.contains(imageFeatures[i].label))
                classes.add(imageFeatures[i].label);
        }

        try {
            arffFile = new File(outputFileName);
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(arffFile),"UTF-8"));

            writer.write("@relation " + relationName);
            writer.newLine();
            writer.newLine();

            for(int i = 0; i < longest; i++)
            {
                writer.write("@attribute " + i + " numeric");
                writer.newLine();
            }

            /*String classesString = "@attribute class{";
            for(int i = 0; i < classes.size()-1; i++)
            {
                classesString += classes.get(i) + ",";
                // bw.write(classes.get(i) + ",");
            }
            classesString += classes.get(classes.size()-1) +"}";
            writer.write(classesString);*/


            writer.newLine();
            writer.write("@data");
            writer.newLine();

            for(int i = 0; i < imageFeatures.length; i++)
            {
                StringBuilder builder = new StringBuilder();
                for(int j = 0;j < longest; j++)
                {
                    if (j == 0){
                        builder.append(imageFeatures[i].features[j]);
                    }
                    else if (j < imageFeatures[i].features.length) {
                        builder.append("," + imageFeatures[i].features[j]);
                    }
                    else{
                        builder.append(","+0);
                    }
                }

                //builder.append(imageFeatures[i].label);
                writer.write(builder.toString());
                writer.newLine();
            }

            writer.close();
        } catch (IOException e) {
            System.out.println("An error occured while writing the output arff file");
            System.exit(-1);
        }
    }

    private ImageFeatures[] loadImages() throws IOException {
        File folderImages = new File("KeywordSpottingData/wordimages/clipped_skew/");
        File[] images = folderImages.listFiles();
        ImageFeatures[] allImages = new ImageFeatures[images.length];

        for (int i = 0; i < images.length; i++) {
            String[] name = images[i].getName().split(" ");
            int idx = name[1].lastIndexOf(".");
            String label = name[1].substring(0, idx);

            BufferedImage image = ImageIO.read(images[i]);

            int width = image.getWidth();
            int height = image.getHeight();
            double[] features = new double[height*width];
            int count = 0;
            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    features[count] = image.getRGB(col, row);
                    count++;
                }
            }

            allImages[i] = new ImageFeatures(label, features);
        }

        return allImages;
    }

    private class ImageFeatures {
        public String label;
        public double[] features;

        public ImageFeatures(String label, double[] features) {
            this.label = label;
            this.features = features;
        }
    }
}
/*
package classifiers.knn;

        import sun.plugin.dom.core.Attr;
        import weka.core.Attribute;
        import weka.core.FastVector;
        import weka.core.Instance;
        import weka.core.Instances;
        import weka.core.converters.ConverterUtils;

        import javax.imageio.ImageIO;
        import java.awt.image.BufferedImage;
        import java.io.BufferedWriter;
        import java.io.File;
        import java.io.FileWriter;
        import java.io.IOException;
        import java.text.ParseException;
        import java.util.ArrayList;
        import java.util.List;

public class KnnDataLoader {
    public Instances getInstances() throws Exception {
        Instances data;
        String arffPath = "KeywordSpottingData/wordimages/features.arff/";
        File arffFile = new File(arffPath);
        if (!arffFile.exists()){
            data = writeArff(arffFile);
        }
        else{
            ConverterUtils.DataSource source = new ConverterUtils.DataSource(arffPath);
            data = source.getDataSet();
        }

        return data;
    }

    public Instances writeArff(File arffFile) throws IOException, ParseException {
        Instances instances = createInstances();
        BufferedWriter writer = new BufferedWriter(new FileWriter(arffFile));
        writer.write(instances.toString());
        writer.flush();
        writer.close();
        return instances;
    }

    public Instances createInstances() throws IOException, ParseException {
        ImageFeatures[] imageFeatures = loadImages();
        int longest = 0;
        for (ImageFeatures ft : imageFeatures) {
            if (ft.features.length > longest){
                longest = ft.features.length;
            }
        }

        FastVector      atts;
        FastVector      attsRel;
        FastVector      attVals;
        FastVector      attValsRel;
        Instances       data;
        Instances       dataRel;
        double[]        vals;
        double[]        valsRel;
        int             i;

        atts = new FastVector();
        atts.addElement(new Attribute("label", (FastVector) null));
        attsRel = new FastVector();
        attValsRel = new FastVector();
        for (i = 0; i < longest; i++)
            attValsRel.addElement("val" + (i+1));
        attsRel.addElement(new Attribute("features", attValsRel));
        dataRel = new Instances("fts", attsRel, 0);
        atts.addElement(new Attribute("fts", dataRel, 0));

        data = new Instances("image", atts, imageFeatures.length);

        for (int ft = 0; ft < imageFeatures.length; ft++) {
            dataRel = null;
            vals = new double[data.numAttributes()];
            vals[0] = data.attribute(0).addStringValue(imageFeatures[ft].label);

            dataRel = new Instances(data.attribute(1).relation(), longest);
            valsRel = new double[imageFeatures[ft].features.length];
            for(int ct = 0; ct < imageFeatures[ft].features.length; ct++){
                valsRel[ct] = imageFeatures[ft].features[ct];
            }

            dataRel.add(new Instance(1.0, valsRel));
            vals[1] = data.attribute(1).addRelation(dataRel);

            data.add(new Instance(1.0, vals));
            System.out.println(ft + " / " + imageFeatures.length);
        }

        return data;
    }

    private ImageFeatures[] loadImages() throws IOException {
        File folderImages = new File("KeywordSpottingData/wordimages/clipped_skew/");
        File[] images = folderImages.listFiles();
        ImageFeatures[] allImages = new ImageFeatures[images.length];

        for (int i = 0; i < images.length; i++) {
            String[] name = images[i].getName().split(" ");
            int idx = name[1].lastIndexOf(".");
            String label = name[1].substring(0, idx);

            BufferedImage image = ImageIO.read(images[i]);

            int width = image.getWidth();
            int height = image.getHeight();
            double[] features = new double[height*width];
            int count = 0;
            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    features[count] = image.getRGB(col, row);
                    count++;
                }
            }

            allImages[i] = new ImageFeatures(label, features);
        }

        return allImages;
    }

    private class ImageFeatures {
        public String label;
        public double[] features;

        public ImageFeatures(String label, double[] features) {
            this.label = label;
            this.features = features;
        }
    }
}
*/
