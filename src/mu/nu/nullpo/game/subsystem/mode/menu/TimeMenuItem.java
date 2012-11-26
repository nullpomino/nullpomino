package mu.nu.nullpo.game.subsystem.mode.menu;

import mu.nu.nullpo.util.GeneralUtil;

public class TimeMenuItem extends IntegerMenuItem {
	public int increment;

	public TimeMenuItem(String name, String displayName, int color,
			int defaultValue, int min, int max) {
		super(name, displayName, color, defaultValue, min, max);
		increment = 60;
	}

	public TimeMenuItem(String name, String displayName, int color,
			int defaultValue, int min, int max, int increment) {
		super(name, displayName, color, defaultValue, min, max);
		this.increment = increment;
	}

	@Override
	public void change(int dir, int fast) {
		int delta = dir * increment;
		value += delta;
		if (value < min)
			value = max;
		if (value > max)
			value = min;
	}

	@Override
	public String getValueString() {
		return GeneralUtil.getTime(value);
	}
}
