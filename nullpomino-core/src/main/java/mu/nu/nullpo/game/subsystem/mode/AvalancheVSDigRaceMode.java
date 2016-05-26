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
import mu.nu.nullpo.game.event.EventReceiver;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.util.CustomProperties;
import mu.nu.nullpo.util.GeneralUtil;

/**
 * AVALANCHE VS DIG RACE mode (Release Candidate 1)
 */
public class AvalancheVSDigRaceMode extends AvalancheVSDummyMode {
	/** Current version */
	private static final int CURRENT_VERSION = 0;

	/** Version */
	private int version;

	/** Ojama handicap to start with */
	private int[] handicapRows;

	/*
	 * Mode name
	 */
	@Override
	public String getName() {
		return "AVALANCHE VS DIG RACE (RC1)";
	}

	/*
	 * Mode initialization
	 */
	@Override
	public void modeInit(GameManager manager) {
		super.modeInit(manager);
		handicapRows = new int[MAX_PLAYERS];
	}

	/**
	 * Load settings not related to speeds
	 * @param engine GameEngine
	 * @param prop Property file to read from
	 */
	private void loadOtherSetting(GameEngine engine, CustomProperties prop) {
		super.loadOtherSetting(engine, prop, "digrace");
		int playerID = engine.playerID;
		ojamaRate[playerID] = prop.getProperty("avalanchevsdigrace.ojamaRate.p" + playerID, 420);
		ojamaHard[playerID] = prop.getProperty("avalanchevsdigrace.ojamaHard.p" + playerID, 0);
		handicapRows[playerID] = prop.getProperty("avalanchevsdigrace.ojamaHandicap.p" + playerID, 6);
	}

	/**
	 * Save settings not related to speeds
	 * @param engine GameEngine
	 * @param prop Property file to save to
	 */
	private void saveOtherSetting(GameEngine engine, CustomProperties prop) {
		super.saveOtherSetting(engine, prop, "digrace");
		int playerID = engine.playerID;
		prop.setProperty("avalanchevsdigrace.ojamaHandicap.p" + playerID, handicapRows[playerID]);
	}

	/*
	 * Initialization for each player
	 */
	@Override
	public void playerInit(GameEngine engine, int playerID) {
		owner = engine.owner;
		receiver = engine.owner.receiver;
		useMap[playerID] = false;
		feverMapSet[playerID] = -1;

		if(engine.owner.replayMode == false) {
			loadOtherSetting(engine, engine.owner.modeConfig);
			loadPreset(engine, engine.owner.modeConfig, -1 - playerID, "digrace");
			version = CURRENT_VERSION;
		} else {
			loadOtherSetting(engine, engine.owner.replayProp);
			loadPreset(engine, engine.owner.replayProp, -1 - playerID, "digrace");
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
			int change = updateCursor(engine, 28);

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
					newChainPower[playerID] = !newChainPower[playerID];
					break;
				case 20:
					engine.colorClearSize += change;
					if(engine.colorClearSize < 2) engine.colorClearSize = 36;
					if(engine.colorClearSize > 36) engine.colorClearSize = 2;
					break;
				case 21:
					outlineType[playerID] += change;
					if(outlineType[playerID] < 0) outlineType[playerID] = 2;
					if(outlineType[playerID] > 2) outlineType[playerID] = 0;
					break;
				case 22:
					chainDisplayType[playerID] += change;
					if(chainDisplayType[playerID] < 0) chainDisplayType[playerID] = 3;
					if(chainDisplayType[playerID] > 3) chainDisplayType[playerID] = 0;
					break;
				case 23:
					cascadeSlow[playerID] = !cascadeSlow[playerID];
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
					bigDisplay = !bigDisplay;
					break;
				case 27:
				case 28:
					presetNumber[playerID] += change;
					if(presetNumber[playerID] < 0) presetNumber[playerID] = 99;
					if(presetNumber[playerID] > 99) presetNumber[playerID] = 0;
					break;
				}
			}

			// 決定
			if(engine.ctrl.isPush(Controller.BUTTON_A) && (menuTime >= 5)) {
				engine.playSE("decide");

				if(menuCursor == 27) {
					loadPreset(engine, owner.modeConfig, presetNumber[playerID], "digrace");
				} else if(menuCursor == 28) {
					savePreset(engine, owner.modeConfig, presetNumber[playerID], "digrace");
					receiver.saveModeConfig(owner.modeConfig);
				} else {
					saveOtherSetting(engine, owner.modeConfig);
					savePreset(engine, owner.modeConfig, -1 - playerID, "digrace");
					receiver.saveModeConfig(owner.modeConfig);
					engine.statc[4] = 1;
				}
			}

