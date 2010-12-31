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
import mu.nu.nullpo.util.CustomProperties;
import mu.nu.nullpo.util.GeneralUtil;

/**
 * AVALANCHE FEVER MARATHON mode (Release Candidate 2)
 */
public class AvalancheFeverMode extends Avalanche1PDummyMode {
	/** Current version */
	private static final int CURRENT_VERSION = 0;

	private static final int[] CHAIN_POWERS =
	{
		4, 10, 18, 22, 30, 48, 80, 120, 160, 240, 280, 288, 342, 400, 440, 480, 520, 560, 600, 640, 680, 720, 760, 800 //Amitie
	};

	/** Names of chain display settings */
	private static final String[] CHAIN_DISPLAY_NAMES = {"OFF", "YELLOW", "SIZE"};

	/** Number of ranking records */
	private static final int RANKING_MAX = 10;

	/** Time limit */
	private static final int TIME_LIMIT = 3600;

	/** Selected game type */
	private int mapSet;

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

	/** List of subsets in selected map */
	private String[] mapSubsets;

	/** Fever chain count when last chain hit occurred */
	private int feverChainDisplay;

	/** Type of chain display */
	private int chainDisplayType;

	/** Number of boards played */
	private int boardsPlayed;

	/** Level at start of chain */
	private int chainLevelMultiplier;

	/** Names of fast-fowards settings */
	private static final String[] FAST_NAMES = {"OFF", "CLEAR", "ALL"};

	/** Fast-forward settings for debug use */
	private int fastenable;

	/** Flag set when fast-forward is enabled */
	private boolean fastinuse;

	/** Indices for map previews */
	private int previewChain, previewSubset;

	/** ??? */
	private int xyzzy;

	/*
	 * Mode name
	 */
	@Override
	public String getName() {
		return "AVALANCHE 1P FEVER MARATHON (RC2)";
	}

	/*
	 * Initialization
	 */
	@Override
	public void playerInit(GameEngine engine, int playerID) {
		super.playerInit(engine, playerID);

		cleared = false;
		boardsPlayed = 0;

		timeLimit = TIME_LIMIT;
		timeLimitAdd = 0;
		timeLimitAddDisplay = 0;

		feverChainDisplay = 0;
		chainDisplayType = 0;

		feverChain = 5;

		rankingRank = -1;
		rankingScore = new int[3][FEVER_MAPS.length][RANKING_MAX];
		rankingTime = new int[3][FEVER_MAPS.length][RANKING_MAX];

		xyzzy = 0;
		fastenable = 0;
		fastinuse = false;
		previewChain = 5;
		previewSubset = 0;

		if(owner.replayMode == false) {
			loadSetting(owner.modeConfig);
			loadRanking(owner.modeConfig, engine.ruleopt.strRuleName);
			version = CURRENT_VERSION;
		} else {
			loadSetting(owner.replayProp);
		}
	}

