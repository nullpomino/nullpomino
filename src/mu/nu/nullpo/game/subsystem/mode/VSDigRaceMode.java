package mu.nu.nullpo.game.subsystem.mode;

import java.util.Random;

import mu.nu.nullpo.game.component.BGMStatus;
import mu.nu.nullpo.game.component.Block;
import mu.nu.nullpo.game.component.Controller;
import mu.nu.nullpo.game.event.EventReceiver;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.util.CustomProperties;
import mu.nu.nullpo.util.GeneralUtil;

/**
 * VS-DIG RACE mode
 */
public class VSDigRaceMode extends AbstractMode {
	/** Current version */
	private static final int CURRENT_VERSION = 0;

	/** Number of players */
	private static final int MAX_PLAYERS = 2;

	/** Each player's frame color */
	private final int[] PLAYER_COLOR_FRAME = {GameEngine.FRAME_COLOR_RED, GameEngine.FRAME_COLOR_BLUE};

	/** Number of garbage lines to clear */
	private int[] goalLines;

	/** Rate of garbage holes change */
	private int[] garbagePercent;

	/** BGM number */
	private int bgmno;

	/** Sound effects ON/OFF */
	private boolean[] enableSE;

	/** Last preset number used */
	private int[] presetNumber;

	/** Winner player ID */
	private int winnerID;

	/** Win count for each player */
	private int[] winCount;

	/** Version */
	private int version;

	/*
	 * Mode name
	 */
	@Override
	public String getName() {
		return "VS-DIG RACE";
	}

	@Override
	public boolean isVSMode() {
		return true;
	}

	/*
	 * Number of players
	 */
	@Override
	public int getPlayers() {
		return MAX_PLAYERS;
	}

	/*
	 * Mode init
	 */
	@Override
	public void modeInit(GameManager manager) {
		owner = manager;
		receiver = manager.receiver;

		goalLines = new int[MAX_PLAYERS];
		garbagePercent = new int[MAX_PLAYERS];
		bgmno = 0;
		enableSE = new boolean[MAX_PLAYERS];
		presetNumber = new int[MAX_PLAYERS];
		winnerID = -1;
		winCount = new int[MAX_PLAYERS];
		version = CURRENT_VERSION;
	}

	/**
	 * Read speed presets
	 * @param engine GameEngine
	 * @param prop Property file to read from
	 * @param preset Preset number
	 */
	private void loadPreset(GameEngine engine, CustomProperties prop, int preset) {
		engine.speed.gravity = prop.getProperty("vsdigrace.gravity." + preset, 4);
		engine.speed.denominator = prop.getProperty("vsdigrace.denominator." + preset, 256);
		engine.speed.are = prop.getProperty("vsdigrace.are." + preset, 0);
		engine.speed.areLine = prop.getProperty("vsdigrace.areLine." + preset, 0);
		engine.speed.lineDelay = prop.getProperty("vsdigrace.lineDelay." + preset, 0);
		engine.speed.lockDelay = prop.getProperty("vsdigrace.lockDelay." + preset, 30);
		engine.speed.das = prop.getProperty("vsdigrace.das." + preset, 14);
	}

	/**
	 * Save speed presets
	 * @param engine GameEngine
	 * @param prop Property file to save to
	 * @param preset Preset number
	 */
	private void savePreset(GameEngine engine, CustomProperties prop, int preset) {
		prop.setProperty("vsdigrace.gravity." + preset, engine.speed.gravity);
		prop.setProperty("vsdigrace.denominator." + preset, engine.speed.denominator);
		prop.setProperty("vsdigrace.are." + preset, engine.speed.are);
		prop.setProperty("vsdigrace.areLine." + preset, engine.speed.areLine);
		prop.setProperty("vsdigrace.lineDelay." + preset, engine.speed.lineDelay);
		prop.setProperty("vsdigrace.lockDelay." + preset, engine.speed.lockDelay);
		prop.setProperty("vsdigrace.das." + preset, engine.speed.das);
	}

	/**
	 * Load settings not related to speeds
	 * @param engine GameEngine
	 * @param prop Property file to read from
	 */
	private void loadOtherSetting(GameEngine engine, CustomProperties prop) {
		int playerID = engine.playerID;
		goalLines[playerID] = prop.getProperty("vsdigrace.goalLines.p" + playerID, 18);
		garbagePercent[playerID] = prop.getProperty("vsdigrace.garbagePercent.p" + playerID, 100);
		bgmno = prop.getProperty("vsdigrace.bgmno", 0);
		enableSE[playerID] = prop.getProperty("vsdigrace.enableSE.p" + playerID, true);
		presetNumber[playerID] = prop.getProperty("vsdigrace.presetNumber.p" + playerID, 0);
	}

