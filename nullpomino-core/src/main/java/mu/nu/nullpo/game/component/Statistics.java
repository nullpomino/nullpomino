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

import mu.nu.nullpo.util.CustomProperties;

/**
 * ScoreInformation such as the
 */
public class Statistics implements Serializable {
	/** Serial version ID */
	private static final long serialVersionUID = -499640168205398295L;

	/** Total score */
	public int score;

	/** Line clear score */
	public int scoreFromLineClear;

	/** Soft drop score */
	public int scoreFromSoftDrop;

	/** Hard drop score */
	public int scoreFromHardDrop;

	/** I got in some other wayScore */
	public int scoreFromOtherBonus;

	/** Total line count */
	public int lines;

	/** Course time */
	public int time;

	/** Level */
	public int level;

	/** LevelAdded to the display ofcount (Display levelUse if it is different from the value of the internal) */
	public int levelDispAdd;

	/** I put the piececount */
	public int totalPieceLocked;

	/** The total operating pieces are time */
	public int totalPieceActiveTime;

	/** The total move the piece count */
	public int totalPieceMove;

	/** A piecerotationTotal to count */
	public int totalPieceRotate;

	/** 1-line clear count */
	public int totalSingle;

	/** 2-line clear count */
	public int totalDouble;

	/** 3-line clear count */
	public int totalTriple;

	/** 4-line clear count */
	public int totalFour;

	/** T-Spin 0 lines (with wallkick) count */
	public int totalTSpinZeroMini;

	/** T-Spin 0 lines (without wallkick) count */
	public int totalTSpinZero;

	/** T-Spin 1 line (with wallkick) count */
	public int totalTSpinSingleMini;

	/** T-Spin 1 line (without wallkick) count */
	public int totalTSpinSingle;

	/** T-Spin 2 line (with wallkick) count */
	public int totalTSpinDoubleMini;

	/** T-Spin 2 line (without wallkick) count */
	public int totalTSpinDouble;

	/** T-Spin 3 line count */
	public int totalTSpinTriple;

	/** Back to Back 4-line clear count */
	public int totalB2BFour;

	/** Back to Back T-Spin clear count */
	public int totalB2BTSpin;

	/** Hold use count */
	public int totalHoldUsed;

	/** Largest combo */
	public int maxCombo;

	/** 1LinesScore per (Score Per Line) */
	public double spl;

	/** 1Score per minute (Score Per Minute) */
	public double spm;

	/** 1Scores per second (Score Per Second) */
	public double sps;

	/** 1Per minuteLinescount (Lines Per Minute) */
	public float lpm;

	/** 1Per secondLinescount (Lines Per Second) */
	public float lps;

	/** 1Pieces per minutecount (Pieces Per Minute) */
	public float ppm;

	/** 1Per second piece ofcount (Pieces Per Second) */
	public float pps;

	/** TAS detection: slowdown rate */
	public float gamerate;

	/** Max chain */
	public int maxChain;

	/** Roll cleared flag (0=Died 1=Reached 2=Fully Survived) */
	public int rollclear;

	/**
	 * Constructor
	 */
	public Statistics() {
		reset();
	}

	/**
	 * Copy constructor
	 * @param s Copy source
	 */
	public Statistics(Statistics s) {
		copy(s);
	}

	/**
	 * Constructor that imports data from a String Array
	 * @param s String Array (String[37])
	 */
	public Statistics(String[] s) {
		importStringArray(s);
	}

	/**
	 * Constructor that imports data from a String
	 * @param s String (Split by ;)
	 */
	public Statistics(String s) {
		importString(s);
	}

	/**
	 * Reset to defaults
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
		spl = 0.0;
		spm = 0.0;
		sps = 0.0;
		lpm = 0f;
		lps = 0f;
		ppm = 0f;
		pps = 0f;
		gamerate = 0f;
		maxChain = 0;
		rollclear = 0;
	}

	/**
	 * OtherStatisticsCopy the value of the
	 * @param s Copy source
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
		rollclear = s.rollclear;
	}

	/**
	 * SPMYaLPMUpdates
	 */
	public void update() {
		if(lines > 0) {
			spl = (double)(score) / (double)(lines);
		}
		if(time > 0) {
			spm = (double)(score * 3600.0) / (double)(time);
			sps = (double)(score * 60.0) / (double)(time);
			lpm = (float)(lines * 3600f) / (float)(time);
			lps = (float)(lines * 60f) / (float)(time);
			ppm = (float)(totalPieceLocked * 3600f) / (float)(time);
			pps = (float)(totalPieceLocked * 60f) / (float)(time);
		}
	}

