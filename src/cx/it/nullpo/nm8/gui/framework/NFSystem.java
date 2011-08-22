package cx.it.nullpo.nm8.gui.framework;

import java.awt.Font;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;

import cx.it.nullpo.nm8.neuro.core.NEURO;
import cx.it.nullpo.nm8.neuro.light.NEUROLight;

/**
 * NullpoMino Framework (NF) - NFSystem<br>
 * Abstract class for main system
 */
public abstract class NFSystem implements Serializable {
	private static final long serialVersionUID = -896391818527202711L;

	/** Sound Provider Type: Java Sound */
	public static final int SOUND_PROVIDER_JAVASOUND = 0;
	/** Sound Provider Type: OpenAL */
	public static final int SOUND_PROVIDER_OPENAL = 1;

	/** NEURO: The event framework */
	protected NEURO neuro;

	/** NFGame: The actual game programs */
	protected NFGame game;

	/** Fullscreen flag */
	protected boolean fullscreen;

	/** Screen width and height */
	protected int screenWidth, screenHeight;

	/** Original screen width and height; Used for scaling the screen */
	protected int originalScreenWidth, originalScreenHeight;

	/** Try to keep screen's aspect ratio */
	protected boolean keepAspectRatio;

	/** Command line arguments */
	protected String[] cmdlineArgs;

	/** Current target FPS */
	protected int targetFPS = -1;

	/** Sound fx volume */
	protected float soundVolume = 1f;

	/** Music volume */
	protected float musicVolume = 1f;

	/**
	 * Empty constructor - should not be used
	 */
	public NFSystem() {
	}

	/**
	 * Constructor
	 * @param game NFGame with actual game programs
	 * @param fullscreen true if you want fullscreen. But not all system supports it.
	 * @param width Screen width
	 * @param height Screen height
	 */
	public NFSystem(NFGame game, boolean fullscreen, int width, int height) {
		this.game = game;
		this.fullscreen = fullscreen;
		this.screenWidth = width;
		this.screenHeight = height;
		this.originalScreenWidth = width;
		this.originalScreenHeight = height;
	}

	/**
	 * Constructor
	 * @param game NFGame with actual game programs
	 * @param fullscreen true if you want fullscreen. But not all system supports it.
	 * @param width Screen width
	 * @param height Screen height
	 * @param oWidth Original (normal) screen width
	 * @param oHeight Original (normal) screen height
	 */
	public NFSystem(NFGame game, boolean fullscreen, int width, int height, int oWidth, int oHeight) {
		this.game = game;
		this.fullscreen = fullscreen;
		this.screenWidth = width;
		this.screenHeight = height;
		this.originalScreenWidth = oWidth;
		this.originalScreenHeight = oHeight;
	}

	/**
	 * Constructor
	 * @param game NFGame with actual game programs
	 * @param fullscreen true if you want fullscreen. But not all system supports it.
	 * @param width Screen width
	 * @param height Screen height
	 * @param oWidth Original (normal) screen width
	 * @param oHeight Original (normal) screen height
	 * @param keepaspectratio Try to keep screen's aspect ratio
	 */
	public NFSystem(NFGame game, boolean fullscreen, int width, int height, int oWidth, int oHeight,
					boolean keepaspectratio) {
		this.game = game;
		this.fullscreen = fullscreen;
		this.screenWidth = width;
		this.screenHeight = height;
		this.originalScreenWidth = oWidth;
		this.originalScreenHeight = oHeight;
		this.keepAspectRatio = keepaspectratio;
	}

	/**
	 * Constructor
	 * @param game NFGame with actual game programs
	 * @param fullscreen true if you want fullscreen. But not all system supports it.
	 * @param width Screen width
	 * @param height Screen height
	 * @param oWidth Original (normal) screen width
	 * @param oHeight Original (normal) screen height
	 * @param keepaspectratio Try to keep screen's aspect ratio
	 * @param cmdArgs Command line arguments
	 */
	public NFSystem(NFGame game, boolean fullscreen, int width, int height, int oWidth, int oHeight,
					boolean keepaspectratio, String[] cmdArgs) {
		this.game = game;
		this.fullscreen = fullscreen;
		this.screenWidth = width;
		this.screenHeight = height;
		this.originalScreenWidth = oWidth;
		this.originalScreenHeight = oHeight;
		this.keepAspectRatio = keepaspectratio;
		this.cmdlineArgs = cmdArgs;
	}

	/**
	 * Get NEURO framework
	 */
	public NEURO getNEURO() {
		return neuro;
	}

	/**
	 * Get NFGame with actual game programs
	 */
	public NFGame getNFGame() {
		return game;
	}

	/**
	 * Get rendering system name (i.e. "Swing")
	 */
	abstract public String getSystemName();

	/**
	 * Initialise the system
	 * @throws Exception Indicates a failure to initialise the system
	 */
	public void init() throws Exception {
		neuro = NEUROLight.create(this);
		game.init(neuro);
	}

