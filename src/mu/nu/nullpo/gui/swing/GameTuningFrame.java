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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;

/**
 * チューニング設定画面の frame
 */
public class GameTuningFrame extends JFrame implements ActionListener {
	/** Serial version ID */
	private static final long serialVersionUID = 1L;

	/** 親ウィンドウ */
	protected NullpoMinoSwing owner;

	/** Player number */
	protected int playerID;

	/** A buttonでのrotationDirectionはルールに従う */
	protected JRadioButton radioRotateButtonDefaultRightAuto;
	/** A buttonでのrotationDirectionを左rotationに固定 */
	protected JRadioButton radioRotateButtonDefaultRightLeft;
	/** A buttonでのrotationDirectionを右rotationに固定 */
	protected JRadioButton radioRotateButtonDefaultRightRight;

	/** 絵柄のComboボックス */
	protected JComboBox comboboxSkin;
	/** Block画像 */
	protected BufferedImage[] imgBlockSkins;

	/** 最小DAS */
	protected JTextField txtfldMinDAS;
	/** MaximumDAS */
	protected JTextField txtfldMaxDAS;

	/** 横移動速度 */
	protected JTextField txtfldDasDelay;

	/** Checkbox to enable swapping the roles of up/down buttons in-game */
	protected JCheckBox chkboxReverseUpDown;

	/** Diagonal move: Auto */
	protected JRadioButton radioMoveDiagonalAuto;
	/** Diagonal move: Disable */
	protected JRadioButton radioMoveDiagonalDisable;
	/** Diagonal move: Enable */
	protected JRadioButton radioMoveDiagonalEnable;

	/**
	 * Constructor
	 * @param owner 親ウィンドウ
	 * @throws HeadlessException キーボード, マウス, ディスプレイなどが存在しない場合の例外
	 */
	public GameTuningFrame(NullpoMinoSwing owner) throws HeadlessException {
		super();
		this.owner = owner;

		// Block画像の読み込み
		loadBlockSkins();

		// GUIのInitialization
		setTitle(NullpoMinoSwing.getUIText("Title_GameTuning"));
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setResizable(false);
		initUI();
		pack();
	}

