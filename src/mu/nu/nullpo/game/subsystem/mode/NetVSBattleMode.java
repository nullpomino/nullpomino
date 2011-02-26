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

	/** KO count */
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
		public GarbageEntry() {
		}

		/**
		 * Constructor
		 * @param g Lines
		 */
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
