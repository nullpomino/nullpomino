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
package mu.nu.nullpo.game.subsystem.mode;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Random;

import mu.nu.nullpo.game.component.BGMStatus;
import mu.nu.nullpo.game.component.Block;
import mu.nu.nullpo.game.component.Controller;
import mu.nu.nullpo.game.component.Field;
import mu.nu.nullpo.game.component.Piece;
import mu.nu.nullpo.game.event.EventReceiver;
import mu.nu.nullpo.game.net.NetPlayerClient;
import mu.nu.nullpo.game.net.NetPlayerInfo;
import mu.nu.nullpo.game.net.NetRoomInfo;
import mu.nu.nullpo.game.net.NetUtil;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.game.subsystem.wallkick.Wallkick;
import mu.nu.nullpo.gui.net.NetLobbyFrame;
import mu.nu.nullpo.util.GeneralUtil;
import net.omegaboshi.nullpomino.game.subsystem.randomizer.Randomizer;

import org.apache.log4j.Logger;

/**
 * NET-VS-BATTLE Mode
 */
public class NetVSBattleMode extends NetDummyMode {
	/** Log */
	static final Logger log = Logger.getLogger(NetVSBattleMode.class);

	/** Maximum number of players */
	private static final int MAX_PLAYERS = 6;

	/** Most recent scoring event typeの定count */
	private static final int EVENT_NONE = 0,
							 EVENT_SINGLE = 1,
							 EVENT_DOUBLE = 2,
							 EVENT_TRIPLE = 3,
							 EVENT_FOUR = 4,
							 EVENT_TSPIN_SINGLE_MINI = 5,
							 EVENT_TSPIN_SINGLE = 6,
							 EVENT_TSPIN_DOUBLE = 7,
							 EVENT_TSPIN_TRIPLE = 8,
							 EVENT_TSPIN_DOUBLE_MINI = 9,
							 EVENT_TSPIN_EZ = 10;

	/** Type of attack performed */
	private static final int ATTACK_CATEGORY_NORMAL = 0,
							 ATTACK_CATEGORY_B2B = 1,
							 ATTACK_CATEGORY_SPIN = 2,
							 ATTACK_CATEGORY_COMBO = 3,
							 ATTACK_CATEGORY_BRAVO = 4,
							 ATTACK_CATEGORY_GEM = 5,
							 ATTACK_CATEGORIES = 6;

	/** ゲーム席とゲーム画面上でのfield numberの対応表 */
	private static final int[][] GAME_SEAT_NUMBERS =
	{
		{0,1,2,3,4,5},
		{1,0,2,3,4,5},
		{1,2,0,3,4,5},
		{1,2,3,0,4,5},
		{1,2,3,4,0,5},
		{1,2,3,4,5,0},
	};

	/** Each player's garbage block color */
	private static final int[] PLAYER_COLOR_BLOCK = {
		Block.BLOCK_COLOR_RED, Block.BLOCK_COLOR_BLUE, Block.BLOCK_COLOR_GREEN,
		Block.BLOCK_COLOR_YELLOW, Block.BLOCK_COLOR_PURPLE, Block.BLOCK_COLOR_CYAN
	};

	/** Each player's frame color */
	private static final int[] PLAYER_COLOR_FRAME = {
		GameEngine.FRAME_COLOR_RED, GameEngine.FRAME_COLOR_BLUE, GameEngine.FRAME_COLOR_GREEN,
		GameEngine.FRAME_COLOR_YELLOW, GameEngine.FRAME_COLOR_PURPLE, GameEngine.FRAME_COLOR_CYAN
	};

	/** 操作中Blockが強制固定されるまでの time */
	private static final int PIECE_AUTO_LOCK_TIME = 60 * 60;

	/** 攻撃力 table(旧T-Spin用) */
	private static final int[][] LINE_ATTACK_TABLE =
	{
		// 1-2人, 3人, 4人, 5人, 6人
		{0, 0, 0, 0, 0},	// Single
		{1, 1, 0, 0, 0},	// Double
		{2, 2, 1, 1, 1},	// Triple
		{4, 3, 2, 2, 2},	// Four
		{1, 1, 0, 0, 0},	// T-Mini-S
		{2, 2, 1, 1, 1},	// T-Single
		{4, 3, 2, 2, 2},	// T-Double
		{6, 4, 3, 3, 3},	// T-Triple
		{4, 3, 2, 2, 2},	// T-Mini-D
		{1, 1, 0, 0, 0},	// EZ-T
	};

	/** 攻撃力 table(新スピン用) */
	private static final int[][] LINE_ATTACK_TABLE_ALLSPIN =
	{
		// 1-2人, 3人, 4人, 5人, 6人
		{0, 0, 0, 0, 0},	// Single
		{1, 1, 0, 0, 0},	// Double
		{2, 2, 1, 1, 1},	// Triple
		{4, 3, 2, 2, 2},	// Four
		{0, 0, 0, 0, 0},	// T-Mini-S
		{2, 2, 1, 1, 1},	// T-Single
		{4, 3, 2, 2, 2},	// T-Double
		{6, 4, 3, 3, 3},	// T-Triple
		{3, 2, 1, 1, 1},	// T-Mini-D
		{0,	0, 0, 0, 0},	// EZ-T
	};

	/** 攻撃力 table参照用のindex number */
	private static final int LINE_ATTACK_INDEX_SINGLE = 0,
							 LINE_ATTACK_INDEX_DOUBLE = 1,
							 LINE_ATTACK_INDEX_TRIPLE = 2,
							 LINE_ATTACK_INDEX_FOUR = 3,
							 LINE_ATTACK_INDEX_TMINI = 4,
							 LINE_ATTACK_INDEX_TSINGLE = 5,
							 LINE_ATTACK_INDEX_TDOUBLE = 6,
							 LINE_ATTACK_INDEX_TTRIPLE = 7,
							 LINE_ATTACK_INDEX_TMINI_D = 8,
							 LINE_ATTACK_INDEX_EZ_T = 9;

	/** Comboの攻撃力 */
	private static final int[][] COMBO_ATTACK_TABLE = {
		{0,0,1,1,2,2,3,3,4,4,4,5}, // 1-2人
		{0,0,1,1,1,2,2,3,3,4,4,4}, // 3人
		{0,0,0,1,1,1,2,2,3,3,4,4}, // 4人
		{0,0,0,1,1,1,1,2,2,3,3,4}, // 5人
		{0,0,0,0,1,1,1,1,2,2,3,3}, // 6人
	};

	private static int GARBAGE_DENOMINATOR = 60; // can be divided by 2,3,4,5

	/** Drawing and event handling EventReceiver */
	private EventReceiver receiver;

	/** ルームID */
	private int currentRoomID;

	/** Current ルーム情報 */
	private NetRoomInfo currentRoomInfo;

	/** ルール固定部屋ならtrue */
	private boolean rulelockFlag;

	/** 3人以上生きている場合に攻撃力を減らす */
	private boolean reduceLineSend;

	/** 新しい断片的garbage blockシステムを使う */
	private boolean useFractionalGarbage;

	private int garbagePercent;

	private boolean garbageChangePerAttack;
	
	private boolean divideChangeRateByPlayers;

	private int lastHole = -1;

	//private boolean useTankMode = false;  
	
	/** Hurryup開始までの秒count(-1でHurryupなし) */
	private int hurryupSeconds;

	/** Hurryup後に何回Blockを置くたびに床をせり上げるか */
	private int hurryupInterval;

	/** Hurryup始まったらtrue */
	private boolean hurryupStarted;

	/** "HURRY UP!"表示の残り time */
	private int hurryupShowFrames;

	/** 自分のゲーム席の number(-1:観戦中) */
	private int playerSeatNumber;

	/** 自分が部屋に入ってから行われたゲームのcount */
	private int numGames;

	/** 自分の勝利count */
	private int numWins;

	/** 合計Number of players(間に挟まれているnull席はカウントしない) */
	private int numPlayers;

	/** 観戦者のcount */
	private int numSpectators;

	/** ゲームが始まったあとの合計Number of players(間に挟まれているnull席はカウントしない) */
	private int numNowPlayers;

	/** この部屋のMaximum人count */
	private int numMaxPlayers;

	/** まだ生きている人count */
	private int numAlivePlayers;

	/** 各fieldのゲーム席の number */
	private int[] allPlayerSeatNumbers;

	/** Playerが存在するfieldならtrue */
	private boolean[] isPlayerExist;

	/** 準備完了状態ならtrue */
	private boolean[] isReady;

	/** 死亡 flag */
	private boolean[] isDead;

	/** 順位 */
	private int[] playerPlace;

	/** 自分がKOしたPlayerはtrue */
	private boolean[] playerKObyYou;

	/** 使用しているスキン */
	private int[] playerSkin;

	/** PlayerのName */
	private String[] playerNames;

	/** Team name */
	private String[] playerTeams;

//	private boolean[] playerTeamsIsTank;
//
//	private boolean isTank;

	/** ゲームが続いてる間true, 開始前や全員完全に終わるとfalse */
	private boolean isNetGameActive;

	/** 全員完全に終わるとtrue(開始前はfalse) */
	private boolean isNetGameFinished;

	/** 進行中の部屋に入った直後はtrue */
	private boolean isNewcomer;

	/** OK表示切り替え直後, 変更が確定するまでtrue */
	private boolean isReadyChangePending;

	/** 練習Mode ならtrue */
	private boolean isPractice;

	/** Automatic start timer is enabled */
	private boolean autoStartActive;

	/** Time left until automatic start */
	private int autoStartTimer;

	/** 経過 timeカウント is enabled */
	private boolean netPlayTimerActive;

	/** 経過 time */
	private int netPlayTimer;

	/** 操作中Blockを動かしている time */
	private int pieceMoveTimer;

	/** 前回の操作中Block typeや位置など */
	private int prevPieceID, prevPieceX, prevPieceY, prevPieceDir;

	/** Time to display the most recent increase in score */
	private int[] scgettime;

	/** Most recent scoring event type */
	private int[] lastevent;

	/** Most recent scoring eventでB2Bだったらtrue */
	private boolean[] lastb2b;

	/** Most recent scoring eventでのCombocount */
	private int[] lastcombo;

	/** Most recent scoring eventでのピースID */
	private int[] lastpiece;

	/** 送ったgarbage blockのcount */
	private int[] garbageSent;

	/** 溜まっているgarbage blockのcount */
	private int[] garbage;

	/** 敵から送られてきたgarbage blockのリスト */
	private LinkedList<GarbageEntry> garbageEntries;

	/** APL (Attack Per Line) */
	private float playerAPL;

	/** APM (Attack Per Minute) */
	private float playerAPM;

	/** Hurryup後にBlockを置いた count */
	private int hurryupCount;

	/** Map number to use */
	private int mapNo;

	/** Practice mode map選択用乱count */
	private Random randMap;

	/** Practice mode last used map number */
	private int mapPreviousPracticeMap;

	/** 最後に攻撃してきた相手のPlayer number */
	private int lastAttackerUID;

	/** KO count */
	private int currentKO;

	/** true if can exit from practice game */
	private boolean isPracticeExitAllowed;

	/**
	 * ゲーム席 numberを元にfield numberを返す
	 * @param seat ゲーム席 number
	 * @return 対応するfield number
	 */
	private int getPlayerIDbySeatID(int seat) {
		int myseat = playerSeatNumber;
		if(myseat < 0) myseat = 0;
		return GAME_SEAT_NUMBERS[myseat][seat];
	}

	/**
	 * Player存在 flagと人countを更新
	 */
	private void updatePlayerExist() {
		numPlayers = 0;
		numSpectators = 0;

		for(int i = 0; i < MAX_PLAYERS; i++) {
			isPlayerExist[i] = false;
			isReady[i] = false;
			allPlayerSeatNumbers[i] = -1;
			owner.engine[i].framecolor = GameEngine.FRAME_COLOR_GRAY;
		}

		if((currentRoomID != -1) && (netLobby != null)) {
			for(NetPlayerInfo pInfo: netLobby.updateSameRoomPlayerInfoList()) {
				if(pInfo.roomID == currentRoomID) {
					if(pInfo.seatID != -1) {
						int playerID = getPlayerIDbySeatID(pInfo.seatID);
						isPlayerExist[playerID] = true;
						isReady[playerID] = pInfo.ready;
						allPlayerSeatNumbers[playerID] = pInfo.seatID;
						numPlayers++;

						if(pInfo.seatID < PLAYER_COLOR_FRAME.length) {
							owner.engine[playerID].framecolor = PLAYER_COLOR_FRAME[pInfo.seatID];
						}
					} else {
						numSpectators++;
					}
				}
			}
		}
	}

