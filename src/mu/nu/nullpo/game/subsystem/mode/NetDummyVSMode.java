package mu.nu.nullpo.game.subsystem.mode;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;

import mu.nu.nullpo.game.component.BGMStatus;
import mu.nu.nullpo.game.component.Block;
import mu.nu.nullpo.game.component.Controller;
import mu.nu.nullpo.game.event.EventReceiver;
import mu.nu.nullpo.game.net.NetPlayerClient;
import mu.nu.nullpo.game.net.NetPlayerInfo;
import mu.nu.nullpo.game.net.NetRoomInfo;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.game.subsystem.wallkick.Wallkick;
import mu.nu.nullpo.gui.net.NetLobbyFrame;
import mu.nu.nullpo.util.GeneralUtil;
import net.omegaboshi.nullpomino.game.subsystem.randomizer.Randomizer;

/**
 * Special base class for netplay VS modes. Up to 6 players supported.
 */
public class NetDummyVSMode extends NetDummyMode {
	/* -------------------- Constants -------------------- */
	/** NET-VS: Max number of players */
	protected static final int NETVS_MAX_PLAYERS = 6;

	/** NET-VS: Numbers of seats numbers corresponding to frames on player's screen */
	protected static final int[][] NETVS_GAME_SEAT_NUMBERS =
	{
		{0,1,2,3,4,5},
		{1,0,2,3,4,5},
		{1,2,0,3,4,5},
		{1,2,3,0,4,5},
		{1,2,3,4,0,5},
		{1,2,3,4,5,0},
	};

	/** NET-VS: Each player's garbage block color */
	protected static final int[] NETVS_PLAYER_COLOR_BLOCK = {
		Block.BLOCK_COLOR_RED, Block.BLOCK_COLOR_BLUE, Block.BLOCK_COLOR_GREEN,
		Block.BLOCK_COLOR_YELLOW, Block.BLOCK_COLOR_PURPLE, Block.BLOCK_COLOR_CYAN
	};

	/** NET-VS: Each player's frame color */
	protected static final int[] NETVS_PLAYER_COLOR_FRAME = {
		GameEngine.FRAME_COLOR_RED, GameEngine.FRAME_COLOR_BLUE, GameEngine.FRAME_COLOR_GREEN,
		GameEngine.FRAME_COLOR_YELLOW, GameEngine.FRAME_COLOR_PURPLE, GameEngine.FRAME_COLOR_CYAN
	};

	/** NET-VS: Team font colors */
	protected static final int[] NETVS_TEAM_FONT_COLORS = {
		EventReceiver.COLOR_WHITE,
		EventReceiver.COLOR_RED, EventReceiver.COLOR_GREEN, EventReceiver.COLOR_BLUE, EventReceiver.COLOR_YELLOW,
		EventReceiver.COLOR_PURPLE, EventReceiver.COLOR_CYAN
	};

	/** NET-VS: Default time before forced piece lock */
	protected static final int NETVS_PIECE_AUTO_LOCK_TIME = 30 * 60;

	/* -------------------- Variables -------------------- */
	/** NET-VS: Local player's seat ID (-1:Spectator) */
	protected int netvsMySeatID;

	/** NET-VS: Number of players */
	protected int netvsNumPlayers;

	/** NET-VS: Number of players in current game */
	protected int netvsNumNowPlayers;

	/** NET-VS: Number of players still alive in current game */
	protected int netvsNumAlivePlayers;

	/** NET-VS: Player exist flag */
	protected boolean[] netvsPlayerExist;

	/** NET-VS: Player ready flag */
	protected boolean[] netvsPlayerReady;

	/** NET-VS: Player dead flag */
	protected boolean[] netvsPlayerDead;

	/** NET-VS: Player active flag (false if newcomer) */
	protected boolean[] netvsPlayerActive;

	/** NET-VS: Player's Seat ID array (-1:No Player) */
	protected int[] netvsPlayerSeatID;

	/** NET-VS: Player's place */
	protected int[] netvsPlayerPlace;

	/** NET-VS: Player's win count */
	protected int[] netvsPlayerWinCount;

	/** NET-VS: Player's game count */
	protected int[] netvsPlayerPlayCount;

	/** NET-VS: Player's team colors */
	protected int[] netvsPlayerTeamColor;

	/** NET-VS: Player names */
	protected String[] netvsPlayerName;

	/** NET-VS: Player team names */
	protected String[] netvsPlayerTeam;

	/** NET-VS: Player's skins */
	protected int[] netvsPlayerSkin;

	/** NET-VS: true if it's ready to show player's result */
	protected boolean[] netvsPlayerResultReceived;

	/** NET-VS: true if automatic start timer is activated */
	protected boolean netvsAutoStartTimerActive;

	/** NET-VS: Time left until the game starts automatically */
	protected int netvsAutoStartTimer;

	/** NET-VS: true if room game is in progress */
	protected boolean netvsIsGameActive;

	/** NET-VS: true if room game is finished */
	protected boolean netvsIsGameFinished;

	/** NET-VS: true if waiting for ready status change */
	protected boolean netvsIsReadyChangePending;

	/** NET-VS: true if waiting for dead status change */
	protected boolean netvsIsDeadPending;

	/** NET-VS: true if local player joined game in progress */
	protected boolean netvsIsNewcomer;

	/** NET-VS: Elapsed timer active flag */
	protected boolean netvsPlayTimerActive;

	/** NET-VS: Elapsed time */
	protected int netvsPlayTimer;

	/** NET-VS: true if practice mode */
	protected boolean netvsIsPractice;

	/** NET-VS: true if can exit from practice game */
	protected boolean netvsIsPracticeExitAllowed;

	/** NET-VS: How long current piece is active */
	protected int netvsPieceMoveTimer;

	/** NET-VS: Time before forced piece lock */
	protected int netvsPieceMoveTimerMax;

	/** NET-VS: Map number to use */
	protected int netvsMapNo;

	/** NET-VS: Random for selecting map in Practice mode */
	protected Random netvsRandMap;

	/** NET-VS: Practice mode last used map number */
	protected int netvsMapPreviousPracticeMap;

	/** NET-VS: UID of player who attacked local player last (-1: Suicide or Unknown) */
	protected int netvsLastAttackerUID;

	/*
	 * Mode Name
	 */
	@Override
	public String getName() {
		return "NET-VS-DUMMY";
	}

	@Override
	public boolean isVSMode() {
		return true;
	}

	/**
	 * NET-VS: Number of players
	 */
	@Override
	public int getPlayers() {
		return NETVS_MAX_PLAYERS;
	}

	/**
	 * NET-VS: This is netplay-only mode
	 */
	@Override
	public boolean isNetplayMode() {
		return true;
	}

