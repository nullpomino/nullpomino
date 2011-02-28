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

import java.io.IOException;
import java.util.LinkedList;
import java.util.Locale;

import mu.nu.nullpo.game.component.Block;
import mu.nu.nullpo.game.component.Controller;
import mu.nu.nullpo.game.component.Piece;
import mu.nu.nullpo.game.event.EventReceiver;
import mu.nu.nullpo.game.net.NetPlayerClient;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.gui.net.NetLobbyFrame;
import mu.nu.nullpo.util.GeneralUtil;

/**
 * NET-VS-BATTLE Mode (under constraction!)
 */
public class NetVSBattleMode extends NetDummyVSMode {
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

	/** Type of attack performed */
	private static final int ATTACK_CATEGORY_NORMAL = 0,
							 ATTACK_CATEGORY_B2B = 1,
							 ATTACK_CATEGORY_SPIN = 2,
							 ATTACK_CATEGORY_COMBO = 3,
							 ATTACK_CATEGORY_BRAVO = 4,
							 ATTACK_CATEGORY_GEM = 5,
							 ATTACK_CATEGORIES = 6;

	/** Attack table (for T-Spin only) */
	private static final int[][] LINE_ATTACK_TABLE =
	{
		// 1-2P, 3P, 4P, 5P, 6P
		{0, 0, 0, 0, 0},	// Single
		{1, 1, 0, 0, 0},	// Double
		{2, 2, 1, 1, 1},	// Triple
		{4, 3, 2, 2, 2},	// Four
		{1, 1, 0, 0, 0},	// T-Mini-S
		{2, 2, 1, 1, 1},	// T-Single
		{4, 3, 2, 2, 2},	// T-Double
		{6, 4, 3, 3, 3},	// T-Triple
		{4, 3, 2, 2, 2},	// T-Mini-D
		{1, 1, 0, 0, 0},	// EZ-T
	};

	/** Attack table(for All Spin) */
	private static final int[][] LINE_ATTACK_TABLE_ALLSPIN =
	{
		// 1-2P, 3P, 4P, 5P, 6P
		{0, 0, 0, 0, 0},	// Single
		{1, 1, 0, 0, 0},	// Double
		{2, 2, 1, 1, 1},	// Triple
		{4, 3, 2, 2, 2},	// Four
		{0, 0, 0, 0, 0},	// T-Mini-S
		{2, 2, 1, 1, 1},	// T-Single
		{4, 3, 2, 2, 2},	// T-Double
		{6, 4, 3, 3, 3},	// T-Triple
		{3, 2, 1, 1, 1},	// T-Mini-D
		{0,	0, 0, 0, 0},	// EZ-T
	};

	/** Indexes of attack types in attack table */
	private static final int LINE_ATTACK_INDEX_SINGLE = 0,
							 LINE_ATTACK_INDEX_DOUBLE = 1,
							 LINE_ATTACK_INDEX_TRIPLE = 2,
							 LINE_ATTACK_INDEX_FOUR = 3,
							 LINE_ATTACK_INDEX_TMINI = 4,
							 LINE_ATTACK_INDEX_TSINGLE = 5,
							 LINE_ATTACK_INDEX_TDOUBLE = 6,
							 LINE_ATTACK_INDEX_TTRIPLE = 7,
							 LINE_ATTACK_INDEX_TMINI_D = 8,
							 LINE_ATTACK_INDEX_EZ_T = 9;

	/** Combo attack table */
	private static final int[][] COMBO_ATTACK_TABLE = {
		{0,0,1,1,2,2,3,3,4,4,4,5}, // 1-2 Player(s)
		{0,0,1,1,1,2,2,3,3,4,4,4}, // 3 Player
		{0,0,0,1,1,1,2,2,3,3,4,4}, // 4 Player
		{0,0,0,1,1,1,1,2,2,3,3,4}, // 5 Player
		{0,0,0,0,1,1,1,1,2,2,3,3}, // 6 Payers
	};

	/** Garbage denominator (can be divided by 2,3,4,5) */
	private static int GARBAGE_DENOMINATOR = 60;

	/** Column number of hole in most recent garbage line */
	private int lastHole = -1;

	/** true if Hurry Up has been started */
	private boolean hurryupStarted;

	/** Number of frames left to show "HURRY UP!" text */
	private int hurryupShowFrames;

	/** Number of pieces placed after Hurry Up has started */
	private int hurryupCount;

	/** true if you KO'd player */
	private boolean[] playerKObyYou;

	/** Your KO count */
	private int currentKO;

	/** Time to display the most recent increase in score */
	private int[] scgettime;

	/** Most recent scoring event type */
	private int[] lastevent;

	/** true if most recent scoring event was B2B */
	private boolean[] lastb2b;

	/** Most recent scoring event Combo count */
	private int[] lastcombo;

	/** Most recent scoring event piece type */
	private int[] lastpiece;

	/** Count of garbage lines send */
	private int[] garbageSent;

	/** Amount of garbage in garbage queue */
	private int[] garbage;

	/** Recieved garbage entries */
	private LinkedList<GarbageEntry> garbageEntries;

	/** APL (Attack Per Line) */
	private float[] playerAPL;

	/** APM (Attack Per Minute) */
	private float[] playerAPM;

	/** Target ID (-1:All) */
	private int targetID;

	/** Target Timer */
	private int targetTimer;

	/*
	 * Mode name
	 */
	@Override
	public String getName() {
		return "NET-VS-BATTLE";
	}

