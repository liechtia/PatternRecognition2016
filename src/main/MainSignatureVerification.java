package main;

import java.io.IOException;

import signatures.GroundTruth;
import signatures.SignatureLoader;
import signatures.SignatureVerification;

public class MainSignatureVerification {
    public static void main(String[] args) throws IOException {
        SignatureVerification sv = new SignatureVerification();
        sv.RunVerification();
    }
}
