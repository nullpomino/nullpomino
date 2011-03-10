package cx.it.nullpo.nm8.game.component;

import java.io.Serializable;

/**
 * Block
 */
public class Block implements Serializable {
	/** Serial version ID */
	private static final long serialVersionUID = -9058214832942751961L;

	/** Block color constants */
	public static final int BLOCK_COLOR_INVALID = -1,
							BLOCK_COLOR_NONE = 0,
							BLOCK_COLOR_GRAY = 1,
							BLOCK_COLOR_RED = 2,
							BLOCK_COLOR_ORANGE = 3,
							BLOCK_COLOR_YELLOW = 4,
							BLOCK_COLOR_GREEN = 5,
							BLOCK_COLOR_CYAN = 6,
							BLOCK_COLOR_BLUE = 7,
							BLOCK_COLOR_PURPLE = 8,
							BLOCK_COLOR_GEM_RED = 9,
							BLOCK_COLOR_GEM_ORANGE = 10,
							BLOCK_COLOR_GEM_YELLOW = 11,
							BLOCK_COLOR_GEM_GREEN = 12,
							BLOCK_COLOR_GEM_CYAN = 13,
							BLOCK_COLOR_GEM_BLUE = 14,
							BLOCK_COLOR_GEM_PURPLE = 15,
							BLOCK_COLOR_SQUARE_GOLD_1 = 16,
							BLOCK_COLOR_SQUARE_GOLD_2 = 17,
							BLOCK_COLOR_SQUARE_GOLD_3 = 18,
							BLOCK_COLOR_SQUARE_GOLD_4 = 19,
							BLOCK_COLOR_SQUARE_GOLD_5 = 20,
							BLOCK_COLOR_SQUARE_GOLD_6 = 21,
							BLOCK_COLOR_SQUARE_GOLD_7 = 22,
							BLOCK_COLOR_SQUARE_GOLD_8 = 23,
							BLOCK_COLOR_SQUARE_GOLD_9 = 24,
							BLOCK_COLOR_SQUARE_SILVER_1 = 25,
							BLOCK_COLOR_SQUARE_SILVER_2 = 26,
							BLOCK_COLOR_SQUARE_SILVER_3 = 27,
							BLOCK_COLOR_SQUARE_SILVER_4 = 28,
							BLOCK_COLOR_SQUARE_SILVER_5 = 29,
							BLOCK_COLOR_SQUARE_SILVER_6 = 30,
							BLOCK_COLOR_SQUARE_SILVER_7 = 31,
							BLOCK_COLOR_SQUARE_SILVER_8 = 32,
							BLOCK_COLOR_SQUARE_SILVER_9 = 33,
							BLOCK_COLOR_RAINBOW = 34,
							BLOCK_COLOR_GEM_RAINBOW = 35;

	/** Item constants */
	public static final int BLOCK_ITEM_NONE = 0,
							BLOCK_ITEM_RANDOM = 1;

	/** Number of items */
	public static final int MAX_ITEM = 1;

	/** Number of normal block colors */
	public static final int BLOCK_COLOR_COUNT = 9;

	/** Number of normal+gem block colors */
	public static final int BLOCK_COLOR_EXT_COUNT = 16;

	/** Block visible */
	public static final int BLOCK_ATTRIBUTE_VISIBLE = 1;

	/** Block outline visible */
	public static final int BLOCK_ATTRIBUTE_OUTLINE = 2;

	/** Bone block flag */
	public static final int BLOCK_ATTRIBUTE_BONE = 4;

	/** Up connection */
	public static final int BLOCK_ATTRIBUTE_CONNECT_UP = 8;

	/** Down connection */
	public static final int BLOCK_ATTRIBUTE_CONNECT_DOWN = 16;

	/** Left connection */
	public static final int BLOCK_ATTRIBUTE_CONNECT_LEFT = 32;

	/** Right connection */
	public static final int BLOCK_ATTRIBUTE_CONNECT_RIGHT = 64;

