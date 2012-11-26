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

import mu.nu.nullpo.game.component.Controller;
import mu.nu.nullpo.game.event.EventReceiver;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.game.subsystem.mode.menu.BooleanMenuItem;
import mu.nu.nullpo.game.subsystem.mode.menu.IntegerMenuItem;
import mu.nu.nullpo.game.subsystem.mode.menu.OnOffMenuItem;
import mu.nu.nullpo.game.subsystem.mode.menu.TimeMenuItem;
import mu.nu.nullpo.util.CustomProperties;
import mu.nu.nullpo.util.GeneralUtil;

/**
 * SPEED MANIA Mode
 */
public class SpeedManiaMode extends AbstractMode {
	/** Current version */
	private static final int CURRENT_VERSION = 3;

	/** ARE table */
	private static final int[] tableARE       = {15, 11, 11,  5,  4,  3};

	/** ARE after line clear table */
	private static final int[] tableARELine   = {11,  5,  5,  4,  4,  3};

	/** Line clear time table */
	private static final int[] tableLineDelay = {12,  6,  6,  7,  5,  4};

	/** 固定 time table */
	private static final int[] tableLockDelay = {31, 27, 23, 19, 16, 16};

	/** DAS table */
	private static final int[] tableDAS       = {11, 11, 10,  9,  7,  7};

	/** BGM fadeout levels */
	private static final int[] tableBGMFadeout = {280, 480, -1};

	/** BGM change levels */
	private static final int[] tableBGMChange  = {300, 500, -1};

	/** 段位のName */
	private static final String[] tableGradeName = {"", "M", "GM"};

	/** 裏段位のName */
	private static final String[] tableSecretGradeName =
	{
		 "9",  "8",  "7",  "6",  "5",  "4",  "3",  "2",  "1",	//  0～ 8
		"S1", "S2", "S3", "S4", "S5", "S6", "S7", "S8", "S9",	//  9～17
		"GM"													// 18
	};

	/** LV999 roll time */
	private static final int ROLLTIMELIMIT = 1982;

	/** Number of entries in rankings */
	private static final int RANKING_MAX = 10;

	/** Number of sections */
	private static final int SECTION_MAX = 10;

	/** Default section time */
	private static final int DEFAULT_SECTION_TIME = 2520;

	/** Next Section の level (これ-1のときに levelストップする) */
	private int nextseclv;

	/** Levelが増えた flag */
	private boolean lvupflag;

	/** Combo bonus */
	private int comboValue;

	/** Most recent increase in score */
	private int lastscore;

	/** 獲得Render scoreがされる残り time */
	private int scgettime;

	/** Roll 経過 time */
	private int rolltime;

	/** Roll started flag */
	private boolean rollstarted;

	/** Current BGM */
	private int bgmlv;

	/** 段位 */
	private int grade;

	/** 段位表示を光らせる残り frame count */
	private int gradeflash;

	/** 裏段位 */
	private int secretGrade;

	/** Section Time */
	private int[] sectiontime;

	/** 新記録が出たSection はtrue */
	private boolean[] sectionIsNewRecord;

	/** Cleared Section count */
	private int sectionscomp;

	/** Average Section Time */
	private int sectionavgtime;

	/** 直前のSection Time */
	private int sectionlasttime;

	/** AC medal 状態 */
	private int medalAC;

	/** ST medal 状態 */
	private int medalST;

	/** SK medal 状態 */
	private int medalSK;

	/** RE medal 状態 */
	private int medalRE;

	/** RO medal 状態 */
	private int medalRO;

	/** CO medal 状態 */
	private int medalCO;

	/** 150個以上Blockがあるとtrue, 70個まで減らすとfalseになる */
	private boolean recoveryFlag;

	/** rotationした合計 count (Maximum4個ずつ増える) */
	private int rotateCount;

	/** Section Time記録表示中ならtrue */
	private boolean isShowBestSectionTime;

	/** Level at start */
	private IntegerMenuItem startlevel;

	/** When true, levelstop sound is enabled */
	private BooleanMenuItem lvstopse;

	/** BigMode */
	private BooleanMenuItem big;

	/** LV500の足切りTime */
	private TimeMenuItem lv500torikan;

	/** When true, section time display is enabled */
	private BooleanMenuItem showsectiontime;

	/** Version */
	private int version;

	/** Current round's ranking rank */
	private int rankingRank;

	/** Rankings' 段位 */
	private int[] rankingGrade;

	/** Rankings'  level */
	private int[] rankingLevel;

	/** Rankings' times */
	private int[] rankingTime;

	/** Section Time記録 */
	private int[] bestSectionTime;

