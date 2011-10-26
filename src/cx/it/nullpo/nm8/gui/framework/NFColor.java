package cx.it.nullpo.nm8.gui.framework;

import java.io.Serializable;

/**
 * NullpoMino Framework (NF) - NFColor<br>
 * An object that stores color values
 */
public class NFColor implements Serializable {
	/** The version ID for this class  */
	private static final long serialVersionUID = -3007888852198148930L;

	/** The fixed color transparent */
	public static final NFColor transpalent = new NFColor(0, 0, 0, 0);
	/** The fixed colour white */
	public static final NFColor white = new NFColor(255, 255, 255, 255);
	/** The fixed colour yellow */
	public static final NFColor yellow = new NFColor(255, 255, 0, 255);
	/** The fixed colour red */
	public static final NFColor red = new NFColor(255, 0, 0, 255);
	/** The fixed colour blue */
	public static final NFColor blue = new NFColor(0, 0, 255, 255);
	/** The fixed colour green */
	public static final NFColor green = new NFColor(0, 255, 0, 255);
	/** The fixed colour black */
	public static final NFColor black = new NFColor(0, 0, 0, 255);
	/** The fixed colour gray */
	public static final NFColor gray = new NFColor(128, 128, 128, 255);
	/** The fixed colour cyan */
	public static final NFColor cyan = new NFColor(0, 255, 255, 255);
	/** The fixed colour dark gray */
	public static final NFColor darkgray = new NFColor(77, 77, 77, 255);
	/** The fixed colour light gray */
	public static final NFColor lightgray = new NFColor(179, 179, 179, 255);
	/** The fixed colour dark pink */
	public static final NFColor pink = new NFColor(255, 175, 175, 255);
	/** The fixed colour dark orange */
	public static final NFColor orange = new NFColor(255, 200, 0, 255);
	/** The fixed colour dark magenta */
	public static final NFColor magenta = new NFColor(255, 0, 255, 255);

	/** The red component of the colour */
	private int r;

	/** The green component of the colour */
	private int g;

	/** The blue component of the colour */
	private int b;

	/** The alpha component of the colour */
	private int a;

	/**
	 * Create a transpalent colour (0,0,0,0)
	 */
	public NFColor() {
		this.r = 0;
		this.g = 0;
		this.b = 0;
		this.a = 0;
	}

	/**
	 * Copy constructor
	 * @param c Copy source
	 */
	public NFColor(NFColor c) {
		this.r = c.r;
		this.g = c.g;
		this.b = c.b;
		this.a = c.a;
	}

	/**
	 * Create NFColor from an AWT Color
	 * @param c AWT Color
	 */
	public NFColor(java.awt.Color c) {
		this.r = c.getRed();
		this.g = c.getGreen();
		this.b = c.getBlue();
		this.a = c.getAlpha();
	}

	/**
	 * Create NFColor from an int value (Alpha value will be set to 255)
	 * @param i int value
	 */
	public NFColor(int i) {
		this.r = ((i >> 16) & 0xFF);
		this.g = ((i >> 8) & 0xFF);
		this.b = ((i) & 0xFF);
		this.a = 255;
	}

	/**
	 * Create NFColor from an int value
	 * @param i int value
	 * @param alpha true to include alpha value
	 */
	public NFColor(int i, boolean alpha) {
		if(!alpha) {
			this.a = 255;
		} else {
			this.a = ((i >> 24) & 0xFF);
		}
		this.r = ((i >> 16) & 0xFF);
		this.g = ((i >> 8) & 0xFF);
		this.b = ((i) & 0xFF);
	}

	/**
	 * Create a 3 component colour
	 * @param r Red
	 * @param g Green
	 * @param b Blue
	 */
	public NFColor(int r, int g, int b) {
		setRed(r);
		setGreen(g);
		setBlue(b);
		setAlpha(255);
	}

	/**
	 * Create a 4 component colour
	 * @param r Red
	 * @param g Green
	 * @param b Blue
	 * @param a Alpha
	 */
	public NFColor(int r, int g, int b, int a) {
		setRed(r);
		setGreen(g);
		setBlue(b);
		setAlpha(a);
	}

	/**
	 * Create a NFColor by using hex string such as "#FF8844".
	 * The string is decoded by Integer.decode(String).
	 * @param nm String to decode
	 * @throws NumberFormatException When the String can't be decoded
	 */
	public NFColor(String nm) {
		Integer intVal = Integer.decode(nm);
		int i = intVal.intValue();
		setRed((i >> 16) & 0xFF);
		setGreen((i >> 8) & 0xFF);
		setBlue(i & 0xFF);
		setAlpha(255);
	}

	/**
	 * Set the red component of this colour
	 */
	private void setRed(int r) {
		this.r = r;
		if(this.r < 0) this.r = 0;
		if(this.r > 255) this.r = 255;
	}

	/**
	 * Set the Green component of this colour
	 */
	private void setGreen(int g) {
		this.g = g;
		if(this.g < 0) this.g = 0;
		if(this.g > 255) this.g = 255;
	}

	/**
	 * Set the Blue component of this colour
	 */
	private void setBlue(int b) {
		this.b = b;
		if(this.b < 0) this.b = 0;
		if(this.b > 255) this.b = 255;
	}

	/**
	 * Set the alpha component of this colour
	 */
	private void setAlpha(int a) {
		this.a = a;
		if(this.a < 0) this.a = 0;
		if(this.a > 255) this.a = 255;
	}

	/**
	 * Get the red component of this colour
	 * @return The red component (range 0-255)
	 */
	public int getRed() {
		return r;
	}

	/**
	 * Get the green component of this colour
	 * @return The green component (range 0-255)
	 */
	public int getGreen() {
		return g;
	}

	/**
	 * Get the blue component of this colour
	 * @return The blue component (range 0-255)
	 */
	public int getBlue() {
		return b;
	}

	/**
	 * Get the alpha component of this colour
	 * @return The alpha component (range 0-255)
	 */
	public int getAlpha() {
		return a;
	}

	/**
	 * Convert this NFColor to an AWT Color
	 * @return AWT Color
	 */
	public java.awt.Color toAWTColor() {
		return new java.awt.Color(r,g,b,a);
	}

	/**
	 * @return true if this NFColor can be used as a color filter
	 */
	public boolean isColorFilter() {
		return ((r != 255) || (g != 255) || (b != 255));
	}

	/**
	 * @return true if this NFColor has alpha value
	 */
	public boolean isAlpha() {
		return (a != 255);
	}

	/**
	 * Convert this NFColor to an int value. Alpha value will NOT be included.
	 * @return int value
	 */
	public int getIntValue() {
		return getIntValue(false);
	}

	/**
	 * Convert this NFColor to an int value.
	 * @param alpha true to include the alpha value
	 * @return int value
	 */
	public int getIntValue(boolean alpha) {
		int i = ((r << 16) | (g << 8) | (b));
		if(alpha) i |= (a << 24);
		return i;
	}

	/**
	 * Get HTML color code of this NFColor
	 * @return String of HTML color code (#RRGGBB)
	 */
	public String toHTMLColorCode() {
		return String.format("#%06X", getIntValue());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + a;
		result = prime * result + b;
		result = prime * result + g;
		result = prime * result + r;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NFColor other = (NFColor) obj;
		if (a != other.a)
			return false;
		if (b != other.b)
			return false;
		if (g != other.g)
			return false;
		if (r != other.r)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "NFColor [r=" + r + ", g=" + g + ", b=" + b + ", a=" + a + "]";
	}
}
