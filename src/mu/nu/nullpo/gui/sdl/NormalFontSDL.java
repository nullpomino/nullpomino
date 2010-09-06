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
package mu.nu.nullpo.gui.sdl;

import sdljava.SDLException;
import sdljava.video.SDLColor;
import sdljava.video.SDLRect;
import sdljava.video.SDLSurface;

/**
 * 普通の文字列の表示クラス
 */
public class NormalFontSDL {
	/** 文字色の定count */
	public static final int COLOR_WHITE = 0, COLOR_BLUE = 1, COLOR_RED = 2, COLOR_PINK = 3, COLOR_GREEN = 4, COLOR_YELLOW = 5, COLOR_CYAN = 6,
			COLOR_ORANGE = 7, COLOR_PURPLE = 8, COLOR_DARKBLUE = 9;

	/** 描画先のSDLSurface */
	public static SDLSurface dest;

	/**
	 * 指定したフォント色をSDLColorとして取得
	 * @param fontColor フォント色
	 * @return フォント色のSDLColor
	 */
	public static SDLColor getFontColorAsSDLColor(int fontColor) {
		switch(fontColor) {
		case COLOR_BLUE:		return new SDLColor(  0,  0,255);
		case COLOR_RED:			return new SDLColor(255,  0,  0);
		case COLOR_PINK:		return new SDLColor(255,128,128);
		case COLOR_GREEN:		return new SDLColor(  0,255,  0);
		case COLOR_YELLOW:		return new SDLColor(255,255,  0);
		case COLOR_CYAN:		return new SDLColor(  0,255,255);
		case COLOR_ORANGE:		return new SDLColor(255,128,  0);
		case COLOR_PURPLE:		return new SDLColor(255,  0,255);
		case COLOR_DARKBLUE:	return new SDLColor(  0,  0,128);
		}

		return new SDLColor(255,255,255);
	}

	/**
	 * TTFフォントを使用して文字列を描画
	 * @param fontX X-coordinate
	 * @param fontY Y-coordinate
	 * @param fontStr 文字列
	 * @param fontColor 文字色
	 * @throws SDLException 描画に失敗した場合
	 */
	public static void printTTFFont(int fontX, int fontY, String fontStr, int fontColor) throws SDLException {
		if(ResourceHolderSDL.ttfFont == null) return;
		SDLSurface surfaceText;
		surfaceText = ResourceHolderSDL.ttfFont.renderTextBlended(fontStr, new SDLColor(0,0,0));
		surfaceText.blitSurface(dest, new SDLRect(fontX+1, fontY+1));
		surfaceText= ResourceHolderSDL.ttfFont.renderTextBlended(fontStr, getFontColorAsSDLColor(fontColor));
		surfaceText.blitSurface(dest, new SDLRect(fontX, fontY));
	}

	/**
	 * TTFフォントを使用して文字列を描画
	 * @param fontX X-coordinate
	 * @param fontY Y-coordinate
	 * @param fontStr 文字列
	 * @throws SDLException 描画に失敗した場合
	 */
	public static void printTTFFont(int fontX, int fontY, String fontStr) throws SDLException {
		printTTFFont(fontX, fontY, fontStr, COLOR_WHITE);
	}

