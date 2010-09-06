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
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * 設定画面の frame
 */
public class GeneralConfigFrame extends JFrame implements ActionListener {
	/** Serial version ID */
	private static final long serialVersionUID = 1L;

	/** 親ウィンドウ */
	protected NullpoMinoSwing owner;

	/** MaximumFPS */
	protected JTextField txtfldMaxFPS;

	/** Sound effectsの音量 */
	protected JTextField txtfldSEVolume;

	/** FPS表示 */
	protected JCheckBox chkboxShowFPS;

	/** Background表示 */
	protected JCheckBox chkboxShowBackground;

	/** Meter表示 */
	protected JCheckBox chkboxShowMeter;

	/** フィールドのBlockの絵を表示 ( check なしの場合は枠線だけ) */
	protected JCheckBox chkboxShowFieldBlockGraphics;

	/** シンプルな絵柄のBlockを使う */
	protected JCheckBox chkboxSimpleBlock;

	/** Sound effects */
	protected JCheckBox chkboxSE;

	/** ネイティブのLook and Feelを使う */
	protected JCheckBox chkboxUseNativeLookAndFeel;

	/**  frame ステップ */
	protected JCheckBox chkboxEnableFrameStep;

	/** ゴーストピースの上にNEXT表示 */
	protected JCheckBox chkboxNextShadow;

	/** 枠線型ゴーストピース */
	protected JCheckBox chkboxOutlineGhost;

	/** Side piece preview */
	protected JCheckBox chkboxSideNext;

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
		this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

		// ---------- MaximumFPS ----------
		JPanel pMaxFPS = new JPanel();
		pMaxFPS.setAlignmentX(LEFT_ALIGNMENT);
		this.add(pMaxFPS);

		JLabel lMaxFPS = new JLabel(NullpoMinoSwing.getUIText("GeneralConfig_MaxFPS"));
		pMaxFPS.add(lMaxFPS);

		txtfldMaxFPS = new JTextField(5);
		pMaxFPS.add(txtfldMaxFPS);

		// ---------- Sound effectsの音量 ----------
		JPanel pSEVolume = new JPanel();
		pSEVolume.setAlignmentX(LEFT_ALIGNMENT);
		this.add(pSEVolume);

		JLabel lSEVolume = new JLabel(NullpoMinoSwing.getUIText("GeneralConfig_SEVolume"));
		pSEVolume.add(lSEVolume);

		txtfldSEVolume = new JTextField(5);
		pSEVolume.add(txtfldSEVolume);

		// ---------- checkボックス ----------
		chkboxShowFPS = new JCheckBox(NullpoMinoSwing.getUIText("GeneralConfig_ShowFPS"));
		chkboxShowFPS.setAlignmentX(LEFT_ALIGNMENT);
		this.add(chkboxShowFPS);

		chkboxShowBackground = new JCheckBox(NullpoMinoSwing.getUIText("GeneralConfig_ShowBackground"));
		chkboxShowBackground.setAlignmentX(LEFT_ALIGNMENT);
		this.add(chkboxShowBackground);

		chkboxShowMeter = new JCheckBox(NullpoMinoSwing.getUIText("GeneralConfig_ShowMeter"));
		chkboxShowMeter.setAlignmentX(LEFT_ALIGNMENT);
		this.add(chkboxShowMeter);

		chkboxShowFieldBlockGraphics = new JCheckBox(NullpoMinoSwing.getUIText("GeneralConfig_ShowFieldBlockGraphics"));
		chkboxShowFieldBlockGraphics.setAlignmentX(LEFT_ALIGNMENT);
		this.add(chkboxShowFieldBlockGraphics);

		chkboxSimpleBlock = new JCheckBox(NullpoMinoSwing.getUIText("GeneralConfig_SimpleBlock"));
		chkboxSimpleBlock.setAlignmentX(LEFT_ALIGNMENT);
		this.add(chkboxSimpleBlock);

		chkboxSE = new JCheckBox(NullpoMinoSwing.getUIText("GeneralConfig_SE"));
		chkboxSE.setAlignmentX(LEFT_ALIGNMENT);
		this.add(chkboxSE);

		chkboxUseNativeLookAndFeel = new JCheckBox(NullpoMinoSwing.getUIText("GeneralConfig_UseNativeLookAndFeel"));
		chkboxUseNativeLookAndFeel.setAlignmentX(LEFT_ALIGNMENT);
		this.add(chkboxUseNativeLookAndFeel);

		chkboxEnableFrameStep = new JCheckBox(NullpoMinoSwing.getUIText("GeneralConfig_EnableFrameStep"));
		chkboxEnableFrameStep.setAlignmentX(LEFT_ALIGNMENT);
		this.add(chkboxEnableFrameStep);

		chkboxNextShadow = new JCheckBox(NullpoMinoSwing.getUIText("GeneralConfig_NextShadow"));
		chkboxNextShadow.setAlignmentX(LEFT_ALIGNMENT);
		this.add(chkboxNextShadow);

		chkboxOutlineGhost = new JCheckBox(NullpoMinoSwing.getUIText("GeneralConfig_OutlineGhost"));
		chkboxOutlineGhost.setAlignmentX(LEFT_ALIGNMENT);
		this.add(chkboxOutlineGhost);

		chkboxSideNext = new JCheckBox(NullpoMinoSwing.getUIText("GeneralConfig_SideNext"));
		chkboxSideNext.setAlignmentX(LEFT_ALIGNMENT);
		this.add(chkboxSideNext);

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
		txtfldMaxFPS.setText(String.valueOf(NullpoMinoSwing.propConfig.getProperty("option.maxfps", 60)));
		txtfldSEVolume.setText(String.valueOf(NullpoMinoSwing.propConfig.getProperty("option.sevolume", 1.0d)));
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
	}

	/*
	 *  Called when button clicked
	 */
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand() == "GeneralConfig_OK") {
			// OK
			int maxfps = NullpoMinoSwing.getIntTextField(60, txtfldMaxFPS);
			NullpoMinoSwing.propConfig.setProperty("option.maxfps", maxfps);

			double sevolume = NullpoMinoSwing.getDoubleTextField(1.0d, txtfldSEVolume);
			NullpoMinoSwing.propConfig.setProperty("option.sevolume", sevolume);

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

			NullpoMinoSwing.saveConfig();
			ResourceHolderSwing.soundManager.setVolume(sevolume);
			if(chkboxShowBackground.isSelected()) {
				ResourceHolderSwing.loadBackgroundImages();
			}

			this.setVisible(false);
		}
		else if(e.getActionCommand() == "GeneralConfig_Cancel") {
			// Cancel
			this.setVisible(false);
		}
	}
}
