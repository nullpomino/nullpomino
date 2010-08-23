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
 * AVALANCHE VS-BATTLE mode (Release Candidate 1)
 */
public class AvalancheVSMode extends DummyMode {
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

	/** Fever map files list */
	private static final String[] FEVER_MAPS =
	{
		"Fever", "15th", "15thDS", "7", "Compendium"
	};

	/** Chain multipliers */
	private static final int[] CHAIN_POWERS = {
		4, 12, 24, 33, 50, 101, 169, 254, 341, 428, 538, 648, 763, 876, 990, 999 //Arle
	};
	
	/** Chain multipliers in Fever */
	private static final int[] FEVER_POWERS = {
		4, 10, 18, 21, 29, 46, 76, 113, 150, 223, 259, 266, 313, 364, 398, 432, 468, 504, 540, 576, 612, 648, 684, 720 //Arle
	};

	/** Number of players */
	private static final int MAX_PLAYERS = 2;

	/** Ojama counter setting constants */
	private static final int OJAMA_COUNTER_OFF = 0, OJAMA_COUNTER_ON = 1, OJAMA_COUNTER_FEVER = 2;

	/** Names of ojama counter settings */
	private static final String[] OJAMA_COUNTER_STRING = {"OFF", "ON", "FEVER"};

	/** Zenkeshi setting constants */
	private static final int /*ZENKESHI_MODE_OFF = 0,*/ ZENKESHI_MODE_ON = 1, ZENKESHI_MODE_FEVER = 2;

	/** Names of zenkeshi settings */
	private static final String[] ZENKESHI_TYPE_NAMES = {"OFF", "ON", "FEVER"};

	/** Names of outline settings */
	private static final String[] OUTLINE_TYPE_NAMES = {"NORMAL", "COLOR", "NONE"};

	/** Names of chain display settings */
	private static final String[] CHAIN_DISPLAY_NAMES = {"OFF", "YELLOW", "PLAYER", "SIZE"};

	/** Constants for chain display settings */
	private static final int CHAIN_DISPLAY_NONE = 0, /*CHAIN_DISPLAY_YELLOW = 1,*/
		CHAIN_DISPLAY_PLAYER = 2, CHAIN_DISPLAY_SIZE = 3;

	/** Names of fever point criteria settings */
	private static final String[] FEVER_POINT_CRITERIA_NAMES = {"COUNTER", "CLEAR", "BOTH"};

	/** Constants for fever point criteria settings */
	private static final int FEVER_POINT_CRITERIA_COUNTER = 0, FEVER_POINT_CRITERIA_CLEAR = 1
			/*,FEVER_POINT_CRITERIA_BOTH = 2*/;

	/** Names of fever time criteria settings */
	private static final String[] FEVER_TIME_CRITERIA_NAMES = {"COUNTER", "ATTACK"};

	/** Constants for fever time criteria settings */
	private static final int FEVER_TIME_CRITERIA_COUNTER = 0, FEVER_TIME_CRITERIA_ATTACK = 1;

	/** Each player's frame color */
	private static final int[] PLAYER_COLOR_FRAME = {GameEngine.FRAME_COLOR_RED, GameEngine.FRAME_COLOR_BLUE};

	/** Fever meter colors */
	private static final int[] FEVER_METER_COLORS =
	{
		EventReceiver.COLOR_RED,
		EventReceiver.COLOR_ORANGE,
		EventReceiver.COLOR_YELLOW,
		EventReceiver.COLOR_GREEN,
		EventReceiver.COLOR_CYAN,
		EventReceiver.COLOR_BLUE,
		EventReceiver.COLOR_DARKBLUE,
		EventReceiver.COLOR_PURPLE,
		EventReceiver.COLOR_PINK
	};

	/** GameManager that owns this mode */
	private GameManager owner;

	/** Drawing and event handling EventReceiver */
	private EventReceiver receiver;

	/** Rule settings for countering ojama not yet dropped */
	private int[] ojamaCounterMode;

	/** 溜まっている邪魔Blockのcount */
	private int[] ojama;

	/** 送った邪魔Blockのcount */
	private int[] ojamaSent;

	/** Time to display the most recent increase in score */
	private int[] scgettime;

	/** 使用するBGM */
	private int bgmno;

	/** Big */
	private boolean[] big;

	/** 効果音ON/OFF */
	private boolean[] enableSE;

	/** マップ使用 flag */
	private boolean[] useMap;

	/** 使用するマップセット number */
	private int[] mapSet;

	/** マップ number(-1でランダム) */
	private int[] mapNumber;

	/** Last preset number used */
	private int[] presetNumber;

	/** 勝者 */
	private int winnerID;

	/** マップセットのProperty file */
	private CustomProperties[] propMap;

	/** 最大マップ number */
	private int[] mapMaxNo;

	/** バックアップ用フィールド（マップをリプレイに保存するときに使用） */
	private Field[] fldBackup;

	/** マップ選択用乱count */
	private Random randMap;

	/** Version */
	private int version;

	/** Flag for all clear */
	private boolean[] zenKeshi;

	/** Amount of points earned from most recent clear */
	private int[] lastscore, lastmultiplier;

	/** Amount of ojama added in current chain */
	private int[] ojamaAdd;

	/** Score */
	private int[] score;

	/** Max amount of ojama dropped at once */
	private int[] maxAttack;

	/** Number of colors to use */
	private int[] numColors;

	/** Minimum chain count needed to send ojama */
	private int[] rensaShibari;

	/** Denominator for score-to-ojama conversion */
	private int[] ojamaRate;

	/** Settings for hard ojama blocks */
	private int[] ojamaHard;

	/** Hurryup開始までの秒count(0でHurryupなし) */
	private int[] hurryupSeconds;

	/** Fever points needed to enter Fever Mode */
	private int[] feverThreshold;

	/** Fever points */
	private int[] feverPoints;

	/** Fever time */
	private int[] feverTime;

	/** Minimum and maximum fever time */
	private int[] feverTimeMin, feverTimeMax;

	/** Flag set to true when player is in Fever Mode */
	private boolean[] inFever;

	/** Backup fields for Fever Mode */
	private Field[] feverBackupField;

	/** Second ojama counter for Fever Mode */
	private int[] ojamaFever;

	/** Set to true when opponent starts chain while in Fever Mode */
	private boolean[] ojamaAddToFever;

	/** Set to true when last drop resulted in a clear */
	private boolean[] cleared;

	/** Set to true when dropping ojama blocks */
	private boolean[] ojamaDrop;

	/** Selected fever map set file */
	private int[] feverMapSet;

	/** Selected fever map set file's subset list */
	private String[][] feverMapSubsets;

	/** Time to display "ZENKESHI!" */
	private int[] zenKeshiDisplay;

	/** Zenkeshi reward type */
	private int[] zenKeshiType;

	/** Fever map CustomProperties */
	private CustomProperties[] propFeverMap;

	/** Chain levels for Fever Mode */
	private int[] feverChain;

	/** Chain level boundaries for Fever Mode */
	private int[] feverChainMin, feverChainMax;

	/** Criteria to add a fever point */
	private int[] feverPointCriteria;

	/** Criteria to add 1 second of fever time */
	private int[] feverTimeCriteria;

	/** Fever power multiplier */
	private int[] feverPower;

	/** Selected outline type */
	private int[] outlineType;

	/** If true, both columns 3 and 4 are danger columns */
	private boolean[] dangerColumnDouble;

	/** If true, red X's appear at tops of danger columns */
	private boolean[] dangerColumnShowX;

	/** Last chain hit number */
	private int[] chain;

	/** Time to display last chain */
	private int[] chainDisplay;

	/** Type of chain display */
	private int[] chainDisplayType;

	/** True to show fever points as meter, false to show numerical counts */
	private boolean[] feverShowMeter;

	/** True to show ojama on meter, false to show fever points */
	private boolean[] ojamaMeter;
	
	/** True to use new (Fever) chain powers */
	private boolean[] newChainPower;
	
	/** True to use slower falling animations, false to use faster */
	private boolean[] cascadeSlow;

	/*
	 * Mode  name
	 */
	@Override
	public String getName() {
		return "AVALANCHE VS-BATTLE (RC1)";
	}

	/*
	 * Number of players
	 */
	@Override
	public int getPlayers() {
		return MAX_PLAYERS;
	}