	/**
	 * 生き残っているチームcountを返す(チーム無しPlayerも1つのチームとcountえる)
	 * @return 生き残っているチームcount
	 */
	private int getNumberOfTeamsAlive() {
		LinkedList<String> listTeamName = new LinkedList<String>();
		int noTeamCount = 0;

		for(int i = 0; i < MAX_PLAYERS; i++) {
			if(isPlayerExist[i] && !isDead[i] && owner.engine[i].gameActive) {
				if(playerTeams[i].length() > 0) {
					if(!listTeamName.contains(playerTeams[i])) {
						listTeamName.add(playerTeams[i]);
					}
				} else {
					noTeamCount++;
				}
			}
		}

		return noTeamCount + listTeamName.size();
	}

	/**
	 * 今この部屋で参戦状態のNumber of playersを返す
	 * @return 参戦状態のNumber of players
	 */
	/*
	private int getCurrentNumberOfPlayers() {
		int count = 0;
		for(int i = 0; i < MAX_PLAYERS; i++) {
			if(isPlayerExist[i]) count++;
		}
		return count;
	}
	*/

	/*
	 * Mode name
	 */
	@Override
	public String getName() {
		return "NET-VS-BATTLE";
	}

	/*
	 * Maximum人count
	 */
	@Override
	public int getPlayers() {
		return MAX_PLAYERS;
	}

	/*
	 * ネットプレイ
	 */
	@Override
	public boolean isNetplayMode() {
		return true;
	}

	/*
	 * Mode Initialization
	 */
	@Override
	public void modeInit(GameManager manager) {
		super.modeInit(manager);
		receiver = owner.receiver;
		currentRoomID = -1;
		playerSeatNumber = -1;
		numGames = 0;
		numWins = 0;
		numPlayers = 0;
		numSpectators = 0;
		numNowPlayers = 0;
		numMaxPlayers = 0;
		autoStartActive = false;
		autoStartTimer = 0;
		garbagePercent = 100;
		garbageChangePerAttack = true;
		divideChangeRateByPlayers = false;
		//useTankMode = false;
		isReady = new boolean[MAX_PLAYERS];
		playerNames = new String[MAX_PLAYERS];
		playerTeams = new String[MAX_PLAYERS];
//		playerTeamsIsTank = new boolean[MAX_PLAYERS];
		scgettime = new int[MAX_PLAYERS];
		lastevent = new int[MAX_PLAYERS];
		lastb2b = new boolean[MAX_PLAYERS];
		lastcombo = new int[MAX_PLAYERS];
		lastpiece = new int[MAX_PLAYERS];
		garbageSent = new int[MAX_PLAYERS];
		garbage = new int[MAX_PLAYERS];
		mapPreviousPracticeMap = -1;
		playerSkin = new int[MAX_PLAYERS];
		for(int i = 0; i < MAX_PLAYERS; i++) playerSkin[i] = -1;
		resetFlags();
	}

	/**
	 * いろいろリセット
	 */
	private void resetFlags() {
//		isTank = false;
		isPractice = false;
		allPlayerSeatNumbers = new int[MAX_PLAYERS];
		isPlayerExist = new boolean[MAX_PLAYERS];
		isDead = new boolean[MAX_PLAYERS];
		playerPlace = new int[MAX_PLAYERS];
		playerKObyYou = new boolean[MAX_PLAYERS];
		isNetGameActive = false;
		isNetGameFinished = false;
		isNewcomer = false;
		isReadyChangePending = false;
		netPlayTimerActive = false;
		netPlayTimer = 0;
		lastAttackerUID = -1;
		currentKO = 0;
	}

	/**
	 * When you join the room
	 * @param lobby NetLobbyFrame
	 * @param client NetPlayerClient
	 * @param roomInfo NetRoomInfo
	 */
	protected void netOnJoin(NetLobbyFrame lobby, NetPlayerClient client, NetRoomInfo roomInfo) {
		log.debug("onJoin on NetVSBattleMode");

		resetFlags();
		owner.reset();

		isReady = new boolean[MAX_PLAYERS];
		if(currentRoomInfo != null) {
			currentRoomInfo.delete();
			currentRoomInfo = null;
		}

		playerSeatNumber = client.getYourPlayerInfo().seatID;
		currentRoomID = client.getYourPlayerInfo().roomID;
		currentRoomInfo = roomInfo;
		autoStartActive = false;

		if(roomInfo == null) numMaxPlayers = 0;
		else numMaxPlayers = roomInfo.maxPlayers;

		if(roomInfo != null) {
			rulelockFlag = roomInfo.ruleLock;
			reduceLineSend = roomInfo.reduceLineSend;
			hurryupSeconds = roomInfo.hurryupSeconds;
			hurryupInterval = roomInfo.hurryupInterval;
			useFractionalGarbage = roomInfo.useFractionalGarbage;
			garbagePercent = roomInfo.garbagePercent;
			garbageChangePerAttack = roomInfo.garbageChangePerAttack;
			divideChangeRateByPlayers = roomInfo.divideChangeRateByPlayers;
			//useTankMode = roomInfo.useTankMode;

			for(int i = 0; i < getPlayers(); i++) {
				owner.engine[i].speed.gravity = roomInfo.gravity;
				owner.engine[i].speed.denominator = roomInfo.denominator;
				owner.engine[i].speed.are = roomInfo.are;
				owner.engine[i].speed.areLine = roomInfo.areLine;
				owner.engine[i].speed.lineDelay = roomInfo.lineDelay;
				owner.engine[i].speed.lockDelay = roomInfo.lockDelay;
				owner.engine[i].speed.das = roomInfo.das;
				owner.engine[i].b2bEnable = roomInfo.b2b;
				owner.engine[i].comboType = (roomInfo.combo) ? GameEngine.COMBO_TYPE_NORMAL : GameEngine.COMBO_TYPE_DISABLE;

				if(roomInfo.tspinEnableType == 0) {
					owner.engine[i].tspinEnable = false;
					owner.engine[i].useAllSpinBonus = false;
				} else if(roomInfo.tspinEnableType == 1) {
					owner.engine[i].tspinEnable = true;
					owner.engine[i].useAllSpinBonus = false;
				} else if(roomInfo.tspinEnableType == 2) {
					owner.engine[i].tspinEnable = true;
					owner.engine[i].useAllSpinBonus = true;
				}
			}

			isNewcomer = roomInfo.playing;

			if(!rulelockFlag) {
				// Revert rules
				owner.engine[0].ruleopt.copy(netLobby.ruleOptPlayer);
				owner.engine[0].randomizer = GeneralUtil.loadRandomizer(owner.engine[0].ruleopt.strRandomizer);
				owner.engine[0].wallkick = GeneralUtil.loadWallkick(owner.engine[0].ruleopt.strWallkick);
			} else {
				// Set to locked rule
				if((netLobby != null) && (netLobby.ruleOptLock != null)) {
					log.info("Set locked rule");
					Randomizer randomizer = GeneralUtil.loadRandomizer(netLobby.ruleOptLock.strRandomizer);
					Wallkick wallkick = GeneralUtil.loadWallkick(netLobby.ruleOptLock.strWallkick);
					for(int i = 0; i < getPlayers(); i++) {
						owner.engine[i].ruleopt.copy(netLobby.ruleOptLock);
						owner.engine[i].randomizer = randomizer;
						owner.engine[i].wallkick = wallkick;
					}
				} else {
					log.warn("Tried to set locked rule, but rule was not received yet!");
				}
			}
		}

		numGames = 0;
		numWins = 0;

		for(int i = 0; i < getPlayers(); i++) {
			owner.engine[i].enableSE = false;
			if(i >= numMaxPlayers) {
				owner.engine[i].isVisible = false;
			} else {
				owner.engine[i].isVisible = true;
			}
		}
		if(playerSeatNumber >= 0) {
			owner.engine[0].displaysize = 0;
			owner.engine[0].enableSE = true;
		} else {
			owner.engine[0].displaysize = -1;
			owner.engine[0].enableSE = false;
		}

		updatePlayerExist();
		updatePlayerNames();
	}

	/**
	 * Update player names
	 */
	private void updatePlayerNames() {
		LinkedList<NetPlayerInfo> pList = netLobby.getSameRoomPlayerInfoList();

		for(int i = 0; i < MAX_PLAYERS; i++) {
			playerNames[i] = "";
			playerTeams[i] = "";

			for(NetPlayerInfo pInfo: pList) {
				if((pInfo.seatID != -1) && (getPlayerIDbySeatID(pInfo.seatID) == i)) {
					playerNames[i] = pInfo.strName;
					playerTeams[i] = pInfo.strTeam;
				}
			}
		}
	}

	/**
	 * 今溜まっているgarbage blockのcountを返す
	 * @return 今溜まっているgarbage blockのcount
	 */
	private int getTotalGarbageLines() {
		int count = 0;
		for(GarbageEntry garbageEntry: garbageEntries) {
			count += garbageEntry.lines;
		}
		return count;
	}

	/**
	 * Send field state
	 * @param engine GameEngine
	 */
	private void sendField(GameEngine engine) {
		if(isPractice) return;
		if(numPlayers + numSpectators < 2) return;

		if(owner.receiver.isStickySkin(engine) || netAlwaysSendFieldAttributes) {
			// Send with attributes
			String strSrcFieldData = engine.field.attrFieldToString();
			int nocompSize = strSrcFieldData.length();

			String strCompFieldData = NetUtil.compressString(strSrcFieldData);
			int compSize = strCompFieldData.length();

			String strFieldData = strSrcFieldData;
			boolean isCompressed = false;
			if(compSize < nocompSize) {
				strFieldData = strCompFieldData;
				isCompressed = true;
			}

			garbage[engine.playerID] = getTotalGarbageLines();

			String msg = "game\tfieldattr\t" + garbage[engine.playerID] + "\t" + engine.getSkin() + "\t";
			msg += strFieldData + "\t" + isCompressed + "\n";
			netLobby.netPlayerClient.send(msg);
		} else {
			// Send without attributes
			String strSrcFieldData = engine.field.fieldToString();
			int nocompSize = strSrcFieldData.length();

			String strCompFieldData = NetUtil.compressString(strSrcFieldData);
			int compSize = strCompFieldData.length();

			String strFieldData = strSrcFieldData;
			boolean isCompressed = false;
			if(compSize < nocompSize) {
				strFieldData = strCompFieldData;
				isCompressed = true;
			}
			//log.debug("nocompSize:" + nocompSize + " compSize:" + compSize + " isCompressed:" + isCompressed);

			garbage[engine.playerID] = getTotalGarbageLines();

			String msg = "game\tfield\t" + garbage[engine.playerID] + "\t" + engine.getSkin() + "\t" + engine.field.getHighestGarbageBlockY() + "\t";
			msg += engine.field.getHeightWithoutHurryupFloor() + "\t";
			msg += strFieldData + "\t" + isCompressed + "\n";
			netLobby.netPlayerClient.send(msg);
		}
	}

	/**
	 * Start practice mode
	 * @param engine GameEngine
	 */
	private void startPractice(GameEngine engine) {
		isPractice = true;
		isPracticeExitAllowed = false;
		engine.init();
		engine.stat = GameEngine.STAT_READY;
		engine.resetStatc();

		// map
		if((currentRoomInfo != null) && currentRoomInfo.useMap && (netLobby.mapList.size() > 0)) {
			if(randMap == null) randMap = new Random();

			int map = 0;
			int maxMap = netLobby.mapList.size();
			do {
				map = randMap.nextInt(maxMap);
			} while ((map == mapPreviousPracticeMap) && (maxMap >= 2));
			mapPreviousPracticeMap = map;

			engine.createFieldIfNeeded();
			engine.field.stringToField(netLobby.mapList.get(map));
			engine.field.setAllSkin(engine.getSkin());
			engine.field.setAllAttribute(Block.BLOCK_ATTRIBUTE_VISIBLE, true);
			engine.field.setAllAttribute(Block.BLOCK_ATTRIBUTE_OUTLINE, true);
			engine.field.setAllAttribute(Block.BLOCK_ATTRIBUTE_SELFPLACED, false);
		}
	}

