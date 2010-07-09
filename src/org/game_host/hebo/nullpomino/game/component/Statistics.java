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
package org.game_host.hebo.nullpomino.game.component;

import java.io.Serializable;

import org.game_host.hebo.nullpomino.util.CustomProperties;

/**
 * スコアなどの情報
 */
public class Statistics implements Serializable {
	/** シリアルバージョンID */
	private static final long serialVersionUID = -499640168205398295L;

	/** 合計スコア */
	public int score;

	/** ライン消去のスコア */
	public int scoreFromLineClear;

	/** ソフトドロップのスコア */
	public int scoreFromSoftDrop;

	/** ハードドロップのスコア */
	public int scoreFromHardDrop;

	/** その他の方法で手に入れたスコア */
	public int scoreFromOtherBonus;

	/** 合計ライン数 */
	public int lines;

	/** 経過時間 */
	public int time;

	/** レベル */
	public int level;

	/** レベルの表示に加算する数（表示レベルが内部の値と異なる場合に使用） */
	public int levelDispAdd;

	/** 置いたピースの数 */
	public int totalPieceLocked;

	/** ピースを操作していた合計時間 */
	public int totalPieceActiveTime;

	/** ピースを移動させた合計回数 */
	public int totalPieceMove;

	/** ピースを回転させた合計回数 */
	public int totalPieceRotate;

	/** 1ライン消し回数 */
	public int totalSingle;

	/** 2ライン消し回数 */
	public int totalDouble;

	/** 3ライン消し回数 */
	public int totalTriple;

	/** 4ライン消し回数 */
	public int totalFour;

	/** T-Spin0ライン（壁蹴りあり）回数 */
	public int totalTSpinZeroMini;

	/** T-Spin0ライン（壁蹴りなし）回数 */
	public int totalTSpinZero;

	/** T-Spin1ライン（壁蹴りあり）回数 */
	public int totalTSpinSingleMini;

	/** T-Spin1ライン（壁蹴りなし）回数 */
	public int totalTSpinSingle;

	/** T-Spin2ライン（壁蹴りあり）回数 */
	public int totalTSpinDoubleMini;

	/** T-Spin2ライン（壁蹴りなし）回数 */
	public int totalTSpinDouble;

	/** T-Spin3ライン回数 */
	public int totalTSpinTriple;

	/** Back to Back 4ライン消し回数 */
	public int totalB2BFour;

	/** Back to Back T-Spin消し回数 */
	public int totalB2BTSpin;

	/** ホールド使用回数 */
	public int totalHoldUsed;

	/** 最大コンボ数 */
	public int maxCombo;

	/** 1ラインあたりの得点（Score Per Line） */
	public double spl;

	/** 1分間あたりの得点（Score Per Minute） */
	public double spm;

	/** 1秒間あたりの得点（Score Per Second） */
	public double sps;

	/** 1分間あたりのライン数（Lines Per Minute） */
	public float lpm;

	/** 1秒間あたりのライン数（Lines Per Second） */
	public float lps;

	/** 1分間あたりのピース数（Pieces Per Minute） */
	public float ppm;

	/** 1秒間あたりのピース数（Pieces Per Second） */
	public float pps;

	/** TAS detection: slowdown rate */
	public float gamerate;

	/** Max chain */
	public int maxChain;

	/**
	 * コンストラクタ
	 */
	public Statistics() {
		reset();
	}

	/**
	 * コピーコンストラクタ
	 * @param s コピー元
	 */
	public Statistics(Statistics s) {
		copy(s);
	}

	/**
	 * 初期値に戻す
	 */
	public void reset() {
		score = 0;
		scoreFromLineClear = 0;
		scoreFromSoftDrop = 0;
		scoreFromHardDrop = 0;
		scoreFromOtherBonus = 0;
		lines = 0;
		time = 0;
		level = 0;
		levelDispAdd = 0;
		totalPieceLocked = 0;
		totalPieceActiveTime = 0;
		totalPieceMove = 0;
		totalPieceRotate = 0;
		totalSingle = 0;
		totalDouble = 0;
		totalTriple = 0;
		totalFour = 0;
		totalTSpinZeroMini = 0;
		totalTSpinZero = 0;
		totalTSpinSingleMini = 0;
		totalTSpinSingle = 0;
		totalTSpinDoubleMini = 0;
		totalTSpinDouble = 0;
		totalTSpinTriple = 0;
		totalB2BFour = 0;
		totalB2BTSpin = 0;
		totalHoldUsed = 0;
		maxCombo = 0;
		spl = 0f;
		spm = 0f;
		sps = 0f;
		lpm = 0f;
		lps = 0f;
		ppm = 0f;
		pps = 0f;
		gamerate = 0f;
		maxChain = 0;
	}

