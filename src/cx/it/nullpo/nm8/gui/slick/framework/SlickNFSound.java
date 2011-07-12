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

	/** Current volume of this sound */
	protected float currentVolume = 1f;

	/** Current pitch of this sound */
	protected float currentPitch = 1f;

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
		nativeSound.loop(currentPitch, currentVolume);
	}

	public void play() {
		nativeSound.play(currentPitch, currentVolume);
	}

	public void stop() {
		nativeSound.stop();
	}

	public void setVolume(float volume) {
		currentVolume = volume;
	}

	public float getVolume() {
		return currentVolume;
	}

	public void setPitch(float pitch) {
		currentPitch = pitch;
	}

	public float getPitch() {
		return currentPitch;
	}

	public void dispose() {
		// Slick can't dispose sound effects
	}
}