	/*
	 * Mode  initialization
	 */
	@Override
	public void modeInit(GameManager manager) {
		owner = manager;
		receiver = owner.receiver;

		ojamaCounterMode = new int[MAX_PLAYERS];
		ojama = new int[MAX_PLAYERS];
		ojamaSent = new int[MAX_PLAYERS];

		scgettime = new int[MAX_PLAYERS];
		bgmno = 0;
		big = new boolean[MAX_PLAYERS];
		enableSE = new boolean[MAX_PLAYERS];
		hurryupSeconds = new int[MAX_PLAYERS];
		useMap = new boolean[MAX_PLAYERS];
		mapSet = new int[MAX_PLAYERS];
		mapNumber = new int[MAX_PLAYERS];
		presetNumber = new int[MAX_PLAYERS];
		propMap = new CustomProperties[MAX_PLAYERS];
		mapMaxNo = new int[MAX_PLAYERS];
		fldBackup = new Field[MAX_PLAYERS];
		randMap = new Random();

		zenKeshi = new boolean[MAX_PLAYERS];
		lastscore = new int[MAX_PLAYERS];
		lastmultiplier = new int[MAX_PLAYERS];
		ojamaAdd = new int[MAX_PLAYERS];
		score = new int[MAX_PLAYERS];
		numColors = new int[MAX_PLAYERS];
		maxAttack = new int[MAX_PLAYERS];
		rensaShibari = new int[MAX_PLAYERS];
		ojamaRate = new int[MAX_PLAYERS];
		ojamaHard = new int[MAX_PLAYERS];

		feverThreshold = new int[MAX_PLAYERS];
		feverPoints = new int[MAX_PLAYERS];
		feverTime = new int[MAX_PLAYERS];
		feverTimeMin = new int[MAX_PLAYERS];
		feverTimeMax = new int[MAX_PLAYERS];
		inFever = new boolean[MAX_PLAYERS];
		feverBackupField = new Field[MAX_PLAYERS];
		ojamaFever = new int[MAX_PLAYERS];
		ojamaAddToFever = new boolean[MAX_PLAYERS];
		cleared = new boolean[MAX_PLAYERS];
		ojamaDrop = new boolean[MAX_PLAYERS];
		feverMapSet = new int[MAX_PLAYERS];
		zenKeshiDisplay = new int[MAX_PLAYERS];
		zenKeshiType = new int[MAX_PLAYERS];
		propFeverMap = new CustomProperties[MAX_PLAYERS];
		feverChain = new int[MAX_PLAYERS];
		feverChainMin = new int[MAX_PLAYERS];
		feverChainMax = new int[MAX_PLAYERS];
		feverMapSubsets = new String[MAX_PLAYERS][];
		outlineType = new int[MAX_PLAYERS];
		dangerColumnDouble = new boolean[MAX_PLAYERS];
		dangerColumnShowX = new boolean[MAX_PLAYERS];
		chain = new int[MAX_PLAYERS];
		chainDisplay = new int[MAX_PLAYERS];
		chainDisplayType = new int[MAX_PLAYERS];
		feverShowMeter = new boolean[MAX_PLAYERS];
		ojamaMeter = new boolean[MAX_PLAYERS];
		feverPointCriteria = new int[MAX_PLAYERS];
		feverTimeCriteria = new int[MAX_PLAYERS];
		feverPower = new int[MAX_PLAYERS];
		newChainPower = new boolean[MAX_PLAYERS];
		cascadeSlow = new boolean[MAX_PLAYERS];

		winnerID = -1;
	}

	/**
	 * Read speed presets
	 * @param engine GameEngine
	 * @param prop Property file to read from
	 * @param preset Preset number
	 */
	private void loadPreset(GameEngine engine, CustomProperties prop, int preset) {
		engine.speed.gravity = prop.getProperty("avalanchevs.gravity." + preset, 4);
		engine.speed.denominator = prop.getProperty("avalanchevs.denominator." + preset, 256);
		engine.speed.are = prop.getProperty("avalanchevs.are." + preset, 30);
		engine.speed.areLine = prop.getProperty("avalanchevs.areLine." + preset, 30);
		engine.speed.lineDelay = prop.getProperty("avalanchevs.lineDelay." + preset, 10);
		engine.speed.lockDelay = prop.getProperty("avalanchevs.lockDelay." + preset, 60);
		engine.speed.das = prop.getProperty("avalanchevs.das." + preset, 14);
		engine.cascadeDelay = prop.getProperty("avalanchevs.fallDelay." + preset, 1);
		engine.cascadeClearDelay = prop.getProperty("avalanchevs.clearDelay." + preset, 10);
	}

	/**
	 * Save speed presets
	 * @param engine GameEngine
	 * @param prop Property file to save to
	 * @param preset Preset number
	 */
	private void savePreset(GameEngine engine, CustomProperties prop, int preset) {
		prop.setProperty("avalanchevs.gravity." + preset, engine.speed.gravity);
		prop.setProperty("avalanchevs.denominator." + preset, engine.speed.denominator);
		prop.setProperty("avalanchevs.are." + preset, engine.speed.are);
		prop.setProperty("avalanchevs.areLine." + preset, engine.speed.areLine);
		prop.setProperty("avalanchevs.lineDelay." + preset, engine.speed.lineDelay);
		prop.setProperty("avalanchevs.lockDelay." + preset, engine.speed.lockDelay);
		prop.setProperty("avalanchevs.das." + preset, engine.speed.das);
		prop.setProperty("avalanchevs.fallDelay." + preset, engine.cascadeDelay);
		prop.setProperty("avalanchevs.clearDelay." + preset, engine.cascadeClearDelay);
	}

	/**
	 * スピード以外の設定を読み込み
	 * @param engine GameEngine
	 * @param prop Property file to read from
	 */
	private void loadOtherSetting(GameEngine engine, CustomProperties prop) {
		int playerID = engine.playerID;
		bgmno = prop.getProperty("avalanchevs.bgmno", 0);
		ojamaCounterMode[playerID] = prop.getProperty("avalanchevs.ojamaCounterMode", OJAMA_COUNTER_ON);
		big[playerID] = prop.getProperty("avalanchevs.big.p" + playerID, false);
		enableSE[playerID] = prop.getProperty("avalanchevs.enableSE.p" + playerID, true);
		hurryupSeconds[playerID] = prop.getProperty("avalanchevs.hurryupSeconds.p" + playerID, 192);
		useMap[playerID] = prop.getProperty("avalanchevs.useMap.p" + playerID, false);
		mapSet[playerID] = prop.getProperty("avalanchevs.mapSet.p" + playerID, 0);
		mapNumber[playerID] = prop.getProperty("avalanchevs.mapNumber.p" + playerID, -1);
		presetNumber[playerID] = prop.getProperty("avalanchevs.presetNumber.p" + playerID, 0);
		maxAttack[playerID] = prop.getProperty("avalanchevs.maxAttack.p" + playerID, 30);
		numColors[playerID] = prop.getProperty("avalanchevs.numColors.p" + playerID, 5);
		rensaShibari[playerID] = prop.getProperty("avalanchevs.rensaShibari.p" + playerID, 1);
		ojamaRate[playerID] = prop.getProperty("avalanchevs.ojamaRate.p" + playerID, 120);
		ojamaHard[playerID] = prop.getProperty("avalanchevs.ojamaHard.p" + playerID, 0);
		feverThreshold[playerID] = prop.getProperty("avalanchevs.feverThreshold.p" + playerID, 0);
		feverTimeMin[playerID] = prop.getProperty("avalanchevs.feverTimeMin.p" + playerID, 15);
		feverTimeMax[playerID] = prop.getProperty("avalanchevs.feverTimeMax.p" + playerID, 30);
		feverMapSet[playerID] = prop.getProperty("avalanchevs.feverMapSet.p" + playerID, 0);
		zenKeshiType[playerID] = prop.getProperty("avalanchevs.zenKeshiType.p" + playerID, 1);
		outlineType[playerID] = prop.getProperty("avalanchevs.outlineType.p" + playerID, 1);
		dangerColumnDouble[playerID] = prop.getProperty("avalanchevs.dangerColumnDouble.p" + playerID, false);
		dangerColumnShowX[playerID] = prop.getProperty("avalanchevs.dangerColumnShowX.p" + playerID, false);
		chainDisplayType[playerID] = prop.getProperty("avalanchevs.chainDisplayType.p" + playerID, 1);
		feverShowMeter[playerID] = prop.getProperty("avalanchevs.feverShowMeter.p" + playerID, true);
		ojamaMeter[playerID] = prop.getProperty("avalanchevs.ojamaMeter.p" + playerID, true);
		feverPointCriteria[playerID] = prop.getProperty("avalanchevs.feverPointCriteria.p" + playerID, 0);
		feverTimeCriteria[playerID] = prop.getProperty("avalanchevs.feverTimeCriteria.p" + playerID, 0);
		feverPower[playerID] = prop.getProperty("avalanchevs.feverPower.p" + playerID, 10);
		newChainPower[playerID] = prop.getProperty("avalanchevs.newChainPower.p" + playerID, false);
		cascadeSlow[playerID] = prop.getProperty("avalanchevs.cascadeSlow.p" + playerID, false);
	}

