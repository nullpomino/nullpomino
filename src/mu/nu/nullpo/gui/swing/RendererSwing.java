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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Image;

import mu.nu.nullpo.game.component.Block;
import mu.nu.nullpo.game.component.Field;
import mu.nu.nullpo.game.component.Piece;
import mu.nu.nullpo.game.event.EventReceiver;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.util.CustomProperties;

//import org.apache.log4j.Logger;

/**
 * ゲームの event 処理と描画処理 (Swing版）
 */
public class RendererSwing extends EventReceiver {
	/** Log */
	//static Logger log = Logger.getLogger(RendererSwing.class);

	/** 描画先サーフェイス */
	protected Graphics2D graphics;

	/** fieldのBlockを表示 (falseなら枠線だけ表示) */
	protected boolean showfieldblockgraphics;

	/** 操作Blockの絵柄をシンプルにする */
	protected boolean simpleblock;

	/** ghost ピースの上にNEXT表示 */
	protected boolean nextshadow;

	/**
	 * 指定した font 色をAWT用Colorとして取得
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
	 * Block colorIDに応じてAWT用Colorオブジェクトを作成・取得
	 * @param colorID Block colorID
	 * @return AWT用Colorオブジェクト
	 */
	public static Color getColorByID(int colorID) {
		switch(colorID) {
		case Block.BLOCK_COLOR_GRAY:   return new Color( 64, 64, 64);
		case Block.BLOCK_COLOR_RED:    return new Color(128,  0,  0);
		case Block.BLOCK_COLOR_ORANGE: return new Color(128, 64,  0);
		case Block.BLOCK_COLOR_YELLOW: return new Color(128,128,  0);
		case Block.BLOCK_COLOR_GREEN:  return new Color(  0,128,  0);
		case Block.BLOCK_COLOR_CYAN:   return new Color(  0,128,128);
		case Block.BLOCK_COLOR_BLUE:   return new Color(  0,  0,128);
		case Block.BLOCK_COLOR_PURPLE: return new Color(128,  0,128);
		}
		return new Color(0,0,0);
	}

	public static Color getColorByIDBright(int colorID) {
		switch(colorID) {
		case Block.BLOCK_COLOR_GRAY:   return new Color(128,128,128);
		case Block.BLOCK_COLOR_RED:    return new Color(255,  0,  0);
		case Block.BLOCK_COLOR_ORANGE: return new Color(255,128,  0);
		case Block.BLOCK_COLOR_YELLOW: return new Color(255,255,  0);
		case Block.BLOCK_COLOR_GREEN:  return new Color(  0,255,  0);
		case Block.BLOCK_COLOR_CYAN:   return new Color(  0,255,255);
		case Block.BLOCK_COLOR_BLUE:   return new Color(  0,  0,255);
		case Block.BLOCK_COLOR_PURPLE: return new Color(255,  0,255);
		}
		return new Color(0,0,0);
	}

	/**
	 * Constructor
	 */
	public RendererSwing() {
		graphics = null;

		showbg = NullpoMinoSwing.propConfig.getProperty("option.showbg", true);
		showmeter = NullpoMinoSwing.propConfig.getProperty("option.showmeter", true);
		showfieldblockgraphics = NullpoMinoSwing.propConfig.getProperty("option.showfieldblockgraphics", true);
		simpleblock = NullpoMinoSwing.propConfig.getProperty("option.simpleblock", false);
		nextshadow = NullpoMinoSwing.propConfig.getProperty("option.nextshadow", false);
		outlineghost = NullpoMinoSwing.propConfig.getProperty("option.outlineghost", false);
		sidenext = NullpoMinoSwing.propConfig.getProperty("option.sidenext", false);
		bigsidenext = NullpoMinoSwing.propConfig.getProperty("option.bigsidenext", false);
	}

	/*
	 * 描画先のGraphicsを設定
	 */
	@Override
	public void setGraphics(Object g) {
		if(g instanceof Graphics2D) {
			graphics = (Graphics2D)g;
		}
	}

	/*
	 * Sound effects再生
	 */
	@Override
	public void playSE(String name) {
		ResourceHolderSwing.soundManager.play(name);
	}

