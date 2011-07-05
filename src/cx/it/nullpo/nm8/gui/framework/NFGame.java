package cx.it.nullpo.nm8.gui.framework;

import java.io.Serializable;

import cx.it.nullpo.nm8.neuro.plugin.NEUROPlugin;

/**
 * NullpoMino Framework (NF) - NFGame<br>
 * Interface for main game app
 */
public interface NFGame extends Serializable, NEUROPlugin {
	/**
	 * Init the game
	 * @param sys NFSystem
	 */
	public void init(NFSystem sys);

	/**
	 * Update the game
	 * @param sys NFSystem
	 * @param delta Time elapsed from the last execution
	 */
	public void update(NFSystem sys, long delta);

	/**
	 * Render the screen
	 * @param sys NFSystem
	 * @param g NFGraphics to draw things on screen
	 */
	public void render(NFSystem sys, NFGraphics g);

	/**
	 * Called when the game is closed
	 * @param sys NFSystem
	 */
	public void onExit(NFSystem sys);
}
