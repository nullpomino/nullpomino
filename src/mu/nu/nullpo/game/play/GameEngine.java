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
package mu.nu.nullpo.game.play;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

import org.apache.log4j.Logger;

import mu.nu.nullpo.game.component.BGMStatus;
import mu.nu.nullpo.game.component.Block;
import mu.nu.nullpo.game.component.Controller;
import mu.nu.nullpo.game.component.Field;
import mu.nu.nullpo.game.component.Piece;
import mu.nu.nullpo.game.component.ReplayData;
import mu.nu.nullpo.game.component.RuleOptions;
import mu.nu.nullpo.game.component.SpeedParam;
import mu.nu.nullpo.game.component.Statistics;
import mu.nu.nullpo.game.component.WallkickResult;
import mu.nu.nullpo.game.subsystem.ai.AIPlayer;
import mu.nu.nullpo.game.subsystem.ai.DummyAI;
import mu.nu.nullpo.game.subsystem.wallkick.Wallkick;
import net.omegaboshi.nullpomino.game.subsystem.randomizer.MemorylessRandomizer;
import net.omegaboshi.nullpomino.game.subsystem.randomizer.Randomizer;

/**
 * 各プレイヤーのゲームの処理
 */
public class GameEngine {
	/** Log (Apache log4j) */
	static Logger log = Logger.getLogger(GameEngine.class);

	/** Constants of main game status */
	public static final int STAT_NOTHING = -1,
							STAT_SETTING = 0,
							STAT_READY = 1,
							STAT_MOVE = 2,
							STAT_LOCKFLASH = 3,
							STAT_LINECLEAR = 4,
							STAT_ARE = 5,
							STAT_ENDINGSTART = 6,
							STAT_CUSTOM = 7,
							STAT_EXCELLENT = 8,
							STAT_GAMEOVER = 9,
							STAT_RESULT = 10,
							STAT_FIELDEDIT = 11,
							STAT_INTERRUPTITEM = 12;

	/** Number of free status counters (used by statc array) */
	public static final int MAX_STATC = 10;

	/** Constants of last successful movements */
	public static final int LASTMOVE_NONE = 0,
							LASTMOVE_FALL_AUTO = 1,
							LASTMOVE_FALL_SELF = 2,
							LASTMOVE_SLIDE_AIR = 3,
							LASTMOVE_SLIDE_GROUND = 4,
							LASTMOVE_ROTATE_AIR = 5,
							LASTMOVE_ROTATE_GROUND = 6;

	/** Constants of block outline type */
	public static final int BLOCK_OUTLINE_NONE = 0, BLOCK_OUTLINE_NORMAL = 1, BLOCK_OUTLINE_CONNECT = 2, BLOCK_OUTLINE_SAMECOLOR = 3;

	/** Default duration of Ready->Go */
	public static final int READY_START = 0, READY_END = 49, GO_START = 50, GO_END = 100;

	/** Constants of frame colors */
	public static final int FRAME_COLOR_BLUE = 0, FRAME_COLOR_GREEN = 1, FRAME_COLOR_RED = 2, FRAME_COLOR_GRAY = 3, FRAME_COLOR_YELLOW = 4,
							FRAME_COLOR_CYAN = 5, FRAME_COLOR_PINK = 6, FRAME_COLOR_PURPLE = 7;

	/** Constants of meter colors */
	public static final int METER_COLOR_RED = 0, METER_COLOR_ORANGE = 1, METER_COLOR_YELLOW = 2, METER_COLOR_GREEN = 3;

	/** Constants of T-Spin Mini detection type */
	public static final int TSPINMINI_TYPE_ROTATECHECK = 0, TSPINMINI_TYPE_WALLKICKFLAG = 1;

	/** Spin detection type */
	public static final int SPINTYPE_4POINT = 0,
							SPINTYPE_IMMOBILE = 1;

	/** Constants of combo type */
	public static final int COMBO_TYPE_DISABLE = 0, COMBO_TYPE_NORMAL = 1, COMBO_TYPE_DOUBLE = 2;

	/** Constants of gameplay-interruptable items */
	public static final int INTERRUPTITEM_NONE = 0,
							INTERRUPTITEM_MIRROR = 1;

	/** Line gravity types */
	public static final int LINE_GRAVITY_NATIVE = 0, LINE_GRAVITY_CASCADE = 1, LINE_GRAVITY_CASCADE_SLOW = 2;

	/** Clear mode settings */
	public static final int CLEAR_LINE = 0, CLEAR_COLOR = 1, CLEAR_LINE_COLOR = 2, CLEAR_GEM_COLOR = 3;

	/** Table for color-block item */
	public static final int[] ITEM_COLOR_BRIGHT_TABLE =
	{
		10, 10,  9,  9,  8,  8,  8,  7,  7,  7,
		 6,  6,  6,  5,  5,  5,  4,  4,  4,  4,
		 3,  3,  3,  3,  2,  2,  2,  2,  1,  1,
		 1,  1,  0,  0,  0,  0,  0,  0,  0,  0
	};

	/** Default list of block colors to use for random block colors. */
	public static final int[] BLOCK_COLORS_DEFAULT = {
		Block.BLOCK_COLOR_RED,
		Block.BLOCK_COLOR_ORANGE,
		Block.BLOCK_COLOR_YELLOW,
		Block.BLOCK_COLOR_GREEN,
		Block.BLOCK_COLOR_CYAN,
		Block.BLOCK_COLOR_BLUE,
		Block.BLOCK_COLOR_PURPLE
	};;

	/** GameManager: Owner of this GameEngine */
	public GameManager owner;

	/** Player ID (0=1P) */
	public int playerID;

	/** RuleOptions: Most game settings are here */
	public RuleOptions ruleopt;

	/** Wallkick: The wallkick system */
	public Wallkick wallkick;

	/** Randomizer: Used by creation of next piece sequence */
	public Randomizer randomizer;

	/** Field: The playfield */
	public Field field;

	/** Controller: You can get player's input from here */
	public Controller ctrl;

	/** Statistics: Various game statistics such as score, number of lines, etc */
	public Statistics statistics;

	/** SpeedParam: Parameters of game speed (Gravity, ARE, Line clear delay, etc) */
	public SpeedParam speed;

	/** Gravity counter (The piece falls when this reaches to the value of speed.denominator) */
	public int gcount;

	/** The first random-seed */
	public long randSeed;

	/** Random: Used for creating various randomness */
	public Random random;

	/** ReplayData: Manages input data for replays */
	public ReplayData replayData;

	/** AIPlayer: AI for auto playing */
	public DummyAI ai;

	/** AI move delay */
	public int aiMoveDelay;

	/** AI think delay (Only when using thread) */
	public int aiThinkDelay;

	/** Use thread for AI */
	public boolean aiUseThread;
	
	/** Show Hint with AI */
	public boolean aiShowHint;

	public int aiHintX;
	public int aiHintY;
	public int aiHintRt;
	public boolean aiHintReady;
	/** Current main game status */
	public int stat;

	/** Free status counters */
	public int[] statc;

	/** true if the game is active */
	public boolean gameActive;

	/** true if the timer is active */
	public boolean timerActive;

	/** Timer for replay */
	public int replayTimer;

	/** Time of game start in milliseconds */
	public long startTime;

	/** Time of game end in milliseconds */
	public long endTime;

	/** Major version */
	public float versionMajor;

	/** Minor version */
	public int versionMinor;

	/** OLD minor version (Used for 6.9 or earlier replays) */
	public float versionMinorOld;

	/** Game quit flag */
	public boolean quitflag;

	/** Piece object of current piece */
	public Piece nowPieceObject;

	/** X coord of current piece */
	public int nowPieceX;

	/** Y coord of current piece */
	public int nowPieceY;

	/** Bottommost Y coord of current piece (Used for ghost piece and harddrop) */
	public int nowPieceBottomY;

	/** Write anything other than -1 to override whole current piece color */
	public int nowPieceColorOverride;

	/** Allow/Disallow certain piece */
	public boolean[] nextPieceEnable;

	/** Preferred size of next piece array. Might be ignored by certain Randomizer. (Default:1400) */
	public int nextPieceArraySize;

	/** Array of next piece IDs */
	public int[] nextPieceArrayID;

	/** Array of next piece Objects */
	public Piece[] nextPieceArrayObject;

	/** Number of pieces put (Used by next piece sequence) */
	public int nextPieceCount;

	/** Hold piece (null: None) */
	public Piece holdPieceObject;

	/** true if hold is disabled because player used it already */
	public boolean holdDisable;

	/** Number of holds used */
	public int holdUsedCount;

	/** Number of lines currently clearing */
	public int lineClearing;

	/** Line gravity type (Native, Cascade, etc) */
	public int lineGravityType;

	/** Current number of chains */
	public int chain;

	/** Number of lines cleared for this chains */
	public int lineGravityTotalLines;

	/** Lock delay counter */
	public int lockDelayNow;

	/** DAS counter */
	public int dasCount;

	/** DAS direction (-1:Left 0:None 1:Right) */
	public int dasDirection;

	/** DAS delay counter */
	public int dasSpeedCount;

	/** Repeat statMove() for instant DAS */
	public boolean dasRepeat;

	/** In the middle of an instant DAS loop */
	public boolean dasInstant;

	/** Disallow shift while locking key is pressed */
	public int shiftLock;

	/** IRS direction */
	public int initialRotateDirection;

	/** Last IRS direction */
	public int initialRotateLastDirection;

	/** IRS continuous use flag */
	public boolean initialRotateContinuousUse;

	/** IHS */
	public boolean initialHoldFlag;

	/** IHS continuous use flag */
	public boolean initialHoldContinuousUse;

	/** Number of current piece movement */
	public int nowPieceMoveCount;

	/** Number of current piece rotations */
	public int nowPieceRotateCount;

	/** Number of movement while touching to the floor */
	public int extendedMoveCount;

	/** Number of rotations while touching to the floor */
	public int extendedRotateCount;

	/** Number of wallkicks used by current piece */
	public int nowWallkickCount;

	/** Number of upward wallkicks used by current piece */
	public int nowUpwardWallkickCount;

	/** Number of rows falled by soft drop (Used by soft drop bonuses) */
	public int softdropFall;

	/** Number of rows falled by hard drop (Used by soft drop bonuses) */
	public int harddropFall;

	/** Soft drop continuous use flag */
	public boolean softdropContinuousUse;

	/** Hard drop continuous use flag */
	public boolean harddropContinuousUse;

	/** true if the piece was manually locked by player */
	public boolean manualLock;

	/** Last successful movement */
	public int lastmove;

	/** ture if T-Spin */
	public boolean tspin;

	/** true if T-Spin Mini */
	public boolean tspinmini;

	/** EZ T-spin */
	public boolean tspinez;

	/** true if B2B */
	public boolean b2b;

	/** B2B counter */
	public int b2bcount;

	/** Number of combos */
	public int combo;

	/** T-Spin enable flag */
	public boolean tspinEnable;

	/** EZ-T toggle */
	public boolean tspinEnableEZ;

	/** Allow T-Spin with wallkicks */
	public boolean tspinAllowKick;

	/** T-Spin Mini detection type */
	public int tspinminiType;

	/** Spin detection type */
	public int spinCheckType;

	/** All Spins flag */
	public boolean useAllSpinBonus;

	/** B2B enable flag */
	public boolean b2bEnable;

	/** Combo type */
	public int comboType;

	/** Number of frames before placed blocks disappear (-1:Disable) */
	public int blockHidden;

	/** Use alpha-blending for blockHidden */
	public boolean blockHiddenAnim;

	/** Outline type */
	public int blockOutlineType;

	/** Show outline only flag. If enabled it does not show actual image of blocks. */
	public boolean blockShowOutlineOnly;

	/** Hebo-hidden Enable flag */
	public boolean heboHiddenEnable;

	/** Hebo-hidden Timer */
	public int heboHiddenTimerNow;

	/** Hebo-hidden Timer Max */
	public int heboHiddenTimerMax;

	/** Hebo-hidden Y coord */
	public int heboHiddenYNow;

	/** Hebo-hidden Y coord Limit */
	public int heboHiddenYLimit;

	/** Set when ARE or line delay is canceled */
	public boolean delayCancel;

	/** Piece must move left after canceled delay */
	public boolean delayCancelMoveLeft;

	/** Piece must move right after canceled delay */
	public boolean delayCancelMoveRight;

	/** Use bone blocks [][][][] */
	public boolean bone;

	/** Big blocks */
	public boolean big;

	/** Big movement type (false:1cell true:2cell) */
	public boolean bigmove;

	/** Halves the amount of lines cleared in Big mode */
	public boolean bighalf;

	/** true if wallkick is used */
	public boolean kickused;

	/** Field size (-1:Default) */
	public int fieldWidth, fieldHeight, fieldHiddenHeight;

	/** Ending mode (0:During the normal game) */
	public int ending;

	/** Enable staffroll challenge (Credits) in ending */
	public boolean staffrollEnable;

	/** Disable death in staffroll challenge */
	public boolean staffrollNoDeath;

	/** Update various statistics in staffroll challenge */
	public boolean staffrollEnableStatistics;

	/** Frame color */
	public int framecolor;

	/** Duration of Ready->Go */
	public int readyStart, readyEnd, goStart, goEnd;

	/** true if Ready->Go is already done */
	public boolean readyDone;

	/** Number of lives */
	public int lives;

	/** Ghost piece flag */
	public boolean ghost;

	/** Amount of meter */
	public int meterValue;

	/** Color of meter */
	public int meterColor;

	/** Lag flag (Infinite length of ARE will happen after placing a piece until this flag is set to false) */
	public boolean lagARE;

	/** Lag flag (Pause the game completely) */
	public boolean lagStop;

	/** Field display size (-1 for mini, 1 for big, 0 for normal) */
	public int displaysize;

