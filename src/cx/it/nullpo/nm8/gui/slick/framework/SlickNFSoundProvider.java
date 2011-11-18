package cx.it.nullpo.nm8.gui.slick.framework;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

import cx.it.nullpo.nm8.gui.framework.NFSound;
import cx.it.nullpo.nm8.gui.framework.NFSoundProvider;
import cx.it.nullpo.nm8.gui.framework.NFSystem;
import cx.it.nullpo.nm8.util.NUtil;

/**
 * Slick implementation of NFSoundProvider
 */
public class SlickNFSoundProvider extends NFSoundProvider {
	private static final long serialVersionUID = 4943978436256548528L;

	private Log log = LogFactory.getLog(SlickNFSoundProvider.class);

	@Override
	public String getName() {
		return "Slick OpenAL";
	}

	@Override
	public int getSoundProviderType() {
		return NFSystem.SOUND_PROVIDER_OPENAL_LWJGL;
	}

	@Override
	public NFSound loadSound(String filename) throws IOException {
		Sound nativeSound = null;
		SlickNFSound nfSound = null;
		File fileSound = new File(filename);
		int maxRetry = 0;

		// In Linux, sometimes it fails to load the file randomly because of the JDK bug
		// So we must try again at least 5 times
		if(NUtil.isLinux() && fileSound.isFile()) {
			maxRetry = 5;
		}

		try {
			for(int i = 0; i <= maxRetry; i++) {
				try {
					nativeSound = new Sound(filename);
					nfSound = new SlickNFSound(nativeSound);
					if(i >= 1) {
						log.debug(filename + " Load successful (Retry count:" + (i+1) + "/" + maxRetry + ")");
					}
					break;
				} catch (SlickException e) {
					if(i >= maxRetry) {
						if(maxRetry != 0) log.error("Give up...");
						throw e;
					} else {
						log.debug(filename + " Retrying (Retry count:" + (i+1) + "/" + maxRetry + ")");
					}
				}
			}
		} catch (Exception e) {
			throw new IOException("Couldn't load sound from " + filename + " (" + e.getMessage() + ")");
		}

		return nfSound;
	}

	@Override
	public NFSound loadSound(URL url) throws IOException {
		return loadSound(url.getPath());
	}

}
