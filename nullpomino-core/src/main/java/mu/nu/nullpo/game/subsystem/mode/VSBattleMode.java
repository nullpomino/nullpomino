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

import java.util.LinkedList;
import java.util.Random;

import mu.nu.nullpo.game.component.BGMStatus;
import mu.nu.nullpo.game.component.Block;
import mu.nu.nullpo.game.component.Controller;
import mu.nu.nullpo.game.component.Field;
import mu.nu.nullpo.game.component.Piece;
import mu.nu.nullpo.game.event.EventReceiver;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.util.CustomProperties;
import mu.nu.nullpo.util.GeneralUtil;

/**
 * VS-BATTLE Mode
 */
public class VSBattleMode extends AbstractMode {
	/** Current version */
	private static final int CURRENT_VERSION = 5;

	/** Number of players */
	private static final int MAX_PLAYERS = 2;

	/** Most recent scoring event type constants */
	private static final int EVENT_NONE = 0,
							 EVENT_SINGLE = 1,
							 EVENT_DOUBLE = 2,
							 EVENT_TRIPLE = 3,
							 EVENT_FOUR = 4,
							 EVENT_TSPIN_SINGLE_MINI = 5,
							 EVENT_TSPIN_SINGLE = 6,
							 EVENT_TSPIN_DOUBLE = 7,
							 EVENT_TSPIN_TRIPLE = 8,
							 EVENT_TSPIN_DOUBLE_MINI = 9,
							 EVENT_TSPIN_EZ = 10;

	/** Combo attack table */
	private final int[] COMBO_ATTACK_TABLE = {0,0,1,1,2,2,3,3,4,4,4,5};

	/** garbage blockChanges to the position of the holes in the normally random */
	private final int GARBAGE_TYPE_NORMAL = 0;

	/** garbage blockThe position of the holes in the1I would not change my time at the rising auction */
	private final int GARBAGE_TYPE_NOCHANGE_ONE_RISE = 1;

	/** garbage blockThe position of the holes in the1Of times Attack I will not change(2If you change more than once) */
	private final int GARBAGE_TYPE_NOCHANGE_ONE_ATTACK = 2;

	/** garbage blockThe display name of the type */
	private final String[] GARBAGE_TYPE_STRING = {"NORMAL", "ONE RISE", "1-ATTACK"};

	/** Each player's garbage block color */
	private final int[] PLAYER_COLOR_BLOCK = {Block.BLOCK_COLOR_RED, Block.BLOCK_COLOR_BLUE};

	/** Each player's frame color */
	private final int[] PLAYER_COLOR_FRAME = {GameEngine.FRAME_COLOR_RED, GameEngine.FRAME_COLOR_BLUE};

	/** garbage blockType of */
	private int[] garbageType;

	/** Rate of change of garbage holes */
	private int[] garbagePercent;

	/** Allow garbage countering */
	private boolean[] garbageCounter;

	/** Allow garbage blocking */
	private boolean[] garbageBlocking;

	/** Has accumulatedgarbage blockOfcount */
	private int[] garbage;

	/** Had sentgarbage blockOfcount */
	private int[] garbageSent;

	/** Last garbage hole position */
	private int[] lastHole;

	/** Time to display the most recent increase in score */
	private int[] scgettime;

	/** Most recent scoring event type */
	private int[] lastevent;

	/** Most recent scoring eventInB2BIf it&#39;s the casetrue */
	private boolean[] lastb2b;

	/** Most recent scoring eventInCombocount */
	private int[] lastcombo;

	/** Most recent scoring eventPeace inID */
	private int[] lastpiece;

	/** UseBGM */
	private int bgmno;

	/** Flag for types of T-Spins allowed (0=none, 1=normal, 2=all spin) */
	private int[] tspinEnableType;

	/** Old flag for allowing T-Spins */
	private boolean[] enableTSpin;

	/** Flag for enabling wallkick T-Spins */
	private boolean[] enableTSpinKick;

	/** Spin check type (4Point or Immobile) */
	private int[] spinCheckType;

	/** Immobile EZ spin */
	private boolean[] tspinEnableEZ;

	/** B2B Type (0=OFF 1=ON 2=ON+Separated-garbage) */
	private int[] b2bType;

	/** Flag for enabling combos */
	private boolean[] enableCombo;

	/** Big */
	private boolean[] big;

	/** Sound effectsON/OFF */
	private boolean[] enableSE;

	/** HurryupSeconds before the startcount(-1InHurryupNo) */
	private int[] hurryupSeconds;

	/** HurryupTimes afterBlockDo you run up the floor every time you put the */
	private int[] hurryupInterval;

	/** MapUse flag */
	private boolean[] useMap;

	/** UseMapSet number */
	private int[] mapSet;

	/** Map number(-1Random in) */
	private int[] mapNumber;

	/** Last preset number used */
	private int[] presetNumber;

	/** True if display detailed stats */
	private boolean showStats;

	/** Winner */
	private int winnerID;

	/** I was sent from the enemygarbage blockA list of */
	private LinkedList<GarbageEntry>[] garbageEntries;

	/** HurryupAfterBlockI put count */
	private int[] hurryupCount;

	/** MapSets ofProperty file */
	private CustomProperties[] propMap;

	/** MaximumMap number */
	private int[] mapMaxNo;

	/** For backupfield (MapUsed to save the replay) */
	private Field[] fldBackup;

	/** MapRan for selectioncount */
	private Random randMap;

	/** Win count for each player */
	private int[] winCount;

	/** Version */
	private int version;

	/*
	 * Mode name
	 */
	@Override
	public String getName() {
		return "VS-BATTLE";
	}

	@Override
	public boolean isVSMode() {
		return true;
	}

	/*
	 * Number of players
	 */
	@Override
	public int getPlayers() {
		return MAX_PLAYERS;
	}

