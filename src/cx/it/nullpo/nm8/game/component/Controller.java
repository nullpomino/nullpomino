package cx.it.nullpo.nm8.game.component;

import java.io.Serializable;

public class Controller implements Serializable {
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

	/** Button auto-repeated flag */
	public boolean[] buttonRepeated;

	/** Button pressing time */
	public long[] buttonTime;

	/** Button auto-repeat timer */
	public long[] buttonRepeatTimer;

	/** Last pushed time */
	public long[] buttonLastPushedTime;

	/** DAS for each button */
	public long[] buttonDAS;

	/** ARR for each button */
	public long[] buttonARR;

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
		buttonRepeated = new boolean[MAX_BUTTON];
		buttonTime = new long[MAX_BUTTON];
		buttonRepeatTimer = new long[MAX_BUTTON];
		buttonLastPushedTime = new long[MAX_BUTTON];
		buttonDAS = new long[MAX_BUTTON];
		buttonARR = new long[MAX_BUTTON];
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
			buttonRepeated[i] = c.buttonRepeated[i];
			buttonTime[i] = c.buttonTime[i];
			buttonRepeatTimer[i] = c.buttonRepeatTimer[i];
			buttonLastPushedTime[i] = c.buttonLastPushedTime[i];
			buttonDAS[i] = c.buttonDAS[i];
			buttonARR[i] = c.buttonARR[i];
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
	 * Check button activated
	 * @param b Button ID
	 * @return true if the button is activated
	 */
	public boolean isButtonActivated(int b) {
		return buttonActive[b];
	}

	/**
	 * Set DAS
	 * @param b Button ID
	 * @param das DAS
	 */
	public void setDAS(int b, long das) {
		buttonDAS[b] = das;
	}

	/**
	 * Set ARR
	 * @param b Button ID
	 * @param das ARR
	 */
	public void setARR(int b, long arr) {
		buttonARR[b] = arr;
	}

	/**
	 * Update the controller status
	 * @param runMsec Milliseconds elapsed from the last execution
	 * @param currentTime Current time
	 */
	public void update(long runMsec, long currentTime) {
		for(int i = 0; i < MAX_BUTTON; i++) {
			if(buttonPress[i]) {
				if(!buttonPushed[i]) {
					buttonPushed[i] = true;
					buttonRepeated[i] = false;
					buttonActive[i] = true;
					buttonTime[i] += runMsec;
					buttonLastPushedTime[i] = currentTime;
				} else {
					buttonActive[i] = false;
					buttonTime[i] += runMsec;

					long delay = buttonRepeated[i] ? buttonARR[i] : buttonDAS[i];
					if(delay > 0) {
						buttonRepeatTimer[i] += runMsec;
						if(buttonRepeatTimer[i] >= delay) {
							buttonRepeatTimer[i] -= delay;
							buttonActive[i] = true;
							buttonRepeated[i] = true;
						}
					}
				}
			} else {
				buttonActive[i] = false;
				buttonPushed[i] = false;
				buttonRepeated[i] = false;
				buttonTime[i] = 0;
				buttonRepeatTimer[i] = 0;
			}
		}
	}
}
