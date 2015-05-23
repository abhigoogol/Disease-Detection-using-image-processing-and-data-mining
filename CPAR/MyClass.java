/*	MyClass.java

	Copyright (C) 2014	Abhishek Singh Sambyal	email:abhishek.sambyal@gmail.com
	written by LUCS-KDD group (avaliable free but not licenced).

	MyClass.java is free software: you can redistribute it and/or modify
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



import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.*;

public class MyClass
{
	JButton jbut1,jbut2,jbut3,jbut4;
	JFrame jframe1,jframe2;//Containing frames
	JPanel panel1,panel2;
	JTextField jtxt1,jtxt2,jtxt3,jtxt4;
	JComboBox jcombo1,jcombo2,jcombo3;
	JLabel jlabel;
	int i,j,k,l,m,classno;
	String f,g,f1,f2,no_of_classes;
	File inp_file,test_file;
	MyClass()
	{
    jframe1=new JFrame("VTU");
    jframe1.setLayout(new FlowLayout());
	jframe1.setSize(300,300);
	jframe1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	f=new String();
	g=new String();
	jbut1=new JButton("Pre processing\n");
	jbut2=new JButton("Start Mining");

	jbut1.addActionListener(new ActionListener()
	{
		public void actionPerformed(ActionEvent ae){pre_func();}//Preprocessing Action
		});
	jbut2.addActionListener(new ActionListener()
	{
		public void actionPerformed(ActionEvent ae){start_mining();}//Start Mining Action
		});
		jframe1.add(jbut1);
		jframe1.add(jbut2);
		jframe1.setVisible(true);
	}

	void pre_func()
	{

		jframe1.dispose();
		//call software program.
		//Preprocessing p=new Preprocessing();
	}
	void start_mining()
	{
		jframe1.dispose();
		jframe2=new JFrame("VTU");
    	jframe2.setLayout(new FlowLayout());
		jframe2.setSize(300,300);
		jframe2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe2.setVisible(true);
		jbut3=new JButton("INPUT FILE");
		jbut4=new JButton("OK");
		jlabel=new JLabel("Number of classes");
		jtxt1=new JTextField("File name",20);
		jtxt1.setEditable(false);
		jtxt3=new JTextField(10);
		jtxt3.setEditable(false);
		jtxt4=new JTextField(10);
		String algo[]={ "Select the algorithm","FOIL","PRM","CPAR"};
		String eval[]={ "Select rule ordering measure","Laplace","Stastical method"};
		String test[]={ "Select test procedure","50:50","75:25 test","new file"	};

		jcombo1=new JComboBox(algo);
		jcombo2=new JComboBox(eval);
		jcombo3=new JComboBox(test);
		jcombo1.setEnabled(false);
		jcombo2.setEnabled(false);
		jcombo3.setEnabled(false);
		jbut4.setEnabled(false);


		jbut3.addActionListener(new ActionListener()
		{
		public void actionPerformed(ActionEvent ae){i=choose_file1();if(i==1) jtxt3.setEditable(true);}//choosing file
		});
		jtxt3.addActionListener(new ActionListener()
		{
		public void actionPerformed(ActionEvent ae){
		no_of_classes=new String(jtxt3.getText());
		if(no_of_classes==null)
			JOptionPane.showMessageDialog(null,"Please enter the number of classes");
		else
		{
			jcombo1.setEnabled(true);jtxt3.setEditable(false);
		}}//entering class
		});
		jbut4.addActionListener(new ActionListener()
		{
		public void actionPerformed(ActionEvent ae){call_func();}//Ready done
		});
		jcombo1.addActionListener(new ActionListener()
		{
		public void actionPerformed(ActionEvent ae){k=jcombo1.getSelectedIndex(); if(k!=0) {jcombo2.setEnabled(true);jcombo1.setEnabled(false);}}//Combo box 1 event
		});
		jcombo2.addActionListener(new ActionListener()
		{
		public void actionPerformed(ActionEvent ae){l=jcombo2.getSelectedIndex();if(l!=0) {jcombo3.setEnabled(true);jcombo2.setEnabled(false);}}//Combo box 2 event
		});
		jcombo3.addActionListener(new ActionListener()
		{
		public void actionPerformed(ActionEvent ae){m=jcombo3.getSelectedIndex();
		if(m==3){
			j=choose_file2();
			if(j!=0) {jbut4.setEnabled(true);jcombo3.setEnabled(false);}
		}
		if(m==1) {jbut4.setEnabled(true);jtxt4.setText("50:50 Selected");jcombo3.setEnabled(false);}
		if(m==2) {jbut4.setEnabled(true);jtxt4.setText("75:25 Selected");jcombo3.setEnabled(false);}}//Combo box 3 event
		});
		jframe2.add(jbut3);
		jframe2.add(jtxt1);
		jframe2.add(jlabel);
		jframe2.add(jtxt3);
		jframe2.add(jcombo1);
		jframe2.add(jcombo2);
		jframe2.add(jcombo3);
		jframe2.add(jtxt4);
		jtxt4.setEditable(false);
		jframe2.add(jbut4);
	}

	public int choose_file1()
	{
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int result = fileChooser.showOpenDialog(jframe2);

		// If cancel button selected return
		if (result == JFileChooser.CANCEL_OPTION) return 0;

		// Obtain selected file
		inp_file = fileChooser.getSelectedFile();
		// Read file if readabale (i.e not a direcrory etc.).
		if (checkFileName(inp_file)){
	    f=inp_file.getName();jtxt1.setText(f);f1=new String(f);return 1;}
	    return 0;
	}
	public int choose_file2()
	{
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int result = fileChooser.showOpenDialog(jframe2);

		// If cancel button selected return
		if (result == JFileChooser.CANCEL_OPTION) return 0;

		// Obtain selected file
		test_file = fileChooser.getSelectedFile();
		// Read file if readabale (i.e not a direcrory etc.).
		if (checkFileName(inp_file)){
	    g=test_file.getName();jtxt4.setText(g);f2=new String(g);return 1;}
	    return 0;

	}

	 public boolean checkFileName(File fname)
	 {
		if (fname.exists()) {
	    	if (fname.canRead()) {
				if (fname.isFile()) return(true);
					else JOptionPane.showMessageDialog(null,"FILE ERROR: File is a directory");
	}
	else JOptionPane.showMessageDialog(null,"FILE ERROR: Access denied");
	    }
	else JOptionPane.showMessageDialog(null,"FILE ERROR: No such file!");
	// Return

	return(false);
	}

	void call_func()
	{
		switch(k)
		{
			case 1:if(m!=3) {ClassFOIL_App A=new ClassFOIL_App(inp_file,no_of_classes,m,l);}
					else
					{ClassFOIL_App A=new ClassFOIL_App(inp_file,test_file,no_of_classes,m,l);}
					break;
			case 2:if(m!=3) {ClassPRM_App B=new ClassPRM_App(inp_file,no_of_classes,m,l);}
					else {ClassPRM_App B=new ClassPRM_App(inp_file,test_file,no_of_classes,m,l);}
					break;
			case 3:if(m!=3) {ClassCPAR_App A=new ClassCPAR_App(inp_file,no_of_classes,m,l);}
					else
					{ClassCPAR_App A=new ClassCPAR_App(inp_file,test_file,no_of_classes,m,l);}
					break;
		}




	}

	public static void main(String args[])
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				new MyClass();
			}
		});
	}


}
