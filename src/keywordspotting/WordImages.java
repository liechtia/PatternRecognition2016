package keywordspotting;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.BridgeContext;
public class WordImages {	
    
     private String folderImages;
     private String folderLocations;
     private String folderSave;
     private String transcriptionFile;
  
    /**
     * Constructor
     * @param folderImages: folder with the document images
     * @param folderLocations: folder with the svg files
     * @param folderSave: folder where to save the word images
     * @param transcriptionFile: file with the transcriptions of the word 
     */
     public WordImages(String folderImages, String folderLocations, String folderSave, String transcriptionFile)
     {
        this.folderImages = folderImages;
        this.folderLocations = folderLocations;
        this.folderSave = folderSave; 
        this.transcriptionFile = transcriptionFile;
	  }
     
     /**
      * Function gets the wordimages with the path specified in the svg files 
      */
     public void getWordImages()
     {
         File folder = new File(folderLocations);
         File[] listOfFiles = folder.listFiles();
         
         File saveFolder_nonClipped= new File(this.folderSave + "/non_clipped");
         File saveFolder_Clipped= new File(this.folderSave + "/clipped");
         File save = new File(this.folderSave);
         
         /**
          * Create the folders for saving if the not exists
          */
         if(!save.exists())
         {
             save.mkdir();
         }
         
         if(!saveFolder_Clipped.exists())
         {
             saveFolder_Clipped.mkdir(); 
         }
         
         if(!saveFolder_nonClipped.exists())
         {
             saveFolder_nonClipped.mkdir(); 
         }
         

          //   BufferedReader br = new BufferedReader(new FileReader(transcriptionFile)); 
             
             /*Get alls svg files which are in the locations folder and extract the words from the 
               *corresponding document image*/
             for (File file : listOfFiles) {
                 if (file.getName().contains(".svg")) {
                     Document doc = readSVG(file.toString());
                     extractWords(file.getName().replace("svg", "jpg"),  doc);   
                 }
                
             }

     }
     
