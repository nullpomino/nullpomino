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
package mu.nu.nullpo.game.net;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.zip.Adler32;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import mu.nu.nullpo.game.component.RuleOptions;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.util.CustomProperties;
import net.clarenceho.crypto.RC4;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.cacas.java.gnu.tools.Crypt;

import biz.source_code.base64Coder.Base64Coder;

/**
 * NullpoMino NetServer
 * <a href="http://hondou.homedns.org/pukiwiki/index.php?JavaSE%20%A5%C1%A5%E3%A5%C3%A5%C8%A5%B7%A5%B9%A5%C6%A5%E0%A4%F2%BA%EE%A4%ED%A4%A6">Source</a>
 */
public class NetServer implements ActionListener {
	/** serialVersionUID */
	private static final long serialVersionUID = 1L;

	/** Log */
	static final Logger log = Logger.getLogger(NetServer.class);

	/** Default port number */
	public static final int DEFAULT_PORT = 9200;

	/** Read buffer size */
	public static final int BUF_SIZE = 2048;

	/** Rule data send buffer size */
	public static final int RULE_BUF_SIZE = 512;

	/** Default value of ratingNormalMaxDiff */
	public static final double NORMAL_MAX_DIFF = 16;

	/** Default value of ratingProvisionalGames */
	public static final int PROVISIONAL_GAMES = 50;

	/** Default value of maxMPRanking */
	public static final int DEFAULT_MAX_MPRANKING = 100;

	/** Server config file */
	private static CustomProperties propServer;

	/** Properties of player data list (mainly for rating) */
	private static CustomProperties propPlayerData;

	/** Properties of multiplayer leaderboard */
	private static CustomProperties propMPRanking;

	/** True if GUI is enabled */
	private static boolean isGUIEnabled;

	/** Default rating */
	private static int ratingDefault;

	/** The maximum possible adjustment per game. (K-value) */
	private static double ratingNormalMaxDiff;

	/** After playing this number of games, the rating logic will take account of number of games played. */
	private static int ratingProvisionalGames;

	/** Min/Max range of rating */
	private static int ratingMin, ratingMax;

	/** Allow same IP player for rating change */
	private static boolean ratingAllowSameIP;

	/** Max entry of multiplayer leaderboard */
	private static int maxMPRanking;

	/** Rule list for rated game. It contains RuleOptions. */
	@SuppressWarnings("rawtypes")
	private static LinkedList[] ruleList;

	/** Setting ID list for rated game. It contains Integer. */
	@SuppressWarnings("rawtypes")
	private static LinkedList[] ruleSettingIDList;

	/** Multiplayer leaderboard list. It contains NetPlayerInfo. */
	@SuppressWarnings("rawtypes")
	private static LinkedList[] mpRankingList;

	/** Encryption algorithm */
	private static RC4 rc4;

	/** List of SocketChannel */
	private List<SocketChannel> channelList = new LinkedList<SocketChannel>();

	/** Send buffer */
	private Map<SocketChannel, ByteArrayOutputStream> bufferMap = new HashMap<SocketChannel, ByteArrayOutputStream>();

	/** Incomplete packet buffer */
	private Map<SocketChannel, StringBuilder> notCompletePacketMap = new HashMap<SocketChannel, StringBuilder>();

	/** Player info */
	private Map<SocketChannel, NetPlayerInfo> playerInfoMap = new HashMap<SocketChannel, NetPlayerInfo>();

	/** Room info list */
	private LinkedList<NetRoomInfo> roomInfoList = new LinkedList<NetRoomInfo>();

	/** Observer list */
	private LinkedList<SocketChannel> observerList = new LinkedList<SocketChannel>();

	/** Admin list */
	private LinkedList<SocketChannel> adminList = new LinkedList<SocketChannel>();

	/** Ban list */
	private LinkedList<NetServerBan> banList = new LinkedList<NetServerBan>();

	/** Selector */
	private Selector selector;

	/** Current port number */
	private int port;

	/** Number of players connected so far (Used for assigning player ID) */
	private int playerCount = 0;

	/** Number of rooms created so far (Used for room ID) */
	private int roomCount = 0;

	/** RNG for map selection */
	private Random rand = new Random();

