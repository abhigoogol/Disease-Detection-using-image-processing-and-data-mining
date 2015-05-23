/*	ClassCPAR_App.java and this program is The CPAR algorithm.

	Copyright (C) 2014	Abhishek Singh Sambyal	email:abhishek.sambyal@gmail.com
	written by LUCS-KDD group (avaliable free but not licenced).

	ClassCPAR_App.java is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/


import java.io.*;

/* Classification application the CPAR (Classification based on Predictive
Association Rules) algorithm proposed by Xiaoxin Yin and Jiawei Han.

Compile using:

javac ClassCPAR_App.java

Run using javaARM.exe, Example:

java ClassCPAR_App -FpimaIndians.D42.N768.C2.num -N2

(-F filename, -N number of classifiers).              */

public class ClassCPAR_App {

    // ------------------- FIELDS ------------------------

    // None

    // ---------------- CONSTRUCTORS ---------------------

    // None

    // ------------------ METHODS ------------------------

    public ClassCPAR_App(File inp,String cno,int i,int j) {


	// Create instance of class ClassificationCPAR
	CPAR_CARgen newClassification = new CPAR_CARgen(inp,cno,i,j);

	// Read data to be mined from file (method in AssocRuleMining class)
	newClassification.inputDataSet();
	newClassification.outputDataArraySize();

	// Create training and test data sets (method in ClassificationAprioriT class)
	// assuming a 50:50 split
	 newClassification.createTrainingAndTestDataSets(i);



	// Mine data and generate CARs
	double time1 = (double) System.currentTimeMillis();
	double accuracy = newClassification.startClassification();
	newClassification.outputDuration(time1,
				(double) System.currentTimeMillis());

	// Output
	newClassification.jtxt.append("\nAccuracy = " + accuracy);
	newClassification.getCurrentRuleListObject().outputNumRules();
	newClassification.getCurrentRuleListObject().outputRules();

	// End
	}
	  public ClassCPAR_App(File inp,File test,String cno,int i,int j) {

	// Create instance of class ClassificationCPAR
	CPAR_CARgen newClassification = new CPAR_CARgen(inp,test,cno,i,j);

	// Read data to be mined from file (method in AssocRuleMining class)
	newClassification.inputDataSet();
	newClassification.outputDataArraySize();

	// Create training and test data sets (method in ClassificationAprioriT class)
	// assuming a 50:50 split

		newClassification.createTestDataFromFile();


	// Mine data and generate CARs
	double time1 = (double) System.currentTimeMillis();
	double accuracy = newClassification.startClassification();
	newClassification.outputDuration(time1,
				(double) System.currentTimeMillis());

	// Output
	newClassification.jtxt.append("\nAccuracy = " + accuracy);
	newClassification.getCurrentRuleListObject().outputNumRules();
	newClassification.getCurrentRuleListObject().outputRules();

	// End
	}
    }
