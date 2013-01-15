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
package mu.nu.nullpo.gui.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.RenderedImage;
import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.JFrame;

import mu.nu.nullpo.game.net.NetObserverClient;
import mu.nu.nullpo.game.play.GameManager;

import org.apache.log4j.Logger;

/**
 * Game screen frame
 */
public class GameFrame extends JFrame implements Runnable {
	/** Serial version ID */
	private static final long serialVersionUID = 1L;

	/** Log */
	static Logger log = Logger.getLogger(GameFrame.class);

	/** Parent window */
	protected NullpoMinoSwing owner = null;

	/** The size of the border and title bar */
	protected Insets insets = null;

	/** BufferStrategy */
	protected BufferStrategy bufferStrategy = null;

	/** Game loop thread */
	protected Thread thread = null;

	/** trueThread moves between */
	public volatile boolean running = false;

	/** FPSFor calculation */
	protected long calcInterval = 0;

	/** FPSFor calculation */
	protected long prevCalcTime = 0;

	/**  frame count */
	protected long frameCount = 0;

	/** MaximumFPS (Setting) */
	public int maxfps;

	/** Current MaximumFPS */
	protected int maxfpsCurrent = 0;

	/** Current Pause time */
	protected long periodCurrent = 0;

	/** ActualFPS */
	public double actualFPS = 0.0;

	/** FPSDisplayDecimalFormat */
	public DecimalFormat df = new DecimalFormat("0.0");

	/** Used by perfect fps mode */
	public long perfectFPSDelay = 0;

	/** True to use perfect FPS */
	public boolean perfectFPSMode = false;

	/** Execute Thread.yield() during Perfect FPS mode */
	public boolean perfectYield = true;

	/** True if execute Toolkit.getDefaultToolkit().sync() at the end of each frame */
	public boolean syncDisplay = true;

	/** Screen width */
	protected int screenWidth = 640;

	/** Screen height */
	protected int screenHeight = 480;

	/** Pause state */
	protected boolean pause = false;

	/** Pose hidden message */
	protected boolean pauseMessageHide = false;

	/** Pause menuOfCursor position */
	protected int cursor = 0;

	/** Number of frames remaining until pause key can be used */
	protected int pauseFrame = 0;

	/** Double speedMode */
	protected int fastforward = 0;

	/** ScreenshotCreating flag */
	protected boolean ssflag = false;

	/** ScreenshotUseImage */
	protected Image ssImage = null;

	/**  frame Step is enabled flag */
	protected boolean enableframestep = false;

	/** FPSDisplay */
	protected boolean showfps = true;

	/** Ingame flag */
	public boolean[] isInGame;

	/** If net playtrue */
	public boolean isNetPlay = false;

	/** Mode name to enter (null=Exit) */
	public String strModeToEnter = "";

	/** Previous ingame flag (Used by title-bar text change) */
	protected boolean prevInGameFlag = false;

	/** Current game mode name */
	public String modeName;

	/**
	 * Constructor
	 * @param owner Parent window
	 * @throws HeadlessException Keyboard, Mouse, Exceptions such as the display if there is no
	 */
	public GameFrame(NullpoMinoSwing owner) throws HeadlessException {
		super();
		this.owner = owner;

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setTitle(NullpoMinoSwing.getUIText("Title_Game"));
		setBackground(Color.black);
		setResizable(false);
		setIgnoreRepaint(true);

		addWindowListener(new GameFrameWindowEvent());
		addKeyListener(new GameFrameKeyEvent());

		maxfps = NullpoMinoSwing.propConfig.getProperty("option.maxfps", 60);

		log.debug("GameFrame created");
	}

	/**
	 * Display the game window
	 */
	public void displayWindow() {
		setVisible(true);

		screenWidth = NullpoMinoSwing.propConfig.getProperty("option.screenwidth", 640);
		screenHeight = NullpoMinoSwing.propConfig.getProperty("option.screenheight", 480);
		insets = getInsets();
		int width = screenWidth + insets.left + insets.right;
		int height = screenHeight + insets.top + insets.bottom;
		setSize(width, height);

		if(!running) {
			thread = new Thread(this, "Game Thread");
			thread.start();
		}
	}

