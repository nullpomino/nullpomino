package cx.it.nullpo.nm8.gui.game;

import cx.it.nullpo.nm8.game.component.Block;
import cx.it.nullpo.nm8.game.component.Field;
import cx.it.nullpo.nm8.game.component.Piece;
import cx.it.nullpo.nm8.game.play.GameEngine;
import cx.it.nullpo.nm8.game.play.GamePlay;
import cx.it.nullpo.nm8.gui.framework.NFColor;
import cx.it.nullpo.nm8.gui.framework.NFGraphics;
import cx.it.nullpo.nm8.gui.framework.NFImage;

/**
 * This class will draw the game screen
 */
public class GameFieldRenderer {
	/**
	 * Draw the game field
	 * @param fld Field
	 * @param g NFGraphics
	 * @param x X
	 * @param y Y
	 * @param width Width
	 * @param height Height
	 * @param blksize Block Size
	 */
	public static void drawField(Field fld, NFGraphics g, int x, int y, int width, int height, int blksize) {
		g.setClip(x, y, width, height);
		g.translate(x, y);

		int yOffset = height % blksize;
		int fldHeight = fld.getHeight();
		int fldHighestBlockY = fld.getHighestBlockY();
		int fldWidth = fld.getWidth();

		for(int i = fldHeight - 1; i >= fldHighestBlockY; i--) {
			if(i * blksize > -blksize - yOffset) {
				for(int j = 0; j < fldWidth; j++) {
					Block blk = fld.getBlock(j, i);
					drawBlock(blk, g, j * blksize, yOffset + (i * blksize), blksize);
				}
			} else {
				break;
			}
		}

		g.translate(-x, -y);
		g.clearClip();
	}

	/**
	 * Draw the game field
	 * @param engine GameEngine
	 * @param g NFGraphics
	 * @param x X
	 * @param y Y
	 * @param width Width
	 * @param height Height
	 * @param blksize Block Size
	 */
	public static void drawField(GameEngine engine, NFGraphics g, int x, int y, int width, int height, int blksize) {
		drawField(engine.field, g, x, y, width, height, blksize);
	}

	/**
	 * Draw the game field
	 * @param gamePlay GamePlay
	 * @param g NFGraphics
	 * @param x X
	 * @param y Y
	 * @param width Width
	 * @param height Height
	 * @param blksize Block Size
	 */
	public static void drawField(GamePlay gamePlay, NFGraphics g, int x, int y, int width, int height, int blksize) {
		drawField(gamePlay.engine, g, x, y, width, height, blksize);
	}

	/**
	 * Draw the current piece
	 * @param gamePlay GamePlay
	 * @param g NFGraphics
	 * @param x X
	 * @param y Y
	 * @param width Width
	 * @param height Height
	 * @param blksize Block Size
	 */
	public static void drawCurrentPiece(GamePlay gamePlay, NFGraphics g, int x, int y, int width, int height, int blksize) {
		g.setClip(x, y, width, height);
		g.translate(x, y);

		int yOffset = height % blksize;

		Piece piece = gamePlay.nowPieceObject;
		if(piece != null) {
			int pieceX = gamePlay.nowPieceX * blksize;
			int pieceY = gamePlay.nowPieceY * blksize;
			drawPiece(piece, g, pieceX, yOffset + pieceY, blksize);
		}

		g.translate(-x, -y);
		g.clearClip();
	}

	/**
	 * Draw the current piece
	 * @param engine GameEngine
	 * @param g NFGraphics
	 * @param x X
	 * @param y Y
	 * @param width Width
	 * @param height Height
	 * @param blksize Block Size
	 */
	public static void drawCurrentPiece(GameEngine engine, NFGraphics g, int x, int y, int width, int height, int blksize) {
		for(int player = 0; player < engine.owner.getNumberOfPlayersForEachEngine(); player++) {
			GamePlay play = engine.getGamePlay(player);
			drawCurrentPiece(play, g, x, y, width, height, blksize);
		}
	}

