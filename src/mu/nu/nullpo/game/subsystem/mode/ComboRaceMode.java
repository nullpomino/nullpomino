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
import mu.nu.nullpo.game.component.Piece;
import mu.nu.nullpo.game.event.EventReceiver;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.util.CustomProperties;
import mu.nu.nullpo.util.GeneralUtil;

/**
 * COMBO RACE mode
 */
public class ComboRaceMode extends DummyMode {
	/** Current version */
	private static final int CURRENT_VERSION = 1;

	/** Number of ranking records */
	private static final int RANKING_MAX = 10;

	/** 制限 time type */
	private static final int GOALTYPE_MAX = 3;

	/** 邪魔Linescountの定count */
	private static final int[] GOAL_TABLE = {20, 40, 100};

	/** Most recent scoring event typeの定count */
	private static final int EVENT_NONE = 0,
							 EVENT_SINGLE = 1,
							 EVENT_DOUBLE = 2,
							 EVENT_TRIPLE = 3,
							 EVENT_FOUR = 4,
							 EVENT_TSPIN_SINGLE_MINI = 5,
							 EVENT_TSPIN_SINGLE = 6,
							 EVENT_TSPIN_DOUBLE = 7,
							 EVENT_TSPIN_TRIPLE = 8,
							 EVENT_TSPIN_DOUBLE_MINI = 9;

	/** Number of starting shapes */
	private static final int SHAPETYPE_MAX = 9;

	/** Names of starting shapes */
	private static final String[] SHAPE_NAME_TABLE = {
		"NONE",
		"LEFT I",
		"RIGHT I",
		"LEFT Z",
		"RIGHT S",
		"LEFT S",
		"RIGHT Z",
		"LEFT J",
		"RIGHT L",
	};

	/** Starting shape table */
	private static final int[][] SHAPE_TABLE = {
		{0,0,0,0,0,0,0,0,0,0,0,0},
		{1,1,1,0,0,0,0,0,0,0,0,0},
		{0,1,1,1,0,0,0,0,0,0,0,0},
		{1,1,0,0,1,0,0,0,0,0,0,0},
		{0,0,1,1,0,0,0,1,0,0,0,0},
		{1,0,0,0,1,1,0,0,0,0,0,0},
		{0,0,0,1,0,0,1,1,0,0,0,0},
		{1,0,0,0,1,0,0,0,1,0,0,0},
		{0,0,0,1,0,0,0,1,0,0,0,1}
	};

	/** Starting shape colour */
	private static final int[] SHAPE_COLOUR_TABLE = {
		Block.BLOCK_COLOR_NONE,
		Block.BLOCK_COLOR_CYAN,
		Block.BLOCK_COLOR_CYAN,
		Block.BLOCK_COLOR_RED,
		Block.BLOCK_COLOR_GREEN,
		Block.BLOCK_COLOR_GREEN,
		Block.BLOCK_COLOR_RED,
		Block.BLOCK_COLOR_BLUE,
		Block.BLOCK_COLOR_ORANGE
	};

	/** Stack colour order */
	private static final int[] STACK_COLOUR_TABLE = {
		Block.BLOCK_COLOR_RED,
		Block.BLOCK_COLOR_ORANGE,
		Block.BLOCK_COLOR_YELLOW,
		Block.BLOCK_COLOR_GREEN,
		Block.BLOCK_COLOR_CYAN,
		Block.BLOCK_COLOR_BLUE,
		Block.BLOCK_COLOR_PURPLE,
	};

	/** Number of stack colours */
	private static final int STACK_COLOUR_MAX = 7;

	/** GameManager object (Manages entire game status) */
	private GameManager owner;

	/** EventReceiver object (This receives many game events, can also be used for drawing the fonts.) */
	private EventReceiver receiver;

	/** Elapsed time from last line clear (lastscore is displayed to screen until this reaches to 120) */
	private int scgettime;

	/** Most recent scoring event type */
	private int lastevent;

	/** Most recent scoring eventでB2Bだったらtrue */
	private boolean lastb2b;