	/**
	 * Main (Entry point)
	 * @param args Command-line options
	 */
	public static void main(String[] args) {
		// Init log system (should be first!)
		PropertyConfigurator.configure("config/etc/log_server.cfg");

		// Load server config file
		propServer = new CustomProperties();
		try {
			FileInputStream in = new FileInputStream("config/etc/netserver.cfg");
			propServer.load(in);
			in.close();
		} catch (IOException e) {
			log.warn("Failed to load config file", e);
		}

		// Fetch port number from config file
		int port = propServer.getProperty("netserver.port", DEFAULT_PORT);

		if(args.length > 0) {
			// If command-line option is used, change port number to the new one
			try {
				port = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {}
		}

		// Run
		new NetServer(port).run();
	}

	/**
	 * Initialize (default port)
	 */
	private void init() {
		init(DEFAULT_PORT);
	}

	/**
	 * Initialize
	 */
	private void init(int port) {
		this.port = port;

		// Load player data file
		propPlayerData = new CustomProperties();
		try {
			FileInputStream in = new FileInputStream("config/setting/netserver_playerdata.cfg");
			propPlayerData.load(in);
			in.close();
		} catch (IOException e) {}

		// Load multiplayer leaderboard file
		propMPRanking = new CustomProperties();
		try {
			FileInputStream in = new FileInputStream("config/setting/netserver_mpranking.cfg");
			propMPRanking.load(in);
			in.close();
		} catch (IOException e) {}

		// Load settings
		isGUIEnabled = propServer.getProperty("netserver.isGUIEnabled", true);
		ratingDefault = propServer.getProperty("netserver.ratingDefault", NetPlayerInfo.DEFAULT_MULTIPLAYER_RATING);
		ratingNormalMaxDiff = propServer.getProperty("netserver.ratingNormalMaxDiff", NORMAL_MAX_DIFF);
		ratingProvisionalGames = propServer.getProperty("netserver.ratingProvisionalGames", PROVISIONAL_GAMES);
		ratingMin = propServer.getProperty("netserver.ratingMin", 0);
		ratingMax = propServer.getProperty("netserver.ratingMax", 99999);
		ratingAllowSameIP = propServer.getProperty("netserver.ratingAllowSameIP", true);
		maxMPRanking = propServer.getProperty("netserver.maxMPRanking", DEFAULT_MAX_MPRANKING);

		// Load rules for rated game
		loadRuleList();

		// Load multiplayer leaderboard
		loadMPRankingList();
	}

	/**
	 * Load rated-game rule list
	 */
	@SuppressWarnings("rawtypes")
	private static void loadRuleList() {
		ruleList = new LinkedList[GameEngine.MAX_GAMESTYLE];
		ruleSettingIDList = new LinkedList[GameEngine.MAX_GAMESTYLE];
		for(int i = 0; i < GameEngine.MAX_GAMESTYLE; i++) {
			ruleList[i] = new LinkedList();
			ruleSettingIDList[i] = new LinkedList();
		}

		try {
			BufferedReader txtRuleList = new BufferedReader(new FileReader("config/etc/netserver_rulelist.lst"));
			int style = 0;

			String str = null;
			while((str = txtRuleList.readLine()) != null) {
				if((str.length() < 1) || str.startsWith("#")) {
					// Empty or a comment line. Do nothing.
				} else if(str.startsWith(":")) {
					// Game style
					String strStyle = str.substring(1);

					style = -1;
					for(int i = 0; i < GameEngine.MAX_GAMESTYLE; i++) {
						if(strStyle.equalsIgnoreCase(GameEngine.GAMESTYLE_NAMES[i])) {
							style = i;
							break;
						}
					}

					if(style == -1) {
						log.warn("{StyleChange} Unknown Style:" + str);
						style = 0;
					} else {
						log.debug("{StyleChange} StyleID:" + style + " StyleName:" + str);
					}
				} else {
					// Rule file
					try {
						int settingID = 0;
						String[] strTempArray = str.split(";");
						if(strTempArray.length > 1) {
							settingID = Integer.parseInt(strTempArray[1]);
						}

						log.debug("{RuleLoad} StyleID:" + style + " RuleFile:" + strTempArray[0] + " SettingID:" + settingID);

						FileInputStream in = new FileInputStream(strTempArray[0]);
						CustomProperties prop = new CustomProperties();
						prop.load(in);
						in.close();

						RuleOptions rule = new RuleOptions();
						rule.readProperty(prop, 0);

						ruleList[style].add(rule);
						ruleSettingIDList[style].add(Integer.valueOf(settingID));
					} catch (Exception e2) {
						log.warn("Failed to load rule file", e2);
					}
				}
			}

			txtRuleList.close();
		} catch (Exception e) {
			log.warn("Failed to load rule list", e);
		}
	}

	/**
	 * Load multiplayer leaderboard
	 */
	@SuppressWarnings("rawtypes")
	private static void loadMPRankingList() {
		mpRankingList = new LinkedList[GameEngine.MAX_GAMESTYLE];
		for(int i = 0; i < GameEngine.MAX_GAMESTYLE; i++) {
			mpRankingList[i] = new LinkedList();
		}

		for(int style = 0; style < GameEngine.MAX_GAMESTYLE; style++) {
			int count = propMPRanking.getProperty(style + ".mpranking.count", 0);
			if(count > maxMPRanking) count = maxMPRanking;

			for(int i = 0; i < count; i++) {
				NetPlayerInfo p = new NetPlayerInfo();
				p.strName = propMPRanking.getProperty(style + ".mpranking.strName." + i, "");
				p.rating[style] = propMPRanking.getProperty(style + ".mpranking.rating." + i, ratingDefault);
				p.playCount[style] = propMPRanking.getProperty(style + ".mpranking.playCount." + i, 0);
				p.winCount[style] = propMPRanking.getProperty(style + ".mpranking.winCount." + i, 0);
				mpRankingList[style].add(p);
			}
		}
	}

	/**
	 * Find a player in multiplayer leaderboard.
	 * @param style Game Style
	 * @param p NetPlayerInfo (can be null, returns -1 if so)
	 * @return Index in mpRankingList[style] (-1 if not found)
	 */
	private static int mpRankingIndexOf(int style, NetPlayerInfo p) {
		if(p == null) return -1;
		for(int i = 0; i < mpRankingList[style].size(); i++) {
			NetPlayerInfo p2 = (NetPlayerInfo)mpRankingList[style].get(i);
			if(p.strName.equals(p2.strName)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Update multiplayer leaderboard.
	 * @param style Game Style
	 * @param p NetPlayerInfo
	 * @return New place (-1 if not ranked)
	 */
	private static int mpRankingUpdate(int style, NetPlayerInfo p) {
		// Remove existing record
		int prevRecord = mpRankingIndexOf(style, p);
		if(prevRecord != -1) mpRankingList[style].remove(prevRecord);

		// Insert new record
		int place = -1;
		boolean rankin = false;
		for(int i = 0; i < mpRankingList[style].size(); i++) {
			NetPlayerInfo p2 = (NetPlayerInfo)mpRankingList[style].get(i);
			if(p.rating[style] > p2.rating[style]) {
				mpRankingList[style].add(i, p);
				place = i;
				rankin = true;
				break;
			}
		}

		// Couldn't rank in? Add to last.
		if(!rankin) {
			mpRankingList[style].addLast(p);
			place = mpRankingList[style].size() - 1;
		}

		// Remove anything after maxMPRanking
		while(mpRankingList[style].size() >= maxMPRanking) mpRankingList[style].removeLast();

		// Done
		return (place >= maxMPRanking) ? -1 : place;
	}

	/**
	 * Write player data properties (propPlayerData) to a file
	 */
	private static void writeMPRankingToFile() {
		for(int style = 0; style < GameEngine.MAX_GAMESTYLE; style++) {
			int count = mpRankingList[style].size();
			if(count > maxMPRanking) count = maxMPRanking;
			propMPRanking.setProperty(style + ".mpranking.count", count);

			for(int i = 0; i < count; i++) {
				NetPlayerInfo p = (NetPlayerInfo)mpRankingList[style].get(i);
				propMPRanking.setProperty(style + ".mpranking.strName." + i, p.strName);
				propMPRanking.setProperty(style + ".mpranking.rating." + i, p.rating[style]);
				propMPRanking.setProperty(style + ".mpranking.playCount." + i, p.playCount[style]);
				propMPRanking.setProperty(style + ".mpranking.winCount." + i, p.winCount[style]);
			}
		}

		try {
			FileOutputStream out = new FileOutputStream("config/setting/netserver_mpranking.cfg");
			propMPRanking.store(out, "NullpoMino NetServer Multiplayer Leaderboard");
			out.close();
		} catch (IOException e) {
			log.error("Failed to write player data", e);
		}
	}

	/**
	 * Get player data from propPlayerData
	 * @param pInfo NetPlayerInfo
	 */
	private static void getPlayerDataFromProperty(NetPlayerInfo pInfo) {
		if(pInfo.isTripUse) {
			for(int i = 0; i < GameEngine.MAX_GAMESTYLE; i++) {
				pInfo.rating[i] = propPlayerData.getProperty("p.rating." + i + "." + pInfo.strName, ratingDefault);
				pInfo.playCount[i] = propPlayerData.getProperty("p.playCount." + i + "." + pInfo.strName, 0);
				pInfo.winCount[i] = propPlayerData.getProperty("p.winCount." + i + "." + pInfo.strName, 0);
			}
		} else {
			for(int i = 0; i < GameEngine.MAX_GAMESTYLE; i++) {
				pInfo.rating[i] = ratingDefault;
				pInfo.playCount[i] = 0;
				pInfo.winCount[i] = 0;
			}
		}
	}

	/**
	 * Set player data to propPlayerData
	 * @param pInfo NetPlayerInfo
	 */
	private static void setPlayerDataToProperty(NetPlayerInfo pInfo) {
		if(pInfo.isTripUse) {
			for(int i = 0; i < GameEngine.MAX_GAMESTYLE; i++) {
				propPlayerData.setProperty("p.rating." + i + "." + pInfo.strName, pInfo.rating[i]);
				propPlayerData.setProperty("p.playCount." + i + "." + pInfo.strName, pInfo.playCount[i]);
				propPlayerData.setProperty("p.winCount." + i + "." + pInfo.strName, pInfo.winCount[i]);
			}
		}
	}

	/**
	 * Write player data properties (propPlayerData) to a file
	 */
	private static void writePlayerDataToFile() {
		try {
			FileOutputStream out = new FileOutputStream("config/setting/netserver_playerdata.cfg");
			propPlayerData.store(out, "NullpoMino NetServer PlayerData");
			out.close();
		} catch (IOException e) {
			log.error("Failed to write player data", e);
		}
	}

	/**
	 * Default constructor
	 */
	public NetServer() {
		this(DEFAULT_PORT);
	}

	/**
	 * Constructor
	 * @param port Port number
	 */
	public NetServer(int port) {
		init(port);
	}

	/**
	 * Server mainloop
	 */
	public void run() {
		ServerSocketChannel serverChannel = null;

		try {
			log.info("Server version:" + GameManager.getVersionMajor());
			log.info("Starting server on port " + port);

			writeServerStatusFile();

			selector = Selector.open();
			serverChannel = ServerSocketChannel.open();
			serverChannel.configureBlocking(false);
			serverChannel.socket().bind(new InetSocketAddress(port));
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);

			while(selector.select() > 0) {
				Iterator<SelectionKey> keyIt = selector.selectedKeys().iterator();

				while(keyIt.hasNext()) {
					SelectionKey key = keyIt.next();
					keyIt.remove();

					if (key.isAcceptable()) {
						doAccept((ServerSocketChannel) key.channel());
					} else if (key.isReadable()) {
						doRead((SocketChannel) key.channel());
					} else if (key.isWritable()) {
						doWrite((SocketChannel) key.channel());
					}
				}

				//Thread.sleep(500);
			}
		} catch (IOException e) {
			log.fatal("IOException throwed on server mainloop", e);
		} catch (Throwable e) {
			log.fatal("Non-IOException throwed on server mainloop", e);
		} finally {
			log.warn("Server Shutdown!");
			try {
				if(serverChannel != null) serverChannel.close();
			} catch (IOException e) {
				log.debug("IOException on shutdown", e);
			}
		}
	}

	/**
	 * Accept a new client
	 * @param daemonChannel ServerSocketChannel
	 */
	private void doAccept(ServerSocketChannel daemonChannel) {
		SocketChannel channel = null;

		try {
			channel = daemonChannel.accept();
			log.info("Accept: " + channel);
			channel.configureBlocking(false);

			channel.register(selector, SelectionKey.OP_READ);

			channelList.add(channel);

			String remoteAddr = channel.socket().getRemoteSocketAddress().toString();
			log.info("Connected: " + remoteAddr);

			if (checkConnectionOnBanlist(channel)) {
				log.warn("Connection is banned: " + remoteAddr);
				logout(channel);
				return;
			}

			send(channel, "welcome\t" + GameManager.getVersionMajor() + "\t" + playerInfoMap.size() + "\t" + observerList.size() + "\n");
			adminSendClientList();
		} catch (IOException e) {
			log.info("IOException throwed on doAccept", e);
			logout(channel);
		} catch (Exception e) {
			log.warn("Non-IOException throwed on doAccept", e);
			logout(channel);
		}
	}

	/**
	 * Receive message(s) from client
	 * @param channel SocketChannel
	 */
	private void doRead(SocketChannel channel) {
		try {
			String remoteAddr = channel.socket().getRemoteSocketAddress().toString();

			ByteBuffer buf = ByteBuffer.allocate(BUF_SIZE);

			if (channel.read(buf) > 0) {
				buf.flip();

				byte[] bytes = new byte[buf.limit()];
				buf.get(bytes);

				log.debug("Message From:" + remoteAddr);

				String message = NetUtil.bytesToString(bytes);
				log.debug(message);

				// Previous incomplete packet buffer (null if none are present)
				StringBuilder notCompletePacketBuffer = notCompletePacketMap.remove(channel);

				// The new packet buffer
				StringBuilder packetBuffer = new StringBuilder();
				if(notCompletePacketBuffer != null) packetBuffer.append(notCompletePacketBuffer);
				packetBuffer.append(message);

				int index;
				while((index = packetBuffer.indexOf("\n")) != -1) {
					String msgNow = packetBuffer.substring(0, index);
					processPacket(channel, msgNow);
					packetBuffer = packetBuffer.replace(0, index+1, "");
				}

				// Place new incomplete packet buffer
				if(packetBuffer.length() > 0) {
					notCompletePacketMap.put(channel, packetBuffer);
				}
			}
		} catch (IOException e) {
			log.debug("Socket Disconnected on doRead (IOException)", e);
			logout(channel);
		} catch (Exception e) {
			log.warn("Socket Disconnected on doRead (NOT-IOException)", e);
			logout(channel);
		}
	}

	/**
	 * When it's ready to send something
	 * @param channel SocketChannel
	 */
	private void doWrite(SocketChannel channel) {
		ByteArrayOutputStream bout = bufferMap.get(channel);
		if (bout != null) {
			log.debug("Write Channel " + channel);
			try {
				ByteBuffer bbuf = ByteBuffer.wrap(bout.toByteArray());
				int size = channel.write(bbuf);

				log.debug("Send " + size + "/" + bbuf.limit());

				if (bbuf.hasRemaining()) {
					ByteArrayOutputStream rest = new ByteArrayOutputStream();
					rest.write(bbuf.array(), bbuf.position(), bbuf.remaining());
					bufferMap.put(channel, rest);
					channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
				} else {
					bufferMap.remove(channel);
					channel.register(selector, SelectionKey.OP_READ);
				}
			} catch (IOException e) {
				log.debug("IOException throwed on doWrite", e);
				logout(channel);
			} catch (Exception e) {
				log.warn("Non-IOException throwed on doWrite", e);
				logout(channel);
			}
		}
	}

	/**
	 * Logout
	 * @param channel SocketChannel
	 */
	private void logout(SocketChannel channel) {
		if(channel == null) return;

		String remoteAddr = "";
		try {
			remoteAddr = channel.socket().getRemoteSocketAddress().toString();
			log.info("Logout: " + remoteAddr);
		} catch (Exception e) {}

		try {
			channel.register(selector, 0);
		} catch (IOException e) {
			log.debug("IOException throwed on logout (channel.register)", e);
		}
		try {
			channel.finishConnect();
		} catch (IOException e) {
			log.debug("IOException throwed on logout (channel.finishConnect)", e);
		}
		try {
			channel.close();
		} catch (IOException e) {
			log.debug("IOException throwed on logout (channel.close)", e);
		}

		try {
			channelList.remove(channel);
			bufferMap.remove(channel);
			notCompletePacketMap.remove(channel);

			NetPlayerInfo pInfo = playerInfoMap.remove(channel);
			if(pInfo != null) {
				log.info(pInfo.strName + " has logged out");

				playerDead(pInfo);
				pInfo.connected = false;
				pInfo.ready = false;

				LinkedList<NetRoomInfo> deleteList = new LinkedList<NetRoomInfo>();	// Room delete check list

				for(NetRoomInfo roomInfo: roomInfoList) {
					if(roomInfo.playerList.contains(pInfo)) {
						roomInfo.playerList.remove(pInfo);
						roomInfo.playerQueue.remove(pInfo);
						roomInfo.exitSeat(pInfo);
						deleteList.add(roomInfo);
					}
				}

				for(NetRoomInfo roomInfo: deleteList) {
					if(!deleteRoom(roomInfo)) {
						joinAllQueuePlayers(roomInfo);

						if(!gameFinished(roomInfo)) {
							if(!gameStartIfPossible(roomInfo)) {
								autoStartTimerCheck(roomInfo);
								broadcastRoomInfoUpdate(roomInfo);
							}
						}
					}
				}
			}
			if(observerList.remove(channel) == true) {
				log.info("Observer logout (" + remoteAddr + ")");
			}
			if(adminList.remove(channel) == true) {
				log.info("Admin logout (" + remoteAddr + ")");
			}

			if(pInfo != null) {
				broadcastPlayerInfoUpdate(pInfo, "playerlogout");
				pInfo.delete();
			}
			broadcastUserCountToAll();
			adminSendClientList();

			log.debug("Channel close success");
		} catch (Exception e) {
			log.warn("Exception throwed on logout", e);
		}

		if(channelList.isEmpty()) {
			cleanup();
		} else if(playerInfoMap.isEmpty()) {
			roomInfoList.clear();
		}
	}

	/**
	 * Cleanup (after all clients are disconnected)
	 */
	private void cleanup() {
		log.info("Cleanup");

		channelList.clear();
		bufferMap.clear();
		notCompletePacketMap.clear();
		observerList.clear();
		adminList.clear();
		playerInfoMap.clear();
		roomInfoList.clear();

		System.gc();
	}

	/**
	 * Send a message
	 * @param client SocketChannel
	 * @param bytes Message to send (byte[])
	 * @throws IOException If something fails. If this occurs, make sure to disconnect this client.
	 */
	private void send(SocketChannel client, byte[] bytes) throws IOException {
		if(client == null) throw new NullPointerException("client is null");
		if(bytes == null) throw new NullPointerException("bytes (message to send) is null");

		log.debug("Send: " + client);

		ByteArrayOutputStream bout = bufferMap.get(client);
		if (bout == null) {
			bout = new ByteArrayOutputStream();
			bufferMap.put(client, bout);
		}
		bout.write(bytes);

		client.register(selector, SelectionKey.OP_WRITE);
	}

	/**
	 * Send a message
	 * @param client SocketChannel
	 * @param msg Message to send (String)
	 * @throws IOException If something fails. If this occurs, make sure to disconnect this client.
	 */
	private void send(SocketChannel client, String msg) throws IOException  {
		send(client, NetUtil.stringToBytes(msg));
	}

	/**
	 * Send a message
	 * @param pInfo NetPlayerInfo
	 * @param msg Message to send (byte[])
	 * @throws IOException If something fails. If this occurs, make sure to disconnect this client.
	 */
	@SuppressWarnings("unused")
	private void send(NetPlayerInfo pInfo, byte[] bytes) throws IOException {
		SocketChannel ch = getSocketChannelByPlayer(pInfo);
		if(ch == null) return;
		send(ch, bytes);
	}

	/**
	 * Send a message
	 * @param pInfo NetPlayerInfo
	 * @param msg Message to send (String)
	 * @throws IOException If something fails. If this occurs, make sure to disconnect this client.
	 */
	private void send(NetPlayerInfo pInfo, String msg) throws IOException {
		SocketChannel ch = getSocketChannelByPlayer(pInfo);
		if(ch == null) return;
		send(ch, NetUtil.stringToBytes(msg));
	}

	/**
	 * Broadcast a message to all players
	 * @param msg Message to send (String)
	 * @throws IOException If something fails. If this occurs, make sure to disconnect this client.
	 */
	private void broadcast(String msg) throws IOException {
		synchronized(channelList) {
			for(SocketChannel ch: channelList) {
				NetPlayerInfo p = playerInfoMap.get(ch);

				if(p != null) {
					send(ch, msg);
				}
			}
		}
	}

	/**
	 * Broadcast a message to all players in specific room
	 * @param msg Message to send (String)
	 * @param roomID Room ID (-1:Lobby)
	 * @throws IOException If something fails. If this occurs, make sure to disconnect this client.
	 */
	private void broadcast(String msg, int roomID) throws IOException {
		synchronized(channelList) {
			for(SocketChannel ch: channelList) {
				NetPlayerInfo p = playerInfoMap.get(ch);

				if((p != null) && (roomID == p.roomID)) {
					send(ch, msg);
				}
			}
		}
	}

	/**
	 * Broadcast a message to all players in specific room, except for the specified player
	 * @param msg Message to send (String)
	 * @param roomID Room ID (-1:Lobby)
	 * @param pInfo The player to avoid sending message
	 * @throws IOException If something fails. If this occurs, make sure to disconnect this client.
	 */
	private void broadcast(String msg, int roomID, NetPlayerInfo pInfo) throws IOException {
		synchronized(channelList) {
			for(SocketChannel ch: channelList) {
				NetPlayerInfo p = playerInfoMap.get(ch);

				if((p != null) && (p.uid != pInfo.uid) && (roomID == p.roomID)) {
					send(ch, msg);
				}
			}
		}
	}

	/**
	 * Broadcast a message to all observers
	 * @param msg Message to send (String)
	 */
	private void broadcastObserver(String msg) throws IOException {
		for(SocketChannel ch: observerList) {
			send(ch, msg);
		}
	}

	/**
	 * Broadcast client count (observers and players) to everyone
	 */
	private void broadcastUserCountToAll() throws IOException {
		String msg = "observerupdate\t" + playerInfoMap.size() + "\t" + observerList.size() + "\n";
		broadcast(msg);
		broadcastObserver(msg);
		writeServerStatusFile();
	}

	/**
	 * Broadcast a message to all admins
	 * @param msg Message to send (String)
	 * @throws IOException If something fails
	 */
	private void broadcastAdmin(String msg) throws IOException {
		for(SocketChannel ch: adminList) {
			send(ch, msg);
		}
	}

	/**
	 * Get SocketChannel from NetPlayerInfo
	 * @param pInfo Player
	 * @return SocketChannel (null if not found)
	 */
	private SocketChannel getSocketChannelByPlayer(NetPlayerInfo pInfo) {
		synchronized(channelList) {
			for(SocketChannel ch: channelList) {
				NetPlayerInfo p = playerInfoMap.get(ch);

				if((p != null) && (p.uid == pInfo.uid)) {
					return ch;
				}
			}
		}
		return null;
	}

	/**
	 * Process a packet.
	 * @param client The SocketChannel who sent this packet
	 * @param fullMessage The string of packet
	 * @throws IOException When something bad happens
	 */
	private void processPacket(SocketChannel client, String fullMessage) throws IOException {
		String[] message = fullMessage.split("\t");	// Split by \t
		NetPlayerInfo pInfo = playerInfoMap.get(client);	// NetPlayerInfo of this client. null if not logged in.

		// Get information of this server.
		if(message[0].equals("getinfo")) {
			int loggedInUsersCount = playerInfoMap.size();
			int observerCount = observerList.size();
			send(client, "getinfo\t" + GameManager.getVersionMajor() + "\t" + loggedInUsersCount + "\t" + observerCount + "\n");
			return;
		}
		// Disconnect request.
		if(message[0].equals("disconnect")) {
			if(isGUIEnabled && (pInfo != null)) {
				String remoteAddr = client.socket().getRemoteSocketAddress().toString();
			}
			throw new IOException("Disconnect requested by the client (this is normal)");
		}
		// Ping
		if(message[0].equals("ping")) {
			//ping\t[ID]
			if(message.length > 1) {
				int id = Integer.parseInt(message[1]);
				send(client, "pong\t" + id + "\n");
			} else {
				send(client, "pong\n");
			}
			return;
		}
		// Observer login
		if(message[0].equals("observerlogin")) {
			//observer\t[VERSION]

			// Ignore it if already logged in
			if(observerList.contains(client)) return;
			if(adminList.contains(client)) return;
			if(playerInfoMap.containsKey(client)) return;

			// Version check
			float serverVer = GameManager.getVersionMajor();
			float clientVer = Float.parseFloat(message[1]);
			if(serverVer != clientVer) {
				send(client, "observerloginfail\tDIFFERENT_VERSION\t" + serverVer + "\n");
				logout(client);
				return;
			}

			// Success
			observerList.add(client);
			send(client, "observerloginsuccess\n");
			broadcastUserCountToAll();
			adminSendClientList();

			log.info("New observer has logged in (" + client.toString() + ")");
			return;
		}
		// Player login
		if(message[0].equals("login")) {
			//login\t[VERSION]\t[NAME]\t[COUNTRY]\t[TEAM]

			// Ignore it if already logged in
			if(observerList.contains(client)) return;
			if(adminList.contains(client)) return;
			if(playerInfoMap.containsKey(client)) return;

			// Version check
			float serverVer = GameManager.getVersionMajor();
			float clientVer = Float.parseFloat(message[1]);
			if(serverVer != clientVer) {
				send(client, "loginfail\tDIFFERENT_VERSION\t" + serverVer + "\n");
				logout(client);
				return;
			}

			// Tripcode
			String originalName = NetUtil.urlDecode(message[2]);
			int sharpIndex = originalName.indexOf('#');
			boolean isTripUse = false;

			if(sharpIndex != -1) {
				String strTripKey = originalName.substring(sharpIndex + 1);
				String strTripCode = NetUtil.createTripCode(strTripKey, propServer.getProperty("netserver.tripcodemax", 10));

				if(sharpIndex > 0) {
					String strTemp = originalName.substring(0, sharpIndex);
					originalName = strTemp.replace('!', '?') + " !" + strTripCode;
				} else {
					originalName = "!" + strTripCode;
				}

				isTripUse = true;
			} else {
				originalName = originalName.replace('!', '?');
			}

			// Decide name (change to something else if needed)
			if(originalName.length() < 1) originalName = "noname";
			String name = originalName;
			int nameCount = 0;
			while(searchPlayerByName(name) != null) {
				name = originalName + "(" + nameCount + ")";
				nameCount++;
			}

			// Set variables
			pInfo = new NetPlayerInfo();
			pInfo.strName = name;
			if(message.length > 3) pInfo.strCountry = message[3];
			if(message.length > 4) pInfo.strTeam = NetUtil.urlDecode(message[4]);
			pInfo.uid = playerCount;
			pInfo.connected = true;
			pInfo.isTripUse = isTripUse;

			pInfo.strRealHost = client.socket().getInetAddress().getHostName();
			pInfo.strRealIP = client.socket().getInetAddress().getHostAddress();

			int showhosttype = propServer.getProperty("netserver.showhosttype", 0);
			if(showhosttype == 1) {
				pInfo.strHost = client.socket().getInetAddress().getHostAddress();
			} else if(showhosttype == 2) {
				pInfo.strHost = client.socket().getInetAddress().getHostName();
			} else if(showhosttype == 3) {
				pInfo.strHost = Crypt.crypt(propServer.getProperty("netserver.hostsalt", "AA"), client.socket().getInetAddress().getHostAddress());

				int maxlen = propServer.getProperty("netserver.hostcryptmax", 8);
				if(pInfo.strHost.length() > maxlen) {
					pInfo.strHost = pInfo.strHost.substring(pInfo.strHost.length() - maxlen);
				}
			} else if(showhosttype == 4) {
				pInfo.strHost = Crypt.crypt(propServer.getProperty("netserver.hostsalt", "AA"), client.socket().getInetAddress().getHostName());

				int maxlen = propServer.getProperty("netserver.hostcryptmax", 8);
				if(pInfo.strHost.length() > maxlen) {
					pInfo.strHost = pInfo.strHost.substring(pInfo.strHost.length() - maxlen);
				}
			}

			// Load rating
			getPlayerDataFromProperty(pInfo);
			//log.info("Play:" + pInfo.playCount[0] + " Win:" + pInfo.winCount[0]);

			// Success
			playerInfoMap.put(client, pInfo);
			playerCount++;
			send(client, "loginsuccess\t" + NetUtil.urlEncode(pInfo.strName) + "\t" + pInfo.uid + "\n");
			log.info(pInfo.strName + " has logged in (Host:" + client.socket().getInetAddress().getHostName() + " Team:" + pInfo.strTeam + ")");

			sendRatedRuleList(client);
			sendPlayerList(client);
			sendRoomList(client);

			broadcastPlayerInfoUpdate(pInfo, "playernew");
			broadcastUserCountToAll();
			adminSendClientList();

			if(isGUIEnabled) {
				String remoteAddr = client.socket().getRemoteSocketAddress().toString();
			}
			return;
		}
		// Send rule data to server (Client->Server)
		if(message[0].equals("ruledata")) {
			//ruledata\t[ADLER32CHECKSUM]\t[RULEDATA]

			if(pInfo != null) {
				String strData = message[2];

				// Is checksum correct?
				Adler32 checksumObj = new Adler32();
				checksumObj.update(NetUtil.stringToBytes(strData));
				long sChecksum = checksumObj.getValue();
				long cChecksum = Long.parseLong(message[1]);

				// OK
				if(sChecksum == cChecksum) {
					String strRuleData = NetUtil.decompressString(strData);

					CustomProperties prop = new CustomProperties();
					prop.decode(strRuleData);
					pInfo.ruleOpt = new RuleOptions();
					pInfo.ruleOpt.readProperty(prop, 0);
					send(client, "ruledatasuccess\n");
				}
				// FAIL
				else {
					send(client, "ruledatafail\t" + sChecksum + "\n");
				}
			}
			return;
		}
		// Get rule data from server (Server->Client)
		if(message[0].equals("ruleget")) {
			//ruleget\t[UID]

			if(pInfo != null) {
				int uid = Integer.parseInt(message[1]);
				NetPlayerInfo p = searchPlayerByUID(uid);

				if(p != null) {
					if(p.ruleOpt == null) p.ruleOpt = new RuleOptions();

					CustomProperties prop = new CustomProperties();
					p.ruleOpt.writeProperty(prop, 0);
					String strRuleTemp = prop.encode("RuleData " + p.strName);
					String strRuleData = NetUtil.compressString(strRuleTemp);

					// Checksum
					Adler32 checksumObj = new Adler32();
					checksumObj.update(NetUtil.stringToBytes(strRuleData));
					long sChecksum = checksumObj.getValue();

					send(client, "rulegetsuccess\t" + uid + "\t" + sChecksum + "\t" + strRuleData + "\n");
				} else {
					send(client, "rulegetfail\t" + uid + "\n");
				}
			}
			return;
		}
		// Send rated-game rule data (Server->Client)
		if(message[0].equals("rulegetrated")) {
			//rulegetrated\t[STYLE]\t[NAME]

			if(pInfo != null) {
				int style = Integer.parseInt(message[1]);
				String name = message[2];
				RuleOptions rule = getRatedRule(style, name);

				if(rule != null) {
					CustomProperties prop = new CustomProperties();
					rule.writeProperty(prop, 0);
					String strRuleTemp = prop.encode("Rated RuleData " + rule.strRuleName);
					String strRuleData = NetUtil.compressString(strRuleTemp);

					// Checksum
					Adler32 checksumObj = new Adler32();
					checksumObj.update(NetUtil.stringToBytes(strRuleData));
					long sChecksum = checksumObj.getValue();

					send(client, "rulegetratedsuccess\t" + style + "\t" + name + "\t" + sChecksum + "\t" + strRuleData + "\n");
				} else {
					send(client, "rulegetratedfail\t" + style + "\t" + name + "\n");
				}
			}
		}
		// Lobby chat
		if(message[0].equals("lobbychat")) {
			//lobbychat\t[MESSAGE]

			if(pInfo != null) {
				broadcast("lobbychat\t" + pInfo.uid + "\t" + NetUtil.urlEncode(pInfo.strName) + "\t" + message[1] + "\n");
				log.info("LobbyChat Name:" + pInfo.strName + " Msg:" + NetUtil.urlDecode(message[1]));
			}
			return;
		}
		// Room chat
		if(message[0].equals("chat")) {
			//chat\t[MESSAGE]

			if((pInfo != null) && (pInfo.roomID != -1)) {
				broadcast("chat\t" + pInfo.uid + "\t" + NetUtil.urlEncode(pInfo.strName) + "\t" + message[1] + "\n", pInfo.roomID);
				log.info("RoomID:" + pInfo.roomID + " Name:" + pInfo.strName + " Msg:" + NetUtil.urlDecode(message[1]));
			}
			return;
		}
		// Get multiplayer leaderboard
		if(message[0].equals("mpranking")) {
			//mpranking\t[STYLE]

			if((pInfo != null) || adminList.contains(client)) {
				int style = Integer.parseInt(message[1]);
				int myRank = mpRankingIndexOf(style, pInfo);

				String strPData = "";
				int prevRating = -1;
				int nowRank = 0;
				for(int i = 0; i < mpRankingList[style].size(); i++) {
					NetPlayerInfo p = (NetPlayerInfo)mpRankingList[style].get(i);
					if((i == 0) || (p.rating[style] < prevRating)) {
						prevRating = p.rating[style];
						nowRank = i;
					}
					strPData += (nowRank) + ";" + NetUtil.urlEncode(p.strName) + ";" +
								p.rating[style] + ";" + p.playCount[style] + ";" + p.winCount[style] + "\t";
				}
				if((myRank == -1) && (pInfo != null)) {
					NetPlayerInfo p = pInfo;
					strPData += (-1) + ";" + NetUtil.urlEncode(p.strName) + ";" +
								p.rating[style] + ";" + p.playCount[style] + ";" + p.winCount[style] + "\t";
				}
				String strPDataC = NetUtil.compressString(strPData);

				String strMsg = "mpranking\t" + style + "\t" + myRank + "\t" + strPDataC + "\n";
				send(client, strMsg);
			}
		}
		// Single player room
		if(message[0].equals("singleroomcreate")) {
			//singleroomcreate\t[roomName]\t[mode]\t[rule]
			if((pInfo != null) && (pInfo.roomID == -1)) {
				NetRoomInfo roomInfo = new NetRoomInfo();

				roomInfo.singleplayer = true;
				roomInfo.strMode = NetUtil.urlDecode(message[2]);

				roomInfo.strName = NetUtil.urlDecode(message[1]);
				if(roomInfo.strName.length() < 1) roomInfo.strName = "Single:" + roomInfo.strMode;

				roomInfo.maxPlayers = 1;

				if(message.length > 3) {
					roomInfo.ruleName = NetUtil.urlDecode(message[3]);
					roomInfo.ruleOpt = new RuleOptions(getRatedRule(0, roomInfo.ruleName));
					roomInfo.ruleLock = true;
					roomInfo.rated = true;
				} else {
					roomInfo.ruleName = pInfo.ruleOpt.strRuleName;
					roomInfo.ruleOpt = new RuleOptions(pInfo.ruleOpt);
					roomInfo.ruleLock = false;
					roomInfo.rated = false;
				}

				roomInfo.roomID = roomCount;

				roomCount++;
				if(roomCount == -1) roomCount = 0;

				roomInfoList.add(roomInfo);

				pInfo.roomID = roomInfo.roomID;
				pInfo.resetPlayState();

				roomInfo.playerList.add(pInfo);
				pInfo.seatID = roomInfo.joinSeat(pInfo);

				// Send rule data if rated room
				if(roomInfo.rated) {
					CustomProperties prop = new CustomProperties();
					roomInfo.ruleOpt.writeProperty(prop, 0);
					String strRuleTemp = prop.encode("RuleData");
					String strRuleData = NetUtil.compressString(strRuleTemp);
					send(client, "rulelock\t" + strRuleData + "\n");
				}

				broadcastPlayerInfoUpdate(pInfo);
				broadcastRoomInfoUpdate(roomInfo, "roomcreate");
				send(client, "roomcreatesuccess\t" + roomInfo.roomID + "\t0\t-1\n");

				log.info("NewSingleRoom ID:" + roomInfo.roomID + " Title:" + roomInfo.strName);
			}
		}
		// Multiplayer room
		if(message[0].equals("roomcreate")) {
			/*
				String msg;
				msg = "roomcreate\t" + roomName + "\t" + integerMaxPlayers + "\t" + integerAutoStartSeconds + "\t";
				msg += integerGravity + "\t" + integerDenominator + "\t" + integerARE + "\t" + integerARELine + "\t";
				msg += integerLineDelay + "\t" + integerLockDelay + "\t" + integerDAS + "\t" + rulelock + "\t";
				msg += tspinEnableType + "\t" + b2b + "\t" + combo + "\t" + reduceLineSend + "\t" + integerHurryupSeconds + "\t";
				msg += integerHurryupInterval + "\t" + autoStartTNET2 + "\t" + disableTimerAfterSomeoneCancelled + "\t";
				msg += useMap + "\t" + useFractionalGarbage + "\t" + garbageChangePerAttack + "\t" + integerGarbagePercent + "\t";
				msg += strMode + "\t" + style + "\t" + strRule + "\t" strMap + "\n";
			 */
			if((pInfo != null) && (pInfo.roomID == -1)) {
				NetRoomInfo roomInfo = new NetRoomInfo();

				roomInfo.strName = NetUtil.urlDecode(message[1]);
				if(roomInfo.strName.length() < 1) roomInfo.strName = "No Title";

				roomInfo.maxPlayers = Integer.parseInt(message[2]);
				if(roomInfo.maxPlayers < 1) roomInfo.maxPlayers = 1;
				if(roomInfo.maxPlayers > 6) roomInfo.maxPlayers = 6;

				roomInfo.autoStartSeconds = Integer.parseInt(message[3]);
				roomInfo.gravity = Integer.parseInt(message[4]);
				roomInfo.denominator = Integer.parseInt(message[5]);
				roomInfo.are = Integer.parseInt(message[6]);
				roomInfo.areLine = Integer.parseInt(message[7]);
				roomInfo.lineDelay = Integer.parseInt(message[8]);
				roomInfo.lockDelay = Integer.parseInt(message[9]);
				roomInfo.das = Integer.parseInt(message[10]);

				roomInfo.ruleLock = Boolean.parseBoolean(message[11]);
				if(roomInfo.ruleLock) {
					roomInfo.ruleName = pInfo.ruleOpt.strRuleName;
					roomInfo.ruleOpt = new RuleOptions(pInfo.ruleOpt);
				}

				roomInfo.tspinEnableType = Integer.parseInt(message[12]);
				roomInfo.b2b = Boolean.parseBoolean(message[13]);
				roomInfo.combo = Boolean.parseBoolean(message[14]);
				roomInfo.rensaBlock = Boolean.parseBoolean(message[15]);
				roomInfo.counter = Boolean.parseBoolean(message[16]);
				roomInfo.bravo = Boolean.parseBoolean(message[17]);
				roomInfo.reduceLineSend = Boolean.parseBoolean(message[18]);
				roomInfo.hurryupSeconds = Integer.parseInt(message[19]);
				roomInfo.hurryupInterval = Integer.parseInt(message[20]);
				roomInfo.autoStartTNET2 = Boolean.parseBoolean(message[21]);
				roomInfo.disableTimerAfterSomeoneCancelled = Boolean.parseBoolean(message[22]);
				roomInfo.useMap = Boolean.parseBoolean(message[23]);
				roomInfo.useFractionalGarbage = Boolean.parseBoolean(message[24]);
				roomInfo.garbageChangePerAttack = Boolean.parseBoolean(message[25]);
				roomInfo.garbagePercent = Integer.parseInt(message[26]);
				roomInfo.spinCheckType = Integer.parseInt(message[27]);
				roomInfo.tspinEnableEZ = Boolean.parseBoolean(message[28]);
				roomInfo.b2bChunk = Boolean.parseBoolean(message[29]);
				roomInfo.strMode = NetUtil.urlDecode(message[30]);
				roomInfo.style = Integer.parseInt(message[31]);

				// Rule
				if((message.length > 32) && (message[32].length() > 0)) {
					roomInfo.ruleName = NetUtil.urlDecode(message[32]);
					roomInfo.ruleOpt = new RuleOptions(getRatedRule(roomInfo.style, roomInfo.ruleName));
					roomInfo.ruleLock = true;
					roomInfo.rated = true;
				}

				// If ranked room, overwrite most settings with predefined ones
				if(roomInfo.rated) {
					int style = roomInfo.style;
					// TODO: Add proper settings selector
					int id = (Integer)ruleSettingIDList[style].get(getRatedRuleIndex(style, roomInfo.ruleName));

					roomInfo.gravity = propServer.getProperty(style + "." + id + ".ranked.gravity", 1);
					roomInfo.denominator = propServer.getProperty(style + "." + id + ".ranked.denominator", 60);
					roomInfo.are = propServer.getProperty(style + "." + id + ".ranked.are", 0);
					roomInfo.areLine = propServer.getProperty(style + "." + id + ".ranked.areLine", 0);
					roomInfo.lineDelay = propServer.getProperty(style + "." + id + ".ranked.lineDelay", 0);
					roomInfo.lockDelay = propServer.getProperty(style + "." + id + ".ranked.lockDelay", 30);
					roomInfo.das = propServer.getProperty(style + "." + id + ".ranked.das", 12);

					roomInfo.tspinEnableType = propServer.getProperty(style + "." + id + ".ranked.tspinEnableType", 2);
					roomInfo.b2b = propServer.getProperty(style + "." + id + ".ranked.b2b", true);
					roomInfo.combo = propServer.getProperty(style + "." + id + ".ranked.combo", true);
					roomInfo.rensaBlock = propServer.getProperty(style + "." + id + ".ranked.rensaBlock", true);
					roomInfo.counter = propServer.getProperty(style + "." + id + ".ranked.counter", true);
					roomInfo.bravo = propServer.getProperty(style + "." + id + ".ranked.bravo", true);
					roomInfo.reduceLineSend = propServer.getProperty(style + "." + id + ".ranked.reduceLineSend", true);
					roomInfo.useFractionalGarbage = propServer.getProperty(style + "." + id + ".ranked.useFractionalGarbage", false);
					roomInfo.garbageChangePerAttack = propServer.getProperty(style + "." + id + ".ranked.garbageChangePerAttack", true);
					roomInfo.garbagePercent = propServer.getProperty(style + "." + id + ".ranked.garbagePercent", 100);
					roomInfo.spinCheckType = propServer.getProperty(style + "." + id + ".ranked.spinCheckType", 0);
					roomInfo.tspinEnableEZ = propServer.getProperty(style + "." + id + ".ranked.tspinEnableEZ", false);
					roomInfo.b2bChunk = propServer.getProperty(style + "." + id + ".ranked.b2bChunk", false);
					roomInfo.hurryupSeconds = propServer.getProperty(style + "." + id + ".ranked.hurryupSeconds", -1);
					roomInfo.hurryupInterval = propServer.getProperty(style + "." + id + ".ranked.hurryupInterval", 5);
					roomInfo.useMap = false;	// TODO: Add force map to use
				}

				// Set map
				if(roomInfo.useMap && (message.length > 33)) {
					String strDecompressed = NetUtil.decompressString(message[33]);
					String[] strMaps = strDecompressed.split("\t");

					int maxMap = strMaps.length;

					for(int i = 0; i < maxMap; i++) {
						String strMap = strMaps[i];
						roomInfo.mapList.add(strMap);
					}

					if(roomInfo.mapList.isEmpty()) {
						log.debug("Room" + roomInfo.roomID + ": No maps");
						roomInfo.useMap = false;
					} else {
						log.debug("Room" + roomInfo.roomID + ": Received " + roomInfo.mapList.size() + " maps");
					}
				}

				roomInfo.roomID = roomCount;

				roomCount++;
				if(roomCount == -1) roomCount = 0;

				roomInfoList.add(roomInfo);

				pInfo.roomID = roomInfo.roomID;
				pInfo.resetPlayState();

				roomInfo.playerList.add(pInfo);
				pInfo.seatID = roomInfo.joinSeat(pInfo);

				// Send rule data if rule-lock is enabled
				if(roomInfo.ruleLock || roomInfo.rated) {
					CustomProperties prop = new CustomProperties();
					roomInfo.ruleOpt.writeProperty(prop, 0);
					String strRuleTemp = prop.encode("RuleData");
					String strRuleData = NetUtil.compressString(strRuleTemp);
					send(client, "rulelock\t" + strRuleData + "\n");
					//log.info("rulelock\t" + strRuleData);
				}

				broadcastPlayerInfoUpdate(pInfo);
				broadcastRoomInfoUpdate(roomInfo, "roomcreate");
				send(client, "roomcreatesuccess\t" + roomInfo.roomID + "\t" + pInfo.seatID + "\t-1\n");

				log.info("NewRoom ID:" + roomInfo.roomID + " Title:" + roomInfo.strName + " RuleLock:" + roomInfo.ruleLock +
						 " Map:" + roomInfo.useMap + " Mode:" + roomInfo.strMode);
			}
			return;
		}
		// Join room (If roomID is -1, the player will return to lobby)
		if(message[0].equals("roomjoin")) {
			//roomjoin\t[ROOMID]\t[WATCH]

			if(pInfo != null) {
				int roomID = Integer.parseInt(message[1]);
				boolean watch = Boolean.parseBoolean(message[2]);
				NetRoomInfo prevRoom = getRoomInfo(pInfo.roomID);
				NetRoomInfo newRoom = getRoomInfo(roomID);

				if(roomID < 0) {
					// Return to lobby
					if(prevRoom != null) {
						int seatID = pInfo.seatID;
						broadcast("playerleave\t" + pInfo.uid + "\t" + NetUtil.urlEncode(pInfo.strName) + "\t" + seatID + "\n",
								prevRoom.roomID, pInfo);
						playerDead(pInfo);
						pInfo.ready = false;
						prevRoom.exitSeat(pInfo);
						prevRoom.exitQueue(pInfo);
						prevRoom.playerList.remove(pInfo);
						if(!deleteRoom(prevRoom)) {
							joinAllQueuePlayers(prevRoom);

							if(!gameFinished(prevRoom)) {
								if(!gameStartIfPossible(prevRoom)) {
									autoStartTimerCheck(prevRoom);
									broadcastRoomInfoUpdate(prevRoom);
								}
							}
						}
					}
					pInfo.roomID = -1;
					pInfo.seatID = -1;
					pInfo.queueID = -1;
					pInfo.resetPlayState();

					broadcastPlayerInfoUpdate(pInfo);
					send(client, "roomjoinsuccess\t-1\t-1\t-1\n");
				} else if(newRoom != null) {
					// Enter a room
					if(prevRoom != null) {
						int seatID = pInfo.seatID;
						broadcast("playerleave\t" + pInfo.uid + "\t" + NetUtil.urlEncode(pInfo.strName) + "\t" + seatID + "\n",
								prevRoom.roomID, pInfo);
						playerDead(pInfo);
						pInfo.ready = false;
						prevRoom.exitSeat(pInfo);
						prevRoom.exitQueue(pInfo);
						prevRoom.playerList.remove(pInfo);
						if(!deleteRoom(prevRoom)) {
							joinAllQueuePlayers(prevRoom);

							if(!gameFinished(prevRoom)) {
								if(!gameStartIfPossible(prevRoom)) {
									autoStartTimerCheck(prevRoom);
									broadcastRoomInfoUpdate(prevRoom);
								}
							}
						}
					}
					pInfo.roomID = newRoom.roomID;
					pInfo.resetPlayState();
					newRoom.playerList.add(pInfo);

					pInfo.seatID = -1;
					if(!watch && !newRoom.singleplayer) {
						pInfo.seatID = newRoom.joinSeat(pInfo);

						if(pInfo.seatID == -1) {
							pInfo.queueID = newRoom.joinQueue(pInfo);
						}
					}

					// Send rule data if rule-lock is enabled
					if(newRoom.ruleLock || newRoom.rated) {
						CustomProperties prop = new CustomProperties();
						newRoom.ruleOpt.writeProperty(prop, 0);
						String strRuleTemp = prop.encode("RuleData");
						String strRuleData = NetUtil.compressString(strRuleTemp);
						send(client, "rulelock\t" + strRuleData + "\n");
						//log.info("rulelock\t" + strRuleData);
					}

					// Map send
					if(newRoom.useMap && !newRoom.mapList.isEmpty()) {
						String strMapTemp = "";
						int maxMap = newRoom.mapList.size();
						for(int i = 0; i < maxMap; i++) {
							strMapTemp += newRoom.mapList.get(i);
							if(i < maxMap - 1) strMapTemp += "\t";
						}
						String strCompressed = NetUtil.compressString(strMapTemp);
						send(client, "map\t" + strCompressed + "\n");
					}

					broadcast("playerenter\t" + pInfo.uid + "\t" + NetUtil.urlEncode(pInfo.strName) + "\t" + pInfo.seatID + "\n",
							newRoom.roomID, pInfo);
					broadcastRoomInfoUpdate(newRoom);
					broadcastPlayerInfoUpdate(pInfo);
					send(client, "roomjoinsuccess\t" + newRoom.roomID + "\t" + pInfo.seatID + "\t" + pInfo.queueID + "\n");
				} else {
					// No such a room
					send(client, "roomjoinfail\n");
				}
			}
			return;
		}
		// Change team
		if(message[0].equals("changeteam")) {
			//changeteam\t[TEAM]
			if((pInfo != null) && (!pInfo.playing)) {
				String strTeam = "";
				if(message.length > 1) strTeam = NetUtil.urlDecode(message[1]);

				if(!strTeam.equalsIgnoreCase(pInfo.strTeam)) {
					pInfo.strTeam = strTeam;
					broadcastPlayerInfoUpdate(pInfo);

					broadcast("changeteam\t" + pInfo.uid + "\t" + NetUtil.urlEncode(pInfo.strName) + "\t" + NetUtil.urlEncode(pInfo.strTeam) + "\n",
							  pInfo.roomID);
				}
			}
		}
		// Change Player/Spectator status
		if(message[0].equals("changestatus")) {
			//changestatus\t[WATCH]
			if((pInfo != null) && (!pInfo.playing) && (pInfo.roomID != -1)) {
				NetRoomInfo roomInfo = getRoomInfo(pInfo.roomID);
				boolean watch = Boolean.parseBoolean(message[1]);

				if(!roomInfo.singleplayer) {
					if(watch) {
						// Change to spectator
						int prevSeatID = pInfo.seatID;
						roomInfo.exitSeat(pInfo);
						roomInfo.exitQueue(pInfo);
						pInfo.ready = false;
						pInfo.seatID = -1;
						pInfo.queueID = -1;
						//send(client, "changestatus\twatchonly\t-1\n");
						broadcast("changestatus\twatchonly\t" + pInfo.uid + "\t" + NetUtil.urlEncode(pInfo.strName) + "\t" + prevSeatID + "\n",
								  pInfo.roomID);

						joinAllQueuePlayers(roomInfo);	// Let the queue-player to join
					} else {
						// Change to player
						if(roomInfo.canJoinSeat()) {
							pInfo.seatID = roomInfo.joinSeat(pInfo);
							pInfo.queueID = -1;
							pInfo.ready = false;
							//send(client, "changestatus\tjoinseat\t" + pInfo.seatID + "\n");
							broadcast("changestatus\tjoinseat\t" + pInfo.uid + "\t" + NetUtil.urlEncode(pInfo.strName) + "\t" + pInfo.seatID + "\n",
									  pInfo.roomID);
						} else {
							pInfo.seatID = -1;
							pInfo.queueID = roomInfo.joinQueue(pInfo);
							pInfo.ready = false;
							//send(client, "changestatus\tjoinqueue\t" + pInfo.queueID + "\n");
							broadcast("changestatus\tjoinqueue\t" + pInfo.uid + "\t" + NetUtil.urlEncode(pInfo.strName) + "\t" + pInfo.queueID + "\n",
									  pInfo.roomID);
						}
					}
					broadcastPlayerInfoUpdate(pInfo);
					if(!gameStartIfPossible(roomInfo)) {
						autoStartTimerCheck(roomInfo);
					}
					broadcastRoomInfoUpdate(roomInfo);
				}
			}
		}
		// Start game (Single player)
		if(message[0].equals("start1p")) {
			if(pInfo != null) {
				log.info("Starting single player game");

				NetRoomInfo roomInfo = getRoomInfo(pInfo.roomID);
				int seat = roomInfo.getPlayerSeatNumber(pInfo);

				if((seat != -1) && (roomInfo.singleplayer)) {
					gameStart(roomInfo);
				}
			}
		}
		// Ready state change
		if(message[0].equals("ready")) {
			//ready\t[STATE]
			if(pInfo != null) {
				NetRoomInfo roomInfo = getRoomInfo(pInfo.roomID);
				int seat = roomInfo.getPlayerSeatNumber(pInfo);

				if((seat != -1) && (!roomInfo.singleplayer)) {
					pInfo.ready = Boolean.parseBoolean(message[1]);
					broadcastPlayerInfoUpdate(pInfo);

					if(!pInfo.ready) roomInfo.isSomeoneCancelled = true;

					// Start a game if possible
					if(!gameStartIfPossible(roomInfo)) {
						autoStartTimerCheck(roomInfo);
					}
				}
			}
		}
		// Autostart
		if(message[0].equals("autostart")) {
			if(pInfo != null) {
				NetRoomInfo roomInfo = getRoomInfo(pInfo.roomID);
				int seat = roomInfo.getPlayerSeatNumber(pInfo);

				if((seat != -1) && (roomInfo.autoStartActive) && (!roomInfo.singleplayer)) {
					if(roomInfo.autoStartTNET2) {
						// Move all non-ready players to spectators
						LinkedList<NetPlayerInfo> pList = new LinkedList<NetPlayerInfo>();
						pList.addAll(roomInfo.playerSeat);

						for(NetPlayerInfo p: pList) {
							if((p != null) && (!p.ready)) {
								int prevSeatID = p.seatID;
								roomInfo.exitSeat(p);
								roomInfo.exitQueue(p);
								p.ready = false;
								p.seatID = -1;
								p.queueID = -1;
								broadcast("changestatus\twatchonly\t" + p.uid + "\t" + NetUtil.urlEncode(p.strName) + "\t" + prevSeatID + "\n",
										  p.roomID);
							}
						}

						joinAllQueuePlayers(roomInfo);
					}

					gameStart(roomInfo);
				}
			}
		}
		// Dead
		if(message[0].equals("dead")) {
			if(pInfo != null) {
				if(message.length > 1) {
					int koUID = Integer.parseInt(message[1]);
					NetPlayerInfo koPlayerInfo = searchPlayerByUID(koUID);
					playerDead(pInfo, koPlayerInfo);
				} else {
					playerDead(pInfo);
				}
			}
		}
		// Multiplayer end-of-game stats
		if(message[0].equals("gstat")) {
			if((pInfo != null) && (pInfo.roomID != -1) && (pInfo.seatID != -1)) {
				NetRoomInfo roomInfo = getRoomInfo(pInfo.roomID);

				if(!roomInfo.singleplayer) {
					String msg = "gstat\t" + pInfo.uid + "\t" + pInfo.seatID + "\t" + NetUtil.urlEncode(pInfo.strName) + "\t";
					for(int i = 1; i < message.length; i++) {
						msg += message[i];
						if(i < message.length - 1) msg += "\t";
					}
					msg += "\n";

					broadcast(msg, roomInfo.roomID);
				}
			}
		}
		// Single player end-of-game stats
		if(message[0].equals("gstat1p")) {
			if((pInfo != null) && (pInfo.roomID != -1) && (pInfo.seatID != -1)) {
				NetRoomInfo roomInfo = getRoomInfo(pInfo.roomID);

				if(roomInfo.singleplayer) {
					String msg = "gstat1p\t" + message[1] + "\n";
					broadcast(msg, roomInfo.roomID);
				}
			}
		}
		// Game messages (NetServer will deliver them to other players but won't modify it)
		if(message[0].equals("game")) {
			if(pInfo != null) {
				NetRoomInfo roomInfo = getRoomInfo(pInfo.roomID);
				if(roomInfo != null) {
					int seat = roomInfo.getPlayerSeatNumber(pInfo);

					if(seat != -1) {
						String msg = "game\t" + pInfo.uid + "\t" + seat + "\t";
						for(int i = 1; i < message.length; i++) {
							msg += message[i];
							if(i < message.length - 1) msg += "\t";
						}
						msg += "\n";
						broadcast(msg, roomInfo.roomID, pInfo);
					}
				}
			}
		}
		// ADMIN: Admin Login
		if(message[0].equals("adminlogin")) {
			// Ignore it if already logged in
			if(observerList.contains(client)) return;
			if(adminList.contains(client)) return;
			if(playerInfoMap.containsKey(client)) return;

			String strRemoteAddr = client.socket().getInetAddress().getHostName();

			// Check version
			float serverVer = GameManager.getVersionMajor();
			float clientVer = Float.parseFloat(message[1]);
			if(serverVer != clientVer) {
				logout(client);
				return;
			}

			// Check username and password
			String strServerUsername = propServer.getProperty("netserver.admin.username", "");
			String strServerPassword = propServer.getProperty("netserver.admin.password", "");
			if((strServerUsername.length() == 0) || (strServerPassword.length() == 0)) {
				log.warn(strRemoteAddr + " has tried to access admin, but admin is disabled");
				send(client, "adminloginfail\tDISABLE\n");
				return;
			}

			String strClientUsername = message[2];
			if(!strClientUsername.equals(strServerUsername)) {
				log.warn(strRemoteAddr + " has tried to access admin with incorrect username (" + strClientUsername + ")");
				send(client, "adminloginfail\tFAIL\n");
				return;
			}

			if(rc4 == null) rc4 = new RC4(strServerPassword);
			byte[] bPass = Base64Coder.decode(message[3]);
			byte[] bPass2 = rc4.rc4(bPass);
			String strClientPasswordCheckData = NetUtil.bytesToString(bPass2);
			if(!strClientPasswordCheckData.equals(strServerUsername)) {
				log.warn(strRemoteAddr + " has tried to access admin with incorrect password (Username:" + strClientUsername + ")");
				send(client, "adminloginfail\tFAIL\n");
				return;
			}

			// Login successful
			adminList.add(client);
			InetAddress addr = client.socket().getInetAddress();
			send(client, "adminloginsuccess\t" + addr.getHostAddress() + "\t" + addr.getHostName() + "\n");
			adminSendClientList();
			log.info("Admin has logged in (" + strRemoteAddr + ")");
		}
		// ADMIN: Admin commands
		if(message[0].equals("admin")) {
			if(adminList.contains(client)) {
				String strAdminCommandTemp = NetUtil.decompressString(message[1]);
				String[] strAdminCommandArray = strAdminCommandTemp.split("\t");
				processAdminCommand(client, strAdminCommandArray);
			} else {
				String strRemoteAddr = client.socket().getInetAddress().getHostName();
				log.warn(strRemoteAddr + " has tried to access admin command without login");
				logout(client);
				return;
			}
		}
	}

	/**
	 * Process admin command
	 * @param client The SocketChannel who sent this packet
	 * @param message The String array of the command
	 * @throws IOException When something bad happens
	 */
	private void processAdminCommand(SocketChannel client, String[] message) throws IOException {
		// Client list (force update)
		if(message[0].equals("clientlist")) {
			adminSendClientList(client);
		}
		// Ban
		if(message[0].equals("ban")) {
			// ban\t[IP]\t(Length)
			if(message.length > 2)
				ban(message[1], Integer.parseInt(message[2]));
			else
				ban(message[1], -1);
		}
	}

	/**
	 * Send admin command result
	 * @param client The admin
	 * @param msg Message to send
	 * @throws IOException When something bad happens
	 */
	private void sendAdminResult(SocketChannel client, String msg) throws IOException {
		send(client, "adminresult\t" + NetUtil.compressString(msg) + "\n");
	}

	/**
	 * Broadcast admin command result to all admins
	 * @param msg Message to send
	 * @throws IOException When something bad happens
	 */
	private void broadcastAdminResult(String msg) throws IOException {
		broadcastAdmin("adminresult\t" + NetUtil.compressString(msg) + "\n");
	}

	/**
	 * Send client list to all admins
	 * @throws IOException When something bad happens
	 */
	private void adminSendClientList() throws IOException {
		adminSendClientList(null);
	}

	/**
	 * Send client list to admin
	 * @param client The admin. If null, it will broadcast to all admins.
	 * @throws IOException When something bad happens
	 */
	private void adminSendClientList(SocketChannel client) throws IOException {
		String strMsg = "clientlist";

		for(SocketChannel ch: channelList) {
			String strIP = ch.socket().getInetAddress().getHostAddress();
			String strHost = ch.socket().getInetAddress().getHostName();
			NetPlayerInfo pInfo = playerInfoMap.get(ch);

			int type = 0;	// Type of client. 0:Not logged in
			if(pInfo != null) type = 1;	// 1:Player
			else if(observerList.contains(ch)) type = 2;	// 2:Observer
			else if(adminList.contains(ch)) type = 3;	// 3:Admin

			String strClientData = strIP + "|" + strHost + "|" + type;
			if(pInfo != null) {
				strClientData += "|" + pInfo.exportString();
			}

			strMsg += "\t" + strClientData;
		}

		if(client == null) {
			broadcastAdminResult(strMsg);
		} else {
			sendAdminResult(client, strMsg);
		}
	}

	/**
	 * Get NetRoomInfo by using roomID
	 * @param roomID Room ID
	 * @return NetRoomInfo (null if not found)
	 */
	private NetRoomInfo getRoomInfo(int roomID) {
		if(roomID == -1) return null;

		for(NetRoomInfo roomInfo: roomInfoList) {
			if(roomID == roomInfo.roomID) {
				return roomInfo;
			}
		}

		return null;
	}

	/**
	 * Send room list to specified client
	 * @param client Client to send
	 */
	private void sendRoomList(SocketChannel client) throws IOException {
		String msg = "roomlist\t" + roomInfoList.size();

		for(NetRoomInfo roomInfo: roomInfoList) {
			msg += "\t";
			msg += roomInfo.exportString();
		}

		msg += "\n";
		send(client, msg);
	}

	/**
	 * Delete a room
	 * @param roomInfo Room to delete
	 * @return true if success, false if fails (room not empty)
	 */
	private boolean deleteRoom(NetRoomInfo roomInfo) throws IOException {
		if((roomInfo != null) && (roomInfo.playerList.isEmpty())) {
			log.info("RoomDelete ID:" + roomInfo.roomID + " Title:" + roomInfo.strName);
			broadcastRoomInfoUpdate(roomInfo, "roomdelete");
			roomInfoList.remove(roomInfo);
			roomInfo.delete();
			return true;
		}
		return false;
	}

	/**
	 * Start/Stop auto start timer
	 * @param roomInfo The room
	 */
	private void autoStartTimerCheck(NetRoomInfo roomInfo) throws IOException {
		if(roomInfo.autoStartSeconds <= 0) return;

		int minPlayers = (roomInfo.autoStartTNET2) ? 2 : 1;

		if((roomInfo.getNumberOfPlayerSeated() <= 1) ||
		   (roomInfo.isSomeoneCancelled && roomInfo.disableTimerAfterSomeoneCancelled) ||
		   (roomInfo.getHowManyPlayersReady() < minPlayers) || (roomInfo.getHowManyPlayersReady() < roomInfo.getNumberOfPlayerSeated() / 2))
		{
			if(roomInfo.autoStartActive == true) {
				broadcast("autostartstop\n", roomInfo.roomID);
			}
			roomInfo.autoStartActive = false;
		}
		else if((roomInfo.autoStartActive == false) &&
				(!roomInfo.isSomeoneCancelled || !roomInfo.disableTimerAfterSomeoneCancelled) &&
				(roomInfo.getHowManyPlayersReady() >= minPlayers) && (roomInfo.getHowManyPlayersReady() >= roomInfo.getNumberOfPlayerSeated() / 2))
		{
			broadcast("autostartbegin\t" + roomInfo.autoStartSeconds + "\n", roomInfo.roomID);
			roomInfo.autoStartActive = true;
		}
	}

	/**
	 * Start a game if possible
	 * @param roomInfo The room
	 * @return true if started, false if not
	 */
	private boolean gameStartIfPossible(NetRoomInfo roomInfo) throws IOException {
		if(roomInfo.getHowManyPlayersReady() == roomInfo.getNumberOfPlayerSeated()) {
			gameStart(roomInfo);
			return true;
		}
		return false;
	}

	/**
	 * Start a game (force start)
	 * @param roomInfo The room
	 */
	private void gameStart(NetRoomInfo roomInfo) throws IOException {
		if(roomInfo == null) return;
		if(roomInfo.getNumberOfPlayerSeated() <= 0) return;
		if(roomInfo.playing) return;

		roomInfo.gameStart();

		int mapNo = 0;
		int mapMax = roomInfo.mapList.size();
		if(roomInfo.useMap && (mapMax > 0)) {
			do {
				mapNo = rand.nextInt(mapMax);
			} while ((mapNo == roomInfo.mapPrevious) && (mapMax >= 2));

			roomInfo.mapPrevious = mapNo;
		}
		String msg = "start\t" + Long.toString(rand.nextLong(), 16) + "\t" + roomInfo.startPlayers + "\t" + mapNo + "\n";
		broadcast(msg, roomInfo.roomID);

		for(NetPlayerInfo p: roomInfo.playerSeat) {
			if(p != null) {
				p.ready = false;
				p.playing = true;

				// If ranked room
				if( roomInfo.rated && !roomInfo.isTeamGame() && (!roomInfo.hasSameIPPlayers() || ratingAllowSameIP) ) {
					p.playCount[roomInfo.style]++;
					p.ratingBefore[roomInfo.style] = p.rating[roomInfo.style];
				}

				broadcastPlayerInfoUpdate(p);
			}
		}

		roomInfo.playing = true;
		roomInfo.autoStartActive = false;
		broadcastRoomInfoUpdate(roomInfo);
	}

	/**
	 * Check if the game is finished. If finished, it will notify players.
	 * @param roomInfo The room
	 * @return true if finished
	 */
	private boolean gameFinished(NetRoomInfo roomInfo) throws IOException {
		int startPlayers = roomInfo.startPlayers;
		int nowPlaying = roomInfo.getHowManyPlayersPlaying();
		boolean isTeamWin = roomInfo.isTeamWin();

		if( (roomInfo != null) && (roomInfo.playing) && ( (nowPlaying < 1) || ((startPlayers >= 2) && (nowPlaying < 2)) || (isTeamWin) ) ) {
			// Game finished
			NetPlayerInfo winner = roomInfo.getWinner();
			String msg = "finish\t";

			if(isTeamWin) {
				// Winner is a team
				String teamName = roomInfo.getWinnerTeam();
				if(teamName == null) teamName = "";
				msg += -1 + "\t" + -1 + "\t" + NetUtil.urlEncode(teamName) + "\t" + isTeamWin;

				for(NetPlayerInfo pInfo: roomInfo.playerSeat) {
					if((pInfo != null) && (pInfo.playing)) {
						pInfo.resetPlayState();
						broadcastPlayerInfoUpdate(pInfo);
						roomInfo.playerSeatDead.addFirst(pInfo);

						// Rated game
						/*
						if(roomInfo.rated) {
							// TODO: Update ratings?
							pInfo.winCount[roomInfo.style]++;
							setPlayerDataToProperty(pInfo);
						}
						*/
					}
				}

				/*
				if(roomInfo.rated) {
					writePlayerDataToFile();
				}
				*/
			} else if(winner != null) {
				// Winner is a player
				roomInfo.playerSeatDead.addFirst(winner);

				// Rated game
				if( roomInfo.rated && !roomInfo.isTeamGame() && (!roomInfo.hasSameIPPlayers() || ratingAllowSameIP) ) {
					// Update win count
					winner.winCount[roomInfo.style]++;

					// Update rating
					int style = roomInfo.style;
					int n = roomInfo.playerSeatDead.size();
					for(int w = 0; w < n - 1; w++) {
						for(int l = w + 1; l < n; l++) {
							NetPlayerInfo wp = roomInfo.playerSeatDead.get(w);
							NetPlayerInfo lp = roomInfo.playerSeatDead.get(l);

							wp.rating[style] += (int) (rankDelta(wp.playCount[style], wp.rating[style], lp.rating[style], 1) / (n-1));
							lp.rating[style] += (int) (rankDelta(lp.playCount[style], lp.rating[style], wp.rating[style], 0) / (n-1));

							if(wp.rating[style] < ratingMin) wp.rating[style] = ratingMin;
							if(lp.rating[style] < ratingMin) lp.rating[style] = ratingMin;
							if(wp.rating[style] > ratingMax) wp.rating[style] = ratingMax;
							if(lp.rating[style] > ratingMax) lp.rating[style] = ratingMax;
						}
					}

					// Notify/Save
					for(int i = 0; i < n; i++) {
						NetPlayerInfo p = roomInfo.playerSeatDead.get(i);
						int change = p.rating[style] - p.ratingBefore[style];
						log.debug("#" + (i+1) + " Name:" + p.strName + " Rating:" + p.rating[style] + " (" + change + ")");
						setPlayerDataToProperty(p);

						String msgRatingChange =
							"rating\t" + p.uid + "\t" + p.seatID + "\t" + NetUtil.urlEncode(p.strName) + "\t" +
							p.rating[style] + "\t" + change + "\n";
						broadcast(msgRatingChange, winner.roomID);
					}
					writePlayerDataToFile();

					// Leaderboard update
					for(int i = 0; i < n; i++) {
						NetPlayerInfo p = roomInfo.playerSeatDead.get(i);
						if(p.isTripUse) {
							mpRankingUpdate(style, p);
						}
					}
					writeMPRankingToFile();
				}

				msg += winner.uid + "\t" + winner.seatID + "\t" + NetUtil.urlEncode(winner.strName) + "\t" + isTeamWin;
				winner.resetPlayState();
				broadcastPlayerInfoUpdate(winner);
			} else {
				// No winner(s)
				msg += -1 + "\t" + -1 + "\t" + "" + "\t" + isTeamWin;
			}
			msg += "\n";
			broadcast(msg, roomInfo.roomID);

			roomInfo.playing = false;
			roomInfo.autoStartActive = false;
			broadcastRoomInfoUpdate(roomInfo);

			return true;
		}

		return false;
	}

	/**
	 * Broadcast a room update information (command will be "roomupdate")
	 * @param roomInfo The room
	 */
	private void broadcastRoomInfoUpdate(NetRoomInfo roomInfo) throws IOException {
		broadcastRoomInfoUpdate(roomInfo, "roomupdate");
	}

	/**
	 * Broadcast a room update information
	 * @param roomInfo The room
	 * @param command Command
	 */
	private void broadcastRoomInfoUpdate(NetRoomInfo roomInfo, String command) throws IOException {
		roomInfo.updatePlayerCount();
		String msg = command + "\t";
		msg += roomInfo.exportString();
		msg += "\n";
		broadcast(msg);
	}

	/**
	 * Send player list to specified client
	 * @param client Client to send
	 */
	private void sendPlayerList(SocketChannel client) throws IOException {
		String msg = "playerlist\t" + playerInfoMap.size();

		for(SocketChannel ch: channelList) {
			NetPlayerInfo pInfo = playerInfoMap.get(ch);

			if(pInfo != null) {
				msg += "\t";
				msg += pInfo.exportString();
			}
		}

		msg += "\n";
		send(client, msg);
	}

	/**
	 * Broadcast a player update information (command will be "playerupdate")
	 * @param pInfo The player
	 */
	private void broadcastPlayerInfoUpdate(NetPlayerInfo pInfo) throws IOException {
		broadcastPlayerInfoUpdate(pInfo, "playerupdate");
	}

	/**
	 * Broadcast a player update information
	 * @param pInfo The player
	 * @param command Command
	 */
	private void broadcastPlayerInfoUpdate(NetPlayerInfo pInfo, String command) throws IOException {
		String msg = command + "\t";
		msg += pInfo.exportString();
		msg += "\n";
		broadcast(msg);
	}

	/**
	 * Get NetPlayerInfo by player's name
	 * @param name Name
	 * @return NetPlayerInfo (null if not found)
	 */
	private NetPlayerInfo searchPlayerByName(String name) {
		for(SocketChannel ch: channelList) {
			NetPlayerInfo pInfo = playerInfoMap.get(ch);
			if((pInfo != null) && (pInfo.strName.equals(name))) {
				return pInfo;
			}
		}
		return null;
	}

	/**
	 * Get NetPlayerInfo by player's ID
	 * @param uid ID
	 * @return NetPlayerInfo (null if not found)
	 */
	private NetPlayerInfo searchPlayerByUID(int uid) {
		for(SocketChannel ch: channelList) {
			NetPlayerInfo pInfo = playerInfoMap.get(ch);
			if((pInfo != null) && (pInfo.uid == uid)) {
				return pInfo;
			}
		}
		return null;
	}

	/**
	 * Move queue player(s) to the game seat if possible
	 * @param roomInfo The room
	 * @return Number of players moved to the game seat
	 */
	private int joinAllQueuePlayers(NetRoomInfo roomInfo) throws IOException {
		int playerJoinedCount = 0;

		while(roomInfo.canJoinSeat() && !roomInfo.playerQueue.isEmpty()) {
			NetPlayerInfo pInfo = roomInfo.playerQueue.poll();
			pInfo.seatID = roomInfo.joinSeat(pInfo);
			pInfo.queueID = -1;
			pInfo.ready = false;
			broadcast("changestatus\tjoinseat\t" + pInfo.uid + "\t" + NetUtil.urlEncode(pInfo.strName) + "\t" + pInfo.seatID + "\n",
					  pInfo.roomID);
			broadcastPlayerInfoUpdate(pInfo);
			playerJoinedCount++;
		}

		if(playerJoinedCount > 0) broadcastRoomInfoUpdate(roomInfo);

		return playerJoinedCount;
	}

	/**
	 * Signal player-dead
	 * @param pInfo Player
	 */
	private void playerDead(NetPlayerInfo pInfo) throws IOException {
		playerDead(pInfo, null);
	}

	/**
	 * Signal player-dead
	 * @param pInfo Player
	 * @param pKOInfo Assailant (can be null)
	 */
	private void playerDead(NetPlayerInfo pInfo, NetPlayerInfo pKOInfo) throws IOException {
		NetRoomInfo roomInfo = getRoomInfo(pInfo.roomID);

		if((roomInfo != null) && (pInfo.seatID != -1) && (pInfo.playing) && (roomInfo.playing)) {
			pInfo.resetPlayState();

			int place = roomInfo.startPlayers - roomInfo.deadCount;
			String msg = "dead\t" + pInfo.uid + "\t" + NetUtil.urlEncode(pInfo.strName) + "\t" + pInfo.seatID + "\t" + place + "\t";
			if(pKOInfo == null) {
				msg += -1 + "\t" + "";
			} else {
				msg += pKOInfo.uid + "\t" + NetUtil.urlEncode(pKOInfo.strName);
			}
			msg += "\n";
			broadcast(msg, pInfo.roomID);

			roomInfo.deadCount++;
			roomInfo.playerSeatDead.addFirst(pInfo);
			gameFinished(roomInfo);

			broadcastPlayerInfoUpdate(pInfo);
		}
	}

	/**
	 * Sets a ban by IP address.
	 * @param strIP IP address
	 * @param banLength The length of the ban. (-1: Kick only, not ban)
	 */
	private void ban(String strIP, int banLength) {
		LinkedList<SocketChannel> banChannels = new LinkedList<SocketChannel>();

		for(SocketChannel ch: channelList) {
			String ip = ch.socket().getInetAddress().getHostAddress();
			if(ip.equals(strIP)) {
				banChannels.add(ch);
			}
		}
		for(SocketChannel ch: banChannels) {
			ban(ch, banLength);
		}
	}

	/**
	 * Sets a ban.
	 * @param client The remote address to ban.
	 * @param banLength The length of the ban. (-1: Kick only, not ban)
	 */
	private void ban(SocketChannel client, int banLength) {
		String remoteAddr = client.socket().getRemoteSocketAddress().toString();
		remoteAddr = remoteAddr.substring(0,remoteAddr.indexOf(':'));

		if(banLength < 0) {
			log.info("Kicked player: "+remoteAddr);
		} else {
			banList.add(new NetServerBan(remoteAddr, banLength));
			log.info("Banned player: "+remoteAddr);
		}

		logout(client);
	}

	/**
	 * Checks whether a connection is banned.
	 * @param client The remote address to check.
	 * @return true if the connection is banned, false if it is not banned or if the ban
	 * is expired.
	 */
	private boolean checkConnectionOnBanlist(SocketChannel client) {
		String remoteAddr = client.socket().getRemoteSocketAddress().toString();
		remoteAddr = remoteAddr.substring(0,remoteAddr.indexOf(':'));

		Iterator<NetServerBan> i = banList.iterator();
		NetServerBan ban;

		while (i.hasNext()) {
			ban = i.next();
			if (ban.addr.equals(remoteAddr)) {
				if (ban.isExpired()) {
					i.remove();
					return false;
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * Send rated-game rule list
	 * @param client Client
	 * @throws IOException When something bad occurs
	 */
	private void sendRatedRuleList(SocketChannel client) throws IOException {
		for(int style = 0; style < GameEngine.MAX_GAMESTYLE; style++) {
			String msg = "rulelist\t" + style;

			for(int i = 0; i < ruleList[style].size(); i++) {
				Object tempObj = ruleList[style].get(i);

				if(tempObj instanceof RuleOptions) {
					RuleOptions rule = (RuleOptions)tempObj;
					msg += "\t" + NetUtil.urlEncode(rule.strRuleName);
				}
			}

			msg += "\n";
			send(client, msg);
		}
		//send(client, "rulelistend\n");
	}

	/**
	 * Get rated-game rule
	 * @param style Style ID
	 * @param name Rule Name
	 * @return Rated-game rule (null if not found)
	 */
	private RuleOptions getRatedRule(int style, String name) {
		for(int i = 0; i < ruleList[style].size(); i++) {
			RuleOptions rule = (RuleOptions)ruleList[style].get(i);

			if(name.equals(rule.strRuleName)) {
				return rule;
			}
		}

		return null;
	}

	/**
	 * Get rated-game rule index
	 * @param style Style ID
	 * @param name Rule Name
	 * @return Index (-1 if not found)
	 */
	private int getRatedRuleIndex(int style, String name) {
		for(int i = 0; i < ruleList[style].size(); i++) {
			RuleOptions rule = (RuleOptions)ruleList[style].get(i);

			if(name.equals(rule.strRuleName)) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * Get new rating
	 * @param playedGames Number of games played by the player
	 * @param myRank Player's rating
	 * @param oppRank Opponent's rating
	 * @param myScore 0:Loss, 1:Win
	 * @return New rating
	 */
	private double rankDelta(int playedGames, double myRank, double oppRank, double myScore) {
		return maxDelta(playedGames) * (myScore - expectedScore(myRank, oppRank));
	}

	/**
	 * Subroutine of rankDelta; Returns expected score.
	 * @param myRank Player's rating
	 * @param oppRank Opponent's rating
	 * @return Expected score
	 */
	private double expectedScore(double myRank, double oppRank) {
		return 1.0 / (1 + Math.pow(10, (oppRank - myRank) / 400.0));
	}

	/**
	 * Subroutine of rankDelta; Returns multiplier of rating change
	 * @param playedGames Number of games played by the player
	 * @return Multiplier of rating change
	 */
	private double maxDelta(int playedGames) {
		return playedGames > ratingProvisionalGames
				? ratingNormalMaxDiff
				: ratingNormalMaxDiff + 400 / (playedGames + 3);
	}

	/**
	 * Write server-status file
	 */
	private void writeServerStatusFile()
	{
		if (!propServer.getProperty("netserver.writestatusfile", false))
				return;

		String status = propServer.getProperty("netserver.statusformat",
				"$observers/$players");

		status = status.replaceAll("\\$version", Float.toString(GameManager.VERSION_MAJOR));
		status = status.replaceAll("\\$observers", Integer.toString(observerList.size()));
		status = status.replaceAll("\\$players", Integer.toString(playerInfoMap.size()));
		status = status.replaceAll("\\$clients", Integer.toString(observerList.size() + playerInfoMap.size()));
		status = status.replaceAll("\\$rooms", Integer.toString(roomInfoList.size()));

		try {
			FileWriter outFile = new FileWriter(propServer.getProperty("netserver.statusfilename", "status.txt"));
			PrintWriter out = new PrintWriter(outFile);
			out.println(status);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}
}
