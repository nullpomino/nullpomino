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
import mu.nu.nullpo.game.component.Field;
import mu.nu.nullpo.game.component.Piece;
import mu.nu.nullpo.game.event.EventReceiver;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.util.CustomProperties;
import mu.nu.nullpo.util.GeneralUtil;

/**
 * AVALANCHE VS DUMMY Mode
 */
public abstract class AvalancheVSDummyMode extends AbstractMode {
	/** Enabled piece types */
	public static final int[] PIECE_ENABLE = {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0};

	/** Block colors */
	public static final int[] BLOCK_COLORS =
	{
		Block.BLOCK_COLOR_RED,
		Block.BLOCK_COLOR_GREEN,
		Block.BLOCK_COLOR_BLUE,
		Block.BLOCK_COLOR_YELLOW,
		Block.BLOCK_COLOR_PURPLE
	};

	/** Fever map files list */
	public static final String[] FEVER_MAPS =
	{
		"Fever", "15th", "15thDS", "7", "Compendium"
	};

	/** Chain multipliers */
	public static final int[] CHAIN_POWERS = {
		4, 12, 24, 33, 50, 101, 169, 254, 341, 428, 538, 648, 763, 876, 990, 999 //Arle
	};

	/** Number of players */
	public static final int MAX_PLAYERS = 2;

	/** Ojama counter setting constants */
	public static final int OJAMA_COUNTER_OFF = 0, OJAMA_COUNTER_ON = 1, OJAMA_COUNTER_FEVER = 2;

	/** Names of ojama counter settings */
	public static final String[] OJAMA_COUNTER_STRING = {"OFF", "ON", "FEVER"};

	/** Zenkeshi setting constants */
	public static final int ZENKESHI_MODE_OFF = 0, ZENKESHI_MODE_ON = 1, ZENKESHI_MODE_FEVER = 2;

	/** Names of zenkeshi settings */
	public static final String[] ZENKESHI_TYPE_NAMES = {"OFF", "ON", "FEVER"};

	/** Names of outline settings */
	public static final String[] OUTLINE_TYPE_NAMES = {"NORMAL", "COLOR", "NONE"};

	/** Names of chain display settings */
	public static final String[] CHAIN_DISPLAY_NAMES = {"OFF", "YELLOW", "PLAYER", "SIZE"};

	/** Constants for chain display settings */
	public static final int CHAIN_DISPLAY_NONE = 0, CHAIN_DISPLAY_YELLOW = 1,
		CHAIN_DISPLAY_PLAYER = 2, CHAIN_DISPLAY_SIZE = 3;

	/** Each player's frame color */
	public static final int[] PLAYER_COLOR_FRAME = {GameEngine.FRAME_COLOR_RED, GameEngine.FRAME_COLOR_BLUE};

	protected GameManager owner;

	protected EventReceiver receiver;

	/** Rule settings for countering ojama not yet dropped */
	protected int[] ojamaCounterMode;

	/** 溜まっているojama blockのcount */
	protected int[] ojama;

	/** 送ったojama blockのcount */
	protected int[] ojamaSent;

	/** Time to display the most recent increase in score */
	protected int[] scgettime;

	/** 使用するBGM */
	protected int bgmno;

	/** Big */
	protected boolean[] big;

	/** Sound effectsON/OFF */
	protected boolean[] enableSE;

	/** Map使用 flag */
	protected boolean[] useMap;

	/** 使用するMapセット number */
	protected int[] mapSet;

	/** Map number(-1でランダム) */
	protected int[] mapNumber;

	/** Last preset number used */
	protected int[] presetNumber;

	/** 勝者 */
	protected int winnerID;

	/** MapセットのProperty file */
	protected CustomProperties[] propMap;

	/** MaximumMap number */
	protected int[] mapMaxNo;

	/** バックアップ用field (Mapをリプレイに保存するときに使用) */
	protected Field[] fldBackup;

	/** Map選択用乱count */
	protected Random randMap;

	/** Flag for all clear */
	protected boolean[] zenKeshi;

	/** Amount of points earned from most recent clear */
	protected int[] lastscore, lastmultiplier;

