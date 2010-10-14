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
import mu.nu.nullpo.game.component.Controller;
import mu.nu.nullpo.game.component.Field;
import mu.nu.nullpo.game.event.EventReceiver;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.util.CustomProperties;
import mu.nu.nullpo.util.GeneralUtil;

/**
 * AVALANCHE VS-BATTLE mode (Release Candidate 1)
 */
public class AvalancheVSMode extends AvalancheVSDummyMode {
	/** Current version */
	private static final int CURRENT_VERSION = 0;

	/** Chain multipliers in Fever */
	private static final int[] FEVER_POWERS = {
		4, 10, 18, 21, 29, 46, 76, 113, 150, 223, 259, 266, 313, 364, 398, 432, 468, 504, 540, 576, 612, 648, 684, 720 //Arle
	};

	/** Names of fever point criteria settings */
	private static final String[] FEVER_POINT_CRITERIA_NAMES = {"COUNTER", "CLEAR", "BOTH"};

	/** Constants for fever point criteria settings */
	private static final int FEVER_POINT_CRITERIA_COUNTER = 0, FEVER_POINT_CRITERIA_CLEAR = 1
			/*,FEVER_POINT_CRITERIA_BOTH = 2*/;

	/** Names of fever time criteria settings */
	private static final String[] FEVER_TIME_CRITERIA_NAMES = {"COUNTER", "ATTACK"};

	/** Constants for fever time criteria settings */
	private static final int FEVER_TIME_CRITERIA_COUNTER = 0, FEVER_TIME_CRITERIA_ATTACK = 1;

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

	/** Version */
	private int version;

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

	/** Chain levels for Fever Mode */
	private int[] feverChain;

	/** Criteria to add a fever point */
	private int[] feverPointCriteria;

	/** Criteria to add 1 second of fever time */
	private int[] feverTimeCriteria;

	/** Fever power multiplier */
	private int[] feverPower;

	/** True to show fever points as meter, false to show numerical counts */
	private boolean[] feverShowMeter;

	/** True to show ojama on meter, false to show fever points */
	private boolean[] ojamaMeter;

	/*
	 * Mode name
	 */
	@Override
	public String getName() {
		return "AVALANCHE VS-BATTLE (RC1)";
	}

	/*
	 * Mode initialization
	 */
	@Override
	public void modeInit(GameManager manager) {
		super.modeInit(manager);
		feverThreshold = new int[MAX_PLAYERS];
		feverPoints = new int[MAX_PLAYERS];
		feverTime = new int[MAX_PLAYERS];
		feverTimeMin = new int[MAX_PLAYERS];
		feverTimeMax = new int[MAX_PLAYERS];
		inFever = new boolean[MAX_PLAYERS];
		feverBackupField = new Field[MAX_PLAYERS];
		ojamaFever = new int[MAX_PLAYERS];
		ojamaAddToFever = new boolean[MAX_PLAYERS];
		feverChain = new int[MAX_PLAYERS];
		feverShowMeter = new boolean[MAX_PLAYERS];
		ojamaMeter = new boolean[MAX_PLAYERS];
		feverPointCriteria = new int[MAX_PLAYERS];
		feverTimeCriteria = new int[MAX_PLAYERS];
		feverPower = new int[MAX_PLAYERS];
	}

	/**
	 * Load settings not related to speeds
	 * @param engine GameEngine
	 * @param prop Property file to read from
	 */
	private void loadOtherSetting(GameEngine engine, CustomProperties prop) {
		super.loadOtherSetting(engine, prop, "");
		int playerID = engine.playerID;
		ojamaRate[playerID] = prop.getProperty("avalanchevs.ojamaRate.p" + playerID, 120);
		ojamaHard[playerID] = prop.getProperty("avalanchevs.ojamaHard.p" + playerID, 0);
		feverThreshold[playerID] = prop.getProperty("avalanchevs.feverThreshold.p" + playerID, 0);
		feverTimeMin[playerID] = prop.getProperty("avalanchevs.feverTimeMin.p" + playerID, 15);
		feverTimeMax[playerID] = prop.getProperty("avalanchevs.feverTimeMax.p" + playerID, 30);
		feverShowMeter[playerID] = prop.getProperty("avalanchevs.feverShowMeter.p" + playerID, true);
		ojamaMeter[playerID] = prop.getProperty("avalanchevs.ojamaMeter.p" + playerID, true);
		feverPointCriteria[playerID] = prop.getProperty("avalanchevs.feverPointCriteria.p" + playerID, 0);
		feverTimeCriteria[playerID] = prop.getProperty("avalanchevs.feverTimeCriteria.p" + playerID, 0);
		feverPower[playerID] = prop.getProperty("avalanchevs.feverPower.p" + playerID, 10);
	}

