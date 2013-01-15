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
 * Normal display class string
 */
public class NormalFontSlick {
	/** Character constant colorcount */
	public static final int COLOR_WHITE = 0, COLOR_BLUE = 1, COLOR_RED = 2, COLOR_PINK = 3, COLOR_GREEN = 4, COLOR_YELLOW = 5, COLOR_CYAN = 6,
			COLOR_ORANGE = 7, COLOR_PURPLE = 8, COLOR_DARKBLUE = 9;

	/**
	 * Specified font ColorSlickUseColorObtained as
	 * @param fontColor  font Color
	 * @return  font ColorColor
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
	 * TTF font Drawing a string using the
	 * @param fontX X-coordinate
	 * @param fontY Y-coordinate
	 * @param fontStr String
	 * @param fontColor Letter color
	 */
	public static void printTTFFont(int fontX, int fontY, String fontStr, int fontColor) {
		if(ResourceHolderSlick.ttfFont == null) return;
		ResourceHolderSlick.ttfFont.drawString(fontX, fontY, fontStr, getFontColorAsColor(fontColor));
	}

	/**
	 * TTF font Drawing a string using the
	 * (Of each stateupdateThe methodResourceHolder.ttfFont.loadGlyphs()I will not be drawing attention not to call)
	 * @param fontX X-coordinate
	 * @param fontY Y-coordinate
	 * @param fontStr String
	 */
	public static void printTTFFont(int fontX, int fontY, String fontStr) {
		printTTFFont(fontX, fontY, fontStr, COLOR_WHITE);
	}

	/**
	 * Draws the string
	 * @param fontX X-coordinate
	 * @param fontY Y-coordinate
	 * @param fontStr String
	 * @param fontColor Letter color
	 * @param scale Enlargement factor
	 */
	public static void printFont(int fontX, int fontY, String fontStr, int fontColor, float scale) {
		int dx = fontX;
		int dy = fontY;

		for(int i = 0; i < fontStr.length(); i++) {
			int stringChar = fontStr.charAt(i);

			if(stringChar == 0x0A) {
				// New line (\n)
				if(scale == 1.0f) {
					dy = (int)(dy + 16 * scale);
					dx = fontX;
				} else {
					dy = dy + 8;
					dx = fontX;
				}
			} else {
				// Character output
				if(scale == 0.5f) {
					int sx = ((stringChar - 32) % 32) * 8;
					int sy = ((stringChar - 32) / 32) * 8 + fontColor * 24;
					ResourceHolderSlick.imgFontSmall.draw(dx, dy, dx + 8, dy + 8, sx, sy, sx + 8, sy + 8);
					dx = dx + 8;
				} else {
					int sx = ((stringChar - 32) % 32) * 16;
					int sy = ((stringChar - 32) / 32) * 16 + fontColor * 48;
					//SDLRect rectSrc = new SDLRect(sx, sy, 16, 16);
					//SDLRect rectDst = new SDLRect(dx, dy, 16, 16);
					//ResourceHolderSDL.imgFont.blitSurface(rectSrc, dest, rectDst);
					ResourceHolderSlick.imgFont.draw(dx, dy, dx + (16 * scale), dy + (16 * scale), sx, sy, sx + 16, sy + 16);
					dx = (int)(dx + 16 * scale);
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
	 */
	public static void printFont(int fontX, int fontY, String fontStr, int fontColor) {
		printFont(fontX, fontY, fontStr, fontColor, 1.0f);
	}

	/**
	 * Draws the string (Character color is white)
	 * @param fontX X-coordinate
	 * @param fontY Y-coordinate
	 * @param fontStr String
	 */
	public static void printFont(int fontX, int fontY, String fontStr) {
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
	 */
	public static void printFont(int fontX, int fontY, String fontStr, boolean flag, int fontColorFalse, int fontColorTrue) {
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
	 */
	public static void printFont(int fontX, int fontY, String fontStr, boolean flag) {
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
	 * @throws SlickException If I failed to draw
	 */
	public static void printFont(int fontX, int fontY, String fontStr, boolean flag, int fontColorFalse, int fontColorTrue, float scale) throws SlickException {
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
	 * @throws SlickException If I failed to draw
	 */
	public static void printFont(int fontX, int fontY, String fontStr, boolean flag, float scale) throws SlickException {
		printFont(fontX, fontY, fontStr, flag, COLOR_WHITE, COLOR_RED, scale);
	}

	/**
	 * Draws the string (16x16Grid units)
	 * @param fontX X-coordinate
	 * @param fontY Y-coordinate
	 * @param fontStr String
	 * @param fontColor Letter color
	 */
	public static void printFontGrid(int fontX, int fontY, String fontStr, int fontColor) {
		printFont(fontX * 16, fontY * 16, fontStr, fontColor);
	}

	/**
	 * Draws the string (16x16Color and character of the white grid units)
	 * @param fontX X-coordinate
	 * @param fontY Y-coordinate
	 * @param fontStr String
	 */
	public static void printFontGrid(int fontX, int fontY, String fontStr) {
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
	 */
	public static void printFontGrid(int fontX, int fontY, String fontStr, boolean flag, int fontColorFalse, int fontColorTrue) {
		printFont(fontX * 16, fontY * 16, fontStr, flag, fontColorFalse, fontColorTrue);
	}

	/**
	 * flagThefalseIf I were white, trueDraws the string in red if I was (16x16Grid units)
	 * @param fontX X-coordinate
	 * @param fontY Y-coordinate
	 * @param fontStr String
	 * @param flag Conditional expression
	 */
	public static void printFontGrid(int fontX, int fontY, String fontStr, boolean flag) {
		printFont(fontX * 16, fontY * 16, fontStr, flag, COLOR_WHITE, COLOR_RED);
	}
}