	/** Amount of ojama added in current chain */
	protected int[] ojamaAdd;

	/** Score */
	protected int[] score;

	/** Max amount of ojama dropped at once */
	protected int[] maxAttack;

	/** Number of colors to use */
	protected int[] numColors;

	/** Minimum chain count needed to send ojama */
	protected int[] rensaShibari;

	/** Denominator for score-to-ojama conversion */
	protected int[] ojamaRate;

	/** Settings for hard ojama blocks */
	protected int[] ojamaHard;

	/** Hurryup開始までの秒count(0でHurryupなし) */
	protected int[] hurryupSeconds;

	/** Set to true when last drop resulted in a clear */
	protected boolean[] cleared;

	/** Set to true when dropping ojama blocks */
	protected boolean[] ojamaDrop;

	/** Time to display "ZENKESHI!" */
	protected int[] zenKeshiDisplay;

	/** Zenkeshi reward type */
	protected int[] zenKeshiType;

	/** Selected fever map set file */
	protected int[] feverMapSet;

	/** Selected fever map set file's subset list */
	protected String[][] feverMapSubsets;

	/** Fever map CustomProperties */
	protected CustomProperties[] propFeverMap;

	/** Chain level boundaries for Fever Mode */
	protected int[] feverChainMin, feverChainMax;

	/** Selected outline type */
	protected int[] outlineType;

	/** If true, both columns 3 and 4 are danger columns */
	protected boolean[] dangerColumnDouble;

	/** If true, red X's appear at tops of danger columns */
	protected boolean[] dangerColumnShowX;

	/** Time to display last chain */
	protected int[] chainDisplay;

	/** Type of chain display */
	protected int[] chainDisplayType;

	/** True to use new (Fever) chain powers */
	protected boolean[] newChainPower;

	/** True to use slower falling animations, false to use faster */
	protected boolean[] cascadeSlow;

	/** True to use big field display */
	protected boolean bigDisplay;

	/*
	 * Mode name
	 */
	@Override
	public String getName() {
		return "AVALANCHE VS DUMMY";
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
		return GameEngine.GAMESTYLE_AVALANCHE;
	}

	/*
	 * Mode initialization
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

		cleared = new boolean[MAX_PLAYERS];
		ojamaDrop = new boolean[MAX_PLAYERS];
		zenKeshiDisplay = new int[MAX_PLAYERS];
		zenKeshiType = new int[MAX_PLAYERS];
		outlineType = new int[MAX_PLAYERS];
		dangerColumnDouble = new boolean[MAX_PLAYERS];
		dangerColumnShowX = new boolean[MAX_PLAYERS];
		chainDisplay = new int[MAX_PLAYERS];
		chainDisplayType = new int[MAX_PLAYERS];
		newChainPower = new boolean[MAX_PLAYERS];
		cascadeSlow = new boolean[MAX_PLAYERS];

		feverMapSet = new int[MAX_PLAYERS];
		propFeverMap = new CustomProperties[MAX_PLAYERS];
		feverMapSubsets = new String[MAX_PLAYERS][];
		feverChainMin = new int[MAX_PLAYERS];
		feverChainMax = new int[MAX_PLAYERS];

		winnerID = -1;
	}

	/**
	 * Read speed presets
	 * @param engine GameEngine
	 * @param prop Property file to read from
	 * @param preset Preset number
	 */
	protected void loadPreset(GameEngine engine, CustomProperties prop, int preset, String name) {
		engine.speed.gravity = prop.getProperty("avalanchevs" + name + ".gravity." + preset, 4);
		engine.speed.denominator = prop.getProperty("avalanchevs" + name + ".denominator." + preset, 256);
		engine.speed.are = prop.getProperty("avalanchevs" + name + ".are." + preset, 30);
		engine.speed.areLine = prop.getProperty("avalanchevs" + name + ".areLine." + preset, 30);
		engine.speed.lineDelay = prop.getProperty("avalanchevs" + name + ".lineDelay." + preset, 10);
		engine.speed.lockDelay = prop.getProperty("avalanchevs" + name + ".lockDelay." + preset, 60);
		engine.speed.das = prop.getProperty("avalanchevs" + name + ".das." + preset, 14);
		engine.cascadeDelay = prop.getProperty("avalanchevs" + name + ".fallDelay." + preset, 1);
		engine.cascadeClearDelay = prop.getProperty("avalanchevs" + name + ".clearDelay." + preset, 10);
	}

