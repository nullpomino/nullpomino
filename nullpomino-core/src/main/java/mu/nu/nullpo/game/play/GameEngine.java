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
import mu.nu.nullpo.game.subsystem.ai.DummyAI;
import mu.nu.nullpo.game.subsystem.wallkick.Wallkick;
import mu.nu.nullpo.util.GeneralUtil;
import net.omegaboshi.nullpomino.game.subsystem.randomizer.MemorylessRandomizer;
import net.omegaboshi.nullpomino.game.subsystem.randomizer.Randomizer;

/**
 * Each player's Game processing
 */
public class GameEngine {
	/** Log (Apache log4j) */
	static Logger log = Logger.getLogger(GameEngine.class);

	/** Constants of game style (Currently not directly used by GameEngine, but from game modes) */
	public static final int GAMESTYLE_TETROMINO = 0,
							GAMESTYLE_AVALANCHE = 1,
							GAMESTYLE_PHYSICIAN = 2,
							GAMESTYLE_SPF = 3;

	/** Max number of game style */
	public static final int MAX_GAMESTYLE = 4;

	/** Game style names */
	public static final String[] GAMESTYLE_NAMES = {"TETROMINO", "AVALANCHE", "PHYSICIAN", "SPF"};

	/** Constants of main game status */
	public static enum Status {
		NOTHING, SETTING, READY, MOVE, LOCKFLASH, LINECLEAR, ARE, ENDINGSTART, CUSTOM, EXCELLENT, GAMEOVER, RESULT, FIELDEDIT, INTERRUPTITEM
	};

	/** Number of free status counters (used by statc array) */
	public static final int MAX_STATC = 10;

	/** Constants of last successful movements */
	public enum LastMove {
		NONE, FALL_AUTO, FALL_SELF, SLIDE_AIR, SLIDE_GROUND, ROTATE_AIR, ROTATE_GROUND
	}

	/** Constants of block outline type */
	public static final int BLOCK_OUTLINE_AUTO = -1,
							BLOCK_OUTLINE_NONE = 0, BLOCK_OUTLINE_NORMAL = 1, BLOCK_OUTLINE_CONNECT = 2, BLOCK_OUTLINE_SAMECOLOR = 3;

	/** Default duration of Ready->Go */
	public static final int READY_START = 0, READY_END = 49, GO_START = 50, GO_END = 100;

	/** Constants of frame colors */
	public static final int FRAME_COLOR_BLUE = 0, FRAME_COLOR_GREEN = 1, FRAME_COLOR_RED = 2, FRAME_COLOR_GRAY = 3, FRAME_COLOR_YELLOW = 4,
							FRAME_COLOR_CYAN = 5, FRAME_COLOR_PINK = 6, FRAME_COLOR_PURPLE = 7;

	/** Constants of meter colors */
	public static final int METER_COLOR_RED = 0,
							METER_COLOR_ORANGE = 1,
							METER_COLOR_YELLOW = 2,
							METER_COLOR_GREEN = 3,
							METER_COLOR_DARKGREEN = 4,
							METER_COLOR_CYAN = 5,
							METER_COLOR_BLUE = 6,
							METER_COLOR_DARKBLUE = 7,
							METER_COLOR_PURPLE = 8,
							METER_COLOR_PINK = 9;

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
	public enum LineGravity {
		NATIVE, CASCADE, CASCADE_SLOW
	}

	/** Clear mode settings  */
	public enum ClearType {
		LINE, COLOR, LINE_COLOR, GEM_COLOR
	}

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

	/** Prethink with AI */
	public boolean aiPrethink;

	/** Show internal state of AI */
	public boolean aiShowState;

	/** AI Hint piece (copy of current or hold) */
	public Piece aiHintPiece;

	/** True if AI Hint is ready */
	public boolean aiHintReady;

	/** Current main game status */
	public Status stat;

	/** Free status counters */
	public int[] statc;

	/** true if game play, false if menu. Used for alternate keyboard mappings. */
	public boolean isInGame;

	/** true if the game is active */
	public boolean gameActive;

	/** true if the timer is active */
	public boolean timerActive;

	/** true if the game is started (It will not change back to false until the game is reset) */
	public boolean gameStarted;

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

	/** Dev build flag */
	public boolean versionIsDevBuild;

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
	public LineGravity lineGravityType;

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

	/** Number of current piece failed rotations */
	public int nowPieceRotateFailCount;

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
	public LastMove lastmove;

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

	/** Amount of meter (layer 2) */
	public int meterValueSub;

	/** Color of meter (layer 2) */
	public int meterColorSub;

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
	public Status fldeditPreviousStat;

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
	public Status interruptItemPreviousStat;

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

	/** Reverse roles of up/down keys in-game */
	public boolean owReverseUpDown;

	/** Diagonal move (-1=Auto 0=Disable 1=Enable) */
	public int owMoveDiagonal;

	/** Outline type (-1:Auto 0orAbove:Fixed) */
	public int owBlockOutlineType;

	/** Show outline only flag (-1:Auto 0:Always Normal 1:Always Outline Only) */
	public int owBlockShowOutlineOnly;

	/** Clear mode selection */
	public ClearType clearMode;

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

	/** If true, the game will execute double rotation to I2 piece when regular rotation fails twice */
	public boolean dominoQuickTurn;

	/** 0 = default, 1 = link by color, 2 = link by color but ignore links for cascade (Avalanche) */
	public int sticky;

	/**
	 * Constructor
	 * @param owner Own the game engineGameOwnerClass
	 * @param playerID PlayerOf number
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
		owReverseUpDown = false;
		owMoveDiagonal = -1;
		owBlockOutlineType = -1;
		owBlockShowOutlineOnly = -1;
	}

	/**
	 * With parameters such as the rule setConstructor
	 * @param owner Own the game engineGameOwnerClass
	 * @param playerID PlayerOf number
	 * @param ruleopt Rule Set
	 * @param wallkick WallkickSystem
	 * @param randomizer BlockGeneration algorithm of the order of appearance of the piece
	 */
	public GameEngine(GameManager owner, int playerID, RuleOptions ruleopt, Wallkick wallkick, Randomizer randomizer) {
		this(owner,playerID);
		this.ruleopt = ruleopt;
		this.wallkick = wallkick;
		this.randomizer = randomizer;
	}

	/**
	 * READYPreviousInitialization
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
			versionIsDevBuild = GameManager.isDevBuild();

			Random tempRand = new Random();
			randSeed = tempRand.nextLong();
			log.debug("Player + " + playerID + "Random seed :" + Long.toString(randSeed, 16));
			random = new Random(randSeed);
		} else {
			versionMajor = owner.replayProp.getProperty("version.core.major", 0f);
			versionMinor = owner.replayProp.getProperty("version.core.minor", 0);
			versionMinorOld = owner.replayProp.getProperty("version.core.minor", 0f);
			versionIsDevBuild = owner.replayProp.getProperty("version.core.dev", false);

			replayData.readProperty(owner.replayProp, playerID);

			String tempRand = owner.replayProp.getProperty(playerID + ".replay.randSeed", "0");
			randSeed = Long.parseLong(tempRand, 16);
			random = new Random(randSeed);

			owRotateButtonDefaultRight = owner.replayProp.getProperty(playerID + ".tuning.owRotateButtonDefaultRight", -1);
			owSkin = owner.replayProp.getProperty(playerID + ".tuning.owSkin", -1);
			owMinDAS = owner.replayProp.getProperty(playerID + ".tuning.owMinDAS", -1);
			owMaxDAS = owner.replayProp.getProperty(playerID + ".tuning.owMaxDAS", -1);
			owDasDelay = owner.replayProp.getProperty(playerID + ".tuning.owDasDelay", -1);
			owReverseUpDown = owner.replayProp.getProperty(playerID + ".tuning.owReverseUpDown", false);
			owMoveDiagonal = owner.replayProp.getProperty(playerID + ".tuning.owMoveDiagonal", -1);
			owBlockOutlineType = owner.replayProp.getProperty(playerID + ".tuning.owBlockOutlineType", -1);
			owBlockShowOutlineOnly = owner.replayProp.getProperty(playerID + ".tuning.owBlockShowOutlineOnly", -1);

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

		stat = Status.SETTING;
		statc = new int[MAX_STATC];

		isInGame = false;
		gameActive = false;
		timerActive = false;
		gameStarted = false;
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
		lineGravityType = LineGravity.NATIVE;
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
		nowPieceRotateFailCount = 0;

		extendedMoveCount = 0;
		extendedRotateCount = 0;

		nowWallkickCount = 0;
		nowUpwardWallkickCount = 0;

		softdropFall = 0;
		harddropFall = 0;
		softdropContinuousUse = false;
		harddropContinuousUse = false;

		manualLock = false;

		lastmove = LastMove.NONE;

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
		meterValueSub = 0;
		meterColorSub = METER_COLOR_RED;

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

		clearMode = ClearType.LINE;
		colorClearSize = -1;
		garbageColorClear = false;
		ignoreHidden = false;
		connectBlocks = true;
		lineColorDiagonals = false;
		blockColors = BLOCK_COLORS_DEFAULT;
		cascadeDelay = 0;
		cascadeClearDelay = 0;

		rainbowAnimate = false;
		dominoQuickTurn = false;
		sticky = 0;

		startTime = 0;
		endTime = 0;

		//  event 発生
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
	 * End processing
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
	 * Status counterInitialization
	 */
	public void resetStatc() {
		for(int i = 0; i < statc.length; i++) statc[i] = 0;
	}

