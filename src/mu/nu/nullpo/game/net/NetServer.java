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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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

import mu.nu.nullpo.game.component.RuleOptions;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.util.CustomProperties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.cacas.java.gnu.tools.Crypt;

/**
 * NullpoMino NetServer
 * <a href="http://hondou.homedns.org/pukiwiki/index.php?JavaSE%20%A5%C1%A5%E3%A5%C3%A5%C8%A5%B7%A5%B9%A5%C6%A5%E0%A4%F2%BA%EE%A4%ED%A4%A6">Source</a>
 */
public class NetServer {
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

	/** Multiplayer leaderboard list. It contains NetPlayerInfo. */
	@SuppressWarnings("rawtypes")
	private static LinkedList[] mpRankingList;

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

		// Fetch port number from config file
		int port = propServer.getProperty("netserver.port", DEFAULT_PORT);
		if(args.length > 0) {
			// If command-line option is used, change port number to the new one
			try {
				port = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {}
		}

		// Load settings
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

		// Run
		new NetServer(port).run();
	}

	/**
	 * Load rated-game rule list
	 */
	@SuppressWarnings("rawtypes")
	private static void loadRuleList() {
		ruleList = new LinkedList[GameEngine.MAX_GAMESTYLE];
		for(int i = 0; i < GameEngine.MAX_GAMESTYLE; i++) {
			ruleList[i] = new LinkedList();
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
						log.debug("{RuleLoad} StyleID:" + style + " RuleFile:" + str);

						FileInputStream in = new FileInputStream(str);
						CustomProperties prop = new CustomProperties();
						prop.load(in);
						in.close();

						RuleOptions rule = new RuleOptions();
						rule.readProperty(prop, 0);

						ruleList[style].add(rule);
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
	 * @param p NetPlayerInfo
	 * @return Index in mpRankingList[style] (-1 if not found)
	 */
	private static int mpRankingIndexOf(int style, NetPlayerInfo p) {
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
			for(int i = 0; i < pInfo.rating.length; i++) {
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
			for(int i = 0; i < pInfo.rating.length; i++) {
				propPlayerData.setProperty("p.rating." + i + "." + pInfo.strName, pInfo.rating[i]);
				propPlayerData.getProperty("p.playCount." + i + "." + pInfo.strName, pInfo.playCount[i]);
				propPlayerData.getProperty("p.winCount." + i + "." + pInfo.strName, pInfo.winCount[i]);
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
		super();
		port = DEFAULT_PORT;
	}

	/**
	 * ポート number設定可能なConstructor
	 * @param port ポート number
	 */
	public NetServer(int port) {
		super();
		this.port = port;
	}

	/*
	 * サーバーの処理
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
	 * 新しいクライアントが接続したとき
	 * @param daemonChannel サーバソケットチャネル
	 */
	private void doAccept(ServerSocketChannel daemonChannel) {
		SocketChannel channel = null;

		try {
			channel = daemonChannel.accept();
			log.info("Accept: " + channel);
			channel.configureBlocking(false);

			// ×× OP_WRITE を監視対象にすると CPU利用率が100%になる ××
			// 書き込むメッセージがあるときだけ, そのチャンネルの OP_WRITE
			// を監視する。
			// channel.register(selector,
			// SelectionKey.OP_READ + SelectionKey.OP_WRITE);

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
		} catch (IOException e) {
			log.info("IOException throwed on doAccept", e);
			logout(channel);
		} catch (Exception e) {
			log.warn("Non-IOException throwed on doAccept", e);
			logout(channel);
		}
	}

	/**
	 * クライアントからメッセージを受信したとき
	 * @param channel ソケットチャネル
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

				// 前回の不完全パケット
				StringBuilder notCompletePacketBuffer = notCompletePacketMap.remove(channel);

				// 受信したメッセージに応じていろいろ処理をする
				StringBuilder packetBuffer = new StringBuilder();
				if(notCompletePacketBuffer != null) packetBuffer.append(notCompletePacketBuffer);
				packetBuffer.append(message);

				int index;
				while((index = packetBuffer.indexOf("\n")) != -1) {
					String msgNow = packetBuffer.substring(0, index);
					processPacket(channel, msgNow);
					packetBuffer = packetBuffer.replace(0, index+1, "");
				}

				// 不完全パケットがある場合
				if(packetBuffer.length() > 0) {
					notCompletePacketMap.put(channel, packetBuffer);
				}
			}
		} catch (IOException e) {
			// Socketが切断された
			log.debug("Socket Disconnected on doRead (IOException)", e);
			logout(channel);
		} catch (Exception e) {
			log.warn("Socket Disconnected on doRead (NOT-IOException)", e);
			logout(channel);
		}
	}

	/**
	 * クライアントにメッセージを送信できるようになったとき
	 * @param channel ソケットチャネル
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
					// bbufをすべてを送信しきれなかったので, 残りをbufferMapに書き戻す
					ByteArrayOutputStream rest = new ByteArrayOutputStream();
					rest.write(bbuf.array(), bbuf.position(), bbuf.remaining());
					bufferMap.put(channel, rest);
					// 宛先チャンネルが Writable になるのを監視し続ける。
					// 宛先チャンネルが切断されたことを検知するために Readable の監視も行う
					channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
				} else {
					// bbufをすべて送信し終わったので, bufferMap今回送信分を削除する
					bufferMap.remove(channel);
					// 宛先チャンネルが Writable になるのを監視するのをやめる
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
	 * クライアントが切断したとき
	 * @param channel ソケットチャネル
	 */
	private void logout(SocketChannel channel) {
		if(channel == null) return;

		try {
			String remoteAddr = channel.socket().getRemoteSocketAddress().toString();
			log.info("Logout: " + remoteAddr);
		} catch (Exception e) {}

		try {
			channel.register(selector, 0);
			channel.finishConnect();
			channel.close();
		} catch (IOException e) {
			log.debug("IOException throwed on logout", e);
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

				LinkedList<NetRoomInfo> deleteList = new LinkedList<NetRoomInfo>();	// 削除 check 予定リスト

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
				log.info("Observer logout (" + channel.toString() + ")");
			}

			if(pInfo != null) {
				broadcastPlayerInfoUpdate(pInfo, "playerlogout");
				pInfo.delete();
			}
			broadcastUserCountToAll();

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
	 * 各種リストの掃除
	 */
	private void cleanup() {
		log.info("Cleanup");

		channelList.clear();
		bufferMap.clear();
		notCompletePacketMap.clear();
		observerList.clear();
		playerInfoMap.clear();
		roomInfoList.clear();

		System.gc();
	}

	/**
	 * クライアントにメッセージを送信(すぐには送信せずBufferに一旦貯められます)
	 * @param client 送信先
	 * @param bytes 送信するメッセージ
	 */
	private void send(SocketChannel client, byte[] bytes) throws IOException {
		if(client == null) throw new NullPointerException("client is null");
		if(bytes == null) throw new NullPointerException("bytes (message to send) is null");

		log.debug("Send: " + client);

		// ×× ここで Channel に write() しちゃダメ ××
		// client.write(ByteBuffer.wrap(bytes));

		// ※ ここでは, Buffer に貯めておいて, Channel が writable
		// ※ になったら, 書き出す。
		ByteArrayOutputStream bout = bufferMap.get(client);
		if (bout == null) {
			bout = new ByteArrayOutputStream();
			bufferMap.put(client, bout);
		}
		bout.write(bytes);

		// 宛先チャンネルが Writable になるのを監視する
		client.register(selector, SelectionKey.OP_WRITE);
	}

	/**
	 * クライアントにメッセージを送信(すぐには送信せずBufferに一旦貯められます)
	 * @param client 送信先
	 * @param msg 送信するメッセージ
	 */
	private void send(SocketChannel client, String msg) throws IOException  {
		send(client, NetUtil.stringToBytes(msg));
	}

	/**
	 * クライアントにメッセージを送信(すぐには送信せずBufferに一旦貯められます)
	 * @param pInfo 送信先Player
	 * @param msg 送信するメッセージ
	 */
	@SuppressWarnings("unused")
	private void send(NetPlayerInfo pInfo, byte[] bytes) throws IOException {
		SocketChannel ch = getSocketChannelByPlayer(pInfo);
		if(ch == null) return;
		send(ch, bytes);
	}

	/**
	 * クライアントにメッセージを送信(すぐには送信せずBufferに一旦貯められます)
	 * @param pInfo 送信先Player
	 * @param msg 送信するメッセージ
	 */
	private void send(NetPlayerInfo pInfo, String msg) throws IOException {
		SocketChannel ch = getSocketChannelByPlayer(pInfo);
		if(ch == null) return;
		send(ch, NetUtil.stringToBytes(msg));
	}

	/**
	 * すべてのPlayerにメッセージ送信
	 * @param msg 送信するメッセージ
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
	 * 特定のルームにいる全員のPlayerにメッセージ送信
	 * @param msg 送信するメッセージ
	 * @param roomID ルームID
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
	 * 特定のルームにいる全員のPlayerにメッセージ送信(送信元Player除く)
	 * @param msg 送信するメッセージ
	 * @param roomID ルームID
	 * @param pInfo 送信元Player
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
	 * すべてのObserverにメッセージ送信
	 * @param msg 送信するメッセージ
	 */
	private void broadcastObserver(String msg) throws IOException {
		for(SocketChannel ch: observerList) {
			send(ch, msg);
		}
	}

	/**
	 * ユーザーcount更新を全員(ObserverとPlayer)に伝える
	 */
	private void broadcastUserCountToAll() throws IOException {
		String msg = "observerupdate\t" + playerInfoMap.size() + "\t" + observerList.size() + "\n";
		broadcast(msg);
		broadcastObserver(msg);
		writeServerStatusFile();
	}

	/**
	 * 指定したPlayerのSocketChannelを取得
	 * @param pInfo Player
	 * @return 指定したPlayerのSocketChannel
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
	 * 受信したメッセージに応じていろいろ処理をする
	 * @param client ソケットチャネル
	 * @param fullMessage 受信したメッセージ
	 * @throws IOException 何かエラーがあったとき
	 */
	private void processPacket(SocketChannel client, String fullMessage) throws IOException {
		String[] message = fullMessage.split("\t");	// タブ区切り
		NetPlayerInfo pInfo = playerInfoMap.get(client);	// Player情報

		// サーバー情報取得
		if(message[0].equals("getinfo")) {
			int loggedInUsersCount = playerInfoMap.size();
			int observerCount = observerList.size();
			send(client, "getinfo\t" + GameManager.getVersionMajor() + "\t" + loggedInUsersCount + "\t" + observerCount + "\n");
			return;
		}
		// 切断
		if(message[0].equals("disconnect")) {
			throw new IOException("Disconnect requested by the client (this is normal)");
		}
		// 接続テスト返答
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
		// Observerログイン
		if(message[0].equals("observerlogin")) {
			//observer\t[VERSION]

			// ログイン済みなら無視
			if(observerList.contains(client)) return;
			if(playerInfoMap.containsKey(client)) return;

			// Version check
			float serverVer = GameManager.getVersionMajor();
			float clientVer = Float.parseFloat(message[1]);
			if(serverVer != clientVer) {
				send(client, "observerloginfail\tDIFFERENT_VERSION\t" + serverVer + "\n");
				logout(client);
				return;
			}

			// ログイン成功
			observerList.add(client);
			send(client, "observerloginsuccess\n");
			broadcastUserCountToAll();

			log.info("New observer has logged in (" + client.toString() + ")");
			return;
		}
		// ログイン
		if(message[0].equals("login")) {
			//login\t[VERSION]\t[NAME]\t[COUNTRY]\t[TEAM]

			// ログイン済みなら無視
			if(observerList.contains(client)) return;
			if(playerInfoMap.containsKey(client)) return;

			// Version check
			float serverVer = GameManager.getVersionMajor();
			float clientVer = Float.parseFloat(message[1]);
			if(serverVer != clientVer) {
				send(client, "loginfail\tDIFFERENT_VERSION\t" + serverVer + "\n");
				logout(client);
				return;
			}

			// トリップ生成
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

			// Player名決定(同じNameの人がいたら後ろにcount字をくっつける)
			if(originalName.length() < 1) originalName = "noname";
			String name = originalName;
			int nameCount = 0;
			while(searchPlayerByName(name) != null) {
				name = originalName + "(" + nameCount + ")";
				nameCount++;
			}

			// 情報取得
			pInfo = new NetPlayerInfo();
			pInfo.strName = name;
			if(message.length > 3) pInfo.strCountry = message[3];
			if(message.length > 4) pInfo.strTeam = NetUtil.urlDecode(message[4]);
			pInfo.uid = playerCount;
			pInfo.connected = true;
			pInfo.isTripUse = isTripUse;
			log.info(pInfo.strName + " has logged in (Host:" + client.socket().getInetAddress().getHostName() + " Team:" + pInfo.strTeam + ")");

			// ホスト名設定
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

			// ログイン成功
			playerInfoMap.put(client, pInfo);
			playerCount++;
			send(client, "loginsuccess\t" + NetUtil.urlEncode(pInfo.strName) + "\t" + pInfo.uid + "\n");

			sendRatedRuleList(client);
			sendPlayerList(client);
			sendRoomList(client);

			broadcastPlayerInfoUpdate(pInfo, "playernew");
			broadcastUserCountToAll();
			return;
		}
		// ルール data登録(クライアント→サーバー)
		if(message[0].equals("ruledata")) {
			//ruledata\t[ADLER32CHECKSUM]\t[RULEDATA]

			if(pInfo != null) {
				String strData = message[2];

				// checkサム計算
				Adler32 checksumObj = new Adler32();
				checksumObj.update(NetUtil.stringToBytes(strData));
				long sChecksum = checksumObj.getValue();
				long cChecksum = Long.parseLong(message[1]);

				// 一致
				if(sChecksum == cChecksum) {
					String strRuleData = NetUtil.decompressString(strData);

					CustomProperties prop = new CustomProperties();
					prop.decode(strRuleData);
					pInfo.ruleOpt = new RuleOptions();
					pInfo.ruleOpt.readProperty(prop, 0);
					send(client, "ruledatasuccess\n");
				}
				// 不一致
				else {
					send(client, "ruledatafail\t" + sChecksum + "\n");
				}
			}
			return;
		}
		// ルール data送信(サーバー→クライアント)
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

					// checkサム計算
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

			if(pInfo != null) {
				int style = Integer.parseInt(message[1]);
				int myRank = mpRankingIndexOf(style, pInfo);

				String strPData = "";
				for(int i = 0; i < mpRankingList[style].size(); i++) {
					NetPlayerInfo p = (NetPlayerInfo)mpRankingList[style].get(i);
					strPData += (i) + ";" + NetUtil.urlEncode(p.strName) + ";" +
								p.rating[style] + ";" + p.playCount[style] + ";" + p.winCount[style] + "\t";
				}
				if(myRank == -1) {
					NetPlayerInfo p = pInfo;
					strPData += (-1) + ";" + NetUtil.urlEncode(p.strName) + ";" +
								p.rating[style] + ";" + p.playCount[style] + ";" + p.winCount[style] + "\t";
				}
				String strPDataC = NetUtil.compressString(strPData);

				String strMsg = "mpranking\t" + style + "\t" + myRank + "\t" + strPDataC + "\n";
				send(pInfo, strMsg);
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
		// ルーム作成
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
					roomInfo.ruleOpt = new RuleOptions(getRatedRule(0, roomInfo.ruleName));
					roomInfo.ruleLock = true;
					roomInfo.rated = true;
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
		// ルーム入室(ルーム numberを-1にするとロビーに戻る)
		if(message[0].equals("roomjoin")) {
			//roomjoin\t[ROOMID]\t[WATCH]

			if(pInfo != null) {
				int roomID = Integer.parseInt(message[1]);
				boolean watch = Boolean.parseBoolean(message[2]);
				NetRoomInfo prevRoom = getRoomInfo(pInfo.roomID);
				NetRoomInfo newRoom = getRoomInfo(roomID);

				if(roomID < 0) {
					// ロビーに戻る
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
					// 入室
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
					// ルームが存在しない
					send(client, "roomjoinfail\n");
				}
			}
			return;
		}
		// チーム変更
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
		// 参戦状態の変更
		if(message[0].equals("changestatus")) {
			//changestatus\t[WATCH]
			if((pInfo != null) && (!pInfo.playing) && (pInfo.roomID != -1)) {
				NetRoomInfo roomInfo = getRoomInfo(pInfo.roomID);
				boolean watch = Boolean.parseBoolean(message[1]);

				if(!roomInfo.singleplayer) {
					if(watch) {
						// 観戦のみ
						int prevSeatID = pInfo.seatID;
						roomInfo.exitSeat(pInfo);
						roomInfo.exitQueue(pInfo);
						pInfo.ready = false;
						pInfo.seatID = -1;
						pInfo.queueID = -1;
						//send(client, "changestatus\twatchonly\t-1\n");
						broadcast("changestatus\twatchonly\t" + pInfo.uid + "\t" + NetUtil.urlEncode(pInfo.strName) + "\t" + prevSeatID + "\n",
								  pInfo.roomID);

						joinAllQueuePlayers(roomInfo);	// 順番待ちの人を入れる
					} else {
						// 参戦
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
		// 準備完了状態の変更
		if(message[0].equals("ready")) {
			//ready\t[STATE]
			if(pInfo != null) {
				NetRoomInfo roomInfo = getRoomInfo(pInfo.roomID);
				int seat = roomInfo.getPlayerSeatNumber(pInfo);

				if((seat != -1) && (!roomInfo.singleplayer)) {
					pInfo.ready = Boolean.parseBoolean(message[1]);
					broadcastPlayerInfoUpdate(pInfo);

					if(!pInfo.ready) roomInfo.isSomeoneCancelled = true;

					// 全員が準備完了状態になったら開始
					if(!gameStartIfPossible(roomInfo)) {
						autoStartTimerCheck(roomInfo);
					}
				}
			}
		}
		// 自動スタート
		if(message[0].equals("autostart")) {
			if(pInfo != null) {
				NetRoomInfo roomInfo = getRoomInfo(pInfo.roomID);
				int seat = roomInfo.getPlayerSeatNumber(pInfo);

				if((seat != -1) && (roomInfo.autoStartActive) && (!roomInfo.singleplayer)) {
					if(roomInfo.autoStartTNET2) {
						// 準備完了でないPlayerを観戦状態にする
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

						joinAllQueuePlayers(roomInfo);	// 順番待ちの人を入れる
					}

					gameStart(roomInfo);
				}
			}
		}
		// 死亡
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
		// ゲーム結果
		if(message[0].equals("gstat")) {
			if((pInfo != null) && (pInfo.roomID != -1) && (pInfo.seatID != -1)) {
				NetRoomInfo roomInfo = getRoomInfo(pInfo.roomID);

				String msg = "gstat\t" + pInfo.uid + "\t" + pInfo.seatID + "\t" + NetUtil.urlEncode(pInfo.strName) + "\t";
				for(int i = 1; i < message.length; i++) {
					msg += message[i];
					if(i < message.length - 1) msg += "\t";
				}
				msg += "\n";

				broadcast(msg, roomInfo.roomID);
			}
		}
		// ゲーム関連メッセージ(可変)
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
	}

	/**
	 * 指定されたIDのルーム情報を返す
	 * @param roomID ルームID
	 * @return ルーム情報(存在しないならnull)
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
	 * ルームリストを送る
	 * @param client 送信先
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
	 * ルームが空だったら削除
	 * @param roomInfo ルーム情報
	 * @return 削除されたらtrue
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
	 * Automatically start timerの開始と停止
	 * @param roomInfo ルーム情報
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
	 * 全員が準備完了状態か check し, 条件を満たしていればゲームを開始する
	 * @param roomInfo ルーム情報
	 * @return Start gameしたらtrue, しなかったらfalse
	 */
	private boolean gameStartIfPossible(NetRoomInfo roomInfo) throws IOException {
		// 全員が準備完了状態になったら
		if(roomInfo.getHowManyPlayersReady() == roomInfo.getNumberOfPlayerSeated()) {
			gameStart(roomInfo);
			return true;
		}

		return false;
	}

	/**
	 * ゲームを開始する(無条件で)
	 * @param roomInfo ルーム情報
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

				if(roomInfo.rated) {
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
	 * game finishedかどうか check し, 終了条件を満たしていればルーム内の全員に通知する
	 * @param roomInfo ルーム情報
	 * @return game finishedしたらtrue, 終了前・すでに終了後ならfalse
	 */
	private boolean gameFinished(NetRoomInfo roomInfo) throws IOException {
		int startPlayers = roomInfo.startPlayers;
		int nowPlaying = roomInfo.getHowManyPlayersPlaying();
		boolean isTeamWin = roomInfo.isTeamWin();

		if( (roomInfo != null) && (roomInfo.playing) && ( (nowPlaying < 1) || ((startPlayers >= 2) && (nowPlaying < 2)) || (isTeamWin) ) ) {
			// game finished通知
			NetPlayerInfo winner = roomInfo.getWinner();
			String msg = "finish\t";

			if(isTeamWin) {
				String teamName = roomInfo.getWinnerTeam();
				if(teamName == null) teamName = "";
				msg += -1 + "\t" + -1 + "\t" + NetUtil.urlEncode(teamName) + "\t" + isTeamWin;

				// Playerのプレイ中 flagを解除
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
				msg += -1 + "\t" + -1 + "\t" + "" + "\t" + isTeamWin;
			}
			msg += "\n";
			broadcast(msg, roomInfo.roomID);

			// ルーム状態更新
			roomInfo.playing = false;
			roomInfo.autoStartActive = false;
			broadcastRoomInfoUpdate(roomInfo);

			return true;
		}

		return false;
	}

	/**
	 * ルーム情報更新通知を全員に送る(コマンドはroomupdate)
	 * @param roomInfo ルーム情報
	 */
	private void broadcastRoomInfoUpdate(NetRoomInfo roomInfo) throws IOException {
		broadcastRoomInfoUpdate(roomInfo, "roomupdate");
	}

	/**
	 * ルーム情報更新通知を全員に送る
	 * @param roomInfo ルーム情報
	 * @param command コマンド
	 */
	private void broadcastRoomInfoUpdate(NetRoomInfo roomInfo, String command) throws IOException {
		roomInfo.updatePlayerCount();
		String msg = command + "\t";
		msg += roomInfo.exportString();
		msg += "\n";
		broadcast(msg);
	}

	/**
	 * Playerリストを送る
	 * @param client 送信先
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
	 * Player情報更新通知を全員に送る(コマンドはplayerupdate)
	 * @param pInfo Player情報
	 */
	private void broadcastPlayerInfoUpdate(NetPlayerInfo pInfo) throws IOException  {
		broadcastPlayerInfoUpdate(pInfo, "playerupdate");
	}

	/**
	 * Player情報更新通知を全員に送る
	 * @param pInfo Player情報
	 * @param command コマンド
	 */
	private void broadcastPlayerInfoUpdate(NetPlayerInfo pInfo, String command) throws IOException {
		String msg = command + "\t";
		msg += pInfo.exportString();
		msg += "\n";
		broadcast(msg);
	}

	/**
	 * 指定したNameのPlayerを探す
	 * @param name Name
	 * @return 指定したNameのPlayer情報(いなかったらnull)
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
	 * 指定したIDのPlayerを探す
	 * @param uid ID
	 * @return 指定したIDのPlayer情報(いなかったらnull)
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
	 * 順番待ちのPlayerをすべて空席に参加させる
	 * @param roomInfo ルーム情報
	 * @return 席に入れたNumber of players
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
	 * Player死亡時の処理
	 * @param pInfo Player
	 */
	private void playerDead(NetPlayerInfo pInfo) throws IOException {
		playerDead(pInfo, null);
	}

	/**
	 * Player死亡時の処理
	 * @param pInfo 被害者
	 * @param pKOInfo 加害者(null可)
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
	 * Sets a ban.
	 * @param client The remote address to ban.
	 * @param banLength The length of the ban.
	 */
	private void ban(SocketChannel client, int banLength) {
		String remoteAddr = client.socket().getRemoteSocketAddress().toString();
		remoteAddr = remoteAddr.substring(0,remoteAddr.indexOf(':'));

		banList.add(new NetServerBan(remoteAddr, banLength));
		logout(client);
		log.info("Banned player: "+remoteAddr);
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
}
