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
package mu.nu.nullpo.gui.slick;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import mu.nu.nullpo.game.net.NetObserverClient;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.util.CustomProperties;
import mu.nu.nullpo.util.ModeManager;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.ScalableGame;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;

/**
 * NullpoMino SlickVersion
 */
public class NullpoMinoSlick extends StateBasedGame {
	/** Log */
	static Logger log = Logger.getLogger(NullpoMinoSlick.class);

	/** プログラムに渡されたコマンドLines引count */
	public static String[] programArgs;

	/** Save settings用Property file */
	public static CustomProperties propConfig;

	/** Save settings用Property file (全Version共通) */
	public static CustomProperties propGlobal;

	/** 音楽リストProperty file */
	public static CustomProperties propMusic;

	/** Observer機能用Property file */
	public static CustomProperties propObserver;

	/** Default language file */
	public static CustomProperties propLangDefault;

	/** 言語ファイル */
	public static CustomProperties propLang;

	/** Default game mode description file */
	public static CustomProperties propDefaultModeDesc;

	/** Game mode description file */
	public static CustomProperties propModeDesc;

	/** Screenshot用 */
	public static BufferedImage ssImage;

	/** Mode 管理 */
	public static ModeManager modeManager;

	/** AppGameContainer */
	public static AppGameContainer appGameContainer;

	/** ロード画面のステート */
	public static StateLoading stateLoading;

	/** タイトル画面のステート */
	public static StateTitle stateTitle;

	/** ゲーム画面のステート */
	public static StateInGame stateInGame;

	/** Mode 選択画面のステート */
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

	/** Joystick button設定画面のステート */
	public static StateConfigJoystickButton stateConfigJoystickButton;

	/** ネットプレイ画面のステート */
	public static StateNetGame stateNetGame;

	/** Joystick 設定メインMenu のステート */
	public static StateConfigJoystickMain stateConfigJoystickMain;

	/** Joystick テスト画面のステート */
	public static StateConfigJoystickTest stateConfigJoystickTest;

	/** チューニング設定画面のステート */
	public static StateConfigGameTuning stateConfigGameTuning;

	/** Style select state */
	public static StateConfigRuleStyleSelect stateConfigRuleStyleSelect;

	/** Keyboard menu navigation settings state */
	public static StateConfigKeyboardNavi stateConfigKeyboardNavi;

	/** Keyboard Reset menu state */
	public static StateConfigKeyboardReset stateConfigKeyboardReset;

	/** Rule select (after mode selection) */
	public static StateSelectRuleFromList stateSelectRuleFromList;

	/** Mode folder select */
	public static StateSelectModeFolder stateSelectModeFolder;

	/** Timing of alternate FPS sleep (false=render true=update) */
	public static boolean alternateFPSTiming;

	/** Allow dynamic adjust of target FPS (as seen in Swing version) */
	public static boolean alternateFPSDynamicAdjust;

	/** Perfect FPS mode (more accurate, eats more CPU) */
	public static boolean alternateFPSPerfectMode;

	/** Execute Thread.yield() during Perfect FPS mode */
	public static boolean alternateFPSPerfectYield;

	/** Target FPS */
	public static int altMaxFPS;

	/** Current max FPS */
	public static int altMaxFPSCurrent;

	/** Used for FPS calculation */
	protected static long periodCurrent;

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

	/**  frame count */
	protected static long frameCount = 0;

	/** 実際のFPS */
	public static double actualFPS = 0.0;

	/** FPS表示用DecimalFormat */
	public static DecimalFormat df = new DecimalFormat("0.0");

	/** Used by perfect fps mode */
	public static long perfectFPSDelay = 0;

	/** Observerクライアント */
	public static NetObserverClient netObserverClient;

	/** true if read keyboard input from JInput */
	public static boolean useJInputKeyboard;