	/**
	 * Sound effectsPlay (enableSEThetrueOnly when)
	 * @param name Sound effectsOfName
	 */
	public void playSE(String name) {
		if(enableSE) owner.receiver.playSE(name);
	}

	/**
	 * NEXTOf PeaceIDGet the
	 * @param c Want to getNEXTThe position of the
	 * @return NEXTOf PeaceID
	 */
	public int getNextID(int c) {
		if(nextPieceArrayID == null) return Piece.PIECE_NONE;
		int c2 = c;
		while(c2 >= nextPieceArrayID.length) c2 = c2 - nextPieceArrayID.length;
		return nextPieceArrayID[c2];
	}

	/**
	 * NEXTGets an object of Peace
	 * @param c Want to getNEXTThe position of the
	 * @return NEXTObject of Peace
	 */
	public Piece getNextObject(int c) {
		if(nextPieceArrayObject == null) return null;
		int c2 = c;
		while(c2 >= nextPieceArrayObject.length) c2 = c2 - nextPieceArrayObject.length;
		return nextPieceArrayObject[c2];
	}

	/**
	 * NEXTObtain a copy of the object of the piece
	 * @param c Want to getNEXTThe position of the
	 * @return NEXTCopy of the object of the piece
	 */
	public Piece getNextObjectCopy(int c) {
		Piece p = getNextObject(c);
		Piece r = null;
		if(p != null) r = new Piece(p);
		return r;
	}

	/**
	 * Current AREGets the value of the (Also consider setting rules)
	 * @return Current ARE
	 */
	public int getARE() {
		if((speed.are < ruleopt.minARE) && (ruleopt.minARE >= 0)) return ruleopt.minARE;
		if((speed.are > ruleopt.maxARE) && (ruleopt.maxARE >= 0)) return ruleopt.maxARE;
		return speed.are;
	}

	/**
	 * Current ARE after line clearGets the value of the (Also consider setting rules)
	 * @return Current ARE after line clear
	 */
	public int getARELine() {
		if((speed.areLine < ruleopt.minARELine) && (ruleopt.minARELine >= 0)) return ruleopt.minARELine;
		if((speed.areLine > ruleopt.maxARELine) && (ruleopt.maxARELine >= 0)) return ruleopt.maxARELine;
		return speed.areLine;
	}

	/**
	 * Current Line clear timeGets the value of the (Also consider setting rules)
	 * @return Current Line clear time
	 */
	public int getLineDelay() {
		if((speed.lineDelay < ruleopt.minLineDelay) && (ruleopt.minLineDelay >= 0)) return ruleopt.minLineDelay;
		if((speed.lineDelay > ruleopt.maxLineDelay) && (ruleopt.maxLineDelay >= 0)) return ruleopt.maxLineDelay;
		return speed.lineDelay;
	}

	/**
	 * Current Fixation timeGets the value of the (Also consider setting rules)
	 * @return Current Fixation time
	 */
	public int getLockDelay() {
		if((speed.lockDelay < ruleopt.minLockDelay) && (ruleopt.minLockDelay >= 0)) return ruleopt.minLockDelay;
		if((speed.lockDelay > ruleopt.maxLockDelay) && (ruleopt.maxLockDelay >= 0)) return ruleopt.maxLockDelay;
		return speed.lockDelay;
	}

	/**
	 * Current DASGets the value of the (Also consider setting rules)
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
	 * @return Controller.BUTTON_UP if controls are normal, Controller.BUTTON_DOWN if up/down are reversed
	 */
	public int getUp() {
		return owReverseUpDown ? Controller.BUTTON_DOWN : Controller.BUTTON_UP;
	}

	/**
	 * @return Controller.BUTTON_DOWN if controls are normal, Controller.BUTTON_UP if up/down are reversed
	 */
	public int getDown(){
		return owReverseUpDown ? Controller.BUTTON_UP : Controller.BUTTON_DOWN;
	}

	/**
	 * Current Gets the horizontal movement speed
	 * @return Lateral movement speed
	 */
	public int getDASDelay() {
		if((ruleopt == null) || (owDasDelay >= 0)) {
			return owDasDelay;
		}
		return ruleopt.dasDelay;
	}

	/**
	 * In current useBlockSkin numberGet the
	 * @return BlockSkin number
	 */
	public int getSkin() {
		if((ruleopt == null) || (owSkin >= 0)) {
			return owSkin;
		}
		return ruleopt.skin;
	}

	/**
	 * @return A buttonI left when pressedrotationIf thefalse, RightrotationIf thetrue
	 */
	public boolean isRotateButtonDefaultRight() {
		if((ruleopt == null) || (owRotateButtonDefaultRight >= 0)) {
			if(owRotateButtonDefaultRight == 0) return false;
			else return true;
		}
		return ruleopt.rotateButtonDefaultRight;
	}

	/**
	 * Is diagonal movement enabled?
	 * @return true if diagonal movement is enabled
	 */
	public boolean isDiagonalMoveEnabled() {
		if((ruleopt == null) || (owMoveDiagonal >= 0)) {
			return (owMoveDiagonal == 1);
		}
		return ruleopt.moveDiagonal;
	}

	/**
	 * Visible / disappearRoll Of the statefieldReturned to the normal state
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
	 * SoftHard drop· Hold preceding precedingrotationRestrictions on the use of release
	 */
	public void checkDropContinuousUse() {
		if(gameActive) {
			if((!ctrl.isPress(getDown())) || (!ruleopt.softdropLimit))
				softdropContinuousUse = false;
			if((!ctrl.isPress(getUp())) || (!ruleopt.harddropLimit))
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
	 * Lateral motion input OfDirectionGet the
	 * @return -1:Left 0:No 1:Right
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
	 * Processing horizontal reservoir
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
	 * Move countDetermines whether or not to exceed the limit
	 * @return Move countI have exceeded the limittrue
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
	 * rotation countDetermines whether or not to exceed the limit
	 * @return rotation countI have exceeded the limittrue
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
	 * SpinJudgment(When all rules for spin)
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param piece Current BlockPeace
	 * @param fld field
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
			//int y2 = y - 1;
			//log.debug(x + "," + y2 + ":" + piece.checkCollision(x, y2, fld));

			if( piece.checkCollision(x, y - 1, fld) &&
					piece.checkCollision(x + 1, y, fld) &&
					piece.checkCollision(x - 1, y, fld) ) {
				tspin = true;
				Field copyField = new Field(fld);
				piece.placeToField(x, y, copyField);
				if((piece.getHeight() + 1 != copyField.checkLineNoFlag()) && (kickused == true)) tspinmini = true;
				//if((copyField.checkLineNoFlag() == 1) && (kickused == true)) tspinmini = true;
			} else if((tspinEnableEZ) && (kickused == true)) {
				tspin = true;
				tspinez = true;
			}
		}
	}