	/**
	 * Send game results
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	private void sendGameStat(GameEngine engine, int playerID) {
		String msg = "gstat\t";
		msg += playerPlace[playerID] + "\t";
		msg += ((float)garbageSent[playerID] / GARBAGE_DENOMINATOR) + "\t" + playerAPL + "\t" + playerAPM + "\t";
		msg += engine.statistics.lines + "\t" + engine.statistics.lpm + "\t";
		msg += engine.statistics.totalPieceLocked + "\t" + engine.statistics.pps + "\t";
		msg += netPlayTimer + "\t" + currentKO + "\t" + numWins + "\t" + numGames;
		msg += "\n";
		netLobby.netPlayerClient.send(msg);
	}

	/*
	 * Initialization for each player
	 */
	@Override
	public void playerInit(GameEngine engine, int playerID) {
		if((playerID >= 1) || (playerSeatNumber == -1)) {
			engine.displaysize = -1;
			engine.enableSE = false;
		} else {
			engine.displaysize = 0;
			engine.enableSE = true;
		}
		engine.fieldWidth = 10;
		engine.fieldHeight = 20;
		engine.gameoverAll = false;
		engine.allowTextRenderByReceiver = true;

		garbage[playerID] = 0;
		garbageSent[playerID] = 0;

//		playerTeamsIsTank[playerID] = true;

		if(playerID == 0) {
			prevPieceID = Piece.PIECE_NONE;
			prevPieceX = 0;
			prevPieceY = 0;
			prevPieceDir = 0;

			if(garbageEntries == null) {
				garbageEntries = new LinkedList<GarbageEntry>();
			} else {
				garbageEntries.clear();
			}

			if(playerSeatNumber >= 0) {
				engine.framecolor = PLAYER_COLOR_FRAME[playerSeatNumber];
			}
		}

		if(playerID >= numMaxPlayers) {
			engine.isVisible = false;
		}
		if(playerID == getPlayers() - 1) {
			updatePlayerExist();
		}
	}

//	@Override
//	public void onFirst(GameEngine engine, int playerID) {
//		if( (useTankMode == true) && (!isPractice) && (isNetGameActive) ){
//			if(engine.ctrl.isPush(Controller.BUTTON_F)) {
//				if((!playerTeamsIsTank[0]) || (!isTank)) {
//					playerTeamsIsTank[0] = true;
//					isTank = true;
//					netLobby.netPlayerClient.send("game\ttank\n");
//				}
//			}
//		}
//	}

	/*
	 * Called at settings screen
	 */
	@Override
	public boolean onSetting(GameEngine engine, int playerID) {
		if((playerID == 0) && (playerSeatNumber >= 0)) {
			isPlayerExist[0] = true;
			engine.framecolor = PLAYER_COLOR_FRAME[playerSeatNumber];

			engine.displaysize = 0;
			engine.enableSE = true;

			if((netLobby != null) && (netLobby.netPlayerClient != null)) {
				if((!isReadyChangePending) && (numPlayers >= 2)) {
					// Ready ON
					if(engine.ctrl.isPush(Controller.BUTTON_A) && (engine.statc[3] >= 5) && (isReady[0] == false) && (!currentRoomInfo.playing)) {
						engine.playSE("decide");
						isReadyChangePending = true;
						netLobby.netPlayerClient.send("ready\ttrue\n");
					}
					// Ready OFF
					if(engine.ctrl.isPush(Controller.BUTTON_B) && (engine.statc[3] >= 5) && (isReady[0] == true) && (!currentRoomInfo.playing)) {
						engine.playSE("change");
						isReadyChangePending = true;
						netLobby.netPlayerClient.send("ready\tfalse\n");
					}
				}

				// Random map preview
				if((currentRoomInfo != null) && currentRoomInfo.useMap && !netLobby.mapList.isEmpty()) {
					if(engine.statc[3] % 30 == 0) {
						engine.statc[5]++;
						if(engine.statc[5] >= netLobby.mapList.size()) engine.statc[5] = 0;
						engine.createFieldIfNeeded();
						engine.field.stringToField(netLobby.mapList.get(engine.statc[5]));
						engine.field.setAllSkin(engine.getSkin());
						engine.field.setAllAttribute(Block.BLOCK_ATTRIBUTE_VISIBLE, true);
						engine.field.setAllAttribute(Block.BLOCK_ATTRIBUTE_OUTLINE, true);
						engine.field.setAllAttribute(Block.BLOCK_ATTRIBUTE_SELFPLACED, false);
					}
				}

				// Practice mode
				if(engine.ctrl.isPush(Controller.BUTTON_F) && (engine.statc[3] >= 5)) {
					engine.playSE("decide");
					startPractice(engine);
				}
			}

			// GC呼び出し
			if(engine.statc[3] == 0) {
				System.gc();
			}

			engine.statc[3]++;
		}

		return true;
	}

	/*
	 * Render the settings screen
	 */
	@Override
	public void renderSetting(GameEngine engine, int playerID) {
		if((netLobby == null) || (netLobby.netPlayerClient == null)) return;
		if(engine.isVisible == false) return;

		if((currentRoomInfo != null) && (!currentRoomInfo.playing)) {
			int x = receiver.getFieldDisplayPositionX(engine, playerID);
			int y = receiver.getFieldDisplayPositionY(engine, playerID);

			if(isReady[playerID] && isPlayerExist[playerID]) {
				if(engine.displaysize != -1)
					receiver.drawDirectFont(engine, playerID, x + 68, y + 204, "OK", EventReceiver.COLOR_YELLOW);
				else
					receiver.drawDirectFont(engine, playerID, x + 36, y + 80, "OK", EventReceiver.COLOR_YELLOW, 0.5f);
			}

			if((playerID == 0) && (playerSeatNumber >= 0) && (!isReadyChangePending) && (numPlayers >= 2)) {
				if(!isReady[playerID]) {
					receiver.drawMenuFont(engine, playerID, 1, 18, "A: READY", EventReceiver.COLOR_CYAN);
				} else {
					receiver.drawMenuFont(engine, playerID, 1, 18, "B:CANCEL", EventReceiver.COLOR_BLUE);
				}
			}
		}

		if((playerID == 0) && (playerSeatNumber >= 0)) {
			receiver.drawMenuFont(engine, playerID, 0, 19, "F:PRACTICE", EventReceiver.COLOR_PURPLE);
		}
	}

	/*
	 * Ready
	 */
	@Override
	public boolean onReady(GameEngine engine, int playerID) {
		if(engine.statc[0] == 0) {
			// Map
			if(currentRoomInfo.useMap && (mapNo < netLobby.mapList.size()) && !isPractice) {
				engine.createFieldIfNeeded();
				engine.field.stringToField(netLobby.mapList.get(mapNo));
				if((playerID == 0) && (playerSeatNumber >= 0)) {
					engine.field.setAllSkin(engine.getSkin());
				} else if(rulelockFlag && (netLobby.ruleOptLock != null)) {
					engine.field.setAllSkin(netLobby.ruleOptLock.skin);
				} else if(playerSkin[playerID] >= 0) {
					engine.field.setAllSkin(playerSkin[playerID]);
				}
				engine.field.setAllAttribute(Block.BLOCK_ATTRIBUTE_VISIBLE, true);
				engine.field.setAllAttribute(Block.BLOCK_ATTRIBUTE_OUTLINE, true);
				engine.field.setAllAttribute(Block.BLOCK_ATTRIBUTE_SELFPLACED, false);
			}
		}

		if(isPractice && (engine.statc[0] >= 10)) {
			isPracticeExitAllowed = true;
		}

		return false;
	}

	/*
	 * Start game
	 */
	@Override
	public void startGame(GameEngine engine, int playerID) {
		if(currentRoomInfo != null) {
			engine.speed.gravity = currentRoomInfo.gravity;
			engine.speed.denominator = currentRoomInfo.denominator;
			engine.speed.are = currentRoomInfo.are;
			engine.speed.areLine = currentRoomInfo.areLine;
			engine.speed.lineDelay = currentRoomInfo.lineDelay;
			engine.speed.lockDelay = currentRoomInfo.lockDelay;
			engine.speed.das = currentRoomInfo.das;
			engine.b2bEnable = currentRoomInfo.b2b;
			engine.comboType = (currentRoomInfo.combo) ? GameEngine.COMBO_TYPE_NORMAL : GameEngine.COMBO_TYPE_DISABLE;

			engine.spinCheckType = currentRoomInfo.spinCheckType;
			engine.tspinEnableEZ = currentRoomInfo.tspinEnableEZ;

			if(currentRoomInfo.tspinEnableType == 0) {
				engine.tspinEnable = false;
				engine.useAllSpinBonus = false;
			} else if(currentRoomInfo.tspinEnableType == 1) {
				engine.tspinEnable = true;
				engine.useAllSpinBonus = false;
			} else if(currentRoomInfo.tspinEnableType == 2) {
				engine.tspinEnable = true;
				engine.useAllSpinBonus = true;
			}
		}
		if(isPractice) {
			owner.bgmStatus.bgm = BGMStatus.BGM_NOTHING;
		} else {
			owner.bgmStatus.bgm = BGMStatus.BGM_NORMAL1;
			owner.bgmStatus.fadesw = false;
		}
		pieceMoveTimer = 0;
		hurryupCount = 0;
		hurryupShowFrames = 0;
		hurryupStarted = false;
	}

	/*
	 * 移動中の処理
	 */
	@Override
	public boolean onMove(GameEngine engine, int playerID) {
		// Start game直後の新規ピース出現時
		if((engine.ending == 0) && (engine.statc[0] == 0) && (engine.holdDisable == false) &&
		   (playerID == 0) && (playerSeatNumber >= 0) && (!isPractice))
		{
			netPlayTimerActive = true;
			sendField(engine);
		}

		// 移動
		if((engine.ending == 0) && (playerID == 0) && (playerSeatNumber >= 0) && (engine.nowPieceObject != null) && (!isPractice) &&
		   (numPlayers + numSpectators >= 2))
		{
			if( ((engine.nowPieceObject == null) && (prevPieceID != Piece.PIECE_NONE)) || (engine.manualLock) )
			{
				prevPieceID = Piece.PIECE_NONE;
				netLobby.netPlayerClient.send("game\tpiece\t" + prevPieceID + "\t" + prevPieceX + "\t" + prevPieceY + "\t" + prevPieceDir + "\t" +
						0 + "\t" + engine.getSkin() + "\t" + false + "\n");

				if((numNowPlayers == 2) && (numMaxPlayers == 2)) netSendNextAndHold(engine);
			}
			else if((engine.nowPieceObject.id != prevPieceID) || (engine.nowPieceX != prevPieceX) ||
					(engine.nowPieceY != prevPieceY) || (engine.nowPieceObject.direction != prevPieceDir))
			{
				prevPieceID = engine.nowPieceObject.id;
				prevPieceX = engine.nowPieceX;
				prevPieceY = engine.nowPieceY;
				prevPieceDir = engine.nowPieceObject.direction;

				int x = prevPieceX + engine.nowPieceObject.dataOffsetX[prevPieceDir];
				int y = prevPieceY + engine.nowPieceObject.dataOffsetY[prevPieceDir];
				netLobby.netPlayerClient.send("game\tpiece\t" + prevPieceID + "\t" + x + "\t" + y + "\t" + prevPieceDir + "\t" +
								engine.nowPieceBottomY + "\t" + engine.ruleopt.pieceColor[prevPieceID] + "\t" + engine.getSkin() + "\t" +
								engine.nowPieceObject.big + "\n");

				if((numNowPlayers == 2) && (numMaxPlayers == 2)) netSendNextAndHold(engine);
			}
		}

		// 強制固定
		if((engine.ending == 0) && (playerID == 0) && (playerSeatNumber >= 0) && (engine.nowPieceObject != null)) {
			pieceMoveTimer++;
			if(pieceMoveTimer >= PIECE_AUTO_LOCK_TIME) {
				engine.nowPieceY = engine.nowPieceBottomY;
				engine.lockDelayNow = engine.getLockDelay();
			}
		}

		if((playerID != 0) || (playerSeatNumber == -1)) {
			return true;
		}

		return false;
	}

	/*
	 * Called whenever a piece is locked
	 */
	@Override
	public void pieceLocked(GameEngine engine, int playerID, int lines) {
		if((engine.ending == 0) && (playerID == 0) && (playerSeatNumber >= 0)) {
			sendField(engine);
			pieceMoveTimer = 0;
		}
	}