	/*
	 * Mode Initialization
	 */
	@Override
	public void modeInit(GameManager manager) {
		super.modeInit(manager);
		playerKObyYou = new boolean[NETVS_MAX_PLAYERS];
		scgettime = new int[NETVS_MAX_PLAYERS];
		lastevent = new int[NETVS_MAX_PLAYERS];
		lastb2b = new boolean[NETVS_MAX_PLAYERS];
		lastcombo = new int[NETVS_MAX_PLAYERS];
		lastpiece = new int[NETVS_MAX_PLAYERS];
		garbageSent = new int[NETVS_MAX_PLAYERS];
		garbage = new int[NETVS_MAX_PLAYERS];
		playerAPL = new float[NETVS_MAX_PLAYERS];
		playerAPM = new float[NETVS_MAX_PLAYERS];
	}

	/**
	 * Get number of possible targets (number of opponents)
	 * @return Number of possible targets (number of opponents)
	 */
	private int getNumberOfPossibleTargets() {
		int count = 0;
		for(int i = 1; i < getPlayers(); i++) {
			if(netvsIsAttackable(i)) count++;
		}
		return count;
	}

	/**
	 * Set new target
	 */
	private void setNewTarget() {
		if((getNumberOfPossibleTargets() >= 1) && (netCurrentRoomInfo != null) && (netCurrentRoomInfo.isTarget) &&
		   (!netvsIsWatch()) && (!netvsIsPractice))
		{
			do {
				targetID++;
				if(targetID >= getPlayers()) targetID = 1;
			} while (!netvsIsAttackable(targetID));
		} else {
			targetID = -1;
		}
	}

	/**
	 * Get number of garbage lines the local player has
	 * @return Number of garbage lines
	 */
	private int getTotalGarbageLines() {
		int count = 0;
		for(GarbageEntry entry: garbageEntries) {
			count += entry.lines;
		}
		return count;
	}

	/*
	 * Initialization for each player
	 */
	@Override
	public void playerInit(GameEngine engine, int playerID) {
		super.playerInit(engine, playerID);

		if((playerID == 0) && (!netvsIsWatch())) {
			lastHole = -1;
			hurryupCount = 0;
			currentKO = 0;
			targetID = -1;
			targetTimer = 0;

			if(garbageEntries == null) {
				garbageEntries = new LinkedList<GarbageEntry>();
			} else {
				garbageEntries.clear();
			}
		}

		playerKObyYou[playerID] = false;
		scgettime[playerID] = 0;
		lastevent[playerID] = EVENT_NONE;
		lastb2b[playerID] = false;
		lastcombo[playerID] = 0;
		lastpiece[playerID] = 0;
		garbageSent[playerID] = 0;
		garbage[playerID] = 0;
		playerAPL[playerID] = 0f;
		playerAPM[playerID] = 0f;
	}

	/*
	 * Executed after Ready->Go, before the first piece appears.
	 */
	@Override
	public void startGame(GameEngine engine, int playerID) {
		super.startGame(engine, playerID);

		if((playerID == 0) && (!netvsIsWatch())) {
			if(!netvsIsPractice) {
				hurryupStarted = false;
				hurryupShowFrames = 0;
			}
			setNewTarget();
			targetTimer = 0;
		}
	}

