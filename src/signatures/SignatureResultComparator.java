package signatures;

import java.util.Comparator;

/**
 * class to compare the two distance values of a signatureresult object in order to be able to sort the list of results by distance
 */
public class SignatureResultComparator implements Comparator<SignatureResult> {

	@Override
	public int compare(SignatureResult arg0, SignatureResult arg1) {
		return Double.compare(arg0.distance, arg1.distance);
	}

}