	/*
	 * Line clear
	 */
	@Override
	public boolean onLineClear(GameEngine engine, int playerID) {
		if((engine.statc[0] == 1) && (engine.ending == 0) && (playerID == 0) && (playerSeatNumber >= 0)) {
			sendField(engine);
		}
		return false;
	}

	/*
	 * Calculate score
	 */
	@Override
	public void calcScore(GameEngine engine, int playerID, int lines) {
		// 攻撃
		if(lines > 0) {
			//int pts = 0;
			int[] pts = new int[ATTACK_CATEGORIES];

			scgettime[playerID] = 0;

			int numAliveTeams = getNumberOfTeamsAlive();
			int attackNumPlayerIndex = numAliveTeams - 2;
			if(isPractice || !reduceLineSend) attackNumPlayerIndex = 0;
			if(attackNumPlayerIndex < 0) attackNumPlayerIndex = 0;
			if(attackNumPlayerIndex > 4) attackNumPlayerIndex = 4;

			int attackLineIndex = LINE_ATTACK_INDEX_SINGLE;
			int mainAttackCategory = ATTACK_CATEGORY_NORMAL;

			if(engine.tspin) {
				mainAttackCategory = ATTACK_CATEGORY_SPIN;
				if(engine.tspinez) {
					attackLineIndex = LINE_ATTACK_INDEX_EZ_T;
					lastevent[playerID] = EVENT_TSPIN_EZ;
				}
				// T-Spin 1 line
				else if(lines == 1) {
					if(engine.tspinmini) {
						attackLineIndex = LINE_ATTACK_INDEX_TMINI;
						lastevent[playerID] = EVENT_TSPIN_SINGLE_MINI;
					} else {
						attackLineIndex = LINE_ATTACK_INDEX_TSINGLE;
						lastevent[playerID] = EVENT_TSPIN_SINGLE;
					}
				}
				// T-Spin 2 lines
				else if(lines == 2) {
					if(engine.tspinmini && engine.useAllSpinBonus) {
						attackLineIndex = LINE_ATTACK_INDEX_TMINI_D;
						lastevent[playerID] = EVENT_TSPIN_DOUBLE_MINI;
					} else {
						attackLineIndex = LINE_ATTACK_INDEX_TDOUBLE;
						lastevent[playerID] = EVENT_TSPIN_DOUBLE;
					}
				}
				// T-Spin 3 lines
				else if(lines >= 3) {
					attackLineIndex = LINE_ATTACK_INDEX_TTRIPLE;
					lastevent[playerID] = EVENT_TSPIN_TRIPLE;
				}
			} else {
				if(lines == 1) {
					// 1列
					attackLineIndex = LINE_ATTACK_INDEX_SINGLE;
					lastevent[playerID] = EVENT_SINGLE;
				} else if(lines == 2) {
					// 2列
					attackLineIndex = LINE_ATTACK_INDEX_DOUBLE;
					lastevent[playerID] = EVENT_DOUBLE;
				} else if(lines == 3) {
					// 3列
					attackLineIndex = LINE_ATTACK_INDEX_TRIPLE;
					lastevent[playerID] = EVENT_TRIPLE;
				} else if(lines >= 4) {
					// 4 lines
					attackLineIndex = LINE_ATTACK_INDEX_FOUR;
					lastevent[playerID] = EVENT_FOUR;
				}
			}

			// 攻撃力計算
			//log.debug("attackNumPlayerIndex:" + attackNumPlayerIndex + ", attackLineIndex:" + attackLineIndex);
			if(engine.useAllSpinBonus)
				pts[mainAttackCategory] += LINE_ATTACK_TABLE_ALLSPIN[attackLineIndex][attackNumPlayerIndex];
			else
				pts[mainAttackCategory] += LINE_ATTACK_TABLE[attackLineIndex][attackNumPlayerIndex];

			// B2B
			if(engine.b2b) {
				lastb2b[playerID] = true;

				if(pts[mainAttackCategory] > 0) {
					if((attackLineIndex == LINE_ATTACK_INDEX_TTRIPLE) && (!engine.useAllSpinBonus))
						pts[ATTACK_CATEGORY_B2B] += 2;
					else
						pts[ATTACK_CATEGORY_B2B] += 1;
				}
			} else {
				lastb2b[playerID] = false;
			}

			// Combo
			if(engine.comboType != GameEngine.COMBO_TYPE_DISABLE) {
				int cmbindex = engine.combo - 1;
				if(cmbindex < 0) cmbindex = 0;
				if(cmbindex >= COMBO_ATTACK_TABLE[attackNumPlayerIndex].length) cmbindex = COMBO_ATTACK_TABLE[attackNumPlayerIndex].length - 1;
				pts[ATTACK_CATEGORY_COMBO] += COMBO_ATTACK_TABLE[attackNumPlayerIndex][cmbindex];
				lastcombo[playerID] = engine.combo;
			}

			// All clear
			if((lines >= 1) && (engine.field.isEmpty()) && (currentRoomInfo.bravo)) {
				engine.playSE("bravo");
				pts[ATTACK_CATEGORY_BRAVO] += 6;
			}

			// gem block attack
			pts[ATTACK_CATEGORY_GEM] += engine.field.getHowManyGemClears();

			lastpiece[playerID] = engine.nowPieceObject.id;

			for(int i = 0; i < pts.length; i++){
				pts[i] *= GARBAGE_DENOMINATOR;
			}
			if(useFractionalGarbage && !isPractice) {
				if(numAliveTeams >= 3) {
					for(int i = 0; i < pts.length; i++){
						pts[i] = pts[i] / (numAliveTeams - 1);
					}
				}
			}

			// Attack lines count
			for(int i : pts){
				garbageSent[playerID] += i;
			}

			// 相殺
			garbage[playerID] = getTotalGarbageLines();
			for(int i = 0; i < pts.length; i++){ //TODO: Establish specific priority of garbage cancellation.
				if((pts[i] > 0) && (garbage[playerID] > 0) && (currentRoomInfo.counter)) {
					while(!useFractionalGarbage && !garbageEntries.isEmpty() && (pts[i] > 0)
							|| useFractionalGarbage && !garbageEntries.isEmpty() && (pts[i] >= GARBAGE_DENOMINATOR)) {
						GarbageEntry garbageEntry = garbageEntries.getFirst();
						garbageEntry.lines -= pts[i];

						if(garbageEntry.lines <= 0) {
							pts[i] = Math.abs(garbageEntry.lines);
							garbageEntries.removeFirst();
						} else {
							pts[i] = 0;
						}
					}
				}
			}

			//  Attack
			if(!isPractice && (numPlayers + numSpectators >= 2)) {
				garbage[playerID] = getTotalGarbageLines();

				String stringPts = "";
				for(int i : pts){
					stringPts += i + "\t";
				}
				netLobby.netPlayerClient.send("game\tattack\t" + stringPts + "\t" + lastevent[playerID] + "\t" + lastb2b[playerID] + "\t" +
						lastcombo[playerID] + "\t" + garbage[playerID] + "\t" + lastpiece[playerID] + "\n");
			}
		}

		// せり上がり
		if(((lines == 0) || (!currentRoomInfo.rensaBlock)) && (getTotalGarbageLines() >= GARBAGE_DENOMINATOR) && (!isPractice)) {
			engine.playSE("garbage");

			int smallGarbageCount = 0;	// 10pts未満のgarbage blockcountの合計count(後でまとめてせり上げる)
			int hole = lastHole;
			int newHole;
			if(hole == -1) {
				hole = engine.random.nextInt(engine.field.getWidth());
			}

			int finalGarbagePercent = garbagePercent;
			if(divideChangeRateByPlayers){
				finalGarbagePercent /= (getNumberOfTeamsAlive() - 1);
			}
			
			while(!garbageEntries.isEmpty()) {
				GarbageEntry garbageEntry = garbageEntries.poll();
				smallGarbageCount += garbageEntry.lines % GARBAGE_DENOMINATOR;

				if(garbageEntry.lines / GARBAGE_DENOMINATOR > 0) {
					int seatFrom = allPlayerSeatNumbers[garbageEntry.playerID];
					int garbageColor = (seatFrom < 0) ? Block.BLOCK_COLOR_GRAY : PLAYER_COLOR_BLOCK[seatFrom];
					lastAttackerUID = garbageEntry.uid;
					if(garbageChangePerAttack == true){
						if(engine.random.nextInt(100) < finalGarbagePercent) {
							newHole = engine.random.nextInt(engine.field.getWidth() - 1);
							if(newHole >= hole) {
								newHole++;
							}
							hole = newHole;
						}
						engine.field.addSingleHoleGarbage(hole, garbageColor, engine.getSkin(),
								  garbageEntry.lines / GARBAGE_DENOMINATOR);
					} else {
						for(int i = garbageEntry.lines / GARBAGE_DENOMINATOR; i > 0; i--) {
							if(engine.random.nextInt(100) < finalGarbagePercent) {
								newHole = engine.random.nextInt(engine.field.getWidth() - 1);
								if(newHole >= hole) {
									newHole++;
								}
								hole = newHole;
							}

							engine.field.addSingleHoleGarbage(hole, garbageColor, engine.getSkin(), 1);
						}
					}
				}
			}

			if(smallGarbageCount > 0) {
				// 10pts以上の部分をすべてせり上げる

				//int hole = engine.random.nextInt(engine.field.getWidth());
				//engine.field.addSingleHoleGarbage(hole, Block.BLOCK_COLOR_GRAY, engine.getSkin(),
				//		  Block.BLOCK_ATTRIBUTE_GARBAGE | Block.BLOCK_ATTRIBUTE_VISIBLE | Block.BLOCK_ATTRIBUTE_OUTLINE,
				//		  smallGarbageCount / GARBAGE_DENOMINATOR);

				if(smallGarbageCount / GARBAGE_DENOMINATOR > 0) {
					lastAttackerUID = -1;

					if(garbageChangePerAttack == true){
						if(engine.random.nextInt(100) < garbagePercent) {
							newHole = engine.random.nextInt(engine.field.getWidth() - 1);
							if(newHole >= hole) {
								newHole++;
							}
							hole = newHole;
						}
						engine.field.addSingleHoleGarbage(hole, Block.BLOCK_COLOR_GRAY, engine.getSkin(),
								  smallGarbageCount / GARBAGE_DENOMINATOR);
					} else {
						for(int i = smallGarbageCount / GARBAGE_DENOMINATOR; i > 0; i--) {
							if(engine.random.nextInt(100) < garbagePercent) {
								newHole = engine.random.nextInt(engine.field.getWidth() - 1);
								if(newHole >= hole) {
									newHole++;
								}
								hole = newHole;
							}

							engine.field.addSingleHoleGarbage(hole, Block.BLOCK_COLOR_GRAY, engine.getSkin(), 1);
						}
					}

				}
				// 10pts未満は次回繰越
				if(smallGarbageCount % GARBAGE_DENOMINATOR > 0) {
					GarbageEntry smallGarbageEntry = new GarbageEntry(smallGarbageCount % GARBAGE_DENOMINATOR, -1);
					garbageEntries.add(smallGarbageEntry);
				}
			}

			lastHole = hole;
		}

		// HURRY UP!
		if((hurryupSeconds >= 0) && (engine.timerActive) && (!isPractice)) {
			if(hurryupStarted) {
				hurryupCount++;

				if(hurryupCount % hurryupInterval == 0) {
					engine.field.addHurryupFloor(1, engine.getSkin());
				}
			} else {
				hurryupCount = hurryupInterval - 1;
			}
		}
	}

	/*
	 * ARE
	 */
	@Override
	public boolean onARE(GameEngine engine, int playerID) {
		if((engine.statc[0] == 0) && (engine.ending == 0) && (playerID == 0) && (playerSeatNumber >= 0)) {
			sendField(engine);
			if((numNowPlayers == 2) && (numMaxPlayers == 2)) netSendNextAndHold(engine);
		}
		return false;
	}

