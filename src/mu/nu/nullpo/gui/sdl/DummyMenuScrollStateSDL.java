package mu.nu.nullpo.gui.sdl;

import sdljava.SDLException;
import sdljava.video.SDLRect;
import sdljava.video.SDLSurface;
import sdljavax.gfx.SDLGfx;

/**
 * Dummy class for menus with a scroll bar
 */
public abstract class DummyMenuScrollStateSDL extends DummyMenuChooseStateSDL {
	/** Scroll bar attributes */
	protected static final int SB_TEXT_X = 38,
		SB_TEXT_COLOR = NormalFontSDL.COLOR_BLUE,
		SB_MIN_X = SB_TEXT_X << 4,
		SB_MIN_Y = 65,
		LINE_WIDTH = 2,
		SB_WIDTH = 14;
	
	/** Scroll bar colors */
	protected static final int SB_SHADOW_COLOR = 0x0C4E9C,
		SB_BORDER_COLOR = 0x3496FC,
		SB_FILL_COLOR = 0xFFFFFF,
		SB_BACK_COLOR = 0;

	/** ID number of file at top of currently displayed section */
	protected int minentry;

	/** Maximum number of entries to display at a time */
	protected int pageHeight;

	/** List of entries */
	protected String[] list;
	
	/** Error messages for null or empty list */
	protected String nullError, emptyError;
	
	public DummyMenuScrollStateSDL () {
		minentry = 0;
		nullError = "";
		emptyError = "";
	}

	/*
	 * 画面描画
	 */
	@Override
	public void render(SDLSurface screen) throws SDLException {
		// 背景
		ResourceHolderSDL.imgMenu.blitSurface(screen);

		// Menu
		if(list == null) {
			NormalFontSDL.printFontGrid(1, 1, nullError, NormalFontSDL.COLOR_RED);
		} else if(list.length == 0) {
			NormalFontSDL.printFontGrid(1, 1, emptyError, NormalFontSDL.COLOR_RED);
		} else {
			if (cursor >= list.length)
				cursor = 0;
			if (cursor < minentry)
				minentry = cursor;
			int maxentry = minentry + pageHeight - 1;
			if (cursor >= maxentry)
			{
				maxentry = cursor;
				minentry = maxentry - pageHeight + 1;
			}
			drawMenuList(screen);
			onRenderSuccess(screen);
		}

		super.render(screen);
	}

	protected void onRenderSuccess (SDLSurface screen) throws SDLException {
	}
	
	@Override
	public boolean updateMouseInput () throws SDLException {
		// Mouse
		MouseInputSDL.mouseInput.update();
		boolean clicked = MouseInputSDL.mouseInput.isMouseClicked();
		int x = MouseInputSDL.mouseInput.getMouseX() >> 4;
		int y = MouseInputSDL.mouseInput.getMouseY() >> 4;
		if (x == SB_TEXT_X && (clicked || MouseInputSDL.mouseInput.isMenuRepeatLeft()))
		{
			int maxentry = minentry + pageHeight - 1;
			if (y == 3 && minentry > 0)
			{
				//Scroll up
				minentry--;
				maxentry--;
				if (cursor > maxentry)
					cursor = maxentry;
			}
			else if (y == 2 + pageHeight && maxentry < list.length-1)
			{
				//Down arrow
				minentry++;
				if (cursor < minentry)
					cursor = minentry;
			}
		}
		else if (clicked && x < SB_TEXT_X-1 && y >= 3 && y <= 2 + pageHeight)
		{
			int newCursor = y - 3 + minentry;
			if (newCursor == cursor)
				return true;
			else
			{
				ResourceHolderSDL.soundManager.play("cursor");
				cursor = newCursor;
			}
		}
		return false;
	}
	
	public void drawMenuList (SDLSurface screen) throws SDLException
	{
		int maxentry = minentry + pageHeight - 1;
		if (maxentry >= list.length)
			maxentry = list.length-1;

		for(int i = minentry, y = 0; i <= maxentry; i++, y++) {
			if(i < list.length) {
				NormalFontSDL.printFontGrid(2, 3 + y, list[i].toUpperCase(), (cursor == i));
				if(cursor == i) NormalFontSDL.printFontGrid(1, 3 + y, "b", NormalFontSDL.COLOR_RED);
			}
		}

		//Draw scroll bar
		NormalFontSDL.printFontGrid(SB_TEXT_X, 3, "k", SB_TEXT_COLOR);
		NormalFontSDL.printFontGrid(SB_TEXT_X, 2 + pageHeight, "n", SB_TEXT_COLOR);

		int sbHeight = 16*(pageHeight - 2) - (LINE_WIDTH << 1);
		//Draw scroll bar
		NormalFontSDL.printFontGrid(SB_TEXT_X, 3, "k", SB_TEXT_COLOR);
		NormalFontSDL.printFontGrid(SB_TEXT_X, 2 + pageHeight, "n", SB_TEXT_COLOR);
		//Draw shadow
		screen.fillRect(new SDLRect(SB_MIN_X+SB_WIDTH, SB_MIN_Y+LINE_WIDTH, LINE_WIDTH, sbHeight), SB_SHADOW_COLOR);
		screen.fillRect(new SDLRect(SB_MIN_X+LINE_WIDTH, SB_MIN_Y+sbHeight, SB_WIDTH, LINE_WIDTH), SB_SHADOW_COLOR);
		//Draw border
		screen.fillRect(new SDLRect(SB_MIN_X, SB_MIN_Y, SB_WIDTH, sbHeight), SB_BORDER_COLOR);
		//Draw inside
		int insideHeight = sbHeight-(LINE_WIDTH << 1);
		int insideWidth = SB_WIDTH-(LINE_WIDTH << 1);
		int fillMinY = ((insideHeight*minentry)/list.length);
		int fillHeight = (((maxentry-minentry+1)*insideHeight+list.length-1)/list.length);
		if (fillHeight < LINE_WIDTH)
		{
			fillHeight = LINE_WIDTH;
			fillMinY = (((insideHeight-fillHeight+1)*minentry)/(list.length-pageHeight+1));
		}
		screen.fillRect(new SDLRect(SB_MIN_X+LINE_WIDTH, SB_MIN_Y+LINE_WIDTH, insideWidth, insideHeight), SB_BACK_COLOR);
		screen.fillRect(new SDLRect(SB_MIN_X+LINE_WIDTH, SB_MIN_Y+LINE_WIDTH+fillMinY, insideWidth, fillHeight), SB_FILL_COLOR);
	}
}
