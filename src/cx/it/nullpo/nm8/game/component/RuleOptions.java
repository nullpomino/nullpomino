package cx.it.nullpo.nm8.game.component;

import java.io.Serializable;

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
}
