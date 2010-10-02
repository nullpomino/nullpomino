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
import mu.nu.nullpo.game.component.Field;
import mu.nu.nullpo.game.component.Piece;
import mu.nu.nullpo.game.event.EventReceiver;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.gui.slick.GameKey;
import mu.nu.nullpo.util.CustomProperties;
import mu.nu.nullpo.util.GeneralUtil;

import org.apache.log4j.Logger;

/**
 * PRACTICE Mode
 */
public class PracticeMode extends DummyMode {
	/** Log */
	static Logger log = Logger.getLogger(PracticeMode.class);

	/** Current version */
	private static final int CURRENT_VERSION = 4;

	/** Most recent scoring event typeの定count */
	private static final int EVENT_NONE = 0,
							 EVENT_SINGLE = 1,
							 EVENT_DOUBLE = 2,
							 EVENT_TRIPLE = 3,
							 EVENT_FOUR = 4,
							 EVENT_TSPIN_ZERO_MINI = 5,
							 EVENT_TSPIN_ZERO = 6,
							 EVENT_TSPIN_SINGLE_MINI = 7,
							 EVENT_TSPIN_SINGLE = 8,
							 EVENT_TSPIN_DOUBLE_MINI = 9,
							 EVENT_TSPIN_DOUBLE = 10,
							 EVENT_TSPIN_TRIPLE = 11;

	/** Comboで手に入る point */
	private static final int COMBO_GOAL_TABLE[] = {0,0,1,1,2,2,3,3,4,4,4,5};

	/** Levelタイプの定count */
	private static final int LEVELTYPE_NONE = 0,
							 LEVELTYPE_10LINES = 1,
							 LEVELTYPE_POINTS = 2,
							 LEVELTYPE_MANIA = 3,
							 LEVELTYPE_MANIAPLUS = 4,
							 LEVELTYPE_MAX = 5;

	/** 裏段位のName */
	private static final String[] tableSecretGradeName =
	{
		 "9",  "8",  "7",  "6",  "5",  "4",  "3",  "2",  "1",	//  0～ 8
		"S1", "S2", "S3", "S4", "S5", "S6", "S7", "S8", "S9",	//  9～17
		"GM"													// 18
	};

	/** Levelタイプの表示名 */
	private static final String[] LEVELTYPE_STRING = {"NONE", "10LINES", "POINTS", "MANIA", "MANIA+"};

	/** Comboタイプの表示名 */
	private static final String[] COMBOTYPE_STRING = {"DISABLE", "NORMAL", "DOUBLE"};

	/** GameManager that owns this mode */
	private GameManager owner;

	/** Drawing and event handling EventReceiver */
	private EventReceiver receiver;

	/** Level upまでの残り point */
	private int goal;

	/** 直前に手に入れた point */
	private int lastgoal;

	/** Most recent increase in score */
	private int lastscore;

	/** Time to display the most recent increase in score */
	private int scgettime;

	/** Most recent scoring event type */
	private int lastevent;

	/** Most recent scoring eventでB2Bだったらtrue */
	private boolean lastb2b;

	/** Most recent scoring eventでのCombocount */
	private int lastcombo;

	/** Most recent scoring eventでのピースID */
	private int lastpiece;

	/** Endingの残り time */
	private int rolltime;

	/** Ending開始 flag */
	private boolean rollstarted;

	/** 裏段位 */
	private int secretGrade;

	/** BGM number */
	private int bgmno;

	/** Flag for types of T-Spins allowed (0=none, 1=normal, 2=all spin) */
	private int tspinEnableType;

	/** Old flag for allowing T-Spins */
	private boolean enableTSpin;

	/** Flag for enabling wallkick T-Spins */
	private boolean enableTSpinKick;

	/** Flag for enabling B2B */
	private boolean enableB2B;

	/** Comboタイプ */
	private int comboType;

	/** Big */
	private boolean big;

	/** Big時の横移動単位 */
	private boolean bigmove;

	/** Big時Linescount半分 */
	private boolean bighalf;

	/** Levelタイプ */
	private int leveltype;

	/** Preset number */
	private int presetNumber;

	/** Map number */
	private int mapNumber;

	/** Current version */
	private int version;

	/** Next Section の level (これ-1のときに levelストップする) */
	private int nextseclv;

	/** Levelが増えた flag */
	private boolean lvupflag;

	/** Combo bonus */
	private int comboValue;

	/** Hard drop bonus */
	private int harddropBonus;

	/** levelstop sound */
	private boolean lvstopse;

	/** クリアになる level */
	private int goallv;

	/** 制限 time (0:なし) */
	private int timelimit;

	/** Ending time (0:なし) */
	private int rolltimelimit;

	/** 出現可能ピースの配列 */
	private boolean[] pieceEnable;

	/** Map使用 flag */
	private boolean useMap;

	/** バックアップ用field (Mapをリプレイに保存するときに使用) */
	private Field fldBackup;

	/** 残り time */
	private int timelimitTimer;

	/** Level upごとに制限 timeをリセットする */
	private boolean timelimitResetEveryLevel;

	/** 骨Blockを使う */
	private boolean bone;

	/*
	 * Mode name
	 */
	@Override
	public String getName() {
		return "PRACTICE";
	}

