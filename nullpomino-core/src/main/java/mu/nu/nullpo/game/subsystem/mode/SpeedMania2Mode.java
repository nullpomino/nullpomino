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
import mu.nu.nullpo.util.CustomProperties;
import mu.nu.nullpo.util.GeneralUtil;

/**
 * SPEED MANIA 2 Mode
 */
public class SpeedMania2Mode extends AbstractMode {
	/** Current version */
	private static final int CURRENT_VERSION = 2;

	/** Default torikan time for non-classic rules */
	private static final int DEFAULT_TORIKAN = 10980;

	/** Default torikan time for classic rules */
	private static final int DEFAULT_TORIKAN_CLASSIC = 8880;

	/** ARE table */
	private static final int[] tableARE       = { 8,  8,  8,  2,  2,  2,  2,  2,  2,  2,  2,  2,  2,  2};

	/** ARE after line clear table */
	private static final int[] tableARELine   = { 4,  3,  2,  2,  1,  1,  1,  1,  1,  1,  1,  1,  1,  2};

	/** Line clear time table */
	private static final int[] tableLineDelay = { 6,  5,  4,  4,  3,  3,  3,  3,  3,  3,  3,  3,  3,  6};

	/** Fixation time table */
	private static final int[] tableLockDelay = {19, 19, 18, 16, 16, 14, 13, 13, 13, 13, 13, 11,  9, 16};

	/** DAS table */
	private static final int[] tableDAS       = { 9,  7,  7,  7,  5,  5,  5,  5,  5,  5,  5,  5,  5,  5};

	/** Rising auction interval */
	private static final int[] tableGarbage   = { 0,  0,  0,  0,  0, 20, 18, 10,  9,  8,  0,  0,  0,  0};

	/** REGRET criteria Time */
	private static final int[] tableTimeRegret = {3600,3600,3000,3000,3000,3000,3000,3000,3000,3000,3000,3000,3000,3000};

	/** BGM fadeout levels */
	private static final int[] tableBGMFadeout = {485, 685, 985, -1};

	/** BGM change levels */
	private static final int[] tableBGMChange  = {500, 700, 1000, -1};

	/** Of danName */
	private static final String[] tableGradeName = {"1", "S1", "S2", "S3", "S4", "S5", "S6", "S7", "S8", "S9", "S10", "S11", "S12", "S13"};

	/** Dan&#39;s backName */
	private static final String[] tableSecretGradeName =
	{
		"S1", "S2", "S3", "S4", "S5", "S6", "S7", "S8", "S9",	//  0~ 8
		"M1", "M2", "M3", "M4", "M5", "M6", "M7", "M8", "M9",	//  9~17
		"GM"													// 18
	};

	/** LV999 roll time */
	private static final int ROLLTIMELIMIT = 3238;

	/** Number of entries in rankings */
	private static final int RANKING_MAX = 10;

	/** Number of sections */
	private static final int SECTION_MAX = 13;

	/** Default section time */
	private static final int DEFAULT_SECTION_TIME = 2520;

	/** Next Section Of level (This-1At levelStop) */
	private int nextseclv;

	/** LevelHas increased flag */
	private boolean lvupflag;

	/** The actual gear position display, such as the final result */
	private int grade;

	/** Illuminate the display remaining dan frame count */
	private int gradeflash;

	/** Combo bonus */
	private int comboValue;

	/** Most recent increase in score */
	private int lastscore;

	/** AcquisitionRender scoreIs remaining to be time */
	private int scgettime;

	/** Roll Course time */
	private int rolltime;

	/** Roll started flag */
	private boolean rollstarted;

	/** Roll completely cleared flag */
	private int rollclear;

	/** Rising up to auctionBlockcount */
	private int garbageCount;

	/** REGRET display time frame count */
	private int regretdispframe;

	/** Dan back */
	private int secretGrade;

	/** Current BGM */
	private int bgmlv;

	/** Section Time */
	private int[] sectiontime;

	/** New record came outSection Thetrue */
	private boolean[] sectionIsNewRecord;

	/** Cleared Section count */
	private int sectionscomp;

	/** Average Section Time */
	private int sectionavgtime;

	/** PreviousSection Time */
	private int sectionlasttime;

	/** AC medal State */
	private int medalAC;

	/** ST medal State */
	private int medalST;

	/** SK medal State */
	private int medalSK;

