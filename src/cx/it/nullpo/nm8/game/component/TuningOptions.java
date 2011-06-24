package cx.it.nullpo.nm8.game.component;

import java.io.Serializable;

/**
 * Tuning options (user preferences)
 */
public class TuningOptions implements Serializable {
	/** Serial version ID */
	private static final long serialVersionUID = 3574429758546005968L;

	/** Preferred DAS in millisecond-based modes (-1:Use mode's setting) */
	public long das;

	/** Preferred DAS in frame-based modes (-1:Use mode's setting) */
	public long dasF;

	/** Preferred ARR in millisecond-based modes (-1:Use mode's setting) */
	public long arr;

	/** Preferred ARR in frame-based modes (-1:Use mode's setting) */
	public long arrF;

	/** Enable IRS if possible (only if enabled in the rule too) */
	public boolean rotateInitial;

	/** Enable IHS if possible (only if enabled in the rule too) */
	public boolean holdInitial;

	/** Show ghost piece if possible */
	public boolean ghost;

	/** Block skin (-1:Use rule's default) */
	public int skin;

	/**
	 * Constructor
	 */
	public TuningOptions() {
		reset();
	}

	/**
	 * Copy constructor
	 * @param t Copy source
	 */
	public TuningOptions(TuningOptions t) {
		copy(t);
	}

	/**
	 * Initialization
	 */
	public void reset() {
		das = -1;
		dasF = -1;
		arr = -1;
		arrF = -1;
		rotateInitial = true;
		holdInitial = true;
		ghost = true;
		skin = -1;
	}

	/**
	 * Copy from another TuningOptions
	 * @param t Copy source
	 */
	public void copy(TuningOptions t) {
		das = t.das;
		dasF = t.dasF;
		arr = t.arr;
		arrF = t.arrF;
		rotateInitial = t.rotateInitial;
		holdInitial = t.holdInitial;
		ghost = t.ghost;
		skin = t.skin;
	}
}