	/**
	 * Save settings not related to speeds
	 * @param engine GameEngine
	 * @param prop Property file to save to
	 */
	private void saveOtherSetting(GameEngine engine, CustomProperties prop) {
		super.saveOtherSetting(engine, prop, "");
		int playerID = engine.playerID;
		prop.setProperty("avalanchevs.feverThreshold.p" + playerID, feverThreshold[playerID]);
		prop.setProperty("avalanchevs.feverTimeMin.p" + playerID, feverTimeMin[playerID]);
		prop.setProperty("avalanchevs.feverTimeMax.p" + playerID, feverTimeMax[playerID]);
		prop.setProperty("avalanchevs.feverShowMeter.p" + playerID, feverShowMeter[playerID]);
		prop.setProperty("avalanchevs.ojamaMeter.p" + playerID, ojamaMeter[playerID]);
		prop.setProperty("avalanchevs.feverPointCriteria.p" + playerID, feverPointCriteria[playerID]);
		prop.setProperty("avalanchevs.feverTimeCriteria.p" + playerID, feverTimeCriteria[playerID]);
		prop.setProperty("avalanchevs.feverPower.p" + playerID, feverPower[playerID]);
	}

	/*
	 * Initialization for each player
	 */
	@Override
	public void playerInit(GameEngine engine, int playerID) {
		super.playerInit(engine, playerID);
		ojamaFever[playerID] = 0;
		feverPoints[playerID] = 0;
		feverTime[playerID] = feverTimeMin[playerID] * 60;
		inFever[playerID] = false;
		feverBackupField[playerID] = null;

		if(engine.owner.replayMode == false) {
			loadOtherSetting(engine, engine.owner.modeConfig);
			loadPreset(engine, engine.owner.modeConfig, -1 - playerID, "");
			version = CURRENT_VERSION;
		} else {
			loadOtherSetting(engine, engine.owner.replayProp);
			loadPreset(engine, engine.owner.replayProp, -1 - playerID, "");
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
			int change = updateCursor(engine, 40);

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
					newChainPower[playerID] = !newChainPower[playerID];
					break;
				case 16:
					outlineType[playerID] += change;
					if(outlineType[playerID] < 0) outlineType[playerID] = 2;
					if(outlineType[playerID] > 2) outlineType[playerID] = 0;
					break;
				case 17:
					chainDisplayType[playerID] += change;
					if(chainDisplayType[playerID] < 0) chainDisplayType[playerID] = 3;
					if(chainDisplayType[playerID] > 3) chainDisplayType[playerID] = 0;
					break;
				case 18:
					cascadeSlow[playerID] = !cascadeSlow[playerID];
					break;
				case 19:
					big[playerID] = !big[playerID];
					break;
				case 20:
					zenKeshiType[playerID] += change;
					if(zenKeshiType[playerID] < 0) zenKeshiType[playerID] = 2;
					if(zenKeshiType[playerID] > 2) zenKeshiType[playerID] = 0;
					break;
				case 21:
					ojamaHard[playerID] += change;
					if(ojamaHard[playerID] < 0) ojamaHard[playerID] = 9;
					if(ojamaHard[playerID] > 9) ojamaHard[playerID] = 0;
					break;
				case 22:
					dangerColumnDouble[playerID] = !dangerColumnDouble[playerID];
					break;
				case 23:
					dangerColumnShowX[playerID] = !dangerColumnShowX[playerID];
					break;
				case 24:
					feverThreshold[playerID] += change;
					if(feverThreshold[playerID] < 0) feverThreshold[playerID] = 9;
					if(feverThreshold[playerID] > 9) feverThreshold[playerID] = 0;
					break;
				case 25:
					feverMapSet[playerID] += change;
					if(feverMapSet[playerID] < 0) feverMapSet[playerID] = FEVER_MAPS.length-1;
					if(feverMapSet[playerID] >= FEVER_MAPS.length) feverMapSet[playerID] = 0;
					break;
				case 26:
					if (m >= 10) feverTimeMin[playerID] += change*10;
					else feverTimeMin[playerID] += change;
					if(feverTimeMin[playerID] < 1) feverTimeMin[playerID] = feverTimeMax[playerID];
					if(feverTimeMin[playerID] > feverTimeMax[playerID]) feverTimeMin[playerID] = 1;
					break;
				case 27:
					if (m >= 10) feverTimeMax[playerID] += change*10;
					else feverTimeMax[playerID] += change;
					if(feverTimeMax[playerID] < feverTimeMin[playerID]) feverTimeMax[playerID] = 99;
					if(feverTimeMax[playerID] > 99) feverTimeMax[playerID] = feverTimeMin[playerID];
					break;
				case 28:
					feverShowMeter[playerID] = !feverShowMeter[playerID];
					break;
				case 29:
					feverPointCriteria[playerID] += change;
					if(feverPointCriteria[playerID] < 0) feverPointCriteria[playerID] = 2;
					if(feverPointCriteria[playerID] > 2) feverPointCriteria[playerID] = 0;
					break;
				case 30:
					feverTimeCriteria[playerID] += change;
					if(feverTimeCriteria[playerID] < 0) feverTimeCriteria[playerID] = 1;
					if(feverTimeCriteria[playerID] > 1) feverTimeCriteria[playerID] = 0;
					break;
				case 31:
					feverPower[playerID] += change;
					if(feverPower[playerID] < 0) feverPower[playerID] = 20;
					if(feverPower[playerID] > 20) feverPower[playerID] = 0;
					break;
				case 32:
					if (feverThreshold[playerID] > 0)
						ojamaMeter[playerID] = !ojamaMeter[playerID];
					else
						ojamaMeter[playerID] = true;
					break;
				case 33:
					useMap[playerID] = !useMap[playerID];
					if(!useMap[playerID]) {
						if(engine.field != null) engine.field.reset();
					} else {
						loadMapPreview(engine, playerID, (mapNumber[playerID] < 0) ? 0 : mapNumber[playerID], true);
					}
					break;
				case 34:
					mapSet[playerID] += change;
					if(mapSet[playerID] < 0) mapSet[playerID] = 99;
					if(mapSet[playerID] > 99) mapSet[playerID] = 0;
					if(useMap[playerID]) {
						mapNumber[playerID] = -1;
						loadMapPreview(engine, playerID, (mapNumber[playerID] < 0) ? 0 : mapNumber[playerID], true);
					}
					break;
				case 35:
					if(useMap[playerID]) {
						mapNumber[playerID] += change;
						if(mapNumber[playerID] < -1) mapNumber[playerID] = mapMaxNo[playerID] - 1;
						if(mapNumber[playerID] > mapMaxNo[playerID] - 1) mapNumber[playerID] = -1;
						loadMapPreview(engine, playerID, (mapNumber[playerID] < 0) ? 0 : mapNumber[playerID], true);
					} else {
						mapNumber[playerID] = -1;
					}
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
					bigDisplay = !bigDisplay;
					break;
				case 39:
				case 40:
					presetNumber[playerID] += change;
					if(presetNumber[playerID] < 0) presetNumber[playerID] = 99;
					if(presetNumber[playerID] > 99) presetNumber[playerID] = 0;
					break;
				}
			}