	public SpeedManiaMode() {
		propName = "speedmania";

		startlevel = new IntegerMenuItem("startlevel", "LEVEL", EventReceiver.COLOR_BLUE, 0, 0, 9) {
			public String getValueString() {
				return String.valueOf(value * 100);
			}
		};
		lvstopse = new OnOffMenuItem("lvstopse", "LVSTOPSE", EventReceiver.COLOR_BLUE, false);
		big = new OnOffMenuItem("big", "BIG", EventReceiver.COLOR_BLUE, false);
		showsectiontime = new OnOffMenuItem("big", "BIG", EventReceiver.COLOR_BLUE, true);
		lv500torikan = new TimeMenuItem("lv500torikan", "LV500LIMIT", EventReceiver.COLOR_BLUE, 12300, 0, 72000) {
			public String getValueString() {
				return (value == 0) ? "NONE" : GeneralUtil.getTime(value);
			}
		};
		menu.add(startlevel);
		menu.add(lvstopse);
		menu.add(showsectiontime);
		menu.add(big);
		menu.add(lv500torikan);
	}
	/*
	 * Mode name
	 */
	@Override
	public String getName() {
		return "SPEED MANIA";
	}

	/*
	 * Initialization
	 */
	@Override
	public void playerInit(GameEngine engine, int playerID) {
		owner = engine.owner;
		receiver = engine.owner.receiver;

		nextseclv = 0;
		lvupflag = true;
		comboValue = 0;
		lastscore = 0;
		scgettime = 0;
		rolltime = 0;
		rollstarted = false;
		bgmlv = 0;
		grade = 0;
		gradeflash = 0;
		secretGrade = 0;
		sectiontime = new int[SECTION_MAX];
		sectionIsNewRecord = new boolean[SECTION_MAX];
		sectionscomp = 0;
		sectionavgtime = 0;
		sectionlasttime = 0;
		medalAC = 0;
		medalST = 0;
		medalSK = 0;
		medalRE = 0;
		medalRO = 0;
		medalCO = 0;
		recoveryFlag = false;
		rotateCount = 0;
		isShowBestSectionTime = false;

		rankingRank = -1;
		rankingGrade = new int[RANKING_MAX];
		rankingLevel = new int[RANKING_MAX];
		rankingTime = new int[RANKING_MAX];
		bestSectionTime = new int[SECTION_MAX];

		engine.tspinEnable = false;
		engine.b2bEnable = false;
		engine.comboType = GameEngine.COMBO_TYPE_DOUBLE;
		engine.framecolor = GameEngine.FRAME_COLOR_RED;
		engine.bighalf = true;
		engine.bigmove = true;
		engine.staffrollEnable = true;
		engine.staffrollNoDeath = true;

		if(owner.replayMode == false) {
			loadSetting(owner.modeConfig);
			loadRanking(owner.modeConfig, engine.ruleopt.strRuleName);
			version = CURRENT_VERSION;
		} else {
			for(int i = 0; i < SECTION_MAX; i++) {
				bestSectionTime[i] = DEFAULT_SECTION_TIME;
			}
			loadSetting(owner.replayProp);
			version = owner.replayProp.getProperty("speedmania.version", 0);
		}

		owner.backgroundStatus.bg = startlevel.value;
	}

	/**
	 * Set BGM at start of game
	 * @param engine GameEngine
	 */
	private void setStartBgmlv(GameEngine engine) {
		bgmlv = 0;
		while((tableBGMChange[bgmlv] != -1) && (engine.statistics.level >= tableBGMChange[bgmlv])) bgmlv++;
	}

	/**
	 * Update falling speed
	 * @param engine GameEngine
	 */
	private void setSpeed(GameEngine engine) {
		engine.speed.gravity = -1;

		int section = engine.statistics.level / 100;
		if(section > tableARE.length - 1) section = tableARE.length - 1;
		engine.speed.are = tableARE[section];
		engine.speed.areLine = tableARELine[section];
		engine.speed.lineDelay = tableLineDelay[section];
		engine.speed.lockDelay = tableLockDelay[section];
		engine.speed.das = tableDAS[section];
	}

	/**
	 * Update average section time
	 */
	private void setAverageSectionTime() {
		if(sectionscomp > 0) {
			int temp = 0;
			for(int i = startlevel.value; i < startlevel.value + sectionscomp; i++) {
				if((i >= 0) && (i < sectiontime.length)) temp += sectiontime[i];
			}
			sectionavgtime = temp / sectionscomp;
		} else {
			sectionavgtime = 0;
		}
	}

