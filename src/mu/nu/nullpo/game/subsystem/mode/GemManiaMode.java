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
import mu.nu.nullpo.game.event.EventReceiver;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.util.CustomProperties;
import mu.nu.nullpo.util.GeneralUtil;

import org.apache.log4j.Logger;

/**
 * GEM MANIA
 */
public class GemManiaMode extends AbstractMode {
	/** Log */
	static Logger log = Logger.getLogger(GemManiaMode.class);

	/** Current version */
	private static final int CURRENT_VERSION = 1;

	/** Maximum stage count */
	private static final int MAX_STAGE_TOTAL = 27;

	/** Normal stage count */
	private static final int MAX_STAGE_NORMAL = 20;

	/** NEXT list */
	private static final String STRING_DEFAULT_NEXT_LIST =
		"1052463015240653120563402534162340621456034251036420314526014362045136455062150461320365204631546310"+
		"6451324023650143620435621456302513025430312603452013625026345012660132450346213462054360143260534215"+
		"0621543621435624013542130562345123641230462134502613542";

	/** 落下速度 table */
	private static final int[] tableGravityValue =
	{
		4, 32, 64, 96, 128, 160, 192, 224, 256, 512, 768, 1024, 768, -1
	};

	/** 落下速度が変わる level */
	private static final int[] tableGravityChangeLevel =
	{
		20, 30, 33, 36, 39, 43, 47, 51, 100, 130, 160, 250, 300, 10000
	};

	/** Number of ranking typesのcount */
	private static final int RANKING_TYPE = 2;

	/** Number of entries in rankings */
	private static final int RANKING_MAX = 10;

	/** Stage set property file */
	private CustomProperties propStageSet;

	/** 残りプラチナBlockcount */
	private int rest;

	/** Current stage number */
	private int stage;

	/** Last stage number */
	private int laststage;

	/** Attempted stage count */
	private int trystage;

	/** Cleared  stage count */
	private int clearstage;

	/** Clear rate */
	private int clearper;

	/** Stage clear flag */
	private boolean clearflag;

	/** Stage skip flag */
	private boolean skipflag;

	/** Time limit left */
	private int limittimeNow;

	/** Time limit at start */
	private int limittimeStart;

	/** Stage time left */
	private int stagetimeNow;

	/** Stage 開始後の経過 time */
	private int cleartime;

	/** Stage time at start */
	private int stagetimeStart;

	/** Stage BGM */
	private int stagebgm;

	/** Current 落下速度の number (tableGravityChangeLevelの levelに到達するたびに1つ増える) */
	private int gravityindex;

	/** Next section level (levelstop when this is -1) */
	private int nextseclv;

	/** Level */
	private int speedlevel;

	/** Levelが増えた flag */
	private boolean lvupflag;

	/** Section Time */
	private int[] sectiontime;

	/** Current time limit extension in seconds */
	private int timeextendSeconds;

	/** Number of frames to display current time limit extension */
	private int timeextendDisp;

	/** Stage clear time limit extension in seconds */
	private int timeextendStageClearSeconds;

	/** Blockを置いた count(1面終了でリセット) */
	private int thisStageTotalPieceLockCount;

	/** Skip buttonを押している time */
	private int skipbuttonPressTime;

	/** Blockピースを置いた count (NEXTピースの計算用）のバックアップ (コンティニュー時に戻す) */
	private int continueNextPieceCount;

	/** Set to true when NO is picked at continue screen */
	private boolean noContinue;

	/** All clear flag */
	private int allclear;

	/** Best time in training mode */
	private int trainingBestTime;

	/** ミラー発動間隔 */
	private int gimmickMirror;

	/** Roll Roll 発動間隔 */
	private int gimmickRoll;

	/** Big発動間隔 */
	private int gimmickBig;

	/** X-RAY発動間隔 */
	private int gimmickXRay;

	/** カラー発動間隔 */
	private int gimmickColor;

	/** Current edit screen */
	private int editModeScreen;

	/** Stage at start */
	private int startstage;

	/** Selected stage set */
	private int stageset;

	/** When true, always ghost ON */
	private boolean alwaysghost;

	/** When true, always 20G */
	private boolean always20g;

	/** When true, levelstop sound is enabled */
	private boolean lvstopse;

	/** When true, section time display is enabled */
	private boolean showsectiontime;

	/** NEXTをランダムにする */
	private boolean randomnext;

	/** Training mode */
	private int trainingType;

	/** Block counter at start */
	private int startnextc;

	/** Version */
	private int version;

	/** Current round's ranking rank */
	private int rankingRank;

	/** Rankings' stage reached */
	private int[][] rankingStage;

	/** Rankings' clear ratio */
	private int[][] rankingClearPer;

	/** Rankings' times */
	private int[][] rankingTime;

	/** Rankings' all clear flag */
	private int[][] rankingAllClear;

	/*
	 * Mode  nameを取得
	 */
	@Override
	public String getName() {
		return "GEM MANIA";
	}

	/*
	 * Initialization
	 */
	@Override
	public void playerInit(GameEngine engine, int playerID) {
		log.debug("playerInit called");

		owner = engine.owner;
		receiver = engine.owner.receiver;

		rest = 0;
		stage = 0;
		laststage = MAX_STAGE_NORMAL - 1;
		trystage = 0;
		clearstage = 0;
		clearper = 0;
		clearflag = false;
		skipflag = false;
		limittimeNow = 0;
		limittimeStart = 0;
		stagetimeNow = 0;
		stagetimeStart = 0;
		cleartime = 0;

		gravityindex = 0;
		nextseclv = 0;
		speedlevel = 0;
		lvupflag = false;

		sectiontime = new int[MAX_STAGE_TOTAL];

		timeextendSeconds = 0;
		timeextendDisp = 0;
		timeextendStageClearSeconds = 0;

		thisStageTotalPieceLockCount = 0;
		skipbuttonPressTime = 0;

		continueNextPieceCount = 0;
		noContinue = false;

		allclear = 0;

		trainingBestTime = -1;

		gimmickMirror = 0;
		gimmickRoll = 0;
		gimmickBig = 0;
		gimmickXRay = 0;
		gimmickColor = 0;

		editModeScreen = 0;

		startstage = 0;
		stageset = -1;
		alwaysghost = false;
		always20g = false;
		lvstopse = true;
		showsectiontime = false;
		randomnext = false;
		trainingType = 0;
		startnextc = 0;

		rankingRank = -1;
		rankingStage = new int[RANKING_TYPE][RANKING_MAX];
		rankingClearPer = new int[RANKING_TYPE][RANKING_MAX];
		rankingTime = new int[RANKING_TYPE][RANKING_MAX];
		rankingAllClear = new int[RANKING_TYPE][RANKING_MAX];

		engine.tspinEnable = false;
		engine.b2bEnable = false;
		engine.framecolor = GameEngine.FRAME_COLOR_PINK;
		engine.comboType = GameEngine.COMBO_TYPE_DISABLE;
		engine.bighalf = true;
		engine.bigmove = false;
		engine.staffrollEnable = false;
		engine.holdButtonNextSkip = true;

		engine.fieldWidth = 10;
		engine.fieldHeight = 20;
		engine.createFieldIfNeeded();

		if(owner.replayMode == false) {
			loadSetting(owner.modeConfig);
			loadRanking(owner.modeConfig, engine.ruleopt.strRuleName);
			version = CURRENT_VERSION;
		} else {
			loadSetting(owner.replayProp);
			version = owner.replayProp.getProperty("gemmania.version", 0);
		}

		if(version <= 0) {
			engine.readyStart = 45;
			engine.readyEnd = 155;
			engine.goStart = 160;
			engine.goEnd = 225;
		}
	}

