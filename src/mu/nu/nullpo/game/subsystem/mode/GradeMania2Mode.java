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
 * GRADE MANIA 2 Mode
 */
public class GradeMania2Mode extends DummyMode {
	/** Current version */
	private static final int CURRENT_VERSION = 2;

	/** 落下速度 table */
	private static final int[] tableGravityValue =
	{
		4, 6, 8, 10, 12, 16, 32, 48, 64, 80, 96, 112, 128, 144, 4, 32, 64, 96, 128, 160, 192, 224, 256, 512, 768, 1024, 1280, 1024, 768, -1
	};

	/** 落下速度が変わる level */
	private static final int[] tableGravityChangeLevel =
	{
		30, 35, 40, 50, 60, 70, 80, 90, 100, 120, 140, 160, 170, 200, 220, 230, 233, 236, 239, 243, 247, 251, 300, 330, 360, 400, 420, 450, 500, 10000
	};

	/** ARE table */
	private static final int[] tableARE       = {23, 23, 23, 23, 23, 23, 23, 14, 10, 10};

	/** ARE after line clear table */
	private static final int[] tableARELine   = {23, 23, 23, 23, 23, 23, 14, 10,  4,  4};

	/** Line clear time table */
	private static final int[] tableLineDelay = {40, 40, 40, 40, 40, 25, 16, 12,  6,  6};

	/** 固定 time table */
	private static final int[] tableLockDelay = {31, 31, 31, 31, 31, 31, 31, 31, 31, 18};

	/** DAS table */
	private static final int[] tableDAS       = {15, 15, 15, 15, 15,  9,  9,  9,  9,  7};

	/** BGM fadeout levels */
	private static final int[] tableBGMFadeout = {495,695,880,-1};

	/** BGM change levels */
	private static final int[] tableBGMChange  = {500,700,900,-1};

	/** Line clear時に入る段位 point */
	private static final int[][] tableGradePoint =
	{
		{10,10,10,10,10, 5, 5, 5, 5, 5, 2},
		{20,20,20,15,15,15,10,10,10,10,12},
		{40,30,30,30,20,20,20,15,15,15,13},
		{50,40,40,40,40,30,30,30,30,30,30},
	};

	/** 段位 pointのCombo bonus */
	private static final float[][] tableGradeComboBonus =
	{
		{1.0f,1.2f,1.2f,1.4f,1.4f,1.4f,1.4f,1.5f,1.5f,2.0f},
		{1.0f,1.4f,1.5f,1.6f,1.7f,1.8f,1.9f,2.0f,2.1f,2.5f},
		{1.0f,1.5f,1.8f,2.0f,2.2f,2.3f,2.4f,2.5f,2.6f,3.0f},
		{1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f},
	};

	/** 実際の段位を上げるのに必要な内部段位 */
	private static final int[] tableGradeChange =
	{
		1, 2, 3, 4, 5, 7, 9, 12, 15, 18, 19, 20, 23, 25, 27, 29, 31, -1
	};

	/** 段位 pointが1つ減る time */
	private static final int[] tableGradeDecayRate =
	{
		125, 80, 80, 50, 45, 45, 45, 40, 40, 40, 40, 40, 30, 30, 30, 20, 20, 20, 20, 20, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 10, 10
	};

	/** 段位のName */
	private static final String[] tableGradeName =
	{
		 "9",  "8",  "7",  "6",  "5",  "4",  "3",  "2",  "1",	//  0～ 8
		"S1", "S2", "S3", "S4", "S5", "S6", "S7", "S8", "S9",	//  9～17
		 "M", "GM"												// 18～19
	};

	/** 裏段位のName */
	private static final String[] tableSecretGradeName =
	{
		 "9",  "8",  "7",  "6",  "5",  "4",  "3",  "2",  "1",	//  0～ 8
		"S1", "S2", "S3", "S4", "S5", "S6", "S7", "S8", "S9",	//  9～17
		"GM"													// 18
	};

	/** LV999 roll time */
	private static final int ROLLTIMELIMIT = 3694;

	/** 消えRoll に必要なLV999到達時のTime */
	private static final int M_ROLL_TIME_REQUIRE = 31500;

	/** Number of entries in rankings */
	private static final int RANKING_MAX = 10;

	/** Number of sections */
	private static final int SECTION_MAX = 10;

	/** Default section time */
	private static final int DEFAULT_SECTION_TIME = 5400;

