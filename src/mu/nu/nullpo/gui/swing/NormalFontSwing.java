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
package mu.nu.nullpo.gui.swing;

import java.awt.Graphics2D;

/**
 * 普通の文字列の表示クラス (Swing用）
 */
public class NormalFontSwing {
	/** 文字色の定count */
	public static final int COLOR_WHITE = 0, COLOR_BLUE = 1, COLOR_RED = 2, COLOR_PINK = 3, COLOR_GREEN = 4, COLOR_YELLOW = 5, COLOR_CYAN = 6,
			COLOR_ORANGE = 7, COLOR_PURPLE = 8, COLOR_DARKBLUE = 9;

	/** 描画先 */
	public static Graphics2D graphics = null;

	/**
	 * フォントを描画
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param str 文字列
	 * @param color 文字色
	 * @param scale 拡大率
	 */
	public static void printFont(int x, int y, String str, int color, float scale) {
		if(graphics == null) return;

		int dx = x;
		int dy = y;

		for(int i = 0; i < str.length(); i++) {
			int stringChar = str.charAt(i);

			if(stringChar == 0x0A) {
				// 改行 (\n）
				dy = (int) (dy + 16 * scale);
				dx = x;
			} else {
				// 文字出力
				if(scale == 0.5f) {
					int sx = ((stringChar - 32) % 32) * 8;
					int sy = ((stringChar - 32) / 32) * 8 + color * 24;
					graphics.drawImage(ResourceHolderSwing.imgFontSmall, dx, dy, dx + 8, dy + 8, sx, sy, sx + 8, sy + 8, null);
					dx = dx + 8;
				} else {
					int sx = ((stringChar - 32) % 32) * 16;
					int sy = ((stringChar - 32) / 32) * 16 + color * 48;
					graphics.drawImage(ResourceHolderSwing.imgFont, dx, dy, (int)(dx + 16 * scale), (int)(dy + 16 * scale), sx, sy, sx + 16, sy + 16, null);
					dx = (int) (dx + 16 * scale);
				}
			}
		}
	}

	/**
	 * 文字列を描画
	 * @param fontX X-coordinate
	 * @param fontY Y-coordinate
	 * @param fontStr 文字列
	 * @param fontColor 文字色
	 */
	public static void printFont(int fontX, int fontY, String fontStr, int fontColor) {
		printFont(fontX, fontY, fontStr, fontColor, 1.0f);
	}

	/**
	 * 文字列を描画 (文字色は白）
	 * @param fontX X-coordinate
	 * @param fontY Y-coordinate
	 * @param fontStr 文字列
	 */
	public static void printFont(int fontX, int fontY, String fontStr) {
		printFont(fontX, fontY, fontStr, COLOR_WHITE);
	}

	/**
	 * flagがfalseだったらfontColorTrueの色、trueだったらfontColorTrueの色で文字列を描画
	 * @param fontX X-coordinate
	 * @param fontY Y-coordinate
	 * @param fontStr 文字列
	 * @param flag 条件式
	 * @param fontColorFalse flagがfalseの場合の文字色
	 * @param fontColorTrue flagがtrueの場合の文字色
	 */
	public static void printFont(int fontX, int fontY, String fontStr, boolean flag, int fontColorFalse, int fontColorTrue) {
		if(!flag)
			printFont(fontX, fontY, fontStr, fontColorFalse);
		else
			printFont(fontX, fontY, fontStr, fontColorTrue);
	}

	/**
	 * flagがfalseだったら白、trueだったら赤で文字列を描画
	 * @param fontX X-coordinate
	 * @param fontY Y-coordinate
	 * @param fontStr 文字列
	 * @param flag 条件式
	 */
	public static void printFont(int fontX, int fontY, String fontStr, boolean flag) {
		printFont(fontX, fontY, fontStr, flag, COLOR_WHITE, COLOR_RED);
	}

	/**
	 * flagがfalseだったらfontColorTrueの色、trueだったらfontColorTrueの色で文字列を描画 (拡大率指定可能）
	 * @param fontX X-coordinate
	 * @param fontY Y-coordinate
	 * @param fontStr 文字列
	 * @param flag 条件式
	 * @param fontColorFalse flagがfalseの場合の文字色
	 * @param fontColorTrue flagがtrueの場合の文字色
	 * @param scale 拡大率
	 */
	public static void printFont(int fontX, int fontY, String fontStr, boolean flag, int fontColorFalse, int fontColorTrue, float scale) {
		if(!flag)
			printFont(fontX, fontY, fontStr, fontColorFalse, scale);
		else
			printFont(fontX, fontY, fontStr, fontColorTrue, scale);
	}

	/**
	 * flagがfalseだったら白、trueだったら赤で文字列を描画 (拡大率指定可能）
	 * @param fontX X-coordinate
	 * @param fontY Y-coordinate
	 * @param fontStr 文字列
	 * @param flag 条件式
	 * @param scale 拡大率
	 */
	public static void printFont(int fontX, int fontY, String fontStr, boolean flag, float scale) {
		printFont(fontX, fontY, fontStr, flag, COLOR_WHITE, COLOR_RED, scale);
	}

	/**
	 * 文字列を描画 (16x16のグリッド単位）
	 * @param fontX X-coordinate
	 * @param fontY Y-coordinate
	 * @param fontStr 文字列
	 * @param fontColor 文字色
	 */
	public static void printFontGrid(int fontX, int fontY, String fontStr, int fontColor) {
		printFont(fontX * 16, fontY * 16, fontStr, fontColor);
	}

	/**
	 * 文字列を描画 (16x16のグリッド単位・文字色は白）
	 * @param fontX X-coordinate
	 * @param fontY Y-coordinate
	 * @param fontStr 文字列
	 */
	public static void printFontGrid(int fontX, int fontY, String fontStr) {
		printFont(fontX * 16, fontY * 16, fontStr, COLOR_WHITE);
	}

	/**
	 * flagがfalseだったらfontColorTrueの色、trueだったらfontColorTrueの色で文字列を描画 (16x16のグリッド単位）
	 * @param fontX X-coordinate
	 * @param fontY Y-coordinate
	 * @param fontStr 文字列
	 * @param flag 条件式
	 * @param fontColorFalse flagがfalseの場合の文字色
	 * @param fontColorTrue flagがtrueの場合の文字色
	 */
	public static void printFontGrid(int fontX, int fontY, String fontStr, boolean flag, int fontColorFalse, int fontColorTrue) {
		printFont(fontX * 16, fontY * 16, fontStr, flag, fontColorFalse, fontColorTrue);
	}

	/**
	 * flagがfalseだったら白、trueだったら赤で文字列を描画 (16x16のグリッド単位）
	 * @param fontX X-coordinate
	 * @param fontY Y-coordinate
	 * @param fontStr 文字列
	 * @param flag 条件式
	 */
	public static void printFontGrid(int fontX, int fontY, String fontStr, boolean flag) {
		printFont(fontX * 16, fontY * 16, fontStr, flag, COLOR_WHITE, COLOR_RED);
	}
}