	/**
	 * NET-VS: Mode Initialization
	 */
	@Override
	public void modeInit(GameManager manager) {
		super.modeInit(manager);
		log.debug("modeInit() on NetDummyVSMode");
		netForceSendMovements = true;
		netvsMySeatID = -1;
		netvsNumPlayers = 0;
		netvsNumNowPlayers = 0;
		netvsNumAlivePlayers = 0;
		netvsPlayerExist = new boolean[NETVS_MAX_PLAYERS];
		netvsPlayerReady = new boolean[NETVS_MAX_PLAYERS];
		netvsPlayerActive = new boolean[NETVS_MAX_PLAYERS];
		netvsPlayerSeatID = new int[NETVS_MAX_PLAYERS];
		netvsPlayerWinCount = new int[NETVS_MAX_PLAYERS];
		netvsPlayerPlayCount = new int[NETVS_MAX_PLAYERS];
		netvsPlayerTeamColor = new int[NETVS_MAX_PLAYERS];
		netvsPlayerName = new String[NETVS_MAX_PLAYERS];
		netvsPlayerTeam = new String[NETVS_MAX_PLAYERS];
		netvsPlayerSkin = new int[NETVS_MAX_PLAYERS];
		for(int i = 0; i < NETVS_MAX_PLAYERS; i++) netvsPlayerSkin[i] = -1;
		netvsAutoStartTimerActive = false;
		netvsAutoStartTimer = 0;
		netvsPieceMoveTimerMax = NETVS_PIECE_AUTO_LOCK_TIME;
		netvsMapPreviousPracticeMap = -1;
		netvsResetFlags();
	}

	/**
	 * NET-VS: Init some variables
	 */
	protected void netvsResetFlags() {
		netvsPlayerResultReceived = new boolean[NETVS_MAX_PLAYERS];
		netvsPlayerDead = new boolean[NETVS_MAX_PLAYERS];
		netvsPlayerPlace = new int[NETVS_MAX_PLAYERS];
		netvsIsGameActive = false;
		netvsIsGameFinished = false;
		netvsIsReadyChangePending = false;
		netvsIsDeadPending = false;
		netvsIsNewcomer = false;
		netvsPlayTimerActive = false;
		netvsPlayTimer = 0;
		netvsIsPractice = false;
		netvsIsPracticeExitAllowed = false;
		netvsPieceMoveTimer = 0;
	}

	/**
	 * NET-VS: Initialization for each player
	 */
	@Override
	public void playerInit(GameEngine engine, int playerID) {
		netPlayerInit(engine, playerID);
	}

	/**
	 * @return true if watch mode
	 */
	protected boolean netvsIsWatch() {
		try {
			return (netLobby.netPlayerClient.getYourPlayerInfo().seatID == -1);
		} catch (Exception e) {}
		return false;
	}

	/**
	 * NET-VS: Update player variables
	 */
	@Override
	protected void netUpdatePlayerExist() {
		netvsMySeatID = netLobby.netPlayerClient.getYourPlayerInfo().seatID;
		netvsNumPlayers = 0;
		netNumSpectators = 0;
		netPlayerName = netLobby.netPlayerClient.getPlayerName();
		netIsWatch = netvsIsWatch();

		for(int i = 0; i < NETVS_MAX_PLAYERS; i++) {
			netvsPlayerExist[i] = false;
			netvsPlayerReady[i] = false;
			netvsPlayerActive[i] = false;
			netvsPlayerSeatID[i] = -1;
			netvsPlayerWinCount[i] = 0;
			netvsPlayerPlayCount[i] = 0;
			netvsPlayerName[i] = "";
			netvsPlayerTeam[i] = "";
			owner.engine[i].framecolor = GameEngine.FRAME_COLOR_GRAY;
		}

		LinkedList<NetPlayerInfo> pList = netLobby.updateSameRoomPlayerInfoList();
		LinkedList<String> teamList = new LinkedList<String>();

		for(NetPlayerInfo pInfo: pList) {
			if(pInfo.roomID == netCurrentRoomInfo.roomID) {
				if(pInfo.seatID == -1) {
					netNumSpectators++;
				} else {
					netvsNumPlayers++;

					int playerID = netvsGetPlayerIDbySeatID(pInfo.seatID);
					netvsPlayerExist[playerID] = true;
					netvsPlayerReady[playerID] = pInfo.ready;
					netvsPlayerActive[playerID] = pInfo.playing;
					netvsPlayerSeatID[playerID] = pInfo.seatID;
					netvsPlayerWinCount[playerID] = pInfo.winCountNow;
					netvsPlayerPlayCount[playerID] = pInfo.playCountNow;
					netvsPlayerName[playerID] = pInfo.strName;
					netvsPlayerTeam[playerID] = pInfo.strTeam;

					// Set frame color
					if(pInfo.seatID < NETVS_PLAYER_COLOR_FRAME.length) {
						owner.engine[playerID].framecolor = NETVS_PLAYER_COLOR_FRAME[pInfo.seatID];
					}

					// Set team color
					if(netvsPlayerTeam[playerID].length() > 0) {
						if(!teamList.contains(netvsPlayerTeam[playerID])) {
							teamList.add(netvsPlayerTeam[playerID]);
							netvsPlayerTeamColor[playerID] = teamList.size();
						} else {
							netvsPlayerTeamColor[playerID] = teamList.indexOf(netvsPlayerTeam[playerID]) + 1;
						}
					}
				}
			}
		}
	}

	/**
	 * NET-VS: When you join the room
	 */
	@Override
	protected void netOnJoin(NetLobbyFrame lobby, NetPlayerClient client, NetRoomInfo roomInfo) {
		log.debug("netOnJoin() on NetDummyVSMode");

		netCurrentRoomInfo = roomInfo;
		netIsNetPlay = true;
		netvsIsNewcomer = netCurrentRoomInfo.playing;

		netUpdatePlayerExist();
		netvsSetLockedRule();
		netvsSetGameScreenLayout();

		if(netvsIsNewcomer) {
			netvsNumNowPlayers = netvsNumPlayers;
		}
	}

	/**
	 * NET-VS: Initialize various NetPlay variables. Usually called from playerInit.
	 */
	@Override
	protected void netPlayerInit(GameEngine engine, int playerID) {
		log.debug("netPlayerInit(engine, " + playerID + ") on NetDummyVSMode");

		super.netPlayerInit(engine, playerID);

		// Misc. variables
		engine.fieldWidth = 10;
		engine.fieldHeight = 20;
		engine.gameoverAll = false;
		engine.allowTextRenderByReceiver = true;
	}

	/**
	 * NET-VS: Draw player's name
	 */
	@Override
	protected void netDrawPlayerName(GameEngine engine) {
		int playerID = engine.playerID;
		int x = owner.receiver.getFieldDisplayPositionX(engine, playerID);
		int y = owner.receiver.getFieldDisplayPositionY(engine, playerID);

		if((netvsPlayerName != null) && (netvsPlayerName[playerID] != null) && (netvsPlayerName[playerID].length() > 0)) {
			String name = netvsPlayerName[playerID];
			int fontcolorNum = netvsPlayerTeamColor[playerID];
			if(fontcolorNum < 0) fontcolorNum = 0;
			if(fontcolorNum > NETVS_TEAM_FONT_COLORS.length - 1) fontcolorNum = NETVS_TEAM_FONT_COLORS.length - 1;
			int fontcolor = NETVS_TEAM_FONT_COLORS[fontcolorNum];

			if(engine.displaysize == -1) {
				if(name.length() > 7) name = name.substring(0, 7) + "..";
				owner.receiver.drawTTFDirectFont(engine, playerID, x, y - 16, name, fontcolor);
			} else if(playerID == 0) {
				if(name.length() > 14) name = name.substring(0, 14) + "..";
				owner.receiver.drawTTFDirectFont(engine, playerID, x, y - 20, name, fontcolor);
			} else {
				owner.receiver.drawTTFDirectFont(engine, playerID, x, y - 20, name, fontcolor);
			}
		}
	}

