package signatures;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import utils.FtVector;

public class SignatureLoader {
	private ArrayList<GroundTruth> groundTruth;
	
	public SignatureLoader(){
		groundTruth = new ArrayList<GroundTruth>();
	}
	
	public List<Signature> LoadSignatures(boolean enrollment, String path) throws IOException{
		List<Signature> sigs = new ArrayList();
		File dir = new File(path);
		File[] files = dir.listFiles();
		for(File file : files){
			Signature signature = new Signature();
			
			String filename = file.getName().replaceFirst("[.][^.]+$", "");
			String[] parts = filename.split("-");
			
			signature.user = Integer.parseInt(parts[0]);
			if (enrollment){			
				signature.number = Integer.parseInt(parts[2]);				
			}
			else{
				signature.number = Integer.parseInt(parts[1]);
			}
			
			BufferedReader reader = new BufferedReader(new FileReader(file));
	        String features;
	        List<FtVector> featureList = new ArrayList();
	        double deltaTime = 0.0;
	        double deltaX = 0.0;
	        double deltaY = 0.0;
	        while ((features = reader.readLine()) != null){	        	
	        	SignatureFeature ft = new SignatureFeature(); 
	        	String[] elem = features.split("\\s+");
	        	ft.t = Double.parseDouble(elem[0]);
	        	ft.x = Double.parseDouble(elem[1]);
	        	ft.y = Double.parseDouble(elem[2]);
	        	ft.pressure = Double.parseDouble(elem[3]);
	        	ft.penup = Double.parseDouble(elem[4]);
	        	ft.azimuth = Double.parseDouble(elem[5]);
	        	ft.inclination = Double.parseDouble(elem[6]);
	        	
	        	if (deltaX == 0.0){
	        		deltaTime = ft.t;
	        		deltaX = ft.x;
	        		deltaY = ft.y;
	        	}

	        	if (ft.t == 0.0 && deltaTime == 0.0){
	        		ft.vx = ft.vy = 0.0;
	        	}
	        	else{
		        	ft.vx = (ft.x - deltaX) / (ft.t - deltaTime);
		        	ft.vy = (ft.y - deltaY) / (ft.t - deltaTime);	
	        	}
	        	
	        	featureList.add(ft);	        	
	        	deltaTime = Double.parseDouble(elem[0]);
	        }
	        
	        signature.features = featureList;
	        
	        sigs.add(signature);
		}
	
		return sigs;		
	}
	
	public List<Signature> LoadVerificationSignatures() throws IOException{
		return LoadSignatures(false, "SignatureVerificationData/TestSignatures/verification");
	}
	
	public List<Signature> LoadEnrollmentSignatures() throws IOException{
		return LoadSignatures(true, "SignatureVerificationData/TestSignatures/enrollment");
	}
	
    public List<Integer> LoadUsers() throws FileNotFoundException {
        List<Integer> users = new ArrayList<>();
        File file = new File( "SignatureVerificationData/TestSignatures/users.txt");
        Scanner scanner = new Scanner(file);
        while(scanner.hasNextInt()) {
            users.add(scanner.nextInt());
        }

        return users;
    }

    public ArrayList<GroundTruth> LoadGroundTruth() throws IOException {
    	if (!groundTruth.isEmpty()){
    		return groundTruth;
    	}
    	
        groundTruth = new ArrayList<>();
        File file = new File( "SignatureVerificationData/verification-gt.txt");
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String gt;
        while ((gt = reader.readLine()) != null){
            GroundTruth signatures = new GroundTruth();
            String[] values = gt.split("-");
            signatures.user = Integer.parseInt(values[0]);
            String[] values2 = values[1].split("\\s+");
            signatures.signatureNumber = Integer.parseInt(values2[0]);
            if (values2[1].equals("g")){
                signatures.genuine = true;
            }
            else{
                signatures.genuine = false;
            }

            groundTruth.add(signatures);
        }

        return groundTruth;
    }
}