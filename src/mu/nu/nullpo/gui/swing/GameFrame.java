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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.RenderedImage;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JFrame;

import mu.nu.nullpo.game.net.NetObserverClient;

import org.apache.log4j.Logger;

/**
 * ゲーム画面の frame 
 */
public class GameFrame extends JFrame implements Runnable {
	/** Serial version ID */
	private static final long serialVersionUID = 1L;

	/** Log */
	static Logger log = Logger.getLogger(GameFrame.class);

	/** 親ウィンドウ */
	protected NullpoMinoSwing owner = null;

	/** タイトルバーやボーダーのサイズ */
	protected Insets insets = null;

	/** BufferStrategy */
	protected BufferStrategy bufferStrategy = null;

	/** ゲームループスレッド */
	protected Thread thread = null;

	/** trueの間スレッドが動く */
	public volatile boolean running = false;

	/** FPS計算用 */
	protected long calcInterval = 0;

	/** FPS計算用 */
	protected long prevCalcTime = 0;

	/**  frame count */
	protected long frameCount = 0;

	/** MaximumFPS (設定値) */
	public int maxfps;

	/** Current MaximumFPS */
	protected int maxfpsCurrent = 0;

	/** Current 休止 time */
	protected long periodCurrent = 0;

	/** 実際のFPS */
	public double actualFPS = 0.0;

	/** FPS表示用DecimalFormat */
	public DecimalFormat df = new DecimalFormat("0.0");

	/** ポーズ状態 */
	protected boolean pause = false;

	/** ポーズメッセージ非表示 */
	protected boolean pauseMessageHide = false;

	/** ポーズメニューのカーソル位置 */
	protected int cursor = 0;

	/** 倍速Mode  */
	protected int fastforward = 0;

	/** スクリーンショット作成 flag */
	protected boolean ssflag = false;

	/** スクリーンショット用Image */
	protected Image ssImage = null;

	/**  frame ステップ有効 flag */
	protected boolean enableframestep = false;

	/** FPS表示 */
	protected boolean showfps = true;

	/** ネットプレイならtrue */
	public boolean isNetPlay = false;

	/** Mode name to enter (null=Exit) */
	public String strModeToEnter = "";

	/**
	 * Constructor
	 * @param owner 親ウィンドウ
	 * @throws HeadlessException キーボード、マウス、ディスプレイなどが存在しない場合の例外
	 */
	public GameFrame(NullpoMinoSwing owner) throws HeadlessException {
		super();
		this.owner = owner;

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setTitle(NullpoMinoSwing.getUIText("Title_Game"));
		setBackground(Color.black);
		setResizable(false);
		setIgnoreRepaint(true);

		addWindowListener(new GameFrameWindowEvent());
		addKeyListener(new GameFrameKeyEvent());

		maxfps = NullpoMinoSwing.propConfig.getProperty("option.maxfps", 60);

		log.debug("GameFrame created");
	}

	/**
	 * ゲームウィンドウを表示
	 */
	public void displayWindow() {
		setVisible(true);
		insets = getInsets();
		int width = 640 + insets.left + insets.right;
		int height = 480 + insets.top + insets.bottom;
		setSize(width, height);

		if(!running) {
			thread = new Thread(this, "Game Thread");
			thread.start();
		}
	}

	/**
	 * 終了処理
	 */
	public void shutdown() {
		if(ssImage != null) {
			ssImage.flush();
			ssImage = null;
		}
		if(isNetPlay) {
			if(NullpoMinoSwing.netLobby != null) {
				try {
					NullpoMinoSwing.netLobby.shutdown();
				} catch (Exception e) {
					log.debug("Exception on NetLobby shutdown", e);
				}
				NullpoMinoSwing.netLobby = null;
			}
		}
		running = false;
		owner.setVisible(true);
		setVisible(false);

		// GC呼び出し
		System.gc();
	}

