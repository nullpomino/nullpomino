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
import mu.nu.nullpo.game.component.Piece;
import mu.nu.nullpo.game.event.EventReceiver;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.util.CustomProperties;
import mu.nu.nullpo.util.GeneralUtil;

/**
 * AVALANCHE VS DIG RACE mode (Release Candidate 1)
 */
public class AvalancheVSDigRaceMode extends DummyMode {
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

	/** Number of players */
	private static final int MAX_PLAYERS = 2;

	/** Ojama counter setting constants */
	private static final int OJAMA_COUNTER_OFF = 0, OJAMA_COUNTER_ON = 1, OJAMA_COUNTER_FEVER = 2;

	/** Names of ojama counter settings */
	private static final String[] OJAMA_COUNTER_STRING = {"OFF", "ON", "FEVER"};

	/** Names of outline settings */
	private static final String[] OUTLINE_TYPE_NAMES = {"NORMAL", "COLOR", "NONE"};

	/** Names of chain display settings */
	private static final String[] CHAIN_DISPLAY_NAMES = {"OFF", "YELLOW", "PLAYER", "SIZE"};

	/** Constants for chain display settings */
	private static final int CHAIN_DISPLAY_NONE = 0, /*CHAIN_DISPLAY_YELLOW = 1,*/
		CHAIN_DISPLAY_PLAYER = 2, CHAIN_DISPLAY_SIZE = 3;

	/** Each player's frame color */
	private static final int[] PLAYER_COLOR_FRAME = {GameEngine.FRAME_COLOR_RED, GameEngine.FRAME_COLOR_BLUE};

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

	/** 効果音ON/OFF */
	private boolean[] enableSE;

	/** Last preset number used */
	private int[] presetNumber;

	/** 勝者 */
	private int winnerID;

	/** Version */
	private int version;

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

	/** Set to true when last drop resulted in a clear */
	private boolean[] cleared;

	/** Set to true when dropping ojama blocks */
	private boolean[] ojamaDrop;

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

	/** Ojama handicap to start with */
	private int[] handicapRows;

	/*
	 * Mode  name
	 */
	@Override
	public String getName() {
		return "AVALANCHE VS DIG RACE (RC1)";
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
		enableSE = new boolean[MAX_PLAYERS];
		hurryupSeconds = new int[MAX_PLAYERS];
		presetNumber = new int[MAX_PLAYERS];

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
		outlineType = new int[MAX_PLAYERS];
		dangerColumnDouble = new boolean[MAX_PLAYERS];
		dangerColumnShowX = new boolean[MAX_PLAYERS];
		chain = new int[MAX_PLAYERS];
		chainDisplay = new int[MAX_PLAYERS];
		chainDisplayType = new int[MAX_PLAYERS];
		handicapRows = new int[MAX_PLAYERS];

		winnerID = -1;
	}

	/**
	 * Read speed presets
	 * @param engine GameEngine
	 * @param prop Property file to read from
	 * @param preset Preset number
	 */
	private void loadPreset(GameEngine engine, CustomProperties prop, int preset) {
		engine.speed.gravity = prop.getProperty("avalanchevsdigrace.gravity." + preset, 4);
		engine.speed.denominator = prop.getProperty("avalanchevsdigrace.denominator." + preset, 256);
		engine.speed.are = prop.getProperty("avalanchevsdigrace.are." + preset, 24);
		engine.speed.areLine = prop.getProperty("avalanchevsdigrace.areLine." + preset, 24);
		engine.speed.lineDelay = prop.getProperty("avalanchevsdigrace.lineDelay." + preset, 10);
		engine.speed.lockDelay = prop.getProperty("avalanchevsdigrace.lockDelay." + preset, 60);
		engine.speed.das = prop.getProperty("avalanchevsdigrace.das." + preset, 14);
		engine.cascadeDelay = prop.getProperty("avalanchevsdigrace.fallDelay." + preset, 1);
		engine.cascadeClearDelay = prop.getProperty("avalanchevsdigrace.clearDelay." + preset, 10);
	}

