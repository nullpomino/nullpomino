package org.game_host.hebo.nullpomino.tool.airanksgenerator;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;




public class AIRanksGenerator extends JFrame implements ActionListener{

private JLabel inputFileLabel;
private JFormattedTextField inputFileField;
private JLabel outputFileLabel;
private JFormattedTextField outputFileField;
private JLabel numIterationsLabel;
//private JFormattedTextField numIterationsField;
private SpinnerNumberModel spinModel;
private JSpinner numIterationsSpinner;
private JButton goButton;
private JButton viewBestsButton;
private JButton viewWorstsButton;


public AIRanksGenerator(){
	super();
	

	setTitle("AI Ranks Generator");
	setDefaultCloseOperation(EXIT_ON_CLOSE);
    initUI();
	pack();

	setVisible(true);
}
private void initUI(){

	
	inputFileLabel=new JLabel("Input File");
	inputFileField=new JFormattedTextField();
	inputFileField.setColumns(20);
	
	outputFileLabel=new JLabel("Output File");
	outputFileField=new JFormattedTextField();
	outputFileField.setColumns(20);
	
	numIterationsLabel=new JLabel("Number of iterations");
	/*numIterationsField=new JFormattedTextField();
	numIterationsField.setColumns(20);*/
	spinModel=new SpinnerNumberModel(1,1,Integer.MAX_VALUE,1);
	numIterationsSpinner=new JSpinner(spinModel);
	goButton= new JButton("Go!");
	goButton.setActionCommand("go");
	goButton.addActionListener(this);
	
	viewBestsButton= new JButton("View Bests");
	viewBestsButton.setActionCommand("bests");
	viewBestsButton.addActionListener(this);
	
	viewWorstsButton= new JButton("View Worsts");
	viewWorstsButton.setActionCommand("worsts");
	viewWorstsButton.addActionListener(this);
	
	
	
	
	JPanel labelPane = new JPanel(new GridLayout(0,1));

	labelPane.add(inputFileLabel);
	labelPane.add(outputFileLabel);
	labelPane.add(numIterationsLabel);
	JPanel fieldPane = new JPanel(new GridLayout(0,1));


	fieldPane.add(inputFileField);
	fieldPane.add(outputFileField);
	//fieldPane.add(numIterationsField);
	fieldPane.add(numIterationsSpinner);
	//fieldPane.add(goButton);
	JPanel pane=new JPanel(new BorderLayout());
	pane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
     
	JPanel buttonsPane=new JPanel();
	buttonsPane.add(goButton);
	buttonsPane.add(viewBestsButton);
	buttonsPane.add(viewWorstsButton);
	pane.add(labelPane, BorderLayout.WEST);
    pane.add(fieldPane, BorderLayout.EAST);
    pane.add(buttonsPane,BorderLayout.SOUTH);
    add(pane);
}
public void actionPerformed(ActionEvent e) {
	if ("go".equals(e.getActionCommand())) {
		
	      new RanksIterator(this,inputFileField.getText(),outputFileField.getText(),(Integer) numIterationsSpinner.getValue());
    
	}
	else{
		Ranks ranks=null;
		
		FileInputStream fis = null;
		ObjectInputStream in = null;
		
		if (inputFileField.getText().trim().isEmpty())
			ranks=new Ranks(4,9);
		else {
			  try {
				fis = new FileInputStream(inputFileField.getText());
				   in = new ObjectInputStream(fis);
				   ranks = (Ranks)in.readObject();
				   in.close();
				  
					
			} catch (FileNotFoundException e1) {
				ranks=new Ranks(4,9);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		
		}
		
		new RanksResult(this,ranks,100,"worsts".equals(e.getActionCommand()));
		
	}
   
} 

public static void main(String[] args) {
	new AIRanksGenerator();
}
}