	/**
	 * スレッドの処理
	 */
	public void run() {
		long beforeTime, afterTime, timeDiff, sleepTime;
		long overSleepTime = 0L;
		int noDelays = 0;

		// Initialization
		maxfpsCurrent = maxfps;
		periodCurrent = (long) (1.0 / maxfpsCurrent * 1000000000);
		beforeTime = System.nanoTime();
		prevCalcTime = beforeTime;
		pause = false;
		pauseMessageHide = false;
		fastforward = 0;
		cursor = 0;
		GameKeySwing.gamekey[0].clear();
		GameKeySwing.gamekey[1].clear();

		// 設定を反映させる
		enableframestep = NullpoMinoSwing.propConfig.getProperty("option.enableframestep", false);
		showfps = NullpoMinoSwing.propConfig.getProperty("option.showfps", true);

		// オブザーバー開始
		if(!isNetPlay) NullpoMinoSwing.startObserverClient();

		// メインループ
		log.debug("Game thread start");
		running = true;
		while(running) {
			if(isNetPlay) {
				gameUpdateNet();
				gameRenderNet();
			} else if(isVisible() && isActive()) {
				gameUpdate();
				gameRender();
			}

			// 休止・FPS計算処理
			afterTime = System.nanoTime();
			timeDiff = afterTime - beforeTime;
			// 前回の frame の休止 time誤差も引いておく
			sleepTime = (periodCurrent - timeDiff) - overSleepTime;

			if(sleepTime > 0) {
				// 休止 timeがとれる場合
				if(maxfps > 0) {
					try {
						Thread.sleep(sleepTime / 1000000L);
					} catch(InterruptedException e) {
						log.debug("Game thread interrupted", e);
					}
				}
				// sleep()の誤差
				overSleepTime = (System.nanoTime() - afterTime) - sleepTime;
			} else {
				// 状態更新・レンダリングで timeを使い切ってしまい
				// 休止 timeがとれない場合
				overSleepTime = 0L;
				// 休止なしが16回以上続いたら
				if(++noDelays >= 16) {
					Thread.yield(); // 他のスレッドを強制実行
					noDelays = 0;
				}
			}

			beforeTime = System.nanoTime();

			// FPSを計算
			calcFPS(periodCurrent);
		}

		NullpoMinoSwing.gameManager.shutdown();
		NullpoMinoSwing.gameManager = null;

		if(!isNetPlay) NullpoMinoSwing.stopObserverClient();

		log.debug("Game thread end");
	}

