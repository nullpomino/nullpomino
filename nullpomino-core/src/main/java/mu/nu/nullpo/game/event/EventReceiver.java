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
package mu.nu.nullpo.game.event;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;

import mu.nu.nullpo.game.component.Block;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.util.CustomProperties;
import mu.nu.nullpo.util.GeneralUtil;

/**
 * Drawing and event handling EventReceiver
 */
public class EventReceiver {
	/** Log */
	static Logger log = Logger.getLogger(EventReceiver.class);

	/** Field X position */
	public static final int[][][] NEW_FIELD_OFFSET_X = {
		{	// TETROMINO
			{119, 247, 375, 503, 247, 375}, // Small
			{ 32, 432, 432, 432, 432, 432}, // Normal
			{ 16, 416, 416, 416, 416, 416}, // Big
		},
		{	// AVALANCHE
			{119, 247, 375, 503, 247, 375}, // Small
			{ 32, 432, 432, 432, 432, 432}, // Normal
			{ 16, 352, 352, 352, 352, 352}, // Big
		},
		{	// PHYSICIAN
			{119, 247, 375, 503, 247, 375}, // Small
			{ 32, 432, 432, 432, 432, 432}, // Normal
			{ 16, 416, 416, 416, 416, 416}, // Big
		},
		{	// SPF
			{119, 247, 375, 503, 247, 375}, // Small
			{ 32, 432, 432, 432, 432, 432}, // Normal
			{ 16, 352, 352, 352, 352, 352}, // Big
		},
	};
	/** Field Y position */
	public static final int[][][] NEW_FIELD_OFFSET_Y = {
		{	// TETROMINO
			{ 80,  80,  80,  80, 286, 286}, // Small
			{ 32,  32,  32,  32,  32,  32}, // Normal
			{  8,   8,   8,   8,   8,   8}, // Big
		},
		{	// AVALANCHE
			{ 80,  80,  80,  80, 286, 286}, // Small
			{ 32,  32,  32,  32,  32,  32}, // Normal
			{  8,   8,   8,   8,   8,   8}, // Big
		},
		{	// PHYSICIAN
			{ 80,  80,  80,  80, 286, 286}, // Small
			{ 32,  32,  32,  32,  32,  32}, // Normal
			{  8,   8,   8,   8,   8,   8}, // Big
		},
		{	// SPF
			{ 80,  80,  80,  80, 286, 286}, // Small
			{ 32,  32,  32,  32,  32,  32}, // Normal
			{ -8,  -8,  -8,  -8,  -8,  -8}, // Big
		},
	};

	/** Field X position (Big side preview) */
	public static final int[][][] NEW_FIELD_OFFSET_X_BSP = {
		{	// TETROMINO
			{208, 320, 432, 544, 320, 432}, // Small
			{ 64, 400, 400, 400, 400, 400}, // Normal
			{ 16, 352, 352, 352, 352, 352}, // Big
		},
		{	// AVALANCHE
			{208, 320, 432, 544, 320, 432}, // Small
			{ 64, 400, 400, 400, 400, 400}, // Normal
			{ 16, 352, 352, 352, 352, 352}, // Big
		},
		{	// PHYSICIAN
			{208, 320, 432, 544, 320, 432}, // Small
			{ 64, 400, 400, 400, 400, 400}, // Normal
			{ 16, 352, 352, 352, 352, 352}, // Big
		},
		{	// SPF
			{208, 320, 432, 544, 320, 432}, // Small
			{ 64, 400, 400, 400, 400, 400}, // Normal
			{ 16, 352, 352, 352, 352, 352}, // Big
		},
	};
	/** Field Y position (Big side preview) */
	public static final int[][][] NEW_FIELD_OFFSET_Y_BSP = {
		{	// TETROMINO
			{ 80,  80,  80,  80, 286, 286}, // Small
			{ 32,  32,  32,  32,  32,  32}, // Normal
			{  8,   8,   8,   8,   8,   8}, // Big
		},
		{	// AVALANCHE
			{ 80,  80,  80,  80, 286, 286}, // Small
			{ 32,  32,  32,  32,  32,  32}, // Normal
			{  8,   8,   8,   8,   8,   8}, // Big
		},
		{	// PHYSICIAN
			{ 80,  80,  80,  80, 286, 286}, // Small
			{ 32,  32,  32,  32,  32,  32}, // Normal
			{  8,   8,   8,   8,   8,   8}, // Big
		},
		{	// SPF
			{ 80,  80,  80,  80, 286, 286}, // Small
			{ 32,  32,  32,  32,  32,  32}, // Normal
			{-16, -16, -16, -16, -16, -16}, // Big
		},
	};