	/** CO medal State */
	private int medalCO;

	/** Section TimeShowing record iftrue */
	private boolean isShowBestSectionTime;

	/** Level at start */
	private int startlevel;

	/** When true, levelstop sound is enabled */
	private boolean lvstopse;

	/** BigMode */
	private boolean big;

	/** LV500Cut legsTime */
	private int torikan;

	/** When true, section time display is enabled */
	private boolean showsectiontime;

	/** Dan view */
	private boolean gradedisp;

	/** Version */
	private int version;

	/** Current round's ranking rank */
	private int rankingRank;

	/** Rankings' Dan */
	private int[] rankingGrade;

	/** Rankings'  level */
	private int[] rankingLevel;

	/** Rankings' times */
	private int[] rankingTime;

	/** Rankings' Roll completely cleared flag */
	private int[] rankingRollclear;

	/** Section TimeRecord */
	private int[] bestSectionTime;

	/*
	 * Mode name
	 */
	@Override
	public String getName() {
		return "SPEED MANIA 2";
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
		grade = 0;
		gradeflash = 0;
		comboValue = 0;
		lastscore = 0;
		scgettime = 0;
		rolltime = 0;
		rollstarted = false;
		rollclear = 0;
		garbageCount = 0;
		regretdispframe = 0;
		secretGrade = 0;
		bgmlv = 0;
		sectiontime = new int[SECTION_MAX];
		sectionIsNewRecord = new boolean[SECTION_MAX];
		sectionscomp = 0;
		sectionavgtime = 0;
		sectionlasttime = 0;
		medalAC = 0;
		medalST = 0;
		medalSK = 0;
		medalCO = 0;
		isShowBestSectionTime = false;
		startlevel = 0;
		lvstopse = false;
		big = false;
		torikan = DEFAULT_TORIKAN;
		showsectiontime = false;
		gradedisp = false;

		rankingRank = -1;
		rankingGrade = new int[RANKING_MAX];
		rankingLevel = new int[RANKING_MAX];
		rankingTime = new int[RANKING_MAX];
		rankingRollclear = new int[RANKING_MAX];
		bestSectionTime = new int[SECTION_MAX];

		engine.tspinEnable = false;
		engine.b2bEnable = false;
		engine.comboType = GameEngine.COMBO_TYPE_DOUBLE;
		engine.framecolor = GameEngine.FRAME_COLOR_RED;
		engine.bighalf = true;
		engine.bigmove = true;
		engine.staffrollEnable = true;
		engine.staffrollNoDeath = false;

		if(owner.replayMode == false) {
			version = CURRENT_VERSION;
			loadSetting(owner.modeConfig, engine.ruleopt.strRuleName);
			loadRanking(owner.modeConfig, engine.ruleopt.strRuleName);
		} else {
			version = owner.replayProp.getProperty("speedmania2.version", 0);
			for(int i = 0; i < SECTION_MAX; i++) {
				bestSectionTime[i] = DEFAULT_SECTION_TIME;
			}
			loadSetting(owner.replayProp, engine.ruleopt.strRuleName);
		}

		owner.backgroundStatus.bg = Math.min(startlevel, 12);
	}

	/**
	 * Load settings from property file
	 * @param prop Property file
	 * @param strRuleName Rule name
	 */
	protected void loadSetting(CustomProperties prop, String strRuleName) {
		startlevel = prop.getProperty("speedmania2.startlevel", 0);
		lvstopse = prop.getProperty("speedmania2.lvstopse", true);
		showsectiontime = prop.getProperty("speedmania2.showsectiontime", false);
		big = prop.getProperty("speedmania2.big", false);
		if(version >= 2) {
			int defaultTorikan = DEFAULT_TORIKAN;
			if(strRuleName.contains("CLASSIC")) defaultTorikan = DEFAULT_TORIKAN_CLASSIC;
			torikan = prop.getProperty("speedmania2.torikan." + strRuleName, defaultTorikan);
		} else {
			torikan = prop.getProperty("speedmania2.torikan", DEFAULT_TORIKAN);
		}
		gradedisp = prop.getProperty("speedmania2.gradedisp", false);
	}