	/*
	 * Called after every frame
	 */
	@Override
	public void onLast(GameEngine engine, int playerID) {
		scgettime[playerID]++;
		if((playerID == 0) && (hurryupShowFrames > 0)) hurryupShowFrames--;

		// HURRY UP!
		if((playerID == 0) && (engine.timerActive) && (hurryupSeconds >= 0) && (engine.statistics.time == hurryupSeconds * 60) &&
		   (!isPractice) && (!hurryupStarted))
		{
			netLobby.netPlayerClient.send("game\thurryup\n");
			owner.receiver.playSE("hurryup");
			hurryupStarted = true;
			hurryupShowFrames = 60 * 5;
		}

		// せり上がりMeter
		int tempGarbage = garbage[playerID] / GARBAGE_DENOMINATOR;
		float tempGarbageF = (float) garbage[playerID] / GARBAGE_DENOMINATOR;
		int newMeterValue = (int)(tempGarbageF * receiver.getBlockGraphicsHeight(engine, playerID));
		if((playerID == 0) && (playerSeatNumber != -1)) {
			if(newMeterValue > engine.meterValue) {
				engine.meterValue += receiver.getBlockGraphicsHeight(engine, playerID) / 2;
				if(engine.meterValue > newMeterValue) {
					engine.meterValue = newMeterValue;
				}
			} else if(newMeterValue < engine.meterValue) {
				engine.meterValue--;
			}
		} else {
			engine.meterValue = newMeterValue;
		}
		if(tempGarbage >= 4) engine.meterColor = GameEngine.METER_COLOR_RED;
		else if(tempGarbage >= 3) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
		else if(tempGarbage >= 1) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
		else engine.meterColor = GameEngine.METER_COLOR_GREEN;

		// APL & APM
		if((playerID == 0) && (engine.gameActive) && (engine.timerActive)) {
			float tempGarbageSent = (float)garbageSent[playerID] / GARBAGE_DENOMINATOR;
			playerAPM = (tempGarbageSent * 3600) / (engine.statistics.time);

			if(engine.statistics.lines > 0) {
				playerAPL = (float)(tempGarbageSent / engine.statistics.lines);
			} else {
				playerAPL = 0f;
			}
		}

		// Timer
		if((playerID == 0) && (netPlayTimerActive)) netPlayTimer++;

		// Automatically start timer
		if((playerID == 0) && (currentRoomInfo != null) && (autoStartActive) && (!isNetGameActive)) {
			if(numPlayers <= 1) {
				autoStartActive = false;
			} else if(autoStartTimer > 0) {
				autoStartTimer--;
			} else {
				if(playerSeatNumber != -1) {
					netLobby.netPlayerClient.send("autostart\n");
				}
				autoStartTimer = 0;
				autoStartActive = false;
			}
		}

		// End practice mode
		if((playerID == 0) && ((isPractice) && (isPracticeExitAllowed)) && (engine.ctrl.isPush(Controller.BUTTON_F))) {
			engine.timerActive = false;
			owner.bgmStatus.bgm = BGMStatus.BGM_NOTHING;
			isPracticeExitAllowed = false;

			if(isPractice) {
				isPractice = false;
				engine.field.reset();
				engine.gameEnded();
				engine.stat = GameEngine.STAT_SETTING;
				engine.resetStatc();
			} else {
				engine.stat = GameEngine.STAT_GAMEOVER;
				engine.resetStatc();
			}
		}

		/*
		if(currentRoomID == -1) {
			engine.isVisible = false;
		} else if((netLobby.netClient != null) && (currentRoomID != -1)) {
			NetRoomInfo roomInfo = netLobby.netClient.getRoomInfo(currentRoomID);
			if((roomInfo == null) || (playerID >= roomInfo.maxPlayers)) {
				engine.isVisible = false;
			}
		}
		*/
	}

	/*
	 * Drawing processing at the end of every frame
	 */
	@Override
	public void renderLast(GameEngine engine, int playerID) {
		// Number of players
		if((playerID == getPlayers() - 1) && (netLobby != null) && (netLobby.netPlayerClient != null) && (netLobby.netPlayerClient.isConnected()) &&
		   (!owner.engine[1].isVisible || owner.engine[1].displaysize == -1 || !isNetGameActive))
		{
			int x = (owner.receiver.getNextDisplayType() == 2) ? 544 : 503;
			if(currentRoomID != -1) {
				receiver.drawDirectFont(engine, 0, x, 286, "PLAYERS", EventReceiver.COLOR_CYAN, 0.5f);
				receiver.drawDirectFont(engine, 0, x, 294, "" + numPlayers, EventReceiver.COLOR_WHITE, 0.5f);
				receiver.drawDirectFont(engine, 0, x, 302, "SPECTATORS", EventReceiver.COLOR_CYAN, 0.5f);
				receiver.drawDirectFont(engine, 0, x, 310, "" + numSpectators, EventReceiver.COLOR_WHITE, 0.5f);
				receiver.drawDirectFont(engine, 0, x, 318, "MATCHES", EventReceiver.COLOR_CYAN, 0.5f);
				receiver.drawDirectFont(engine, 0, x, 326, "" + numGames, EventReceiver.COLOR_WHITE, 0.5f);
				receiver.drawDirectFont(engine, 0, x, 334, "WINS", EventReceiver.COLOR_CYAN, 0.5f);
				receiver.drawDirectFont(engine, 0, x, 342, "" + numWins, EventReceiver.COLOR_WHITE, 0.5f);
			}
			receiver.drawDirectFont(engine, 0, x, 358, "ALL ROOMS", EventReceiver.COLOR_GREEN, 0.5f);
			receiver.drawDirectFont(engine, 0, x, 366, "" + netLobby.netPlayerClient.getRoomInfoList().size(), EventReceiver.COLOR_WHITE, 0.5f);
		}

		// All number of players
		if(playerID == getPlayers() - 1) netDrawAllPlayersCount(engine);

		// 経過 time
		if((playerID == 0) && (currentRoomID != -1)) {
			receiver.drawDirectFont(engine, 0, 256, 16, GeneralUtil.getTime(netPlayTimer));

			if((hurryupSeconds >= 0) && (hurryupShowFrames > 0) && (!isPractice) && (hurryupStarted)) {
				receiver.drawDirectFont(engine, 0, 256 - 8, 32, "HURRY UP!", (hurryupShowFrames % 2 == 0));
			}
		}

		if((isPlayerExist[playerID]) && (engine.isVisible)) {
			int x = receiver.getFieldDisplayPositionX(engine, playerID);
			int y = receiver.getFieldDisplayPositionY(engine, playerID);

			// Name
			if((playerNames != null) && (playerNames[playerID] != null) && (playerNames[playerID].length() > 0)) {
				String name = playerNames[playerID];

				if( (playerSeatNumber != -1) &&
					(playerTeams[0].length() > 0) && (playerTeams[playerID].length() > 0) &&
					(playerTeams[0].equalsIgnoreCase(playerTeams[playerID]) == true) )
				{
					name = "*" + playerNames[playerID];
				}

				if(engine.displaysize == -1) {
					if(name.length() > 7) name = name.substring(0, 7) + "..";
					receiver.drawTTFDirectFont(engine, playerID, x, y - 16, name);
				} else if(playerID == 0) {
					if(name.length() > 14) name = name.substring(0, 14) + "..";
					receiver.drawTTFDirectFont(engine, playerID, x, y - 20, name);
				} else {
					receiver.drawTTFDirectFont(engine, playerID, x, y - 20, name);
				}
			}

			// garbage blockcount
			if((garbage[playerID] > 0) && (useFractionalGarbage)) {
				String strTempGarbage;

				int fontColor = EventReceiver.COLOR_WHITE;
				if(garbage[playerID] >= GARBAGE_DENOMINATOR) fontColor = EventReceiver.COLOR_YELLOW;
				if(garbage[playerID] >= GARBAGE_DENOMINATOR*3) fontColor = EventReceiver.COLOR_ORANGE;
				if(garbage[playerID] >= GARBAGE_DENOMINATOR*4) fontColor = EventReceiver.COLOR_RED;

				if(engine.displaysize != -1) {
					//strTempGarbage = String.format(Locale.ROOT, "%5.2f", (float)garbage[playerID] / GARBAGE_DENOMINATOR);
					strTempGarbage = String.format(Locale.US, "%5.2f", (float)garbage[playerID] / GARBAGE_DENOMINATOR);
					receiver.drawDirectFont(engine, playerID, x + 96, y + 372, strTempGarbage, fontColor, 1.0f);
				} else {
					//strTempGarbage = String.format(Locale.ROOT, "%4.1f", (float)garbage[playerID] / GARBAGE_DENOMINATOR);
					strTempGarbage = String.format(Locale.US, "%4.1f", (float)garbage[playerID] / GARBAGE_DENOMINATOR);
					receiver.drawDirectFont(engine, playerID, x + 64, y + 168, strTempGarbage, fontColor, 0.5f);
				}
			}
		}

		// Practice mode
		if((playerID == 0) && ((isPractice) || (numNowPlayers == 1)) && (isPracticeExitAllowed)) {
			if((lastevent[playerID] == EVENT_NONE) || (scgettime[playerID] >= 120) || (lastcombo[playerID] < 2)) {
				receiver.drawMenuFont(engine, 0, 0, 22, "F:END GAME", EventReceiver.COLOR_PURPLE);
			}

			if(isPractice && engine.timerActive) {
				receiver.drawDirectFont(engine, 0, 256, 32, GeneralUtil.getTime(engine.statistics.time), EventReceiver.COLOR_PURPLE);
			}
		}

		// Automatically start timer
		if((playerID == 0) && (currentRoomInfo != null) && (autoStartActive) && (!isNetGameActive)) {
			receiver.drawDirectFont(engine, 0, 496, 16, GeneralUtil.getTime(autoStartTimer),
									currentRoomInfo.autoStartTNET2, EventReceiver.COLOR_RED, EventReceiver.COLOR_YELLOW);
		}

		// Line clear event 表示
		if((lastevent[playerID] != EVENT_NONE) && (scgettime[playerID] < 120)) {
			String strPieceName = Piece.getPieceName(lastpiece[playerID]);

			if(engine.displaysize != -1) {
				switch(lastevent[playerID]) {
				case EVENT_SINGLE:
					receiver.drawMenuFont(engine, playerID, 2, 21, "SINGLE", EventReceiver.COLOR_DARKBLUE);
					break;
				case EVENT_DOUBLE:
					receiver.drawMenuFont(engine, playerID, 2, 21, "DOUBLE", EventReceiver.COLOR_BLUE);
					break;
				case EVENT_TRIPLE:
					receiver.drawMenuFont(engine, playerID, 2, 21, "TRIPLE", EventReceiver.COLOR_GREEN);
					break;
				case EVENT_FOUR:
					if(lastb2b[playerID]) receiver.drawMenuFont(engine, playerID, 3, 21, "FOUR", EventReceiver.COLOR_RED);
					else receiver.drawMenuFont(engine, playerID, 3, 21, "FOUR", EventReceiver.COLOR_ORANGE);
					break;
				case EVENT_TSPIN_SINGLE_MINI:
					if(lastb2b[playerID]) receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-MINI-S", EventReceiver.COLOR_RED);
					else receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-MINI-S", EventReceiver.COLOR_ORANGE);
					break;
				case EVENT_TSPIN_SINGLE:
					if(lastb2b[playerID]) receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-SINGLE", EventReceiver.COLOR_RED);
					else receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-SINGLE", EventReceiver.COLOR_ORANGE);
					break;
				case EVENT_TSPIN_DOUBLE_MINI:
					if(lastb2b[playerID]) receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-MINI-D", EventReceiver.COLOR_RED);
					else receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-MINI-D", EventReceiver.COLOR_ORANGE);
					break;
				case EVENT_TSPIN_DOUBLE:
					if(lastb2b[playerID]) receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-DOUBLE", EventReceiver.COLOR_RED);
					else receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-DOUBLE", EventReceiver.COLOR_ORANGE);
					break;
				case EVENT_TSPIN_TRIPLE:
					if(lastb2b[playerID]) receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-TRIPLE", EventReceiver.COLOR_RED);
					else receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-TRIPLE", EventReceiver.COLOR_ORANGE);
					break;
				case EVENT_TSPIN_EZ:
					if(lastb2b[playerID]) receiver.drawMenuFont(engine, playerID, 3, 21, "EZ-" + strPieceName, EventReceiver.COLOR_RED);
					else receiver.drawMenuFont(engine, playerID, 3, 21, "EZ-" + strPieceName, EventReceiver.COLOR_ORANGE);
					break;
				}

				if(lastcombo[playerID] >= 2)
					receiver.drawMenuFont(engine, playerID, 2, 22, (lastcombo[playerID] - 1) + "COMBO", EventReceiver.COLOR_CYAN);
			} else {
				int x = receiver.getFieldDisplayPositionX(engine, playerID);
				int y = receiver.getFieldDisplayPositionY(engine, playerID);
				int x2 = 8;
				if(useFractionalGarbage && (garbage[playerID] > 0)) x2 = 0;

				switch(lastevent[playerID]) {
				case EVENT_SINGLE:
					receiver.drawDirectFont(engine, playerID, x + 4 + 16, y + 168, "SINGLE", EventReceiver.COLOR_DARKBLUE, 0.5f);
					break;
				case EVENT_DOUBLE:
					receiver.drawDirectFont(engine, playerID, x + 4 + 16, y + 168, "DOUBLE", EventReceiver.COLOR_BLUE, 0.5f);
					break;
				case EVENT_TRIPLE:
					receiver.drawDirectFont(engine, playerID, x + 4 + 16, y + 168, "TRIPLE", EventReceiver.COLOR_GREEN, 0.5f);
					break;
				case EVENT_FOUR:
					if(lastb2b[playerID]) receiver.drawDirectFont(engine, playerID, x + 4 + 24, y + 168, "FOUR", EventReceiver.COLOR_RED, 0.5f);
					else receiver.drawDirectFont(engine, playerID, x + 4 + 24, y + 168, "FOUR", EventReceiver.COLOR_ORANGE, 0.5f);
					break;
				case EVENT_TSPIN_SINGLE_MINI:
					if(lastb2b[playerID])
						receiver.drawDirectFont(engine, playerID, x + 4 + x2, y + 168, strPieceName + "-MINI-S", EventReceiver.COLOR_RED, 0.5f);
					else
						receiver.drawDirectFont(engine, playerID, x + 4 + x2, y + 168, strPieceName + "-MINI-S", EventReceiver.COLOR_ORANGE, 0.5f);
					break;
				case EVENT_TSPIN_SINGLE:
					if(lastb2b[playerID])
						receiver.drawDirectFont(engine, playerID, x + 4 + x2, y + 168, strPieceName + "-SINGLE", EventReceiver.COLOR_RED, 0.5f);
					else
						receiver.drawDirectFont(engine, playerID, x + 4 + x2, y + 168, strPieceName + "-SINGLE", EventReceiver.COLOR_ORANGE, 0.5f);
					break;
				case EVENT_TSPIN_DOUBLE_MINI:
					if(lastb2b[playerID])
						receiver.drawDirectFont(engine, playerID, x + 4 + x2, y + 168, strPieceName + "-MINI-D", EventReceiver.COLOR_RED, 0.5f);
					else
						receiver.drawDirectFont(engine, playerID, x + 4 + x2, y + 168, strPieceName + "-MINI-D", EventReceiver.COLOR_ORANGE, 0.5f);
					break;
				case EVENT_TSPIN_DOUBLE:
					if(lastb2b[playerID])
						receiver.drawDirectFont(engine, playerID, x + 4 + x2, y + 168, strPieceName + "-DOUBLE", EventReceiver.COLOR_RED, 0.5f);
					else
						receiver.drawDirectFont(engine, playerID, x + 4 + x2, y + 168, strPieceName + "-DOUBLE", EventReceiver.COLOR_ORANGE, 0.5f);
					break;
				case EVENT_TSPIN_TRIPLE:
					if(lastb2b[playerID])
						receiver.drawDirectFont(engine, playerID, x + 4 + x2, y + 168, strPieceName + "-TRIPLE", EventReceiver.COLOR_RED, 0.5f);
					else
						receiver.drawDirectFont(engine, playerID, x + 4 + x2, y + 168, strPieceName + "-TRIPLE", EventReceiver.COLOR_ORANGE, 0.5f);
					break;
				case EVENT_TSPIN_EZ:
					if(lastb2b[playerID])
						receiver.drawDirectFont(engine, playerID, x + 4 + 24, y + 168, "EZ-" + strPieceName, EventReceiver.COLOR_RED, 0.5f);
					else
						receiver.drawDirectFont(engine, playerID, x + 4 + 24, y + 168, "EZ-" + strPieceName, EventReceiver.COLOR_ORANGE, 0.5f);
					break;
				}

				if(lastcombo[playerID] >= 2)
					receiver.drawDirectFont(engine, playerID, x + 4 + 16, y + 176, (lastcombo[playerID] - 1) + "COMBO", EventReceiver.COLOR_CYAN, 0.5f);
			}
		}
	}

