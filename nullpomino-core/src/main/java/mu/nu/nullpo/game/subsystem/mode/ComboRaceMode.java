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
import mu.nu.nullpo.game.net.NetUtil;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.util.CustomProperties;
import mu.nu.nullpo.util.GeneralUtil;

/**
 * COMBO RACE Mode
 */
public class ComboRaceMode extends NetDummyMode {
	/** Current version */
	private static final int CURRENT_VERSION = 1;

	/** Number of ranking records */
	private static final int RANKING_MAX = 10;

	/** 邪魔Linescountの定count */
	private static final int[] GOAL_TABLE = {20, 40, 100, -1};

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
	
	/** Meter colors for really high combos in Endless */
	private static final int[] METER_COLOUR_TABLE = {
		GameEngine.METER_COLOR_GREEN,
		GameEngine.METER_COLOR_YELLOW,
		GameEngine.METER_COLOR_ORANGE,
		GameEngine.METER_COLOR_RED,
		GameEngine.METER_COLOR_PINK,
		GameEngine.METER_COLOR_PURPLE,
		GameEngine.METER_COLOR_DARKBLUE,
		GameEngine.METER_COLOR_BLUE,
		GameEngine.METER_COLOR_CYAN,
		GameEngine.METER_COLOR_DARKGREEN,
	};
	

	/** EventReceiver object (This receives many game events, can also be used for drawing the fonts.) */

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
		rankingTime = new int[GOAL_TABLE.length][RANKING_MAX];
		rankingCombo = new int[GOAL_TABLE.length][RANKING_MAX];

		engine.framecolor = GameEngine.FRAME_COLOR_RED;

		netPlayerInit(engine, playerID);