	/*
	 * Menu 用の文字列を描画
	 */
	@Override
	public void drawMenuFont(GameEngine engine, int playerID, int x, int y, String str, int color, float scale) {
		int x2 = (scale == 0.5f) ? x * 8 : x * 16;
		int y2 = (scale == 0.5f) ? y * 8 : y * 16;
		if(!engine.owner.menuOnly) {
			x2 += getFieldDisplayPositionX(engine, playerID) + 4;
			y2 += getFieldDisplayPositionY(engine, playerID) + 52;
		}
		NormalFontSwing.printFont(x2, y2, str, color, scale);
	}

	/*
	 * Menu 用の文字列をTTF font で描画
	 */
	@Override
	public void drawTTFMenuFont(GameEngine engine, int playerID, int x, int y, String str, int color) {
		int x2 = x * 16;
		int y2 = y * 16 + 12;
		if(!engine.owner.menuOnly) {
			x2 += getFieldDisplayPositionX(engine, playerID) + 4;
			y2 += getFieldDisplayPositionY(engine, playerID) + 52;
		}
		graphics.setColor(getFontColorAsColor(color));
		graphics.drawString(str, x2, y2);
		graphics.setColor(Color.white);
	}

	/*
	 * Render score用の font を描画
	 */
	@Override
	public void drawScoreFont(GameEngine engine, int playerID, int x, int y, String str, int color, float scale) {
		if(engine.owner.menuOnly) return;
		int size = (scale == 0.5f) ? 8 : 16;
		NormalFontSwing.printFont(getScoreDisplayPositionX(engine, playerID) + (x * size),
								  getScoreDisplayPositionY(engine, playerID) + (y * size), str, color, scale);
	}

	/*
	 * Render score用の font をTTF font で描画
	 */
	@Override
	public void drawTTFScoreFont(GameEngine engine, int playerID, int x, int y, String str, int color) {
		if(engine.owner.menuOnly) return;

		graphics.setColor(getFontColorAsColor(color));
		graphics.drawString(str,
						    getScoreDisplayPositionX(engine, playerID) + (x * 16),
						    getScoreDisplayPositionY(engine, playerID) + (y * 16));
		graphics.setColor(Color.white);
	}

	/*
	 * 直接指定した座標へ文字列を描画
	 */
	@Override
	public void drawDirectFont(GameEngine engine, int playerID, int x, int y, String str, int color, float scale) {
		NormalFontSwing.printFont(x, y, str, color, scale);
	}

	/*
	 * 直接指定した座標へ描画できるTTF font を描画
	 */
	@Override
	public void drawTTFDirectFont(GameEngine engine, int playerID, int x, int y, String str, int color) {
		graphics.setColor(getFontColorAsColor(color));
		graphics.drawString(str, x, y);
		graphics.setColor(Color.white);
	}

	/*
	 * スピードMeterを描画
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
			graphics.fillRect(dx1 + 1, dy1 + 1, tempSpeedMeter + 1, 3);
		}

		graphics.setColor(Color.white);
	}

	/*
	 * TTF使用可能
	 */
	@Override
	public boolean isTTFSupport() {
		return true;
	}

	/*
	 * リプレイを保存
	 */
	@Override
	public void saveReplay(GameManager owner, CustomProperties prop) {
		if(owner.mode.isNetplayMode()) return;

		saveReplay(owner, prop, NullpoMinoSwing.propGlobal.getProperty("custom.replay.directory", "replay"));
	}

	/*
	 * 1マスBlockを描画
	 */
	@Override
	public void drawSingleBlock(GameEngine engine, int playerID, int x, int y, int color, int skin, boolean bone, float darkness, float alpha, float scale) {
		drawBlock(x, y, color, skin, bone, darkness, alpha, scale);
	}

