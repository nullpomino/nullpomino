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
import mu.nu.nullpo.util.CustomProperties;
import mu.nu.nullpo.util.GeneralUtil;

/**
 * TIME ATTACK mode (Original from NullpoUE build 010210 by Zircean. This mode is heavily modified from the original.)
 */
public class TimeAttackMode extends DummyMode {
	/** Current version of this mode */
	private static final int CURRENT_VERSION = 1;

	/** Gravity tables */
	private static final int tableGravity[][] =
	{
		{ 4, 12, 48, 72, 96, 128, 256, 384, 512, 768, 1024, 1280, -1},	// NORMAL
		{84,128,256,512,768,1024,1280,  -1},							// HIGH SPEED 1
		{-1},															// HIGH SPEED 2
		{-1},															// ANOTHER
		{-1},															// ANOTHER 2
		{ 4, 12, 48, 72, 96, 128, 256, 384, 512, 768, 1024, 1280, -1},	// NORMAL 200
		{-1},															// ANOTHER 200
		{ 1,  3, 15, 30, 60, 120, 180, 240, 300, 300, -1},				// BASIC
		{-1},															// HELL
		{-1},															// HELL-X
		{-1},															// VOID
	};

	/** Denominator table */
	private static final int tableDenominator[] =
	{
		256,	// NORMAL
		256,	// HIGH SPEED 1
		256,	// HIGH SPEED 2
		256,	// ANOTHER
		256,	// ANOTHER2
		256,	// NORMAL 200
		256,	// ANOTHER 200
		60,		// BASIC
		256,	// HELL
		256,	// HELL-X
		256,	// VOID
	};

	/** Max level table */
	private static final int tableGoalLevel[] =
	{
		15,	// NORMAL
		15,	// HIGH SPEED 1
		15,	// HIGH SPEED 2
		15,	// ANOTHER
		15,	// ANOTHER2
		20,	// NORMAL 200
		20,	// ANOTHER 200
		20,	// BASIC
		20,	// HELL
		20,	// HELL-X
		20,	// VOID
	};

	/** Level timer tables */
	private static final int tableLevelTimer[][] =
	{
		{7200, 7200, 5400},										// NORMAL
		{7200, 7200, 5400},										// HIGH SPEED 1
		{7200, 7200, 5400},										// HIGH SPEED 2
		{3600},													// ANOTHER
		{7200, 7200, 5400},										// ANOTHER 2
		{7200, 7200, 5400},										// NORMAL 200
		{3600},													// ANOTHER 200
		{1800, 1800, 1800, 1800, 1800, 1500, 1500, 1500, 1500, 1500,
			1200, 1200, 1200, 1200, 1200, 1200, 1200, 1200, 1200, 900},	// BASIC
		{1800, 1800, 1800, 1800, 1800, 1500, 1500, 1500, 1500, 1500,
			1200, 1200, 1200, 1020, 900, 1200, 1020, 900, 840},	// HELL
		{1800, 1800, 1800, 1800, 1800, 1500, 1500, 1500, 1500, 1500,
			1200, 1200, 1200, 1020, 900, 1200, 1020, 900, 840},	// HELL-X
		{1800, 1200, 900, 900, 900, 840, 840, 840, 840, 840, 720,
			720, 720, 720, 720, 540, 480, 420, 360, 300},		// VOID
	};

	/** Speed table for ANOTHER */
	private static final int tableAnother[][] =
	{
		{18,14,14,14,12,12,10, 8, 7, 6}, // ARE
		{14, 8, 8, 5, 5, 5, 5, 5, 5, 5}, // Line delay
		{28,24,22,20,18,14,14,13,13,13}, // Lock delay
		{10,10, 9, 9, 9, 8, 8, 7, 7, 7}  // DAS
	};

	/** Speed table for NORMAL 200 */
	private static final int tableNormal200[][] =
	{
		{25,25,25,25,25,25,25,25,25,25,25,25,25,25,22,16,16,12,12,10},	// ARE
		{25,25,25,25,25,25,25,25,25,25,25,25,25,25,16,16,16,12,12, 8},	// Line delay
		{30,30,30,30,30,30,30,30,30,30,30,30,30,30,30,25,24,22,20,17},	// Lock delay
		{15,15,15,15,15,15,15,15,15,15,15,15,15,15,12,10, 9, 8, 8, 6}	// DAS
	};

	/** Speed table for VOID */
	private static final int tableVoid[][] =
	{
		{ 2, 2,1,1,1,0,0,0,0,0,0,0,0,0,0,0},	// ARE
		{ 3, 1,0,0,0,0,0,0,0,0,0,0,0,0,0,0},	// Line delay
		{11,10,9,9,9,9,9,9,9,9,9,9,9,9,9,8},	// Lock delay
		{ 7, 5,5,4,4,3,3,3,3,3,3,3,3,3,3,2}		// DAS
	};