	/** Sound effects enable flag */
	public boolean enableSE;

	/** Stops all other players when this player dies */
	public boolean gameoverAll;

	/** Field visible flag (false for invisible challenge) */
	public boolean isVisible;

	/** Piece preview visible flag */
	public boolean isNextVisible;

	/** Hold piece visible flag */
	public boolean isHoldVisible;

	/** Field edit screen: Cursor coord */
	public int fldeditX, fldeditY;

	/** Field edit screen: Selected color */
	public int fldeditColor;

	/** Field edit screen: Previous game status number */
	public int fldeditPreviousStat;

	/** Field edit screen: Frame counter */
	public int fldeditFrames;

	/** Next-skip during Ready->Go */
	public boolean holdButtonNextSkip;

	/** Allow default text rendering (such as "READY", "GO!", "GAME OVER", etc) */
	public boolean allowTextRenderByReceiver;

	/** RollRoll (Auto rotation) enable flag */
	public boolean itemRollRollEnable;

	/** RollRoll (Auto rotation) interval */
	public int itemRollRollInterval;

	/** X-RAY enable flag */
	public boolean itemXRayEnable;

	/** X-RAY counter */
	public int itemXRayCount;

	/** Color-block enable flag */
	public boolean itemColorEnable;

	/** Color-block counter */
	public int itemColorCount;

	/** Gameplay-interruptable item */
	public int interruptItemNumber;

	/** Post-status of interruptable item */
	public int interruptItemPreviousStat;

	/** Backup field for Mirror item */
	public Field interruptItemMirrorField;

	/** A button direction -1=Auto(Use rule settings) 0=Left 1=Right */
	public int owRotateButtonDefaultRight;

	/** Block Skin (-1=Auto 0orAbove=Fixed) */
	public int owSkin;

	/** Min/Max DAS (-1=Auto 0orAbove=Fixed) */
	public int owMinDAS, owMaxDAS;

	/** DAS delay (-1=Auto 0orAbove=Fixed) */
	public int owDasDelay;

	/** Clear mode selection */
	public int clearMode;

	/** Size needed for a color-group clear */
	public int colorClearSize;

	/** If true, color clears will also clear adjacent garbage blocks. */
	public boolean garbageColorClear;

	/** If true, each individual block is a random color. */
	public boolean randomBlockColor;

	/** If true, block in pieces are connected. */
	public boolean connectBlocks;

	/** List of block colors to use for random block colors. */
	public int[] blockColors;

	/** Number of colors in blockColors to use. */
	public int numColors;

	/** If true, line color clears can be diagonal. */
	public boolean lineColorDiagonals;

	/** If true, gems count as the same color as their respectively-colored normal blocks */
	public boolean gemSameColor;

	/** Delay for each step in cascade animations */
	public int cascadeDelay;

	/** Delay between landing and checking for clears in cascade */
	public int cascadeClearDelay;

	/** If true, color clears will ignore hidden rows */
	public boolean ignoreHidden;
	
	/** Set to true to process rainbow block effects, false to skip. */
	public boolean rainbowAnimate;
	
	/**
	 * Constructor
	 * @param owner このゲームエンジンを所有するGameOwnerクラス
	 * @param playerID プレイヤーの number
	 */
	public GameEngine(GameManager owner, int playerID) {
		this.owner = owner;
		this.playerID = playerID;
		this.ruleopt = new RuleOptions();
		this.wallkick = null;
		this.randomizer = null;

		owRotateButtonDefaultRight = -1;
		owSkin = -1;
		owMinDAS = -1;
		owMaxDAS = -1;
		owDasDelay = -1;
	}

	/**
	 * ルール設定などのパラメータ付きのConstructor
	 * @param owner このゲームエンジンを所有するGameOwnerクラス
	 * @param playerID プレイヤーの number
	 * @param ruleopt ルール設定
	 * @param wallkick Wallkickシステム
	 * @param randomizer Blockピースの出現順の生成アルゴリズム
	 */
	public GameEngine(GameManager owner, int playerID, RuleOptions ruleopt, Wallkick wallkick, Randomizer randomizer) {
		this(owner,playerID);
		this.ruleopt = ruleopt;
		this.wallkick = wallkick;
		this.randomizer = randomizer;
	}

	/**
	 * READY前のInitialization
	 */
	public void init() {
		//log.debug("GameEngine init() playerID:" + playerID);

		field = null;
		ctrl = new Controller();
		statistics = new Statistics();
		speed = new SpeedParam();
		gcount = 0;
		replayData = new ReplayData();

		if(owner.replayMode == false) {
			versionMajor = GameManager.getVersionMajor();
			versionMinor = GameManager.getVersionMinor();
			versionMinorOld = GameManager.getVersionMinorOld();

			Random tempRand = new Random();
			randSeed = tempRand.nextLong();
			random = new Random(randSeed);
		} else {
			versionMajor = owner.replayProp.getProperty("version.core.major", 0f);
			versionMinor = owner.replayProp.getProperty("version.core.minor", 0);
			versionMinorOld = owner.replayProp.getProperty("version.core.minor", 0f);

			replayData.readProperty(owner.replayProp, playerID);

			String tempRand = owner.replayProp.getProperty(playerID + ".replay.randSeed", "0");
			randSeed = Long.parseLong(tempRand, 16);
			random = new Random(randSeed);

			owRotateButtonDefaultRight = owner.replayProp.getProperty(playerID + ".tuning.owRotateButtonDefaultRight", -1);
			owSkin = owner.replayProp.getProperty(playerID + ".tuning.owSkin", -1);
			owMinDAS = owner.replayProp.getProperty(playerID + ".tuning.owMinDAS", -1);
			owMaxDAS = owner.replayProp.getProperty(playerID + ".tuning.owMaxDAS", -1);
			owDasDelay = owner.replayProp.getProperty(playerID + ".tuning.owDasDelay", -1);

			// Fixing old replays to accomodate for new DAS notation
			if (versionMajor < 7.3) {
				if  (owDasDelay >= 0) {
					owDasDelay++;
				} else {
					owDasDelay = owner.replayProp.getProperty(playerID + ".ruleopt.dasDelay", 0) + 1;
				}
			}
		}

		quitflag = false;

		stat = STAT_SETTING;
		statc = new int[MAX_STATC];

		gameActive = false;
		timerActive = false;
		replayTimer = 0;

		nowPieceObject = null;
		nowPieceX = 0;
		nowPieceY = 0;
		nowPieceBottomY = 0;
		nowPieceColorOverride = -1;

		nextPieceArraySize = 1400;
		nextPieceEnable = new boolean[Piece.PIECE_COUNT];
		for(int i = 0; i < Piece.PIECE_STANDARD_COUNT; i++) nextPieceEnable[i] = true;
		nextPieceArrayID = null;
		nextPieceArrayObject = null;
		nextPieceCount = 0;

		holdPieceObject = null;
		holdDisable = false;
		holdUsedCount = 0;

		lineClearing = 0;
		lineGravityType = LINE_GRAVITY_NATIVE;
		chain = 0;
		lineGravityTotalLines = 0;

		lockDelayNow = 0;

		dasCount = 0;
		dasDirection = 0;
		dasSpeedCount = getDASDelay();
		dasRepeat = false;
		dasInstant = false;
		shiftLock = 0;

		initialRotateDirection = 0;
		initialRotateLastDirection = 0;
		initialHoldFlag = false;
		initialRotateContinuousUse = false;
		initialHoldContinuousUse = false;

		nowPieceMoveCount = 0;
		nowPieceRotateCount = 0;
		extendedMoveCount = 0;
		extendedRotateCount = 0;

		nowWallkickCount = 0;
		nowUpwardWallkickCount = 0;

		softdropFall = 0;
		harddropFall = 0;
		softdropContinuousUse = false;
		harddropContinuousUse = false;

		manualLock = false;

		lastmove = LASTMOVE_NONE;

		tspin = false;
		tspinmini = false;
		tspinez = false;
		b2b = false;
		b2bcount = 0;
		combo = 0;

		tspinEnable = false;
		tspinEnableEZ = false;
		tspinAllowKick = true;
		tspinminiType = TSPINMINI_TYPE_ROTATECHECK;
		spinCheckType = SPINTYPE_4POINT;
		useAllSpinBonus = false;
		b2bEnable = false;
		comboType = COMBO_TYPE_DISABLE;

		blockHidden = -1;
		blockHiddenAnim = true;
		blockOutlineType = BLOCK_OUTLINE_NORMAL;
		blockShowOutlineOnly = false;

		heboHiddenEnable = false;
		heboHiddenTimerNow = 0;
		heboHiddenTimerMax = 0;
		heboHiddenYNow = 0;
		heboHiddenYLimit = 0;

		delayCancel = false;
		delayCancelMoveLeft = false;
		delayCancelMoveRight = false;

		bone = false;

		big = false;
		bigmove = true;
		bighalf = true;

		kickused = false;

		fieldWidth = -1;
		fieldHeight = -1;
		fieldHiddenHeight = -1;

		ending = 0;
		staffrollEnable = false;
		staffrollNoDeath = false;
		staffrollEnableStatistics = false;

		framecolor = FRAME_COLOR_BLUE;

		readyStart = READY_START;
		readyEnd = READY_END;
		goStart = GO_START;
		goEnd = GO_END;

		readyDone = false;

		lives = 0;

		ghost = true;

		meterValue = 0;
		meterColor = METER_COLOR_RED;

		lagARE = false;
		lagStop = false;
		displaysize = (playerID >= 2) ? -1 : 0;

		enableSE = true;
		gameoverAll = true;

		isNextVisible = true;
		isHoldVisible = true;
		isVisible = true;

		holdButtonNextSkip = false;

		allowTextRenderByReceiver = true;

		itemRollRollEnable = false;
		itemRollRollInterval = 30;

		itemXRayEnable = false;
		itemXRayCount = 0;

		itemColorEnable = false;
		itemColorCount = 0;

		interruptItemNumber = INTERRUPTITEM_NONE;

		clearMode = CLEAR_LINE;
		colorClearSize = -1;
		garbageColorClear = false;
		ignoreHidden = false;
		connectBlocks = true;
		lineColorDiagonals = false;
		blockColors = BLOCK_COLORS_DEFAULT;
		cascadeDelay = 0;
		cascadeClearDelay = 0;
		
		rainbowAnimate = false;

		// イベント発生
		if(owner.mode != null) {
			owner.mode.playerInit(this, playerID);
			if(owner.replayMode) owner.mode.loadReplay(this, playerID, owner.replayProp);
		}
		owner.receiver.playerInit(this, playerID);
		if(ai != null) {
			ai.shutdown(this, playerID);
			ai.init(this, playerID);
		}
	}

	/**
	 * 終了処理
	 */
	public void shutdown() {
		//log.debug("GameEngine shutdown() playerID:" + playerID);

		if(ai != null) ai.shutdown(this, playerID);
		owner = null;
		ruleopt = null;
		wallkick = null;
		randomizer = null;
		field = null;
		ctrl = null;
		statistics = null;
		speed = null;
		random = null;
		replayData = null;
	}

	/**
	 * ステータスカウンタInitialization
	 */
	public void resetStatc() {
		for(int i = 0; i < statc.length; i++) statc[i] = 0;
	}

	/**
	 * 効果音を再生する（enableSEがtrueのときだけ）
	 * @param name 効果音の名前
	 */
	public void playSE(String name) {
		if(enableSE) owner.receiver.playSE(name);
	}

	/**
	 * NEXTピースのIDを取得
	 * @param c 取得したいNEXTの位置
	 * @return NEXTピースのID
	 */
	public int getNextID(int c) {
		if(nextPieceArrayID == null) return Piece.PIECE_NONE;
		int c2 = c;
		while(c2 >= nextPieceArrayID.length) c2 = c2 - nextPieceArrayID.length;
		return nextPieceArrayID[c2];
	}

	/**
	 * NEXTピースのオブジェクトを取得
	 * @param c 取得したいNEXTの位置
	 * @return NEXTピースのオブジェクト
	 */
	public Piece getNextObject(int c) {
		if(nextPieceArrayObject == null) return null;
		int c2 = c;
		while(c2 >= nextPieceArrayObject.length) c2 = c2 - nextPieceArrayObject.length;
		return nextPieceArrayObject[c2];
	}

	/**
	 * NEXTピースのオブジェクトのコピーを取得
	 * @param c 取得したいNEXTの位置
	 * @return NEXTピースのオブジェクトのコピー
	 */
	public Piece getNextObjectCopy(int c) {
		Piece p = getNextObject(c);
		Piece r = null;
		if(p != null) r = new Piece(p);
		return r;
	}

	/**
	 * Current AREの値を取得（ルール設定も考慮）
	 * @return Current ARE
	 */
	public int getARE() {
		if((speed.are < ruleopt.minARE) && (ruleopt.minARE >= 0)) return ruleopt.minARE;
		if((speed.are > ruleopt.maxARE) && (ruleopt.maxARE >= 0)) return ruleopt.maxARE;
		return speed.are;
	}

	/**
	 * Current ARE after line clearの値を取得（ルール設定も考慮）
	 * @return Current ARE after line clear
	 */
	public int getARELine() {
		if((speed.areLine < ruleopt.minARELine) && (ruleopt.minARELine >= 0)) return ruleopt.minARELine;
		if((speed.areLine > ruleopt.maxARELine) && (ruleopt.maxARELine >= 0)) return ruleopt.maxARELine;
		return speed.areLine;
	}

	/**
	 * Current Line clear timeの値を取得（ルール設定も考慮）
	 * @return Current Line clear time
	 */
	public int getLineDelay() {
		if((speed.lineDelay < ruleopt.minLineDelay) && (ruleopt.minLineDelay >= 0)) return ruleopt.minLineDelay;
		if((speed.lineDelay > ruleopt.maxLineDelay) && (ruleopt.maxLineDelay >= 0)) return ruleopt.maxLineDelay;
		return speed.lineDelay;
	}

