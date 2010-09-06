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

import mu.nu.nullpo.game.component.BGMStatus;
import mu.nu.nullpo.game.component.Controller;
import mu.nu.nullpo.game.component.Piece;
import mu.nu.nullpo.game.event.EventReceiver;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.util.CustomProperties;
import mu.nu.nullpo.util.GeneralUtil;

/**
 * TECHNICIANMode
 */
public class TechnicianMode extends DummyMode {
	/** Current version */
	private static final int CURRENT_VERSION = 2;

	/** Fall velocity table (numerators) */
	private static final int tableGravity[]     = { 1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1, 465, 731, 1280, 1707,  -1,  -1,  -1};

	/** Fall velocity table (denominators) */
	private static final int tableDenominator[] = {63, 50, 39, 30, 22, 16, 12,  8,  6,  4,  3,  2,  1, 256, 256,  256,  256, 256, 256, 256};

	/** BGM change levels */
	private static final int tableBGMChange[]   = {9, 15, 19, 23, 27, -1};

	/** Comboで手に入る point */
	private static final int COMBO_GOAL_TABLE[] = {0,0,1,1,2,2,3,3,4,4,4,5};

	/** Number of entries in rankings */
	private static final int RANKING_MAX = 10;

	/** Number of ranking types */
	private static final int RANKING_TYPE = 5;

	/** Most recent scoring event typeの定count */
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

	/** Game type typeの定count */
	private static final int GAMETYPE_LV15_EASY = 0,
							 GAMETYPE_LV15_HARD = 1,
							 GAMETYPE_10MIN_EASY = 2,
							 GAMETYPE_10MIN_HARD = 3,
							 GAMETYPE_SPECIAL = 4;

	/** Game typeの表示名 */
	private static final String[] GAMETYPE_NAME = {"LV15-EASY", "LV15-HARD", "10MIN-EASY", "10MIN-HARD", "SPECIAL"};

	/** Game type typeのcount */
	private static final int GAMETYPE_MAX = 5;

	/** 各 levelの制限 time */
	private static final int TIMELIMIT_LEVEL = 3600*2;

	/** 10分間Mode のゲーム制限 time */
	private static final int TIMELIMIT_10MIN = 3600*10;

	/** SPECIALMode 開始時の制限 time */
	private static final int TIMELIMIT_SPECIAL = 3600*2;

	/** SPECIALMode でLevel upしたときに増える time */
	private static final int TIMELIMIT_SPECIAL_BONUS = 60*30;

	/** Endingの time */
	private static final int TIMELIMIT_ROLL = 3600;

	/** GameManager that owns this mode */
	private GameManager owner;

	/** Drawing and event handling EventReceiver */
	private EventReceiver receiver;

	/** Level upまでの残り point */
	private int goal;

	/** この levelでの経過 time */
	private int levelTimer;

	/** この levelでタイマーが切れていたらtrue */
	private boolean levelTimeOut;

	/** ゲーム全体の制限 time */
	private int totalTimer;

	/** Ending time */
	private int rolltime;

	/** 直前に手に入れた point */
	private int lastgoal;

	/** Most recent increase in score */
	private int lastscore;

	/** 直前に手に入れたTime bonus */
	private int lasttimebonus;

	/** Time to display the most recent increase in score */
	private int scgettime;

	/** REGRET display time frame count */
	private int regretdispframe;

	/** Most recent scoring event type */
	private int lastevent;

	/** Most recent scoring eventでB2Bだったらtrue */
	private boolean lastb2b;

	/** Most recent scoring eventでのCombocount */
	private int lastcombo;

	/** Most recent scoring eventでのピースID */
	private int lastpiece;

	/** Current BGM */
	private int bgmlv;

	/** Game type */
	private int gametype;

	/** Level at start time */
	private int startlevel;

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

	/** Version */
	private int version;

	/** Current round's ranking rank */
	private int rankingRank;

	/** Rankings' scores */
	private int[][] rankingScore;

	/** Rankings' line counts */
	private int[][] rankingLines;

