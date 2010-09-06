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

import mu.nu.nullpo.game.component.Block;
import mu.nu.nullpo.game.component.Controller;
import mu.nu.nullpo.game.event.EventReceiver;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.util.CustomProperties;
import mu.nu.nullpo.util.GeneralUtil;

/**
 * CLASSIC MARATHON mode (Original from NullpoUE build 010210 by Zircean)
 */
public class RetroMarathonMode extends DummyMode {
	/** Current version of this mode */
	private static final int CURRENT_VERSION = 2;

	/** Denominator table (Normal) */
	private static final int tableDenominator[] =
	{
		//	0  1  2  3  4  5  6  7  8  9    +xx
		   48,43,38,33,28,23,18,13, 8, 6, // 00
		    5, 5, 5, 4, 4, 4, 3, 3, 3, 2, // 10
		    2, 2, 2, 2, 2, 2, 2, 2, 2, 1  // 20
	};

	/** Gravity table (Arrange) */
	private static final int tableGravityArrange[] =
	{
		//	0  1  2  3  4  5  6  7  8  9    +xx
		    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, // 00
		    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, // 10
		    1, 1, 1, 1, 1, 1, 5, 5, 3, 3, // 20
		    7, 7, 2, 2, 2, 3, 3, 5, 5,-1  // 30
	};

	/** Denominator table (Arrange) */
	private static final int tableDenominatorArrange[] =
	{
		//	0  1  2  3  4  5  6  7  8  9    +xx
		   48,43,38,33,28,23,18,13, 8, 6, // 00
		    5, 5, 4, 4, 4, 3, 3, 3, 2, 2, // 10
		    2, 2, 2, 1, 1, 1, 4, 4, 2, 2, // 20
		    4, 4, 1, 1, 1, 1, 1, 1, 1, 1  // 30
	};

	/** Garbage height table */
	private static final int tableGarbageHeight[] = {0,3,5,8,10,12};

	/** Game types */
	private static final int GAMETYPE_TYPE_A = 0,
							 GAMETYPE_TYPE_B = 1,
							 GAMETYPE_ARRANGE = 2;

	/** Number of game types */
	private static final int GAMETYPE_MAX = 3;

	/** Game type name */
	private static final String[] GAMETYPE_NAME = {"TYPE A","TYPE B","ARRANGE"};

	/** Number of ranking records */
	private static final int RANKING_MAX = 10;

	/** Number of ranking types */
	private static final int RANKING_TYPE = 3;

	/** Level name table */
	private static final String[] LEVEL_NAME =
	{
		//    0    1    2    3    4    5    6    7    8    9      +xx
			"00","01","02","03","04","05","06","07","08","09", // 000
			"10","11","12","13","14","15","16","17","18","19", // 010
			"20","21","22","23","24","25","26","27","28","29", // 020
			"00","0A","14","1E","28","32","3C","46","50","5A", // 030
			"64","6E","78","82","8C","96","A0","AA","B4","BE", // 040
			"C6","20","E6","20","06","21","26","21","46","21", // 050
			"66","21","86","21","A6","21","C6","21","E6","21", // 060
			"06","22","26","22","46","22","66","22","86","22", // 070
			"A6","22","C6","22","E6","22","06","23","26","23", // 080
			"85","A8","29","F0","4A","4A","4A","4A","8D","07", // 090
			"20","A5","A8","29","0F","8D","07","20","60","A6", // 100
			"49","E0","15","10","53","BD","D6","96","A8","8A", // 110
			"0A","AA","E8","BD","EA","96","8D","06","20","CA", // 120
			"A5","BE","C9","01","F0","1E","A5","B9","C9","05", // 130
			"F0","0C","BD","EA","96","38","E9","02","8D","06", // 140
			"20","4C","67","97","BD","EA","96","18","69","0C", // 150
			"8D","06","20","4C","67","97","BD","EA","96","18", // 160
			"69","06","8D","06","20","A2","0A","B1","B8","8D", // 170
			"07","20","C8","CA","D0","F7","E6","49","A5","49", // 180
			"C9","14","30","04","A9","20","85","49","60","A5", // 190
			"B1","29","03","D0","78","A9","00","85","AA","A6", // 200
			"AA","B5","4A","F0","5C","0A","A8","B9","EA","96", // 210
			"85","A8","A5","BE","C9","01","D0","0A","A5","A8", // 220
			"18","69","06","85","A8","4C","BD","97","A5","B9", // 230
			"C9","04","D0","0A","A5","A8","38","E9","02","85", // 240
			"A8","4C","BD","97","A5","A8"                      // 250
	};

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