	/** GameManager that owns this mode */
	private GameManager owner;

	/** Drawing and event handling EventReceiver */
	private EventReceiver receiver;

	/** Current 落下速度の number (tableGravityChangeLevelの levelに到達するたびに1つ増える) */
	private int gravityindex;

	/** Next Section の level (これ-1のときに levelストップする) */
	private int nextseclv;

	/** Levelが増えた flag */
	private boolean lvupflag;

	/** 画面に表示されている実際の段位 */
	private int grade;

	/** 内部段位 */
	private int gradeInternal;

	/** 段位 point */
	private int gradePoint;

	/** 段位 pointが1つ減る time */
	private int gradeDecay;

	/** 最後に段位が上がった time */
	private int lastGradeTime;

	/** Hard dropした段count */
	private int harddropBonus;

	/** Combo bonus */
	private int comboValue;

	/** Most recent increase in score */
	private int lastscore;

	/** 獲得Render scoreがされる残り time */
	private int scgettime;

	/** Roll 経過 time */
	private int rolltime;

	/** Roll completely cleared flag */
	private int rollclear;

	/** Roll started flag */
	private boolean rollstarted;

	/** 裏段位 */
	private int secretGrade;

	/** Current BGM */
	private int bgmlv;

	/** 段位表示を光らせる残り frame count */
	private int gradeflash;

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

	/** Section 内で4-line clearた count */
	private int[] sectionfourline;

	/** 消えRoll  flag１ (Section Time) */
	private boolean mrollSectiontime;

	/** 消えRoll  flag２ (4-line clear) */
	private boolean mrollFourline;

	/** 消えRoll started flag */
	private boolean mrollFlag;

	/** 消えRoll 中に消したline count */
	private int mrollLines;

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
	private int startlevel;

	/** When true, always ghost ON */
	private boolean alwaysghost;

	/** When true, always 20G */
	private boolean always20g;

	/** When true, levelstop sound is enabled */
	private boolean lvstopse;

	/** BigMode */
	private boolean big;

	/** When true, section time display is enabled */
	private boolean showsectiontime;

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

	/** Rankings' Roll completely cleared flag */
	private int[] rankingRollclear;

	/** Section Time記録 */
	private int[] bestSectionTime;

	/*
	 * Mode name
	 */
	@Override
	public String getName() {
		return "GRADE MANIA 2";
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
		gradeInternal = 0;
		gradePoint = 0;
		gradeDecay = 0;
		lastGradeTime = 0;
		harddropBonus = 0;
		comboValue = 0;
		lastscore = 0;
		scgettime = 0;
		rolltime = 0;
		rollclear = 0;
		rollstarted = false;
		secretGrade = 0;
		bgmlv = 0;
		gradeflash = 0;
		sectiontime = new int[SECTION_MAX];
		sectionIsNewRecord = new boolean[SECTION_MAX];
		sectionscomp = 0;
		sectionavgtime = 0;
		sectionlasttime = 0;
		sectionfourline = new int[SECTION_MAX];
		mrollSectiontime = true;
		mrollFourline = true;
		mrollFlag = false;
		mrollLines = 0;
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
		alwaysghost = false;
		always20g = false;
		lvstopse = false;
		big = false;

		rankingRank = -1;
		rankingGrade = new int[RANKING_MAX];
		rankingLevel = new int[RANKING_MAX];
		rankingTime = new int[RANKING_MAX];
		rankingRollclear = new int[RANKING_MAX];
		bestSectionTime = new int[SECTION_MAX];

		engine.tspinEnable = false;
		engine.b2bEnable = false;
		engine.comboType = GameEngine.COMBO_TYPE_DOUBLE;
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
			version = owner.replayProp.getProperty("grademania2.version", 0);
		}

