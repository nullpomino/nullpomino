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
package mu.nu.nullpo.tool.musiclisteditor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.UIManager;

import mu.nu.nullpo.game.component.BGMStatus;
import mu.nu.nullpo.util.CustomProperties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * MusicListEditor (音楽リスト編集ツール)
 */
public class MusicListEditor extends JFrame implements ActionListener {
	/** Serial version ID */
	private static final long serialVersionUID = -6480034324392568869L;

	/** Log */
	static final Logger log = Logger.getLogger(MusicListEditor.class);

	/** Swing版の設定保存用Property file */
	private CustomProperties propConfig;

	/** Default language file */
	private CustomProperties propLangDefault;

	/** UI翻訳用Property file */
	private CustomProperties propLang;

	/** 音楽リストが含まれるProperty file */
	private CustomProperties propMusic;

	/** 音楽のファイル名用テキストボックス */
	private JTextField[] txtfldMusicFileNames;

	/** ループなし check ボックス */
	private JCheckBox[] chkboxNoLoop;

	/** ファイル選択ダイアログ */
	private JFileChooser fileChooser;

	/** ファイルフィルタのHashMap */
	private HashMap<String, SimpleFileFilter> hashmapFileFilters;

	/**
	 * Constructor
	 */
	public MusicListEditor() {
		super();
		init();
		setVisible(true);
	}

