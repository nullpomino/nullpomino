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
package mu.nu.nullpo.gui.slick;

import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;

/**
 * 普通の文字列の表示クラス
 */
public class NormalFont {
	/** 文字色の定count */
	public static final int COLOR_WHITE = 0, COLOR_BLUE = 1, COLOR_RED = 2, COLOR_PINK = 3, COLOR_GREEN = 4, COLOR_YELLOW = 5, COLOR_CYAN = 6,
			COLOR_ORANGE = 7, COLOR_PURPLE = 8, COLOR_DARKBLUE = 9;

	/**
	 * 指定した font 色をSlick用Colorとして取得
	 * @param fontColor  font 色
	 * @return  font 色のColor
	 */
	public static Color getFontColorAsColor(int fontColor) {
		switch(fontColor) {
		case COLOR_BLUE:		return new Color(  0,  0,255);
		case COLOR_RED:			return new Color(255,  0,  0);
		case COLOR_PINK:		return new Color(255,128,128);
		case COLOR_GREEN:		return new Color(  0,255,  0);
		case COLOR_YELLOW:		return new Color(255,255,  0);
		case COLOR_CYAN:		return new Color(  0,255,255);
		case COLOR_ORANGE:		return new Color(255,128,  0);
		case COLOR_PURPLE:		return new Color(255,  0,255);
		case COLOR_DARKBLUE:	return new Color(  0,  0,128);
		}

		return new Color(255,255,255);
	}

	/**
	 * TTF font を使用して文字列を描画
	 * @param fontX X-coordinate
	 * @param fontY Y-coordinate
	 * @param fontStr 文字列
	 * @param fontColor 文字色
	 */
	public static void printTTFFont(int fontX, int fontY, String fontStr, int fontColor) {
		if(ResourceHolder.ttfFont == null) return;
		ResourceHolder.ttfFont.drawString(fontX, fontY, fontStr, getFontColorAsColor(fontColor));
	}

	/**
	 * TTF font を使用して文字列を描画
	 * (各ステートのupdateメソッドでResourceHolder.ttfFont.loadGlyphs()を呼ばないと描画されないので注意)
	 * @param fontX X-coordinate
	 * @param fontY Y-coordinate
	 * @param fontStr 文字列
	 */
	public static void printTTFFont(int fontX, int fontY, String fontStr) {
		printTTFFont(fontX, fontY, fontStr, COLOR_WHITE);
	}

	/**
	 * 文字列を描画
	 * @param fontX X-coordinate
	 * @param fontY Y-coordinate
	 * @param fontStr 文字列
	 * @param fontColor 文字色
	 * @param scale 拡大率
	 */
	public static void printFont(int fontX, int fontY, String fontStr, int fontColor, float scale) {
		int dx = fontX;
		int dy = fontY;

		for(int i = 0; i < fontStr.length(); i++) {
			int stringChar = fontStr.charAt(i);

			if(stringChar == 0x0A) {
				// 改行 (\n）
				if(scale == 1.0f) {
					dy = (int)(dy + 16 * scale);
					dx = fontX;
				} else {
					dy = dy + 8;
					dx = fontX;
				}
			} else {
				// 文字出力
				if(scale == 0.5f) {
					int sx = ((stringChar - 32) % 32) * 8;
					int sy = ((stringChar - 32) / 32) * 8 + fontColor * 24;
					ResourceHolder.imgFontSmall.draw(dx, dy, dx + 8, dy + 8, sx, sy, sx + 8, sy + 8);
					dx = dx + 8;
				} else {
					int sx = ((stringChar - 32) % 32) * 16;
					int sy = ((stringChar - 32) / 32) * 16 + fontColor * 48;
					//SDLRect rectSrc = new SDLRect(sx, sy, 16, 16);
					//SDLRect rectDst = new SDLRect(dx, dy, 16, 16);
					//ResourceHolderSDL.imgFont.blitSurface(rectSrc, dest, rectDst);
					ResourceHolder.imgFont.draw(dx, dy, dx + (16 * scale), dy + (16 * scale), sx, sy, sx + 16, sy + 16);
					dx = (int)(dx + 16 * scale);
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
	 * flagがfalseだったらfontColorTrue color, trueだったらfontColorTrue colorで文字列を描画
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
	 * flagがfalseだったら白, trueだったら赤で文字列を描画
	 * @param fontX X-coordinate
	 * @param fontY Y-coordinate
	 * @param fontStr 文字列
	 * @param flag 条件式
	 */
	public static void printFont(int fontX, int fontY, String fontStr, boolean flag) {
		printFont(fontX, fontY, fontStr, flag, COLOR_WHITE, COLOR_RED);
	}

	/**
	 * flagがfalseだったらfontColorTrue color, trueだったらfontColorTrue colorで文字列を描画 (拡大率指定可能）
	 * @param fontX X-coordinate
	 * @param fontY Y-coordinate
	 * @param fontStr 文字列
	 * @param flag 条件式
	 * @param fontColorFalse flagがfalseの場合の文字色
	 * @param fontColorTrue flagがtrueの場合の文字色
	 * @param scale 拡大率
	 * @throws SlickException 描画に失敗した場合
	 */
	public static void printFont(int fontX, int fontY, String fontStr, boolean flag, int fontColorFalse, int fontColorTrue, float scale) throws SlickException {
		if(!flag)
			printFont(fontX, fontY, fontStr, fontColorFalse, scale);
		else
			printFont(fontX, fontY, fontStr, fontColorTrue, scale);
	}

	/**
	 * flagがfalseだったら白, trueだったら赤で文字列を描画 (拡大率指定可能）
	 * @param fontX X-coordinate
	 * @param fontY Y-coordinate
	 * @param fontStr 文字列
	 * @param flag 条件式
	 * @param scale 拡大率
	 * @throws SlickException 描画に失敗した場合
	 */
	public static void printFont(int fontX, int fontY, String fontStr, boolean flag, float scale) throws SlickException {
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
	 * flagがfalseだったらfontColorTrue color, trueだったらfontColorTrue colorで文字列を描画 (16x16のグリッド単位）
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
	 * flagがfalseだったら白, trueだったら赤で文字列を描画 (16x16のグリッド単位）
	 * @param fontX X-coordinate
	 * @param fontY Y-coordinate
	 * @param fontStr 文字列
	 * @param flag 条件式
	 */
	public static void printFontGrid(int fontX, int fontY, String fontStr, boolean flag) {
		printFont(fontX * 16, fontY * 16, fontStr, flag, COLOR_WHITE, COLOR_RED);
	}
}
