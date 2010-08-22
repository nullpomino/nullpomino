/*
    Copyright (c) 2010, NullNoname
    All rights reserved.

    Redistribution and use in source and binary forms, with or without
    modification, are permitted provided that the following conditions are met:

        * Redistributions of source code must retain the above copyright
          notice, this list of conditions and the following disclaimer.
        * Redistributions in binary form must reproduce the above copyright
          notice, this list of conditions and the following disclaimer in the
          documentation and/or other materials provided with the distribution.
        * Neither the name of NullNoname nor the names of its
          contributors may be used to endorse or promote products derived from
          this software without specific prior written permission.

    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
    AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
    IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
    ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
    LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
    CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
    SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
    INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
    CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
    ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
    POSSIBILITY OF SUCH DAMAGE.
*/
package mu.nu.nullpo.tool.sequencer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import mu.nu.nullpo.game.component.Piece;
import mu.nu.nullpo.util.CustomProperties;
import net.omegaboshi.nullpomino.game.subsystem.randomizer.Randomizer;

/**
 * NullpoMino Sequence Viewer (Original from NullpoUE build 010210 by Zircean)
 */
public class Sequencer extends JFrame implements ActionListener {
	/** Serial Version UID */
	private static final long serialVersionUID = 1L;

	/** Log */
	static final Logger log = Logger.getLogger(Sequencer.class);

	/** Config File */
	public CustomProperties propConfig;

	/** Default language file */
	public CustomProperties propLangDefault;

	/** UI Language File */
	public CustomProperties propLang;

	//----------------------------------------------------------------------
	/** Rand-seed textfield */
	private JTextField txtfldSeed;

	/** Sequence Length textfield */
	private JTextField txtfldSeqLength;

	/** Sequence Offset textfield */
	private JTextField txtfldSeqOffset;

	/** Randomizer combobox */
	private JComboBox comboboxRandomizer;

	/** Randomizer list */
	private Vector<String> vectorRandomizer;

	/** Generate button */
	private JButton btnGenerate;

	/** Generated Sequence textarea */
	private JTextArea txtareaSequence;

	//----------------------------------------------------------------------
	/** Generated Sequence */
	private int[] sequence;

	/** Enabled Pieces */
	private boolean[] nextPieceEnable;

	/**
	 * Constructor
	 */
	public Sequencer() {
		init();

		setVisible(true);
	}

	/**
	 * Initialize
	 */
	private void init() {
		// Load config file
		propConfig = new CustomProperties();
		try {
			FileInputStream in = new FileInputStream("config/setting/swing.cfg");
			propConfig.load(in);
			in.close();
		} catch(IOException e) {}

		// Load UI Language file
		propLangDefault = new CustomProperties();
		try {
			FileInputStream in = new FileInputStream("config/lang/sequencer_default.properties");
			propLangDefault.load(in);
			in.close();
		} catch (IOException e) {
			log.error("Couldn't load default UI language file", e);
		}

		propLang = new CustomProperties();
		try {
			FileInputStream in = new FileInputStream("config/lang/sequencer_" + Locale.getDefault().getCountry() + ".properties");
			propLang.load(in);
			in.close();
		} catch(IOException e) {}

		// Set Look&Feel
		if(propConfig.getProperty("option.usenativelookandfeel", true) == true) {
			try {
				UIManager.getInstalledLookAndFeels();
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch(Exception e) {
				log.warn("Failed to set native look&feel", e);
			}
		}

		// Initialize enabled pieces
		nextPieceEnable = new boolean[Piece.PIECE_COUNT];
		for(int i = 0; i < Piece.PIECE_STANDARD_COUNT; i++) nextPieceEnable[i] = true;

		setTitle(getUIText("Title_Sequencer"));
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		initUI();
		pack();
	}

	/**
	 * Init GUI
	 */
	private void initUI() {
		getContentPane().setLayout(new BorderLayout());

		// Menubar --------------------------------------------------
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		// File menu
		JMenu mFile = new JMenu(getUIText("JMenu_File"));
		mFile.setMnemonic('F');
		menuBar.add(mFile);

		// New
		JMenuItem miNew = new JMenuItem(getUIText("JMenuItem_New"));
		miNew.setMnemonic('N');
		miNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
		miNew.setActionCommand("New");
		miNew.addActionListener(this);
		//mFile.add(miNew);

		// Open
		JMenuItem miOpen = new JMenuItem(getUIText("JMenuItem_Open"));
		miOpen.setMnemonic('O');
		miOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
		miOpen.setActionCommand("Open");
		miOpen.addActionListener(this);
		mFile.add(miOpen);

		// Save
		JMenuItem miSave = new JMenuItem(getUIText("JMenuItem_Save"));
		miSave.setMnemonic('S');
		miSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
		miSave.setActionCommand("Save");
		miSave.addActionListener(this);
		mFile.add(miSave);

		// Reset
		JMenuItem miReset = new JMenuItem(getUIText("JMenuItem_Reset"));
		miReset.setMnemonic('R');
		miReset.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));
		miReset.setActionCommand("Reset");
		miReset.addActionListener(this);
		mFile.add(miReset);