     /**
      * Read the svg file 
      * @param path
      * @return SVG document object
      */
     private Document readSVG(String path)
     {
         initSVGDOM();
         
         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         DocumentBuilder builder;
         Document document = null;
        try {
            builder = factory.newDocumentBuilder();
            document = builder.parse(path);
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return document;

     }
	  
	/**
	 * Extract the words from the corresponding image file
	 * @param fileName: name of the image file
	 * @param doc: document with the svg paths
	 * @param transcriptionFile: file with the transcriptions (to name the word images)
	 */
     private void extractWords(String fileName, Document doc) {
         String xpathExpression = "//path/@d";
         String xpathIDExpression = "//path/@id";
		 XPathFactory xpf = XPathFactory.newInstance();
		 XPath xpath = xpf.newXPath();
		 XPathExpression expression;
		 
		 XPathFactory xpfId = XPathFactory.newInstance();
		 XPath xpathID = xpfId.newXPath();
         XPathExpression expressionID;
		 
		 String saveName;
        try {
            expression = xpath.compile(xpathExpression);
            expressionID = xpathID.compile(xpathIDExpression);
            
            /*get all svg paths from the svg file 
             * a path specifies how a box around a word was drawn 
             * it has a starting point 
             */
            NodeList svgPaths = (NodeList)expression.evaluate(doc, XPathConstants.NODESET);
            NodeList saveNameList = (NodeList)expressionID.evaluate(doc,XPathConstants.NODESET );
            System.out.println(saveNameList);
            
            //get for all svg paths the correspinding word from the image file
            for(int i = 0; i <  svgPaths.getLength(); i++)
            {
                saveName = saveNameList.item(i).getNodeValue();
               
                System.out.println(saveName);
                String svgPath = svgPaths.item(i).getNodeValue();
                SvgPath svgpath = getBox( svgPath);                 //translate the path to normalized x and y coordinates 
                getImage(fileName, saveName, svgpath);
                
                
            } 
        } catch (XPathExpressionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
	    

	  }
	  
     /**
      * Function to get a word image correspinding to an svg paths
      * @param fileName: name of the iamge file
      * @param saveName: name for saving the extracted word
      * @param svgpath: SvgPath object 
      */
	  private void getImage(String fileName, String saveName, SvgPath svgpath)
	  {
	      int height = svgpath.getHeight();
	      int width = svgpath.getWidth();
    
	      String pathToImage = this.folderImages + "/" + fileName;
	      File outputfile;
	      BufferedImage imageDocument;

        try {
            imageDocument = ImageIO.read(new File(pathToImage) );
            
            /*
             * cut first an rectangle from the image which starts at the starting poins from the 
             * wordimage box and has the widht and height of the box 
             * 
             * the complete word is than cut out from this subimage 
             */
            BufferedImage wordImageUnclipped = imageDocument.getSubimage(svgpath.getStartX(), svgpath.getStartY(), width, height);
            BufferedImage wordImageClipped = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            //save the unclipped small image for now (maybe it is useful later)
            outputfile = new File(this.folderSave + "/non_clipped/" + saveName + ".png");
            ImageIO.write(wordImageUnclipped, "png", outputfile);
            
            //create graphic to cut out the word from the subimage
            Graphics2D g2 = wordImageClipped.createGraphics();
            int x2Points[] = svgpath.getXValues();
            int y2Points[] = svgpath.getYValues();
            
            //create a polyline with the points of the box
            GeneralPath polyline = new GeneralPath(GeneralPath.WIND_EVEN_ODD, x2Points.length);
            polyline.moveTo (x2Points[0], y2Points[0]);
            for (int index = 1; index < x2Points.length; index++) {
                     polyline.lineTo(x2Points[index], y2Points[index]);
            };
            polyline.lineTo (x2Points[0], y2Points[0]);
            
            //clip the image 
            g2.setClip(polyline);
            g2.draw(polyline);
            //take the clipped parte from the unclipped image and draw it on the g2 graphic
            g2.drawImage(wordImageUnclipped, 0, 0, null);     
            g2.dispose();   
            
            for(int i = 0; i < wordImageClipped.getWidth(); i++)
            {
                for(int j = 0; j < wordImageClipped.getHeight(); j++)
                {
                    if(wordImageClipped.getRGB(i, j) ==0)
                    {
                        wordImageClipped.setRGB(i, j, new Color(255, 255, 255).getRGB());
                    }
                    
                }
                
            }
            
            //save the extracted word
            outputfile = new File(this.folderSave + "/clipped/" + saveName + ".png");
            ImageIO.write(wordImageClipped, "png", outputfile);
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
	    
	   
	  }
	  
	  
	  private SvgPath getBox(String svgPath)
	  {
	      String[] svg = svgPath.split(" ");
	      ArrayList<Integer> xValues = new ArrayList<Integer>();
	      ArrayList<Integer> yValues = new ArrayList<Integer>();
	      
	      for(int i =0; i < svg.length-1; i = i + 3)
	      {
	          double valuex = Double.parseDouble(svg[i+1]);
	          double valuey = Double.parseDouble(svg[i+2]);
	          xValues.add((int) valuex);
	          yValues.add((int) valuey);
	         
	      }

	      int[] xValuesArr = new int[xValues.size()];
	      int[] yValuesArr = new int[yValues.size()];
	      
	      for(int i = 0 ; i < xValues.size(); i++){
	          xValuesArr[i] = xValues.get(i);
	          yValuesArr[i] = yValues.get(i);
	      }
	      
	      Collections.sort(xValues);
	      Collections.sort(yValues);
	      
	      int height = yValues.get(yValues.size()-1) - yValues.get(0);
	      int width =  xValues.get(xValues.size()-1) - xValues.get(0);
	      
	      SvgPath svgpath = new SvgPath( height,  width, xValuesArr, yValuesArr);
	      svgpath.normValues(xValues.get(0), yValues.get(0));
	      
	      return svgpath;
	      
	  }

	private void initSVGDOM() {
		UserAgent userAgent = new UserAgentAdapter();
		  DocumentLoader loader = new DocumentLoader( userAgent );
		  BridgeContext bridgeContext = new BridgeContext( userAgent, loader );
		  bridgeContext.setDynamicState( BridgeContext.DYNAMIC );
//			  (new GVTBuilder()).build( bridgeContext, document );
	}
}
