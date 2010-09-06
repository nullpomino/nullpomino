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
 * 音楽の再生状況を管理するクラス
 */
public class BGMStatus implements Serializable {
	/** Serial version ID */
	private static final long serialVersionUID = -1003092972570497408L;

	/** 音楽の定count */
	public static final int BGM_NOTHING = -1,
							BGM_NORMAL1 = 0,
							BGM_NORMAL2 = 1,
							BGM_NORMAL3 = 2,
							BGM_NORMAL4 = 3,
							BGM_NORMAL5 = 4,
							BGM_NORMAL6 = 5,
							BGM_PUZZLE1 = 6,
							BGM_PUZZLE2 = 7,
							BGM_PUZZLE3 = 8,
							BGM_PUZZLE4 = 9,
							BGM_ENDING1 = 10,
							BGM_ENDING2 = 11,
							BGM_SPECIAL1 = 12,
							BGM_SPECIAL2 = 13,
							BGM_SPECIAL3 = 14,
							BGM_SPECIAL4 = 15;

	/** 音楽のMaximumcount */
	public static final int BGM_COUNT = 16;

	/** Current BGM number */
	public int bgm;

	/** 音量 (1f=100%、0.5f=50%) */
	public float volume;

	/** BGMフェードアウトスイッチ */
	public boolean fadesw;

	/**
	 * Constructor
	 */
	public BGMStatus() {
		reset();
	}

	/**
	 * Copy constructor
	 * @param b Copy source
	 */
	public BGMStatus(BGMStatus b) {
		copy(b);
	}

	/**
	 * 初期値に戻す
	 */
	public void reset() {
		bgm = BGM_NOTHING;
		volume = 1f;
		fadesw = false;
	}

	/**
	 * 他のBGMStatusからコピー
	 * @param b Copy source
	 */
	public void copy(BGMStatus b) {
		bgm = b.bgm;
		volume = b.volume;
		fadesw = b.fadesw;
	}

	/**
	 * BGMフェード状態と音量の更新
	 */
	public void fadeUpdate() {
		if(fadesw == true) {
			if(volume > 0f) {
				volume -= 0.005f;
			} else if(volume < 0f) {
				volume = 0f;
			}
		} else {
			if(volume < 1f) {
				volume = 1f;
			}
		}
	}
}