	/**
	 * Save speed presets
	 * @param engine GameEngine
	 * @param prop Property file to save to
	 * @param preset Preset number
	 */
	private void savePreset(GameEngine engine, CustomProperties prop, int preset) {
		prop.setProperty("avalanchevsdigrace.gravity." + preset, engine.speed.gravity);
		prop.setProperty("avalanchevsdigrace.denominator." + preset, engine.speed.denominator);
		prop.setProperty("avalanchevsdigrace.are." + preset, engine.speed.are);
		prop.setProperty("avalanchevsdigrace.areLine." + preset, engine.speed.areLine);
		prop.setProperty("avalanchevsdigrace.lineDelay." + preset, engine.speed.lineDelay);
		prop.setProperty("avalanchevsdigrace.lockDelay." + preset, engine.speed.lockDelay);
		prop.setProperty("avalanchevsdigrace.das." + preset, engine.speed.das);
		prop.setProperty("avalanchevsdigrace.fallDelay." + preset, engine.cascadeDelay);
		prop.setProperty("avalanchevsdigrace.clearDelay." + preset, engine.cascadeClearDelay);
	}

	/**
	 * スピード以外の設定を読み込み
	 * @param engine GameEngine
	 * @param prop Property file to read from
	 */
	private void loadOtherSetting(GameEngine engine, CustomProperties prop) {
		int playerID = engine.playerID;
		bgmno = prop.getProperty("avalanchevsdigrace.bgmno", 0);
		enableSE[playerID] = prop.getProperty("avalanchevsdigrace.enableSE.p" + playerID, true);
		hurryupSeconds[playerID] = prop.getProperty("avalanchevsdigrace.hurryupSeconds.p" + playerID, 192);
		presetNumber[playerID] = prop.getProperty("avalanchevsdigrace.presetNumber.p" + playerID, 0);
		maxAttack[playerID] = prop.getProperty("avalanchevsdigrace.maxAttack.p" + playerID, 30);
		numColors[playerID] = prop.getProperty("avalanchevsdigrace.numColors.p" + playerID, 4);
		rensaShibari[playerID] = prop.getProperty("avalanchevsdigrace.rensaShibari.p" + playerID, 1);
		ojamaRate[playerID] = prop.getProperty("avalanchevsdigrace.ojamaRate.p" + playerID, 420);
		ojamaHard[playerID] = prop.getProperty("avalanchevsdigrace.ojamaHard.p" + playerID, 0);
		outlineType[playerID] = prop.getProperty("avalanchevsdigrace.outlineType.p" + playerID, 1);
		dangerColumnDouble[playerID] = prop.getProperty("avalanchevsdigrace.dangerColumnDouble.p" + playerID, false);
		dangerColumnShowX[playerID] = prop.getProperty("avalanchevsdigrace.dangerColumnShowX.p" + playerID, false);
		chainDisplayType[playerID] = prop.getProperty("avalanchevsdigrace.chainDisplayType.p" + playerID, 1);
		handicapRows[playerID] = prop.getProperty("avalanchevsdigrace.ojamaHandicap.p" + playerID, 6);
	}

