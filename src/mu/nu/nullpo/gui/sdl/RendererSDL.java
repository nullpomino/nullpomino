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

import java.util.ArrayList;

import mu.nu.nullpo.game.component.Block;
import mu.nu.nullpo.game.component.Field;
import mu.nu.nullpo.game.component.Piece;
import mu.nu.nullpo.game.event.EventReceiver;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.gui.EffectObject;
import mu.nu.nullpo.util.CustomProperties;

import org.apache.log4j.Logger;

import sdljava.SDLException;
import sdljava.video.SDLRect;
import sdljava.video.SDLSurface;
import sdljava.video.SDLVideo;

/**
 * ゲームの event 処理と描画処理 (SDL版）
 */
public class RendererSDL extends EventReceiver {
	/** Log */
	static Logger log = Logger.getLogger(RendererSDL.class);

	/** 描画先サーフェイス */
	protected SDLSurface graphics;

	/** 演出オブジェクト */
	protected ArrayList<EffectObject> effectlist;

	/** Line clearエフェクト表示 */
	protected boolean showlineeffect;

	/** 重い演出を使う */
	protected boolean heavyeffect;

	/** fieldBackgroundの明るさ */
	protected int fieldbgbright;

	/** NEXT欄を暗くする */
	protected boolean darknextarea;

	/** ghost ピースの上にNEXT表示 */
	protected boolean nextshadow;

	/** Line clear effect speed */
	protected int lineeffectspeed;

	/**
	 * Constructor
	 */
	public RendererSDL() {
		graphics = null;
		effectlist = new ArrayList<EffectObject>(10*4);

		showbg = NullpoMinoSDL.propConfig.getProperty("option.showbg", true);
		showlineeffect = NullpoMinoSDL.propConfig.getProperty("option.showlineeffect", true);
		heavyeffect = NullpoMinoSDL.propConfig.getProperty("option.heavyeffect", false);
		fieldbgbright = NullpoMinoSDL.propConfig.getProperty("option.fieldbgbright", 128);
		showmeter = NullpoMinoSDL.propConfig.getProperty("option.showmeter", true);
		darknextarea = NullpoMinoSDL.propConfig.getProperty("option.darknextarea", true);
		nextshadow = NullpoMinoSDL.propConfig.getProperty("option.nextshadow", false);
		lineeffectspeed = NullpoMinoSDL.propConfig.getProperty("option.lineeffectspeed", 0);
		outlineghost = NullpoMinoSDL.propConfig.getProperty("option.outlineghost", false);
		sidenext = NullpoMinoSDL.propConfig.getProperty("option.sidenext", false);
		bigsidenext = NullpoMinoSDL.propConfig.getProperty("option.bigsidenext", false);
	}

	/**
	 * SDL用カラー値を取得
	 * @param r 赤
	 * @param g 緑
	 * @param b 青
	 * @return SDL用カラー値
	 */
	public long getColorValue(int r, int g, int b) {
		try {
			return SDLVideo.mapRGB(graphics.getFormat(), r, g, b);
		} catch (SDLException e) {
			log.debug("SDLException thrown", e);
		}
		return 0;
	}

	/**
	 * Block colorIDに応じてSDL用カラー値を取得
	 * @param colorID Block colorID
	 * @return SDL用カラー値
	 */
	public long getColorByID(int colorID) {
		switch(colorID) {
		case Block.BLOCK_COLOR_GRAY:   return getColorValue( 64, 64, 64);
		case Block.BLOCK_COLOR_RED:    return getColorValue(128,  0,  0);
		case Block.BLOCK_COLOR_ORANGE: return getColorValue(128, 64,  0);
		case Block.BLOCK_COLOR_YELLOW: return getColorValue(128,128,  0);
		case Block.BLOCK_COLOR_GREEN:  return getColorValue(  0,128,  0);
		case Block.BLOCK_COLOR_CYAN:   return getColorValue(  0,128,128);
		case Block.BLOCK_COLOR_BLUE:   return getColorValue(  0,  0,128);
		case Block.BLOCK_COLOR_PURPLE: return getColorValue(128,  0,128);
		}
		return getColorValue(0,0,0);
	}

	public long getColorByIDBright(int colorID) {
		switch(colorID) {
		case Block.BLOCK_COLOR_GRAY:   return getColorValue(128,128,128);
		case Block.BLOCK_COLOR_RED:    return getColorValue(255,  0,  0);
		case Block.BLOCK_COLOR_ORANGE: return getColorValue(255,128,  0);
		case Block.BLOCK_COLOR_YELLOW: return getColorValue(255,255,  0);
		case Block.BLOCK_COLOR_GREEN:  return getColorValue(  0,255,  0);
		case Block.BLOCK_COLOR_CYAN:   return getColorValue(  0,255,255);
		case Block.BLOCK_COLOR_BLUE:   return getColorValue(  0,  0,255);
		case Block.BLOCK_COLOR_PURPLE: return getColorValue(255,  0,255);
		}
		return getColorValue(0,0,0);
	}

	/*
	 * Menu 用の文字列を描画
	 */
	@Override
	public void drawMenuFont(GameEngine engine, int playerID, int x, int y, String str, int color, float scale) {
		try {
			int x2 = (scale == 0.5f) ? x * 8 : x * 16;
			int y2 = (scale == 0.5f) ? y * 8 : y * 16;
			if(!engine.owner.menuOnly) {
				x2 += getFieldDisplayPositionX(engine, playerID) + 4;
				y2 += getFieldDisplayPositionY(engine, playerID) + 52;
			}
			NormalFontSDL.printFont(x2, y2, str, color, scale);
		} catch (SDLException e) {
			log.debug("SDLException thrown", e);
		}
	}

	/*
	 * Menu 用の文字列をTTF font で描画
	 */
	@Override
	public void drawTTFMenuFont(GameEngine engine, int playerID, int x, int y, String str, int color) {
		try {
			int x2 = x * 16;
			int y2 = y * 16;
			if(!engine.owner.menuOnly) {
				x2 += getFieldDisplayPositionX(engine, playerID) + 4;
				y2 += getFieldDisplayPositionY(engine, playerID) + 52;
			}
			NormalFontSDL.printTTFFont(x2, y2, str, color);
		} catch (SDLException e) {
			log.debug("SDLException thrown", e);
		}
	}

	/*
	 * Render score用の font を描画
	 */
	@Override
	public void drawScoreFont(GameEngine engine, int playerID, int x, int y, String str, int color, float scale) {
		if(engine.owner.menuOnly) return;

		try {
			int size = (scale == 0.5f) ? 8 : 16;
			NormalFontSDL.printFont(getScoreDisplayPositionX(engine, playerID) + (x * size),
									getScoreDisplayPositionY(engine, playerID) + (y * size),
									str, color, scale);
		} catch (SDLException e) {
			log.debug("SDLException thrown", e);
		}
	}

	/*
	 * Render score用の font をTTF font で描画
	 */
	@Override
	public void drawTTFScoreFont(GameEngine engine, int playerID, int x, int y, String str, int color) {
		if(engine.owner.menuOnly) return;

		try {
			NormalFontSDL.printTTFFont(getScoreDisplayPositionX(engine, playerID) + (x * 16),
									   getScoreDisplayPositionY(engine, playerID) + (y * 16),
									   str, color);
		} catch (SDLException e) {
			log.debug("SDLException thrown", e);
		}
	}

	/*
	 * 直接指定した座標へ文字列を描画
	 */
	@Override
	public void drawDirectFont(GameEngine engine, int playerID, int x, int y, String str, int color, float scale) {
		try {
			NormalFontSDL.printFont(x, y, str, color, scale);
		} catch (SDLException e) {
			log.debug("SDLException thrown", e);
		}
	}