	/**
	 * ST medal check
	 * @param engine GameEngine
	 * @param sectionNumber Section number
	 */
	private void stMedalCheck(GameEngine engine, int sectionNumber) {
		int best = bestSectionTime[sectionNumber];

		if(sectionlasttime < best) {
			if(medalST < 3) {
				engine.playSE("medal");
				medalST = 3;
			}
			if(!owner.replayMode) {
				sectionIsNewRecord[sectionNumber] = true;
			}
		} else if((sectionlasttime < best + 300) && (medalST < 2)) {
			engine.playSE("medal");
			medalST = 2;
		} else if((sectionlasttime < best + 600) && (medalST < 1)) {
			engine.playSE("medal");
			medalST = 1;
		}
	}

	/**
	 * RO medal check
	 * @param engine Engine
	 */
	private void roMedalCheck(GameEngine engine) {
		float rotateAverage = (float)rotateCount / (float)engine.statistics.totalPieceLocked;

		if((rotateAverage >= 1.2f) && (medalRO < 3)) {
			engine.playSE("medal");
			medalRO++;
		}
	}

	/**
	 *  medal の文字色を取得
	 * @param medalColor  medal 状態
	 * @return  medal の文字色
	 */
	private int getMedalFontColor(int medalColor) {
		if(medalColor == 1) return EventReceiver.COLOR_RED;
		if(medalColor == 2) return EventReceiver.COLOR_WHITE;
		if(medalColor == 3) return EventReceiver.COLOR_YELLOW;
		return -1;
	}

	/*
	 * Called at settings screen
	 */
	@Override
	public boolean onSetting(GameEngine engine, int playerID) {
		// Menu
		if(engine.owner.replayMode == false) {
			// Configuration changes
			updateMenu(engine);

			//  section time display切替
			if(engine.ctrl.isPush(Controller.BUTTON_F) && (menuTime >= 5)) {
				engine.playSE("change");
				isShowBestSectionTime = !isShowBestSectionTime;
			}

			// 決定
			if(engine.ctrl.isPush(Controller.BUTTON_A) && (menuTime >= 5)) {
				engine.playSE("decide");
				saveSetting(owner.modeConfig);
				receiver.saveModeConfig(owner.modeConfig);
				isShowBestSectionTime = false;
				sectionscomp = 0;
				return false;
			}

			// Cancel
			if(engine.ctrl.isPush(Controller.BUTTON_B)) {
				engine.quitflag = true;
			}

			menuTime++;
		} else {
			menuTime++;
			menuCursor = -1;

			if(menuTime >= 60) {
				return false;
			}
		}

		return true;
	}

	/*
	 * Called at game start
	 */
	@Override
	public void startGame(GameEngine engine, int playerID) {
		engine.statistics.level = startlevel.value * 100;

		nextseclv = engine.statistics.level + 100;
		if(engine.statistics.level < 0) nextseclv = 100;
		if(engine.statistics.level >= 900) nextseclv = 999;

		owner.backgroundStatus.bg = engine.statistics.level / 100;

		engine.big = big.value;

		setSpeed(engine);
		setStartBgmlv(engine);
		owner.bgmStatus.bgm = bgmlv + 1;
	}