	/** Speed table for BASIC */
	private static final int tableBasic[][] =
	{
		{26,26,26,26,26,26,26,26,26,26,15,11,11,11,11,10, 9, 5, 3, 2}, // ARE
		{40,40,40,30,30,25,25,25,25,25,20,15,12,10, 6, 5, 4, 3, 3, 3}, // Line delay
		{28,28,28,26,26,26,26,26,25,25,25,23,23,23,20,20,18,18,14,11}, // Lock delay
		{15,15,15,15,15,15,15,15,15,15,10, 9, 9, 8, 8, 7, 7, 7, 7, 7}  // DAS
	};

	/** BGM change lines table */
	private static final int tableBGMChange[][] =
	{
		{50, 100},	// NORMAL
		{50, 100},	// HI-SPEED 1
		{50, 100},	// HI-SPEED 2
		{40, 100},	// ANOTHER
		{40, 100},	// ANOTHER2
		{50, 150},	// NORMAL 200
		{40, 150},	// ANOTHER 200
		{50, 150},	// BASIC
		{50, 150},	// HELL
		{50, 150},	// HELL-X
		{},			// VOID
	};

	/** BGM fadeout lines table */
	private static final int tableBGMFadeout[][] =
	{
		{45, 95, 145},	// NORMAL
		{45, 95, 145},	// HI-SPEED 1
		{45, 95, 145},	// HI-SPEED 2
		{35, 95, 145},	// ANOTHER
		{35, 95, 145},	// ANOTHER2
		{45, 145, 195},	// NORMAL 200
		{45, 145, 195},	// ANOTHER 200
		{45, 145, 195},	// BASIC
		{45, 145, 195},	// HELL
		{45, 145, 195},	// HELL-X
		{195},			// VOID
	};

	/** BGM kind table */
	private static final int tableBGMNumber[][] =
	{
		{BGMStatus.BGM_SPECIAL1, BGMStatus.BGM_NORMAL2,  BGMStatus.BGM_NORMAL3},	// NORMAL
		{BGMStatus.BGM_NORMAL1,  BGMStatus.BGM_NORMAL3,  BGMStatus.BGM_NORMAL6},	// HI-SPEED 1
		{BGMStatus.BGM_NORMAL3,  BGMStatus.BGM_NORMAL6,  BGMStatus.BGM_NORMAL4},	// HI-SPEED 2
		{BGMStatus.BGM_NORMAL6,  BGMStatus.BGM_NORMAL4,  BGMStatus.BGM_NORMAL5},	// ANOTHER
		{BGMStatus.BGM_NORMAL6,  BGMStatus.BGM_NORMAL4,  BGMStatus.BGM_NORMAL5},	// ANOTHER2
		{BGMStatus.BGM_SPECIAL1, BGMStatus.BGM_NORMAL2,  BGMStatus.BGM_NORMAL3},	// NORMAL 200
		{BGMStatus.BGM_NORMAL6,  BGMStatus.BGM_NORMAL4,  BGMStatus.BGM_NORMAL5},	// ANOTHER 200
		{BGMStatus.BGM_NORMAL1,  BGMStatus.BGM_NORMAL2,  BGMStatus.BGM_NORMAL3},	// BASIC
		{BGMStatus.BGM_PUZZLE4,  BGMStatus.BGM_SPECIAL4, BGMStatus.BGM_SPECIAL2},	// HELL
		{BGMStatus.BGM_NORMAL4,  BGMStatus.BGM_NORMAL5,  BGMStatus.BGM_SPECIAL3},	// HELL-X
		{BGMStatus.BGM_NORMAL6},	// VOID
	};

	/** Game types */
	private static final int GAMETYPE_NORMAL = 0,
							 GAMETYPE_HIGHSPEED1 = 1,
							 GAMETYPE_HIGHSPEED2 = 2,
							 GAMETYPE_ANOTHER = 3,
							 GAMETYPE_ANOTHER2 = 4,
							 GAMETYPE_NORMAL200 = 5,
							 GAMETYPE_ANOTHER200 = 6,
							 GAMETYPE_BASIC = 7,
							 GAMETYPE_HELL = 8,
							 GAMETYPE_HELLX = 9,
							 GAMETYPE_VOID = 10;

	/** Number of game types */
	private static final int GAMETYPE_MAX = 11;

	/** Game type names (short) */
	private static final String[] GAMETYPE_NAME = {"NORMAL","HISPEED1","HISPEED2","ANOTHER","ANOTHER2",
		"NORM200","ANOTH200","BASIC","HELL","HELL-X","VOID"};

	/** Game type names (long) */
	private static final String[] GAMETYPE_NAME_LONG = {"NORMAL","HIGH SPEED 1","HIGH SPEED 2","ANOTHER","ANOTHER 2",
		"NORMAL 200","ANOTHER 200","BASIC","HELL","HELL-X","VOID"};

