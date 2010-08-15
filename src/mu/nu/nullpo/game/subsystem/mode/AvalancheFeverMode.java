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
import mu.nu.nullpo.game.component.Piece;
import mu.nu.nullpo.game.event.EventReceiver;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.util.CustomProperties;
import mu.nu.nullpo.util.GeneralUtil;

/**
 * AVALANCHE FEVER MARATHON mode (Release Candidate 1)
 */
public class AvalancheFeverMode extends DummyMode {
	/** Current version */
	private static final int CURRENT_VERSION = 0;
	
	/** Enabled piece types */
	private static final int[] PIECE_ENABLE = {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0};
	
	/** Block colors */
	private static final int[] BLOCK_COLORS =
	{
		Block.BLOCK_COLOR_RED,
		Block.BLOCK_COLOR_GREEN,
		Block.BLOCK_COLOR_BLUE,
		Block.BLOCK_COLOR_YELLOW,
		Block.BLOCK_COLOR_PURPLE
	};
	
	private static final int[] CHAIN_POWERS = 
	{
		4, 10, 18, 22, 30, 48, 80, 120, 160, 240, 280, 288, 342, 400, 440, 480, 520, 560, 600, 640, 680, 720, 760, 800 //Amitie
	};

	public int[] tableGravityChangeScore =
	{
		15000, 30000, 40000, 50000, 60000, 70000, 80000, 90000, 100000, 150000, 250000, 400000, Integer.MAX_VALUE
	};

	public int[] tableGravityValue =
	{
		1, 2, 3, 4, 6, 8, 10, 20, 30, 60, 120, 180, 300, -1
	};

	/** Fever map files list */
	private static final String[] FEVER_MAPS = 
	{
		"Fever", "15th", "15thDS", "7"
	};

	/** Names of chain display settings */
	private static final String[] CHAIN_DISPLAY_NAMES = {"OFF", "YELLOW", "SIZE"};

	/** Number of ranking records */
	private static final int RANKING_MAX = 10;

	/** Time limit */
	private static final int TIME_LIMIT = 3600;

	/** GameManager object (Manages entire game status) */
	private GameManager owner;

	/** EventReceiver object (This receives many game events, can also be used for drawing the fonts.) */
	private EventReceiver receiver;

	/** Current 落下速度の number（tableGravityChangeLevelの levelに到達するたびに1つ増える） */
	private int gravityindex;

	/** Amount of points earned from most recent clear */
	private int lastscore, lastmultiplier;

	/** Elapsed time from last line clear (lastscore is displayed to screen until this reaches to 120) */
	private int scgettime;

	/** Selected game type */
	private int mapSet;

	/** Outline type */
	private int outlinetype;

	/** Version number */
	private int version;

	/** Current round's ranking rank */
	private int rankingRank;

	/** Rankings' line counts */
	private int[][][] rankingScore;

	/** Rankings' times */
	private int[][][] rankingTime;
	
	/** Flag for all clear */
	private int zenKeshiDisplay;
	
	/** Flag set to true if first group in the chain is larger minimum size */
	//private boolean firstExtra;
	
	/** Amount of garbage sent */
	private int garbageSent, garbageAdd;
	
	/** Number of colors to use */
	private int numColors;
	
	/** Time limit left */
	private int timeLimit;
	
	/** Time added to limit */
	private int timeLimitAdd;
	
	/** Time to display added time */
	private int timeLimitAddDisplay;

	/** Fever map CustomProperties */
	private CustomProperties propFeverMap;
	
	/** Chain levels for Fever Mode */
	private int feverChain;
	
	/** Chain level boundaries for Fever Mode */
	private int feverChainMin, feverChainMax;
	
	/** Flag set to true when last piece caused a clear */
	private boolean cleared;
	
	/** Flag for all-clears */
	private boolean zenKeshi;
	
	/** Number of all clears */
	private int zenKeshiCount;
	
	/** Max chain */
	private int maxChain;
	
	/** List of subsets in selected map */
	private String[] mapSubsets;
	
	/** Last chain hit number */
	private int chain;
	
	/** Time to display last chain */
	private int chainDisplay;
	