	/** Selected garbage height */
	private int startheight;

	/** Used for soft drop scoring */
	private int softdropscore;

	/** Used for hard drop scoring */
	private int harddropscore;

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
		return "RETRO MARATHON";
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

		rankingRank = -1;
		rankingScore = new int[RANKING_TYPE][RANKING_MAX];
		rankingLines = new int[RANKING_TYPE][RANKING_MAX];
		rankingLevel = new int[RANKING_TYPE][RANKING_MAX];

		engine.tspinEnable = false;
		engine.b2bEnable = false;
		engine.comboType = GameEngine.COMBO_TYPE_DISABLE;
		engine.bighalf = false;
		engine.bigmove = false;

		engine.speed.are = 10;
		engine.speed.areLine = 20;
		engine.speed.lineDelay = 20;
		engine.speed.lockDelay = 0;
		engine.speed.das = (gametype == GAMETYPE_ARRANGE ? 12 : 16);

		if(owner.replayMode == false) {
			loadSetting(owner.modeConfig);
			loadRanking(owner.modeConfig, engine.ruleopt.strRuleName);
			version = CURRENT_VERSION;
		} else {
			loadSetting(owner.replayProp);
		}

		engine.owner.backgroundStatus.bg = startlevel;
		if(engine.owner.backgroundStatus.bg > 19) engine.owner.backgroundStatus.bg = 19;
		levellines = Math.min((startlevel+1)*10,Math.max(100,(startlevel-5)*10));
		engine.framecolor = GameEngine.FRAME_COLOR_GRAY;
	}

	/**
	 * Set the gravity speed
	 * @param engine GameEngine object
	 */
	private void setSpeed(GameEngine engine) {
		int lv = engine.statistics.level;

		if (gametype == GAMETYPE_ARRANGE) {
			if(lv < 0) lv = 0;
			if(lv >= tableDenominatorArrange.length) lv = tableDenominatorArrange.length - 1;

			engine.speed.gravity = tableGravityArrange[lv];
			engine.speed.denominator = tableDenominatorArrange[lv];
		} else {
			if(lv < 0) lv = 0;
			if(lv >= tableDenominator.length) lv = tableDenominator.length - 1;

			engine.speed.gravity = 1;
			engine.speed.denominator = tableDenominator[lv];
		}
	}

	/**
	 * Main routine for game setup screen
	 */
	@Override
	public boolean onSetting(GameEngine engine, int playerID) {
		// Menu
		if(engine.owner.replayMode == false) {
			// Configuration changes
			int change = updateCursor(engine, 3);

			if(change != 0) {
				receiver.playSE("change");

				switch(engine.statc[2]) {
				case 0:
					gametype += change;
					if(gametype < 0) gametype = GAMETYPE_MAX - 1;
					if(gametype > GAMETYPE_MAX - 1) gametype = 0;
					break;
				case 1:
					startlevel += change;
					if(startlevel < 0) startlevel = 19;
					if(startlevel > 19) startlevel = 0;
					engine.owner.backgroundStatus.bg = startlevel;
					levellines = Math.min((startlevel+1)*10,Math.max(100,(startlevel-5)*10));
					break;
				case 2:
					startheight += change;
					if(startheight < 0) startheight = 5;
					if(startheight > 5) startheight = 0;
					break;
				case 3:
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
		drawMenu(engine, playerID, receiver, 0, EventReceiver.COLOR_BLUE, 0,
				"GAME TYPE", GAMETYPE_NAME[gametype],
				"LEVEL", LEVEL_NAME[startlevel],
				"HEIGHT", String.valueOf(startheight),
				"BIG", GeneralUtil.getONorOFF(big));
	}

	public boolean onReady(GameEngine engine, int playerID) {
		if (engine.statc[0] == 0) {
			engine.createFieldIfNeeded();
			fillGarbage(engine,startheight);
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

		setSpeed(engine);
	}

	/**
	 * Renders HUD (leaderboard or game statistics)
	 */
	@Override
	public void renderLast(GameEngine engine, int playerID) {
		receiver.drawScoreFont(engine, playerID, 0, 0, "RETRO MARATHON", EventReceiver.COLOR_GREEN);
		receiver.drawScoreFont(engine, playerID, 0, 1, "("+GAMETYPE_NAME[gametype]+")", EventReceiver.COLOR_GREEN);

		if( (engine.stat == GameEngine.STAT_SETTING) || ((engine.stat == GameEngine.STAT_RESULT) && (owner.replayMode == false)) ) {
			if((owner.replayMode == false) && (big == false) && (engine.ai == null)) {
				receiver.drawScoreFont(engine, playerID, 3, 3, "SCORE    LINE LEVEL", EventReceiver.COLOR_BLUE);

				for(int i = 0; i < RANKING_MAX; i++) {
					receiver.drawScoreFont(engine, playerID, 0, 4 + i, String.format("%2d", i + 1), EventReceiver.COLOR_YELLOW);
					receiver.drawScoreFont(engine, playerID, 3, 4 + i, String.valueOf(rankingScore[gametype][i]), (i == rankingRank));
					receiver.drawScoreFont(engine, playerID, 12, 4 + i, String.valueOf(rankingLines[gametype][i]), (i == rankingRank));
					receiver.drawScoreFont(engine, playerID, 17, 4 + i, LEVEL_NAME[rankingLevel[gametype][i]], (i == rankingRank));
				}
			}
		} else {
			receiver.drawScoreFont(engine, playerID, 0, 3, "SCORE", EventReceiver.COLOR_BLUE);
			String strScore;
			if((lastscore == 0) || (scgettime >= 120)) {
				strScore = String.valueOf(engine.statistics.score);
			} else {
				strScore = String.valueOf(engine.statistics.score) + "(+" + String.valueOf(lastscore) + ")";
			}
			receiver.drawScoreFont(engine, playerID, 0, 4, strScore);

			String strLine;
			switch (gametype) {
				case GAMETYPE_TYPE_A: strLine = String.valueOf(engine.statistics.lines); break;
				case GAMETYPE_TYPE_B: strLine = String.valueOf(Math.max(25-engine.statistics.lines,0)); break;
				case GAMETYPE_ARRANGE: strLine = engine.statistics.lines + "/" + levellines; break;
				default: strLine = String.valueOf(engine.statistics.lines); break;
			}

			receiver.drawScoreFont(engine, playerID, 0, 6, "LINE", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 0, 7, strLine);

			receiver.drawScoreFont(engine, playerID, 0, 9, "LEVEL", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 0, 10, LEVEL_NAME[engine.statistics.level]);

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

		engine.statistics.score += harddropscore;
		engine.statistics.scoreFromHardDrop += harddropscore;
		harddropscore = 0;

		// Line clear score
		int pts = 0;
		if(lines == 1) {
			pts += 40 * (engine.statistics.level + 1); // Single
		} else if(lines == 2) {
			pts += 100 * (engine.statistics.level + 1); // Double
		} else if(lines == 3) {
			pts += 300 * (engine.statistics.level + 1); // Triple
		} else if(lines >= 4) {
			pts += 1200 * (engine.statistics.level + 1); // Four
		}

		// B-TYPE game completed
		if(gametype == GAMETYPE_TYPE_B && engine.statistics.lines >= 25) {
			pts += (engine.statistics.level + startheight) * 1000;
			engine.ending = 1;
			engine.gameActive = false;
		}

		// Add score to total
		if(pts > 0) {
			lastscore = pts;
			scgettime = 0;
			engine.statistics.scoreFromLineClear += pts;
			engine.statistics.score += pts;
		}

		if (!(gametype == GAMETYPE_ARRANGE) && engine.statistics.score > 999999) {
			engine.statistics.score = 999999;
		}

		// Update meter
		if (gametype == GAMETYPE_TYPE_B)
		{
			engine.meterValue = (Math.min(engine.statistics.lines,25) * receiver.getMeterMax(engine)) / 25;
			engine.meterColor = GameEngine.METER_COLOR_GREEN;
			if(engine.statistics.lines >= 10) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
			if(engine.statistics.lines >= 15) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
			if(engine.statistics.lines >= 20) engine.meterColor = GameEngine.METER_COLOR_RED;
		}
		else
		{
			engine.meterValue = ((engine.statistics.lines % 10) * receiver.getMeterMax(engine)) / 9;
			engine.meterColor = GameEngine.METER_COLOR_GREEN;
			if(engine.statistics.lines % 10 >= 2) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
			if(engine.statistics.lines % 10 >= 5) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
			if(engine.statistics.lines % 10 >= 8) engine.meterColor = GameEngine.METER_COLOR_RED;
		}

		if(!(gametype == GAMETYPE_TYPE_B) && engine.statistics.lines >= levellines ) {
			// Level up
			engine.statistics.level++;

			levellines += 10;

			if (engine.statistics.level > 255) engine.statistics.level = 0;

			owner.backgroundStatus.fadesw = true;
			owner.backgroundStatus.fadecount = 0;

			int lv = engine.statistics.level;

			if(lv < 0) lv = 0;
			if(lv >= 19) lv = 19;

			owner.backgroundStatus.fadebg = lv;

			setSpeed(engine);
			receiver.playSE("levelup");
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

		drawResultStats(engine, playerID, receiver, 3, EventReceiver.COLOR_BLUE,
				STAT_SCORE, STAT_LINES);
		receiver.drawMenuFont(engine, playerID,  0, 7, "LEVEL", EventReceiver.COLOR_BLUE);
		String strLevel = String.format("%10s", LEVEL_NAME[engine.statistics.level]);
		receiver.drawMenuFont(engine, playerID,  0, 8, strLevel);
		drawResultStats(engine, playerID, receiver, 9, EventReceiver.COLOR_BLUE,
				STAT_TIME, STAT_SPL, STAT_LPM);
		drawResultRank(engine, playerID, receiver, 15, EventReceiver.COLOR_BLUE, rankingRank);

	}

	/**
	 * This function will be called when the replay data is going to be saved
	 */
	@Override
	public void saveReplay(GameEngine engine, int playerID, CustomProperties prop) {
		saveSetting(prop);

		// Checks/Updates the ranking
		if((owner.replayMode == false) && (big == false) && (engine.ai == null)) {
			updateRanking(engine.statistics.score, engine.statistics.lines, engine.statistics.level, gametype);

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
		gametype = prop.getProperty("retromarathon.gametype", 0);
		startlevel = prop.getProperty("retromarathon.startlevel", 0);
		startheight = prop.getProperty("retromarathon.startheight", 0);
		big = prop.getProperty("retromarathon.big", false);
		version = prop.getProperty("retromarathon.version", 0);
	}

	/**
	 * Save the settings
	 * @param prop CustomProperties
	 */
	private void saveSetting(CustomProperties prop) {
		prop.setProperty("retromarathon.gametype", gametype);
		prop.setProperty("retromarathon.startlevel", startlevel);
		prop.setProperty("retromarathon.startheight", startheight);
		prop.setProperty("retromarathon.big", big);
		prop.setProperty("retromarathon.version", version);
	}

	/**
	 * Load the ranking
	 * @param prop CustomProperties
	 * @param ruleName Rule name
	 */
	private void loadRanking(CustomProperties prop, String ruleName) {
		for(int i = 0; i < RANKING_MAX; i++) {
			for(int gametypeIndex = 0; gametypeIndex < RANKING_TYPE; gametypeIndex++) {
				rankingScore[gametypeIndex][i] = prop.getProperty("retromarathon.ranking." + ruleName + "." + gametypeIndex + ".score." + i, 0);
				rankingLines[gametypeIndex][i] = prop.getProperty("retromarathon.ranking." + ruleName + "." + gametypeIndex + ".lines." + i, 0);
				rankingLevel[gametypeIndex][i] = prop.getProperty("retromarathon.ranking." + ruleName + "." + gametypeIndex + ".level." + i, 0);
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
				prop.setProperty("retromarathon.ranking." + ruleName + "." + gametypeIndex + ".score." + i, rankingScore[gametypeIndex][i]);
				prop.setProperty("retromarathon.ranking." + ruleName + "." + gametypeIndex + ".lines." + i, rankingLines[gametypeIndex][i]);
				prop.setProperty("retromarathon.ranking." + ruleName + "." + gametypeIndex + ".level." + i, rankingLevel[gametypeIndex][i]);
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

	/**
	 * Fill the playfield with garbage
	 * @param engine GameEngine
	 * @param height Garbage height level number
	 */
	private void fillGarbage(GameEngine engine, int height) {
		int h = engine.field.getHeight();
		int startHeight = (version >= 2) ? (h - 1) : h;
		float f;
		for (int y = startHeight; y >= h - tableGarbageHeight[height]; y--) {
			for (int x = 0; x < engine.field.getWidth(); x++) {
				f = engine.random.nextFloat();
				if (f < 0.5) {
					engine.field.setBlock(x,y,new Block((int)(f*14)+2,engine.getSkin(),Block.BLOCK_ATTRIBUTE_VISIBLE | Block.BLOCK_ATTRIBUTE_GARBAGE));
				}
			}
		}
	}
}
