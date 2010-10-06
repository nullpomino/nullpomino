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
package mu.nu.nullpo.gui.swing;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.LinkedList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.util.CustomProperties;

import org.apache.log4j.Logger;

/**
 * ルール選択画面の frame
 */
public class RuleSelectFrame extends JFrame implements ActionListener {
	/** Serial version ID */
	private static final long serialVersionUID = 1L;

	/** Log */
	static Logger log = Logger.getLogger(RuleSelectFrame.class);

	/** Owner window */
	protected NullpoMinoSwing owner;

	/** Player number */
	protected int playerID;

	/** Filename */
	private String[] strFileNameList;

	/** Current ルールファイル */
	private String[] strCurrentFileName;

	/** Current Rule name */
	private String[] strCurrentRuleName;

	/** Rule entries */
	private LinkedList<RuleEntry> ruleEntries;

	/** Rule select listbox */
	private JList[] listboxRule;

	/** Tab */
	private JTabbedPane tabPane;

	/**
	 * Constructor
	 * @param owner Owner window
	 * @throws HeadlessException If GUI cannot be used
	 */
	public RuleSelectFrame(NullpoMinoSwing owner) throws HeadlessException {
		super();
		this.owner = owner;

		// Get rule list
		strFileNameList = getRuleFileList();
		if(strFileNameList == null) {
			log.error("Rule file directory not found");
		} else {
			createRuleEntries(strFileNameList);
		}

		// GUI Initialization
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		initUI();
		pack();
	}