	/** Fever chain count when last chain hit occurred */
	private int feverChainDisplay;
	
	/** Type of chain display */
	private int chainDisplayType;
	
	/** Score before adding zenkeshi bonus and max chain bonus */
	private int scoreBeforeBonus;
	
	/** Zenkeshi bonus and max chain bonus amounts */
	private int zenKeshiBonus, maxChainBonus;
	
	/** Number of boards played */
	private int boardsPlayed;
	
	/** Blocks cleared */
	private int blocksCleared;
	
	/** Current level */
	private int level;
	
	/** Blocks blocksCleared needed to reach next level */
	private int toNextLevel;
	
	/** Level at start of chain */
	private int chainLevelMultiplier;
	
	/*
	 * Mode  name
	 */
	@Override
	public String getName() {
		return "AVALANCHE 1P FEVER MARATHON (RC1)";
	}

	/*
	 * Initialization
	 */
	@Override
	public void playerInit(GameEngine engine, int playerID) {
		owner = engine.owner;
		receiver = engine.owner.receiver;
		lastscore = 0;
		lastmultiplier = 0;
		scgettime = 0;

		outlinetype = 0;
		
		zenKeshiDisplay = 0;
		garbageSent = 0;
		garbageAdd = 0;
		//firstExtra = false;
		
		cleared = false;
		zenKeshi = false;
		zenKeshiCount = 0;
		maxChain = 0;
		scoreBeforeBonus = 0;
		zenKeshiBonus = 0;
		maxChainBonus = 0;
		boardsPlayed = 0;
		blocksCleared = 0;
		
		timeLimit = TIME_LIMIT;
		timeLimitAdd = 0;
		timeLimitAddDisplay = 0;
		
		chain = 0;
		chainDisplay = 0;
		feverChainDisplay = 0;
		chainDisplayType = 0;
		
		feverChain = 5;

		rankingRank = -1;
		rankingScore = new int[3][FEVER_MAPS.length][RANKING_MAX];
		rankingTime = new int[3][FEVER_MAPS.length][RANKING_MAX];

		if(owner.replayMode == false) {
			loadSetting(owner.modeConfig);
			loadRanking(owner.modeConfig, engine.ruleopt.strRuleName);
			version = CURRENT_VERSION;
		} else {
			loadSetting(owner.replayProp);
		}

		engine.framecolor = GameEngine.FRAME_COLOR_PURPLE;
		engine.clearMode = GameEngine.CLEAR_COLOR;
		engine.garbageColorClear = true;
		engine.colorClearSize = 4;
		engine.ignoreHidden = true;
		engine.lineGravityType = GameEngine.LINE_GRAVITY_CASCADE;
		for(int i = 0; i < Piece.PIECE_COUNT; i++)
			engine.nextPieceEnable[i] = (PIECE_ENABLE[i] == 1);
		engine.randomBlockColor = true;
		engine.blockColors = BLOCK_COLORS;
		engine.connectBlocks = false;
		engine.cascadeDelay = 1;
		engine.cascadeClearDelay = 10;
		/*
		engine.fieldWidth = 6;
		engine.fieldHeight = 12;
		engine.fieldHiddenHeight = 2;
		*/
	}

	/**
	 * Set the gravity rate
	 * @param engine GameEngine
	 */
	public void setSpeed(GameEngine engine) {
		if (mapSet == 0) {
			int speedlv = engine.statistics.score;
			if (speedlv < 0) speedlv = 0;
			if (speedlv > 5000) speedlv = 5000;

			while(speedlv >= tableGravityChangeScore[gravityindex]) gravityindex++;
			engine.speed.gravity = tableGravityValue[gravityindex];
		} else {
			engine.speed.gravity = 1;
		}
		engine.speed.denominator = 256;
	}

