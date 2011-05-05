package cx.it.nullpo.nm8.gui.swing;

import cx.it.nullpo.nm8.gui.game.NullpoMino;
import cx.it.nullpo.nm8.gui.swing.framework.SwingNFSystem;

/**
 * Start NullpoMino with Swing framework
 */
public class NullpoMinoSwing {
	public static void main(String[] args) {
		try {
			SwingNFSystem sys = new SwingNFSystem(new NullpoMino(), false, 640, 480);
			sys.setTargetFPS(60);
			sys.init();
			sys.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
