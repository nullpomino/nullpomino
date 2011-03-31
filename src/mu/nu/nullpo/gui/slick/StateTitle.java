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

import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.gui.net.UpdateChecker;

import org.apache.log4j.Logger;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Title screen state
 */
public class StateTitle extends DummyMenuChooseState {
	/** This state's ID */
	public static final int ID = 1;

	/** Strings for menu choices */
	private static final String[] CHOICES = {"START", "REPLAY", "NETPLAY", "OPTIONS", "EXIT"};

	/** UI Text identifier Strings */
	private static final String[] UI_TEXT = {
        "Title_Start", "Title_Replay", "Title_NetPlay", "Title_Config", "Title_Exit"
	};

	/** Log */
	static Logger log = Logger.getLogger(StateTitle.class);

	/** true when new version is already checked */
	protected boolean isNewVersionChecked = false;

	public StateTitle () {
		maxCursor = 4;
		minChoiceY = 4;
	}

	/*
	 * Fetch this state's ID
	 */
	@Override
	public int getID() {
		return ID;
	}

	/*
	 * State initialization
	 */
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
	}

	/*
	 * Called when entering this state
	 */
	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		// Observer start
		NullpoMinoSlick.startObserverClient();
		// Call GC
		System.gc();

		// Update title bar
		if(container instanceof AppGameContainer) {
			((AppGameContainer) container).setTitle("NullpoMino version" + GameManager.getVersionString());
			((AppGameContainer) container).setUpdateOnlyWhenVisible(true);
		}

		// New Version check
		if(!isNewVersionChecked && NullpoMinoSlick.propGlobal.getProperty("updatechecker.enable", true)) {
			isNewVersionChecked = true;

			int startupCount = NullpoMinoSlick.propGlobal.getProperty("updatechecker.startupCount", 0);
			int startupMax = NullpoMinoSlick.propGlobal.getProperty("updatechecker.startupMax", 20);

			if(startupCount >= startupMax) {
				String strURL = NullpoMinoSlick.propGlobal.getProperty("updatechecker.url", "");
				UpdateChecker.startCheckForUpdates(strURL);
				startupCount = 0;
			} else {
				startupCount++;
			}

			if(startupMax >= 1) {
				NullpoMinoSlick.propGlobal.setProperty("updatechecker.startupCount", startupCount);
				NullpoMinoSlick.saveConfig();
			}
		}
	}

	/*
	 * Draw the screen
	 */
	@Override
	protected void renderImpl(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		// Background
		g.drawImage(ResourceHolderSlick.imgTitle, 0, 0);

		// Menu
		NormalFontSlick.printFontGrid(1, 1, "NULLPOMINO", NormalFontSlick.COLOR_ORANGE);
		NormalFontSlick.printFontGrid(1, 2, "VERSION " + GameManager.getVersionString(), NormalFontSlick.COLOR_ORANGE);

		renderChoices(2, 4, CHOICES);

		NormalFontSlick.printTTFFont(16, 432, NullpoMinoSlick.getUIText(UI_TEXT[cursor]));

		if(UpdateChecker.isNewVersionAvailable(GameManager.getVersionMajor(), GameManager.getVersionMinor())) {
			String strTemp = String.format(NullpoMinoSlick.getUIText("Title_NewVersion"),
					UpdateChecker.getLatestVersionFullString(), UpdateChecker.getStrReleaseDate());
			NormalFontSlick.printTTFFont(16, 416, strTemp);
		}
	}

	@Override
	protected boolean onDecide(GameContainer container, StateBasedGame game, int delta) {
		ResourceHolderSlick.soundManager.play("decide");

		switch(cursor) {
		case 0:
			StateSelectMode.isTopLevel = true;
			game.enterState(StateSelectMode.ID);
			break;
		case 1:
			game.enterState(StateReplaySelect.ID);
			break;
		case 2:
			game.enterState(StateNetGame.ID);
			break;
		case 3:
			game.enterState(StateConfigMainMenu.ID);
			break;
		case 4:
			container.exit();
			break;
		}

		return false;
	}
}