	/**
	 * 他のStatisticsの値をコピー
	 * @param s コピー元
	 */
	public void copy(Statistics s) {
		score = s.score;
		scoreFromLineClear = s.scoreFromLineClear;
		scoreFromSoftDrop = s.scoreFromSoftDrop;
		scoreFromHardDrop = s.scoreFromHardDrop;
		scoreFromOtherBonus = s.scoreFromOtherBonus;
		lines = s.lines;
		time = s.time;
		level = s.level;
		levelDispAdd = s.levelDispAdd;
		totalPieceLocked = s.totalPieceLocked;
		totalPieceActiveTime = s.totalPieceActiveTime;
		totalPieceMove = s.totalPieceMove;
		totalPieceRotate = s.totalPieceRotate;
		totalSingle = s.totalSingle;
		totalDouble = s.totalDouble;
		totalTriple = s.totalTriple;
		totalFour = s.totalFour;
		totalTSpinZeroMini = s.totalTSpinZeroMini;
		totalTSpinZero = s.totalTSpinZero;
		totalTSpinSingleMini = s.totalTSpinSingleMini;
		totalTSpinSingle = s.totalTSpinSingle;
		totalTSpinDoubleMini = s.totalTSpinDoubleMini;
		totalTSpinDouble = s.totalTSpinDouble;
		totalTSpinTriple = s.totalTSpinTriple;
		totalB2BFour = s.totalB2BFour;
		totalB2BTSpin = s.totalB2BTSpin;
		maxCombo = s.maxCombo;
		spl = s.spl;
		spm = s.spm;
		sps = s.sps;
		lpm = s.lpm;
		lps = s.lps;
		ppm = s.ppm;
		pps = s.pps;
		gamerate = s.gamerate;
		maxChain = s.maxChain;
	}

	/**
	 * SPMやLPMの更新
	 */
	public void update() {
		if(lines > 0) {
			spl = (double)(score) / (double)(lines);
		}
		if(time > 0) {
			spm = (double)(score * 3600) / (double)(time);
			sps = (double)(score * 60) / (double)(time);
			lpm = (float)(lines * 3600) / (float)(time);
			lps = (float)(lines * 60) / (float)(time);
			ppm = (float)(totalPieceLocked * 3600) / (float)(time);
			pps = (float)(totalPieceLocked * 60) / (float)(time);
		}
	}

	/**
	 * プロパティセットに保存
	 * @param p プロパティセット
	 * @param id 任意のID（プレイヤーIDなど）
	 */
	public void writeProperty(CustomProperties p, int id) {
		p.setProperty(id + ".statistics.score", score);
		p.setProperty(id + ".statistics.scoreFromLineClear", scoreFromLineClear);
		p.setProperty(id + ".statistics.scoreFromSoftDrop", scoreFromSoftDrop);
		p.setProperty(id + ".statistics.scoreFromHardDrop", scoreFromHardDrop);
		p.setProperty(id + ".statistics.scoreFromOtherBonus", scoreFromOtherBonus);
		p.setProperty(id + ".statistics.lines", lines);
		p.setProperty(id + ".statistics.time", time);
		p.setProperty(id + ".statistics.level", level);
		p.setProperty(id + ".statistics.levelDispAdd", levelDispAdd);
		p.setProperty(id + ".statistics.totalPieceLocked", totalPieceLocked);
		p.setProperty(id + ".statistics.totalPieceActiveTime", totalPieceActiveTime);
		p.setProperty(id + ".statistics.totalPieceMove", totalPieceMove);
		p.setProperty(id + ".statistics.totalPieceRotate", totalPieceRotate);
		p.setProperty(id + ".statistics.totalSingle", totalSingle);
		p.setProperty(id + ".statistics.totalDouble", totalDouble);
		p.setProperty(id + ".statistics.totalTriple", totalTriple);
		p.setProperty(id + ".statistics.totalFour", totalFour);
		p.setProperty(id + ".statistics.totalTSpinZeroMini", totalTSpinZeroMini);
		p.setProperty(id + ".statistics.totalTSpinZero", totalTSpinZero);
		p.setProperty(id + ".statistics.totalTSpinSingleMini", totalTSpinSingleMini);
		p.setProperty(id + ".statistics.totalTSpinSingle", totalTSpinSingle);
		p.setProperty(id + ".statistics.totalTSpinDoubleMini", totalTSpinDoubleMini);
		p.setProperty(id + ".statistics.totalTSpinDouble", totalTSpinDouble);
		p.setProperty(id + ".statistics.totalTSpinTriple", totalTSpinTriple);
		p.setProperty(id + ".statistics.totalB2BFour", totalB2BFour);
		p.setProperty(id + ".statistics.totalB2BTSpin", totalB2BTSpin);
		p.setProperty(id + ".statistics.totalHoldUsed", totalHoldUsed);
		p.setProperty(id + ".statistics.maxCombo", maxCombo);
		p.setProperty(id + ".statistics.spl", spl);
		p.setProperty(id + ".statistics.spm", spm);
		p.setProperty(id + ".statistics.sps", sps);
		p.setProperty(id + ".statistics.lpm", lpm);
		p.setProperty(id + ".statistics.lps", lps);
		p.setProperty(id + ".statistics.ppm", ppm);
		p.setProperty(id + ".statistics.pps", pps);
		p.setProperty(id + ".statistics.gamerate", gamerate);
		p.setProperty(id + ".statistics.maxChain", maxChain);

		// 旧バージョンとの互換用
		if(id == 0) {
			p.setProperty("result.score", score);
			p.setProperty("result.totallines", lines);
			p.setProperty("result.level", level);
			p.setProperty("result.time", time);
		}
	}

