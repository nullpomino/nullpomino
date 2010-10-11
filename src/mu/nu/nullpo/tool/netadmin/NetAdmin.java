package mu.nu.nullpo.tool.netadmin;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
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
import java.util.Calendar;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import mu.nu.nullpo.game.net.NetBaseClient;
import mu.nu.nullpo.game.net.NetMessageListener;
import mu.nu.nullpo.game.net.NetPlayerInfo;
import mu.nu.nullpo.game.net.NetServerBan;
import mu.nu.nullpo.game.net.NetUtil;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.util.CustomProperties;
import net.clarenceho.crypto.RC4;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import biz.source_code.base64Coder.Base64Coder;

/**
 * NetAdmin - NetServer admin tool
 */
public class NetAdmin extends JFrame implements ActionListener, NetMessageListener {
	//***** Constants *****
	/** Serial Version ID */
	private static final long serialVersionUID = 1L;

	/** Constants for each screen-card */
	private static final int SCREENCARD_LOGIN = 0, SCREENCARD_LOBBY = 1;

	/** Names for each screen-card */
	private static final String[] SCREENCARD_NAMES = {"Login", "Lobby"};

	/** User type names */
	private static final String[] USERTABLE_USERTYPES = {
		"UserTable_Type_Guest", "UserTable_Type_Player", "UserTable_Type_Observer", "UserTable_Type_Admin"
	};

	/** User table column names. These strings will be passed to getUIText(String) subroutine. */
	private static final String[] USERTABLE_COLUMNNAMES = {
		"UserTable_IP", "UserTable_Hostname", "UserTable_Type", "UserTable_Name"
	};

	/** Multiplayer leaderboard column names. These strings will be passed to getUIText(String) subroutine. */
	private static final String[] MPRANKING_COLUMNNAMES  = {
		"MPRanking_Rank", "MPRanking_Name", "MPRanking_Rating", "MPRanking_PlayCount", "MPRanking_WinCount"
	};

	//***** Variables *****
	/** Log */
	static Logger log = Logger.getLogger(NetAdmin.class);

	/** ServerAdmin properties */
	private static CustomProperties propConfig;

	/** Default language file */
	private static CustomProperties propLangDefault;

	/** Property file for GUI translations */
	private static CustomProperties propLang;

	/** NetBaseClient */
	private static NetBaseClient client;

	/** true if disconnection is intended (If false, it will display error message) */
	private static boolean isWantedDisconnect;

	/** true if server shutdown is requested */
	private static boolean isShutdownRequested;

	/** Hostname of the server */
	private static String strServerHost;

	/** Port-number of the server */
	private static int serverPort;

	/** Your IP */
	private static String strMyIP;

	/** Your Hostname */
	private static String strMyHostname;

	//***** Main GUI elements *****
	/** Layout manager for main screen */
	private CardLayout contentPaneCardLayout;

	/** Current screen-card number */
	private int currentScreenCardNumber;

	//***** Login screen elements *****
	/** Login Message label */
	private JLabel labelLoginMessage;

	/** Server textbox */
	private JTextField txtfldServer;

	/** Username textbox */
	private JTextField txtfldUsername;

	/** Password textbox */
	private JPasswordField passfldPassword;

	/** Remember Username checkbox */
	private JCheckBox chkboxRememberUsername;

	/** Remember Password checkbox */
	private JCheckBox chkboxRememberPassword;

	/** Login button */
	private JButton btnLogin;

	//***** Lobby screen elements *****
	/** Console Log textpane */
	private JTextPane txtpaneConsoleLog;

	/** Console Command textbox */
	private JTextField txtfldConsoleCommand;

	/** Console Command Execute button */
	private JButton btnConsoleCommandExecute;

	/** Users table data */
	private DefaultTableModel tablemodelUsers;

	/** Users table component */
	private JTable tableUsers;

	/** MPRanking table data */
	private DefaultTableModel[] tablemodelMPRanking;

	/** MPRanking table component */
	private JTable[] tableMPRanking;

	/** Load/Refresh Ranking button */
	private JButton btnRankingLoad;

	/**
	 * Constructor
	 */
	public NetAdmin() {
		super();
		init();
	}

