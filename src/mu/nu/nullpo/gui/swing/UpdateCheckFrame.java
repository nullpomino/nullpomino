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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.gui.net.UpdateChecker;
import mu.nu.nullpo.gui.net.UpdateCheckerListener;

import org.apache.log4j.Logger;

import com.centerkey.utils.BareBonesBrowserLaunch;

/**
 * 更新 check 設定画面
 */
public class UpdateCheckFrame extends JFrame implements ActionListener, UpdateCheckerListener {
	/** Log */
	static Logger log = Logger.getLogger(UpdateCheckFrame.class);

	/** Serial version ID */
	private static final long serialVersionUID = 1L;

	/** 親ウィンドウ */
	protected NullpoMinoSwing owner;

	/** 状態ラベル */
	protected JLabel lStatus;

	/** 最新版のVersion number */
	protected JTextField txtfldLatestVersion;

	/** 最新版のリリース日 */
	protected JTextField txtfldReleaseDate;

	/** 最新版のダウンロードURL */
	protected JTextField txtfldDownloadURL;

	/** Windows Installer URL */
	protected JTextField txtfldWindowsInstallerURL;

	/** 今すぐ更新 check ボタン */
	protected JButton btnCheckNow;

	/** ブラウザでダウンロードボタン */
	protected JButton btnOpenDownloadURL;

	/** Installer download button */
	protected JButton btnOpenInstallerURL;

	/** 更新 check  is enabled */
	protected JCheckBox chkboxEnable;

	/** XMLのURL */
	protected JTextField txtfldXMLURL;

	/** この起動 countごとに更新 check */
	protected JTextField txtfldStartupMax;

	/**
	 * Constructor
	 * @param owner 親ウィンドウ
	 * @throws HeadlessException キーボード, マウス, ディスプレイなどが存在しない場合の例外
	 */
	public UpdateCheckFrame(NullpoMinoSwing owner) throws HeadlessException {
		super();
		this.owner = owner;

		// GUIのInitialization
		setTitle(NullpoMinoSwing.getUIText("Title_UpdateCheck"));
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setResizable(false);
		initUI();
		pack();
	}