	/*
	 * Initialization
	 */
	@Override
	public void playerInit(GameEngine engine, int playerID) {
		log.debug("playerInit called");

		owner = engine.owner;
		receiver = engine.owner.receiver;
		goal = 0;
		lastgoal = 0;
		lastscore = 0;
		scgettime = 0;
		lastevent = EVENT_NONE;
		lastb2b = false;
		lastcombo = 0;
		lastpiece = 0;
		nextseclv = 100;
		lvupflag = false;
		comboValue = 0;
		harddropBonus = 0;
		rolltime = 0;
		rollstarted = false;
		secretGrade = 0;
		pieceEnable = new boolean[Piece.PIECE_COUNT];
		fldBackup = null;
		timelimitTimer = 0;
		engine.framecolor = GameEngine.FRAME_COLOR_YELLOW;

		if(engine.owner.replayMode == false) {
			version = CURRENT_VERSION;
			presetNumber = engine.owner.modeConfig.getProperty("practice.presetNumber", 0);
			mapNumber = engine.owner.modeConfig.getProperty("practice.mapNumber", 0);
			loadPreset(engine, engine.owner.modeConfig, -1);
		} else {
			version = engine.owner.replayProp.getProperty("practice.version", CURRENT_VERSION);
			presetNumber = 0;
			mapNumber = 0;
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
		engine.speed.gravity = prop.getProperty("practice.gravity." + preset, 4);
		engine.speed.denominator = prop.getProperty("practice.denominator." + preset, 256);
		engine.speed.are = prop.getProperty("practice.are." + preset, 0);
		engine.speed.areLine = prop.getProperty("practice.areLine." + preset, 0);
		engine.speed.lineDelay = prop.getProperty("practice.lineDelay." + preset, 0);
		engine.speed.lockDelay = prop.getProperty("practice.lockDelay." + preset, 30);
		engine.speed.das = prop.getProperty("practice.das." + preset, 14);
		bgmno = prop.getProperty("practice.bgmno." + preset, 0);
		tspinEnableType = prop.getProperty("practice.tspinEnableType." + preset, 1);
		enableTSpin = prop.getProperty("practice.enableTSpin." + preset, true);
		enableTSpinKick = prop.getProperty("practice.enableTSpinKick." + preset, true);
		enableB2B = prop.getProperty("practice.enableB2B." + preset, true);
		comboType = prop.getProperty("practice.comboType." + preset, GameEngine.COMBO_TYPE_NORMAL);
		big = prop.getProperty("practice.big." + preset, false);
		bigmove = prop.getProperty("practice.bigmove." + preset, true);
		bighalf = prop.getProperty("practice.bighalf." + preset, true);
		leveltype = prop.getProperty("practice.leveltype." + preset, LEVELTYPE_NONE);
		lvstopse = prop.getProperty("practice.lvstopse." + preset, true);
		goallv = prop.getProperty("practice.goallv." + preset, -1);
		timelimit = prop.getProperty("practice.timelimit." + preset, 0);
		rolltimelimit = prop.getProperty("practice.rolltimelimit." + preset, 0);
		for(int i = 0; i < Piece.PIECE_COUNT; i++) {
			pieceEnable[i] = prop.getProperty("practice.pieceEnable." + i + "." + preset, (i < Piece.PIECE_STANDARD_COUNT));
		}
		useMap = prop.getProperty("practice.useMap." + preset, false);
		timelimitResetEveryLevel = prop.getProperty("practice.timelimitResetEveryLevel." + preset, false);
		bone = prop.getProperty("practice.bone." + preset, false);
	}

	/**
	 * Presetを保存
	 * @param engine GameEngine
	 * @param prop Property file to save to
	 * @param preset Preset number
	 */
	private void savePreset(GameEngine engine, CustomProperties prop, int preset) {
		prop.setProperty("practice.gravity." + preset, engine.speed.gravity);
		prop.setProperty("practice.denominator." + preset, engine.speed.denominator);
		prop.setProperty("practice.are." + preset, engine.speed.are);
		prop.setProperty("practice.areLine." + preset, engine.speed.areLine);
		prop.setProperty("practice.lineDelay." + preset, engine.speed.lineDelay);
		prop.setProperty("practice.lockDelay." + preset, engine.speed.lockDelay);
		prop.setProperty("practice.das." + preset, engine.speed.das);
		prop.setProperty("practice.bgmno." + preset, bgmno);
		prop.setProperty("practice.tspinEnableType." + preset, tspinEnableType);
		prop.setProperty("practice.enableTSpin." + preset, enableTSpin);
		prop.setProperty("practice.enableTSpinKick." + preset, enableTSpinKick);
		prop.setProperty("practice.enableB2B." + preset, enableB2B);
		prop.setProperty("practice.comboType." + preset, comboType);
		prop.setProperty("practice.big." + preset, big);
		prop.setProperty("practice.bigmove." + preset, bigmove);
		prop.setProperty("practice.bighalf." + preset, bighalf);
		prop.setProperty("practice.leveltype." + preset, leveltype);
		prop.setProperty("practice.lvstopse." + preset, lvstopse);
		prop.setProperty("practice.goallv." + preset, goallv);
		prop.setProperty("practice.timelimit." + preset, timelimit);
		prop.setProperty("practice.rolltimelimit." + preset, rolltimelimit);
		for(int i = 0; i < Piece.PIECE_COUNT; i++) {
			prop.setProperty("practice.pieceEnable." + i + "." + preset, pieceEnable[i]);
		}
		prop.setProperty("practice.useMap." + preset, useMap);
		prop.setProperty("practice.timelimitResetEveryLevel." + preset, timelimitResetEveryLevel);
		prop.setProperty("practice.bone." + preset, bone);
	}

	/**
	 * Map読み込み
	 * @param field field
	 * @param prop Property file to read from
	 * @param preset 任意のID
	 */
	private void loadMap(Field field, CustomProperties prop, int id) {
		field.reset();
		field.readProperty(prop, id);
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
		field.writeProperty(prop, id);
	}

	/*
	 * Called at settings screen
	 */
	@Override
	public boolean onSetting(GameEngine engine, int playerID) {
		// Menu
		if(engine.owner.replayMode == false) {
			owner.menuOnly = true;

			// Configuration changes
			int change = updateCursor(engine, 38);

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
					big = !big;
					break;
				case 9:
					leveltype += change;
					if(leveltype < 0) leveltype = LEVELTYPE_MAX - 1;
					if(leveltype > LEVELTYPE_MAX - 1) leveltype = 0;
					break;
				case 10:
					//enableTSpin = !enableTSpin;
					tspinEnableType += change;
					if(tspinEnableType < 0) tspinEnableType = 2;
					if(tspinEnableType > 2) tspinEnableType = 0;
					break;
				case 11:
					enableTSpinKick = !enableTSpinKick;
					break;
				case 12:
					enableB2B = !enableB2B;
					break;
				case 13:
					comboType += change;
					if(comboType < 0) comboType = 2;
					if(comboType > 2) comboType = 0;
					break;
				case 14:
					lvstopse = !lvstopse;
					break;
				case 15:
					bigmove = !bigmove;
					break;
				case 16:
					bighalf = !bighalf;
					break;
				case 17:
					goallv += change * m;
					if(goallv < -1) goallv = 9999;
					if(goallv > 9999) goallv = -1;
					break;
				case 18:
					timelimit += change * 60 * m;
					if(timelimit < 0) timelimit = 3600 * 20;
					if(timelimit > 3600 * 20) timelimit = 0;
					break;
				case 19:
					rolltimelimit += change * 60 * m;
					if(rolltimelimit < 0) rolltimelimit = 3600 * 20;
					if(rolltimelimit > 3600 * 20) rolltimelimit = 0;
					break;
				case 20:
					timelimitResetEveryLevel = !timelimitResetEveryLevel;
					break;
				case 21:
					bone = !bone;
					break;
				case 22:
					pieceEnable[0] = !pieceEnable[0];
					break;
				case 23:
					pieceEnable[1] = !pieceEnable[1];
					break;
				case 24:
					pieceEnable[2] = !pieceEnable[2];
					break;
				case 25:
					pieceEnable[3] = !pieceEnable[3];
					break;
				case 26:
					pieceEnable[4] = !pieceEnable[4];
					break;
				case 27:
					pieceEnable[5] = !pieceEnable[5];
					break;
				case 28:
					pieceEnable[6] = !pieceEnable[6];
					break;
				case 29:
					pieceEnable[7] = !pieceEnable[7];
					break;
				case 30:
					pieceEnable[8] = !pieceEnable[8];
					break;
				case 31:
					pieceEnable[9] = !pieceEnable[9];
					break;
				case 32:
					pieceEnable[10] = !pieceEnable[10];
					break;
				case 33:
					useMap = !useMap;
					break;
				case 34:
				case 35:
				case 36:
					mapNumber += change;
					if(mapNumber < 0) mapNumber = 99;
					if(mapNumber > 99) mapNumber = 0;
					break;
				case 37:
				case 38:
					presetNumber += change;
					if(presetNumber < 0) presetNumber = 99;
					if(presetNumber > 99) presetNumber = 0;
					break;
				}
			}

			// 決定
			if(GameKey.gamekey[playerID].isPushKey(GameKey.BUTTON_NAV_SELECT) && (engine.statc[3] >= 5)) {
				engine.playSE("decide");

				if(engine.statc[2] == 34) {
					// fieldエディット
					engine.enterFieldEdit();
					return true;
				} else if(engine.statc[2] == 35) {
					// Map読み込み
					engine.createFieldIfNeeded();
					engine.field.reset();

					CustomProperties prop = receiver.loadProperties("config/map/practice/" + mapNumber + ".map");
					if(prop != null) {
						loadMap(engine.field, prop, 0);
						engine.field.setAllSkin(engine.getSkin());
					}
				} else if(engine.statc[2] == 36) {
					// Map保存
					if(engine.field != null) {
						CustomProperties prop = new CustomProperties();
						saveMap(engine.field, prop, 0);
						receiver.saveProperties("config/map/practice/" + mapNumber + ".map", prop);
					}
				} else if(engine.statc[2] == 37) {
					// Preset読み込み
					loadPreset(engine, owner.modeConfig, presetNumber);
				} else if(engine.statc[2] == 38) {
					// Preset保存
					savePreset(engine, owner.modeConfig, presetNumber);
					receiver.saveModeConfig(owner.modeConfig);
				} else {
					// Start game
					owner.modeConfig.setProperty("practice.presetNumber", presetNumber);
					owner.modeConfig.setProperty("practice.mapNumber", mapNumber);
					savePreset(engine, owner.modeConfig, -1);
					receiver.saveModeConfig(owner.modeConfig);

					if(useMap && ((engine.field == null) || (engine.field.isEmpty()))) {
						CustomProperties prop = receiver.loadProperties("config/map/practice/" + mapNumber + ".map");
						if(prop != null) {
							engine.createFieldIfNeeded();
							loadMap(engine.field, prop, 0);
							engine.field.setAllSkin(engine.getSkin());
						} else {
							useMap = false;
						}
					}

					owner.menuOnly = false;
					return false;
				}
			}

			// Cancel
			if(GameKey.gamekey[playerID].isPushKey(GameKey.BUTTON_NAV_CANCEL)) {
				engine.quitflag = true;
			}

			engine.statc[3]++;
		} else {
			owner.menuOnly = true;

			engine.statc[3]++;
			engine.statc[2] = 0;

			if(engine.statc[3] >= 60) {
				engine.statc[2] = 22;
			}
			if((engine.statc[3] >= 120) || engine.ctrl.isPush(Controller.BUTTON_F)) {
				owner.menuOnly = false;
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
		receiver.drawMenuFont(engine, playerID, 1, 1, "PRACTICE MODE SETTINGS", EventReceiver.COLOR_ORANGE);

		if(engine.owner.replayMode == false) {
			receiver.drawMenuFont(engine, playerID, 1, 27, "A:START B:EXIT C+<>:FAST CHANGE", EventReceiver.COLOR_CYAN);
		} else {
			receiver.drawMenuFont(engine, playerID, 1, 27, "F:SKIP", EventReceiver.COLOR_RED);
		}

		if(engine.statc[2] < 22) {
			if(owner.replayMode == false) {
				receiver.drawMenuFont(engine, playerID, 1, engine.statc[2] + 3, "b", EventReceiver.COLOR_RED);
			}

			receiver.drawMenuFont(engine, playerID, 2,  3, "GRAVITY:" + engine.speed.gravity, (engine.statc[2] == 0));
			receiver.drawMenuFont(engine, playerID, 2,  4, "G-MAX:" + engine.speed.denominator, (engine.statc[2] == 1));
			receiver.drawMenuFont(engine, playerID, 2,  5, "ARE:" + engine.speed.are, (engine.statc[2] == 2));
			receiver.drawMenuFont(engine, playerID, 2,  6, "ARE LINE:" + engine.speed.areLine, (engine.statc[2] == 3));
			receiver.drawMenuFont(engine, playerID, 2,  7, "LINE DELAY:" + engine.speed.lineDelay, (engine.statc[2] == 4));
			receiver.drawMenuFont(engine, playerID, 2,  8, "LOCK DELAY:" + engine.speed.lockDelay, (engine.statc[2] == 5));
			receiver.drawMenuFont(engine, playerID, 2,  9, "DAS:" + engine.speed.das, (engine.statc[2] == 6));
			receiver.drawMenuFont(engine, playerID, 2, 10, "BGM:" + bgmno, (engine.statc[2] == 7));
			receiver.drawMenuFont(engine, playerID, 2, 11, "BIG:" + GeneralUtil.getONorOFF(big), (engine.statc[2] == 8));
			receiver.drawMenuFont(engine, playerID, 2, 12, "LEVEL TYPE:" + LEVELTYPE_STRING[leveltype], (engine.statc[2] == 9));
			String strTSpinEnable = "";
			if(version >= 4) {
				if(tspinEnableType == 0) strTSpinEnable = "OFF";
				if(tspinEnableType == 1) strTSpinEnable = "T-ONLY";
				if(tspinEnableType == 2) strTSpinEnable = "ALL";
			} else {
				strTSpinEnable = GeneralUtil.getONorOFF(enableTSpin);
			}
			receiver.drawMenuFont(engine, playerID, 2, 13, "SPIN BONUS:" + strTSpinEnable, (engine.statc[2] == 10));
			receiver.drawMenuFont(engine, playerID, 2, 14, "EZ SPIN:" + GeneralUtil.getONorOFF(enableTSpinKick), (engine.statc[2] == 11));
			receiver.drawMenuFont(engine, playerID, 2, 15, "B2B:" + GeneralUtil.getONorOFF(enableB2B), (engine.statc[2] == 12));
			receiver.drawMenuFont(engine, playerID, 2, 16, "COMBO:" + COMBOTYPE_STRING[comboType], (engine.statc[2] == 13));
			receiver.drawMenuFont(engine, playerID, 2, 17, "LEVEL STOP SE:" + GeneralUtil.getONorOFF(lvstopse), (engine.statc[2] == 14));
			receiver.drawMenuFont(engine, playerID, 2, 18, "BIG MOVE:" + (bigmove ? "2 CELL" : "1 CELL"), (engine.statc[2] == 15));
			receiver.drawMenuFont(engine, playerID, 2, 19, "BIG HALF:" + GeneralUtil.getONorOFF(bighalf), (engine.statc[2] == 16));
			String strGoalLv = "ENDLESS";
			if(goallv >= 0) {
				if((leveltype == LEVELTYPE_MANIA) || (leveltype == LEVELTYPE_MANIAPLUS))
					strGoalLv = "LV" + String.valueOf((goallv + 1) * 100);
				else if(leveltype == LEVELTYPE_NONE)
					strGoalLv = String.valueOf(goallv + 1) + " LINES";
				else
					strGoalLv = "LV" + String.valueOf(goallv + 1);
			}
			receiver.drawMenuFont(engine, playerID, 2, 20, "GOAL LEVEL:" + strGoalLv, (engine.statc[2] == 17));
			receiver.drawMenuFont(engine, playerID, 2, 21, "TIME LIMIT:" + ((timelimit == 0) ? "NONE" : GeneralUtil.getTime(timelimit)),
								  (engine.statc[2] == 18));
			receiver.drawMenuFont(engine, playerID, 2, 22, "ROLL LIMIT:" + ((rolltimelimit == 0) ? "NONE" : GeneralUtil.getTime(rolltimelimit)),
								  (engine.statc[2] == 19));
			receiver.drawMenuFont(engine, playerID, 2, 23, "TIME LIMIT RESET EVERY LEVEL:" + GeneralUtil.getONorOFF(timelimitResetEveryLevel),
								  (engine.statc[2] == 20));
			receiver.drawMenuFont(engine, playerID, 2, 24, "USE BONE BLOCKS:" + GeneralUtil.getONorOFF(bone), (engine.statc[2] == 21));
		} else if(engine.statc[2] < 42) {
			if(owner.replayMode == false) {
				receiver.drawMenuFont(engine, playerID, 1, engine.statc[2] - 22 + 3, "b", EventReceiver.COLOR_RED);
			}

			receiver.drawMenuFont(engine, playerID, 2,  3, "PIECE I:" + GeneralUtil.getONorOFF(pieceEnable[0]), (engine.statc[2] == 22));
			receiver.drawMenuFont(engine, playerID, 2,  4, "PIECE L:" + GeneralUtil.getONorOFF(pieceEnable[1]), (engine.statc[2] == 23));
			receiver.drawMenuFont(engine, playerID, 2,  5, "PIECE O:" + GeneralUtil.getONorOFF(pieceEnable[2]), (engine.statc[2] == 24));
			receiver.drawMenuFont(engine, playerID, 2,  6, "PIECE Z:" + GeneralUtil.getONorOFF(pieceEnable[3]), (engine.statc[2] == 25));
			receiver.drawMenuFont(engine, playerID, 2,  7, "PIECE T:" + GeneralUtil.getONorOFF(pieceEnable[4]), (engine.statc[2] == 26));
			receiver.drawMenuFont(engine, playerID, 2,  8, "PIECE J:" + GeneralUtil.getONorOFF(pieceEnable[5]), (engine.statc[2] == 27));
			receiver.drawMenuFont(engine, playerID, 2,  9, "PIECE S:" + GeneralUtil.getONorOFF(pieceEnable[6]), (engine.statc[2] == 28));
			receiver.drawMenuFont(engine, playerID, 2, 10, "PIECE I1:" + GeneralUtil.getONorOFF(pieceEnable[7]), (engine.statc[2] == 29));
			receiver.drawMenuFont(engine, playerID, 2, 11, "PIECE I2:" + GeneralUtil.getONorOFF(pieceEnable[8]), (engine.statc[2] == 30));
			receiver.drawMenuFont(engine, playerID, 2, 12, "PIECE I3:" + GeneralUtil.getONorOFF(pieceEnable[9]), (engine.statc[2] == 31));
			receiver.drawMenuFont(engine, playerID, 2, 13, "PIECE L3:" + GeneralUtil.getONorOFF(pieceEnable[10]), (engine.statc[2] == 32));
			receiver.drawMenuFont(engine, playerID, 2, 14, "USE MAP:" + GeneralUtil.getONorOFF(useMap), (engine.statc[2] == 33));
			receiver.drawMenuFont(engine, playerID, 2, 15, "[EDIT FIELD MAP]", (engine.statc[2] == 34));
			receiver.drawMenuFont(engine, playerID, 2, 16, "[LOAD FIELD MAP]:" + mapNumber, (engine.statc[2] == 35));
			receiver.drawMenuFont(engine, playerID, 2, 17, "[SAVE FIELD MAP]:" + mapNumber, (engine.statc[2] == 36));
			receiver.drawMenuFont(engine, playerID, 2, 18, "[LOAD PRESET]:" + presetNumber, (engine.statc[2] == 37));
			receiver.drawMenuFont(engine, playerID, 2, 19, "[SAVE PRESET]:" + presetNumber, (engine.statc[2] == 38));
		}
	}

	/*
	 * Called for initialization during Ready (before initialization)
	 */
	@Override
	public boolean onReady(GameEngine engine, int playerID) {
		if(engine.statc[0] == 0) {
			//  time制限設定
			if(timelimit > 0) timelimitTimer = timelimit;

			// 骨Block
			engine.bone = bone;

			// 出現可能なピースを設定
			if(version >= 1) {
				for(int i = 0; i < Piece.PIECE_COUNT; i++) {
					engine.nextPieceEnable[i] = pieceEnable[i];
				}
			}

			// Map読み込み・リプレイ保存用にバックアップ
			if(version >= 2) {
				if(useMap) {
					if(owner.replayMode) {
						log.debug("Loading map data from replay data");
						engine.createFieldIfNeeded();
						loadMap(engine.field, owner.replayProp, 0);
						engine.field.setAllSkin(engine.getSkin());
					} else {
						log.debug("Backup map data");
						fldBackup = new Field(engine.field);
					}
				} else if(engine.field != null) {
					log.debug("Use no map, reseting field");
					engine.field.reset();
				} else {
					log.debug("Use no map");
				}
			}
		}

		return false;
	}

	/*
	 * Readyの時のCalled at initialization (Start game直前）
	 */
	@Override
	public void startGame(GameEngine engine, int playerID) {
		engine.big = big;
		engine.bigmove = bigmove;
		engine.bighalf = bighalf;

		if((leveltype != LEVELTYPE_MANIA) && (leveltype != LEVELTYPE_MANIAPLUS)) {
			engine.b2bEnable = enableB2B;
			engine.comboType = comboType;
			engine.statistics.levelDispAdd = 1;

			engine.tspinAllowKick = enableTSpinKick;
			if(version >= 4) {
				if(tspinEnableType == 0) {
					engine.tspinEnable = false;
				} else if(tspinEnableType == 1) {
					engine.tspinEnable = true;
				} else {
					engine.tspinEnable = true;
					engine.useAllSpinBonus = true;
				}
			} else {
				engine.tspinEnable = enableTSpin;
			}
		} else {
			engine.tspinEnable = false;
			engine.tspinAllowKick = false;
			engine.b2bEnable = false;
			engine.comboType = GameEngine.COMBO_TYPE_DOUBLE;
			engine.statistics.levelDispAdd = 0;
		}

		owner.bgmStatus.bgm = bgmno;

		goal = 5 * (engine.statistics.level + 1);

		engine.meterValue = 0;
		engine.meterColor = GameEngine.METER_COLOR_GREEN;
		setMeter(engine, playerID);
	}

	/*
	 * Render score
	 */
	@Override
	public void renderLast(GameEngine engine, int playerID) {
		receiver.drawScoreFont(engine, playerID, 0, 0, "PRACTICE", EventReceiver.COLOR_YELLOW);

		if(engine.stat == GameEngine.STAT_FIELDEDIT) {
			// fieldエディットのとき

			// 座標
			receiver.drawScoreFont(engine, playerID, 0, 2, "X POS", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 0, 3, "" + engine.fldeditX);
			receiver.drawScoreFont(engine, playerID, 0, 4, "Y POS", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 0, 5, "" + engine.fldeditY);

			// Put your field-checking algorithm test codes here
			/*
			if(engine.field != null) {
				receiver.drawScoreFont(engine, playerID, 0, 7, "T-SLOT+LINECLEAR", EventReceiver.COLOR_BLUE);
				receiver.drawScoreFont(engine, playerID, 0, 8, "" + engine.field.getTSlotLineClearAll(false));
				receiver.drawScoreFont(engine, playerID, 0, 9, "HOLE", EventReceiver.COLOR_BLUE);
				receiver.drawScoreFont(engine, playerID, 0, 10, "" + engine.field.getHowManyHoles());
			}
			*/
		} else if((leveltype == LEVELTYPE_MANIA) || (leveltype == LEVELTYPE_MANIAPLUS)) {
			//  levelタイプがMANIAのとき

			// Score
			receiver.drawScoreFont(engine, playerID, 0, 5, "SCORE", EventReceiver.COLOR_BLUE);
			String strScore = String.valueOf(engine.statistics.score);
			if((lastscore > 0) && (scgettime < 120)) strScore += "(+" + lastscore + ")";
			receiver.drawScoreFont(engine, playerID, 0, 6, strScore);

			//  level
			receiver.drawScoreFont(engine, playerID, 0, 9, "LEVEL", EventReceiver.COLOR_BLUE);
			int tempLevel = engine.statistics.level;
			if(tempLevel < 0) tempLevel = 0;
			String strLevel = String.format("%3d", tempLevel);
			receiver.drawScoreFont(engine, playerID, 0, 10, strLevel);

			int speed = engine.speed.gravity / 128;
			if(engine.speed.gravity < 0) speed = 40;
			receiver.drawSpeedMeter(engine, playerID, 0, 11, speed);

			receiver.drawScoreFont(engine, playerID, 0, 12, String.format("%3d", nextseclv));

			// Time
			receiver.drawScoreFont(engine, playerID, 0, 14, "TIME", EventReceiver.COLOR_BLUE);
			int time = engine.statistics.time;
			if(timelimit > 0) time = timelimitTimer;
			if(time < 0) time = 0;
			int fontcolor = EventReceiver.COLOR_WHITE;
			if((time < 30 * 60) && (time > 0) && (timelimit > 0)) fontcolor = EventReceiver.COLOR_YELLOW;
			if((time < 20 * 60) && (time > 0) && (timelimit > 0)) fontcolor = EventReceiver.COLOR_ORANGE;
			if((time < 10 * 60) && (time > 0) && (timelimit > 0)) fontcolor = EventReceiver.COLOR_RED;
			receiver.drawScoreFont(engine, playerID, 0, 15, GeneralUtil.getTime(time), fontcolor);

			// Roll 残り time
			if((engine.gameActive) && (engine.ending == 2)) {
				int remainTime = rolltimelimit - rolltime;
				if(remainTime < 0) remainTime = 0;
				receiver.drawScoreFont(engine, playerID, 0, 17, "ROLL TIME", EventReceiver.COLOR_BLUE);
				receiver.drawScoreFont(engine, playerID, 0, 18, GeneralUtil.getTime(remainTime), ((remainTime > 0) && (remainTime < 10 * 60)));
			}
		} else {
			//  levelタイプがMANIA以外のとき

			// Score
			receiver.drawScoreFont(engine, playerID, 0, 2, "SCORE", EventReceiver.COLOR_BLUE);
			String strScore = String.valueOf(engine.statistics.score);
			if((lastscore > 0) && (scgettime < 120)) strScore += "(+" + lastscore + ")";
			receiver.drawScoreFont(engine, playerID, 0, 3, strScore);

			if(leveltype == LEVELTYPE_POINTS) {
				// ゴール
				receiver.drawScoreFont(engine, playerID, 0, 5, "GOAL", EventReceiver.COLOR_BLUE);
				String strGoal = String.valueOf(goal);
				if((lastgoal != 0) && (scgettime < 120) && (engine.ending == 0))
					strGoal += "(-" + String.valueOf(lastgoal) + ")";
				receiver.drawScoreFont(engine, playerID, 0, 6, strGoal);
			} else if(leveltype == LEVELTYPE_10LINES) {
				// Lines( levelタイプが10LINESのとき)
				receiver.drawScoreFont(engine, playerID, 0, 5, "LINE", EventReceiver.COLOR_BLUE);
				receiver.drawScoreFont(engine, playerID, 0, 6, engine.statistics.lines + "/" + ((engine.statistics.level + 1) * 10));
			} else {
				// Lines( levelタイプがNONEのとき)
				receiver.drawScoreFont(engine, playerID, 0, 5, "LINE", EventReceiver.COLOR_BLUE);
				receiver.drawScoreFont(engine, playerID, 0, 6, String.valueOf(engine.statistics.lines));
			}

			//  level
			if(leveltype != LEVELTYPE_NONE) {
				receiver.drawScoreFont(engine, playerID, 0, 8, "LEVEL", EventReceiver.COLOR_BLUE);
				receiver.drawScoreFont(engine, playerID, 0, 9, String.valueOf(engine.statistics.level + 1));
			}

			// 1分間あたり score
			receiver.drawScoreFont(engine, playerID, 0, 11, "SCORE/MIN", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 0, 12, String.format("%-10g", engine.statistics.spm));

			// 1分間あたりのLines
			receiver.drawScoreFont(engine, playerID, 0, 14, "LINE/MIN", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 0, 15, String.valueOf(engine.statistics.lpm));

			// Time
			receiver.drawScoreFont(engine, playerID, 0, 17, "TIME", EventReceiver.COLOR_BLUE);
			int time = engine.statistics.time;
			if(timelimit > 0) time = timelimitTimer;
			if(time < 0) time = 0;
			int fontcolor = EventReceiver.COLOR_WHITE;
			if((time < 30 * 60) && (time > 0) && (timelimit > 0)) fontcolor = EventReceiver.COLOR_YELLOW;
			if((time < 20 * 60) && (time > 0) && (timelimit > 0)) fontcolor = EventReceiver.COLOR_ORANGE;
			if((time < 10 * 60) && (time > 0) && (timelimit > 0)) fontcolor = EventReceiver.COLOR_RED;
			receiver.drawScoreFont(engine, playerID, 0, 18, GeneralUtil.getTime(time), fontcolor);

			// Roll 残り time
			if((engine.gameActive) && (engine.ending == 2)) {
				int remainTime = rolltimelimit - rolltime;
				if(remainTime < 0) remainTime = 0;
				receiver.drawScoreFont(engine, playerID, 0, 20, "ROLL TIME", EventReceiver.COLOR_BLUE);
				receiver.drawScoreFont(engine, playerID, 0, 21, GeneralUtil.getTime(remainTime), ((remainTime > 0) && (remainTime < 10 * 60)));
			}

			// Line clear event 
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
				case EVENT_TSPIN_ZERO_MINI:
					receiver.drawMenuFont(engine, playerID, 2, 21, strPieceName + "-SPIN", EventReceiver.COLOR_PURPLE);
					break;
				case EVENT_TSPIN_ZERO:
					receiver.drawMenuFont(engine, playerID, 2, 21, strPieceName + "-SPIN", EventReceiver.COLOR_PINK);
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

				if((lastcombo >= 2) && (lastevent != EVENT_TSPIN_ZERO_MINI) && (lastevent != EVENT_TSPIN_ZERO))
					receiver.drawMenuFont(engine, playerID, 2, 22, (lastcombo - 1) + "COMBO", EventReceiver.COLOR_CYAN);
			}
		}
	}

