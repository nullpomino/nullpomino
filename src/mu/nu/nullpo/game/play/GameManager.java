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
package mu.nu.nullpo.game.play;

import mu.nu.nullpo.game.component.BGMStatus;
import mu.nu.nullpo.game.component.BackgroundStatus;
import mu.nu.nullpo.game.event.EventReceiver;
import mu.nu.nullpo.game.subsystem.mode.GameMode;
import mu.nu.nullpo.util.CustomProperties;

import org.apache.log4j.Logger;

/**
 * GameManager: The container of the game
 */
public class GameManager {
	/** Log (Apache log4j) */
	static Logger log = Logger.getLogger(GameManager.class);

	/** Major version */
	public static final float VERSION_MAJOR = 7.4f;

	/** Minor version */
	public static final int VERSION_MINOR = 1;

	/** Game Mode */
	public GameMode mode;

	/** Properties used by game mode */
	public CustomProperties modeConfig;

	/** Properties for replay file */
	public CustomProperties replayProp;

	/** true if replay mode */
	public boolean replayMode;

	/** true if replay rerecording */
	public boolean replayRerecord;

	/** true if display menus only (No game screens) */
	public boolean menuOnly;

	/** EventReceiver: Manages various events, and renders everything to the screen */
	public EventReceiver receiver;

	/** BGMStatus: Manages the status of background music */
	public BGMStatus bgmStatus;

	/** BackgroundStatus: Manages the status of background image */
	public BackgroundStatus backgroundStatus;

	/** GameEngine: This is where the most action takes place */
	public GameEngine[] engine;

	/** true to show invisible blocks in replay */
	public boolean replayShowInvisible;

	/**
	 * Get major version
	 * @return Major version
	 */
	public static float getVersionMajor() {
		return VERSION_MAJOR;
	}

	/**
	 * Get minor version
	 * @return Minor version
	 */
	public static int getVersionMinor() {
		return VERSION_MINOR;
	}

	/**
	 * Get minor version (For compatibility with old replays)
	 * @return Minor version
	 */
	public static float getVersionMinorOld() {
		return VERSION_MINOR;
	}

	/**
	 * Get version information as String
	 * @return Version information
	 */
	public static String getVersionString() {
		return VERSION_MAJOR + "." + VERSION_MINOR;
	}

	/**
	 * Default constructor
	 */
	public GameManager() {
		log.debug("GameManager constructor called");
	}

	/**
	 * Normal constructor
	 * @param receiver EventReceiver
	 */
	public GameManager(EventReceiver receiver) {
		this();
		this.receiver = receiver;
	}

	/**
	 * Initialize the game
	 */
	public void init() {
		log.debug("GameManager init()");

		if(receiver == null) receiver = new EventReceiver();

		modeConfig = receiver.loadModeConfig();
		if(modeConfig == null) modeConfig = new CustomProperties();

		if(replayProp == null) {
			replayProp = new CustomProperties();
			replayMode = false;
		}

		replayRerecord = false;
		menuOnly = false;

		bgmStatus = new BGMStatus();
		backgroundStatus = new BackgroundStatus();

		int players = 1;
		if(mode != null) {
			mode.modeInit(this);
			players = mode.getPlayers();
		}
		engine = new GameEngine[players];
		for(int i = 0; i < engine.length; i++) engine[i] = new GameEngine(this, i);
	}

	/**
	 * Reset the game
	 */
	public void reset() {
		log.debug("GameManager reset()");

		menuOnly = false;
		bgmStatus.reset();
		backgroundStatus.reset();
		if(!replayMode) replayProp = new CustomProperties();
		for(int i = 0; i < engine.length; i++) engine[i].init();
	}

	/**
	 * Shutdown the game
	 */
	public void shutdown() {
		log.debug("GameManager shutdown()");

		try {
			for(int i = 0; i < engine.length; i++) {
				engine[i].shutdown();
				engine[i] = null;
			}
			engine = null;
			mode = null;
			modeConfig = null;
			replayProp = null;
			receiver = null;
			bgmStatus = null;
			backgroundStatus = null;
		} catch (Throwable e) {
			log.debug("Caught Throwable on shutdown", e);
		}
	}

	/**
	 * Get number of players
	 * @return Number of players
	 */
	public int getPlayers() {
		return engine.length;
	}

	/**
	 * Check if quit flag is true in any GameEngine object
	 * @return true if the game should quit
	 */
	public boolean getQuitFlag() {
		if(engine != null) {
			for(int i = 0; i < engine.length; i++) {
				if((engine[i] != null) && (engine[i].quitflag == true))
					return true;
			}
		}

		return false;
	}

	/**
	 * Check if at least 1 game is active
	 * @return true if there is a active GameEngine
	 */
	public boolean isGameActive() {
		if(engine != null) {
			for(int i = 0; i < engine.length; i++) {
				if((engine[i] != null) && (engine[i].gameActive == true))
					return true;
			}
		}

		return false;
	}

	/**
	 * Get winner ID
	 * @return Player ID of last survivor. -1 in single player game. -2 in tied game.
	 */
	public int getWinner() {
		if(engine.length < 2) return -1;

		for(int i = 0; i < engine.length; i++) {
			if(engine[i].stat != GameEngine.STAT_GAMEOVER) {
				return i;
			}
		}

		return -2;
	}

	/**
	 * Update every GameEngine
	 */
	public void updateAll() {
		for(int i = 0; i < engine.length; i++) {
			engine[i].update();
		}
		bgmStatus.fadeUpdate();
		backgroundStatus.fadeUpdate();
	}

	/**
	 * Dispatches all render events to EventReceiver
	 */
	public void renderAll() {
		for(int i = 0; i < engine.length; i++) {
			engine[i].render();
		}
	}

	/**
	 * Replay save routine
	 */
	public void saveReplay() {
		replayProp = new CustomProperties();
		for(int i = 0; i < engine.length; i++) {
			engine[i].saveReplay();
		}
		receiver.saveReplay(this, replayProp);
	}
}
