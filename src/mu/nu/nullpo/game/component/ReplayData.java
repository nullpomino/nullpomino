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
import java.util.ArrayList;

import mu.nu.nullpo.util.CustomProperties;

/**
 * リプレイで使用する button input dataのクラス
 */
public class ReplayData implements Serializable {
	/** Serial version ID */
	private static final long serialVersionUID = 737226985994393117L;

	/**  button input dataの default の長さ */
	public static final int DEFAULT_ARRAYLIST_SIZE = 60 * 60 * 10;

	/**  button input data */
	public ArrayList<Integer> inputDataArray;

	/**
	 *  default Constructor
	 */
	public ReplayData() {
		reset();
	}

	/**
	 * Copy constructor
	 * @param r Copy source
	 */
	public ReplayData(ReplayData r) {
		copy(r);
	}

	/**
	 * 初期値に戻す
	 */
	public void reset() {
		if(inputDataArray == null)
			inputDataArray = new ArrayList<Integer>(DEFAULT_ARRAYLIST_SIZE);
		else
			inputDataArray.clear();
	}

	/**
	 * 他のReplayDataからコピー
	 * @param r Copy source
	 */
	public void copy(ReplayData r) {
		reset();

		for(int i = 0; i < r.inputDataArray.size(); i++) {
			inputDataArray.add(i, r.inputDataArray.get(i));
		}
	}

	/**
	 *  button input状況を設定
	 * @param input  button input状況のビット flag
	 * @param frame  frame  (経過 time）
	 */
	public void setInputData(int input, int frame) {
		if((frame < 0) || (frame >= inputDataArray.size())) {
			inputDataArray.add(input);
		} else {
			inputDataArray.set(frame, input);
		}
	}

	/**
	 *  button input状況を取得
	 * @param frame  frame  (経過 time）
	 * @return  button input状況のビット flag
	 */
	public int getInputData(int frame) {
		if((frame < 0) || (frame >= inputDataArray.size())) {
			return 0;
		}
		return inputDataArray.get(frame);
	}

	/**
	 * プロパティセットに保存
	 * @param p プロパティセット
	 * @param id 任意のID (Player IDなど）
	 * @param maxFrame 保存する frame count (-1で全部保存）
	 */
	public void writeProperty(CustomProperties p, int id, int maxFrame) {
		int max = maxFrame;
		if((maxFrame < 0) || (maxFrame > inputDataArray.size())) max = inputDataArray.size();

		for(int i = 0; i < max; i++) {
			int input = getInputData(i);
			int previous = getInputData(i - 1);
			if(input != previous) p.setProperty(id + ".r." + i, input);
		}
		p.setProperty(id + ".r.max", max);
	}

	/**
	 * プロパティセットから読み込み
	 * @param p プロパティセット
	 * @param id 任意のID (Player IDなど）
	 */
	public void readProperty(CustomProperties p, int id) {
		reset();
		int max = p.getProperty(id + ".r.max", 0);
		int input = 0;

		for(int i = 0; i < max; i++) {
			int data = p.getProperty(id + ".r." + i, -1);
			if(data != -1) input = data;
			setInputData(input, i);
		}
	}
}
