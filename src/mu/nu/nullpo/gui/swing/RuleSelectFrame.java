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

	/** 親ウィンドウ */
	protected NullpoMinoSwing owner;

	/** Player number */
	protected int playerID;

	/** Filename */
	protected String[] strFileNameList;

	/** ファイルパス一覧 */
	protected String[] strFilePathList;

	/** Rule name一覧 */
	protected String[] strRuleNameList;

	/** Current ルールファイル */
	protected String[] strCurrentFileName;

	/** Current Rule name */
	protected String[] strCurrentRuleName;

	/** Rule select listbox */
	protected JList[] listboxRule;

	/** Tab */
	protected JTabbedPane tabPane;

	/**
	 * Constructor
	 * @param owner 親ウィンドウ
	 * @throws HeadlessException キーボード, マウス, ディスプレイなどが存在しない場合の例外
	 */
	public RuleSelectFrame(NullpoMinoSwing owner) throws HeadlessException {
		super();
		this.owner = owner;

		// ルール一覧取得
		strFileNameList = getRuleFileList();
		if(strFileNameList == null) {
			log.error("Rule file directory not found");
		}

		updateDetails();

		// GUIのInitialization
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		initUI();
		pack();
	}

	/**
	 * 現在選択しているルールを読み込み
	 * @param pl Player number
	 */
	public void load(int pl) {
		this.playerID = pl;

		setTitle(NullpoMinoSwing.getUIText("Title_RuleSelect") + " (" + (playerID+1) + "P)");

		strCurrentFileName = new String[listboxRule.length];
		strCurrentRuleName = new String[listboxRule.length];

		for(int i = 0; i < listboxRule.length; i++) {
			if(i == 0) {
				strCurrentFileName[i] = NullpoMinoSwing.propGlobal.getProperty(playerID + ".rulefile", "");
				strCurrentRuleName[i] = NullpoMinoSwing.propGlobal.getProperty(playerID + ".rulename", "");
			} else {
				strCurrentFileName[i] = NullpoMinoSwing.propGlobal.getProperty(playerID + ".rulefile." + i, "");
				strCurrentRuleName[i] = NullpoMinoSwing.propGlobal.getProperty(playerID + ".rulename." + i, "");
			}

			for(int j = 0; j < strFileNameList.length; j++) {
				if(strCurrentFileName[i].equals(strFileNameList[j])) {
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
		String[] strList = new String[strFileNameList.length];
		for(int i = 0; i < strFileNameList.length; i++) {
			strList[i] = strRuleNameList[i] + " (" + strFileNameList[i] + ")";
		}

		listboxRule = new JList[GameEngine.MAX_GAMESTYLE];
		for(int i = 0; i < GameEngine.MAX_GAMESTYLE; i++) {
			listboxRule[i] = new JList(strList);
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
	 * ルールファイル一覧を取得
	 * @return ルールファイルのFilenameの配列。ディレクトリがないならnull
	 */
	protected String[] getRuleFileList() {
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
	 * 詳細情報を更新
	 */
	protected void updateDetails() {
		strFilePathList = new String[strFileNameList.length];
		strRuleNameList = new String[strFileNameList.length];

		for(int i = 0; i < strFileNameList.length; i++) {
			File file = new File("config/rule/" + strFileNameList[i]);
			strFilePathList[i] = file.getPath();

			CustomProperties prop = new CustomProperties();

			try {
				FileInputStream in = new FileInputStream("config/rule/" + strFileNameList[i]);
				prop.load(in);
				in.close();
				strRuleNameList[i] = prop.getProperty("0.ruleopt.strRuleName", "");
			} catch (Exception e) {
				strRuleNameList[i] = "";
			}
		}
	}

	/*
	 * Menu 実行時の処理
	 */
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand() == "RuleSelect_OK") {
			for(int i = 0; i < listboxRule.length; i++) {
				int id = listboxRule[i].getSelectedIndex();

				if(i == 0) {
					if(id >= 0) {
						NullpoMinoSwing.propGlobal.setProperty(playerID + ".rule", strFilePathList[id]);
						NullpoMinoSwing.propGlobal.setProperty(playerID + ".rulefile", strFileNameList[id]);
						NullpoMinoSwing.propGlobal.setProperty(playerID + ".rulename", strRuleNameList[id]);
					} else {
						NullpoMinoSwing.propGlobal.setProperty(playerID + ".rule", "");
						NullpoMinoSwing.propGlobal.setProperty(playerID + ".rulefile", "");
						NullpoMinoSwing.propGlobal.setProperty(playerID + ".rulename", "");
					}
				} else {
					if(id >= 0) {
						NullpoMinoSwing.propGlobal.setProperty(playerID + ".rule." + i, strFilePathList[id]);
						NullpoMinoSwing.propGlobal.setProperty(playerID + ".rulefile." + i, strFileNameList[id]);
						NullpoMinoSwing.propGlobal.setProperty(playerID + ".rulename." + i, strRuleNameList[id]);
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
}
