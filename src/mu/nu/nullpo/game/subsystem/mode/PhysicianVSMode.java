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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import mu.nu.nullpo.game.component.BGMStatus;
import mu.nu.nullpo.game.component.Block;
import mu.nu.nullpo.game.component.Controller;
import mu.nu.nullpo.game.component.Field;
import mu.nu.nullpo.game.component.Piece;
import mu.nu.nullpo.game.event.EventReceiver;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.util.CustomProperties;
import mu.nu.nullpo.util.GeneralUtil;

/**
 * PHYSICIAN VS-BATTLE mode (beta)
 */
public class PhysicianVSMode extends AbstractMode {
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
	//private static final int[] BASE_SPEEDS = {10, 20, 25};

	/** Names of speed settings */
	private static final String[] SPEED_NAME = {"LOW", "MED", "HI"};

	/** Colors for speed settings */
	private static final int[] SPEED_COLOR =
	{
		EventReceiver.COLOR_BLUE,
		EventReceiver.COLOR_YELLOW,
		EventReceiver.COLOR_RED
	};

	/** Number of players */
	private static final int MAX_PLAYERS = 2;

	/** Each player's frame color */
	private final int[] PLAYER_COLOR_FRAME = {GameEngine.FRAME_COLOR_RED, GameEngine.FRAME_COLOR_BLUE};

	/** 溜まっているojama blockのcount */
	//private int[] garbage;

	/** 送ったojama blockのcount */
	//private int[] garbageSent;

	/** Time to display the most recent increase in score */
	private int[] scgettime;

	/** 使用するBGM */
	private int bgmno;

	/** Sound effectsON/OFF */
	private boolean[] enableSE;

	/** Map使用 flag */
	private boolean[] useMap;

	/** 使用するMapセット number */
	private int[] mapSet;

	/** Map number(-1でランダム) */
	private int[] mapNumber;

	/** Last preset number used */
	private int[] presetNumber;

	/** 勝者 */
	private int winnerID;

	/** MapセットのProperty file */
	private CustomProperties[] propMap;

	/** MaximumMap number */
	private int[] mapMaxNo;

	/** バックアップ用field (Mapをリプレイに保存するときに使用) */
	private Field[] fldBackup;

	/** Map選択用乱count */
	private Random randMap;

	/** Version */
	private int version;

	/** Flag for all clear */
	//private boolean[] zenKeshi;

	/** Amount of points earned from most recent clear */
	private int[] lastscore;

	/** Amount of garbage added in current chain */
	//private int[] garbageAdd;

	/** Score */
	private int[] score;

	/** Number of initial gem blocks */
	private int[] hoverBlocks;

	/** Speed mode */
	private int[] speed;

	/** Number gem blocks cleared in current chain */
	private int[] gemsClearedChainTotal;

	/** Each player's remaining gem count */
	private int[] rest;

	/** Each player's garbage block colors to be dropped */
	private ArrayList<Integer>[] garbageColors;

	/** Flash/normal mode settings */
	private boolean[] flash;

