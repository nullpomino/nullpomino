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

import mu.nu.nullpo.game.component.Controller;
import mu.nu.nullpo.game.event.EventReceiver;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.util.CustomProperties;
import mu.nu.nullpo.util.GeneralUtil;

/**
 * RETRO MANIA mode (Original from NullpoUE build 121909 by Zircean)
 */
public class RetroManiaMode extends AbstractMode {
	/** Current version */
	private static final int CURRENT_VERSION = 2;

	/** Poweron Pattern */
	private static final String STRING_POWERON_PATTERN =
		"4040050165233516506133350555213560141520224542633206134255165200333560031332022463366645230432611435"+
		"5335503262512313515002442203656664131543211220146344201325061401134610644005663441101532234006340505"+
		"4621441004021465010225623313311635326311133504346120621126223156523530636115044065300222245330252325"+
		"5563545455656660124120450663502223206465164461126135621055103645066644052535021110020361422122352566"+
		"1564343513043465103636404534525056551422631026052022163516150316500504641606133253660234134530365424"+
		"4124644510156225214120146050543513004022131140054341604166064441010614144404145451160041314635320626"+
		"0246251556635262420616451361336106153451563316660054255631510320566516465265421144640513424316315421"+
		"6644140264401653410103024436251016522052305506020020331200443440341001604426324366453255122653512056"+
		"4234334231212152312006153023444306242003331046140330636540231321265610510125435251421621035523001404"+
		"0335464640401464125332132315552404146634264364245513600336065666305002023203545052006445544450440460";

	/** Gravity table */
	private static final int tableDenominator[][] = {{48, 32, 24, 18, 14, 12, 10, 8, 6, 4, 12, 10, 8, 6, 4, 2},
													 {48, 24, 18, 15, 12, 10,  8, 6, 4, 2, 10,  8, 6, 4, 2, 1},
													 {40, 20, 16, 12, 10,  8,  6, 4, 2, 1, 10,  8, 6, 4, 2, 1},
													 {30, 15, 12, 10,  8,  6,  4, 2, 1, 1,  8,  6, 4, 2, 1, 1}};

	/** Time until auto-level up occers */
	private static final int levelTime[] = {3584, 2304, 2304, 2304, 2304, 2304, 2304, 2304,
											2304, 3584, 3584, 2304, 2304, 2304, 2304, 3584};

	/** Name of game types */
	private static final String[] GAMETYPE_NAME = {"EASY","NORMAL","HARD","HARDEST"};

	/** Number of game type */
	private static final int GAMETYPE_MAX = 4;

	/** Number of ranking records */
	private static final int RANKING_MAX = 10;

	/** Number of ranking types */
	private static final int RANKING_TYPE = 4;

	/** Max score */
	private static final int MAX_SCORE = 999999;

	/** Max lines */
	private static final int MAX_LINES = 999;

	/** Max level */
	private static final int MAX_LEVEL = 99;

	/** GameManager object (Manages entire game status) */

	/** EventReceiver object (This receives many game events, can also be used for drawing the fonts.) */

	/** Amount of points you just get from line clears */
	private int lastscore;

	/** Elapsed time from last line clear (lastscore is displayed to screen until this reaches to 120) */
	private int scgettime;

	/** Selected game type */
	private int gametype;

	/** Selected starting level */
	private int startlevel;

	/** Level timer */
	private int levelTimer;

	/** Amount of lines cleared (It will be reset when the level increases) */
	private int linesAfterLastLevelUp;

	/** Big mode on/off */
	private boolean big;

	/** Poweron Pattern on/off */
	private boolean poweron;

	/** Version of this mode */
	private int version;

	/** Your place on leaderboard (-1: out of rank) */
	private int rankingRank;

	/** Score records */
	private int[][] rankingScore;

	/** Line records */
	private int[][] rankingLines;

	/** Time records */
	private int[][] rankingTime;

	/**
	 * Returns the name of this mode
	 */
	@Override
	public String getName() {
		return "RETRO MANIA";
	}

	/**
	 * This function will be called when the game enters the main game screen.
	 */
	@Override
	public void playerInit(GameEngine engine, int playerID) {
		owner = engine.owner;
		receiver = engine.owner.receiver;
		lastscore = 0;
		scgettime = 0;
		levelTimer = 0;
		linesAfterLastLevelUp = 0;

		rankingRank = -1;
		rankingScore = new int[RANKING_TYPE][RANKING_MAX];
		rankingLines = new int[RANKING_TYPE][RANKING_MAX];
		rankingTime = new int[RANKING_TYPE][RANKING_MAX];

		engine.tspinEnable = false;
		engine.b2bEnable = false;
		engine.comboType = GameEngine.COMBO_TYPE_DISABLE;
		engine.bighalf = false;
		engine.bigmove = false;

		engine.speed.are = 30;
		engine.speed.areLine = 30;
		engine.speed.lineDelay = 42;
		engine.speed.lockDelay = 30;
		engine.speed.das = 20;

		if(owner.replayMode == false) {
			loadSetting(owner.modeConfig);
			loadRanking(owner.modeConfig, engine.ruleopt.strRuleName);
			version = CURRENT_VERSION;
		} else {
			loadSetting(owner.replayProp);
		}

		engine.owner.backgroundStatus.bg = startlevel/2;
		if(engine.owner.backgroundStatus.bg > 19) engine.owner.backgroundStatus.bg = 19;
		engine.framecolor = GameEngine.FRAME_COLOR_GRAY;
	}

