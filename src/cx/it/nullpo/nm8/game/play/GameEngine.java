package cx.it.nullpo.nm8.game.play;

import java.io.Serializable;

import cx.it.nullpo.nm8.game.component.Field;

/**
 * GameEngine: Manager of the each GamePlay. Unlike previous versions of NullpoMino, there are not much here.
 */
public class GameEngine implements Serializable {
	/** Serial version ID */
	private static final long serialVersionUID = -1983896978679274559L;

	/** Default duration of Ready->Go (Miliseconds) */
	public static final long DEFAULT_READY_START = 0, DEFAULT_READY_END = 499,
							 DEFAULT_GO_START = 500, DEFAULT_GO_END = 1000;

	/** Default duration of Ready->Go (Frames) */
	public static final long F_DEFAULT_READY_START = 0, F_DEFAULT_READY_END = 29,
							 F_DEFAULT_GO_START = 30, F_DEFAULT_GO_END = 60;

	/** GameManager: Owner of this GameEngine */
	public GameManager owner;

	/** Engine ID (0=1st team field) */
	public int engineID;

	/** Field: The playfield */
	public Field field;

	/** GamePlay: Where the most actions take place */
	public GamePlay[] gamePlay;

	/** true if the game is active */
	public boolean gameActive;

	/** true if the timer is active */
	public boolean timerActive;

	/** true if the game is started (It will not change back to false until the game is reset) */
	public boolean gameStarted;

	/** Global timer for replay */
	public long replayTimer;

	/** Duration of Ready->Go */
	public long readyStart, readyEnd, goStart, goEnd;

	/**
	 * Constructor
	 */
	public GameEngine() {
	}

	/**
	 * Constructor
	 * @param owner GameManager
	 * @param playerID Engine ID
	 */
	public GameEngine(GameManager owner, int engineID) {
		this.owner = owner;
		this.engineID = engineID;
	}

	/**
	 * Init
	 */
	public void init() {
		field = new Field();
		gameActive = false;
		timerActive = false;
		gameStarted = false;
		replayTimer = 0;

		if(!owner.isFrameBasedTimer()) {
			readyStart = DEFAULT_READY_START;
			readyEnd = DEFAULT_READY_END;
			goStart = DEFAULT_GO_START;
			goEnd = DEFAULT_GO_END;
		} else {
			readyStart = F_DEFAULT_READY_START;
			readyEnd = F_DEFAULT_READY_END;
			goStart = F_DEFAULT_GO_START;
			goEnd = F_DEFAULT_GO_END;
		}

		gamePlay = new GamePlay[owner.getNumberOfPlayersForEachEngine()];
		for(int i = 0; i < gamePlay.length; i++) gamePlay[i] = new GamePlay(this, i);
		for(int i = 0; i < gamePlay.length; i++) gamePlay[i].init();
	}

	/**
	 * Start game
	 */
	public void start() {
		gameActive = true;

		for(int i = 0; i < gamePlay.length; i++) {
			gamePlay[i].start();
		}
	}

	/**
	 * Update game
	 * @param runMsec Milliseconds elapsed from the last execution, or 1 if using frame-based timer
	 */
	public void update(long runMsec) {
		if(gameActive) replayTimer += runMsec;

		for(int i = 0; i < gamePlay.length; i++) {
			gamePlay[i].update(runMsec);
		}
	}

	/**
	 * Get specific GamePlay
	 * @param playerID Player ID
	 * @return GamePlay
	 */
	public GamePlay getGamePlay(int playerID) {
		return gamePlay[playerID];
	}
}