	/*
	 * game over
	 */
	@Override
	public boolean onGameOver(GameEngine engine, int playerID) {
		engine.gameEnded();
		engine.allowTextRenderByReceiver = false;
		isPracticeExitAllowed = false;

		if((playerID == 0) && (isPractice)) {
			if(engine.statc[0] < engine.field.getHeight() + 1) {
				return false;
			} else {
				engine.field.reset();
				engine.stat = GameEngine.STAT_RESULT;
				engine.resetStatc();
				return true;
			}
		}

		if((playerID == 0) && (!isDead[playerID])) {
			owner.bgmStatus.bgm = BGMStatus.BGM_NOTHING;
			engine.resetFieldVisible();

			sendField(engine);
			if((numNowPlayers == 2) && (numMaxPlayers == 2)) netSendNextAndHold(engine);
			netLobby.netPlayerClient.send("dead\t" + lastAttackerUID + "\n");

			engine.stat = GameEngine.STAT_CUSTOM;
			engine.resetStatc();
			return true;
		}

		if(isDead[playerID]) {
			if(playerPlace[playerID] <= 2) {
				engine.statistics.time = netPlayTimer;
			}
			if(engine.field == null) {
				engine.stat = GameEngine.STAT_SETTING;
				engine.resetStatc();
				return true;
			}
			if( (engine.statc[0] < engine.field.getHeight() + 1) || ((playerID == 0) && (playerSeatNumber >= 0)) ) {
				return false;
			}
		}

		return true;
	}

	/*
	 * game overDraw the screen
	 */
	@Override
	public void renderGameOver(GameEngine engine, int playerID) {
		if((playerID == 0) && (isPractice)) return;
		if(engine.isVisible == false) return;

		int x = receiver.getFieldDisplayPositionX(engine, playerID);
		int y = receiver.getFieldDisplayPositionY(engine, playerID);
		int place = playerPlace[playerID];

		if(engine.displaysize != -1) {
			if(isReady[playerID] && !isNetGameActive) {
				receiver.drawDirectFont(engine, playerID, x + 68, y + 204, "OK", EventReceiver.COLOR_YELLOW);
			} else if((numNowPlayers == 2) && (isDead[playerID])) {
				receiver.drawDirectFont(engine, playerID, x + 52, y + 204, "LOSE", EventReceiver.COLOR_WHITE);
			} else if(place == 1) {
				//receiver.drawDirectFont(engine, playerID, x + 12, y + 204, "GAME OVER", EventReceiver.COLOR_WHITE);
			} else if(place == 2) {
				receiver.drawDirectFont(engine, playerID, x + 12, y + 204, "2ND PLACE", EventReceiver.COLOR_WHITE);
			} else if(place == 3) {
				receiver.drawDirectFont(engine, playerID, x + 12, y + 204, "3RD PLACE", EventReceiver.COLOR_RED);
			} else if(place == 4) {
				receiver.drawDirectFont(engine, playerID, x + 12, y + 204, "4TH PLACE", EventReceiver.COLOR_GREEN);
			} else if(place == 5) {
				receiver.drawDirectFont(engine, playerID, x + 12, y + 204, "5TH PLACE", EventReceiver.COLOR_BLUE);
			} else if(place == 6) {
				receiver.drawDirectFont(engine, playerID, x + 12, y + 204, "6TH PLACE", EventReceiver.COLOR_PURPLE);
			}

			if(playerKObyYou[playerID]) {
				receiver.drawDirectFont(engine, playerID, x + 52, y + 236, "K.O.", EventReceiver.COLOR_PINK);
			}
		} else {
			if(isReady[playerID] && !isNetGameActive) {
				receiver.drawDirectFont(engine, playerID, x + 36, y + 80, "OK", EventReceiver.COLOR_YELLOW, 0.5f);
			} else if(numNowPlayers == 2) {
				receiver.drawDirectFont(engine, playerID, x + 28, y + 80, "LOSE", EventReceiver.COLOR_WHITE, 0.5f);
			} else if(place == 1) {
				//receiver.drawDirectFont(engine, playerID, x + 8, y + 80, "GAME OVER", EventReceiver.COLOR_WHITE, 0.5f);
			} else if(place == 2) {
				receiver.drawDirectFont(engine, playerID, x + 8, y + 80, "2ND PLACE", EventReceiver.COLOR_WHITE, 0.5f);
			} else if(place == 3) {
				receiver.drawDirectFont(engine, playerID, x + 8, y + 80, "3RD PLACE", EventReceiver.COLOR_RED, 0.5f);
			} else if(place == 4) {
				receiver.drawDirectFont(engine, playerID, x + 8, y + 80, "4TH PLACE", EventReceiver.COLOR_GREEN, 0.5f);
			} else if(place == 5) {
				receiver.drawDirectFont(engine, playerID, x + 8, y + 80, "5TH PLACE", EventReceiver.COLOR_BLUE, 0.5f);
			} else if(place == 6) {
				receiver.drawDirectFont(engine, playerID, x + 8, y + 80, "6TH PLACE", EventReceiver.COLOR_PURPLE, 0.5f);
			}

			if(playerKObyYou[playerID]) {
				receiver.drawDirectFont(engine, playerID, x + 28, y + 96, "K.O.", EventReceiver.COLOR_PINK, 0.5f);
			}
		}
	}

	/*
	 * After being defeated
	 */
	@Override
	public boolean onCustom(GameEngine engine, int playerID) {
		if(!isNetGameActive) {
			isDead[playerID] = true;
			engine.stat = GameEngine.STAT_GAMEOVER;
			engine.resetStatc();
		}
		return false;
	}

	/*
	 * EXCELLENT画面の処理
	 */
	@Override
	public boolean onExcellent(GameEngine engine, int playerID) {
		engine.gameEnded();
		engine.allowTextRenderByReceiver = false;

		if(engine.statc[0] == 0) {
			//if((playerID == 0) && (playerSeatNumber != -1)) numWins++;
			owner.bgmStatus.bgm = BGMStatus.BGM_NOTHING;
			if(engine.ai != null) engine.ai.shutdown(engine, playerID);
			engine.resetFieldVisible();
			engine.playSE("excellent");
		}

		if((engine.statc[0] >= 120) && (engine.ctrl.isPush(Controller.BUTTON_A))) {
			engine.statc[0] = engine.field.getHeight() + 1 + 180;
		}

		if((engine.statc[0] >= engine.field.getHeight() + 1 + 180) && (!isNetGameActive) && (playerID == 0) && (playerSeatNumber != -1)) {
			if(engine.field != null) engine.field.reset();
			engine.resetStatc();
			engine.stat = GameEngine.STAT_RESULT;
		} else {
			engine.statc[0]++;
		}

		return true;
	}

	/*
	 * EXCELLENT画面の描画処理
	 */
	@Override
	public void renderExcellent(GameEngine engine, int playerID) {
		if(engine.isVisible == false) return;

		int x = receiver.getFieldDisplayPositionX(engine, playerID);
		int y = receiver.getFieldDisplayPositionY(engine, playerID);

		if(engine.displaysize != -1) {
			if(isReady[playerID] && !isNetGameActive) {
				receiver.drawDirectFont(engine, playerID, x + 68, y + 204, "OK", EventReceiver.COLOR_YELLOW);
			} else if(numNowPlayers == 2) {
				receiver.drawDirectFont(engine, playerID, x + 52, y + 204, "WIN!", EventReceiver.COLOR_YELLOW);
			} else {
				receiver.drawDirectFont(engine, playerID, x + 4, y + 204, "1ST PLACE!", EventReceiver.COLOR_YELLOW);
			}
		} else {
			if(isReady[playerID] && !isNetGameActive) {
				receiver.drawDirectFont(engine, playerID, x + 36, y + 80, "OK", EventReceiver.COLOR_YELLOW, 0.5f);
			} else if(numNowPlayers == 2) {
				receiver.drawDirectFont(engine, playerID, x + 28, y + 80, "WIN!", EventReceiver.COLOR_YELLOW, 0.5f);
			} else {
				receiver.drawDirectFont(engine, playerID, x + 4, y + 80, "1ST PLACE!", EventReceiver.COLOR_YELLOW, 0.5f);
			}
		}
	}