	/**
	 * スピード以外の設定を保存
	 * @param engine GameEngine
	 * @param prop Property file to save to
	 */
	private void saveOtherSetting(GameEngine engine, CustomProperties prop) {
		int playerID = engine.playerID;
		prop.setProperty("avalanchevs.bgmno", bgmno);
		prop.setProperty("avalanchevs.ojamaCounterMode", ojamaCounterMode[playerID]);
		prop.setProperty("avalanchevs.big.p" + playerID, big[playerID]);
		prop.setProperty("avalanchevs.enableSE.p" + playerID, enableSE[playerID]);
		prop.setProperty("avalanchevs.hurryupSeconds.p" + playerID, hurryupSeconds[playerID]);
		prop.setProperty("avalanchevs.useMap.p" + playerID, useMap[playerID]);
		prop.setProperty("avalanchevs.mapSet.p" + playerID, mapSet[playerID]);
		prop.setProperty("avalanchevs.mapNumber.p" + playerID, mapNumber[playerID]);
		prop.setProperty("avalanchevs.presetNumber.p" + playerID, presetNumber[playerID]);
		prop.setProperty("avalanchevs.maxAttack.p" + playerID, maxAttack[playerID]);
		prop.setProperty("avalanchevs.numColors.p" + playerID, numColors[playerID]);
		prop.setProperty("avalanchevs.rensaShibari.p" + playerID, rensaShibari[playerID]);
		prop.setProperty("avalanchevs.ojamaRate.p" + playerID, ojamaRate[playerID]);
		prop.setProperty("avalanchevs.ojamaHard.p" + playerID, ojamaHard[playerID]);
		prop.setProperty("avalanchevs.feverThreshold.p" + playerID, feverThreshold[playerID]);
		prop.setProperty("avalanchevs.feverTimeMin.p" + playerID, feverTimeMin[playerID]);
		prop.setProperty("avalanchevs.feverTimeMax.p" + playerID, feverTimeMax[playerID]);
		prop.setProperty("avalanchevs.feverMapSet.p" + playerID, feverMapSet[playerID]);
		prop.setProperty("avalanchevs.zenKeshiType.p" + playerID, zenKeshiType[playerID]);
		prop.setProperty("avalanchevs.outlineType.p" + playerID, outlineType[playerID]);
		prop.setProperty("avalanchevs.dangerColumnDouble.p" + playerID, dangerColumnDouble[playerID]);
		prop.setProperty("avalanchevs.dangerColumnShowX.p" + playerID, dangerColumnShowX[playerID]);
		prop.setProperty("avalanchevs.chainDisplayType.p" + playerID, chainDisplayType[playerID]);
		prop.setProperty("avalanchevs.feverShowMeter.p" + playerID, feverShowMeter[playerID]);
		prop.setProperty("avalanchevs.ojamaMeter.p" + playerID, ojamaMeter[playerID]);
		prop.setProperty("avalanchevs.feverPointCriteria.p" + playerID, feverPointCriteria[playerID]);
		prop.setProperty("avalanchevs.feverTimeCriteria.p" + playerID, feverTimeCriteria[playerID]);
		prop.setProperty("avalanchevs.feverPower.p" + playerID, feverPower[playerID]);
		prop.setProperty("avalanchevs.newChainPower.p" + playerID, newChainPower[playerID]);
		prop.setProperty("avalanchevs.cascadeSlow.p" + playerID, cascadeSlow[playerID]);
	}

	/**
	 * マップ読み込み
	 * @param field フィールド
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
	 * マップ保存
	 * @param field フィールド
	 * @param prop Property file to save to
	 * @param id 任意のID
	 */
	private void saveMap(Field field, CustomProperties prop, int id) {
		//field.writeProperty(prop, id);
		prop.setProperty("map." + id, field.fieldToString());
	}

