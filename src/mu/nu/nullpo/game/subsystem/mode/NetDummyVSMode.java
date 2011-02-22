package mu.nu.nullpo.game.subsystem.mode;

import java.util.LinkedList;
import java.util.Random;

import net.omegaboshi.nullpomino.game.subsystem.randomizer.Randomizer;

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

	/** NET-VS: Player count */
	protected int netvsNumPlayers;

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

	/** NET-VS: true if automatic start timer is activated */
	protected boolean netvsAutoStartTimerActive;

	/** NET-VS: Time left until the game starts automatically */
	protected int netvsAutoStartTimer;

	/** NET-VS: true if room game is in progress */
	protected boolean netvsIsGameActive;

	/** NET-VS: true if waiting for ready status change */
	protected boolean netvsIsReadyChangePending;

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

	/** Map number to use */
	protected int netvsMapNo;

	/** Random for selecting map in Practice mode */
	protected Random netvsRandMap;

	/** Practice mode last used map number */
	protected int netvsMapPreviousPracticeMap;

	/*
	 * Mode Name
	 */
	@Override
	public String getName() {
		return "NET-VS-DUMMY";
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
		netvsMySeatID = -1;
		netvsNumPlayers = 0;
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
		netvsResetFlags();
	}

	/**
	 * NET-VS: Init some variables
	 */
	protected void netvsResetFlags() {
		netvsPlayerDead = new boolean[NETVS_MAX_PLAYERS];
		netvsPlayerPlace = new int[NETVS_MAX_PLAYERS];
		netvsIsGameActive = false;
		netvsIsReadyChangePending = false;
		netvsPlayTimerActive = false;
		netvsPlayTimer = 0;
		netvsIsPractice = false;
		netvsIsPracticeExitAllowed = false;
		netvsPieceMoveTimer = 0;
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
		netIsWatch = (netvsMySeatID == -1);

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

	/**
	 * NET-VS: When you join the room
	 */
	@Override
	protected void netOnJoin(NetLobbyFrame lobby, NetPlayerClient client, NetRoomInfo roomInfo) {
		log.debug("netOnJoin() on NetDummyVSMode");

		netCurrentRoomInfo = roomInfo;
		netIsNetPlay = true;

		netUpdatePlayerExist();
		netvsSetLockedRule();
		netvsSetGameScreenLayout();
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
		} else if(!netIsWatch) {
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
			owner.engine[i].displaysize = -1;
			owner.engine[i].enableSE = false;
			if(i >= netCurrentRoomInfo.maxPlayers) {
				owner.engine[i].isVisible = false;
			} else {
				owner.engine[i].isVisible = true;
			}
		}

		if(!netIsWatch) {
			owner.engine[0].displaysize = 0;
			owner.engine[0].enableSE = true;
			owner.engine[0].isVisible = true;
		}

		// 1vs1 layout
		if(netCurrentRoomInfo.maxPlayers == 2) {
			owner.engine[1].displaysize = 0;
			owner.engine[1].isVisible = true;
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
	 * NET-VS: Settings screen
	 */
	@Override
	public boolean onSetting(GameEngine engine, int playerID) {
		if((playerID == 0) && (!netIsWatch)) {
			netvsPlayerExist[0] = true;
			engine.framecolor = NETVS_PLAYER_COLOR_FRAME[netvsPlayerSeatID[0]];

			engine.displaysize = 0;
			engine.enableSE = true;

			// 1vs1 layout
			if(netCurrentRoomInfo.maxPlayers == 2) {
				owner.engine[1].displaysize = 0;
				owner.engine[1].isVisible = true;
			}

			if((!netvsIsReadyChangePending) && (netvsNumPlayers >= 2) && (!netCurrentRoomInfo.playing) && (engine.statc[3] >= 5)) {
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

			// Random Map Preview
			if(netCurrentRoomInfo.useMap && !netLobby.mapList.isEmpty()) {
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

			// Practice Mode
			if(engine.ctrl.isPush(Controller.BUTTON_F) && (engine.statc[3] >= 5)) {
				engine.playSE("decide");
				//startpractice
			}
		}

		return true;
	}

	/**
	 * NET-VS: Render the settings screen
	 */
	@Override
	public void renderSetting(GameEngine engine, int playerID) {
		if(engine.isVisible == false) return;

		if(!netCurrentRoomInfo.playing) {
			int x = owner.receiver.getFieldDisplayPositionX(engine, playerID);
			int y = owner.receiver.getFieldDisplayPositionY(engine, playerID);

			if(netvsPlayerReady[playerID] && netvsPlayerExist[playerID]) {
				if(engine.displaysize != -1)
					owner.receiver.drawDirectFont(engine, playerID, x + 68, y + 204, "OK", EventReceiver.COLOR_YELLOW);
				else
					owner.receiver.drawDirectFont(engine, playerID, x + 36, y + 80, "OK", EventReceiver.COLOR_YELLOW, 0.5f);
			}

			if((!netvsIsReadyChangePending) && (netvsNumPlayers >= 2) && (!netCurrentRoomInfo.playing)) {
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

			if(engine.ctrl.isPush(Controller.BUTTON_F) && (engine.statc[3] >= 5)) {
				String strTemp = "F(" + owner.receiver.getKeyNameByButtonID(engine, Controller.BUTTON_F) + " KEY):";
				if(strTemp.length() > 10) strTemp = strTemp.substring(0, 10);
				strTemp = strTemp.toUpperCase();
				owner.receiver.drawMenuFont(engine, playerID, 0, 18, strTemp, EventReceiver.COLOR_PURPLE);
				owner.receiver.drawMenuFont(engine, playerID, 1, 19, "PRACTICE", EventReceiver.COLOR_PURPLE);
			}
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
				if((playerID == 0) && (!netIsWatch)) {
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

		// Set BGM
		if(playerID == 0) {
			if(netvsIsPractice) {
				owner.bgmStatus.bgm = BGMStatus.BGM_NOTHING;
			} else {
				owner.bgmStatus.bgm = BGMStatus.BGM_NORMAL1;
				owner.bgmStatus.fadesw = false;
			}
		}

		// Init Variables
		netvsPieceMoveTimer = 0;
	}

	/**
	 * NET-VS: When the pieces can move
	 */
	@Override
	public boolean onMove(GameEngine engine, int playerID) {
		if(super.onMove(engine, playerID)) return true;

		// Stop game for remote players
		if(playerID != 0) {
			return true;
		}

		// Auto lock
		if((engine.ending == 0) && (playerID == 0) && (!netIsWatch) && (engine.nowPieceObject != null) && (netvsPieceMoveTimerMax > 0)) {
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
}
