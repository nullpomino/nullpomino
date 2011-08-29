package cx.it.nullpo.nm8.gui.swing;

import cx.it.nullpo.nm8.gui.framework.NFSystem;
import cx.it.nullpo.nm8.gui.game.NullpoMino;
import cx.it.nullpo.nm8.gui.swing.framework.SwingNFSystem;

/**
 * Start NullpoMino with Swing framework
 */
public class NullpoMinoSwing {
	public static void main(String[] args) {
		try {
			SwingNFSystem sys = new SwingNFSystem(new NullpoMino(), false, 640, 480, 640, 480, true, args);
			sys.setTargetFPS(60);
			sys.init();
			sys.setSoundProviderType(NFSystem.SOUND_PROVIDER_OPENAL);
			sys.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