	/*
	 * Mode initialization
	 */
	@Override
	public void modeInit(GameManager manager) {
		owner = manager;
		receiver = owner.receiver;

		garbageType = new int[MAX_PLAYERS];
		garbagePercent = new int[MAX_PLAYERS];
		garbageCounter = new boolean[MAX_PLAYERS];
		garbageBlocking = new boolean[MAX_PLAYERS];
		garbage = new int[MAX_PLAYERS];
		garbageSent = new int[MAX_PLAYERS];
		lastHole = new int[MAX_PLAYERS];
		scgettime = new int[MAX_PLAYERS];
		lastevent = new int[MAX_PLAYERS];
		lastb2b = new boolean[MAX_PLAYERS];
		lastcombo = new int[MAX_PLAYERS];
		lastpiece = new int[MAX_PLAYERS];
		bgmno = 0;
		tspinEnableType = new int[MAX_PLAYERS];
		enableTSpin = new boolean[MAX_PLAYERS];
		enableTSpinKick = new boolean[MAX_PLAYERS];
		spinCheckType = new int[MAX_PLAYERS];
		tspinEnableEZ = new boolean[MAX_PLAYERS];
		b2bType = new int[MAX_PLAYERS];
		enableCombo = new boolean[MAX_PLAYERS];
		big = new boolean[MAX_PLAYERS];
		enableSE = new boolean[MAX_PLAYERS];
		hurryupSeconds = new int[MAX_PLAYERS];
		hurryupInterval = new int[MAX_PLAYERS];
		useMap = new boolean[MAX_PLAYERS];
		mapSet = new int[MAX_PLAYERS];
		mapNumber = new int[MAX_PLAYERS];
		presetNumber = new int[MAX_PLAYERS];
		garbageEntries = new LinkedList[MAX_PLAYERS];
		hurryupCount = new int[MAX_PLAYERS];
		propMap = new CustomProperties[MAX_PLAYERS];
		mapMaxNo = new int[MAX_PLAYERS];
		fldBackup = new Field[MAX_PLAYERS];
		randMap = new Random();
		winCount = new int[MAX_PLAYERS];
		winnerID = -1;
	}

	/**
	 * Read speed presets
	 * @param engine GameEngine
	 * @param prop Property file to read from
	 * @param preset Preset number
	 */
	private void loadPreset(GameEngine engine, CustomProperties prop, int preset) {
		engine.speed.gravity = prop.getProperty("vsbattle.gravity." + preset, 4);
		engine.speed.denominator = prop.getProperty("vsbattle.denominator." + preset, 256);
		engine.speed.are = prop.getProperty("vsbattle.are." + preset, 0);
		engine.speed.areLine = prop.getProperty("vsbattle.areLine." + preset, 0);
		engine.speed.lineDelay = prop.getProperty("vsbattle.lineDelay." + preset, 0);
		engine.speed.lockDelay = prop.getProperty("vsbattle.lockDelay." + preset, 30);
		engine.speed.das = prop.getProperty("vsbattle.das." + preset, 14);
	}

	/**
	 * Save speed presets
	 * @param engine GameEngine
	 * @param prop Property file to save to
	 * @param preset Preset number
	 */
	private void savePreset(GameEngine engine, CustomProperties prop, int preset) {
		prop.setProperty("vsbattle.gravity." + preset, engine.speed.gravity);
		prop.setProperty("vsbattle.denominator." + preset, engine.speed.denominator);
		prop.setProperty("vsbattle.are." + preset, engine.speed.are);
		prop.setProperty("vsbattle.areLine." + preset, engine.speed.areLine);
		prop.setProperty("vsbattle.lineDelay." + preset, engine.speed.lineDelay);
		prop.setProperty("vsbattle.lockDelay." + preset, engine.speed.lockDelay);
		prop.setProperty("vsbattle.das." + preset, engine.speed.das);
	}

	/**
	 * Load settings not related to speeds
	 * @param engine GameEngine
	 * @param prop Property file to read from
	 */
	private void loadOtherSetting(GameEngine engine, CustomProperties prop) {
		int playerID = engine.playerID;
		bgmno = prop.getProperty("vsbattle.bgmno", 0);
		if(version >= 5) {
			garbageType[playerID] = prop.getProperty("vsbattle.garbageType.p" + playerID, GARBAGE_TYPE_NOCHANGE_ONE_ATTACK);
		} else {
			garbageType[playerID] = prop.getProperty("vsbattle.garbageType", GARBAGE_TYPE_NOCHANGE_ONE_ATTACK);
		}
		garbagePercent[playerID] = prop.getProperty("vsbattle.garbagePercent.p" + playerID, 100);
		garbageCounter[playerID] = prop.getProperty("vsbattle.garbageCounter.p" + playerID, true);
		garbageBlocking[playerID] = prop.getProperty("vsbattle.garbageBlocking.p" + playerID, true);
		tspinEnableType[playerID] = prop.getProperty("vsbattle.tspinEnableType.p" + playerID, 1);
		enableTSpin[playerID] = prop.getProperty("vsbattle.enableTSpin.p" + playerID, true);
		enableTSpinKick[playerID] = prop.getProperty("vsbattle.enableTSpinKick.p" + playerID, true);
		spinCheckType[playerID] = prop.getProperty("vsbattle.spinCheckType.p" + playerID, 0);
		tspinEnableEZ[playerID] = prop.getProperty("vsbattle.tspinEnableEZ.p" + playerID, false);
		if(version >= 5) {
			b2bType[playerID] = prop.getProperty("vsbattle.b2bType.p" + playerID, 1);
		} else {
			boolean b = prop.getProperty("vsbattle.enableB2B.p" + playerID, true);
			b2bType[playerID] = b ? 1 : 0;
		}
		enableCombo[playerID] = prop.getProperty("vsbattle.enableCombo.p" + playerID, true);
		big[playerID] = prop.getProperty("vsbattle.big.p" + playerID, false);
		enableSE[playerID] = prop.getProperty("vsbattle.enableSE.p" + playerID, true);
		hurryupSeconds[playerID] = prop.getProperty("vsbattle.hurryupSeconds.p" + playerID, -1);
		hurryupInterval[playerID] = prop.getProperty("vsbattle.hurryupInterval.p" + playerID, 5);
		useMap[playerID] = prop.getProperty("vsbattle.useMap.p" + playerID, false);
		mapSet[playerID] = prop.getProperty("vsbattle.mapSet.p" + playerID, 0);
		mapNumber[playerID] = prop.getProperty("vsbattle.mapNumber.p" + playerID, -1);
		presetNumber[playerID] = prop.getProperty("vsbattle.presetNumber.p" + playerID, 0);
		showStats = prop.getProperty("vsbattle.showStats", true);
	}