	/*
	 * 直接指定した座標へ描画できるTTF font を描画
	 */
	@Override
	public void drawTTFDirectFont(GameEngine engine, int playerID, int x, int y, String str, int color) {
		try {
			NormalFontSDL.printTTFFont(x, y, str, color);
		} catch (SDLException e) {
			log.debug("SDLException thrown", e);
		}
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

		SDLRect rectSrc = new SDLRect(0, 0, 42, 4);
		SDLRect rectDst = new SDLRect(dx1, dy1, 42, 4);

		try {
			ResourceHolderSDL.imgSprite.blitSurface(rectSrc, graphics, rectDst);
		} catch (SDLException e) {
			log.debug("SDLException thrown", e);
		}

		int tempSpeedMeter = s;
		if((tempSpeedMeter < 0) || (tempSpeedMeter > 40)) tempSpeedMeter = 40;

		if(tempSpeedMeter > 0) {
			SDLRect rectSrc2 = new SDLRect(0, 4, tempSpeedMeter, 2);
			SDLRect rectDst2 = new SDLRect(dx1 + 1, dy1 + 1, tempSpeedMeter, 2);

			try {
				ResourceHolderSDL.imgSprite.blitSurface(rectSrc2, graphics, rectDst2);
			} catch (SDLException e) {
				log.debug("SDLException thrown", e);
			}
		}
	}

	/*
	 * TTF使用可能
	 */
	@Override
	public boolean isTTFSupport() {
		return (ResourceHolderSDL.ttfFont != null);
	}

	/*
	 * Get key name by button ID
	 */
	@Override
	public String getKeyNameByButtonID(GameEngine engine, int btnID) {
		int[] keymap = engine.isInGame ? GameKeySDL.gamekey[engine.playerID].keymap : GameKeySDL.gamekey[engine.playerID].keymapNav;

		if((btnID >= 0) && (btnID < keymap.length)) {
			int keycode = keymap[btnID];

			if((keycode >= 0) && (keycode < NullpoMinoSDL.SDL_KEY_MAX)) {
				return NullpoMinoSDL.SDL_KEYNAMES[keycode];
			}
		}

		return "";
	}

	/*
	 * Is the skin sticky?
	 */
	@Override
	public boolean isStickySkin(int skin) {
		if((skin >= 0) && (skin < ResourceHolderSDL.blockStickyFlagList.size()) && (ResourceHolderSDL.blockStickyFlagList.get(skin) == true)) {
			return true;
		}
		return false;
	}

	/*
	 * Sound effects再生
	 */
	@Override
	public void playSE(String name) {
		ResourceHolderSDL.soundManager.play(name);
	}

	/*
	 * 描画先のサーフェイスを設定
	 */
	@Override
	public void setGraphics(Object g) {
		if(g instanceof SDLSurface) {
			graphics = (SDLSurface)g;
		}
	}

	/*
	 * リプレイを保存
	 */
	@Override
	public void saveReplay(GameManager owner, CustomProperties prop) {
		if(owner.mode.isNetplayMode()) return;

		saveReplay(owner, prop, NullpoMinoSDL.propGlobal.getProperty("custom.replay.directory", "replay"));
	}

