package cx.it.nullpo.nm8.gui.common.font.angelcode;

import java.io.Serializable;

/**
 * Each character info of AngelCodeFont fnt file.
 */
public class AngelCodeCharInfo implements Serializable {
	private static final long serialVersionUID = 2135560737190434058L;

	private int id;

	private int x;

	private int y;

	private int width;

	private int height;

	private int xoffset;

	private int yoffset;

	private int xadvance;

	private int page;

	private int chnl;

	public AngelCodeCharInfo(int id, int x, int y, int width, int height, int xoffset, int yoffset, int xadvance, int page, int chnl) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.xoffset = xoffset;
		this.yoffset = yoffset;
		this.xadvance = xadvance;
		this.page = page;
		this.chnl = chnl;
	}

	public int getId() {
		return id;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getXoffset() {
		return xoffset;
	}

	public int getYoffset() {
		return yoffset;
	}

	public int getXadvance() {
		return xadvance;
	}

	public int getPage() {
		return page;
	}

	public int getChnl() {
		return chnl;
	}

	@Override
	public String toString() {
		return "AngelCodeCharInfo [id=" + id + ", x=" + x + ", y=" + y
				+ ", width=" + width + ", height=" + height + ", xoffset="
				+ xoffset + ", yoffset=" + yoffset + ", xadvance=" + xadvance
				+ ", page=" + page + ", chnl=" + chnl + "]";
	}
}