	/**
	 * Current 固定 timeの値を取得（ルール設定も考慮）
	 * @return Current 固定 time
	 */
	public int getLockDelay() {
		if((speed.lockDelay < ruleopt.minLockDelay) && (ruleopt.minLockDelay >= 0)) return ruleopt.minLockDelay;
		if((speed.lockDelay > ruleopt.maxLockDelay) && (ruleopt.maxLockDelay >= 0)) return ruleopt.maxLockDelay;
		return speed.lockDelay;
	}

	/**
	 * Current DASの値を取得（ルール設定も考慮）
	 * @return Current DAS
	 */
	public int getDAS() {
		if((speed.das < owMinDAS) && (owMinDAS >= 0)) return owMinDAS;
		if((speed.das > owMaxDAS) && (owMaxDAS >= 0)) return owMaxDAS;
		if((speed.das < ruleopt.minDAS) && (ruleopt.minDAS >= 0)) return ruleopt.minDAS;
		if((speed.das > ruleopt.maxDAS) && (ruleopt.maxDAS >= 0)) return ruleopt.maxDAS;
		return speed.das;
	}

	/**
	 * Current 横移動速度を取得
	 * @return 横移動速度
	 */
	public int getDASDelay() {
		if((ruleopt == null) || (owDasDelay >= 0)) {
			return owDasDelay;
		}
		return ruleopt.dasDelay;
	}

	/**
	 * 現在使用中のBlockスキン numberを取得
	 * @return Blockスキン number
	 */
	public int getSkin() {
		if((ruleopt == null) || (owSkin >= 0)) {
			return owSkin;
		}
		return ruleopt.skin;
	}

	/**
	 * @return A buttonを押したときに左回転するならfalse、右回転するならtrue
	 */
	public boolean isRotateButtonDefaultRight() {
		if((ruleopt == null) || (owRotateButtonDefaultRight >= 0)) {
			if(owRotateButtonDefaultRight == 0) return false;
			else return true;
		}
		return ruleopt.rotateButtonDefaultRight;
	}

	/**
	 * 見え／消えRoll 状態のフィールドを通常状態に戻す
	 */
	public void resetFieldVisible() {
		if(field != null) {
			for(int x = 0; x < field.getWidth(); x++) {
				for(int y = 0; y < field.getHeight(); y++) {
					Block blk = field.getBlock(x, y);

					if((blk != null) && (blk.color > Block.BLOCK_COLOR_NONE)) {
						blk.alpha = 1f;
						blk.darkness = 0f;
						blk.setAttribute(Block.BLOCK_ATTRIBUTE_VISIBLE, true);
						blk.setAttribute(Block.BLOCK_ATTRIBUTE_OUTLINE, true);
					}
				}
			}
		}
	}

	/**
	 * ソフト・Hard drop・先行ホールド・先行回転の使用制限解除
	 */
	public void checkDropContinuousUse() {
		if(gameActive) {
			if((!ctrl.isPress(Controller.BUTTON_DOWN)) || (!ruleopt.softdropLimit))
				softdropContinuousUse = false;
			if((!ctrl.isPress(Controller.BUTTON_UP)) || (!ruleopt.harddropLimit))
				harddropContinuousUse = false;
			if((!ctrl.isPress(Controller.BUTTON_D)) || (!ruleopt.holdInitialLimit))
				initialHoldContinuousUse = false;
			if(!ruleopt.rotateInitialLimit)
				initialRotateContinuousUse = false;

			if(initialRotateContinuousUse) {
				int dir = 0;
				if(ctrl.isPress(Controller.BUTTON_A) || ctrl.isPress(Controller.BUTTON_C)) dir = -1;
				else if(ctrl.isPress(Controller.BUTTON_B)) dir = 1;
				else if(ctrl.isPress(Controller.BUTTON_E)) dir = 2;

				if((initialRotateLastDirection != dir) || (dir == 0))
					initialRotateContinuousUse = false;
			}
		}
	}

	/**
	 * 横移動入力のDirectionを取得
	 * @return -1:左 0:なし 1:右
	 */
	public int getMoveDirection() {
		if(ctrl.isPress(Controller.BUTTON_LEFT) && ctrl.isPress(Controller.BUTTON_RIGHT)) {
			if(ruleopt.moveLeftAndRightAllow) {
				if(ctrl.buttonTime[Controller.BUTTON_LEFT] > ctrl.buttonTime[Controller.BUTTON_RIGHT])
					return ruleopt.moveLeftAndRightUsePreviousInput ? -1 : 1;
				else if(ctrl.buttonTime[Controller.BUTTON_LEFT] < ctrl.buttonTime[Controller.BUTTON_RIGHT])
					return ruleopt.moveLeftAndRightUsePreviousInput ? 1 : -1;
			}
		} else if(ctrl.isPress(Controller.BUTTON_LEFT)) {
			return -1;
		} else if(ctrl.isPress(Controller.BUTTON_RIGHT)) {
			return 1;
		}

		return 0;
	}

	/**
	 * 横溜め処理
	 */
	public void padRepeat() {
		int moveDirection = getMoveDirection();
		if(moveDirection != 0) {
			dasCount++;
		} else if(!ruleopt.dasStoreChargeOnNeutral) {
			dasCount = 0;
		}
		dasDirection = moveDirection;
	}

	/**
	 * Called if delay doesn't allow charging but dasRedirectInDelay == true
	 * Updates dasDirection so player can change direction without dropping charge on entry.
	 */
	public void dasRedirect() {
      dasDirection = getMoveDirection();
	}

	/**
	 * 移動 count制限を超過しているか判定
	 * @return 移動 count制限を超過したらtrue
	 */
	public boolean isMoveCountExceed() {
		if(ruleopt.lockresetLimitShareCount == true) {
			if((extendedMoveCount + extendedRotateCount >= ruleopt.lockresetLimitMove) && (ruleopt.lockresetLimitMove >= 0))
				return true;
		} else {
			if((extendedMoveCount >= ruleopt.lockresetLimitMove) && (ruleopt.lockresetLimitMove >= 0))
				return true;
		}

		return false;
	}

	/**
	 * 回転 count制限を超過しているか判定
	 * @return 回転 count制限を超過したらtrue
	 */
	public boolean isRotateCountExceed() {
		if(ruleopt.lockresetLimitShareCount == true) {
			if((extendedMoveCount + extendedRotateCount >= ruleopt.lockresetLimitMove) && (ruleopt.lockresetLimitMove >= 0))
				return true;
		} else {
			if((extendedRotateCount >= ruleopt.lockresetLimitRotate) && (ruleopt.lockresetLimitRotate >= 0))
				return true;
		}

		return false;
	}

	/**
	 * T-Spin routine
	 * @param x X coord
	 * @param y Y coord
	 * @param piece Current piece object
	 * @param fld Field object
	 */
	public void setTSpin(int x, int y, Piece piece, Field fld) {
		if((piece == null) || (piece.id != Piece.PIECE_T)) {
			tspin = false;
			return;
		}

		if(!tspinAllowKick && kickused) {
			tspin = false;
			return;
		}

		if(spinCheckType == SPINTYPE_4POINT) {
			if(tspinminiType == TSPINMINI_TYPE_ROTATECHECK) {
				if(nowPieceObject.checkCollision(nowPieceX, nowPieceY, getRotateDirection(-1), field) &&
						   nowPieceObject.checkCollision(nowPieceX, nowPieceY, getRotateDirection( 1), field))
							tspinmini = true;
			} else if(tspinminiType == TSPINMINI_TYPE_WALLKICKFLAG) {
				tspinmini = kickused;
			}

			int[] tx = new int[4];
			int[] ty = new int[4];

			// Setup 4-point coordinates
			if(piece.big == true) {
				tx[0] = 1;
				ty[0] = 1;
				tx[1] = 4;
				ty[1] = 1;
				tx[2] = 1;
				ty[2] = 4;
				tx[3] = 4;
				ty[3] = 4;
			} else {
				tx[0] = 0;
				ty[0] = 0;
				tx[1] = 2;
				ty[1] = 0;
				tx[2] = 0;
				ty[2] = 2;
				tx[3] = 2;
				ty[3] = 2;
			}
			for(int i = 0; i < tx.length; i++) {
				if(piece.big) {
					tx[i] += ruleopt.pieceOffsetX[piece.id][piece.direction] * 2;
					ty[i] += ruleopt.pieceOffsetY[piece.id][piece.direction] * 2;
				} else {
					tx[i] += ruleopt.pieceOffsetX[piece.id][piece.direction];
					ty[i] += ruleopt.pieceOffsetY[piece.id][piece.direction];
				}
			}

			// Check the corner of the T piece
			int count = 0;

			for(int i = 0; i < tx.length; i++) {
				if(fld.getBlockColor(x + tx[i], y + ty[i]) != Block.BLOCK_COLOR_NONE) count++;
			}

			if(count >= 3) tspin = true;
		} else if(spinCheckType == SPINTYPE_IMMOBILE) {
			if( piece.checkCollision(x, y - 1, fld) &&
					piece.checkCollision(x + 1, y, fld) &&
					piece.checkCollision(x - 1, y, fld) ) {
				tspin = true;
				Field copyField = new Field(fld);
				piece.placeToField(x, y, copyField);
				if((copyField.checkLineNoFlag() == 1) && (kickused == true)) tspinmini = true;
			} else if((tspinEnableEZ) && (kickused == true)) {
				tspin = true;
				tspinez = true;
			}
		}
	}

	/**
	 * Spin判定(全スピンルールのとき用)
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param piece Current Blockピース
	 * @param fld フィールド
	 */
	public void setAllSpin(int x, int y, Piece piece, Field fld) {
		tspin = false;
		tspinmini = false;
		tspinez = false;

		if(piece == null) return;
		if(!tspinAllowKick && kickused) return;
		if(piece.big) return;

		if(spinCheckType == SPINTYPE_4POINT) {

			int offsetX = ruleopt.pieceOffsetX[piece.id][piece.direction];
			int offsetY = ruleopt.pieceOffsetY[piece.id][piece.direction];

			for(int i = 0; i < Piece.SPINBONUSDATA_HIGH_X[piece.id][piece.direction].length / 2; i++) {
				boolean isHighSpot1 = false;
				boolean isHighSpot2 = false;
				boolean isLowSpot1 = false;
				boolean isLowSpot2 = false;

				if(!fld.getBlockEmptyF(
					x + Piece.SPINBONUSDATA_HIGH_X[piece.id][piece.direction][i * 2 + 0] + offsetX,
					y + Piece.SPINBONUSDATA_HIGH_Y[piece.id][piece.direction][i * 2 + 0] + offsetY))
				{
					isHighSpot1 = true;
				}
				if(!fld.getBlockEmptyF(
					x + Piece.SPINBONUSDATA_HIGH_X[piece.id][piece.direction][i * 2 + 1] + offsetX,
					y + Piece.SPINBONUSDATA_HIGH_Y[piece.id][piece.direction][i * 2 + 1] + offsetY))
				{
					isHighSpot2 = true;
				}
				if(!fld.getBlockEmptyF(
					x + Piece.SPINBONUSDATA_LOW_X[piece.id][piece.direction][i * 2 + 0] + offsetX,
					y + Piece.SPINBONUSDATA_LOW_Y[piece.id][piece.direction][i * 2 + 0] + offsetY))
				{
					isLowSpot1 = true;
				}
				if(!fld.getBlockEmptyF(
					x + Piece.SPINBONUSDATA_LOW_X[piece.id][piece.direction][i * 2 + 1] + offsetX,
					y + Piece.SPINBONUSDATA_LOW_Y[piece.id][piece.direction][i * 2 + 1] + offsetY))
				{
					isLowSpot2 = true;
				}

				//log.debug(isHighSpot1 + "," + isHighSpot2 + "," + isLowSpot1 + "," + isLowSpot2);

				if(isHighSpot1 && isHighSpot2 && (isLowSpot1 || isLowSpot2)) {
					tspin = true;
				} else if(!tspin && isLowSpot1 && isLowSpot2 && (isHighSpot1 || isHighSpot2)) {
					tspin = true;
					tspinmini = true;
				}
			}
		} else if(spinCheckType == SPINTYPE_IMMOBILE) {
			int y2 = y - 1;
			log.debug(x + "," + y2 + ":" + piece.checkCollision(x, y2, fld));

			if( piece.checkCollision(x, y - 1, fld) &&
					piece.checkCollision(x + 1, y, fld) &&
					piece.checkCollision(x - 1, y, fld) ) {
				tspin = true;
				Field copyField = new Field(fld);
				piece.placeToField(x, y, copyField);
				if((copyField.checkLineNoFlag() == 1) && (kickused == true)) tspinmini = true;
			} else if((tspinEnableEZ) && (kickused == true)) {
				tspin = true;
				tspinez = true;
			}
		}
	}

	/**
	 * ホールド可能かどうか判定
	 * @return ホールド可能ならtrue
	 */
	public boolean isHoldOK() {
		if( (!ruleopt.holdEnable) || (holdDisable) || ((holdUsedCount >= ruleopt.holdLimit) && (ruleopt.holdLimit >= 0)) || (initialHoldContinuousUse) )
			return false;

		return true;
	}

	/**
	 * ピースが出現するX-coordinateを取得
	 * @param fld フィールド
	 * @param piece ピース
	 * @return 出現位置のX-coordinate
	 */
	public int getSpawnPosX(Field fld, Piece piece) {
		int x = -1 + (fld.getWidth() - piece.getWidth() + 1) / 2;

		if((big == true) && (bigmove == true) && (x % 2 != 0))
			x++;

		if(big == true) {
			x += ruleopt.pieceSpawnXBig[piece.id][piece.direction];
		} else {
			x += ruleopt.pieceSpawnX[piece.id][piece.direction];
		}

		return x;
	}