	/*
	 * Calculate Score
	 */
	@Override
	public void calcScore(GameEngine engine, int playerID, int lines) {
		// Attack
		if((lines > 0) && (playerID == 0)) {
			int[] pts = new int[ATTACK_CATEGORIES];

			scgettime[playerID] = 0;

			int numAliveTeams = netvsGetNumberOfTeamsAlive();
			int attackNumPlayerIndex = numAliveTeams - 2;
			if(netvsIsPractice || !netCurrentRoomInfo.reduceLineSend) attackNumPlayerIndex = 0;
			if(attackNumPlayerIndex < 0) attackNumPlayerIndex = 0;
			if(attackNumPlayerIndex > 4) attackNumPlayerIndex = 4;

			int attackLineIndex = LINE_ATTACK_INDEX_SINGLE;
			int mainAttackCategory = ATTACK_CATEGORY_NORMAL;

			// T-Spin style attack
			if(engine.tspin) {
				mainAttackCategory = ATTACK_CATEGORY_SPIN;

				// EZ-T
				if(engine.tspinez) {
					attackLineIndex = LINE_ATTACK_INDEX_EZ_T;
					lastevent[playerID] = EVENT_TSPIN_EZ;
				}
				// T-Spin 1 line
				else if(lines == 1) {
					if(engine.tspinmini) {
						attackLineIndex = LINE_ATTACK_INDEX_TMINI;
						lastevent[playerID] = EVENT_TSPIN_SINGLE_MINI;
					} else {
						attackLineIndex = LINE_ATTACK_INDEX_TSINGLE;
						lastevent[playerID] = EVENT_TSPIN_SINGLE;
					}
				}
				// T-Spin 2 lines
				else if(lines == 2) {
					if(engine.tspinmini && engine.useAllSpinBonus) {
						attackLineIndex = LINE_ATTACK_INDEX_TMINI_D;
						lastevent[playerID] = EVENT_TSPIN_DOUBLE_MINI;
					} else {
						attackLineIndex = LINE_ATTACK_INDEX_TDOUBLE;
						lastevent[playerID] = EVENT_TSPIN_DOUBLE;
					}
				}
				// T-Spin 3 lines
				else if(lines >= 3) {
					attackLineIndex = LINE_ATTACK_INDEX_TTRIPLE;
					lastevent[playerID] = EVENT_TSPIN_TRIPLE;
				}
			}
			// Normal style attack
			else {
				// Single
				if(lines == 1) {
					attackLineIndex = LINE_ATTACK_INDEX_SINGLE;
					lastevent[playerID] = EVENT_SINGLE;
				}
				// Double
				else if(lines == 2) {
					attackLineIndex = LINE_ATTACK_INDEX_DOUBLE;
					lastevent[playerID] = EVENT_DOUBLE;
				}
				// Triple
				else if(lines == 3) {
					attackLineIndex = LINE_ATTACK_INDEX_TRIPLE;
					lastevent[playerID] = EVENT_TRIPLE;
				}
				// Four
				else if(lines >= 4) {
					attackLineIndex = LINE_ATTACK_INDEX_FOUR;
					lastevent[playerID] = EVENT_FOUR;
				}
			}

			if(engine.useAllSpinBonus)
				pts[mainAttackCategory] += LINE_ATTACK_TABLE_ALLSPIN[attackLineIndex][attackNumPlayerIndex];
			else
				pts[mainAttackCategory] += LINE_ATTACK_TABLE[attackLineIndex][attackNumPlayerIndex];

			// B2B
			if(engine.b2b) {
				lastb2b[playerID] = true;

				if(pts[mainAttackCategory] > 0) {
					if((attackLineIndex == LINE_ATTACK_INDEX_TTRIPLE) && (!engine.useAllSpinBonus))
						pts[ATTACK_CATEGORY_B2B] += 2;
					else
						pts[ATTACK_CATEGORY_B2B] += 1;
				}
			} else {
				lastb2b[playerID] = false;
			}

			// Combo
			if(engine.comboType != GameEngine.COMBO_TYPE_DISABLE) {
				int cmbindex = engine.combo - 1;
				if(cmbindex < 0) cmbindex = 0;
				if(cmbindex >= COMBO_ATTACK_TABLE[attackNumPlayerIndex].length) cmbindex = COMBO_ATTACK_TABLE[attackNumPlayerIndex].length - 1;
				pts[ATTACK_CATEGORY_COMBO] += COMBO_ATTACK_TABLE[attackNumPlayerIndex][cmbindex];
				lastcombo[playerID] = engine.combo;
			}

			// All clear (Bravo)
			if((lines >= 1) && (engine.field.isEmpty()) && (netCurrentRoomInfo.bravo)) {
				engine.playSE("bravo");
				pts[ATTACK_CATEGORY_BRAVO] += 6;
			}

			// Gem block attack
			pts[ATTACK_CATEGORY_GEM] += engine.field.getHowManyGemClears();

			lastpiece[playerID] = engine.nowPieceObject.id;

			for(int i = 0; i < pts.length; i++){
				pts[i] *= GARBAGE_DENOMINATOR;
			}
			if(netCurrentRoomInfo.useFractionalGarbage && !netvsIsPractice) {
				if(numAliveTeams >= 3) {
					for(int i = 0; i < pts.length; i++){
						pts[i] = pts[i] / (numAliveTeams - 1);
					}
				}
			}

			// Attack lines count
			for(int i : pts){
				garbageSent[playerID] += i;
			}

			// Garbage countering
			garbage[playerID] = getTotalGarbageLines();
			for(int i = 0; i < pts.length; i++){ //TODO: Establish specific priority of garbage cancellation.
				if((pts[i] > 0) && (garbage[playerID] > 0) && (netCurrentRoomInfo.counter)) {
					while(!netCurrentRoomInfo.useFractionalGarbage && !garbageEntries.isEmpty() && (pts[i] > 0)
						|| netCurrentRoomInfo.useFractionalGarbage && !garbageEntries.isEmpty() && (pts[i] >= GARBAGE_DENOMINATOR))
					{
						GarbageEntry garbageEntry = garbageEntries.getFirst();
						garbageEntry.lines -= pts[i];

						if(garbageEntry.lines <= 0) {
							pts[i] = Math.abs(garbageEntry.lines);
							garbageEntries.removeFirst();
						} else {
							pts[i] = 0;
						}
					}
				}
			}

			// Send garbage lines
			if(!netvsIsPractice) {
				garbage[playerID] = getTotalGarbageLines();

				String stringPts = "";
				for(int i : pts){
					stringPts += i + "\t";
				}

				if((targetID != -1) && !netvsIsAttackable(targetID)) setNewTarget();
				int targetSeatID = (targetID == -1) ? -1 : netvsPlayerSeatID[targetID];

				netLobby.netPlayerClient.send("game\tattack\t" + stringPts + "\t" + lastevent[playerID] + "\t" + lastb2b[playerID] + "\t" +
						lastcombo[playerID] + "\t" + garbage[playerID] + "\t" + lastpiece[playerID] + "\t" + targetSeatID + "\n");
			}
		}

		// Garbage lines appear
		if( ((lines == 0) || (!netCurrentRoomInfo.rensaBlock)) && (getTotalGarbageLines() >= GARBAGE_DENOMINATOR) && (!netvsIsPractice) ) {
			engine.playSE("garbage");

			int smallGarbageCount = 0;
			int hole = lastHole;
			int newHole;
			if(hole == -1) {
				hole = engine.random.nextInt(engine.field.getWidth());
			}

			int finalGarbagePercent = netCurrentRoomInfo.garbagePercent;
			if(netCurrentRoomInfo.divideChangeRateByPlayers){
				finalGarbagePercent /= (netvsGetNumberOfTeamsAlive() - 1);
			}

			// Make regular garbage lines appear
			while(!garbageEntries.isEmpty()) {
				GarbageEntry garbageEntry = garbageEntries.poll();
				smallGarbageCount += garbageEntry.lines % GARBAGE_DENOMINATOR;

				if(garbageEntry.lines / GARBAGE_DENOMINATOR > 0) {
					int seatFrom = netvsPlayerSeatID[garbageEntry.playerID];
					int garbageColor = (seatFrom < 0) ? Block.BLOCK_COLOR_GRAY : NETVS_PLAYER_COLOR_BLOCK[seatFrom];
					netvsLastAttackerUID = garbageEntry.uid;
					if(netCurrentRoomInfo.garbageChangePerAttack == true){
						if(engine.random.nextInt(100) < finalGarbagePercent) {
							newHole = engine.random.nextInt(engine.field.getWidth() - 1);
							if(newHole >= hole) {
								newHole++;
							}
							hole = newHole;
						}
						engine.field.addSingleHoleGarbage(hole, garbageColor, engine.getSkin(),
								  garbageEntry.lines / GARBAGE_DENOMINATOR);
					} else {
						for(int i = garbageEntry.lines / GARBAGE_DENOMINATOR; i > 0; i--) {
							if(engine.random.nextInt(100) < finalGarbagePercent) {
								newHole = engine.random.nextInt(engine.field.getWidth() - 1);
								if(newHole >= hole) {
									newHole++;
								}
								hole = newHole;
							}

							engine.field.addSingleHoleGarbage(hole, garbageColor, engine.getSkin(), 1);
						}
					}
				}
			}

			// Make small garbage lines appear
			if(smallGarbageCount > 0) {
				if(smallGarbageCount / GARBAGE_DENOMINATOR > 0) {
					netvsLastAttackerUID = -1;

					if(netCurrentRoomInfo.garbageChangePerAttack == true){
						if(engine.random.nextInt(100) < finalGarbagePercent) {
							newHole = engine.random.nextInt(engine.field.getWidth() - 1);
							if(newHole >= hole) {
								newHole++;
							}
							hole = newHole;
						}
						engine.field.addSingleHoleGarbage(hole, Block.BLOCK_COLOR_GRAY, engine.getSkin(),
								  smallGarbageCount / GARBAGE_DENOMINATOR);
					} else {
						for(int i = smallGarbageCount / GARBAGE_DENOMINATOR; i > 0; i--) {
							if(engine.random.nextInt(100) < finalGarbagePercent) {
								newHole = engine.random.nextInt(engine.field.getWidth() - 1);
								if(newHole >= hole) {
									newHole++;
								}
								hole = newHole;
							}

							engine.field.addSingleHoleGarbage(hole, Block.BLOCK_COLOR_GRAY, engine.getSkin(), 1);
						}
					}
				}

				if(smallGarbageCount % GARBAGE_DENOMINATOR > 0) {
					GarbageEntry smallGarbageEntry = new GarbageEntry(smallGarbageCount % GARBAGE_DENOMINATOR, -1);
					garbageEntries.add(smallGarbageEntry);
				}
			}

			lastHole = hole;
			garbage[playerID] = getTotalGarbageLines();
		}

		// HURRY UP!
		if((netCurrentRoomInfo.hurryupSeconds >= 0) && (engine.timerActive) && (!netvsIsPractice)) {
			if(hurryupStarted) {
				hurryupCount++;

				if(hurryupCount % netCurrentRoomInfo.hurryupInterval == 0) {
					engine.field.addHurryupFloor(1, engine.getSkin());
				}
			} else {
				hurryupCount = netCurrentRoomInfo.hurryupInterval - 1;
			}
		}
	}

