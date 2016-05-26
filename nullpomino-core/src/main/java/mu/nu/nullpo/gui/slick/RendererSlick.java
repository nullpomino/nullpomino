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

import java.util.ArrayList;

import mu.nu.nullpo.game.component.Block;
import mu.nu.nullpo.game.component.Field;
import mu.nu.nullpo.game.component.Piece;
import mu.nu.nullpo.game.event.EventReceiver;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.gui.EffectObject;
import mu.nu.nullpo.util.CustomProperties;

//import org.apache.log4j.Logger;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

/**
 * Game event Processing and rendering process (SlickVersion)
 */
public class RendererSlick extends EventReceiver {
	/** Log */
	//static Logger log = Logger.getLogger(RendererSlick.class);

	/** Surface to draw */
	protected Graphics graphics;

	/** Production Object */
	protected ArrayList<EffectObject> effectlist;

	/** Line clearDisplay Effects */
	protected boolean showlineeffect;

	/** Heavy production use */
	protected boolean heavyeffect;

	/** fieldBackgroundThe brightness of the */
	protected float fieldbgbright;

	/** Show field BG grid */
	protected boolean showfieldbggrid;

	/** NEXTDarken the field */
	protected boolean darknextarea;

	/** ghost On top of the pieceNEXTDisplay */
	protected boolean nextshadow;

	/** Line clear effect speed */
	protected int lineeffectspeed;

	/**
	 * Block colorIDDepending onSlickUseColorObjects created or received
	 * @param colorID Block colorID
	 * @return SlickUseColorObject
	 */
	public static Color getColorByID(int colorID) {
		switch(colorID) {
		case Block.BLOCK_COLOR_GRAY:   return new Color(Color.gray);
		case Block.BLOCK_COLOR_RED:    return new Color(Color.red);
		case Block.BLOCK_COLOR_ORANGE: return new Color(255,128,0);
		case Block.BLOCK_COLOR_YELLOW: return new Color(Color.yellow);
		case Block.BLOCK_COLOR_GREEN:  return new Color(Color.green);
		case Block.BLOCK_COLOR_CYAN:   return new Color(Color.cyan);
		case Block.BLOCK_COLOR_BLUE:   return new Color(Color.blue);
		case Block.BLOCK_COLOR_PURPLE: return new Color(Color.magenta);
		}
		return new Color(Color.black);
	}

	public static Color getMeterColorAsColor(int meterColor) {
		switch(meterColor) {
		case GameEngine.METER_COLOR_PINK:		return new Color(255,  0,255);
		case GameEngine.METER_COLOR_PURPLE:		return new Color(128,  0,255);
		case GameEngine.METER_COLOR_DARKBLUE:	return new Color(  0,  0,128);
		case GameEngine.METER_COLOR_BLUE:		return Color.blue;
		case GameEngine.METER_COLOR_CYAN:		return Color.cyan;
		case GameEngine.METER_COLOR_DARKGREEN:	return new Color(  0,128,  0);
		case GameEngine.METER_COLOR_GREEN:		return Color.green;
		case GameEngine.METER_COLOR_YELLOW:		return Color.yellow;
		case GameEngine.METER_COLOR_ORANGE:		return Color.orange;
		case GameEngine.METER_COLOR_RED:		return Color.red;
		}
		
		return Color.white;
	}

	/**
	 * Constructor
	 */
	public RendererSlick() {
		graphics = null;
		effectlist = new ArrayList<EffectObject>(10*4);

		showbg = NullpoMinoSlick.propConfig.getProperty("option.showbg", true);
		showlineeffect = NullpoMinoSlick.propConfig.getProperty("option.showlineeffect", true);
		heavyeffect = NullpoMinoSlick.propConfig.getProperty("option.heavyeffect", false);
		int bright = NullpoMinoSlick.propConfig.getProperty("option.fieldbgbright", 64) * 2;
		if(NullpoMinoSlick.propConfig.getProperty("option.fieldbgbright2") != null) {
			bright = NullpoMinoSlick.propConfig.getProperty("option.fieldbgbright2", 128);
		}
		if(bright > 255) bright = 255;
		fieldbgbright = bright / (float)255;
		showfieldbggrid = NullpoMinoSlick.propConfig.getProperty("option.showfieldbggrid", true);
		showmeter = NullpoMinoSlick.propConfig.getProperty("option.showmeter", true);
		darknextarea = NullpoMinoSlick.propConfig.getProperty("option.darknextarea", true);
		nextshadow = NullpoMinoSlick.propConfig.getProperty("option.nextshadow", false);
		lineeffectspeed = NullpoMinoSlick.propConfig.getProperty("option.lineeffectspeed", 0);
		outlineghost = NullpoMinoSlick.propConfig.getProperty("option.outlineghost", false);
		sidenext = NullpoMinoSlick.propConfig.getProperty("option.sidenext", false);
		bigsidenext = NullpoMinoSlick.propConfig.getProperty("option.bigsidenext", false);
	}

	/*
	 * Menu Drawing a string for
	 */
	@Override
	public void drawMenuFont(GameEngine engine, int playerID, int x, int y, String str, int color, float scale) {
		int x2 = (scale == 0.5f) ? x * 8 : x * 16;
		int y2 = (scale == 0.5f) ? y * 8 : y * 16;
		if(!engine.owner.menuOnly) {
			x2 += getFieldDisplayPositionX(engine, playerID) + 4;
			if(engine.displaysize == -1) {
				y2 += getFieldDisplayPositionY(engine, playerID) + 4;
			} else {
				y2 += getFieldDisplayPositionY(engine, playerID) + 52;
			}
		}
		NormalFontSlick.printFont(x2, y2, str, color, scale);
	}

	/*
	 * Menu A string forTTF font Drawing on
	 */
	@Override
	public void drawTTFMenuFont(GameEngine engine, int playerID, int x, int y, String str, int color) {
		int x2 = x * 16;
		int y2 = y * 16;
		if(!engine.owner.menuOnly) {
			x2 += getFieldDisplayPositionX(engine, playerID) + 4;
			if(engine.displaysize == -1) {
				y2 += getFieldDisplayPositionY(engine, playerID) + 4;
			} else {
				y2 += getFieldDisplayPositionY(engine, playerID) + 52;
			}
		}
		NormalFontSlick.printTTFFont(x2, y2, str, color);
	}

	/*
	 * Render scoreFor font Draw a
	 */
	@Override
	public void drawScoreFont(GameEngine engine, int playerID, int x, int y, String str, int color, float scale) {
		if(engine.owner.menuOnly) return;
		int size = (scale == 0.5f) ? 8 : 16;
		NormalFontSlick.printFont(getScoreDisplayPositionX(engine, playerID) + (x * size),
							 getScoreDisplayPositionY(engine, playerID) + (y * size),
							 str, color, scale);
	}

	/*
	 * Render scoreFor font ATTF font Drawing on
	 */
	@Override
	public void drawTTFScoreFont(GameEngine engine, int playerID, int x, int y, String str, int color) {
		if(engine.owner.menuOnly) return;
		NormalFontSlick.printTTFFont(getScoreDisplayPositionX(engine, playerID) + (x * 16),
								getScoreDisplayPositionY(engine, playerID) + (y * 16),
								str, color);
	}

	/*
	 * Draws the string to the specified coordinates I direct
	 */
	@Override
	public void drawDirectFont(GameEngine engine, int playerID, int x, int y, String str, int color, float scale) {
		NormalFontSlick.printFont(x, y, str, color, scale);
	}

	/*
	 * I can draw directly to the specified coordinatesTTF font Draw a
	 */
	@Override
	public void drawTTFDirectFont(GameEngine engine, int playerID, int x, int y, String str, int color) {
		NormalFontSlick.printTTFFont(x, y, str, color);
	}

	/*
	 * SpeedMeterDraw a
	 */
	@Override
	public void drawSpeedMeter(GameEngine engine, int playerID, int x, int y, int s) {
		if(graphics == null) return;
		if(engine.owner.menuOnly) return;

		int dx1 = getScoreDisplayPositionX(engine, playerID) + 6 + (x * 16);
		int dy1 = getScoreDisplayPositionY(engine, playerID) + 6 + (y * 16);

		graphics.setColor(Color.black);
		graphics.drawRect(dx1, dy1, 41, 3);
		graphics.setColor(Color.green);
		graphics.fillRect(dx1 + 1, dy1 + 1, 40, 2);

		int tempSpeedMeter = s;
		if((tempSpeedMeter < 0) || (tempSpeedMeter > 40)) tempSpeedMeter = 40;

		if(tempSpeedMeter > 0) {
			graphics.setColor(Color.red);
			graphics.fillRect(dx1 + 1, dy1 + 1, tempSpeedMeter, 2);
		}

		graphics.setColor(Color.white);
	}