	/** Most recent scoring eventでのCombocount */
	private int lastcombo;

	/** Most recent scoring eventでのピースID */
	private int lastpiece;

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

	/** Rankings' Combo */
	private int[][] rankingCombo;

	/** Shape type */
	private int shapetype;

	/** Stack colour */
	private int stackColour;

	/** Column number of combo well (starts from 1) */
	private int comboColumn;

	/** Width of combo well */
	private int comboWidth;

	/** Height difference between ceiling and stack (negative number lowers the stack height) */
	private int ceilingAdjust;

	/** Piece spawns above field if true */
	private boolean spawnAboveField;

	/** Number of remaining stack lines that need to be added when lines are cleared */
	private int remainStack;

	/** Next section lines */
	private int nextseclines;

	/**
	 * Returns the name of this mode
	 */
	@Override
	public String getName() {
		return "COMBO RACE";
	}

	/**
	 * This function will be called when the game enters the main game screen.
	 */
	@Override
	public void playerInit(GameEngine engine, int playerID) {
		owner = engine.owner;
		receiver = engine.owner.receiver;

		scgettime = 0;
		lastevent = EVENT_NONE;
		lastb2b = false;
		lastcombo = 0;
		lastpiece = 0;
		bgmno = 0;
		big = false;
		goaltype = 0;
		shapetype = 1;
		presetNumber = 0;
		remainStack = 0;
		stackColour = 0;
		nextseclines = 10;

		rankingRank = -1;
		rankingTime = new int[GOALTYPE_MAX][RANKING_MAX];
		rankingCombo = new int[GOALTYPE_MAX][RANKING_MAX];

		engine.framecolor = GameEngine.FRAME_COLOR_RED;

		if(engine.owner.replayMode == false) {
			version = CURRENT_VERSION;
			presetNumber = engine.owner.modeConfig.getProperty("comborace.presetNumber", 0);
			loadPreset(engine, engine.owner.modeConfig, -1);
			loadRanking(owner.modeConfig, engine.ruleopt.strRuleName);
		} else {
			version = engine.owner.replayProp.getProperty("comborace.version", 0);
			presetNumber = 0;
			loadPreset(engine, engine.owner.replayProp, -1);
		}
	}

	/**
	 * Load the settings
	 */
	private void loadPreset(GameEngine engine, CustomProperties prop, int preset) {
		engine.speed.gravity = prop.getProperty("comborace.gravity." + preset, 4);
		engine.speed.denominator = prop.getProperty("comborace.denominator." + preset, 256);
		engine.speed.are = prop.getProperty("comborace.are." + preset, 0);
		engine.speed.areLine = prop.getProperty("comborace.areLine." + preset, 0);
		engine.speed.lineDelay = prop.getProperty("comborace.lineDelay." + preset, 0);
		engine.speed.lockDelay = prop.getProperty("comborace.lockDelay." + preset, 30);
		engine.speed.das = prop.getProperty("comborace.das." + preset, 14);
		bgmno = prop.getProperty("comborace.bgmno." + preset, 0);
		big = prop.getProperty("comborace.big." + preset, false);
		goaltype = prop.getProperty("comborace.goaltype." + preset, 1);
		shapetype = prop.getProperty("comborace.shapetype." + preset, 1);
		comboWidth = prop.getProperty("comborace.comboWidth." + preset, 4);
		comboColumn = prop.getProperty("comborace.comboColumn." + preset, 4);
		ceilingAdjust = prop.getProperty("comborace.ceilingAdjust." + preset, -2);
		spawnAboveField = prop.getProperty("comborace.spawnAboveField." + preset, true);
	}

