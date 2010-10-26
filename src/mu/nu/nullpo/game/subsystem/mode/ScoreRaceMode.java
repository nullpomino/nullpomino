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
import java.util.zip.Adler32;

import net.omegaboshi.nullpomino.game.subsystem.randomizer.Randomizer;

import org.apache.log4j.Logger;

import mu.nu.nullpo.game.component.BGMStatus;
import mu.nu.nullpo.game.component.Block;
import mu.nu.nullpo.game.component.Controller;
import mu.nu.nullpo.game.component.Field;
import mu.nu.nullpo.game.component.Piece;
import mu.nu.nullpo.game.component.Statistics;
import mu.nu.nullpo.game.event.EventReceiver;
import mu.nu.nullpo.game.net.NetPlayerClient;
import mu.nu.nullpo.game.net.NetPlayerInfo;
import mu.nu.nullpo.game.net.NetRoomInfo;
import mu.nu.nullpo.game.net.NetSPRecord;
import mu.nu.nullpo.game.net.NetUtil;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.game.subsystem.wallkick.Wallkick;
import mu.nu.nullpo.gui.net.NetLobbyFrame;
import mu.nu.nullpo.util.CustomProperties;
import mu.nu.nullpo.util.GeneralUtil;

/**
 * SCORE RACE Mode
 */
public class ScoreRaceMode extends NetDummyMode {
	/* ----- Main constants ----- */
	/** Current version */
	private static final int CURRENT_VERSION = 1;

	/** Number of entries in rankings */
	private static final int RANKING_MAX = 10;

	/** Goal score type */
	private static final int GOALTYPE_MAX = 3;

	/** Goal score constants */
	private static final int[] GOAL_TABLE = {10000, 25000, 30000};

	/** Most recent scoring event type constants */
	private static final int EVENT_NONE = 0,
							 EVENT_SINGLE = 1,
							 EVENT_DOUBLE = 2,
							 EVENT_TRIPLE = 3,
							 EVENT_FOUR = 4,
							 EVENT_TSPIN_ZERO_MINI = 5,
							 EVENT_TSPIN_ZERO = 6,
							 EVENT_TSPIN_SINGLE_MINI = 7,
							 EVENT_TSPIN_SINGLE = 8,
							 EVENT_TSPIN_DOUBLE_MINI = 9,
							 EVENT_TSPIN_DOUBLE = 10,
							 EVENT_TSPIN_TRIPLE = 11;

	/* ----- Main variables ----- */
	/** Log */
	static Logger log = Logger.getLogger(ScoreRaceMode.class);

	/** Drawing and event handling EventReceiver */
	private EventReceiver receiver;

	/** Most recent increase in score */
	private int lastscore;

	/** Time to display the most recent increase in score */
	private int scgettime;

	/** Most recent scoring event type */
	private int lastevent;

	/** Most recent scoring event b2b */
	private boolean lastb2b;

	/** Most recent scoring event combo count */
	private int lastcombo;

	/** Most recent scoring event piece ID */
	private int lastpiece;

	/** BGM number */
	private int bgmno;

	/** Flag for types of T-Spins allowed (0=none, 1=normal, 2=all spin) */
	private int tspinEnableType;

	/** Old flag for allowing T-Spins */
	private boolean enableTSpin;

	/** Flag for enabling wallkick T-Spins */
	private boolean enableTSpinKick;

	/** Flag for enabling B2B */
	private boolean enableB2B;

	/** Flag for enabling combos */
	private boolean enableCombo;

	/** Big */
	private boolean big;

	/** Goal score type */
	private int goaltype;

	/** Last preset number used */
	private int presetNumber;

	/** Version */
	private int version;

	/** Current round's ranking rank */
	private int rankingRank;

	/** Rankings' times */
	private int[][] rankingTime;

	/** Rankings' line counts */
	private int[][] rankingLines;

	/** Rankings' score/line */
	private double[][] rankingSPL;

	/* ----- NET variables ----- */
	/** NET: true if netplay */
	private boolean netIsNetPlay;

	/** NET: true if watch mode */
	private boolean netIsWatch;

	/** NET: Current room info */
	private NetRoomInfo netCurrentRoomInfo;

	/** NET: Number of spectators */
	private int netNumSpectators;

	/** NET: Previous piece informations */
	private int netPrevPieceID, netPrevPieceX, netPrevPieceY, netPrevPieceDir;

	/** NET: The skin player using */
	private int netPlayerSkin;

	/** NET: Player name */
	private String netPlayerName;

	/** NET: Replay send status (0:Before Send 1:Sending 2:Sent) */
	private int netReplaySendStatus;

	/** NET: Current round's online ranking rank */
	private int netRankingRank;

	/** NET: True if new personal record */
	private boolean netIsPB;

	/** NET: True if net ranking display mode */
	private boolean netIsNetRankingDisplayMode;

	/** NET: Net ranking cursor position */
	private int netRankingCursor;

	/** NET: Net ranking player's current rank */
	private int netRankingMyRank;

	/** NET: Net Rankings' rank */
	private int[] netRankingPlace;

	/** NET: Net Rankings' names */
	private String[] netRankingName;

	/** NET: Net Rankings' times */
	private int[] netRankingTime;

	/** NET: Net Rankings' line counts */
	private int[] netRankingLines;

	/** NET: Net Rankings' score/line */
	private double[] netRankingSPL;

	/*
	 * Mode name
	 */
	@Override
	public String getName() {
		return "SCORE RACE";
	}

	/*
	 * Mode Initialization
	 */
	@Override
	public void modeInit(GameManager manager) {
		log.debug("modeInit");

		netIsNetPlay = false;
		netIsWatch = false;
		netNumSpectators = 0;
	}

	/*
	 * NET: Netplay Initialization
	 */
	@Override
	public void netplayInit(Object obj) {
		super.netplayInit(obj);
		log.debug("netplayInit");

		if(obj instanceof NetLobbyFrame) {
			onJoin(netLobby, netLobby.netPlayerClient, netLobby.netPlayerClient.getCurrentRoomInfo());
		}
	}

	/**
	 * NET: When you join the room
	 * @param lobby NetLobbyFrame
	 * @param client NetPlayerClient
	 * @param roomInfo NetRoomInfo
	 */
	private void onJoin(NetLobbyFrame lobby, NetPlayerClient client, NetRoomInfo roomInfo) {
		log.debug("onJoin");

		netCurrentRoomInfo = roomInfo;
		netIsNetPlay = true;
		netIsWatch = (netLobby.netPlayerClient.getYourPlayerInfo().seatID == -1);
		netNumSpectators = 0;
		netUpdatePlayerExist();

		if(roomInfo != null) {
			// Set to locked rule
			if((roomInfo.ruleLock) && (netLobby != null) && (netLobby.ruleOptLock != null)) {
				log.info("Set locked rule");
				Randomizer randomizer = GeneralUtil.loadRandomizer(netLobby.ruleOptLock.strRandomizer);
				Wallkick wallkick = GeneralUtil.loadWallkick(netLobby.ruleOptLock.strWallkick);
				owner.engine[0].ruleopt.copy(netLobby.ruleOptLock);
				owner.engine[0].randomizer = randomizer;
				owner.engine[0].wallkick = wallkick;
			}
		}
	}