	/*
	 * 結果画面の処理
	 */
	@Override
	public boolean onResult(GameEngine engine, int playerID) {
		engine.allowTextRenderByReceiver = false;

		// 設定画面へ
		if(engine.ctrl.isPush(Controller.BUTTON_A) && !isNetGameActive && (playerID == 0)) {
			engine.playSE("decide");
			resetFlags();
			owner.reset();
		}
		// Practice mode
		if(engine.ctrl.isPush(Controller.BUTTON_F) && (playerID == 0)) {
			engine.playSE("decide");
			startPractice(engine);
		}

		return true;
	}

	/*
	 * Render results screen処理
	 */
	@Override
	public void renderResult(GameEngine engine, int playerID) {
		if(!isPractice) {
			receiver.drawMenuFont(engine, playerID, 0, 0, "RESULT", EventReceiver.COLOR_ORANGE);

			if(playerPlace[playerID] == 1) {
				if(numNowPlayers == 2) {
					receiver.drawMenuFont(engine, playerID, 6, 1, "WIN!", EventReceiver.COLOR_YELLOW);
				} else if(numNowPlayers > 2) {
					receiver.drawMenuFont(engine, playerID, 6, 1, "1ST!", EventReceiver.COLOR_YELLOW);
				}
			} else if(playerPlace[playerID] == 2) {
				if(numNowPlayers == 2) {
					receiver.drawMenuFont(engine, playerID, 6, 1, "LOSE", EventReceiver.COLOR_WHITE);
				} else {
					receiver.drawMenuFont(engine, playerID, 7, 1, "2ND", EventReceiver.COLOR_WHITE);
				}
			} else if(playerPlace[playerID] == 3) {
				receiver.drawMenuFont(engine, playerID, 7, 1, "3RD", EventReceiver.COLOR_RED);
			} else if(playerPlace[playerID] == 4) {
				receiver.drawMenuFont(engine, playerID, 7, 1, "4TH", EventReceiver.COLOR_GREEN);
			} else if(playerPlace[playerID] == 5) {
				receiver.drawMenuFont(engine, playerID, 7, 1, "5TH", EventReceiver.COLOR_BLUE);
			} else if(playerPlace[playerID] == 6) {
				receiver.drawMenuFont(engine, playerID, 7, 1, "6TH", EventReceiver.COLOR_DARKBLUE);
			}
		} else {
			receiver.drawMenuFont(engine, playerID, 0, 0, "PRACTICE", EventReceiver.COLOR_PINK);
		}

		drawResult(engine, playerID, receiver, 2, EventReceiver.COLOR_ORANGE,
				"ATTACK", String.format("%10g", (float)garbageSent[playerID] / GARBAGE_DENOMINATOR),
				"LINE", String.format("%10d", engine.statistics.lines),
				"PIECE", String.format("%10d", engine.statistics.totalPieceLocked),
				"ATK/LINE", String.format("%10g", playerAPL),
				"ATTACK/MIN", String.format("%10g", playerAPM),
				"LINE/MIN", String.format("%10g", engine.statistics.lpm),
				"PIECE/SEC", String.format("%10g", engine.statistics.pps),
				"TIME", String.format("%10s", GeneralUtil.getTime(engine.statistics.time)));

		if(!isNetGameActive && (playerSeatNumber >= 0) && (playerID == 0)) {
			receiver.drawMenuFont(engine, playerID, 2, 18, "PUSH A", EventReceiver.COLOR_RED);
		}

		if(!isPractice) {
			receiver.drawMenuFont(engine, playerID, 0, 19, "F:PRACTICE", EventReceiver.COLOR_PURPLE);
		} else {
			receiver.drawMenuFont(engine, playerID, 1, 19, "F: RETRY", EventReceiver.COLOR_PURPLE);
		}
	}

	/**
	 * No retry key.
	 */
	@Override
	public void netplayOnRetryKey(GameEngine engine, int playerID) {
	}

	public void netlobbyOnDisconnect(NetLobbyFrame lobby, NetPlayerClient client, Throwable ex) {
		for(int i = 0; i < getPlayers(); i++) {
			owner.engine[i].stat = GameEngine.STAT_NOTHING;
		}
	}