			// 決定
			if(engine.ctrl.isPush(Controller.BUTTON_A) && (engine.statc[3] >= 5)) {
				engine.playSE("decide");

				if(engine.statc[2] == 39) {
					loadPreset(engine, owner.modeConfig, presetNumber[playerID], "");
				} else if(engine.statc[2] == 40) {
					savePreset(engine, owner.modeConfig, presetNumber[playerID], "");
					receiver.saveModeConfig(owner.modeConfig);
				} else {
					saveOtherSetting(engine, owner.modeConfig);
					savePreset(engine, owner.modeConfig, -1 - playerID, "");
					receiver.saveModeConfig(owner.modeConfig);
					engine.statc[4] = 1;
				}
			}

			// Cancel
			if(engine.ctrl.isPush(Controller.BUTTON_B)) {
				engine.quitflag = true;
			}

			// プレビュー用Map読み込み
			if(useMap[playerID] && (engine.statc[3] == 0)) {
				loadMapPreview(engine, playerID, (mapNumber[playerID] < 0) ? 0 : mapNumber[playerID], true);
			}

			// Random map preview
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
			} else if(engine.statc[2] < 16) {
				drawMenu(engine, playerID, receiver, 0, EventReceiver.COLOR_CYAN, 9,
						"COUNTER", OJAMA_COUNTER_STRING[ojamaCounterMode[playerID]],
						"MAX ATTACK", String.valueOf(maxAttack[playerID]),
						"COLORS", String.valueOf(numColors[playerID]),
						"MIN CHAIN", String.valueOf(rensaShibari[playerID]),
						"OJAMA RATE", String.valueOf(ojamaRate[playerID]),
						"HURRYUP", (hurryupSeconds[playerID] == 0) ? "NONE" : hurryupSeconds[playerID]+"SEC",
						"CHAINPOWER", newChainPower[playerID] ? "FEVER" : "CLASSIC");

				receiver.drawMenuFont(engine, playerID, 0, 19, "PAGE 2/5", EventReceiver.COLOR_YELLOW);
			} else if(engine.statc[2] < 24) {
				drawMenu(engine, playerID, receiver, 0, EventReceiver.COLOR_DARKBLUE, 16,
						"OUTLINE", OUTLINE_TYPE_NAMES[outlineType[playerID]],
						"SHOW CHAIN", CHAIN_DISPLAY_NAMES[chainDisplayType[playerID]],
						"FALL ANIM", cascadeSlow[playerID] ? "FEVER" : "CLASSIC");
				drawMenu(engine, playerID, receiver, 6, EventReceiver.COLOR_CYAN, 19,
						"BIG", GeneralUtil.getONorOFF(big[playerID]));
				drawMenu(engine, playerID, receiver, 8,
						big[playerID] ? EventReceiver.COLOR_WHITE : EventReceiver.COLOR_CYAN, 20,
						"ZENKESHI", ZENKESHI_TYPE_NAMES[zenKeshiType[playerID]],
						"HARD OJAMA", String.valueOf(ojamaHard[playerID]),
						"X COLUMN", dangerColumnDouble[playerID] ? "3 AND 4" : "3 ONLY",
						"X SHOW", GeneralUtil.getONorOFF(dangerColumnShowX[playerID]));

				receiver.drawMenuFont(engine, playerID, 0, 19, "PAGE 3/5", EventReceiver.COLOR_YELLOW);
			} else if(engine.statc[2] < 33) {
				drawMenu(engine, playerID, receiver, 0,
						big[playerID] ? EventReceiver.COLOR_WHITE : EventReceiver.COLOR_PURPLE, 24,
						"FEVER", (feverThreshold[playerID] == 0) ? "NONE" : feverThreshold[playerID]+" PTS");
				drawMenu(engine, playerID, receiver, 2, (big[playerID] ||
						(feverThreshold[playerID] == 0 && zenKeshiType[playerID] != ZENKESHI_MODE_FEVER)) ?
						EventReceiver.COLOR_WHITE : EventReceiver.COLOR_PURPLE, 25,
						"F-MAP SET", FEVER_MAPS[feverMapSet[playerID]].toUpperCase());
				drawMenu(engine, playerID, receiver, 4, (big[playerID] || feverThreshold[playerID] == 0) ?
							EventReceiver.COLOR_WHITE : EventReceiver.COLOR_PURPLE, 26,
						"F-MIN TIME", feverTimeMin[playerID] + "SEC",
						"F-MAX TIME", feverTimeMax[playerID] + "SEC",
						"F-DISPLAY", feverShowMeter[playerID] ? "METER" : "COUNT",
						"F-ADDPOINT", FEVER_POINT_CRITERIA_NAMES[feverPointCriteria[playerID]],
						"F-ADDTIME", FEVER_TIME_CRITERIA_NAMES[feverTimeCriteria[playerID]],
						"F-POWER", (feverPower[playerID] * 10) + "%",
						"SIDE METER", (ojamaMeter[playerID] || feverThreshold[playerID] == 0) ? "OJAMA" : "FEVER");

				receiver.drawMenuFont(engine, playerID, 0, 19, "PAGE 4/5", EventReceiver.COLOR_YELLOW);
			} else {
				drawMenu(engine, playerID, receiver, 0, EventReceiver.COLOR_PINK, 33,
						"USE MAP", GeneralUtil.getONorOFF(useMap[playerID]),
						"MAP SET", String.valueOf(mapSet[playerID]),
						"MAP NO.", (mapNumber[playerID] < 0) ? "RANDOM" : mapNumber[playerID]+"/"+(mapMaxNo[playerID]-1));
				drawMenu(engine, playerID, receiver, 6, EventReceiver.COLOR_DARKBLUE, 36,
						"BGM", String.valueOf(bgmno));
				drawMenu(engine, playerID, receiver, 8, EventReceiver.COLOR_YELLOW, 37,
						"SE", GeneralUtil.getONorOFF(enableSE[playerID]));
				drawMenu(engine, playerID, receiver, 10, EventReceiver.COLOR_DARKBLUE, 38,
						"BIG DISP", GeneralUtil.getONorOFF(bigDisplay));
				drawMenu(engine, playerID, receiver, 12, EventReceiver.COLOR_GREEN, 39,
						"LOAD", String.valueOf(presetNumber[playerID]),
						"SAVE", String.valueOf(presetNumber[playerID]));

				receiver.drawMenuFont(engine, playerID, 0, 19, "PAGE 5/5", EventReceiver.COLOR_YELLOW);
			}
		} else {
			receiver.drawMenuFont(engine, playerID, 3, 10, "WAIT", EventReceiver.COLOR_YELLOW);
		}
	}

	/*
	 * Called for initialization during Ready (before initialization)
	 */
	@Override
	public boolean readyInit(GameEngine engine, int playerID) {
		if (big[playerID])
		{
			feverThreshold[playerID] = 0;
			ojamaMeter[playerID] = true;
		}
		else if(feverThreshold[playerID] == 0)
			ojamaMeter[playerID] = true;
		else
		{
			feverTime[playerID] = feverTimeMin[playerID] * 60;
			feverChain[playerID] = 5;
		}
		super.readyInit(engine, playerID);
		return false;
	}

	@Override
	public void renderMove(GameEngine engine, int playerID) {
		drawXorTimer(engine, playerID);
	}

	@Override
	public void renderLast(GameEngine engine, int playerID) {
		int fldPosX = receiver.getFieldDisplayPositionX(engine, playerID);
		int fldPosY = receiver.getFieldDisplayPositionY(engine, playerID);
		int playerColor = (playerID == 0) ? EventReceiver.COLOR_RED : EventReceiver.COLOR_BLUE;
		int fontColor = EventReceiver.COLOR_WHITE;

		// Timer
		if(playerID == 0) {
			if(receiver.getNextDisplayType() == 0)
				receiver.drawDirectFont(engine, playerID, 224, 16, GeneralUtil.getTime(engine.statistics.time));
			else
				receiver.drawDirectFont(engine, playerID, 256, 16, GeneralUtil.getTime(engine.statistics.time));
		}

		// Ojama Counter
		fontColor = EventReceiver.COLOR_WHITE;
		if(ojama[playerID] >= 1) fontColor = EventReceiver.COLOR_YELLOW;
		if(ojama[playerID] >= 6) fontColor = EventReceiver.COLOR_ORANGE;
		if(ojama[playerID] >= 12) fontColor = EventReceiver.COLOR_RED;
		if(ojama[playerID] + ojamaAdd[playerID] > 0) {
			receiver.drawDirectFont(engine, playerID, fldPosX + 4, fldPosY + 32, ""+(ojama[playerID] + ojamaAdd[playerID]), fontColor);
		}
		if(ojamaFever[playerID] > 0) {
			receiver.drawDirectFont(engine, playerID, fldPosX + 4, fldPosY + 16, ""+ojamaFever[playerID]);
		}

		// Score
		String strScoreMultiplier = "";
		if((lastscore[playerID] != 0) && (lastmultiplier[playerID] != 0) && (scgettime[playerID] > 0))
			strScoreMultiplier = "(" + lastscore[playerID] + "e" + lastmultiplier[playerID] + ")";

		if(engine.displaysize == 1) {
			receiver.drawDirectFont(engine, playerID, fldPosX + 4, fldPosY + 440, String.format("%12d", score[playerID]), playerColor);
			receiver.drawDirectFont(engine, playerID, fldPosX + 4, fldPosY + 456, String.format("%12s", strScoreMultiplier), playerColor);
		} else if(engine.gameActive) {
			receiver.drawDirectFont(engine, playerID, fldPosX - 28, fldPosY + 248, String.format("%8d", score[playerID]), playerColor);
			receiver.drawDirectFont(engine, playerID, fldPosX - 28, fldPosY + 264, String.format("%8s", strScoreMultiplier), playerColor);
		}

		// Fever
		if(feverThreshold[playerID] > 0) {
			// Timer
			if(engine.displaysize == 1) {
				receiver.drawDirectFont(engine, playerID, fldPosX + 224, fldPosY + 200, "REST", playerColor, 0.5f);
				receiver.drawDirectFont(engine, playerID, fldPosX + 216, fldPosY + 216, String.format("%2d", feverTime[playerID] / 60));
				receiver.drawDirectFont(engine, playerID, fldPosX + 248, fldPosY + 224, String.format(".%d", feverTime[playerID] % 60 / 6), 0.5f);
			} else if(engine.gameActive) {
				receiver.drawDirectFont(engine, playerID, fldPosX + 128, fldPosY + 184, "REST", playerColor, 0.5f);
				receiver.drawDirectFont(engine, playerID, fldPosX + 120, fldPosY + 200, String.format("%2d", feverTime[playerID] / 60));
				receiver.drawDirectFont(engine, playerID, fldPosX + 152, fldPosY + 208, String.format(".%d", feverTime[playerID] % 60 / 6), 0.5f);
			}

			// Points
			if(feverShowMeter[playerID] && (engine.displaysize == 1)) {
				if(inFever[playerID]) {
					int color = (engine.statistics.time >> 2) % FEVER_METER_COLORS.length;
					for(int i = 0; i < feverThreshold[playerID]; i++) {
						if(color == 0) color = FEVER_METER_COLORS.length;
						color--;
						receiver.drawDirectFont(engine, playerID, fldPosX + 232, fldPosY + 424 - (i * 16), "d", FEVER_METER_COLORS[color]);
					}
				} else {
					for(int i = feverPoints[playerID]; i < feverThreshold[playerID]; i++) {
						receiver.drawDirectFont(engine, playerID, fldPosX + 232, fldPosY + 424 - (i * 16), "c");
					}
					for(int i = 0; i < feverPoints[playerID]; i++) {
						int color = feverThreshold[playerID] - 1 - i;
						receiver.drawDirectFont(engine, playerID, fldPosX + 232, fldPosY + 424 - (i * 16), "d", FEVER_METER_COLORS[color]);
					}
				}
			} else if(engine.displaysize == 1) {
				receiver.drawDirectFont(engine, playerID, fldPosX + 220, fldPosY + 240, "FEVER", playerColor, 0.5f);
				receiver.drawDirectFont(engine, playerID, fldPosX + 228, fldPosY + 256, feverPoints[playerID]+"/"+feverThreshold[playerID], 0.5f);
			} else if(engine.gameActive) {
				receiver.drawDirectFont(engine, playerID, fldPosX + 124, fldPosY + 224, "FEVER", playerColor, 0.5f);
				receiver.drawDirectFont(engine, playerID, fldPosX + 132, fldPosY + 240, feverPoints[playerID]+"/"+feverThreshold[playerID], 0.5f);
			}
		}

		if((engine.stat != GameEngine.STAT_MOVE) && (engine.gameActive))
			drawXorTimer(engine, playerID);

		if(ojamaHard[playerID] > 0)
			drawHardOjama(engine, playerID);

		int textHeight = 13;
		if (engine.field != null) {
			textHeight = engine.field.getHeight();
			textHeight += 3;
		}
		if(engine.displaysize == 1) textHeight = 11;

		int baseX = (engine.displaysize == 1) ? 1 : -2;

		if (chain[playerID] > 0 && chainDisplay[playerID] > 0 && chainDisplayType[playerID] != CHAIN_DISPLAY_NONE)
			receiver.drawMenuFont(engine, playerID, baseX + (chain[playerID] > 9 ? 0 : 1), textHeight,
					chain[playerID] + " CHAIN!", getChainColor(playerID));
		if(zenKeshi[playerID] || zenKeshiDisplay[playerID] > 0)
			receiver.drawMenuFont(engine, playerID, baseX+1, textHeight+1, "ZENKESHI!", EventReceiver.COLOR_YELLOW);
	}

	/**
	 * Draw X or fever timer
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	protected void drawXorTimer(GameEngine engine, int playerID) {
		if(inFever[playerID]) {
			if(engine.displaysize == 1) {
				receiver.drawMenuFont(engine, playerID, 4, 0, String.format("%02d",(feverTime[playerID]+59)/60),
						feverTime[playerID] < 360 ? EventReceiver.COLOR_RED : EventReceiver.COLOR_WHITE, 2.0f);
			} else {
				receiver.drawMenuFont(engine, playerID, 2, 0, String.format("%02d",(feverTime[playerID]+59)/60),
						feverTime[playerID] < 360 ? EventReceiver.COLOR_RED : EventReceiver.COLOR_WHITE);
			}
		} else if (!big[playerID] && dangerColumnShowX[playerID]) {
			drawX(engine, playerID);
		}
	}

	protected int calcChainNewPower(GameEngine engine, int playerID, int chain) {
		int[] powers = inFever[playerID] ? FEVER_POWERS : CHAIN_POWERS;
		if (chain > powers.length)
			return powers[powers.length-1];
		else
			return powers[chain-1];
	}

	protected void onClear(GameEngine engine, int playerID) {
		int enemyID = 0;
		if(playerID == 0) enemyID = 1;

		if (chain[playerID] == 1)
			ojamaAddToFever[enemyID] = inFever[enemyID];
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
		if (inFever[playerID])
			ojamaNew += ((pts*feverPower[playerID])+(10*rate)-1) / (10*rate);
		else
			ojamaNew += (pts+rate-1)/rate;
		ojamaSent[playerID] += ojamaNew;

		if (feverThreshold[playerID] > 0 && feverTimeCriteria[playerID] == FEVER_TIME_CRITERIA_ATTACK && !inFever[playerID])
			feverTime[playerID] = Math.min(feverTime[playerID]+60,feverTimeMax[playerID]*60);

		boolean countered = false;
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
		}
		if (zenKeshi[playerID] && zenKeshiType[playerID] != ZENKESHI_MODE_ON)
		{
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
			engine.field.garbageDrop(engine, drop, false, ojamaHard[playerID]);
			engine.field.setAllSkin(engine.getSkin());
			return true;
		}
		//Check for game over
		gameOverCheck(engine, playerID);
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

	/*
	 * Called after every frame
	 */
	@Override
	public void onLast(GameEngine engine, int playerID) {
		super.onLast(engine, playerID);

		if(engine.ctrl.isPush(Controller.BUTTON_F)) {
			if(feverPoints[playerID] < feverThreshold[playerID])
				feverPoints[playerID]++;
		}

		if (inFever[playerID] && feverTime[playerID] > 0 && engine.timerActive)
		{
			feverTime[playerID]--;
			if((feverTime[playerID] > 0) && (feverTime[playerID] <= 360) && (feverTime[playerID] % 60 == 0))
				engine.playSE("countdown");
			else if (feverTime[playerID] == 0)
				engine.playSE("levelstop");
		}
		if (ojamaMeter[playerID] || feverThreshold[playerID] == 0)
			updateOjamaMeter(engine, playerID);
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
	}

	/*
	 * Called when saving replay
	 */
	@Override
	public void saveReplay(GameEngine engine, int playerID, CustomProperties prop) {
		saveOtherSetting(engine, owner.replayProp);
		savePreset(engine, owner.replayProp, -1 - playerID, "");

		if(useMap[playerID] && (fldBackup[playerID] != null)) {
			saveMap(fldBackup[playerID], owner.replayProp, playerID);
		}

		owner.replayProp.setProperty("avalanchevs.version", version);
	}
}