	/**
	 * ピースが出現するY-coordinateを取得
	 * @param piece ピース
	 * @return 出現位置のY-coordinate
	 */
	public int getSpawnPosY(Piece piece) {
		int y = 0;

		if((ruleopt.pieceEnterAboveField == true) && (ruleopt.fieldCeiling == false)) {
			y = -1 - piece.getMaximumBlockY();
			if(big == true) y--;
		} else {
			y = -piece.getMinimumBlockY();
		}

		if(big == true) {
			y += ruleopt.pieceSpawnYBig[piece.id][piece.direction];
		} else {
			y += ruleopt.pieceSpawnY[piece.id][piece.direction];
		}

		return y;
	}

	/**
	 * 回転 buttonを押したあとのピースのDirectionを取得
	 * @param move 回転Direction（-1:左 1:右 2:180度）
	 * @return 回転 buttonを押したあとのピースのDirection
	 */
	public int getRotateDirection(int move) {
		int rt = 0 + move;
		if(nowPieceObject != null) rt = nowPieceObject.direction + move;

		if(move == 2) {
			if(rt > 3) rt -= 4;
			if(rt < 0) rt += 4;
		} else {
			if(rt > 3) rt = 0;
			if(rt < 0) rt = 3;
		}

		return rt;
	}

	/**
	 * 先行回転と先行ホールドの処理
	 */
	public void initialRotate() {
		initialRotateDirection = 0;
		initialHoldFlag = false;

		if((ruleopt.rotateInitial == true) && (initialRotateContinuousUse == false)) {
			int dir = 0;
			if(ctrl.isPress(Controller.BUTTON_A) || ctrl.isPress(Controller.BUTTON_C)) dir = -1;
			else if(ctrl.isPress(Controller.BUTTON_B)) dir = 1;
			else if(ctrl.isPress(Controller.BUTTON_E)) dir = 2;
			initialRotateDirection = dir;
		}

		if((ctrl.isPress(Controller.BUTTON_D)) && (ruleopt.holdInitial == true) && isHoldOK()) {
			initialHoldFlag = true;
			initialHoldContinuousUse = true;
			playSE("initialhold");
		}
	}

	/**
	 * フィールドのBlockの状態を更新
	 */
	public void fieldUpdate() {
		if(field != null) {
			for(int i = 0; i < field.getWidth(); i++) {
				for(int j = (field.getHiddenHeight() * -1); j < field.getHeight(); j++) {
					Block blk = field.getBlock(i, j);

					if((blk != null) && (blk.color >= Block.BLOCK_COLOR_GRAY)) {
						if(blk.elapsedFrames < 0) {
							if(!blk.getAttribute(Block.BLOCK_ATTRIBUTE_GARBAGE))
								blk.darkness = 0f;
						} else if(blk.elapsedFrames < ruleopt.lockflash) {
							blk.darkness = -0.8f;
							if(blockShowOutlineOnly) {
								blk.setAttribute(Block.BLOCK_ATTRIBUTE_OUTLINE, true);
								blk.setAttribute(Block.BLOCK_ATTRIBUTE_VISIBLE, false);
								blk.setAttribute(Block.BLOCK_ATTRIBUTE_BONE, false);
							}
						} else {
							blk.darkness = 0f;
							blk.setAttribute(Block.BLOCK_ATTRIBUTE_OUTLINE, true);
							if(blockShowOutlineOnly) {
								blk.setAttribute(Block.BLOCK_ATTRIBUTE_VISIBLE, false);
								blk.setAttribute(Block.BLOCK_ATTRIBUTE_BONE, false);
							}
						}

						if((blockHidden != -1) && (blk.elapsedFrames >= blockHidden - 10) && (gameActive == true)) {
							if(blockHiddenAnim == true) {
								blk.alpha -= 0.1f;
								if(blk.alpha < 0.0f) blk.alpha = 0.0f;
							}

							if(blk.elapsedFrames >= blockHidden) {
								blk.alpha = 0.0f;
								blk.setAttribute(Block.BLOCK_ATTRIBUTE_OUTLINE, false);
								blk.setAttribute(Block.BLOCK_ATTRIBUTE_VISIBLE, false);
							}
						}

						if(blk.elapsedFrames >= 0) blk.elapsedFrames++;
					}
				}
			}
		}

		// X-RAY
		if((field != null) && (gameActive) && (itemXRayEnable)) {
			for(int i = 0; i < field.getWidth(); i++) {
				for(int j = (field.getHiddenHeight() * -1); j < field.getHeight(); j++) {
					Block blk = field.getBlock(i, j);

					if((blk != null) && (blk.color >= Block.BLOCK_COLOR_GRAY)) {
						blk.setAttribute(Block.BLOCK_ATTRIBUTE_VISIBLE, (itemXRayCount % 36 == i));
						blk.setAttribute(Block.BLOCK_ATTRIBUTE_OUTLINE, (itemXRayCount % 36 == i));
					}
				}
			}
			itemXRayCount++;
		} else {
			itemXRayCount = 0;
		}

		// COLOR
		if((field != null) && (gameActive) && (itemColorEnable)) {
			for(int i = 0; i < field.getWidth(); i++) {
				for(int j = (field.getHiddenHeight() * -1); j < field.getHeight(); j++) {
					int bright = j;
					if(bright >= 5) bright = 9 - bright;
					bright = 40 - ( (((20 - i) + bright) * 4 + itemColorCount) % 40 );
					if((bright >= 0) && (bright < ITEM_COLOR_BRIGHT_TABLE.length)) {
						bright = 10 - ITEM_COLOR_BRIGHT_TABLE[bright];
					}
					if(bright > 10) bright = 10;

					Block blk = field.getBlock(i, j);

					if(blk != null) {
						blk.alpha = bright * 0.1f;
						blk.setAttribute(Block.BLOCK_ATTRIBUTE_OUTLINE, false);
						blk.setAttribute(Block.BLOCK_ATTRIBUTE_VISIBLE, true);
					}
				}
			}
			itemColorCount++;
		} else {
			itemColorCount = 0;
		}

		// ヘボHIDDEN
		if(heboHiddenEnable && gameActive) {
			heboHiddenTimerNow++;

			if(heboHiddenTimerNow > heboHiddenTimerMax) {
				heboHiddenTimerNow = 0;
				heboHiddenYNow++;
				if(heboHiddenYNow > heboHiddenYLimit)
					heboHiddenYNow = heboHiddenYLimit;
			}
		}
	}

	/**
	 * Called when saving replay
	 */
	public void saveReplay() {
		if((owner.replayMode == true) && (owner.replayRerecord == false)) return;

		owner.replayProp.setProperty("version.core", versionMajor + "." + versionMinor);
		owner.replayProp.setProperty("version.core.major", versionMajor);
		owner.replayProp.setProperty("version.core.minor", versionMinor);

		owner.replayProp.setProperty(playerID + ".replay.randSeed", Long.toString(randSeed, 16));

		replayData.writeProperty(owner.replayProp, playerID, replayTimer);
		statistics.writeProperty(owner.replayProp, playerID);
		ruleopt.writeProperty(owner.replayProp, playerID);

		if(playerID == 0) {
			if(owner.mode != null) owner.replayProp.setProperty("name.mode", owner.mode.getName());
			if(ruleopt.strRuleName != null) owner.replayProp.setProperty("name.rule", ruleopt.strRuleName);

			GregorianCalendar currentTime = new GregorianCalendar();
			int month = currentTime.get(Calendar.MONTH) + 1;
			String strDate = String.format("%04d/%02d/%02d", currentTime.get(Calendar.YEAR), month, currentTime.get(Calendar.DATE));
			String strTime = String.format("%02d:%02d:%02d",
											currentTime.get(Calendar.HOUR_OF_DAY), currentTime.get(Calendar.MINUTE), currentTime.get(Calendar.SECOND));
			owner.replayProp.setProperty("timestamp.date", strDate);
			owner.replayProp.setProperty("timestamp.time", strTime);
		}

		owner.replayProp.setProperty(playerID + ".tuning.owRotateButtonDefaultRight", owRotateButtonDefaultRight);
		owner.replayProp.setProperty(playerID + ".tuning.owSkin", owSkin);
		owner.replayProp.setProperty(playerID + ".tuning.owMinDAS", owMinDAS);
		owner.replayProp.setProperty(playerID + ".tuning.owMaxDAS", owMaxDAS);
		owner.replayProp.setProperty(playerID + ".tuning.owDasDelay", owDasDelay);

		if(owner.mode != null) owner.mode.saveReplay(this, playerID, owner.replayProp);
	}

	/**
	 * フィールドエディット画面に入る処理
	 */
	public void enterFieldEdit() {
		fldeditPreviousStat = stat;
		stat = STAT_FIELDEDIT;
		fldeditX = 0;
		fldeditY = 0;
		fldeditColor = Block.BLOCK_COLOR_GRAY;
		fldeditFrames = 0;
		owner.menuOnly = false;
		createFieldIfNeeded();
	}

	/**
	 * フィールドをInitialization（まだ存在しない場合）
	 */
	public void createFieldIfNeeded() {
		if(fieldWidth < 0) fieldWidth = ruleopt.fieldWidth;
		if(fieldHeight < 0) fieldHeight = ruleopt.fieldHeight;
		if(fieldHiddenHeight < 0) fieldHiddenHeight = ruleopt.fieldHiddenHeight;
		if(field == null) field = new Field(fieldWidth, fieldHeight, fieldHiddenHeight, ruleopt.fieldCeiling);
	}

	/**
	 * ゲームの状態の更新
	 */
	public void update() {
		if(gameActive) {
			// リプレイ関連の処理
			if(!owner.replayMode || owner.replayRerecord) {
				// AIの button処理
				if (ai != null) {
					if (aiShowHint == false) {
						ai.setControl(this, playerID, ctrl);
					} else {
						aiHintReady = (ai.thinkCurrentPieceNo == ai.thinkLastPieceNo)
								&& (ai.thinkCurrentPieceNo > 0)
								&& !(ai.bestHold || ai.forceHold);
						if (aiHintReady) {
							aiHintX = ai.bestX;
							aiHintY = ai.bestY;
							aiHintRt = ai.bestRt;
						}
					}
				}

				// 入力状態をリプレイに記録
				replayData.setInputData(ctrl.getButtonBit(), replayTimer);
			} else {
				// 入力状態をリプレイから読み込み
				ctrl.setButtonBit(replayData.getInputData(replayTimer));
			}
			replayTimer++;
		}

		//  button入力 timeの更新
		ctrl.updateButtonTime();

		// 最初の処理
		if(owner.mode != null) owner.mode.onFirst(this, playerID);
		owner.receiver.onFirst(this, playerID);
		if((ai != null) && (!owner.replayMode || owner.replayRerecord)) ai.onFirst(this, playerID);

		// 各ステータスの処理
		if(!lagStop) {
			switch(stat) {
			case STAT_NOTHING:
				break;
			case STAT_SETTING:
				statSetting();
				break;
			case STAT_READY:
				statReady();
				break;
			case STAT_MOVE:
				dasRepeat = true;
				dasInstant = false;
				while(dasRepeat){
					statMove();
				}
				break;
			case STAT_LOCKFLASH:
				statLockFlash();
				break;
			case STAT_LINECLEAR:
				statLineClear();
				break;
			case STAT_ARE:
				statARE();
				break;
			case STAT_ENDINGSTART:
				statEndingStart();
				break;
			case STAT_CUSTOM:
				statCustom();
				break;
			case STAT_EXCELLENT:
				statExcellent();
				break;
			case STAT_GAMEOVER:
				statGameOver();
				break;
			case STAT_RESULT:
				statResult();
				break;
			case STAT_FIELDEDIT:
				statFieldEdit();
				break;
			case STAT_INTERRUPTITEM:
				statInterruptItem();
				break;
			}
		}

		// フィールドのBlockの状態や統計情報を更新
		fieldUpdate();
		if((ending == 0) || (staffrollEnableStatistics)) statistics.update();

		// 最後の処理
		if(owner.mode != null) owner.mode.onLast(this, playerID);
		owner.receiver.onLast(this, playerID);
		if((ai != null) && (!owner.replayMode || owner.replayRerecord)) ai.onLast(this, playerID);

		// タイマー増加
		if(gameActive && timerActive) {
			statistics.time++;
		}
	}

	/**
	 * 画面描画
	 * （各Mode やイベント処理クラスのイベントを呼び出すだけで、それ以外にGameEngine自身は何もしません）
	 */
	public void render() {
		// 最初の処理
		if(owner.mode != null) owner.mode.renderFirst(this, playerID);
		owner.receiver.renderFirst(this, playerID);
		
		if (rainbowAnimate)
			Block.updateRainbowPhase(this);

		// 各ステータスの処理
		switch(stat) {
		case STAT_NOTHING:
			break;
		case STAT_SETTING:
			if(owner.mode != null) owner.mode.renderSetting(this, playerID);
			owner.receiver.renderSetting(this, playerID);
			break;
		case STAT_READY:
			if(owner.mode != null) owner.mode.renderReady(this, playerID);
			owner.receiver.renderReady(this, playerID);
			break;
		case STAT_MOVE:
			if(owner.mode != null) owner.mode.renderMove(this, playerID);
			owner.receiver.renderMove(this, playerID);
			break;
		case STAT_LOCKFLASH:
			if(owner.mode != null) owner.mode.renderLockFlash(this, playerID);
			owner.receiver.renderLockFlash(this, playerID);
			break;
		case STAT_LINECLEAR:
			if(owner.mode != null) owner.mode.renderLineClear(this, playerID);
			owner.receiver.renderLineClear(this, playerID);
			break;
		case STAT_ARE:
			if(owner.mode != null) owner.mode.renderARE(this, playerID);
			owner.receiver.renderARE(this, playerID);
			break;
		case STAT_ENDINGSTART:
			if(owner.mode != null) owner.mode.renderEndingStart(this, playerID);
			owner.receiver.renderEndingStart(this, playerID);
			break;
		case STAT_CUSTOM:
			if(owner.mode != null) owner.mode.renderCustom(this, playerID);
			owner.receiver.renderCustom(this, playerID);
			break;
		case STAT_EXCELLENT:
			if(owner.mode != null) owner.mode.renderExcellent(this, playerID);
			owner.receiver.renderExcellent(this, playerID);
			break;
		case STAT_GAMEOVER:
			if(owner.mode != null) owner.mode.renderGameOver(this, playerID);
			owner.receiver.renderGameOver(this, playerID);
			break;
		case STAT_RESULT:
			if(owner.mode != null) owner.mode.renderResult(this, playerID);
			owner.receiver.renderResult(this, playerID);
			break;
		case STAT_FIELDEDIT:
			if(owner.mode != null) owner.mode.renderFieldEdit(this, playerID);
			owner.receiver.renderFieldEdit(this, playerID);
			break;
		case STAT_INTERRUPTITEM:
			break;
		}

		// 最後の処理
		if(owner.mode != null) owner.mode.renderLast(this, playerID);
		owner.receiver.renderLast(this, playerID);
	}