	/*
	 * Initialization
	 */
	@Override
	public void playerInit(GameEngine engine, int playerID) {
		owner = engine.owner;
		receiver = engine.owner.receiver;
		lastscore = 0;
		scgettime = 0;
		lastevent = EVENT_NONE;
		lastb2b = false;
		lastcombo = 0;
		lastpiece = 0;
		bgmno = 0;

		rankingRank = -1;
		rankingTime = new int[GOALTYPE_MAX][RANKING_MAX];
		rankingLines = new int[GOALTYPE_MAX][RANKING_MAX];
		rankingSPL = new double[GOALTYPE_MAX][RANKING_MAX];

		engine.framecolor = GameEngine.FRAME_COLOR_YELLOW;

		netPrevPieceID = Piece.PIECE_NONE;
		netPrevPieceX = 0;
		netPrevPieceY = 0;
		netPrevPieceDir = 0;
		netPlayerSkin = 0;
		netReplaySendStatus = 0;
		netRankingRank = -1;
		netIsPB = false;
		netIsNetRankingDisplayMode = false;

		if(netIsWatch) {
			engine.isNextVisible = false;
			engine.isHoldVisible = false;
		}

		if(engine.owner.replayMode == false) {
			presetNumber = engine.owner.modeConfig.getProperty("scorerace.presetNumber", 0);
			loadPreset(engine, engine.owner.modeConfig, -1);
			loadRanking(owner.modeConfig, engine.ruleopt.strRuleName);
			version = CURRENT_VERSION;
		} else {
			presetNumber = 0;
			loadPreset(engine, engine.owner.replayProp, -1);
			version = engine.owner.replayProp.getProperty("scorerace.version", 0);
			// NET: Load name
			netPlayerName = engine.owner.replayProp.getProperty(playerID + ".net.netPlayerName", "");
		}
	}

	/**
	 * Load options from a preset
	 * @param engine GameEngine
	 * @param prop Property file to read from
	 * @param preset Preset number
	 */
	private void loadPreset(GameEngine engine, CustomProperties prop, int preset) {
		engine.speed.gravity = prop.getProperty("scorerace.gravity." + preset, 4);
		engine.speed.denominator = prop.getProperty("scorerace.denominator." + preset, 256);
		engine.speed.are = prop.getProperty("scorerace.are." + preset, 0);
		engine.speed.areLine = prop.getProperty("scorerace.areLine." + preset, 0);
		engine.speed.lineDelay = prop.getProperty("scorerace.lineDelay." + preset, 0);
		engine.speed.lockDelay = prop.getProperty("scorerace.lockDelay." + preset, 30);
		engine.speed.das = prop.getProperty("scorerace.das." + preset, 14);
		bgmno = prop.getProperty("scorerace.bgmno." + preset, 0);
		tspinEnableType = prop.getProperty("scorerace.tspinEnableType." + preset, 1);
		enableTSpin = prop.getProperty("scorerace.enableTSpin." + preset, true);
		enableTSpinKick = prop.getProperty("scorerace.enableTSpinKick." + preset, true);
		enableB2B = prop.getProperty("scorerace.enableB2B." + preset, true);
		enableCombo = prop.getProperty("scorerace.enableCombo." + preset, true);
		big = prop.getProperty("scorerace.big." + preset, false);
		goaltype = prop.getProperty("scorerace.goaltype." + preset, 1);
	}

	/**
	 * Save options to a preset
	 * @param engine GameEngine
	 * @param prop Property file to save to
	 * @param preset Preset number
	 */
	private void savePreset(GameEngine engine, CustomProperties prop, int preset) {
		prop.setProperty("scorerace.gravity." + preset, engine.speed.gravity);
		prop.setProperty("scorerace.denominator." + preset, engine.speed.denominator);
		prop.setProperty("scorerace.are." + preset, engine.speed.are);
		prop.setProperty("scorerace.areLine." + preset, engine.speed.areLine);
		prop.setProperty("scorerace.lineDelay." + preset, engine.speed.lineDelay);
		prop.setProperty("scorerace.lockDelay." + preset, engine.speed.lockDelay);
		prop.setProperty("scorerace.das." + preset, engine.speed.das);
		prop.setProperty("scorerace.bgmno." + preset, bgmno);
		prop.setProperty("scorerace.tspinEnableType." + preset, tspinEnableType);
		prop.setProperty("scorerace.enableTSpin." + preset, enableTSpin);
		prop.setProperty("scorerace.enableTSpinKick." + preset, enableTSpinKick);
		prop.setProperty("scorerace.enableB2B." + preset, enableB2B);
		prop.setProperty("scorerace.enableCombo." + preset, enableCombo);
		prop.setProperty("scorerace.big." + preset, big);
		prop.setProperty("scorerace.goaltype." + preset, goaltype);
	}

