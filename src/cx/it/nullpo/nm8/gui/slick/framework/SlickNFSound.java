package cx.it.nullpo.nm8.gui.slick.framework;

import org.newdawn.slick.Sound;

import cx.it.nullpo.nm8.gui.framework.NFSound;

/**
 * Slick implementation of NFSound
 */
public class SlickNFSound implements NFSound {
	private static final long serialVersionUID = 5586676211265875654L;

	/** Slick native Sound */
	protected Sound nativeSound;

	/**
	 * Constructor
	 * @param nativeSound Slick native Sound
	 */
	public SlickNFSound(Sound nativeSound) {
		this.nativeSound = nativeSound;
	}

	/**
	 * Get Slick native Sound
	 * @return Slick native Sound
	 */
	public Sound getNativeSound() {
		return nativeSound;
	}

	public boolean isPlaying() {
		return nativeSound.playing();
	}

	public void loop() {
		nativeSound.loop();
	}

	public void play() {
		nativeSound.play();
	}

	public void stop() {
		nativeSound.stop();
	}
}
