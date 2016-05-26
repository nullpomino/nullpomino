package net.tetrisconcept.poochy.nullpomino.ai;

import mu.nu.nullpo.game.component.Controller;
import mu.nu.nullpo.game.component.Field;
import mu.nu.nullpo.game.component.Piece;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.game.subsystem.ai.DummyAI;

import org.apache.log4j.Logger;

/**
 * Nohoho AI
 * @author Poochy.EXE
 *         Poochy.Spambucket@gmail.com
 */
public class Nohoho extends DummyAI implements Runnable {
	/** Log */
	static Logger log = Logger.getLogger(Nohoho.class);

	/** 接地したあとのX-coordinate */
	public int bestXSub;

	/** 接地したあとのY-coordinate */
	public int bestYSub;

	/** 接地したあとのDirection(-1: None) */
	public int bestRtSub;

	/** 最善手のEvaluation score */
	public int bestPts;

	/** 移動を遅らせる用の変count */
	public int delay;

	/** The GameEngine that owns this AI */
	public GameEngine gEngine;

	/** The GameManager that owns this AI */
	public GameManager gManager;

	/** When true,スレッドにThink routineの実行を指示 */
	public ThinkRequestMutex thinkRequest;

	/** true when thread is executing the think routine. */
	public boolean thinking;

	/** スレッドを停止させる time */
	public int thinkDelay;

	/** When true,スレッド動作中 */
	public volatile boolean threadRunning;

	/** Thread for executing the think routine */
	public Thread thread;

	/** Number of frames for which piece has been stuck */
	protected int stuckDelay;

	/** Status of last frame */
	protected int lastInput, lastX, lastY, lastRt;
	/** Number of consecutive frames with same piece status */
	protected int sameStatusTime;
	/** DAS charge status. -1 = left, 0 = none, 1 = right */
	protected int setDAS;
	/** Last input if done in ARE */
	protected int inputARE;
	/** Maximum妥協 level */
	protected static final int MAX_THINK_DEPTH = 2;
	/** Set to true to print debug information */
	protected static final boolean DEBUG_ALL = true;
	/** Did the thinking thread finish successfully? */
	protected boolean thinkComplete;
	/** Did the thinking thread find a possible position? */
	protected boolean thinkSuccess;
	/** Was the game in ARE as of the last frame? */
	protected boolean inARE;

	/*
	 * AI's name
	 */
	public String getName() {
		return "Avalanche-R V0.01";
	}

	/*
	 * Called at initialization
	 */
	public void init(GameEngine engine, int playerID) {
		delay = 0;
		gEngine = engine;
		gManager = engine.owner;
		thinkRequest = new ThinkRequestMutex();
		thinking = false;
		threadRunning = false;
		setDAS = 0;

		stuckDelay = 0;
		inputARE = 0;
		lastInput = 0;
		lastX = -1;
		lastY = -1;
		lastRt = -1;
		sameStatusTime = 0;
		thinkComplete = false;
		thinkSuccess = false;
		inARE = false;

		if( ((thread == null) || !thread.isAlive()) && (engine.aiUseThread) ) {
			thread = new Thread(this, "AI_" + playerID);
			thread.setDaemon(true);
			thread.start();
			thinkDelay = engine.aiThinkDelay;
			thinkCurrentPieceNo = 0;
			thinkLastPieceNo = 0;
		}
	}

	/*
	 * 終了処理
	 */
	public void shutdown(GameEngine engine, int playerID) {
		if((thread != null) && (thread.isAlive())) {
			thread.interrupt();
			threadRunning = false;
			thread = null;
		}
	}

	/*
	 * Called whenever a new piece is spawned
	 */
	public void newPiece(GameEngine engine, int playerID) {
		if(!engine.aiUseThread) {
			thinkBestPosition(engine, playerID);
		} else if ((!thinking && !thinkComplete) || !engine.aiPrethink || engine.aiShowHint) {
			thinkRequest.newRequest();
			thinkCurrentPieceNo++;
		}
	}