	/*
	 * Executed at the end of each frame
	 */
	@Override
	public void onLast(GameEngine engine, int playerID) {
		super.onLast(engine, playerID);

		scgettime[playerID]++;
		if((playerID == 0) && (hurryupShowFrames > 0)) hurryupShowFrames--;

		// HURRY UP!
		if((playerID == 0) && (engine.timerActive) && (netCurrentRoomInfo != null) && (netCurrentRoomInfo.hurryupSeconds >= 0) &&
		   (netvsPlayTimer == netCurrentRoomInfo.hurryupSeconds * 60) && (!hurryupStarted))
		{
			if(!netvsIsWatch() && !netvsIsPractice) {
				netLobby.netPlayerClient.send("game\thurryup\n");
				owner.receiver.playSE("hurryup");
			}
			hurryupStarted = true;
			hurryupShowFrames = 60 * 5;
		}

		// Garbage meter
		int tempGarbage = garbage[playerID] / GARBAGE_DENOMINATOR;
		float tempGarbageF = (float) garbage[playerID] / GARBAGE_DENOMINATOR;
		int newMeterValue = (int)(tempGarbageF * owner.receiver.getBlockGraphicsHeight(engine, playerID));
		if((playerID == 0) && !netvsIsWatch()) {
			if(newMeterValue > engine.meterValue) {
				engine.meterValue += owner.receiver.getBlockGraphicsHeight(engine, playerID) / 2;
				if(engine.meterValue > newMeterValue) {
					engine.meterValue = newMeterValue;
				}
			} else if(newMeterValue < engine.meterValue) {
				engine.meterValue--;
			}
		} else {
			engine.meterValue = newMeterValue;
		}
		if(tempGarbage >= 4) engine.meterColor = GameEngine.METER_COLOR_RED;
		else if(tempGarbage >= 3) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
		else if(tempGarbage >= 1) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
		else engine.meterColor = GameEngine.METER_COLOR_GREEN;

		// APL & APM
		if((playerID == 0) && (engine.gameActive) && (engine.timerActive) && !netvsIsWatch()) {
			float tempGarbageSent = (float)garbageSent[playerID] / GARBAGE_DENOMINATOR;
			playerAPM[0] = (tempGarbageSent * 3600) / (engine.statistics.time);

			if(engine.statistics.lines > 0) {
				playerAPL[0] = (float)(tempGarbageSent / engine.statistics.lines);
			} else {
				playerAPL[0] = 0f;
			}
		}

		// Target
		if((playerID == 0) && !netvsIsWatch() && (netvsPlayTimerActive) && (engine.gameActive) && (engine.timerActive) &&
		   (getNumberOfPossibleTargets() >= 1) && (netCurrentRoomInfo != null) && (netCurrentRoomInfo.isTarget))
		{
			targetTimer++;

			if((targetTimer >= netCurrentRoomInfo.targetTimer) || (!netvsIsAttackable(targetID))) {
				targetTimer = 0;
				setNewTarget();
			}
		}
	}