	/*
	 * 1マスBlockを描画
	 */
	@Override
	public void drawSingleBlock(GameEngine engine, int playerID, int x, int y, int color, int skin, boolean bone, float darkness, float alpha, float scale) {
		try {
			drawBlock(x, y, color, skin, bone, darkness, alpha, scale);
		} catch (SDLException e) {
			log.debug("SDLException thrown", e);
		}
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
	 * @throws SDLException When something bad happens
	 */
	protected void drawBlock(int x, int y, int color, int skin, boolean bone, float darkness, float alpha, float scale, int attr) throws SDLException {
		if(graphics == null) return;

		if(color <= Block.BLOCK_COLOR_INVALID) return;
		if(skin >= ResourceHolderSDL.imgNormalBlockList.size()) skin = 0;

		boolean isSpecialBlocks = (color >= Block.BLOCK_COLOR_COUNT);
		boolean isSticky = ResourceHolderSDL.blockStickyFlagList.get(skin);

		int size = (int)(16 * scale);
		SDLSurface img = null;
		if(scale == 0.5f)
			img = ResourceHolderSDL.imgSmallBlockList.get(skin);
		else if(scale == 2.0f)
			img = ResourceHolderSDL.imgBigBlockList.get(skin);
		else
			img = ResourceHolderSDL.imgNormalBlockList.get(skin);

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

		SDLRect rectSrc = new SDLRect(sx, sy, size, size);
		SDLRect rectDst = new SDLRect(x, y, size, size);

		NullpoMinoSDL.fixRect(rectSrc, rectDst);

		if(alpha < 1.0f) {
			int alphalv = (int)(255 * alpha);
			img.setAlpha(SDLVideo.SDL_SRCALPHA | SDLVideo.SDL_RLEACCEL, alphalv);
		} else {
			img.setAlpha(0, 255);
		}

		img.blitSurface(rectSrc, graphics, rectDst);

		if(isSticky && !isSpecialBlocks) {
			int d = 16 * size;
			int h = (size/2);

			SDLRect rectDst2 = null;
			SDLRect rectSrc2 = null;

			if( ((attr & Block.BLOCK_ATTRIBUTE_CONNECT_UP) != 0) && ((attr & Block.BLOCK_ATTRIBUTE_CONNECT_LEFT) != 0) ) {
				rectDst2 = new SDLRect(x, y, h, h);
				rectSrc2 = new SDLRect(d, sy, h, h);
				NullpoMinoSDL.fixRect(rectSrc2, rectDst2);
				img.blitSurface(rectSrc2, graphics, rectDst2);
				//graphics.drawImage(img, x, y, x + h, y + h, d, sy, d + h, sy + h, filter);
			}
			if( ((attr & Block.BLOCK_ATTRIBUTE_CONNECT_UP) != 0) && ((attr & Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT) != 0) ) {
				rectDst2 = new SDLRect(x + h, y, h, h);
				rectSrc2 = new SDLRect(d + h, sy, h, h);
				NullpoMinoSDL.fixRect(rectSrc2, rectDst2);
				img.blitSurface(rectSrc2, graphics, rectDst2);
				//graphics.drawImage(img, x + h, y, x + h + h, y + h, d + h, sy, d + h + h, sy + h, filter);
			}
			if( ((attr & Block.BLOCK_ATTRIBUTE_CONNECT_DOWN) != 0) && ((attr & Block.BLOCK_ATTRIBUTE_CONNECT_LEFT) != 0) ) {
				rectDst2 = new SDLRect(x, y + h, h, h);
				rectSrc2 = new SDLRect(d, sy + h, h, h);
				NullpoMinoSDL.fixRect(rectSrc2, rectDst2);
				img.blitSurface(rectSrc2, graphics, rectDst2);
				//graphics.drawImage(img, x, y + h, x + h, y + h + h, d, sy + h, d + h, sy + h + h, filter);
			}
			if( ((attr & Block.BLOCK_ATTRIBUTE_CONNECT_DOWN) != 0) && ((attr & Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT) != 0) ) {
				rectDst2 = new SDLRect(x + h, y + h, h, h);
				rectSrc2 = new SDLRect(d + h, sy + h, h, h);
				NullpoMinoSDL.fixRect(rectSrc2, rectDst2);
				img.blitSurface(rectSrc2, graphics, rectDst2);
				//graphics.drawImage(img, x + h, y + h, x + h + h, y + h + h, d + h, sy + h, d + h + h, sy + h + h, filter);
			}
		}

		if(darkness > 0) {
			int alphalv = (int)(255 * darkness);
			ResourceHolderSDL.imgBlankBlack.setAlpha(SDLVideo.SDL_SRCALPHA | SDLVideo.SDL_RLEACCEL, alphalv);
			ResourceHolderSDL.imgBlankBlack.blitSurface(new SDLRect(0, 0, size, size), graphics, rectDst);
		} else if(darkness < 0) {
			int alphalv = (int)(255 * -darkness);
			ResourceHolderSDL.imgBlankWhite.setAlpha(SDLVideo.SDL_SRCALPHA | SDLVideo.SDL_RLEACCEL, alphalv);
			ResourceHolderSDL.imgBlankWhite.blitSurface(new SDLRect(0, 0, size, size), graphics, rectDst);
		}
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
	 * @throws SDLException 描画に失敗した場合
	 */
	protected void drawBlock(int x, int y, int color, int skin, boolean bone, float darkness, float alpha, float scale) throws SDLException {
		drawBlock(x, y, color, skin, bone, darkness, alpha, scale, 0);
	}

	/**
	 * Blockクラスのインスタンスを使用してBlockを描画
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param blk Blockクラスのインスタンス
	 * @throws SDLException 描画に失敗した場合
	 */
	protected void drawBlock(int x, int y, Block blk) throws SDLException {
		drawBlock(x, y, blk.getDrawColor(), blk.skin, blk.getAttribute(Block.BLOCK_ATTRIBUTE_BONE), blk.darkness, blk.alpha, 1.0f, blk.attribute);
	}

	/**
	 * Blockクラスのインスタンスを使用してBlockを描画 (拡大率指定可能）
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param blk Blockクラスのインスタンス
	 * @param scale 拡大率
	 * @throws SDLException 描画に失敗した場合
	 */
	protected void drawBlock(int x, int y, Block blk, float scale) throws SDLException {
		drawBlock(x, y, blk.getDrawColor(), blk.skin, blk.getAttribute(Block.BLOCK_ATTRIBUTE_BONE), blk.darkness, blk.alpha, scale, blk.attribute);
	}

	/**
	 * Blockクラスのインスタンスを使用してBlockを描画 (拡大率と暗さ指定可能）
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param blk Blockクラスのインスタンス
	 * @param scale 拡大率
	 * @param darkness 暗さもしくは明るさ
	 * @throws SDLException 描画に失敗した場合
	 */
	protected void drawBlock(int x, int y, Block blk, float scale, float darkness) throws SDLException {
		drawBlock(x, y, blk.getDrawColor(), blk.skin, blk.getAttribute(Block.BLOCK_ATTRIBUTE_BONE), darkness, blk.alpha, scale, blk.attribute);
	}

	protected void drawBlockForceVisible(int x, int y, Block blk, float scale) throws SDLException {
		drawBlock(x, y, blk.getDrawColor(), blk.skin, blk.getAttribute(Block.BLOCK_ATTRIBUTE_BONE), blk.darkness,
				(0.5f*blk.alpha)+0.5f, scale, blk.attribute);
	}

	/**
	 * Blockピースを描画
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param piece 描画するピース
	 * @throws SDLException 描画に失敗した場合
	 */
	protected void drawPiece(int x, int y, Piece piece) throws SDLException {
		drawPiece(x, y, piece, 1.0f);
	}

	/**
	 * Blockピースを描画 (拡大率指定可能）
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param piece 描画するピース
	 * @param scale 拡大率
	 * @throws SDLException 描画に失敗した場合
	 */
	protected void drawPiece(int x, int y, Piece piece, float scale) throws SDLException {
		drawPiece(x, y, piece, scale, 0f);
	}

	/**
	 * Blockピースを描画 (暗さもしくは明るさの指定可能）
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param piece 描画するピース
	 * @param scale 拡大率
	 * @param darkness 暗さもしくは明るさ
	 * @throws SDLException 描画に失敗した場合
	 */
	protected void drawPiece(int x, int y, Piece piece, float scale, float darkness) throws SDLException {
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
	 * @param scale 表示倍率
	 * @throws SDLException 描画に失敗した場合
	 */
	protected void drawCurrentPiece(int x, int y, GameEngine engine, float scale) throws SDLException {
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
	 * @param scale 表示倍率
	 * @throws SDLException 描画に失敗した場合
	 */
	protected void drawGhostPiece(int x, int y, GameEngine engine, float scale) throws SDLException {
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

							int colorID = blkTemp.getDrawColor();
							if(blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_BONE)) colorID = -1;
							long color = getColorByID(colorID);
							graphics.fillRect(new SDLRect(x3, y3, blksize, blksize), color);

							if(!blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_UP)) {
								ResourceHolderSDL.imgSprite.blitSurface(new SDLRect(1,16,blksize,1), graphics, new SDLRect(x3,y3,blksize,1));
								ResourceHolderSDL.imgSprite.blitSurface(new SDLRect(1,16,blksize,1), graphics, new SDLRect(x3,y3+1,blksize,1));
							}
							if(!blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_DOWN)) {
								ResourceHolderSDL.imgSprite.blitSurface(new SDLRect(1,16,blksize,1), graphics, new SDLRect(x3,y3 + blksize-1,blksize,1));
								ResourceHolderSDL.imgSprite.blitSurface(new SDLRect(1,16,blksize,1), graphics, new SDLRect(x3,y3 + blksize-2,blksize,1));
							}
							if(!blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_LEFT)) {
								ResourceHolderSDL.imgSprite.blitSurface(new SDLRect(0,16,1,blksize), graphics, new SDLRect(x3,y3,1,blksize));
								ResourceHolderSDL.imgSprite.blitSurface(new SDLRect(0,16,1,blksize), graphics, new SDLRect(x3+1,y3,1,blksize));
							}
							if(!blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT)) {
								ResourceHolderSDL.imgSprite.blitSurface(new SDLRect(0,16,1,blksize), graphics, new SDLRect(x3 + blksize-1,y3,1,blksize));
								ResourceHolderSDL.imgSprite.blitSurface(new SDLRect(0,16,1,blksize), graphics, new SDLRect(x3 + blksize-2,y3,1,blksize));
							}