		// Exit
		JMenuItem miExit = new JMenuItem(getUIText("JMenuItem_Exit"));
		miExit.setMnemonic('X');
		miExit.setActionCommand("Exit");
		miExit.addActionListener(this);
		mFile.add(miExit);

		// Options menu
		JMenu mOptions = new JMenu(getUIText("JMenu_Options"));
		mOptions.setMnemonic('P');
		menuBar.add(mOptions);

		// Set piece enable
		JMenuItem miSetPieceEnable = new JMenuItem(getUIText("JMenuItem_SetPieceEnable"));
		miSetPieceEnable.setMnemonic('E');
		miSetPieceEnable.setActionCommand("Set piece enable");
		miSetPieceEnable.addActionListener(this);
		mOptions.add(miSetPieceEnable);

		// Set up content pane ------------------------------
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

		// Seed
		JPanel pSeed = new JPanel();
		getContentPane().add(pSeed);

		JLabel lSeed = new JLabel(getUIText("Option_Seed"));
		pSeed.add(lSeed);

		txtfldSeed = new JTextField("0", 15);
		pSeed.add(txtfldSeed);

		// Sequence Length
		JPanel pSeqLength = new JPanel();
		getContentPane().add(pSeqLength);

		JLabel lSeqLength = new JLabel(getUIText("Option_SequenceLength"));
		pSeqLength.add(lSeqLength);

		txtfldSeqLength = new JTextField("100", 6);
		pSeqLength.add(txtfldSeqLength);

		// Sequence Offset
		JPanel pSeqOffset = new JPanel();
		getContentPane().add(pSeqOffset);

		JLabel lSeqOffset = new JLabel(getUIText("Option_SequenceOffset"));
		pSeqOffset.add(lSeqOffset);

		txtfldSeqOffset = new JTextField("0", 6);
		pSeqOffset.add(txtfldSeqOffset);

		// Randomizer
		JPanel pRandomizer = new JPanel();
		getContentPane().add(pRandomizer);

		JLabel lRandomizer = new JLabel(getUIText("Option_Randomizer"));
		pRandomizer.add(lRandomizer);

		vectorRandomizer = getTextFileVector("config/list/randomizer.lst");
		comboboxRandomizer = new JComboBox(createShortStringVector(vectorRandomizer));
		comboboxRandomizer.setPreferredSize(new Dimension(200, 30));
		comboboxRandomizer.setSelectedIndex(0);
		pRandomizer.add(comboboxRandomizer);

		// Generate
		JPanel pGenerate = new JPanel();
		getContentPane().add(pGenerate);

		btnGenerate = new JButton(getUIText("Option_Generate"));
		btnGenerate.setMnemonic('G');
		btnGenerate.setActionCommand("Generate");
		btnGenerate.addActionListener(this);
		pGenerate.add(btnGenerate);

		// Sequence
		txtareaSequence = new JTextArea(10,37);
		txtareaSequence.setLineWrap(true);
		txtareaSequence.setEditable(false);