	/**
	 * Update game state
	 */
	protected void gameUpdate() {
		if(NullpoMinoSwing.gameManager == null) return;

		GameKeySwing.gamekey[0].update();
		GameKeySwing.gamekey[1].update();

		// ポーズ button
		if(GameKeySwing.gamekey[0].isPushKey(GameKeySwing.BUTTON_PAUSE) || GameKeySwing.gamekey[1].isPushKey(GameKeySwing.BUTTON_PAUSE)) {
			if(!pause) {
				if((NullpoMinoSwing.gameManager != null) && (NullpoMinoSwing.gameManager.isGameActive())) {
					ResourceHolderSwing.soundManager.play("pause");
					pause = true;
					cursor = 0;
				}
			} else {
				ResourceHolderSwing.soundManager.play("pause");
				pause = false;
			}
		}
		// ポーズメニュー
		if(pause && !enableframestep && !pauseMessageHide) {
			if(GameKeySwing.gamekey[0].isMenuRepeatKey(GameKeySwing.BUTTON_UP)) {
				ResourceHolderSwing.soundManager.play("cursor");
				cursor--;

				if(cursor < 0) {
					if(NullpoMinoSwing.gameManager.replayMode && !NullpoMinoSwing.gameManager.replayRerecord)
						cursor = 3;
					else
						cursor = 2;
				}
			}
			if(GameKeySwing.gamekey[0].isMenuRepeatKey(GameKeySwing.BUTTON_DOWN)) {
				ResourceHolderSwing.soundManager.play("cursor");
				cursor++;
				if(cursor > 3) cursor = 0;

				if((!NullpoMinoSwing.gameManager.replayMode || NullpoMinoSwing.gameManager.replayRerecord) && (cursor > 2))
					cursor = 0;
			}
			if(GameKeySwing.gamekey[0].isPushKey(GameKeySwing.BUTTON_A)) {
				ResourceHolderSwing.soundManager.play("decide");
				if(cursor == 0) {
					// 再開
					pause = false;
				} else if(cursor == 1) {
					// リトライ
					pause = false;
					NullpoMinoSwing.gameManager.reset();
				} else if(cursor == 2) {
					// 終了
					shutdown();
					return;
				} else if(cursor == 3) {
					// リプレイ追記
					NullpoMinoSwing.gameManager.replayRerecord = true;
					cursor = 0;
				}
			}
		}
		// ポーズメニュー非表示
		pauseMessageHide = GameKeySwing.gamekey[0].isPressKey(GameKeySwing.BUTTON_C);

		if(NullpoMinoSwing.gameManager.replayMode && !NullpoMinoSwing.gameManager.replayRerecord && NullpoMinoSwing.gameManager.engine[0].gameActive) {
			// リプレイ倍速
			if(GameKeySwing.gamekey[0].isMenuRepeatKey(GameKeySwing.BUTTON_LEFT)) {
				if(fastforward > 0) {
					fastforward--;
				}
			}
			if(GameKeySwing.gamekey[0].isMenuRepeatKey(GameKeySwing.BUTTON_RIGHT)) {
				if(fastforward < 98) {
					fastforward++;
				}
			}

			// リプレイ追記
			if(GameKeySwing.gamekey[0].isPushKey(GameKeySwing.BUTTON_D)) {
				NullpoMinoSwing.gameManager.replayRerecord = true;
				cursor = 0;
			}
			// Show invisible blocks in replay
			if(GameKeySwing.gamekey[0].isPushKey(GameKeySwing.BUTTON_E)) {
				NullpoMinoSwing.gameManager.replayShowInvisible = !NullpoMinoSwing.gameManager.replayShowInvisible;
				cursor = 0;
			}
		} else {
			fastforward = 0;
		}

		// ゲームの処理を実行
		if(!pause || (GameKeySwing.gamekey[0].isPushKey(GameKeySwing.BUTTON_FRAMESTEP) && enableframestep)) {
			if(NullpoMinoSwing.gameManager != null) {
				for(int i = 0; i < Math.min(NullpoMinoSwing.gameManager.getPlayers(), 2); i++) {
					if(!NullpoMinoSwing.gameManager.replayMode || NullpoMinoSwing.gameManager.replayRerecord ||
					   !NullpoMinoSwing.gameManager.engine[i].gameActive)
					{
						GameKeySwing.gamekey[i].inputStatusUpdate(NullpoMinoSwing.gameManager.engine[i].ctrl);
					}
				}

				for(int i = 0; i <= fastforward; i++) NullpoMinoSwing.gameManager.updateAll();
			}
		}

		if(NullpoMinoSwing.gameManager != null) {
			// リトライ button
			if(GameKeySwing.gamekey[0].isPushKey(GameKeySwing.BUTTON_RETRY) || GameKeySwing.gamekey[1].isPushKey(GameKeySwing.BUTTON_RETRY)) {
				pause = false;
				NullpoMinoSwing.gameManager.reset();
			}

			// タイトルに戻る
			if(NullpoMinoSwing.gameManager.getQuitFlag() ||
			   GameKeySwing.gamekey[0].isPushKey(GameKeySwing.BUTTON_QUIT) ||
			   GameKeySwing.gamekey[1].isPushKey(GameKeySwing.BUTTON_QUIT))
			{
				shutdown();
				return;
			}
		}

		// スクリーンショット button
		if(GameKeySwing.gamekey[0].isPushKey(GameKeySwing.BUTTON_SCREENSHOT) || GameKeySwing.gamekey[1].isPushKey(GameKeySwing.BUTTON_SCREENSHOT)) {
			ssflag = true;
		}
	}

	/**
	 * Update game state(ネットプレイ用)
	 */
	protected void gameUpdateNet() {
		if(NullpoMinoSwing.gameManager == null) return;

		try {
			GameKeySwing.gamekey[0].update();

			// ゲームの処理を実行
			if(NullpoMinoSwing.gameManager != null) {
				GameKeySwing.gamekey[0].inputStatusUpdate(NullpoMinoSwing.gameManager.engine[0].ctrl);
				NullpoMinoSwing.gameManager.updateAll();

				// タイトルに戻る
				if(NullpoMinoSwing.gameManager.getQuitFlag()) {
					shutdown();
					return;
				}
			}

			// スクリーンショット button
			if(GameKeySwing.gamekey[0].isPushKey(GameKeySwing.BUTTON_SCREENSHOT) || GameKeySwing.gamekey[1].isPushKey(GameKeySwing.BUTTON_SCREENSHOT)) {
				ssflag = true;
			}

			// Enter to new mode
			if(strModeToEnter == null) {
				owner.enterNewMode(null);
				strModeToEnter = "";
			} else if (strModeToEnter.length() > 0) {
				owner.enterNewMode(strModeToEnter);
				strModeToEnter = "";
			}
		} catch (NullPointerException e) {
			log.error("update NPE", e);
		} catch (Exception e) {
			log.error("update fail", e);
		}
	}