	/*
	 * Called after every frame
	 */
	@Override
	public void onLast(GameEngine engine, int playerID) {
		scgettime++;

		if((engine.gameActive) && (engine.ending == 2)) {
			// Ending中
			rolltime++;

			// Roll 終了
			if(rolltime >= rolltimelimit) {
				engine.gameActive = false;
				engine.resetStatc();
				engine.stat = GameEngine.STAT_EXCELLENT;
			}
		} else {
			if((timelimitTimer > 0) && (engine.timerActive == true)) timelimitTimer--;

			// Out of time
			if((timelimit > 0) && (timelimitTimer <= 0) && (engine.timerActive == true)) {
				engine.gameActive = false;
				engine.timerActive = false;
				engine.resetStatc();
				if(goallv == -1) engine.stat = GameEngine.STAT_ENDINGSTART;
				else engine.stat = GameEngine.STAT_GAMEOVER;
			}

			// 10秒前からのカウントダウン
			if((timelimit > 0) && (timelimitTimer <= 10 * 60) && (timelimitTimer % 60 == 0) && (engine.timerActive == true)) {
				engine.playSE("countdown");
			}

			// 5秒前からのBGM fadeout
			if((timelimit > 0) && (timelimitTimer <= 5 * 60) && (timelimitResetEveryLevel == false) && (engine.timerActive == true)) {
				owner.bgmStatus.fadesw = true;
			}
		}

		// Update meter
		setMeter(engine, playerID);
	}

