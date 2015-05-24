# Disease-Detection-using-image-processing-and-data-mining

This is a project code readme.txt.
Project: Knowledge Abstraction from Brain MRI Images Using Statistical Techniques and Associative Classification
Author: Abhishek Singh Sambyal	
USN: 1BI12SCS01

For any querries you can reach me here: abhishek.sambyal@gmail.com

This code is licensed under GNU General Public License v3.0. See GNU General Public License.txt for further details.

Files and Folders:
1.	featurevector.m
2.	featureselection.m
3.	cooccurrence.m
4.	GLCM_Features4.m
5.	CPAR (folder)
	   --- 5.1	MyClass.java
	   --- 5.2	AssocRuleMining.java
	   --- 5.3	ClassCPAR_App.java
	   --- 5.4	ClassFOIL_App.java
	   --- 5.5	Classification.java
	   --- 5.6	ClassPRM_App.java
	   --- 5.7	CPAR_CARgen.java
	   --- 5.8	FOIL_CARgen.java
	   --- 5.9	PRM_CARgen.java
	   --- 5.10	RuleList.java
6.	testing.txt


1.	Install Matlab 2013a

2.	Open the featurevector.m file. It contain two sections and either one of them will run everytime depending on your requirement.
	See featurevector.m for further understanding.
	Once it is done, you will have a feature vector.

3.	Run featureselection.m file. It will output the testing.txt file.

4.	Install jdk8 or above. Set the path and envirnoment variable. Refer this link http://www.computerhope.com/issues/ch000549.htm

5.	Open command prompt. Navigate to CPAR folder using change directory (cd) commmand.

6.	In cmd type:

		X:\>javac MyClass.java	
		It will output some warnings, do worry.

		X:\>java MyClass

		It will open up a small window. Press START MINING.
		Click INPUT FILE button. Navigate to folder where your testing.txt is stored and select it.
		In Number of classes field add 2.
		Select the Algorithm to CPAR.
		Select rule ordering measure to Laplace.
		Select test procedure to 75:25 (The previously trained data is on 75:25 ratio. You can change as per your need.).

7. 	Done. It will output the set of rules.