	/**
	 * Blockを描画
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param color 色
	 * @param skin 模様
	 * @param bone 骨Block
	 * @param darkness 暗さもしくは明るさ
	 * @param alpha 透明度
	 * @param scale 拡大率
	 */
	protected void drawBlock(int x, int y, int color, int skin, boolean bone, float darkness, float alpha, float scale) {
		if(graphics == null) return;

		if((color <= Block.BLOCK_COLOR_INVALID)) return;
		boolean isSpecialBlocks = (color >= Block.BLOCK_COLOR_COUNT);

		int size = 16;
		Image img = ResourceHolderSwing.imgBlock;
		if(scale == 0.5f) {
			size = 8;
			img = ResourceHolderSwing.imgBlockSmall;
		}
		if(scale == 2.0f) {
			size = 32;
			img = ResourceHolderSwing.imgBlockBig;
		}

		int sx = color * size;
		if(bone) sx += 9 * size;
		int sy = skin * size;

		if(isSpecialBlocks) sx = ((color - Block.BLOCK_COLOR_COUNT) + 18) * size;

		int imageWidth = img.getWidth(null);
		if((sx >= imageWidth) && (imageWidth != -1)) sx = 0;
		int imageHeight = img.getHeight(null);
		if((sy >= imageHeight) && (imageHeight != -1)) sy = 0;

		Composite backupComposite = graphics.getComposite();

		if((alpha >= 0f) && (alpha < 1f) && (!showbg)) {
			AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
			graphics.setComposite(composite);
		}

		if(simpleblock) {
			switch(color) {
			case Block.BLOCK_COLOR_GRAY:
				graphics.setColor(Color.lightGray);
				break;
			case Block.BLOCK_COLOR_RED:
				graphics.setColor(Color.red);
				break;
			case Block.BLOCK_COLOR_ORANGE:
				graphics.setColor(Color.orange);
				break;
			case Block.BLOCK_COLOR_YELLOW:
				graphics.setColor(Color.yellow);
				break;
			case Block.BLOCK_COLOR_GREEN:
				graphics.setColor(Color.green);
				break;
			case Block.BLOCK_COLOR_CYAN:
				graphics.setColor(Color.cyan);
				break;
			case Block.BLOCK_COLOR_BLUE:
				graphics.setColor(Color.blue);
				break;
			case Block.BLOCK_COLOR_PURPLE:
				graphics.setColor(Color.magenta);
				break;
			default:
				graphics.setColor(Color.white);
				break;
			}
			graphics.drawRect(x, y, size-1, size-1);

			if(showbg) {
				graphics.setColor(Color.black);
				graphics.fillRect(x + 1, y + 1, size - 2, size - 2);
			}
		} else {
			graphics.drawImage(img, x, y, x + size, y + size, sx, sy, sx + size, sy + size, null);
		}

		graphics.setComposite(backupComposite);

		if( (darkness != 0) || ((alpha >= 0f) && (alpha < 1f) && (showbg)) ) {
			Color backupColor = graphics.getColor();

			Color filterColor;
			if((alpha >= 0f) && (alpha < 1f) && (showbg)) {
				filterColor = new Color(0f, 0f, 0f, alpha);
			} else if(darkness > 0) {
				filterColor = new Color(0f, 0f, 0f, darkness);
			} else {
				filterColor = new Color(1f, 1f, 1f, -darkness);
			}

			graphics.setColor(filterColor);
			graphics.fillRect(x, y, size, size);
			graphics.setColor(backupColor);
		}
	}

	/**
	 * Blockクラスのインスタンスを使用してBlockを描画
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param blk Blockクラスのインスタンス
	 */
	protected void drawBlock(int x, int y, Block blk) {
		drawBlock(x, y, blk.getDrawColor(), blk.skin, blk.getAttribute(Block.BLOCK_ATTRIBUTE_BONE), blk.darkness, blk.alpha, 1.0f);
	}

	/**
	 * Blockクラスのインスタンスを使用してBlockを描画 (拡大率指定可能）
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param blk Blockクラスのインスタンス
	 * @param scale 拡大率
	 */
	protected void drawBlock(int x, int y, Block blk, float scale) {
		drawBlock(x, y, blk.getDrawColor(), blk.skin, blk.getAttribute(Block.BLOCK_ATTRIBUTE_BONE), blk.darkness, blk.alpha, scale);
	}

