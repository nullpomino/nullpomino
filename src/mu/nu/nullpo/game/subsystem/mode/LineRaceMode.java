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

import java.io.IOException;

import org.apache.log4j.Logger;

import mu.nu.nullpo.game.component.BGMStatus;
import mu.nu.nullpo.game.component.Controller;
import mu.nu.nullpo.game.component.Field;
import mu.nu.nullpo.game.event.EventReceiver;
import mu.nu.nullpo.game.net.NetPlayerClient;
import mu.nu.nullpo.game.net.NetPlayerInfo;
import mu.nu.nullpo.game.net.NetRoomInfo;
import mu.nu.nullpo.game.net.NetUtil;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.gui.net.NetLobbyFrame;
import mu.nu.nullpo.util.CustomProperties;
import mu.nu.nullpo.util.GeneralUtil;

/**
 * LINE RACEMode
 */
public class LineRaceMode extends NetDummyMode {
	/** Logger */
	static Logger log = Logger.getLogger(LineRaceMode.class);

	/** Number of entries in rankings */
	private static final int RANKING_MAX = 10;

	/** Target line count type */
	private static final int GOALTYPE_MAX = 3;

	/** Target line count constants */
	private static final int[] GOAL_TABLE = {20, 40, 100};

	/** Drawing and event handling EventReceiver */
	private EventReceiver receiver;

	/** BGM number */
	private int bgmno;

	/** Big */
	private boolean big;

	/** Target line count type（0=20,1=40,2=100） */
	private int goaltype;

	/** Last preset number used */
	private int presetNumber;

	/** Current round's ranking rank */
	private int rankingRank;

	/** Rankings' times */
	private int[][] rankingTime;

	/** Rankings' piece counts */
	private int[][] rankingPiece;

	/** Rankings' PPS values */
	private float[][] rankingPPS;

	/** NET: true if netplay */
	private boolean netIsNetPlay;

	/** NET: true if watch mode */
	private boolean netIsWatch;

	/** NET: Current room info */
	private NetRoomInfo netCurrentRoomInfo;

	/** NET: Number of spectators */
	private int netNumSpectators;

	/*
	 * Mode name
	 */
	@Override
	public String getName() {
		return "LINE RACE";
	}

	/*
	 * Mode Initialization
	 */
	@Override
	public void modeInit(GameManager manager) {
		super.modeInit(manager);
		netIsNetPlay = false;
		netIsWatch = false;
		netNumSpectators = 0;
	}

	/*
	 * NET: Netplay Initialization
	 */
	@Override
	public void netlobbyOnInit(NetLobbyFrame lobby) {
		super.netlobbyOnInit(lobby);
		netIsNetPlay = true;
	}

