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
import mu.nu.nullpo.util.CustomProperties;
import mu.nu.nullpo.util.GeneralUtil;

/**
 * SCORE ATTACK mode (Original from NullpoUE build 121909 by Zircean)
 */
public class ScoreAttackMode extends AbstractMode {
	/** Current version */
	private static final int CURRENT_VERSION = 0;

	/** Gravity table (Gravity speed value) */
	private static final int[] tableGravityValue =
	{
		4, 5, 6, 8, 10, 12, 16, 32, 48, 64, 4, 5, 6, 8, 12, 32, 48, 80, 112, 128, 144, 16, 48, 80, 112, 144, 176, 192, 208, 224, 240, -1
	};

	/** Gravity table (Gravity change level) */
	private static final int[] tableGravityChangeLevel =
	{
		8, 19, 35, 40, 50, 60, 70, 80, 90, 100, 108, 119, 125, 131, 139, 149, 146, 164, 174, 180, 200, 212, 221, 232, 244, 256, 267, 277, 287, 295, 300, 10000
	};

	/** Ending time limit */
	private static final int ROLLTIMELIMIT = 1956;

	/** Number of hiscore records */
	private static final int RANKING_MAX = 10;

	/** Secret grade names */
	private static final String[] tableSecretGradeName =
	{
		"S1", "S2", "S3", "S4", "S5", "S6", "S7", "S8", "S9",	//  0 -  8
		"M1", "M2", "M3", "M4", "M5", "M6", "M7", "M8", "M9",	//  9 - 17
		"GM"													// 18
	};

	/** Number of sections */
	private static final int SECTION_MAX = 3;

	/** Default section time */
	private static final int DEFAULT_SECTION_TIME = 6000;

	/** GameManager object (Manages entire game status) */

	/** EventReceiver object (This receives many game events, can also be used for drawing the fonts.) */

	/** Current gravity index number (Increases when the level reaches to certain value that defined in tableGravityChangeLevel) */
	private int gravityindex;

	/** Next section level */
	private int nextseclv;

	/** Level up flag (Set to true when the level increases) */
	private boolean lvupflag;

	/** Used by Hard-drop scoring */
	private int harddropBonus;

	/** Used by combo scoring */
	private int comboValue;

	/** Amount of points you just get from line clears */
	private int lastscore;

	/** Elapsed time from last line clear (lastscore is displayed to screen until this reaches to 120) */
	private int scgettime;

	/** Remaining ending time limit */
	private int rolltime;

	/** Secret Grade */
	private int secretGrade;

	/** Current BGM number */
	private int bgmlv;

	/** Section Time */
	private int[] sectiontime;

	/** This will be true if the player achieves new section time record in specific section */
	private boolean[] sectionIsNewRecord;

	/** This will be true if the player achieves new section time record somewhere */
	private boolean sectionAnyNewRecord;

	/** Amount of sections completed */
	private int sectionscomp;

	/** Average section time */
	private int sectionavgtime;

	/** false:Leaderboard, true:Section time record (Push F in settings screen to flip it) */
	private boolean isShowBestSectionTime;

	/** Selected start level */
	private int startlevel;

	/** Always show ghost */
	private boolean alwaysghost;

	/** Always 20G */
	private boolean always20g;

	/** Big Mode */
	private boolean big;

	/** Show section time */
	private boolean showsectiontime;

	/** Version of this mode */
	private int version;

	/** Your place on leaderboard (-1: out of rank) */
	private int rankingRank;

	/** Score records */
	private int[] rankingScore;

	/** Level records */
	private int[] rankingLevel;

	/** Time records */
	private int[] rankingTime;

	/** Best section time records */
	private int[] bestSectionTime;

	/**
	 * Returns the name of this mode
	 */
	@Override
	public String getName() {
		return "SCORE ATTACK";
	}

