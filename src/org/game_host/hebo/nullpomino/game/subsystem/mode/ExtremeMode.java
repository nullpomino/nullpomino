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

import org.game_host.hebo.nullpomino.game.component.BGMStatus;
import org.game_host.hebo.nullpomino.game.component.Controller;
import org.game_host.hebo.nullpomino.game.component.Piece;
import org.game_host.hebo.nullpomino.game.event.EventReceiver;
import org.game_host.hebo.nullpomino.game.play.GameEngine;
import org.game_host.hebo.nullpomino.game.play.GameManager;
import org.game_host.hebo.nullpomino.util.CustomProperties;
import org.game_host.hebo.nullpomino.util.GeneralUtil;

/**
 * EXTREMEモード
 */
public class ExtremeMode extends DummyMode {
	/** 現在のバージョン */
	private static final int CURRENT_VERSION = 1;

	/** エンディングの時間 */
	protected static final int ROLLTIMELIMIT = 2968;

	/** AREテーブル */
	private static final int tableARE[] = {25,20,15,10,10,10, 8, 6, 5, 4, 4, 3, 2, 2, 1, 1, 0, 0, 0, 0};

	/** ライン消去後AREテーブル */
	private static final int tableARELine[] = {25,20,15,10, 6, 4, 4, 2, 2, 2, 2, 2, 2, 2, 1, 0, 0, 0, 0, 0};

	/** ライン消去時間テーブル */
	private static final int tableLineDelay[] = {40,20,10, 5, 6, 4, 4, 2, 2, 2, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0};

	/** 固定時間テーブル */
	private static final int tableLockDelay[] = {30,25,25,20,18,18,17,16,15,15,14,14,14,13,13,13,13,12,11,11};

	/** DASテーブル */
	private static final int tableDAS[] = {16,10,10, 8, 8, 6, 6, 6, 6, 4, 4, 4, 4, 4, 4, 4, 4, 4, 3, 3};

	/** BGMが変わるライン数 */
	private static final int tableBGMChange[] = {50, 100, 150, -1};

	/** ランキングに記録する数 */
	private static final int RANKING_MAX = 10;

	/** ランキングの種類 */
	private static final int RANKING_TYPE = 2;

	/** 直前のスコア獲得の種類の定数 */
	private static final int EVENT_NONE = 0,
							 EVENT_SINGLE = 1,
							 EVENT_DOUBLE = 2,
							 EVENT_TRIPLE = 3,
							 EVENT_FOUR = 4,
							 EVENT_TSPIN_ZERO_MINI = 5,
							 EVENT_TSPIN_ZERO = 6,
							 EVENT_TSPIN_SINGLE_MINI = 7,
							 EVENT_TSPIN_SINGLE = 8,
							 EVENT_TSPIN_DOUBLE_MINI = 9,
							 EVENT_TSPIN_DOUBLE = 10,
							 EVENT_TSPIN_TRIPLE = 11;

	/** このモードを所有するGameManager */
	private GameManager owner;

	/** 描画などのイベント処理 */
	private EventReceiver receiver;

	/** 直前に手に入れたスコア */
	private int lastscore;

	/** 最後にスコア獲得してから経過した時間 */
	private int scgettime;

	/** 直前のスコア獲得の種類 */
	private int lastevent;

	/** 直前のスコア獲得でB2Bだったらtrue */
	private boolean lastb2b;

	/** 直前のスコア獲得でのコンボ数 */
	private int lastcombo;

	/** 直前のスコア獲得でのピースID */
	private int lastpiece;

	/** エンディング時間 */
	private int rolltime;

	/** 現在のBGM */
	private int bgmlv;

	/** スタート時のレベル */
	private int startlevel;

	/** T-Spin有効フラグ(0=なし 1=普通 2=全スピン) */
	private int tspinEnableType;

	/** 旧T-Spin有効フラグ */
	private boolean enableTSpin;

	/** 壁蹴りありT-Spin有効 */
	private boolean enableTSpinKick;

	/** B2B有効 */
	private boolean enableB2B;