	/**
	 * Save settings not related to speeds
	 * @param engine GameEngine
	 * @param prop Property file to save to
	 */
	private void saveOtherSetting(GameEngine engine, CustomProperties prop) {
		int playerID = engine.playerID;
		prop.setProperty("vsbattle.bgmno", bgmno);
		prop.setProperty("vsbattle.garbageType.p" + playerID, garbageType[playerID]);
		prop.setProperty("vsbattle.garbagePercent.p" + playerID, garbagePercent[playerID]);
		prop.setProperty("vsbattle.garbageCounter.p" + playerID, garbageCounter[playerID]);
		prop.setProperty("vsbattle.garbageBlocking.p" + playerID, garbageBlocking[playerID]);
		prop.setProperty("vsbattle.tspinEnableType.p" + playerID, tspinEnableType[playerID]);
		prop.setProperty("vsbattle.enableTSpin.p" + playerID, enableTSpin[playerID]);
		prop.setProperty("vsbattle.enableTSpinKick.p" + playerID, enableTSpinKick[playerID]);
		prop.setProperty("vsbattle.spinCheckType.p" + playerID, spinCheckType[playerID]);
		prop.setProperty("vsbattle.tspinEnableEZ.p" + playerID, tspinEnableEZ[playerID]);
		prop.setProperty("vsbattle.b2bType.p" + playerID, b2bType[playerID]);
		prop.setProperty("vsbattle.enableCombo.p" + playerID, enableCombo[playerID]);
		prop.setProperty("vsbattle.big.p" + playerID, big[playerID]);
		prop.setProperty("vsbattle.enableSE.p" + playerID, enableSE[playerID]);
		prop.setProperty("vsbattle.hurryupSeconds.p" + playerID, hurryupSeconds[playerID]);
		prop.setProperty("vsbattle.hurryupInterval.p" + playerID, hurryupInterval[playerID]);
		prop.setProperty("vsbattle.useMap.p" + playerID, useMap[playerID]);
		prop.setProperty("vsbattle.mapSet.p" + playerID, mapSet[playerID]);
		prop.setProperty("vsbattle.mapNumber.p" + playerID, mapNumber[playerID]);
		prop.setProperty("vsbattle.presetNumber.p" + playerID, presetNumber[playerID]);
		prop.setProperty("vsbattle.showStats", showStats);
	}

	/**
	 * MapRead
	 * @param field field
	 * @param prop Property file to read from
	 * @param preset AnyID
	 */
	private void loadMap(Field field, CustomProperties prop, int id) {
		field.reset();
		//field.readProperty(prop, id);
		field.stringToField(prop.getProperty("map." + id, ""));
		field.setAllAttribute(Block.BLOCK_ATTRIBUTE_VISIBLE, true);
		field.setAllAttribute(Block.BLOCK_ATTRIBUTE_OUTLINE, true);
		field.setAllAttribute(Block.BLOCK_ATTRIBUTE_SELFPLACED, false);
	}

	/**
	 * MapSave
	 * @param field field
	 * @param prop Property file to save to
	 * @param id AnyID
	 */
	private void saveMap(Field field, CustomProperties prop, int id) {
		//field.writeProperty(prop, id);
		prop.setProperty("map." + id, field.fieldToString());
	}

	/**
	 * I have now accumulatedgarbage blockOfcountReturns
	 * @param playerID Player ID
	 * @return I have now accumulatedgarbage blockOfcount
	 */
	private int getTotalGarbageLines(int playerID) {
		int count = 0;
		for(GarbageEntry garbageEntry: garbageEntries[playerID]) {
			count += garbageEntry.lines;
		}
		return count;
	}

	/**
	 * For previewMapRead
	 * @param engine GameEngine
	 * @param playerID Player number
	 * @param id MapID
	 * @param forceReload trueWhen youMapForce Reload the file
	 */
	private void loadMapPreview(GameEngine engine, int playerID, int id, boolean forceReload) {
		if((propMap[playerID] == null) || (forceReload)) {
			mapMaxNo[playerID] = 0;
			propMap[playerID] = receiver.loadProperties("config/map/vsbattle/" + mapSet[playerID] + ".map");
		}

		if((propMap[playerID] == null) && (engine.field != null)) {
			engine.field.reset();
		} else if(propMap[playerID] != null) {
			mapMaxNo[playerID] = propMap[playerID].getProperty("map.maxMapNumber", 0);
			engine.createFieldIfNeeded();
			loadMap(engine.field, propMap[playerID], id);
			engine.field.setAllSkin(engine.getSkin());
		}
	}

	/*
	 * Initialization for each player
	 */
	@Override
	public void playerInit(GameEngine engine, int playerID) {
		if(playerID == 1) {
			engine.randSeed = owner.engine[0].randSeed;
			engine.random = new Random(owner.engine[0].randSeed);
		}

		engine.framecolor = PLAYER_COLOR_FRAME[playerID];

		garbage[playerID] = 0;
		garbageSent[playerID] = 0;
		lastHole[playerID] = -1;
		scgettime[playerID] = 0;
		lastevent[playerID] = EVENT_NONE;
		lastb2b[playerID] = false;
		lastcombo[playerID] = 0;

		garbageEntries[playerID] = new LinkedList<GarbageEntry>();

		hurryupCount[playerID] = 0;

		if(engine.owner.replayMode == false) {
			version = CURRENT_VERSION;
			loadOtherSetting(engine, engine.owner.modeConfig);
			loadPreset(engine, engine.owner.modeConfig, -1 - playerID);
		} else {
			version = owner.replayProp.getProperty("vsbattle.version", 0);
			loadOtherSetting(engine, engine.owner.replayProp);
			loadPreset(engine, engine.owner.replayProp, -1 - playerID);
		}
	}

