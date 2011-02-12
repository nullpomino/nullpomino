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
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

/**
 * 設定画面の frame
 */
public class GeneralConfigFrame extends JFrame implements ActionListener {
	/** Serial version ID */
	private static final long serialVersionUID = 1L;

	/** Screen size table */
	protected static final int[][] SCREENSIZE_TABLE =
	{
		{320,240}, {400,300}, {480,360}, {512,384}, {640,480}, {800,600}, {1024,768}, {1152,864}, {1280,960}
	};

	/** 親ウィンドウ */
	protected NullpoMinoSwing owner;

	/** Model of screen size combobox */
	protected DefaultComboBoxModel modelScreenSize;

	/** Screen size combobox */
	protected JComboBox comboboxScreenSize;

	/** MaximumFPS */
	protected JTextField txtfldMaxFPS;

	/** Sound effectsの音量 */
	protected JTextField txtfldSEVolume;

	/** Line clear effect speed */
	protected JTextField txtfldLineClearEffectSpeed;

	/** FPS表示 */
	protected JCheckBox chkboxShowFPS;

	/** Background表示 */
	protected JCheckBox chkboxShowBackground;

	/** Meter表示 */
	protected JCheckBox chkboxShowMeter;

	/** fieldのBlockの絵を表示 ( check なしの場合は枠線だけ) */
	protected JCheckBox chkboxShowFieldBlockGraphics;

	/** シンプルな絵柄のBlockを使う */
	protected JCheckBox chkboxSimpleBlock;

	/** Sound effects */
	protected JCheckBox chkboxSE;

	/** ネイティブのLook and Feelを使う */
	protected JCheckBox chkboxUseNativeLookAndFeel;

	/**  frame ステップ */
	protected JCheckBox chkboxEnableFrameStep;

	/** ghost ピースの上にNEXT表示 */
	protected JCheckBox chkboxNextShadow;

	/** 枠線型ghost ピース */
	protected JCheckBox chkboxOutlineGhost;

	/** Side piece preview */
	protected JCheckBox chkboxSideNext;

	/** Use bigger side piece preview */
	protected JCheckBox chkboxBigSideNext;

	/** Perfect FPS */
	protected JCheckBox chkboxPerfectFPSMode;

	/** Execute Thread.yield() during Perfect FPS mode */
	protected JCheckBox chkboxPerfectYield;

	/** Sync Display */
	protected JCheckBox chkboxSyncDisplay;

	/** Show line clear effect */
	protected JCheckBox chkboxShowLineClearEffect;

	/** Dark piece preview area */
	protected JCheckBox chkboxDarkNextArea;

	/** Show field BG grid */
	protected JCheckBox chkboxShowFieldBGGrid;

	/**
	 * Constructor
	 * @param owner 親ウィンドウ
	 * @throws HeadlessException キーボード, マウス, ディスプレイなどが存在しない場合の例外
	 */
	public GeneralConfigFrame(NullpoMinoSwing owner) throws HeadlessException {
		super();
		this.owner = owner;

		// GUIのInitialization
		setTitle(NullpoMinoSwing.getUIText("Title_GeneralConfig"));
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setResizable(false);
		initUI();
		pack();
	}

