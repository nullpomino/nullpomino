package mu.nu.nullpo.tool.airanksgenerator;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import mu.nu.nullpo.util.CustomProperties;

public class AIRanksGenerator extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;

	/** Default language file */
	public static CustomProperties propLangDefault;
	/** Primary language file */
	public static CustomProperties propLang;

	private JLabel inputFileLabel;
	private JFormattedTextField inputFileField;
	private JLabel outputFileLabel;
	private JFormattedTextField outputFileField;
	private JLabel numIterationsLabel;

	private SpinnerNumberModel spinModel;
	private JSpinner numIterationsSpinner;
	private JButton goButton;
	private JButton viewBestsButton;
	private JButton viewWorstsButton;

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
		inputFileField = new JFormattedTextField();
		inputFileField.setColumns(20);
		inputFileField.setToolTipText(getUIText("Main_Input_Tip"));

		outputFileLabel = new JLabel(getUIText("Main_Output_Label"));
		outputFileField = new JFormattedTextField("ranks.bin");
		outputFileField.setColumns(20);
		outputFileField.setToolTipText(getUIText("Main_Output_Tip"));

		numIterationsLabel = new JLabel(getUIText("Main_Iterations_Label"));
		spinModel = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1);
		numIterationsSpinner = new JSpinner(spinModel);
		numIterationsSpinner.setToolTipText(getUIText("Main_Iterations_Tip"));

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

		JPanel labelPane = new JPanel(new GridLayout(0, 1));

		labelPane.add(inputFileLabel);
		labelPane.add(outputFileLabel);
		labelPane.add(numIterationsLabel);
		
		JPanel fieldPane = new JPanel(new GridLayout(0, 1));

		fieldPane.add(inputFileField);
		fieldPane.add(outputFileField);
		fieldPane.add(numIterationsSpinner);
		
		JPanel pane = new JPanel(new BorderLayout());
		
		pane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		JPanel buttonsPane = new JPanel();
		buttonsPane.add(goButton);
		buttonsPane.add(viewBestsButton);
		buttonsPane.add(viewWorstsButton);
		pane.add(labelPane, BorderLayout.WEST);
		pane.add(fieldPane, BorderLayout.EAST);
		pane.add(buttonsPane, BorderLayout.SOUTH);
		add(pane);
	}
	
	

	public void actionPerformed(ActionEvent e) {
		if ("go".equals(e.getActionCommand())) {
			goButton.setEnabled(false);
			new RanksIterator(this, inputFileField.getText(),
					outputFileField.getText(),
					(Integer) numIterationsSpinner.getValue());
			goButton.setEnabled(true);

			
		} else {
			setEnabledBWButtons(false);
			Ranks ranks = null;

			FileInputStream fis = null;
			ObjectInputStream in = null;

			if (inputFileField.getText().trim().length() == 0)
				ranks = new Ranks(4, 9);
			else {
				try {
					fis = new FileInputStream(inputFileField.getText());
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
