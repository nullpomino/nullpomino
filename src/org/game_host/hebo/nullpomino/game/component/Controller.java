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
package org.game_host.hebo.nullpomino.game.component;

import java.io.Serializable;

/**
 * ボタン入力状態を管理するクラス
 */
public class Controller implements Serializable {
	/** シリアルバージョンID */
	private static final long serialVersionUID = -4855072501928533723L;

	/** ↑（ハードドロップ）ボタン */
	public static final int BUTTON_UP = 0;

	/** ↓（ソフトドロップ）ボタン */
	public static final int BUTTON_DOWN = 1;

	/** ←（左移動）ボタン */
	public static final int BUTTON_LEFT = 2;

	/** →（右移動）ボタン */
	public static final int BUTTON_RIGHT = 3;

	/** A（正回転）ボタン */
	public static final int BUTTON_A = 4;

	/** B（逆回転）ボタン */
	public static final int BUTTON_B = 5;

	/** C（正回転）ボタン */
	public static final int BUTTON_C = 6;

	/** D（ホールド）ボタン */
	public static final int BUTTON_D = 7;

	/** E（180度回転）ボタン */
	public static final int BUTTON_E = 8;

	/** F（アイテム使用やスタッフロール早送りなど）ボタン */
	public static final int BUTTON_F = 9;

	/** ボタンの数 */
	public static final int BUTTON_COUNT = 10;

	/** ビット演算用定数 */
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

	/** ボタンを押した状態ならtrue */
	public boolean[] buttonPress;

	/** ボタンを押しっぱなしにしている時間 */
	public int[] buttonTime;

	/**
	 * コンストラクタ
	 */
	public Controller() {
		reset();
	}

	/**
	 * コピーコンストラクタ
	 * @param c コピー元
	 */
	public Controller(Controller c) {
		copy(c);
	}

	/**
	 * 初期状態に戻す
	 */
	public void reset() {
		buttonPress = new boolean[BUTTON_COUNT];
		buttonTime = new int[BUTTON_COUNT];
	}

	/**
	 * 他のControllerの状態をコピー
	 * @param c コピー元
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
	 * ボタンをすべて押していない状態にする
	 */
	public void clearButtonState() {
		for(int i = 0; i < BUTTON_COUNT; i++) buttonPress[i] = false;
	}

	/**
	 * ボタンを1フレームだけ押した状態かどうか判定
	 * @param btn ボタン番号
	 * @return ボタンを1フレームだけ押した状態ならtrue
	 */
	public boolean isPush(int btn) {
		return (buttonTime[btn] == 1);
	}

	/**
	 * ボタンを押している状態かどうか判定
	 * @param btn ボタン番号
	 * @return ボタンを押している状態ならtrue
	 */
	public boolean isPress(int btn) {
		return (buttonTime[btn] >= 1);
	}

	/**
	 * メニューでカーソルが動くかどうか判定
	 * @param key ボタン番号
	 * @return カーソルが動くならtrue
	 */
	public boolean isMenuRepeatKey(int key) {
		return isMenuRepeatKey(key, true);
	}

	/**
	 * メニューでカーソルが動くかどうか判定
	 * @param key ボタン番号
	 * @param enableCButton Cボタンでの高速移動許可
	 * @return カーソルが動くならtrue
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
	 * ボタンを押した状態にする
	 * @param key ボタン番号
	 */
	public void setButtonPressed(int key) {
		if((key >= 0) && (key < buttonPress.length)) buttonPress[key] = true;
	}

	/**
	 * ボタンを押してない状態にする
	 * @param key ボタン番号
	 */
	public void setButtonUnpressed(int key) {
		if((key >= 0) && (key < buttonPress.length)) buttonPress[key] = false;
	}

	/**
	 * ボタンを押した状態を設定
	 * @param key ボタン番号
	 * @param pressed trueなら押した、falseなら押してない
	 */
	public void setButtonState(int key, boolean pressed) {
		if((key >= 0) && (key < buttonPress.length)) buttonPress[key] = pressed;
	}

	/**
	 * ボタン入力状態をビットフラグで返す
	 * @return ボタン入力状態のビットフラグ
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
	 * ボタン入力状態をビットフラグを元に設定
	 * @param input ボタン入力状態のビットフラグ
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
	 * ボタン入力時間を更新
	 */
	public void updateButtonTime() {
		for(int i = 0; i < BUTTON_COUNT; i++) {
			if(buttonPress[i]) buttonTime[i]++;
			else buttonTime[i] = 0;
		}
	}

	/**
	 * ボタン入力状態をリセット
	 */
	public void clearButtonTime() {
		for(int i = 0; i < BUTTON_COUNT; i++) {
			buttonTime[i] = 0;
		}
	}
}
