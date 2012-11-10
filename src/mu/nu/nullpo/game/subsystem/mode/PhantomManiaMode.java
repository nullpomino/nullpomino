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
import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.util.CustomProperties;
import mu.nu.nullpo.util.GeneralUtil;

/**
 * PHANTOM MANIA mode (Original from NullpoUE build 121909 by Zircean)
 */
public class PhantomManiaMode extends AbstractMode {
	/** Current version */
	private static final int CURRENT_VERSION = 1;

	/** ARE table */
	private static final int[] tableARE       = {15, 11, 11,  5,  4,  3};

	/** ARE Line table */
	private static final int[] tableARELine   = {11,  5,  5,  4,  4,  3};

	/** Line Delay table */
	private static final int[] tableLineDelay = {12,  6,  6,  7,  5,  4};

	/** Lock Delay table */
	private static final int[] tableLockDelay = {31, 27, 23, 19, 16, 16};

	/** DAS table */
	private static final int[] tableDAS =       {11, 11, 10,  9,  7,  7};

	/** BGM fadeout level */
	private static final int[] tableBGMFadeout = {280, 480, -1};

	/** BGM change level */
	private static final int[] tableBGMChange  = {300, 500, -1};

	/** Grade names */
	private static final String[] tableGradeName = {"", "M", "MK", "MV", "MO", "MM", "GM"};

	/** Secret grade names */
	private static final String[] tableSecretGradeName =
	{
		"S1", "S2", "S3", "S4", "S5", "S6", "S7", "S8", "S9",	//  0?` 8
		"M1", "M2", "M3", "M4", "M5", "M6", "M7", "M8", "M9",	//  9?`17
		"GM"													// 18
	};

	/** Required level for grade */
	private static final int[] tableGradeLevel = {0, 300, 500, 600, 700, 800, 999};

	/** Ending time limit */
	private static final int ROLLTIMELIMIT = 1982;

	/** Number of hiscore records */
	private static final int RANKING_MAX = 10;

	/** Level 300 time limit */
	private static final int LV300TORIKAN = 8880;

	/** Level 500 time limit */
	private static final int LV500TORIKAN = 13080;

	/** Level 800 time limit */
	private static final int LV800TORIKAN = 19380;

	/** Number of sections */
	private static final int SECTION_MAX = 10;

	/** Default section time */
	private static final int DEFAULT_SECTION_TIME = 3600;

	/** GameManager object (Manages entire game status) */

	/** EventReceiver object (This receives many game events, can also be used for drawing the fonts.) */

	/** Next section level */
	private int nextseclv;

	/** Level up flag (Set to true when the level increases) */
	private boolean lvupflag;

	/** Current grade */
	private int grade;

	/** Remaining frames of flash effect of grade display */
	private int gradeflash;

	/** Used by combo scoring */
	private int comboValue;

	/** Amount of points you just get from line clears */
	private int lastscore;

	/** Elapsed time from last line clear (lastscore is displayed to screen until this reaches to 120) */
	private int scgettime;

	/** Secret Grade */
	private int secretGrade;

	/** Remaining ending time limit */
	private int rolltime;

	/** 0:Died before ending, 1:Died during ending, 2:Completed ending */
	private int rollclear;

	/** True if ending has started */
	private boolean rollstarted;

	/** Current BGM */
	private int bgmlv;

	/** Section Time */
	private int[] sectiontime;

	/** This will be true if the player achieves new section time record in specific section */
	private boolean[] sectionIsNewRecord;

	/** Amount of sections completed */
	private int sectionscomp;

	/** Average section time */
	private int sectionavgtime;

	/** Current section time */
	private int sectionlasttime;

	/** Number of 4-Line clears in current section */
	private int sectionfourline;

	/** Set to true by default, set to false when sectionfourline is below 2 */
	private boolean gmfourline;

	/** AC medal (0:None, 1:Bronze, 2:Silver, 3:Gold) */
	private int medalAC;

	/** ST medal */
	private int medalST;

	/** SK medal */
	private int medalSK;

	/** RE medal */
	private int medalRE;

	/** RO medal */
	private int medalRO;

	/** CO medal */
	private int medalCO;

	/** Used by RE medal */
	private boolean recoveryFlag;

