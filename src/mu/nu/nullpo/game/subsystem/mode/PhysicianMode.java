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
import mu.nu.nullpo.gui.slick.GameKey;
import mu.nu.nullpo.util.CustomProperties;
import mu.nu.nullpo.util.GeneralUtil;

/**
 * PHYSICIAN mode (beta)
 */
public class PhysicianMode extends DummyMode {
	/** Current version */
	private static final int CURRENT_VERSION = 0;

	/** Enabled piece types */
	private static final int[] PIECE_ENABLE = {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0};

	/** Block colors */
	private static final int[] BLOCK_COLORS =
	{
		Block.BLOCK_COLOR_RED,
		Block.BLOCK_COLOR_BLUE,
		Block.BLOCK_COLOR_YELLOW
	};
	/** Hovering block colors */
	private static final int[] HOVER_BLOCK_COLORS =
	{
		Block.BLOCK_COLOR_GEM_RED,
		Block.BLOCK_COLOR_GEM_BLUE,
		Block.BLOCK_COLOR_GEM_YELLOW
	};
	private static final int[] BASE_SPEEDS = {10, 20, 25};

	/** Number of ranking records */
	private static final int RANKING_MAX = 10;

	/** Names of speed settings */
	private static final String[] SPEED_NAME = {"LOW", "MED", "HI"};

	/** Colors for speed settings */
	private static final int[] SPEED_COLOR =
	{
		EventReceiver.COLOR_BLUE,
		EventReceiver.COLOR_YELLOW,
		EventReceiver.COLOR_RED
	};

	/** GameManager object (Manages entire game status) */
	private GameManager owner;

	/** EventReceiver object (This receives many game events, can also be used for drawing the fonts.) */
	private EventReceiver receiver;

	/** Amount of points you just get from line clears */
	private int lastscore;

	/** Elapsed time from last line clear (lastscore is displayed to screen until this reaches to 120) */
	private int scgettime;

	/** Version number */
	private int version;

	/** Current round's ranking rank */
	private int rankingRank;

	/** Rankings' line counts */
	private int[] rankingScore;

	/** Rankings' times */
	private int[] rankingTime;

	/** Number of initial gem blocks */
	private int hoverBlocks;

	/** Speed mode */
	private int speed;

	/** Number gem blocks cleared in current chain */
	private int gemsClearedChainTotal;

	/*
	 * Mode name
	 */
	@Override
	public String getName() {
		return "PHYSICIAN (RC1)";
	}

	/*
	 * Game style
	 */
	@Override
	public int getGameStyle() {
		return GameEngine.GAMESTYLE_PHYSICIAN;
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
		gemsClearedChainTotal = 0;

		rankingRank = -1;
		rankingScore = new int[RANKING_MAX];
		rankingTime = new int[RANKING_MAX];

		if(owner.replayMode == false) {
			loadSetting(owner.modeConfig);
			loadRanking(owner.modeConfig, engine.ruleopt.strRuleName);
			version = CURRENT_VERSION;
		} else {
			loadSetting(owner.replayProp);
		}

		engine.framecolor = GameEngine.FRAME_COLOR_PURPLE;
		engine.clearMode = GameEngine.CLEAR_LINE_COLOR;
		engine.garbageColorClear = false;
		engine.colorClearSize = 4;
		engine.lineGravityType = GameEngine.LINE_GRAVITY_CASCADE;
		for(int i = 0; i < Piece.PIECE_COUNT; i++)
			engine.nextPieceEnable[i] = (PIECE_ENABLE[i] == 1);
		engine.randomBlockColor = true;
		engine.blockColors = BLOCK_COLORS;
		engine.connectBlocks = true;
		engine.cascadeDelay = 18;
		engine.gemSameColor = true;
	}

	/**
	 * Set the gravity rate
	 * @param engine GameEngine
	 */
	public void setSpeed(GameEngine engine) {
		engine.speed.gravity = BASE_SPEEDS[speed]*(10+(engine.statistics.totalPieceLocked/10));
		engine.speed.denominator = 3600;
	}