	public boolean readyInit (GameEngine engine, int playerID) {
		cascadeSlow = true;
		super.readyInit(engine, playerID);
		loadMapSetFever(engine, playerID, mapSet, true);
		loadFeverMap(engine, playerID, feverChain);
		timeLimit = TIME_LIMIT;
		timeLimitAdd = 0;
		timeLimitAddDisplay = 0;
		chainLevelMultiplier = level;
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
			int change = updateCursor(engine, (xyzzy == 573) ? 7 : 4);

			if(change != 0) {
				engine.playSE("change");

				switch(engine.statc[2]) {

				case 0:
					mapSet += change;
					if(mapSet < 0) mapSet = FEVER_MAPS.length - 1;
					if(mapSet > FEVER_MAPS.length - 1) mapSet = 0;
					if (xyzzy == 573) loadMapSetFever(engine, playerID, mapSet, true);
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
				case 4:
					bigDisplay = !bigDisplay;
					break;
				case 5:
					fastenable += change;
					if(fastenable < 0) fastenable = 2;
					if(fastenable > 2) fastenable = 0;
					break;
				case 6:
					previewSubset += change;
					if(previewSubset < 0) previewSubset = mapSubsets.length-1;
					if(previewSubset >= mapSubsets.length) previewSubset = 0;
					break;
				case 7:
					previewChain += change;
					if(previewChain < feverChainMin) previewChain = feverChainMax;
					if(previewChain > feverChainMax) previewChain = feverChainMin;
					break;
				}
				if (mapSet == 4) numColors = 3;
			}

			if (xyzzy != 573) {
				if (engine.ctrl.isPush(Controller.BUTTON_UP)) {
					if (xyzzy == 1)
						xyzzy++;
					else if (xyzzy != 2)
						xyzzy = 1;
				}
				if (engine.ctrl.isPush(Controller.BUTTON_DOWN)) {
					if (xyzzy == 2 || xyzzy == 3)
						xyzzy++;
					else
						xyzzy = 0;
				}
				if (engine.ctrl.isPush(Controller.BUTTON_LEFT)) {
					if (xyzzy == 4 || xyzzy == 6)
						xyzzy++;
					else
						xyzzy = 0;
				}
				if (engine.ctrl.isPush(Controller.BUTTON_RIGHT)) {
					if (xyzzy == 5 || xyzzy == 7)
						xyzzy++;
					else
						xyzzy = 0;
				}
			}

			if (engine.ctrl.isPush(Controller.BUTTON_A)) {
				if ((xyzzy == 573) && engine.statc[2] > 5) {
					loadMapSetFever(engine, playerID, mapSet, true);
					loadFeverMap(engine, playerID, previewChain, previewSubset);
				} else if (xyzzy == 9) {
					engine.playSE("levelup");
					xyzzy = 573;
					loadMapSetFever(engine, playerID, mapSet, true);
				} else if (engine.statc[3] >= 5) {
					// 決定
					engine.playSE("decide");
					saveSetting(owner.modeConfig);
					receiver.saveModeConfig(owner.modeConfig);
					return false;
				}
			}

			if((engine.ctrl.isPush(Controller.BUTTON_B))) {
				if (xyzzy == 8)
					xyzzy++;
				else {
					// Cancel
					engine.quitflag = true;
				}
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
	 * When the piece is movable
	 */
	@Override
	public void renderMove(GameEngine engine, int playerID) {
		if(engine.gameStarted) {
			drawXorTimer(engine, playerID);
		}
	}

	/*
	 * Render the settings screen
	 */
	@Override
	public void renderSetting(GameEngine engine, int playerID) {
		if (engine.statc[2] <= 5) {
			String strOutline = "";
			if(outlinetype == 0) strOutline = "NORMAL";
			if(outlinetype == 1) strOutline = "COLOR";
			if(outlinetype == 2) strOutline = "NONE";

			initMenu(0, EventReceiver.COLOR_BLUE, 0);
			drawMenu(engine, playerID, receiver,
					"MAP SET", FEVER_MAPS[mapSet].toUpperCase(),
					"OUTLINE", strOutline,
					"COLORS", String.valueOf(numColors),
					"SHOW CHAIN", CHAIN_DISPLAY_NAMES[chainDisplayType],
					"BIG DISP", GeneralUtil.getONorOFF(bigDisplay));
			if (xyzzy == 573)
				drawMenu(engine, playerID, receiver, "FAST", FAST_NAMES[fastenable]);
		} else {
			receiver.drawMenuFont(engine, playerID, 0, 13, "MAP PREVIEW", EventReceiver.COLOR_YELLOW);
			receiver.drawMenuFont(engine, playerID, 0, 14, "A:DISPLAY", EventReceiver.COLOR_GREEN);
			drawMenu(engine, playerID, receiver, 15, EventReceiver.COLOR_BLUE, 6,
					"SUBSET", mapSubsets[previewSubset].toUpperCase(),
					"CHAIN", String.valueOf(previewChain));
		}
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
				float scale = (receiver.getNextDisplayType() == 2) ? 0.5f : 1.0f;
				int topY = (receiver.getNextDisplayType() == 2) ? 6 : 4;

				receiver.drawScoreFont(engine, playerID, 3, topY-1, "SCORE      TIME", EventReceiver.COLOR_BLUE, scale);

				for(int i = 0; i < RANKING_MAX; i++) {
					receiver.drawScoreFont(engine, playerID, 0, topY+i, String.format("%2d", i + 1), EventReceiver.COLOR_YELLOW, scale);
					receiver.drawScoreFont(engine, playerID, 3, topY+i, String.valueOf(rankingScore[numColors-3][mapSet][i]), (i == rankingRank), scale);
					receiver.drawScoreFont(engine, playerID, 14, topY+i, GeneralUtil.getTime(rankingTime[numColors-3][mapSet][i]), (i == rankingRank), scale);
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

			receiver.drawScoreFont(engine, playerID, 11, 6, "BOARDS", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 11, 7, String.valueOf(boardsPlayed));

			receiver.drawScoreFont(engine, playerID, 11, 9, "ZENKESHI", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 11, 10, String.valueOf(zenKeshiCount));

			receiver.drawScoreFont(engine, playerID, 11, 12, "MAX CHAIN", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 11, 13, String.valueOf(engine.statistics.maxChain));

			receiver.drawScoreFont(engine, playerID, 11, 15, "OJAMA SENT", EventReceiver.COLOR_BLUE);
			String strSent = String.valueOf(garbageSent);
			if(garbageAdd > 0) {
				strSent = strSent + "(+" + String.valueOf(garbageAdd)+ ")";
			}
			receiver.drawScoreFont(engine, playerID, 11, 16, strSent);

			receiver.drawScoreFont(engine, playerID, 11, 18, "CLEARED", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 11, 19, String.valueOf(blocksCleared));

			if(engine.gameStarted && (engine.stat != GameEngine.STAT_MOVE) && (engine.stat != GameEngine.STAT_RESULT)) {
				drawXorTimer(engine, playerID);
			}

			if (!engine.gameActive)
				return;

			int textHeight = 13;
			if (engine.field != null)
				textHeight = engine.field.getHeight()+1;
			if(engine.displaysize == 1)
				textHeight = 11;

			int baseX = (engine.displaysize == 1) ? 1 : 0;
			if (engine.chain > 0 && chainDisplay > 0 && chainDisplayType != 0)
			{
				int color = EventReceiver.COLOR_YELLOW;
				if (chainDisplayType == 2)
				{
					if (engine.chain >= feverChainDisplay)
						color = EventReceiver.COLOR_GREEN;
					else if (engine.chain == feverChainDisplay-2)
						color = EventReceiver.COLOR_ORANGE;
					else if (engine.chain < feverChainDisplay-2)
						color = EventReceiver.COLOR_RED;
				}
				receiver.drawMenuFont(engine, playerID, baseX + (engine.chain > 9 ? 0 : 1), textHeight, engine.chain + " CHAIN!", color);
			}
			if (zenKeshiDisplay > 0)
				receiver.drawMenuFont(engine, playerID, baseX, textHeight+1, "ZENKESHI!", EventReceiver.COLOR_YELLOW);
		}
	}

	/**
	 * Draw fever timer on death columns
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	protected void drawXorTimer(GameEngine engine, int playerID) {
		String strFeverTimer = String.format("%02d",(timeLimit+59)/60);

		for(int i = 0; i < 2; i++) {
			if((engine.field == null) || (engine.field.getBlockEmpty(2 + i, 0))) {
				if(engine.displaysize == 1) {
					receiver.drawMenuFont(engine, playerID, 4 + (i * 2), 0, ""+strFeverTimer.charAt(i),
							timeLimit < 360 ? EventReceiver.COLOR_RED : EventReceiver.COLOR_WHITE, 2.0f);
				} else {
					receiver.drawMenuFont(engine, playerID, 2 + i, 0, ""+strFeverTimer.charAt(i),
							timeLimit < 360 ? EventReceiver.COLOR_RED : EventReceiver.COLOR_WHITE);
				}
			}
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
				timeLimit--;
				if((timeLimit > 0) && (timeLimit <= 360) && (timeLimit % 60 == 0))
					engine.playSE("countdown");
				else if (timeLimit == 0)
					engine.playSE("levelstop");
			}
		}
		if (timeLimitAddDisplay > 0)
			timeLimitAddDisplay--;

		// Time meter
		engine.meterValue = (timeLimit * receiver.getMeterMax(engine)) / TIME_LIMIT;
		engine.meterColor = GameEngine.METER_COLOR_GREEN;
		if(timeLimit <= 1800) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
		if(timeLimit <= 900) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
		if(timeLimit <= 300) engine.meterColor = GameEngine.METER_COLOR_RED;

		if (!fastinuse && engine.ctrl.isPress(Controller.BUTTON_F) &&
				((fastenable == 2) || (engine.stat == GameEngine.STAT_LINECLEAR && fastenable == 1))) {
			fastinuse = true;
			for (int i = 0; i < 4; i++) engine.owner.updateAll();
			fastinuse = false;
		}
	}

	protected int calcOjama(int score, int avalanche, int pts, int multiplier)
	{
		return ((avalanche*10*multiplier)+ojamaRate-1)/ojamaRate;
	}

	protected int calcPts (int avalanche) {
		return avalanche*chainLevelMultiplier*10;
	}

	protected int calcChainMultiplier(int chain) {
		if (chain > CHAIN_POWERS.length)
			return CHAIN_POWERS[CHAIN_POWERS.length-1];
		else
			return CHAIN_POWERS[chain-1];
	}

	protected void onClear (GameEngine engine, int playerID) {
		chainDisplay = 60;
		cleared = true;
		feverChainDisplay = feverChain;
		if (engine.chain == 1)
			chainLevelMultiplier = level;
	}

	public boolean lineClearEnd(GameEngine engine, int playerID) {
		if (garbageAdd > 0)
		{
			garbageSent += garbageAdd;
			garbageAdd = 0;
		}

		if (cleared)
		{
			boardsPlayed++;
			timeLimitAdd = 0;
			int newFeverChain = Math.max(engine.chain+1, feverChain-2);
			if (zenKeshi)
			{
				timeLimitAdd += 180;
				zenKeshiDisplay = 120;
				newFeverChain += 2;
			}
			if (newFeverChain < feverChainMin)
				newFeverChain = feverChainMin;
			if (newFeverChain > feverChainMax)
				newFeverChain = feverChainMax;
			if (newFeverChain > feverChain)
				engine.playSE("cool");
			else if (newFeverChain < feverChain)
				engine.playSE("regret");
			feverChain = newFeverChain;
			if (timeLimit > 0)
			{
				timeLimitAdd += Math.max(0, (engine.chain-2)*60);
				if (timeLimitAdd > 0)
				{
					timeLimit += timeLimitAdd;
					timeLimitAddDisplay = 120;
				}
				loadFeverMap(engine, playerID, feverChain);
			}
		}
		else if (engine.field != null)
		{
			if (!engine.field.getBlockEmpty(2, 0) || !engine.field.getBlockEmpty(3, 0))
			{
				engine.stat = GameEngine.STAT_GAMEOVER;
				engine.gameEnded();
				engine.resetStatc();
				engine.statc[1] = 1;
			}
		}

		// Out of time
		if((timeLimit <= 0) && (engine.timerActive == true)) {
			engine.gameEnded();
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
		String strMaxChain = String.format("%10d", engine.statistics.maxChain);
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
		bigDisplay = prop.getProperty("avalanchefever.bigDisplay", false);
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
		prop.setProperty("avalanchefever.bigDisplay", bigDisplay);
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
		loadFeverMap(engine, playerID, chain, engine.random.nextInt(mapSubsets.length));
	}

	private void loadFeverMap(GameEngine engine, int playerID, int chain, int subset) {
		engine.createFieldIfNeeded();
		engine.field.reset();
		engine.field.stringToField(propFeverMap.getProperty(mapSubsets[subset] +
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
