package mu.nu.nullpo.tool.airanksgenerator;

import java.awt.BorderLayout;
import java.awt.GridLayout;
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
import javax.swing.SpinnerNumberModel;

import org.apache.log4j.Logger;

import mu.nu.nullpo.util.CustomProperties;

public class AIRanksGenerator extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	/** Log */
	static final Logger log = Logger.getLogger(AIRanksGenerator.class);

	/** Default language file */
	public static CustomProperties propLangDefault;
	/** Primary language file */
	public static CustomProperties propLang;

	private JLabel inputFileLabel;
	//private JFormattedTextField inputFileField;
	private JComboBox inputFileComboBox;
	private JLabel outputFileLabel;
	private JFormattedTextField outputFileField;
	private JLabel numIterationsLabel;
	private JLabel numPreviewsLabel;
	private JLabel allowHoldLabel;
	private SpinnerNumberModel spinModel;
	private JSpinner numIterationsSpinner;
	private SpinnerNumberModel numPreviewsSpinModel;
	private JSpinner numPreviewsSpinner;
	private JCheckBox allowHoldCheckBox;
	private JButton goButton;
	private JButton viewBestsButton;
	private JButton viewWorstsButton;
	private JButton setDefaultButton;
	public static String RANKSAI_DIR="res/ranksAI/";
	public static String RANKSAI_CONFIG_FILE="config/setting/ranksai.cfg";
	private String newFileText;

	public AIRanksGenerator() {
		super();

		setTitle(getUIText("Main_Title"));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		initUI();
		pack();

		setVisible(true);
	}

	private void initUI() {

		inputFileLabel = new JLabel(getUIText("Main_Input_Label"));
		CustomProperties propRanksAI = new CustomProperties();
		try {
			FileInputStream in = new FileInputStream(AIRanksGenerator.RANKSAI_CONFIG_FILE);
			propRanksAI.load(in);
			in.close();
		} catch (IOException e) {}
		String file=propRanksAI.getProperty("ranksai.file");
		int numPreviews=propRanksAI.getProperty("ranksai.numpreviews", 2);
		boolean allowHold=propRanksAI.getProperty("ranksai.allowhold", false);
		String [] children=new File(RANKSAI_DIR).list();
		String []ranksList;
		int fileIndex=0;
		if (children != null){

			if (file!=null){
				fileIndex=-1;
				for (int i=0;i<children.length;i++){
					if (children[i].equals(file)){
						fileIndex=i;
					}
				}
				fileIndex++;
			}
			ranksList=new String[children.length+1];
			System.arraycopy(children, 0, ranksList, 1, children.length);
		}
		else {
			ranksList=new String[1];
		}
		newFileText=getUIText("Main_New_File_Text");
		ranksList[0]=newFileText;
		
		inputFileComboBox=new JComboBox(ranksList);
		inputFileComboBox.setSelectedIndex(fileIndex);
		//inputFileField = new JFormattedTextField();
		//inputFileField.setColumns(20);
		inputFileComboBox.setToolTipText(getUIText("Main_Input_Tip"));
		inputFileComboBox.setActionCommand("input");
		inputFileComboBox.addActionListener(this);

		outputFileLabel = new JLabel(getUIText("Main_Output_Label"));
		outputFileField = new JFormattedTextField("ranks.bin");
		outputFileField.setColumns(20);
		if (inputFileComboBox.getSelectedIndex()>0){
			outputFileField.setText((String) inputFileComboBox.getSelectedItem());
		}
		outputFileField.setToolTipText(getUIText("Main_Output_Tip"));

		numIterationsLabel = new JLabel(getUIText("Main_Iterations_Label"));
		spinModel = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1);
		numIterationsSpinner = new JSpinner(spinModel);
				
		numIterationsSpinner.setToolTipText(getUIText("Main_Iterations_Tip"));
		
		numPreviewsLabel = new JLabel(getUIText("Main_Num_Previews_Label"));
		numPreviewsSpinModel = new SpinnerNumberModel(2, 0, Integer.MAX_VALUE, 1);
		numPreviewsSpinner = new JSpinner(numPreviewsSpinModel);
		if (numPreviews>=0){
			numPreviewsSpinner.setValue((Integer) numPreviews);
		}

		numPreviewsSpinner.setToolTipText(getUIText("Main_Num_Previews_Tip"));
		
		allowHoldCheckBox= new JCheckBox();
		allowHoldCheckBox.setSelected(allowHold);
		allowHoldLabel=new JLabel(getUIText("Main_Allow_Hold"));
		allowHoldCheckBox.setToolTipText(getUIText("Main_Allow_Hold_Tip"));
		
		goButton = new JButton(getUIText("Main_Go_Label"));
		goButton.setActionCommand("go");
		goButton.addActionListener(this);
		goButton.setToolTipText(getUIText("Main_Go_Tip"));
		goButton.setMnemonic('G');

		viewBestsButton = new JButton(getUIText("Main_Bests_Label"));
		viewBestsButton.setActionCommand("bests");
		viewBestsButton.addActionListener(this);
		viewBestsButton.setToolTipText(getUIText("Main_Bests_Tip"));
		viewBestsButton.setMnemonic('B');

		viewWorstsButton = new JButton(getUIText("Main_Worsts_Label"));
		viewWorstsButton.setActionCommand("worsts");
		viewWorstsButton.addActionListener(this);
		viewWorstsButton.setToolTipText(getUIText("Main_Worsts_Tip"));
		viewWorstsButton.setMnemonic('W');
		
		setDefaultButton=new JButton(getUIText("Main_Set_Default_Label"));
		setDefaultButton.setActionCommand("default");
		setDefaultButton.addActionListener(this);
		setDefaultButton.setToolTipText(getUIText("Main_Set_Default_Tip"));
		setDefaultButton.setMnemonic('S');

		JPanel labelPane = new JPanel(new GridLayout(0, 1));

		labelPane.add(inputFileLabel);
		labelPane.add(outputFileLabel);
		labelPane.add(numIterationsLabel);
		labelPane.add(numPreviewsLabel);
		labelPane.add(allowHoldLabel);
		JPanel fieldPane = new JPanel(new GridLayout(0, 1));

		fieldPane.add(inputFileComboBox);
		fieldPane.add(outputFileField);
		fieldPane.add(numIterationsSpinner);
		fieldPane.add(numPreviewsSpinner);
		fieldPane.add(allowHoldCheckBox);
		
		JPanel pane = new JPanel(new BorderLayout());
		
		pane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		JPanel buttonsPane = new JPanel();
		buttonsPane.add(goButton);
		buttonsPane.add(viewBestsButton);
		buttonsPane.add(viewWorstsButton);
		buttonsPane.add(setDefaultButton);
		pane.add(labelPane, BorderLayout.WEST);
		pane.add(fieldPane, BorderLayout.EAST);
		pane.add(buttonsPane, BorderLayout.SOUTH);
		add(pane);
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
					inputFileComboBox.setSelectedIndex(inputFileComboBox.getItemCount()-1);
					}
					else{
						inputFileComboBox.setSelectedIndex(index);
					}
					
					setDefaults(outputFileField.getText());
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
						fis = new FileInputStream(inputFile);
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
					setDefaults(inputFile);
				}
				else {
					if ("input".equals(e.getActionCommand())){
						if (inputFileComboBox.getSelectedIndex()>0){
							outputFileField.setText((String) inputFileComboBox.getSelectedItem());
						}
						else {
							outputFileField.setText("ranks.bin");
						}
					}
				}
			}
		}
		
	}
	public void setDefaults(String file){
		CustomProperties ranksAIConfig=new CustomProperties();
		ranksAIConfig.setProperty("ranksai.file",file );
		ranksAIConfig.setProperty("ranksai.numpreviews",(Integer) numPreviewsSpinner.getValue());
		ranksAIConfig.setProperty("ranksai.allowhold", allowHoldCheckBox.isSelected());
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
			FileInputStream in = new FileInputStream("config/lang/airanksgenerator_default.properties");
			propLangDefault.load(in);
			in.close();
		} catch (IOException e) {
			System.err.println("Couldn't load default UI language file");
			e.printStackTrace();
		}

		propLang = new CustomProperties();
		try {
			FileInputStream in = new FileInputStream("config/lang/airanksgenerator_" + Locale.getDefault().getCountry() + ".properties");
			propLang.load(in);
			in.close();
		} catch(IOException e) {}

		// Start
		new AIRanksGenerator();
	}
}
