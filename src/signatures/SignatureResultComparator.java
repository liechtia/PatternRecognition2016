package signatures;

import java.util.Comparator;

public class SignatureResultComparator implements Comparator<SignatureResult> {

	@Override
	public int compare(SignatureResult arg0, SignatureResult arg1) {
		return Double.compare(arg0.distance, arg1.distance);
	}

}
