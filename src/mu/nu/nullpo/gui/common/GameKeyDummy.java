package mu.nu.nullpo.gui.common;

import mu.nu.nullpo.game.component.Controller;
import mu.nu.nullpo.util.CustomProperties;

public class GameKeyDummy {

	/** Button number constants */
	public static final int BUTTON_UP = 0, BUTTON_DOWN = 1, BUTTON_LEFT = 2, BUTTON_RIGHT = 3, BUTTON_A = 4, BUTTON_B = 5, BUTTON_C = 6,
			BUTTON_D = 7, BUTTON_E = 8, BUTTON_F = 9, BUTTON_QUIT = 10, BUTTON_PAUSE = 11, BUTTON_GIVEUP = 12, BUTTON_RETRY = 13,
			BUTTON_FRAMESTEP = 14, BUTTON_SCREENSHOT = 15;

	/** Max button number */
	public static final int MAX_BUTTON = 16;

	/** Key code (ingame) */
	public int[] keymap;

	/** Key code (menu) */
	public int[] keymapNav;

	/** Joystick button number */
	public int[] buttonmap;

	/** Joystick direction key border */
	public int joyBorder;

	/** Player ID */
	public int player;

	/** Button input flag and length */
	protected int inputstate[];

	/** Button input flag */
	protected boolean[] pressstate;

	/**
	 * Default constructor
	 */
	protected GameKeyDummy() {
		this(0);
	}

	/**
	 * Player numberを指定できるConstructor
	 * @param pl Player number
	 */
	protected GameKeyDummy(int pl) {
		keymap = new int[MAX_BUTTON];
		keymapNav = new int[MAX_BUTTON];
		buttonmap = new int[MAX_BUTTON];
		joyBorder = 0;
		for(int i = 0; i < buttonmap.length; i++) buttonmap[i] = -1;
		player = pl;
		inputstate = new int[MAX_BUTTON];
		pressstate = new boolean[MAX_BUTTON];
	}

	/**
	 * Clear button input state
	 */
	public void clear() {
		for(int i = 0; i < MAX_BUTTON; i++) {
			inputstate[i] = 0;
		}
	}

	/**
	 *  buttonが1 frame だけ押されているか判定
	 * @param key Button number
	 * @return 押されていたらtrue
	 */
	public boolean isPushKey(int key) {
		return (inputstate[key] == 1);
	}

	/**
	 *  buttonが押されているか判定
	 * @param key Button number
	 * @return 押されていたらtrue
	 */
	public boolean isPressKey(int key) {
		return (inputstate[key] >= 1);
	}

	/**
	 * Menu でカーソルが動くかどうか判定
	 * @param key Button number
	 * @return カーソルが動くならtrue
	 */
	public boolean isMenuRepeatKey(int key) {
		if((inputstate[key] == 1) || ((inputstate[key] >= 25) && (inputstate[key] % 3 == 0)) || ((inputstate[key] >= 1) && isPressKey(BUTTON_C)))
			return true;

		return false;
	}

	/**
	 *  buttonを押している timeを取得
	 * @param key Button number
	 * @return  buttonを押している time (0なら押してない）
	 */
	public int getInputState(int key) {
		return inputstate[key];
	}

	/**
	 *  buttonを押している timeを強制変更
	 * @param key Button number
	 * @param state  buttonを押している time
	 */
	public void setInputState(int key, int state) {
		inputstate[key] = state;
	}

