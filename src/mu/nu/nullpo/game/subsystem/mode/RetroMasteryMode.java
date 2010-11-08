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
 * RETRO MASTERY mode by Pineapple 20100722 - 20100808
 */
public class RetroMasteryMode extends DummyMode {
	/** Current version of this mode */
	private static final int CURRENT_VERSION = 1;

	/** Denominator table */
	private static final int tableDenominator[] =
	{
		//	0  1  2  3  4  5  6  7  8  9    +xx
		   48,40,32,27,22,18,15,12,10, 8, // 00
		    7, 6,11, 5, 9, 4, 7, 3,11,10, // 10
		    9, 8,15,14,13,12,11,10, 9, 8, // 20
		    1
	};

	/** Gravity table */
	private static final int tableGravity[] =
	{
		//	0  1  2  3  4  5  6  7  8  9    +xx
		    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, // 00
		    1, 1, 2, 1, 2, 1, 2, 1, 4, 4, // 10
		    4, 4, 8, 8, 8, 8, 8, 8, 8, 8, // 20
		    1
	};

	/** Lock delay table */
	private static final int tableLockDelay[] =
	{
		//	0  1  2  3  4  5  6  7  8  9    +xx
		   60,52,45,39,34,30,27,24,22,20, // 00
		   19,18,17,16,15,14,13,12,11,10, // 10
		    9, 8, 8, 8, 8, 7, 7, 7, 7, 7, // 20
		    6
	};

	/** Game types */
	private static final int GAMETYPE_200 = 0,
							 GAMETYPE_ENDLESS = 1,
							 GAMETYPE_PRESSURE = 2,
							 GAMETYPE_MAX = 3;

	/** Game type name */
	private static final String[] GAMETYPE_NAME = {"200","ENDLESS","PRESSURE"};

	/** Number of ranking records */
	private static final int RANKING_MAX = 10;

	/** Number of ranking types */
	private static final int RANKING_TYPE = 3;

	/** GameManager object (Manages entire game status) */
	private GameManager owner;

	/** EventReceiver object (This receives many game events, can also be used for drawing the fonts.) */
	private EventReceiver receiver;

	/** Amount of points you just get from line clears */
	private int lastscore;

	/** Elapsed time from last line clear (lastscore is displayed to screen until this reaches to 120) */
	private int scgettime;

	/** Selected game type */
	private int gametype;

	/** Selected starting level */
	private int startlevel;

	/** Used for soft drop scoring */
	private int softdropscore;

	/** Used for hard drop scoring */
	private int harddropscore;

	/** Number of "lines" cleared (most things use this instead of engine.statistics.lines); don't ask me why I called it this...*/
	private int loons;

	/** Number of line clear actions */
	private int actions;

	/** Efficiency (engine.statistics.lines / actions) */
	private float efficiency;

	/** Next level lines */
	private int levellines;

	/** Big mode on/off */
	private boolean big;

	/** Version of this mode */
	private int version;

	/** Your place on leaderboard (-1: out of rank) */
	private int rankingRank;

	/** Score records */
	private int[][] rankingScore;

	/** Line records */
	private int[][] rankingLines;

	/** Level records */
	private int[][] rankingLevel;

	/**
	 * Returns the name of this mode
	 */
	@Override
	public String getName() {
		return "RETRO MASTERY";
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
		softdropscore = 0;
		harddropscore = 0;
		levellines = 0;
		loons = 0;
		actions = 0;
		efficiency = 0;

		rankingRank = -1;
		rankingScore = new int[RANKING_TYPE][RANKING_MAX];
		rankingLines = new int[RANKING_TYPE][RANKING_MAX];
		rankingLevel = new int[RANKING_TYPE][RANKING_MAX];

		engine.tspinEnable = false;
		engine.b2bEnable = false;
		engine.comboType = GameEngine.COMBO_TYPE_DISABLE;
		engine.bighalf = true;
		engine.bigmove = true;

		engine.speed.are = 12;
		engine.speed.areLine = 15;
		engine.speed.das = 12;

		if(owner.replayMode == false) {
			loadSetting(owner.modeConfig);
			loadRanking(owner.modeConfig, engine.ruleopt.strRuleName);
			version = CURRENT_VERSION;
		} else {
			loadSetting(owner.replayProp);
		}

		engine.owner.backgroundStatus.bg = gametype == GAMETYPE_PRESSURE ? 0 : startlevel;
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
		if(lv >= tableDenominator.length) lv = tableDenominator.length - 1;

		engine.speed.gravity = tableGravity[lv];
		engine.speed.denominator = tableDenominator[lv];
		engine.speed.lockDelay = tableLockDelay[lv];
		engine.speed.lineDelay = lv >= 10 ? 20 : 25;
	}

