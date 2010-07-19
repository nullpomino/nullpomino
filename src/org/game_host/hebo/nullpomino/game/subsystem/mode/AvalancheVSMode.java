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
package org.game_host.hebo.nullpomino.game.subsystem.mode;

import java.util.Random;

import org.game_host.hebo.nullpomino.game.component.BGMStatus;
import org.game_host.hebo.nullpomino.game.component.Block;
import org.game_host.hebo.nullpomino.game.component.Controller;
import org.game_host.hebo.nullpomino.game.component.Field;
import org.game_host.hebo.nullpomino.game.component.Piece;
import org.game_host.hebo.nullpomino.game.event.EventReceiver;
import org.game_host.hebo.nullpomino.game.play.GameEngine;
import org.game_host.hebo.nullpomino.game.play.GameManager;
import org.game_host.hebo.nullpomino.util.CustomProperties;
import org.game_host.hebo.nullpomino.util.GeneralUtil;

/**
 * AVALANCHE VS-BATTLE mode (beta)
 */
public class AvalancheVSMode extends DummyMode {
	/** 現在のバージョン */
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

	/** プレイヤーの数 */
	private static final int MAX_PLAYERS = 2;
	
	/** Names of garbage counter setting constants */
	private final int GARBAGE_COUNTER_OFF = 0, GARBAGE_COUNTER_ON = 1, GARBAGE_COUNTER_OFFSET = 2;

	/** 邪魔ブロックタイプの表示名 */
	//private final String[] GARBAGE_TYPE_STRING = {"NORMAL", "ONE RISE", "1-ATTACK"};
	
	/** Names of garbage counter settings */
	private final String[] GARBAGE_COUNTER_STRING = {"OFF", "ON", "OFFSET"};

	/** 各プレイヤーの枠の色 */
	private final int[] PLAYER_COLOR_FRAME = {GameEngine.FRAME_COLOR_RED, GameEngine.FRAME_COLOR_BLUE};

	/** このモードを所有するGameManager */
	private GameManager owner;

	/** 描画などのイベント処理 */
	private EventReceiver receiver;

	/** 邪魔ブロックのタイプ */
	private int[] garbageCounterMode;

	/** 溜まっている邪魔ブロックの数 */
	private int[] garbage;

	/** 送った邪魔ブロックの数 */
	private int[] garbageSent;

	/** 最後にスコア獲得してから経過した時間 */
	private int[] scgettime;

	/** 使用するBGM */
	private int bgmno;

	/** ビッグ */
	private boolean[] big;

	/** 効果音ON/OFF */
	private boolean[] enableSE;

	/** マップ使用フラグ */
	private boolean[] useMap;

	/** 使用するマップセット番号 */
	private int[] mapSet;

	/** マップ番号(-1でランダム) */
	private int[] mapNumber;

	/** 最後に使ったプリセット番号 */
	private int[] presetNumber;

	/** 勝者 */
	private int winnerID;

	/** マップセットのプロパティファイル */
	private CustomProperties[] propMap;

	/** 最大マップ番号 */
	private int[] mapMaxNo;

	/** バックアップ用フィールド（マップをリプレイに保存するときに使用） */
	private Field[] fldBackup;

	/** マップ選択用乱数 */
	private Random randMap;

	/** バージョン */
	private int version;
	
	/** Flag for all clear */
	private boolean[] zenKeshi;

	/** Amount of points earned from most recent clear */
	private int[] lastscore, lastmultiplier;
	
	/** Amount of garbage added in current chain */
	private int[] garbageAdd;
	
	/** Score */
	private int[] score;
	
	/** Max amount of garbage dropped at once */
	private int[] maxAttack;

	/** Number of colors to use */
	private int[] numColors;

	/** Minimum chain count needed to send garbage */
	private int[] rensaShibari;

	/** Denominator for score-to-garbage conversion */
	private int[] garbageRate;

	/*
	 * モード名
	 */
	@Override
	public String getName() {
		return "AVALANCHE VS-BATTLE (BETA)";
	}

