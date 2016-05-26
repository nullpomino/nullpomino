package mu.nu.nullpo.gui.common;

import mu.nu.nullpo.game.component.Block;
import mu.nu.nullpo.game.component.Field;
import mu.nu.nullpo.game.component.Piece;
import mu.nu.nullpo.game.event.EventReceiver;
import mu.nu.nullpo.game.play.GameEngine;

public abstract class AbstractRenderer extends EventReceiver {
	
	ResourceHolder resources;
	
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
		printFontSpecific(x2, y2, str, color, scale);
	}
	
	
	
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
		printTTFFontSpecific(x2, y2, str, color);
	}
	
	public void drawScoreFont(GameEngine engine, int playerID, int x, int y, String str, int color, float scale) {
		if(engine.owner.menuOnly) return;
		int size = (scale == 0.5f) ? 8 : 16;
		printFontSpecific(getScoreDisplayPositionX(engine, playerID) + (x * size),
				getScoreDisplayPositionY(engine, playerID) + (y * size),
				str, color, scale);
	}
	
	public void drawTTFScoreFont(GameEngine engine, int playerID, int x, int y, String str, int color) {
		if(engine.owner.menuOnly) return;
		printTTFFontSpecific(getScoreDisplayPositionX(engine, playerID) + (x * 16),
				getScoreDisplayPositionY(engine, playerID) + (y * 16),
				str, color);
	}
	
	public void drawDirectFont(GameEngine engine, int playerID, int x, int y, String str, int color, float scale) {
		printFontSpecific(x, y, str, color, scale);
	}
	
	public void drawTTFDirectFont(GameEngine engine, int playerID, int x, int y, String str, int color) {
		printTTFFontSpecific(x, y, str, color);
	}

	protected void drawBlock(int x, int y, int color, int skin, boolean bone, float darkness, float alpha, 
			float scale, int attr) {
		
		if(!doesGraphicsExist()) return;

		if((color <= Block.BLOCK_COLOR_INVALID)) return;
		if(skin >= resources.getImgBlockListSize()) skin = 0;

		boolean isSpecialBlocks = (color >= Block.BLOCK_COLOR_COUNT);
		boolean isSticky = resources.getBlockIsSticky(skin);

		int size = (int)(16 * scale);
		AbstractImage img = null;
		if(scale == 0.5f) {
			img = resources.getImgSmallBlock(skin);
		} else if(scale == 2.0f) {
			img = resources.getImgBigBlock(skin);
		} else {
			img = resources.getImgNormalBlock(skin);
		}

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
		
		drawBlockSpecific(x, y, sx, sy, darkness, alpha, img);
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
		drawBlock(x, y, color, skin, bone, darkness, alpha, scale, 0);
	}

	/**
	 * Blockクラスのインスタンスを使用してBlockを描画
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param blk Blockクラスのインスタンス
	 */
	protected void drawBlock(int x, int y, Block blk) {
		drawBlock(x, y, blk.getDrawColor(), blk.skin, blk.getAttribute(Block.BLOCK_ATTRIBUTE_BONE), blk.darkness, blk.alpha, 1.0f, blk.attribute);
	}

	/**
	 * Blockクラスのインスタンスを使用してBlockを描画 (拡大率指定可能）
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param blk Blockクラスのインスタンス
	 * @param scale 拡大率
	 */
	protected void drawBlock(int x, int y, Block blk, float scale) {
		drawBlock(x, y, blk.getDrawColor(), blk.skin, blk.getAttribute(Block.BLOCK_ATTRIBUTE_BONE), blk.darkness, blk.alpha, scale, blk.attribute);
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
		drawBlock(x, y, blk.getDrawColor(), blk.skin, blk.getAttribute(Block.BLOCK_ATTRIBUTE_BONE), darkness, blk.alpha, scale, blk.attribute);
	}

	protected void drawBlockForceVisible(int x, int y, Block blk, float scale) {
		drawBlock(x, y, blk.getDrawColor(), blk.skin, blk.getAttribute(Block.BLOCK_ATTRIBUTE_BONE), blk.darkness,
				(0.5f*blk.alpha)+0.5f, scale, blk.attribute);
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
	 * @param scale 表示倍率
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
	 * fieldのBlockを描画
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param engine GameEngineのインスタンス
	 * @param small 半分サイズ
	 */
	protected void drawField(int x, int y, GameEngine engine, int size) {
		if(!doesGraphicsExist()) return;

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
		
		drawFieldSpecific(x, y, width, viewHeight, blksize, scale, outlineType);

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
	 * 現在操作中のBlockピースのghost を描画
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param engine GameEngineのインスタンス
	 * @param scale 表示倍率
	 */
	protected abstract void drawGhostPiece(int x, int y, GameEngine engine, float scale);
	
	protected abstract void drawHintPiece(int x, int y, GameEngine engine, float scale);
	
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
	
	protected abstract void printFontSpecific(int x, int y, String str, int color, float scale);
	protected abstract void printTTFFontSpecific(int x, int y, String str, int color);
	
	protected abstract boolean doesGraphicsExist();
	
	protected abstract void drawBlockSpecific(int x, int y, int sx, int sy, 
			float darkness, float alpha, AbstractImage img);
	
	protected abstract void drawOutlineSpecific(int i, int j, int x, int y, int blksize, Block blk, int outlineType);
	
	protected abstract void drawFieldSpecific(int x, int y, int width, int viewHeight, int blksize, 
			float scale, int outlineType);
}
