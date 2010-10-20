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
package mu.nu.nullpo.gui.sdl;

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

import mu.nu.nullpo.game.net.NetObserverClient;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.util.CustomProperties;
import mu.nu.nullpo.util.ModeManager;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import sdljava.SDLException;
import sdljava.SDLMain;
import sdljava.SDLVersion;
import sdljava.event.SDLEvent;
import sdljava.event.SDLKeyboardEvent;
import sdljava.event.SDLQuitEvent;
import sdljava.joystick.HatState;
import sdljava.joystick.SDLJoystick;
import sdljava.mixer.SDLMixer;
import sdljava.ttf.SDLTTF;
import sdljava.video.SDLRect;
import sdljava.video.SDLSurface;
import sdljava.video.SDLVideo;

/**
 * NullpoMino SDLVersion
 */
public class NullpoMinoSDL {
	/** Log */
	static Logger log = Logger.getLogger(NullpoMinoSDL.class);

	/** SDL key names */
	public static final String[] SDL_KEYNAMES =
	{
		"NONE","(1)","(2)","(3)","(4)","(5)","(6)","(7)","BACKSPACE","TAB","(10)","(11)","CLEAR","RETURN",
		"(14)","(15)","(16)","(17)","(18)","PAUSE","(20)","(21)","(22)","(23)","(24)","(25)","(26)","ESCAPE",
		"(28)","(29)","(30)","(31)","SPACE","EXCLAIM","QUOTEDBL","HASH","DOLLAR","(37)","AMPERSAND","QUOTE",
		"LEFTPAREN","RIGHTPAREN","ASTERISK","PLUS","COMMA","MINUS","PERIOD","SLASH","0","1","2","3","4","5",
		"6","7","8","9","COLON","SEMICOLON","LESS","EQUALS","GREATER","QUESTION","AT","(65)","(66)","(67)",
		"(68)","(69)","(70)","(71)","(72)","(73)","(74)","(75)","(76)","(77)","(78)","(79)","(80)","(81)",
		"(82)","(83)","(84)","(85)","(86)","(87)","(88)","(89)","(90)","LEFTBRACKET","BACKSLASH","RIGHTBRACKET",
		"CARET","UNDERSCORE","BACKQUOTE","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R",
		"S","T","U","V","W","X","Y","Z","(123)","(124)","(125)","(126)","DELETE","(128)","(129)","(130)","(131)",
		"(132)","(133)","(134)","(135)","(136)","(137)","(138)","(139)","(140)","(141)","(142)","(143)","(144)",
		"(145)","(146)","(147)","(148)","(149)","(150)","(151)","(152)","(153)","(154)","(155)","(156)","(157)",
		"(158)","(159)","WORLD_0","WORLD_1","WORLD_2","WORLD_3","WORLD_4","WORLD_5","WORLD_6","WORLD_7","WORLD_8",
		"WORLD_9","WORLD_10","WORLD_11","WORLD_12","WORLD_13","WORLD_14","WORLD_15","WORLD_16","WORLD_17","WORLD_18",
		"WORLD_19","WORLD_20","WORLD_21","WORLD_22","WORLD_23","WORLD_24","WORLD_25","WORLD_26","WORLD_27","WORLD_28",
		"WORLD_29","WORLD_30","WORLD_31","WORLD_32","WORLD_33","WORLD_34","WORLD_35","WORLD_36","WORLD_37","WORLD_38",
		"WORLD_39","WORLD_40","WORLD_41","WORLD_42","WORLD_43","WORLD_44","WORLD_45","WORLD_46","WORLD_47","WORLD_48",
		"WORLD_49","WORLD_50","WORLD_51","WORLD_52","WORLD_53","WORLD_54","WORLD_55","WORLD_56","WORLD_57","WORLD_58",
		"WORLD_59","WORLD_60","WORLD_61","WORLD_62","WORLD_63","WORLD_64","WORLD_65","WORLD_66","WORLD_67","WORLD_68",
		"WORLD_69","WORLD_70","WORLD_71","WORLD_72","WORLD_73","WORLD_74","WORLD_75","WORLD_76","WORLD_77","WORLD_78",
		"WORLD_79","WORLD_80","WORLD_81","WORLD_82","WORLD_83","WORLD_84","WORLD_85","WORLD_86","WORLD_87","WORLD_88",
		"WORLD_89","WORLD_90","WORLD_91","WORLD_92","WORLD_93","WORLD_94","WORLD_95","KP0","KP1","KP2","KP3","KP4",
		"KP5","KP6","KP7","KP8","KP9","KP_PERIOD","KP_DIVIDE","KP_MULTIPLY","KP_MINUS","KP_PLUS","KP_ENTER","KP_EQUALS",
		"UP","DOWN","RIGHT","LEFT","INSERT","HOME","END","PAGEUP","PAGEDOWN","F1","F2","F3","F4","F5","F6","F7","F8","F9",
		"F10","F11","F12","F13","F14","F15","(297)","(298)","(299)","NUMLOCK","CAPSLOCK","SCROLLOCK","RSHIFT","LSHIFT","RCTRL"
		,"LCTRL","RALT","LALT","RMETA","LMETA","LSUPER","RSUPER","MODE","COMPOSE","HELP","PRINT","SYSREQ","BREAK","MENU",
		"POWER","EURO","UNDO"
	};