	/**
	 * メイン関count
	 * @param args プログラムに渡されたコマンドLines引count
	 */
	public static void main(String[] args) {
		programArgs = args;

		PropertyConfigurator.configure("config/etc/log_slick.cfg");
		Log.setLogSystem(new LogSystemLog4j());
		log.info("NullpoMinoSlick Start");

		try {
			log.info("Driver adapter:" + Display.getAdapter() + ", Driver version:" + Display.getVersion());
		} catch (Throwable e) {
			log.warn("Cannot get driver informations", e);
		}

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
		propLangDefault = new CustomProperties();
		try {
			FileInputStream in = new FileInputStream("config/lang/slick_default.properties");
			propLangDefault.load(in);
			in.close();
		} catch(IOException e) {
			log.error("Couldn't load default UI language file", e);
		}

		propLang = new CustomProperties();
		try {
			FileInputStream in = new FileInputStream("config/lang/slick_" + Locale.getDefault().getCountry() + ".properties");
			propLang.load(in);
			in.close();
		} catch(IOException e) {}

		// Game mode description
		propDefaultModeDesc = new CustomProperties();
		try {
			FileInputStream in = new FileInputStream("config/lang/modedesc_default.properties");
			propDefaultModeDesc.load(in);
			in.close();
		} catch(IOException e) {
			log.error("Couldn't load default mode description file", e);
		}

		propModeDesc = new CustomProperties();
		try {
			FileInputStream in = new FileInputStream("config/lang/modedesc_" + Locale.getDefault().getCountry() + ".properties");
			propModeDesc.load(in);
			in.close();
		} catch(IOException e) {}

		// Mode読み込み
		modeManager = new ModeManager();
		try {
			BufferedReader txtMode = new BufferedReader(new FileReader("config/list/mode.lst"));
			modeManager.loadGameModes(txtMode);
			txtMode.close();
		} catch (IOException e) {
			log.error("Mode list load failed", e);
		}

		// Set default rule selections
		try {
			CustomProperties propDefaultRule = new CustomProperties();
			FileInputStream in = new FileInputStream("config/list/global_defaultrule.properties");
			propDefaultRule.load(in);
			in.close();

			for(int pl = 0; pl < 2; pl++)
				for(int i = 0; i < GameEngine.MAX_GAMESTYLE; i++) {
					// TETROMINO
					if(i == 0) {
						if(propGlobal.getProperty(pl + ".rule") == null) {
							propGlobal.setProperty(pl + ".rule", propDefaultRule.getProperty("default.rule", ""));
							propGlobal.setProperty(pl + ".rulefile", propDefaultRule.getProperty("default.rulefile", ""));
							propGlobal.setProperty(pl + ".rulename", propDefaultRule.getProperty("default.rulename", ""));
						}
					}
					// etc
					else {
						if(propGlobal.getProperty(pl + ".rule." + i) == null) {
							propGlobal.setProperty(pl + ".rule." + i, propDefaultRule.getProperty("default.rule." + i, ""));
							propGlobal.setProperty(pl + ".rulefile." + i, propDefaultRule.getProperty("default.rulefile." + i, ""));
							propGlobal.setProperty(pl + ".rulename." + i, propDefaultRule.getProperty("default.rulename." + i, ""));
						}
					}
				}
		} catch (Exception e) {}

		// Use JInput option
		useJInputKeyboard = false;
		//log.debug("args.length:" + args.length);
		if( (args.length > 0) && (args[0].equals("-j") || args[0].equals("/j")) ) {
			useJInputKeyboard = true;
			log.info("-j option is used. Use JInput to read keyboard input.");
		}

		perfectFPSDelay = System.nanoTime();

		// ゲーム画面などの初期化
		try {
			int sWidth = propConfig.getProperty("option.screenwidth", 640);
			int sHeight = propConfig.getProperty("option.screenheight", 480);

			NullpoMinoSlick obj = new NullpoMinoSlick();

			if((sWidth != 640) || (sHeight != 480)) {
				ScalableGame sObj = new ScalableGame(obj, 640, 480, true);
				appGameContainer = new AppGameContainer(sObj);
			} else {
				appGameContainer = new AppGameContainer(obj);
			}
			appGameContainer.setShowFPS(false);
			appGameContainer.setClearEachFrame(false);
			appGameContainer.setMinimumLogicUpdateInterval(0);
			appGameContainer.setMaximumLogicUpdateInterval(0);
			appGameContainer.setUpdateOnlyWhenVisible(false);
			appGameContainer.setForceExit(false);
			appGameContainer.setDisplayMode(sWidth, sHeight, propConfig.getProperty("option.fullscreen", false));
			appGameContainer.start();
		} catch (Throwable e) {
			log.fatal("Game initialize failed", e);
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
		appGameContainer.setTargetFrameRate(-1);
		beforeTime = System.nanoTime();
		overSleepTime = 0L;
		noDelays = 0;

		alternateFPSTiming = propConfig.getProperty("option.alternateFPSTiming", true);
		alternateFPSDynamicAdjust = propConfig.getProperty("option.alternateFPSDynamicAdjust", false);
		alternateFPSPerfectMode = propConfig.getProperty("option.alternateFPSPerfectMode", true);
		alternateFPSPerfectYield = propConfig.getProperty("option.alternateFPSPerfectYield", true);
		altMaxFPS = propConfig.getProperty("option.maxfps", 60);
		altMaxFPSCurrent = altMaxFPS;

		appGameContainer.setVSync(propConfig.getProperty("option.vsync", false));

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

		//useJInputKeyboard = propConfig.getProperty("option.useJInputKeyboard", true);
	}

	/**
	 * Screenshot保存
	 * @param container GameContainer
	 * @param g Graphics
	 */
	public static void saveScreenShot(GameContainer container, Graphics g) {
		// Filenameを決める
		String dir = propGlobal.getProperty("custom.screenshot.directory", "ss");
		Calendar c = Calendar.getInstance();
		DateFormat dfm = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		String filename = dir + "/" + dfm.format(c.getTime()) + ".png";
		log.info("Saving screenshot to " + filename);

		// Screenshot作成
		try {
			File ssfolder = new File(dir);
			if (!ssfolder.exists()) {
				if (ssfolder.mkdir()) {
					log.info("Created screenshot folder: " + dir);
				} else {
					log.info("Couldn't create screenshot folder at "+ dir);
				}
			}

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
					Color color = screenImage.getColor(i, j + 1);	// どうもY-coordinateは+1しないとズレるらしい

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
	 * @return 翻訳後のUIの文字列 (無いならそのままstrを返す）
	 */
	public static String getUIText(String str) {
		String result = propLang.getProperty(str);
		if(result == null) {
			result = propLangDefault.getProperty(str, str);
		}
		return result;
	}

	/**
	 * FPS cap routine
	 */
	public static void alternateFPSSleep() {
		alternateFPSSleep(false);
	}

	/**
	 * FPS cap routine
	 * @param ingame <code>true</code> if during the gameplay
	 */
	public static void alternateFPSSleep(boolean ingame) {
		int maxfps = altMaxFPSCurrent;

		if(maxfps > 0) {
			periodCurrent = (long) (1.0 / maxfps * 1000000000);

			long afterTime, timeDiff, sleepTime;

			// 休止・FPS計算処理
			afterTime = System.nanoTime();
			timeDiff = afterTime - beforeTime;
			// 前回の frame の休止 time誤差も引いておく
			sleepTime = (periodCurrent - timeDiff) - overSleepTime;

			if(alternateFPSPerfectMode && ingame) {
				if(alternateFPSPerfectYield) {
					while(System.nanoTime() < perfectFPSDelay + 1000000000 / altMaxFPS) {Thread.yield();}
				} else {
					while(System.nanoTime() < perfectFPSDelay + 1000000000 / altMaxFPS) {}
				}
				perfectFPSDelay += 1000000000 / altMaxFPS;
			} else if(sleepTime > 0) {
				// 休止 timeがとれる場合
				if(maxfps > 0) {
					try {
						Thread.sleep(sleepTime / 1000000L);
					} catch(InterruptedException e) {}
					//appGameContainer.sleep((int) (sleepTime / 1000000L));
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
			if(!alternateFPSPerfectMode || !ingame) perfectFPSDelay = beforeTime;
			calcFPS(ingame, periodCurrent);
		} else {
			periodCurrent = (long) (1.0 / 60 * 1000000000);
			calcFPS(ingame, periodCurrent);
		}
	}

	/**
	 * FPSの計算
	 * @param period FPSを計算する間隔
	 */
	protected static void calcFPS(boolean ingame, long period) {
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

			// Set new target fps
			if((altMaxFPS > 0) && (alternateFPSDynamicAdjust) && (!alternateFPSPerfectMode || !ingame)) {
				if(actualFPS < altMaxFPS - 1) {
					// Too Slow
					altMaxFPSCurrent++;
					if(altMaxFPSCurrent > altMaxFPS + 5) altMaxFPSCurrent = altMaxFPS + 5;
					periodCurrent = (long) (1.0 / altMaxFPSCurrent * 1000000000);
				} else if(actualFPS > altMaxFPS + 1) {
					// Too Fast
					altMaxFPSCurrent--;
					if(altMaxFPSCurrent < altMaxFPS - 5) altMaxFPSCurrent = altMaxFPS - 5;
					if(altMaxFPSCurrent < 0) altMaxFPSCurrent = 0;
					periodCurrent = (long) (1.0 / altMaxFPSCurrent * 1000000000);
				}
			}
		}
	}

	/**
	 * Constructor
	 */
	public NullpoMinoSlick() {
		super("NullpoMino (Now Loading...)");
	}

	/*
	 * ステート (タイトルとかゲームとかのシーンのことね）を追加
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
		stateConfigRuleStyleSelect = new StateConfigRuleStyleSelect();
		stateConfigKeyboardNavi = new StateConfigKeyboardNavi();
		stateConfigKeyboardReset = new StateConfigKeyboardReset();
		stateSelectRuleFromList = new StateSelectRuleFromList();
		stateSelectModeFolder = new StateSelectModeFolder();

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
		addState(stateConfigRuleStyleSelect);
		addState(stateConfigKeyboardNavi);
		addState(stateConfigKeyboardReset);
		addState(stateSelectRuleFromList);
		addState(stateSelectModeFolder);
	}

	/**
	 * FPS display
	 * @param container GameContainer
	 */
	public static void drawFPS(GameContainer container) {
		drawFPS(container, false);
	}

	/**
	 * FPS display
	 * @param container GameContainer
	 */
	public static void drawFPS(GameContainer container, boolean ingame) {
		if(propConfig.getProperty("option.showfps", true) == true) {
			if(!alternateFPSDynamicAdjust || (alternateFPSPerfectMode && ingame))
				NormalFont.printFont(0, 480 - 16, df.format(actualFPS), NormalFont.COLOR_BLUE);
			else
				NormalFont.printFont(0, 480 - 16, df.format(actualFPS) + "/" + altMaxFPSCurrent, NormalFont.COLOR_BLUE);
		}
	}

	/**
	 * Observerクライアントを開始
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
	 * Observerクライアントを停止
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
	 * Observerクライアントからの情報を描画
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
