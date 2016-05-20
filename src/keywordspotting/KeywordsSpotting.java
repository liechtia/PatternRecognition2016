package keywordspotting;


import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import javax.imageio.ImageIO;

import utils.Distance;


public class KeywordsSpotting {

    private KeywordImage template;
    BufferedImage imageTemplate;
    private int baselineTemplate;
    private String classified = "";
    private int countDescenderTemplate = 0; 
   
    public KeywordImage getTemplate() {return this.template;}
    
    public String getClassified(){return this.classified;}
    
   public ArrayList<String> labels;
   public   int[] votes;
    
   
   private  HashMap<String, ArrayList<KeywordImage>> trainingHashMap = new  HashMap<String, ArrayList<KeywordImage>>();
    private HashMap<String, ArrayList<KeywordImage>> validHashMap = new  HashMap<String, ArrayList<KeywordImage>>();
    private ArrayList<KeywordImage> files = new ArrayList<KeywordImage>();
    private BufferedWriter outputWithId = null;
    private BufferedWriter outputWithLabel = null;
    private  ArrayList<KeywordImage> trainingImages;
    private ArrayList<KeywordImage> validImages;
    
   private double[] treshold = {0.07};
   
   private String keywordFile;
   private ArrayList<String> keywords;
    
    public KeywordsSpotting(String keywordFile) throws IOException {
        this.keywordFile = keywordFile;
        
        System.out.println("Read data");
        readData();
        
        System.out.println("Spot keywords");
        this.spotKeywords();
        
        outputWithId.close();
        outputWithLabel.close();
    }
    
    private void readData() throws IOException
    {
       
        File folderImages = new File("KeywordSpottingData/wordimages/final_clipped/");
        String pathTrain = "KeywordSpottingData/task/train.txt";
        String pathValid = "KeywordSpottingData/task/valid.txt";
        
      
        
        File file = new File("results/keywordspotting.txt");
        outputWithId = new BufferedWriter(new FileWriter(file));
        
        File file2 = new File("results/keywordspottingWithLabel.txt");
        outputWithLabel = new BufferedWriter(new FileWriter(file2));
        
        ArrayList<String> trainFiles = readFile(new File(pathTrain));
        ArrayList<String> validFiles = readFile(new File(pathValid));
        
        trainingImages =  new  ArrayList<KeywordImage>();
        validImages =  new ArrayList<KeywordImage>();
      
        
        File[] images = folderImages.listFiles();

        for(File imageFile : images)
        {     
            if(!imageFile.getName().contains(".png")) continue; 
            
            String[] name = imageFile.getName().split(" ");
            String[] information = name[0].split("-");
            String doc = information[0];
            int line = Integer.parseInt(information[0]);
            int wordInLine = Integer.parseInt(information[1]);
            
            int  idx = name[1].lastIndexOf(".");
            
            String label = name[1].substring(0, idx).toLowerCase();
            if(label.lastIndexOf("-s_") >0 )
            {
                label = label.substring(0, label.lastIndexOf("-s_"));
            }
            if(label.equals(""))
                continue; 
            
            //Calculate features for the images
            KeywordImage im = new KeywordImage(label, imageFile, line, wordInLine);
            //according to their doc number add the image to the hash map 
            if(trainFiles.contains(doc))
            {              
                trainingImages.add(im);  
                addImage(trainingHashMap, label, im); 
            }
            else if(validFiles.contains(doc))
            {
                validImages.add(im);
                addImage(validHashMap, label, im); 
            }

        }
        
        keywords = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new FileReader(keywordFile));
        String line = null;
        