		JScrollPane pSequence = new JScrollPane(txtareaSequence,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		getContentPane().add(pSequence);

	}

	public Vector<String> getTextFileVector(String filename) {
		Vector<String> vec = new Vector<String>();

		try {
			BufferedReader in = new BufferedReader(new FileReader(filename));

			while(true) {
				String str = in.readLine();
				if((str == null) || (str.length() <= 0)) break;
				vec.add(str);
			}
		} catch (IOException e) {}

		return vec;
	}

	public Vector<String> createShortStringVector(Vector<String> vecSrc) {
		Vector<String> vec = new Vector<String>();

		for(int i = 0; i < vecSrc.size(); i++) {
			vec.add(createShortString(vecSrc.get(i)));
		}

		return vec;
	}

	public String createShortString(String str) {
		int last = str.lastIndexOf('.');

		String newStr = "";
		if(last != -1) {
			newStr = str.substring(last + 1);
		} else {
			newStr = str;
		}
		return newStr;
	}

	public void readReplayToUI(CustomProperties prop, int playerID) {
		txtfldSeed.setText(String.valueOf(Long.parseLong(prop.getProperty(
			playerID+".replay.randSeed", "0"),16)));
		comboboxRandomizer.setSelectedItem(createShortString(prop.getProperty(
			playerID+".ruleopt.strRandomizer",null)));
	}

	public CustomProperties load(String filename) throws IOException {
		log.info("Loading replay file from " + filename);
		CustomProperties prop = new CustomProperties();

		FileInputStream in = new FileInputStream(filename);
		prop.load(in);
		in.close();

		return prop;
	}

	public void save(String filename) throws IOException {
		log.info("Saving piece sequence file to " + filename);
		BufferedWriter out = new BufferedWriter(new FileWriter(filename));
		out.write("# NullpoMino Piece Sequence"); out.newLine();
		out.write(txtareaSequence.getText());
		out.close();
	}

	/**
	 * Get translated text from UI Language file
	 * @param str Text
	 * @return Translated text (If translated text is NOT available, it will return str itself)
	 */
	public String getUIText(String str) {
		String result = propLang.getProperty(str);
		if(result == null) {
			result = propLangDefault.getProperty(str, str);
		}
		return result;
	}

	/**
	 * Get int value from a JTextField
	 * @param txtfld JTextField
	 * @return An int value from JTextField (If fails, it will return zero)
	 */
	public int getIntTextField(JTextField txtfld) {
		int v = 0;

		try {
			v = Integer.parseInt(txtfld.getText());
		} catch(Exception e) {}

		return v;
	}

	/**
	 * Get long value from a JTextField
	 * @param txtfld JTextField
	 * @return A long value from JTextField (If fails, it will return zero)
	 */
	public long getLongTextField(JTextField txtfld) {
		long v = 0L;

		try {
			v = Long.parseLong(txtfld.getText());
		} catch(Exception e) {}

		return v;
	}

	public void generate() {
		Class<?> randomizerClass;
		Randomizer randomizerObject;

		String name = vectorRandomizer.get(comboboxRandomizer.getSelectedIndex());

		try {
			randomizerClass = Class.forName(name);
			randomizerObject = (Randomizer) randomizerClass.newInstance();
			randomizerObject.setState(nextPieceEnable, getLongTextField(txtfldSeed));
			sequence = new int[getIntTextField(txtfldSeqLength)];
			for (int i = 0; i < getIntTextField(txtfldSeqOffset); i++) {
				randomizerObject.next();
			}
			for (int i = 0; i < sequence.length; i++) {
				sequence[i] = randomizerObject.next();
			}
		} catch(Exception e) {
			log.error("Randomizer class " + name + " load failed", e);
		}
	}

	public void display() {
		if (!txtareaSequence.getText().equals("")) txtareaSequence.setText("");
		for (int i = 1; i <= sequence.length; i++) {
			txtareaSequence.append(getUIText("PieceName"+sequence[i-1]));
			if (i % 5 == 0) txtareaSequence.append(" ");
			if (i % 60 == 0) txtareaSequence.append("\n");
		}
	}