			// Cancel
			if(engine.ctrl.isPush(Controller.BUTTON_B)) {
				engine.quitflag = true;
			}
			menuTime++;
		} else if(engine.statc[4] == 0) {
			menuTime++;
			menuCursor = 0;

			if(menuTime >= 180)
				engine.statc[4] = 1;
			else if(menuTime >= 120)
				menuCursor = 18;
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

				receiver.drawMenuFont(engine, playerID, 0, 19, "PAGE 1/3", EventReceiver.COLOR_YELLOW);
			} else if(menuCursor < 18) {
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
				initMenu(EventReceiver.COLOR_PURPLE, 18);
				drawMenu(engine, playerID, receiver, "ROWS", String.valueOf(handicapRows[playerID]));
				menuColor = EventReceiver.COLOR_CYAN;
				drawMenu(engine, playerID, receiver,
						"CHAINPOWER", newChainPower[playerID] ? "FEVER" : "CLASSIC",
						"CLEAR SIZE", String.valueOf(engine.colorClearSize));
				menuColor = EventReceiver.COLOR_DARKBLUE;
				drawMenu(engine, playerID, receiver,
						"OUTLINE", OUTLINE_TYPE_NAMES[outlineType[playerID]],
						"SHOW CHAIN", CHAIN_DISPLAY_NAMES[chainDisplayType[playerID]],
						"FALL ANIM", cascadeSlow[playerID] ? "FEVER" : "CLASSIC");
				menuColor = EventReceiver.COLOR_PINK;
				drawMenuCompact(engine, playerID, receiver, "BGM", String.valueOf(bgmno));
				menuColor = EventReceiver.COLOR_YELLOW;
				drawMenuCompact(engine, playerID, receiver, "SE", GeneralUtil.getONorOFF(enableSE[playerID]));
				menuColor = EventReceiver.COLOR_PINK;
				drawMenu(engine, playerID, receiver, "BIG DISP", GeneralUtil.getONorOFF(bigDisplay));
				menuColor = EventReceiver.COLOR_GREEN;
				drawMenuCompact(engine, playerID, receiver,
						"LOAD", String.valueOf(presetNumber[playerID]),
						"SAVE", String.valueOf(presetNumber[playerID]));
				
				receiver.drawMenuFont(engine, playerID, 0, 19, "PAGE 3/3", EventReceiver.COLOR_YELLOW);
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
			engine.numColors = numColors[playerID];
			engine.lineGravityType = cascadeSlow[playerID] ? GameEngine.LineGravity.CASCADE_SLOW : GameEngine.LineGravity.CASCADE;
			engine.rainbowAnimate = true;
			engine.displaysize = bigDisplay ? 1 : 0;

			if(outlineType[playerID] == 0) engine.blockOutlineType = GameEngine.BLOCK_OUTLINE_NORMAL;
			if(outlineType[playerID] == 1) engine.blockOutlineType = GameEngine.BLOCK_OUTLINE_SAMECOLOR;
			if(outlineType[playerID] == 2) engine.blockOutlineType = GameEngine.BLOCK_OUTLINE_NONE;

			if(engine.field != null)
				engine.field.reset();
		}

		return false;
	}

	/*
	 * Called at game start
	 */
	@Override
	public void startGame(GameEngine engine, int playerID) {
		super.startGame(engine, playerID);

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
		int sizeLimit = Math.max(engine.colorClearSize-1, 2);
		do {
			for (int i = 0; i < handicapRows[playerID]; i++)
				for (int j = 0; j < width; j++)
					if (engine.field.getBlockEmpty(j, y-i))
						engine.field.setBlockColor(j, y-i, BLOCK_COLORS[rand.nextInt(numColors[playerID])]);
		} while (engine.field.clearColor(sizeLimit, false, false, true) > 0);
		engine.field.setAllAttribute(Block.BLOCK_ATTRIBUTE_VISIBLE, true);
		engine.field.setAllSkin(engine.getSkin());
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
		if(ojama[playerID] >= 6) fontColor = EventReceiver.COLOR_ORANGE;
		if(ojama[playerID] >= 12) fontColor = EventReceiver.COLOR_RED;

		String strOjama = String.valueOf(ojama[playerID]);
		if(ojamaAdd[playerID] > 0)
			strOjama = strOjama + "(+" + String.valueOf(ojamaAdd[playerID]) + ")";

		if(!strOjama.equals("0")) {
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

		if (!owner.engine[playerID].gameActive)
			return;
		if((engine.stat != GameEngine.Status.MOVE) && (engine.stat != GameEngine.Status.RESULT) && (engine.gameStarted))
			drawX(engine, playerID);
		drawHardOjama(engine, playerID);

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
				engine.stat = GameEngine.Status.GAMEOVER;
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

		updateOjamaMeter(engine, playerID);

		// 決着
		if((playerID == 1) && (owner.engine[0].gameActive)) {
			boolean p1Lose = (owner.engine[0].stat == GameEngine.Status.GAMEOVER);
			if (!p1Lose && owner.engine[1].field != null && owner.engine[1].stat != GameEngine.Status.READY)
				p1Lose = (owner.engine[1].field.getHowManyGems() == 0);
			boolean p2Lose = (owner.engine[1].stat == GameEngine.Status.GAMEOVER);
			if (!p2Lose && owner.engine[0].field != null && owner.engine[0].stat != GameEngine.Status.READY)
				p2Lose = (owner.engine[0].field.getHowManyGems() == 0);
			if(p1Lose && p2Lose) {
				// Draw
				winnerID = -1;
				owner.engine[0].stat = GameEngine.Status.GAMEOVER;
				owner.engine[1].stat = GameEngine.Status.GAMEOVER;
			} else if(p2Lose && !p1Lose) {
				// 1P win
				winnerID = 0;
				owner.engine[0].stat = GameEngine.Status.EXCELLENT;
				owner.engine[1].stat = GameEngine.Status.GAMEOVER;
			} else if(p1Lose && !p2Lose) {
				// 2P win
				winnerID = 1;
				owner.engine[0].stat = GameEngine.Status.GAMEOVER;
				owner.engine[1].stat = GameEngine.Status.EXCELLENT;
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
	 * Called when saving replay
	 */
	@Override
	public void saveReplay(GameEngine engine, int playerID, CustomProperties prop) {
		saveOtherSetting(engine, owner.replayProp);
		savePreset(engine, owner.replayProp, -1 - playerID, "digrace");

		owner.replayProp.setProperty("avalanchevsdigrace.version", version);
	}
}
