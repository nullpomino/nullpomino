package net.tetrisconcept.poochy.nullpomino.ai;

import mu.nu.nullpo.game.component.Field;
import mu.nu.nullpo.game.component.Piece;
import mu.nu.nullpo.game.component.WallkickResult;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.game.subsystem.ai.DummyAI;
import mu.nu.nullpo.game.subsystem.wallkick.StandardWallkick;
import net.omegaboshi.nullpomino.game.subsystem.randomizer.BagNoSZORandomizer;

import org.apache.log4j.Logger;

public class ComboRaceSeedSearch extends DummyAI {
	/** Log (Apache log4j) */
	static Logger log = Logger.getLogger(ComboRaceSeedSearch.class);

	/** List of field state codes which are possible to sustain a stable combo */
	private static final int[] FIELDS = {
		0x7, 0xB, 0xD, 0xE,
		0x13, 0x15, 0x16, 0x19, 0x1A, 0x1C,
		0x23, 0x29,
		0x31, 0x32,
		0x49, 0x4C,
		0x61, 0x68,
		0x83, 0x85, 0x86, 0x89, 0x8A, 0x8C,
		0xC4, 0xC8,
		0x111, 0x888
	};

	/** Number of pieces to think ahead */
	protected static final int MAX_THINK_DEPTH = 6;
	/** Length of piece queue */
	protected static final int QUEUE_SIZE = 1400;

	protected static int[] stateScores = {6, 7, 7, 6, 8, 3, 2, 9, 3, 4, 3, 1, 8, 4, 1, 3, 1, 1, 4, 3, 9, 2, 3, 8, 4, 8, 3, 3};

	protected static int[] pieceScores = {28, 18, 10, 9, 18, 18, 9};
	protected static Transition[][] moves;
	protected static int[] nextQueueIDs;
	protected static int[] queue;
	public static boolean bestHold;
	public static int bestPts;
	public static int bestNext;

    public static void main(String[] args)
    {
    	createTables();
    	long bestSeed = 0l;
    	int bestResult = 0;
    	int result, pos, fld, holdID;
		nextQueueIDs = new int[MAX_THINK_DEPTH];
		boolean[] nextPieceEnable = new boolean[Piece.PIECE_COUNT];
		for(int i = 0; i < Piece.PIECE_STANDARD_COUNT; i++) nextPieceEnable[i] = true;
		BagNoSZORandomizer rand = new BagNoSZORandomizer();
		rand.setPieceEnable(nextPieceEnable);
		
    	for (long seed = 0l; seed < Long.MAX_VALUE; seed++)
    	{
    		fld = 0x13;
    		rand = new BagNoSZORandomizer();
    		rand.setState(nextPieceEnable, seed);
			queue = new int[QUEUE_SIZE];
			for (int i = 0; i < queue.length; i++)
				queue[i] = rand.next();
			result = 0;
			pos = 0;
			holdID = -1;
			while (true)
			{
				thinkBestPosition(fld, pos, holdID);
				if (bestNext == -1)
					break;
				fld = bestNext;
				if (bestHold)
				{
					if (holdID == -1)
					{
						holdID = queue[pos%queue.length];
						pos++;
					}
					else
						holdID = queue[pos%queue.length];
				}
				pos++;
				result++;
				if (result > 8l*QUEUE_SIZE*FIELDS.length)
				{
					System.out.println("Endless loop found! Seed = " + Long.toString(seed, 16));
					break;
				}
			}
			if (result > bestResult)
			{
				bestSeed = seed;
				bestResult = result;
				System.out.println("New best result: seed = " + Long.toString(bestSeed, 16)
						+ ", result = " + bestResult);
			}
    	}
    }
	/**
	 * Search for the best choice
	 * @param engine The GameEngine that owns this AI
	 * @param playerID Player ID
	 */
	public static void thinkBestPosition(int state, int nextIndex, int holdID) {
		if (state < 0)
			return;

		bestPts = Integer.MIN_VALUE;
		bestNext = -1;

		int nowID = queue[nextIndex%queue.length];
		nextIndex++;
		for (int i = 0; i < nextQueueIDs.length; i++)
			nextQueueIDs[i] = queue[(nextIndex+i)%queue.length];

		Transition t = moves[state][nowID];

		while (t != null)
		{
			int pts = thinkMain(t.newField, holdID, 0);

			if (pts > bestPts)
			{
				bestPts = pts;
				bestNext = t.newField;
				bestHold = false;
			}

			t = t.next;
		}
		if (holdID != nowID)
		{
			t = moves[state][holdID == -1 ? nextQueueIDs[0] : holdID];

			while (t != null)
			{
				int pts = thinkMain(t.newField, nowID, (holdID == -1) ? 1 : 0);

				if (pts > bestPts)
				{
					bestPts = pts;
					bestNext = t.newField;
					bestHold = true;
				}

				t = t.next;
			}
		}


		//System.out.println("X:" + bestX + " Y:" + bestY + " R:" + bestRt + " H:" + bestHold + " Pts:" + bestPts);
	}