	/** Self-placed flag (not set if it's a preloaded block or a garbage) */
	public static final int BLOCK_ATTRIBUTE_SELFPLACED = 128;

	/** Broken flag */
	public static final int BLOCK_ATTRIBUTE_BROKEN = 256;

	/** Ojama block */
	public static final int BLOCK_ATTRIBUTE_GARBAGE = 512;

	/** Wall flag (not clearable) */
	public static final int BLOCK_ATTRIBUTE_WALL = 1024;

	/** Erase flag */
	public static final int BLOCK_ATTRIBUTE_ERASE = 2048;

	/** Temporary mark for block linking check algorithm */
	public static final int BLOCK_ATTRIBUTE_TEMP_MARK = 4096;

	/** "Block has fallen" flag for cascade gravity */
	public static final int BLOCK_ATTRIBUTE_CASCADE_FALL = 8192;

	/** Anti-gravity flag (The block will not fall by gravity) */
	public static final int BLOCK_ATTRIBUTE_ANTIGRAVITY = 16384;

	/** Last commit flag -- block was part of last placement or cascade **/
	public static final int BLOCK_ATTRIBUTE_LAST_COMMIT = 32768;

	/** Ignore block connections (for Avalanche modes) */
	public static final int BLOCK_ATTRIBUTE_IGNORE_BLOCKLINK = 65536;

	/** Block color */
	public int color;

	/** Block skin */
	public int skin;

	/** Block attributes */
	public int attribute;

	/** Elapsed time after locked */
	public int elapsedFrames;

	/** Block darkness (if minus, it will be lighter) */
	public float darkness;

	/** Alpha value (1.0f is complete visible, 0.0f is invisible) */
	public float alpha;

	/** Piece number (-1 if preloaded or garbage) */
	public int pieceNum;

	/** Item number */
	public int item;

	/** Number of extra clears required before block is erased */
	public int hard;

	/** Counter for blocks that count down before some effect occurs */
	public int countdown;

	/** Color to turn into when garbage block turns into a regular block */
	public int secondaryColor;

	/** Bonus value awarded when cleared */
	public int bonusValue;

	/** Color-shift phase for rainbow blocks */
	public static int rainbowPhase = 0;

	/**
	 * Constructor
	 */
	public Block() {
		reset();
	}

	/**
	 * Constructor
	 * @param color Block color
	 */
	public Block(int color) {
		reset();
		this.color = color;
	}

	/**
	 * Constructor
	 * @param color Block color
	 * @param skin Block skin
	 */
	public Block(int color, int skin) {
		reset();
		this.color = color;
		this.skin = skin;
	}

	/**
	 * Constructor
	 * @param color Block color
	 * @param skin Block skin
	 * @param attribute Block attributes
	 */
	public Block(int color, int skin, int attribute) {
		reset();
		this.color = color;
		this.skin = skin;
		this.attribute = attribute;
	}

	/**
	 * Copy constructor
	 * @param b Copy source
	 */
	public Block(Block b) {
		copy(b);
	}

	/**
	 * Reset to defaults
	 */
	public void reset() {
		color = BLOCK_COLOR_NONE;
		skin = 0;
		attribute = 0;
		elapsedFrames = 0;
		darkness = 0f;
		alpha = 1f;
		pieceNum = -1;
		item = 0;
		hard = 0;
		countdown = 0;
		secondaryColor = 0;
		bonusValue = 0;
	}

	/**
	 * Copy from other instance of Block
	 * @param b Copy source
	 */
	public void copy(Block b) {
		color = b.color;
		skin = b.skin;
		attribute = b.attribute;
		elapsedFrames = b.elapsedFrames;
		darkness = b.darkness;
		alpha = b.alpha;
		pieceNum = b.pieceNum;
		item = b.item;
		hard = b.hard;
		countdown = b.countdown;
		secondaryColor = b.secondaryColor;
		bonusValue = b.bonusValue;
	}

	/**
	 * Get the status of specific attribute
	 * @param attr Attribute
	 * @return true if the attribute is set
	 */
	public boolean getAttribute(int attr) {
		return ((attribute & attr) != 0);
	}