	/**
	 * 開始前の設定画面のときの処理
	 */
	public void statSetting() {
		// イベント発生
		if(owner.mode != null) {
			if(owner.mode.onSetting(this, playerID) == true) return;
		}
		owner.receiver.onSetting(this, playerID);

		// Mode側が何もしない場合はReady画面へ移動
		stat = STAT_READY;
		resetStatc();
	}

	/**
	 * Ready→Goのときの処理
	 */
	public void statReady() {
		// イベント発生
		if(owner.mode != null) {
			if(owner.mode.onReady(this, playerID) == true) return;
		}
		owner.receiver.onReady(this, playerID);

		// 横溜め
		if(ruleopt.dasInReady && gameActive) padRepeat();
		else if(ruleopt.dasRedirectInDelay) { dasRedirect(); }

		// Initialization
		if(statc[0] == 0) {
			// フィールドInitialization
			createFieldIfNeeded();

			// NEXTピース作成
			if(nextPieceArrayID == null) {
				// 出現可能なピースが1つもない場合は全て出現できるようにする
				boolean allDisable = true;
				for(int i = 0; i < nextPieceEnable.length; i++) {
					if(nextPieceEnable[i] == true) {
						allDisable = false;
						break;
					}
				}
				if(allDisable == true) {
					for(int i = 0; i < nextPieceEnable.length; i++) nextPieceEnable[i] = true;
				}

				// NEXTピースの出現順を作成
				if(randomizer == null) {
					randomizer = new MemorylessRandomizer(nextPieceEnable, randSeed);
				} else {
					randomizer.setState(nextPieceEnable, randSeed);
				}
				nextPieceArrayID = new int[nextPieceArraySize];
				for (int i = 0; i < nextPieceArraySize; i++) {
					nextPieceArrayID[i] = randomizer.next();
				}
			}
			// NEXTピースのオブジェクトを作成
			if(nextPieceArrayObject == null) {
				nextPieceArrayObject = new Piece[nextPieceArrayID.length];

				for(int i = 0; i < nextPieceArrayObject.length; i++) {
					nextPieceArrayObject[i] = new Piece(nextPieceArrayID[i]);
					nextPieceArrayObject[i].direction = ruleopt.pieceDefaultDirection[nextPieceArrayObject[i].id];
					if(nextPieceArrayObject[i].direction >= Piece.DIRECTION_COUNT) {
						nextPieceArrayObject[i].direction = random.nextInt(Piece.DIRECTION_COUNT);
					}
					nextPieceArrayObject[i].setColor(ruleopt.pieceColor[nextPieceArrayObject[i].id]);
					nextPieceArrayObject[i].setSkin(getSkin());
					nextPieceArrayObject[i].updateConnectData();
					nextPieceArrayObject[i].setAttribute(Block.BLOCK_ATTRIBUTE_VISIBLE, true);
					nextPieceArrayObject[i].setAttribute(Block.BLOCK_ATTRIBUTE_BONE, bone);
					nextPieceArrayObject[i].connectBlocks = this.connectBlocks;
				}
				if (randomBlockColor)
				{
					if (blockColors.length < numColors || numColors < 1)
						numColors = blockColors.length;
					for(int i = 0; i < nextPieceArrayObject.length; i++) {
						int size = nextPieceArrayObject[i].getMaxBlock();
						int[] colors = new int[size];
						for (int j = 0; j < size; j++)
							colors[j] = blockColors[random.nextInt(numColors)];
						nextPieceArrayObject[i].setColor(colors);
					}
				}
			}

			if(!readyDone) {
				//  button入力状態リセット
				ctrl.reset();
				// ゲーム中 flagON
				gameActive = true;
			}
		}

		// READY音
		if(statc[0] == readyStart) playSE("ready");

		// GO音
		if(statc[0] == goStart) playSE("go");

		// NEXTスキップ
		if((statc[0] > 0) && (statc[0] < goEnd) && (holdButtonNextSkip) && (isHoldOK()) && (ctrl.isPush(Controller.BUTTON_D))) {
			playSE("initialhold");
			holdPieceObject = getNextObjectCopy(nextPieceCount);
			holdPieceObject.applyOffsetArray(ruleopt.pieceOffsetX[holdPieceObject.id], ruleopt.pieceOffsetY[holdPieceObject.id]);
			nextPieceCount++;
			if(nextPieceCount < 0) nextPieceCount = 0;
		}

		// 開始
		if(statc[0] >= goEnd) {
			if(!readyDone) owner.bgmStatus.bgm = 0;
			if(owner.mode != null) owner.mode.startGame(this, playerID);
			owner.receiver.startGame(this, playerID);
			initialRotate();
			stat = STAT_MOVE;
			resetStatc();
			if(!readyDone) {
				startTime = System.currentTimeMillis();
			}
			readyDone = true;
			return;
		}

		statc[0]++;
	}

