/*
    Copyright (c) 2010, NullNoname
    All rights reserved.

    Redistribution and use in source and binary forms, with or without
    modification, are permitted provided that the following conditions are met:

        * Redistributions of source code must retain the above copyright
          notice, this list of conditions and the following disclaimer.
        * Redistributions in binary form must reproduce the above copyright
          notice, this list of conditions and the following disclaimer in the
          documentation and/or other materials provided with the distribution.
        * Neither the name of NullNoname nor the names of its
          contributors may be used to endorse or promote products derived from
          this software without specific prior written permission.

    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
    AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
    IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
    ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
    LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
    CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
    SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
    INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
    CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
    ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
    POSSIBILITY OF SUCH DAMAGE.
*/
package mu.nu.nullpo.game.component;

import java.io.Serializable;

/**
 *  button inputClass to manage the state
 */
public class Controller implements Serializable {
	/** Serial version ID */
	private static final long serialVersionUID = -4855072501928533723L;

	/** ↑ (Hard drop) button */
	public static final int BUTTON_UP = 0;

	/** ↓ (Soft drop) button */
	public static final int BUTTON_DOWN = 1;

	/** ← (Left movement) button */
	public static final int BUTTON_LEFT = 2;

	/** → (Right movement) button */
	public static final int BUTTON_RIGHT = 3;

	/** A (Regular rotation) button */
	public static final int BUTTON_A = 4;

	/** B (Reverse rotation)  button */
	public static final int BUTTON_B = 5;

	/** C (Regular rotation) button */
	public static final int BUTTON_C = 6;

	/** D (Hold) button */
	public static final int BUTTON_D = 7;

	/** E (180-degree rotation) button */
	public static final int BUTTON_E = 8;

	/** F (Use item, staff roll fast-forward, etc.) button */
	public static final int BUTTON_F = 9;

	/** Number of buttons */
	public static final int BUTTON_COUNT = 10;

	/** Constant-bit operationcount */
	public static final int BUTTON_BIT_UP = 1,
							BUTTON_BIT_DOWN = 2,
							BUTTON_BIT_LEFT = 4,
							BUTTON_BIT_RIGHT = 8,
							BUTTON_BIT_A = 16,
							BUTTON_BIT_B = 32,
							BUTTON_BIT_C = 64,
							BUTTON_BIT_D = 128,
							BUTTON_BIT_E = 256,
							BUTTON_BIT_F = 512;

	/** ButtonIf you hold down thetrue */
	public boolean[] buttonPress;

	/** ButtonI have to leave the press time */
	public int[] buttonTime;

	/**
	 * Constructor
	 */
	public Controller() {
		reset();
	}

	/**
	 * Copy constructor
	 * @param c Copy source
	 */
	public Controller(Controller c) {
		copy(c);
	}

	/**
	 * Back to the initial state
	 */
	public void reset() {
		buttonPress = new boolean[BUTTON_COUNT];
		buttonTime = new int[BUTTON_COUNT];
	}

	/**
	 * OtherController stateCopy
	 * @param c Copy source
	 */
	public void copy(Controller c) {
		buttonPress = new boolean[BUTTON_COUNT];
		buttonTime = new int[BUTTON_COUNT];

		for(int i = 0; i < BUTTON_COUNT; i++) {
			buttonPress[i] = c.buttonPress[i];
			buttonTime[i] = c.buttonTime[i];
		}
	}

	/**
	 *  buttonThe state is not pressed all the
	 */
	public void clearButtonState() {
		for(int i = 0; i < BUTTON_COUNT; i++) buttonPress[i] = false;
	}

	/**
	 *  buttonA1 frame Determine whether the state I was only pressed
	 * @param btn Button number
	 * @return  buttonA1 frame If you hold down onlytrue
	 */
	public boolean isPush(int btn) {
		return (buttonTime[btn] == 1);
	}

	/**
	 *  buttonDetermine whether the state is pressed
	 * @param btn Button number
	 * @return  buttonState if you press thetrue
	 */
	public boolean isPress(int btn) {
		return (buttonTime[btn] >= 1);
	}

