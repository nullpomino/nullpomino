package mu.nu.nullpo.game.subsystem.mode.menu;

import mu.nu.nullpo.util.CustomProperties;

public class IntegerMenuItem extends AbstractMenuItem<Integer> {
	public int min, max;

	public IntegerMenuItem(String name, String displayName, int color,
			int defaultValue, int min, int max) {
		super(name, displayName, color, defaultValue);
		this.min = min;
		this.max = max;
	}

	@Override
	public void change(int dir, int fast) {
		value += dir;
		if (value < min)
			value = max;
		if (value > max)
			value = min;
	}

	@Override
	public void save(int playerID, CustomProperties prop, String modeName) {
		prop.setProperty(modeName + "." + name
				+ (playerID < 0 ? "" : ".p" + playerID), value);
	}

	@Override
	public void load(int playerID, CustomProperties prop, String modeName) {
		value = prop.getProperty(modeName + "." + name
				+ (playerID < 0 ? "" : ".p" + playerID), DEFAULT_VALUE);
	}

	@Override
	public String getValueString() {
		return String.valueOf(value);
	}
}