	/**
	 * Blockピースの移動処理
	 */
	public void statMove() {
		dasRepeat = false;

		// イベント発生
		if(owner.mode != null) {
			if(owner.mode.onMove(this, playerID) == true) return;
		}
		owner.receiver.onMove(this, playerID);

		// 横溜めInitialization
		int moveDirection = getMoveDirection();

		if((statc[0] > 0) || (ruleopt.dasInMoveFirstFrame)) {
			if(dasDirection != moveDirection) {
				dasDirection = moveDirection;
				if(!(dasDirection == 0 && ruleopt.dasStoreChargeOnNeutral)){
				   dasCount = 0;
				}
			}
		}

		// 出現時の処理
		if(statc[0] == 0) {
			if((statc[1] == 0) && (initialHoldFlag == false)) {
				// 通常出現
				nowPieceObject = getNextObjectCopy(nextPieceCount);
				nextPieceCount++;
				if(nextPieceCount < 0) nextPieceCount = 0;
				holdDisable = false;
			} else {
				// ホールド出現
				if(initialHoldFlag) {
					// 先行ホールド
					if(holdPieceObject == null) {
						// 1回目
						holdPieceObject = getNextObjectCopy(nextPieceCount);
						holdPieceObject.applyOffsetArray(ruleopt.pieceOffsetX[holdPieceObject.id], ruleopt.pieceOffsetY[holdPieceObject.id]);
						nextPieceCount++;
						if(nextPieceCount < 0) nextPieceCount = 0;

						if(bone == true) getNextObject(nextPieceCount + ruleopt.nextDisplay - 1).setAttribute(Block.BLOCK_ATTRIBUTE_BONE, true);

						nowPieceObject = getNextObjectCopy(nextPieceCount);
						nextPieceCount++;
						if(nextPieceCount < 0) nextPieceCount = 0;
					} else {
						// 2回目以降
						Piece pieceTemp = holdPieceObject;
						holdPieceObject = getNextObjectCopy(nextPieceCount);
						holdPieceObject.applyOffsetArray(ruleopt.pieceOffsetX[holdPieceObject.id], ruleopt.pieceOffsetY[holdPieceObject.id]);
						nowPieceObject = pieceTemp;
						nextPieceCount++;
						if(nextPieceCount < 0) nextPieceCount = 0;
					}
				} else {
					// 通常ホールド
					if(holdPieceObject == null) {
						// 1回目
						nowPieceObject.big = false;
						holdPieceObject = nowPieceObject;
						nowPieceObject = getNextObjectCopy(nextPieceCount);
						nextPieceCount++;
						if(nextPieceCount < 0) nextPieceCount = 0;
					} else {
						// 2回目以降
						nowPieceObject.big = false;
						Piece pieceTemp = holdPieceObject;
						holdPieceObject = nowPieceObject;
						nowPieceObject = pieceTemp;
					}
				}

				// Directionを戻す
				if((ruleopt.holdResetDirection) && (ruleopt.pieceDefaultDirection[holdPieceObject.id] < Piece.DIRECTION_COUNT)) {
					holdPieceObject.direction = ruleopt.pieceDefaultDirection[holdPieceObject.id];
					holdPieceObject.updateConnectData();
				}

				// 使用した count+1
				holdUsedCount++;
				statistics.totalHoldUsed++;

				// ホールド無効化
				initialHoldFlag = false;
				holdDisable = true;
			}
			playSE("piece" + getNextObject(nextPieceCount).id);

			if(nowPieceObject.offsetApplied == false)
				nowPieceObject.applyOffsetArray(ruleopt.pieceOffsetX[nowPieceObject.id], ruleopt.pieceOffsetY[nowPieceObject.id]);

			nowPieceObject.big = big;

			// 出現位置（横）
			nowPieceX = getSpawnPosX(field, nowPieceObject);

			// 出現位置（縦）
			nowPieceY = getSpawnPosY(nowPieceObject);

			nowPieceBottomY = nowPieceObject.getBottom(nowPieceX, nowPieceY, field);
			nowPieceColorOverride = -1;

			if(itemRollRollEnable) nowPieceColorOverride = Block.BLOCK_COLOR_GRAY;

			// 先行回転
			initialRotate();
			//if( (getARE() != 0) && ((getARELine() != 0) || (version < 6.3f)) ) initialRotate();

			if((speed.gravity > speed.denominator) && (speed.denominator > 0))
				gcount = speed.gravity % speed.denominator;
			else
				gcount = 0;

			lockDelayNow = 0;
			dasSpeedCount = getDASDelay();
			dasRepeat = false;
			dasInstant = false;
			extendedMoveCount = 0;
			extendedRotateCount = 0;
			softdropFall = 0;
			harddropFall = 0;
			manualLock = false;
			nowPieceMoveCount = 0;
			nowPieceRotateCount = 0;
			nowWallkickCount = 0;
			nowUpwardWallkickCount = 0;
			lineClearing = 0;
			lastmove = LASTMOVE_NONE;
			kickused = false;
			tspin = false;
			tspinmini = false;
			tspinez = false;

			getNextObject(nextPieceCount + ruleopt.nextDisplay - 1).setAttribute(Block.BLOCK_ATTRIBUTE_BONE, bone);

			if(ending == 0) timerActive = true;

			if((ai != null) && (!owner.replayMode || owner.replayRerecord)) ai.newPiece(this, playerID);
		}

		checkDropContinuousUse();

		boolean softdropUsed = false; // このフレームにSoft dropを使ったらtrue
		int softdropFallNow = 0; // このフレームのSoft dropで落下した段count

		boolean updown = false; // Up下同時押し flag
		if(ctrl.isPress(Controller.BUTTON_UP) && ctrl.isPress(Controller.BUTTON_DOWN)) updown = true;

		if(!dasInstant) {

			// ホールド
			if(ctrl.isPush(Controller.BUTTON_D) || initialHoldFlag) {
				if(isHoldOK()) {
					statc[0] = 0;
					statc[1] = 1;
					if(!initialHoldFlag) playSE("hold");
					initialHoldContinuousUse = true;
					initialHoldFlag = false;
					holdDisable = true;
					initialRotate();
					statMove();
					return;
				} else if((statc[0] > 0) && (!initialHoldFlag)) {
					playSE("holdfail");
				}
			}

			// 回転
			boolean onGroundBeforeRotate = nowPieceObject.checkCollision(nowPieceX, nowPieceY + 1, field);
			int move = 0;
			boolean rotated = false;

			if(initialRotateDirection != 0) {
				move = initialRotateDirection;
				initialRotateLastDirection = initialRotateDirection;
				initialRotateContinuousUse = true;
				playSE("initialrotate");
			} else if((statc[0] > 0) || (ruleopt.moveFirstFrame == true)) {
				if((itemRollRollEnable) && (replayTimer % itemRollRollInterval == 0)) move = 1;	// Roll Roll 

				//  button入力
				if(ctrl.isPush(Controller.BUTTON_A) || ctrl.isPush(Controller.BUTTON_C)) move = -1;
				else if(ctrl.isPush(Controller.BUTTON_B)) move = 1;
				else if(ctrl.isPush(Controller.BUTTON_E)) move = 2;

				if(move != 0) {
					initialRotateLastDirection = move;
					initialRotateContinuousUse = true;
				}
			}

			if((ruleopt.rotateButtonAllowDouble == false) && (move == 2)) move = -1;
			if((ruleopt.rotateButtonAllowReverse == false) && (move == 1)) move = -1;
			if(isRotateButtonDefaultRight() && (move != 2)) move = move * -1;

			if(move != 0) {
				// 回転後のDirectionを決める
				int rt = getRotateDirection(move);

				// 回転できるか判定
				if(nowPieceObject.checkCollision(nowPieceX, nowPieceY, rt, field) == false)
				{
					// Wallkickなしで回転できるとき
					rotated = true;
					kickused = false;
					nowPieceObject.direction = rt;
					nowPieceObject.updateConnectData();
				} else if( (ruleopt.rotateWallkick == true) &&
						   (wallkick != null) &&
						   ((initialRotateDirection == 0) || (ruleopt.rotateInitialWallkick == true)) &&
						   ((ruleopt.lockresetLimitOver != RuleOptions.LOCKRESET_LIMIT_OVER_NOWALLKICK) || (isRotateCountExceed() == false)) )
				{
					// Wallkickを試みる
					boolean allowUpward = (ruleopt.rotateMaxUpwardWallkick < 0) || (nowUpwardWallkickCount < ruleopt.rotateMaxUpwardWallkick);
					WallkickResult kick = wallkick.executeWallkick(nowPieceX, nowPieceY, move, nowPieceObject.direction, rt,
										  allowUpward, nowPieceObject, field, ctrl);

					if(kick != null) {
						rotated = true;
						kickused = true;
						nowWallkickCount++;
						if(kick.isUpward()) nowUpwardWallkickCount++;
						nowPieceObject.direction = kick.direction;
						nowPieceObject.updateConnectData();
						nowPieceX += kick.offsetX;
						nowPieceY += kick.offsetY;
					}
				}

				if(rotated == true) {
					// 回転成功
					nowPieceBottomY = nowPieceObject.getBottom(nowPieceX, nowPieceY, field);

					if((ruleopt.lockresetRotate == true) && (isRotateCountExceed() == false)) {
						lockDelayNow = 0;
						nowPieceObject.setDarkness(0f);
					}

					if(onGroundBeforeRotate) {
						extendedRotateCount++;
						lastmove = LASTMOVE_ROTATE_GROUND;
					} else {
						lastmove = LASTMOVE_ROTATE_AIR;
					}

					if(initialRotateDirection == 0) {
						playSE("rotate");
					}

					nowPieceRotateCount++;
					if((ending == 0) || (staffrollEnableStatistics)) statistics.totalPieceRotate++;
				} else {
					// 回転失敗
					playSE("rotfail");
				}
			}
			initialRotateDirection = 0;

			// ゲームオーバー check 
			if((statc[0] == 0) && (nowPieceObject.checkCollision(nowPieceX, nowPieceY, field) == true)) {
				// Blockの出現位置を上にずらすことができる場合はそうする
				for(int i = 0; i < ruleopt.pieceEnterMaxDistanceY; i++) {
					if(nowPieceObject.big) nowPieceY -= 2;
					else nowPieceY--;

					if(nowPieceObject.checkCollision(nowPieceX, nowPieceY, field) == false) {
						nowPieceBottomY = nowPieceObject.getBottom(nowPieceX, nowPieceY, field);
						break;
					}
				}

				// 死亡
				if(nowPieceObject.checkCollision(nowPieceX, nowPieceY, field) == true) {
					nowPieceObject.placeToField(nowPieceX, nowPieceY, field);
					nowPieceObject = null;
					stat = STAT_GAMEOVER;
					if((ending == 2) && (staffrollNoDeath)) stat = STAT_NOTHING;
					resetStatc();
					return;
				}
			}

		}

		int move = 0;
		boolean sidemoveflag = false;	// このフレームに横移動したらtrue

		if((statc[0] > 0) || (ruleopt.moveFirstFrame == true)) {
			// 横移動
			boolean onGroundBeforeMove = nowPieceObject.checkCollision(nowPieceX, nowPieceY + 1, field);

			move = moveDirection;

			if (statc[0] == 0 && delayCancel) {
				if (delayCancelMoveLeft) move = -1;
				if (delayCancelMoveRight) move = 1;
				dasCount = 0;
				// delayCancel = false;
				delayCancelMoveLeft = false;
				delayCancelMoveRight = false;
			} else if (statc[0] == 1 && delayCancel && (dasCount < getDAS())) {
				move = 0;
				delayCancel = false;
			}

			if(move != 0) sidemoveflag = true;

			if(big && bigmove) move *= 2;

			if((move != 0) && (dasCount == 0)) shiftLock = 0;

			if( (move != 0) && ((dasCount == 0) || (dasCount >= getDAS())) ) {
				shiftLock &= ctrl.getButtonBit();

				if(shiftLock == 0) {
					if( (dasSpeedCount >= getDASDelay()) || (dasCount == 0) ) {
						if(dasCount > 0) dasSpeedCount = 1;

						if(nowPieceObject.checkCollision(nowPieceX + move, nowPieceY, field) == false) {
							nowPieceX += move;

							if((getDASDelay() == 0) && (dasCount > 0) && (nowPieceObject.checkCollision(nowPieceX + move, nowPieceY, field) == false)) {
								if(!dasInstant) playSE("move");
								dasRepeat = true;
								dasInstant = true;
							}

							//log.debug("Successful movement: move="+move);

							if((ruleopt.lockresetMove == true) && (isMoveCountExceed() == false)) {
								lockDelayNow = 0;
								nowPieceObject.setDarkness(0f);
							}

							nowPieceMoveCount++;
							if((ending == 0) || (staffrollEnableStatistics)) statistics.totalPieceMove++;
							nowPieceBottomY = nowPieceObject.getBottom(nowPieceX, nowPieceY, field);

							if(onGroundBeforeMove) {
								extendedMoveCount++;
								lastmove = LASTMOVE_SLIDE_GROUND;
							} else {
								lastmove = LASTMOVE_SLIDE_AIR;
							}

							if(!dasInstant) playSE("move");
							
						} else if (ruleopt.dasChargeOnBlockedMove) {
							dasCount = getDAS();
							dasSpeedCount = getDASDelay();
						}
					} else {
						dasSpeedCount++;
					}
				}
			}

			// Hard drop
			if( (ctrl.isPress(Controller.BUTTON_UP) == true) &&
				(harddropContinuousUse == false) &&
				(ruleopt.harddropEnable == true) &&
				((ruleopt.moveDiagonal == true) || (sidemoveflag == false)) &&
				((ruleopt.moveUpAndDown == true) || (updown == false)) &&
				(nowPieceY < nowPieceBottomY) )
			{
				harddropFall += nowPieceBottomY - nowPieceY;

				if(nowPieceY != nowPieceBottomY) {
					nowPieceY = nowPieceBottomY;
					playSE("harddrop");
				}

				if(owner.mode != null) owner.mode.afterHardDropFall(this, playerID, harddropFall);
				owner.receiver.afterHardDropFall(this, playerID, harddropFall);

				lastmove = LASTMOVE_FALL_SELF;
				if(ruleopt.lockresetFall == true) {
					lockDelayNow = 0;
					nowPieceObject.setDarkness(0f);
					extendedMoveCount = 0;
					extendedRotateCount = 0;
				}
			}

			// Soft drop
			if( (ctrl.isPress(Controller.BUTTON_DOWN) == true) &&
				(softdropContinuousUse == false) &&
				(ruleopt.softdropEnable == true) &&
				((ruleopt.moveDiagonal == true) || (sidemoveflag == false)) &&
				((ruleopt.moveUpAndDown == true) || (updown == false)) )
			{
				if((ruleopt.softdropMultiplyNativeSpeed == true) || (speed.denominator <= 0))
					gcount += (int)(speed.gravity * ruleopt.softdropSpeed);
				else
					gcount += (int)(speed.denominator * ruleopt.softdropSpeed);

				softdropUsed = true;
			}

			if((ending == 0) || (staffrollEnableStatistics)) statistics.totalPieceActiveTime++;
		}

		// 落下
		gcount += speed.gravity;

		while((gcount >= speed.denominator) || (speed.gravity < 0)) {
			if(nowPieceObject.checkCollision(nowPieceX, nowPieceY + 1, field) == false) {
				if(speed.gravity >= 0) gcount -= speed.denominator;
				nowPieceY++;

				if(ruleopt.lockresetFall == true) {
					lockDelayNow = 0;
					nowPieceObject.setDarkness(0f);
				}

				if((lastmove != LASTMOVE_ROTATE_GROUND) && (lastmove != LASTMOVE_SLIDE_GROUND) && (lastmove != LASTMOVE_FALL_SELF)) {
					extendedMoveCount = 0;
					extendedRotateCount = 0;
				}

				if(softdropUsed == true) {
					lastmove = LASTMOVE_FALL_SELF;
					softdropFall++;
					softdropFallNow++;
					playSE("softdrop");
				} else {
					lastmove = LASTMOVE_FALL_AUTO;
				}
			} else {
				break;
			}
		}

		if(softdropFallNow > 0) {
			if(owner.mode != null) owner.mode.afterSoftDropFall(this, playerID, softdropFallNow);
			owner.receiver.afterSoftDropFall(this, playerID, softdropFallNow);
		}

		// 接地と固定
		if( (nowPieceObject.checkCollision(nowPieceX, nowPieceY + 1, field) == true) &&
			((statc[0] > 0) || (ruleopt.moveFirstFrame == true)) )
		{
			if((lockDelayNow == 0) && (getLockDelay() > 0))
				playSE("step");

			if(lockDelayNow < getLockDelay())
				lockDelayNow++;

			if((getLockDelay() >= 99) && (lockDelayNow > 98))
				lockDelayNow = 98;

			if(lockDelayNow < getLockDelay()) {
				if(lockDelayNow >= getLockDelay() - 1)
					nowPieceObject.setDarkness(0.5f);
				else
					nowPieceObject.setDarkness((lockDelayNow * 7 / getLockDelay()) * 0.05f);
			}

			if(getLockDelay() != 0)
				gcount = speed.gravity;

			// trueになると即固定
			boolean instantlock = false;

			// Hard drop固定
			if( (ctrl.isPress(Controller.BUTTON_UP) == true) &&
				(harddropContinuousUse == false) &&
				(ruleopt.harddropEnable == true) &&
				((ruleopt.moveDiagonal == true) || (sidemoveflag == false)) &&
				((ruleopt.moveUpAndDown == true) || (updown == false)) &&
				(ruleopt.harddropLock == true) )
			{
				harddropContinuousUse = true;
				manualLock = true;
				instantlock = true;
			}

			// Soft drop固定
			if( (ctrl.isPress(Controller.BUTTON_DOWN) == true) &&
				(softdropContinuousUse == false) &&
				(ruleopt.softdropEnable == true) &&
				((ruleopt.moveDiagonal == true) || (sidemoveflag == false)) &&
				((ruleopt.moveUpAndDown == true) || (updown == false)) &&
				(ruleopt.softdropLock == true) )
			{
				softdropContinuousUse = true;
				manualLock = true;
				instantlock = true;
			}

			// 接地状態でソフドドロップ固定
			if( (ctrl.isPush(Controller.BUTTON_DOWN) == true) &&
				(ruleopt.softdropEnable == true) &&
				((ruleopt.moveDiagonal == true) || (sidemoveflag == false)) &&
				((ruleopt.moveUpAndDown == true) || (updown == false)) &&
				(ruleopt.softdropSurfaceLock == true) )
			{
				softdropContinuousUse = true;
				manualLock = true;
				instantlock = true;
			}

			if((manualLock == true) && (ruleopt.shiftLockEnable)) {
				// bit 1 and 2 are button_up and button_down currently
				shiftLock = ctrl.getButtonBit() & 3;
			}

			// 移動＆回転count制限超過
			if( (ruleopt.lockresetLimitOver == RuleOptions.LOCKRESET_LIMIT_OVER_INSTANT) && (isMoveCountExceed() || isRotateCountExceed()) ) {
				instantlock = true;
			}

			// 接地即固定
			if( (getLockDelay() == 0) && ((gcount >= speed.denominator) || (speed.gravity < 0)) ) {
				instantlock = true;
			}

			// 固定
			if( ((lockDelayNow >= getLockDelay()) && (getLockDelay() > 0)) || (instantlock == true) ) {
				if(ruleopt.lockflash > 0) nowPieceObject.setDarkness(-0.8f);

				/*if((lastmove == LASTMOVE_ROTATE_GROUND) && (tspinEnable == true)) {

					tspinmini = false;

					// T-Spin Mini判定

					if(!useAllSpinBonus) {
						if(spinCheckType == SPINTYPE_4POINT) {
							if(tspinminiType == TSPINMINI_TYPE_ROTATECHECK) {
								if(nowPieceObject.checkCollision(nowPieceX, nowPieceY, getRotateDirection(-1), field) &&
								   nowPieceObject.checkCollision(nowPieceX, nowPieceY, getRotateDirection( 1), field))
									tspinmini = true;
							} else if(tspinminiType == TSPINMINI_TYPE_WALLKICKFLAG) {
								tspinmini = kickused;
							}
						} else if(spinCheckType == SPINTYPE_IMMOBILE) {
							Field copyField = new Field(field);
							nowPieceObject.placeToField(nowPieceX, nowPieceY, copyField);
							if((copyField.checkLineNoFlag() == 1) && (kickused == true)) tspinmini = true;
						}
					}
				}*/

				// T-Spin判定
				if((lastmove == LASTMOVE_ROTATE_GROUND) && (tspinEnable == true)) {
					if(useAllSpinBonus)
						setAllSpin(nowPieceX, nowPieceY, nowPieceObject, field);
					else
						setTSpin(nowPieceX, nowPieceY, nowPieceObject, field);
				}

				nowPieceObject.setAttribute(Block.BLOCK_ATTRIBUTE_SELFPLACED, true);

				boolean partialLockOut = nowPieceObject.isPartialLockOut(nowPieceX, nowPieceY, field);
				boolean put = nowPieceObject.placeToField(nowPieceX, nowPieceY, field);

				playSE("lock");

				holdDisable = false;

				if((ending == 0) || (staffrollEnableStatistics)) statistics.totalPieceLocked++;

				if (clearMode == CLEAR_LINE)
					lineClearing = field.checkLineNoFlag();
				else if (clearMode == CLEAR_COLOR)
					lineClearing = field.checkColor(colorClearSize, false, garbageColorClear, gemSameColor, ignoreHidden);
				else if (clearMode == CLEAR_LINE_COLOR)
					lineClearing = field.checkLineColor(colorClearSize, false, lineColorDiagonals, gemSameColor);
				else if (clearMode == CLEAR_GEM_COLOR)
					lineClearing = field.gemColorCheck(colorClearSize, false, garbageColorClear, ignoreHidden);
				chain = 0;
				lineGravityTotalLines = 0;

				if(lineClearing == 0) {
					combo = 0;

					if(tspin) {
						playSE("tspin0");

						if((ending == 0) || (staffrollEnableStatistics)) {
							if(tspinmini) statistics.totalTSpinZeroMini++;
							else statistics.totalTSpinZero++;
						}
					}

					if(owner.mode != null) owner.mode.calcScore(this, playerID, lineClearing);
					owner.receiver.calcScore(this, playerID, lineClearing);
				}

				if(owner.mode != null) owner.mode.pieceLocked(this, playerID, lineClearing);
				owner.receiver.pieceLocked(this, playerID, lineClearing);

				dasRepeat = false;
				dasInstant = false;

				// 次の処理を決める(Mode 側でステータスを弄っている場合は何もしない)
				if((stat == STAT_MOVE) || (versionMajor <= 6.3f)) {
					resetStatc();

					if((ending == 1) && (versionMajor >= 6.6f) && (versionMinorOld >= 0.1f)) {
						// Ending
						stat = STAT_ENDINGSTART;
					} else if( (!put && ruleopt.fieldLockoutDeath) || (partialLockOut && ruleopt.fieldPartialLockoutDeath) ) {
						// 画面外に置いて死亡
						stat = STAT_GAMEOVER;
						if((ending == 2) && (staffrollNoDeath)) stat = STAT_NOTHING;
					} else if ((lineGravityType == LINE_GRAVITY_CASCADE || lineGravityType == LINE_GRAVITY_CASCADE_SLOW)
							&& !connectBlocks) {
						stat = STAT_LINECLEAR;
						statc[0] = getLineDelay();
						statLineClear();
					} else if( (lineClearing > 0) && ((ruleopt.lockflash <= 0) || (!ruleopt.lockflashBeforeLineClear)) ) {
						// Line clear
						stat = STAT_LINECLEAR;
						statLineClear();
					} else if( ((getARE() > 0) || (lagARE) || (ruleopt.lockflashBeforeLineClear)) &&
							    (ruleopt.lockflash > 0) && (ruleopt.lockflashOnlyFrame) )
					{
						// AREあり（光あり）
						stat = STAT_LOCKFLASH;
					} else if((getARE() > 0) || (lagARE)) {
						// AREあり（光なし）
						statc[1] = getARE();
						stat = STAT_ARE;
					} else if(interruptItemNumber != INTERRUPTITEM_NONE) {
						// 中断効果のあるアイテム処理
						nowPieceObject = null;
						interruptItemPreviousStat = STAT_MOVE;
						stat = STAT_INTERRUPTITEM;
					} else {
						// AREなし
						stat = STAT_MOVE;
						if(ruleopt.moveFirstFrame == false) statMove();
					}
				}
				return;
			}
		}

		// 横溜め
		if((statc[0] > 0) || (ruleopt.dasInMoveFirstFrame)) {
			if( (moveDirection != 0) && (moveDirection == dasDirection) && ((dasCount < getDAS()) || (getDAS() <= 0)) ) {
				dasCount++;
			}
		}

		statc[0]++;
	}

