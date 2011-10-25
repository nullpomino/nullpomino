package cx.it.nullpo.nm8.gui.slick.framework;

import java.awt.Font;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.Music;
import org.newdawn.slick.ScalableGame;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.openal.SoundStore;

import cx.it.nullpo.nm8.gui.common.font.angelcode.AngelCodeMainInfo;
import cx.it.nullpo.nm8.gui.common.sound.javasound.JSSoundProvider;
import cx.it.nullpo.nm8.gui.framework.NFFont;
import cx.it.nullpo.nm8.gui.framework.NFGame;
import cx.it.nullpo.nm8.gui.framework.NFGraphics;
import cx.it.nullpo.nm8.gui.framework.NFImage;
import cx.it.nullpo.nm8.gui.framework.NFJoystickManager;
import cx.it.nullpo.nm8.gui.framework.NFKeyboard;
import cx.it.nullpo.nm8.gui.framework.NFMouse;
import cx.it.nullpo.nm8.gui.framework.NFMusic;
import cx.it.nullpo.nm8.gui.framework.NFSound;
import cx.it.nullpo.nm8.gui.framework.NFSoundProvider;
import cx.it.nullpo.nm8.gui.framework.NFSystem;
import cx.it.nullpo.nm8.util.NUtil;

/**
 * Slick implementation of NFSystem
 */
public class SlickNFSystem extends NFSystem {
	private static final long serialVersionUID = -8136236560162750019L;

	/** Log */
	private Log log = LogFactory.getLog(SlickNFSystem.class);

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

	/** Sound Provider */
	protected NFSoundProvider nfSoundProvider;

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

		if(nfSoundProvider != null) {
			nfSoundProvider.dispose();
		}
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
			g = new SlickNFGraphics(container.getGraphics(), this);
		}
		return g;
	}

	@Override
	public NFImage createImage(int width, int height) {
		try {
			Image img = new Image(width, height);
			return new SlickNFImage(img, this);
		} catch (SlickException e) {
			throw new RuntimeException("Couldn't create an empty image", e);
		}
	}

	@Override
	public NFImage loadImage(String filename) throws IOException {
		try {
			Image img = new Image(filename);
			return new SlickNFImage(img, this);
		} catch (SlickException e) {
			throw new IOException("Couldn't load image from " + filename + " (" + e.getMessage() + ")");
		}
	}

	@Override
	public NFFont loadFont(Font font) {
		if(font == null) {
			throw new NullPointerException("font parameter is null. You might have passed a font that not created from AWT.");
		}
		UnicodeFont uFont = new UnicodeFont(font);
		uFont.getEffects().add(new ColorEffect(java.awt.Color.white));	// TODO: Add support more effects
		SlickNFFont nfFont = new SlickNFFont(uFont);
		return nfFont;
	}

	@Override
	public NFFont loadFont(Font font, int size, boolean bold, boolean italic) {
		if(font == null) {
			throw new NullPointerException("font parameter is null. You might have passed a font that not created from AWT.");
		}
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
		URL url = NUtil.getURL(filename);

		if(filename.endsWith(".fnt")) {
			// It's an AngelCodeFont. We need to find out where the image is stored.
			log.debug("Loading an AngelCodeFont from " + filename + " ...");

			InputStream in = url.openStream();
			List<String> fntfile = NUtil.getStringListFromInputStreamE(in);
			in.close();

			AngelCodeMainInfo info = new AngelCodeMainInfo(fntfile);
			int pages = info.getPages();
			if(pages == 0) {
				throw new IllegalArgumentException("This fnt file does not contain any pages.");
			} else if(pages >= 2) {
				log.warn("This fnt file contains " + info.getPages() + " pages, but we can only use the first page for now.");
			}

			String imgfile = info.getPageInfoList().get(0).getFileName();

			URL urlImage = NUtil.getURL(imgfile);
			InputStream inFnt = url.openStream();
			InputStream inImg = urlImage.openStream();

			try {
				AngelCodeFont aFont = new AngelCodeFont(filename, inFnt, inImg);
				SlickNFAngelCodeFont nfFont = new SlickNFAngelCodeFont(aFont, info);
				log.debug("Successfully loaded an AngelCodeFont from " + filename + " with the image file " + imgfile);
				return nfFont;
			} catch (SlickException e) {
				log.error("Failed to load AngelCodeFont from " + filename + " and " + imgfile + " (" + e.getMessage() + ")", e);
				throw new IOException("Failed to load AngelCodeFont from " + filename + " and " + imgfile + " (" + e.getMessage() + ")", e);
			} finally {
				try {
					inFnt.close();
					inImg.close();
				} catch (IOException e2) {}
			}
		}

		try {
			// Otherwise it's a UnicodeFont
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
		if(nfSoundProvider == null) {
			nfSoundProvider = new JSSoundProvider();
		}
		return nfSoundProvider.loadSound(filename);
	}

	@Override
	public boolean isSoundSupported() {
		return true;
	}

	@Override
	public boolean isSoundProviderTypeSupported(int type) {
		if(type == SOUND_PROVIDER_JAVASOUND) return true;
		if(type == SOUND_PROVIDER_OPENAL) return true;
		return false;
	}

	@Override
	public boolean setSoundProviderType(int type) {
		try {
			if(type == SOUND_PROVIDER_JAVASOUND) {
				nfSoundProvider = new JSSoundProvider();
			} else if(type == SOUND_PROVIDER_OPENAL) {
				nfSoundProvider = new SlickNFSoundProvider();
			}
			return true;
		} catch (Exception e) {
			log.error("Cannot set sound provider type to " + type, e);
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
		if(container != null) container.setTargetFrameRate(targetFPS);
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