	/*
	 * Called at settings screen
	 */
	@Override
	public boolean onSetting(GameEngine engine, int playerID) {
		// Menu
		if(engine.owner.replayMode == false) {
			// Configuration changes
			int change = updateCursor(engine, 1);

			int m = 1;
			if(engine.ctrl.isPress(Controller.BUTTON_E)) m = 100;
			if(engine.ctrl.isPress(Controller.BUTTON_F)) m = 1000;

			if(change != 0) {
				engine.playSE("change");

				switch(engine.statc[2]) {

				case 0:
					if (m >= 10) hoverBlocks += change*10;
					else hoverBlocks += change;
					if(hoverBlocks < 1) hoverBlocks = 99;
					if(hoverBlocks > 99) hoverBlocks = 1;
					break;
				case 1:
					speed += change;
					if(speed < 0) speed = 2;
					if(speed > 2) speed = 0;
				}
			}

			// 決定
			if(GameKey.gamekey[playerID].isPushKey(GameKey.BUTTON_NAV_SELECT) && (engine.statc[3] >= 5)) {
				engine.playSE("decide");
				saveSetting(owner.modeConfig);
				receiver.saveModeConfig(owner.modeConfig);
				return false;
			}

			// Cancel
			if(GameKey.gamekey[playerID].isPushKey(GameKey.BUTTON_NAV_CANCEL)) {
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
		drawMenu(engine, playerID, receiver, 0, EventReceiver.COLOR_BLUE, 0,
				"GEMS", String.valueOf(hoverBlocks),
				"SPEED", SPEED_NAME[speed]);
	}

	/*
	 * Called for initialization during "Ready" screen
	 */
	@Override
	public void startGame(GameEngine engine, int playerID) {
		engine.comboType = GameEngine.COMBO_TYPE_DISABLE;

		engine.speed.are = 30;
		engine.speed.areLine = 30;
		engine.speed.das = 10;
		engine.speed.lockDelay = 30;

		setSpeed(engine);
	}

	@Override
	public void renderLast(GameEngine engine, int playerID) {
		receiver.drawScoreFont(engine, playerID, 0, 0, "PHYSICIAN", EventReceiver.COLOR_DARKBLUE);

		if( (engine.stat == GameEngine.STAT_SETTING) || ((engine.stat == GameEngine.STAT_RESULT) && (owner.replayMode == false)) ) {
			if((owner.replayMode == false) && (engine.ai == null)) {
				receiver.drawScoreFont(engine, playerID, 3, 3, "SCORE  TIME", EventReceiver.COLOR_BLUE);
				for(int i = 0; i < RANKING_MAX; i++) {
					receiver.drawScoreFont(engine, playerID, 0, 4 + i, String.format("%2d", i + 1), EventReceiver.COLOR_YELLOW);
					receiver.drawScoreFont(engine, playerID, 3, 4 + i, String.valueOf(rankingScore[i]), (i == rankingRank));
					receiver.drawScoreFont(engine, playerID, 10, 4 + i, GeneralUtil.getTime(rankingTime[i]), (i == rankingRank));
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

			receiver.drawScoreFont(engine, playerID, 0, 6, "REST", EventReceiver.COLOR_BLUE);
			if (engine.field != null)
			{
				receiver.drawScoreFont(engine, playerID, 0, 7, String.valueOf(engine.field.getHowManyGems()));
				int red = 0, yellow = 0, blue = 0;
				for (int y = 0; y < engine.field.getHeight(); y++)
					for (int x = 0; x < engine.field.getWidth(); x++)
					{
						int blockColor = engine.field.getBlockColor(x, y);
						if (blockColor == Block.BLOCK_COLOR_GEM_BLUE)
							blue++;
						else if (blockColor == Block.BLOCK_COLOR_GEM_RED)
							red++;
						else if (blockColor == Block.BLOCK_COLOR_GEM_YELLOW)
							yellow++;
					}
				receiver.drawScoreFont(engine, playerID, 0, 8, "(");
				receiver.drawScoreFont(engine, playerID, 1, 8, String.format("%2d", red), EventReceiver.COLOR_RED);
				receiver.drawScoreFont(engine, playerID, 4, 8, String.format("%2d", yellow), EventReceiver.COLOR_YELLOW);
				receiver.drawScoreFont(engine, playerID, 7, 8, String.format("%2d", blue), EventReceiver.COLOR_BLUE);
				receiver.drawScoreFont(engine, playerID, 9, 8, ")");
			}

			receiver.drawScoreFont(engine, playerID, 0, 10, "SPEED", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 0, 11, SPEED_NAME[speed], SPEED_COLOR[speed]);

			receiver.drawScoreFont(engine, playerID, 0, 13, "TIME", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 0, 14, GeneralUtil.getTime(engine.statistics.time));
		}
	}

	/*
	 * Ready画面の処理
	 */
	@Override
	public boolean onReady(GameEngine engine, int playerID) {
		if(hoverBlocks > 0 && engine.statc[0] == 0)
		{
			engine.createFieldIfNeeded();
			int minY = 6;
			if (hoverBlocks >= 80) minY = 3;
			else if (hoverBlocks >= 72) minY = 4;
			else if (hoverBlocks >= 64) minY = 5;
			engine.field.addRandomHoverBlocks(engine, hoverBlocks, HOVER_BLOCK_COLORS, minY, true);
			engine.blockOutlineType = GameEngine.BLOCK_OUTLINE_CONNECT;
		}
		return false;
	}

	/*
	 * Called after every frame
	 */
	@Override
	public void onLast(GameEngine engine, int playerID) {
		if (scgettime > 0) scgettime--;

		if (engine.field == null)
			return;

		int rest = engine.field.getHowManyGems();
		engine.meterValue = (rest * receiver.getMeterMax(engine)) / hoverBlocks;
		if (rest <= 3) engine.meterColor = GameEngine.METER_COLOR_GREEN;
		else if (rest < (hoverBlocks >> 2)) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
		else if (rest < (hoverBlocks >> 1)) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
		else engine.meterColor = GameEngine.METER_COLOR_RED;

		if(rest == 0 && engine.timerActive) {
			engine.gameActive = false;
			engine.timerActive = false;
			engine.resetStatc();
			engine.stat = GameEngine.STAT_EXCELLENT;
		}
	}

	/*
	 * Calculate score
	 */
	@Override
	public void calcScore(GameEngine engine, int playerID, int lines) {
		int gemsCleared = engine.field.gemsCleared;
		if (gemsCleared > 0 && lines > 0) {
			int pts = 0;
			while (gemsCleared > 0 && gemsClearedChainTotal < 5)
			{
				pts += 1 << gemsClearedChainTotal;
				gemsClearedChainTotal++;
				gemsCleared--;
			}
			if (gemsClearedChainTotal >= 5)
				pts += gemsCleared << 5;
			pts *= (speed+1) * 100;
			gemsClearedChainTotal += gemsCleared;
			lastscore = pts;
			scgettime = 120;
			engine.statistics.scoreFromLineClear += pts;
			engine.statistics.score += pts;
			engine.playSE("gem");
			setSpeed(engine);
		}
	}

	@Override
	public boolean lineClearEnd(GameEngine engine, int playerID) {
		gemsClearedChainTotal = 0;
		return false;
	}

	/*
	 * Render results screen
	 */
	@Override
	public void renderResult(GameEngine engine, int playerID) {
		receiver.drawMenuFont(engine, playerID,  0, 1, "PLAY DATA", EventReceiver.COLOR_ORANGE);

		drawResult(engine, playerID, receiver, 3, EventReceiver.COLOR_BLUE,
				"SCORE", String.format("%10d", engine.statistics.score),
				"CLEARED", String.format("%10d", engine.statistics.lines),
				"TIME", String.format("%10s", GeneralUtil.getTime(engine.statistics.time)));
		drawResultRank(engine, playerID, receiver, 9, EventReceiver.COLOR_BLUE, rankingRank);
	}

	/*
	 * Called when saving replay
	 */
	@Override
	public void saveReplay(GameEngine engine, int playerID, CustomProperties prop) {
		saveSetting(prop);

		// Update rankings
		if((owner.replayMode == false) && (engine.ai == null)) {
			updateRanking(engine.statistics.score, engine.statistics.time);

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
		hoverBlocks = prop.getProperty("physician.hoverBlocks", 40);
		speed = prop.getProperty("physician.speed", 1);
		version = prop.getProperty("physician.version", 0);
	}

	/**
	 * Save settings to property file
	 * @param prop Property file
	 */
	private void saveSetting(CustomProperties prop) {
		prop.setProperty("physician.hoverBlocks", hoverBlocks);
		prop.setProperty("physician.speed", speed);
		prop.setProperty("physician.version", version);
	}

	/**
	 * Read rankings from property file
	 * @param prop Property file
	 * @param ruleName Rule name
	 */
	private void loadRanking(CustomProperties prop, String ruleName) {
		for(int i = 0; i < RANKING_MAX; i++) {
			rankingScore[i] = prop.getProperty("physician.ranking." + ruleName + ".score." + i, 0);
			rankingTime[i] = prop.getProperty("physician.ranking." + ruleName + ".time." + i, -1);
		}
	}

	/**
	 * Save rankings to property file
	 * @param prop Property file
	 * @param ruleName Rule name
	 */
	private void saveRanking(CustomProperties prop, String ruleName) {
		for(int i = 0; i < RANKING_MAX; i++) {
			prop.setProperty("physician.ranking." + ruleName + ".score." + i, rankingScore[i]);
			prop.setProperty("physician.ranking." + ruleName + ".time." + i, rankingTime[i]);
		}
	}

	/**
	 * Update rankings
	 * @param sc Score
	 * @param li Lines
	 * @param time Time
	 */
	private void updateRanking(int sc, int time) {
		rankingRank = checkRanking(sc, time);

		if(rankingRank != -1) {
			// Shift down ranking entries
			for(int i = RANKING_MAX - 1; i > rankingRank; i--) {
				rankingScore[i] = rankingScore[i - 1];
				rankingTime[i] = rankingTime[i - 1];
			}

			// Add new data
			rankingScore[rankingRank] = sc;
			rankingTime[rankingRank] = time;
		}
	}

	/**
	 * Calculate ranking position
	 * @param sc Score
	 * @param time Time
	 * @return Position (-1 if unranked)
	 */
	private int checkRanking(int sc, int time) {
		for(int i = 0; i < RANKING_MAX; i++) {
			if(sc > rankingScore[i]) {
				return i;
			} else if((sc == rankingScore[i]) && (time < rankingTime[i])) {
				return i;
			}
		}

		return -1;
	}
}
