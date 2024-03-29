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
import mu.nu.nullpo.game.subsystem.mode.menu.IntegerMenuItem;
import mu.nu.nullpo.game.subsystem.mode.menu.OnOffMenuItem;
import mu.nu.nullpo.util.CustomProperties;
import mu.nu.nullpo.util.GeneralUtil;

/**
 * GRADE MANIA Mode
 */
public class GradeManiaMode extends AbstractMode {
	/** Current version */
	private static final int CURRENT_VERSION = 1;

	/** Fall velocity table */
	private static final int[] tableGravityValue =
	{
		4, 6, 8, 10, 12, 16, 32, 48, 64, 80, 96, 112, 128, 144, 4, 32, 64, 96, 128, 160, 192, 224, 256, 512, 768, 1024, 1280, 1024, 768, -1
	};

	/** Fall velocity changes level */
	private static final int[] tableGravityChangeLevel =
	{
		30, 35, 40, 50, 60, 70, 80, 90, 100, 120, 140, 160, 170, 200, 220, 230, 233, 236, 239, 243, 247, 251, 300, 330, 360, 400, 420, 450, 500, 10000
	};

	/** Needed to rise danScore */
	private static final int[] tableGradeScore =
	{
		   400,   800,  1400,  2000,  3500,  5500,  8000,  12000,			// 8~1
		 16000, 22000, 30000, 40000, 52000, 66000, 82000, 100000, 120000,	// S1~S9
		126000																// GM
	};

	/** Of danName */
	private static final String[] tableGradeName =
	{
		 "9",  "8",  "7",  "6",  "5",  "4",  "3",  "2",  "1",	//  0~ 8
		"S1", "S2", "S3", "S4", "S5", "S6", "S7", "S8", "S9",	//  9~17
		"GM"													// 18
	};

	/** GMEvaluation at the time ofTime */
	private static final int[] tablePier21GradeTime = {48600,43200,39600,37800,36000,33600,32400};

	/** GMEvaluation at the time of */
	private static final String[] tablePier21GradeName =
	{
		"ALUMINUM","STEEL","BRONZE","SILVER","GOLD","PLATINUM","DIAMOND"
	};

	/** LV999 roll time */
	private static final int ROLLTIMELIMIT = 2968;

	/** GMI need to takeLV300Dan when reaching the minimum */
	private static final int GM_300_GRADE_REQUIRE = 8;

	/** GMI need to takeLV500Dan when reaching the minimum */
	private static final int GM_500_GRADE_REQUIRE = 12;

	/** GMI need to takeLV300When reaching theTime */
	private static final int GM_300_TIME_REQUIRE = 15300;

	/** GMI need to takeLV500When reaching theTime */
	private static final int GM_500_TIME_REQUIRE = 27000;

	/** GMI need to takeLV500When reaching theTime(OldVersionUse) */
	private static final int GM_500_TIME_REQUIRE_V0 = 25200;

	/** GMI need to takeLV999When reaching theTime */
	private static final int GM_999_TIME_REQUIRE = 48600;

	/** Number of entries in rankings */
	private static final int RANKING_MAX = 10;

	/** Number of sections */
	private static final int SECTION_MAX = 10;

	/** Default section time */
	private static final int DEFAULT_SECTION_TIME = 5400;

	/** Current Speed ​​of fall number (tableGravityChangeLevelOf levelAt each of1Increase one) */
	private int gravityindex;

	/** Next Section Of level (This-1At levelStop) */
	private int nextseclv;

	/** LevelHas increased flag */
	private boolean lvupflag;

	/** Dan */
	private int grade;

	/** Dan went up at the end time */
	private int lastGradeTime;

	/** Combo bonus */
	private int comboValue;

	/** Most recent increase in score */
	private int lastscore;

	/** AcquisitionRender scoreIs remaining to be time */
	private int scgettime;

	/** Roll Course time */
	private int rolltime;

	/** LV300Dan provisions when it reachescountIf it was more thantrueBecome */
	private boolean gm300;

	/** LV500Dan provisions when it reachescount&amp; MoreTimeIf it was less than the prescribedtrueBecome */
	private boolean gm500;

