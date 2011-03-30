package mu.nu.nullpo.gui.common;

import org.apache.log4j.Logger;

public abstract class AbstractResourceHolder {
	
	/** Log */
	protected static Logger log;
	
	/** Backgroundのcount */
	public static final int BACKGROUND_MAX = 20;

	/** Number of images for block spatter animation during line clears */
	public static final int BLOCK_BREAK_MAX = 8;

	/** Number of image splits for block spatter animation during line clears */
	public static final int BLOCK_BREAK_SEGMENTS = 2;

	/** Number of gem block clear effects */
	public static final int PERASE_MAX = 7;
}