	/**
	 * Blockクラスのインスタンスを使用してBlockを描画 (拡大率と暗さ指定可能）
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param blk Blockクラスのインスタンス
	 * @param scale 拡大率
	 * @param darkness 暗さもしくは明るさ
	 */
	protected void drawBlock(int x, int y, Block blk, float scale, float darkness) {
		drawBlock(x, y, blk.getDrawColor(), blk.skin, blk.getAttribute(Block.BLOCK_ATTRIBUTE_BONE), darkness, blk.alpha, scale);
	}

	protected void drawBlockForceVisible(int x, int y, Block blk, float scale) {
		drawBlock(x, y, blk.getDrawColor(), blk.skin, blk.getAttribute(Block.BLOCK_ATTRIBUTE_BONE), blk.darkness,
				(0.5f*blk.alpha)+0.5f, scale);
	}

	/**
	 * Blockピースを描画
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param piece 描画するピース
	 */
	protected void drawPiece(int x, int y, Piece piece) {
		drawPiece(x, y, piece, 1.0f);
	}

	/**
	 * Blockピースを描画 (拡大率指定可能）
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param piece 描画するピース
	 * @param scale 拡大率
	 */
	protected void drawPiece(int x, int y, Piece piece, float scale) {
		drawPiece(x, y, piece, scale, 0f);
	}