	/** Dan back */
	private int secretGrade;

	/** Current BGM */
	private int bgmlv;

	/** Illuminate the display remaining dan frame count */
	private int gradeflash;

	/** Section Time */
	private int[] sectiontime;

	/** New record came outSection Thetrue */
	private boolean[] sectionIsNewRecord;

	/** SomewhereSection When I put out a new record intrue */
	private boolean sectionAnyNewRecord;

	/** Cleared Section count */
	private int sectionscomp;

	/** Average Section Time */
	private int sectionavgtime;

	/** Section TimeShowing record iftrue */
	private boolean isShowBestSectionTime;

	/** Level at start */
	private IntegerMenuItem startlevel;

	/** When true, always ghost ON */
	private OnOffMenuItem alwaysghost;

	/** When true, always 20G */
	private OnOffMenuItem always20g;

	/** When true, levelstop sound is enabled */
	private OnOffMenuItem lvstopse;

	/** BigMode */
	private OnOffMenuItem big;

	/** When true, section time display is enabled */
	private OnOffMenuItem showsectiontime;

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

	/** Section TimeRecord */
	private int[] bestSectionTime;

	public GradeManiaMode() {
		propName = "grademania";
		startlevel = new IntegerMenuItem("startlevel", "LEVEL", EventReceiver.COLOR_BLUE, 0, 0, 9) {
			public String getValueString() {
				return String.valueOf(value * 100);
			}
		};
		alwaysghost = new OnOffMenuItem("alwaysghost", "FULL GHOST", EventReceiver.COLOR_BLUE, false);
		always20g = new OnOffMenuItem("always20g", "20G MODE", EventReceiver.COLOR_BLUE, false);
		lvstopse = new OnOffMenuItem("lvstopse", "LVSTOPSE", EventReceiver.COLOR_BLUE, false);
		big = new OnOffMenuItem("big", "BIG", EventReceiver.COLOR_BLUE, false);
		showsectiontime = new OnOffMenuItem("showsectiontime", "SHOW STIME", EventReceiver.COLOR_BLUE, false);
		menu.add(startlevel);
		menu.add(alwaysghost);
		menu.add(always20g);
		menu.add(lvstopse);
		menu.add(showsectiontime);
		menu.add(big);
	}

	/*
	 * Mode name
	 */
	@Override
	public String getName() {
		return "GRADE MANIA";
	}

	/*
	 * Initialization
	 */
	@Override
	public void playerInit(GameEngine engine, int playerID) {
		owner = engine.owner;
		receiver = engine.owner.receiver;

		gravityindex = 0;
		nextseclv = 0;
		lvupflag = true;
		grade = 0;
		lastGradeTime = 0;
		comboValue = 0;
		lastscore = 0;
		scgettime = 0;
		rolltime = 0;
		gm300 = false;
		gm500 = false;
		secretGrade = 0;
		bgmlv = 0;
		gradeflash = 0;
		sectiontime = new int[SECTION_MAX];
		sectionIsNewRecord = new boolean[SECTION_MAX];
		sectionAnyNewRecord = false;
		sectionscomp = 0;
		sectionavgtime = 0;
		isShowBestSectionTime = false;

		rankingRank = -1;
		rankingGrade = new int[RANKING_MAX];
		rankingLevel = new int[RANKING_MAX];
		rankingTime = new int[RANKING_MAX];
		bestSectionTime = new int[SECTION_MAX];

		engine.tspinEnable = false;
		engine.b2bEnable = false;
		engine.comboType = GameEngine.COMBO_TYPE_DOUBLE;
		engine.bighalf = false;
		engine.bigmove = false;
		engine.staffrollNoDeath = true;

		engine.speed.are = 25;
		engine.speed.areLine = 25;
		engine.speed.lineDelay = 41;
		engine.speed.lockDelay = 30;
		engine.speed.das = 15;

		if(owner.replayMode == false) {
			loadSetting(owner.modeConfig);
			loadRanking(owner.modeConfig, engine.ruleopt.strRuleName);
			version = CURRENT_VERSION;
		} else {
			loadSetting(owner.replayProp);
			version = owner.replayProp.getProperty("grademania.version", 0);
		}

		owner.backgroundStatus.bg = startlevel.value;
	}

