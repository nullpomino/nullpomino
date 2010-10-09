package mu.nu.nullpo.tool.netadmin;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
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

	/** Encryption algorithm */
	private static RC4 rc4;

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
	private DefaultTableModel[] tablemodelMPRankings;

	/** MPRanking table component */
	private JTable[] tableMPRankings;

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
		passfldPassword.setComponentPopupMenu(new TextComponentPopupMenu(passfldPassword));
		spPassword.add(passfldPassword, BorderLayout.EAST);

		// * Remember Username checkbox
		chkboxRememberUsername = new JCheckBox(getUIText("Login_RememberUsername"));
		chkboxRememberUsername.setAlignmentX(0f);
		mpLogin.add(chkboxRememberUsername);

		// * Remember Password checkbox
		chkboxRememberPassword = new JCheckBox(getUIText("Login_RememberPassword"));
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
		/*
		tableUsers.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() >= 2) {
					int rowNumber = tableUsers.getSelectedRow();
					if(rowNumber != -1) {
						String strIP = (String)tableUsers.getValueAt(rowNumber, 0);
						requestBanFromGUI(strIP, -1);
					}
				}
			}
		});
		 */
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

		tableMPRankings = new JTable[GameEngine.MAX_GAMESTYLE];
		tablemodelMPRankings = new DefaultTableModel[GameEngine.MAX_GAMESTYLE];

		for(int i = 0; i < GameEngine.MAX_GAMESTYLE; i++) {
			tablemodelMPRankings[i] = new DefaultTableModel(strMPRankingColumnNames, 0);

			tableMPRankings[i] = new JTable(tablemodelMPRankings[i]);
			tableMPRankings[i].setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			tableMPRankings[i].setDefaultEditor(Object.class, null);
			tableMPRankings[i].setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			tableMPRankings[i].getTableHeader().setReorderingAllowed(false);

			TableColumnModel tm = tableMPRankings[i].getColumnModel();
			tm.getColumn(0).setPreferredWidth(propConfig.getProperty("tableMPRanking.width.rank", 30));	// Rank
			tm.getColumn(1).setPreferredWidth(propConfig.getProperty("tableMPRanking.width.name", 200));	// Name
			tm.getColumn(2).setPreferredWidth(propConfig.getProperty("tableMPRanking.width.rating", 60));	// Rating
			tm.getColumn(3).setPreferredWidth(propConfig.getProperty("tableMPRanking.width.play", 60));	// Play
			tm.getColumn(4).setPreferredWidth(propConfig.getProperty("tableMPRanking.width.win", 60));	// Win

			JScrollPane sMPRanking = new JScrollPane(tableMPRankings[i]);
			tabMPRanking.addTab(GameEngine.GAMESTYLE_NAMES[i], sMPRanking);
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

		setLoginUIEnabled(true);
		if(currentScreenCardNumber != SCREENCARD_LOGIN) {
			changeCurrentScreenCard(SCREENCARD_LOGIN);
		}
	}

	/**
	 * Shutdown this program
	 */
	public void shutdown() {
		logout();
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

	private void requestBanFromGUI(String strIP, int banLength) {
		if((strIP == null) || (strIP.length() == 0)) return;

		if(banLength == -1) {
			int answer = JOptionPane.showConfirmDialog(
						this,
						getUIText("Message_ConfirmKick") + "\n" + strIP,
						getUIText("Title_ConfirmKick"),
						JOptionPane.YES_NO_OPTION);

			if(answer == JOptionPane.YES_OPTION) {
				sendCommand("ban\t" + strIP);
			}
		} else {
			int answer = JOptionPane.showConfirmDialog(
						this,
						String.format(getUIText("Message_ConfirmBan"), getUIText("BanType" + banLength)) + "\n" + strIP,
						getUIText("Title_ConfirmBan"),
						JOptionPane.YES_NO_OPTION);

			if(answer == JOptionPane.YES_OPTION) {
				sendCommand("ban\t" + strIP + "\t" + banLength);
			}
		}
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

			rc4 = new RC4(passfldPassword.getPassword());
			byte[] ePassword = rc4.rc4(NetUtil.stringToBytes(strUsername));
			char[] b64Password = Base64Coder.encode(ePassword);

			String strLogin = "adminlogin\t" + clientVer + "\t" + strUsername + "\t" + new String(b64Password) + "\n";
			log.info("Send login message:" + strLogin);
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
			changeCurrentScreenCard(SCREENCARD_LOBBY);
			strMyIP = message[1];
			strMyHostname = message[2];
			addConsoleLog("Your IP:" + strMyIP + ", Your Hostname:" + strMyHostname);
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
			if(tablemodelUsers.getRowCount() > message.length - 1) {
				tablemodelUsers.setRowCount(message.length - 1);
			}

			for(int i = 1; i < message.length; i++) {
				String[] strClientData = message[i].split("\\|");

				String strIP = strClientData[0];
				String strHost = strClientData[1];

				int type = Integer.parseInt(strClientData[2]);
				String strType = getUIText(USERTABLE_USERTYPES[type]);
				if(strIP.equals(strMyIP) && strHost.equals(strMyHostname) && (type == 3))
					strType = "*" + getUIText(USERTABLE_USERTYPES[type]);

				NetPlayerInfo pInfo = null;
				if((type == 1) && (strClientData.length > 3)) {
					String strPlayerInfoTemp = strClientData[3];
					pInfo = new NetPlayerInfo(strPlayerInfoTemp);
				}

				String[] strTableData = new String[tablemodelUsers.getColumnCount()];
				strTableData[0] = strIP;
				strTableData[1] = strHost;
				strTableData[2] = strType;
				strTableData[3] = (pInfo != null) ? pInfo.strName : "";

				int rowNumber = i - 1;
				if(rowNumber < tablemodelUsers.getRowCount()) {
					for(int j = 0; j < strTableData.length; j++) {
						tablemodelUsers.setValueAt(strTableData[j], rowNumber, j);
					}
				} else {
					tablemodelUsers.addRow(strTableData);
				}
			}
		}
	}

	/*
	 * Disconnected
	 */
	public void netOnDisconnect(NetBaseClient client, Throwable ex) {
		if(isWantedDisconnect) {
			log.info("Disconnected");
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
	
	private class UserPopupMenu extends JPopupMenu {
		
		private Action kickAction;
		
		public UserPopupMenu(final JTable table) {
			super();

			add(kickAction = new AbstractAction(getUIText("Popup_Kick")) {
				private static final long serialVersionUID = 1L;
				public void actionPerformed(ActionEvent evt) {
					int rowNumber = table.getSelectedRow();
					String strIP = (String)table.getValueAt(rowNumber, 0);
					requestBanFromGUI(strIP, -1);
				}
			});
		}
		
		@Override
		public void show(Component c, int x, int y) {
			JTable table = (JTable) c;
			boolean flg = table.getSelectedRow() != -1;
			kickAction.setEnabled(flg);
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
