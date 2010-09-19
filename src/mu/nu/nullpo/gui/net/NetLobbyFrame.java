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
package mu.nu.nullpo.gui.net;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.Locale;
import java.util.zip.Adler32;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import mu.nu.nullpo.game.component.RuleOptions;
import mu.nu.nullpo.game.net.NetBaseClient;
import mu.nu.nullpo.game.net.NetMessageListener;
import mu.nu.nullpo.game.net.NetPlayerClient;
import mu.nu.nullpo.game.net.NetPlayerInfo;
import mu.nu.nullpo.game.net.NetRoomInfo;
import mu.nu.nullpo.game.net.NetUtil;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.game.subsystem.mode.NetDummyMode;
import mu.nu.nullpo.util.CustomProperties;
import mu.nu.nullpo.util.GeneralUtil;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * NullpoMino NetLobby
 */
public class NetLobbyFrame extends JFrame implements ActionListener, NetMessageListener {
	/** Serial Version ID */
	private static final long serialVersionUID = 1L;

	/** Room-table column names. These strings will be passed to getUIText(String) subroutine. */
	public static final String[] ROOMTABLE_COLUMNNAMES = {
		"RoomTable_ID","RoomTable_Name","RoomTable_Rated","RoomTable_RuleName","RoomTable_Status","RoomTable_Players","RoomTable_Spectators"
	};

	/** End-of-game statistics column names. These strings will be passed to getUIText(String) subroutine. */
	public static final String[] STATTABLE_COLUMNNAMES = {
		"StatTable_Rank", "StatTable_Name",
		"StatTable_Attack", "StatTable_APM", "StatTable_Lines", "StatTable_LPM", "StatTable_Piece", "StatTable_PPS", "StatTable_Time",
		"StatTable_KO", "StatTable_Wins", "StatTable_Games"
	};

	/** Multiplayer leaderboard column names. These strings will be passed to getUIText(String) subroutine. */
	public static final String[] MPRANKING_COLUMNNAMES  = {
		"MPRanking_Rank", "MPRanking_Name", "MPRanking_Rating", "MPRanking_PlayCount", "MPRanking_WinCount"
	};

	/** Spin bonus names */
	public static final String[] COMBOBOX_SPINBONUS_NAMES = {"CreateRoom_TSpin_Disable", "CreateRoom_TSpin_TOnly", "CreateRoom_TSpin_All"};

	/** Names for spin check types */
	public static final String[] COMBOBOX_SPINCHECKTYPE_NAMES = {"CreateRoom_SpinCheck_4Point", "CreateRoom_SpinCheck_Immobile"};

	/** Constants for each screen-card */
	public static final int SCREENCARD_SERVERSELECT = 0,
							SCREENCARD_LOBBY = 1,
							SCREENCARD_SERVERADD = 2,
							SCREENCARD_CREATEROOM = 3,
							SCREENCARD_CREATEROOM1P = 4,
							SCREENCARD_MPRANKING = 5;

	/** Names for each screen-card */
	public static final String[] SCREENCARD_NAMES = {"ServerSelect", "Lobby", "ServerAdd", "CreateRoom", "CreateRoom1P", "MPRanking"};

	/** Log */
	static final Logger log = Logger.getLogger(NetLobbyFrame.class);

	/** NetPlayerClient */
	public NetPlayerClient netPlayerClient;

	/** Rule data */
	public RuleOptions ruleOptPlayer, ruleOptLock;

	/** Map list */
	public LinkedList<String> mapList;

	/** Event listeners */
	protected LinkedList<NetLobbyListener> listeners = new LinkedList<NetLobbyListener>();

	/** Current game mode (act as special NetLobbyListener) */
	protected NetDummyMode netDummyMode;

	/** Property file for lobby settings */
	protected CustomProperties propConfig;

	/** Property file for swing settings */
	protected CustomProperties propSwingConfig;

	/** Property file for observer ("Watch") settings */
	protected CustomProperties propObserver;

	/** Default language file */
	protected CustomProperties propLangDefault;

	/** Property file for GUI translations */
	protected CustomProperties propLang;

	/** Current screen-card number */
	protected int currentScreenCardNumber;

	/** Current room ID (for View Detail) */
	protected int currentViewDetailRoomID = -1;

	/** NetRoomInfo for settings backup */
	protected NetRoomInfo backupRoomInfo;

	/** PrintWriter for lobby log */
	protected PrintWriter writerLobbyLog;

	/** PrintWriter for room log */
	protected PrintWriter writerRoomLog;

	/** Rated-game rule name list */
	@SuppressWarnings("rawtypes")
	protected LinkedList[] listRatedRuleName;

	/** Layout manager for main screen */
	protected CardLayout contentPaneCardLayout;

	/** Text field of player name (Server select screen) */
	protected JTextField txtfldPlayerName;

	/** Text field of team name (Server select screen) */
	protected JTextField txtfldPlayerTeam;

	/** Listbox for servers (Server select screen) */
	protected JList listboxServerList;

	/** Listbox data for servers (Server select screen) */
	protected DefaultListModel listmodelServerList;

	/** Connect button (Server select screen) */
	protected JButton btnServerConnect;

	/** Lobby/Room Tab */
	protected JTabbedPane tabLobbyAndRoom;

	/** JSplitPane (Lobby screen) */
	protected JSplitPane splitLobby;

	/** ロビー画面上部のレイアウトマネージャ */
	protected CardLayout roomListTopBarCardLayout;

	/** ロビー画面上部のパネル */
	protected JPanel subpanelRoomListTopBar;

	/** クイックスタート button(Lobby screen) */
	protected JButton btnRoomListQuickStart;

	/** ルーム作成 button(Lobby screen) */
	protected JButton btnRoomListRoomCreate;

	/** Create Room 1P (Lobby screen) */
	protected JButton btnRoomListRoomCreate1P;

	/** チーム変更 button(Lobby screen) */
	protected JButton btnRoomListTeamChange;

	/** Leaderboard button (Lobby screen) */
	protected JButton btnRoomListRanking;

	/** Team name input 欄(Lobby screen) */
	protected JTextField txtfldRoomListTeam;

	/** ルーム一覧 table */
	protected JTable tableRoomList;

	/** ルーム一覧 tableのカラム名(翻訳後) */
	protected String[] strTableColumnNames;

	/** ルーム一覧 tableの data */
	protected DefaultTableModel tablemodelRoomList;

	/** チャットログとPlayerリストの仕切り線(Lobby screen) */
	protected JSplitPane splitLobbyChat;

	/** チャットログ(Lobby screen) */
	protected JTextPane txtpaneLobbyChatLog;

	/** Playerリスト(Lobby screen) */
	protected JList listboxLobbyChatPlayerList;

	/** Playerリスト(Lobby screen)の data */
	protected DefaultListModel listmodelLobbyChatPlayerList;

	/** チャット input 欄(Lobby screen) */
	protected JTextField txtfldLobbyChatInput;

	/** チャット送信 button(Lobby screen) */
	protected JButton btnLobbyChatSend;

	/** 参戦 button(Room screen) */
	protected JButton btnRoomButtonsJoin;

	/** 離脱 button(Room screen) */
	protected JButton btnRoomButtonsSitOut;

	/** チーム変更 button(Room screen) */
	protected JButton btnRoomButtonsTeamChange;

	/** View Settings button (Room screen) */
	protected JButton btnRoomButtonsViewSetting;

	/** Leaderboard button (Room screen) */
	protected JButton btnRoomButtonsRanking;

	/** 上下を分ける仕切り線(Room screen) */
	protected JSplitPane splitRoom;

	/** ルーム画面上部のレイアウトマネージャ */
	protected CardLayout roomTopBarCardLayout;

	/** ルーム画面上部パネル */
	protected JPanel subpanelRoomTopBar;

	/** ゲーム結果 table */
	protected JTable tableGameStat;

	/** ゲーム結果 tableのカラム名(翻訳後) */
	protected String[] strGameStatTableColumnNames;

	/** ゲーム結果 tableの data */
	protected DefaultTableModel tablemodelGameStat;

	/** チャットログとPlayerリストの仕切り線(Room screen) */
	protected JSplitPane splitRoomChat;

	/** チャットログ(Room screen) */
	protected JTextPane txtpaneRoomChatLog;

	/** Playerリスト(Room screen) */
	protected JList listboxRoomChatPlayerList;

	/** Playerリスト(Room screen)の data */
	protected DefaultListModel listmodelRoomChatPlayerList;

	/** 同じ部屋のPlayer情報 */
	protected LinkedList<NetPlayerInfo> sameRoomPlayerInfoList;

	/** チャット input 欄(Room screen) */
	protected JTextField txtfldRoomChatInput;

	/** チャット送信 button(Room screen) */
	protected JButton btnRoomChatSend;

	/** Team name input 欄(Room screen) */
	protected JTextField txtfldRoomTeam;

	/** ホスト名 input 欄(Server add screen) */
	protected JTextField txtfldServerAddHost;

	/** OK button(Server add screen) */
	protected JButton btnServerAddOK;

	/** ルーム名(Create room screen) */
	protected JTextField txtfldCreateRoomName;

	/** 参加人count(Create room screen) */
	protected JSpinner spinnerCreateRoomMaxPlayers;

	/** 自動開始前の待機 time(Create room screen) */
	protected JSpinner spinnerCreateRoomAutoStartSeconds;

	/** 落下速度・分子(Create room screen) */
	protected JSpinner spinnerCreateRoomGravity;

	/** 落下速度・分母(Create room screen) */
	protected JSpinner spinnerCreateRoomDenominator;

	/** ARE(Create room screen) */
	protected JSpinner spinnerCreateRoomARE;

	/** ARE after line clear(Create room screen) */
	protected JSpinner spinnerCreateRoomARELine;

	/** Line clear time(Create room screen) */
	protected JSpinner spinnerCreateRoomLineDelay;

	/** 固定 time(Create room screen) */
	protected JSpinner spinnerCreateRoomLockDelay;

	/** 横溜め(Create room screen) */
	protected JSpinner spinnerCreateRoomDAS;

	/** Hurryup開始までの秒count(Create room screen) */
	protected JSpinner spinnerCreateRoomHurryupSeconds;

	/** Hurryup後に何回Blockを置くたびに床をせり上げるか(Create room screen) */
	protected JSpinner spinnerCreateRoomHurryupInterval;

	/** MapセットID(Create room screen) */
	protected JSpinner spinnerCreateRoomMapSetID;

	/** Rate of change of garbage holes */
	protected JSpinner spinnerCreateRoomGarbagePercent;

	/** Map is enabled(Create room screen) */
	protected JCheckBox chkboxCreateRoomUseMap;

	/** 全員のルール固定(Create room screen) */
	protected JCheckBox chkboxCreateRoomRuleLock;

	/** スピン bonusタイプ(Create room screen) */
	protected JComboBox comboboxCreateRoomTSpinEnableType;

	/** Spin recognition type (4-point, immobile, etc.) */
	protected JComboBox comboboxCreateRoomSpinCheckType;

	/** Flag for enabling B2B(Create room screen) */
	protected JCheckBox chkboxCreateRoomB2B;

	/** Flag for enabling combos(Create room screen) */
	protected JCheckBox chkboxCreateRoomCombo;

	/** Allow Rensa/Combo Block */
	protected JCheckBox chkboxCreateRoomRensaBlock;

	/** Allow countering */
	protected JCheckBox chkboxCreateRoomCounter;

	/** Enable bravo bonus */
	protected JCheckBox chkboxCreateRoomBravo;

	/** Allow EZ spins */
	protected JCheckBox chkboxCreateRoomTSpinEnableEZ;

	/** 3人以上生きている場合に Attack 力を減らす(Create room screen) */
	protected JCheckBox chkboxCreateRoomReduceLineSend;

	/** Set garbage type */
	protected JCheckBox chkboxCreateRoomGarbageChangePerAttack;

	/** B2B chunk type */
	protected JCheckBox chkboxCreateRoomB2BChunk;

	/** 断片的garbage blockシステムを使う(Create room screen) */
	protected JCheckBox chkboxCreateRoomUseFractionalGarbage;

	/** TNET2タイプのAutomatically start timerを使う(Create room screen) */
	protected JCheckBox chkboxCreateRoomAutoStartTNET2;

	/** 誰かCancelしたらTimer無効化(Create room screen) */
	protected JCheckBox chkboxCreateRoomDisableTimerAfterSomeoneCancelled;

	/** Rule list listbox (Create room screen) */
	protected JList listboxCreateRoomRuleList;

	/** Rule list list data (Create room screen) */
	protected DefaultListModel listmodelCreateRoomRuleList;

	/** OK button(Create room screen) */
	protected JButton btnCreateRoomOK;

	/** 参戦 button(Create room screen) */
	protected JButton btnCreateRoomJoin;

	/** 観戦 button(Create room screen) */
	protected JButton btnCreateRoomWatch;

	/** Cancel Button (Create room screen) */
	protected JButton btnCreateRoomCancel;

	/** Game mode listbox (Create room 1P screen) */
	protected JList listboxCreateRoom1PModeList;

	/** Game mode list data (Create room 1P screen) */
	protected DefaultListModel listmodelCreateRoom1PModeList;

	/** Rule list listbox (Create room 1P screen) */
	protected JList listboxCreateRoom1PRuleList;

	/** Rule list list data (Create room 1P screen) */
	protected DefaultListModel listmodelCreateRoom1PRuleList;

	/** OK button (Create room 1P screen) */
	protected JButton btnCreateRoom1POK;

	/** Cancel button (Create room 1P screen) */
	protected JButton btnCreateRoom1PCancel;

	/** Tab (MPRanking screen) */
	protected JTabbedPane tabMPRanking;

	/** Column names of multiplayer leaderboard (MPRanking screen) */
	protected String[] strMPRankingTableColumnNames;

	/** Table of multiplayer leaderboard (MPRanking screen) */
	protected JTable[] tableMPRanking;

	/** Table data of multiplayer leaderboard (MPRanking screen) */
	protected DefaultTableModel[] tablemodelMPRanking;

	/** OK button (MPRanking screen) */
	protected JButton btnMPRankingOK;

	/**
	 * Constructor
	 */
	public NetLobbyFrame() {
		super();
	}

