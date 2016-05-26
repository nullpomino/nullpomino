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
package mu.nu.nullpo.gui.slick;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Dummy class for menus where the player picks from a list of options
 */
public abstract class DummyMenuChooseState extends BaseGameState {
	/** Cursor position */
	protected int cursor = 0;

	/** Max cursor value */
	protected int maxCursor;

	/** Top choice's y-coordinate */
	protected int minChoiceY;

	/** Set to false to ignore mouse input */
	protected boolean mouseEnabled;

	public DummyMenuChooseState () {
		maxCursor = -1;
		minChoiceY = 3;
		mouseEnabled = true;
	}

	@Override
	protected void updateImpl(GameContainer container, StateBasedGame game, int delta) throws SlickException
	{
		// TTF font load
		if(ResourceHolderSlick.ttfFont != null) ResourceHolderSlick.ttfFont.loadGlyphs();

		// Update key input states
		GameKeySlick.gamekey[0].update(container.getInput());

		// Mouse
		boolean mouseConfirm = false;
		if (mouseEnabled)
			mouseConfirm = updateMouseInput(container.getInput());

		if (maxCursor >= 0) {
			// Cursor movement
			if(GameKeySlick.gamekey[0].isMenuRepeatKey(GameKeySlick.BUTTON_UP)) {
				cursor--;
				if(cursor < 0) cursor = maxCursor;
				ResourceHolderSlick.soundManager.play("cursor");
			}
			if(GameKeySlick.gamekey[0].isMenuRepeatKey(GameKeySlick.BUTTON_DOWN)) {
			    cursor++;
				if(cursor > maxCursor) cursor = 0;
				ResourceHolderSlick.soundManager.play("cursor");
			}

			int change = 0;
			if(GameKeySlick.gamekey[0].isMenuRepeatKey(GameKeySlick.BUTTON_LEFT)) change = -1;
			if(GameKeySlick.gamekey[0].isMenuRepeatKey(GameKeySlick.BUTTON_RIGHT)) change = 1;

			if(change != 0)
				onChange(container, game, delta, change);

			// Confirm button
			if(GameKeySlick.gamekey[0].isPushKey(GameKeySlick.BUTTON_A) || mouseConfirm) {
				if (onDecide(container, game, delta))
					return;
			}

		}
		if(GameKeySlick.gamekey[0].isPushKey(GameKeySlick.BUTTON_D)) {
			if (onPushButtonD(container, game, delta));
				return;
		}

		// Cancel button
		if(GameKeySlick.gamekey[0].isPushKey(GameKeySlick.BUTTON_B) || MouseInputSlick.mouseInput.isMouseRightClicked()) {
			if (onCancel(container, game, delta));
				return;
		}
	}

	protected boolean updateMouseInput(Input input)
	{
		MouseInputSlick.mouseInput.update(input);
		if (MouseInputSlick.mouseInput.isMouseClicked())
		{
			int y = MouseInputSlick.mouseInput.getMouseY() >> 4;
			int newCursor = y - minChoiceY;
			if (newCursor >= 0 && newCursor <= maxCursor)
			{
				if (newCursor == cursor)
					return true;
				ResourceHolderSlick.soundManager.play("cursor");
				cursor = newCursor;
			}
		}
		return false;
	}

	protected void renderChoices(int x, String[] choices)
	{
		renderChoices(x, minChoiceY, choices);
	}

	protected void renderChoices(int x, int y, String[] choices)
	{
		NormalFontSlick.printFontGrid(x-1, y+cursor, "b", NormalFontSlick.COLOR_RED);
		for (int i = 0; i < choices.length; i++)
			NormalFontSlick.printFontGrid(x, y+i, choices[i], (cursor == i));
	}

	/**
	 * Called when left or right is pressed.
	 */
	protected void onChange(GameContainer container, StateBasedGame game, int delta, int change) {
	}

	/**
	 * Called on a decide operation (left click on highlighted entry or select button).
	 * @return True to skip all further update processing, false otherwise.
	 */
	protected boolean onDecide(GameContainer container, StateBasedGame game, int delta) {
		return false;
	}

	/**
	 * Called on a cancel operation (right click or cancel button).
	 * @return True to skip all further update processing, false otherwise.
	 */
	protected boolean onCancel(GameContainer container, StateBasedGame game, int delta) {
		return false;
	}

	/**
	 * Called when D button is pushed.
	 * Currently, this is the only one needed; methods for other buttons can be added if needed.
	 * @return True to skip all further update processing, false otherwise.
	 */
	protected boolean onPushButtonD(GameContainer container, StateBasedGame game, int delta) {
		return false;
	}
}
