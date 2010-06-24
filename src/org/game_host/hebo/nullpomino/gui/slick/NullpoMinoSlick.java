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
package org.game_host.hebo.nullpomino.gui.slick;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.game_host.hebo.nullpomino.game.net.NetObserverClient;
import org.game_host.hebo.nullpomino.util.CustomProperties;
import org.game_host.hebo.nullpomino.util.ModeManager;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;

/**
 * NullpoMino Slickバージョン
 */
public class NullpoMinoSlick extends StateBasedGame {
	/** ログ */
	static Logger log = Logger.getLogger(NullpoMinoSlick.class);

	/** プログラムに渡されたコマンドライン引数 */
	public static String[] programArgs;

	/** 設定保存用プロパティファイル */
	public static CustomProperties propConfig;

	/** 設定保存用プロパティファイル（全バージョン共通） */
	public static CustomProperties propGlobal;

	/** 音楽リストプロパティファイル */
	public static CustomProperties propMusic;

	/** オブザーバー機能用プロパティファイル */
	public static CustomProperties propObserver;

	/** 言語ファイル */
	public static CustomProperties propLang;

	/** スクリーンショット用 */
	public static BufferedImage ssImage;

	/** モード管理 */
	public static ModeManager modeManager;

	/** AppGameContainer */
	public static AppGameContainer appGameContainer;

	/** ロード画面のステート */
	public static StateLoading stateLoading;

	/** タイトル画面のステート */
	public static StateTitle stateTitle;

	/** ゲーム画面のステート */
	public static StateInGame stateInGame;

	/** モード選択画面のステート */
	public static StateSelectMode stateSelectMode;

	/** リプレイ選択画面のステート */
	public static StateReplaySelect stateReplaySelect;

	/** 設定画面のステート */
	public static StateConfigMainMenu stateConfigMainMenu;

	/** 全般の設定画面のステート */
	public static StateConfigGeneral stateConfigGeneral;

	/** ルール選択画面のステート */
	public static StateConfigRuleSelect stateConfigRuleSelect;

	/** AI選択画面のステート */
	public static StateConfigAISelect stateConfigAISelect;

	/** キーボード設定画面のステート */
	public static StateConfigKeyboard stateConfigKeyboard;

	/** ジョイスティックボタン設定画面のステート */
	public static StateConfigJoystickButton stateConfigJoystickButton;

	/** ネットプレイ画面のステート */
	public static StateNetGame stateNetGame;

	/** ジョイスティック設定メインメニューのステート */
	public static StateConfigJoystickMain stateConfigJoystickMain;

	/** ジョイスティックテスト画面のステート */
	public static StateConfigJoystickTest stateConfigJoystickTest;

	/** チューニング設定画面のステート */
	public static StateConfigGameTuning stateConfigGameTuning;

	/** 独自のFPS維持法を使う */
	public static boolean useAlternateFPSSleep;

	/** 独自のFPS維持法での目標FPS */
	public static int alternateTargetFPS;

	/** FPS維持用 */
	protected static long beforeTime;

	/** FPS維持用 */
	protected static long overSleepTime;

	/** FPS維持用 */
	protected static int noDelays;

	/** FPS計算用 */
	protected static long calcInterval = 0;

	/** FPS計算用 */
	protected static long prevCalcTime = 0;

	/** フレーム数 */
	protected static long frameCount = 0;

	/** 実際のFPS */
	public static double actualFPS = 0.0;

	/** FPS表示用DecimalFormat */
	public static DecimalFormat df = new DecimalFormat("0.0");

	/** オブザーバークライアント */
	public static NetObserverClient netObserverClient;

