package mu.nu.nullpo.gui.slick;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Dummy class for menus with a scroll bar
 */
public abstract class DummyMenuScrollState extends DummyMenuChooseState {
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

	/** ID number of file at top of currently displayed section */
	protected int minentry;

	/** Maximum number of entries to display at a time */
	protected int pageHeight;

	/** List of entries */
	protected String[] list;

	/** Error messages for null or empty list */
	protected String nullError, emptyError;

	/** Y-coordinates of dark sections of scroll bar */
	protected int pUpMinY, pUpMaxY, pDownMinY, pDownMaxY;

	public DummyMenuScrollState () {
		minentry = 0;
		nullError = "";
		emptyError = "";
		pDownMinY = 0;
		pDownMaxY = 0;
		pUpMinY = 0;
		pUpMaxY = 0;
	}

	/*
	 * Draw the screen
	 */
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics graphics) throws SlickException {
		// Background
		graphics.drawImage(ResourceHolder.imgMenu, 0, 0);

		// Menu
		if(list == null) {
			NormalFont.printFontGrid(1, 1, nullError, NormalFont.COLOR_RED);
		} else if(list.length == 0) {
			NormalFont.printFontGrid(1, 1, emptyError, NormalFont.COLOR_RED);
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
			drawMenuList(graphics);
			onRenderSuccess(container, game, graphics);
		}

		super.render(container, game, graphics);
	}

	protected void onRenderSuccess (GameContainer container, StateBasedGame game, Graphics graphics)  {
	}

	@Override
	public boolean updateMouseInput (Input input) {
		// Mouse
		MouseInput.mouseInput.update(input);
		boolean clicked = MouseInput.mouseInput.isMouseClicked();
		int x = MouseInput.mouseInput.getMouseX() >> 4;
		int y = MouseInput.mouseInput.getMouseY() >> 4;
		if (x == SB_TEXT_X && (clicked || MouseInput.mouseInput.isMenuRepeatLeft()) && y >= 3 && y <= 2 + pageHeight)
		{
			int maxentry = minentry + pageHeight - 1;
			if (y == 3 && minentry > 0)
			{
				ResourceHolder.soundManager.play("cursor");
				//Scroll up
				minentry--;
				maxentry--;
				if (cursor > maxentry)
					cursor = maxentry;
			}
			else if (y == 2 + pageHeight && maxentry < list.length-1)
			{
				ResourceHolder.soundManager.play("cursor");
				//Down arrow
				minentry++;
				if (cursor < minentry)
					cursor = minentry;
			}
			else if (y > 3 && y < 2 + pageHeight)
			{
				int pixelY = MouseInput.mouseInput.getMouseY();
				if (pixelY >= pUpMinY && pixelY < pUpMaxY)
					pageUp();
				else if (pixelY >= pDownMinY && pixelY < pDownMaxY)
					pageDown();
			}
		}
		else if (clicked && x < SB_TEXT_X-1 && y >= 3 && y <= 2 + pageHeight)
		{
			int newCursor = y - 3 + minentry;
			if (newCursor == cursor)
				return true;
			else if (newCursor >= list.length)
				return false;
			else
			{
				ResourceHolder.soundManager.play("cursor");
				cursor = newCursor;
			}
		}
		return false;
	}

	public void drawMenuList (Graphics graphics)
	{
		int maxentry = minentry + pageHeight - 1;
		if (maxentry >= list.length)
			maxentry = list.length-1;

		for(int i = minentry, y = 0; i <= maxentry; i++, y++) {
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
		int fillMinY = ((insideHeight*minentry)/list.length);
		int fillHeight = (((maxentry-minentry+1)*insideHeight+list.length-1)/list.length);
		if (fillHeight < LINE_WIDTH)
		{
			fillHeight = LINE_WIDTH;
			fillMinY = (((insideHeight-fillHeight+1)*minentry)/(list.length-pageHeight+1));
		}
		graphics.setColor(SB_BACK_COLOR);
		graphics.fillRect(SB_MIN_X+LINE_WIDTH, SB_MIN_Y+LINE_WIDTH, insideWidth, insideHeight);
		graphics.setColor(SB_FILL_COLOR);
		graphics.fillRect(SB_MIN_X+LINE_WIDTH, SB_MIN_Y+LINE_WIDTH+fillMinY, insideWidth, fillHeight);
		graphics.setColor(Color.white);

		//Update coordinates
		pUpMinY = SB_MIN_Y+LINE_WIDTH;
		pUpMaxY = pUpMinY+fillMinY;
		pDownMinY = pUpMaxY+fillHeight;
		pDownMaxY = SB_MIN_Y+LINE_WIDTH+insideHeight;
	}

	@Override
	protected void onChange(GameContainer container, StateBasedGame game, int delta, int change) {
		if (change == 1) pageDown();
		else if (change == -1) pageUp();
	}

	protected void pageDown() {
		ResourceHolder.soundManager.play("cursor");
		int max = maxCursor - pageHeight + 1;
		if (minentry >= max)
			cursor = maxCursor;
		else
		{
			cursor += pageHeight;
			minentry += pageHeight;
			if (minentry > max)
			{
				cursor -= (minentry - max);
				minentry = max;
			}
		}
	}

	protected void pageUp() {
		ResourceHolder.soundManager.play("cursor");
		if (minentry == 0)
			cursor = 0;
		else
		{
			cursor -= pageHeight;
			minentry -= pageHeight;
			if (minentry < 0)
			{
				cursor -= minentry;
				minentry = 0;
			}
		}
	}
}