	/**
	 * Save settings to property file
	 * @param prop Property file
	 * @param strRuleName Rule name
	 */
	protected void saveSetting(CustomProperties prop, String strRuleName) {
		prop.setProperty("speedmania2.startlevel", startlevel);
		prop.setProperty("speedmania2.lvstopse", lvstopse);
		prop.setProperty("speedmania2.showsectiontime", showsectiontime);
		prop.setProperty("speedmania2.big", big);
		if(version >= 2) {
			prop.setProperty("speedmania2.torikan." + strRuleName, torikan);
		} else {
			prop.setProperty("speedmania2.torikan", torikan);
		}
		prop.setProperty("speedmania2.gradedisp", gradedisp);
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
		if(sectionscomp > 0 && startlevel < 13) {
			int temp = 0;
			for(int i = startlevel; i < startlevel + sectionscomp; i++) {
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
	 *  medal Gets the color of the character
	 * @param medalColor  medal State
	 * @return  medal Text color of the
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
			int change = updateCursor(engine, 5);

			if(change != 0) {
				engine.playSE("change");

				switch(menuCursor) {
				case 0:
					startlevel += change;
					if(startlevel < 0) startlevel = 13;
					if(startlevel > 13) startlevel = 0;
					owner.backgroundStatus.bg = Math.min(startlevel, 12);
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
				case 4:
					torikan += 60 * change;
					if(torikan < 0) torikan = 72000;
					if(torikan > 72000) torikan = 0;
					break;
				case 5:
					gradedisp = !gradedisp;
					break;
				}
			}

			//  section time display切替
			if(engine.ctrl.isPush(Controller.BUTTON_F) && (menuTime >= 5)) {
				engine.playSE("change");
				isShowBestSectionTime = !isShowBestSectionTime;
			}

			// 決定
			if(engine.ctrl.isPush(Controller.BUTTON_A) && (menuTime >= 5)) {
				engine.playSE("decide");
				saveSetting(owner.modeConfig, engine.ruleopt.strRuleName);
				receiver.saveModeConfig(owner.modeConfig);

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
	 * Render the settings screen
	 */
	@Override
	public void renderSetting(GameEngine engine, int playerID) {
		drawMenu(engine, playerID, receiver, 0, EventReceiver.COLOR_BLUE, 0,
				"LEVEL", String.valueOf(startlevel * 100),
				"LVSTOPSE", GeneralUtil.getONorOFF(lvstopse),
				"SHOW STIME", GeneralUtil.getONorOFF(showsectiontime),
				"BIG",  GeneralUtil.getONorOFF(big),
				"LV500LIMIT", (torikan == 0) ? "NONE" : GeneralUtil.getTime(torikan),
				"GRADE DISP", GeneralUtil.getONorOFF(gradedisp));
	}

	/*
	 * Called at game start
	 */
	@Override
	public void startGame(GameEngine engine, int playerID) {
		engine.statistics.level = startlevel * 100;

		nextseclv = engine.statistics.level + 100;
		if(engine.statistics.level < 0) nextseclv = 100;
		if(engine.statistics.level >= 1300) nextseclv = 1300;
		if(engine.statistics.level >= 1000) engine.bone = true;

		owner.backgroundStatus.bg = Math.min(startlevel, 12);

		engine.big = big;

		setSpeed(engine);
		setStartBgmlv(engine);
		owner.bgmStatus.bgm = bgmlv + 2;
		
		if (startlevel >= 13)
		{
			// Ending
			engine.statistics.level = 1300;
			engine.timerActive = false;
			engine.ending = 2;
			rollclear = 1;
			rollstarted = true;
			engine.big = true;
			owner.bgmStatus.bgm = BGMStatus.BGM_ENDING1;
		}
	}

	/*
	 * Render score
	 */
	@Override
	public void renderLast(GameEngine engine, int playerID) {
		receiver.drawScoreFont(engine, playerID, 0, 0, "SPEED MANIA 2", EventReceiver.COLOR_RED);

		if( (engine.stat == GameEngine.Status.SETTING) || ((engine.stat == GameEngine.Status.RESULT) && (owner.replayMode == false)) ) {
			if((owner.replayMode == false) && (startlevel == 0) && (big == false) && (engine.ai == null)) {
				if(!isShowBestSectionTime) {
					// Rankings
					float scale = (receiver.getNextDisplayType() == 2) ? 0.5f : 1.0f;
					int topY = (receiver.getNextDisplayType() == 2) ? 5 : 3;
					receiver.drawScoreFont(engine, playerID, 3, topY-1, "GRADE LEVEL TIME", EventReceiver.COLOR_BLUE, scale);

					for(int i = 0; i < RANKING_MAX; i++) {
						int gcolor = EventReceiver.COLOR_WHITE;
						if(rankingRollclear[i] == 1) gcolor = EventReceiver.COLOR_GREEN;
						if(rankingRollclear[i] == 2) gcolor = EventReceiver.COLOR_ORANGE;

						receiver.drawScoreFont(engine, playerID, 0, topY+i, String.format("%2d", i + 1), EventReceiver.COLOR_YELLOW, scale);
						receiver.drawScoreFont(engine, playerID, 3, topY+i, tableGradeName[rankingGrade[i]], gcolor, scale);
						receiver.drawScoreFont(engine, playerID, 9, topY+i, String.valueOf(rankingLevel[i]), (i == rankingRank), scale);
						receiver.drawScoreFont(engine, playerID, 15, topY+i, GeneralUtil.getTime(rankingTime[i]), (i == rankingRank), scale);
					}

					receiver.drawScoreFont(engine, playerID, 0, 20, "F:VIEW SECTION TIME", EventReceiver.COLOR_GREEN);
				} else {
					// Section Time
					receiver.drawScoreFont(engine, playerID, 0, 2, "SECTION TIME", EventReceiver.COLOR_BLUE);

					int totalTime = 0;
					for(int i = 0; i < SECTION_MAX; i++) {
						int temp = i * 100;
						int temp2 = ((i + 1) * 100) - 1;

						String strSectionTime;
						strSectionTime = String.format("%4d-%4d %s", temp, temp2, GeneralUtil.getTime(bestSectionTime[i]));

						receiver.drawScoreFont(engine, playerID, 0, 3 + i, strSectionTime, sectionIsNewRecord[i]);

						totalTime += bestSectionTime[i];
					}

					receiver.drawScoreFont(engine, playerID, 0, 17, "TOTAL", EventReceiver.COLOR_BLUE);
					receiver.drawScoreFont(engine, playerID, 0, 18, GeneralUtil.getTime(totalTime));
					receiver.drawScoreFont(engine, playerID, 9, 17, "AVERAGE", EventReceiver.COLOR_BLUE);
					receiver.drawScoreFont(engine, playerID, 9, 18, GeneralUtil.getTime(totalTime / SECTION_MAX));

					receiver.drawScoreFont(engine, playerID, 0, 20, "F:VIEW RANKING", EventReceiver.COLOR_GREEN);
				}
			}
		} else {
			if(gradedisp) {
				// 段位
				receiver.drawScoreFont(engine, playerID, 0, 2, "GRADE", EventReceiver.COLOR_BLUE);
				if((grade >= 0) && (grade < tableGradeName.length))
					receiver.drawScoreFont(engine, playerID, 0, 3, tableGradeName[grade], ((gradeflash > 0) && (gradeflash % 4 == 0)));

				// Score
				receiver.drawScoreFont(engine, playerID, 0, 5, "SCORE", EventReceiver.COLOR_BLUE);
				String strScore;
				if((lastscore == 0) || (scgettime <= 0)) {
					strScore = String.valueOf(engine.statistics.score);
				} else {
					strScore = String.valueOf(engine.statistics.score) + "\n(+" + String.valueOf(lastscore) + ")";
				}
				receiver.drawScoreFont(engine, playerID, 0, 6, strScore);
			}

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

			// Roll Rest time
			if((engine.gameActive) && (engine.ending == 2)) {
				int time = ROLLTIMELIMIT - rolltime;
				if(time < 0) time = 0;
				receiver.drawScoreFont(engine, playerID, 0, 17, "ROLL TIME", EventReceiver.COLOR_BLUE);
				receiver.drawScoreFont(engine, playerID, 0, 18, GeneralUtil.getTime(time), ((time > 0) && (time < 10 * 60)));
			}

			// REGRETDisplay
			if(regretdispframe > 0) {
				receiver.drawMenuFont(engine,playerID,2,21,"REGRET",(regretdispframe % 4 == 0),EventReceiver.COLOR_WHITE,EventReceiver.COLOR_ORANGE);
			}

			//  medal
			if(medalAC >= 1) receiver.drawScoreFont(engine, playerID, 0, 20, "AC", getMedalFontColor(medalAC));
			if(medalST >= 1) receiver.drawScoreFont(engine, playerID, 3, 20, "ST", getMedalFontColor(medalST));
			if(medalSK >= 1) receiver.drawScoreFont(engine, playerID, 0, 21, "SK", getMedalFontColor(medalSK));
			if(medalCO >= 1) receiver.drawScoreFont(engine, playerID, 3, 21, "CO", getMedalFontColor(medalCO));

			// Section Time
			if((showsectiontime == true) && (sectiontime != null)) {
				int y = (receiver.getNextDisplayType() == 2) ? 4 : 2;
				int x = (receiver.getNextDisplayType() == 2) ? 20 : 12;
				int x2 = (receiver.getNextDisplayType() == 2) ? 9 : 12;
				float scale = (receiver.getNextDisplayType() == 2) ? 0.5f : 1.0f;

				receiver.drawScoreFont(engine, playerID, x, y, "SECTION TIME", EventReceiver.COLOR_BLUE, scale);

				for(int i = 0; i < sectiontime.length; i++) {
					if(sectiontime[i] > 0) {
						int temp = i * 100;

						int section = engine.statistics.level / 100;
						String strSeparator = " ";
						if((i == section) && (engine.ending == 0)) strSeparator = "b";

						String strSectionTime;
						strSectionTime = String.format("%4d%s%s", temp, strSeparator, GeneralUtil.getTime(sectiontime[i]));

						receiver.drawScoreFont(engine, playerID, x-1, y + 1 + i, strSectionTime, sectionIsNewRecord[i], scale);
					}
				}

				if(sectionavgtime > 0) {
					receiver.drawScoreFont(engine, playerID, x2, 17, "AVERAGE", EventReceiver.COLOR_BLUE);
					receiver.drawScoreFont(engine, playerID, x2, 18, GeneralUtil.getTime(sectionavgtime));
				}
			}
		}
	}

	/*
	 * Processing on the move
	 */
	@Override
	public boolean onMove(GameEngine engine, int playerID) {
		// Occurrence new piece
		if((engine.ending == 0) && (engine.statc[0] == 0) && (engine.holdDisable == false) && (!lvupflag)) {
			// Level up
			if(engine.statistics.level < nextseclv - 1) {
				engine.statistics.level++;
				if((engine.statistics.level == nextseclv - 1) && (lvstopse == true)) engine.playSE("levelstop");
			}
			levelUp(engine);
		}
		if( (engine.ending == 0) && (engine.statc[0] > 0) && ((version >= 1) || (engine.holdDisable == false)) ) {
			lvupflag = false;
		}

		if((engine.ending == 0) && (engine.statc[0] == 0) && (engine.holdDisable == false)) {
			// Count rising auction
			if(tableGarbage[engine.statistics.level / 100] != 0) garbageCount++;

			// Rising auction
			if((garbageCount >= tableGarbage[engine.statistics.level / 100]) && (tableGarbage[engine.statistics.level / 100] != 0)) {
				engine.playSE("garbage");
				engine.field.addBottomCopyGarbage(Block.BLOCK_COLOR_GRAY,
												  engine.getSkin(),
												  Block.BLOCK_ATTRIBUTE_GARBAGE | Block.BLOCK_ATTRIBUTE_VISIBLE | Block.BLOCK_ATTRIBUTE_OUTLINE,
												  1);
				garbageCount = 0;
			}
		}

		// EndingStart
		if((engine.ending == 2) && (rollstarted == false)) {
			rollstarted = true;
			engine.big = true;
			owner.bgmStatus.bgm = BGMStatus.BGM_ENDING1;
		}

		return false;
	}

	/*
	 * AREProcessing during
	 */
	@Override
	public boolean onARE(GameEngine engine, int playerID) {
		// Last frame
		if((engine.ending == 0) && (engine.statc[0] >= engine.statc[1] - 1) && (!lvupflag)) {
			if(engine.statistics.level < nextseclv - 1) {
				engine.statistics.level++;
				if((engine.statistics.level == nextseclv - 1) && (lvstopse == true)) engine.playSE("levelstop");
			}
			levelUp(engine);
			lvupflag = true;
		}

		return false;
	}

	/**
	 *  levelcommon process is raised when
	 */
	private void levelUp(GameEngine engine) {
		// Meter
		engine.meterValue = ((engine.statistics.level % 100) * receiver.getMeterMax(engine)) / 99;
		engine.meterColor = GameEngine.METER_COLOR_GREEN;
		if(engine.statistics.level % 100 >= 50) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
		if(engine.statistics.level % 100 >= 80) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
		if(engine.statistics.level == nextseclv - 1) engine.meterColor = GameEngine.METER_COLOR_RED;

		// Speed ​​change
		setSpeed(engine);

		// BGM fadeout
		if((tableBGMFadeout[bgmlv] != -1) && (engine.statistics.level >= tableBGMFadeout[bgmlv]))
			owner.bgmStatus.fadesw  = true;
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

		if((lines >= 1) && (engine.ending == 0)) {
			// 4-line clearCount
			if(lines >= 4) {
				// SK medal
				if(big == true) {
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
			if(big == true) {
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

			// Count decrease rising auction
			if(tableGarbage[engine.statistics.level / 100] != 0) garbageCount -= lines;
			if(garbageCount < 0) garbageCount = 0;

			// Level up
			int levelb = engine.statistics.level;
			int levelplus = lines;
			if(lines == 3) levelplus = 4;
			if(lines >= 4) levelplus = 6;

			engine.statistics.level += levelplus;

			levelUp(engine);

			if(engine.statistics.level >= 1300) {
				// Ending
				engine.playSE("endingstart");
				engine.statistics.level = 1300;
				engine.timerActive = false;
				engine.ending = 1;
				rollclear = 1;

				// Section TimeRecord
				sectionlasttime = sectiontime[levelb / 100];
				sectionscomp++;
				setAverageSectionTime();

				// ST medal
				stMedalCheck(engine, levelb / 100);

				if(sectionlasttime > tableTimeRegret[levelb / 100]) {
					// REGRETJudgment
					regretdispframe = 180;
					engine.playSE("regret");
				} else {
					// Dan rise
					grade++;
					if(grade > 13) grade = 13;
					gradeflash = 180;
				}
			} else if( ((nextseclv ==  500) && (engine.statistics.level >=  500) && (torikan > 0) && (engine.statistics.time > torikan)) ||
					   ((nextseclv == 1000) && (engine.statistics.level >= 1000) && (torikan > 0) && (engine.statistics.time > torikan * 2)) )
			{
				//  level500/1000Kang birds
				engine.playSE("endingstart");

				if(nextseclv == 500) engine.statistics.level = 500;
				if(nextseclv == 1000) engine.statistics.level = 1000;

				engine.gameEnded();
				engine.staffrollEnable = false;
				engine.ending = 1;

				secretGrade = engine.field.getSecretGrade();

				// Section TimeRecord
				sectionlasttime = sectiontime[levelb / 100];
				sectionscomp++;
				setAverageSectionTime();

				// ST medal
				stMedalCheck(engine, levelb / 100);

				if(sectionlasttime > tableTimeRegret[levelb / 100]) {
					// REGRETJudgment
					regretdispframe = 180;
					engine.playSE("regret");
				} else {
					// Dan rise
					grade++;
					if(grade > 13) grade = 13;
					gradeflash = 180;
				}
			} else if(engine.statistics.level >= nextseclv) {
				// Next Section
				engine.playSE("levelup");

				// BackgroundSwitching
				owner.backgroundStatus.fadesw = true;
				owner.backgroundStatus.fadecount = 0;
				owner.backgroundStatus.fadebg = nextseclv / 100;

				// BGMSwitching
				if((tableBGMChange[bgmlv] != -1) && (engine.statistics.level >= tableBGMChange[bgmlv])) {
					bgmlv++;
					owner.bgmStatus.fadesw = false;
					owner.bgmStatus.bgm = bgmlv + 2;
				}

				// Section TimeRecord
				sectionlasttime = sectiontime[levelb / 100];
				sectionscomp++;
				setAverageSectionTime();

				// ST medal
				stMedalCheck(engine, levelb / 100);

				// BoneBlockAdvent start
				if(engine.statistics.level >= 1000) engine.bone = true;

				// Update level for next section
				nextseclv += 100;

				if(sectionlasttime > tableTimeRegret[levelb / 100]) {
					// REGRETJudgment
					regretdispframe = 180;
					engine.playSE("regret");
				} else {
					// Dan rise
					grade++;
					if(grade > 13) grade = 13;
					gradeflash = 180;
				}
			} else if((engine.statistics.level == nextseclv - 1) && (lvstopse == true)) {
				engine.playSE("levelstop");
			}

			// Calculate score
			int manuallock = 0;
			if(engine.manualLock == true) manuallock = 1;

			int bravo = 1;
			if(engine.field.isEmpty()) bravo = 2;

			int speedBonus = engine.getLockDelay() - engine.statc[0];
			if(speedBonus < 0) speedBonus = 0;

			lastscore = ( ((levelb + lines) / 4 + engine.softdropFall + manuallock) * lines * comboValue + speedBonus +
						(engine.statistics.level / 2) ) * bravo;

			engine.statistics.score += lastscore;
			scgettime = 120;
		}
	}

	/*
	 * Ready→GoProcessing
	 */
	@Override
	public boolean onReady(GameEngine engine, int playerID) {
		if(engine.statc[0] == 0 && startlevel >= 11)
			engine.bone = true;
		return false;
	}

	/*
	 * Each frame Processing at the end of
	 */
	@Override
	public void onLast(GameEngine engine, int playerID) {
		// Flash at elevated dan
		if(gradeflash > 0) gradeflash--;

		// AcquisitionRender score
		if(scgettime > 0) scgettime--;

		// REGRETDisplay
		if(regretdispframe > 0) regretdispframe--;

		// Section TimeIncrease
		if((engine.timerActive) && (engine.ending == 0)) {
			int section = engine.statistics.level / 100;

			if((section >= 0) && (section < sectiontime.length)) {
				sectiontime[section]++;
			}
		}

		// Ending
		if((engine.gameActive) && (engine.ending == 2)) {
			rolltime++;

			// Time meter
			int remainRollTime = ROLLTIMELIMIT - rolltime;
			engine.meterValue = (remainRollTime * receiver.getMeterMax(engine)) / ROLLTIMELIMIT;
			engine.meterColor = GameEngine.METER_COLOR_GREEN;
			if(remainRollTime <= 30*60) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
			if(remainRollTime <= 20*60) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
			if(remainRollTime <= 10*60) engine.meterColor = GameEngine.METER_COLOR_RED;

			// Roll End
			if(rolltime >= ROLLTIMELIMIT) {
				secretGrade = engine.field.getSecretGrade();
				rollclear = 2;
				engine.gameEnded();
				engine.resetStatc();
				engine.stat = GameEngine.Status.EXCELLENT;
				owner.bgmStatus.bgm = BGMStatus.BGM_NOTHING;
			}
		}
	}

	/*
	 * Called at game over
	 */
	@Override
	public boolean onGameOver(GameEngine engine, int playerID) {
		if((engine.statc[0] == 0) && (engine.gameActive)) {
			secretGrade = engine.field.getSecretGrade();
		}
		return false;
	}

	/*
	 * Results screen
	 */
	@Override
	public void renderResult(GameEngine engine, int playerID) {
		receiver.drawMenuFont(engine, playerID, 0, 0, "kn PAGE" + (engine.statc[1] + 1) + "/3", EventReceiver.COLOR_RED);

		if(engine.statc[1] == 0) {
			int gcolor = EventReceiver.COLOR_WHITE;
			if((rollclear == 1) || (rollclear == 3)) gcolor = EventReceiver.COLOR_GREEN;
			if((rollclear == 2) || (rollclear == 4)) gcolor = EventReceiver.COLOR_ORANGE;
			receiver.drawMenuFont(engine, playerID, 0, 2, "GRADE", EventReceiver.COLOR_BLUE);
			String strGrade = String.format("%10s", tableGradeName[grade]);
			receiver.drawMenuFont(engine, playerID, 0, 3, strGrade, gcolor);

			drawResultStats(engine, playerID, receiver, 4, EventReceiver.COLOR_BLUE,
					Statistic.SCORE, Statistic.LINES, Statistic.LEVEL_MANIA, Statistic.TIME);
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
				receiver.drawMenuFont(engine, playerID, 0, 16, "AVERAGE", EventReceiver.COLOR_BLUE);
				receiver.drawMenuFont(engine, playerID, 2, 17, GeneralUtil.getTime(sectionavgtime));
			}
		} else if(engine.statc[1] == 2) {
			receiver.drawMenuFont(engine, playerID, 0, 2, "MEDAL", EventReceiver.COLOR_BLUE);
			if(medalAC >= 1) receiver.drawMenuFont(engine, playerID, 5, 3, "AC", getMedalFontColor(medalAC));
			if(medalST >= 1) receiver.drawMenuFont(engine, playerID, 8, 3, "ST", getMedalFontColor(medalST));
			if(medalSK >= 1) receiver.drawMenuFont(engine, playerID, 5, 4, "SK", getMedalFontColor(medalSK));
			if(medalCO >= 1) receiver.drawMenuFont(engine, playerID, 8, 4, "CO", getMedalFontColor(medalCO));

			drawResultStats(engine, playerID, receiver, 6, EventReceiver.COLOR_BLUE,
					Statistic.LPM, Statistic.SPM, Statistic.PIECE, Statistic.PPS);
		}
	}

	/*
	 * Processing of the results screen
	 */
	@Override
	public boolean onResult(GameEngine engine, int playerID) {
		// Page switching
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
		//  section time displaySwitching
		if(engine.ctrl.isPush(Controller.BUTTON_F)) {
			engine.playSE("change");
			isShowBestSectionTime = !isShowBestSectionTime;
		}

		return false;
	}

	/*
	 * Save replay
	 */
	@Override
	public void saveReplay(GameEngine engine, int playerID, CustomProperties prop) {
		saveSetting(owner.replayProp, engine.ruleopt.strRuleName);
		owner.replayProp.setProperty("speedmania2.version", version);

		// Update rankings
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
	 * Read rankings from property file
	 * @param prop Property file
	 * @param ruleName Rule name
	 */
	private void loadRanking(CustomProperties prop, String ruleName) {
		for(int i = 0; i < RANKING_MAX; i++) {
			rankingGrade[i] = prop.getProperty("speedmania2.ranking." + ruleName + ".grade." + i, 0);
			rankingLevel[i] = prop.getProperty("speedmania2.ranking." + ruleName + ".level." + i, 0);
			rankingTime[i] = prop.getProperty("speedmania2.ranking." + ruleName + ".time." + i, 0);
			rankingRollclear[i] = prop.getProperty("speedmania2.ranking." + ruleName + ".rollclear." + i, 0);
		}
		for(int i = 0; i < SECTION_MAX; i++) {
			bestSectionTime[i] = prop.getProperty("speedmania2.bestSectionTime." + ruleName + "." + i, DEFAULT_SECTION_TIME);
		}
	}

	/**
	 * Save rankings to property file
	 * @param prop Property file
	 * @param ruleName Rule name
	 */
	private void saveRanking(CustomProperties prop, String ruleName) {
		for(int i = 0; i < RANKING_MAX; i++) {
			prop.setProperty("speedmania2.ranking." + ruleName + ".grade." + i, rankingGrade[i]);
			prop.setProperty("speedmania2.ranking." + ruleName + ".level." + i, rankingLevel[i]);
			prop.setProperty("speedmania2.ranking." + ruleName + ".time." + i, rankingTime[i]);
			prop.setProperty("speedmania2.ranking." + ruleName + ".rollclear." + i, rankingRollclear[i]);
		}
		for(int i = 0; i < SECTION_MAX; i++) {
			prop.setProperty("speedmania2.bestSectionTime." + ruleName + "." + i, bestSectionTime[i]);
		}
	}

	/**
	 * Update rankings
	 * @param gr Dan
	 * @param lv  level
	 * @param time Time
	 * @param clear Roll Clear flag
	 */
	private void updateRanking(int gr, int lv, int time, int clear) {
		rankingRank = checkRanking(gr, lv, time, clear);

		if(rankingRank != -1) {
			// Shift down ranking entries
			for(int i = RANKING_MAX - 1; i > rankingRank; i--) {
				rankingGrade[i] = rankingGrade[i - 1];
				rankingLevel[i] = rankingLevel[i - 1];
				rankingTime[i] = rankingTime[i - 1];
				rankingRollclear[i] = rankingRollclear[i - 1];
			}

			// Add new data
			rankingGrade[rankingRank] = gr;
			rankingLevel[rankingRank] = lv;
			rankingTime[rankingRank] = time;
			rankingRollclear[rankingRank] = clear;
		}
	}

	/**
	 * Calculate ranking position
	 * @param gr Dan
	 * @param lv  level
	 * @param time Time
	 * @param clear Roll Clear flag
	 * @return Position (-1 if unranked)
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
