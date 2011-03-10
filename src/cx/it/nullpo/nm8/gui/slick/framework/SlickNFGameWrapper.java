package cx.it.nullpo.nm8.gui.slick.framework;

import org.newdawn.slick.Game;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.KeyListener;
import org.newdawn.slick.SlickException;

/**
 * Implementation of Slick's Game interface
 */
public class SlickNFGameWrapper implements Game, KeyListener {
	/** SlickNFSystem */
	protected SlickNFSystem sys;

	/** Last update time */
	protected long lastExecTime;

	/**
	 * Constructor
	 * @param sys SlickNFSystem
	 */
	public SlickNFGameWrapper(SlickNFSystem sys) {
		this.sys = sys;
	}

	public boolean closeRequested() {
		return true;
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
		sys.getNFGame().init(sys);
	}

	public void render(GameContainer container, Graphics g) throws SlickException {
		sys.getNFGame().render(sys, sys.getGraphics());
	}

	public void update(GameContainer container, int delta) throws SlickException {
		// We don't rely on Slick's default delta because
		// LWJGL's timer is way faster than the real time
		long ndelta = 0;
		long nowTime = System.nanoTime();
		if(lastExecTime == 0) {
			ndelta = 0;
		} else {
			ndelta = (nowTime - lastExecTime) / 1000000L;
		}
		lastExecTime = nowTime;
		sys.getNFGame().update(sys, ndelta);
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
}