	/*
	 * Drawing processing at the end of every frame
	 */
	@Override
	public void renderLast(GameEngine engine, int playerID) {
		super.renderLast(engine, playerID);

		int x = owner.receiver.getFieldDisplayPositionX(engine, playerID);
		int y = owner.receiver.getFieldDisplayPositionY(engine, playerID);

		// Room info box
		if(playerID == getPlayers() - 1) {
			int x2 = (owner.receiver.getNextDisplayType() == 2) ? 544 : 503;
			if((owner.receiver.getNextDisplayType() == 2) && (netCurrentRoomInfo.maxPlayers == 2))
				x2 = 321;
			if((owner.receiver.getNextDisplayType() != 2) && (netCurrentRoomInfo.maxPlayers == 2))
				x2 = 351;

			netvsDrawRoomInfoBox(engine, x2, 286);
		}

		if(netvsPlayerExist[playerID] && engine.isVisible) {
			// Garbage Count
			if((garbage[playerID] > 0) && (netCurrentRoomInfo.useFractionalGarbage) && (engine.stat != GameEngine.STAT_RESULT)) {
				String strTempGarbage;

				int fontColor = EventReceiver.COLOR_WHITE;
				if(garbage[playerID] >= GARBAGE_DENOMINATOR) fontColor = EventReceiver.COLOR_YELLOW;
				if(garbage[playerID] >= GARBAGE_DENOMINATOR*3) fontColor = EventReceiver.COLOR_ORANGE;
				if(garbage[playerID] >= GARBAGE_DENOMINATOR*4) fontColor = EventReceiver.COLOR_RED;

				if(engine.displaysize != -1) {
					strTempGarbage = String.format(Locale.US, "%5.2f", (float)garbage[playerID] / GARBAGE_DENOMINATOR);
					owner.receiver.drawDirectFont(engine, playerID, x + 96, y + 372, strTempGarbage, fontColor, 1.0f);
				} else {
					strTempGarbage = String.format(Locale.US, "%4.1f", (float)garbage[playerID] / GARBAGE_DENOMINATOR);
					owner.receiver.drawDirectFont(engine, playerID, x + 64, y + 168, strTempGarbage, fontColor, 0.5f);
				}
			}

			// Target
			if((playerID == targetID) && (netCurrentRoomInfo != null) && (netCurrentRoomInfo.isTarget) && (netvsNumAlivePlayers >= 3) &&
			   (netvsIsGameActive) && netvsIsAttackable(playerID) && !netvsIsWatch())
			{
				int fontcolor = EventReceiver.COLOR_GREEN;
				if((targetTimer >= netCurrentRoomInfo.targetTimer - 20) && (targetTimer % 2 == 0)) fontcolor = EventReceiver.COLOR_WHITE;

				if(engine.displaysize != -1) {
					owner.receiver.drawMenuFont(engine, playerID, 2, 12, "TARGET", fontcolor);
				} else {
					owner.receiver.drawDirectFont(engine, playerID, x + 4 + 16, y + 80, "TARGET", fontcolor, 0.5f);
				}
			}
		}

		// Practice mode
		if((playerID == 0) && (netvsIsPractice) && (netvsIsPracticeExitAllowed) && (engine.stat != GameEngine.STAT_RESULT)) {
			if((lastevent[playerID] == EVENT_NONE) || (scgettime[playerID] >= 120)) {
				owner.receiver.drawMenuFont(engine, 0, 0, 21,
						"F(" + owner.receiver.getKeyNameByButtonID(engine, Controller.BUTTON_F) + " KEY):\n END GAME",
						EventReceiver.COLOR_PURPLE);
			}
		}

		// Hurry Up
		if((netCurrentRoomInfo != null) && (playerID == 0)) {
			if((netCurrentRoomInfo.hurryupSeconds >= 0) && (hurryupShowFrames > 0) && (!netvsIsPractice) && (hurryupStarted)) {
				owner.receiver.drawDirectFont(engine, 0, 256 - 8, 32, "HURRY UP!", (hurryupShowFrames % 2 == 0));
			}
		}

		// Bottom message
		if(netvsPlayerExist[playerID] && engine.isVisible) {
			// K.O.
			if(playerKObyYou[playerID]) {
				if(engine.displaysize != -1) {
					owner.receiver.drawMenuFont(engine, playerID, 3, 21, "K.O.", EventReceiver.COLOR_PINK);
				} else {
					owner.receiver.drawDirectFont(engine, playerID, x + 4 + 24, y + 168, "K.O.", EventReceiver.COLOR_PINK, 0.5f);
				}
			}
			// Line clear event
			else if((lastevent[playerID] != EVENT_NONE) && (scgettime[playerID] < 120)) {
				String strPieceName = Piece.getPieceName(lastpiece[playerID]);

				if(engine.displaysize != -1) {
					switch(lastevent[playerID]) {
					case EVENT_SINGLE:
						owner.receiver.drawMenuFont(engine, playerID, 2, 21, "SINGLE", EventReceiver.COLOR_DARKBLUE);
						break;
					case EVENT_DOUBLE:
						owner.receiver.drawMenuFont(engine, playerID, 2, 21, "DOUBLE", EventReceiver.COLOR_BLUE);
						break;
					case EVENT_TRIPLE:
						owner.receiver.drawMenuFont(engine, playerID, 2, 21, "TRIPLE", EventReceiver.COLOR_GREEN);
						break;
					case EVENT_FOUR:
						if(lastb2b[playerID])
							owner.receiver.drawMenuFont(engine, playerID, 3, 21, "FOUR", EventReceiver.COLOR_RED);
						else
							owner.receiver.drawMenuFont(engine, playerID, 3, 21, "FOUR", EventReceiver.COLOR_ORANGE);
						break;
					case EVENT_TSPIN_SINGLE_MINI:
						if(lastb2b[playerID])
							owner.receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-MINI-S", EventReceiver.COLOR_RED);
						else
							owner.receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-MINI-S", EventReceiver.COLOR_ORANGE);
						break;
					case EVENT_TSPIN_SINGLE:
						if(lastb2b[playerID])
							owner.receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-SINGLE", EventReceiver.COLOR_RED);
						else
							owner.receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-SINGLE", EventReceiver.COLOR_ORANGE);
						break;
					case EVENT_TSPIN_DOUBLE_MINI:
						if(lastb2b[playerID])
							owner.receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-MINI-D", EventReceiver.COLOR_RED);
						else
							owner.receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-MINI-D", EventReceiver.COLOR_ORANGE);
						break;
					case EVENT_TSPIN_DOUBLE:
						if(lastb2b[playerID])
							owner.receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-DOUBLE", EventReceiver.COLOR_RED);
						else
							owner.receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-DOUBLE", EventReceiver.COLOR_ORANGE);
						break;
					case EVENT_TSPIN_TRIPLE:
						if(lastb2b[playerID])
							owner.receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-TRIPLE", EventReceiver.COLOR_RED);
						else
							owner.receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-TRIPLE", EventReceiver.COLOR_ORANGE);
						break;
					case EVENT_TSPIN_EZ:
						if(lastb2b[playerID])
							owner.receiver.drawMenuFont(engine, playerID, 3, 21, "EZ-" + strPieceName, EventReceiver.COLOR_RED);
						else
							owner.receiver.drawMenuFont(engine, playerID, 3, 21, "EZ-" + strPieceName, EventReceiver.COLOR_ORANGE);
						break;
					}

					if(lastcombo[playerID] >= 2)
						owner.receiver.drawMenuFont(engine, playerID, 2, 22, (lastcombo[playerID] - 1) + "COMBO", EventReceiver.COLOR_CYAN);
				} else {
					int x2 = 8;
					if(netCurrentRoomInfo.useFractionalGarbage && (garbage[playerID] > 0)) x2 = 0;

					switch(lastevent[playerID]) {
					case EVENT_SINGLE:
						owner.receiver.drawDirectFont(engine, playerID, x + 4 + 16, y + 168, "SINGLE", EventReceiver.COLOR_DARKBLUE, 0.5f);
						break;
					case EVENT_DOUBLE:
						owner.receiver.drawDirectFont(engine, playerID, x + 4 + 16, y + 168, "DOUBLE", EventReceiver.COLOR_BLUE, 0.5f);
						break;
					case EVENT_TRIPLE:
						owner.receiver.drawDirectFont(engine, playerID, x + 4 + 16, y + 168, "TRIPLE", EventReceiver.COLOR_GREEN, 0.5f);
						break;
					case EVENT_FOUR:
						if(lastb2b[playerID])
							owner.receiver.drawDirectFont(engine, playerID, x + 4 + 24, y + 168, "FOUR", EventReceiver.COLOR_RED, 0.5f);
						else
							owner.receiver.drawDirectFont(engine, playerID, x + 4 + 24, y + 168, "FOUR", EventReceiver.COLOR_ORANGE, 0.5f);
						break;
					case EVENT_TSPIN_SINGLE_MINI:
						if(lastb2b[playerID])
							owner.receiver.drawDirectFont(engine, playerID, x + 4 + x2, y + 168,
									strPieceName + "-MINI-S", EventReceiver.COLOR_RED, 0.5f);
						else
							owner.receiver.drawDirectFont(engine, playerID, x + 4 + x2, y + 168,
									strPieceName + "-MINI-S", EventReceiver.COLOR_ORANGE, 0.5f);
						break;
					case EVENT_TSPIN_SINGLE:
						if(lastb2b[playerID])
							owner.receiver.drawDirectFont(engine, playerID, x + 4 + x2, y + 168,
									strPieceName + "-SINGLE", EventReceiver.COLOR_RED, 0.5f);
						else
							owner.receiver.drawDirectFont(engine, playerID, x + 4 + x2, y + 168,
									strPieceName + "-SINGLE", EventReceiver.COLOR_ORANGE, 0.5f);
						break;
					case EVENT_TSPIN_DOUBLE_MINI:
						if(lastb2b[playerID])
							owner.receiver.drawDirectFont(engine, playerID, x + 4 + x2, y + 168,
									strPieceName + "-MINI-D", EventReceiver.COLOR_RED, 0.5f);
						else
							owner.receiver.drawDirectFont(engine, playerID, x + 4 + x2, y + 168,
									strPieceName + "-MINI-D", EventReceiver.COLOR_ORANGE, 0.5f);
						break;
					case EVENT_TSPIN_DOUBLE:
						if(lastb2b[playerID])
							owner.receiver.drawDirectFont(engine, playerID, x + 4 + x2, y + 168,
									strPieceName + "-DOUBLE", EventReceiver.COLOR_RED, 0.5f);
						else
							owner.receiver.drawDirectFont(engine, playerID, x + 4 + x2, y + 168,
									strPieceName + "-DOUBLE", EventReceiver.COLOR_ORANGE, 0.5f);
						break;
					case EVENT_TSPIN_TRIPLE:
						if(lastb2b[playerID])
							owner.receiver.drawDirectFont(engine, playerID, x + 4 + x2, y + 168,
									strPieceName + "-TRIPLE", EventReceiver.COLOR_RED, 0.5f);
						else
							owner.receiver.drawDirectFont(engine, playerID, x + 4 + x2, y + 168,
									strPieceName + "-TRIPLE", EventReceiver.COLOR_ORANGE, 0.5f);
						break;
					case EVENT_TSPIN_EZ:
						if(lastb2b[playerID])
							owner.receiver.drawDirectFont(engine, playerID, x + 4 + 24, y + 168,
									"EZ-" + strPieceName, EventReceiver.COLOR_RED, 0.5f);
						else
							owner.receiver.drawDirectFont(engine, playerID, x + 4 + 24, y + 168,
									"EZ-" + strPieceName, EventReceiver.COLOR_ORANGE, 0.5f);
						break;
					}

					if(lastcombo[playerID] >= 2)
						owner.receiver.drawDirectFont(engine, playerID, x + 4 + 16, y + 176,
							(lastcombo[playerID] - 1) + "COMBO", EventReceiver.COLOR_CYAN, 0.5f);
				}
			}
			// Games count
			else if(!netvsIsPractice || (playerID != 0)) {
				String strTemp = netvsPlayerWinCount[playerID] + "/" + netvsPlayerPlayCount[playerID];

				if(engine.displaysize != -1) {
					int y2 = 21;
					if(engine.stat == GameEngine.STAT_RESULT) y2 = 22;
					owner.receiver.drawMenuFont(engine, playerID, 0, y2, strTemp, EventReceiver.COLOR_WHITE);
				} else {
					owner.receiver.drawDirectFont(engine, playerID, x + 4, y + 168, strTemp, EventReceiver.COLOR_WHITE, 0.5f);
				}
			}
		}
	}