	/**
	 * Block固定直後の光っているときの処理
	 */
	public void statLockFlash() {
		// イベント発生
		if(owner.mode != null) {
			if(owner.mode.onLockFlash(this, playerID) == true) return;
		}
		owner.receiver.onLockFlash(this, playerID);

		statc[0]++;

		checkDropContinuousUse();

		// 横溜め
		if(ruleopt.dasInLockFlash) padRepeat();
		else if(ruleopt.dasRedirectInDelay) { dasRedirect(); }

		// 次のステータス
		if(statc[0] >= ruleopt.lockflash) {
			resetStatc();

			if(lineClearing > 0) {
				// Line clear
				stat = STAT_LINECLEAR;
				statLineClear();
			} else {
				// ARE
				statc[1] = getARE();
				stat = STAT_ARE;
			}
			return;
		}
	}

	/**
	 * Line clear処理
	 */
	public void statLineClear() {
		// イベント発生
		if(owner.mode != null) {
			if(owner.mode.onLineClear(this, playerID) == true) return;
		}
		owner.receiver.onLineClear(this, playerID);

		checkDropContinuousUse();

		// 横溜め
		if(ruleopt.dasInLineClear) padRepeat();
		else if(ruleopt.dasRedirectInDelay) { dasRedirect(); }

		// 最初のフレーム
		if(statc[0] == 0) {
			// Line clear flagを設定
			if (clearMode == CLEAR_LINE)
				lineClearing = field.checkLine();
			// Set color clear flags
			else if (clearMode == CLEAR_COLOR)
				lineClearing = field.checkColor(colorClearSize, true, garbageColorClear, gemSameColor, ignoreHidden);
			// Set line color clear flags
			else if (clearMode == CLEAR_LINE_COLOR)
				lineClearing = field.checkLineColor(colorClearSize, true, lineColorDiagonals, gemSameColor);
			else if (clearMode == CLEAR_GEM_COLOR)
				lineClearing = field.gemColorCheck(colorClearSize, true, garbageColorClear, ignoreHidden);

			// Linescountを決める
			int li = lineClearing;
			if(big && bighalf)
				li >>= 1;
			//if(li > 4) li = 4;

			if(tspin) {
				playSE("tspin" + li);

				if((ending == 0) || (staffrollEnableStatistics)) {
					if((li == 1) && (tspinmini))  statistics.totalTSpinSingleMini++;
					if((li == 1) && (!tspinmini)) statistics.totalTSpinSingle++;
					if((li == 2) && (tspinmini))  statistics.totalTSpinDoubleMini++;
					if((li == 2) && (!tspinmini)) statistics.totalTSpinDouble++;
					if(li == 3) statistics.totalTSpinTriple++;
				}
			} else {
				if (clearMode == CLEAR_LINE)
					playSE("erase" + li);

				if((ending == 0) || (staffrollEnableStatistics)) {
					if(li == 1) statistics.totalSingle++;
					if(li == 2) statistics.totalDouble++;
					if(li == 3) statistics.totalTriple++;
					if(li == 4) statistics.totalFour++;
				}
			}

			// B2B bonus
			if(b2bEnable) {
				if((tspin) || (li >= 4)) {
					b2bcount++;

					if(b2bcount == 1) {
						playSE("b2b_start");
					} else {
						b2b = true;
						playSE("b2b_continue");

						if((ending == 0) || (staffrollEnableStatistics)) {
							if(li == 4) statistics.totalB2BFour++;
							else statistics.totalB2BTSpin++;
						}
					}
				} else if(b2bcount != 0) {
					b2b = false;
					b2bcount = 0;
					playSE("b2b_end");
				}
			}

			// Combo
			if((comboType != COMBO_TYPE_DISABLE) && (chain == 0)) {
				if( (comboType == COMBO_TYPE_NORMAL) || ((comboType == COMBO_TYPE_DOUBLE) && (li >= 2)) )
					combo++;

				if(combo >= 2) {
					int cmbse = combo - 1;
					if(cmbse > 20) cmbse = 20;
					playSE("combo" + cmbse);
				}

				if((ending == 0) || (staffrollEnableStatistics)) {
					if(combo > statistics.maxCombo) statistics.maxCombo = combo;
				}
			}

			lineGravityTotalLines += lineClearing;

			if((ending == 0) || (staffrollEnableStatistics)) statistics.lines += li;

			if(field.getHowManyGemClears() > 0) playSE("gem");

			// Calculate score
			if(owner.mode != null) owner.mode.calcScore(this, playerID, li);
			owner.receiver.calcScore(this, playerID, li);

			// Blockを消す演出を出す（まだ実際には消えていない）
			if (clearMode == CLEAR_LINE) {
				for(int i = 0; i < field.getHeight(); i++) {
					if(field.getLineFlag(i)) {
						for(int j = 0; j < field.getWidth(); j++) {
							Block blk = field.getBlock(j, i);

							if(blk != null) {
								if(owner.mode != null) owner.mode.blockBreak(this, playerID, j, i, blk);
								owner.receiver.blockBreak(this, playerID, j, i, blk);
							}
						}
					}
				}
			} else if (clearMode == CLEAR_LINE_COLOR || clearMode == CLEAR_COLOR || clearMode == CLEAR_GEM_COLOR)
				for(int i = 0; i < field.getHeight(); i++) {
					for(int j = 0; j < field.getWidth(); j++) {
						Block blk = field.getBlock(j, i);
						if (blk == null)
							continue;
						if(blk.getAttribute(Block.BLOCK_ATTRIBUTE_ERASE)) {
							if(owner.mode != null) owner.mode.blockBreak(this, playerID, j, i, blk);
							if (displaysize == 1)
							{
								owner.receiver.blockBreak(this, playerID, 2*j, 2*i, blk);
								owner.receiver.blockBreak(this, playerID, 2*j+1, 2*i, blk);
								owner.receiver.blockBreak(this, playerID, 2*j, 2*i+1, blk);
								owner.receiver.blockBreak(this, playerID, 2*j+1, 2*i+1, blk);
							}
							else
								owner.receiver.blockBreak(this, playerID, j, i, blk);
						}
					}
				}

			// Blockを消す
			if (clearMode == CLEAR_LINE)
				field.clearLine();
			else if (clearMode == CLEAR_COLOR)
				field.clearColor(colorClearSize, garbageColorClear, gemSameColor, ignoreHidden);
			else if (clearMode == CLEAR_LINE_COLOR)
				field.clearLineColor(colorClearSize, lineColorDiagonals, gemSameColor);
			else if (clearMode == CLEAR_GEM_COLOR)
				lineClearing = field.gemClearColor(colorClearSize, garbageColorClear, ignoreHidden);
		}

		// Linesを1段落とす
		if((lineGravityType == LINE_GRAVITY_NATIVE) &&
		   (getLineDelay() >= (lineClearing - 1)) && (statc[0] >= getLineDelay() - (lineClearing - 1)) && (ruleopt.lineFallAnim))
		{
			field.downFloatingBlocksSingleLine();
		}

		// Line delay cancel check
		delayCancelMoveLeft = ctrl.isPush(Controller.BUTTON_LEFT);
		delayCancelMoveRight = ctrl.isPush(Controller.BUTTON_RIGHT);

		boolean moveCancel = ruleopt.lineCancelMove && (ctrl.isPush(Controller.BUTTON_UP) ||
			ctrl.isPush(Controller.BUTTON_DOWN) || delayCancelMoveLeft || delayCancelMoveRight);
		boolean rotateCancel = ruleopt.lineCancelRotate && (ctrl.isPush(Controller.BUTTON_A) ||
			ctrl.isPush(Controller.BUTTON_B) || ctrl.isPush(Controller.BUTTON_C) ||
			ctrl.isPush(Controller.BUTTON_E));
		boolean holdCancel = ruleopt.lineCancelHold && ctrl.isPush(Controller.BUTTON_D);

		delayCancel = moveCancel || rotateCancel || holdCancel;

		if( (statc[0] < getLineDelay()) && delayCancel ) {
			statc[0] = getLineDelay();
		}

		// 次のステータス
		if(statc[0] >= getLineDelay()) {
			// Cascade
			if((lineGravityType == LINE_GRAVITY_CASCADE || lineGravityType == LINE_GRAVITY_CASCADE_SLOW)) {
				if (statc[6] < getCascadeDelay()) {
					statc[6]++;
					return;
				} else if(field.doCascadeGravity(lineGravityType)) {
					statc[6] = 0;
					return;
				} else if (statc[6] < getCascadeClearDelay()) {
					statc[6]++;
					return;
				} else if(((clearMode == CLEAR_LINE) && field.checkLineNoFlag() > 0) ||
						((clearMode == CLEAR_COLOR) && field.checkColor(colorClearSize, false, garbageColorClear, gemSameColor, ignoreHidden) > 0) ||
						((clearMode == CLEAR_LINE_COLOR) && field.checkLineColor(colorClearSize, false, lineColorDiagonals, gemSameColor) > 0) || 
						((clearMode == CLEAR_GEM_COLOR) && field.gemColorCheck(colorClearSize, false, garbageColorClear, ignoreHidden) > 0)) {
					tspin = false;
					tspinmini = false;
					chain++;
					if(chain > statistics.maxChain) statistics.maxChain = chain;
					statc[0] = 0;
					statc[6] = 0;
					return;
				}
			}

			boolean skip = false;
			if(owner.mode != null) skip = owner.mode.lineClearEnd(this, playerID);
			owner.receiver.lineClearEnd(this, playerID);

			if(!skip) {
				if(lineGravityType == LINE_GRAVITY_NATIVE) field.downFloatingBlocks();
				playSE("linefall");

				field.lineColorsCleared = null;

				if((stat == STAT_LINECLEAR) || (versionMajor <= 6.3f)) {
					resetStatc();
					if(ending == 1) {
						// Ending
						stat = STAT_ENDINGSTART;
					} else if((getARELine() > 0) || (lagARE)) {
						// AREあり
						statc[0] = 0;
						statc[1] = getARELine();
						statc[2] = 1;
						stat = STAT_ARE;
					} else if(interruptItemNumber != INTERRUPTITEM_NONE) {
						// 中断効果のあるアイテム処理
						nowPieceObject = null;
						interruptItemPreviousStat = STAT_MOVE;
						stat = STAT_INTERRUPTITEM;
					} else {
						// AREなし
						nowPieceObject = null;
						initialRotate();
						stat = STAT_MOVE;
					}
				}
			}

			return;
		}

		statc[0]++;
	}

