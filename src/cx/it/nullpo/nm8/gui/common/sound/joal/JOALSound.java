package cx.it.nullpo.nm8.gui.common.sound.joal;

import cx.it.nullpo.nm8.gui.framework.NFSound;

/**
 * OpenAL (JOAL backend) implementation of NFSound.
 * Please note this class does NOT handle most actions.
 * Most actions are handled by JOALSoundProvider, the owner of this class.
 */
public class JOALSound implements NFSound {
	private static final long serialVersionUID = -7435493109899815153L;

	/** Owner of this class */
	protected JOALSoundProvider owner = null;

	/** true if disposed */
	protected boolean isDisposed = false;

	/** Buffers hold sound data */
	protected int buffer;

	/** Current Volume */
	protected float currentVolume = 1f;

	/** Current Pitch */
	protected float currentPitch = 1f;

	/**
	 * Constructor
	 * @param owner JOALSoundProvider
	 * @param buffer Buffer
	 */
	public JOALSound(JOALSoundProvider owner, int buffer) {
		this.owner = owner;
		this.buffer = buffer;
	}

	public void play() {
		owner.playSound(this, false);
	}

	public void loop() {
		owner.playSound(this, true);
	}

	public void stop() {
		owner.stopSound(this);
	}

	public boolean isPlaying() {
		return owner.isPlaying(this);
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
		if(!isDisposed) {
			owner.al.alDeleteBuffers(1, new int[] {buffer}, 0);
			isDisposed = true;
		}
	}

	public int getBuffer() {
		return buffer;
	}
}
