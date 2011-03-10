package cx.it.nullpo.nm8.gui.slick;

import cx.it.nullpo.nm8.gui.game.NullpoMino;
import cx.it.nullpo.nm8.gui.slick.framework.SlickNFSystem;

/**
 * Start NullpoMino with Slick framework
 */
public class NullpoMinoSlick {
	public static void main(String[] args) {
		try {
			SlickNFSystem sys = new SlickNFSystem(new NullpoMino(), false, 640, 480);
			sys.setTargetFPS(60);
			sys.init();
			sys.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