	/**
	 * Think routine
	 * @param engine GameEngine
	 * @param state Think state
	 * @param holdID Hold piece ID
	 * @param depth Search depth
	 * @return Evaluation score
	 */
	public static int thinkMain(int state, int holdID, int depth) {
		if (state == -1)
			return 0;
		if (depth == nextQueueIDs.length)
		{
			int result = stateScores[state]*100;
			if (holdID == Piece.PIECE_I)
				result += 1000;
			else if (holdID >= 0 && holdID < pieceScores.length)
				result += pieceScores[holdID]*100/28;
			return result;
		}

		int bestPts = 0;
		Transition t = moves[state][nextQueueIDs[depth]];

		while (t != null)
		{
			bestPts = Math.max(bestPts,
					thinkMain(t.newField, holdID, depth+1) + 1000);
			t = t.next;
		}

		if (holdID == -1)
			bestPts = Math.max(bestPts, thinkMain(state, nextQueueIDs[depth], depth+1));
		else
		{
			t = moves[state][holdID];
			while (t != null)
			{
				bestPts = Math.max(bestPts,
						thinkMain(t.newField, nextQueueIDs[depth], depth+1) + 1000);
				t = t.next;
			}
		}

		return bestPts;
	}

	public static Piece checkOffset(Piece p, GameEngine engine)
	{
		Piece result = new Piece(p);
		result.big = engine.big;
		if (!p.offsetApplied)
			result.applyOffsetArray(engine.ruleopt.pieceOffsetX[p.id], engine.ruleopt.pieceOffsetY[p.id]);
		return result;
	}

	/**
	 * Constructs the moves table if necessary.
	 */
	public static void createTables ()
	{
		if (moves != null)
			return;
		
		StandardWallkick wallkick = new StandardWallkick();

		moves = new Transition[FIELDS.length][7];

		Field fldEmpty = new Field(4, Field.DEFAULT_HEIGHT, Field.DEFAULT_HIDDEN_HEIGHT);
		Field fldBackup = new Field(fldEmpty);
		Field fldTemp = new Field(fldEmpty);

		Piece[] pieces = new Piece[7];
		for (int p = 0; p < 7; p++)
		{
			pieces[p] = new Piece(p);
			pieces[p].setColor(1);
		}

		int count = 0;

		for (int i = 0; i < FIELDS.length; i++)
		{
			fldBackup.copy(fldEmpty);
			int code = FIELDS[i];

			for (int y = Field.DEFAULT_HEIGHT-1; y > Field.DEFAULT_HEIGHT-4; y--)
				for (int x = 3; x >= 0; x--)
				{
					if ((code & 1) == 1)
						fldBackup.setBlockColor(x, y, 1);
					code >>= 1;
				}

			for (int p = 0; p < 7; p++)
			{
				int tempX = -1 + (fldBackup.getWidth() - pieces[p].getWidth() + 1) / 2;
				for (int rt = 0; rt < Piece.DIRECTION_COUNT; rt++)
				{
					int minX = pieces[p].getMostMovableLeft(tempX, 0, rt, fldBackup);
					int maxX = pieces[p].getMostMovableRight(tempX, 0, rt, fldBackup);

					for (int x = minX; x <= maxX; x++)
					{
						int y = pieces[p].getBottom(x, 0, rt, fldBackup);
						if (p == Piece.PIECE_L || p == Piece.PIECE_T || p == Piece.PIECE_J || rt < 2)
						{
							fldTemp.copy(fldBackup);
							pieces[p].placeToField(x, y, rt, fldTemp);
							if (fldTemp.checkLine() == 1)
							{
								fldTemp.clearLine();
								fldTemp.downFloatingBlocks();
								int index = fieldToIndex(fldTemp, 0);
								if (index >= 0)
								{
									moves[i][p] = new Transition(x, rt, index, moves[i][p]);
									count++;
								}
							}
							if (p == Piece.PIECE_O)
								continue;
						}

						// Left rotation
						int rot = pieces[p].getRotateDirection(-1, rt);
						int newX = x;
						int newY = y;
						fldTemp.copy(fldBackup);

						if(pieces[p].checkCollision(x, y, rot, fldTemp)) {
							WallkickResult kick = wallkick.executeWallkick(x, y, -1, rt, rot,
									true, pieces[p], fldTemp, null);

							if(kick != null) {
								newX = x + kick.offsetX;
								newY = pieces[p].getBottom(newX, y + kick.offsetY, rot, fldTemp);
							}
						}
						if (!pieces[p].checkCollision(newX, newY, rot, fldTemp)
								&& newY > pieces[p].getBottom(newX, 0, rot, fldTemp))
						{
							pieces[p].placeToField(newX, newY, rot, fldTemp);
							if (fldTemp.checkLine() == 1)
							{
								fldTemp.clearLine();
								fldTemp.downFloatingBlocks();
								int index = fieldToIndex(fldTemp, 0);
								if (index >= 0)
								{
									moves[i][p] = new Transition(x, rt, -1, index, moves[i][p]);
									count++;
								}
							}
						}

						// Right rotation
						rot = pieces[p].getRotateDirection(1, rt);
						newX = x;
						newY = y;
						fldTemp.copy(fldBackup);

						if(pieces[p].checkCollision(x, y, rot, fldTemp)) {
							WallkickResult kick = wallkick.executeWallkick(x, y, 1, rt, rot,
									true, pieces[p], fldTemp, null);

							if(kick != null) {
								newX = x + kick.offsetX;
								newY = pieces[p].getBottom(newX, y + kick.offsetY, rot, fldTemp);
							}
						}
						if (!pieces[p].checkCollision(newX, newY, rot, fldTemp)
								&& newY > pieces[p].getBottom(newX, 0, rot, fldTemp))
						{
							pieces[p].placeToField(newX, newY, rot, fldTemp);
							if (fldTemp.checkLine() == 1)
							{
								fldTemp.clearLine();
								fldTemp.downFloatingBlocks();
								int index = fieldToIndex(fldTemp, 0);
								if (index >= 0)
								{
									moves[i][p] = new Transition(x, rt, 1, index, moves[i][p]);
									count++;
								}
							}
						}
					}

					if (pieces[p].id == Piece.PIECE_O)
						break;
				}
			}
		}
		//log.debug("Transition table created. Total entries: " + count);
		//TODO: PageRank scores for each state
	}

