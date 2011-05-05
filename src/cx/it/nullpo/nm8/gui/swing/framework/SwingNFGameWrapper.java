package cx.it.nullpo.nm8.gui.swing.framework;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * A JFrame that runs our game
 */
public class SwingNFGameWrapper extends JFrame implements Runnable {
	private static final long serialVersionUID = -2574561105591999416L;

	/** SwingNFSystem (owner of this frame) */
	protected SwingNFSystem sys;

	/** The size of window border */
	protected Insets insets;

	/** BufferStrategy which is important to gain enough speed */
	protected BufferStrategy bufferStrategy;

	/** Game thread */
	protected Thread thread;

	/** Shutdown requested flag */
	public volatile boolean shutdownRequested;

	/** Last update time */
	protected long lastExecTime;

	// FPS cap variables
	protected long calcInterval;
	protected long prevCalcTime;
	protected long frameCount;
	protected long periodCurrent;
	protected long perfectFPSDelay;
	public double actualFPS;

	/**
	 * Constructor
	 * @param sys SwingNFSystem
	 */
	public SwingNFGameWrapper(SwingNFSystem sys) {
		super();
		this.sys = sys;

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBackground(Color.black);
		setResizable(false);
		setIgnoreRepaint(true);
		setTitle(sys.getWindowTitle());

		addWindowListener(new WindowEventHandler());
		addKeyListener(new KeyEventHandler());
	}

	/**
	 * Start the game
	 */
	public void start() {
		setVisible(true);

		insets = getInsets();
		int width = sys.getWidth() + insets.left + insets.right;
		int height = sys.getHeight() + insets.top + insets.bottom;
		setSize(width, height);

		thread = new Thread(this, "Game Thread");
		thread.start();
	}

	/**
	 * The game thread
	 */
	public void run() {
		boolean sleepFlag;
		long beforeTime, afterTime, timeDiff, sleepTime, sleepTimeInMillis;
		long overSleepTime = 0L;
		int noDelays = 0;
		int maxfps = 0;

		beforeTime = System.nanoTime();
		prevCalcTime = beforeTime;

		sys.getNFGame().init(sys);

		while(!shutdownRequested) {
			// Update the game
			update();

			// Render
			render();

			// FPS cap
			maxfps = sys.getTargetFPS();
			periodCurrent = (long) (1.0 / maxfps * 1000000000);
			sleepFlag = false;

			afterTime = System.nanoTime();
			timeDiff = afterTime - beforeTime;

			sleepTime = (periodCurrent - timeDiff) - overSleepTime;
			sleepTimeInMillis = sleepTime / 1000000L;

			if(sleepTimeInMillis >= 4) {
				// If it is possible to use sleep
				if(maxfps > 0) {
					try {
						Thread.sleep(sleepTimeInMillis);
					} catch(InterruptedException e) {}
				}
				// sleep() oversleep
				overSleepTime = (System.nanoTime() - afterTime) - sleepTime;
				perfectFPSDelay = System.nanoTime();
				sleepFlag = true;
			} else if(sleepTime > 0) {
				// Perfect FPS
				overSleepTime = 0L;
				while(System.nanoTime() < perfectFPSDelay + 1000000000 / maxfps) {}
				perfectFPSDelay += 1000000000 / maxfps;
				sleepFlag = true;
			}

			if(!sleepFlag) {
				// Impossible to sleep!
				overSleepTime = 0L;
				if(++noDelays >= 16) {
					Thread.yield();
					noDelays = 0;
				}
				perfectFPSDelay = System.nanoTime();
			}

			beforeTime = System.nanoTime();
			calcFPS(periodCurrent);
		}

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				dispose();
			}
		});
	}

	/**
	 * Calc FPS
	 * @param period Delay between FPS calculation
	 */
	protected void calcFPS(long period) {
		frameCount++;
		calcInterval += period;

		if(calcInterval >= 1000000000L) {
			long timeNow = System.nanoTime();

			long realElapsedTime = timeNow - prevCalcTime;

			actualFPS = ((double) frameCount / realElapsedTime) * 1000000000L;

			frameCount = 0L;
			calcInterval = 0L;
			prevCalcTime = timeNow;
		}
	}

	/**
	 * Update the game
	 */
	public void update() {
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

	/**
	 * Render the game
	 */
	public void render() {
		if((bufferStrategy == null) || bufferStrategy.contentsLost()) {
			try {
				createBufferStrategy(2);
				bufferStrategy = getBufferStrategy();
			} catch (Exception e) {
				return;
			}
		}

		Graphics g = null;
		g = bufferStrategy.getDrawGraphics();

		if(g != null) {
			if(insets != null) g.translate(insets.left, insets.top);

			if(sys.g == null) {
				sys.g = new SwingNFGraphics(g);
			} else {
				sys.g.setNativeGraphics(g);
			}
			sys.getNFGame().render(sys, sys.getGraphics());

			if((bufferStrategy != null) && !bufferStrategy.contentsLost()) {
				bufferStrategy.show();
			}

			g.dispose();
		}
	}

	/**
	 * Called when the window is closing
	 */
	protected class WindowEventHandler extends WindowAdapter {
		@Override
		public void windowClosing(java.awt.event.WindowEvent e) {
			sys.exit();
		}
	}

	/**
	 * Called when a key is pressed/released
	 */
	protected class KeyEventHandler extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			SwingNFKeyboard k = (SwingNFKeyboard)sys.getKeyboard();
			k.setKeyDown(e.getKeyCode(), true);
			k.dispatchKeyPressed(e.getKeyCode(), e.getKeyChar());
		}
		@Override
		public void keyReleased(KeyEvent e) {
			SwingNFKeyboard k = (SwingNFKeyboard)sys.getKeyboard();
			k.setKeyDown(e.getKeyCode(), false);
			k.dispatchKeyReleased(e.getKeyCode(), e.getKeyChar());
		}
	}
}