	/**
	 * メイン関数
	 * @param args プログラムに渡されたコマンドライン引数
	 */
	public static void main(String[] args) {
		programArgs = args;

		PropertyConfigurator.configure("config/etc/log_slick.cfg");
		Log.setLogSystem(new LogSystemLog4j());
		log.info("NullpoMinoSlick Start");

		propConfig = new CustomProperties();
		propGlobal = new CustomProperties();
		propMusic = new CustomProperties();

		// 設定ファイル読み込み
		try {
			FileInputStream in = new FileInputStream("config/setting/slick.cfg");
			propConfig.load(in);
			in.close();
		} catch(IOException e) {}
		try {
			FileInputStream in = new FileInputStream("config/setting/global.cfg");
			propGlobal.load(in);
			in.close();
		} catch(IOException e) {}
		try {
			FileInputStream in = new FileInputStream("config/setting/music.cfg");
			propMusic.load(in);
			in.close();
		} catch (IOException e) {}

		// 言語ファイル読み込み
		propLang = new CustomProperties();
		try {
			FileInputStream in = new FileInputStream("config/lang/slick_" + Locale.getDefault().getCountry() + ".properties");
			propLang.load(in);
			in.close();
		} catch(IOException e) {
			try {
				FileInputStream in = new FileInputStream("config/lang/slick_default.properties");
				propLang.load(in);
				in.close();
			} catch(IOException e2) {
				log.error("Couldn't load default UI language file", e2);
			}
		}

		// モード読み込み
		modeManager = new ModeManager();
		try {
			BufferedReader txtMode = new BufferedReader(new FileReader("config/list/mode.lst"));
			modeManager.loadGameModes(txtMode);
			txtMode.close();
		} catch (IOException e) {
			log.error("Mode list load failed", e);
		}

		// ゲーム画面などの初期化
		try {
			NullpoMinoSlick obj = new NullpoMinoSlick();

			appGameContainer = new AppGameContainer(obj);
			appGameContainer.setShowFPS(false);
			appGameContainer.setClearEachFrame(false);
			appGameContainer.setMinimumLogicUpdateInterval(0);
			appGameContainer.setMaximumLogicUpdateInterval(0);
			appGameContainer.setUpdateOnlyWhenVisible(false);
			appGameContainer.setForceExit(false);
			appGameContainer.setDisplayMode(640, 480, propConfig.getProperty("option.fullscreen", false));
			appGameContainer.start();
		} catch (Throwable e) {
			log.error("Game initialize failed", e);
		}

		stopObserverClient();

		if(stateNetGame.netLobby != null) {
			log.debug("Calling netLobby shutdown routine");
			stateNetGame.netLobby.shutdown();
		}

		System.exit(0);
	}

	/**
	 * 設定ファイルを保存
	 */
	public static void saveConfig() {
		try {
			FileOutputStream out = new FileOutputStream("config/setting/slick.cfg");
			propConfig.store(out, "NullpoMino Slick-frontend Config");
			out.close();
		} catch(IOException e) {
			log.error("Failed to save Slick-specific config", e);
		}

		try {
			FileOutputStream out = new FileOutputStream("config/setting/global.cfg");
			propGlobal.store(out, "NullpoMino Global Config");
			out.close();
		} catch(IOException e) {
			log.error("Failed to save global config", e);
		}
	}

	/**
	 * いろいろな設定を反映させる
	 */
	public static void setGeneralConfig() {
		useAlternateFPSSleep = propConfig.getProperty("option.useAlternateFPSSleep", false);

		if(useAlternateFPSSleep == true) {
			appGameContainer.setTargetFrameRate(-1);
			beforeTime = System.nanoTime();
			overSleepTime = 0L;
			noDelays = 0;
			alternateTargetFPS = propConfig.getProperty("option.maxfps", 60);
		} else {
			int maxfps = propConfig.getProperty("option.maxfps", 60);
			if(maxfps == 0) maxfps = -1;
			appGameContainer.setTargetFrameRate(maxfps);
		}

		appGameContainer.setVSync(propConfig.getProperty("option.vsync", false));
		appGameContainer.setSmoothDeltas(propConfig.getProperty("option.smoothdeltas", false));

		int sevolume = propConfig.getProperty("option.sevolume", 128);
		appGameContainer.setSoundVolume(sevolume / (float)128);

		ControllerManager.method = propConfig.getProperty("option.joymethod", ControllerManager.CONTROLLER_METHOD_SLICK_DEFAULT);
		ControllerManager.controllerID[0] = propConfig.getProperty("joyUseNumber.p0", -1);
		ControllerManager.controllerID[1] = propConfig.getProperty("joyUseNumber.p1", -1);
		int joyBorder = propConfig.getProperty("joyBorder.p0", 0);
		ControllerManager.border[0] = joyBorder / (float)32768;
		joyBorder = propConfig.getProperty("joyBorder.p1", 0);
		ControllerManager.border[1] = joyBorder / (float)32768;
		ControllerManager.ignoreAxis[0] = propConfig.getProperty("joyIgnoreAxis.p0", false);
		ControllerManager.ignoreAxis[1] = propConfig.getProperty("joyIgnoreAxis.p1", false);
		ControllerManager.ignorePOV[0] = propConfig.getProperty("joyIgnorePOV.p0", false);
		ControllerManager.ignorePOV[1] = propConfig.getProperty("joyIgnorePOV.p1", false);
	}