	public int getCascadeDelay() {
		return cascadeDelay;
	}

	public int getCascadeClearDelay() {
		return cascadeClearDelay;
	}

	/**
	 * ARE中の処理
	 */
	public void statARE() {
		// イベント発生
		if(owner.mode != null) {
			if(owner.mode.onARE(this, playerID) == true) return;
		}
		owner.receiver.onARE(this, playerID);

		statc[0]++;

		checkDropContinuousUse();

		// ARE cancel check
		delayCancelMoveLeft = ctrl.isPush(Controller.BUTTON_LEFT);
		delayCancelMoveRight = ctrl.isPush(Controller.BUTTON_RIGHT);

		boolean moveCancel = ruleopt.areCancelMove && (ctrl.isPush(Controller.BUTTON_UP) ||
			ctrl.isPush(Controller.BUTTON_DOWN) || delayCancelMoveLeft || delayCancelMoveRight);
		boolean rotateCancel = ruleopt.areCancelRotate && (ctrl.isPush(Controller.BUTTON_A) ||
			ctrl.isPush(Controller.BUTTON_B) || ctrl.isPush(Controller.BUTTON_C) ||
			ctrl.isPush(Controller.BUTTON_E));
		boolean holdCancel = ruleopt.areCancelHold && ctrl.isPush(Controller.BUTTON_D);

		delayCancel = moveCancel || rotateCancel || holdCancel;

		if( (statc[0] < statc[1]) && delayCancel ) {
			statc[0] = statc[1];
		}

		// 横溜め
		if( (ruleopt.dasInARE) && ((statc[0] < statc[1] - 1) || (ruleopt.dasInARELastFrame)) )
			padRepeat();
		else if(ruleopt.dasRedirectInDelay) { dasRedirect(); }

		// 次のステータス
		if((statc[0] >= statc[1]) && (!lagARE)) {
			nowPieceObject = null;
			resetStatc();

			if(interruptItemNumber != INTERRUPTITEM_NONE) {
				// 中断効果のあるアイテム処理
				interruptItemPreviousStat = STAT_MOVE;
				stat = STAT_INTERRUPTITEM;
			} else {
				// Blockピース移動処理
				initialRotate();
				stat = STAT_MOVE;
			}
		}
	}

	/**
	 * Ending突入処理
	 */
	public void statEndingStart() {
		// イベント発生
		if(owner.mode != null) {
			if(owner.mode.onEndingStart(this, playerID) == true) return;
		}
		owner.receiver.onEndingStart(this, playerID);

		checkDropContinuousUse();

		// 横溜め
		if(ruleopt.dasInEndingStart) padRepeat();
		else if(ruleopt.dasRedirectInDelay) { dasRedirect(); }

		if(statc[2] == 0) {
			timerActive = false;
			owner.bgmStatus.bgm = BGMStatus.BGM_NOTHING;
			playSE("endingstart");
			statc[2] = 1;
		}

		if(statc[0] < getLineDelay()) {
			statc[0]++;
		} else if(statc[1] < field.getHeight() * 6) {
			if(statc[1] % 6 == 0) {
				int y = field.getHeight() - (statc[1] / 6);
				field.setLineFlag(y, true);

				for(int i = 0; i < field.getWidth(); i++) {
					Block blk = field.getBlock(i, y);

					if((blk != null) && (blk.color != Block.BLOCK_COLOR_NONE)) {
						if(owner.mode != null) owner.mode.blockBreak(this, playerID, i, y, blk);
						owner.receiver.blockBreak(this, playerID, i, y, blk);
						field.setBlockColor(i, y, Block.BLOCK_COLOR_NONE);
					}
				}
			}

			statc[1]++;
		} else if(statc[0] < getLineDelay() + 2) {
			statc[0]++;
		} else {
			ending = 2;
			field.reset();
			resetStatc();

			if(staffrollEnable) {
				nowPieceObject = null;
				stat = STAT_MOVE;
			} else {
				stat = STAT_EXCELLENT;
			}
		}
	}

	/**
	 * 各ゲームMode が自由に使えるステータスの処理
	 */
	public void statCustom() {
		// イベント発生
		if(owner.mode != null) {
			if(owner.mode.onCustom(this, playerID) == true) return;
		}
		owner.receiver.onCustom(this, playerID);
	}

	/**
	 * Ending画面
	 */
	public void statExcellent() {
		// イベント発生
		if(owner.mode != null) {
			if(owner.mode.onExcellent(this, playerID) == true) return;
		}
		owner.receiver.onExcellent(this, playerID);

		if(statc[0] == 0) {
			gameActive = false;
			timerActive = false;
			owner.bgmStatus.fadesw = true;
			if(ai != null) ai.shutdown(this, playerID);

			resetFieldVisible();

			playSE("excellent");
		}

		if((statc[0] >= 120) && (ctrl.isPush(Controller.BUTTON_A))) {
			statc[0] = 600;
		}

		if((statc[0] >= 600) && (statc[1] == 0)) {
			resetStatc();
			stat = STAT_GAMEOVER;
		} else {
			statc[0]++;
		}
	}

	/**
	 * ゲームオーバーの処理
	 */
	public void statGameOver() {
		// イベント発生
		if(owner.mode != null) {
			if(owner.mode.onGameOver(this, playerID) == true) return;
		}
		owner.receiver.onGameOver(this, playerID);

		if(lives <= 0) {
			// もう復活できないとき
			if(statc[0] == 0) {
				endTime = System.currentTimeMillis();
				statistics.gamerate = (float)(replayTimer / (.06*(endTime - startTime)));

				gameActive = false;
				timerActive = false;
				blockShowOutlineOnly = false;
				if(owner.getPlayers() < 2) owner.bgmStatus.bgm = BGMStatus.BGM_NOTHING;
				if(ai != null) ai.shutdown(this, playerID);

				if(field.isEmpty()) {
					statc[0] = field.getHeight() + 1;
				} else {
					resetFieldVisible();
				}
			}

			if(statc[0] < field.getHeight() + 1) {
				for(int i = 0; i < field.getWidth(); i++) {
					if(field.getBlockColor(i, field.getHeight() - statc[0]) != Block.BLOCK_COLOR_NONE) {
						Block blk = field.getBlock(i, field.getHeight() - statc[0]);

						if(blk != null) {
							if(!blk.getAttribute(Block.BLOCK_ATTRIBUTE_GARBAGE)) {
								blk.color = Block.BLOCK_COLOR_GRAY;
								blk.setAttribute(Block.BLOCK_ATTRIBUTE_GARBAGE, true);
							}
							blk.darkness = 0.3f;
							blk.elapsedFrames = -1;
						}
					}
				}
				statc[0]++;
			} else if(statc[0] == field.getHeight() + 1) {
				playSE("gameover");
				statc[0]++;
			} else if(statc[0] < field.getHeight() + 1 + 180) {
				if((statc[0] >= field.getHeight() + 1 + 60) && (ctrl.isPush(Controller.BUTTON_A))) {
					statc[0] = field.getHeight() + 1 + 180;
				}

				statc[0]++;
			} else {
				if(!owner.replayMode || owner.replayRerecord) owner.saveReplay();

				for(int i = 0; i < owner.getPlayers(); i++) {
					if((i == playerID) || (gameoverAll)) {
						if(owner.engine[i].field != null) {
							owner.engine[i].field.reset();
						}
						owner.engine[i].resetStatc();
						owner.engine[i].stat = STAT_RESULT;
					}
				}
			}
		} else {
			// 復活できるとき
			if(statc[0] == 0) {
				blockShowOutlineOnly = false;
				playSE("died");

				resetFieldVisible();

				for(int i = (field.getHiddenHeight() * -1); i < field.getHeight(); i++) {
					for(int j = 0; j < field.getWidth(); j++) {
						if(field.getBlockColor(j, i) != Block.BLOCK_COLOR_NONE) {
							field.setBlockColor(j, i, Block.BLOCK_COLOR_GRAY);
						}
					}
				}

				statc[0] = 1;
			}

			if(!field.isEmpty()) {
				field.pushDown();
			} else if(statc[1] < getARE()) {
				statc[1]++;
			} else {
				lives--;
				resetStatc();
				stat = STAT_MOVE;
			}
		}
	}

	/**
	 * 結果画面
	 */
	public void statResult() {
		// イベント発生
		if(owner.mode != null) {
			if(owner.mode.onResult(this, playerID) == true) return;
		}
		owner.receiver.onResult(this, playerID);

		// カーソル移動
		if(ctrl.isMenuRepeatKey(Controller.BUTTON_LEFT) || ctrl.isMenuRepeatKey(Controller.BUTTON_RIGHT)) {
			if(statc[0] == 0) statc[0] = 1;
			else statc[0] = 0;
			playSE("cursor");
		}

		// 決定
		if(ctrl.isPush(Controller.BUTTON_A)) {
			playSE("decide");

			if(statc[0] == 0) {
				owner.reset();
			} else {
				quitflag = true;
			}
		}
	}

	/**
	 * フィールドエディット画面
	 */
	public void statFieldEdit() {
		// イベント発生
		if(owner.mode != null) {
			if(owner.mode.onFieldEdit(this, playerID) == true) return;
		}
		owner.receiver.onFieldEdit(this, playerID);

		fldeditFrames++;

		// カーソル移動
		if(ctrl.isMenuRepeatKey(Controller.BUTTON_LEFT, false) && !ctrl.isPress(Controller.BUTTON_C)) {
			playSE("move");
			fldeditX--;
			if(fldeditX < 0) fldeditX = fieldWidth - 1;
		}
		if(ctrl.isMenuRepeatKey(Controller.BUTTON_RIGHT, false) && !ctrl.isPress(Controller.BUTTON_C)) {
			playSE("move");
			fldeditX++;
			if(fldeditX > fieldWidth - 1) fldeditX = 0;
		}
		if(ctrl.isMenuRepeatKey(Controller.BUTTON_UP, false)) {
			playSE("move");
			fldeditY--;
			if(fldeditY < 0) fldeditY = fieldHeight - 1;
		}
		if(ctrl.isMenuRepeatKey(Controller.BUTTON_DOWN, false)) {
			playSE("move");
			fldeditY++;
			if(fldeditY > fieldHeight - 1) fldeditY = 0;
		}

		// 色選択
		if(ctrl.isMenuRepeatKey(Controller.BUTTON_LEFT, false) && ctrl.isPress(Controller.BUTTON_C)) {
			playSE("cursor");
			fldeditColor--;
			if(fldeditColor < Block.BLOCK_COLOR_GRAY) fldeditColor = Block.BLOCK_COLOR_GEM_PURPLE;
		}
		if(ctrl.isMenuRepeatKey(Controller.BUTTON_RIGHT, false) && ctrl.isPress(Controller.BUTTON_C)) {
			playSE("cursor");
			fldeditColor++;
			if(fldeditColor > Block.BLOCK_COLOR_GEM_PURPLE) fldeditColor = Block.BLOCK_COLOR_GRAY;
		}

		// 配置
		if(ctrl.isPress(Controller.BUTTON_A) && (fldeditFrames > 10)) {
			try {
				if(field.getBlockColorE(fldeditX, fldeditY) != fldeditColor) {
					Block blk = new Block(fldeditColor, getSkin(), Block.BLOCK_ATTRIBUTE_VISIBLE | Block.BLOCK_ATTRIBUTE_OUTLINE);
					field.setBlockE(fldeditX, fldeditY, blk);
					playSE("change");
				}
			} catch (Exception e) {}
		}

		// 消去
		if(ctrl.isPress(Controller.BUTTON_D) && (fldeditFrames > 10)) {
			try {
				if(!field.getBlockEmptyE(fldeditX, fldeditY)) {
					field.setBlockColorE(fldeditX, fldeditY, Block.BLOCK_COLOR_NONE);
					playSE("change");
				}
			} catch (Exception e) {}
		}

		// 終了
		if(ctrl.isPush(Controller.BUTTON_B) && (fldeditFrames > 10)) {
			stat = fldeditPreviousStat;
			if(owner.mode != null) owner.mode.fieldEditExit(this, playerID);
			owner.receiver.fieldEditExit(this, playerID);
		}
	}

	/**
	 * プレイ中断効果のあるアイテム処理
	 */
	public void statInterruptItem() {
		boolean contFlag = false;	// 続行 flag

		switch(interruptItemNumber) {
		case INTERRUPTITEM_MIRROR:	// ミラー
			contFlag = interruptItemMirrorProc();
			break;
		}

		if(!contFlag) {
			interruptItemNumber = INTERRUPTITEM_NONE;
			resetStatc();
			stat = interruptItemPreviousStat;
		}
	}

	/**
	 * ミラー処理
	 * @return trueならミラー処理続行
	 */
	public boolean interruptItemMirrorProc() {
		if(statc[0] == 0) {
			// フィールドをバックアップにコピー
			interruptItemMirrorField = new Field(field);
			// フィールドのBlockを全部消す
			field.reset();
		} else if((statc[0] >= 21) && (statc[0] < 21 + (field.getWidth() * 2)) && (statc[0] % 2 == 0)) {
			// 反転
			int x = ((statc[0] - 20) / 2) - 1;

			for(int y = (field.getHiddenHeight() * -1); y < field.getHeight(); y++) {
				field.setBlock(field.getWidth() - x - 1, y, interruptItemMirrorField.getBlock(x, y));
			}
		} else if(statc[0] < 21 + (field.getWidth() * 2) + 5) {
			// 待ち time
		} else {
			// 終了
			statc[0] = 0;
			interruptItemMirrorField = null;
			return false;
		}

		statc[0]++;
		return true;
	}
}