	/**
	 * Determines whether the hold
	 * @return If you can holdtrue
	 */
	public boolean isHoldOK() {
		if( (!ruleopt.holdEnable) || (holdDisable) || ((holdUsedCount >= ruleopt.holdLimit) && (ruleopt.holdLimit >= 0)) || (initialHoldContinuousUse) )
			return false;

		return true;
	}

	/**
	 * Peace appearsX-coordinateGet the
	 * @param fld field
	 * @param piece Piece
	 * @return Appearance position ofX-coordinate
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
	 * Peace appearsY-coordinateGet the
	 * @param piece Piece
	 * @return Appearance position ofY-coordinate
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
	 * rotation buttonPiece after pressing theDirectionGet the
	 * @param move rotationDirection (-1:Left 1:Right 2:180Degrees)
	 * @return rotation buttonPiece after pressing theDirection
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
	 * PrecedingrotationHold processing and precedence
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
	 * fieldOfBlock stateUpdate
	 */
	public void fieldUpdate() {
		boolean outlineOnly = blockShowOutlineOnly;	// Show outline only flag
		if(owBlockShowOutlineOnly == 0) outlineOnly = false;
		if(owBlockShowOutlineOnly == 1) outlineOnly = true;

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
							if(outlineOnly) {
								blk.setAttribute(Block.BLOCK_ATTRIBUTE_OUTLINE, true);
								blk.setAttribute(Block.BLOCK_ATTRIBUTE_VISIBLE, false);
								blk.setAttribute(Block.BLOCK_ATTRIBUTE_BONE, false);
							}
						} else {
							blk.darkness = 0f;
							blk.setAttribute(Block.BLOCK_ATTRIBUTE_OUTLINE, true);
							if(outlineOnly) {
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

		// BunglerHIDDEN
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
		owner.replayProp.setProperty("version.core.dev", versionIsDevBuild);

		owner.replayProp.setProperty(playerID + ".replay.randSeed", Long.toString(randSeed, 16));

		replayData.writeProperty(owner.replayProp, playerID, replayTimer);
		statistics.writeProperty(owner.replayProp, playerID);
		ruleopt.writeProperty(owner.replayProp, playerID);

		if(playerID == 0) {
			if(owner.mode != null) owner.replayProp.setProperty("name.mode", owner.mode.getName());
			if(ruleopt.strRuleName != null) owner.replayProp.setProperty("name.rule", ruleopt.strRuleName);

			// Local timestamp
			Calendar currentTime = Calendar.getInstance();
			int month = currentTime.get(Calendar.MONTH) + 1;
			String strDate = String.format("%04d/%02d/%02d", currentTime.get(Calendar.YEAR), month, currentTime.get(Calendar.DATE));
			String strTime = String.format("%02d:%02d:%02d",
											currentTime.get(Calendar.HOUR_OF_DAY), currentTime.get(Calendar.MINUTE), currentTime.get(Calendar.SECOND));
			owner.replayProp.setProperty("timestamp.date", strDate);
			owner.replayProp.setProperty("timestamp.time", strTime);

			// GMT timestamp
			owner.replayProp.setProperty("timestamp.gmt", GeneralUtil.exportCalendarString());
		}

		owner.replayProp.setProperty(playerID + ".tuning.owRotateButtonDefaultRight", owRotateButtonDefaultRight);
		owner.replayProp.setProperty(playerID + ".tuning.owSkin", owSkin);
		owner.replayProp.setProperty(playerID + ".tuning.owMinDAS", owMinDAS);
		owner.replayProp.setProperty(playerID + ".tuning.owMaxDAS", owMaxDAS);
		owner.replayProp.setProperty(playerID + ".tuning.owDasDelay", owDasDelay);
		owner.replayProp.setProperty(playerID + ".tuning.owReverseUpDown", owReverseUpDown);
		owner.replayProp.setProperty(playerID + ".tuning.owMoveDiagonal", owMoveDiagonal);

		if(owner.mode != null) owner.mode.saveReplay(this, playerID, owner.replayProp);
	}

	/**
	 * fieldProcessing to enter the edit screen
	 */
	public void enterFieldEdit() {
		fldeditPreviousStat = stat;
		stat = Status.FIELDEDIT;
		fldeditX = 0;
		fldeditY = 0;
		fldeditColor = Block.BLOCK_COLOR_GRAY;
		fldeditFrames = 0;
		owner.menuOnly = false;
		createFieldIfNeeded();
	}

	/**
	 * fieldAInitialization (If I do not exist yet)
	 */
	public void createFieldIfNeeded() {
		if(fieldWidth < 0) fieldWidth = ruleopt.fieldWidth;
		if(fieldHeight < 0) fieldHeight = ruleopt.fieldHeight;
		if(fieldHiddenHeight < 0) fieldHiddenHeight = ruleopt.fieldHiddenHeight;
		if(field == null) field = new Field(fieldWidth, fieldHeight, fieldHiddenHeight, ruleopt.fieldCeiling);
	}

	/**
	 * Call this if the game has ended
	 */
	public void gameEnded() {
		if(endTime == 0) {
			endTime = System.nanoTime();
			statistics.gamerate = (float)(replayTimer / (0.00000006*(endTime - startTime)));
		}
		gameActive = false;
		timerActive = false;
		isInGame = false;
		if(ai != null) ai.shutdown(this, playerID);
	}

	/**
	 * Game stateUpdates
	 */
	public void update() {
		if(gameActive) {
			// Related processing replay
			if(!owner.replayMode || owner.replayRerecord) {
				// AIOf buttonProcessing
				if (ai != null) {
					if (aiShowHint == false) {
						ai.setControl(this, playerID, ctrl);
					} else {
						aiHintReady = (ai.thinkComplete || ((ai.thinkCurrentPieceNo > 0)
								&& (ai.thinkCurrentPieceNo <= ai.thinkLastPieceNo)));
						if (aiHintReady) {
							aiHintPiece = null;
							if (ai.bestHold)
							{
								if (holdPieceObject != null)
									aiHintPiece = new Piece(holdPieceObject);
								else
								{
									aiHintPiece = getNextObjectCopy(nextPieceCount);
									if (!aiHintPiece.offsetApplied)
										aiHintPiece.applyOffsetArray(ruleopt.pieceOffsetX[aiHintPiece.id],
												ruleopt.pieceOffsetY[aiHintPiece.id]);
								}
							}
							else if (nowPieceObject != null)
								aiHintPiece = new Piece(nowPieceObject);
						}
					}
				}

				//  input Replay recorded in the state
				replayData.setInputData(ctrl.getButtonBit(), replayTimer);
			} else {
				//  input Replay the state read from
				ctrl.setButtonBit(replayData.getInputData(replayTimer));
			}
			replayTimer++;
		}

		//  button input timeUpdates
		ctrl.updateButtonTime();

		// 最初の処理
		if(owner.mode != null) owner.mode.onFirst(this, playerID);
		owner.receiver.onFirst(this, playerID);
		if((ai != null) && (!owner.replayMode || owner.replayRerecord)) ai.onFirst(this, playerID);

		// Processing status of each
		if(!lagStop) {
			switch(stat) {
			case NOTHING:
				break;
			case SETTING:
				statSetting();
				break;
			case READY:
				statReady();
				break;
			case MOVE:
				dasRepeat = true;
				dasInstant = false;
				while(dasRepeat){
					statMove();
				}
				break;
			case LOCKFLASH:
				statLockFlash();
				break;
			case LINECLEAR:
				statLineClear();
				break;
			case ARE:
				statARE();
				break;
			case ENDINGSTART:
				statEndingStart();
				break;
			case CUSTOM:
				statCustom();
				break;
			case EXCELLENT:
				statExcellent();
				break;
			case GAMEOVER:
				statGameOver();
				break;
			case RESULT:
				statResult();
				break;
			case FIELDEDIT:
				statFieldEdit();
				break;
			case INTERRUPTITEM:
				statInterruptItem();
				break;
			}
		}

		// fieldOfBlock stateUpdate and statistics
		fieldUpdate();
		if((ending == 0) || (staffrollEnableStatistics)) statistics.update();

		// 最後の処理
		if(owner.mode != null) owner.mode.onLast(this, playerID);
		owner.receiver.onLast(this, playerID);
		if((ai != null) && (!owner.replayMode || owner.replayRerecord)) ai.onLast(this, playerID);

		// TimerIncrease
		if(gameActive && timerActive) {
			statistics.time++;
		}

		/*
		if(startTime > 0 && endTime == 0) {
			statistics.gamerate = (float)(replayTimer / (0.00000006*(System.nanoTime() - startTime)));
		}
		*/
	}

	/**
	 * Draw the screen
	 *  (EachMode Ya event Processing classes event Just call, OtherwiseGameEngineItself does not do anything)
	 */
	public void render() {
		// 最初の処理
		owner.receiver.renderFirst(this, playerID);
		if(owner.mode != null) owner.mode.renderFirst(this, playerID);

		if (rainbowAnimate)
			Block.updateRainbowPhase(this);

		// Processing status of each
		switch(stat) {
		case NOTHING:
			break;
		case SETTING:
			if(owner.mode != null) owner.mode.renderSetting(this, playerID);
			owner.receiver.renderSetting(this, playerID);
			break;
		case READY:
			if(owner.mode != null) owner.mode.renderReady(this, playerID);
			owner.receiver.renderReady(this, playerID);
			break;
		case MOVE:
			if(owner.mode != null) owner.mode.renderMove(this, playerID);
			owner.receiver.renderMove(this, playerID);
			break;
		case LOCKFLASH:
			if(owner.mode != null) owner.mode.renderLockFlash(this, playerID);
			owner.receiver.renderLockFlash(this, playerID);
			break;
		case LINECLEAR:
			if(owner.mode != null) owner.mode.renderLineClear(this, playerID);
			owner.receiver.renderLineClear(this, playerID);
			break;
		case ARE:
			if(owner.mode != null) owner.mode.renderARE(this, playerID);
			owner.receiver.renderARE(this, playerID);
			break;
		case ENDINGSTART:
			if(owner.mode != null) owner.mode.renderEndingStart(this, playerID);
			owner.receiver.renderEndingStart(this, playerID);
			break;
		case CUSTOM:
			if(owner.mode != null) owner.mode.renderCustom(this, playerID);
			owner.receiver.renderCustom(this, playerID);
			break;
		case EXCELLENT:
			if(owner.mode != null) owner.mode.renderExcellent(this, playerID);
			owner.receiver.renderExcellent(this, playerID);
			break;
		case GAMEOVER:
			if(owner.mode != null) owner.mode.renderGameOver(this, playerID);
			owner.receiver.renderGameOver(this, playerID);
			break;
		case RESULT:
			if(owner.mode != null) owner.mode.renderResult(this, playerID);
			owner.receiver.renderResult(this, playerID);
			break;
		case FIELDEDIT:
			if(owner.mode != null) owner.mode.renderFieldEdit(this, playerID);
			owner.receiver.renderFieldEdit(this, playerID);
			break;
		case INTERRUPTITEM:
			break;
		}

		if (owner.showInput)
		{
			if(owner.mode != null) owner.mode.renderInput(this, playerID);
			owner.receiver.renderInput(this, playerID);
		}
		if (ai != null)
		{
			if (aiShowState)
				ai.renderState(this, playerID);
			if (aiShowHint)
				ai.renderHint(this, playerID);
		}

		// 最後の処理
		if(owner.mode != null) owner.mode.renderLast(this, playerID);
		owner.receiver.renderLast(this, playerID);
	}

	/**
	 * Processing when the setup screen before the start of
	 */
	public void statSetting() {
		//  event 発生
		if(owner.mode != null) {
			if(owner.mode.onSetting(this, playerID) == true) return;
		}
		owner.receiver.onSetting(this, playerID);

		// Mode側が何もしない場合はReady画面へ移動
		stat = Status.READY;
		resetStatc();
	}

	/**
	 * Ready→GoProcessing time
	 */
	public void statReady() {
		//  event 発生
		if(owner.mode != null) {
			if(owner.mode.onReady(this, playerID) == true) return;
		}
		owner.receiver.onReady(this, playerID);

		// Horizontal reservoir
		if(ruleopt.dasInReady && gameActive) padRepeat();
		else if(ruleopt.dasRedirectInDelay) { dasRedirect(); }

		// Initialization
		if(statc[0] == 0) {
			// fieldInitialization
			createFieldIfNeeded();

			// NEXTCreating Peace
			if(nextPieceArrayID == null) {
				// Peace is possible emergence1If no one is to be able to all appearance
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

				// NEXTCreate the order of appearance of the piece
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
			// NEXTCreate an object of Peace
			if(nextPieceArrayObject == null) {
				nextPieceArrayObject = new Piece[nextPieceArrayID.length];

				for(int i = 0; i < nextPieceArrayObject.length; i++) {
					nextPieceArrayObject[i] = new Piece(nextPieceArrayID[i]);
					nextPieceArrayObject[i].direction = ruleopt.pieceDefaultDirection[nextPieceArrayObject[i].id];
					if(nextPieceArrayObject[i].direction >= Piece.DIRECTION_COUNT) {
						nextPieceArrayObject[i].direction = random.nextInt(Piece.DIRECTION_COUNT);
					}
					nextPieceArrayObject[i].connectBlocks = this.connectBlocks;
					nextPieceArrayObject[i].setColor(ruleopt.pieceColor[nextPieceArrayObject[i].id]);
					nextPieceArrayObject[i].setSkin(getSkin());
					nextPieceArrayObject[i].updateConnectData();
					nextPieceArrayObject[i].setAttribute(Block.BLOCK_ATTRIBUTE_VISIBLE, true);
					nextPieceArrayObject[i].setAttribute(Block.BLOCK_ATTRIBUTE_BONE, bone);
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
						nextPieceArrayObject[i].updateConnectData();
					}
				}
			}

			if(!readyDone) {
				//  button inputReset state
				ctrl.reset();
				// Game flagON
				gameActive = true;
				gameStarted = true;
				isInGame = true;
			}
		}

		// READYSound
		if(statc[0] == readyStart) playSE("ready");

		// GOSound
		if(statc[0] == goStart) playSE("go");

		// NEXTSkip
		if((statc[0] > 0) && (statc[0] < goEnd) && (holdButtonNextSkip) && (isHoldOK()) && (ctrl.isPush(Controller.BUTTON_D))) {
			playSE("initialhold");
			holdPieceObject = getNextObjectCopy(nextPieceCount);
			holdPieceObject.applyOffsetArray(ruleopt.pieceOffsetX[holdPieceObject.id], ruleopt.pieceOffsetY[holdPieceObject.id]);
			nextPieceCount++;
			if(nextPieceCount < 0) nextPieceCount = 0;
		}

		// Start
		if(statc[0] >= goEnd) {
			if(!readyDone) owner.bgmStatus.bgm = 0;
			if(owner.mode != null) owner.mode.startGame(this, playerID);
			owner.receiver.startGame(this, playerID);
			initialRotate();
			stat = Status.MOVE;
			resetStatc();
			if(!readyDone) {
				startTime = System.nanoTime();
				//startTime = System.nanoTime()/1000000L;
			}
			readyDone = true;
			return;
		}

		statc[0]++;
	}

	/**
	 * BlockProcess of moving the pieces
	 */
	public void statMove() {
		dasRepeat = false;

		//  event 発生
		if(owner.mode != null) {
			if(owner.mode.onMove(this, playerID) == true) return;
		}
		owner.receiver.onMove(this, playerID);

		// Horizontal reservoirInitialization
		int moveDirection = getMoveDirection();

		if((statc[0] > 0) || (ruleopt.dasInMoveFirstFrame)) {
			if(dasDirection != moveDirection) {
				dasDirection = moveDirection;
				if(!(dasDirection == 0 && ruleopt.dasStoreChargeOnNeutral)){
				   dasCount = 0;
				}
			}
		}

		// Processing at the time of emergence
		if(statc[0] == 0) {
			if((statc[1] == 0) && (initialHoldFlag == false)) {
				// Normal appearance
				nowPieceObject = getNextObjectCopy(nextPieceCount);
				nextPieceCount++;
				if(nextPieceCount < 0) nextPieceCount = 0;
				holdDisable = false;
			} else {
				// Hold appearance
				if(initialHoldFlag) {
					// Hold preceding
					if(holdPieceObject == null) {
						// 1Th
						holdPieceObject = getNextObjectCopy(nextPieceCount);
						holdPieceObject.applyOffsetArray(ruleopt.pieceOffsetX[holdPieceObject.id], ruleopt.pieceOffsetY[holdPieceObject.id]);
						nextPieceCount++;
						if(nextPieceCount < 0) nextPieceCount = 0;

						if(bone == true) getNextObject(nextPieceCount + ruleopt.nextDisplay - 1).setAttribute(Block.BLOCK_ATTRIBUTE_BONE, true);

						nowPieceObject = getNextObjectCopy(nextPieceCount);
						nextPieceCount++;
						if(nextPieceCount < 0) nextPieceCount = 0;
					} else {
						// 2Subsequent
						Piece pieceTemp = holdPieceObject;
						holdPieceObject = getNextObjectCopy(nextPieceCount);
						holdPieceObject.applyOffsetArray(ruleopt.pieceOffsetX[holdPieceObject.id], ruleopt.pieceOffsetY[holdPieceObject.id]);
						nowPieceObject = pieceTemp;
						nextPieceCount++;
						if(nextPieceCount < 0) nextPieceCount = 0;
					}
				} else {
					// Usually hold
					if(holdPieceObject == null) {
						// 1Th
						nowPieceObject.big = false;
						holdPieceObject = nowPieceObject;
						nowPieceObject = getNextObjectCopy(nextPieceCount);
						nextPieceCount++;
						if(nextPieceCount < 0) nextPieceCount = 0;
					} else {
						// 2Subsequent
						nowPieceObject.big = false;
						Piece pieceTemp = holdPieceObject;
						holdPieceObject = nowPieceObject;
						nowPieceObject = pieceTemp;
					}
				}

				// DirectionReturn
				if((ruleopt.holdResetDirection) && (ruleopt.pieceDefaultDirection[holdPieceObject.id] < Piece.DIRECTION_COUNT)) {
					holdPieceObject.direction = ruleopt.pieceDefaultDirection[holdPieceObject.id];
					holdPieceObject.updateConnectData();
				}

				// Was used count+1
				holdUsedCount++;
				statistics.totalHoldUsed++;

				// Disabling Hold
				initialHoldFlag = false;
				holdDisable = true;
			}
			playSE("piece" + getNextObject(nextPieceCount).id);

			if(nowPieceObject.offsetApplied == false)
				nowPieceObject.applyOffsetArray(ruleopt.pieceOffsetX[nowPieceObject.id], ruleopt.pieceOffsetY[nowPieceObject.id]);

			nowPieceObject.big = big;

			// Appearance position (Horizontal)
			nowPieceX = getSpawnPosX(field, nowPieceObject);

			// Appearance position (Vertical)
			nowPieceY = getSpawnPosY(nowPieceObject);

			nowPieceBottomY = nowPieceObject.getBottom(nowPieceX, nowPieceY, field);
			nowPieceColorOverride = -1;

			if(itemRollRollEnable) nowPieceColorOverride = Block.BLOCK_COLOR_GRAY;

			// Precedingrotation
			if(versionMajor < 7.5f) initialRotate(); //XXX: Weird active time IRS
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
			nowPieceRotateFailCount = 0;
			nowWallkickCount = 0;
			nowUpwardWallkickCount = 0;
			lineClearing = 0;
			lastmove = LastMove.NONE;
			kickused = false;
			tspin = false;
			tspinmini = false;
			tspinez = false;

			getNextObject(nextPieceCount + ruleopt.nextDisplay - 1).setAttribute(Block.BLOCK_ATTRIBUTE_BONE, bone);

			if(ending == 0) timerActive = true;

			if((ai != null) && (!owner.replayMode || owner.replayRerecord)) ai.newPiece(this, playerID);
		}

		checkDropContinuousUse();

		boolean softdropUsed = false; // This frame ToSoft dropI usedtrue
		int softdropFallNow = 0; // This frame OfSoft dropStage which has fallen incount

		boolean updown = false; // UpUnder simultaneous press flag
		if(ctrl.isPress(getUp()) && ctrl.isPress(getDown())) updown = true;

		if(!dasInstant) {

			// Hold
			if(ctrl.isPush(Controller.BUTTON_D) || initialHoldFlag) {
				if(isHoldOK()) {
					statc[0] = 0;
					statc[1] = 1;
					if(!initialHoldFlag) playSE("hold");
					initialHoldContinuousUse = true;
					initialHoldFlag = false;
					holdDisable = true;
					initialRotate(); //Hold swap triggered IRS
					statMove();
					return;
				} else if((statc[0] > 0) && (!initialHoldFlag)) {
					playSE("holdfail");
				}
			}

			// rotation
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

				//  button input
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
				// Direction after rotationI decided to
				int rt = getRotateDirection(move);

				// rotationYou can determine whether the
				if(nowPieceObject.checkCollision(nowPieceX, nowPieceY, rt, field) == false)
				{
					// WallkickWithoutrotationwhen you can
					rotated = true;
					kickused = false;
					nowPieceObject.direction = rt;
					nowPieceObject.updateConnectData();
				} else if( (ruleopt.rotateWallkick == true) &&
						   (wallkick != null) &&
						   ((initialRotateDirection == 0) || (ruleopt.rotateInitialWallkick == true)) &&
						   ((ruleopt.lockresetLimitOver != RuleOptions.LOCKRESET_LIMIT_OVER_NOWALLKICK) || (isRotateCountExceed() == false)) )
				{
					// WallkickAttempt to
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

						if(ruleopt.lockresetWallkick && !isRotateCountExceed()) {
							lockDelayNow = 0;
							nowPieceObject.setDarkness(0f);
						}
					}
				}

				// Domino Quick Turn
				if(!rotated && dominoQuickTurn && (nowPieceObject.id == Piece.PIECE_I2) && (nowPieceRotateFailCount >= 1)) {
					rt = getRotateDirection(2);
					rotated = true;
					nowPieceObject.direction = rt;
					nowPieceObject.updateConnectData();
					nowPieceRotateFailCount = 0;

					if(nowPieceObject.checkCollision(nowPieceX, nowPieceY, rt, field) == true) {
						nowPieceY--;
					} else if(onGroundBeforeRotate) {
						nowPieceY++;
					}
				}

				if(rotated == true) {
					// rotationSuccess
					nowPieceBottomY = nowPieceObject.getBottom(nowPieceX, nowPieceY, field);

					if((ruleopt.lockresetRotate == true) && (isRotateCountExceed() == false)) {
						lockDelayNow = 0;
						nowPieceObject.setDarkness(0f);
					}

					if(onGroundBeforeRotate) {
						extendedRotateCount++;
						lastmove = LastMove.ROTATE_GROUND;
					} else {
						lastmove = LastMove.ROTATE_AIR;
					}

					if(initialRotateDirection == 0) {
						playSE("rotate");
					}

					nowPieceRotateCount++;
					if((ending == 0) || (staffrollEnableStatistics)) statistics.totalPieceRotate++;
				} else {
					// rotationFailure
					playSE("rotfail");
					nowPieceRotateFailCount++;
				}
			}
			initialRotateDirection = 0;

			// game over check
			if((statc[0] == 0) && (nowPieceObject.checkCollision(nowPieceX, nowPieceY, field) == true)) {
				// BlockSo if you can shift on the position of the emergence of
				for(int i = 0; i < ruleopt.pieceEnterMaxDistanceY; i++) {
					if(nowPieceObject.big) nowPieceY -= 2;
					else nowPieceY--;

					if(nowPieceObject.checkCollision(nowPieceX, nowPieceY, field) == false) {
						nowPieceBottomY = nowPieceObject.getBottom(nowPieceX, nowPieceY, field);
						break;
					}
				}

				// Death
				if(nowPieceObject.checkCollision(nowPieceX, nowPieceY, field) == true) {
					nowPieceObject.placeToField(nowPieceX, nowPieceY, field);
					nowPieceObject = null;
					stat = Status.GAMEOVER;
					if((ending == 2) && (staffrollNoDeath)) stat = Status.NOTHING;
					resetStatc();
					return;
				}
			}

		}

