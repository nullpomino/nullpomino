package mu.nu.nullpo.gui.menu;

public class NumericMenuItem extends MenuItem {

	private int minValue, maxValue, step;
	private int arithmeticStyle;

	public static final int ARITHSTYLE_MODULAR = 0, ARITHSTYLE_SATURATE = 1;

	public NumericMenuItem(String name, int color) {
		this(name, color, 50, 0, 100, 1, ARITHSTYLE_MODULAR);
	}

	public NumericMenuItem(String name, int color, int state, int minValue, int maxValue, int step) {
		this(name, color, state, minValue, maxValue, step, ARITHSTYLE_MODULAR);
	}

	public NumericMenuItem(String name, int color, int state, int minValue, int maxValue, int step, int arithmeticStyle) {
		this.name = name;
		this.color = color;
		this.state = state;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.step = step;
		this.arithmeticStyle = arithmeticStyle;
	}

	public void changeState(int change) {
		state += step*change;
		int range = maxValue - minValue;
		if (state > maxValue) {
			switch (arithmeticStyle) {
				case ARITHSTYLE_MODULAR:
					do {
						state -= range;
					} while (state > maxValue);
					break;
				case ARITHSTYLE_SATURATE: state = maxValue; break;
			}
		} else if (state < minValue) {
			switch (arithmeticStyle) {
				case ARITHSTYLE_MODULAR:
					do {
						state += range;
					} while (state < maxValue);
					break;
				case ARITHSTYLE_SATURATE: state = minValue; break;
			}
		}
	}

	public int getState() {
		return state;
	}
}