	/*
	 * Initialization for each player
	 */
	@Override
	public void playerInit(GameEngine engine, int playerID) {
		owner = engine.owner;
		receiver = engine.owner.receiver;

		bgmno = 0;
		big = false;
		goaltype = 0;
		presetNumber = 0;

		rankingRank = -1;
		rankingTime = new int[GOALTYPE_MAX][RANKING_MAX];
		rankingPiece = new int[GOALTYPE_MAX][RANKING_MAX];
		rankingPPS = new float[GOALTYPE_MAX][RANKING_MAX];

		engine.framecolor = GameEngine.FRAME_COLOR_RED;

		if(engine.owner.replayMode == false) {
			presetNumber = engine.owner.modeConfig.getProperty("linerace.presetNumber", 0);
			loadPreset(engine, engine.owner.modeConfig, -1);
			loadRanking(owner.modeConfig, engine.ruleopt.strRuleName);
		} else {
			presetNumber = 0;
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
		engine.speed.gravity = prop.getProperty("linerace.gravity." + preset, 4);
		engine.speed.denominator = prop.getProperty("linerace.denominator." + preset, 256);
		engine.speed.are = prop.getProperty("linerace.are." + preset, 24);
		engine.speed.areLine = prop.getProperty("linerace.areLine." + preset, 24);
		engine.speed.lineDelay = prop.getProperty("linerace.lineDelay." + preset, 40);
		engine.speed.lockDelay = prop.getProperty("linerace.lockDelay." + preset, 30);
		engine.speed.das = prop.getProperty("linerace.das." + preset, 14);
		bgmno = prop.getProperty("linerace.bgmno." + preset, 0);
		big = prop.getProperty("linerace.big." + preset, false);
		goaltype = prop.getProperty("linerace.goaltype." + preset, 1);
	}

	/**
	 * Presetを保存
	 * @param engine GameEngine
	 * @param prop Property file to save to
	 * @param preset Preset number
	 */
	private void savePreset(GameEngine engine, CustomProperties prop, int preset) {
		prop.setProperty("linerace.gravity." + preset, engine.speed.gravity);
		prop.setProperty("linerace.denominator." + preset, engine.speed.denominator);
		prop.setProperty("linerace.are." + preset, engine.speed.are);
		prop.setProperty("linerace.areLine." + preset, engine.speed.areLine);
		prop.setProperty("linerace.lineDelay." + preset, engine.speed.lineDelay);
		prop.setProperty("linerace.lockDelay." + preset, engine.speed.lockDelay);
		prop.setProperty("linerace.das." + preset, engine.speed.das);
		prop.setProperty("linerace.bgmno." + preset, bgmno);
		prop.setProperty("linerace.big." + preset, big);
		prop.setProperty("linerace.goaltype." + preset, goaltype);
	}

	/*
	 * Called at settings screen
	 */
	@Override
	public boolean onSetting(GameEngine engine, int playerID) {
		// Menu
		if(engine.owner.replayMode == false) {
			// Up
			if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_UP) && (!netIsWatch)) {
				engine.statc[2]--;
				if(engine.statc[2] < 0) engine.statc[2] = 11;
				engine.playSE("cursor");

				// NET: Signal cursor change
				if(netIsNetPlay && (netNumSpectators > 0)) {
					netLobby.netPlayerClient.send("game\tcursor\t" + engine.statc[2] + "\n");
				}
			}
			// Down
			if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_DOWN) && (!netIsWatch)) {
				engine.statc[2]++;
				if(engine.statc[2] > 11) engine.statc[2] = 0;
				engine.playSE("cursor");

				// NET: Signal cursor change
				if(netIsNetPlay && (netNumSpectators > 0)) {
					netLobby.netPlayerClient.send("game\tcursor\t" + engine.statc[2] + "\n");
				}
			}

			// Configuration changes
			int change = 0;
			if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_LEFT)) change = -1;
			if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_RIGHT)) change = 1;
			if(netIsWatch) change = 0;

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
					goaltype += change;
					if(goaltype < 0) goaltype = 2;
					if(goaltype > 2) goaltype = 0;
					break;
				case 10:
				case 11:
					presetNumber += change;
					if(presetNumber < 0) presetNumber = 99;
					if(presetNumber > 99) presetNumber = 0;
					break;
				}

				// NET: Signal options change
				if(netIsNetPlay && (netNumSpectators > 0)) {
					String msg = "game\toption\t";
					msg += engine.speed.gravity + "\t" + engine.speed.denominator + "\t" + engine.speed.are + "\t";
					msg += engine.speed.areLine + "\t" + engine.speed.lineDelay + "\t" + engine.speed.lockDelay + "\t";
					msg += engine.speed.das + "\t" + bgmno + "\t" + big + "\t" + goaltype + "\t" + presetNumber;
					msg += "\n";
					netLobby.netPlayerClient.send(msg);
				}
			}

			// 決定
			if(engine.ctrl.isPush(Controller.BUTTON_A) && (engine.statc[3] >= 5) && (!netIsWatch)) {
				engine.playSE("decide");

				if(engine.statc[2] == 10) {
					loadPreset(engine, owner.modeConfig, presetNumber);
				} else if(engine.statc[2] == 11) {
					savePreset(engine, owner.modeConfig, presetNumber);
					receiver.saveModeConfig(owner.modeConfig);
				} else {
					// Save settings
					owner.modeConfig.setProperty("linerace.presetNumber", presetNumber);
					savePreset(engine, owner.modeConfig, -1);
					receiver.saveModeConfig(owner.modeConfig);

					// NET: Signal start of the game
					if(netIsNetPlay) netLobby.netPlayerClient.send("start1p\n");

					return false;
				}
			}

			// Cancel
			if(engine.ctrl.isPush(Controller.BUTTON_B) && (!netIsNetPlay)) {
				engine.quitflag = true;
			}

			engine.statc[3]++;
		} else {
			engine.statc[3]++;
			engine.statc[2] = -1;

			if(engine.statc[3] >= 60) {
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
		if(engine.statc[2] < 10) {
			if(!owner.replayMode) {
				receiver.drawMenuFont(engine, playerID, 0, (engine.statc[2] * 2) + 1, "b", EventReceiver.COLOR_RED);
			}

			receiver.drawMenuFont(engine, playerID, 0, 0, "GRAVITY", EventReceiver.COLOR_BLUE);
			receiver.drawMenuFont(engine, playerID, 1, 1, String.valueOf(engine.speed.gravity), (engine.statc[2] == 0));
			receiver.drawMenuFont(engine, playerID, 0, 2, "G-MAX", EventReceiver.COLOR_BLUE);
			receiver.drawMenuFont(engine, playerID, 1, 3, String.valueOf(engine.speed.denominator), (engine.statc[2] == 1));
			receiver.drawMenuFont(engine, playerID, 0, 4, "ARE", EventReceiver.COLOR_BLUE);
			receiver.drawMenuFont(engine, playerID, 1, 5, String.valueOf(engine.speed.are), (engine.statc[2] == 2));
			receiver.drawMenuFont(engine, playerID, 0, 6, "ARE LINE", EventReceiver.COLOR_BLUE);
			receiver.drawMenuFont(engine, playerID, 1, 7, String.valueOf(engine.speed.areLine), (engine.statc[2] == 3));
			receiver.drawMenuFont(engine, playerID, 0, 8, "LINE DELAY", EventReceiver.COLOR_BLUE);
			receiver.drawMenuFont(engine, playerID, 1, 9, String.valueOf(engine.speed.lineDelay), (engine.statc[2] == 4));
			receiver.drawMenuFont(engine, playerID, 0, 10, "LOCK DELAY", EventReceiver.COLOR_BLUE);
			receiver.drawMenuFont(engine, playerID, 1, 11, String.valueOf(engine.speed.lockDelay), (engine.statc[2] == 5));
			receiver.drawMenuFont(engine, playerID, 0, 12, "DAS", EventReceiver.COLOR_BLUE);
			receiver.drawMenuFont(engine, playerID, 1, 13, String.valueOf(engine.speed.das), (engine.statc[2] == 6));
			receiver.drawMenuFont(engine, playerID, 0, 14, "BGM", EventReceiver.COLOR_BLUE);
			receiver.drawMenuFont(engine, playerID, 1, 15, String.valueOf(bgmno), (engine.statc[2] == 7));
			receiver.drawMenuFont(engine, playerID, 0, 16, "BIG", EventReceiver.COLOR_BLUE);
			receiver.drawMenuFont(engine, playerID, 1, 17, GeneralUtil.getONorOFF(big), (engine.statc[2] == 8));
			receiver.drawMenuFont(engine, playerID, 0, 18, "GOAL", EventReceiver.COLOR_BLUE);
			receiver.drawMenuFont(engine, playerID, 1, 19, String.valueOf(GOAL_TABLE[goaltype]), (engine.statc[2] == 9));
		} else {
			if(!owner.replayMode) {
				receiver.drawMenuFont(engine, playerID, 0, ((engine.statc[2] - 10) * 2) + 1, "b", EventReceiver.COLOR_RED);
			}

			receiver.drawMenuFont(engine, playerID, 0, 0, "LOAD", EventReceiver.COLOR_GREEN);
			receiver.drawMenuFont(engine, playerID, 1, 1, String.valueOf(presetNumber), (engine.statc[2] == 10));
			receiver.drawMenuFont(engine, playerID, 0, 2, "SAVE", EventReceiver.COLOR_GREEN);
			receiver.drawMenuFont(engine, playerID, 1, 3, String.valueOf(presetNumber), (engine.statc[2] == 11));
		}
	}

	/*
	 * Readyの時のInitialization処理
	 */
	@Override
	public void startGame(GameEngine engine, int playerID) {
		engine.big = big;
		owner.bgmStatus.bgm = bgmno;

		engine.meterColor = GameEngine.METER_COLOR_GREEN;
		engine.meterValue = receiver.getMeterMax(engine);
	}

	/*
	 * When the pieces can move
	 */
	@Override
	public boolean onMove(GameEngine engine, int playerID) {
		// NET: Send field
		if((engine.ending == 0) && (engine.statc[0] == 0) && (engine.holdDisable == false) &&
		   (playerID == 0) && (netIsNetPlay) && (!netIsWatch))
		{
			netSendField(engine);
		}
		// NET: Stop game in watch mode
		if(netIsWatch) {
			return true;
		}

		return false;
	}

	/*
	 * When the piece locked
	 */
	@Override
	public void pieceLocked(GameEngine engine, int playerID, int lines) {
		if((engine.ending == 0) && (playerID == 0) && (netIsNetPlay) && (!netIsWatch)) {
			netSendField(engine);
		}
	}

	/*
	 * Line clear
	 */
	@Override
	public boolean onLineClear(GameEngine engine, int playerID) {
		if((engine.statc[0] == 1) && (engine.ending == 0) && (playerID == 0) && (netIsNetPlay) && (!netIsWatch)) {
			netSendField(engine);
		}
		return false;
	}

	/*
	 * Score display
	 */
	@Override
	public void renderLast(GameEngine engine, int playerID) {
		receiver.drawScoreFont(engine, playerID, 0, 0, "LINE RACE", EventReceiver.COLOR_RED);
		receiver.drawScoreFont(engine, playerID, 0, 1, "(" + GOAL_TABLE[goaltype] + " LINES GAME)", EventReceiver.COLOR_RED);

		if( (engine.stat == GameEngine.STAT_SETTING) || ((engine.stat == GameEngine.STAT_RESULT) && (owner.replayMode == false)) ) {
			if(!owner.replayMode && !big && (engine.ai == null) && !netIsWatch) {
				receiver.drawScoreFont(engine, playerID, 3, 3, "TIME     PIECE PPS", EventReceiver.COLOR_BLUE);

				for(int i = 0; i < RANKING_MAX; i++) {
					receiver.drawScoreFont(engine, playerID, 0, 4 + i, String.format("%2d", i + 1), EventReceiver.COLOR_YELLOW);
					receiver.drawScoreFont(engine, playerID, 3, 4 + i, GeneralUtil.getTime(rankingTime[goaltype][i]), (rankingRank == i));
					receiver.drawScoreFont(engine, playerID, 12, 4 + i, String.valueOf(rankingPiece[goaltype][i]), (rankingRank == i));
					receiver.drawScoreFont(engine, playerID, 18, 4 + i, String.format("%.5g", rankingPPS[goaltype][i]), (rankingRank == i));
				}
			}
		} else {
			receiver.drawScoreFont(engine, playerID, 0, 3, "LINE", EventReceiver.COLOR_BLUE);
			int remainLines = GOAL_TABLE[goaltype] - engine.statistics.lines;
			String strLines = String.valueOf(remainLines);
			if(remainLines < 0) strLines = "0";
			int fontcolor = EventReceiver.COLOR_WHITE;
			if((remainLines <= 30) && (remainLines > 0)) fontcolor = EventReceiver.COLOR_YELLOW;
			if((remainLines <= 20) && (remainLines > 0)) fontcolor = EventReceiver.COLOR_ORANGE;
			if((remainLines <= 10) && (remainLines > 0)) fontcolor = EventReceiver.COLOR_RED;
			receiver.drawScoreFont(engine, playerID, 0, 4, strLines, fontcolor);

			receiver.drawScoreFont(engine, playerID, 0, 6, "PIECE", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 0, 7, String.valueOf(engine.statistics.totalPieceLocked));

			receiver.drawScoreFont(engine, playerID, 0, 9, "LINE/MIN", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 0, 10, String.valueOf(engine.statistics.lpm));

			receiver.drawScoreFont(engine, playerID, 0, 12, "PIECE/SEC", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 0, 13, String.valueOf(engine.statistics.pps));

			receiver.drawScoreFont(engine, playerID, 0, 15, "TIME", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 0, 16, GeneralUtil.getTime(engine.statistics.time));
		}

		if(netIsNetPlay) {
			receiver.drawDirectFont(engine, 0, 503, 302, "SPECTATORS", EventReceiver.COLOR_CYAN, 0.5f);
			receiver.drawDirectFont(engine, 0, 503, 310, "" + netNumSpectators, EventReceiver.COLOR_WHITE, 0.5f);

			if(netIsWatch) {
				receiver.drawDirectFont(engine, 0, 503, 318, "WATCH", EventReceiver.COLOR_GREEN, 0.5f);
			} else {
				receiver.drawDirectFont(engine, 0, 503, 318, "PLAY", EventReceiver.COLOR_RED, 0.5f);
			}
		}
	}

	/*
	 * Calculate score
	 */
	@Override
	public void calcScore(GameEngine engine, int playerID, int lines) {
		int remainLines = GOAL_TABLE[goaltype] - engine.statistics.lines;
		engine.meterValue = (remainLines * receiver.getMeterMax(engine)) / GOAL_TABLE[goaltype];

		if(remainLines <= 30) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
		if(remainLines <= 20) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
		if(remainLines <= 10) engine.meterColor = GameEngine.METER_COLOR_RED;

		// All clear
		if((lines >= 1) && (engine.field.isEmpty())) {
			engine.playSE("bravo");
		}

		// ゴール
		if(engine.statistics.lines >= GOAL_TABLE[goaltype]) {
			engine.ending = 1;
			engine.timerActive = false;
			engine.gameActive = false;

			if(netIsNetPlay && !netIsWatch) {
				netSendField(engine);
				netLobby.netPlayerClient.send("game\tending\n");
			}
		} else if(engine.statistics.lines >= GOAL_TABLE[goaltype] - 5) {
			owner.bgmStatus.fadesw = true;
		}
	}

	/*
	 * Game Over
	 */
	@Override
	public boolean onGameOver(GameEngine engine, int playerID) {
		if(netIsNetPlay){
			if(!netIsWatch) {
				if(engine.statc[0] == 0) {
					netSendField(engine);
					netLobby.netPlayerClient.send("dead\t-1\n");
				}
			} else {
				if(engine.statc[0] < engine.field.getHeight() + 1 + 180) {
					return false;
				} else {
					engine.field.reset();
					engine.stat = GameEngine.STAT_RESULT;
					engine.resetStatc();
					return true;
				}
			}
		}

		return false;
	}

	/*
	 * Results screen
	 */
	public boolean onResult(GameEngine engine, int playerID) {
		// NET: Retry
		if(netIsNetPlay) {
			engine.allowTextRenderByReceiver = false;

			// Retry
			if(engine.ctrl.isPush(Controller.BUTTON_A) && !netIsWatch) {
				engine.playSE("decide");
				if(netNumSpectators > 0) netLobby.netPlayerClient.send("game\tretry\n");
				owner.reset();
			}

			return true;
		}

		return false;
	}

	/*
	 * Render results screen
	 */
	@Override
	public void renderResult(GameEngine engine, int playerID) {
		receiver.drawMenuFont(engine, playerID,  0, 1, "PLAY DATA", EventReceiver.COLOR_ORANGE);

		receiver.drawMenuFont(engine, playerID,  0, 3, "LINE", EventReceiver.COLOR_BLUE);
		String strLines = String.format("%10d", engine.statistics.lines);
		receiver.drawMenuFont(engine, playerID,  0, 4, strLines);

		receiver.drawMenuFont(engine, playerID,  0, 5, "PIECE", EventReceiver.COLOR_BLUE);
		String strPiece = String.format("%10d", engine.statistics.totalPieceLocked);
		receiver.drawMenuFont(engine, playerID,  0, 6, strPiece);

		receiver.drawMenuFont(engine, playerID,  0, 7, "TIME", EventReceiver.COLOR_BLUE);
		String strTime = String.format("%10s", GeneralUtil.getTime(engine.statistics.time));
		receiver.drawMenuFont(engine, playerID,  0, 8, strTime);

		receiver.drawMenuFont(engine, playerID,  0, 9, "LINE/MIN", EventReceiver.COLOR_BLUE);
		String strLPM = String.format("%10g", engine.statistics.lpm);
		receiver.drawMenuFont(engine, playerID,  0, 10, strLPM);

		receiver.drawMenuFont(engine, playerID,  0, 11, "PIECE/SEC", EventReceiver.COLOR_BLUE);
		String strPPS = String.format("%10g", engine.statistics.pps);
		receiver.drawMenuFont(engine, playerID,  0, 12, strPPS);

		if(rankingRank != -1) {
			receiver.drawMenuFont(engine, playerID,  0, 13, "RANK", EventReceiver.COLOR_BLUE);
			String strRank = String.format("%10d", rankingRank + 1);
			receiver.drawMenuFont(engine, playerID,  0, 14, strRank);
		}

		if(netIsNetPlay && !netIsWatch) {
			receiver.drawMenuFont(engine, playerID, 1, 18, "A: RETRY", EventReceiver.COLOR_RED);
		}
	}

	/*
	 * リプレイ保存
	 */
	@Override
	public void saveReplay(GameEngine engine, int playerID, CustomProperties prop) {
		savePreset(engine, engine.owner.replayProp, -1);

		// Update rankings
		if((owner.replayMode == false) && (engine.statistics.lines >= GOAL_TABLE[goaltype]) && (big == false) && (engine.ai == null)) {
			updateRanking(engine.statistics.time, engine.statistics.totalPieceLocked, engine.statistics.pps);

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
		for(int i = 0; i < GOALTYPE_MAX; i++) {
			for(int j = 0; j < RANKING_MAX; j++) {
				rankingTime[i][j] = prop.getProperty("linerace.ranking." + ruleName + "." + i + ".time." + j, -1);
				rankingPiece[i][j] = prop.getProperty("linerace.ranking." + ruleName + "." + i + ".piece." + j, 0);
				rankingPPS[i][j] = prop.getProperty("linerace.ranking." + ruleName + "." + i + ".pps." + j, 0f);
			}
		}
	}

	/**
	 * Save rankings to property file
	 * @param prop Property file
	 * @param ruleName Rule name
	 */
	private void saveRanking(CustomProperties prop, String ruleName) {
		for(int i = 0; i < GOALTYPE_MAX; i++) {
			for(int j = 0; j < RANKING_MAX; j++) {
				prop.setProperty("linerace.ranking." + ruleName + "." + i + ".time." + j, rankingTime[i][j]);
				prop.setProperty("linerace.ranking." + ruleName + "." + i + ".piece." + j, rankingPiece[i][j]);
				prop.setProperty("linerace.ranking." + ruleName + "." + i + ".pps." + j, rankingPPS[i][j]);
			}
		}
	}

	/**
	 * Update rankings
	 * @param time Time
	 * @param piece ピースcount
	 */
	private void updateRanking(int time, int piece, float pps) {
		rankingRank = checkRanking(time, piece, pps);

		if(rankingRank != -1) {
			// Shift down ranking entries
			for(int i = RANKING_MAX - 1; i > rankingRank; i--) {
				rankingTime[goaltype][i] = rankingTime[goaltype][i - 1];
				rankingPiece[goaltype][i] = rankingPiece[goaltype][i - 1];
				rankingPPS[goaltype][i] = rankingPPS[goaltype][i - 1];
			}

			// Add new data
			rankingTime[goaltype][rankingRank] = time;
			rankingPiece[goaltype][rankingRank] = piece;
			rankingPPS[goaltype][rankingRank] = pps;
		}
	}

	/**
	 * Calculate ranking position
	 * @param time Time
	 * @param piece ピースcount
	 * @return Position (-1 if unranked)
	 */
	private int checkRanking(int time, int piece, float pps) {
		for(int i = 0; i < RANKING_MAX; i++) {
			if((time < rankingTime[goaltype][i]) || (rankingTime[goaltype][i] < 0)) {
				return i;
			} else if((time == rankingTime[goaltype][i]) && ((piece < rankingPiece[goaltype][i]) || (rankingPiece[goaltype][i] == 0))) {
				return i;
			} else if((time == rankingTime[goaltype][i]) && (piece == rankingPiece[goaltype][i]) && (pps > rankingPPS[goaltype][i])) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * NET: Update player count
	 */
	private void netUpdatePlayerExist() {
		netNumSpectators = 0;

		if((netCurrentRoomInfo.roomID != -1) && (netLobby != null)) {
			for(NetPlayerInfo pInfo: netLobby.updateSameRoomPlayerInfoList()) {
				if(pInfo.roomID == netCurrentRoomInfo.roomID) {
					if(pInfo.seatID == -1) {
						netNumSpectators++;
					}
				}
			}
		}
	}

	/**
	 * NET: Send field to all spectators
	 * @param engine GameEngine
	 */
	private void netSendField(GameEngine engine) {
		if(netNumSpectators < 1) return;

		String strSrcFieldData = engine.field.fieldToString();
		int nocompSize = strSrcFieldData.length();

		String strCompFieldData = NetUtil.compressString(strSrcFieldData);
		int compSize = strCompFieldData.length();

		String strFieldData = strSrcFieldData;
		boolean isCompressed = false;
		if(compSize < nocompSize) {
			strFieldData = strCompFieldData;
			isCompressed = true;
		}
		//log.debug("nocompSize:" + nocompSize + " compSize:" + compSize + " isCompressed:" + isCompressed);

		String msg = "game\tfield\t" + engine.statistics.lines + "\t" + goaltype + "\t";
		msg += engine.getSkin() + "\t" + engine.field.getHighestGarbageBlockY() + "\t";
		msg += engine.field.getHeightWithoutHurryupFloor() + "\t";
		msg += strFieldData + "\t" + isCompressed + "\n";
		netLobby.netPlayerClient.send(msg);
	}

	/*
	 * NET: When you enter a room
	 */
	@Override
	public void netlobbyOnRoomJoin(NetLobbyFrame lobby, NetPlayerClient client, NetRoomInfo roomInfo) {
		super.netlobbyOnRoomJoin(lobby, client, roomInfo);
		netCurrentRoomInfo = roomInfo;
		netIsNetPlay = true;
		netIsWatch = (client.getYourPlayerInfo().seatID == -1);
		netNumSpectators = 0;
		netUpdatePlayerExist();
	}

	/*
	 * NET: Process netplay messages
	 */
	@Override
	public void netlobbyOnMessage(NetLobbyFrame lobby, NetPlayerClient client, String[] message) throws IOException {
		super.netlobbyOnMessage(lobby, client, message);

		// Player status update
		if(message[0].equals("playerupdate")) {
			netUpdatePlayerExist();
		}
		// When someone logout
		if(message[0].equals("playerlogout")) {
			NetPlayerInfo pInfo = new NetPlayerInfo(message[1]);

			if(pInfo.roomID == netCurrentRoomInfo.roomID) {
				netUpdatePlayerExist();
			}
		}
		// Game started
		if(message[0].equals("start")) {
			log.debug("NET: Game started");

			if(netIsWatch) {
				owner.engine[0].stat = GameEngine.STAT_READY;
				owner.engine[0].resetStatc();
			}
		}
		// Dead
		if(message[0].equals("dead")) {
			log.debug("NET: Dead");

			if(netIsWatch) {
				owner.engine[0].stat = GameEngine.STAT_GAMEOVER;
				owner.engine[0].resetStatc();
			}
		}
		// Game messages
		if(message[0].equals("game")) {
			if(netIsWatch) {
				GameEngine engine = owner.engine[0];
				if(engine.field == null) {
					engine.field = new Field();
				}

				// Move cursor
				if(message[3].equals("cursor")) {
					if(engine.stat == GameEngine.STAT_SETTING) {
						engine.statc[2] = Integer.parseInt(message[4]);
						engine.playSE("cursor");
					}
				}
				// Change game options
				if(message[3].equals("option")) {
					engine.speed.gravity = Integer.parseInt(message[4]);
					engine.speed.denominator = Integer.parseInt(message[5]);
					engine.speed.are = Integer.parseInt(message[6]);
					engine.speed.areLine = Integer.parseInt(message[7]);
					engine.speed.lineDelay = Integer.parseInt(message[8]);
					engine.speed.lockDelay = Integer.parseInt(message[9]);
					engine.speed.das = Integer.parseInt(message[10]);
					bgmno = Integer.parseInt(message[11]);
					big = Boolean.parseBoolean(message[12]);
					goaltype = Integer.parseInt(message[13]);
					presetNumber = Integer.parseInt(message[14]);
					engine.playSE("change");
				}
				// Field
				if(message[3].equals("field")) {
					if(message.length > 7) {
						engine.nowPieceObject = null;
						engine.holdDisable = false;
						if(engine.stat == GameEngine.STAT_SETTING) engine.stat = GameEngine.STAT_MOVE;
						engine.statistics.lines = Integer.parseInt(message[4]);
						goaltype = Integer.parseInt(message[5]);
						int skin = Integer.parseInt(message[6]);
						int highestGarbageY = Integer.parseInt(message[7]);
						int highestWallY = Integer.parseInt(message[8]);
						if(message.length > 10) {
							String strFieldData = message[9];
							boolean isCompressed = Boolean.parseBoolean(message[10]);
							if(isCompressed) {
								strFieldData = NetUtil.decompressString(strFieldData);
							}
							engine.field.stringToField(strFieldData, skin, highestGarbageY, highestWallY);
						} else {
							engine.field.reset();
						}

						int remainLines = GOAL_TABLE[goaltype] - engine.statistics.lines;
						engine.meterValue = (remainLines * receiver.getMeterMax(engine)) / GOAL_TABLE[goaltype];
						if(remainLines <= 30) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
						if(remainLines <= 20) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
						if(remainLines <= 10) engine.meterColor = GameEngine.METER_COLOR_RED;
					}
				}
				// Ending
				if(message[3].equals("ending")) {
					engine.ending = 1;
					engine.timerActive = false;
					engine.gameActive = false;
					engine.stat = GameEngine.STAT_ENDINGSTART;
					engine.resetStatc();
				}
				// Retry
				if(message[3].equals("retry")) {
					engine.stat = GameEngine.STAT_SETTING;
					engine.resetStatc();
					engine.playSE("decide");
				}
			}
		}
	}
}