	/**
	 * レンダリング
	 */
	protected void gameRender() {
		if(NullpoMinoSwing.gameManager == null) return;

		// 画面の準備
		if(ssImage == null) {
			ssImage = createImage(640, 480);
		}
		if((bufferStrategy == null) || bufferStrategy.contentsLost()) {
			try {
				createBufferStrategy(2);
				bufferStrategy = getBufferStrategy();
			} catch (Exception e) {
				return;
			}
		}

		Graphics g = null;
		if(ssflag) {
			g = ssImage.getGraphics();
		} else {
			g = bufferStrategy.getDrawGraphics();
			if(insets != null) g.translate(insets.left, insets.top);
		}

		// ゲーム画面
		NormalFontSwing.graphics = (Graphics2D) g;
		NullpoMinoSwing.gameManager.receiver.setGraphics(g);
		NullpoMinoSwing.gameManager.renderAll();

		// ポーズメニュー
		if(pause && !enableframestep && !pauseMessageHide) {
			int offsetX = RendererSwing.FIELD_OFFSET_X[0];
			int offsetY = RendererSwing.FIELD_OFFSET_Y[0];

			NormalFontSwing.printFont(offsetX + 12, offsetY + 188 + (cursor * 16), "b", NormalFontSwing.COLOR_RED);

			NormalFontSwing.printFont(offsetX + 28, offsetY + 188, "CONTINUE", (cursor == 0));
			NormalFontSwing.printFont(offsetX + 28, offsetY + 204, "RETRY", (cursor == 1));
			NormalFontSwing.printFont(offsetX + 28, offsetY + 220, "END", (cursor == 2));
			if(NullpoMinoSwing.gameManager.replayMode && !NullpoMinoSwing.gameManager.replayRerecord)
				NormalFontSwing.printFont(offsetX + 28, offsetY + 236, "RERECORD", (cursor == 3));
		}

		int offsetX = RendererSwing.FIELD_OFFSET_X[0];
		int offsetY = RendererSwing.FIELD_OFFSET_Y[0];
		// 早送り
		if(fastforward != 0)
			NormalFontSwing.printFont(offsetX, offsetY + 376, "e" + (fastforward + 1), NormalFontSwing.COLOR_ORANGE);
		if(NullpoMinoSwing.gameManager.replayShowInvisible)
			NormalFontSwing.printFont(offsetX, offsetY + 392, "SHOW INVIS", NormalFontSwing.COLOR_ORANGE);

		// FPS表示
		if(showfps) {
			NormalFontSwing.printFont(0, 480-16, df.format(actualFPS) + "/" + maxfpsCurrent, NormalFontSwing.COLOR_BLUE, 1.0f);
		}

		// オブザーバー情報
		NetObserverClient obClient = NullpoMinoSwing.getObserverClient();
		if((obClient != null) && obClient.isConnected()) {
			int observerCount = obClient.getObserverCount();
			int playerCount = obClient.getPlayerCount();
			int fontcolor = (playerCount > 0) ? NormalFontSwing.COLOR_RED : NormalFontSwing.COLOR_BLUE;
			String strObserverInfo = String.format("%d/%d", observerCount, playerCount);
			String strObserverString = String.format("%40s", strObserverInfo);
			NormalFontSwing.printFont(0, 480 - 16, strObserverString, fontcolor);
		}

		// 画面に表示／スクリーンショット作成
		g.dispose();
		if(ssflag) {
			saveScreenShot();

			if(insets != null) {
				Graphics g2 = getGraphics();
				g2.drawImage(ssImage, insets.left, insets.top, null);
				g2.dispose();
				Toolkit.getDefaultToolkit().sync();
			}

			ssflag = false;
		} else if((bufferStrategy != null) && !bufferStrategy.contentsLost()) {
			bufferStrategy.show();
			Toolkit.getDefaultToolkit().sync();
		}
	}

	/**
	 * レンダリング(ネットプレイ用)
	 */
	protected void gameRenderNet() {
		if(NullpoMinoSwing.gameManager == null) return;

		// 画面の準備
		if(ssImage == null) {
			ssImage = createImage(640, 480);
		}
		if((bufferStrategy == null) || bufferStrategy.contentsLost()) {
			try {
				createBufferStrategy(2);
				bufferStrategy = getBufferStrategy();
			} catch (Exception e) {
				return;
			}
		}

		Graphics g = null;
		if(ssflag) {
			g = ssImage.getGraphics();
		} else {
			g = bufferStrategy.getDrawGraphics();
			if(insets != null) g.translate(insets.left, insets.top);
		}

		// ゲーム画面
		try {
			NormalFontSwing.graphics = (Graphics2D) g;
			NullpoMinoSwing.gameManager.receiver.setGraphics(g);
			NullpoMinoSwing.gameManager.renderAll();
		} catch (NullPointerException e) {
			log.error("update NPE", e);
		} catch (Exception e) {
			log.error("render fail", e);
		}

		// FPS表示
		if(showfps) {
			NormalFontSwing.printFont(0, 480-16, df.format(actualFPS) + "/" + maxfpsCurrent, NormalFontSwing.COLOR_BLUE, 1.0f);
		}

		// 画面に表示／スクリーンショット作成
		g.dispose();
		if(ssflag) {
			saveScreenShot();

			if(insets != null) {
				Graphics g2 = getGraphics();
				g2.drawImage(ssImage, insets.left, insets.top, null);
				g2.dispose();
				Toolkit.getDefaultToolkit().sync();
			}

			ssflag = false;
		} else if((bufferStrategy != null) && !bufferStrategy.contentsLost()) {
			bufferStrategy.show();
			Toolkit.getDefaultToolkit().sync();
		}
	}