	/**
	 * GUIのInitialization
	 */
	protected void initUI() {
		this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

		// ---------- A buttonでのrotationDirection ----------
		JPanel pRotateButtonDefaultRight = new JPanel();
		pRotateButtonDefaultRight.setLayout(new BoxLayout(pRotateButtonDefaultRight, BoxLayout.Y_AXIS));
		pRotateButtonDefaultRight.setAlignmentX(LEFT_ALIGNMENT);
		this.add(pRotateButtonDefaultRight);

		JLabel lRotateButtonDefaultRight = new JLabel(NullpoMinoSwing.getUIText("GameTuning_RotateButtonDefaultRight_Label"));
		pRotateButtonDefaultRight.add(lRotateButtonDefaultRight);

		ButtonGroup gRotateButtonDefaultRight = new ButtonGroup();

		radioRotateButtonDefaultRightAuto = new JRadioButton(NullpoMinoSwing.getUIText("GameTuning_RotateButtonDefaultRight_Auto"));
		pRotateButtonDefaultRight.add(radioRotateButtonDefaultRightAuto);
		gRotateButtonDefaultRight.add(radioRotateButtonDefaultRightAuto);

		radioRotateButtonDefaultRightLeft = new JRadioButton(NullpoMinoSwing.getUIText("GameTuning_RotateButtonDefaultRight_Left"));
		pRotateButtonDefaultRight.add(radioRotateButtonDefaultRightLeft);
		gRotateButtonDefaultRight.add(radioRotateButtonDefaultRightLeft);

		radioRotateButtonDefaultRightRight = new JRadioButton(NullpoMinoSwing.getUIText("GameTuning_RotateButtonDefaultRight_Right"));
		pRotateButtonDefaultRight.add(radioRotateButtonDefaultRightRight);
		gRotateButtonDefaultRight.add(radioRotateButtonDefaultRightRight);

		// ---------- Diagonal Move ----------
		JPanel pMoveDiagonal = new JPanel();
		pMoveDiagonal.setLayout(new BoxLayout(pMoveDiagonal, BoxLayout.Y_AXIS));
		pMoveDiagonal.setAlignmentX(LEFT_ALIGNMENT);
		this.add(pMoveDiagonal);

		JLabel lMoveDiagonal = new JLabel(NullpoMinoSwing.getUIText("GameTuning_MoveDiagonal_Label"));
		pMoveDiagonal.add(lMoveDiagonal);

		ButtonGroup gMoveDiagonal = new ButtonGroup();

		radioMoveDiagonalAuto = new JRadioButton(NullpoMinoSwing.getUIText("GameTuning_MoveDiagonal_Auto"));
		pMoveDiagonal.add(radioMoveDiagonalAuto);
		gMoveDiagonal.add(radioMoveDiagonalAuto);

		radioMoveDiagonalDisable = new JRadioButton(NullpoMinoSwing.getUIText("GameTuning_MoveDiagonal_Disable"));
		pMoveDiagonal.add(radioMoveDiagonalDisable);
		gMoveDiagonal.add(radioMoveDiagonalDisable);

		radioMoveDiagonalEnable = new JRadioButton(NullpoMinoSwing.getUIText("GameTuning_MoveDiagonal_Enable"));
		pMoveDiagonal.add(radioMoveDiagonalEnable);
		gMoveDiagonal.add(radioMoveDiagonalEnable);

		// ---------- 絵柄 ----------
		JPanel pSkin = new JPanel();
		pSkin.setAlignmentX(LEFT_ALIGNMENT);
		this.add(pSkin);

		JLabel lSkin = new JLabel(NullpoMinoSwing.getUIText("GameTuning_Skin_Label"));
		pSkin.add(lSkin);

		DefaultComboBoxModel model = new DefaultComboBoxModel();
		model.addElement(new ComboLabel(NullpoMinoSwing.getUIText("GameTuning_Skin_Auto")));
		for(int i = 0; i < imgBlockSkins.length; i++) {
			model.addElement(new ComboLabel("" + i, new ImageIcon(imgBlockSkins[i])));
		}

		comboboxSkin = new JComboBox(model);
		comboboxSkin.setRenderer(new ComboLabelCellRenderer());
		comboboxSkin.setPreferredSize(new Dimension(190, 30));
		pSkin.add(comboboxSkin);

		// ---------- 最低DAS ----------
		JPanel pMinDAS = new JPanel();
		pMinDAS.setAlignmentX(LEFT_ALIGNMENT);
		this.add(pMinDAS);

		JLabel lMinDAS = new JLabel(NullpoMinoSwing.getUIText("GameTuning_MinDAS_Label"));
		pMinDAS.add(lMinDAS);

		txtfldMinDAS = new JTextField(5);
		pMinDAS.add(txtfldMinDAS);

		// ---------- MaximumDAS ----------
		JPanel pMaxDAS = new JPanel();
		pMaxDAS.setAlignmentX(LEFT_ALIGNMENT);
		this.add(pMaxDAS);

		JLabel lMaxDAS = new JLabel(NullpoMinoSwing.getUIText("GameTuning_MaxDAS_Label"));
		pMaxDAS.add(lMaxDAS);

		txtfldMaxDAS = new JTextField(5);
		pMaxDAS.add(txtfldMaxDAS);

		// ---------- 横移動速度 ----------
		JPanel pDasDelay = new JPanel();
		pDasDelay.setAlignmentX(LEFT_ALIGNMENT);
		this.add(pDasDelay);

		JLabel lDasDelay = new JLabel(NullpoMinoSwing.getUIText("GameTuning_DasDelay_Label"));
		pDasDelay.add(lDasDelay);

		txtfldDasDelay = new JTextField(5);
		pDasDelay.add(txtfldDasDelay);

		// ---------- Reverse Up/Down ----------
		JPanel pReverseUpDown = new JPanel();
		pReverseUpDown.setAlignmentX(LEFT_ALIGNMENT);
		this.add(pReverseUpDown);

		JLabel lReverseUpDown = new JLabel(NullpoMinoSwing.getUIText("GameTuning_ReverseUpDown_Label"));
		pReverseUpDown.add(lReverseUpDown);

		chkboxReverseUpDown = new JCheckBox();
		pReverseUpDown.add(chkboxReverseUpDown);

		// ---------- 画面下の button ----------
		JPanel pButtons = new JPanel();
		pButtons.setAlignmentX(LEFT_ALIGNMENT);
		this.add(pButtons);

		JButton buttonOK = new JButton(NullpoMinoSwing.getUIText("GameTuning_OK"));
		buttonOK.setMnemonic('O');
		buttonOK.addActionListener(this);
		buttonOK.setActionCommand("GameTuning_OK");
		pButtons.add(buttonOK);

		JButton buttonCancel = new JButton(NullpoMinoSwing.getUIText("GameTuning_Cancel"));
		buttonCancel.setMnemonic('C');
		buttonCancel.addActionListener(this);
		buttonCancel.setActionCommand("GameTuning_Cancel");
		pButtons.add(buttonCancel);
	}