	/*
	 * Called at game over
	 */
	@Override
	public boolean onGameOver(GameEngine engine, int playerID) {
		if((engine.statc[0] == 0) && (engine.gameActive)) {
			secretGrade = engine.field.getSecretGrade();
		}
		return false;
	}

	/*
	 * 移動中の処理
	 */
	@Override
	public boolean onMove(GameEngine engine, int playerID) {
		// 新規ピース出現時
		if((leveltype == LEVELTYPE_MANIA) || (leveltype == LEVELTYPE_MANIAPLUS)) {
			if((engine.ending == 0) && (engine.statc[0] == 0) && (engine.holdDisable == false) && (!lvupflag)) {
				// Level up
				if(engine.statistics.level < nextseclv - 1) {
					engine.statistics.level++;
					if((engine.statistics.level == nextseclv - 1) && (lvstopse == true)) engine.playSE("levelstop");
					setMeter(engine, playerID);
				}

				// Hard drop bonusInitialization
				harddropBonus = 0;
			}
			if( (engine.ending == 0) && (engine.statc[0] > 0) && ((version >= 1) || (engine.holdDisable == false)) ) {
				lvupflag = false;
			}
		}

		// Endingスタート
		if((engine.ending == 2) && (rollstarted == false)) {
			rollstarted = true;

			if((leveltype == LEVELTYPE_MANIA) || (leveltype == LEVELTYPE_MANIAPLUS)) {
				engine.blockHidden = 300;
				engine.blockHiddenAnim = true;

				if(leveltype == LEVELTYPE_MANIA)
					engine.blockOutlineType = GameEngine.BLOCK_OUTLINE_NONE;
			}

			owner.bgmStatus.bgm = BGMStatus.BGM_ENDING1;
		}

		return false;
	}