	/**
	 * NET-VS: Send field to everyone. It won't do anything in practice game.
	 */
	@Override
	protected void netSendField(GameEngine engine) {
		if(!netvsIsPractice && (engine.playerID == 0) && (!netIsWatch)) {
			super.netSendField(engine);
		}
	}

	/**
	 * NET-VS: Send next and hold piece informations to everyone. It won't do anything in practice game.
	 */
	@Override
	protected void netSendNextAndHold(GameEngine engine) {
		if(!netvsIsPractice && (engine.playerID == 0) && (!netIsWatch)) {
			super.netSendNextAndHold(engine);
		}
	}

	/**
	 * NET-VS: Send the current piece's movement to everyone. It won't do anything in practice game.
	 */
	@Override
	protected boolean netSendPieceMovement(GameEngine engine, boolean forceSend) {
		if(!netvsIsPractice && (engine.playerID == 0) && (!netIsWatch)) {
			return super.netSendPieceMovement(engine, forceSend);
		}
		return false;
	}

	/**
	 * NET-VS: Set locked rule/Revert to user rule
	 */
	protected void netvsSetLockedRule() {
		if((netCurrentRoomInfo != null) && (netCurrentRoomInfo.ruleLock)) {
			// Set to locked rule
			if((netLobby != null) && (netLobby.ruleOptLock != null)) {
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
		} else if(!netvsIsWatch()) {
			// Revert rules
			owner.engine[0].ruleopt.copy(netLobby.ruleOptPlayer);
			owner.engine[0].randomizer = GeneralUtil.loadRandomizer(owner.engine[0].ruleopt.strRandomizer);
			owner.engine[0].wallkick = GeneralUtil.loadWallkick(owner.engine[0].ruleopt.strWallkick);
		}
	}

	/**
	 * Set game screen layout
	 */
	protected void netvsSetGameScreenLayout() {
		for(int i = 0; i < getPlayers(); i++) {
			netvsSetGameScreenLayout(owner.engine[i]);
		}
	}

	/**
	 * Set game screen layout
	 * @param engine GameEngine
	 */
	protected void netvsSetGameScreenLayout(GameEngine engine) {
		// Set display size
		if( ((engine.playerID == 0) && !netvsIsWatch()) ||
			((netCurrentRoomInfo != null) && (netCurrentRoomInfo.maxPlayers == 2) && (engine.playerID <= 1)) )
		{
			engine.displaysize = 0;
			engine.enableSE = true;
		} else {
			engine.displaysize = -1;
			engine.enableSE = false;
		}

		// Set visible flag
		if((netCurrentRoomInfo != null) && (engine.playerID >= netCurrentRoomInfo.maxPlayers)) {
			engine.isVisible = false;
		}

		// Set frame color
		int seatID = netvsPlayerSeatID[engine.playerID];
		if((seatID >= 0) && (seatID < NETVS_PLAYER_COLOR_FRAME.length)) {
			engine.framecolor = NETVS_PLAYER_COLOR_FRAME[seatID];
		} else {
			engine.framecolor = GameEngine.FRAME_COLOR_GRAY;
		}
	}

	/**
	 * NET-VS: Apply room's settings (such as gravity) to all GameEngine
	 */
	protected void netvsApplyRoomSettings() {
		for(int i = 0; i < getPlayers(); i++) {
			netvsApplyRoomSettings(owner.engine[i]);
		}
	}

	/**
	 * NET-VS: Apply room's settings (such as gravity) to the specific GameEngine
	 * @param engine GameEngine to apply settings
	 */
	protected void netvsApplyRoomSettings(GameEngine engine) {
		if(netCurrentRoomInfo != null) {
			engine.speed.gravity = netCurrentRoomInfo.gravity;
			engine.speed.denominator = netCurrentRoomInfo.denominator;
			engine.speed.are = netCurrentRoomInfo.are;
			engine.speed.areLine = netCurrentRoomInfo.areLine;
			engine.speed.lineDelay = netCurrentRoomInfo.lineDelay;
			engine.speed.lockDelay = netCurrentRoomInfo.lockDelay;
			engine.speed.das = netCurrentRoomInfo.das;

			engine.b2bEnable = netCurrentRoomInfo.b2b;
			engine.comboType = netCurrentRoomInfo.combo ? GameEngine.COMBO_TYPE_NORMAL : GameEngine.COMBO_TYPE_DISABLE;

			if(netCurrentRoomInfo.tspinEnableType == 0) {
				engine.tspinEnable = false;
				engine.useAllSpinBonus = false;
			} else if(netCurrentRoomInfo.tspinEnableType == 1) {
				engine.tspinEnable = true;
				engine.useAllSpinBonus = false;
			} else if(netCurrentRoomInfo.tspinEnableType == 2) {
				engine.tspinEnable = true;
				engine.useAllSpinBonus = true;
			}
		}
	}

	/**
	 * NET-VS: Get player field number by seat ID
	 * @param seat The seat ID want to know
	 * @return Player number
	 */
	protected int netvsGetPlayerIDbySeatID(int seat) {
		return netvsGetPlayerIDbySeatID(seat, netvsMySeatID);
	}

	/**
	 * NET-VS: Get player field number by seat ID
	 * @param seat The seat ID want to know
	 * @param myseat Your seat number (-1 if spectator)
	 * @return Player number
	 */
	protected int netvsGetPlayerIDbySeatID(int seat, int myseat) {
		int myseat2 = myseat;
		if(myseat2 < 0) myseat2 = 0;
		return NETVS_GAME_SEAT_NUMBERS[myseat2][seat];
	}

	/**
	 * NET-VS: Start a practice game
	 * @param engine GameEngine
	 */
	protected void netvsStartPractice(GameEngine engine) {
		netvsIsPractice = true;
		netvsIsPracticeExitAllowed = false;

		engine.init();
		engine.stat = GameEngine.STAT_READY;
		engine.resetStatc();
		netUpdatePlayerExist();
		netvsSetGameScreenLayout();

		// Map
		if(netCurrentRoomInfo.useMap && (netLobby.mapList.size() > 0)) {
			if(netvsRandMap == null) netvsRandMap = new Random();

			int map = 0;
			int maxMap = netLobby.mapList.size();
			do {
				map = netvsRandMap.nextInt(maxMap);
			} while ((map == netvsMapPreviousPracticeMap) && (maxMap >= 2));
			netvsMapPreviousPracticeMap = map;

			engine.createFieldIfNeeded();
			engine.field.stringToField(netLobby.mapList.get(map));
			engine.field.setAllSkin(engine.getSkin());
			engine.field.setAllAttribute(Block.BLOCK_ATTRIBUTE_VISIBLE, true);
			engine.field.setAllAttribute(Block.BLOCK_ATTRIBUTE_OUTLINE, true);
			engine.field.setAllAttribute(Block.BLOCK_ATTRIBUTE_SELFPLACED, false);
		}
	}

	/**
	 * NET-VS: Receive end-of-game stats.<br>
	 * Game modes should implement this. However, there are some sample codes in NetDummyVSMode.
	 * @param message Message
	 */
	protected void netvsRecvEndGameStats(String[] message) {
		int seatID = Integer.parseInt(message[2]);
		int playerID = netvsGetPlayerIDbySeatID(seatID);

		if((playerID != 0) || (netvsIsWatch())) {
			netvsPlayerResultReceived[playerID] = true;
		}
	}

	/**
	 * Get number of teams alive (Each independence player will also count as a team)
	 * @return Number of teams alive
	 */
	protected int netvsGetNumberOfTeamsAlive() {
		LinkedList<String> listTeamName = new LinkedList<String>();
		int noTeamCount = 0;

		for(int i = 0; i < getPlayers(); i++) {
			if(netvsPlayerExist[i] && !netvsPlayerDead[i] && owner.engine[i].gameActive) {
				if(netvsPlayerTeam[i].length() > 0) {
					if(!listTeamName.contains(netvsPlayerTeam[i])) {
						listTeamName.add(netvsPlayerTeam[i]);
					}
				} else {
					noTeamCount++;
				}
			}
		}

		return noTeamCount + listTeamName.size();
	}

	/**
	 * Check if the given playerID can be attacked
	 * @param playerID Player ID (to attack)
	 * @return true if playerID can be attacked
	 */
	protected boolean netvsIsAttackable(int playerID) {
		// Can't attack self
		if(playerID <= 0) return false;

		// Doesn't exist?
		if(!netvsPlayerExist[playerID]) return false;
		// Dead?
		if(netvsPlayerDead[playerID]) return false;
		// Newcomer?
		if(!netvsPlayerActive[playerID]) return false;

		// Is teammate?
		String myTeam = netvsPlayerTeam[0];
		String thisTeam = netvsPlayerTeam[playerID];
		if((myTeam.length() > 0) && (thisTeam.length() > 0) && myTeam.equals(thisTeam)) {
			return false;
		}

		return true;
	}

	/**
	 * Draw room info box (number of players, number of spectators, etc) to somewhere on the screen
	 * @param engine GameEngine
	 * @param x X position
	 * @param y Y position
	 */
	protected void netvsDrawRoomInfoBox(GameEngine engine, int x, int y) {
		if(netCurrentRoomInfo != null) {
			owner.receiver.drawDirectFont(engine, 0, x, y +  0, "PLAYERS", EventReceiver.COLOR_CYAN, 0.5f);
			owner.receiver.drawDirectFont(engine, 0, x, y +  8, "" + netvsNumPlayers, EventReceiver.COLOR_WHITE, 0.5f);
			owner.receiver.drawDirectFont(engine, 0, x, y + 16, "SPECTATORS", EventReceiver.COLOR_CYAN, 0.5f);
			owner.receiver.drawDirectFont(engine, 0, x, y + 24, "" + netNumSpectators, EventReceiver.COLOR_WHITE, 0.5f);

			if(!netvsIsWatch()) {
				owner.receiver.drawDirectFont(engine, 0, x, y + 32, "MATCHES", EventReceiver.COLOR_CYAN, 0.5f);
				owner.receiver.drawDirectFont(engine, 0, x, y + 40, "" + netvsPlayerPlayCount[0], EventReceiver.COLOR_WHITE, 0.5f);
				owner.receiver.drawDirectFont(engine, 0, x, y + 48, "WINS", EventReceiver.COLOR_CYAN, 0.5f);
				owner.receiver.drawDirectFont(engine, 0, x, y + 56, "" + netvsPlayerWinCount[0], EventReceiver.COLOR_WHITE, 0.5f);
			}
		}
		owner.receiver.drawDirectFont(engine, 0, x, y + 72, "ALL ROOMS", EventReceiver.COLOR_GREEN, 0.5f);
		owner.receiver.drawDirectFont(engine, 0, x, y + 80, "" + netLobby.netPlayerClient.getRoomInfoList().size(), EventReceiver.COLOR_WHITE, 0.5f);
	}

	/**
	 * NET-VS: Settings screen
	 */
	@Override
	public boolean onSetting(GameEngine engine, int playerID) {
		if((netCurrentRoomInfo != null) && (playerID == 0) && (!netvsIsWatch())) {
			netvsPlayerExist[0] = true;

			engine.displaysize = 0;
			engine.enableSE = true;
			engine.isVisible = true;

			if((!netvsIsReadyChangePending) && (netvsNumPlayers >= 2) && (!netvsIsNewcomer) && (engine.statc[3] >= 5)) {
				// Ready ON
				if(engine.ctrl.isPush(Controller.BUTTON_A) && !netvsPlayerReady[0]) {
					engine.playSE("decide");
					netvsIsReadyChangePending = true;
					netLobby.netPlayerClient.send("ready\ttrue\n");
				}
				// Ready OFF
				if(engine.ctrl.isPush(Controller.BUTTON_B) && netvsPlayerReady[0]) {
					engine.playSE("decide");
					netvsIsReadyChangePending = true;
					netLobby.netPlayerClient.send("ready\tfalse\n");
				}
			}

			// Practice Mode
			if(engine.ctrl.isPush(Controller.BUTTON_F) && (engine.statc[3] >= 5)) {
				engine.playSE("decide");
				netvsStartPractice(engine);
				return true;
			}
		}

		// Random Map Preview
		if((netCurrentRoomInfo != null) && netCurrentRoomInfo.useMap && !netLobby.mapList.isEmpty()) {
			if(netvsPlayerExist[playerID]) {
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
			} else if((engine.field != null) && !engine.field.isEmpty()) {
				engine.field.reset();
			}
		}

		engine.statc[3]++;

		return true;
	}

	/**
	 * NET-VS: Render the settings screen
	 */
	@Override
	public void renderSetting(GameEngine engine, int playerID) {
		if(engine.isVisible == false) return;

		int x = owner.receiver.getFieldDisplayPositionX(engine, playerID);
		int y = owner.receiver.getFieldDisplayPositionY(engine, playerID);

		if(netCurrentRoomInfo != null) {
			if(netvsPlayerReady[playerID] && netvsPlayerExist[playerID]) {
				if(engine.displaysize != -1)
					owner.receiver.drawDirectFont(engine, playerID, x + 68, y + 204, "OK", EventReceiver.COLOR_YELLOW);
				else
					owner.receiver.drawDirectFont(engine, playerID, x + 36, y + 80, "OK", EventReceiver.COLOR_YELLOW, 0.5f);
			}

			if((playerID == 0) && !netvsIsWatch() && (!netvsIsReadyChangePending) && (netvsNumPlayers >= 2) && !netvsIsNewcomer) {
				if(!netvsPlayerReady[playerID]) {
					String strTemp = "A(" + owner.receiver.getKeyNameByButtonID(engine, Controller.BUTTON_A) + " KEY):";
					if(strTemp.length() > 10) strTemp = strTemp.substring(0, 10);
					owner.receiver.drawMenuFont(engine, playerID, 0, 16, strTemp, EventReceiver.COLOR_CYAN);
					owner.receiver.drawMenuFont(engine, playerID, 1, 17, "READY", EventReceiver.COLOR_CYAN);
				} else {
					String strTemp = "B(" + owner.receiver.getKeyNameByButtonID(engine, Controller.BUTTON_B) + " KEY):";
					if(strTemp.length() > 10) strTemp = strTemp.substring(0, 10);
					owner.receiver.drawMenuFont(engine, playerID, 0, 16, strTemp, EventReceiver.COLOR_BLUE);
					owner.receiver.drawMenuFont(engine, playerID, 1, 17, "CANCEL", EventReceiver.COLOR_BLUE);
				}
			}
		}

		if((playerID == 0) && !netvsIsWatch() && (engine.statc[3] >= 5)) {
			String strTemp = "F(" + owner.receiver.getKeyNameByButtonID(engine, Controller.BUTTON_F) + " KEY):";
			if(strTemp.length() > 10) strTemp = strTemp.substring(0, 10);
			strTemp = strTemp.toUpperCase();
			owner.receiver.drawMenuFont(engine, playerID, 0, 18, strTemp, EventReceiver.COLOR_PURPLE);
			owner.receiver.drawMenuFont(engine, playerID, 1, 19, "PRACTICE", EventReceiver.COLOR_PURPLE);
		}
	}

	/**
	 * NET-VS: Ready
	 */
	@Override
	public boolean onReady(GameEngine engine, int playerID) {
		if(engine.statc[0] == 0) {
			// Map
			if(netCurrentRoomInfo.useMap && (netvsMapNo < netLobby.mapList.size()) && !netvsIsPractice) {
				engine.createFieldIfNeeded();
				engine.field.stringToField(netLobby.mapList.get(netvsMapNo));
				if((playerID == 0) && (!netvsIsWatch())) {
					engine.field.setAllSkin(engine.getSkin());
				} else if(netCurrentRoomInfo.ruleLock && (netLobby.ruleOptLock != null)) {
					engine.field.setAllSkin(netLobby.ruleOptLock.skin);
				} else if(netvsPlayerSkin[playerID] >= 0) {
					engine.field.setAllSkin(netvsPlayerSkin[playerID]);
				}
				engine.field.setAllAttribute(Block.BLOCK_ATTRIBUTE_VISIBLE, true);
				engine.field.setAllAttribute(Block.BLOCK_ATTRIBUTE_OUTLINE, true);
				engine.field.setAllAttribute(Block.BLOCK_ATTRIBUTE_SELFPLACED, false);
			}
		}

		if(netvsIsPractice && (engine.statc[0] >= 10)) {
			netvsIsPracticeExitAllowed = true;
		}

		return false;
	}

	/**
	 * NET-VS: Executed after Ready->Go, before the first piece appears.
	 */
	@Override
	public void startGame(GameEngine engine, int playerID) {
		netvsApplyRoomSettings(engine);

		if(playerID == 0) {
			// Set BGM
			if(netvsIsPractice) {
				owner.bgmStatus.bgm = BGMStatus.BGM_NOTHING;
			} else {
				owner.bgmStatus.bgm = BGMStatus.BGM_NORMAL1;
				owner.bgmStatus.fadesw = false;
			}

			// Init Variables
			netvsPieceMoveTimer = 0;
		}
	}

	/**
	 * NET-VS: When the pieces can move
	 */
	@Override
	public boolean onMove(GameEngine engine, int playerID) {
		// Stop game for remote players
		if((playerID != 0) || netvsIsWatch()) {
			return true;
		}

		// Timer start
		if((engine.ending == 0) && (engine.statc[0] == 0) && (engine.holdDisable == false) && (!netvsIsPractice))
			netvsPlayTimerActive = true;

		// Send movements
		super.onMove(engine, playerID);

		// Auto lock
		if((engine.ending == 0) && (engine.nowPieceObject != null) && (netvsPieceMoveTimerMax > 0)) {
			netvsPieceMoveTimer++;
			if(netvsPieceMoveTimer >= netvsPieceMoveTimerMax) {
				engine.nowPieceY = engine.nowPieceBottomY;
				engine.lockDelayNow = engine.getLockDelay();
				netvsPieceMoveTimer = 0;
			}
		}

		return false;
	}

	/**
	 * NET-VS: When the piece locked
	 */
	@Override
	public void pieceLocked(GameEngine engine, int playerID, int lines) {
		super.pieceLocked(engine, playerID, lines);
		netvsPieceMoveTimer = 0;
	}

	/**
	 * NET-VS: Executed at the end of each frame
	 */
	@Override
	public void onLast(GameEngine engine, int playerID) {
		super.onLast(engine, playerID);

		// Play Timer
		if((playerID == 0) && (netvsPlayTimerActive)) netvsPlayTimer++;

		// Automatic start timer
		if((playerID == 0) && (netCurrentRoomInfo != null) && (netvsAutoStartTimerActive) && (!netvsIsGameActive)) {
			if(netvsNumPlayers <= 1) {
				netvsAutoStartTimerActive = false;
			} else if(netvsAutoStartTimer > 0) {
				netvsAutoStartTimer--;
			} else {
				if(!netvsIsWatch()) {
					netLobby.netPlayerClient.send("autostart\n");
				}
				netvsAutoStartTimer = 0;
				netvsAutoStartTimerActive = false;
			}
		}

		// End practice mode
		if((playerID == 0) && (netvsIsPractice) && (netvsIsPracticeExitAllowed) && (engine.ctrl.isPush(Controller.BUTTON_F))) {
			netvsIsPractice = false;
			netvsIsPracticeExitAllowed = false;
			owner.bgmStatus.bgm = BGMStatus.BGM_NOTHING;
			engine.field.reset();
			engine.gameEnded();
			engine.stat = GameEngine.STAT_SETTING;
			engine.resetStatc();
		}
	}

	/**
	 * NET-VS: Render something such as HUD
	 */
	@Override
	public void renderLast(GameEngine engine, int playerID) {
		// Player count
		if(playerID == getPlayers() - 1) netDrawAllPlayersCount(engine);

		// Elapsed time
		if(playerID == 0) {
			owner.receiver.drawDirectFont(engine, 0, 256, 16, GeneralUtil.getTime(netvsPlayTimer));

			if(netvsIsPractice) {
				owner.receiver.drawDirectFont(engine, 0, 256, 32, GeneralUtil.getTime(engine.statistics.time), EventReceiver.COLOR_PURPLE);
			}
		}

		// Automatic start timer
		if((playerID == 0) && (netCurrentRoomInfo != null) && (netvsAutoStartTimerActive) && (!netvsIsGameActive)) {
			owner.receiver.drawDirectFont(engine, 0, 496, 16, GeneralUtil.getTime(netvsAutoStartTimer), netCurrentRoomInfo.autoStartTNET2,
					EventReceiver.COLOR_RED, EventReceiver.COLOR_YELLOW);
		}

		// Name
		netDrawPlayerName(engine);
	}

	/**
	 * NET-VS: Game Over
	 */
	@Override
	public boolean onGameOver(GameEngine engine, int playerID) {
		if(engine.statc[0] == 0) engine.gameEnded();
		engine.allowTextRenderByReceiver = false;

		// Practice
		if((playerID == 0) && (netvsIsPractice)) {
			if(engine.statc[0] < engine.field.getHeight() + 1) {
				return false;
			} else {
				engine.field.reset();
				engine.stat = GameEngine.STAT_RESULT;
				engine.resetStatc();
				return true;
			}
		}

		// 1P died
		if((playerID == 0) && (!netvsPlayerDead[playerID]) && (!netvsIsDeadPending) && !netvsIsWatch()) {
			owner.bgmStatus.bgm = BGMStatus.BGM_NOTHING;
			engine.resetFieldVisible();

			netSendField(engine);
			netSendNextAndHold(engine);
			netSendStats(engine);

			netLobby.netPlayerClient.send("dead\t" + netvsLastAttackerUID + "\n");

			netvsPlayerResultReceived[playerID] = true;
			netvsIsDeadPending = true;
			return true;
		}

		// Player/Opponent died
		if(netvsPlayerDead[playerID]) {
			if(engine.field == null) {
				engine.stat = GameEngine.STAT_SETTING;
				engine.resetStatc();
				return true;
			}
			if((engine.statc[0] < engine.field.getHeight() + 1) || (netvsPlayerResultReceived[playerID])) {
				return false;
			}
		}

		return true;
	}

	/**
	 * NET-VS: Draw Game Over screen
	 */
	@Override
	public void renderGameOver(GameEngine engine, int playerID) {
		if((playerID == 0) && (netvsIsPractice)) return;
		if(!engine.isVisible) return;

		int x = owner.receiver.getFieldDisplayPositionX(engine, playerID);
		int y = owner.receiver.getFieldDisplayPositionY(engine, playerID);
		int place = netvsPlayerPlace[playerID];

		if(engine.displaysize != -1) {
			if(netvsPlayerReady[playerID] && !netvsIsGameActive) {
				owner.receiver.drawDirectFont(engine, playerID, x + 68, y + 204, "OK", EventReceiver.COLOR_YELLOW);
			} else if((netvsNumNowPlayers == 2) || (netCurrentRoomInfo.maxPlayers == 2)) {
				owner.receiver.drawDirectFont(engine, playerID, x + 52, y + 204, "LOSE", EventReceiver.COLOR_WHITE);
			} else if(place == 1) {
				//owner.receiver.drawDirectFont(engine, playerID, x + 12, y + 204, "GAME OVER", EventReceiver.COLOR_WHITE);
			} else if(place == 2) {
				owner.receiver.drawDirectFont(engine, playerID, x + 12, y + 204, "2ND PLACE", EventReceiver.COLOR_WHITE);
			} else if(place == 3) {
				owner.receiver.drawDirectFont(engine, playerID, x + 12, y + 204, "3RD PLACE", EventReceiver.COLOR_RED);
			} else if(place == 4) {
				owner.receiver.drawDirectFont(engine, playerID, x + 12, y + 204, "4TH PLACE", EventReceiver.COLOR_GREEN);
			} else if(place == 5) {
				owner.receiver.drawDirectFont(engine, playerID, x + 12, y + 204, "5TH PLACE", EventReceiver.COLOR_BLUE);
			} else if(place == 6) {
				owner.receiver.drawDirectFont(engine, playerID, x + 12, y + 204, "6TH PLACE", EventReceiver.COLOR_PURPLE);
			}
		} else {
			if(netvsPlayerReady[playerID] && !netvsIsGameActive) {
				owner.receiver.drawDirectFont(engine, playerID, x + 36, y + 80, "OK", EventReceiver.COLOR_YELLOW, 0.5f);
			} else if((netvsNumNowPlayers == 2) || (netCurrentRoomInfo.maxPlayers == 2)) {
				owner.receiver.drawDirectFont(engine, playerID, x + 28, y + 80, "LOSE", EventReceiver.COLOR_WHITE, 0.5f);
			} else if(place == 1) {
				//owner.receiver.drawDirectFont(engine, playerID, x + 8, y + 80, "GAME OVER", EventReceiver.COLOR_WHITE, 0.5f);
			} else if(place == 2) {
				owner.receiver.drawDirectFont(engine, playerID, x + 8, y + 80, "2ND PLACE", EventReceiver.COLOR_WHITE, 0.5f);
			} else if(place == 3) {
				owner.receiver.drawDirectFont(engine, playerID, x + 8, y + 80, "3RD PLACE", EventReceiver.COLOR_RED, 0.5f);
			} else if(place == 4) {
				owner.receiver.drawDirectFont(engine, playerID, x + 8, y + 80, "4TH PLACE", EventReceiver.COLOR_GREEN, 0.5f);
			} else if(place == 5) {
				owner.receiver.drawDirectFont(engine, playerID, x + 8, y + 80, "5TH PLACE", EventReceiver.COLOR_BLUE, 0.5f);
			} else if(place == 6) {
				owner.receiver.drawDirectFont(engine, playerID, x + 8, y + 80, "6TH PLACE", EventReceiver.COLOR_PURPLE, 0.5f);
			}
		}
	}

	/**
	 * NET-VS: Excellent screen
	 */
	@Override
	public boolean onExcellent(GameEngine engine, int playerID) {
		engine.allowTextRenderByReceiver = false;
		if(playerID == 0) netvsPlayerResultReceived[playerID] = true;

		if(engine.statc[0] == 0) {
			engine.gameEnded();
			owner.bgmStatus.bgm = BGMStatus.BGM_NOTHING;
			engine.resetFieldVisible();
			engine.playSE("excellent");
		}

		if((engine.statc[0] >= 120) && (engine.ctrl.isPush(Controller.BUTTON_A))) {
			engine.statc[0] = engine.field.getHeight() + 1 + 180;
		}

		if(engine.statc[0] >= engine.field.getHeight() + 1 + 180) {
			if((!netvsIsGameActive) && (netvsPlayerResultReceived[playerID])) {
				if(engine.field != null) engine.field.reset();
				engine.resetStatc();
				engine.stat = GameEngine.STAT_RESULT;
			}
		} else {
			engine.statc[0]++;
		}

		return true;
	}

	/**
	 * NET-VS: Draw Excellent screen
	 */
	@Override
	public void renderExcellent(GameEngine engine, int playerID) {
		if(!engine.isVisible) return;

		int x = owner.receiver.getFieldDisplayPositionX(engine, playerID);
		int y = owner.receiver.getFieldDisplayPositionY(engine, playerID);

		if(engine.displaysize != -1) {
			if(netvsPlayerReady[playerID] && !netvsIsGameActive) {
				owner.receiver.drawDirectFont(engine, playerID, x + 68, y + 204, "OK", EventReceiver.COLOR_YELLOW);
			} else if((netvsNumNowPlayers == 2) || (netCurrentRoomInfo.maxPlayers == 2)) {
				owner.receiver.drawDirectFont(engine, playerID, x + 52, y + 204, "WIN!", EventReceiver.COLOR_YELLOW);
			} else {
				owner.receiver.drawDirectFont(engine, playerID, x + 4, y + 204, "1ST PLACE!", EventReceiver.COLOR_YELLOW);
			}
		} else {
			if(netvsPlayerReady[playerID] && !netvsIsGameActive) {
				owner.receiver.drawDirectFont(engine, playerID, x + 36, y + 80, "OK", EventReceiver.COLOR_YELLOW, 0.5f);
			} else if((netvsNumNowPlayers == 2) || (netCurrentRoomInfo.maxPlayers == 2)) {
				owner.receiver.drawDirectFont(engine, playerID, x + 28, y + 80, "WIN!", EventReceiver.COLOR_YELLOW, 0.5f);
			} else {
				owner.receiver.drawDirectFont(engine, playerID, x + 4, y + 80, "1ST PLACE!", EventReceiver.COLOR_YELLOW, 0.5f);
			}
		}
	}

	/**
	 * NET-VS: Results screen
	 */
	@Override
	public boolean onResult(GameEngine engine, int playerID) {
		engine.allowTextRenderByReceiver = false;

		if((playerID == 0) && (!netvsIsWatch())) {
			// To the settings screen
			if(engine.ctrl.isPush(Controller.BUTTON_A)) {
				engine.playSE("decide");
				netvsIsPractice = false;
				engine.stat = GameEngine.STAT_SETTING;
				engine.resetStatc();
				return true;
			}
			// Start Practice
			if(engine.ctrl.isPush(Controller.BUTTON_F)) {
				engine.playSE("decide");
				netvsStartPractice(engine);
				return true;
			}
		}

		return true;
	}

	/**
	 * NET-VS: Draw results screen
	 */
	@Override
	public void renderResult(GameEngine engine, int playerID) {
		float scale = 1.0f;
		if(engine.displaysize == -1) scale = 0.5f;

		// Place
		if(!netvsIsPractice) {
			owner.receiver.drawMenuFont(engine, playerID, 0, 0, "RESULT", EventReceiver.COLOR_ORANGE, scale);
			if(netvsPlayerPlace[playerID] == 1) {
				if(netvsNumNowPlayers == 2) {
					owner.receiver.drawMenuFont(engine, playerID, 6, 1, "WIN!", EventReceiver.COLOR_YELLOW, scale);
				} else {
					owner.receiver.drawMenuFont(engine, playerID, 6, 1, "1ST!", EventReceiver.COLOR_YELLOW, scale);
				}
			} else if(netvsPlayerPlace[playerID] == 2) {
				if(netvsNumNowPlayers == 2) {
					owner.receiver.drawMenuFont(engine, playerID, 6, 1, "LOSE", EventReceiver.COLOR_WHITE, scale);
				} else {
					owner.receiver.drawMenuFont(engine, playerID, 7, 1, "2ND", EventReceiver.COLOR_WHITE, scale);
				}
			} else if(netvsPlayerPlace[playerID] == 3) {
				owner.receiver.drawMenuFont(engine, playerID, 7, 1, "3RD", EventReceiver.COLOR_RED, scale);
			} else if(netvsPlayerPlace[playerID] == 4) {
				owner.receiver.drawMenuFont(engine, playerID, 7, 1, "4TH", EventReceiver.COLOR_GREEN, scale);
			} else if(netvsPlayerPlace[playerID] == 5) {
				owner.receiver.drawMenuFont(engine, playerID, 7, 1, "5TH", EventReceiver.COLOR_BLUE, scale);
			} else if(netvsPlayerPlace[playerID] == 6) {
				owner.receiver.drawMenuFont(engine, playerID, 7, 1, "6TH", EventReceiver.COLOR_DARKBLUE, scale);
			}
		} else {
			owner.receiver.drawMenuFont(engine, playerID, 0, 0, "PRACTICE", EventReceiver.COLOR_PINK, scale);
		}

		// Restart/Practice
		if((playerID == 0) && (!netvsIsWatch())) {
			String strTemp = "A(" + owner.receiver.getKeyNameByButtonID(engine, Controller.BUTTON_A) + " KEY):";
			if(strTemp.length() > 10) strTemp = strTemp.substring(0, 10);
			owner.receiver.drawMenuFont(engine, playerID, 0, 18, strTemp, EventReceiver.COLOR_RED);
			owner.receiver.drawMenuFont(engine, playerID, 1, 19, "RESTART", EventReceiver.COLOR_RED);

			String strTempF = "F(" + owner.receiver.getKeyNameByButtonID(engine, Controller.BUTTON_F) + " KEY):";
			if(strTempF.length() > 10) strTempF = strTempF.substring(0, 10);
			owner.receiver.drawMenuFont(engine, playerID, 0, 20, strTempF, EventReceiver.COLOR_PURPLE);
			if(!netvsIsPractice) {
				owner.receiver.drawMenuFont(engine, playerID, 1, 21, "PRACTICE", EventReceiver.COLOR_PURPLE);
			} else {
				owner.receiver.drawMenuFont(engine, playerID, 1, 21, "RETRY", EventReceiver.COLOR_PURPLE);
			}
		}
	}

	/**
	 * NET-VS: No retry key.
	 */
	@Override
	public void netplayOnRetryKey(GameEngine engine, int playerID) {
	}

	/**
	 * NET-VS: Disconnected
	 */
	@Override
	public void netlobbyOnDisconnect(NetLobbyFrame lobby, NetPlayerClient client, Throwable ex) {
		for(int i = 0; i < getPlayers(); i++) {
			owner.engine[i].stat = GameEngine.STAT_NOTHING;
		}
	}

	/**
	 * NET-VS: Message received
	 */
	@Override
	public void netlobbyOnMessage(NetLobbyFrame lobby, NetPlayerClient client, String[] message) throws IOException {
		// Player status update
		if(message[0].equals("playerupdate")) {
			NetPlayerInfo pInfo = new NetPlayerInfo(message[1]);

			// Ready status change
			if((pInfo.roomID == netCurrentRoomInfo.roomID) && (pInfo.seatID != -1)) {
				int playerID = netvsGetPlayerIDbySeatID(pInfo.seatID);

				if(netvsPlayerReady[playerID] != pInfo.ready) {
					netvsPlayerReady[playerID] = pInfo.ready;

					if((playerID == 0) && (!netvsIsWatch())) {
						netvsIsReadyChangePending = false;
					} else {
						if(pInfo.ready) owner.receiver.playSE("decide");
						else if(!pInfo.playing) owner.receiver.playSE("change");
					}
				}
			}

			netUpdatePlayerExist();
		}
		// When someone logout
		if(message[0].equals("playerlogout")) {
			NetPlayerInfo pInfo = new NetPlayerInfo(message[1]);

			if((pInfo.roomID == netCurrentRoomInfo.roomID) && (pInfo.seatID != -1)) {
				netUpdatePlayerExist();
			}
		}
		// Player status change (Join/Watch)
		if(message[0].equals("changestatus")) {
			int uid = Integer.parseInt(message[2]);

			netUpdatePlayerExist();
			netvsSetGameScreenLayout();

			if(uid == netLobby.netPlayerClient.getPlayerUID()) {
				netvsIsPractice = false;
				if(netvsIsGameActive && !netvsIsWatch()) {
					netvsIsNewcomer = true;
				}

				owner.engine[0].stat = GameEngine.STAT_SETTING;

				for(int i = 0; i < getPlayers(); i++) {
					if(owner.engine[i].field != null) {
						owner.engine[i].field.reset();
					}
					owner.engine[i].nowPieceObject = null;

					if((owner.engine[i].stat == GameEngine.STAT_NOTHING) || (!netvsIsGameActive)) {
						owner.engine[i].stat = GameEngine.STAT_SETTING;
					}
					owner.engine[i].resetStatc();
				}
			}
		}
		// Someone entered here
		if(message[0].equals("playerenter")) {
			int seatID = Integer.parseInt(message[3]);
			if((seatID != -1) && (netvsNumPlayers < 2)) {
				owner.receiver.playSE("levelstop");
			}
		}
		// Someone leave here
		if(message[0].equals("playerleave")) {
			netUpdatePlayerExist();

			if(netvsNumPlayers < 2) {
				netvsAutoStartTimerActive = false;
			}
		}
		// Automatic timer start
		if(message[0].equals("autostartbegin")) {
			if(netvsNumPlayers >= 2) {
				int seconds = Integer.parseInt(message[1]);
				netvsAutoStartTimer = seconds * 60;
				netvsAutoStartTimerActive = true;
			}
		}
		// Automatic timer stop
		if(message[0].equals("autostartstop")) {
			netvsAutoStartTimerActive = false;
		}
		// Game Started
		if(message[0].equals("start")) {
			long randseed = Long.parseLong(message[1], 16);
			netvsNumNowPlayers = Integer.parseInt(message[2]);
			netvsNumAlivePlayers = netvsNumNowPlayers;
			netvsMapNo = Integer.parseInt(message[3]);

			netvsResetFlags();
			netUpdatePlayerExist();

			owner.menuOnly = false;
			owner.bgmStatus.reset();
			owner.backgroundStatus.reset();
			owner.replayProp.clear();
			for(int i = 0; i < getPlayers(); i++) {
				if(netvsPlayerExist[i]) {
					owner.engine[i].init();
					netvsSetGameScreenLayout(owner.engine[i]);
				}
			}

			netvsAutoStartTimerActive = false;
			netvsIsGameActive = true;
			netvsIsGameFinished = false;
			netvsPlayTimer = 0;

			netvsSetLockedRule();	// Set locked rule/Restore rule

			for(int i = 0; i < getPlayers(); i++) {
				GameEngine engine = owner.engine[i];
				engine.resetStatc();

				if(netvsPlayerExist[i]) {
					netvsPlayerActive[i] = true;
					engine.stat = GameEngine.STAT_READY;
					engine.randSeed = randseed;
					engine.random = new Random(randseed);

					if((netCurrentRoomInfo.maxPlayers == 2) && (netvsNumPlayers == 2)) {
						engine.isVisible = true;
						engine.displaysize = 0;

						if( (netCurrentRoomInfo.ruleLock) || ((i == 0) && (!netvsIsWatch())) ) {
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
				} else if(i < netCurrentRoomInfo.maxPlayers) {
					engine.stat = GameEngine.STAT_SETTING;
					engine.isVisible = true;
					engine.isNextVisible = false;
					engine.isHoldVisible = false;

					if((netCurrentRoomInfo.maxPlayers == 2) && (netvsNumPlayers == 2)) {
						engine.isVisible = false;
					}
				} else {
					engine.stat = GameEngine.STAT_SETTING;
					engine.isVisible = false;
				}

				netvsPlayerResultReceived[i] = false;
				netvsPlayerDead[i] = false;
				netvsPlayerReady[i] = false;
			}
		}
		// Dead
		if(message[0].equals("dead")) {
			int seatID = Integer.parseInt(message[3]);
			int playerID = netvsGetPlayerIDbySeatID(seatID);

			if(!netvsPlayerDead[playerID]) {
				netvsPlayerDead[playerID] = true;
				netvsPlayerPlace[playerID] = Integer.parseInt(message[4]);
				owner.engine[playerID].stat = GameEngine.STAT_GAMEOVER;
				owner.engine[playerID].resetStatc();
				netvsNumAlivePlayers--;

				if(seatID == netLobby.netPlayerClient.getYourPlayerInfo().seatID) {
					netSendEndGameStats(owner.engine[0]);
				}
			}
		}
		// End-of-game Stats
		if(message[0].equals("gstat")) {
			netvsRecvEndGameStats(message);
		}
		// Game Finished
		if(message[0].equals("finish")) {
			netvsIsGameActive = false;
			netvsIsGameFinished = true;
			netvsPlayTimerActive = false;
			netvsIsNewcomer = false;

			// Stop practice game
			if(netvsIsPractice) {
				netvsIsPractice = false;
				netvsIsPracticeExitAllowed = false;
				owner.bgmStatus.bgm = BGMStatus.BGM_NOTHING;
				owner.engine[0].gameEnded();
				owner.engine[0].stat = GameEngine.STAT_SETTING;
				owner.engine[0].resetStatc();
			}

			boolean flagTeamWin = Boolean.parseBoolean(message[4]);

			if(flagTeamWin) {
				// Team won
				for(int i = 0; i < getPlayers(); i++) {
					if(netvsPlayerExist[i] && !netvsPlayerDead[i]) {
						netvsPlayerPlace[i] = 1;
						owner.engine[i].gameEnded();
						owner.engine[i].stat = GameEngine.STAT_EXCELLENT;
						owner.engine[i].resetStatc();
						owner.engine[i].statistics.time = netvsPlayTimer;
						netvsNumAlivePlayers--;

						if((i == 0) && (!netvsIsWatch())) {
							netSendEndGameStats(owner.engine[0]);
						}
					}
				}
			} else {
				// Normal player won
				int seatID = Integer.parseInt(message[2]);
				if(seatID != -1) {
					int playerID = netvsGetPlayerIDbySeatID(seatID);
					if(netvsPlayerExist[playerID]) {
						netvsPlayerPlace[playerID] = 1;
						owner.engine[playerID].gameEnded();
						owner.engine[playerID].stat = GameEngine.STAT_EXCELLENT;
						owner.engine[playerID].resetStatc();
						owner.engine[playerID].statistics.time = netvsPlayTimer;
						netvsNumAlivePlayers--;

						if((seatID == netLobby.netPlayerClient.getYourPlayerInfo().seatID) && (!netvsIsWatch())) {
							netSendEndGameStats(owner.engine[0]);
						}
					}
				}
			}

			if((netvsIsWatch()) || (netvsPlayerPlace[0] >= 3)) {
				owner.receiver.playSE("matchend");
			}

			netUpdatePlayerExist();
		}
		// Game messages
		if(message[0].equals("game")) {
			//int uid = Integer.parseInt(message[1]);
			int seatID = Integer.parseInt(message[2]);
			int playerID = netvsGetPlayerIDbySeatID(seatID);
			GameEngine engine = owner.engine[playerID];

			if(engine.field == null) {
				engine.createFieldIfNeeded();
			}

			// Field
			if(message[3].equals("field") || message[3].equals("fieldattr")) {
				netRecvField(engine, message);
			}
			// Stats
			if(message[3].equals("stats")) {
				netRecvStats(engine, message);
			}
			// Current Piece
			if(message[3].equals("piece")) {
				netRecvPieceMovement(engine, message);

				// Play timer start
				if(netvsIsWatch() && !netvsIsNewcomer && !netvsPlayTimerActive && !netvsIsGameFinished) {
					netvsPlayTimerActive = true;
					netvsPlayTimer = 0;
				}

				// Force start
				if((!netvsIsWatch()) && (netvsPlayTimerActive) && (!netvsIsPractice) &&
				   (engine.stat == GameEngine.STAT_READY) && (engine.statc[0] < engine.goEnd))
				{
					engine.statc[0] = engine.goEnd;
				}
			}
			// Next and Hold
			if(message[3].equals("next")) {
				netRecvNextAndHold(engine, message);
			}
		}
	}
}
