package cx.it.nullpo.nm8.game.subsystem.mode;

import java.io.Serializable;

import cx.it.nullpo.nm8.game.play.GameEngine;
import cx.it.nullpo.nm8.game.play.GameManager;
import cx.it.nullpo.nm8.game.play.GamePlay;

/**
 * Game mode base class
 */
public class GameMode implements Serializable {
	private static final long serialVersionUID = -5538212077373500911L;

	/**
	 * Get mode name.
	 * @return Mode name
	 */
	public String getName() {
		return "DUMMY";
	}

	/**
	 * Get game style.
	 * @return Game style of this mode (0:Tetromino, 1:Avalanche, 2:Physician, 3:SPF)
	 */
	public int getGameStyle() {
		return GameManager.GAMESTYLE_TETROMINO;
	}

	/**
	 * Get number of GameEngine
	 * @return Number of GameEngine
	 */
	public int getNumberOfEngines() {
		return 1;
	}

	/**
	 * Get number of players (GamePlay) for each GameEngine
	 * @return Number of players (GamePlay) for each GameEngine
	 */
	public int getNumberOfPlayersForEachEngine() {
		return 1;
	}

	/**
	 * Initialization of game mode. Executed before the game screen appears.
	 * @param manager GameManager that owns this mode
	 */
	public void modeInit(GameManager manager) {
	}

	/**
	 * Initialization for each game engine.
	 * @param play GameEngine
	 */
	public void engineInit(GameEngine engine) {
	}

	/**
	 * Initialization for each player.
	 * @param play GamePlay
	 */
	public void playerInit(GamePlay play) {
	}
}