	/**
	 * FPSの計算
	 * @param period FPSを計算する間隔
	 */
	protected void calcFPS(long period) {
		frameCount++;
		calcInterval += period;

		// 1秒おきにFPSを再計算する
		if(calcInterval >= 1000000000L) {
			long timeNow = System.nanoTime();

			// 実際の経過 timeを測定
			long realElapsedTime = timeNow - prevCalcTime; // 単位: ns

			// FPSを計算
			// realElapsedTimeの単位はnsなのでsに変換する
			actualFPS = ((double) frameCount / realElapsedTime) * 1000000000L;

			frameCount = 0L;
			calcInterval = 0L;
			prevCalcTime = timeNow;

			// 新しい目標FPSを設定
			if(maxfps > 0) {
				if(actualFPS < maxfps - 1) {
					// 遅すぎ
					maxfpsCurrent++;
					if(maxfpsCurrent > maxfps + 10) maxfpsCurrent = maxfps + 10;
					periodCurrent = (long) (1.0 / maxfpsCurrent * 1000000000);
				} else if(actualFPS > maxfps + 1) {
					// 速すぎ
					maxfpsCurrent--;
					if(maxfpsCurrent < maxfps - 10) maxfpsCurrent = maxfps - 10;
					if(maxfpsCurrent < 0) maxfpsCurrent = 0;
					periodCurrent = (long) (1.0 / maxfpsCurrent * 1000000000);
				}
			}
		}
	}

	/**
	 * Save a screen shot
	 */
	protected void saveScreenShot() {
		// Create filename
		String dir = NullpoMinoSwing.propGlobal.getProperty("custom.screenshot.directory", "ss");
		GregorianCalendar currentTime = new GregorianCalendar();
		int month = currentTime.get(Calendar.MONTH) + 1;
		String filename = String.format(
				dir + "/%04d_%02d_%02d_%02d_%02d_%02d.png",
				currentTime.get(Calendar.YEAR), month, currentTime.get(Calendar.DATE), currentTime.get(Calendar.HOUR_OF_DAY),
				currentTime.get(Calendar.MINUTE), currentTime.get(Calendar.SECOND)
		);
		log.info("Saving screenshot to " + filename);

		// Create ss folder if not exist
		File ssfolder = new File(dir);
		if (!ssfolder.exists()) {
			if (ssfolder.mkdir()) {
				log.info("Created screenshot folder: " + dir);
			} else {
				log.info("Couldn't create screenshot folder at "+ dir);
			}
		}

		// Write
		try {
			javax.imageio.ImageIO.write((RenderedImage)ssImage, "PNG", new File(filename));
		} catch (Exception e) {
			log.warn("Failed to save screenshot to " + filename, e);
		}
	}

	/**
	 * ウィンドウイベントの処理
	 */
	protected class GameFrameWindowEvent extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e) {
			shutdown();
		}
	}

	/**
	 * キーボードイベントの処理
	 */
	protected class GameFrameKeyEvent extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			setButtonPressedState(e.getKeyCode(), true);
		}

		@Override
		public void keyReleased(KeyEvent e) {
			setButtonPressedState(e.getKeyCode(), false);
		}

		protected void setButtonPressedState(int keyCode, boolean pressed) {
			for(int playerID = 0; playerID < GameKeySwing.gamekey.length; playerID++) {
				for(int i = 0; i < GameKeySwing.MAX_BUTTON; i++) {
					if(keyCode == GameKeySwing.gamekey[playerID].keymap[i]) {
						//log.debug("KeyCode:" + keyCode + " pressed:" + pressed + " button:" + i);
						GameKeySwing.gamekey[playerID].setPressState(i, pressed);
					}
				}
			}
		}
	}
}
