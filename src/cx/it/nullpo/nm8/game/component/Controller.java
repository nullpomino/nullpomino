package cx.it.nullpo.nm8.game.component;

import java.io.Serializable;

/**
 * Controller: Game input manager
 */
public class Controller implements Serializable {
	//TODO: Move all DAS/ARR related things to GamePlay
	/** Serial version ID */
	private static final long serialVersionUID = 9022697244937343371L;

	/** Controller button constants */
	public static final int BUTTON_HARD = 0,
							BUTTON_SOFT = 1,
							BUTTON_LEFT = 2,
							BUTTON_RIGHT = 3,
							BUTTON_LROTATE = 4,
							BUTTON_RROTATE = 5,
							BUTTON_DROTATE = 6,
							BUTTON_HOLD = 7,
							BUTTON_SPECIAL = 8,
							MAX_BUTTON = 9;

	/** Controller button constants for bit-flags */
	public static final int BUTTON_BIT_HARD = 1,
							BUTTON_BIT_SOFT = 2,
							BUTTON_BIT_LEFT = 4,
							BUTTON_BIT_RIGHT = 8,
							BUTTON_BIT_LROTATE = 16,
							BUTTON_BIT_RROTATE = 32,
							BUTTON_BIT_DROTATE = 64,
							BUTTON_BIT_HOLD = 128,
							BUTTON_BIT_SPECIAL = 256;

	/** Button pressed flag array */
	public boolean[] buttonPress;

	/** Button activated flag */
	public boolean[] buttonActive;

	/** Button pushed flag */
	public boolean[] buttonPushed;

	/** Button pressing time */
	public long[] buttonTime;

	/** Last pressed time */
	public long[] buttonLastPressedTime;

	/** Last activated time */
	public long[] buttonLastActivatedTime;

	/**
	 * Constructor
	 */
	public Controller() {
		reset();
	}

	/**
	 * Copy Constructor
	 * @param c Copy source
	 */
	public Controller(Controller c) {
		reset();
		copy(c);
	}

	/**
	 * Reset to defaults
	 */
	public void reset() {
		buttonPress = new boolean[MAX_BUTTON];
		buttonActive = new boolean[MAX_BUTTON];
		buttonPushed = new boolean[MAX_BUTTON];
		buttonTime = new long[MAX_BUTTON];
		buttonLastPressedTime = new long[MAX_BUTTON];
		buttonLastActivatedTime = new long[MAX_BUTTON];
	}

	/**
	 * Copy from other instance of Controller
	 * @param c Copy source
	 */
	public void copy(Controller c) {
		for(int i = 0; i < MAX_BUTTON; i++) {
			buttonPress[i] = c.buttonPress[i];
			buttonActive[i] = c.buttonActive[i];
			buttonPushed[i] = c.buttonPushed[i];
			buttonTime[i] = c.buttonTime[i];
			buttonLastPressedTime[i] = c.buttonLastPressedTime[i];
			buttonLastActivatedTime[i] = c.buttonLastActivatedTime[i];
		}
	}

	/**
	 * Clear the button pressed state
	 */
	public void clearButtonState() {
		for(int i = 0; i < MAX_BUTTON; i++) buttonPress[i] = false;
	}

	/**
	 * Set button pressed state
	 * @param b Button ID
	 * @param p Pressed flag
	 */
	public void setButtonState(int b, boolean p) {
		buttonPress[b] = p;
	}

	/**
	 * Check button pressed
	 * @param b Button ID
	 * @return true if the button is pressed
	 */
	public boolean isButtonPressed(int b) {
		return buttonPress[b];
	}

	/**
	 * Check button activated (pushed)
	 * @param b Button ID
	 * @return true if the button is activated
	 */
	public boolean isButtonActivated(int b) {
		return buttonActive[b];
	}

	/**
	 * Update the controller status
	 * @param currentTime Current time
	 */
	public void update(long currentTime) {
		for(int i = 0; i < MAX_BUTTON; i++) {
			if(buttonPress[i]) {
				if(!buttonPushed[i]) {
					buttonPushed[i] = true;
					buttonActive[i] = true;
					buttonTime[i]++;
					buttonLastActivatedTime[i] = currentTime;
					buttonLastPressedTime[i] = currentTime;
				} else {
					buttonActive[i] = false;
					buttonTime[i]++;
					buttonLastPressedTime[i] = currentTime;
				}
			} else {
				buttonActive[i] = false;
				buttonPushed[i] = false;
				buttonTime[i] = 0;
			}
		}
	}
}
