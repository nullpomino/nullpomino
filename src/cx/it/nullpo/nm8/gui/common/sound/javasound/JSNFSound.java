package cx.it.nullpo.nm8.gui.common.sound.javasound;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cx.it.nullpo.nm8.gui.framework.NFSound;

/**
 * Java Sound implementation of NFSound
 */
public class JSNFSound implements NFSound {
	private static final long serialVersionUID = 7484516954156886425L;

	/** Log */
	private Log log = LogFactory.getLog(JSNFSound.class);

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

		try {
			FloatControl ctrl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
			ctrl.setValue((float)Math.log10(volume) * 20);
		} catch (IllegalArgumentException e) {
			log.warn("Your Java Sound system does not support the control of master gain. Volume cannot be changed.", e);
		}
	}

	public float getVolume() {
		return currentVolume;
	}

	public void setPitch(float pitch) {
	}

	public float getPitch() {
		return 1f;
	}

	public void dispose() {
		clip.close();
	}
}
