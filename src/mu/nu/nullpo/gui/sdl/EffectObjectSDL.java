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

/**
 * 各種エフェクトの状態
 */
public class EffectObjectSDL {
	/** エフェクト type */
	public int effect;

	/** X-coordinate */
	public int x;

	/** Y-coordinate */
	public int y;

	/** エフェクトのパラメータ (Blockの色など) */
	public int param;

	/** アニメーションカウンタ */
	public int anim;

	/**
	 * Constructor
	 */
	public EffectObjectSDL() {
		effect = 0;
		x = 0;
		y = 0;
		param = 0;
		anim = 0;
	}

	/**
	 * パラメータ付きConstructor
	 * @param effect エフェクト type
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param param エフェクトのパラメータ (Blockの色など）
	 */
	public EffectObjectSDL(int effect, int x, int y, int param) {
		this.effect = effect;
		this.x = x;
		this.y = y;
		this.param = param;
		anim = 0;
	}

	/**
	 * Copy constructor
	 * @param src Copy source
	 */
	public EffectObjectSDL(EffectObjectSDL src) {
		this.effect = src.effect;
		this.x = src.x;
		this.y = src.y;
		this.param = src.param;
		this.anim = src.anim;
	}
}