	/**
	 * Load settings
	 * @param prop Property file to read from
	 */
	public void loadConfig(CustomProperties prop) {
		// Keyboard - ingame
		keymap[BUTTON_UP] = prop.getProperty("key.p" + player + ".up", 0);
		keymap[BUTTON_DOWN] = prop.getProperty("key.p" + player + ".down", 0);
		keymap[BUTTON_LEFT] = prop.getProperty("key.p" + player + ".left", 0);
		keymap[BUTTON_RIGHT] = prop.getProperty("key.p" + player + ".right", 0);
		keymap[BUTTON_A] = prop.getProperty("key.p" + player + ".a", 0);
		keymap[BUTTON_B] = prop.getProperty("key.p" + player + ".b", 0);
		keymap[BUTTON_C] = prop.getProperty("key.p" + player + ".c", 0);
		keymap[BUTTON_D] = prop.getProperty("key.p" + player + ".d", 0);
		keymap[BUTTON_E] = prop.getProperty("key.p" + player + ".e", 0);
		keymap[BUTTON_F] = prop.getProperty("key.p" + player + ".f", 0);
		keymap[BUTTON_QUIT] = prop.getProperty("key.p" + player + ".quit", 0);
		keymap[BUTTON_PAUSE] = prop.getProperty("key.p" + player + ".pause", 0);
		keymap[BUTTON_GIVEUP] = prop.getProperty("key.p" + player + ".giveup", 0);
		keymap[BUTTON_RETRY] = prop.getProperty("key.p" + player + ".retry", 0);
		keymap[BUTTON_FRAMESTEP] = prop.getProperty("key.p" + player + ".framestep", 0);
		keymap[BUTTON_SCREENSHOT] = prop.getProperty("key.p" + player + ".screenshot", 0);

		// Keyboard - menu
		keymapNav[BUTTON_UP] = prop.getProperty("keynav.p" + player + ".up", keymap[BUTTON_UP]);
		keymapNav[BUTTON_DOWN] = prop.getProperty("keynav.p" + player + ".down", keymap[BUTTON_DOWN]);
		keymapNav[BUTTON_LEFT] = prop.getProperty("keynav.p" + player + ".left", keymap[BUTTON_LEFT]);
		keymapNav[BUTTON_RIGHT] = prop.getProperty("keynav.p" + player + ".right", keymap[BUTTON_RIGHT]);
		keymapNav[BUTTON_A] = prop.getProperty("keynav.p" + player + ".a", keymap[BUTTON_A]);
		keymapNav[BUTTON_B] = prop.getProperty("keynav.p" + player + ".b", keymap[BUTTON_B]);
		keymapNav[BUTTON_C] = prop.getProperty("keynav.p" + player + ".c", keymap[BUTTON_C]);
		keymapNav[BUTTON_D] = prop.getProperty("keynav.p" + player + ".d", keymap[BUTTON_D]);
		keymapNav[BUTTON_E] = prop.getProperty("keynav.p" + player + ".e", keymap[BUTTON_E]);
		keymapNav[BUTTON_F] = prop.getProperty("keynav.p" + player + ".f", keymap[BUTTON_F]);
		keymapNav[BUTTON_QUIT] = prop.getProperty("keynav.p" + player + ".quit", keymap[BUTTON_QUIT]);
		keymapNav[BUTTON_PAUSE] = prop.getProperty("keynav.p" + player + ".pause", keymap[BUTTON_PAUSE]);
		keymapNav[BUTTON_GIVEUP] = prop.getProperty("keynav.p" + player + ".giveup", keymap[BUTTON_GIVEUP]);
		keymapNav[BUTTON_RETRY] = prop.getProperty("keynav.p" + player + ".retry", keymap[BUTTON_RETRY]);
		keymapNav[BUTTON_FRAMESTEP] = prop.getProperty("keynav.p" + player + ".framestep", keymap[BUTTON_FRAMESTEP]);
		keymapNav[BUTTON_SCREENSHOT] = prop.getProperty("keynav.p" + player + ".screenshot", keymap[BUTTON_SCREENSHOT]);

		// Joystick
		//buttonmap[BUTTON_UP] = prop.getProperty("button.p" + player + ".up", 0);
		//buttonmap[BUTTON_DOWN] = prop.getProperty("button.p" + player + ".down", 0);
		//buttonmap[BUTTON_LEFT] = prop.getProperty("button.p" + player + ".left", 0);
		//buttonmap[BUTTON_RIGHT] = prop.getProperty("button.p" + player + ".right", 0);
		buttonmap[BUTTON_A] = prop.getProperty("button.p" + player + ".a", -1);
		buttonmap[BUTTON_B] = prop.getProperty("button.p" + player + ".b", -1);
		buttonmap[BUTTON_C] = prop.getProperty("button.p" + player + ".c", -1);
		buttonmap[BUTTON_D] = prop.getProperty("button.p" + player + ".d", -1);
		buttonmap[BUTTON_E] = prop.getProperty("button.p" + player + ".e", -1);
		buttonmap[BUTTON_F] = prop.getProperty("button.p" + player + ".f", -1);
		buttonmap[BUTTON_QUIT] = prop.getProperty("button.p" + player + ".quit", -1);
		buttonmap[BUTTON_PAUSE] = prop.getProperty("button.p" + player + ".pause", -1);
		buttonmap[BUTTON_GIVEUP] = prop.getProperty("button.p" + player + ".giveup", -1);
		buttonmap[BUTTON_RETRY] = prop.getProperty("button.p" + player + ".retry", -1);
		buttonmap[BUTTON_FRAMESTEP] = prop.getProperty("button.p" + player + ".framestep", -1);
		buttonmap[BUTTON_SCREENSHOT] = prop.getProperty("button.p" + player + ".screenshot", -1);

		joyBorder = prop.getProperty("joyBorder.p" + player, 0);
	}