							color = getColorValue(255, 255, 255);
							if(blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_LEFT | Block.BLOCK_ATTRIBUTE_CONNECT_UP)) {
								graphics.fillRect(new SDLRect(x3, y3, 2, 2), color);
							}
							if(blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_LEFT | Block.BLOCK_ATTRIBUTE_CONNECT_DOWN)) {
								graphics.fillRect(new SDLRect(x3, y3 + (blksize-2), 2, 2), color);
							}
							if(blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT | Block.BLOCK_ATTRIBUTE_CONNECT_UP)) {
								graphics.fillRect(new SDLRect(x3 + (blksize-2), y3, 2, 2), color);
							}
							if(blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT | Block.BLOCK_ATTRIBUTE_CONNECT_DOWN)) {
								graphics.fillRect(new SDLRect(x3 + (blksize-2), y3 + (blksize-2), 2, 2), color);
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

						int colorID = blkTemp.getDrawColor();
						if(blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_BONE)) colorID = -1;
						long color = getColorByID(colorID);
						graphics.fillRect(new SDLRect(x3, y3, blksize * 2, blksize * 2), color);

						if(!blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_UP)) {
							ResourceHolderSDL.imgSprite.blitSurface(new SDLRect(1,16,blksize*2,1), graphics, new SDLRect(x3,y3,blksize*2,1));
							ResourceHolderSDL.imgSprite.blitSurface(new SDLRect(1,16,blksize*2,1), graphics, new SDLRect(x3,y3+1,blksize*2,1));
						}
						if(!blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_DOWN)) {
							ResourceHolderSDL.imgSprite.blitSurface(new SDLRect(1,16,blksize*2,1), graphics, new SDLRect(x3,y3 + blksize*2-1,blksize*2,1));
							ResourceHolderSDL.imgSprite.blitSurface(new SDLRect(1,16,blksize*2,1), graphics, new SDLRect(x3,y3 + blksize*2-2,blksize*2,1));
						}
						if(!blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_LEFT)) {
							ResourceHolderSDL.imgSprite.blitSurface(new SDLRect(0,16,1,blksize*2), graphics, new SDLRect(x3,y3,1,blksize*2));
							ResourceHolderSDL.imgSprite.blitSurface(new SDLRect(0,16,1,blksize*2), graphics, new SDLRect(x3+1,y3,1,blksize*2));
						}
						if(!blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT)) {
							ResourceHolderSDL.imgSprite.blitSurface(new SDLRect(0,16,1,blksize*2), graphics, new SDLRect(x3 + blksize*2-1,y3,1,blksize*2));
							ResourceHolderSDL.imgSprite.blitSurface(new SDLRect(0,16,1,blksize*2), graphics, new SDLRect(x3 + blksize*2-2,y3,1,blksize*2));
						}

						color = getColorValue(255, 255, 255);
						if(blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_LEFT | Block.BLOCK_ATTRIBUTE_CONNECT_UP)) {
							graphics.fillRect(new SDLRect(x3, y3, 2, 2), color);
						}
						if(blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_LEFT | Block.BLOCK_ATTRIBUTE_CONNECT_DOWN)) {
							graphics.fillRect(new SDLRect(x3, y3 + (blksize*2-2), 2, 2), color);
						}
						if(blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT | Block.BLOCK_ATTRIBUTE_CONNECT_UP)) {
							graphics.fillRect(new SDLRect(x3 + (blksize*2-2), y3, 2, 2), color);
						}
						if(blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT | Block.BLOCK_ATTRIBUTE_CONNECT_DOWN)) {
							graphics.fillRect(new SDLRect(x3 + (blksize*2-2), y3 + (blksize*2-2), 2, 2), color);
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

	protected void drawHintPiece(int x, int y, GameEngine engine, float scale) throws SDLException {
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
							long color = getColorByIDBright(colorID);
							//graphics.fillRect(new SDLRect(x3, y3, blksize, blksize), color);

							if(!blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_UP))
								graphics.fillRect(new SDLRect(x3, y3, ls, 2), color);
							if(!blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_DOWN))
								graphics.fillRect(new SDLRect(x3, y3 + ls - 1, ls, 2), color);
							if(!blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_LEFT))
								graphics.fillRect(new SDLRect(x3, y3, 2, ls), color);
							if(!blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT))
								graphics.fillRect(new SDLRect(x3 + ls - 1, y3, 2, ls), color);
							if(blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_LEFT | Block.BLOCK_ATTRIBUTE_CONNECT_UP))
								graphics.fillRect(new SDLRect(x3, y3, 2, 2), color);
							if(blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_LEFT | Block.BLOCK_ATTRIBUTE_CONNECT_DOWN))
								graphics.fillRect(new SDLRect(x3, y3 + (blksize-2), 2, 2), color);
							if(blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT | Block.BLOCK_ATTRIBUTE_CONNECT_UP))
								graphics.fillRect(new SDLRect(x3 + (blksize-2), y3, 2, 2), color);
							if(blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT | Block.BLOCK_ATTRIBUTE_CONNECT_DOWN))
								graphics.fillRect(new SDLRect(x3 + (blksize-2), y3 + (blksize-2), 2, 2), color);
						}
					} else {
						int x2 = engine.aiHintX + (piece.dataX[piece.direction][i] * 2);
						int y2 = engine.aiHintY + (piece.dataY[piece.direction][i] * 2);

						Block blkTemp = piece.block[i];
						int x3 = x + (x2 * blksize);
						int y3 = y + (y2 * blksize);

						int colorID = blkTemp.getDrawColor();
						if(blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_BONE)) colorID = -1;
						long color = getColorByID(colorID);
						//graphics.fillRect(new SDLRect(x3, y3, blksize * 2, blksize * 2), color);

						if(!blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_UP)) {
							ResourceHolderSDL.imgSprite.blitSurface(new SDLRect(1,16,blksize*2,1), graphics, new SDLRect(x3,y3,blksize*2,1));
							ResourceHolderSDL.imgSprite.blitSurface(new SDLRect(1,16,blksize*2,1), graphics, new SDLRect(x3,y3+1,blksize*2,1));
						}
						if(!blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_DOWN)) {
							ResourceHolderSDL.imgSprite.blitSurface(new SDLRect(1,16,blksize*2,1), graphics, new SDLRect(x3,y3 + blksize*2-1,blksize*2,1));
							ResourceHolderSDL.imgSprite.blitSurface(new SDLRect(1,16,blksize*2,1), graphics, new SDLRect(x3,y3 + blksize*2-2,blksize*2,1));
						}
						if(!blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_LEFT)) {
							ResourceHolderSDL.imgSprite.blitSurface(new SDLRect(0,16,1,blksize*2), graphics, new SDLRect(x3,y3,1,blksize*2));
							ResourceHolderSDL.imgSprite.blitSurface(new SDLRect(0,16,1,blksize*2), graphics, new SDLRect(x3+1,y3,1,blksize*2));
						}
						if(!blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT)) {
							ResourceHolderSDL.imgSprite.blitSurface(new SDLRect(0,16,1,blksize*2), graphics, new SDLRect(x3 + blksize*2-1,y3,1,blksize*2));
							ResourceHolderSDL.imgSprite.blitSurface(new SDLRect(0,16,1,blksize*2), graphics, new SDLRect(x3 + blksize*2-2,y3,1,blksize*2));
						}

						color = getColorValue(255, 255, 255);
						if(blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_LEFT | Block.BLOCK_ATTRIBUTE_CONNECT_UP)) {
							graphics.fillRect(new SDLRect(x3, y3, 2, 2), color);
						}
						if(blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_LEFT | Block.BLOCK_ATTRIBUTE_CONNECT_DOWN)) {
							graphics.fillRect(new SDLRect(x3, y3 + (blksize*2-2), 2, 2), color);
						}
						if(blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT | Block.BLOCK_ATTRIBUTE_CONNECT_UP)) {
							graphics.fillRect(new SDLRect(x3 + (blksize*2-2), y3, 2, 2), color);
						}
						if(blkTemp.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT | Block.BLOCK_ATTRIBUTE_CONNECT_DOWN)) {
							graphics.fillRect(new SDLRect(x3 + (blksize*2-2), y3 + (blksize*2-2), 2, 2), color);
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
	 * @param small 半分サイズ
	 * @throws SDLException 描画に失敗した場合
	 */
	protected void drawField(int x, int y, GameEngine engine, int size) throws SDLException {
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

		SDLSurface imgFieldbg = ResourceHolderSDL.imgFieldbg;
		//if((width == 10) && (height == 20)) imgFieldbg = ResourceHolderSDL.imgFieldbg2;
		if(engine.owner.getPlayers() < 2)
			imgFieldbg.setAlpha(SDLVideo.SDL_SRCALPHA | SDLVideo.SDL_RLEACCEL, fieldbgbright);
		else
			imgFieldbg.setAlpha(0, 255);

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

					if( (!blk.getAttribute(Block.BLOCK_ATTRIBUTE_VISIBLE) || (blk.alpha < 1.0f)) && (fieldbgbright > 0) ) {
						if((width > 10) && (height > 20)) {
							int sx = (((i % 2 == 0) && (j % 2 == 0)) || ((i % 2 != 0) && (j % 2 != 0))) ? 0 : 32;
							imgFieldbg.blitSurface(new SDLRect(sx,0,blksize,blksize), graphics, new SDLRect(x2,y2,blksize,blksize));
						}
					}

					if(blk.getAttribute(Block.BLOCK_ATTRIBUTE_OUTLINE) && !blk.getAttribute(Block.BLOCK_ATTRIBUTE_BONE)) {
						ResourceHolderSDL.imgSprite.setAlpha(SDLVideo.SDL_SRCALPHA | SDLVideo.SDL_RLEACCEL, (int)(255 * blk.alpha));

						if(engine.blockOutlineType == GameEngine.BLOCK_OUTLINE_NORMAL) {
							if(field.getBlockColor(j, i - 1) == Block.BLOCK_COLOR_NONE)
								ResourceHolderSDL.imgSprite.blitSurface(new SDLRect(1,16,blksize,1), graphics, new SDLRect(x2,y2,blksize,1));
							if(field.getBlockColor(j, i + 1) == Block.BLOCK_COLOR_NONE)
								ResourceHolderSDL.imgSprite.blitSurface(new SDLRect(1,16,blksize,1), graphics, new SDLRect(x2,y2 + blksize-1,blksize,1));
							if(field.getBlockColor(j - 1, i) == Block.BLOCK_COLOR_NONE)
								ResourceHolderSDL.imgSprite.blitSurface(new SDLRect(0,16,1,blksize), graphics, new SDLRect(x2,y2,1,blksize));
							if(field.getBlockColor(j + 1, i) == Block.BLOCK_COLOR_NONE)
								ResourceHolderSDL.imgSprite.blitSurface(new SDLRect(0,16,1,blksize), graphics, new SDLRect(x2 + blksize-1,y2,1,blksize));
						} else if(engine.blockOutlineType == GameEngine.BLOCK_OUTLINE_CONNECT) {
							if(!blk.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_UP))
								ResourceHolderSDL.imgSprite.blitSurface(new SDLRect(1,16,blksize,1), graphics, new SDLRect(x2,y2,blksize,1));
							if(!blk.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_DOWN))
								ResourceHolderSDL.imgSprite.blitSurface(new SDLRect(1,16,blksize,1), graphics, new SDLRect(x2,y2 + blksize-1,blksize,1));
							if(!blk.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_LEFT))
								ResourceHolderSDL.imgSprite.blitSurface(new SDLRect(0,16,1,blksize), graphics, new SDLRect(x2,y2,1,blksize));
							if(!blk.getAttribute(Block.BLOCK_ATTRIBUTE_CONNECT_RIGHT))
								ResourceHolderSDL.imgSprite.blitSurface(new SDLRect(0,16,1,blksize), graphics, new SDLRect(x2 + blksize-1,y2,1,blksize));
						} else if(engine.blockOutlineType == GameEngine.BLOCK_OUTLINE_SAMECOLOR) {
							if(field.getBlockColor(j, i - 1) != blk.color)
								ResourceHolderSDL.imgSprite.blitSurface(new SDLRect(1,16,blksize,1), graphics, new SDLRect(x2,y2,blksize,1));
							if(field.getBlockColor(j, i + 1) != blk.color)
								ResourceHolderSDL.imgSprite.blitSurface(new SDLRect(1,16,blksize,1), graphics, new SDLRect(x2,y2 + blksize-1,blksize,1));
							if(field.getBlockColor(j - 1, i) != blk.color)
								ResourceHolderSDL.imgSprite.blitSurface(new SDLRect(0,16,1,blksize), graphics, new SDLRect(x2,y2,1,blksize));
							if(field.getBlockColor(j + 1, i) != blk.color)
								ResourceHolderSDL.imgSprite.blitSurface(new SDLRect(0,16,1,blksize), graphics, new SDLRect(x2 + blksize-1,y2,1,blksize));
						}

						ResourceHolderSDL.imgSprite.setAlpha(0, 255);
					}
				} else if(fieldbgbright > 0) {
					if((width > 10) && (height > 20)) {
						int sx = (((i % 2 == 0) && (j % 2 == 0)) || ((i % 2 != 0) && (j % 2 != 0))) ? 0 : 32;
						imgFieldbg.blitSurface(new SDLRect(sx,0,blksize,blksize), graphics, new SDLRect(x2,y2,blksize,blksize));
					}
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
	 * @param small 半分サイズ
	 * @throws SDLException 描画に失敗した場合
	 */
	protected void drawFrame(int x, int y, GameEngine engine, int displaysize) throws SDLException {
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
			if((width <= 10) && (height <= 20)) {
				SDLSurface img = ResourceHolderSDL.imgFieldbg2;
				if(displaysize == -1) img = ResourceHolderSDL.imgFieldbg2Small;
				if(displaysize == 1) img = ResourceHolderSDL.imgFieldbg2Big;

				if(engine.owner.getPlayers() < 2)
					img.setAlpha(SDLVideo.SDL_SRCALPHA | SDLVideo.SDL_RLEACCEL, fieldbgbright);
				else
					img.setAlpha(0, 255);

				img.blitSurface(new SDLRect(0, 0, width*size*4, height*size*4), graphics, new SDLRect(x + 4, y + 4, width*size*4, height*size*4));
			}
		}

		SDLRect rectSrc = null;
		SDLRect rectDst = null;

		// Upと下
		int maxWidth = (width * size);
		if(showmeter) maxWidth = (width * size) + 2;

		for(int i = 0; i < maxWidth; i++) {
			rectSrc = new SDLRect(offsetX + 4, 0, 4, 4);
			rectDst = new SDLRect(x + ((i + 1) * 4), y, 4, 4);
			ResourceHolderSDL.imgFrame.blitSurface(rectSrc, graphics, rectDst);

			rectSrc = new SDLRect(offsetX + 4, 8, 4, 4);
			rectDst = new SDLRect(x + ((i + 1) * 4), y + (height * size * 4) + 4, 4, 4);
			ResourceHolderSDL.imgFrame.blitSurface(rectSrc, graphics, rectDst);
		}

		// 左と右
		for(int i = 0; i < height * size; i++) {
			rectSrc = new SDLRect(offsetX + 0, 4, 4, 4);
			rectDst = new SDLRect(x, y + ((i + 1) * 4), 4, 4);
			ResourceHolderSDL.imgFrame.blitSurface(rectSrc, graphics, rectDst);

			rectSrc = new SDLRect(offsetX + 8, 4, 4, 4);
			if(showmeter) rectDst = new SDLRect(x + (width * size * 4) + 12, y + ((i + 1) * 4), 4, 4);
			else rectDst = new SDLRect(x + (width * size * 4) + 4, y + ((i + 1) * 4), 4, 4);
			ResourceHolderSDL.imgFrame.blitSurface(rectSrc, graphics, rectDst);
		}

		// 左上
		rectSrc = new SDLRect(offsetX + 0, 0, 4, 4);
		rectDst = new SDLRect(x, y, 4, 4);
		ResourceHolderSDL.imgFrame.blitSurface(rectSrc, graphics, rectDst);

		// 左下
		rectSrc = new SDLRect(offsetX + 0, 8, 4, 4);
		rectDst = new SDLRect(x, y + (height * size * 4) + 4, 4, 4);
		ResourceHolderSDL.imgFrame.blitSurface(rectSrc, graphics, rectDst);

		if(showmeter) {
			// MeterONのときの右上
			rectSrc = new SDLRect(offsetX + 8, 0, 4, 4);
			rectDst = new SDLRect(x + (width * size * 4) + 12, y, 4, 4);
			ResourceHolderSDL.imgFrame.blitSurface(rectSrc, graphics, rectDst);

			// MeterONのときの右下
			rectSrc = new SDLRect(offsetX + 8, 8, 4, 4);
			rectDst = new SDLRect(x + (width * size * 4) + 12, y + (height * size * 4) + 4, 4, 4);
			ResourceHolderSDL.imgFrame.blitSurface(rectSrc, graphics, rectDst);

			// 右Meterの枠
			for(int i = 0; i < height * size; i++) {
				rectSrc = new SDLRect(offsetX + 12, 4, 4, 4);
				rectDst = new SDLRect(x + (width * size * 4) + 4, y + ((i + 1) * 4), 4, 4);
				ResourceHolderSDL.imgFrame.blitSurface(rectSrc, graphics, rectDst);
			}

			rectSrc = new SDLRect(offsetX + 12, 0, 4, 4);
			rectDst = new SDLRect(x + (width * size * 4) + 4, y, 4, 4);
			ResourceHolderSDL.imgFrame.blitSurface(rectSrc, graphics, rectDst);

			rectSrc = new SDLRect(offsetX + 12, 8, 4, 4);
			rectDst = new SDLRect(x + (width * size * 4) + 4, y + (height * size * 4) + 4, 4, 4);
			ResourceHolderSDL.imgFrame.blitSurface(rectSrc, graphics, rectDst);

			// 右Meter
			int maxHeight = height * size * 4;
			if((engine != null) && (engine.meterValue > 0)) maxHeight = (height * size * 4) - engine.meterValue;

			for(int i = 0; i < maxHeight; i++) {
				rectSrc = new SDLRect(59, 0, 4, 1);
				rectDst = new SDLRect(x + (width * size * 4) + 8, y + 4 + i, 4, 1);
				ResourceHolderSDL.imgSprite.blitSurface(rectSrc, graphics, rectDst);
			}

			if((engine != null) && (engine.meterValue > 0)) {
				int value = engine.meterValue;
				if(value > height * size * 4) value = height * size * 4;

				for(int i = 0; i < value; i++) {
					rectSrc = new SDLRect(63 + (engine.meterColor * 4), 0, 4, 1);
					rectDst = new SDLRect(x + (width * size * 4) + 8, y + (height * size * 4) + 3 - i, 4, 1);
					ResourceHolderSDL.imgSprite.blitSurface(rectSrc, graphics, rectDst);
				}
			}
		} else {
			// MeterOFFのときの右上
			rectSrc = new SDLRect(offsetX + 8, 0, 4, 4);
			rectDst = new SDLRect(x + (width * size * 4) + 4, y, 4, 4);
			ResourceHolderSDL.imgFrame.blitSurface(rectSrc, graphics, rectDst);

			// MeterOFFのときの右下
			rectSrc = new SDLRect(offsetX + 8, 8, 4, 4);
			rectDst = new SDLRect(x + (width * size * 4) + 4, y + (height * size * 4) + 4, 4, 4);
			ResourceHolderSDL.imgFrame.blitSurface(rectSrc, graphics, rectDst);
		}
	}

	/**
	 * NEXTを描画
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param engine GameEngineのインスタンス
	 * @throws SDLException 描画に失敗した場合
	 */
	protected void drawNext(int x, int y, GameEngine engine) throws SDLException {
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
			if(getNextDisplayType() == 2) {
				int x2 = x + 8 + (fldWidth * fldBlkSize) + meterWidth;
				int maxNext = engine.isNextVisible ? engine.ruleopt.nextDisplay : 0;

				// HOLD area
				if(engine.ruleopt.holdEnable && engine.isHoldVisible) {
					ResourceHolderSDL.imgBlankBlack.setAlpha(0, 255);
					ResourceHolderSDL.imgBlankBlack.blitSurface(new SDLRect(0,0,64,64 - 16), graphics, new SDLRect(x - 64, y + 48 + 8, 64, 64 - 16));

					for(int i = 0; i <= 8; i++) {
						int alpha = (int)(((float)i / (float)8) * 255);
						ResourceHolderSDL.imgBlankBlack.setAlpha(SDLVideo.SDL_SRCALPHA | SDLVideo.SDL_RLEACCEL, alpha);
						ResourceHolderSDL.imgBlankBlack.blitSurface(new SDLRect(0,0,64,1), graphics, new SDLRect(x - 64, y + 47 + i, 64, 1));
					}
					for(int i = 0; i <= 8; i++) {
						int alpha = (int)(((float)i / (float)8) * 255);
						ResourceHolderSDL.imgBlankBlack.setAlpha(SDLVideo.SDL_SRCALPHA | SDLVideo.SDL_RLEACCEL, alpha);
						ResourceHolderSDL.imgBlankBlack.blitSurface(new SDLRect(0,0,64,1), graphics, new SDLRect(x - 64, y + 112 - i, 64, 1));
					}
				}

				// NEXT area
				if(maxNext > 0) {
					ResourceHolderSDL.imgBlankBlack.setAlpha(0, 255);
					ResourceHolderSDL.imgBlankBlack.blitSurface(new SDLRect(0,0,64,(64 * maxNext)-16),graphics,
							new SDLRect(x2,y + 48 + 8,64,(64 * maxNext) - 16));

					for(int i = 0; i <= 8; i++) {
						int alpha = (int)(((float)i / (float)8) * 255);
						ResourceHolderSDL.imgBlankBlack.setAlpha(SDLVideo.SDL_SRCALPHA | SDLVideo.SDL_RLEACCEL, alpha);
						ResourceHolderSDL.imgBlankBlack.blitSurface(new SDLRect(0,0,64,1), graphics, new SDLRect(x2, y + 47 + i, 64, 1));
					}
					for(int i = 0; i <= 8; i++) {
						int alpha = (int)(((float)i / (float)8) * 255);
						ResourceHolderSDL.imgBlankBlack.setAlpha(SDLVideo.SDL_SRCALPHA | SDLVideo.SDL_RLEACCEL, alpha);
						ResourceHolderSDL.imgBlankBlack.blitSurface(new SDLRect(0,0,64,1), graphics, new SDLRect(x2, y + 48+(64*maxNext)-i, 64, 1));
					}
				}
			} else if(getNextDisplayType() == 1) {
				int x2 = x + 8 + (fldWidth * fldBlkSize) + meterWidth;
				int maxNext = engine.isNextVisible ? engine.ruleopt.nextDisplay : 0;

				// HOLD area
				if(engine.ruleopt.holdEnable && engine.isHoldVisible) {
					ResourceHolderSDL.imgBlankBlack.setAlpha(0, 255);
					ResourceHolderSDL.imgBlankBlack.blitSurface(new SDLRect(0,0,32,32 - 16), graphics, new SDLRect(x - 32, y + 48 + 8, 32, 32 - 16));

					for(int i = 0; i <= 8; i++) {
						int alpha = (int)(((float)i / (float)8) * 255);
						ResourceHolderSDL.imgBlankBlack.setAlpha(SDLVideo.SDL_SRCALPHA | SDLVideo.SDL_RLEACCEL, alpha);
						ResourceHolderSDL.imgBlankBlack.blitSurface(new SDLRect(0,0,32,1), graphics, new SDLRect(x - 32, y + 47 + i, 32, 1));
					}
					for(int i = 0; i <= 8; i++) {
						int alpha = (int)(((float)i / (float)8) * 255);
						ResourceHolderSDL.imgBlankBlack.setAlpha(SDLVideo.SDL_SRCALPHA | SDLVideo.SDL_RLEACCEL, alpha);
						ResourceHolderSDL.imgBlankBlack.blitSurface(new SDLRect(0,0,32,1), graphics, new SDLRect(x - 32, y + 80 - i, 32, 1));
					}
				}

				// NEXT area
				if(maxNext > 0) {
					ResourceHolderSDL.imgBlankBlack.setAlpha(0, 255);
					ResourceHolderSDL.imgBlankBlack.blitSurface(new SDLRect(0,0,32,(32 * maxNext)-16),graphics,
							new SDLRect(x2,y + 48 + 8,32,(32 * maxNext) - 16));

					for(int i = 0; i <= 8; i++) {
						int alpha = (int)(((float)i / (float)8) * 255);
						ResourceHolderSDL.imgBlankBlack.setAlpha(SDLVideo.SDL_SRCALPHA | SDLVideo.SDL_RLEACCEL, alpha);
						ResourceHolderSDL.imgBlankBlack.blitSurface(new SDLRect(0,0,32,1), graphics, new SDLRect(x2, y + 47 + i, 32, 1));
					}
					for(int i = 0; i <= 8; i++) {
						int alpha = (int)(((float)i / (float)8) * 255);
						ResourceHolderSDL.imgBlankBlack.setAlpha(SDLVideo.SDL_SRCALPHA | SDLVideo.SDL_RLEACCEL, alpha);
						ResourceHolderSDL.imgBlankBlack.blitSurface(new SDLRect(0,0,32,1), graphics, new SDLRect(x2, y + 48+(32*maxNext)-i, 32, 1));
					}
				}
			} else {
				int w = (fldWidth * fldBlkSize) + 15;

				ResourceHolderSDL.imgBlankBlack.setAlpha(0, 255);
				ResourceHolderSDL.imgBlankBlack.blitSurface(new SDLRect(0,0,w-40,48), graphics, new SDLRect(x + 20, y, w-40, 48));

				for(int i = 0; i <= 20; i++) {
					int alpha = (int)(((float)i / (float)20) * 255);
					ResourceHolderSDL.imgBlankBlack.setAlpha(SDLVideo.SDL_SRCALPHA | SDLVideo.SDL_RLEACCEL, alpha);
					ResourceHolderSDL.imgBlankBlack.blitSurface(new SDLRect(0,0,1,48), graphics, new SDLRect(x + i - 1, y, 1, 48));
				}
				for(int i = 0; i <= 20; i++) {
					int alpha = (int)(((float)(20 - i) / (float)20) * 255);
					ResourceHolderSDL.imgBlankBlack.setAlpha(SDLVideo.SDL_SRCALPHA | SDLVideo.SDL_RLEACCEL, alpha);
					ResourceHolderSDL.imgBlankBlack.blitSurface(new SDLRect(0,0,1,48), graphics, new SDLRect(x + i + (w-20), y, 1, 48));
				}
			}

			ResourceHolderSDL.imgBlankBlack.setAlpha(0, 255);
		}

		if(engine.isNextVisible) {
			if(getNextDisplayType() == 2) {
				if(engine.ruleopt.nextDisplay >= 1) {
					int x2 = x + 8 + (fldWidth * fldBlkSize) + meterWidth;
					NormalFontSDL.printFont(x2 + 16, y + 40, NullpoMinoSDL.getUIText("InGame_Next"), COLOR_ORANGE, 0.5f);

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
					NormalFontSDL.printFont(x2, y + 40, NullpoMinoSDL.getUIText("InGame_Next"), COLOR_ORANGE, 0.5f);

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
					NormalFontSDL.printFont(x + 60, y, NullpoMinoSDL.getUIText("InGame_Next"), COLOR_ORANGE, 0.5f);

					Piece piece = engine.getNextObject(engine.nextPieceCount);
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
					NormalFontSDL.printFont(x2, y2, NullpoMinoSDL.getUIText("InGame_Hold"), tempColor, 0.5f);
				} else {
					if(!engine.holdDisable) {
						if((holdRemain > 0) && (holdRemain <= 10)) tempColor = COLOR_YELLOW;
						if((holdRemain > 0) && (holdRemain <= 5)) tempColor = COLOR_RED;
					}

					NormalFontSDL.printFont(x2, y2, NullpoMinoSDL.getUIText("InGame_Hold") + "\ne " + holdRemain, tempColor, 0.5f);
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
	 * @throws SDLException
	 */
	protected void drawShadowNexts(int x, int y, GameEngine engine, float scale) throws SDLException {
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

	/*
	 * 各 frame 最初の描画処理
	 */
	@Override
	public void renderFirst(GameEngine engine, int playerID) {
		try {
			// Background
			if(playerID == 0) {
				if(engine.owner.menuOnly) {
					ResourceHolderSDL.imgMenu.blitSurface(graphics);
				} else {
					int bg = engine.owner.backgroundStatus.bg;
					if(engine.owner.backgroundStatus.fadesw && !heavyeffect) {
						bg = engine.owner.backgroundStatus.fadebg;
					}

					if((ResourceHolderSDL.imgPlayBG != null) && (bg >= 0) && (bg < ResourceHolderSDL.imgPlayBG.length) && (showbg == true)) {
						ResourceHolderSDL.imgPlayBG[bg].blitSurface(graphics);

						if(engine.owner.backgroundStatus.fadesw && heavyeffect) {
							int alphalv = engine.owner.backgroundStatus.fadestat ? (100 - engine.owner.backgroundStatus.fadecount) : engine.owner.backgroundStatus.fadecount;
							ResourceHolderSDL.imgBlankBlack.setAlpha(SDLVideo.SDL_SRCALPHA | SDLVideo.SDL_RLEACCEL, alphalv * 2);
							ResourceHolderSDL.imgBlankBlack.blitSurface(graphics);
						}
					} else {
						graphics.fillRect(SDLVideo.mapRGB(graphics.getFormat(), 0, 0, 0));
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
		} catch (SDLException e) {
			log.debug("SDLException thrown", e);
		}
	}

	/*
	 * Ready画面の描画処理
	 */
	@Override
	public void renderReady(GameEngine engine, int playerID) {
		if(graphics == null) return;
		if(engine.allowTextRenderByReceiver == false) return;
		//if(!engine.isVisible) return;

		try {
			int offsetX = getFieldDisplayPositionX(engine, playerID);
			int offsetY = getFieldDisplayPositionY(engine, playerID);

			if(engine.statc[0] > 0) {
				if(engine.displaysize != -1) {
					if((engine.statc[0] >= engine.readyStart) && (engine.statc[0] < engine.readyEnd))
						NormalFontSDL.printFont(offsetX + 44, offsetY + 204, "READY", COLOR_WHITE, 1.0f);
					else if((engine.statc[0] >= engine.goStart) && (engine.statc[0] < engine.goEnd))
						NormalFontSDL.printFont(offsetX + 62, offsetY + 204, "GO!", COLOR_WHITE, 1.0f);
				} else {
					if((engine.statc[0] >= engine.readyStart) && (engine.statc[0] < engine.readyEnd))
						NormalFontSDL.printFont(offsetX + 24, offsetY + 80, "READY", COLOR_WHITE, 0.5f);
					else if((engine.statc[0] >= engine.goStart) && (engine.statc[0] < engine.goEnd))
						NormalFontSDL.printFont(offsetX + 32, offsetY + 80, "GO!", COLOR_WHITE, 0.5f);
				}
			}
		} catch (SDLException e) {
			log.debug("SDLException thrown", e);
		}
	}

	/*
	 * Blockピース移動時の処理
	 */
	@Override
	public void renderMove(GameEngine engine, int playerID) {
		try {
			if(!engine.isVisible) return;

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
		} catch (SDLException e) {
			log.debug("SDLException thrown", e);
		}
	}

	/*
	 * Blockを消す演出を出すときの処理
	 */
	@Override
	public void blockBreak(GameEngine engine, int playerID, int x, int y, Block blk) {
		if(showlineeffect && (blk != null) && engine.displaysize != -1) {
			int color = blk.getDrawColor();
			// 通常Block
			if((color >= Block.BLOCK_COLOR_GRAY) && (color <= Block.BLOCK_COLOR_PURPLE) && !blk.getAttribute(Block.BLOCK_ATTRIBUTE_BONE)) {
				EffectObject obj =
					new EffectObject(1,
										getFieldDisplayPositionX(engine, playerID) + 4 + (x * 16),
										getFieldDisplayPositionY(engine, playerID) + 52 + (y * 16),
										color);
				effectlist.add(obj);
			}
			// 宝石Block
			else if(blk.isGemBlock()) {
				EffectObject obj =
					new EffectObject(2,
										getFieldDisplayPositionX(engine, playerID) + 4 + (x * 16),
										getFieldDisplayPositionY(engine, playerID) + 52 + (y * 16),
										color);
				effectlist.add(obj);
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
		if(!engine.isVisible) return;

		try {
			int offsetX = getFieldDisplayPositionX(engine, playerID);
			int offsetY = getFieldDisplayPositionY(engine, playerID);

			if(engine.displaysize != -1) {
				if(engine.statc[1] == 0)
					NormalFontSDL.printFont(offsetX + 4, offsetY + 204, "EXCELLENT!", COLOR_ORANGE, 1.0f);
				else if(engine.owner.getPlayers() < 3)
					NormalFontSDL.printFont(offsetX + 52, offsetY + 204, "WIN!", COLOR_ORANGE, 1.0f);
				else
					NormalFontSDL.printFont(offsetX + 4, offsetY + 204, "1ST PLACE!", COLOR_ORANGE, 1.0f);
			} else {
				if(engine.statc[1] == 0)
					NormalFontSDL.printFont(offsetX + 4, offsetY + 80, "EXCELLENT!", COLOR_ORANGE, 0.5f);
				else if(engine.owner.getPlayers() < 3)
					NormalFontSDL.printFont(offsetX + 33, offsetY + 80, "WIN!", COLOR_ORANGE, 0.5f);
				else
					NormalFontSDL.printFont(offsetX + 4, offsetY + 80, "1ST PLACE!", COLOR_ORANGE, 0.5f);
			}
		} catch (SDLException e) {
			log.debug("SDLException thrown", e);
		}
	}

	/*
	 * game over画面の描画処理
	 */
	@Override
	public void renderGameOver(GameEngine engine, int playerID) {
		if(graphics == null) return;
		if(engine.allowTextRenderByReceiver == false) return;
		if(!engine.isVisible) return;

		if((engine.statc[0] >= engine.field.getHeight() + 1) && (engine.statc[0] < engine.field.getHeight() + 1 + 180))
			try {
				int offsetX = getFieldDisplayPositionX(engine, playerID);
				int offsetY = getFieldDisplayPositionY(engine, playerID);

				if(engine.displaysize != -1) {
					if(engine.owner.getPlayers() < 2)
						NormalFontSDL.printFont(offsetX + 12, offsetY + 204, "GAME OVER", COLOR_WHITE, 1.0f);
					else if(engine.owner.getWinner() == -2)
						NormalFontSDL.printFont(offsetX + 52, offsetY + 204, "DRAW", COLOR_GREEN, 1.0f);
					else if(engine.owner.getPlayers() < 3)
						NormalFontSDL.printFont(offsetX + 52, offsetY + 204, "LOSE", COLOR_WHITE, 1.0f);
				} else {
					if(engine.owner.getPlayers() < 2)
						NormalFontSDL.printFont(offsetX + 4, offsetY + 80, "GAME OVER", COLOR_WHITE, 0.5f);
					else if(engine.owner.getWinner() == -2)
						NormalFontSDL.printFont(offsetX + 28, offsetY + 80, "DRAW", COLOR_GREEN, 0.5f);
					else if(engine.owner.getPlayers() < 3)
						NormalFontSDL.printFont(offsetX + 28, offsetY + 80, "LOSE", COLOR_WHITE, 0.5f);
				}
			} catch (SDLException e) {
				log.debug("SDLException thrown", e);
			}
	}

	/*
	 * Render results screen処理
	 */
	@Override
	public void renderResult(GameEngine engine, int playerID) {
		if(graphics == null) return;
		if(engine.allowTextRenderByReceiver == false) return;
		if(!engine.isVisible) return;

		try {
			int offsetX = getFieldDisplayPositionX(engine, playerID);
			int offsetY = getFieldDisplayPositionY(engine, playerID);

			int tempColor;

			if(engine.statc[0] == 0)
				tempColor = COLOR_RED;
			else
				tempColor = COLOR_WHITE;
			NormalFontSDL.printFont(offsetX + 12, offsetY + 340, "RETRY", tempColor, 1.0f);

			if(engine.statc[0] == 1)
				tempColor = COLOR_RED;
			else
				tempColor = COLOR_WHITE;
			NormalFontSDL.printFont(offsetX + 108, offsetY + 340, "END", tempColor, 1.0f);
		} catch (SDLException e) {
			log.debug("SDLException thrown", e);
		}
	}

	/*
	 * fieldエディット画面の描画処理
	 */
	@Override
	public void renderFieldEdit(GameEngine engine, int playerID) {
		if(graphics == null) return;

		try {
			int x = getFieldDisplayPositionX(engine, playerID) + 4 + (engine.fldeditX * 16);
			int y = getFieldDisplayPositionY(engine, playerID) + 52 + (engine.fldeditY * 16);
			float bright = (engine.fldeditFrames % 60 >= 30) ? -0.5f : -0.2f;
			drawBlock(x, y, engine.fldeditColor, engine.getSkin(), false, bright, 1.0f, 1.0f);
		} catch (SDLException e) {
			log.debug("SDLException thrown", e);
		}
	}

	/*
	 * 各 frame の最後に行われる処理
	 */
	@Override
	public void onLast(GameEngine engine, int playerID) {
		if(playerID == engine.owner.getPlayers() - 1) effectUpdate();
	}

	/*
	 * 各 frame の最後に行われる描画処理
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

				int srcx = ((obj.anim-1) % 6) * 96;
				int srcy = ((obj.anim-1) / 6) * 96;
				if(obj.anim >= 30) {
					srcx = ((obj.anim-30) % 6) * 96;
					srcy = ((obj.anim-30) / 6) * 96;
				}

				SDLRect rectSrc = new SDLRect(srcx, srcy, 96, 96);
				SDLRect rectDst = new SDLRect(x, y, 96, 96);
				NullpoMinoSDL.fixRect(rectSrc, rectDst);

				try {
					if(ResourceHolderSDL.imgBreak != null) {
						if(obj.anim < 30) {
							ResourceHolderSDL.imgBreak[color][0].blitSurface(rectSrc, graphics, rectDst);
						} else {
							ResourceHolderSDL.imgBreak[color][1].blitSurface(rectSrc, graphics, rectDst);
						}
					}
				} catch (SDLException e) {
					log.debug("SDLException thrown", e);
				}
			}
			// Gem Block
			if(obj.effect == 2) {
				int x = obj.x - 8;
				int y = obj.y - 8;
				int srcx = ((obj.anim-1) % 10) * 32;
				int srcy = ((obj.anim-1) / 10) * 32;
				int color = obj.param - Block.BLOCK_COLOR_GEM_RED;

				SDLRect rectSrc = new SDLRect(srcx, srcy, 32, 32);
				SDLRect rectDst = new SDLRect(x, y, 32, 32);
				NullpoMinoSDL.fixRect(rectSrc, rectDst);

				try {
					if(ResourceHolderSDL.imgPErase != null) {
						ResourceHolderSDL.imgPErase[color].blitSurface(rectSrc, graphics, rectDst);
					}
				} catch (SDLException e) {
					log.debug("SDLException thrown", e);
				}
			}
		}
	}
}
