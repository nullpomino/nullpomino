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
import mu.nu.nullpo.game.event.EventReceiver;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.util.CustomProperties;
import mu.nu.nullpo.util.GeneralUtil;

/**
 * AVALANCHE VS BOMB BATTLE mode (Release Candidate 1)
 */
public class AvalancheVSBombBattleMode extends AvalancheVSDummyMode {
	/** Current version */
	private static final int CURRENT_VERSION = 0;

	/** Version */
	private int version;

	/** True to use new (Fever) chain powers */
	private boolean[] newChainPower;

	/** Settings for starting countdown for ojama blocks */
	private int[] ojamaCountdown;

	/*
	 * Mode name
	 */
	@Override
	public String getName() {
		return "AVALANCHE VS BOMB BATTLE (RC1)";
	}

	/*
	 * Mode initialization
	 */
	@Override
	public void modeInit(GameManager manager) {
		super.modeInit(manager);

		ojamaCountdown = new int[MAX_PLAYERS];
		newChainPower = new boolean[MAX_PLAYERS];
	}

	/**
	 * Load settings not related to speeds
	 * @param engine GameEngine
	 * @param prop Property file to read from
	 */
	private void loadOtherSetting(GameEngine engine, CustomProperties prop) {
		super.loadOtherSetting(engine, prop, "bombbattle");
		int playerID = engine.playerID;
		ojamaRate[playerID] = prop.getProperty("avalanchevsbombbattle.ojamaRate.p" + playerID, 60);
		ojamaHard[playerID] = prop.getProperty("avalanchevsbombbattle.ojamaHard.p" + playerID, 1);
		newChainPower[playerID] = prop.getProperty("avalanchevsbombbattle.newChainPower.p" + playerID, false);
		ojamaCountdown[playerID] = prop.getProperty("avalanchevsbombbattle.ojamaCountdown.p" + playerID, 5);
	}

	/**
	 * Save settings not related to speeds
	 * @param engine GameEngine
	 * @param prop Property file to save to
	 */
	private void saveOtherSetting(GameEngine engine, CustomProperties prop) {
		super.saveOtherSetting(engine, prop, "bombbattle");
		int playerID = engine.playerID;
		prop.setProperty("avalanchevsbombbattle.newChainPower.p" + playerID, newChainPower[playerID]);
		prop.setProperty("avalanchevsbombbattle.ojamaCountdown.p" + playerID, ojamaCountdown[playerID]);
	}