	/**
	 * Initialization
	 */
	private void init() {
		// 設定ファイル読み込み
		propConfig = new CustomProperties();
		try {
			FileInputStream in = new FileInputStream("config/setting/swing.cfg");
			propConfig.load(in);
			in.close();
		} catch(IOException e) {}

		// 言語ファイル読み込み
		propLangDefault = new CustomProperties();
		try {
			FileInputStream in = new FileInputStream("config/lang/musiclisteditor_default.properties");
			propLangDefault.load(in);
			in.close();
		} catch (IOException e) {
			log.error("Couldn't load default UI language file", e);
		}

		propLang = new CustomProperties();
		try {
			FileInputStream in = new FileInputStream("config/lang/musiclisteditor_" + Locale.getDefault().getCountry() + ".properties");
			propLang.load(in);
			in.close();
		} catch(IOException e) {}

		// 音楽リスト読み込み
		loadMusicList();

		// Look&Feel設定
		if(propConfig.getProperty("option.usenativelookandfeel", true) == true) {
			try {
				UIManager.getInstalledLookAndFeels();
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch(Exception e) {
				log.warn("Failed to set native look&feel", e);
			}
		}

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle(getUIText("Title_MusicListEditor"));

		initUI();
		pack();
	}

	/**
	 * 画面のInitialization
	 */
	private void initUI() {
		this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

		// メイン画面
		JPanel pMusicSetting = new JPanel();
		pMusicSetting.setLayout(new BoxLayout(pMusicSetting, BoxLayout.Y_AXIS));
		pMusicSetting.setAlignmentX(LEFT_ALIGNMENT);
		this.add(pMusicSetting);

		txtfldMusicFileNames = new JTextField[BGMStatus.BGM_COUNT];
		chkboxNoLoop = new JCheckBox[BGMStatus.BGM_COUNT];

		for(int i = 0; i < BGMStatus.BGM_COUNT; i++) {
			JPanel pMusicTemp = new JPanel(new BorderLayout());
			pMusicSetting.add(pMusicTemp);

			JPanel pMusicTempLabels = new JPanel(new BorderLayout());
			pMusicTemp.add(pMusicTempLabels, BorderLayout.WEST);
			pMusicTempLabels.add(new JLabel(getUIText("MusicListEditor_LabelMusic" + i)), BorderLayout.WEST);

			JPanel pMusicTempTexts = new JPanel(new BorderLayout());
			pMusicTemp.add(pMusicTempTexts, BorderLayout.EAST);

			txtfldMusicFileNames[i] = new JTextField(45);
			txtfldMusicFileNames[i].setComponentPopupMenu(new TextFieldPopupMenu(txtfldMusicFileNames[i]));
			txtfldMusicFileNames[i].setText(propMusic.getProperty("music.filename." + i, ""));
			pMusicTempTexts.add(txtfldMusicFileNames[i], BorderLayout.CENTER);

			JPanel pMusicTempTextsButtons = new JPanel(new BorderLayout());
			pMusicTempTexts.add(pMusicTempTextsButtons, BorderLayout.EAST);

			chkboxNoLoop[i] = new JCheckBox();
			chkboxNoLoop[i].setToolTipText(getUIText("MusicListEditor_NoLoop_Tip"));
			chkboxNoLoop[i].setSelected(propMusic.getProperty("music.noloop." + i, false));
			pMusicTempTextsButtons.add(chkboxNoLoop[i], BorderLayout.WEST);

			JButton btnClear = new JButton(getUIText("MusicListEditor_Clear"));
			btnClear.setToolTipText(getUIText("MusicListEditor_Clear_Tip"));
			btnClear.setActionCommand("Clear" + i);
			btnClear.addActionListener(this);
			pMusicTempTextsButtons.add(btnClear, BorderLayout.CENTER);

			JButton btnOpen = new JButton(getUIText("MusicListEditor_OpenFileDialog"));
			btnOpen.setToolTipText(getUIText("MusicListEditor_OpenFileDialog_Tip"));
			btnOpen.setActionCommand("OpenFileDialog" + i);
			btnOpen.addActionListener(this);
			pMusicTempTextsButtons.add(btnOpen, BorderLayout.EAST);
		}

		// 画面下の button類
		JPanel pButtons = new JPanel();
		pButtons.setLayout(new BoxLayout(pButtons, BoxLayout.X_AXIS));
		pButtons.setAlignmentX(LEFT_ALIGNMENT);
		this.add(pButtons);

		JButton btnOK = new JButton(getUIText("MusicListEditor_OK"));
		btnOK.addActionListener(this);
		btnOK.setActionCommand("OK");
		btnOK.setMaximumSize(new Dimension(Short.MAX_VALUE, 30));
		btnOK.setMnemonic('O');
		pButtons.add(btnOK);

		JButton btnCancel = new JButton(getUIText("MusicListEditor_Cancel"));
		btnCancel.addActionListener(this);
		btnCancel.setActionCommand("Cancel");
		btnCancel.setMaximumSize(new Dimension(Short.MAX_VALUE, 30));
		btnCancel.setMnemonic('C');
		pButtons.add(btnCancel);

		// ファイルフィルタ
		hashmapFileFilters = new HashMap<String, SimpleFileFilter>();
		hashmapFileFilters.put(".wav", new SimpleFileFilter(".wav", getUIText("FileChooser_wav")));
		hashmapFileFilters.put(".xm", new SimpleFileFilter(".xm", getUIText("FileChooser_xm")));
		hashmapFileFilters.put(".mod", new SimpleFileFilter(".mod", getUIText("FileChooser_mod")));
		hashmapFileFilters.put(".aif", new SimpleFileFilter(".aif", getUIText("FileChooser_aif")));
		hashmapFileFilters.put(".aiff", new SimpleFileFilter(".aif", getUIText("FileChooser_aiff")));
		hashmapFileFilters.put(".ogg", new SimpleFileFilter(".ogg", getUIText("FileChooser_ogg")));

		// ファイル選択ダイアログ
		fileChooser = new JFileChooser();

		Iterator<SimpleFileFilter> it = hashmapFileFilters.values().iterator();
		while(it.hasNext()) {
			SimpleFileFilter filter = it.next();
			fileChooser.addChoosableFileFilter(filter);
		}
	}

	/**
	 * 翻訳後のUIの文字列を取得
	 * @param str 文字列
	 * @return 翻訳後のUIの文字列 (無いならそのままstrを返す）
	 */
	private String getUIText(String str) {
		String result = propLang.getProperty(str);
		if(result == null) {
			result = propLangDefault.getProperty(str, str);
		}
		return result;
	}

	/**
	 * 音楽リスト読み込み
	 */
	private void loadMusicList() {
		propMusic = new CustomProperties();
		try {
			FileInputStream in = new FileInputStream("config/setting/music.cfg");
			propMusic.load(in);
			in.close();
		} catch (IOException e) {}
	}

	/**
	 * 音楽リストを保存
	 * @throws IOException 保存失敗
	 */
	private void saveMusicList() throws IOException {
		try {
			FileOutputStream out = new FileOutputStream("config/setting/music.cfg");
			propMusic.store(out, "NullpoMino Music List");
			out.close();
		} catch (IOException e) {
			log.error("Failed to save music list file", e);
			throw e;
		}
	}

	/*
	 * メニュー実行時の処理
	 */
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().startsWith("OpenFileDialog")) {
			// Button number取得
			int number = 0;
			try {
				String strNum = e.getActionCommand().replaceFirst("OpenFileDialog", "");
				number = Integer.parseInt(strNum);
			} catch (Exception e2) {
				log.error("OpenFileDialog: Failed to get button number", e2);
				return;
			}

			// カレントディレクトリ
			String currentDirectory = System.getProperty("user.dir");

			//  default ディレクトリを設定
			String defaultDirectory = txtfldMusicFileNames[number].getText();
			if(defaultDirectory.length() < 1) defaultDirectory = currentDirectory + "/res/bgm";

			File file = new File(defaultDirectory);
			fileChooser.setCurrentDirectory(file);

			// ファイル選択ダイアログの default 拡張子を設定
			if(file.isFile()) {
				try {
					String strName = file.getName();
					int lastPeriod = strName.lastIndexOf('.');
					if(lastPeriod != -1) {
						String strExt = strName.substring(lastPeriod, strName.length());
						fileChooser.setFileFilter(hashmapFileFilters.get(strExt));
					}
				} catch (Exception e2) {}
			}

			// ファイル選択ダイアログを表示
			if(fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				String strPath = fileChooser.getSelectedFile().getPath();
				txtfldMusicFileNames[number].setText(strPath);
			}
		}
		else if(e.getActionCommand().startsWith("Clear")) {
			int number = 0;
			try {
				String strNum = e.getActionCommand().replaceFirst("Clear", "");
				number = Integer.parseInt(strNum);
			} catch (Exception e2) {
				log.error("Clear: Failed to get button number", e2);
				return;
			}

			txtfldMusicFileNames[number].setText("");
		}
		else if(e.getActionCommand() == "OK") {
			for(int i = 0; i < txtfldMusicFileNames.length; i++) {
				propMusic.setProperty("music.filename." + i, txtfldMusicFileNames[i].getText());
				propMusic.setProperty("music.noloop." + i, chkboxNoLoop[i].isSelected());
			}

			try {
				saveMusicList();
			} catch (IOException e2) {
				JOptionPane.showMessageDialog(this, getUIText("Message_FileSaveFailed") + "\n" + e2.getLocalizedMessage(),
											  getUIText("Title_FileSaveFailed"), JOptionPane.ERROR_MESSAGE);
			}

			this.dispose();
		}
		else if(e.getActionCommand() == "Cancel") {
			this.dispose();
		}
	}

	/**
	 * メイン関count
	 * @param args コマンドLines引count
	 */
	public static void main(String[] args) {
		PropertyConfigurator.configure("config/etc/log.cfg");
		log.debug("MusicListEditor start");
		new MusicListEditor();
	}

	/**
	 * ポップアップメニュー
	 * <a href="http://terai.xrea.jp/Swing/DefaultEditorKit.html">出展</a>
	 */
	private class TextFieldPopupMenu extends JPopupMenu {
		private static final long serialVersionUID = 1L;

		private Action cutAction;
		private Action copyAction;
		@SuppressWarnings("unused")
		private Action pasteAction;
		private Action deleteAction;
		private Action selectAllAction;

		public TextFieldPopupMenu(final JTextField field) {
			super();

			add(cutAction = new AbstractAction(getUIText("Popup_Cut")) {
				private static final long serialVersionUID = 1L;
				public void actionPerformed(ActionEvent evt) {
					field.cut();
				}
			});
			add(copyAction = new AbstractAction(getUIText("Popup_Copy")) {
				private static final long serialVersionUID = 1L;
				public void actionPerformed(ActionEvent evt) {
					field.copy();
				}
			});
			add(pasteAction = new AbstractAction(getUIText("Popup_Paste")) {
				private static final long serialVersionUID = 1L;
				public void actionPerformed(ActionEvent evt) {
					field.paste();
				}
			});
			add(deleteAction = new AbstractAction(getUIText("Popup_Delete")) {
				private static final long serialVersionUID = 1L;
				public void actionPerformed(ActionEvent evt) {
					field.replaceSelection(null);
				}
			});
			add(selectAllAction = new AbstractAction(getUIText("Popup_SelectAll")) {
				private static final long serialVersionUID = 1L;
				public void actionPerformed(ActionEvent evt) {
					field.selectAll();
				}
			});
		}

		@Override
		public void show(Component c, int x, int y) {
			JTextField field = (JTextField) c;
			boolean flg = field.getSelectedText() != null;
			cutAction.setEnabled(flg);
			copyAction.setEnabled(flg);
			deleteAction.setEnabled(flg);
			selectAllAction.setEnabled(field.isFocusOwner());
			super.show(c, x, y);
		}
	}
}
