package signatures;

import java.awt.List;
import java.io.IOException;
import java.util.ArrayList;

/**
 * stores the results computed by the signature verification class after dtw was applied
 */
public class SignatureResult {
	public int user;
	public Signature verification;
	public double distance;
	
	/**
	 * @param u
	 * @param e
	 * @param v
	 * @param d
	 * 
	 * constructor overload for creating results directly with values
	 */
	public SignatureResult(int u, Signature e, Signature v, double d){
		this.user = u;
		this.verification = v;
		this.distance = d;
	}
	
	/**
	 * default constructor sets distance to 0 as this value will be increased each time the calling method iterates through the same user
	 */
	public SignatureResult(){
		this.distance = 0.0;
	}
	
	/**
	 * @param loader
	 * @return
	 * @throws IOException
	 * 
	 * if there is a ground truth file, this method returns whether signature is genuine
	 */
	public boolean isGenuine(SignatureLoader loader) throws IOException{
		ArrayList<GroundTruth> gt = loader.LoadGroundTruth();
		for(GroundTruth truth : gt){
			if (truth.user == this.user && truth.signatureNumber == this.verification.number){
				return truth.genuine;
			}
		}
		
		return false;
	}
}
