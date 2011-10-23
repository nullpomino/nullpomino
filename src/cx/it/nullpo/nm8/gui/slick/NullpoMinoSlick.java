package cx.it.nullpo.nm8.gui.slick;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cx.it.nullpo.nm8.gui.framework.NFGame;
import cx.it.nullpo.nm8.gui.game.NullpoMino;
import cx.it.nullpo.nm8.gui.niftygui.NiftyGUITest;
import cx.it.nullpo.nm8.gui.slick.framework.SlickNFSystem;
import cx.it.nullpo.nm8.util.CustomProperties;
import cx.it.nullpo.nm8.util.NGlobalConfig;

/**
 * Start NullpoMino with Slick framework
 */
public class NullpoMinoSlick {
	/** Log */
	private static Log log = LogFactory.getLog(NullpoMinoSlick.class);

	public static void main(String[] args) {
		try {
			org.newdawn.slick.util.Log.setLogSystem(new SlickCustomLogSystem());

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

			SlickNFSystem sys = new SlickNFSystem(game, fullscreen, screenWidth, screenHeight, 640, 480, true, args);
			NGlobalConfig.applyNFSystem(sys);
			if(!fullscreen) sys.setUseAWTKeyReceiver(propGlobal.getProperty("slick.awtkey", false));

			sys.init();
			sys.start();
		} catch (Exception e) {
			log.fatal("Something went wrong", e);
		}
	}
}