	/** Rankings' times */
	private int[][] rankingTime;

	/*
	 * Mode name
	 */
	@Override
	public String getName() {
		return "TECHNICIAN";
	}

	/*
	 * Initialization
	 */
	@Override
	public void playerInit(GameEngine engine, int playerID) {
		owner = engine.owner;
		receiver = engine.owner.receiver;
		goal = 0;
		levelTimer = 0;
		levelTimeOut = false;
		totalTimer = 0;
		rolltime = 0;
		lastgoal = 0;
		lastscore = 0;
		lasttimebonus = 0;
		scgettime = 0;
		regretdispframe = 0;
		lastevent = EVENT_NONE;
		lastb2b = false;
		lastcombo = 0;
		lastpiece = 0;
		bgmlv = 0;

		rankingRank = -1;
		rankingScore = new int[RANKING_TYPE][RANKING_MAX];
		rankingLines = new int[RANKING_TYPE][RANKING_MAX];
		rankingTime = new int[RANKING_TYPE][RANKING_MAX];

		if(owner.replayMode == false) {
			loadSetting(owner.modeConfig);
			loadRanking(owner.modeConfig, engine.ruleopt.strRuleName);
			version = CURRENT_VERSION;
		} else {
			loadSetting(owner.replayProp);
		}

		engine.owner.backgroundStatus.bg = startlevel;
		if(engine.owner.backgroundStatus.bg > 19) engine.owner.backgroundStatus.bg = 19;
		engine.framecolor = GameEngine.FRAME_COLOR_GRAY;
	}

	/**
	 * Set the gravity rate
	 * @param engine GameEngine
	 */
	private void setSpeed(GameEngine engine) {
		int lv = engine.statistics.level;

		if(lv < 0) lv = 0;
		if(lv >= tableGravity.length) lv = tableGravity.length - 1;

		engine.speed.gravity = tableGravity[lv];
		engine.speed.denominator = tableDenominator[lv];
	}

	/**
	 * Set BGM at start of game
	 * @param engine GameEngine
	 */
	private void setStartBgmlv(GameEngine engine) {
		bgmlv = 0;
		while((tableBGMChange[bgmlv] != -1) && (engine.statistics.level >= tableBGMChange[bgmlv])) bgmlv++;
	}

	/*
	 * Called at settings screen
	 */
	@Override
	public boolean onSetting(GameEngine engine, int playerID) {
		// Menu
		if(engine.owner.replayMode == false) {
			// Configuration changes
			int change = updateCursor(engine, 6);

			if(change != 0) {
				engine.playSE("change");

				switch(engine.statc[2]) {
				case 0:
					gametype += change;
					if(gametype < 0) gametype = GAMETYPE_MAX - 1;
					if(gametype > GAMETYPE_MAX - 1) gametype = 0;
					break;
				case 1:
					startlevel += change;
					if(startlevel < 0) startlevel = 29;
					if(startlevel > 29) startlevel = 0;
					engine.owner.backgroundStatus.bg = startlevel;
					if(engine.owner.backgroundStatus.bg > 19) engine.owner.backgroundStatus.bg = 19;
					break;
				case 2:
					//enableTSpin = !enableTSpin;
					tspinEnableType += change;
					if(tspinEnableType < 0) tspinEnableType = 2;
					if(tspinEnableType > 2) tspinEnableType = 0;
					break;
				case 3:
					enableTSpinKick = !enableTSpinKick;
					break;
				case 4:
					enableB2B = !enableB2B;
					break;
				case 5:
					enableCombo = !enableCombo;
					break;
				case 6:
					big = !big;
					break;
				}
			}

			// 決定
			if(engine.ctrl.isPush(Controller.BUTTON_A) && (engine.statc[3] >= 5)) {
				engine.playSE("decide");
				saveSetting(owner.modeConfig);
				receiver.saveModeConfig(owner.modeConfig);
				return false;
			}

			// Cancel
			if(engine.ctrl.isPush(Controller.BUTTON_B)) {
				engine.quitflag = true;
			}

			engine.statc[3]++;
		} else {
			engine.statc[3]++;
			engine.statc[2] = -1;

			if(engine.statc[3] >= 60) {
				return false;
			}
		}

		return true;
	}

