package old;


public class mainBinarization {
    private static final String folderClipped = "KeywordSpottingData/wordimages/clipped/";
    private static final String folderClippedSave = "KeywordSpottingData/wordimages/clipped_binary/";
    private static final String folderNonClipped = "KeywordSpottingData/wordimages/non_clipped/";
    private static final String folderNonClippedSave = "KeywordSpottingData/wordimages/non_clipped_binary/";


    public static void main(String[] args) {
        Binarization binaryConverter = new Binarization();
        binaryConverter.binarizeImages(folderClipped, folderClippedSave, 100);
        binaryConverter.binarizeImages(folderNonClipped, folderNonClippedSave, 175);
    }
}