	/**
	 * End processing
	 */
	public void shutdown() {
		if(ssImage != null) {
			ssImage.flush();
			ssImage = null;
		}
		if(isNetPlay) {
			if(NullpoMinoSwing.netLobby != null) {
				try {
					NullpoMinoSwing.netLobby.shutdown();
				} catch (Exception e) {
					log.debug("Exception on NetLobby shutdown", e);
				}
				NullpoMinoSwing.netLobby = null;
			}

			// Reload global config (because it can change rules)
			NullpoMinoSwing.loadGlobalConfig();
		}
		running = false;
		owner.setVisible(true);
		setVisible(false);

		// GCCall
		System.gc();
	}

	/**
	 * Processing of the thread
	 */
	public void run() {
		boolean sleepFlag;
		long beforeTime, afterTime, timeDiff, sleepTime, sleepTimeInMillis;
		long overSleepTime = 0L;
		int noDelays = 0;

		// Initialization
		maxfpsCurrent = maxfps;
		periodCurrent = (long) (1.0 / maxfpsCurrent * 1000000000);
		beforeTime = System.nanoTime();
		prevCalcTime = beforeTime;
		pause = false;
		pauseMessageHide = false;
		fastforward = 0;
		cursor = 0;
		prevInGameFlag = false;
		isInGame = new boolean[2];
		GameKeySwing.gamekey[0].clear();
		GameKeySwing.gamekey[1].clear();
		updateTitleBarCaption();

		// Settings to take effect
		enableframestep = NullpoMinoSwing.propConfig.getProperty("option.enableframestep", false);
		showfps = NullpoMinoSwing.propConfig.getProperty("option.showfps", true);
		perfectFPSMode = NullpoMinoSwing.propConfig.getProperty("option.perfectFPSMode", false);
		perfectYield = NullpoMinoSwing.propConfig.getProperty("option.perfectYield", true);
		syncDisplay = NullpoMinoSwing.propConfig.getProperty("option.syncDisplay", true);

		// ObserverStart
		if(!isNetPlay) NullpoMinoSwing.startObserverClient();

		// Main loop
		log.debug("Game thread start");
		running = true;
		perfectFPSDelay = System.nanoTime();
		while(running) {
			if(isNetPlay) {
				gameUpdateNet();
				gameRenderNet();
			} else if(isVisible() && isActive()) {
				gameUpdate();
				gameRender();
			} else {
				GameKeySwing.gamekey[0].clear();
				GameKeySwing.gamekey[1].clear();
			}

			// FPS cap
			sleepFlag = false;

			afterTime = System.nanoTime();
			timeDiff = afterTime - beforeTime;

			sleepTime = (periodCurrent - timeDiff) - overSleepTime;
			sleepTimeInMillis = sleepTime / 1000000L;

			if((sleepTimeInMillis >= 4) && (!perfectFPSMode)) {
				// If it is possible to use sleep
				if(maxfps > 0) {
					try {
						Thread.sleep(sleepTimeInMillis);
					} catch(InterruptedException e) {
						log.debug("Game thread interrupted", e);
					}
				}
				// sleep() oversleep
				overSleepTime = (System.nanoTime() - afterTime) - sleepTime;
				perfectFPSDelay = System.nanoTime();
				sleepFlag = true;
			} else if((perfectFPSMode) || (sleepTime > 0)) {
				// Perfect FPS
				overSleepTime = 0L;
				if(maxfpsCurrent > maxfps + 5) maxfpsCurrent = maxfps + 5;
				if(perfectYield) {
					while(System.nanoTime() < perfectFPSDelay + 1000000000 / maxfps) {Thread.yield();}
				} else {
					while(System.nanoTime() < perfectFPSDelay + 1000000000 / maxfps) {}
				}
				perfectFPSDelay += 1000000000 / maxfps;

				// Don't run in super fast after the heavy slowdown
				if(System.nanoTime() > perfectFPSDelay + 2000000000 / maxfps) {
					perfectFPSDelay = System.nanoTime();
				}

				sleepFlag = true;
			}

			if(!sleepFlag) {
				// Impossible to sleep!
				overSleepTime = 0L;
				if(++noDelays >= 16) {
					Thread.yield();
					noDelays = 0;
				}
				perfectFPSDelay = System.nanoTime();
			}

			beforeTime = System.nanoTime();
			calcFPS(periodCurrent);
		}

		NullpoMinoSwing.gameManager.shutdown();
		NullpoMinoSwing.gameManager = null;

		if(!isNetPlay) NullpoMinoSwing.stopObserverClient();

		log.debug("Game thread end");
	}