	/**
	 * Stored in the property set
	 * @param p Property Set
	 * @param id AnyID (Player IDEtc.)
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
		p.setProperty(id + ".statistics.rollclear", rollclear);

		// OldVersionFor compatibility with
		if(id == 0) {
			p.setProperty("result.score", score);
			p.setProperty("result.totallines", lines);
			p.setProperty("result.level", level);
			p.setProperty("result.time", time);
		}
	}

	/**
	 * Read from the property set
	 * @param p Property Set
	 * @param id AnyID (Player IDEtc.)
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
		rollclear = p.getProperty(id + ".statistics.rollclear", 0);
	}

	/**
	 * Import from String Array
	 * @param s String Array (String[38])
	 */
	public void importStringArray(String[] s) {
		score = Integer.parseInt(s[0]);
		scoreFromLineClear = Integer.parseInt(s[1]);
		scoreFromSoftDrop = Integer.parseInt(s[2]);
		scoreFromHardDrop = Integer.parseInt(s[3]);
		scoreFromOtherBonus = Integer.parseInt(s[4]);
		lines = Integer.parseInt(s[5]);
		time = Integer.parseInt(s[6]);
		level = Integer.parseInt(s[7]);
		levelDispAdd = Integer.parseInt(s[8]);
		totalPieceLocked = Integer.parseInt(s[9]);
		totalPieceActiveTime = Integer.parseInt(s[10]);
		totalPieceMove = Integer.parseInt(s[11]);
		totalPieceRotate = Integer.parseInt(s[12]);
		totalSingle = Integer.parseInt(s[13]);
		totalDouble = Integer.parseInt(s[14]);
		totalTriple = Integer.parseInt(s[15]);
		totalFour = Integer.parseInt(s[16]);
		totalTSpinZeroMini = Integer.parseInt(s[17]);
		totalTSpinZero = Integer.parseInt(s[18]);
		totalTSpinSingleMini = Integer.parseInt(s[19]);
		totalTSpinSingle = Integer.parseInt(s[20]);
		totalTSpinDoubleMini = Integer.parseInt(s[21]);
		totalTSpinDouble = Integer.parseInt(s[22]);
		totalTSpinTriple = Integer.parseInt(s[23]);
		totalB2BFour = Integer.parseInt(s[24]);
		totalB2BTSpin = Integer.parseInt(s[25]);
		totalHoldUsed = Integer.parseInt(s[26]);
		maxCombo = Integer.parseInt(s[27]);
		spl = Double.parseDouble(s[28]);
		spm = Double.parseDouble(s[29]);
		sps = Double.parseDouble(s[30]);
		lpm = Float.parseFloat(s[31]);
		lps = Float.parseFloat(s[32]);
		ppm = Float.parseFloat(s[33]);
		pps = Float.parseFloat(s[34]);
		gamerate = Float.parseFloat(s[35]);
		maxChain = Integer.parseInt(s[36]);
		if(s.length > 37) rollclear = Integer.parseInt(s[37]);
	}

	/**
	 * Import from String
	 * @param s String (Split by ;)
	 */
	public void importString(String s) {
		importStringArray(s.split(";"));
	}

	/**
	 * Export to String Array
	 * @return String Array (String[38])
	 */
	public String[] exportStringArray() {
		String[] s = new String[38];
		s[0] = Integer.toString(score);
		s[1] = Integer.toString(scoreFromLineClear);
		s[2] = Integer.toString(scoreFromSoftDrop);
		s[3] = Integer.toString(scoreFromHardDrop);
		s[4] = Integer.toString(scoreFromOtherBonus);
		s[5] = Integer.toString(lines);
		s[6] = Integer.toString(time);
		s[7] = Integer.toString(level);
		s[8] = Integer.toString(levelDispAdd);
		s[9] = Integer.toString(totalPieceLocked);
		s[10] = Integer.toString(totalPieceActiveTime);
		s[11] = Integer.toString(totalPieceMove);
		s[12] = Integer.toString(totalPieceRotate);
		s[13] = Integer.toString(totalSingle);
		s[14] = Integer.toString(totalDouble);
		s[15] = Integer.toString(totalTriple);
		s[16] = Integer.toString(totalFour);
		s[17] = Integer.toString(totalTSpinZeroMini);
		s[18] = Integer.toString(totalTSpinZero);
		s[19] = Integer.toString(totalTSpinSingleMini);
		s[20] = Integer.toString(totalTSpinSingle);
		s[21] = Integer.toString(totalTSpinDoubleMini);
		s[22] = Integer.toString(totalTSpinDouble);
		s[23] = Integer.toString(totalTSpinTriple);
		s[24] = Integer.toString(totalB2BFour);
		s[25] = Integer.toString(totalB2BTSpin);
		s[26] = Integer.toString(totalHoldUsed);
		s[27] = Integer.toString(maxCombo);
		s[28] = Double.toString(spl);
		s[29] = Double.toString(spm);
		s[30] = Double.toString(sps);
		s[31] = Float.toString(lpm);
		s[32] = Float.toString(lps);
		s[33] = Float.toString(ppm);
		s[34] = Float.toString(pps);
		s[35] = Float.toString(gamerate);
		s[36] = Integer.toString(maxChain);
		s[37] = Integer.toString(rollclear);
		return s;
	}

	/**
	 * Export to String
	 * @return String (Split by ;)
	 */
	public String exportString() {
		String[] array = exportStringArray();
		String result = "";
		for(int i = 0; i < array.length; i++) {
			if(i > 0) result += ";";
			result += array[i];
		}
		return result;
	}
}
