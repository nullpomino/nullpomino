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
import mu.nu.nullpo.game.component.Piece;
import mu.nu.nullpo.game.event.EventReceiver;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.gui.slick.GameKey;
import mu.nu.nullpo.util.CustomProperties;
import mu.nu.nullpo.util.GeneralUtil;

/**
 * MARATHON+ Mode
 */
public class MarathonPlusMode extends DummyMode {
	/** Current version */
	private static final int CURRENT_VERSION = 1;

	/** Fall velocity table (numerators) */
	private static final int tableGravity[]     = { 1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1, 465, 731, 1280, 1707,  -1,  -1,  -1, 1};

	/** Fall velocity table (denominators) */
	private static final int tableDenominator[] = {63, 50, 39, 30, 22, 16, 12,  8,  6,  4,  3,  2,  1, 256, 256,  256,  256, 256, 256, 256, 4};

	/** Line counts when BGM changes occur */
	private static final int tableBGMChange[] = {50, 100, 150, 200, -1};

	/** Number of entries in rankings */
	private static final int RANKING_MAX = 10;

	/** Number of ranking types */
	private static final int RANKING_TYPE = 2;

	/** Number of game types */
	private static final int GAMETYPE_MAX = 2;

	/** Most recent scoring event typeの定count */
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

	/** GameManager that owns this mode */
	private GameManager owner;

	/** Drawing and event handling EventReceiver */
	private EventReceiver receiver;

	/** Most recent increase in score */
	private int lastscore;

	/** Time to display the most recent increase in score */
	private int scgettime;

	/** Most recent scoring event type */
	private int lastevent;

	/** Most recent scoring eventでB2Bだったらtrue */
	private boolean lastb2b;

	/** Most recent scoring eventでのCombocount */
	private int lastcombo;

	/** Most recent scoring eventでのピースID */
	private int lastpiece;

	/** Current BGM */
	private int bgmlv;

	/**  bonus levelでのline count */
	private int bonusLines;

	/**  bonus levelでBlockを置いた count */
	private int bonusPieceCount;

	/**  bonus levelでのBlockが見える残り time */
	private int bonusFlashNow;

	/**  bonus level経過 time */
	private int bonusTime;

	/** Level at start time */
	private int startlevel;

	/** Flag for types of T-Spins allowed (0=none, 1=normal, 2=all spin) */
	private int tspinEnableType;

	/** Old flag for allowing T-Spins */
	private boolean enableTSpin;

	/** Flag for enabling wallkick T-Spins */
	private boolean enableTSpinKick;

	/** Flag for enabling B2B */
	private boolean enableB2B;

	/** Flag for enabling combos */
	private boolean enableCombo;

	/** Big */
	private boolean big;

	/** Version */
	private int version;

	/** Current round's ranking rank */
	private int rankingRank;

	/** Rankings' scores */
	private int[][] rankingScore;

	/** Rankings' line counts */
	private int[][] rankingLines;

	/** Rankings' times */
	private int[][] rankingTime;

	/*
	 * Mode name
	 */
	@Override
	public String getName() {
		return "MARATHON+";
	}

	/*
	 * Initialization
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

		bonusLines = 0;
		bonusPieceCount = 0;
		bonusFlashNow = 0;
		bonusTime = 0;

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

		engine.owner.backgroundStatus.bg = startlevel;
		if(engine.owner.backgroundStatus.bg > 19) engine.owner.backgroundStatus.bg = 19;
		engine.framecolor = GameEngine.FRAME_COLOR_GRAY;
	}

	/**
	 * Set the gravity rate
	 * @param engine GameEngine
	 */
	private void setSpeed(GameEngine engine) {
		int lv = engine.statistics.level;

		if(lv < 0) lv = 0;
		if(lv >= tableGravity.length) lv = tableGravity.length - 1;

		engine.speed.gravity = tableGravity[lv];
		engine.speed.denominator = tableDenominator[lv];

		engine.speed.lineDelay = 12;
	}