	/**
	 * Update game state
	 */
	protected void gameUpdate() {
		if(NullpoMinoSwing.gameManager == null) return;

		// Set ingame flag
		for(int i = 0; i < 2; i++) {
			boolean prevInGame = isInGame[i];

			if((NullpoMinoSwing.gameManager.engine != null) && (NullpoMinoSwing.gameManager.engine.length > i)) {
				isInGame[i] = NullpoMinoSwing.gameManager.engine[i].isInGame;
			}
			if(pause && !enableframestep) {
				isInGame[i] = false;
			}

			if(prevInGame != isInGame[i]) {
				GameKeySwing.gamekey[i].clear();
			}
		}

		GameKeySwing.gamekey[0].update();
		GameKeySwing.gamekey[1].update();

		// Title bar update
		if((NullpoMinoSwing.gameManager != null) && (NullpoMinoSwing.gameManager.engine != null) &&
		   (NullpoMinoSwing.gameManager.engine.length > 0) && (NullpoMinoSwing.gameManager.engine[0] != null))
		{
			boolean nowInGame = NullpoMinoSwing.gameManager.engine[0].isInGame;
			if(prevInGameFlag != nowInGame) {
				prevInGameFlag = nowInGame;
				updateTitleBarCaption();
			}
		}

		// Pause button
		if(GameKeySwing.gamekey[0].isPushKey(GameKeySwing.BUTTON_PAUSE) || GameKeySwing.gamekey[1].isPushKey(GameKeySwing.BUTTON_PAUSE)) {
			if(!pause) {
				if((NullpoMinoSwing.gameManager != null) && (NullpoMinoSwing.gameManager.isGameActive()) && (pauseFrame <= 0)) {
					ResourceHolderSwing.soundManager.play("pause");
					pause = true;
					if(!enableframestep) pauseFrame = 5;
					cursor = 0;
				}
			} else {
				ResourceHolderSwing.soundManager.play("pause");
				pause = false;
				pauseFrame = 0;
			}
			updateTitleBarCaption();
		}
		// Pause menu
		if(pause && !enableframestep && !pauseMessageHide) {
			// Cursor movement
			if(GameKeySwing.gamekey[0].isMenuRepeatKey(GameKeySwing.BUTTON_UP)) {
				ResourceHolderSwing.soundManager.play("cursor");
				cursor--;

				if(cursor < 0) {
					if(NullpoMinoSwing.gameManager.replayMode && !NullpoMinoSwing.gameManager.replayRerecord)
						cursor = 3;
					else
						cursor = 2;
				}
			}
			if(GameKeySwing.gamekey[0].isMenuRepeatKey(GameKeySwing.BUTTON_DOWN)) {
				ResourceHolderSwing.soundManager.play("cursor");
				cursor++;
				if(cursor > 3) cursor = 0;

				if((!NullpoMinoSwing.gameManager.replayMode || NullpoMinoSwing.gameManager.replayRerecord) && (cursor > 2))
					cursor = 0;
			}

			// Confirm
			if(GameKeySwing.gamekey[0].isPushKey(GameKeySwing.BUTTON_A)) {
				ResourceHolderSwing.soundManager.play("decide");
				if(cursor == 0) {
					// Resumption
					pause = false;
					pauseFrame = 0;
					GameKeySwing.gamekey[0].clear();
				} else if(cursor == 1) {
					// Retry
					pause = false;
					NullpoMinoSwing.gameManager.reset();
				} else if(cursor == 2) {
					// End
					shutdown();
					return;
				} else if(cursor == 3) {
					// Replay re-record
					NullpoMinoSwing.gameManager.replayRerecord = true;
					cursor = 0;
				}
				updateTitleBarCaption();
			}
			// Unpause by cancel key
			else if(GameKeySwing.gamekey[0].isPushKey(GameKeySwing.BUTTON_B) && (pauseFrame <= 0)) {
				ResourceHolderSwing.soundManager.play("pause");
				pause = false;
				pauseFrame = 5;
				GameKeySwing.gamekey[0].clear();
				updateTitleBarCaption();
			}
		}
		if(pauseFrame > 0) pauseFrame--;

		// Hide pause menu
		pauseMessageHide = GameKeySwing.gamekey[0].isPressKey(GameKeySwing.BUTTON_C);

		if(NullpoMinoSwing.gameManager.replayMode && !NullpoMinoSwing.gameManager.replayRerecord && NullpoMinoSwing.gameManager.engine[0].gameActive) {
			// Replay speed
			if(GameKeySwing.gamekey[0].isMenuRepeatKey(GameKeySwing.BUTTON_LEFT)) {
				if(fastforward > 0) {
					fastforward--;
				}
			}
			if(GameKeySwing.gamekey[0].isMenuRepeatKey(GameKeySwing.BUTTON_RIGHT)) {
				if(fastforward < 98) {
					fastforward++;
				}
			}

			// Replay re-record
			if(GameKeySwing.gamekey[0].isPushKey(GameKeySwing.BUTTON_D)) {
				NullpoMinoSwing.gameManager.replayRerecord = true;
				cursor = 0;
			}
			// Show invisible blocks in replay
			if(GameKeySwing.gamekey[0].isPushKey(GameKeySwing.BUTTON_E)) {
				NullpoMinoSwing.gameManager.replayShowInvisible = !NullpoMinoSwing.gameManager.replayShowInvisible;
				cursor = 0;
			}
		} else {
			fastforward = 0;
		}

		// Execute game loops
		if(!pause || (GameKeySwing.gamekey[0].isPushKey(GameKeySwing.BUTTON_FRAMESTEP) && enableframestep)) {
			if(NullpoMinoSwing.gameManager != null) {
				for(int i = 0; i < Math.min(NullpoMinoSwing.gameManager.getPlayers(), 2); i++) {
					if(!NullpoMinoSwing.gameManager.replayMode || NullpoMinoSwing.gameManager.replayRerecord ||
					   !NullpoMinoSwing.gameManager.engine[i].gameActive)
					{
						GameKeySwing.gamekey[i].inputStatusUpdate(NullpoMinoSwing.gameManager.engine[i].ctrl);
					}
				}

				for(int i = 0; i <= fastforward; i++) NullpoMinoSwing.gameManager.updateAll();
			}
		}

		if(NullpoMinoSwing.gameManager != null) {
			// Retry button
			if(GameKeySwing.gamekey[0].isPushKey(GameKeySwing.BUTTON_RETRY) || GameKeySwing.gamekey[1].isPushKey(GameKeySwing.BUTTON_RETRY)) {
				pause = false;
				NullpoMinoSwing.gameManager.reset();
			}

			// Return to title
			if(NullpoMinoSwing.gameManager.getQuitFlag() ||
			   GameKeySwing.gamekey[0].isPushKey(GameKeySwing.BUTTON_GIVEUP) ||
			   GameKeySwing.gamekey[1].isPushKey(GameKeySwing.BUTTON_GIVEUP))
			{
				shutdown();
				return;
			}
		}

		// Screenshot button
		if(GameKeySwing.gamekey[0].isPushKey(GameKeySwing.BUTTON_SCREENSHOT) || GameKeySwing.gamekey[1].isPushKey(GameKeySwing.BUTTON_SCREENSHOT)) {
			ssflag = true;
		}

		// Quit button
		if(GameKeySwing.gamekey[0].isPushKey(GameKeySwing.BUTTON_QUIT) ||
		   GameKeySwing.gamekey[1].isPushKey(GameKeySwing.BUTTON_QUIT))
		{
			shutdown();
			owner.shutdown();
			return;
		}
	}