	/** HELL-X fade table */
	private static final int tableHellXFade[] = {-1,-1,-1,-1,-1,150,150,150,150,150,
		150,150,150,150,150,120,120,120,120,60};

	/** Ending time limit */
	private static final int ROLLTIMELIMIT = 3238;

	/** Number of ranking records */
	private static final int RANKING_MAX = 10;

	/** Number of ranking types */
	private static final int RANKING_TYPE = 11;

	/** GameManager object (Manages entire game status) */
	private GameManager owner;

	/** EventReceiver object (This receives many game events, can also be used for drawing the fonts.) */
	private EventReceiver receiver;

	/** Remaining level time */
	private int levelTimer;

	/** Original level time */
	private int levelTimerMax;

	/** Current lines (for levelup) */
	private int norm;

	/** Current BGM number */
	private int bgmlv;

	/** Elapsed ending time */
	private int rolltime;

	/** Ending started flag */
	private boolean rollstarted;

	/** Game completed flag (0=Died before 150/200 lines 1=Died during credits roll 2=Survived credits roll */
	private int rollclear;

	/** Section time */
	private int[] sectiontime;

	/** Number of sections completed */
	private int sectionscomp;

	/** Average section time */
	private int sectionavgtime;

	/** Game type */
	private int gametype;

	/** Selected starting level */
	private int startlevel;

	/** Big mode on/off */
	private boolean big;

	/** Show section time */
	private boolean showsectiontime;

	/** Version of this mode */
	private int version;

	/** Your place on leaderboard (-1: out of rank) */
	private int rankingRank;

	/** Line records */
	private int[][] rankingLines;

	/** Time records */
	private int[][] rankingTime;

	/** Game completed flag records */
	private int[][] rankingRollclear;

	/**
	 * Returns the name of this mode
	 */
	@Override
	public String getName() {
		return "TIME ATTACK";
	}

	/**
	 * This function will be called when the game enters the main game screen.
	 */
	@Override
	public void playerInit(GameEngine engine, int playerID) {
		owner = engine.owner;
		receiver = engine.owner.receiver;

		norm = 0;
		gametype = 0;
		startlevel = 0;
		rolltime = 0;
		rollstarted = false;
		rollclear = 0;
		sectiontime = new int[20];
		sectionscomp = 0;
		sectionavgtime = 0;
		big = false;
		showsectiontime = true;

		rankingRank = -1;
		rankingLines = new int[RANKING_TYPE][RANKING_MAX];
		rankingTime = new int[RANKING_TYPE][RANKING_MAX];
		rankingRollclear = new int[RANKING_TYPE][RANKING_MAX];

		engine.tspinEnable = false;
		engine.b2bEnable = false;
		engine.comboType = GameEngine.COMBO_TYPE_DISABLE;
		engine.framecolor = GameEngine.FRAME_COLOR_GRAY;
		engine.bighalf = true;
		engine.bigmove = true;
		engine.staffrollEnable = false;
		engine.staffrollNoDeath = false;

		if(owner.replayMode == false) {
			loadSetting(owner.modeConfig);
			loadRanking(owner.modeConfig, engine.ruleopt.strRuleName);
			version = CURRENT_VERSION;
		} else {
			loadSetting(owner.replayProp);
		}

		engine.owner.backgroundStatus.bg = startlevel;
	}