		owner.backgroundStatus.bg = startlevel;
	}

	/**
	 * Load settings from property file
	 * @param prop Property file
	 */
	private void loadSetting(CustomProperties prop) {
		startlevel = prop.getProperty("grademania2.startlevel", 0);
		alwaysghost = prop.getProperty("grademania2.alwaysghost", false);
		always20g = prop.getProperty("grademania2.always20g", false);
		lvstopse = prop.getProperty("grademania2.lvstopse", false);
		showsectiontime = prop.getProperty("grademania2.showsectiontime", false);
		big = prop.getProperty("grademania2.big", false);
	}

	/**
	 * Save settings to property file
	 * @param prop Property file
	 */
	private void saveSetting(CustomProperties prop) {
		prop.setProperty("grademania2.startlevel", startlevel);
		prop.setProperty("grademania2.alwaysghost", alwaysghost);
		prop.setProperty("grademania2.always20g", always20g);
		prop.setProperty("grademania2.lvstopse", lvstopse);
		prop.setProperty("grademania2.showsectiontime", showsectiontime);
		prop.setProperty("grademania2.big", big);
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
		if((always20g == true) || (engine.statistics.time >= 54000)) {
			engine.speed.gravity = -1;
		} else {
			while(engine.statistics.level >= tableGravityChangeLevel[gravityindex]) gravityindex++;
			engine.speed.gravity = tableGravityValue[gravityindex];
		}

		int section = engine.statistics.level / 100;
		if(section > tableARE.length - 1) section = tableARE.length - 1;
		engine.speed.das = tableDAS[section];

		if(engine.statistics.time >= 54000) {
			engine.speed.are = 3;
			engine.speed.areLine = 6;
			engine.speed.lineDelay = 6;
			engine.speed.lockDelay = 19;
		} else {
			engine.speed.are = tableARE[section];
			engine.speed.areLine = tableARELine[section];
			engine.speed.lineDelay = tableLineDelay[section];
			engine.speed.lockDelay = tableLockDelay[section];
		}
	}

	/**
	 * Update average section time
	 */
	private void setAverageSectionTime() {
		if(sectionscomp > 0) {
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
	 * 消えRoll 条件を満たしているか check
	 * @param levelb 上がる前の level
	 */
	private void mrollCheck(int levelb) {
		// Section Time
		if(levelb < 500) {
			if(sectionlasttime > 3900) mrollSectiontime = false;
		} else if(levelb < 600) {
			int temp = 0;
			for(int i = 0; i < 5; i++) temp += sectiontime[i];
			temp = temp / 5;
			if(sectionlasttime > temp + 120) mrollSectiontime = false;
		} else {
			int temp = sectiontime[(levelb / 100) - 1];
			if(sectionlasttime > temp + 120) mrollSectiontime = false;
		}

		// 4-line clear
		int required4line = 2;
		if((levelb >= 500) && (levelb < 900)) required4line = 1;
		if(levelb >= 900) required4line = 0;

		if(sectionfourline[levelb / 100] < required4line) {
			mrollFourline = false;
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
			int change = updateCursor(engine, 5);

			if(change != 0) {
				engine.playSE("change");

				switch(engine.statc[2]) {
				case 0:
					startlevel += change;
					if(startlevel < 0) startlevel = 9;
					if(startlevel > 9) startlevel = 0;
					owner.backgroundStatus.bg = startlevel;
					break;
				case 1:
					alwaysghost = !alwaysghost;
					break;
				case 2:
					always20g = !always20g;
					break;
				case 3:
					lvstopse = !lvstopse;
					break;
				case 4:
					showsectiontime = !showsectiontime;
					break;
				case 5:
					big = !big;
					break;
				}
			}

			//  section time display切替
			if(engine.ctrl.isPush(Controller.BUTTON_F) && (engine.statc[3] >= 5)) {
				engine.playSE("change");
				isShowBestSectionTime = !isShowBestSectionTime;
			}

			// 決定
			if(engine.ctrl.isPush(Controller.BUTTON_A) && (engine.statc[3] >= 5)) {
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
		drawMenu(engine, playerID, receiver, 0, EventReceiver.COLOR_BLUE, 0,
				"LEVEL", String.valueOf(startlevel * 100),
				"FULL GHOST", GeneralUtil.getONorOFF(alwaysghost),
				"20G MODE", GeneralUtil.getONorOFF(always20g),
				"LVSTOPSE", GeneralUtil.getONorOFF(lvstopse),
				"SHOW STIME", GeneralUtil.getONorOFF(showsectiontime),
				"BIG",  GeneralUtil.getONorOFF(big));
	}

	/*
	 * Called at game start
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
		owner.bgmStatus.bgm = bgmlv;
	}

	/*
	 * Render score
	 */
	@Override
	public void renderLast(GameEngine engine, int playerID) {
		receiver.drawScoreFont(engine, playerID, 0, 0, "GRADE MANIA 2", EventReceiver.COLOR_CYAN);

		if( (engine.stat == GameEngine.STAT_SETTING) || ((engine.stat == GameEngine.STAT_RESULT) && (owner.replayMode == false)) ) {
			if((owner.replayMode == false) && (startlevel == 0) && (big == false) && (always20g == false) && (engine.ai == null)) {
				if(!isShowBestSectionTime) {
					// Rankings
					receiver.drawScoreFont(engine, playerID, 3, 2, "GRADE LEVEL TIME", EventReceiver.COLOR_BLUE);

					for(int i = 0; i < RANKING_MAX; i++) {
						int gcolor = EventReceiver.COLOR_WHITE;
						if((rankingRollclear[i] == 1) || (rankingRollclear[i] == 3)) gcolor = EventReceiver.COLOR_GREEN;
						if((rankingRollclear[i] == 2) || (rankingRollclear[i] == 4)) gcolor = EventReceiver.COLOR_ORANGE;

						receiver.drawScoreFont(engine, playerID, 0, 3 + i, String.format("%2d", i + 1), EventReceiver.COLOR_YELLOW);
						if((rankingGrade[i] >= 0) && (rankingGrade[i] < tableGradeName.length))
							receiver.drawScoreFont(engine, playerID, 3, 3 + i, tableGradeName[rankingGrade[i]], gcolor);
						receiver.drawScoreFont(engine, playerID, 9, 3 + i, String.valueOf(rankingLevel[i]), (i == rankingRank));
						receiver.drawScoreFont(engine, playerID, 15, 3 + i, GeneralUtil.getTime(rankingTime[i]), (i == rankingRank));
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
			if((showsectiontime == true) && (sectiontime != null)) {
				receiver.drawScoreFont(engine, playerID, 12, 2, "SECTION TIME", EventReceiver.COLOR_BLUE);

				for(int i = 0; i < sectiontime.length; i++) {
					if(sectiontime[i] > 0) {
						int temp = i * 100;
						if(temp > 999) temp = 999;

						int section = engine.statistics.level / 100;
						String strSeparator = " ";
						if((i == section) && (engine.ending == 0)) strSeparator = "b";

						String strSectionTime;
						strSectionTime = String.format("%3d%s%s", temp, strSeparator, GeneralUtil.getTime(sectiontime[i]));

						receiver.drawScoreFont(engine, playerID, 12, 3 + i, strSectionTime, sectionIsNewRecord[i]);
					}
				}

				if(sectionavgtime > 0) {
					receiver.drawScoreFont(engine, playerID, 12, 14, "AVERAGE", EventReceiver.COLOR_BLUE);
					receiver.drawScoreFont(engine, playerID, 12, 15, GeneralUtil.getTime(sectionavgtime));
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
				if((engine.statistics.level == nextseclv - 1) && (lvstopse == true)) engine.playSE("levelstop");
			}
			levelUp(engine);

			// 旧Version用
			if(version <= 1) {
				// Hard drop bonusInitialization
				harddropBonus = 0;

				// RE medal
				if((engine.timerActive == true) && (medalRE < 3)) {
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
		}
		if( (engine.ending == 0) && (engine.statc[0] > 0) && ((version >= 1) || (engine.holdDisable == false)) ) {
			lvupflag = false;
		}

		// 段位 point減少
		if((engine.timerActive == true) && (gradePoint > 0) && (engine.combo <= 0) && (engine.lockDelayNow < engine.getLockDelay() - 1)) {
			gradeDecay++;

			int index = gradeInternal;
			if(index > tableGradeDecayRate.length - 1) index = tableGradeDecayRate.length - 1;

			if(gradeDecay >= tableGradeDecayRate[index]) {
				gradeDecay = 0;
				gradePoint--;
			}
		}

		// Endingスタート
		if((engine.ending == 2) && (rollstarted == false)) {
			rollstarted = true;

			if(mrollFlag) {
				engine.blockHidden = engine.ruleopt.lockflash;
				engine.blockHiddenAnim = false;
				engine.blockOutlineType = GameEngine.BLOCK_OUTLINE_NONE;
			} else {
				engine.blockHidden = 300;
				engine.blockHiddenAnim = true;
				engine.blockOutlineType = GameEngine.BLOCK_OUTLINE_NONE;
			}

			owner.bgmStatus.bgm = BGMStatus.BGM_ENDING1;
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
				if((engine.statistics.level == nextseclv - 1) && (lvstopse == true)) engine.playSE("levelstop");
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

		// LV100到達でghost を消す
		if((engine.statistics.level >= 100) && (!alwaysghost)) engine.ghost = false;

		// BGM fadeout
		if((tableBGMFadeout[bgmlv] != -1) && (engine.statistics.level >= tableBGMFadeout[bgmlv]))
			owner.bgmStatus.fadesw  = true;

		if(version >= 2) {
			// Hard drop bonusInitialization
			harddropBonus = 0;

			// RE medal
			if((engine.timerActive == true) && (medalRE < 3)) {
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
			// 段位 point
			int index = gradeInternal;
			if(index > 10) index = 10;
			int basepoint = tableGradePoint[lines - 1][index];

			int indexcombo = engine.combo - 1;
			if(indexcombo < 0) indexcombo = 0;
			if(indexcombo > tableGradeComboBonus[lines - 1].length - 1) indexcombo = tableGradeComboBonus[lines - 1].length - 1;
			float combobonus = tableGradeComboBonus[lines - 1][indexcombo];

			int levelbonus = 1 + (engine.statistics.level / 250);

			float point = (basepoint * combobonus) * levelbonus;
			gradePoint += (int)point;

			// 内部段位上昇
			if(gradePoint >= 100) {
				gradePoint = 0;
				gradeDecay = 0;
				gradeInternal++;

				if((tableGradeChange[grade] != -1) && (gradeInternal >= tableGradeChange[grade])) {
					engine.playSE("gradeup");
					grade++;
					gradeflash = 180;
					lastGradeTime = engine.statistics.time;
				}
			}

			// 4-line clearカウント
			if(lines >= 4) {
				sectionfourline[engine.statistics.level / 100]++;

				// SK medal
				if(big == true) {
					if((engine.statistics.totalFour == 1) || (engine.statistics.totalFour == 2) || (engine.statistics.totalFour == 4)) {
						engine.playSE("medal");
						medalSK++;
					}
				} else {
					if((engine.statistics.totalFour == 10) || (engine.statistics.totalFour == 20) || (engine.statistics.totalFour == 35)) {
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

			// Level up
			int levelb = engine.statistics.level;
			engine.statistics.level += lines;
			levelUp(engine);

			if(engine.statistics.level >= 999) {
				// Ending
				engine.statistics.level = 999;
				engine.timerActive = false;
				engine.ending = 1;
				rollclear = 1;

				lastGradeTime = engine.statistics.time;

				// Section Timeを記録
				sectionlasttime = sectiontime[levelb / 100];
				sectionscomp++;
				setAverageSectionTime();

				// 消えRoll check
				mrollCheck(levelb);

				// ST medal
				stMedalCheck(engine, levelb / 100);

				// RO medal
				roMedalCheck(engine);

				// 条件を全て満たしているなら消えRoll 発動
				if((mrollSectiontime == true) && (mrollFourline == true) && (engine.statistics.time <= M_ROLL_TIME_REQUIRE) && (grade >= 17))
					mrollFlag = true;
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
					owner.bgmStatus.bgm = bgmlv;
				}

				// Section Timeを記録
				sectionlasttime = sectiontime[levelb / 100];
				sectionscomp++;
				setAverageSectionTime();

				// 消えRoll check
				mrollCheck(levelb);

				// ST medal
				stMedalCheck(engine, levelb / 100);

				// RO medal
				if((nextseclv == 300) || (nextseclv == 700)) roMedalCheck(engine);

				// Update level for next section
				nextseclv += 100;
				if(nextseclv > 999) nextseclv = 999;
			} else if((engine.statistics.level == nextseclv - 1) && (lvstopse == true)) {
				engine.playSE("levelstop");
			}

			// Calculate score
			int manuallock = 0;
			if(engine.manualLock == true) manuallock = 1;

			int bravo = 1;
			if(engine.field.isEmpty()) bravo = 4;

			int speedBonus = engine.getLockDelay() - engine.statc[0];
			if(speedBonus < 0) speedBonus = 0;

			lastscore = ((levelb + lines)/4 + engine.softdropFall + manuallock + harddropBonus) * lines * comboValue * bravo +
						(engine.statistics.level / 2) + (speedBonus * 7);
			engine.statistics.score += lastscore;
			scgettime = 120;
		} else if((lines >= 1) && (mrollFlag == true) && (engine.ending == 2)) {
			// 消えRoll 中のLine clear
			mrollLines += lines;
		}
	}

	/*
	 * Called when hard drop used
	 */
	@Override
	public void afterHardDropFall(GameEngine engine, int playerID, int fall) {
		if(fall * 2 > harddropBonus) harddropBonus = fall * 2;
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

		// 15分経過
		if(engine.statistics.time >= 54000) {
			setSpeed(engine);
		}

		// Section Time増加
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

			// Roll 終了
			if(rolltime >= ROLLTIMELIMIT) {
				rollclear = 2;

				if(mrollFlag == true) {
					grade = 19;
					gradeflash = 180;
					lastGradeTime = engine.statistics.time;
					engine.playSE("gradeup");

					rollclear = 3;
					if(mrollLines >= 32) rollclear = 4;
				}

				engine.blockOutlineType = GameEngine.BLOCK_OUTLINE_NORMAL;

				engine.gameActive = false;
				engine.resetStatc();
				engine.stat = GameEngine.STAT_EXCELLENT;
			}
		}
	}

	/*
	 * game over
	 */
	@Override
	public boolean onGameOver(GameEngine engine, int playerID) {
		// 段位M
		if((mrollFlag == true) && (grade < 18) && (engine.ending == 2) && (engine.statc[0] == 0)) {
			grade = 18;
			gradeflash = 180;
			lastGradeTime = engine.statistics.time;
			engine.playSE("gradeup");
		}

		if(engine.statc[0] == 0) {
			// Blockの表示を元に戻す
			engine.blockOutlineType = GameEngine.BLOCK_OUTLINE_NORMAL;
			// 裏段位
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
			int gcolor = EventReceiver.COLOR_WHITE;
			if((rollclear == 1) || (rollclear == 3)) gcolor = EventReceiver.COLOR_GREEN;
			if((rollclear == 2) || (rollclear == 4)) gcolor = EventReceiver.COLOR_ORANGE;
			receiver.drawMenuFont(engine, playerID, 0, 2, "GRADE", EventReceiver.COLOR_BLUE);
			String strGrade = String.format("%10s", tableGradeName[grade]);
			receiver.drawMenuFont(engine, playerID, 0, 3, strGrade, gcolor);

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
		owner.replayProp.setProperty("result.grade.name", tableGradeName[grade]);
		owner.replayProp.setProperty("result.grade.number", grade);
		owner.replayProp.setProperty("grademania2.version", version);

		// Update rankings
		if((owner.replayMode == false) && (startlevel == 0) && (always20g == false) && (big == false) && (engine.ai == null)) {
			updateRanking(grade, engine.statistics.level, lastGradeTime, rollclear);
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
			rankingGrade[i] = prop.getProperty("grademania2.ranking." + ruleName + ".grade." + i, 0);
			rankingLevel[i] = prop.getProperty("grademania2.ranking." + ruleName + ".level." + i, 0);
			rankingTime[i] = prop.getProperty("grademania2.ranking." + ruleName + ".time." + i, 0);
			rankingRollclear[i] = prop.getProperty("grademania2.ranking." + ruleName + ".rollclear." + i, 0);
		}
		for(int i = 0; i < SECTION_MAX; i++) {
			bestSectionTime[i] = prop.getProperty("grademania2.bestSectionTime." + ruleName + "." + i, DEFAULT_SECTION_TIME);
		}
	}

	/**
	 * Save rankings to property file
	 * @param prop Property file
	 * @param ruleName Rule name
	 */
	private void saveRanking(CustomProperties prop, String ruleName) {
		for(int i = 0; i < RANKING_MAX; i++) {
			prop.setProperty("grademania2.ranking." + ruleName + ".grade." + i, rankingGrade[i]);
			prop.setProperty("grademania2.ranking." + ruleName + ".level." + i, rankingLevel[i]);
			prop.setProperty("grademania2.ranking." + ruleName + ".time." + i, rankingTime[i]);
			prop.setProperty("grademania2.ranking." + ruleName + ".rollclear." + i, rankingRollclear[i]);
		}
		for(int i = 0; i < SECTION_MAX; i++) {
			prop.setProperty("grademania2.bestSectionTime." + ruleName + "." + i, bestSectionTime[i]);
		}
	}

	/**
	 * Update rankings
	 * @param gr 段位
	 * @param lv  level
	 * @param time Time
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
	 * @param gr 段位
	 * @param lv  level
	 * @param time Time
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
