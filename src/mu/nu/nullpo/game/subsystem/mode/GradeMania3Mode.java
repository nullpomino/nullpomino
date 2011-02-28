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
import mu.nu.nullpo.game.component.Controller;
import mu.nu.nullpo.game.event.EventReceiver;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.util.CustomProperties;
import mu.nu.nullpo.util.GeneralUtil;

import org.apache.log4j.Logger;

/**
 * GRADE MANIA 3 Mode
 */
public class GradeMania3Mode extends DummyMode {
	/** Log */
	static final Logger log = Logger.getLogger(GradeMania3Mode.class);

	/** Current version */
	private static final int CURRENT_VERSION = 2;

	/** Section COOL criteria Time */
	private static final int[] tableTimeCool =
	{
		3120, 3120, 2940, 2700, 2700, 2520, 2520, 2280, 2280, 0
	};

	/** Section REGRET criteria Time */
	private static final int[] tableTimeRegret =
	{
		5400, 4500, 4500, 4080, 3600, 3600, 3000, 3000, 3000, 3000
	};

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
	private static final int[] tableARE       = {23, 23, 23, 23, 23, 23, 23, 14, 10, 10,  4,  3,  2};

	/** ARE after line clear table */
	private static final int[] tableARELine   = {23, 23, 23, 23, 23, 23, 14, 10,  4,  4,  4,  3,  2};

	/** Line clear time table */
	private static final int[] tableLineDelay = {40, 40, 40, 40, 40, 25, 16, 12,  6,  6,  6,  6,  6};

	/** 固定 time table */
	private static final int[] tableLockDelay = {31, 31, 31, 31, 31, 31, 31, 31, 31, 18, 18, 16, 16};

	/** DAS table */
	private static final int[] tableDAS       = {15, 15, 15, 15, 15,  9,  9,  9,  9,  7,  7,  7,  7};

	/** BGM fadeout level */
	private static final int[] tableBGMFadeout = {485,685,-1};

	/** BGM change level */
	private static final int[] tableBGMChange  = {500,700,-1};

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
		{1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f,1.0f},
		{1.0f,1.2f,1.2f,1.4f,1.4f,1.4f,1.4f,1.5f,1.5f,2.0f},
		{1.0f,1.4f,1.5f,1.6f,1.7f,1.8f,1.9f,2.0f,2.1f,2.5f},
		{1.0f,1.5f,1.8f,2.0f,2.2f,2.3f,2.4f,2.5f,2.6f,3.0f},
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

	///** 段位のcount */
	//private static final int GRADE_MAX = 33;

	/** 段位のName */
	private static final String[] tableGradeName =
	{
		 "9",  "8",  "7",  "6",  "5",  "4",  "3",  "2",  "1",	//  0～ 8
		"S1", "S2", "S3", "S4", "S5", "S6", "S7", "S8", "S9",	//  9～17
		"M1", "M2", "M3", "M4", "M5", "M6", "M7", "M8", "M9",	// 18～26
		 "M", "MK", "MV", "MO", "MM", "GM"						// 27～32
	};

	/** 裏段位のName */
	private static final String[] tableSecretGradeName =
	{
		 "9",  "8",  "7",  "6",  "5",  "4",  "3",  "2",  "1",	//  0～ 8
		"S1", "S2", "S3", "S4", "S5", "S6", "S7", "S8", "S9",	//  9～17
		"GM"													// 18
	};

	/** LV999 roll time */
	private static final int ROLLTIMELIMIT = 3238;

	/** Number of entries in rankings */
	private static final int RANKING_MAX = 10;

	/** Number of ranking types */
	private static final int RANKING_TYPE = 2;

	/** 段位履歴のサイズ */
	private static final int GRADE_HISTORY_SIZE = 7;

	/** 段位認定試験の発生確率(EXAM_CHANCE分の1の確率で発生) */
	private static final int EXAM_CHANCE = 3;

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

	/** 内部 level */
	private int internalLevel;

	/** Internal level at start */
	private int internalStartLevel;

	/** Levelが増えた flag */
	private boolean lvupflag;

	/** 最終結果などに表示される実際の段位 */
	private int grade;

	/** メインの段位 */
	private int gradeBasicReal;

	/** メインの段位の内部段位 */
	private int gradeBasicInternal;

	/** メインの段位の内部段位 point */
	private int gradeBasicPoint;

	/** メインの段位の内部段位 pointが1つ減る time */
	private int gradeBasicDecay;

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

	/** このSection でCOOLを出すとtrue */
	private boolean cool;

	/** COOL count */
	private int coolcount;

	/** 直前のSection でCOOLを出したらtrue */
	private boolean previouscool;

	/** 直前のSection での level70通過Time */
	private int coolprevtime;

	/** このSection でCOOL check をしたらtrue */
	private boolean coolchecked;

	/** このSection でCOOL表示をしたらtrue */
	private boolean cooldisplayed;

	/** COOL display time frame count */
	private int cooldispframe;

	/** REGRET display time frame count */
	private int regretdispframe;

	/** COOL section flags*/
	private boolean[] coolsection;

	/** REGRET section flags*/
	private boolean[] regretsection;

	/** Section time display color-code type */
	private int stcolor;

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

	/** 消えRoll started flag */
	private boolean mrollFlag;

	/** Roll 中に稼いだ point (段位上昇用) */
	private float rollPoints;

	/** Roll 中に稼いだ point (合計) */
	private float rollPointsTotal;

	/** AC medal 状態 */
	private int medalAC;

	/** ST medal 状態 */
	private int medalST;

	/** SK medal 状態 */
	private int medalSK;

	/** CO medal 状態 */
	private int medalCO;

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

	/** 段位表示 */
	private boolean gradedisp;

	/** LV500の足切りTime */
	private int lv500torikan;

	/** 昇格・降格試験 is enabled */
	private boolean enableexam;

	/** Version */
	private int version;

	/** Current round's ranking rank */
	private int rankingRank;

	/** Rankings' 段位 */
	private int[][] rankingGrade;

	/** Rankings' levels */
	private int[][] rankingLevel;

	/** Rankings' times */
	private int[][] rankingTime;

	/** Rankings' Roll completely cleared flag */
	private int[][] rankingRollclear;

	/** Section Time記録 */
	private int[][] bestSectionTime;

	/** 段位履歴 (昇格・降格試験用) */
	private int[] gradeHistory;

	/** 昇格試験の目標段位 */
	private int promotionalExam;

	/** Current 認定段位 */
	private int qualifiedGrade;

	/** 降格試験 point (30以上溜まると降格試験発生) */
	private int demotionPoints;

	/** 昇格試験 flag */
	private boolean promotionFlag;