	/**
	 * Save speed presets
	 * @param engine GameEngine
	 * @param prop Property file to save to
	 * @param preset Preset number
	 */
	protected void savePreset(GameEngine engine, CustomProperties prop, int preset, String name) {
		prop.setProperty("avalanchevs" + name + ".gravity." + preset, engine.speed.gravity);
		prop.setProperty("avalanchevs" + name + ".denominator." + preset, engine.speed.denominator);
		prop.setProperty("avalanchevs" + name + ".are." + preset, engine.speed.are);
		prop.setProperty("avalanchevs" + name + ".areLine." + preset, engine.speed.areLine);
		prop.setProperty("avalanchevs" + name + ".lineDelay." + preset, engine.speed.lineDelay);
		prop.setProperty("avalanchevs" + name + ".lockDelay." + preset, engine.speed.lockDelay);
		prop.setProperty("avalanchevs" + name + ".das." + preset, engine.speed.das);
		prop.setProperty("avalanchevs" + name + ".fallDelay." + preset, engine.cascadeDelay);
		prop.setProperty("avalanchevs" + name + ".clearDelay." + preset, engine.cascadeClearDelay);
	}

	/**
	 * Load settings not related to speeds
	 * Note: Subclasses need to load ojamaRate and ojamaHard, since default values vary.
	 * @param engine GameEngine
	 * @param prop Property file to read from
	 */
	protected void loadOtherSetting(GameEngine engine, CustomProperties prop, String name) {
		int playerID = engine.playerID;
		bgmno = prop.getProperty("avalanchevs" + name + ".bgmno", 0);
		ojamaCounterMode[playerID] = prop.getProperty("avalanchevs" + name + ".ojamaCounterMode", OJAMA_COUNTER_ON);
		big[playerID] = prop.getProperty("avalanchevs" + name + ".big.p" + playerID, false);
		enableSE[playerID] = prop.getProperty("avalanchevs" + name + ".enableSE.p" + playerID, true);
		hurryupSeconds[playerID] = prop.getProperty("avalanchevs" + name + ".hurryupSeconds.p" + playerID, 192);
		useMap[playerID] = prop.getProperty("avalanchevs" + name + ".useMap.p" + playerID, false);
		mapSet[playerID] = prop.getProperty("avalanchevs" + name + ".mapSet.p" + playerID, 0);
		mapNumber[playerID] = prop.getProperty("avalanchevs" + name + ".mapNumber.p" + playerID, -1);
		feverMapSet[playerID] = prop.getProperty("avalanchevs" + name + ".feverMapSet.p" + playerID, 0);
		presetNumber[playerID] = prop.getProperty("avalanchevs" + name + ".presetNumber.p" + playerID, 0);
		maxAttack[playerID] = prop.getProperty("avalanchevs" + name + ".maxAttack.p" + playerID, 30);
		numColors[playerID] = prop.getProperty("avalanchevs" + name + ".numColors.p" + playerID, 5);
		rensaShibari[playerID] = prop.getProperty("avalanchevs" + name + ".rensaShibari.p" + playerID, 1);
		zenKeshiType[playerID] = prop.getProperty("avalanchevs" + name + ".zenKeshiType.p" + playerID, 1);
		outlineType[playerID] = prop.getProperty("avalanchevs" + name + ".outlineType.p" + playerID, 1);
		dangerColumnDouble[playerID] = prop.getProperty("avalanchevs" + name + ".dangerColumnDouble.p" + playerID, false);
		dangerColumnShowX[playerID] = prop.getProperty("avalanchevs" + name + ".dangerColumnShowX.p" + playerID, false);
		chainDisplayType[playerID] = prop.getProperty("avalanchevs" + name + ".chainDisplayType.p" + playerID, 1);
		newChainPower[playerID] = prop.getProperty("avalanchevs" + name + ".newChainPower.p" + playerID, false);
		cascadeSlow[playerID] = prop.getProperty("avalanchevs" + name + ".cascadeSlow.p" + playerID, false);
		bigDisplay = prop.getProperty("avalanchevs" + name + ".bigDisplay", false);
		engine.colorClearSize = prop.getProperty("avalanchevs" + name + ".clearSize.p" + playerID, 4);
		if (feverMapSet[playerID] >= 0 && feverMapSet[playerID] < FEVER_MAPS.length)
			loadMapSetFever(engine, playerID, feverMapSet[playerID], true);
	}

