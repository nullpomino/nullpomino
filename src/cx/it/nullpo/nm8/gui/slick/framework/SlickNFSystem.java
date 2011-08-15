package cx.it.nullpo.nm8.gui.slick.framework;

import java.awt.Font;
import java.io.IOException;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.Music;
import org.newdawn.slick.ScalableGame;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.openal.SoundStore;

import cx.it.nullpo.nm8.gui.common.JSSoundLoader;
import cx.it.nullpo.nm8.gui.framework.NFFont;
import cx.it.nullpo.nm8.gui.framework.NFGame;
import cx.it.nullpo.nm8.gui.framework.NFGraphics;
import cx.it.nullpo.nm8.gui.framework.NFImage;
import cx.it.nullpo.nm8.gui.framework.NFJoystickManager;
import cx.it.nullpo.nm8.gui.framework.NFKeyboard;
import cx.it.nullpo.nm8.gui.framework.NFMouse;
import cx.it.nullpo.nm8.gui.framework.NFMusic;
import cx.it.nullpo.nm8.gui.framework.NFSound;
import cx.it.nullpo.nm8.gui.framework.NFSystem;

/**
 * Slick implementation of NFSystem
 */
public class SlickNFSystem extends NFSystem {
	private static final long serialVersionUID = -8136236560162750019L;

	/** AppGameContainer to run our game */
	protected AppGameContainer container;

	/** Implementation of Slick's Game interface */
	protected SlickNFGameWrapper gameWrapper;

	/** Main graphics context */
	protected SlickNFGraphics g;

	/** Keyboard Input */
	protected SlickNFKeyboard keyboard;

	/** Mouse Input */
	protected SlickNFMouse mouse;

	/** Joystick Manager */
	protected SlickNFJoystckManager joyManager;

	/** Window title */
	protected String windowTitle = "";

	/** Use Java Sound API instead of OpenAL */
	protected boolean useJavaSound;

	/** Use AWT key receiver instead of LWJGL's native one */
	protected boolean useAWTKeyReceiver;

	/** SlickKeyReceiverJFrame for AWT key receiver */
	protected SlickKeyReceiverJFrame slickKeyReceiverJFrame;

	public SlickNFSystem() {
		super();
	}

	public SlickNFSystem(NFGame game, boolean fullscreen, int width, int height) {
		super(game, fullscreen, width, height);
	}
	public SlickNFSystem(NFGame game, boolean fullscreen, int width, int height, int oWidth, int oHeight) {
		super(game, fullscreen, width, height, oWidth, oHeight);
	}
	public SlickNFSystem(NFGame game, boolean fullscreen, int width, int height, int oWidth, int oHeight,
						boolean keepaspectratio) {
		super(game, fullscreen, width, height, oWidth, oHeight, keepaspectratio);
	}
	public SlickNFSystem(NFGame game, boolean fullscreen, int width, int height, int oWidth, int oHeight,
						boolean keepaspectratio, String[] cmdArgs) {
		super(game, fullscreen, width, height, oWidth, oHeight, keepaspectratio, cmdArgs);
	}

	/**
	 * Get AppGameContainer to run our game
	 * @return AppGameContainer
	 */
	public AppGameContainer getAppGameContainer() {
		return container;
	}

	@Override
	public String getSystemName() {
		return "Slick";
	}

	@Override
	public void init() throws Exception {
		gameWrapper = new SlickNFGameWrapper(this);

		if(isGameWindowScalingUsed()) {
			ScalableGame s = new ScalableGame(gameWrapper, originalScreenWidth, originalScreenHeight, keepAspectRatio);
			container = new AppGameContainer(s, screenWidth, screenHeight, fullscreen);
		} else {
			container = new AppGameContainer(gameWrapper, screenWidth, screenHeight, fullscreen);
		}

		container.setForceExit(false);
		container.setShowFPS(false);
		container.setMaximumLogicUpdateInterval(0);
		container.setMinimumLogicUpdateInterval(0);
		container.setTargetFrameRate(targetFPS);
		container.setSoundVolume(soundVolume);
		container.setMusicVolume(musicVolume);

		container.setAlwaysRender(useAWTKeyReceiver);
		container.setUpdateOnlyWhenVisible(!useAWTKeyReceiver);

		super.init();
	}