	/*
	 * Render score
	 */
	@Override
	public void renderLast(GameEngine engine, int playerID) {
		receiver.drawScoreFont(engine, playerID, 0, 0, "SPEED MANIA", EventReceiver.COLOR_RED);

		if( (engine.stat == GameEngine.STAT_SETTING) || ((engine.stat == GameEngine.STAT_RESULT) && (owner.replayMode == false)) ) {
			if((owner.replayMode == false) && (startlevel.value == 0) && (big.value == false) && (engine.ai == null)) {
				if(!isShowBestSectionTime) {
					// Rankings
					float scale = (receiver.getNextDisplayType() == 2) ? 0.5f : 1.0f;
					int topY = (receiver.getNextDisplayType() == 2) ? 5 : 3;
					receiver.drawScoreFont(engine, playerID, 3, topY-1, "GRADE LEVEL TIME", EventReceiver.COLOR_BLUE, scale);

					for(int i = 0; i < RANKING_MAX; i++) {
						receiver.drawScoreFont(engine, playerID, 0, topY+i, String.format("%2d", i + 1), EventReceiver.COLOR_YELLOW, scale);
						receiver.drawScoreFont(engine, playerID, 3, topY+i, tableGradeName[rankingGrade[i]], (i == rankingRank), scale);
						receiver.drawScoreFont(engine, playerID, 9, topY+i, String.valueOf(rankingLevel[i]), (i == rankingRank), scale);
						receiver.drawScoreFont(engine, playerID, 15, topY+i, GeneralUtil.getTime(rankingTime[i]), (i == rankingRank), scale);
					}

					receiver.drawScoreFont(engine, playerID, 0, 17, "F:VIEW SECTION TIME", EventReceiver.COLOR_GREEN);
				} else {
					// Section Time
					receiver.drawScoreFont(engine, playerID, 0, 2, "SECTION TIME", EventReceiver.COLOR_BLUE);

					int totalTime = 0;
					for(int i = 0; i < SECTION_MAX; i++) {
						int temp = Math.min(i * 100, 999);
						int temp2 = Math.min(((i + 1) * 100) - 1, 999);

						String strSectionTime;
						strSectionTime = String.format("%3d-%3d %s", temp, temp2, GeneralUtil.getTime(bestSectionTime[i]));

						receiver.drawScoreFont(engine, playerID, 0, 3 + i, strSectionTime, sectionIsNewRecord[i]);

						totalTime += bestSectionTime[i];
					}

					receiver.drawScoreFont(engine, playerID, 0, 14, "TOTAL", EventReceiver.COLOR_BLUE);
					receiver.drawScoreFont(engine, playerID, 0, 15, GeneralUtil.getTime(totalTime));
					receiver.drawScoreFont(engine, playerID, 9, 14, "AVERAGE", EventReceiver.COLOR_BLUE);
					receiver.drawScoreFont(engine, playerID, 9, 15, GeneralUtil.getTime(totalTime / SECTION_MAX));

					receiver.drawScoreFont(engine, playerID, 0, 17, "F:VIEW RANKING", EventReceiver.COLOR_GREEN);
				}
			}
		} else {
			// 段位
			if((grade >= 1) && (grade < tableGradeName.length)) {
				receiver.drawScoreFont(engine, playerID, 0, 2, "GRADE", EventReceiver.COLOR_BLUE);
				receiver.drawScoreFont(engine, playerID, 0, 3, tableGradeName[grade], ((gradeflash > 0) && (gradeflash % 4 == 0)));
			}

			// Score
			receiver.drawScoreFont(engine, playerID, 0, 5, "SCORE", EventReceiver.COLOR_BLUE);
			String strScore;
			if((lastscore == 0) || (scgettime <= 0)) {
				strScore = String.valueOf(engine.statistics.score);
			} else {
				strScore = String.valueOf(engine.statistics.score) + "\n(+" + String.valueOf(lastscore) + ")";
			}
			receiver.drawScoreFont(engine, playerID, 0, 6, strScore);

			//  level
			receiver.drawScoreFont(engine, playerID, 0, 9, "LEVEL", EventReceiver.COLOR_BLUE);
			int tempLevel = engine.statistics.level;
			if(tempLevel < 0) tempLevel = 0;
			String strLevel = String.format("%3d", tempLevel);
			receiver.drawScoreFont(engine, playerID, 0, 10, strLevel);

			int speed = engine.speed.gravity / 128;
			if(engine.speed.gravity < 0) speed = 40;
			receiver.drawSpeedMeter(engine, playerID, 0, 11, speed);

			receiver.drawScoreFont(engine, playerID, 0, 12, String.format("%3d", nextseclv));

			// Time
			receiver.drawScoreFont(engine, playerID, 0, 14, "TIME", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 0, 15, GeneralUtil.getTime(engine.statistics.time));

			// Roll 残り time
			if((engine.gameActive) && (engine.ending == 2)) {
				int time = ROLLTIMELIMIT - rolltime;
				if(time < 0) time = 0;
				receiver.drawScoreFont(engine, playerID, 0, 17, "ROLL TIME", EventReceiver.COLOR_BLUE);
				receiver.drawScoreFont(engine, playerID, 0, 18, GeneralUtil.getTime(time), ((time > 0) && (time < 10 * 60)));
			}

			//  medal
			if(medalAC >= 1) receiver.drawScoreFont(engine, playerID, 0, 20, "AC", getMedalFontColor(medalAC));
			if(medalST >= 1) receiver.drawScoreFont(engine, playerID, 3, 20, "ST", getMedalFontColor(medalST));
			if(medalSK >= 1) receiver.drawScoreFont(engine, playerID, 0, 21, "SK", getMedalFontColor(medalSK));
			if(medalRE >= 1) receiver.drawScoreFont(engine, playerID, 3, 21, "RE", getMedalFontColor(medalRE));
			if(medalRO >= 1) receiver.drawScoreFont(engine, playerID, 0, 22, "SK", getMedalFontColor(medalRO));
			if(medalCO >= 1) receiver.drawScoreFont(engine, playerID, 3, 22, "CO", getMedalFontColor(medalCO));

			// Section Time
			if((showsectiontime.value) && (sectiontime != null)) {
				int x = (receiver.getNextDisplayType() == 2) ? 8 : 12;
				int x2 = (receiver.getNextDisplayType() == 2) ? 9 : 12;

				receiver.drawScoreFont(engine, playerID, x, 2, "SECTION TIME", EventReceiver.COLOR_BLUE);

				for(int i = 0; i < sectiontime.length; i++) {
					if(sectiontime[i] > 0) {
						int temp = i * 100;
						if(temp > 999) temp = 999;

						int section = engine.statistics.level / 100;
						String strSeparator = " ";
						if((i == section) && (engine.ending == 0)) strSeparator = "b";

						String strSectionTime;
						strSectionTime = String.format("%3d%s%s", temp, strSeparator, GeneralUtil.getTime(sectiontime[i]));

						receiver.drawScoreFont(engine, playerID, x, 3 + i, strSectionTime, sectionIsNewRecord[i]);
					}
				}

				if(sectionavgtime > 0) {
					receiver.drawScoreFont(engine, playerID, x2, 14, "AVERAGE", EventReceiver.COLOR_BLUE);
					receiver.drawScoreFont(engine, playerID, x2, 15, GeneralUtil.getTime(sectionavgtime));
				}
			}
		}
	}

