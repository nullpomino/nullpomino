/**
 *
 */
package cx.it.nullpo.nm8.gui.swing.framework;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

import cx.it.nullpo.nm8.gui.framework.NFFont;
import cx.it.nullpo.nm8.gui.framework.NFGame;
import cx.it.nullpo.nm8.gui.framework.NFGraphics;
import cx.it.nullpo.nm8.gui.framework.NFImage;
import cx.it.nullpo.nm8.gui.framework.NFKeyboard;
import cx.it.nullpo.nm8.gui.framework.NFSystem;

/**
 * Swing implementation of NFSystem
 */
public class SwingNFSystem extends NFSystem {
	private static final long serialVersionUID = 1L;

	/** A JFrame that runs our game */
	protected SwingNFGameWrapper gameWrapper;

	/** Main graphics context */
	public SwingNFGraphics g;

	/** Keyboard Input */
	protected SwingNFKeyboard keyboard;

	/** Window title */
	protected String windowTitle = "";

	public SwingNFSystem() {
		super();
	}
	public SwingNFSystem(NFGame game, boolean fullscreen, int width, int height, int oWidth, int oHeight) {
		super(game, fullscreen, width, height, oWidth, oHeight);
	}
	public SwingNFSystem(NFGame game, boolean fullscreen, int width, int height) {
		super(game, fullscreen, width, height);
	}

	@Override
	public String getSystemName() {
		return "Swing";
	}

	@Override
	public void init() throws Exception {
		gameWrapper = new SwingNFGameWrapper(this);
	}

	@Override
	public void start() throws Exception {
		gameWrapper.start();
	}

	@Override
	public void exit() {
		getNFGame().onExit(this);

		if(gameWrapper != null) {
			gameWrapper.shutdownRequested = true;
		}
	}

	@Override
	public boolean hasFocus() {
		return (gameWrapper == null) ? false : gameWrapper.hasFocus();
	}

	@Override
	public NFKeyboard getKeyboard() {
		if(keyboard == null) {
			keyboard = new SwingNFKeyboard();
		}
		return keyboard;
	}

	@Override
	public NFGraphics getGraphics() {
		return g;
	}

	@Override
	public NFImage createImage(int width, int height) {
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		return new SwingNFImage(img);
	}

	@Override
	public NFImage loadImage(String filename) throws IOException {
		return loadImage(getURL(filename));
	}

	@Override
	public NFImage loadImage(URL url) throws IOException {
		BufferedImage img = ImageIO.read(url);
		return new SwingNFImage(img);
	}

	@Override
	public NFFont loadFont(Font font) {
		return new SwingNFFont(font);
	}

	@Override
	public NFFont loadFont(Font font, int size, boolean bold, boolean italic) {
		int style = Font.PLAIN;
		if(bold) style |= Font.BOLD;
		if(italic) style |= Font.ITALIC;
		Font newfont = font.deriveFont(style, (float)size);
		return new SwingNFFont(newfont);
	}

	@Override
	public NFFont loadFont(String filename) throws IOException {
		try {
			File file = new File(filename);
			Font basefont = Font.createFont(Font.TRUETYPE_FONT, file);
			Font newfont = basefont.deriveFont((float)16);
			return new SwingNFFont(newfont);
		} catch (FontFormatException e) {
			throw new IOException(filename + " is not a valid font (" + e.getMessage() + ")");
		}
	}

	@Override
	public NFFont loadFont(String filename, int size, boolean bold, boolean italic) throws IOException {
		try {
			File file = new File(filename);
			Font basefont = Font.createFont(Font.TRUETYPE_FONT, file);

			int style = Font.PLAIN;
			if(bold) style |= Font.BOLD;
			if(italic) style |= Font.ITALIC;

			Font newfont = basefont.deriveFont(style, (float)size);
			return new SwingNFFont(newfont);
		} catch (FontFormatException e) {
			throw new IOException(filename + " is not a valid font (" + e.getMessage() + ")");
		}
	}

	@Override
	public boolean isFontSupported() {
		return true;
	}

	@Override
	public float getFPS() {
		if(gameWrapper == null) return 0;
		return (float)gameWrapper.actualFPS;
	}

	@Override
	public void setWindowTitle(String title) {
		windowTitle = title;
		if(gameWrapper != null) gameWrapper.setTitle(title);
	}

	@Override
	public String getWindowTitle() {
		return windowTitle;
	}

	/**
	 * Get URL from a String
	 * @param str Filename
	 * @return URL
	 */
	public static URL getURL(String str) {
		URL url = null;

		try {
			char sep = File.separator.charAt(0);
			String file = str.replace(sep, '/');

			// Source (already dead):http://www.asahi-net.or.jp/~DP8T-ASM/java/tips/HowToMakeURL.html
			if(file.charAt(0) != '/') {
				String dir = System.getProperty("user.dir");
				dir = dir.replace(sep, '/') + '/';
				if(dir.charAt(0) != '/') {
					dir = "/" + dir;
				}
				file = dir + file;
			}
			url = new URL("file", "", file);
		} catch(MalformedURLException e) {
			return null;
		}

		return url;
	}
}