	/*
	 * Render results screen
	 */
	@Override
	public void renderResult(GameEngine engine, int playerID) {
		super.renderResult(engine, playerID);

		float scale = 1.0f;
		if(engine.displaysize == -1) scale = 0.5f;

		drawResultScale(engine, playerID, owner.receiver, 2, EventReceiver.COLOR_ORANGE, scale,
				"ATTACK", String.format("%10g", (float)garbageSent[playerID] / GARBAGE_DENOMINATOR),
				"LINE", String.format("%10d", engine.statistics.lines),
				"PIECE", String.format("%10d", engine.statistics.totalPieceLocked),
				"ATK/LINE", String.format("%10g", playerAPL[playerID]),
				"ATTACK/MIN", String.format("%10g", playerAPM[playerID]),
				"LINE/MIN", String.format("%10g", engine.statistics.lpm),
				"PIECE/SEC", String.format("%10g", engine.statistics.pps),
				"TIME", String.format("%10s", GeneralUtil.getTime(engine.statistics.time)));
	}

	/*
	 * Send stats
	 */
	@Override
	protected void netSendStats(GameEngine engine) {
		if((engine.playerID == 0) && !netvsIsWatch()) {
			netLobby.netPlayerClient.send("game\tstats\t" + garbage[engine.playerID] + "\n");
		}
	}

