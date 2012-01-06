package cx.it.nullpo.nm8.gui.game;

import java.awt.event.KeyEvent;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;

import cx.it.nullpo.nm8.game.component.Controller;
import cx.it.nullpo.nm8.game.play.GameEngine;
import cx.it.nullpo.nm8.game.play.GameManager;
import cx.it.nullpo.nm8.game.play.GamePlay;
import cx.it.nullpo.nm8.gui.framework.NFColor;
import cx.it.nullpo.nm8.gui.framework.NFGraphics;
import cx.it.nullpo.nm8.gui.framework.NFKeyboard;
import cx.it.nullpo.nm8.gui.framework.NFNEUROGame;
import cx.it.nullpo.nm8.gui.framework.NFSystem;
import cx.it.nullpo.nm8.gui.niftygui.NFInputSystem;
import cx.it.nullpo.nm8.gui.niftygui.NFRenderDevice;
import cx.it.nullpo.nm8.gui.niftygui.NFSoundDevice;
import cx.it.nullpo.nm8.neuro.event.KeyInputEvent;
import cx.it.nullpo.nm8.util.NGlobalConfig;
import cx.it.nullpo.nm8.util.NUtil;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.tools.TimeProvider;

/**
 * NFGame implementation of NullpoMino (WIP)
 */
public class NullpoMinoNiftyGUI extends NFNEUROGame {
	private static final long serialVersionUID = 612498635208267339L;

	/** Log */
	private Log log = LogFactory.getLog(NullpoMinoNiftyGUI.class);

	/** NFSystem */
	public NFSystem sys;

	/** NiftyGUI */
	protected Nifty nifty;

	/** true if NiftyGUI is inited */
	protected boolean niftyInited;

	/** true if various resource has loaded */
	protected boolean isResourceLoaded;

	/** GameManager */
	protected GameManager gameManager;

	// Constants for key config
	protected static final int MAX_PLAYERS = 2;
	protected static final int MAX_RULE_KIND = 2;
	protected static final int MAX_KEY_SLOTS = 4;
	protected static final int MAX_KEY_KINDS = 9;

	@Override
	public String getName() {
		return "NullpoMino";
	}

	@Override
	public float getVersion() {
		return GameManager.getVersionMajor();
	}

	@Override
	public String getAuthor() {
		return "The NullpoMino Dev Team";
	}

	@Override
	public void init(NFSystem sys) {
		log.trace("init");

		this.sys = sys;
		addListener(KeyInputEvent.class);

		sys.setWindowTitle("NullpoMino8 (NiftyGUI)");
	}

	@Override
	public void update(NFSystem sys, long delta) {
		if(!isResourceLoaded) {
			isResourceLoaded = true;

			try {
				String strBlockSkinPathName = "nullpoworld";
				SAXBuilder builder = new SAXBuilder();
				Document doc = null;
				doc = builder.build(NUtil.getURL("data/res/graphics/block/" + strBlockSkinPathName + "/blockindex.xml"));
				ResourceHolder.blockSkin = BlockSkin.loadByXML(sys, strBlockSkinPathName, doc); // TODO: Load multiple block skin
			} catch (Exception e) {
				log.error("Failed to load block skin", e);
			}

			try {
				if(sys.isSoundSupported() && NGlobalConfig.getConfig().getProperty("sys.enablesound", true)) {
					log.debug("Loading sound effects");
					ResourceHolder.loadSoundEffects(sys, "default");
				}
			} catch (Exception e) {
				log.error("Failed to load sound effects", e);
			}
		}

		if(niftyInited && (nifty != null)) {
			nifty.update();
		}

		if((gameManager != null) && gameManager.isGameActive()) {
			gameManager.update(delta);

			// Play sound effects
			if(gameManager.getGamePlay(0,0) != null) {
				synchronized (gameManager.getGamePlay(0,0).seQueue) {
					Iterator<String> it = gameManager.getGamePlay(0,0).seQueue.iterator();
					while(it.hasNext()) {
						ResourceHolder.playSE(it.next());
						it.remove();
					}
				}
			}
		}
	}