	/*
	 * Called at settings screen
	 */
	@Override
	public boolean onSetting(GameEngine engine, int playerID) {
		// Menu
		if((engine.owner.replayMode == false) && (engine.statc[4] == 0)) {
			// Configuration changes
			int change = updateCursor(engine, 27, playerID);

			if(change != 0) {
				engine.playSE("change");

				int m = 1;
				if(engine.ctrl.isPress(Controller.BUTTON_E)) m = 100;
				if(engine.ctrl.isPress(Controller.BUTTON_F)) m = 1000;

				switch(menuCursor) {
				case 0:
					engine.speed.gravity += change * m;
					if(engine.speed.gravity < -1) engine.speed.gravity = 99999;
					if(engine.speed.gravity > 99999) engine.speed.gravity = -1;
					break;
				case 1:
					engine.speed.denominator += change * m;
					if(engine.speed.denominator < -1) engine.speed.denominator = 99999;
					if(engine.speed.denominator > 99999) engine.speed.denominator = -1;
					break;
				case 2:
					engine.speed.are += change;
					if(engine.speed.are < 0) engine.speed.are = 99;
					if(engine.speed.are > 99) engine.speed.are = 0;
					break;
				case 3:
					engine.speed.areLine += change;
					if(engine.speed.areLine < 0) engine.speed.areLine = 99;
					if(engine.speed.areLine > 99) engine.speed.areLine = 0;
					break;
				case 4:
					engine.speed.lineDelay += change;
					if(engine.speed.lineDelay < 0) engine.speed.lineDelay = 99;
					if(engine.speed.lineDelay > 99) engine.speed.lineDelay = 0;
					break;
				case 5:
					engine.speed.lockDelay += change;
					if(engine.speed.lockDelay < 0) engine.speed.lockDelay = 99;
					if(engine.speed.lockDelay > 99) engine.speed.lockDelay = 0;
					break;
				case 6:
					engine.speed.das += change;
					if(engine.speed.das < 0) engine.speed.das = 99;
					if(engine.speed.das > 99) engine.speed.das = 0;
					break;
				case 7:
				case 8:
					presetNumber[playerID] += change;
					if(presetNumber[playerID] < 0) presetNumber[playerID] = 99;
					if(presetNumber[playerID] > 99) presetNumber[playerID] = 0;
					break;
				case 9:
					garbageType[playerID] += change;
					if(garbageType[playerID] < 0) garbageType[playerID] = 2;
					if(garbageType[playerID] > 2) garbageType[playerID] = 0;
					break;
				case 10:
					garbagePercent[playerID] += change;
					if(garbagePercent[playerID] < 0) garbagePercent[playerID] = 100;
					if(garbagePercent[playerID] > 100) garbagePercent[playerID] = 0;
					break;
				case 11:
					garbageCounter[playerID] = !garbageCounter[playerID];
					break;
				case 12:
					garbageBlocking[playerID] = !garbageBlocking[playerID];
					break;
				case 13:
					//enableTSpin[playerID] = !enableTSpin[playerID];
					tspinEnableType[playerID] += change;
					if(tspinEnableType[playerID] < 0) tspinEnableType[playerID] = 2;
					if(tspinEnableType[playerID] > 2) tspinEnableType[playerID] = 0;
					break;
				case 14:
					enableTSpinKick[playerID] = !enableTSpinKick[playerID];
					break;
				case 15:
					spinCheckType[playerID] += change;
					if(spinCheckType[playerID] < 0) spinCheckType[playerID] = 1;
					if(spinCheckType[playerID] > 1) spinCheckType[playerID] = 0;
					break;
				case 16:
					tspinEnableEZ[playerID] = !tspinEnableEZ[playerID];
					break;
				case 17:
					//enableB2B[playerID] = !enableB2B[playerID];
					b2bType[playerID] += change;
					if(b2bType[playerID] < 0) b2bType[playerID] = 2;
					if(b2bType[playerID] > 2) b2bType[playerID] = 0;
					break;
				case 18:
					enableCombo[playerID] = !enableCombo[playerID];
					break;
				case 19:
					big[playerID] = !big[playerID];
					break;
				case 20:
					enableSE[playerID] = !enableSE[playerID];
					break;
				case 21:
					hurryupSeconds[playerID] += change;
					if(hurryupSeconds[playerID] < -1) hurryupSeconds[playerID] = 300;
					if(hurryupSeconds[playerID] > 300) hurryupSeconds[playerID] = -1;
					break;
				case 22:
					hurryupInterval[playerID] += change;
					if(hurryupInterval[playerID] < 1) hurryupInterval[playerID] = 99;
					if(hurryupInterval[playerID] > 99) hurryupInterval[playerID] = 1;
					break;
				case 23:
					bgmno += change;
					if(bgmno < 0) bgmno = BGMStatus.BGM_COUNT - 1;
					if(bgmno > BGMStatus.BGM_COUNT - 1) bgmno = 0;
					break;
				case 24:
					showStats = !showStats;
					break;
				case 25:
					useMap[playerID] = !useMap[playerID];
					if(!useMap[playerID]) {
						if(engine.field != null) engine.field.reset();
					} else {
						loadMapPreview(engine, playerID, (mapNumber[playerID] < 0) ? 0 : mapNumber[playerID], true);
					}
					break;
				case 26:
					mapSet[playerID] += change;
					if(mapSet[playerID] < 0) mapSet[playerID] = 99;
					if(mapSet[playerID] > 99) mapSet[playerID] = 0;
					if(useMap[playerID]) {
						mapNumber[playerID] = -1;
						loadMapPreview(engine, playerID, (mapNumber[playerID] < 0) ? 0 : mapNumber[playerID], true);
					}
					break;
				case 27:
					if(useMap[playerID]) {
						mapNumber[playerID] += change;
						if(mapNumber[playerID] < -1) mapNumber[playerID] = mapMaxNo[playerID] - 1;
						if(mapNumber[playerID] > mapMaxNo[playerID] - 1) mapNumber[playerID] = -1;
						loadMapPreview(engine, playerID, (mapNumber[playerID] < 0) ? 0 : mapNumber[playerID], true);
					} else {
						mapNumber[playerID] = -1;
					}
					break;
				}
			}

			// 決定
			if(engine.ctrl.isPush(Controller.BUTTON_A) && (menuTime >= 5)) {
				engine.playSE("decide");

				if(menuCursor == 7) {
					loadPreset(engine, owner.modeConfig, presetNumber[playerID]);
				} else if(menuCursor == 8) {
					savePreset(engine, owner.modeConfig, presetNumber[playerID]);
					receiver.saveModeConfig(owner.modeConfig);
				} else {
					saveOtherSetting(engine, owner.modeConfig);
					savePreset(engine, owner.modeConfig, -1 - playerID);
					receiver.saveModeConfig(owner.modeConfig);
					engine.statc[4] = 1;
				}
			}

			// Cancel
			if(engine.ctrl.isPush(Controller.BUTTON_B)) {
				engine.quitflag = true;
			}

			// プレビュー用Map読み込み
			if(useMap[playerID] && (menuTime == 0)) {
				loadMapPreview(engine, playerID, (mapNumber[playerID] < 0) ? 0 : mapNumber[playerID], true);
			}

			// Random map preview
			if(useMap[playerID] && (propMap[playerID] != null) && (mapNumber[playerID] < 0)) {
				if(menuTime % 30 == 0) {
					engine.statc[5]++;
					if(engine.statc[5] >= mapMaxNo[playerID]) engine.statc[5] = 0;
					loadMapPreview(engine, playerID, engine.statc[5], false);
				}
			}

			menuTime++;
		} else if(engine.statc[4] == 0) {
			menuTime++;
			menuCursor = 0;

			if(menuTime >= 60) {
				menuCursor = 9;
			}
			if(menuTime >= 120) {
				engine.statc[4] = 1;
			}
		} else {
			// Start
			if((owner.engine[0].statc[4] == 1) && (owner.engine[1].statc[4] == 1) && (playerID == 1)) {
				owner.engine[0].stat = GameEngine.Status.READY;
				owner.engine[1].stat = GameEngine.Status.READY;
				owner.engine[0].resetStatc();
				owner.engine[1].resetStatc();
			}
			// Cancel
			else if(engine.ctrl.isPush(Controller.BUTTON_B)) {
				engine.statc[4] = 0;
			}
		}

		return true;
	}

