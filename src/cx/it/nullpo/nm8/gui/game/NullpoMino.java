package cx.it.nullpo.nm8.gui.game;

import java.awt.event.KeyEvent;

import cx.it.nullpo.nm8.game.component.Block;
import cx.it.nullpo.nm8.game.component.Controller;
import cx.it.nullpo.nm8.game.component.Piece;
import cx.it.nullpo.nm8.game.play.GameManager;
import cx.it.nullpo.nm8.game.util.NUtil;
import cx.it.nullpo.nm8.gui.framework.NFColor;
import cx.it.nullpo.nm8.gui.framework.NFFont;
import cx.it.nullpo.nm8.gui.framework.NFGame;
import cx.it.nullpo.nm8.gui.framework.NFGraphics;
import cx.it.nullpo.nm8.gui.framework.NFKeyListener;
import cx.it.nullpo.nm8.gui.framework.NFKeyboard;
import cx.it.nullpo.nm8.gui.framework.NFSystem;

public class NullpoMino implements NFGame, NFKeyListener {
	private static final long serialVersionUID = 4545597070306756443L;

	NFSystem sys;
	NFGraphics g;
	NFFont font;
	GameManager manager;
	long lastdelta;


	public void init(NFSystem sys) {
		this.sys = sys;
		sys.setWindowTitle("NullpoMino8 Alpha Test - Loading");
		sys.getKeyboard().addKeyListener(this);

		try {
			if(sys.isFontSupported()) {
				font = sys.loadFont("data/res/font/font.ttf");
			}
			g = sys.getGraphics();

			manager = new GameManager();
			manager.init();
			manager.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		sys.setWindowTitle("NullpoMino8 Alpha Test");
	}

	public void render(NFSystem sys, NFGraphics g) {
		if(sys.hasFocus()) {
			try {
				this.g = g;

				g.setColor(NFColor.black);
				g.fillRect(0, 0, sys.getOriginalWidth(), sys.getOriginalHeight());

				g.setColor(NFColor.white);
				if(font != null) {
					g.setFont(font);
				}
				g.drawString("FPS:" + sys.getFPS(), 5, 10);
				String strTimer = NUtil.getTime(manager.getGamePlay(0,0).statistics.time);
				g.drawString("Time:" + strTimer, 5, 30);
				g.drawString("Delta:" + lastdelta, 5, 50);

				int fldX = 200;
				int fldY = 80;

				// Field
				for(int y = 0; y < 20; y++) {
					for(int x = 0; x < 10; x++) {
						if(!manager.getGameEngine(0).field.getBlockEmpty(x, y)) {
							Block blk = manager.getGameEngine(0).field.getBlock(x, y);
							if(blk != null) drawBlock(blk, fldX + (x * 16), fldY + (y * 16));
						}
					}
				}

				// Ghost piece
				if(manager.getGamePlay(0,0).nowPieceObject != null) {
					int ghostY = manager.getGamePlay(0,0).nowPieceObject.getBottom(
							manager.getGamePlay(0,0).nowPieceX,
							manager.getGamePlay(0,0).nowPieceY,
							manager.getGameEngine(0).field);

					drawPiece(manager.getGamePlay(0,0).nowPieceObject,
							fldX + (manager.getGamePlay(0,0).nowPieceX * 16),
							fldY + (ghostY * 16),
							true);
				}

				// Current piece
				if(manager.getGamePlay(0,0).nowPieceObject != null) {
					drawPiece(manager.getGamePlay(0,0).nowPieceObject,
							fldX + (manager.getGamePlay(0,0).nowPieceX * 16),
							fldY + (manager.getGamePlay(0,0).nowPieceY * 16));
				}

				// Hold piece
				if(manager.getGamePlay(0,0).holdPieceObject != null) {
					drawPiece(manager.getGamePlay(0,0).holdPieceObject,
							fldX - (5 * 16),
							fldY);
				}

				// Next pieces
				if(manager.getGamePlay(0,0).nextPieceArray != null) {
					for(int i = 0; i < manager.getGamePlay(0,0).nextPieceArray.length; i++) {
						drawPiece(manager.getGamePlay(0,0).nextPieceArray[i],
								fldX + 11*16,
								fldY + (i * 48));
					}
				}

				// Field box
				g.setColor(NFColor.white);
				g.drawRect(fldX, fldY, 10*16, 20*16);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void update(NFSystem sys, long delta) {
		if(sys.hasFocus()) {
			try {
				if(font != null) font.loadGlyphs();
				lastdelta = delta;
				manager.update(delta);
			} catch (Exception e) {
				System.out.println("Game update fail");
				e.printStackTrace();
			}
		}
	}

	public void onExit(NFSystem sys) {
	}

	public void onKey(NFKeyboard keyboard, int key, char c, boolean pressed) {
		if(manager != null) {
			Controller ctrl = manager.getGamePlay(0,0).ctrl;

			switch(key) {
			case KeyEvent.VK_UP:
				ctrl.setButtonState(Controller.BUTTON_HARD, pressed);
				break;
			case KeyEvent.VK_DOWN:
				ctrl.setButtonState(Controller.BUTTON_SOFT, pressed);
				break;
			case KeyEvent.VK_LEFT:
				ctrl.setButtonState(Controller.BUTTON_LEFT, pressed);
				break;
			case KeyEvent.VK_RIGHT:
				ctrl.setButtonState(Controller.BUTTON_RIGHT, pressed);
				break;
			case KeyEvent.VK_Z:
				ctrl.setButtonState(Controller.BUTTON_LROTATE, pressed);
				break;
			case KeyEvent.VK_X:
				ctrl.setButtonState(Controller.BUTTON_RROTATE, pressed);
				break;
			case KeyEvent.VK_SPACE:
				ctrl.setButtonState(Controller.BUTTON_HOLD, pressed);
				break;
			case KeyEvent.VK_D:
				ctrl.setButtonState(Controller.BUTTON_DROTATE, pressed);
				break;
			case KeyEvent.VK_ESCAPE:
				sys.exit();
				break;
			}
		}
	}

	public void keyPressed(NFKeyboard keyboard, int key, char c) {
		onKey(keyboard, key, c, true);
	}

	public void keyReleased(NFKeyboard keyboard, int key, char c) {
		onKey(keyboard, key, c, false);
	}

	public void drawBlock(Block blk, int x, int y) {
		drawBlock(blk, x, y, false);
	}

	public void drawBlock(Block blk, int x, int y, boolean isGhost) {
		switch(blk.color) {
		case Block.BLOCK_COLOR_GRAY:
			g.setColor(NFColor.gray);
			break;
		case Block.BLOCK_COLOR_RED:
			g.setColor(NFColor.red);
			break;
		case Block.BLOCK_COLOR_ORANGE:
			g.setColor(NFColor.orange);
			break;
		case Block.BLOCK_COLOR_YELLOW:
			g.setColor(NFColor.yellow);
			break;
		case Block.BLOCK_COLOR_GREEN:
			g.setColor(NFColor.green);
			break;
		case Block.BLOCK_COLOR_CYAN:
			g.setColor(NFColor.cyan);
			break;
		case Block.BLOCK_COLOR_BLUE:
			g.setColor(NFColor.blue);
			break;
		case Block.BLOCK_COLOR_PURPLE:
			g.setColor(NFColor.magenta);
			break;
		default:
			g.setColor(NFColor.white);
		}

		if(isGhost) g.drawRect(x, y, 16-1, 16-1);
		else g.fillRect(x, y, 16, 16);

		g.setColor(NFColor.white);
	}

	public void drawPiece(Piece piece, int x, int y) {
		drawPiece(piece, x, y, false);
	}

	public void drawPiece(Piece piece, int x, int y, boolean isGhost) {
		for(int i = 0; i < piece.getMaxBlock(); i++) {
			Block blk = piece.block[i];
			int x2 = piece.getDataX(i);
			int y2 = piece.getDataY(i);
			drawBlock(blk, x + (x2 * 16), y + (y2 * 16), isGhost);
		}
	}
}