	/*
	 * Called at the start of each frame
	 */
	public void onFirst(GameEngine engine, int playerID) {
		if (engine.aiPrethink && engine.getARE() > 0 && engine.getARELine() > 0)
		{
			inputARE = 0;
			boolean newInARE = engine.stat == GameEngine.Status.ARE ||
				engine.stat == GameEngine.Status.READY;
			if ((newInARE && !inARE) || (!thinking && !thinkSuccess))
			{
				if (DEBUG_ALL) log.debug("Begin pre-think of next piece.");
				thinkComplete = false;
				thinkRequest.newRequest();
			}
			inARE = newInARE;
		}
	}

	/*
	 * Called after every frame
	 */
	public void onLast(GameEngine engine, int playerID) {
	}

	/*
	 * Set button input states
	 */
	public void setControl(GameEngine engine, int playerID, Controller ctrl) {
		if( (engine.nowPieceObject != null) && (engine.stat == GameEngine.Status.MOVE) &&
			(delay >= engine.aiMoveDelay) && (engine.statc[0] > 0) &&
		    (!engine.aiUseThread || (threadRunning && !thinking && (thinkCurrentPieceNo <= thinkLastPieceNo))) )
		{
			inputARE = 0;
			int input = 0;	// Button input data
			Piece pieceNow = checkOffset(engine.nowPieceObject, engine);
			int nowX = engine.nowPieceX;
			int nowY = engine.nowPieceY;
			int rt = pieceNow.direction;
			Field fld = engine.field;
			boolean pieceTouchGround = pieceNow.checkCollision(nowX, nowY + 1, fld);

			int moveDir = 0; //-1 = left,  1 = right
			int rotateDir = 0; //-1 = left,  1 = right
			int drop = 0; //1 = up, -1 = down
			boolean sync = false; //true = delay either rotate or movement for synchro move if needed.

			//If stuck, rethink.
			if (pieceTouchGround && rt == bestRt &&
					(pieceNow.getMostMovableRight(nowX, nowY, rt, engine.field) < bestX ||
					pieceNow.getMostMovableLeft(nowX, nowY, rt, engine.field) > bestX))
				stuckDelay++;
			else
				stuckDelay = 0;
			if (stuckDelay > 4)
			{
				thinkRequest.newRequest();
				thinkComplete = false;
				if (DEBUG_ALL) log.debug("Needs rethink - piece is stuck!");
			}
			if (nowX == lastX && nowY == lastY && rt == lastRt && lastInput != 0)
			{
				sameStatusTime++;
				if (sameStatusTime > 4)
				{
					thinkRequest.newRequest();
					thinkComplete = false;
					if (DEBUG_ALL) log.debug("Needs rethink - piece is stuck, last inputs had no effect!");
				}
			}
			if (engine.nowPieceRotateCount >= 8)
			{
				thinkRequest.newRequest();
				thinkComplete = false;
				if (DEBUG_ALL) log.debug("Needs rethink - piece is stuck, too many rotations!");
			}
			else
				sameStatusTime = 0;
			if((bestHold == true) && thinkComplete && engine.isHoldOK()) {
				// Hold
				input |= Controller.BUTTON_BIT_D;
			} else {
				if (DEBUG_ALL) log.debug("bestX = " + bestX + ", nowX = " + nowX +
						", bestY = " + bestY + ", nowY = " + nowY +
						", bestRt = " + bestRt + ", rt = " + rt +
						", bestXSub = " + bestXSub + ", bestYSub = " + bestYSub + ", bestRtSub = " + bestRtSub);
				// Rotation
				boolean best180 = Math.abs(rt - bestRt) == 2;
				if(rt != bestRt)
				{
					int lrot = engine.getRotateDirection(-1);
					int rrot = engine.getRotateDirection(1);
					if (DEBUG_ALL) log.debug("lrot = " + lrot + ", rrot = " + rrot);

					if(best180 && (engine.ruleopt.rotateButtonAllowDouble) && !ctrl.isPress(Controller.BUTTON_E))
						input |= Controller.BUTTON_BIT_E;
					else if (bestRt == rrot)
						rotateDir = 1;
					else if(bestRt == lrot)
						rotateDir = -1;
					else if (engine.ruleopt.rotateButtonAllowReverse && best180 && (rt&1) == 1)
					{
						if(rrot == Piece.DIRECTION_UP)
							rotateDir = 1;
						else
							rotateDir = -1;
					}
					else
						rotateDir = 1;
				}

				// 到達可能な位置かどうか
				int minX = pieceNow.getMostMovableLeft(nowX, nowY, rt, fld);
				int maxX = pieceNow.getMostMovableRight(nowX, nowY, rt, fld);

				if( ((bestX < minX - 1) || (bestX > maxX + 1) || (bestY < nowY)) && (rt == bestRt) ) {
					// 到達不能なので再度思考する
					//thinkBestPosition(engine, playerID);
					thinkRequest.newRequest();
					thinkComplete = false;
					//thinkCurrentPieceNo++;
					//System.out.println("rethink c:" + thinkCurrentPieceNo + " l:" + thinkLastPieceNo);
					if (DEBUG_ALL) log.debug("Needs rethink - cannot reach desired position");
				} else {
					// 到達できる場合
					if((nowX == bestX) && (pieceTouchGround)) {
						if (rt == bestRt) {
							// 接地rotation
							if(bestRtSub != -1) {
								bestRt = bestRtSub;
								bestRtSub = -1;
							}
							// ずらし移動
							if(bestX != bestXSub) {
								bestX = bestXSub;
								bestY = bestYSub;
							}
						}
					}
					if (nowX > bestX)
						moveDir = -1;
					else if(nowX < bestX)
						moveDir = 1;
					else if((nowX == bestX) && (rt == bestRt)) {
						moveDir = 0;
						setDAS = 0;
						// 目標到達
						if((bestRtSub == -1) && (bestX == bestXSub)) {
							if (pieceTouchGround && engine.ruleopt.softdropLock)
								drop = -1;
							else if(engine.ruleopt.harddropEnable)
								drop = 1;
							else if(engine.ruleopt.softdropEnable || engine.ruleopt.softdropLock)
								drop = -1;
						} else {
							if(engine.ruleopt.harddropEnable && !engine.ruleopt.harddropLock)
								drop = 1;
							else if(engine.ruleopt.softdropEnable && !engine.ruleopt.softdropLock)
								drop = -1;
						}
					}
				}
			}

			//Convert parameters to input
			boolean useDAS = engine.dasCount >= engine.getDAS() && moveDir == setDAS;
			if(moveDir == -1 && (!ctrl.isPress(Controller.BUTTON_LEFT) || useDAS))
				input |= Controller.BUTTON_BIT_LEFT;
			else if(moveDir == 1 && (!ctrl.isPress(Controller.BUTTON_RIGHT) || useDAS))
				input |= Controller.BUTTON_BIT_RIGHT;
			if(drop == 1 && !ctrl.isPress(Controller.BUTTON_UP))
				input |= Controller.BUTTON_BIT_UP;
			else if(drop == -1)
				input |= Controller.BUTTON_BIT_DOWN;

			if (rotateDir != 0)
			{
				if(engine.ruleopt.rotateButtonAllowDouble &&
						rotateDir == 2 && !ctrl.isPress(Controller.BUTTON_E))
					input |= Controller.BUTTON_BIT_E;
				else if(engine.ruleopt.rotateButtonAllowReverse &&
						  !engine.ruleopt.rotateButtonDefaultRight && (rotateDir == 1))
				{
					if(!ctrl.isPress(Controller.BUTTON_B))
						input |= Controller.BUTTON_BIT_B;
				}
				else if(engine.ruleopt.rotateButtonAllowReverse &&
						  engine.ruleopt.rotateButtonDefaultRight && (rotateDir == -1))
				{
					if(!ctrl.isPress(Controller.BUTTON_B))
						input |= Controller.BUTTON_BIT_B;
				}
				else if(!ctrl.isPress(Controller.BUTTON_A))
					input |= Controller.BUTTON_BIT_A;
			}
			if (setDAS != moveDir)
				setDAS = 0;

			lastInput = input;
			lastX = nowX;
			lastY = nowY;
			lastRt = rt;

			if (DEBUG_ALL) log.debug ("Input = " + input + ", moveDir = " + moveDir  + ", rotateDir = " + rotateDir +
					 ", sync = " + sync  + ", drop = " + drop  + ", setDAS = " + setDAS);

			delay = 0;
			ctrl.setButtonBit(input);
		}
		else {
			//dropDelay = 0;
			delay++;
			ctrl.setButtonBit(inputARE);
		}
	}

