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

import java.io.IOException;
import java.util.LinkedList;
import java.util.Locale;

import mu.nu.nullpo.game.play.GameManager;

import org.apache.log4j.Logger;

/**
 * クライアント(Player用)
 */
public class NetPlayerClient extends NetBaseClient {
	/** Log */
	static final Logger log = Logger.getLogger(NetPlayerClient.class);

	/** Player情報 */
	protected LinkedList<NetPlayerInfo> playerInfoList = new LinkedList<NetPlayerInfo>();

	/** ルーム情報 */
	protected LinkedList<NetRoomInfo> roomInfoList = new LinkedList<NetRoomInfo>();

	/** 自分のPlayer名 */
	protected String playerName;

	/** 自分のTeam name */
	protected String playerTeam;

	/** 自分のPlayer識別 number */
	protected int playerUID;

	/** サーバーVersion */
	protected float serverVersion = -1f;

	/** Number of players */
	protected int playerCount = -1;

	/** Observercount */
	protected int observerCount = -1;

	/**
	 * Default constructor
	 */
	public NetPlayerClient() {
		super();
	}

	/**
	 * Constructor
	 * @param host 接続先ホスト
	 */
	public NetPlayerClient(String host) {
		super(host);
	}

	/**
	 * Constructor
	 * @param host 接続先ホスト
	 * @param port 接続先ポート number
	 */
	public NetPlayerClient(String host, int port) {
		super(host, port);
	}

	/**
	 * Constructor
	 * @param host 接続先ホスト
	 * @param port 接続先ポート number
	 * @param name PlayerのName
	 */
	public NetPlayerClient(String host, int port, String name) {
		super();
		this.host = host;
		this.port = port;
		this.playerName = name;
		this.playerTeam = "";
	}

	/**
	 * Constructor
	 * @param host 接続先ホスト
	 * @param port 接続先ポート number
	 * @param name PlayerのName
	 * @param team 所属するTeam name
	 */
	public NetPlayerClient(String host, int port, String name, String team) {
		super();
		this.host = host;
		this.port = port;
		this.playerName = name;
		this.playerTeam = team;
	}

	/*
	 * 受信したメッセージに応じていろいろ処理をする
	 */
	@Override
	protected void processPacket(String fullMessage) throws IOException {
		String[] message = fullMessage.split("\t");	// タブ区切り

		// 接続完了
		if(message[0].equals("welcome")) {
			//welcome\t[VERSION]\t[PLAYERS]\t[OBSERVERS]\t[VERSION MINOR]\t[VERSION STRING]\t[PING INTERVAL]\t[DEV BUILD]
			playerCount = Integer.parseInt(message[2]);
			observerCount = Integer.parseInt(message[3]);

			long pingInterval = (message.length > 6) ? Long.parseLong(message[6]) : PING_INTERVAL;
			if(pingInterval != PING_INTERVAL) {
				startPingTask(pingInterval);
			}

			send("login\t" + GameManager.getVersionMajor() + "\t" + NetUtil.urlEncode(playerName) + "\t" + Locale.getDefault().getCountry() + "\t" +
				 NetUtil.urlEncode(playerTeam) + "\t" + GameManager.getVersionMinor() + "\t" + GameManager.isDevBuild() + "\n");
		}
		// 人count更新
		if(message[0].equals("observerupdate")) {
			//observerupdate\t[PLAYERS]\t[OBSERVERS]
			playerCount = Integer.parseInt(message[1]);
			observerCount = Integer.parseInt(message[2]);
		}
		// ログイン成功
		if(message[0].equals("loginsuccess")) {
			//loginsuccess\t[NAME]\t[UID]
			playerName = NetUtil.urlDecode(message[1]);
			playerUID = Integer.parseInt(message[2]);
		}
		// Playerリスト
		if(message[0].equals("playerlist")) {
			//playerlist\t[PLAYERS]\t[PLAYERDATA...]

			int numPlayers = Integer.parseInt(message[1]);

			for(int i = 0; i < numPlayers; i++) {
				NetPlayerInfo p = new NetPlayerInfo(message[2 + i]);
				playerInfoList.add(p);
			}
		}
		// Player情報更新/新規Player
		if(message[0].equals("playerupdate") || message[0].equals("playernew")) {
			//playerupdate\t[PLAYERDATA]

			NetPlayerInfo p = new NetPlayerInfo(message[1]);
			NetPlayerInfo p2 = getPlayerInfoByUID(p.uid);

			if(p2 == null) {
				playerInfoList.add(p);
			} else {
				int index = playerInfoList.indexOf(p2);
				playerInfoList.set(index, p);
			}
		}
		// Player切断
		if(message[0].equals("playerlogout")) {
			//playerlogout\t[PLAYERDATA]

			NetPlayerInfo p = new NetPlayerInfo(message[1]);
			NetPlayerInfo p2 = getPlayerInfoByUID(p.uid);

			if(p2 != null) {
				playerInfoList.remove(p2);
				p2.delete();
			}
		}
		// ルームリスト
		if(message[0].equals("roomlist")) {
			//roomlist\t[ROOMS]\t[ROOMDATA...]

			int numRooms = Integer.parseInt(message[1]);

			for(int i = 0; i < numRooms; i++) {
				NetRoomInfo r = new NetRoomInfo(message[2 + i]);
				roomInfoList.add(r);
			}
		}
		// ルーム情報更新/新規ルーム出現
		if(message[0].equals("roomupdate") || message[0].equals("roomcreate")) {
			//roomupdate\t[ROOMDATA]

			NetRoomInfo r = new NetRoomInfo(message[1]);
			NetRoomInfo r2 = getRoomInfo(r.roomID);

			if(r2 == null) {
				roomInfoList.add(r);
			} else {
				int index = roomInfoList.indexOf(r2);
				roomInfoList.set(index, r);
			}
		}
		// ルーム消滅
		if(message[0].equals("roomdelete")) {
			//roomdelete\t[ROOMDATA]

			NetRoomInfo r = new NetRoomInfo(message[1]);
			NetRoomInfo r2 = getRoomInfo(r.roomID);

			if(r2 != null) {
				roomInfoList.remove(r2);
				r2.delete();
			}
		}
		// 参戦状態変更
		if(message[0].equals("changestatus")) {
			NetPlayerInfo p = getPlayerInfoByUID(Integer.parseInt(message[2]));

			if(p != null) {
				if(message[1].equals("watchonly")) {
					p.seatID = -1;
					p.queueID = -1;
				} else if(message[1].equals("joinqueue")) {
					p.seatID = -1;
					p.queueID = Integer.parseInt(message[4]);
				} else if(message[1].equals("joinseat")) {
					p.seatID = Integer.parseInt(message[4]);
					p.queueID = -1;
				}
			}
		}

		// Listener呼び出し
		super.processPacket(fullMessage);
	}