	/** 降格試験 flag */
	private boolean demotionFlag;

	/** 降格試験での目標段位 */
	private int demotionExamGrade;

	/** 試験開始前演出の frame count */
	private int readyframe;

	/** 試験終了演出の frame count */
	private int passframe;

	/*
	 * Mode name
	 */
	@Override
	public String getName() {
		return "GRADE MANIA 3";
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
		internalLevel = 0;
		internalStartLevel = 0;
		lvupflag = true;
		grade = 0;
		gradeBasicReal = 0;
		gradeBasicInternal = 0;
		gradeBasicPoint = 0;
		gradeBasicDecay = 0;
		lastGradeTime = 0;
		harddropBonus = 0;
		comboValue = 0;
		lastscore = 0;
		scgettime = 0;
		cool = false;
		coolcount = 0;
		previouscool = false;
		coolprevtime = 0;
		coolchecked = false;
		cooldisplayed = false;
		cooldispframe = 0;
		regretdispframe = 0;
		rolltime = 0;
		rollclear = 0;
		rollstarted = false;
		secretGrade = 0;
		bgmlv = 0;
		gradeflash = 0;
		sectiontime = new int[SECTION_MAX];
		sectionIsNewRecord = new boolean[SECTION_MAX];
		regretsection = new boolean[SECTION_MAX];
		coolsection = new boolean[SECTION_MAX];
		sectionscomp = 0;
		sectionavgtime = 0;
		sectionlasttime = 0;
		mrollFlag = false;
		rollPoints = 0f;
		rollPointsTotal = 0f;
		medalAC = 0;
		medalST = 0;
		medalSK = 0;
		medalCO = 0;
		isShowBestSectionTime = false;
		startlevel = 0;
		alwaysghost = false;
		always20g = false;
		lvstopse = false;
		big = false;
		gradedisp = false;
		lv500torikan = 25200;
		enableexam = false;

		promotionalExam = 0;
		qualifiedGrade = 0;
		demotionPoints = 0;
		readyframe = 0;
		passframe = 0;
		gradeHistory = new int[GRADE_HISTORY_SIZE];
		promotionFlag = false;
		demotionFlag = false;
		demotionExamGrade = 0;

		rankingRank = -1;
		rankingGrade = new int[RANKING_MAX][RANKING_TYPE];
		rankingLevel = new int[RANKING_MAX][RANKING_TYPE];
		rankingTime = new int[RANKING_MAX][RANKING_TYPE];
		rankingRollclear = new int[RANKING_MAX][RANKING_TYPE];
		bestSectionTime = new int[SECTION_MAX][RANKING_TYPE];

		engine.tspinEnable = false;
		engine.b2bEnable = false;
		engine.framecolor = GameEngine.FRAME_COLOR_BLUE;
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
			loadSetting(owner.replayProp);
			version = owner.replayProp.getProperty("grademania3.version", 0);

			for(int i = 0; i < SECTION_MAX; i++) {
				bestSectionTime[i][enableexam ? 1 : 0] = DEFAULT_SECTION_TIME;
			}

			if(enableexam) {
				promotionalExam = owner.replayProp.getProperty("grademania3.exam", 0);
				demotionPoints = owner.replayProp.getProperty("grademania3.demopoint", 0);
				demotionExamGrade = owner.replayProp.getProperty("grademania3.demotionExamGrade", 0);
				if (promotionalExam > 0) {
					promotionFlag = true;
					readyframe = 100;
					passframe = 600;
				} else if (demotionPoints >= 30) {
					demotionFlag = true;
					passframe = 600;
				}

				log.debug("** Exam data from replay START **");
				log.debug("Promotional Exam Grade:" + getGradeName(promotionalExam) + " (" + promotionalExam + ")");
				log.debug("Promotional Exam Flag:" + promotionFlag);
				log.debug("Demotion Points:" + demotionPoints);
				log.debug("Demotional Exam Grade:" + getGradeName(demotionExamGrade) + " (" + demotionExamGrade + ")");
				log.debug("Demotional Exam Flag:" + demotionFlag);
				log.debug("*** Exam data from replay END ***");
			}
		}

