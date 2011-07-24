package cx.it.nullpo.nm8.game.play;

import java.io.Serializable;

import cx.it.nullpo.nm8.game.subsystem.mode.GameMode;

/**
 * GameManager: The container of the game
 */
public class GameManager implements Serializable {
	/** Serial version ID */
	private static final long serialVersionUID = 4457370420853602694L;

	/** Major version */
	public static final float VERSION_MAJOR = 8.0f;

	/** Minor version */
	public static final int VERSION_MINOR = 0;

	/** Development-build flag (false:Release-build, true:Dev-build) */
	public static final boolean DEV_BUILD = true;

	/** Constants of game style */
	public static final int GAMESTYLE_TETROMINO = 0,
							GAMESTYLE_AVALANCHE = 1,
							GAMESTYLE_PHYSICIAN = 2,
							GAMESTYLE_SPF = 3;

	/** Max number of game style */
	public static final int MAX_GAMESTYLE = 4;

	/** Game style names */
	public static final String[] GAMESTYLE_NAMES = {"TETROMINO", "AVALANCHE", "PHYSICIAN", "SPF"};

	/** The game mode */
	public GameMode gameMode;

	/** Game loop time */
	public long gameLoopTime;

	/** Max game loop time for frame-based timer */
	public long frameBasedLoopTime = 16666666;

	/** GameEngine: This is where the most action takes place. */
	public GameEngine[] engine;

	/**
	 * Get major version
	 * @return Major version
	 */
	public static float getVersionMajor() {
		return VERSION_MAJOR;
	}

	/**
	 * Get minor version
	 * @return Minor version
	 */
	public static int getVersionMinor() {
		return VERSION_MINOR;
	}

	/**
	 * Get version information as String
	 * @return Version information
	 */
	public static String getVersionString() {
		return VERSION_MAJOR + "." + VERSION_MINOR + (DEV_BUILD ? "D" : "");
	}

	/**
	 * Is this development build?
	 * @return true if dev build
	 */
	public static boolean isDevBuild() {
		return DEV_BUILD;
	}

	/**
	 * Get build type as string
	 * @return Build type as String
	 */
	public static String getBuildTypeString() {
		return DEV_BUILD ? "Development" : "Release";
	}

	/**
	 * Get build type name
	 * @param type Build type (false:Release true:Development)
	 * @return Build type as String
	 */
	public static String getBuildTypeString(boolean type) {
		return type ? "Development" : "Release";
	}

	/**
	 * Constructor (Use a dummy game mode)
	 */
	public GameManager() {
		gameMode = new GameMode();
	}

	/**
	 * Constructor
	 * @param mode Game mode
	 */
	public GameManager(GameMode mode) {
		gameMode = mode;
	}

	/**
	 * Init
	 */
	public void init() {
		// Game manager init
		gameMode.modeInit(this);
		gameLoopTime = 0;
		engine = new GameEngine[getNumberOfEngines()];
		for(int i = 0; i < engine.length; i++) engine[i] = new GameEngine(this, i);
		for(int i = 0; i < engine.length; i++) engine[i].init();
	}

	/**
	 * Start game
	 */
	public void start() {
		for(int i = 0; i < engine.length; i++) {
			engine[i].start();
		}
	}

	public void stop() {
		for (int i = 0; i < engine.length; i++) {
			// TODO stop engines
		}
	}

	/**
	 * Update game
	 * @param runMsec Nanoseconds elapsed from the last execution
	 */
	public void update(long runMsec) {
		gameLoopTime += runMsec;

		while(gameLoopTime >= frameBasedLoopTime) {
			gameLoopTime -= frameBasedLoopTime;

			for(int i = 0; i < engine.length; i++) {
				engine[i].update();
			}
		}
	}

	/**
	 * Get current game style
	 * @return Game style ID
	 */
	public int getGameStyle() {
		return gameMode.getGameStyle();
	}

	/**
	 * Get number of GameEngine
	 * @return Number of GameEngine
	 */
	public int getNumberOfEngines() {
		return gameMode.getNumberOfEngines();
	}

	/**
	 * Get number of players (GamePlay) for each GameEngine
	 * @return Number of players (GamePlay) for each GameEngine
	 */
	public int getNumberOfPlayersForEachEngine() {
		return gameMode.getNumberOfPlayersForEachEngine();
	}

	/**
	 * Get specific GameEngine
	 * @param engineID Engine ID
	 * @return GameEngine
	 */
	public GameEngine getGameEngine(int engineID) {
		return engine[engineID];
	}

	/**
	 * Get specific GamePlay
	 * @param engineID Engine ID
	 * @param playerID Player ID
	 * @return GamePlay
	 */
	public GamePlay getGamePlay(int engineID, int playerID) {
		return engine[engineID].gamePlay[playerID];
	}
}
