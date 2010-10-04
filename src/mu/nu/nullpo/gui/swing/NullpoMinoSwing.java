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
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import mu.nu.nullpo.game.component.RuleOptions;
import mu.nu.nullpo.game.net.NetObserverClient;
import mu.nu.nullpo.game.net.NetPlayerClient;
import mu.nu.nullpo.game.net.NetRoomInfo;
import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.game.subsystem.ai.DummyAI;
import mu.nu.nullpo.game.subsystem.mode.GameMode;
import mu.nu.nullpo.game.subsystem.mode.NetDummyMode;
import mu.nu.nullpo.game.subsystem.wallkick.Wallkick;
import mu.nu.nullpo.gui.net.NetLobbyFrame;
import mu.nu.nullpo.gui.net.NetLobbyListener;
import mu.nu.nullpo.gui.net.UpdateChecker;
import mu.nu.nullpo.gui.net.UpdateCheckerListener;
import mu.nu.nullpo.util.CustomProperties;
import mu.nu.nullpo.util.GeneralUtil;
import mu.nu.nullpo.util.ModeManager;
import net.omegaboshi.nullpomino.game.subsystem.randomizer.Randomizer;

/**
 * NullpoMino SwingVersion
 */
public class NullpoMinoSwing extends JFrame implements ActionListener, NetLobbyListener, UpdateCheckerListener {
	/** Serial version ID */
	private static final long serialVersionUID = 1L;

	/** Log */
	static Logger log = Logger.getLogger(NullpoMinoSwing.class);

	/** メインウィンドウの frame */
	public static NullpoMinoSwing mainFrame;

	/** ゲームウィンドウの frame */
	public static GameFrame gameFrame;

	/** キーコンフィグ画面の frame */
	public static KeyConfigFrame keyConfigFrame;

	/** ルール選択画面の frame */
	public static RuleSelectFrame ruleSelectFrame;

	/** AI選択画面の frame */
	public static AISelectFrame aiSelectFrame;

	/** その他の設定画面の frame */
	public static GeneralConfigFrame generalConfigFrame;

	/** チューニング設定画面の frame */
	public static GameTuningFrame gameTuningFrame;

	/** 更新 check 設定画面の frame */
	public static UpdateCheckFrame updateCheckFrame;

	/** プログラムに渡されたコマンドLines引count */
	public static String[] programArgs;

	/** Save settings用Property file */
	public static CustomProperties propConfig;

	/** Save settings用Property file (全Version共通) */
	public static CustomProperties propGlobal;

	/** Observer機能用Property file */
	public static CustomProperties propObserver;

	/** Default language file */
	public static CustomProperties propLangDefault;

	/** 言語ファイル */
	public static CustomProperties propLang;

	/** Default game mode description file */
	public static CustomProperties propDefaultModeDesc;

	/** Game mode description file */
	public static CustomProperties propModeDesc;

	/** Mode 管理 */
	public static ModeManager modeManager;

	/** メイン画面のレイアウトマネージャ */
	public static CardLayout mainLayout;

	/** ゲームのメインクラス */
	public static GameManager gameManager;

	/** ゲームの event 処理と描画処理 */
	public static RendererSwing rendererSwing;

	/** ゲームMode nameの配列 */
	public static String[] modeList;

	/** Mode 選択リストボックス */
	public static JList listboxMode;

	/** リプレイファイル選択ダイアログ */
	public static JFileChooser replayFileChooser;

	/** ロビー画面 */
	public static NetLobbyFrame netLobby;

	/** Observerクライアント */
	public static NetObserverClient netObserverClient;

	/** Mode セレクト画面のラベル(新Versionがある場合は切り替わる) */
	public static JLabel lModeSelect;