	protected void initUI() {
		this.getContentPane().setLayout(new BorderLayout());

		// タブ
		JTabbedPane tabPane = new JTabbedPane();
		this.getContentPane().add(tabPane, BorderLayout.NORTH);

		// 情報パネル
		JPanel pUpdateInfo = new JPanel();
		pUpdateInfo.setAlignmentX(0f);
		pUpdateInfo.setLayout(new BoxLayout(pUpdateInfo, BoxLayout.Y_AXIS));
		tabPane.addTab(NullpoMinoSwing.getUIText("UpdateCheck_Tab_UpdateInfo"), pUpdateInfo);

		// * 状態ラベル
		lStatus = new JLabel(NullpoMinoSwing.getUIText("UpdateCheck_Label_Status_Ready"));
		lStatus.setAlignmentX(0f);
		pUpdateInfo.add(lStatus);

		// * Version number
		JPanel spLatestVersion = new JPanel(new BorderLayout());
		spLatestVersion.setAlignmentX(0f);
		pUpdateInfo.add(spLatestVersion);

		JLabel lLatestVersion = new JLabel(NullpoMinoSwing.getUIText("UpdateCheck_Label_LatestVersion"));
		spLatestVersion.add(lLatestVersion, BorderLayout.WEST);

		txtfldLatestVersion = new JTextField();
		txtfldLatestVersion.setPreferredSize(new Dimension(320, 20));
		txtfldLatestVersion.setEditable(false);
		spLatestVersion.add(txtfldLatestVersion, BorderLayout.EAST);

		// * リリース日
		JPanel spReleaseDate = new JPanel(new BorderLayout());
		spReleaseDate.setAlignmentX(0f);
		pUpdateInfo.add(spReleaseDate);

		JLabel lReleaseDate = new JLabel(NullpoMinoSwing.getUIText("UpdateCheck_Label_ReleaseDate"));
		spReleaseDate.add(lReleaseDate, BorderLayout.WEST);

		txtfldReleaseDate = new JTextField();
		txtfldReleaseDate.setPreferredSize(new Dimension(320, 20));
		txtfldReleaseDate.setEditable(false);
		spReleaseDate.add(txtfldReleaseDate, BorderLayout.EAST);

		// * ダウンロードURL
		JPanel spDownloadURL = new JPanel(new BorderLayout());
		spDownloadURL.setAlignmentX(0f);
		pUpdateInfo.add(spDownloadURL);

		JLabel lDownloadURL = new JLabel(NullpoMinoSwing.getUIText("UpdateCheck_Label_DownloadURL"));
		spDownloadURL.add(lDownloadURL, BorderLayout.WEST);

		txtfldDownloadURL = new JTextField();
		txtfldDownloadURL.setPreferredSize(new Dimension(320, 20));
		txtfldDownloadURL.setEditable(false);
		spDownloadURL.add(txtfldDownloadURL, BorderLayout.EAST);

		// * Installer URL
		JPanel spInstallerURL = new JPanel(new BorderLayout());
		spInstallerURL.setAlignmentX(0f);
		pUpdateInfo.add(spInstallerURL);

		JLabel lInstallerURL = new JLabel(NullpoMinoSwing.getUIText("UpdateCheck_Label_InstallerURL"));
		spInstallerURL.add(lInstallerURL, BorderLayout.WEST);

		txtfldWindowsInstallerURL = new JTextField();
		txtfldWindowsInstallerURL.setPreferredSize(new Dimension(320, 20));
		txtfldWindowsInstallerURL.setEditable(false);
		txtfldWindowsInstallerURL.setVisible(System.getProperty("os.name").startsWith("Windows"));
		spInstallerURL.add(txtfldWindowsInstallerURL, BorderLayout.EAST);

		// * 今すぐ check ボタン
		btnCheckNow = new JButton(NullpoMinoSwing.getUIText("UpdateCheck_Button_CheckNow"));
		btnCheckNow.setAlignmentX(0f);
		btnCheckNow.setMaximumSize(new Dimension(Short.MAX_VALUE, 20));
		btnCheckNow.setMnemonic('N');
		btnCheckNow.addActionListener(this);
		btnCheckNow.setActionCommand("CheckNow");
		pUpdateInfo.add(btnCheckNow);

		// * ブラウザでダウンロードボタン
		btnOpenDownloadURL = new JButton(NullpoMinoSwing.getUIText("UpdateCheck_Button_OpenDownloadURL"));
		btnOpenDownloadURL.setAlignmentX(0f);
		btnOpenDownloadURL.setMaximumSize(new Dimension(Short.MAX_VALUE, 20));
		btnOpenDownloadURL.setMnemonic('D');
		btnOpenDownloadURL.addActionListener(this);
		btnOpenDownloadURL.setActionCommand("OpenDownloadURL");
		btnOpenDownloadURL.setEnabled(false);
		//btnOpenDownloadURL.setVisible(Desktop.isDesktopSupported());
		pUpdateInfo.add(btnOpenDownloadURL);

		// * Installer Download
		btnOpenInstallerURL = new JButton(NullpoMinoSwing.getUIText("UpdateCheck_Button_OpenInstallerURL"));
		btnOpenInstallerURL.setAlignmentX(0f);
		btnOpenInstallerURL.setMaximumSize(new Dimension(Short.MAX_VALUE, 20));
		btnOpenInstallerURL.setMnemonic('I');
		btnOpenInstallerURL.addActionListener(this);
		btnOpenInstallerURL.setActionCommand("OpenInstallerURL");
		btnOpenInstallerURL.setEnabled(false);
		btnOpenInstallerURL.setVisible(System.getProperty("os.name").startsWith("Windows"));
		pUpdateInfo.add(btnOpenInstallerURL);

		// 設定パネル
		JPanel pSetting = new JPanel(new BorderLayout());
		pSetting.setAlignmentX(0f);
		tabPane.addTab(NullpoMinoSwing.getUIText("UpdateCheck_Tab_Setting"), pSetting);

		// * そのままだと縦に引き伸ばされてしまうのでもう1枚パネルを使う
		JPanel spSetting = new JPanel();
		spSetting.setAlignmentX(0f);
		spSetting.setLayout(new BoxLayout(spSetting, BoxLayout.Y_AXIS));
		pSetting.add(spSetting, BorderLayout.NORTH);

		// * 更新 check  is enabled
		chkboxEnable = new JCheckBox(NullpoMinoSwing.getUIText("UpdateCheck_CheckBox_Enable"));
		chkboxEnable.setAlignmentX(0f);
		chkboxEnable.setMnemonic('E');
		spSetting.add(chkboxEnable);

		// * XMLのURL
		JPanel spXMLURL = new JPanel(new BorderLayout());
		spXMLURL.setAlignmentX(0f);
		spSetting.add(spXMLURL);

		JLabel lXMLURL = new JLabel(NullpoMinoSwing.getUIText("UpdateCheck_Label_XMLURL"));
		spXMLURL.add(lXMLURL, BorderLayout.WEST);

		txtfldXMLURL = new JTextField();
		txtfldXMLURL.setPreferredSize(new Dimension(220, 20));
		spXMLURL.add(txtfldXMLURL, BorderLayout.EAST);

		// * この起動 countごとに更新 check
		JPanel spStartupMax = new JPanel(new BorderLayout());
		spStartupMax.setAlignmentX(0f);
		spSetting.add(spStartupMax);

		JLabel lStartupMax = new JLabel(NullpoMinoSwing.getUIText("UpdateCheck_Label_StartupMax"));
		spStartupMax.add(lStartupMax, BorderLayout.WEST);

		txtfldStartupMax = new JTextField();
		txtfldStartupMax.setPreferredSize(new Dimension(220, 20));
		spStartupMax.add(txtfldStartupMax, BorderLayout.EAST);

		// * 保存ボタン
		JButton btnSave = new JButton(NullpoMinoSwing.getUIText("UpdateCheck_Button_Save"));
		btnSave.setAlignmentX(0f);
		btnSave.setMaximumSize(new Dimension(Short.MAX_VALUE, 20));
		btnSave.setMnemonic('S');
		btnSave.addActionListener(this);
		btnSave.setActionCommand("Save");
		spSetting.add(btnSave);

		// 閉じるボタン
		JButton btnClose = new JButton(NullpoMinoSwing.getUIText("UpdateCheck_Button_Close"));
		btnClose.setAlignmentX(0f);
		btnClose.setMnemonic('C');
		btnClose.addActionListener(this);
		btnClose.setActionCommand("Close");
		this.getContentPane().add(btnClose, BorderLayout.SOUTH);
	}