	/**
	 * スピード以外の設定を保存
	 * @param engine GameEngine
	 * @param prop Property file to save to
	 */
	private void saveOtherSetting(GameEngine engine, CustomProperties prop) {
		int playerID = engine.playerID;
		prop.setProperty("avalanchevsdigrace.bgmno", bgmno);
		prop.setProperty("avalanchevsdigrace.enableSE.p" + playerID, enableSE[playerID]);
		prop.setProperty("avalanchevsdigrace.hurryupSeconds.p" + playerID, hurryupSeconds[playerID]);
		prop.setProperty("avalanchevsdigrace.ojamaCounterMode", ojamaCounterMode[playerID]);
		prop.setProperty("avalanchevsdigrace.presetNumber.p" + playerID, presetNumber[playerID]);
		prop.setProperty("avalanchevsdigrace.maxAttack.p" + playerID, maxAttack[playerID]);
		prop.setProperty("avalanchevsdigrace.numColors.p" + playerID, numColors[playerID]);
		prop.setProperty("avalanchevsdigrace.rensaShibari.p" + playerID, rensaShibari[playerID]);
		prop.setProperty("avalanchevsdigrace.ojamaRate.p" + playerID, ojamaRate[playerID]);
		prop.setProperty("avalanchevsdigrace.ojamaHard.p" + playerID, ojamaHard[playerID]);
		prop.setProperty("avalanchevsdigrace.outlineType.p" + playerID, outlineType[playerID]);
		prop.setProperty("avalanchevsdigrace.dangerColumnDouble.p" + playerID, dangerColumnDouble[playerID]);
		prop.setProperty("avalanchevsdigrace.dangerColumnShowX.p" + playerID, dangerColumnShowX[playerID]);
		prop.setProperty("avalanchevsdigrace.ojamaHandicap.p" + playerID, handicapRows[playerID]);
		prop.setProperty("avalanchevsdigrace.chainDisplayType.p" + playerID, chainDisplayType[playerID]);
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
		ojamaSent[playerID] = 0;
		score[playerID] = 0;
		scgettime[playerID] = 0;
		cleared[playerID] = false;
		ojamaDrop[playerID] = false;
		chain[playerID] = 0;
		chainDisplay[playerID] = 0;

		if(engine.owner.replayMode == false) {
			loadOtherSetting(engine, engine.owner.modeConfig);
			loadPreset(engine, engine.owner.modeConfig, -1 - playerID);
			version = CURRENT_VERSION;
		} else {
			loadOtherSetting(engine, engine.owner.replayProp);
			loadPreset(engine, engine.owner.replayProp, -1 - playerID);
			version = owner.replayProp.getProperty("avalanchevsdigrace.version", 0);
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
			int change = updateCursor(engine, 24);

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
					handicapRows[playerID] += change;
					if(handicapRows[playerID] < 0) handicapRows[playerID] = 11;
					if(handicapRows[playerID] > 11) handicapRows[playerID] = 0;
					break;
				case 19:
					outlineType[playerID] += change;
					if(outlineType[playerID] < 0) outlineType[playerID] = 2;
					if(outlineType[playerID] > 2) outlineType[playerID] = 0;
					break;
				case 20:
					chainDisplayType[playerID] += change;
					if(chainDisplayType[playerID] < 0) chainDisplayType[playerID] = 3;
					if(chainDisplayType[playerID] > 3) chainDisplayType[playerID] = 0;
					break;
				case 21:
					bgmno += change;
					if(bgmno < 0) bgmno = BGMStatus.BGM_COUNT - 1;
					if(bgmno > BGMStatus.BGM_COUNT - 1) bgmno = 0;
					break;
				case 22:
					enableSE[playerID] = !enableSE[playerID];
					break;
				case 23:
				case 24:
					presetNumber[playerID] += change;
					if(presetNumber[playerID] < 0) presetNumber[playerID] = 99;
					if(presetNumber[playerID] > 99) presetNumber[playerID] = 0;
					break;
				}
			}