	/**
	 * Update falling speed
	 * @param engine GameEngine
	 */
	private void setSpeed(GameEngine engine) {
		if(always20g.value) {
			engine.speed.gravity = -1;
		} else {
			while(engine.statistics.level >= tableGravityChangeLevel[gravityindex]) gravityindex++;
			engine.speed.gravity = tableGravityValue[gravityindex];
		}
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
	 * Section TimeUpdate process
	 * @param sectionNumber Section number
	 */
	private void stNewRecordCheck(int sectionNumber) {
		if((sectiontime[sectionNumber] < bestSectionTime[sectionNumber]) && (!owner.replayMode)) {
			sectionIsNewRecord[sectionNumber] = true;
			sectionAnyNewRecord = true;
		}
	}

	/*
	 * Called at settings screen
	 */
	@Override
	public boolean onSetting(GameEngine engine, int playerID) {
		// Menu
		if(!engine.owner.replayMode) {
			updateMenu(engine);
			owner.backgroundStatus.bg = Math.min(9, startlevel.value);

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

		if(engine.statistics.level < 500) bgmlv = 0;
		else bgmlv = 1;

		engine.big = big.value;

		setSpeed(engine);
		owner.bgmStatus.bgm = bgmlv;
	}

	/*
	 * Render score
	 */
	@Override
	public void renderLast(GameEngine engine, int playerID) {
		receiver.drawScoreFont(engine, playerID, 0, 0, "GRADE MANIA", EventReceiver.COLOR_CYAN);

		if( (engine.stat == GameEngine.Status.SETTING) || ((engine.stat == GameEngine.Status.RESULT) && (!owner.replayMode)) ) {
			if((owner.replayMode == false) && (startlevel.value == 0) && (!big.value) && (!always20g.value) && (engine.ai == null)) {
				if(!isShowBestSectionTime) {
					// Rankings
					float scale = (receiver.getNextDisplayType() == 2) ? 0.5f : 1.0f;
					int topY = (receiver.getNextDisplayType() == 2) ? 5 : 3;
					receiver.drawScoreFont(engine, playerID, 3, topY-1, "GRADE LEVEL TIME", EventReceiver.COLOR_BLUE, scale);

					for(int i = 0; i < RANKING_MAX; i++) {
						receiver.drawScoreFont(engine, playerID, 0, topY+i, String.format("%2d", i + 1), EventReceiver.COLOR_YELLOW, scale);
						if((rankingGrade[i] >= 0) && (rankingGrade[i] < tableGradeName.length))
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
			receiver.drawScoreFont(engine, playerID, 0, 2, "GRADE", EventReceiver.COLOR_BLUE);
			if((grade >= 0) && (grade < tableGradeName.length))
				receiver.drawScoreFont(engine, playerID, 0, 3, tableGradeName[grade], ((gradeflash > 0) && (gradeflash % 4 == 0)));

			receiver.drawScoreFont(engine, playerID, 0, 5, "POINTS", EventReceiver.COLOR_BLUE);
			String strScore;
			if((lastscore == 0) || (scgettime <= 0)) {
				strScore = String.valueOf(engine.statistics.score);
			} else {
				strScore = String.valueOf(engine.statistics.score) + "(+" + String.valueOf(lastscore) + ")";
			}
			receiver.drawScoreFont(engine, playerID, 0, 6, strScore);
			if(grade < 17) {
				receiver.drawScoreFont(engine, playerID, 0, 7, String.valueOf(tableGradeScore[grade]));
			}

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
	 * Processing on the move
	 */
	@Override
	public boolean onMove(GameEngine engine, int playerID) {
		// Occurrence new piece
		if((engine.ending == 0) && (engine.statc[0] == 0) && (engine.holdDisable == false) && (!lvupflag)) {
			if(engine.statistics.level < nextseclv - 1) {
				engine.statistics.level++;
				if((engine.statistics.level == nextseclv - 1) && (lvstopse.value)) engine.playSE("levelstop");
			}
			levelUp(engine);
		}
		if( (engine.ending == 0) && (engine.statc[0] > 0) && ((version >= 1) || (engine.holdDisable == false)) ) {
			lvupflag = false;
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
				if((engine.statistics.level == nextseclv - 1) && (lvstopse.value)) engine.playSE("levelstop");
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

		// LV100In reachingghost Disappear
		if((engine.statistics.level >= 100) && (!alwaysghost.value)) engine.ghost = false;

		// BGM fadeout
		if((bgmlv == 0) && (engine.statistics.level >= 490))
			owner.bgmStatus.fadesw  = true;
	}

	/*
	 * Calculate score
	 */
	@Override
	public void calcScore(GameEngine engine, int playerID, int lines) {
		if(engine.ending != 0) return;

		// Combo
		if(lines == 0) {
			comboValue = 1;
		} else {
			comboValue = comboValue + (2 * lines) - 2;
			if(comboValue < 1) comboValue = 1;
		}

		if(lines >= 1) {
			// Calculate score
			int manuallock = 0;
			if(engine.manualLock == true) manuallock = 1;

			int bravo = 1;
			if(engine.field.isEmpty()) {
				bravo = 4;
				engine.playSE("bravo");
			}

			lastscore = ( ((engine.statistics.level + lines) / 4) + engine.softdropFall + engine.harddropFall + manuallock ) * lines * comboValue * bravo;
			engine.statistics.score += lastscore;
			scgettime = 120;

			// Dan rise
			while((grade < 17) && (engine.statistics.score >= tableGradeScore[grade])) {
				engine.playSE("gradeup");
				grade++;
				gradeflash = 180;
				lastGradeTime = engine.statistics.time;
			}

			// Level up
			engine.statistics.level += lines;
			levelUp(engine);

			if(engine.statistics.level >= 999) {
				// Ending
				engine.statistics.level = 999;
				engine.timerActive = false;
				lastGradeTime = engine.statistics.time;

				sectionscomp++;
				setAverageSectionTime();
				stNewRecordCheck(sectionscomp - 1);

				if((engine.statistics.time <= GM_999_TIME_REQUIRE) && (engine.statistics.score >= tableGradeScore[17]) && (gm300) && (gm500)) {
					engine.playSE("endingstart");
					engine.playSE("gradeup");

					grade = 18;
					gradeflash = 180;

					owner.bgmStatus.fadesw = false;
					owner.bgmStatus.bgm = BGMStatus.BGM_ENDING2;

					engine.ending = 2;
				} else {
					engine.gameEnded();
					engine.ending = 1;
				}
			} else if(engine.statistics.level >= nextseclv) {
				// Next Section
				engine.playSE("levelup");

				owner.backgroundStatus.fadesw = true;
				owner.backgroundStatus.fadecount = 0;
				owner.backgroundStatus.fadebg = nextseclv / 100;

				if(version >= 1) {
					if((nextseclv == 300) && (grade >= GM_300_GRADE_REQUIRE) && (engine.statistics.time <= GM_300_TIME_REQUIRE))
						gm300 = true;
					if((nextseclv == 500) && (grade >= GM_500_GRADE_REQUIRE) && (engine.statistics.time <= GM_500_TIME_REQUIRE))
						gm500 = true;
				} else {
					if((nextseclv == 300) && (grade >= GM_300_GRADE_REQUIRE))
						gm300 = true;
					if((nextseclv == 500) && (grade >= GM_500_GRADE_REQUIRE) && (engine.statistics.time <= GM_500_TIME_REQUIRE_V0))
						gm500 = true;
				}

				sectionscomp++;
				setAverageSectionTime();
				stNewRecordCheck(sectionscomp - 1);

				if((bgmlv == 0) && (nextseclv == 500)) {
					bgmlv++;
					owner.bgmStatus.fadesw = false;
					owner.bgmStatus.bgm = bgmlv;
				}

				nextseclv += 100;
				if(nextseclv > 999) nextseclv = 999;
			} else if((engine.statistics.level == nextseclv - 1) && (lvstopse.value)) {
				engine.playSE("levelstop");
			}
		}
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
				engine.gameEnded();
				engine.resetStatc();
				engine.stat = GameEngine.Status.EXCELLENT;
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
	 * Results screen
	 */
	@Override
	public void renderResult(GameEngine engine, int playerID) {
		receiver.drawMenuFont(engine, playerID, 0, 0, "kn PAGE" + (engine.statc[1] + 1) + "/3", EventReceiver.COLOR_RED);

		if(engine.statc[1] == 0) {
			drawResult(engine, playerID, receiver, 2, EventReceiver.COLOR_BLUE,
					"GRADE", String.format("%10s", tableGradeName[grade]));

			drawResultStats(engine, playerID, receiver, 4, EventReceiver.COLOR_BLUE,
					Statistic.SCORE, Statistic.LINES, Statistic.LEVEL_MANIA, Statistic.TIME);
			drawResultRank(engine, playerID, receiver, 12, EventReceiver.COLOR_BLUE, rankingRank);
			if(secretGrade > 4) {
				drawResult(engine, playerID, receiver, 14, EventReceiver.COLOR_BLUE,
						"S. GRADE", String.format("%10s", tableGradeName[secretGrade-1]));
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
			drawResultStats(engine, playerID, receiver, 2, EventReceiver.COLOR_BLUE,
					Statistic.LPM, Statistic.SPM, Statistic.PIECE, Statistic.PPS);

			if(grade == 18) {
				int pierRank = 0;
				for(int i = 1; i < tablePier21GradeTime.length; i++) {
					if(engine.statistics.time < tablePier21GradeTime[i]) pierRank = i;
				}
				drawResult(engine, playerID, receiver, 10, EventReceiver.COLOR_BLUE,
						"PIER GRADE", String.format("%10s", tablePier21GradeName[pierRank]));
			}
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
		saveSetting(owner.replayProp);
		owner.replayProp.setProperty("result.grade.name", tableGradeName[grade]);
		owner.replayProp.setProperty("result.grade.number", grade);
		owner.replayProp.setProperty("grademania.version", version);

		// Update rankings
		if((owner.replayMode == false) && (startlevel.value == 0) && (!always20g.value) && (big.value) && (engine.ai == null)) {
			updateRanking(grade, engine.statistics.level, lastGradeTime);
			if(sectionAnyNewRecord) updateBestSectionTime();

			if((rankingRank != -1) || (sectionAnyNewRecord)) {
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
			rankingGrade[i] = prop.getProperty("grademania.ranking." + ruleName + ".grade." + i, 0);
			rankingLevel[i] = prop.getProperty("grademania.ranking." + ruleName + ".level." + i, 0);
			rankingTime[i] = prop.getProperty("grademania.ranking." + ruleName + ".time." + i, 0);
		}
		for(int i = 0; i < SECTION_MAX; i++) {
			bestSectionTime[i] = prop.getProperty("grademania.bestSectionTime." + ruleName + "." + i, DEFAULT_SECTION_TIME);
		}
	}

	/**
	 * Save rankings to property file
	 * @param prop Property file
	 * @param ruleName Rule name
	 */
	private void saveRanking(CustomProperties prop, String ruleName) {
		for(int i = 0; i < RANKING_MAX; i++) {
			prop.setProperty("grademania.ranking." + ruleName + ".grade." + i, rankingGrade[i]);
			prop.setProperty("grademania.ranking." + ruleName + ".level." + i, rankingLevel[i]);
			prop.setProperty("grademania.ranking." + ruleName + ".time." + i, rankingTime[i]);
		}
		for(int i = 0; i < SECTION_MAX; i++) {
			prop.setProperty("grademania.bestSectionTime." + ruleName + "." + i, bestSectionTime[i]);
		}
	}

	/**
	 * Update rankings
	 * @param gr Dan
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
	 * @param gr Dan
	 * @param lv  level
	 * @param time Time
	 * @return Position (-1 if unranked)
	 */
	private int checkRanking(int gr, int lv, int time) {
		for(int i = 0; i < RANKING_MAX; i++) {
			if(gr > rankingGrade[i]) {
				return i;
			} else if((gr == rankingGrade[i]) && (lv > rankingLevel[i])) {
				return i;
			} else if((gr == rankingGrade[i]) && (lv == rankingLevel[i]) && (time < rankingTime[i])) {
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
