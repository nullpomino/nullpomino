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
import mu.nu.nullpo.game.component.Block;
import mu.nu.nullpo.game.component.Controller;
import mu.nu.nullpo.game.event.EventReceiver;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.util.CustomProperties;
import mu.nu.nullpo.util.GeneralUtil;

/**
 * DIG RACEMode
 */
public class DigRaceMode extends DummyMode {
	/** Current version */
	private static final int CURRENT_VERSION = 1;

	/** Number of entries in rankings */
	private static final int RANKING_MAX = 10;

	/** 邪魔Linescount type */
	private static final int GOALTYPE_MAX = 3;

	/** 邪魔Linescountの定count */
	private static final int[] GOAL_TABLE = {5, 10, 18};

	/** GameManager that owns this mode */
	private GameManager owner;

	/** Drawing and event handling EventReceiver */
	private EventReceiver receiver;

	/** BGM number */
	private int bgmno;

	/** Big */
	private boolean big;

	/** 邪魔Linescount type (0=5,1=10,2=18) */
	private int goaltype;

	/** Current version */
	private int version;

	/** Last preset number used */
	private int presetNumber;

	/** Current round's ranking rank */
	private int rankingRank;

	/** Rankings' times */
	private int[][] rankingTime;

	/** Rankings' line counts */
	private int[][] rankingLines;

	/** Rankings' piece counts */
	private int[][] rankingPiece;

	/*
	 * Mode name
	 */
	@Override
	public String getName() {
		return "DIG RACE";
	}

	/*
	 * Initialization
	 */
	@Override
	public void playerInit(GameEngine engine, int playerID) {
		owner = engine.owner;
		receiver = engine.owner.receiver;

		bgmno = 0;
		big = false;
		goaltype = 0;
		presetNumber = 0;

		rankingRank = -1;
		rankingTime = new int[GOALTYPE_MAX][RANKING_MAX];
		rankingLines = new int[GOALTYPE_MAX][RANKING_MAX];
		rankingPiece = new int[GOALTYPE_MAX][RANKING_MAX];

		engine.framecolor = GameEngine.FRAME_COLOR_GREEN;

		if(engine.owner.replayMode == false) {
			version = CURRENT_VERSION;
			presetNumber = engine.owner.modeConfig.getProperty("digrace.presetNumber", 0);
			loadPreset(engine, engine.owner.modeConfig, -1);
			loadRanking(owner.modeConfig, engine.ruleopt.strRuleName);
		} else {
			version = engine.owner.replayProp.getProperty("digrace.version", 0);
			presetNumber = 0;
			loadPreset(engine, engine.owner.replayProp, -1);
		}
	}

	/**
	 * Presetを読み込み
	 * @param engine GameEngine
	 * @param prop Property file to read from
	 * @param preset Preset number
	 */
	private void loadPreset(GameEngine engine, CustomProperties prop, int preset) {
		engine.speed.gravity = prop.getProperty("digrace.gravity." + preset, 4);
		engine.speed.denominator = prop.getProperty("digrace.denominator." + preset, 256);
		engine.speed.are = prop.getProperty("digrace.are." + preset, 24);
		engine.speed.areLine = prop.getProperty("digrace.areLine." + preset, 24);
		engine.speed.lineDelay = prop.getProperty("digrace.lineDelay." + preset, 40);
		engine.speed.lockDelay = prop.getProperty("digrace.lockDelay." + preset, 30);
		engine.speed.das = prop.getProperty("digrace.das." + preset, 14);
		bgmno = prop.getProperty("digrace.bgmno." + preset, 0);
		big = prop.getProperty("digrace.big." + preset, false);
		goaltype = prop.getProperty("digrace.goaltype." + preset, 1);
	}

	/**
	 * Presetを保存
	 * @param engine GameEngine
	 * @param prop Property file to save to
	 * @param preset Preset number
	 */
	private void savePreset(GameEngine engine, CustomProperties prop, int preset) {
		prop.setProperty("digrace.gravity." + preset, engine.speed.gravity);
		prop.setProperty("digrace.denominator." + preset, engine.speed.denominator);
		prop.setProperty("digrace.are." + preset, engine.speed.are);
		prop.setProperty("digrace.areLine." + preset, engine.speed.areLine);
		prop.setProperty("digrace.lineDelay." + preset, engine.speed.lineDelay);
		prop.setProperty("digrace.lockDelay." + preset, engine.speed.lockDelay);
		prop.setProperty("digrace.das." + preset, engine.speed.das);
		prop.setProperty("digrace.bgmno." + preset, bgmno);
		prop.setProperty("digrace.big." + preset, big);
		prop.setProperty("digrace.goaltype." + preset, goaltype);
	}