			// 決定
			if(engine.ctrl.isPush(Controller.BUTTON_A) && (engine.statc[3] >= 5)) {
				engine.playSE("decide");

				if(engine.statc[2] == 22) {
					loadPreset(engine, owner.modeConfig, presetNumber[playerID]);
				} else if(engine.statc[2] == 23) {
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
			engine.statc[3]++;
		} else if(engine.statc[4] == 0) {
			engine.statc[3]++;
			engine.statc[2] = 0;

			if(engine.statc[3] >= 180)
				engine.statc[4] = 1;
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

				receiver.drawMenuFont(engine, playerID, 0, 19, "PAGE 1/3", EventReceiver.COLOR_YELLOW);
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

				receiver.drawMenuFont(engine, playerID, 0, 19, "PAGE 2/3", EventReceiver.COLOR_YELLOW);
			} else {
				drawMenu(engine, playerID, receiver, 0, EventReceiver.COLOR_PURPLE, 18,
						"ROWS", String.valueOf(handicapRows[playerID]));
				drawMenu(engine, playerID, receiver, 2, EventReceiver.COLOR_DARKBLUE, 19,
						"OUTLINE", OUTLINE_TYPE_NAMES[outlineType[playerID]],
						"SHOW CHAIN", CHAIN_DISPLAY_NAMES[chainDisplayType[playerID]]);
				drawMenu(engine, playerID, receiver, 6, EventReceiver.COLOR_PINK, 21,
						"BGM", String.valueOf(bgmno),
						"SE", GeneralUtil.getONorOFF(enableSE[playerID]));
				drawMenu(engine, playerID, receiver, 10, EventReceiver.COLOR_GREEN, 23,
						"LOAD", String.valueOf(presetNumber[playerID]),
						"SAVE", String.valueOf(presetNumber[playerID]));

				receiver.drawMenuFont(engine, playerID, 0, 19, "PAGE 3/3", EventReceiver.COLOR_YELLOW);
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
			engine.rainbowAnimate = true;

			if(outlineType[playerID] == 0) engine.blockOutlineType = GameEngine.BLOCK_OUTLINE_NORMAL;
			if(outlineType[playerID] == 1) engine.blockOutlineType = GameEngine.BLOCK_OUTLINE_SAMECOLOR;
			if(outlineType[playerID] == 2) engine.blockOutlineType = GameEngine.BLOCK_OUTLINE_NONE;

			if(engine.field != null)
				engine.field.reset();
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
		engine.big = false;
		engine.enableSE = enableSE[playerID];
		if(playerID == 1) owner.bgmStatus.bgm = bgmno;
		engine.colorClearSize = 4;
		engine.ignoreHidden = true;

		engine.tspinAllowKick = false;
		engine.tspinEnable = false;
		engine.useAllSpinBonus = false;

		engine.createFieldIfNeeded();
		int y = engine.field.getHeight()-1;
		Random rand = new Random(engine.random.nextLong());
		int width = engine.field.getWidth();
		int x = rand.nextInt(width);
		engine.field.garbageDropPlace(x, y, false, 0);
		engine.field.setBlockColor(x, y, Block.BLOCK_COLOR_GEM_RAINBOW);
		engine.field.garbageDropPlace(x, y-1, false, 1);
		if (x > 0)
		{
			engine.field.garbageDropPlace(x-1, y, false, 1);
			engine.field.garbageDropPlace(x-1, y-1, false, 1);
		}
		if (x < width-1)
		{
			engine.field.garbageDropPlace(x+1, y, false, 1);
			engine.field.garbageDropPlace(x+1, y-1, false, 1);
		}
		do {
			for (int i = 0; i < handicapRows[playerID]; i++)
				for (int j = 0; j < width; j++)
					if (engine.field.getBlockEmpty(j, y-i))
						engine.field.setBlockColor(j, y-i, BLOCK_COLORS[rand.nextInt(numColors[playerID])]);
		} while (engine.field.clearColor(3, false, false, true) > 0);
		engine.field.setAllAttribute(Block.BLOCK_ATTRIBUTE_VISIBLE, true);
		engine.field.setAllSkin(engine.getSkin());
	}

	/*
	 * Render score
	 */
	@Override
	public void renderLast(GameEngine engine, int playerID) {
		// ステータス表示
		if(playerID == 0) {
			receiver.drawScoreFont(engine, playerID, -1,  0, "AVALANCHE VS", EventReceiver.COLOR_GREEN);

			receiver.drawScoreFont(engine, playerID, -1,  2, "OJAMA", EventReceiver.COLOR_PURPLE);
			String ojamaFeverStr1P = String.valueOf(ojama[0]);
			if (ojamaAdd[0] > 0)
				ojamaFeverStr1P = ojamaFeverStr1P + "(+" + String.valueOf(ojamaAdd[0]) + ")";
			String ojamaFeverStr2P = String.valueOf(ojama[1]);
			if (ojamaAdd[1] > 0)
				ojamaFeverStr2P = ojamaFeverStr2P + "(+" + String.valueOf(ojamaAdd[1]) + ")";
			receiver.drawScoreFont(engine, playerID, -1,  3, "1P:", EventReceiver.COLOR_RED);
			receiver.drawScoreFont(engine, playerID,  3,  3, ojamaFeverStr1P, (ojama[0] > 0));
			receiver.drawScoreFont(engine, playerID, -1,  4, "2P:", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID,  3,  4, ojamaFeverStr2P, (ojama[1] > 0));

			receiver.drawScoreFont(engine, playerID, -1,  6, "ATTACK", EventReceiver.COLOR_GREEN);
			receiver.drawScoreFont(engine, playerID, -1,  7, "1P: " + String.valueOf(ojamaSent[0]), EventReceiver.COLOR_RED);
			receiver.drawScoreFont(engine, playerID, -1,  8, "2P: " + String.valueOf(ojamaSent[1]), EventReceiver.COLOR_BLUE);

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
		}

		if (!owner.engine[playerID].gameActive)
			return;
		int playerColor = (playerID == 0) ? EventReceiver.COLOR_RED : EventReceiver.COLOR_BLUE;
		if (dangerColumnShowX[playerID])
			receiver.drawMenuFont(engine, playerID, 2, 0, dangerColumnDouble[playerID] ? "XX" : "X", EventReceiver.COLOR_RED);
		if (engine.field != null)
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

		// Line clear bonus
		int pts = avalanche*10;
		int ojamaNew = 0;
		if (avalanche > 0) {
			cleared[playerID] = true;

			chain[playerID] = engine.chain;
			chainDisplay[playerID] = 60;
			engine.playSE("combo" + Math.min(chain[playerID], 20));
			int multiplier = engine.field.colorClearExtraCount;
			if (engine.field.colorsCleared > 1)
				multiplier += (engine.field.colorsCleared-1)*2;
			/*
			if (multiplier < 0)
				multiplier = 0;
			if (chain == 0)
				firstExtra = avalanche > engine.colorClearSize;
			*/
			if (chain[playerID] == 2)
				multiplier += 8;
			else if (chain[playerID] == 3)
				multiplier += 16;
			else if (chain[playerID] >= 4)
				multiplier += 32*(chain[playerID]-3);
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

			if (chain[playerID] >= rensaShibari[playerID])
			{
				//Add ojama
				int rate = ojamaRate[playerID];
				if (hurryupSeconds[playerID] > 0 && engine.statistics.time > hurryupSeconds[playerID])
					rate >>= engine.statistics.time / (hurryupSeconds[playerID] * 60);
				if (rate <= 0)
					rate = 1;
				ojamaNew += (ptsTotal+rate-1)/rate;
				ojamaSent[playerID] += ojamaNew;

				//Counter ojama
				if (ojamaCounterMode[playerID] != OJAMA_COUNTER_OFF)
				{
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
		}
		else if (!engine.field.canCascade())
			cleared[playerID] = false;
	}

	public boolean lineClearEnd(GameEngine engine, int playerID) {
		int enemyID = 0;
		if(playerID == 0) enemyID = 1;
		if (ojamaAdd[enemyID] > 0)
		{
			ojama[enemyID] += ojamaAdd[enemyID];
			ojamaAdd[enemyID] = 0;
		}
		//Drop garbage if needed.
		if (ojama[playerID] > 0 && !ojamaDrop[playerID] && (!cleared[playerID] ||
				(ojamaCounterMode[playerID] != OJAMA_COUNTER_FEVER)))
		{
			ojamaDrop[playerID] = true;
			int drop = Math.min(ojama[playerID], maxAttack[playerID]);
			ojama[playerID] -= drop;
			engine.field.garbageDrop(engine, drop, false, ojamaHard[playerID]);
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
		return false;
	}

	/*
	 * Called after every frame
	 */
	@Override
	public void onLast(GameEngine engine, int playerID) {
		if (scgettime[playerID] > 0)
			scgettime[playerID]--;
		if (chainDisplay[playerID] > 0)
			chainDisplay[playerID]--;

		int width = 1;
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

		// 決着
		if((playerID == 1) && (owner.engine[0].gameActive)) {
			boolean p1Lose = (owner.engine[0].stat == GameEngine.STAT_GAMEOVER);
			if (!p1Lose && owner.engine[1].field != null && owner.engine[1].stat != GameEngine.STAT_READY)
				p1Lose = (owner.engine[1].field.getHowManyGems() == 0);
			boolean p2Lose = (owner.engine[1].stat == GameEngine.STAT_GAMEOVER);
			if (!p2Lose && owner.engine[0].field != null && owner.engine[0].stat != GameEngine.STAT_READY)
				p2Lose = (owner.engine[0].field.getHowManyGems() == 0);
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

		owner.replayProp.setProperty("avalanchevsdigrace.version", version);
	}
}
