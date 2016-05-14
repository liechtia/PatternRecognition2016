package signatures;

import java.io.IOException;

public class SignatureVerification {
	private int[] users;
	private GroundTruth[] gts;
	private Signature[] enrollment;
	private Signature[] verification;
	
	public SignatureVerification() throws IOException{
		SignatureLoader loader = new SignatureLoader();
        users = loader.LoadUsers();
        gts = loader.LoadGroundTruth();
        enrollment = loader.LoadEnrollmentSignatures();
        verification = loader.LoadVerificationSignatures();
	}
	
	public void RunVerification(){
		
	}	
}