	/*
	 * 移動中の処理
	 */
	@Override
	public boolean onMove(GameEngine engine, int playerID) {
		// 新規ピース出現時
		if((engine.ending == 0) && (engine.statc[0] == 0) && (engine.holdDisable == false) && (!lvupflag)) {
			// Level up
			if(engine.statistics.level < nextseclv - 1) {
				engine.statistics.level++;
				if((engine.statistics.level == nextseclv - 1) && (lvstopse.value)) engine.playSE("levelstop");
			}
			levelUp(engine);

			// RE medal (旧)
			if((engine.timerActive == true) && (medalRE < 3) && (version <= 2)) {
				int blocks = engine.field.getHowManyBlocks();

				if(recoveryFlag == false) {
					if(blocks >= 150) {
						recoveryFlag = true;
					}
				} else {
					if(blocks <= 70) {
						recoveryFlag = false;
						engine.playSE("medal");
						medalRE++;
					}
				}
			}
		}
		if( (engine.ending == 0) && (engine.statc[0] > 0) && ((version >= 2) || (engine.holdDisable == false)) ) {
			lvupflag = false;
		}

		// Endingスタート
		if((engine.ending == 2) && (rollstarted == false)) {
			rollstarted = true;
		}

		return false;
	}

	/*
	 * ARE中の処理
	 */
	@Override
	public boolean onARE(GameEngine engine, int playerID) {
		// 最後の frame
		if((engine.ending == 0) && (engine.statc[0] >= engine.statc[1] - 1) && (!lvupflag)) {
			if(engine.statistics.level < nextseclv - 1) {
				engine.statistics.level++;
				if((engine.statistics.level == nextseclv - 1) && (lvstopse.value)) engine.playSE("levelstop");
			}
			levelUp(engine);
			lvupflag = true;
		}

		return false;
	}

	/**
	 *  levelが上がったときの共通処理
	 */
	private void levelUp(GameEngine engine) {
		// Meter
		engine.meterValue = ((engine.statistics.level % 100) * receiver.getMeterMax(engine)) / 99;
		engine.meterColor = GameEngine.METER_COLOR_GREEN;
		if(engine.statistics.level % 100 >= 50) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
		if(engine.statistics.level % 100 >= 80) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
		if(engine.statistics.level == nextseclv - 1) engine.meterColor = GameEngine.METER_COLOR_RED;

		// 速度変更
		setSpeed(engine);

		// BGM fadeout
		if((tableBGMFadeout[bgmlv] != -1) && (engine.statistics.level >= tableBGMFadeout[bgmlv]))
			owner.bgmStatus.fadesw  = true;

		// RE medal
		if((engine.timerActive == true) && (medalRE < 3) && (version >= 3)) {
			int blocks = engine.field.getHowManyBlocks();

			if(recoveryFlag == false) {
				if(blocks >= 150) {
					recoveryFlag = true;
				}
			} else {
				if(blocks <= 70) {
					recoveryFlag = false;
					engine.playSE("medal");
					medalRE++;
				}
			}
		}
	}