	/**
	 * Menu Determines whether the cursor is moved in
	 * @param key Button number
	 * @return If the cursor movestrue
	 */
	public boolean isMenuRepeatKey(int key) {
		return isMenuRepeatKey(key, true);
	}

	/**
	 * Menu Determines whether the cursor is moved in
	 * @param key Button number
	 * @param enableCButton C buttonAllow for high-speed movement
	 * @return If the cursor movestrue
	 */
	public boolean isMenuRepeatKey(int key, boolean enableCButton) {
		if( (buttonTime[key] == 1) || ((buttonTime[key] >= 25) && (buttonTime[key] % 3 == 0)) ||
		    ((buttonTime[key] >= 1) && isPress(BUTTON_C) && enableCButton) )
		{
			return true;
		}

		return false;
	}

	/**
	 *  buttonThe Press and hold the
	 * @param key Button number
	 */
	public void setButtonPressed(int key) {
		if((key >= 0) && (key < buttonPress.length)) buttonPress[key] = true;
	}

	/**
	 *  buttonThe state did not press
	 * @param key Button number
	 */
	public void setButtonUnpressed(int key) {
		if((key >= 0) && (key < buttonPress.length)) buttonPress[key] = false;
	}

	/**
	 *  buttonSets the status by pressing the
	 * @param key Button number
	 * @param pressed When true,Press, falseIf I do not press
	 */
	public void setButtonState(int key, boolean pressed) {
		if((key >= 0) && (key < buttonPress.length)) buttonPress[key] = pressed;
	}

	/**
	 *  button inputBit state flagReturns
	 * @return  button inputBit of state flag
	 */
	public int getButtonBit() {
		int input = 0;

		if(buttonPress[BUTTON_UP]) input |= BUTTON_BIT_UP;
		if(buttonPress[BUTTON_DOWN]) input |= BUTTON_BIT_DOWN;
		if(buttonPress[BUTTON_LEFT]) input |= BUTTON_BIT_LEFT;
		if(buttonPress[BUTTON_RIGHT]) input |= BUTTON_BIT_RIGHT;
		if(buttonPress[BUTTON_A]) input |= BUTTON_BIT_A;
		if(buttonPress[BUTTON_B]) input |= BUTTON_BIT_B;
		if(buttonPress[BUTTON_C]) input |= BUTTON_BIT_C;
		if(buttonPress[BUTTON_D]) input |= BUTTON_BIT_D;
		if(buttonPress[BUTTON_E]) input |= BUTTON_BIT_E;
		if(buttonPress[BUTTON_F]) input |= BUTTON_BIT_F;

		return input;
	}

	/**
	 *  button inputBit state flagSet based on
	 * @param input  button inputBit of state flag
	 */
	public void setButtonBit(int input) {
		clearButtonState();

		if((input & BUTTON_BIT_UP) != 0) buttonPress[BUTTON_UP] = true;
		if((input & BUTTON_BIT_DOWN) != 0) buttonPress[BUTTON_DOWN] = true;
		if((input & BUTTON_BIT_LEFT) != 0) buttonPress[BUTTON_LEFT] = true;
		if((input & BUTTON_BIT_RIGHT) != 0) buttonPress[BUTTON_RIGHT] = true;
		if((input & BUTTON_BIT_A) != 0) buttonPress[BUTTON_A] = true;
		if((input & BUTTON_BIT_B) != 0) buttonPress[BUTTON_B] = true;
		if((input & BUTTON_BIT_C) != 0) buttonPress[BUTTON_C] = true;
		if((input & BUTTON_BIT_D) != 0) buttonPress[BUTTON_D] = true;
		if((input & BUTTON_BIT_E) != 0) buttonPress[BUTTON_E] = true;
		if((input & BUTTON_BIT_F) != 0) buttonPress[BUTTON_F] = true;
	}

	/**
	 *  button input timeUpdate
	 */
	public void updateButtonTime() {
		for(int i = 0; i < BUTTON_COUNT; i++) {
			if(buttonPress[i]) buttonTime[i]++;
			else buttonTime[i] = 0;
		}
	}

	/**
	 *  button inputResets the state
	 */
	public void clearButtonTime() {
		for(int i = 0; i < BUTTON_COUNT; i++) {
			buttonTime[i] = 0;
		}
	}
}