	/**
	 * Save settings not related to speeds
	 * @param engine GameEngine
	 * @param prop Property file to save to
	 */
	protected void saveOtherSetting(GameEngine engine, CustomProperties prop, String name) {
		int playerID = engine.playerID;
		prop.setProperty("avalanchevs" + name + ".bgmno", bgmno);
		prop.setProperty("avalanchevs" + name + ".ojamaCounterMode", ojamaCounterMode[playerID]);
		prop.setProperty("avalanchevs" + name + ".big.p" + playerID, big[playerID]);
		prop.setProperty("avalanchevs" + name + ".enableSE.p" + playerID, enableSE[playerID]);
		prop.setProperty("avalanchevs" + name + ".hurryupSeconds.p" + playerID, hurryupSeconds[playerID]);
		prop.setProperty("avalanchevs" + name + ".useMap.p" + playerID, useMap[playerID]);
		prop.setProperty("avalanchevs" + name + ".mapSet.p" + playerID, mapSet[playerID]);
		prop.setProperty("avalanchevs" + name + ".mapNumber.p" + playerID, mapNumber[playerID]);
		prop.setProperty("avalanchevs" + name + ".feverMapSet.p" + playerID, feverMapSet[playerID]);
		prop.setProperty("avalanchevs" + name + ".presetNumber.p" + playerID, presetNumber[playerID]);
		prop.setProperty("avalanchevs" + name + ".maxAttack.p" + playerID, maxAttack[playerID]);
		prop.setProperty("avalanchevs" + name + ".numColors.p" + playerID, numColors[playerID]);
		prop.setProperty("avalanchevs" + name + ".rensaShibari.p" + playerID, rensaShibari[playerID]);
		prop.setProperty("avalanchevs" + name + ".ojamaRate.p" + playerID, ojamaRate[playerID]);
		prop.setProperty("avalanchevs" + name + ".ojamaHard.p" + playerID, ojamaHard[playerID]);
		prop.setProperty("avalanchevs" + name + ".zenKeshiType.p" + playerID, zenKeshiType[playerID]);
		prop.setProperty("avalanchevs" + name + ".outlineType.p" + playerID, outlineType[playerID]);
		prop.setProperty("avalanchevs" + name + ".dangerColumnDouble.p" + playerID, dangerColumnDouble[playerID]);
		prop.setProperty("avalanchevs" + name + ".dangerColumnShowX.p" + playerID, dangerColumnShowX[playerID]);
		prop.setProperty("avalanchevs" + name + ".chainDisplayType.p" + playerID, chainDisplayType[playerID]);
		prop.setProperty("avalanchevs" + name + ".newChainPower.p" + playerID, newChainPower[playerID]);
		prop.setProperty("avalanchevs" + name + ".cascadeSlow.p" + playerID, cascadeSlow[playerID]);
		prop.setProperty("avalanchevs" + name + ".bigDisplay", bigDisplay);
		prop.setProperty("avalanchevs" + name + ".clearSize.p" + playerID, engine.colorClearSize);
	}

