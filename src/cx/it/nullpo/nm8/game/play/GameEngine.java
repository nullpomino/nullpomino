package cx.it.nullpo.nm8.game.play;

import java.io.Serializable;

import cx.it.nullpo.nm8.game.component.Field;

/**
 * GameEngine: Manager of the each GamePlay. Unlike previous versions of NullpoMino, there are not much here.
 */
public class GameEngine implements Serializable {
	/** Serial version ID */
	private static final long serialVersionUID = -1983896978679274559L;

	/** Default duration of Ready->Go */
	public static final long DEFAULT_READY_START = 0, DEFAULT_READY_END = 29,
							 DEFAULT_GO_START = 30, DEFAULT_GO_END = 60;

	/** GameOver type constants */
	public static int GAMEOVER_NONE = 0, GAMEOVER_LOSE = 1, GAMEOVER_DRAW = 2, GAMEOVER_WIN = 3;

	/** Death type constants */
	public static int DEATH_NONE = 0, DEATH_BLOCKOUT = 1, DEATH_LOCKOUT = 2, DEATH_TIMEOUT = 3;

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

	/** true if game is over */
	public boolean flagGameOver;

	/** GameOver type */
	public int gameoverType;

	/** Death type */
	public int deathType;

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
		flagGameOver = false;
		gameoverType = GAMEOVER_NONE;
		deathType = DEATH_NONE;

		replayTimer = 0;

		readyStart = DEFAULT_READY_START;
		readyEnd = DEFAULT_READY_END;
		goStart = DEFAULT_GO_START;
		goEnd = DEFAULT_GO_END;

		owner.gameMode.engineInit(this);

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
	 */
	public void update() {
		if(gameActive) {
			replayTimer++;

			for(int i = 0; i < gamePlay.length; i++) {
				gamePlay[i].update();
			}
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

	/**
	 * Signals Game Over
	 * @param type GameOver type
	 */
	public void signalGameOver(int type) {
		signalGameOver(type, DEATH_NONE);
	}

	/**
	 * Signals Game Over
	 * @param type GameOver type
	 * @param death Death type
	 */
	public void signalGameOver(int type, int death) {
		if(!flagGameOver) {
			flagGameOver = true;
			gameActive = false;
			timerActive = false;
			gameoverType = type;
			deathType = death;
			owner.gameMode.signalGameOver(this, type, death);
		}
	}
}
