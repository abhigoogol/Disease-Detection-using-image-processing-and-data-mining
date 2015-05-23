/*	FOIL_CARgen.java

	Copyright (C) 2014	Abhishek Singh Sambyal	email:abhishek.sambyal@gmail.com
	written by LUCS-KDD group (avaliable free but not licenced).

	FOIL_CARgen.java is free software: you can redistribute it and/or modify
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



/* Class structure

AssocRuleMining
      |
      +-- Classification
      		|
		+-- FOIL_CARgen		*/

// Java packages

import java.util.*;
import java.io.*;

/** Methods to produce classification rules using FOIL (First Order Inductive
Learner) algorithm first proposed by Ross Quinlan in 1993. Assumes
that input dataset is organised such that classifiers are at the end of each
record. Note: number of classifiers value is stored in the <TT>numClasses</TT>
field in <TT>AssocRuleMining</TT> parent class.
@author Frans Coenen
@version 3 February 2004 */

/* To compile: javaARMpackc.exe FOIL_CARgen.java    */

public class FOIL_CARgen extends Classification {

    /* ------ FIELDS ------ */

    // Data structures
    /** 2-D array to hold positive examples */
    private short[][] positiveExamples = null;
    /** 2-D array to hold negative examples */
    private short[][] negativeExamples = null;
    /** 2-D array to temporaily hold positive examples */
    private short[][] positiveExamples2 = null;
    /** 2-D array to temporaily hold negative examples */
    private short[][] negativeExamples2 = null;

    // Constants
    /** The maximum number of attributes that can be contained in a rule
    antecedent. */
    private final int MAX_NUM_ATTS_IN_ANTECEDENT=3;

    /* ------ CONSTRUCTORS ------ */

    /** Constructor processes command line arguments.
    @param args the command line arguments (array of String instances). */

    public FOIL_CARgen(File inp,String cno,int t,int o) {
	super(inp,cno,t,o);
	}
	 public FOIL_CARgen(File inp,File test,String cno,int t,int o) {
	super(inp,test,cno,t,o);
	}

    /* ------ METHODS ------ */

    /* START CLASSIFICATION */

    /** Starts classification rule generation process.
    @return The classification accuracay (%).		*/

    public double startClassification() {
        // Calculate minimum support threshold in terms of number of
        // records in the training set.
	jtxt.append("\nSTART FOIL CLASSIFICATION\n" +
				"-------------------------");
	jtxt.append("\nMax number of attributes per rule = " +
						MAX_NUM_ATTS_IN_ANTECEDENT);

        // Set rule list to null. Note that startRuleList is defined in the
	// AssocRuleMining parent class and is also used to store Association
	// Rules (ARS) with respect ARM.
	currentRlist.startRulelist = null;

	// Set DataArray and number of classes fields
	currentRlist.setDataArray(dataArray);
	currentRlist.setNumClasses(numClasses);

	// Check for classifier array
	if (classifiers==null) {
	    jtxt.append("\nERROR: no classifiers array! To create a " +
			"classifiers array use createClassifiersArray(), " +
			"contained in ClassAprioriTserial class, called " +
			"from ther application class.");
	    System.exit(1);
	    }

	// Start FOIL generation process
	startFOIL();

	// Test classification using the test set and return accuracy.
	return(twoDecPlaces(testClassification()));
        }

    /* START FOIL CLASSIFICATION *.

    /** Commences FOIL process to generate CARs. <P> Commence by, for each
    class, generating the positive and negative examples. Also generate an
    attribute array to store gain values. */

    private void startFOIL() {
        // Generate attribute array
	attributes = new double[numOneItemSets-numClasses+1][2];
	for(int attIndex=0;attIndex<attributes.length;attIndex++) {
	    attributes[attIndex][0]=0.0;
	    attributes[attIndex][1]=0.0;
	    }

	// Loop through classifiers array and find rule for each
	for (int index=0;index<classifiers.length;index++) {
	    // Generate positive and negative examples from training set
	    generatePosAndNegExamples(classifiers[index]);
	    // Identify consequent of rule
	    short[] consequent = new short[1];
	    consequent[0] = classifiers[index];
	    // Process positive examples until all positive examples have been
	    // addressed.
	    //-------------------------------------------------------------------------------------------
	    double x=(double) getSizeOfExamples(positiveExamples);
	    double y=(double) getSizeOfExamples(negativeExamples);
	    //-------------------------------------------------------------------------------------------
	    while (positiveExamples!=null) {
	        // Vopy negative and positive examples and attribute array
	        positiveExamples2 = copy2DshortArray(positiveExamples);
	        negativeExamples2 = copy2DshortArray(negativeExamples);
		attributes2       = copyDouble2Darray(attributes);
		// It is possible, if no attributes with gain above the
		// specified minimum are discovered, that the example positive
		// data set P will never be reduced to null --- therefore
		// test for this situation.
		if (noValidGains()) break;
		// Proceed with generation process
	        foilGeneration(null,consequent,x,y);
		}
	    }
	}