	/**
	 * Update game state (for netplay)
	 */
	protected void gameUpdateNet() {
		if(NullpoMinoSwing.gameManager == null) return;

		try {
			// Set ingame flag
			boolean prevInGame = isInGame[0];

			if((NullpoMinoSwing.gameManager.engine != null) && (NullpoMinoSwing.gameManager.engine.length > 0)) {
				isInGame[0] = NullpoMinoSwing.gameManager.engine[0].isInGame;
			}
			if(pause && !enableframestep) {
				isInGame[0] = false;
			}

			if(prevInGame != isInGame[0]) {
				GameKeySwing.gamekey[0].clear();
			}

			// Update button inputs
			if(isVisible() && isActive()) {
				GameKeySwing.gamekey[0].update();
			} else {
				GameKeySwing.gamekey[0].clear();
			}

			// Title bar update
			if((NullpoMinoSwing.gameManager != null) && (NullpoMinoSwing.gameManager.engine != null) &&
			   (NullpoMinoSwing.gameManager.engine.length > 0) && (NullpoMinoSwing.gameManager.engine[0] != null))
			{
				boolean nowInGame = NullpoMinoSwing.gameManager.engine[0].isInGame;
				if(prevInGameFlag != nowInGame) {
					prevInGameFlag = nowInGame;
					updateTitleBarCaption();
				}
			}

			// Execute game loops
			if((NullpoMinoSwing.gameManager != null) && (NullpoMinoSwing.gameManager.mode != null)) {
				GameKeySwing.gamekey[0].inputStatusUpdate(NullpoMinoSwing.gameManager.engine[0].ctrl);
				NullpoMinoSwing.gameManager.updateAll();

				// Return to title
				if(NullpoMinoSwing.gameManager.getQuitFlag()) {
					shutdown();
					return;
				}

				// Retry button
				if(GameKeySwing.gamekey[0].isPushKey(GameKeySwing.BUTTON_RETRY)) {
					NullpoMinoSwing.gameManager.mode.netplayOnRetryKey(NullpoMinoSwing.gameManager.engine[0], 0);
				}
			}

			// Screenshot button
			if(GameKeySwing.gamekey[0].isPushKey(GameKeySwing.BUTTON_SCREENSHOT) || GameKeySwing.gamekey[1].isPushKey(GameKeySwing.BUTTON_SCREENSHOT)) {
				ssflag = true;
			}

			// Enter to new mode
			if(strModeToEnter == null) {
				owner.enterNewMode(null);
				strModeToEnter = "";
			} else if (strModeToEnter.length() > 0) {
				owner.enterNewMode(strModeToEnter);
				strModeToEnter = "";
			}
		} catch (NullPointerException e) {
			try {
				if((NullpoMinoSwing.gameManager != null) && NullpoMinoSwing.gameManager.getQuitFlag()) {
					shutdown();
					return;
				} else {
					log.error("update NPE", e);
				}
			} catch (Throwable e2) {}
		} catch (Exception e) {
			try {
				if((NullpoMinoSwing.gameManager != null) && NullpoMinoSwing.gameManager.getQuitFlag()) {
					shutdown();
					return;
				} else {
					log.error("update fail", e);
				}
			} catch (Throwable e2) {}
		}
	}

