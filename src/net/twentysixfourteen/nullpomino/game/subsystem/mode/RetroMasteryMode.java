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
package net.twentysixfourteen.nullpomino.game.subsystem.mode;

import org.game_host.hebo.nullpomino.game.subsystem.mode.DummyMode;
import org.game_host.hebo.nullpomino.game.component.Controller;
import org.game_host.hebo.nullpomino.game.event.EventReceiver;
import org.game_host.hebo.nullpomino.game.play.GameEngine;
import org.game_host.hebo.nullpomino.game.play.GameManager;
import org.game_host.hebo.nullpomino.util.CustomProperties;
import org.game_host.hebo.nullpomino.util.GeneralUtil;

/**
 * RETRO MASTERY mode by Pineapple 20100724ish
 */
public class RetroMasteryMode extends DummyMode {
	/** Current version of this mode */
	private static final int CURRENT_VERSION = 1;

	/** Denominator table */
	private static final int tableDenominator[] =
	{
		//	0  1  2  3  4  5  6  7  8  9    +xx
		   48,40,32,27,22,18,15,12,10, 8, // 00
		    7, 6,11, 5, 9, 4, 7, 3, 5, 2, // 10
	};

	/** Gravity table */
	private static final int tableGravity[] =
	{
		//	0  1  2  3  4  5  6  7  8  9    +xx
		    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, // 00
		    1, 1, 2, 1, 2, 1, 2, 1, 2, 1, // 10
	};

	/** Lock delay table */
	private static final int tableLockDelay[] =
	{
		//	0  1  2  3  4  5  6  7  8  9    +xx
		   12,12,12,12,12,11,11,11,11,11, // 00
		   10,10, 9, 9, 8, 8, 7, 7, 6, 6, // 10
	};

	/** Number of ranking records */
	private static final int RANKING_MAX = 10;

	/** GameManager object (Manages entire game status) */
	private GameManager owner;

	/** EventReceiver object (This receives many game events, can also be used for drawing the fonts.) */
	private EventReceiver receiver;

	/** Amount of points you just get from line clears */
	private int lastscore;

	/** Elapsed time from last line clear (lastscore is displayed to screen until this reaches to 120) */
	private int scgettime;

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
	private int[] rankingScore;

	/** Line records */
	private int[] rankingLines;

	/** Level records */
	private int[] rankingLevel;

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
		rankingScore = new int[RANKING_MAX];
		rankingLines = new int[RANKING_MAX];
		rankingLevel = new int[RANKING_MAX];

		engine.tspinEnable = false;
		engine.b2bEnable = false;
		engine.comboType = GameEngine.COMBO_TYPE_DISABLE;
		engine.bighalf = false;
		engine.bigmove = false;

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

