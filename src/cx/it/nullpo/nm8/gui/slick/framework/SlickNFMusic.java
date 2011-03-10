package cx.it.nullpo.nm8.gui.slick.framework;

import org.newdawn.slick.Music;

import cx.it.nullpo.nm8.gui.framework.NFMusic;

public class SlickNFMusic implements NFMusic {
	private static final long serialVersionUID = -8941128130442945263L;

	/** Slick native Music */
	protected Music nativeMusic;

	/**
	 * Constructor
	 * @param nativeMusic Slick native Music
	 */
	public SlickNFMusic(Music nativeMusic) {
		this.nativeMusic = nativeMusic;
	}

	/**
	 * Get Slick native Music
	 * @return Slick native Music
	 */
	public Music getNativeMusic() {
		return nativeMusic;
	}

	public float getPosition() {
		return nativeMusic.getPosition();
	}

	public float getVolume() {
		return nativeMusic.getVolume();
	}

	public boolean isPlaying() {
		return nativeMusic.playing();
	}

	public void loop() {
		nativeMusic.loop();
	}

	public void pause() {
		nativeMusic.pause();
	}

	public void play() {
		nativeMusic.play();
	}

	public void resume() {
		nativeMusic.resume();
	}

	public boolean setPosition(float position) {
		return nativeMusic.setPosition(position);
	}

	public void setVolume(float volume) {
		nativeMusic.setVolume(volume);
	}

	public void stop() {
		nativeMusic.stop();
	}
}