	public boolean onReady(GameEngine engine, int playerID) {
		if(engine.statc[0] == 0)
		{
			loadMapSetFever(engine, playerID, mapSet, true);
			engine.numColors = numColors;
			loadFeverMap(engine, playerID, feverChain);
			timeLimit = TIME_LIMIT;
			timeLimitAdd = 0;
			timeLimitAddDisplay = 0;

			if(outlinetype == 0) engine.blockOutlineType = GameEngine.BLOCK_OUTLINE_NORMAL;
			if(outlinetype == 1) engine.blockOutlineType = GameEngine.BLOCK_OUTLINE_SAMECOLOR;
			if(outlinetype == 2) engine.blockOutlineType = GameEngine.BLOCK_OUTLINE_NONE;
			
			if (numColors == 3) level = 1;
			else if (numColors == 4) level = 5;
			else if (numColors == 5) level = 10;
			chainLevelMultiplier = level;
			toNextLevel = 15;
			
			zenKeshiCount = 0;
			maxChain = 0;
			scoreBeforeBonus = 0;
			zenKeshiBonus = 0;
			maxChainBonus = 0;
			blocksCleared = 0;
		}
		return false;
	}
	
	/*
	 * Called at settings screen
	 */
	@Override
	public boolean onSetting(GameEngine engine, int playerID) {
		// Menu
		if(engine.owner.replayMode == false) {
			// Configuration changes
			int change = updateCursor(engine, 3);

			if(change != 0) {
				engine.playSE("change");

				switch(engine.statc[2]) {

				case 0:
					mapSet += change;
					if(mapSet < 0) mapSet = FEVER_MAPS.length - 1;
					if(mapSet > FEVER_MAPS.length - 1) mapSet = 0;
					break;
				case 1:
					outlinetype += change;
					if(outlinetype < 0) outlinetype = 2;
					if(outlinetype > 2) outlinetype = 0;
					break;
				case 2:
					numColors += change;
					if(numColors < 3) numColors = 5;
					if(numColors > 5) numColors = 3;
					break;
				case 3:
					chainDisplayType += change;
					if(chainDisplayType < 0) chainDisplayType = 2;
					if(chainDisplayType > 2) chainDisplayType = 0;
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
		String strOutline = "";
		if(outlinetype == 0) strOutline = "NORMAL";
		if(outlinetype == 1) strOutline = "COLOR";
		if(outlinetype == 2) strOutline = "NONE";
		
		drawMenu(engine, playerID, receiver, 0, EventReceiver.COLOR_BLUE, 0,
				"MAP SET", FEVER_MAPS[mapSet].toUpperCase(),
				"OUTLINE", strOutline,
				"COLORS", String.valueOf(numColors),
				"SHOW CHAIN", CHAIN_DISPLAY_NAMES[chainDisplayType]);
	}

	/*
	 * Called for initialization during "Ready" screen
	 */
	@Override
	public void startGame(GameEngine engine, int playerID) {
		engine.comboType = GameEngine.COMBO_TYPE_DISABLE;

		engine.tspinEnable = false;
		engine.useAllSpinBonus = false;
		engine.tspinAllowKick = false;

		engine.speed.are = 30;
		engine.speed.areLine = 30;
		engine.speed.das = 10;
		engine.speed.lockDelay = 60;

		setSpeed(engine);
	}

	/*
	 * Render score
	 */
	@Override
	public void renderLast(GameEngine engine, int playerID) {
		receiver.drawScoreFont(engine, playerID, 0, 0, "AVALANCHE FEVER MARATHON", EventReceiver.COLOR_DARKBLUE);
		receiver.drawScoreFont(engine, playerID, 0, 1, "(" + FEVER_MAPS[mapSet].toUpperCase() + " " +
				numColors + " COLORS)", EventReceiver.COLOR_DARKBLUE);

		if( (engine.stat == GameEngine.STAT_SETTING) || ((engine.stat == GameEngine.STAT_RESULT) && (owner.replayMode == false)) ) {
			if((owner.replayMode == false) && (engine.ai == null)) {
				receiver.drawScoreFont(engine, playerID, 3, 3, "SCORE      TIME", EventReceiver.COLOR_BLUE);

				for(int i = 0; i < RANKING_MAX; i++) {
					receiver.drawScoreFont(engine, playerID, 0, 4 + i, String.format("%2d", i + 1), EventReceiver.COLOR_YELLOW);
					receiver.drawScoreFont(engine, playerID, 3, 4 + i, String.valueOf(rankingScore[numColors-3][mapSet][i]), (i == rankingRank));
					receiver.drawScoreFont(engine, playerID, 14, 4 + i, GeneralUtil.getTime(rankingTime[numColors-3][mapSet][i]), (i == rankingRank));
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
			
			receiver.drawScoreFont(engine, playerID, 0, 9, "TIME", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 0, 10, GeneralUtil.getTime(engine.statistics.time));
			
			receiver.drawScoreFont(engine, playerID, 0, 12, "LIMIT TIME", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 0, 13, GeneralUtil.getTime(timeLimit));
			if (timeLimitAddDisplay > 0)
				receiver.drawScoreFont(engine, playerID, 0, 14, "(+" + (timeLimitAdd/60) + " SEC.)");
			
			String timeStr = String.valueOf(timeLimit/60);
			if (timeStr.length() == 1)
				timeStr = "0" + timeStr;
			receiver.drawMenuFont(engine, playerID, 2, 0, timeStr, EventReceiver.COLOR_RED);
			
			receiver.drawScoreFont(engine, playerID, 11, 6, "BOARDS", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 11, 7, String.valueOf(boardsPlayed));
			
			receiver.drawScoreFont(engine, playerID, 11, 9, "ZENKESHI", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 11, 10, String.valueOf(zenKeshiCount));
			
			receiver.drawScoreFont(engine, playerID, 11, 12, "MAX CHAIN", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 11, 13, String.valueOf(maxChain));

			receiver.drawScoreFont(engine, playerID, 11, 15, "OJAMA SENT", EventReceiver.COLOR_BLUE);
			String strSent = String.valueOf(garbageSent);
			if(garbageAdd > 0) {
				strSent = strSent + "(+" + String.valueOf(garbageAdd)+ ")";
			}
			receiver.drawScoreFont(engine, playerID, 11, 16, strSent);
			
			receiver.drawScoreFont(engine, playerID, 11, 18, "CLEARED", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 11, 19, String.valueOf(blocksCleared));
			
			int textHeight = 13;
			if (engine.field != null)
				textHeight = engine.field.getHeight()+1;
			if (chain > 0 && chainDisplay > 0 && chainDisplayType != 0)
			{
				int color = EventReceiver.COLOR_YELLOW;
				if (chainDisplayType == 2)
				{
					if (chain >= feverChainDisplay)
						color = EventReceiver.COLOR_GREEN;
					else if (chain == feverChainDisplay-2)
						color = EventReceiver.COLOR_ORANGE;
					else if (chain < feverChainDisplay-2)
						color = EventReceiver.COLOR_RED;
				}
				receiver.drawMenuFont(engine, playerID, chain > 9 ? 0 : 1, textHeight, chain + " CHAIN!", color);
			}
			if (zenKeshiDisplay > 0)
				receiver.drawMenuFont(engine, playerID, 0, textHeight+1, "ZENKESHI!", EventReceiver.COLOR_YELLOW);
		}
	}

	public boolean onMove (GameEngine engine, int playerID) {
		cleared = false;
		zenKeshi = false;
		return false;
	}
	
	/*
	 * Called after every frame
	 */
	@Override
	public void onLast(GameEngine engine, int playerID) {
		if (scgettime > 0) scgettime--;
		
		if (engine.timerActive)
		{
			if (chainDisplay > 0)
				chainDisplay--;
			if (zenKeshiDisplay > 0)
				zenKeshiDisplay--;
			if (timeLimit > 0)
			{
				if (timeLimit == 1)
					engine.playSE("levelstop");
				timeLimit--;
			}
		}
		if (timeLimitAddDisplay > 0)
			timeLimitAddDisplay--;

		//  timeMeter
		engine.meterValue = (timeLimit * receiver.getMeterMax(engine)) / TIME_LIMIT;
		engine.meterColor = GameEngine.METER_COLOR_GREEN;
		if(timeLimit <= 1800) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
		if(timeLimit <= 900) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
		if(timeLimit <= 
			300) engine.meterColor = GameEngine.METER_COLOR_RED;
	}

	/*
	 * ゲームオーバー
	 */
	@Override
	public boolean onGameOver(GameEngine engine, int playerID) {
		if(engine.statc[0] == 0)
		{
			scoreBeforeBonus = engine.statistics.score;
			if (numColors >= 5)
				zenKeshiBonus = zenKeshiCount*zenKeshiCount*1000;
			else if (numColors == 4)
				zenKeshiBonus = zenKeshiCount*(zenKeshiCount+1)*500;
			else
				zenKeshiBonus = zenKeshiCount*(zenKeshiCount+3)*250;
			maxChainBonus = maxChain*maxChain*2000;
			engine.statistics.score += zenKeshiBonus + maxChainBonus;
		}
		return false;
	}

	/*
	 * Hard dropしたときの処理
	 */
	@Override
	public void afterHardDropFall(GameEngine engine, int playerID, int fall) {
		engine.statistics.score += fall;
	}

	/*
	 * Hard dropしたときの処理
	 */
	@Override
	public void afterSoftDropFall(GameEngine engine, int playerID, int fall) {
		engine.statistics.score += fall;
	}
	
	/*
	 * Calculate score
	 */
	@Override
	public void calcScore(GameEngine engine, int playerID, int avalanche) {
		// Line clear bonus
		int pts = avalanche*10;

		if (avalanche > 0) {
			cleared = true;
			if (engine.field.isEmpty()) {
				engine.playSE("bravo");
				zenKeshi = true;
				zenKeshiDisplay = 120;
				zenKeshiCount++;
				// engine.statistics.score += 2100;
			}

			chain = engine.chain;
			if (chain == 1)
				chainLevelMultiplier = level;
			if (chain > maxChain)
				maxChain = chain;
			blocksCleared += avalanche;
			toNextLevel -= avalanche;
			if (toNextLevel <= 0)
			{
				toNextLevel = 15;
				if (level < 99)
					level++;
			}
			
			pts *= chainLevelMultiplier;
			
			chainDisplay = 60;
			feverChainDisplay = feverChain;
			engine.playSE("combo" + Math.min(chain, 20));
			int multiplier = engine.field.colorClearExtraCount;
			if (engine.field.colorsCleared > 1)
				multiplier += (engine.field.colorsCleared-1)*2;
			
			if (chain > CHAIN_POWERS.length)
				multiplier += CHAIN_POWERS[CHAIN_POWERS.length-1];
			else
				multiplier += CHAIN_POWERS[chain-1];
			
			if (multiplier > 999)
				multiplier = 999;
			if (multiplier < 1)
				multiplier = 1;
			
			lastscore = pts;
			lastmultiplier = multiplier;
			scgettime = 120;
			int score = pts*multiplier;
			engine.statistics.scoreFromLineClear += score;
			engine.statistics.score += score;

			garbageAdd += ((avalanche*10*multiplier)+119)/120;
			
			setSpeed(engine);
		}
	}

	public boolean lineClearEnd(GameEngine engine, int playerID) {
		if (garbageAdd > 0)
		{
			garbageSent += garbageAdd;
			garbageAdd = 0;
		}
		
		int feverChainNow = feverChain;
		if (cleared)
		{
			boardsPlayed++;
			timeLimitAdd = 0;
			if (zenKeshi && timeLimit > 0)
			{
				if (timeLimit > 0)
					timeLimitAdd += 180;
				feverChain += 2;
				if (feverChain > feverChainMax)
					feverChain = feverChainMax;
			}
			if (timeLimit > 0)
			{
				int addTime = (engine.chain-2)*60;
				if (addTime > 0)
					timeLimitAdd += addTime;
				if (timeLimitAdd > 0)
				{
					timeLimit += timeLimitAdd;
					timeLimitAddDisplay = 120;
				}
			}
			int chainShort = feverChainNow - engine.chain;
			if (chainShort <= 0)
			{
				engine.playSE("cool");
				if (feverChain < feverChainMax)
					feverChain++;
			}
			else if(chainShort == 2)
			{
				engine.playSE("regret");
				feverChain--;
			}
			else if (chainShort > 2)
			{
				engine.playSE("regret");
				feverChain-=2;
			}
			if (feverChain < feverChainMin)
				feverChain = feverChainMin;
			if (timeLimit > 0)
				loadFeverMap(engine, playerID, feverChain);
		}
		else if (engine.field != null)
		{
			if (!engine.field.getBlockEmpty(2, 0) || !engine.field.getBlockEmpty(3, 0))
			{
				engine.stat = GameEngine.STAT_GAMEOVER;
				engine.gameActive = false;
				engine.resetStatc();
				engine.statc[1] = 1;
			}
		}

		//  time切れ
		if((timeLimit <= 0) && (engine.timerActive == true)) {
			engine.gameActive = false;
			engine.timerActive = false;
			engine.resetStatc();
			engine.stat = GameEngine.STAT_ENDINGSTART;
		}
		return false;
	}
	
	/*
	 * Render results screen
	 */
	@Override
	public void renderResult(GameEngine engine, int playerID) {
		receiver.drawMenuFont(engine, playerID,  0, 1, "PLAY DATA", EventReceiver.COLOR_ORANGE);

		receiver.drawMenuFont(engine, playerID,  0, 3, "SCORE", EventReceiver.COLOR_BLUE);
		String strScoreBefore = String.format("%10d", scoreBeforeBonus);
		receiver.drawMenuFont(engine, playerID,  0, 4, strScoreBefore, EventReceiver.COLOR_GREEN);

		receiver.drawMenuFont(engine, playerID,  0, 5, "ZENKESHI", EventReceiver.COLOR_BLUE);
		String strZenKeshi = String.format("%10d", zenKeshiCount);
		receiver.drawMenuFont(engine, playerID,  0, 6, strZenKeshi);
		String strZenKeshiBonus = "+" + zenKeshiBonus;
		receiver.drawMenuFont(engine, playerID, 10-strZenKeshiBonus.length(), 7, strZenKeshiBonus, EventReceiver.COLOR_GREEN);
		
		receiver.drawMenuFont(engine, playerID,  0, 8, "MAX CHAIN", EventReceiver.COLOR_BLUE);
		String strMaxChain = String.format("%10d", maxChain);
		receiver.drawMenuFont(engine, playerID,  0, 9, strMaxChain);
		String strMaxChainBonus = "+" + maxChainBonus;
		receiver.drawMenuFont(engine, playerID, 10-strMaxChainBonus.length(), 10, strMaxChainBonus, EventReceiver.COLOR_GREEN);

		receiver.drawMenuFont(engine, playerID,  0, 11, "TOTAL", EventReceiver.COLOR_BLUE);
		String strScore = String.format("%10d", engine.statistics.score);
		receiver.drawMenuFont(engine, playerID,  0, 12, strScore, EventReceiver.COLOR_RED);
		
		receiver.drawMenuFont(engine, playerID,  0, 13, "TIME", EventReceiver.COLOR_BLUE);
		String strTime = String.format("%10s", GeneralUtil.getTime(engine.statistics.time));
		receiver.drawMenuFont(engine, playerID,  0, 14, strTime);

		if(rankingRank != -1) {
			receiver.drawMenuFont(engine, playerID,  0, 15, "RANK", EventReceiver.COLOR_BLUE);
			String strRank = String.format("%10d", rankingRank + 1);
			receiver.drawMenuFont(engine, playerID,  0, 16, strRank);
		}
	}

	/*
	 * Called when saving replay
	 */
	@Override
	public void saveReplay(GameEngine engine, int playerID, CustomProperties prop) {
		saveSetting(prop);

		// Update rankings
		if((owner.replayMode == false) && (engine.ai == null)) {
			updateRanking(engine.statistics.score, engine.statistics.time, mapSet, numColors);

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
		mapSet = prop.getProperty("avalanchefever.gametype", 0);
		outlinetype = prop.getProperty("avalanchefever.outlinetype", 0);
		numColors = prop.getProperty("avalanchefever.numcolors", 4);
		version = prop.getProperty("avalanchefever.version", 0);
		chainDisplayType = prop.getProperty("avalanchefever.chainDisplayType", 1);
	}

	/**
	 * Save settings to property file
	 * @param prop Property file
	 */
	private void saveSetting(CustomProperties prop) {
		prop.setProperty("avalanchefever.gametype", mapSet);
		prop.setProperty("avalanchefever.outlinetype", outlinetype);
		prop.setProperty("avalanchefever.numcolors", numColors);
		prop.setProperty("avalanchefever.version", version);
		prop.setProperty("avalanchefever.chainDisplayType", chainDisplayType);
	}

	private void loadMapSetFever(GameEngine engine, int playerID, int id, boolean forceReload) {
		if((propFeverMap == null) || (forceReload)) {
			propFeverMap = receiver.loadProperties("config/map/avalanche/" +
					FEVER_MAPS[id] + "Endless.map");
			feverChainMin = propFeverMap.getProperty("minChain", 3);
			feverChainMax = propFeverMap.getProperty("maxChain", 15);
			String subsets = propFeverMap.getProperty("sets");
			mapSubsets = subsets.split(",");
		}
	}

	private void loadFeverMap(GameEngine engine, int playerID, int chain) {
		engine.createFieldIfNeeded();
		engine.field.reset();
		engine.field.stringToField(propFeverMap.getProperty(
				mapSubsets[engine.random.nextInt(mapSubsets.length)] +
				"." + numColors + "colors." + chain + "chain"));
		engine.field.setAllAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_LEFT, false);
		engine.field.setAllAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_DOWN, false);
		engine.field.setAllAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_UP, false);
		engine.field.setAllAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT, false);
		engine.field.setAllAttribute(Block.BLOCK_ATTRIBUTE_GARBAGE, false);
		engine.field.setAllAttribute(Block.BLOCK_ATTRIBUTE_ANTIGRAVITY, false);
		engine.field.setAllSkin(engine.getSkin());
		engine.field.shuffleColors(BLOCK_COLORS, numColors, engine.random);
	}
	
	/**
	 * Read rankings from property file
	 * @param prop Property file
	 * @param ruleName Rule name
	 */
	private void loadRanking(CustomProperties prop, String ruleName) {
		for(int i = 0; i < RANKING_MAX; i++) {
			for(int j = 0; j < FEVER_MAPS.length; j++) {
				for(int colors = 3; colors <= 5; colors++) {
					rankingScore[colors-3][j][i] = prop.getProperty("avalanchefever.ranking." + ruleName + "." + colors +
							"colors." + FEVER_MAPS[j] + ".score." + i, 0);
					rankingTime[colors-3][j][i] = prop.getProperty("avalanchefever.ranking." + ruleName + "." + colors +
							"colors." + FEVER_MAPS[j] + ".time." + i, -1);
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
			for(int j = 0; j < FEVER_MAPS.length; j++) {
				for(int colors = 3; colors <= 5; colors++) {
					prop.setProperty("avalanchefever.ranking." + ruleName + "." + colors +
							"colors." + FEVER_MAPS[j] + ".score." + i, rankingScore[colors-3][j][i]);
					prop.setProperty("avalanchefever.ranking." + ruleName + "." + colors +
							"colors." + FEVER_MAPS[j] + ".time." + i, rankingTime[colors-3][j][i]);
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
	private void updateRanking(int sc, int time, int type, int colors) {
		rankingRank = checkRanking(sc, time, type, colors);

		if(rankingRank != -1) {
			// Shift down ranking entries
			for(int i = RANKING_MAX - 1; i > rankingRank; i--) {
				rankingScore[colors-3][type][i] = rankingScore[colors-3][type][i - 1];
				rankingTime[colors-3][type][i] = rankingTime[colors-3][type][i - 1];
			}

			// Add new data
			rankingScore[colors-3][type][rankingRank] = sc;
			rankingTime[colors-3][type][rankingRank] = time;
		}
	}

	/**
	 * Calculate ranking position
	 * @param sc Score
	 * @param time Time
	 * @return Position (-1 if unranked)
	 */
	private int checkRanking(int sc, int time, int type, int colors) {
		for(int i = 0; i < RANKING_MAX; i++) {
			if(sc > rankingScore[colors-3][type][i]) {
				return i;
			} else if((sc == rankingScore[colors-3][type][i]) && (time < rankingTime[colors-3][type][i])) {
				return i;
			}
		}

		return -1;
	}
}