	/**
	 * Search for the best choice
	 * @param engine The GameEngine that owns this AI
	 * @param playerID Player ID
	 */
	public void thinkBestPosition(GameEngine engine, int playerID) {
		if (DEBUG_ALL) log.debug("thinkBestPosition called, inARE = " + inARE + ", piece: ");
		bestHold = false;
		bestX = 0;
		bestY = 0;
		bestRt = 0;
		bestXSub = 0;
		bestYSub = 0;
		bestRtSub = -1;
		bestPts = 0;
		thinkSuccess = false;

		engine.createFieldIfNeeded();
		Field fld = new Field(engine.field);
		Piece pieceNow = engine.nowPieceObject;
		Piece pieceHold = engine.holdPieceObject;
		boolean holdOK = engine.isHoldOK();
		int nowX, nowY;
		if (inARE || pieceNow == null)
		{
			pieceNow = engine.getNextObjectCopy(engine.nextPieceCount);
			nowX = engine.getSpawnPosX(fld, pieceNow);
			nowY = engine.getSpawnPosY(pieceNow);
			if(holdOK && pieceHold == null)
				pieceHold = engine.getNextObjectCopy(engine.nextPieceCount+1);
		}
		else {
			nowX = engine.nowPieceX;
			nowY = engine.nowPieceY;
			if(holdOK && pieceHold == null)
				pieceHold = engine.getNextObjectCopy(engine.nextPieceCount);
		}
		pieceNow = checkOffset(pieceNow, engine);
		if(holdOK && pieceHold == null) {
			pieceHold = checkOffset(pieceHold, engine);
			if (pieceHold.id == pieceNow.id)
				pieceHold = null;
		}

		int defcon = 5; //Defense condition. 1 = most defensive, 5 = least defensive.
		int[] depths = getColumnDepths(fld);
		if (depths[2] <= 3)
			defcon = 1;
		else if (depths[3] <= 0)
			defcon = (depths[2] <= 6) ? 3 : 4;
		
		if (defcon >= 4)
		{
			int x, maxX;
			if (depths[3] <= 0)
				maxX = 2;
			else if (depths[4] <= 0)
				maxX = 3;
			else if (depths[5] <= 0)
				maxX = 4;
			else
				maxX = 5;
			for(int rt = 0; rt < Piece.DIRECTION_COUNT; rt++) {
				x = maxX - pieceNow.getMaximumBlockX();
				fld.copy(engine.field);
				int y = pieceNow.getBottom(x, nowY, rt, fld);

				if(!pieceNow.checkCollision(x, y, rt, fld)) {
					int pts = thinkMain(x, y, rt, -1, fld, pieceNow, defcon);

					if(pts >= bestPts) {
						bestHold = false;
						bestX = x;
						bestY = y;
						bestRt = rt;
						bestXSub = x;
						bestYSub = y;
						bestRtSub = -1;
						bestPts = pts;
						if (DEBUG_ALL)
							logBest(1);
						thinkSuccess = true;
					}
				}
				if((holdOK == true) && (pieceHold != null)) {
					x = maxX - pieceHold.getMaximumBlockX();
					fld.copy(engine.field);
					y = pieceHold.getBottom(x, nowY, rt, fld);

					if(!pieceHold.checkCollision(x, y, rt, fld)) {
						int pts = thinkMain(x, y, rt, -1, fld, pieceHold, defcon);

						if(pts >= bestPts) {
							bestHold = false;
							bestX = x;
							bestY = y;
							bestRt = rt;
							bestXSub = x;
							bestYSub = y;
							bestRtSub = -1;
							bestPts = pts;
							if (DEBUG_ALL)
								logBest(2);
							thinkSuccess = true;
						}
					}
				}
			}
		}
		else {
			for(int rt = 0; rt < Piece.DIRECTION_COUNT; rt++) {
				int minX = pieceNow.getMostMovableLeft(nowX, nowY, rt, engine.field);
				int maxX = pieceNow.getMostMovableRight(nowX, nowY, rt, engine.field);
				for(int x = minX; x <= maxX; x++) {
					fld.copy(engine.field);
					int y = pieceNow.getBottom(x, nowY, rt, fld);

					if(!pieceNow.checkCollision(x, y, rt, fld)) {
						// そのまま
						int pts = thinkMain(x, y, rt, -1, fld, pieceNow, defcon);

						if(pts >= bestPts) {
							bestHold = false;
							bestX = x;
							bestY = y;
							bestRt = rt;
							bestXSub = x;
							bestYSub = y;
							bestRtSub = -1;
							bestPts = pts;
							if (DEBUG_ALL)
								logBest(3);
							thinkSuccess = true;
						}
					}
				}

				// Hold piece
				if((holdOK == true) && (pieceHold != null)) {
					int spawnX = engine.getSpawnPosX(engine.field, pieceHold);
					int spawnY = engine.getSpawnPosY(pieceHold);
					int minHoldX = pieceHold.getMostMovableLeft(spawnX, spawnY, rt, engine.field);
					int maxHoldX = pieceHold.getMostMovableRight(spawnX, spawnY, rt, engine.field);

					for(int x = minHoldX; x <= maxHoldX; x++)
					{
						fld.copy(engine.field);
						int y = pieceHold.getBottom(x, spawnY, rt, fld);

						if(!pieceHold.checkCollision(x, y, rt, fld)) {
							// そのまま
							int pts = thinkMain(x, y, rt, -1, fld, pieceHold, defcon);
							if(pts >= bestPts) {
								bestHold = true;
								bestX = x;
								bestY = y;
								bestRt = rt;
								bestXSub = x;
								bestYSub = y;
								bestRtSub = -1;
								bestPts = pts;
								if (DEBUG_ALL)
									logBest(4);
								thinkSuccess = true;
							}
						}
					}
				}
			}
		}

		thinkLastPieceNo++;

		//System.out.println("X:" + bestX + " Y:" + bestY + " R:" + bestRt + " H:" + bestHold + " Pts:" + bestPts);
	}

