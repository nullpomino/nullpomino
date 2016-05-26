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

import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.gui.net.UpdateChecker;
import sdljava.SDLException;
//import sdljava.event.MouseState;
//import sdljava.event.SDLEvent;
import sdljava.video.SDLSurface;
import sdljava.video.SDLVideo;

/**
 * Title screen state (SDL)
 */
public class StateTitleSDL extends DummyMenuChooseStateSDL {
	/** Strings for menu choices */
	private static final String[] CHOICES = {"START", "REPLAY", "NETPLAY", "OPTIONS", "EXIT"};

	/** UI Text identifier Strings */
	private static final String[] UI_TEXT = {
        "Title_Start", "Title_Replay", "Title_NetPlay", "Title_Config", "Title_Exit"
	};

	/** true when new version is already checked */
	protected boolean isNewVersionChecked = false;

	public StateTitleSDL () {
		maxCursor = 4;
		minChoiceY = 4;
	}

	/*
	 * Called when entering this state
	 */
	@Override
	public void enter() throws SDLException {
		// Update title bar
		SDLVideo.wmSetCaption("NullpoMino version" + GameManager.getVersionString(), null);
		// Observer start
		NullpoMinoSDL.startObserverClient();
		// Call GC
		System.gc();

		// New Version check
		if(!isNewVersionChecked && NullpoMinoSDL.propGlobal.getProperty("updatechecker.enable", true)) {
			isNewVersionChecked = true;

			int startupCount = NullpoMinoSDL.propGlobal.getProperty("updatechecker.startupCount", 0);
			int startupMax = NullpoMinoSDL.propGlobal.getProperty("updatechecker.startupMax", 20);

			if(startupCount >= startupMax) {
				String strURL = NullpoMinoSDL.propGlobal.getProperty("updatechecker.url", "");
				UpdateChecker.startCheckForUpdates(strURL);
				startupCount = 0;
			} else {
				startupCount++;
			}

			if(startupMax >= 1) {
				NullpoMinoSDL.propGlobal.setProperty("updatechecker.startupCount", startupCount);
				NullpoMinoSDL.saveConfig();
			}
		}
	}

	/*
	 * Draw the game screen
	 */
	@Override
	public void render(SDLSurface screen) throws SDLException {
		ResourceHolderSDL.imgTitle.blitSurface(screen);

		NormalFontSDL.printFontGrid(1, 1, "NULLPOMINO", NormalFontSDL.COLOR_ORANGE);
		NormalFontSDL.printFontGrid(1, 2, "VERSION " + GameManager.getVersionString(), NormalFontSDL.COLOR_ORANGE);

		NormalFontSDL.printFontGrid(1, 4 + cursor, "b", NormalFontSDL.COLOR_RED);

		renderChoices(2, 4, CHOICES);

		NormalFontSDL.printTTFFont(16, 432, NullpoMinoSDL.getUIText(UI_TEXT[cursor]));

		if(UpdateChecker.isNewVersionAvailable(GameManager.getVersionMajor(), GameManager.getVersionMinor())) {
			String strTemp = String.format(NullpoMinoSDL.getUIText("Title_NewVersion"),
					UpdateChecker.getLatestVersionFullString(), UpdateChecker.getStrReleaseDate());
			NormalFontSDL.printTTFFont(16, 416, strTemp);
		}
	}

	@Override
	protected boolean onDecide () throws SDLException {
		ResourceHolderSDL.soundManager.play("decide");

		switch(cursor) {
		case 0:
			StateSelectModeSDL.isTopLevel = true;
			NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_SELECTMODE);
			break;
		case 1:
			NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_REPLAYSELECT);
			break;
		case 2:
			NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_NETGAME);
			break;
		case 3:
			NullpoMinoSDL.enterState(NullpoMinoSDL.STATE_CONFIG_MAINMENU);
			break;
		case 4:
			NullpoMinoSDL.enterState(-1);
			break;
		}
		return false;
	}
}