	/*
	 * Calculate score
	 */
	@Override
	public void calcScore(GameEngine engine, int playerID, int lines) {
		// Combo
		if(lines == 0) {
			comboValue = 1;
		} else {
			comboValue = comboValue + (2 * lines) - 2;
			if(comboValue < 1) comboValue = 1;
		}

		// RO medal 用カウント
		int rotateTemp = engine.nowPieceRotateCount;
		if(rotateTemp > 4) rotateTemp = 4;
		rotateCount += rotateTemp;

		if((lines >= 1) && (engine.ending == 0)) {
			// 4-line clearカウント
			if(lines >= 4) {
				// SK medal
				if(big.value) {
					if((engine.statistics.totalFour == 1) || (engine.statistics.totalFour == 2) || (engine.statistics.totalFour == 4)) {
						engine.playSE("medal");
						medalSK++;
					}
				} else {
					if((engine.statistics.totalFour == 5) || (engine.statistics.totalFour == 10) || (engine.statistics.totalFour == 17)) {
						engine.playSE("medal");
						medalSK++;
					}
				}
			}

			// AC medal
			if(engine.field.isEmpty()) {
				engine.playSE("bravo");

				if(medalAC < 3) {
					engine.playSE("medal");
					medalAC++;
				}
			}

			// CO medal
			if(big.value) {
				if((engine.combo >= 2) && (medalCO < 1)) {
					engine.playSE("medal");
					medalCO = 1;
				} else if((engine.combo >= 3) && (medalCO < 2)) {
					engine.playSE("medal");
					medalCO = 2;
				} else if((engine.combo >= 4) && (medalCO < 3)) {
					engine.playSE("medal");
					medalCO = 3;
				}
			} else {
				if((engine.combo >= 4) && (medalCO < 1)) {
					engine.playSE("medal");
					medalCO = 1;
				} else if((engine.combo >= 5) && (medalCO < 2)) {
					engine.playSE("medal");
					medalCO = 2;
				} else if((engine.combo >= 7) && (medalCO < 3)) {
					engine.playSE("medal");
					medalCO = 3;
				}
			}

			// Level up
			int levelb = engine.statistics.level;
			engine.statistics.level += lines;
			levelUp(engine);

			if(engine.statistics.level >= 999) {
				// Ending
				engine.playSE("endingstart");
				engine.statistics.level = 999;
				engine.timerActive = false;
				engine.ending = 2;

				grade = 2;
				gradeflash = 180;

				// Section Timeを記録
				sectionlasttime = sectiontime[levelb / 100];
				sectionscomp++;
				setAverageSectionTime();

				// ST medal
				stMedalCheck(engine, levelb / 100);

				// RO medal
				roMedalCheck(engine);
			} else if((nextseclv == 500) && (engine.statistics.level >= 500) && (lv500torikan.value > 0) && (engine.statistics.time > lv500torikan.value)) {
				//  level500とりカン
				engine.playSE("endingstart");
				engine.statistics.level = 500;
				engine.timerActive = false;
				engine.ending = 2;

				// BGM切り替え
				if((tableBGMChange[bgmlv] != -1) && (engine.statistics.level >= tableBGMChange[bgmlv])) {
					bgmlv++;
					owner.bgmStatus.fadesw = false;
					owner.bgmStatus.bgm = bgmlv + 1;
				}

				// Section Timeを記録
				sectionlasttime = sectiontime[levelb / 100];
				sectionscomp++;
				setAverageSectionTime();

				// ST medal
				stMedalCheck(engine, levelb / 100);
			} else if(engine.statistics.level >= nextseclv) {
				// Next Section
				engine.playSE("levelup");

				// Background切り替え
				owner.backgroundStatus.fadesw = true;
				owner.backgroundStatus.fadecount = 0;
				owner.backgroundStatus.fadebg = nextseclv / 100;

				// BGM切り替え
				if((tableBGMChange[bgmlv] != -1) && (engine.statistics.level >= tableBGMChange[bgmlv])) {
					bgmlv++;
					owner.bgmStatus.fadesw = false;
					owner.bgmStatus.bgm = bgmlv + 1;
				}

				// Section Timeを記録
				sectionlasttime = sectiontime[levelb / 100];
				sectionscomp++;
				setAverageSectionTime();

				// ST medal
				stMedalCheck(engine, levelb / 100);

				// RO medal
				if((nextseclv == 300) || (nextseclv == 700)) roMedalCheck(engine);

				// 段位上昇
				if(nextseclv == 500) {
					grade = 1;
					gradeflash = 180;
				}

				// Update level for next section
				nextseclv += 100;
				if(nextseclv > 999) nextseclv = 999;
			} else if((engine.statistics.level == nextseclv - 1) && (lvstopse.value)) {
				engine.playSE("levelstop");
			}

			// Calculate score
			int manuallock = 0;
			if(engine.manualLock == true) manuallock = 1;

			int bravo = 1;
			if(engine.field.isEmpty()) bravo = 4;

			int speedBonus = engine.getLockDelay() - engine.statc[0];
			if(speedBonus < 0) speedBonus = 0;

			lastscore = ((levelb + lines)/4 + engine.softdropFall + manuallock) * lines * comboValue * bravo +
						(engine.statistics.level / 2) + (speedBonus * 7);
			engine.statistics.score += lastscore;
			scgettime = 120;
		}
	}