	/**
	 * Think routine
	 * @param x X-coordinate
	 * @param y Y-coordinate
	 * @param rt Direction
	 * @param rtOld Direction before rotation (-1: None）
	 * @param fld Field (Can be modified without problems)
	 * @param piece Piece
	 * @param defcon Defense level (the lower, the more defensive)
	 * @return Evaluation score
	 */
	public int thinkMain(int x, int y, int rt, int rtOld, Field fld, Piece piece, int defcon) {
		int pts = 0;

		if (defcon <= 3)
			pts -= fld.getHighestBlockY(2);
		
		// ピースを置く
		if(!piece.placeToField(x, y, rt, fld)) {
			if (DEBUG_ALL)
				log.debug("End of thinkMain(" + x + ", " + y + ", " + rt + ", " + rtOld +
					", fld, piece " + piece.id + ", " + defcon + "). pts = MIN_VALUE (Cannot place piece)");
			return Integer.MIN_VALUE;
		}

		fld.freeFall();
		
		if (defcon >= 4)
		{
			int maxX = piece.getMaximumBlockX()+x;
			if(maxX < 2) {
				if (DEBUG_ALL)
					log.debug("End of thinkMain(" + x + ", " + y + ", " + rt + ", " + rtOld + ", fld, piece "
							+ piece.id + ", " + defcon + "). pts = MIN_VALUE (Invalid location/defcon combination)");
				return Integer.MIN_VALUE;
			}
			int maxY = fld.getHighestBlockY(maxX);
			int clear = fld.clearColor(maxX, maxY, true, true, false, true);
			if (clear >= 4)
				pts += (defcon == 5) ? -4 : 4;
			else if (clear == 3)
				pts += 2;
			else if (clear == 2)
				pts++;
			
			if ((rt&1) == 1)
			{
				pts++;
				clear = fld.clearColor(maxX, maxY+1, true, true, false, true);
			}
			else
				clear = fld.clearColor(maxX-1, fld.getHighestBlockY(maxX-1), true, true, false, true);
			if (clear >= 4)
				pts += (defcon == 5) ? -4 : 4;
			else if (clear == 3)
				pts += 2;
			else if (clear == 2)
				pts++;
		}

		// Clear
		int chain = 1;
		while (true)
		{
			int clear = fld.clearColor(4, true, false, true);
			if (clear <= 0)
				break;
			else if (defcon <= 4)
			{
				if (chain == 0)
					pts += clear;
				else if (chain == 2)
					pts += clear << 3;
				else if (chain == 3)
					pts += clear << 4;
				else if (chain >= 4)
					pts += clear*32*(chain-3);
			}
			fld.freeFall();
			chain++;
		}

		if (defcon <= 3)
			pts += fld.getHighestBlockY(2);

		// All clear
		boolean allclear = fld.isEmpty();
		if(allclear) pts += 1000;

		if (DEBUG_ALL)
			log.debug("End of thinkMain(" + x + ", " + y + ", " + rt + ", " + rtOld +
					", fld, piece " + piece.id + ", " + defcon + "). pts = " + pts);
		return pts;
	}
	//private static final int[][] HI_PENALTY = {{6, 2}, {7, 6}, {6, 2}, {1, 0}};
	public static Piece checkOffset(Piece p, GameEngine engine)
	{
		Piece result = new Piece(p);
		result.big = engine.big;
		if (!p.offsetApplied)
			result.applyOffsetArray(engine.ruleopt.pieceOffsetX[p.id], engine.ruleopt.pieceOffsetY[p.id]);
		return result;
	}