	/*
	 * Initialization for each player
	 */
	@Override
	public void playerInit(GameEngine engine, int playerID) {
		owner = engine.owner;
		receiver = engine.owner.receiver;
		if(engine.owner.replayMode == false) {
			loadOtherSetting(engine, engine.owner.modeConfig);
			loadPreset(engine, engine.owner.modeConfig, -1 - playerID, "bombbattle");
			version = CURRENT_VERSION;
		} else {
			loadOtherSetting(engine, engine.owner.replayProp);
			loadPreset(engine, engine.owner.replayProp, -1 - playerID, "bombbattle");
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
			int change = updateCursor(engine, 33);

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
					engine.colorClearSize += change;
					if(engine.colorClearSize < 2) engine.colorClearSize = 36;
					if(engine.colorClearSize > 36) engine.colorClearSize = 2;
					break;
				case 14:
					if (m >= 10) ojamaRate[playerID] += change*100;
					else ojamaRate[playerID] += change*10;
					if(ojamaRate[playerID] < 10) ojamaRate[playerID] = 1000;
					if(ojamaRate[playerID] > 1000) ojamaRate[playerID] = 10;
					break;
				case 15:
					if (m > 10) hurryupSeconds[playerID] += change*m/10;
					else hurryupSeconds[playerID] += change;
					if(hurryupSeconds[playerID] < 0) hurryupSeconds[playerID] = 300;
					if(hurryupSeconds[playerID] > 300) hurryupSeconds[playerID] = 0;
					break;
				case 16:
					ojamaHard[playerID] += change;
					if(ojamaHard[playerID] < 0) ojamaHard[playerID] = 9;
					if(ojamaHard[playerID] > 9) ojamaHard[playerID] = 0;
					break;
				case 17:
					dangerColumnDouble[playerID] = !dangerColumnDouble[playerID];
					break;
				case 18:
					dangerColumnShowX[playerID] = !dangerColumnShowX[playerID];
					break;
				case 19:
					ojamaCountdown[playerID] += change;
					if(ojamaCountdown[playerID] < 0) ojamaCountdown[playerID] = 9;
					if(ojamaCountdown[playerID] > 9) ojamaCountdown[playerID] = 0;
					break;
				case 20:
					zenKeshiType[playerID] += change;
					if(zenKeshiType[playerID] < 0) zenKeshiType[playerID] = 2;
					if(zenKeshiType[playerID] > 2) zenKeshiType[playerID] = 0;
					break;
				case 21:
					feverMapSet[playerID] += change;
					if(feverMapSet[playerID] < 0) feverMapSet[playerID] = FEVER_MAPS.length-1;
					if(feverMapSet[playerID] >= FEVER_MAPS.length) feverMapSet[playerID] = 0;
					break;
				case 22:
					outlineType[playerID] += change;
					if(outlineType[playerID] < 0) outlineType[playerID] = 2;
					if(outlineType[playerID] > 2) outlineType[playerID] = 0;
					break;
				case 23:
					chainDisplayType[playerID] += change;
					if(chainDisplayType[playerID] < 0) chainDisplayType[playerID] = 3;
					if(chainDisplayType[playerID] > 3) chainDisplayType[playerID] = 0;
					break;
				case 24:
					cascadeSlow[playerID] = !cascadeSlow[playerID];
					break;
				case 25:
					newChainPower[playerID] = !newChainPower[playerID];
					break;
				case 26:
					useMap[playerID] = !useMap[playerID];
					if(!useMap[playerID]) {
						if(engine.field != null) engine.field.reset();
					} else {
						loadMapPreview(engine, playerID, (mapNumber[playerID] < 0) ? 0 : mapNumber[playerID], true);
					}
					break;
				case 27:
					mapSet[playerID] += change;
					if(mapSet[playerID] < 0) mapSet[playerID] = 99;
					if(mapSet[playerID] > 99) mapSet[playerID] = 0;
					if(useMap[playerID]) {
						mapNumber[playerID] = -1;
						loadMapPreview(engine, playerID, (mapNumber[playerID] < 0) ? 0 : mapNumber[playerID], true);
					}
					break;
				case 28:
					if(useMap[playerID]) {
						mapNumber[playerID] += change;
						if(mapNumber[playerID] < -1) mapNumber[playerID] = mapMaxNo[playerID] - 1;
						if(mapNumber[playerID] > mapMaxNo[playerID] - 1) mapNumber[playerID] = -1;
						loadMapPreview(engine, playerID, (mapNumber[playerID] < 0) ? 0 : mapNumber[playerID], true);
					} else {
						mapNumber[playerID] = -1;
					}
					break;
				case 29:
					bigDisplay = !bigDisplay;
					break;
				case 30:
					bgmno += change;
					if(bgmno < 0) bgmno = BGMStatus.BGM_COUNT - 1;
					if(bgmno > BGMStatus.BGM_COUNT - 1) bgmno = 0;
					break;
				case 31:
					enableSE[playerID] = !enableSE[playerID];
					break;
				case 32:
				case 33:
					presetNumber[playerID] += change;
					if(presetNumber[playerID] < 0) presetNumber[playerID] = 99;
					if(presetNumber[playerID] > 99) presetNumber[playerID] = 0;
					break;
				}
			}

