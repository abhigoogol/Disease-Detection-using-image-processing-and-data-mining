/*	AssocRuleMining.java and this program Set of general CARM (and ARM) utility methods to allow: (i) data input and input error checking, (ii) data preprocessing, (iii) manipulation of records (e.g. operations such as subset, member, union etc.) and (iv) data and parameter output.

	Copyright (C) 2014	Abhishek Singh Sambyal	email:abhishek.sambyal@gmail.com
	written by LUCS-KDD group (avaliable free but not licenced).

	AssocRuleMining.java is free software: you can redistribute it and/or modify
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


// Java packages
import java.io.*;
import java.util.*;

// Java GUI packages
import javax.swing.*;
//---------------------------------------------------------------------------------------
import java.awt.*;
import java.awt.event.*;
//----------------------------------------------------

/** Set of utilities to support various Association Rule Mining (ARM)
algorithms included in the LUCS-KDD suite of ARM programs.
@author Frans Coenen
@version 2 July 2003 */

public class AssocRuleMining extends JFrame {

    /* ------ FIELDS ------ */

    // Data structures

    /** 2-D aray to hold input data from data file */
    protected short[][] dataArray = null;
    protected short[][] testDataArray = null;


    // Command line arguments with default values and associated fields

    /** Command line argument for data file name. */
    protected String  fileName = null;
    protected String testfileName= null;
    /** Number of classes in input data set (input by the user). */
    protected int numClasses   = 0;

    // Flags

    /** Error flag used when checking command line arguments (default =
    <TT>true</TT>). */
    protected boolean errorFlag  = true;
    /** Input format OK flag( default = <TT>true</TT>). */
    protected boolean inputFormatOkFlag = true;
    //-----------------------------------------------------------------------------

	static JTextArea jtxt;

	//-----------------------------------------------------------------------------

    // Other fields

    /** Number of columns. */
    protected int     numCols    = 0;
    /** Number of rows. */
    protected int     numRows    = 0,numtestRows=0;
    /** The number of one itemsets (singletons). */
    protected int numOneItemSets = 0;
    /** The input stream. */
    protected BufferedReader fileInput;
    /** The file path */
    protected File filePath = null;
    protected File testfilePath=null;
     int testType=0;
    int orderType=0;

    /* ------ CONSTRUCTORS ------ */

    /** Processes command line arguments */