	/*
	 * ARE中の処理
	 */
	@Override
	public boolean onARE(GameEngine engine, int playerID) {
		// 最後の frame
		if((leveltype == LEVELTYPE_MANIA) || (leveltype == LEVELTYPE_MANIAPLUS)) {
			if((engine.ending == 0) && (engine.statc[0] >= engine.statc[1] - 1) && (!lvupflag)) {
				if(engine.statistics.level < nextseclv - 1) {
					engine.statistics.level++;
					if((engine.statistics.level == nextseclv - 1) && (lvstopse == true)) engine.playSE("levelstop");
					setMeter(engine, playerID);
				}
				lvupflag = true;
			}
		}

		return false;
	}

	/*
	 * Calculate score
	 */
	@Override
	public void calcScore(GameEngine engine, int playerID, int lines) {
		if((leveltype == LEVELTYPE_MANIA) || (leveltype == LEVELTYPE_MANIAPLUS)) {
			calcScoreMania(engine, playerID, lines);
		} else {
			calcScoreNormal(engine, playerID, lines);
		}
	}

	/**
	 *  levelタイプがMANIAのときのCalculate score
	 */
	private void calcScoreMania(GameEngine engine, int playerID, int lines) {
		// Combo
		if(lines == 0) {
			comboValue = 1;
		} else {
			comboValue = comboValue + (2 * lines) - 2;
			if(comboValue < 1) comboValue = 1;
		}

		if((lines >= 1) && (engine.ending == 0)) {
			// Level up
			int levelb = engine.statistics.level;

			if(leveltype == LEVELTYPE_MANIA) {
				engine.statistics.level += lines;
			} else {
				int levelplus = lines;
				if(lines == 3) levelplus = 4;
				if(lines >= 4) levelplus = 6;
				engine.statistics.level += levelplus;
			}

			if((engine.statistics.level >= (goallv + 1) * 100) && (goallv != -1)) {
				// Ending
				engine.statistics.level = (goallv + 1) * 100;
				engine.ending = 1;
				engine.timerActive = false;
				if(rolltimelimit == 0) {
					engine.gameActive = false;
					secretGrade = engine.field.getSecretGrade();
				} else {
					engine.staffrollEnable = true;
					engine.staffrollEnableStatistics = false;
					engine.staffrollNoDeath = false;
				}
			} else if(engine.statistics.level >= nextseclv) {
				// Next Section
				engine.playSE("levelup");

				// Background切り替え
				if(owner.backgroundStatus.bg < 19) {
					owner.backgroundStatus.fadesw = true;
					owner.backgroundStatus.fadecount = 0;
					owner.backgroundStatus.fadebg = owner.backgroundStatus.bg + 1;
				}

				// Update level for next section
				nextseclv += 100;

				// 制限 timeリセット
				if((timelimitResetEveryLevel == true) && (timelimit > 0)) timelimitTimer = timelimit;
			} else if((engine.statistics.level == nextseclv - 1) && (lvstopse == true)) {
				engine.playSE("levelstop");
			}

			// Calculate score
			if(leveltype == LEVELTYPE_MANIA) {
				int manuallock = 0;
				if(engine.manualLock == true) manuallock = 1;

				int bravo = 1;
				if(engine.field.isEmpty()) {
					bravo = 4;
					engine.playSE("bravo");
				}

				int speedBonus = engine.getLockDelay() - engine.statc[0];
				if(speedBonus < 0) speedBonus = 0;

				lastscore = ((levelb + lines)/4 + engine.softdropFall + manuallock + harddropBonus) * lines * comboValue * bravo +
							(engine.statistics.level / 2) + (speedBonus * 7);

				engine.statistics.score += lastscore;
				engine.statistics.scoreFromLineClear += lastscore;
				scgettime = 0;
			} else {
				int manuallock = 0;
				if(engine.manualLock == true) manuallock = 1;

				int bravo = 1;
				if(engine.field.isEmpty()) {
					bravo = 2;
					engine.playSE("bravo");
				}

				int speedBonus = engine.getLockDelay() - engine.statc[0];
				if(speedBonus < 0) speedBonus = 0;

				lastscore = ( ((levelb + lines) / 4 + engine.softdropFall + manuallock + harddropBonus) * lines * comboValue + speedBonus +
							(engine.statistics.level / 2) ) * bravo;

				engine.statistics.score += lastscore;
				engine.statistics.scoreFromLineClear += lastscore;
				scgettime = 0;
			}

			setMeter(engine, playerID);
		}
	}

