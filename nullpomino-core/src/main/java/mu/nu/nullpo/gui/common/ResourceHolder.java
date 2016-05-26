package mu.nu.nullpo.gui.common;

import java.util.List;

public abstract class ResourceHolder {

	/** Backgroundのcount */
	public static final int BACKGROUND_MAX = 20;

	/** Number of images for block spatter animation during line clears */
	public static final int BLOCK_BREAK_MAX = 8;

	/** Number of image splits for block spatter animation during line clears */
	public static final int BLOCK_BREAK_SEGMENTS = 2;

	/** Number of gem block clear effects */
	public static final int PERASE_MAX = 7;
	
	/** Block sticky flag */
	public static List<Boolean> blockStickyFlagList;
	
	public abstract AbstractImage getImgNormalBlock(int skin);
	public abstract AbstractImage getImgSmallBlock(int skin);
	public abstract AbstractImage getImgBigBlock(int skin);
	public abstract int getImgBlockListSize();
	
	public boolean getBlockIsSticky(int skin) {
		return skin >= 0 && skin < getImgBlockListSize() && blockStickyFlagList.get(skin);
	}
}