	public static int[] getColumnDepths (Field fld)
	{
		int width = fld.getWidth();
		int[] result = new int[width];
		for (int x = 0; x < width; x++)
			result[x] = fld.getHighestBlockY(x);
		return result;
	}

	protected void logBest(int caseNum)
	{
		log.debug("New best position found (Case " + caseNum +
				"): bestHold = " + bestHold +
				", bestX = " + bestX +
				", bestY = " + bestY +
				", bestRt = " + bestRt +
				", bestXSub = " + bestXSub +
				", bestYSub = " + bestYSub +
				", bestRtSub = " + bestRtSub +
				", bestPts = " + bestPts);
	}

	/*
	 * スレッドの処理
	 */
	public void run() {
		log.info("Nohoho: Thread start");
		threadRunning = true;

		while(threadRunning) {
			try {
				synchronized(thinkRequest)
				{
					if (!thinkRequest.active)
						thinkRequest.wait();
				}
			} catch (InterruptedException e) {
				log.debug("PoochyBot: InterruptedException waiting for thinkRequest signal");
			}
			if(thinkRequest.active) {
				thinkRequest.active = false;
				thinking = true;
				try {
					thinkBestPosition(gEngine, gEngine.playerID);
					thinkComplete = true;
					log.debug("Nohoho: thinkBestPosition completed successfully");
				} catch (Throwable e) {
					log.debug("Nohoho: thinkBestPosition Failed", e);
				}
				thinking = false;
			}

			if(thinkDelay > 0) {
				try {
					Thread.sleep(thinkDelay);
				} catch (InterruptedException e) {
					break;
				}
			}
		}

		threadRunning = false;
		log.info("Nohoho: Thread end");
	}
	
	//Wrapper for think requests
	private static class ThinkRequestMutex
	{
		public boolean active;
		public ThinkRequestMutex()
		{
			active = false;
		}
		public synchronized void newRequest()
		{
			active = true;
			notifyAll();
		}
	}
}