	/**
	 * Set the gravity speed
	 * @param engine GameEngine object
	 */
	private void setSpeed(GameEngine engine) {
		int lv = engine.statistics.level;

		if(lv < 0) lv = 0;
		if(lv >= tableDenominator[0].length) lv = tableDenominator[0].length - 1;

		engine.speed.gravity = 1;
		engine.speed.denominator = tableDenominator[gametype][lv];
	}

	/**
	 * Main routine for game setup screen
	 */
	@Override
	public boolean onSetting(GameEngine engine, int playerID) {
		if(engine.owner.replayMode == false) {
			// Configuration changes
			int change = updateCursor(engine, 3);

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
					if (startlevel < 0) startlevel = 15;
					if (startlevel > 15) startlevel = 0;
					engine.owner.backgroundStatus.bg = startlevel/2;
					break;
				case 2:
					big = !big;
					break;
				case 3:
					poweron = !poweron;
					break;
				}
			}

			// Check for A button, when pressed this will begin the game
			if(engine.ctrl.isPush(Controller.BUTTON_A) && (engine.statc[3] >= 5)) {
				engine.playSE("decide");
				saveSetting(owner.modeConfig);
				receiver.saveModeConfig(owner.modeConfig);
				return false;
			}

			// Check for B button, when pressed this will shutdown the game engine.
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

	/**
	 * Renders game setup screen
	 */
	@Override
	public void renderSetting(GameEngine engine, int playerID) {
		drawMenu(engine, playerID, receiver, 0, EventReceiver.COLOR_BLUE, 0,
				"DIFFICULTY", GAMETYPE_NAME[gametype],
				"LEVEL", String.valueOf(startlevel),
				"BIG", GeneralUtil.getONorOFF(big),
				"POWERON", GeneralUtil.getONorOFF(poweron));
	}

	/**
	 * Ready
	 */
	@Override
	public boolean onReady(GameEngine engine, int playerID) {
		if(engine.statc[0] == 0) {
			if(poweron) {
				engine.nextPieceArrayID = GeneralUtil.createNextPieceArrayFromNumberString(STRING_POWERON_PATTERN);
			}
		}
		return false;
	}

	/**
	 * This function will be called before the game actually begins (after Ready&Go screen disappears)
	 */
	@Override
	public void startGame(GameEngine engine, int playerID) {
		engine.statistics.level = startlevel;
		engine.statistics.levelDispAdd = 1;

		engine.big = big;

		owner.bgmStatus.bgm = 0;
		setSpeed(engine);
	}