	/** ゲームステートのID */
	public static final int STATE_TITLE = 0,
							STATE_CONFIG_MAINMENU = 1,
							STATE_CONFIG_RULESELECT = 2,
							STATE_CONFIG_GENERAL = 3,
							STATE_CONFIG_KEYBOARD = 4,
							STATE_CONFIG_JOYSTICK_BUTTON = 5,
							STATE_SELECTMODE = 6,
							STATE_INGAME = 7,
							STATE_REPLAYSELECT = 8,
							STATE_CONFIG_AISELECT = 9,
							STATE_NETGAME = 10,
							STATE_CONFIG_JOYSTICK_MAIN = 11,
							STATE_CONFIG_JOYSTICK_TEST = 12,
							STATE_CONFIG_GAMETUNING = 13,
							STATE_CONFIG_RULESTYLESELECT = 14,
							STATE_CONFIG_KEYBOARD_NAVI = 15,
							STATE_CONFIG_KEYBOARD_RESET = 16,
							STATE_SELECTRULEFROMLIST = 17;

	/** ゲームステートのcount */
	public static final int STATE_MAX = 18;

	/** 認識するキーのMaximum値 */
	public static final int SDL_KEY_MAX = 322;

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

	/** Mode 管理 */
	public static ModeManager modeManager;

	/** 終了 flag */
	public static boolean quit = false;

	/** FPS表示 */
	public static boolean showfps = true;

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

	/** キーを押しているならtrue */
	public static boolean[] keyPressedState;

	/** 使用するJoystick の number */
	public static int[] joyUseNumber;

	/** Joystick のアナログスティック無視 */
	public static boolean[] joyIgnoreAxis;

	/** Joystick のハットスイッチ無視 */
	public static boolean[] joyIgnorePOV;

	/** Joystick のcount */
	public static int joystickMax;

	/** Joystick */
	public static SDLJoystick[] joystick;

	/** Joystick direction key 状態 */
	public static int[] joyAxisX, joyAxisY;

	/** Joystick のハットスイッチのcount */
	public static int[] joyMaxHat;

	/** Joystick のハットスイッチ state */
	public static HatState[] joyHatState;

	/** Joystick の buttonのcount */
	public static int[] joyMaxButton;

	/** Joystick の buttonを押しているならtrue */
	public static boolean[][] joyPressedState;

	/** ゲームステート */
	public static BaseStateSDL[] gameStates;

	/** Current ステート */
	public static int currentState;

	/** Exit buttonやScreenshot buttonの使用許可 */
	public static boolean enableSpecialKeys;

	/** Exit button使用許可 */
	public static boolean allowQuit;

	/** true if disable automatic input update */
	public static boolean disableAutoInputUpdate;

	/** MaximumFPS */
	public static int maxFPS;

	/** Observerクライアント */
	public static NetObserverClient netObserverClient;

