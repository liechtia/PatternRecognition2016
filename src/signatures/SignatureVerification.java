package signatures;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import keywordspotting.DTW;

/**
 * this class loads signatures, then applies dtw on them and stores it into signatureresult objects
 */
public class SignatureVerification {
	private List<Integer> users;
	private List<Signature> enrollment;
	private List<Signature> verification;
	private List<SignatureResult> results;
	private SignatureLoader loader;
	
	/**
	 * @throws IOException
	 * 
	 * load all the data when class is created
	 */
	public SignatureVerification() throws IOException{
		loader = new SignatureLoader();
        users = loader.LoadUsers();
        //loader.LoadGroundTruth(); 
        enrollment = loader.LoadEnrollmentSignatures();
        verification = loader.LoadVerificationSignatures();
	}
	
	/**
	 * @throws IOException
	 * 
	 * runs verification and stores result in resultfiles, then calls evaluation method
	 */
	public void runVerification() throws IOException{
	   results = new ArrayList<SignatureResult>();
       for(Signature verificationSig : verification){
    	   SignatureResult result = new SignatureResult();
    	   int ctr = 0;
    	   for(Signature enrollmentSig : enrollment){
    		   if (enrollmentSig.user == verificationSig.user){
    			   double score = DTW.computeDTW(enrollmentSig.formFeatureArray(), verificationSig.formFeatureArray(), 10);
    			   result.user = enrollmentSig.user; 
    			   result.verification = verificationSig;
    			   result.distance += score;    
    			   ctr++;
    		   }   
    	   }
    	   
    	   //calculate average
    	   result.distance = result.distance/ctr;
    	   
    	   results.add(result);
		   //System.out.println(" u: " + result.user + " d: " + "v: " + result.verification.number + result.distance);
        }
       
       //evaluate and write results
       evaluateResults();
	}

	/**
	 * @throws IOException
	 * 
	 * evaluates the result objects and orders them according to accuracy
	 *  then the desired output file is created
	 */
	private void evaluateResults() throws IOException {		
		BufferedWriter output = null;
		   File file = new File("results/signature_results.txt");
		   output = new BufferedWriter(new FileWriter(file));
		   
		   for(Integer user : users){  
		       output.write(user.toString());
		       
		       List<SignatureResult> resultsForUser = new ArrayList<SignatureResult>();
		       
		       for(SignatureResult result : results){
		    	   if (result.user == user){
		    		   resultsForUser.add(result);
		    	   }
		       }
		       
		       Collections.sort(resultsForUser, new SignatureResultComparator());
		       DecimalFormat formatter = new DecimalFormat("#0.00");
		       
		       for (SignatureResult resultForUser : resultsForUser){
		    	   output.write(", " + resultForUser.verification.number + ", " + formatter.format(resultForUser.distance));
		    	   
/*        	   boolean truth = resultForUser.isGenuine(loader);
		    	   if (truth){
		    		   output.write("true");
		    	   }
		    	   else{
		    		   output.write("false");
		    	   }*/
		       }
		    
		       output.newLine();
		   }
		   
		   output.close();
	}	
}