		owner.backgroundStatus.bg = Math.min(9, startlevel);
	}

	/**
	 * Load settings from property file
	 * @param prop Property file
	 */
	private void loadSetting(CustomProperties prop) {
		startlevel = prop.getProperty("grademania3.startlevel", 0);
		internalStartLevel = prop.getProperty("grademania3.internalLevel", startlevel * 100);
		alwaysghost = prop.getProperty("grademania3.alwaysghost", false);
		always20g = prop.getProperty("grademania3.always20g", false);
		lvstopse = prop.getProperty("grademania3.lvstopse", true);
		showsectiontime = prop.getProperty("grademania3.showsectiontime", false);
		big = prop.getProperty("grademania3.big", false);
		gradedisp = prop.getProperty("grademania3.gradedisp", false);
		lv500torikan = prop.getProperty("grademania3.lv500torikan", 25200);
		enableexam = prop.getProperty("grademania3.enableexam", false);
		stcolor = prop.getProperty("grademania3.stcolor", 1);
	}

	/**
	 * Save settings to property file
	 * @param prop Property file
	 */
	private void saveSetting(CustomProperties prop) {
		prop.setProperty("grademania3.startlevel", startlevel);
		prop.setProperty("grademania3.internalLevel", internalStartLevel);
		prop.setProperty("grademania3.alwaysghost", alwaysghost);
		prop.setProperty("grademania3.always20g", always20g);
		prop.setProperty("grademania3.lvstopse", lvstopse);
		prop.setProperty("grademania3.showsectiontime", showsectiontime);
		prop.setProperty("grademania3.big", big);
		prop.setProperty("grademania3.gradedisp", gradedisp);
		prop.setProperty("grademania3.lv500torikan", lv500torikan);
		prop.setProperty("grademania3.enableexam", enableexam);
		prop.setProperty("grademania3.stcolor", stcolor);
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
			while(internalLevel >= tableGravityChangeLevel[gravityindex]) gravityindex++;
			engine.speed.gravity = tableGravityValue[gravityindex];
		}

		if(engine.statistics.time >= 54000) {
			engine.speed.are = 2;
			engine.speed.areLine = 1;
			engine.speed.lineDelay = 3;
			engine.speed.lockDelay = 13;
			engine.speed.das = 5;
		} else {
			int section = internalLevel / 100;
			if(section > tableARE.length - 1) section = tableARE.length - 1;
			engine.speed.are = tableARE[section];
			engine.speed.areLine = tableARELine[section];
			engine.speed.lineDelay = tableLineDelay[section];
			engine.speed.lockDelay = tableLockDelay[section];
			engine.speed.das = tableDAS[section];
		}
	}

	/**
	 * Update average section time
	 */
	private void setAverageSectionTime() {
		if(sectionscomp > 0 && startlevel < 10) {
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
		int type = enableexam ? 1 : 0;
		int best = bestSectionTime[sectionNumber][type];

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

	/**
	 * COOLの check
	 * @param engine GameEngine
	 */
	private void checkCool(GameEngine engine) {
		// COOL check
		if((engine.statistics.level % 100 >= 70) && (coolchecked == false)) {
			int section = engine.statistics.level / 100;

			if( (sectiontime[section] <= tableTimeCool[section]) &&
				((previouscool == false) || ((previouscool == true) && (sectiontime[section] <= coolprevtime + 120))) )
			{
				cool = true;
				coolsection[section] = true;
			}
			else {
				coolsection[section] = false;
			}
			coolprevtime = sectiontime[section];
			coolchecked = true;
		}

		// COOL表示
		if((engine.statistics.level % 100 >= 82) && (cool == true) && (cooldisplayed == false)) {
			engine.playSE("cool");
			cooldispframe = 180;
			cooldisplayed = true;
		}
	}

	/**
	 * REGRETの check
	 * @param engine GameEngine
	 * @param levelb Line clear前の level
	 */
	private void checkRegret(GameEngine engine, int levelb) {
		int section = levelb / 100;
		if(sectionlasttime > tableTimeRegret[section]) {
			previouscool = false;

			coolcount--;
			if(coolcount < 0) coolcount = 0;

			grade--;
			if(grade < 0) grade = 0;
			gradeflash = 180;

			regretdispframe = 180;
			engine.playSE("regret");
			regretsection[section] = true;
		}
		else {
			regretsection[section] = false;
		}
	}

	/**
	 * @return 何らかの試験中ならtrue
	 */
	private boolean isAnyExam() {
		return promotionFlag || demotionFlag;
	}

	/**
	 * 段位名を取得
	 * @param g 段位 number
	 * @return 段位名(範囲外ならN/A)
	 */
	private String getGradeName(int g) {
		if((g < 0) || (g >= tableGradeName.length)) return "N/A";
		return tableGradeName[g];
	}

	/*
	 * Called at settings screen
	 */
	@Override
	public boolean onSetting(GameEngine engine, int playerID) {
		// Menu
		if(engine.owner.replayMode == false) {
			// Configuration changes
			int change = updateCursor(engine, 10);

			if(change != 0) {
				engine.playSE("change");

				
				switch(engine.statc[2]) {
				case 0:
					startlevel += change;
					if(startlevel < 0) startlevel = 11;
					if(startlevel > 11) startlevel = 0;
					owner.backgroundStatus.bg = Math.min(9, startlevel);
					if (startlevel == 11)
						internalStartLevel = 1200;
					else {
						if (startlevel < 10 && startlevel - change < 10 && internalStartLevel < 1200)
							internalStartLevel += change * 100;
						int minSpeed = Math.min(startlevel, 9) * 100;
						int maxSpeed = Math.min(startlevel << 1, 12) * 100;
						if(internalStartLevel < minSpeed) internalStartLevel = minSpeed;
						if(internalStartLevel > maxSpeed) internalStartLevel = maxSpeed;
					}
					break;
				case 1:
					if (startlevel == 11)
						internalStartLevel = 1200;
					else
					{
						internalStartLevel += change * 100;
						int minSpeed = Math.min(startlevel, 9) * 100;
						int maxSpeed = Math.min(startlevel << 1, 12) * 100;
						if(internalStartLevel < minSpeed) internalStartLevel = maxSpeed;
						if(internalStartLevel > maxSpeed) internalStartLevel = minSpeed;
					}
					break;
				case 2:
					alwaysghost = !alwaysghost;
					break;
				case 3:
					always20g = !always20g;
					break;
				case 4:
					lvstopse = !lvstopse;
					break;
				case 5:
					showsectiontime = !showsectiontime;
					break;
				case 6:
					gradedisp = !gradedisp;
					break;
				case 7:
					if(engine.ctrl.isPress(Controller.BUTTON_E)) lv500torikan += 3600 * change;
					else lv500torikan += 60 * change;
					if(lv500torikan < 0) lv500torikan = 72000;
					if(lv500torikan > 72000) lv500torikan = 0;
					break;
				case 8:
					stcolor += change;
					if(stcolor < 0) stcolor = 2;
					if(stcolor > 2) stcolor = 0;
					break;
				case 9:
					big = !big;
					break;
				case 10:
					enableexam = !enableexam;
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

				Random rand = new Random();
				if(!always20g && !big && enableexam && (rand.nextInt(EXAM_CHANCE) == 0)) {
					setPromotionalGrade();
					if(promotionalExam > qualifiedGrade) {
						promotionFlag = true;
						readyframe = 100;
						passframe = 600;
					} else if(demotionPoints >= 30) {
						demotionFlag = true;
						demotionExamGrade = qualifiedGrade;
						passframe = 600;
						demotionPoints = 0;
					}

					log.debug("** Exam debug log START **");
					log.debug("Current Qualified Grade:" + getGradeName(qualifiedGrade) + " (" + qualifiedGrade + ")");
					log.debug("Promotional Exam Grade:" + getGradeName(promotionalExam) + " (" + promotionalExam + ")");
					log.debug("Promotional Exam Flag:" + promotionFlag);
					log.debug("Demotion Points:" + demotionPoints);
					log.debug("Demotional Exam Grade:" + getGradeName(demotionExamGrade) + " (" + demotionExamGrade + ")");
					log.debug("Demotional Exam Flag:" + demotionFlag);
					log.debug("*** Exam debug log END ***");
				}

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
		String scolorStr = "NONE";
		if (stcolor == 1) scolorStr = "NEWRECORD";
		else if (stcolor == 2) scolorStr = "COOL";
		String level;
		if (startlevel == 10) level = "ROLL";
		else if (startlevel == 11) level = "M-ROLL";
		else level = String.valueOf(startlevel * 100);
		String speedlevel;
		if (internalStartLevel >= 1200) speedlevel = "MAX";
		else speedlevel = String.valueOf(internalStartLevel);
		drawMenu(engine, playerID, receiver, 0, EventReceiver.COLOR_BLUE, 0,
				"LEVEL", level,
				"SPEED", speedlevel,
				"FULL GHOST", GeneralUtil.getONorOFF(alwaysghost),
				"20G MODE", GeneralUtil.getONorOFF(always20g),
				"LVSTOPSE", GeneralUtil.getONorOFF(lvstopse),
				"SHOW STIME", GeneralUtil.getONorOFF(showsectiontime),
				"GRADE DISP", GeneralUtil.getONorOFF(gradedisp),
				"LV500LIMIT", (lv500torikan == 0) ? "NONE" : GeneralUtil.getTime(lv500torikan),
				"STIMECOLOR", scolorStr);
		drawMenuCompact(engine, playerID, receiver,
				"BIG", GeneralUtil.getONorOFF(big),
				"EXAM", GeneralUtil.getONorOFF(enableexam));
	}

	/*
	 * Called at game start
	 */
	@Override
	public void startGame(GameEngine engine, int playerID) {
		engine.statistics.level = Math.min(999, startlevel * 100);

		nextseclv = engine.statistics.level + 100;
		if(engine.statistics.level < 0) nextseclv = 100;
		if(engine.statistics.level >= 900) nextseclv = 999;

		owner.backgroundStatus.bg = Math.min(startlevel, 9);

		engine.big = big;

		setSpeed(engine);
		setStartBgmlv(engine);
		owner.bgmStatus.bgm = bgmlv;
		
		internalLevel = internalStartLevel;

		if (startlevel >= 10)
		{
			// Ending
			engine.timerActive = false;
			engine.ending = 2;
			rollclear = 1;
			mrollFlag = (startlevel == 11);
			rollstarted = true;

			if(mrollFlag) {
				engine.blockHidden = engine.ruleopt.lockflash;
				engine.blockHiddenAnim = false;
				engine.blockShowOutlineOnly = true;
			} else {
				engine.blockHidden = 300;
				engine.blockHiddenAnim = true;
			}

			owner.bgmStatus.bgm = BGMStatus.BGM_ENDING1;
		}
	}

	/*
	 * Render score
	 */
	@Override
	public void renderLast(GameEngine engine, int playerID) {
		receiver.drawScoreFont(engine, playerID, 0, 0, "GRADE MANIA 3" + (enableexam ? "(+EXAM)" : ""), EventReceiver.COLOR_CYAN);

		if( (engine.stat == GameEngine.STAT_SETTING) || ((engine.stat == GameEngine.STAT_RESULT) && (!owner.replayMode)) ) {
			if((startlevel == 0) && (!big) && (!always20g) && (!owner.replayMode) && (engine.ai == null)) {
				if(!isShowBestSectionTime) {
					// Rankings
					float scale = (receiver.getNextDisplayType() == 2) ? 0.5f : 1.0f;
					int topY = (receiver.getNextDisplayType() == 2) ? 5 : 3;
					receiver.drawScoreFont(engine, playerID, 3, topY-1, "GRADE LEVEL TIME", EventReceiver.COLOR_BLUE, scale);

					for(int i = 0; i < RANKING_MAX; i++) {
						int type = enableexam ? 1 : 0;
						int gcolor = EventReceiver.COLOR_WHITE;
						if((rankingRollclear[i][type] == 1) || (rankingRollclear[i][type] == 3)) gcolor = EventReceiver.COLOR_GREEN;
						if((rankingRollclear[i][type] == 2) || (rankingRollclear[i][type] == 4)) gcolor = EventReceiver.COLOR_ORANGE;

						receiver.drawScoreFont(engine, playerID, 0, topY+i, String.format("%2d", i + 1), EventReceiver.COLOR_YELLOW, scale);
						receiver.drawScoreFont(engine, playerID, 3, topY+i, getGradeName(rankingGrade[i][type]), gcolor, scale);
						receiver.drawScoreFont(engine, playerID, 9, topY+i, String.valueOf(rankingLevel[i][type]), (i == rankingRank), scale);
						receiver.drawScoreFont(engine, playerID, 15, topY+i, GeneralUtil.getTime(rankingTime[i][type]), (i == rankingRank), scale);
					}

					if(enableexam) {
						receiver.drawScoreFont(engine, playerID, 0, 14, "QUALIFIED GRADE", EventReceiver.COLOR_YELLOW);
						receiver.drawScoreFont(engine, playerID, 0, 15, getGradeName(qualifiedGrade));
					}

					receiver.drawScoreFont(engine, playerID, 0, 17, "F:VIEW SECTION TIME", EventReceiver.COLOR_GREEN);
				} else {
					// Section Time
					receiver.drawScoreFont(engine, playerID, 0, 2, "SECTION TIME", EventReceiver.COLOR_BLUE);

					int totalTime = 0;
					for(int i = 0; i < SECTION_MAX; i++) {
						int type = enableexam ? 1 : 0;

						int temp = Math.min(i * 100, 999);
						int temp2 = Math.min(((i + 1) * 100) - 1, 999);

						String strSectionTime;
						strSectionTime = String.format("%3d-%3d %s", temp, temp2, GeneralUtil.getTime(bestSectionTime[i][type]));

						receiver.drawScoreFont(engine, playerID, 0, 3 + i, strSectionTime, (sectionIsNewRecord[i] && !isAnyExam()));

						totalTime += bestSectionTime[i][type];
					}

					receiver.drawScoreFont(engine, playerID, 0, 14, "TOTAL", EventReceiver.COLOR_BLUE);
					receiver.drawScoreFont(engine, playerID, 0, 15, GeneralUtil.getTime(totalTime));
					receiver.drawScoreFont(engine, playerID, 9, 14, "AVERAGE", EventReceiver.COLOR_BLUE);
					receiver.drawScoreFont(engine, playerID, 9, 15, GeneralUtil.getTime(totalTime / SECTION_MAX));

					receiver.drawScoreFont(engine, playerID, 0, 17, "F:VIEW RANKING", EventReceiver.COLOR_GREEN);
				}
			}
		} else {
			if(promotionFlag) {
				// 試験段位
				receiver.drawScoreFont(engine, playerID, 0, 5, "QUALIFY", EventReceiver.COLOR_YELLOW);
				receiver.drawScoreFont(engine, playerID, 0, 6, getGradeName(promotionalExam));
			}

			if(gradedisp) {
				// 段位
				int rgrade = grade;
				if(enableexam && (rgrade >= 32) && (qualifiedGrade < 32)) rgrade = 31;

				receiver.drawScoreFont(engine, playerID, 0, 2, "GRADE", EventReceiver.COLOR_BLUE);
				receiver.drawScoreFont(engine, playerID, 0, 3, getGradeName(rgrade), ((gradeflash > 0) && (gradeflash % 4 == 0)));

				if(!promotionFlag) {
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

			// Roll 残り time
			if((engine.gameActive) && (engine.ending == 2)) {
				int time = ROLLTIMELIMIT - rolltime;
				if(time < 0) time = 0;
				receiver.drawScoreFont(engine, playerID, 0, 17, "ROLL TIME", EventReceiver.COLOR_BLUE);
				receiver.drawScoreFont(engine, playerID, 0, 18, GeneralUtil.getTime(time), ((time > 0) && (time < 10 * 60)));
			}

			if(regretdispframe > 0) {
				// REGRET表示
				receiver.drawMenuFont(engine,playerID,2,21,"REGRET",(regretdispframe % 4 == 0),EventReceiver.COLOR_WHITE,EventReceiver.COLOR_ORANGE);
			} else if(cooldispframe > 0) {
				// COOL表示
				receiver.drawMenuFont(engine,playerID,2,21,"COOL!!",(cooldispframe % 4 == 0),EventReceiver.COLOR_WHITE,EventReceiver.COLOR_ORANGE);
			}

			//  medal
			if(medalAC >= 1) receiver.drawScoreFont(engine, playerID, 0, 20, "AC", getMedalFontColor(medalAC));
			if(medalST >= 1) receiver.drawScoreFont(engine, playerID, 3, 20, "ST", getMedalFontColor(medalST));
			if(medalSK >= 1) receiver.drawScoreFont(engine, playerID, 0, 21, "SK", getMedalFontColor(medalSK));
			if(medalCO >= 1) receiver.drawScoreFont(engine, playerID, 3, 21, "CO", getMedalFontColor(medalCO));

			// Section Time
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

						int color = EventReceiver.COLOR_WHITE;
						if((i == section) && (engine.ending == 0)) {
							strSeparator = "b";
						} else {
							if (stcolor == 1 && sectionIsNewRecord[i]) {
								color = EventReceiver.COLOR_RED;
							} else if (stcolor == 2) {
								if (regretsection[i]) color = EventReceiver.COLOR_RED;
								else if (coolsection[i])  color = EventReceiver.COLOR_GREEN;
							}
						}

						String strSectionTime;
						strSectionTime = String.format("%3d%s%s", temp, strSeparator, GeneralUtil.getTime(sectiontime[i]));

						receiver.drawScoreFont(engine, playerID, x, 3 + i, strSectionTime, color);
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
	 * Ready→Goの処理
	 */
	@Override
	public boolean onReady(GameEngine engine, int playerID) {
		if(promotionFlag) {
			engine.framecolor = GameEngine.FRAME_COLOR_YELLOW;

			if(readyframe == 100) engine.playSE("tspin3");

			if(engine.ctrl.isPush(Controller.BUTTON_A) || engine.ctrl.isPush(Controller.BUTTON_B))
				readyframe = 0;

			if(readyframe > 0) {
				readyframe--;
				return true;
			}
		} else if(demotionFlag) {
			engine.framecolor = GameEngine.FRAME_COLOR_GRAY;
			engine.playSE("danger");
		}

		return false;
	}

	/*
	 * Ready→Goのときの描画
	 */
	@Override
	public void renderReady(GameEngine engine, int playerID) {
		if(promotionFlag && readyframe > 0) {
			receiver.drawMenuFont(engine, playerID, 0, 2, "PROMOTION", EventReceiver.COLOR_YELLOW);
			receiver.drawMenuFont(engine, playerID, 6, 3, "EXAM", EventReceiver.COLOR_YELLOW);
			receiver.drawMenuFont(engine, playerID, 4, 6, getGradeName(promotionalExam), (readyframe % 4 ==0),
				EventReceiver.COLOR_WHITE, EventReceiver.COLOR_ORANGE);
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
				internalLevel++;
				if((engine.statistics.level == nextseclv - 1) && (lvstopse == true)) engine.playSE("levelstop");
			}
			levelUp(engine);

			// Hard drop bonusInitialization
			harddropBonus = 0;
		}
		if( (engine.ending == 0) && (engine.statc[0] > 0) && ((version >= 1) || (engine.holdDisable == false)) ) {
			lvupflag = false;
		}

		// 段位 point減少
		if((engine.timerActive == true) && (gradeBasicPoint > 0) && (engine.combo <= 0) && (engine.lockDelayNow < engine.getLockDelay() - 1)) {
			gradeBasicDecay++;

			int index = gradeBasicInternal;
			if(index > tableGradeDecayRate.length - 1) index = tableGradeDecayRate.length - 1;

			if(gradeBasicDecay >= tableGradeDecayRate[index]) {
				gradeBasicDecay = 0;
				gradeBasicPoint--;
			}
		}

		// Endingスタート
		if((engine.ending == 2) && (rollstarted == false)) {
			rollstarted = true;

			if(mrollFlag) {
				engine.blockHidden = engine.ruleopt.lockflash;
				engine.blockHiddenAnim = false;
				engine.blockShowOutlineOnly = true;
			} else {
				engine.blockHidden = 300;
				engine.blockHiddenAnim = true;
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
				internalLevel++;
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

		// COOL check
		checkCool(engine);

		// 速度変更
		setSpeed(engine);

		// LV100到達でghost を消す
		if((engine.statistics.level >= 100) && (!alwaysghost)) engine.ghost = false;

		// BGM fadeout
		int tempLevel = internalLevel;
		if(cool) tempLevel += 100;

		if((tableBGMFadeout[bgmlv] != -1) && (tempLevel >= tableBGMFadeout[bgmlv])) {
			owner.bgmStatus.fadesw  = true;
		}

		// BGM切り替え
		if((tableBGMChange[bgmlv] != -1) && (internalLevel >= tableBGMChange[bgmlv])) {
			bgmlv++;
			owner.bgmStatus.fadesw = false;
			owner.bgmStatus.bgm = bgmlv;
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

		if((lines >= 1) && (engine.ending == 0)) {
			// 段位 point
			int index = gradeBasicInternal;
			if(index > 10) index = 10;
			int basepoint = tableGradePoint[lines - 1][index];

			int indexcombo = engine.combo - 1;
			if(indexcombo < 0) indexcombo = 0;
			if(indexcombo > tableGradeComboBonus[lines - 1].length - 1) indexcombo = tableGradeComboBonus[lines - 1].length - 1;
			float combobonus = tableGradeComboBonus[lines - 1][indexcombo];

			int levelbonus = 1 + (engine.statistics.level / 250);

			float point = (basepoint * combobonus) * levelbonus;
			gradeBasicPoint += (int)point;

			// 内部段位上昇
			if(gradeBasicPoint >= 100) {
				gradeBasicPoint = 0;
				gradeBasicDecay = 0;
				gradeBasicInternal++;

				if((tableGradeChange[gradeBasicReal] != -1) && (gradeBasicInternal >= tableGradeChange[gradeBasicReal])) {
					if(gradedisp) engine.playSE("gradeup");
					gradeBasicReal++;
					grade++;
					if(grade > 31) grade = 31;
					gradeflash = 180;
					lastGradeTime = engine.statistics.time;
				}
			}

			// 4-line clearカウント
			if(lines >= 4) {
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

			int levelplus = lines;
			if(lines == 3) levelplus = 4;
			if(lines >= 4) levelplus = 6;

			engine.statistics.level += levelplus;
			internalLevel += levelplus;

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

				// ST medal
				stMedalCheck(engine, levelb / 100);

				// REGRET判定
				checkRegret(engine, levelb);

				// 条件を全て満たしているなら消えRoll 発動
				if(version >= 2) {
					if((grade >= 24) && (coolcount >= 9)) mrollFlag = true;
				} else {
					if((grade >= 15) && (coolcount >= 9)) mrollFlag = true;
				}
			} else if((nextseclv == 500) && (engine.statistics.level >= 500) && (lv500torikan > 0) && (engine.statistics.time > lv500torikan) &&
					  (!promotionFlag) && (!demotionFlag)) {
				//  level500とりカン
				engine.statistics.level = 999;
				engine.gameEnded();
				engine.staffrollEnable = false;
				engine.ending = 1;

				secretGrade = engine.field.getSecretGrade();

				// Section Timeを記録
				sectionlasttime = sectiontime[levelb / 100];
				sectionscomp++;
				setAverageSectionTime();

				// ST medal
				stMedalCheck(engine, levelb / 100);

				// REGRET判定
				checkRegret(engine, levelb);
			} else if(engine.statistics.level >= nextseclv) {
				// Next Section
				engine.playSE("levelup");

				// Background切り替え
				owner.backgroundStatus.fadesw = true;
				owner.backgroundStatus.fadecount = 0;
				owner.backgroundStatus.fadebg = nextseclv / 100;

				// BGM切り替え
				if((tableBGMChange[bgmlv] != -1) && (internalLevel >= tableBGMChange[bgmlv])) {
					bgmlv++;
					owner.bgmStatus.fadesw = false;
					owner.bgmStatus.bgm = bgmlv;
				}

				// Section Timeを記録
				sectionlasttime = sectiontime[levelb / 100];
				sectionscomp++;
				setAverageSectionTime();

				// ST medal
				stMedalCheck(engine, levelb / 100);

				// REGRET判定
				checkRegret(engine, levelb);

				// COOLを取ってたら
				if(cool == true) {
					previouscool = true;

					coolcount++;
					grade++;
					if(grade > 31) grade = 31;
					gradeflash = 180;

					if(gradedisp) engine.playSE("gradeup");

					internalLevel += 100;
				} else {
					previouscool = false;
				}

				cool = false;
				coolchecked = false;
				cooldisplayed = false;

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
			if(engine.field.isEmpty()) bravo = 2;

			int speedBonus = engine.getLockDelay() - engine.statc[0];
			if(speedBonus < 0) speedBonus = 0;

			lastscore = ( ((levelb + lines) / 4 + engine.softdropFall + manuallock + harddropBonus) * lines * comboValue + speedBonus +
						(engine.statistics.level / 2) ) * bravo;

			engine.statistics.score += lastscore;
			scgettime = 120;
		} else if((lines >= 1) && (engine.ending == 2)) {
			// Roll 中のLine clear
			float points = 0f;
			if(mrollFlag == false) {
				if(lines == 1) points = 0.04f;
				if(lines == 2) points = 0.08f;
				if(lines == 3) points = 0.12f;
				if(lines == 4) points = 0.26f;
			} else {
				if(lines == 1) points = 0.1f;
				if(lines == 2) points = 0.2f;
				if(lines == 3) points = 0.3f;
				if(lines == 4) points = 1.0f;
			}
			rollPoints += points;
			rollPointsTotal += points;

			while((rollPoints >= 1.0f) && (grade < 31)) {
				rollPoints -= 1.0f;
				grade++;
				gradeflash = 180;
				if(gradedisp) engine.playSE("gradeup");
			}
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

		// REGRET表示
		if(regretdispframe > 0) regretdispframe--;

		// COOL表示
		if(cooldispframe > 0) cooldispframe--;

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

				secretGrade = engine.field.getSecretGrade();

				if(mrollFlag == false) {
					rollPoints += 0.5f;
					rollPointsTotal += 0.5f;
				} else {
					rollPoints += 1.6f;
					rollPointsTotal += 1.6f;
				}

				while(rollPoints >= 1.0f) {
					rollPoints -= 1.0f;
					grade++;
					if(grade > 32) grade = 32;
					gradeflash = 180;
					if(gradedisp) engine.playSE("gradeup");
				}

				engine.blockOutlineType = GameEngine.BLOCK_OUTLINE_NORMAL;
				engine.blockShowOutlineOnly = false;

				engine.gameEnded();
				engine.resetStatc();
				engine.stat = GameEngine.STAT_EXCELLENT;
				owner.bgmStatus.bgm = BGMStatus.BGM_NOTHING;
			}
		}
	}

	/*
	 * game over
	 */
	@Override
	public boolean onGameOver(GameEngine engine, int playerID) {
		// This code block will executed only once
		if(engine.statc[0] == 0) {
			secretGrade = engine.field.getSecretGrade();

			if(enableexam) {
				if(grade < (qualifiedGrade - 7)) {
					demotionPoints += (qualifiedGrade - grade - 7);
				}
				if(promotionFlag && grade >= promotionalExam) {
					qualifiedGrade = promotionalExam;
					demotionPoints = 0;
				}
				if(demotionFlag && grade < demotionExamGrade) {
					qualifiedGrade = demotionExamGrade - 1;
					if(qualifiedGrade < 0) qualifiedGrade = 0;
				}

				log.debug("** Exam result log START **");
				log.debug("Current Qualified Grade:" + getGradeName(qualifiedGrade) + " (" + qualifiedGrade + ")");
				log.debug("Promotional Exam Grade:" + getGradeName(promotionalExam) + " (" + promotionalExam + ")");
				log.debug("Promotional Exam Flag:" + promotionFlag);
				log.debug("Demotion Points:" + demotionPoints);
				log.debug("Demotional Exam Grade:" + getGradeName(demotionExamGrade) + " (" + demotionExamGrade + ")");
				log.debug("Demotional Exam Flag:" + demotionFlag);
				log.debug("*** Exam result log END ***");
			}
		}

		return false;
	}

	/*
	 * 結果画面
	 */
	@Override
	public void renderResult(GameEngine engine, int playerID) {
		if(passframe > 0) {
			if (promotionFlag) {
				receiver.drawMenuFont(engine, playerID, 0, 2, "PROMOTION", EventReceiver.COLOR_YELLOW);
				receiver.drawMenuFont(engine, playerID, 6, 3, "EXAM", EventReceiver.COLOR_YELLOW);
				receiver.drawMenuFont(engine, playerID, 4, 6, getGradeName(promotionalExam), (passframe % 4 == 0),
						EventReceiver.COLOR_WHITE, EventReceiver.COLOR_ORANGE);

				if(passframe < 420) {
					if(grade < promotionalExam) {
						receiver.drawMenuFont(engine,playerID,3,11,"FAIL",(passframe % 4 == 0),EventReceiver.COLOR_WHITE,EventReceiver.COLOR_RED);
					} else {
						receiver.drawMenuFont(engine,playerID,2,11,"PASS!!",(passframe % 4 == 0),EventReceiver.COLOR_ORANGE,EventReceiver.COLOR_YELLOW);
					}
				}
			} else if (demotionFlag) {
				receiver.drawMenuFont(engine, playerID, 0, 2, "DEMOTION", EventReceiver.COLOR_RED);
				receiver.drawMenuFont(engine, playerID, 6, 3, "EXAM", EventReceiver.COLOR_RED);

				if(passframe < 420) {
					if(grade < demotionExamGrade) {
						receiver.drawMenuFont(engine,playerID,3,11,"FAIL",(passframe % 4 == 0),EventReceiver.COLOR_WHITE,EventReceiver.COLOR_RED);
					} else {
						receiver.drawMenuFont(engine,playerID,3,11,"PASS",(passframe % 4 == 0),EventReceiver.COLOR_WHITE,EventReceiver.COLOR_YELLOW);
					}
				}
			}
		} else {
			receiver.drawMenuFont(engine, playerID, 0, 0, "kn PAGE" + (engine.statc[1] + 1) + "/3", EventReceiver.COLOR_RED);

			if(engine.statc[1] == 0) {
				int rgrade = grade;
				if(enableexam && (rgrade >= 32) && (qualifiedGrade < 32)) rgrade = 31;
				int gcolor = EventReceiver.COLOR_WHITE;
				if((rollclear == 1) || (rollclear == 3)) gcolor = EventReceiver.COLOR_GREEN;
				if((rollclear == 2) || (rollclear == 4)) gcolor = EventReceiver.COLOR_ORANGE;
				if((grade >= 32) && (engine.statc[2] % 2 == 0)) gcolor = EventReceiver.COLOR_YELLOW;
				receiver.drawMenuFont(engine, playerID, 0, 2, "GRADE", EventReceiver.COLOR_BLUE);
				String strGrade = String.format("%10s", getGradeName(rgrade));
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
						int color = EventReceiver.COLOR_WHITE;
						if (stcolor == 1 && sectionIsNewRecord[i]) {
							color = EventReceiver.COLOR_RED;
						} else if (stcolor == 2) {
							if (regretsection[i]) color = EventReceiver.COLOR_RED;
							else if (coolsection[i])  color = EventReceiver.COLOR_GREEN;
						}
						receiver.drawMenuFont(engine, playerID, 2, 3 + i, GeneralUtil.getTime(sectiontime[i]), color);
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
				if(medalCO >= 1) receiver.drawMenuFont(engine, playerID, 8, 4, "CO", getMedalFontColor(medalCO));

				if(rollPointsTotal > 0) {
					receiver.drawMenuFont(engine, playerID, 0, 6, "ROLL POINT", EventReceiver.COLOR_BLUE);
					String strRollPointsTotal = String.format("%10g", rollPointsTotal);
					receiver.drawMenuFont(engine, playerID, 0, 7, strRollPointsTotal);
				}

				drawResultStats(engine, playerID, receiver, 8, EventReceiver.COLOR_BLUE,
						STAT_LPM, STAT_SPM, STAT_PIECE, STAT_PPS);
			}
		}
	}

	/*
	 * 結果画面の処理
	 */
	@Override
	public boolean onResult(GameEngine engine, int playerID) {
		if(passframe > 0) {
			engine.allowTextRenderByReceiver = false; // Turn off RETRY/END menu

			if(engine.ctrl.isPush(Controller.BUTTON_A) || engine.ctrl.isPush(Controller.BUTTON_B)) {
				if(passframe > 420)
					passframe = 420;
				else if(passframe < 300)
					passframe = 0;
			}

			if(promotionFlag) {
				if(passframe == 420) {
					if(grade >= promotionalExam) {
						engine.playSE("excellent");
					} else {
						engine.playSE("regret");
					}
				}
			} else if(demotionFlag) {
				if(passframe == 420) {
					if(grade >= qualifiedGrade) {
						engine.playSE("gradeup");
					} else {
						engine.playSE("gameover");
					}
				}
			}

			passframe--;
			return true;
		}

		engine.allowTextRenderByReceiver = true;

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

		engine.statc[2]++;

		return false;
	}

	/*
	 * リプレイ保存
	 */
	@Override
	public void saveReplay(GameEngine engine, int playerID, CustomProperties prop) {
		saveSetting(owner.replayProp);
		owner.replayProp.setProperty("result.grade.name", getGradeName(grade));
		owner.replayProp.setProperty("result.grade.number", grade);
		owner.replayProp.setProperty("grademania3.version", version);
		owner.replayProp.setProperty("grademania3.exam", (promotionFlag ? promotionalExam : 0));
		owner.replayProp.setProperty("grademania3.demopoint", demotionPoints);
		owner.replayProp.setProperty("grademania3.demotionExamGrade", demotionExamGrade);

		// Update rankings
		if((owner.replayMode == false) && (startlevel == 0) && (always20g == false) && (big == false) && (engine.ai == null)) {
			int rgrade = grade;
			if(enableexam && (rgrade >= 32) && (qualifiedGrade < 32)) {
				rgrade = 31;
			}
			if(!enableexam || !isAnyExam()) {
				updateRanking(rgrade, engine.statistics.level, lastGradeTime, rollclear, enableexam ? 1 : 0);
			} else {
				rankingRank = -1;
			}

			if(enableexam) updateGradeHistory(grade);

			if((medalST == 3) && !isAnyExam()) updateBestSectionTime();

			if((rankingRank != -1) || (enableexam) || (medalST == 3)) {
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
			for(int j = 0; j < RANKING_TYPE; j++) {
				if(j == 0) {
					rankingGrade[i][j] = prop.getProperty("grademania3.ranking." + ruleName + ".grade." + i, 0);
					rankingLevel[i][j] = prop.getProperty("grademania3.ranking." + ruleName + ".level." + i, 0);
					rankingTime[i][j] = prop.getProperty("grademania3.ranking." + ruleName + ".time." + i, 0);
					rankingRollclear[i][j] = prop.getProperty("grademania3.ranking." + ruleName + ".rollclear." + i, 0);
				} else {
					rankingGrade[i][j] = prop.getProperty("grademania3.ranking.exam." + ruleName + ".grade." + i, 0);
					rankingLevel[i][j] = prop.getProperty("grademania3.ranking.exam." + ruleName + ".level." + i, 0);
					rankingTime[i][j] = prop.getProperty("grademania3.ranking.exam." + ruleName + ".time." + i, 0);
					rankingRollclear[i][j] = prop.getProperty("grademania3.ranking.exam." + ruleName + ".rollclear." + i, 0);
				}
			}
		}

		for(int i = 0; i < SECTION_MAX; i++) {
			for(int j = 0; j < RANKING_TYPE; j++) {
				bestSectionTime[i][j] = prop.getProperty("grademania3.bestSectionTime." + j + "." + ruleName + "." + i, DEFAULT_SECTION_TIME);
			}
		}

		for(int i = 0; i < GRADE_HISTORY_SIZE; i++) {
			gradeHistory[i] = prop.getProperty("grademania3.gradehistory." + ruleName + "." + i, -1);
		}
		qualifiedGrade = prop.getProperty("grademania3.qualified." + ruleName, 0);
		demotionPoints = prop.getProperty("grademania3.demopoint." + ruleName, 0);
	}

	/**
	 * Save rankings to property file
	 * @param prop Property file
	 * @param ruleName Rule name
	 */
	private void saveRanking(CustomProperties prop, String ruleName) {
		for(int i = 0; i < RANKING_MAX; i++) {
			for(int j = 0; j < RANKING_TYPE; j++) {
				if(j == 0) {
					prop.setProperty("grademania3.ranking." + ruleName + ".grade." + i, rankingGrade[i][j]);
					prop.setProperty("grademania3.ranking." + ruleName + ".level." + i, rankingLevel[i][j]);
					prop.setProperty("grademania3.ranking." + ruleName + ".time." + i, rankingTime[i][j]);
					prop.setProperty("grademania3.ranking." + ruleName + ".rollclear." + i, rankingRollclear[i][j]);
				} else {
					prop.setProperty("grademania3.ranking.exam." + ruleName + ".grade." + i, rankingGrade[i][j]);
					prop.setProperty("grademania3.ranking.exam." + ruleName + ".level." + i, rankingLevel[i][j]);
					prop.setProperty("grademania3.ranking.exam." + ruleName + ".time." + i, rankingTime[i][j]);
					prop.setProperty("grademania3.ranking.exam." + ruleName + ".rollclear." + i, rankingRollclear[i][j]);
				}
			}
		}

		for(int i = 0; i < SECTION_MAX; i++) {
			for(int j = 0; j < RANKING_TYPE; j++) {
				prop.setProperty("grademania3.bestSectionTime." + j + "." + ruleName + "." + i, bestSectionTime[i][j]);
			}
		}

		for(int i = 0; i < GRADE_HISTORY_SIZE; i++) {
			prop.setProperty("grademania3.gradehistory." + ruleName+ "." + i, gradeHistory[i]);
		}
		prop.setProperty("grademania3.qualified." + ruleName, qualifiedGrade);
		prop.setProperty("grademania3.demopoint." + ruleName, demotionPoints);
	}

	/**
	 * Update rankings
	 * @param gr 段位
	 * @param lv  level
	 * @param time Time
	 */
	private void updateRanking(int gr, int lv, int time, int clear, int type) {
		rankingRank = checkRanking(gr, lv, time, clear, type);

		if(rankingRank != -1) {
			// Shift down ranking entries
			for(int i = RANKING_MAX - 1; i > rankingRank; i--) {
				rankingGrade[i][type] = rankingGrade[i - 1][type];
				rankingLevel[i][type] = rankingLevel[i - 1][type];
				rankingTime[i][type] = rankingTime[i - 1][type];
				rankingRollclear[i][type] = rankingRollclear[i - 1][type];
			}

			// Add new data
			rankingGrade[rankingRank][type] = gr;
			rankingLevel[rankingRank][type] = lv;
			rankingTime[rankingRank][type] = time;
			rankingRollclear[rankingRank][type] = clear;
		}
	}

	/**
	 * Calculate ranking position
	 * @param gr 段位
	 * @param lv  level
	 * @param time Time
	 * @return Position (-1 if unranked)
	 */
	private int checkRanking(int gr, int lv, int time, int clear, int type) {
		for(int i = 0; i < RANKING_MAX; i++) {
			if(gr > rankingGrade[i][type]) {
				return i;
			} else if((gr == rankingGrade[i][type]) && (clear > rankingRollclear[i][type])) {
				return i;
			} else if((gr == rankingGrade[i][type]) && (clear == rankingRollclear[i][type]) && (lv > rankingLevel[i][type])) {
				return i;
			} else if((gr == rankingGrade[i][type]) && (clear == rankingRollclear[i][type]) && (lv == rankingLevel[i][type]) &&
					  (time < rankingTime[i][type]))
			{
				return i;
			}
		}

		return -1;
	}

	/**
	 * 段位履歴を更新
	 * @param gr 段位
	 */
	private void updateGradeHistory(int gr) {
		for(int i = GRADE_HISTORY_SIZE - 1; i > 0; i--) {
			gradeHistory[i] = gradeHistory[i - 1];
		}
		gradeHistory[0] = gr;

		// Debug log
		log.debug("** Exam grade history START **");
		for(int i = 0; i < gradeHistory.length; i++) {
			log.debug(i + ": " + getGradeName(gradeHistory[i]) + " (" + gradeHistory[i] + ")");
		}
		log.debug("*** Exam grade history END ***");
	}

	/**
	 * 昇格試験の目標段位を設定
	 * @author Zircean
	 */
	private void setPromotionalGrade() {
		int gradesOver;

		for(int i = tableGradeName.length - 1; i >= 0; i--) {
			gradesOver = 0;
			for(int j = 0; j < GRADE_HISTORY_SIZE; j++) {
				if(gradeHistory[j] == -1) {
					promotionalExam = 0;
					return;
				} else {
					if(gradeHistory[j] >= i) {
						gradesOver++;
					}
				}
			}
			if(gradesOver > 3) {
				promotionalExam = i;
				if(qualifiedGrade < 31 && promotionalExam == 32) {
					promotionalExam = 31;
				}
				return;
			}
		}
	}

	/**
	 * Update best section time records
	 */
	private void updateBestSectionTime() {
		for(int i = 0; i < SECTION_MAX; i++) {
			if(sectionIsNewRecord[i]) {
				int type = enableexam ? 1 : 0;
				bestSectionTime[i][type] = sectiontime[i];
			}
		}
	}
}
