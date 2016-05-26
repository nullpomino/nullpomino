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
 * Normal display class string
 */
public class NormalFontSDL {
	/** Character constant colorcount */
	public static final int COLOR_WHITE = 0, COLOR_BLUE = 1, COLOR_RED = 2, COLOR_PINK = 3, COLOR_GREEN = 4, COLOR_YELLOW = 5, COLOR_CYAN = 6,
			COLOR_ORANGE = 7, COLOR_PURPLE = 8, COLOR_DARKBLUE = 9;

	/** Which to drawSDLSurface */
	public static SDLSurface dest;

	/**
	 * Specified font ColorSDLColorObtained as
	 * @param fontColor  font Color
	 * @return  font ColorSDLColor
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
	 * TTF font Drawing a string using the
	 * @param fontX X-coordinate
	 * @param fontY Y-coordinate
	 * @param fontStr String
	 * @param fontColor Letter color
	 * @throws SDLException If I failed to draw
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
	 * TTF font Drawing a string using the
	 * @param fontX X-coordinate
	 * @param fontY Y-coordinate
	 * @param fontStr String
	 * @throws SDLException If I failed to draw
	 */
	public static void printTTFFont(int fontX, int fontY, String fontStr) throws SDLException {
		printTTFFont(fontX, fontY, fontStr, COLOR_WHITE);
	}

