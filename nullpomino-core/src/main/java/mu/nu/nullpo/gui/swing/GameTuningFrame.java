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
 * Tuning Settings screen frame
 */
public class GameTuningFrame extends JFrame implements ActionListener {
	/** Serial version ID */
	private static final long serialVersionUID = 1L;

	/** Outline type names (before translation) */
	protected static final String[] OUTLINE_TYPE_NAMES = {
		"GameTuning_OutlineType_Auto", "GameTuning_OutlineType_None", "GameTuning_OutlineType_Normal",
		"GameTuning_OutlineType_Connect", "GameTuning_OutlineType_SameColor"
	};

	/** Parent window */
	protected NullpoMinoSwing owner;

	/** Player number */
	protected int playerID;

	/** A buttonInrotationDirectionFollow the rules */
	protected JRadioButton radioRotateButtonDefaultRightAuto;
	/** A buttonInrotationDirectionLeftrotationFixed on */
	protected JRadioButton radioRotateButtonDefaultRightLeft;
	/** A buttonInrotationDirectionRightrotationFixed on */
	protected JRadioButton radioRotateButtonDefaultRightRight;

	/** Of pictureComboBox */
	protected JComboBox comboboxSkin;
	/** BlockImage */
	protected BufferedImage[] imgBlockSkins;

	/** Outline type combobox */
	protected JComboBox comboboxBlockOutlineType;

	/** MinimumDAS */
	protected JTextField txtfldMinDAS;
	/** MaximumDAS */
	protected JTextField txtfldMaxDAS;

	/** Lateral movement speed */
	protected JTextField txtfldDasDelay;

	/** Checkbox to enable swapping the roles of up/down buttons in-game */
	protected JCheckBox chkboxReverseUpDown;

	/** Diagonal move: Auto */
	protected JRadioButton radioMoveDiagonalAuto;
	/** Diagonal move: Disable */
	protected JRadioButton radioMoveDiagonalDisable;
	/** Diagonal move: Enable */
	protected JRadioButton radioMoveDiagonalEnable;

	/** Show Outline Only: Auto */
	protected JRadioButton radioBlockShowOutlineOnlyAuto;
	/** Show Outline Only: Disable */
	protected JRadioButton radioBlockShowOutlineOnlyDisable;
	/** Show Outline Only: Enable */
	protected JRadioButton radioBlockShowOutlineOnlyEnable;

	/**
	 * Constructor
	 * @param owner Parent window
	 * @throws HeadlessException Keyboard, Mouse, Exceptions such as the display if there is no
	 */
	public GameTuningFrame(NullpoMinoSwing owner) throws HeadlessException {
		super();
		this.owner = owner;

		// BlockLoading Images
		loadBlockSkins();

		// GUIOfInitialization
		setTitle(NullpoMinoSwing.getUIText("Title_GameTuning"));
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setResizable(false);
		initUI();
		pack();
	}