	/**
	 * Start running the game
	 * @throws Exception Indicates a failure to initialise the system
	 */
	abstract public void start() throws Exception;

	/**
	 * Update the current game
	 * @param delta Time elapsed from the last execution
	 */
	public void update(long delta) {
		neuro.update(delta);
	}

	/**
	 * Render the current game
	 */
	public void render() {
		neuro.draw(getGraphics());
	}

	/**
	 * Cause the game to exit and shutdown cleanly
	 */
	abstract public void exit();

	/**
	 * Check if the game window has focus
	 * @return True if the game window has focus
	 */
	abstract public boolean hasFocus();

	/**
	 * Get NFKeyboard for keyboard access
	 */
	abstract public NFKeyboard getKeyboard();

	/**
	 * Get NFMouse for mouse access
	 */
	abstract public NFMouse getMouse();

	/**
	 * Get the graphics context used by this container
	 */
	abstract public NFGraphics getGraphics();

	/**
	 * Create an empty image
	 * @param width Width
	 * @param height Height
	 * @return An empty image
	 */
	abstract public NFImage createImage(int width, int height);

	/**
	 * Load an image from specified filename
	 * @param filename Filename
	 * @return Image
	 * @throws IOException When load fails
	 */
	abstract public NFImage loadImage(String filename) throws IOException;

	/**
	 * Load an image from a URL
	 * @param url URL
	 * @return Image
	 * @throws IOException When load fails
	 */
	public NFImage loadImage(URL url) throws IOException {
		return loadImage(url.getPath());
	}

	/**
	 * Create an NFFont by using an AWT font
	 * @param font AWT font
	 * @return NFFont (or null if this system doesn't support AWT font loading)
	 */
	public NFFont loadFont(Font font) {
		return null;
	}

	/**
	 * Create an NFFont by using an AWT font
	 * @param font AWT font
	 * @param size Point size
	 * @param bold True if the font should be rendered in bold typeface
	 * @param italic True if the font should be rendered in italic typeface
	 * @return NFFont (or null if this system doesn't support AWT font loading)
	 */
	public NFFont loadFont(Font font, int size, boolean bold, boolean italic) {
		return null;
	}

	/**
	 * Load an NFFont from a TTF file
	 * @param filename Filename of TTF file
	 * @return NFFont (or null if this system doesn't support AWT font loading)
	 * @throws IOException When load fails
	 */
	public NFFont loadFont(String filename) throws IOException {
		return null;
	}

	/**
	 * Load an NFFont from a TTF file
	 * @param filename Filename of TTF file
	 * @param size Point size
	 * @param bold True if the font should be rendered in bold typeface
	 * @param italic True if the font should be rendered in italic typeface
	 * @return NFFont (or null if this system doesn't support AWT font loading)
	 * @throws IOException When load fails
	 */
	public NFFont loadFont(String filename, int size, boolean bold, boolean italic) throws IOException {
		return null;
	}

	/**
	 * Load an NFFont from a URL
	 * @param url URL
	 * @return NFFont (or null if this system doesn't support AWT font loading)
	 * @throws IOException When load fails
	 */
	public NFFont loadFont(URL url) throws IOException {
		return loadFont(url.getPath());
	}

	/**
	 * Load an NFFont from a URL
	 * @param url URL
	 * @param size Point size
	 * @param bold True if the font should be rendered in bold typeface
	 * @param italic True if the font should be rendered in italic typeface
	 * @return NFFont (or null if this system doesn't support AWT font loading)
	 * @throws IOException When load fails
	 */
	public NFFont loadFont(URL url, int size, boolean bold, boolean italic) throws IOException {
		return loadFont(url.getPath(), size, bold, italic);
	}

	/**
	 * Check if this system has font loading/rendering support
	 * @return True if this system can load and render fonts
	 */
	public boolean isFontSupported() {
		return false;
	}

	/**
	 * Get NFJoystickManager for joystick access. Don't forget to call initJoystick after you obtain it!
	 * @return NFJoystickManager, or null if the system doesn't have the joystick suppot
	 */
	public NFJoystickManager getJoystickManager() {
		return null;
	}

	/**
	 * Check if this system has joystick support
	 * @return True if this system can use joysticks
	 */
	public boolean isJoystickSupported() {
		return false;
	}

	/**
	 * Load a sound effect
	 * @param filename Filename
	 * @return NFSound (or null if this system doesn't have an sound effect support)
	 * @throws IOException When load fails
	 * @throws IllegalStateException If no more sound effects can be loaded
	 */
	public NFSound loadSound(String filename) throws IOException {
		return null;
	}

	/**
	 * Load a sound effect from a URL
	 * @param url URL
	 * @return  NFSound (or null if this system doesn't have an sound effect support)
	 * @throws IOException When load fails
	 * @throws IllegalStateException If no more sound effects can be loaded
	 */
	public NFSound loadSound(URL url) throws IOException {
		return loadSound(url.getPath());
	}

	/**
	 * Check if this system has sound effect support
	 * @return True if this system can load and play sound effects
	 */
	public boolean isSoundSupported() {
		return false;
	}