	/*
	 * Render the settings screen
	 */
	@Override
	public void renderSetting(GameEngine engine, int playerID) {
		String strTSpinEnable = "";
		if(version >= 1) {
			if(tspinEnableType == 0) strTSpinEnable = "OFF";
			if(tspinEnableType == 1) strTSpinEnable = "T-ONLY";
			if(tspinEnableType == 2) strTSpinEnable = "ALL";
		} else {
			strTSpinEnable = GeneralUtil.getONorOFF(enableTSpin);
		}
		drawMenu(engine, playerID, receiver, 0, EventReceiver.COLOR_BLUE, 0,
				"GAME TYPE", GAMETYPE_NAME[gametype],
				"LEVEL", String.valueOf(startlevel + 1),
				"SPIN BONUS", strTSpinEnable,
				"EZ SPIN", GeneralUtil.getONorOFF(enableTSpinKick),
				"B2B", GeneralUtil.getONorOFF(enableB2B),
				"COMBO",  GeneralUtil.getONorOFF(enableCombo),
				"BIG", GeneralUtil.getONorOFF(big));
	}

	/*
	 * Called for initialization during "Ready" screen
	 */
	@Override
	public void startGame(GameEngine engine, int playerID) {
		engine.statistics.level = startlevel;
		engine.statistics.levelDispAdd = 1;
		engine.b2bEnable = enableB2B;
		if(enableCombo == true) {
			engine.comboType = GameEngine.COMBO_TYPE_NORMAL;
		} else {
			engine.comboType = GameEngine.COMBO_TYPE_DISABLE;
		}
		engine.big = big;

		if(version >= 2) {
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

		engine.speed.lineDelay = 8;

		goal = (engine.statistics.level + 1) * 5;

		setSpeed(engine);

		setStartBgmlv(engine);
		owner.bgmStatus.bgm = bgmlv;

		if((gametype == GAMETYPE_10MIN_EASY) || (gametype == GAMETYPE_10MIN_HARD))
			totalTimer = TIMELIMIT_10MIN;
		if(gametype == GAMETYPE_SPECIAL)
			totalTimer = TIMELIMIT_SPECIAL;
	}

	/*
	 * Render score
	 */
	@Override
	public void renderLast(GameEngine engine, int playerID) {
		receiver.drawScoreFont(engine, playerID, 0, 0, "TECHNICIAN\n(" + GAMETYPE_NAME[gametype] + ")", EventReceiver.COLOR_WHITE);

		if( (engine.stat == GameEngine.STAT_SETTING) || ((engine.stat == GameEngine.STAT_RESULT) && (owner.replayMode == false)) ) {
			if((owner.replayMode == false) && (big == false) && (startlevel == 0) && (engine.ai == null)) {
				receiver.drawScoreFont(engine, playerID, 3, 3, "SCORE   LINE TIME", EventReceiver.COLOR_BLUE);

				for(int i = 0; i < RANKING_MAX; i++) {
					receiver.drawScoreFont(engine, playerID, 0, 4 + i, String.format("%2d", i + 1), EventReceiver.COLOR_YELLOW);
					receiver.drawScoreFont(engine, playerID, 3, 4 + i, String.valueOf(rankingScore[gametype][i]), (i == rankingRank));
					receiver.drawScoreFont(engine, playerID, 11, 4 + i, String.valueOf(rankingLines[gametype][i]), (i == rankingRank));
					receiver.drawScoreFont(engine, playerID, 16, 4 + i, GeneralUtil.getTime(rankingTime[gametype][i]), (i == rankingRank));
				}
			}
		} else {
			// SCORE
			receiver.drawScoreFont(engine, playerID, 0, 3, "SCORE", EventReceiver.COLOR_BLUE);
			String strScore = String.valueOf(engine.statistics.score);
			if((lasttimebonus > 0) && (scgettime < 120) && (gametype != GAMETYPE_SPECIAL))
				strScore += "(+" + lastscore + "+" + lasttimebonus + ")";
			else if((lastscore > 0) && (scgettime < 120))
				strScore += "(+" + lastscore + ")";
			receiver.drawScoreFont(engine, playerID, 0, 4, strScore);

			// GOAL
			receiver.drawScoreFont(engine, playerID, 0, 6, "GOAL", EventReceiver.COLOR_BLUE);
			String strGoal = String.valueOf(goal);
			if((lastgoal != 0) && (scgettime < 120) && (engine.ending == 0))
				strGoal += "(-" + String.valueOf(lastgoal) + ")";
			receiver.drawScoreFont(engine, playerID, 0, 7, strGoal);

			// LEVEL
			receiver.drawScoreFont(engine, playerID, 0, 9, "LEVEL", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 0, 10, String.valueOf(engine.statistics.level + 1));

			// LEVEL TIME
			if(gametype != GAMETYPE_SPECIAL) {
				receiver.drawScoreFont(engine, playerID, 0, 12, "LEVEL TIME", EventReceiver.COLOR_BLUE);
				int remainLevelTime = TIMELIMIT_LEVEL - levelTimer;
				if(remainLevelTime < 0) remainLevelTime = 0;
				int fontcolorLevelTime = EventReceiver.COLOR_WHITE;
				if((remainLevelTime < 60 * 60) && (remainLevelTime > 0)) fontcolorLevelTime = EventReceiver.COLOR_YELLOW;
				if((remainLevelTime < 30 * 60) && (remainLevelTime > 0)) fontcolorLevelTime = EventReceiver.COLOR_ORANGE;
				if((remainLevelTime < 10 * 60) && (remainLevelTime > 0)) fontcolorLevelTime = EventReceiver.COLOR_RED;
				receiver.drawScoreFont(engine, playerID, 0, 13, GeneralUtil.getTime(TIMELIMIT_LEVEL - levelTimer), fontcolorLevelTime);
			}

			// TOTAL TIME
			receiver.drawScoreFont(engine, playerID, 0, 15, "TOTAL TIME", EventReceiver.COLOR_BLUE);
			int totaltime = engine.statistics.time;
			if((gametype == GAMETYPE_10MIN_EASY) || (gametype == GAMETYPE_10MIN_HARD)) totaltime = TIMELIMIT_10MIN - engine.statistics.time;
			if(gametype == GAMETYPE_SPECIAL) totaltime = totalTimer;
			int fontcolorTotalTime = EventReceiver.COLOR_WHITE;
			if((gametype != GAMETYPE_LV15_EASY) && (gametype != GAMETYPE_LV15_HARD)) {
				if((totaltime < 60 * 60) && (totaltime > 0)) fontcolorTotalTime = EventReceiver.COLOR_YELLOW;
				if((totaltime < 30 * 60) && (totaltime > 0)) fontcolorTotalTime = EventReceiver.COLOR_ORANGE;
				if((totaltime < 10 * 60) && (totaltime > 0)) fontcolorTotalTime = EventReceiver.COLOR_RED;
			}
			receiver.drawScoreFont(engine, playerID, 0, 16, GeneralUtil.getTime(totaltime), fontcolorTotalTime);

			// +30sec
			if((gametype == GAMETYPE_SPECIAL) && (lasttimebonus > 0) && (scgettime < 120) && (engine.ending == 0)) {
				receiver.drawScoreFont(engine, playerID, 0, 17, "+" + (lasttimebonus / 60) + "SEC.", EventReceiver.COLOR_YELLOW);
			}

			// Ending time
			if((engine.gameActive) && (engine.ending == 2)) {
				int remainRollTime = TIMELIMIT_ROLL - rolltime;
				if(remainRollTime < 0) remainRollTime = 0;

				receiver.drawScoreFont(engine, playerID, 0, 12, "ROLL TIME", EventReceiver.COLOR_BLUE);
				receiver.drawScoreFont(engine, playerID, 0, 13, GeneralUtil.getTime(remainRollTime), ((remainRollTime > 0) && (remainRollTime < 10 * 60)));
			}

			if(regretdispframe > 0) {
				// REGRET表示
				receiver.drawMenuFont(engine,playerID,2,21,"REGRET",(regretdispframe % 4 == 0),EventReceiver.COLOR_WHITE,EventReceiver.COLOR_ORANGE);
			}
			else if((lastevent != EVENT_NONE) && (scgettime < 120)) {
				// 直前のLine clear type表示
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
	}

	/*
	 * Called after every frame
	 */
	@Override
	public void onLast(GameEngine engine, int playerID) {
		scgettime++;
		if(regretdispframe > 0) regretdispframe--;

		//  levelTime
		if(engine.gameActive && engine.timerActive && (gametype != GAMETYPE_SPECIAL)) {
			levelTimer++;
			int remainTime = TIMELIMIT_LEVEL - levelTimer;

			//  timeMeter
			engine.meterValue = (remainTime * receiver.getMeterMax(engine)) / TIMELIMIT_LEVEL;
			engine.meterColor = GameEngine.METER_COLOR_GREEN;
			if(remainTime <= 60*60) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
			if(remainTime <= 30*60) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
			if(remainTime <= 10*60) engine.meterColor = GameEngine.METER_COLOR_RED;

			if(levelTimer >= TIMELIMIT_LEVEL) {
				//  time切れ
				levelTimeOut = true;

				if((gametype == GAMETYPE_LV15_HARD) || (gametype == GAMETYPE_10MIN_HARD)) {
					engine.gameActive = false;
					engine.timerActive = false;
					engine.resetStatc();
					engine.stat = GameEngine.STAT_GAMEOVER;
				} else if(gametype == GAMETYPE_10MIN_EASY) {
					regretdispframe = 180;
					engine.playSE("regret");
					goal = (engine.statistics.level + 1) * 5;
					levelTimer = 0;
				}
			} else if((remainTime <= 10 * 60) && (remainTime % 60 == 0)) {
				// 10秒前からのカウントダウン
				engine.playSE("countdown");
			}
		}

		// トータルTime
		if(engine.gameActive && engine.timerActive && (gametype != GAMETYPE_LV15_EASY) && (gametype != GAMETYPE_LV15_HARD)) {
			totalTimer--;

			//  timeMeter
			if(gametype == GAMETYPE_SPECIAL) {
				engine.meterValue = (totalTimer * receiver.getMeterMax(engine)) / (5 * 3600);
				engine.meterColor = GameEngine.METER_COLOR_GREEN;
				if(totalTimer <= 60*60) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
				if(totalTimer <= 30*60) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
				if(totalTimer <= 10*60) engine.meterColor = GameEngine.METER_COLOR_RED;
			}

			if(totalTimer < 0) {
				//  time切れ
				engine.gameActive = false;
				engine.timerActive = false;
				engine.resetStatc();

				if((gametype == GAMETYPE_10MIN_EASY) || (gametype == GAMETYPE_10MIN_HARD)) {
					engine.stat = GameEngine.STAT_ENDINGSTART;
				} else {
					engine.stat = GameEngine.STAT_GAMEOVER;
				}

				totalTimer = 0;
			} else if((totalTimer <= 10 * 60) && (totalTimer % 60 == 0)) {
				// 10秒前からのカウントダウン
				engine.playSE("countdown");
			}
		}

		// Ending
		if((engine.gameActive) && (engine.ending == 2)) {
			rolltime++;

			//  timeMeter
			int remainRollTime = TIMELIMIT_ROLL - rolltime;
			engine.meterValue = (remainRollTime * receiver.getMeterMax(engine)) / TIMELIMIT_ROLL;
			engine.meterColor = GameEngine.METER_COLOR_GREEN;
			if(remainRollTime <= 30*60) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
			if(remainRollTime <= 20*60) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
			if(remainRollTime <= 10*60) engine.meterColor = GameEngine.METER_COLOR_RED;

			// Roll 終了
			if(rolltime >= TIMELIMIT_ROLL) {
				scgettime = 0;
				lastscore = totalTimer * 2;
				engine.statistics.score += lastscore;
				engine.statistics.scoreFromOtherBonus += lastscore;
				lastevent = EVENT_NONE;

				engine.gameActive = false;
				engine.resetStatc();
				engine.stat = GameEngine.STAT_EXCELLENT;
			}
		}
	}

	/*
	 * Calculate score
	 */
	@Override
	public void calcScore(GameEngine engine, int playerID, int lines) {
		// Line clear bonus
		int pts = 0;
		int cmb = 0;

		if(engine.tspin) {
			// T-Spin 0 lines
			if(lines == 0) {
				if(engine.tspinmini) {
					pts += 100 * (engine.statistics.level + 1);
					lastevent = EVENT_TSPIN_ZERO_MINI;
				} else {
					pts += 400 * (engine.statistics.level + 1);
					lastevent = EVENT_TSPIN_ZERO;
				}
			}
			// T-Spin 1 line
			else if(lines == 1) {
				if(engine.tspinmini) {
					if(engine.b2b) {
						pts += 300 * (engine.statistics.level + 1);
					} else {
						pts += 200 * (engine.statistics.level + 1);
					}
					lastevent = EVENT_TSPIN_SINGLE_MINI;
				} else {
					if(engine.b2b) {
						pts += 1200 * (engine.statistics.level + 1);
					} else {
						pts += 800 * (engine.statistics.level + 1);
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
					pts += 2400 * (engine.statistics.level + 1);
				} else {
					pts += 1600 * (engine.statistics.level + 1);
				}
				lastevent = EVENT_TSPIN_TRIPLE;
			}
		} else {
			if(lines == 1) {
				pts += 100 * (engine.statistics.level + 1); // 1列
				lastevent = EVENT_SINGLE;
			} else if(lines == 2) {
				pts += 300 * (engine.statistics.level + 1); // 2列
				lastevent = EVENT_DOUBLE;
			} else if(lines == 3) {
				pts += 500 * (engine.statistics.level + 1); // 3列
				lastevent = EVENT_TRIPLE;
			} else if(lines >= 4) {
				// 4 lines
				if(engine.b2b) {
					pts += 1200 * (engine.statistics.level + 1);
				} else {
					pts += 800 * (engine.statistics.level + 1);
				}
				lastevent = EVENT_FOUR;
			}
		}

		lastb2b = engine.b2b;

		// Combo
		if((enableCombo) && (engine.combo >= 1) && (lines >= 1)) {
			cmb += ((engine.combo - 1) * 50) * (engine.statistics.level + 1);
			lastcombo = engine.combo;
		}

		// All clear
		if((lines >= 1) && (engine.field.isEmpty())) {
			engine.playSE("bravo");
			pts += 1800 * (engine.statistics.level + 1);
		}

		// Add to score
		if((pts > 0) || (cmb > 0)) {
			lastpiece = engine.nowPieceObject.id;
			lastscore = pts + cmb;
			scgettime = 0;
			if(lines >= 1) engine.statistics.scoreFromLineClear += pts;
			else engine.statistics.scoreFromOtherBonus += pts;
			engine.statistics.score += pts;

			int cmbindex = engine.combo - 1;
			if(cmbindex < 0) cmbindex = 0;
			if(cmbindex >= COMBO_GOAL_TABLE.length) cmbindex = COMBO_GOAL_TABLE.length - 1;
			lastgoal = ((pts / 100) / (engine.statistics.level + 1)) + COMBO_GOAL_TABLE[cmbindex];
			goal -= lastgoal;
			if(goal <= 0) goal = 0;
		}

		if(engine.ending == 0) {
			// Time bonus
			if((goal <= 0) && (levelTimeOut == false) && (gametype != GAMETYPE_SPECIAL)) {
				lasttimebonus = (TIMELIMIT_LEVEL - levelTimer) * (engine.statistics.level + 1);
				if(lasttimebonus < 0) lasttimebonus = 0;
				scgettime = 0;
				engine.statistics.scoreFromOtherBonus += lasttimebonus;
				engine.statistics.score += lasttimebonus;
			} else if((goal <= 0) && (gametype == GAMETYPE_SPECIAL)) {
				lasttimebonus = TIMELIMIT_SPECIAL_BONUS;
				totalTimer += lasttimebonus;
			} else if(pts > 0) {
				lasttimebonus = 0;
			}

			// BGM fade-out effects and BGM changes
			if((tableBGMChange[bgmlv] != -1) && (engine.statistics.level == tableBGMChange[bgmlv] - 1)) {
				if((goal > 0) && (goal <= 10)) {
					owner.bgmStatus.fadesw = true;
				} else if(goal <= 0) {
					bgmlv++;
					owner.bgmStatus.bgm = bgmlv;
					owner.bgmStatus.fadesw = false;
				}
			}

			if(goal <= 0) {
				if((engine.statistics.level >= 14) && ((gametype == GAMETYPE_LV15_EASY) || (gametype == GAMETYPE_LV15_HARD))) {
					// Ending (LV15-EASY/HARD）
					engine.ending = 1;
					engine.gameActive = false;
				} else if((engine.statistics.level >= 29) && (gametype == GAMETYPE_SPECIAL)) {
					// Ending (SPECIAL）
					engine.ending = 2;
					engine.timerActive = false;
					engine.staffrollEnable = true;
					engine.staffrollEnableStatistics = true;
					engine.staffrollNoDeath = true;
					owner.bgmStatus.bgm = BGMStatus.BGM_ENDING1;
					owner.bgmStatus.fadesw = false;
					engine.playSE("endingstart");
				} else {
					// Level up
					engine.statistics.level++;
					if(engine.statistics.level > 29) engine.statistics.level = 29;

					if(owner.backgroundStatus.bg < 19) {
						owner.backgroundStatus.fadesw = true;
						owner.backgroundStatus.fadecount = 0;
						owner.backgroundStatus.fadebg = engine.statistics.level;
					}

					goal = (engine.statistics.level + 1) * 5;

					levelTimer = 0;
					if(version >= 1) engine.holdUsedCount = 0;

					setSpeed(engine);
					engine.playSE("levelup");
				}
			}
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
	 * Render results screen
	 */
	@Override
	public void renderResult(GameEngine engine, int playerID) {
		receiver.drawMenuFont(engine, playerID,  0, 1, "PLAY DATA", EventReceiver.COLOR_ORANGE);

		drawResultStats(engine, playerID, receiver, 3, EventReceiver.COLOR_BLUE,
				STAT_SCORE, STAT_LINES, STAT_LEVEL, STAT_TIME, STAT_SPL, STAT_LPM);
		drawResultRank(engine, playerID, receiver, 15, EventReceiver.COLOR_BLUE, rankingRank);
	}

	/*
	 * Called when saving replay
	 */
	@Override
	public void saveReplay(GameEngine engine, int playerID, CustomProperties prop) {
		saveSetting(prop);

		// Update rankings
		if((owner.replayMode == false) && (big == false) && (engine.ai == null)) {
			updateRanking(engine.statistics.score, engine.statistics.lines, engine.statistics.time, gametype);

			if(rankingRank != -1) {
				saveRanking(owner.modeConfig, engine.ruleopt.strRuleName);
				receiver.saveModeConfig(owner.modeConfig);
			}
		}
	}

	/**
	 * Load settings from property file
	 * @param prop Property file
	 */
	private void loadSetting(CustomProperties prop) {
		gametype = prop.getProperty("technician.gametype", 0);
		startlevel = prop.getProperty("technician.startlevel", 0);
		tspinEnableType = prop.getProperty("technician.tspinEnableType", 1);
		enableTSpin = prop.getProperty("technician.enableTSpin", true);
		enableTSpinKick = prop.getProperty("technician.enableTSpinKick", true);
		enableB2B = prop.getProperty("technician.enableB2B", true);
		enableCombo = prop.getProperty("technician.enableCombo", true);
		big = prop.getProperty("technician.big", false);
		version = prop.getProperty("technician.version", 0);
	}

	/**
	 * Save settings to property file
	 * @param prop Property file
	 */
	private void saveSetting(CustomProperties prop) {
		prop.setProperty("technician.gametype", gametype);
		prop.setProperty("technician.startlevel", startlevel);
		prop.setProperty("technician.tspinEnableType", tspinEnableType);
		prop.setProperty("technician.enableTSpin", enableTSpin);
		prop.setProperty("technician.enableTSpinKick", enableTSpinKick);
		prop.setProperty("technician.enableB2B", enableB2B);
		prop.setProperty("technician.enableCombo", enableCombo);
		prop.setProperty("technician.big", big);
		prop.setProperty("technician.version", version);
	}

	/**
	 * Read rankings from property file
	 * @param prop Property file
	 * @param ruleName Rule name
	 */
	private void loadRanking(CustomProperties prop, String ruleName) {
		for(int i = 0; i < RANKING_MAX; i++) {
			for(int gametypeIndex = 0; gametypeIndex < RANKING_TYPE; gametypeIndex++) {
				rankingScore[gametypeIndex][i] = prop.getProperty("technician.ranking." + ruleName + "." + gametypeIndex + ".score." + i, 0);
				rankingLines[gametypeIndex][i] = prop.getProperty("technician.ranking." + ruleName + "." + gametypeIndex + ".lines." + i, 0);
				rankingTime[gametypeIndex][i] = prop.getProperty("technician.ranking." + ruleName + "." + gametypeIndex + ".time." + i, 0);
			}
		}
	}

	/**
	 * Save rankings to property file
	 * @param prop Property file
	 * @param ruleName Rule name
	 */
	private void saveRanking(CustomProperties prop, String ruleName) {
		for(int i = 0; i < RANKING_MAX; i++) {
			for(int gametypeIndex = 0; gametypeIndex < RANKING_TYPE; gametypeIndex++) {
				prop.setProperty("technician.ranking." + ruleName + "." + gametypeIndex + ".score." + i, rankingScore[gametypeIndex][i]);
				prop.setProperty("technician.ranking." + ruleName + "." + gametypeIndex + ".lines." + i, rankingLines[gametypeIndex][i]);
				prop.setProperty("technician.ranking." + ruleName + "." + gametypeIndex + ".time." + i, rankingTime[gametypeIndex][i]);
			}
		}
	}

	/**
	 * Update rankings
	 * @param sc Score
	 * @param li Lines
	 * @param time Time
	 * @param type Game type
	 */
	private void updateRanking(int sc, int li, int time, int type) {
		rankingRank = checkRanking(sc, li, time, type);

		if(rankingRank != -1) {
			// Shift down ranking entries
			for(int i = RANKING_MAX - 1; i > rankingRank; i--) {
				rankingScore[type][i] = rankingScore[type][i - 1];
				rankingLines[type][i] = rankingLines[type][i - 1];
				rankingTime[type][i] = rankingTime[type][i - 1];
			}

			// Add new data
			rankingScore[type][rankingRank] = sc;
			rankingLines[type][rankingRank] = li;
			rankingTime[type][rankingRank] = time;
		}
	}

	/**
	 * Calculate ranking position
	 * @param sc Score
	 * @param li Lines
	 * @param time Time
	 * @return Position (-1 if unranked)
	 */
	private int checkRanking(int sc, int li, int time, int type) {
		for(int i = 0; i < RANKING_MAX; i++) {
			if(sc > rankingScore[type][i]) {
				return i;
			} else if((sc == rankingScore[type][i]) && (li > rankingLines[type][i])) {
				return i;
			} else if((sc == rankingScore[type][i]) && (li == rankingLines[type][i]) && (time < rankingTime[type][i])) {
				return i;
			}
		}

		return -1;
	}
}