	/*
	 * TTFAvailable
	 */
	@Override
	public boolean isTTFSupport() {
		return (ResourceHolderSlick.ttfFont != null);
	}

	/*
	 * Get key name by button ID
	 */
	@Override
	public String getKeyNameByButtonID(GameEngine engine, int btnID) {
		int[] keymap = engine.isInGame ? GameKeySlick.gamekey[engine.playerID].keymap : GameKeySlick.gamekey[engine.playerID].keymapNav;

		if((btnID >= 0) && (btnID < keymap.length)) {
			int keycode = keymap[btnID];
			String str = org.lwjgl.input.Keyboard.getKeyName(keycode);
			if(str == null) str = "(" + keycode + ")";
			return str;
		}

		return "";
	}

	/*
	 * Is the skin sticky?
	 */
	@Override
	public boolean isStickySkin(int skin) {
		if((skin >= 0) && (skin < ResourceHolderSlick.blockStickyFlagList.size()) && (ResourceHolderSlick.blockStickyFlagList.get(skin) == true)) {
			return true;
		}
		return false;
	}

	/*
	 * Sound effectsPlayback
	 */
	@Override
	public void playSE(String name) {
		ResourceHolderSlick.soundManager.play(name);
	}

	/*
	 * Set the target surface drawing
	 */
	@Override
	public void setGraphics(Object g) {
		if(g instanceof Graphics) {
			graphics = (Graphics)g;
		}
	}

	/*
	 * Save the replay
	 */
	@Override
	public void saveReplay(GameManager owner, CustomProperties prop) {
		if(owner.mode.isNetplayMode()) return;

		saveReplay(owner, prop, NullpoMinoSlick.propGlobal.getProperty("custom.replay.directory", "replay"));
	}

	/*
	 * 1MassBlockDraw a
	 */
	@Override
	public void drawSingleBlock(GameEngine engine, int playerID, int x, int y, int color, int skin, boolean bone, float darkness, float alpha, float scale) {
		drawBlock(x, y, color, skin, bone, darkness, alpha, scale);
	}

	/**
	 * Draw a block
	 * @param x X pos
	 * @param y Y pos
	 * @param color Color
	 * @param skin Skin
	 * @param bone true to use bone block ([][][][])
	 * @param darkness Darkness or brightness
	 * @param alpha Alpha
	 * @param scale Size (0.5f, 1.0f, 2.0f)
	 * @param attr Attribute
	 */
	protected void drawBlock(int x, int y, int color, int skin, boolean bone, float darkness, float alpha, float scale, int attr) {
		if(graphics == null) return;

		if((color <= Block.BLOCK_COLOR_INVALID)) return;
		if(skin >= ResourceHolderSlick.imgNormalBlockList.size()) skin = 0;

		boolean isSpecialBlocks = (color >= Block.BLOCK_COLOR_COUNT);
		boolean isSticky = ResourceHolderSlick.blockStickyFlagList.get(skin);

		int size = (int)(16 * scale);
		Image img = null;
		if(scale == 0.5f)
			img = ResourceHolderSlick.imgSmallBlockList.get(skin);
		else if(scale == 2.0f)
			img = ResourceHolderSlick.imgBigBlockList.get(skin);
		else
			img = ResourceHolderSlick.imgNormalBlockList.get(skin);

		int sx = color * size;
		if(bone) sx += 9 * size;
		int sy = 0;
		if(isSpecialBlocks) sx = ((color - Block.BLOCK_COLOR_COUNT) + 18) * size;

		if(isSticky) {
			if(isSpecialBlocks) {
				sx = (color - Block.BLOCK_COLOR_COUNT) * size;
				sy = 18 * size;
			} else {
				sx = 0;
				if((attr & Block.BLOCK_ATTRIBUTE_CONNECT_UP) != 0) sx |= 0x1;
				if((attr & Block.BLOCK_ATTRIBUTE_CONNECT_DOWN) != 0) sx |= 0x2;
				if((attr & Block.BLOCK_ATTRIBUTE_CONNECT_LEFT) != 0) sx |= 0x4;
				if((attr & Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT) != 0) sx |= 0x8;
				sx *= size;
				sy = color * size;
				if(bone) sy += 9 * size;
			}
		}

		int imageWidth = img.getWidth();
		if((sx >= imageWidth) && (imageWidth != -1)) sx = 0;
		int imageHeight = img.getHeight();
		if((sy >= imageHeight) && (imageHeight != -1)) sy = 0;

		Color filter = new Color(Color.white);
		filter.a = alpha;
		if(darkness > 0) {
			filter = filter.darker(darkness);
		}

		graphics.drawImage(img, x, y, x + size, y + size, sx, sy, sx + size, sy + size, filter);

		if(isSticky && !isSpecialBlocks) {
			int d = 16 * size;
			int h = (size/2);

			if( ((attr & Block.BLOCK_ATTRIBUTE_CONNECT_UP) != 0) && ((attr & Block.BLOCK_ATTRIBUTE_CONNECT_LEFT) != 0) )
				graphics.drawImage(img, x, y, x + h, y + h, d, sy, d + h, sy + h, filter);
			if( ((attr & Block.BLOCK_ATTRIBUTE_CONNECT_UP) != 0) && ((attr & Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT) != 0) )
				graphics.drawImage(img, x + h, y, x + h + h, y + h, d + h, sy, d + h + h, sy + h, filter);
			if( ((attr & Block.BLOCK_ATTRIBUTE_CONNECT_DOWN) != 0) && ((attr & Block.BLOCK_ATTRIBUTE_CONNECT_LEFT) != 0) )
				graphics.drawImage(img, x, y + h, x + h, y + h + h, d, sy + h, d + h, sy + h + h, filter);
			if( ((attr & Block.BLOCK_ATTRIBUTE_CONNECT_DOWN) != 0) && ((attr & Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT) != 0) )
				graphics.drawImage(img, x + h, y + h, x + h + h, y + h + h, d + h, sy + h, d + h + h, sy + h + h, filter);
		}

		if(darkness < 0) {
			Color brightfilter = new Color(Color.white);
			brightfilter.a = -darkness;
			graphics.setColor(brightfilter);
			graphics.fillRect(x, y, size, size);
		}
	}

	/**
	 * BlockDraw a
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param color Color
	 * @param skin Pattern
	 * @param bone BoneBlock
	 * @param darkness Lightness or darkness
	 * @param alpha Transparency
	 * @param scale Enlargement factor
	 */
	protected void drawBlock(int x, int y, int color, int skin, boolean bone, float darkness, float alpha, float scale) {
		drawBlock(x, y, color, skin, bone, darkness, alpha, scale, 0);
	}

	/**
	 * BlockUsing an instance of the classBlockDraw a
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param blk BlockInstance of a class
	 */
	protected void drawBlock(int x, int y, Block blk) {
		drawBlock(x, y, blk.getDrawColor(), blk.skin, blk.getAttribute(Block.BLOCK_ATTRIBUTE_BONE), blk.darkness, blk.alpha, 1.0f, blk.attribute);
	}

	/**
	 * BlockUsing an instance of the classBlockDraw a (You can specify the magnification)
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param blk BlockInstance of a class
	 * @param scale Enlargement factor
	 */
	protected void drawBlock(int x, int y, Block blk, float scale) {
		drawBlock(x, y, blk.getDrawColor(), blk.skin, blk.getAttribute(Block.BLOCK_ATTRIBUTE_BONE), blk.darkness, blk.alpha, scale, blk.attribute);
	}

	/**
	 * BlockUsing an instance of the classBlockDraw a (You can specify the magnification and dark)
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param blk BlockInstance of a class
	 * @param scale Enlargement factor
	 * @param darkness Lightness or darkness
	 */
	protected void drawBlock(int x, int y, Block blk, float scale, float darkness) {
		drawBlock(x, y, blk.getDrawColor(), blk.skin, blk.getAttribute(Block.BLOCK_ATTRIBUTE_BONE), darkness, blk.alpha, scale, blk.attribute);
	}

	protected void drawBlockForceVisible(int x, int y, Block blk, float scale) {
		drawBlock(x, y, blk.getDrawColor(), blk.skin, blk.getAttribute(Block.BLOCK_ATTRIBUTE_BONE), blk.darkness,
				(0.5f*blk.alpha)+0.5f, scale, blk.attribute);
	}