	/** Background display */
	protected boolean showbg;

	/** Show meter */
	protected boolean showmeter;

	/** Outline ghost piece */
	protected boolean outlineghost;

	/** Piece previews on sides */
	protected boolean sidenext;

	/** Use bigger side previews */
	protected boolean bigsidenext;

	/**
	 * Font color constants
	 */
	public static final int COLOR_WHITE = 0, COLOR_BLUE = 1, COLOR_RED = 2, COLOR_PINK = 3, COLOR_GREEN = 4, COLOR_YELLOW = 5, COLOR_CYAN = 6,
			COLOR_ORANGE = 7, COLOR_PURPLE = 8, COLOR_DARKBLUE = 9;

	/**
	 * Draw String inside the field.
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param str String to draw
	 * @param color Font color
	 * @param scale Font size (0.5f, 1.0f, 2.0f)
	 */
	public void drawMenuFont(GameEngine engine, int playerID, int x, int y, String str, int color, float scale) {}

	/**
	 * [You don't have to override this]
	 * Draw String inside the field. (Font color is white)
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param str String to draw
	 */
	public void drawMenuFont(GameEngine engine, int playerID, int x, int y, String str) {
		drawMenuFont(engine, playerID, x, y, str, COLOR_WHITE, 1.0f);
	}

	/**
	 * [You don't have to override this]
	 * Draw String inside the field. (Font color is white)
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param str String to draw
	 * @param scale Font size (0.5f, 1.0f, 2.0f)
	 */
	public void drawMenuFont(GameEngine engine, int playerID, int x, int y, String str, float scale) {
		drawMenuFont(engine, playerID, x, y, str, COLOR_WHITE, scale);
	}

	/**
	 * [You don't have to override this]
	 * Draw String inside the field.
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param str String to draw
	 * @param color Font color
	 */
	public void drawMenuFont(GameEngine engine, int playerID, int x, int y, String str, int color) {
		drawMenuFont(engine, playerID, x, y, str, color, 1.0f);
	}

	/**
	 * [You don't have to override this]
	 * Draw String inside the field.
	 * If flag is false, it will use colorF as font color. If flag is true, it will use colorT instead.
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param str String to draw
	 * @param flag Any boolean variable
	 * @param colorF Font color when flag is false
	 * @param colorT Font color when flag is true
	 */
	public void drawMenuFont(GameEngine engine, int playerID, int x, int y, String str, boolean flag, int colorF, int colorT) {
		if(flag == false) {
			drawMenuFont(engine, playerID, x, y, str, colorF, 1.0f);
		} else {
			drawMenuFont(engine, playerID, x, y, str, colorT, 1.0f);
		}
	}

	/**
	 * [You don't have to override this]
	 * Draw String inside the field.
	 * If flag is false, it will use white font color. If flag is true, it will use red instead.
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param str String to draw
	 * @param flag Any boolean variable
	 */
	public void drawMenuFont(GameEngine engine, int playerID, int x, int y, String str, boolean flag) {
		drawMenuFont(engine, playerID, x, y, str, flag, 1.0f);
	}

	/**
	 * [You don't have to override this]
	 * Draw String inside the field.
	 * If flag is false, it will use white font color. If flag is true, it will use red instead.
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param str String to draw
	 * @param flag Any boolean variable
	 * @param scale Font size
	 */
	public void drawMenuFont(GameEngine engine, int playerID, int x, int y, String str, boolean flag, float scale) {
		if(flag == false) {
			drawMenuFont(engine, playerID, x, y, str, COLOR_WHITE, scale);
		} else {
			int fontcolor = COLOR_RED;
			if(playerID == 1) fontcolor = COLOR_BLUE;
			drawMenuFont(engine, playerID, x, y, str, fontcolor, scale);
		}
	}

