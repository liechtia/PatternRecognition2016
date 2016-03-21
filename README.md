# PatternRecognition2016
Team Project 1 for the Course Pattern Recognition in Unifr 

#Project structure:
data: 	folder with the data files
	the file train.csv contains around 5000 datapoints
	the file trainAll.csv contains all of them.

src:	the source code
  packages: 
	classifiers: for the different classifers
	utils: differnet methods which can be handy for different classifiers
	main: for the classes to test the classifiers 

#Classes:
testSVM.java:
class to test the SVM classifer. You can read here how to call 
the function which read the csv file and converts it to an arff file

Datapoint.java:
Class to represent a datapoint 

FeatureExtraction.java.
Class to extract some feature to reduce the amount of data.
I implemented for the image a reducing which calculates for every column
in the image the average grey value, the upper most pixel, the lower most pixel
and the number of black-white transitions.
For the MINST dataset this reduced the vector to 4*28 = 112 features

IO_Functions:
Class to read and write to files.

SVM:
Class for the svm classifer. 