	/**
	 * Set the gravity speed and some other things
	 * @param engine GameEngine object
	 */
	private void setSpeed(GameEngine engine) {
		// Gravity speed
		int gravlv = engine.statistics.level;
		if(gravlv < 0) gravlv = 0;
		if(gravlv >= tableGravity[gametype].length) gravlv = tableGravity[gametype].length - 1;
		engine.speed.gravity = tableGravity[gametype][gravlv];
		engine.speed.denominator = tableDenominator[gametype];

		// Other speed values
		int speedlv = engine.statistics.level;
		if(speedlv < 0) speedlv = 0;

		switch(gametype) {
		case GAMETYPE_NORMAL:
		case GAMETYPE_HIGHSPEED1:
		case GAMETYPE_HIGHSPEED2:
			engine.speed.are = 25;
			engine.speed.areLine = 25;
			engine.speed.lineDelay = 41;
			engine.speed.lockDelay = 30;
			engine.speed.das = 15;
			break;
		case GAMETYPE_ANOTHER:
		case GAMETYPE_ANOTHER200:
			if(speedlv >= tableAnother[0].length) speedlv = tableAnother[0].length - 1;
			engine.speed.are = tableAnother[0][speedlv];
			engine.speed.areLine = tableAnother[0][speedlv];
			engine.speed.lineDelay = tableAnother[1][speedlv];
			engine.speed.lockDelay = tableAnother[2][speedlv];
			engine.speed.das = tableAnother[3][speedlv];
			break;
		case GAMETYPE_ANOTHER2:
			engine.speed.are = 6;
			engine.speed.areLine = 6;
			engine.speed.lineDelay = 4;
			engine.speed.lockDelay = 13;
			engine.speed.das = 7;
			break;
		case GAMETYPE_NORMAL200:
			if(speedlv >= tableNormal200[0].length) speedlv = tableNormal200[0].length - 1;
			engine.speed.are = tableNormal200[0][speedlv];
			engine.speed.areLine = tableNormal200[0][speedlv];
			engine.speed.lineDelay = tableNormal200[1][speedlv];
			engine.speed.lockDelay = tableNormal200[2][speedlv];
			engine.speed.das = tableNormal200[3][speedlv];
			break;
		case GAMETYPE_BASIC:
			if(speedlv >= tableBasic[0].length) speedlv = tableBasic[0].length - 1;
			engine.speed.are = tableBasic[0][speedlv];
			engine.speed.areLine = tableBasic[0][speedlv];
			engine.speed.lineDelay = tableBasic[1][speedlv];
			engine.speed.lockDelay = tableBasic[2][speedlv];
			engine.speed.das = tableBasic[3][speedlv];
			break;
		case GAMETYPE_HELL:
		case GAMETYPE_HELLX:
			engine.speed.are = 2;
			engine.speed.areLine = 2;
			engine.speed.lineDelay = 3;
			engine.speed.lockDelay = 11;
			engine.speed.das = 7;
			break;
		case GAMETYPE_VOID:
			if(speedlv >= tableVoid[0].length) speedlv = tableVoid[0].length - 1;
			engine.speed.are = tableVoid[0][speedlv];
			engine.speed.areLine = tableVoid[0][speedlv];
			engine.speed.lineDelay = tableVoid[1][speedlv];
			engine.speed.lockDelay = tableVoid[2][speedlv];
			engine.speed.das = tableVoid[3][speedlv];
			break;
		}

		// Level timer
		int timelv = engine.statistics.level;
		if(timelv < 0) timelv = 0;
		if(timelv >= tableLevelTimer[gametype].length) timelv = tableLevelTimer[gametype].length - 1;
		levelTimerMax = levelTimer = tableLevelTimer[gametype][timelv];

		// Show outline only
		if(gametype == GAMETYPE_HELL) {
			engine.blockShowOutlineOnly = true;
		}
		// Bone blocks
		if( (gametype == GAMETYPE_HELLX) || ((gametype == GAMETYPE_HELL) && (engine.statistics.level >= 15)) || (gametype == GAMETYPE_VOID) ) {
			engine.bone = true;
		}
		// Block fade for HELL-X
		if(gametype == GAMETYPE_HELLX) {
			int fadelv = engine.statistics.level;
			if(fadelv < 0) fadelv = 0;
			if(fadelv >= tableHellXFade.length) fadelv = tableHellXFade.length - 1;
			engine.blockHidden = tableHellXFade[fadelv];
		}

		// for test
		/*
		engine.speed.are = 25;
		engine.speed.areLine = 25;
		engine.speed.lineDelay = 10;
		engine.speed.lockDelay = 30;
		engine.speed.das = 12;
		levelTimerMax = levelTimer = 3600 * 3;
		*/
	}

	/**
	 * Set the starting bgmlv
	 * @param engine GameEngine
	 */
	private void setStartBgmlv(GameEngine engine) {
		bgmlv = 0;
		while((bgmlv < tableBGMChange[gametype].length) && (norm >= tableBGMChange[gametype][bgmlv])) bgmlv++;
	}