	public void reset() {
		txtfldSeed.setText("0");
		txtfldSeqLength.setText("100");
		txtfldSeqOffset.setText("0");
		comboboxRandomizer.setSelectedIndex(0);
		txtareaSequence.setText("");
		sequence = null;
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand() == "New") {
			// New
		} else if(e.getActionCommand() == "Open") {
			// Open
			JFileChooser c = new JFileChooser(System.getProperty("user.dir") + "/replay");
			c.setFileFilter(new FileFilterREP());

			if(c.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				File file = c.getSelectedFile();
				CustomProperties prop = new CustomProperties();

				try {
					prop = load(file.getPath());
				} catch (IOException e2) {
					log.error("Failed to load replay data", e2);
					JOptionPane.showMessageDialog(this, getUIText("Message_FileLoadFailed")+"\n"+e2, getUIText("Title_FileLoadFailed"),
												  JOptionPane.ERROR_MESSAGE);
					return;
				}

				readReplayToUI(prop,0);
			}
		} else if((e.getActionCommand() == "Save")) {
			// Save
			generate();
			display();
			JFileChooser c = new JFileChooser(System.getProperty("user.dir"));
			c.setFileFilter(new FileFilterTXT());

			if(c.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				File file = c.getSelectedFile();
				String filename = file.getPath();
				if(!filename.endsWith(".txt")) filename = filename + ".txt";

				try {
					save(filename);
				} catch (Exception e2) {
					log.error("Failed to save sequence data", e2);
					JOptionPane.showMessageDialog(this, getUIText("Message_FileSaveFailed")+"\n"+e2, getUIText("Title_FileSaveFailed"),
												  JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
		} else if(e.getActionCommand() == "Reset") {
			// Reset
			reset();
		} else if(e.getActionCommand() == "Set piece enable") {
			// Set piece enable
			setPieceEnable();
		} else if(e.getActionCommand() == "Generate") {
			// Generate
			generate();
			display();
		} else if(e.getActionCommand() == "Exit") {
			// Exit
			dispose();
		}
	}

	public void setPieceEnable() {
		final JFrame setPieceEnableFrame = new JFrame(getUIText("Title_SetPieceEnable"));
		setPieceEnableFrame.getContentPane().setLayout(new GridLayout(0,2,10,10));
		final JCheckBox[] chkboxEnable = new JCheckBox[Piece.PIECE_COUNT];
		for (int i = 0; i < Piece.PIECE_COUNT; i++) {
			chkboxEnable[i] = new JCheckBox("Piece "+getUIText("PieceName"+i));
			chkboxEnable[i].setSelected(nextPieceEnable[i]);
			setPieceEnableFrame.getContentPane().add(chkboxEnable[i]);
		}
		if (Piece.PIECE_COUNT % 2 == 0) setPieceEnableFrame.getContentPane().add(new JLabel(""));
		final JButton btnConfirm = new JButton(getUIText("Button_Confirm"));
		btnConfirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < Piece.PIECE_COUNT; i++) {
					nextPieceEnable[i] = chkboxEnable[i].isSelected();
				}
				setPieceEnableFrame.dispose();
			}
		});
		setPieceEnableFrame.getContentPane().add(btnConfirm);
		setPieceEnableFrame.pack();
		setPieceEnableFrame.setVisible(true);
	}

	public static void main(String[] args) {
		PropertyConfigurator.configure("config/etc/log.cfg");
		log.debug("Sequencer start");
		new Sequencer();
	}

	protected class FileFilterREP extends FileFilter {
		@Override
		public boolean accept(File f) {
			if(f.isDirectory()) return true;
			if(f.getName().endsWith(".rep")) return true;
			return false;
		}

		@Override
		public String getDescription() {
			return getUIText("FileChooser_ReplayFile");
		}
	}

	protected class FileFilterTXT extends FileFilter {
		@Override
		public boolean accept(File f) {
			if(f.isDirectory()) return true;
			if(f.getName().endsWith(".txt")) return true;
			return false;
		}

		@Override
		public String getDescription() {
			return getUIText("FileChooser_TextFile");
		}
	}
}
