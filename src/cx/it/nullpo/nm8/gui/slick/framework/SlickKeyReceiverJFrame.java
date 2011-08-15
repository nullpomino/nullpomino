package cx.it.nullpo.nm8.gui.slick.framework;

import java.awt.HeadlessException;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

public class SlickKeyReceiverJFrame extends JFrame {
	private static final long serialVersionUID = 6291339833357420261L;

	/** Number of keys */
	protected final int MAX_KEYS = 0x10000;
	/** Key down status */
	protected boolean[] keyDown;
	/** SlickNFSystem */
	protected SlickNFSystem sys;

	/** Key event handler */
	protected class KeyEventHandler extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			setKeyDown(e.getKeyCode(), true);
			sys.getKeyboard().dispatchKeyPressed(e.getKeyCode(), e.getKeyChar());
		}
		@Override
		public void keyReleased(KeyEvent e) {
			setKeyDown(e.getKeyCode(), false);
			sys.getKeyboard().dispatchKeyReleased(e.getKeyCode(), e.getKeyChar());
		}
	}

	/** Window event handler */
	protected class WindowEventHandler extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e) {
			sys.exit();
		}
	}

	/**
	 * Set key down status
	 * @param key Keycode
	 * @param isDown true if the key is down
	 */
	public void setKeyDown(int key, boolean isDown) {
		if((key < 0) || (key >= MAX_KEYS)) return;
		keyDown[key] = isDown;
	}

	public boolean isKeyDown(int key) {
		if((key < 0) || (key >= MAX_KEYS)) return false;
		return keyDown[key];
	}

	public SlickKeyReceiverJFrame(SlickNFSystem sys) throws HeadlessException {
		super();

		keyDown = new boolean[MAX_KEYS];

		this.sys = sys;

		setTitle("KeyReceiver");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		addKeyListener(new KeyEventHandler());
		addWindowListener(new WindowEventHandler());

		setVisible(true);
	}
}
