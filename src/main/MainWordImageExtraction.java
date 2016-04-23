//
//

package main;

import ks.datahandling.WordImages;

public class MainWordImageExtraction {

    private static final String folderLocations = "KeywordSpottingData/ground-truth/locations";
    private static final String folderSave = "KeywordSpottingData/wordimages";
    private static final String folderImages = "KeywordSpottingData/images";
    private static final String transcriptionFile = "KeywordSpottingData/ground-truth/transcription.txt";
    
    
    public static void main(String[] args) {
        WordImages wi = new WordImages( folderImages, folderLocations, folderSave,  transcriptionFile);
        wi.getWordImages();
    }

}