	/**
	 * Save settings not related to speeds
	 * @param engine GameEngine
	 * @param prop Property file to save to
	 */
	private void saveOtherSetting(GameEngine engine, CustomProperties prop) {
		int playerID = engine.playerID;
		prop.setProperty("vsdigrace.goalLines.p" + playerID, goalLines[playerID]);
		prop.setProperty("vsdigrace.garbagePercent.p" + playerID, garbagePercent[playerID]);
		prop.setProperty("vsdigrace.bgmno", bgmno);
		prop.setProperty("vsdigrace.enableSE.p" + playerID, enableSE[playerID]);
		prop.setProperty("vsdigrace.presetNumber.p" + playerID, presetNumber[playerID]);
	}

	/*
	 * Initialization for each player
	 */
	@Override
	public void playerInit(GameEngine engine, int playerID) {
		if(playerID == 1) {
			engine.randSeed = owner.engine[0].randSeed;
			engine.random = new Random(owner.engine[0].randSeed);
		}

		engine.framecolor = PLAYER_COLOR_FRAME[playerID];

		if(engine.owner.replayMode == false) {
			version = CURRENT_VERSION;
			loadOtherSetting(engine, engine.owner.modeConfig);
			loadPreset(engine, engine.owner.modeConfig, -1 - playerID);
		} else {
			version = owner.replayProp.getProperty("vsbattle.version", 0);
			loadOtherSetting(engine, engine.owner.replayProp);
			loadPreset(engine, engine.owner.replayProp, -1 - playerID);
		}
	}

	/*
	 * Called at settings screen
	 */
	@Override
	public boolean onSetting(GameEngine engine, int playerID) {
		// Menu
		if((engine.owner.replayMode == false) && (engine.statc[4] == 0)) {
			// Configuration changes
			int change = updateCursor(engine, 12, playerID);

			if(change != 0) {
				engine.playSE("change");

				int m = 1;
				if(engine.ctrl.isPress(Controller.BUTTON_E)) m = 100;
				if(engine.ctrl.isPress(Controller.BUTTON_F)) m = 1000;

				switch(menuCursor) {
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
				case 8:
					presetNumber[playerID] += change;
					if(presetNumber[playerID] < 0) presetNumber[playerID] = 99;
					if(presetNumber[playerID] > 99) presetNumber[playerID] = 0;
					break;
				case 9:
					goalLines[playerID] += change;
					if(goalLines[playerID] < 1) goalLines[playerID] = 18;
					if(goalLines[playerID] > 18) goalLines[playerID] = 1;
					break;
				case 10:
					garbagePercent[playerID] += change;
					if(garbagePercent[playerID] < 0) garbagePercent[playerID] = 100;
					if(garbagePercent[playerID] > 100) garbagePercent[playerID] = 0;
					break;
				case 11:
					enableSE[playerID] = !enableSE[playerID];
					break;
				case 12:
					bgmno += change;
					if(bgmno < 0) bgmno = BGMStatus.BGM_COUNT - 1;
					if(bgmno > BGMStatus.BGM_COUNT - 1) bgmno = 0;
					break;
				}
			}

			// Confirm
			if(engine.ctrl.isPush(Controller.BUTTON_A) && (menuTime >= 5)) {
				engine.playSE("decide");

				if(menuCursor == 7) {
					loadPreset(engine, owner.modeConfig, presetNumber[playerID]);
				} else if(menuCursor == 8) {
					savePreset(engine, owner.modeConfig, presetNumber[playerID]);
					receiver.saveModeConfig(owner.modeConfig);
				} else {
					saveOtherSetting(engine, owner.modeConfig);
					savePreset(engine, owner.modeConfig, -1 - playerID);
					receiver.saveModeConfig(owner.modeConfig);
					engine.statc[4] = 1;
				}
			}

			// Cancel
			if(engine.ctrl.isPush(Controller.BUTTON_B)) {
				engine.quitflag = true;
			}

			menuTime++;
		} else if(engine.statc[4] == 0) {
			// Replay start
			menuTime++;
			menuCursor = 0;

			if(menuTime >= 60) {
				menuCursor = 9;
			}
			if(menuTime >= 120) {
				engine.statc[4] = 1;
			}
		} else {
			// Start the game when both players are ready
			if((owner.engine[0].statc[4] == 1) && (owner.engine[1].statc[4] == 1) && (playerID == 1)) {
				owner.engine[0].stat = GameEngine.STAT_READY;
				owner.engine[1].stat = GameEngine.STAT_READY;
				owner.engine[0].resetStatc();
				owner.engine[1].resetStatc();
			}
			// Cancel
			else if(engine.ctrl.isPush(Controller.BUTTON_B)) {
				engine.statc[4] = 0;
			}
		}

		return true;
	}