	/**
	 * Current 設定をGUIに反映させる
	 */
	public void load() {
		txtfldLatestVersion.setForeground(Color.black);
		if(UpdateChecker.isCompleted()) {
			txtfldLatestVersion.setText(UpdateChecker.getLatestVersionFullString());
			txtfldReleaseDate.setText(UpdateChecker.getStrReleaseDate());
			txtfldDownloadURL.setText(UpdateChecker.getStrDownloadURL());

			if(UpdateChecker.isNewVersionAvailable(GameManager.getVersionMajor(), GameManager.getVersionMinor())) {
				txtfldLatestVersion.setForeground(Color.red);
			}
			btnOpenDownloadURL.setEnabled(true);
		}
		chkboxEnable.setSelected(NullpoMinoSwing.propGlobal.getProperty("updatechecker.enable", true));
		txtfldXMLURL.setText(NullpoMinoSwing.propGlobal.getProperty("updatechecker.url", ""));
		txtfldStartupMax.setText(NullpoMinoSwing.propGlobal.getProperty("updatechecker.startupMax", "20"));
	}

	/*
	 * ボタンクリック時の処理
	 */
	public void actionPerformed(ActionEvent e) {
		// 今すぐ更新 check
		if(e.getActionCommand() == "CheckNow") {
			if(!UpdateChecker.isRunning()) {
				txtfldLatestVersion.setForeground(Color.black);
				UpdateChecker.addListener(this);
				UpdateChecker.startCheckForUpdates(txtfldXMLURL.getText());
				btnCheckNow.setEnabled(false);
			}
		}
		// ブラウザでダウンロード
		else if(e.getActionCommand() == "OpenDownloadURL") {
			BareBonesBrowserLaunch.openURL(txtfldDownloadURL.getText());
		}
		// Installer Download
		else if(e.getActionCommand() == "OpenInstallerURL") {
			BareBonesBrowserLaunch.openURL(txtfldWindowsInstallerURL.getText());
		}
		// 保存
		else if(e.getActionCommand() == "Save") {
			NullpoMinoSwing.propGlobal.setProperty("updatechecker.enable", chkboxEnable.isSelected());
			NullpoMinoSwing.propGlobal.setProperty("updatechecker.url", txtfldXMLURL.getText());
			NullpoMinoSwing.propGlobal.setProperty("updatechecker.startupMax", NullpoMinoSwing.getIntTextField(20, txtfldStartupMax));
			NullpoMinoSwing.saveConfig();
		}
		// 閉じる
		else if(e.getActionCommand() == "Close") {
			this.setVisible(false);
		}
	}

	public void onUpdateCheckerStart() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				lStatus.setText(NullpoMinoSwing.getUIText("UpdateCheck_Label_Status_Checking"));
			}
		});
	}

	public void onUpdateCheckerEnd(int status) {
		btnCheckNow.setEnabled(true);

		if(status == UpdateChecker.STATUS_ERROR) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					lStatus.setText(NullpoMinoSwing.getUIText("UpdateCheck_Label_Status_Failed"));
				}
			});
		} else if(status == UpdateChecker.STATUS_COMPLETE) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					String strURL = UpdateChecker.getStrDownloadURL();
					String strInstaller = UpdateChecker.getStrWindowsInstallerURL();

					lStatus.setText(NullpoMinoSwing.getUIText("UpdateCheck_Label_Status_Complete"));
					txtfldLatestVersion.setText(UpdateChecker.getLatestVersionFullString());
					txtfldReleaseDate.setText(UpdateChecker.getStrReleaseDate());
					txtfldDownloadURL.setText(strURL);
					txtfldWindowsInstallerURL.setText(strInstaller);

					if(UpdateChecker.isNewVersionAvailable(GameManager.getVersionMajor(), GameManager.getVersionMinor())) {
						txtfldLatestVersion.setForeground(Color.red);
						txtfldWindowsInstallerURL.setForeground(Color.red);
					}

					btnOpenDownloadURL.setEnabled(((strURL != null) && (strURL.length() > 0)));
					btnOpenInstallerURL.setEnabled(((strInstaller != null) && (strInstaller.length() > 0)));
				}
			});
		}
	}
}
