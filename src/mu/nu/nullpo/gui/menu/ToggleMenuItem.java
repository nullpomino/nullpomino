package mu.nu.nullpo.gui.menu;

public class ToggleMenuItem extends MenuItem {

	private int drawStyle;

	public static final int DRAWSTYLE_OX = 0, DRAWSTYLE_ONOFF = 1;

	public ToggleMenuItem(String name, int color) {
		this(name, color, 0, DRAWSTYLE_OX);
	}

	public ToggleMenuItem(String name, int color, int state, int drawStyle) {
		super(name);
		this.name = name;
		this.color = color;
		this.state = state;
		this.drawStyle = drawStyle;
	}

	public void changeState(int change) {
		state = 1 - state;
	}

	public int getState() {
		return state;
	}

	public int getDrawStyle() {
		return drawStyle;
	}
}