	/*
	 * Called at settings screen
	 */
	@Override
	public boolean onSetting(GameEngine engine, int playerID) {
		// NET: Net Ranking
		if(netIsNetRankingDisplayMode) {
			if((netRankingName != null) && (netRankingName.length > 0)) {
				// Up
				if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_UP)) {
					netRankingCursor--;
					if(netRankingCursor < 0) netRankingCursor = netRankingPlace.length - 1;
					engine.playSE("cursor");
				}
				// Down
				if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_DOWN)) {
					netRankingCursor++;
					if(netRankingCursor > netRankingPlace.length - 1) netRankingCursor = 0;
					engine.playSE("cursor");
				}
				// Download
				if(engine.ctrl.isPush(Controller.BUTTON_A)) {
					engine.playSE("decide");
					String strMsg = "spdownload\t" + netCurrentRoomInfo.ruleName + "\t" + getName() + "\t" + goaltype + "\t"
									+ NetUtil.urlEncode(netRankingName[netRankingCursor]) + "\n";
					netLobby.netPlayerClient.send(strMsg);
					netIsNetRankingDisplayMode = false;
					owner.menuOnly = false;
				}
			}

			// Exit
			if(engine.ctrl.isPush(Controller.BUTTON_B)) {
				netIsNetRankingDisplayMode = false;
				owner.menuOnly = false;
			}
		}
		// Menu
		else if(engine.owner.replayMode == false) {
			// Up
			if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_UP) && (!netIsWatch)) {
				engine.statc[2]--;
				if(engine.statc[2] < 0) engine.statc[2] = 15;
				engine.playSE("cursor");

				// NET: Signal cursor change
				if(netIsNetPlay && (netNumSpectators > 0)) {
					netLobby.netPlayerClient.send("game\tcursor\t" + engine.statc[2] + "\n");
				}
			}
			// Down
			if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_DOWN) && (!netIsWatch)) {
				engine.statc[2]++;
				if(engine.statc[2] > 15) engine.statc[2] = 0;
				engine.playSE("cursor");

				// NET: Signal cursor change
				if(netIsNetPlay && (netNumSpectators > 0)) {
					netLobby.netPlayerClient.send("game\tcursor\t" + engine.statc[2] + "\n");
				}
			}

			// Configuration changes
			int change = 0;
			if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_LEFT)) change = -1;
			if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_RIGHT)) change = 1;
			if(netIsWatch) change = 0;

			if(change != 0) {
				engine.playSE("change");

				int m = 1;
				if(engine.ctrl.isPress(Controller.BUTTON_E)) m = 100;
				if(engine.ctrl.isPress(Controller.BUTTON_F)) m = 1000;

				switch(engine.statc[2]) {
				case 0:
					engine.speed.gravity += change * m;
					if(engine.speed.gravity < -1) engine.speed.gravity = 99999;
					if(engine.speed.gravity > 99999) engine.speed.gravity = -1;
					break;
				case 1:
					engine.speed.denominator += change * m;
					if(engine.speed.denominator < -1) engine.speed.denominator = 99999;
					if(engine.speed.denominator > 99999) engine.speed.denominator = -1;
					break;
				case 2:
					engine.speed.are += change;
					if(engine.speed.are < 0) engine.speed.are = 99;
					if(engine.speed.are > 99) engine.speed.are = 0;
					break;
				case 3:
					engine.speed.areLine += change;
					if(engine.speed.areLine < 0) engine.speed.areLine = 99;
					if(engine.speed.areLine > 99) engine.speed.areLine = 0;
					break;
				case 4:
					engine.speed.lineDelay += change;
					if(engine.speed.lineDelay < 0) engine.speed.lineDelay = 99;
					if(engine.speed.lineDelay > 99) engine.speed.lineDelay = 0;
					break;
				case 5:
					engine.speed.lockDelay += change;
					if(engine.speed.lockDelay < 0) engine.speed.lockDelay = 99;
					if(engine.speed.lockDelay > 99) engine.speed.lockDelay = 0;
					break;
				case 6:
					engine.speed.das += change;
					if(engine.speed.das < 0) engine.speed.das = 99;
					if(engine.speed.das > 99) engine.speed.das = 0;
					break;
				case 7:
					bgmno += change;
					if(bgmno < 0) bgmno = BGMStatus.BGM_COUNT - 1;
					if(bgmno > BGMStatus.BGM_COUNT - 1) bgmno = 0;
					break;
				case 8:
					big = !big;
					break;
				case 9:
					goaltype += change;
					if(goaltype < 0) goaltype = GOALTYPE_MAX - 1;
					if(goaltype > GOALTYPE_MAX - 1) goaltype = 0;
					break;
				case 10:
					//enableTSpin = !enableTSpin;
					tspinEnableType += change;
					if(tspinEnableType < 0) tspinEnableType = 2;
					if(tspinEnableType > 2) tspinEnableType = 0;
					break;
				case 11:
					enableTSpinKick = !enableTSpinKick;
					break;
				case 12:
					enableB2B = !enableB2B;
					break;
				case 13:
					enableCombo = !enableCombo;
					break;
				case 14:
				case 15:
					presetNumber += change;
					if(presetNumber < 0) presetNumber = 99;
					if(presetNumber > 99) presetNumber = 0;
					break;
				}

				// NET: Signal options change
				if(netIsNetPlay && (netNumSpectators > 0)) {
					netSendOptions(engine);
				}
			}

			// Confirm
			if(engine.ctrl.isPush(Controller.BUTTON_A) && (engine.statc[3] >= 5)) {
				engine.playSE("decide");

				if(engine.statc[2] == 14) {
					// Load preset
					loadPreset(engine, owner.modeConfig, presetNumber);

					// NET: Signal options change
					if(netIsNetPlay && (netNumSpectators > 0)) {
						netSendOptions(engine);
					}
				} else if(engine.statc[2] == 15) {
					// Save preset
					savePreset(engine, owner.modeConfig, presetNumber);
					receiver.saveModeConfig(owner.modeConfig);
				} else {
					// Save settings
					owner.modeConfig.setProperty("scorerace.presetNumber", presetNumber);
					savePreset(engine, owner.modeConfig, -1);
					receiver.saveModeConfig(owner.modeConfig);

					// NET: Signal start of the game
					if(netIsNetPlay) netLobby.netPlayerClient.send("start1p\n");

					return false;
				}
			}

			// Cancel
			if(engine.ctrl.isPush(Controller.BUTTON_B) && !netIsNetPlay) {
				engine.quitflag = true;
			}

			// NET: Netplay Ranking
			if(engine.ctrl.isPush(Controller.BUTTON_D) && (netIsNetPlay) && (!netIsWatch) && (netCurrentRoomInfo.rated) &&
			   (!big) && (engine.ai == null))
			{
				netRankingPlace = null;
				netRankingCursor = 0;
				netRankingMyRank = -1;
				netIsNetRankingDisplayMode = true;
				owner.menuOnly = true;
				netLobby.netPlayerClient.send("spranking\t" + netCurrentRoomInfo.ruleName + "\t" + getName() + "\t" + goaltype + "\n");
			}

			engine.statc[3]++;
		}
		// Replay
		else {
			engine.statc[3]++;
			engine.statc[2] = 0;

			if(engine.statc[3] >= 60) {
				engine.statc[2] = 10;
			}
			if(engine.statc[3] >= 120) {
				return false;
			}
		}

		return true;
	}

	/*
	 * Render settings screen
	 */
	@Override
	public void renderSetting(GameEngine engine, int playerID) {
		if(netIsNetRankingDisplayMode) {
			// NET: Netplay Ranking
			if((netRankingPlace != null) && (netRankingPlace.length > 0)) {
				receiver.drawMenuFont(engine, playerID, 1, 1,
						"ONLINE RANKING (" + (netRankingCursor+1) + "/" + netRankingPlace.length + ")",
						EventReceiver.COLOR_GREEN);

				receiver.drawMenuFont(engine, playerID, 1, 3, "    TIME     LINE SPL    NAME", EventReceiver.COLOR_BLUE);

				int startIndex = (netRankingCursor / 25) * 25;
				int endIndex = startIndex + 25;
				if(endIndex > netRankingPlace.length) endIndex = netRankingPlace.length;
				int c = 0;

				for(int i = startIndex; i < endIndex; i++) {
					if(i == netRankingCursor) {
						receiver.drawMenuFont(engine, playerID, 0, 4 + c, "b", EventReceiver.COLOR_RED);
					}

					int rankColor = (i == netRankingMyRank) ? EventReceiver.COLOR_PINK : EventReceiver.COLOR_YELLOW;
					if(netRankingPlace[i] == -1) {
						receiver.drawMenuFont(engine, playerID, 1, 4 + c, "N/A", rankColor);
					} else {
						receiver.drawMenuFont(engine, playerID, 1, 4 + c, String.format("%3d", netRankingPlace[i]+1), rankColor);
					}
					receiver.drawMenuFont(engine, playerID, 5, 4 + c, GeneralUtil.getTime(netRankingTime[i]), (i == netRankingCursor));
					receiver.drawMenuFont(engine, playerID, 14, 4 + c, "" + netRankingLines[i], (i == netRankingCursor));
					receiver.drawMenuFont(engine, playerID, 19, 4 + c, String.format("%.5g", netRankingSPL[i]), (i == netRankingCursor));
					receiver.drawTTFMenuFont(engine, playerID, 26, 4 + c, netRankingName[i], (i == netRankingCursor));

					c++;
				}
			} else if((netRankingPlace != null) && (netRankingPlace.length == 0)) {
				receiver.drawMenuFont(engine, playerID, 1, 1, "NO DATA", EventReceiver.COLOR_DARKBLUE);
			} else if(netRankingPlace == null) {
				receiver.drawMenuFont(engine, playerID, 1, 1, "LOADING...", EventReceiver.COLOR_CYAN);
			}
		} else if(engine.statc[2] < 10) {
			drawMenu(engine, playerID, receiver, 0, EventReceiver.COLOR_BLUE, 0,
					"GRAVITY", String.valueOf(engine.speed.gravity),
					"G-MAX", String.valueOf(engine.speed.denominator),
					"ARE", String.valueOf(engine.speed.are),
					"ARE LINE", String.valueOf(engine.speed.areLine),
					"LINE DELAY", String.valueOf(engine.speed.lineDelay),
					"LOCK DELAY", String.valueOf(engine.speed.lockDelay),
					"DAS", String.valueOf(engine.speed.das),
					"BGM", String.valueOf(bgmno),
					"BIG",  GeneralUtil.getONorOFF(big),
					"GOAL", String.valueOf(GOAL_TABLE[goaltype]));
		} else {
			String strTSpinEnable = "";
			if(version >= 1) {
				if(tspinEnableType == 0) strTSpinEnable = "OFF";
				if(tspinEnableType == 1) strTSpinEnable = "T-ONLY";
				if(tspinEnableType == 2) strTSpinEnable = "ALL";
			} else {
				strTSpinEnable = GeneralUtil.getONorOFF(enableTSpin);
			}
			drawMenu(engine, playerID, receiver, 0, EventReceiver.COLOR_BLUE, 10,
					"SPIN BONUS", strTSpinEnable,
					"EZ SPIN", GeneralUtil.getONorOFF(enableTSpinKick),
					"B2B", GeneralUtil.getONorOFF(enableB2B),
					"COMBO",  GeneralUtil.getONorOFF(enableCombo));
			drawMenu(engine, playerID, receiver, 8, EventReceiver.COLOR_GREEN, 14,
					"LOAD", String.valueOf(presetNumber),
					"SAVE", String.valueOf(presetNumber));
		}
	}

	/*
	 * This function will be called before the game actually begins (after Ready&Go screen disappears)
	 */
	@Override
	public void startGame(GameEngine engine, int playerID) {
		engine.big = big;
		engine.b2bEnable = enableB2B;
		if(enableCombo) engine.comboType = GameEngine.COMBO_TYPE_NORMAL;
		else engine.comboType = GameEngine.COMBO_TYPE_DISABLE;
		owner.bgmStatus.bgm = bgmno;

		if(version >= 1) {
			engine.tspinAllowKick = enableTSpinKick;
			if(tspinEnableType == 0) {
				engine.tspinEnable = false;
			} else if(tspinEnableType == 1) {
				engine.tspinEnable = true;
			} else {
				engine.tspinEnable = true;
				engine.useAllSpinBonus = true;
			}
		} else {
			engine.tspinEnable = enableTSpin;
		}
	}

	/*
	 * When the pieces can move
	 */
	@Override
	public boolean onMove(GameEngine engine, int playerID) {
		// NET: Send field, next, and stats
		if((engine.ending == 0) && (engine.statc[0] == 0) && (engine.holdDisable == false) &&
		   (netIsNetPlay) && (!netIsWatch) && (netNumSpectators > 0))
		{
			netSendField(engine);
			netSendStats(engine);
		}
		// NET: Send piece movement
		if((engine.ending == 0) && (netIsNetPlay) && (!netIsWatch) && (engine.nowPieceObject != null) && (netNumSpectators > 0))
		{
			if( ((engine.nowPieceObject == null) && (netPrevPieceID != Piece.PIECE_NONE)) || (engine.manualLock) )
			{
				netPrevPieceID = Piece.PIECE_NONE;
				netLobby.netPlayerClient.send("game\tpiece\t" + netPrevPieceID + "\t" + netPrevPieceX + "\t" + netPrevPieceY + "\t" +
						netPrevPieceDir + "\t" + 0 + "\t" + engine.getSkin() + "\n");
				netSendNextAndHold(engine);
			}
			else if((engine.nowPieceObject.id != netPrevPieceID) || (engine.nowPieceX != netPrevPieceX) ||
					(engine.nowPieceY != netPrevPieceY) || (engine.nowPieceObject.direction != netPrevPieceDir))
			{
				netPrevPieceID = engine.nowPieceObject.id;
				netPrevPieceX = engine.nowPieceX;
				netPrevPieceY = engine.nowPieceY;
				netPrevPieceDir = engine.nowPieceObject.direction;

				int x = netPrevPieceX + engine.nowPieceObject.dataOffsetX[netPrevPieceDir];
				int y = netPrevPieceY + engine.nowPieceObject.dataOffsetY[netPrevPieceDir];
				netLobby.netPlayerClient.send("game\tpiece\t" + netPrevPieceID + "\t" + x + "\t" + y + "\t" + netPrevPieceDir + "\t" +
								engine.nowPieceBottomY + "\t" + engine.ruleopt.pieceColor[netPrevPieceID] + "\t" + engine.getSkin() + "\n");
				netSendNextAndHold(engine);
			}
		}
		// NET: Stop game in watch mode
		if(netIsWatch) {
			return true;
		}

		return false;
	}

	/*
	 * When the piece locked
	 */
	@Override
	public void pieceLocked(GameEngine engine, int playerID, int lines) {
		// NET: Send field and stats
		if((engine.ending == 0) && (netIsNetPlay) && (!netIsWatch) && (netNumSpectators > 0)) {
			netSendField(engine);
			netSendStats(engine);
		}
	}

	/*
	 * Line clear
	 */
	@Override
	public boolean onLineClear(GameEngine engine, int playerID) {
		// NET: Send field and stats
		if((engine.statc[0] == 1) && (engine.ending == 0) && (netIsNetPlay) && (!netIsWatch) && (netNumSpectators > 0)) {
			netSendField(engine);
			netSendStats(engine);
		}
		return false;
	}

	/*
	 * ARE
	 */
	@Override
	public boolean onARE(GameEngine engine, int playerID) {
		// NET: Send field, next, and stats
		if((engine.statc[0] == 0) && (engine.ending == 0) && (netIsNetPlay) && (!netIsWatch) && (netNumSpectators > 0)) {
			netSendField(engine);
			netSendNextAndHold(engine);
			netSendStats(engine);
		}
		return false;
	}

	/*
	 * Score display
	 */
	@Override
	public void renderLast(GameEngine engine, int playerID) {
		if(owner.menuOnly) return;

		receiver.drawScoreFont(engine, playerID, 0, 0, "SCORE RACE", EventReceiver.COLOR_RED);
		receiver.drawScoreFont(engine, playerID, 0, 1, "(" + GOAL_TABLE[goaltype] + " PTS GAME)", EventReceiver.COLOR_RED);

		if( (engine.stat == GameEngine.STAT_SETTING) || ((engine.stat == GameEngine.STAT_RESULT) && (owner.replayMode == false)) ) {
			if(!owner.replayMode && !big && (engine.ai == null) && !netIsWatch) {
				float scale = (receiver.getNextDisplayType() == 2) ? 0.5f : 1.0f;
				int topY = (receiver.getNextDisplayType() == 2) ? 6 : 4;
				receiver.drawScoreFont(engine, playerID, 3, topY-1, "TIME     LINE SPL", EventReceiver.COLOR_BLUE, scale);

				for(int i = 0; i < RANKING_MAX; i++) {
					receiver.drawScoreFont(engine, playerID, 0, topY+i, String.format("%2d", i + 1), EventReceiver.COLOR_YELLOW, scale);
					receiver.drawScoreFont(engine, playerID, 3, topY+i, GeneralUtil.getTime(rankingTime[goaltype][i]), (rankingRank == i), scale);
					receiver.drawScoreFont(engine, playerID, 12, topY+i, String.valueOf(rankingLines[goaltype][i]), (rankingRank == i), scale);
					receiver.drawScoreFont(engine, playerID, 17, topY+i, String.format("%.6g", rankingSPL[goaltype][i]), (rankingRank == i), scale);
				}
			}
		} else {
			receiver.drawScoreFont(engine, playerID, 0, 3, "SCORE", EventReceiver.COLOR_BLUE);
			String strScore;
			int sc = GOAL_TABLE[goaltype] - engine.statistics.score;
			if(sc < 0) sc = 0;
			int fontcolor = EventReceiver.COLOR_WHITE;
			if((sc <= 9600) && (sc > 0)) fontcolor = EventReceiver.COLOR_YELLOW;
			if((sc <= 4800) && (sc > 0)) fontcolor = EventReceiver.COLOR_ORANGE;
			if((sc <= 2400) && (sc > 0)) fontcolor = EventReceiver.COLOR_RED;
			if((lastscore == 0) || (scgettime >= 120)) {
				strScore = String.valueOf(sc);
			} else {
				strScore = String.valueOf(sc) + "(-" + String.valueOf(lastscore) + ")";
			}
			receiver.drawScoreFont(engine, playerID, 0, 4, strScore, fontcolor);

			receiver.drawScoreFont(engine, playerID, 0, 6, "LINE", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 0, 7, String.valueOf(engine.statistics.lines));

			receiver.drawScoreFont(engine, playerID, 0, 9, "SCORE/MIN", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 0, 10, String.format("%-10g", engine.statistics.spm));

			receiver.drawScoreFont(engine, playerID, 0, 12, "LINE/MIN", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 0, 13, String.valueOf(engine.statistics.lpm));

			receiver.drawScoreFont(engine, playerID, 0, 15, "SCORE/LINE", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 0, 16, String.format("%-10g", engine.statistics.spl));

			receiver.drawScoreFont(engine, playerID, 0, 18, "TIME", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 0, 19, GeneralUtil.getTime(engine.statistics.time));

			if((lastevent != EVENT_NONE) && (scgettime < 120)) {
				String strPieceName = Piece.getPieceName(lastpiece);

				switch(lastevent) {
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
					if(lastb2b) receiver.drawMenuFont(engine, playerID, 3, 21, "FOUR", EventReceiver.COLOR_RED);
					else receiver.drawMenuFont(engine, playerID, 3, 21, "FOUR", EventReceiver.COLOR_ORANGE);
					break;
				case EVENT_TSPIN_ZERO_MINI:
					receiver.drawMenuFont(engine, playerID, 2, 21, strPieceName + "-SPIN", EventReceiver.COLOR_PURPLE);
					break;
				case EVENT_TSPIN_ZERO:
					receiver.drawMenuFont(engine, playerID, 2, 21, strPieceName + "-SPIN", EventReceiver.COLOR_PINK);
					break;
				case EVENT_TSPIN_SINGLE_MINI:
					if(lastb2b) receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-MINI-S", EventReceiver.COLOR_RED);
					else receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-MINI-S", EventReceiver.COLOR_ORANGE);
					break;
				case EVENT_TSPIN_SINGLE:
					if(lastb2b) receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-SINGLE", EventReceiver.COLOR_RED);
					else receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-SINGLE", EventReceiver.COLOR_ORANGE);
					break;
				case EVENT_TSPIN_DOUBLE_MINI:
					if(lastb2b) receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-MINI-D", EventReceiver.COLOR_RED);
					else receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-MINI-D", EventReceiver.COLOR_ORANGE);
					break;
				case EVENT_TSPIN_DOUBLE:
					if(lastb2b) receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-DOUBLE", EventReceiver.COLOR_RED);
					else receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-DOUBLE", EventReceiver.COLOR_ORANGE);
					break;
				case EVENT_TSPIN_TRIPLE:
					if(lastb2b) receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-TRIPLE", EventReceiver.COLOR_RED);
					else receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-TRIPLE", EventReceiver.COLOR_ORANGE);
					break;
				}

				if((lastcombo >= 2) && (lastevent != EVENT_TSPIN_ZERO_MINI) && (lastevent != EVENT_TSPIN_ZERO))
					receiver.drawMenuFont(engine, playerID, 2, 22, (lastcombo - 1) + "COMBO", EventReceiver.COLOR_CYAN);
			}
		}

		// NET: Number of spectators
		if(netIsNetPlay) {
			receiver.drawScoreFont(engine, playerID, 0, 21, "SPECTATORS", EventReceiver.COLOR_CYAN);
			receiver.drawScoreFont(engine, playerID, 0, 22, "" + netNumSpectators, EventReceiver.COLOR_WHITE);

			if(netIsWatch) {
				receiver.drawScoreFont(engine, playerID, 0, 23, "WATCH", EventReceiver.COLOR_GREEN);
			} else {
				receiver.drawScoreFont(engine, playerID, 0, 23, "PLAY", EventReceiver.COLOR_RED);
			}

			if((engine.stat == GameEngine.STAT_SETTING) && (!netIsWatch) && (netCurrentRoomInfo.rated) && (!big) && (engine.ai == null)) {
				receiver.drawScoreFont(engine, playerID, 0, 24, "D:ONLINE RANKING", EventReceiver.COLOR_GREEN);
			}

			// All number of players
			if(playerID == getPlayers() - 1) netDrawAllPlayersCount(engine);
		}

		// NET: Player name (It may also appear in offline replay)
		if((netPlayerName != null) && (netPlayerName.length() > 0)) {
			String name = netPlayerName;
			//if(name.length() > 14) name = name.substring(0, 14) + "..";
			receiver.drawTTFDirectFont(
					engine, playerID,
					receiver.getFieldDisplayPositionX(engine, playerID),
					receiver.getFieldDisplayPositionY(engine, playerID) - 20,
					name);
		}
	}

	/*
	 * Calculate score
	 */
	@Override
	public void calcScore(GameEngine engine, int playerID, int lines) {
		// Line clear bonus
		int pts = 0;

		if(engine.tspin) {
			// T-Spin 0 lines
			if(lines == 0) {
				if(engine.tspinmini) {
					pts += 100;
					lastevent = EVENT_TSPIN_ZERO_MINI;
				} else {
					pts += 400;
					lastevent = EVENT_TSPIN_ZERO;
				}
			}
			// T-Spin 1 line
			else if(lines == 1) {
				if(engine.tspinmini) {
					if(engine.b2b) {
						pts += 300;
					} else {
						pts += 200;
					}
					lastevent = EVENT_TSPIN_SINGLE_MINI;
				} else {
					if(engine.b2b) {
						pts += 1200;
					} else {
						pts += 800;
					}
					lastevent = EVENT_TSPIN_SINGLE;
				}
			}
			// T-Spin 2 lines
			else if(lines == 2) {
				if(engine.tspinmini && engine.useAllSpinBonus) {
					if(engine.b2b) {
						pts += 600 * (engine.statistics.level + 1);
					} else {
						pts += 400 * (engine.statistics.level + 1);
					}
					lastevent = EVENT_TSPIN_DOUBLE_MINI;
				} else {
					if(engine.b2b) {
						pts += 1800 * (engine.statistics.level + 1);
					} else {
						pts += 1200 * (engine.statistics.level + 1);
					}
					lastevent = EVENT_TSPIN_DOUBLE;
				}
			}
			// T-Spin 3 lines
			else if(lines >= 3) {
				if(engine.b2b) {
					pts += 2400;
				} else {
					pts += 1600;
				}
				lastevent = EVENT_TSPIN_TRIPLE;
			}
		} else {
			if(lines == 1) {
				pts += 100; // 1列
				lastevent = EVENT_SINGLE;
			} else if(lines == 2) {
				pts += 300; // 2列
				lastevent = EVENT_DOUBLE;
			} else if(lines == 3) {
				pts += 500; // 3列
				lastevent = EVENT_TRIPLE;
			} else if(lines >= 4) {
				// 4 lines
				if(engine.b2b) {
					pts += 1200;
				} else {
					pts += 800;
				}
				lastevent = EVENT_FOUR;
			}
		}

		lastb2b = engine.b2b;

		// Combo
		if((enableCombo) && (engine.combo >= 1) && (lines >= 1)) {
			pts += ((engine.combo - 1) * 50);
			lastcombo = engine.combo;
		}

		// All clear
		if((lines >= 1) && (engine.field.isEmpty())) {
			engine.playSE("bravo");
			pts += 1800;
		}

		// Add to score
		if(pts > 0) {
			lastpiece = engine.nowPieceObject.id;
			lastscore = pts;
			scgettime = 0;
			if(lines >= 1) engine.statistics.scoreFromLineClear += pts;
			else engine.statistics.scoreFromOtherBonus += pts;
			engine.statistics.score += pts;
		}
	}

	/*
	 * Soft drop
	 */
	@Override
	public void afterSoftDropFall(GameEngine engine, int playerID, int fall) {
		engine.statistics.scoreFromSoftDrop += fall;
		engine.statistics.score += fall;
	}

	/*
	 * Hard drop
	 */
	@Override
	public void afterHardDropFall(GameEngine engine, int playerID, int fall) {
		engine.statistics.scoreFromHardDrop += fall * 2;
		engine.statistics.score += fall * 2;
	}

	/*
	 * Called after every frame
	 */
	@Override
	public void onLast(GameEngine engine, int playerID) {
		// Update meter
		int remainScore = GOAL_TABLE[goaltype] - engine.statistics.score;
		if(engine.timerActive == false) remainScore = 0;
		engine.meterValue = (remainScore * receiver.getMeterMax(engine)) / GOAL_TABLE[goaltype];
		engine.meterColor = GameEngine.METER_COLOR_GREEN;
		if(remainScore <= 9600) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
		if(remainScore <= 4800) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
		if(remainScore <= 2400) engine.meterColor = GameEngine.METER_COLOR_RED;

		// Goal reached
		if((engine.statistics.score >= GOAL_TABLE[goaltype]) && (engine.timerActive == true)) {
			engine.gameActive = false;
			engine.timerActive = false;
			engine.resetStatc();
			engine.stat = GameEngine.STAT_ENDINGSTART;

			// NET: Send game completed messages
			if(netIsNetPlay && !netIsWatch) {
				if(netNumSpectators > 0) {
					netSendField(engine);
					netSendNextAndHold(engine);
					netSendStats(engine);
				}
				netLobby.netPlayerClient.send("game\tending\n");
			}
		}

		// BGM fadeout
		if((remainScore <= 1000) && (engine.timerActive == true)) {
			owner.bgmStatus.fadesw = true;
		}

		scgettime++;
	}

	/*
	 * Game Over
	 */
	@Override
	public boolean onGameOver(GameEngine engine, int playerID) {
		// NET: Send messages / Wait for messages
		if(netIsNetPlay){
			if(!netIsWatch) {
				if(engine.statc[0] == 0) {
					if(netNumSpectators > 0) {
						netSendField(engine);
						netSendNextAndHold(engine);
						netSendStats(engine);
					}
					netSendEndGameStats(engine);
					netLobby.netPlayerClient.send("dead\t-1\n");
				}
			} else {
				if(engine.statc[0] < engine.field.getHeight() + 1 + 180) {
					return false;
				} else {
					engine.field.reset();
					engine.stat = GameEngine.STAT_RESULT;
					engine.resetStatc();
					return true;
				}
			}
		}

		return false;
	}

	/*
	 * Results screen
	 */
	public boolean onResult(GameEngine engine, int playerID) {
		// NET: Retry
		if(netIsNetPlay) {
			engine.allowTextRenderByReceiver = false;

			// Replay Send
			if(netIsWatch || owner.replayMode) {
				netReplaySendStatus = 2;
			} else if(netReplaySendStatus == 0) {
				netReplaySendStatus = 1;
				netSendReplay(engine);
			}

			// Retry
			if(engine.ctrl.isPush(Controller.BUTTON_A) && !netIsWatch && (netReplaySendStatus == 2)) {
				engine.playSE("decide");
				if(netNumSpectators > 0) {
					netLobby.netPlayerClient.send("game\tretry\n");
					netSendOptions(engine);
				}
				owner.reset();
			}

			return true;
		}

		return false;
	}

	/*
	 * Render results screen
	 */
	@Override
	public void renderResult(GameEngine engine, int playerID) {
		drawResultStats(engine, playerID, receiver, 0, EventReceiver.COLOR_BLUE,
				STAT_SCORE, STAT_LINES, STAT_TIME, STAT_PIECE, STAT_SPL, STAT_SPM, STAT_LPM, STAT_PPS);
		drawResultRank(engine, playerID, receiver, 16, EventReceiver.COLOR_BLUE, rankingRank);
		drawResultNetRank(engine, playerID, receiver, 18, EventReceiver.COLOR_BLUE, netRankingRank);

		if(netIsPB) {
			receiver.drawMenuFont(engine, playerID, 2, 20, "NEW PB", EventReceiver.COLOR_ORANGE);
		}

		if(netIsNetPlay && (netReplaySendStatus == 1)) {
			receiver.drawMenuFont(engine, playerID, 0, 21, "SENDING...", EventReceiver.COLOR_PINK);
		} else if(netIsNetPlay && !netIsWatch && (netReplaySendStatus == 2)) {
			receiver.drawMenuFont(engine, playerID, 1, 21, "A: RETRY", EventReceiver.COLOR_RED);
		}
	}

	/*
	 * Save replay
	 */
	@Override
	public void saveReplay(GameEngine engine, int playerID, CustomProperties prop) {
		savePreset(engine, engine.owner.replayProp, -1);
		engine.owner.replayProp.setProperty("scorerace.version", version);

		// NET: Save name
		if((netPlayerName != null) && (netPlayerName.length() > 0)) {
			prop.setProperty(playerID + ".net.netPlayerName", netPlayerName);
		}

		// Update rankings
		if((owner.replayMode == false) && (engine.statistics.score >= GOAL_TABLE[goaltype]) && (big == false) && (engine.ai == null)) {
			updateRanking(engine.statistics.time, engine.statistics.lines, engine.statistics.spl);

			if(rankingRank != -1) {
				saveRanking(owner.modeConfig, engine.ruleopt.strRuleName);
				receiver.saveModeConfig(owner.modeConfig);
			}
		}
	}

	/**
	 * Read rankings from property file
	 * @param prop Property file
	 * @param ruleName Rule name
	 */
	private void loadRanking(CustomProperties prop, String ruleName) {
		for(int i = 0; i < GOALTYPE_MAX; i++) {
			for(int j = 0; j < RANKING_MAX; j++) {
				rankingTime[i][j] = prop.getProperty("scorerace.ranking." + ruleName + "." + i + ".time." + j, -1);
				rankingLines[i][j] = prop.getProperty("scorerace.ranking." + ruleName + "." + i + ".lines." + j, 0);

				if(rankingLines[i][j] > 0) {
					double defaultSPL = (double)(GOAL_TABLE[i]) / (double)(rankingLines[i][j]);
					rankingSPL[i][j] = prop.getProperty("scorerace.ranking." + ruleName + "." + i + ".spl." + j, defaultSPL);
				} else {
					rankingSPL[i][j] = 0;
				}
			}
		}
	}

	/**
	 * Save rankings to property file
	 * @param prop Property file
	 * @param ruleName Rule name
	 */
	private void saveRanking(CustomProperties prop, String ruleName) {
		for(int i = 0; i < GOALTYPE_MAX; i++) {
			for(int j = 0; j < RANKING_MAX; j++) {
				prop.setProperty("scorerace.ranking." + ruleName + "." + i + ".time." + j, rankingTime[i][j]);
				prop.setProperty("scorerace.ranking." + ruleName + "." + i + ".lines." + j, rankingLines[i][j]);
				prop.setProperty("scorerace.ranking." + ruleName + "." + i + ".spl." + j, rankingSPL[i][j]);
			}
		}
	}

	/**
	 * Update rankings
	 * @param time Time
	 * @param lines Lines
	 * @param spl Score/Line
	 */
	private void updateRanking(int time, int lines, double spl) {
		rankingRank = checkRanking(time, lines, spl);

		if(rankingRank != -1) {
			// Shift down ranking entries
			for(int i = RANKING_MAX - 1; i > rankingRank; i--) {
				rankingTime[goaltype][i] = rankingTime[goaltype][i - 1];
				rankingLines[goaltype][i] = rankingLines[goaltype][i - 1];
				rankingSPL[goaltype][i] = rankingSPL[goaltype][i - 1];
			}

			// Add new data
			rankingTime[goaltype][rankingRank] = time;
			rankingLines[goaltype][rankingRank] = lines;
			rankingSPL[goaltype][rankingRank] = spl;
		}
	}

	/**
	 * Calculate ranking position
	 * @param time Time
	 * @param lines Lines
	 * @param spl Score/Line
	 * @return Position (-1 if unranked)
	 */
	private int checkRanking(int time, int lines, double spl) {
		for(int i = 0; i < RANKING_MAX; i++) {
			if((time < rankingTime[goaltype][i]) || (rankingTime[goaltype][i] < 0)) {
				return i;
			} else if((time == rankingTime[goaltype][i]) && ((lines < rankingLines[goaltype][i]) || (rankingLines[goaltype][i] == 0))) {
				return i;
			} else if((time == rankingTime[goaltype][i]) && (lines == rankingLines[goaltype][i]) && (spl > rankingSPL[goaltype][i])) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * NET: Update player count
	 */
	private void netUpdatePlayerExist() {
		netNumSpectators = 0;
		netPlayerName = "";

		if((netCurrentRoomInfo.roomID != -1) && (netLobby != null)) {
			for(NetPlayerInfo pInfo: netLobby.updateSameRoomPlayerInfoList()) {
				if(pInfo.roomID == netCurrentRoomInfo.roomID) {
					if(pInfo.seatID == 0) {
						netPlayerName = pInfo.strName;
					} else if(pInfo.seatID == -1) {
						netNumSpectators++;
					}
				}
			}
		}
	}

	/**
	 * NET: Send field to all spectators
	 * @param engine GameEngine
	 */
	private void netSendField(GameEngine engine) {
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

		String msg = "game\tfield\t";
		msg += engine.getSkin() + "\t";
		msg += engine.field.getHeightWithoutHurryupFloor() + "\t";
		msg += strFieldData + "\t" + isCompressed + "\n";
		netLobby.netPlayerClient.send(msg);
	}

	/**
	 * NET: Send various in-game stats (as well as goaltype)
	 * @param engine GameEngine
	 */
	private void netSendStats(GameEngine engine) {
		String msg = "game\tstats\t";
		msg += engine.statistics.score + "\t" + engine.statistics.lines + "\t" + engine.statistics.totalPieceLocked + "\t";
		msg += engine.statistics.time + "\t" + engine.statistics.spm + "\t";
		msg += engine.statistics.lpm + "\t" + engine.statistics.spl + "\t" + goaltype + "\t";
		msg += engine.gameActive + "\t" + engine.timerActive + "\t";
		msg += lastscore + "\t" + scgettime + "\t" + lastevent + "\t" + lastb2b + "\t" + lastcombo + "\t" + lastpiece;
		msg += "\n";
		netLobby.netPlayerClient.send(msg);
	}

	/**
	 * NET: Send end-of-game stats
	 * @param engine GameEngine
	 */
	private void netSendEndGameStats(GameEngine engine) {
		String subMsg = "";
		subMsg += "SCORE;" + engine.statistics.score + "/" + GOAL_TABLE[goaltype] + "\t";
		subMsg += "LINE;" + engine.statistics.lines + "\t";
		subMsg += "TIME;" + GeneralUtil.getTime(engine.statistics.time) + "\t";
		subMsg += "PIECE;" + engine.statistics.totalPieceLocked + "\t";
		subMsg += "SCORE/LINE;" + engine.statistics.spl + "\t";
		subMsg += "SCORE/MIN;" + engine.statistics.spm + "\t";
		subMsg += "LINE/MIN;" + engine.statistics.lpm + "\t";
		subMsg += "PIECE/SEC;" + engine.statistics.pps + "\t";

		String msg = "gstat1p\t" + NetUtil.urlEncode(subMsg) + "\n";
		netLobby.netPlayerClient.send(msg);
	}

	/**
	 * NET: Send next and hold piece informations to all spectators
	 * @param engine GameEngine
	 */
	private void netSendNextAndHold(GameEngine engine) {
		int holdID = Piece.PIECE_NONE;
		int holdDirection = Piece.DIRECTION_UP;
		int holdColor = Block.BLOCK_COLOR_GRAY;
		if(engine.holdPieceObject != null) {
			holdID = engine.holdPieceObject.id;
			holdDirection = engine.holdPieceObject.direction;
			holdColor = engine.ruleopt.pieceColor[engine.holdPieceObject.id];
		}

		String msg = "game\tnext\t" + engine.ruleopt.nextDisplay + "\t" + engine.holdDisable + "\t";

		for(int i = -1; i < engine.ruleopt.nextDisplay; i++) {
			if(i < 0) {
				msg += holdID + ";" + holdDirection + ";" + holdColor;
			} else {
				Piece nextObj = engine.getNextObject(engine.nextPieceCount + i);
				msg += nextObj.id + ";" + nextObj.direction + ";" + engine.ruleopt.pieceColor[nextObj.id];
			}

			if(i < engine.ruleopt.nextDisplay - 1) msg += "\t";
		}

		msg += "\n";
		netLobby.netPlayerClient.send(msg);
	}

	/**
	 * NET: Send game options to all spectators
	 * @param engine GameEngine
	 */
	private void netSendOptions(GameEngine engine) {
		String msg = "game\toption\t";
		msg += engine.speed.gravity + "\t" + engine.speed.denominator + "\t" + engine.speed.are + "\t";
		msg += engine.speed.areLine + "\t" + engine.speed.lineDelay + "\t" + engine.speed.lockDelay + "\t";
		msg += engine.speed.das + "\t" + bgmno + "\t" + big + "\t" + goaltype + "\t" + tspinEnableType + "\t";
		msg += enableTSpinKick + "\t" + enableB2B + "\t" + enableCombo + "\t" + presetNumber;
		msg += "\n";
		netLobby.netPlayerClient.send(msg);
	}

	/**
	 * NET: Send replay data
	 * @param engine GameEngine
	 */
	private void netSendReplay(GameEngine engine) {
		if((engine.statistics.score >= GOAL_TABLE[goaltype]) && (!big) && (engine.ai == null)) {
			NetSPRecord record = new NetSPRecord();
			record.setReplayProp(owner.replayProp);
			record.stats = new Statistics(engine.statistics);
			record.gameType = goaltype;

			String strData = NetUtil.compressString(record.exportString());

			Adler32 checksumObj = new Adler32();
			checksumObj.update(NetUtil.stringToBytes(strData));
			long sChecksum = checksumObj.getValue();

			netLobby.netPlayerClient.send("spsend\t" + sChecksum + "\t" + strData + "\n");
		} else {
			netReplaySendStatus = 2;
		}
	}

	@Override
	public void netlobbyOnMessage(NetLobbyFrame lobby, NetPlayerClient client, String[] message) throws IOException {
		super.netlobbyOnMessage(lobby, client, message);

		// Player status update
		if(message[0].equals("playerupdate")) {
			netUpdatePlayerExist();
		}
		// When someone logout
		if(message[0].equals("playerlogout")) {
			NetPlayerInfo pInfo = new NetPlayerInfo(message[1]);

			if(pInfo.roomID == netCurrentRoomInfo.roomID) {
				netUpdatePlayerExist();
			}
		}
		// Game started
		if(message[0].equals("start")) {
			log.debug("NET: Game started");

			if(netIsWatch) {
				owner.reset();
				owner.engine[0].stat = GameEngine.STAT_READY;
				owner.engine[0].resetStatc();
			}
		}
		// Dead
		if(message[0].equals("dead")) {
			log.debug("NET: Dead");

			if(netIsWatch) {
				owner.engine[0].gameActive = false;
				owner.engine[0].timerActive = false;

				if((owner.engine[0].stat != GameEngine.STAT_GAMEOVER) && (owner.engine[0].stat != GameEngine.STAT_RESULT)) {
					owner.engine[0].stat = GameEngine.STAT_GAMEOVER;
					owner.engine[0].resetStatc();
				}
			}
		}
		// Replay send fail
		if(message[0].equals("spsendng")) {
			netReplaySendStatus = 1;
			netSendReplay(owner.engine[0]);
		}
		// Replay send complete
		if(message[0].equals("spsendok")) {
			netReplaySendStatus = 2;
			netRankingRank = Integer.parseInt(message[1]);
			netIsPB = Boolean.parseBoolean(message[2]);
		}
		// Netplay Ranking
		if(message[0].equals("spranking")) {
			int maxRecords = Integer.parseInt(message[5]);
			log.debug("spranking message.length:" + message.length + " maxRecords:" + maxRecords);

			if(message.length > 6) {
				String[] arrayRow = message[6].split(";");
				maxRecords = Math.min(maxRecords, arrayRow.length);

				netRankingPlace = new int[maxRecords];
				netRankingName = new String[maxRecords];
				netRankingTime = new int[maxRecords];
				netRankingLines = new int[maxRecords];
				netRankingSPL = new double[maxRecords];

				for(int i = 0; i < maxRecords; i++) {
					String[] arrayData = arrayRow[i].split(",");
					netRankingPlace[i] = Integer.parseInt(arrayData[0]);
					netRankingName[i] = NetUtil.urlDecode(arrayData[1]);
					netRankingTime[i] = Integer.parseInt(arrayData[2]);
					netRankingLines[i] = Integer.parseInt(arrayData[3]);
					netRankingSPL[i] = Double.parseDouble(arrayData[4]);

					if(netRankingName[i].equals(netPlayerName)) {
						netRankingCursor = i;
						netRankingMyRank = i;
					}
				}
			} else {
				netRankingPlace = new int[0];
				netRankingName = new String[0];
				netRankingTime = new int[0];
				netRankingLines = new int[0];
				netRankingSPL = new double[0];
			}
		}
		// Game messages
		if(message[0].equals("game")) {
			if(netIsWatch) {
				GameEngine engine = owner.engine[0];
				if(engine.field == null) {
					engine.field = new Field();
				}

				// Move cursor
				if(message[3].equals("cursor")) {
					if(engine.stat == GameEngine.STAT_SETTING) {
						engine.statc[2] = Integer.parseInt(message[4]);
						engine.playSE("cursor");
					}
				}
				// Change game options
				if(message[3].equals("option")) {
					engine.speed.gravity = Integer.parseInt(message[4]);
					engine.speed.denominator = Integer.parseInt(message[5]);
					engine.speed.are = Integer.parseInt(message[6]);
					engine.speed.areLine = Integer.parseInt(message[7]);
					engine.speed.lineDelay = Integer.parseInt(message[8]);
					engine.speed.lockDelay = Integer.parseInt(message[9]);
					engine.speed.das = Integer.parseInt(message[10]);
					bgmno = Integer.parseInt(message[11]);
					big = Boolean.parseBoolean(message[12]);
					goaltype = Integer.parseInt(message[13]);
					tspinEnableType = Integer.parseInt(message[14]);
					enableTSpinKick = Boolean.parseBoolean(message[15]);
					enableB2B = Boolean.parseBoolean(message[16]);
					enableCombo = Boolean.parseBoolean(message[17]);
					presetNumber = Integer.parseInt(message[18]);
				}
				// Field
				if(message[3].equals("field")) {
					if(message.length > 5) {
						engine.nowPieceObject = null;
						engine.holdDisable = false;
						if(engine.stat == GameEngine.STAT_SETTING) engine.stat = GameEngine.STAT_MOVE;
						int skin = Integer.parseInt(message[4]);
						int highestWallY = Integer.parseInt(message[5]);
						netPlayerSkin = skin;
						if(message.length > 7) {
							String strFieldData = message[6];
							boolean isCompressed = Boolean.parseBoolean(message[7]);
							if(isCompressed) {
								strFieldData = NetUtil.decompressString(strFieldData);
							}
							engine.field.stringToField(strFieldData, skin, highestWallY, highestWallY);
						} else {
							engine.field.reset();
						}
					}
				}
				// Stats
				if(message[3].equals("stats")) {
					engine.statistics.score = Integer.parseInt(message[4]);
					engine.statistics.lines = Integer.parseInt(message[5]);
					engine.statistics.totalPieceLocked = Integer.parseInt(message[6]);
					engine.statistics.time = Integer.parseInt(message[7]);
					engine.statistics.spm = Double.parseDouble(message[8]);
					engine.statistics.lpm = Float.parseFloat(message[9]);
					engine.statistics.spl = Double.parseDouble(message[10]);
					goaltype = Integer.parseInt(message[11]);
					engine.gameActive = Boolean.parseBoolean(message[12]);
					engine.timerActive = Boolean.parseBoolean(message[13]);
					lastscore = Integer.parseInt(message[14]);
					scgettime = Integer.parseInt(message[15]);
					lastevent = Integer.parseInt(message[16]);
					lastb2b = Boolean.parseBoolean(message[17]);
					lastcombo = Integer.parseInt(message[18]);
					lastpiece = Integer.parseInt(message[19]);
				}
				// Current Piece
				if(message[3].equals("piece")) {
					int id = Integer.parseInt(message[4]);

					if(id >= 0) {
						int pieceX = Integer.parseInt(message[5]);
						int pieceY = Integer.parseInt(message[6]);
						int pieceDir = Integer.parseInt(message[7]);
						//int pieceBottomY = Integer.parseInt(message[8]);
						int pieceColor = Integer.parseInt(message[9]);
						int pieceSkin = Integer.parseInt(message[10]);

						engine.nowPieceObject = new Piece(id);
						engine.nowPieceObject.direction = pieceDir;
						engine.nowPieceObject.setAttribute(Block.BLOCK_ATTRIBUTE_VISIBLE, true);
						engine.nowPieceObject.setColor(pieceColor);
						engine.nowPieceObject.setSkin(pieceSkin);
						engine.nowPieceX = pieceX;
						engine.nowPieceY = pieceY;
						//engine.nowPieceBottomY = pieceBottomY;
						engine.nowPieceObject.updateConnectData();
						engine.nowPieceBottomY =
							engine.nowPieceObject.getBottom(pieceX, pieceY, engine.field);

						if((engine.stat != GameEngine.STAT_EXCELLENT) && (engine.stat != GameEngine.STAT_GAMEOVER) &&
						   (engine.stat != GameEngine.STAT_RESULT))
						{
							engine.gameActive = true;
							engine.timerActive = true;
							engine.stat = GameEngine.STAT_MOVE;
							engine.statc[0] = 2;
						}

						netPlayerSkin = pieceSkin;
					} else {
						engine.nowPieceObject = null;
					}
				}
				// Next and Hold
				if(message[3].equals("next")) {
					int maxNext = Integer.parseInt(message[4]);
					engine.ruleopt.nextDisplay = maxNext;
					engine.holdDisable = Boolean.parseBoolean(message[5]);

					for(int i = 0; i < maxNext + 1; i++) {
						if(i + 6 < message.length) {
							String[] strPieceData = message[i + 6].split(";");
							int pieceID = Integer.parseInt(strPieceData[0]);
							int pieceDirection = Integer.parseInt(strPieceData[1]);
							int pieceColor = Integer.parseInt(strPieceData[2]);

							if(i == 0) {
								if(pieceID == Piece.PIECE_NONE) {
									engine.holdPieceObject = null;
								} else {
									engine.holdPieceObject = new Piece(pieceID);
									engine.holdPieceObject.direction = pieceDirection;
									engine.holdPieceObject.setColor(pieceColor);
									engine.holdPieceObject.setSkin(netPlayerSkin);
								}
							} else {
								if((engine.nextPieceArrayObject == null) || (engine.nextPieceArrayObject.length < maxNext)) {
									engine.nextPieceArrayObject = new Piece[maxNext];
								}
								engine.nextPieceArrayObject[i - 1] = new Piece(pieceID);
								engine.nextPieceArrayObject[i - 1].direction = pieceDirection;
								engine.nextPieceArrayObject[i - 1].setColor(pieceColor);
								engine.nextPieceArrayObject[i - 1].setSkin(netPlayerSkin);
							}
						}
					}

					engine.isNextVisible = true;
					engine.isHoldVisible = true;
				}
				// Ending
				if(message[3].equals("ending")) {
					engine.ending = 1;
					engine.timerActive = false;
					engine.gameActive = false;
					engine.stat = GameEngine.STAT_ENDINGSTART;
					engine.resetStatc();
				}
				// Retry
				if(message[3].equals("retry")) {
					engine.ending = 0;
					engine.timerActive = false;
					engine.gameActive = false;
					engine.stat = GameEngine.STAT_SETTING;
					engine.resetStatc();
					engine.playSE("decide");
				}
			}
		}
	}
}