	/**
	 * Init
	 */
	private void init() {
		// Load config file
		propConfig = new CustomProperties();
		try {
			FileInputStream in = new FileInputStream("config/setting/netadmin.cfg");
			propConfig.load(in);
			in.close();
		} catch (IOException e) {}

		// Load language files
		propLangDefault = new CustomProperties();
		try {
			FileInputStream in = new FileInputStream("config/lang/netadmin_default.properties");
			propLangDefault.load(in);
			in.close();
		} catch (IOException e) {
			log.error("Failed to load default UI language file", e);
		}
		propLang = new CustomProperties();
		try {
			FileInputStream in = new FileInputStream("config/lang/netadmin_" + Locale.getDefault().getCountry() + ".properties");
			propLang.load(in);
			in.close();
		} catch (IOException e) {}

		// Set look&feel
		try {
			CustomProperties propSwingConfig = new CustomProperties();
			FileInputStream in = new FileInputStream("config/setting/swing.cfg");
			propSwingConfig.load(in);
			in.close();

			if(propSwingConfig.getProperty("option.usenativelookandfeel", true) == true) {
				try {
					UIManager.getInstalledLookAndFeels();
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch(Exception e) {
					log.warn("Failed to set native look&feel", e);
				}
			}
		} catch (Exception e) {}

		// Set close action
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				shutdown();
			}
		});

		setTitle(getUIText("Title_ServerAdmin"));
		initUI();

		this.setSize(propConfig.getProperty("mainwindow.width", 500), propConfig.getProperty("mainwindow.height", 450));
		this.setLocation(propConfig.getProperty("mainwindow.x", 0), propConfig.getProperty("mainwindow.y", 0));
		this.setVisible(true);
	}

	/**
	 * Init GUI
	 */
	private void initUI() {
		contentPaneCardLayout = new CardLayout();
		this.getContentPane().setLayout(contentPaneCardLayout);

		initLoginUI();
		initLobbyUI();

		changeCurrentScreenCard(SCREENCARD_LOGIN);
	}

	/**
	 * Init login screen
	 */
	private void initLoginUI() {
		// Main panel
		JPanel mpLoginOwner = new JPanel(new BorderLayout());
		this.getContentPane().add(mpLoginOwner, SCREENCARD_NAMES[SCREENCARD_LOGIN]);
		JPanel mpLogin = new JPanel();
		mpLogin.setLayout(new BoxLayout(mpLogin, BoxLayout.Y_AXIS));
		mpLoginOwner.add(mpLogin, BorderLayout.NORTH);

		// * Login Message label
		labelLoginMessage = new JLabel(getUIText("Login_Message_Default"));
		labelLoginMessage.setAlignmentX(0f);
		mpLogin.add(labelLoginMessage);

		// * Server panel
		JPanel spServer = new JPanel(new BorderLayout());
		spServer.setAlignmentX(0f);
		mpLogin.add(spServer);

		// ** Server label
		JLabel lServer = new JLabel(getUIText("Login_Server"));
		spServer.add(lServer, BorderLayout.WEST);

		// ** Server textbox
		txtfldServer = new JTextField(30);
		txtfldServer.setText(propConfig.getProperty("login.server", ""));
		txtfldServer.setComponentPopupMenu(new TextComponentPopupMenu(txtfldServer));
		spServer.add(txtfldServer, BorderLayout.EAST);

		// * Username panel
		JPanel spUsername = new JPanel(new BorderLayout());
		spUsername.setAlignmentX(0f);
		mpLogin.add(spUsername);

		// ** Username label
		JLabel lUsername = new JLabel(getUIText("Login_Username"));
		spUsername.add(lUsername, BorderLayout.WEST);

		// ** Username textbox
		txtfldUsername = new JTextField(30);
		txtfldUsername.setText(propConfig.getProperty("login.username", ""));
		txtfldUsername.setComponentPopupMenu(new TextComponentPopupMenu(txtfldUsername));
		spUsername.add(txtfldUsername, BorderLayout.EAST);

		// * Password panel
		JPanel spPassword = new JPanel(new BorderLayout());
		spPassword.setAlignmentX(0f);
		mpLogin.add(spPassword);

		// ** Password label
		JLabel lPassword = new JLabel(getUIText("Login_Password"));
		spPassword.add(lPassword, BorderLayout.WEST);

		// ** Password textbox
		passfldPassword = new JPasswordField(30);
		String strPassword = propConfig.getProperty("login.password", "");
		if(strPassword.length() > 0) {
			passfldPassword.setText(NetUtil.decompressString(strPassword));
		}
		passfldPassword.setComponentPopupMenu(new TextComponentPopupMenu(passfldPassword));
		spPassword.add(passfldPassword, BorderLayout.EAST);

		// * Remember Username checkbox
		chkboxRememberUsername = new JCheckBox(getUIText("Login_RememberUsername"));
		chkboxRememberUsername.setSelected(propConfig.getProperty("login.rememberUsername", false));
		chkboxRememberUsername.setAlignmentX(0f);
		mpLogin.add(chkboxRememberUsername);

		// * Remember Password checkbox
		chkboxRememberPassword = new JCheckBox(getUIText("Login_RememberPassword"));
		chkboxRememberPassword.setSelected(propConfig.getProperty("login.rememberPassword", false));
		chkboxRememberPassword.setAlignmentX(0f);
		mpLogin.add(chkboxRememberPassword);

		// * Buttons panel
		JPanel spButtons = new JPanel();
		spButtons.setLayout(new BoxLayout(spButtons, BoxLayout.X_AXIS));
		spButtons.setAlignmentX(0f);
		mpLogin.add(spButtons);

		// ** Login button
		btnLogin = new JButton(getUIText("Login_Login"));
		btnLogin.setMnemonic('L');
		btnLogin.setMaximumSize(new Dimension(Short.MAX_VALUE, btnLogin.getMaximumSize().height));
		btnLogin.setActionCommand("Login_Login");
		btnLogin.addActionListener(this);
		spButtons.add(btnLogin);

		// ** Quit button
		JButton btnQuit = new JButton(getUIText("Login_Quit"));
		btnQuit.setMnemonic('Q');
		btnQuit.setMaximumSize(new Dimension(Short.MAX_VALUE, btnQuit.getMaximumSize().height));
		btnQuit.setActionCommand("Login_Quit");
		btnQuit.addActionListener(this);
		spButtons.add(btnQuit);
	}

	/**
	 * Init lobby screen
	 */
	private void initLobbyUI() {
		// Main panel
		JPanel mpLobby = new JPanel(new BorderLayout());
		this.getContentPane().add(mpLobby, SCREENCARD_NAMES[SCREENCARD_LOBBY]);

		// * Tab
		JTabbedPane tabLobby = new JTabbedPane();
		mpLobby.add(tabLobby, BorderLayout.CENTER);

		// ** Console tab
		JPanel spConsole = new JPanel(new BorderLayout());
		tabLobby.addTab(getUIText("Lobby_Tab_Console"), spConsole);

		// *** Console log textpane
		txtpaneConsoleLog = new JTextPane();
		txtpaneConsoleLog.setComponentPopupMenu(new LogPopupMenu(txtpaneConsoleLog));
		txtpaneConsoleLog.addKeyListener(new LogKeyAdapter());
		JScrollPane sConsoleLog = new JScrollPane(txtpaneConsoleLog);
		spConsole.add(sConsoleLog, BorderLayout.CENTER);

		// *** Command panel
		JPanel spConsoleCommand = new JPanel(new BorderLayout());
		spConsole.add(spConsoleCommand, BorderLayout.SOUTH);

		// *** Command textbox
		txtfldConsoleCommand = new JTextField();
		txtfldConsoleCommand.setComponentPopupMenu(new TextComponentPopupMenu(txtfldConsoleCommand));
		spConsoleCommand.add(txtfldConsoleCommand, BorderLayout.CENTER);

		// *** Command Execute button
		btnConsoleCommandExecute = new JButton(getUIText("Lobby_Console_Execute"));
		btnConsoleCommandExecute.setMnemonic('E');
		btnConsoleCommandExecute.setActionCommand("Lobby_Console_Execute");
		btnConsoleCommandExecute.addActionListener(this);
		spConsoleCommand.add(btnConsoleCommandExecute, BorderLayout.EAST);

		// ** Users tab
		JPanel spUsers = new JPanel(new BorderLayout());
		tabLobby.addTab(getUIText("Lobby_Tab_Users"), spUsers);

		// *** Users table
		String[] strUsersColumnNames = new String[USERTABLE_COLUMNNAMES.length];
		for(int i = 0; i < strUsersColumnNames.length; i++) {
			strUsersColumnNames[i] = getUIText(USERTABLE_COLUMNNAMES[i]);
		}

		tablemodelUsers = new DefaultTableModel(strUsersColumnNames, 0);
		tableUsers = new JTable(tablemodelUsers);
		tableUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableUsers.setDefaultEditor(Object.class, null);
		tableUsers.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tableUsers.getTableHeader().setReorderingAllowed(false);
		tableUsers.setComponentPopupMenu(new UserPopupMenu(tableUsers));
		tableUsers.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() >= 2) {
					int rowNumber = tableUsers.getSelectedRow();
					if(rowNumber != -1) {
						String strIP = (String)tableUsers.getValueAt(rowNumber, 0);
						openBanDialog(strIP);
					}
				}
			}
		});

		TableColumnModel tmUsers = tableUsers.getColumnModel();
		tmUsers.getColumn(0).setPreferredWidth(propConfig.getProperty("tableUsers.width.ip", 90));	// IP
		tmUsers.getColumn(1).setPreferredWidth(propConfig.getProperty("tableUsers.width.host", 140));	// Hostname
		tmUsers.getColumn(2).setPreferredWidth(propConfig.getProperty("tableUsers.width.type", 60));	// Type
		tmUsers.getColumn(3).setPreferredWidth(propConfig.getProperty("tableUsers.width.name", 150));	// Name

		JScrollPane sUsers = new JScrollPane(tableUsers);
		spUsers.add(sUsers, BorderLayout.CENTER);

		// ** Multiplayer Leaderboard tab
		JPanel spMPRanking = new JPanel(new BorderLayout());
		tabLobby.addTab(getUIText("Lobby_Tab_MPRanking"), spMPRanking);

		// *** Game Style tab
		JTabbedPane tabMPRanking = new JTabbedPane();
		spMPRanking.add(tabMPRanking, BorderLayout.CENTER);

		// *** Leaderboard table
		String[] strMPRankingColumnNames = new String[MPRANKING_COLUMNNAMES.length];
		for(int i = 0; i < strMPRankingColumnNames.length; i++) {
			strMPRankingColumnNames[i] = getUIText(MPRANKING_COLUMNNAMES[i]);
		}

		tableMPRanking = new JTable[GameEngine.MAX_GAMESTYLE];
		tablemodelMPRanking = new DefaultTableModel[GameEngine.MAX_GAMESTYLE];

		for(int i = 0; i < GameEngine.MAX_GAMESTYLE; i++) {
			tablemodelMPRanking[i] = new DefaultTableModel(strMPRankingColumnNames, 0);

			tableMPRanking[i] = new JTable(tablemodelMPRanking[i]);
			tableMPRanking[i].setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			tableMPRanking[i].setDefaultEditor(Object.class, null);
			tableMPRanking[i].setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			tableMPRanking[i].getTableHeader().setReorderingAllowed(false);
			tableMPRanking[i].setComponentPopupMenu(new MPRankingPopupMenu(tableMPRanking[i]));

			TableColumnModel tm = tableMPRanking[i].getColumnModel();
			tm.getColumn(0).setPreferredWidth(propConfig.getProperty("tableMPRanking.width.rank", 30));	// Rank
			tm.getColumn(1).setPreferredWidth(propConfig.getProperty("tableMPRanking.width.name", 200));	// Name
			tm.getColumn(2).setPreferredWidth(propConfig.getProperty("tableMPRanking.width.rating", 60));	// Rating
			tm.getColumn(3).setPreferredWidth(propConfig.getProperty("tableMPRanking.width.play", 60));	// Play
			tm.getColumn(4).setPreferredWidth(propConfig.getProperty("tableMPRanking.width.win", 60));	// Win

			JScrollPane sMPRanking = new JScrollPane(tableMPRanking[i]);
			tabMPRanking.addTab(GameEngine.GAMESTYLE_NAMES[i], sMPRanking);
		}

		// *** Load/Refresh Ranking button
		btnRankingLoad = new JButton(getUIText("MPRanking_Button_LoadRanking"));
		btnRankingLoad.setMnemonic('L');
		btnRankingLoad.setActionCommand("MPRanking_Button_LoadRanking");
		btnRankingLoad.addActionListener(this);
		spMPRanking.add(btnRankingLoad, BorderLayout.SOUTH);
	}

	/**
	 * Save settings
	 */
	private void saveConfig() {
		propConfig.setProperty("mainwindow.width", this.getSize().width);
		propConfig.setProperty("mainwindow.height", this.getSize().height);
		propConfig.setProperty("mainwindow.x", this.getLocation().x);
		propConfig.setProperty("mainwindow.y", this.getLocation().y);

		TableColumnModel tmUsers = tableUsers.getColumnModel();
		propConfig.setProperty("tableUsers.width.ip", tmUsers.getColumn(0).getWidth());
		propConfig.setProperty("tableUsers.width.host", tmUsers.getColumn(1).getWidth());
		propConfig.setProperty("tableUsers.width.type", tmUsers.getColumn(2).getWidth());
		propConfig.setProperty("tableUsers.width.name", tmUsers.getColumn(3).getWidth());

		for(int i = 0; i < GameEngine.MAX_GAMESTYLE; i++) {
			TableColumnModel tm = tableMPRanking[i].getColumnModel();
			propConfig.setProperty("tableMPRanking.width.rank", tm.getColumn(0).getWidth());
			propConfig.setProperty("tableMPRanking.width.name", tm.getColumn(1).getWidth());
			propConfig.setProperty("tableMPRanking.width.rating", tm.getColumn(2).getWidth());
			propConfig.setProperty("tableMPRanking.width.play", tm.getColumn(3).getWidth());
			propConfig.setProperty("tableMPRanking.width.win", tm.getColumn(4).getWidth());
		}

		try {
			FileOutputStream out = new FileOutputStream("config/setting/netadmin.cfg");
			propConfig.store(out, "NullpoMino NetAdmin Config");
			out.close();
		} catch (IOException e) {
			log.warn("Failed to save netlobby config file", e);
		}
	}

	/**
	 * Change current screen card
	 * @param cardNumber Screen card ID
	 */
	private void changeCurrentScreenCard(int cardNumber) {
		contentPaneCardLayout.show(getContentPane(), SCREENCARD_NAMES[cardNumber]);
		currentScreenCardNumber = cardNumber;

		// Set default button
		JButton defaultButton = null;
		switch(cardNumber) {
		case SCREENCARD_LOGIN:
			defaultButton = btnLogin;
			break;
		case SCREENCARD_LOBBY:
			defaultButton = btnConsoleCommandExecute;
			break;
		}

		if(defaultButton != null) {
			this.getRootPane().setDefaultButton(defaultButton);
		}
	}

	/**
	 * Enable/Disable Login screen UI elements
	 * @param b true to enable, false to disable
	 */
	private void setLoginUIEnabled(boolean b) {
		txtfldServer.setEnabled(b);
		txtfldUsername.setEnabled(b);
		passfldPassword.setEnabled(b);
		chkboxRememberUsername.setEnabled(b);
		chkboxRememberPassword.setEnabled(b);
		btnLogin.setEnabled(b);
	}

	/**
	 * Disconnect from the server
	 */
	private void logout() {
		if(client != null) {
			if(client.isConnected()) {
				client.send("disconnect\n");
			}
			client.removeListener(this);
			client.threadRunning = false;
			client.interrupt();
			client = null;
		}

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				setLoginUIEnabled(true);
				changeCurrentScreenCard(SCREENCARD_LOGIN);
			}
		});
	}

	/**
	 * Shutdown this program
	 */
	public void shutdown() {
		logout();
		saveConfig();
		this.dispose();
	}

	/**
	 * Send admin command
	 * @param msg Command to send
	 * @return true if successful
	 */
	private boolean sendCommand(String msg) {
		if((client == null) || !client.isConnected()) return false;
		String strCommand = NetUtil.compressString(msg);
		return client.send("admin\t" + strCommand + "\n");
	}

	/**
	 * Add message to console
	 * @param str Message
	 */
	private void addConsoleLog(String str) {
		addConsoleLog(str, null);
	}

	/**
	 * Add message to console
	 * @param str Message
	 * @param fgcolor Text color (can be null)
	 */
	private void addConsoleLog(String str, Color fgcolor) {
		SimpleAttributeSet sas = null;
		if(fgcolor != null) {
			sas = new SimpleAttributeSet();
			StyleConstants.setForeground(sas, fgcolor);
		}
		try {
			Document doc = txtpaneConsoleLog.getDocument();
			doc.insertString(doc.getLength(), str + "\n", sas);
			txtpaneConsoleLog.setCaretPosition(doc.getLength());
		} catch (Exception e) {}
	}

	/**
	 * Execute a console command
	 * @param commands Command line (split by every single space)
	 * @param fullCommandLine Command line (raw String)
	 */
	private void executeConsoleCommand(String[] commands, String fullCommandLine) {
		if(commands.length == 0 || fullCommandLine.length() == 0) return;

		addConsoleLog(">" + fullCommandLine, Color.blue);

		// help/h/?
		if(commands[0].equalsIgnoreCase("help")||commands[0].equalsIgnoreCase("h")||commands[0].equalsIgnoreCase("?")) {
			try {
				FileReader filereaderHelp = null;
				try {
					filereaderHelp = new FileReader("config/lang/netadmin_help_" + Locale.getDefault().getCountry() + ".txt");
				} catch (IOException e2) {
					filereaderHelp = new FileReader("config/lang/netadmin_help_default.txt");
				}

				BufferedReader txtHelp = new BufferedReader(filereaderHelp);

				String str;
				while((str = txtHelp.readLine()) != null) {
					addConsoleLog(str);
				}

				filereaderHelp.close();
			} catch (IOException e) {
				log.error("Failed to load help file", e);
				addConsoleLog(String.format(getUIText("Console_Help_Error"), e.toString()), Color.red);
			}
		}
		// echo
		else if(commands[0].equalsIgnoreCase("echo")) {
			String strTemp = "";
			for(int i = 0; i < commands.length - 1; i++) {
				if(i >= 1) strTemp += " " + commands[i + 1];
				else strTemp += commands[i + 1];
			}
			addConsoleLog(strTemp);
		}
		// cls
		else if(commands[0].equalsIgnoreCase("cls")) {
			txtpaneConsoleLog.setText(null);
		}
		// logout/logoff/disconnect
		else if(commands[0].equalsIgnoreCase("logout")||commands[0].equalsIgnoreCase("logoff")||commands[0].equalsIgnoreCase("disconnect")) {
			addConsoleLog(getUIText("Console_Logout"));
			labelLoginMessage.setForeground(Color.black);
			labelLoginMessage.setText(getUIText("Login_Message_LoggingOut"));
			logout();
		}
		// quit/exit/shutdown
		else if(commands[0].equalsIgnoreCase("quit")||commands[0].equalsIgnoreCase("exit")) {
			shutdown();
		}
		// shutdown
		else if(commands[0].equalsIgnoreCase("shutdown")) {
			addConsoleLog(getUIText("Console_Shutdown"));
			isWantedDisconnect = true;
			isShutdownRequested = true;
			sendCommand("shutdown");
		}
		// announce
		else if(commands[0].equalsIgnoreCase("announce")) {
			String strTemp = "";
			for(int i = 0; i < commands.length - 1; i++) {
				if(i >= 1) strTemp += " " + commands[i + 1];
				else strTemp += commands[i + 1];
			}
			if(strTemp.length() > 0) {
				sendCommand("announce\t" + NetUtil.urlEncode(strTemp));
				addConsoleLog(getUIText("Console_Announce") + strTemp);
			}
		}
		// myip
		else if(commands[0].equalsIgnoreCase("myip")) {
			addConsoleLog(strMyIP);
		}
		// myhost
		else if(commands[0].equalsIgnoreCase("myhost")) {
			addConsoleLog(strMyHostname);
		}
		// serverhost
		else if(commands[0].equalsIgnoreCase("serverhost")) {
			addConsoleLog(strServerHost);
		}
		// serverport
		else if(commands[0].equalsIgnoreCase("serverport")) {
			addConsoleLog(Integer.toString(serverPort));
		}
		// bangui
		else if(commands[0].equalsIgnoreCase("bangui")) {
			if(commands.length > 1) {
				openBanDialog(commands[1]);
			} else {
				openBanDialog("");
			}
		}
		// ban
		else if(commands[0].equalsIgnoreCase("ban")) {
			if(commands.length > 2) {
				int banLength = -1;
				try {
					banLength = Integer.parseInt(commands[2]);
				} catch (NumberFormatException e) {
					addConsoleLog(String.format(getUIText("Console_Ban_InvalidLength"), commands[2]));
					return;
				}
				if((banLength < -1) || (banLength > 6)) {
					addConsoleLog(String.format(getUIText("Console_Ban_InvalidLength"), commands[2]));
					return;
				}
				requestBanFromGUI(commands[1], banLength, false);
			}
			else if(commands.length > 1) {
				requestBanFromGUI(commands[1], -1, false);
			}
			else {
				addConsoleLog(getUIText("Console_Ban_NoParams"));
			}
		}
		// banlist
		else if(commands[0].equalsIgnoreCase("banlist")) {
			sendCommand("banlist");
		}
		// unban
		else if(commands[0].equalsIgnoreCase("unban")) {
			if(commands.length > 1) {
				sendCommand("unban\t" + commands[1]);
			} else {
				addConsoleLog(getUIText("Console_UnBan_NoParams"));
			}
		}
		// playerdelete
		else if(commands[0].equalsIgnoreCase("playerdelete")||commands[0].equalsIgnoreCase("pdel")) {
			if(commands.length > 1) {
				sendCommand("playerdelete\t" + commands[1]);
			} else {
				addConsoleLog(getUIText("Console_PlayerDelete_NoParams"));
			}
		}
		// Invalid
		else {
			addConsoleLog(String.format(getUIText("Console_UnknownCommand"), commands[0]));
		}
	}

	/**
	 * Get translated GUI text
	 * @param str String
	 * @return Translated GUI text
	 */
	private static String getUIText(String str) {
		String result = propLang.getProperty(str);
		if(result == null) {
			result = propLangDefault.getProperty(str, str);
		}
		return result;
	}

	/**
	 * Program entry point
	 * @param args Command line options
	 */
	public static void main(String[] args) {
		PropertyConfigurator.configure("config/etc/log.cfg");
		new NetAdmin();
	}

	/**
	 * Sets a ban.
	 * @param strIP IP
	 * @param banLength Length of ban (-1:Kick only)
	 * @param showMessage true if display a confirm dialog
	 */
	private void requestBanFromGUI(String strIP, int banLength, boolean showMessage) {
		if((strIP == null) || (strIP.length() == 0)) return;

		if(banLength == -1) {
			int answer = JOptionPane.YES_OPTION;

			if(showMessage) {
				answer = JOptionPane.showConfirmDialog(
						this,
						getUIText("Message_ConfirmKick") + "\n" + strIP,
						getUIText("Title_ConfirmKick"),
						JOptionPane.YES_NO_OPTION);
			}

			if(answer == JOptionPane.YES_OPTION) {
				sendCommand("ban\t" + strIP);
			}
		} else {
			int answer = JOptionPane.YES_OPTION;

			if(showMessage) {
				answer = JOptionPane.showConfirmDialog(
						this,
						String.format(getUIText("Message_ConfirmBan"), getUIText("BanType" + banLength)) + "\n" + strIP,
						getUIText("Title_ConfirmBan"),
						JOptionPane.YES_NO_OPTION);
			}

			if(answer == JOptionPane.YES_OPTION) {
				sendCommand("ban\t" + strIP + "\t" + banLength);
			}
		}
	}

	/**
	 * Open ban dialog
	 * @param strIP Default IP
	 */
	private void openBanDialog(String strIP) {
		// Dialog box
		final JDialog dialogBan = new JDialog();
		dialogBan.setTitle(getUIText("Title_BanDialog"));
		dialogBan.getContentPane().setLayout(new BoxLayout(dialogBan.getContentPane(), BoxLayout.Y_AXIS));

		// IP options
		final JPanel pBanIP = new JPanel();
		dialogBan.getContentPane().add(pBanIP);

		final JLabel lBanIP = new JLabel(getUIText("Ban_IP"));
		pBanIP.add(lBanIP);

		final JTextField txtfldBanIP = new JTextField(16);
		if(strIP != null) txtfldBanIP.setText(strIP);
		txtfldBanIP.setComponentPopupMenu(new TextComponentPopupMenu(txtfldBanIP));
		pBanIP.add(txtfldBanIP);

		// Ban length Options
		final JPanel pBanLength = new JPanel();
		dialogBan.getContentPane().add(pBanLength);

		// Ban length
		final JLabel lBanLength = new JLabel(getUIText("Ban_Length"));
		pBanLength.add(lBanLength);

		final JComboBox comboboxBanLength = new JComboBox();
		comboboxBanLength.setToolTipText(getUIText("Ban_Length_Tip"));
		for (int i = -1; i < NetServerBan.BANLENGTH_TOTAL; i++) {
			comboboxBanLength.addItem(getUIText("BanType"+i));
		}
		pBanLength.add(comboboxBanLength);

		// Buttons
		final JPanel pButtons = new JPanel();
		dialogBan.getContentPane().add(pButtons);

		final JButton btnConfirm = new JButton(getUIText("Ban_Confirm"));
		btnConfirm.setMnemonic('O');
		btnConfirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				requestBanFromGUI(txtfldBanIP.getText(),comboboxBanLength.getSelectedIndex() - 1, false);
				dialogBan.dispose();
			}
		});
		pButtons.add(btnConfirm);

		final JButton btnCancel = new JButton(getUIText("Ban_Cancel"));
		btnCancel.setMnemonic('C');
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialogBan.dispose();
			}
		});
		pButtons.add(btnCancel);

		// Set frame vitals
		dialogBan.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialogBan.setLocationRelativeTo(null);
		dialogBan.setModal(true);
		dialogBan.setResizable(false);
		dialogBan.getRootPane().setDefaultButton(btnConfirm);
		dialogBan.pack();
		dialogBan.setVisible(true);
	}

	/*
	 * Button clicked
	 */
	public void actionPerformed(ActionEvent e) {
		// Login
		if(e.getActionCommand() == "Login_Login") {
			if((txtfldUsername.getText().length() > 0) && (passfldPassword.getPassword().length > 0)) {
				setLoginUIEnabled(false);
				labelLoginMessage.setForeground(Color.black);
				labelLoginMessage.setText(getUIText("Login_Message_Connecting"));

				// Get hostname and port number
				String strHost = txtfldServer.getText();
				if(strHost.length() == 0) strHost = "127.0.0.1";
				int portSpliter = strHost.indexOf(':');
				if(portSpliter == -1) portSpliter = strHost.length();

				strServerHost = strHost.substring(0, portSpliter);

				serverPort = NetBaseClient.DEFAULT_PORT;
				try {
					String strPort = strHost.substring(portSpliter + 1, strHost.length());
					serverPort = Integer.parseInt(strPort);
				} catch (Exception e2) {}

				// Begin connect
				isWantedDisconnect = false;
				isShutdownRequested = false;
				client = new NetBaseClient(strServerHost, serverPort);
				client.setDaemon(true);
				client.addListener(this);
				client.start();
			}
		}
		// Quit
		if(e.getActionCommand() == "Login_Quit") {
			shutdown();
		}
		// Execute console command
		if(e.getActionCommand() == "Lobby_Console_Execute") {
			String commandline = txtfldConsoleCommand.getText();
			String[] commands = commandline.split(" ");
			executeConsoleCommand(commands, commandline);
			txtfldConsoleCommand.setText("");
		}
		// Load/Refresh Ranking
		if(e.getActionCommand() == "MPRanking_Button_LoadRanking") {
			btnRankingLoad.setEnabled(false);
			client.send("mpranking\t0\n");
		}
	}

	/*
	 * Received a message
	 */
	public void netOnMessage(NetBaseClient client, String[] message) throws IOException {
		//if(message.length > 0) log.debug(message[0]);

		// Welcome
		if(message[0].equals("welcome")) {
			labelLoginMessage.setForeground(Color.black);
			labelLoginMessage.setText(getUIText("Login_Message_LoggingIn"));

			// Version check
			float clientVer = GameManager.getVersionMajor();
			float serverVer = Float.parseFloat(message[1]);

			if(clientVer != serverVer) {
				labelLoginMessage.setForeground(Color.red);
				labelLoginMessage.setText(String.format(getUIText("Login_Message_VersionError"), clientVer, serverVer));
				isWantedDisconnect = true;
				logout();
				return;
			}

			// Send login message
			String strUsername = txtfldUsername.getText();

			RC4 rc4 = new RC4(passfldPassword.getPassword());
			byte[] ePassword = rc4.rc4(NetUtil.stringToBytes(strUsername));
			char[] b64Password = Base64Coder.encode(ePassword);

			String strLogin = "adminlogin\t" + clientVer + "\t" + strUsername + "\t" + new String(b64Password) + "\n";
			log.debug("Send login message:" + strLogin);
			client.send(strLogin);
		}
		// Login failed
		if(message[0].equals("adminloginfail")) {
			labelLoginMessage.setForeground(Color.red);
			if((message.length > 1) && (message[1].equals("DISABLE"))) {
				labelLoginMessage.setText(getUIText("Login_Message_DisabledError"));
			} else {
				labelLoginMessage.setText(getUIText("Login_Message_LoginError"));
			}
			isWantedDisconnect = true;
			logout();
			return;
		}
		// Login successful
		if(message[0].equals("adminloginsuccess")) {
			strMyIP = message[1];
			strMyHostname = message[2];

			propConfig.setProperty("login.rememberUsername", chkboxRememberUsername.isSelected());
			propConfig.setProperty("login.rememberPassword", chkboxRememberPassword.isSelected());

			propConfig.setProperty("login.server", txtfldServer.getText());
			if(chkboxRememberUsername.isSelected()) {
				propConfig.setProperty("login.username", txtfldUsername.getText());
			} else {
				propConfig.setProperty("login.username", "");
			}
			if(chkboxRememberPassword.isSelected()) {
				propConfig.setProperty("login.password", NetUtil.compressString(new String(passfldPassword.getPassword())));
			} else {
				propConfig.setProperty("login.password", "");
			}

			addConsoleLog(String.format(getUIText("Console_LoginOK"), strServerHost, serverPort));

			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					changeCurrentScreenCard(SCREENCARD_LOBBY);
				}
			});
		}
		// Multiplayer Leaderboard
		if(message[0].equals("mpranking")) {
			btnRankingLoad.setEnabled(true);

			int style = Integer.parseInt(message[1]);

			tablemodelMPRanking[style].setRowCount(0);

			String strPData = NetUtil.decompressString(message[3]);
			String[] strPDataA = strPData.split("\t");

			for(int i = 0; i < strPDataA.length; i++) {
				String[] strRankData = strPDataA[i].split(";");

				if(strRankData.length >= MPRANKING_COLUMNNAMES.length) {
					String[] strRowData = new String[MPRANKING_COLUMNNAMES.length];
					int rank = Integer.parseInt(strRankData[0]);
					if(rank == -1) {
						strRowData[0] = "N/A";
					} else {
						strRowData[0] = Integer.toString(rank + 1);
					}
					strRowData[1] = NetUtil.urlDecode(strRankData[1]);
					strRowData[2] = strRankData[2];
					strRowData[3] = strRankData[3];
					strRowData[4] = strRankData[4];
					tablemodelMPRanking[style].addRow(strRowData);
				}
			}
		}
		// Admin command result
		if(message[0].equals("adminresult")) {
			if(message.length > 1) {
				String strAdminResultTemp = NetUtil.decompressString(message[1]);
				String[] strAdminResultArray = strAdminResultTemp.split("\t");
				onAdminResultMessage(client, strAdminResultArray);
			}
		}
	}

	/**
	 * When received an admin command result
	 * @param client NetBaseClient
	 * @param message Message
	 * @throws IOException When something bad happens
	 */
	private void onAdminResultMessage(NetBaseClient client, String[] message) throws IOException {
		// Client list
		if(message[0].equals("clientlist")) {
			// Get current selected IP and Type
			String strSelectedIP = null;
			String strSelectedType = null;
			if(tableUsers.getSelectedRow() != -1) {
				strSelectedIP   = (String)tablemodelUsers.getValueAt(tableUsers.getSelectedRow(), 0);
				strSelectedType = (String)tablemodelUsers.getValueAt(tableUsers.getSelectedRow(), 2);
			}
			tableUsers.getSelectionModel().clearSelection();

			// Set number of rows
			if(tablemodelUsers.getRowCount() > message.length - 1) {
				tablemodelUsers.setRowCount(message.length - 1);
			}

			for(int i = 1; i < message.length; i++) {
				String[] strClientData = message[i].split("\\|");

				String strIP = strClientData[0];		// IP
				String strHost = strClientData[1];		// Hostname

				// Type of the client
				int type = Integer.parseInt(strClientData[2]);
				String strType = getUIText(USERTABLE_USERTYPES[type]);
				if(strIP.equals(strMyIP) && strHost.equals(strMyHostname) && (type == 3))
					strType = "*" + getUIText(USERTABLE_USERTYPES[type]);

				// Player info
				NetPlayerInfo pInfo = null;
				if((type == 1) && (strClientData.length > 3)) {
					String strPlayerInfoTemp = strClientData[3];
					pInfo = new NetPlayerInfo(strPlayerInfoTemp);
				}

				// Create a row data
				String[] strTableData = new String[tablemodelUsers.getColumnCount()];
				strTableData[0] = strIP;
				strTableData[1] = strHost;
				strTableData[2] = strType;
				strTableData[3] = (pInfo != null) ? pInfo.strName : "";

				// Add the row data
				int rowNumber = i - 1;
				int maxRow = tablemodelUsers.getRowCount();
				if(rowNumber < maxRow) {
					// Modify an existing row
					for(int j = 0; j < strTableData.length; j++) {
						tablemodelUsers.setValueAt(strTableData[j], rowNumber, j);
					}

					// Set selected row
					if((strSelectedIP != null) && (strSelectedType != null) &&
						strSelectedIP.equals(strIP) && strSelectedType.equals(strType))
					{
						tableUsers.getSelectionModel().setSelectionInterval(rowNumber, rowNumber);
						strSelectedIP = null;
						strSelectedType = null;
					}
				} else {
					// Add an new row
					tablemodelUsers.addRow(strTableData);

					// Set selected row
					if((strSelectedIP != null) && (strSelectedType != null) &&
						strSelectedIP.equals(strIP) && strSelectedType.equals(strType))
					{
						tableUsers.getSelectionModel().setSelectionInterval(maxRow, maxRow);
						strSelectedIP = null;
						strSelectedType = null;
					}
				}
			}
		}
		// Ban
		if(message[0].equals("ban")) {
			if(message.length > 3) {
				String strBanLength = getUIText("BanType" + message[2]);
				addConsoleLog(String.format(getUIText("Console_Ban_Result"),message[1],strBanLength,message[3]), new Color(0, 64, 64));
			}
		}
		// Ban List
		if(message[0].equals("banlist")) {
			if(message.length < 2) {
				addConsoleLog(getUIText("Console_BanList_Result_None"), new Color(0, 64, 64));
			} else {
				for(int i = 0; i < message.length - 1; i++) {
					NetServerBan ban = new NetServerBan();
					ban.importString(message[i + 1]);

					String strBanLength = getUIText("BanType" + ban.banLength);
					String strDate = "";
					if(ban.startDate != null) {
						Calendar d = ban.startDate;
						strDate = String.format("%04d-%02d-%02d %02d:%02d:%02d",
								d.get(Calendar.YEAR), d.get(Calendar.MONTH) + 1, d.get(Calendar.DATE),
								d.get(Calendar.HOUR_OF_DAY), d.get(Calendar.MINUTE), d.get(Calendar.SECOND));
					}

					addConsoleLog(String.format(getUIText("Console_BanList_Result"), ban.addr, strBanLength, strDate), new Color(0, 64, 64));
				}
			}
		}
		// Un-Ban
		if(message[0].equals("unban")) {
			if(message.length > 2) {
				addConsoleLog(String.format(getUIText("Console_UnBan_Result"), message[1], message[2]), new Color(0, 64, 64));
			}
		}
		// Player Delete
		if(message[0].equals("playerdelete")) {
			if(message.length > 1) {
				addConsoleLog(String.format(getUIText("Console_PlayerDelete_Result"), message[1]), new Color(0, 64, 64));
			}
		}
	}

	/*
	 * Disconnected
	 */
	public void netOnDisconnect(NetBaseClient client, Throwable ex) {
		if(isShutdownRequested) {
			log.info("Server shutdown completed");
			labelLoginMessage.setForeground(Color.black);
			labelLoginMessage.setText(getUIText("Login_Message_Shutdown"));
		} else if(isWantedDisconnect) {
			log.info("Disconnected from the server");
		} else {
			labelLoginMessage.setForeground(Color.red);
			if(ex == null) {
				log.warn("ERROR Disconnected! (null)");
				labelLoginMessage.setText(String.format(getUIText("Login_Message_UnwantedDisconnect"), "(null)"));
			} else {
				log.error("ERROR Disconnected!", ex);
				labelLoginMessage.setText(String.format(getUIText("Login_Message_UnwantedDisconnect"), ex.toString()));
			}
		}
		logout();
	}

	/**
	 * Popup menu for text components
	 */
	private class TextComponentPopupMenu extends JPopupMenu {
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
	 * Popup menu for console log
	 */
	private class LogPopupMenu extends JPopupMenu {
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
	 * Popup menu for users table
	 */
	private class UserPopupMenu extends JPopupMenu {
		private static final long serialVersionUID = 1L;

		private Action copyAction;
		private Action kickAction;
		private Action banAction;

		public UserPopupMenu(final JTable table) {
			super();

			add(copyAction = new AbstractAction(getUIText("Popup_Copy")) {
				private static final long serialVersionUID = 1L;
				public void actionPerformed(ActionEvent e) {
					int row = table.getSelectedRow();

					if(row != -1) {
						String strCopy = "";

						for(int column = 0; column < table.getColumnCount(); column++) {
							Object selectedObject = table.getValueAt(row, column);
							if(selectedObject instanceof String) {
								if(column == 0) {
									strCopy += (String)selectedObject;
								} else {
									strCopy += "," + (String)selectedObject;
								}
							}
						}

						StringSelection ss = new StringSelection(strCopy);
						Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
						clipboard.setContents(ss, ss);
					}
				}
			});
			add(kickAction = new AbstractAction(getUIText("Popup_Kick")) {
				private static final long serialVersionUID = 1L;
				public void actionPerformed(ActionEvent evt) {
					int rowNumber = table.getSelectedRow();
					String strIP = (String)table.getValueAt(rowNumber, 0);
					requestBanFromGUI(strIP, -1, true);
				}
			});
			add(banAction = new AbstractAction(getUIText("Popup_Ban")) {
				private static final long serialVersionUID = 1L;
				public void actionPerformed(ActionEvent evt) {
					int rowNumber = table.getSelectedRow();
					final String strIP = (String)table.getValueAt(rowNumber, 0);
					openBanDialog(strIP);
				}
			});
		}

		@Override
		public void show(Component c, int x, int y) {
			JTable table = (JTable) c;
			boolean flg = table.getSelectedRow() != -1;
			copyAction.setEnabled(flg);
			kickAction.setEnabled(flg);
			banAction.setEnabled(flg);
			super.show(c, x, y);
		}
	}

	/**
	 * Popup menu for leaderboard table
	 */
	private class MPRankingPopupMenu extends JPopupMenu {
		private static final long serialVersionUID = 1L;

		private Action copyAction;
		private Action deleteAction;

		public MPRankingPopupMenu(final JTable table) {
			super();

			add(copyAction = new AbstractAction(getUIText("Popup_Copy")) {
				private static final long serialVersionUID = 1L;
				public void actionPerformed(ActionEvent e) {
					int row = table.getSelectedRow();

					if(row != -1) {
						String strCopy = "";

						for(int column = 0; column < table.getColumnCount(); column++) {
							Object selectedObject = table.getValueAt(row, column);
							if(selectedObject instanceof String) {
								if(column == 0) {
									strCopy += (String)selectedObject;
								} else {
									strCopy += "," + (String)selectedObject;
								}
							}
						}

						StringSelection ss = new StringSelection(strCopy);
						Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
						clipboard.setContents(ss, ss);
					}
				}
			});
			add(deleteAction = new AbstractAction(getUIText("Popup_Delete")) {
				private static final long serialVersionUID = 1L;
				public void actionPerformed(ActionEvent evt) {
					int rowNumber = table.getSelectedRow();
					String strName = (String)table.getValueAt(rowNumber, 1);
					sendCommand("playerdelete\t" + strName);
					client.send("mpranking\t0\n");
				}
			});
		}

		@Override
		public void show(Component c, int x, int y) {
			JTable table = (JTable) c;
			boolean flg = table.getSelectedRow() != -1;
			copyAction.setEnabled(flg);
			deleteAction.setEnabled(flg);
			super.show(c, x, y);
		}
	}

	/**
	 * KeyAdapter for console log
	 */
	private class LogKeyAdapter extends KeyAdapter {
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