	/**
	 * Draw String inside the field by using a TTF font.
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param str String to draw
	 * @param color Font color
	 */
	public void drawTTFMenuFont(GameEngine engine, int playerID, int x, int y, String str, int color) {}

	/**
	 * [You don't have to override this]
	 * Draw String inside the field by using a TTF font. (Font color is white)
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param str String to draw
	 */
	public void drawTTFMenuFont(GameEngine engine, int playerID, int x, int y, String str) {
		drawTTFMenuFont(engine, playerID, x, y, str, COLOR_WHITE);
	}

	/**
	 * [You don't have to override this]
	 * Draw String inside the field by using a TTF font.
	 * If flag is false, it will use colorF as font color. If flag is true, it will use colorT instead.
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param str String to draw
	 * @param flag Any boolean variable
	 * @param colorF Font color when flag is false
	 * @param colorT Font color when flag is true
	 */
	public void drawTTFMenuFont(GameEngine engine, int playerID, int x, int y, String str, boolean flag, int colorF, int colorT) {
		if(flag == false) {
			drawTTFMenuFont(engine, playerID, x, y, str, colorF);
		} else {
			drawTTFMenuFont(engine, playerID, x, y, str, colorT);
		}
	}

	/**
	 * [You don't have to override this]
	 * Draw String inside the field by using a TTF font.
	 * If flag is false, it will use white font color. If flag is true, it will use red instead.
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param str String to draw
	 * @param flag Any boolean variable
	 */
	public void drawTTFMenuFont(GameEngine engine, int playerID, int x, int y, String str, boolean flag) {
		if(flag == false) {
			drawTTFMenuFont(engine, playerID, x, y, str, COLOR_WHITE);
		} else {
			int fontcolor = COLOR_RED;
			if(playerID == 1) fontcolor = COLOR_BLUE;
			drawTTFMenuFont(engine, playerID, x, y, str, fontcolor);
		}
	}

	/**
	 * Draw String to score display area.
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param str String to draw
	 * @param color Font color
	 * @param scale Font size (0.5f, 1.0f, 2.0f)
	 */
	public void drawScoreFont(GameEngine engine, int playerID, int x, int y, String str, int color, float scale) {}

	/**
	 * [You don't have to override this]
	 * Draw String to score display area. (Font color is white)
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param str String to draw
	 */
	public void drawScoreFont(GameEngine engine, int playerID, int x, int y, String str) {
		drawScoreFont(engine, playerID, x, y, str, COLOR_WHITE, 1.0f);
	}

	/**
	 * [You don't have to override this]
	 * Draw String to score display area. (Font color is white)
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param str String to draw
	 * @param scale Font size (0.5f, 1.0f, 2.0f)
	 */
	public void drawScoreFont(GameEngine engine, int playerID, int x, int y, String str, float scale) {
		drawScoreFont(engine, playerID, x, y, str, COLOR_WHITE, scale);
	}

	/**
	 * [You don't have to override this]
	 * Draw String to score display area.
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param str String to draw
	 * @param color Font color
	 */
	public void drawScoreFont(GameEngine engine, int playerID, int x, int y, String str, int color) {
		drawScoreFont(engine, playerID, x, y, str, color, 1.0f);
	}

	/**
	 * [You don't have to override this]
	 * Draw String to score display area.
	 * If flag is false, it will use colorF as font color. If flag is true, it will use colorT instead.
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param str String to draw
	 * @param flag Any boolean variable
	 * @param colorF Font color when flag is false
	 * @param colorT Font color when flag is true
	 */
	public void drawScoreFont(GameEngine engine, int playerID, int x, int y, String str, boolean flag, int colorF, int colorT) {
		if(flag == false) {
			drawScoreFont(engine, playerID, x, y, str, colorF, 1.0f);
		} else {
			drawScoreFont(engine, playerID, x, y, str, colorT, 1.0f);
		}
	}

