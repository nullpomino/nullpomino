package mu.nu.nullpo.game.subsystem.mode.menu;

public abstract class EnumMenuItem extends IntegerMenuItem {
	public final String[] CHOICE_NAMES;
	public EnumMenuItem(String name, String displayName, int color, int defaultValue,
			String[] choiceNames) {
		super(name, displayName,  color, defaultValue,0, choiceNames.length);
		CHOICE_NAMES = choiceNames;
	}

	@Override
	public String getValueString() {
		return CHOICE_NAMES[value];
	}
}