	/**
	 * Renders HUD (leaderboard or game statistics)
	 */
	@Override
	public void renderLast(GameEngine engine, int playerID) {
		receiver.drawScoreFont(engine, playerID, 0, 0, "RETRO MANIA", EventReceiver.COLOR_GREEN);
		receiver.drawScoreFont(engine, playerID, 0, 1, "("+GAMETYPE_NAME[gametype]+" SPEED)", EventReceiver.COLOR_GREEN);

		if( (engine.stat == GameEngine.STAT_SETTING) || ((engine.stat == GameEngine.STAT_RESULT) && (owner.replayMode == false)) ) {
			// Leaderboard
			if((owner.replayMode == false) && (big == false) && (startlevel == 0) && (engine.ai == null)) {
				float scale = (receiver.getNextDisplayType() == 2) ? 0.5f : 1.0f;
				int topY = (receiver.getNextDisplayType() == 2) ? 6 : 4;
				receiver.drawScoreFont(engine, playerID, 3, topY-1, "SCORE  LINE TIME", EventReceiver.COLOR_BLUE, scale);

				for(int i = 0; i < RANKING_MAX; i++) {
					receiver.drawScoreFont(engine, playerID, 0, topY+i, String.format("%2d", i + 1), EventReceiver.COLOR_YELLOW, scale);
					receiver.drawScoreFont(engine, playerID, 3, topY+i, String.valueOf(rankingScore[gametype][i]), (i == rankingRank), scale);
					receiver.drawScoreFont(engine, playerID, 10, topY+i, String.valueOf(rankingLines[gametype][i]), (i == rankingRank), scale);
					receiver.drawScoreFont(engine, playerID, 15, topY+i, GeneralUtil.getTime(rankingTime[gametype][i]), (i == rankingRank), scale);
				}
			}
		} else {
			// Game statistics
			receiver.drawScoreFont(engine, playerID, 0, 3, "SCORE", EventReceiver.COLOR_BLUE);
			String strScore;
			if((lastscore == 0) || (scgettime >= 120)) {
				strScore = String.valueOf(engine.statistics.score);
			} else {
				strScore = String.valueOf(engine.statistics.score) + "(+" + String.valueOf(lastscore) + ")";
			}
			receiver.drawScoreFont(engine, playerID, 0, 4, strScore);

			receiver.drawScoreFont(engine, playerID, 0, 6, "LINE", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 0, 7, String.valueOf(engine.statistics.lines));

			receiver.drawScoreFont(engine, playerID, 0, 9, "LEVEL", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 0, 10, String.valueOf(engine.statistics.level));

			receiver.drawScoreFont(engine, playerID, 0, 12, "TIME", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 0, 13, GeneralUtil.getTime(engine.statistics.time));

			//receiver.drawScoreFont(engine, playerID, 0, 15, String.valueOf(linesAfterLastLevelUp));
			//receiver.drawScoreFont(engine, playerID, 0, 16, GeneralUtil.getTime(levelTime[Math.min(engine.statistics.level,15)] - levelTimer));
		}
	}

	/**
	 * This function will be called when the game timer updates
	 */
	@Override
	public void onLast(GameEngine engine, int playerID) {
		scgettime++;
		if(engine.timerActive) levelTimer++;

		// Max-out score, lines, and level
		if(version >= 2) {
			if(engine.statistics.score > MAX_SCORE) engine.statistics.score = MAX_SCORE;
			if(engine.statistics.lines > MAX_LINES) engine.statistics.lines = MAX_LINES;
			if(engine.statistics.level > MAX_LEVEL) engine.statistics.level = MAX_LEVEL;
		}
	}

	/**
	 * Calculates line-clear score
	 * (This function will be called even if no lines are cleared)
	 */
	@Override
	public void calcScore(GameEngine engine, int playerID, int lines) {
		// Determines line-clear bonus
		int pts = 0;
		int mult = Math.min(engine.statistics.level/2 + 1,5);
		if(lines == 1) {
			pts += 100 * mult; // Single
		} else if(lines == 2) {
			pts += 400 * mult; // Double
		} else if(lines == 3) {
			pts += 900 * mult; // Triple
		} else if(lines >= 4) {
			pts += 2000 * mult; // Four
		}

		// Perfect clear bonus
		if(engine.field.isEmpty()) {
			engine.playSE("bravo");
			if(version >= 2) pts *= 10;
			else pts *= 20;
		}

		// Add score
		if(pts > 0) {
			lastscore = pts;
			scgettime = 0;
			engine.statistics.scoreFromLineClear += pts;
			engine.statistics.score += pts;
		}

		// Add lines
		linesAfterLastLevelUp += lines;

		// Update the meter
		engine.meterValue = ((linesAfterLastLevelUp % 4) * receiver.getMeterMax(engine)) / 3;
		engine.meterColor = GameEngine.METER_COLOR_GREEN;
		if(linesAfterLastLevelUp >= 1) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
		if(linesAfterLastLevelUp >= 2) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
		if(linesAfterLastLevelUp >= 3) engine.meterColor = GameEngine.METER_COLOR_RED;

		// Level up
		if( (linesAfterLastLevelUp >= 4) ||
			((levelTimer >= levelTime[Math.min(engine.statistics.level,15)]) && (lines == 0)) )
		{
			engine.statistics.level++;

			owner.backgroundStatus.fadecount = 0;
			owner.backgroundStatus.fadebg = (engine.statistics.level/2);
			if (owner.backgroundStatus.fadebg > 19) owner.backgroundStatus.fadebg = 19;
			owner.backgroundStatus.fadesw = (owner.backgroundStatus.fadebg != owner.backgroundStatus.bg);

			levelTimer = 0;
			linesAfterLastLevelUp = 0;

			engine.meterValue = 0;

			setSpeed(engine);
			engine.playSE("levelup");
		}
	}

	/**
	 * This function will be called when soft-drop is used
	 */
	@Override
	public void afterSoftDropFall(GameEngine engine, int playerID, int fall) {
		if((version >= 2) && (engine.speed.denominator == 1)) return;
		engine.statistics.scoreFromSoftDrop += fall;
		engine.statistics.score += fall;
	}