    /* FOIL GENERATION */
    /** Uses the FOIL algorithm to generate a CAR for the given class.
    @param antecedent the rule antecedent sofar.
    @param consequent the given class.   */

    private void foilGeneration(short[] antecedent, short[] consequent,double x,double y) {
	// Determine size of previous example data sets.
	double oldPosCount = (double) getSizeOfExamples(positiveExamples2);
	double oldNegCount = (double) getSizeOfExamples(negativeExamples2);

        // Loop through attributes array and determine gain if added to rule,
	// gains placed in second row of attribute array.
    	calculateGains(antecedent,oldPosCount,oldNegCount);

	// Get index for attribute with best gain
	int indexOfBestAttribute = getBestGain();   // Inherited method
	double bestGain = attributes2[indexOfBestAttribute][1];

	// If best gain less than minimum, stop.
	if (bestGain <= MIN_BEST_GAIN) {
            foundRule(antecedent,consequent,x,y);
            return;
	    }

	// Confirm by assigning to antecedent and indicating, in attributes
	// array, that selected attribute is no longer available
	antecedent = reallocInsert(antecedent,(short) indexOfBestAttribute);
	attributes2[indexOfBestAttribute][0]=1.0;

	// Revise example datasets by removing examples that do not satisfy
	// the current rule antecedent
	positiveExamples2=removeExDoNotSatRule(antecedent,positiveExamples2);
	negativeExamples2=removeExDoNotSatRule(antecedent,negativeExamples2);
	if (antecedent.length>=MAX_NUM_ATTS_IN_ANTECEDENT ||
					negativeExamples2==null) {
            foundRule(antecedent,consequent,x,y);
	    return;
	    }

	// Repeat
	foilGeneration(antecedent,consequent,x,y);
	}

    /* FOUND CLASSIFICATION RULE */
    /** Inserts discovered rule into list of rules. <P> Note the generic rule
    array also has field for support and confidence that are not used here.
    @param antecedent the antecedent of the discovered rule.
    @param consequent the consequent of the discovered rule. */

    private void foundRule(short[] antecedent, short[] consequent,double x,double y) {
	// It is possible, if we have no negative examples (N={}), to
	// produce a rule with an empty antecedent --- such a rule should
	// not be included in the rule list.
	if (antecedent==null) return;

        // Insert discovered rule into rule list according to Laplace accuracy
        // estimate.
	double laplace = currentRlist.getLaplaceAccuracy(antecedent,
								consequent[0]);
	double ruleacc=	currentRlist.getRuleAccuracy(antecedent,
								consequent[0],x,y);
	if(orderType==1) {currentRlist.insertRuleintoRulelist(antecedent,consequent,laplace,ruleacc);}
	else currentRlist.insertRuleintoRulelist_rule(antecedent,consequent,laplace,ruleacc);

	// Revise positive examples
	positiveExamples=removeExamplesThatDoSatRule(antecedent,
							positiveExamples);
	}

    /* ------------------------------------------------------------ */
    /*                                                              */
    /*                       GAIN CALCULATIONS                      */
    /*                                                              */
    /* ------------------------------------------------------------ */

   /* CALCULATE GAINS */

   /** Loops through available attributes and determine gain for each if
   attribute added to rule. <P> Attribute is unavailable (i.e. has already been
   used in rule) if first element for attribute set to '1.0'
   @param antecedent the given antecedent.
   @param oldPosCount number of previous positive examples.
   @param oldNegCount number of previous negative examples. */