	/**
	 * Rendering
	 */
	protected void gameRender() {
		if(NullpoMinoSwing.gameManager == null) return;

		// Prepare the screen
		if(ssImage == null) {
			ssImage = createImage(640, 480);
		}
		if((bufferStrategy == null) || bufferStrategy.contentsLost()) {
			try {
				createBufferStrategy(2);
				bufferStrategy = getBufferStrategy();
			} catch (Exception e) {
				return;
			}
		}

		Graphics g = null;
		if(ssflag || (screenWidth != 640) || (screenHeight != 480)) {
			g = ssImage.getGraphics();
		} else {
			g = bufferStrategy.getDrawGraphics();
			if(insets != null) g.translate(insets.left, insets.top);
		}

		// Game screen
		NormalFontSwing.graphics = (Graphics2D) g;
		NullpoMinoSwing.gameManager.receiver.setGraphics(g);
		NullpoMinoSwing.gameManager.renderAll();

		if((NullpoMinoSwing.gameManager.engine.length > 0) && (NullpoMinoSwing.gameManager.engine[0] != null)) {
			int offsetX = NullpoMinoSwing.gameManager.receiver.getFieldDisplayPositionX(NullpoMinoSwing.gameManager.engine[0], 0);
			int offsetY = NullpoMinoSwing.gameManager.receiver.getFieldDisplayPositionY(NullpoMinoSwing.gameManager.engine[0], 0);

			// Pause menu
			if(pause && !enableframestep && !pauseMessageHide) {
				NormalFontSwing.printFont(offsetX + 12, offsetY + 188 + (cursor * 16), "b", NormalFontSwing.COLOR_RED);

				NormalFontSwing.printFont(offsetX + 28, offsetY + 188, "CONTINUE", (cursor == 0));
				NormalFontSwing.printFont(offsetX + 28, offsetY + 204, "RETRY", (cursor == 1));
				NormalFontSwing.printFont(offsetX + 28, offsetY + 220, "END", (cursor == 2));
				if(NullpoMinoSwing.gameManager.replayMode && !NullpoMinoSwing.gameManager.replayRerecord)
					NormalFontSwing.printFont(offsetX + 28, offsetY + 236, "RERECORD", (cursor == 3));
			}

			// Fast forward
			if(fastforward != 0)
				NormalFontSwing.printFont(offsetX, offsetY + 376, "e" + (fastforward + 1), NormalFontSwing.COLOR_ORANGE);
			if(NullpoMinoSwing.gameManager.replayShowInvisible)
				NormalFontSwing.printFont(offsetX, offsetY + 392, "SHOW INVIS", NormalFontSwing.COLOR_ORANGE);
		}

		// FPSDisplay
		if(showfps) {
			if(perfectFPSMode)
				NormalFontSwing.printFont(0, 480-16, df.format(actualFPS), NormalFontSwing.COLOR_BLUE, 1.0f);
			else
				NormalFontSwing.printFont(0, 480-16, df.format(actualFPS) + "/" + maxfpsCurrent, NormalFontSwing.COLOR_BLUE, 1.0f);
		}

		// ObserverInformation
		NetObserverClient obClient = NullpoMinoSwing.getObserverClient();
		if((obClient != null) && obClient.isConnected()) {
			int observerCount = obClient.getObserverCount();
			int playerCount = obClient.getPlayerCount();
			int fontcolor = NormalFontSwing.COLOR_BLUE;
			if(observerCount > 1) fontcolor = NormalFontSwing.COLOR_GREEN;
			if(observerCount > 0 && playerCount > 0) fontcolor = NormalFontSwing.COLOR_RED;
			String strObserverInfo = String.format("%d/%d", observerCount, playerCount);
			String strObserverString = String.format("%40s", strObserverInfo);
			NormalFontSwing.printFont(0, 480 - 16, strObserverString, fontcolor);
		}

		// Displayed on the screen /ScreenshotCreating
		g.dispose();
		if(ssflag || (screenWidth != 640) || (screenHeight != 480)) {
			if(ssflag) saveScreenShot();

			if(insets != null) {
				Graphics g2 = getGraphics();
				if((screenWidth != 640) || (screenHeight != 480)) {
					g2.drawImage(ssImage, insets.left, insets.top, screenWidth, screenHeight, null);
				} else {
					g2.drawImage(ssImage, insets.left, insets.top, null);
				}
				g2.dispose();
				if(syncDisplay) Toolkit.getDefaultToolkit().sync();
			}

			ssflag = false;
		} else if((bufferStrategy != null) && !bufferStrategy.contentsLost()) {
			bufferStrategy.show();
			if(syncDisplay) Toolkit.getDefaultToolkit().sync();
		}
	}