	/**
	 * Draws the string
	 * @param fontX X-coordinate
	 * @param fontY Y-coordinate
	 * @param fontStr String
	 * @param fontColor Letter color
	 * @param scale Enlargement factor (1.0fAnd0.5fOnly is enabled)
	 * @throws SDLException If I failed to draw
	 */
	public static void printFont(int fontX, int fontY, String fontStr, int fontColor, float scale) throws SDLException {
		int dx = fontX;
		int dy = fontY;

		for(int i = 0; i < fontStr.length(); i++) {
			int stringChar = fontStr.charAt(i);

			if(stringChar == 0x0A) {
				// New line (\n)
				if(scale == 2.0f) {
					dy = dy + 32;
					dx = fontX;
				} else if(scale == 1.0f) {
					dy = dy + 16;
					dx = fontX;
				} else {
					dy = dy + 8;
					dx = fontX;
				}
			} else {
				// Character output
				if(scale == 2.0f) {
					int sx = ((stringChar - 32) % 32) * 32;
					int sy = ((stringChar - 32) / 32) * 32 + fontColor * 96;
					SDLRect rectSrc = new SDLRect(sx, sy, 32, 32);
					SDLRect rectDst = new SDLRect(dx, dy, 32, 32);
					ResourceHolderSDL.imgFontBig.blitSurface(rectSrc, dest, rectDst);
					dx = dx + 32;
				} else if(scale == 1.0f) {
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
	 * Draws the string
	 * @param fontX X-coordinate
	 * @param fontY Y-coordinate
	 * @param fontStr String
	 * @param fontColor Letter color
	 * @throws SDLException If I failed to draw
	 */
	public static void printFont(int fontX, int fontY, String fontStr, int fontColor) throws SDLException {
		printFont(fontX, fontY, fontStr, fontColor, 1.0f);
	}

	/**
	 * Draws the string (Character color is white)
	 * @param fontX X-coordinate
	 * @param fontY Y-coordinate
	 * @param fontStr String
	 * @throws SDLException If I failed to draw
	 */
	public static void printFont(int fontX, int fontY, String fontStr) throws SDLException {
		printFont(fontX, fontY, fontStr, COLOR_WHITE);
	}

	/**
	 * flagThefalseIf it&#39;s the casefontColorTrue color, trueIf it&#39;s the casefontColorTrue colorDraws the string in
	 * @param fontX X-coordinate
	 * @param fontY Y-coordinate
	 * @param fontStr String
	 * @param flag Conditional expression
	 * @param fontColorFalse flagThefalseText color in the case of
	 * @param fontColorTrue flagThetrueText color in the case of
	 * @throws SDLException If I failed to draw
	 */
	public static void printFont(int fontX, int fontY, String fontStr, boolean flag, int fontColorFalse, int fontColorTrue) throws SDLException {
		if(!flag)
			printFont(fontX, fontY, fontStr, fontColorFalse);
		else
			printFont(fontX, fontY, fontStr, fontColorTrue);
	}

	/**
	 * flagThefalseIf I were white, trueDraws the string in red if I was
	 * @param fontX X-coordinate
	 * @param fontY Y-coordinate
	 * @param fontStr String
	 * @param flag Conditional expression
	 * @throws SDLException If I failed to draw
	 */
	public static void printFont(int fontX, int fontY, String fontStr, boolean flag) throws SDLException {
		printFont(fontX, fontY, fontStr, flag, COLOR_WHITE, COLOR_RED);
	}

	/**
	 * flagThefalseIf it&#39;s the casefontColorTrue color, trueIf it&#39;s the casefontColorTrue colorDraws the string in (You can specify the magnification)
	 * @param fontX X-coordinate
	 * @param fontY Y-coordinate
	 * @param fontStr String
	 * @param flag Conditional expression
	 * @param fontColorFalse flagThefalseText color in the case of
	 * @param fontColorTrue flagThetrueText color in the case of
	 * @param scale Enlargement factor
	 * @throws SDLException If I failed to draw
	 */
	public static void printFont(int fontX, int fontY, String fontStr, boolean flag, int fontColorFalse, int fontColorTrue, float scale) throws SDLException {
		if(!flag)
			printFont(fontX, fontY, fontStr, fontColorFalse, scale);
		else
			printFont(fontX, fontY, fontStr, fontColorTrue, scale);
	}

	/**
	 * flagThefalseIf I were white, trueDraws the string in red if I was (You can specify the magnification)
	 * @param fontX X-coordinate
	 * @param fontY Y-coordinate
	 * @param fontStr String
	 * @param flag Conditional expression
	 * @param scale Enlargement factor
	 * @throws SDLException If I failed to draw
	 */
	public static void printFont(int fontX, int fontY, String fontStr, boolean flag, float scale) throws SDLException {
		printFont(fontX, fontY, fontStr, flag, COLOR_WHITE, COLOR_RED, scale);
	}

	/**
	 * Draws the string (16x16Grid units)
	 * @param fontX X-coordinate
	 * @param fontY Y-coordinate
	 * @param fontStr String
	 * @param fontColor Letter color
	 * @throws SDLException If I failed to draw
	 */
	public static void printFontGrid(int fontX, int fontY, String fontStr, int fontColor) throws SDLException {
		printFont(fontX * 16, fontY * 16, fontStr, fontColor);
	}

	/**
	 * Draws the string (16x16Color and character of the white grid units)
	 * @param fontX X-coordinate
	 * @param fontY Y-coordinate
	 * @param fontStr String
	 * @throws SDLException If I failed to draw
	 */
	public static void printFontGrid(int fontX, int fontY, String fontStr) throws SDLException {
		printFont(fontX * 16, fontY * 16, fontStr, COLOR_WHITE);
	}

	/**
	 * flagThefalseIf it&#39;s the casefontColorTrue color, trueIf it&#39;s the casefontColorTrue colorDraws the string in (16x16Grid units)
	 * @param fontX X-coordinate
	 * @param fontY Y-coordinate
	 * @param fontStr String
	 * @param flag Conditional expression
	 * @param fontColorFalse flagThefalseText color in the case of
	 * @param fontColorTrue flagThetrueText color in the case of
	 * @throws SDLException If I failed to draw
	 */
	public static void printFontGrid(int fontX, int fontY, String fontStr, boolean flag, int fontColorFalse, int fontColorTrue) throws SDLException {
		printFont(fontX * 16, fontY * 16, fontStr, flag, fontColorFalse, fontColorTrue);
	}

	/**
	 * flagThefalseIf I were white, trueDraws the string in red if I was (16x16Grid units)
	 * @param fontX X-coordinate
	 * @param fontY Y-coordinate
	 * @param fontStr String
	 * @param flag Conditional expression
	 * @throws SDLException If I failed to draw
	 */
	public static void printFontGrid(int fontX, int fontY, String fontStr, boolean flag) throws SDLException {
		printFont(fontX * 16, fontY * 16, fontStr, flag, COLOR_WHITE, COLOR_RED);
	}
}