	/**
	 * This function will be called when the game enters the main game screen.
	 */
	@Override
	public void playerInit(GameEngine engine, int playerID) {
		owner = engine.owner;
		receiver = engine.owner.receiver;

		gravityindex = 0;
		nextseclv = 0;
		lvupflag = true;
		comboValue = 0;
		lastscore = 0;
		scgettime = 0;
		rolltime = 0;
		bgmlv = 0;
		sectiontime = new int[SECTION_MAX];
		sectionIsNewRecord = new boolean[SECTION_MAX];
		sectionAnyNewRecord = false;
		sectionscomp = 0;
		sectionavgtime = 0;
		isShowBestSectionTime = false;
		startlevel = 0;
		alwaysghost = false;
		always20g = false;
		big = false;
		showsectiontime = true;

		rankingRank = -1;
		rankingScore = new int[RANKING_MAX];
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
			version = owner.replayProp.getProperty("scoreattack.version", 0);
		}

		owner.backgroundStatus.bg = startlevel;
	}

	/**
	 * Load the settings
	 */
	protected void loadSetting(CustomProperties prop) {
		startlevel = prop.getProperty("scoreattack.startlevel", 0);
		alwaysghost = prop.getProperty("scoreattack.alwaysghost", false);
		always20g = prop.getProperty("scoreattack.always20g", false);
		showsectiontime = prop.getProperty("scoreattack.showsectiontime", false);
		big = prop.getProperty("scoreattack.big", false);
		version = prop.getProperty("scoreattack.version", 0);
	}

	/**
	 * Save the settings
	 */
	protected void saveSetting(CustomProperties prop) {
		prop.setProperty("scoreattack.startlevel", startlevel);
		prop.setProperty("scoreattack.alwaysghost", alwaysghost);
		prop.setProperty("scoreattack.always20g", always20g);
		prop.setProperty("scoreattack.showsectiontime", showsectiontime);
		prop.setProperty("scoreattack.big", big);
		prop.setProperty("scoreattack.version", version);
	}

	/**
	 * Set the gravity speed
	 * @param engine GameEngine object
	 */
	private void setSpeed(GameEngine engine) {
		if(always20g == true) {
			engine.speed.gravity = -1;
		} else {
			while(engine.statistics.level >= tableGravityChangeLevel[gravityindex]) gravityindex++;
			engine.speed.gravity = tableGravityValue[gravityindex];
		}
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
	 * Best section time update check routine
	 * @param sectionNumber Section Number
	 */
	private void stNewRecordCheck(int sectionNumber) {
		if((sectiontime[sectionNumber] < bestSectionTime[sectionNumber]) && (!owner.replayMode)) {
			sectionIsNewRecord[sectionNumber] = true;
			sectionAnyNewRecord = true;
		}
	}

	/**
	 * Main routine for game setup screen
	 */
	@Override
	public boolean onSetting(GameEngine engine, int playerID) {
		if(engine.owner.replayMode == false) {
			// Configuration changes
			int change = updateCursor(engine, 4);
			if(change != 0) {
				receiver.playSE("change");

				switch(menuCursor) {
				case 0:
					startlevel += change;
					if(startlevel < 0) startlevel = 2;
					if(startlevel > 2) startlevel = 0;
					owner.backgroundStatus.bg = startlevel;
					break;
				case 1:
					alwaysghost = !alwaysghost;
					break;
				case 2:
					always20g = !always20g;
					break;
				case 3:
					showsectiontime = !showsectiontime;
					break;
				case 4:
					big = !big;
					break;
				}
			}

			// Check for F button, when pressed this will flip Leaderboard/Best Section Time Records
			if(engine.ctrl.isPush(Controller.BUTTON_F) && (menuTime >= 5)) {
				engine.playSE("change");
				isShowBestSectionTime = !isShowBestSectionTime;
			}

			// Check for A button, when pressed this will begin the game
			if(engine.ctrl.isPush(Controller.BUTTON_A) && (menuTime >= 5)) {
				receiver.playSE("decide");
				saveSetting(owner.modeConfig);
				receiver.saveModeConfig(owner.modeConfig);
				isShowBestSectionTime = false;
				sectionscomp = 0;
				return false;
			}

			// Check for B button, when pressed this will shutdown the game engine.
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

	/**
	 * Renders game setup screen
	 */
	@Override
	public void renderSetting(GameEngine engine, int playerID) {
		drawMenu(engine, playerID, receiver, 0, EventReceiver.COLOR_BLUE, 0,
				"LEVEL", String.valueOf(startlevel * 100),
				"FULL GHOST", GeneralUtil.getONorOFF(alwaysghost),
				"20G MODE", GeneralUtil.getONorOFF(always20g),
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

		if(engine.statistics.level < 500) bgmlv = 0;
		else bgmlv = 1;

		engine.big = big;

		setSpeed(engine);
		owner.bgmStatus.bgm = bgmlv;
	}

	/**
	 * Renders HUD (leaderboard or game statistics)
	 */
	@Override
	public void renderLast(GameEngine engine, int playerID) {
		receiver.drawScoreFont(engine, playerID, 0, 0, "SCORE ATTACK", EventReceiver.COLOR_DARKBLUE);

		if( (engine.stat == GameEngine.Status.SETTING) || ((engine.stat == GameEngine.Status.RESULT) && (owner.replayMode == false)) ) {
			if((owner.replayMode == false) && (startlevel == 0) && (big == false) && (always20g == false) && (engine.ai == null)) {
				if(!isShowBestSectionTime) {
					// Score Leaderboard
					receiver.drawScoreFont(engine, playerID, 3, 2, "SCORE  TIME", EventReceiver.COLOR_BLUE);

					for(int i = 0; i < RANKING_MAX; i++) {
						receiver.drawScoreFont(engine, playerID, 0, 3 + i, String.format("%2d", i + 1), EventReceiver.COLOR_YELLOW);
						receiver.drawScoreFont(engine, playerID, 3, 3 + i, String.valueOf(rankingScore[i]), (i == rankingRank));
						receiver.drawScoreFont(engine, playerID, 10, 3 + i, GeneralUtil.getTime(rankingTime[i]), (i == rankingRank));
					}

					receiver.drawScoreFont(engine, playerID, 0, 17, "F:VIEW SECTION TIME", EventReceiver.COLOR_GREEN);
				} else {
					// Best Section Time Records
					receiver.drawScoreFont(engine, playerID, 0, 2, "SECTION TIME", EventReceiver.COLOR_BLUE);

					int totalTime = 0;
					for(int i = 0; i < SECTION_MAX; i++) {
						int temp = i * 100;
						int temp2 = ((i + 1) * 100) - 1;

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
			String strScore;
			receiver.drawScoreFont(engine, playerID, 0, 5, "SCORE", EventReceiver.COLOR_BLUE);
			if((lastscore == 0) || (scgettime <= 0)) {
				strScore = String.valueOf(engine.statistics.score);
			} else {
				strScore = String.valueOf(engine.statistics.score) + "(+" + String.valueOf(lastscore) + ")";
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

			receiver.drawScoreFont(engine, playerID, 0, 12, "300");

			receiver.drawScoreFont(engine, playerID, 0, 14, "TIME", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 0, 15, GeneralUtil.getTime(engine.statistics.time));

			if((engine.gameActive) && (engine.ending == 2)) {
				int time = ROLLTIMELIMIT - rolltime;
				if(time < 0) time = 0;
				receiver.drawScoreFont(engine, playerID, 0, 17, "ROLL TIME", EventReceiver.COLOR_BLUE);
				receiver.drawScoreFont(engine, playerID, 0, 18, GeneralUtil.getTime(time), ((time > 0) && (time < 10 * 60)));
			}

			// Section time
			if((showsectiontime == true) && (sectiontime != null)) {
				int x = (receiver.getNextDisplayType() == 2) ? 8 : 12;
				int x2 = (receiver.getNextDisplayType() == 2) ? 9 : 12;

				receiver.drawScoreFont(engine, playerID, x, 2, "SECTION TIME", EventReceiver.COLOR_BLUE);

				for(int i = 0; i < sectiontime.length; i++) {
					if(sectiontime[i] > 0) {
						int temp = i * 100;
						if(temp > 300) temp = 300;

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
			if(engine.statistics.level < 299) engine.statistics.level++;
			levelUp(engine);
		}
		if((engine.ending == 0) && (engine.statc[0] > 0)) {
			lvupflag = false;
		}

		return false;
	}

	/**
	 * This function will be called during ARE
	 */
	@Override
	public boolean onARE(GameEngine engine, int playerID) {
		if((engine.ending == 0) && (engine.statc[0] >= engine.statc[1] - 1) && (!lvupflag)) {
			if (engine.statistics.level < 299) engine.statistics.level++;
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

		if(engine.statistics.level >= nextseclv) {
			nextseclv += 100;
			receiver.playSE("levelup");

			//owner.backgroundStatus.fadesw = true;
			//owner.backgroundStatus.fadecount = 0;
			//owner.backgroundStatus.fadebg = nextseclv / 100;

			sectionscomp++;
			setAverageSectionTime();
			stNewRecordCheck(sectionscomp - 1);
		}

		setSpeed(engine);

		if((engine.statistics.level >= 100) && (!alwaysghost)) engine.ghost = false;

		if((bgmlv == 0) && (engine.statistics.level >= 290) && (engine.ending == 0))
			owner.bgmStatus.fadesw = true;
	}

	/**
	 * Calculates line-clear score
	 * (This function will be called even if no lines are cleared)
	 */
	@Override
	public void calcScore(GameEngine engine, int playerID, int lines) {
		if(engine.ending != 0) return;

		if(lines == 0) {
			comboValue = 1;
		} else {
			comboValue = comboValue + (2 * lines) - 2;
			if(comboValue < 1) comboValue = 1;
		}

		if(lines >= 1) {
			int levelb = engine.statistics.level;
			engine.statistics.level += lines;
			levelUp(engine);

			if(engine.statistics.level >= 300) {
				if(engine.timerActive) {
					//sectionscomp++;
					//setAverageSectionTime();
					//stNewRecordCheck(sectionscomp - 1);
					engine.statistics.score += 1253*Math.ceil(Math.max(18000-engine.statistics.time,0)/60D);
				}

				bgmlv++;
				owner.bgmStatus.fadesw = false;
				owner.bgmStatus.bgm = bgmlv;

				engine.statistics.level = 300;
				engine.timerActive = false;
				engine.ending = 2;
			} else if(engine.statistics.level >= nextseclv) {
				//receiver.playSE("levelup");
				//nextseclv += 100;
			}

			if(owner.backgroundStatus.bg < (nextseclv-100) / 100) {
				owner.backgroundStatus.fadesw = true;
				owner.backgroundStatus.fadecount = 0;
				owner.backgroundStatus.fadebg = (nextseclv-100) / 100;
			}

			int manuallock = 0;
			if(engine.manualLock == true) manuallock = 1;

			int bravo = 1;
			if(engine.field.isEmpty()) bravo = 4;

			int speedBonus = engine.getLockDelay() - engine.statc[0];
			if(speedBonus < 0) speedBonus = 0;

			lastscore = 6*(((levelb + lines)/4 + engine.softdropFall + manuallock + harddropBonus) * lines * comboValue * bravo +
						(engine.statistics.level / 2) + (speedBonus * 7));
			engine.statistics.score += lastscore;
			scgettime = 120;
		}
	}

	/**
	 * This function will be called when hard-drop is used
	 */
	@Override
	public void afterHardDropFall(GameEngine engine, int playerID, int fall) {
		if(fall * 2 > harddropBonus) harddropBonus = fall * 2;
	}

	/**
	 * This function will be called when the game timer updates
	 */
	@Override
	public void onLast(GameEngine engine, int playerID) {
		// Decrease scgettime
		if(scgettime > 0) scgettime--;

		// Increase section timer
		if((engine.timerActive) && (engine.ending == 0)) {
			int section = engine.statistics.level / 100;

			if((section >= 0) && (section < sectiontime.length)) {
				sectiontime[section]++;
			}
		}

		// Increase ending timer
		if((engine.gameActive) && (engine.ending == 2)) {
			if(engine.ctrl.isPress(Controller.BUTTON_F))
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
				engine.gameEnded();
				engine.resetStatc();
				engine.stat = GameEngine.Status.EXCELLENT;
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
		receiver.drawMenuFont(engine, playerID,  0, 0, "kn PAGE" + (engine.statc[1] + 1) + "/3", EventReceiver.COLOR_RED);

		if(engine.statc[1] == 0) {
			drawResultStats(engine, playerID, receiver, 2, EventReceiver.COLOR_BLUE,
					Statistic.SCORE, Statistic.LINES, Statistic.LEVEL, Statistic.TIME);
			drawResultRank(engine, playerID, receiver, 13, EventReceiver.COLOR_BLUE, rankingRank);
			if(secretGrade > 4) {
				drawResult(engine, playerID, receiver, 15, EventReceiver.COLOR_BLUE,
						"S. GRADE", String.format("%10s", tableSecretGradeName[secretGrade-1]));
			}
		} else if(engine.statc[1] == 1) {
			receiver.drawMenuFont(engine, playerID,  0, 2, "SECTION", EventReceiver.COLOR_BLUE);

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
		saveSetting(prop);

		if((owner.replayMode == false) && (startlevel == 0) && (always20g == false) && (big == false) && (engine.ai == null)) {
			updateRanking(engine.statistics.score, engine.statistics.level, engine.statistics.time);
			if(sectionAnyNewRecord) updateBestSectionTime();

			if((rankingRank != -1) || (sectionAnyNewRecord)) {
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
			rankingScore[i] = prop.getProperty("scoreattack.ranking." + ruleName + ".score." + i, 0);
			rankingLevel[i] = prop.getProperty("scoreattack.ranking." + ruleName + ".level." + i, 0);
			rankingTime[i] = prop.getProperty("scoreattack.ranking." + ruleName + ".time." + i, 0);
		}
		for(int i = 0; i < SECTION_MAX; i++) {
			bestSectionTime[i] = prop.getProperty("scoreattack.bestSectionTime." + ruleName + "." + i, DEFAULT_SECTION_TIME);
		}
	}

	/**
	 * Save the ranking
	 */
	private void saveRanking(CustomProperties prop, String ruleName) {
		for(int i = 0; i < RANKING_MAX; i++) {
			prop.setProperty("scoreattack.ranking." + ruleName + ".score." + i, rankingScore[i]);
			prop.setProperty("scoreattack.ranking." + ruleName + ".level." + i, rankingLevel[i]);
			prop.setProperty("scoreattack.ranking." + ruleName + ".time." + i, rankingTime[i]);
		}
		for(int i = 0; i < SECTION_MAX; i++) {
			prop.setProperty("scoreattack.bestSectionTime." + ruleName + "." + i, bestSectionTime[i]);
		}
	}

	/**
	 * Update the ranking
	 */
	private void updateRanking(int sc, int lv, int time) {
		rankingRank = checkRanking(sc, lv, time);

		if(rankingRank != -1) {
			for(int i = RANKING_MAX - 1; i > rankingRank; i--) {
				rankingScore[i] = rankingScore[i - 1];
				rankingLevel[i] = rankingLevel[i - 1];
				rankingTime[i] = rankingTime[i - 1];
			}

			rankingScore[rankingRank] = sc;
			rankingLevel[rankingRank] = lv;
			rankingTime[rankingRank] = time;
		}
	}

	/**
	 * This function will check the ranking and returns which place you are. (-1: Out of rank)
	 */
	private int checkRanking(int sc, int lv, int time) {
		for(int i = 0; i < RANKING_MAX; i++) {
			if(sc > rankingScore[i]) {
				return i;
			} else if((sc == rankingScore[i]) && (lv > rankingLevel[i])) {
				return i;
			} else if((sc == rankingScore[i]) && (lv == rankingLevel[i]) && (time < rankingTime[i])) {
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