	/*
	 * Setting screen drawing
	 */
	@Override
	public void renderSetting(GameEngine engine, int playerID) {
		if(engine.statc[4] == 0) {
			if(menuCursor < 9) {
				drawMenu(engine, playerID, receiver, 0, EventReceiver.COLOR_ORANGE, 0,
						"GRAVITY", String.valueOf(engine.speed.gravity),
						"G-MAX", String.valueOf(engine.speed.denominator),
						"ARE", String.valueOf(engine.speed.are),
						"ARE LINE", String.valueOf(engine.speed.areLine),
						"LINE DELAY", String.valueOf(engine.speed.lineDelay),
						"LOCK DELAY", String.valueOf(engine.speed.lockDelay),
						"DAS", String.valueOf(engine.speed.das));
				drawMenu(engine, playerID, receiver, 14, EventReceiver.COLOR_GREEN, 7,
						"LOAD", String.valueOf(presetNumber[playerID]),
						"SAVE", String.valueOf(presetNumber[playerID]));
			} else if(menuCursor < 19) {
				String strTSpinEnable = "";
				if(version >= 4) {
					if(tspinEnableType[playerID] == 0) strTSpinEnable = "OFF";
					if(tspinEnableType[playerID] == 1) strTSpinEnable = "T-ONLY";
					if(tspinEnableType[playerID] == 2) strTSpinEnable = "ALL";
				} else {
					strTSpinEnable = GeneralUtil.getONorOFF(enableTSpin[playerID]);
				}
				String strB2BType = "";
				if(b2bType[playerID] == 0) strB2BType = "OFF";
				if(b2bType[playerID] == 1) strB2BType = "ON";
				if(b2bType[playerID] == 2) strB2BType = "SEPARATE";
				drawMenu(engine, playerID, receiver, 0, EventReceiver.COLOR_CYAN, 9,
						"GARBAGE", GARBAGE_TYPE_STRING[garbageType[playerID]],
						"CHANGERATE", garbagePercent[playerID] + "%",
						"COUNTERING", GeneralUtil.getONorOFF(garbageCounter[playerID]),
						"BLOCKING", GeneralUtil.getONorOFF(garbageBlocking[playerID]),
						"SPIN BONUS", strTSpinEnable,
						"KICK SPIN", GeneralUtil.getONorOFF(enableTSpinKick[playerID]),
						"SPIN TYPE", (spinCheckType[playerID] == 0) ? "4POINT" : "IMMOBILE",
						"EZIMMOBILE", GeneralUtil.getONorOFF(tspinEnableEZ[playerID]),
						"B2B", strB2BType,
						"COMBO",  GeneralUtil.getONorOFF(enableCombo[playerID]));
			} else {
				drawMenu(engine, playerID, receiver, 0, EventReceiver.COLOR_CYAN, 19,
						"BIG", GeneralUtil.getONorOFF(big[playerID]),
						"SE", GeneralUtil.getONorOFF(enableSE[playerID]),
						"HURRYUP", (hurryupSeconds[playerID] == -1) ? "NONE" : hurryupSeconds[playerID]+"SEC",
						"INTERVAL", String.valueOf(hurryupInterval[playerID]));
				drawMenu(engine, playerID, receiver, 8, EventReceiver.COLOR_PINK, 23,
						"BGM", String.valueOf(bgmno),
						"SHOW STATS", GeneralUtil.getONorOFF(showStats));
				drawMenu(engine, playerID, receiver, 12, EventReceiver.COLOR_CYAN, 25,
						"USE MAP", GeneralUtil.getONorOFF(useMap[playerID]),
						"MAP SET", String.valueOf(mapSet[playerID]),
						"MAP NO.", (mapNumber[playerID] < 0) ? "RANDOM" : mapNumber[playerID]+"/"+(mapMaxNo[playerID]-1));
			}
		} else {
			receiver.drawMenuFont(engine, playerID, 3, 10, "WAIT", EventReceiver.COLOR_YELLOW);
		}
	}

	/*
	 * Called for initialization during Ready (before initialization)
	 */
	@Override
	public boolean onReady(GameEngine engine, int playerID) {
		if(engine.statc[0] == 0) {
			// MapFor storing backup Replay read
			if(version >= 3) {
				if(useMap[playerID]) {
					if(owner.replayMode) {
						engine.createFieldIfNeeded();
						loadMap(engine.field, owner.replayProp, playerID);
						engine.field.setAllSkin(engine.getSkin());
					} else {
						if(propMap[playerID] == null) {
							propMap[playerID] = receiver.loadProperties("config/map/vsbattle/" + mapSet[playerID] + ".map");
						}

						if(propMap[playerID] != null) {
							engine.createFieldIfNeeded();

							if(mapNumber[playerID] < 0) {
								if((playerID == 1) && (useMap[0]) && (mapNumber[0] < 0)) {
									engine.field.copy(owner.engine[0].field);
								} else {
									int no = (mapMaxNo[playerID] < 1) ? 0 : randMap.nextInt(mapMaxNo[playerID]);
									loadMap(engine.field, propMap[playerID], no);
								}
							} else {
								loadMap(engine.field, propMap[playerID], mapNumber[playerID]);
							}

							engine.field.setAllSkin(engine.getSkin());
							fldBackup[playerID] = new Field(engine.field);
						}
					}
				} else if(engine.field != null) {
					engine.field.reset();
				}
			}
		}

		return false;
	}

	/*
	 * Called at game start
	 */
	@Override
	public void startGame(GameEngine engine, int playerID) {
		engine.b2bEnable = (b2bType[playerID] >= 1);
		engine.comboType = enableCombo[playerID] ? GameEngine.COMBO_TYPE_NORMAL : GameEngine.COMBO_TYPE_DISABLE;
		engine.big = big[playerID];
		engine.enableSE = enableSE[playerID];
		if(playerID == 1) owner.bgmStatus.bgm = bgmno;

		engine.tspinAllowKick = enableTSpinKick[playerID];
		if(version >= 4) {
			if(tspinEnableType[playerID] == 0) {
				engine.tspinEnable = false;
				engine.useAllSpinBonus = false;
			} else if(tspinEnableType[playerID] == 1) {
				engine.tspinEnable = true;
				engine.useAllSpinBonus = false;
			} else if(tspinEnableType[playerID] == 2) {
				engine.tspinEnable = true;
				engine.useAllSpinBonus = true;
			}
		} else {
			engine.tspinEnable = enableTSpin[playerID];
		}

		if(version >= 5) {
			engine.spinCheckType = spinCheckType[playerID];
			engine.tspinEnableEZ = tspinEnableEZ[playerID];
		}
	}

