package ks.datahandling;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import java.io.*;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.util.XMLResourceDescriptor;

public class WordImages {		
	  public static void main( String args[] ) throws Exception {
		  WordImages images = new WordImages();
		  images.tryThisStuff();
	  }
	  
	  @SuppressWarnings("unused")
	public void tryThisStuff() throws Exception{
		  try {			  			  
			  initSVGDOM();
			  
			  String parser = XMLResourceDescriptor.getXMLParserClassName();
			  SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
		      Document doc = f.createDocument("KeywordSpottingData/ground-truth/locations/270.svg");
			  DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
			} 
		    catch (IOException ex) {
			    throw new Exception(ex);
			}
	  }

	private void initSVGDOM() {
		UserAgent userAgent = new UserAgentAdapter();
		  DocumentLoader loader = new DocumentLoader( userAgent );
		  BridgeContext bridgeContext = new BridgeContext( userAgent, loader );
		  bridgeContext.setDynamicState( BridgeContext.DYNAMIC );
//			  (new GVTBuilder()).build( bridgeContext, document );
	}
}