	/**
	 * Rendering(For net play)
	 */
	protected void gameRenderNet() {
		if(NullpoMinoSwing.gameManager == null) return;

		// Prepare the screen
		if(ssImage == null) {
			ssImage = createImage(640, 480);
		}
		if((bufferStrategy == null) || bufferStrategy.contentsLost()) {
			try {
				createBufferStrategy(2);
				bufferStrategy = getBufferStrategy();
			} catch (Exception e) {
				return;
			}
		}

		Graphics g = null;
		if(ssflag || (screenWidth != 640) || (screenHeight != 480)) {
			g = ssImage.getGraphics();
		} else {
			g = bufferStrategy.getDrawGraphics();
			if(insets != null) g.translate(insets.left, insets.top);
		}

		// Game screen
		try {
			NormalFontSwing.graphics = (Graphics2D) g;
			NullpoMinoSwing.gameManager.receiver.setGraphics(g);
			NullpoMinoSwing.gameManager.renderAll();
		} catch (NullPointerException e) {
			try {
				if((NullpoMinoSwing.gameManager == null) || !NullpoMinoSwing.gameManager.getQuitFlag()) {
					log.error("render NPE", e);
				}
			} catch (Throwable e2) {}
		} catch (Exception e) {
			try {
				if((NullpoMinoSwing.gameManager == null) || !NullpoMinoSwing.gameManager.getQuitFlag()) {
					log.error("render fail", e);
				}
			} catch (Throwable e2) {}
		}

		// FPSDisplay
		if(showfps) {
			NormalFontSwing.printFont(0, 480-16, df.format(actualFPS) + "/" + maxfpsCurrent, NormalFontSwing.COLOR_BLUE, 1.0f);
		}

		// Displayed on the screen /ScreenshotCreating
		g.dispose();
		if(ssflag || (screenWidth != 640) || (screenHeight != 480)) {
			if(ssflag) saveScreenShot();

			if(insets != null) {
				Graphics g2 = getGraphics();
				if((screenWidth != 640) || (screenHeight != 480)) {
					g2.drawImage(ssImage, insets.left, insets.top, screenWidth, screenHeight, null);
				} else {
					g2.drawImage(ssImage, insets.left, insets.top, null);
				}
				g2.dispose();
				if(syncDisplay) Toolkit.getDefaultToolkit().sync();
			}

			ssflag = false;
		} else if((bufferStrategy != null) && !bufferStrategy.contentsLost()) {
			bufferStrategy.show();
			if(syncDisplay) Toolkit.getDefaultToolkit().sync();
		}
	}