	/*
	 * Render score
	 */
	@Override
	public void renderLast(GameEngine engine, int playerID) {
		// Status display
		if(playerID == 0) {
			receiver.drawDirectFont(engine, playerID, 256, 16, GeneralUtil.getTime(engine.statistics.time));

			if((hurryupSeconds[playerID] >= 0) && (engine.timerActive) &&
			   (engine.statistics.time >= hurryupSeconds[playerID] * 60) && (engine.statistics.time < (hurryupSeconds[playerID] + 5) * 60))
			{
				receiver.drawDirectFont(engine, playerID, 256 - 8, 32, "HURRY UP!", (engine.statistics.time % 2 == 0));
			}
		}

		if((playerID == 0) && (owner.receiver.getNextDisplayType() != 2) && (showStats)) {
			receiver.drawScoreFont(engine, playerID, 0, 0, "VS-BATTLE", EventReceiver.COLOR_ORANGE);

			receiver.drawScoreFont(engine, playerID, 0, 2, "1P ATTACK", EventReceiver.COLOR_RED);
			receiver.drawScoreFont(engine, playerID, 0, 3, String.valueOf(garbageSent[0]));

			receiver.drawScoreFont(engine, playerID, 0, 5, "2P ATTACK", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 0, 6, String.valueOf(garbageSent[1]));

			if(!owner.replayMode) {
				receiver.drawScoreFont(engine, playerID, 0, 8, "1P WINS", EventReceiver.COLOR_RED);
				receiver.drawScoreFont(engine, playerID, 0, 9, String.valueOf(winCount[0]));

				receiver.drawScoreFont(engine, playerID, 0, 11, "2P WINS", EventReceiver.COLOR_BLUE);
				receiver.drawScoreFont(engine, playerID, 0, 12, String.valueOf(winCount[1]));
			}
		}

		if(showStats) {
			int x = receiver.getFieldDisplayPositionX(engine, playerID);
			int y = receiver.getFieldDisplayPositionY(engine, playerID);
			int fontColor = EventReceiver.COLOR_WHITE;

			if(garbage[playerID] > 0) {
				if(garbage[playerID] >= 1) fontColor = EventReceiver.COLOR_YELLOW;
				if(garbage[playerID] >= 3) fontColor = EventReceiver.COLOR_ORANGE;
				if(garbage[playerID] >= 4) fontColor = EventReceiver.COLOR_RED;

				String strTempGarbage = String.format("%5d", garbage[playerID]);
				receiver.drawDirectFont(engine, playerID, x + 96, y + 372, strTempGarbage, fontColor);
			}

			if(owner.receiver.getNextDisplayType() == 2) {
				fontColor = (playerID == 0) ? EventReceiver.COLOR_RED : EventReceiver.COLOR_BLUE;

				receiver.drawDirectFont(engine, playerID, x - 48, y + 120, "TOTAL", fontColor, 0.5f);
				receiver.drawDirectFont(engine, playerID, x - 52, y + 128, "ATTACK", fontColor, 0.5f);
				if(garbageSent[playerID] >= 10)
					receiver.drawDirectFont(engine, playerID, x - 44, y + 142, String.valueOf(garbageSent[playerID]));
				else
					receiver.drawDirectFont(engine, playerID, x - 36, y + 142, String.valueOf(garbageSent[playerID]));

				receiver.drawDirectFont(engine, playerID, x - 44, y + 190, "WINS", fontColor, 0.5f);
				if(winCount[playerID] >= 10)
					receiver.drawDirectFont(engine, playerID, x - 44, y + 204, String.valueOf(winCount[playerID]));
				else
					receiver.drawDirectFont(engine, playerID, x - 36, y + 204, String.valueOf(winCount[playerID]));
			}
		}

		// Line clear event Display
		if((lastevent[playerID] != EVENT_NONE) && (scgettime[playerID] < 120)) {
			String strPieceName = Piece.getPieceName(lastpiece[playerID]);

			switch(lastevent[playerID]) {
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
				if(lastb2b[playerID]) receiver.drawMenuFont(engine, playerID, 3, 21, "FOUR", EventReceiver.COLOR_RED);
				else receiver.drawMenuFont(engine, playerID, 3, 21, "FOUR", EventReceiver.COLOR_ORANGE);
				break;
			case EVENT_TSPIN_SINGLE_MINI:
				if(lastb2b[playerID]) receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-MINI-S", EventReceiver.COLOR_RED);
				else receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-MINI-S", EventReceiver.COLOR_ORANGE);
				break;
			case EVENT_TSPIN_SINGLE:
				if(lastb2b[playerID]) receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-SINGLE", EventReceiver.COLOR_RED);
				else receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-SINGLE", EventReceiver.COLOR_ORANGE);
				break;
			case EVENT_TSPIN_DOUBLE_MINI:
				if(lastb2b[playerID]) receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-MINI-D", EventReceiver.COLOR_RED);
				else receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-MINI-D", EventReceiver.COLOR_ORANGE);
				break;
			case EVENT_TSPIN_DOUBLE:
				if(lastb2b[playerID]) receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-DOUBLE", EventReceiver.COLOR_RED);
				else receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-DOUBLE", EventReceiver.COLOR_ORANGE);
				break;
			case EVENT_TSPIN_TRIPLE:
				if(lastb2b[playerID]) receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-TRIPLE", EventReceiver.COLOR_RED);
				else receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-TRIPLE", EventReceiver.COLOR_ORANGE);
				break;
			case EVENT_TSPIN_EZ:
				if(lastb2b[playerID]) receiver.drawMenuFont(engine, playerID, 3, 21, "EZ-" + strPieceName, EventReceiver.COLOR_RED);
				else receiver.drawMenuFont(engine, playerID, 3, 21, "EZ-" + strPieceName, EventReceiver.COLOR_ORANGE);
				break;
			}

			if(lastcombo[playerID] >= 2)
				receiver.drawMenuFont(engine, playerID, 2, 22, (lastcombo[playerID] - 1) + "COMBO", EventReceiver.COLOR_CYAN);
		}
	}