	/**
	 * [You don't have to override this]
	 * Draw String to score display area.
	 * If flag is false, it will use white font color. If flag is true, it will use red instead.
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param str String to draw
	 * @param flag Any boolean variable
	 */
	public void drawScoreFont(GameEngine engine, int playerID, int x, int y, String str, boolean flag) {
		drawScoreFont(engine, playerID, x, y, str, flag, 1.0f);
	}

	/**
	 * [You don't have to override this]
	 * Draw String to score display area.
	 * If flag is false, it will use white font color. If flag is true, it will use red instead.
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param str String to draw
	 * @param flag Any boolean variable
	 * @param scale Font size
	 */
	public void drawScoreFont(GameEngine engine, int playerID, int x, int y, String str, boolean flag, float scale) {
		if(flag == false) {
			drawScoreFont(engine, playerID, x, y, str, COLOR_WHITE, scale);
		} else {
			int fontcolor = COLOR_RED;
			if(playerID == 1) fontcolor = COLOR_BLUE;
			drawScoreFont(engine, playerID, x, y, str, fontcolor, scale);
		}
	}

	/**
	 * Draw String to score display area by using a TTF font.
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param str String to draw
	 * @param color Font color
	 */
	public void drawTTFScoreFont(GameEngine engine, int playerID, int x, int y, String str, int color) {}

	/**
	 * [You don't have to override this]
	 * Draw String to score display area by using a TTF font. (Font color is white)
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param str String to draw
	 */
	public void drawTTFScoreFont(GameEngine engine, int playerID, int x, int y, String str) {
		drawTTFScoreFont(engine, playerID, x, y, str, COLOR_WHITE);
	}

	/**
	 * [You don't have to override this]
	 * Draw String to score display area by using a TTF font.
	 * If flag is false, it will use colorF as font color. If flag is true, it will use colorT instead.
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param str String to draw
	 * @param flag Any boolean variable
	 * @param colorF Font color when flag is false
	 * @param colorT Font color when flag is true
	 */
	public void drawTTFScoreFont(GameEngine engine, int playerID, int x, int y, String str, boolean flag, int colorF, int colorT) {
		if(flag == false) {
			drawTTFScoreFont(engine, playerID, x, y, str, colorF);
		} else {
			drawTTFScoreFont(engine, playerID, x, y, str, colorT);
		}
	}

	/**
	 * [You don't have to override this]
	 * Draw String to score display area by using a TTF font.
	 * If flag is false, it will use white font color. If flag is true, it will use red instead.
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param str String to draw
	 * @param flag Any boolean variable
	 */
	public void drawTTFScoreFont(GameEngine engine, int playerID, int x, int y, String str, boolean flag) {
		if(flag == false) {
			drawTTFScoreFont(engine, playerID, x, y, str, COLOR_WHITE);
		} else {
			int fontcolor = COLOR_RED;
			if(playerID == 1) fontcolor = COLOR_BLUE;
			drawTTFScoreFont(engine, playerID, x, y, str, fontcolor);
		}
	}

	/**
	 * Draw String to any location.
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param str String to draw
	 * @param color Font color
	 * @param scale Font size (0.5f, 1.0f, 2.0f)
	 */
	public void drawDirectFont(GameEngine engine, int playerID, int x, int y, String str, int color, float scale) {}

	/**
	 * [You don't have to override this]
	 * Draw String to any location. (Font color if white)
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param str String to draw
	 */
	public void drawDirectFont(GameEngine engine, int playerID, int x, int y, String str) {
		drawDirectFont(engine, playerID, x, y, str, COLOR_WHITE, 1.0f);
	}

	/**
	 * [You don't have to override this]
	 * Draw String to any location. (Font color if white)
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param str String to draw
	 * @param scale Font size (0.5f, 1.0f, 2.0f)
	 */
	public void drawDirectFont(GameEngine engine, int playerID, int x, int y, String str, float scale) {
		drawDirectFont(engine, playerID, x, y, str, COLOR_WHITE, scale);
	}

	/**
	 * [You don't have to override this]
	 * Draw String to any location.
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param str String to draw
	 * @param color Font color
	 */
	public void drawDirectFont(GameEngine engine, int playerID, int x, int y, String str, int color) {
		drawDirectFont(engine, playerID, x, y, str, color, 1.0f);
	}

