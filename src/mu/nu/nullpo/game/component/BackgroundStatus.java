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
package mu.nu.nullpo.game.component;

import java.io.Serializable;

/**
 * 背景画像の状態
 */
public class BackgroundStatus implements Serializable {
	/** Serial version ID */
	private static final long serialVersionUID = 2159669210087818385L;

	/** 背景 number */
	public int bg;

	/** 背景フェード flag */
	public boolean fadesw;

	/** 背景フェードの状態 (falseならフェードアウト、trueならフェードイン) */
	public boolean fadestat;

	/** 背景フェード用カウンタ */
	public int fadecount;

	/** 背景フェード後の背景 */
	public int fadebg;

	/**
	 *  default Constructor
	 */
	public BackgroundStatus() {
		reset();
	}

	/**
	 * Copy constructor
	 * @param b Copy source
	 */
	public BackgroundStatus(BackgroundStatus b) {
		copy(b);
	}

	/**
	 * 初期値に戻す
	 */
	public void reset() {
		bg = 0;
		fadesw = false;
		fadestat = false;
		fadecount = 0;
		fadebg = 0;
	}

	/**
	 * 別のBackgroundStatusからコピー
	 * @param b Copy source
	 */
	public void copy(BackgroundStatus b) {
		bg = b.bg;
		fadesw = b.fadesw;
		fadestat = b.fadestat;
		fadecount = b.fadecount;
		fadebg = b.fadebg;
	}

	/**
	 * 背景フェードの状態を更新
	 */
	public void fadeUpdate() {
		if(fadesw == true) {
			if(fadecount < 100) {
				fadecount += 10;
			} else {
				if(fadestat == false) {
					bg = fadebg;
					fadestat = true;
					fadecount = 0;
				} else {
					fadesw = false;
					fadestat = false;
					fadecount = 0;
				}
			}
		}
	}
}
