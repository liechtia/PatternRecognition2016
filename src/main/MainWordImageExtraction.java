//
//

package main;

import keywordspotting.WordImages;

public class MainWordImageExtraction {

    private static final String folder ="KeywordSpottingData_Test";
    private static final String folderLocations = folder + "/ground-truth/locations";
    private static final String folderSave = folder + "/wordimages";
    private static final String folderImages = folder +"/images";
    private static final String transcriptionFile = folder + "/ground-truth/transcription.txt";
    
    
    public static void main(String[] args) {
        WordImages wi = new WordImages( folderImages, folderLocations, folderSave,  transcriptionFile);
        wi.getWordImages();
    }

}