   private void calculateGains(short[] antecedent, double oldPosCount,
   						double oldNegCount) {
    	// Loop through attribute list
	for (int index=1;index<attributes2.length;index++) {
	    // If attribute not selected previously determine gain.
	    if ((int) attributes2[index][0] == 0) {
	        // Insert attribute in to rule antecedent
	        short[] tempItemSet = reallocInsert(antecedent,(short) index);
	        // Calculate gain and add to attribute array
	        attributes2[index][1] = calculateGain(tempItemSet,oldPosCount,
					oldNegCount);
		}
	    }
	}

    /* CALCULATE GAIN */

    /** Calculates gain for the given rule antecedent.
    @param antecedent the given antecedent.
    @param oldPosCount the size of the positive examples from the previous
    iteration.
    @param oldNegCount the size of the negative examples from the previous
    iteration. */

    private double calculateGain(short[] antecedent, double oldPosCount,
    		double oldNegCount) {
        // Determine number of positive examples that satisfy the new rules
	// antecedent
        double posCount = getCountExDoSatRule(antecedent,
							positiveExamples2);

	// If count is 0 gain is 0
	if ((int) posCount == 0) return(0.0);

	// Determine number of negative examples that satisfy the new rules
	// antecedent
        double negCount = getCountExDoSatRule(antecedent,
							negativeExamples2);

	// Calculate gain
	double oldGain = Math.log(oldPosCount/(oldPosCount+oldNegCount));
	double newGain =
		     Math.log((double) posCount/(double) (posCount+negCount));
	return(posCount*(newGain-oldGain));
	}

    /* GET COUNT EXAMPLES THAT DO NOT SATISFY RULE. */

    /** Get the number of positive examples that satisfy the given rule
    antecedent for the given examples.
    @param antecedent the given antecedent
    @param examples the given example set.
    @return the number of example. */

    private int getCountExDoNotSatRule(short[] antecedent,
    						short[][] examples) {
        // Check for empty set
	if (examples==null) return(0);

	// Determine number of positive examples that satisfy the given rule
	// antecedent
        int posCount=0;
	for(int index=0;index<examples.length;index++) {
	    if (!isSubset(antecedent,examples[index])) posCount++;
	    }

	// Return
	return(posCount);
	}

    /* GET COUNT EXAMPLES THAT DO SATISFY RULE. */

    /** Get the number of positive examples that satisfy the given rule
    antecedent for the given examples.
    @param antecedent the given antecedent
    @param examples the given example set.
    @return the number of example. */

    private int getCountExDoSatRule(short[] antecedent,
    						short[][] examples) {
        // Check for empty set
	if (examples==null) return(0);

	// Determine number of positive examples that satisfy the given rule
	// antecedent
        int posCount=0;
	for(int index=0;index<examples.length;index++) {
	    if (isSubset(antecedent,examples[index])) posCount++;
	    }

	// Return
	return(posCount);
	}

    /* NO VALID GAINS IN PN ARRAY */

    /** Boolean method to check whether an attribute with a gain above the
    minimum threshold exists or not. <P> It is possible, if no attributes
    with gain above the specified minimum are discovered, that the current
    example positive data set P will never be reduced to null.
    @return true if no attributes, false otherwise. */

    private boolean noValidGains() {
    	boolean noValidGains = true;
    	short[] tempItemSet = new short[1];

    	// Determine size of previous example data sets.
	double oldPosCount = (double) getSizeOfExamples(positiveExamples2);
	double oldNegCount = (double) getSizeOfExamples(negativeExamples2);

    	// Loop through attribute list
	for (int index=1;index<attributes2.length;index++) {
	    // If attribute not selected previously determine gain.
	    if ((int) attributes2[index][0] == 0) {
	        // Insert attribute in to "rule antecedent"
	        tempItemSet[0] = (short) index;
	        // Calculate gain and add to attribute array
	        double gain = calculateGain(tempItemSet,oldPosCount,
					oldNegCount);
		// Check gain
	        if (gain>MIN_BEST_GAIN) noValidGains=false;
		}
	    }

	// Return
	return(noValidGains);
	}

    /* ------------------------------------------------------------ */
    /*                                                              */
    /*                POSITIVE AND NEGATIVE EXAMPLES                */
    /*                                                              */
    /* ------------------------------------------------------------ */

    /* GENERATE POSITIVE AND NEGATIVE EXAMPLES */

