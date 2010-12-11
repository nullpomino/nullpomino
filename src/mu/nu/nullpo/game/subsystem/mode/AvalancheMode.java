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
import mu.nu.nullpo.util.CustomProperties;
import mu.nu.nullpo.util.GeneralUtil;

/**
 * AVALANCHE mode (Release Candidate 2)
 */
public class AvalancheMode extends Avalanche1PDummyMode {
	/** Current version */
	private static final int CURRENT_VERSION = 0;

	/** Enabled piece types */
	private static final int[] CHAIN_POWERS_FEVERTYPE =
	{
		4, 12, 24, 32, 48, 96, 160, 240, 320, 400, 500, 600, 700, 800, 900, 999
	};

	/** Number of ranking records */
	private static final int RANKING_MAX = 10;

	/** Number of ranking types */
	private static final int RANKING_TYPE = 7;

	/** Name of game types */
	private static final String[] GAMETYPE_NAME = {"MARATHON","ULTRA","SPRINT"};

	/** Number of game types */
	private static final int GAMETYPE_MAX = 3;

	/** Name of score types */
	private static final String[] SCORETYPE_NAME = {"CLASSIC", "FEVER"};

	/** Number of score types */
	private static final int SCORETYPE_MAX = 2;

	/** Max time in Ultra */
	private static final int ULTRA_MAX_TIME = 10800;

	/** Max score in Sprint */
	private static final int[] SPRINT_MAX_SCORE = {15000, 20000, 100000, 175000, 350000};

	/** Selected game type */
	private int gametype;

	/** Version number */
	private int version;

	/** Current round's ranking rank */
	private int rankingRank;

	/** Rankings' line counts */
	private int[][][][] rankingScore;

	/** Rankings' times */
	private int[][][][] rankingTime;

	/** Chain display enable/disable */
	private boolean showChains;

	/** If true, both columns 3 and 4 are danger columns */
	protected boolean dangerColumnDouble;

	/** If true, red X's appear at tops of danger columns */
	protected boolean dangerColumnShowX;

	/** True for classic scoring, false for 15th scoring algorithm */
	private int scoreType;

	/** Sprint target score */
	private int sprintTarget;

	/*
	 * Mode name
	 */
	@Override
	public String getName() {
		return "AVALANCHE 1P (RC2)";
	}

	/*
	 * Initialization
	 */
	@Override
	public void playerInit(GameEngine engine, int playerID) {
		super.playerInit(engine, playerID);

		showChains = true;

		scoreType = 0;
		sprintTarget = 0;

		rankingRank = -1;
		rankingScore = new int[SCORETYPE_MAX][3][RANKING_TYPE][RANKING_MAX];
		rankingTime = new int[SCORETYPE_MAX][3][RANKING_TYPE][RANKING_MAX];

		if(owner.replayMode == false) {
			loadSetting(owner.modeConfig);
			loadRanking(owner.modeConfig, engine.ruleopt.strRuleName);
			version = CURRENT_VERSION;
		} else {
			loadSetting(owner.replayProp);
		}
	}

	/**
	 * Set the gravity rate
	 * @param engine GameEngine
	 */
	public void setSpeed(GameEngine engine) {
		engine.speed.gravity = 1;
		if (gametype == 0) {
			engine.speed.denominator = Math.max(41-level, 2);
		} else {
			engine.speed.denominator = 40;
		}
	}