	/**
	 * Calculates average section time
	 */
	private void setAverageSectionTime() {
		if(sectionscomp > 0) {
			int temp = 0;
			for(int i = startlevel; i < sectionscomp; i++) temp += sectiontime[i];
			sectionavgtime = temp / sectionscomp;
		} else {
			sectionavgtime = 0;
		}
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
					gametype += change;
					if(gametype < 0) gametype = GAMETYPE_MAX - 1;
					if(gametype > GAMETYPE_MAX - 1) gametype = 0;
					if(startlevel > tableGoalLevel[gametype] - 1) startlevel = tableGoalLevel[gametype] - 1;
					engine.owner.backgroundStatus.bg = startlevel;
					break;
				case 1:
					startlevel += change;
					if(startlevel < 0) startlevel = tableGoalLevel[gametype] - 1;
					if(startlevel > tableGoalLevel[gametype] - 1) startlevel = 0;
					engine.owner.backgroundStatus.bg = startlevel;
					break;
				case 2:
					showsectiontime = !showsectiontime;
					break;
				case 3:
					big = !big;
					break;
				}
			}

			// Check for A button, when pressed this will begin the game
			if(engine.ctrl.isPush(Controller.BUTTON_A) && (engine.statc[3] >= 5)) {
				receiver.playSE("decide");
				saveSetting(owner.modeConfig);
				receiver.saveModeConfig(owner.modeConfig);
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
				"DIFFICULTY", GAMETYPE_NAME[gametype],
				"LEVEL", String.valueOf(startlevel),
				"SHOW STIME", GeneralUtil.getONorOFF(showsectiontime),
				"BIG",  GeneralUtil.getONorOFF(big));
	}

	/**
	 * Ready screen
	 */
	@Override
	public boolean onReady(GameEngine engine, int playerID) {
		if(engine.statc[0] == 0) {
			engine.statistics.level = startlevel;
			engine.statistics.levelDispAdd = 1;
			engine.big = big;
			norm = startlevel * 10;
			setSpeed(engine);
			setStartBgmlv(engine);
		}

		return false;
	}

	/**
	 * This function will be called before the game actually begins (after Ready&Go screen disappears)
	 */
	@Override
	public void startGame(GameEngine engine, int playerID) {
		owner.bgmStatus.bgm = tableBGMNumber[gametype][bgmlv];
	}

	/**
	 * Renders HUD (leaderboard or game statistics)
	 */
	@Override
	public void renderLast(GameEngine engine, int playerID) {
		receiver.drawScoreFont(engine, playerID, 0, 0, "TIME ATTACK", EventReceiver.COLOR_PURPLE);
		receiver.drawScoreFont(engine, playerID, 0, 1, "("+GAMETYPE_NAME_LONG[gametype]+")", EventReceiver.COLOR_PURPLE);

		if( (engine.stat == GameEngine.STAT_SETTING) || ((engine.stat == GameEngine.STAT_RESULT) && (owner.replayMode == false)) ) {
			if((owner.replayMode == false) && (startlevel == 0) && (big == false) && (engine.ai == null)) {
				receiver.drawScoreFont(engine, playerID, 3, 3, "LINE TIME", EventReceiver.COLOR_BLUE);

				for(int i = 0; i < RANKING_MAX; i++) {
					int gcolor = EventReceiver.COLOR_WHITE;
					if(rankingRollclear[gametype][i] == 1) gcolor = EventReceiver.COLOR_GREEN;
					if(rankingRollclear[gametype][i] == 2) gcolor = EventReceiver.COLOR_ORANGE;

					receiver.drawScoreFont(engine, playerID, 0, 4 + i, String.format("%2d", i + 1),
							(i == rankingRank) ? EventReceiver.COLOR_RED : EventReceiver.COLOR_YELLOW);
					receiver.drawScoreFont(engine, playerID, 3, 4 + i, String.valueOf(rankingLines[gametype][i]), gcolor);
					receiver.drawScoreFont(engine, playerID, 8, 4 + i, GeneralUtil.getTime(rankingTime[gametype][i]), gcolor);
				}
			}
		} else {
			receiver.drawScoreFont(engine, playerID, 0, 3, "LEVEL", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 0, 4, String.valueOf(engine.statistics.level + 1));

			receiver.drawScoreFont(engine, playerID, 0, 6, "TIME LIMIT", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 0, 7, GeneralUtil.getTime(levelTimer),
									((levelTimer > 0) && (levelTimer < 600) && (levelTimer % 4 == 0)));

			receiver.drawScoreFont(engine, playerID, 0, 9, "TOTAL TIME", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 0, 10, GeneralUtil.getTime(engine.statistics.time));

			receiver.drawScoreFont(engine, playerID, 0, 12, "NORM", EventReceiver.COLOR_BLUE);
			String strLevel = String.format("%3d", norm);
			receiver.drawScoreFont(engine, playerID, 0, 13, strLevel);

			int speed = engine.speed.gravity / (tableDenominator[gametype]/2);
			if(engine.speed.gravity < 0) speed = 40;
			receiver.drawSpeedMeter(engine, playerID, 0, 14, speed);

			receiver.drawScoreFont(engine, playerID, 0, 15, String.format("%3d", (engine.statistics.level + 1)*10));

			// Remaining ending time
			if((engine.gameActive) && (engine.ending == 2) && (engine.staffrollEnable)) {
				int time = ROLLTIMELIMIT - rolltime;
				if(time < 0) time = 0;
				receiver.drawScoreFont(engine, playerID, 0, 17, "ROLL TIME", EventReceiver.COLOR_BLUE);
				receiver.drawScoreFont(engine, playerID, 0, 18, GeneralUtil.getTime(time), ((time > 0) && (time < 10 * 60)));
			}

			// Section time
			if((showsectiontime == true) && (sectiontime != null)) {
				receiver.drawScoreFont(engine, playerID, 12, 3, "SECTION TIME", EventReceiver.COLOR_BLUE);

				for(int i = 0; i < sectiontime.length; i++) {
					if(sectiontime[i] > 0) {
						String strSeparator = " ";
						if((i == engine.statistics.level) && (engine.ending == 0)) strSeparator = "b";

						String strSectionTime;
						strSectionTime = String.format("%2d%s%s", i + 1, strSeparator, GeneralUtil.getTime(sectiontime[i]));

						int pos = i - Math.max(engine.statistics.level-9,0);

						if (pos >= 0) receiver.drawScoreFont(engine, playerID, 13, 4 + pos, strSectionTime);
					}
				}

				if(sectionavgtime > 0) {
					receiver.drawScoreFont(engine, playerID, 12, 15, "AVERAGE", EventReceiver.COLOR_BLUE);
					receiver.drawScoreFont(engine, playerID, 12, 16, GeneralUtil.getTime(sectionavgtime));
				}
			}
		}
	}

	/**
	 * This function will be called when the piece is active
	 */
	@Override
	public boolean onMove(GameEngine engine, int playerID) {
		// Enable timer again after the levelup
		if((engine.ending == 0) && (engine.statc[0] == 0) && (engine.holdDisable == false) && (engine.ending == 0)) {
			engine.timerActive = true;
		}
		// Ending start
		if((engine.ending == 2) && (engine.staffrollEnable == true) && (rollstarted == false)) {
			rollstarted = true;
			owner.bgmStatus.bgm = BGMStatus.BGM_ENDING1;
			owner.bgmStatus.fadesw = false;

			// VOID ending
			if(gametype == GAMETYPE_VOID) {
				engine.blockHidden = engine.ruleopt.lockflash;
				engine.blockHiddenAnim = false;
				engine.blockOutlineType = GameEngine.BLOCK_OUTLINE_NONE;
			}
		}

		return false;
	}

	/**
	 * This function will be called when the game timer updates
	 */
	@Override
	public void onLast(GameEngine engine, int playerID) {
		// Level timer
		if((engine.timerActive) && (engine.ending == 0)) {
			if(levelTimer > 0) {
				levelTimer--;
				if((levelTimer <= 600) && (levelTimer % 60 == 0)) {
					receiver.playSE("countdown");
				}
			} else {
				engine.gameActive = false;
				engine.timerActive = false;
				engine.resetStatc();
				engine.stat = GameEngine.STAT_GAMEOVER;
			}
		}

		// Update meter
		if((tableGoalLevel[gametype] >= 20) && (engine.ending == 0) && (levelTimerMax != 0)) {
			engine.meterValue = (levelTimer * receiver.getMeterMax(engine)) / levelTimerMax;
			engine.meterColor = GameEngine.METER_COLOR_GREEN;
			if(levelTimer <= 25*60) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
			if(levelTimer <= 15*60) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
			if(levelTimer <= 10*60) engine.meterColor = GameEngine.METER_COLOR_RED;
		}

		// Section time
		if((engine.timerActive) && (engine.ending == 0)) {
			if((engine.statistics.level >= 0) && (engine.statistics.level < sectiontime.length)) {
				sectiontime[engine.statistics.level]++;
				setAverageSectionTime();
			}
		}

		// Hebo Hidden for HELL
		if((gametype == GAMETYPE_HELL) && (engine.timerActive) && (engine.ending == 0)) {
			if((engine.statistics.level >= 5) && (engine.statistics.level <= 6)) {
				engine.heboHiddenEnable = true;
				engine.heboHiddenYLimit = 19;
				engine.heboHiddenTimerMax = engine.heboHiddenYNow * 30 + 45;
			} else if((engine.statistics.level >= 7) && (engine.statistics.level <= 14)) {
				engine.heboHiddenEnable = true;
				engine.heboHiddenYLimit = 19;
				engine.heboHiddenTimerMax = engine.heboHiddenYNow * 10 + 30;
			} else {
				engine.heboHiddenEnable = false;
			}
		}

		// Ending
		if((engine.gameActive) && (engine.ending == 2)) {
			rolltime++;

			// Update meter
			int remainRollTime = ROLLTIMELIMIT - rolltime;
			engine.meterValue = (remainRollTime * receiver.getMeterMax(engine)) / ROLLTIMELIMIT;
			engine.meterColor = GameEngine.METER_COLOR_GREEN;
			if(remainRollTime <= 30*60) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
			if(remainRollTime <= 20*60) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
			if(remainRollTime <= 10*60) engine.meterColor = GameEngine.METER_COLOR_RED;

			// Completed
			if(rolltime >= ROLLTIMELIMIT) {
				rollclear = 2;
				engine.gameActive = false;
				engine.resetStatc();
				engine.stat = GameEngine.STAT_EXCELLENT;
			}
		}
	}

	/**
	 * Calculates line-clear score
	 * (This function will be called even if no lines are cleared)
	 */
	@Override
	public void calcScore(GameEngine engine, int playerID, int lines) {
		// Don't do anything during the ending
		if(engine.ending != 0) return;

		// Add lines to norm
		norm += lines;

		// Decrease Hebo Hidden
		if((engine.heboHiddenEnable) && (lines > 0)) {
			engine.heboHiddenTimerNow = 0;
			engine.heboHiddenYNow -= lines;
			if(engine.heboHiddenYNow < 0) engine.heboHiddenYNow = 0;
		}

		// Update meter
		if(tableGoalLevel[gametype] < 20) {
			engine.meterValue = ((norm % 10) * receiver.getMeterMax(engine)) / 9;
			engine.meterColor = GameEngine.METER_COLOR_GREEN;
			if(norm % 10 >= 4) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
			if(norm % 10 >= 6) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
			if(norm % 10 >= 8) engine.meterColor = GameEngine.METER_COLOR_RED;
		}

		// BGM change
		if((bgmlv < tableBGMChange[gametype].length) && (norm >= tableBGMChange[gametype][bgmlv])) {
			bgmlv++;
			owner.bgmStatus.bgm = tableBGMNumber[gametype][bgmlv];
			owner.bgmStatus.fadesw = false;
		}
		// BGM fadeout
		else if((bgmlv < tableBGMFadeout[gametype].length) && (norm >= tableBGMFadeout[gametype][bgmlv])) {
			owner.bgmStatus.fadesw = true;
		}

		// Game completed
		if(norm >= tableGoalLevel[gametype] * 10) {
			receiver.playSE("levelup");

			// Update section time
			if(engine.timerActive) {
				sectionscomp++;
				setAverageSectionTime();
			}

			norm = tableGoalLevel[gametype] * 10;
			engine.ending = 1;
			engine.timerActive = false;

			if((gametype == GAMETYPE_HELLX) || (gametype == GAMETYPE_VOID)) {
				// HELL-X ending & VOID ending
				engine.staffrollEnable = true;
				rollclear = 1;
			} else {
				engine.gameActive = false;
				rollclear = 2;
			}
		}
		// Level up
		else if((norm >= (engine.statistics.level + 1) * 10) && (engine.statistics.level < tableGoalLevel[gametype] - 1)) {
			receiver.playSE("levelup");
			engine.statistics.level++;

			owner.backgroundStatus.fadesw = true;
			owner.backgroundStatus.fadecount = 0;
			owner.backgroundStatus.fadebg = engine.statistics.level;

			sectionscomp++;

			engine.timerActive = false;	// Stop timer until the next piece becomes active

			setSpeed(engine);
		}
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

			receiver.drawMenuFont(engine, playerID,  0, 2, "NORM", EventReceiver.COLOR_BLUE);
			String strLines = String.format("%10d", norm);
			receiver.drawMenuFont(engine, playerID,  0, 3, strLines, gcolor);

			drawResultStats(engine, playerID, receiver, 4, EventReceiver.COLOR_BLUE,
					STAT_LEVEL, STAT_TIME, STAT_LPM, STAT_PPS);
			drawResultRank(engine, playerID, receiver, 13, EventReceiver.COLOR_BLUE, rankingRank);
		} else if(engine.statc[1] == 1) {
			receiver.drawMenuFont(engine, playerID, 0, 2, "SECTION", EventReceiver.COLOR_BLUE);

			for(int i = 0; i < 10; i++) {
				if(sectiontime[i] > 0) {
					receiver.drawMenuFont(engine, playerID, 2, 3 + i, GeneralUtil.getTime(sectiontime[i]));
				}
			}

			if(sectionavgtime > 0) {
				receiver.drawMenuFont(engine, playerID, 0, 14, "AVERAGE", EventReceiver.COLOR_BLUE);
				receiver.drawMenuFont(engine, playerID, 2, 15, GeneralUtil.getTime(sectionavgtime));
			}
		} else if(engine.statc[1] == 2) {
			receiver.drawMenuFont(engine, playerID, 0, 2, "SECTION", EventReceiver.COLOR_BLUE);

			for(int i = 10; i < sectiontime.length; i++) {
				if(sectiontime[i] > 0) {
					receiver.drawMenuFont(engine, playerID, 2, i - 7, GeneralUtil.getTime(sectiontime[i]));
				}
			}

			if(sectionavgtime > 0) {
				receiver.drawMenuFont(engine, playerID, 0, 14, "AVERAGE", EventReceiver.COLOR_BLUE);
				receiver.drawMenuFont(engine, playerID, 2, 15, GeneralUtil.getTime(sectionavgtime));
			}
		}
	}

	/**
	 * Additional routine for game result screen
	 */
	@Override
	public boolean onResult(GameEngine engine, int playerID) {
		if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_UP)) {
			engine.statc[1]--;
			if(engine.statc[1] < 0) engine.statc[1] = 2;
			receiver.playSE("change");
		}
		if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_DOWN)) {
			engine.statc[1]++;
			if(engine.statc[1] > 2) engine.statc[1] = 0;
			receiver.playSE("change");
		}

		return false;
	}

	/**
	 * This function will be called when the replay data is going to be saved
	 */
	@Override
	public void saveReplay(GameEngine engine, int playerID, CustomProperties prop) {
		saveSetting(prop);

		if((owner.replayMode == false) && (startlevel == 0) && (big == false) && (engine.ai == null)) {
			updateRanking(norm, engine.statistics.time, gametype, rollclear);

			if(rankingRank != -1) {
				saveRanking(owner.modeConfig, engine.ruleopt.strRuleName);
				receiver.saveModeConfig(owner.modeConfig);
			}
		}
	}

	/**
	 * Load the settings
	 * @param prop CustomProperties
	 */
	private void loadSetting(CustomProperties prop) {
		gametype = prop.getProperty("timeattack.gametype", 0);
		startlevel = prop.getProperty("timeattack.startlevel", 0);
		big = prop.getProperty("timeattack.big", false);
		showsectiontime = prop.getProperty("timeattack.showsectiontime", true);
		version = prop.getProperty("timeattack.version", 0);
	}

	/**
	 * Save the settings
	 * @param prop CustomProperties
	 */
	private void saveSetting(CustomProperties prop) {
		prop.setProperty("timeattack.gametype", gametype);
		prop.setProperty("timeattack.startlevel", startlevel);
		prop.setProperty("timeattack.big", big);
		prop.setProperty("timeattack.showsectiontime", showsectiontime);
		prop.setProperty("timeattack.version", version);
	}

	/**
	 * Load the ranking
	 * @param prop CustomProperties
	 * @param ruleName Rule name
	 */
	private void loadRanking(CustomProperties prop, String ruleName) {
		for(int i = 0; i < RANKING_MAX; i++) {
			for(int type = 0; type < GAMETYPE_MAX; type++) {
				rankingLines[type][i] = prop.getProperty("timeattack.ranking." + ruleName + "." + type + ".lines." + i, 0);
				rankingTime[type][i] = prop.getProperty("timeattack.ranking." + ruleName + "." + type + ".time." + i, 0);
				rankingRollclear[type][i] = prop.getProperty("timeattack.ranking." + ruleName + "." + type + ".rollclear." + i, 0);
			}
		}
	}

	/**
	 * Save the ranking
	 * @param prop CustomProperties
	 * @param ruleName Rule name
	 */
	private void saveRanking(CustomProperties prop, String ruleName) {
		for(int i = 0; i < RANKING_MAX; i++) {
			for(int type = 0; type < GAMETYPE_MAX; type++) {
				prop.setProperty("timeattack.ranking." + ruleName + "." + type + ".lines." + i, rankingLines[type][i]);
				prop.setProperty("timeattack.ranking." + ruleName + "." + type + ".time." + i, rankingTime[type][i]);
				prop.setProperty("timeattack.ranking." + ruleName + "." + type + ".rollclear." + i, rankingRollclear[type][i]);
			}
		}
	}

	/**
	 * Update the ranking
	 * @param li Lines
	 * @param time Time
	 * @param type Game type
	 * @param clear Game completed flag
	 */
	private void updateRanking(int li, int time, int type, int clear) {
		rankingRank = checkRanking(li, time, type, clear);

		if(rankingRank != -1) {
			for(int i = RANKING_MAX - 1; i > rankingRank; i--) {
				rankingLines[type][i] = rankingLines[type][i - 1];
				rankingTime[type][i] = rankingTime[type][i - 1];
				rankingRollclear[type][i] = rankingRollclear[type][i - 1];
			}

			rankingLines[type][rankingRank] = li;
			rankingTime[type][rankingRank] = time;
			rankingRollclear[type][rankingRank] = clear;
		}
	}

	/**
	 * This function will check the ranking and returns which place you are. (-1: Out of rank)
	 * @param li Lines
	 * @param time Time
	 * @param type Game type
	 * @param clear Game completed flag
	 * @return Place (First place is 0. -1 is Out of Rank)
	 */
	private int checkRanking(int li, int time, int type, int clear) {
		for(int i = 0; i < RANKING_MAX; i++) {
			if(clear > rankingRollclear[type][i]) {
				return i;
			} else if((clear == rankingRollclear[type][i]) && (li > rankingLines[type][i])) {
				return i;
			} else if((clear == rankingRollclear[type][i]) && (li == rankingLines[type][i]) && (time < rankingTime[type][i])) {
				return i;
			}
		}

		return -1;
	}
}