			// 決定
			if(engine.ctrl.isPush(Controller.BUTTON_A) && (menuTime >= 5)) {
				engine.playSE("decide");

				if(menuCursor == 32) {
					loadPreset(engine, owner.modeConfig, presetNumber[playerID], "bombbattle");
				} else if(menuCursor == 33) {
					savePreset(engine, owner.modeConfig, presetNumber[playerID], "bombbattle");
					receiver.saveModeConfig(owner.modeConfig);
				} else {
					saveOtherSetting(engine, owner.modeConfig);
					savePreset(engine, owner.modeConfig, -1 - playerID, "bombbattle");
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

			if(menuTime >= 240)
				engine.statc[4] = 1;
			else if(menuTime >= 180)
				menuCursor = 26;
			else if(menuTime >= 120)
				menuCursor = 17;
			else if(menuTime >= 60)
				menuCursor = 9;
		} else {
			// 開始
			if((owner.engine[0].statc[4] == 1) && (owner.engine[1].statc[4] == 1) && (playerID == 1)) {
				owner.engine[0].stat = GameEngine.Status.READY;
				owner.engine[1].stat = GameEngine.Status.READY;
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
			} else if(menuCursor < 17) {
				drawMenu(engine, playerID, receiver, 0, EventReceiver.COLOR_CYAN, 9,
						"COUNTER", OJAMA_COUNTER_STRING[ojamaCounterMode[playerID]],
						"MAX ATTACK", String.valueOf(maxAttack[playerID]),
						"COLORS", String.valueOf(numColors[playerID]),
						"MIN CHAIN", String.valueOf(rensaShibari[playerID]),
						"CLEAR SIZE", String.valueOf(engine.colorClearSize),
						"OJAMA RATE", String.valueOf(ojamaRate[playerID]),
						"HURRYUP", (hurryupSeconds[playerID] == 0) ? "NONE" : hurryupSeconds[playerID]+"SEC",
						"HARD OJAMA", String.valueOf(ojamaHard[playerID]));

				receiver.drawMenuFont(engine, playerID, 0, 19, "PAGE 2/4", EventReceiver.COLOR_YELLOW);
			} else if(menuCursor < 26) {
				initMenu(EventReceiver.COLOR_CYAN, 17);
				drawMenu(engine, playerID, receiver,
						"X COLUMN", dangerColumnDouble[playerID] ? "3 AND 4" : "3 ONLY",
						"X SHOW", GeneralUtil.getONorOFF(dangerColumnShowX[playerID]),
						"COUNTDOWN", String.valueOf(ojamaCountdown[playerID]),
						"ZENKESHI", ZENKESHI_TYPE_NAMES[zenKeshiType[playerID]]);
				menuColor = zenKeshiType[playerID] == ZENKESHI_MODE_FEVER ?
						EventReceiver.COLOR_PURPLE : EventReceiver.COLOR_WHITE;
				drawMenu(engine, playerID, receiver,
						"F-MAP SET", FEVER_MAPS[feverMapSet[playerID]].toUpperCase());
				menuColor = EventReceiver.COLOR_DARKBLUE;
				drawMenu(engine, playerID, receiver,
						"OUTLINE", OUTLINE_TYPE_NAMES[outlineType[playerID]],
						"SHOW CHAIN", CHAIN_DISPLAY_NAMES[chainDisplayType[playerID]],
						"FALL ANIM", cascadeSlow[playerID] ? "FEVER" : "CLASSIC");
				menuColor = EventReceiver.COLOR_CYAN;
				drawMenu(engine, playerID, receiver,
						"CHAINPOWER", newChainPower[playerID] ? "FEVER" : "CLASSIC");

				receiver.drawMenuFont(engine, playerID, 0, 19, "PAGE 3/4", EventReceiver.COLOR_YELLOW);
			} else {
				initMenu(EventReceiver.COLOR_PINK, 26);
				drawMenu(engine, playerID, receiver,
						"USE MAP", GeneralUtil.getONorOFF(useMap[playerID]),
						"MAP SET", String.valueOf(mapSet[playerID]),
						"MAP NO.", (mapNumber[playerID] < 0) ? "RANDOM" : mapNumber[playerID]+"/"+(mapMaxNo[playerID]-1),
						"BIG DISP", GeneralUtil.getONorOFF(bigDisplay));
				menuColor = EventReceiver.COLOR_DARKBLUE;
				drawMenu(engine, playerID, receiver,
						"BGM", String.valueOf(bgmno),
						"SE", GeneralUtil.getONorOFF(enableSE[playerID]));
				menuColor = EventReceiver.COLOR_GREEN;
				drawMenu(engine, playerID, receiver,
						"LOAD", String.valueOf(presetNumber[playerID]),
						"SAVE", String.valueOf(presetNumber[playerID]));

				receiver.drawMenuFont(engine, playerID, 0, 19, "PAGE 4/4", EventReceiver.COLOR_YELLOW);
			}
		} else {
			receiver.drawMenuFont(engine, playerID, 3, 10, "WAIT", EventReceiver.COLOR_YELLOW);
		}
	}

