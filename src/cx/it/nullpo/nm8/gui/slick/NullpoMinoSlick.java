package cx.it.nullpo.nm8.gui.slick;

import cx.it.nullpo.nm8.gui.framework.NFSystem;
import cx.it.nullpo.nm8.gui.game.NullpoMino;
import cx.it.nullpo.nm8.gui.slick.framework.SlickNFSystem;

/**
 * Start NullpoMino with Slick framework
 */
public class NullpoMinoSlick {
	public static void main(String[] args) {
		try {
			boolean useAWTKeyReceiver = false;
			System.out.println("args.length: " + args.length);
			if((args.length > 0) && (args[0].equals("-a") || args[0].equals("/a"))) {
				useAWTKeyReceiver = true;
				System.out.println("useAWTKeyReceiver");
			}

			SlickNFSystem sys = new SlickNFSystem(new NullpoMino(), false, 640, 480, 640, 480, true, args);
			sys.setTargetFPS(60);
			sys.setSoundProviderType(NFSystem.SOUND_PROVIDER_OPENAL);
			sys.setUseAWTKeyReceiver(useAWTKeyReceiver);
			sys.init();
			sys.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