	/*
	 * Settings screen
	 */
	@Override
	public void renderSetting(GameEngine engine, int playerID) {
		if(engine.statc[4] == 0) {
			if(menuCursor < 9) {
				drawMenu(engine, playerID, receiver, 0, EventReceiver.COLOR_ORANGE, 0,
						"GRAVITY", String.valueOf(engine.speed.gravity),
						"G-MAX", String.valueOf(engine.speed.denominator),
						"ARE", String.valueOf(engine.speed.are),
						"ARE LINE", String.valueOf(engine.speed.areLine),
						"LINE DELAY", String.valueOf(engine.speed.lineDelay),
						"LOCK DELAY", String.valueOf(engine.speed.lockDelay),
						"DAS", String.valueOf(engine.speed.das));
				drawMenu(engine, playerID, receiver, 14, EventReceiver.COLOR_GREEN, 7,
						"LOAD", String.valueOf(presetNumber[playerID]),
						"SAVE", String.valueOf(presetNumber[playerID]));
			} else {
				drawMenu(engine, playerID, receiver, 0, EventReceiver.COLOR_CYAN, 9,
						"GOAL", String.valueOf(goalLines[playerID]),
						"CHANGERATE", String.valueOf(garbagePercent[playerID]) + "%",
						"SE", GeneralUtil.getONorOFF(enableSE[playerID]));
				drawMenu(engine, playerID, receiver, 6, EventReceiver.COLOR_PINK, 12,
						"BGM", String.valueOf(bgmno));
			}
		} else {
			receiver.drawMenuFont(engine, playerID, 3, 10, "WAIT", EventReceiver.COLOR_YELLOW);
		}
	}

	/*
	 * Ready
	 */
	@Override
	public boolean onReady(GameEngine engine, int playerID) {
		if(engine.statc[0] == 0) {
			engine.createFieldIfNeeded();
			fillGarbage(engine, playerID);

			// Update meter
			int remainLines = getRemainGarbageLines(engine, playerID);
			engine.meterValue = remainLines * receiver.getBlockGraphicsHeight(engine, playerID);
			engine.meterColor = GameEngine.METER_COLOR_GREEN;
		}
		return false;
	}

	/*
	 * Called at game start
	 */
	@Override
	public void startGame(GameEngine engine, int playerID) {
		engine.enableSE = enableSE[playerID];
		if(playerID == 1) owner.bgmStatus.bgm = bgmno;

		engine.meterColor = GameEngine.METER_COLOR_GREEN;
		engine.meterValue = receiver.getMeterMax(engine);
	}