	/**
	 * メイン関count
	 * @param args プログラムに渡された引count
	 */
	public static void main(String[] args) {
		PropertyConfigurator.configure("config/etc/log_sdl.cfg");
		log.info("NullpoMinoSDL Start");

		programArgs = args;
		propConfig = new CustomProperties();
		propGlobal = new CustomProperties();
		propMusic = new CustomProperties();

		// 設定ファイル読み込み
		try {
			FileInputStream in = new FileInputStream("config/setting/sdl.cfg");
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
		} catch(IOException e) {}

		// 言語ファイル読み込み
		propLangDefault = new CustomProperties();
		try {
			FileInputStream in = new FileInputStream("config/lang/sdl_default.properties");
			propLangDefault.load(in);
			in.close();
		} catch (IOException e) {
			log.error("Failed to load default UI language file", e);
		}

		propLang = new CustomProperties();
		try {
			FileInputStream in = new FileInputStream("config/lang/sdl_" + Locale.getDefault().getCountry() + ".properties");
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
			log.error("Failed to load game mode list", e);
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

		// Key input のInitialization
		keyPressedState = new boolean[SDL_KEY_MAX];
		GameKeySDL.initGlobalGameKeySDL();
		GameKeySDL.gamekey[0].loadConfig(propConfig);
		GameKeySDL.gamekey[1].loadConfig(propConfig);
		MouseInputSDL.initalizeMouseInput();

		// State initialization
		currentState = -1;
		gameStates = new BaseStateSDL[STATE_MAX];
		gameStates[STATE_TITLE] = new StateTitleSDL();
		gameStates[STATE_CONFIG_MAINMENU] = new StateConfigMainMenuSDL();
		gameStates[STATE_CONFIG_RULESELECT] = new StateConfigRuleSelectSDL();
		gameStates[STATE_CONFIG_GENERAL] = new StateConfigGeneralSDL();
		gameStates[STATE_CONFIG_KEYBOARD] = new StateConfigKeyboardSDL();
		gameStates[STATE_CONFIG_JOYSTICK_BUTTON] = new StateConfigJoystickButtonSDL();
		gameStates[STATE_SELECTMODE] = new StateSelectModeSDL();
		gameStates[STATE_INGAME] = new StateInGameSDL();
		gameStates[STATE_REPLAYSELECT] = new StateReplaySelectSDL();
		gameStates[STATE_CONFIG_AISELECT] = new StateConfigAISelectSDL();
		gameStates[STATE_NETGAME] = new StateNetGameSDL();
		gameStates[STATE_CONFIG_JOYSTICK_MAIN] = new StateConfigJoystickMainSDL();
		gameStates[STATE_CONFIG_JOYSTICK_TEST] = new StateConfigJoystickTestSDL();
		gameStates[STATE_CONFIG_GAMETUNING] = new StateConfigGameTuningSDL();
		gameStates[STATE_CONFIG_RULESTYLESELECT] = new StateConfigRuleStyleSelectSDL();
		gameStates[STATE_CONFIG_KEYBOARD_NAVI] = new StateConfigKeyboardNaviSDL();
		gameStates[STATE_CONFIG_KEYBOARD_RESET] = new StateConfigKeyboardResetSDL();
		gameStates[STATE_SELECTRULEFROMLIST] = new StateSelectRuleFromListSDL();

		// SDLのInitializationと開始
		try {
			init();
			run();
		} catch (Throwable e) {
			log.fatal("Uncaught Exception", e);
		} finally {
			shutdown();
		}

		System.exit(0);
	}

	/**
	 * SDLのInitialization
	 * @throws SDLException SDLのエラーが発生した場合
	 */
	public static void init() throws SDLException {
		log.info("Now initializing SDL...");

		SDLMain.init(SDLMain.SDL_INIT_VIDEO | SDLMain.SDL_INIT_AUDIO | SDLMain.SDL_INIT_JOYSTICK);

		SDLVersion ver = SDLMain.getSDLVersion();
		log.info("SDL Version:" + ver.getMajor() + "." + ver.getMinor() + "." + ver.getPatch());

		SDLVideo.wmSetCaption("NullpoMino (Now Loading...)", null);

		long flags = SDLVideo.SDL_ANYFORMAT | SDLVideo.SDL_DOUBLEBUF | SDLVideo.SDL_HWSURFACE;
		if(propConfig.getProperty("option.fullscreen", false) == true) flags |= SDLVideo.SDL_FULLSCREEN;
		SDLVideo.setVideoMode(640, 480, 0, flags);

		SDLTTF.init();
		SDLVersion ttfver = SDLTTF.getTTFVersion();
		log.info("TTF Version:" + ttfver.getMajor() + "." + ttfver.getMinor() + "." + ttfver.getPatch());

		SDLMixer.openAudio(44100, SDLMixer.AUDIO_S16SYS, 2, propConfig.getProperty("option.soundbuffer", 1024));

		joyUseNumber = new int[2];
		joyUseNumber[0] = propConfig.getProperty("joyUseNumber.p0", -1);
		joyUseNumber[1] = propConfig.getProperty("joyUseNumber.p1", -1);
		joyIgnoreAxis = new boolean[2];
		joyIgnoreAxis[0] = propConfig.getProperty("joyIgnoreAxis.p0", false);
		joyIgnoreAxis[1] = propConfig.getProperty("joyIgnoreAxis.p1", false);
		joyIgnorePOV = new boolean[2];
		joyIgnorePOV[0] = propConfig.getProperty("joyIgnorePOV.p0", false);
		joyIgnorePOV[1] = propConfig.getProperty("joyIgnorePOV.p1", false);

		joystickMax = SDLJoystick.numJoysticks();
		log.info("Number of Joysticks:" + joystickMax);

		if(joystickMax > 0) {
			joystick = new SDLJoystick[joystickMax];
			joyAxisX = new int[joystickMax];
			joyAxisY = new int[joystickMax];
			joyMaxHat = new int[joystickMax];
			joyMaxButton = new int[joystickMax];
			joyHatState = new HatState[joystickMax];

			int max = 0;

			for(int i = 0; i < joystickMax; i++) {
				try {
					joystick[i] = SDLJoystick.joystickOpen(i);

					joyMaxButton[i] = joystick[i].joystickNumButtons();
					if(joyMaxButton[i] > max) max = joyMaxButton[i];

					joyMaxHat[i] = joystick[i].joystickNumHats();
					joyHatState[i] = null;
				} catch (Throwable e) {
					log.warn("Failed to open Joystick #" + i, e);
				}
			}

			joyPressedState = new boolean[joystickMax][max];
		}
	}

	/**
	 * メインループ
	 * @throws SDLException SDLのエラーが発生した場合
	 */
	public static void run() throws SDLException {
		maxFPS = propConfig.getProperty("option.maxfps", 60);

		long beforeTime, afterTime, timeDiff, sleepTime;
		long overSleepTime = 0L;
		int noDelays = 0;

		showfps = propConfig.getProperty("option.showfps", true);

		beforeTime = System.nanoTime();
		prevCalcTime = beforeTime;

		quit = false;
		enableSpecialKeys = true;
		allowQuit = true;

		SDLSurface surface = SDLVideo.getVideoSurface();

		// 画像などの読み込み
		ResourceHolderSDL.load();
		NormalFontSDL.dest = surface;

		// First run
		if(propConfig.getProperty("option.firstSetupMode", true) == true) {
			// Set various default settings here
			GameKeySDL.gamekey[0].loadDefaultKeymap();
			GameKeySDL.gamekey[0].saveConfig(propConfig);
			propConfig.setProperty("option.firstSetupMode", false);

			// Set default rotation button setting (only for first run)
			if(propGlobal.getProperty("global.firstSetupMode", true) == true) {
				for(int pl = 0; pl < 2; pl++) {
					if(propGlobal.getProperty(pl + ".tuning.owRotateButtonDefaultRight") == null) {
						propGlobal.setProperty(pl + ".tuning.owRotateButtonDefaultRight", 0);
					}
				}
				propGlobal.setProperty("global.firstSetupMode", false);
			}

			// Save settings
			saveConfig();

			// Go to title screen
			enterState(STATE_TITLE);
		}
		// Second+ run
		else {
			enterState(STATE_TITLE);
		}

		// メインループ
		while(quit == false) {
			//  event 処理
			processEvent();
			if(quit == true) break;

			// Joystick の更新
			if(joystickMax > 0) joyUpdate();

			// Update key input states
			if(!disableAutoInputUpdate) {
				for(int i = 0; i < 2; i++) {
					int joynum = joyUseNumber[i];

					if((joystickMax > 0) && (joynum >= 0) && (joynum < joystickMax)) {
						GameKeySDL.gamekey[i].update(keyPressedState, joyPressedState[joynum], joyAxisX[joynum], joyAxisY[joynum], joyHatState[joynum]);
					} else {
						GameKeySDL.gamekey[i].update(keyPressedState);
					}
				}
			}

			// 各ステートの処理を実行
			gameStates[currentState].update();
			gameStates[currentState].render(surface);

			// FPS描画
			if(showfps) NormalFontSDL.printFont(0, 480 - 16, NullpoMinoSDL.df.format(NullpoMinoSDL.actualFPS), NormalFontSDL.COLOR_BLUE, 1.0f);

			// Observerクライアント
			if((netObserverClient != null) && netObserverClient.isConnected()) {
				int fontcolor = NormalFontSDL.COLOR_BLUE;
				if(netObserverClient.getObserverCount() > 1) fontcolor = NormalFontSDL.COLOR_GREEN;
				if(netObserverClient.getObserverCount() > 0 && netObserverClient.getPlayerCount() > 0) fontcolor = NormalFontSDL.COLOR_RED;
				String strObserverInfo = String.format("%d/%d", netObserverClient.getObserverCount(), netObserverClient.getPlayerCount());
				String strObserverString = String.format("%40s", strObserverInfo);
				NormalFontSDL.printFont(0, 480 - 16, strObserverString, fontcolor);
			}

			// 特殊キー
			if(enableSpecialKeys) {
				// Screenshot
				if(GameKeySDL.gamekey[0].isPushKey(GameKeySDL.BUTTON_SCREENSHOT) || GameKeySDL.gamekey[1].isPushKey(GameKeySDL.BUTTON_SCREENSHOT))
					saveScreenShot();

				// Exit button
				if(allowQuit) {
					if(GameKeySDL.gamekey[0].isPushKey(GameKeySDL.BUTTON_QUIT) || GameKeySDL.gamekey[1].isPushKey(GameKeySDL.BUTTON_QUIT))
						enterState(-1);
				}
			}

			// 画面に表示
			surface.flip();

			// 休止・FPS計算処理
			afterTime = System.nanoTime();
			timeDiff = afterTime - beforeTime;
			// 前回の frame の休止 time誤差も引いておく
			long period = (long) (1.0 / maxFPS * 1000000000);
			sleepTime = (period - timeDiff) - overSleepTime;

			if(sleepTime > 0) {
				// 休止 timeがとれる場合
				if(maxFPS > 0) {
					try {
						Thread.sleep(sleepTime / 1000000L);
					} catch(InterruptedException e) {}
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
			calcFPS(period);
		}
	}

	/**
	 * SDLの終了処理
	 */
	public static void shutdown() {
		log.info("NullpoMinoSDL shutdown()");

		try {
			stopObserverClient();
			for(int i = 0; i < joystickMax; i++) {
				joystick[i].joystickClose();
			}
			SDLMixer.close();
			SDLMain.quit();
		} catch (Throwable e) {}
	}

	/**
	 * ステート切り替え
	 * @param id 切り替え先ステートID (-1で終了）
	 * @throws SDLException SDLのエラーが発生した場合
	 */
	public static void enterState(int id) throws SDLException {
		if((currentState >= 0) && (currentState < STATE_MAX) && (gameStates[currentState] != null)) {
			gameStates[currentState].leave();
		}
		if((id >= 0) && (id < STATE_MAX) && (gameStates[id] != null)) {
			currentState = id;
			gameStates[currentState].enter();
		} else if(id < 0) {
			quit = true;
		} else {
			throw new NullPointerException("Game state #" + id + " is null");
		}
	}

	/**
	 * 設定ファイルを保存
	 */
	public static void saveConfig() {
		try {
			FileOutputStream out = new FileOutputStream("config/setting/sdl.cfg");
			propConfig.store(out, "NullpoMino SDL-frontend Config");
			out.close();
			log.debug("Saved SDL-frontend config");
		} catch(IOException e) {
			log.error("Failed to save SDL-specific config", e);
		}

		try {
			FileOutputStream out = new FileOutputStream("config/setting/global.cfg");
			propGlobal.store(out, "NullpoMino Global Config");
			out.close();
			log.debug("Saved global config");
		} catch(IOException e) {
			log.error("Failed to save global config", e);
		}
	}

	/**
	 * Screenshotを保存
	 * @throws SDLException 保存に失敗した場合
	 */
	public static void saveScreenShot() throws SDLException {
		// Filenameを決める
		String dir = NullpoMinoSDL.propGlobal.getProperty("custom.screenshot.directory", "ss");
		GregorianCalendar currentTime = new GregorianCalendar();
		int month = currentTime.get(Calendar.MONTH) + 1;
		String filename = String.format(
				dir + "/%04d_%02d_%02d_%02d_%02d_%02d.bmp",
				currentTime.get(Calendar.YEAR), month, currentTime.get(Calendar.DATE), currentTime.get(Calendar.HOUR_OF_DAY),
				currentTime.get(Calendar.MINUTE), currentTime.get(Calendar.SECOND)
		);
		log.info("Saving screenshot to " + filename);

		File ssfolder = new File(dir);
		if (!ssfolder.exists()) {
			if (ssfolder.mkdir()) {
				log.info("Created screenshot folder: " + dir);
			} else {
				log.info("Couldn't create screenshot folder at "+ dir);
			}
		}

		// ファイルに保存
		SDLVideo.getVideoSurface().saveBMP(filename);
	}

	/**
	 * 画面外にはみ出す画像をちゃんと描画できるようにSDLRectを修正する
	 * @param rectSrc 修正するSDLRect (描画元）
	 * @param rectDst 修正するSDLRect (描画先）
	 */
	public static void fixRect(SDLRect rectSrc, SDLRect rectDst) {
		if(rectSrc == null) return;
		if(rectDst == null) return;

		if(rectDst.x < 0) {
			int prevX = rectDst.x;
			rectDst.width += prevX;
			rectDst.x = 0;
			rectSrc.width += prevX;
			rectSrc.x -= prevX;
		}

		if(rectDst.y < 0) {
			int prevY = rectDst.y;
			rectDst.height += prevY;
			rectDst.y = 0;
			rectSrc.height += prevY;
			rectSrc.y -= prevY;
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
	 *  event 処理
	 * @throws SDLException SDLのエラーが発生した場合
	 */
	protected static void processEvent() throws SDLException {
		while(true) {
			SDLEvent event = SDLEvent.pollEvent();
			if(event == null) break;

			if(event instanceof SDLQuitEvent) {
				// Exit button
				enterState(-1);
			} else if(event instanceof SDLKeyboardEvent) {
				// Key input
				SDLKeyboardEvent keyevent = (SDLKeyboardEvent)event;

				int keysym = keyevent.getSym();

				if(keyevent.getType() == SDLKeyboardEvent.SDL_KEYDOWN) {
					if(keysym < keyPressedState.length) keyPressedState[keysym] = true;
				} else if(keyevent.getType() == SDLKeyboardEvent.SDL_KEYUP) {
					if(keysym < keyPressedState.length) keyPressedState[keysym] = false;
				}
			}
		}
	}

	/**
	 * Joystick  stateの更新
	 */
	protected static void joyUpdate() {
		try {
			SDLJoystick.joystickUpdate();

			for(int i = 0; i < joystickMax; i++) {
				if(joyIgnoreAxis[i] == false) {
					joyAxisX[i] = joystick[i].joystickGetAxis(0);
					joyAxisY[i] = joystick[i].joystickGetAxis(1);
				} else {
					joyAxisX[i] = 0;
					joyAxisY[i] = 0;
				}

				for(int j = 0; j < joyMaxButton[i]; j++) {
					joyPressedState[i][j] = joystick[i].joystickGetButton(j);
				}

				if((joyMaxHat[i] > 0) && (joyIgnorePOV[i] == false)) {
					joyHatState[i] = joystick[i].joystickGetHat(0);
				} else {
					joyHatState[i] = null;
				}
			}
		} catch (Throwable e) {
			log.warn("Joystick state update failed", e);
		}
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

			// 実際の経過 timeを測定
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
}