	/** Total rotations */
	private int rotateCount;

	/** false:Leaderboard, true:Section time record (Push F in settings screen to flip it) */
	private boolean isShowBestSectionTime;

	/** Selected start level */
	private int startlevel;

	/** Enable/Disable level stop sfx */
	private boolean lvstopse;

	/** Big mode */
	private boolean big;

	/** Show section time */
	private boolean showsectiontime;

	/** Version of this mode */
	private int version;

	/** Your place on leaderboard (-1: out of rank) */
	private int rankingRank;

	/** Grade records */
	private int[] rankingGrade;

	/** Level records */
	private int[] rankingLevel;

	/** Time records */
	private int[] rankingTime;

	/** Roll-Cleared records */
	private int[] rankingRollclear;

	/** Best section time records */
	private int[] bestSectionTime;

	/**
	 * Returns the name of this mode
	 */
	@Override
	public String getName() {
		return "PHANTOM MANIA";
	}

	/**
	 * This function will be called when the game enters the main game screen.
	 */
	@Override
	public void playerInit(GameEngine engine, int playerID) {
		owner = engine.owner;
		receiver = engine.owner.receiver;

		nextseclv = 0;
		lvupflag = true;
		grade = 0;
		gradeflash = 0;
		comboValue = 0;
		lastscore = 0;
		scgettime = 0;
		rolltime = 0;
		rollclear = 0;
		rollstarted = false;
		bgmlv = 0;
		sectiontime = new int[SECTION_MAX];
		sectionIsNewRecord = new boolean[SECTION_MAX];
		sectionavgtime = 0;
		sectionlasttime = 0;
		sectionfourline = 0;
		gmfourline = true;
		medalAC = 0;
		medalST = 0;
		medalSK = 0;
		medalRE = 0;
		medalRO = 0;
		medalCO = 0;
		recoveryFlag = false;
		rotateCount = 0;
		isShowBestSectionTime = false;
		startlevel = 0;
		lvstopse = false;
		big = false;
		showsectiontime = true;

		rankingRank = -1;
		rankingGrade = new int[RANKING_MAX];
		rankingLevel = new int[RANKING_MAX];
		rankingTime = new int[RANKING_MAX];
		rankingRollclear = new int[RANKING_MAX];
		bestSectionTime = new int[SECTION_MAX];

		engine.tspinEnable = false;
		engine.b2bEnable = false;
		engine.comboType = GameEngine.COMBO_TYPE_DOUBLE;
		engine.framecolor = GameEngine.FRAME_COLOR_CYAN;
		engine.blockHidden = engine.ruleopt.lockflash;
		engine.bighalf = true;
		engine.bigmove = true;
		engine.staffrollEnable = true;
		engine.staffrollNoDeath = false;

		if(owner.replayMode == false) {
			loadSetting(owner.modeConfig);
			loadRanking(owner.modeConfig, engine.ruleopt.strRuleName);
			version = CURRENT_VERSION;
		} else {
			for(int i = 0; i < SECTION_MAX; i++) {
				bestSectionTime[i] = DEFAULT_SECTION_TIME;
			}
			loadSetting(owner.replayProp);
			version = owner.replayProp.getProperty("phantommania.version", 0);
		}

		owner.backgroundStatus.bg = startlevel;
	}

	/**
	 * Load the settings
	 */
	protected void loadSetting(CustomProperties prop) {
		startlevel = prop.getProperty("phantommania.startlevel", 0);
		lvstopse = prop.getProperty("phantommania.lvstopse", false);
		showsectiontime = prop.getProperty("phantommania.showsectiontime", true);
		big = prop.getProperty("phantommania.big", false);
	}

	/**
	 * Save the settings
	 */
	protected void saveSetting(CustomProperties prop) {
		prop.setProperty("phantommania.startlevel", startlevel);
		prop.setProperty("phantommania.lvstopse", lvstopse);
		prop.setProperty("phantommania.showsectiontime", showsectiontime);
		prop.setProperty("phantommania.big", big);
	}

	/**
	 * Set the starting bgmlv
	 */
	private void setStartBgmlv(GameEngine engine) {
		bgmlv = 0;
		while((tableBGMChange[bgmlv] != -1) && (engine.statistics.level >= tableBGMChange[bgmlv])) bgmlv++;
	}

