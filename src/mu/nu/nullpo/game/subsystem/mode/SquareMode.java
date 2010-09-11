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
import mu.nu.nullpo.game.component.Field;
import mu.nu.nullpo.game.event.EventReceiver;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.util.CustomProperties;
import mu.nu.nullpo.util.GeneralUtil;

/**
 * SQUARE Mode
 */
public class SquareMode extends DummyMode {
	/** Current version */
	private static final int CURRENT_VERSION = 1;

	public int[] tableGravityChangeScore =
	{
		150, 300, 400, 500, 600, 700, 800, 900, 1000, 1500, 2500, 4000, 5000
	};

	public int[] tableGravityValue =
	{
		1, 2, 3, 4, 6, 8, 10, 20, 30, 60, 120, 180, 300, -1
	};

	/** Number of ranking records */
	private static final int RANKING_MAX = 10;

	/** Number of ranking types */
	private static final int RANKING_TYPE = 3;

	/** Name of game types */
	private static final String[] GAMETYPE_NAME = {"MARATHON","ULTRA","SPRINT"};

	/** Number of game types */
	private static final int GAMETYPE_MAX = 3;

	/** Max time in Ultra */
	private static final int ULTRA_MAX_TIME = 10800;

	/** Max score in Sprint */
	private static final int SPRINT_MAX_SCORE = 150;

	/** GameManager object (Manages entire game status) */
	private GameManager owner;

	/** EventReceiver object (This receives many game events, can also be used for drawing the fonts.) */
	private EventReceiver receiver;

	/** Current gravity number (When the point reaches tableGravityChangeScore's value, this variable will increase) */
	private int gravityindex;

	/** Amount of points you just get from line clears */
	private int lastscore;

	/** Elapsed time from last line clear (lastscore is displayed to screen until this reaches to 120) */
	private int scgettime;

	/** Number of squares created */
	private int squares;

	/** Selected game type */
	private int gametype;

	/** Outline type */
	private int outlinetype;

	/** Type of spins allowed (0=off 1=t-only 2=all) */
	private int tspinEnableType;

	/** Use TNT64 avalanche (native+cascade) */
	private boolean tntAvalanche;

	/** Grayout broken blocks */
	private int grayoutEnable;

	/** Version number */
	private int version;

	/** Your place on leaderboard (-1: out of rank) */
	private int rankingRank;

	/** Score records */
	private int[][] rankingScore;

	/** Time records */
	private int[][] rankingTime;

	/** Squares records */
	private int[][] rankingSquares;

	/*
	 * Returns the name of this mode
	 */
	@Override
	public String getName() {
		return "SQUARE";
	}

	/*
	 * This function will be called when the game enters the main game screen.
	 */
	@Override
	public void playerInit(GameEngine engine, int playerID) {
		owner = engine.owner;
		receiver = engine.owner.receiver;
		lastscore = 0;
		scgettime = 0;
		squares = 0;

		outlinetype = 0;
		tspinEnableType = 2;
		grayoutEnable = 1;

		rankingRank = -1;
		rankingScore = new int[RANKING_TYPE][RANKING_MAX];
		rankingTime = new int[RANKING_TYPE][RANKING_MAX];
		rankingSquares = new int[RANKING_TYPE][RANKING_MAX];

		if(owner.replayMode == false) {
			loadSetting(owner.modeConfig);
			loadRanking(owner.modeConfig, engine.ruleopt.strRuleName);
			version = CURRENT_VERSION;
		} else {
			loadSetting(owner.replayProp);
		}

		engine.framecolor = GameEngine.FRAME_COLOR_PURPLE;
	}

	/**
	 * Set the gravity speed
	 * @param engine GameEngine
	 */
	public void setSpeed(GameEngine engine) {
		if (gametype == 0) {
			int speedlv = engine.statistics.score;
			if (speedlv < 0) speedlv = 0;
			if (speedlv > 5000) speedlv = 5000;

			while(speedlv >= tableGravityChangeScore[gravityindex]) gravityindex++;
			engine.speed.gravity = tableGravityValue[gravityindex];
		} else {
			engine.speed.gravity = 1;
		}
		engine.speed.denominator = 60;
	}