	@Override
	public void start() throws Exception {
		if(container == null) init();
		container.start();
	}

	@Override
	public void exit() {
		getNFGame().onExit(this);

		if(slickKeyReceiverJFrame != null) {
			slickKeyReceiverJFrame.dispose();
		}
		if(container != null) {
			container.exit();
		}
	}

	@Override
	public boolean hasFocus() {
		if((slickKeyReceiverJFrame != null) && slickKeyReceiverJFrame.isFocused()) {
			return true;
		}
		return (container == null) ? false : container.hasFocus();
	}

	@Override
	public NFKeyboard getKeyboard() {
		if((keyboard == null) && (container != null)) {
			if(useAWTKeyReceiver) {
				slickKeyReceiverJFrame = new SlickKeyReceiverJFrame(this);
				keyboard = new SlickNFKeyboard(container.getInput(), slickKeyReceiverJFrame);
			} else {
				keyboard = new SlickNFKeyboard(container.getInput());
			}
		} else if (keyboard.getNativeInput() == null && container != null) {
			keyboard.setNativeInput(container.getInput());
		}
		return keyboard;
	}

	@Override
	public NFMouse getMouse() {
		if((mouse == null) && (container != null)) {
			mouse = new SlickNFMouse(container.getInput());
		} else if (mouse.getNativeInput() == null && container != null) {
			mouse.setNativeInput(container.getInput());
		}
		return mouse;
	}

	@Override
	public NFGraphics getGraphics() {
		if(g == null) {
			g = new SlickNFGraphics(container.getGraphics());
		}
		return g;
	}

	@Override
	public NFImage createImage(int width, int height) {
		try {
			Image img = new Image(width, height);
			return new SlickNFImage(img);
		} catch (SlickException e) {
			throw new RuntimeException("Couldn't create an empty image", e);
		}
	}

	@Override
	public NFImage loadImage(String filename) throws IOException {
		try {
			Image img = new Image(filename);
			return new SlickNFImage(img);
		} catch (SlickException e) {
			throw new IOException("Couldn't load image from " + filename + " (" + e.getMessage() + ")");
		}
	}

	@Override
	public NFFont loadFont(Font font) {
		UnicodeFont uFont = new UnicodeFont(font);
		uFont.getEffects().add(new ColorEffect(java.awt.Color.white));	// TODO: Add support more effects
		SlickNFFont nfFont = new SlickNFFont(uFont);
		return nfFont;
	}

	@Override
	public NFFont loadFont(Font font, int size, boolean bold, boolean italic) {
		UnicodeFont uFont = new UnicodeFont(font, size, bold, italic);
		uFont.getEffects().add(new ColorEffect(java.awt.Color.white));	// TODO: Add support more effects
		SlickNFFont nfFont = new SlickNFFont(uFont, size, bold, italic);
		return nfFont;
	}

	@Override
	public NFFont loadFont(String filename) throws IOException {
		return loadFont(filename, 16, false, false);
	}

	@Override
	public NFFont loadFont(String filename, int size, boolean bold, boolean italic) throws IOException {
		try {
			UnicodeFont uFont = new UnicodeFont(filename, size, bold, italic);
			uFont.getEffects().add(new ColorEffect(java.awt.Color.white));	// TODO: Add support more effects
			SlickNFFont nfFont = new SlickNFFont(uFont, size, bold, italic);
			return nfFont;
		} catch (SlickException e) {
			throw new IOException("Couldn't load font from " + filename + " (" + e.getMessage() + ")");
		}
	}

	@Override
	public boolean isFontSupported() {
		return true;
	}

	@Override
	public NFJoystickManager getJoystickManager() {
		if(joyManager == null) {
			joyManager = new SlickNFJoystckManager();
		}
		return joyManager;
	}