	/**
	 * BlockDraw a piece
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param piece Peace to draw
	 */
	protected void drawPiece(int x, int y, Piece piece) {
		drawPiece(x, y, piece, 1.0f);
	}

	/**
	 * BlockDraw a piece (You can specify the magnification)
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param piece Peace to draw
	 * @param scale Enlargement factor
	 */
	protected void drawPiece(int x, int y, Piece piece, float scale) {
		drawPiece(x, y, piece, scale, 0f);
	}

	/**
	 * BlockDraw a piece (You can specify the brightness or darkness)
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param piece Peace to draw
	 * @param scale Enlargement factor
	 * @param darkness Lightness or darkness
	 */
	protected void drawPiece(int x, int y, Piece piece, float scale, float darkness) {
		for(int i = 0; i < piece.getMaxBlock(); i++) {
			int x2 = x + (int)(piece.dataX[piece.direction][i] * 16 * scale);
			int y2 = y + (int)(piece.dataY[piece.direction][i] * 16 * scale);

			Block blkTemp = new Block(piece.block[i]);
			blkTemp.darkness = darkness;

			drawBlock(x2, y2, blkTemp, scale);
		}
	}

	/**
	 * Currently working onBlockDraw a piece (Y-coordinateThe0MoreBlockDisplay only)
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param engine GameEngineInstance of
	 * @param scale Display magnification
	 */
	protected void drawCurrentPiece(int x, int y, GameEngine engine, float scale) {
		Piece piece = engine.nowPieceObject;
		int blksize = (int)(16 * scale);

		if(piece != null) {
			for(int i = 0; i < piece.getMaxBlock(); i++) {
				if(!piece.big) {
					int x2 = engine.nowPieceX + piece.dataX[piece.direction][i];
					int y2 = engine.nowPieceY + piece.dataY[piece.direction][i];

					if(y2 >= 0) {
						Block blkTemp = piece.block[i];
						if(engine.nowPieceColorOverride >= 0) {
							blkTemp = new Block(piece.block[i]);
							blkTemp.color = engine.nowPieceColorOverride;
						}
						drawBlock(x + (x2 * blksize), y + (y2 * blksize), blkTemp, scale);
					}
				} else {
					int x2 = engine.nowPieceX + (piece.dataX[piece.direction][i] * 2);
					int y2 = engine.nowPieceY + (piece.dataY[piece.direction][i] * 2);

					Block blkTemp = piece.block[i];
					if(engine.nowPieceColorOverride >= 0) {
						blkTemp = new Block(piece.block[i]);
						blkTemp.color = engine.nowPieceColorOverride;
					}
					drawBlock(x + (x2 * blksize), y + (y2 * blksize), blkTemp, scale * 2.0f);
				}
			}
		}
	}