	/**
	 * Fill the playfield with garbage
	 * @param engine GameEngine
	 */
	private void fillGarbage(GameEngine engine, int playerID) {
		int w = engine.field.getWidth();
		int h = engine.field.getHeight();
		int hole = -1;

		for(int y = h - 1; y >= h - goalLines[playerID]; y--) {
			if((hole == -1) || (engine.random.nextInt(100) < garbagePercent[playerID])) {
				int newhole = -1;
				do {
					newhole = engine.random.nextInt(w);
				} while(newhole == hole);
				hole = newhole;
			}

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

			// Set connections
			if(receiver.isStickySkin(engine) && (y != h - 1)) {
				for(int x = 0; x < w; x++) {
					if(x != hole) {
						Block blk = engine.field.getBlock(x, y);
						if(blk != null) {
							if(!engine.field.getBlockEmpty(x-1, y)) blk.setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_LEFT, true);
							if(!engine.field.getBlockEmpty(x+1, y)) blk.setAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT, true);
						}
					}
				}
			}
		}
	}

	private int getRemainGarbageLines(GameEngine engine, int playerID) {
		if((engine == null) || (engine.field == null)) return -1;

		int w = engine.field.getWidth();
		int h = engine.field.getHeight();
		int lines = 0;
		boolean hasGemBlock = false;

		for(int y = h - 1; y >= h - goalLines[playerID]; y--) {
			if(!engine.field.getLineFlag(y)) {
				for(int x = 0; x < w; x++) {
					Block blk = engine.field.getBlock(x, y);

					if((blk != null) && (blk.isGemBlock())) {
						hasGemBlock = true;
					}
					if((blk != null) && (blk.getAttribute(Block.BLOCK_ATTRIBUTE_GARBAGE))) {
						lines++;
						break;
					}
				}
			}
		}

		if(!hasGemBlock) return 0;

		return lines;
	}

	/*
	 * Render score
	 */
	@Override
	public void renderLast(GameEngine engine, int playerID) {
		int enemyID = 0;
		if(playerID == 0) enemyID = 1;

		int x = receiver.getFieldDisplayPositionX(engine, playerID);
		int y = receiver.getFieldDisplayPositionY(engine, playerID);
		int fontColor = EventReceiver.COLOR_WHITE;

		int remainLines = Math.max(0, getRemainGarbageLines(engine, playerID));
		fontColor = EventReceiver.COLOR_WHITE;
		if((remainLines <= 14) && (remainLines > 0)) fontColor = EventReceiver.COLOR_YELLOW;
		if((remainLines <=  8) && (remainLines > 0)) fontColor = EventReceiver.COLOR_ORANGE;
		if((remainLines <=  4) && (remainLines > 0)) fontColor = EventReceiver.COLOR_RED;

		int enemyRemainLines = Math.max(0, getRemainGarbageLines(owner.engine[enemyID], enemyID));
		/*
		int fontColorEnemy = EventReceiver.COLOR_WHITE;
		if((enemyRemainLines <= 14) && (enemyRemainLines > 0)) fontColorEnemy = EventReceiver.COLOR_YELLOW;
		if((enemyRemainLines <=  8) && (enemyRemainLines > 0)) fontColorEnemy = EventReceiver.COLOR_ORANGE;
		if((enemyRemainLines <=  4) && (enemyRemainLines > 0)) fontColorEnemy = EventReceiver.COLOR_RED;
		*/

		// Lines left (bottom)
		String strLines = String.valueOf(remainLines);

		if(remainLines > 0) {
			if(strLines.length() == 1) {
				receiver.drawMenuFont(engine, playerID, 4, 21, strLines, fontColor, 2.0f);
			} else if(strLines.length() == 2) {
				receiver.drawMenuFont(engine, playerID, 3, 21, strLines, fontColor, 2.0f);
			} else if(strLines.length() == 3) {
				receiver.drawMenuFont(engine, playerID, 2, 21, strLines, fontColor, 2.0f);
			}
		}

		// 1st/2nd
		if(remainLines < enemyRemainLines)
			receiver.drawMenuFont(engine, playerID, -2, 22, "1ST", EventReceiver.COLOR_ORANGE);
		else if(remainLines > enemyRemainLines)
			receiver.drawMenuFont(engine, playerID, -2, 22, "2ND", EventReceiver.COLOR_WHITE);

		// Timer
		if(playerID == 0) {
			receiver.drawDirectFont(engine, playerID, 256, 16, GeneralUtil.getTime(engine.statistics.time));
		}

		// Normal layout
		if((owner.receiver.getNextDisplayType() != 2) && (playerID == 0)) {
			receiver.drawScoreFont(engine, playerID, 0, 2, "1P LINES", EventReceiver.COLOR_RED);
			receiver.drawScoreFont(engine, playerID, 0, 3, String.valueOf(owner.engine[0].statistics.lines));

			receiver.drawScoreFont(engine, playerID, 0, 5, "2P LINES", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 0, 6, String.valueOf(owner.engine[1].statistics.lines));

			if(!owner.replayMode) {
				receiver.drawScoreFont(engine, playerID, 0, 8, "1P WINS", EventReceiver.COLOR_RED);
				receiver.drawScoreFont(engine, playerID, 0, 9, String.valueOf(winCount[0]));

				receiver.drawScoreFont(engine, playerID, 0, 11, "2P WINS", EventReceiver.COLOR_BLUE);
				receiver.drawScoreFont(engine, playerID, 0, 12, String.valueOf(winCount[1]));
			}
		}

		// Big-side-next layout
		if(owner.receiver.getNextDisplayType() == 2) {
			int fontColor2 = (playerID == 0) ? EventReceiver.COLOR_RED : EventReceiver.COLOR_BLUE;

			if(!owner.replayMode) {
				receiver.drawDirectFont(engine, playerID, x - 44, y + 190, "WINS", fontColor2, 0.5f);
				if(winCount[playerID] >= 10)
					receiver.drawDirectFont(engine, playerID, x - 44, y + 204, String.valueOf(winCount[playerID]));
				else
					receiver.drawDirectFont(engine, playerID, x - 36, y + 204, String.valueOf(winCount[playerID]));
			}
		}
	}

	/*
	 * Calculate score
	 */
	@Override
	public void calcScore(GameEngine engine, int playerID, int lines) {
		int enemyID = 0;
		if(playerID == 0) enemyID = 1;

		// Update meter
		int remainLines = getRemainGarbageLines(engine, playerID);
		engine.meterValue = remainLines * receiver.getBlockGraphicsHeight(engine, playerID);
		if(remainLines <= 14) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
		if(remainLines <= 8) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
		if(remainLines <= 4) engine.meterColor = GameEngine.METER_COLOR_RED;

		// Game completed
		if((lines > 0) && (remainLines <= 0)) {
			engine.timerActive = false;
			owner.engine[enemyID].stat = GameEngine.STAT_GAMEOVER;
			owner.engine[enemyID].resetStatc();
		}
	}

	@Override
	public void onLast(GameEngine engine, int playerID) {
		// Game End
		if((playerID == 1) && (owner.engine[0].gameActive)) {
			if((owner.engine[0].stat == GameEngine.STAT_GAMEOVER) && (owner.engine[1].stat == GameEngine.STAT_GAMEOVER)) {
				// Draw
				winnerID = -1;
				owner.engine[0].gameEnded();
				owner.engine[1].gameEnded();
				owner.bgmStatus.bgm = BGMStatus.BGM_NOTHING;
			} else if((owner.engine[0].stat != GameEngine.STAT_GAMEOVER) && (owner.engine[1].stat == GameEngine.STAT_GAMEOVER)) {
				// 1P win
				winnerID = 0;
				owner.engine[0].gameEnded();
				owner.engine[1].gameEnded();
				owner.engine[0].stat = GameEngine.STAT_EXCELLENT;
				owner.engine[0].resetStatc();
				owner.engine[0].statc[1] = 1;
				owner.bgmStatus.bgm = BGMStatus.BGM_NOTHING;
				if(!owner.replayMode) winCount[0]++;
			} else if((owner.engine[0].stat == GameEngine.STAT_GAMEOVER) && (owner.engine[1].stat != GameEngine.STAT_GAMEOVER)) {
				// 2P win
				winnerID = 1;
				owner.engine[0].gameEnded();
				owner.engine[1].gameEnded();
				owner.engine[1].stat = GameEngine.STAT_EXCELLENT;
				owner.engine[1].resetStatc();
				owner.engine[1].statc[1] = 1;
				owner.bgmStatus.bgm = BGMStatus.BGM_NOTHING;
				if(!owner.replayMode) winCount[1]++;
			}
		}
	}

	/*
	 * Render results screen
	 */
	@Override
	public void renderResult(GameEngine engine, int playerID) {
		receiver.drawMenuFont(engine, playerID, 0, 0, "RESULT", EventReceiver.COLOR_ORANGE);
		if(winnerID == -1) {
			receiver.drawMenuFont(engine, playerID, 6, 1, "DRAW", EventReceiver.COLOR_GREEN);
		} else if(winnerID == playerID) {
			receiver.drawMenuFont(engine, playerID, 6, 1, "WIN!", EventReceiver.COLOR_YELLOW);
		} else {
			receiver.drawMenuFont(engine, playerID, 6, 1, "LOSE", EventReceiver.COLOR_WHITE);
		}
		drawResultStats(engine, playerID, receiver, 2, EventReceiver.COLOR_ORANGE,
				STAT_LINES, STAT_PIECE, STAT_LPM, STAT_PPS, STAT_TIME);
	}

	/*
	 * Called when saving replay
	 */
	@Override
	public void saveReplay(GameEngine engine, int playerID, CustomProperties prop) {
		saveOtherSetting(engine, owner.replayProp);
		savePreset(engine, owner.replayProp, -1 - playerID);
		owner.replayProp.setProperty("vsdigrace.version", version);
	}
}