	/**
	 * Save the settings
	 */
	private void savePreset(GameEngine engine, CustomProperties prop, int preset) {
		prop.setProperty("comborace.gravity." + preset, engine.speed.gravity);
		prop.setProperty("comborace.denominator." + preset, engine.speed.denominator);
		prop.setProperty("comborace.are." + preset, engine.speed.are);
		prop.setProperty("comborace.areLine." + preset, engine.speed.areLine);
		prop.setProperty("comborace.lineDelay." + preset, engine.speed.lineDelay);
		prop.setProperty("comborace.lockDelay." + preset, engine.speed.lockDelay);
		prop.setProperty("comborace.das." + preset, engine.speed.das);
		prop.setProperty("comborace.bgmno." + preset, bgmno);
		prop.setProperty("comborace.big." + preset, big);
		prop.setProperty("comborace.goaltype." + preset, goaltype);
		prop.setProperty("comborace.shapetype." + preset, shapetype);
		prop.setProperty("comborace.comboWidth." + preset, comboWidth);
		prop.setProperty("comborace.comboColumn." + preset, comboColumn);
		prop.setProperty("comborace.ceilingAdjust." + preset, ceilingAdjust);
		prop.setProperty("comborace.spawnAboveField." + preset, spawnAboveField);
	}

	/**
	 * Main routine for game setup screen
	 */
	@Override
	public boolean onSetting(GameEngine engine, int playerID) {
		// Menu
		if(engine.owner.replayMode == false) {
			// Configuration changes
			int change = updateCursor(engine, 15);

			if(change != 0) {
				engine.playSE("change");

				int m = 1;
				if(engine.ctrl.isPress(Controller.BUTTON_E)) m = 100;
				if(engine.ctrl.isPress(Controller.BUTTON_F)) m = 1000;

				switch(engine.statc[2]) {
				case 0:
					goaltype += change;
					if(goaltype < 0) goaltype = 2;
					if(goaltype > GOALTYPE_MAX - 1) goaltype = 0;
					break;
				case 1:
					shapetype += change;
					if(shapetype < 0) shapetype = SHAPETYPE_MAX - 1;
					if(shapetype > SHAPETYPE_MAX - 1) shapetype = 0;
					break;
				case 2:
					comboColumn += change;
					if(comboColumn > 10) comboColumn = 1;
					if(comboColumn < 1) comboColumn = 10;
					while(comboColumn + comboWidth - 1 > 10) {
						comboWidth--;
					}
					break;
				case 3:
					comboWidth += change;
					if(comboWidth > 10) comboWidth = 1;
					if(comboWidth < 1) comboWidth = 10;
					while(comboColumn + comboWidth - 1 > 10) {
						comboColumn--;
					}
					break;
				case 4:
					ceilingAdjust += change;
					if(ceilingAdjust > 10) ceilingAdjust = -10;
					if(ceilingAdjust < -10) ceilingAdjust = 10;
					break;
				case 5:
					spawnAboveField = !spawnAboveField;
					break;
				case 6:
					engine.speed.gravity += change * m;
					if(engine.speed.gravity < -1) engine.speed.gravity = 99999;
					if(engine.speed.gravity > 99999) engine.speed.gravity = -1;
					break;
				case 7:
					engine.speed.denominator += change * m;
					if(engine.speed.denominator < -1) engine.speed.denominator = 99999;
					if(engine.speed.denominator > 99999) engine.speed.denominator = -1;
					break;
				case 8:
					engine.speed.are += change;
					if(engine.speed.are < 0) engine.speed.are = 99;
					if(engine.speed.are > 99) engine.speed.are = 0;
					break;
				case 9:
					engine.speed.areLine += change;
					if(engine.speed.areLine < 0) engine.speed.areLine = 99;
					if(engine.speed.areLine > 99) engine.speed.areLine = 0;
					break;
				case 10:
					engine.speed.lineDelay += change;
					if(engine.speed.lineDelay < 0) engine.speed.lineDelay = 99;
					if(engine.speed.lineDelay > 99) engine.speed.lineDelay = 0;
					break;
				case 11:
					engine.speed.lockDelay += change;
					if(engine.speed.lockDelay < 0) engine.speed.lockDelay = 99;
					if(engine.speed.lockDelay > 99) engine.speed.lockDelay = 0;
					break;
				case 12:
					engine.speed.das += change;
					if(engine.speed.das < 0) engine.speed.das = 99;
					if(engine.speed.das > 99) engine.speed.das = 0;
					break;
				case 13:
					bgmno += change;
					if(bgmno < 0) bgmno = BGMStatus.BGM_COUNT - 1;
					if(bgmno > BGMStatus.BGM_COUNT - 1) bgmno = 0;
					break;
				case 14:
				case 15:
					presetNumber += change;
					if(presetNumber < 0) presetNumber = 99;
					if(presetNumber > 99) presetNumber = 0;
					break;
				}
			}

			// 決定
			if(engine.ctrl.isPush(Controller.BUTTON_A) && (engine.statc[3] >= 5)) {
				engine.playSE("decide");

				if(engine.statc[2] == 14) {
					loadPreset(engine, owner.modeConfig, presetNumber);
				} else if(engine.statc[2] == 15) {
					savePreset(engine, owner.modeConfig, presetNumber);
					receiver.saveModeConfig(owner.modeConfig);
				} else {
					owner.modeConfig.setProperty("comborace.presetNumber", presetNumber);
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

	/**
	 * Renders game setup screen
	 */
	@Override
	public void renderSetting(GameEngine engine, int playerID) {
		if(engine.statc[2] < 6) {
			String strSpawn = spawnAboveField ? "ABOVE" : "BELOW";

			drawMenu(engine, playerID, receiver, 0, EventReceiver.COLOR_BLUE, 0,
					"GOAL", String.valueOf(GOAL_TABLE[goaltype]));
			drawMenu(engine, playerID, receiver, 2, (comboWidth == 4) ? EventReceiver.COLOR_BLUE : EventReceiver.COLOR_WHITE, 1,
					"STARTSHAPE", SHAPE_NAME_TABLE[shapetype]);
			drawMenu(engine, playerID, receiver, 4, EventReceiver.COLOR_BLUE, 2,
					"COLUMN", String.valueOf(comboColumn),
					"WIDTH", String.valueOf(comboWidth),
					"CEILING", String.valueOf(ceilingAdjust),
					"PIECESPAWN", String.valueOf(strSpawn));
		} else {
			drawMenu(engine, playerID, receiver, 0, EventReceiver.COLOR_BLUE, 6,
					"GRAVITY", String.valueOf(engine.speed.gravity),
					"G-MAX", String.valueOf(engine.speed.denominator),
					"ARE", String.valueOf(engine.speed.are),
					"ARE LINE", String.valueOf(engine.speed.areLine),
					"LINE DELAY", String.valueOf(engine.speed.lineDelay),
					"LOCK DELAY", String.valueOf(engine.speed.lockDelay),
					"DAS", String.valueOf(engine.speed.das),
					"BGM", String.valueOf(bgmno));
			drawMenu(engine, playerID, receiver, 16, EventReceiver.COLOR_GREEN, 14,
					"LOAD", String.valueOf(presetNumber),
					"SAVE", String.valueOf(presetNumber));
		}
	}

	/**
	 * Ready
	 */
	@Override
	public boolean onReady(GameEngine engine, int playerID) {
		if(engine.statc[0] == 0) {
			engine.createFieldIfNeeded();
			fillStack(engine, goaltype);
		}
		return false;
	}

	/**
	 * This function will be called before the game actually begins (after Ready&Go screen disappears)
	 */
	@Override
	public void startGame(GameEngine engine, int playerID) {
		if(version <= 0) {
			engine.big = big;
		}
		owner.bgmStatus.bgm = bgmno;
		engine.meterColor = GameEngine.METER_COLOR_GREEN;
		engine.meterValue = receiver.getMeterMax(engine);
		engine.comboType = GameEngine.COMBO_TYPE_NORMAL;
		engine.tspinEnable = true;
		engine.tspinAllowKick = true;
		engine.ruleopt.pieceEnterAboveField = spawnAboveField;
	}

	/**
	 * Fill the playfield with stack
	 * @param engine GameEngine
	 * @param height Stack height level number
	 */
	private void fillStack(GameEngine engine, int height) {
		int w = engine.field.getWidth();
		int h = engine.field.getHeight();
		int stackHeight;

		/*
		 *  set initial stack height and remaining stack lines
		 *  depending on the goal lines and ceiling height adjustment
		 */
		if(GOAL_TABLE[height] > h + ceilingAdjust) {
			stackHeight = h + ceilingAdjust;
			remainStack = GOAL_TABLE[height] - h - ceilingAdjust;
		} else {
			stackHeight = GOAL_TABLE[height];
			remainStack = 0;
		}

		// fill stack from the bottom to the top
		for(int y = h - 1; y >= h - stackHeight; y--) {
			for(int x = 0; x < w; x++) {
				if(	((x < comboColumn - 1) || (x > comboColumn - 2 + comboWidth))
					) {
					engine.field.setBlock(x,y,new Block(STACK_COLOUR_TABLE[stackColour % STACK_COLOUR_MAX],engine.getSkin(),
							Block.BLOCK_ATTRIBUTE_VISIBLE | Block.BLOCK_ATTRIBUTE_GARBAGE));
				}
			}
			stackColour++;
		}

		// insert starting shape
		if(comboWidth == 4) {
			for(int i = 0; i < 12; i++) {
				if(SHAPE_TABLE[shapetype][i] == 1) {
					engine.field.setBlock(i%4 + comboColumn - 1, h - 1 - i/4,new Block(SHAPE_COLOUR_TABLE[shapetype],engine.getSkin(),
							Block.BLOCK_ATTRIBUTE_VISIBLE | Block.BLOCK_ATTRIBUTE_GARBAGE));
				}
			}
		}
	}

	/**
	 * Renders HUD (leaderboard or game statistics)
	 */
	@Override
	public void renderLast(GameEngine engine, int playerID) {
		receiver.drawScoreFont(engine, playerID, 0, 0, "COMBO RACE", EventReceiver.COLOR_RED);
		receiver.drawScoreFont(engine, playerID, 0, 1, "(" + GOAL_TABLE[goaltype] + " LINES GAME)", EventReceiver.COLOR_WHITE);

		if( (engine.stat == GameEngine.STAT_SETTING) || ((engine.stat == GameEngine.STAT_RESULT) && (owner.replayMode == false)) ) {
			if(!owner.replayMode && !big && (engine.ai == null)) {
				receiver.drawScoreFont(engine, playerID, 3, 3, "COMBO TIME", EventReceiver.COLOR_BLUE);

				for(int i = 0; i < RANKING_MAX; i++) {
					receiver.drawScoreFont(engine, playerID, 0, 4 + i, String.format("%2d", i + 1), EventReceiver.COLOR_YELLOW);
					receiver.drawScoreFont(engine, playerID, 3, 4 + i, String.valueOf(rankingCombo[goaltype][i]), (rankingRank == i));
					receiver.drawScoreFont(engine, playerID, 9, 4 + i, GeneralUtil.getTime(rankingTime[goaltype][i]), (rankingRank == i));
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

			if((lastevent != EVENT_NONE) && (scgettime < 120)) {
				String strPieceName = Piece.getPieceName(lastpiece);

				switch(lastevent) {
				case EVENT_SINGLE:
					receiver.drawMenuFont(engine, playerID, 2, 21, "SINGLE", EventReceiver.COLOR_DARKBLUE);
					break;
				case EVENT_DOUBLE:
					receiver.drawMenuFont(engine, playerID, 2, 21, "DOUBLE", EventReceiver.COLOR_BLUE);
					break;
				case EVENT_TRIPLE:
					receiver.drawMenuFont(engine, playerID, 2, 21, "TRIPLE", EventReceiver.COLOR_GREEN);
					break;
				case EVENT_FOUR:
					if(lastb2b) receiver.drawMenuFont(engine, playerID, 3, 21, "FOUR", EventReceiver.COLOR_RED);
					else receiver.drawMenuFont(engine, playerID, 3, 21, "FOUR", EventReceiver.COLOR_ORANGE);
					break;
				case EVENT_TSPIN_SINGLE_MINI:
					if(lastb2b) receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-MINI-S", EventReceiver.COLOR_RED);
					else receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-MINI-S", EventReceiver.COLOR_ORANGE);
					break;
				case EVENT_TSPIN_SINGLE:
					if(lastb2b) receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-SINGLE", EventReceiver.COLOR_RED);
					else receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-SINGLE", EventReceiver.COLOR_ORANGE);
					break;
				case EVENT_TSPIN_DOUBLE_MINI:
					if(lastb2b) receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-MINI-D", EventReceiver.COLOR_RED);
					else receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-MINI-D", EventReceiver.COLOR_ORANGE);
					break;
				case EVENT_TSPIN_DOUBLE:
					if(lastb2b) receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-DOUBLE", EventReceiver.COLOR_RED);
					else receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-DOUBLE", EventReceiver.COLOR_ORANGE);
					break;
				case EVENT_TSPIN_TRIPLE:
					if(lastb2b) receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-TRIPLE", EventReceiver.COLOR_RED);
					else receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-TRIPLE", EventReceiver.COLOR_ORANGE);
					break;
				}

				if(lastcombo >= 2)
					receiver.drawMenuFont(engine, playerID, 2, 22, (lastcombo - 1) + "COMBO", EventReceiver.COLOR_CYAN);
			}
		}

	}

	/**
	 * Calculates line-clear score
	 * (This function will be called even if no lines are cleared)
	 */
	@Override
	public void calcScore(GameEngine engine, int playerID, int lines) {
		// 攻撃
		if(lines > 0) {
			scgettime = 0;

			if(engine.tspin) {
				// T-Spin 1 line
				if(lines == 1) {
					if(engine.tspinmini) {
						lastevent = EVENT_TSPIN_SINGLE_MINI;
					} else {
						lastevent = EVENT_TSPIN_SINGLE;
					}
				}
				// T-Spin 2 lines
				else if(lines == 2) {
					if(engine.tspinmini && engine.useAllSpinBonus) {
						lastevent = EVENT_TSPIN_DOUBLE_MINI;
					} else {
						lastevent = EVENT_TSPIN_DOUBLE;
					}
				}
				// T-Spin 3 lines
				else if(lines >= 3) {
					lastevent = EVENT_TSPIN_TRIPLE;
				}
			} else {
				if(lines == 1) {
					lastevent = EVENT_SINGLE;
				} else if(lines == 2) {
					lastevent = EVENT_DOUBLE;
				} else if(lines == 3) {
					lastevent = EVENT_TRIPLE;
				} else if(lines >= 4) {
					lastevent = EVENT_FOUR;
				}
			}

			// B2B
			lastb2b = engine.b2b;

			// Combo
			lastcombo = engine.combo;

			// All clear
			if((lines >= 1) && (engine.field.isEmpty())) {
				engine.playSE("bravo");
			}

			lastpiece = engine.nowPieceObject.id;

			// add any remaining stack lines
			for(int tmplines = 1; tmplines <= lines && remainStack > 0; tmplines++, remainStack--) {
				for(int x = 0; x < engine.field.getWidth(); x++) {
					if((x < comboColumn - 1) || (x > comboColumn - 2 + comboWidth)) {
						engine.field.setBlock(x,-ceilingAdjust-tmplines,new Block(STACK_COLOUR_TABLE[stackColour % STACK_COLOUR_MAX],engine.getSkin(),
								Block.BLOCK_ATTRIBUTE_VISIBLE | Block.BLOCK_ATTRIBUTE_GARBAGE));
					}
				}
				stackColour++;
			}

			int remainLines = GOAL_TABLE[goaltype] - engine.statistics.lines;
			engine.meterValue = (remainLines * receiver.getMeterMax(engine)) / GOAL_TABLE[goaltype];

			if(remainLines <= 30) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
			if(remainLines <= 20) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
			if(remainLines <= 10) engine.meterColor = GameEngine.METER_COLOR_RED;

			// ゴール
			if(engine.statistics.lines >= GOAL_TABLE[goaltype]) {
				engine.ending = 1;
				engine.timerActive = false;
				engine.gameActive = false;
			} else if(engine.statistics.lines >= GOAL_TABLE[goaltype] - 5) {
				owner.bgmStatus.fadesw = true;
			} else if(engine.statistics.lines >= nextseclines) {
				owner.backgroundStatus.fadesw = true;
				owner.backgroundStatus.fadecount = 0;
				owner.backgroundStatus.fadebg = nextseclines / 10;
				nextseclines += 10;
			}
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
	 * Renders game result screen
	 */
	@Override
	public void renderResult(GameEngine engine, int playerID) {
		receiver.drawMenuFont(engine, playerID,  0, 1, "PLAY DATA", EventReceiver.COLOR_ORANGE);

		drawResultStats(engine, playerID, receiver, 3, EventReceiver.COLOR_BLUE,
				STAT_LINES, STAT_PIECE, STAT_LPM, STAT_PPS, STAT_TIME);
		drawResultStats(engine, playerID, receiver, 13, EventReceiver.COLOR_CYAN, STAT_MAXCOMBO);
		drawResult(engine, playerID, receiver, 13, EventReceiver.COLOR_CYAN,
				"MAX COMBO", String.format("%10d", engine.statistics.maxCombo - 1));
		drawResultRank(engine, playerID, receiver, 15, EventReceiver.COLOR_BLUE, rankingRank);
	}

	/**
	 * This function will be called when the replay data is going to be saved
	 */
	@Override
	public void saveReplay(GameEngine engine, int playerID, CustomProperties prop) {
		engine.owner.replayProp.setProperty("comborace.version", version);
		savePreset(engine, engine.owner.replayProp, -1);

		// Update rankings
		if((owner.replayMode == false) && (engine.ending != 0) && (big == false) && (engine.ai == null)) {
			updateRanking(engine.statistics.maxCombo - 1, engine.statistics.time);

			if(rankingRank != -1) {
				saveRanking(owner.modeConfig, engine.ruleopt.strRuleName);
				receiver.saveModeConfig(owner.modeConfig);
			}
		}
	}

	/**
	 * Load the ranking
	 */
	private void loadRanking(CustomProperties prop, String ruleName) {
		for(int i = 0; i < GOALTYPE_MAX; i++) {
			for(int j = 0; j < RANKING_MAX; j++) {
				rankingCombo[i][j] = prop.getProperty("comborace.ranking." + ruleName + "." + i + ".maxcombo." + j, 0);
				rankingTime[i][j] = prop.getProperty("comborace.ranking." + ruleName + "." + i + ".time." + j, -1);
			}
		}
	}

	/**
	 * Save the ranking
	 */
	private void saveRanking(CustomProperties prop, String ruleName) {
		for(int i = 0; i < GOALTYPE_MAX; i++) {
			for(int j = 0; j < RANKING_MAX; j++) {
				prop.setProperty("comborace.ranking." + ruleName + "." + i + ".maxcombo." + j, rankingCombo[i][j]);
				prop.setProperty("comborace.ranking." + ruleName + "." + i + ".time." + j, rankingTime[i][j]);
			}
		}
	}

	/**
	 * Update the ranking
	 */
	private void updateRanking(int maxcombo, int time) {
		rankingRank = checkRanking(maxcombo, time);

		if(rankingRank != -1) {
			// Shift down ranking entries
			for(int i = RANKING_MAX - 1; i > rankingRank; i--) {
				rankingCombo[goaltype][i] = rankingCombo[goaltype][i - 1];
				rankingTime[goaltype][i] = rankingTime[goaltype][i - 1];
			}

			// Add new data
			rankingCombo[goaltype][rankingRank] = maxcombo;
			rankingTime[goaltype][rankingRank] = time;
		}
	}

	/**
	 * This function will check the ranking and returns which place you are. (-1: Out of rank)
	 */
	private int checkRanking(int maxcombo, int time) {
		for(int i = 0; i < RANKING_MAX; i++) {
			if(maxcombo > rankingCombo[goaltype][i]) {
				return i;
			} else if ((maxcombo == rankingCombo[goaltype][i]) && (time < rankingTime[goaltype][i])) {
				return i;
			}
		}

		return -1;
	}
}