	public static void drawGhostPiece(GamePlay gamePlay, NFGraphics g, int x, int y, int width, int height, int blksize) {
		g.setClip(x, y, width, height);
		g.translate(x, y);

		int yOffset = height % blksize;

		Piece piece = gamePlay.nowPieceObject;
		if(piece != null) {
			piece = new Piece(piece);
			piece.setAlpha(0.5f);
			int pieceX = gamePlay.nowPieceX * blksize;
			int bottomY = piece.getBottom(gamePlay.nowPieceX, gamePlay.nowPieceY, gamePlay.engine.field);
			int pieceY = bottomY * blksize;
			drawPiece(piece, g, pieceX, yOffset + pieceY, blksize);
		}

		g.translate(-x, -y);
		g.clearClip();
	}

	public static void drawGhostPiece(GameEngine engine, NFGraphics g, int x, int y, int width, int height, int blksize) {
		for(int player = 0; player < engine.owner.getNumberOfPlayersForEachEngine(); player++) {
			GamePlay play = engine.getGamePlay(player);
			drawGhostPiece(play, g, x, y, width, height, blksize);
		}
	}

	/**
	 * Draw a piece to somewhere on the screen
	 * @param piece Piece
	 * @param direction Direction
	 * @param g NFGraphics
	 * @param x X
	 * @param y Y
	 * @param blksize Block Size
	 */
	public static void drawPiece(Piece piece, int direction, NFGraphics g, int x, int y, int blksize) {
		for(int i = 0; i < piece.getMaxBlock(); i++) {
			int x2 = x + (piece.getDataX(i, direction) * blksize);
			int y2 = y + (piece.getDataY(i, direction) * blksize);
			Block blk = piece.block[i];
			drawBlock(blk, g, x2, y2, blksize);
		}
	}

	/**
	 * Draw a piece to somewhere on the screen
	 * @param piece Piece
	 * @param g NFGraphics
	 * @param x X
	 * @param y Y
	 * @param blksize Block Size
	 */
	public static void drawPiece(Piece piece, NFGraphics g, int x, int y, int blksize) {
		drawPiece(piece, piece.direction, g, x, y, blksize);
	}

	public static void drawPieceCenter(Piece piece, int direction, NFGraphics g, int x, int y, int blksize,
										 int boxWidth, int boxHeight)
	{
		int pWidth = piece.getWidth() + 1;
		int pHeight = piece.getHeight() + 1;
		int x2 = ((boxWidth - (pWidth * blksize)) / 2) - (piece.getMinimumBlockX() * blksize);
		int y2 = ((boxHeight - (pHeight * blksize)) / 2) - (piece.getMinimumBlockY() * blksize);
		drawPiece(piece, direction, g, x+x2, y+y2, blksize);
	}

	public static void drawPieceCenter(Piece piece, NFGraphics g, int x, int y, int blksize,
										int boxWidth, int boxHeight)
	{
		drawPieceCenter(piece, piece.direction, g, x, y, blksize, boxWidth, boxHeight);
	}

	/**
	 * Draw a Block to somewhere on the screen
	 * @param blk Block
	 * @param g NFGraphics
	 * @param x X
	 * @param y Y
	 * @param blksize Block Size
	 */
	public static void drawBlock(Block blk, NFGraphics g, int x, int y, int blksize) {
		if(blk == null) return;
		if(blk.isEmpty()) return;

		if(ResourceHolder.blockSkin != null) {
			// TODO: Add support of multiple block skin
			int obs = 16;
			NFImage img = ResourceHolder.blockSkin.mapImageNormal.get(obs);

			if(img != null) {
				NFColor col = new NFColor(255,255,255,(int)(blk.alpha * 255));
				g.drawImage(img, x, y, x+blksize, y+blksize, blk.color * obs, 0, (blk.color * obs) + obs, obs, col);
			}
		} else {
			g.setColor(NFColor.green);
			g.fillRect(x, y, blksize, blksize);
		}
	}
}