	/**
	 *  levelタイプがMANIA系以外のときのCalculate score
	 */
	private void calcScoreNormal(GameEngine engine, int playerID, int lines) {
		// Line clear bonus
		int pts = 0;
		int cmb = 0;

		if(engine.tspin) {
			// T-Spin 0 lines
			if(lines == 0) {
				if(engine.tspinmini) {
					pts += 100 * (engine.statistics.level + 1);
					lastevent = EVENT_TSPIN_ZERO_MINI;
				} else {
					pts += 400 * (engine.statistics.level + 1);
					lastevent = EVENT_TSPIN_ZERO;
				}
			}
			// T-Spin 1 line
			else if(lines == 1) {
				if(engine.tspinmini) {
					if(engine.b2b) {
						pts += 300 * (engine.statistics.level + 1);
					} else {
						pts += 200 * (engine.statistics.level + 1);
					}
					lastevent = EVENT_TSPIN_SINGLE_MINI;
				} else {
					if(engine.b2b) {
						pts += 1200 * (engine.statistics.level + 1);
					} else {
						pts += 800 * (engine.statistics.level + 1);
					}
					lastevent = EVENT_TSPIN_SINGLE;
				}
			}
			// T-Spin 2 lines
			else if(lines == 2) {
				if(engine.tspinmini && engine.useAllSpinBonus) {
					if(engine.b2b) {
						pts += 600 * (engine.statistics.level + 1);
					} else {
						pts += 400 * (engine.statistics.level + 1);
					}
					lastevent = EVENT_TSPIN_DOUBLE_MINI;
				} else {
					if(engine.b2b) {
						pts += 1800 * (engine.statistics.level + 1);
					} else {
						pts += 1200 * (engine.statistics.level + 1);
					}
					lastevent = EVENT_TSPIN_DOUBLE;
				}
			}
			// T-Spin 3 lines
			else if(lines >= 3) {
				if(engine.b2b) {
					pts += 2400 * (engine.statistics.level + 1);
				} else {
					pts += 1600 * (engine.statistics.level + 1);
				}
				lastevent = EVENT_TSPIN_TRIPLE;
			}
		} else {
			if(lines == 1) {
				pts += 100 * (engine.statistics.level + 1); // 1列
				lastevent = EVENT_SINGLE;
			} else if(lines == 2) {
				pts += 300 * (engine.statistics.level + 1); // 2列
				lastevent = EVENT_DOUBLE;
			} else if(lines == 3) {
				pts += 500 * (engine.statistics.level + 1); // 3列
				lastevent = EVENT_TRIPLE;
			} else if(lines >= 4) {
				// 4 lines
				if(engine.b2b) {
					pts += 1200 * (engine.statistics.level + 1);
				} else {
					pts += 800 * (engine.statistics.level + 1);
				}
				lastevent = EVENT_FOUR;
			}
		}

		lastb2b = engine.b2b;

		// Combo
		if((engine.combo >= 1) && (lines >= 1)) {
			cmb += ((engine.combo - 1) * 50) * (engine.statistics.level + 1);
			lastcombo = engine.combo;
		}

		// All clear
		if((lines >= 1) && (engine.field.isEmpty())) {
			engine.playSE("bravo");
			pts += 1800 * (engine.statistics.level + 1);
		}

		// Add to score
		if((pts > 0) || (cmb > 0)) {
			lastpiece = engine.nowPieceObject.id;
			lastscore = pts + cmb;
			scgettime = 0;
			if(lines >= 1) engine.statistics.scoreFromLineClear += pts;
			else engine.statistics.scoreFromOtherBonus += pts;
			engine.statistics.score += pts;

			int cmbindex = engine.combo - 1;
			if(cmbindex < 0) cmbindex = 0;
			if(cmbindex >= COMBO_GOAL_TABLE.length) cmbindex = COMBO_GOAL_TABLE.length - 1;
			lastgoal = ((pts / 100) / (engine.statistics.level + 1)) + COMBO_GOAL_TABLE[cmbindex];
			goal -= lastgoal;
			if(goal <= 0) goal = 0;
		}

		boolean endingFlag = false; // Ending突入ならtrue

		if( ((leveltype == LEVELTYPE_10LINES) && (engine.statistics.lines >= (engine.statistics.level + 1) * 10)) ||
		    ((leveltype == LEVELTYPE_POINTS) && (goal <= 0)) )
		{
			if((engine.statistics.level >= goallv) && (goallv != -1)) {
				// Ending
				endingFlag = true;
			} else {
				// Level up
				engine.statistics.level++;

				if(owner.backgroundStatus.bg < 19) {
					owner.backgroundStatus.fadesw = true;
					owner.backgroundStatus.fadecount = 0;
					owner.backgroundStatus.fadebg = owner.backgroundStatus.bg + 1;
				}

				goal = 5 * (engine.statistics.level + 1);

				// 制限 timeリセット
				if((timelimitResetEveryLevel == true) && (timelimit > 0)) timelimitTimer = timelimit;

				engine.playSE("levelup");
			}
		}

		// Ending ( levelタイプNONE）
		if( (version >= 2) && (leveltype == LEVELTYPE_NONE) && (engine.statistics.lines >= goallv + 1) && ((goallv != -1) || (version <= 2)) ) {
			endingFlag = true;
		}

		// Ending突入処理
		if(endingFlag) {
			engine.timerActive = false;

			if(rolltimelimit == 0) {
				engine.ending = 1;
				engine.gameActive = false;
				secretGrade = engine.field.getSecretGrade();
			} else {
				engine.ending = 2;
				engine.staffrollEnable = true;
				engine.staffrollEnableStatistics = true;
				engine.staffrollNoDeath = true;
			}
		}

		setMeter(engine, playerID);
	}

