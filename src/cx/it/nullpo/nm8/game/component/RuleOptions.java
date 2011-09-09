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

	/** Control Scheme IDs */
	public static final int CTRL_SCHEME_STANDARD = 0, CTRL_SCHEME_CLASSIC = 1;

	/** Do not reset lock delay after exceeding lock reset limit */
	public static final int LOCKRESET_LIMIT_OVER_NORESET = 0;
	/** Lock instantly after exceeding lock reset limit */
	public static final int LOCKRESET_LIMIT_OVER_INSTANT = 1;
	/** Disable wallkick after exceeding lock reset limit */
	public static final int LOCKRESET_LIMIT_OVER_NOWALLKICK = 2;
	/** Disallow all rotations after exceeding lock reset limit */
	public static final int LOCKRESET_LIMIT_OVER_NOROTATE = 3;

	/** Game style */
	public int style;

	/** Wallkick to use */
	public int wallkickID;

	/** Randomizer to use */
	public int randomizerID;

	/**
	 * Control Scheme ID to use<br>
	 * Some players may want to swap rotation buttons
	 * or maybe use completely different keymap in classic rules
	 * so this comes into handy
	 */
	public int ctrlSchemeID;

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

	/** Disallow the use of IHS in zero delay */
	public boolean holdInitialDisallowZeroDelay;

	/** Reset the piece's direction when using hold */
	public boolean holdResetDirection;

	/** Number of holds (-1:Unlimited) */
	public int holdLimit;

	/** Enable Hard Drop */
	public boolean harddropEnable;

	/** Instant lock by Hard Drop */
	public boolean harddropLock;

	/** Disallow continuous use of Hard Drop */
	public boolean harddropLimit;

	/** Enable Soft Drop */
	public boolean softdropEnable;

	/** Instant lock by Soft Drop */
	public boolean softdropLock;

	/** Disallow continuous use of Soft Drop */
	public boolean softdropLimit;

	/** Multiply the native speed when using Soft Drop */
	public boolean softdropMultiplyNativeSpeed;

	/** Use new soft drop codes */
	public boolean softdropGravitySpeedLimit;

	/** Enable Initial Rotation */
	public boolean rotateInitial;

	/** Disallow continuous use of Initial Rotation */
	public boolean rotateInitialLimit;

	/** Disallow the use of Initial Rotation in zero delay */
	public boolean rotateInitialDisallowZeroDelay;

	/** Lock delay reset by falling */
	public boolean lockresetFall;

	/** Lock delay reset by left/right movement */
	public boolean lockresetMove;

	/** Lock delay reset by rotation */
	public boolean lockresetRotate;

	/** Lock delay reset on wallkick */
	public boolean lockresetWallkick;

	/** Lock delay reset limit for left/right movement (-1:Unlimited) */
	public int lockresetLimitMove;

	/** Lock delay reset limit for rotation (-1:Unlimited) */
	public int lockresetLimitRotate;

	/** Share lock reset counter (only use movement counter if true) */
	public boolean lockresetLimitShareCount;

	/** What should happen when movement/rotation counter exceeds */
	public int lockresetLimitOver;

	/** Count the movement/rotation even in mid-air */
	public boolean lockresetLimitCountAir;

	/** Reset the movement/rotation counter by using the deepest Y position the current piece has reached */
	public boolean lockresetLimitUseDeepestY;

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
		ctrlSchemeID = CTRL_SCHEME_STANDARD;

		pieceOffsetX = new int[PieceManager.PIECE_COUNT][Piece.DIRECTION_COUNT];
		pieceOffsetY = new int[PieceManager.PIECE_COUNT][Piece.DIRECTION_COUNT];
		pieceSpawnX = new int[PieceManager.PIECE_COUNT][Piece.DIRECTION_COUNT];
		pieceSpawnXBig = new int[PieceManager.PIECE_COUNT][Piece.DIRECTION_COUNT];
		pieceSpawnY = new int[PieceManager.PIECE_COUNT][Piece.DIRECTION_COUNT];
		pieceSpawnYBig = new int[PieceManager.PIECE_COUNT][Piece.DIRECTION_COUNT];

		pieceColor = new int[PieceManager.PIECE_COUNT];
		pieceColor[PieceManager.PIECE_I] = Block.BLOCK_COLOR_CYAN;
		pieceColor[PieceManager.PIECE_L] = Block.BLOCK_COLOR_ORANGE;
		pieceColor[PieceManager.PIECE_O] = Block.BLOCK_COLOR_YELLOW;
		pieceColor[PieceManager.PIECE_Z] = Block.BLOCK_COLOR_RED;
		pieceColor[PieceManager.PIECE_T] = Block.BLOCK_COLOR_PURPLE;
		pieceColor[PieceManager.PIECE_J] = Block.BLOCK_COLOR_BLUE;
		pieceColor[PieceManager.PIECE_S] = Block.BLOCK_COLOR_GREEN;
		pieceColor[PieceManager.PIECE_I1] = Block.BLOCK_COLOR_PURPLE;
		pieceColor[PieceManager.PIECE_I2] = Block.BLOCK_COLOR_BLUE;
		pieceColor[PieceManager.PIECE_I3] = Block.BLOCK_COLOR_GREEN;
		pieceColor[PieceManager.PIECE_L3] = Block.BLOCK_COLOR_ORANGE;

		pieceDefaultDirection = new int[PieceManager.PIECE_COUNT];
		pieceEnterAboveField = true;
		pieceEnterMaxDistanceY = 0;

		fieldCeiling = false;
		fieldLockoutDeath = true;
		fieldPartialLockoutDeath = false;

		nextDisplay = 6;

		holdEnable = true;
		holdInitial = true;
		holdInitialLimit = false;
		holdInitialDisallowZeroDelay = true;
		holdResetDirection = true;
		holdLimit = -1;

		harddropEnable = true;
		harddropLock = true;
		harddropLimit = true;

		softdropEnable = true;
		softdropLock = false;
		softdropLimit = false;
		softdropMultiplyNativeSpeed = false;
		softdropGravitySpeedLimit = true;

		rotateInitial = true;
		rotateInitialLimit = false;
		rotateInitialDisallowZeroDelay = true;

		lockresetFall = true;
		lockresetMove = true;
		lockresetRotate = true;
		lockresetWallkick = true;
		lockresetLimitMove = 15;
		lockresetLimitRotate = -1;
		lockresetLimitShareCount = true;
		lockresetLimitOver = LOCKRESET_LIMIT_OVER_INSTANT;
		lockresetLimitCountAir = true;
		lockresetLimitUseDeepestY = true;
	}

	/**
	 * Copy from another RuleOptions
	 * @param r Copy source
	 */
	public void copy(RuleOptions r) {
		style = r.style;
		wallkickID = r.wallkickID;
		randomizerID = r.randomizerID;
		ctrlSchemeID = r.ctrlSchemeID;

		for(int i = 0; i < PieceManager.PIECE_COUNT; i++) {
			for(int j = 0; j < Piece.DIRECTION_COUNT; j++) {
				pieceOffsetX[i][j] = r.pieceOffsetX[i][j];
				pieceOffsetY[i][j] = r.pieceOffsetY[i][j];
				pieceSpawnX[i][j] = r.pieceSpawnX[i][j];
				pieceSpawnXBig[i][j] = r.pieceSpawnXBig[i][j];
				pieceSpawnY[i][j] = r.pieceSpawnY[i][j];
				pieceSpawnYBig[i][j] = r.pieceSpawnYBig[i][j];
			}

			pieceColor[i] = r.pieceColor[i];
			pieceDefaultDirection[i] = r.pieceDefaultDirection[i];
		}

		pieceEnterAboveField = r.pieceEnterAboveField;
		pieceEnterMaxDistanceY = r.pieceEnterMaxDistanceY;

		fieldCeiling = r.fieldCeiling;
		fieldLockoutDeath = r.fieldLockoutDeath;
		fieldPartialLockoutDeath = r.fieldPartialLockoutDeath;

		nextDisplay = r.nextDisplay;

		holdEnable = r.holdEnable;
		holdInitial = r.holdInitial;
		holdInitialLimit = r.holdInitialLimit;
		holdInitialDisallowZeroDelay = r.holdInitialDisallowZeroDelay;
		holdResetDirection = r.holdResetDirection;
		holdLimit = r.holdLimit;

		harddropEnable = r.harddropEnable;
		harddropLock = r.harddropLock;
		harddropLimit = r.harddropLimit;

		softdropEnable = r.softdropEnable;
		softdropLock = r.softdropLock;
		softdropLimit = r.softdropLimit;
		softdropMultiplyNativeSpeed = r.softdropMultiplyNativeSpeed;
		softdropGravitySpeedLimit = r.softdropGravitySpeedLimit;

		rotateInitial = r.rotateInitial;
		rotateInitialLimit = r.rotateInitialLimit;
		rotateInitialDisallowZeroDelay = r.rotateInitialDisallowZeroDelay;

		lockresetFall = r.lockresetFall;
		lockresetMove = r.lockresetMove;
		lockresetRotate = r.lockresetRotate;
		lockresetWallkick = r.lockresetWallkick;
		lockresetLimitMove = r.lockresetLimitMove;
		lockresetLimitRotate = r.lockresetLimitRotate;
		lockresetLimitShareCount = r.lockresetLimitShareCount;
		lockresetLimitOver = r.lockresetLimitOver;
		lockresetLimitCountAir = r.lockresetLimitCountAir;
		lockresetLimitUseDeepestY = r.lockresetLimitUseDeepestY;
	}
}