	/*
	 * When the current piece is in action
	 */
	@Override
	public void renderMove(GameEngine engine, int playerID) {
		if(engine.gameStarted)
			drawX(engine, playerID);
	}

	/*
	 * Render score
	 */
	@Override
	public void renderLast(GameEngine engine, int playerID) {
		int fldPosX = receiver.getFieldDisplayPositionX(engine, playerID);
		int fldPosY = receiver.getFieldDisplayPositionY(engine, playerID);
		int playerColor = (playerID == 0) ? EventReceiver.COLOR_RED : EventReceiver.COLOR_BLUE;
		int fontColor = EventReceiver.COLOR_WHITE;

		// Timer
		if(playerID == 0) {
			receiver.drawDirectFont(engine, playerID, 224, 8, GeneralUtil.getTime(engine.statistics.time));
		}

		// Ojama Counter
		fontColor = EventReceiver.COLOR_WHITE;
		if(ojama[playerID] >= 1) fontColor = EventReceiver.COLOR_YELLOW;
		if(ojama[playerID] >= 3) fontColor = EventReceiver.COLOR_ORANGE;
		if(ojama[playerID] >= 6) fontColor = EventReceiver.COLOR_RED;

		String strOjama = ojama[playerID]/6 + " " + ojama[playerID]%6 + "/6";
		if(ojamaAdd[playerID] > 0)
			strOjama = strOjama + "(+" + ojamaAdd[playerID]/6 + " " + ojamaAdd[playerID]%6 + "/6" + ")";

		if((ojama[playerID] > 0) || (ojamaAdd[playerID] > 0)) {
			receiver.drawDirectFont(engine, playerID, fldPosX + 4, fldPosY + 32, strOjama, fontColor);
		}

		// Score
		String strScoreMultiplier = "";
		if((lastscore[playerID] != 0) && (lastmultiplier[playerID] != 0) && (scgettime[playerID] > 0))
			strScoreMultiplier = "(" + lastscore[playerID] + "e" + lastmultiplier[playerID] + ")";

		if(engine.displaysize == 1) {
			receiver.drawDirectFont(engine, playerID, fldPosX + 4, fldPosY + 440, String.format("%12d", score[playerID]), playerColor);
			receiver.drawDirectFont(engine, playerID, fldPosX + 4, fldPosY + 456, String.format("%12s", strScoreMultiplier), playerColor);
		} else if(engine.gameStarted) {
			receiver.drawDirectFont(engine, playerID, fldPosX - 28, fldPosY + 248, String.format("%8d", score[playerID]), playerColor);
			receiver.drawDirectFont(engine, playerID, fldPosX - 28, fldPosY + 264, String.format("%8s", strScoreMultiplier), playerColor);
		}

		if((engine.stat != GameEngine.Status.MOVE) && (engine.stat != GameEngine.Status.RESULT) && (engine.gameStarted)) {
			drawX(engine, playerID);
		}

		if((engine.stat != GameEngine.Status.RESULT) && (engine.gameStarted)) {
			if (engine.field != null)
				for (int x = 0; x < engine.field.getWidth(); x++)
					for (int y = 0; y < engine.field.getHeight(); y++)
					{
						Block b = engine.field.getBlock(x, y);
						if (b == null)
							continue;
						if (b.isEmpty())
							continue;
						if (b.hard > 0) {
							if(engine.displaysize == 1)
								receiver.drawMenuFont(engine, playerID, x * 2, y * 2, String.valueOf(b.hard), EventReceiver.COLOR_YELLOW, 2.0f);
							else
								receiver.drawMenuFont(engine, playerID, x, y, String.valueOf(b.hard), EventReceiver.COLOR_YELLOW);
						}
						if (b.countdown > 0) {
							if(engine.displaysize == 1)
								receiver.drawMenuFont(engine, playerID, x * 2, y * 2, String.valueOf(b.countdown), EventReceiver.COLOR_RED, 2.0f);
							else
								receiver.drawMenuFont(engine, playerID, x, y, String.valueOf(b.countdown), EventReceiver.COLOR_RED);
						}
					}
		}

		super.renderLast(engine, playerID);
	}