	/**
	 * 指定されたIDのルーム情報を返す
	 * @param roomID ルームID
	 * @return ルーム情報(存在しないならnull)
	 */
	public NetRoomInfo getRoomInfo(int roomID) {
		if(roomID < 0) return null;

		for(NetRoomInfo roomInfo: roomInfoList) {
			if(roomID == roomInfo.roomID) {
				return roomInfo;
			}
		}

		return null;
	}

	/**
	 * 指定したNameのPlayerを取得
	 * @param name Name
	 * @return 指定したNameのPlayer情報(いなかったらnull)
	 */
	public NetPlayerInfo getPlayerInfoByName(String name) {
		for(NetPlayerInfo pInfo: playerInfoList) {
			if((pInfo != null) && (pInfo.strName == name)) {
				return pInfo;
			}
		}
		return null;
	}

	/**
	 * 指定したIDのPlayerを取得
	 * @param uid ID
	 * @return 指定したIDのPlayer情報(いなかったらnull)
	 */
	public NetPlayerInfo getPlayerInfoByUID(int uid) {
		for(NetPlayerInfo pInfo: playerInfoList) {
			if((pInfo != null) && (pInfo.uid == uid)) {
				return pInfo;
			}
		}
		return null;
	}

	/**
	 * @return Player情報のリスト
	 */
	public LinkedList<NetPlayerInfo> getPlayerInfoList() {
		return playerInfoList;
	}

	/**
	 * @return ルーム情報のリスト
	 */
	public LinkedList<NetRoomInfo> getRoomInfoList() {
		return roomInfoList;
	}

	/**
	 * @return Current Player名
	 */
	public String getPlayerName() {
		return playerName;
	}

	/**
	 * @return Current Playerの識別 number
	 */
	public int getPlayerUID() {
		return playerUID;
	}

	/**
	 * 自分自身の情報を取得
	 * @return 自分自身の情報
	 */
	public NetPlayerInfo getYourPlayerInfo() {
		return getPlayerInfoByUID(playerUID);
	}

	/**
	 * @return Current room ID
	 */
	public int getCurrentRoomID() {
		try {
			return getYourPlayerInfo().roomID;
		} catch (NullPointerException e) {}
		return -1;
	}

	/**
	 * @return Current room info
	 */
	public NetRoomInfo getCurrentRoomInfo() {
		return getRoomInfo(getCurrentRoomID());
	}

	/**
	 * @return サーバーVersion
	 */
	public float getServerVersion() {
		return serverVersion;
	}

	/**
	 * @return Number of players
	 */
	public int getPlayerCount() {
		return playerCount;
	}

	/**
	 * @return Observercount
	 */
	public int getObserverCount() {
		return observerCount;
	}
}