	/**
	 * [You don't have to override this]
	 * Draw String to any location.
	 * If flag is false, it will use colorF as font color. If flag is true, it will use colorT instead.
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param str String to draw
	 * @param flag Any boolean variable
	 * @param colorF Font color when flag is false
	 * @param colorT Font color when flag is true
	 */
	public void drawDirectFont(GameEngine engine, int playerID, int x, int y, String str, boolean flag, int colorF, int colorT) {
		if(flag == false) {
			drawDirectFont(engine, playerID, x, y, str, colorF, 1.0f);
		} else {
			drawDirectFont(engine, playerID, x, y, str, colorT, 1.0f);
		}
	}

	/**
	 * [You don't have to override this]
	 * Draw String to any location.
	 * If flag is false, it will use white font color. If flag is true, it will use red instead.
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param str String to draw
	 * @param flag Any boolean variable
	 */
	public void drawDirectFont(GameEngine engine, int playerID, int x, int y, String str, boolean flag) {
		drawDirectFont(engine, playerID, x, y, str, flag, 1.0f);
	}

	/**
	 * [You don't have to override this]
	 * Draw String to any location.
	 * If flag is false, it will use white font color. If flag is true, it will use red instead.
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param str String to draw
	 * @param flag Any boolean variable
	 */
	public void drawDirectFont(GameEngine engine, int playerID, int x, int y, String str, boolean flag, float scale) {
		if(flag == false) {
			drawDirectFont(engine, playerID, x, y, str, COLOR_WHITE, scale);
		} else {
			int fontcolor = COLOR_RED;
			if(playerID == 1) fontcolor = COLOR_BLUE;
			drawDirectFont(engine, playerID, x, y, str, fontcolor, scale);
		}
	}

	/**
	 * Draw String to any location by using a TTF font.
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param str String to draw
	 * @param color Font color
	 */
	public void drawTTFDirectFont(GameEngine engine, int playerID, int x, int y, String str, int color) {}

	/**
	 * [You don't have to override this]
	 * Draw String to any location by using a TTF font. (Font color is white)
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param str String to draw
	 */
	public void drawTTFDirectFont(GameEngine engine, int playerID, int x, int y, String str) {
		drawTTFDirectFont(engine, playerID, x, y, str, COLOR_WHITE);
	}

	/**
	 * [You don't have to override this]
	 * Draw String to any location by using a TTF font.
	 * If flag is false, it will use colorF as font color. If flag is true, it will use colorT instead.
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param str String to draw
	 * @param flag Any boolean variable
	 * @param colorF Font color when flag is false
	 * @param colorT Font color when flag is true
	 */
	public void drawTTFDirectFont(GameEngine engine, int playerID, int x, int y, String str, boolean flag, int colorF, int colorT) {
		if(flag == false) {
			drawTTFDirectFont(engine, playerID, x, y, str, colorF);
		} else {
			drawTTFDirectFont(engine, playerID, x, y, str, colorT);
		}
	}

	/**
	 * [You don't have to override this]
	 * Draw String to any location by using a TTF font.
	 * If flag is false, it will use white font color. If flag is true, it will use red instead.
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param str String to draw
	 * @param flag Any boolean variable
	 */
	public void drawTTFDirectFont(GameEngine engine, int playerID, int x, int y, String str, boolean flag) {
		if(flag == false) {
			drawTTFDirectFont(engine, playerID, x, y, str, COLOR_WHITE);
		} else {
			int fontcolor = COLOR_RED;
			if(playerID == 1) fontcolor = COLOR_BLUE;
			drawTTFDirectFont(engine, playerID, x, y, str, fontcolor);
		}
	}

	/**
	 * Draw speed meter.
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param s Speed
	 */
	public void drawSpeedMeter(GameEngine engine, int playerID, int x, int y, int s) {}

	/**
	 * Draw a block
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param color Block color
	 * @param skin Block skin
	 * @param bone When true, it will use [] (bone) blocks
	 * @param darkness Brightness
	 * @param alpha Alpha-blending
	 * @param scale Size (0.5f, 1.0f, 2.0f)
	 */
	public void drawSingleBlock(GameEngine engine, int playerID, int x, int y, int color, int skin, boolean bone, float darkness, float alpha, float scale) {}