	/*
	 * 各 frame の終わりの処理
	 */
	@Override
	public void onLast(GameEngine engine, int playerID) {
		// 段位上昇時のフラッシュ
		if(gradeflash > 0) gradeflash--;

		// 獲得Render score
		if(scgettime > 0) scgettime--;

		// Section Time増加
		if((engine.timerActive) && (engine.ending == 0)) {
			int section = engine.statistics.level / 100;

			if((section >= 0) && (section < sectiontime.length)) {
				sectiontime[section]++;
			}
		}

		// Ending
		if((engine.gameActive) && (engine.ending == 2)) {
			if((version >= 1) && (engine.ctrl.isPress(Controller.BUTTON_F)))
				rolltime += 5;
			else
				rolltime += 1;

			// Time meter
			int remainRollTime = ROLLTIMELIMIT - rolltime;
			engine.meterValue = (remainRollTime * receiver.getMeterMax(engine)) / ROLLTIMELIMIT;
			engine.meterColor = GameEngine.METER_COLOR_GREEN;
			if(remainRollTime <= 30*60) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
			if(remainRollTime <= 20*60) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
			if(remainRollTime <= 10*60) engine.meterColor = GameEngine.METER_COLOR_RED;

			// Roll 終了
			if(rolltime >= ROLLTIMELIMIT) {
				engine.gameEnded();
				engine.resetStatc();
				engine.stat = GameEngine.STAT_EXCELLENT;
			}
		}
	}

	/*
	 * Called at game over
	 */
	@Override
	public boolean onGameOver(GameEngine engine, int playerID) {
		if(engine.statc[0] == 0) {
			secretGrade = engine.field.getSecretGrade();
		}
		return false;
	}

	/*
	 * 結果画面
	 */
	@Override
	public void renderResult(GameEngine engine, int playerID) {
		receiver.drawMenuFont(engine, playerID, 0, 0, "kn PAGE" + (engine.statc[1] + 1) + "/3", EventReceiver.COLOR_RED);

		if(engine.statc[1] == 0) {
			if((grade >= 1) && (grade < tableGradeName.length)) {
				receiver.drawMenuFont(engine, playerID, 0, 2, "GRADE", EventReceiver.COLOR_BLUE);
				String strGrade = String.format("%10s", tableGradeName[grade]);
				receiver.drawMenuFont(engine, playerID, 0, 3, strGrade);
			}

			drawResultStats(engine, playerID, receiver, 4, EventReceiver.COLOR_BLUE,
					STAT_SCORE, STAT_LINES, STAT_LEVEL_MANIA, STAT_TIME);
			drawResultRank(engine, playerID, receiver, 12, EventReceiver.COLOR_BLUE, rankingRank);
			if(secretGrade > 4) {
				drawResult(engine, playerID, receiver, 14, EventReceiver.COLOR_BLUE,
						"S. GRADE", String.format("%10s", tableSecretGradeName[secretGrade-1]));
			}
		} else if(engine.statc[1] == 1) {
			receiver.drawMenuFont(engine, playerID, 0, 2, "SECTION", EventReceiver.COLOR_BLUE);

			for(int i = 0; i < sectiontime.length; i++) {
				if(sectiontime[i] > 0) {
					receiver.drawMenuFont(engine, playerID, 2, 3 + i, GeneralUtil.getTime(sectiontime[i]), sectionIsNewRecord[i]);
				}
			}

			if(sectionavgtime > 0) {
				receiver.drawMenuFont(engine, playerID, 0, 14, "AVERAGE", EventReceiver.COLOR_BLUE);
				receiver.drawMenuFont(engine, playerID, 2, 15, GeneralUtil.getTime(sectionavgtime));
			}
		} else if(engine.statc[1] == 2) {
			receiver.drawMenuFont(engine, playerID, 0, 2, "MEDAL", EventReceiver.COLOR_BLUE);
			if(medalAC >= 1) receiver.drawMenuFont(engine, playerID, 5, 3, "AC", getMedalFontColor(medalAC));
			if(medalST >= 1) receiver.drawMenuFont(engine, playerID, 8, 3, "ST", getMedalFontColor(medalST));
			if(medalSK >= 1) receiver.drawMenuFont(engine, playerID, 5, 4, "SK", getMedalFontColor(medalSK));
			if(medalRE >= 1) receiver.drawMenuFont(engine, playerID, 8, 4, "RE", getMedalFontColor(medalRE));
			if(medalRO >= 1) receiver.drawMenuFont(engine, playerID, 5, 5, "SK", getMedalFontColor(medalRO));
			if(medalCO >= 1) receiver.drawMenuFont(engine, playerID, 8, 5, "CO", getMedalFontColor(medalCO));

			drawResultStats(engine, playerID, receiver, 6, EventReceiver.COLOR_BLUE,
					STAT_LPM, STAT_SPM, STAT_PIECE, STAT_PPS);
		}
	}