	@Override
	public boolean isJoystickSupported() {
		return true;
	}

	@Override
	public NFSound loadSound(String filename) throws IOException {
		if(useJavaSound) {
			return JSSoundLoader.load(filename);
		}

		// In Linux, sometimes it fails to load the file randomly because of the JDK bug
		// So we must try again at least 5 times
		int maxRetry = 0;
		if(System.getProperty("os.name").contains("Linux")) {
			maxRetry = 5;
		}

		try {
			Sound nativeSound = null;

			for(int i = 0; i <= maxRetry; i++) {
				try {
					nativeSound = new Sound(filename);
					break;
				} catch (SlickException e) {
					if(i >= maxRetry) {
						if(maxRetry != 0) System.err.println("Give up...");
						throw e;
					} else {
						System.err.println("Retrying (" + (i+1) + "/" + maxRetry + ")");
					}
				}
			}

			SlickNFSound nfSound = new SlickNFSound(nativeSound);
			return nfSound;
		} catch (Exception e) {
			throw new IOException("Couldn't load sound from " + filename + " (" + e.getMessage() + ")");
		}
	}

	@Override
	public boolean isSoundSupported() {
		return true;
	}

	@Override
	public NFMusic loadMusic(String filename, boolean stream) throws IOException {
		try {
			Music nativeMusic = new Music(filename, stream);
			SlickNFMusic nfMusic = new SlickNFMusic(nativeMusic);
			return nfMusic;
		} catch (SlickException e) {
			throw new IOException("Couldn't load music from " + filename + " (" + e.getMessage() + ")");
		}
	}

	@Override
	public boolean isMusicSupported() {
		return SoundStore.get().soundWorks();
	}

	@Override
	public void setSoundVolume(float volume) {
		super.setSoundVolume(volume);
		if(container != null) container.setSoundVolume(volume);
	}

	@Override
	public void setMusicVolume(float volume) {
		super.setMusicVolume(volume);
		if(container != null) container.setMusicVolume(volume);
	}

	@Override
	public void setTargetFPS(int fps) {
		super.setTargetFPS(fps);
		if(container != null) container.setTargetFrameRate(fps);
	}

	@Override
	public float getFPS() {
		return (container == null) ? 0 : (float)container.getFPS();
	}

	@Override
	public boolean isFullscreen() {
		return (container == null) ? fullscreen : container.isFullscreen();
	}

	@Override
	public void setWindowTitle(String title) {
		windowTitle = title;
		if(container != null) container.setTitle(title);
	}

	@Override
	public String getWindowTitle() {
		return windowTitle;
	}

	/**
	 * [Slick only]<br>
	 * Returns true if we use Java Sound API instead of OpenAL
	 * @return true if we use Java Sound API instead of OpenAL
	 */
	public boolean isUseJavaSound() {
		return useJavaSound;
	}

	/**
	 * [Slick only]<br>
	 * If set to true, we use Java Sound API instead of OpenAL.<br>
	 * OpenAL does not work with some sound cards (especially laptops), but Java Sound might still work.<br>
	 * Linux only notes: Java Sound API is awful in Linux. Not recommended.
	 * @param b true if we use Java Sound API instead of OpenAL
	 */
	public void setUseJavaSound(boolean b) {
		useJavaSound = b;
	}

	/**
	 * [Slick only]<br>
	 * Returns true if we use AWT's key receiver instead of LWJGL's
	 * @return true if we use AWT's key receiver instead of LWJGL's
	 */
	public boolean isUseAWTKeyReceiver() {
		return useAWTKeyReceiver;
	}

	/**
	 * [Slick only]<br>
	 * If set to true, we'll spawn a JFrame to receive key inputs from AWT.<br>
	 * If your Keyboard do not respond, try using this.<br>
	 * @param useAWTKeyReceiver true if we use AWT's key receiver instead of LWJGL's
	 */
	public void setUseAWTKeyReceiver(boolean useAWTKeyReceiver) {
		this.useAWTKeyReceiver = useAWTKeyReceiver;
	}
}