	/**
	 * Is TTF font available?
	 * @return true if you can use TTF font routines.
	 */
	public boolean isTTFSupport() {
		return false;
	}

	/**
	 * Get key name by button ID
	 * @param engine GameEngine
	 * @param btnID Button ID
	 * @return Key name
	 */
	public String getKeyNameByButtonID(GameEngine engine, int btnID) {
		return "";
	}

	/**
	 * Get maximum value of the meter.
	 * @param engine GameEngine
	 * @return Maximum value of the meter
	 */
	public int getMeterMax(GameEngine engine) {
		if(!showmeter) return 0;
		int blksize = 16;
		if (engine.displaysize == -1)
			blksize =  8;
		else if (engine.displaysize == 1)
			blksize = 32;
		return engine.fieldHeight * blksize;
	}

	/**
	 * Get width of block image.
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @return Width of block image
	 */
	public int getBlockGraphicsWidth(GameEngine engine, int playerID) {
		if (engine.displaysize == -1)
			return 8;
		else if (engine.displaysize == 1)
			return 32;
		else
			return 16;
	}

	/**
	 * Get height of block image.
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @return Height of block image
	 */
	public int getBlockGraphicsHeight(GameEngine engine, int playerID) {
		if (engine.displaysize == -1)
			return 8;
		else if (engine.displaysize == 1)
			return 32;
		else
			return 16;
	}

	/**
	 * Get X position of field
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @return X position of field
	 */
	public int getFieldDisplayPositionX(GameEngine engine, int playerID) {
		if(getNextDisplayType() == 2) return NEW_FIELD_OFFSET_X_BSP[engine.owner.mode.getGameStyle()][engine.displaysize + 1][playerID];
		return NEW_FIELD_OFFSET_X[engine.owner.mode.getGameStyle()][engine.displaysize + 1][playerID];
	}

	/**
	 * Get Y position of field
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @return Y position of field
	 */
	public int getFieldDisplayPositionY(GameEngine engine, int playerID) {
		if(getNextDisplayType() == 2) return NEW_FIELD_OFFSET_Y_BSP[engine.owner.mode.getGameStyle()][engine.displaysize + 1][playerID];
		return NEW_FIELD_OFFSET_Y[engine.owner.mode.getGameStyle()][engine.displaysize + 1][playerID];
	}

	/**
	 * Get X position of score display area
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @return X position of score display area
	 */
	public int getScoreDisplayPositionX(GameEngine engine, int playerID) {
		int xOffset = (getNextDisplayType() == 2) ? 256 : 216;
		if(engine.displaysize == 1) xOffset += 32;
		return getFieldDisplayPositionX(engine, playerID) + xOffset;
	}

	/**
	 * Get Y position of score display area
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @return Y position of score display area
	 */
	public int getScoreDisplayPositionY(GameEngine engine, int playerID) {
		return getFieldDisplayPositionY(engine, playerID) + 48;
	}

	/**
	 * Get type of piece preview
	 * @return 0=Above 1=Side Small 2=Side Big
	 */
	public int getNextDisplayType() {
		if(sidenext && bigsidenext) return 2;
		if(sidenext && !bigsidenext) return 1;
		return 0;
	}

	/**
	 * Check if the skin is sticky type
	 * @param skin Skin ID
	 * @return true if the skin is sticky type
	 */
	public boolean isStickySkin(int skin) {
		return false;
	}

	/**
	 * [You don't have to override this]
	 * Check if the current skin is sticky type
	 * @param engine GameEngine
	 * @return true if the current skin is sticky type
	 */
	public boolean isStickySkin(GameEngine engine) {
		return isStickySkin(engine.getSkin());
	}

	/**
	 * Play sound effects
	 * @param name Name of SFX
	 */
	public void playSE(String name) {}

	/**
	 * Set Graphics context
	 * @param g Graphics context
	 */
	public void setGraphics(Object g) {}

