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
import mu.nu.nullpo.game.event.EventReceiver;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.gui.slick.GameKey;
import mu.nu.nullpo.util.CustomProperties;
import mu.nu.nullpo.util.GeneralUtil;

/**
 * AVALANCHE VS FEVER MARATHON mode (Release Candidate 1)
 */
public class AvalancheVSFeverMode extends AvalancheVSDummyMode {
	/** Current version */
	private static final int CURRENT_VERSION = 1;

	/** Chain multipliers */
	private static final int[] FEVER_POWERS = {
		4, 10, 18, 21, 29, 46, 76, 113, 150, 223, 259, 266, 313, 364, 398, 432, 468, 504, 540, 576, 612, 648, 684, 720 //Arle
	};
	/** Constants for chain display settings */
	public static final int CHAIN_DISPLAY_FEVERSIZE = 4;

	/** Version */
	private int version;

	/** Second ojama counter for Fever Mode */
	private int[] ojamaHandicapLeft;

	/** Chain levels for Fever Mode */
	private int[] feverChain;

	/** Ojama handicap to start with */
	private int[] ojamaHandicap;

	/** Fever chain count when last chain hit occurred */
	private int[] feverChainDisplay;

	/*
	 * Mode name
	 */
	@Override
	public String getName() {
		return "AVALANCHE VS FEVER MARATHON (RC1)";
	}

	/*
	 * Mode initialization
	 */
	@Override
	public void modeInit(GameManager manager) {
		super.modeInit(manager);
		ojamaHandicapLeft = new int[MAX_PLAYERS];
		feverChain = new int[MAX_PLAYERS];
		ojamaHandicap = new int[MAX_PLAYERS];
		feverChainDisplay = new int[MAX_PLAYERS];
	}

	/**
	 * Load settings not related to speeds
	 * @param engine GameEngine
	 * @param prop Property file to read from
	 */
	private void loadOtherSetting(GameEngine engine, CustomProperties prop) {
		super.loadOtherSetting(engine, prop, "fever");
		int playerID = engine.playerID;
		ojamaRate[playerID] = prop.getProperty("avalanchevsfever.ojamaRate.p" + playerID, 120);
		ojamaHard[playerID] = prop.getProperty("avalanchevsfever.ojamaHard.p" + playerID, 0);
		ojamaHandicap[playerID] = prop.getProperty("avalanchevsfever.ojamaHandicap.p" + playerID, 270);
	}

	/**
	 * Save settings not related to speeds
	 * @param engine GameEngine
	 * @param prop Property file to save to
	 */
	private void saveOtherSetting(GameEngine engine, CustomProperties prop) {
		super.saveOtherSetting(engine, prop, "fever");
		int playerID = engine.playerID;
		prop.setProperty("avalanchevsfever.ojamaHandicap.p" + playerID, ojamaHandicap[playerID]);
	}

