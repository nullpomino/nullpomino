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

import java.util.zip.Adler32;

import org.apache.log4j.Logger;

import mu.nu.nullpo.game.component.BGMStatus;
import mu.nu.nullpo.game.component.Controller;
import mu.nu.nullpo.game.component.Statistics;
import mu.nu.nullpo.game.event.EventReceiver;
import mu.nu.nullpo.game.net.NetPlayerClient;
import mu.nu.nullpo.game.net.NetRoomInfo;
import mu.nu.nullpo.game.net.NetSPRecord;
import mu.nu.nullpo.game.net.NetUtil;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.gui.net.NetLobbyFrame;
import mu.nu.nullpo.util.CustomProperties;
import mu.nu.nullpo.util.GeneralUtil;

/**
 * LINE RACE Mode
 */
public class LineRaceMode extends NetDummyMode {
	/* ----- Main variables ----- */
	/** Logger */
	static Logger log = Logger.getLogger(LineRaceMode.class);

	/** Number of entries in rankings */
	private static final int RANKING_MAX = 10;

	/** Target line count type */
	private static final int GOALTYPE_MAX = 3;

	/** Target line count constants */
	private static final int[] GOAL_TABLE = {20, 40, 100};

	/** Drawing and event handling EventReceiver */
	private EventReceiver receiver;

	/** BGM number */
	private int bgmno;

	/** Big */
	private boolean big;

	/** Target line count type (0=20,1=40,2=100) */
	private int goaltype;

	/** Last preset number used */
	private int presetNumber;

	/** Current round's ranking rank */
	private int rankingRank;

	/** Rankings' times */
	private int[][] rankingTime;

	/** Rankings' piece counts */
	private int[][] rankingPiece;

	/** Rankings' PPS values */
	private float[][] rankingPPS;

	/*
	 * Mode name
	 */
	@Override
	public String getName() {
		return "LINE RACE";
	}

	/*
	 * Initialization for each player
	 */
	@Override
	public void playerInit(GameEngine engine, int playerID) {
		log.debug("playerInit");

		owner = engine.owner;
		receiver = engine.owner.receiver;

		bgmno = 0;
		big = false;
		goaltype = 0;
		presetNumber = 0;

		rankingRank = -1;
		rankingTime = new int[GOALTYPE_MAX][RANKING_MAX];
		rankingPiece = new int[GOALTYPE_MAX][RANKING_MAX];
		rankingPPS = new float[GOALTYPE_MAX][RANKING_MAX];

		engine.framecolor = GameEngine.FRAME_COLOR_RED;

		netPlayerInit(engine, playerID);

		if(engine.owner.replayMode == false) {
			presetNumber = engine.owner.modeConfig.getProperty("linerace.presetNumber", 0);
			loadPreset(engine, engine.owner.modeConfig, -1);
			loadRanking(owner.modeConfig, engine.ruleopt.strRuleName);
		} else {
			presetNumber = 0;
			loadPreset(engine, engine.owner.replayProp, -1);

			// NET: Load name
			netPlayerName = engine.owner.replayProp.getProperty(playerID + ".net.netPlayerName", "");
		}
	}

	/**
	 * NET: When you join the room
	 * @param lobby NetLobbyFrame
	 * @param client NetPlayerClient
	 * @param roomInfo NetRoomInfo
	 */
	protected void onJoin(NetLobbyFrame lobby, NetPlayerClient client, NetRoomInfo roomInfo) {
		super.onJoin(lobby, client, roomInfo);

		if(roomInfo != null) {
			// Load locked rule rankings
			if((roomInfo.ruleLock) && (netLobby != null) && (netLobby.ruleOptLock != null)) {
				log.info("Load locked rule rankings");
				loadRanking(owner.modeConfig, owner.engine[0].ruleopt.strRuleName);
			}
		}
	}

	/**
	 * Load options from a preset
	 * @param engine GameEngine
	 * @param prop Property file to read from
	 * @param preset Preset number
	 */
	private void loadPreset(GameEngine engine, CustomProperties prop, int preset) {
		engine.speed.gravity = prop.getProperty("linerace.gravity." + preset, 4);
		engine.speed.denominator = prop.getProperty("linerace.denominator." + preset, 256);
		engine.speed.are = prop.getProperty("linerace.are." + preset, 0);
		engine.speed.areLine = prop.getProperty("linerace.areLine." + preset, 0);
		engine.speed.lineDelay = prop.getProperty("linerace.lineDelay." + preset, 0);
		engine.speed.lockDelay = prop.getProperty("linerace.lockDelay." + preset, 30);
		engine.speed.das = prop.getProperty("linerace.das." + preset, 14);
		bgmno = prop.getProperty("linerace.bgmno." + preset, 0);
		big = prop.getProperty("linerace.big." + preset, false);
		goaltype = prop.getProperty("linerace.goaltype." + preset, 1);
	}