	/**
	 * Set the status of specific attribute
	 * @param attr Attribute
	 * @param status true to set, false to unset
	 */
	public void setAttribute(int attr, boolean status) {
		if(status) attribute |= attr;
		else attribute &= ~attr;
	}

	/**
	 * Checks to see if <code>this</code> is a empty space
	 * @return <code>true</code> if the block is a empty space
	 */
	public boolean isEmpty() {
		return (color < BLOCK_COLOR_GRAY);
	}

	/**
	 * Checks to see if <code>this</code> is a gem block
	 * @return <code>true</code> if the block is a gem block
	 */
	public boolean isGemBlock() {
		return ((color >= BLOCK_COLOR_GEM_RED) && (color <= BLOCK_COLOR_GEM_PURPLE)) ||
				(color == BLOCK_COLOR_GEM_RAINBOW);
	}

	/**
	 * Checks to see if <code>this</code> is a gold square block
	 * @return <code>true</code> if the block is a gold square block
	 */
	public boolean isGoldSquareBlock() {
		return (color >= BLOCK_COLOR_SQUARE_GOLD_1) && (color <= BLOCK_COLOR_SQUARE_GOLD_9);
	}

	/**
	 * Checks to see if <code>this</code> is a silver square block
	 * @return <code>true</code> if the block is a silver square block
	 */
	public boolean isSilverSquareBlock() {
		return (color >= BLOCK_COLOR_SQUARE_SILVER_1) && (color <= BLOCK_COLOR_SQUARE_SILVER_9);
	}

	/**
	 * Checks to see if <code>this</code> is a normal block (gray to purple)
	 * @return <code>true</code> if the block is a normal block
	 */
	public boolean isNormalBlock() {
		return (color >= BLOCK_COLOR_GRAY) && (color <= BLOCK_COLOR_PURPLE);
	}

	/**
	 * @return The draw color of this block (for rainbow block)
	 */
	public int getDrawColor() {
		if (color == BLOCK_COLOR_GEM_RAINBOW)
			return BLOCK_COLOR_GEM_RED + (rainbowPhase/3);
		else if (color == BLOCK_COLOR_RAINBOW)
			return BLOCK_COLOR_RED + (rainbowPhase/3);
		else
			return color;
	}

	/**
	 * @return the character representing the color of this block
	 */
	public char blockToChar(){
		//'0'-'9','A'-'Z' represent colors 0-35.
		//Colors beyond that would follow the ASCII table starting at '['.
		if(color >= 10) {
			return (char)('A' + (color - 10));
		}
		return (char)('0' + Math.max(0, color));
	}

	/*
	 * Get the character representing the color of this block
	 */
	@Override
	public String toString(){
		return ""+blockToChar();
	}

	/**
	 * @param c A character representing a block
	 * @return The int representing the block's color
	 */
	public static int charToBlockColor(char c){
		int blkColor = 0;

		//With a radix of 36, the digits encompass '0'-'9','A'-'Z'.
		//With a radix higher than 36, we can also have characters 'a'-'z' represent digits.
		blkColor = Character.digit(c, 36);

		//Given the current implementation of other functions, I assumed that
		//if we needed additional BLOCK_COLOR values, it would follow from 'Z'->'['
		//in the ASCII chart.
		if(blkColor == -1) {
			blkColor = (c - '[') + 36;
		}
		return blkColor;
	}

	/**
	 * Convert gem color to normal color
	 * @param color Color to convert from
	 * @return Normal color
	 */
	public static int gemToNormalColor(int color) {
		if ((color >= BLOCK_COLOR_GEM_RED) && (color <= BLOCK_COLOR_GEM_PURPLE))
			return color - 7;
		else if (color == BLOCK_COLOR_GEM_RAINBOW)
			return BLOCK_COLOR_RAINBOW;
		else
			return color;
	}

	public static void updateRainbowPhase(int time) {
		rainbowPhase = time%350;
	}

}
