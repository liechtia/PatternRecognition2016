package signatures;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import keywordspotting.DTW;
import keywordspotting.KeywordImage;
import utils.DTWResult;
import utils.Distance;
import utils.FeatureVector;
import utils.Tuple;

public class SignatureVerification {
	private int[] users;
	private GroundTruth[] gts;
	private List<Signature> enrollment;
	private List<Signature> verification;
	private SignatureResult[] results;
	
	public SignatureVerification() throws IOException{
		SignatureLoader loader = new SignatureLoader();
        users = loader.LoadUsers();
        gts = loader.LoadGroundTruth();
        enrollment = loader.LoadEnrollmentSignatures();
        verification = loader.LoadVerificationSignatures();
	}
	
	public void RunVerification(){
	   List<SignatureResult> res = new ArrayList();
       for(Signature verificationSig : verification){
    	   for(Signature enrollmentSig : enrollment){
    		   if (enrollmentSig.user == verificationSig.user){
    			   double score = DTW.computeDTW(enrollmentSig, verificationSig, 10);  
    			   res.add(new SignatureResult(enrollmentSig.user, enrollmentSig, verificationSig, score));
    		   }   
    	   }
        }
       
       results = new SignatureResult[res.size()];
       for(int i = 0;i < results.length;i++)
           results[i] = res.get(i);
       
       for(int i=0;i<users.length;i++){
    	   // TODO evaluation of results
       }
	}	
}