	/**
	 * Blockピースを描画 (暗さもしくは明るさの指定可能）
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param piece 描画するピース
	 * @param scale 拡大率
	 * @param darkness 暗さもしくは明るさ
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
	 * 現在操作中のBlockピースを描画 (Y-coordinateが0以上のBlockだけ表示）
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param engine GameEngineのインスタンス
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
	 * 現在操作中のBlockピースのghost を描画
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param engine GameEngineのインスタンス
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
		if (engine.nowPieceObject!=null){
			Piece piece = new Piece(engine.nowPieceObject);
			piece.direction=engine.aiHintRt;
			piece.updateConnectData();
			int blksize = (int)(16 * scale);

			if(piece != null) {
				for(int i = 0; i < piece.getMaxBlock(); i++) {
					if(!piece.big) {
						int x2 = engine.aiHintX + piece.dataX[piece.direction][i];
						int y2 = engine.aiHintY + piece.dataY[piece.direction][i];

						if(y2 >= 0) {

							Block blkTemp = piece.block[i];
							int x3 = x + (x2 * blksize);
							int y3 = y + (y2 * blksize);
							int ls = (blksize-1);

							int colorID = blkTemp.getDrawColor();
							if(blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_BONE)) colorID = -1;
							Color color = getColorByIDBright(colorID);
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
						int x2 = engine.aiHintX + (piece.dataX[piece.direction][i] * 2);
						int y2 = engine.aiHintY + (piece.dataY[piece.direction][i] * 2);

						Block blkTemp = piece.block[i];
						int x3 = x + (x2 * blksize);
						int y3 = y + (y2 * blksize);
						int ls = (blksize * 2 -1);

						int colorID = blkTemp.getDrawColor();
						if(blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_BONE)) colorID = -1;
						Color color = getColorByID(colorID);
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
	 * fieldのBlockを描画
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param engine GameEngineのインスタンス
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

		for(int i = 0; i < viewHeight; i++) {
			for(int j = 0; j < width; j++) {
				int x2 = x + (j * blksize);
				int y2 = y + (i * blksize);

				Block blk = null;
				if(field != null) blk = field.getBlock(j, i);

				if((field != null) && (blk != null) && (blk.color > Block.BLOCK_COLOR_NONE)) {
					if(blk.getAttribute(Block.BLOCK_ATTRIBUTE_WALL)) {
						drawBlock(x2, y2, Block.BLOCK_COLOR_NONE, blk.skin, blk.getAttribute(Block.BLOCK_ATTRIBUTE_BONE),
								  blk.darkness, blk.alpha, scale);
					} else if (showfieldblockgraphics) {
						if (engine.owner.replayMode && engine.owner.replayShowInvisible) {
							drawBlockForceVisible(x2, y2, blk, scale);
						} else if(blk.getAttribute(Block.BLOCK_ATTRIBUTE_VISIBLE)) {
							drawBlock(x2, y2, blk, scale);
						}
					} else {
						int sx = (((i % 2 == 0) && (j % 2 == 0)) || ((i % 2 != 0) && (j % 2 != 0))) ? 0 : 16;
						graphics.drawImage(ResourceHolderSwing.imgFieldbg, x2, y2, x2+blksize, y2+blksize, sx, 0, sx+16, 16, null);
					}

					if(blk.getAttribute(Block.BLOCK_ATTRIBUTE_OUTLINE) && !blk.getAttribute(Block.BLOCK_ATTRIBUTE_BONE)) {
						graphics.setColor(Color.white);
						int ls = (blksize-1);
						if(engine.blockOutlineType == GameEngine.BLOCK_OUTLINE_NORMAL) {
							if(field.getBlockColor(j, i - 1) == Block.BLOCK_COLOR_NONE) graphics.drawLine(x2, y2, x2 + ls, y2);
							if(field.getBlockColor(j, i + 1) == Block.BLOCK_COLOR_NONE) graphics.drawLine(x2, y2 + ls, x2 + ls, y2 + ls);
							if(field.getBlockColor(j - 1, i) == Block.BLOCK_COLOR_NONE) graphics.drawLine(x2, y2, x2, y2 + ls);
							if(field.getBlockColor(j + 1, i) == Block.BLOCK_COLOR_NONE) graphics.drawLine(x2 + ls, y2, x2 + ls, y2 + ls);
						} else if(engine.blockOutlineType == GameEngine.BLOCK_OUTLINE_CONNECT) {
							if(!blk.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_UP))     graphics.drawLine(x2, y2, x2 + ls, y2);
							if(!blk.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_DOWN))   graphics.drawLine(x2, y2 + ls, x2 + ls, y2 + ls);
							if(!blk.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_LEFT))   graphics.drawLine(x2, y2, x2, y2 + ls);
							if(!blk.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT))  graphics.drawLine(x2 + ls, y2, x2 + ls, y2 + ls);
						} else if(engine.blockOutlineType == GameEngine.BLOCK_OUTLINE_SAMECOLOR) {
							if(field.getBlockColor(j, i - 1) != blk.color) graphics.drawLine(x2, y2, x2 + ls, y2);
							if(field.getBlockColor(j, i + 1) != blk.color) graphics.drawLine(x2, y2 + ls, x2 + ls, y2 + ls);
							if(field.getBlockColor(j - 1, i) != blk.color) graphics.drawLine(x2, y2, x2, y2 + ls);
							if(field.getBlockColor(j + 1, i) != blk.color) graphics.drawLine(x2 + ls, y2, x2 + ls, y2 + ls);
						}
					}
				} else if(showbg) {
					int sx = (((i % 2 == 0) && (j % 2 == 0)) || ((i % 2 != 0) && (j % 2 != 0))) ? 0 : 16;
					graphics.drawImage(ResourceHolderSwing.imgFieldbg, x2, y2, x2+blksize, y2+blksize, sx, 0, sx+16, 16, null);
				}
			}
		}

		// ヘボHIDDEN
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
	 * Field frameを描画
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param engine GameEngineのインスタンス
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

		// Upと下
		int maxWidth = (width * size * 4);
		if(showmeter) maxWidth = (width * size * 4) + (2 * 4);

		int tmpX = 0;
		int tmpY = 0;

		tmpX = x + 4;
		tmpY = y;
		graphics.drawImage(ResourceHolderSwing.imgFrame, tmpX, tmpY, tmpX + maxWidth, tmpY + 4, offsetX + 4, 0, (offsetX + 4) + 4, 4, null);
		tmpY = y + (height * size * 4) + 4;
		graphics.drawImage(ResourceHolderSwing.imgFrame, tmpX, tmpY, tmpX + maxWidth, tmpY + 4, offsetX + 4, 8, (offsetX + 4) + 4, 8 + 4, null);

		// 左と右
		tmpX = x;
		tmpY = y + 4;
		graphics.drawImage(ResourceHolderSwing.imgFrame, tmpX, tmpY, tmpX + 4, tmpY + (height * size*4), offsetX, 4, offsetX + 4, 4 + 4, null);

		if(showmeter) {
			tmpX = x + (width * size * 4) + 12;
		} else {
			tmpX = x + (width * size * 4) + 4;
		}
		graphics.drawImage(ResourceHolderSwing.imgFrame, tmpX, tmpY, tmpX + 4, tmpY + (height * size*4), offsetX + 8, 4, offsetX + 8 + 4, 4 + 4, null);

		// 左上
		tmpX = x;
		tmpY = y;
		graphics.drawImage(ResourceHolderSwing.imgFrame, tmpX, tmpY, tmpX + 4, tmpY + 4, offsetX, 0, offsetX + 4, 4, null);

		// 左下
		tmpX = x;
		tmpY = y + (height * size * 4) + 4;
		graphics.drawImage(ResourceHolderSwing.imgFrame, tmpX, tmpY, tmpX + 4, tmpY + 4, offsetX, 8, offsetX + 4, 8 + 4, null);

		if(showmeter) {
			// MeterONのときの右上
			tmpX = x + (width * size * 4) + 12;
			tmpY = y;
			graphics.drawImage(ResourceHolderSwing.imgFrame, tmpX, tmpY, tmpX + 4, tmpY + 4, offsetX + 8, 0, (offsetX + 8) + 4, 4, null);

			// MeterONのときの右下
			tmpX = x + (width * size * 4) + 12;
			tmpY = y + (height * size * 4) + 4;
			graphics.drawImage(ResourceHolderSwing.imgFrame, tmpX, tmpY, tmpX + 4, tmpY + 4, offsetX + 8, 8, (offsetX + 8) + 4, 8 + 4, null);

			// 右Meterの枠
			tmpX = x + (width * size * 4) + 4;
			tmpY = y + 4;
			graphics.drawImage(ResourceHolderSwing.imgFrame, tmpX, tmpY, tmpX + 4, tmpY + (height * size * 4), offsetX + 12, 4, (offsetX + 12) + 4, 4 + 4,
								null);

			tmpX = x + (width * size * 4) + 4;
			tmpY = y;
			graphics.drawImage(ResourceHolderSwing.imgFrame, tmpX, tmpY, tmpX + 4, tmpY + 4, offsetX + 12, 0, (offsetX + 12) + 4, 4, null);

			tmpX = x + (width * size * 4) + 4;
			tmpY = y + (height * size * 4) + 4;
			graphics.drawImage(ResourceHolderSwing.imgFrame, tmpX, tmpY, tmpX + 4, tmpY + 4, offsetX + 12, 8, (offsetX + 12) + 4, 8 + 4, null);

			// 右Meter
			int maxHeight = height * size * 4;
			if((engine != null) && (engine.meterValue > 0)) maxHeight = (height * size * 4) - engine.meterValue;

			tmpX = x + (width * size * 4) + 8;
			tmpY = y + 4;

			if(maxHeight > 0) {
				graphics.setColor(Color.black);
				graphics.fillRect(tmpX, tmpY, 4, maxHeight);
				graphics.setColor(Color.white);
			}

			if((engine != null) && (engine.meterValue > 0)) {
				int value = engine.meterValue;
				if(value > height * size * 4) value = height * size * 4;

				if(value > 0) {
					tmpX = x + (width * size * 4) + 8;
					tmpY = y + (height * size * 4) + 3 - (value - 1);

					Color color = Color.white;
					switch(engine.meterColor) {
					case GameEngine.METER_COLOR_GREEN:
						color = Color.green;
						break;
					case GameEngine.METER_COLOR_YELLOW:
						color = Color.yellow;
						break;
					case GameEngine.METER_COLOR_ORANGE:
						color = Color.orange;
						break;
					case GameEngine.METER_COLOR_RED:
						color = Color.red;
						break;
					}
					graphics.setColor(color);
					graphics.fillRect(tmpX, tmpY, 4, value);
					graphics.setColor(Color.white);
				}
			}
		} else {
			// MeterOFFのときの右上
			tmpX = x + (width * size * 4) + 4;
			tmpY = y;
			graphics.drawImage(ResourceHolderSwing.imgFrame, tmpX, tmpY, tmpX + 4, tmpY + 4, offsetX + 8, 0, (offsetX + 8) + 4, 4, null);

			// MeterOFFのときの右下
			tmpX = x + (width * size * 4) + 4;
			tmpY = y + (height * size * 4) + 4;
			graphics.drawImage(ResourceHolderSwing.imgFrame, tmpX, tmpY, tmpX + 4, tmpY + 4, offsetX + 8, 8, (offsetX + 8) + 4, 8 + 4, null);
		}
	}

	/**
	 * NEXTを描画
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param engine GameEngineのインスタンス
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

		if(engine.isNextVisible) {
			if(getNextDisplayType() == 2) {
				if(engine.ruleopt.nextDisplay >= 1) {
					int x2 = x + 8 + (fldWidth * fldBlkSize) + meterWidth;
					NormalFontSwing.printFont(x2 + 16, y + 40, NullpoMinoSwing.getUIText("InGame_Next"), COLOR_ORANGE, 0.5f);

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
					NormalFontSwing.printFont(x2, y + 40, NullpoMinoSwing.getUIText("InGame_Next"), COLOR_ORANGE, 0.5f);

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
					Piece piece = engine.getNextObject(engine.nextPieceCount);
					NormalFontSwing.printFont(x + 60, y, NullpoMinoSwing.getUIText("InGame_Next"), COLOR_ORANGE, 0.5f);

					if(piece != null) {
						//int x2 = x + 4 + ((-1 + (engine.field.getWidth() - piece.getWidth() + 1) / 2) * 16);
						int x2 = x + 4 + engine.getSpawnPosX(engine.field, piece) * fldBlkSize; //Rules with spawn x modified were misaligned.
						int y2 = y + 48 - ((piece.getMaximumBlockY() + 1) * 16);
						drawPiece(x2, y2, piece);
					}
				}

				// NEXT2・3
				for(int i = 0; i < engine.ruleopt.nextDisplay - 1; i++) {
					if(i >= 2) break;

					Piece piece = engine.getNextObject(engine.nextPieceCount + i + 1);

					if(piece != null) {
						drawPiece(x + 124 + (i * 40), y + 48 - ((piece.getMaximumBlockY() + 1) * 8), piece, 0.5f);
					}
				}

				// NEXT4～
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
					NormalFontSwing.printFont(x2, y2, NullpoMinoSwing.getUIText("InGame_Hold"), tempColor, 0.5f);
				} else {
					if(!engine.holdDisable) {
						if((holdRemain > 0) && (holdRemain <= 10)) tempColor = COLOR_YELLOW;
						if((holdRemain > 0) && (holdRemain <= 5)) tempColor = COLOR_RED;
					}

					NormalFontSwing.printFont(x2, y2, NullpoMinoSwing.getUIText("InGame_Hold") + "\ne " + holdRemain, tempColor, 0.5f);
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
	 * 各 frame 最初の描画処理
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	@Override
	public void renderFirst(GameEngine engine, int playerID) {
		if(graphics == null) return;

		if(engine.playerID == 0) {
			// Background
			if(!showbg || engine.owner.menuOnly) {
				graphics.setColor(Color.black);
				graphics.fillRect(0, 0, 640, 480);
			} else {
				int bg = engine.owner.backgroundStatus.bg;
				if(engine.owner.backgroundStatus.fadesw) bg = engine.owner.backgroundStatus.fadebg;

				if((ResourceHolderSwing.imgPlayBG != null) && (bg >= 0) && (bg < ResourceHolderSwing.BACKGROUND_MAX)) {
					graphics.drawImage(ResourceHolderSwing.imgPlayBG[bg], 0, 0, null);
				}
			}
		}

		// NEXTなど
		if(!engine.owner.menuOnly && engine.isVisible) {
			int offsetX = getFieldDisplayPositionX(engine, playerID);
			int offsetY = getFieldDisplayPositionY(engine, playerID);

			if(engine.displaysize != -1) {
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
	 * Ready画面の描画処理
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
						NormalFontSwing.printFont(offsetX + 44, offsetY + 204, "READY", COLOR_WHITE, 1.0f);
					else if((engine.statc[0] >= engine.goStart) && (engine.statc[0] < engine.goEnd))
						NormalFontSwing.printFont(offsetX + 62, offsetY + 204, "GO!", COLOR_WHITE, 1.0f);
				} else {
					if((engine.statc[0] >= engine.readyStart) && (engine.statc[0] < engine.readyEnd))
						NormalFontSwing.printFont(offsetX + 24, offsetY + 80, "READY", COLOR_WHITE, 0.5f);
					else if((engine.statc[0] >= engine.goStart) && (engine.statc[0] < engine.goEnd))
						NormalFontSwing.printFont(offsetX + 32, offsetY + 80, "GO!", COLOR_WHITE, 0.5f);
				}
			}
		}
	}

	/*
	 * Blockピース移動時の処理
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
	 * EXCELLENT画面の描画処理
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
				NormalFontSwing.printFont(offsetX + 4, offsetY + 204, "EXCELLENT!", COLOR_ORANGE, 1.0f);
			else if(engine.owner.getPlayers() < 3)
				NormalFontSwing.printFont(offsetX + 52, offsetY + 204, "WIN!", COLOR_ORANGE, 1.0f);
			else
				NormalFontSwing.printFont(offsetX + 4, offsetY + 204, "1ST PLACE!", COLOR_ORANGE, 1.0f);
		} else {
			if(engine.statc[1] == 0)
				NormalFontSwing.printFont(offsetX + 4, offsetY + 80, "EXCELLENT!", COLOR_ORANGE, 0.5f);
			else if(engine.owner.getPlayers() < 3)
				NormalFontSwing.printFont(offsetX + 33, offsetY + 80, "WIN!", COLOR_ORANGE, 0.5f);
			else
				NormalFontSwing.printFont(offsetX + 4, offsetY + 80, "1ST PLACE!", COLOR_ORANGE, 0.5f);
		}
	}

	/*
	 * game over画面の描画処理
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
					NormalFontSwing.printFont(offsetX + 12, offsetY + 204, "GAME OVER", COLOR_WHITE, 1.0f);
				else if(engine.owner.getWinner() == -2)
					NormalFontSwing.printFont(offsetX + 52, offsetY + 204, "DRAW", COLOR_GREEN, 1.0f);
				else if(engine.owner.getPlayers() < 3)
					NormalFontSwing.printFont(offsetX + 52, offsetY + 204, "LOSE", COLOR_WHITE, 1.0f);
			} else {
				if(engine.owner.getPlayers() < 2)
					NormalFontSwing.printFont(offsetX + 4, offsetY + 80, "GAME OVER", COLOR_WHITE, 0.5f);
				else if(engine.owner.getWinner() == -2)
					NormalFontSwing.printFont(offsetX + 28, offsetY + 80, "DRAW", COLOR_GREEN, 0.5f);
				else if(engine.owner.getPlayers() < 3)
					NormalFontSwing.printFont(offsetX + 28, offsetY + 80, "LOSE", COLOR_WHITE, 0.5f);
			}
		}
	}

	/*
	 * Render results screen処理
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
		NormalFontSwing.printFont(getFieldDisplayPositionX(engine, playerID) + 12,
								  getFieldDisplayPositionY(engine, playerID) + 340, "RETRY", tempColor, 1.0f);

		if(engine.statc[0] == 1)
			tempColor = COLOR_RED;
		else
			tempColor = COLOR_WHITE;
		NormalFontSwing.printFont(getFieldDisplayPositionX(engine, playerID) + 108,
								  getFieldDisplayPositionY(engine, playerID) + 340, "END", tempColor, 1.0f);
	}

	/*
	 * fieldエディット画面の描画処理
	 */
	@Override
	public void renderFieldEdit(GameEngine engine, int playerID) {
		if(graphics == null) return;
		int x = getFieldDisplayPositionX(engine, playerID) + 4 + (engine.fldeditX * 16);
		int y = getFieldDisplayPositionY(engine, playerID) + 52 + (engine.fldeditY * 16);
		float bright = (engine.fldeditFrames % 60 >= 30) ? -0.5f : -0.2f;
		drawBlock(x, y, engine.fldeditColor, engine.getSkin(), false, bright, 1.0f, 1.0f);
	}
}