		int move = 0;
		boolean sidemoveflag = false;	// This frame I moved next totrue

		if((statc[0] > 0) || (ruleopt.moveFirstFrame == true)) {
			// Lateral motion
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
								lastmove = LastMove.SLIDE_GROUND;
							} else {
								lastmove = LastMove.SLIDE_AIR;
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

			if((!dasRepeat) || (versionMajor < 7.6f)){
				// Hard drop
				if( (ctrl.isPress(getUp()) == true) &&
					(harddropContinuousUse == false) &&
					(ruleopt.harddropEnable == true) &&
					((isDiagonalMoveEnabled() == true) || (sidemoveflag == false)) &&
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
	
					lastmove = LastMove.FALL_SELF;
					if(ruleopt.lockresetFall == true) {
						lockDelayNow = 0;
						nowPieceObject.setDarkness(0f);
						extendedMoveCount = 0;
						extendedRotateCount = 0;
					}
				}
	
				if(!ruleopt.softdropGravitySpeedLimit || (ruleopt.softdropSpeed < 1.0f)) {
					// Old Soft Drop codes
					if( (ctrl.isPress(getDown()) == true) &&
						(softdropContinuousUse == false) &&
						(ruleopt.softdropEnable == true) &&
						((isDiagonalMoveEnabled() == true) || (sidemoveflag == false)) &&
						((ruleopt.moveUpAndDown == true) || (updown == false)) )
					{
						if((ruleopt.softdropMultiplyNativeSpeed == true) || (speed.denominator <= 0))
							gcount += (int)(speed.gravity * ruleopt.softdropSpeed);
						else
							gcount += (int)(speed.denominator * ruleopt.softdropSpeed);
	
						softdropUsed = true;
					}
				} else {
					// New Soft Drop codes
					if( ctrl.isPress(getDown()) && !softdropContinuousUse &&
						ruleopt.softdropEnable && (isDiagonalMoveEnabled() || !sidemoveflag) &&
						(ruleopt.moveUpAndDown || !updown) &&
						(ruleopt.softdropMultiplyNativeSpeed || (speed.gravity < (int)(speed.denominator * ruleopt.softdropSpeed))) )
					{
						if((ruleopt.softdropMultiplyNativeSpeed == true) || (speed.denominator <= 0)) {
							// gcount += (int)(speed.gravity * ruleopt.softdropSpeed);
							gcount = (int)(speed.gravity * ruleopt.softdropSpeed);
						} else {
							// gcount += (int)(speed.denominator * ruleopt.softdropSpeed);
							gcount = (int)(speed.denominator * ruleopt.softdropSpeed);
						}
	
						softdropUsed = true;
					} else {
						// Fall
						// This prevents soft drop from adding to the gravity speed.
						gcount += speed.gravity;
					}
				}
			}
			
			if((ending == 0) || (staffrollEnableStatistics)) statistics.totalPieceActiveTime++;
		}