	/*
	 * Called at settings screen
	 */
	@Override
	public boolean onSetting(GameEngine engine, int playerID) {
		// Menu
		if(engine.owner.replayMode == false) {
			// Configuration changes
			int change = updateCursor(engine, 10);

			if(change != 0) {
				engine.playSE("change");

				int m = 1;
				if(engine.ctrl.isPress(Controller.BUTTON_E)) m = 100;
				if(engine.ctrl.isPress(Controller.BUTTON_F)) m = 1000;

				switch(engine.statc[2]) {
				case 0:
					engine.speed.gravity += change * m;
					if(engine.speed.gravity < -1) engine.speed.gravity = 99999;
					if(engine.speed.gravity > 99999) engine.speed.gravity = -1;
					break;
				case 1:
					engine.speed.denominator += change * m;
					if(engine.speed.denominator < -1) engine.speed.denominator = 99999;
					if(engine.speed.denominator > 99999) engine.speed.denominator = -1;
					break;
				case 2:
					engine.speed.are += change;
					if(engine.speed.are < 0) engine.speed.are = 99;
					if(engine.speed.are > 99) engine.speed.are = 0;
					break;
				case 3:
					engine.speed.areLine += change;
					if(engine.speed.areLine < 0) engine.speed.areLine = 99;
					if(engine.speed.areLine > 99) engine.speed.areLine = 0;
					break;
				case 4:
					engine.speed.lineDelay += change;
					if(engine.speed.lineDelay < 0) engine.speed.lineDelay = 99;
					if(engine.speed.lineDelay > 99) engine.speed.lineDelay = 0;
					break;
				case 5:
					engine.speed.lockDelay += change;
					if(engine.speed.lockDelay < 0) engine.speed.lockDelay = 99;
					if(engine.speed.lockDelay > 99) engine.speed.lockDelay = 0;
					break;
				case 6:
					engine.speed.das += change;
					if(engine.speed.das < 0) engine.speed.das = 99;
					if(engine.speed.das > 99) engine.speed.das = 0;
					break;
				case 7:
					bgmno += change;
					if(bgmno < 0) bgmno = BGMStatus.BGM_COUNT - 1;
					if(bgmno > BGMStatus.BGM_COUNT - 1) bgmno = 0;
					break;
				case 8:
					goaltype += change;
					if(goaltype < 0) goaltype = 2;
					if(goaltype > 2) goaltype = 0;
					break;
				case 9:
				case 10:
					presetNumber += change;
					if(presetNumber < 0) presetNumber = 99;
					if(presetNumber > 99) presetNumber = 0;
					break;
				}
			}

			// 決定
			if(engine.ctrl.isPush(Controller.BUTTON_A) && (engine.statc[3] >= 5)) {
				engine.playSE("decide");

				if(engine.statc[2] == 9) {
					loadPreset(engine, owner.modeConfig, presetNumber);
				} else if(engine.statc[2] == 10) {
					savePreset(engine, owner.modeConfig, presetNumber);
					receiver.saveModeConfig(owner.modeConfig);
				} else {
					owner.modeConfig.setProperty("digrace.presetNumber", presetNumber);
					savePreset(engine, owner.modeConfig, -1);
					receiver.saveModeConfig(owner.modeConfig);
					return false;
				}
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
	 * 設定画面の描画
	 */
	@Override
	public void renderSetting(GameEngine engine, int playerID) {
		if(engine.statc[2] < 9) {
			drawMenu(engine, playerID, receiver, 0, EventReceiver.COLOR_BLUE, 0,
					"GRAVITY", String.valueOf(engine.speed.gravity),
					"G-MAX", String.valueOf(engine.speed.denominator),
					"ARE", String.valueOf(engine.speed.are),
					"ARE LINE", String.valueOf(engine.speed.areLine),
					"LINE DELAY", String.valueOf(engine.speed.lineDelay),
					"LOCK DELAY", String.valueOf(engine.speed.lockDelay),
					"DAS", String.valueOf(engine.speed.das),
					"BGM", String.valueOf(bgmno),
					"GOAL", String.valueOf(GOAL_TABLE[goaltype]));
		} else {
			drawMenu(engine, playerID, receiver, 0, EventReceiver.COLOR_GREEN, 9,
					"LOAD", String.valueOf(presetNumber),
					"SAVE", String.valueOf(presetNumber));
		}
	}

	/*
	 * Ready
	 */
	@Override
	public boolean onReady(GameEngine engine, int playerID) {
		if(engine.statc[0] == 0) {
			engine.createFieldIfNeeded();
			fillGarbage(engine, goaltype);
		}
		return false;
	}

	/*
	 * Readyの時のCalled at initialization
	 */
	@Override
	public void startGame(GameEngine engine, int playerID) {
		if(version <= 0) {
			engine.big = big;
		}
		owner.bgmStatus.bgm = bgmno;
	}

	/**
	 * Fill the playfield with garbage
	 * @param engine GameEngine
	 * @param height Garbage height level number
	 */
	private void fillGarbage(GameEngine engine, int height) {
		int w = engine.field.getWidth();
		int h = engine.field.getHeight();
		int hole = -1;

		for(int y = h - 1; y >= h - GOAL_TABLE[height]; y--) {
			int newhole = -1;
			do {
				newhole = engine.random.nextInt(w);
			} while(newhole == hole);
			hole = newhole;

			int prevColor = -1;
			for(int x = 0; x < w; x++) {
				if(x != hole) {
					int color = Block.BLOCK_COLOR_GRAY;
					if(y == h - 1) {
						do {
							color = Block.BLOCK_COLOR_GEM_RED + engine.random.nextInt(7);
						} while(color == prevColor);
						prevColor = color;
					}
					engine.field.setBlock(x,y,new Block(color,engine.getSkin(),Block.BLOCK_ATTRIBUTE_VISIBLE | Block.BLOCK_ATTRIBUTE_GARBAGE));
				}
			}
		}
	}

	/*
	 * Score描画
	 */
	@Override
	public void renderLast(GameEngine engine, int playerID) {
		receiver.drawScoreFont(engine, playerID, 0, 0, "DIG RACE", EventReceiver.COLOR_GREEN);
		receiver.drawScoreFont(engine, playerID, 0, 1, "(" + GOAL_TABLE[goaltype] + " GARBAGE GAME)", EventReceiver.COLOR_GREEN);

		if( (engine.stat == GameEngine.STAT_SETTING) || ((engine.stat == GameEngine.STAT_RESULT) && (owner.replayMode == false)) ) {
			if(!owner.replayMode && !big && (engine.ai == null)) {
				receiver.drawScoreFont(engine, playerID, 3, 3, "TIME     LINE PIECE", EventReceiver.COLOR_BLUE);

				for(int i = 0; i < RANKING_MAX; i++) {
					receiver.drawScoreFont(engine, playerID, 0, 4 + i, String.format("%2d", i + 1), EventReceiver.COLOR_YELLOW);
					receiver.drawScoreFont(engine, playerID, 3, 4 + i, GeneralUtil.getTime(rankingTime[goaltype][i]), (rankingRank == i));
					receiver.drawScoreFont(engine, playerID, 12, 4 + i, String.valueOf(rankingLines[goaltype][i]), (rankingRank == i));
					receiver.drawScoreFont(engine, playerID, 17, 4 + i, String.valueOf(rankingPiece[goaltype][i]), (rankingRank == i));
				}
			}
		} else {
			receiver.drawScoreFont(engine, playerID, 0, 3, "LINE", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 0, 4, String.valueOf(engine.statistics.lines));

			receiver.drawScoreFont(engine, playerID, 0, 6, "PIECE", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 0, 7, String.valueOf(engine.statistics.totalPieceLocked));

			receiver.drawScoreFont(engine, playerID, 0, 9, "LINE/MIN", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 0, 10, String.valueOf(engine.statistics.lpm));

			receiver.drawScoreFont(engine, playerID, 0, 12, "PIECE/SEC", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 0, 13, String.valueOf(engine.statistics.pps));

			receiver.drawScoreFont(engine, playerID, 0, 15, "TIME", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 0, 16, GeneralUtil.getTime(engine.statistics.time));
		}
	}

	/*
	 * Calculate score
	 */
	@Override
	public void calcScore(GameEngine engine, int playerID, int lines) {
		// ゴール
		if((lines > 0) && (engine.field.getHowManyGemClears() > 0)) {
			engine.ending = 1;
			engine.timerActive = false;
			engine.gameActive = false;
		}
	}

	/*
	 * Render results screen
	 */
	@Override
	public void renderResult(GameEngine engine, int playerID) {
		receiver.drawMenuFont(engine, playerID,  0, 1, "PLAY DATA", EventReceiver.COLOR_ORANGE);

		drawResultStats(engine, playerID, receiver, 3, EventReceiver.COLOR_BLUE,
				STAT_LINES, STAT_PIECE, STAT_TIME, STAT_LPM, STAT_PPS);
		drawResultRank(engine, playerID, receiver, 13, EventReceiver.COLOR_BLUE, rankingRank);
	}

	/*
	 * リプレイ保存
	 */
	@Override
	public void saveReplay(GameEngine engine, int playerID, CustomProperties prop) {
		engine.owner.replayProp.setProperty("digrace.version", version);
		savePreset(engine, engine.owner.replayProp, -1);

		// Update rankings
		if((owner.replayMode == false) && (engine.ending != 0) && (big == false) && (engine.ai == null)) {
			updateRanking(engine.statistics.time, engine.statistics.lines, engine.statistics.totalPieceLocked);

			if(rankingRank != -1) {
				saveRanking(owner.modeConfig, engine.ruleopt.strRuleName);
				receiver.saveModeConfig(owner.modeConfig);
			}
		}
	}

	/**
	 * Read rankings from property file
	 * @param prop Property file
	 * @param ruleName Rule name
	 */
	private void loadRanking(CustomProperties prop, String ruleName) {
		for(int i = 0; i < GOALTYPE_MAX; i++) {
			for(int j = 0; j < RANKING_MAX; j++) {
				rankingTime[i][j] = prop.getProperty("digrace.ranking." + ruleName + "." + i + ".time." + j, -1);
				rankingLines[i][j] = prop.getProperty("digrace.ranking." + ruleName + "." + i + ".lines." + j, 0);
				rankingPiece[i][j] = prop.getProperty("digrace.ranking." + ruleName + "." + i + ".piece." + j, 0);
			}
		}
	}

	/**
	 * Save rankings to property file
	 * @param prop Property file
	 * @param ruleName Rule name
	 */
	private void saveRanking(CustomProperties prop, String ruleName) {
		for(int i = 0; i < GOALTYPE_MAX; i++) {
			for(int j = 0; j < RANKING_MAX; j++) {
				prop.setProperty("digrace.ranking." + ruleName + "." + i + ".time." + j, rankingTime[i][j]);
				prop.setProperty("digrace.ranking." + ruleName + "." + i + ".lines." + j, rankingLines[i][j]);
				prop.setProperty("digrace.ranking." + ruleName + "." + i + ".piece." + j, rankingPiece[i][j]);
			}
		}
	}

	/**
	 * Update rankings
	 * @param time Time
	 * @param piece Piececount
	 */
	private void updateRanking(int time, int lines, int piece) {
		rankingRank = checkRanking(time, lines, piece);

		if(rankingRank != -1) {
			// Shift down ranking entries
			for(int i = RANKING_MAX - 1; i > rankingRank; i--) {
				rankingTime[goaltype][i] = rankingTime[goaltype][i - 1];
				rankingLines[goaltype][i] = rankingLines[goaltype][i - 1];
				rankingPiece[goaltype][i] = rankingPiece[goaltype][i - 1];
			}

			// Add new data
			rankingTime[goaltype][rankingRank] = time;
			rankingLines[goaltype][rankingRank] = lines;
			rankingPiece[goaltype][rankingRank] = piece;
		}
	}

	/**
	 * Calculate ranking position
	 * @param time Time
	 * @param piece Piececount
	 * @return Position (-1 if unranked)
	 */
	private int checkRanking(int time, int lines, int piece) {
		for(int i = 0; i < RANKING_MAX; i++) {
			if((time < rankingTime[goaltype][i]) || (rankingTime[goaltype][i] < 0)) {
				return i;
			} else if((time == rankingTime[goaltype][i]) && (lines < rankingLines[goaltype][i])) {
				return i;
			} else if((time == rankingTime[goaltype][i]) && (lines == rankingLines[goaltype][i]) && (piece < rankingPiece[goaltype][i])) {
				return i;
			}
		}

		return -1;
	}
}
