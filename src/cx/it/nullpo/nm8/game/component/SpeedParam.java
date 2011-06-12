package cx.it.nullpo.nm8.game.component;

import java.io.Serializable;

/**
 * Speed parameters
 */
public class SpeedParam implements Serializable {
	/** Serial version ID */
	private static final long serialVersionUID = 6851579409706241896L;

	/** Gravity */
	public long gravity;

	/** ARE (AppeaRancE delay) */
	public long are;

	/** Line clear ARE */
	public long areLine;

	/** Lock flash */
	public long lockFlash;

	/** Line clear delay */
	public long lineDelay;

	/** Lock delay */
	public long lockDelay;

	/** DAS (Delayed Auto Shift) */
	public long das;

	/** ARR (Auto Repeat Rate) */
	public long arr;

	/** Lock DAS to this SpeedParam's setting (If false, DAS will be defined by player) */
	public boolean lockDAS;

	/** Lock ARR to this SpeedParam's setting (If false, ARR will be defined by player) */
	public boolean lockARR;

	/**
	 * Constructor
	 */
	public SpeedParam() {
		reset();
	}

	/**
	 * Constructor
	 * @param frameBased true if using frame based timer
	 */
	public SpeedParam(boolean frameBased) {
		reset(frameBased);
	}

	/**
	 * Copy Constructor
	 * @param s Copy source
	 */
	public SpeedParam(SpeedParam s) {
		copy(s);
	}

	/**
	 * Reset to miliseconds defaults
	 */
	public void reset() {
		reset(false);
	}

	/**
	 * Reset to defaults
	 * @param frameBased true if using frame based timer
	 */
	public void reset(boolean frameBased) {
		if(!frameBased) {
			gravity = 1000;
			are = 0;
			areLine = 0;
			lockFlash = 0;
			lineDelay = 200;
			lockDelay = 500;
			das = 133;
			arr = 17;
		} else {
			gravity = 60;
			are = 0;
			areLine = 0;
			lockFlash = 0;
			lineDelay = 12;
			lockDelay = 30;
			das = 12;
			arr = 1;
		}
		lockDAS = false;
		lockARR = false;
	}

	/**
	 * Copy from other instance of SpeedParam
	 * @param s Copy source
	 */
	public void copy(SpeedParam s) {
		gravity = s.gravity;
		are = s.are;
		areLine = s.areLine;
		lockFlash = s.lockFlash;
		lineDelay = s.lineDelay;
		lockDelay = s.lockDelay;
		das = s.das;
		arr = s.arr;
		lockDAS = s.lockDAS;
		lockARR = s.lockARR;
	}
}