	/**
	 * Map読み込み
	 * @param field field
	 * @param prop Property file to read from
	 * @param preset 任意のID
	 */
	protected void loadMap(Field field, CustomProperties prop, int id) {
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
	protected void saveMap(Field field, CustomProperties prop, int id) {
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
	protected void loadMapPreview(GameEngine engine, int playerID, int id, boolean forceReload) {
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

	protected void loadMapSetFever(GameEngine engine, int playerID, int id, boolean forceReload) {
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
		engine.dominoQuickTurn = true;

		ojama[playerID] = 0;
		ojamaAdd[playerID] = 0;
		ojamaSent[playerID] = 0;
		score[playerID] = 0;
		zenKeshi[playerID] = false;
		scgettime[playerID] = 0;
		cleared[playerID] = false;
		ojamaDrop[playerID] = false;
		zenKeshiDisplay[playerID] = 0;
		chainDisplay[playerID] = 0;
	}

	/*
	 * Called for initialization during Ready (before initialization)
	 */
	@Override
	public boolean onReady(GameEngine engine, int playerID) {
		if(engine.statc[0] == 0)
			return readyInit(engine, playerID);
		return false;
	}

	public boolean readyInit(GameEngine engine, int playerID) {
		engine.numColors = numColors[playerID];
		engine.lineGravityType = cascadeSlow[playerID] ? GameEngine.LINE_GRAVITY_CASCADE_SLOW : GameEngine.LINE_GRAVITY_CASCADE;
		engine.displaysize = bigDisplay ? 1 : 0;
		engine.sticky = 2;

		if(outlineType[playerID] == 0) engine.blockOutlineType = GameEngine.BLOCK_OUTLINE_NORMAL;
		if(outlineType[playerID] == 1) engine.blockOutlineType = GameEngine.BLOCK_OUTLINE_SAMECOLOR;
		if(outlineType[playerID] == 2) engine.blockOutlineType = GameEngine.BLOCK_OUTLINE_NONE;

		if (big[playerID])
		{
			engine.fieldHeight = 6;
			engine.fieldWidth = 3;
			engine.field = null;
			engine.colorClearSize = 3;
			engine.displaysize = 1;
			engine.createFieldIfNeeded();
			zenKeshiType[playerID] = ZENKESHI_MODE_OFF;
			ojamaHard[playerID] = 0;
		}
		else if (feverMapSet[playerID] >= 0 && feverMapSet[playerID] < FEVER_MAPS.length)
			loadMapSetFever(engine, playerID, feverMapSet[playerID], true);
		// Map読み込み・リプレイ保存用にバックアップ
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
		return false;
	}

	/*
	 * Called at game start
	 */
	@Override
	public void startGame(GameEngine engine, int playerID) {
		engine.b2bEnable = false;
		engine.comboType = GameEngine.COMBO_TYPE_DISABLE;
		engine.enableSE = enableSE[playerID];
		if(playerID == 1) owner.bgmStatus.bgm = bgmno;
		engine.ignoreHidden = true;

		engine.tspinAllowKick = false;
		engine.tspinEnable = false;
		engine.useAllSpinBonus = false;
	}

	/*
	 * Called when hard drop used
	 */
	@Override
	public void afterHardDropFall(GameEngine engine, int playerID, int fall) {
		engine.statistics.score += fall;
	}

	/*
	 * Called when soft drop used
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
		if (avalanche > 0) {
			cleared[playerID] = true;

			chainDisplay[playerID] = 60;
			engine.playSE("combo" + Math.min(engine.chain, 20));
			onClear(engine, playerID);

			int pts = calcPts(engine, playerID, avalanche);

			int multiplier = engine.field.colorClearExtraCount;
			if (big[playerID])
				multiplier >>= 2;
			if (engine.field.colorsCleared > 1)
				multiplier += (engine.field.colorsCleared-1)*2;

			multiplier += calcChainMultiplier(engine, playerID, engine.chain);

			if (multiplier > 999)
				multiplier = 999;
			if (multiplier < 1)
				multiplier = 1;

			lastscore[playerID] = pts;
			lastmultiplier[playerID] = multiplier;
			scgettime[playerID] = 25;
			int ptsTotal = pts*multiplier;
			score[playerID] += ptsTotal;

			if (engine.chain >= rensaShibari[playerID])
				addOjama(engine, playerID, ptsTotal);

			if (engine.field.isEmpty()) {
				engine.playSE("bravo");
				zenKeshi[playerID] = true;
				engine.statistics.score += 2100;
				score[playerID] += 2100;
			}
			else
				zenKeshi[playerID] = false;
		}
		else if (!engine.field.canCascade())
			cleared[playerID] = false;
	}

	protected int calcPts (GameEngine engine, int playerID, int avalanche) {
		return avalanche*10;
	}

	protected int calcChainMultiplier(GameEngine engine, int playerID, int chain) {
		if (newChainPower[playerID])
			return calcChainNewPower(engine, playerID, chain);
		else
			return calcChainClassicPower(engine, playerID, chain);
	}

	protected int calcChainNewPower(GameEngine engine, int playerID, int chain) {
		if (chain > CHAIN_POWERS.length)
			return CHAIN_POWERS[CHAIN_POWERS.length-1];
		else
			return CHAIN_POWERS[chain-1];
	}

	protected int calcChainClassicPower(GameEngine engine, int playerID, int chain) {
		if (chain == 2)
			return 8;
		else if (chain == 3)
			return 16;
		else if (chain >= 4)
			return 32*(chain-3);
		else
			return 0;
	}

	protected void onClear(GameEngine engine, int playerID) {
	}

	protected void addOjama(GameEngine engine, int playerID, int pts) {
		int enemyID = 0;
		if(playerID == 0) enemyID = 1;

		int ojamaNew = 0;
		if (zenKeshi[playerID] && zenKeshiType[playerID] == ZENKESHI_MODE_ON)
			ojamaNew += 30;
		//Add ojama
		int rate = ojamaRate[playerID];
		if (hurryupSeconds[playerID] > 0 && engine.statistics.time > hurryupSeconds[playerID])
			rate >>= engine.statistics.time / (hurryupSeconds[playerID] * 60);
		if (rate <= 0)
			rate = 1;
		ojamaNew += ptsToOjama(engine, playerID, pts, rate);
		ojamaSent[playerID] += ojamaNew;

		if (ojamaCounterMode[playerID] != OJAMA_COUNTER_OFF)
		{
			//Counter ojama
			if (ojama[playerID] > 0 && ojamaNew > 0)
			{
				int delta = Math.min(ojama[playerID], ojamaNew);
				ojama[playerID] -= delta;
				ojamaNew -= delta;
			}
			if (ojamaAdd[playerID] > 0 && ojamaNew > 0)
			{
				int delta = Math.min(ojamaAdd[playerID], ojamaNew);
				ojamaAdd[playerID] -= delta;
				ojamaNew -= delta;
			}
		}
		if (ojamaNew > 0)
			ojamaAdd[enemyID] += ojamaNew;
	}

	protected int ptsToOjama(GameEngine engine, int playerID, int pts, int rate)
	{
		return (pts+rate-1)/rate;
	}

	@Override
	public abstract boolean lineClearEnd(GameEngine engine, int playerID);

	/**
	 * Check for game over
	 */
	protected void gameOverCheck(GameEngine engine, int playerID) {
		if (engine.field == null)
			return;
		if (big[playerID])
		{
			if (!engine.field.getBlockEmpty(1, 0))
				engine.stat = GameEngine.STAT_GAMEOVER;
		}
		else if (!engine.field.getBlockEmpty(2, 0) ||
				(dangerColumnDouble[playerID] && !engine.field.getBlockEmpty(3, 0)))
			engine.stat = GameEngine.STAT_GAMEOVER;
	}

	protected void loadFeverMap(GameEngine engine, int playerID, int chain) {
		loadFeverMap(engine, playerID, engine.random, chain,
				engine.random.nextInt(feverMapSubsets[playerID].length));
	}

	protected void loadFeverMap(GameEngine engine, int playerID, Random rand, int chain, int subset) {
		engine.createFieldIfNeeded();
		engine.field.reset();
		engine.field.stringToField(propFeverMap[playerID].getProperty(feverMapSubsets[playerID][subset] +
				"." + numColors[playerID] + "colors." + chain + "chain"));
		engine.field.setBlockLinkByColor();
		engine.field.setAllAttribute(Block.BLOCK_ATTRIBUTE_GARBAGE, false);
		engine.field.setAllAttribute(Block.BLOCK_ATTRIBUTE_ANTIGRAVITY, false);
		engine.field.setAllSkin(engine.getSkin());
		engine.field.shuffleColors(BLOCK_COLORS, numColors[playerID], new Random(rand.nextLong()));
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

		// 決着
		if((playerID == 1) && (owner.engine[0].gameActive)) {
			boolean p1Lose = (owner.engine[0].stat == GameEngine.STAT_GAMEOVER);
			boolean p2Lose = (owner.engine[1].stat == GameEngine.STAT_GAMEOVER);
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

	@Override
	public void pieceLocked (GameEngine engine, int playerID, int clear) {
		cleared[playerID] = false;
		ojamaDrop[playerID] = false;
	}

	protected void updateOjamaMeter (GameEngine engine, int playerID) {
		int width = 6;
		if (engine.field != null)
			width = engine.field.getWidth();
		int blockHeight = receiver.getBlockGraphicsHeight(engine, playerID);
		// せり上がりMeter
		int value = ojama[playerID] * blockHeight / width;
		if(ojama[playerID] >= 5*width) engine.meterColor = GameEngine.METER_COLOR_RED;
		else if(ojama[playerID] >= width) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
		else if(ojama[playerID] >= 1) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
		else engine.meterColor = GameEngine.METER_COLOR_GREEN;
		if (value > engine.meterValue)
			engine.meterValue++;
		else if (value < engine.meterValue)
			engine.meterValue--;
	}

	@Override
	public void renderLast (GameEngine engine, int playerID) {
		if (!owner.engine[playerID].gameActive)
			return;

		int textHeight = 13;
		if (engine.field != null) {
			textHeight = engine.field.getHeight();
			textHeight += 3;
		}
		if(engine.displaysize == 1) textHeight = 11;

		int baseX = (engine.displaysize == 1) ? 1 : -2;

		if (engine.chain > 0 && chainDisplay[playerID] > 0 && chainDisplayType[playerID] != CHAIN_DISPLAY_NONE)
			receiver.drawMenuFont(engine, playerID, baseX + (engine.chain > 9 ? 0 : 1), textHeight,
					engine.chain + " CHAIN!", getChainColor(engine, playerID));
		if(zenKeshi[playerID] || zenKeshiDisplay[playerID] > 0)
			receiver.drawMenuFont(engine, playerID, baseX+1, textHeight+1, "ZENKESHI!", EventReceiver.COLOR_YELLOW);
	}

	protected int getChainColor (GameEngine engine, int playerID) {
		if (chainDisplayType[playerID] == CHAIN_DISPLAY_PLAYER)
			return (playerID == 0) ? EventReceiver.COLOR_RED : EventReceiver.COLOR_BLUE;
		else if (chainDisplayType[playerID] == CHAIN_DISPLAY_SIZE)
			return (engine.chain >= rensaShibari[playerID]) ? EventReceiver.COLOR_GREEN : EventReceiver.COLOR_RED;
		else
			return EventReceiver.COLOR_YELLOW;
	}

	protected void drawX (GameEngine engine, int playerID) {
		if(!dangerColumnShowX[playerID]) return;

		int baseX = big[playerID] ? 1 : 2;

		for(int i = 0; i < ((dangerColumnDouble[playerID] && !big[playerID]) ? 2 : 1); i++) {
			if((engine.field == null) || (engine.field.getBlockEmpty(baseX + i, 0))) {
				if(big[playerID])
					receiver.drawMenuFont(engine, playerID, 2, 0, "e", EventReceiver.COLOR_RED, 2.0f);
				else if(engine.displaysize == 1)
					receiver.drawMenuFont(engine, playerID, 4 + (i * 2), 0, "e", EventReceiver.COLOR_RED, 2.0f);
				else
					receiver.drawMenuFont(engine, playerID, 2 + i, 0, "e", EventReceiver.COLOR_RED);
			}
		}
	}

	protected void drawHardOjama (GameEngine engine, int playerID) {
		if (engine.field != null)
			for (int x = 0; x < engine.field.getWidth(); x++)
				for (int y = 0; y < engine.field.getHeight(); y++)
				{
					int hard = engine.field.getBlock(x, y).hard;
					if (hard > 0) {
						if(engine.displaysize == 1)
							receiver.drawMenuFont(engine, playerID, x * 2, y * 2, String.valueOf(hard), EventReceiver.COLOR_YELLOW, 2.0f);
						else
							receiver.drawMenuFont(engine, playerID, x, y, String.valueOf(hard), EventReceiver.COLOR_YELLOW);
					}
				}
	}

	protected void drawScores (GameEngine engine, int playerID, int x, int y, int headerColor) {
		receiver.drawScoreFont(engine, playerID, x, y, "SCORE", headerColor);
		y++;
		receiver.drawScoreFont(engine, playerID, x, y, "1P: ", EventReceiver.COLOR_RED);
		if (scgettime[0] > 0 && lastscore[0] > 0 && lastmultiplier[0] > 0)
			receiver.drawScoreFont(engine, playerID, x+4, y, "+" + lastscore[0] + "e" + lastmultiplier[0],
					EventReceiver.COLOR_RED);
		else
			receiver.drawScoreFont(engine, playerID, x+4, y, String.valueOf(score[0]), EventReceiver.COLOR_RED);
		y++;
		receiver.drawScoreFont(engine, playerID, x, y, "2P: ", EventReceiver.COLOR_BLUE);
		if (scgettime[1] > 0 && lastscore[1] > 0 && lastmultiplier[1] > 0)
			receiver.drawScoreFont(engine, playerID, x+4, y, "+" + lastscore[1] + "e" + lastmultiplier[1],
					EventReceiver.COLOR_BLUE);
		else
			receiver.drawScoreFont(engine, playerID, x+4, y, String.valueOf(score[1]), EventReceiver.COLOR_BLUE);
	}

	protected void drawOjama (GameEngine engine, int playerID, int x, int y, int headerColor) {
		receiver.drawScoreFont(engine, playerID, x, y, "OJAMA", headerColor);
		String ojamaStr1P = String.valueOf(ojama[0]);
		if (ojamaAdd[0] > 0)
			ojamaStr1P = ojamaStr1P + "(+" + String.valueOf(ojamaAdd[0]) + ")";
		String ojamaStr2P = String.valueOf(ojama[1]);
		if (ojamaAdd[1] > 0)
			ojamaStr2P = ojamaStr2P + "(+" + String.valueOf(ojamaAdd[1]) + ")";
		receiver.drawScoreFont(engine, playerID, x, y+1, "1P:", EventReceiver.COLOR_RED);
		receiver.drawScoreFont(engine, playerID, x+4, y+1, ojamaStr1P, (ojama[0] > 0));
		receiver.drawScoreFont(engine, playerID, x, y+2, "2P:", EventReceiver.COLOR_BLUE);
		receiver.drawScoreFont(engine, playerID, x+4, y+2, ojamaStr2P, (ojama[1] > 0));
	}

	protected void drawAttack (GameEngine engine, int playerID, int x, int y, int headerColor) {
		receiver.drawScoreFont(engine, playerID, x, y, "ATTACK", headerColor);
		receiver.drawScoreFont(engine, playerID, x, y+1, "1P: " + String.valueOf(ojamaSent[0]), EventReceiver.COLOR_RED);
		receiver.drawScoreFont(engine, playerID, x, y+2, "2P: " + String.valueOf(ojamaSent[1]), EventReceiver.COLOR_BLUE);
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

		float apm = (float)(ojamaSent[playerID] * 3600) / (float)(engine.statistics.time);
		drawResult(engine, playerID, receiver, 3, EventReceiver.COLOR_ORANGE,
				"ATTACK", String.format("%10d", ojamaSent[playerID]),
				"CLEARED", String.format("%10d", engine.statistics.lines),
				"MAX CHAIN", String.format("%10d", engine.statistics.maxChain),
				"PIECE", String.format("%10d", engine.statistics.totalPieceLocked),
				"ATTACK/MIN", String.format("%10g", apm),
				"PIECE/SEC", String.format("%10g", engine.statistics.pps),
				"TIME", String.format("%10s", GeneralUtil.getTime(owner.engine[0].statistics.time)));
	}
}
