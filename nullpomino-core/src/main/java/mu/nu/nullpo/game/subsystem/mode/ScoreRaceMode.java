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

import org.apache.log4j.Logger;

import mu.nu.nullpo.game.component.BGMStatus;
import mu.nu.nullpo.game.component.Controller;
import mu.nu.nullpo.game.component.Piece;
import mu.nu.nullpo.game.event.EventReceiver;
import mu.nu.nullpo.game.net.NetUtil;
import mu.nu.nullpo.game.play.GameEngine;
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
							 EVENT_TSPIN_TRIPLE = 11,
							 EVENT_TSPIN_EZ = 12;

	/* ----- Main variables ----- */
	/** Log */
	static Logger log = Logger.getLogger(ScoreRaceMode.class);

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

	/** Spin check type (4Point or Immobile) */
	private int spinCheckType;

	/** Immobile EZ spin */
	private boolean tspinEnableEZ;

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

	/*
	 * Mode name
	 */
	@Override
	public String getName() {
		return "SCORE RACE";
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

		netPlayerInit(engine, playerID);

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
		spinCheckType = prop.getProperty("scorerace.spinCheckType." + preset, 0);
		tspinEnableEZ = prop.getProperty("scorerace.tspinEnableEZ." + preset, false);
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
		prop.setProperty("scorerace.spinCheckType." + preset, spinCheckType);
		prop.setProperty("scorerace.tspinEnableEZ." + preset, tspinEnableEZ);
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
			netOnUpdateNetPlayRanking(engine, goaltype);
		}
		// Menu
		else if(engine.owner.replayMode == false) {
			// Configuration changes
			int change = updateCursor(engine, 17, playerID);

			if(change != 0) {
				engine.playSE("change");

				int m = 1;
				if(engine.ctrl.isPress(Controller.BUTTON_E)) m = 100;
				if(engine.ctrl.isPress(Controller.BUTTON_F)) m = 1000;

				switch(menuCursor) {
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
					spinCheckType += change;
					if(spinCheckType < 0) spinCheckType = 1;
					if(spinCheckType > 1) spinCheckType = 0;
					break;
				case 13:
					tspinEnableEZ = !tspinEnableEZ;
					break;
				case 14:
					enableB2B = !enableB2B;
					break;
				case 15:
					enableCombo = !enableCombo;
					break;
				case 16:
				case 17:
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
			if(engine.ctrl.isPush(Controller.BUTTON_A) && (menuTime >= 5)) {
				engine.playSE("decide");

				if(menuCursor == 16) {
					// Load preset
					loadPreset(engine, owner.modeConfig, presetNumber);

					// NET: Signal options change
					if(netIsNetPlay && (netNumSpectators > 0)) {
						netSendOptions(engine);
					}
				} else if(menuCursor == 17) {
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
			if(engine.ctrl.isPush(Controller.BUTTON_D) && netIsNetPlay && !netIsWatch && !big && engine.ai == null)
			{
				netEnterNetPlayRankingScreen(engine, playerID, goaltype);
			}

			menuTime++;
		}
		// Replay
		else {
			menuTime++;
			menuCursor = 0;

			if(menuTime >= 60) {
				menuCursor = 10;
			}
			if(menuTime >= 120) {
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
		} else if(menuCursor < 10) {
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
					"SPIN TYPE", (spinCheckType == 0) ? "4POINT" : "IMMOBILE",
					"EZIMMOBILE", GeneralUtil.getONorOFF(tspinEnableEZ),
					"B2B", GeneralUtil.getONorOFF(enableB2B),
					"COMBO",  GeneralUtil.getONorOFF(enableCombo));
			drawMenu(engine, playerID, receiver, 12, EventReceiver.COLOR_GREEN, 16,
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

		if(netIsWatch) {
			owner.bgmStatus.bgm = BGMStatus.BGM_NOTHING;
		} else {
			owner.bgmStatus.bgm = bgmno;
		}

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

		engine.spinCheckType = spinCheckType;
		engine.tspinEnableEZ = tspinEnableEZ;
	}

	/*
	 * Score display
	 */
	@Override
	public void renderLast(GameEngine engine, int playerID) {
		if(owner.menuOnly) return;

		receiver.drawScoreFont(engine, playerID, 0, 0, "SCORE RACE", EventReceiver.COLOR_RED);
		receiver.drawScoreFont(engine, playerID, 0, 1, "(" + GOAL_TABLE[goaltype] + " PTS GAME)", EventReceiver.COLOR_RED);

		if( (engine.stat == GameEngine.Status.SETTING) || ((engine.stat == GameEngine.Status.RESULT) && (owner.replayMode == false)) ) {
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
				case EVENT_TSPIN_EZ:
					if(lastb2b) receiver.drawMenuFont(engine, playerID, 3, 21, "EZ-" + strPieceName, EventReceiver.COLOR_RED);
					else receiver.drawMenuFont(engine, playerID, 3, 21, "EZ-" + strPieceName, EventReceiver.COLOR_ORANGE);
					break;
				}

				if((lastcombo >= 2) && (lastevent != EVENT_TSPIN_ZERO_MINI) && (lastevent != EVENT_TSPIN_ZERO))
					receiver.drawMenuFont(engine, playerID, 2, 22, (lastcombo - 1) + "COMBO", EventReceiver.COLOR_CYAN);
			}
		}

		// NET: Number of spectators
		netDrawSpectatorsCount(engine, 0, 21);
		// NET: All number of players
		if(playerID == getPlayers() - 1) {
			netDrawAllPlayersCount(engine);
			netDrawGameRate(engine);
		}
		// NET: Player name (It may also appear in offline replay)
		netDrawPlayerName(engine);
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
			if((lines == 0) && (!engine.tspinez)) {
				if(engine.tspinmini) {
					pts += 100;
					lastevent = EVENT_TSPIN_ZERO_MINI;
				} else {
					pts += 400;
					lastevent = EVENT_TSPIN_ZERO;
				}
			}
			// Immobile EZ Spin
			else if(engine.tspinez && (lines > 0)) {
				if(engine.b2b) {
					pts += 180 * (engine.statistics.level + 1);
				} else {
					pts += 120 * (engine.statistics.level + 1);
				}
				lastevent = EVENT_TSPIN_EZ;
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
				pts += 100; // 1Column
				lastevent = EVENT_SINGLE;
			} else if(lines == 2) {
				pts += 300; // 2Column
				lastevent = EVENT_DOUBLE;
			} else if(lines == 3) {
				pts += 500; // 3Column
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
			engine.gameEnded();
			engine.resetStatc();
			engine.stat = GameEngine.Status.ENDINGSTART;
		}

		// BGM fadeout
		if((remainScore <= 1000) && (engine.timerActive == true)) {
			owner.bgmStatus.fadesw = true;
		}

		scgettime++;
	}

	/*
	 * Render results screen
	 */
	@Override
	public void renderResult(GameEngine engine, int playerID) {
		receiver.drawMenuFont(engine, playerID, 0, 0, "kn PAGE" + (engine.statc[1] + 1) + "/2", EventReceiver.COLOR_RED);

		if(engine.statc[1] == 0) {
			drawResultStats(engine, playerID, receiver, 2, EventReceiver.COLOR_BLUE,
					Statistic.SCORE, Statistic.LINES, Statistic.TIME, Statistic.PIECE);
			drawResultRank(engine, playerID, receiver, 10, EventReceiver.COLOR_BLUE, rankingRank);
			drawResultNetRank(engine, playerID, receiver, 12, EventReceiver.COLOR_BLUE, netRankingRank[0]);
			drawResultNetRankDaily(engine, playerID, receiver, 14, EventReceiver.COLOR_BLUE, netRankingRank[1]);
		} else if(engine.statc[1] == 1) {
			drawResultStats(engine, playerID, receiver, 2, EventReceiver.COLOR_BLUE,
					Statistic.SPL, Statistic.SPM, Statistic.LPM, Statistic.PPS);
		}

		if(netIsPB) {
			receiver.drawMenuFont(engine, playerID, 2, 21, "NEW PB", EventReceiver.COLOR_ORANGE);
		}

		if(netIsNetPlay && (netReplaySendStatus == 1)) {
			receiver.drawMenuFont(engine, playerID, 0, 22, "SENDING...", EventReceiver.COLOR_PINK);
		} else if(netIsNetPlay && !netIsWatch && (netReplaySendStatus == 2)) {
			receiver.drawMenuFont(engine, playerID, 1, 22, "A: RETRY", EventReceiver.COLOR_RED);
		}
	}

	/*
	 * Results screen
	 */
	@Override
	public boolean onResult(GameEngine engine, int playerID) {
		// Page change
		if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_UP)) {
			engine.statc[1]--;
			if(engine.statc[1] < 0) engine.statc[1] = 1;
			engine.playSE("change");
		}
		if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_DOWN)) {
			engine.statc[1]++;
			if(engine.statc[1] > 1) engine.statc[1] = 0;
			engine.playSE("change");
		}

		return super.onResult(engine, playerID);
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
	@Override
	protected void loadRanking(CustomProperties prop, String ruleName) {
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
	 * NET: Send various in-game stats (as well as goaltype)
	 * @param engine GameEngine
	 */
	@Override
	protected void netSendStats(GameEngine engine) {
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
	 * NET: Receive various in-game stats (as well as goaltype)
	 */
	@Override
	protected void netRecvStats(GameEngine engine, String[] message) {
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

	/**
	 * NET: Send end-of-game stats
	 * @param engine GameEngine
	 */
	@Override
	protected void netSendEndGameStats(GameEngine engine) {
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
	 * NET: Send game options to all spectators
	 * @param engine GameEngine
	 */
	@Override
	protected void netSendOptions(GameEngine engine) {
		String msg = "game\toption\t";
		msg += engine.speed.gravity + "\t" + engine.speed.denominator + "\t" + engine.speed.are + "\t";
		msg += engine.speed.areLine + "\t" + engine.speed.lineDelay + "\t" + engine.speed.lockDelay + "\t";
		msg += engine.speed.das + "\t" + bgmno + "\t" + big + "\t" + goaltype + "\t" + tspinEnableType + "\t";
		msg += enableTSpinKick + "\t" + enableB2B + "\t" + enableCombo + "\t" + presetNumber + "\t";
		msg += spinCheckType + "\t" + tspinEnableEZ + "\n";
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
		tspinEnableType = Integer.parseInt(message[14]);
		enableTSpinKick = Boolean.parseBoolean(message[15]);
		enableB2B = Boolean.parseBoolean(message[16]);
		enableCombo = Boolean.parseBoolean(message[17]);
		presetNumber = Integer.parseInt(message[18]);
		spinCheckType = Integer.parseInt(message[19]);
		tspinEnableEZ = Boolean.parseBoolean(message[20]);
	}

	/**
	 * NET: Get goal type
	 */
	@Override
	protected int netGetGoalType() {
		return goaltype;
	}

	/**
	 * NET: It returns true when the current settings doesn't prevent leaderboard screen from showing.
	 */
	@Override
	protected boolean netIsNetRankingViewOK(GameEngine engine) {
		return (!big) && (engine.ai == null);
	}

	/**
	 * NET: It returns true when the current settings doesn't prevent replay data from sending.
	 */
	@Override
	protected boolean netIsNetRankingSendOK(GameEngine engine) {
		return netIsNetRankingViewOK(engine) && (engine.statistics.score >= GOAL_TABLE[goaltype]);
	}
}
