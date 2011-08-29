package cx.it.nullpo.nm8.gui.swing.framework;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import cx.it.nullpo.nm8.gui.common.sound.javasound.JSSoundProvider;
import cx.it.nullpo.nm8.gui.framework.NFFont;
import cx.it.nullpo.nm8.gui.framework.NFGame;
import cx.it.nullpo.nm8.gui.framework.NFGraphics;
import cx.it.nullpo.nm8.gui.framework.NFImage;
import cx.it.nullpo.nm8.gui.framework.NFKeyboard;
import cx.it.nullpo.nm8.gui.framework.NFMouse;
import cx.it.nullpo.nm8.gui.framework.NFSound;
import cx.it.nullpo.nm8.gui.framework.NFSoundProvider;
import cx.it.nullpo.nm8.gui.framework.NFSystem;
import cx.it.nullpo.nm8.util.NUtil;

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

	/** Mouse Input */
	protected SwingNFMouse mouse;

	/** Window title */
	protected String windowTitle = "";

	/** Sound Provider */
	protected NFSoundProvider nfSoundProvider;

	public SwingNFSystem() {
		super();
	}
	public SwingNFSystem(NFGame game, boolean fullscreen, int width, int height, int oWidth, int oHeight) {
		super(game, fullscreen, width, height, oWidth, oHeight);
	}
	public SwingNFSystem(NFGame game, boolean fullscreen, int width, int height) {
		super(game, fullscreen, width, height);
	}
	public SwingNFSystem(NFGame game, boolean fullscreen, int width, int height, int oWidth, int oHeight,
						boolean keepaspectratio) {
		super(game, fullscreen, width, height, oWidth, oHeight, keepaspectratio);
	}
	public SwingNFSystem(NFGame game, boolean fullscreen, int width, int height, int oWidth, int oHeight,
						boolean keepaspectratio, String[] cmdArgs) {
		super(game, fullscreen, width, height, oWidth, oHeight, keepaspectratio, cmdArgs);
	}

	@Override
	public String getSystemName() {
		return "Swing";
	}

	@Override
	public void init() throws Exception {
		super.init();
		gameWrapper = new SwingNFGameWrapper(this);
	}

	@Override
	public void start() throws Exception {
		gameWrapper.start();
	}

	@Override
	public void exit() {
		getNFGame().onExit(this);

		if(nfSoundProvider != null) {
			nfSoundProvider.dispose();
		}
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
	public NFMouse getMouse() {
		if((mouse == null) && (gameWrapper != null)) {
			mouse = new SwingNFMouse(this, gameWrapper);
		}
		return mouse;
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
		return loadImage(NUtil.getURL(filename));
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
	public NFSound loadSound(String filename) throws IOException {
		if(nfSoundProvider == null) {
			nfSoundProvider = new JSSoundProvider();
		}
		return nfSoundProvider.loadSound(filename);
	}
	@Override
	public NFSound loadSound(URL url) throws IOException {
		if(nfSoundProvider == null) {
			nfSoundProvider = new JSSoundProvider();
		}
		return nfSoundProvider.loadSound(url);
	}
	@Override
	public boolean isSoundSupported() {
		return true;
	}
	@Override
	public boolean isSoundProviderTypeSupported(int type) {
		return true;
	}
	@Override
	public boolean setSoundProviderType(int type) {
		if(type == NFSystem.SOUND_PROVIDER_JAVASOUND) {
			nfSoundProvider = new JSSoundProvider();
			return true;
		} else if(type == NFSystem.SOUND_PROVIDER_OPENAL) {
			try {
				Class<?> c = null;
				NFSoundProvider obj = null;

				c = Class.forName("cx.it.nullpo.nm8.gui.common.sound.lwjglal.LWJGLALSoundProvider");
				obj = (NFSoundProvider) c.newInstance();

				nfSoundProvider = obj;
				return true;
			} catch (Throwable e) {
				System.err.println("Cannot initialize OpenAL");
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}
	@Override
	public NFSoundProvider getCurrentSoundProvider() {
		if(nfSoundProvider == null) {
			nfSoundProvider = new JSSoundProvider();
		}
		return nfSoundProvider;
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
}