	/**
	 * Load properties from "config/setting/mode.cfg"
	 * @return Properties from "config/setting/mode.cfg". null if load fails.
	 */
	public CustomProperties loadModeConfig() {
		CustomProperties propModeConfig = new CustomProperties();

		try {
			FileInputStream in = new FileInputStream("config/setting/mode.cfg");
			propModeConfig.load(in);
			in.close();
		} catch(IOException e) {
			return null;
		}

		return propModeConfig;
	}

	/**
	 * Save properties to "config/setting/mode.cfg"
	 * @param modeConfig Properties you want to save
	 */
	public void saveModeConfig(CustomProperties modeConfig) {
		try {
			FileOutputStream out = new FileOutputStream("config/setting/mode.cfg");
			modeConfig.store(out, "NullpoMino Mode Config");
			out.close();
		} catch(IOException e) {
			log.error("Failed to save mode config", e);
		}
	}

	/**
	 * Load any properties from any location.
	 * @param filename Filename
	 * @return Properties you specified, or null if the file doesn't exist.
	 */
	public CustomProperties loadProperties(String filename) {
		CustomProperties prop = new CustomProperties();

		try {
			FileInputStream in = new FileInputStream(filename);
			prop.load(in);
			in.close();
		} catch(IOException e) {
			log.debug("Failed to load custom property file from " + filename, e);
			return null;
		}

		return prop;
	}

	/**
	 * Save any properties to any location.
	 * @param filename Filename
	 * @param prop Properties you want to save
	 * @return true if success
	 */
	public boolean saveProperties(String filename, CustomProperties prop) {
		try {
			FileOutputStream out = new FileOutputStream(filename);
			prop.store(out, "NullpoMino Custom Property File");
			out.close();
		} catch(IOException e) {
			log.debug("Failed to save custom property file to " + filename, e);
			return false;
		}

		return true;
	}

	/**
	 * It will be called before game screen appears.
	 * @param manager GameManager that owns this mode
	 */
	public void modeInit(GameManager manager) {}

	/**
	 * It will be called at the end of initialization for each player.
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	public void playerInit(GameEngine engine, int playerID) {}

	/**
	 * It will be called when Ready->Go is about to end, before first piece appears.
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	public void startGame(GameEngine engine, int playerID) {}

	/**
	 * It will be called at the start of each frame.
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	public void onFirst(GameEngine engine, int playerID) {}

	/**
	 * It will be called at the end of each frame.
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	public void onLast(GameEngine engine, int playerID) {}

	/**
	 * It will be called at the settings screen.
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	public void onSetting(GameEngine engine, int playerID) {}

	/**
	 * It will be called during the "Ready->Go" screen.
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	public void onReady(GameEngine engine, int playerID) {}

	/**
	 * It will be called during the piece movement.
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	public void onMove(GameEngine engine, int playerID) {}

	/**
	 * It will be called during the "Lock flash".
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	public void onLockFlash(GameEngine engine, int playerID) {}

	/**
	 * It will be called during the line clear.
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	public void onLineClear(GameEngine engine, int playerID) {}

	/**
	 * It will be called during the ARE.
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	public void onARE(GameEngine engine, int playerID) {}

	/**
	 * It will be called during the "Ending start" screen.
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	public void onEndingStart(GameEngine engine, int playerID) {}

	/**
	 * It will be called during the "Custom" screen.
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	public void onCustom(GameEngine engine, int playerID) {}

	/**
	 * It will be called during the "EXCELLENT!" screen.
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	public void onExcellent(GameEngine engine, int playerID) {}

	/**
	 * It will be called during the Game Over screen.
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	public void onGameOver(GameEngine engine, int playerID) {}

	/**
	 * It will be called during the end-of-game stats screen.
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	public void onResult(GameEngine engine, int playerID) {}

	/**
	 * It will be called during the field editor screen.
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	public void onFieldEdit(GameEngine engine, int playerID) {}

	/**
	 * It will be called at the start of each frame. (For rendering)
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	public void renderFirst(GameEngine engine, int playerID) {}

	/**
	 * It will be called at the end of each frame. (For rendering)
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	public void renderLast(GameEngine engine, int playerID) {}

	/**
	 * It will be called at the settings screen. (For rendering)
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	public void renderSetting(GameEngine engine, int playerID) {}

	/**
	 * It will be called during the "Ready->Go" screen. (For rendering)
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	public void renderReady(GameEngine engine, int playerID) {}

	/**
	 * It will be called during the piece movement. (For rendering)
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	public void renderMove(GameEngine engine, int playerID) {}

	/**
	 * It will be called during the "Lock flash". (For rendering)
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	public void renderLockFlash(GameEngine engine, int playerID) {}

	/**
	 * It will be called during the line clear. (For rendering)
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	public void renderLineClear(GameEngine engine, int playerID) {}

	/**
	 * It will be called during the ARE. (For rendering)
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	public void renderARE(GameEngine engine, int playerID) {}

	/**
	 * It will be called during the "Ending start" screen. (For rendering)
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	public void renderEndingStart(GameEngine engine, int playerID) {}

	/**
	 * It will be called during the "Custom" screen. (For rendering)
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	public void renderCustom(GameEngine engine, int playerID) {}

	/**
	 * It will be called during the "EXCELLENT!" screen. (For rendering)
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	public void renderExcellent(GameEngine engine, int playerID) {}

	/**
	 * It will be called during the Game Over screen. (For rendering)
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	public void renderGameOver(GameEngine engine, int playerID) {}

	/**
	 * It will be called during the end-of-game stats screen. (For rendering)
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	public void renderResult(GameEngine engine, int playerID) {}

	/**
	 * It will be called during the field editor screen. (For rendering)
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	public void renderFieldEdit(GameEngine engine, int playerID) {}

	/**
	 * It will be called if the player's input is being displayed. (For rendering)
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	public void renderInput(GameEngine engine, int playerID) {}

	/**
	 * It will be called when a block is cleared.
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param blk Block
	 */
	public void blockBreak(GameEngine engine, int playerID, int x, int y, Block blk) {}

