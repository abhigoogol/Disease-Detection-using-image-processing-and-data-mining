/*	ClassPRM_App.java

	Copyright (C) 2014	Abhishek Singh Sambyal	email:abhishek.sambyal@gmail.com
	written by LUCS-KDD group (avaliable free but not licenced).

	ClassPRM_App.java is free software: you can redistribute it and/or modify
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

/* Classification application the PRM (First Order Inductive Learner) algorithm
proposed by Xiaoxin Yin and Jiawei Han.

Compile using:

javac ClassPRM_App.java

Run using javaARM.exe, Example:

java ClassPRM_App -FpimaIndians.D42.N768.C2.num -N2

(-F filename, -N number of classifiers).              */


public class ClassPRM_App {

    // ------------------- FIELDS ------------------------

    // None

    // ---------------- CONSTRUCTORS ---------------------

    // None

    // ------------------ METHODS ------------------------

    public ClassPRM_App(File inp,String cno,int i,int j) {


	// Create instance of class ClassificationPRM
	PRM_CARgen newClassification = new PRM_CARgen(inp,cno,i,j);

	// Read data to be mined from file (method in AssocRuleMining class)
	newClassification.inputDataSet();
	newClassification.outputDataArraySize();

	// Create training data set (method in ClassificationAprioriT class)
	// assuming a 50:50 split
        newClassification.createTrainingAndTestDataSets(i);

	// Mine data, produce T-tree and generate CRs
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
	public ClassPRM_App(File inp,File test,String cno,int i,int j) {


	// Create instance of class ClassificationPRM
	PRM_CARgen newClassification = new PRM_CARgen(inp,test,cno,i,j);

	// Read data to be mined from file (method in AssocRuleMining class)
	newClassification.inputDataSet();
	newClassification.outputDataArraySize();

	// Create training data set (method in ClassificationAprioriT class)
	// assuming a 50:50 split
        newClassification.createTestDataFromFile();

	// Mine data, produce T-tree and generate CRs
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