	/**
	 * Save options to a preset
	 * @param engine GameEngine
	 * @param prop Property file to save to
	 * @param preset Preset number
	 */
	private void savePreset(GameEngine engine, CustomProperties prop, int preset) {
		prop.setProperty("linerace.gravity." + preset, engine.speed.gravity);
		prop.setProperty("linerace.denominator." + preset, engine.speed.denominator);
		prop.setProperty("linerace.are." + preset, engine.speed.are);
		prop.setProperty("linerace.areLine." + preset, engine.speed.areLine);
		prop.setProperty("linerace.lineDelay." + preset, engine.speed.lineDelay);
		prop.setProperty("linerace.lockDelay." + preset, engine.speed.lockDelay);
		prop.setProperty("linerace.das." + preset, engine.speed.das);
		prop.setProperty("linerace.bgmno." + preset, bgmno);
		prop.setProperty("linerace.big." + preset, big);
		prop.setProperty("linerace.goaltype." + preset, goaltype);
	}

	/*
	 * Called at settings screen
	 */
	@Override
	public boolean onSetting(GameEngine engine, int playerID) {
		// NET: Net Ranking
		if(netIsNetRankingDisplayMode) {
			netOnUpdateNetPlayRanking(engine, goaltype);
		}
		// Menu
		else if(engine.owner.replayMode == false) {
			// Up
			if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_UP) && (!netIsWatch)) {
				engine.statc[2]--;
				if(engine.statc[2] < 0) engine.statc[2] = 11;
				engine.playSE("cursor");

				// NET: Signal cursor change
				if(netIsNetPlay && (netNumSpectators > 0)) {
					netLobby.netPlayerClient.send("game\tcursor\t" + engine.statc[2] + "\n");
				}
			}
			// Down
			if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_DOWN) && (!netIsWatch)) {
				engine.statc[2]++;
				if(engine.statc[2] > 11) engine.statc[2] = 0;
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
					if(goaltype < 0) goaltype = 2;
					if(goaltype > 2) goaltype = 0;
					break;
				case 10:
				case 11:
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
			if(engine.ctrl.isPush(Controller.BUTTON_A) && (engine.statc[3] >= 5) && (!netIsWatch)) {
				engine.playSE("decide");

				if(engine.statc[2] == 10) {
					// Load preset
					loadPreset(engine, owner.modeConfig, presetNumber);

					// NET: Signal options change
					if(netIsNetPlay && (netNumSpectators > 0)) {
						netSendOptions(engine);
					}
				} else if(engine.statc[2] == 11) {
					// Save preset
					savePreset(engine, owner.modeConfig, presetNumber);
					receiver.saveModeConfig(owner.modeConfig);
				} else {
					// Save settings
					owner.modeConfig.setProperty("linerace.presetNumber", presetNumber);
					savePreset(engine, owner.modeConfig, -1);
					receiver.saveModeConfig(owner.modeConfig);

					// NET: Signal start of the game
					if(netIsNetPlay) netLobby.netPlayerClient.send("start1p\n");

					return false;
				}
			}

			// Cancel
			if(engine.ctrl.isPush(Controller.BUTTON_B) && (!netIsNetPlay)) {
				engine.quitflag = true;
			}

			// NET: Netplay Ranking
			if(engine.ctrl.isPush(Controller.BUTTON_D) && (netIsNetPlay) && (netCurrentRoomInfo.rated) && (!big) && (engine.ai == null)) {
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
			engine.statc[2] = -1;

			if(engine.statc[3] >= 60) {
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
			netOnRenderNetPlayRanking(engine, playerID, receiver);
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
			drawMenu(engine, playerID, receiver, 0, EventReceiver.COLOR_GREEN, 10,
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
		owner.bgmStatus.bgm = bgmno;

		engine.meterColor = GameEngine.METER_COLOR_GREEN;
		engine.meterValue = receiver.getMeterMax(engine);
	}

	/*
	 * Score display
	 */
	@Override
	public void renderLast(GameEngine engine, int playerID) {
		if(owner.menuOnly) return;

		receiver.drawScoreFont(engine, playerID, 0, 0, "LINE RACE", EventReceiver.COLOR_RED);
		receiver.drawScoreFont(engine, playerID, 0, 1, "(" + GOAL_TABLE[goaltype] + " LINES GAME)", EventReceiver.COLOR_RED);

		if( (engine.stat == GameEngine.STAT_SETTING) || ((engine.stat == GameEngine.STAT_RESULT) && (owner.replayMode == false)) ) {
			if(!owner.replayMode && !big && (engine.ai == null) && !netIsWatch) {
				float scale = (receiver.getNextDisplayType() == 2) ? 0.5f : 1.0f;
				int topY = (receiver.getNextDisplayType() == 2) ? 6 : 4;
				receiver.drawScoreFont(engine, playerID, 3, topY-1, "TIME     PIECE PPS", EventReceiver.COLOR_BLUE, scale);

				for(int i = 0; i < RANKING_MAX; i++) {
					receiver.drawScoreFont(engine, playerID,  0, topY+i, String.format("%2d", i + 1), EventReceiver.COLOR_YELLOW, scale);
					receiver.drawScoreFont(engine, playerID,  3, topY+i, GeneralUtil.getTime(rankingTime[goaltype][i]), (rankingRank == i), scale);
					receiver.drawScoreFont(engine, playerID, 12, topY+i, String.valueOf(rankingPiece[goaltype][i]), (rankingRank == i), scale);
					receiver.drawScoreFont(engine, playerID, 18, topY+i, String.format("%.5g", rankingPPS[goaltype][i]), (rankingRank == i), scale);
				}
			}
		} else {
			receiver.drawScoreFont(engine, playerID, 0, 3, "LINE", EventReceiver.COLOR_BLUE);
			int remainLines = GOAL_TABLE[goaltype] - engine.statistics.lines;
			String strLines = String.valueOf(remainLines);
			if(remainLines < 0) strLines = "0";
			int fontcolor = EventReceiver.COLOR_WHITE;
			if((remainLines <= 30) && (remainLines > 0)) fontcolor = EventReceiver.COLOR_YELLOW;
			if((remainLines <= 20) && (remainLines > 0)) fontcolor = EventReceiver.COLOR_ORANGE;
			if((remainLines <= 10) && (remainLines > 0)) fontcolor = EventReceiver.COLOR_RED;
			receiver.drawScoreFont(engine, playerID, 0, 4, strLines, fontcolor);

			if(strLines.length() == 1) {
				receiver.drawMenuFont(engine, playerID, 4, 21, strLines, fontcolor, 2.0f);
			} else if(strLines.length() == 2) {
				receiver.drawMenuFont(engine, playerID, 3, 21, strLines, fontcolor, 2.0f);
			} else if(strLines.length() == 3) {
				receiver.drawMenuFont(engine, playerID, 2, 21, strLines, fontcolor, 2.0f);
			}

			receiver.drawScoreFont(engine, playerID, 0, 6, "PIECE", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 0, 7, String.valueOf(engine.statistics.totalPieceLocked));

			receiver.drawScoreFont(engine, playerID, 0, 9, "LINE/MIN", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 0, 10, String.valueOf(engine.statistics.lpm));

			receiver.drawScoreFont(engine, playerID, 0, 12, "PIECE/SEC", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 0, 13, String.valueOf(engine.statistics.pps));

			receiver.drawScoreFont(engine, playerID, 0, 15, "TIME", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 0, 16, GeneralUtil.getTime(engine.statistics.time));
		}

		// NET: Number of spectators
		if(netIsNetPlay) {
			receiver.drawScoreFont(engine, playerID, 0, 18, "SPECTATORS", EventReceiver.COLOR_CYAN);
			receiver.drawScoreFont(engine, playerID, 0, 19, "" + netNumSpectators, EventReceiver.COLOR_WHITE);

			if(netIsWatch) {
				receiver.drawScoreFont(engine, playerID, 0, 20, "WATCH", EventReceiver.COLOR_GREEN);
			} else {
				receiver.drawScoreFont(engine, playerID, 0, 20, "PLAY", EventReceiver.COLOR_RED);
			}

			if((engine.stat == GameEngine.STAT_SETTING) && (!netIsWatch) && (netCurrentRoomInfo.rated) && (!big) && (engine.ai == null)) {
				receiver.drawScoreFont(engine, playerID, 0, 22, "D:ONLINE RANKING", EventReceiver.COLOR_GREEN);
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
		int remainLines = GOAL_TABLE[goaltype] - engine.statistics.lines;
		engine.meterValue = (remainLines * receiver.getMeterMax(engine)) / GOAL_TABLE[goaltype];

		if(remainLines <= 30) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
		if(remainLines <= 20) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
		if(remainLines <= 10) engine.meterColor = GameEngine.METER_COLOR_RED;

		// All clear
		if((lines >= 1) && (engine.field.isEmpty())) {
			engine.playSE("bravo");
		}

		// Game completed
		if(engine.statistics.lines >= GOAL_TABLE[goaltype]) {
			engine.ending = 1;
			engine.timerActive = false;
			engine.gameActive = false;

			// NET: Send game completed messages
			if(netIsNetPlay && !netIsWatch) {
				if(netNumSpectators > 0) {
					netSendField(engine);
					netSendNextAndHold(engine);
					netSendStats(engine);
				}
				netLobby.netPlayerClient.send("game\tending\n");
			}
		} else if(engine.statistics.lines >= GOAL_TABLE[goaltype] - 5) {
			owner.bgmStatus.fadesw = true;
		}
	}

	/*
	 * Render results screen
	 */
	@Override
	public void renderResult(GameEngine engine, int playerID) {
		drawResultStats(engine, playerID, receiver, 1, EventReceiver.COLOR_BLUE,
				STAT_LINES, STAT_PIECE, STAT_TIME, STAT_LPM, STAT_PPS);
		drawResultRank(engine, playerID, receiver, 11, EventReceiver.COLOR_BLUE, rankingRank);
		drawResultNetRank(engine, playerID, receiver, 13, EventReceiver.COLOR_BLUE, netRankingRank);

		if(netIsPB) {
			receiver.drawMenuFont(engine, playerID, 2, 16, "NEW PB", EventReceiver.COLOR_ORANGE);
		}

		if(netIsNetPlay && (netReplaySendStatus == 1)) {
			receiver.drawMenuFont(engine, playerID, 0, 18, "SENDING...", EventReceiver.COLOR_PINK);
		} else if(netIsNetPlay && !netIsWatch && (netReplaySendStatus == 2)) {
			receiver.drawMenuFont(engine, playerID, 1, 18, "A: RETRY", EventReceiver.COLOR_RED);
		}
	}

	/*
	 * Save replay file
	 */
	@Override
	public void saveReplay(GameEngine engine, int playerID, CustomProperties prop) {
		savePreset(engine, engine.owner.replayProp, -1);

		// NET: Save name
		if((netPlayerName != null) && (netPlayerName.length() > 0)) {
			prop.setProperty(playerID + ".net.netPlayerName", netPlayerName);
		}

		// Update rankings
		if((!owner.replayMode) && (engine.statistics.lines >= GOAL_TABLE[goaltype]) && (!big) && (engine.ai == null) && (!netIsWatch))
		{
			updateRanking(engine.statistics.time, engine.statistics.totalPieceLocked, engine.statistics.pps);

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
				rankingTime[i][j] = prop.getProperty("linerace.ranking." + ruleName + "." + i + ".time." + j, -1);
				rankingPiece[i][j] = prop.getProperty("linerace.ranking." + ruleName + "." + i + ".piece." + j, 0);
				rankingPPS[i][j] = prop.getProperty("linerace.ranking." + ruleName + "." + i + ".pps." + j, 0f);
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
				prop.setProperty("linerace.ranking." + ruleName + "." + i + ".time." + j, rankingTime[i][j]);
				prop.setProperty("linerace.ranking." + ruleName + "." + i + ".piece." + j, rankingPiece[i][j]);
				prop.setProperty("linerace.ranking." + ruleName + "." + i + ".pps." + j, rankingPPS[i][j]);
			}
		}
	}

	/**
	 * Update rankings
	 * @param time Time
	 * @param piece Piece count
	 */
	private void updateRanking(int time, int piece, float pps) {
		rankingRank = checkRanking(time, piece, pps);

		if(rankingRank != -1) {
			// Shift down ranking entries
			for(int i = RANKING_MAX - 1; i > rankingRank; i--) {
				rankingTime[goaltype][i] = rankingTime[goaltype][i - 1];
				rankingPiece[goaltype][i] = rankingPiece[goaltype][i - 1];
				rankingPPS[goaltype][i] = rankingPPS[goaltype][i - 1];
			}

			// Add new data
			rankingTime[goaltype][rankingRank] = time;
			rankingPiece[goaltype][rankingRank] = piece;
			rankingPPS[goaltype][rankingRank] = pps;
		}
	}

	/**
	 * Calculate ranking position
	 * @param time Time
	 * @param piece Piece count
	 * @return Position (-1 if unranked)
	 */
	private int checkRanking(int time, int piece, float pps) {
		for(int i = 0; i < RANKING_MAX; i++) {
			if((time < rankingTime[goaltype][i]) || (rankingTime[goaltype][i] < 0)) {
				return i;
			} else if((time == rankingTime[goaltype][i]) && ((piece < rankingPiece[goaltype][i]) || (rankingPiece[goaltype][i] == 0))) {
				return i;
			} else if((time == rankingTime[goaltype][i]) && (piece == rankingPiece[goaltype][i]) && (pps > rankingPPS[goaltype][i])) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * NET: Send various in-game stats (as well as goaltype)
	 * @param engine GameEngine
	 */
	protected void netSendStats(GameEngine engine) {
		String msg = "game\tstats\t";
		msg += engine.statistics.lines + "\t" + engine.statistics.totalPieceLocked + "\t";
		msg += engine.statistics.time + "\t" + engine.statistics.lpm + "\t";
		msg += engine.statistics.pps + "\t" + goaltype + "\t";
		msg += engine.gameActive + "\t" + engine.timerActive;
		msg += "\n";
		netLobby.netPlayerClient.send(msg);
	}

	/**
	 * NET: Receive various in-game stats (as well as goaltype)
	 */
	@Override
	protected void netRecvStats(GameEngine engine, String[] message) {
		engine.statistics.lines = Integer.parseInt(message[4]);
		engine.statistics.totalPieceLocked = Integer.parseInt(message[5]);
		engine.statistics.time = Integer.parseInt(message[6]);
		engine.statistics.lpm = Float.parseFloat(message[7]);
		engine.statistics.pps = Float.parseFloat(message[8]);
		goaltype = Integer.parseInt(message[9]);
		engine.gameActive = Boolean.parseBoolean(message[10]);
		engine.timerActive = Boolean.parseBoolean(message[11]);

		// Update meter
		int remainLines = GOAL_TABLE[goaltype] - engine.statistics.lines;
		engine.meterValue = (remainLines * receiver.getMeterMax(engine)) / GOAL_TABLE[goaltype];
		if(remainLines <= 30) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
		if(remainLines <= 20) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
		if(remainLines <= 10) engine.meterColor = GameEngine.METER_COLOR_RED;
	}

	/**
	 * NET: Send end-of-game stats
	 * @param engine GameEngine
	 */
	protected void netSendEndGameStats(GameEngine engine) {
		String subMsg = "";
		subMsg += "LINE;" + engine.statistics.lines + "/" + GOAL_TABLE[goaltype] + "\t";
		subMsg += "PIECE;" + engine.statistics.totalPieceLocked + "\t";
		subMsg += "TIME;" + GeneralUtil.getTime(engine.statistics.time) + "\t";
		subMsg += "LINE/MIN;" + engine.statistics.lpm + "\t";
		subMsg += "PIECE/SEC;" + engine.statistics.pps + "\t";

		String msg = "gstat1p\t" + NetUtil.urlEncode(subMsg) + "\n";
		netLobby.netPlayerClient.send(msg);
	}

	/**
	 * NET: Send game options to all spectators
	 * @param engine GameEngine
	 */
	protected void netSendOptions(GameEngine engine) {
		String msg = "game\toption\t";
		msg += engine.speed.gravity + "\t" + engine.speed.denominator + "\t" + engine.speed.are + "\t";
		msg += engine.speed.areLine + "\t" + engine.speed.lineDelay + "\t" + engine.speed.lockDelay + "\t";
		msg += engine.speed.das + "\t" + bgmno + "\t" + big + "\t" + goaltype + "\t" + presetNumber;
		msg += "\n";
		netLobby.netPlayerClient.send(msg);
	}

	/**
	 * NET: Receive game options
	 */
	@Override
	protected void netRecvOptions(GameEngine engine, String[] message) {
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
		presetNumber = Integer.parseInt(message[14]);
	}

	/**
	 * NET: Send replay data
	 * @param engine GameEngine
	 */
	protected void netSendReplay(GameEngine engine) {
		if((engine.statistics.lines >= GOAL_TABLE[goaltype]) && (!big) && (engine.ai == null)) {
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
}