	/**
	 *  stage 開始時の処理
	 * @param engine GameEngine
	 */
	private void startStage(GameEngine engine) {
		// スピード levelInitialization
		speedlevel = -1;
		gravityindex = 0;
		nextseclv = 100;
		lvupflag = false;
		setSpeed(engine);
		thisStageTotalPieceLockCount = 0;
		continueNextPieceCount = engine.nextPieceCount;

		// Background戻す
		if(owner.backgroundStatus.bg != 0) {
			owner.backgroundStatus.fadesw = true;
			owner.backgroundStatus.fadecount = 0;
			owner.backgroundStatus.fadebg = 0;
		}

		// ghost 復活
		engine.ghost = true;

		// ホールド消去
		engine.holdDisable = false;
		engine.holdPieceObject = null;

		clearflag = false;
		skipflag = false;

		//  stage Map読み込み
		engine.createFieldIfNeeded();
		loadMap(engine.field, propStageSet, stage);
		engine.field.setAllSkin(engine.getSkin());

		//  stage Timeなどを設定
		cleartime = 0;
		sectiontime[stage] = 0;
		stagetimeNow = stagetimeStart;
		rest = engine.field.getHowManyGems();

		if(owner.bgmStatus.bgm != stagebgm) {
			owner.bgmStatus.fadesw = true;
		}
	}

	/**
	 *  stage セットを読み込み
	 * @param id  stage セット number(-1で default )
	 */
	private void loadStageSet(int id) {
		if(id >= 0) {
			log.debug("Loading stage set from custom set #" + id);
			propStageSet = receiver.loadProperties("config/map/gemmania/custom" + id + ".map");
		} else {
			log.debug("Loading stage set from default set");
			propStageSet = receiver.loadProperties("config/map/gemmania/default.map");
		}

		if(propStageSet == null) propStageSet = new CustomProperties();
	}

	/**
	 *  stage セットを保存
	 * @param id  stage セット number(-1で default )
	 */
	private void saveStageSet(int id) {
		if((propStageSet != null) && (!owner.replayMode)) {
			if(id >= 0) {
				log.debug("Saving stage set to custom set #" + id);
				receiver.saveProperties("config/map/gemmania/custom" + id + ".map", propStageSet);
			} else {
				log.debug("Saving stage set to default set");
				receiver.saveProperties("config/map/gemmania/default.map", propStageSet);
			}
		}
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
		limittimeStart = prop.getProperty(id + ".gemmania.limittimeStart", 3600 * 3);
		stagetimeStart = prop.getProperty(id + ".gemmania.stagetimeStart", 3600 * 1);
		stagebgm = prop.getProperty(id + ".gemmania.stagebgm", BGMStatus.BGM_PUZZLE1);
		gimmickMirror = prop.getProperty(id + ".gemmania.gimmickMirror", 0);
		gimmickRoll = prop.getProperty(id + ".gemmania.gimmickRoll", 0);
		gimmickBig = prop.getProperty(id + ".gemmania.gimmickBig", 0);
		gimmickXRay = prop.getProperty(id + ".gemmania.gimmickXRay", 0);
		gimmickColor = prop.getProperty(id + ".gemmania.gimmickColor", 0);
	}

	/**
	 * Map保存
	 * @param field field
	 * @param prop Property file to save to
	 * @param id 任意のID
	 */
	private void saveMap(Field field, CustomProperties prop, int id) {
		field.writeProperty(prop, id);
		prop.setProperty(id + ".gemmania.limittimeStart", limittimeStart);
		prop.setProperty(id + ".gemmania.stagetimeStart", stagetimeStart);
		prop.setProperty(id + ".gemmania.stagebgm", stagebgm);
		prop.setProperty(id + ".gemmania.gimmickMirror", gimmickMirror);
		prop.setProperty(id + ".gemmania.gimmickRoll", gimmickRoll);
		prop.setProperty(id + ".gemmania.gimmickBig", gimmickBig);
		prop.setProperty(id + ".gemmania.gimmickXRay", gimmickXRay);
		prop.setProperty(id + ".gemmania.gimmickColor", gimmickColor);
	}

	/**
	 * Load settings from property file
	 * @param prop Property file
	 */
	protected void loadSetting(CustomProperties prop) {
		startstage = prop.getProperty("gemmania.startstage", 0);
		stageset = prop.getProperty("gemmania.stageset", -1);
		alwaysghost = prop.getProperty("gemmania.alwaysghost", false);
		always20g = prop.getProperty("gemmania.always20g", false);
		lvstopse = prop.getProperty("gemmania.lvstopse", true);
		showsectiontime = prop.getProperty("gemmania.showsectiontime", false);
		randomnext = prop.getProperty("gemmania.randomnext", false);
		trainingType = prop.getProperty("gemmania.trainingType", 0);
		startnextc = prop.getProperty("gemmania.startnextc", 0);
	}

	/**
	 * Save settings to property file
	 * @param prop Property file
	 */
	protected void saveSetting(CustomProperties prop) {
		prop.setProperty("gemmania.startstage", startstage);
		prop.setProperty("gemmania.stageset", stageset);
		prop.setProperty("gemmania.alwaysghost", alwaysghost);
		prop.setProperty("gemmania.always20g", always20g);
		prop.setProperty("gemmania.lvstopse", lvstopse);
		prop.setProperty("gemmania.showsectiontime", showsectiontime);
		prop.setProperty("gemmania.randomnext", randomnext);
		prop.setProperty("gemmania.trainingType", trainingType);
		prop.setProperty("gemmania.startnextc", startnextc);
	}

	/**
	 * Update falling speed
	 * @param engine GameEngine
	 */
	private void setSpeed(GameEngine engine) {
		if(always20g == true) {
			engine.speed.gravity = -1;
		} else {
			while(speedlevel >= tableGravityChangeLevel[gravityindex]) gravityindex++;
			engine.speed.gravity = tableGravityValue[gravityindex];
		}

		engine.speed.are = 23;
		engine.speed.areLine = 23;
		engine.speed.lockDelay = 31;

		if(speedlevel >= 300) {
			engine.speed.lineDelay = 25;
			engine.speed.das = 15;
		} else {
			engine.speed.lineDelay = 40;
			engine.speed.das = 9;
		}

		if((speedlevel >= 100) && (!alwaysghost)) engine.ghost = false;
	}

	/**
	 *  Stage clearや time切れの判定
	 * @param engine GameEngine
	 */
	private void checkStageEnd(GameEngine engine) {
		if( (clearflag) || ((stagetimeNow <= 0) && (stagetimeStart > 0) && (engine.timerActive)) ) {
			skipflag = false;
			engine.nowPieceObject = null;
			engine.timerActive = false;
			engine.stat = GameEngine.STAT_CUSTOM;
			engine.resetStatc();
		} else if((limittimeNow <= 0) && (engine.timerActive)) {
			engine.nowPieceObject = null;
			engine.stat = GameEngine.STAT_GAMEOVER;
			engine.resetStatc();
		}
	}

	/**
	 *  stage  numberをStringで取得
	 * @param stageNumber  stage  number
	 * @return  stage  numberの文字列(21面以降はEX扱い)
	 */
	private String getStageName(int stageNumber) {
		if(stageNumber >= MAX_STAGE_NORMAL) {
			return "EX" + ((stageNumber + 1) - MAX_STAGE_NORMAL);
		}
		return "" + (stageNumber + 1);
	}