	/*
	 * Calculate score
	 */
	@Override
	public void calcScore(GameEngine engine, int playerID, int lines) {
		int enemyID = 0;
		if(playerID == 0) enemyID = 1;

		//  Attack
		if(lines > 0) {
			int pts = 0;
			int ptsB2B = 0;
			scgettime[playerID] = 0;

			if(engine.tspin) {
				// Immobile EZ Spin
				if(engine.tspinez) {
					if(engine.useAllSpinBonus) {
						//pts += 0;
					} else {
						pts += 1;
					}
					lastevent[playerID] = EVENT_TSPIN_EZ;
				}
				// T-Spin 1 line
				else if(lines == 1) {
					if(engine.tspinmini) {
						if(engine.useAllSpinBonus) {
							//pts += 0;
						} else {
							pts += 1;
						}
						lastevent[playerID] = EVENT_TSPIN_SINGLE_MINI;
					} else {
						pts += 2;
						lastevent[playerID] = EVENT_TSPIN_SINGLE;
					}
				}
				// T-Spin 2 lines
				else if(lines == 2) {
					if(engine.tspinmini && engine.useAllSpinBonus) {
						pts += 3;
						lastevent[playerID] = EVENT_TSPIN_DOUBLE_MINI;
					} else {
						pts += 4;
						lastevent[playerID] = EVENT_TSPIN_DOUBLE;
					}
				}
				// T-Spin 3 lines
				else if(lines >= 3) {
					pts += 6;
					lastevent[playerID] = EVENT_TSPIN_TRIPLE;
				}
			} else {
				if(lines == 1) {
					// 1Column
					lastevent[playerID] = EVENT_SINGLE;
				} else if(lines == 2) {
					pts += 1; // 2Column
					lastevent[playerID] = EVENT_DOUBLE;
				} else if(lines == 3) {
					pts += 2; // 3Column
					lastevent[playerID] = EVENT_TRIPLE;
				} else if(lines >= 4) {
					pts += 4; // 4 lines
					lastevent[playerID] = EVENT_FOUR;
				}
			}

			// B2B
			if(engine.b2b) {
				lastb2b[playerID] = true;

				if(pts > 0) {
					if((version >= 1) && (lastevent[playerID] == EVENT_TSPIN_TRIPLE) && (!engine.useAllSpinBonus))
						ptsB2B += 2;
					else
						ptsB2B += 1;

					if(b2bType[playerID] == 1)
						pts += ptsB2B;	// Non-separated B2B
				}
			} else {
				lastb2b[playerID] = false;
			}

			// Combo
			if(engine.comboType != GameEngine.COMBO_TYPE_DISABLE) {
				int cmbindex = engine.combo - 1;
				if(cmbindex < 0) cmbindex = 0;
				if(cmbindex >= COMBO_ATTACK_TABLE.length) cmbindex = COMBO_ATTACK_TABLE.length - 1;
				pts += COMBO_ATTACK_TABLE[cmbindex];
				lastcombo[playerID] = engine.combo;
			}

			// All clear
			if((lines >= 1) && (engine.field.isEmpty())) {
				engine.playSE("bravo");
				pts += 6;
			}

			// gem block attack
			pts += engine.field.getHowManyGemClears();

			lastpiece[playerID] = engine.nowPieceObject.id;

			/*
			if(pts > 0) {
				garbageSent[playerID] += pts;

				if(garbage[playerID] > 0) {
					// Offset
					garbage[playerID] -= pts;
					if(garbage[playerID] < 0) {
						// Ojama return
						garbage[enemyID] += Math.abs(garbage[playerID]);
						garbage[playerID] = 0;
					}
				} else {
					//  Attack
					garbage[enemyID] += pts;
				}
			}
			*/

			// Attack lines count
			garbageSent[playerID] += pts;
			if(b2bType[playerID] == 2) garbageSent[playerID] += ptsB2B;

			// Offset
			garbage[playerID] = getTotalGarbageLines(playerID);
			if((pts > 0) && (garbage[playerID] > 0) && (garbageCounter[playerID])) {
				while(!garbageEntries[playerID].isEmpty() && (pts > 0)) {
					GarbageEntry garbageEntry = garbageEntries[playerID].getFirst();
					garbageEntry.lines -= pts;

					if(garbageEntry.lines <= 0) {
						pts = Math.abs(garbageEntry.lines);
						garbageEntries[playerID].removeFirst();
					} else {
						pts = 0;
					}
				}
			}

			//  Attack
			if(pts > 0) {
				garbageEntries[enemyID].add(new GarbageEntry(pts, playerID));

				// Separated B2B
				if((b2bType[playerID] == 2) && (ptsB2B > 0)) {
					garbageEntries[enemyID].add(new GarbageEntry(ptsB2B, playerID));
				}

				garbage[enemyID] = getTotalGarbageLines(enemyID);

				if((owner.engine[enemyID].ai == null) && (garbage[enemyID] >= 4)) {
					owner.engine[enemyID].playSE("danger");
				}
			}
		}

		// Rising auction
		garbage[playerID] = getTotalGarbageLines(playerID);
		if( ((lines == 0) || (!garbageBlocking[playerID])) && (garbage[playerID] > 0) ) {
			engine.playSE("garbage");

			while(!garbageEntries[playerID].isEmpty()) {
				GarbageEntry garbageEntry = garbageEntries[playerID].poll();
				int garbageColor = PLAYER_COLOR_BLOCK[garbageEntry.playerID];

				if(garbageEntry.lines > 0) {
					int hole = lastHole[playerID];
					if((hole == -1) || (version <= 4)) {
						hole = engine.random.nextInt(engine.field.getWidth());
					}

					if(garbageType[playerID] == GARBAGE_TYPE_NORMAL) {
						// Change the normal hole position
						while(garbageEntry.lines > 0) {
							engine.field.addSingleHoleGarbage(hole, garbageColor, engine.getSkin(), 1);

							if(version >= 5) {
								if(engine.random.nextInt(100) < garbagePercent[playerID]) {
									hole = engine.random.nextInt(engine.field.getWidth());
								}
							} else {
								if(engine.random.nextInt(10) >= 7) {
									hole = engine.random.nextInt(engine.field.getWidth());
								}
							}

							garbageEntry.lines--;
						}
					} else if(garbageType[playerID] == GARBAGE_TYPE_NOCHANGE_ONE_RISE) {
						// 1Hole position does not change at the rising times of auction
						if(version >= 5) {
							if(engine.random.nextInt(100) < garbagePercent[playerID]) {
								int newHole = engine.random.nextInt(engine.field.getWidth() - 1);
								if(newHole >= hole) {
									newHole++;
								}
								hole = newHole;
							}
						} else {
							hole = engine.random.nextInt(engine.field.getWidth());
						}

						engine.field.addSingleHoleGarbage(hole, garbageColor, engine.getSkin(), garbage[playerID]);
						garbageEntries[playerID].clear();
						break;
					} else if(garbageType[playerID] == GARBAGE_TYPE_NOCHANGE_ONE_ATTACK) {
						// garbage blockThe position of the holes in the1Of times Attack I will not change(2If you change more than once)
						if(version >= 5) {
							if(engine.random.nextInt(100) < garbagePercent[playerID]) {
								int newHole = engine.random.nextInt(engine.field.getWidth() - 1);
								if(newHole >= hole) {
									newHole++;
								}
								hole = newHole;
							}
						} else {
							hole = engine.random.nextInt(engine.field.getWidth());
						}

						engine.field.addSingleHoleGarbage(hole, garbageColor, engine.getSkin(), garbageEntry.lines);
					}

					lastHole[playerID] = hole;
				}
			}

			garbage[playerID] = 0;
		}

		// HURRY UP!
		if(version >= 2) {
			if((hurryupSeconds[playerID] >= 0) && (engine.timerActive)) {
				if(engine.statistics.time >= hurryupSeconds[playerID] * 60) {
					hurryupCount[playerID]++;

					if(hurryupCount[playerID] % hurryupInterval[playerID] == 0) {
						engine.field.addHurryupFloor(1, engine.getSkin());
					}
				} else {
					hurryupCount[playerID] = hurryupInterval[playerID] - 1;
				}
			}
		} else {
			if((hurryupSeconds[playerID] >= 0) && (engine.timerActive) && (engine.statistics.time >= hurryupSeconds[playerID] * 60)) {
				hurryupCount[playerID]++;

				if(hurryupCount[playerID] % hurryupInterval[playerID] == 0) {
					engine.field.addHurryupFloor(1, engine.getSkin());
				}
			}
		}
	}