		engine.owner.backgroundStatus.bg = startlevel;
		if(engine.owner.backgroundStatus.bg > 19) engine.owner.backgroundStatus.bg = 19;
		levellines = (startlevel+1)*10;
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
				if(engine.statc[2] < 0) engine.statc[2] = 1;
				receiver.playSE("cursor");
			}
			// Check for DOWN button, when pressed it will move cursor down.
			if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_DOWN)) {
				engine.statc[2]++;
				if(engine.statc[2] > 1) engine.statc[2] = 0;
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
					startlevel += change;
					if(startlevel < 0) startlevel = 19;
					if(startlevel > 19) startlevel = 0;
					engine.owner.backgroundStatus.bg = startlevel;
					levellines = (startlevel+1)*10;
					break;
				case 1:
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

		receiver.drawMenuFont(engine, playerID, 0, 0, "LEVEL", EventReceiver.COLOR_BLUE);
		receiver.drawMenuFont(engine, playerID, 1, 1, String.format("%02d", startlevel), (engine.statc[2] == 0));
		receiver.drawMenuFont(engine, playerID, 0, 2, "BIG", EventReceiver.COLOR_BLUE);
		receiver.drawMenuFont(engine, playerID, 1, 3, GeneralUtil.getONorOFF(big), (engine.statc[2] == 1));
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
		receiver.drawScoreFont(engine, playerID, 0, 0, "RETRO MASTERY", EventReceiver.COLOR_GREEN);

		if( (engine.stat == GameEngine.STAT_SETTING) || ((engine.stat == GameEngine.STAT_RESULT) && (owner.replayMode == false)) ) {
			if((owner.replayMode == false) && (big == false) && (engine.ai == null)) {
				receiver.drawScoreFont(engine, playerID, 3, 3, "SCORE   LINES LEVEL", EventReceiver.COLOR_BLUE);

				for(int i = 0; i < RANKING_MAX; i++) {
					receiver.drawScoreFont(engine, playerID, 0, 4 + i, String.format("%2d", i + 1), EventReceiver.COLOR_YELLOW);
					receiver.drawScoreFont(engine, playerID, 3, 4 + i, String.valueOf(rankingScore[i]), (i == rankingRank));
					receiver.drawScoreFont(engine, playerID, 12, 4 + i, String.valueOf(rankingLines[i]), (i == rankingRank));
					receiver.drawScoreFont(engine, playerID, 17, 4 + i, String.valueOf(rankingLevel[i]), (i == rankingRank));
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
		if(loons >= 200) {
			engine.ending = 1;
			engine.gameActive = false;
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

		// Update meter
		engine.meterValue = ((loons % 10) * receiver.getMeterMax(engine)) / 9;
		engine.meterColor = GameEngine.METER_COLOR_GREEN;
		if(loons % 10 >= 2) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
		if(loons % 10 >= 5) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
		if(loons % 10 >= 8) engine.meterColor = GameEngine.METER_COLOR_RED;

		if(loons >= levellines ) {
			// Level up
			engine.statistics.level++;

			levellines += 10;

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

		receiver.drawMenuFont(engine, playerID,  0, 3, "SCORE", EventReceiver.COLOR_BLUE);
		String strScore = String.format("%10d", engine.statistics.score);
		receiver.drawMenuFont(engine, playerID,  0, 4, strScore);

		receiver.drawMenuFont(engine, playerID,  0, 5, "LINES", EventReceiver.COLOR_BLUE);
		String strLines = String.format("%10d", loons);
		receiver.drawMenuFont(engine, playerID,  0, 6, strLines);
		String strFour = String.format("%10s", String.format("+%d", engine.statistics.totalFour));
		receiver.drawMenuFont(engine, playerID,  0, 7, strFour);

		receiver.drawMenuFont(engine, playerID,  0, 8, "LEVEL", EventReceiver.COLOR_BLUE);
		String strLevel = String.format("%10s", String.format("%02d", engine.statistics.level));
		receiver.drawMenuFont(engine, playerID,  0, 9, strLevel);

		receiver.drawMenuFont(engine, playerID,  0, 10, "TIME", EventReceiver.COLOR_BLUE);
		String strTime = String.format("%10s", GeneralUtil.getTime(engine.statistics.time));
		receiver.drawMenuFont(engine, playerID,  0, 11, strTime);

		receiver.drawMenuFont(engine, playerID,  0, 12, "EFFICIENCY", EventReceiver.COLOR_BLUE);
		String strEff = String.format("%5.3f", efficiency);
		receiver.drawMenuFont(engine, playerID,  5, 13, strEff);

		if(rankingRank != -1) {
			receiver.drawMenuFont(engine, playerID,  0, 14, "RANK", EventReceiver.COLOR_BLUE);
			String strRank = String.format("%10d", rankingRank + 1);
			receiver.drawMenuFont(engine, playerID,  0, 15, strRank);
		}
	}

	/**
	 * This function will be called when the replay data is going to be saved
	 */
	@Override
	public void saveReplay(GameEngine engine, int playerID, CustomProperties prop) {
		saveSetting(prop);

		// Checks/Updates the ranking
		if((owner.replayMode == false) && (big == false) && (engine.ai == null)) {
			updateRanking(engine.statistics.score, loons, engine.statistics.level);

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
		startlevel = prop.getProperty("classicmarathon.startlevel", 0);
		big = prop.getProperty("classicmarathon.big", false);
		version = prop.getProperty("classicmarathon.version", 0);
	}

	/**
	 * Save the settings
	 * @param prop CustomProperties
	 */
	private void saveSetting(CustomProperties prop) {
		prop.setProperty("classicmarathon.startlevel", startlevel);
		prop.setProperty("classicmarathon.big", big);
		prop.setProperty("classicmarathon.version", version);
	}

	/**
	 * Load the ranking
	 * @param prop CustomProperties
	 * @param ruleName Rule name
	 */
	private void loadRanking(CustomProperties prop, String ruleName) {
		for(int i = 0; i < RANKING_MAX; i++) {
			rankingScore[i] = prop.getProperty("classicmarathon.ranking." + ruleName + "." + ".score." + i, 0);
			rankingLines[i] = prop.getProperty("classicmarathon.ranking." + ruleName + "." + ".lines." + i, 0);
			rankingLevel[i] = prop.getProperty("classicmarathon.ranking." + ruleName + "." + ".level." + i, 0);
		}
	}

	/**
	 * Save the ranking
	 * @param prop CustomProperties
	 * @param ruleName Rule name
	 */
	private void saveRanking(CustomProperties prop, String ruleName) {
		for(int i = 0; i < RANKING_MAX; i++) {
			prop.setProperty("classicmarathon.ranking." + ruleName + "." + ".score." + i, rankingScore[i]);
			prop.setProperty("classicmarathon.ranking." + ruleName + "." + ".lines." + i, rankingLines[i]);
			prop.setProperty("classicmarathon.ranking." + ruleName + "." + ".level." + i, rankingLevel[i]);
		}
	}

	/**
	 * Update the ranking
	 * @param sc Score
	 * @param li Lines
	 * @param lv Level
	 */
	private void updateRanking(int sc, int li, int lv) {
		rankingRank = checkRanking(sc, li, lv);

		if(rankingRank != -1) {
			// Shift the ranking data
			for(int i = RANKING_MAX - 1; i > rankingRank; i--) {
				rankingScore[i] = rankingScore[i - 1];
				rankingLines[i] = rankingLines[i - 1];
				rankingLevel[i] = rankingLevel[i - 1];
			}

			// Insert a new data
			rankingScore[rankingRank] = sc;
			rankingLines[rankingRank] = li;
			rankingLevel[rankingRank] = lv;
		}
	}

	/**
	 * This function will check the ranking and returns which place you are. (-1: Out of rank)
	 * @param sc Score
	 * @param li Lines
	 * @param lv Level
	 * @return Place (First place is 0. -1 is Out of Rank)
	 */
	private int checkRanking(int sc, int li, int lv) {
		for(int i = 0; i < RANKING_MAX; i++) {
			if(sc > rankingScore[i]) {
				return i;
			} else if((sc == rankingScore[i]) && (li > rankingLines[i])) {
				return i;
			} else if((sc == rankingScore[i]) && (li == rankingLines[i]) && (lv < rankingLevel[i])) {
				return i;
			}
		}

		return -1;
	}
}