	/**
	 * スクリーンショット保存
	 * @param container GameContainer
	 * @param g Graphics
	 */
	public static void saveScreenShot(GameContainer container, Graphics g) {
		// ファイル名を決める
		String dir = propGlobal.getProperty("custom.screenshot.directory", "ss");
		GregorianCalendar currentTime = new GregorianCalendar();
		int month = currentTime.get(Calendar.MONTH) + 1;
		String filename = String.format(
				dir + "/%04d_%02d_%02d_%02d_%02d_%02d.png",
				currentTime.get(Calendar.YEAR), month, currentTime.get(Calendar.DATE), currentTime.get(Calendar.HOUR_OF_DAY),
				currentTime.get(Calendar.MINUTE), currentTime.get(Calendar.SECOND)
		);
		log.info("Saving screenshot to " + filename);

		// スクリーンショット作成
		try {
			int screenWidth = container.getWidth();
			int screenHeight = container.getHeight();

			Image screenImage = new Image(screenWidth, screenHeight);
			g.copyArea(screenImage, 0, 0);

			// 以下の方法だと上下さかさま
			//ImageOut.write(screenImage, filename);

			// なので自前で画面をコピーする
			if(ssImage == null) {
				ssImage = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_RGB);
			}

			for(int i = 0; i < screenWidth; i++)
				for(int j = 0; j < screenHeight; j++) {
					Color color = screenImage.getColor(i, j + 1);	// どうもY座標は+1しないとズレるらしい

					int rgb =
						((color.getRed()   & 0x000000FF) << 16) |
						((color.getGreen() & 0x000000FF) <<  8) |
						((color.getBlue()  & 0x000000FF) <<  0);

					ssImage.setRGB(i, j, rgb);
				}

			// ファイルに保存
			javax.imageio.ImageIO.write(ssImage, "png", new File(filename));
		} catch (Throwable e) {
			log.error("Failed to create screen shot", e);
		}
	}

	/**
	 * 翻訳後のUIの文字列を取得
	 * @param str 文字列
	 * @return 翻訳後のUIの文字列（無いならそのままstrを返す）
	 */
	public static String getUIText(String str) {
		return propLang.getProperty(str, str);
	}

	/**
	 * Slick標準とは違うFPS維持
	 */
	public static void alternateFPSSleep() {
		if(!useAlternateFPSSleep) return;

		int maxfps = alternateTargetFPS;
		if(maxfps <= 0) return;
		long period = (long) (1.0 / maxfps * 1000000000);

		long afterTime, timeDiff, sleepTime;

		// 休止・FPS計算処理
		afterTime = System.nanoTime();
		timeDiff = afterTime - beforeTime;
		// 前回のフレームの休止時間誤差も引いておく
		sleepTime = (period - timeDiff) - overSleepTime;

		if(sleepTime > 0) {
			// 休止時間がとれる場合
			if(maxfps > 0) {
				try {
					Thread.sleep(sleepTime / 1000000L);
				} catch(InterruptedException e) {}
				//appGameContainer.sleep((int) (sleepTime / 1000000L));
			}
			// sleep()の誤差
			overSleepTime = (System.nanoTime() - afterTime) - sleepTime;
		} else {
			// 状態更新・レンダリングで時間を使い切ってしまい
			// 休止時間がとれない場合
			overSleepTime = 0L;
			// 休止なしが16回以上続いたら
			if(++noDelays >= 16) {
				Thread.yield(); // 他のスレッドを強制実行
				noDelays = 0;
			}
		}

		beforeTime = System.nanoTime();

		calcFPS(period);
	}

	/**
	 * FPSの計算
	 * @param period FPSを計算する間隔
	 */
	protected static void calcFPS(long period) {
		frameCount++;
		calcInterval += period;

		// 1秒おきにFPSを再計算する
		if(calcInterval >= 1000000000L) {
			long timeNow = System.nanoTime();

			// 実際の経過時間を測定
			long realElapsedTime = timeNow - prevCalcTime; // 単位: ns

			// FPSを計算
			// realElapsedTimeの単位はnsなのでsに変換する
			actualFPS = ((double) frameCount / realElapsedTime) * 1000000000L;

			frameCount = 0L;
			calcInterval = 0L;
			prevCalcTime = timeNow;
		}
	}

	/**
	 * コンストラクタ
	 */
	public NullpoMinoSlick() {
		super("NullpoMino (Now Loading...)");
	}

	/*
	 * ステート（タイトルとかゲームとかのシーンのことね）を追加
	 */
	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		stateLoading = new StateLoading();
		stateTitle = new StateTitle();
		stateInGame = new StateInGame();
		stateSelectMode = new StateSelectMode();
		stateReplaySelect = new StateReplaySelect();
		stateConfigMainMenu = new StateConfigMainMenu();
		stateConfigGeneral = new StateConfigGeneral();
		stateConfigRuleSelect = new StateConfigRuleSelect();
		stateConfigAISelect = new StateConfigAISelect();
		stateConfigKeyboard = new StateConfigKeyboard();
		stateConfigJoystickButton = new StateConfigJoystickButton();
		stateNetGame = new StateNetGame();
		stateConfigJoystickMain = new StateConfigJoystickMain();
		stateConfigJoystickTest = new StateConfigJoystickTest();
		stateConfigGameTuning = new StateConfigGameTuning();

		addState(stateLoading);
		addState(stateTitle);
		addState(stateInGame);
		addState(stateSelectMode);
		addState(stateReplaySelect);
		addState(stateConfigMainMenu);
		addState(stateConfigGeneral);
		addState(stateConfigRuleSelect);
		addState(stateConfigAISelect);
		addState(stateConfigKeyboard);
		addState(stateConfigJoystickButton);
		addState(stateNetGame);
		addState(stateConfigJoystickMain);
		addState(stateConfigJoystickTest);
		addState(stateConfigGameTuning);
	}

	/**
	 * FPS描画
	 * @param container GameContainer
	 */
	public static void drawFPS(GameContainer container) {
		if(propConfig.getProperty("option.showfps", true) == true) {
			if(useAlternateFPSSleep)
				NormalFont.printFont(0, 480 - 16, df.format(actualFPS), NormalFont.COLOR_BLUE);
			else
				NormalFont.printFont(0, 480 - 16, String.valueOf(container.getFPS()), NormalFont.COLOR_BLUE);
		}
	}

	/**
	 * オブザーバークライアントを開始
	 */
	public static void startObserverClient() {
		log.debug("startObserverClient called");

		if(propObserver == null) {
			propObserver = new CustomProperties();
			try {
				FileInputStream in = new FileInputStream("config/setting/netobserver.cfg");
				propObserver.load(in);
				in.close();
			} catch (IOException e) {}
		}

		if(propObserver.getProperty("observer.enable", false) == false) return;
		if((netObserverClient != null) && netObserverClient.isConnected()) return;

		String host = propObserver.getProperty("observer.host", "");
		int port = propObserver.getProperty("observer.port", NetObserverClient.DEFAULT_PORT);

		if((host.length() > 0) && (port > 0)) {
			netObserverClient = new NetObserverClient(host, port);
			netObserverClient.start();
		}
	}

	/**
	 * オブザーバークライアントを停止
	 */
	public static void stopObserverClient() {
		log.debug("stopObserverClient called");

		if(netObserverClient != null) {
			if(netObserverClient.isConnected()) {
				netObserverClient.send("disconnect\n");
			}
			netObserverClient.threadRunning = false;
			netObserverClient.connectedFlag = false;
			netObserverClient = null;
		}
		propObserver = null;
	}

	/**
	 * オブザーバークライアントからの情報を描画
	 */
	public static void drawObserverClient() {
		if((netObserverClient != null) && netObserverClient.isConnected()) {
			int fontcolor = NormalFont.COLOR_BLUE;
			if(netObserverClient.getObserverCount() > 1) fontcolor = NormalFont.COLOR_GREEN;
			if(netObserverClient.getObserverCount() > 0 && netObserverClient.getPlayerCount() > 0) fontcolor = NormalFont.COLOR_RED;
			String strObserverInfo = String.format("%d/%d", netObserverClient.getObserverCount(), netObserverClient.getPlayerCount());
			String strObserverString = String.format("%40s", strObserverInfo);
			NormalFont.printFont(0, 480 - 16, strObserverString, fontcolor);
		}
	}
}