	/**
	 * Set BGM at start of game
	 * @param engine GameEngine
	 */
	private void setStartBgmlv(GameEngine engine) {
		bgmlv = 0;
		if(startlevel >= 20) {
			bgmlv = 4;
		} else {
			while((tableBGMChange[bgmlv] != -1) && (engine.statistics.lines >= tableBGMChange[bgmlv])) bgmlv++;
		}
	}

	/*
	 * Called at settings screen
	 */
	@Override
	public boolean onSetting(GameEngine engine, int playerID) {
		// Menu
		if(engine.owner.replayMode == false) {
			// Configuration changes
			int change = updateCursor(engine, 5);

			if(change != 0) {
				engine.playSE("change");

				switch(engine.statc[2]) {
				case 0:
					startlevel += change;
					if(startlevel < 0) startlevel = 20;
					if(startlevel > 20) startlevel = 0;
					engine.owner.backgroundStatus.bg = startlevel;
					if(engine.owner.backgroundStatus.bg > 19) engine.owner.backgroundStatus.bg = 19;
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

			// Cancel
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
	 * Render the settings screen
	 */
	@Override
	public void renderSetting(GameEngine engine, int playerID) {
		String strTSpinEnable = "";
		if(version >= 1) {
			if(tspinEnableType == 0) strTSpinEnable = "OFF";
			if(tspinEnableType == 1) strTSpinEnable = "T-ONLY";
			if(tspinEnableType == 2) strTSpinEnable = "ALL";
		} else {
			strTSpinEnable = GeneralUtil.getONorOFF(enableTSpin);
		}
		drawMenu(engine, playerID, receiver, 0, EventReceiver.COLOR_BLUE, 0,
				"LEVEL", String.valueOf(startlevel + 1),
				"SPIN BONUS", strTSpinEnable,
				"EZ SPIN", GeneralUtil.getONorOFF(enableTSpinKick),
				"B2B", GeneralUtil.getONorOFF(enableB2B),
				"COMBO",  GeneralUtil.getONorOFF(enableCombo),
				"BIG", GeneralUtil.getONorOFF(big));
	}

	/*
	 * Called for initialization during "Ready" screen
	 */
	@Override
	public void startGame(GameEngine engine, int playerID) {
		if(!engine.readyDone) {
			if(startlevel < 20) engine.statistics.lines = startlevel * 10;
			engine.statistics.level = startlevel;
			engine.statistics.levelDispAdd = 1;
			engine.b2bEnable = enableB2B;
			if(enableCombo == true) {
				engine.comboType = GameEngine.COMBO_TYPE_NORMAL;
			} else {
				engine.comboType = GameEngine.COMBO_TYPE_DISABLE;
			}
			engine.big = big;

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
		}
		setSpeed(engine);
		setStartBgmlv(engine);

		owner.bgmStatus.bgm = bgmlv;
		owner.bgmStatus.fadesw = false;
	}

	/*
	 * Render score
	 */
	@Override
	public void renderLast(GameEngine engine, int playerID) {
		receiver.drawScoreFont(engine, playerID, 0, 0, "MARATHON+", EventReceiver.COLOR_GREEN);

		if(startlevel == 20) {
			receiver.drawScoreFont(engine, playerID, 0, 1, "(BONUS GAME)", EventReceiver.COLOR_GREEN);
		} else if(startlevel == 0) {
			receiver.drawScoreFont(engine, playerID, 0, 1, "(NORMAL GAME)", EventReceiver.COLOR_GREEN);
		}

		if( (engine.stat == GameEngine.STAT_SETTING) || ((engine.stat == GameEngine.STAT_RESULT) && (owner.replayMode == false)) ) {
			if( (owner.replayMode == false) && (big == false) && ((startlevel == 0) || (startlevel == 20)) && (engine.ai == null) ) {
				receiver.drawScoreFont(engine, playerID, 3, 3, "SCORE   LINE TIME", EventReceiver.COLOR_BLUE);

				for(int i = 0; i < RANKING_MAX; i++) {
					int gametype = (startlevel == 20) ? 1 : 0;
					receiver.drawScoreFont(engine, playerID, 0, 4 + i, String.format("%2d", i + 1), EventReceiver.COLOR_YELLOW);
					receiver.drawScoreFont(engine, playerID, 3, 4 + i, String.valueOf(rankingScore[gametype][i]), (i == rankingRank));
					receiver.drawScoreFont(engine, playerID, 11, 4 + i, String.valueOf(rankingLines[gametype][i]), (i == rankingRank));
					receiver.drawScoreFont(engine, playerID, 16, 4 + i, GeneralUtil.getTime(rankingTime[gametype][i]), (i == rankingRank));
				}
			}
		} else {
			receiver.drawScoreFont(engine, playerID, 0, 3, "SCORE", EventReceiver.COLOR_BLUE);
			String strScore;
			if((lastscore == 0) || (scgettime >= 120)) {
				strScore = String.valueOf(engine.statistics.score);
			} else {
				strScore = String.valueOf(engine.statistics.score) + "(+" + String.valueOf(lastscore) + ")";
			}
			receiver.drawScoreFont(engine, playerID, 0, 4, strScore);

			receiver.drawScoreFont(engine, playerID, 0, 6, "LINE", EventReceiver.COLOR_BLUE);
			if(startlevel >= 20)
				receiver.drawScoreFont(engine, playerID, 0, 7, engine.statistics.lines + "");
			else if(engine.statistics.level >= 20)
				receiver.drawScoreFont(engine, playerID, 0, 7, engine.statistics.lines + " (" + bonusLines + ")");
			else
				receiver.drawScoreFont(engine, playerID, 0, 7, engine.statistics.lines + "/" + ((engine.statistics.level + 1) * 10));

			receiver.drawScoreFont(engine, playerID, 0, 9, "LEVEL", EventReceiver.COLOR_BLUE);
			if(engine.statistics.level >= 20) {
				receiver.drawScoreFont(engine, playerID, 0, 10, "BONUS", EventReceiver.COLOR_ORANGE);
			} else {
				receiver.drawScoreFont(engine, playerID, 0, 10, String.valueOf(engine.statistics.level + 1));
			}

			receiver.drawScoreFont(engine, playerID, 0, 12, "TIME", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 0, 13, GeneralUtil.getTime(engine.statistics.time));

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
	 * Called after every frame
	 */
	@Override
	public void onLast(GameEngine engine, int playerID) {
		scgettime++;

		if((engine.statistics.level >= 20) && (engine.timerActive) && (engine.gameActive)) {
			bonusTime++;

			if(bonusFlashNow > 0) {
				bonusFlashNow--;
			} else {
				engine.blockOutlineType = GameEngine.BLOCK_OUTLINE_NONE;

				for(int i = 0; i < engine.field.getHeight(); i++) {
					for(int j = 0; j < engine.field.getWidth(); j++) {
						Block blk = engine.field.getBlock(j, i);

						if((blk != null) && (blk.color > Block.BLOCK_COLOR_NONE)) {
							blk.setAttribute(Block.BLOCK_ATTRIBUTE_VISIBLE, false);
							blk.setAttribute(Block.BLOCK_ATTRIBUTE_OUTLINE, false);
						}
					}
				}
			}
		}

	}

	/*
	 * Calculate score
	 */
	@Override
	public void calcScore(GameEngine engine, int playerID, int lines) {
		// Line clear bonus
		int pts = 0;

		if(engine.tspin) {
			// T-Spin 0 lines
			if(lines == 0) {
				if(engine.tspinmini) {
					pts += 100 * (engine.statistics.level + 1);
					lastevent = EVENT_TSPIN_ZERO_MINI;
				} else {
					pts += 400 * (engine.statistics.level + 1);
					lastevent = EVENT_TSPIN_ZERO;
				}
			}
			// T-Spin 1 line
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
			// T-Spin 2 lines
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
			// T-Spin 3 lines
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
				// 4 lines
				if(engine.b2b) {
					pts += 1200 * (engine.statistics.level + 1);
				} else {
					pts += 800 * (engine.statistics.level + 1);
				}
				lastevent = EVENT_FOUR;
			}
		}

		lastb2b = engine.b2b;

		// Combo
		if((enableCombo) && (engine.combo >= 1) && (lines >= 1)) {
			pts += ((engine.combo - 1) * 50) * (engine.statistics.level + 1);
			lastcombo = engine.combo;
		}

		// All clear
		if((lines >= 1) && (engine.field.isEmpty())) {
			engine.playSE("bravo");
			pts += 1800 * (engine.statistics.level + 1);
		}

		// Add to score
		if(pts > 0) {
			lastpiece = engine.nowPieceObject.id;
			lastscore = pts;
			scgettime = 0;
			if(lines >= 1) engine.statistics.scoreFromLineClear += pts;
			else engine.statistics.scoreFromOtherBonus += pts;
			engine.statistics.score += pts;
		}

		// BGM fade-out effects and BGM changes
		if((tableBGMChange[bgmlv] != -1) && (startlevel < 20)) {
			if(engine.statistics.lines >= tableBGMChange[bgmlv] - 5) owner.bgmStatus.fadesw = true;

			if(engine.statistics.lines >= tableBGMChange[bgmlv]) {
				bgmlv++;

				if(engine.statistics.level < 20) {
					owner.bgmStatus.bgm = bgmlv;
					owner.bgmStatus.fadesw = false;
				}
			}
		}

		// Meter
		if(engine.statistics.level < 20) {
			engine.meterValue = ((engine.statistics.lines % 10) * receiver.getMeterMax(engine)) / 9;
			engine.meterColor = GameEngine.METER_COLOR_GREEN;
			if(engine.statistics.lines % 10 >= 4) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
			if(engine.statistics.lines % 10 >= 6) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
			if(engine.statistics.lines % 10 >= 8) engine.meterColor = GameEngine.METER_COLOR_RED;
		}

		//  bonus level
		if(engine.statistics.level >= 20) {
			bonusLines += lines;
			bonusPieceCount++;

			if(bonusPieceCount > bonusLines / 4) {
				bonusPieceCount = 0;
				bonusFlashNow = 30;
				engine.blockOutlineType = GameEngine.BLOCK_OUTLINE_NORMAL;
				engine.resetFieldVisible();
			}
		}

		if((engine.statistics.lines >= (engine.statistics.level + 1) * 10) && (engine.statistics.level < 20) && (startlevel < 20)) {
			// Level up
			engine.statistics.level++;

			if(engine.statistics.level >= 20) {
				engine.meterValue = 0;
				owner.bgmStatus.bgm = BGMStatus.BGM_NOTHING;
				engine.timerActive = false;
				engine.ending = 1;
			} else {
				owner.backgroundStatus.fadesw = true;
				owner.backgroundStatus.fadecount = 0;
				owner.backgroundStatus.fadebg = engine.statistics.level;

				setSpeed(engine);
				engine.playSE("levelup");
			}
		}
	}

	/*
	 * Soft drop
	 */
	@Override
	public void afterSoftDropFall(GameEngine engine, int playerID, int fall) {
		engine.statistics.scoreFromSoftDrop += fall;
		engine.statistics.score += fall;
	}

	/*
	 * Hard drop
	 */
	@Override
	public void afterHardDropFall(GameEngine engine, int playerID, int fall) {
		engine.statistics.scoreFromHardDrop += fall * 2;
		engine.statistics.score += fall * 2;
	}

	/*
	 * Ending
	 */
	@Override
	public boolean onEndingStart(GameEngine engine, int playerID) {
		engine.stat = GameEngine.STAT_CUSTOM;
		engine.resetStatc();
		return true;
	}

	/*
	 *  bonus level到達画面の処理
	 */
	@Override
	public boolean onCustom(GameEngine engine, int playerID) {
		if(engine.statc[0] == 0) {
			engine.nowPieceObject = null;
			engine.timerActive = false;
			engine.playSE("endingstart");
		} else if(engine.statc[0] == 90) {
			engine.playSE("excellent");
		} else if((engine.statc[0] >= 120) && (engine.statc[0] < 480)) {
			if(engine.ctrl.isPush(Controller.BUTTON_A)) {
				engine.statc[0] = 480;
			}
		} else if(engine.statc[0] >= 480) {
			engine.ending = 0;
			engine.stat = GameEngine.STAT_READY;
			engine.resetStatc();
			return true;
		}

		engine.statc[0]++;
		return false;
	}

	/*
	 *  bonus level到達画面の描画
	 */
	@Override
	public void renderCustom(GameEngine engine, int playerID) {
		if(engine.statc[0] >= 90) {
			receiver.drawMenuFont(engine, playerID,  0, 8, "EXCELLENT!", EventReceiver.COLOR_ORANGE);

			receiver.drawMenuFont(engine, playerID,  1, 10, "UNLOCKED", EventReceiver.COLOR_ORANGE);
			receiver.drawMenuFont(engine, playerID, 1, 11, "BONUS", (engine.statc[0] % 2 == 0), EventReceiver.COLOR_WHITE, EventReceiver.COLOR_YELLOW);
			receiver.drawMenuFont(engine, playerID, 4, 12, "LEVEL", (engine.statc[0] % 2 == 0), EventReceiver.COLOR_WHITE, EventReceiver.COLOR_YELLOW);
		}
	}

	/*
	 * game over
	 */
	@Override
	public boolean onGameOver(GameEngine engine, int playerID) {
		if((engine.statc[0] == 0) && (engine.gameActive)) {
			engine.blockOutlineType = GameEngine.BLOCK_OUTLINE_NORMAL;
		}
		return false;
	}

	/*
	 * Render results screen
	 */
	@Override
	public void renderResult(GameEngine engine, int playerID) {
		drawResultStats(engine, playerID, receiver, 0, EventReceiver.COLOR_BLUE, STAT_SCORE, STAT_LINES);
		if(engine.statistics.level >= 20) {
			drawResult(engine, playerID, receiver, 4, EventReceiver.COLOR_BLUE,
					"BONUS LINE", String.format("%10d", bonusLines));
		} else {
			drawResultStats(engine, playerID, receiver, 4, EventReceiver.COLOR_BLUE, STAT_LEVEL);
		}
		drawResult(engine, playerID, receiver, 6, EventReceiver.COLOR_BLUE,
				"TOTAL TIME", String.format("%10s", GeneralUtil.getTime(engine.statistics.time)),
				"LV20- TIME", String.format("%10s", GeneralUtil.getTime(engine.statistics.time - bonusTime)),
				"BONUS TIME", String.format("%10s", GeneralUtil.getTime(bonusTime)));

		drawResultStats(engine, playerID, receiver, 12, EventReceiver.COLOR_BLUE, STAT_SPL, STAT_LPM);
		drawResultRank(engine, playerID, receiver, 16, EventReceiver.COLOR_BLUE, rankingRank);
	}

	/*
	 * Called when saving replay
	 */
	@Override
	public void saveReplay(GameEngine engine, int playerID, CustomProperties prop) {
		saveSetting(prop);

		// Update rankings
		if( (owner.replayMode == false) && (big == false) && ((startlevel == 0) || (startlevel == 20)) && (engine.ai == null) ) {
			int gametype = (startlevel == 20) ? 1 : 0;
			updateRanking(engine.statistics.score, engine.statistics.lines, engine.statistics.time, gametype);

			if(rankingRank != -1) {
				saveRanking(owner.modeConfig, engine.ruleopt.strRuleName);
				receiver.saveModeConfig(owner.modeConfig);
			}
		}
	}

	/**
	 * Load settings from property file
	 * @param prop Property file
	 */
	private void loadSetting(CustomProperties prop) {
		startlevel = prop.getProperty("marathonplus.startlevel", 0);
		tspinEnableType = prop.getProperty("marathonplus.tspinEnableType", 1);
		enableTSpin = prop.getProperty("marathonplus.enableTSpin", true);
		enableTSpinKick = prop.getProperty("marathonplus.enableTSpinKick", true);
		enableB2B = prop.getProperty("marathonplus.enableB2B", true);
		enableCombo = prop.getProperty("marathonplus.enableCombo", true);
		big = prop.getProperty("marathonplus.big", false);
		version = prop.getProperty("marathonplus.version", 0);
	}

	/**
	 * Save settings to property file
	 * @param prop Property file
	 */
	private void saveSetting(CustomProperties prop) {
		prop.setProperty("marathonplus.startlevel", startlevel);
		prop.setProperty("marathonplus.tspinEnableType", tspinEnableType);
		prop.setProperty("marathonplus.enableTSpin", enableTSpin);
		prop.setProperty("marathonplus.enableTSpinKick", enableTSpinKick);
		prop.setProperty("marathonplus.enableB2B", enableB2B);
		prop.setProperty("marathonplus.enableCombo", enableCombo);
		prop.setProperty("marathonplus.big", big);
		prop.setProperty("marathonplus.version", version);
	}

	/**
	 * Read rankings from property file
	 * @param prop Property file
	 * @param ruleName Rule name
	 */
	private void loadRanking(CustomProperties prop, String ruleName) {
		for(int i = 0; i < RANKING_MAX; i++) {
			for(int j = 0; j < GAMETYPE_MAX; j++) {
				rankingScore[j][i] = prop.getProperty("marathonplus.ranking." + ruleName + "." + j + ".score." + i, 0);
				rankingLines[j][i] = prop.getProperty("marathonplus.ranking." + ruleName + "." + j + ".lines." + i, 0);
				rankingTime[j][i] = prop.getProperty("marathonplus.ranking." + ruleName + "." + j + ".time." + i, 0);
			}
		}
	}

	/**
	 * Save rankings to property file
	 * @param prop Property file
	 * @param ruleName Rule name
	 */
	private void saveRanking(CustomProperties prop, String ruleName) {
		for(int i = 0; i < RANKING_MAX; i++) {
			for(int j = 0; j < GAMETYPE_MAX; j++) {
				prop.setProperty("marathonplus.ranking." + ruleName + "." + j + ".score." + i, rankingScore[j][i]);
				prop.setProperty("marathonplus.ranking." + ruleName + "." + j + ".lines." + i, rankingLines[j][i]);
				prop.setProperty("marathonplus.ranking." + ruleName + "." + j + ".time." + i, rankingTime[j][i]);
			}
		}
	}

	/**
	 * Update rankings
	 * @param sc Score
	 * @param li Lines
	 * @param time Time
	 */
	private void updateRanking(int sc, int li, int time, int type) {
		rankingRank = checkRanking(sc, li, time, type);

		if(rankingRank != -1) {
			// Shift down ranking entries
			for(int i = RANKING_MAX - 1; i > rankingRank; i--) {
				rankingScore[type][i] = rankingScore[type][i - 1];
				rankingLines[type][i] = rankingLines[type][i - 1];
				rankingTime[type][i] = rankingTime[type][i - 1];
			}

			// Add new data
			rankingScore[type][rankingRank] = sc;
			rankingLines[type][rankingRank] = li;
			rankingTime[type][rankingRank] = time;
		}
	}

	/**
	 * Calculate ranking position
	 * @param sc Score
	 * @param li Lines
	 * @param time Time
	 * @return Position (-1 if unranked)
	 */
	private int checkRanking(int sc, int li, int time, int type) {
		for(int i = 0; i < RANKING_MAX; i++) {
			if(sc > rankingScore[type][i]) {
				return i;
			} else if((sc == rankingScore[type][i]) && (li > rankingLines[type][i])) {
				return i;
			} else if((sc == rankingScore[type][i]) && (li == rankingLines[type][i]) && (time < rankingTime[type][i])) {
				return i;
			}
		}

		return -1;
	}
}