	/**
	 * Meterの量を更新
	 * @param engine GameEngine
	 * @param playerID Player number
	 */
	private void setMeter(GameEngine engine, int playerID) {
		if((engine.gameActive) && (engine.ending == 2)) {
			int remainRollTime = rolltimelimit - rolltime;
			engine.meterValue = (remainRollTime * receiver.getMeterMax(engine)) / rolltimelimit;
			engine.meterColor = GameEngine.METER_COLOR_GREEN;
			if(remainRollTime <= 30*60) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
			if(remainRollTime <= 20*60) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
			if(remainRollTime <= 10*60) engine.meterColor = GameEngine.METER_COLOR_RED;
		} else if(timelimit > 0) {
			int remainTime = timelimitTimer;
			engine.meterValue = (remainTime * receiver.getMeterMax(engine)) / timelimit;
			engine.meterColor = GameEngine.METER_COLOR_GREEN;
			if(remainTime <= 30*60) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
			if(remainTime <= 20*60) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
			if(remainTime <= 10*60) engine.meterColor = GameEngine.METER_COLOR_RED;
		} else if(leveltype == LEVELTYPE_10LINES) {
			engine.meterValue = ((engine.statistics.lines % 10) * receiver.getMeterMax(engine)) / 9;
			engine.meterColor = GameEngine.METER_COLOR_GREEN;
			if(engine.statistics.lines % 10 >= 4) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
			if(engine.statistics.lines % 10 >= 6) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
			if(engine.statistics.lines % 10 >= 8) engine.meterColor = GameEngine.METER_COLOR_RED;
		} else if(leveltype == LEVELTYPE_POINTS) {
			engine.meterValue = (goal * receiver.getMeterMax(engine)) / (5 * (engine.statistics.level + 1));
			engine.meterColor = GameEngine.METER_COLOR_GREEN;
			if(engine.meterValue <= receiver.getMeterMax(engine) / 2) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
			if(engine.meterValue <= receiver.getMeterMax(engine) / 3) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
			if(engine.meterValue <= receiver.getMeterMax(engine) / 4) engine.meterColor = GameEngine.METER_COLOR_RED;
		} else if((leveltype == LEVELTYPE_MANIA) || (leveltype == LEVELTYPE_MANIAPLUS)) {
			engine.meterValue = ((engine.statistics.level % 100) * receiver.getMeterMax(engine)) / 99;
			engine.meterColor = GameEngine.METER_COLOR_GREEN;
			if(engine.statistics.level % 100 >= 50) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
			if(engine.statistics.level % 100 >= 80) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
			if(engine.statistics.level == nextseclv - 1) engine.meterColor = GameEngine.METER_COLOR_RED;
		} else if((leveltype == LEVELTYPE_NONE) && (goallv != -1)) {
			engine.meterValue = ((engine.statistics.lines) * receiver.getMeterMax(engine)) / (goallv + 1);
			engine.meterColor = GameEngine.METER_COLOR_GREEN;
			if(engine.meterValue >= receiver.getMeterMax(engine) / 10) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
			if(engine.meterValue >= receiver.getMeterMax(engine) / 5) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
			if(engine.meterValue >= receiver.getMeterMax(engine) / 2) engine.meterColor = GameEngine.METER_COLOR_RED;
		}

		if(engine.meterValue < 0) engine.meterValue = 0;
		if(engine.meterValue > receiver.getMeterMax(engine)) engine.meterValue = receiver.getMeterMax(engine);
	}