	/**
	 * 文字列を描画
	 * @param fontX X-coordinate
	 * @param fontY Y-coordinate
	 * @param fontStr 文字列
	 * @param fontColor 文字色
	 * @param scale 拡大率 (1.0fと0.5fのみ有効）
	 * @throws SDLException 描画に失敗した場合
	 */
	public static void printFont(int fontX, int fontY, String fontStr, int fontColor, float scale) throws SDLException {
		int dx = fontX;
		int dy = fontY;

		for(int i = 0; i < fontStr.length(); i++) {
			int stringChar = fontStr.charAt(i);

			if(stringChar == 0x0A) {
				// 改行 (\n）
				if(scale == 1.0f) {
					dy = dy + 16;
					dx = fontX;
				} else {
					dy = dy + 8;
					dx = fontX;
				}
			} else {
				// 文字出力
				if(scale == 1.0f) {
					int sx = ((stringChar - 32) % 32) * 16;
					int sy = ((stringChar - 32) / 32) * 16 + fontColor * 48;
					SDLRect rectSrc = new SDLRect(sx, sy, 16, 16);
					SDLRect rectDst = new SDLRect(dx, dy, 16, 16);
					ResourceHolderSDL.imgFont.blitSurface(rectSrc, dest, rectDst);
					dx = dx + 16;
				} else if(scale == 0.5f) {
					int sx = ((stringChar - 32) % 32) * 8;
					int sy = ((stringChar - 32) / 32) * 8 + fontColor * 24;
					SDLRect rectSrc = new SDLRect(sx, sy, 8, 8);
					SDLRect rectDst = new SDLRect(dx, dy, 8, 8);
					ResourceHolderSDL.imgFontSmall.blitSurface(rectSrc, dest, rectDst);
					dx = dx + 8;
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
	 * @throws SDLException 描画に失敗した場合
	 */
	public static void printFont(int fontX, int fontY, String fontStr, int fontColor) throws SDLException {
		printFont(fontX, fontY, fontStr, fontColor, 1.0f);
	}

	/**
	 * 文字列を描画 (文字色は白）
	 * @param fontX X-coordinate
	 * @param fontY Y-coordinate
	 * @param fontStr 文字列
	 * @throws SDLException 描画に失敗した場合
	 */
	public static void printFont(int fontX, int fontY, String fontStr) throws SDLException {
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
	 * @throws SDLException 描画に失敗した場合
	 */
	public static void printFont(int fontX, int fontY, String fontStr, boolean flag, int fontColorFalse, int fontColorTrue) throws SDLException {
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
	 * @throws SDLException 描画に失敗した場合
	 */
	public static void printFont(int fontX, int fontY, String fontStr, boolean flag) throws SDLException {
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
	 * @throws SDLException 描画に失敗した場合
	 */
	public static void printFont(int fontX, int fontY, String fontStr, boolean flag, int fontColorFalse, int fontColorTrue, float scale) throws SDLException {
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
	 * @throws SDLException 描画に失敗した場合
	 */
	public static void printFont(int fontX, int fontY, String fontStr, boolean flag, float scale) throws SDLException {
		printFont(fontX, fontY, fontStr, flag, COLOR_WHITE, COLOR_RED, scale);
	}

	/**
	 * 文字列を描画 (16x16のグリッド単位）
	 * @param fontX X-coordinate
	 * @param fontY Y-coordinate
	 * @param fontStr 文字列
	 * @param fontColor 文字色
	 * @throws SDLException 描画に失敗した場合
	 */
	public static void printFontGrid(int fontX, int fontY, String fontStr, int fontColor) throws SDLException {
		printFont(fontX * 16, fontY * 16, fontStr, fontColor);
	}

	/**
	 * 文字列を描画 (16x16のグリッド単位・文字色は白）
	 * @param fontX X-coordinate
	 * @param fontY Y-coordinate
	 * @param fontStr 文字列
	 * @throws SDLException 描画に失敗した場合
	 */
	public static void printFontGrid(int fontX, int fontY, String fontStr) throws SDLException {
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
	 * @throws SDLException 描画に失敗した場合
	 */
	public static void printFontGrid(int fontX, int fontY, String fontStr, boolean flag, int fontColorFalse, int fontColorTrue) throws SDLException {
		printFont(fontX * 16, fontY * 16, fontStr, flag, fontColorFalse, fontColorTrue);
	}

	/**
	 * flagがfalseだったら白、trueだったら赤で文字列を描画 (16x16のグリッド単位）
	 * @param fontX X-coordinate
	 * @param fontY Y-coordinate
	 * @param fontStr 文字列
	 * @param flag 条件式
	 * @throws SDLException 描画に失敗した場合
	 */
	public static void printFontGrid(int fontX, int fontY, String fontStr, boolean flag) throws SDLException {
		printFont(fontX * 16, fontY * 16, fontStr, flag, COLOR_WHITE, COLOR_RED);
	}
}