	/**
	 * GUIのInitialization
	 */
	protected void initUI() {
		this.getContentPane().setLayout(new BorderLayout());

		// * Tab pane
		JTabbedPane tabPane = new JTabbedPane();
		this.add(tabPane, BorderLayout.CENTER);

		// ** Basic Tab
		JPanel pBasicTab = new JPanel();
		pBasicTab.setLayout(new BoxLayout(pBasicTab, BoxLayout.Y_AXIS));
		tabPane.addTab(NullpoMinoSwing.getUIText("GeneralConfig_TabName_Basic"), pBasicTab);

		// ---------- Sound effectsの音量 ----------
		JPanel pSEVolume = new JPanel();
		pSEVolume.setAlignmentX(LEFT_ALIGNMENT);
		pBasicTab.add(pSEVolume);

		JLabel lSEVolume = new JLabel(NullpoMinoSwing.getUIText("GeneralConfig_SEVolume"));
		pSEVolume.add(lSEVolume);

		txtfldSEVolume = new JTextField(5);
		pSEVolume.add(txtfldSEVolume);

		// ---------- checkボックス ----------
		chkboxShowBackground = new JCheckBox(NullpoMinoSwing.getUIText("GeneralConfig_ShowBackground"));
		chkboxShowBackground.setAlignmentX(LEFT_ALIGNMENT);
		pBasicTab.add(chkboxShowBackground);

		chkboxShowMeter = new JCheckBox(NullpoMinoSwing.getUIText("GeneralConfig_ShowMeter"));
		chkboxShowMeter.setAlignmentX(LEFT_ALIGNMENT);
		pBasicTab.add(chkboxShowMeter);

		chkboxShowFieldBlockGraphics = new JCheckBox(NullpoMinoSwing.getUIText("GeneralConfig_ShowFieldBlockGraphics"));
		chkboxShowFieldBlockGraphics.setAlignmentX(LEFT_ALIGNMENT);
		pBasicTab.add(chkboxShowFieldBlockGraphics);

		chkboxSimpleBlock = new JCheckBox(NullpoMinoSwing.getUIText("GeneralConfig_SimpleBlock"));
		chkboxSimpleBlock.setAlignmentX(LEFT_ALIGNMENT);
		pBasicTab.add(chkboxSimpleBlock);

		chkboxSE = new JCheckBox(NullpoMinoSwing.getUIText("GeneralConfig_SE"));
		chkboxSE.setAlignmentX(LEFT_ALIGNMENT);
		pBasicTab.add(chkboxSE);

		chkboxNextShadow = new JCheckBox(NullpoMinoSwing.getUIText("GeneralConfig_NextShadow"));
		chkboxNextShadow.setAlignmentX(LEFT_ALIGNMENT);
		pBasicTab.add(chkboxNextShadow);

		chkboxOutlineGhost = new JCheckBox(NullpoMinoSwing.getUIText("GeneralConfig_OutlineGhost"));
		chkboxOutlineGhost.setAlignmentX(LEFT_ALIGNMENT);
		pBasicTab.add(chkboxOutlineGhost);

		chkboxSideNext = new JCheckBox(NullpoMinoSwing.getUIText("GeneralConfig_SideNext"));
		chkboxSideNext.setAlignmentX(LEFT_ALIGNMENT);
		pBasicTab.add(chkboxSideNext);

		chkboxBigSideNext = new JCheckBox(NullpoMinoSwing.getUIText("GeneralConfig_BigSideNext"));
		chkboxBigSideNext.setAlignmentX(LEFT_ALIGNMENT);
		pBasicTab.add(chkboxBigSideNext);

		chkboxDarkNextArea = new JCheckBox(NullpoMinoSwing.getUIText("GeneralConfig_DarkNextArea"));
		chkboxDarkNextArea.setAlignmentX(LEFT_ALIGNMENT);
		pBasicTab.add(chkboxDarkNextArea);

		chkboxShowFieldBGGrid = new JCheckBox(NullpoMinoSwing.getUIText("GeneralConfig_ShowFieldBGGrid"));
		chkboxShowFieldBGGrid.setAlignmentX(LEFT_ALIGNMENT);
		pBasicTab.add(chkboxShowFieldBGGrid);

		// ** Advanced Tab
		JPanel pAdvancedTab = new JPanel();
		pAdvancedTab.setLayout(new BoxLayout(pAdvancedTab, BoxLayout.Y_AXIS));
		tabPane.addTab(NullpoMinoSwing.getUIText("GeneralConfig_TabName_Advanced"), pAdvancedTab);

		// ---------- Screen size ----------
		JPanel pScreenSize = new JPanel();
		pScreenSize.setAlignmentX(LEFT_ALIGNMENT);
		pAdvancedTab.add(pScreenSize);

		JLabel lScreenSize = new JLabel(NullpoMinoSwing.getUIText("GeneralConfig_ScreenSize"));
		pScreenSize.add(lScreenSize);

		modelScreenSize = new DefaultComboBoxModel();
		for(int i = 0; i < SCREENSIZE_TABLE.length; i++) {
			String strTemp = SCREENSIZE_TABLE[i][0] + "x" + SCREENSIZE_TABLE[i][1];
			modelScreenSize.addElement(strTemp);
		}
		comboboxScreenSize = new JComboBox(modelScreenSize);
		pScreenSize.add(comboboxScreenSize);

		// ---------- MaximumFPS ----------
		JPanel pMaxFPS = new JPanel();
		pMaxFPS.setAlignmentX(LEFT_ALIGNMENT);
		pAdvancedTab.add(pMaxFPS);

		JLabel lMaxFPS = new JLabel(NullpoMinoSwing.getUIText("GeneralConfig_MaxFPS"));
		pMaxFPS.add(lMaxFPS);

		txtfldMaxFPS = new JTextField(5);
		pMaxFPS.add(txtfldMaxFPS);

		// ---------- Line clear effect speed ----------
		JPanel pLineClearEffectSpeed = new JPanel();
		pLineClearEffectSpeed.setAlignmentX(LEFT_ALIGNMENT);
		pAdvancedTab.add(pLineClearEffectSpeed);

		JLabel lLineClearEffectSpeed = new JLabel(NullpoMinoSwing.getUIText("GeneralConfig_LineClearEffectSpeed"));
		pLineClearEffectSpeed.add(lLineClearEffectSpeed);

		txtfldLineClearEffectSpeed = new JTextField(5);
		pLineClearEffectSpeed.add(txtfldLineClearEffectSpeed);

		// ---------- Checkboxes ----------
		chkboxShowFPS = new JCheckBox(NullpoMinoSwing.getUIText("GeneralConfig_ShowFPS"));
		chkboxShowFPS.setAlignmentX(LEFT_ALIGNMENT);
		pAdvancedTab.add(chkboxShowFPS);

		chkboxUseNativeLookAndFeel = new JCheckBox(NullpoMinoSwing.getUIText("GeneralConfig_UseNativeLookAndFeel"));
		chkboxUseNativeLookAndFeel.setAlignmentX(LEFT_ALIGNMENT);
		pAdvancedTab.add(chkboxUseNativeLookAndFeel);

		chkboxEnableFrameStep = new JCheckBox(NullpoMinoSwing.getUIText("GeneralConfig_EnableFrameStep"));
		chkboxEnableFrameStep.setAlignmentX(LEFT_ALIGNMENT);
		pAdvancedTab.add(chkboxEnableFrameStep);

		chkboxPerfectFPSMode = new JCheckBox(NullpoMinoSwing.getUIText("GeneralConfig_PerfectFPSMode"));
		chkboxPerfectFPSMode.setAlignmentX(LEFT_ALIGNMENT);
		pAdvancedTab.add(chkboxPerfectFPSMode);

		chkboxPerfectYield = new JCheckBox(NullpoMinoSwing.getUIText("GeneralConfig_PerfectYield"));
		chkboxPerfectYield.setAlignmentX(LEFT_ALIGNMENT);
		pAdvancedTab.add(chkboxPerfectYield);

		chkboxSyncDisplay = new JCheckBox(NullpoMinoSwing.getUIText("GeneralConfig_SyncDisplay"));
		chkboxSyncDisplay.setAlignmentX(LEFT_ALIGNMENT);
		pAdvancedTab.add(chkboxSyncDisplay);

		chkboxShowLineClearEffect = new JCheckBox(NullpoMinoSwing.getUIText("GeneralConfig_ShowLineClearEffect"));
		chkboxShowLineClearEffect.setAlignmentX(LEFT_ALIGNMENT);
		pAdvancedTab.add(chkboxShowLineClearEffect);

		// ---------- 画面下の button ----------
		JPanel pButtons = new JPanel();
		pButtons.setAlignmentX(LEFT_ALIGNMENT);
		this.add(pButtons, BorderLayout.SOUTH);

		JButton buttonOK = new JButton(NullpoMinoSwing.getUIText("GeneralConfig_OK"));
		buttonOK.setMnemonic('O');
		buttonOK.addActionListener(this);
		buttonOK.setActionCommand("GeneralConfig_OK");
		pButtons.add(buttonOK);

		JButton buttonCancel = new JButton(NullpoMinoSwing.getUIText("GeneralConfig_Cancel"));
		buttonCancel.setMnemonic('C');
		buttonCancel.addActionListener(this);
		buttonCancel.setActionCommand("GeneralConfig_Cancel");
		pButtons.add(buttonCancel);
	}