    /** Generates positive and negative example data sets for the given
    class.
    @param classification the given class of interest. */

    private void generatePosAndNegExamples(short classification) {
        int posIndex=0, negIndex=0;

        // Loop through data array (training set)
        for(int index=0;index<dataArray.length;index++) {
	    // get last index
	    int lastIndex = dataArray[index].length-1;
	    if (dataArray[index][lastIndex]==classification) posIndex++;
	    else negIndex++;
	    }

	// Dimension negative and positive arrays
	if (posIndex==0) positiveExamples = null;
	else positiveExamples = new short[posIndex][];
        if (negIndex==0) negativeExamples = null;
	else negativeExamples = new short[negIndex][];

        // Loop through data array again
        posIndex=0;
        negIndex=0;
        for(int index=0;index<dataArray.length;index++) {
	    // get last index
	    int lastIndex = dataArray[index].length-1;
	    if (dataArray[index][lastIndex]==classification) {
	        positiveExamples[posIndex] = copyItemSet(dataArray[index]);
	        posIndex++;
	        }
	    else {
	        negativeExamples[negIndex] = copyItemSet(dataArray[index]);
	        negIndex++;
	        }
	    }
        }

    /* GET SIZE OF EXAMPLES DATA SET */
    /** Returns the size (length) of the given examples set.
    @param examples the given examples data set. */

    private int getSizeOfExamples(short[][] examples) {
        if (examples==null) return(0);
	else return(examples.length);
	}

    /* REMOVE EXAMPLES THAT DO NOT SATISFY RULE */

    /** Remove from given example data sets all examples that do
    not satisfy the given rule.
    @param antecedent the given antecedent for the rule.
    @param examples the given examples data set. */

    private short[][] removeExDoNotSatRule(short[] antecedent,
    					short[][] examples) {
        // Return null if input array is empty.
	if (examples==null) return(null);

	// Dimension new array
	int size = getCountExDoSatRule(antecedent,examples);
	if (size==0) return(null);
	short[][] newExamples = new short[size][];

	// Loop through given array
	int newIndex1=0;
	for (int index1=0;index1<examples.length;index1++) {
	    if (isSubset(antecedent,examples[index1])) {
	        newExamples[newIndex1] = new short[examples[index1].length];
	        for (int index2=0;index2<examples[index1].length;index2++) {
		    newExamples[newIndex1][index2]=examples[index1][index2];
		    }
		newIndex1++;
		}
	    }

	// Return
	return(newExamples);
	}

    /* REMOVE EXAMPLES THAT DO SATISFY RULE */

    /** Remove from given example data sets all examples that satisfy the
    given rule.
    @param antecedent the given antecedent for the rule.
    @param examples the given positive or negative example data sets. */

    private short[][] removeExamplesThatDoSatRule(short[] antecedent,
    							short[][] examples) {
        // Return null if input array is empty.
	if (examples==null) return(null);

	// Dimension new array
        int size = getCountExDoNotSatRule(antecedent,examples);
	if (size==0) return(null);
	short[][] newExamples = new short[size][];

	// Loop through given array
	int newIndex1=0;
	for (int index1=0;index1<examples.length;index1++) {
	    if (!isSubset(antecedent,examples[index1])) {
	        newExamples[newIndex1] = new short[examples[index1].length];
	        for (int index2=0;index2<examples[index1].length;index2++) {
		    newExamples[newIndex1][index2]=examples[index1][index2];
		    }
		newIndex1++;
		}
	    }

	// Return
	return(newExamples);
	}

    /* ---------------------------------------------------------------- */
    /*                                                                  */
    /*                               OUTPUT                             */
    /*                                                                  */
    /* ---------------------------------------------------------------- */

    /* OUTPUT SETTINGS */

    /** Outputs command line values provided by user. (Overrides higher level
    method.) */

    protected void outputSettings() {
    jtxt.append("\nSETTINGS\n--------");
	jtxt.append("\nFile name               = " + fileName);
	jtxt.append("\nNum. classes            = " + numClasses);
	jtxt.append("\nK value                 = " + K_VALUE);
	jtxt.append("\nMin, best gain          = " + MIN_BEST_GAIN);
	jtxt.append("\nMax. size of antecedent = " +MAX_NUM_ATTS_IN_ANTECEDENT);
	jtxt.append("\n");
        }

    }