	/**
	 * Converts field to field state int code
	 * @param field Field object
	 * @param valleyX Leftmost x-coordinate of 4-block-wide valley to combo in
	 * @return Field state int code.
	 */
	public static int fieldToCode(Field field, int valleyX)
	{
		int height = field.getHeight();
		int result = 0;
		for (int y = height-3; y < height; y++)
			for (int x = 0; x < 4; x++)
			{
				result <<= 1;
				if (!field.getBlockEmptyF(x+valleyX, y))
					result++;
			}
		return result;
	}
	public static int fieldToCode(Field field)
	{
		return fieldToCode(field, 3);
	}

	/**
	 * Converts field state int code to FIELDS array index
	 * @param field Field state int code
	 * @return State index if found; -1 if not found.
	 */
	public static int fieldToIndex(int field)
	{
		int min = 0;
		int max = FIELDS.length-1;
		int mid;
		while (min <= max)
		{
			mid = (min+max) >> 1;
			if (FIELDS[mid] > field)
				max = mid-1;
			else if (FIELDS[mid] < field)
				min = mid+1;
			else
				return mid;
		}
		return -1;
	}

	/**
	 * Converts field object to FIELDS array index
	 * @param field Field object
	 * @param valleyX Leftmost x-coordinate of 4-block-wide valley to combo in
	 * @return State index if found; -1 if not found.
	 */
	public static int fieldToIndex(Field field, int valleyX)
	{
		return fieldToIndex(fieldToCode(field, valleyX));
	}
	public static int fieldToIndex(Field field)
	{
		return fieldToIndex(fieldToCode(field));
	}
	
	protected static class Transition
	{
		public int x, rt, rtSub, newField;
		public Transition next;
		public Transition (int bestX, int bestRt, int bestRtSub, int newFld)
		{
			x = bestX;
			rt = bestRt;
			rtSub = bestRtSub;
			newField = newFld;
		}
		public Transition (int bestX, int bestRt, int newFld)
		{
			x = bestX;
			rt = bestRt;
			rtSub = 0;
			newField = newFld;
		}
		public Transition (int bestX, int bestRt, int bestRtSub, int newFld, Transition nxt)
		{
			x = bestX;
			rt = bestRt;
			rtSub = bestRtSub;
			newField = newFld;
			next = nxt;
		}
		public Transition (int bestX, int bestRt, int newFld, Transition nxt)
		{
			x = bestX;
			rt = bestRt;
			rtSub = 0;
			newField = newFld;
			next = nxt;
		}
	}
}