	/** コンボ有効 */
	private boolean enableCombo;

	/** エンドレス */
	private boolean endless;

	/** ビッグ */
	private boolean big;

	/** バージョン */
	private int version;

	/** 今回のプレイのランキングでのランク */
	private int rankingRank;

	/** ランキングのスコア */
	private int[][] rankingScore;

	/** ランキングのライン数 */
	private int[][] rankingLines;

	/** ランキングのタイム */
	private int[][] rankingTime;

	/*
	 * モード名
	 */
	@Override
	public String getName() {
		return "EXTREME";
	}

	/*
	 * 初期化
	 */
	@Override
	public void playerInit(GameEngine engine, int playerID) {
		owner = engine.owner;
		receiver = engine.owner.receiver;
		lastscore = 0;
		scgettime = 0;
		lastevent = EVENT_NONE;
		lastb2b = false;
		lastcombo = 0;
		lastpiece = 0;
		bgmlv = 0;
		rolltime = 0;

		rankingRank = -1;
		rankingScore = new int[RANKING_TYPE][RANKING_MAX];
		rankingLines = new int[RANKING_TYPE][RANKING_MAX];
		rankingTime = new int[RANKING_TYPE][RANKING_MAX];

		if(owner.replayMode == false) {
			loadSetting(owner.modeConfig);
			loadRanking(owner.modeConfig, engine.ruleopt.strRuleName);
			version = CURRENT_VERSION;
		} else {
			loadSetting(owner.replayProp);
		}

		engine.staffrollEnable = true;
		engine.staffrollNoDeath = true;
		engine.staffrollEnableStatistics = true;

		engine.owner.backgroundStatus.bg = startlevel;
		engine.framecolor = GameEngine.FRAME_COLOR_RED;
	}

	/**
	 * 落下速度を設定
	 * @param engine GameEngine
	 */
	public void setSpeed(GameEngine engine) {
		int lv = engine.statistics.level;

		if(lv < 0) lv = 0;
		if(lv >= tableARE.length) lv = tableARE.length - 1;

		engine.speed.gravity = -1;
		engine.speed.are = tableARE[lv];
		engine.speed.areLine = tableARELine[lv];
		engine.speed.lineDelay = tableLineDelay[lv];
		engine.speed.lockDelay = tableLockDelay[lv];
		engine.speed.das = tableDAS[lv];
	}