	/**
	 * Set the gravity speed
	 * @param engine GameEngine object
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
	 * Calculates average section time
	 */
	private void setAverageSectionTime() {
		if(sectionscomp > 0) {
			int temp = 0;
			for(int i = startlevel; i < startlevel + sectionscomp; i++) temp += sectiontime[i];
			sectionavgtime = temp / sectionscomp;
		} else {
			sectionavgtime = 0;
		}
	}

	/**
	 * Checks ST medal
	 * @param engine GameEngine
	 * @param sectionNumber Section Number
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
	 * Checks RO medal
	 */
	private void roMedalCheck(GameEngine engine) {
		float rotateAverage = (float)rotateCount / (float)engine.statistics.totalPieceLocked;

		if((rotateAverage >= 1.2f) && (medalRO < 3)) {
			receiver.playSE("medal");
			medalRO++;
		}
	}

	/**
	 * Get medal font color
	 */
	private int getMedalFontColor(int medalColor) {
		if(medalColor == 1) return EventReceiver.COLOR_RED;
		if(medalColor == 2) return EventReceiver.COLOR_WHITE;
		if(medalColor == 3) return EventReceiver.COLOR_YELLOW;
		return -1;
	}

	/**
	 * Main routine for game setup screen
	 */
	@Override
	public boolean onSetting(GameEngine engine, int playerID) {
		if(engine.owner.replayMode == false) {
			// Configuration changes
			int change = updateCursor(engine, 3);

			if(change != 0) {
				receiver.playSE("change");

				switch(engine.statc[2]) {
				case 0:
					startlevel += change;
					if(startlevel < 0) startlevel = 9;
					if(startlevel > 9) startlevel = 0;
					owner.backgroundStatus.bg = startlevel;
					break;
				case 1:
					lvstopse = !lvstopse;
					break;
				case 2:
					showsectiontime = !showsectiontime;
					break;
				case 3:
					big = !big;
					break;
				}
			}

			// Check for F button, when pressed this will flip Leaderboard/Best Section Time Records
			if(engine.ctrl.isPush(Controller.BUTTON_F) && (engine.statc[3] >= 5)) {
				engine.playSE("change");
				isShowBestSectionTime = !isShowBestSectionTime;
			}

			// Check for A button, when pressed this will begin the game
			if(engine.ctrl.isPush(Controller.BUTTON_A) && (engine.statc[3] >= 5)) {
				receiver.playSE("decide");
				saveSetting(owner.modeConfig);
				receiver.saveModeConfig(owner.modeConfig);
				isShowBestSectionTime = false;
				return false;
			}

			// Check for B button, when pressed this will shutdown the game engine.
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

	/**
	 * Renders game setup screen
	 */
	@Override
	public void renderSetting(GameEngine engine, int playerID) {
		drawMenu(engine, playerID, receiver, 0, EventReceiver.COLOR_BLUE, 0,
				"LEVEL", String.valueOf(startlevel * 100),
				"LVSTOPSE", GeneralUtil.getONorOFF(lvstopse),
				"SHOW STIME", GeneralUtil.getONorOFF(showsectiontime),
				"BIG",  GeneralUtil.getONorOFF(big));
	}

	/**
	 * This function will be called before the game actually begins (after Ready&Go screen disappears)
	 */
	@Override
	public void startGame(GameEngine engine, int playerID) {
		engine.statistics.level = startlevel * 100;

		nextseclv = engine.statistics.level + 100;
		if(engine.statistics.level < 0) nextseclv = 100;
		if(engine.statistics.level >= 900) nextseclv = 999;

		owner.backgroundStatus.bg = engine.statistics.level / 100;

		engine.big = big;

		setSpeed(engine);
		setStartBgmlv(engine);
		owner.bgmStatus.bgm = bgmlv + 1;

		sectionscomp = 0;
	}

	/**
	 * Renders HUD (leaderboard or game statistics)
	 */
	@Override
	public void renderLast(GameEngine engine, int playerID) {
		receiver.drawScoreFont(engine, playerID, 0, 0, "PHANTOM MANIA", EventReceiver.COLOR_WHITE);

		if( (engine.stat == GameEngine.STAT_SETTING) || ((engine.stat == GameEngine.STAT_RESULT) && (owner.replayMode == false)) ) {
			if((owner.replayMode == false) && (startlevel == 0) && (big == false) && (engine.ai == null)) {
				if(!isShowBestSectionTime) {
					// Leaderboard
					float scale = (receiver.getNextDisplayType() == 2) ? 0.5f : 1.0f;
					int topY = (receiver.getNextDisplayType() == 2) ? 5 : 3;
					receiver.drawScoreFont(engine, playerID, 3, topY-1, "GRADE LEVEL TIME", EventReceiver.COLOR_BLUE, scale);

					for(int i = 0; i < RANKING_MAX; i++) {
						int gcolor = EventReceiver.COLOR_WHITE;
						if(rankingRollclear[i] == 1) gcolor = EventReceiver.COLOR_GREEN;
						if(rankingRollclear[i] == 2) gcolor = EventReceiver.COLOR_ORANGE;

						receiver.drawScoreFont(engine, playerID, 0, topY+i, String.format("%2d", i + 1), EventReceiver.COLOR_YELLOW, scale);
						if((rankingGrade[i] >= 0) && (rankingGrade[i] < tableGradeName.length))
							receiver.drawScoreFont(engine, playerID, 3, topY+i, tableGradeName[rankingGrade[i]], gcolor, scale);
						receiver.drawScoreFont(engine, playerID, 9, topY+i, String.valueOf(rankingLevel[i]), (i == rankingRank), scale);
						receiver.drawScoreFont(engine, playerID, 15, topY+i, GeneralUtil.getTime(rankingTime[i]), (i == rankingRank), scale);
					}

					receiver.drawScoreFont(engine, playerID, 0, 17, "F:VIEW SECTION TIME", EventReceiver.COLOR_GREEN);
				} else {
					// Best section time records
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
			receiver.drawScoreFont(engine, playerID, 0, 2, "GRADE", EventReceiver.COLOR_BLUE);
			if((grade >= 0) && (grade < tableGradeName.length))
				receiver.drawScoreFont(engine, playerID, 0, 3, tableGradeName[grade], ((gradeflash > 0) && (gradeflash % 4 == 0)));

			receiver.drawScoreFont(engine, playerID, 0, 5, "SCORE", EventReceiver.COLOR_BLUE);
			String strScore;
			if((lastscore == 0) || (scgettime <= 0)) {
				strScore = String.valueOf(engine.statistics.score);
			} else {
				strScore = String.valueOf(engine.statistics.score) + "\n(+" + String.valueOf(lastscore) + ")";
			}
			receiver.drawScoreFont(engine, playerID, 0, 6, strScore);

			receiver.drawScoreFont(engine, playerID, 0, 9, "LEVEL", EventReceiver.COLOR_BLUE);
			int tempLevel = engine.statistics.level;
			if(tempLevel < 0) tempLevel = 0;
			String strLevel = String.format("%3d", tempLevel);
			receiver.drawScoreFont(engine, playerID, 0, 10, strLevel);

			int speed = engine.speed.gravity / 128;
			if(engine.speed.gravity < 0) speed = 40;
			receiver.drawSpeedMeter(engine, playerID, 0, 11, speed);

			receiver.drawScoreFont(engine, playerID, 0, 12, String.format("%3d", nextseclv));

			receiver.drawScoreFont(engine, playerID, 0, 14, "TIME", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 0, 15, GeneralUtil.getTime(engine.statistics.time));

			if((engine.gameActive) && (engine.ending == 2)) {
				int time = ROLLTIMELIMIT - rolltime;
				if(time < 0) time = 0;
				receiver.drawScoreFont(engine, playerID, 0, 17, "ROLL TIME", EventReceiver.COLOR_BLUE);
				receiver.drawScoreFont(engine, playerID, 0, 18, GeneralUtil.getTime(time), ((time > 0) && (time < 10 * 60)));
			}

			if(medalAC >= 1) receiver.drawScoreFont(engine, playerID, 0, 20, "AC", getMedalFontColor(medalAC));
			if(medalST >= 1) receiver.drawScoreFont(engine, playerID, 3, 20, "ST", getMedalFontColor(medalST));
			if(medalSK >= 1) receiver.drawScoreFont(engine, playerID, 0, 21, "SK", getMedalFontColor(medalSK));
			if(medalRE >= 1) receiver.drawScoreFont(engine, playerID, 3, 21, "RE", getMedalFontColor(medalRE));
			if(medalRO >= 1) receiver.drawScoreFont(engine, playerID, 0, 22, "RO", getMedalFontColor(medalRO));
			if(medalCO >= 1) receiver.drawScoreFont(engine, playerID, 3, 22, "CO", getMedalFontColor(medalCO));

			if((showsectiontime == true) && (sectiontime != null)) {
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

	/**
	 * This function will be called when the piece is active
	 */
	@Override
	public boolean onMove(GameEngine engine, int playerID) {
		if((engine.ending == 0) && (engine.statc[0] == 0) && (engine.holdDisable == false) && (!lvupflag)) {
			if(engine.statistics.level < nextseclv - 1) {
				engine.statistics.level++;
				if((engine.statistics.level == nextseclv - 1) && (lvstopse == true)) owner.receiver.playSE("levelstop");
			}
			levelUp(engine);

			if((engine.timerActive == true) && (medalRE < 3)) {
				int blocks = engine.field.getHowManyBlocks();

				if(recoveryFlag == false) {
					if(blocks >= 150) {
						recoveryFlag = true;
					}
				} else {
					if(blocks <= 70) {
						recoveryFlag = false;
						receiver.playSE("medal");
						medalRE++;
					}
				}
			}
		}
		if((engine.ending == 0) && (engine.statc[0] > 0)) {
			lvupflag = false;
		}

		if((engine.ending == 2) && (rollstarted == false)) {
			rollstarted = true;
		}

		return false;
	}

	/**
	 * This function will be called during ARE
	 */
	@Override
	public boolean onARE(GameEngine engine, int playerID) {
		if((engine.ending == 0) && (engine.statc[0] >= engine.statc[1] - 1) && (!lvupflag)) {
			if(engine.statistics.level < nextseclv - 1) {
				engine.statistics.level++;
				if((engine.statistics.level == nextseclv - 1) && (lvstopse == true)) owner.receiver.playSE("levelstop");
			}
			levelUp(engine);
			lvupflag = true;
		}

		return false;
	}

	/**
	 * Levelup
	 */
	private void levelUp(GameEngine engine) {
		engine.meterValue = ((engine.statistics.level % 100) * receiver.getMeterMax(engine)) / 99;
		engine.meterColor = GameEngine.METER_COLOR_GREEN;
		if(engine.statistics.level % 100 >= 50) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
		if(engine.statistics.level % 100 >= 80) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
		if(engine.statistics.level >= nextseclv - 1) engine.meterColor = GameEngine.METER_COLOR_RED;

		setSpeed(engine);

		if((tableBGMFadeout[bgmlv] != -1) && (engine.statistics.level >= tableBGMFadeout[bgmlv]))
			owner.bgmStatus.fadesw  = true;
	}

	/**
	 * Calculates line-clear score
	 * (This function will be called even if no lines are cleared)
	 */
	@Override
	public void calcScore(GameEngine engine, int playerID, int lines) {
		if(lines == 0) {
			comboValue = 1;
		} else {
			comboValue = comboValue + (2 * lines) - 2;
			if(comboValue < 1) comboValue = 1;
		}

		int rotateTemp = engine.nowPieceRotateCount;
		if(rotateTemp > 4) rotateTemp = 4;
		rotateCount += rotateTemp;

		if((lines >= 1) && (engine.ending == 0)) {
			if(lines >= 4) {
				sectionfourline++;

				if(big == true) {
					if((engine.statistics.totalFour == 1) || (engine.statistics.totalFour == 2) || (engine.statistics.totalFour == 4)) {
						receiver.playSE("medal");
						medalSK++;
					}
				} else {
					if((engine.statistics.totalFour == 5) || (engine.statistics.totalFour == 10) || (engine.statistics.totalFour == 17)) {
						receiver.playSE("medal");
						medalSK++;
					}
				}
			}

			if(engine.field.isEmpty()) {
				receiver.playSE("bravo");

				if(medalAC < 3) {
					receiver.playSE("medal");
					medalAC++;
				}
			}

			if(big == true) {
				if((engine.combo >= 2) && (medalCO < 1)) {
					receiver.playSE("medal");
					medalCO = 1;
				} else if((engine.combo >= 3) && (medalCO < 2)) {
					receiver.playSE("medal");
					medalCO = 2;
				} else if((engine.combo >= 4) && (medalCO < 3)) {
					receiver.playSE("medal");
					medalCO = 3;
				}
			} else {
				if((engine.combo >= 4) && (medalCO < 1)) {
					receiver.playSE("medal");
					medalCO = 1;
				} else if((engine.combo >= 5) && (medalCO < 2)) {
					receiver.playSE("medal");
					medalCO = 2;
				} else if((engine.combo >= 7) && (medalCO < 3)) {
					receiver.playSE("medal");
					medalCO = 3;
				}
			}

			int levelb = engine.statistics.level;
			engine.statistics.level += lines;
			levelUp(engine);

			if(engine.statistics.level >= 999) {
				if (engine.timerActive)
				{
					sectionscomp++;
					setAverageSectionTime();
				}

				receiver.playSE("endingstart");
				engine.statistics.level = 999;
				engine.timerActive = false;
				engine.ending = 2;
				rollclear = 1;

				sectionlasttime = sectiontime[levelb / 100];

				stMedalCheck(engine, levelb / 100);

				roMedalCheck(engine);

				if ((engine.statistics.totalFour) >= 31 && (gmfourline) && (sectionfourline >= 1))
				{
					grade = 6; gradeflash = 180;
				}
			} else if((nextseclv == 300) && (engine.statistics.level >= 300) && (engine.statistics.time > LV300TORIKAN)) {
				if (engine.timerActive)
				{
					sectionscomp++;
					setAverageSectionTime();
				}

				receiver.playSE("endingstart");
				engine.statistics.level = 300;
				engine.timerActive = false;
				engine.ending = 2;

				if((tableBGMChange[bgmlv] != -1) && (engine.statistics.level >= tableBGMChange[bgmlv])) {
					bgmlv++;
					owner.bgmStatus.fadesw = false;
					owner.bgmStatus.bgm = bgmlv + 1;
				}

				sectionlasttime = sectiontime[levelb / 100];

				stMedalCheck(engine, levelb / 100);
			} else if((nextseclv == 500) && (engine.statistics.level >= 500) && (engine.statistics.time > LV500TORIKAN)) {
				if (engine.timerActive)
				{
					sectionscomp++;
					setAverageSectionTime();
				}

				receiver.playSE("endingstart");
				engine.statistics.level = 500;
				engine.timerActive = false;
				engine.ending = 2;

				if((tableBGMChange[bgmlv] != -1) && (engine.statistics.level >= tableBGMChange[bgmlv])) {
					bgmlv++;
					owner.bgmStatus.fadesw = false;
					owner.bgmStatus.bgm = bgmlv + 1;
				}

				sectionlasttime = sectiontime[levelb / 100];

				stMedalCheck(engine, levelb / 100);
			} else if((nextseclv == 800) && (engine.statistics.level >= 800) && (engine.statistics.time > LV800TORIKAN)) {
				if (engine.timerActive)
				{
					sectionscomp++;
					setAverageSectionTime();
				}

				receiver.playSE("endingstart");
				engine.statistics.level = 800;
				engine.timerActive = false;
				engine.ending = 2;

				if((tableBGMChange[bgmlv] != -1) && (engine.statistics.level >= tableBGMChange[bgmlv])) {
					bgmlv++;
					owner.bgmStatus.fadesw = false;
					owner.bgmStatus.bgm = bgmlv + 1;
				}

				sectionlasttime = sectiontime[levelb / 100];

				stMedalCheck(engine, levelb / 100);
			} else if(engine.statistics.level >= nextseclv) {
				receiver.playSE("levelup");

				owner.backgroundStatus.fadesw = true;
				owner.backgroundStatus.fadecount = 0;
				owner.backgroundStatus.fadebg = nextseclv / 100;

				if((tableBGMChange[bgmlv] != -1) && (engine.statistics.level >= tableBGMChange[bgmlv])) {
					bgmlv++;
					owner.bgmStatus.fadesw = false;
					owner.bgmStatus.bgm = bgmlv + 1;
				}

				sectionscomp++;

				sectionlasttime = sectiontime[levelb / 100];

				if (sectionfourline < 2) {gmfourline = false;}

				sectionfourline = 0;

				stMedalCheck(engine, levelb / 100);

				if((nextseclv == 300) || (nextseclv == 700)) roMedalCheck(engine);

				if (startlevel == 0)
				{
					for (int i = 0; i < tableGradeLevel.length - 1; i++)
					{
						if(engine.statistics.level >= tableGradeLevel[i]) {
							grade = i;
							gradeflash = 180;
						}
					}
				}

				nextseclv += 100;
				if(nextseclv > 999) nextseclv = 999;
			} else if((engine.statistics.level == nextseclv - 1) && (lvstopse == true)) {
				receiver.playSE("levelstop");
			}

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

	/**
	 * This function will be called when the game timer updates
	 */
	@Override
	public void onLast(GameEngine engine, int playerID) {
		if(gradeflash > 0) gradeflash--;

		if(scgettime > 0) scgettime--;

		if((engine.timerActive) && (engine.ending == 0)) {
			int section = engine.statistics.level / 100;

			if((section >= 0) && (section < sectiontime.length)) {
				sectiontime[section]++;
				setAverageSectionTime();
			}
		}

		if((engine.gameActive) && (engine.ending == 2)) {
			if((version >= 1) && (engine.ctrl.isPress(Controller.BUTTON_F)) && (engine.statistics.level < 999))
				rolltime += 5;
			else
				rolltime += 1;

			int remainRollTime = ROLLTIMELIMIT - rolltime;
			engine.meterValue = (remainRollTime * receiver.getMeterMax(engine)) / ROLLTIMELIMIT;
			engine.meterColor = GameEngine.METER_COLOR_GREEN;
			if(remainRollTime <= 30*60) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
			if(remainRollTime <= 20*60) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
			if(remainRollTime <= 10*60) engine.meterColor = GameEngine.METER_COLOR_RED;

			if(rolltime >= ROLLTIMELIMIT) {
				if (engine.statistics.level >= 999) rollclear = 2;

				engine.gameEnded();
				engine.resetStatc();
				engine.stat = GameEngine.STAT_EXCELLENT;
			}
		}
	}

	/**
	 * This function will be called when the player tops out
	 */
	@Override
	public boolean onGameOver(GameEngine engine, int playerID) {
		if(engine.statc[0] == 0) {
			secretGrade = engine.field.getSecretGrade();
		}
		return false;
	}

	/**
	 * Renders game result screen
	 */
	@Override
	public void renderResult(GameEngine engine, int playerID) {
		receiver.drawMenuFont(engine, playerID, 0, 0, "kn PAGE" + (engine.statc[1] + 1) + "/3", EventReceiver.COLOR_RED);

		if(engine.statc[1] == 0) {
			int gcolor = EventReceiver.COLOR_WHITE;
			if(rollclear == 1) gcolor = EventReceiver.COLOR_GREEN;
			if(rollclear == 2) gcolor = EventReceiver.COLOR_ORANGE;
			receiver.drawMenuFont(engine, playerID, 0, 2, "GRADE", EventReceiver.COLOR_BLUE);
			String strGrade = String.format("%10s", tableGradeName[grade]);
			receiver.drawMenuFont(engine, playerID, 0, 3, strGrade, gcolor);

			drawResultStats(engine, playerID, receiver, 4, EventReceiver.COLOR_BLUE,
					STAT_SCORE, STAT_LINES, STAT_LEVEL_MANIA, STAT_TIME);
			drawResultRank(engine, playerID, receiver, 12, EventReceiver.COLOR_BLUE, rankingRank);
			if(secretGrade > 4) {
				drawResult(engine, playerID, receiver, 15, EventReceiver.COLOR_BLUE,
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

	/**
	 * Additional routine for game result screen
	 */
	@Override
	public boolean onResult(GameEngine engine, int playerID) {
		// Page change
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
		// Flip Leaderboard/Best Section Time Records
		if(engine.ctrl.isPush(Controller.BUTTON_F)) {
			engine.playSE("change");
			isShowBestSectionTime = !isShowBestSectionTime;
		}

		return false;
	}

	/**
	 * This function will be called when the replay data is going to be saved
	 */
	@Override
	public void saveReplay(GameEngine engine, int playerID, CustomProperties prop) {
		saveSetting(owner.replayProp);
		owner.replayProp.setProperty("phantommania.version", version);

		if((owner.replayMode == false) && (startlevel == 0) && (big == false) && (engine.ai == null)) {
			updateRanking(grade, engine.statistics.level, engine.statistics.time, rollclear);
			if(medalST == 3) updateBestSectionTime();

			if((rankingRank != -1) || (medalST == 3)) {
				saveRanking(owner.modeConfig, engine.ruleopt.strRuleName);
				receiver.saveModeConfig(owner.modeConfig);
			}
		}
	}

	/**
	 * Load the ranking
	 */
	private void loadRanking(CustomProperties prop, String ruleName) {
		for(int i = 0; i < RANKING_MAX; i++) {
			rankingGrade[i] = prop.getProperty("phantommania.ranking." + ruleName + ".grade." + i, 0);
			rankingLevel[i] = prop.getProperty("phantommania.ranking." + ruleName + ".level." + i, 0);
			rankingTime[i] = prop.getProperty("phantommania.ranking." + ruleName + ".time." + i, 0);
			rankingRollclear[i] = prop.getProperty("phantommania.ranking." + ruleName + ".rollclear." + i, 0);
		}
		for(int i = 0; i < SECTION_MAX; i++) {
			bestSectionTime[i] = prop.getProperty("phantommania.bestSectionTime." + ruleName + "." + i, DEFAULT_SECTION_TIME);
		}
	}

	/**
	 * Save the ranking
	 */
	private void saveRanking(CustomProperties prop, String ruleName) {
		for(int i = 0; i < RANKING_MAX; i++) {
			prop.setProperty("phantommania.ranking." + ruleName + ".grade." + i, rankingGrade[i]);
			prop.setProperty("phantommania.ranking." + ruleName + ".level." + i, rankingLevel[i]);
			prop.setProperty("phantommania.ranking." + ruleName + ".time." + i, rankingTime[i]);
			prop.setProperty("phantommania.ranking." + ruleName + ".rollclear." + i, rankingRollclear[i]);
		}
		for(int i = 0; i < SECTION_MAX; i++) {
			prop.setProperty("phantommania.bestSectionTime." + ruleName + "." + i, bestSectionTime[i]);
		}
	}

	/**
	 * Update the ranking
	 */
	private void updateRanking(int gr, int lv, int time, int clear) {
		rankingRank = checkRanking(gr, lv, time, clear);

		if(rankingRank != -1) {
			for(int i = RANKING_MAX - 1; i > rankingRank; i--) {
				rankingGrade[i] = rankingGrade[i - 1];
				rankingLevel[i] = rankingLevel[i - 1];
				rankingTime[i] = rankingTime[i - 1];
				rankingRollclear[i] = rankingRollclear[i - 1];
			}

			rankingGrade[rankingRank] = gr;
			rankingLevel[rankingRank] = lv;
			rankingTime[rankingRank] = time;
			rankingRollclear[rankingRank] = clear;
		}
	}

	/**
	 * This function will check the ranking and returns which place you are. (-1: Out of rank)
	 */
	private int checkRanking(int gr, int lv, int time, int clear) {
		for(int i = 0; i < RANKING_MAX; i++) {
			if(clear > rankingRollclear[i]) {
				return i;
			} else if((clear == rankingRollclear[i]) && (gr > rankingGrade[i])) {
				return i;
			} else if((clear == rankingRollclear[i]) && (gr == rankingGrade[i]) && (lv > rankingLevel[i])) {
				return i;
			} else if((clear == rankingRollclear[i]) && (gr == rankingGrade[i]) && (lv == rankingLevel[i]) && (time < rankingTime[i])) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * Updates best section time records
	 */
	private void updateBestSectionTime() {
		for(int i = 0; i < SECTION_MAX; i++) {
			if(sectionIsNewRecord[i]) {
				bestSectionTime[i] = sectiontime[i];
			}
		}
	}
}
