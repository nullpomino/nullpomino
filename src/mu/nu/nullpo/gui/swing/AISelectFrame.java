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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import mu.nu.nullpo.game.subsystem.ai.AIPlayer;

import org.apache.log4j.Logger;

/**
 * AI選択画面のフレーム
 */
public class AISelectFrame extends JFrame implements ActionListener {
	/** シリアルVersionID */
	private static final long serialVersionUID = 1L;

	/** Log */
	static Logger log = Logger.getLogger(AISelectFrame.class);

	/** 親ウィンドウ */
	protected NullpoMinoSwing owner;

	/** プレイヤー number */
	protected int playerID;

	/** AIのクラス一覧 */
	protected String[] aiPathList;

	/** AIの名前一覧 */
	protected String[] aiNameList;

	/** Current AIのクラス */
	protected String currentAI;

	/** AIのID */
	protected int aiID = 0;

	/** AIの移動間隔 */
	protected int aiMoveDelay = 0;

	/** AIの思考の待ち time */
	protected int aiThinkDelay = 0;

	/** AIでスレッドを使う */
	protected boolean aiUseThread = false;
	
	protected boolean aiShowHint = false;

	/** AI一覧リストボックス */
	protected JList listboxAI;

	/** AIの移動間隔のテキストボックス */
	protected JTextField txtfldAIMoveDelay;

	/** AIの思考の待ち timeのテキストボックス */
	protected JTextField txtfldAIThinkDelay;

	/** AIでスレッド使用 check ボックス */
	protected JCheckBox chkboxAIUseThread;
	
	protected JCheckBox chkBoxAIShowHint;

	/**
	 * Constructor
	 * @param owner 親ウィンドウ
	 * @throws HeadlessException キーボード、マウス、ディスプレイなどが存在しない場合の例外
	 */
	public AISelectFrame(NullpoMinoSwing owner) throws HeadlessException {
		super();
		this.owner = owner;

		try {
			BufferedReader in = new BufferedReader(new FileReader("config/list/ai.lst"));
			aiPathList = loadAIList(in);
			aiNameList = loadAINames(aiPathList);
			in.close();
		} catch (IOException e) {
			log.warn("Failed to load AI list", e);
		}

		// GUIのInitialization
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		initUI();
		pack();
	}

	/**
	 * このフレームを表示するときに実行する処理
	 * @param pl プレイヤー number
	 */
	public void load(int pl) {
		this.playerID = pl;

		setTitle(NullpoMinoSwing.getUIText("Title_AISelect") + " (" + (playerID+1) + "P)");

		currentAI = NullpoMinoSwing.propGlobal.getProperty(playerID + ".ai", "");
		aiMoveDelay = NullpoMinoSwing.propGlobal.getProperty(playerID + ".aiMoveDelay", 0);
		aiThinkDelay = NullpoMinoSwing.propGlobal.getProperty(playerID + ".aiThinkDelay", 0);
		aiUseThread = NullpoMinoSwing.propGlobal.getProperty(playerID + ".aiUseThread", true);
		aiShowHint = NullpoMinoSwing.propGlobal.getProperty(playerID + ".aiShowHint", false);

		aiID = -1;
		listboxAI.clearSelection();
		for(int i = 0; i < aiPathList.length; i++) {
			if(currentAI.equals(aiPathList[i])) {
				aiID = i;
				listboxAI.setSelectedIndex(i);
				break;
			}
		}

		txtfldAIMoveDelay.setText(String.valueOf(aiMoveDelay));
		txtfldAIThinkDelay.setText(String.valueOf(aiThinkDelay));
		chkboxAIUseThread.setSelected(aiUseThread);
		chkBoxAIShowHint.setSelected(aiShowHint);
	}

	/**
	 * AI一覧を読み込み
	 * @param bf 読み込み元のテキストファイル
	 * @return AI一覧
	 */
	public String[] loadAIList(BufferedReader bf) {
		ArrayList<String> aiArrayList = new ArrayList<String>();

		while(true) {
			String name = null;
			try {
				name = bf.readLine();
			} catch (Exception e) {
				break;
			}
			if(name == null) break;
			if(name.length() == 0) break;

			if(!name.startsWith("#"))
				aiArrayList.add(name);
		}

		String[] aiStringList = new String[aiArrayList.size()];
		for(int i = 0; i < aiArrayList.size(); i++) aiStringList[i] = aiArrayList.get(i);

		return aiStringList;
	}

	/**
	 * AIの名前一覧を作成
	 * @param aiPath AIのクラスのリスト
	 * @return AIの名前一覧
	 */
	public String[] loadAINames(String[] aiPath) {
		String[] aiName = new String[aiPath.length];

		for(int i = 0; i < aiPath.length; i++) {
			Class<?> aiClass;
			AIPlayer aiObj;
			aiName[i] = "[Invalid]";

			try {
				aiClass = Class.forName(aiPath[i]);
				aiObj = (AIPlayer) aiClass.newInstance();
				aiName[i] = aiObj.getName();
			} catch(ClassNotFoundException e) {
				log.warn("AI class " + aiPath[i] + " not found", e);
			} catch(Throwable e) {
				log.warn("AI class " + aiPath[i] + " load failed", e);
			}
		}

		return aiName;
	}

