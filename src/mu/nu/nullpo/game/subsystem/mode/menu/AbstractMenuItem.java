package mu.nu.nullpo.game.subsystem.mode.menu;

import mu.nu.nullpo.util.CustomProperties;

public abstract class AbstractMenuItem<T> {
	public final String name;
	public final String displayName;
	public final int color;
	public final T DEFAULT_VALUE;
	public T value;

	public AbstractMenuItem(String name, String displayName, int color,
			T defaultValue) {
		this.name = name;
		this.displayName = displayName;
		this.color = color;
		DEFAULT_VALUE = defaultValue;
		value = defaultValue;
	}

	public abstract String getValueString();

	/**
	 * Change the attribute
	 * 
	 * @param dir
	 *            Direction pressed: -1 = left, 1 = right
	 * @param fast
	 *            0 by default, +1 if E held, +2 if F held.
	 */
	public abstract void change(int dir, int fast);

	public abstract void save(int playerID, CustomProperties prop,
			String modeName);

	public abstract void load(int playerID, CustomProperties prop,
			String modeName);
}