		if(!ruleopt.softdropGravitySpeedLimit || (ruleopt.softdropSpeed < 1.0f))
			gcount += speed.gravity;	// Part of Old Soft Drop

		while((gcount >= speed.denominator) || (speed.gravity < 0)) {
			if(nowPieceObject.checkCollision(nowPieceX, nowPieceY + 1, field) == false) {
				if(speed.gravity >= 0) gcount -= speed.denominator;
				nowPieceY++;

				if(ruleopt.lockresetFall == true) {
					lockDelayNow = 0;
					nowPieceObject.setDarkness(0f);
				}

				if((lastmove != LastMove.ROTATE_GROUND) && (lastmove != LastMove.SLIDE_GROUND) && (lastmove != LastMove.FALL_SELF)) {
					extendedMoveCount = 0;
					extendedRotateCount = 0;
				}

				if(softdropUsed == true) {
					lastmove = LastMove.FALL_SELF;
					softdropFall++;
					softdropFallNow++;
					playSE("softdrop");
				} else {
					lastmove = LastMove.FALL_AUTO;
				}
			} else {
				break;
			}
		}

		if(softdropFallNow > 0) {
			if(owner.mode != null) owner.mode.afterSoftDropFall(this, playerID, softdropFallNow);
			owner.receiver.afterSoftDropFall(this, playerID, softdropFallNow);
		}

