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
package mu.nu.nullpo.game.subsystem.ai;

import mu.nu.nullpo.game.component.Controller;
import mu.nu.nullpo.game.play.GameEngine;

/**
 * AIのインターフェイス
 */
public interface AIPlayer {
	/**
	 * Nameを取得
	 * @return AIのName
	 */
	public String getName();

	/**
	 * Called at initialization
	 * @param engine The GameEngine that owns this AI
	 * @param playerID Player ID
	 */
	public void init(GameEngine engine, int playerID);

	/**
	 * 終了処理
	 * @param engine The GameEngine that owns this AI
	 * @param playerID Player ID
	 */
	public void shutdown(GameEngine engine, int playerID);

	/**
	 *  Set button input states
	 * @param engine The GameEngine that owns this AI
	 * @param playerID Player ID
	 * @param ctrl Button input状態管理クラス
	 */
	public void setControl(GameEngine engine, int playerID, Controller ctrl);

	/**
	 * Called at the start of each frame
	 * @param engine The GameEngine that owns this AI
	 * @param playerID Player ID
	 */
	public void onFirst(GameEngine engine, int playerID);

	/**
	 * Called after every frame
	 * @param engine The GameEngine that owns this AI
	 * @param playerID Player ID
	 */
	public void onLast(GameEngine engine, int playerID);

	/**
	 * Called to display internal state
	 * @param engine The GameEngine that owns this AI
	 * @param playerID Player ID
	 */
	public void renderState(GameEngine engine, int playerID);

	/**
	 * 新しいピースが出現したときの処理
	 * @param engine The GameEngine that owns this AI
	 * @param playerID Player ID
	 */
	public void newPiece(GameEngine engine, int playerID);

	/**
	 * Called to display additional hint information
	 * @param engine The GameEngine that owns this AI
	 * @param playerID Player ID
	 */
	public void renderHint(GameEngine engine, int playerID);
}
