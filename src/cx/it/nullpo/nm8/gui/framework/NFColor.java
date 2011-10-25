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
	protected int r;

	/** The green component of the colour */
	protected int g;

	/** The blue component of the colour */
	protected int b;

	/** The alpha component of the colour */
	protected int a;

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
	 * Set the red component of this colour
	 */
	public void setRed(int r) {
		this.r = r;
		if(this.r < 0) this.r = 0;
		if(this.r > 255) this.r = 255;
	}

	/**
	 * Set the Green component of this colour
	 */
	public void setGreen(int g) {
		this.g = g;
		if(this.g < 0) this.g = 0;
		if(this.g > 255) this.g = 255;
	}

	/**
	 * Set the Blue component of this colour
	 */
	public void setBlue(int b) {
		this.b = b;
		if(this.b < 0) this.b = 0;
		if(this.b > 255) this.b = 255;
	}

	/**
	 * Set the alpha component of this colour
	 */
	public void setAlpha(int a) {
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
}