	/*
	 * Receive stats
	 */
	@Override
	protected void netRecvStats(GameEngine engine, String[] message) {
		if(message.length > 4) {
			garbage[engine.playerID] = Integer.parseInt(message[4]);
		}
	}

	/*
	 * Send end-of-game stats
	 */
	@Override
	protected void netSendEndGameStats(GameEngine engine) {
		int playerID = engine.playerID;
		String msg = "gstat\t";
		msg += netvsPlayerPlace[playerID] + "\t";
		msg += ((float)garbageSent[playerID] / GARBAGE_DENOMINATOR) + "\t" + playerAPL[playerID] + "\t" + playerAPM[playerID] + "\t";
		msg += engine.statistics.lines + "\t" + engine.statistics.lpm + "\t";
		msg += engine.statistics.totalPieceLocked + "\t" + engine.statistics.pps + "\t";
		msg += netvsPlayTimer + "\t" + currentKO + "\t" + netvsPlayerWinCount[playerID] + "\t" + netvsPlayerPlayCount[playerID];
		msg += "\n";
		netLobby.netPlayerClient.send(msg);
	}

	/*
	 * Receive end-of-game stats
	 */
	@Override
	protected void netvsRecvEndGameStats(String[] message) {
		int seatID = Integer.parseInt(message[2]);
		int playerID = netvsGetPlayerIDbySeatID(seatID);

		if((playerID != 0) || (netvsIsWatch())) {
			GameEngine engine = owner.engine[playerID];

			float tempGarbageSend = Float.parseFloat(message[5]);
			garbageSent[playerID] = (int)(tempGarbageSend * GARBAGE_DENOMINATOR);

			playerAPL[playerID] = Float.parseFloat(message[6]);
			playerAPM[playerID] = Float.parseFloat(message[7]);
			engine.statistics.lines = Integer.parseInt(message[8]);
			engine.statistics.lpm = Float.parseFloat(message[9]);
			engine.statistics.totalPieceLocked = Integer.parseInt(message[10]);
			engine.statistics.pps = Float.parseFloat(message[11]);
			engine.statistics.time = Integer.parseInt(message[12]);

			netvsPlayerResultReceived[playerID] = true;
		}
	}