	/**
	 * It will be called when the game mode is going to calculate score.
	 * Please note it will be called even if no lines are cleared.
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @param lines Number of lines cleared (0 if no line clear happened)
	 */
	public void calcScore(GameEngine engine, int playerID, int lines) {}

	/**
	 * After Soft Drop is used
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @param fall Number of rows the piece falled by Soft Drop
	 */
	public void afterSoftDropFall(GameEngine engine, int playerID, int fall) {}

	/**
	 * After Hard Drop is used
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @param fall Number of rows the piece falled by Hard Drop
	 */
	public void afterHardDropFall(GameEngine engine, int playerID, int fall) {}

	/**
	 * It will be called when the player exit the field editor.
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	public void fieldEditExit(GameEngine engine, int playerID) {}

	/**
	 * It will be called when the piece has locked. (after calcScore)
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @param lines Number of lines to be cleared (can be 0)
	 */
	public void pieceLocked(GameEngine engine, int playerID, int lines) {}

	/**
	 * It will be called at the end of line-clear phase.
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	public void lineClearEnd(GameEngine engine, int playerID) {}

	/**
	 * Called when saving replay
	 * @param owner GameManager
	 * @param prop CustomProperties where the replay is going to stored
	 */
	public void saveReplay(GameManager owner, CustomProperties prop) {}

	/**
	 * Called when saving replay (This is main body)
	 * @param owner GameManager
	 * @param prop CustomProperties where the replay is going to stored
	 * @param foldername Replay folder name
	 */
	public void saveReplay(GameManager owner, CustomProperties prop, String foldername) {
		if(owner.mode.isNetplayMode()) return;

		String filename = foldername + "/" + GeneralUtil.getReplayFilename();
		try {
			File repfolder = new File(foldername);
			if (!repfolder.exists()) {
				if (repfolder.mkdir()) {
					log.info("Created replay folder: " + foldername);
				} else {
					log.info("Couldn't create replay folder at "+ foldername);
				}
			}

			FileOutputStream out = new FileOutputStream(filename);
			prop.store(new FileOutputStream(filename), "NullpoMino Replay");
			out.close();
			log.info("Saved replay file: " + filename);
		} catch(IOException e) {
			log.error("Couldn't save replay file to " + filename, e);
		}
	}
}