	/*
	 * Initialization for each player
	 */
	@Override
	public void playerInit(GameEngine engine, int playerID) {
		super.playerInit(engine, playerID);
		ojamaCounterMode[playerID] = OJAMA_COUNTER_FEVER;

		ojama[playerID] = 0;
		feverChainDisplay[playerID] = 0;

		if(engine.owner.replayMode == false) {
			loadOtherSetting(engine, engine.owner.modeConfig);
			loadPreset(engine, engine.owner.modeConfig, -1 - playerID, "fever");
			version = CURRENT_VERSION;
		} else {
			loadOtherSetting(engine, engine.owner.replayProp);
			loadPreset(engine, engine.owner.replayProp, -1 - playerID, "fever");
			version = owner.replayProp.getProperty("avalanchevsfever.version", 0);
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
			int change = updateCursor(engine, 27);

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
					zenKeshiType[playerID] += change;
					if(zenKeshiType[playerID] < 0) zenKeshiType[playerID] = 2;
					if(zenKeshiType[playerID] > 2) zenKeshiType[playerID] = 0;
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
					ojamaHandicap[playerID] += change * m;
					if(ojamaHandicap[playerID] < 0) ojamaHandicap[playerID] = 9999;
					if(ojamaHandicap[playerID] > 9999) ojamaHandicap[playerID] = 0;
					break;
				case 19:
					feverMapSet[playerID] += change;
					if(feverMapSet[playerID] < 0) feverMapSet[playerID] = FEVER_MAPS.length-1;
					if(feverMapSet[playerID] >= FEVER_MAPS.length) feverMapSet[playerID] = 0;
					break;
				case 20:
					outlineType[playerID] += change;
					if(outlineType[playerID] < 0) outlineType[playerID] = 2;
					if(outlineType[playerID] > 2) outlineType[playerID] = 0;
					break;
				case 21:
					chainDisplayType[playerID] += change;
					if(chainDisplayType[playerID] < 0) chainDisplayType[playerID] = 4;
					if(chainDisplayType[playerID] > 4) chainDisplayType[playerID] = 0;
					break;
				case 22:
					cascadeSlow[playerID] = !cascadeSlow[playerID];
					break;
				case 23:
					newChainPower[playerID] = !newChainPower[playerID];
					break;
				case 24:
					bgmno += change;
					if(bgmno < 0) bgmno = BGMStatus.BGM_COUNT - 1;
					if(bgmno > BGMStatus.BGM_COUNT - 1) bgmno = 0;
					break;
				case 25:
					enableSE[playerID] = !enableSE[playerID];
					break;
				case 26:
				case 27:
					presetNumber[playerID] += change;
					if(presetNumber[playerID] < 0) presetNumber[playerID] = 99;
					if(presetNumber[playerID] > 99) presetNumber[playerID] = 0;
					break;
				}
			}

			// 決定
			if(GameKey.gamekey[playerID].isPushKey(GameKey.BUTTON_NAV_SELECT) && (engine.statc[3] >= 5)) {
				engine.playSE("decide");

				if(engine.statc[2] == 26) {
					loadPreset(engine, owner.modeConfig, presetNumber[playerID], "fever");
				} else if(engine.statc[2] == 27) {
					savePreset(engine, owner.modeConfig, presetNumber[playerID], "fever");
					receiver.saveModeConfig(owner.modeConfig);
				} else {
					saveOtherSetting(engine, owner.modeConfig);
					savePreset(engine, owner.modeConfig, -1 - playerID, "fever");
					receiver.saveModeConfig(owner.modeConfig);
					engine.statc[4] = 1;
				}
			}