	@Override
	public void render(NFSystem sys, NFGraphics g) {
		g.setColor(NFColor.black);
		g.fillRect(0, 0, sys.getOriginalWidth(), sys.getOriginalHeight());

		if(!niftyInited && (nifty == null)) {
			try {
				nifty = new Nifty(new NFRenderDevice(sys),
				                   new NFSoundDevice(sys),
				                   new NFInputSystem(sys.getKeyboard(), sys.getMouse()),
				                   new TimeProvider());
				log.trace("NiftyGUI created");
				initGUI(nifty);

				nifty.fromXml("data/xml/game_main.xml", "start", new MainScreenController(this));
				niftyInited = true;
			} catch (Exception e) {
				log.error("NiftyGUI init failed", e);
			}
		}

		if(niftyInited && (nifty != null)) {
			nifty.render(false);

			if((gameManager != null) && gameManager.isGameStarted()) {
				Screen curScreen = nifty.getCurrentScreen();
				if(curScreen != null) {
					for(int engineID = 0; engineID < gameManager.getNumberOfEngines(); engineID++) {
						GameFieldController fldctl = curScreen.findControl("field-" + engineID, GameFieldController.class);

						if((fldctl != null) && (fldctl.getElementPanelField() != null)) {
							Element e = fldctl.getElementPanelField();
							GameEngine engine = gameManager.getGameEngine(engineID);

							if(engine.gameStarted) {
								GameFieldRenderer.drawField(engine, g, e.getX(), e.getY(), e.getWidth(), e.getHeight(), fldctl.getBlockSize());
								GameFieldRenderer.drawGhostPiece(engine, g, e.getX(), e.getY(), e.getWidth(), e.getHeight(), fldctl.getBlockSize());
								GameFieldRenderer.drawCurrentPiece(engine, g, e.getX(), e.getY(), e.getWidth(), e.getHeight(), fldctl.getBlockSize());
							}
						}
					}
				}
			}
		}

		g.setColor(NFColor.white);
		g.resetFont();
		g.drawString("FPS:" + sys.getFPS(), 0, 0);
	}

	@Override
	public void onExit(NFSystem sys) {
		try {
			ResourceHolder.unloadSoundEffects();
			nifty.exit();
		} catch (Throwable e) {}
	}

	public void keyPressed(NFKeyboard keyboard, int key, char c) {
		handleKeyEvent(keyboard, key, c, true);
	}
	public void keyReleased(NFKeyboard keyboard, int key, char c) {
		handleKeyEvent(keyboard, key, c, false);
	}

	public void gameStart() {
		log.trace("Starting a new game...");

		try {
			if(gameManager == null) {
				gameManager = new GameManager();
			} else {
				gameManager.stop();
			}
			gameManager.init();
			gameManager.start();
		} catch (Exception e) {
			log.fatal("Start new game failed", e);
		}

		log.trace("Started a new game");
	}

	public void receiveEvent(KeyInputEvent e) {
		handleKeyEvent(e.getKeyboard(), e.getKey(), e.getChar(), e.getPressed());
	}

	public void handleKeyEvent(NFKeyboard keyboard, int key, char c, boolean pressed) {
		if(gameManager != null && gameManager.isGameActive()) {
			for(int engineID = 0; engineID < gameManager.getNumberOfEngines(); engineID++) {
				if(gameManager.getGameEngine(engineID).gameActive) {
					for(int playerID = 0; playerID < gameManager.getNumberOfPlayersForEachEngine(); playerID++) {
						GamePlay play = gameManager.getGamePlay(engineID, playerID);
						Controller ctrl = play.ctrl;
						int ctrlSchemeID = play.ruleopt.ctrlSchemeID;

						for(int slot = 0; slot < MAX_KEY_SLOTS; slot++) {
							for(int keyKind = 0; keyKind < MAX_KEY_KINDS; keyKind++) {
								String strID = "key_" + playerID + "_" + ctrlSchemeID + "_" + slot + "_" + keyKind;
								int tempKeyCode = NGlobalConfig.getConfig().getProperty(strID, KeyEvent.VK_UNDEFINED);

								if(key == tempKeyCode) {
									ctrl.setButtonState(keyKind, pressed);
								}
							}
						}

						if(key == KeyEvent.VK_ESCAPE) {
							sys.exit();
						}
					}
				}
			}
		}
	}
}
