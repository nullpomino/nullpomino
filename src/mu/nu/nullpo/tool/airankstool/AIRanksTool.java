package mu.nu.nullpo.tool.airankstool;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import org.apache.log4j.Logger;
import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.GroupLayout.ParallelGroup;

import mu.nu.nullpo.util.CustomProperties;

public class AIRanksTool extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	/** Log */
	static final Logger log = Logger.getLogger(AIRanksTool.class);

	/** Default language file */
	public static CustomProperties propLangDefault;
	/** Primary language file */
	public static CustomProperties propLang;

	/** UI */

	//****************************
	//Tab 1 (Generation) variables
	//****************************

	// Input File
	private JLabel inputFileLabel;
	private JComboBox inputFileComboBox;

	// Output File
	private JLabel outputFileLabel;
	private JTextField outputFileField;

	//Number of Iterations
	private JLabel numIterationsLabel;
	private SpinnerNumberModel spinModel;
	private JSpinner numIterationsSpinner;

	// Generation Button
	private JButton goButton;

	// View the best surfaces button
	private JButton viewBestsButton;

	// View the worst surfaces button
	private JButton viewWorstsButton;


	//***************************
	//Tab 2 (AI Config) variables
	//***************************

	//Ranks File Used
	private JLabel ranksFileUsedLabel;
	private JComboBox ranksFileUsedComboBox;

	//Number of previews Used
	private JLabel numPreviewsLabel;
	private SpinnerNumberModel numPreviewsSpinModel;
	private JSpinner numPreviewsSpinner;

	//Allow Hold or not
	private JLabel allowHoldLabel;
	private JCheckBox allowHoldCheckBox;

	//Speed Limit
	private JLabel speedLimitLabel;
	private JFormattedTextField speedLimitField;

	// Save AI Config Button
	private JButton saveAIConfigButton;
	private JTabbedPane tabbedPane;

	//***************************
	//Tab 3 (Ranks Info) variables
	//***************************

	//Ranks File To get info from
	private JLabel ranksFileInfoLabel;
	private JComboBox ranksFileInfoComboBox;



	//*****************
	//Default variables
	//*****************

	public static String RANKSAI_DIR="res/ranksAI/";
	public static String RANKSAI_CONFIG_FILE="config/setting/ranksai.cfg";
	public static String DEFAULT_RANKS_FILE="ranks";
	private String newFileText;

	public AIRanksTool() {
		super();

		setTitle(getUIText("Main_Title"));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		initUI();
		pack();

		setVisible(true);
	}

	private void initUI() {

		// Loads Ranks AI property file, to populate the fields
		CustomProperties propRanksAI = new CustomProperties();
		try {
			FileInputStream in = new FileInputStream(AIRanksTool.RANKSAI_CONFIG_FILE);
			propRanksAI.load(in);
			in.close();
		} catch (IOException e) {}

		//Ranks File used
		String ranksFile=propRanksAI.getProperty("ranksai.file");

		//Number of previews used
		int numPreviews=propRanksAI.getProperty("ranksai.numpreviews", 2);

		//Allow Hold ?
		boolean allowHold=propRanksAI.getProperty("ranksai.allowhold", false);

		//Speed Limit
		int speedLimit=propRanksAI.getProperty("ranksai.speedlimit", 0);

		// Loads the ranks file list from the ranksAI directory (/res/ranksai)
		String [] children=new File(RANKSAI_DIR).list();

		int fileIndex=-1;

		//Find the index of default Ranks File
		if (children != null){

			if (ranksFile!=null){
				fileIndex=-1;
				for (int i=0;i<children.length;i++){
					if (children[i].equals(ranksFile)){
						fileIndex=i;
					}
				}
			}
			String []ranksList;
			ranksList=new String[children.length+1];
			System.arraycopy(children, 0, ranksList, 1, children.length);
		}


		//Tab 1 (Generation)

		// Input File Label
		inputFileLabel = new JLabel(getUIText("Main_Input_Label"));

		// Add the New File entry to combobox list
		String []ranksList;
		if (children!=null){
			ranksList=new String[children.length+1];
			System.arraycopy(children, 0, ranksList, 1, children.length);
		}
		else {
			ranksList=new String[1];
		}
		newFileText=getUIText("Main_New_File_Text");
		ranksList[0]=newFileText;

		// Creates the combo box
		inputFileComboBox=new JComboBox(ranksList);
		inputFileComboBox.setSelectedIndex(fileIndex+1);
		inputFileComboBox.setToolTipText(getUIText("Main_Input_Tip"));
		inputFileComboBox.setActionCommand("input");
		inputFileComboBox.addActionListener(this);


		// Output File
		outputFileLabel = new JLabel(getUIText("Main_Output_Label"));
		outputFileField = new JTextField(DEFAULT_RANKS_FILE);
		outputFileField.setColumns(20);
		if (inputFileComboBox.getSelectedIndex()>0){
			outputFileField.setText((String) inputFileComboBox.getSelectedItem());
		}
		outputFileField.setToolTipText(getUIText("Main_Output_Tip"));


		//Number of iterations to run
		numIterationsLabel = new JLabel(getUIText("Main_Iterations_Label"));
		spinModel = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1);
		numIterationsSpinner = new JSpinner(spinModel);
		numIterationsSpinner.setToolTipText(getUIText("Main_Iterations_Tip"));

		//Go button (starts the generation)
		goButton = new JButton(getUIText("Main_Go_Label"));
		goButton.setActionCommand("go");
		goButton.addActionListener(this);
		goButton.setToolTipText(getUIText("Main_Go_Tip"));
		goButton.setMnemonic('G');

		// View Bests Button
		viewBestsButton = new JButton(getUIText("Main_Bests_Label"));
		viewBestsButton.setActionCommand("bests");
		viewBestsButton.addActionListener(this);
		viewBestsButton.setToolTipText(getUIText("Main_Bests_Tip"));
		viewBestsButton.setMnemonic('B');

		// View worsts Button
		viewWorstsButton = new JButton(getUIText("Main_Worsts_Label"));
		viewWorstsButton.setActionCommand("worsts");
		viewWorstsButton.addActionListener(this);
		viewWorstsButton.setToolTipText(getUIText("Main_Worsts_Tip"));
		viewWorstsButton.setMnemonic('W');

		//*******************************************************************

		//Tab 2

		//Ranks File Used
		ranksFileUsedLabel=new JLabel(getUIText("Main_Ranks_File_Used_Label"));

		if (fileIndex>=0){
			ranksFileUsedComboBox=new JComboBox(children);

			ranksFileUsedComboBox.setSelectedIndex(fileIndex);
		}
		else {

			if ((children==null)||(children.length==0)){
				String[] list={" "};
				ranksFileUsedComboBox=new JComboBox(list);
			}

				ranksFileUsedComboBox.setSelectedIndex(0);

		}
		ranksFileUsedComboBox.setToolTipText(getUIText("Main_Ranks_File_Used_Tooltip"));
		ranksFileUsedComboBox.setActionCommand("input2");
		ranksFileUsedComboBox.addActionListener(this);


		//Number of previews to use
		numPreviewsLabel = new JLabel(getUIText("Main_Num_Previews_Label"));
		numPreviewsSpinModel = new SpinnerNumberModel(2, 0, Integer.MAX_VALUE, 1);
		numPreviewsSpinner = new JSpinner(numPreviewsSpinModel);
		if (numPreviews>=0){
			numPreviewsSpinner.setValue((Integer) numPreviews);
		}
		numPreviewsSpinner.setToolTipText(getUIText("Main_Num_Previews_Tip"));


		//Switch to allow hold
		allowHoldLabel=new JLabel(getUIText("Main_Allow_Hold"));
		allowHoldCheckBox= new JCheckBox();
		allowHoldCheckBox.setSelected(allowHold);
		allowHoldCheckBox.setToolTipText(getUIText("Main_Allow_Hold_Tip"));

		//Speed Limit
		speedLimitLabel=new JLabel(getUIText("Main_Speed_Limit_Label"));
		speedLimitField=new JFormattedTextField(new Integer(speedLimit));
		speedLimitField.setToolTipText(getUIText("Main_Speed_Limit_Tip"));

		// Save config Button
		saveAIConfigButton=new JButton(getUIText("Main_Set_Default_Label"));
		saveAIConfigButton.setActionCommand("default");
		saveAIConfigButton.addActionListener(this);
		saveAIConfigButton.setToolTipText(getUIText("Main_Set_Default_Tip"));
		saveAIConfigButton.setMnemonic('S');


		//*************************************************************************
		// Generates the panels

		// Tab 1
		JPanel formPane=new JPanel();
		GroupLayout layout=new GroupLayout(formPane);
		formPane.setLayout(layout);
		layout.setAutocreateGaps(true);
		layout.setAutocreateContainerGaps(true);
		   GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		   ParallelGroup labelsPg=layout.createParallelGroup();
		   labelsPg.add(inputFileLabel);
		   labelsPg.add(outputFileLabel);
		   labelsPg.add(numIterationsLabel);
		   hGroup.add(labelsPg);

		   ParallelGroup fieldsPg=layout.createParallelGroup();
			fieldsPg.add(inputFileComboBox);
			fieldsPg.add(outputFileField);
			fieldsPg.add(numIterationsSpinner);
		   hGroup.add(fieldsPg);

		   layout.setHorizontalGroup(hGroup);


		   GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		   vGroup.add(layout.createParallelGroup(GroupLayout.BASELINE).
		            add(inputFileLabel).add(inputFileComboBox));
		   vGroup.add(layout.createParallelGroup(GroupLayout.BASELINE).
		            add(outputFileLabel).add(outputFileField));
		   vGroup.add(layout.createParallelGroup(GroupLayout.BASELINE).
		            add(numIterationsLabel).add(numIterationsSpinner));

		   layout.setVerticalGroup(vGroup);

		JPanel buttonsPane = new JPanel();
		buttonsPane.add(goButton);
		buttonsPane.add(viewBestsButton);
		buttonsPane.add(viewWorstsButton);

	JPanel pane1 = new JPanel(new BorderLayout());

		pane1.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		pane1.add(formPane,BorderLayout.CENTER);
		pane1.add(buttonsPane, BorderLayout.SOUTH);

		// Tab 2

		JPanel formPane2=new JPanel();
		GroupLayout layout2=new GroupLayout(formPane2);
		formPane2.setLayout(layout2);
		layout2.setAutocreateGaps(true);
		layout2.setAutocreateContainerGaps(true);
		   GroupLayout.SequentialGroup hGroup2 = layout2.createSequentialGroup();

		   ParallelGroup labelsPg2=layout2.createParallelGroup();
		   labelsPg2.add(ranksFileUsedLabel);
		   labelsPg2.add(numPreviewsLabel);
		   labelsPg2.add(allowHoldLabel);
		   labelsPg2.add(speedLimitLabel);
		   hGroup2.add(labelsPg2);

		   ParallelGroup fieldsPg2=layout2.createParallelGroup();
		   fieldsPg2.add(ranksFileUsedComboBox);
		   fieldsPg2.add(numPreviewsSpinner);
		   fieldsPg2.add(allowHoldCheckBox);
		   fieldsPg2.add(speedLimitField);
		   hGroup2.add(fieldsPg2);

		   layout2.setHorizontalGroup(hGroup2);


		   GroupLayout.SequentialGroup vGroup2 = layout2.createSequentialGroup();

		   vGroup2.add(layout2.createParallelGroup(GroupLayout.BASELINE).
		            add(ranksFileUsedLabel).add(ranksFileUsedComboBox));
		   vGroup2.add(layout2.createParallelGroup(GroupLayout.BASELINE).
		            add(numPreviewsLabel).add(numPreviewsSpinner));
		   vGroup2.add(layout2.createParallelGroup(GroupLayout.BASELINE).
		            add(allowHoldLabel).add(allowHoldCheckBox));
		   vGroup2.add(layout2.createParallelGroup(GroupLayout.BASELINE).
		            add(speedLimitLabel).add(speedLimitField));
		   layout2.setVerticalGroup(vGroup2);


		JPanel buttonsPane2 = new JPanel();
		buttonsPane2.add(saveAIConfigButton);

		JPanel pane2 = new JPanel(new BorderLayout());

		pane2.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		pane2.add(formPane2,BorderLayout.CENTER);
		pane2.add(buttonsPane2, BorderLayout.SOUTH);

		//Tab 3


		tabbedPane=new JTabbedPane();
		tabbedPane.addTab(getUIText("Main_Generation_Tab_Title"), pane1);
		tabbedPane.addTab(getUIText("Main_AI_Config_Tab_Title"),pane2);
		add(tabbedPane);
	}



	public void actionPerformed(ActionEvent e) {
		String inputFile=(String) inputFileComboBox.getSelectedItem();
		if (inputFile.equals(newFileText)){
			inputFile="";
		}
		if ("go".equals(e.getActionCommand())) {
			String outputFile=outputFileField.getText();
			goButton.setEnabled(false);

			RanksIterator ranksIterator=new RanksIterator(this, inputFile,
					outputFile,
					(Integer) numIterationsSpinner.getValue());

			ranksIterator.addWindowListener(new WindowAdapter(){


				@Override
				public void windowClosed(WindowEvent e) {
					boolean isInCombo=false;
					int index=0;
					for (int i=1;i<inputFileComboBox.getItemCount();i++){
						if (outputFileField.getText().equals(inputFileComboBox.getItemAt(i))){
							isInCombo=true;
							index=i;
							break;
						}
					}
					if (!isInCombo){
						inputFileComboBox.addItem(outputFileField.getText());
						ranksFileUsedComboBox.addItem(outputFileField.getText());
						ranksFileUsedComboBox.setSelectedIndex(ranksFileUsedComboBox.getItemCount()-1);
					}
					else{
						ranksFileUsedComboBox.setSelectedIndex(index);
					}

					setDefaults();
					goButton.setEnabled(true);

				}

			});


		} else {
			if ("bests".equals(e.getActionCommand()) || "worsts".equals(e.getActionCommand())){
				setEnabledBWButtons(false);
				Ranks ranks = null;

				FileInputStream fis = null;
				ObjectInputStream in = null;

				if (inputFile.trim().length() == 0)
					ranks = new Ranks(4, 9);
				else {
					try {
						fis = new FileInputStream(RANKSAI_DIR+inputFile);
						in = new ObjectInputStream(fis);
						ranks = (Ranks) in.readObject();
						in.close();
					} catch (FileNotFoundException e1) {
						ranks = new Ranks(4, 9);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();

					} catch (ClassNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				}

				JDialog results=new RanksResult(this, ranks, 100, "worsts".equals(e.getActionCommand()));
				results.addWindowListener(new WindowAdapter(){


					@Override
					public void windowClosed(WindowEvent e) {

						setEnabledBWButtons(true);

					}

				});

			}
			else {
				if ("default".equals(e.getActionCommand())){
					setDefaults();
				}
				else {
					if ("input".equals(e.getActionCommand())){
						if (inputFileComboBox.getSelectedIndex()>0){
							outputFileField.setText((String) inputFileComboBox.getSelectedItem());
						}
						else {
							outputFileField.setText(DEFAULT_RANKS_FILE);
						}
					}
				}
			}
		}

	}
	public void setDefaults(){
		CustomProperties ranksAIConfig=new CustomProperties();
		ranksAIConfig.setProperty("ranksai.file",(String) ranksFileUsedComboBox.getSelectedItem());
		ranksAIConfig.setProperty("ranksai.numpreviews",(Integer) numPreviewsSpinner.getValue());
		ranksAIConfig.setProperty("ranksai.allowhold", allowHoldCheckBox.isSelected());
		ranksAIConfig.setProperty("ranksai.speedlimit", (Integer)speedLimitField.getValue());
		try {
			FileOutputStream out = new FileOutputStream(RANKSAI_CONFIG_FILE);
			ranksAIConfig.store(out, "Ranks AI Config");
		} catch (IOException exc) {
			log.error("Failed to save RanksAI config file", exc);

		}
	}
	public void setEnabledBWButtons(boolean b){
		viewBestsButton.setEnabled(b);
		viewWorstsButton.setEnabled(b);

	}

	public static String getUIText(String str) {
		String result = propLang.getProperty(str);
		if(result == null) {
			result = propLangDefault.getProperty(str, str);
		}
		return result;
	}

	public static void main(String[] args) {
		// Load language files
		propLangDefault = new CustomProperties();
		try {
			FileInputStream in = new FileInputStream("config/lang/airankstool_default.properties");
			propLangDefault.load(in);
			in.close();
		} catch (IOException e) {
			System.err.println("Couldn't load default UI language file");
			e.printStackTrace();
		}

		propLang = new CustomProperties();
		try {
			FileInputStream in = new FileInputStream("config/lang/airankstool_" + Locale.getDefault().getCountry() + ".properties");
			propLang.load(in);
			in.close();
		} catch(IOException e) {}

		// Start
		new AIRanksTool();
	}
}