			// Cancel
			if(GameKey.gamekey[playerID].isPushKey(GameKey.BUTTON_NAV_CANCEL)) {
				engine.quitflag = true;
			}
			engine.statc[3]++;
		} else if(engine.statc[4] == 0) {
			engine.statc[3]++;
			engine.statc[2] = 0;

			if(engine.statc[3] >= 240)
				engine.statc[4] = 1;
			else if(engine.statc[3] >= 180)
				engine.statc[2] = 24;
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
			else if(GameKey.gamekey[playerID].isPushKey(GameKey.BUTTON_NAV_CANCEL)) {
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

				receiver.drawMenuFont(engine, playerID, 0, 19, "PAGE 1/4", EventReceiver.COLOR_YELLOW);
			} else if(engine.statc[2] < 18) {
				drawMenu(engine, playerID, receiver, 0, EventReceiver.COLOR_CYAN, 9,
						"ZENKESHI", ZENKESHI_TYPE_NAMES[zenKeshiType[playerID]],
						"MAX ATTACK", String.valueOf(maxAttack[playerID]),
						"COLORS", String.valueOf(numColors[playerID]),
						"MIN CHAIN", String.valueOf(rensaShibari[playerID]),
						"OJAMA RATE", String.valueOf(ojamaRate[playerID]),
						"HURRYUP", (hurryupSeconds[playerID] == 0) ? "NONE" : hurryupSeconds[playerID]+"SEC",
						"HARD OJAMA", String.valueOf(ojamaHard[playerID]),
						"X COLUMN", dangerColumnDouble[playerID] ? "3 AND 4" : "3 ONLY",
						"X SHOW", GeneralUtil.getONorOFF(dangerColumnShowX[playerID]));

				receiver.drawMenuFont(engine, playerID, 0, 19, "PAGE 2/4", EventReceiver.COLOR_YELLOW);
			} else if(engine.statc[2] < 24) {
				drawMenu(engine, playerID, receiver, 0, EventReceiver.COLOR_PURPLE, 18,
						"HANDICAP", String.valueOf(ojamaHandicap[playerID]),
						"F-MAP SET", FEVER_MAPS[feverMapSet[playerID]].toUpperCase());
				drawMenu(engine, playerID, receiver, 4, EventReceiver.COLOR_DARKBLUE, 20,
						"OUTLINE", OUTLINE_TYPE_NAMES[outlineType[playerID]],
						"SHOW CHAIN", chainDisplayType[playerID] == CHAIN_DISPLAY_FEVERSIZE ?
								"FEVERSIZE" : CHAIN_DISPLAY_NAMES[chainDisplayType[playerID]],
						"FALL ANIM", cascadeSlow[playerID] ? "FEVER" : "CLASSIC");
				drawMenu(engine, playerID, receiver, 10, EventReceiver.COLOR_CYAN, 23,
						"CHAINPOWER", newChainPower[playerID] ? "FEVER" : "CLASSIC");

				receiver.drawMenuFont(engine, playerID, 0, 19, "PAGE 3/4", EventReceiver.COLOR_YELLOW);
			} else {
				drawMenu(engine, playerID, receiver, 0, EventReceiver.COLOR_PINK, 24,
						"BGM", String.valueOf(bgmno),
						"SE", GeneralUtil.getONorOFF(enableSE[playerID]));
				drawMenu(engine, playerID, receiver, 4, EventReceiver.COLOR_GREEN, 26,
						"LOAD", String.valueOf(presetNumber[playerID]),
						"SAVE", String.valueOf(presetNumber[playerID]));

				receiver.drawMenuFont(engine, playerID, 0, 19, "PAGE 4/4", EventReceiver.COLOR_YELLOW);
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
		super.readyInit(engine, playerID);
		ojamaHandicapLeft[playerID] = ojamaHandicap[playerID];
		feverChain[playerID] = 5;
		if(engine.field != null)
			engine.field.reset();
		loadMapSetFever(engine, playerID, feverMapSet[playerID], true);
		return false;
	}

	/*
	 * Called at game start
	 */
	@Override
	public void startGame(GameEngine engine, int playerID) {
		super.startGame(engine, playerID);
		loadFeverMap(engine, playerID, feverChain[playerID]);
	}

	/*
	 * Render score
	 */
	@Override
	public void renderLast(GameEngine engine, int playerID) {
		// Status display
		if(playerID == 0) {
			receiver.drawScoreFont(engine, playerID, -1,  0, "AVALANCHE VS", EventReceiver.COLOR_GREEN);

			drawOjama(engine, playerID, -1, 2, EventReceiver.COLOR_PURPLE);

			receiver.drawScoreFont(engine, playerID, -1,  5, "HANDICAP", EventReceiver.COLOR_PURPLE);
			receiver.drawScoreFont(engine, playerID, -1,  6, "1P:", EventReceiver.COLOR_RED);
			receiver.drawScoreFont(engine, playerID,  3,  6, String.valueOf(ojamaHandicapLeft[0]), (ojamaHandicapLeft[0] == 0));
			receiver.drawScoreFont(engine, playerID, -1,  7, "2P:", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID,  3,  7, String.valueOf(ojamaHandicapLeft[1]), (ojamaHandicapLeft[1] == 0));

			drawAttack(engine, playerID, -1, 9, EventReceiver.COLOR_GREEN);
			drawScores(engine, playerID, -1, 13, EventReceiver.COLOR_PURPLE);

			receiver.drawScoreFont(engine, playerID, -1, 17, "TIME", EventReceiver.COLOR_GREEN);
			receiver.drawScoreFont(engine, playerID, -1, 18, GeneralUtil.getTime(engine.statistics.time));
		}

		if (!owner.engine[playerID].gameActive)
			return;
		if (dangerColumnShowX[playerID])
			drawX(engine, playerID);
		if (ojamaHard[playerID] > 0)
			drawHardOjama(engine, playerID);

		super.renderLast(engine, playerID);
	}

	@Override
	protected int getChainColor (int playerID) {
		if (chainDisplayType[playerID] == CHAIN_DISPLAY_FEVERSIZE)
		{
			if (chain[playerID] >= feverChainDisplay[playerID])
				return EventReceiver.COLOR_GREEN;
			else if (chain[playerID] == feverChainDisplay[playerID]-2)
				return EventReceiver.COLOR_ORANGE;
			else if (chain[playerID] < feverChainDisplay[playerID]-2)
				return EventReceiver.COLOR_RED;
			else
				return EventReceiver.COLOR_YELLOW;
		}
		else
			return super.getChainColor(playerID);
	}

	protected int calcChainNewPower(GameEngine engine, int playerID, int chain) {
		if (chain > FEVER_POWERS.length)
			return FEVER_POWERS[FEVER_POWERS.length-1];
		else
			return FEVER_POWERS[chain-1];
	}

	protected void onClear(GameEngine engine, int playerID) {
		feverChainDisplay[playerID] = feverChain[playerID];
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
		ojamaNew += (pts+rate-1)/rate;
		ojamaSent[playerID] += ojamaNew;

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
		if (ojamaHandicapLeft[playerID] > 0 && ojamaNew > 0)
		{
			int delta = Math.min(ojamaHandicapLeft[playerID], ojamaNew);
			ojamaHandicapLeft[playerID] -= delta;
			ojamaNew -= delta;
		}
		if (ojamaNew > 0)
			ojamaAdd[enemyID] += ojamaNew;
	}

	public boolean lineClearEnd(GameEngine engine, int playerID) {
		int enemyID = 0;
		if(playerID == 0) enemyID = 1;
		if (ojamaAdd[enemyID] > 0)
		{
			ojama[enemyID] += ojamaAdd[enemyID];
			ojamaAdd[enemyID] = 0;
		}
		//Reset Fever board if necessary
		if (cleared[playerID])
		{
			int newFeverChain = Math.max(engine.chain+1, feverChain[playerID]-2);
			if (newFeverChain > feverChain[playerID])
				engine.playSE("cool");
			else if (newFeverChain < feverChain[playerID])
				engine.playSE("regret");
			feverChain[playerID] = newFeverChain;
			if (zenKeshi[playerID] && zenKeshiType[playerID] == ZENKESHI_MODE_FEVER)
			{
				feverChain[playerID] += 2;
				zenKeshi[playerID] = false;
				zenKeshiDisplay[playerID] = 120;
			}
			if (feverChain[playerID] < feverChainMin[playerID])
				feverChain[playerID] = feverChainMin[playerID];
			if (feverChain[playerID] > feverChainMax[playerID])
				feverChain[playerID] = feverChainMax[playerID];
			loadFeverMap(engine, playerID, feverChain[playerID]);
		}
		//Drop garbage if needed.
		if (ojama[playerID] > 0 && !ojamaDrop[playerID] && !cleared[playerID])
		{
			ojamaDrop[playerID] = true;
			int drop = Math.min(ojama[playerID], maxAttack[playerID]);
			ojama[playerID] -= drop;
			engine.field.garbageDrop(engine, drop, false, ojamaHard[playerID]);
			engine.field.setAllSkin(engine.getSkin());
			return true;
		}
		//Check for game over
		gameOverCheck(engine, playerID);
		return false;
	}

	/*
	 * Called after every frame
	 */
	@Override
	public void onLast(GameEngine engine, int playerID) {
		super.onLast(engine, playerID);
		updateOjamaMeter(engine, playerID);
	}

	/*
	 * Called when saving replay
	 */
	@Override
	public void saveReplay(GameEngine engine, int playerID, CustomProperties prop) {
		saveOtherSetting(engine, owner.replayProp);
		savePreset(engine, owner.replayProp, -1 - playerID, "digrace");

		owner.replayProp.setProperty("avalanchevsfever.version", version);
	}
}