	/**
	 * Currently working onBlockOf Peaceghost Draw a
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param engine GameEngineInstance of
	 * @param scale Display magnification
	 */
	protected void drawGhostPiece(int x, int y, GameEngine engine, float scale) {
		Piece piece = engine.nowPieceObject;
		int blksize = (int)(16 * scale);

		if(piece != null) {
			for(int i = 0; i < piece.getMaxBlock(); i++) {
				if(!piece.big) {
					int x2 = engine.nowPieceX + piece.dataX[piece.direction][i];
					int y2 = engine.nowPieceBottomY + piece.dataY[piece.direction][i];

					if(y2 >= 0) {
						if(outlineghost) {
							Block blkTemp = piece.block[i];
							int x3 = x + (x2 * blksize);
							int y3 = y + (y2 * blksize);
							int ls = (blksize-1);

							int colorID = blkTemp.getDrawColor();
							if(blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_BONE)) colorID = -1;
							Color color = getColorByID(colorID);
							if(showbg) {
								color.a = 0.5f;
							} else {
								color = color.darker(0.5f);
							}
							graphics.setColor(color);
							graphics.fillRect(x3, y3, blksize, blksize);
							graphics.setColor(Color.white);

							if(!blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_UP)) {
								graphics.drawLine(x3, y3, x3 + ls, y3);
								graphics.drawLine(x3, y3 + 1, x3 + ls, y3 + 1);
							}
							if(!blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_DOWN)) {
								graphics.drawLine(x3, y3 + ls, x3 + ls, y3 + ls);
								graphics.drawLine(x3, y3 - 1 + ls, x3 + ls, y3 - 1 + ls);
							}
							if(!blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_LEFT)) {
								graphics.drawLine(x3, y3, x3, y3 + ls);
								graphics.drawLine(x3 + 1, y3, x3 + 1, y3 + ls);
							}
							if(!blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT)) {
								graphics.drawLine(x3 + ls, y3, x3 + ls, y3 + ls);
								graphics.drawLine(x3 - 1 + ls, y3, x3 - 1 + ls, y3 + ls);
							}
							if(blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_LEFT | Block.BLOCK_ATTRIBUTE_CONNECT_UP)) {
								graphics.fillRect(x3, y3, 2, 2);
							}
							if(blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_LEFT | Block.BLOCK_ATTRIBUTE_CONNECT_DOWN)) {
								graphics.fillRect(x3, y3 + (blksize-2), 2, 2);
							}
							if(blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT | Block.BLOCK_ATTRIBUTE_CONNECT_UP)) {
								graphics.fillRect(x3 + (blksize-2), y3, 2, 2);
							}
							if(blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT | Block.BLOCK_ATTRIBUTE_CONNECT_DOWN)) {
								graphics.fillRect(x3 + (blksize-2), y3 + (blksize-2), 2, 2);
							}
						} else {
							Block blkTemp = new Block(piece.block[i]);
							blkTemp.darkness = 0.3f;
							if(engine.nowPieceColorOverride >= 0) {
								blkTemp.color = engine.nowPieceColorOverride;
							}
							drawBlock(x + (x2 * blksize), y + (y2 * blksize), blkTemp, scale);
						}
					}
				} else {
					int x2 = engine.nowPieceX + (piece.dataX[piece.direction][i] * 2);
					int y2 = engine.nowPieceBottomY + (piece.dataY[piece.direction][i] * 2);

					if(outlineghost) {
						Block blkTemp = piece.block[i];
						int x3 = x + (x2 * blksize);
						int y3 = y + (y2 * blksize);
						int ls = (blksize * 2 -1);

						int colorID = blkTemp.getDrawColor();
						if(blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_BONE)) colorID = -1;
						Color color = getColorByID(colorID);
						if(showbg) {
							color.a = 0.5f;
						} else {
							color = color.darker(0.5f);
						}
						graphics.setColor(color);
						graphics.fillRect(x3, y3, blksize * 2, blksize * 2);
						graphics.setColor(Color.white);

						if(!blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_UP)) {
							graphics.drawLine(x3, y3, x3 + ls, y3);
							graphics.drawLine(x3, y3 + 1, x3 + ls, y3 + 1);
						}
						if(!blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_DOWN)) {
							graphics.drawLine(x3, y3 + ls, x3 + ls, y3 + ls);
							graphics.drawLine(x3, y3 - 1 + ls, x3 + ls, y3 - 1 + ls);
						}
						if(!blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_LEFT)) {
							graphics.drawLine(x3, y3, x3, y3 + ls);
							graphics.drawLine(x3 + 1, y3, x3 + 1, y3 + ls);
						}
						if(!blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT)) {
							graphics.drawLine(x3 + ls, y3, x3 + ls, y3 + ls);
							graphics.drawLine(x3 - 1 + ls, y3, x3 - 1 + ls, y3 + ls);
						}
						if(blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_LEFT | Block.BLOCK_ATTRIBUTE_CONNECT_UP)) {
							graphics.fillRect(x3, y3, 2, 2);
						}
						if(blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_LEFT | Block.BLOCK_ATTRIBUTE_CONNECT_DOWN)) {
							graphics.fillRect(x3, y3 + (blksize*2-2), 2, 2);
						}
						if(blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT | Block.BLOCK_ATTRIBUTE_CONNECT_UP)) {
							graphics.fillRect(x3 + (blksize*2-2), y3, 2, 2);
						}
						if(blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT | Block.BLOCK_ATTRIBUTE_CONNECT_DOWN)) {
							graphics.fillRect(x3 + (blksize*2-2), y3 + (blksize*2-2), 2, 2);
						}
					} else {
						Block blkTemp = new Block(piece.block[i]);
						blkTemp.darkness = 0.3f;
						if(engine.nowPieceColorOverride >= 0) {
							blkTemp.color = engine.nowPieceColorOverride;
						}
						drawBlock(x + (x2 * blksize), y + (y2 * blksize), blkTemp, scale * 2.0f);
					}
				}
			}
		}
	}

	protected void drawHintPiece(int x, int y, GameEngine engine, float scale) {
		Piece piece = engine.aiHintPiece;
		if (piece != null) {
			piece.direction=engine.ai.bestRt;
			piece.updateConnectData();
			int blksize = (int)(16 * scale);

			if(piece != null) {
				for(int i = 0; i < piece.getMaxBlock(); i++) {
					if(!piece.big) {
						int x2 = engine.ai.bestX + piece.dataX[engine.ai.bestRt][i];
						int y2 = engine.ai.bestY + piece.dataY[engine.ai.bestRt][i];

						if(y2 >= 0) {

							Block blkTemp = piece.block[i];
							int x3 = x + (x2 * blksize);
							int y3 = y + (y2 * blksize);
							int ls = (blksize-1);

							int colorID = blkTemp.getDrawColor();
							if(blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_BONE)) colorID = -1;
							Color color = getColorByID(colorID);
							graphics.setColor(color);

							if(!blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_UP))
								graphics.fillRect(x3, y3, ls, 2);
							if(!blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_DOWN))
								graphics.fillRect(x3, y3 + ls - 1, ls, 2);
							if(!blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_LEFT))
								graphics.fillRect(x3, y3, 2, ls);
							if(!blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT))
								graphics.fillRect(x3 + ls - 1, y3, 2, ls);
							if(blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_LEFT | Block.BLOCK_ATTRIBUTE_CONNECT_UP))
								graphics.fillRect(x3, y3, 2, 2);
							if(blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_LEFT | Block.BLOCK_ATTRIBUTE_CONNECT_DOWN))
								graphics.fillRect(x3, y3 + (blksize-2), 2, 2);
							if(blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT | Block.BLOCK_ATTRIBUTE_CONNECT_UP))
								graphics.fillRect(x3 + (blksize-2), y3, 2, 2);
							if(blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT | Block.BLOCK_ATTRIBUTE_CONNECT_DOWN))
								graphics.fillRect(x3 + (blksize-2), y3 + (blksize-2), 2, 2);
						}
					} else {
						int x2 = engine.ai.bestX + (piece.dataX[engine.ai.bestRt][i] * 2);
						int y2 = engine.ai.bestY + (piece.dataY[engine.ai.bestRt][i] * 2);

						Block blkTemp = piece.block[i];
						int x3 = x + (x2 * blksize);
						int y3 = y + (y2 * blksize);
						int ls = (blksize * 2 -1);

						int colorID = blkTemp.getDrawColor();
						if(blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_BONE)) colorID = -1;
						Color color = getColorByID(colorID);
						if(showbg) {
							color.a = 0.5f;
						} else {
							color = color.darker(0.5f);
						}
						graphics.setColor(color);
						//graphics.fillRect(x3, y3, blksize * 2, blksize * 2);
						graphics.setColor(Color.white);

						if(!blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_UP)) {
							graphics.drawLine(x3, y3, x3 + ls, y3);
							graphics.drawLine(x3, y3 + 1, x3 + ls, y3 + 1);
						}
						if(!blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_DOWN)) {
							graphics.drawLine(x3, y3 + ls, x3 + ls, y3 + ls);
							graphics.drawLine(x3, y3 - 1 + ls, x3 + ls, y3 - 1 + ls);
						}
						if(!blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_LEFT)) {
							graphics.drawLine(x3, y3, x3, y3 + ls);
							graphics.drawLine(x3 + 1, y3, x3 + 1, y3 + ls);
						}
						if(!blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT)) {
							graphics.drawLine(x3 + ls, y3, x3 + ls, y3 + ls);
							graphics.drawLine(x3 - 1 + ls, y3, x3 - 1 + ls, y3 + ls);
						}
						if(blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_LEFT | Block.BLOCK_ATTRIBUTE_CONNECT_UP)) {
							graphics.fillRect(x3, y3, 2, 2);
						}
						if(blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_LEFT | Block.BLOCK_ATTRIBUTE_CONNECT_DOWN)) {
							graphics.fillRect(x3, y3 + (blksize*2-2), 2, 2);
						}
						if(blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT | Block.BLOCK_ATTRIBUTE_CONNECT_UP)) {
							graphics.fillRect(x3 + (blksize*2-2), y3, 2, 2);
						}
						if(blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT | Block.BLOCK_ATTRIBUTE_CONNECT_DOWN)) {
							graphics.fillRect(x3 + (blksize*2-2), y3 + (blksize*2-2), 2, 2);
						}
					}
				}
			}
		}
	}

	/**
	 * fieldOfBlockDraw a
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param engine GameEngineInstance of
	 * @param small Half size
	 */
	protected void drawField(int x, int y, GameEngine engine, int size) {
		if(graphics == null) return;

		int blksize = 16;
		float scale = 1.0f;
		if (size == -1) {
			blksize = 8;
			scale = 0.5f;
		} else if (size == 1){
			blksize = 32;
			scale = 2.0f;
		}

		Field field = engine.field;
		int width = 10;
		int height = 20;
		int viewHeight = 20;

		if(field != null) {
			width = field.getWidth();
			viewHeight = height = field.getHeight();
		}
		if((engine.heboHiddenEnable) && (engine.gameActive) && (field != null)) {
			viewHeight -= engine.heboHiddenYNow;
		}

		int outlineType = engine.blockOutlineType;
		if(engine.owBlockOutlineType != -1) outlineType = engine.owBlockOutlineType;

		for(int i = 0; i < viewHeight; i++) {
			for(int j = 0; j < width; j++) {
				int x2 = x + (j * blksize);
				int y2 = y + (i * blksize);

				Block blk = null;
				if(field != null) blk = field.getBlock(j, i);

				if((field != null) && (blk != null) && (blk.color > Block.BLOCK_COLOR_NONE)) {
					if(blk.getAttribute(Block.BLOCK_ATTRIBUTE_WALL)) {
						drawBlock(x2, y2, Block.BLOCK_COLOR_NONE, blk.skin, blk.getAttribute(Block.BLOCK_ATTRIBUTE_BONE),
								  blk.darkness, blk.alpha, scale, blk.attribute);
					} else if (engine.owner.replayMode && engine.owner.replayShowInvisible) {
						drawBlockForceVisible(x2, y2, blk, scale);
					} else if(blk.getAttribute(Block.BLOCK_ATTRIBUTE_VISIBLE)) {
						drawBlock(x2, y2, blk, scale);
					}

					if(blk.getAttribute(Block.BLOCK_ATTRIBUTE_OUTLINE) && !blk.getAttribute(Block.BLOCK_ATTRIBUTE_BONE)) {
						Color filter = new Color(Color.white);
						filter.a = blk.alpha;
						graphics.setColor(filter);
						int ls = (blksize-1);
						if(outlineType == GameEngine.BLOCK_OUTLINE_NORMAL) {
							if(field.getBlockColor(j, i - 1) == Block.BLOCK_COLOR_NONE) graphics.drawLine(x2, y2, x2 + ls, y2);
							if(field.getBlockColor(j, i + 1) == Block.BLOCK_COLOR_NONE) graphics.drawLine(x2, y2 + ls, x2 + ls, y2 + ls);
							if(field.getBlockColor(j - 1, i) == Block.BLOCK_COLOR_NONE) graphics.drawLine(x2, y2, x2, y2 + ls);
							if(field.getBlockColor(j + 1, i) == Block.BLOCK_COLOR_NONE) graphics.drawLine(x2 + ls, y2, x2 + ls, y2 + ls);
						} else if(outlineType == GameEngine.BLOCK_OUTLINE_CONNECT) {
							if(!blk.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_UP))     graphics.drawLine(x2, y2, x2 + ls, y2);
							if(!blk.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_DOWN))   graphics.drawLine(x2, y2 + ls, x2 + ls, y2 + ls);
							if(!blk.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_LEFT))   graphics.drawLine(x2, y2, x2, y2 + ls);
							if(!blk.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT))  graphics.drawLine(x2 + ls, y2, x2 + ls, y2 + ls);
						} else if(outlineType == GameEngine.BLOCK_OUTLINE_SAMECOLOR) {
							if(field.getBlockColor(j, i - 1) != blk.color) graphics.drawLine(x2, y2, x2 + ls, y2);
							if(field.getBlockColor(j, i + 1) != blk.color) graphics.drawLine(x2, y2 + ls, x2 + ls, y2 + ls);
							if(field.getBlockColor(j - 1, i) != blk.color) graphics.drawLine(x2, y2, x2, y2 + ls);
							if(field.getBlockColor(j + 1, i) != blk.color) graphics.drawLine(x2 + ls, y2, x2 + ls, y2 + ls);
						}
					}

					graphics.setColor(Color.white);
				}
			}
		}

		// BunglerHIDDEN
		if((engine.heboHiddenEnable) && (engine.gameActive) && (field != null)) {
			int maxY = engine.heboHiddenYNow;
			if(maxY > height) maxY = height;
			for(int i = 0; i < maxY; i++) {
				for(int j = 0; j < width; j++) {
					drawBlock(x + (j * blksize), y + ((height - 1 - i) * blksize), Block.BLOCK_COLOR_GRAY, 0, false, 0.0f, 1.0f, scale);
				}
			}
		}
	}

	/**
	 * Field frameDraw a
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param engine GameEngineInstance of
	 * @param small Half size
	 */
	protected void drawFrame(int x, int y, GameEngine engine, int displaysize) {
		if(graphics == null) return;

		int size = 4;
		if (displaysize == -1)
			size = 2;
		else if (displaysize == 1)
			size = 8;
		int width = 10;
		int height = 20;
		int offsetX = 0;

		if(engine.field != null) {
			width = engine.field.getWidth();
			height = engine.field.getHeight();
		}
		if(engine != null) {
			offsetX = engine.framecolor * 16;
		}

		// Field Background
		if(fieldbgbright > 0) {
			if((width <= 10) && (height <= 20) && (showfieldbggrid)) {
				Color filter = new Color(Color.white);
				filter.a = fieldbgbright;

				Image img = ResourceHolderSlick.imgFieldbg2;
				if(displaysize == -1) img = ResourceHolderSlick.imgFieldbg2Small;
				if(displaysize == 1) img = ResourceHolderSlick.imgFieldbg2Big;

				graphics.drawImage(img, x + 4, y + 4, (x + 4)+(width*size*4), (y + 4)+(height*size*4), 0, 0, width*size*4, height*size*4, filter);
			} else if(showbg) {
				Color filter = new Color(Color.black);
				filter.a = fieldbgbright;
				graphics.setColor(filter);
				graphics.fillRect(x + 4, y + 4, width * size*4, height * size*4);
				graphics.setColor(Color.white);
			}
		}

		// UpAnd the lower
		int maxWidth = (width * size * 4);
		if(showmeter) maxWidth = (width * size * 4) + (2 * 4);

		int tmpX = 0;
		int tmpY = 0;

		tmpX = x + 4;
		tmpY = y;
		graphics.drawImage(ResourceHolderSlick.imgFrame, tmpX, tmpY, tmpX + maxWidth, tmpY + 4, offsetX + 4, 0, (offsetX + 4) + 4, 4);
		tmpY = y + (height * size * 4) + 4;
		graphics.drawImage(ResourceHolderSlick.imgFrame, tmpX, tmpY, tmpX + maxWidth, tmpY + 4, offsetX + 4, 8, (offsetX + 4) + 4, 8 + 4);

		// Left and Right
		tmpX = x;
		tmpY = y + 4;
		graphics.drawImage(ResourceHolderSlick.imgFrame, tmpX, tmpY, tmpX + 4, tmpY + (height * size*4), offsetX, 4, offsetX + 4, 4 + 4);

		if(showmeter) {
			tmpX = x + (width * size * 4) + 12;
		} else {
			tmpX = x + (width * size * 4) + 4;
		}
		graphics.drawImage(ResourceHolderSlick.imgFrame, tmpX, tmpY, tmpX + 4, tmpY + (height * size*4), offsetX + 8, 4, offsetX + 8 + 4, 4 + 4);

		// Upper left
		tmpX = x;
		tmpY = y;
		graphics.drawImage(ResourceHolderSlick.imgFrame, tmpX, tmpY, tmpX + 4, tmpY + 4, offsetX, 0, offsetX + 4, 4);

		// Lower left
		tmpX = x;
		tmpY = y + (height * size * 4) + 4;
		graphics.drawImage(ResourceHolderSlick.imgFrame, tmpX, tmpY, tmpX + 4, tmpY + 4, offsetX, 8, offsetX + 4, 8 + 4);

		if(showmeter) {
			// MeterONWhen the upper right corner of the
			tmpX = x + (width * size * 4) + 12;
			tmpY = y;
			graphics.drawImage(ResourceHolderSlick.imgFrame, tmpX, tmpY, tmpX + 4, tmpY + 4, offsetX + 8, 0, (offsetX + 8) + 4, 4);

			// MeterONWhen the lower-right corner of
			tmpX = x + (width * size * 4) + 12;
			tmpY = y + (height * size * 4) + 4;
			graphics.drawImage(ResourceHolderSlick.imgFrame, tmpX, tmpY, tmpX + 4, tmpY + 4, offsetX + 8, 8, (offsetX + 8) + 4, 8 + 4);

			// RightMeterFrame
			tmpX = x + (width * size * 4) + 4;
			tmpY = y + 4;
			graphics.drawImage(ResourceHolderSlick.imgFrame, tmpX, tmpY, tmpX + 4, tmpY + (height * size * 4), offsetX + 12, 4, (offsetX + 12) + 4, 4 + 4);

			tmpX = x + (width * size * 4) + 4;
			tmpY = y;
			graphics.drawImage(ResourceHolderSlick.imgFrame, tmpX, tmpY, tmpX + 4, tmpY + 4, offsetX + 12, 0, (offsetX + 12) + 4, 4);

			tmpX = x + (width * size * 4) + 4;
			tmpY = y + (height * size * 4) + 4;
			graphics.drawImage(ResourceHolderSlick.imgFrame, tmpX, tmpY, tmpX + 4, tmpY + 4, offsetX + 12, 8, (offsetX + 12) + 4, 8 + 4);

			// RightMeter
			int maxHeight = height * size * 4;
			if((engine != null) && (engine.meterValueSub > 0 || engine.meterValue > 0))
				maxHeight -= Math.max(engine.meterValue, engine.meterValueSub);

			tmpX = x + (width * size * 4) + 8;
			tmpY = y + 4;

			if(maxHeight > 0) {
				graphics.setColor(Color.black);
				graphics.fillRect(tmpX, tmpY, 4, maxHeight);
				graphics.setColor(Color.white);
			}

			if(engine != null) {
				if (engine.meterValueSub > Math.max(engine.meterValue, 0)) {
					int value = engine.meterValueSub;
					if(value > height * size * 4) value = height * size * 4;

					if(value > 0) {
						tmpX = x + (width * size * 4) + 8;
						tmpY = y + (height * size * 4) + 3 - (value - 1);

						graphics.setColor(getMeterColorAsColor(engine.meterColorSub));
						graphics.fillRect(tmpX, tmpY, 4, value);
						graphics.setColor(Color.white);
					}
				}
				if (engine.meterValue > 0) {
					int value = engine.meterValue;
					if(value > height * size * 4) value = height * size * 4;

					if(value > 0) {
						tmpX = x + (width * size * 4) + 8;
						tmpY = y + (height * size * 4) + 3 - (value - 1);

						graphics.setColor(getMeterColorAsColor(engine.meterColor));
						graphics.fillRect(tmpX, tmpY, 4, value);
						graphics.setColor(Color.white);
					}
				}
			}
		} else {
			// MeterOFFWhen the upper right corner of the
			tmpX = x + (width * size * 4) + 4;
			tmpY = y;
			graphics.drawImage(ResourceHolderSlick.imgFrame, tmpX, tmpY, tmpX + 4, tmpY + 4, offsetX + 8, 0, (offsetX + 8) + 4, 4);

			// MeterOFFWhen the lower-right corner of
			tmpX = x + (width * size * 4) + 4;
			tmpY = y + (height * size * 4) + 4;
			graphics.drawImage(ResourceHolderSlick.imgFrame, tmpX, tmpY, tmpX + 4, tmpY + 4, offsetX + 8, 8, (offsetX + 8) + 4, 8 + 4);
		}
	}

	/**
	 * NEXTDraw a
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param engine GameEngineInstance of
	 */
	protected void drawNext(int x, int y, GameEngine engine) {
		if(graphics == null) return;

		int fldWidth = 10;
		int fldBlkSize = 16;
		int meterWidth = showmeter ? 8 : 0;
		if((engine != null) && (engine.field != null)) {
			fldWidth = engine.field.getWidth();
			if(engine.displaysize == 1) fldBlkSize = 32;
		}

		// NEXT area background
		if(showbg && darknextarea) {
			Color filter = new Color(Color.black);
			graphics.setColor(filter);

			if(getNextDisplayType() == 2) {
				int x2 = x + 8 + (fldWidth * fldBlkSize) + meterWidth;
				int maxNext = engine.isNextVisible ? engine.ruleopt.nextDisplay : 0;

				// HOLD area
				if(engine.ruleopt.holdEnable && engine.isHoldVisible) {
					graphics.fillRect(x - 64, y + 48 + 8, 64, 64 - 16);
					for(int i = 0; i <= 8; i++) {
						Color filter2 = new Color(Color.black);
						filter2.a = ((float)i / (float)8);
						graphics.setColor(filter2);
						graphics.fillRect(x - 64, y + 47 + i, 64, 1);
					}
					for(int i = 0; i <= 8; i++) {
						Color filter2 = new Color(Color.black);
						filter2.a = ((float)i / (float)8);
						graphics.setColor(filter2);
						graphics.fillRect(x - 64, y + 112 - i, 64, 1);
					}
				}

				// NEXT area
				if(maxNext > 0) {
					graphics.fillRect(x2, y + 48 + 8, 64, (64 * maxNext) - 16);
					for(int i = 0; i <= 8; i++) {
						Color filter2 = new Color(Color.black);
						filter2.a = ((float)i / (float)8);
						graphics.setColor(filter2);
						graphics.fillRect(x2, y + 47 + i, 64, 1);
					}
					for(int i = 0; i <= 8; i++) {
						Color filter2 = new Color(Color.black);
						filter2.a = ((float)i / (float)8);
						graphics.setColor(filter2);
						graphics.fillRect(x2, y + 48 + (64 * maxNext) - i, 64, 1);
					}
				}
			} else if(getNextDisplayType() == 1) {
				int x2 = x + 8 + (fldWidth * fldBlkSize) + meterWidth;
				int maxNext = engine.isNextVisible ? engine.ruleopt.nextDisplay : 0;

				// HOLD area
				if(engine.ruleopt.holdEnable && engine.isHoldVisible) {
					graphics.fillRect(x - 32, y + 48 + 8, 32, 32 - 16);
					for(int i = 0; i <= 8; i++) {
						Color filter2 = new Color(Color.black);
						filter2.a = ((float)i / (float)8);
						graphics.setColor(filter2);
						graphics.fillRect(x - 32, y + 47 + i, 32, 1);
					}
					for(int i = 0; i <= 8; i++) {
						Color filter2 = new Color(Color.black);
						filter2.a = ((float)i / (float)8);
						graphics.setColor(filter2);
						graphics.fillRect(x - 32, y + 80 - i, 32, 1);
					}
				}

				// NEXT area
				if(maxNext > 0) {
					graphics.fillRect(x2, y + 48 + 8, 32, (32 * maxNext) - 16);
					for(int i = 0; i <= 8; i++) {
						Color filter2 = new Color(Color.black);
						filter2.a = ((float)i / (float)8);
						graphics.setColor(filter2);
						graphics.fillRect(x2, y + 47 + i, 32, 1);
					}
					for(int i = 0; i <= 8; i++) {
						Color filter2 = new Color(Color.black);
						filter2.a = ((float)i / (float)8);
						graphics.setColor(filter2);
						graphics.fillRect(x2, y + 48 + (32 * maxNext) - i, 32, 1);
					}
				}
			} else {
				int w = (fldWidth * fldBlkSize) + 15;

				graphics.fillRect(x + 20, y, w - 40, 48);

				for(int i = 0; i <= 20; i++) {
					Color filter2 = new Color(Color.black);
					filter2.a = ((float)i / (float)20);
					graphics.setColor(filter2);
					graphics.fillRect(x + i - 1, y, 1, 48);
				}
				for(int i = 0; i <= 20; i++) {
					Color filter2 = new Color(Color.black);
					filter2.a = ((float)(20 - i) / (float)20);
					graphics.setColor(filter2);
					graphics.fillRect(x + i + (w - 20), y, 1, 48);
				}
			}

			graphics.setColor(Color.white);
		}

		if(engine.isNextVisible) {
			if(getNextDisplayType() == 2) {
				if(engine.ruleopt.nextDisplay >= 1) {
					int x2 = x + 8 + (fldWidth * fldBlkSize) + meterWidth;
					NormalFontSlick.printFont(x2 + 16, y + 40, NullpoMinoSlick.getUIText("InGame_Next"), COLOR_ORANGE, 0.5f);

					for(int i = 0; i < engine.ruleopt.nextDisplay; i++) {
						Piece piece = engine.getNextObject(engine.nextPieceCount + i);

						if(piece != null) {
							int centerX = ( (64 - ((piece.getWidth() + 1) * 16)) / 2 ) - (piece.getMinimumBlockX() * 16);
							int centerY = ( (64 - ((piece.getHeight() + 1) * 16)) / 2 ) - (piece.getMinimumBlockY() * 16);
							drawPiece(x2 + centerX, y + 48 + (i * 64) + centerY, piece, 1.0f);
						}
					}
				}
			} else if(getNextDisplayType() == 1) {
				if(engine.ruleopt.nextDisplay >= 1) {
					int x2 = x + 8 + (fldWidth * fldBlkSize) + meterWidth;
					NormalFontSlick.printFont(x2, y + 40, NullpoMinoSlick.getUIText("InGame_Next"), COLOR_ORANGE, 0.5f);

					for(int i = 0; i < engine.ruleopt.nextDisplay; i++) {
						Piece piece = engine.getNextObject(engine.nextPieceCount + i);

						if(piece != null) {
							int centerX = ( (32 - ((piece.getWidth() + 1) * 8)) / 2 ) - (piece.getMinimumBlockX() * 8);
							int centerY = ( (32 - ((piece.getHeight() + 1) * 8)) / 2 ) - (piece.getMinimumBlockY() * 8);
							drawPiece(x2 + centerX, y + 48 + (i * 32) + centerY, piece, 0.5f);
						}
					}
				}
			} else {
				// NEXT1
				if(engine.ruleopt.nextDisplay >= 1) {
					NormalFontSlick.printFont(x + 60, y, NullpoMinoSlick.getUIText("InGame_Next"), COLOR_ORANGE, 0.5f);

					Piece piece = engine.getNextObject(engine.nextPieceCount);
					if(piece != null) {
						//int x2 = x + 4 + ((-1 + (engine.field.getWidth() - piece.getWidth() + 1) / 2) * 16);
						int x2 = x + 4 + engine.getSpawnPosX(engine.field, piece) * fldBlkSize; //Rules with spawn x modified were misaligned.
						int y2 = y + 48 - ((piece.getMaximumBlockY() + 1) * 16);
						drawPiece(x2, y2, piece);
					}
				}

				// NEXT2·3
				for(int i = 0; i < engine.ruleopt.nextDisplay - 1; i++) {
					if(i >= 2) break;

					Piece piece = engine.getNextObject(engine.nextPieceCount + i + 1);

					if(piece != null) {
						drawPiece(x + 124 + (i * 40), y + 48 - ((piece.getMaximumBlockY() + 1) * 8), piece, 0.5f);
					}
				}

				// NEXT4~
				for(int i = 0; i < engine.ruleopt.nextDisplay - 3; i++) {
					Piece piece = engine.getNextObject(engine.nextPieceCount + i + 3);

					if(piece != null) {
						if(showmeter)
							drawPiece(x + 176, y + (i * 40) + 88 - ((piece.getMaximumBlockY() + 1) * 8), piece, 0.5f);
						else
							drawPiece(x + 168, y + (i * 40) + 88 - ((piece.getMaximumBlockY() + 1) * 8), piece, 0.5f);
					}
				}
			}
		}

		if(engine.isHoldVisible) {
			// HOLD
			int holdRemain = engine.ruleopt.holdLimit - engine.holdUsedCount;
			int x2 = sidenext ? (x - 32) : x;
			int y2 = sidenext ? (y + 40) : y;
			if(getNextDisplayType() == 2) x2 = x - 48;

			if( (engine.ruleopt.holdEnable == true) && ((engine.ruleopt.holdLimit < 0) || (holdRemain > 0)) ) {
				int tempColor = COLOR_GREEN;
				if(engine.holdDisable == true) tempColor = COLOR_WHITE;

				if(engine.ruleopt.holdLimit < 0) {
					NormalFontSlick.printFont(x2, y2, NullpoMinoSlick.getUIText("InGame_Hold"), tempColor, 0.5f);
				} else {
					if(!engine.holdDisable) {
						if((holdRemain > 0) && (holdRemain <= 10)) tempColor = COLOR_YELLOW;
						if((holdRemain > 0) && (holdRemain <= 5)) tempColor = COLOR_RED;
					}

					NormalFontSlick.printFont(x2, y2, NullpoMinoSlick.getUIText("InGame_Hold") + "\ne " + holdRemain, tempColor, 0.5f);
				}

				if(engine.holdPieceObject != null) {
					float dark = 0f;
					if(engine.holdDisable == true) dark = 0.3f;
					Piece piece = new Piece(engine.holdPieceObject);
					piece.resetOffsetArray();

					if(getNextDisplayType() == 2) {
						int centerX = ( (64 - ((piece.getWidth() + 1) * 16)) / 2 ) - (piece.getMinimumBlockX() * 16);
						int centerY = ( (64 - ((piece.getHeight() + 1) * 16)) / 2 ) - (piece.getMinimumBlockY() * 16);
						drawPiece((x - 64) + centerX, y + 48 + centerY, piece, 1.0f, dark);
					} else if(getNextDisplayType() == 1) {
						int centerX = ( (32 - ((piece.getWidth() + 1) * 8)) / 2 ) - (piece.getMinimumBlockX() * 8);
						int centerY = ( (32 - ((piece.getHeight() + 1) * 8)) / 2 ) - (piece.getMinimumBlockY() * 8);
						drawPiece(x2 + centerX, y + 48 + centerY, piece, 0.5f, dark);
					} else {
						drawPiece(x2, y + 48 - ((piece.getMaximumBlockY() + 1) * 8), piece, 0.5f, dark);
					}
				}
			}
		}
	}

	/**
	 * Draw shadow nexts
	 * @param x X coord
	 * @param y Y coord
	 * @param engine GameEngine
	 * @param scale Display size of piece
	 * @author Wojtek
	 */
	protected void drawShadowNexts(int x, int y, GameEngine engine, float scale) {
		Piece piece = engine.nowPieceObject;
		int blksize = (int) (16 * scale);

		if (piece != null) {
			int shadowX = engine.nowPieceX;
			int shadowY = engine.nowPieceBottomY + piece.getMinimumBlockY();

			for (int i = 0; i < engine.ruleopt.nextDisplay - 1; i++) {
				if (i >= 3)
					break;

				Piece next = engine.getNextObject(engine.nextPieceCount + i);

				if (next != null) {
					int size = ((piece.big || engine.displaysize == 1) ? 2 : 1);
					int shadowCenter = blksize * piece.getMinimumBlockX() + blksize
							* (piece.getWidth() + size) / 2;
					int nextCenter = blksize / 2 * next.getMinimumBlockX() + blksize / 2
							* (next.getWidth() + 1) / 2;
					int vPos = blksize * shadowY - (i + 1) * 24 - 8;

					if (vPos >= -blksize / 2)
						drawPiece(x + blksize * shadowX + shadowCenter - nextCenter, y
								+ vPos, next, 0.5f * scale, 0.1f);
				}
			}
		}
	}

	/**
	 * Each frame Drawing process of the first
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	@Override
	public void renderFirst(GameEngine engine, int playerID) {
		if(graphics == null) return;

		if(engine.playerID == 0) {
			// Background
			if(engine.owner.menuOnly) {
				graphics.setColor(Color.white);
				graphics.drawImage(ResourceHolderSlick.imgMenu, 0, 0);
			} else {
				int bg = engine.owner.backgroundStatus.bg;
				if(engine.owner.backgroundStatus.fadesw && !heavyeffect) {
					bg = engine.owner.backgroundStatus.fadebg;
				}

				if((ResourceHolderSlick.imgPlayBG != null) && (bg >= 0) && (bg < ResourceHolderSlick.imgPlayBG.length) && (showbg == true)) {
					graphics.setColor(Color.white);
					graphics.drawImage(ResourceHolderSlick.imgPlayBG[bg], 0, 0);

					if(engine.owner.backgroundStatus.fadesw && heavyeffect) {
						Color filter = new Color(Color.black);
						if(engine.owner.backgroundStatus.fadestat == false) {
							filter.a = (float) engine.owner.backgroundStatus.fadecount / 100;
						} else {
							filter.a = (float) (100 - engine.owner.backgroundStatus.fadecount) / 100;
						}
						graphics.setColor(filter);
						graphics.fillRect(0, 0, 640, 480);
					}
				} else {
					//graphics.setColor(Color.black);
					//graphics.fillRect(0, 0, 640, 480);
				}
			}
		}

		// NEXTなど
		if(!engine.owner.menuOnly && engine.isVisible) {
			int offsetX = getFieldDisplayPositionX(engine, playerID);
			int offsetY = getFieldDisplayPositionY(engine, playerID);

			if((engine.displaysize != -1)) {
				drawNext(offsetX, offsetY, engine);
				drawFrame(offsetX, offsetY + 48, engine, engine.displaysize);
				drawField(offsetX + 4, offsetY + 52, engine, engine.displaysize);
			} else {
				drawFrame(offsetX, offsetY, engine, -1);
				drawField(offsetX + 4, offsetY + 4, engine, -1);
			}
		}
	}

	/*
	 * ReadyProcess of drawing the screen
	 */
	@Override
	public void renderReady(GameEngine engine, int playerID) {
		if(graphics == null) return;
		if(engine.allowTextRenderByReceiver == false) return;
		//if(engine.isVisible == false) return;

		if(engine.statc[0] > 0) {
			int offsetX = getFieldDisplayPositionX(engine, playerID);
			int offsetY = getFieldDisplayPositionY(engine, playerID);

			if(engine.statc[0] > 0) {
				if(engine.displaysize != -1) {
					if((engine.statc[0] >= engine.readyStart) && (engine.statc[0] < engine.readyEnd))
						NormalFontSlick.printFont(offsetX + 44, offsetY + 204, "READY", COLOR_WHITE, 1.0f);
					else if((engine.statc[0] >= engine.goStart) && (engine.statc[0] < engine.goEnd))
						NormalFontSlick.printFont(offsetX + 62, offsetY + 204, "GO!", COLOR_WHITE, 1.0f);
				} else {
					if((engine.statc[0] >= engine.readyStart) && (engine.statc[0] < engine.readyEnd))
						NormalFontSlick.printFont(offsetX + 24, offsetY + 80, "READY", COLOR_WHITE, 0.5f);
					else if((engine.statc[0] >= engine.goStart) && (engine.statc[0] < engine.goEnd))
						NormalFontSlick.printFont(offsetX + 32, offsetY + 80, "GO!", COLOR_WHITE, 0.5f);
				}
			}
		}
	}

	/*
	 * BlockHandling when moving piece
	 */
	@Override
	public void renderMove(GameEngine engine, int playerID) {
		if(engine.isVisible == false) return;

		int offsetX = getFieldDisplayPositionX(engine, playerID);
		int offsetY = getFieldDisplayPositionY(engine, playerID);

		if((engine.statc[0] > 1) || (engine.ruleopt.moveFirstFrame)) {
			if(engine.displaysize == 1) {
				if(nextshadow) drawShadowNexts(offsetX + 4, offsetY + 52, engine, 2.0f);
				if(engine.ghost && engine.ruleopt.ghost) drawGhostPiece(offsetX + 4, offsetY + 52, engine, 2.0f);
				if((engine.ai!=null) && (engine.aiShowHint)&& engine.aiHintReady) drawHintPiece(offsetX + 4, offsetY + 52, engine, 2.0f);
				drawCurrentPiece(offsetX + 4, offsetY + 52, engine, 2.0f);
			} else if(engine.displaysize == 0) {
				if(nextshadow) drawShadowNexts(offsetX + 4, offsetY + 52, engine, 1.0f);
				if(engine.ghost && engine.ruleopt.ghost) drawGhostPiece(offsetX + 4, offsetY + 52, engine, 1.0f);
				if((engine.ai!=null) && (engine.aiShowHint ) && engine.aiHintReady) drawHintPiece(offsetX + 4, offsetY + 52, engine, 1.0f);
				drawCurrentPiece(offsetX + 4, offsetY + 52, engine, 1.0f);
			} else {
				if(engine.ghost && engine.ruleopt.ghost) drawGhostPiece(offsetX + 4, offsetY + 4, engine, 0.5f);
				if((engine.ai!=null) && (engine.aiShowHint) &&engine.aiHintReady) drawHintPiece(offsetX + 4, offsetY + 4, engine, 0.5f);
				drawCurrentPiece(offsetX + 4, offsetY + 4, engine, 0.5f);
			}
		}
	}

	/*
	 * BlockWhen you issue the production process to turn off the
	 */
	@Override
	public void blockBreak(GameEngine engine, int playerID, int x, int y, Block blk) {
		if(showlineeffect && (blk != null) && engine.displaysize != -1) {
			int color = blk.getDrawColor();
			// UsuallyBlock
			if((color >= Block.BLOCK_COLOR_GRAY) && (color <= Block.BLOCK_COLOR_PURPLE) && !blk.getAttribute(Block.BLOCK_ATTRIBUTE_BONE)) {
				EffectObject obj = new EffectObject(1,
													getFieldDisplayPositionX(engine, playerID) + 4 + (x * 16),
													getFieldDisplayPositionY(engine, playerID) + 52 + (y * 16),
													color);
				effectlist.add(obj);
			}
			// JewelBlock
			else if(blk.isGemBlock()) {
				EffectObject obj = new EffectObject(2,
													getFieldDisplayPositionX(engine, playerID) + 4 + (x * 16),
													getFieldDisplayPositionY(engine, playerID) + 52 + (y * 16),
													color);
				effectlist.add(obj);
			}
		}
	}

	/*
	 * EXCELLENTProcess of drawing the screen
	 */
	@Override
	public void renderExcellent(GameEngine engine, int playerID) {
		if(graphics == null) return;
		if(engine.allowTextRenderByReceiver == false) return;
		if(engine.isVisible == false) return;

		int offsetX = getFieldDisplayPositionX(engine, playerID);
		int offsetY = getFieldDisplayPositionY(engine, playerID);

		if(engine.displaysize != -1) {
			if(engine.statc[1] == 0)
				NormalFontSlick.printFont(offsetX + 4, offsetY + 204, "EXCELLENT!", COLOR_ORANGE, 1.0f);
			else if(engine.owner.getPlayers() < 3)
				NormalFontSlick.printFont(offsetX + 52, offsetY + 204, "WIN!", COLOR_ORANGE, 1.0f);
			else
				NormalFontSlick.printFont(offsetX + 4, offsetY + 204, "1ST PLACE!", COLOR_ORANGE, 1.0f);
		} else {
			if(engine.statc[1] == 0)
				NormalFontSlick.printFont(offsetX + 4, offsetY + 80, "EXCELLENT!", COLOR_ORANGE, 0.5f);
			else if(engine.owner.getPlayers() < 3)
				NormalFontSlick.printFont(offsetX + 33, offsetY + 80, "WIN!", COLOR_ORANGE, 0.5f);
			else
				NormalFontSlick.printFont(offsetX + 4, offsetY + 80, "1ST PLACE!", COLOR_ORANGE, 0.5f);
		}
	}

	/*
	 * game overProcess of drawing the screen
	 */
	@Override
	public void renderGameOver(GameEngine engine, int playerID) {
		if(graphics == null) return;
		if(engine.allowTextRenderByReceiver == false) return;
		if(engine.isVisible == false) return;

		if((engine.statc[0] >= engine.field.getHeight() + 1) && (engine.statc[0] < engine.field.getHeight() + 1 + 180)) {
			int offsetX = getFieldDisplayPositionX(engine, playerID);
			int offsetY = getFieldDisplayPositionY(engine, playerID);

			if(engine.displaysize != -1) {
				if(engine.owner.getPlayers() < 2)
					NormalFontSlick.printFont(offsetX + 12, offsetY + 204, "GAME OVER", COLOR_WHITE, 1.0f);
				else if(engine.owner.getWinner() == -2)
					NormalFontSlick.printFont(offsetX + 52, offsetY + 204, "DRAW", COLOR_GREEN, 1.0f);
				else if(engine.owner.getPlayers() < 3)
					NormalFontSlick.printFont(offsetX + 52, offsetY + 204, "LOSE", COLOR_WHITE, 1.0f);
			} else {
				if(engine.owner.getPlayers() < 2)
					NormalFontSlick.printFont(offsetX + 4, offsetY + 80, "GAME OVER", COLOR_WHITE, 0.5f);
				else if(engine.owner.getWinner() == -2)
					NormalFontSlick.printFont(offsetX + 28, offsetY + 80, "DRAW", COLOR_GREEN, 0.5f);
				else if(engine.owner.getPlayers() < 3)
					NormalFontSlick.printFont(offsetX + 28, offsetY + 80, "LOSE", COLOR_WHITE, 0.5f);
			}
		}
	}

	/*
	 * Render results screenProcessing
	 */
	@Override
	public void renderResult(GameEngine engine, int playerID) {
		if(graphics == null) return;
		if(engine.allowTextRenderByReceiver == false) return;
		if(engine.isVisible == false) return;

		int tempColor;

		if(engine.statc[0] == 0)
			tempColor = COLOR_RED;
		else
			tempColor = COLOR_WHITE;
		NormalFontSlick.printFont(getFieldDisplayPositionX(engine, playerID) + 12, getFieldDisplayPositionY(engine, playerID) + 340, "RETRY", tempColor, 1.0f);

		if(engine.statc[0] == 1)
			tempColor = COLOR_RED;
		else
			tempColor = COLOR_WHITE;
		NormalFontSlick.printFont(getFieldDisplayPositionX(engine, playerID) + 108, getFieldDisplayPositionY(engine, playerID) + 340, "END", tempColor, 1.0f);
	}

	/*
	 * fieldDrawing process of edit screen
	 */
	@Override
	public void renderFieldEdit(GameEngine engine, int playerID) {
		if(graphics == null) return;
		int x = getFieldDisplayPositionX(engine, playerID) + 4 + (engine.fldeditX * 16);
		int y = getFieldDisplayPositionY(engine, playerID) + 52 + (engine.fldeditY * 16);
		float bright = (engine.fldeditFrames % 60 >= 30) ? -0.5f : -0.2f;
		drawBlock(x, y, engine.fldeditColor, engine.getSkin(), false, bright, 1.0f, 1.0f);
	}

	/*
	 * Each frame Processing that takes place at the end of the
	 */
	@Override
	public void onLast(GameEngine engine, int playerID) {
		if(playerID == engine.owner.getPlayers() - 1) effectUpdate();
	}

	/*
	 * Each frame Drawing process that takes place at the end of the
	 */
	@Override
	public void renderLast(GameEngine engine, int playerID) {
		if(playerID == engine.owner.getPlayers() - 1) effectRender();
	}

	/**
	 * Update effects
	 */
	protected void effectUpdate() {
		boolean emptyflag = true;

		for(int i = 0; i < effectlist.size(); i++) {
			EffectObject obj = effectlist.get(i);

			if(obj.effect != 0) emptyflag = false;

			// Normal Block
			if(obj.effect == 1) {
				obj.anim += (lineeffectspeed + 1);
				if(obj.anim >= 36) obj.effect = 0;
			}
			// Gem Block
			if(obj.effect == 2) {
				obj.anim += (lineeffectspeed + 1);
				if(obj.anim >= 60) obj.effect = 0;
			}
		}

		if(emptyflag) effectlist.clear();
	}

	/**
	 * Render effects
	 */
	protected void effectRender() {
		for(int i = 0; i < effectlist.size(); i++) {
			EffectObject obj = effectlist.get(i);

			// Normal Block
			if(obj.effect == 1) {
				int x = obj.x - 40;
				int y = obj.y - 15;
				int color = obj.param - Block.BLOCK_COLOR_GRAY;

				if(obj.anim < 30) {
					int srcx = ((obj.anim-1) % 6) * 96;
					int srcy = ((obj.anim-1) / 6) * 96;
					try {
						graphics.drawImage(ResourceHolderSlick.imgBreak[color][0], x, y, x + 96, y + 96, srcx, srcy, srcx + 96, srcy + 96);
					} catch (Exception e) {}
				} else {
					int srcx = ((obj.anim-30) % 6) * 96;
					int srcy = ((obj.anim-30) / 6) * 96;
					try {
						graphics.drawImage(ResourceHolderSlick.imgBreak[color][1], x, y, x + 96, y + 96, srcx, srcy, srcx + 96, srcy + 96);
					} catch (Exception e) {}
				}
			}
			// Gem Block
			if(obj.effect == 2) {
				int x = obj.x - 8;
				int y = obj.y - 8;
				int srcx = ((obj.anim-1) % 10) * 32;
				int srcy = ((obj.anim-1) / 10) * 32;
				int color = obj.param - Block.BLOCK_COLOR_GEM_RED;

				try {
					graphics.drawImage(ResourceHolderSlick.imgPErase[color], x, y, x + 32, y + 32, srcx, srcy, srcx + 32, srcy + 32);
				} catch (Exception e) {}
			}
		}
	}
}