	/*
	 * Mode name
	 */
	@Override
	public String getName() {
		return "PHYSICIAN VS-BATTLE (RC1)";
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
	 * Game style
	 */
	@Override
	public int getGameStyle() {
		return GameEngine.GAMESTYLE_PHYSICIAN;
	}

	/*
	 * Mode initialization
	 */
	@Override
	public void modeInit(GameManager manager) {
		owner = manager;
		receiver = owner.receiver;

		//garbage = new int[MAX_PLAYERS];
		//garbageSent = new int[MAX_PLAYERS];

		scgettime = new int[MAX_PLAYERS];
		bgmno = 0;
		enableSE = new boolean[MAX_PLAYERS];
		useMap = new boolean[MAX_PLAYERS];
		mapSet = new int[MAX_PLAYERS];
		mapNumber = new int[MAX_PLAYERS];
		presetNumber = new int[MAX_PLAYERS];
		propMap = new CustomProperties[MAX_PLAYERS];
		mapMaxNo = new int[MAX_PLAYERS];
		fldBackup = new Field[MAX_PLAYERS];
		randMap = new Random();

		lastscore = new int[MAX_PLAYERS];
		//garbageAdd = new int[MAX_PLAYERS];
		score = new int[MAX_PLAYERS];
		hoverBlocks = new int[MAX_PLAYERS];
		speed = new int[MAX_PLAYERS];
		gemsClearedChainTotal = new int[MAX_PLAYERS];
		rest = new int[MAX_PLAYERS];
		garbageColors = new ArrayList[MAX_PLAYERS];
		flash = new boolean[MAX_PLAYERS];

		winnerID = -1;
	}

	/**
	 * Read speed presets
	 * @param engine GameEngine
	 * @param prop Property file to read from
	 * @param preset Preset number
	 */
	private void loadPreset(GameEngine engine, CustomProperties prop, int preset) {
		engine.speed.gravity = prop.getProperty("physicianvs.gravity." + preset, 4);
		engine.speed.denominator = prop.getProperty("physicianvs.denominator." + preset, 256);
		engine.speed.are = prop.getProperty("physicianvs.are." + preset, 24);
		engine.speed.areLine = prop.getProperty("physicianvs.areLine." + preset, 24);
		engine.speed.lineDelay = prop.getProperty("physicianvs.lineDelay." + preset, 10);
		engine.speed.lockDelay = prop.getProperty("physicianvs.lockDelay." + preset, 30);
		engine.speed.das = prop.getProperty("physicianvs.das." + preset, 14);
	}

	/**
	 * Save speed presets
	 * @param engine GameEngine
	 * @param prop Property file to save to
	 * @param preset Preset number
	 */
	private void savePreset(GameEngine engine, CustomProperties prop, int preset) {
		prop.setProperty("physicianvs.gravity." + preset, engine.speed.gravity);
		prop.setProperty("physicianvs.denominator." + preset, engine.speed.denominator);
		prop.setProperty("physicianvs.are." + preset, engine.speed.are);
		prop.setProperty("physicianvs.areLine." + preset, engine.speed.areLine);
		prop.setProperty("physicianvs.lineDelay." + preset, engine.speed.lineDelay);
		prop.setProperty("physicianvs.lockDelay." + preset, engine.speed.lockDelay);
		prop.setProperty("physicianvs.das." + preset, engine.speed.das);
	}

	/**
	 * Load settings not related to speeds
	 * @param engine GameEngine
	 * @param prop Property file to read from
	 */
	private void loadOtherSetting(GameEngine engine, CustomProperties prop) {
		int playerID = engine.playerID;
		bgmno = prop.getProperty("physicianvs.bgmno", 0);
		enableSE[playerID] = prop.getProperty("physicianvs.enableSE.p" + playerID, true);
		useMap[playerID] = prop.getProperty("physicianvs.useMap.p" + playerID, false);
		mapSet[playerID] = prop.getProperty("physicianvs.mapSet.p" + playerID, 0);
		mapNumber[playerID] = prop.getProperty("physicianvs.mapNumber.p" + playerID, -1);
		presetNumber[playerID] = prop.getProperty("physicianvs.presetNumber.p" + playerID, 0);
		speed[playerID] = prop.getProperty("physicianvs.speed.p" + playerID, 1);
		hoverBlocks[playerID] = prop.getProperty("physicianvs.hoverBlocks.p" + playerID, 40);
		flash[playerID] = prop.getProperty("physicianvs.flash.p" + playerID, false);
	}

	/**
	 * Save settings not related to speeds
	 * @param engine GameEngine
	 * @param prop Property file to save to
	 */
	private void saveOtherSetting(GameEngine engine, CustomProperties prop) {
		int playerID = engine.playerID;
		prop.setProperty("physicianvs.bgmno", bgmno);
		prop.setProperty("physicianvs.enableSE.p" + playerID, enableSE[playerID]);
		prop.setProperty("physicianvs.useMap.p" + playerID, useMap[playerID]);
		prop.setProperty("physicianvs.mapSet.p" + playerID, mapSet[playerID]);
		prop.setProperty("physicianvs.mapNumber.p" + playerID, mapNumber[playerID]);
		prop.setProperty("physicianvs.presetNumber.p" + playerID, presetNumber[playerID]);
		prop.setProperty("physicianvs.speed.p" + playerID, speed[playerID]);
		prop.setProperty("physicianvs.hoverBlocks.p" + playerID, hoverBlocks[playerID]);
		prop.setProperty("physicianvs.flash.p" + playerID, flash[playerID]);
	}

	/**
	 * Map読み込み
	 * @param field field
	 * @param prop Property file to read from
	 * @param preset 任意のID
	 */
	private void loadMap(Field field, CustomProperties prop, int id) {
		field.reset();
		//field.readProperty(prop, id);
		field.stringToField(prop.getProperty("map." + id, ""));
		field.setAllAttribute(Block.BLOCK_ATTRIBUTE_VISIBLE, true);
		field.setAllAttribute(Block.BLOCK_ATTRIBUTE_OUTLINE, true);
		field.setAllAttribute(Block.BLOCK_ATTRIBUTE_SELFPLACED, false);
	}

	/**
	 * Map保存
	 * @param field field
	 * @param prop Property file to save to
	 * @param id 任意のID
	 */
	private void saveMap(Field field, CustomProperties prop, int id) {
		//field.writeProperty(prop, id);
		prop.setProperty("map." + id, field.fieldToString());
	}

	/**
	 * プレビュー用にMapを読み込み
	 * @param engine GameEngine
	 * @param playerID Player number
	 * @param id MapID
	 * @param forceReload trueにするとMapファイルを強制再読み込み
	 */
	private void loadMapPreview(GameEngine engine, int playerID, int id, boolean forceReload) {
		if((propMap[playerID] == null) || (forceReload)) {
			mapMaxNo[playerID] = 0;
			propMap[playerID] = receiver.loadProperties("config/map/vsbattle/" + mapSet[playerID] + ".map");
		}

		if((propMap[playerID] == null) && (engine.field != null)) {
			engine.field.reset();
		} else if(propMap[playerID] != null) {
			mapMaxNo[playerID] = propMap[playerID].getProperty("map.maxMapNumber", 0);
			engine.createFieldIfNeeded();
			loadMap(engine.field, propMap[playerID], id);
			engine.field.setAllSkin(engine.getSkin());
		}
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

		//garbage[playerID] = 0;
		//garbageSent[playerID] = 0;
		score[playerID] = 0;
		scgettime[playerID] = 0;
		gemsClearedChainTotal[playerID] = 0;
		rest[playerID] = 0;

		if(engine.owner.replayMode == false) {
			loadOtherSetting(engine, engine.owner.modeConfig);
			loadPreset(engine, engine.owner.modeConfig, -1 - playerID);
			version = CURRENT_VERSION;
		} else {
			loadOtherSetting(engine, engine.owner.replayProp);
			loadPreset(engine, engine.owner.replayProp, -1 - playerID);
			version = owner.replayProp.getProperty("physicianvs.version", 0);
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
			int change = updateCursor(engine, 16);

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
					speed[playerID] += change;
					if(speed[playerID] < 0) speed[playerID] = 2;
					if(speed[playerID] > 2) speed[playerID] = 0;
					break;
				case 10:
					if (m >= 10) hoverBlocks[playerID] += change*10;
					else hoverBlocks[playerID] += change;
					if(hoverBlocks[playerID] < 1) hoverBlocks[playerID] = 99;
					if(hoverBlocks[playerID] > 99) hoverBlocks[playerID] = 1;
					break;
				case 11:
					flash[playerID] = !flash[playerID];
					break;
				case 12:
					enableSE[playerID] = !enableSE[playerID];
					break;
				case 13:
					bgmno += change;
					if(bgmno < 0) bgmno = BGMStatus.BGM_COUNT - 1;
					if(bgmno > BGMStatus.BGM_COUNT - 1) bgmno = 0;
					break;
				case 14:
					useMap[playerID] = !useMap[playerID];
					if(!useMap[playerID]) {
						if(engine.field != null) engine.field.reset();
					} else {
						loadMapPreview(engine, playerID, (mapNumber[playerID] < 0) ? 0 : mapNumber[playerID], true);
					}
					break;
				case 15:
					mapSet[playerID] += change;
					if(mapSet[playerID] < 0) mapSet[playerID] = 99;
					if(mapSet[playerID] > 99) mapSet[playerID] = 0;
					if(useMap[playerID]) {
						mapNumber[playerID] = -1;
						loadMapPreview(engine, playerID, (mapNumber[playerID] < 0) ? 0 : mapNumber[playerID], true);
					}
					break;
				case 16:
					if(useMap[playerID]) {
						mapNumber[playerID] += change;
						if(mapNumber[playerID] < -1) mapNumber[playerID] = mapMaxNo[playerID] - 1;
						if(mapNumber[playerID] > mapMaxNo[playerID] - 1) mapNumber[playerID] = -1;
						loadMapPreview(engine, playerID, (mapNumber[playerID] < 0) ? 0 : mapNumber[playerID], true);
					} else {
						mapNumber[playerID] = -1;
					}
					break;
				}
			}

			// 決定
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

			// プレビュー用Map読み込み
			if(useMap[playerID] && (menuTime == 0)) {
				loadMapPreview(engine, playerID, (mapNumber[playerID] < 0) ? 0 : mapNumber[playerID], true);
			}

			// Random map preview
			if(useMap[playerID] && (propMap[playerID] != null) && (mapNumber[playerID] < 0)) {
				if(menuTime % 30 == 0) {
					engine.statc[5]++;
					if(engine.statc[5] >= mapMaxNo[playerID]) engine.statc[5] = 0;
					loadMapPreview(engine, playerID, engine.statc[5], false);
				}
			}

			menuTime++;
		} else if(engine.statc[4] == 0) {
			menuTime++;
			menuCursor = 0;

			if(menuTime >= 120)
				engine.statc[4] = 1;
			else if(menuTime >= 60)
				menuCursor = 9;
		} else {
			// 開始
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
	 * 設定画面の描画
	 */
	@Override
	public void renderSetting(GameEngine engine, int playerID) {
		if(engine.statc[4] == 0) {
			if(menuCursor < 9) {
				initMenu(EventReceiver.COLOR_ORANGE, 0);
				drawMenu(engine, playerID, receiver,
						"GRAVITY", String.valueOf(engine.speed.gravity),
						"G-MAX", String.valueOf(engine.speed.denominator),
						"ARE", String.valueOf(engine.speed.are),
						"ARE LINE", String.valueOf(engine.speed.areLine),
						"LINE DELAY", String.valueOf(engine.speed.lineDelay),
						"LOCK DELAY", String.valueOf(engine.speed.lockDelay),
						"DAS", String.valueOf(engine.speed.das));
				menuColor = EventReceiver.COLOR_GREEN;
				drawMenu(engine, playerID, receiver,
						"LOAD", String.valueOf(presetNumber[playerID]),
						"SAVE", String.valueOf(presetNumber[playerID]));
			} else {
				initMenu(EventReceiver.COLOR_CYAN, 9);
				drawMenu(engine, playerID, receiver,
						"SPEED", SPEED_NAME[speed[playerID]],
						"VIRUS", String.valueOf(hoverBlocks[playerID]),
						"MODE", (flash[playerID] ? "FLASH" : "NORMAL"));
				menuColor = EventReceiver.COLOR_PINK;
				drawMenu(engine, playerID, receiver,
						"SE", GeneralUtil.getONorOFF(enableSE[playerID]),
						"BGM", String.valueOf(bgmno));
				menuColor = EventReceiver.COLOR_CYAN;
				drawMenu(engine, playerID, receiver,
						"USE MAP", GeneralUtil.getONorOFF(useMap[playerID]),
						"MAP SET", String.valueOf(mapSet[playerID]),
						"MAP NO.", (mapNumber[playerID] < 0) ? "RANDOM" : mapNumber[playerID]+"/"+(mapMaxNo[playerID]-1));
			}
		} else {
			receiver.drawMenuFont(engine, playerID, 3, 10, "WAIT", EventReceiver.COLOR_YELLOW);
		}
	}

	/*
	 * Called for initialization during Ready (before initialization)
	 */
	@Override
	public boolean onReady(GameEngine engine, int playerID) {
		if(engine.statc[0] == 0) {
			// Map読み込み・リプレイ保存用にバックアップ
			if(useMap[playerID]) {
				if(owner.replayMode) {
					engine.createFieldIfNeeded();
					loadMap(engine.field, owner.replayProp, playerID);
					engine.field.setAllSkin(engine.getSkin());
				} else {
					if(propMap[playerID] == null) {
						propMap[playerID] = receiver.loadProperties("config/map/vsbattle/" + mapSet[playerID] + ".map");
					}

					if(propMap[playerID] != null) {
						engine.createFieldIfNeeded();

						if(mapNumber[playerID] < 0) {
							if((playerID == 1) && (useMap[0]) && (mapNumber[0] < 0)) {
								engine.field.copy(owner.engine[0].field);
							} else {
								int no = (mapMaxNo[playerID] < 1) ? 0 : randMap.nextInt(mapMaxNo[playerID]);
								loadMap(engine.field, propMap[playerID], no);
							}
						} else {
							loadMap(engine.field, propMap[playerID], mapNumber[playerID]);
						}

						engine.field.setAllSkin(engine.getSkin());
						fldBackup[playerID] = new Field(engine.field);
					}
				}
			} else if(engine.field != null) {
				engine.field.reset();
			}
			if(hoverBlocks[playerID] > 0) {
				engine.createFieldIfNeeded();
				int minY = 6;
				if (hoverBlocks[playerID] >= 80) minY = 3;
				else if (hoverBlocks[playerID] >= 72) minY = 4;
				else if (hoverBlocks[playerID] >= 64) minY = 5;
				if (flash[playerID])
				{
					engine.field.addRandomHoverBlocks(engine, hoverBlocks[playerID], BLOCK_COLORS, minY, true, true);
					engine.field.setAllSkin(12);
				}
				else
					engine.field.addRandomHoverBlocks(engine, hoverBlocks[playerID], HOVER_BLOCK_COLORS, minY, true);
			}
		}

		return false;
	}

	/*
	 * Called at game start
	 */
	@Override
	public void startGame(GameEngine engine, int playerID) {
		engine.b2bEnable = false;
		engine.comboType = GameEngine.COMBO_TYPE_DISABLE;
		engine.blockOutlineType = GameEngine.BLOCK_OUTLINE_CONNECT;
		engine.enableSE = enableSE[playerID];
		if(playerID == 1) owner.bgmStatus.bgm = bgmno;

		engine.tspinAllowKick = false;
		engine.tspinEnable = false;
		engine.useAllSpinBonus = false;
	}

	/*
	 * Render score
	 */
	@Override
	public void renderLast(GameEngine engine, int playerID) {
		int fldPosX = receiver.getFieldDisplayPositionX(engine, playerID);
		int fldPosY = receiver.getFieldDisplayPositionY(engine, playerID);
		int playerColor = (playerID == 0) ? EventReceiver.COLOR_RED : EventReceiver.COLOR_BLUE;
		int tempX = 0;

		// Timer
		if(playerID == 0) {
			receiver.drawDirectFont(engine, playerID, 256, 16, GeneralUtil.getTime(engine.statistics.time));
		}

		if(engine.gameStarted) {
			// Rest
			receiver.drawDirectFont(engine, playerID, fldPosX + 160, fldPosY + 241, "REST", playerColor, 0.5f);
			tempX = (rest[playerID] < 10) ? 8 : 0;
			receiver.drawDirectFont(engine, playerID, fldPosX + 160 + tempX, fldPosY + 257,
					String.valueOf(rest[playerID]), (rest[playerID] <= (flash[playerID] ? 1 : 3)),
					EventReceiver.COLOR_WHITE, EventReceiver.COLOR_RED);

			// Speed
			receiver.drawDirectFont(engine, playerID, fldPosX + 156, fldPosY + 280, "SPEED", playerColor, 0.5f);
			receiver.drawDirectFont(engine, playerID, fldPosX + 152, fldPosY + 296, SPEED_NAME[speed[playerID]], SPEED_COLOR[speed[playerID]]);
		}

		/*
		if(playerID == 0) {
			receiver.drawScoreFont(engine, playerID, -1, 0, "PHYSICIAN VS", EventReceiver.COLOR_GREEN);

			receiver.drawScoreFont(engine, playerID, -1, 2, "REST", EventReceiver.COLOR_PURPLE);
			receiver.drawScoreFont(engine, playerID, -1, 3, "1P:", EventReceiver.COLOR_RED);
			receiver.drawScoreFont(engine, playerID, 3, 3, String.valueOf(rest[0]), (rest[0] <= (flash[playerID] ? 1 : 3)));
			receiver.drawScoreFont(engine, playerID, -1, 4, "2P:", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 3, 4, String.valueOf(rest[1]), (rest[1] <= (flash[playerID] ? 1 : 3)));

			receiver.drawScoreFont(engine, playerID, -1, 6, "SPEED", EventReceiver.COLOR_GREEN);
			receiver.drawScoreFont(engine, playerID, -1, 7, "1P:", EventReceiver.COLOR_RED);
			receiver.drawScoreFont(engine, playerID,  3, 7, SPEED_NAME[speed[0]], SPEED_COLOR[speed[0]]);
			receiver.drawScoreFont(engine, playerID, -1, 8, "2P:", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID,  3, 8, SPEED_NAME[speed[1]], SPEED_COLOR[speed[1]]);

			receiver.drawScoreFont(engine, playerID, -1, 10, "SCORE", EventReceiver.COLOR_PURPLE);
			receiver.drawScoreFont(engine, playerID, -1, 11, "1P: " + String.valueOf(score[0]), EventReceiver.COLOR_RED);
			receiver.drawScoreFont(engine, playerID, -1, 12, "2P: " + String.valueOf(score[1]), EventReceiver.COLOR_BLUE);

			receiver.drawScoreFont(engine, playerID, -1, 14, "TIME", EventReceiver.COLOR_GREEN);
			receiver.drawScoreFont(engine, playerID, -1, 15, GeneralUtil.getTime(engine.statistics.time));
		}
		*/
	}

	/*
	 * Calculate score
	 */
	@Override
	public void calcScore(GameEngine engine, int playerID, int lines) {
		if (engine.field == null)
			return;
		int gemsCleared = engine.field.gemsCleared;
		if (gemsCleared > 0 && lines > 0) {
			int pts = 0;
			while (gemsCleared > 0 && gemsClearedChainTotal[playerID] < 5)
			{
				pts += 1 << gemsClearedChainTotal[playerID];
				gemsClearedChainTotal[playerID]++;
				gemsCleared--;
			}
			if (gemsClearedChainTotal[playerID] >= 5)
				pts += gemsCleared << 5;
			pts *= (speed[playerID]+1) * 100;
			gemsClearedChainTotal[playerID] += gemsCleared;
			lastscore[playerID] = pts;
			scgettime[playerID] = 120;
			engine.statistics.scoreFromLineClear += pts;
			engine.statistics.score += pts;
			score[playerID] += pts;
			engine.playSE("gem");
			setSpeed(engine);
		}
		else if (lines == 0 && !engine.field.canCascade() && garbageColors[playerID] != null)
		{
			if (garbageCheck(engine, playerID))
			{
				engine.stat = GameEngine.STAT_LINECLEAR;
				engine.statc[0] = engine.getLineDelay();
			}
		}
	}

	/**
	 * Set the gravity rate
	 * @param engine GameEngine
	 */
	public void setSpeed(GameEngine engine) {
		/*
		engine.speed.gravity = BASE_SPEEDS[speed[playerID]]*(10+(engine.statistics.totalPieceLocked/10));
		engine.speed.denominator = 3600;
		*/
	}

	public boolean lineClearEnd(GameEngine engine, int playerID) {
		if (engine.field == null)
			return false;

		int enemyID = 0;
		if(playerID == 0) enemyID = 1;

		ArrayList<Integer> cleared = engine.field.lineColorsCleared;
		engine.field.lineColorsCleared = null;
		if (cleared != null)
			if (cleared.size() > 1)
			{
				if (garbageColors[enemyID] == null)
					garbageColors[enemyID] = cleared;
				else
					garbageColors[enemyID].addAll(cleared);
			}

		return garbageCheck(engine, playerID);
	}

	private boolean garbageCheck(GameEngine engine, int playerID) {
		if (garbageColors[playerID] == null)
			return false;
		int size = garbageColors[playerID].size();
		if (size < 2)
			return false;
		Collections.shuffle(garbageColors[playerID], engine.random);
		int[] colors = new int[4];
		if (size >= 4)
			for (int x = 0; x < 4; x++)
				colors[x] = garbageColors[playerID].get(x).intValue();
		else if (size == 3)
		{
			int skipSlot = engine.random.nextInt(4);
			colors[skipSlot] = -1;
			int i;
			for (int x = 0; x < 3; x++)
			{
				i = x;
				if (x >= skipSlot)
					i++;
				colors[i] = garbageColors[playerID].get(x).intValue();
			}
		}
		else
		{
			int firstSlot = engine.random.nextInt(4);
			colors[firstSlot] = garbageColors[playerID].get(0).intValue();
			int secondSlot = firstSlot + 2;
			if (secondSlot > 3)
				secondSlot -= 4;
			colors[secondSlot] = garbageColors[playerID].get(1).intValue();
		}
		int shift = engine.random.nextInt(2);
		int y = (-1 * engine.field.getHiddenHeight());
		for (int x = 0; x < 4; x++)
			if (colors[x] != -1)
			{
				engine.field.garbageDropPlace(2*x+shift, y, false, 0, colors[x]);
				engine.field.getBlock(2*x+shift, y).skin = engine.getSkin();
			}
		garbageColors[playerID] = null;
		return true;
	}

	/*
	 * Called after every frame
	 */
	@Override
	public void onLast(GameEngine engine, int playerID) {
		scgettime[playerID]++;

		if (engine.field != null)
		{
			int rest = engine.field.getHowManyGems();
			if (flash[playerID])
			{
				engine.meterValue = (rest * receiver.getMeterMax(engine)) / 3;
				if (rest == 1) engine.meterColor = GameEngine.METER_COLOR_GREEN;
				else if (rest == 2) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
				else engine.meterColor = GameEngine.METER_COLOR_RED;
			}
			else
			{
				engine.meterValue = (rest * receiver.getMeterMax(engine)) / hoverBlocks[playerID];
				if (rest <= 3) engine.meterColor = GameEngine.METER_COLOR_GREEN;
				else if (rest < (hoverBlocks[playerID] >> 2)) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
				else if (rest < (hoverBlocks[playerID] >> 1)) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
				else engine.meterColor = GameEngine.METER_COLOR_RED;
			}
		}

		// 決着
		if((playerID == 1) && (owner.engine[0].gameActive)) {
			boolean p1Lose = (owner.engine[0].stat == GameEngine.STAT_GAMEOVER);
			if (!p1Lose && owner.engine[1].field != null)
			{
				rest[1] = owner.engine[1].field.getHowManyGems();
				p1Lose = (rest[1] == 0);
			}
			boolean p2Lose = (owner.engine[1].stat == GameEngine.STAT_GAMEOVER);
			if (!p2Lose && owner.engine[0].field != null)
			{
				rest[0] = owner.engine[0].field.getHowManyGems();
				p2Lose = (rest[0] == 0);
			}
			if(p1Lose && p2Lose) {
				// Draw
				winnerID = -1;
				owner.engine[0].stat = GameEngine.STAT_GAMEOVER;
				owner.engine[1].stat = GameEngine.STAT_GAMEOVER;
			} else if(p2Lose && !p1Lose) {
				// 1P win
				winnerID = 0;
				owner.engine[0].stat = GameEngine.STAT_EXCELLENT;
				owner.engine[1].stat = GameEngine.STAT_GAMEOVER;
			} else if(p1Lose && !p2Lose) {
				// 2P win
				winnerID = 1;
				owner.engine[0].stat = GameEngine.STAT_GAMEOVER;
				owner.engine[1].stat = GameEngine.STAT_EXCELLENT;
			}
			if (p1Lose || p2Lose) {
				owner.engine[0].gameEnded();
				owner.engine[1].gameEnded();
				owner.engine[0].resetStatc();
				owner.engine[1].resetStatc();
				owner.engine[0].statc[1] = 1;
				owner.engine[1].statc[1] = 1;
				owner.bgmStatus.bgm = BGMStatus.BGM_NOTHING;
			}
		}
	}

	/*
	 * Render results screen
	 */
	@Override
	public void renderResult(GameEngine engine, int playerID) {
		receiver.drawMenuFont(engine, playerID, 0, 1, "RESULT", EventReceiver.COLOR_ORANGE);
		if(winnerID == -1) {
			receiver.drawMenuFont(engine, playerID, 6, 2, "DRAW", EventReceiver.COLOR_GREEN);
		} else if(winnerID == playerID) {
			receiver.drawMenuFont(engine, playerID, 6, 2, "WIN!", EventReceiver.COLOR_YELLOW);
		} else {
			receiver.drawMenuFont(engine, playerID, 6, 2, "LOSE", EventReceiver.COLOR_WHITE);
		}

		drawResultStats(engine, playerID, receiver, 3, EventReceiver.COLOR_ORANGE,
				STAT_LINES, STAT_PIECE, STAT_LPM, STAT_PPS, STAT_TIME);
		/*
		float apm = (float)(garbageSent[playerID] * 3600) / (float)(engine.statistics.time);
		drawResult(engine, playerID, receiver, 3, EventReceiver.COLOR_ORANGE,
				"ATTACK", String.format("%10d", garbageSent[playerID]),
				"ATTACK/MIN", String.format("%10g", apm));
		*/
	}

	/*
	 * Called when saving replay
	 */
	@Override
	public void saveReplay(GameEngine engine, int playerID, CustomProperties prop) {
		saveOtherSetting(engine, owner.replayProp);
		savePreset(engine, owner.replayProp, -1 - playerID);

		if(useMap[playerID] && (fldBackup[playerID] != null)) {
			saveMap(fldBackup[playerID], owner.replayProp, playerID);
		}

		owner.replayProp.setProperty("physicianvs.version", version);
	}
}