        while((line = br.readLine()) != null)
        {
            keywords.add(line.toLowerCase()); 
        }
        
    }
        
    private void spotKeywords()
    {
      
        double[] avRecall = new double[treshold.length];
        double[] avPrec = new double[treshold.length];
        
        for(KeywordImage i : trainingImages)
        {
            getFeatures(i); 
        }
        normFeatures(trainingImages); 
        
        for(KeywordImage i : validImages)
        {
            getFeatures(i);
        }
        normFeatures(validImages);
        
        for(int k = 0; k < treshold.length; k++)
        {
            double t = treshold[k];
            System.out.println(t); 
            double recall = 0;
            double prec = 0; 
           int x = 0;
           
           for(String keyword : keywords)
           {
               x++;
               if(!validHashMap.containsKey(keyword))
                   {
                       continue;
                   }

                      System.out.println(keyword);
                      FindSimilarPictures similar = new FindSimilarPictures(validHashMap.get(keyword));
                      ArrayList<Distance> d = similar.findSimilarPictures(t, trainingImages);
                      
                  
                      
                   double[] eval = this.evaluate(keyword, d);
                   writeResult(outputWithId, outputWithLabel, keyword, d);
                   recall += eval[0];
                    prec += eval[1];       
               
           }
            
         
            avRecall[k] = recall / (x+0.0);
            avPrec[k] = prec / (x+0.0); 
            double f= 2*((avRecall[k]*avPrec[k])/(avRecall[k]+avPrec[k]));
            
            System.out.println("Recall: " + avRecall[k]);
            System.out.println("Prec: " + avPrec[k]);
            System.out.println("F-Measure: " + f);

            
        }
        
        
       
    }
    
    private void writeResult(BufferedWriter output, BufferedWriter outputWithLabel, String test, ArrayList<Distance> list)
    {
        try {
            output.write(test + " ");
            outputWithLabel.write(test + " ");
            for(Distance d : list)
            {
                output.write(d.getImage().getFile().getName().split(" ")[0] + ", " + d.getScore() + " ");
                outputWithLabel.write(d.getImage().getLabel() + ", " + d.getScore() + " ");
            }
            output.newLine();
            outputWithLabel.newLine();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
    private double[] evaluate(String test, ArrayList<Distance> list)
    {
        int tp = 0;
        int fp = 0;
        int fn = 0;
        
        double prec;
        double recall; 
        
        if(!trainingHashMap.containsKey(test)) //ground truth = 0
        {
            if(list.size() == 0)
            {
                prec = 1;
                recall = 1;
            }
            else 
            {
                prec = 0;
                recall  = 0; 
            }   
        }
        else if(list.size() == 0)
        {
            recall = 0;
            prec = 0; 
        }
        else
        {
            for(Distance d : list)
            {
               if( d.getImage().getLabel().equals(test))
                {
                    tp++;
                }
               else
               {
                   fp++;
               }
            }
            
            fn = trainingHashMap.get(test).size() - tp; 
            recall = (tp /(tp+fn+0.0));
            prec = (tp /(fp+tp+0.0));
        }
        
        double[] re = {recall, prec};
        return re;
        
       
    }


    
    /**
     * Delete all images which are probably not similiar to the  test image 
     * The deleting is done with:
     * ratio of the area
     * ratio of the aspect (width/length)

     * 
     * TODO:
     * get rid of comma, semikolon ect in the images...
     * @param images
     * @return
     */
    public int pruneImages(ArrayList<KeywordImage> images)
    {
        files = new ArrayList<KeywordImage>();
        int wrong = 0;
        for(KeywordImage imageFile : images)
        {
            files.add(imageFile);
           
           BufferedImage image = imageFile.getImage();
         
           
           boolean ratioArea = Pruning.getRatioOfArea(imageTemplate, image, 2.0);
           boolean ratioAspect =  Pruning.getRatioOfAspect(imageTemplate, image, 1.5);
           
           boolean prune = false;
           
           if(!ratioArea)
           {
               prune = true;
           }
           
           if(!ratioAspect)
               prune = true;
           
           int descenders = 0;
           if(!prune)
           {
               descenders = Pruning.countDescenders(image, baselineTemplate);

                   if(this.countDescenderTemplate != descenders)
                   {
                    prune =true;    
                   }
               
           }
           
           if(prune)
           {
               if(imageFile.getLabel().contains(this.template.getLabel()))
               {
                   wrong++;

               }
           }
           else
           {
               files.add(imageFile);
           }
        }
        
        return wrong;
    }
    
    
    private static void normFeatures( ArrayList<KeywordImage> trainingImages)
    {

        
        for(KeywordImage i : trainingImages)
        {
            i.formFeatureArray();
             i.normFeatures();
        }


    }
  

    private static void getFeatures(KeywordImage im)
    {
        BufferedImage image;
        try {
            image = ImageIO.read(im.getFile());    
            im.setImage(image);
            im.slidingWindow();

        } catch (IOException e) {
         
            e.printStackTrace();
        }
    
    }
    private static void addImage(HashMap<String, ArrayList<KeywordImage>> list, String label, KeywordImage im)
    {
        if(list.containsKey(label))
        {
            list.get(label).add(im);
        }
        else
        {
            list.put(label, new ArrayList<>(Arrays.asList(im)));
        }
    }
    
    private static ArrayList<String> readFile(File f)
    {
        ArrayList<String> files = new ArrayList<String>();
        
        try {
            BufferedReader br= new BufferedReader(new FileReader(f));
            String line;
           
            while((line = br.readLine()) != null)
            {
                files.add(line);
            }
            
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return files; 
        
        
    }
    
    
  
}

class CustomComparator implements Comparator<Distance> {
    @Override
    public int compare(Distance o1, Distance o2) {
        if(o1.getScore() < o2.getScore()) return -1;
        if(o1.getScore() > o2.getScore()) return 1;
        
        return 0; 
    }
}
