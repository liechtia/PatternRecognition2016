package signatures;

public class SignatureResult {
	public SignatureResult(int u, Signature e, Signature v, double d){
		this.user = u;
		this.enrollment = e; 
		this.verification = v;
		this.distance = d;
	}
	
	public int user;
	public Signature enrollment;
	public Signature verification;
	public double distance;
}