	/*
	 * 結果画面の処理
	 */
	@Override
	public boolean onResult(GameEngine engine, int playerID) {
		// ページ切り替え
		if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_UP)) {
			engine.statc[1]--;
			if(engine.statc[1] < 0) engine.statc[1] = 2;
			engine.playSE("change");
		}
		if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_DOWN)) {
			engine.statc[1]++;
			if(engine.statc[1] > 2) engine.statc[1] = 0;
			engine.playSE("change");
		}
		//  section time display切替
		if(engine.ctrl.isPush(Controller.BUTTON_F)) {
			engine.playSE("change");
			isShowBestSectionTime = !isShowBestSectionTime;
		}

		return false;
	}

	/*
	 * リプレイ保存
	 */
	@Override
	public void saveReplay(GameEngine engine, int playerID, CustomProperties prop) {
		saveSetting(owner.replayProp);
		owner.replayProp.setProperty("speedmania.version", version);

		// Update rankings
		if((owner.replayMode == false) && (startlevel.value == 0) && (big.value == false) && (engine.ai == null)) {
			updateRanking(grade, engine.statistics.level, engine.statistics.time);
			if(medalST == 3) updateBestSectionTime();

			if((rankingRank != -1) || (medalST == 3)) {
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
		for(int i = 0; i < RANKING_MAX; i++) {
			rankingGrade[i] = prop.getProperty("speedmania.ranking." + ruleName + ".grade." + i, 0);
			rankingLevel[i] = prop.getProperty("speedmania.ranking." + ruleName + ".level." + i, 0);
			rankingTime[i] = prop.getProperty("speedmania.ranking." + ruleName + ".time." + i, 0);
		}
		for(int i = 0; i < SECTION_MAX; i++) {
			bestSectionTime[i] = prop.getProperty("speedmania.bestSectionTime." + ruleName + "." + i, DEFAULT_SECTION_TIME);
		}
	}

	/**
	 * Save rankings to property file
	 * @param prop Property file
	 * @param ruleName Rule name
	 */
	private void saveRanking(CustomProperties prop, String ruleName) {
		for(int i = 0; i < RANKING_MAX; i++) {
			prop.setProperty("speedmania.ranking." + ruleName + ".grade." + i, rankingGrade[i]);
			prop.setProperty("speedmania.ranking." + ruleName + ".level." + i, rankingLevel[i]);
			prop.setProperty("speedmania.ranking." + ruleName + ".time." + i, rankingTime[i]);
		}
		for(int i = 0; i < SECTION_MAX; i++) {
			prop.setProperty("speedmania.bestSectionTime." + ruleName + "." + i, bestSectionTime[i]);
		}
	}

	/**
	 * Update rankings
	 * @param gr 段位
	 * @param lv  level
	 * @param time Time
	 */
	private void updateRanking(int gr, int lv, int time) {
		rankingRank = checkRanking(gr, lv, time);

		if(rankingRank != -1) {
			// Shift down ranking entries
			for(int i = RANKING_MAX - 1; i > rankingRank; i--) {
				rankingGrade[i] = rankingGrade[i - 1];
				rankingLevel[i] = rankingLevel[i - 1];
				rankingTime[i] = rankingTime[i - 1];
			}

			// Add new data
			rankingGrade[rankingRank] = gr;
			rankingLevel[rankingRank] = lv;
			rankingTime[rankingRank] = time;
		}
	}

	/**
	 * Calculate ranking position
	 * @param gr 段位
	 * @param lv  level
	 * @param time Time
	 * @return Position (-1 if unranked)
	 */
	private int checkRanking(int gr, int lv, int time) {
		for(int i = 0; i < RANKING_MAX; i++) {
			if(lv > rankingLevel[i]) {
				return i;
			} else if((lv == rankingLevel[i]) && (time < rankingTime[i])) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * Update best section time records
	 */
	private void updateBestSectionTime() {
		for(int i = 0; i < SECTION_MAX; i++) {
			if(sectionIsNewRecord[i]) {
				bestSectionTime[i] = sectiontime[i];
			}
		}
	}
}