	/**
	 * Setup rule selector
	 * @param pl Player number
	 */
	public void load(int pl) {
		this.playerID = pl;

		setTitle(NullpoMinoSwing.getUIText("Title_RuleSelect") + " (" + (playerID+1) + "P)");

		strCurrentFileName = new String[GameEngine.MAX_GAMESTYLE];
		strCurrentRuleName = new String[GameEngine.MAX_GAMESTYLE];

		for(int i = 0; i < GameEngine.MAX_GAMESTYLE; i++) {
			if(i == 0) {
				strCurrentFileName[i] = NullpoMinoSwing.propGlobal.getProperty(playerID + ".rulefile", "");
				strCurrentRuleName[i] = NullpoMinoSwing.propGlobal.getProperty(playerID + ".rulename", "");
			} else {
				strCurrentFileName[i] = NullpoMinoSwing.propGlobal.getProperty(playerID + ".rulefile." + i, "");
				strCurrentRuleName[i] = NullpoMinoSwing.propGlobal.getProperty(playerID + ".rulename." + i, "");
			}

			LinkedList<RuleEntry> subEntries = getSubsetEntries(i);

			for(int j = 0; j < subEntries.size(); j++) {
				if(subEntries.get(j).filename.equals(strCurrentFileName[i])) {
					listboxRule[i].setSelectedIndex(j);
				}
			}
		}

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				listboxRule[0].requestFocusInWindow();
			}
		});
	}

	/**
	 * GUIをInitialization
	 */
	protected void initUI() {
		this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

		// Tab
		tabPane = new JTabbedPane();
		tabPane.setAlignmentX(LEFT_ALIGNMENT);
		this.add(tabPane);

		// Rules
		listboxRule = new JList[GameEngine.MAX_GAMESTYLE];
		for(int i = 0; i < GameEngine.MAX_GAMESTYLE; i++) {
			listboxRule[i] = new JList(extractRuleListFromRuleEntries(i));
			JScrollPane scpaneRule = new JScrollPane(listboxRule[i]);
			scpaneRule.setPreferredSize(new Dimension(380, 250));
			scpaneRule.setAlignmentX(LEFT_ALIGNMENT);
			tabPane.addTab(GameEngine.GAMESTYLE_NAMES[i], scpaneRule);
		}

		//  default に戻す button
		JButton btnUseDefault = new JButton(NullpoMinoSwing.getUIText("RuleSelect_UseDefault"));
		btnUseDefault.setMnemonic('D');
		btnUseDefault.addActionListener(this);
		btnUseDefault.setActionCommand("RuleSelect_UseDefault");
		btnUseDefault.setAlignmentX(LEFT_ALIGNMENT);
		btnUseDefault.setMaximumSize(new Dimension(Short.MAX_VALUE, 30));
		btnUseDefault.setVisible(false);
		this.add(btnUseDefault);

		//  button類
		JPanel pButtons = new JPanel();
		pButtons.setLayout(new BoxLayout(pButtons, BoxLayout.X_AXIS));
		pButtons.setAlignmentX(LEFT_ALIGNMENT);
		this.add(pButtons);

		JButton btnOK = new JButton(NullpoMinoSwing.getUIText("RuleSelect_OK"));
		btnOK.setMnemonic('O');
		btnOK.addActionListener(this);
		btnOK.setActionCommand("RuleSelect_OK");
		btnOK.setAlignmentX(LEFT_ALIGNMENT);
		btnOK.setMaximumSize(new Dimension(Short.MAX_VALUE, 30));
		pButtons.add(btnOK);
		this.getRootPane().setDefaultButton(btnOK);

		JButton btnCancel = new JButton(NullpoMinoSwing.getUIText("RuleSelect_Cancel"));
		btnCancel.setMnemonic('C');
		btnCancel.addActionListener(this);
		btnCancel.setActionCommand("RuleSelect_Cancel");
		btnCancel.setAlignmentX(LEFT_ALIGNMENT);
		btnCancel.setMaximumSize(new Dimension(Short.MAX_VALUE, 30));
		pButtons.add(btnCancel);
	}

	/**
	 * Get rule file list
	 * @return Rule file list. null if directory doesn't exist.
	 */
	private String[] getRuleFileList() {
		File dir = new File("config/rule");

		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir1, String name) {
				return name.endsWith(".rul");
			}
		};

		String[] list = dir.list(filter);

		if(!System.getProperty("os.name").startsWith("Windows")) {
			// Sort if not windows
			Arrays.sort(list);
		}

		return list;
	}

	/**
	 * Create rule entries
	 * @param filelist Rule file list
	 */
	private void createRuleEntries(String[] filelist) {
		ruleEntries = new LinkedList<RuleEntry>();

		for(int i = 0; i < filelist.length; i++) {
			RuleEntry entry = new RuleEntry();

			File file = new File("config/rule/" + filelist[i]);
			entry.filename = filelist[i];
			entry.filepath = file.getPath();

			CustomProperties prop = new CustomProperties();
			try {
				FileInputStream in = new FileInputStream("config/rule/" + filelist[i]);
				prop.load(in);
				in.close();
				entry.rulename = prop.getProperty("0.ruleopt.strRuleName", "");
				entry.style = prop.getProperty("0.ruleopt.style", 0);
			} catch (Exception e) {
				entry.rulename = "";
				entry.style = -1;
			}

			ruleEntries.add(entry);
		}
	}

	/**
	 * Get subset of rule entries
	 * @param currentStyle Current style
	 * @return Subset of rule entries
	 */
	private LinkedList<RuleEntry> getSubsetEntries(int currentStyle) {
		LinkedList<RuleEntry> subEntries = new LinkedList<RuleEntry>();
		for(int i = 0; i < ruleEntries.size(); i++) {
			if(ruleEntries.get(i).style == currentStyle) {
				subEntries.add(ruleEntries.get(i));
			}
		}
		return subEntries;
	}

	/**
	 * Get rule name + file name list as String[]
	 * @param currentStyle Current style
	 * @return Rule name + file name list
	 */
	private String[] extractRuleListFromRuleEntries(int currentStyle) {
		LinkedList<RuleEntry> subEntries = getSubsetEntries(currentStyle);

		String[] result = new String[subEntries.size()];
		for(int i = 0; i < subEntries.size(); i++) {
			RuleEntry entry = subEntries.get(i);
			result[i] = entry.rulename + " (" + entry.filename + ")";
		}

		return result;
	}

	/*
	 * Menu 実行時の処理
	 */
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand() == "RuleSelect_OK") {
			for(int i = 0; i < GameEngine.MAX_GAMESTYLE; i++) {
				int id = listboxRule[i].getSelectedIndex();
				LinkedList<RuleEntry> subEntries = getSubsetEntries(i);
				RuleEntry entry = subEntries.get(id);

				if(i == 0) {
					if(id >= 0) {
						NullpoMinoSwing.propGlobal.setProperty(playerID + ".rule", entry.filepath);
						NullpoMinoSwing.propGlobal.setProperty(playerID + ".rulefile", entry.filename);
						NullpoMinoSwing.propGlobal.setProperty(playerID + ".rulename", entry.rulename);
					} else {
						NullpoMinoSwing.propGlobal.setProperty(playerID + ".rule", "");
						NullpoMinoSwing.propGlobal.setProperty(playerID + ".rulefile", "");
						NullpoMinoSwing.propGlobal.setProperty(playerID + ".rulename", "");
					}
				} else {
					if(id >= 0) {
						NullpoMinoSwing.propGlobal.setProperty(playerID + ".rule." + i, entry.filepath);
						NullpoMinoSwing.propGlobal.setProperty(playerID + ".rulefile." + i, entry.filename);
						NullpoMinoSwing.propGlobal.setProperty(playerID + ".rulename." + i, entry.rulename);
					} else {
						NullpoMinoSwing.propGlobal.setProperty(playerID + ".rule." + i, "");
						NullpoMinoSwing.propGlobal.setProperty(playerID + ".rulefile." + i, "");
						NullpoMinoSwing.propGlobal.setProperty(playerID + ".rulename." + i, "");
					}
				}
			}
			NullpoMinoSwing.saveConfig();
			this.setVisible(false);
		}
		else if(e.getActionCommand() == "RuleSelect_UseDefault") {
			int id = tabPane.getSelectedIndex();
			if((id >= 0) && (id < listboxRule.length)) {
				listboxRule[id].clearSelection();
			}
		}
		else if(e.getActionCommand() == "RuleSelect_Cancel") {
			this.setVisible(false);
		}
	}

	/**
	 * Rule entry
	 */
	private class RuleEntry {
		/** File name */
		public String filename;
		/** File path */
		public String filepath;
		/** Rule name */
		public String rulename;
		/** Game style */
		public int style;
	}
}
