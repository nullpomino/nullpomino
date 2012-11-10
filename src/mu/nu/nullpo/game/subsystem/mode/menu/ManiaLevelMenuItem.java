package mu.nu.nullpo.game.subsystem.mode.menu;

public class ManiaLevelMenuItem extends IntegerMenuItem {
	public int min, max;

	public ManiaLevelMenuItem(String name, String displayName, int color) {
		super(name, displayName, color, 0, 0, 9);
	}

	@Override
	public String getValueString() {
		return String.valueOf(value * 100);
	}
}