	/*
	 * Called at settings screen
	 */
	@Override
	public boolean onSetting(GameEngine engine, int playerID) {
		// Menu
		if(engine.owner.replayMode == false) {
			// Up
			if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_UP)) {
				engine.statc[2]--;
				if(engine.statc[2] < 0) engine.statc[2] = 10;
				else if(engine.statc[2] == 1 && gametype != 2) engine.statc[2]--;
				engine.playSE("cursor");
			}
			// Down
			if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_DOWN)) {
				engine.statc[2]++;
				if(engine.statc[2] > 10) engine.statc[2] = 0;
				else if(engine.statc[2] == 1 && gametype != 2) engine.statc[2]++;
				engine.playSE("cursor");
			}

			// Configuration changes
			int change = 0;
			if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_LEFT)) change = -1;
			if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_RIGHT)) change = 1;

			if(change != 0) {
				engine.playSE("change");

				switch(engine.statc[2]) {

				case 0:
					gametype += change;
					if(gametype < 0) gametype = GAMETYPE_MAX - 1;
					if(gametype > GAMETYPE_MAX - 1) gametype = 0;
					break;
				case 1:
					sprintTarget += change;
					if(sprintTarget < 0) sprintTarget = SPRINT_MAX_SCORE.length-1;
					if(sprintTarget >= SPRINT_MAX_SCORE.length) sprintTarget = 0;
					break;
				case 2:
					scoreType += change;
					if(scoreType < 0) scoreType = SCORETYPE_MAX-1;
					if(scoreType >= SCORETYPE_MAX) scoreType = 0;
					break;
				case 3:
					numColors += change;
					if(numColors < 3) numColors = 5;
					if(numColors > 5) numColors = 3;
					break;
				case 4:
					dangerColumnDouble = !dangerColumnDouble;
					break;
				case 5:
					dangerColumnShowX = !dangerColumnShowX;
					break;
				case 6:
					engine.colorClearSize += change;
					if(engine.colorClearSize < 2) engine.colorClearSize = 36;
					if(engine.colorClearSize > 36) engine.colorClearSize = 2;
					break;
				case 7:
					cascadeSlow = !cascadeSlow;
					break;
				case 8:
					bigDisplay = !bigDisplay;
					break;
				case 9:
					outlinetype += change;
					if(outlinetype < 0) outlinetype = 2;
					if(outlinetype > 2) outlinetype = 0;
					break;
				case 10:
					showChains = !showChains;
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

			if(engine.statc[3] >= 60)
				engine.statc[2] = 9;
			else if(engine.statc[3] >= 120)
				return false;
		}

		return true;
	}

	/*
	 * Render the settings screen
	 */
	@Override
	public void renderSetting(GameEngine engine, int playerID) {
		if (engine.statc[2] <= 8) {
			drawMenu(engine, playerID, receiver, 0, EventReceiver.COLOR_BLUE, 0,
					"GAME TYPE", GAMETYPE_NAME[gametype]);
			if (gametype == 2)
			{
				drawMenu(engine, playerID, receiver, 2, EventReceiver.COLOR_BLUE, 1,
						"TARGET", String.valueOf(SPRINT_MAX_SCORE[sprintTarget]));
			}
			drawMenu(engine, playerID, receiver, 4, EventReceiver.COLOR_BLUE, 2,
					"SCORE TYPE", SCORETYPE_NAME[scoreType],
					"COLORS", String.valueOf(numColors),
					"X COLUMN", dangerColumnDouble ? "3 AND 4" : "3 ONLY",
					"X SHOW", GeneralUtil.getONorOFF(dangerColumnShowX),
					"CLEAR SIZE", String.valueOf(engine.colorClearSize),
					"FALL ANIM", cascadeSlow ?  "FEVER" : "CLASSIC",
					"BIG DISP", GeneralUtil.getONorOFF(bigDisplay));
			
			receiver.drawMenuFont(engine, playerID, 0, 19, "PAGE 1/2", EventReceiver.COLOR_YELLOW);
		} else {
			String strOutline = "";
			if(outlinetype == 0) strOutline = "NORMAL";
			if(outlinetype == 1) strOutline = "COLOR";
			if(outlinetype == 2) strOutline = "NONE";
			drawMenu(engine, playerID, receiver, 0, EventReceiver.COLOR_BLUE, 9,
					"OUTLINE", strOutline,
					"SHOW CHAIN", GeneralUtil.getONorOFF(showChains));
			
			receiver.drawMenuFont(engine, playerID, 0, 19, "PAGE 2/2", EventReceiver.COLOR_YELLOW);
		}
	}

	/*
	 * When the piece is movable
	 */
	@Override
	public void renderMove(GameEngine engine, int playerID) {
		if(dangerColumnShowX && engine.gameStarted) {
			drawXorTimer(engine, playerID);
		}
	}

	/*
	 * Render score
	 */
	@Override
	public void renderLast(GameEngine engine, int playerID) {
		String modeStr = GAMETYPE_NAME[gametype];
		if (gametype == 2)
			modeStr = modeStr + " " + (SPRINT_MAX_SCORE[sprintTarget]/1000) + "K";
		receiver.drawScoreFont(engine, playerID, 0, 0, "AVALANCHE (" + modeStr + ")", EventReceiver.COLOR_DARKBLUE);
		receiver.drawScoreFont(engine, playerID, 0, 1, "("+SCORETYPE_NAME[scoreType] + " " + numColors + " COLORS)", EventReceiver.COLOR_DARKBLUE);

		if( (engine.stat == GameEngine.STAT_SETTING) || ((engine.stat == GameEngine.STAT_RESULT) && (owner.replayMode == false)) ) {
			if((owner.replayMode == false) && (engine.ai == null) && (engine.colorClearSize == 4)) {
				float scale = ((receiver.getNextDisplayType() == 2) && (gametype == 0)) ? 0.5f : 1.0f;
				int topY = ((receiver.getNextDisplayType() == 2) && (gametype == 0)) ? 6 : 4;

				if (gametype == 0) {
					receiver.drawScoreFont(engine, playerID, 3, topY-1, "SCORE      TIME", EventReceiver.COLOR_BLUE, scale);
				} else if (gametype == 1) {
					receiver.drawScoreFont(engine, playerID, 3, 3, "SCORE", EventReceiver.COLOR_BLUE);
				} else if (gametype == 2) {
					receiver.drawScoreFont(engine, playerID, 3, 3, "TIME", EventReceiver.COLOR_BLUE);
				}

				for(int i = 0; i < RANKING_MAX; i++) {
					receiver.drawScoreFont(engine, playerID, 0, topY+i, String.format("%2d", i + 1), EventReceiver.COLOR_YELLOW, scale);
					if (gametype == 0) {
						receiver.drawScoreFont(engine, playerID, 3, topY+i, String.valueOf(rankingScore[scoreType][numColors-3][gametype][i]), (i == rankingRank), scale);
						receiver.drawScoreFont(engine, playerID, 14, topY+i, GeneralUtil.getTime(rankingTime[scoreType][numColors-3][gametype][i]), (i == rankingRank), scale);
					} else if (gametype == 1) {
						receiver.drawScoreFont(engine, playerID, 3, 4 + i, String.valueOf(rankingScore[scoreType][numColors-3][gametype][i]), (i == rankingRank));
					} else if (gametype == 2) {
						receiver.drawScoreFont(engine, playerID, 3, 4 + i, GeneralUtil.getTime(rankingTime[scoreType][numColors-3][gametype][i]), (i == rankingRank));
					}
				}
			}
		} else {
			receiver.drawScoreFont(engine, playerID, 0, 3, "SCORE", EventReceiver.COLOR_BLUE);
			String strScore;
			if((lastscore == 0) || (lastmultiplier == 0) || (scgettime <= 0)) {
				strScore = String.valueOf(engine.statistics.score);
			} else {
				strScore = String.valueOf(engine.statistics.score) + "(+" + String.valueOf(lastscore) + "X" +
					String.valueOf(lastmultiplier) + ")";
			}
			receiver.drawScoreFont(engine, playerID, 0, 4, strScore);

			receiver.drawScoreFont(engine, playerID, 0, 6, "LEVEL", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 0, 7, String.valueOf(level));

			receiver.drawScoreFont(engine, playerID, 0, 9, "OJAMA SENT", EventReceiver.COLOR_BLUE);
			String strSent = String.valueOf(garbageSent);
			if(garbageAdd > 0) {
				strSent = strSent + "(+" + String.valueOf(garbageAdd)+ ")";
			}
			receiver.drawScoreFont(engine, playerID, 0, 10, strSent);

			receiver.drawScoreFont(engine, playerID, 0, 12, "TIME", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 0, 13, GeneralUtil.getTime(engine.statistics.time));

			receiver.drawScoreFont(engine, playerID, 11, 6, "CLEARED", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 11, 7, String.valueOf(blocksCleared));

			receiver.drawScoreFont(engine, playerID, 11, 9, "ZENKESHI", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 11, 10, String.valueOf(zenKeshiCount));

			receiver.drawScoreFont(engine, playerID, 11, 12, "MAX CHAIN", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 11, 13, String.valueOf(engine.statistics.maxChain));

			if(dangerColumnShowX && engine.gameStarted && (engine.stat != GameEngine.STAT_MOVE) && (engine.stat != GameEngine.STAT_RESULT)) {
				drawXorTimer(engine, playerID);
			}

			int textHeight = 13;
			if (engine.field != null)
				textHeight = engine.field.getHeight()+1;
			if(engine.displaysize == 1)
				textHeight = 11;

			int baseX = (engine.displaysize == 1) ? 1 : 0;
			if (chain > 0 && chainDisplay > 0 && showChains)
				receiver.drawMenuFont(engine, playerID, baseX + (chain > 9 ? 0 : 1), textHeight, chain + " CHAIN!", EventReceiver.COLOR_YELLOW);
			if (zenKeshi)
				receiver.drawMenuFont(engine, playerID, baseX, textHeight+1, "ZENKESHI!", EventReceiver.COLOR_YELLOW);
		}
	}

	/**
	 * Draw X on death columns
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	protected void drawXorTimer(GameEngine engine, int playerID) {
		for(int i = 0; i < (dangerColumnDouble ? 2 : 1); i++) {
			if((engine.field == null) || (engine.field.getBlockEmpty(2 + i, 0))) {
				if(engine.displaysize == 1) {
					receiver.drawMenuFont(engine, playerID, 4 + (i * 2), 0, "e", EventReceiver.COLOR_RED, 2.0f);
				} else {
					receiver.drawMenuFont(engine, playerID, 2 + i, 0, "e", EventReceiver.COLOR_RED);
				}
			}
		}
	}

	/*
	 * Called after every frame
	 */
	@Override
	public void onLast(GameEngine engine, int playerID) {
		if (scgettime > 0)
			scgettime--;
		if (chainDisplay > 0)
			chainDisplay--;

		if (gametype == 1) {
			int remainTime = ULTRA_MAX_TIME - engine.statistics.time;
			// Time meter
			engine.meterValue = (remainTime * receiver.getMeterMax(engine)) / ULTRA_MAX_TIME;
			engine.meterColor = GameEngine.METER_COLOR_GREEN;
			if(remainTime <= 3600) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
			if(remainTime <= 1800) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
			if(remainTime <= 600) engine.meterColor = GameEngine.METER_COLOR_RED;

			// Out of time
			if((engine.statistics.time >= ULTRA_MAX_TIME) && (engine.timerActive == true)) {
				engine.gameEnded();
				engine.resetStatc();
				engine.stat = GameEngine.STAT_ENDINGSTART;
				return;
			}
		} else if (gametype == 2) {
			int remainScore = SPRINT_MAX_SCORE[sprintTarget] - engine.statistics.score;
			if(engine.timerActive == false) remainScore = 0;
			engine.meterValue = (remainScore * receiver.getMeterMax(engine)) / SPRINT_MAX_SCORE[sprintTarget];
			engine.meterColor = GameEngine.METER_COLOR_GREEN;
			if(remainScore <= 50) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
			if(remainScore <= 30) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
			if(remainScore <= 10) engine.meterColor = GameEngine.METER_COLOR_RED;

			// ゴール
			if((engine.statistics.score >= SPRINT_MAX_SCORE[sprintTarget]) && (engine.timerActive == true)) {
				engine.gameEnded();
				engine.resetStatc();
				engine.stat = GameEngine.STAT_ENDINGSTART;
			}
		}
	}

	protected void addBonus (GameEngine engine, int playerID) {
		if (gametype != 2)
			super.addBonus(engine, playerID);
	}

	protected int calcChainMultiplier(int chain) {
		if (scoreType == 0)
		{
			if (chain == 2)
				return 8;
			else if (chain == 3)
				return 16;
			else if (chain >= 4)
				return 32*(chain-3);
		}
		else if (chain > CHAIN_POWERS_FEVERTYPE.length)
			return CHAIN_POWERS_FEVERTYPE[CHAIN_POWERS_FEVERTYPE.length-1];
		else
			return CHAIN_POWERS_FEVERTYPE[chain-1];
		return 0;
	}

	public boolean lineClearEnd(GameEngine engine, int playerID) {
		super.lineClearEnd(engine, playerID);

		if (engine.field != null)
		{
			if (!engine.field.getBlockEmpty(2, 0) || (dangerColumnDouble && !engine.field.getBlockEmpty(3, 0)))
			{
				engine.stat = GameEngine.STAT_GAMEOVER;
				engine.gameEnded();
				engine.resetStatc();
				engine.statc[1] = 1;
			}
		}

		return false;
	}

	/*
	 * Render results screen
	 */
	@Override
	public void renderResult(GameEngine engine, int playerID) {

		if (gametype == 2)
		{
			receiver.drawMenuFont(engine, playerID,  0, 1, "PLAY DATA", EventReceiver.COLOR_ORANGE);
			receiver.drawMenuFont(engine, playerID,  0, 3, "TIME", EventReceiver.COLOR_BLUE);
			String strTime = String.format("%10s", GeneralUtil.getTime(engine.statistics.time));
			receiver.drawMenuFont(engine, playerID,  0, 4, strTime);
			receiver.drawMenuFont(engine, playerID,  0, 5, "SCORE", EventReceiver.COLOR_BLUE);
			receiver.drawMenuFont(engine, playerID,  0, 6, String.valueOf(engine.statistics.score));
			receiver.drawMenuFont(engine, playerID,  0, 7, "ZENKESHI", EventReceiver.COLOR_BLUE);
			receiver.drawMenuFont(engine, playerID,  0, 8, String.format("%10d", zenKeshiCount));
			receiver.drawMenuFont(engine, playerID,  0, 9, "MAX CHAIN", EventReceiver.COLOR_BLUE);
			receiver.drawMenuFont(engine, playerID,  0, 10, String.format("%10d", engine.statistics.maxChain));
			if(rankingRank != -1) {
				receiver.drawMenuFont(engine, playerID,  0, 11, "RANK", EventReceiver.COLOR_BLUE);
				String strRank = String.format("%10d", rankingRank + 1);
				receiver.drawMenuFont(engine, playerID,  0, 12, strRank);
			}
		}
		else
		{
			super.renderResult(engine, playerID);

			if(rankingRank != -1) {
				receiver.drawMenuFont(engine, playerID,  0, 15, "RANK", EventReceiver.COLOR_BLUE);
				String strRank = String.format("%10d", rankingRank + 1);
				receiver.drawMenuFont(engine, playerID,  0, 16, strRank);
			}
		}
	}

	/*
	 * Called when saving replay
	 */
	@Override
	public void saveReplay(GameEngine engine, int playerID, CustomProperties prop) {
		saveSetting(prop);

		// Update rankings
		if((owner.replayMode == false) && (engine.ai == null) && (engine.colorClearSize == 4)) {
			updateRanking(engine.statistics.score, engine.statistics.time, gametype, scoreType, numColors);

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
		gametype = prop.getProperty("avalanche.gametype", 0);
		sprintTarget = prop.getProperty("avalanche.sprintTarget", 0);
		scoreType = prop.getProperty("avalanche.scoreType", 0);
		outlinetype = prop.getProperty("avalanche.outlinetype", 0);
		numColors = prop.getProperty("avalanche.numcolors", 4);
		version = prop.getProperty("avalanche.version", 0);
		dangerColumnDouble = prop.getProperty("avalanche.dangerColumnDouble", false);
		dangerColumnShowX = prop.getProperty("avalanche.dangerColumnShowX", false);
		showChains = prop.getProperty("avalanche.showChains", true);
		cascadeSlow = prop.getProperty("avalanche.cascadeSlow", false);
		bigDisplay = prop.getProperty("avalanche.bigDisplay", false);
	}

	/**
	 * Save settings to property file
	 * @param prop Property file
	 */
	private void saveSetting(CustomProperties prop) {
		prop.setProperty("avalanche.gametype", gametype);
		prop.setProperty("avalanche.sprintTarget", sprintTarget);
		prop.setProperty("avalanche.scoreType", scoreType);
		prop.setProperty("avalanche.outlinetype", outlinetype);
		prop.setProperty("avalanche.numcolors", numColors);
		prop.setProperty("avalanche.version", version);
		prop.setProperty("avalanche.dangerColumnDouble", dangerColumnDouble);
		prop.setProperty("avalanche.dangerColumnShowX", dangerColumnShowX);
		prop.setProperty("avalanche.showChains", showChains);
		prop.setProperty("avalanche.cascadeSlow", cascadeSlow);
		prop.setProperty("avalanche.bigDisplay", bigDisplay);
	}

	/**
	 * Read rankings from property file
	 * @param prop Property file
	 * @param ruleName Rule name
	 */
	private void loadRanking(CustomProperties prop, String ruleName) {
		for(int i = 0; i < RANKING_MAX; i++) {
			for(int j = 0; j < GAMETYPE_MAX; j++) {
				for(int colors = 3; colors <= 5; colors++) {
					for(int sctype = 0; sctype < SCORETYPE_MAX; sctype++) {
						rankingScore[sctype][colors-3][j][i] = prop.getProperty("avalanche.ranking." + ruleName +
								".scoretype" + sctype + "." + colors + "colors." + j + ".score." + i, 0);
						rankingTime[sctype][colors-3][j][i] = prop.getProperty("avalanche.ranking." + ruleName +
								".scoretype" + sctype + "." + colors + "colors." + j + ".time." + i, -1);
					}
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
		for(int i = 0; i < RANKING_MAX; i++) {
			for(int j = 0; j < GAMETYPE_MAX; j++) {
				for(int colors = 3; colors <= 5; colors++) {
					for(int sctype = 0; sctype < SCORETYPE_MAX; sctype++) {
						prop.setProperty("avalanche.ranking." + ruleName + ".scoretype" + sctype +
								"." + colors + "colors." + j + ".score." + i, rankingScore[sctype][colors-3][j][i]);
						prop.setProperty("avalanche.ranking." + ruleName + ".scoretype" + sctype +
								"." + colors + "colors." + j + ".time." + i, rankingTime[sctype][colors-3][j][i]);
					}
				}
			}
		}
	}

	/**
	 * Update rankings
	 * @param sc Score
	 * @param li Lines
	 * @param time Time
	 */
	private void updateRanking(int sc, int time, int type, int sctype, int colors) {
		rankingRank = checkRanking(sc, time, type, sctype, colors);

		if(rankingRank != -1) {
			// Shift down ranking entries
			for(int i = RANKING_MAX - 1; i > rankingRank; i--) {
				rankingScore[sctype][colors-3][type][i] = rankingScore[sctype][colors-3][type][i - 1];
				rankingTime[sctype][colors-3][type][i] = rankingTime[sctype][colors-3][type][i - 1];
			}

			// Add new data
			rankingScore[sctype][colors-3][type][rankingRank] = sc;
			rankingTime[sctype][colors-3][type][rankingRank] = time;
		}
	}

	/**
	 * Calculate ranking position
	 * @param sc Score
	 * @param time Time
	 * @return Position (-1 if unranked)
	 */
	private int checkRanking(int sc, int time, int type, int sctype, int colors) {
		if (type == 2 && sc < SPRINT_MAX_SCORE[sprintTarget])
			return -1;
		for(int i = 0; i < RANKING_MAX; i++) {
			if (type == 0) {
				if(sc > rankingScore[sctype][colors-3][type][i]) {
					return i;
				} else if((sc == rankingScore[sctype][colors-3][type][i]) && (time < rankingTime[sctype][colors-3][type][i])) {
					return i;
				}
			} else if (type == 1) {
				if(sc > rankingScore[sctype][colors-3][type][i]) {
					return i;
				}
			} else if (type == 2) {
				if(time < rankingTime[sctype][colors-3][type+sprintTarget][i] || (rankingTime[sctype][colors-3][type+sprintTarget][i] < 0)) {
					return i;
				}
			}
		}

		return -1;
	}
}
