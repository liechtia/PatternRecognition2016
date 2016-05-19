package signatures;

import java.awt.List;
import java.io.IOException;
import java.util.ArrayList;

public class SignatureResult {
	public SignatureResult(int u, Signature e, Signature v, double d){
		this.user = u;
		//this.enrollment = e; 
		this.verification = v;
		this.distance = d;
	}
	
	public SignatureResult(){
		this.distance = 0.0;
	}
	
	public boolean isGenuine(SignatureLoader loader) throws IOException{
		ArrayList<GroundTruth> gt = loader.LoadGroundTruth();
		for(GroundTruth truth : gt){
			if (truth.user == this.user && truth.signatureNumber == this.verification.number){
				return truth.genuine;
			}
		}
		
		return false;
	}
	
	public int user;
	//public Signature enrollment;
	public Signature verification;
	public double distance;
}