	/*
	 * Message received
	 */
	@Override
	public void netlobbyOnMessage(NetLobbyFrame lobby, NetPlayerClient client, String[] message) throws IOException {
		super.netlobbyOnMessage(lobby, client, message);

		// Dead
		if(message[0].equals("dead")) {
			int seatID = Integer.parseInt(message[3]);
			int playerID = netvsGetPlayerIDbySeatID(seatID);
			int koUID = -1;
			if(message.length > 5) koUID = Integer.parseInt(message[5]);

			// Increase KO count
			if(koUID == netLobby.netPlayerClient.getPlayerUID()) {
				playerKObyYou[playerID] = true;
				currentKO++;
			}
		}
		// Game messages
		if(message[0].equals("game")) {
			int uid = Integer.parseInt(message[1]);
			int seatID = Integer.parseInt(message[2]);
			int playerID = netvsGetPlayerIDbySeatID(seatID);
			//GameEngine engine = owner.engine[playerID];

			// Attack
			if(message[3].equals("attack")) {
				int[] pts = new int[ATTACK_CATEGORIES];
				int sumPts = 0;

				for(int i = 0; i < ATTACK_CATEGORIES; i++){
					pts[i] = Integer.parseInt(message[4+i]);
					sumPts += pts[i];
				}

				lastevent[playerID] = Integer.parseInt(message[ATTACK_CATEGORIES + 5]);
				lastb2b[playerID] = Boolean.parseBoolean(message[ATTACK_CATEGORIES + 6]);
				lastcombo[playerID] = Integer.parseInt(message[ATTACK_CATEGORIES + 7]);
				garbage[playerID] = Integer.parseInt(message[ATTACK_CATEGORIES + 8]);
				lastpiece[playerID] = Integer.parseInt(message[ATTACK_CATEGORIES + 9]);
				scgettime[playerID] = 0;
				int targetSeatID = Integer.parseInt(message[ATTACK_CATEGORIES + 10]);

				if( !netvsIsWatch() && (owner.engine[0].timerActive) && (sumPts > 0) && (!netvsIsPractice) && (!netvsIsNewcomer) &&
					((targetSeatID == -1) || (netvsPlayerSeatID[0] == targetSeatID) || (!netCurrentRoomInfo.isTarget)) &&
					netvsIsAttackable(playerID) )
				{
					int secondAdd = 0; //TODO: Allow for chunking of attack types other than b2b.
					if(netCurrentRoomInfo.b2bChunk){
						secondAdd = pts[ATTACK_CATEGORY_B2B];
					}

					GarbageEntry garbageEntry = new GarbageEntry(sumPts - secondAdd, playerID, uid);
					garbageEntries.add(garbageEntry);

					if(secondAdd > 0){
						garbageEntry = new GarbageEntry(secondAdd, playerID, uid);
						garbageEntries.add(garbageEntry);
					}

					garbage[0] = getTotalGarbageLines();
					if(garbage[0] >= 4*GARBAGE_DENOMINATOR) owner.engine[0].playSE("danger");
					netSendStats(owner.engine[0]);
				}
			}
			// HurryUp
			if(message[3].equals("hurryup")) {
				if(!hurryupStarted && (netCurrentRoomInfo != null) && (netCurrentRoomInfo.hurryupSeconds > 0)) {
					if(!netvsIsWatch() && !netvsIsPractice && owner.engine[0].timerActive) {
						owner.receiver.playSE("hurryup");
					}
					hurryupStarted = true;
					hurryupShowFrames = 60 * 5;
				}
			}
		}
	}

	/**
	 * Garbage data
	 */
	private class GarbageEntry {
		/** Number of garbage lines */
		public int lines = 0;

		/** Sender's playerID */
		public int playerID = 0;

		/** Sender's UID */
		public int uid = 0;

		/**
		 * Constructor
		 */
		@SuppressWarnings("unused")
		public GarbageEntry() {
		}

		/**
		 * Constructor
		 * @param g Lines
		 */
		@SuppressWarnings("unused")
		public GarbageEntry(int g) {
			lines = g;
		}

		/**
		 * Constructor
		 * @param g Lines
		 * @param p Sender's playerID
		 */
		public GarbageEntry(int g, int p) {
			lines = g;
			playerID = p;
		}

		/**
		 * Constructor
		 * @param g Lines
		 * @param p Sender's playerID
		 * @param s Sender's UID
		 */
		public GarbageEntry(int g, int p, int s) {
			lines = g;
			playerID = p;
			uid = s;
		}
	}
}