	/**
	 * GUIOfInitialization
	 */
	protected void initUI() {
		this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

		// ---------- A buttonInrotationDirection ----------
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

		// ---------- Show Outline Only ----------
		JPanel pBlockShowOutlineOnly = new JPanel();
		pBlockShowOutlineOnly.setLayout(new BoxLayout(pBlockShowOutlineOnly, BoxLayout.Y_AXIS));
		pBlockShowOutlineOnly.setAlignmentX(LEFT_ALIGNMENT);
		this.add(pBlockShowOutlineOnly);

		JLabel lBlockShowOutlineOnly = new JLabel(NullpoMinoSwing.getUIText("GameTuning_BlockShowOutlineOnly_Label"));
		pBlockShowOutlineOnly.add(lBlockShowOutlineOnly);

		ButtonGroup gBlockShowOutlineOnly = new ButtonGroup();

		radioBlockShowOutlineOnlyAuto = new JRadioButton(NullpoMinoSwing.getUIText("GameTuning_BlockShowOutlineOnly_Auto"));
		pBlockShowOutlineOnly.add(radioBlockShowOutlineOnlyAuto);
		gBlockShowOutlineOnly.add(radioBlockShowOutlineOnlyAuto);

		radioBlockShowOutlineOnlyDisable = new JRadioButton(NullpoMinoSwing.getUIText("GameTuning_BlockShowOutlineOnly_Disable"));
		pBlockShowOutlineOnly.add(radioBlockShowOutlineOnlyDisable);
		gBlockShowOutlineOnly.add(radioBlockShowOutlineOnlyDisable);

		radioBlockShowOutlineOnlyEnable = new JRadioButton(NullpoMinoSwing.getUIText("GameTuning_BlockShowOutlineOnly_Enable"));
		pBlockShowOutlineOnly.add(radioBlockShowOutlineOnlyEnable);
		gBlockShowOutlineOnly.add(radioBlockShowOutlineOnlyEnable);

		// ---------- Picture ----------
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

		// ---------- Outline Type ----------
		JPanel pOutlineType = new JPanel();
		pOutlineType.setAlignmentX(LEFT_ALIGNMENT);
		this.add(pOutlineType);

		JLabel lOutlineType = new JLabel(NullpoMinoSwing.getUIText("GameTuning_OutlineType_Label"));
		pOutlineType.add(lOutlineType);

		String[] strArrayOutlineType = new String[OUTLINE_TYPE_NAMES.length];
		for(int i = 0; i < OUTLINE_TYPE_NAMES.length; i++) {
			strArrayOutlineType[i] = NullpoMinoSwing.getUIText(OUTLINE_TYPE_NAMES[i]);
		}
		DefaultComboBoxModel modelOutlineType = new DefaultComboBoxModel(strArrayOutlineType);
		comboboxBlockOutlineType = new JComboBox(modelOutlineType);
		comboboxBlockOutlineType.setPreferredSize(new Dimension(190, 30));
		pOutlineType.add(comboboxBlockOutlineType);

		// ---------- LowestDAS ----------
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

		// ---------- Lateral movement speed ----------
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

		// ---------- The bottom of the screen button ----------
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
	 * BlockLoad an image
	 */
	protected void loadBlockSkins() {
		int numSkins = ResourceHolderSwing.imgNormalBlockList.size();
		imgBlockSkins = new BufferedImage[numSkins];

		for(int i = 0; i < numSkins; i++) {
			BufferedImage imgBlock = (BufferedImage) ResourceHolderSwing.imgNormalBlockList.get(i);
			boolean isSticky = ResourceHolderSwing.blockStickyFlagList.get(i);

			imgBlockSkins[i] = new BufferedImage(144, 16, BufferedImage.TYPE_INT_RGB);

			if(isSticky) {
				for(int j = 0; j < 9; j++) {
					imgBlockSkins[i].getGraphics().drawImage(imgBlock, j * 16, 0, (j * 16) + 16, 16, 0, j * 16, 16, (j * 16) + 16, null);
				}
			} else {
				imgBlockSkins[i].getGraphics().drawImage(imgBlock, 0, 0, 144, 16, 0, 0, 144, 16, null);
			}
		}
	}

	/**
	 * This frame Action to take when you view the
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

		int owBlockShowOutlineOnly = NullpoMinoSwing.propGlobal.getProperty(playerID + ".tuning.owBlockShowOutlineOnly", -1);
		if(owBlockShowOutlineOnly == -1) radioBlockShowOutlineOnlyAuto.setSelected(true);
		if(owBlockShowOutlineOnly ==  0) radioBlockShowOutlineOnlyDisable.setSelected(true);
		if(owBlockShowOutlineOnly ==  1) radioBlockShowOutlineOnlyEnable.setSelected(true);

		int owSkin = NullpoMinoSwing.propGlobal.getProperty(playerID + ".tuning.owSkin", -1);
		comboboxSkin.setSelectedIndex(owSkin + 1);

		int owBlockOutlineType = NullpoMinoSwing.propGlobal.getProperty(playerID + ".tuning.owBlockOutlineType", -1);
		comboboxBlockOutlineType.setSelectedIndex(owBlockOutlineType + 1);

		txtfldMinDAS.setText(NullpoMinoSwing.propGlobal.getProperty(playerID + ".tuning.owMinDAS", "-1"));
		txtfldMaxDAS.setText(NullpoMinoSwing.propGlobal.getProperty(playerID + ".tuning.owMaxDAS", "-1"));
		txtfldDasDelay.setText(NullpoMinoSwing.propGlobal.getProperty(playerID + ".tuning.owDasDelay", "-1"));
		chkboxReverseUpDown.setSelected(NullpoMinoSwing.propGlobal.getProperty(playerID + ".tuning.owReverseUpDown", false));
	}

	/**
	 * Save
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

		int owBlockShowOutlineOnly = -1;
		if(radioBlockShowOutlineOnlyAuto.isSelected()) owBlockShowOutlineOnly = -1;
		if(radioBlockShowOutlineOnlyDisable.isSelected()) owBlockShowOutlineOnly = 0;
		if(radioBlockShowOutlineOnlyEnable.isSelected()) owBlockShowOutlineOnly = 1;
		NullpoMinoSwing.propGlobal.setProperty(playerID + ".tuning.owBlockShowOutlineOnly", owBlockShowOutlineOnly);

		int owSkin = comboboxSkin.getSelectedIndex() - 1;
		NullpoMinoSwing.propGlobal.setProperty(playerID + ".tuning.owSkin", owSkin);

		int owBlockOutlineType = comboboxBlockOutlineType.getSelectedIndex() - 1;
		NullpoMinoSwing.propGlobal.setProperty(playerID + ".tuning.owBlockOutlineType", owBlockOutlineType);

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
	 * Image displayComboItems in box<br>
	 * <a href="http://www.javadrive.jp/tutorial/jcombobox/index20.html">Source</a>
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
	 * Image displayComboOf the boxListCellRenderer<br>
	 * <a href="http://www.javadrive.jp/tutorial/jcombobox/index20.html">Source</a>
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