	/**
	 * Save settings
	 * @param prop Property file to save to
	 */
	public void saveConfig(CustomProperties prop) {
		// Keyboard - ingame
		prop.setProperty("key.p" + player + ".up", keymap[BUTTON_UP]);
		prop.setProperty("key.p" + player + ".down", keymap[BUTTON_DOWN]);
		prop.setProperty("key.p" + player + ".left", keymap[BUTTON_LEFT]);
		prop.setProperty("key.p" + player + ".right", keymap[BUTTON_RIGHT]);
		prop.setProperty("key.p" + player + ".a", keymap[BUTTON_A]);
		prop.setProperty("key.p" + player + ".b", keymap[BUTTON_B]);
		prop.setProperty("key.p" + player + ".c", keymap[BUTTON_C]);
		prop.setProperty("key.p" + player + ".d", keymap[BUTTON_D]);
		prop.setProperty("key.p" + player + ".e", keymap[BUTTON_E]);
		prop.setProperty("key.p" + player + ".f", keymap[BUTTON_F]);
		prop.setProperty("key.p" + player + ".quit", keymap[BUTTON_QUIT]);
		prop.setProperty("key.p" + player + ".pause", keymap[BUTTON_PAUSE]);
		prop.setProperty("key.p" + player + ".giveup", keymap[BUTTON_GIVEUP]);
		prop.setProperty("key.p" + player + ".retry", keymap[BUTTON_RETRY]);
		prop.setProperty("key.p" + player + ".framestep", keymap[BUTTON_FRAMESTEP]);
		prop.setProperty("key.p" + player + ".screenshot", keymap[BUTTON_SCREENSHOT]);

		// Keyboard - menu
		prop.setProperty("keynav.p" + player + ".up", keymapNav[BUTTON_UP]);
		prop.setProperty("keynav.p" + player + ".down", keymapNav[BUTTON_DOWN]);
		prop.setProperty("keynav.p" + player + ".left", keymapNav[BUTTON_LEFT]);
		prop.setProperty("keynav.p" + player + ".right", keymapNav[BUTTON_RIGHT]);
		prop.setProperty("keynav.p" + player + ".a", keymapNav[BUTTON_A]);
		prop.setProperty("keynav.p" + player + ".b", keymapNav[BUTTON_B]);
		prop.setProperty("keynav.p" + player + ".c", keymapNav[BUTTON_C]);
		prop.setProperty("keynav.p" + player + ".d", keymapNav[BUTTON_D]);
		prop.setProperty("keynav.p" + player + ".e", keymapNav[BUTTON_E]);
		prop.setProperty("keynav.p" + player + ".f", keymapNav[BUTTON_F]);
		prop.setProperty("keynav.p" + player + ".quit", keymapNav[BUTTON_QUIT]);
		prop.setProperty("keynav.p" + player + ".pause", keymapNav[BUTTON_PAUSE]);
		prop.setProperty("keynav.p" + player + ".giveup", keymapNav[BUTTON_GIVEUP]);
		prop.setProperty("keynav.p" + player + ".retry", keymapNav[BUTTON_RETRY]);
		prop.setProperty("keynav.p" + player + ".framestep", keymapNav[BUTTON_FRAMESTEP]);
		prop.setProperty("keynav.p" + player + ".screenshot", keymapNav[BUTTON_SCREENSHOT]);

		// Joystick
		//prop.setProperty("button.p" + player + ".up", buttonmap[BUTTON_UP]);
		//prop.setProperty("button.p" + player + ".down", buttonmap[BUTTON_DOWN]);
		//prop.setProperty("button.p" + player + ".left", buttonmap[BUTTON_LEFT]);
		//prop.setProperty("button.p" + player + ".right", buttonmap[BUTTON_RIGHT]);
		prop.setProperty("button.p" + player + ".a", buttonmap[BUTTON_A]);
		prop.setProperty("button.p" + player + ".b", buttonmap[BUTTON_B]);
		prop.setProperty("button.p" + player + ".c", buttonmap[BUTTON_C]);
		prop.setProperty("button.p" + player + ".d", buttonmap[BUTTON_D]);
		prop.setProperty("button.p" + player + ".e", buttonmap[BUTTON_E]);
		prop.setProperty("button.p" + player + ".f", buttonmap[BUTTON_F]);
		prop.setProperty("button.p" + player + ".quit", buttonmap[BUTTON_QUIT]);
		prop.setProperty("button.p" + player + ".pause", buttonmap[BUTTON_PAUSE]);
		prop.setProperty("button.p" + player + ".giveup", buttonmap[BUTTON_GIVEUP]);
		prop.setProperty("button.p" + player + ".retry", buttonmap[BUTTON_RETRY]);
		prop.setProperty("button.p" + player + ".framestep", buttonmap[BUTTON_FRAMESTEP]);
		prop.setProperty("button.p" + player + ".screenshot", buttonmap[BUTTON_SCREENSHOT]);

        prop.setProperty("joyBorder.p" + player, joyBorder);

	}

	/**
	 * Controllerに input 状況を伝える
	 * @param ctrl  input 状況を伝えるControllerのインスタンス
	 */
	public void inputStatusUpdate(Controller ctrl) {
		for(int i = 0; i < Controller.BUTTON_COUNT; i++) {
			ctrl.buttonPress[i] = isPressKey(i);
		}
	}

	public static boolean isNavKey(int key) {
		//return (key >= BUTTON_NAV_UP) && (key <= BUTTON_NAV_CANCEL);
		return false;
	}

}
