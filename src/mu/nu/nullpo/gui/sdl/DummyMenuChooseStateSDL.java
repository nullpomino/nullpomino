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
package mu.nu.nullpo.gui.sdl;

import sdljava.SDLException;
import sdljava.video.SDLSurface;

/**
 * Dummy class for menus where the player picks from a list of options
 */
public abstract class DummyMenuChooseStateSDL extends BaseStateSDL {
	/** Cursor position */
	protected int cursor = 0;

	/** Screenshot撮影 flag */
	protected boolean ssflag = false;

	/** Max cursor value */
	protected int maxCursor;

	/** Top choice's y-coordinate */
	protected int minChoiceY;

	/** Set to false to ignore mouse input */
	protected boolean mouseEnabled;

	public DummyMenuChooseStateSDL () {
		maxCursor = -1;
		minChoiceY = 3;
		mouseEnabled = true;
	}

	@Override
	public void render(SDLSurface screen) throws SDLException {
	}

	@Override
	public void update() throws SDLException
	{
		// Mouse
		boolean mouseConfirm = false;
		if (mouseEnabled)
			mouseConfirm = updateMouseInput();

		if (maxCursor >= 0) {

			// Cursor movement
			if(GameKeySDL.gamekey[0].isMenuRepeatKey(GameKeySDL.BUTTON_UP)) {
				cursor--;
				if(cursor < 0) cursor = maxCursor;
				ResourceHolderSDL.soundManager.play("cursor");
			}
			if(GameKeySDL.gamekey[0].isMenuRepeatKey(GameKeySDL.BUTTON_DOWN)) {
				cursor++;
				if(cursor > maxCursor) cursor = 0;
				ResourceHolderSDL.soundManager.play("cursor");
			}

			int change = 0;
			if(GameKeySDL.gamekey[0].isMenuRepeatKey(GameKeySDL.BUTTON_LEFT)) change = -1;
			if(GameKeySDL.gamekey[0].isMenuRepeatKey(GameKeySDL.BUTTON_RIGHT)) change = 1;

			if(change != 0)
				onChange(change);

			// 決定 button
			if(GameKeySDL.gamekey[0].isPushKey(GameKeySDL.BUTTON_A) || mouseConfirm) {
				if (onDecide())
					return;
			}

		}
		if(GameKeySDL.gamekey[0].isPushKey(GameKeySDL.BUTTON_D)) {
			if (onPushButtonD());
				return;
		}

		// Cancel button
		if(GameKeySDL.gamekey[0].isPushKey(GameKeySDL.BUTTON_B) || MouseInputSDL.mouseInput.isMouseRightClicked()) {
			if (onCancel());
				return;
		}
	}

	protected boolean updateMouseInput() throws SDLException
	{
		MouseInputSDL.mouseInput.update();
		if (MouseInputSDL.mouseInput.isMouseClicked())
		{
			int y = MouseInputSDL.mouseInput.getMouseY() >> 4;
			int newCursor = y - minChoiceY;
			if (newCursor >= 0 && newCursor <= maxCursor)
			{
				if (newCursor == cursor)
					return true;
				ResourceHolderSDL.soundManager.play("cursor");
				cursor = newCursor;
			}
		}
		return false;
	}

	protected void renderChoices(int x, String[] choices) throws SDLException
	{
		renderChoices(x, minChoiceY, choices);
	}

	protected void renderChoices(int x, int y, String[] choices) throws SDLException
	{
		NormalFontSDL.printFontGrid(x-1, y+cursor, "b", NormalFontSDL.COLOR_RED);
		for (int i = 0; i < choices.length; i++)
			NormalFontSDL.printFontGrid(x, y+i, choices[i], (cursor == i));
	}

	/**
	 * Called when left or right is pressed.
	 * @throws SDLException When something bad happens.
	 */
	protected void onChange(int change) throws SDLException {
	}

	/**
	 * Called on a decide operation (left click on highlighted entry or select button).
	 * @return True to skip all further update processing, false otherwise.
	 * @throws SDLException When something bad happens.
	 */
	protected boolean onDecide() throws SDLException {
		return false;
	}

	/**
	 * Called on a cancel operation (right click or cancel button).
	 * @return True to skip all further update processing, false otherwise.
	 * @throws SDLException When something bad happens.
	 */
	protected boolean onCancel() throws SDLException {
		return false;
	}

	/**
	 * Called when D button is pushed.
	 * Currently, this is the only one needed; methods for other buttons can be added if needed.
	 * @return True to skip all further update processing, false otherwise.
	 * @throws SDLException When something bad happens.
	 */
	protected boolean onPushButtonD() throws SDLException {
		return false;
	}
}