	/**
	 * プレビュー用にマップを読み込み
	 * @param engine GameEngine
	 * @param playerID プレイヤー number
	 * @param id マップID
	 * @param forceReload trueにするとマップファイルを強制再読み込み
	 */
	private void loadMapPreview(GameEngine engine, int playerID, int id, boolean forceReload) {
		if((propMap[playerID] == null) || (forceReload)) {
			mapMaxNo[playerID] = 0;
			propMap[playerID] = receiver.loadProperties("config/map/avalanche/" + mapSet[playerID] + ".map");
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
		engine.clearMode = GameEngine.CLEAR_COLOR;
		engine.garbageColorClear = true;
		engine.lineGravityType = GameEngine.LINE_GRAVITY_CASCADE;
		for(int i = 0; i < Piece.PIECE_COUNT; i++)
			engine.nextPieceEnable[i] = (PIECE_ENABLE[i] == 1);
		engine.blockColors = BLOCK_COLORS;
		engine.randomBlockColor = true;
		engine.connectBlocks = false;

		ojama[playerID] = 0;
		ojamaAdd[playerID] = 0;
		ojamaFever[playerID] = 0;
		ojamaSent[playerID] = 0;
		score[playerID] = 0;
		zenKeshi[playerID] = false;
		scgettime[playerID] = 0;
		feverPoints[playerID] = 0;
		feverTime[playerID] = feverTimeMin[playerID] * 60;
		inFever[playerID] = false;
		feverBackupField[playerID] = null;
		cleared[playerID] = false;
		ojamaDrop[playerID] = false;
		zenKeshiDisplay[playerID] = 0;
		chain[playerID] = 0;
		chainDisplay[playerID] = 0;

		if(engine.owner.replayMode == false) {
			loadOtherSetting(engine, engine.owner.modeConfig);
			loadPreset(engine, engine.owner.modeConfig, -1 - playerID);
			version = CURRENT_VERSION;
		} else {
			loadOtherSetting(engine, engine.owner.replayProp);
			loadPreset(engine, engine.owner.replayProp, -1 - playerID);
			version = owner.replayProp.getProperty("avalanchevs.version", 0);
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
			int change = updateCursor(engine, 39);

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
					if (m >= 10) engine.speed.lockDelay += change*10;
					else engine.speed.lockDelay += change;
					if(engine.speed.lockDelay < 0) engine.speed.lockDelay = 999;
					if(engine.speed.lockDelay > 999) engine.speed.lockDelay = 0;
					break;
				case 6:
					engine.speed.das += change;
					if(engine.speed.das < 0) engine.speed.das = 99;
					if(engine.speed.das > 99) engine.speed.das = 0;
					break;
				case 7:
					engine.cascadeDelay += change;
					if(engine.cascadeDelay < 0) engine.cascadeDelay = 20;
					if(engine.cascadeDelay > 20) engine.cascadeDelay = 0;
					break;
				case 8:
					engine.cascadeClearDelay += change;
					if(engine.cascadeClearDelay < 0) engine.cascadeClearDelay = 99;
					if(engine.cascadeClearDelay > 99) engine.cascadeClearDelay = 0;
					break;
				case 9:
					ojamaCounterMode[playerID] += change;
					if(ojamaCounterMode[playerID] < 0) ojamaCounterMode[playerID] = 2;
					if(ojamaCounterMode[playerID] > 2) ojamaCounterMode[playerID] = 0;
					break;
				case 10:
					if (m >= 10) maxAttack[playerID] += change*10;
					else maxAttack[playerID] += change;
					if(maxAttack[playerID] < 0) maxAttack[playerID] = 99;
					if(maxAttack[playerID] > 99) maxAttack[playerID] = 0;
					break;
				case 11:
					numColors[playerID] += change;
					if(numColors[playerID] < 3) numColors[playerID] = 5;
					if(numColors[playerID] > 5) numColors[playerID] = 3;
					break;
				case 12:
					rensaShibari[playerID] += change;
					if(rensaShibari[playerID] < 1) rensaShibari[playerID] = 20;
					if(rensaShibari[playerID] > 20) rensaShibari[playerID] = 1;
					break;
				case 13:
					if (m >= 10) ojamaRate[playerID] += change*100;
					else ojamaRate[playerID] += change*10;
					if(ojamaRate[playerID] < 10) ojamaRate[playerID] = 1000;
					if(ojamaRate[playerID] > 1000) ojamaRate[playerID] = 10;
					break;
				case 14:
					if (m > 10) hurryupSeconds[playerID] += change*m/10;
					else hurryupSeconds[playerID] += change;
					if(hurryupSeconds[playerID] < 0) hurryupSeconds[playerID] = 300;
					if(hurryupSeconds[playerID] > 300) hurryupSeconds[playerID] = 0;
					break;
				case 15:
					ojamaHard[playerID] += change;
					if(ojamaHard[playerID] < 0) ojamaHard[playerID] = 9;
					if(ojamaHard[playerID] > 9) ojamaHard[playerID] = 0;
					break;
				case 16:
					dangerColumnDouble[playerID] = !dangerColumnDouble[playerID];
					break;
				case 17:
					dangerColumnShowX[playerID] = !dangerColumnShowX[playerID];
					break;
				case 18:
					feverThreshold[playerID] += change;
					if(feverThreshold[playerID] < 0) feverThreshold[playerID] = 9;
					if(feverThreshold[playerID] > 9) feverThreshold[playerID] = 0;
					break;
				case 19:
					if (m >= 10) feverTimeMin[playerID] += change*10;
					else feverTimeMin[playerID] += change;
					if(feverTimeMin[playerID] < 1) feverTimeMin[playerID] = feverTimeMax[playerID];
					if(feverTimeMin[playerID] > feverTimeMax[playerID]) feverTimeMin[playerID] = 1;
					break;
				case 20:
					if (m >= 10) feverTimeMax[playerID] += change*10;
					else feverTimeMax[playerID] += change;
					if(feverTimeMax[playerID] < feverTimeMin[playerID]) feverTimeMax[playerID] = 99;
					if(feverTimeMax[playerID] > 99) feverTimeMax[playerID] = feverTimeMin[playerID];
					break;
				case 21:
					feverMapSet[playerID] += change;
					if(feverMapSet[playerID] < 0) feverMapSet[playerID] = FEVER_MAPS.length-1;
					if(feverMapSet[playerID] >= FEVER_MAPS.length) feverMapSet[playerID] = 0;
					break;
				case 22:
					feverShowMeter[playerID] = !feverShowMeter[playerID];
					break;
				case 23:
					feverPointCriteria[playerID] += change;
					if(feverPointCriteria[playerID] < 0) feverPointCriteria[playerID] = 2;
					if(feverPointCriteria[playerID] > 2) feverPointCriteria[playerID] = 0;
					break;
				case 24:
					feverTimeCriteria[playerID] += change;
					if(feverTimeCriteria[playerID] < 0) feverTimeCriteria[playerID] = 1;
					if(feverTimeCriteria[playerID] > 1) feverTimeCriteria[playerID] = 0;
					break;
				case 25:
					feverPower[playerID] += change;
					if(feverPower[playerID] < 0) feverPower[playerID] = 20;
					if(feverPower[playerID] > 20) feverPower[playerID] = 0;
					break;
				case 26:
					zenKeshiType[playerID] += change;
					if(zenKeshiType[playerID] < 0) zenKeshiType[playerID] = 2;
					if(zenKeshiType[playerID] > 2) zenKeshiType[playerID] = 0;
					break;
				case 27:
					if (feverThreshold[playerID] > 0)
						ojamaMeter[playerID] = !ojamaMeter[playerID];
					else
						ojamaMeter[playerID] = true;
					break;
				case 28:
					outlineType[playerID] += change;
					if(outlineType[playerID] < 0) outlineType[playerID] = 2;
					if(outlineType[playerID] > 2) outlineType[playerID] = 0;
					break;
				case 29:
					chainDisplayType[playerID] += change;
					if(chainDisplayType[playerID] < 0) chainDisplayType[playerID] = 3;
					if(chainDisplayType[playerID] > 3) chainDisplayType[playerID] = 0;
					break;
				case 30:
					cascadeSlow[playerID] = !cascadeSlow[playerID];
					break;
				case 31:
					newChainPower[playerID] = !newChainPower[playerID];
					break;
				case 32:
					useMap[playerID] = !useMap[playerID];
					if(!useMap[playerID]) {
						if(engine.field != null) engine.field.reset();
					} else {
						loadMapPreview(engine, playerID, (mapNumber[playerID] < 0) ? 0 : mapNumber[playerID], true);
					}
					break;
				case 33:
					mapSet[playerID] += change;
					if(mapSet[playerID] < 0) mapSet[playerID] = 99;
					if(mapSet[playerID] > 99) mapSet[playerID] = 0;
					if(useMap[playerID]) {
						mapNumber[playerID] = -1;
						loadMapPreview(engine, playerID, (mapNumber[playerID] < 0) ? 0 : mapNumber[playerID], true);
					}
					break;
				case 34:
					if(useMap[playerID]) {
						mapNumber[playerID] += change;
						if(mapNumber[playerID] < -1) mapNumber[playerID] = mapMaxNo[playerID] - 1;
						if(mapNumber[playerID] > mapMaxNo[playerID] - 1) mapNumber[playerID] = -1;
						loadMapPreview(engine, playerID, (mapNumber[playerID] < 0) ? 0 : mapNumber[playerID], true);
					} else {
						mapNumber[playerID] = -1;
					}
					break;
				case 35:
					//big[playerID] = !big[playerID];
					big[playerID] = false;
					break;
				case 36:
					bgmno += change;
					if(bgmno < 0) bgmno = BGMStatus.BGM_COUNT - 1;
					if(bgmno > BGMStatus.BGM_COUNT - 1) bgmno = 0;
					break;
				case 37:
					enableSE[playerID] = !enableSE[playerID];
					break;
				case 38:
				case 39:
					presetNumber[playerID] += change;
					if(presetNumber[playerID] < 0) presetNumber[playerID] = 99;
					if(presetNumber[playerID] > 99) presetNumber[playerID] = 0;
					break;
				}
			}

			// 決定
			if(engine.ctrl.isPush(Controller.BUTTON_A) && (engine.statc[3] >= 5)) {
				engine.playSE("decide");

				if(engine.statc[2] == 36) {
					loadPreset(engine, owner.modeConfig, presetNumber[playerID]);
				} else if(engine.statc[2] == 37) {
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

			// プレビュー用マップ読み込み
			if(useMap[playerID] && (engine.statc[3] == 0)) {
				loadMapPreview(engine, playerID, (mapNumber[playerID] < 0) ? 0 : mapNumber[playerID], true);
			}

			// ランダムマッププレビュー
			if(useMap[playerID] && (propMap[playerID] != null) && (mapNumber[playerID] < 0)) {
				if(engine.statc[3] % 30 == 0) {
					engine.statc[5]++;
					if(engine.statc[5] >= mapMaxNo[playerID]) engine.statc[5] = 0;
					loadMapPreview(engine, playerID, engine.statc[5], false);
				}
			}

			engine.statc[3]++;
		} else if(engine.statc[4] == 0) {
			engine.statc[3]++;
			engine.statc[2] = 0;

			if(engine.statc[3] >= 240)
				engine.statc[4] = 1;
			else if(engine.statc[3] >= 180)
				engine.statc[2] = 27;
			else if(engine.statc[3] >= 120)
				engine.statc[2] = 18;
			else if(engine.statc[3] >= 60)
				engine.statc[2] = 9;
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

	private void loadMapSetFever(GameEngine engine, int playerID, int id, boolean forceReload) {
		if((propFeverMap[playerID] == null) || (forceReload)) {
			propFeverMap[playerID] = receiver.loadProperties("config/map/avalanche/" +
					FEVER_MAPS[id] + ".map");
			feverChainMin[playerID] = propFeverMap[playerID].getProperty("minChain", 3);
			feverChainMax[playerID] = propFeverMap[playerID].getProperty("maxChain", 15);
			String subsets = propFeverMap[playerID].getProperty("sets");
			feverMapSubsets[playerID] = subsets.split(",");
		}
	}

	/*
	 * 設定画面の描画
	 */
	@Override
	public void renderSetting(GameEngine engine, int playerID) {
		if(engine.statc[4] == 0) {
			if(engine.statc[2] < 9) {
				drawMenu(engine, playerID, receiver, 0, EventReceiver.COLOR_ORANGE, 0,
						"GRAVITY", String.valueOf(engine.speed.gravity),
						"G-MAX", String.valueOf(engine.speed.denominator),
						"ARE", String.valueOf(engine.speed.are),
						"ARE LINE", String.valueOf(engine.speed.areLine),
						"LINE DELAY", String.valueOf(engine.speed.lineDelay),
						"LOCK DELAY", String.valueOf(engine.speed.lockDelay),
						"DAS", String.valueOf(engine.speed.das),
						"FALL DELAY", String.valueOf(engine.cascadeDelay),
						"CLEAR DELAY", String.valueOf(engine.cascadeClearDelay));

				receiver.drawMenuFont(engine, playerID, 0, 19, "PAGE 1/5", EventReceiver.COLOR_YELLOW);
			} else if(engine.statc[2] < 18) {
				drawMenu(engine, playerID, receiver, 0, EventReceiver.COLOR_CYAN, 9,
						"COUNTER", OJAMA_COUNTER_STRING[ojamaCounterMode[playerID]],
						"MAX ATTACK", String.valueOf(maxAttack[playerID]),
						"COLORS", String.valueOf(numColors[playerID]),
						"MIN CHAIN", String.valueOf(rensaShibari[playerID]),
						"OJAMA RATE", String.valueOf(ojamaRate[playerID]),
						"HURRYUP", (hurryupSeconds[playerID] == 0) ? "NONE" : hurryupSeconds[playerID]+"SEC",
						"HARD OJAMA", String.valueOf(ojamaHard[playerID]),
						"X COLUMN", dangerColumnDouble[playerID] ? "3 AND 4" : "3 ONLY",
						"X SHOW", GeneralUtil.getONorOFF(dangerColumnShowX[playerID]));
				
				receiver.drawMenuFont(engine, playerID, 0, 19, "PAGE 2/5", EventReceiver.COLOR_YELLOW);
			} else if(engine.statc[2] < 27) {
				drawMenu(engine, playerID, receiver, 0, EventReceiver.COLOR_PURPLE, 18,
						"FEVER", (feverThreshold[playerID] == 0) ? "NONE" : feverThreshold[playerID]+" PTS",
						"F-MIN TIME", feverTimeMin[playerID] + "SEC",
						"F-MAX TIME", feverTimeMax[playerID] + "SEC",
						"F-MAP SET", FEVER_MAPS[feverMapSet[playerID]].toUpperCase(),
						"F-DISPLAY", feverShowMeter[playerID] ? "METER" : "COUNT",
						"F-ADDPOINT", FEVER_POINT_CRITERIA_NAMES[feverPointCriteria[playerID]],
						"F-ADDTIME", FEVER_TIME_CRITERIA_NAMES[feverTimeCriteria[playerID]],
						"F-POWER", (feverPower[playerID] * 10) + "%");
				drawMenu(engine, playerID, receiver, 16, EventReceiver.COLOR_CYAN, 26,
						"ZENKESHI", ZENKESHI_TYPE_NAMES[zenKeshiType[playerID]]);

				receiver.drawMenuFont(engine, playerID, 0, 19, "PAGE 3/5", EventReceiver.COLOR_YELLOW);
			} else if(engine.statc[2] < 32) {
				drawMenu(engine, playerID, receiver, 0, EventReceiver.COLOR_DARKBLUE, 27,
						"SIDE METER", (ojamaMeter[playerID] || feverThreshold[playerID] == 0) ? "OJAMA" : "FEVER",
						"OUTLINE", OUTLINE_TYPE_NAMES[outlineType[playerID]],
						"SHOW CHAIN", CHAIN_DISPLAY_NAMES[chainDisplayType[playerID]],
						"FALL ANIM", cascadeSlow[playerID] ? "FEVER" : "CLASSIC");
				drawMenu(engine, playerID, receiver, 8, EventReceiver.COLOR_CYAN, 31,
						"CHAINPOWER", newChainPower[playerID] ? "FEVER" : "CLASSIC");
				receiver.drawMenuFont(engine, playerID, 0, 19, "PAGE 4/5", EventReceiver.COLOR_YELLOW);
			} else {
				drawMenu(engine, playerID, receiver, 0, EventReceiver.COLOR_PINK, 32,
						"USE MAP", GeneralUtil.getONorOFF(useMap[playerID]),
						"MAP SET", String.valueOf(mapSet[playerID]),
						"MAP NO.", (mapNumber[playerID] < 0) ? "RANDOM" : mapNumber[playerID]+"/"+(mapMaxNo[playerID]-1),
						"BIG", GeneralUtil.getONorOFF(big[playerID]));
				drawMenu(engine, playerID, receiver, 8, EventReceiver.COLOR_DARKBLUE, 36,
						"BGM", String.valueOf(bgmno),
						"SE", GeneralUtil.getONorOFF(enableSE[playerID]));
				drawMenu(engine, playerID, receiver, 12, EventReceiver.COLOR_GREEN, 38,
						"LOAD", String.valueOf(presetNumber[playerID]),
						"SAVE", String.valueOf(presetNumber[playerID]));
				
				receiver.drawMenuFont(engine, playerID, 0, 19, "PAGE 5/5", EventReceiver.COLOR_YELLOW);
			}
		} else {
			receiver.drawMenuFont(engine, playerID, 3, 10, "WAIT", EventReceiver.COLOR_YELLOW);
		}
	}

	/*
	 * Readyの時のInitialization処理（Initialization前）
	 */
	@Override
	public boolean onReady(GameEngine engine, int playerID) {
		if(engine.statc[0] == 0) {
			engine.numColors = numColors[playerID];
			engine.lineGravityType = cascadeSlow[playerID] ? GameEngine.LINE_GRAVITY_CASCADE_SLOW : GameEngine.LINE_GRAVITY_CASCADE;

			if(outlineType[playerID] == 0) engine.blockOutlineType = GameEngine.BLOCK_OUTLINE_NORMAL;
			if(outlineType[playerID] == 1) engine.blockOutlineType = GameEngine.BLOCK_OUTLINE_SAMECOLOR;
			if(outlineType[playerID] == 2) engine.blockOutlineType = GameEngine.BLOCK_OUTLINE_NONE;

			if(feverThreshold[playerID] == 0) ojamaMeter[playerID] = true;

			feverTime[playerID] = feverTimeMin[playerID] * 60;
			feverChain[playerID] = 5;
			// マップ読み込み・リプレイ保存用にバックアップ
			if(useMap[playerID]) {
				if(owner.replayMode) {
					engine.createFieldIfNeeded();
					loadMap(engine.field, owner.replayProp, playerID);
					engine.field.setAllSkin(engine.getSkin());
				} else {
					if(propMap[playerID] == null) {
						propMap[playerID] = receiver.loadProperties("config/map/avalanche/" + mapSet[playerID] + ".map");
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
			loadMapSetFever(engine, playerID, feverMapSet[playerID], true);
		}

		return false;
	}

	/*
	 * ゲーム開始時の処理
	 */
	@Override
	public void startGame(GameEngine engine, int playerID) {
		engine.b2bEnable = false;
		engine.comboType = GameEngine.COMBO_TYPE_DISABLE;
		engine.big = big[playerID];
		engine.enableSE = enableSE[playerID];
		if(playerID == 1) owner.bgmStatus.bgm = bgmno;
		engine.colorClearSize = big[playerID] ? 12 : 4;
		engine.ignoreHidden = true;

		engine.tspinAllowKick = false;
		engine.tspinEnable = false;
		engine.useAllSpinBonus = false;
	}

	@Override
	public void renderLast(GameEngine engine, int playerID) {
		// ステータス表示
		if(playerID == 0) {
			receiver.drawScoreFont(engine, playerID, -1, 0, "AVALANCHE VS", EventReceiver.COLOR_GREEN);

			receiver.drawScoreFont(engine, playerID, -1, 2, "OJAMA", EventReceiver.COLOR_PURPLE);
			String ojamaStr1P = String.valueOf(ojama[0]);
			if (ojamaAdd[0] > 0 && !(inFever[0] && ojamaAddToFever[0]))
				ojamaStr1P = ojamaStr1P + "(+" + String.valueOf(ojamaAdd[0]) + ")";
			String ojamaStr2P = String.valueOf(ojama[1]);
			if (ojamaAdd[1] > 0 && !(inFever[1] && ojamaAddToFever[1]))
				ojamaStr2P = ojamaStr2P + "(+" + String.valueOf(ojamaAdd[1]) + ")";
			receiver.drawScoreFont(engine, playerID, -1, 3, "1P:", EventReceiver.COLOR_RED);
			receiver.drawScoreFont(engine, playerID, 3, 3, ojamaStr1P, (ojama[0] > 0));
			receiver.drawScoreFont(engine, playerID, -1, 4, "2P:", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 3, 4, ojamaStr2P, (ojama[1] > 0));

			receiver.drawScoreFont(engine, playerID, -1, 6, "ATTACK", EventReceiver.COLOR_GREEN);
			receiver.drawScoreFont(engine, playerID, -1, 7, "1P: " + String.valueOf(ojamaSent[0]), EventReceiver.COLOR_RED);
			receiver.drawScoreFont(engine, playerID, -1, 8, "2P: " + String.valueOf(ojamaSent[1]), EventReceiver.COLOR_BLUE);

			receiver.drawScoreFont(engine, playerID, -1, 10, "SCORE", EventReceiver.COLOR_PURPLE);
			receiver.drawScoreFont(engine, playerID, -1, 11, "1P: ", EventReceiver.COLOR_RED);
			if (scgettime[0] > 0 && lastscore[0] > 0 && lastmultiplier[0] > 0)
				receiver.drawScoreFont(engine, playerID, 3, 11, "+" + lastscore[0] + "X" + lastmultiplier[0], EventReceiver.COLOR_RED);
			else
				receiver.drawScoreFont(engine, playerID, 3, 11, String.valueOf(score[0]), EventReceiver.COLOR_RED);

			receiver.drawScoreFont(engine, playerID, -1, 12, "2P: ", EventReceiver.COLOR_BLUE);
			if (scgettime[1] > 0 && lastscore[1] > 0 && lastmultiplier[1] > 0)
				receiver.drawScoreFont(engine, playerID, 3, 12, "+" + lastscore[1] + "X" + lastmultiplier[1], EventReceiver.COLOR_BLUE);
			else
				receiver.drawScoreFont(engine, playerID, 3, 12, String.valueOf(score[1]), EventReceiver.COLOR_BLUE);

			receiver.drawScoreFont(engine, playerID, -1, 14, "TIME", EventReceiver.COLOR_GREEN);
			receiver.drawScoreFont(engine, playerID, -1, 15, GeneralUtil.getTime(engine.statistics.time));

			if (inFever[0] || inFever[1])
			{
				receiver.drawScoreFont(engine, playerID, -1, 17, "FEVER OJAMA", EventReceiver.COLOR_PURPLE);
				String ojamaFeverStr1P = String.valueOf(ojamaFever[0]);
				if (ojamaAdd[0] > 0 && inFever[0] && ojamaAddToFever[0])
					ojamaFeverStr1P = ojamaFeverStr1P + "(+" + String.valueOf(ojamaAdd[0]) + ")";
				String ojamaFeverStr2P = String.valueOf(ojamaFever[1]);
				if (ojamaAdd[1] > 0 && inFever[1] && ojamaAddToFever[1])
					ojamaFeverStr2P = ojamaFeverStr2P + "(+" + String.valueOf(ojamaAdd[1]) + ")";
				receiver.drawScoreFont(engine, playerID, -1, 18, "1P:", EventReceiver.COLOR_RED);
				receiver.drawScoreFont(engine, playerID, 3, 18, ojamaFeverStr1P, (ojamaFever[0] > 0));
				receiver.drawScoreFont(engine, playerID, -1, 19, "2P:", EventReceiver.COLOR_BLUE);
				receiver.drawScoreFont(engine, playerID, 3, 19, ojamaFeverStr2P, (ojamaFever[1] > 0));
			}
		}

		if (!owner.engine[playerID].gameActive)
			return;
		int playerColor = (playerID == 0) ? EventReceiver.COLOR_RED : EventReceiver.COLOR_BLUE;
		if (feverThreshold[playerID] > 0)
		{
			receiver.drawMenuFont(engine, playerID, 0, 17, "FEVER POINT", playerColor);
			if (feverShowMeter[playerID])
			{
				if (inFever[playerID])
				{
					int color = (engine.statistics.time >> 2) % FEVER_METER_COLORS.length;
					for (int x = 0; x < feverThreshold[playerID]; x++)
					{
						if (color == 0)
							color = FEVER_METER_COLORS.length;
						color--;
						receiver.drawMenuFont(engine, playerID, x, 18, "d", FEVER_METER_COLORS[color]);
					}
				}
				else
				{
					int x = 0;
					for (x = 0; x < feverPoints[playerID]; x++)
						receiver.drawMenuFont(engine, playerID, x, 18, "d");
					for (; x < feverThreshold[playerID]; x++)
						receiver.drawMenuFont(engine, playerID, x, 18, "c");
				}
			}
			else
				receiver.drawMenuFont(engine, playerID, 0, 18, feverPoints[playerID] + " / " + feverThreshold[playerID], inFever[playerID]);
			receiver.drawMenuFont(engine, playerID, 0, 19, "FEVER TIME", playerColor);
			receiver.drawMenuFont(engine, playerID, 0, 20, GeneralUtil.getTime(feverTime[playerID]), inFever[playerID]);
		}
		if (inFever[playerID])
			receiver.drawMenuFont(engine, playerID, 2, 0, String.format("%2d",(feverTime[playerID]+59)/60),
					feverTime[playerID] < 360 ? EventReceiver.COLOR_RED : EventReceiver.COLOR_WHITE);
		else if (dangerColumnShowX[playerID])
			receiver.drawMenuFont(engine, playerID, 2, 0, dangerColumnDouble[playerID] ? "XX" : "X", EventReceiver.COLOR_RED);
		if (ojamaHard[playerID] > 0 && engine.field != null)
			for (int x = 0; x < engine.field.getWidth(); x++)
				for (int y = 0; y < engine.field.getHeight(); y++)
				{
					int hard = engine.field.getBlock(x, y).hard;
					if (hard > 0)
						receiver.drawMenuFont(engine, playerID, x, y, String.valueOf(hard), EventReceiver.COLOR_YELLOW);
				}


		int textHeight = 13;
		if (engine.field != null)
			textHeight = engine.field.getHeight()+1;
		if (chain[playerID] > 0 && chainDisplay[playerID] > 0 && chainDisplayType[playerID] != CHAIN_DISPLAY_NONE)
		{
			int color = EventReceiver.COLOR_YELLOW;
			if (chainDisplayType[playerID] == CHAIN_DISPLAY_PLAYER)
				color = playerColor;
			else if (chainDisplayType[playerID] == CHAIN_DISPLAY_SIZE)
				color = chain[playerID] >= rensaShibari[playerID] ? EventReceiver.COLOR_GREEN : EventReceiver.COLOR_RED;
			receiver.drawMenuFont(engine, playerID, chain[playerID] > 9 ? 0 : 1, textHeight, chain[playerID] + " CHAIN!", color);
		}
		if(zenKeshi[playerID] || zenKeshiDisplay[playerID] > 0)
			receiver.drawMenuFont(engine, playerID, 0, textHeight+1, "ZENKESHI!", EventReceiver.COLOR_YELLOW);
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
		int enemyID = 0;
		if(playerID == 0) enemyID = 1;

		if (big[playerID])
			avalanche >>= 2;
		// Line clear bonus
		int pts = avalanche*10;
		int ojamaNew = 0;
		if (avalanche > 0) {
			cleared[playerID] = true;
			if (zenKeshi[playerID] && zenKeshiType[playerID] == ZENKESHI_MODE_ON)
				ojamaNew += 30;
			if (engine.field.isEmpty()) {
				engine.playSE("bravo");
				zenKeshi[playerID] = true;
				engine.statistics.score += 2100;
				score[playerID] += 2100;
			}
			else
				zenKeshi[playerID] = false;

			chain[playerID] = engine.chain;
			chainDisplay[playerID] = 60;
			engine.playSE("combo" + Math.min(chain[playerID], 20));
			if (chain[playerID] == 1)
				ojamaAddToFever[enemyID] = inFever[enemyID];
			int multiplier = engine.field.colorClearExtraCount;
			if (big[playerID])
				multiplier >>= 2;
			if (engine.field.colorsCleared > 1)
				multiplier += (engine.field.colorsCleared-1)*2;
			/*
			if (multiplier < 0)
				multiplier = 0;
			if (chain == 0)
				firstExtra = avalanche > engine.colorClearSize;
			*/
			if (!newChainPower[playerID])
			{
				if (chain[playerID] == 2)
					multiplier += 8;
				else if (chain[playerID] == 3)
					multiplier += 16;
				else if (chain[playerID] >= 4)
					multiplier += 32*(chain[playerID]-3);
			}
			else
			{
				int[] powers = inFever[playerID] ? FEVER_POWERS : CHAIN_POWERS;
				if (chain[playerID] > powers.length)
					multiplier += powers[powers.length-1];
				else
					multiplier += powers[chain[playerID]-1];
			}
			/*
			if (firstExtra)
				multiplier++;
			*/

			if (multiplier > 999)
				multiplier = 999;
			if (multiplier < 1)
				multiplier = 1;

			lastscore[playerID] = pts;
			lastmultiplier[playerID] = multiplier;
			scgettime[playerID] = 25;
			int ptsTotal = pts*multiplier;
			score[playerID] += ptsTotal;

			boolean countered = false;
			if (chain[playerID] >= rensaShibari[playerID])
			{
				//Add ojama
				int rate = ojamaRate[playerID];
				if (hurryupSeconds[playerID] > 0 && engine.statistics.time > hurryupSeconds[playerID])
					rate >>= engine.statistics.time / (hurryupSeconds[playerID] * 60);
				if (rate <= 0)
					rate = 1;
				if (inFever[playerID])
					ojamaNew += ((ptsTotal*feverPower[playerID])+(10*rate)-1) / (10*rate);
				else
					ojamaNew += (ptsTotal+rate-1)/rate;
				ojamaSent[playerID] += ojamaNew;

				if (feverThreshold[playerID] > 0 && feverTimeCriteria[playerID] == FEVER_TIME_CRITERIA_ATTACK && !inFever[playerID])
					feverTime[playerID] = Math.min(feverTime[playerID]+60,feverTimeMax[playerID]*60);

				if (ojamaCounterMode[playerID] != OJAMA_COUNTER_OFF)
				{
					//Counter ojama
					if (inFever[playerID])
					{
						if (ojamaFever[playerID] > 0 && ojamaNew > 0)
						{
							int delta = Math.min(ojamaFever[playerID], ojamaNew);
							ojamaFever[playerID] -= delta;
							ojamaNew -= delta;
							countered = true;
						}
						if (ojamaAdd[playerID] > 0 && ojamaNew > 0)
						{
							int delta = Math.min(ojamaAdd[playerID], ojamaNew);
							ojamaAdd[playerID] -= delta;
							ojamaNew -= delta;
							countered = true;
						}
					}
					if (ojama[playerID] > 0 && ojamaNew > 0)
					{
						int delta = Math.min(ojama[playerID], ojamaNew);
						ojama[playerID] -= delta;
						ojamaNew -= delta;
						countered = true;
					}
					if (ojamaAdd[playerID] > 0 && ojamaNew > 0)
					{
						int delta = Math.min(ojamaAdd[playerID], ojamaNew);
						ojamaAdd[playerID] -= delta;
						ojamaNew -= delta;
						countered = true;
					}
				}
				if (ojamaNew > 0)
					ojamaAdd[enemyID] += ojamaNew;
				if ((countered && feverPointCriteria[playerID] != FEVER_POINT_CRITERIA_CLEAR) ||
						(engine.field.garbageCleared > 0 && feverPointCriteria[playerID] != FEVER_POINT_CRITERIA_COUNTER))
				{
					if (feverThreshold[playerID] > 0 && feverThreshold[playerID] > feverPoints[playerID])
						feverPoints[playerID]++;
					if (feverThreshold[enemyID] > 0 && feverTimeCriteria[enemyID] == FEVER_TIME_CRITERIA_COUNTER && !inFever[enemyID])
						feverTime[enemyID] = Math.min(feverTime[enemyID]+60,feverTimeMax[enemyID]*60);
				}
			}
		}
		else if (!engine.field.canCascade())
			cleared[playerID] = false;
	}

	public boolean lineClearEnd(GameEngine engine, int playerID) {
		int enemyID = 0;
		if(playerID == 0) enemyID = 1;
		if (ojamaAdd[enemyID] > 0)
		{
			if (ojamaAddToFever[enemyID] && inFever[enemyID])
				ojamaFever[enemyID] += ojamaAdd[enemyID];
			else
				ojama[enemyID] += ojamaAdd[enemyID];
			ojamaAdd[enemyID] = 0;
		}
		int feverChainNow = feverChain[playerID];
		if (zenKeshi[playerID] && zenKeshiType[playerID] == ZENKESHI_MODE_FEVER)
		{
			if (feverTime[playerID] > 0)
				feverTime[playerID] = Math.min(feverTime[playerID]+300, feverTimeMax[playerID]*60);
			if (inFever[playerID] || feverPoints[playerID] >= feverThreshold[playerID])
			{
				feverChain[playerID] += 2;
				if (feverChain[playerID] > feverChainMax[playerID])
					feverChain[playerID] = feverChainMax[playerID];
			}
			else
				loadFeverMap(engine, playerID, 4);
			zenKeshi[playerID] = false;
			zenKeshiDisplay[playerID] = 120;
		}
		//Reset Fever board if necessary
		if (inFever[playerID] && cleared[playerID])
		{
			feverChain[playerID] += Math.max(engine.chain+1-feverChainNow, -2);
			if (feverChain[playerID] < feverChainMin[playerID])
				feverChain[playerID] = feverChainMin[playerID];
			if (feverChain[playerID] > feverChainMax[playerID])
				feverChain[playerID] = feverChainMax[playerID];
			if (feverChain[playerID] > feverChainNow)
				engine.playSE("cool");
			else if (feverChain[playerID] < feverChainNow)
				engine.playSE("regret");
			if (feverTime[playerID] > 0)
			{
				if (engine.chain > 2)
					feverTime[playerID] += (engine.chain-2)*30;
				loadFeverMap(engine, playerID, feverChain[playerID]);
			}
		}
		//Check to end Fever Mode
		if (inFever[playerID] && feverTime[playerID] == 0)
		{
			engine.playSE("levelup");
			inFever[playerID] = false;
			feverTime[playerID] = feverTimeMin[playerID] * 60;
			feverPoints[playerID] = 0;
			engine.field = feverBackupField[playerID];
			if (engine.field != null && ojamaMeter[playerID])
				engine.meterValue = ojama[playerID] * receiver.getBlockGraphicsHeight(engine, playerID) /
					engine.field.getWidth();
			ojama[playerID] += ojamaFever[playerID];
			ojamaFever[playerID] = 0;
			ojamaAddToFever[playerID] = false;
		}
		//Drop garbage if needed.
		int ojamaNow = inFever[playerID] ? ojamaFever[playerID] : ojama[playerID];
		if (ojamaNow > 0 && !ojamaDrop[playerID] && (!cleared[playerID] ||
				(!inFever[playerID] && ojamaCounterMode[playerID] != OJAMA_COUNTER_FEVER)))
		{
			ojamaDrop[playerID] = true;
			int drop = Math.min(ojamaNow, maxAttack[playerID]);
			if (inFever[playerID])
				ojamaFever[playerID] -= drop;
			else
				ojama[playerID] -= drop;
			engine.field.garbageDrop(engine, drop, big[playerID], ojamaHard[playerID]);
			engine.field.setAllSkin(engine.getSkin());
			return true;
		}
		//Check for game over
		if (engine.field != null)
		{
			if (!engine.field.getBlockEmpty(2, 0) ||
					(dangerColumnDouble[playerID] && !engine.field.getBlockEmpty(3, 0)))
			{
				engine.stat = GameEngine.STAT_GAMEOVER;
			}
		}
		//Check to start Fever Mode
		if (!inFever[playerID] && feverPoints[playerID] >= feverThreshold[playerID] && feverThreshold[playerID] > 0)
		{
			engine.playSE("levelup");
			inFever[playerID] = true;
			feverBackupField[playerID] = engine.field;
			engine.field = null;
			loadFeverMap(engine, playerID, feverChain[playerID]);
			if (!ojamaMeter[playerID])
				engine.meterValue = 0;
		}
		return false;
	}

	private void loadFeverMap(GameEngine engine, int playerID, int chain) {
		engine.createFieldIfNeeded();
		engine.field.reset();
		engine.field.stringToField(propFeverMap[playerID].getProperty(
				feverMapSubsets[playerID][engine.random.nextInt(feverMapSubsets[playerID].length)] +
				"." + numColors[playerID] + "colors." + chain + "chain"));
		engine.field.setAllAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_LEFT, false);
		engine.field.setAllAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_DOWN, false);
		engine.field.setAllAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_UP, false);
		engine.field.setAllAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT, false);
		engine.field.setAllAttribute(Block.BLOCK_ATTRIBUTE_GARBAGE, false);
		engine.field.setAllAttribute(Block.BLOCK_ATTRIBUTE_ANTIGRAVITY, false);
		engine.field.setAllSkin(engine.getSkin());
		engine.field.shuffleColors(BLOCK_COLORS, numColors[playerID], engine.random);
	}

	/*
	 * Called after every frame
	 */
	@Override
	public void onLast(GameEngine engine, int playerID) {
		if (scgettime[playerID] > 0)
			scgettime[playerID]--;
		if (zenKeshiDisplay[playerID] > 0)
			zenKeshiDisplay[playerID]--;
		if (chainDisplay[playerID] > 0)
			chainDisplay[playerID]--;

		if (inFever[playerID] && feverTime[playerID] > 0 && engine.timerActive)
		{
			feverTime[playerID]--;
			if((feverTime[playerID] > 0) && (feverTime[playerID] <= 360) && (feverTime[playerID] % 60 == 0))
				engine.playSE("countdown");
			else if (feverTime[playerID] == 0)
				engine.playSE("levelstop");
		}
		int width = 1;
		if (engine.field != null)
			width = engine.field.getWidth();
		int blockHeight = receiver.getBlockGraphicsHeight(engine, playerID);
		// せり上がりMeter
		if (ojamaMeter[playerID] || feverThreshold[playerID] == 0)
		{
			int ojamaNow = inFever[playerID] ? ojamaFever[playerID] : ojama[playerID];
			int value = ojamaNow * blockHeight / width;
			if(ojamaNow >= 5*width) engine.meterColor = GameEngine.METER_COLOR_RED;
			else if(ojamaNow >= width) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
			else if(ojamaNow >= 1) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
			else engine.meterColor = GameEngine.METER_COLOR_GREEN;
			if (value > engine.meterValue)
				engine.meterValue++;
			else if (value < engine.meterValue)
				engine.meterValue--;
		}
		else
		{
			if (!inFever[playerID])
			{
				engine.meterValue = (receiver.getMeterMax(engine) * feverPoints[playerID]) / feverThreshold[playerID];
				if (feverPoints[playerID] == feverThreshold[playerID] - 1)
					engine.meterColor = GameEngine.METER_COLOR_ORANGE;
				else if (feverPoints[playerID] < feverThreshold[playerID] - 1)
					engine.meterColor = GameEngine.METER_COLOR_YELLOW;
				else if (feverPoints[playerID] == feverThreshold[playerID])
					engine.meterColor = GameEngine.METER_COLOR_RED;
			}
			else
			{
				engine.meterValue = (feverTime[playerID] * receiver.getMeterMax(engine)) / (feverTimeMax[playerID] * 60);
				engine.meterColor = GameEngine.METER_COLOR_GREEN;
				if(feverTime[playerID] <= feverTimeMin[playerID]*15) engine.meterColor = GameEngine.METER_COLOR_RED;
				else if(feverTime[playerID] <= feverTimeMin[playerID]*30) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
				else if(feverTime[playerID] <= feverTimeMin[playerID]*60) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
			}
		}

		// 決着
		if((playerID == 1) && (owner.engine[0].gameActive)) {
			boolean p1Lose = (owner.engine[0].stat == GameEngine.STAT_GAMEOVER);
			boolean p2Lose = (owner.engine[1].stat == GameEngine.STAT_GAMEOVER);
			if(p1Lose && p2Lose) {
				// 引き分け
				winnerID = -1;
				owner.engine[0].stat = GameEngine.STAT_GAMEOVER;
				owner.engine[1].stat = GameEngine.STAT_GAMEOVER;
			} else if(p2Lose && !p1Lose) {
				// 1P勝利
				winnerID = 0;
				owner.engine[0].stat = GameEngine.STAT_EXCELLENT;
				owner.engine[1].stat = GameEngine.STAT_GAMEOVER;
			} else if(p1Lose && !p2Lose) {
				// 2P勝利
				winnerID = 1;
				owner.engine[0].stat = GameEngine.STAT_GAMEOVER;
				owner.engine[1].stat = GameEngine.STAT_EXCELLENT;
			}
			if (p1Lose || p2Lose) {
				owner.engine[0].gameActive = false;
				owner.engine[1].gameActive = false;
				owner.engine[0].resetStatc();
				owner.engine[1].resetStatc();
				owner.engine[0].statc[1] = 1;
				owner.engine[1].statc[1] = 1;
				owner.bgmStatus.bgm = BGMStatus.BGM_NOTHING;
			}
		}
	}

	@Override
	public boolean onMove (GameEngine engine, int playerID) {
		cleared[playerID] = false;
		ojamaDrop[playerID] = false;
		return false;
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

		receiver.drawMenuFont(engine, playerID, 0, 3, "ATTACK", EventReceiver.COLOR_ORANGE);
		String strScore = String.format("%10d", ojamaSent[playerID]);
		receiver.drawMenuFont(engine, playerID, 0, 4, strScore);

		receiver.drawMenuFont(engine, playerID, 0, 5, "LINE", EventReceiver.COLOR_ORANGE);
		String strLines = String.format("%10d", engine.statistics.lines);
		receiver.drawMenuFont(engine, playerID, 0, 6, strLines);

		receiver.drawMenuFont(engine, playerID, 0, 7, "PIECE", EventReceiver.COLOR_ORANGE);
		String strPiece = String.format("%10d", engine.statistics.totalPieceLocked);
		receiver.drawMenuFont(engine, playerID, 0, 8, strPiece);

		receiver.drawMenuFont(engine, playerID, 0, 9, "ATTACK/MIN", EventReceiver.COLOR_ORANGE);
		float apm = (float)(ojamaSent[playerID] * 3600) / (float)(engine.statistics.time);
		String strAPM = String.format("%10g", apm);
		receiver.drawMenuFont(engine, playerID, 0, 10, strAPM);

		receiver.drawMenuFont(engine, playerID, 0, 11, "LINE/MIN", EventReceiver.COLOR_ORANGE);
		String strLPM = String.format("%10g", engine.statistics.lpm);
		receiver.drawMenuFont(engine, playerID, 0, 12, strLPM);

		receiver.drawMenuFont(engine, playerID, 0, 13, "PIECE/SEC", EventReceiver.COLOR_ORANGE);
		String strPPS = String.format("%10g", engine.statistics.pps);
		receiver.drawMenuFont(engine, playerID, 0, 14, strPPS);

		receiver.drawMenuFont(engine, playerID, 0, 15, "TIME", EventReceiver.COLOR_ORANGE);
		String strTime = String.format("%10s", GeneralUtil.getTime(owner.engine[0].statistics.time));
		receiver.drawMenuFont(engine, playerID, 0, 16, strTime);
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

		owner.replayProp.setProperty("avalanchevs.version", version);
	}
}