	/*
	 * Called at settings screen
	 */
	@Override
	public boolean onSetting(GameEngine engine, int playerID) {
		// エディットMenu  メイン画面
		if(editModeScreen == 1) {
			// Configuration changes
			int change = updateCursor(engine, 4);

			if(change != 0) {
				engine.playSE("change");

				switch(menuCursor) {
				case 0:
					break;
				case 1:
				case 2:
					startstage += change;
					if(startstage < 0) startstage = MAX_STAGE_TOTAL - 1;
					if(startstage > MAX_STAGE_TOTAL - 1) startstage = 0;
					break;
				case 3:
				case 4:
					stageset += change;
					if(stageset < 0) stageset = 99;
					if(stageset > 99) stageset = 0;
					break;
				}
			}

			// 決定
			if(engine.ctrl.isPush(Controller.BUTTON_A) && (menuTime >= 5)) {
				engine.playSE("decide");

				switch(menuCursor) {
				case 0:
					editModeScreen = 2;
					menuCursor = 0;
					menuTime = 0;
					break;
				case 1:
					if((propStageSet != null) && (engine.field != null)) {
						loadMap(engine.field, propStageSet, startstage);
						engine.field.setAllSkin(engine.getSkin());
					}
					break;
				case 2:
					if((propStageSet != null) && (engine.field != null)) {
						saveMap(engine.field, propStageSet, startstage);
					}
					break;
				case 3:
					loadStageSet(stageset);
					break;
				case 4:
					saveStageSet(stageset);
					break;
				}
			}

			// Cancel
			if(engine.ctrl.isPress(Controller.BUTTON_D) && engine.ctrl.isPress(Controller.BUTTON_E)) {
				editModeScreen = 0;
				menuCursor = 0;
				menuTime = 0;
			}

			menuTime++;
		}
		// エディットMenu   stage 画面
		else if(editModeScreen == 2) {
			// Up
			if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_UP)) {
				menuCursor--;
				if(menuCursor < 0) menuCursor = 4;
				engine.playSE("cursor");
			}
			// Down
			if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_DOWN)) {
				menuCursor++;
				if(menuCursor > 4) menuCursor = 0;
				engine.playSE("cursor");
			}

			// Configuration changes
			int change = 0;
			if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_LEFT)) change = -1;
			if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_RIGHT)) change = 1;

			if(change != 0) {
				engine.playSE("change");

				int m = 1;
				if(engine.ctrl.isPress(Controller.BUTTON_E)) m = 100;
				if(engine.ctrl.isPress(Controller.BUTTON_F)) m = 1000;

				switch(menuCursor) {
				case 0:
					break;
				case 1:
					stagetimeStart += change * 60 * m;
					if(stagetimeStart < 0) stagetimeStart = 3600 * 20;
					if(stagetimeStart > 3600 * 20) stagetimeStart = 0;
					break;
				case 2:
					limittimeStart += change * 60 * m;
					if(limittimeStart < 0) limittimeStart = 3600 * 20;
					if(limittimeStart > 3600 * 20) limittimeStart = 0;
					break;
				case 3:
					stagebgm += change;
					if(stagebgm < 0) stagebgm = BGMStatus.BGM_COUNT - 1;
					if(stagebgm > BGMStatus.BGM_COUNT - 1) stagebgm = 0;
					break;
				case 4:
					gimmickMirror += change;
					if(gimmickMirror < 0) gimmickMirror = 99;
					if(gimmickMirror > 99) gimmickMirror = 0;
					break;
				}
			}

			// 決定
			if(engine.ctrl.isPush(Controller.BUTTON_A) && (menuTime >= 5)) {
				engine.playSE("decide");

				if(menuCursor == 0) {
					engine.enterFieldEdit();
					return true;
				} else {
					editModeScreen = 1;
					menuCursor = 0;
					menuTime = 0;
				}
			}

			// Cancel
			if(engine.ctrl.isPush(Controller.BUTTON_B) && (menuTime >= 5)) {
				editModeScreen = 1;
				menuCursor = 0;
				menuTime = 0;
			}

			menuTime++;
		}
		// 普通のMenu
		else if(engine.owner.replayMode == false) {
			// Up
			if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_UP)) {
				menuCursor--;
				if(menuCursor < 0) menuCursor = 8;
				engine.playSE("cursor");
			}
			// Down
			if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_DOWN)) {
				menuCursor++;
				if(menuCursor > 8) menuCursor = 0;
				engine.playSE("cursor");
			}

			// Configuration changes
			int change = 0;
			if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_LEFT)) change = -1;
			if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_RIGHT)) change = 1;

			if(change != 0) {
				engine.playSE("change");

				switch(menuCursor) {
				case 0:
					startstage += change;
					if(startstage < 0) startstage = MAX_STAGE_TOTAL - 1;
					if(startstage > MAX_STAGE_TOTAL - 1) startstage = 0;

					if(propStageSet == null) loadStageSet(stageset);
					loadMap(engine.field, propStageSet, startstage);
					engine.field.setAllSkin(engine.getSkin());

					break;
				case 1:
					stageset += change;
					if(stageset < -1) stageset = 99;
					if(stageset > 99) stageset = -1;

					loadStageSet(stageset);
					loadMap(engine.field, propStageSet, startstage);
					engine.field.setAllSkin(engine.getSkin());

					break;
				case 2:
					alwaysghost = !alwaysghost;
					break;
				case 3:
					always20g = !always20g;
					break;
				case 4:
					lvstopse = !lvstopse;
					break;
				case 5:
					showsectiontime = !showsectiontime;
					break;
				case 6:
					randomnext = !randomnext;
					break;
				case 7:
					trainingType += change;
					if(trainingType < 0) trainingType = 2;
					if(trainingType > 2) trainingType = 0;
					break;
				case 8:
					startnextc += change;
					if(startnextc < 0) startnextc = STRING_DEFAULT_NEXT_LIST.length() - 1;
					if(startnextc > STRING_DEFAULT_NEXT_LIST.length() - 1) startnextc = 0;
					break;
				}
			}

			// 決定
			if(engine.ctrl.isPush(Controller.BUTTON_A) && (menuTime >= 5)) {
				engine.playSE("decide");
				saveSetting(owner.modeConfig);
				receiver.saveModeConfig(owner.modeConfig);
				return false;
			}

			// Cancel
			if(engine.ctrl.isPush(Controller.BUTTON_B)) {
				engine.quitflag = true;
			}

			// エディット
			if(engine.ctrl.isPush(Controller.BUTTON_D)) {
				if(stageset < 0) stageset = 0;

				loadStageSet(stageset);
				loadMap(engine.field, propStageSet, startstage);
				engine.field.setAllSkin(engine.getSkin());

				editModeScreen = 1;
				menuCursor = 0;
				menuTime = 0;
			}

			menuTime++;
		} else {
			menuTime++;
			menuCursor = -1;

			if(menuTime >= 60) {
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
		if(editModeScreen == 1) {
			drawMenu(engine, playerID, receiver, 0, EventReceiver.COLOR_GREEN, 0,
					"STAGE EDIT", "[PUSH A]",
					"LOAD STAGE", "[" + getStageName(startstage) + "]",
					"SAVE STAGE", "[" + getStageName(startstage) + "]",
					"LOAD", "[SET " + stageset + "]",
					"SAVE", "[SET " + stageset + "]");

			receiver.drawMenuFont(engine, playerID, 0, 19, "EXIT-> D+E", EventReceiver.COLOR_ORANGE);
		} else if(editModeScreen == 2) {
			// エディットMenu   stage 画面
			drawMenu(engine, playerID, receiver, 0, EventReceiver.COLOR_GREEN, 0,
					"MAP EDIT", "[PUSH A]",
					"STAGE TIME", GeneralUtil.getTime(stagetimeStart),
					"LIMIT TIME", GeneralUtil.getTime(limittimeStart),
					"BGM", String.valueOf(stagebgm),
					"MIRROR", (gimmickMirror == 0) ? "OFF" : String.valueOf(gimmickMirror));
		} else {
			// 普通のMenu
			if(engine.owner.replayMode == false) {
				receiver.drawMenuFont(engine, playerID, 0, 19, "D:EDIT", EventReceiver.COLOR_ORANGE);
			}
			String strTrainingType = "OFF";
			if(trainingType == 1) strTrainingType = "ON";
			if(trainingType == 2) strTrainingType = "ON+RESET";
			drawMenu(engine, playerID, receiver, 0, EventReceiver.COLOR_PINK, 0,
					"STAGE NO.", getStageName(startstage),
					"STAGE SET", (stageset < 0) ? "DEFAULT" : "EDIT "+stageset,
					"FULL GHOST", GeneralUtil.getONorOFF(alwaysghost),
					"20G MODE", GeneralUtil.getONorOFF(always20g),
					"LVSTOPSE", GeneralUtil.getONorOFF(lvstopse),
					"SHOW STIME", GeneralUtil.getONorOFF(showsectiontime),
					"RANDOM", GeneralUtil.getONorOFF(randomnext),
					"TRAINING", strTrainingType,
					"NEXT COUNT", String.valueOf(startnextc));
		}
	}

	/*
	 * Ready画面の処理
	 */
	@Override
	public boolean onReady(GameEngine engine, int playerID) {
		if(engine.statc[0] == 0) {
			if(!engine.readyDone) {
				loadStageSet(stageset);
				stage = startstage;
				engine.nextPieceCount = startnextc;

				if(!randomnext)
					engine.nextPieceArrayID = GeneralUtil.createNextPieceArrayFromNumberString(STRING_DEFAULT_NEXT_LIST);
			}

			startStage(engine);

			if(!engine.readyDone) {
				limittimeNow = limittimeStart;
			}
		}
		return false;
	}

	/*
	 * Ready画面の描画処理
	 */
	@Override
	public void renderReady(GameEngine engine, int playerID) {
		if(engine.statc[0] >= engine.readyStart) {
			// トレーニング
			if(trainingType != 0) {
				receiver.drawMenuFont(engine, playerID, 1, 5, "TRAINING", EventReceiver.COLOR_GREEN);
			}

			// STAGE XX
			if(stage >= MAX_STAGE_NORMAL) {
				receiver.drawMenuFont(engine, playerID, 0, 7, "EX STAGE ", EventReceiver.COLOR_GREEN);
				receiver.drawMenuFont(engine, playerID, 9, 7, "" + (stage + 1 - MAX_STAGE_NORMAL));
			} else {
				receiver.drawMenuFont(engine, playerID, 1, 7, "STAGE", EventReceiver.COLOR_GREEN);
				String strStage = String.format("%2s", getStageName(stage));
				receiver.drawMenuFont(engine, playerID, 7, 7, strStage);
			}
		}
	}

	/*
	 * Called at game start(2回目以降のReadyも含む)
	 */
	@Override
	public void startGame(GameEngine engine, int playerID) {
		// X-RAY開始
		if(gimmickXRay > 0) engine.itemXRayEnable = true;
		// カラー開始
		if(gimmickColor > 0) engine.itemColorEnable = true;

		// BGM切り替え
		owner.bgmStatus.fadesw = false;
		owner.bgmStatus.bgm = stagebgm;
	}

	/*
	 * Render score
	 */
	@Override
	public void renderLast(GameEngine engine, int playerID) {
		receiver.drawScoreFont(engine, playerID, 0, 0, "GEM MANIA " + ((randomnext) ? "(RANDOM)" : ""), EventReceiver.COLOR_RED);

		if( (engine.stat == GameEngine.STAT_SETTING) || ((engine.stat == GameEngine.STAT_RESULT) && (owner.replayMode == false)) ) {
			if((startstage == 0) && (always20g == false) && (trainingType == 0) && (startnextc == 0) && (stageset < 0) && (engine.ai == null)) {
				float scale = (receiver.getNextDisplayType() == 2) ? 0.5f : 1.0f;
				int topY = (receiver.getNextDisplayType() == 2) ? 5 : 3;

				receiver.drawScoreFont(engine, playerID, 3, topY-1, "STAGE CLEAR TIME", EventReceiver.COLOR_PINK, scale);
				int type = randomnext ? 1 : 0;

				for(int i = 0; i < RANKING_MAX; i++) {
					int gcolor = EventReceiver.COLOR_WHITE;
					if(rankingAllClear[type][i] == 1) gcolor = EventReceiver.COLOR_GREEN;
					if(rankingAllClear[type][i] == 2) gcolor = EventReceiver.COLOR_ORANGE;

					receiver.drawScoreFont(engine, playerID, 0, topY+i, String.format("%2d", i + 1), EventReceiver.COLOR_YELLOW, scale);
					receiver.drawScoreFont(engine, playerID, 3, topY+i, getStageName(rankingStage[type][i]), gcolor, scale);
					receiver.drawScoreFont(engine, playerID, 9, topY+i, rankingClearPer[type][i] + "%", (i == rankingRank), scale);
					receiver.drawScoreFont(engine, playerID, 15, topY+i, GeneralUtil.getTime(rankingTime[type][i]), (i == rankingRank), scale);
				}
			}
		} else {
			receiver.drawScoreFont(engine, playerID, 0, 2, "STAGE", EventReceiver.COLOR_PINK);
			receiver.drawScoreFont(engine, playerID, 0, 3, getStageName(stage));
			if(gimmickMirror > 0) {
				receiver.drawScoreFont(engine, playerID, 0, 4, "MIRROR", EventReceiver.COLOR_RED);
			} else if(gimmickRoll > 0) {
				receiver.drawScoreFont(engine, playerID, 0, 4, "ROLL ROLL", EventReceiver.COLOR_RED);
			} else if(gimmickBig > 0) {
				receiver.drawScoreFont(engine, playerID, 0, 4, "DEATH BLOCK", EventReceiver.COLOR_RED);
			} else if(gimmickXRay > 0) {
				receiver.drawScoreFont(engine, playerID, 0, 4, "X-RAY", EventReceiver.COLOR_RED);
			} else if(gimmickColor > 0) {
				receiver.drawScoreFont(engine, playerID, 0, 4, "COLOR", EventReceiver.COLOR_RED);
			}

			receiver.drawScoreFont(engine, playerID, 0, 5, "REST", EventReceiver.COLOR_PINK);
			receiver.drawScoreFont(engine, playerID, 0, 6, ""+rest);

			if(trainingType == 0) {
				receiver.drawScoreFont(engine, playerID, 0, 8, "CLEAR", EventReceiver.COLOR_PINK);
				receiver.drawScoreFont(engine, playerID, 0, 9, clearper + "%");
			} else {
				receiver.drawScoreFont(engine, playerID, 0, 8, "BEST TIME", EventReceiver.COLOR_PINK);
				receiver.drawScoreFont(engine, playerID, 0, 9, GeneralUtil.getTime(trainingBestTime));
			}

			//  level
			receiver.drawScoreFont(engine, playerID, 0, 11, "LEVEL", EventReceiver.COLOR_PINK);
			int tempLevel = speedlevel;
			if(tempLevel < 0) tempLevel = 0;
			String strLevel = String.format("%3d", tempLevel);
			receiver.drawScoreFont(engine, playerID, 0, 12, strLevel);

			int speed = engine.speed.gravity / 128;
			if(engine.speed.gravity < 0) speed = 40;
			receiver.drawSpeedMeter(engine, playerID, 0, 13, speed);

			receiver.drawScoreFont(engine, playerID, 0, 14, String.format("%3d", nextseclv));

			//  stage Time
			if(stagetimeStart > 0) {
				receiver.drawScoreFont(engine, playerID, 0, 16, "STAGE TIME", EventReceiver.COLOR_PINK);
				receiver.drawScoreFont(engine, playerID, 0, 17, GeneralUtil.getTime(stagetimeNow),
										((engine.timerActive) && (stagetimeNow < 600) && (stagetimeNow % 4 == 0)));
			}

			// Time limit
			if(limittimeStart > 0) {
				receiver.drawScoreFont(engine, playerID, 0, 19, "LIMIT TIME", EventReceiver.COLOR_PINK);
				String strLimitTime = GeneralUtil.getTime(limittimeNow);
				if(timeextendDisp > 0) {
					strLimitTime += "\n(+" + timeextendSeconds + " SEC.)";
				}
				receiver.drawScoreFont(engine, playerID, 0, 20, strLimitTime, ((engine.timerActive) && (limittimeNow < 600) && (limittimeNow % 4 == 0)));
			}

			// Section Time
			if((showsectiontime == true) && (sectiontime != null)) {
				int y = (receiver.getNextDisplayType() == 2) ? 4 : 2;
				int x = (receiver.getNextDisplayType() == 2) ? 22 : 12;
				float scale = (receiver.getNextDisplayType() == 2) ? 0.5f : 1.0f;

				receiver.drawScoreFont(engine, playerID, x, y, "SECTION TIME", EventReceiver.COLOR_PINK, scale);

				for(int i = 0; i < sectiontime.length; i++) {
					if(sectiontime[i] != 0) {
						String strSeparator = " ";
						if((i == stage) && (engine.ending == 0)) strSeparator = "b";

						String strSectionTime = String.format("%3s%s%s", getStageName(i), strSeparator, GeneralUtil.getTime(sectiontime[i]));
						if(sectiontime[i] == -1) {
							strSectionTime = String.format("%3s%s%s", getStageName(i), strSeparator, "FAILED");
						} else if(sectiontime[i] == -2) {
							strSectionTime = String.format("%3s%s%s", getStageName(i), strSeparator, "SKIPPED");
						}

						int pos = i - Math.max(stage-14,0);

						if (pos >= 0) receiver.drawScoreFont(engine, playerID, x, y + 1 + pos, strSectionTime, scale);
					}
				}

				if(receiver.getNextDisplayType() == 2) {
					receiver.drawScoreFont(engine, playerID, 11, 19, "TOTAL", EventReceiver.COLOR_PINK);
					receiver.drawScoreFont(engine, playerID, 11, 20, GeneralUtil.getTime(engine.statistics.time));
				} else {
					receiver.drawScoreFont(engine, playerID, 12, 19, "TOTAL TIME", EventReceiver.COLOR_PINK);
					receiver.drawScoreFont(engine, playerID, 12, 20, GeneralUtil.getTime(engine.statistics.time));
				}
			}
		}
	}

	/*
	 * Called after every frame
	 */
	@Override
	public void onLast(GameEngine engine, int playerID) {
		if(timeextendDisp > 0) timeextendDisp--;

		if(engine.gameActive && engine.timerActive && engine.ctrl.isPress(Controller.BUTTON_F)) {
			skipbuttonPressTime++;

			if((skipbuttonPressTime >= 60) && ((stage < MAX_STAGE_NORMAL - 1) || (trainingType != 0)) && (limittimeNow > 30 * 60) && (!clearflag)) {
				skipflag = true;
				engine.nowPieceObject = null;
				engine.timerActive = false;
				engine.stat = GameEngine.STAT_CUSTOM;
				engine.resetStatc();
			}
		} else {
			skipbuttonPressTime = 0;
		}

		// 経過 time
		if(engine.gameActive && engine.timerActive) {
			cleartime++;
			sectiontime[stage]++;
		}

		// Time limit
		if(engine.gameActive && engine.timerActive && (limittimeNow > 0)) {
			limittimeNow--;

			// Time meter
			if(limittimeNow >= limittimeStart) {
				engine.meterValue = receiver.getMeterMax(engine);
			} else {
				engine.meterValue = (limittimeNow * receiver.getMeterMax(engine)) / limittimeStart;
			}
			engine.meterColor = GameEngine.METER_COLOR_GREEN;
			if(limittimeNow <= 60*60) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
			if(limittimeNow <= 30*60) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
			if(limittimeNow <= 10*60) engine.meterColor = GameEngine.METER_COLOR_RED;

			if((limittimeNow > 0) && (limittimeNow <= 10 * 60) && (limittimeNow % 60 == 0)) {
				// 10秒前からのカウントダウン
				engine.playSE("countdown");
			}
		}

		//  stage Time
		if(engine.gameActive && engine.timerActive && (stagetimeNow > 0)) {
			stagetimeNow--;

			if((stagetimeNow > 0) && (stagetimeNow <= 10 * 60) && (stagetimeNow % 60 == 0)) {
				// 10秒前からのカウントダウン
				engine.playSE("countdown");
			}
		}
	}

	/*
	 * 移動中の処理
	 */
	@Override
	public boolean onMove(GameEngine engine, int playerID) {
		// 新規ピース出現時
		if((engine.ending == 0) && (engine.statc[0] == 0) && (engine.holdDisable == false) && (!lvupflag)) {
			// Level up
			if(speedlevel < nextseclv - 1) {
				speedlevel++;
				if((speedlevel == nextseclv - 1) && (lvstopse == true)) engine.playSE("levelstop");
			}
			setSpeed(engine);
		}
		if((engine.ending == 0) && (engine.statc[0] > 0)) {
			lvupflag = false;
		}

		if((engine.ending == 0) && (engine.statc[0] == 0) && (engine.holdDisable == false)) {
			// Roll Roll
			engine.itemRollRollEnable = ((gimmickRoll > 0) && ((thisStageTotalPieceLockCount + 1) % gimmickRoll == 0));
			// Big
			engine.big = ((gimmickBig > 0) && ((thisStageTotalPieceLockCount + 1) % gimmickBig == 0));

			// X-RAY
			if(gimmickXRay > 0) {
				if(thisStageTotalPieceLockCount % gimmickXRay == 0) {
					engine.itemXRayEnable = true;
				} else {
					engine.itemXRayEnable = false;
					engine.resetFieldVisible();
				}
			}

			// カラー
			if(gimmickColor > 0) {
				if(thisStageTotalPieceLockCount % gimmickColor == 0) {
					engine.itemColorEnable = true;
				} else {
					engine.itemColorEnable = false;
					engine.resetFieldVisible();
				}
			}
		}

		return false;
	}

	/*
	 * ARE中の処理
	 */
	@Override
	public boolean onARE(GameEngine engine, int playerID) {
		// 最後の frame
		if((engine.ending == 0) && (engine.statc[0] >= engine.statc[1] - 1) && (!lvupflag)) {
			if(speedlevel < nextseclv - 1) {
				speedlevel++;
				if((speedlevel == nextseclv - 1) && (lvstopse == true)) engine.playSE("levelstop");
			}
			setSpeed(engine);
			lvupflag = true;
		}

		return false;
	}

	/*
	 * Calculate score
	 */
	@Override
	public void calcScore(GameEngine engine, int playerID, int lines) {
		// 実際に消えるLinescount(Big時半分にならない)
		int realLines = engine.field.getLines();

		if((realLines >= 1) && (engine.ending == 0)) {
			// 宝石消去
			int gemClears = engine.field.getHowManyGemClears();
			if(gemClears > 0) {
				rest -= gemClears;
				if(rest <= 0) clearflag = true;
				limittimeNow += 60 * gemClears;
				timeextendSeconds = gemClears;
				timeextendDisp = 120;
			}

			// Level up
			int levelplus = lines;
			if(lines == 3) levelplus = 4;
			if(lines >= 4) levelplus = 6;

			speedlevel += levelplus;

			setSpeed(engine);

			if(speedlevel > 998) {
				speedlevel = 998;
			} else if(speedlevel >= nextseclv) {
				// Next Section
				engine.playSE("levelup");

				// Background切り替え
				owner.backgroundStatus.fadesw = true;
				owner.backgroundStatus.fadecount = 0;
				owner.backgroundStatus.fadebg = nextseclv / 100;

				// Update level for next section
				nextseclv += 100;
			} else if((speedlevel == nextseclv - 1) && (lvstopse == true)) {
				engine.playSE("levelstop");
			}
		}
	}

	/*
	 * Line clear処理が終わったときの処理
	 */
	@Override
	public boolean lineClearEnd(GameEngine engine, int playerID) {
		checkStageEnd(engine);

		return false;
	}

	/*
	 * Blockピースが固定されたときの処理(calcScoreの直後)
	 */
	@Override
	public void pieceLocked(GameEngine engine, int playerID, int lines) {
		// 固定 count+1
		thisStageTotalPieceLockCount++;

		// ミラー
		if((gimmickMirror > 0) && (thisStageTotalPieceLockCount % gimmickMirror == 0)) {
			engine.interruptItemNumber = GameEngine.INTERRUPTITEM_MIRROR;
		}

		//  stage 終了判定
		if(lines <= 0) checkStageEnd(engine);
	}

	/**
	 *  stage 終了画面の描画
	 */
	@Override
	public boolean onCustom(GameEngine engine, int playerID) {
		// 最初の frame の処理
		if(engine.statc[0] == 0) {
			// Sound effects
			if(clearflag) engine.playSE("stageclear");
			else engine.playSE("stagefail");

			// Cleared  stage +1
			if(clearflag) clearstage++;

			// クリア率計算
			trystage++;
			clearper = (clearstage * 100) / trystage;

			// Time bonus
			timeextendStageClearSeconds = 0;
			if(clearflag) {
				if(cleartime < 10 * 60) timeextendStageClearSeconds = 10;
				else if(cleartime < 20 * 60) timeextendStageClearSeconds = 5;

				if(stage == MAX_STAGE_NORMAL - 1) timeextendStageClearSeconds += 60;
			} else if(skipflag) {
				timeextendStageClearSeconds = 30;
			}

			// 最終 stage を決定
			if(stage == MAX_STAGE_NORMAL - 1) {
				if(clearper < 90)
					laststage = 19;	// クリア率が90%に満たない場合は stage 20で終了
				else if(clearper < 100)
					laststage = 22;	// クリア率が90～99%はEX3まで
				else if(engine.statistics.time > 5 * 3600)
					laststage = 24;	// クリア率が100%で5分超えている場合はEX5
				else
					laststage = MAX_STAGE_TOTAL - 1;	// クリア率が100%で5分以内ならEX7
			}

			// BGM fadeout
			if( ((stage == MAX_STAGE_NORMAL - 1) || (stage == laststage)) && (trainingType == 0) ) {
				owner.bgmStatus.fadesw = true;
			}

			// ギミック解除
			engine.interruptItemNumber = GameEngine.INTERRUPTITEM_NONE;
			engine.itemXRayEnable = false;
			engine.itemColorEnable = false;
			engine.resetFieldVisible();

			// Section Time設定
			if(!clearflag) {
				if(!skipflag)
					sectiontime[stage] = -1;	// Out of time
				else
					sectiontime[stage] = -2;	// スキップ
			} else {
				sectiontime[stage] = cleartime;
			}

			// トレーニングでのベストTime
			if( (trainingType != 0) && (clearflag) && ((cleartime < trainingBestTime) || (trainingBestTime < 0)) ) {
				trainingBestTime = cleartime;
			}
		}

		// Time limitが増える演出
		if(engine.statc[1] < timeextendStageClearSeconds * 60) {
			if(timeextendStageClearSeconds < 30) {
				engine.statc[1] += 4;
			} else if(timeextendStageClearSeconds < 60) {
				engine.statc[1] += 10;
			} else {
				engine.statc[1] += 30;
			}

			// Time meter
			int limittimeTemp = limittimeNow + engine.statc[1];
			if(skipflag) limittimeTemp = limittimeNow - engine.statc[1];

			if(limittimeTemp >= limittimeStart) {
				engine.meterValue = receiver.getMeterMax(engine);
			} else {
				engine.meterValue = (limittimeTemp * receiver.getMeterMax(engine)) / limittimeStart;
			}
			engine.meterColor = GameEngine.METER_COLOR_GREEN;
			if(limittimeTemp <= 60*60) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
			if(limittimeTemp <= 30*60) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
			if(limittimeTemp <= 10*60) engine.meterColor = GameEngine.METER_COLOR_RED;
		}

		// Next 画面へ
		if((engine.statc[0] >= 300) || (engine.ctrl.isPush(Controller.BUTTON_A))) {
			// Training
			if(trainingType != 0) {
				if(clearflag) limittimeNow += timeextendStageClearSeconds * 60;
				if(skipflag) limittimeNow -= timeextendStageClearSeconds * 60;
				if(trainingType == 2) engine.nextPieceCount = continueNextPieceCount;
				engine.stat = GameEngine.STAT_READY;
				engine.resetStatc();
			}
			// Ending
			else if(stage >= laststage) {
				allclear = (stage >= MAX_STAGE_TOTAL - 1) ? 2 : 1;
				engine.ending = 1;
				engine.gameEnded();
				engine.stat = GameEngine.STAT_ENDINGSTART;
				engine.resetStatc();
			}
			// Next  stage
			else {
				stage++;
				if(clearflag) limittimeNow += timeextendStageClearSeconds * 60;
				if(skipflag) limittimeNow -= timeextendStageClearSeconds * 60;
				engine.stat = GameEngine.STAT_READY;
				engine.resetStatc();
			}
			return true;
		}

		engine.statc[0]++;

		return true;
	}

	/**
	 *  stage 終了画面の描画
	 */
	@Override
	public void renderCustom(GameEngine engine, int playerID) {
		if(engine.statc[0] < 1) return;

		// STAGE XX
		receiver.drawMenuFont(engine, playerID, 1, 2, "STAGE", EventReceiver.COLOR_GREEN);
		String strStage = String.format("%2s", getStageName(stage));
		receiver.drawMenuFont(engine, playerID, 7, 2, strStage);

		if(clearflag) {
			// クリア
			receiver.drawMenuFont(engine, playerID, 2, 4, "CLEAR!", (engine.statc[0] % 2 == 0), EventReceiver.COLOR_WHITE, EventReceiver.COLOR_ORANGE);

			receiver.drawMenuFont(engine, playerID, 0, 7, "LIMIT TIME", EventReceiver.COLOR_PINK);
			receiver.drawMenuFont(engine, playerID, 1, 8, GeneralUtil.getTime(limittimeNow + engine.statc[1]),
					((engine.statc[0] % 2 == 0) && (engine.statc[1] < timeextendStageClearSeconds * 60)),
					EventReceiver.COLOR_WHITE, EventReceiver.COLOR_ORANGE);

			receiver.drawMenuFont(engine, playerID, 2, 10, "EXTEND", EventReceiver.COLOR_PINK);
			receiver.drawMenuFont(engine, playerID, 2, 11, timeextendStageClearSeconds + " SEC.");

			receiver.drawMenuFont(engine, playerID, 0, 13, "CLEAR TIME", EventReceiver.COLOR_PINK);
			receiver.drawMenuFont(engine, playerID, 1, 14, GeneralUtil.getTime(cleartime));

			receiver.drawMenuFont(engine, playerID, 0, 16, "TOTAL TIME", EventReceiver.COLOR_PINK);
			receiver.drawMenuFont(engine, playerID, 1, 17, GeneralUtil.getTime(engine.statistics.time));
		} else if(skipflag) {
			// スキップ
			receiver.drawMenuFont(engine, playerID, 1, 4, "SKIPPED");
			receiver.drawMenuFont(engine, playerID, 1, 5, "-30 SEC.");

			receiver.drawMenuFont(engine, playerID, 0, 10, "LIMIT TIME", EventReceiver.COLOR_PINK);
			receiver.drawMenuFont(engine, playerID, 1, 11, GeneralUtil.getTime(limittimeNow - engine.statc[1]),
					((engine.statc[0] % 2 == 0) && (engine.statc[1] < 30 * 60)),
					EventReceiver.COLOR_WHITE, EventReceiver.COLOR_RED);

			if(trainingType == 0) {
				receiver.drawMenuFont(engine, playerID, 0, 13, "CLEAR PER.", EventReceiver.COLOR_PINK);
				receiver.drawMenuFont(engine, playerID, 3, 14, clearper + "%");
			}

			receiver.drawMenuFont(engine, playerID, 0, 16, "TOTAL TIME", EventReceiver.COLOR_PINK);
			receiver.drawMenuFont(engine, playerID, 1, 17, GeneralUtil.getTime(engine.statistics.time));
		} else if((stagetimeNow <= 0) && (stagetimeStart > 0)) {
			// Timeアップ
			receiver.drawMenuFont(engine, playerID, 1, 4, "TIME UP!");
			receiver.drawMenuFont(engine, playerID, 1, 5, "TRY NEXT");

			receiver.drawMenuFont(engine, playerID, 0, 10, "LIMIT TIME", EventReceiver.COLOR_PINK);
			receiver.drawMenuFont(engine, playerID, 1, 11, GeneralUtil.getTime(limittimeNow));

			if(trainingType == 0) {
				receiver.drawMenuFont(engine, playerID, 0, 13, "CLEAR PER.", EventReceiver.COLOR_PINK);
				receiver.drawMenuFont(engine, playerID, 3, 14, clearper + "%");
			}

			receiver.drawMenuFont(engine, playerID, 0, 16, "TOTAL TIME", EventReceiver.COLOR_PINK);
			receiver.drawMenuFont(engine, playerID, 1, 17, GeneralUtil.getTime(engine.statistics.time));
		}
	}

	/*
	 * Called at game over(主にコンティニュー画面)
	 */
	@Override
	public boolean onGameOver(GameEngine engine, int playerID) {
		// コンティニュー画面
		if((engine.ending == 0) && (!noContinue)) {
			if(engine.statc[0] == 0) {
				engine.playSE("died");
				owner.bgmStatus.bgm = BGMStatus.BGM_NOTHING;

				engine.timerActive = false;
				engine.blockShowOutlineOnly = false;

				engine.itemXRayEnable = false;
				engine.itemColorEnable = false;
				engine.interruptItemNumber = GameEngine.INTERRUPTITEM_NONE;

				engine.resetFieldVisible();

				engine.allowTextRenderByReceiver = false;	// GAMEOVER表示抑制
			}
			if(engine.statc[0] < engine.field.getHeight() + 1) {
				// field灰色化
				for(int i = 0; i < engine.field.getWidth(); i++) {
					if(engine.field.getBlockColor(i, engine.statc[0]) != Block.BLOCK_COLOR_NONE) {
						Block blk = engine.field.getBlock(i, engine.statc[0]);

						if(blk != null) {
							blk.color = Block.BLOCK_COLOR_GRAY;
							blk.darkness = 0f;
							blk.elapsedFrames = -1;
						}
					}
				}
				engine.statc[0]++;
			} else if(engine.statc[0] < (engine.field.getHeight() + 1) + 600) {
				// コンティニュー選択
				if(engine.ctrl.isPush(Controller.BUTTON_UP) || engine.ctrl.isPush(Controller.BUTTON_DOWN)) {
					engine.statc[1]++;
					if(engine.statc[1] > 1) engine.statc[1] = 0;
					engine.playSE("cursor");
				}
				// 決定
				if(engine.ctrl.isPush(Controller.BUTTON_A)) {
					if(engine.statc[1] == 0) {
						// YES
						limittimeNow = limittimeStart;
						engine.nextPieceCount = continueNextPieceCount;
						if(trainingType == 0) engine.statistics.time += 60 * 60 * 2;
						engine.allowTextRenderByReceiver = true;
						engine.stat = GameEngine.STAT_READY;
						engine.resetStatc();
						engine.playSE("decide");
					} else {
						// NO
						engine.statc[0] = (engine.field.getHeight() + 1) + 600;
					}
				} else {
					engine.statc[0]++;
				}
			} else if(engine.statc[0] >= (engine.field.getHeight() + 1) + 600) {
				// ＼(^o^)／ｵﾜﾀ
				noContinue = true;
				engine.allowTextRenderByReceiver = true;	// GAMEOVER表示抑制解除
				engine.resetStatc();
			}

			return true;
		}

		return false;
	}

	/*
	 * game over時の描画処理(主にコンティニュー画面)
	 */
	@Override
	public void renderGameOver(GameEngine engine, int playerID) {
		if((engine.ending == 0) && (!noContinue)) {
			if((engine.statc[0] >= engine.field.getHeight() + 1) && (engine.statc[0] < (engine.field.getHeight() + 1) + 600)) {
				receiver.drawMenuFont(engine, playerID, 1, 7, "CONTINUE?", EventReceiver.COLOR_PINK);

				receiver.drawMenuFont(engine, playerID, 3, 9 + engine.statc[1] * 2, "b", EventReceiver.COLOR_RED);
				receiver.drawMenuFont(engine, playerID, 4, 9, "YES", (engine.statc[1] == 0));
				receiver.drawMenuFont(engine, playerID, 4, 11, "NO", (engine.statc[1] == 1));

				int t = ((engine.field.getHeight() + 1) + 600) - engine.statc[0];
				receiver.drawMenuFont(engine, playerID, 2, 13, "TIME " + ((t-1) / 60), EventReceiver.COLOR_GREEN);

				receiver.drawMenuFont(engine, playerID, 0, 16, "TOTAL TIME", EventReceiver.COLOR_PINK);
				receiver.drawMenuFont(engine, playerID, 1, 17, GeneralUtil.getTime(engine.statistics.time));

				if(trainingType == 0) receiver.drawMenuFont(engine, playerID, 0, 18, "+2 MINUTES", EventReceiver.COLOR_RED);
			}
		}
	}

	/*
	 * 結果画面の処理
	 */
	@Override
	public boolean onResult(GameEngine engine, int playerID) {
		if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_UP)) {
			engine.statc[1]--;
			if(engine.statc[1] < 0) engine.statc[1] = 2;
			receiver.playSE("change");
		}
		if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_DOWN)) {
			engine.statc[1]++;
			if(engine.statc[1] > 2) engine.statc[1] = 0;
			receiver.playSE("change");
		}

		return false;
	}

	/*
	 * Render results screen
	 */
	@Override
	public void renderResult(GameEngine engine, int playerID) {
		receiver.drawMenuFont(engine, playerID, 0, 0, "kn PAGE" + (engine.statc[1] + 1) + "/3", EventReceiver.COLOR_RED);

		if(engine.statc[1] == 0) {
			int gcolor = EventReceiver.COLOR_WHITE;
			if(allclear == 1) gcolor = EventReceiver.COLOR_GREEN;
			if(allclear == 2) gcolor = EventReceiver.COLOR_ORANGE;

			receiver.drawMenuFont(engine, playerID,  0, 2, "STAGE", EventReceiver.COLOR_PINK);
			String strStage = String.format("%10s", getStageName(stage));
			receiver.drawMenuFont(engine, playerID,  0, 3, strStage, gcolor);

			drawResult(engine, playerID, receiver, 4, EventReceiver.COLOR_PINK,
					"CLEAR", String.format("%9d%%", clearper));
			drawResultStats(engine, playerID, receiver, 6, EventReceiver.COLOR_PINK,
					STAT_LINES, STAT_PIECE, STAT_TIME);
			drawResultRank(engine, playerID, receiver, 12, EventReceiver.COLOR_PINK, rankingRank);
		} else if(engine.statc[1] == 1) {
			receiver.drawMenuFont(engine, playerID, 0, 2, "SECTION1/2", EventReceiver.COLOR_PINK);

			for(int i = 0; i < 15; i++) {
				if(sectiontime[i] != 0) {
					String strSectionTime = GeneralUtil.getTime(sectiontime[i]);
					if(sectiontime[i] == -1) strSectionTime = "FAILED";
					if(sectiontime[i] == -2) strSectionTime = "SKIPPED";
					receiver.drawMenuFont(engine, playerID, 2, 3 + i, strSectionTime);
				}
			}
		} else if(engine.statc[1] == 2) {
			receiver.drawMenuFont(engine, playerID, 0, 2, "SECTION2/2", EventReceiver.COLOR_PINK);

			for(int i = 15; i < sectiontime.length; i++) {
				if(sectiontime[i] != 0) {
					String strSectionTime = GeneralUtil.getTime(sectiontime[i]);
					if(sectiontime[i] == -1) strSectionTime = "FAILED";
					if(sectiontime[i] == -2) strSectionTime = "SKIPPED";
					receiver.drawMenuFont(engine, playerID, 2, i - 12, strSectionTime);
				}
			}
		}
	}

	/*
	 * リプレイ保存
	 */
	@Override
	public void saveReplay(GameEngine engine, int playerID, CustomProperties prop) {
		saveSetting(prop);
		prop.setProperty("gemmania.version", version);
		prop.setProperty("gemmania.result.stage", stage);
		prop.setProperty("gemmania.result.clearper", clearper);
		prop.setProperty("gemmania.result.allclear", allclear);

		engine.statistics.level = stage;
		engine.statistics.levelDispAdd = 1;
		engine.statistics.score = clearper;
		engine.statistics.writeProperty(prop, playerID);

		// Update rankings
		if((owner.replayMode == false) && (startstage == 0) && (trainingType == 0) &&
		   (startnextc == 0) && (stageset < 0) && (always20g == false) && (engine.ai == null))
		{
			updateRanking(randomnext ? 1 : 0, stage, clearper, engine.statistics.time, allclear);

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
		for(int type = 0; type < RANKING_TYPE; type++)
			for(int i = 0; i < RANKING_MAX; i++) {
				rankingStage[type][i] = prop.getProperty("gemmania.ranking." + ruleName + "." + type + ".stage." + i, 0);
				rankingClearPer[type][i] = prop.getProperty("gemmania.ranking." + ruleName + "." + type + ".clearper." + i, 0);
				rankingTime[type][i] = prop.getProperty("gemmania.ranking." + ruleName + "." + type + ".time." + i, 0);
				rankingAllClear[type][i] = prop.getProperty("gemmania.ranking." + ruleName + "." + type + ".allclear." + i, 0);
			}
	}

	/**
	 * Save rankings to property file
	 * @param prop Property file
	 * @param ruleName Rule name
	 */
	private void saveRanking(CustomProperties prop, String ruleName) {
		for(int type = 0; type < RANKING_TYPE; type++)
			for(int i = 0; i < RANKING_MAX; i++) {
				prop.setProperty("gemmania.ranking." + ruleName + "." + type + ".stage." + i, rankingStage[type][i]);
				prop.setProperty("gemmania.ranking." + ruleName + "." + type + ".clearper." + i, rankingClearPer[type][i]);
				prop.setProperty("gemmania.ranking." + ruleName + "." + type + ".time." + i, rankingTime[type][i]);
				prop.setProperty("gemmania.ranking." + ruleName + "." + type + ".allclear." + i, rankingAllClear[type][i]);
			}
	}

	/**
	 * Update rankings
	 * @param type Game type
	 * @param stg  stage
	 * @param clper クリア率
	 * @param time Time
	 * @param clear 完全クリア flag
	 */
	private void updateRanking(int type, int stg, int clper, int time, int clear) {
		rankingRank = checkRanking(type, stg, clper, time, clear);

		if(rankingRank != -1) {
			// Shift down ranking entries
			for(int i = RANKING_MAX - 1; i > rankingRank; i--) {
				rankingStage[type][i] = rankingStage[type][i - 1];
				rankingClearPer[type][i] = rankingClearPer[type][i - 1];
				rankingTime[type][i] = rankingTime[type][i - 1];
				rankingAllClear[type][i] = rankingAllClear[type][i - 1];
			}

			// Add new data
			rankingStage[type][rankingRank] = stg;
			rankingClearPer[type][rankingRank] = clper;
			rankingTime[type][rankingRank] = time;
			rankingAllClear[type][rankingRank] = clear;
		}
	}

	/**
	 * Calculate ranking position
	 * @param type Game type
	 * @param stg  stage
	 * @param clper クリア率
	 * @param time Time
	 * @param clear 完全クリア flag
	 * @return Position (-1 if unranked)
	 */
	private int checkRanking(int type, int stg, int clper, int time, int clear) {
		for(int i = 0; i < RANKING_MAX; i++) {
			if(clear > rankingAllClear[type][i]) {
				return i;
			} else if((clear == rankingAllClear[type][i]) && (stg > rankingStage[type][i])) {
				return i;
			} else if((clear == rankingAllClear[type][i]) && (stg == rankingStage[type][i]) && (clper > rankingClearPer[type][i])) {
				return i;
			} else if((clear == rankingAllClear[type][i]) && (stg == rankingStage[type][i]) && (clper == rankingClearPer[type][i]) &&
			          (time < rankingTime[type][i]))
			{
				return i;
			}
		}
		return -1;
	}
}