		if(engine.owner.replayMode == false) {
			version = CURRENT_VERSION;
			presetNumber = engine.owner.modeConfig.getProperty("comborace.presetNumber", 0);
			loadPreset(engine, engine.owner.modeConfig, -1);
			loadRanking(owner.modeConfig, engine.ruleopt.strRuleName);
		} else {
			version = engine.owner.replayProp.getProperty("comborace.version", 0);
			presetNumber = 0;
			loadPreset(engine, engine.owner.replayProp, -1);

			// NET: Load name
			netPlayerName = engine.owner.replayProp.getProperty(playerID + ".net.netPlayerName", "");
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
		// NET: Net Ranking
		if(netIsNetRankingDisplayMode) {
			netOnUpdateNetPlayRanking(engine, goaltype);
		}
		// Menu
		else if(engine.owner.replayMode == false) {
			// Configuration changes
			int change = updateCursor(engine, 15);

			if(change != 0) {
				engine.playSE("change");

				int m = 1;
				if(engine.ctrl.isPress(Controller.BUTTON_E)) m = 100;
				if(engine.ctrl.isPress(Controller.BUTTON_F)) m = 1000;

				switch(menuCursor) {
				case 0:
					goaltype += change;
					if(goaltype < 0) goaltype = GOAL_TABLE.length - 1;
					if(goaltype > GOAL_TABLE.length - 1) goaltype = 0;
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

				// NET: Signal options change
				if(netIsNetPlay && (netNumSpectators > 0)) netSendOptions(engine);
			}

			// Confirm
			if(engine.ctrl.isPush(Controller.BUTTON_A) && (menuTime >= 5)) {
				engine.playSE("decide");

				if(menuCursor == 14) {
					loadPreset(engine, owner.modeConfig, presetNumber);

					// NET: Signal options change
					if(netIsNetPlay && (netNumSpectators > 0)) netSendOptions(engine);
				} else if(menuCursor == 15) {
					savePreset(engine, owner.modeConfig, presetNumber);
					receiver.saveModeConfig(owner.modeConfig);
				} else {
					owner.modeConfig.setProperty("comborace.presetNumber", presetNumber);
					savePreset(engine, owner.modeConfig, -1);
					receiver.saveModeConfig(owner.modeConfig);

					// NET: Signal start of the game
					if(netIsNetPlay) netLobby.netPlayerClient.send("start1p\n");

					return false;
				}
			}

			// Cancel
			if(engine.ctrl.isPush(Controller.BUTTON_B) && !netIsNetPlay) {
				engine.quitflag = true;
			}

			// NET: Netplay Ranking
			if(engine.ctrl.isPush(Controller.BUTTON_D) && netIsNetPlay && netIsNetRankingViewOK(engine)) {
				netEnterNetPlayRankingScreen(engine, playerID, goaltype);
			}

			menuTime++;
		}
		// Replay
		else {
			menuTime++;
			menuCursor = -1;

			if(menuTime >= 60) {
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
		if(netIsNetRankingDisplayMode) {
			// NET: Netplay Ranking
			netOnRenderNetPlayRanking(engine, playerID, receiver);
		} else if(menuCursor < 6) {
			String strSpawn = spawnAboveField ? "ABOVE" : "BELOW";

			drawMenu(engine, playerID, receiver, 0, EventReceiver.COLOR_BLUE, 0,
					"GOAL", (GOAL_TABLE[goaltype] == -1) ? "ENDLESS" : String.valueOf(GOAL_TABLE[goaltype]));
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
			engine.meterColor = GameEngine.METER_COLOR_GREEN;
			engine.meterValue = (GOAL_TABLE[goaltype] == -1) ? 0 : receiver.getMeterMax(engine);

			if(!netIsWatch) {
				fillStack(engine, goaltype);

				// NET: Send field
				if(netNumSpectators > 0) {
					netSendField(engine);
				}
			}
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
		if(netIsWatch) {
			owner.bgmStatus.bgm = BGMStatus.BGM_NOTHING;
		} else {
			owner.bgmStatus.bgm = bgmno;
		}
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
		if(GOAL_TABLE[height] > h + ceilingAdjust || GOAL_TABLE[height] == -1) {
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
					engine.field.setBlock(x,y,new Block(STACK_COLOUR_TABLE[stackColour % STACK_COLOUR_TABLE.length],engine.getSkin(),
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
		if(owner.menuOnly) return;

		receiver.drawScoreFont(engine, playerID, 0, 0, "COMBO RACE", EventReceiver.COLOR_RED);
		if (GOAL_TABLE[goaltype] == -1)
			receiver.drawScoreFont(engine, playerID, 0, 1, "(ENDLESS GAME)", EventReceiver.COLOR_WHITE);
		else
			receiver.drawScoreFont(engine, playerID, 0, 1, "(" + GOAL_TABLE[goaltype] + " LINES GAME)", EventReceiver.COLOR_WHITE);

		if( (engine.stat == GameEngine.Status.SETTING) || ((engine.stat == GameEngine.Status.RESULT) && (owner.replayMode == false)) ) {
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
			} else if((engine.combo >= 2) && (engine.gameActive)) {
				receiver.drawMenuFont(engine, playerID, 2, 22, (lastcombo - 1) + "COMBO", EventReceiver.COLOR_CYAN);
			} else if( ((engine.combo == 0) || (!engine.gameActive)) && (engine.statistics.maxCombo >= 2) ) {
				receiver.drawMenuFont(engine, playerID, 2, 22, (engine.statistics.maxCombo - 1) + "COMBO", EventReceiver.COLOR_WHITE);
			}
		}

		// NET: Number of spectators
		netDrawSpectatorsCount(engine, 0, 18);
		// NET: All number of players
		if(playerID == getPlayers() - 1) {
			netDrawAllPlayersCount(engine);
			netDrawGameRate(engine);
		}
		// NET: Player name (It may also appear in offline replay)
		netDrawPlayerName(engine);
	}

	/**
	 * Calculates line-clear score
	 * (This function will be called even if no lines are cleared)
	 */
	@Override
	public void calcScore(GameEngine engine, int playerID, int lines) {
		//  Attack
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
			if (GOAL_TABLE[goaltype] == -1)
				remainStack = Integer.MAX_VALUE;
			for(int tmplines = 1; tmplines <= lines && remainStack > 0; tmplines++, remainStack--) {
				for(int x = 0; x < engine.field.getWidth(); x++) {
					if((x < comboColumn - 1) || (x > comboColumn - 2 + comboWidth)) {
						engine.field.setBlock(x,-ceilingAdjust-tmplines,new Block(STACK_COLOUR_TABLE[stackColour % STACK_COLOUR_TABLE.length],engine.getSkin(),
								Block.BLOCK_ATTRIBUTE_VISIBLE | Block.BLOCK_ATTRIBUTE_GARBAGE));
					}
				}
				stackColour++;
			}

			if (GOAL_TABLE[goaltype] == -1) {
				int meterMax = receiver.getMeterMax(engine);
				int colorIndex = (engine.statistics.maxCombo - 1) / meterMax;
				engine.meterValue = (engine.statistics.maxCombo - 1) % meterMax;
				engine.meterColor = METER_COLOUR_TABLE[colorIndex % METER_COLOUR_TABLE.length];
				engine.meterValueSub = colorIndex > 0 ? meterMax : 0;
				engine.meterColorSub = METER_COLOUR_TABLE[Math.max(colorIndex-1, 0)  % METER_COLOUR_TABLE.length];
			} else {
				int remainLines = GOAL_TABLE[goaltype] - engine.statistics.lines;
				engine.meterValue = (remainLines * receiver.getMeterMax(engine)) / GOAL_TABLE[goaltype];

				if(remainLines <= 30) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
				if(remainLines <= 20) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
				if(remainLines <= 10) engine.meterColor = GameEngine.METER_COLOR_RED;

				// ゴール
				if(engine.statistics.lines >= GOAL_TABLE[goaltype]) {
					engine.ending = 1;
					engine.gameEnded();
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
		else if (GOAL_TABLE[goaltype] == -1 && engine.statistics.maxCombo >= 2) {
			engine.ending = 1;
			engine.gameEnded();
			engine.resetStatc();
			engine.stat = (engine.statistics.maxCombo > 40) ?
					GameEngine.Status.EXCELLENT : GameEngine.Status.GAMEOVER;
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
		drawResultStats(engine, playerID, receiver, 0, EventReceiver.COLOR_CYAN,
				Statistic.MAXCOMBO, Statistic.TIME);
		drawResultStats(engine, playerID, receiver, 4, EventReceiver.COLOR_BLUE,
				Statistic.LINES, Statistic.PIECE, Statistic.LPM, Statistic.PPS);
		drawResultRank(engine, playerID, receiver, 12, EventReceiver.COLOR_BLUE, rankingRank);
		drawResultNetRank(engine, playerID, receiver, 14, EventReceiver.COLOR_BLUE, netRankingRank[0]);
		drawResultNetRankDaily(engine, playerID, receiver, 16, EventReceiver.COLOR_BLUE, netRankingRank[1]);

		if(netIsPB) {
			receiver.drawMenuFont(engine, playerID, 2, 18, "NEW PB", EventReceiver.COLOR_ORANGE);
		}

		if(netIsNetPlay && (netReplaySendStatus == 1)) {
			receiver.drawMenuFont(engine, playerID, 0, 19, "SENDING...", EventReceiver.COLOR_PINK);
		} else if(netIsNetPlay && !netIsWatch && (netReplaySendStatus == 2)) {
			receiver.drawMenuFont(engine, playerID, 1, 19, "A: RETRY", EventReceiver.COLOR_RED);
		}
	}

	/**
	 * This function will be called when the replay data is going to be saved
	 */
	@Override
	public void saveReplay(GameEngine engine, int playerID, CustomProperties prop) {
		engine.owner.replayProp.setProperty("comborace.version", version);
		savePreset(engine, engine.owner.replayProp, -1);

		// NET: Save name
		if((netPlayerName != null) && (netPlayerName.length() > 0)) {
			prop.setProperty(playerID + ".net.netPlayerName", netPlayerName);
		}

		// Update rankings
		if((owner.replayMode == false) && (!big) && (engine.ai == null)) {
			updateRanking(engine.statistics.maxCombo - 1,
					(engine.ending == 0) ? -1 : engine.statistics.time);

			if(rankingRank != -1) {
				saveRanking(owner.modeConfig, engine.ruleopt.strRuleName);
				receiver.saveModeConfig(owner.modeConfig);
			}
		}
	}

	/**
	 * Load the ranking
	 */
	@Override
	protected void loadRanking(CustomProperties prop, String ruleName) {
		for(int i = 0; i < GOAL_TABLE.length; i++) {
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
		for(int i = 0; i < GOAL_TABLE.length; i++) {
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
			} else if ((maxcombo == rankingCombo[goaltype][i]) && (time >= 0) &&
					((time < rankingTime[goaltype][i]) || (rankingTime[goaltype][i] == -1))) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * NET: Send various in-game stats (as well as goaltype)
	 * @param engine GameEngine
	 */
	@Override
	protected void netSendStats(GameEngine engine) {
		int bg = owner.backgroundStatus.fadesw ? owner.backgroundStatus.fadebg : owner.backgroundStatus.bg;
		String msg = "game\tstats\t";
		msg += engine.statistics.lines + "\t" + engine.statistics.totalPieceLocked + "\t";
		msg += engine.statistics.time + "\t" + engine.statistics.lpm + "\t";
		msg += engine.statistics.pps + "\t" + goaltype + "\t";
		msg += engine.gameActive + "\t" + engine.timerActive + "\t";
		msg += engine.meterColor + "\t" + engine.meterValue + "\t";
		msg += bg + "\t";
		msg += scgettime + "\t" + lastevent + "\t" + lastb2b + "\t" + lastcombo + "\t" + lastpiece + "\t";
		msg += engine.statistics.maxCombo + "\t" + engine.combo + "\n";
		netLobby.netPlayerClient.send(msg);
	}

	/**
	 * NET: Receive various in-game stats (as well as goaltype)
	 */
	@Override
	protected void netRecvStats(GameEngine engine, String[] message) {
		engine.statistics.lines = Integer.parseInt(message[4]);
		engine.statistics.totalPieceLocked = Integer.parseInt(message[5]);
		engine.statistics.time = Integer.parseInt(message[6]);
		engine.statistics.lpm = Float.parseFloat(message[7]);
		engine.statistics.pps = Float.parseFloat(message[8]);
		goaltype = Integer.parseInt(message[9]);
		engine.gameActive = Boolean.parseBoolean(message[10]);
		engine.timerActive = Boolean.parseBoolean(message[11]);
		engine.meterColor = Integer.parseInt(message[12]);
		engine.meterValue = Integer.parseInt(message[13]);
		owner.backgroundStatus.bg = Integer.parseInt(message[14]);
		scgettime = Integer.parseInt(message[15]);
		lastevent = Integer.parseInt(message[16]);
		lastb2b = Boolean.parseBoolean(message[17]);
		lastcombo = Integer.parseInt(message[18]);
		lastpiece = Integer.parseInt(message[19]);
		engine.statistics.maxCombo = Integer.parseInt(message[20]);
		engine.combo = Integer.parseInt(message[21]);
	}

	/**
	 * NET: Send end-of-game stats
	 * @param engine GameEngine
	 */
	@Override
	protected void netSendEndGameStats(GameEngine engine) {
		String subMsg = "";
		subMsg += "MAX COMBO;" + (engine.statistics.maxCombo - 1) + "\t";
		subMsg += "TIME;" + GeneralUtil.getTime(engine.statistics.time) + "\t";
		subMsg += "LINE;" + engine.statistics.lines + "\t";
		subMsg += "PIECE;" + engine.statistics.totalPieceLocked + "\t";
		subMsg += "LINE/MIN;" + engine.statistics.lpm + "\t";
		subMsg += "PIECE/SEC;" + engine.statistics.pps + "\t";
		String msg = "gstat1p\t" + NetUtil.urlEncode(subMsg) + "\n";
		netLobby.netPlayerClient.send(msg);
	}

	/**
	 * NET: Send game options to all spectators
	 * @param engine GameEngine
	 */
	@Override
	protected void netSendOptions(GameEngine engine) {
		String msg = "game\toption\t";
		msg += engine.speed.gravity + "\t" + engine.speed.denominator + "\t" + engine.speed.are + "\t";
		msg += engine.speed.areLine + "\t" + engine.speed.lineDelay + "\t" + engine.speed.lockDelay + "\t";
		msg += engine.speed.das + "\t" + bgmno + "\t" + goaltype + "\t" + presetNumber + "\t";
		msg += shapetype + "\t" + comboColumn + "\t" + comboWidth + "\t" + ceilingAdjust + "\t" + spawnAboveField + "\n";
		netLobby.netPlayerClient.send(msg);
	}

	/**
	 * NET: Receive game options
	 */
	@Override
	protected void netRecvOptions(GameEngine engine, String[] message) {
		engine.speed.gravity = Integer.parseInt(message[4]);
		engine.speed.denominator = Integer.parseInt(message[5]);
		engine.speed.are = Integer.parseInt(message[6]);
		engine.speed.areLine = Integer.parseInt(message[7]);
		engine.speed.lineDelay = Integer.parseInt(message[8]);
		engine.speed.lockDelay = Integer.parseInt(message[9]);
		engine.speed.das = Integer.parseInt(message[10]);
		bgmno = Integer.parseInt(message[11]);
		goaltype = Integer.parseInt(message[12]);
		presetNumber = Integer.parseInt(message[13]);
		shapetype = Integer.parseInt(message[14]);
		comboColumn = Integer.parseInt(message[15]);
		comboWidth = Integer.parseInt(message[16]);
		ceilingAdjust = Integer.parseInt(message[17]);
		spawnAboveField = Boolean.parseBoolean(message[18]);
	}

	/**
	 * NET: Get goal type
	 */
	@Override
	protected int netGetGoalType() {
		return goaltype;
	}

	/**
	 * NET: It returns true when the current settings doesn't prevent leaderboard screen from showing.
	 */
	@Override
	protected boolean netIsNetRankingViewOK(GameEngine engine) {
		return (!big) && (engine.ai == null);
	}
}