	/**
	 * Block画像を読み込み
	 */
	protected void loadBlockSkins() {
		BufferedImage imgBlock = (BufferedImage) ResourceHolderSwing.imgBlock;
		int numSkins = imgBlock.getHeight() / 16;

		imgBlockSkins = new BufferedImage[numSkins];

		for(int i = 0; i < numSkins; i++) {
			imgBlockSkins[i] = new BufferedImage(144, 16, BufferedImage.TYPE_INT_RGB);
			imgBlockSkins[i].getGraphics().drawImage(imgBlock, 0, 0, 144, 16, 0, i * 16, 144, (i * 16) + 16, null);
		}
	}

	/**
	 * この frame を表示するときに実行する処理
	 * @param pl Player number
	 */
	public void load(int pl) {
		this.playerID = pl;

		setTitle(NullpoMinoSwing.getUIText("Title_GameTuning") + " (" + (playerID+1) + "P)");

		int owRotateButtonDefaultRight = NullpoMinoSwing.propGlobal.getProperty(playerID + ".tuning.owRotateButtonDefaultRight", -1);
		if(owRotateButtonDefaultRight == -1) radioRotateButtonDefaultRightAuto.setSelected(true);
		if(owRotateButtonDefaultRight ==  0) radioRotateButtonDefaultRightLeft.setSelected(true);
		if(owRotateButtonDefaultRight ==  1) radioRotateButtonDefaultRightRight.setSelected(true);

		int owMoveDiagonal = NullpoMinoSwing.propGlobal.getProperty(playerID + ".tuning.owMoveDiagonal", -1);
		if(owMoveDiagonal == -1) radioMoveDiagonalAuto.setSelected(true);
		if(owMoveDiagonal ==  0) radioMoveDiagonalDisable.setSelected(true);
		if(owMoveDiagonal ==  1) radioMoveDiagonalEnable.setSelected(true);

		int owSkin = NullpoMinoSwing.propGlobal.getProperty(playerID + ".tuning.owSkin", -1);
		comboboxSkin.setSelectedIndex(owSkin + 1);

		txtfldMinDAS.setText(NullpoMinoSwing.propGlobal.getProperty(playerID + ".tuning.owMinDAS", "-1"));
		txtfldMaxDAS.setText(NullpoMinoSwing.propGlobal.getProperty(playerID + ".tuning.owMaxDAS", "-1"));
		txtfldDasDelay.setText(NullpoMinoSwing.propGlobal.getProperty(playerID + ".tuning.owDasDelay", "-1"));
		chkboxReverseUpDown.setSelected(NullpoMinoSwing.propGlobal.getProperty(playerID + ".tuning.owReverseUpDown", false));
	}

