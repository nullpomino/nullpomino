package cx.it.nullpo.nm8.gui.swing;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cx.it.nullpo.nm8.gui.framework.NFGame;
import cx.it.nullpo.nm8.gui.game.NullpoMinoNiftyGUI;
import cx.it.nullpo.nm8.gui.swing.framework.SwingNFSystem;
import cx.it.nullpo.nm8.util.CustomProperties;
import cx.it.nullpo.nm8.util.NGlobalConfig;

/**
 * Start NullpoMino with Swing framework
 */
public class NullpoMinoSwing {
	/** Log */
	private static Log log = LogFactory.getLog(NullpoMinoSwing.class);

	public static void main(String[] args) {
		try {
			NGlobalConfig.load();

			CustomProperties propGlobal = NGlobalConfig.getConfig();
			int screenWidth = propGlobal.getProperty("sys.resolution.width", 640);
			int screenHeight = propGlobal.getProperty("sys.resolution.height", 480);
			boolean fullscreen = propGlobal.getProperty("sys.fullscreen", false);

			NFGame game = null;
			SwingNFSystem sys = null;

			game = new NullpoMinoNiftyGUI();
			sys = new SwingNFSystem(game, fullscreen, screenWidth, screenHeight, screenWidth, screenHeight, true, args);

			NGlobalConfig.applyNFSystem(sys);
			sys.setUseBufferStrategy(propGlobal.getProperty("swing.useBufferStrategy", true));
			log.trace("Use BufferStrategy:" + sys.isUseBufferStrategy());

			sys.init();
			sys.start();
		} catch (Exception e) {
			log.fatal("Something went wrong", e);
		}
	}
}