	/**
	 * Initialization
	 */
	@SuppressWarnings("rawtypes")
	public void init() {
		// 設定ファイル読み込み
		propConfig = new CustomProperties();
		try {
			FileInputStream in = new FileInputStream("config/setting/netlobby.cfg");
			propConfig.load(in);
			in.close();
		} catch(IOException e) {}

		// Swing版の設定ファイル読み込み
		propSwingConfig = new CustomProperties();
		try {
			FileInputStream in = new FileInputStream("config/setting/swing.cfg");
			propSwingConfig.load(in);
			in.close();
		} catch(IOException e) {}

		// Observer機能の設定ファイル読み込み
		propObserver = new CustomProperties();
		try {
			FileInputStream in = new FileInputStream("config/setting/netobserver.cfg");
			propObserver.load(in);
			in.close();
		} catch(IOException e) {}

		// 言語ファイル読み込み
		propLangDefault = new CustomProperties();
		try {
			FileInputStream in = new FileInputStream("config/lang/netlobby_default.properties");
			propLangDefault.load(in);
			in.close();
		} catch (Exception e) {
			log.error("Couldn't load default UI language file", e);
		}

		propLang = new CustomProperties();
		try {
			FileInputStream in = new FileInputStream("config/lang/netlobby_" + Locale.getDefault().getCountry() + ".properties");
			propLang.load(in);
			in.close();
		} catch(IOException e) {}

		// Look&Feel設定
		if(propSwingConfig.getProperty("option.usenativelookandfeel", true) == true) {
			try {
				UIManager.getInstalledLookAndFeels();
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch(Exception e) {
				log.warn("Failed to set native look&feel", e);
			}
		}

		// WindowListener登録
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				shutdown();
			}
		});

		// Rated-game rule name list
		listRatedRuleName = new LinkedList[GameEngine.MAX_GAMESTYLE];
		for(int i = 0; i < GameEngine.MAX_GAMESTYLE; i++) {
			listRatedRuleName[i] = new LinkedList();
		}

		// Map list
		mapList = new LinkedList<String>();

		// GUI Init
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setTitle(getUIText("Title_NetLobby"));

		initUI();

		this.setSize(propConfig.getProperty("mainwindow.width", 500), propConfig.getProperty("mainwindow.height", 450));
		this.setLocation(propConfig.getProperty("mainwindow.x", 0), propConfig.getProperty("mainwindow.y", 0));

		// Listener呼び出し
		if(listeners != null) {
			for(NetLobbyListener l: listeners) {
				if(l != null)
					l.netlobbyOnInit(this);
			}
		}
		if(netDummyMode != null) {
			netDummyMode.netlobbyOnInit(this);
		}
	}

	/**
	 * GUI Initialization
	 */
	protected void initUI() {
		contentPaneCardLayout = new CardLayout();
		this.getContentPane().setLayout(contentPaneCardLayout);

		initServerSelectUI();
		initLobbyUI();
		initServerAddUI();
		initCreateRoomUI();
		initCreateRoom1PUI();
		initMPRankingUI();

		changeCurrentScreenCard(SCREENCARD_SERVERSELECT);
	}

	/**
	 * Server select screen initialization
	 */
	protected void initServerSelectUI() {
		// サーバー選択とName input 画面
		JPanel mainpanelServerSelect = new JPanel(new BorderLayout());
		this.getContentPane().add(mainpanelServerSelect, SCREENCARD_NAMES[SCREENCARD_SERVERSELECT]);

		// * NameとTeam name input パネル
		JPanel subpanelNames = new JPanel();
		subpanelNames.setLayout(new BoxLayout(subpanelNames, BoxLayout.Y_AXIS));
		mainpanelServerSelect.add(subpanelNames, BorderLayout.NORTH);

		// ** Name input パネル
		JPanel subpanelNameEntry = new JPanel(new BorderLayout());
		subpanelNames.add(subpanelNameEntry);

		// *** 「Name:」ラベル
		JLabel labelNameEntry = new JLabel(getUIText("ServerSelect_LabelName"));
		subpanelNameEntry.add(labelNameEntry, BorderLayout.WEST);

		// *** Name input 欄
		txtfldPlayerName = new JTextField();
		txtfldPlayerName.setComponentPopupMenu(new TextComponentPopupMenu(txtfldPlayerName));
		txtfldPlayerName.setText(propConfig.getProperty("serverselect.txtfldPlayerName.text", ""));
		subpanelNameEntry.add(txtfldPlayerName, BorderLayout.CENTER);

		// ** Team name input パネル
		JPanel subpanelTeamEntry = new JPanel(new BorderLayout());
		subpanelNames.add(subpanelTeamEntry);

		// *** 「Team name:」ラベル
		JLabel labelTeamEntry = new JLabel(getUIText("ServerSelect_LabelTeam"));
		subpanelTeamEntry.add(labelTeamEntry, BorderLayout.WEST);

		// *** Team name input 欄
		txtfldPlayerTeam = new JTextField();
		txtfldPlayerTeam.setComponentPopupMenu(new TextComponentPopupMenu(txtfldPlayerTeam));
		txtfldPlayerTeam.setText(propConfig.getProperty("serverselect.txtfldPlayerTeam.text", ""));
		subpanelTeamEntry.add(txtfldPlayerTeam, BorderLayout.CENTER);

		// * サーバー選択リストボックス
		listmodelServerList = new DefaultListModel();
		if(!loadListToDefaultListModel(listmodelServerList, "config/setting/netlobby_serverlist.cfg")) {
			loadListToDefaultListModel(listmodelServerList, "config/list/netlobby_serverlist_default.lst");
			saveListFromDefaultListModel(listmodelServerList, "config/setting/netlobby_serverlist.cfg");
		}
		listboxServerList = new JList(listmodelServerList);
		listboxServerList.setComponentPopupMenu(new ServerSelectListBoxPopupMenu());
		listboxServerList.addMouseListener(new ServerSelectListBoxMouseAdapter());
		listboxServerList.setSelectedValue(propConfig.getProperty("serverselect.listboxServerList.value", ""), true);
		JScrollPane spListboxServerSelect = new JScrollPane(listboxServerList);
		mainpanelServerSelect.add(spListboxServerSelect, BorderLayout.CENTER);

		// * サーバー追加・削除パネル
		JPanel subpanelServerAdd = new JPanel();
		subpanelServerAdd.setLayout(new BoxLayout(subpanelServerAdd, BoxLayout.Y_AXIS));
		mainpanelServerSelect.add(subpanelServerAdd, BorderLayout.EAST);

		// ** サーバー追加 button
		JButton btnServerAdd = new JButton(getUIText("ServerSelect_ServerAdd"));
		btnServerAdd.setMaximumSize(new Dimension(Short.MAX_VALUE, btnServerAdd.getMaximumSize().height));
		btnServerAdd.addActionListener(this);
		btnServerAdd.setActionCommand("ServerSelect_ServerAdd");
		btnServerAdd.setMnemonic('A');
		subpanelServerAdd.add(btnServerAdd);

		// ** サーバー削除 button
		JButton btnServerDelete = new JButton(getUIText("ServerSelect_ServerDelete"));
		btnServerDelete.setMaximumSize(new Dimension(Short.MAX_VALUE, btnServerDelete.getMaximumSize().height));
		btnServerDelete.addActionListener(this);
		btnServerDelete.setActionCommand("ServerSelect_ServerDelete");
		btnServerDelete.setMnemonic('D');
		subpanelServerAdd.add(btnServerDelete);

		// ** 監視設定 button
		JButton btnSetObserver = new JButton(getUIText("ServerSelect_SetObserver"));
		btnSetObserver.setMaximumSize(new Dimension(Short.MAX_VALUE, btnSetObserver.getMaximumSize().height));
		btnSetObserver.addActionListener(this);
		btnSetObserver.setActionCommand("ServerSelect_SetObserver");
		btnSetObserver.setMnemonic('S');
		subpanelServerAdd.add(btnSetObserver);

		// ** 監視解除 button
		JButton btnUnsetObserver = new JButton(getUIText("ServerSelect_UnsetObserver"));
		btnUnsetObserver.setMaximumSize(new Dimension(Short.MAX_VALUE, btnUnsetObserver.getMaximumSize().height));
		btnUnsetObserver.addActionListener(this);
		btnUnsetObserver.setActionCommand("ServerSelect_UnsetObserver");
		btnUnsetObserver.setMnemonic('U');
		subpanelServerAdd.add(btnUnsetObserver);

		// * 接続 button・Exit button用パネル
		JPanel subpanelServerSelectButtons = new JPanel();
		subpanelServerSelectButtons.setLayout(new BoxLayout(subpanelServerSelectButtons, BoxLayout.X_AXIS));
		mainpanelServerSelect.add(subpanelServerSelectButtons, BorderLayout.SOUTH);

		// ** 接続 button
		btnServerConnect = new JButton(getUIText("ServerSelect_Connect"));
		btnServerConnect.setMaximumSize(new Dimension(Short.MAX_VALUE, btnServerConnect.getMaximumSize().height));
		btnServerConnect.addActionListener(this);
		btnServerConnect.setActionCommand("ServerSelect_Connect");
		btnServerConnect.setMnemonic('C');
		subpanelServerSelectButtons.add(btnServerConnect);

		// ** Exit button
		JButton btnServerExit = new JButton(getUIText("ServerSelect_Exit"));
		btnServerExit.setMaximumSize(new Dimension(Short.MAX_VALUE, btnServerExit.getMaximumSize().height));
		btnServerExit.addActionListener(this);
		btnServerExit.setActionCommand("ServerSelect_Exit");
		btnServerExit.setMnemonic('X');
		subpanelServerSelectButtons.add(btnServerExit);
	}

	/**
	 * Lobby screen initialization
	 */
	protected void initLobbyUI() {
		tabLobbyAndRoom = new JTabbedPane();
		this.getContentPane().add(tabLobbyAndRoom, SCREENCARD_NAMES[SCREENCARD_LOBBY]);

		// === Lobby Tab ===
		JPanel mainpanelLobby = new JPanel(new BorderLayout());
		//this.getContentPane().add(mainpanelLobby, SCREENCARD_NAMES[SCREENCARD_LOBBY]);
		tabLobbyAndRoom.addTab(getUIText("Lobby_Tab_Lobby"), mainpanelLobby);
		tabLobbyAndRoom.setMnemonicAt(0, 'Y');

		// * 上下を分ける仕切り線
		splitLobby = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitLobby.setDividerLocation(propConfig.getProperty("lobby.splitLobby.location", 200));
		mainpanelLobby.add(splitLobby, BorderLayout.CENTER);

		// ** ルーム一覧(上)
		JPanel subpanelRoomList = new JPanel(new BorderLayout());
		subpanelRoomList.setMinimumSize(new Dimension(0,0));
		splitLobby.setTopComponent(subpanelRoomList);

		// *** ロビー画面上部パネル
		roomListTopBarCardLayout = new CardLayout();
		subpanelRoomListTopBar = new JPanel(roomListTopBarCardLayout);
		subpanelRoomList.add(subpanelRoomListTopBar, BorderLayout.NORTH);

		// **** ルーム一覧 button類
		JPanel subpanelRoomListButtons = new JPanel();
		subpanelRoomListTopBar.add(subpanelRoomListButtons, "Buttons");
		//subpanelRoomList.add(subpanelRoomListButtons, BorderLayout.NORTH);

		// ***** TODO:クイックスタート button
		btnRoomListQuickStart = new JButton(getUIText("Lobby_QuickStart"));
		btnRoomListQuickStart.addActionListener(this);
		btnRoomListQuickStart.setActionCommand("Lobby_QuickStart");
		btnRoomListQuickStart.setMnemonic('Q');
		btnRoomListQuickStart.setVisible(false);
		subpanelRoomListButtons.add(btnRoomListQuickStart);

		// ***** ルーム作成 button
		btnRoomListRoomCreate = new JButton(getUIText("Lobby_RoomCreate"));
		btnRoomListRoomCreate.addActionListener(this);
		btnRoomListRoomCreate.setActionCommand("Lobby_RoomCreate");
		btnRoomListRoomCreate.setMnemonic('C');
		subpanelRoomListButtons.add(btnRoomListRoomCreate);

		// ***** Create Room (1P) button
		btnRoomListRoomCreate1P = new JButton(getUIText("Lobby_RoomCreate1P"));
		btnRoomListRoomCreate1P.addActionListener(this);
		btnRoomListRoomCreate1P.setActionCommand("Lobby_RoomCreate1P");
		btnRoomListRoomCreate1P.setMnemonic('1');
		subpanelRoomListButtons.add(btnRoomListRoomCreate1P);

		// ***** チーム変更 button
		btnRoomListTeamChange = new JButton(getUIText("Lobby_TeamChange"));
		btnRoomListTeamChange.addActionListener(this);
		btnRoomListTeamChange.setActionCommand("Lobby_TeamChange");
		btnRoomListTeamChange.setMnemonic('T');
		subpanelRoomListButtons.add(btnRoomListTeamChange);

		// ***** Leaderboard button
		btnRoomListRanking = new JButton(getUIText("Lobby_Ranking"));
		btnRoomListRanking.addActionListener(this);
		btnRoomListRanking.setActionCommand("Lobby_Ranking");
		btnRoomListRanking.setMnemonic('L');
		subpanelRoomListButtons.add(btnRoomListRanking);

		// ***** 切断 button
		JButton btnRoomListDisconnect = new JButton(getUIText("Lobby_Disconnect"));
		btnRoomListDisconnect.addActionListener(this);
		btnRoomListDisconnect.setActionCommand("Lobby_Disconnect");
		btnRoomListDisconnect.setMnemonic('D');
		subpanelRoomListButtons.add(btnRoomListDisconnect);

		// **** チーム変更パネル
		JPanel subpanelRoomListTeam = new JPanel(new BorderLayout());
		subpanelRoomListTopBar.add(subpanelRoomListTeam, "Team");

		// ***** Team name input 欄
		txtfldRoomListTeam = new JTextField();
		subpanelRoomListTeam.add(txtfldRoomListTeam, BorderLayout.CENTER);

		// ***** Team name変更 buttonパネル
		JPanel subpanelRoomListTeamButtons = new JPanel();
		subpanelRoomListTeam.add(subpanelRoomListTeamButtons, BorderLayout.EAST);

		// ****** Team name変更OK
		JButton btnRoomListTeamOK = new JButton(getUIText("Lobby_TeamChange_OK"));
		btnRoomListTeamOK.addActionListener(this);
		btnRoomListTeamOK.setActionCommand("Lobby_TeamChange_OK");
		btnRoomListTeamOK.setMnemonic('O');
		subpanelRoomListTeamButtons.add(btnRoomListTeamOK);

		// ****** Team name変更Cancel
		JButton btnRoomListTeamCancel = new JButton(getUIText("Lobby_TeamChange_Cancel"));
		btnRoomListTeamCancel.addActionListener(this);
		btnRoomListTeamCancel.setActionCommand("Lobby_TeamChange_Cancel");
		btnRoomListTeamCancel.setMnemonic('C');
		subpanelRoomListTeamButtons.add(btnRoomListTeamCancel);

		// *** ルーム一覧 table
		strTableColumnNames = new String[ROOMTABLE_COLUMNNAMES.length];
		for(int i = 0; i < strTableColumnNames.length; i++) {
			strTableColumnNames[i] = getUIText(ROOMTABLE_COLUMNNAMES[i]);
		}
		tablemodelRoomList = new DefaultTableModel(strTableColumnNames, 0);
		tableRoomList = new JTable(tablemodelRoomList);
		tableRoomList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableRoomList.setDefaultEditor(Object.class, null);
		tableRoomList.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tableRoomList.getTableHeader().setReorderingAllowed(false);
		tableRoomList.setComponentPopupMenu(new RoomTablePopupMenu());
		tableRoomList.addMouseListener(new RoomTableMouseAdapter());
		tableRoomList.addKeyListener(new RoomTableKeyAdapter());

		TableColumnModel tm = tableRoomList.getColumnModel();
		tm.getColumn(0).setPreferredWidth(propConfig.getProperty("tableRoomList.width.id", 35));			// ID
		tm.getColumn(1).setPreferredWidth(propConfig.getProperty("tableRoomList.width.name", 155));			// Name
		tm.getColumn(2).setPreferredWidth(propConfig.getProperty("tableRoomList.width.rated", 50));			// Rated
		tm.getColumn(3).setPreferredWidth(propConfig.getProperty("tableRoomList.width.rulename", 105));		// Rule name
		tm.getColumn(4).setPreferredWidth(propConfig.getProperty("tableRoomList.width.status", 55));		// Status
		tm.getColumn(5).setPreferredWidth(propConfig.getProperty("tableRoomList.width.players", 65));		// Players
		tm.getColumn(6).setPreferredWidth(propConfig.getProperty("tableRoomList.width.spectators", 65));	// Spectators

		JScrollPane spTableRoomList = new JScrollPane(tableRoomList);
		subpanelRoomList.add(spTableRoomList, BorderLayout.CENTER);

		// ** チャット(下)
		JPanel subpanelLobbyChat = new JPanel(new BorderLayout());
		subpanelLobbyChat.setMinimumSize(new Dimension(0,0));
		splitLobby.setBottomComponent(subpanelLobbyChat);

		// *** チャットログとPlayerリストの仕切り線
		splitLobbyChat = new JSplitPane();
		splitLobbyChat.setDividerLocation(propConfig.getProperty("lobby.splitLobbyChat.location", 350));
		subpanelLobbyChat.add(splitLobbyChat, BorderLayout.CENTER);

		// **** チャットログ(Lobby screen)
		txtpaneLobbyChatLog = new JTextPane();
		txtpaneLobbyChatLog.setComponentPopupMenu(new LogPopupMenu(txtpaneLobbyChatLog));
		txtpaneLobbyChatLog.addKeyListener(new LogKeyAdapter());
		JScrollPane spTxtpaneLobbyChatLog = new JScrollPane(txtpaneLobbyChatLog);
		spTxtpaneLobbyChatLog.setMinimumSize(new Dimension(0,0));
		splitLobbyChat.setLeftComponent(spTxtpaneLobbyChatLog);

		// **** Playerリスト(Lobby screen)
		listmodelLobbyChatPlayerList = new DefaultListModel();
		listboxLobbyChatPlayerList = new JList(listmodelLobbyChatPlayerList);
		listboxLobbyChatPlayerList.setComponentPopupMenu(new ListBoxPopupMenu(listboxLobbyChatPlayerList));
		JScrollPane spListboxLobbyChatPlayerList = new JScrollPane(listboxLobbyChatPlayerList);
		spListboxLobbyChatPlayerList.setMinimumSize(new Dimension(0, 0));
		splitLobbyChat.setRightComponent(spListboxLobbyChatPlayerList);

		// *** チャット input 欄パネル(Lobby screen)
		JPanel subpanelLobbyChatInputArea = new JPanel(new BorderLayout());
		subpanelLobbyChat.add(subpanelLobbyChatInputArea, BorderLayout.SOUTH);

		// **** チャット input 欄(Lobby screen)
		txtfldLobbyChatInput = new JTextField();
		txtfldLobbyChatInput.setComponentPopupMenu(new TextComponentPopupMenu(txtfldLobbyChatInput));
		subpanelLobbyChatInputArea.add(txtfldLobbyChatInput, BorderLayout.CENTER);

		// **** チャット送信 button(Lobby screen)
		btnLobbyChatSend = new JButton(getUIText("Lobby_ChatSend"));
		btnLobbyChatSend.addActionListener(this);
		btnLobbyChatSend.setActionCommand("Lobby_ChatSend");
		btnLobbyChatSend.setMnemonic('S');
		subpanelLobbyChatInputArea.add(btnLobbyChatSend, BorderLayout.EAST);

		// === Room Tab ===
		JPanel mainpanelRoom = new JPanel(new BorderLayout());
		//this.getContentPane().add(mainpanelRoom, SCREENCARD_NAMES[SCREENCARD_ROOM]);
		tabLobbyAndRoom.addTab(getUIText("Lobby_Tab_NoRoom"), mainpanelRoom);
		tabLobbyAndRoom.setMnemonicAt(1, 'R');
		tabLobbyAndRoom.setEnabledAt(1, false);

		// * 上下を分ける仕切り線
		splitRoom = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitRoom.setDividerLocation(propConfig.getProperty("room.splitRoom.location", 200));
		mainpanelRoom.add(splitRoom, BorderLayout.CENTER);

		// ** ゲーム結果一覧(上)
		JPanel subpanelRoomTop = new JPanel(new BorderLayout());
		subpanelRoomTop.setMinimumSize(new Dimension(0,0));
		splitRoom.setTopComponent(subpanelRoomTop);

		// *** ルーム画面上部パネル
		roomTopBarCardLayout = new CardLayout();
		subpanelRoomTopBar = new JPanel(roomTopBarCardLayout);
		subpanelRoomTop.add(subpanelRoomTopBar, BorderLayout.NORTH);

		// ****  button類パネル
		JPanel subpanelRoomButtons = new JPanel();
		subpanelRoomTopBar.add(subpanelRoomButtons, "Buttons");

		// ***** 退出 button
		JButton btnRoomButtonsLeave = new JButton(getUIText("Room_Leave"));
		btnRoomButtonsLeave.addActionListener(this);
		btnRoomButtonsLeave.setActionCommand("Room_Leave");
		btnRoomButtonsLeave.setMnemonic('L');
		subpanelRoomButtons.add(btnRoomButtonsLeave);

		// ***** 参戦 button
		btnRoomButtonsJoin = new JButton(getUIText("Room_Join"));
		btnRoomButtonsJoin.addActionListener(this);
		btnRoomButtonsJoin.setActionCommand("Room_Join");
		btnRoomButtonsJoin.setMnemonic('J');
		btnRoomButtonsJoin.setVisible(false);
		subpanelRoomButtons.add(btnRoomButtonsJoin);

		// ***** 離脱 button
		btnRoomButtonsSitOut = new JButton(getUIText("Room_SitOut"));
		btnRoomButtonsSitOut.addActionListener(this);
		btnRoomButtonsSitOut.setActionCommand("Room_SitOut");
		btnRoomButtonsSitOut.setMnemonic('O');
		btnRoomButtonsSitOut.setVisible(false);
		subpanelRoomButtons.add(btnRoomButtonsSitOut);

		// ***** チーム変更 button
		btnRoomButtonsTeamChange = new JButton(getUIText("Room_TeamChange"));
		btnRoomButtonsTeamChange.addActionListener(this);
		btnRoomButtonsTeamChange.setActionCommand("Room_TeamChange");
		btnRoomButtonsTeamChange.setMnemonic('T');
		subpanelRoomButtons.add(btnRoomButtonsTeamChange);

		// **** チーム変更パネル
		JPanel subpanelRoomTeam = new JPanel(new BorderLayout());
		subpanelRoomTopBar.add(subpanelRoomTeam, "Team");

		// ***** Team name input 欄
		txtfldRoomTeam = new JTextField();
		subpanelRoomTeam.add(txtfldRoomTeam, BorderLayout.CENTER);

		// ***** Team name変更 buttonパネル
		JPanel subpanelRoomTeamButtons = new JPanel();
		subpanelRoomTeam.add(subpanelRoomTeamButtons, BorderLayout.EAST);

		// ****** Team name変更OK
		JButton btnRoomTeamOK = new JButton(getUIText("Room_TeamChange_OK"));
		btnRoomTeamOK.addActionListener(this);
		btnRoomTeamOK.setActionCommand("Room_TeamChange_OK");
		btnRoomTeamOK.setMnemonic('O');
		subpanelRoomTeamButtons.add(btnRoomTeamOK);

		// ****** Team name変更Cancel
		JButton btnRoomTeamCancel = new JButton(getUIText("Room_TeamChange_Cancel"));
		btnRoomTeamCancel.addActionListener(this);
		btnRoomTeamCancel.setActionCommand("Room_TeamChange_Cancel");
		btnRoomTeamCancel.setMnemonic('C');
		subpanelRoomTeamButtons.add(btnRoomTeamCancel);

		// ***** 設定確認 button
		btnRoomButtonsViewSetting = new JButton(getUIText("Room_ViewSetting"));
		btnRoomButtonsViewSetting.addActionListener(this);
		btnRoomButtonsViewSetting.setActionCommand("Room_ViewSetting");
		btnRoomButtonsViewSetting.setMnemonic('V');
		subpanelRoomButtons.add(btnRoomButtonsViewSetting);

		// ***** Leaderboard button
		btnRoomButtonsRanking = new JButton(getUIText("Room_Ranking"));
		btnRoomButtonsRanking.addActionListener(this);
		btnRoomButtonsRanking.setActionCommand("Room_Ranking");
		btnRoomButtonsRanking.setMnemonic('L');
		btnRoomButtonsRanking.setVisible(false);
		subpanelRoomButtons.add(btnRoomButtonsRanking);

		// *** ゲーム結果一覧 table
		strGameStatTableColumnNames = new String[STATTABLE_COLUMNNAMES.length];
		for(int i = 0; i < strGameStatTableColumnNames.length; i++) {
			strGameStatTableColumnNames[i] = getUIText(STATTABLE_COLUMNNAMES[i]);
		}
		tablemodelGameStat = new DefaultTableModel(strGameStatTableColumnNames, 0);
		tableGameStat = new JTable(tablemodelGameStat);
		tableGameStat.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableGameStat.setDefaultEditor(Object.class, null);
		tableGameStat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tableGameStat.getTableHeader().setReorderingAllowed(false);

		TableColumnModel tm2 = tableGameStat.getColumnModel();
		tm2.getColumn(0).setPreferredWidth(propConfig.getProperty("tableGameStat.width.rank", 30));			// 順位
		tm2.getColumn(1).setPreferredWidth(propConfig.getProperty("tableGameStat.width.name", 100));			// Name
		tm2.getColumn(2).setPreferredWidth(propConfig.getProperty("tableGameStat.width.attack", 55));		//  Attack count
		tm2.getColumn(3).setPreferredWidth(propConfig.getProperty("tableGameStat.width.apm", 55));			// APM
		tm2.getColumn(4).setPreferredWidth(propConfig.getProperty("tableGameStat.width.lines", 55));			// 消去count
		tm2.getColumn(5).setPreferredWidth(propConfig.getProperty("tableGameStat.width.lpm", 55));			// LPM
		tm2.getColumn(6).setPreferredWidth(propConfig.getProperty("tableGameStat.width.piece", 55));			// ピースcount
		tm2.getColumn(7).setPreferredWidth(propConfig.getProperty("tableGameStat.width.pps", 55));			// PPS
		tm2.getColumn(8).setPreferredWidth(propConfig.getProperty("tableGameStat.width.time", 65));			//  time
		tm2.getColumn(9).setPreferredWidth(propConfig.getProperty("tableGameStat.width.ko", 40));			// KO
		tm2.getColumn(10).setPreferredWidth(propConfig.getProperty("tableGameStat.width.wins", 55));			// 勝count
		tm2.getColumn(11).setPreferredWidth(propConfig.getProperty("tableGameStat.width.games", 55));		//  count

		JScrollPane spTableGameStat = new JScrollPane(tableGameStat);
		subpanelRoomTop.add(spTableGameStat, BorderLayout.CENTER);

		// ** チャットパネル(下)
		JPanel subpanelRoomChat = new JPanel(new BorderLayout());
		subpanelRoomChat.setMinimumSize(new Dimension(0,0));
		splitRoom.setBottomComponent(subpanelRoomChat);

		// *** チャットログとPlayerリストの仕切り線(Room screen)
		splitRoomChat = new JSplitPane();
		splitRoomChat.setDividerLocation(propConfig.getProperty("room.splitRoomChat.location", 350));
		subpanelRoomChat.add(splitRoomChat, BorderLayout.CENTER);

		// **** チャットログ(Room screen)
		txtpaneRoomChatLog = new JTextPane();
		txtpaneRoomChatLog.setComponentPopupMenu(new LogPopupMenu(txtpaneRoomChatLog));
		txtpaneRoomChatLog.addKeyListener(new LogKeyAdapter());
		JScrollPane spTxtpaneRoomChatLog = new JScrollPane(txtpaneRoomChatLog);
		spTxtpaneRoomChatLog.setMinimumSize(new Dimension(0,0));
		splitRoomChat.setLeftComponent(spTxtpaneRoomChatLog);

		// **** Playerリスト(Room screen)
		sameRoomPlayerInfoList = new LinkedList<NetPlayerInfo>();
		listmodelRoomChatPlayerList = new DefaultListModel();
		listboxRoomChatPlayerList = new JList(listmodelRoomChatPlayerList);
		listboxRoomChatPlayerList.setComponentPopupMenu(new ListBoxPopupMenu(listboxRoomChatPlayerList));
		JScrollPane spListboxRoomChatPlayerList = new JScrollPane(listboxRoomChatPlayerList);
		spListboxRoomChatPlayerList.setMinimumSize(new Dimension(0,0));
		splitRoomChat.setRightComponent(spListboxRoomChatPlayerList);

		// *** チャット input 欄パネル(Room screen)
		JPanel subpanelRoomChatInputArea = new JPanel(new BorderLayout());
		subpanelRoomChat.add(subpanelRoomChatInputArea, BorderLayout.SOUTH);

		// **** チャット input 欄(Room screen)
		txtfldRoomChatInput = new JTextField();
		txtfldRoomChatInput.setComponentPopupMenu(new TextComponentPopupMenu(txtfldRoomChatInput));
		subpanelRoomChatInputArea.add(txtfldRoomChatInput, BorderLayout.CENTER);

		// **** チャット送信 button(Room screen)
		btnRoomChatSend = new JButton(getUIText("Room_ChatSend"));
		btnRoomChatSend.addActionListener(this);
		btnRoomChatSend.setActionCommand("Room_ChatSend");
		btnRoomChatSend.setMnemonic('S');
		subpanelRoomChatInputArea.add(btnRoomChatSend, BorderLayout.EAST);
	}

	/**
	 * Server-add screen initialization
	 */
	protected void initServerAddUI() {
		// サーバー追加画面
		JPanel mainpanelServerAdd = new JPanel(new BorderLayout());
		this.getContentPane().add(mainpanelServerAdd, SCREENCARD_NAMES[SCREENCARD_SERVERADD]);

		// * サーバー追加画面パネル(そのまま追加すると縦に引き伸ばされてしまうのでパネルをもう1枚使う)
		JPanel containerpanelServerAdd = new JPanel();
		containerpanelServerAdd.setLayout(new BoxLayout(containerpanelServerAdd, BoxLayout.Y_AXIS));
		mainpanelServerAdd.add(containerpanelServerAdd, BorderLayout.NORTH);

		// ** ホスト名パネル
		JPanel subpanelHost = new JPanel(new BorderLayout());
		containerpanelServerAdd.add(subpanelHost);

		// *** 「ホスト名またはIPアドレス:」ラベル
		JLabel labelHost = new JLabel(getUIText("ServerAdd_Host"));
		subpanelHost.add(labelHost, BorderLayout.WEST);

		// *** ホスト名 input 欄
		txtfldServerAddHost = new JTextField();
		txtfldServerAddHost.setComponentPopupMenu(new TextComponentPopupMenu(txtfldServerAddHost));
		subpanelHost.add(txtfldServerAddHost, BorderLayout.CENTER);

		// **  button類パネル
		JPanel subpanelButtons = new JPanel();
		subpanelButtons.setLayout(new BoxLayout(subpanelButtons, BoxLayout.X_AXIS));
		containerpanelServerAdd.add(subpanelButtons);

		// *** OK button
		btnServerAddOK = new JButton(getUIText("ServerAdd_OK"));
		btnServerAddOK.addActionListener(this);
		btnServerAddOK.setActionCommand("ServerAdd_OK");
		btnServerAddOK.setMnemonic('O');
		btnServerAddOK.setMaximumSize(new Dimension(Short.MAX_VALUE, btnServerAddOK.getMaximumSize().height));
		subpanelButtons.add(btnServerAddOK);

		// *** Cancel button
		JButton btnServerAddCancel = new JButton(getUIText("ServerAdd_Cancel"));
		btnServerAddCancel.addActionListener(this);
		btnServerAddCancel.setActionCommand("ServerAdd_Cancel");
		btnServerAddCancel.setMnemonic('C');
		btnServerAddCancel.setMaximumSize(new Dimension(Short.MAX_VALUE, btnServerAddCancel.getMaximumSize().height));
		subpanelButtons.add(btnServerAddCancel);
	}

	/**
	 * Create room scren initialization
	 */
	protected void initCreateRoomUI() {
		// ルーム作成画面
		JPanel mainpanelCreateRoom = new JPanel(new BorderLayout());
		this.getContentPane().add(mainpanelCreateRoom, SCREENCARD_NAMES[SCREENCARD_CREATEROOM]);

		// タブ
		JTabbedPane tabbedPane = new JTabbedPane();
		mainpanelCreateRoom.add(tabbedPane, BorderLayout.CENTER);

		// tabs

		// * 基本設定パネル
		JPanel containerpanelCreateRoomMainOwner = new JPanel(new BorderLayout());
		tabbedPane.addTab(getUIText("CreateRoom_Tab_Main"), containerpanelCreateRoomMainOwner);

		// * 速度設定パネル(引き伸ばし防止用)
		JPanel containerpanelCreateRoomSpeedOwner = new JPanel(new BorderLayout());
		tabbedPane.addTab(getUIText("CreateRoom_Tab_Speed"), containerpanelCreateRoomSpeedOwner);

		// * Bonus tab
		JPanel containerpanelCreateRoomBonusOwner = new JPanel(new BorderLayout());
		tabbedPane.addTab(getUIText("CreateRoom_Tab_Bonus"), containerpanelCreateRoomBonusOwner);

		// * Garbage tab
		JPanel containerpanelCreateRoomGarbageOwner = new JPanel(new BorderLayout());
		tabbedPane.addTab(getUIText("CreateRoom_Tab_Garbage"), containerpanelCreateRoomGarbageOwner);

		// * Miscellaneous tab
		JPanel containerpanelCreateRoomMiscOwner = new JPanel(new BorderLayout());
		tabbedPane.addTab(getUIText("CreateRoom_Tab_Misc"), containerpanelCreateRoomMiscOwner);

		// * Rated Rule tab
		JPanel containerpanelCreateRoomRatedRuleOwner = new JPanel(new BorderLayout());
		tabbedPane.addTab(getUIText("CreateRoom_Tab_RatedRule"), containerpanelCreateRoomRatedRuleOwner);

		// general tab

		// * 速度設定パネル(本体)
		JPanel containerpanelCreateRoomMain = new JPanel();
		containerpanelCreateRoomMain.setLayout(new BoxLayout(containerpanelCreateRoomMain, BoxLayout.Y_AXIS));
		containerpanelCreateRoomMainOwner.add(containerpanelCreateRoomMain, BorderLayout.NORTH);

		// ** ルーム名パネル
		JPanel subpanelName = new JPanel(new BorderLayout());
		containerpanelCreateRoomMain.add(subpanelName);

		// *** 「ルーム名:」ラベル
		JLabel labelName = new JLabel(getUIText("CreateRoom_Name"));
		subpanelName.add(labelName, BorderLayout.WEST);

		// *** ルーム名 input 欄
		txtfldCreateRoomName = new JTextField();
		txtfldCreateRoomName.setComponentPopupMenu(new TextComponentPopupMenu(txtfldCreateRoomName));
		txtfldCreateRoomName.setToolTipText(getUIText("CreateRoom_Name_Tip"));
		subpanelName.add(txtfldCreateRoomName, BorderLayout.CENTER);

		// ** 参加人countパネル
		JPanel subpanelMaxPlayers = new JPanel(new BorderLayout());
		containerpanelCreateRoomMain.add(subpanelMaxPlayers);

		// *** 「参加人count:」ラベル
		JLabel labelMaxPlayers = new JLabel(getUIText("CreateRoom_MaxPlayers"));
		subpanelMaxPlayers.add(labelMaxPlayers, BorderLayout.WEST);

		// *** 参加人count選択
		int defaultMaxPlayers = propConfig.getProperty("createroom.defaultMaxPlayers", 6);
		spinnerCreateRoomMaxPlayers = new JSpinner(new SpinnerNumberModel(defaultMaxPlayers, 1, 6, 1));
		spinnerCreateRoomMaxPlayers.setPreferredSize(new Dimension(200, 20));
		spinnerCreateRoomMaxPlayers.setToolTipText(getUIText("CreateRoom_MaxPlayers_Tip"));
		subpanelMaxPlayers.add(spinnerCreateRoomMaxPlayers, BorderLayout.EAST);

		// ** Hurryup秒countパネル
		JPanel subpanelHurryupSeconds = new JPanel(new BorderLayout());
		containerpanelCreateRoomMain.add(subpanelHurryupSeconds);

		// *** 「HURRY UP開始までの秒count:」ラベル
		JLabel labelHurryupSeconds = new JLabel(getUIText("CreateRoom_HurryupSeconds"));
		subpanelHurryupSeconds.add(labelHurryupSeconds, BorderLayout.WEST);

		// *** Hurryup秒count
		int defaultHurryupSeconds = propConfig.getProperty("createroom.defaultHurryupSeconds", 90);
		spinnerCreateRoomHurryupSeconds = new JSpinner(new SpinnerNumberModel(defaultHurryupSeconds, -1, 999, 1));
		spinnerCreateRoomHurryupSeconds.setPreferredSize(new Dimension(200, 20));
		spinnerCreateRoomHurryupSeconds.setToolTipText(getUIText("CreateRoom_HurryupSeconds_Tip"));
		subpanelHurryupSeconds.add(spinnerCreateRoomHurryupSeconds, BorderLayout.EAST);

		// ** Hurryup間隔パネル
		JPanel subpanelHurryupInterval = new JPanel(new BorderLayout());
		containerpanelCreateRoomMain.add(subpanelHurryupInterval);

		// *** 「HURRY UP後, 床をせり上げる間隔:」ラベル
		JLabel labelHurryupInterval = new JLabel(getUIText("CreateRoom_HurryupInterval"));
		subpanelHurryupInterval.add(labelHurryupInterval, BorderLayout.WEST);

		// *** Hurryup間隔
		int defaultHurryupInterval = propConfig.getProperty("createroom.defaultHurryupInterval", 5);
		spinnerCreateRoomHurryupInterval = new JSpinner(new SpinnerNumberModel(defaultHurryupInterval, 1, 99, 1));
		spinnerCreateRoomHurryupInterval.setPreferredSize(new Dimension(200, 20));
		spinnerCreateRoomHurryupInterval.setToolTipText(getUIText("CreateRoom_HurryupInterval_Tip"));
		subpanelHurryupInterval.add(spinnerCreateRoomHurryupInterval, BorderLayout.EAST);

		// ** MapセットIDパネル
		JPanel subpanelMapSetID = new JPanel(new BorderLayout());
		containerpanelCreateRoomMain.add(subpanelMapSetID);

		// *** 「MapセットID:」ラベル
		JLabel labelMapSetID = new JLabel(getUIText("CreateRoom_MapSetID"));
		subpanelMapSetID.add(labelMapSetID, BorderLayout.WEST);

		// *** MapセットID
		int defaultMapSetID = propConfig.getProperty("createroom.defaultMapSetID", 0);
		spinnerCreateRoomMapSetID = new JSpinner(new SpinnerNumberModel(defaultMapSetID, 0, 99, 1));
		spinnerCreateRoomMapSetID.setPreferredSize(new Dimension(200, 20));
		spinnerCreateRoomMapSetID.setToolTipText(getUIText("CreateRoom_MapSetID_Tip"));
		subpanelMapSetID.add(spinnerCreateRoomMapSetID, BorderLayout.EAST);

		// ** Map is enabled
		chkboxCreateRoomUseMap = new JCheckBox(getUIText("CreateRoom_UseMap"));
		chkboxCreateRoomUseMap.setMnemonic('P');
		chkboxCreateRoomUseMap.setSelected(propConfig.getProperty("createroom.defaultUseMap", false));
		chkboxCreateRoomUseMap.setToolTipText(getUIText("CreateRoom_UseMap_Tip"));
		containerpanelCreateRoomMain.add(chkboxCreateRoomUseMap);

		// ** 全員のルール固定
		chkboxCreateRoomRuleLock = new JCheckBox(getUIText("CreateRoom_RuleLock"));
		chkboxCreateRoomRuleLock.setMnemonic('L');
		chkboxCreateRoomRuleLock.setSelected(propConfig.getProperty("createroom.defaultRuleLock", false));
		chkboxCreateRoomRuleLock.setToolTipText(getUIText("CreateRoom_RuleLock_Tip"));
		containerpanelCreateRoomMain.add(chkboxCreateRoomRuleLock);

		// speed tab

		// * 速度設定パネル(本体)
		JPanel containerpanelCreateRoomSpeed = new JPanel();
		containerpanelCreateRoomSpeed.setLayout(new BoxLayout(containerpanelCreateRoomSpeed, BoxLayout.Y_AXIS));
		containerpanelCreateRoomSpeedOwner.add(containerpanelCreateRoomSpeed, BorderLayout.NORTH);

		// ** 落下速度(分子)パネル
		JPanel subpanelGravity = new JPanel(new BorderLayout());
		containerpanelCreateRoomSpeed.add(subpanelGravity);

		// *** 「落下速度(分子):」ラベル
		JLabel labelGravity = new JLabel(getUIText("CreateRoom_Gravity"));
		subpanelGravity.add(labelGravity, BorderLayout.WEST);

		// *** 落下速度(分子)
		int defaultGravity = propConfig.getProperty("createroom.defaultGravity", 1);
		spinnerCreateRoomGravity = new JSpinner(new SpinnerNumberModel(defaultGravity, -1, 99999, 1));
		spinnerCreateRoomGravity.setPreferredSize(new Dimension(200, 20));
		subpanelGravity.add(spinnerCreateRoomGravity, BorderLayout.EAST);

		// ** 落下速度(分母)パネル
		JPanel subpanelDenominator = new JPanel(new BorderLayout());
		containerpanelCreateRoomSpeed.add(subpanelDenominator);

		// *** 「落下速度(分母):」ラベル
		JLabel labelDenominator = new JLabel(getUIText("CreateRoom_Denominator"));
		subpanelDenominator.add(labelDenominator, BorderLayout.WEST);

		// *** 落下速度(分母)
		int defaultDenominator = propConfig.getProperty("createroom.defaultDenominator", 60);
		spinnerCreateRoomDenominator = new JSpinner(new SpinnerNumberModel(defaultDenominator, 0, 99999, 1));
		spinnerCreateRoomDenominator.setPreferredSize(new Dimension(200, 20));
		subpanelDenominator.add(spinnerCreateRoomDenominator, BorderLayout.EAST);

		// ** AREパネル
		JPanel subpanelARE = new JPanel(new BorderLayout());
		containerpanelCreateRoomSpeed.add(subpanelARE);

		// *** 「ARE:」ラベル
		JLabel labelARE = new JLabel(getUIText("CreateRoom_ARE"));
		subpanelARE.add(labelARE, BorderLayout.WEST);

		// *** ARE
		int defaultARE = propConfig.getProperty("createroom.defaultARE", 30);
		spinnerCreateRoomARE = new JSpinner(new SpinnerNumberModel(defaultARE, 0, 99, 1));
		spinnerCreateRoomARE.setPreferredSize(new Dimension(200, 20));
		subpanelARE.add(spinnerCreateRoomARE, BorderLayout.EAST);

		// ** ARE after line clearパネル
		JPanel subpanelARELine = new JPanel(new BorderLayout());
		containerpanelCreateRoomSpeed.add(subpanelARELine);

		// *** 「ARE after line clear:」ラベル
		JLabel labelARELine = new JLabel(getUIText("CreateRoom_ARELine"));
		subpanelARELine.add(labelARELine, BorderLayout.WEST);

		// *** ARE after line clear
		int defaultARELine = propConfig.getProperty("createroom.defaultARELine", 30);
		spinnerCreateRoomARELine = new JSpinner(new SpinnerNumberModel(defaultARELine, 0, 99, 1));
		spinnerCreateRoomARELine.setPreferredSize(new Dimension(200, 20));
		subpanelARELine.add(spinnerCreateRoomARELine, BorderLayout.EAST);

		// ** Line clear timeパネル
		JPanel subpanelLineDelay = new JPanel(new BorderLayout());
		containerpanelCreateRoomSpeed.add(subpanelLineDelay);

		// *** 「Line clear time:」ラベル
		JLabel labelLineDelay = new JLabel(getUIText("CreateRoom_LineDelay"));
		subpanelLineDelay.add(labelLineDelay, BorderLayout.WEST);

		// *** Line clear time
		int defaultLineDelay = propConfig.getProperty("createroom.defaultLineDelay", 20);
		spinnerCreateRoomLineDelay = new JSpinner(new SpinnerNumberModel(defaultLineDelay, 0, 99, 1));
		spinnerCreateRoomLineDelay.setPreferredSize(new Dimension(200, 20));
		subpanelLineDelay.add(spinnerCreateRoomLineDelay, BorderLayout.EAST);

		// ** 固定 timeパネル
		JPanel subpanelLockDelay = new JPanel(new BorderLayout());
		containerpanelCreateRoomSpeed.add(subpanelLockDelay);

		// *** 「固定 time:」ラベル
		JLabel labelLockDelay = new JLabel(getUIText("CreateRoom_LockDelay"));
		subpanelLockDelay.add(labelLockDelay, BorderLayout.WEST);

		// *** 固定 time
		int defaultLockDelay = propConfig.getProperty("createroom.defaultLockDelay", 30);
		spinnerCreateRoomLockDelay = new JSpinner(new SpinnerNumberModel(defaultLockDelay, 0, 98, 1));
		spinnerCreateRoomLockDelay.setPreferredSize(new Dimension(200, 20));
		subpanelLockDelay.add(spinnerCreateRoomLockDelay, BorderLayout.EAST);

		// ** 横溜めパネル
		JPanel subpanelDAS = new JPanel(new BorderLayout());
		containerpanelCreateRoomSpeed.add(subpanelDAS);

		// *** 「横溜め:」ラベル
		JLabel labelDAS = new JLabel(getUIText("CreateRoom_DAS"));
		subpanelDAS.add(labelDAS, BorderLayout.WEST);

		// *** 横溜め
		int defaultDAS = propConfig.getProperty("createroom.defaultDAS", 14);
		spinnerCreateRoomDAS = new JSpinner(new SpinnerNumberModel(defaultDAS, 0, 99, 1));
		spinnerCreateRoomDAS.setPreferredSize(new Dimension(200, 20));
		subpanelDAS.add(spinnerCreateRoomDAS, BorderLayout.EAST);

		// bonus tab

		// bonus panel
		JPanel containerpanelCreateRoomBonus = new JPanel();
		containerpanelCreateRoomBonus.setLayout(new BoxLayout(containerpanelCreateRoomBonus, BoxLayout.Y_AXIS));
		containerpanelCreateRoomBonusOwner.add(containerpanelCreateRoomBonus, BorderLayout.NORTH);

		// ** スピン bonusパネル
		JPanel subpanelTSpinEnableType = new JPanel(new BorderLayout());
		containerpanelCreateRoomBonus.add(subpanelTSpinEnableType);

		// *** 「スピン bonus:」ラベル
		JLabel labelTSpinEnableType = new JLabel(getUIText("CreateRoom_TSpinEnableType"));
		subpanelTSpinEnableType.add(labelTSpinEnableType, BorderLayout.WEST);

		// *** スピン bonus
		String[] strSpinBonusNames = new String[COMBOBOX_SPINBONUS_NAMES.length];
		for(int i = 0; i < strSpinBonusNames.length; i++) {
			strSpinBonusNames[i] = getUIText(COMBOBOX_SPINBONUS_NAMES[i]);
		}
		comboboxCreateRoomTSpinEnableType = new JComboBox(strSpinBonusNames);
		comboboxCreateRoomTSpinEnableType.setSelectedIndex(propConfig.getProperty("createroom.defaultTSpinEnableType", 1));
		comboboxCreateRoomTSpinEnableType.setPreferredSize(new Dimension(200, 20));
		comboboxCreateRoomTSpinEnableType.setToolTipText(getUIText("CreateRoom_TSpinEnableType_Tip"));
		subpanelTSpinEnableType.add(comboboxCreateRoomTSpinEnableType, BorderLayout.EAST);

		// ** Spin check type panel
		JPanel subpanelSpinCheckType = new JPanel(new BorderLayout());
		containerpanelCreateRoomBonus.add(subpanelSpinCheckType);

		// *** Spin check type label
		JLabel labelSpinCheckType = new JLabel(getUIText("CreateRoom_SpinCheckType"));
		subpanelSpinCheckType.add(labelSpinCheckType, BorderLayout.WEST);

		// *** Spin check type combobox
		String[] strSpinCheckTypeNames = new String[COMBOBOX_SPINCHECKTYPE_NAMES.length];
		for(int i = 0; i < strSpinCheckTypeNames.length; i++) {
			strSpinCheckTypeNames[i] = getUIText(COMBOBOX_SPINCHECKTYPE_NAMES[i]);
		}
		comboboxCreateRoomSpinCheckType = new JComboBox(strSpinCheckTypeNames);
		comboboxCreateRoomSpinCheckType.setSelectedIndex(propConfig.getProperty("createroom.defaultSpinCheckType", 0));
		comboboxCreateRoomSpinCheckType.setPreferredSize(new Dimension(200, 20));
		comboboxCreateRoomSpinCheckType.setToolTipText(getUIText("CreateRoom_SpinCheckType_Tip"));
		subpanelSpinCheckType.add(comboboxCreateRoomSpinCheckType, BorderLayout.EAST);

		// ** EZ Spin checkbox
		chkboxCreateRoomTSpinEnableEZ = new JCheckBox(getUIText("CreateRoom_TSpinEnableEZ"));
		chkboxCreateRoomTSpinEnableEZ.setMnemonic('E');
		chkboxCreateRoomTSpinEnableEZ.setSelected(propConfig.getProperty("createroom.defaultTSpinEnableEZ", false));
		chkboxCreateRoomTSpinEnableEZ.setToolTipText(getUIText("CreateRoom_TSpinEnableEZ_Tip"));
		containerpanelCreateRoomBonus.add(chkboxCreateRoomTSpinEnableEZ);

		// ** Flag for enabling B2B
		chkboxCreateRoomB2B = new JCheckBox(getUIText("CreateRoom_B2B"));
		chkboxCreateRoomB2B.setMnemonic('B');
		chkboxCreateRoomB2B.setSelected(propConfig.getProperty("createroom.defaultB2B", true));
		chkboxCreateRoomB2B.setToolTipText(getUIText("CreateRoom_B2B_Tip"));
		containerpanelCreateRoomBonus.add(chkboxCreateRoomB2B);

		// ** Flag for enabling combos
		chkboxCreateRoomCombo = new JCheckBox(getUIText("CreateRoom_Combo"));
		chkboxCreateRoomCombo.setMnemonic('M');
		chkboxCreateRoomCombo.setSelected(propConfig.getProperty("createroom.defaultCombo", true));
		chkboxCreateRoomCombo.setToolTipText(getUIText("CreateRoom_Combo_Tip"));
		containerpanelCreateRoomBonus.add(chkboxCreateRoomCombo);

		// ** Bravo bonus
		chkboxCreateRoomBravo = new JCheckBox(getUIText("CreateRoom_Bravo"));
		chkboxCreateRoomBravo.setMnemonic('A');
		chkboxCreateRoomBravo.setSelected(propConfig.getProperty("createroom.defaultBravo", true));
		chkboxCreateRoomBravo.setToolTipText(getUIText("CreateRoom_Bravo_Tip"));
		containerpanelCreateRoomBonus.add(chkboxCreateRoomBravo);

		// garbage tab

		// garbage panel
		JPanel containerpanelCreateRoomGarbage = new JPanel();
		containerpanelCreateRoomGarbage.setLayout(new BoxLayout(containerpanelCreateRoomGarbage, BoxLayout.Y_AXIS));
		containerpanelCreateRoomGarbageOwner.add(containerpanelCreateRoomGarbage, BorderLayout.NORTH);

		// ** Garbage change rate panel
		JPanel subpanelGarbagePercent = new JPanel(new BorderLayout());
		containerpanelCreateRoomGarbage.add(subpanelGarbagePercent);

		// ** Label for garbage change rate
		JLabel labelGarbagePercent = new JLabel(getUIText("CreateRoom_GarbagePercent"));
		subpanelGarbagePercent.add(labelGarbagePercent, BorderLayout.WEST);

		// ** Spinner for garbage change rate
		int defaultGarbagePercent = propConfig.getProperty("createroom.defaultGarbagePercent", 100);
		spinnerCreateRoomGarbagePercent = new JSpinner(new SpinnerNumberModel(defaultGarbagePercent, 0, 100, 10));
		spinnerCreateRoomGarbagePercent.setPreferredSize(new Dimension(200, 20));
		spinnerCreateRoomGarbagePercent.setToolTipText(getUIText("CreateRoom_GarbagePercent_Tip"));
		subpanelGarbagePercent.add(spinnerCreateRoomGarbagePercent, BorderLayout.EAST);

		// ** Set garbage type
		chkboxCreateRoomGarbageChangePerAttack = new JCheckBox(getUIText("CreateRoom_GarbageChangePerAttack"));
		chkboxCreateRoomGarbageChangePerAttack.setMnemonic('G');
		chkboxCreateRoomGarbageChangePerAttack.setSelected(propConfig.getProperty("createroom.defaultGarbageChangePerAttack", true));
		chkboxCreateRoomGarbageChangePerAttack.setToolTipText(getUIText("CreateRoom_GarbageChangePerAttack_Tip"));
		containerpanelCreateRoomGarbage.add(chkboxCreateRoomGarbageChangePerAttack);

		// ** B2B chunk
		chkboxCreateRoomB2BChunk = new JCheckBox(getUIText("CreateRoom_B2BChunk"));
		chkboxCreateRoomB2BChunk.setMnemonic('B');
		chkboxCreateRoomB2BChunk.setSelected(propConfig.getProperty("createroom.defaultB2BChunk", false));
		chkboxCreateRoomB2BChunk.setToolTipText(getUIText("CreateRoom_B2BChunk_Tip"));
		containerpanelCreateRoomGarbage.add(chkboxCreateRoomB2BChunk);

		// ** Rensa/Combo Block
		chkboxCreateRoomRensaBlock = new JCheckBox(getUIText("CreateRoom_RensaBlock"));
		chkboxCreateRoomRensaBlock.setMnemonic('E');
		chkboxCreateRoomRensaBlock.setSelected(propConfig.getProperty("createroom.defaultRensaBlock", true));
		chkboxCreateRoomRensaBlock.setToolTipText(getUIText("CreateRoom_RensaBlock_Tip"));
		containerpanelCreateRoomGarbage.add(chkboxCreateRoomRensaBlock);

		// ** Garbage countering
		chkboxCreateRoomCounter = new JCheckBox(getUIText("CreateRoom_Counter"));
		chkboxCreateRoomCounter.setMnemonic('C');
		chkboxCreateRoomCounter.setSelected(propConfig.getProperty("createroom.defaultCounter", true));
		chkboxCreateRoomCounter.setToolTipText(getUIText("CreateRoom_Counter_Tip"));
		containerpanelCreateRoomGarbage.add(chkboxCreateRoomCounter);

		// ** 3人以上生きている場合に Attack 力を減らす
		chkboxCreateRoomReduceLineSend = new JCheckBox(getUIText("CreateRoom_ReduceLineSend"));
		chkboxCreateRoomReduceLineSend.setMnemonic('R');
		chkboxCreateRoomReduceLineSend.setSelected(propConfig.getProperty("createroom.defaultReduceLineSend", false));
		chkboxCreateRoomReduceLineSend.setToolTipText(getUIText("CreateRoom_ReduceLineSend_Tip"));
		containerpanelCreateRoomGarbage.add(chkboxCreateRoomReduceLineSend);

		// ** 断片的garbage blockシステムを使う
		chkboxCreateRoomUseFractionalGarbage = new JCheckBox(getUIText("CreateRoom_UseFractionalGarbage"));
		chkboxCreateRoomUseFractionalGarbage.setMnemonic('F');
		chkboxCreateRoomUseFractionalGarbage.setSelected(propConfig.getProperty("createroom.defaultUseFractionalGarbage", false));
		chkboxCreateRoomUseFractionalGarbage.setToolTipText(getUIText("CreateRoom_UseFractionalGarbage_Tip"));
		containerpanelCreateRoomGarbage.add(chkboxCreateRoomUseFractionalGarbage);

		// misc tab

		// misc panel
		JPanel containerpanelCreateRoomMisc = new JPanel();
		containerpanelCreateRoomMisc.setLayout(new BoxLayout(containerpanelCreateRoomMisc, BoxLayout.Y_AXIS));
		containerpanelCreateRoomMiscOwner.add(containerpanelCreateRoomMisc, BorderLayout.NORTH);

		// ** 自動開始前の待機 timeパネル
		JPanel subpanelAutoStartSeconds = new JPanel(new BorderLayout());
		containerpanelCreateRoomMisc.add(subpanelAutoStartSeconds);

		// *** 「自動開始前の待機 time:」ラベル
		JLabel labelAutoStartSeconds = new JLabel(getUIText("CreateRoom_AutoStartSeconds"));
		subpanelAutoStartSeconds.add(labelAutoStartSeconds, BorderLayout.WEST);

		// *** 自動開始前の待機 time
		int defaultAutoStartSeconds = propConfig.getProperty("createroom.defaultAutoStartSeconds", 15);
		spinnerCreateRoomAutoStartSeconds = new JSpinner(new SpinnerNumberModel(defaultAutoStartSeconds, 0, 999, 1));
		spinnerCreateRoomAutoStartSeconds.setPreferredSize(new Dimension(200, 20));
		spinnerCreateRoomAutoStartSeconds.setToolTipText(getUIText("CreateRoom_AutoStartSeconds_Tip"));
		subpanelAutoStartSeconds.add(spinnerCreateRoomAutoStartSeconds, BorderLayout.EAST);

		// ** TNET2タイプのAutomatically start timerを使う
		chkboxCreateRoomAutoStartTNET2 = new JCheckBox(getUIText("CreateRoom_AutoStartTNET2"));
		chkboxCreateRoomAutoStartTNET2.setMnemonic('A');
		chkboxCreateRoomAutoStartTNET2.setSelected(propConfig.getProperty("createroom.defaultAutoStartTNET2", false));
		chkboxCreateRoomAutoStartTNET2.setToolTipText(getUIText("CreateRoom_AutoStartTNET2_Tip"));
		containerpanelCreateRoomMisc.add(chkboxCreateRoomAutoStartTNET2);

		// ** 誰かCancelしたらTimer無効化
		chkboxCreateRoomDisableTimerAfterSomeoneCancelled = new JCheckBox(getUIText("CreateRoom_DisableTimerAfterSomeoneCancelled"));
		chkboxCreateRoomDisableTimerAfterSomeoneCancelled.setMnemonic('D');
		chkboxCreateRoomDisableTimerAfterSomeoneCancelled.setSelected(propConfig.getProperty("createroom.defaultDisableTimerAfterSomeoneCancelled", false));
		chkboxCreateRoomDisableTimerAfterSomeoneCancelled.setToolTipText(getUIText("CreateRoom_DisableTimerAfterSomeoneCancelled_Tip"));
		containerpanelCreateRoomMisc.add(chkboxCreateRoomDisableTimerAfterSomeoneCancelled);

		// Rated Rule tab
		JPanel containerpanelCreateRoomRatedRule = new JPanel(new BorderLayout());
		containerpanelCreateRoomRatedRuleOwner.add(containerpanelCreateRoomRatedRule);

		listmodelCreateRoomRuleList = new DefaultListModel();
		listboxCreateRoomRuleList = new JList(listmodelCreateRoomRuleList);
		JScrollPane spCreateRoomRuleList = new JScrollPane(listboxCreateRoomRuleList);
		containerpanelCreateRoomRatedRule.add(spCreateRoomRuleList, BorderLayout.CENTER);

		// buttons

		// **  button類パネル
		JPanel subpanelButtons = new JPanel();
		subpanelButtons.setLayout(new BoxLayout(subpanelButtons, BoxLayout.X_AXIS));
		//containerpanelCreateRoom.add(subpanelButtons);
		mainpanelCreateRoom.add(subpanelButtons, BorderLayout.SOUTH);

		// *** OK button
		btnCreateRoomOK = new JButton(getUIText("CreateRoom_OK"));
		btnCreateRoomOK.addActionListener(this);
		btnCreateRoomOK.setActionCommand("CreateRoom_OK");
		btnCreateRoomOK.setMnemonic('O');
		btnCreateRoomOK.setMaximumSize(new Dimension(Short.MAX_VALUE, btnCreateRoomOK.getMaximumSize().height));
		subpanelButtons.add(btnCreateRoomOK);

		// *** 参戦 button
		btnCreateRoomJoin = new JButton(getUIText("CreateRoom_Join"));
		btnCreateRoomJoin.addActionListener(this);
		btnCreateRoomJoin.setActionCommand("CreateRoom_Join");
		btnCreateRoomJoin.setMnemonic('J');
		btnCreateRoomJoin.setMaximumSize(new Dimension(Short.MAX_VALUE, btnCreateRoomJoin.getMaximumSize().height));
		subpanelButtons.add(btnCreateRoomJoin);

		// *** 参戦 button
		btnCreateRoomWatch = new JButton(getUIText("CreateRoom_Watch"));
		btnCreateRoomWatch.addActionListener(this);
		btnCreateRoomWatch.setActionCommand("CreateRoom_Watch");
		btnCreateRoomWatch.setMnemonic('W');
		btnCreateRoomWatch.setMaximumSize(new Dimension(Short.MAX_VALUE, btnCreateRoomWatch.getMaximumSize().height));
		subpanelButtons.add(btnCreateRoomWatch);

		// *** Cancel Button
		btnCreateRoomCancel = new JButton(getUIText("CreateRoom_Cancel"));
		btnCreateRoomCancel.addActionListener(this);
		btnCreateRoomCancel.setActionCommand("CreateRoom_Cancel");
		btnCreateRoomCancel.setMnemonic('C');
		btnCreateRoomCancel.setMaximumSize(new Dimension(Short.MAX_VALUE, btnCreateRoomCancel.getMaximumSize().height));
		subpanelButtons.add(btnCreateRoomCancel);
	}

	/**
	 * Create room (1P) screen initialization
	 */
	protected void initCreateRoom1PUI() {
		// Main panel for Create room 1P
		JPanel mainpanelCreateRoom1P = new JPanel();
		mainpanelCreateRoom1P.setLayout(new BoxLayout(mainpanelCreateRoom1P, BoxLayout.Y_AXIS));
		this.getContentPane().add(mainpanelCreateRoom1P, SCREENCARD_NAMES[SCREENCARD_CREATEROOM1P]);

		// * Game mode panel
		JPanel pModeList = new JPanel(new BorderLayout());
		mainpanelCreateRoom1P.add(pModeList);

		// ** "Game Mode:" label
		JLabel lCreateRoom1P = new JLabel(getUIText("CreateRoom1P_Mode_Label"));
		pModeList.add(lCreateRoom1P, BorderLayout.NORTH);

		// ** Game mode listbox
		listmodelCreateRoom1PModeList = new DefaultListModel();
		loadListToDefaultListModel(listmodelCreateRoom1PModeList, "config/list/netlobby_singlemode.lst");

		listboxCreateRoom1PModeList = new JList(listmodelCreateRoom1PModeList);
		listboxCreateRoom1PModeList.setSelectedValue(propConfig.getProperty("createroom1p.listboxCreateRoom1PModeList.value", ""), true);
		JScrollPane spCreateRoom1PModeList = new JScrollPane(listboxCreateRoom1PModeList);
		pModeList.add(spCreateRoom1PModeList, BorderLayout.CENTER);

		// * Rule list panel
		JPanel pRuleList = new JPanel(new BorderLayout());
		mainpanelCreateRoom1P.add(pRuleList);

		// ** "Rule:" label
		JLabel lCreateRoom1PRuleList = new JLabel(getUIText("CreateRoom1P_Rule_Label"));
		pRuleList.add(lCreateRoom1PRuleList, BorderLayout.NORTH);

		// ** Rule list listbox
		listmodelCreateRoom1PRuleList = new DefaultListModel();
		listboxCreateRoom1PRuleList = new JList(listmodelCreateRoom1PRuleList);
		JScrollPane spCreateRoom1PRuleList = new JScrollPane(listboxCreateRoom1PRuleList);
		pRuleList.add(spCreateRoom1PRuleList, BorderLayout.CENTER);

		// * Buttons panel
		JPanel subpanelButtons = new JPanel();
		subpanelButtons.setLayout(new BoxLayout(subpanelButtons, BoxLayout.X_AXIS));
		mainpanelCreateRoom1P.add(subpanelButtons);

		// ** OK button
		btnCreateRoom1POK = new JButton(getUIText("CreateRoom1P_OK"));
		btnCreateRoom1POK.addActionListener(this);
		btnCreateRoom1POK.setActionCommand("CreateRoom1P_OK");
		btnCreateRoom1POK.setMnemonic('O');
		btnCreateRoom1POK.setMaximumSize(new Dimension(Short.MAX_VALUE, btnCreateRoom1POK.getMaximumSize().height));
		subpanelButtons.add(btnCreateRoom1POK);

		// ** Cancel button
		btnCreateRoom1PCancel = new JButton(getUIText("CreateRoom1P_Cancel"));
		btnCreateRoom1PCancel.addActionListener(this);
		btnCreateRoom1PCancel.setActionCommand("CreateRoom1P_Cancel");
		btnCreateRoom1PCancel.setMnemonic('C');
		btnCreateRoom1PCancel.setMaximumSize(new Dimension(Short.MAX_VALUE, btnCreateRoom1PCancel.getMaximumSize().height));
		subpanelButtons.add(btnCreateRoom1PCancel);
	}

	/**
	 * MPRanking screen initialization
	 */
	protected void initMPRankingUI() {
		// Main panel for MPRanking
		JPanel mainpanelMPRanking = new JPanel(new BorderLayout());
		this.getContentPane().add(mainpanelMPRanking, SCREENCARD_NAMES[SCREENCARD_MPRANKING]);

		// * Tab
		tabMPRanking = new JTabbedPane();
		mainpanelMPRanking.add(tabMPRanking, BorderLayout.CENTER);

		// ** Leaderboard Table
		strMPRankingTableColumnNames = new String[MPRANKING_COLUMNNAMES.length];
		for(int i = 0; i < strMPRankingTableColumnNames.length; i++) {
			strMPRankingTableColumnNames[i] = getUIText(MPRANKING_COLUMNNAMES[i]);
		}

		tableMPRanking = new JTable[GameEngine.MAX_GAMESTYLE];
		tablemodelMPRanking = new DefaultTableModel[GameEngine.MAX_GAMESTYLE];

		for(int i = 0; i < GameEngine.MAX_GAMESTYLE; i++) {
			tablemodelMPRanking[i] = new DefaultTableModel(strMPRankingTableColumnNames, 0);

			tableMPRanking[i] = new JTable(tablemodelMPRanking[i]);
			tableMPRanking[i].setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			tableMPRanking[i].setDefaultEditor(Object.class, null);
			tableMPRanking[i].setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			tableMPRanking[i].getTableHeader().setReorderingAllowed(false);

			TableColumnModel tm = tableMPRanking[i].getColumnModel();
			tm.getColumn(0).setPreferredWidth(propConfig.getProperty("tableMPRanking.width.rank", 30));	// Rank
			tm.getColumn(1).setPreferredWidth(propConfig.getProperty("tableMPRanking.width.name", 200));	// Name
			tm.getColumn(2).setPreferredWidth(propConfig.getProperty("tableMPRanking.width.rating", 60));	// Rating
			tm.getColumn(3).setPreferredWidth(propConfig.getProperty("tableMPRanking.width.play", 60));	// Play
			tm.getColumn(4).setPreferredWidth(propConfig.getProperty("tableMPRanking.width.win", 60));	// Win

			JScrollPane spMPRanking = new JScrollPane(tableMPRanking[i]);
			tabMPRanking.addTab(GameEngine.GAMESTYLE_NAMES[i], spMPRanking);
		}

		// * OK Button
		btnMPRankingOK = new JButton(getUIText("MPRanking_OK"));
		btnMPRankingOK.addActionListener(this);
		btnMPRankingOK.setActionCommand("MPRanking_OK");
		btnMPRankingOK.setMnemonic('O');
		mainpanelMPRanking.add(btnMPRankingOK, BorderLayout.SOUTH);
	}

	/**
	 * 翻訳後のUIの文字列を取得
	 * @param str 文字列
	 * @return 翻訳後のUIの文字列 (無いならそのままstrを返す）
	 */
	public String getUIText(String str) {
		String result = propLang.getProperty(str);
		if(result == null) {
			result = propLangDefault.getProperty(str, str);
		}
		return result;
	}

	/**
	 * 画面切り替え
	 * @param cardNumber 切替先の画面カード number
	 */
	public void changeCurrentScreenCard(int cardNumber) {
		contentPaneCardLayout.show(this.getContentPane(), SCREENCARD_NAMES[cardNumber]);
		currentScreenCardNumber = cardNumber;

		//  default  buttonの設定
		JButton defaultButton = null;
		switch(currentScreenCardNumber) {
		case SCREENCARD_SERVERSELECT:
			defaultButton = btnServerConnect;
			break;
		case SCREENCARD_LOBBY:
			if(tabLobbyAndRoom.getSelectedIndex() == 0)
				defaultButton = btnLobbyChatSend;
			else
				defaultButton = btnRoomChatSend;
			break;
		case SCREENCARD_SERVERADD:
			defaultButton = btnServerAddOK;
			break;
		case SCREENCARD_CREATEROOM:
			if (btnCreateRoomOK.isVisible())
				defaultButton = btnCreateRoomOK;
			else
				defaultButton = btnCreateRoomCancel;
			break;
		case SCREENCARD_CREATEROOM1P:
			defaultButton = btnCreateRoom1POK;
			break;
		case SCREENCARD_MPRANKING:
			defaultButton = btnMPRankingOK;
			break;
		}
		this.getRootPane().setDefaultButton(defaultButton);
	}

	/**
	 * @return Current 画面のチャットログ
	 */
	public JTextPane getCurrentChatLogTextPane() {
		if(tabLobbyAndRoom.getSelectedIndex() != 0) {
			return txtpaneRoomChatLog;
		}
		return txtpaneLobbyChatLog;
	}

	/**
	 * 現在時刻をString型で取得
	 * @return 現在時刻
	 */
	public String getCurrentTimeAsString() {
		GregorianCalendar currentTime = new GregorianCalendar();
		String strTime = String.format("%02d:%02d:%02d",
										currentTime.get(Calendar.HOUR_OF_DAY), currentTime.get(Calendar.MINUTE), currentTime.get(Calendar.SECOND));
		return strTime;
	}

	/**
	 * PlayerのNameをトリップ記号変換して取得
	 * @param pInfo Player情報
	 * @return PlayerのName(トリップ記号変換済み)
	 */
	public String getPlayerNameWithTripCode(NetPlayerInfo pInfo) {
		return convTripCode(pInfo.strName);
	}

	/**
	 * トリップ記号を変換
	 * @param s 変換する文字列(たいていはName)
	 * @return 変換された文字列
	 */
	public String convTripCode(String s) {
		if(propLang.getProperty("TripSeparator_EnableConvert", false) == false) return s;
		String strName = s;
		strName = strName.replace(getUIText("TripSeparator_True"), getUIText("TripSeparator_False"));
		strName = strName.replace("!", getUIText("TripSeparator_True"));
		strName = strName.replace("?", getUIText("TripSeparator_False"));
		return strName;
	}

	/**
	 * チャットログに新しい行を追加(システムメッセージ)
	 * @param txtpane チャットログ
	 * @param str 追加する文字列
	 */
	public void addSystemChatLog(JTextPane txtpane, String str) {
		addSystemChatLog(txtpane, str, null);
	}

	/**
	 * チャットログに新しい行を追加(システムメッセージ)
	 * @param txtpane チャットログ
	 * @param str 追加する文字列
	 * @param fgcolor 文字色(null可)
	 */
	public void addSystemChatLog(JTextPane txtpane, String str, Color fgcolor) {
		String strTime = getCurrentTimeAsString();

		SimpleAttributeSet sas = null;
		if(fgcolor != null) {
			sas = new SimpleAttributeSet();
			StyleConstants.setForeground(sas, fgcolor);
		}
		try {
			Document doc = txtpane.getDocument();
			doc.insertString(doc.getLength(), str + "\n", sas);
			txtpane.setCaretPosition(doc.getLength());

			if(txtpane == txtpaneRoomChatLog) {
				if(writerRoomLog != null) {
					writerRoomLog.println("[" + strTime + "] " + str);
					writerRoomLog.flush();
				}
			} else if(writerLobbyLog != null) {
				writerLobbyLog.println("[" + strTime + "] " + str);
				writerLobbyLog.flush();
			}
		} catch (Exception e) {}
	}

	/**
	 * チャットログに新しい行を追加(システムメッセージ・別スレッドからの呼び出し用)
	 * @param txtpane チャットログ
	 * @param str 追加する文字列
	 */
	public void addSystemChatLogLater(final JTextPane txtpane, final String str) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				addSystemChatLog(txtpane, str);
			}
		});
	}

	/**
	 * チャットログに新しい行を追加(システムメッセージ・別スレッドからの呼び出し用)
	 * @param txtpane チャットログ
	 * @param str 追加する文字列
	 * @param fgcolor 文字色(null可)
	 */
	public void addSystemChatLogLater(final JTextPane txtpane, final String str, final Color fgcolor) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				addSystemChatLog(txtpane, str, fgcolor);
			}
		});
	}

	/**
	 * チャットログに新しい行を追加(ユーザーチャット)
	 * @param txtpane チャットログ
	 * @param username ユーザー名
	 * @param str 発言内容
	 */
	public void addUserChatLog(JTextPane txtpane, String username, String str) {
		SimpleAttributeSet sasTime = new SimpleAttributeSet();
		StyleConstants.setForeground(sasTime, Color.gray);
		String strTime = getCurrentTimeAsString();

		SimpleAttributeSet sas = new SimpleAttributeSet();
		StyleConstants.setBold(sas, true);
		StyleConstants.setUnderline(sas, true);

		try {
			Document doc = txtpane.getDocument();
			doc.insertString(doc.getLength(), "[" + strTime + "]", sasTime);
			doc.insertString(doc.getLength(), "<" + username + ">", sas);
			doc.insertString(doc.getLength(), " " + str + "\n", null);
			txtpane.setCaretPosition(doc.getLength());

			if(txtpane == txtpaneRoomChatLog) {
				if(writerRoomLog != null) {
					writerRoomLog.println("[" + strTime + "]" + "<" + username + "> " + str);
					writerRoomLog.flush();
				}
			} else if(writerLobbyLog != null) {
				writerLobbyLog.println("[" + strTime + "]" + "<" + username + "> " + str);
				writerLobbyLog.flush();
			}
		} catch (Exception e) {}
	}

	/**
	 * チャットログに新しい行を追加(ユーザーチャット・別スレッドからの呼び出し用)
	 * @param txtpane チャットログ
	 * @param username ユーザー名
	 * @param str 発言内容
	 */
	public void addUserChatLogLater(final JTextPane txtpane, final String username, final String str) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				addUserChatLog(txtpane, username, str);
			}
		});
	}

	/**
	 * ファイルをDefaultListModelに読み込み
	 * @param listModel 読み込み先のDefaultListModel
	 * @param filename Filename
	 * @return 成功するとtrue
	 */
	public boolean loadListToDefaultListModel(DefaultListModel listModel, String filename) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(filename));
			listModel.clear();

			String str = null;
			while((str = in.readLine()) != null) {
				if(str.length() > 0)
					listModel.addElement(str);
			}
		} catch (IOException e) {
			log.debug("Failed to load server list", e);
			return false;
		}
		return true;
	}

	/**
	 * DefaultListModelからファイルに保存
	 * @param listModel 保存元のDefaultListModel
	 * @param filename Filename
	 * @return 成功するとtrue
	 */
	public boolean saveListFromDefaultListModel(DefaultListModel listModel, String filename) {
		try {
			PrintWriter out = new PrintWriter(filename);
			for(int i = 0; i < listModel.size(); i++) {
				out.println(listModel.get(i));
			}
			out.flush();
			out.close();
		} catch (IOException e) {
			log.debug("Failed to save server list", e);
			return false;
		}
		return true;
	}

	/**
	 * Set enabled state of lobby buttons
	 * @param mode 0=Disable all 1=Full Lobby 2=Inside Room
	 */
	public void setLobbyButtonsEnabled(int mode) {
		btnRoomListQuickStart.setEnabled((mode == 1));
		btnRoomListRoomCreate.setEnabled((mode == 1));
		btnRoomListRoomCreate1P.setEnabled((mode == 1));
		btnRoomListTeamChange.setEnabled((mode >= 1));
		btnLobbyChatSend.setEnabled((mode >= 1));
		txtfldLobbyChatInput.setEnabled((mode >= 1));
	}

	/**
	 * ルーム画面の buttonの is enabled状態を変更
	 * @param b When true, is enabled, falseなら無効
	 */
	public void setRoomButtonsEnabled(boolean b) {
		btnRoomButtonsTeamChange.setEnabled(b);
		btnRoomButtonsJoin.setEnabled(b);
		btnRoomButtonsSitOut.setEnabled(b);
		btnRoomButtonsViewSetting.setEnabled(b);
		btnRoomChatSend.setEnabled(b);
		txtfldRoomChatInput.setEnabled(b);
	}

	/**
	 * ルーム画面の参戦 buttonと離脱 buttonの切り替え
	 * @param b trueのときは参戦 button, falseのときは離脱 buttonを表示
	 */
	public void setRoomJoinButtonVisible(boolean b) {
		btnRoomButtonsJoin.setVisible(b);
		btnRoomButtonsJoin.setEnabled(true);
		btnRoomButtonsSitOut.setVisible(!b);
		btnRoomButtonsSitOut.setEnabled(true);
	}

	/**
	 * ルームリスト tableの行 dataを作成
	 * @param r ルーム情報
	 * @return 行 data
	 */
	public String[] createRoomListRowData(NetRoomInfo r) {
		String[] rowData = new String[7];
		rowData[0] = Integer.toString(r.roomID);
		rowData[1] = r.strName;
		rowData[2] = r.rated ? getUIText("RoomTable_Rated_True") : getUIText("RoomTable_Rated_False");
		rowData[3] = r.ruleLock ? r.ruleName.toUpperCase() : getUIText("RoomTable_RuleName_Any");
		rowData[4] = r.playing ? getUIText("RoomTable_Status_Playing") : getUIText("RoomTable_Status_Waiting");
		rowData[5] = r.playerSeatedCount + "/" + r.maxPlayers;
		rowData[6] = Integer.toString(r.spectatorCount);
		return rowData;
	}

	/**
	 * 指定したルームに入る
	 * @param roomID ルームID
	 * @param watch When true,観戦のみ
	 */
	public void joinRoom(int roomID, boolean watch) {
		tabLobbyAndRoom.setEnabledAt(1, true);
		tabLobbyAndRoom.setSelectedIndex(1);

		if(netPlayerClient.getYourPlayerInfo().roomID != roomID) {
			txtpaneRoomChatLog.setText("");
			setRoomButtonsEnabled(false);
			netPlayerClient.send("roomjoin\t" + roomID + "\t" + watch + "\n");
		}

		changeCurrentScreenCard(SCREENCARD_LOBBY);
	}

	/**
	 * ルーム作成画面のMode 切り替え
	 * @param isDetailMode falseなら作成Mode , When true,詳細Mode
	 * @param roomInfo ルーム情報(isDetailMode == trueのときだけ使用)
	 */
	public void setCreateRoomUIType(boolean isDetailMode, NetRoomInfo roomInfo) {
		NetRoomInfo r = null;

		if(isDetailMode) {
			btnCreateRoomOK.setVisible(false);
			btnCreateRoomJoin.setVisible(true);
			btnCreateRoomWatch.setVisible(true);
			r = roomInfo;

			if(netPlayerClient.getYourPlayerInfo().roomID == r.roomID) {
				btnCreateRoomJoin.setVisible(false);
				btnCreateRoomWatch.setVisible(false);
			}
		} else {
			btnCreateRoomOK.setVisible(true);
			btnCreateRoomJoin.setVisible(false);
			btnCreateRoomWatch.setVisible(false);

			if(backupRoomInfo != null) {
				r = backupRoomInfo;
			} else {
				r = new NetRoomInfo();
				r.maxPlayers = propConfig.getProperty("createroom.defaultMaxPlayers", 6);
				r.autoStartSeconds = propConfig.getProperty("createroom.defaultAutoStartSeconds", 15);
				r.gravity = propConfig.getProperty("createroom.defaultGravity", 1);
				r.denominator = propConfig.getProperty("createroom.defaultDenominator", 60);
				r.are = propConfig.getProperty("createroom.defaultARE", 30);
				r.areLine = propConfig.getProperty("createroom.defaultARELine", 30);
				r.lineDelay = propConfig.getProperty("createroom.defaultLineDelay", 20);
				r.lockDelay = propConfig.getProperty("createroom.defaultLockDelay", 30);
				r.das = propConfig.getProperty("createroom.defaultDAS", 14);
				r.hurryupSeconds = propConfig.getProperty("createroom.defaultHurryupSeconds", 90);
				r.hurryupInterval = propConfig.getProperty("createroom.defaultHurryupInterval", 5);
				r.garbagePercent = propConfig.getProperty("createroom.defaultGarbagePercent", 100);
				r.ruleLock = propConfig.getProperty("createroom.defaultRuleLock", false);
				r.tspinEnableType = propConfig.getProperty("createroom.defaultTSpinEnableType", 1);
				r.spinCheckType = propConfig.getProperty("createroom.defaultSpinCheckType", 0);
				r.tspinEnableEZ = propConfig.getProperty("createroom.defaultTSpinEnableEZ", false);
				r.b2b = propConfig.getProperty("createroom.defaultB2B", true);
				r.combo = propConfig.getProperty("createroom.defaultCombo", true);
				r.rensaBlock = propConfig.getProperty("createroom.defaultRensaBlock", true);
				r.counter = propConfig.getProperty("createroom.defaultCounter", true);
				r.bravo = propConfig.getProperty("createroom.defaultBravo", true);
				r.reduceLineSend = propConfig.getProperty("createroom.defaultReduceLineSend", false);
				r.garbageChangePerAttack = propConfig.getProperty("createroom.defaultGarbageChangePerAttack", true);
				r.b2bChunk = propConfig.getProperty("createroom.defaultB2BChunk", false);
				r.useFractionalGarbage = propConfig.getProperty("createroom.defaultUseFractionalGarbage", false);
				r.autoStartTNET2 = propConfig.getProperty("createroom.defaultAutoStartTNET2", false);
				r.disableTimerAfterSomeoneCancelled = propConfig.getProperty("createroom.defaultDisableTimerAfterSomeoneCancelled", false);
				r.useMap = propConfig.getProperty("createroom.defaultUseMap", false);
				//propConfig.getProperty("createroom.defaultMapSetID", 0);
				r.ruleName = propConfig.getProperty("createroom.ruleName", "");
			}
		}

		if(r != null) {
			txtfldCreateRoomName.setText(r.strName);
			spinnerCreateRoomMaxPlayers.setValue(r.maxPlayers);
			spinnerCreateRoomAutoStartSeconds.setValue(r.autoStartSeconds);
			spinnerCreateRoomGravity.setValue(r.gravity);
			spinnerCreateRoomDenominator.setValue(r.denominator);
			spinnerCreateRoomARE.setValue(r.are);
			spinnerCreateRoomARELine.setValue(r.areLine);
			spinnerCreateRoomLineDelay.setValue(r.lineDelay);
			spinnerCreateRoomLockDelay.setValue(r.lockDelay);
			spinnerCreateRoomDAS.setValue(r.das);
			spinnerCreateRoomHurryupSeconds.setValue(r.hurryupSeconds);
			spinnerCreateRoomHurryupInterval.setValue(r.hurryupInterval);
			spinnerCreateRoomGarbagePercent.setValue(r.garbagePercent);
			chkboxCreateRoomUseMap.setSelected(r.useMap);
			chkboxCreateRoomRuleLock.setSelected(r.ruleLock);
			comboboxCreateRoomTSpinEnableType.setSelectedIndex(r.tspinEnableType);
			comboboxCreateRoomSpinCheckType.setSelectedIndex(r.spinCheckType);
			chkboxCreateRoomTSpinEnableEZ.setSelected(r.tspinEnableEZ);
			chkboxCreateRoomB2B.setSelected(r.b2b);
			chkboxCreateRoomCombo.setSelected(r.combo);
			chkboxCreateRoomRensaBlock.setSelected(r.rensaBlock);
			chkboxCreateRoomCounter.setSelected(r.counter);
			chkboxCreateRoomBravo.setSelected(r.bravo);
			chkboxCreateRoomReduceLineSend.setSelected(r.reduceLineSend);
			chkboxCreateRoomGarbageChangePerAttack.setSelected(r.garbageChangePerAttack);
			chkboxCreateRoomB2BChunk.setSelected(r.b2bChunk);
			chkboxCreateRoomUseFractionalGarbage.setSelected(r.useFractionalGarbage);
			chkboxCreateRoomAutoStartTNET2.setSelected(r.autoStartTNET2);
			chkboxCreateRoomDisableTimerAfterSomeoneCancelled.setSelected(r.disableTimerAfterSomeoneCancelled);
			if(r.rated) listboxCreateRoomRuleList.setSelectedValue(r.ruleName, true);
			else listboxCreateRoomRuleList.setSelectedIndex(0);
		}
	}

	/**
	 * ルーム詳細画面に切り替える
	 * @param roomID ルームID
	 */
	public void viewRoomDetail(int roomID) {
		NetRoomInfo roomInfo = netPlayerClient.getRoomInfo(roomID);

		if(roomInfo != null) {
			currentViewDetailRoomID = roomID;
			setCreateRoomUIType(true, roomInfo);
			changeCurrentScreenCard(SCREENCARD_CREATEROOM);
		}
	}

	/**
	 * ロビー画面のPlayerリスト更新
	 */
	public void updateLobbyUserList() {
		LinkedList<NetPlayerInfo> pList = new LinkedList<NetPlayerInfo>(netPlayerClient.getPlayerInfoList());

		if(!pList.isEmpty()) {
			listmodelLobbyChatPlayerList.clear();

			for(int i = 0; i < pList.size(); i++) {
				NetPlayerInfo pInfo = pList.get(i);

				// Name
				String name = getPlayerNameWithTripCode(pInfo);
				if(pInfo.uid == netPlayerClient.getPlayerUID()) name = "*" + getPlayerNameWithTripCode(pInfo);

				// Team
				if(pInfo.strTeam.length() > 0) {
					name = getPlayerNameWithTripCode(pInfo) + " - " + pInfo.strTeam;
					if(pInfo.uid == netPlayerClient.getPlayerUID()) name = "*" + getPlayerNameWithTripCode(pInfo) + " - " + pInfo.strTeam;
				}

				// Rating
				name += " |" + pInfo.rating[0] + "|";
				/*
				name += " |T:" + pInfo.rating[0] + "|";
				name += "A:" + pInfo.rating[1] + "|";
				name += "P:" + pInfo.rating[2] + "|";
				name += "S:" + pInfo.rating[3] + "|";
				*/

				// Country code
				if(pInfo.strCountry.length() > 0) {
					name += " (" + pInfo.strCountry + ")";
				}

				// Hostname
				if(pInfo.strHost.length() > 0) {
					name += " {" + pInfo.strHost + "}";
				}

				if(pInfo.roomID == -1) {
					listmodelLobbyChatPlayerList.addElement(name);
				}
			}
			for(int i = 0; i < pList.size(); i++) {
				NetPlayerInfo pInfo = pList.get(i);

				// Name
				String name = getPlayerNameWithTripCode(pInfo);
				if(pInfo.uid == netPlayerClient.getPlayerUID()) name = "*" + getPlayerNameWithTripCode(pInfo);

				// Team
				if(pInfo.strTeam.length() > 0) {
					name = getPlayerNameWithTripCode(pInfo) + " - " + pInfo.strTeam;
					if(pInfo.uid == netPlayerClient.getPlayerUID()) name = "*" + getPlayerNameWithTripCode(pInfo) + " - " + pInfo.strTeam;
				}

				// Rating
				name += " |" + pInfo.rating[0] + "|";
				/*
				name += " |T:" + pInfo.rating[0] + "|";
				name += "A:" + pInfo.rating[1] + "|";
				name += "P:" + pInfo.rating[2] + "|";
				name += "S:" + pInfo.rating[3] + "|";
				*/

				// Country code
				if(pInfo.strCountry.length() > 0) {
					name += " (" + pInfo.strCountry + ")";
				}

				// Hostname
				if(pInfo.strHost.length() > 0) {
					name += " {" + pInfo.strHost + "}";
				}

				if(pInfo.roomID != -1) {
					listmodelLobbyChatPlayerList.addElement("{" + pInfo.roomID + "} " + name);
				}
			}
		}
	}

	/**
	 * ルーム画面のPlayerリスト更新
	 */
	public void updateRoomUserList() {
		NetRoomInfo roomInfo = netPlayerClient.getRoomInfo(netPlayerClient.getYourPlayerInfo().roomID);
		if(roomInfo == null) return;

		LinkedList<NetPlayerInfo> pList = new LinkedList<NetPlayerInfo>(netPlayerClient.getPlayerInfoList());

		if(!pList.isEmpty()) {
			listmodelRoomChatPlayerList.clear();

			for(int i = 0; i < roomInfo.maxPlayers; i++) {
				listmodelRoomChatPlayerList.addElement("[" + (i + 1) + "]");
			}

			for(int i = 0; i < pList.size(); i++) {
				NetPlayerInfo pInfo = pList.get(i);

				// Name
				String name = getPlayerNameWithTripCode(pInfo);
				if(pInfo.uid == netPlayerClient.getPlayerUID()) name = "*" + getPlayerNameWithTripCode(pInfo);

				// Team
				if(pInfo.strTeam.length() > 0) {
					name = getPlayerNameWithTripCode(pInfo) + " - " + pInfo.strTeam;
					if(pInfo.uid == netPlayerClient.getPlayerUID()) name = "*" + getPlayerNameWithTripCode(pInfo) + " - " + pInfo.strTeam;
				}

				// Rating
				name += " |" + pInfo.rating[roomInfo.style] + "|";

				// Country code
				if(pInfo.strCountry.length() > 0) {
					name += " (" + pInfo.strCountry + ")";
				}

				// Hostname
				if(pInfo.strHost.length() > 0) {
					name += " {" + pInfo.strHost + "}";
				}

				// Status
				if(pInfo.playing) name += getUIText("RoomUserList_Playing");
				else if(pInfo.ready) name += getUIText("RoomUserList_Ready");

				if(pInfo.roomID == roomInfo.roomID) {
					if((pInfo.seatID >= 0) && (pInfo.seatID < roomInfo.maxPlayers)) {
						listmodelRoomChatPlayerList.set(pInfo.seatID, "[" + (pInfo.seatID + 1) + "] " + name);
					} else if(pInfo.queueID != -1) {
						listmodelRoomChatPlayerList.addElement((pInfo.queueID + 1) + ". " + name);
					} else {
						listmodelRoomChatPlayerList.addElement(name);
					}
				}
			}
		}
	}

	/**
	 * 同じ部屋にいるPlayerリストを更新
	 * @return 同じ部屋にいるPlayerリスト
	 */
	public LinkedList<NetPlayerInfo> updateSameRoomPlayerInfoList() {
		LinkedList<NetPlayerInfo> pList = new LinkedList<NetPlayerInfo>(netPlayerClient.getPlayerInfoList());
		int roomID = netPlayerClient.getYourPlayerInfo().roomID;
		sameRoomPlayerInfoList.clear();

		for(NetPlayerInfo pInfo: pList) {
			if(pInfo.roomID == roomID) {
				sameRoomPlayerInfoList.add(pInfo);
			}
		}

		return sameRoomPlayerInfoList;
	}

	/**
	 * 同じ部屋にいるPlayerリストを返す(更新はしない)
	 * @return 同じ部屋にいるPlayerリスト
	 */
	public LinkedList<NetPlayerInfo> getSameRoomPlayerInfoList() {
		return sameRoomPlayerInfoList;
	}

	/**
	 * ルール data送信
	 */
	public void sendMyRuleDataToServer() {
		if(ruleOptPlayer == null) ruleOptPlayer = new RuleOptions();

		CustomProperties prop = new CustomProperties();
		ruleOptPlayer.writeProperty(prop, 0);
		String strRuleTemp = prop.encode("RuleData");
		String strRuleData = NetUtil.compressString(strRuleTemp);
		log.debug("RuleData uncompressed:" + strRuleTemp.length() + " compressed:" + strRuleData.length());

		// checkサム計算
		Adler32 checksumObj = new Adler32();
		checksumObj.update(NetUtil.stringToBytes(strRuleData));
		long sChecksum = checksumObj.getValue();

		// 送信
		netPlayerClient.send("ruledata\t" + sChecksum + "\t" + strRuleData + "\n");
	}

	/**
	 * ロビーの設定を保存
	 */
	public void saveConfig() {
		propConfig.setProperty("mainwindow.width", this.getSize().width);
		propConfig.setProperty("mainwindow.height", this.getSize().height);
		propConfig.setProperty("mainwindow.x", this.getLocation().x);
		propConfig.setProperty("mainwindow.y", this.getLocation().y);
		propConfig.setProperty("lobby.splitLobby.location", splitLobby.getDividerLocation());
		propConfig.setProperty("lobby.splitLobbyChat.location", splitLobbyChat.getDividerLocation());
		propConfig.setProperty("room.splitRoom.location", splitRoom.getDividerLocation());
		propConfig.setProperty("room.splitRoomChat.location", splitRoomChat.getDividerLocation());
		propConfig.setProperty("serverselect.txtfldPlayerName.text", txtfldPlayerName.getText());
		propConfig.setProperty("serverselect.txtfldPlayerTeam.text", txtfldPlayerTeam.getText());

		Object listboxServerListSelectedValue = listboxServerList.getSelectedValue();
		if((listboxServerListSelectedValue != null) && (listboxServerListSelectedValue instanceof String)) {
			propConfig.setProperty("serverselect.listboxServerList.value", (String)listboxServerListSelectedValue);
		} else {
			propConfig.setProperty("serverselect.listboxServerList.value", "");
		}

		TableColumnModel tm = tableRoomList.getColumnModel();
		propConfig.setProperty("tableRoomList.width.id", tm.getColumn(0).getWidth());
		propConfig.setProperty("tableRoomList.width.name", tm.getColumn(1).getWidth());
		propConfig.setProperty("tableRoomList.width.rated", tm.getColumn(2).getWidth());
		propConfig.setProperty("tableRoomList.width.rulename", tm.getColumn(3).getWidth());
		propConfig.setProperty("tableRoomList.width.status", tm.getColumn(4).getWidth());
		propConfig.setProperty("tableRoomList.width.players", tm.getColumn(5).getWidth());
		propConfig.setProperty("tableRoomList.width.spectators", tm.getColumn(6).getWidth());

		tm = tableGameStat.getColumnModel();
		propConfig.setProperty("tableGameStat.width.rank", tm.getColumn(0).getWidth());
		propConfig.setProperty("tableGameStat.width.name", tm.getColumn(1).getWidth());
		propConfig.setProperty("tableGameStat.width.attack", tm.getColumn(2).getWidth());
		propConfig.setProperty("tableGameStat.width.apm", tm.getColumn(3).getWidth());
		propConfig.setProperty("tableGameStat.width.lines", tm.getColumn(4).getWidth());
		propConfig.setProperty("tableGameStat.width.lpm", tm.getColumn(5).getWidth());
		propConfig.setProperty("tableGameStat.width.piece", tm.getColumn(6).getWidth());
		propConfig.setProperty("tableGameStat.width.pps", tm.getColumn(7).getWidth());
		propConfig.setProperty("tableGameStat.width.time", tm.getColumn(8).getWidth());
		propConfig.setProperty("tableGameStat.width.ko", tm.getColumn(9).getWidth());
		propConfig.setProperty("tableGameStat.width.wins", tm.getColumn(10).getWidth());
		propConfig.setProperty("tableGameStat.width.games", tm.getColumn(11).getWidth());

		tm = tableMPRanking[0].getColumnModel();
		propConfig.setProperty("tableMPRanking.width.rank", tm.getColumn(0).getWidth());
		propConfig.setProperty("tableMPRanking.width.name", tm.getColumn(1).getWidth());
		propConfig.setProperty("tableMPRanking.width.rating", tm.getColumn(2).getWidth());
		propConfig.setProperty("tableMPRanking.width.play", tm.getColumn(3).getWidth());
		propConfig.setProperty("tableMPRanking.width.win", tm.getColumn(4).getWidth());

		if(backupRoomInfo != null) {
			propConfig.setProperty("createroom.defaultMaxPlayers", backupRoomInfo.maxPlayers);
			propConfig.setProperty("createroom.defaultAutoStartSeconds", backupRoomInfo.autoStartSeconds);
			propConfig.setProperty("createroom.defaultGravity", backupRoomInfo.gravity);
			propConfig.setProperty("createroom.defaultDenominator", backupRoomInfo.denominator);
			propConfig.setProperty("createroom.defaultARE", backupRoomInfo.are);
			propConfig.setProperty("createroom.defaultARELine", backupRoomInfo.areLine);
			propConfig.setProperty("createroom.defaultLineDelay", backupRoomInfo.lineDelay);
			propConfig.setProperty("createroom.defaultLockDelay", backupRoomInfo.lockDelay);
			propConfig.setProperty("createroom.defaultDAS", backupRoomInfo.das);
			propConfig.setProperty("createroom.defaultGarbagePercent", backupRoomInfo.garbagePercent);
			propConfig.setProperty("createroom.defaultHurryupSeconds", backupRoomInfo.hurryupSeconds);
			propConfig.setProperty("createroom.defaultHurryupInterval", backupRoomInfo.hurryupInterval);
			propConfig.setProperty("createroom.defaultRuleLock", backupRoomInfo.ruleLock);
			propConfig.setProperty("createroom.defaultTSpinEnableType", backupRoomInfo.tspinEnableType);
			propConfig.setProperty("createroom.defaultSpinCheckType", backupRoomInfo.spinCheckType);
			propConfig.setProperty("createroom.defaultTSpinEnableEZ", backupRoomInfo.tspinEnableEZ);
			propConfig.setProperty("createroom.defaultB2B", backupRoomInfo.b2b);
			propConfig.setProperty("createroom.defaultCombo", backupRoomInfo.combo);
			propConfig.setProperty("createroom.defaultRensaBlock", backupRoomInfo.rensaBlock);
			propConfig.setProperty("createroom.defaultCounter", backupRoomInfo.counter);
			propConfig.setProperty("createroom.defaultBravo", backupRoomInfo.bravo);
			propConfig.setProperty("createroom.defaultReduceLineSend", backupRoomInfo.reduceLineSend);
			propConfig.setProperty("createroom.defaultGarbageChangePerAttack", backupRoomInfo.garbageChangePerAttack);
			propConfig.setProperty("createroom.defaultB2BChunk", backupRoomInfo.b2bChunk);
			propConfig.setProperty("createroom.defaultUseFractionalGarbage", backupRoomInfo.useFractionalGarbage);
			propConfig.setProperty("createroom.defaultAutoStartTNET2", backupRoomInfo.autoStartTNET2);
			propConfig.setProperty("createroom.defaultDisableTimerAfterSomeoneCancelled", backupRoomInfo.disableTimerAfterSomeoneCancelled);
			propConfig.setProperty("createroom.defaultUseMap", backupRoomInfo.useMap);
			propConfig.setProperty("createroom.defaultMapSetID", (Integer)spinnerCreateRoomMapSetID.getValue());
		}

		Object listboxCreateRoom1PModeListSelectedValue = listboxCreateRoom1PModeList.getSelectedValue();
		if((listboxCreateRoom1PModeListSelectedValue != null) && (listboxCreateRoom1PModeListSelectedValue instanceof String)) {
			propConfig.setProperty("createroom1p.listboxCreateRoom1PModeList.value", (String)listboxCreateRoom1PModeListSelectedValue);
		} else {
			propConfig.setProperty("createroom1p.listboxCreateRoom1PModeList.value", "");
		}

		try {
			FileOutputStream out = new FileOutputStream("config/setting/netlobby.cfg");
			propConfig.store(out, "NullpoMino NetLobby Config");
			out.close();
		} catch (IOException e) {
			log.warn("Failed to save netlobby config file", e);
		}
	}

	/**
	 * 終了処理
	 */
	public void shutdown() {
		saveConfig();

		if(writerLobbyLog != null) {
			writerLobbyLog.flush();
			writerLobbyLog.close();
			writerLobbyLog = null;
		}
		if(writerRoomLog != null) {
			writerRoomLog.flush();
			writerRoomLog.close();
			writerRoomLog = null;
		}

		// 切断
		if(netPlayerClient != null) {
			if(netPlayerClient.isConnected()) {
				netPlayerClient.send("disconnect\n");
			}
			netPlayerClient.threadRunning = false;
			netPlayerClient.interrupt();
			netPlayerClient = null;
		}

		// Listener呼び出し
		if(listeners != null) {
			for(NetLobbyListener l: listeners) {
				l.netlobbyOnExit(this);
			}
			listeners = null;
		}
		if(netDummyMode != null) {
			netDummyMode.netlobbyOnExit(this);
			netDummyMode = null;
		}

		this.dispose();
	}

	/**
	 * サーバー削除 buttonが押されたときの処理
	 */
	public void serverSelectDeleteButtonClicked() {
		int index = listboxServerList.getSelectedIndex();
		if(index != -1) {
			String server = (String)listboxServerList.getSelectedValue();
			int answer = JOptionPane.showConfirmDialog(this,
													   getUIText("MessageBody_ServerDelete") + "\n" + server,
													   getUIText("MessageTitle_ServerDelete"),
													   JOptionPane.YES_NO_OPTION);
			if(answer == JOptionPane.YES_OPTION) {
				listmodelServerList.remove(index);
				saveListFromDefaultListModel(listmodelServerList, "config/setting/netlobby_serverlist.cfg");
			}
		}
	}

	/**
	 * サーバー接続 buttonが押されたときの処理
	 */
	public void serverSelectConnectButtonClicked() {
		int index = listboxServerList.getSelectedIndex();
		if(index != -1) {
			String strServer = (String)listboxServerList.getSelectedValue();
			int portSpliter = strServer.indexOf(":");
			if(portSpliter == -1) portSpliter = strServer.length();

			String strHost = strServer.substring(0, portSpliter);
			log.debug("Host:" + strHost);

			int port = NetPlayerClient.DEFAULT_PORT;
			try {
				String strPort = strServer.substring(portSpliter + 1, strServer.length());
				port = Integer.parseInt(strPort);
			} catch (Exception e2) {
				log.debug("Failed to get port number; Try to use default port");
			}
			log.debug("Port:" + port);

			netPlayerClient = new NetPlayerClient(strHost, port, txtfldPlayerName.getText(), txtfldPlayerTeam.getText().trim());
			netPlayerClient.setDaemon(true);
			netPlayerClient.addListener(this);
			netPlayerClient.start();

			txtpaneLobbyChatLog.setText("");
			setLobbyButtonsEnabled(0);
			tablemodelRoomList.setRowCount(0);

			changeCurrentScreenCard(SCREENCARD_LOBBY);
		}
	}

	/**
	 * 監視設定 buttonが押されたときの処理
	 */
	public void serverSelectSetObserverButtonClicked() {
		int index = listboxServerList.getSelectedIndex();
		if(index != -1) {
			String strServer = (String)listboxServerList.getSelectedValue();
			int portSpliter = strServer.indexOf(":");
			if(portSpliter == -1) portSpliter = strServer.length();

			String strHost = strServer.substring(0, portSpliter);
			log.debug("Host:" + strHost);

			int port = NetPlayerClient.DEFAULT_PORT;
			try {
				String strPort = strServer.substring(portSpliter + 1, strServer.length());
				port = Integer.parseInt(strPort);
			} catch (Exception e2) {
				log.debug("Failed to get port number; Try to use default port");
			}
			log.debug("Port:" + port);

			int answer = JOptionPane.showConfirmDialog(this,
					   getUIText("MessageBody_SetObserver") + "\n" + strServer,
					   getUIText("MessageTitle_SetObserver"),
					   JOptionPane.YES_NO_OPTION);

			if(answer == JOptionPane.YES_OPTION) {
				propObserver.setProperty("observer.enable", true);
				propObserver.setProperty("observer.host", strHost);
				propObserver.setProperty("observer.port", port);

				try {
					FileOutputStream out = new FileOutputStream("config/setting/netobserver.cfg");
					propObserver.store(out, "NullpoMino NetObserver Config");
					out.close();
				} catch (IOException e) {
					log.warn("Failed to save NetObserver config file", e);
				}
			}
		}
	}

	/**
	 * Get currenlty selected map set ID
	 * @return Map set ID
	 */
	public int getCurrentSelectedMapSetID() {
		if(spinnerCreateRoomMapSetID != null) {
			return (Integer)spinnerCreateRoomMapSetID.getValue();
		}
		return 0;
	}

	/**
	 * Send a chat message
	 * @param roomchat <code>true</code> if room chat
	 * @param strMsg Message to send
	 */
	public void sendChat(boolean roomchat, String strMsg) {
		String msg = strMsg;
		if(msg.startsWith("/team")) {
			msg = msg.replaceFirst("/team", "");
			msg = msg.trim();
			netPlayerClient.send("changeteam\t" + NetUtil.urlEncode(msg) + "\n");
		} else if(roomchat) {
			netPlayerClient.send("chat\t" + NetUtil.urlEncode(msg) + "\n");
		} else {
			netPlayerClient.send("lobbychat\t" + NetUtil.urlEncode(msg) + "\n");
		}
	}

	/*
	 * Menu 実行時の処理
	 */
	public void actionPerformed(ActionEvent e) {
		//addSystemChatLog(getCurrentChatLogTextPane(), e.getActionCommand(), Color.magenta);

		// サーバー追加
		if(e.getActionCommand() == "ServerSelect_ServerAdd") {
			changeCurrentScreenCard(SCREENCARD_SERVERADD);
		}
		// サーバー削除
		if(e.getActionCommand() == "ServerSelect_ServerDelete") {
			serverSelectDeleteButtonClicked();
		}
		// サーバー接続
		if(e.getActionCommand() == "ServerSelect_Connect") {
			serverSelectConnectButtonClicked();
		}
		// 監視設定
		if(e.getActionCommand() == "ServerSelect_SetObserver") {
			serverSelectSetObserverButtonClicked();
		}
		// 監視解除
		if(e.getActionCommand() == "ServerSelect_UnsetObserver") {
			if(propObserver.getProperty("observer.enable", false) == true) {
				String strCurrentHost = propObserver.getProperty("observer.host", "");
				int currentPort = propObserver.getProperty("observer.port", 0);
				String strMessageBox = String.format(getUIText("MessageBody_UnsetObserver"), strCurrentHost, currentPort);

				int answer = JOptionPane.showConfirmDialog(this, strMessageBox, getUIText("MessageTitle_UnsetObserver"), JOptionPane.YES_NO_OPTION);

				if(answer == JOptionPane.YES_OPTION) {
					propObserver.setProperty("observer.enable", false);
					try {
						FileOutputStream out = new FileOutputStream("config/setting/netobserver.cfg");
						propObserver.store(out, "NullpoMino NetObserver Config");
						out.close();
					} catch (IOException e2) {
						log.warn("Failed to save NetObserver config file", e2);
					}
				}
			}
		}
		// 終了
		if(e.getActionCommand() == "ServerSelect_Exit") {
			shutdown();
		}
		// クイックスタート
		if(e.getActionCommand() == "Lobby_QuickStart") {
			// TODO:クイックスタート
		}
		// Create Room 1P
		if(e.getActionCommand() == "Lobby_RoomCreate1P") {
			changeCurrentScreenCard(SCREENCARD_CREATEROOM1P);
		}
		// ルーム作成
		if(e.getActionCommand() == "Lobby_RoomCreate") {
			currentViewDetailRoomID = -1;
			setCreateRoomUIType(false, null);
			changeCurrentScreenCard(SCREENCARD_CREATEROOM);
		}
		// チーム変更(Lobby screen)
		if(e.getActionCommand() == "Lobby_TeamChange") {
			if((netPlayerClient != null) && (netPlayerClient.isConnected())) {
				txtfldRoomListTeam.setText(netPlayerClient.getYourPlayerInfo().strTeam);
				roomListTopBarCardLayout.next(subpanelRoomListTopBar);
			}
		}
		// 切断
		if(e.getActionCommand() == "Lobby_Disconnect") {
			if((netPlayerClient != null) && (netPlayerClient.isConnected())) {
				netPlayerClient.send("disconnect\n");
				netPlayerClient.threadRunning = false;
				netPlayerClient.interrupt();
				netPlayerClient = null;
			}
			tabLobbyAndRoom.setSelectedIndex(0);
			tabLobbyAndRoom.setEnabledAt(1, false);
			tabLobbyAndRoom.setTitleAt(1, getUIText("Lobby_Tab_NoRoom"));
			setLobbyButtonsEnabled(1);
			setTitle(getUIText("Title_NetLobby"));
			changeCurrentScreenCard(SCREENCARD_SERVERSELECT);
		}
		// Multiplayer Leaderboard
		if((e.getActionCommand() == "Lobby_Ranking") || (e.getActionCommand() == "Room_Ranking")) {
			if((netPlayerClient != null) && (netPlayerClient.isConnected())) {
				tablemodelMPRanking[0].setRowCount(0);
				netPlayerClient.send("mpranking\t0\n");
				changeCurrentScreenCard(SCREENCARD_MPRANKING);
			}
		}
		// チャット送信
		if((e.getActionCommand() == "Lobby_ChatSend") || (e.getActionCommand() == "Room_ChatSend")) {
			if((txtfldLobbyChatInput.getText().length() > 0) && (netPlayerClient != null) && netPlayerClient.isConnected()) {
				sendChat(false, txtfldLobbyChatInput.getText());
				txtfldLobbyChatInput.setText("");
			}

			if((netPlayerClient != null) && netPlayerClient.isConnected()) {
				if(tabLobbyAndRoom.getSelectedIndex() == 0) {
					if(txtfldLobbyChatInput.getText().length() > 0) {
						sendChat(false, txtfldLobbyChatInput.getText());
						txtfldLobbyChatInput.setText("");
					}
				} else {
					if(txtfldRoomChatInput.getText().length() > 0) {
						sendChat(true, txtfldRoomChatInput.getText());
						txtfldRoomChatInput.setText("");
					}
				}
			}
		}
		// チーム変更OK(Lobby screen)
		if(e.getActionCommand() == "Lobby_TeamChange_OK") {
			if((netPlayerClient != null) && (netPlayerClient.isConnected())) {
				netPlayerClient.send("changeteam\t" + NetUtil.urlEncode(txtfldRoomListTeam.getText()) + "\n");
				roomListTopBarCardLayout.first(subpanelRoomListTopBar);
			}
		}
		// チーム変更Cancel(Lobby screen)
		if(e.getActionCommand() == "Lobby_TeamChange_Cancel") {
			roomListTopBarCardLayout.first(subpanelRoomListTopBar);
		}
		// 退出 button
		if(e.getActionCommand() == "Room_Leave") {
			netPlayerClient.send("roomjoin\t-1\tfalse\n");
			tablemodelGameStat.setRowCount(0);
			changeCurrentScreenCard(SCREENCARD_LOBBY);
		}
		// 参戦 button
		if(e.getActionCommand() == "Room_Join") {
			netPlayerClient.send("changestatus\tfalse\n");
			btnRoomButtonsJoin.setEnabled(false);
		}
		// 離脱(観戦のみ) button
		if(e.getActionCommand() == "Room_SitOut") {
			netPlayerClient.send("changestatus\ttrue\n");
			btnRoomButtonsSitOut.setEnabled(false);
		}
		// チーム変更(Room screen)
		if(e.getActionCommand() == "Room_TeamChange") {
			if((netPlayerClient != null) && (netPlayerClient.isConnected())) {
				txtfldRoomTeam.setText(netPlayerClient.getYourPlayerInfo().strTeam);
				roomTopBarCardLayout.next(subpanelRoomTopBar);
			}
		}
		// チーム変更OK(Room screen)
		if(e.getActionCommand() == "Room_TeamChange_OK") {
			if((netPlayerClient != null) && (netPlayerClient.isConnected())) {
				netPlayerClient.send("changeteam\t" + NetUtil.urlEncode(txtfldRoomTeam.getText()) + "\n");
				roomTopBarCardLayout.first(subpanelRoomTopBar);
			}
		}
		// チーム変更Cancel(Room screen)
		if(e.getActionCommand() == "Room_TeamChange_Cancel") {
			roomTopBarCardLayout.first(subpanelRoomTopBar);
		}
		// ルール確認(Room screen)
		if(e.getActionCommand() == "Room_ViewSetting") {
			viewRoomDetail(netPlayerClient.getYourPlayerInfo().roomID);
		}
		// サーバー追加画面でのOK button
		if(e.getActionCommand() == "ServerAdd_OK") {
			if(txtfldServerAddHost.getText().length() > 0) {
				listmodelServerList.addElement(txtfldServerAddHost.getText());
				saveListFromDefaultListModel(listmodelServerList, "config/setting/netlobby_serverlist.cfg");
				txtfldServerAddHost.setText("");
			}
			changeCurrentScreenCard(SCREENCARD_SERVERSELECT);
		}
		// サーバー追加画面でのCancel button
		if(e.getActionCommand() == "ServerAdd_Cancel") {
			txtfldServerAddHost.setText("");
			changeCurrentScreenCard(SCREENCARD_SERVERSELECT);
		}
		// ルーム作成画面でのOK button
		if(e.getActionCommand() == "CreateRoom_OK") {
			try {
				String roomName = txtfldCreateRoomName.getText();
				Integer integerMaxPlayers = (Integer)spinnerCreateRoomMaxPlayers.getValue();
				Integer integerAutoStartSeconds = (Integer)spinnerCreateRoomAutoStartSeconds.getValue();
				Integer integerGravity = (Integer)spinnerCreateRoomGravity.getValue();
				Integer integerDenominator = (Integer)spinnerCreateRoomDenominator.getValue();
				Integer integerARE = (Integer)spinnerCreateRoomARE.getValue();
				Integer integerARELine = (Integer)spinnerCreateRoomARELine.getValue();
				Integer integerLineDelay = (Integer)spinnerCreateRoomLineDelay.getValue();
				Integer integerLockDelay = (Integer)spinnerCreateRoomLockDelay.getValue();
				Integer integerDAS = (Integer)spinnerCreateRoomDAS.getValue();
				Integer integerHurryupSeconds = (Integer)spinnerCreateRoomHurryupSeconds.getValue();
				Integer integerHurryupInterval = (Integer)spinnerCreateRoomHurryupInterval.getValue();
				boolean rulelock = chkboxCreateRoomRuleLock.isSelected();
				int tspinEnableType = comboboxCreateRoomTSpinEnableType.getSelectedIndex();
				int spinCheckType = comboboxCreateRoomSpinCheckType.getSelectedIndex();
				boolean tspinEnableEZ = chkboxCreateRoomTSpinEnableEZ.isSelected();
				boolean b2b = chkboxCreateRoomB2B.isSelected();
				boolean combo = chkboxCreateRoomCombo.isSelected();
				boolean rensaBlock = chkboxCreateRoomRensaBlock.isSelected();
				boolean counter = chkboxCreateRoomCounter.isSelected();
				boolean bravo = chkboxCreateRoomBravo.isSelected();
				boolean reduceLineSend = chkboxCreateRoomReduceLineSend.isSelected();
				boolean autoStartTNET2 = chkboxCreateRoomAutoStartTNET2.isSelected();
				boolean disableTimerAfterSomeoneCancelled = chkboxCreateRoomDisableTimerAfterSomeoneCancelled.isSelected();
				boolean useMap = chkboxCreateRoomUseMap.isSelected();
				boolean useFractionalGarbage = chkboxCreateRoomUseFractionalGarbage.isSelected();
				boolean garbageChangePerAttack = chkboxCreateRoomGarbageChangePerAttack.isSelected();
				Integer integerGarbagePercent = (Integer)spinnerCreateRoomGarbagePercent.getValue();
				boolean b2bChunk = chkboxCreateRoomB2BChunk.isSelected();

				if(backupRoomInfo == null) backupRoomInfo = new NetRoomInfo();
				backupRoomInfo.strName = roomName;
				backupRoomInfo.maxPlayers = integerMaxPlayers;
				backupRoomInfo.autoStartSeconds = integerAutoStartSeconds;
				backupRoomInfo.gravity = integerGravity;
				backupRoomInfo.denominator = integerDenominator;
				backupRoomInfo.are = integerARE;
				backupRoomInfo.areLine = integerARELine;
				backupRoomInfo.lineDelay = integerLineDelay;
				backupRoomInfo.lockDelay = integerLockDelay;
				backupRoomInfo.das = integerDAS;
				backupRoomInfo.hurryupSeconds = integerHurryupSeconds;
				backupRoomInfo.hurryupInterval = integerHurryupInterval;
				backupRoomInfo.ruleLock = rulelock;
				backupRoomInfo.tspinEnableType = tspinEnableType;
				backupRoomInfo.spinCheckType = spinCheckType;
				backupRoomInfo.tspinEnableEZ = tspinEnableEZ;
				backupRoomInfo.b2b = b2b;
				backupRoomInfo.combo = combo;
				backupRoomInfo.rensaBlock = rensaBlock;
				backupRoomInfo.counter = counter;
				backupRoomInfo.bravo = bravo;
				backupRoomInfo.reduceLineSend = reduceLineSend;
				backupRoomInfo.autoStartTNET2 = autoStartTNET2;
				backupRoomInfo.disableTimerAfterSomeoneCancelled = disableTimerAfterSomeoneCancelled;
				backupRoomInfo.useMap = useMap;
				backupRoomInfo.useFractionalGarbage = useFractionalGarbage;
				backupRoomInfo.garbageChangePerAttack = garbageChangePerAttack;
				backupRoomInfo.garbagePercent = integerGarbagePercent;
				backupRoomInfo.b2bChunk = b2bChunk;

				String msg;
				msg = "roomcreate\t" + roomName + "\t" + integerMaxPlayers + "\t" + integerAutoStartSeconds + "\t";
				msg += integerGravity + "\t" + integerDenominator + "\t" + integerARE + "\t" + integerARELine + "\t";
				msg += integerLineDelay + "\t" + integerLockDelay + "\t" + integerDAS + "\t" + rulelock + "\t";
				msg += tspinEnableType + "\t" + b2b + "\t" + combo + "\t" + rensaBlock + "\t";
				msg += counter + "\t" + bravo + "\t" + reduceLineSend + "\t" + integerHurryupSeconds + "\t";
				msg += integerHurryupInterval + "\t" + autoStartTNET2 + "\t" + disableTimerAfterSomeoneCancelled + "\t";
				msg += useMap + "\t" + useFractionalGarbage + "\t" + garbageChangePerAttack + "\t" + integerGarbagePercent + "\t";
				msg += spinCheckType + "\t" + tspinEnableEZ + "\t"  + b2bChunk + "\t";
				msg += NetUtil.urlEncode("NET-VS-BATTLE") + "\t" + 0 + "\t";

				// Rule
				if(listboxCreateRoomRuleList.getSelectedIndex() >= 1) {
					backupRoomInfo.ruleName = (String)listboxCreateRoomRuleList.getSelectedValue();
					msg += (String)listboxCreateRoomRuleList.getSelectedValue() + "\t";
				} else {
					backupRoomInfo.ruleName = "";
					msg += "\t";
				}

				// Map send
				if(useMap) {
					int setID = getCurrentSelectedMapSetID();
					log.debug("MapSetID:" + setID);

					mapList.clear();
					CustomProperties propMap = new CustomProperties();
					try {
						FileInputStream in = new FileInputStream("config/map/vsbattle/" + setID + ".map");
						propMap.load(in);
						in.close();
					} catch (IOException e2) {
						log.error("Map set " + setID + " not found", e2);
					}

					int maxMap = propMap.getProperty("map.maxMapNumber", 0);
					log.debug("Number of maps:" + maxMap);

					String strMap = "";

					for(int i = 0; i < maxMap; i++) {
						String strMapTemp = propMap.getProperty("map." + i, "");
						mapList.add(strMapTemp);
						strMap += strMapTemp;
						if(i < maxMap - 1) strMap += "\t";
					}

					String strCompressed = NetUtil.compressString(strMap);
					log.debug("Map uncompressed:" + strMap.length() + " compressed:" + strCompressed.length());

					msg += strCompressed;
				}

				msg += "\n";

				txtpaneRoomChatLog.setText("");
				setRoomButtonsEnabled(false);
				tabLobbyAndRoom.setEnabledAt(1, true);
				tabLobbyAndRoom.setSelectedIndex(1);
				changeCurrentScreenCard(SCREENCARD_LOBBY);

				netPlayerClient.send(msg);
			} catch (Exception e2) {
				log.error("Error on CreateRoom_OK", e2);
			}
		}
		// ルーム作成画面での参戦 button
		if(e.getActionCommand() == "CreateRoom_Join") {
			joinRoom(currentViewDetailRoomID, false);
		}
		// ルーム作成画面での観戦 button
		if(e.getActionCommand() == "CreateRoom_Watch") {
			joinRoom(currentViewDetailRoomID, true);
		}
		// ルーム作成画面でのCancel button
		if(e.getActionCommand() == "CreateRoom_Cancel") {
			changeCurrentScreenCard(SCREENCARD_LOBBY);
		}
		// OK button (Create Room 1P)
		if(e.getActionCommand() == "CreateRoom1P_OK") {
			//singleroomcreate\t[roomName]\t[mode]
			try {
				if(listboxCreateRoom1PModeList.getSelectedIndex() != -1) {
					String strMode = (String)listboxCreateRoom1PModeList.getSelectedValue();
					String strRule = "";
					if(listboxCreateRoom1PRuleList.getSelectedIndex() >= 1) {
						strRule = (String)listboxCreateRoom1PRuleList.getSelectedValue();
					}

					txtpaneRoomChatLog.setText("");
					setRoomButtonsEnabled(false);
					tabLobbyAndRoom.setEnabledAt(1, true);
					tabLobbyAndRoom.setSelectedIndex(1);
					changeCurrentScreenCard(SCREENCARD_LOBBY);

					netPlayerClient.send("singleroomcreate\t" + "\t" + NetUtil.urlEncode(strMode) + "\t" + NetUtil.urlEncode(strRule) + "\n");
				}
			} catch (Exception e2) {
				log.error("Error on CreateRoom1P_OK", e2);
			}
		}
		// Cancel button (Create Room 1P)
		if(e.getActionCommand() == "CreateRoom1P_Cancel") {
			changeCurrentScreenCard(SCREENCARD_LOBBY);
		}
		// OK button (MPRanking)
		if(e.getActionCommand() == "MPRanking_OK") {
			changeCurrentScreenCard(SCREENCARD_LOBBY);
		}
	}

	/*
	 * メッセージ受信
	 */
	public void netOnMessage(NetBaseClient client, String[] message) throws IOException {
		//addSystemChatLog(getCurrentChatLogTextPane(), message[0], Color.green);

		// 接続完了
		if(message[0].equals("welcome")) {
			//welcome\t[VERSION]\t[PLAYERS]
			// チャットログファイル作成
			if(writerLobbyLog == null) {
				try {
					GregorianCalendar currentTime = new GregorianCalendar();
					int month = currentTime.get(Calendar.MONTH) + 1;
					String filename = String.format(
							"log/lobby_%04d_%02d_%02d_%02d_%02d_%02d.txt",
							currentTime.get(Calendar.YEAR), month, currentTime.get(Calendar.DATE), currentTime.get(Calendar.HOUR_OF_DAY),
							currentTime.get(Calendar.MINUTE), currentTime.get(Calendar.SECOND)
					);
					writerLobbyLog = new PrintWriter(filename);
				} catch (Exception e) {
					log.warn("Failed to create lobby log file", e);
				}
			}
			if(writerRoomLog == null) {
				try {
					GregorianCalendar currentTime = new GregorianCalendar();
					int month = currentTime.get(Calendar.MONTH) + 1;
					String filename = String.format(
							"log/room_%04d_%02d_%02d_%02d_%02d_%02d.txt",
							currentTime.get(Calendar.YEAR), month, currentTime.get(Calendar.DATE), currentTime.get(Calendar.HOUR_OF_DAY),
							currentTime.get(Calendar.MINUTE), currentTime.get(Calendar.SECOND)
					);
					writerRoomLog = new PrintWriter(filename);
				} catch (Exception e) {
					log.warn("Failed to create room log file", e);
				}
			}

			String strTemp = String.format(getUIText("SysMsg_ServerConnected"), netPlayerClient.getHost(), netPlayerClient.getPort());
			addSystemChatLogLater(txtpaneLobbyChatLog, strTemp, Color.blue);

			addSystemChatLogLater(txtpaneLobbyChatLog, getUIText("SysMsg_ServerVersion") + message[1], Color.blue);
			addSystemChatLogLater(txtpaneLobbyChatLog, getUIText("SysMsg_NumberOfPlayers") + message[2], Color.blue);
		}
		// ログイン成功
		if(message[0].equals("loginsuccess")) {
			addSystemChatLogLater(txtpaneLobbyChatLog, getUIText("SysMsg_LoginOK"), Color.blue);
			addSystemChatLogLater(txtpaneLobbyChatLog,
								  getUIText("SysMsg_YourNickname") + convTripCode(NetUtil.urlDecode(message[1])), Color.blue);
			addSystemChatLogLater(txtpaneLobbyChatLog, getUIText("SysMsg_YourUID") + netPlayerClient.getPlayerUID(), Color.blue);

			addSystemChatLogLater(txtpaneLobbyChatLog, getUIText("SysMsg_SendRuleDataStart"), Color.blue);
			sendMyRuleDataToServer();
		}
		// ログイン失敗
		if(message[0].equals("loginfail")) {
			setLobbyButtonsEnabled(1);
			String reason = "";
			for(int i = 1; i < message.length; i++) {
				reason += message[i] + " ";
			}
			addSystemChatLogLater(txtpaneLobbyChatLog, getUIText("SysMsg_LoginFail") + reason, Color.red);
		}
		// ルール data送信成功
		if(message[0].equals("ruledatasuccess")) {
			addSystemChatLogLater(txtpaneLobbyChatLog, getUIText("SysMsg_SendRuleDataOK"), Color.blue);

			// Listener呼び出し
			for(NetLobbyListener l: listeners) {
				l.netlobbyOnLoginOK(this, netPlayerClient);
			}
			if(netDummyMode != null) netDummyMode.netlobbyOnLoginOK(this, netPlayerClient);

			setLobbyButtonsEnabled(1);
		}
		// ルール data送信失敗
		if(message[0].equals("ruledatafail")) {
			sendMyRuleDataToServer();
		}
		// Rule receive (for rule-locked games)
		if(message[0].equals("rulelock")) {
			if(ruleOptLock == null) ruleOptLock = new RuleOptions();

			String strRuleData = NetUtil.decompressString(message[1]);

			CustomProperties prop = new CustomProperties();
			prop.decode(strRuleData);
			ruleOptLock.readProperty(prop, 0);

			log.info("Received rule data (" + ruleOptLock.strRuleName + ")");
		}
		// Rated-game rule list
		if(message[0].equals("rulelist")) {
			int style = Integer.parseInt(message[1]);

			if(style < listRatedRuleName.length) {
				for(int i = 0; i < message.length - 2; i++) {
					String name = NetUtil.urlDecode(message[2 + i]);
					listRatedRuleName[style].add(name);
				}
			}

			if(style == 0) {
				listmodelCreateRoomRuleList.clear();
				listmodelCreateRoomRuleList.addElement(getUIText("CreateRoom_YourRule"));
				listboxCreateRoomRuleList.setSelectedIndex(0);

				listmodelCreateRoom1PRuleList.clear();
				listmodelCreateRoom1PRuleList.addElement(getUIText("CreateRoom1P_YourRule"));
				listboxCreateRoom1PRuleList.setSelectedIndex(0);

				for(int i = 0; i < listRatedRuleName[style].size(); i++) {
					String name = (String)listRatedRuleName[style].get(i);
					listmodelCreateRoomRuleList.addElement(name);
					listmodelCreateRoom1PRuleList.addElement(name);
				}
			}
		}
		// Playerリスト
		if(message[0].equals("playerlist") || message[0].equals("playerupdate") ||
		   message[0].equals("playernew") || message[0].equals("playerlogout"))
		{
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					updateLobbyUserList();
				}
			});

			if(tabLobbyAndRoom.isEnabledAt(1)) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						updateRoomUserList();
					}
				});

				if(message[0].equals("playerlogout")) {
					NetPlayerInfo p = new NetPlayerInfo(message[1]);
					NetPlayerInfo p2 = netPlayerClient.getYourPlayerInfo();
					if((p != null) && (p2 != null) && (p.roomID == p2.roomID)) {
						String strTemp = "";
						if(p.strHost.length() > 0) {
							strTemp = String.format(getUIText("SysMsg_LeaveRoomWithHost"), getPlayerNameWithTripCode(p), p.strHost);
						} else {
							strTemp = String.format(getUIText("SysMsg_LeaveRoom"), getPlayerNameWithTripCode(p));
						}
						addSystemChatLogLater(txtpaneRoomChatLog, strTemp, Color.blue);
					}
				}
			}
		}
		// Player入室
		if(message[0].equals("playerenter")) {
			int uid = Integer.parseInt(message[1]);
			NetPlayerInfo pInfo = netPlayerClient.getPlayerInfoByUID(uid);

			if(pInfo != null) {
				String strTemp = "";
				if(pInfo.strHost.length() > 0) {
					strTemp = String.format(getUIText("SysMsg_EnterRoomWithHost"), getPlayerNameWithTripCode(pInfo), pInfo.strHost);
				} else {
					strTemp = String.format(getUIText("SysMsg_EnterRoom"), getPlayerNameWithTripCode(pInfo));
				}
				addSystemChatLogLater(txtpaneRoomChatLog, strTemp, Color.blue);
			}
		}
		// Player退出
		if(message[0].equals("playerleave")) {
			int uid = Integer.parseInt(message[1]);
			NetPlayerInfo pInfo = netPlayerClient.getPlayerInfoByUID(uid);

			if(pInfo != null) {
				String strTemp = "";
				if(pInfo.strHost.length() > 0) {
					strTemp = String.format(getUIText("SysMsg_LeaveRoomWithHost"), getPlayerNameWithTripCode(pInfo), pInfo.strHost);
				} else {
					strTemp = String.format(getUIText("SysMsg_LeaveRoom"), getPlayerNameWithTripCode(pInfo));
				}
				addSystemChatLogLater(txtpaneRoomChatLog, strTemp, Color.blue);
			}
		}
		// チーム変更
		if(message[0].equals("changeteam")) {
			int uid = Integer.parseInt(message[1]);
			NetPlayerInfo pInfo = netPlayerClient.getPlayerInfoByUID(uid);

			if(pInfo != null) {
				String strTeam = "";
				String strTemp = "";

				if(message.length > 3) {
					strTeam = NetUtil.urlDecode(message[3]);
					strTemp = String.format(getUIText("SysMsg_ChangeTeam"), getPlayerNameWithTripCode(pInfo), strTeam);
				} else {
					strTemp = String.format(getUIText("SysMsg_ChangeTeam_None"), getPlayerNameWithTripCode(pInfo));
				}

				addSystemChatLogLater(getCurrentChatLogTextPane(), strTemp, Color.blue);
			}
		}
		// ルームリスト
		if(message[0].equals("roomlist")) {
			int size = Integer.parseInt(message[1]);

			tablemodelRoomList.setRowCount(0);
			for(int i = 0; i < size; i++) {
				NetRoomInfo r = new NetRoomInfo(message[2 + i]);
				tablemodelRoomList.addRow(createRoomListRowData(r));
			}
		}
		// 新規ルーム出現
		if(message[0].equals("roomcreate")) {
			NetRoomInfo r = new NetRoomInfo(message[1]);
			tablemodelRoomList.addRow(createRoomListRowData(r));
		}
		// ルーム情報更新
		if(message[0].equals("roomupdate")) {
			NetRoomInfo r = new NetRoomInfo(message[1]);
			int columnID = tablemodelRoomList.findColumn(getUIText(ROOMTABLE_COLUMNNAMES[0]));

			for(int i = 0; i < tablemodelRoomList.getRowCount(); i++) {
				String strID = (String)tablemodelRoomList.getValueAt(i, columnID);
				int roomID = Integer.parseInt(strID);

				if(roomID == r.roomID) {
					String[] rowData = createRoomListRowData(r);
					for(int j = 0; j < rowData.length; j++) {
						tablemodelRoomList.setValueAt(rowData[j], i, j);
					}
					break;
				}
			}
		}
		// ルーム消滅
		if(message[0].equals("roomdelete")) {
			NetRoomInfo r = new NetRoomInfo(message[1]);
			int columnID = tablemodelRoomList.findColumn(getUIText(ROOMTABLE_COLUMNNAMES[0]));

			for(int i = 0; i < tablemodelRoomList.getRowCount(); i++) {
				String strID = (String)tablemodelRoomList.getValueAt(i, columnID);
				int roomID = Integer.parseInt(strID);

				if(roomID == r.roomID) {
					tablemodelRoomList.removeRow(i);
					break;
				}
			}

			if((r.roomID == currentViewDetailRoomID) && (currentScreenCardNumber == SCREENCARD_CREATEROOM)) {
				changeCurrentScreenCard(SCREENCARD_LOBBY);
			}
		}
		// ルーム作成・入室成功
		if(message[0].equals("roomcreatesuccess") || message[0].equals("roomjoinsuccess")) {
			int roomID = Integer.parseInt(message[1]);
			int seatID = Integer.parseInt(message[2]);
			int queueID = Integer.parseInt(message[3]);

			netPlayerClient.getYourPlayerInfo().roomID = roomID;
			netPlayerClient.getYourPlayerInfo().seatID = seatID;
			netPlayerClient.getYourPlayerInfo().queueID = queueID;

			if(roomID != -1) {
				NetRoomInfo roomInfo = netPlayerClient.getRoomInfo(roomID);
				NetPlayerInfo pInfo = netPlayerClient.getYourPlayerInfo();

				if((seatID == -1) && (queueID == -1)) {
					String strTemp = String.format(getUIText("SysMsg_StatusChange_Spectator"), getPlayerNameWithTripCode(pInfo));
					addSystemChatLogLater(txtpaneRoomChatLog, strTemp, Color.blue);
					setRoomJoinButtonVisible(true);
				} else if(seatID == -1) {
					String strTemp = String.format(getUIText("SysMsg_StatusChange_Queue"), getPlayerNameWithTripCode(pInfo));
					addSystemChatLogLater(txtpaneRoomChatLog, strTemp, Color.blue);
					setRoomJoinButtonVisible(false);
				} else {
					String strTemp = String.format(getUIText("SysMsg_StatusChange_Joined"), getPlayerNameWithTripCode(pInfo));
					addSystemChatLogLater(txtpaneRoomChatLog, strTemp, Color.blue);
					setRoomJoinButtonVisible(false);
				}

				if((netPlayerClient != null) && (netPlayerClient.getRoomInfo(roomID) != null)) {
					if(netPlayerClient.getRoomInfo(roomID).singleplayer) {
						btnRoomButtonsJoin.setVisible(false);
						btnRoomButtonsSitOut.setVisible(false);
						btnRoomButtonsViewSetting.setVisible(false);
						btnRoomButtonsRanking.setVisible(false);
					} else {
						btnRoomButtonsViewSetting.setVisible(true);
						btnRoomButtonsRanking.setVisible(netPlayerClient.getRoomInfo(roomID).rated);
					}
				}

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						setRoomButtonsEnabled(true);
						updateRoomUserList();
					}
				});

				String strTitle = roomInfo.strName;
				this.setTitle(getUIText("Title_NetLobby") + " - " + strTitle);
				tabLobbyAndRoom.setTitleAt(1, getUIText("Lobby_Tab_Room") + strTitle);

				addSystemChatLogLater(txtpaneRoomChatLog, getUIText("SysMsg_RoomJoin_Title") + strTitle, Color.blue);
				addSystemChatLogLater(txtpaneRoomChatLog, getUIText("SysMsg_RoomJoin_ID") + roomInfo.roomID, Color.blue);
				if(roomInfo.ruleLock) {
					addSystemChatLogLater(txtpaneRoomChatLog, getUIText("SysMsg_RoomJoin_Rule") + roomInfo.ruleName, Color.blue);
				}

				setLobbyButtonsEnabled(2);
				changeCurrentScreenCard(SCREENCARD_LOBBY);

				// Listener呼び出し
				for(NetLobbyListener l: listeners) {
					l.netlobbyOnRoomJoin(this, netPlayerClient, roomInfo);
				}
				if(netDummyMode != null) netDummyMode.netlobbyOnRoomJoin(this, netPlayerClient, roomInfo);
			} else {
				addSystemChatLogLater(txtpaneRoomChatLog, getUIText("SysMsg_RoomJoin_Lobby"), Color.blue);

				this.setTitle(getUIText("Title_NetLobby"));
				tabLobbyAndRoom.setSelectedIndex(0);
				tabLobbyAndRoom.setEnabledAt(1, false);
				tabLobbyAndRoom.setTitleAt(1, getUIText("Lobby_Tab_NoRoom"));

				setLobbyButtonsEnabled(1);
				changeCurrentScreenCard(SCREENCARD_LOBBY);

				// Listener呼び出し
				for(NetLobbyListener l: listeners) {
					l.netlobbyOnRoomLeave(this, netPlayerClient);
				}
				if(netDummyMode != null) netDummyMode.netlobbyOnRoomLeave(this, netPlayerClient);
			}
		}
		// ルーム入室失敗
		if(message[0].equals("roomjoinfail")) {
			addSystemChatLogLater(txtpaneRoomChatLog, getUIText("SysMsg_RoomJoinFail"), Color.red);
		}
		// Map receive
		if(message[0].equals("map")) {
			String strDecompressed = NetUtil.decompressString(message[1]);
			String[] strMaps = strDecompressed.split("\t");

			mapList.clear();

			int maxMap = strMaps.length;
			for(int i = 0; i < maxMap; i++) {
				mapList.add(strMaps[i]);
			}

			log.debug("Received " + mapList.size() + " maps");
		}
		// Lobby chat
		if(message[0].equals("lobbychat")) {
			int uid = Integer.parseInt(message[1]);
			NetPlayerInfo pInfo = netPlayerClient.getPlayerInfoByUID(uid);

			if(pInfo != null) {
				String strMsgBody = NetUtil.urlDecode(message[3]);
				addUserChatLogLater(txtpaneLobbyChatLog, getPlayerNameWithTripCode(pInfo), strMsgBody);
			}
		}
		// Room chat
		if(message[0].equals("chat")) {
			int uid = Integer.parseInt(message[1]);
			NetPlayerInfo pInfo = netPlayerClient.getPlayerInfoByUID(uid);

			if(pInfo != null) {
				String strMsgBody = NetUtil.urlDecode(message[3]);
				addUserChatLogLater(txtpaneRoomChatLog, getPlayerNameWithTripCode(pInfo), strMsgBody);
			}
		}
		// 参戦状態変更
		if(message[0].equals("changestatus")) {
			int uid = Integer.parseInt(message[2]);
			NetPlayerInfo pInfo = netPlayerClient.getPlayerInfoByUID(uid);

			if(pInfo != null) {
				if(message[1].equals("watchonly")) {
					String strTemp = String.format(getUIText("SysMsg_StatusChange_Spectator"), getPlayerNameWithTripCode(pInfo));
					addSystemChatLogLater(txtpaneRoomChatLog, strTemp, Color.blue);
					if(uid == netPlayerClient.getPlayerUID()) setRoomJoinButtonVisible(true);
				} else if(message[1].equals("joinqueue")) {
					String strTemp = String.format(getUIText("SysMsg_StatusChange_Queue"), getPlayerNameWithTripCode(pInfo));
					addSystemChatLogLater(txtpaneRoomChatLog, strTemp, Color.blue);
					if(uid == netPlayerClient.getPlayerUID()) setRoomJoinButtonVisible(false);
				} else if(message[1].equals("joinseat")) {
					String strTemp = String.format(getUIText("SysMsg_StatusChange_Joined"), getPlayerNameWithTripCode(pInfo));
					addSystemChatLogLater(txtpaneRoomChatLog, strTemp, Color.blue);
					if(uid == netPlayerClient.getPlayerUID()) setRoomJoinButtonVisible(false);
				}
			}

			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					updateRoomUserList();
				}
			});
		}
		// Automatically start timer開始
		if(message[0].equals("autostartbegin")) {
			String strTemp = String.format(getUIText("SysMsg_AutoStartBegin"), message[1]);
			addSystemChatLogLater(txtpaneRoomChatLog, strTemp, new Color(64, 128, 0));
		}
		// game start
		if(message[0].equals("start")) {
			addSystemChatLogLater(txtpaneRoomChatLog, getUIText("SysMsg_GameStart"), new Color(0, 128, 0));
			tablemodelGameStat.setRowCount(0);

			if(netPlayerClient.getYourPlayerInfo().seatID != -1) {
				btnRoomButtonsSitOut.setEnabled(false);
				btnRoomButtonsTeamChange.setEnabled(false);
				roomTopBarCardLayout.first(subpanelRoomTopBar);
			}
		}
		// 死亡
		if(message[0].equals("dead")) {
			int uid = Integer.parseInt(message[1]);
			String name = convTripCode(NetUtil.urlDecode(message[2]));

			if(message.length > 6) {
				String strTemp = String.format(getUIText("SysMsg_KO"), convTripCode(NetUtil.urlDecode(message[6])), name);
				addSystemChatLogLater(txtpaneRoomChatLog, strTemp, new Color(0, 128, 0));
			}

			if(uid == netPlayerClient.getPlayerUID()) {
				btnRoomButtonsSitOut.setEnabled(true);
				btnRoomButtonsTeamChange.setEnabled(true);
			}
		}
		// ゲーム結果
		if(message[0].equals("gstat")) {
			String[] rowdata = new String[12];
			int myRank = Integer.parseInt(message[4]);

			rowdata[0] = Integer.toString(myRank);			// 順位
			rowdata[1] = convTripCode(NetUtil.urlDecode(message[3]));	// Name
			rowdata[2] = message[5];						//  Attack count
			rowdata[3] = message[6];						// APM
			rowdata[4] = message[7];						// 消去count
			rowdata[5] = message[8];						// LPM
			rowdata[6] = message[9];						// ピースcount
			rowdata[7] = message[10];						// PPS
			rowdata[8] = GeneralUtil.getTime(Integer.parseInt(message[11]));	//  time
			rowdata[9] = message[12];						// KO
			rowdata[10] = message[13];						// 勝count
			rowdata[11] = message[14];						//  count

			int insertPos = 0;
			for(int i = 0; i < tablemodelGameStat.getRowCount(); i++) {
				String strRank = (String)tablemodelGameStat.getValueAt(i, 0);
				int rank = Integer.parseInt(strRank);

				if(myRank > rank) {
					insertPos = i + 1;
				}
			}

			tablemodelGameStat.insertRow(insertPos, rowdata);

			if(writerRoomLog != null) {
				writerRoomLog.print("[" + getCurrentTimeAsString() + "] ");

				for(int i = 0; i < rowdata.length; i++) {
					writerRoomLog.print(rowdata[i]);
					if(i < rowdata.length - 1) writerRoomLog.print(",");
					else writerRoomLog.print("\n");
				}

				writerRoomLog.flush();
			}
		}
		// game finished
		if(message[0].equals("finish")) {
			addSystemChatLogLater(txtpaneRoomChatLog, getUIText("SysMsg_GameEnd"), new Color(0, 128, 0));

			if((message.length > 3) && (message[3].length() > 0)) {
				boolean flagTeamWin = false;
				if(message.length > 4) flagTeamWin = Boolean.parseBoolean(message[4]);

				String strWinner = "";
				if(flagTeamWin) strWinner = String.format(getUIText("SysMsg_WinnerTeam"), NetUtil.urlDecode(message[3]));
				else strWinner = String.format(getUIText("SysMsg_Winner"), convTripCode(NetUtil.urlDecode(message[3])));
				addSystemChatLogLater(txtpaneRoomChatLog, strWinner, new Color(0, 128, 0));
			}

			btnRoomButtonsSitOut.setEnabled(true);
			btnRoomButtonsTeamChange.setEnabled(true);
		}
		// Rating change
		if(message[0].equals("rating")) {
			String strPlayerName = convTripCode(NetUtil.urlDecode(message[3]));
			int ratingNow = Integer.parseInt(message[4]);
			int ratingChange = Integer.parseInt(message[5]);
			String strTemp = String.format(getUIText("SysMsg_Rating"), strPlayerName, ratingNow, ratingChange);
			addSystemChatLogLater(txtpaneRoomChatLog, strTemp, new Color(0, 128, 0));
		}
		// Multiplayer Leaderboard
		if(message[0].equals("mpranking")) {
			int style = Integer.parseInt(message[1]);
			int myRank = Integer.parseInt(message[2]);

			tablemodelMPRanking[style].setRowCount(0);

			String strPData = NetUtil.decompressString(message[3]);
			String[] strPDataA = strPData.split("\t");

			for(int i = 0; i < strPDataA.length; i++) {
				String[] strRankData = strPDataA[i].split(";");
				String[] strRowData = new String[MPRANKING_COLUMNNAMES.length];
				int rank = Integer.parseInt(strRankData[0]);
				if(rank == -1) {
					strRowData[0] = "N/A";
				} else {
					strRowData[0] = Integer.toString(rank + 1);
				}
				strRowData[1] = convTripCode(NetUtil.urlDecode(strRankData[1]));
				strRowData[2] = strRankData[2];
				strRowData[3] = strRankData[3];
				strRowData[4] = strRankData[4];
				tablemodelMPRanking[style].addRow(strRowData);
			}

			if(myRank == -1) {
				int tableRowMax = tablemodelMPRanking[style].getRowCount();
				tableMPRanking[style].getSelectionModel().setSelectionInterval(tableRowMax - 1, tableRowMax - 1);
			} else {
				tableMPRanking[style].getSelectionModel().setSelectionInterval(myRank, myRank);
			}
		}

		// Listener呼び出し
		if(listeners != null) {
			for(NetLobbyListener l: listeners) {
				l.netlobbyOnMessage(this, netPlayerClient, message);
			}
		}
		if(netDummyMode != null) netDummyMode.netlobbyOnMessage(this, netPlayerClient, message);
	}

	/*
	 * 切断されたとき
	 */
	public void netOnDisconnect(NetBaseClient client, Throwable ex) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				setLobbyButtonsEnabled(0);
				setRoomButtonsEnabled(false);
				tablemodelRoomList.setRowCount(0);
			}
		});

		if(ex != null) {
			addSystemChatLogLater(getCurrentChatLogTextPane(), getUIText("SysMsg_DisconnectedError") + "\n" + ex.getLocalizedMessage(), Color.red);
			log.info("Server Disconnected", ex);
		} else {
			addSystemChatLogLater(getCurrentChatLogTextPane(), getUIText("SysMsg_DisconnectedOK"), new Color(128, 0, 0));
			log.info("Server Disconnected (null)");
		}

		// Listener呼び出し
		if(listeners != null) {
			for(NetLobbyListener l: listeners) {
				if(l != null) {
					l.netlobbyOnDisconnect(this, netPlayerClient, ex);
				}
			}
		}
		if(netDummyMode != null) netDummyMode.netlobbyOnDisconnect(this, netPlayerClient, ex);
	}

	/**
	 * Add an new NetLobbyListener, but don't add NetDummyMode!
	 * @param l A NetLobbyListener to add
	 */
	public void addListener(NetLobbyListener l) {
		listeners.add(l);
	}

	/**
	 * Remove a NetLobbyListener from the listeners list
	 * @param l NetLobbyListener to remove
	 * @return true if removed, false if not found or already removed
	 */
	public boolean removeListener(NetLobbyListener l) {
		return listeners.remove(l);
	}

	/**
	 * Set new game mode
	 * @param m Mode
	 */
	public void setNetDummyMode(NetDummyMode m) {
		netDummyMode = m;
	}

	/**
	 * Get current game mode
	 * @return Current game mode
	 */
	public NetDummyMode getNetDummyMode() {
		return netDummyMode;
	}

	/**
	 * メイン関count
	 * @param args コマンドLines引count
	 */
	public static void main(String[] args) {
		PropertyConfigurator.configure("config/etc/log.cfg");
		NetLobbyFrame frame = new NetLobbyFrame();
		frame.init();
		frame.setVisible(true);
	}

	/**
	 * テキスト input 欄用ポップアップMenu
	 * <a href="http://terai.xrea.jp/Swing/DefaultEditorKit.html">出展</a>
	 */
	protected class TextComponentPopupMenu extends JPopupMenu {
		private static final long serialVersionUID = 1L;

		private Action cutAction;
		private Action copyAction;
		@SuppressWarnings("unused")
		private Action pasteAction;
		private Action deleteAction;
		private Action selectAllAction;

		public TextComponentPopupMenu(final JTextComponent field) {
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
			JTextComponent field = (JTextComponent) c;
			boolean flg = field.getSelectedText() != null;
			cutAction.setEnabled(flg);
			copyAction.setEnabled(flg);
			deleteAction.setEnabled(flg);
			selectAllAction.setEnabled(field.isFocusOwner());
			super.show(c, x, y);
		}
	}

	/**
	 * リストボックス用ポップアップMenu
	 */
	protected class ListBoxPopupMenu extends JPopupMenu {
		private static final long serialVersionUID = 1L;

		private JList listbox;
		private Action copyAction;

		public ListBoxPopupMenu(JList l) {
			super();

			this.listbox = l;

			add(copyAction = new AbstractAction(getUIText("Popup_Copy")) {
				private static final long serialVersionUID = 1L;
				public void actionPerformed(ActionEvent e) {
					if(listbox == null) return;
					Object selectedObj = listbox.getSelectedValue();

					if((selectedObj != null) && (selectedObj instanceof String)) {
						String selectedString = (String)selectedObj;
						StringSelection ss = new StringSelection(selectedString);
						Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
						clipboard.setContents(ss, ss);
					}
				}
			});
		}

		@Override
		public void show(Component c, int x, int y) {
			if(listbox.getSelectedIndex() != -1) {
				copyAction.setEnabled(true);
				super.show(c, x, y);
			}
		}
	}

	/**
	 * サーバー選択リストボックス用ポップアップMenu
	 */
	protected class ServerSelectListBoxPopupMenu extends JPopupMenu {
		private static final long serialVersionUID = 1L;

		@SuppressWarnings("unused")
		private Action connectAction;
		@SuppressWarnings("unused")
		private Action deleteAction;
		@SuppressWarnings("unused")
		private Action setObserverAction;

		public ServerSelectListBoxPopupMenu() {
			super();

			add(connectAction = new AbstractAction(getUIText("Popup_ServerSelect_Connect")) {
				private static final long serialVersionUID = 1L;
				public void actionPerformed(ActionEvent e) {
					serverSelectConnectButtonClicked();
				}
			});
			add(deleteAction = new AbstractAction(getUIText("Popup_ServerSelect_Delete")) {
				private static final long serialVersionUID = 1L;
				public void actionPerformed(ActionEvent e) {
					serverSelectDeleteButtonClicked();
				}
			});
			add(setObserverAction = new AbstractAction(getUIText("Popup_ServerSelect_SetObserver")) {
				private static final long serialVersionUID = 1L;
				public void actionPerformed(ActionEvent e) {
					serverSelectSetObserverButtonClicked();
				}
			});
		}

		@Override
		public void show(Component c, int x, int y) {
			if(listboxServerList.getSelectedIndex() != -1) {
				super.show(c, x, y);
			}
		}
	}

	/**
	 * サーバー選択リストボックス用MouseAdapter
	 */
	protected class ServerSelectListBoxMouseAdapter extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			if((e.getClickCount() == 2) && (e.getButton() == MouseEvent.BUTTON1)) {
				serverSelectConnectButtonClicked();
			}
		}
	}

	/**
	 * ルーム一覧 table用ポップアップMenu
	 */
	protected class RoomTablePopupMenu extends JPopupMenu {
		private static final long serialVersionUID = 1L;

		private Action joinAction;
		private Action watchAction;
		private Action detailAction;

		public RoomTablePopupMenu() {
			super();

			add(joinAction = new AbstractAction(getUIText("Popup_RoomTable_Join")) {
				private static final long serialVersionUID = 1L;
				public void actionPerformed(ActionEvent evt) {
					int row = tableRoomList.getSelectedRow();
					if(row != -1) {
						int columnID = tablemodelRoomList.findColumn(getUIText(ROOMTABLE_COLUMNNAMES[0]));
						String strRoomID = (String)tablemodelRoomList.getValueAt(row, columnID);
						int roomID = Integer.parseInt(strRoomID);
						joinRoom(roomID, false);
					}
				}
			});
			add(watchAction = new AbstractAction(getUIText("Popup_RoomTable_Watch")) {
				private static final long serialVersionUID = 1L;
				public void actionPerformed(ActionEvent evt) {
					int row = tableRoomList.getSelectedRow();
					if(row != -1) {
						int columnID = tablemodelRoomList.findColumn(getUIText(ROOMTABLE_COLUMNNAMES[0]));
						String strRoomID = (String)tablemodelRoomList.getValueAt(row, columnID);
						int roomID = Integer.parseInt(strRoomID);
						joinRoom(roomID, true);
					}
				}
			});
			add(detailAction = new AbstractAction(getUIText("Popup_RoomTable_Detail")) {
				private static final long serialVersionUID = 1L;
				public void actionPerformed(ActionEvent evt) {
					int row = tableRoomList.getSelectedRow();
					if(row != -1) {
						int columnID = tablemodelRoomList.findColumn(getUIText(ROOMTABLE_COLUMNNAMES[0]));
						String strRoomID = (String)tablemodelRoomList.getValueAt(row, columnID);
						int roomID = Integer.parseInt(strRoomID);
						viewRoomDetail(roomID);
					}
				}
			});
		}

		@Override
		public void show(Component c, int x, int y) {
			if(tableRoomList.getSelectedRow() != -1) {
				joinAction.setEnabled(true);
				watchAction.setEnabled(true);
				detailAction.setEnabled(true);
				super.show(c, x, y);
			}
		}
	}

	/**
	 * ルーム一覧 table用MouseAdapter
	 */
	protected class RoomTableMouseAdapter extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			if((e.getClickCount() == 2) && (e.getButton() == MouseEvent.BUTTON1)) {
				Point pt = e.getPoint();
				int row = tableRoomList.rowAtPoint(pt);

				if(row != -1) {
					int columnID = tablemodelRoomList.findColumn(getUIText(ROOMTABLE_COLUMNNAMES[0]));
					String strRoomID = (String)tablemodelRoomList.getValueAt(row, columnID);
					int roomID = Integer.parseInt(strRoomID);
					joinRoom(roomID, false);
				}
			}
		}
	}

	/**
	 * ルーム一覧 table用KeyAdapter
	 */
	protected class RoomTableKeyAdapter extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_ENTER) {
				e.consume();
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_ENTER) {
				int row = tableRoomList.getSelectedRow();
				if(row != -1) {
					int columnID = tablemodelRoomList.findColumn(getUIText(ROOMTABLE_COLUMNNAMES[0]));
					String strRoomID = (String)tablemodelRoomList.getValueAt(row, columnID);
					int roomID = Integer.parseInt(strRoomID);
					joinRoom(roomID, false);
				}
				e.consume();
			}
		}
	}

	/**
	 * ログ表示欄用ポップアップMenu
	 */
	protected class LogPopupMenu extends JPopupMenu {
		private static final long serialVersionUID = 1L;

		private Action copyAction;
		private Action selectAllAction;
		@SuppressWarnings("unused")
		private Action clearAction;

		public LogPopupMenu(final JTextComponent field) {
			super();

			add(copyAction = new AbstractAction(getUIText("Popup_Copy")) {
				private static final long serialVersionUID = 1L;
				public void actionPerformed(ActionEvent evt) {
					field.copy();
				}
			});
			add(selectAllAction = new AbstractAction(getUIText("Popup_SelectAll")) {
				private static final long serialVersionUID = 1L;
				public void actionPerformed(ActionEvent evt) {
					field.selectAll();
				}
			});
			add(clearAction = new AbstractAction(getUIText("Popup_Clear")) {
				private static final long serialVersionUID = 1L;
				public void actionPerformed(ActionEvent evt) {
					field.setText(null);
				}
			});
		}

		@Override
		public void show(Component c, int x, int y) {
			JTextComponent field = (JTextComponent) c;
			boolean flg = field.getSelectedText() != null;
			copyAction.setEnabled(flg);
			selectAllAction.setEnabled(field.isFocusOwner());
			super.show(c, x, y);
		}
	}

	/**
	 * ログ表示欄用KeyAdapter
	 */
	protected class LogKeyAdapter extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			if( (e.getKeyCode() != KeyEvent.VK_UP) && (e.getKeyCode() != KeyEvent.VK_DOWN) &&
			    (e.getKeyCode() != KeyEvent.VK_LEFT) && (e.getKeyCode() != KeyEvent.VK_RIGHT) &&
			    (e.getKeyCode() != KeyEvent.VK_HOME) && (e.getKeyCode() != KeyEvent.VK_END) &&
			    (e.getKeyCode() != KeyEvent.VK_PAGE_UP) && (e.getKeyCode() != KeyEvent.VK_PAGE_DOWN) &&
			    ((e.getKeyCode() != KeyEvent.VK_A) || (e.isControlDown() == false)) &&
			    ((e.getKeyCode() != KeyEvent.VK_C) || (e.isControlDown() == false)) &&
			    (!e.isAltDown()) )
			{
				e.consume();
			}
		}
		@Override
		public void keyTyped(KeyEvent e) {
			e.consume();
		}
	}
}
