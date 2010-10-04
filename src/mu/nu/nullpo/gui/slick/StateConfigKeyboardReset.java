package mu.nu.nullpo.gui.slick;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Keyboard Reset menu
 */
public class StateConfigKeyboardReset extends DummyMenuChooseState {
	/** This state's ID */
	public static final int ID = 17;

	/** Player number */
	public int player = 0;

	/**
	 * Constructor
	 */
	public StateConfigKeyboardReset() {
		super();
		maxCursor = 2;
		minChoiceY = 4;
	}

	/*
	 * Fetch this state's ID
	 */
	@Override
	public int getID() {
		return ID;
	}

	/*
	 * State initialization
	 */
	public void init(GameContainer arg0, StateBasedGame arg1) throws SlickException {
	}

	/*
	 * Draw the screen
	 */
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		// Background
		g.drawImage(ResourceHolder.imgMenu, 0, 0);

		// Menu
		NormalFont.printFontGrid(1, 1, "KEYBOARD RESET (" + (player+1) + "P)", NormalFont.COLOR_ORANGE);

		NormalFont.printFontGrid(1, 3, "RESET SETTINGS TO...", NormalFont.COLOR_GREEN);

		NormalFont.printFontGrid(1, 4 + cursor, "b", NormalFont.COLOR_RED);

		NormalFont.printFontGrid(2, 4, "BLOCKBOX STYLE (DEFAULT)", (cursor == 0));
		NormalFont.printFontGrid(2, 5, "GUIDELINE STYLE", (cursor == 1));
		NormalFont.printFontGrid(2, 6, "NULLPOMINO CLASSIC STYLE", (cursor == 2));

		super.render(container, game, g);
	}

	/*
	 * Decide
	 */
	@Override
	protected boolean onDecide(GameContainer container, StateBasedGame game, int delta) {
		ResourceHolder.soundManager.play("decide");
		GameKey.gamekey[player].loadDefaultKeymap(cursor);
		GameKey.gamekey[player].saveConfig(NullpoMinoSlick.propConfig);
		NullpoMinoSlick.saveConfig();
		game.enterState(StateConfigMainMenu.ID);
		return false;
	}

	/*
	 * Cancel
	 */
	@Override
	protected boolean onCancel(GameContainer container, StateBasedGame game, int delta) {
		game.enterState(StateConfigMainMenu.ID);
		return false;
	}
}
