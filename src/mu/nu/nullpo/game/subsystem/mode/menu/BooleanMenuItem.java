package mu.nu.nullpo.game.subsystem.mode.menu;

import mu.nu.nullpo.util.CustomProperties;

public class BooleanMenuItem extends AbstractMenuItem<Boolean> {
	public BooleanMenuItem(String name, String displayName, int color,
			boolean defaultValue) {
		super(name, displayName, color, defaultValue);
	}

	@Override
	public void change(int dir, int fast) {
		value = !value;
	}

	@Override
	public String getValueString() {
		return String.valueOf(value).toUpperCase();
	}

	@Override
	public void save(int playerID, CustomProperties prop, String modeName) {
		prop.setProperty(modeName + "." + name
				+ (playerID >= 0 ? "" : ".p" + playerID), value);
	}

	@Override
	public void load(int playerID, CustomProperties prop, String modeName) {
		prop.getProperty(modeName + "." + name
				+ (playerID >= 0 ? "" : ".p" + playerID), DEFAULT_VALUE);
	}
}