	/*
	 * プレイヤー数
	 */
	@Override
	public int getPlayers() {
		return MAX_PLAYERS;
	}

	/*
	 * モードの初期化
	 */
	@Override
	public void modeInit(GameManager manager) {
		owner = manager;
		receiver = owner.receiver;

		garbageCounterMode = new int[MAX_PLAYERS];
		garbage = new int[MAX_PLAYERS];
		garbageSent = new int[MAX_PLAYERS];

		scgettime = new int[MAX_PLAYERS];
		bgmno = 0;
		big = new boolean[MAX_PLAYERS];
		enableSE = new boolean[MAX_PLAYERS];
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
		garbageAdd = new int[MAX_PLAYERS];
		score = new int[MAX_PLAYERS];
		numColors = new int[MAX_PLAYERS];
		maxAttack = new int[MAX_PLAYERS];
		rensaShibari = new int[MAX_PLAYERS];
		garbageRate = new int[MAX_PLAYERS];

		winnerID = -1;
	}

	/**
	 * スピードプリセットを読み込み
	 * @param engine GameEngine
	 * @param prop 読み込み元のプロパティファイル
	 * @param preset プリセット番号
	 */
	private void loadPreset(GameEngine engine, CustomProperties prop, int preset) {
		engine.speed.gravity = prop.getProperty("avalanchevs.gravity." + preset, 4);
		engine.speed.denominator = prop.getProperty("avalanchevs.denominator." + preset, 256);
		engine.speed.are = prop.getProperty("avalanchevs.are." + preset, 24);
		engine.speed.areLine = prop.getProperty("avalanchevs.areLine." + preset, 24);
		engine.speed.lineDelay = prop.getProperty("avalanchevs.lineDelay." + preset, 10);
		engine.speed.lockDelay = prop.getProperty("avalanchevs.lockDelay." + preset, 30);
		engine.speed.das = prop.getProperty("avalanchevs.das." + preset, 14);
	}

	/**
	 * スピードプリセットを保存
	 * @param engine GameEngine
	 * @param prop 保存先のプロパティファイル
	 * @param preset プリセット番号
	 */
	private void savePreset(GameEngine engine, CustomProperties prop, int preset) {
		prop.setProperty("avalanchevs.gravity." + preset, engine.speed.gravity);
		prop.setProperty("avalanchevs.denominator." + preset, engine.speed.denominator);
		prop.setProperty("avalanchevs.are." + preset, engine.speed.are);
		prop.setProperty("avalanchevs.areLine." + preset, engine.speed.areLine);
		prop.setProperty("avalanchevs.lineDelay." + preset, engine.speed.lineDelay);
		prop.setProperty("avalanchevs.lockDelay." + preset, engine.speed.lockDelay);
		prop.setProperty("avalanchevs.das." + preset, engine.speed.das);
	}

	/**
	 * スピード以外の設定を読み込み
	 * @param engine GameEngine
	 * @param prop 読み込み元のプロパティファイル
	 */
	private void loadOtherSetting(GameEngine engine, CustomProperties prop) {
		int playerID = engine.playerID;
		bgmno = prop.getProperty("avalanchevs.bgmno", 0);
		garbageCounterMode[playerID] = prop.getProperty("avalanchevs.garbageCounterMode", GARBAGE_COUNTER_ON);
		big[playerID] = prop.getProperty("avalanchevs.big.p" + playerID, false);
		enableSE[playerID] = prop.getProperty("avalanchevs.enableSE.p" + playerID, true);
		useMap[playerID] = prop.getProperty("avalanchevs.useMap.p" + playerID, false);
		mapSet[playerID] = prop.getProperty("avalanchevs.mapSet.p" + playerID, 0);
		mapNumber[playerID] = prop.getProperty("avalanchevs.mapNumber.p" + playerID, -1);
		presetNumber[playerID] = prop.getProperty("avalanchevs.presetNumber.p" + playerID, 0);
		maxAttack[playerID] = prop.getProperty("avalanchevs.maxAttack.p" + playerID, 30);
		numColors[playerID] = prop.getProperty("avalanchevs.numColors.p" + playerID, 5);
		rensaShibari[playerID] = prop.getProperty("avalanchevs.rensaShibari.p" + playerID, 1);
		garbageRate[playerID] = prop.getProperty("avalanchevs.garbageRate.p" + playerID, 120);
	}

