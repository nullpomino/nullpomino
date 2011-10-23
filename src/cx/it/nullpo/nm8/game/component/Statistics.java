package cx.it.nullpo.nm8.game.component;

import java.io.Serializable;

import cx.it.nullpo.nm8.util.CustomProperties;

/**
 * Game statistics
 */
public class Statistics implements Serializable {
	/** Serial version ID */
	private static final long serialVersionUID = -499640168205398295L;

	/** Total score */
	public long score;

	/** Line clear score */
	public long scoreFromLineClear;

	/** Soft drop score */
	public long scoreFromSoftDrop;

	/** Hard drop score */
	public long scoreFromHardDrop;

	/** Points from other bonuses */
	public long scoreFromOtherBonus;

	/** Total line count */
	public int lines;

	/** Play time */
	public long time;

	/** Level */
	public int level;

	/** Number of levels to add to display (Used when internal level is different from the displayed one) */
	public int levelDispAdd;

	/** Number of pieces locked */
	public int totalPieceLocked;

	/** Total piece active time */
	public long totalPieceActiveTime;

	/** Number of times the piece moved */
	public int totalPieceMove;

	/** Number of times the piece rotated */
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

	/** Score Per Line */
	public double spl;

	/** Score Per Minute */
	public double spm;

	/** Score Per Second */
	public double sps;

	/** Lines Per Minute */
	public float lpm;

	/** Lines Per Second */
	public float lps;

	/** Pieces Per Minute */
	public float ppm;

	/** Pieces Per Second */
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
	 * Copy from other Statistics
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

	public void update() {
		if(lines > 0) {
			spl = (double)(score) / (double)(lines);
		}
		if(time > 0) {
			spm = (double)(score * 60000.0) / (double)(time);
			sps = (double)(score * 1000.0) / (double)(time);
			lpm = (float)(lines * 60000f) / (float)(time);
			lps = (float)(lines * 1000f) / (float)(time);
			ppm = (float)(totalPieceLocked * 60000f) / (float)(time);
			pps = (float)(totalPieceLocked * 1000f) / (float)(time);
		}
	}

	/**
	 * Write to a CustomProperties
	 * @param p CustomProperties
	 * @param id ID
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

		// For old version
		if(id == 0) {
			p.setProperty("result.score", score);
			p.setProperty("result.totallines", lines);
			p.setProperty("result.level", level);
			p.setProperty("result.time", time);
		}
	}

	/**
	 * Read from a CustomProperties
	 * @param p CustomProperties
	 * @param id ID
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
		time = Long.parseLong(s[6]);
		level = Integer.parseInt(s[7]);
		levelDispAdd = Integer.parseInt(s[8]);
		totalPieceLocked = Integer.parseInt(s[9]);
		totalPieceActiveTime = Long.parseLong(s[10]);
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
		s[0] = String.valueOf(score);
		s[1] = String.valueOf(scoreFromLineClear);
		s[2] = String.valueOf(scoreFromSoftDrop);
		s[3] = String.valueOf(scoreFromHardDrop);
		s[4] = String.valueOf(scoreFromOtherBonus);
		s[5] = String.valueOf(lines);
		s[6] = String.valueOf(time);
		s[7] = String.valueOf(level);
		s[8] = String.valueOf(levelDispAdd);
		s[9] = String.valueOf(totalPieceLocked);
		s[10] = String.valueOf(totalPieceActiveTime);
		s[11] = String.valueOf(totalPieceMove);
		s[12] = String.valueOf(totalPieceRotate);
		s[13] = String.valueOf(totalSingle);
		s[14] = String.valueOf(totalDouble);
		s[15] = String.valueOf(totalTriple);
		s[16] = String.valueOf(totalFour);
		s[17] = String.valueOf(totalTSpinZeroMini);
		s[18] = String.valueOf(totalTSpinZero);
		s[19] = String.valueOf(totalTSpinSingleMini);
		s[20] = String.valueOf(totalTSpinSingle);
		s[21] = String.valueOf(totalTSpinDoubleMini);
		s[22] = String.valueOf(totalTSpinDouble);
		s[23] = String.valueOf(totalTSpinTriple);
		s[24] = String.valueOf(totalB2BFour);
		s[25] = String.valueOf(totalB2BTSpin);
		s[26] = String.valueOf(totalHoldUsed);
		s[27] = String.valueOf(maxCombo);
		s[28] = String.valueOf(spl);
		s[29] = String.valueOf(spm);
		s[30] = String.valueOf(sps);
		s[31] = String.valueOf(lpm);
		s[32] = String.valueOf(lps);
		s[33] = String.valueOf(ppm);
		s[34] = String.valueOf(pps);
		s[35] = String.valueOf(gamerate);
		s[36] = String.valueOf(maxChain);
		s[37] = String.valueOf(rollclear);
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