    public AssocRuleMining(File inp,String cno,int t,int o) {
    	testType=t;
    	orderType=o;
    	//--------------------------------------------------------------------------------------
 JPanel panel=new JPanel();
jtxt=new JTextArea(100,100);
panel.add(jtxt);


//Create a scrollbar using JScrollPane and add panel into it's viewport
//Set vertical and horizontal scrollbar always show
JScrollPane scrollBar=new JScrollPane(panel,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

//Create a JFrame with title ( AddScrollBarToJFrame )
JFrame frame=new JFrame("AddScrollBarToJFrame");

//Add JScrollPane into JFrame
frame.add(scrollBar);

//Set close operation for JFrame
frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

//Set JFrame size
frame.setSize(400,400);

//Make JFrame visible. So we can see it.
frame.setVisible(true);


//----------------------------------------------------------------------------------------------

	// Process command line arguments
 	filePath=inp;
	fileName = filePath.getName();
	numClasses =  Integer.parseInt(cno);
	// If command line arguments read successfully (errorFlag set to "true")
	// check validity of arguments
	if (errorFlag) CheckInputArguments();
	else outputMenu();
        }
         public AssocRuleMining(File inp,File test,String cno,int t,int o) {
    	testType=t;
    	orderType=o;
    	//--------------------------------------------------------------------------------------
 JPanel panel=new JPanel();
jtxt=new JTextArea(100,100);
panel.add(jtxt);


//Create a scrollbar using JScrollPane and add panel into it's viewport
//Set vertical and horizontal scrollbar always show
JScrollPane scrollBar=new JScrollPane(panel,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

//Create a JFrame with title ( AddScrollBarToJFrame )
JFrame frame=new JFrame("AddScrollBarToJFrame");

//Add JScrollPane into JFrame
frame.add(scrollBar);

//Set close operation for JFrame
frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

//Set JFrame size
frame.setSize(400,400);

//Make JFrame visible. So we can see it.
frame.setVisible(true);


//----------------------------------------------------------------------------------------------

	// Process command line arguments
    filePath=inp;
	fileName = filePath.getName();
	numClasses =  Integer.parseInt(cno);

	testfilePath=test;
	testfileName = testfilePath.getName();




	// If command line arguments read successfully (errorFlag set to "true")
	// check validity of arguments
	if (errorFlag) CheckInputArguments();
	else outputMenu();
        }


    /** Default constructor */

    public AssocRuleMining() {
        }

    /* ------ METHODS ------ */

    /* ---------------------------------------------------------------- */
    /*                                                                  */
    /*                        COMMAND LINE ARGUMENTS                    */
    /*                                                                  */
    /* ---------------------------------------------------------------- */

    /* IDENTIFY ARGUMENT */
    /** Identifies nature of individual command line arguments:
    -N = number of classes, -F = file name, -S = support. */



    /* CHECK INPUT ARGUMENTS */
    /** Invokes methods to check values associate with command line
    arguments */

    protected void CheckInputArguments() {
    	/* STUB */
	}

    /* CHECK FILE NAME */
    /** Checks if data file name provided, if not <TT>errorFlag</TT> set
    to <TT>false</TT>. */

    protected void checkFileName() {
	if (fileName == null) {
	    jtxt.append("\nINPUT ERROR: Must specify file name (-F)");
            errorFlag = false;
	    }
	}

    /* ---------------------------------------------------------------- */
    /*                                                                  */
    /*                     READ INPUT DATA FROM FILE                    */
    /*                                                                  */
    /* ---------------------------------------------------------------- */

    /* READ FILE */
    /** Reads input data from file specified in command line argument (GUI
    version also exists). <P>Proceeds as follows:
    <OL>
    <LI>Gets number of lines in file, checking format of each line (space
    separated integers), if incorrectly formatted line found
    <TT>inputFormatOkFlag</TT> set to <TT>false</TT>.
    <LI>Dimensions input array.
    <LI>Reads data
    </OL> */

    protected void readFile() {
        try {
	    // Dimension data structure
	    inputFormatOkFlag=true;
	    numRows = getNumberOfLines(fileName);
	    if (inputFormatOkFlag) {
	        dataArray = new short[numRows][];
	        // Read file
		jtxt.append("\nReading input file: " + fileName);
	        readInputDataSet();
		}
	    else jtxt.append("\nError reading file: " + fileName + "\n");
	    }
	catch(IOException ioException) {
	    jtxt.append("\nError reading File");
	    closeFile();
	    System.exit(1);
	    }
	}
	//--------------------------------------------------------------------------------------------------
	   protected void readtestFile() {
        try {
	    // Dimension data structure
	    inputFormatOkFlag=true;
	    numtestRows = gettestNumberOfLines(testfileName);
	    if (inputFormatOkFlag) {
	        testDataArray = new short[numtestRows][];
	        // Read file
		jtxt.append("\nReading test file: " + testfileName);
	        readInputDataSet();
		}
	    else jtxt.append("\nError reading file: " + fileName + "\n");
	    }
	catch(IOException ioException) {
	    jtxt.append("\nError reading File");
	    closeFile();
	    System.exit(1);
	    }
	}
	//------------------------------------------------------------------------------------------------------

    /* GET NUMBER OF LINES */

    /** Gets number of lines/records in input file and checks format of each
    line.
    @param nameOfFile the filename of the file to be opened.
    @return the number pf rows in the given file. */

    protected int getNumberOfLines(String nameOfFile) throws IOException {
        int counter = 0;

	// Open the file
	if (filePath==null) openFileName(nameOfFile);
	else openFilePath();

	// Loop through file incrementing counter
	// get first row.
	String line = fileInput.readLine();
	while (line != null) {
	    checkLine(counter+1,line);
	    StringTokenizer dataLine = new StringTokenizer(line);
            int numberOfTokens = dataLine.countTokens();
	    if (numberOfTokens == 0) break;
	    counter++;
            line = fileInput.readLine();
	    }

	// Close file and return
        closeFile();
	return(counter);
	}
	//---------------------------------------------------------------------------------------------------------
	 protected int gettestNumberOfLines(String nameOfFile) throws IOException {
        int counter = 0;

	// Open the file
	if (testfilePath==null) openFileName(nameOfFile);
	else opentestFilePath();

	// Loop through file incrementing counter
	// get first row.
	String line = fileInput.readLine();
	while (line != null) {
	    checkLine(counter+1,line);
	    StringTokenizer dataLine = new StringTokenizer(line);
            int numberOfTokens = dataLine.countTokens();
	    if (numberOfTokens == 0) break;
	    counter++;
            line = fileInput.readLine();
	    }

	// Close file and return
        closeFile();
	return(counter);
	}
	//---------------------------------------------------------------------------------------------------------

    /* CHECK LINE */

    /** Check whether given line from input file is of appropriate format
    (space separated integers), if incorrectly formatted line found
    <TT>inputFormatOkFlag</TT> set to <TT>false</TT>.
    @param counter the line number in the input file.
    @param str the current line from the input file. */

    protected void checkLine(int counter, String str) {

        for (int index=0;index <str.length();index++) {
            if (!Character.isDigit(str.charAt(index)) &&
	    			!Character.isWhitespace(str.charAt(index))) {
		JOptionPane.showMessageDialog(null,"FILE INPUT ERROR:\n" +
					"charcater on line " + counter +
					" is not a digit or white space");
		inputFormatOkFlag = false;
		break;
		}
	    }
	}

    /* READ INPUT DATA SET */
    /** Reads input data from file specified in command line argument. */

    public void readInputDataSet() throws IOException {
        int rowIndex=0;

	// Open the file
	if (filePath==null) openFileName(fileName);
	else openFilePath();

	// get first row.
	String line = fileInput.readLine();
	while (line != null) {
	    StringTokenizer dataLine = new StringTokenizer(line);
            int numberOfTokens = dataLine.countTokens();
	    if (numberOfTokens == 0) break;
	    // Convert input string to a sequence of short integers
	    short[] code = binConversion(dataLine,numberOfTokens);
	    // Check for "null" input
	    if (code != null) {
	        // Dimension row in 2-D dataArray
		int codeLength = code.length;
		dataArray[rowIndex] = new short[codeLength];
		// Assign to elements in row
		for (int colIndex=0;colIndex<codeLength;colIndex++)
				dataArray[rowIndex][colIndex] = code[colIndex];
		}
	    else dataArray[rowIndex]= null;
	    // Increment first index in 2-D data array
	    rowIndex++;
	    // get next line
            line = fileInput.readLine();
	    }

	// Close file
	closeFile();
	}

	//--------------------------------------------------------------------------------------------------------
	    public void readTestDataSet() throws IOException {
        int rowIndex=0;

	// Open the file
	if (testfilePath==null) openFileName(testfileName);
	else opentestFilePath();

	// get first row.
	String line = fileInput.readLine();
	while (line != null) {
	    StringTokenizer dataLine = new StringTokenizer(line);
            int numberOfTokens = dataLine.countTokens();
	    if (numberOfTokens == 0) break;
	    // Convert input string to a sequence of short integers
	    short[] code = binConversion(dataLine,numberOfTokens);
	    // Check for "null" input
	    if (code != null) {
	        // Dimension row in 2-D dataArray
		int codeLength = code.length;
		testDataArray[rowIndex] = new short[codeLength];
		// Assign to elements in row
		for (int colIndex=0;colIndex<codeLength;colIndex++)
				testDataArray[rowIndex][colIndex] = code[colIndex];
		}
	    else testDataArray[rowIndex]= null;
	    // Increment first index in 2-D data array
	    rowIndex++;
	    // get next line
            line = fileInput.readLine();
	    }

	// Close file
	closeFile();
	}
	//-------------------------------------------------------------------------------------------------------

    /* CHECK DATASET ORDERING */
    /** Checks that data set is ordered correctly. */

    protected boolean checkOrdering() {
        boolean result = true;

	// Loop through input data
	for(int index=0;index<dataArray.length;index++) {
	    if (!checkLineOrdering(index+1,dataArray[index])) result=false;
	    }

	// Return
	return(result);
	}

    /* CHECK LINE ORDERING */
    /** Checks whether a given line in the input data is in numeric sequence.
    @param lineNum the line number.
    @param itemSet the item set represented by the line
    @return true if OK and false otherwise. */

    private boolean checkLineOrdering(int lineNum, short[] itemSet) {
        for (int index=0;index<itemSet.length-1;index++) {
	    if (itemSet[index] >= itemSet[index+1]) {
		JOptionPane.showMessageDialog(null,"FILE FORMAT ERROR:\n" +
	       		"Attribute data in line " + lineNum +
			" not in numeric order");
		return(false);
		}
	    }

	// Default return
	return(true);
	}

    /* COUNT NUMBER OF COLUMNS */
    /** Counts number of columns represented by input data. */

    protected void countNumCols() {
        int maxAttribute=0;

	// Loop through data array
        for(int index=0;index<dataArray.length;index++) {
	    int lastIndex = dataArray[index].length-1;
	    if (dataArray[index][lastIndex] > maxAttribute)
	    		maxAttribute = dataArray[index][lastIndex];
	    }

	numCols        = maxAttribute;
	numOneItemSets = numCols; 	// default value only
	}

    /* OPEN FILE NAME */
    /** Opens file using fileName (instance field).
    @param nameOfFile the filename of the file to be opened. */

    protected void openFileName(String nameOfFile) {
	try {
	    // Open file
	    FileReader file = new FileReader(nameOfFile);
	    fileInput = new BufferedReader(file);
	    }
	catch(IOException ioException) {
	    JOptionPane.showMessageDialog(this,"Error Opening File",
			 "Error: ",JOptionPane.ERROR_MESSAGE);
	    }
	}

    /* OPEN FILE PATH */
    /** Opens file using filePath (instance field). */

    private void openFilePath() {
	try {
	    // Open file
	    FileReader file = new FileReader(filePath);
	    fileInput = new BufferedReader(file);
	    }
	catch(IOException ioException) {
	    JOptionPane.showMessageDialog(this,"Error Opening File",
			 "Error: ",JOptionPane.ERROR_MESSAGE);
	    }
	}
	//-----------------------------------------------------------------------------------------
	 private void opentestFilePath() {
	try {
	    // Open file
	    FileReader file = new FileReader(testfilePath);
	    fileInput = new BufferedReader(file);
	    }
	catch(IOException ioException) {
	    JOptionPane.showMessageDialog(this,"Error Opening test File",
			 "Error: ",JOptionPane.ERROR_MESSAGE);
	    }
	}
	//------------------------------------------------------------------------------------------

    /* CLOSE FILE */
    /** Close file fileName (instance field). */

    protected void closeFile() {
        if (fileInput != null) {
	    try {
	    	fileInput.close();
		}
	    catch (IOException ioException) {
		JOptionPane.showMessageDialog(this,"Error Closing File",
			 "Error: ",JOptionPane.ERROR_MESSAGE);
		}
	    }
	}

    /* BINARY CONVERSION. */

    /** Produce an item set (array of elements) from input line.
    @param dataLine row from the input data file
    @param numberOfTokens number of items in row
    @return 1-D array of short integers representing attributes in input
    row */

    protected short[] binConversion(StringTokenizer dataLine,
    				int numberOfTokens) {
        short number;
	short[] newItemSet = null;

	// Load array

	for (int tokenCounter=0;tokenCounter < numberOfTokens;tokenCounter++) {
            number = new Short(dataLine.nextToken()).shortValue();
	    newItemSet = realloc1(newItemSet,number);
	    }

	// Return itemSet

	return(newItemSet);
	}

    /* ----------------------------------------------- */
    /*                                                 */
    /*        ITEM SET INSERT AND ADD METHODS          */
    /*                                                 */
    /* ----------------------------------------------- */

    /* REALLOC INSERT */

    /** Resizes given item set so that its length is increased by one
    and new element inserted.
    @param oldItemSet the original item set
    @param newElement the new element/attribute to be inserted
    @return the combined item set */

    protected short[] reallocInsert(short[] oldItemSet, short newElement) {

	// No old item set

	if (oldItemSet == null) {
	    short[] newItemSet = {newElement};
	    return(newItemSet);
	    }

	// Otherwise create new item set with length one greater than old
	// item set

	int oldItemSetLength = oldItemSet.length;
	short[] newItemSet = new short[oldItemSetLength+1];

	// Loop

	int index1;
	for (index1=0;index1 < oldItemSetLength;index1++) {
	    if (newElement < oldItemSet[index1]) {
		newItemSet[index1] = newElement;
		// Add rest
		for(int index2 = index1+1;index2<newItemSet.length;index2++)
				newItemSet[index2] = oldItemSet[index2-1];
		return(newItemSet);
		}
	    else newItemSet[index1] = oldItemSet[index1];
	    }

	// Add to end

	newItemSet[newItemSet.length-1] = newElement;

	// Return new item set

	return(newItemSet);
	}

    /* REALLOC 1 */

    /** Resizes given item set so that its length is increased by one
    and appends new element (identical to append method)
    @param oldItemSet the original item set
    @param newElement the new element/attribute to be appended
    @return the combined item set */

    protected short[] realloc1(short[] oldItemSet, short newElement) {

	// No old item set

	if (oldItemSet == null) {
	    short[] newItemSet = {newElement};
	    return(newItemSet);
	    }

	// Otherwise create new item set with length one greater than old
	// item set

	int oldItemSetLength = oldItemSet.length;
	short[] newItemSet = new short[oldItemSetLength+1];

	// Loop

	int index;
	for (index=0;index < oldItemSetLength;index++)
		newItemSet[index] = oldItemSet[index];
	newItemSet[index] = newElement;

	// Return new item set

	return(newItemSet);
	}

    /* ---------------------------------------------------------------- */
    /*                                                                  */
    /*              METHODS TO RETURN SUBSETS OF ITEMSETS               */
    /*                                                                  */
    /* ---------------------------------------------------------------- */

    /* GET LAST ELEMENT */

    /** Gets thelast element in the given item set, or '0' if the itemset is
    empty.
    @param itemSet the given item set.
    @return the last element. */

    protected short getLastElement(short[] itemSet) {
        // Check for empty item set
	if (itemSet == null) return(0);
	// Otherwise return last element
        return(itemSet[itemSet.length-1]);
	}

    /* ----------------------------------------------------- */
    /*                                                       */
    /*             BOOLEAN ITEM SET METHODS ETC.             */
    /*                                                       */
    /* ----------------------------------------------------- */

    /* EQUALITY CHECK */

    /** Checks whether two item sets are the same.
    @param itemSet1 the first item set.
    @param itemSet2 the second item set to be compared with first.
    @return true if itemSet1 is equal to itemSet2, and false otherwise. */

    protected boolean isEqual(short[] itemSet1, short[] itemSet2) {

	// If no itemSet2 (i.e. itemSet2 is null return false)

	if (itemSet2 == null) return(false);

	// Compare sizes, if not same length they cannot be equal.

	int length1 = itemSet1.length;
	int length2 = itemSet2.length;
	if (length1 != length2) return(false);

        // Same size compare elements

        for (int index=0;index < length1;index++) {
	    if (itemSet1[index] != itemSet2[index]) return(false);
	    }

        // itemSet the same.

        return(true);
        }

    /* ----------------------------------------------------- */
    /*                                                       */
    /*             BOOLEAN ITEM SET METHODS ETC.             */
    /*                                                       */
    /* ----------------------------------------------------- */

    /* SUBSET CHECK */

    /** Checks whether one item set is subset of a second item set.
    @param itemSet1 the first item set.
    @param itemSet2 the second item set to be compared with first.
    @return true if itemSet1 is a subset of itemSet2, and false otherwise.
    */

    protected boolean isSubset(short[] itemSet1, short[] itemSet2) {
	// Check for empty itemsets
	if (itemSet1==null) return(true);
	if (itemSet2==null) return(false);

	// Loop through itemSet1
	for(int index1=0;index1<itemSet1.length;index1++) {
	    if (notMemberOf(itemSet1[index1],itemSet2)) return(false);
	    }

	// itemSet1 is a subset of itemSet2
	return(true);
	}

    /* NOT MEMBER OF */

    /** Checks whether a particular element/attribute identified by a
    column number is not a member of the given item set.
    @param number the attribute identifier (column number).
    @param itemSet the given item set.
    @return true if first argument is not a member of itemSet, and false
    otherwise */

    protected boolean notMemberOf(short number, short[] itemSet) {

	// Loop through itemSet

	for(int index=0;index<itemSet.length;index++) {
	    if (number < itemSet[index]) return(true);
	    if (number == itemSet[index]) return(false);
	    }

	// Got to the end of itemSet and found nothing, return false

	return(true);
	}

    /* ---------------------------------------------------------------- */
    /*                                                                  */
    /*                            MISCELANEOUS                          */
    /*                                                                  */
    /* ---------------------------------------------------------------- */

    /* COPY ITEM SET */

    /** Makes a copy of a given itemSet.
    @param itemSet the given item set.
    @return copy of given item set. */

    protected short[] copyItemSet(short[] itemSet) {

	// Check whether there is a itemSet to copy
	if (itemSet == null) return(null);

	// Do copy and return
	short[] newItemSet = new short[itemSet.length];
	for(int index=0;index<itemSet.length;index++) {
	    newItemSet[index] = itemSet[index];
	    }

	// Return
	return(newItemSet);
	}

    /* ------------------------------------------------- */
    /*                                                   */
    /*                   OUTPUT METHODS                  */
    /*                                                   */
    /* ------------------------------------------------- */

    /* ----------------- */
    /* OUTPUT DATA TABLE */
    /* ----------------- */
    /** Outputs stored input data set; initially read from input data file, but
    may be reordered or pruned if desired by a particular application. */

    public void outputDataArray() {
       jtxt.append("\nDATA SET\n" + "--------\n");

	// Loop through data array
        for(int index=0;index<dataArray.length;index++) {
	    outputItemSet(dataArray[index]);
	    jtxt.append("\n");
	    }
	}

    /* -------------- */
    /* OUTPUT ITEMSET */
    /* -------------- */
    /** Outputs a given item set.
    @param itemSet the given item set. */

    protected void outputItemSet(short[] itemSet) {

	// Loop through item set elements

	if (itemSet == null) jtxt.append("\n null ");
	else {
            int counter = 0;
	    for (int index=0;index<itemSet.length;index++) {
	        if (counter == 0) {
	    	    counter++;
		    jtxt.append(" {");
		    }
	        else jtxt.append(" ");
	        jtxt.append(Short.toString(itemSet[index]));
		}
	    jtxt.append("} ");
	    }
	}

    /* ---------------------- */
    /* OUTPUT DATA ARRAY SIZE */
    /* ---------------------- */
    /** Outputs size (number of records and number of elements) of stored
    input data set read from input data file. */

    public void outputDataArraySize() {
    	int numRecords = 0;
	int numElements = 0;

	// Loop through data array

	for (int index=0;index<dataArray.length;index++) {
	    if (dataArray[index] != null) {
	        numRecords++;
		numElements = numElements+dataArray[index].length;
	        }
	    }

	// Output
	jtxt.append("\nNumber of columns  = " + numCols);
	jtxt.append("\nNumber of records  = " + numRecords);
	jtxt.append("\nNumber of elements = " + numElements);
	double density = (double) numElements/ (numCols*numRecords);
		jtxt.append("\nData set density   = " + twoDecPlaces(density) +"%\n");
	}

    /* ----------- */
    /* OUTPUT MENU */
    /* ----------- */
    /** Outputs menu for command line arguments. */

    protected void outputMenu() {
        jtxt.append("\n");
	jtxt.append("\n-F  = File name");
	jtxt.append("\nN  = Number of classes (Optional)");
	jtxt.append("\n");

	// Exit

	System.exit(1);
	}
    /* --------------- */
    /* OUTPUT SETTINGS */
    /* --------------- */
    /** Outputs command line values provided by user. */

    protected void outputSettings() {
        /* STUB */
        }

    /* --------------------------------- */
    /*                                   */
    /*        DIAGNOSTIC OUTPUT          */
    /*                                   */
    /* --------------------------------- */

    /* OUTPUT DURATION */
    /** Outputs difference between two given times.
    @param time1 the first time.
    @param time2 the second time.
    @return duration. */

    public double outputDuration(double time1, double time2) {
        double duration = (time2-time1)/1000;
	jtxt.append("\nGeneration time = " + twoDecPlaces(duration) +
	             " seconds (" + twoDecPlaces(duration/60) + " mins)");

	// Return
	return(duration);
	}

    /* -------------------------------- */
    /*                                  */
    /*        OUTPUT UTILITIES          */
    /*                                  */
    /* -------------------------------- */

    /* TWO DECIMAL PLACES */

    /** Converts given real number to real number rounded up to two decimal
    places.
    @param number the given number.
    @return the number to two decimal places. */

    protected double twoDecPlaces(double number) {
    	int numInt = (int) ((number+0.005)*100.0);
	number = ((double) numInt)/100.0;
	return(number);
	}
    }

