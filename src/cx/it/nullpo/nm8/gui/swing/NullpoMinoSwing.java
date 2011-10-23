package cx.it.nullpo.nm8.gui.swing;

import cx.it.nullpo.nm8.gui.framework.NFGame;
import cx.it.nullpo.nm8.gui.game.NullpoMino;
import cx.it.nullpo.nm8.gui.niftygui.NiftyGUITest;
import cx.it.nullpo.nm8.gui.swing.framework.SwingNFSystem;
import cx.it.nullpo.nm8.util.CustomProperties;
import cx.it.nullpo.nm8.util.NGlobalConfig;

/**
 * Start NullpoMino with Swing framework
 */
public class NullpoMinoSwing {
	public static void main(String[] args) {
		try {
			NGlobalConfig.load();

			CustomProperties propGlobal = NGlobalConfig.getConfig();
			int screenWidth = propGlobal.getProperty("sys.resolution.width", 640);
			int screenHeight = propGlobal.getProperty("sys.resolution.height", 480);
			boolean fullscreen = propGlobal.getProperty("sys.fullscreen", false);

			NFGame game = null;
			if(args.length > 0 && args[0].equals("--guitest")) {
				game = new NiftyGUITest();
			} else {
				game = new NullpoMino();
			}

			SwingNFSystem sys = new SwingNFSystem(game, fullscreen, screenWidth, screenHeight, 640, 480, true, args);
			NGlobalConfig.applyNFSystem(sys);

			sys.init();
			sys.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