	/*
	 * Soft drop
	 */
	@Override
	public void afterSoftDropFall(GameEngine engine, int playerID, int fall) {
		if((leveltype != LEVELTYPE_MANIA) && (leveltype != LEVELTYPE_MANIAPLUS)) {
			engine.statistics.scoreFromSoftDrop += fall;
			engine.statistics.score += fall;
		}
	}

	/*
	 * Hard drop
	 */
	@Override
	public void afterHardDropFall(GameEngine engine, int playerID, int fall) {
		if((leveltype == LEVELTYPE_MANIA) || (leveltype == LEVELTYPE_MANIAPLUS)) {
			if(fall * 2 > harddropBonus) harddropBonus = fall * 2;
		} else {
			engine.statistics.scoreFromHardDrop += fall * 2;
			engine.statistics.score += fall * 2;
		}
	}

	/*
	 * Render results screen
	 */
	@Override
	public void renderResult(GameEngine engine, int playerID) {
		drawResultStats(engine, playerID, receiver, 0, EventReceiver.COLOR_BLUE,
				STAT_SCORE, STAT_LINES, STAT_LEVEL_ADD_DISP, STAT_TIME, STAT_SPL, STAT_SPM, STAT_LPM);
		if(secretGrade > 0) {
			drawResult(engine, playerID, receiver, 14, EventReceiver.COLOR_BLUE,
					"S. GRADE", String.format("%10s", tableSecretGradeName[secretGrade-1]));
		}
	}

	/*
	 * Called when saving replay
	 */
	@Override
	public void saveReplay(GameEngine engine, int playerID, CustomProperties prop) {
		engine.owner.replayProp.setProperty("practice.version", version);
		if(useMap && (fldBackup != null)) {
			saveMap(fldBackup, prop, 0);
		}
		savePreset(engine, engine.owner.replayProp, -1);
	}
}