	/**
	 * Current 設定をGUIに反映させる
	 */
	public void load() {
		int sWidth = NullpoMinoSwing.propConfig.getProperty("option.screenwidth", 640);
		int sHeight = NullpoMinoSwing.propConfig.getProperty("option.screenheight", 480);
		comboboxScreenSize.setSelectedIndex(4);	// Default to 640x480
		for(int i = 0; i < SCREENSIZE_TABLE.length; i++) {
			if((sWidth == SCREENSIZE_TABLE[i][0]) && (sHeight == SCREENSIZE_TABLE[i][1])) {
				comboboxScreenSize.setSelectedIndex(i);
				break;
			}
		}

		txtfldMaxFPS.setText(String.valueOf(NullpoMinoSwing.propConfig.getProperty("option.maxfps", 60)));
		txtfldSEVolume.setText(String.valueOf(NullpoMinoSwing.propConfig.getProperty("option.sevolume", 1.0d)));
		txtfldLineClearEffectSpeed.setText(String.valueOf(NullpoMinoSwing.propConfig.getProperty("option.lineeffectspeed", 0) + 1));
		chkboxShowFPS.setSelected(NullpoMinoSwing.propConfig.getProperty("option.showfps", true));
		chkboxShowBackground.setSelected(NullpoMinoSwing.propConfig.getProperty("option.showbg", true));
		chkboxShowMeter.setSelected(NullpoMinoSwing.propConfig.getProperty("option.showmeter", true));
		chkboxShowFieldBlockGraphics.setSelected(NullpoMinoSwing.propConfig.getProperty("option.showfieldblockgraphics", true));
		chkboxSimpleBlock.setSelected(NullpoMinoSwing.propConfig.getProperty("option.simpleblock", false));
		chkboxSE.setSelected(NullpoMinoSwing.propConfig.getProperty("option.se", true));
		chkboxUseNativeLookAndFeel.setSelected(NullpoMinoSwing.propConfig.getProperty("option.usenativelookandfeel", true));
		chkboxEnableFrameStep.setSelected(NullpoMinoSwing.propConfig.getProperty("option.enableframestep", false));
		chkboxNextShadow.setSelected(NullpoMinoSwing.propConfig.getProperty("option.nextshadow", false));
		chkboxOutlineGhost.setSelected(NullpoMinoSwing.propConfig.getProperty("option.outlineghost", false));
		chkboxSideNext.setSelected(NullpoMinoSwing.propConfig.getProperty("option.sidenext", false));
		chkboxBigSideNext.setSelected(NullpoMinoSwing.propConfig.getProperty("option.bigsidenext", false));
		chkboxDarkNextArea.setSelected(NullpoMinoSwing.propConfig.getProperty("option.darknextarea", true));
		chkboxShowFieldBGGrid.setSelected(NullpoMinoSwing.propConfig.getProperty("option.showfieldbggrid", true));
		chkboxPerfectFPSMode.setSelected(NullpoMinoSwing.propConfig.getProperty("option.perfectFPSMode", false));
		chkboxPerfectYield.setSelected(NullpoMinoSwing.propConfig.getProperty("option.perfectYield", true));
		chkboxSyncDisplay.setSelected(NullpoMinoSwing.propConfig.getProperty("option.syncDisplay", true));
		chkboxShowLineClearEffect.setSelected(NullpoMinoSwing.propConfig.getProperty("option.showlineeffect", false));
	}