	/**
	 * FPSCalculation of
	 * @param period FPSInterval to calculate the
	 */
	protected void calcFPS(long period) {
		frameCount++;
		calcInterval += period;

		// 1Second intervalsFPSRecalculate the
		if(calcInterval >= 1000000000L) {
			long timeNow = System.nanoTime();

			// Actual elapsed timeMeasure
			long realElapsedTime = timeNow - prevCalcTime; // Unit: ns

			// FPSCalculate the
			// realElapsedTimeThe unit ofnsSosConverted to
			actualFPS = ((double) frameCount / realElapsedTime) * 1000000000L;

			frameCount = 0L;
			calcInterval = 0L;
			prevCalcTime = timeNow;

			// Set new target fps
			if((maxfps > 0) && (!perfectFPSMode)) {
				if(actualFPS < maxfps - 1) {
					// Too slow
					maxfpsCurrent++;
					if(maxfpsCurrent > maxfps + 20) maxfpsCurrent = maxfps + 20;
					periodCurrent = (long) (1.0 / maxfpsCurrent * 1000000000);
				} else if(actualFPS > maxfps + 1) {
					// Too fast
					maxfpsCurrent--;
					if(maxfpsCurrent < maxfps - 0) maxfpsCurrent = maxfps - 0;
					if(maxfpsCurrent < 0) maxfpsCurrent = 0;
					periodCurrent = (long) (1.0 / maxfpsCurrent * 1000000000);
				}
			}
		}
	}

