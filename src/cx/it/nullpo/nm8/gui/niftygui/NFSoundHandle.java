package cx.it.nullpo.nm8.gui.niftygui;

import cx.it.nullpo.nm8.gui.framework.NFSound;
import de.lessvoid.nifty.spi.sound.SoundHandle;

public class NFSoundHandle implements SoundHandle {
	protected NFSound nfSound;

	public NFSoundHandle(NFSound nfSound) {
		this.nfSound = nfSound;
	}

	public void play() {
		nfSound.play();
	}

	public void stop() {
		nfSound.stop();
	}

	public void setVolume(float volume) {
		nfSound.setVolume(volume);
	}

	public float getVolume() {
		return nfSound.getVolume();
	}

	public boolean isPlaying() {
		return nfSound.isPlaying();
	}

	public void dispose() {
		nfSound.dispose();
	}
}
