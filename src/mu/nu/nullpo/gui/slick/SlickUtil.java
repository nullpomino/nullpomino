package mu.nu.nullpo.gui.slick;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class SlickUtil {
	/** Scroll bar attributes */
	protected static final int SB_TEXT_X = 38,
		SB_TEXT_COLOR = NormalFont.COLOR_BLUE,
		SB_MIN_X = SB_TEXT_X << 4,
		SB_MIN_Y = 65,
		LINE_WIDTH = 2,
		SB_WIDTH = 14;
	
	/** Scroll bar colors */
	protected static final Color SB_SHADOW_COLOR = new Color(12, 78, 156),
		SB_BORDER_COLOR = new Color(52, 150, 252),
		SB_FILL_COLOR = Color.white,
		SB_BACK_COLOR = Color.black;
	
	public static void drawMenuList (Graphics graphics, int pageHeight, String[] list,
			int cursor, int topEntry, int bottomEntry)
	{
		if (bottomEntry >= list.length)
			bottomEntry = list.length-1;

		for(int i = topEntry, y = 0; i <= bottomEntry; i++, y++) {
			if(i < list.length) {
				NormalFont.printFontGrid(2, 3 + y, list[i].toUpperCase(), (cursor == i));
				if(cursor == i) NormalFont.printFontGrid(1, 3 + y, "b", NormalFont.COLOR_RED);
			}
		}

		int sbHeight = 16*(pageHeight - 2) - (LINE_WIDTH << 1);
		//Draw scroll bar
		NormalFont.printFontGrid(SB_TEXT_X, 3, "k", SB_TEXT_COLOR);
		NormalFont.printFontGrid(SB_TEXT_X, 2 + pageHeight, "n", SB_TEXT_COLOR);
		//Draw shadow
		graphics.setColor(SB_SHADOW_COLOR);
		graphics.fillRect(SB_MIN_X+SB_WIDTH, SB_MIN_Y+LINE_WIDTH, LINE_WIDTH, sbHeight);
		graphics.fillRect(SB_MIN_X+LINE_WIDTH, SB_MIN_Y+sbHeight, SB_WIDTH, LINE_WIDTH);
		//Draw border
		graphics.setColor(SB_BORDER_COLOR);
		graphics.fillRect(SB_MIN_X, SB_MIN_Y, SB_WIDTH, sbHeight);
		//Draw inside
		int insideHeight = sbHeight-(LINE_WIDTH << 1);
		int insideWidth = SB_WIDTH-(LINE_WIDTH << 1);
		int fillMinY = ((insideHeight*topEntry)/list.length);
		int fillHeight = (((bottomEntry-topEntry+1)*insideHeight+list.length-1)/list.length);
		if (fillHeight < LINE_WIDTH)
		{
			fillHeight = LINE_WIDTH;
			fillMinY = (((insideHeight-fillHeight+1)*topEntry)/(list.length-pageHeight+1));
		}
		graphics.setColor(SB_BACK_COLOR);
		graphics.fillRect(SB_MIN_X+LINE_WIDTH, SB_MIN_Y+LINE_WIDTH, insideWidth, insideHeight);
		graphics.setColor(SB_FILL_COLOR);
		graphics.fillRect(SB_MIN_X+LINE_WIDTH, SB_MIN_Y+LINE_WIDTH+fillMinY, insideWidth, fillHeight);
		graphics.setColor(Color.white);
	}
}