	/**
	 * スピード以外の設定を保存
	 * @param engine GameEngine
	 * @param prop 保存先のプロパティファイル
	 */
	private void saveOtherSetting(GameEngine engine, CustomProperties prop) {
		int playerID = engine.playerID;
		prop.setProperty("avalanchevs.bgmno", bgmno);
		prop.setProperty("avalanchevs.garbageCounterMode", garbageCounterMode[playerID]);
		prop.setProperty("avalanchevs.big.p" + playerID, big[playerID]);
		prop.setProperty("avalanchevs.enableSE.p" + playerID, enableSE[playerID]);
		prop.setProperty("avalanchevs.useMap.p" + playerID, useMap[playerID]);
		prop.setProperty("avalanchevs.mapSet.p" + playerID, mapSet[playerID]);
		prop.setProperty("avalanchevs.mapNumber.p" + playerID, mapNumber[playerID]);
		prop.setProperty("avalanchevs.presetNumber.p" + playerID, presetNumber[playerID]);
		prop.setProperty("avalanchevs.maxAttack.p" + playerID, maxAttack[playerID]);
		prop.setProperty("avalanchevs.numColors.p" + playerID, numColors[playerID]);
		prop.setProperty("avalanchevs.rensaShibari.p" + playerID, rensaShibari[playerID]);
		prop.setProperty("avalanchevs.garbageRate.p" + playerID, garbageRate[playerID]);
	}

	/**
	 * マップ読み込み
	 * @param field フィールド
	 * @param prop 読み込み元のプロパティファイル
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
	 * @param prop 保存先のプロパティファイル
	 * @param id 任意のID
	 */
	private void saveMap(Field field, CustomProperties prop, int id) {
		//field.writeProperty(prop, id);
		prop.setProperty("map." + id, field.fieldToString());
	}

