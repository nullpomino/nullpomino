package cx.it.nullpo.nm8.gui.common;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

import cx.it.nullpo.nm8.gui.framework.NFSound;

/**
 * Java Sound implementation of NFSound
 */
public class JSNFSound implements NFSound {
	private static final long serialVersionUID = 7484516954156886425L;

	/** Java Sound native Clip */
	protected Clip clip;

	/** Current volume of this sound */
	protected float currentVolume = 1f;

	/**
	 * Constructor
	 * @param clip Java Sound native Clip
	 */
	public JSNFSound(Clip clip) {
		this.clip = clip;
	}

	public void play() {
		clip.stop();
		clip.setFramePosition(0);
		clip.start();
	}

	public void loop() {
		clip.stop();
		clip.setFramePosition(0);
		clip.loop(Clip.LOOP_CONTINUOUSLY);
	}

	public void stop() {
		clip.stop();
	}

	public boolean isPlaying() {
		return clip.isRunning();
	}

	public void setVolume(float volume) {
		currentVolume = volume;
		FloatControl ctrl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
		ctrl.setValue((float)Math.log10(volume) * 20);
	}

	public float getVolume() {
		return currentVolume;
	}

	public void setPitch(float pitch) {
	}

	public float getPitch() {
		return 1f;
	}
}