	/**
	 * Save a screen shot
	 */
	protected void saveScreenShot() {
		// Create filename
		String dir = NullpoMinoSwing.propGlobal.getProperty("custom.screenshot.directory", "ss");
		Calendar c = Calendar.getInstance();
		DateFormat dfm = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		String filename = dir + "/" + dfm.format(c.getTime()) + ".png";
		log.info("Saving screenshot to " + filename);

		// Create ss folder if not exist
		File ssfolder = new File(dir);
		if (!ssfolder.exists()) {
			if (ssfolder.mkdir()) {
				log.info("Created screenshot folder: " + dir);
			} else {
				log.info("Couldn't create screenshot folder at "+ dir);
			}
		}

		// Write
		try {
			javax.imageio.ImageIO.write((RenderedImage)ssImage, "PNG", new File(filename));
		} catch (Exception e) {
			log.warn("Failed to save screenshot to " + filename, e);
		}
	}

	/**
	 * Update title bar text
	 */
	public void updateTitleBarCaption() {
		GameManager gameManager = NullpoMinoSwing.gameManager;

		String strModeName = null;
		if((gameManager != null) && (gameManager.mode != null)) {
			strModeName = gameManager.mode.getName();
		}

		String strBaseTitle = "NullpoMino - " + strModeName;
		if(isNetPlay) strBaseTitle = "NullpoMino NetPlay - " + strModeName;

		String strTitle = strBaseTitle;

		if(isNetPlay && strModeName.equals("NET-DUMMY")) {
			strTitle = "NullpoMino NetPlay";
		} else if((gameManager != null) && (gameManager.engine != null) && (gameManager.engine.length > 0) && (gameManager.engine[0] != null)) {
			if(pause && !enableframestep)
				strTitle = "[PAUSE] " + strBaseTitle;
			else if(gameManager.engine[0].isInGame && !gameManager.replayMode && !gameManager.replayRerecord)
				strTitle = "[PLAY] " + strBaseTitle;
			else if(gameManager.replayMode && gameManager.replayRerecord)
				strTitle = "[RERECORD] " + strBaseTitle;
			else if(gameManager.replayMode && !gameManager.replayRerecord)
				strTitle = "[REPLAY] " + strBaseTitle;
			else
				strTitle = "[MENU] " + strBaseTitle;
		}

		this.setTitle(strTitle);
	}

	/**
	 * Window event Processing
	 */
	protected class GameFrameWindowEvent extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e) {
			shutdown();
		}
	}

	/**
	 * Keyboard event Processing
	 */
	protected class GameFrameKeyEvent extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			setButtonPressedState(e.getKeyCode(), true);
		}

		@Override
		public void keyReleased(KeyEvent e) {
			setButtonPressedState(e.getKeyCode(), false);
		}

		protected void setButtonPressedState(int keyCode, boolean pressed) {
			for(int playerID = 0; playerID < GameKeySwing.gamekey.length; playerID++) {
				int[] kmap = isInGame[playerID] ? GameKeySwing.gamekey[playerID].keymap : GameKeySwing.gamekey[playerID].keymapNav;

				for(int i = 0; i < GameKeySwing.MAX_BUTTON; i++) {
					if(keyCode == kmap[i]) {
						//log.debug("KeyCode:" + keyCode + " pressed:" + pressed + " button:" + i);
						GameKeySwing.gamekey[playerID].setPressState(i, pressed);
					}
				}
			}
		}
	}
}