	/*
	 * 設定画面の処理
	 */
	@Override
	public boolean onSetting(GameEngine engine, int playerID) {
		// メニュー
		if(engine.owner.replayMode == false) {
			// 上
			if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_UP)) {
				engine.statc[2]--;
				if(engine.statc[2] < 0) engine.statc[2] = 6;
				engine.playSE("cursor");
			}
			// 下
			if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_DOWN)) {
				engine.statc[2]++;
				if(engine.statc[2] > 6) engine.statc[2] = 0;
				engine.playSE("cursor");
			}

			// 設定変更
			int change = 0;
			if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_LEFT)) change = -1;
			if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_RIGHT)) change = 1;

			if(change != 0) {
				engine.playSE("change");

				switch(engine.statc[2]) {
				case 0:
					startlevel += change;
					if(startlevel < 0) startlevel = 19;
					if(startlevel > 19) startlevel = 0;
					engine.owner.backgroundStatus.bg = startlevel;
					break;
				case 1:
					//enableTSpin = !enableTSpin;
					tspinEnableType += change;
					if(tspinEnableType < 0) tspinEnableType = 2;
					if(tspinEnableType > 2) tspinEnableType = 0;
					break;
				case 2:
					enableTSpinKick = !enableTSpinKick;
					break;
				case 3:
					enableB2B = !enableB2B;
					break;
				case 4:
					enableCombo = !enableCombo;
					break;
				case 5:
					endless = !endless;
					break;
				case 6:
					big = !big;
					break;
				}
			}

			// 決定
			if(engine.ctrl.isPush(Controller.BUTTON_A) && (engine.statc[3] >= 5)) {
				engine.playSE("decide");
				saveSetting(owner.modeConfig);
				receiver.saveModeConfig(owner.modeConfig);
				return false;
			}

			// キャンセル
			if(engine.ctrl.isPush(Controller.BUTTON_B)) {
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
	 * 設定画面の描画処理
	 */
	@Override
	public void renderSetting(GameEngine engine, int playerID) {
		if(engine.owner.replayMode == false) {
			receiver.drawMenuFont(engine, playerID, 0, (engine.statc[2] * 2) + 1, "b", EventReceiver.COLOR_RED);
		}

		receiver.drawMenuFont(engine, playerID, 0, 0, "LEVEL", EventReceiver.COLOR_BLUE);
		receiver.drawMenuFont(engine, playerID, 1, 1, String.valueOf(startlevel + 1), (engine.statc[2] == 0));
		receiver.drawMenuFont(engine, playerID, 0, 2, "SPIN BONUS", EventReceiver.COLOR_BLUE);
		String strTSpinEnable = "";
		if(version >= 1) {
			if(tspinEnableType == 0) strTSpinEnable = "OFF";
			if(tspinEnableType == 1) strTSpinEnable = "T-ONLY";
			if(tspinEnableType == 2) strTSpinEnable = "ALL";
		} else {
			strTSpinEnable = GeneralUtil.getONorOFF(enableTSpin);
		}
		receiver.drawMenuFont(engine, playerID, 1, 3, strTSpinEnable, (engine.statc[2] == 1));
		receiver.drawMenuFont(engine, playerID, 0, 4, "EZ SPIN", EventReceiver.COLOR_BLUE);
		receiver.drawMenuFont(engine, playerID, 1, 5, GeneralUtil.getONorOFF(enableTSpinKick), (engine.statc[2] == 2));
		receiver.drawMenuFont(engine, playerID, 0, 6, "B2B", EventReceiver.COLOR_BLUE);
		receiver.drawMenuFont(engine, playerID, 1, 7, GeneralUtil.getONorOFF(enableB2B), (engine.statc[2] == 3));
		receiver.drawMenuFont(engine, playerID, 0, 8, "COMBO", EventReceiver.COLOR_BLUE);
		receiver.drawMenuFont(engine, playerID, 1, 9, GeneralUtil.getONorOFF(enableCombo), (engine.statc[2] == 4));
		receiver.drawMenuFont(engine, playerID, 0, 10, "ENDLESS", EventReceiver.COLOR_BLUE);
		receiver.drawMenuFont(engine, playerID, 1, 11, GeneralUtil.getONorOFF(endless), (engine.statc[2] == 5));
		receiver.drawMenuFont(engine, playerID, 0, 12, "BIG", EventReceiver.COLOR_BLUE);
		receiver.drawMenuFont(engine, playerID, 1, 13, GeneralUtil.getONorOFF(big), (engine.statc[2] == 6));
	}

	/*
	 * Readyのときの初期化処理
	 */
	@Override
	public void startGame(GameEngine engine, int playerID) {
		engine.statistics.level = startlevel;
		engine.statistics.levelDispAdd = 1;
		engine.b2bEnable = enableB2B;
		if(enableCombo == true) {
			engine.comboType = GameEngine.COMBO_TYPE_NORMAL;
		} else {
			engine.comboType = GameEngine.COMBO_TYPE_DISABLE;
		}
		engine.big = big;
		owner.bgmStatus.bgm = bgmlv + 2;

		if(version >= 1) {
			engine.tspinAllowKick = enableTSpinKick;
			if(tspinEnableType == 0) {
				engine.tspinEnable = false;
			} else if(tspinEnableType == 1) {
				engine.tspinEnable = true;
			} else {
				engine.tspinEnable = true;
				engine.useAllSpinBonus = true;
			}
		} else {
			engine.tspinEnable = enableTSpin;
		}

		setSpeed(engine);
	}

	/*
	 * スコア表示
	 */
	@Override
	public void renderLast(GameEngine engine, int playerID) {
		receiver.drawScoreFont(engine, playerID, 0, 0, "EXTREME", EventReceiver.COLOR_RED);

		if( (engine.stat == GameEngine.STAT_SETTING) || ((engine.stat == GameEngine.STAT_RESULT) && (owner.replayMode == false)) ) {
			if((owner.replayMode == false) && (big == false) && (engine.ai == null)) {
				receiver.drawScoreFont(engine, playerID, 3, 2, "SCORE  LINE TIME", EventReceiver.COLOR_BLUE);

				for(int i = 0; i < RANKING_MAX; i++) {
					int endlessIndex = 0;
					if(endless) endlessIndex = 1;

					receiver.drawScoreFont(engine, playerID, 0, 3 + i, String.format("%2d", i + 1), EventReceiver.COLOR_YELLOW);
					receiver.drawScoreFont(engine, playerID, 3, 3 + i, String.valueOf(rankingScore[endlessIndex][i]), (i == rankingRank));
					receiver.drawScoreFont(engine, playerID, 10, 3 + i, String.valueOf(rankingLines[endlessIndex][i]), (i == rankingRank));
					receiver.drawScoreFont(engine, playerID, 15, 3 + i, GeneralUtil.getTime(rankingTime[endlessIndex][i]), (i == rankingRank));
				}
			}
		} else {
			receiver.drawScoreFont(engine, playerID, 0, 2, "SCORE", EventReceiver.COLOR_BLUE);
			String strScore;
			if((lastscore == 0) || (scgettime >= 120)) {
				strScore = String.valueOf(engine.statistics.score);
			} else {
				strScore = String.valueOf(engine.statistics.score) + "(+" + String.valueOf(lastscore) + ")";
			}
			receiver.drawScoreFont(engine, playerID, 0, 3, strScore);

			receiver.drawScoreFont(engine, playerID, 0, 5, "LINE", EventReceiver.COLOR_BLUE);
			if((engine.statistics.level < 19) || ((endless == false) && (engine.ending == 0)))
				receiver.drawScoreFont(engine, playerID, 0, 6, engine.statistics.lines + "/" + ((engine.statistics.level + 1) * 10));
			else
				receiver.drawScoreFont(engine, playerID, 0, 6, engine.statistics.lines + "");

			receiver.drawScoreFont(engine, playerID, 0, 8, "LEVEL", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 0, 9, String.valueOf(engine.statistics.level + 1));

			receiver.drawScoreFont(engine, playerID, 0, 11, "TIME", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 0, 12, GeneralUtil.getTime(engine.statistics.time));

			if((engine.gameActive) && (engine.ending == 2)) {
				int remainRollTime = ROLLTIMELIMIT - rolltime;
				if(remainRollTime < 0) remainRollTime = 0;

				receiver.drawScoreFont(engine, playerID, 0, 14, "ROLL TIME", EventReceiver.COLOR_BLUE);
				receiver.drawScoreFont(engine, playerID, 0, 15, GeneralUtil.getTime(remainRollTime), ((remainRollTime > 0) && (remainRollTime < 10 * 60)));
			}

			if((lastevent != EVENT_NONE) && (scgettime < 120)) {
				String strPieceName = Piece.getPieceName(lastpiece);

				switch(lastevent) {
				case EVENT_SINGLE:
					receiver.drawMenuFont(engine, playerID, 2, 21, "SINGLE", EventReceiver.COLOR_DARKBLUE);
					break;
				case EVENT_DOUBLE:
					receiver.drawMenuFont(engine, playerID, 2, 21, "DOUBLE", EventReceiver.COLOR_BLUE);
					break;
				case EVENT_TRIPLE:
					receiver.drawMenuFont(engine, playerID, 2, 21, "TRIPLE", EventReceiver.COLOR_GREEN);
					break;
				case EVENT_FOUR:
					if(lastb2b) receiver.drawMenuFont(engine, playerID, 3, 21, "FOUR", EventReceiver.COLOR_RED);
					else receiver.drawMenuFont(engine, playerID, 3, 21, "FOUR", EventReceiver.COLOR_ORANGE);
					break;
				case EVENT_TSPIN_ZERO_MINI:
					receiver.drawMenuFont(engine, playerID, 2, 21, strPieceName + "-SPIN", EventReceiver.COLOR_PURPLE);
					break;
				case EVENT_TSPIN_ZERO:
					receiver.drawMenuFont(engine, playerID, 2, 21, strPieceName + "-SPIN", EventReceiver.COLOR_PINK);
					break;
				case EVENT_TSPIN_SINGLE_MINI:
					if(lastb2b) receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-MINI-S", EventReceiver.COLOR_RED);
					else receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-MINI-S", EventReceiver.COLOR_ORANGE);
					break;
				case EVENT_TSPIN_SINGLE:
					if(lastb2b) receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-SINGLE", EventReceiver.COLOR_RED);
					else receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-SINGLE", EventReceiver.COLOR_ORANGE);
					break;
				case EVENT_TSPIN_DOUBLE_MINI:
					if(lastb2b) receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-MINI-D", EventReceiver.COLOR_RED);
					else receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-MINI-D", EventReceiver.COLOR_ORANGE);
					break;
				case EVENT_TSPIN_DOUBLE:
					if(lastb2b) receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-DOUBLE", EventReceiver.COLOR_RED);
					else receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-DOUBLE", EventReceiver.COLOR_ORANGE);
					break;
				case EVENT_TSPIN_TRIPLE:
					if(lastb2b) receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-TRIPLE", EventReceiver.COLOR_RED);
					else receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-TRIPLE", EventReceiver.COLOR_ORANGE);
					break;
				}

				if((lastcombo >= 2) && (lastevent != EVENT_TSPIN_ZERO_MINI) && (lastevent != EVENT_TSPIN_ZERO))
					receiver.drawMenuFont(engine, playerID, 2, 22, (lastcombo - 1) + "COMBO", EventReceiver.COLOR_CYAN);
			}
		}
	}

	/*
	 * 各フレームの終わりの処理
	 */
	@Override
	public void onLast(GameEngine engine, int playerID) {
		// エンディング
		if((engine.gameActive) && (engine.ending == 2)) {
			rolltime++;

			// 時間メーター
			int remainRollTime = ROLLTIMELIMIT - rolltime;
			if(remainRollTime < 0) remainRollTime = 0;
			engine.meterValue = (remainRollTime * receiver.getMeterMax(engine)) / ROLLTIMELIMIT;
			engine.meterColor = GameEngine.METER_COLOR_GREEN;
			if(remainRollTime <= 30*60) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
			if(remainRollTime <= 20*60) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
			if(remainRollTime <= 10*60) engine.meterColor = GameEngine.METER_COLOR_RED;

			// ロール終了
			if(rolltime >= ROLLTIMELIMIT) {
				engine.gameActive = false;
				engine.resetStatc();
				engine.stat = GameEngine.STAT_EXCELLENT;
			}
		}

		scgettime++;
	}

	/*
	 * スコア計算
	 */
	@Override
	public void calcScore(GameEngine engine, int playerID, int lines) {
		// ラインクリアボーナス
		int pts = 0;

		if(engine.tspin) {
			// T-Spin 0列
			if(lines == 0) {
				if(engine.tspinmini) {
					pts += 100 * (engine.statistics.level + 1);
					lastevent = EVENT_TSPIN_ZERO_MINI;
				} else {
					pts += 400 * (engine.statistics.level + 1);
					lastevent = EVENT_TSPIN_ZERO;
				}
			}
			// T-Spin 1列
			else if(lines == 1) {
				if(engine.tspinmini) {
					if(engine.b2b) {
						pts += 300 * (engine.statistics.level + 1);
					} else {
						pts += 200 * (engine.statistics.level + 1);
					}
					lastevent = EVENT_TSPIN_SINGLE_MINI;
				} else {
					if(engine.b2b) {
						pts += 1200 * (engine.statistics.level + 1);
					} else {
						pts += 800 * (engine.statistics.level + 1);
					}
					lastevent = EVENT_TSPIN_SINGLE;
				}
			}
			// T-Spin 2列
			else if(lines == 2) {
				if(engine.tspinmini && engine.useAllSpinBonus) {
					if(engine.b2b) {
						pts += 600 * (engine.statistics.level + 1);
					} else {
						pts += 400 * (engine.statistics.level + 1);
					}
					lastevent = EVENT_TSPIN_DOUBLE_MINI;
				} else {
					if(engine.b2b) {
						pts += 1800 * (engine.statistics.level + 1);
					} else {
						pts += 1200 * (engine.statistics.level + 1);
					}
					lastevent = EVENT_TSPIN_DOUBLE;
				}
			}
			// T-Spin 3列
			else if(lines >= 3) {
				if(engine.b2b) {
					pts += 2400 * (engine.statistics.level + 1);
				} else {
					pts += 1600 * (engine.statistics.level + 1);
				}
				lastevent = EVENT_TSPIN_TRIPLE;
			}
		} else {
			if(lines == 1) {
				pts += 100 * (engine.statistics.level + 1); // 1列
				lastevent = EVENT_SINGLE;
			} else if(lines == 2) {
				pts += 300 * (engine.statistics.level + 1); // 2列
				lastevent = EVENT_DOUBLE;
			} else if(lines == 3) {
				pts += 500 * (engine.statistics.level + 1); // 3列
				lastevent = EVENT_TRIPLE;
			} else if(lines >= 4) {
				// 4列
				if(engine.b2b) {
					pts += 1200 * (engine.statistics.level + 1);
				} else {
					pts += 800 * (engine.statistics.level + 1);
				}
				lastevent = EVENT_FOUR;
			}
		}

		lastb2b = engine.b2b;

		// コンボ
		if((enableCombo) && (engine.combo >= 1) && (lines >= 1)) {
			pts += ((engine.combo - 1) * 50) * (engine.statistics.level + 1);
			lastcombo = engine.combo;
		}

		// 全消し
		if((lines >= 1) && (engine.field.isEmpty())) {
			engine.playSE("bravo");
			pts += 1800 * (engine.statistics.level + 1);
		}

		// スコア加算
		if(pts > 0) {
			lastpiece = engine.nowPieceObject.id;
			lastscore = pts;
			scgettime = 0;
			if(lines >= 1) engine.statistics.scoreFromLineClear += pts;
			else engine.statistics.scoreFromOtherBonus += pts;
			engine.statistics.score += pts;
		}

		if(engine.ending == 0) {
			// BGMフェードアウトとBGM切り替え
			if(tableBGMChange[bgmlv] != -1) {
				if(engine.statistics.lines >= tableBGMChange[bgmlv] - 5) owner.bgmStatus.fadesw = true;

				if(engine.statistics.lines >= tableBGMChange[bgmlv]) {
					bgmlv++;
					owner.bgmStatus.bgm = bgmlv + 2;
					owner.bgmStatus.fadesw = false;
				}
			}

			// メーター
			engine.meterValue = ((engine.statistics.lines % 10) * receiver.getMeterMax(engine)) / 9;
			engine.meterColor = GameEngine.METER_COLOR_GREEN;
			if(engine.statistics.lines % 10 >= 4) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
			if(engine.statistics.lines % 10 >= 6) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
			if(engine.statistics.lines % 10 >= 8) engine.meterColor = GameEngine.METER_COLOR_RED;

			if((engine.statistics.lines >= 200) && (endless == false)) {
				// エンディング
				engine.playSE("levelup");
				engine.playSE("endingstart");
				owner.bgmStatus.bgm = BGMStatus.BGM_ENDING1;
				owner.bgmStatus.fadesw = false;
				engine.bone = true;
				engine.ending = 2;
				engine.timerActive = false;
			} else if((engine.statistics.lines >= (engine.statistics.level + 1) * 10) && (engine.statistics.level < 19)) {
				// レベルアップ
				engine.statistics.level++;

				owner.backgroundStatus.fadesw = true;
				owner.backgroundStatus.fadecount = 0;
				owner.backgroundStatus.fadebg = engine.statistics.level;

				setSpeed(engine);
				engine.playSE("levelup");
			}
		}
	}

	/*
	 * 結果画面の描画
	 */
	@Override
	public void renderResult(GameEngine engine, int playerID) {
		receiver.drawMenuFont(engine, playerID,  0, 1, "PLAY DATA", EventReceiver.COLOR_ORANGE);

		receiver.drawMenuFont(engine, playerID,  0, 3, "SCORE", EventReceiver.COLOR_BLUE);
		String strScore = String.format("%10d", engine.statistics.score);
		receiver.drawMenuFont(engine, playerID,  0, 4, strScore);

		receiver.drawMenuFont(engine, playerID,  0, 5, "LINE", EventReceiver.COLOR_BLUE);
		String strLines = String.format("%10d", engine.statistics.lines);
		receiver.drawMenuFont(engine, playerID,  0, 6, strLines);

		receiver.drawMenuFont(engine, playerID,  0, 7, "LEVEL", EventReceiver.COLOR_BLUE);
		String strLevel = String.format("%10d", engine.statistics.level + 1);
		receiver.drawMenuFont(engine, playerID,  0, 8, strLevel);

		receiver.drawMenuFont(engine, playerID,  0, 9, "TIME", EventReceiver.COLOR_BLUE);
		String strTime = String.format("%10s", GeneralUtil.getTime(engine.statistics.time));
		receiver.drawMenuFont(engine, playerID,  0, 10, strTime);

		receiver.drawMenuFont(engine, playerID,  0, 11, "SCORE/LINE", EventReceiver.COLOR_BLUE);
		String strSPL = String.format("%10g", engine.statistics.spl);
		receiver.drawMenuFont(engine, playerID,  0, 12, strSPL);

		receiver.drawMenuFont(engine, playerID,  0, 13, "LINE/MIN", EventReceiver.COLOR_BLUE);
		String strLPM = String.format("%10g", engine.statistics.lpm);
		receiver.drawMenuFont(engine, playerID,  0, 14, strLPM);

		if(rankingRank != -1) {
			receiver.drawMenuFont(engine, playerID,  0, 15, "RANK", EventReceiver.COLOR_BLUE);
			String strRank = String.format("%10d", rankingRank + 1);
			receiver.drawMenuFont(engine, playerID,  0, 16, strRank);
		}
	}

	/*
	 * リプレイ保存時の処理
	 */
	@Override
	public void saveReplay(GameEngine engine, int playerID, CustomProperties prop) {
		saveSetting(prop);

		// ランキング更新
		if((owner.replayMode == false) && (big == false) && (engine.ai == null)) {
			updateRanking(engine.statistics.score, engine.statistics.lines, engine.statistics.time, endless);

			if(rankingRank != -1) {
				saveRanking(owner.modeConfig, engine.ruleopt.strRuleName);
				receiver.saveModeConfig(owner.modeConfig);
			}
		}
	}

	/**
	 * プロパティファイルから設定を読み込み
	 * @param prop プロパティファイル
	 */
	private void loadSetting(CustomProperties prop) {
		startlevel = prop.getProperty("extreme.startlevel", 0);
		tspinEnableType = prop.getProperty("extreme.tspinEnableType", 1);
		enableTSpin = prop.getProperty("extreme.enableTSpin", true);
		enableTSpinKick = prop.getProperty("extreme.enableTSpinKick", true);
		enableB2B = prop.getProperty("extreme.enableB2B", true);
		enableCombo = prop.getProperty("extreme.enableCombo", true);
		endless = prop.getProperty("extreme.endless", false);
		big = prop.getProperty("extreme.big", false);
		version = prop.getProperty("extreme.version", 0);
	}

	/**
	 * プロパティファイルに設定を保存
	 * @param prop プロパティファイル
	 */
	private void saveSetting(CustomProperties prop) {
		prop.setProperty("extreme.startlevel", startlevel);
		prop.setProperty("extreme.tspinEnableType", tspinEnableType);
		prop.setProperty("extreme.enableTSpin", enableTSpin);
		prop.setProperty("extreme.enableTSpinKick", enableTSpinKick);
		prop.setProperty("extreme.enableB2B", enableB2B);
		prop.setProperty("extreme.enableCombo", enableCombo);
		prop.setProperty("extreme.endless", endless);
		prop.setProperty("extreme.big", big);
		prop.setProperty("extreme.version", version);
	}

	/**
	 * プロパティファイルからランキングを読み込み
	 * @param prop プロパティファイル
	 * @param ruleName ルール名
	 */
	private void loadRanking(CustomProperties prop, String ruleName) {
		for(int i = 0; i < RANKING_MAX; i++) {
			for(int endlessIndex = 0; endlessIndex < 2; endlessIndex++) {
				rankingScore[endlessIndex][i] = prop.getProperty("extreme.ranking." + ruleName + "." + endlessIndex + ".score." + i, 0);
				rankingLines[endlessIndex][i] = prop.getProperty("extreme.ranking." + ruleName + "." + endlessIndex + ".lines." + i, 0);
				rankingTime[endlessIndex][i] = prop.getProperty("extreme.ranking." + ruleName + "." + endlessIndex + ".time." + i, 0);
			}
		}
	}

	/**
	 * プロパティファイルにランキングを保存
	 * @param prop プロパティファイル
	 * @param ruleName ルール名
	 */
	private void saveRanking(CustomProperties prop, String ruleName) {
		for(int i = 0; i < RANKING_MAX; i++) {
			for(int endlessIndex = 0; endlessIndex < 2; endlessIndex++) {
				prop.setProperty("extreme.ranking." + ruleName + "." + endlessIndex + ".score." + i, rankingScore[endlessIndex][i]);
				prop.setProperty("extreme.ranking." + ruleName + "." + endlessIndex + ".lines." + i, rankingLines[endlessIndex][i]);
				prop.setProperty("extreme.ranking." + ruleName + "." + endlessIndex + ".time." + i, rankingTime[endlessIndex][i]);
			}
		}
	}

	/**
	 * ランキングを更新
	 * @param sc スコア
	 * @param li ライン
	 * @param time タイム
	 */
	private void updateRanking(int sc, int li, int time, boolean endlessMode) {
		rankingRank = checkRanking(sc, li, time, endlessMode);

		if(rankingRank != -1) {
			int endlessIndex = 0;
			if(endlessMode) endlessIndex = 1;

			// ランキングをずらす
			for(int i = RANKING_MAX - 1; i > rankingRank; i--) {
				rankingScore[endlessIndex][i] = rankingScore[endlessIndex][i - 1];
				rankingLines[endlessIndex][i] = rankingLines[endlessIndex][i - 1];
				rankingTime[endlessIndex][i] = rankingTime[endlessIndex][i - 1];
			}

			// 新しいデータを登録
			rankingScore[endlessIndex][rankingRank] = sc;
			rankingLines[endlessIndex][rankingRank] = li;
			rankingTime[endlessIndex][rankingRank] = time;
		}
	}

	/**
	 * ランキングの順位を取得
	 * @param sc スコア
	 * @param li ライン
	 * @param time タイム
	 * @return 順位(ランク外なら-1)
	 */
	private int checkRanking(int sc, int li, int time, boolean endlessMode) {
		int endlessIndex = 0;
		if(endlessMode) endlessIndex = 1;

		for(int i = 0; i < RANKING_MAX; i++) {
			if(sc > rankingScore[endlessIndex][i]) {
				return i;
			} else if((sc == rankingScore[endlessIndex][i]) && (li > rankingLines[endlessIndex][i])) {
				return i;
			} else if((sc == rankingScore[endlessIndex][i]) && (li == rankingLines[endlessIndex][i]) && (time < rankingTime[endlessIndex][i])) {
				return i;
			}
		}

		return -1;
	}
}
