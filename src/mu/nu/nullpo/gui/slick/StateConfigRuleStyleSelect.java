package mu.nu.nullpo.gui.slick;

import mu.nu.nullpo.game.play.GameEngine;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Style select menu
 */
public class StateConfigRuleStyleSelect extends DummyMenuChooseState {
	/** This state's ID */
	public static final int ID = 15;

	/** Player number */
	protected int player = 0;

	public StateConfigRuleStyleSelect() {
		super();
		maxCursor = GameEngine.MAX_GAMESTYLE - 1;
		minChoiceY = 3;
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
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
	}

	/*
	 * Draw the screen
	 */
	@Override
	protected void renderImpl(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		// Background
		g.drawImage(ResourceHolder.imgMenu, 0, 0);

		// Menu
		NormalFont.printFontGrid(1, 1, "SELECT " + (player+1) + "P STYLE", NormalFont.COLOR_ORANGE);

		NormalFont.printFontGrid(1, 3 + cursor, "b", NormalFont.COLOR_RED);

		for(int i = 0; i < GameEngine.MAX_GAMESTYLE; i++) {
			NormalFont.printFontGrid(2, 3 + i, GameEngine.GAMESTYLE_NAMES[i], (cursor == i));
		}
	}

	/*
	 * Decide
	 */
	@Override
	protected boolean onDecide(GameContainer container, StateBasedGame game, int delta) {
		ResourceHolder.soundManager.play("decide");
		NullpoMinoSlick.stateConfigRuleSelect.player = player;
		NullpoMinoSlick.stateConfigRuleSelect.style = cursor;
		game.enterState(StateConfigRuleSelect.ID);
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
