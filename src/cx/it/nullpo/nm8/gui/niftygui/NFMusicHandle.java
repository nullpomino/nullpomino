package cx.it.nullpo.nm8.gui.niftygui;

import cx.it.nullpo.nm8.gui.framework.NFMusic;
import de.lessvoid.nifty.spi.sound.SoundHandle;

public class NFMusicHandle implements SoundHandle {
	protected NFMusic nfMusic;

	public NFMusicHandle(NFMusic nfMusic) {
		this.nfMusic = nfMusic;
	}

	public void play() {
		nfMusic.play();
	}

	public void stop() {
		nfMusic.stop();
	}

	public void setVolume(float volume) {
		nfMusic.setVolume(volume);
	}

	public float getVolume() {
		return nfMusic.getVolume();
	}

	public boolean isPlaying() {
		return nfMusic.isPlaying();
	}

	public void dispose() {
		nfMusic.dispose();
	}
}