	/**
	 * This function will be called when hard-drop is used
	 */
	@Override
	public void afterHardDropFall(GameEngine engine, int playerID, int fall) {
		engine.statistics.scoreFromHardDrop += fall;
		engine.statistics.score += fall;
	}

	/**
	 * Renders game result screen
	 */
	@Override
	public void renderResult(GameEngine engine, int playerID) {
		receiver.drawMenuFont(engine, playerID,  0, 1, "PLAY DATA", EventReceiver.COLOR_ORANGE);

		drawResultStats(engine, playerID, receiver, 3, EventReceiver.COLOR_BLUE,
				STAT_SCORE, STAT_LINES, STAT_LEVEL, STAT_TIME);
		drawResultRank(engine, playerID, receiver, 11, EventReceiver.COLOR_BLUE, rankingRank);
	}

	/**
	 * This function will be called when the replay data is going to be saved
	 */
	@Override
	public void saveReplay(GameEngine engine, int playerID, CustomProperties prop) {
		saveSetting(prop);

		// Checks/Updates the ranking
		if((owner.replayMode == false) && (big == false) && (engine.ai == null)) {
			updateRanking(engine.statistics.score, engine.statistics.lines, engine.statistics.time, gametype);

			if(rankingRank != -1) {
				saveRanking(owner.modeConfig, engine.ruleopt.strRuleName);
				receiver.saveModeConfig(owner.modeConfig);
			}
		}
	}

	/**
	 * Load the settings
	 */
	protected void loadSetting(CustomProperties prop) {
		startlevel = prop.getProperty("retromania.startlevel", 0);
		gametype = prop.getProperty("retromania.gametype", 0);
		big = prop.getProperty("retromania.big", false);
		poweron = prop.getProperty("retromania.poweron", false);
		version = prop.getProperty("retromania.version", 0);
	}

	/**
	 * Save the settings
	 */
	protected void saveSetting(CustomProperties prop) {
		prop.setProperty("retromania.startlevel", startlevel);
		prop.setProperty("retromania.gametype", gametype);
		prop.setProperty("retromania.big", big);
		prop.setProperty("retromania.poweron", poweron);
		prop.setProperty("retromania.version", version);
	}

	/**
	 * Load the ranking
	 */
	private void loadRanking(CustomProperties prop, String ruleName) {
		for(int i = 0; i < RANKING_MAX; i++) {
			for (int gametypeIndex = 0; gametypeIndex < RANKING_TYPE; gametypeIndex++)
			{
				rankingScore[gametypeIndex][i] = prop.getProperty("retromania.ranking." + ruleName + "." + gametypeIndex + ".score." + i, 0);
				rankingLines[gametypeIndex][i] = prop.getProperty("retromania.ranking." + ruleName + "." + gametypeIndex + ".lines." + i, 0);
				rankingTime[gametypeIndex][i] = prop.getProperty("retromania.ranking." + ruleName + "." + gametypeIndex + ".time." + i, 0);

				if(rankingScore[gametypeIndex][i] > MAX_SCORE) rankingScore[gametypeIndex][i] = MAX_SCORE;
				if(rankingLines[gametypeIndex][i] > MAX_LINES) rankingLines[gametypeIndex][i] = MAX_LINES;
			}
		}
	}

	/**
	 * Save the ranking
	 */
	private void saveRanking(CustomProperties prop, String ruleName) {
		for(int i = 0; i < RANKING_MAX; i++) {
			for (int gametypeIndex = 0; gametypeIndex < RANKING_TYPE; gametypeIndex++)
			{
				prop.setProperty("retromania.ranking." + ruleName + "." + gametypeIndex + ".score." + i, rankingScore[gametypeIndex][i]);
				prop.setProperty("retromania.ranking." + ruleName + "." + gametypeIndex + ".lines." + i, rankingLines[gametypeIndex][i]);
				prop.setProperty("retromania.ranking." + ruleName + "." + gametypeIndex + ".time." + i, rankingTime[gametypeIndex][i]);
			}
		}
	}

	/**
	 * Update the ranking
	 */
	private void updateRanking(int sc, int li, int time, int type) {
		rankingRank = checkRanking(sc, li, time, type);

		if(rankingRank != -1) {
			// Shift the old records
			for(int i = RANKING_MAX - 1; i > rankingRank; i--) {
				rankingScore[type][i] = rankingScore[type][i - 1];
				rankingLines[type][i] = rankingLines[type][i - 1];
				rankingTime[type][i] = rankingTime[type][i - 1];
			}

			// Insert a new record
			rankingScore[type][rankingRank] = sc;
			rankingLines[type][rankingRank] = li;
			rankingTime[type][rankingRank] = time;
		}
	}

	/**
	 * This function will check the ranking and returns which place you are. (-1: Out of rank)
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