	/**
	 * プレビュー用にマップを読み込み
	 * @param engine GameEngine
	 * @param playerID プレイヤー番号
	 * @param id マップID
	 * @param forceReload trueにするとマップファイルを強制再読み込み
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
	 * 各プレイヤーの初期化
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
		engine.colorClearSize = 4;
		engine.lineGravityType = GameEngine.LINE_GRAVITY_CASCADE;
		for(int i = 0; i < Piece.PIECE_COUNT; i++)
			engine.nextPieceEnable[i] = (PIECE_ENABLE[i] == 1);
		engine.randomBlockColor = true;
		engine.blockColors = BLOCK_COLORS;
		engine.connectBlocks = false;

		garbage[playerID] = 0;
		garbageSent[playerID] = 0;
		scgettime[playerID] = 0;

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
	 * 設定画面の処理
	 */
	@Override
	public boolean onSetting(GameEngine engine, int playerID) {
		// メニュー
		if((engine.owner.replayMode == false) && (engine.statc[4] == 0)) {
			// 上
			if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_UP)) {
				engine.statc[2]--;
				if(engine.statc[2] < 0) engine.statc[2] = 21;
				engine.playSE("cursor");
			}
			// 下
			if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_DOWN)) {
				engine.statc[2]++;
				if(engine.statc[2] > 21) engine.statc[2] = 0;
				engine.playSE("cursor");
			}

			// 設定変更
			int change = 0;
			if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_LEFT)) change = -1;
			if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_RIGHT)) change = 1;

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
				case 8:
					presetNumber[playerID] += change;
					if(presetNumber[playerID] < 0) presetNumber[playerID] = 99;
					if(presetNumber[playerID] > 99) presetNumber[playerID] = 0;
					break;
				case 9:
					garbageCounterMode[playerID] += change;
					if(garbageCounterMode[playerID] < 0) garbageCounterMode[playerID] = 2;
					if(garbageCounterMode[playerID] > 2) garbageCounterMode[playerID] = 0;
					break;
				case 10:
					maxAttack[playerID] += change;
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
					garbageRate[playerID] += change*10;
					if(garbageRate[playerID] < 10) garbageRate[playerID] = 1000;
					if(garbageRate[playerID] > 1000) garbageRate[playerID] = 10;
					break;
				case 14:
					big[playerID] = !big[playerID];
					break;
				case 15:
					enableSE[playerID] = !enableSE[playerID];
					break;
				/*
				case 16:
					hurryupSeconds[playerID] += change;
					if(hurryupSeconds[playerID] < -1) hurryupSeconds[playerID] = 300;
					if(hurryupSeconds[playerID] > 300) hurryupSeconds[playerID] = -1;
					break;
				case 17:
					hurryupInterval[playerID] += change;
					if(hurryupInterval[playerID] < 1) hurryupInterval[playerID] = 99;
					if(hurryupInterval[playerID] > 99) hurryupInterval[playerID] = 1;
					break;
				*/
				case 18:
					bgmno += change;
					if(bgmno < 0) bgmno = BGMStatus.BGM_COUNT - 1;
					if(bgmno > BGMStatus.BGM_COUNT - 1) bgmno = 0;
					break;
				case 19:
					useMap[playerID] = !useMap[playerID];
					if(!useMap[playerID]) {
						if(engine.field != null) engine.field.reset();
					} else {
						loadMapPreview(engine, playerID, (mapNumber[playerID] < 0) ? 0 : mapNumber[playerID], true);
					}
					break;
				case 20:
					mapSet[playerID] += change;
					if(mapSet[playerID] < 0) mapSet[playerID] = 99;
					if(mapSet[playerID] > 99) mapSet[playerID] = 0;
					if(useMap[playerID]) {
						mapNumber[playerID] = -1;
						loadMapPreview(engine, playerID, (mapNumber[playerID] < 0) ? 0 : mapNumber[playerID], true);
					}
					break;
				case 21:
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
			if(engine.ctrl.isPush(Controller.BUTTON_A) && (engine.statc[3] >= 5)) {
				engine.playSE("decide");

				if(engine.statc[2] == 7) {
					loadPreset(engine, owner.modeConfig, presetNumber[playerID]);
				} else if(engine.statc[2] == 8) {
					savePreset(engine, owner.modeConfig, presetNumber[playerID]);
					receiver.saveModeConfig(owner.modeConfig);
				} else {
					saveOtherSetting(engine, owner.modeConfig);
					savePreset(engine, owner.modeConfig, -1 - playerID);
					receiver.saveModeConfig(owner.modeConfig);
					engine.statc[4] = 1;
				}
			}

			// キャンセル
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

			if(engine.statc[3] >= 60) {
				engine.statc[2] = 9;
			}
			if(engine.statc[3] >= 120) {
				engine.statc[4] = 1;
			}
		} else {
			// 開始
			if((owner.engine[0].statc[4] == 1) && (owner.engine[1].statc[4] == 1) && (playerID == 1)) {
				owner.engine[0].stat = GameEngine.STAT_READY;
				owner.engine[1].stat = GameEngine.STAT_READY;
				owner.engine[0].resetStatc();
				owner.engine[1].resetStatc();
			}
			// キャンセル
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
				if(owner.replayMode == false) {
					receiver.drawMenuFont(engine, playerID, 0, (engine.statc[2] * 2) + 1, "b",
										  (playerID == 0) ? EventReceiver.COLOR_RED : EventReceiver.COLOR_BLUE);
				}

				receiver.drawMenuFont(engine, playerID, 0,  0, "GRAVITY", EventReceiver.COLOR_ORANGE);
				receiver.drawMenuFont(engine, playerID, 1,  1, String.valueOf(engine.speed.gravity), (engine.statc[2] == 0));
				receiver.drawMenuFont(engine, playerID, 0,  2, "G-MAX", EventReceiver.COLOR_ORANGE);
				receiver.drawMenuFont(engine, playerID, 1,  3, String.valueOf(engine.speed.denominator), (engine.statc[2] == 1));
				receiver.drawMenuFont(engine, playerID, 0,  4, "ARE", EventReceiver.COLOR_ORANGE);
				receiver.drawMenuFont(engine, playerID, 1,  5, String.valueOf(engine.speed.are), (engine.statc[2] == 2));
				receiver.drawMenuFont(engine, playerID, 0,  6, "ARE LINE", EventReceiver.COLOR_ORANGE);
				receiver.drawMenuFont(engine, playerID, 1,  7, String.valueOf(engine.speed.areLine), (engine.statc[2] == 3));
				receiver.drawMenuFont(engine, playerID, 0,  8, "LINE DELAY", EventReceiver.COLOR_ORANGE);
				receiver.drawMenuFont(engine, playerID, 1,  9, String.valueOf(engine.speed.lineDelay), (engine.statc[2] == 4));
				receiver.drawMenuFont(engine, playerID, 0, 10, "LOCK DELAY", EventReceiver.COLOR_ORANGE);
				receiver.drawMenuFont(engine, playerID, 1, 11, String.valueOf(engine.speed.lockDelay), (engine.statc[2] == 5));
				receiver.drawMenuFont(engine, playerID, 0, 12, "DAS", EventReceiver.COLOR_ORANGE);
				receiver.drawMenuFont(engine, playerID, 1, 13, String.valueOf(engine.speed.das), (engine.statc[2] == 6));
				receiver.drawMenuFont(engine, playerID, 0, 14, "LOAD", EventReceiver.COLOR_GREEN);
				receiver.drawMenuFont(engine, playerID, 1, 15, String.valueOf(presetNumber[playerID]), (engine.statc[2] == 7));
				receiver.drawMenuFont(engine, playerID, 0, 16, "SAVE", EventReceiver.COLOR_GREEN);
				receiver.drawMenuFont(engine, playerID, 1, 17, String.valueOf(presetNumber[playerID]), (engine.statc[2] == 8));
			} else if(engine.statc[2] < 19) {
				if(owner.replayMode == false) {
					receiver.drawMenuFont(engine, playerID, 0, ((engine.statc[2] - 9) * 2) + 1, "b",
										  (playerID == 0) ? EventReceiver.COLOR_RED : EventReceiver.COLOR_BLUE);
				}

				receiver.drawMenuFont(engine, playerID, 0,  0, "COUNTER", EventReceiver.COLOR_CYAN);
				receiver.drawMenuFont(engine, playerID, 1,  1, GARBAGE_COUNTER_STRING[garbageCounterMode[playerID]], (engine.statc[2] == 9));
				receiver.drawMenuFont(engine, playerID, 0,  2, "MAX ATTACK", EventReceiver.COLOR_CYAN);
				receiver.drawMenuFont(engine, playerID, 1,  3, String.valueOf(maxAttack[playerID]), (engine.statc[2] == 10));
				receiver.drawMenuFont(engine, playerID, 0,  4, "COLORS", EventReceiver.COLOR_CYAN);
				receiver.drawMenuFont(engine, playerID, 1,  5, String.valueOf(numColors[playerID]), (engine.statc[2] == 11));
				receiver.drawMenuFont(engine, playerID, 0,  6, "MIN CHAIN", EventReceiver.COLOR_CYAN);
				receiver.drawMenuFont(engine, playerID, 1,  7, String.valueOf(rensaShibari[playerID]), (engine.statc[2] == 12));
				receiver.drawMenuFont(engine, playerID, 0,  8, "OJAMA RATE", EventReceiver.COLOR_CYAN);
				receiver.drawMenuFont(engine, playerID, 1,  9, String.valueOf(garbageRate[playerID]), (engine.statc[2] == 13));
				receiver.drawMenuFont(engine, playerID, 0, 10, "BIG", EventReceiver.COLOR_CYAN);
				receiver.drawMenuFont(engine, playerID, 1, 11, GeneralUtil.getONorOFF(big[playerID]), (engine.statc[2] == 14));
				receiver.drawMenuFont(engine, playerID, 0, 12, "SE", EventReceiver.COLOR_CYAN);
				receiver.drawMenuFont(engine, playerID, 1, 13, GeneralUtil.getONorOFF(enableSE[playerID]), (engine.statc[2] == 15));
				/*
				receiver.drawMenuFont(engine, playerID, 0, 14, "HURRYUP", EventReceiver.COLOR_CYAN);
				receiver.drawMenuFont(engine, playerID, 1, 15, (hurryupSeconds[playerID] == -1) ? "NONE" : hurryupSeconds[playerID]+"SEC",
				                      (engine.statc[2] == 16));
				receiver.drawMenuFont(engine, playerID, 0, 16, "INTERVAL", EventReceiver.COLOR_CYAN);
				receiver.drawMenuFont(engine, playerID, 1, 17, String.valueOf(hurryupInterval[playerID]), (engine.statc[2] == 17));
				*/
				receiver.drawMenuFont(engine, playerID, 0, 18, "BGM", EventReceiver.COLOR_PINK);
				receiver.drawMenuFont(engine, playerID, 1, 19, String.valueOf(bgmno), (engine.statc[2] == 18));
			} else {
				if(owner.replayMode == false) {
					receiver.drawMenuFont(engine, playerID, 0, ((engine.statc[2] - 19) * 2) + 1, "b",
										  (playerID == 0) ? EventReceiver.COLOR_RED : EventReceiver.COLOR_BLUE);
				}

				receiver.drawMenuFont(engine, playerID, 0,  0, "USE MAP", EventReceiver.COLOR_CYAN);
				receiver.drawMenuFont(engine, playerID, 1,  1, GeneralUtil.getONorOFF(useMap[playerID]), (engine.statc[2] == 19));
				receiver.drawMenuFont(engine, playerID, 0,  2, "MAP SET", EventReceiver.COLOR_CYAN);
				receiver.drawMenuFont(engine, playerID, 1,  3, String.valueOf(mapSet[playerID]), (engine.statc[2] == 20));
				receiver.drawMenuFont(engine, playerID, 0,  4, "MAP NO.", EventReceiver.COLOR_CYAN);
				receiver.drawMenuFont(engine, playerID, 1,  5, (mapNumber[playerID] < 0) ? "RANDOM" : mapNumber[playerID]+"/"+(mapMaxNo[playerID]-1),
									  (engine.statc[2] == 21));
			}
		} else {
			receiver.drawMenuFont(engine, playerID, 3, 10, "WAIT", EventReceiver.COLOR_YELLOW);
		}
	}

	/*
	 * Readyの時の初期化処理（初期化前）
	 */
	@Override
	public boolean onReady(GameEngine engine, int playerID) {
		if(engine.statc[0] == 0) {
			engine.numColors = numColors[playerID];
			// マップ読み込み・リプレイ保存用にバックアップ
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

		engine.tspinAllowKick = false;
		engine.tspinEnable = false;
		engine.useAllSpinBonus = false;
	}

	/*
	 * スコア表示
	 */
	@Override
	public void renderLast(GameEngine engine, int playerID) {
		// ステータス表示
		if(playerID == 0) {
			receiver.drawScoreFont(engine, playerID, -1, 0, "AVALANCHE VS", EventReceiver.COLOR_GREEN);

			receiver.drawScoreFont(engine, playerID, -1, 2, "GARBAGE", EventReceiver.COLOR_PURPLE);
			String garbageStr1P = String.valueOf(garbage[0]);
			if (garbageAdd[0] > 0)
				garbageStr1P = garbageStr1P + "(+" + String.valueOf(garbageAdd[0]) + ")";
			String garbageStr2P = String.valueOf(garbage[1]);
			if (garbageAdd[1] > 0)
				garbageStr2P = garbageStr2P + "(+" + String.valueOf(garbageAdd[1]) + ")";
			receiver.drawScoreFont(engine, playerID, -1, 3, "1P:", EventReceiver.COLOR_RED);
			receiver.drawScoreFont(engine, playerID, 3, 3, garbageStr1P, (garbage[0] > 0));
			receiver.drawScoreFont(engine, playerID, -1, 4, "2P:", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 3, 4, garbageStr2P, (garbage[1] > 0));

			receiver.drawScoreFont(engine, playerID, -1, 6, "ATTACK", EventReceiver.COLOR_GREEN);
			receiver.drawScoreFont(engine, playerID, -1, 7, "1P: " + String.valueOf(garbageSent[0]), EventReceiver.COLOR_RED);
			receiver.drawScoreFont(engine, playerID, -1, 8, "2P: " + String.valueOf(garbageSent[1]), EventReceiver.COLOR_BLUE);

			receiver.drawScoreFont(engine, playerID, -1, 10, "SCORE", EventReceiver.COLOR_PURPLE);
			receiver.drawScoreFont(engine, playerID, -1, 11, "1P: " + String.valueOf(score[0]), EventReceiver.COLOR_RED);
			receiver.drawScoreFont(engine, playerID, -1, 12, "2P: " + String.valueOf(score[1]), EventReceiver.COLOR_BLUE);

			receiver.drawScoreFont(engine, playerID, -1, 14, "TIME", EventReceiver.COLOR_GREEN);
			receiver.drawScoreFont(engine, playerID, -1, 15, GeneralUtil.getTime(engine.statistics.time));
		}

		// ライン消去イベント表示
		if(zenKeshi[playerID])
			receiver.drawMenuFont(engine, playerID, 1, 21, "ZENKESHI!", EventReceiver.COLOR_YELLOW);
	}

	/*
	 * スコア計算
	 */
	@Override
	public void calcScore(GameEngine engine, int playerID, int avalanche) {
		int enemyID = 0;
		if(playerID == 0) enemyID = 1;
		
		// ラインクリアボーナス
		int pts = avalanche*10;
		int garbageNew = 0;
		boolean cleared = avalanche > 0;
		if (cleared) {
			if (zenKeshi[playerID])
				garbageNew += 30;
			if (engine.field.isEmpty()) {
				engine.playSE("bravo");
				zenKeshi[playerID] = true;
				engine.statistics.score += 2100;
			}
			else
				zenKeshi[playerID] = false;

			int chain = engine.chain;
			int multiplier = engine.field.colorClearExtraCount;
			if (engine.field.colorsCleared > 1)
				multiplier += (engine.field.colorsCleared-1)*2;
			/*
			if (multiplier < 0)
				multiplier = 0;
			if (chain == 0)
				firstExtra = avalanche > engine.colorClearSize;
			*/
			if (chain == 2)
				multiplier += 8;
			else if (chain == 3)
				multiplier += 16;
			else if (chain >= 4)
				multiplier += 32*(chain-3);
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
			scgettime[playerID] = 120;
			int ptsTotal = pts*multiplier;
			score[playerID] += ptsTotal;

			garbageNew += (ptsTotal+garbageRate[playerID]-1)/garbageRate[playerID];
			if (chain >= rensaShibari[playerID])
			{
				garbageSent[playerID] += garbageNew;
				if (garbageCounterMode[playerID] != GARBAGE_COUNTER_OFF)
				{
					if (garbage[playerID] > 0)
					{
						int delta = Math.min(garbage[playerID], garbageNew);
						garbage[playerID] -= delta;
						garbageNew -= delta;
					}
					if (garbageAdd[playerID] > 0 && garbageNew > 0)
					{
						int delta = Math.min(garbageAdd[playerID], garbageNew);
						garbageAdd[playerID] -= delta;
						garbageNew -= delta;
					}
				}
				if (garbageNew > 0)
					garbageAdd[enemyID] += garbageNew;
			}
		}
		if ((!cleared || garbageCounterMode[playerID] != GARBAGE_COUNTER_OFFSET)
				&& !engine.field.canCascade() && garbage[playerID] > 0)
		{
			int drop = Math.min(garbage[playerID], maxAttack[playerID]);
			garbage[playerID] -= drop;
			engine.field.garbageDrop(engine, drop);
		}
	}

	public boolean lineClearEnd(GameEngine engine, int playerID) {
		int enemyID = 0;
		if(playerID == 0) enemyID = 1;
		if (garbageAdd[enemyID] > 0)
		{
			garbage[enemyID] += garbageAdd[enemyID];
			garbageAdd[enemyID] = 0;
		}
		if (garbageCounterMode[playerID] != GARBAGE_COUNTER_OFFSET && garbage[playerID] > 0)
		{
			int drop = Math.min(garbage[playerID], maxAttack[playerID]);
			garbage[playerID] -= drop;
			engine.field.garbageDrop(engine, drop);
			return true;
		}
		return false;
	}

	/*
	 * 各フレームの最後の処理
	 */
	@Override
	public void onLast(GameEngine engine, int playerID) {
		scgettime[playerID]++;

		int width = 1;
		if (engine.field != null)
			width = engine.field.getWidth();
		int blockHeight = receiver.getBlockGraphicsHeight(engine, playerID);
		// せり上がりメーター
		if(garbage[playerID] * blockHeight / width > engine.meterValue) {
			engine.meterValue += blockHeight / 2;
		} else if(garbage[playerID] * blockHeight / width < engine.meterValue) {
			engine.meterValue--;
		}
		if(garbage[playerID] >= 6*width) engine.meterColor = GameEngine.METER_COLOR_RED;
		else if(garbage[playerID] >= width) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
		else if(garbage[playerID] >= 1) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
		else engine.meterColor = GameEngine.METER_COLOR_GREEN;

		// 決着
		if((playerID == 1) && (owner.engine[0].gameActive)) {
			if((owner.engine[0].stat == GameEngine.STAT_GAMEOVER) && (owner.engine[1].stat == GameEngine.STAT_GAMEOVER)) {
				// 引き分け
				winnerID = -1;
				owner.engine[0].gameActive = false;
				owner.engine[1].gameActive = false;
				owner.bgmStatus.bgm = BGMStatus.BGM_NOTHING;
			} else if((owner.engine[0].stat != GameEngine.STAT_GAMEOVER) && (owner.engine[1].stat == GameEngine.STAT_GAMEOVER)) {
				// 1P勝利
				winnerID = 0;
				owner.engine[0].gameActive = false;
				owner.engine[1].gameActive = false;
				owner.engine[0].stat = GameEngine.STAT_EXCELLENT;
				owner.engine[0].resetStatc();
				owner.engine[0].statc[1] = 1;
				owner.bgmStatus.bgm = BGMStatus.BGM_NOTHING;
			} else if((owner.engine[0].stat == GameEngine.STAT_GAMEOVER) && (owner.engine[1].stat != GameEngine.STAT_GAMEOVER)) {
				// 2P勝利
				winnerID = 1;
				owner.engine[0].gameActive = false;
				owner.engine[1].gameActive = false;
				owner.engine[1].stat = GameEngine.STAT_EXCELLENT;
				owner.engine[1].resetStatc();
				owner.engine[1].statc[1] = 1;
				owner.bgmStatus.bgm = BGMStatus.BGM_NOTHING;
			}
		}
	}

	/*
	 * 結果画面の描画
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
		String strScore = String.format("%10d", garbageSent[playerID]);
		receiver.drawMenuFont(engine, playerID, 0, 4, strScore);

		receiver.drawMenuFont(engine, playerID, 0, 5, "LINE", EventReceiver.COLOR_ORANGE);
		String strLines = String.format("%10d", engine.statistics.lines);
		receiver.drawMenuFont(engine, playerID, 0, 6, strLines);

		receiver.drawMenuFont(engine, playerID, 0, 7, "PIECE", EventReceiver.COLOR_ORANGE);
		String strPiece = String.format("%10d", engine.statistics.totalPieceLocked);
		receiver.drawMenuFont(engine, playerID, 0, 8, strPiece);

		receiver.drawMenuFont(engine, playerID, 0, 9, "ATTACK/MIN", EventReceiver.COLOR_ORANGE);
		float apm = (float)(garbageSent[playerID] * 3600) / (float)(engine.statistics.time);
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
	 * リプレイ保存時の処理
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