		// And fixed ground
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

			// trueI fixed immediately becomes
			boolean instantlock = false;

			// Hard dropFixation
			if( (ctrl.isPress(getUp()) == true) &&
				(harddropContinuousUse == false) &&
				(ruleopt.harddropEnable == true) &&
				((isDiagonalMoveEnabled() == true) || (sidemoveflag == false)) &&
				((ruleopt.moveUpAndDown == true) || (updown == false)) &&
				(ruleopt.harddropLock == true) )
			{
				harddropContinuousUse = true;
				manualLock = true;
				instantlock = true;
			}

			// Soft dropFixation
			if( (ctrl.isPress(getDown()) == true) &&
				(softdropContinuousUse == false) &&
				(ruleopt.softdropEnable == true) &&
				((isDiagonalMoveEnabled() == true) || (sidemoveflag == false)) &&
				((ruleopt.moveUpAndDown == true) || (updown == false)) &&
				(ruleopt.softdropLock == true) )
			{
				softdropContinuousUse = true;
				manualLock = true;
				instantlock = true;
			}

			// Soft-drop fixed in the ground state
			if( (ctrl.isPush(getDown()) == true) &&
				(ruleopt.softdropEnable == true) &&
				((isDiagonalMoveEnabled() == true) || (sidemoveflag == false)) &&
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

			// &amp; MobilerotationcountLimit exceeded
			if( (ruleopt.lockresetLimitOver == RuleOptions.LOCKRESET_LIMIT_OVER_INSTANT) && (isMoveCountExceed() || isRotateCountExceed()) ) {
				instantlock = true;
			}

			// Immediately fixed ground
			if( (getLockDelay() == 0) && ((gcount >= speed.denominator) || (speed.gravity < 0)) ) {
				instantlock = true;
			}

			// Fixation
			if( ((lockDelayNow >= getLockDelay()) && (getLockDelay() > 0)) || (instantlock == true) ) {
				if(ruleopt.lockflash > 0) nowPieceObject.setDarkness(-0.8f);

				// T-Spin判定
				if(((lastmove == LastMove.ROTATE_GROUND) || (lastmove == LastMove.ROTATE_AIR)) && (tspinEnable == true)) {
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

				if (clearMode == ClearType.LINE)
					lineClearing = field.checkLineNoFlag();
				else if (clearMode == ClearType.COLOR)
					lineClearing = field.checkColor(colorClearSize, false, garbageColorClear, gemSameColor, ignoreHidden);
				else if (clearMode == ClearType.LINE_COLOR)
					lineClearing = field.checkLineColor(colorClearSize, false, lineColorDiagonals, gemSameColor);
				else if (clearMode == ClearType.GEM_COLOR)
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

				// Next 処理を決める(Mode 側でステータスを弄っている場合は何もしない)
				if((stat == Status.MOVE) || (versionMajor <= 6.3f)) {
					resetStatc();

					if((ending == 1) && (versionMajor >= 6.6f) && (versionMinorOld >= 0.1f)) {
						// Ending
						stat = Status.ENDINGSTART;
					} else if( (!put && ruleopt.fieldLockoutDeath) || (partialLockOut && ruleopt.fieldPartialLockoutDeath) ) {
						// 画面外に置いて死亡
						stat = Status.GAMEOVER;
						if((ending == 2) && (staffrollNoDeath)) stat = Status.NOTHING;
					} else if ((lineGravityType == LineGravity.CASCADE || lineGravityType == LineGravity.CASCADE_SLOW)
							&& !connectBlocks) {
						stat = Status.LINECLEAR;
						statc[0] = getLineDelay();
						statLineClear();
					} else if( (lineClearing > 0) && ((ruleopt.lockflash <= 0) || (!ruleopt.lockflashBeforeLineClear)) ) {
						// Line clear
						stat = Status.LINECLEAR;
						statLineClear();
					} else if( ((getARE() > 0) || (lagARE) || (ruleopt.lockflashBeforeLineClear)) &&
							    (ruleopt.lockflash > 0) && (ruleopt.lockflashOnlyFrame) )
					{
						// AREあり (光あり）
						stat = Status.LOCKFLASH;
					} else if((getARE() > 0) || (lagARE)) {
						// ARESome (No light)
						statc[1] = getARE();
						stat = Status.ARE;
					} else if(interruptItemNumber != INTERRUPTITEM_NONE) {
						// Effective treatment interruption item
						nowPieceObject = null;
						interruptItemPreviousStat = Status.MOVE;
						stat = Status.INTERRUPTITEM;
					} else {
						// AREなし
						stat = Status.MOVE;
						if(ruleopt.moveFirstFrame == false) statMove();
					}
				}
				return;
			}
		}

		// Horizontal reservoir
		if((statc[0] > 0) || (ruleopt.dasInMoveFirstFrame)) {
			if( (moveDirection != 0) && (moveDirection == dasDirection) && ((dasCount < getDAS()) || (getDAS() <= 0)) ) {
				dasCount++;
			}
		}

		statc[0]++;
	}

