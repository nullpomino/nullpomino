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
package org.game_host.hebo.nullpomino.game.play;

import org.apache.log4j.Logger;
import org.game_host.hebo.nullpomino.game.component.BGMStatus;
import org.game_host.hebo.nullpomino.game.component.BackgroundStatus;
import org.game_host.hebo.nullpomino.game.event.EventReceiver;
import org.game_host.hebo.nullpomino.game.subsystem.mode.GameMode;
import org.game_host.hebo.nullpomino.util.CustomProperties;

/**
 * ゲーム状態の管理
 */
public class GameManager {
	/** ログ */
	static Logger log = Logger.getLogger(GameManager.class);

	/** メジャーバージョン */
	public static final float VERSION_MAJOR = 7.2f;

	/** マイナーバージョン */
	public static final int VERSION_MINOR = 0;

	/** ゲームモード */
	public GameMode mode;

	/** モードが保存する設定データのプロパティセット */
	public CustomProperties modeConfig;

	/** リプレイが格納されるプロパティセット */
	public CustomProperties replayProp;

	/** リプレイならtrue */
	public boolean replayMode;

	/** リプレイ追記モード */
	public boolean replayRerecord;

	/** メニューだけ表示する（ゲーム画面を表示しない） */
	public boolean menuOnly;

	/** 描画などのイベント処理 */
	public EventReceiver receiver;

	/** BGMの状態 */
	public BGMStatus bgmStatus;

	/** 背景の状態 */
	public BackgroundStatus backgroundStatus;

	/** 各プレイヤーの状態 */
	public GameEngine[] engine;

	/**
	 * メジャーバージョンを取得
	 * @return メジャーバージョン
	 */
	public static float getVersionMajor() {
		return VERSION_MAJOR;
	}

	/**
	 * マイナーバージョンを取得
	 * @return マイナーバージョン
	 */
	public static int getVersionMinor() {
		return VERSION_MINOR;
	}

	/**
	 * マイナーバージョンを取得(旧バージョンとの互換用)
	 * @return マイナーバージョン
	 */
	public static float getVersionMinorOld() {
		return VERSION_MINOR;
	}

	/**
	 * バージョンを文字列で取得
	 * @return バージョンの文字列
	 */
	public static String getVersionString() {
		return VERSION_MAJOR + "." + VERSION_MINOR;
	}

	/**
	 * パラメータなしコンストラクタ
	 */
	public GameManager() {
		log.debug("GameManager constructor called");
	}

	/**
	 * 通常のコンストラクタ
	 * @param receiver 描画などのイベント処理
	 */
	public GameManager(EventReceiver receiver) {
		this();
		this.receiver = receiver;
	}

	/**
	 * 初期化
	 */
	public void init() {
		log.debug("GameManager init()");

		if(receiver == null) receiver = new EventReceiver();

		modeConfig = receiver.loadModeConfig();
		if(modeConfig == null) modeConfig = new CustomProperties();

		if(replayProp == null) {
			replayProp = new CustomProperties();
			replayMode = false;
		}

		replayRerecord = false;
		menuOnly = false;

		bgmStatus = new BGMStatus();
		backgroundStatus = new BackgroundStatus();

		int players = 1;
		if(mode != null) {
			mode.modeInit(this);
			players = mode.getPlayers();
		}
		engine = new GameEngine[players];
		for(int i = 0; i < engine.length; i++) engine[i] = new GameEngine(this, i);
	}

	/**
	 * ゲームのリセット
	 */
	public void reset() {
		log.debug("GameManager reset()");

		menuOnly = false;
		bgmStatus.reset();
		backgroundStatus.reset();
		if(!replayMode) replayProp = new CustomProperties();
		for(int i = 0; i < engine.length; i++) engine[i].init();
	}

	/**
	 * 終了処理
	 */
	public void shutdown() {
		log.debug("GameManager shutdown()");

		try {
			for(int i = 0; i < engine.length; i++) {
				engine[i].shutdown();
				engine[i] = null;
			}
			engine = null;
			mode = null;
			modeConfig = null;
			replayProp = null;
			receiver = null;
			bgmStatus = null;
			backgroundStatus = null;
		} catch (Throwable e) {
			log.debug("Caught Throwable on shutdown", e);
		}
	}

	/**
	 * プレイヤー人数を取得
	 * @return プレイヤー人数
	 */
	public int getPlayers() {
		return engine.length;
	}

	/**
	 * どれかのゲームエンジンで終了フラグが立っているかどうか判定
	 * @return どれかのゲームエンジンで終了フラグが立っているとtrue
	 */
	public boolean getQuitFlag() {
		if(engine != null) {
			for(int i = 0; i < engine.length; i++) {
				if((engine[i] != null) && (engine[i].quitflag == true))
					return true;
			}
		}

		return false;
	}

	/**
	 * どれかのゲームエンジンが動作中か判定
	 * @return どれかのゲームエンジンが動作中ならtrue
	 */
	public boolean isGameActive() {
		if(engine != null) {
			for(int i = 0; i < engine.length; i++) {
				if((engine[i] != null) && (engine[i].gameActive == true))
					return true;
			}
		}

		return false;
	}

	/**
	 * 生き残ったプレイヤーのIDを取得
	 * @return 生き残ったプレイヤーのID（1人プレイなら-1、引き分けなら-2）
	 */
	public int getWinner() {
		if(engine.length < 2) return -1;

		for(int i = 0; i < engine.length; i++) {
			if(engine[i].stat != GameEngine.STAT_GAMEOVER) {
				return i;
			}
		}

		return -2;
	}

	/**
	 * 全てのゲームエンジンの状態を更新
	 */
	public void updateAll() {
		for(int i = 0; i < engine.length; i++) {
			engine[i].update();
		}
		bgmStatus.fadeUpdate();
		backgroundStatus.fadeUpdate();
	}

	/**
	 * 全てのゲームエンジンの描画処理を実行
	 */
	public void renderAll() {
		for(int i = 0; i < engine.length; i++) {
			engine[i].render();
		}
	}

	/**
	 * リプレイ保存時の処理
	 */
	public void saveReplay() {
		replayProp = new CustomProperties();
		for(int i = 0; i < engine.length; i++) {
			engine[i].saveReplay();
		}
		receiver.saveReplay(this, replayProp);
	}
}