	/*
	 * Main routine for game setup screen
	 */
	@Override
	public boolean onSetting(GameEngine engine, int playerID) {
		// Main menu
		if(engine.owner.replayMode == false) {
			// Configuration changes
			int change = updateCursor(engine, 4);

			if(change != 0) {
				engine.playSE("change");

				switch(engine.statc[2]) {
				case 0:
					gametype += change;
					if(gametype < 0) gametype = GAMETYPE_MAX - 1;
					if(gametype > GAMETYPE_MAX - 1) gametype = 0;
					break;
				case 1:
					outlinetype += change;
					if(outlinetype < 0) outlinetype = 2;
					if(outlinetype > 2) outlinetype = 0;
					break;
				case 2:
					tspinEnableType += change;
					if(tspinEnableType < 0) tspinEnableType = 2;
					if(tspinEnableType > 2) tspinEnableType = 0;
					break;
				case 3:
					tntAvalanche = !tntAvalanche;
					break;
				case 4:
					grayoutEnable += change;
					if(grayoutEnable < 0) grayoutEnable = 2;
					if(grayoutEnable > 2) grayoutEnable = 0;
					break;
				}
			}

			// A button (confirm)
			if(engine.ctrl.isPush(Controller.BUTTON_A) && (engine.statc[3] >= 5)) {
				engine.playSE("decide");
				saveSetting(owner.modeConfig);
				receiver.saveModeConfig(owner.modeConfig);
				return false;
			}

			// B button (cancel)
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
	 * Renders game setup screen
	 */
	@Override
	public void renderSetting(GameEngine engine, int playerID) {
		String strOutline = "";
		if(outlinetype == 0) strOutline = "NORMAL";
		if(outlinetype == 1) strOutline = "CONNECT";
		if(outlinetype == 2) strOutline = "NONE";
		String strTSpinEnable = "";
		if(tspinEnableType == 0) strTSpinEnable = "OFF";
		if(tspinEnableType == 1) strTSpinEnable = "T-ONLY";
		if(tspinEnableType == 2) strTSpinEnable = "ALL";
		String grayoutStr = "";
		if(grayoutEnable == 0) grayoutStr = "OFF";
		if(grayoutEnable == 1) grayoutStr = "SPIN ONLY";
		if(grayoutEnable == 2) grayoutStr = "ALL";
		drawMenu(engine, playerID, receiver, 0, EventReceiver.COLOR_BLUE, 0,
				"GAME TYPE", GAMETYPE_NAME[gametype],
				"OUTLINE", strOutline,
				"AVALANCHE", strTSpinEnable,
				"AVALANCHE", tntAvalanche ? "TNT" : "WORLDS",
				"GRAYOUT", grayoutStr);
	}

	/*
	 * This function will be called before the game actually begins (after Ready&Go screen disappears)
	 */
	@Override
	public void startGame(GameEngine engine, int playerID) {
		engine.comboType = GameEngine.COMBO_TYPE_DISABLE;

		if(outlinetype == 0) engine.blockOutlineType = GameEngine.BLOCK_OUTLINE_NORMAL;
		if(outlinetype == 1) engine.blockOutlineType = GameEngine.BLOCK_OUTLINE_CONNECT;
		if(outlinetype == 2) engine.blockOutlineType = GameEngine.BLOCK_OUTLINE_NONE;

		if(tspinEnableType == 0) {
			engine.tspinEnable = false;
		} else if(tspinEnableType == 1) {
			engine.tspinEnable = true;
		} else {
			engine.tspinEnable = true;
			engine.useAllSpinBonus = true;
		}

		engine.speed.are = 30;
		engine.speed.areLine = 30;
		engine.speed.das = 10;
		engine.speed.lockDelay = 30;

		setSpeed(engine);
	}

	/*
	 * Piece movement
	 */
	@Override
	public boolean onMove(GameEngine engine, int playerID) {
		// Disable cascade
		engine.lineGravityType = GameEngine.LINE_GRAVITY_NATIVE;
		return false;
	}

	/*
	 * Renders HUD (leaderboard or game statistics)
	 */
	@Override
	public void renderLast(GameEngine engine, int playerID) {
		receiver.drawScoreFont(engine, playerID, 0, 0, "SQUARE ("+GAMETYPE_NAME[gametype]+")", EventReceiver.COLOR_DARKBLUE);

		if( (engine.stat == GameEngine.STAT_SETTING) || ((engine.stat == GameEngine.STAT_RESULT) && (owner.replayMode == false)) ) {
			if((owner.replayMode == false) && (engine.ai == null)) {
				if (gametype == 0) {
					receiver.drawScoreFont(engine, playerID, 3, 3, "SCORE SQUARE TIME", EventReceiver.COLOR_BLUE);
				} else if (gametype == 1) {
					receiver.drawScoreFont(engine, playerID, 3, 3, "SCORE SQUARE", EventReceiver.COLOR_BLUE);
				} else if (gametype == 2) {
					receiver.drawScoreFont(engine, playerID, 3, 3, "TIME     SQUARE", EventReceiver.COLOR_BLUE);
				}

				for(int i = 0; i < RANKING_MAX; i++) {
					receiver.drawScoreFont(engine, playerID, 0, 4 + i, String.format("%2d", i + 1), EventReceiver.COLOR_YELLOW);
					if (gametype == 0) {
						receiver.drawScoreFont(engine, playerID, 3, 4 + i, String.valueOf(rankingScore[gametype][i]), (i == rankingRank));
						receiver.drawScoreFont(engine, playerID, 9, 4 + i, String.valueOf(rankingSquares[gametype][i]), (i == rankingRank));
						receiver.drawScoreFont(engine, playerID, 16, 4 + i, GeneralUtil.getTime(rankingTime[gametype][i]), (i == rankingRank));
					} else if (gametype == 1) {
						receiver.drawScoreFont(engine, playerID, 3, 4 + i, String.valueOf(rankingScore[gametype][i]), (i == rankingRank));
						receiver.drawScoreFont(engine, playerID, 9, 4 + i, String.valueOf(rankingSquares[gametype][i]), (i == rankingRank));
					} else if (gametype == 2) {
						receiver.drawScoreFont(engine, playerID, 3, 4 + i, GeneralUtil.getTime(rankingTime[gametype][i]), (i == rankingRank));
						receiver.drawScoreFont(engine, playerID, 12, 4 + i, String.valueOf(rankingSquares[gametype][i]), (i == rankingRank));
					}
				}
			}
		} else {
			receiver.drawScoreFont(engine, playerID, 0, 3, "SCORE", EventReceiver.COLOR_BLUE);
			String strScore;
			if((lastscore == 0) || (scgettime <= 0)) {
				strScore = String.valueOf(engine.statistics.score);
			} else {
				strScore = String.valueOf(engine.statistics.score) + "(+" + String.valueOf(lastscore) + ")";
			}
			receiver.drawScoreFont(engine, playerID, 0, 4, strScore);

			receiver.drawScoreFont(engine, playerID, 0, 6, "LINE", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 0, 7, String.valueOf(engine.statistics.lines));

			receiver.drawScoreFont(engine, playerID, 0, 9, "SQUARE", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 0, 10, String.valueOf(squares));

			receiver.drawScoreFont(engine, playerID, 0, 12, "TIME", EventReceiver.COLOR_BLUE);
			if(gametype == 1) {
				// Ultra timer
				int time = ULTRA_MAX_TIME - engine.statistics.time;
				if(time < 0) time = 0;
				int fontcolor = EventReceiver.COLOR_WHITE;
				if((time < 30 * 60) && (time > 0)) fontcolor = EventReceiver.COLOR_YELLOW;
				if((time < 20 * 60) && (time > 0)) fontcolor = EventReceiver.COLOR_ORANGE;
				if((time < 10 * 60) && (time > 0)) fontcolor = EventReceiver.COLOR_RED;
				receiver.drawScoreFont(engine, playerID, 0, 13, GeneralUtil.getTime(time), fontcolor);
			} else {
				// Normal timer
				receiver.drawScoreFont(engine, playerID, 0, 13, GeneralUtil.getTime(engine.statistics.time));
			}
		}
	}

	/*
	 * This function will be called when the game timer updates
	 */
	@Override
	public void onLast(GameEngine engine, int playerID) {
		if (scgettime > 0) scgettime--;

		if (gametype == 1) {
			int remainTime = ULTRA_MAX_TIME - engine.statistics.time;
			// Timer meter
			engine.meterValue = (remainTime * receiver.getMeterMax(engine)) / ULTRA_MAX_TIME;
			engine.meterColor = GameEngine.METER_COLOR_GREEN;
			if(remainTime <= 3600) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
			if(remainTime <= 1800) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
			if(remainTime <= 600) engine.meterColor = GameEngine.METER_COLOR_RED;

			// Countdown
			if((remainTime > 0) && (remainTime <= 10 * 60) && (engine.statistics.time % 60 == 0) && (engine.timerActive == true)) {
				engine.playSE("countdown");
			}

			// BGM fadeout
			if((remainTime <= 5 * 60) && (engine.timerActive == true)) {
				owner.bgmStatus.fadesw = true;
			}

			// Time up!
			if((engine.statistics.time >= ULTRA_MAX_TIME) && (engine.timerActive == true)) {
				engine.gameActive = false;
				engine.timerActive = false;
				engine.resetStatc();
				engine.stat = GameEngine.STAT_ENDINGSTART;
				return;
			}
		} else if (gametype == 2) {
			int remainScore = SPRINT_MAX_SCORE - engine.statistics.score;
			if(engine.timerActive == false) remainScore = 0;
			engine.meterValue = (remainScore * receiver.getMeterMax(engine)) / SPRINT_MAX_SCORE;
			engine.meterColor = GameEngine.METER_COLOR_GREEN;
			if(remainScore <= 50) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
			if(remainScore <= 30) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
			if(remainScore <= 10) engine.meterColor = GameEngine.METER_COLOR_RED;

			// Goal
			if((engine.statistics.score >= SPRINT_MAX_SCORE) && (engine.timerActive == true)) {
				engine.gameActive = false;
				engine.timerActive = false;
				engine.resetStatc();
				engine.stat = GameEngine.STAT_ENDINGSTART;
			}
		}
	}

	/*
	 * Line clear
	 */
	@Override
	public boolean onLineClear(GameEngine engine, int playerID) {
		if(engine.statc[0] == 1) {
			if(grayoutEnable == 2) grayoutBrokenBlocks(engine.field);
		}
		return false;
	}

	/**
	 * Make all broken blocks gray.
	 * @param field Field
	 */
	private void grayoutBrokenBlocks(Field field) {
		for(int i = (field.getHiddenHeight() * -1); i < field.getHeightWithoutHurryupFloor(); i++) {
			for(int j = 0; j < field.getWidth(); j++) {
				Block blk = field.getBlock(j, i);
				if((blk != null) && !blk.isEmpty() && blk.getAttribute(Block.BLOCK_ATTRIBUTE_BROKEN)) {
					blk.color = Block.BLOCK_COLOR_GRAY;
				}
			}
		}
	}

	/*
	 * Calculates line-clear score
	 * (This function will be called even if no lines are cleared)
	 */
	@Override
	public void calcScore(GameEngine engine, int playerID, int lines) {
		if (lines > 0 && engine.tspin) {
			if (version == 0)
				avalancheOld(engine, playerID, lines);
			else
				avalanche(engine, playerID, lines);
			return;
		}

		// Line clear bonus
		int pts = lines;

		if (lines > 0) {
			engine.lineGravityType = GameEngine.LINE_GRAVITY_NATIVE;
			if (engine.field.isEmpty()) {
				engine.playSE("bravo");
			}

			if (lines > 3) {
				pts = 3 + (lines - 3)*2;
			}

			int[] squareClears = engine.field.getHowManySquareClears();
			pts += 10*squareClears[0]+5*squareClears[1];

			lastscore = pts;
			scgettime = 120;
			engine.statistics.scoreFromLineClear += pts;
			engine.statistics.score += pts;
			setSpeed(engine);

		}
	}

	/**
	 * Spin avalanche routine.
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @param lines Number of lines cleared
	 */
	private void avalanche(GameEngine engine, int playerID, int lines) {
		Field field = engine.field;
		field.setAllAttribute(Block.BLOCK_ATTRIBUTE_ANTIGRAVITY, false);

		int hiddenHeight = field.getHiddenHeight();
		int height = field.getHeight();
		boolean[] affectY = new boolean[height+hiddenHeight];
		for (int i = 0; i < affectY.length; i++)
			affectY[i] = false;
		int minY = engine.nowPieceObject.getMinimumBlockY()+engine.nowPieceY;
		if (field.getLineFlag(minY))
			for (int i = minY+hiddenHeight; i >= 0; i--)
				affectY[i] = true;

		int testY = minY+1;

		while (!field.getLineFlag(testY) && testY < height)
			testY++;
		for (int y = testY+hiddenHeight; y < affectY.length; y++)
			affectY[y] = true;

		for (int y = (hiddenHeight * -1); y < height; y++)
		{
			if (affectY[y+hiddenHeight])
			{
				for(int x = 0; x < field.getWidth(); x++) {
					Block blk = field.getBlock(x, y);
					if((blk != null) && !blk.isEmpty()) {
						// Change each affected block to broken and garbage, and break connections.
						blk.setAttribute(Block.BLOCK_ATTRIBUTE_GARBAGE, true);
						blk.setAttribute(Block.BLOCK_ATTRIBUTE_BROKEN, true);
						blk.setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_UP, false);
						blk.setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_DOWN, false);
						blk.setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_LEFT, false);
						blk.setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT, false);
						if(grayoutEnable != 0) blk.color = Block.BLOCK_COLOR_GRAY;
					}
				}
			}
			else if(tntAvalanche)
			{
				// Set anti-gravity when TNT avalanche is used
				for(int x = 0; x < field.getWidth(); x++) {
					Block blk = field.getBlock(x, y);
					if((blk != null) && !blk.isEmpty()) {
						blk.setAttribute(Block.BLOCK_ATTRIBUTE_ANTIGRAVITY, true);
					}
					blk = field.getBlock(x, y-1);
					if((blk != null) && !blk.isEmpty()) {
						blk.setAttribute(Block.BLOCK_ATTRIBUTE_ANTIGRAVITY, true);
					}
				}
			}
		}
		// Reset line flags
		for (int y = (-1 * hiddenHeight); y < height; y++)
			engine.field.setLineFlag(y, false);
		// Set cascade flag
		engine.lineGravityType = GameEngine.LINE_GRAVITY_CASCADE;
	}

	/**
	 * Old T-Spin avalanche routine.
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @param lines Number of lines cleared
	 */
	@Deprecated
	private void avalancheOld(GameEngine engine, int playerID, int lines) {
		Field field = engine.field;
		field.setAllAttribute(Block.BLOCK_ATTRIBUTE_ANTIGRAVITY, false);

		// This sets the highest line that will be affected by the avalanche.
		int topLine = field.getHiddenHeight() * -1;
		if(lines == 1) {
			for(int i = (field.getHiddenHeight() * -1); i < field.getHeightWithoutHurryupFloor(); i++) {
				if(field.getLineFlag(i)) {
					// Found a line
					topLine = i + 1;
					break;
				} else if(tntAvalanche) {
					// Set anti-gravity when TNT avalanche is used
					for(int j = 0; j < field.getWidth(); j++) {
						Block blk = field.getBlock(j, i);
						if((blk != null) && !blk.isEmpty()) {
							blk.setAttribute(Block.BLOCK_ATTRIBUTE_ANTIGRAVITY, true);
						}
					}
				}
			}
		}

		for(int i = (field.getHeightWithoutHurryupFloor() - 1); i >= topLine; i--) {
			// There can be lines cleared underneath, in case of a spin hurdle or such.
			if(!field.getLineFlag(i)) {
				for(int j = 0; j < field.getWidth(); j++) {
					Block blk = field.getBlock(j, i);
					if((blk != null) && !blk.isEmpty()) {
						// Change each affected block to broken and garbage, and break connections.
						blk.setAttribute(Block.BLOCK_ATTRIBUTE_GARBAGE, true);
						blk.setAttribute(Block.BLOCK_ATTRIBUTE_BROKEN, true);
						blk.setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_UP, false);
						blk.setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_DOWN, false);
						blk.setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_LEFT, false);
						blk.setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT, false);
						if(grayoutEnable != 0) blk.color = Block.BLOCK_COLOR_GRAY;
					}
				}
			}
		}

		// Set cascade flag
		engine.lineGravityType = GameEngine.LINE_GRAVITY_CASCADE;
	}

	/*
	 * When the line clear ends
	 */
	@Override
	public boolean lineClearEnd(GameEngine engine, int playerID) {
		if((engine.lineGravityType == GameEngine.LINE_GRAVITY_CASCADE) && (engine.lineGravityTotalLines > 0) && (tntAvalanche)) {
			Field field = engine.field;
			for(int i = field.getHeightWithoutHurryupFloor() - 1; i >= (field.getHiddenHeight() * -1); i--) {
				if(field.isEmptyLine(i)) {
					field.cutLine(i, 1);
					engine.lineGravityTotalLines--;
					return true;
				}
			}
		}
		return false;
	}

	/*
	 * Check for squares when piece locks
	 */
	@Override
	public void pieceLocked(GameEngine engine, int playerID, int lines) {
		int[] sq = engine.field.checkForSquares();
		squares += sq[0] + sq[1];
		if(sq[0] == 0 && sq[1] > 0) engine.playSE("square_s");
		else if(sq[0] > 0) engine.playSE("square_g");
	}

	/*
	 * Results screen
	 */
	@Override
	public void renderResult(GameEngine engine, int playerID) {
		receiver.drawMenuFont(engine, playerID,  0, 1, "PLAY DATA", EventReceiver.COLOR_ORANGE);

		drawResult(engine, playerID, receiver, 3, EventReceiver.COLOR_BLUE,
				"SCORE", String.format("%10d", engine.statistics.score),
				"LINE", String.format("%10d", engine.statistics.lines),
				"SQUARE", String.format("%10d", squares),
				"TIME", String.format("%10s", GeneralUtil.getTime(engine.statistics.time)));
		drawResultRank(engine, playerID, receiver, 11, EventReceiver.COLOR_BLUE, rankingRank);
	}

	/*
	 * This function will be called when the replay data is going to be saved
	 */
	@Override
	public void saveReplay(GameEngine engine, int playerID, CustomProperties prop) {
		saveSetting(prop);
		prop.setProperty("square.squares", squares);

		// Update the ranking
		if((owner.replayMode == false) && (engine.ai == null)) {
			updateRanking(engine.statistics.score, engine.statistics.time, squares, gametype);

			if(rankingRank != -1) {
				saveRanking(owner.modeConfig, engine.ruleopt.strRuleName);
				receiver.saveModeConfig(owner.modeConfig);
			}
		}
	}

	/**
	 * Load the settings from CustomProperties
	 * @param prop CustomProperties to read
	 */
	private void loadSetting(CustomProperties prop) {
		gametype = prop.getProperty("square.gametype", 0);
		outlinetype = prop.getProperty("square.outlinetype", 0);
		tspinEnableType = prop.getProperty("square.tspinEnableType", 2);
		tntAvalanche = prop.getProperty("square.tntAvalanche", false);
		if (version == 0)
			grayoutEnable = prop.getProperty("square.grayoutEnable", false) ? 2 : 0;
		else
			grayoutEnable = prop.getProperty("square.grayoutEnable", 2);
		version = prop.getProperty("square.version", 0);
	}

	/**
	 * Save the settings to CustomProperties
	 * @param prop CustomProperties to write
	 */
	private void saveSetting(CustomProperties prop) {
		prop.setProperty("square.gametype", gametype);
		prop.setProperty("square.outlinetype", outlinetype);
		prop.setProperty("square.tspinEnableType", tspinEnableType);
		prop.setProperty("square.tntAvalanche", tntAvalanche);
		prop.setProperty("square.grayoutEnable", grayoutEnable);
		prop.setProperty("square.version", version);
	}

	/**
	 * Load the ranking from CustomProperties
	 * @param prop CustomProperties to read
	 * @param ruleName Rule name
	 */
	private void loadRanking(CustomProperties prop, String ruleName) {
		for(int i = 0; i < RANKING_MAX; i++) {
			for(int j = 0; j < GAMETYPE_MAX; j++) {
				rankingScore[j][i] = prop.getProperty("square.ranking." + ruleName + "." + j + ".score." + i, 0);
				rankingTime[j][i] = prop.getProperty("square.ranking." + ruleName + "." + j + ".time." + i, -1);
				rankingSquares[j][i] = prop.getProperty("square.ranking." + ruleName + "." + j + ".squares." + i, 0);
			}
		}
	}

	/**
	 * Save the ranking to CustomProperties
	 * @param prop CustomProperties to write
	 * @param ruleName Rule name
	 */
	private void saveRanking(CustomProperties prop, String ruleName) {
		for(int i = 0; i < RANKING_MAX; i++) {
			for(int j = 0; j < GAMETYPE_MAX; j++) {
				prop.setProperty("square.ranking." + ruleName + "." + j + ".score." + i, rankingScore[j][i]);
				prop.setProperty("square.ranking." + ruleName + "." + j + ".time." + i, rankingTime[j][i]);
				prop.setProperty("square.ranking." + ruleName + "." + j + ".squares." + i, rankingSquares[j][i]);
			}
		}
	}

	/**
	 * Update the ranking
	 * @param sc Score
	 * @param time Time
	 * @param sq Squares
	 * @param type GameType
	 */
	private void updateRanking(int sc, int time, int sq, int type) {
		rankingRank = checkRanking(sc, time, sq, type);

		if(rankingRank != -1) {
			// Shift the old records
			for(int i = RANKING_MAX - 1; i > rankingRank; i--) {
				rankingScore[type][i] = rankingScore[type][i - 1];
				rankingTime[type][i] = rankingTime[type][i - 1];
				rankingSquares[type][i] = rankingSquares[type][i - 1];
			}

			// Register new record
			rankingScore[type][rankingRank] = sc;
			rankingTime[type][rankingRank] = time;
			rankingSquares[type][rankingRank] = sq;
		}
	}

	/**
	 * This function will check the ranking and returns which place you are. (-1: Out of rank)
	 * @param sc Score
	 * @param time Time
	 * @param sq Squares
	 * @param type GameType
	 * @return Place (-1: Out of rank)
	 */
	private int checkRanking(int sc, int time, int sq, int type) {
		for(int i = 0; i < RANKING_MAX; i++) {
			if (gametype == 0) {
				// Marathon
				if(sc > rankingScore[type][i]) {
					return i;
				} else if((sc == rankingScore[type][i]) && (sq > rankingSquares[type][i])) {
					return i;
				} else if((sc == rankingScore[type][i]) && (sq == rankingSquares[type][i]) && (time < rankingTime[type][i])) {
					return i;
				}
			} else if (gametype == 1 && time >= ULTRA_MAX_TIME) {
				// Ultra
				if(sc > rankingScore[type][i]) {
					return i;
				} else if((sc == rankingScore[type][i]) && (sq > rankingSquares[type][i])) {
					return i;
				}
			} else if (gametype == 2 && sc >= SPRINT_MAX_SCORE) {
				// Sprint
				if((time < rankingTime[type][i]) || (rankingTime[type][i] < 0)) {
					return i;
				} else if((time == rankingTime[type][i]) && (sq > rankingSquares[type][i])) {
					return i;
				}
			}
		}

		return -1;
	}
}
