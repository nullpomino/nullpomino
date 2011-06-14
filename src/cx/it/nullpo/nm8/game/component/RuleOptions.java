package cx.it.nullpo.nm8.game.component;

import java.io.Serializable;

import cx.it.nullpo.nm8.game.play.GameManager;
import cx.it.nullpo.nm8.game.subsystem.randomizer.RandomizerFactory;
import cx.it.nullpo.nm8.game.subsystem.wallkick.WallkickFactory;

/**
 * Game rule options
 */
public class RuleOptions implements Serializable {
	/** Serial version ID */
	private static final long serialVersionUID = -935704342253648268L;

	/** Game style */
	public int style;

	/** Wallkick to use */
	public int wallkickID;

	/** Randomizer to use */
	public int randomizerID;

	/** Piece pattern offect X */
	public int[][] pieceOffsetX;

	/** Piece pattern offect Y */
	public int[][] pieceOffsetY;

	/** Piece spawn position offset X */
	public int[][] pieceSpawnX;

	/** Piece spawn position offset Y */
	public int[][] pieceSpawnY;

	/** Piece spawn position offset X Big */
	public int[][] pieceSpawnXBig;

	/** Piece spawn position offset Y Big */
	public int[][] pieceSpawnYBig;

	/** Piece colors */
	public int[] pieceColor;

	/** Piece default directions */
	public int[] pieceDefaultDirection;

	/** Piece spawns above the field */
	public boolean pieceEnterAboveField;

	/** Number of tries to push up when the new piece overraps to existing blocks */
	public int pieceEnterMaxDistanceY;

	/** Preferred field width */
	public int fieldWidth;

	/** Preferred field height */
	public int fieldHeight;

	/** Preferred field hidden height */
	public int fieldHiddenHeight;

	/** Field ceiling flag */
	public boolean fieldCeiling;

	/** Cause a death when the piece locks completely outside of the field */
	public boolean fieldLockoutDeath;

	/** Cause a death when at least 1 block of the piece locks outside of the field */
	public boolean fieldPartialLockoutDeath;

	/** Number of piece preview */
	public int nextDisplay;

	/** Enable Hold */
	public boolean holdEnable;

	/** Enable IHS */
	public boolean holdInitial;

	/** Can't use IHS twice in a row */
	public boolean holdInitialLimit;

	/** Reset the piece's direction when using hold */
	public boolean holdResetDirection;

	/** Number of holds (-1:Unlimited) */
	public int holdLimit;

	/**
	 * Constructor
	 */
	public RuleOptions() {
		reset();
	}

	/**
	 * Copy constructor
	 * @param r Copy source
	 */
	public RuleOptions(RuleOptions r) {
		copy(r);
	}

	/**
	 * Initialization
	 */
	public void reset() {
		style = GameManager.GAMESTYLE_TETROMINO;
		wallkickID = WallkickFactory.WALLKICK_STANDARD;
		randomizerID = RandomizerFactory.RANDOMIZER_BAG;

		pieceOffsetX = new int[Piece.PIECE_COUNT][Piece.DIRECTION_COUNT];
		pieceOffsetY = new int[Piece.PIECE_COUNT][Piece.DIRECTION_COUNT];
		pieceSpawnX = new int[Piece.PIECE_COUNT][Piece.DIRECTION_COUNT];
		pieceSpawnXBig = new int[Piece.PIECE_COUNT][Piece.DIRECTION_COUNT];
		pieceSpawnY = new int[Piece.PIECE_COUNT][Piece.DIRECTION_COUNT];
		pieceSpawnYBig = new int[Piece.PIECE_COUNT][Piece.DIRECTION_COUNT];

		pieceColor = new int[Piece.PIECE_COUNT];
		pieceColor[Piece.PIECE_I] = Block.BLOCK_COLOR_CYAN;
		pieceColor[Piece.PIECE_L] = Block.BLOCK_COLOR_ORANGE;
		pieceColor[Piece.PIECE_O] = Block.BLOCK_COLOR_YELLOW;
		pieceColor[Piece.PIECE_Z] = Block.BLOCK_COLOR_RED;
		pieceColor[Piece.PIECE_T] = Block.BLOCK_COLOR_PURPLE;
		pieceColor[Piece.PIECE_J] = Block.BLOCK_COLOR_BLUE;
		pieceColor[Piece.PIECE_S] = Block.BLOCK_COLOR_GREEN;
		pieceColor[Piece.PIECE_I1] = Block.BLOCK_COLOR_PURPLE;
		pieceColor[Piece.PIECE_I2] = Block.BLOCK_COLOR_BLUE;
		pieceColor[Piece.PIECE_I3] = Block.BLOCK_COLOR_GREEN;
		pieceColor[Piece.PIECE_L3] = Block.BLOCK_COLOR_ORANGE;

		pieceDefaultDirection = new int[Piece.PIECE_COUNT];
		pieceEnterAboveField = true;
		pieceEnterMaxDistanceY = 0;

		fieldWidth = Field.DEFAULT_WIDTH;
		fieldHeight = Field.DEFAULT_HEIGHT;
		fieldHiddenHeight = Field.DEFAULT_HIDDEN_HEIGHT;
		fieldCeiling = false;
		fieldLockoutDeath = true;
		fieldPartialLockoutDeath = false;

		nextDisplay = 6;

		holdEnable = true;
		holdInitial = true;
		holdInitialLimit = false;
		holdResetDirection = true;
		holdLimit = -1;
	}

	/**
	 * Copy from another RuleOptions
	 * @param r Copy source
	 */
	public void copy(RuleOptions r) {

	}
}
