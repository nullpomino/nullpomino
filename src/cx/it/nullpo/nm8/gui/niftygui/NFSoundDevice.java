package cx.it.nullpo.nm8.gui.niftygui;

import java.io.IOException;

import cx.it.nullpo.nm8.gui.framework.NFMusic;
import cx.it.nullpo.nm8.gui.framework.NFSound;
import cx.it.nullpo.nm8.gui.framework.NFSystem;
import de.lessvoid.nifty.sound.SoundSystem;
import de.lessvoid.nifty.spi.sound.SoundDevice;
import de.lessvoid.nifty.spi.sound.SoundHandle;

public class NFSoundDevice implements SoundDevice {
	protected NFSystem sys;

	public NFSoundDevice(NFSystem sys) {
		this.sys = sys;
	}

	public SoundHandle loadSound(SoundSystem soundSystem, String filename) {
		if(sys.isSoundSupported()) {
			try {
				NFSound nfSound = sys.loadSound(filename);
				SoundHandle soundHandle = new NFSoundHandle(nfSound);
				return soundHandle;
			} catch (IOException e) {
				return null;
			} catch (IllegalStateException e) {
				return null;
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}

	public SoundHandle loadMusic(SoundSystem soundSystem, String filename) {
		if(sys.isMusicSupported()) {
			try {
				NFMusic nfMusic = sys.loadMusic(filename, true);
				SoundHandle soundHandle = new NFMusicHandle(nfMusic);
				return soundHandle;
			} catch (IOException e) {
				return null;
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}

	public void update(int delta) {

	}
}