	/**
	 * プロパティセットから読み込み
	 * @param p プロパティセット
	 * @param id 任意のID（プレイヤーIDなど）
	 */
	public void readProperty(CustomProperties p, int id) {
		score = p.getProperty(id + ".statistics.score", 0);
		scoreFromLineClear = p.getProperty(id + ".statistics.scoreFromLineClear", 0);
		scoreFromSoftDrop = p.getProperty(id + ".statistics.scoreFromSoftDrop", 0);
		scoreFromHardDrop = p.getProperty(id + ".statistics.scoreFromHardDrop", 0);
		scoreFromOtherBonus = p.getProperty(id + ".statistics.scoreFromOtherBonus", 0);
		lines = p.getProperty(id + ".statistics.lines", 0);
		time = p.getProperty(id + ".statistics.time", 0);
		level = p.getProperty(id + ".statistics.level", 0);
		levelDispAdd = p.getProperty(id + ".statistics.levelDispAdd", 0);
		totalPieceLocked = p.getProperty(id + ".statistics.totalPieceLocked", 0);
		totalPieceActiveTime = p.getProperty(id + ".statistics.totalPieceActiveTime", 0);
		totalPieceMove = p.getProperty(id + ".statistics.totalPieceMove", 0);
		totalPieceRotate = p.getProperty(id + ".statistics.totalPieceRotate", 0);
		totalSingle = p.getProperty(id + ".statistics.totalSingle", 0);
		totalDouble = p.getProperty(id + ".statistics.totalDouble", 0);
		totalTriple = p.getProperty(id + ".statistics.totalTriple", 0);
		totalFour = p.getProperty(id + ".statistics.totalFour", 0);
		totalTSpinZeroMini = p.getProperty(id + ".statistics.totalTSpinZeroMini", 0);
		totalTSpinZero = p.getProperty(id + ".statistics.totalTSpinZero", 0);
		totalTSpinSingleMini = p.getProperty(id + ".statistics.totalTSpinSingleMini", 0);
		totalTSpinSingle = p.getProperty(id + ".statistics.totalTSpinSingle", 0);
		totalTSpinDoubleMini = p.getProperty(id + ".statistics.totalTSpinDoubleMini", 0);
		totalTSpinDouble = p.getProperty(id + ".statistics.totalTSpinDouble", 0);
		totalTSpinTriple = p.getProperty(id + ".statistics.totalTSpinTriple", 0);
		totalB2BFour = p.getProperty(id + ".statistics.totalB2BFour", 0);
		totalB2BTSpin = p.getProperty(id + ".statistics.totalB2BTSpin", 0);
		totalHoldUsed = p.getProperty(id + ".statistics.totalHoldUsed", 0);
		maxCombo = p.getProperty(id + ".statistics.maxCombo", 0);
		spl = p.getProperty(id + ".statistics.spl", 0f);
		spm = p.getProperty(id + ".statistics.spm", 0f);
		sps = p.getProperty(id + ".statistics.sps", 0f);
		lpm = p.getProperty(id + ".statistics.lpm", 0f);
		lps = p.getProperty(id + ".statistics.lps", 0f);
		ppm = p.getProperty(id + ".statistics.ppm", 0f);
		pps = p.getProperty(id + ".statistics.pps", 0f);
		gamerate = p.getProperty(id + ".statistics.gamerate", 0f);
		maxChain = p.getProperty(id + ".statistics.maxChain", 0);
	}
}