	/**
	 * GUIをInitialization
	 */
	protected void initUI() {
		this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

		// AIリスト
		JPanel panelAIList = new JPanel();
		panelAIList.setLayout(new BorderLayout());
		panelAIList.setAlignmentX(LEFT_ALIGNMENT);
		this.add(panelAIList);

		String[] strList = new String[aiPathList.length];
		for(int i = 0; i < strList.length; i++) {
			strList[i] = aiNameList[i] + " (" + aiPathList[i] + ")";
		}
		listboxAI = new JList(strList);

		JScrollPane scpaneAI = new JScrollPane(listboxAI);
		scpaneAI.setPreferredSize(new Dimension(400, 250));
		panelAIList.add(scpaneAI, BorderLayout.CENTER);

		JButton btnNoUse = new JButton(NullpoMinoSwing.getUIText("AISelect_NoUse"));
		btnNoUse.setMnemonic('N');
		btnNoUse.addActionListener(this);
		btnNoUse.setActionCommand("AISelect_NoUse");
		btnNoUse.setMaximumSize(new Dimension(Short.MAX_VALUE, 30));
		panelAIList.add(btnNoUse, BorderLayout.SOUTH);

		// AIの移動間隔のテキストボックス
		JPanel panelTxtfldAIMoveDelay = new JPanel();
		panelTxtfldAIMoveDelay.setLayout(new BorderLayout());
		panelTxtfldAIMoveDelay.setAlignmentX(LEFT_ALIGNMENT);
		this.add(panelTxtfldAIMoveDelay);

		panelTxtfldAIMoveDelay.add(new JLabel(NullpoMinoSwing.getUIText("AISelect_LabelAIMoveDelay")), BorderLayout.WEST);

		txtfldAIMoveDelay = new JTextField(20);
		panelTxtfldAIMoveDelay.add(txtfldAIMoveDelay, BorderLayout.EAST);

		// AIの移動間隔のテキストボックス
		JPanel panelTxtfldAIThinkDelay = new JPanel();
		panelTxtfldAIThinkDelay.setLayout(new BorderLayout());
		panelTxtfldAIThinkDelay.setAlignmentX(LEFT_ALIGNMENT);
		this.add(panelTxtfldAIThinkDelay);

		panelTxtfldAIThinkDelay.add(new JLabel(NullpoMinoSwing.getUIText("AISelect_LabelAIThinkDelay")), BorderLayout.WEST);

		txtfldAIThinkDelay = new JTextField(20);
		panelTxtfldAIThinkDelay.add(txtfldAIThinkDelay, BorderLayout.EAST);

		// AIスレッド使用 check ボックス
		chkboxAIUseThread = new JCheckBox(NullpoMinoSwing.getUIText("AISelect_CheckboxAIUseThread"));
		chkboxAIUseThread.setAlignmentX(LEFT_ALIGNMENT);
		chkboxAIUseThread.setMnemonic('T');
		this.add(chkboxAIUseThread);
		
		chkBoxAIShowHint = new JCheckBox(NullpoMinoSwing.getUIText("AISelect_CheckboxAIShowHint"));
		chkBoxAIShowHint.setAlignmentX(LEFT_ALIGNMENT);
		chkBoxAIShowHint.setMnemonic('H');
		this.add(chkBoxAIShowHint);

		//  button類
		JPanel panelButtons = new JPanel();
		panelButtons.setLayout(new BoxLayout(panelButtons, BoxLayout.X_AXIS));
		panelButtons.setAlignmentX(LEFT_ALIGNMENT);
		this.add(panelButtons);

		JButton btnOK = new JButton(NullpoMinoSwing.getUIText("AISelect_OK"));
		btnOK.setMnemonic('O');
		btnOK.addActionListener(this);
		btnOK.setActionCommand("AISelect_OK");
		btnOK.setAlignmentX(LEFT_ALIGNMENT);
		btnOK.setMaximumSize(new Dimension(Short.MAX_VALUE, 30));
		panelButtons.add(btnOK);
		this.getRootPane().setDefaultButton(btnOK);

		JButton btnCancel = new JButton(NullpoMinoSwing.getUIText("AISelect_Cancel"));
		btnCancel.setMnemonic('C');
		btnCancel.addActionListener(this);
		btnCancel.setActionCommand("AISelect_Cancel");
		btnCancel.setAlignmentX(LEFT_ALIGNMENT);
		btnCancel.setMaximumSize(new Dimension(Short.MAX_VALUE, 30));
		panelButtons.add(btnCancel);
	}

	/*
	 *  Called when button clicked
	 */
	public void actionPerformed(ActionEvent e) {
		// AI使わない button
		if(e.getActionCommand() == "AISelect_NoUse") {
			listboxAI.clearSelection();
		}
		// OK
		else if(e.getActionCommand() == "AISelect_OK") {
			aiID = listboxAI.getSelectedIndex();
			try {
				aiMoveDelay = Integer.parseInt(txtfldAIMoveDelay.getText());
			} catch (NumberFormatException e2) {
				aiMoveDelay = -1;
			}
			try {
				aiThinkDelay = Integer.parseInt(txtfldAIThinkDelay.getText());
			} catch (NumberFormatException e2) {
				aiThinkDelay = 0;
			}
			aiUseThread = chkboxAIUseThread.isSelected();
			aiShowHint=chkBoxAIShowHint.isSelected();

			if(aiID >= 0) NullpoMinoSwing.propGlobal.setProperty(playerID + ".ai", aiPathList[aiID]);
			else NullpoMinoSwing.propGlobal.setProperty(playerID + ".ai", "");
			NullpoMinoSwing.propGlobal.setProperty(playerID + ".aiMoveDelay", aiMoveDelay);
			NullpoMinoSwing.propGlobal.setProperty(playerID + ".aiThinkDelay", aiThinkDelay);
			NullpoMinoSwing.propGlobal.setProperty(playerID + ".aiUseThread", aiUseThread);
			NullpoMinoSwing.propGlobal.setProperty(playerID + ".aiShowHint", aiShowHint);
			
			NullpoMinoSwing.saveConfig();

			this.setVisible(false);
		}
		// Cancel
		else if(e.getActionCommand() == "AISelect_Cancel") {
			this.setVisible(false);
		}
	}
}
