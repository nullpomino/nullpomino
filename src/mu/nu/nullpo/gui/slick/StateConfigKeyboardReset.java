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
	@Override
	public void init(GameContainer arg0, StateBasedGame arg1) throws SlickException {
	}

	/*
	 * Draw the screen
	 */
	@Override
	protected void renderImpl(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		// Background
		g.drawImage(ResourceHolderSlick.imgMenu, 0, 0);

		// Menu
		NormalFontSlick.printFontGrid(1, 1, "KEYBOARD RESET (" + (player+1) + "P)", NormalFontSlick.COLOR_ORANGE);

		NormalFontSlick.printFontGrid(1, 3, "RESET SETTINGS TO...", NormalFontSlick.COLOR_GREEN);

		NormalFontSlick.printFontGrid(1, 4 + cursor, "b", NormalFontSlick.COLOR_RED);

		NormalFontSlick.printFontGrid(2, 4, "BLOCKBOX STYLE (DEFAULT)", (cursor == 0));
		NormalFontSlick.printFontGrid(2, 5, "GUIDELINE STYLE", (cursor == 1));
		NormalFontSlick.printFontGrid(2, 6, "NULLPOMINO CLASSIC STYLE", (cursor == 2));
	}

	/*
	 * Decide
	 */
	@Override
	protected boolean onDecide(GameContainer container, StateBasedGame game, int delta) {
		ResourceHolderSlick.soundManager.play("decide");
		GameKeySlick.gamekey[player].loadDefaultKeymap(cursor);
		GameKeySlick.gamekey[player].saveConfig(NullpoMinoSlick.propConfig);
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