	public void netlobbyOnMessage(NetLobbyFrame lobby, NetPlayerClient client, String[] message) throws IOException {
		// Player状態変更
		if(message[0].equals("playerupdate")) {
			NetPlayerInfo pInfo = new NetPlayerInfo(message[1]);

			if((pInfo.roomID == currentRoomID) && (pInfo.seatID != -1)) {
				int playerID = getPlayerIDbySeatID(pInfo.seatID);

				if(isReady[playerID] != pInfo.ready) {
					isReady[playerID] = pInfo.ready;

					if((playerID == 0) && (playerSeatNumber != -1)) {
						isReadyChangePending = false;
					} else {
						if(pInfo.ready) receiver.playSE("decide");
						else if(!pInfo.playing) receiver.playSE("change");
					}
				}
			}

			updatePlayerExist();
			updatePlayerNames();
		}
		// Player切断
		if(message[0].equals("playerlogout")) {
			NetPlayerInfo pInfo = new NetPlayerInfo(message[1]);

			if((pInfo.roomID == currentRoomID) && (pInfo.seatID != -1)) {
				updatePlayerExist();
				updatePlayerNames();
			}
		}
		// 参戦状態変更
		if(message[0].equals("changestatus")) {
			int uid = Integer.parseInt(message[2]);

			if(uid == netLobby.netPlayerClient.getPlayerUID()) {
				playerSeatNumber = client.getYourPlayerInfo().seatID;
				isReady[0] = false;

				updatePlayerExist();
				updatePlayerNames();

				if(playerSeatNumber >= 0) {
					// 参戦
					owner.engine[0].displaysize = 0;
					owner.engine[0].enableSE = true;
					for(int i = 1; i < getPlayers(); i++) {
						owner.engine[i].displaysize = -1;
					}
				} else {
					// 観戦
					for(int i = 0; i < getPlayers(); i++) {
						owner.engine[i].displaysize = -1;
						owner.engine[i].enableSE = false;
					}
				}
				isPractice = false;
				owner.engine[0].stat = GameEngine.STAT_SETTING;

				for(int i = 0; i < getPlayers(); i++) {
					if(owner.engine[i].field != null) {
						owner.engine[i].field.reset();
					}
					owner.engine[i].nowPieceObject = null;
					garbage[i] = 0;

					if((owner.engine[i].stat == GameEngine.STAT_NOTHING) || (isNetGameFinished)) {
						owner.engine[i].stat = GameEngine.STAT_SETTING;
					}
					owner.engine[i].resetStatc();
				}
			} else {
				if(message[1].equals("watchonly")) {
					int seatID = Integer.parseInt(message[4]);
					int playerID = getPlayerIDbySeatID(seatID);
					isPlayerExist[playerID] = false;
					isReady[playerID] = false;
					garbage[playerID] = 0;
				}
			}
		}
		// 誰か来た
		if(message[0].equals("playerenter")) {
			int seatID = Integer.parseInt(message[3]);
			if((seatID != -1) && (numPlayers < 2)) {
				owner.receiver.playSE("levelstop");
			}
		}
		// 誰か出て行った
		if(message[0].equals("playerleave")) {
			int seatID = Integer.parseInt(message[3]);

			if(seatID != -1) {
				int playerID = getPlayerIDbySeatID(seatID);
				isPlayerExist[playerID] = false;
				isReady[playerID] = false;
				garbage[playerID] = 0;

				numPlayers--;
				if(numPlayers < 2) {
					isReady[0] = false;
					autoStartActive = false;
				}
			}
		}
		// Automatic timer start
		if(message[0].equals("autostartbegin")) {
			if(numPlayers >= 2) {
				int seconds = Integer.parseInt(message[1]);
				autoStartTimer = seconds * 60;
				autoStartActive = true;
			}
		}
		// Automatic timer stop
		if(message[0].equals("autostartstop")) {
			autoStartActive = false;
		}
		// game start
		if(message[0].equals("start")) {
			long randseed = Long.parseLong(message[1], 16);
			numNowPlayers = Integer.parseInt(message[2]);
			if((numNowPlayers >= 2) && (playerSeatNumber != -1)) numGames++;
			numAlivePlayers = numNowPlayers;
			mapNo = Integer.parseInt(message[3]);

			resetFlags();
			owner.reset();

			autoStartActive = false;
			isNetGameActive = true;
			netPlayTimer = 0;

			if(currentRoomInfo != null)
				currentRoomInfo.playing = true;

			if((playerSeatNumber != -1) && (!rulelockFlag) && (netLobby.ruleOptPlayer != null))
				owner.engine[0].ruleopt.copy(netLobby.ruleOptPlayer);	// Restore rules

			updatePlayerExist();
			updatePlayerNames();

			log.debug("Game Started numNowPlayers:" + numNowPlayers + " numMaxPlayers:" + numMaxPlayers + " mapNo:" + mapNo);

			for(int i = 0; i < getPlayers(); i++) {
				GameEngine engine = owner.engine[i];
				engine.resetStatc();

				if(isPlayerExist[i]) {
					engine.stat = GameEngine.STAT_READY;
					engine.randSeed = randseed;
					engine.random = new Random(randseed);

					if((numMaxPlayers == 2) && (numNowPlayers == 2)) {
						engine.isVisible = true;
						engine.displaysize = 0;

						if( (rulelockFlag) || ((i == 0) && (playerSeatNumber != -1)) ) {
							engine.isNextVisible = true;
							engine.isHoldVisible = true;

							if(i != 0) {
								engine.randomizer = owner.engine[0].randomizer;
							}
						} else {
							engine.isNextVisible = false;
							engine.isHoldVisible = false;
						}
					}
				} else if(i < numMaxPlayers) {
					engine.stat = GameEngine.STAT_SETTING;
					engine.isVisible = true;
					engine.isNextVisible = false;
					engine.isHoldVisible = false;

					if((numMaxPlayers == 2) && (numNowPlayers == 2)) {
						engine.isVisible = false;
					}
				} else {
					engine.stat = GameEngine.STAT_SETTING;
					engine.isVisible = false;
				}

				isDead[i] = false;
				isReady[i] = false;
			}
		}
		// 死亡
		if(message[0].equals("dead")) {
			int seatID = Integer.parseInt(message[3]);
			int playerID = getPlayerIDbySeatID(seatID);
			int koUID = -1;
			if(message.length > 5) koUID = Integer.parseInt(message[5]);

//			if((useTankMode == true) && (playerTeamsIsTank[playerID] == true)) {
//				String teamName = playerTeams[playerID];
//				for(int i = 0; i < MAX_PLAYERS; i++ ){
//					if((playerTeams[i].length() > 0) && (isPlayerExist[i]) && (playerTeams[i].equals(teamName))) {
//						if(i != playerID) {
//							playerTeamsIsTank[i] = true;
//						}
//					}
//
//				}
//				isTank = true;
//			}

			if(!isDead[playerID]) {
				isDead[playerID] = true;
				playerPlace[playerID] = Integer.parseInt(message[4]);
				owner.engine[playerID].gameEnded();
				owner.engine[playerID].stat = GameEngine.STAT_GAMEOVER;
				owner.engine[playerID].resetStatc();
				numAlivePlayers--;

				if(koUID == netLobby.netPlayerClient.getPlayerUID()) {
					playerKObyYou[playerID] = true;
					currentKO++;
				}
				if((seatID == playerSeatNumber) && (playerSeatNumber != -1)) {
					sendGameStat(owner.engine[playerID], playerID);
				}
			}
		}
		// game finished
		if(message[0].equals("finish")) {
			log.debug("Game Finished");

			isNetGameActive = false;
			isNetGameFinished = true;
			isNewcomer = false;
			netPlayTimerActive = false;

			if(currentRoomInfo != null)
				currentRoomInfo.playing = false;

			if(isPractice) {
				isPractice = false;
				owner.bgmStatus.bgm = BGMStatus.BGM_NOTHING;
				owner.engine[0].gameEnded();
				owner.engine[0].stat = GameEngine.STAT_SETTING;
				owner.engine[0].resetStatc();
			}

			boolean flagTeamWin = Boolean.parseBoolean(message[4]);

			if(flagTeamWin) {
				//String strTeam = NetUtil.urlDecode(message[3]);
				for(int i = 0; i < MAX_PLAYERS; i++) {
					if(isPlayerExist[i] && !isDead[i]) {
						playerPlace[i] = 1;
						owner.engine[i].gameEnded();
						owner.engine[i].stat = GameEngine.STAT_EXCELLENT;
						owner.engine[i].resetStatc();
						owner.engine[i].statistics.time = netPlayTimer;
						numAlivePlayers--;

						if((i == 0) && (playerSeatNumber != -1)) {
							numWins++;
							sendGameStat(owner.engine[i], i);
						}
					}
				}
			} else {
				int seatID = Integer.parseInt(message[2]);
				if(seatID != -1) {
					int playerID = getPlayerIDbySeatID(seatID);
					if(isPlayerExist[playerID]) {
						playerPlace[playerID] = 1;
						owner.engine[playerID].gameEnded();
						owner.engine[playerID].stat = GameEngine.STAT_EXCELLENT;
						owner.engine[playerID].resetStatc();
						owner.engine[playerID].statistics.time = netPlayTimer;
						numAlivePlayers--;

						if((seatID == playerSeatNumber) && (playerSeatNumber != -1)) {
							numWins++;
							sendGameStat(owner.engine[playerID], playerID);
						}
					}
				}
			}

			if((playerSeatNumber == -1) || (playerPlace[0] >= 3)) {
				owner.receiver.playSE("matchend");
			}

			updatePlayerExist();
			updatePlayerNames();
		}
		// game messages
		if(message[0].equals("game")) {
			int uid = Integer.parseInt(message[1]);
			int seatID = Integer.parseInt(message[2]);
			int playerID = getPlayerIDbySeatID(seatID);

			if(owner.engine[playerID].field == null) {
				owner.engine[playerID].field = new Field();
			}

			// Field without attributes
			if(message[3].equals("field")) {
				if(message.length > 7) {
					owner.engine[playerID].nowPieceObject = null;
					owner.engine[playerID].holdDisable = false;
					garbage[playerID] = Integer.parseInt(message[4]);
					int skin = Integer.parseInt(message[5]);
					int highestGarbageY = Integer.parseInt(message[6]);
					int highestWallY = Integer.parseInt(message[7]);
					playerSkin[playerID] = skin;
					if(message.length > 9) {
						String strFieldData = message[8];
						boolean isCompressed = Boolean.parseBoolean(message[9]);
						if(isCompressed) {
							strFieldData = NetUtil.decompressString(strFieldData);
						}
						owner.engine[playerID].field.stringToField(strFieldData, skin, highestGarbageY, highestWallY);
					} else {
						owner.engine[playerID].field.reset();
					}
				}
			}
			// Field with attributes
			if(message[3].equals("fieldattr")) {
				if(message.length > 5) {
					owner.engine[playerID].nowPieceObject = null;
					owner.engine[playerID].holdDisable = false;
					garbage[playerID] = Integer.parseInt(message[4]);
					int skin = Integer.parseInt(message[5]);
					playerSkin[playerID] = skin;
					if(message.length > 7) {
						String strFieldData = message[6];
						boolean isCompressed = Boolean.parseBoolean(message[7]);
						if(isCompressed) {
							strFieldData = NetUtil.decompressString(strFieldData);
						}
						owner.engine[playerID].field.attrStringToField(strFieldData, skin);
					} else {
						owner.engine[playerID].field.reset();
					}
				}
			}
			// 操作中Block
			if(message[3].equals("piece")) {
				int id = Integer.parseInt(message[4]);

				if(id >= 0) {
					int pieceX = Integer.parseInt(message[5]);
					int pieceY = Integer.parseInt(message[6]);
					int pieceDir = Integer.parseInt(message[7]);
					//int pieceBottomY = Integer.parseInt(message[8]);
					int pieceColor = Integer.parseInt(message[9]);
					int pieceSkin = Integer.parseInt(message[10]);
					boolean pieceBig = (message.length > 11) ? Boolean.parseBoolean(message[11]) : false;

					owner.engine[playerID].nowPieceObject = new Piece(id);
					owner.engine[playerID].nowPieceObject.direction = pieceDir;
					owner.engine[playerID].nowPieceObject.setAttribute(Block.BLOCK_ATTRIBUTE_VISIBLE, true);
					owner.engine[playerID].nowPieceObject.setColor(pieceColor);
					owner.engine[playerID].nowPieceObject.setSkin(pieceSkin);
					owner.engine[playerID].nowPieceX = pieceX;
					owner.engine[playerID].nowPieceY = pieceY;
					//owner.engine[playerID].nowPieceBottomY = pieceBottomY;
					owner.engine[playerID].nowPieceObject.big = pieceBig;
					owner.engine[playerID].nowPieceObject.updateConnectData();
					owner.engine[playerID].nowPieceBottomY =
						owner.engine[playerID].nowPieceObject.getBottom(pieceX, pieceY, owner.engine[playerID].field);

					if(owner.engine[playerID].stat != GameEngine.STAT_EXCELLENT) {
						owner.engine[playerID].stat = GameEngine.STAT_MOVE;
						owner.engine[playerID].statc[0] = 2;
					}

					playerSkin[playerID] = pieceSkin;
				} else {
					owner.engine[playerID].nowPieceObject = null;
				}

				if((playerSeatNumber == -1) && (!netPlayTimerActive) && (!isNetGameFinished) && (!isNewcomer)) {
					netPlayTimerActive = true;
					netPlayTimer = 0;
				}

				if((playerSeatNumber != -1) && (netPlayTimerActive) && (!isPractice) &&
				   (owner.engine[0].stat == GameEngine.STAT_READY) && (owner.engine[0].statc[0] < owner.engine[0].goEnd))
				{
					owner.engine[0].statc[0] = owner.engine[0].goEnd;
				}
			}
//			if((message[3].equals("tank")) && (useTankMode == true)){
//				String teamName = playerTeams[playerID];
//				for(int i = 0; i < MAX_PLAYERS; i++ ){
//					if((playerTeams[i].length() > 0) && (isPlayerExist[i]) && (playerTeams[i].equals(teamName))) {
//						if(i != playerID) {
//							playerTeamsIsTank[i] = false;
//						}
//					}
//
//				}
//				isTank = true;
//				playerTeamsIsTank[playerID] = true;
//			}
			//  Attack
			if(message[3].equals("attack")) {
				//int pts = Integer.parseInt(message[4]);
				int[] pts = new int[ATTACK_CATEGORIES];
				int sumPts = 0;

				for(int i = 0; i < ATTACK_CATEGORIES; i++){
					pts[i] = Integer.parseInt(message[4+i]);
					sumPts += pts[i];
				}

				lastevent[playerID] = Integer.parseInt(message[ATTACK_CATEGORIES + 5]);
				lastb2b[playerID] = Boolean.parseBoolean(message[ATTACK_CATEGORIES + 6]);
				lastcombo[playerID] = Integer.parseInt(message[ATTACK_CATEGORIES + 7]);
				garbage[playerID] = Integer.parseInt(message[ATTACK_CATEGORIES + 8]);
				lastpiece[playerID] = Integer.parseInt(message[ATTACK_CATEGORIES + 9]);
				scgettime[playerID] = 0;

				if( (playerSeatNumber != -1) && (owner.engine[0].timerActive) && (sumPts > 0) && (!isPractice) && (!isNewcomer) &&
					    ((playerTeams[0].length() <= 0) || (playerTeams[playerID].length() <= 0) || !playerTeams[0].equalsIgnoreCase(playerTeams[playerID])))
//				if( (playerSeatNumber != -1) && (owner.engine[0].timerActive) && (sumPts > 0) && (!isPractice) && (!isNewcomer) &&
//				    ((playerTeams[0].length() <= 0) || (playerTeams[playerID].length() <= 0) || (!playerTeams[0].equalsIgnoreCase(playerTeams[playerID]))) &&
//				    (playerTeamsIsTank[0]) )
				{
					int secondAdd = 0; //TODO: Allow for chunking of attack types other than b2b.
					if(currentRoomInfo.b2bChunk){
						secondAdd = pts[ATTACK_CATEGORY_B2B];
					}

					GarbageEntry garbageEntry = new GarbageEntry(sumPts - secondAdd, playerID, uid);
					garbageEntries.add(garbageEntry);

					if(secondAdd > 0){
						garbageEntry = new GarbageEntry(secondAdd, playerID, uid);
						garbageEntries.add(garbageEntry);
					}

					garbage[0] = getTotalGarbageLines();
					if(garbage[0] >= 4*GARBAGE_DENOMINATOR) owner.engine[0].playSE("danger");
					netLobby.netPlayerClient.send("game\tgarbageupdate\t" + garbage[0] + "\n");
				}
			}
			// せり上がりバー更新
			if(message[3].equals("garbageupdate")) {
				garbage[playerID] = Integer.parseInt(message[4]);
			}
			// NEXT and HOLD
			if(message[3].equals("next")) {
				int maxNext = Integer.parseInt(message[4]);
				owner.engine[playerID].ruleopt.nextDisplay = maxNext;
				owner.engine[playerID].holdDisable = Boolean.parseBoolean(message[5]);

				for(int i = 0; i < maxNext + 1; i++) {
					if(i + 6 < message.length) {
						String[] strPieceData = message[i + 6].split(";");
						int pieceID = Integer.parseInt(strPieceData[0]);
						int pieceDirection = Integer.parseInt(strPieceData[1]);
						int pieceColor = Integer.parseInt(strPieceData[2]);

						if(i == 0) {
							if(pieceID == Piece.PIECE_NONE) {
								owner.engine[playerID].holdPieceObject = null;
							} else {
								owner.engine[playerID].holdPieceObject = new Piece(pieceID);
								owner.engine[playerID].holdPieceObject.direction = pieceDirection;
								owner.engine[playerID].holdPieceObject.setColor(pieceColor);
								owner.engine[playerID].holdPieceObject.setSkin(playerSkin[playerID]);
								owner.engine[playerID].holdPieceObject.updateConnectData();
							}
						} else {
							if((owner.engine[playerID].nextPieceArrayObject == null) || (owner.engine[playerID].nextPieceArrayObject.length < maxNext)) {
								owner.engine[playerID].nextPieceArrayObject = new Piece[maxNext];
							}
							owner.engine[playerID].nextPieceArrayObject[i - 1] = new Piece(pieceID);
							owner.engine[playerID].nextPieceArrayObject[i - 1].direction = pieceDirection;
							owner.engine[playerID].nextPieceArrayObject[i - 1].setColor(pieceColor);
							owner.engine[playerID].nextPieceArrayObject[i - 1].setSkin(playerSkin[playerID]);
							owner.engine[playerID].nextPieceArrayObject[i - 1].updateConnectData();
						}
					}
				}

				owner.engine[playerID].isNextVisible = true;
				owner.engine[playerID].isHoldVisible = true;
			}
			// HurryUp
			if(message[3].equals("hurryup")) {
				if(!hurryupStarted && (hurryupSeconds > 0)) {
					if((playerSeatNumber != -1) && (owner.engine[0].timerActive)) {
						owner.receiver.playSE("hurryup");
					}
					hurryupStarted = true;
					hurryupShowFrames = 60 * 5;
				}
			}
		}
	}

	/**
	 * 敵から送られてきたgarbage blockの data
	 */
	private class GarbageEntry {
		/** garbage blockcount */
		public int lines = 0;

		/** 送信元(ゲーム用Player number) */
		public int playerID = 0;

		/** 送信元(ゲーム以外用Player number) */
		public int uid = 0;

		/**
		 * Constructor
		 */
		@SuppressWarnings("unused")
		public GarbageEntry() {
		}

		/**
		 * パラメータ付きConstructor
		 * @param g garbage blockcount
		 */
		@SuppressWarnings("unused")
		public GarbageEntry(int g) {
			lines = g;
		}

		/**
		 * パラメータ付きConstructor
		 * @param g garbage blockcount
		 * @param p 送信元(ゲーム用Player number)
		 */
		public GarbageEntry(int g, int p) {
			lines = g;
			playerID = p;
		}

		/**
		 * パラメータ付きConstructor
		 * @param g garbage blockcount
		 * @param p 送信元(ゲーム用Player number)
		 * @param s 送信元(ゲーム以外用Player number)
		 */
		public GarbageEntry(int g, int p, int s) {
			lines = g;
			playerID = p;
			uid = s;
		}
	}
}