	/**
	 * メイン関count
	 * @param args プログラムに渡されたコマンドLines引count
	 */
	public static void main(String[] args) {
		programArgs = args;

		PropertyConfigurator.configure("config/etc/log_swing.cfg");
		log.debug("NullpoMinoSwing Start");

		// 設定ファイル読み込み
		propConfig = new CustomProperties();
		try {
			FileInputStream in = new FileInputStream("config/setting/swing.cfg");
			propConfig.load(in);
			in.close();
		} catch(IOException e) {}

		propGlobal = new CustomProperties();
		try {
			FileInputStream in = new FileInputStream("config/setting/global.cfg");
			propGlobal.load(in);
			in.close();
		} catch(IOException e) {}

		// Mode読み込み
		modeManager = new ModeManager();
		try {
			BufferedReader txtMode = new BufferedReader(new FileReader("config/list/mode.lst"));
			modeManager.loadGameModes(txtMode);
			txtMode.close();
			modeList = modeManager.getModeNames(false);
		} catch (IOException e) {
			log.error("Mode list load failed", e);
		}

		// 言語ファイル読み込み
		propLangDefault = new CustomProperties();
		try {
			FileInputStream in = new FileInputStream("config/lang/swing_default.properties");
			propLangDefault.load(in);
			in.close();
		} catch (IOException e) {
			log.error("Couldn't load default UI language file", e);
		}

		propLang = new CustomProperties();
		try {
			FileInputStream in = new FileInputStream("config/lang/swing_" + Locale.getDefault().getCountry() + ".properties");
			propLang.load(in);
			in.close();
		} catch(IOException e) {}

		// Game mode description
		propDefaultModeDesc = new CustomProperties();
		try {
			FileInputStream in = new FileInputStream("config/lang/modedesc_default.properties");
			propDefaultModeDesc.load(in);
			in.close();
		} catch(IOException e) {
			log.error("Couldn't load default mode description file", e);
		}

		propModeDesc = new CustomProperties();
		try {
			FileInputStream in = new FileInputStream("config/lang/modedesc_" + Locale.getDefault().getCountry() + ".properties");
			propModeDesc.load(in);
			in.close();
		} catch(IOException e) {}

		// Load keyboard settings
		GameKeySwing.initGlobalGameKeySwing();
		GameKeySwing.gamekey[0].loadConfig(propConfig);
		GameKeySwing.gamekey[1].loadConfig(propConfig);

		// Look&Feel
		if(propConfig.getProperty("option.usenativelookandfeel", true) == true) {
			try {
				UIManager.getInstalledLookAndFeels();
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch(Exception e) {
				log.warn("Failed to set native look&feel", e);
			}
		}

		// Load images
		try {
			ResourceHolderSwing.load();
		} catch (Exception e) {
			log.error("Image load failed", e);
		}

		// First run?
		if(propConfig.getProperty("option.firstSetupMode", true) == true) {
			// Set various default settings here
			GameKeySwing.gamekey[0].loadDefaultKeymap();
			GameKeySwing.gamekey[0].saveConfig(propConfig);
			propConfig.setProperty("option.firstSetupMode", false);

			// Save settings
			saveConfig();
		}

		// Create and display main window
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				mainFrame = new NullpoMinoSwing();
			}
		});
	}

	/**
	 * 翻訳後のUIの文字列を取得
	 * @param str 文字列
	 * @return 翻訳後のUIの文字列 (無いならそのままstrを返す）
	 */
	public static String getUIText(String str) {
		String result = propLang.getProperty(str);
		if(result == null) {
			result = propLangDefault.getProperty(str, str);
		}
		return result;
	}

	/**
	 * 設定ファイルを保存
	 */
	public static void saveConfig() {
		try {
			FileOutputStream out = new FileOutputStream("config/setting/swing.cfg");
			propConfig.store(out, "NullpoMino Swing-frontend Config");
			out.close();
		} catch(IOException e) {
			log.error("Failed to save Swing-specific config", e);
		}

		try {
			FileOutputStream out = new FileOutputStream("config/setting/global.cfg");
			propGlobal.store(out, "NullpoMino Global Config");
			out.close();
		} catch(IOException e) {
			log.error("Failed to save global config", e);
		}
	}

	/**
	 * テキストfieldからint型の値を取得
	 * @param value テキストfieldから値の取得に失敗したときの値
	 * @param txtfld テキストfield
	 * @return テキストfieldから値を取得できた場合はその値, 失敗したらvalueをそのまま返す
	 */
	public static int getIntTextField(int value, JTextField txtfld) {
		int v = value;

		try {
			v = Integer.parseInt(txtfld.getText());
		} catch(NumberFormatException e) {}

		return v;
	}

	/**
	 * テキストfieldからdouble型の値を取得
	 * @param value テキストfieldから値の取得に失敗したときの値
	 * @param txtfld テキストfield
	 * @return テキストfieldから値を取得できた場合はその値, 失敗したらvalueをそのまま返す
	 */
	public static double getDoubleTextField(double value, JTextField txtfld) {
		double v = value;

		try {
			v = Double.parseDouble(txtfld.getText());
		} catch(NumberFormatException e) {}

		return v;
	}

	/**
	 * テキストfieldからfloat型の値を取得
	 * @param value テキストfieldから値の取得に失敗したときの値
	 * @param txtfld テキストfield
	 * @return テキストfieldから値を取得できた場合はその値, 失敗したらvalueをそのまま返す
	 */
	public static float getFloatTextField(float value, JTextField txtfld) {
		float v = value;

		try {
			v = Float.parseFloat(txtfld.getText());
		} catch(NumberFormatException e) {}

		return v;
	}

	/**
	 * Get game mode description
	 * @param str Mode name
	 * @return Description
	 */
	protected static String getModeDesc(String str) {
		String str2 = str.replace(' ', '_');
		str2 = str2.replace('(', 'l');
		str2 = str2.replace(')', 'r');
		String result = propModeDesc.getProperty(str2);
		if(result == null) {
			result = propDefaultModeDesc.getProperty(str2, str2);
		}
		return result;
	}

	/**
	 * Constructor
	 * @throws HeadlessException キーボード, マウス, ディスプレイなどが存在しない場合の例外
	 */
	public NullpoMinoSwing() throws HeadlessException {
		super();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle(getUIText("Title_Main") + " version" + GameManager.getVersionString());

		initUI();
		pack();

		setVisible(true);

		// 新Version check
		if(propGlobal.getProperty("updatechecker.enable", true)) {
			int startupCount = propGlobal.getProperty("updatechecker.startupCount", 0);
			int startupMax = propGlobal.getProperty("updatechecker.startupMax", 5);

			if(startupCount >= startupMax) {
				String strURL = propGlobal.getProperty("updatechecker.url", "");
				UpdateChecker.addListener(this);
				UpdateChecker.startCheckForUpdates(strURL);
				startupCount = 0;
			} else {
				startupCount++;
			}

			if(startupMax >= 1) {
				propGlobal.setProperty("updatechecker.startupCount", startupCount);
				saveConfig();
			}
		}

		// コマンドLinesからリプレイ再生
		if((programArgs != null) && (programArgs.length > 0)) {
			startReplayGame(programArgs[0]);
		}
	}

	/**
	 * GUIのInitialization
	 */
	protected void initUI() {
		mainLayout = new CardLayout();
		this.setLayout(mainLayout);

		// トップ画面
		JPanel panelTop = new JPanel();
		initTopScreenUI(panelTop);
		this.add(panelTop, "top");
	}

	/**
	 * トップ画面のGUIをInitialization
	 */
	protected void initTopScreenUI(JComponent p) {
		p.setLayout(new BorderLayout());

		// Modeセレクト
		JPanel subpanelModeSelect = new JPanel(new BorderLayout());
		subpanelModeSelect.setBorder(new EtchedBorder());
		p.add(subpanelModeSelect, BorderLayout.CENTER);

		// ラベル
		lModeSelect = new JLabel(getUIText("Top_ModeSelect"));
		subpanelModeSelect.add(lModeSelect, BorderLayout.PAGE_START);

		listboxMode = new JList(modeList);
		listboxMode.setSelectedIndex(0);
		listboxMode.setSelectedValue(propGlobal.getProperty("name.mode", ""), true);
		listboxMode.addMouseListener(new ListboxModeMouseAdapter());
		listboxMode.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				String strMode = (String)listboxMode.getSelectedValue();
				lModeSelect.setText(getModeDesc(strMode));
			}
		});
		JScrollPane scpaneListboxMode = new JScrollPane(listboxMode);
		scpaneListboxMode.setPreferredSize(new Dimension(300, 375));
		subpanelModeSelect.add(scpaneListboxMode, BorderLayout.CENTER);

		// 開始 button
		JButton buttonStartOffline = new JButton(getUIText("Top_StartOffline"));
		buttonStartOffline.setMnemonic('S');
		buttonStartOffline.addActionListener(this);
		buttonStartOffline.setActionCommand("Top_StartOffline");
		subpanelModeSelect.add(buttonStartOffline, BorderLayout.PAGE_END);
		this.getRootPane().setDefaultButton(buttonStartOffline);

		// Menu
		initMenu();
	}

	/**
	 * ルーム画面のGUIをInitialization
	 * @param p
	 */
	protected void initRoomScreenUI(JComponent p) {
		p.setLayout(new BorderLayout());

		// ルームチャット
		JPanel panelChat = new JPanel();
		panelChat.setLayout(new BorderLayout());
		p.add(panelChat, BorderLayout.CENTER);

		JList listboxUsers = new JList();
		listboxUsers.setPreferredSize(new Dimension(120, 200));
		listboxUsers.setBorder(new EtchedBorder());
		panelChat.add(listboxUsers, BorderLayout.EAST);

		JTextArea txtareaChat = new JTextArea();
		txtareaChat.setBorder(new EtchedBorder());
		panelChat.add(txtareaChat, BorderLayout.CENTER);

		JTextField txtfldMessage = new JTextField();
		panelChat.add(txtfldMessage, BorderLayout.PAGE_END);

		// 画面下の button
		JPanel panelButtons = new JPanel();
		p.add(panelButtons, BorderLayout.PAGE_END);

		JButton buttonReady = new JButton(getUIText("Room_Ready"));
		buttonReady.setMnemonic('R');
		buttonReady.addActionListener(this);
		buttonReady.setActionCommand("Room_Ready");
		panelButtons.add(buttonReady);

		JButton buttonLeave = new JButton(getUIText("Room_Leave"));
		buttonLeave.setMnemonic('L');
		buttonLeave.addActionListener(this);
		buttonLeave.setActionCommand("Room_Leave");
		panelButtons.add(buttonLeave);
	}

	/**
	 * Menu のInitialization
	 */
	protected void initMenu() {
		JMenuBar menubar = new JMenuBar();
		this.setJMenuBar(menubar);

		// ファイルMenu
		JMenu menuFile = new JMenu(getUIText("Menu_File"));
		menuFile.setMnemonic('F');
		menubar.add(menuFile);

		// リプレイを開く
		JMenuItem miOpen = new JMenuItem(getUIText("Menu_Open"));
		miOpen.setMnemonic('O');
		miOpen.addActionListener(this);
		miOpen.setActionCommand("Menu_Open");
		menuFile.add(miOpen);

		// ネットプレイ開始
		JMenuItem miNetPlay = new JMenuItem(getUIText("Menu_NetPlay"));
		miNetPlay.setMnemonic('N');
		miNetPlay.addActionListener(this);
		miNetPlay.setActionCommand("Menu_NetPlay");
		menuFile.add(miNetPlay);

		// 終了
		JMenuItem miExit = new JMenuItem(getUIText("Menu_Exit"));
		miExit.setMnemonic('X');
		miExit.addActionListener(this);
		miExit.setActionCommand("Menu_Exit");
		menuFile.add(miExit);

		// 設定Menu
		JMenu menuConfig = new JMenu(getUIText("Menu_Config"));
		menuConfig.setMnemonic('C');
		menubar.add(menuConfig);

		// ルール選択
		JMenuItem miRuleSelect = new JMenuItem(getUIText("Menu_RuleSelect"));
		miRuleSelect.setMnemonic('R');
		miRuleSelect.addActionListener(this);
		miRuleSelect.setActionCommand("Menu_RuleSelect");
		menuConfig.add(miRuleSelect);

		// ルール選択(2P)
		JMenuItem miRuleSelect2P = new JMenuItem(getUIText("Menu_RuleSelect2P"));
		miRuleSelect2P.setMnemonic('S');
		miRuleSelect2P.addActionListener(this);
		miRuleSelect2P.setActionCommand("Menu_RuleSelect2P");
		menuConfig.add(miRuleSelect2P);

		// チューニング設定
		JMenuItem miGameTuning = new JMenuItem(getUIText("Menu_GameTuning"));
		miGameTuning.setMnemonic('T');
		miGameTuning.addActionListener(this);
		miGameTuning.setActionCommand("Menu_GameTuning");
		menuConfig.add(miGameTuning);

		// チューニング設定(2P)
		JMenuItem miGameTuning2P = new JMenuItem(getUIText("Menu_GameTuning2P"));
		miGameTuning2P.setMnemonic('U');
		miGameTuning2P.addActionListener(this);
		miGameTuning2P.setActionCommand("Menu_GameTuning2P");
		menuConfig.add(miGameTuning2P);

		// AI設定
		JMenuItem miAIConfig = new JMenuItem(getUIText("Menu_AIConfig"));
		miAIConfig.setMnemonic('A');
		miAIConfig.addActionListener(this);
		miAIConfig.setActionCommand("Menu_AIConfig");
		menuConfig.add(miAIConfig);

		// AI設定(2P)
		JMenuItem miAIConfig2P = new JMenuItem(getUIText("Menu_AIConfig2P"));
		miAIConfig2P.setMnemonic('Z');
		miAIConfig2P.addActionListener(this);
		miAIConfig2P.setActionCommand("Menu_AIConfig2P");
		menuConfig.add(miAIConfig2P);

		// キー設定
		JMenuItem miKeyConfig = new JMenuItem(getUIText("Menu_KeyConfig"));
		miKeyConfig.setMnemonic('K');
		miKeyConfig.addActionListener(this);
		miKeyConfig.setActionCommand("Menu_KeyConfig");
		menuConfig.add(miKeyConfig);

		// キー設定(2P)
		JMenuItem miKeyConfig2P = new JMenuItem(getUIText("Menu_KeyConfig2P"));
		miKeyConfig2P.setMnemonic('E');
		miKeyConfig2P.addActionListener(this);
		miKeyConfig2P.setActionCommand("Menu_KeyConfig2P");
		menuConfig.add(miKeyConfig2P);

		// 更新 check 設定
		JMenuItem miUpdateCheck = new JMenuItem(getUIText("Menu_UpdateCheck"));
		miUpdateCheck.setMnemonic('D');
		miUpdateCheck.addActionListener(this);
		miUpdateCheck.setActionCommand("Menu_UpdateCheck");
		menuConfig.add(miUpdateCheck);

		// その他の設定
		JMenuItem miGeneralConfig = new JMenuItem(getUIText("Menu_GeneralConfig"));
		miGeneralConfig.setMnemonic('G');
		miGeneralConfig.addActionListener(this);
		miGeneralConfig.setActionCommand("Menu_GeneralConfig");
		menuConfig.add(miGeneralConfig);
	}

	/**
	 * オフLinesStart game buttonが押されたとき
	 */
	protected void onStartOfflineClicked() {
		String strMode = (String)listboxMode.getSelectedValue();
		propGlobal.setProperty("name.mode", strMode);
		saveConfig();
		startNewGame();
		if(gameFrame == null) {
			gameFrame = new GameFrame(this);
		}
		if((gameManager != null) && (gameManager.mode != null)) {
			gameFrame.setTitle(getUIText("Title_Game") + " - " + gameManager.mode.getName());
			gameFrame.maxfps = propConfig.getProperty("option.maxfps", 60);
			gameFrame.isNetPlay = false;
		}
		hideAllSubWindows();
		this.setVisible(false);
		gameFrame.displayWindow();
	}

	/*
	 * Menu 実行時の処理
	 */
	public void actionPerformed(ActionEvent e) {
		// オフLinesStart game
		if(e.getActionCommand() == "Top_StartOffline") {
			onStartOfflineClicked();
		}
		// リプレイ開く
		else if(e.getActionCommand() == "Menu_Open") {
			if(replayFileChooser == null) {
				File dir = new File(propGlobal.getProperty("custom.replay.directory", "replay"));
				replayFileChooser = new JFileChooser(dir);
				replayFileChooser.addChoosableFileFilter(new ReplayFileFilter());
			}
			if(replayFileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				startReplayGame(replayFileChooser.getSelectedFile().getPath());
				if(gameFrame == null) {
					gameFrame = new GameFrame(this);
				}
				if((gameManager != null) && (gameManager.mode != null)) {
					gameFrame.setTitle(getUIText("Title_Game") + " - " + gameManager.mode.getName() + " (Replay)");
					gameFrame.maxfps = propConfig.getProperty("option.maxfps", 60);
					gameFrame.isNetPlay = false;
				}
				hideAllSubWindows();
				this.setVisible(false);
				gameFrame.displayWindow();
			}
		}
		// ネットプレイ開始
		else if(e.getActionCommand() == "Menu_NetPlay") {
			startNetPlayGame();
			if(gameFrame == null) {
				gameFrame = new GameFrame(this);
			}
			if((gameManager != null) && (gameManager.mode != null)) {
				gameFrame.setTitle(getUIText("Title_Game") + " - " + gameManager.mode.getName());
				gameFrame.maxfps = 60;
				gameFrame.isNetPlay = true;
			}
			hideAllSubWindows();
			this.setVisible(false);
			gameFrame.displayWindow();
		}
		// ルール選択
		else if(e.getActionCommand() == "Menu_RuleSelect") {
			if(ruleSelectFrame == null) {
				ruleSelectFrame = new RuleSelectFrame(this);
			}
			ruleSelectFrame.load(0);
			ruleSelectFrame.setVisible(true);
		}
		// ルール選択(2P)
		else if(e.getActionCommand() == "Menu_RuleSelect2P") {
			if(ruleSelectFrame == null) {
				ruleSelectFrame = new RuleSelectFrame(this);
			}
			ruleSelectFrame.load(1);
			ruleSelectFrame.setVisible(true);
		}
		// キーボード設定
		else if(e.getActionCommand() == "Menu_KeyConfig") {
			if(keyConfigFrame == null) {
				keyConfigFrame = new KeyConfigFrame(this);
			}
			keyConfigFrame.load(0);
			keyConfigFrame.setVisible(true);
		}
		// キーボード設定(2P)
		else if(e.getActionCommand() == "Menu_KeyConfig2P") {
			if(keyConfigFrame == null) {
				keyConfigFrame = new KeyConfigFrame(this);
			}
			keyConfigFrame.load(1);
			keyConfigFrame.setVisible(true);
		}
		// AI設定
		else if(e.getActionCommand() == "Menu_AIConfig") {
			if(aiSelectFrame == null) {
				aiSelectFrame = new AISelectFrame(this);
			}
			aiSelectFrame.load(0);
			aiSelectFrame.setVisible(true);
		}
		// AI設定(2P)
		else if(e.getActionCommand() == "Menu_AIConfig2P") {
			if(aiSelectFrame == null) {
				aiSelectFrame = new AISelectFrame(this);
			}
			aiSelectFrame.load(1);
			aiSelectFrame.setVisible(true);
		}
		// チューニング設定
		else if(e.getActionCommand() == "Menu_GameTuning") {
			if(gameTuningFrame == null) {
				gameTuningFrame = new GameTuningFrame(this);
			}
			gameTuningFrame.load(0);
			gameTuningFrame.setVisible(true);
		}
		// チューニング設定(2P)
		else if(e.getActionCommand() == "Menu_GameTuning2P") {
			if(gameTuningFrame == null) {
				gameTuningFrame = new GameTuningFrame(this);
			}
			gameTuningFrame.load(1);
			gameTuningFrame.setVisible(true);
		}
		// 更新 check 設定
		else if(e.getActionCommand() == "Menu_UpdateCheck") {
			if(updateCheckFrame == null) {
				updateCheckFrame = new UpdateCheckFrame(this);
			}
			updateCheckFrame.load();
			updateCheckFrame.setVisible(true);
		}
		// その他の設定
		else if(e.getActionCommand() == "Menu_GeneralConfig") {
			if(generalConfigFrame == null) {
				generalConfigFrame = new GeneralConfigFrame(this);
			}
			generalConfigFrame.load();
			generalConfigFrame.setVisible(true);
		}
		// 終了
		else if(e.getActionCommand() == "Menu_Exit") {
			System.exit(0);
		}
	}

	/**
	 * すべてのサブウィンドウを隠す
	 */
	public void hideAllSubWindows() {
		if(keyConfigFrame != null) keyConfigFrame.setVisible(false);
		if(ruleSelectFrame != null) ruleSelectFrame.setVisible(false);
		if(aiSelectFrame != null) aiSelectFrame.setVisible(false);
		if(generalConfigFrame != null) generalConfigFrame.setVisible(false);
		if(gameTuningFrame != null) gameTuningFrame.setVisible(false);
		if(updateCheckFrame != null) updateCheckFrame.setVisible(false);
	}

	/**
	 * 新しいゲームの開始処理
	 */
	public void startNewGame() {
		rendererSwing = new RendererSwing();
		gameManager = new GameManager(rendererSwing);

		// Mode
		String modeName = propGlobal.getProperty("name.mode", "");
		GameMode modeObj = modeManager.getMode(modeName);
		if(modeObj == null) {
			log.error("Couldn't find mode:" + modeName);
		} else {
			gameManager.mode = modeObj;
		}

		gameManager.init();

		// Initialization for each player
		for(int i = 0; i < gameManager.getPlayers(); i++) {
			// チューニング設定
			gameManager.engine[i].owRotateButtonDefaultRight = propGlobal.getProperty(i + ".tuning.owRotateButtonDefaultRight", 0);
			gameManager.engine[i].owSkin = propGlobal.getProperty(i + ".tuning.owSkin", -1);
			gameManager.engine[i].owMinDAS = propGlobal.getProperty(i + ".tuning.owMinDAS", -1);
			gameManager.engine[i].owMaxDAS = propGlobal.getProperty(i + ".tuning.owMaxDAS", -1);
			gameManager.engine[i].owDasDelay = propGlobal.getProperty(i + ".tuning.owDasDelay", -1);

			// ルール
			RuleOptions ruleopt = null;
			String rulename = propGlobal.getProperty(i + ".rule", "");
			if(gameManager.mode.getGameStyle() > 0) {
				rulename = propGlobal.getProperty(i + ".rule." + gameManager.mode.getGameStyle(), "");
			}
			if((rulename != null) && (rulename.length() > 0)) {
				log.debug("Load rule options from " + rulename);
				ruleopt = GeneralUtil.loadRule(rulename);
			} else {
				log.debug("Load rule options from setting file");
				ruleopt = new RuleOptions();
				ruleopt.readProperty(propGlobal, i);
			}
			gameManager.engine[i].ruleopt = ruleopt;

			// NEXT順生成アルゴリズム
			if((ruleopt.strRandomizer != null) && (ruleopt.strRandomizer.length() > 0)) {
				Randomizer randomizerObject = GeneralUtil.loadRandomizer(ruleopt.strRandomizer);
				gameManager.engine[i].randomizer = randomizerObject;
			}

			// Wallkick
			if((ruleopt.strWallkick != null) && (ruleopt.strWallkick.length() > 0)) {
				Wallkick wallkickObject = GeneralUtil.loadWallkick(ruleopt.strWallkick);
				gameManager.engine[i].wallkick = wallkickObject;
			}

			// AI
			String aiName = propGlobal.getProperty(i + ".ai", "");
			if(aiName.length() > 0) {
				DummyAI aiObj = GeneralUtil.loadAIPlayer(aiName);
				gameManager.engine[i].ai = aiObj;
				gameManager.engine[i].aiMoveDelay = propGlobal.getProperty(i + ".aiMoveDelay", 0);
				gameManager.engine[i].aiThinkDelay = propGlobal.getProperty(i + ".aiThinkDelay", 0);
				gameManager.engine[i].aiUseThread = propGlobal.getProperty(i + ".aiUseThread", true);
				gameManager.engine[i].aiShowHint = propGlobal.getProperty(i+".aiShowHint", false);
			}

			// Called at initialization
			gameManager.engine[i].init();
		}
	}

	/**
	 * リプレイを読み込んで再生
	 * @param filename リプレイ dataのFilename
	 */
	public void startReplayGame(String filename) {
		log.info("Loading Replay:" + filename);
		CustomProperties prop = new CustomProperties();

		try {
			FileInputStream stream = new FileInputStream(filename);
			prop.load(stream);
			stream.close();
		} catch (IOException e) {
			log.error("Couldn't load replay file from " + filename, e);
			return;
		}

		rendererSwing = new RendererSwing();
		gameManager = new GameManager(rendererSwing);
		gameManager.replayMode = true;
		gameManager.replayProp = prop;

		// Mode
		String modeName = prop.getProperty("name.mode", "");
		GameMode modeObj = modeManager.getMode(modeName);
		if(modeObj == null) {
			log.error("Couldn't find mode:" + modeName);
		} else {
			gameManager.mode = modeObj;
		}

		gameManager.init();

		// Initialization for each player
		for(int i = 0; i < gameManager.getPlayers(); i++) {
			// ルール
			RuleOptions ruleopt = new RuleOptions();
			ruleopt.readProperty(prop, i);
			gameManager.engine[i].ruleopt = ruleopt;

			// NEXT順生成アルゴリズム
			if((ruleopt.strRandomizer != null) && (ruleopt.strRandomizer.length() > 0)) {
				Randomizer randomizerObject = GeneralUtil.loadRandomizer(ruleopt.strRandomizer);
				gameManager.engine[i].randomizer = randomizerObject;
			}

			// Wallkick
			if((ruleopt.strWallkick != null) && (ruleopt.strWallkick.length() > 0)) {
				Wallkick wallkickObject = GeneralUtil.loadWallkick(ruleopt.strWallkick);
				gameManager.engine[i].wallkick = wallkickObject;
			}

			// AI (リプレイ追記用）
			String aiName = propGlobal.getProperty(i + ".ai", "");
			if(aiName.length() > 0) {
				DummyAI aiObj = GeneralUtil.loadAIPlayer(aiName);
				gameManager.engine[i].ai = aiObj;
				gameManager.engine[i].aiMoveDelay = propGlobal.getProperty(i + ".aiMoveDelay", 0);
				gameManager.engine[i].aiThinkDelay = propGlobal.getProperty(i + ".aiThinkDelay", 0);
				gameManager.engine[i].aiUseThread = propGlobal.getProperty(i + ".aiUseThread", true);
				gameManager.engine[i].aiShowHint = propGlobal.getProperty(i+".aiShowHint",false);
			}

			// Called at initialization
			gameManager.engine[i].init();
		}
	}

	/**
	 * ネットプレイ開始処理
	 */
	public void startNetPlayGame() {
		// gameManager Initialization
		rendererSwing = new RendererSwing();
		gameManager = new GameManager(rendererSwing);

		// Lobby Initialization
		netLobby = new NetLobbyFrame();
		netLobby.addListener(this);

		// Mode initialization
		enterNewMode(null);

		// Lobby start
		netLobby.init();
		netLobby.setVisible(true);
	}

	/**
	 * Enter to a new mode in netplay
	 * @param modeName Mode name
	 */
	public void enterNewMode(String modeName) {
		GameMode previousMode = gameManager.mode;
		GameMode newModeTemp = (modeName == null) ? new NetDummyMode() : NullpoMinoSwing.modeManager.getMode(modeName);

		if(newModeTemp == null) {
			log.error("Cannot find a mode:" + modeName);
		} else if(newModeTemp instanceof NetDummyMode) {
			log.info("Enter new mode:" + newModeTemp.getName());

			NetDummyMode newMode = (NetDummyMode)newModeTemp;
			if(gameFrame != null) gameFrame.setTitle(getUIText("Title_Game") + " - " + newMode.getName());

			if(previousMode != null) previousMode.netplayUnload(netLobby);
			gameManager.mode = newMode;
			gameManager.init();

			// Tuning
			gameManager.engine[0].owRotateButtonDefaultRight = NullpoMinoSwing.propGlobal.getProperty(0 + ".tuning.owRotateButtonDefaultRight", 0);
			gameManager.engine[0].owSkin = NullpoMinoSwing.propGlobal.getProperty(0 + ".tuning.owSkin", -1);
			gameManager.engine[0].owMinDAS = NullpoMinoSwing.propGlobal.getProperty(0 + ".tuning.owMinDAS", -1);
			gameManager.engine[0].owMaxDAS = NullpoMinoSwing.propGlobal.getProperty(0 + ".tuning.owMaxDAS", -1);
			gameManager.engine[0].owDasDelay = NullpoMinoSwing.propGlobal.getProperty(0 + ".tuning.owDasDelay", -1);

			// Rule
			RuleOptions ruleopt = null;
			String rulename = NullpoMinoSwing.propGlobal.getProperty(0 + ".rule", "");
			if(gameManager.mode.getGameStyle() > 0) {
				rulename = propGlobal.getProperty(0 + ".rule." + gameManager.mode.getGameStyle(), "");
			}
			if((rulename != null) && (rulename.length() > 0)) {
				log.info("Load rule options from " + rulename);
				ruleopt = GeneralUtil.loadRule(rulename);
			} else {
				log.info("Load rule options from setting file");
				ruleopt = new RuleOptions();
				ruleopt.readProperty(NullpoMinoSwing.propGlobal, 0);
			}
			gameManager.engine[0].ruleopt = ruleopt;

			// Randomizer
			if((ruleopt.strRandomizer != null) && (ruleopt.strRandomizer.length() > 0)) {
				Randomizer randomizerObject = GeneralUtil.loadRandomizer(ruleopt.strRandomizer);
				gameManager.engine[0].randomizer = randomizerObject;
			}

			// Wallkick
			if((ruleopt.strWallkick != null) && (ruleopt.strWallkick.length() > 0)) {
				Wallkick wallkickObject = GeneralUtil.loadWallkick(ruleopt.strWallkick);
				gameManager.engine[0].wallkick = wallkickObject;
			}

			// AI
			String aiName = NullpoMinoSwing.propGlobal.getProperty(0 + ".ai", "");
			if(aiName.length() > 0) {
				DummyAI aiObj = GeneralUtil.loadAIPlayer(aiName);
				gameManager.engine[0].ai = aiObj;
				gameManager.engine[0].aiMoveDelay = NullpoMinoSwing.propGlobal.getProperty(0 + ".aiMoveDelay", 0);
				gameManager.engine[0].aiThinkDelay = NullpoMinoSwing.propGlobal.getProperty(0 + ".aiThinkDelay", 0);
				gameManager.engine[0].aiUseThread = NullpoMinoSwing.propGlobal.getProperty(0 + ".aiUseThread", true);
				gameManager.engine[0].aiShowHint = NullpoMinoSwing.propGlobal.getProperty(0+".aiShowHint",false);
			}

			// Initialization for each player
			for(int i = 0; i < gameManager.getPlayers(); i++) {
				gameManager.engine[i].init();
			}

			newMode.netplayInit(netLobby);
		} else {
			log.error("This mode does not support netplay:" + modeName);
		}
	}

	/**
	 * Observerクライアントを開始
	 */
	public synchronized static void startObserverClient() {
		log.debug("startObserverClient called");

		if(propObserver == null) {
			propObserver = new CustomProperties();
			try {
				FileInputStream in = new FileInputStream("config/setting/netobserver.cfg");
				propObserver.load(in);
				in.close();
			} catch (IOException e) {}
		}

		if(propObserver.getProperty("observer.enable", false) == false) return;
		if((netObserverClient != null) && netObserverClient.isConnected()) return;

		String host = propObserver.getProperty("observer.host", "");
		int port = propObserver.getProperty("observer.port", NetObserverClient.DEFAULT_PORT);

		if((host.length() > 0) && (port > 0)) {
			netObserverClient = new NetObserverClient(host, port);
			netObserverClient.start();
			log.debug("Observer started");
		}
	}

	/**
	 * Observerクライアントを停止
	 */
	public synchronized static void stopObserverClient() {
		log.debug("stopObserverClient called");

		if(netObserverClient != null) {
			if(netObserverClient.isConnected()) {
				netObserverClient.send("disconnect\n");
			}
			netObserverClient.threadRunning = false;
			netObserverClient.connectedFlag = false;
			netObserverClient = null;
			log.debug("Observer stoped");
		}
	}

	/**
	 * Observerクライアント取得
	 * @return Observerクライアント
	 */
	public synchronized static NetObserverClient getObserverClient() {
		return netObserverClient;
	}

	public void netlobbyOnDisconnect(NetLobbyFrame lobby, NetPlayerClient client, Throwable ex) {
		if(gameFrame != null) gameFrame.strModeToEnter = null;
	}

	public void netlobbyOnExit(NetLobbyFrame lobby) {
		if(gameManager != null) {
			gameManager.engine[0].quitflag = true;
		}
	}

	public void netlobbyOnInit(NetLobbyFrame lobby) {
	}

	public void netlobbyOnLoginOK(NetLobbyFrame lobby, NetPlayerClient client) {
	}

	public void netlobbyOnMessage(NetLobbyFrame lobby, NetPlayerClient client, String[] message) throws IOException {
	}

	public void netlobbyOnRoomJoin(NetLobbyFrame lobby, NetPlayerClient client, NetRoomInfo roomInfo) {
		//enterNewMode(roomInfo.strMode);
		if(gameFrame != null) gameFrame.strModeToEnter = roomInfo.strMode;
	}

	public void netlobbyOnRoomLeave(NetLobbyFrame lobby, NetPlayerClient client) {
		//enterNewMode(null);
		if(gameFrame != null) gameFrame.strModeToEnter = null;
	}

	public void onUpdateCheckerStart() {
	}

	public void onUpdateCheckerEnd(int status) {
		if(UpdateChecker.isNewVersionAvailable(GameManager.getVersionMajor(), GameManager.getVersionMinor())) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					if(lModeSelect != null) {
						String strTemp = String.format(getUIText("Top_NewVersion"),
								UpdateChecker.getLatestVersionFullString(), UpdateChecker.getStrReleaseDate());
						lModeSelect.setText(strTemp);
					}
				}
			});
		}
	}

	/**
	 * リプレイファイル選択用フィルタ
	 */
	protected class ReplayFileFilter extends FileFilter {
		/*
		 * ファイルを表示するかどうか判定
		 */
		@Override
		public boolean accept(File f) {
			// ディレクトリなら無条件表示
			// またはファイル末尾が.repだったら表示
			if(f.isDirectory() || f.getName().endsWith(".rep")) return true;
			return false;
		}

		/*
		 * このフィルタの表示名を返す
		 */
		@Override
		public String getDescription() {
			return getUIText("FileChooser_ReplayFile");
		}
	}

	/**
	 * Mode 選択リストボックス用MouseAdapter
	 */
	protected class ListboxModeMouseAdapter extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			if((e.getClickCount() == 2) && (e.getButton() == MouseEvent.BUTTON1)) {
				onStartOfflineClicked();
			}
		}
	}
}