	/**
	 * 保存
	 */
	protected void save() {
		int owRotateButtonDefaultRight = -1;
		if(radioRotateButtonDefaultRightAuto.isSelected()) owRotateButtonDefaultRight = -1;
		if(radioRotateButtonDefaultRightLeft.isSelected()) owRotateButtonDefaultRight =  0;
		if(radioRotateButtonDefaultRightRight.isSelected()) owRotateButtonDefaultRight = 1;
		NullpoMinoSwing.propGlobal.setProperty(playerID + ".tuning.owRotateButtonDefaultRight", owRotateButtonDefaultRight);

		int owMoveDiagonal = -1;
		if(radioMoveDiagonalAuto.isSelected()) owMoveDiagonal = -1;
		if(radioMoveDiagonalDisable.isSelected()) owMoveDiagonal = 0;
		if(radioMoveDiagonalEnable.isSelected()) owMoveDiagonal = 1;
		NullpoMinoSwing.propGlobal.setProperty(playerID + ".tuning.owMoveDiagonal", owMoveDiagonal);

		int owSkin = comboboxSkin.getSelectedIndex() - 1;
		NullpoMinoSwing.propGlobal.setProperty(playerID + ".tuning.owSkin", owSkin);

		int owMinDAS = NullpoMinoSwing.getIntTextField(-1, txtfldMinDAS);
		NullpoMinoSwing.propGlobal.setProperty(playerID + ".tuning.owMinDAS", owMinDAS);
		int owMaxDAS = NullpoMinoSwing.getIntTextField(-1, txtfldMaxDAS);
		NullpoMinoSwing.propGlobal.setProperty(playerID + ".tuning.owMaxDAS", owMaxDAS);
		int owDasDelay = NullpoMinoSwing.getIntTextField(-1, txtfldDasDelay);
		NullpoMinoSwing.propGlobal.setProperty(playerID + ".tuning.owDasDelay", owDasDelay);
		boolean owReverseUpDown = chkboxReverseUpDown.isSelected();
		NullpoMinoSwing.propGlobal.setProperty(playerID + ".tuning.owReverseUpDown", owReverseUpDown);

		NullpoMinoSwing.saveConfig();
	}

	/*
	 *  Called when button clicked
	 */
	public void actionPerformed(ActionEvent e) {
		// OK
		if(e.getActionCommand() == "GameTuning_OK") {
			save();
			this.setVisible(false);
		}
		// Cancel
		else if(e.getActionCommand() == "GameTuning_Cancel") {
			this.setVisible(false);
		}
	}

	/**
	 * 画像表示Comboボックスの項目<br>
	 * <a href="http://www.javadrive.jp/tutorial/jcombobox/index20.html">出典</a>
	 */
	protected class ComboLabel {
		private String text = "";
		private Icon icon = null;

		public ComboLabel() {
		}

		public ComboLabel(String text) {
			this.text = text;
		}

		public ComboLabel(Icon icon) {
			this.icon = icon;
		}

		public ComboLabel(String text, Icon icon) {
			this.text = text;
			this.icon = icon;
		}

		public void setText(String text) {
			this.text = text;
		}

		public String getText() {
			return text;
		}

		public void setIcon(Icon icon) {
			this.icon = icon;
		}

		public Icon getIcon() {
			return icon;
		}
	}

	/**
	 * 画像表示ComboボックスのListCellRenderer<br>
	 * <a href="http://www.javadrive.jp/tutorial/jcombobox/index20.html">出典</a>
	 */
	protected class ComboLabelCellRenderer extends JLabel implements ListCellRenderer {
		private static final long serialVersionUID = 1L;

		public ComboLabelCellRenderer() {
			this.setOpaque(true);
		}

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			ComboLabel data = (ComboLabel)value;
			setText(data.getText());
			setIcon(data.getIcon());

			if(isSelected) {
				setForeground(Color.white);
				setBackground(Color.black);
			} else {
				setForeground(Color.black);
				setBackground(Color.white);
			}

			return this;
		}
	}
}
