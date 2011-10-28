package cx.it.nullpo.nm8.gui.slick.framework;

import java.util.Iterator;

import org.newdawn.slick.Game;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.KeyListener;
import org.newdawn.slick.MouseListener;
import org.newdawn.slick.SlickException;

import cx.it.nullpo.nm8.gui.framework.NFMouseListener;

/**
 * Implementation of Slick's Game interface
 */
public class SlickNFGameWrapper implements Game, KeyListener, MouseListener {
	/** SlickNFSystem */
	protected SlickNFSystem sys;

	/** Last update time */
	protected long lastExecTime;

	/** Mouse */
	protected SlickNFMouse mouse;

	/**
	 * Constructor
	 * @param sys SlickNFSystem
	 */
	public SlickNFGameWrapper(SlickNFSystem sys) {
		this.sys = sys;
	}

	/**
	 * This is called when the user tries to close the main window.
	 * If we returns false, the window won't close.
	 */
	public boolean closeRequested() {
		sys.exit();
		return false;
	}

	public String getTitle() {
		return sys.getWindowTitle();
	}

	public void init(GameContainer container) throws SlickException {
		if(sys.getKeyboard() instanceof SlickNFKeyboard) {
			// Add key listener
			SlickNFKeyboard keyboard = (SlickNFKeyboard)sys.getKeyboard();
			keyboard.getNativeInput().addKeyListener(this);
		}
		if(sys.getMouse() instanceof SlickNFMouse) {
			// Add mouse listener
			mouse = (SlickNFMouse)sys.getMouse();
			mouse.getNativeInput().addMouseListener(this);
		}
		sys.getNFGame().init(sys);
	}

	public void render(GameContainer container, Graphics g) throws SlickException {
		sys.render();
	}

	public void update(GameContainer container, int delta) throws SlickException {
		// Poll the keyboard input
		if(sys.getKeyboard() != null) {
			sys.getKeyboard().poll();
		}

		// We don't rely on Slick's default delta because
		// LWJGL's timer is way faster than the real time
		// and we need nanoseconds for our game anyway.
		long ndelta = 0;
		long nowTime = System.nanoTime();
		if(lastExecTime == 0) {
			ndelta = 0;
		} else {
			//ndelta = (nowTime - lastExecTime) / 1000000L;
			ndelta = (nowTime - lastExecTime);
		}
		lastExecTime = nowTime;
		sys.update(ndelta);
	}

	public void keyPressed(int key, char c) {
		sys.getKeyboard().dispatchKeyPressed(key, c);
	}

	public void keyReleased(int key, char c) {
		sys.getKeyboard().dispatchKeyReleased(key, c);
	}

	public void inputEnded() {
	}

	public void inputStarted() {
	}

	public boolean isAcceptingInput() {
		return true;
	}

	public void setInput(Input input) {
	}

	public void mouseWheelMoved(int change) {
		synchronized (mouse.mouseListeners) {
			Iterator<NFMouseListener> it = mouse.mouseListeners.iterator();
			while(it.hasNext()) {
				it.next().mouseWheelMoved(mouse, -change);	// change is reversed in Slick, so...
			}
		}
	}

	public void mouseClicked(int button, int x, int y, int clickCount) {
		synchronized (mouse.mouseListeners) {
			Iterator<NFMouseListener> it = mouse.mouseListeners.iterator();
			while(it.hasNext()) {
				it.next().mouseClicked(mouse, button, x, y, clickCount);
			}
		}
	}

	public void mousePressed(int button, int x, int y) {
		synchronized (mouse.mouseListeners) {
			Iterator<NFMouseListener> it = mouse.mouseListeners.iterator();
			while(it.hasNext()) {
				it.next().mousePressed(mouse, button, x, y);
			}
		}
	}

	public void mouseReleased(int button, int x, int y) {
		synchronized (mouse.mouseListeners) {
			Iterator<NFMouseListener> it = mouse.mouseListeners.iterator();
			while(it.hasNext()) {
				it.next().mouseReleased(mouse, button, x, y);
			}
		}
	}

	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		synchronized (mouse.mouseListeners) {
			Iterator<NFMouseListener> it = mouse.mouseListeners.iterator();
			while(it.hasNext()) {
				it.next().mouseMoved(mouse, oldx, oldy, newx, newy);
			}
		}
	}

	public void mouseDragged(int oldx, int oldy, int newx, int newy) {
		synchronized (mouse.mouseListeners) {
			Iterator<NFMouseListener> it = mouse.mouseListeners.iterator();
			while(it.hasNext()) {
				it.next().mouseDragged(mouse, oldx, oldy, newx, newy);
			}
		}
	}
}