	/**
	 * BlockSparkling happens when fixed immediately after
	 */
	public void statLockFlash() {
		//  event 発生
		if(owner.mode != null) {
			if(owner.mode.onLockFlash(this, playerID) == true) return;
		}
		owner.receiver.onLockFlash(this, playerID);

		statc[0]++;

		checkDropContinuousUse();

		// Horizontal reservoir
		if(ruleopt.dasInLockFlash) padRepeat();
		else if(ruleopt.dasRedirectInDelay) { dasRedirect(); }

		// Next Status
		if(statc[0] >= ruleopt.lockflash) {
			resetStatc();

			if(lineClearing > 0) {
				// Line clear
				stat = Status.LINECLEAR;
				statLineClear();
			} else {
				// ARE
				statc[1] = getARE();
				stat = Status.ARE;
			}
			return;
		}
	}

	/**
	 * Line clearProcessing
	 */
	public void statLineClear() {
		//  event 発生
		if(owner.mode != null) {
			if(owner.mode.onLineClear(this, playerID) == true) return;
		}
		owner.receiver.onLineClear(this, playerID);

		checkDropContinuousUse();

		// Horizontal reservoir
		if(ruleopt.dasInLineClear) padRepeat();
		else if(ruleopt.dasRedirectInDelay) { dasRedirect(); }

		// First frame
		if(statc[0] == 0) {
			if (sticky > 0)
				field.setBlockLinkByColor();
			if (sticky == 2)
				field.setAllAttribute(Block.BLOCK_ATTRIBUTE_IGNORE_BLOCKLINK, true);
			// Line clear flagを設定
			if (clearMode == ClearType.LINE)
				lineClearing = field.checkLine();
			// Set color clear flags
			else if (clearMode == ClearType.COLOR)
				lineClearing = field.checkColor(colorClearSize, true, garbageColorClear, gemSameColor, ignoreHidden);
			// Set line color clear flags
			else if (clearMode == ClearType.LINE_COLOR)
				lineClearing = field.checkLineColor(colorClearSize, true, lineColorDiagonals, gemSameColor);
			else if (clearMode == ClearType.GEM_COLOR)
				lineClearing = field.gemColorCheck(colorClearSize, true, garbageColorClear, ignoreHidden);

			// LinescountI decided to
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
				if (clearMode == ClearType.LINE)
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

			// Blockを消す演出を出す (まだ実際には消えていない）
			if (clearMode == ClearType.LINE) {
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
			} else if (clearMode == ClearType.LINE_COLOR || clearMode == ClearType.COLOR || clearMode == ClearType.GEM_COLOR)
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
			if (clearMode == ClearType.LINE)
				field.clearLine();
			else if (clearMode == ClearType.COLOR)
				field.clearColor(colorClearSize, garbageColorClear, gemSameColor, ignoreHidden);
			else if (clearMode == ClearType.LINE_COLOR)
				field.clearLineColor(colorClearSize, lineColorDiagonals, gemSameColor);
			else if (clearMode == ClearType.GEM_COLOR)
				lineClearing = field.gemClearColor(colorClearSize, garbageColorClear, ignoreHidden);
		}

		// Linesを1段落とす
		if((lineGravityType == LineGravity.NATIVE) &&
		   (getLineDelay() >= (lineClearing - 1)) && (statc[0] >= getLineDelay() - (lineClearing - 1)) && (ruleopt.lineFallAnim))
		{
			field.downFloatingBlocksSingleLine();
		}

		// Line delay cancel check
		delayCancelMoveLeft = ctrl.isPush(Controller.BUTTON_LEFT);
		delayCancelMoveRight = ctrl.isPush(Controller.BUTTON_RIGHT);

		boolean moveCancel = ruleopt.lineCancelMove && (ctrl.isPush(getUp()) ||
			ctrl.isPush(getDown()) || delayCancelMoveLeft || delayCancelMoveRight);
		boolean rotateCancel = ruleopt.lineCancelRotate && (ctrl.isPush(Controller.BUTTON_A) ||
			ctrl.isPush(Controller.BUTTON_B) || ctrl.isPush(Controller.BUTTON_C) ||
			ctrl.isPush(Controller.BUTTON_E));
		boolean holdCancel = ruleopt.lineCancelHold && ctrl.isPush(Controller.BUTTON_D);

		delayCancel = moveCancel || rotateCancel || holdCancel;

		if( (statc[0] < getLineDelay()) && delayCancel ) {
			statc[0] = getLineDelay();
		}

		// Next Status
		if(statc[0] >= getLineDelay()) {
			// Cascade
			if((lineGravityType == LineGravity.CASCADE || lineGravityType == LineGravity.CASCADE_SLOW)) {
				if (statc[6] < getCascadeDelay()) {
					statc[6]++;
					return;
				} else if(field.doCascadeGravity(lineGravityType)) {
					statc[6] = 0;
					return;
				} else if (statc[6] < getCascadeClearDelay()) {
					if (sticky > 0)
						field.setBlockLinkByColor();
					statc[6]++;
					return;
				} else if(((clearMode == ClearType.LINE) && field.checkLineNoFlag() > 0) ||
						((clearMode == ClearType.COLOR) && field.checkColor(colorClearSize, false, garbageColorClear, gemSameColor, ignoreHidden) > 0) ||
						((clearMode == ClearType.LINE_COLOR) && field.checkLineColor(colorClearSize, false, lineColorDiagonals, gemSameColor) > 0) ||
						((clearMode == ClearType.GEM_COLOR) && field.gemColorCheck(colorClearSize, false, garbageColorClear, ignoreHidden) > 0)) {
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
			if (sticky > 0)
				field.setBlockLinkByColor();
			if (sticky == 2)
				field.setAllAttribute(Block.BLOCK_ATTRIBUTE_IGNORE_BLOCKLINK, true);

			if(!skip) {
				if(lineGravityType == LineGravity.NATIVE) field.downFloatingBlocks();
				playSE("linefall");

				field.lineColorsCleared = null;

				if((stat == Status.LINECLEAR) || (versionMajor <= 6.3f)) {
					resetStatc();
					if(ending == 1) {
						// Ending
						stat = Status.ENDINGSTART;
					} else if((getARELine() > 0) || (lagARE)) {
						// ARESome
						statc[0] = 0;
						statc[1] = getARELine();
						statc[2] = 1;
						stat = Status.ARE;
					} else if(interruptItemNumber != INTERRUPTITEM_NONE) {
						// Effective treatment interruption item
						nowPieceObject = null;
						interruptItemPreviousStat = Status.MOVE;
						stat = Status.INTERRUPTITEM;
					} else {
						// ARENo
						nowPieceObject = null;
						if(versionMajor < 7.5f) initialRotate(); //XXX: Weird IRS thing on lines cleared but no ARE
						stat = Status.MOVE;
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
	 * AREProcessing during
	 */
	public void statARE() {
		//  event 発生
		if(owner.mode != null) {
			if(owner.mode.onARE(this, playerID) == true) return;
		}
		owner.receiver.onARE(this, playerID);

		statc[0]++;

		checkDropContinuousUse();

		// ARE cancel check
		delayCancelMoveLeft = ctrl.isPush(Controller.BUTTON_LEFT);
		delayCancelMoveRight = ctrl.isPush(Controller.BUTTON_RIGHT);

		boolean moveCancel = ruleopt.areCancelMove && (ctrl.isPush(getUp()) ||
			ctrl.isPush(getDown()) || delayCancelMoveLeft || delayCancelMoveRight);
		boolean rotateCancel = ruleopt.areCancelRotate && (ctrl.isPush(Controller.BUTTON_A) ||
			ctrl.isPush(Controller.BUTTON_B) || ctrl.isPush(Controller.BUTTON_C) ||
			ctrl.isPush(Controller.BUTTON_E));
		boolean holdCancel = ruleopt.areCancelHold && ctrl.isPush(Controller.BUTTON_D);

		delayCancel = moveCancel || rotateCancel || holdCancel;

		if( (statc[0] < statc[1]) && delayCancel ) {
			statc[0] = statc[1];
		}

		// Horizontal reservoir
		if( (ruleopt.dasInARE) && ((statc[0] < statc[1] - 1) || (ruleopt.dasInARELastFrame)) )
			padRepeat();
		else if(ruleopt.dasRedirectInDelay) { dasRedirect(); }

		// Next Status
		if((statc[0] >= statc[1]) && (!lagARE)) {
			nowPieceObject = null;
			resetStatc();

			if(interruptItemNumber != INTERRUPTITEM_NONE) {
				// 中断効果のあるアイテム処理
				interruptItemPreviousStat = Status.MOVE;
				stat = Status.INTERRUPTITEM;
			} else {
				// BlockPeace movement process
				initialRotate();
				stat = Status.MOVE;
			}
		}
	}

	/**
	 * EndingRush processing
	 */
	public void statEndingStart() {
		//  event 発生
		if(owner.mode != null) {
			if(owner.mode.onEndingStart(this, playerID) == true) return;
		}
		owner.receiver.onEndingStart(this, playerID);

		checkDropContinuousUse();

		// Horizontal reservoir
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
				stat = Status.MOVE;
			} else {
				stat = Status.EXCELLENT;
			}
		}
	}

	/**
	 * Each gameMode Treatment of status that can be freely used
	 */
	public void statCustom() {
		//  event 発生
		if(owner.mode != null) {
			if(owner.mode.onCustom(this, playerID) == true) return;
		}
		owner.receiver.onCustom(this, playerID);
	}

	/**
	 * EndingScreen
	 */
	public void statExcellent() {
		//  event 発生
		if(owner.mode != null) {
			if(owner.mode.onExcellent(this, playerID) == true) return;
		}
		owner.receiver.onExcellent(this, playerID);

		if(statc[0] == 0) {
			gameEnded();
			owner.bgmStatus.fadesw = true;

			resetFieldVisible();

			playSE("excellent");
		}

		if((statc[0] >= 120) && (ctrl.isPush(Controller.BUTTON_A))) {
			statc[0] = 600;
		}

		if((statc[0] >= 600) && (statc[1] == 0)) {
			resetStatc();
			stat = Status.GAMEOVER;
		} else {
			statc[0]++;
		}
	}

	/**
	 * game overProcessing
	 */
	public void statGameOver() {
		//  event 発生
		if(owner.mode != null) {
			if(owner.mode.onGameOver(this, playerID) == true) return;
		}
		owner.receiver.onGameOver(this, playerID);

		if(lives <= 0) {
			// When I can not be recovered anymore
			if(statc[0] == 0) {
				gameEnded();
				blockShowOutlineOnly = false;
				if(owner.getPlayers() < 2) owner.bgmStatus.bgm = BGMStatus.BGM_NOTHING;

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
							if(displaysize != -1) {
								blk.darkness = 0.3f;
							}
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
						owner.engine[i].stat = Status.RESULT;
					}
				}
			}
		} else {
			// When it can be revived
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
				stat = Status.MOVE;
			}
		}
	}

	/**
	 * Results screen
	 */
	public void statResult() {
		// Event
		if(owner.mode != null) {
			if(owner.mode.onResult(this, playerID) == true) return;
		}
		owner.receiver.onResult(this, playerID);

		// Turn-off in-game flags
		gameActive = false;
		timerActive = false;
		isInGame = false;

		// Cursor movement
		if(ctrl.isMenuRepeatKey(Controller.BUTTON_LEFT) || ctrl.isMenuRepeatKey(Controller.BUTTON_RIGHT)) {
			if(statc[0] == 0) statc[0] = 1;
			else statc[0] = 0;
			playSE("cursor");
		}

		// Confirm
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
	 * fieldEdit screen
	 */
	public void statFieldEdit() {
		//  event 発生
		if(owner.mode != null) {
			if(owner.mode.onFieldEdit(this, playerID) == true) return;
		}
		owner.receiver.onFieldEdit(this, playerID);

		fldeditFrames++;

		// Cursor movement
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
		if(ctrl.isMenuRepeatKey(getUp(), false)) {
			playSE("move");
			fldeditY--;
			if(fldeditY < 0) fldeditY = fieldHeight - 1;
		}
		if(ctrl.isMenuRepeatKey(getDown(), false)) {
			playSE("move");
			fldeditY++;
			if(fldeditY > fieldHeight - 1) fldeditY = 0;
		}

		// Color selection
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

		// Placement
		if(ctrl.isPress(Controller.BUTTON_A) && (fldeditFrames > 10)) {
			try {
				if(field.getBlockColorE(fldeditX, fldeditY) != fldeditColor) {
					Block blk = new Block(fldeditColor, getSkin(), Block.BLOCK_ATTRIBUTE_VISIBLE | Block.BLOCK_ATTRIBUTE_OUTLINE);
					field.setBlockE(fldeditX, fldeditY, blk);
					playSE("change");
				}
			} catch (Exception e) {}
		}

		// Elimination
		if(ctrl.isPress(Controller.BUTTON_D) && (fldeditFrames > 10)) {
			try {
				if(!field.getBlockEmptyE(fldeditX, fldeditY)) {
					field.setBlockColorE(fldeditX, fldeditY, Block.BLOCK_COLOR_NONE);
					playSE("change");
				}
			} catch (Exception e) {}
		}

		// End
		if(ctrl.isPush(Controller.BUTTON_B) && (fldeditFrames > 10)) {
			stat = fldeditPreviousStat;
			if(owner.mode != null) owner.mode.fieldEditExit(this, playerID);
			owner.receiver.fieldEditExit(this, playerID);
		}
	}

	/**
	 * Effective treatment interruption Play items
	 */
	public void statInterruptItem() {
		boolean contFlag = false;	// Continue flag

		switch(interruptItemNumber) {
		case INTERRUPTITEM_MIRROR:	// Miller
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
	 * Mirror operation
	 * @return When true,Process continues Miller
	 */
	public boolean interruptItemMirrorProc() {
		if(statc[0] == 0) {
			// fieldCopy the backup
			interruptItemMirrorField = new Field(field);
			// fieldOfBlockTurn off all the
			field.reset();
		} else if((statc[0] >= 21) && (statc[0] < 21 + (field.getWidth() * 2)) && (statc[0] % 2 == 0)) {
			// Inversion
			int x = ((statc[0] - 20) / 2) - 1;

			for(int y = (field.getHiddenHeight() * -1); y < field.getHeight(); y++) {
				field.setBlock(field.getWidth() - x - 1, y, interruptItemMirrorField.getBlock(x, y));
			}
		} else if(statc[0] < 21 + (field.getWidth() * 2) + 5) {
			// Wait time
		} else {
			// End
			statc[0] = 0;
			interruptItemMirrorField = null;
			return false;
		}

		statc[0]++;
		return true;
	}
}
