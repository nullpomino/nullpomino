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
package mu.nu.nullpo.game.subsystem.mode;

import mu.nu.nullpo.game.component.Block;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.util.CustomProperties;

/**
 * ゲームMode のインターフェイス
 */
public interface GameMode {
	/**
	 * Mode  nameを取得
	 * @return Mode name
	 */
	public String getName();

	/**
	 * このMode のPlayerの人countを取得
	 * @return Playerの人count
	 */
	public int getPlayers();

	/**
	 * ゲーム画面表示直前に呼び出される処理
	 * @param manager GameManager that owns this mode
	 */
	public void modeInit(GameManager manager);

	/**
	 * Initialization for each playerが終わるときに呼び出される処理
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	public void playerInit(GameEngine engine, int playerID);

	/**
	 * Ready→Go直後、最初のピースが現れる直前の処理
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	public void startGame(GameEngine engine, int playerID);

	/**
	 * 各Playerの最初の処理の時に呼び出される
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	public void onFirst(GameEngine engine, int playerID);

	/**
	 * 各Playerの最後の処理の時に呼び出される
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	public void onLast(GameEngine engine, int playerID);

	/**
	 * 開始前の設定画面のときの処理
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @return trueを返すと自動的にReady画面に移動しない
	 */
	public boolean onSetting(GameEngine engine, int playerID);

	/**
	 * Ready→Goのときの処理
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @return trueを返すと通常の処理を行わない
	 */
	public boolean onReady(GameEngine engine, int playerID);

	/**
	 * Blockピースの移動処理
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @return trueを返すと通常の処理を行わない
	 */
	public boolean onMove(GameEngine engine, int playerID);

	/**
	 * Block固定直後の光っているときの処理
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @return trueを返すと通常の処理を行わない
	 */
	public boolean onLockFlash(GameEngine engine, int playerID);

	/**
	 * Line clear処理
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @return trueを返すと通常の処理を行わない
	 */
	public boolean onLineClear(GameEngine engine, int playerID);

	/**
	 * ARE中の処理
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @return trueを返すと通常の処理を行わない
	 */
	public boolean onARE(GameEngine engine, int playerID);

	/**
	 * Ending突入時の処理
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @return trueを返すと通常の処理を行わない
	 */
	public boolean onEndingStart(GameEngine engine, int playerID);

	/**
	 * 各ゲームMode が自由に使えるステータスの処理
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @return trueでもfalseでも意味は変わりません
	 */
	public boolean onCustom(GameEngine engine, int playerID);

	/**
	 * Ending画面の処理
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @return trueを返すと通常の処理を行わない
	 */
	public boolean onExcellent(GameEngine engine, int playerID);

	/**
	 * game over画面の処理
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @return trueを返すと通常の処理を行わない
	 */
	public boolean onGameOver(GameEngine engine, int playerID);

	/**
	 * 結果画面の処理
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @return trueを返すと通常の処理を行わない
	 */
	public boolean onResult(GameEngine engine, int playerID);

	/**
	 * フィールドエディット画面の処理
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @return trueを返すと通常の処理を行わない
	 */
	public boolean onFieldEdit(GameEngine engine, int playerID);

	/**
	 * 各Playerの最初の描画処理の時に呼び出される
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	public void renderFirst(GameEngine engine, int playerID);

	/**
	 * 各Playerの最後の描画処理の時に呼び出される
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	public void renderLast(GameEngine engine, int playerID);

	/**
	 * 開始前の設定画面のときの描画処理
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	public void renderSetting(GameEngine engine, int playerID);

	/**
	 * Ready→Goのときの描画処理
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	public void renderReady(GameEngine engine, int playerID);

	/**
	 * Blockピースの移動描画処理
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	public void renderMove(GameEngine engine, int playerID);

	/**
	 * Block固定直後の光っているときの描画処理
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	public void renderLockFlash(GameEngine engine, int playerID);

	/**
	 * Line clear描画処理
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	public void renderLineClear(GameEngine engine, int playerID);

	/**
	 * ARE中の描画処理
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	public void renderARE(GameEngine engine, int playerID);

	/**
	 * Ending突入時の描画処理
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	public void renderEndingStart(GameEngine engine, int playerID);

	/**
	 * 各ゲームMode が自由に使えるステータスの描画処理
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	public void renderCustom(GameEngine engine, int playerID);

	/**
	 * Ending画面の描画処理
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	public void renderExcellent(GameEngine engine, int playerID);

	/**
	 * game over画面の描画処理
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	public void renderGameOver(GameEngine engine, int playerID);

	/**
	 * Render results screen処理
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	public void renderResult(GameEngine engine, int playerID);

	/**
	 * フィールドエディット画面の描画処理
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	public void renderFieldEdit(GameEngine engine, int playerID);

	/**
	 * Blockを消す演出を出すときの処理
	 * @param engine GameEngineのインスタンス
	 * @param playerID Player ID
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param blk Block
	 */
	public void blockBreak(GameEngine engine, int playerID, int x, int y, Block blk);

	/**
	 * Calculate score(pieceLockedの前)
	 * @param engine GameEngineのインスタンス
	 * @param playerID Player ID
	 * @param lines 消えるLinescount (消えなかった場合は0）
	 */
	public void calcScore(GameEngine engine, int playerID, int lines);

	/**
	 * Soft drop使用後の処理
	 * @param engine GameEngineのインスタンス
	 * @param playerID Player ID
	 * @param fall 今落下した段count
	 */
	public void afterSoftDropFall(GameEngine engine, int playerID, int fall);

	/**
	 * Hard drop使用後の処理
	 * @param engine GameEngineのインスタンス
	 * @param playerID Player ID
	 * @param fall 今落下した段count
	 */
	public void afterHardDropFall(GameEngine engine, int playerID, int fall);

	/**
	 * フィールドエディット画面から出たときの処理
	 * @param engine GameEngineのインスタンス
	 * @param playerID Player ID
	 */
	public void fieldEditExit(GameEngine engine, int playerID);

	/**
	 * Blockピースが固定されたときの処理(calcScoreの直後)
	 * @param engine GameEngineのインスタンス
	 * @param playerID Player ID
	 * @param lines 消えるLinescount (消えなかった場合は0）
	 */
	public void pieceLocked(GameEngine engine, int playerID, int lines);

	/**
	 * Line clearが終わるときに呼び出される処理
	 * @param engine GameEngine
	 * @param playerID Player ID
	 * @return trueを返すと通常の処理を行わない
	 */
	public boolean lineClearEnd(GameEngine engine, int playerID);

	/**
	 * Called when saving replay
	 * @param engine GameEngineのインスタンス
	 * @param playerID Player ID
	 * @param prop リプレイ保存先のプロパティセット
	 */
	public void saveReplay(GameEngine engine, int playerID, CustomProperties prop);

	/**
	 * リプレイ読み込み時の処理
	 * @param engine GameEngineのインスタンス
	 * @param playerID Player ID
	 * @param prop リプレイ読み込み元のプロパティセット
	 */
	public void loadReplay(GameEngine engine, int playerID, CustomProperties prop);

	/**
	 * ネットプレイ用Mode かどうかを取得
	 * @return ネットプレイ用Mode ならtrue
	 */
	public boolean isNetplayMode();

	/**
	 * ネットプレイ準備
	 * @param obj 任意のオブジェクト(今のところNetLobbyFrame)
	 */
	public void netplayInit(Object obj);

	/**
	 * When the mode unloads during netplay (Called when mode change happens)
	 * @param obj Any object (Currently NetLobbyFrame)
	 */
	public void netplayUnload(Object obj);
}