	/*
	 *  Called when button clicked
	 */
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand() == "GeneralConfig_OK") {
			// OK
			int screenSizeIndex = comboboxScreenSize.getSelectedIndex();
			if((screenSizeIndex >= 0) && (screenSizeIndex < SCREENSIZE_TABLE.length)) {
				NullpoMinoSwing.propConfig.setProperty("option.screenwidth", SCREENSIZE_TABLE[screenSizeIndex][0]);
				NullpoMinoSwing.propConfig.setProperty("option.screenheight", SCREENSIZE_TABLE[screenSizeIndex][1]);
			}

			int maxfps = NullpoMinoSwing.getIntTextField(60, txtfldMaxFPS);
			NullpoMinoSwing.propConfig.setProperty("option.maxfps", maxfps);

			double sevolume = NullpoMinoSwing.getDoubleTextField(1.0d, txtfldSEVolume);
			NullpoMinoSwing.propConfig.setProperty("option.sevolume", sevolume);

			int lineeffectspeed = NullpoMinoSwing.getIntTextField(0, txtfldLineClearEffectSpeed) - 1;
			if(lineeffectspeed < 0) lineeffectspeed = 0;
			NullpoMinoSwing.propConfig.setProperty("option.lineeffectspeed", lineeffectspeed);

			NullpoMinoSwing.propConfig.setProperty("option.showfps", chkboxShowFPS.isSelected());
			NullpoMinoSwing.propConfig.setProperty("option.showbg", chkboxShowBackground.isSelected());
			NullpoMinoSwing.propConfig.setProperty("option.showmeter", chkboxShowMeter.isSelected());
			NullpoMinoSwing.propConfig.setProperty("option.showfieldblockgraphics", chkboxShowFieldBlockGraphics.isSelected());
			NullpoMinoSwing.propConfig.setProperty("option.simpleblock", chkboxSimpleBlock.isSelected());
			NullpoMinoSwing.propConfig.setProperty("option.se", chkboxSE.isSelected());
			NullpoMinoSwing.propConfig.setProperty("option.usenativelookandfeel", chkboxUseNativeLookAndFeel.isSelected());
			NullpoMinoSwing.propConfig.setProperty("option.enableframestep", chkboxEnableFrameStep.isSelected());
			NullpoMinoSwing.propConfig.setProperty("option.nextshadow", chkboxNextShadow.isSelected());
			NullpoMinoSwing.propConfig.setProperty("option.outlineghost", chkboxOutlineGhost.isSelected());
			NullpoMinoSwing.propConfig.setProperty("option.sidenext", chkboxSideNext.isSelected());
			NullpoMinoSwing.propConfig.setProperty("option.bigsidenext", chkboxBigSideNext.isSelected());
			NullpoMinoSwing.propConfig.setProperty("option.darknextarea", chkboxDarkNextArea.isSelected());
			NullpoMinoSwing.propConfig.setProperty("option.showfieldbggrid", chkboxShowFieldBGGrid.isSelected());
			NullpoMinoSwing.propConfig.setProperty("option.perfectFPSMode", chkboxPerfectFPSMode.isSelected());
			NullpoMinoSwing.propConfig.setProperty("option.perfectYield", chkboxPerfectYield.isSelected());
			NullpoMinoSwing.propConfig.setProperty("option.syncDisplay", chkboxSyncDisplay.isSelected());
			NullpoMinoSwing.propConfig.setProperty("option.showlineeffect", chkboxShowLineClearEffect.isSelected());

			NullpoMinoSwing.saveConfig();
			ResourceHolderSwing.soundManager.setVolume(sevolume);
			if(chkboxShowBackground.isSelected()) {
				ResourceHolderSwing.loadBackgroundImages();
			}
			if(chkboxShowLineClearEffect.isSelected()) {
				ResourceHolderSwing.loadLineClearEffectImages();
			}
			this.setVisible(false);
		}
		else if(e.getActionCommand() == "GeneralConfig_Cancel") {
			// Cancel
			this.setVisible(false);
		}
	}
}