	/*
	 * Called after every frame
	 */
	@Override
	public void onLast(GameEngine engine, int playerID) {
		scgettime[playerID]++;

		// HURRY UP!
		if((playerID == 0) && (engine.timerActive) && (hurryupSeconds[playerID] >= 0) && (engine.statistics.time == hurryupSeconds[playerID] * 60)) {
			owner.receiver.playSE("hurryup");
		}

		// Rising auctionMeter
		if(garbage[playerID] * receiver.getBlockGraphicsHeight(engine, playerID) > engine.meterValue) {
			engine.meterValue += receiver.getBlockGraphicsHeight(engine, playerID) / 2;
		} else if(garbage[playerID] * receiver.getBlockGraphicsHeight(engine, playerID) < engine.meterValue) {
			engine.meterValue--;
		}
		if(garbage[playerID] >= 4) engine.meterColor = GameEngine.METER_COLOR_RED;
		else if(garbage[playerID] >= 3) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
		else if(garbage[playerID] >= 1) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
		else engine.meterColor = GameEngine.METER_COLOR_GREEN;

		// Settlement
		if((playerID == 1) && (owner.engine[0].gameActive)) {
			if((owner.engine[0].stat == GameEngine.Status.GAMEOVER) && (owner.engine[1].stat == GameEngine.Status.GAMEOVER)) {
				// Draw
				winnerID = -1;
				owner.engine[0].gameEnded();
				owner.engine[1].gameEnded();
				owner.bgmStatus.bgm = BGMStatus.BGM_NOTHING;
			} else if((owner.engine[0].stat != GameEngine.Status.GAMEOVER) && (owner.engine[1].stat == GameEngine.Status.GAMEOVER)) {
				// 1P win
				winnerID = 0;
				owner.engine[0].gameEnded();
				owner.engine[1].gameEnded();
				owner.engine[0].stat = GameEngine.Status.EXCELLENT;
				owner.engine[0].resetStatc();
				owner.engine[0].statc[1] = 1;
				owner.bgmStatus.bgm = BGMStatus.BGM_NOTHING;
				if(!owner.replayMode) winCount[0]++;
			} else if((owner.engine[0].stat == GameEngine.Status.GAMEOVER) && (owner.engine[1].stat != GameEngine.Status.GAMEOVER)) {
				// 2P win
				winnerID = 1;
				owner.engine[0].gameEnded();
				owner.engine[1].gameEnded();
				owner.engine[1].stat = GameEngine.Status.EXCELLENT;
				owner.engine[1].resetStatc();
				owner.engine[1].statc[1] = 1;
				owner.bgmStatus.bgm = BGMStatus.BGM_NOTHING;
				if(!owner.replayMode) winCount[1]++;
			}
		}
	}

	/*
	 * Render results screen
	 */
	@Override
	public void renderResult(GameEngine engine, int playerID) {
		receiver.drawMenuFont(engine, playerID, 0, 0, "RESULT", EventReceiver.COLOR_ORANGE);
		if(winnerID == -1) {
			receiver.drawMenuFont(engine, playerID, 6, 1, "DRAW", EventReceiver.COLOR_GREEN);
		} else if(winnerID == playerID) {
			receiver.drawMenuFont(engine, playerID, 6, 1, "WIN!", EventReceiver.COLOR_YELLOW);
		} else {
			receiver.drawMenuFont(engine, playerID, 6, 1, "LOSE", EventReceiver.COLOR_WHITE);
		}

		float apm = (float)(garbageSent[playerID] * 3600) / (float)(engine.statistics.time);
		float apl = 0f;
		if(engine.statistics.lines > 0) apl = (float)(garbageSent[playerID]) / (float)(engine.statistics.lines);

		drawResult(engine, playerID, receiver, 2, EventReceiver.COLOR_ORANGE,
				"ATTACK", String.format("%10d", garbageSent[playerID]));
		drawResultStats(engine, playerID, receiver, 4, EventReceiver.COLOR_ORANGE,
				Statistic.LINES, Statistic.PIECE);
		drawResult(engine, playerID, receiver, 8, EventReceiver.COLOR_ORANGE,
				"ATK/LINE", String.format("%10g", apl));
		drawResult(engine, playerID, receiver, 10, EventReceiver.COLOR_ORANGE,
				"ATTACK/MIN", String.format("%10g", apm));
		drawResultStats(engine, playerID, receiver, 12, EventReceiver.COLOR_ORANGE,
				Statistic.LPM, Statistic.PPS, Statistic.TIME);
	}

	/*
	 * Called when saving replay
	 */
	@Override
	public void saveReplay(GameEngine engine, int playerID, CustomProperties prop) {
		saveOtherSetting(engine, owner.replayProp);
		savePreset(engine, owner.replayProp, -1 - playerID);

		if(useMap[playerID] && (fldBackup[playerID] != null)) {
			saveMap(fldBackup[playerID], owner.replayProp, playerID);
		}

		owner.replayProp.setProperty("vsbattle.version", version);
	}

	/**
	 * I was sent from the enemygarbage blockOf data
	 */
	private class GarbageEntry {
		/** garbage blockcount */
		public int lines = 0;

		/** Source */
		public int playerID = 0;

		/**
		 * Constructor
		 */
		@SuppressWarnings("unused")
		public GarbageEntry() {
		}

		/**
		 * With parametersConstructor
		 * @param g garbage blockcount
		 */
		@SuppressWarnings("unused")
		public GarbageEntry(int g) {
			lines = g;
		}

		/**
		 * With parametersConstructor
		 * @param g garbage blockcount
		 * @param p Source
		 */
		public GarbageEntry(int g, int p) {
			lines = g;
			playerID = p;
		}
	}
}