	@Override
	public boolean lineClearEnd(GameEngine engine, int playerID) {
		int enemyID = 0;
		if(playerID == 0) enemyID = 1;
		if (ojamaAdd[enemyID] > 0)
		{
			ojama[enemyID] += ojamaAdd[enemyID];
			ojamaAdd[enemyID] = 0;
		}
		if (zenKeshi[playerID] && zenKeshiType[playerID] == ZENKESHI_MODE_FEVER)
		{
			loadFeverMap(engine, playerID, 4);
			zenKeshi[playerID] = false;
			zenKeshiDisplay[playerID] = 120;
		}
		//Drop garbage if needed.
		if (ojama[playerID] >= 6 && !ojamaDrop[playerID] && (!cleared[playerID] ||
				(ojamaCounterMode[playerID] != OJAMA_COUNTER_FEVER)))
		{
			ojamaDrop[playerID] = true;
			int drop = Math.min(ojama[playerID]/6, maxAttack[playerID]);
			ojama[playerID] -= drop*6;
			engine.field.garbageDrop(engine, drop, false, 0, ojamaCountdown[playerID]);
			engine.field.setAllSkin(engine.getSkin());
			return true;
		}
		//Decrement bomb blocks' countdowns and explode those that hit 0.
		for (int y = (engine.field.getHiddenHeight() * -1); y < engine.field.getHeight(); y++)
			for (int x = 0; x < engine.field.getWidth(); x++)
			{
				Block b = engine.field.getBlock(x, y);
				if (b == null)
					continue;
				else if (b.isEmpty())
					continue;
				else if (b.countdown > 1)
					b.countdown--;
				else if (b.countdown == 1)
					explode(engine, playerID, x, y);
			}
		//Check for game over
		gameOverCheck(engine, playerID);
		return false;
	}

	private void explode(GameEngine engine, int playerID, int x, int y)
	{
		Block b = engine.field.getBlock(x, y);
		if (b == null)
			return;
		b.countdown = 0;
		for (int x2 = x-1; x2 <= x+1; x2++)
			for (int y2 = y-1; y2 <= y+1; y2++)
			{
				Block b2 = engine.field.getBlock(x2, y2);
				if (b2 == null)
					continue;
				if (b2.isEmpty())
					continue;
				if (b2.countdown > 0)
					explode(engine, playerID, x2, y2);
				b2.color = Block.BLOCK_COLOR_GRAY;
				b2.setAttribute(Block.BLOCK_ATTRIBUTE_GARBAGE, true);
				b2.hard = ojamaHard[playerID];

				if(engine.displaysize == 1) {
					owner.receiver.blockBreak(engine, playerID, 2*x2,   2*y2,   b2);
					owner.receiver.blockBreak(engine, playerID, 2*x2+1, 2*y2,   b2);
					owner.receiver.blockBreak(engine, playerID, 2*x2,   2*y2+1, b2);
					owner.receiver.blockBreak(engine, playerID, 2*x2+1, 2*y2+1, b2);
				} else {
					owner.receiver.blockBreak(engine, playerID, x2, y2, b2);
				}
			}
	}

	/*
	 * Called after every frame
	 */
	@Override
	public void onLast(GameEngine engine, int playerID) {
		super.onLast(engine, playerID);
		updateOjamaMeter(engine, playerID);
	}

	@Override
	protected void updateOjamaMeter (GameEngine engine, int playerID) {
		int width = 6;
		if (engine.field != null)
			width = engine.field.getWidth();
		width *= 6;
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

	/*
	 * Called when saving replay
	 */
	@Override
	public void saveReplay(GameEngine engine, int playerID, CustomProperties prop) {
		saveOtherSetting(engine, owner.replayProp);
		savePreset(engine, owner.replayProp, -1 - playerID, "bombbattle");

		if(useMap[playerID] && (fldBackup[playerID] != null)) {
			saveMap(fldBackup[playerID], owner.replayProp, playerID);
		}

		owner.replayProp.setProperty("avalanchevs.version", version);
	}
}
