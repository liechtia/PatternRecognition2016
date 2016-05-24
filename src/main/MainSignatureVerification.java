package main;

import java.io.IOException;

import signatures.GroundTruth;
import signatures.SignatureLoader;
import signatures.SignatureVerification;

/**
 * main class&method for running signature verification
 */
public class MainSignatureVerification {
    public static void main(String[] args) throws IOException {
        SignatureVerification sv = new SignatureVerification();
        sv.runVerification();
    }
}