	/**
	 * Main routine for game setup screen
	 */
	@Override
	public boolean onSetting(GameEngine engine, int playerID) {
		// Menu
		if(engine.owner.replayMode == false) {
			// Check for UP button, when pressed it will move cursor up.
			if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_UP)) {
				engine.statc[2]--;
				if(engine.statc[2] == 1 && gametype == GAMETYPE_PRESSURE) engine.statc[2]--;
				if(engine.statc[2] < 0) engine.statc[2] = 2;
				receiver.playSE("cursor");
			}
			// Check for DOWN button, when pressed it will move cursor down.
			if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_DOWN)) {
				engine.statc[2]++;
				if(engine.statc[2] == 1 && gametype == GAMETYPE_PRESSURE) engine.statc[2]++;
				if(engine.statc[2] > 2) engine.statc[2] = 0;
				receiver.playSE("cursor");
			}

			// Check for LEFT/RIGHT keys
			int change = 0;
			if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_LEFT)) change = -1;
			if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_RIGHT)) change = 1;

			if(change != 0) {
				receiver.playSE("change");

				switch(engine.statc[2]) {
				case 0:
					gametype += change;
					if(gametype < 0) gametype = GAMETYPE_MAX - 1;
					if(gametype > GAMETYPE_MAX - 1) gametype = 0;
					engine.owner.backgroundStatus.bg = gametype == GAMETYPE_PRESSURE ? 0 : startlevel;
					break;
				case 1:
					startlevel += change;
					if(startlevel < 0) startlevel = 19;
					if(startlevel > 19) startlevel = 0;
					engine.owner.backgroundStatus.bg = startlevel;
					break;
				case 2:
					big = !big;
					break;
				}
			}

			// Check for A button, when pressed this will begin the game
			if(engine.ctrl.isPush(Controller.BUTTON_A) && (engine.statc[3] >= 5)) {
				receiver.playSE("decide");
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
		if(engine.owner.replayMode == false) {
			receiver.drawMenuFont(engine, playerID, 0, (engine.statc[2] * 2) + 1, "b", EventReceiver.COLOR_RED);
		}

		receiver.drawMenuFont(engine, playerID, 0, 0, "GAME TYPE", EventReceiver.COLOR_BLUE);
		receiver.drawMenuFont(engine, playerID, 1, 1, GAMETYPE_NAME[gametype], (engine.statc[2] == 0));
		if(gametype != GAMETYPE_PRESSURE){
			receiver.drawMenuFont(engine, playerID, 0, 2, "LEVEL", EventReceiver.COLOR_BLUE);
			receiver.drawMenuFont(engine, playerID, 1, 3, String.format("%02d", startlevel), (engine.statc[2] == 1));
		}
		receiver.drawMenuFont(engine, playerID, 0, 4, "BIG", EventReceiver.COLOR_BLUE);
		receiver.drawMenuFont(engine, playerID, 1, 5, GeneralUtil.getONorOFF(big), (engine.statc[2] == 2));
	}

	/**
	 * This function will be called before the game actually begins (after Ready&Go screen disappears)
	 */
	@Override
	public void startGame(GameEngine engine, int playerID) {
		engine.big = big;
		engine.statistics.levelDispAdd = 1;

		switch (gametype) {
		case GAMETYPE_PRESSURE:
			engine.statistics.level = 0;
			levellines = 5;
			break;
		case GAMETYPE_200:
			engine.statistics.level = startlevel;
			levellines = 10 * Math.min(startlevel + 1, 10);
			break;
		case GAMETYPE_ENDLESS:
			engine.statistics.level = startlevel;
			levellines = startlevel <= 9 ? (startlevel + 1) * 10 : (startlevel + 11) * 5 ;
			break;
		}

		setSpeed(engine);
	}

	/**
	 * Renders HUD (leaderboard or game statistics)
	 */
	@Override
	public void renderLast(GameEngine engine, int playerID) {
		receiver.drawScoreFont(engine, playerID, 0, 0, "RETRO MASTERY", EventReceiver.COLOR_GREEN);
		receiver.drawScoreFont(engine, playerID, 0, 1, "("+GAMETYPE_NAME[gametype]+")", EventReceiver.COLOR_GREEN);

		if( (engine.stat == GameEngine.STAT_SETTING) || ((engine.stat == GameEngine.STAT_RESULT) && (owner.replayMode == false)) ) {
			if((owner.replayMode == false) && (big == false) && (engine.ai == null)) {
				receiver.drawScoreFont(engine, playerID, 3, 3, "SCORE    LINE LV.", EventReceiver.COLOR_BLUE);

				for(int i = 0; i < RANKING_MAX; i++) {
					receiver.drawScoreFont(engine, playerID, 0, 4 + i, String.format("%2d", i + 1), EventReceiver.COLOR_YELLOW);
					receiver.drawScoreFont(engine, playerID, 3, 4 + i, String.valueOf(rankingScore[gametype][i]), (i == rankingRank));
					receiver.drawScoreFont(engine, playerID, 12, 4 + i, String.valueOf(rankingLines[gametype][i]), (i == rankingRank));
					receiver.drawScoreFont(engine, playerID, 17, 4 + i, String.format("%02d", rankingLevel[gametype][i]), (i == rankingRank));
				}
			}
		} else {
			receiver.drawScoreFont(engine, playerID, 0, 3, "SCORE", EventReceiver.COLOR_BLUE);
			String strScore;
			if((lastscore == 0) || (scgettime >= 120)) {
				strScore = String.valueOf(engine.statistics.score);
			} else {
				strScore = String.valueOf(engine.statistics.score) + " (+" + String.valueOf(lastscore) + ")";
			}
			receiver.drawScoreFont(engine, playerID, 0, 4, strScore);

			String strLine;
			strLine = String.valueOf(loons);

			receiver.drawScoreFont(engine, playerID, 0, 6, "LINES", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 0, 7, strLine);

			receiver.drawScoreFont(engine, playerID, 0, 9, "LEVEL", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 0, 10, String.format("%02d", engine.statistics.level));

			receiver.drawScoreFont(engine, playerID, 0, 12, "TIME", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 0, 13, GeneralUtil.getTime(engine.statistics.time));
		}
	}

	/**
	 * This function will be called when the game timer updates
	 */
	@Override
	public void onLast(GameEngine engine, int playerID) {
		scgettime++;
	}

	/**
	 * Calculates line-clear score
	 * (This function will be called even if no lines are cleared)
	 */
	@Override
	public void calcScore(GameEngine engine, int playerID, int lines) {
		softdropscore /= 2;
		engine.statistics.score += softdropscore;
		engine.statistics.scoreFromSoftDrop += softdropscore;
		softdropscore = 0;

		harddropscore /= 2;
		engine.statistics.score += harddropscore;
		engine.statistics.scoreFromHardDrop += harddropscore;
		harddropscore = 0;

		// Line clear score
		int pts = 0;
		if(lines == 1) {
			pts += 40 * (engine.statistics.level + 1); // Single
			loons += 1;
		} else if(lines == 2) {
			pts += 100 * (engine.statistics.level + 1); // Double
			loons += 2;
		} else if(lines == 3) {
			pts += 200 * (engine.statistics.level + 1); // Triple
			loons += 3;
		} else if(lines >= 4) {
			pts += 300 * (engine.statistics.level + 1); // Four
			loons += 3;
		}

		// Do the ending (at 200 lines for now)
		if(gametype == GAMETYPE_200 && loons >= 200) {
			engine.ending = 1;
			engine.gameEnded();
		}

		// Add score to total
		if(pts > 0) {
			actions++;
			lastscore = pts;
			scgettime = 0;
			engine.statistics.scoreFromLineClear += pts;
			engine.statistics.score += pts;
		}

		efficiency = (actions != 0) ? engine.statistics.lines / (float)actions : 0;

		if(loons >= levellines ) {
			// Level up
			engine.statistics.level++;

			levellines += gametype == GAMETYPE_PRESSURE ? 5 : 10;

			owner.backgroundStatus.fadesw = true;
			owner.backgroundStatus.fadecount = 0;

			int lv = engine.statistics.level;

			if(lv < 0) lv = 0;
			else if(lv >= 19) lv = 19;

			owner.backgroundStatus.fadebg = lv;

			setSpeed(engine);
			receiver.playSE("levelup");
		}

		// Update meter
		int togo = levellines - loons;
		if (gametype == GAMETYPE_PRESSURE) {
			engine.meterValue = ((loons % 5) * receiver.getMeterMax(engine)) / 4;
			if(togo == 1) engine.meterColor = GameEngine.METER_COLOR_RED;
			else if(togo == 2) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
			else if(togo == 3) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
			else engine.meterColor = GameEngine.METER_COLOR_GREEN;
		} else if (engine.statistics.level == startlevel && startlevel != 0){
			engine.meterValue = (loons * receiver.getMeterMax(engine)) / (levellines - 1);
			if(togo <= 5) engine.meterColor = GameEngine.METER_COLOR_RED;
			else if(togo <= 10) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
			else if(togo <= 20) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
			else engine.meterColor = GameEngine.METER_COLOR_GREEN;
		} else {
			engine.meterValue = ((10 - togo) * receiver.getMeterMax(engine)) / 9;
			if(togo <= 2) engine.meterColor = GameEngine.METER_COLOR_RED;
			else if(togo <= 5) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
			else if(togo <= 8) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
			else engine.meterColor = GameEngine.METER_COLOR_GREEN;

		}
	}

	/**
	 * This function will be called when soft-drop is used
	 */
	@Override
	public void afterSoftDropFall(GameEngine engine, int playerID, int fall) {
		softdropscore += fall;
	}

	/**
	 * This function will be called when hard-drop is used
	 */
	@Override
	public void afterHardDropFall(GameEngine engine, int playerID, int fall) {
		harddropscore += fall;
	}

	/**
	 * Renders game result screen
	 */
	@Override
	public void renderResult(GameEngine engine, int playerID) {
		receiver.drawMenuFont(engine, playerID,  0, 1, "PLAY DATA", EventReceiver.COLOR_ORANGE);

		drawResultStats(engine, playerID, receiver, 3, EventReceiver.COLOR_BLUE, STAT_SCORE);

		receiver.drawMenuFont(engine, playerID,  0, 5, "LINES", EventReceiver.COLOR_BLUE);
		String strLines = String.format("%10d", loons);
		receiver.drawMenuFont(engine, playerID,  0, 6, strLines);
		String strFour = String.format("%10s", String.format("+%d", engine.statistics.totalFour));
		receiver.drawMenuFont(engine, playerID,  0, 7, strFour);

		drawResultStats(engine, playerID, receiver, 8, EventReceiver.COLOR_BLUE,
				STAT_LEVEL, STAT_TIME);
		drawResult(engine, playerID, receiver, 12, EventReceiver.COLOR_BLUE,
				"EFFICIENCY", String.format("%10.3f", efficiency));
		drawResultRank(engine, playerID, receiver, 14, EventReceiver.COLOR_BLUE, rankingRank);
	}

	/**
	 * This function will be called when the replay data is going to be saved
	 */
	@Override
	public void saveReplay(GameEngine engine, int playerID, CustomProperties prop) {
		saveSetting(prop);

		// Checks/Updates the ranking
		if((owner.replayMode == false) && (big == false) && (engine.ai == null)) {
			updateRanking(engine.statistics.score, loons, engine.statistics.level, gametype);

			if(rankingRank != -1) {
				saveRanking(owner.modeConfig, engine.ruleopt.strRuleName);
				receiver.saveModeConfig(owner.modeConfig);
			}
		}
	}

	/**
	 * Load the settings
	 * @param prop CustomProperties
	 */
	private void loadSetting(CustomProperties prop) {
		gametype = prop.getProperty("retromastery.gametype", 0);
		startlevel = prop.getProperty("retromastery.startlevel", 0);
		big = prop.getProperty("retromastery.big", false);
		version = prop.getProperty("retromastery.version", 0);
	}

	/**
	 * Save the settings
	 * @param prop CustomProperties
	 */
	private void saveSetting(CustomProperties prop) {
		prop.setProperty("retromastery.gametype", gametype);
		prop.setProperty("retromastery.startlevel", startlevel);
		prop.setProperty("retromastery.big", big);
		prop.setProperty("retromastery.version", version);
	}

	/**
	 * Load the ranking
	 * @param prop CustomProperties
	 * @param ruleName Rule name
	 */
	private void loadRanking(CustomProperties prop, String ruleName) {
		for(int i = 0; i < RANKING_MAX; i++) {
			for(int gametypeIndex = 0; gametypeIndex < RANKING_TYPE; gametypeIndex++) {
				rankingScore[gametypeIndex][i] = prop.getProperty("retromastery.ranking." + ruleName + "." + gametypeIndex + ".score." + i, 0);
				rankingLines[gametypeIndex][i] = prop.getProperty("retromastery.ranking." + ruleName + "." + gametypeIndex + ".lines." + i, 0);
				rankingLevel[gametypeIndex][i] = prop.getProperty("retromastery.ranking." + ruleName + "." + gametypeIndex + ".level." + i, 0);
			}
		}
	}

	/**
	 * Save the ranking
	 * @param prop CustomProperties
	 * @param ruleName Rule name
	 */
	private void saveRanking(CustomProperties prop, String ruleName) {
		for(int i = 0; i < RANKING_MAX; i++) {
			for(int gametypeIndex = 0; gametypeIndex < RANKING_TYPE; gametypeIndex++) {
				prop.setProperty("retromastery.ranking." + ruleName + "." + gametypeIndex + ".score." + i, rankingScore[gametypeIndex][i]);
				prop.setProperty("retromastery.ranking." + ruleName + "." + gametypeIndex + ".lines." + i, rankingLines[gametypeIndex][i]);
				prop.setProperty("retromastery.ranking." + ruleName + "." + gametypeIndex + ".level." + i, rankingLevel[gametypeIndex][i]);
			}
		}
	}

	/**
	 * Update the ranking
	 * @param sc Score
	 * @param li Lines
	 * @param lv Level
	 * @param type Game type
	 */
	private void updateRanking(int sc, int li, int lv, int type) {
		rankingRank = checkRanking(sc, li, lv, type);

		if(rankingRank != -1) {
			// Shift the ranking data
			for(int i = RANKING_MAX - 1; i > rankingRank; i--) {
				rankingScore[type][i] = rankingScore[type][i - 1];
				rankingLines[type][i] = rankingLines[type][i - 1];
				rankingLevel[type][i] = rankingLevel[type][i - 1];
			}

			// Insert a new data
			rankingScore[type][rankingRank] = sc;
			rankingLines[type][rankingRank] = li;
			rankingLevel[type][rankingRank] = lv;
		}
	}

	/**
	 * This function will check the ranking and returns which place you are. (-1: Out of rank)
	 * @param sc Score
	 * @param li Lines
	 * @param lv Level
	 * @return Place (First place is 0. -1 is Out of Rank)
	 */
	private int checkRanking(int sc, int li, int lv, int type) {
		for(int i = 0; i < RANKING_MAX; i++) {
			if(sc > rankingScore[type][i]) {
				return i;
			} else if((sc == rankingScore[type][i]) && (li > rankingLines[type][i])) {
				return i;
			} else if((sc == rankingScore[type][i]) && (li == rankingLines[type][i]) && (lv < rankingLevel[type][i])) {
				return i;
			}
		}

		return -1;
	}
}