	/**
	 * Check if specific sound provider is supported in this system
	 * @param type Sound provider type
	 * @return true if specific sound provider is supported in this system
	 */
	public boolean isSoundProviderTypeSupported(int type) {
		return false;
	}

	/**
	 * Set sound provider type
	 * @param type Sound provider type
	 * @return true if successful
	 */
	public boolean setSoundProviderType(int type) {
		return false;
	}

	/**
	 * Get currently using sound provider
	 * @return Currently using sound provider (default is Java Sound)
	 */
	public NFSoundProvider getCurrentSoundProvider() {
		return null;
	}

	/**
	 * Load a music
	 * @param filename Filename
	 * @return NFMusic (or null if this system doesn't have an music support)
	 * @throws IOException When load fails
	 */
	public NFMusic loadMusic(String filename) throws IOException {
		return loadMusic(filename, false);
	}

	/**
	 * Load a music
	 * @param filename Filename
	 * @param stream If true, and if possible, streaming will be used
	 * @return NFMusic (or null if this system doesn't have an music support)
	 * @throws IOException When load fails
	 */
	public NFMusic loadMusic(String filename, boolean stream) throws IOException {
		return null;
	}

	/**
	 * Load a music
	 * @param url URL
	 * @return NFMusic (or null if this system doesn't have an music support)
	 * @throws IOException When load fails
	 */
	public NFMusic loadMusic(URL url) throws IOException {
		return loadMusic(url.getPath());
	}

	/**
	 * Load a music
	 * @param url URL
	 * @param stream If true, and if possible, streaming will be used
	 * @return NFMusic (or null if this system doesn't have an music support)
	 * @throws IOException When load fails
	 */
	public NFMusic loadMusic(URL url, boolean stream) throws IOException {
		return loadMusic(url.getPath(), stream);
	}

	/**
	 * Check if this system has music support
	 * @return True if this system can load and play music
	 */
	public boolean isMusicSupported() {
		return false;
	}

	/**
	 * Set the volume for sound fx
	 * @param volume Sound fx volume (1f is 100%)
	 */
	public void setSoundVolume(float volume) {
		soundVolume = volume;
	}

	/**
	 * Get the volume for sound fx
	 * @return Sound fx volume (1f is 100%)
	 */
	public float getSoundVolume() {
		return soundVolume;
	}

	/**
	 * Set the volume for music
	 * @param volume Music volume (1f is 100%)
	 */
	public void setMusicVolume(float volume) {
		musicVolume = volume;
	}

	/**
	 * Get the volume for music
	 * @return Music volume (1f is 100%)
	 */
	public float getMusicVolume() {
		return musicVolume;
	}

	/**
	 * Set the target fps we're hoping to get. The exact logic of FPS cap may vary between renderers.
	 * @param fps The target fps we're hoping to get (-1 for no FPS-cap)
	 */
	public void setTargetFPS(int fps) {
		targetFPS = fps;
		if(fps == 0) fps = -1;
	}

	/**
	 * Get the target fps we're hoping to get
	 * @return The target fps we're hoping to get (-1 for no FPS-cap)
	 */
	public int getTargetFPS() {
		return targetFPS;
	}

	/**
	 * Get the current recorded FPS (frames per second)<br>
	 * The exact logic of FPS calculation may vary between renderers.
	 * @return The current FPS
	 */
	public float getFPS() {
		return 0f;
	}

	/**
	 * Check if the game window is fullscreen
	 * @return True if the game window is fullscreen
	 */
	public boolean isFullscreen() {
		return fullscreen;
	}

	/**
	 * Get screen width
	 * @return Screen width
	 */
	public int getWidth() {
		return screenWidth;
	}

	/**
	 * Get screen height
	 * @return Screen height
	 */
	public int getHeight() {
		return screenHeight;
	}

	/**
	 * Get original (normal) screen width
	 * @return Original screen width
	 */
	public int getOriginalWidth() {
		return originalScreenWidth;
	}

	/**
	 * Get original (normal) screen height
	 * @return Original screen height
	 */
	public int getOriginalHeight() {
		return originalScreenHeight;
	}

	/**
	 * Check if game window scaling is used
	 * @return True if game window scaling is used
	 */
	public boolean isGameWindowScalingUsed() {
		if((screenWidth != originalScreenWidth) || (screenHeight != originalScreenHeight))
			return true;

		return false;
	}

	/**
	 * Check if maintaining the aspect ratio
	 * @return True if we are trying to keep the aspect ratio
	 */
	public boolean isKeepAspectRatio() {
		return keepAspectRatio;
	}

	/**
	 * Get command line arguments
	 * @return Command line arguments
	 */
	public String[] getCommandLineArgs() {
		return cmdlineArgs;
	}

	/**
	 * Set window title
	 * @param title Window title
	 */
	public void setWindowTitle(String title) {
	}

	/**
	 * Get window title
	 * @return Window title
	 */
	public String getWindowTitle() {
		return "";
	}
}
