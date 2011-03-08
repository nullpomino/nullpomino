package net.tetrisconcept.poochy.nullpomino.ai;

import mu.nu.nullpo.game.component.Controller;
import mu.nu.nullpo.game.component.Field;
import mu.nu.nullpo.game.component.Piece;
import mu.nu.nullpo.game.component.SpeedParam;
import mu.nu.nullpo.game.component.WallkickResult;
import mu.nu.nullpo.game.event.EventReceiver;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.game.subsystem.ai.DummyAI;
import mu.nu.nullpo.util.GeneralUtil;

import org.apache.log4j.Logger;

/**
 * PoochyBot AI
 * @author Poochy.EXE
 *         Poochy.Spambucket@gmail.com
 */
public class PoochyBot extends DummyAI implements Runnable {
	/** Log */
	static Logger log = Logger.getLogger(PoochyBot.class);

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
	public boolean thinkRequest;

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
	protected static final boolean DEBUG_ALL = false;
	/** Wait extra frames at low speeds? */
	//protected static final boolean DELAY_DROP_ON = false;
	/** # of extra frames to wait */
	//protected static final int DROP_DELAY = 2;
	/** Number of frames waited */
	//protected int dropDelay;
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
		return "PoochyBot V1.25";
	}

	/*
	 * Called at initialization
	 */
	public void init(GameEngine engine, int playerID) {
		delay = 0;
		gEngine = engine;
		gManager = engine.owner;
		thinkRequest = false;
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
		//dropDelay = 0;
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
		} else if ((!thinking && !thinkComplete) || !engine.aiPrethink
				|| engine.getARE() <= 0 || engine.getARELine() <= 0) {
			thinkComplete = false;
			thinkRequest = true;
			thinkCurrentPieceNo++;
		}
	}

	/*
	 * Called at the start of each frame
	 */
	public void onFirst(GameEngine engine, int playerID) {
		inputARE = 0;
		boolean newInARE = engine.stat == GameEngine.STAT_ARE;
		if ((engine.aiPrethink && engine.getARE() > 0 && engine.getARELine() > 0)
				&& ((newInARE && !inARE) || (!thinking && !thinkSuccess)))
		{
			if (DEBUG_ALL) log.debug("Begin pre-think of next piece.");
			if (engine.field == null)
				engine.createFieldIfNeeded();
			thinkComplete = false;
			thinkRequest = true;
		}
		inARE = newInARE;
		if(inARE && delay >= engine.aiMoveDelay) {
			int input = 0;
			Piece nextPiece = engine.getNextObject(engine.nextPieceCount);
			if (bestHold && thinkComplete)
			{
				input |= Controller.BUTTON_BIT_D;
				if (engine.holdPieceObject == null)
					nextPiece = engine.getNextObject(engine.nextPieceCount+1);
				else
					nextPiece = engine.holdPieceObject;
			}
			if (nextPiece == null)
				return;
			nextPiece = checkOffset(nextPiece, engine);
			input |= calcIRS(nextPiece, engine);
			if (threadRunning && !thinking && thinkComplete)
			{
				int spawnX = engine.getSpawnPosX(engine.field, nextPiece);
				if(bestX - spawnX > 1) {
					// left
					//setDAS = -1;
					input |= Controller.BUTTON_BIT_LEFT;
				} else if(spawnX - bestX > 1) {
					// right
					//setDAS = 1;
					input |= Controller.BUTTON_BIT_RIGHT;
				}
				else
					setDAS = 0;
				delay = 0;
			}
			if (DEBUG_ALL) log.debug("Currently in ARE. Next piece type = " +
					Piece.PIECE_NAMES[nextPiece.id] + ", IRS = " + input);
			//engine.ctrl.setButtonBit(input);
			inputARE = input;
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
		if( (engine.nowPieceObject != null) && (engine.stat == GameEngine.STAT_MOVE) &&
			(delay >= engine.aiMoveDelay) && (engine.statc[0] > 0) &&
		    (!engine.aiUseThread || (threadRunning && !thinking && thinkComplete)))
		{
			inputARE = 0;
			int input = 0;	// Button input data
			Piece pieceNow = checkOffset(engine.nowPieceObject, engine);
			int nowX = engine.nowPieceX;
			int nowY = engine.nowPieceY;
			int rt = pieceNow.direction;
			Field fld = engine.field;
			boolean pieceTouchGround = pieceNow.checkCollision(nowX, nowY + 1, fld);
			int nowType = pieceNow.id;
			int width = fld.getWidth();

			int moveDir = 0; //-1 = left,  1 = right
			int rotateDir = 0; //-1 = left,  1 = right
			int drop = 0; //1 = up, -1 = down
			boolean sync = false; //true = delay either rotate or movement for synchro move if needed.

			//SpeedParam speed = engine.speed;
			//boolean lowSpeed = speed.gravity < speed.denominator;
			boolean canFloorKick = engine.nowUpwardWallkickCount < engine.ruleopt.rotateMaxUpwardWallkick
				|| engine.ruleopt.rotateMaxUpwardWallkick < 0;

			//If stuck, rethink.
			/*
			if ((nowX < bestX && pieceNow.checkCollision(nowX+1, nowY, rt, fld)) ||
					(nowX > bestX && pieceNow.checkCollision(nowX-1, nowY, rt, fld)))
			{
				thinkRequest = true;
				if (DEBUG_ALL) log.debug("Needs rethink - piece is stuck!");
			}
			*/
			/*
			if (rt == Piece.DIRECTION_DOWN &&
					((nowType == Piece.PIECE_L && bestX > nowX) || (nowType == Piece.PIECE_J && bestX < nowX)))
				{
					if (DEBUG_ALL) log.debug("Checking for stuck L or J piece.");
					if (DEBUG_ALL) log.debug("Coordinates of piece: x = " + nowX + ", y = " + nowY);
					if (DEBUG_ALL) log.debug("Coordinates of block to check: x = " + (pieceNow.getMaximumBlockX()+nowX-1) +
							", y = " + (pieceNow.getMaximumBlockY()+nowY));
					for (int xCheck = 0; xCheck < fld.getWidth(); xCheck++)
						if (DEBUG_ALL) log.debug("fld.getHighestBlockY(" + xCheck + ") = " + fld.getHighestBlockY(xCheck));
				}
			*/
			if ((rt == Piece.DIRECTION_DOWN &&
					((nowType == Piece.PIECE_L && bestX > nowX) || (nowType == Piece.PIECE_J && bestX < nowX))
					&& !fld.getBlockEmpty(pieceNow.getMaximumBlockX()+nowX-1, pieceNow.getMaximumBlockY()+nowY)))
			{
				thinkRequest = true;
				thinkComplete = false;
				if (DEBUG_ALL) log.debug("Needs rethink - L or J piece is stuck!");
			}
			if (nowType == Piece.PIECE_O && ((bestX < nowX && pieceNow.checkCollision(nowX-1, nowY, rt, fld))
					|| (bestX < nowX && pieceNow.checkCollision(nowX-1, nowY, rt, fld))))
			{
				thinkRequest = true;
				thinkComplete = false;
				if (DEBUG_ALL) log.debug("Needs rethink - O piece is stuck!");
			}
			if (pieceTouchGround && rt == bestRt &&
					(pieceNow.getMostMovableRight(nowX, nowY, rt, engine.field) < bestX ||
					pieceNow.getMostMovableLeft(nowX, nowY, rt, engine.field) > bestX))
				stuckDelay++;
			else
				stuckDelay = 0;
			if (stuckDelay > 4)
			{
				thinkRequest = true;
				thinkComplete = false;
				if (DEBUG_ALL) log.debug("Needs rethink - piece is stuck!");
			}
			if (nowX == lastX && nowY == lastY && rt == lastRt && lastInput != 0)
			{
				sameStatusTime++;
				if (sameStatusTime > 4)
				{
					thinkRequest = true;
					thinkComplete = false;
					if (DEBUG_ALL) log.debug("Needs rethink - piece is stuck, last inputs had no effect!");
				}
			}
			if (engine.nowPieceRotateCount >= 8)
			{
				thinkRequest = true;
				thinkComplete = false;
				if (DEBUG_ALL) log.debug("Needs rethink - piece is stuck, too many rotations!");
			}
			else
				sameStatusTime = 0;
			if((bestHold == true) && thinkComplete && engine.isHoldOK()) {
				// Hold
				input |= Controller.BUTTON_BIT_D;

				Piece holdPiece = engine.holdPieceObject;
				if (holdPiece != null)
					input |= calcIRS(holdPiece, engine);
			} else {
				if (DEBUG_ALL) log.debug("bestX = " + bestX + ", nowX = " + nowX +
						", bestY = " + bestY + ", nowY = " + nowY +
						", bestRt = " + bestRt + ", rt = " + rt +
						", bestXSub = " + bestXSub + ", bestYSub = " + bestYSub + ", bestRtSub = " + bestRtSub);
				printPieceAndDirection(nowType, rt);
				// Rotation
				//Rotate iff near destination or stuck
				int xDiff = Math.abs(nowX - bestX);
				if (bestX < nowX && nowType == Piece.PIECE_I &&
						rt == Piece.DIRECTION_DOWN && bestRt != rt)
					xDiff--;
				boolean best180 = Math.abs(rt - bestRt) == 2;
				//Special movements for I piece
				if (nowType == Piece.PIECE_I)
				{
					int hypRtDir = 1;
					boolean rotateI = false;
					if ((rt+3)%4 == bestRt)
						hypRtDir = -1;
					if (nowX < bestX)
					{
						moveDir = 1;
						if (pieceNow.checkCollision(nowX+1, nowY, fld))
						{
							if((rt&1) == 0 && (canFloorKick || !pieceNow.checkCollision(nowX, nowY, (rt+1)%4, fld)))
								rotateI = true;
							else if ((rt&1) == 1 && canFloorKick)
								rotateI = true;
							else if (engine.isHoldOK() && !ctrl.isPress(Controller.BUTTON_D))
							{
								if (DEBUG_ALL) log.debug("Stuck I piece - use hold");
								input |= Controller.BUTTON_BIT_D;

								Piece holdPiece = engine.holdPieceObject;
								if (holdPiece != null)
									input |= calcIRS(holdPiece, engine);
							}
						}
					}
					else if (nowX > bestX)
					{
						moveDir = -1;
						if (pieceNow.checkCollision(nowX-1, nowY, fld))
						{
							if((rt&1) == 0 && (canFloorKick || !pieceNow.checkCollision(nowX, nowY, (rt+1)%4, fld)))
								rotateI = true;
							else if ((rt&1) == 1 && !pieceNow.checkCollision(nowX-1, nowY, (rt+1)%4, fld) &&
									canFloorKick)
								rotateI = true;
							else if (engine.isHoldOK() && !ctrl.isPress(Controller.BUTTON_D))
							{
								if (DEBUG_ALL) log.debug("Stuck I piece - use hold");
								input |= Controller.BUTTON_BIT_D;

								Piece holdPiece = engine.holdPieceObject;
								if (holdPiece != null)
									input |= calcIRS(holdPiece, engine);
							}
						}
					}
					else if (rt != bestRt)
					{
						if (best180)
							bestRt = (bestRt+2)%4;
						else
							rotateI = true;
					}
					if (rotateI)
						rotateDir = hypRtDir;
				}
				else if((rt != bestRt && ((xDiff <= 1) ||
						(bestX == 0 && nowX == 2 && nowType == Piece.PIECE_I) ||
						(((nowX < bestX && pieceNow.checkCollision(nowX+1, nowY, rt, fld)) ||
						(nowX > bestX && pieceNow.checkCollision(nowX-1, nowY, rt, fld))) &&
						!(pieceNow.getMaximumBlockX()+nowX == width-2 && (rt&1) == 1) &&
						!(pieceNow.getMinimumBlockY()+nowY == 2 && pieceTouchGround && (rt&1) == 0 && nowType != Piece.PIECE_I)))))
				{
					//if (DEBUG_ALL) log.debug("Case 1 rotation");

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
				//Try to keep flat side down on L, J, or T piece.
				else if (((rt != Piece.DIRECTION_UP && xDiff > 1 && engine.ruleopt.rotateButtonAllowReverse) /*|| best180*/) &&
						(nowType == Piece.PIECE_L || nowType == Piece.PIECE_J || nowType == Piece.PIECE_T))
				{
					//if (DEBUG_ALL) log.debug("Case 2 rotation");

					if (rt == Piece.DIRECTION_DOWN)
					{
						if (engine.ruleopt.rotateButtonAllowDouble && !ctrl.isPress(Controller.BUTTON_E))
							input |= Controller.BUTTON_BIT_E;
						else if (nowType == Piece.PIECE_L)
							rotateDir = -1;
						else if (nowType == Piece.PIECE_J)
							rotateDir = 1;
						else if (nowType == Piece.PIECE_T)
						{
							if (nowX > bestX)
								rotateDir = -1;
							else if (nowX < bestX)
								rotateDir = 1;
						}
					}
					else if (rt == Piece.DIRECTION_RIGHT)
						rotateDir = -1;
					else if (rt == Piece.DIRECTION_LEFT)
						rotateDir = 1;
				}

				// 到達可能な位置かどうか
				int minX = pieceNow.getMostMovableLeft(nowX, nowY, rt, fld);
				int maxX = pieceNow.getMostMovableRight(nowX, nowY, rt, fld);

				if( ((bestX < minX - 1) || (bestX > maxX + 1) || (bestY < nowY)) && (rt == bestRt) ) {
					// 到達不能なので再度思考する
					//thinkBestPosition(engine, playerID);
					thinkRequest = true;
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
						else if (nowType == Piece.PIECE_I && (rt & 1) == 1 &&
								nowX+pieceNow.getMaximumBlockX() == width-2 && (fld.getHighestBlockY() <= 4 ||
									(fld.getHighestBlockY(width-2) - fld.getHighestBlockY(width-1) >=4 )))
						{
							bestRt = rt;
							bestX++;
						}
					}
					/*
					//Move left if need to move left, or if at rightmost position and can move left.
					if (pieceTouchGround && pieceNow.id != Piece.PIECE_I &&
							nowX+pieceNow.getMaximumBlockX() == width-1 &&
							!pieceNow.checkCollision(nowX-1, nowY, fld))
					{
						if(!ctrl.isPress(Controller.BUTTON_LEFT) && (engine.aiMoveDelay >= 0))
							input |= Controller.BUTTON_BIT_LEFT;
						bestX = nowX - 1;
					}
					*/
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

			int minBlockX = nowX+pieceNow.getMinimumBlockX();
			int maxBlockX = nowX+pieceNow.getMaximumBlockX();
			int minBlockXDepth = fld.getHighestBlockY(minBlockX);
			int maxBlockXDepth = fld.getHighestBlockY(maxBlockX);
			if (nowType == Piece.PIECE_L && minBlockXDepth < maxBlockXDepth && pieceTouchGround
					&& rt == Piece.DIRECTION_DOWN && rotateDir == -1 && maxBlockX < width-1)
			{
				if (bestX == nowX+1)
					moveDir = 1;
				else if (bestX < nowX)
				{
					if (DEBUG_ALL) log.debug("Delaying rotation on L piece to avoid getting stuck. (Case 1)");
					sync = false;
					rotateDir = 0;
					moveDir = 1;
				}
				else if (bestX > nowX)
				{
					/*
					if (minBlockXDepth == fld.getHighestBlockY(minBlockX-1))
					{
						if (DEBUG_ALL) log.debug("Delaying rotation on L piece to avoid getting stuck. (Case 2)");
						sync = false;
						rotateDir = 0;
						moveDir = -1;
					}
					else
					*/
					if (DEBUG_ALL) log.debug("Attempting synchro move on L piece to avoid getting stuck.");
					sync = true;
					rotateDir = -1;
					moveDir = -1;
				}
			}
			else if (nowType == Piece.PIECE_J && minBlockXDepth > maxBlockXDepth && pieceTouchGround
					&& rt == Piece.DIRECTION_DOWN && rotateDir == 1 && minBlockX > 0)
			{
				if (bestX == nowX-1)
					moveDir = -1;
				else if (bestX > nowX)
				{
					if (DEBUG_ALL) log.debug("Delaying rotation on J piece to avoid getting stuck. (Case 1)");
					sync = false;
					rotateDir = 0;
					moveDir = -1;
				}
				else if (bestX < nowX)
				{
					/*
					if (maxBlockXDepth == fld.getHighestBlockY(maxBlockX+1))
					{
						if (DEBUG_ALL) log.debug("Delaying rotation on J piece to avoid getting stuck. (Case 2)");
						sync = false;
						rotateDir = 0;
						moveDir = 1;
					}
					else
					*/
					if (DEBUG_ALL) log.debug("Attempting synchro move on J piece to avoid getting stuck.");
					sync = true;
					rotateDir = 1;
					moveDir = 1;
				}
			}
			else if (rotateDir != 0 && moveDir != 0 && pieceTouchGround && (rt&1) == 1
					&& (nowType == Piece.PIECE_J || nowType == Piece.PIECE_L)
					&& !pieceNow.checkCollision(nowX+moveDir, nowY+1, rt, fld))
			{
				if (DEBUG_ALL) log.debug("Delaying move on L or J piece to avoid getting stuck.");
				sync = false;
				moveDir = 0;
			}
			if (engine.nowPieceRotateCount >= 5 && rotateDir != 0 && moveDir != 0 && !sync)
			{
				if (DEBUG_ALL) log.debug("Piece seems to be stuck due to unintentional synchro - trying intentional desync.");
				moveDir = 0;
			}
			if (moveDir == -1 && minBlockX == 1 && nowType == Piece.PIECE_I && (rt&1) == 1
					&& pieceNow.checkCollision(nowX-1, nowY, rt, fld))
			{
				int depthNow = fld.getHighestBlockY(minBlockX);
				int depthLeft = fld.getHighestBlockY(minBlockX-1);
				if(depthNow > depthLeft && depthNow - depthLeft < 2)
				{
					if (!pieceNow.checkCollision(nowX+1, nowY, rt, fld))
						moveDir = 1;
					else if (engine.isHoldOK() && !ctrl.isPress(Controller.BUTTON_D))
						input |= Controller.BUTTON_BIT_D;
				}
			}
			/*
			//Catch bug where it fails to rotate J piece
			if (moveDir == 0 && rotateDir == 0 & drop == 0)
			{
				if ((rt+1)%4 == bestRt)
					rotateDir = 1;
				else if ((rt+3)%4 == bestRt)
					rotateDir = -1;
				else if ((rt+2)%4 == bestRt)
				{
					if(engine.ruleopt.rotateButtonAllowDouble)
						rotateDir = 2;
					else if (rt == 3)
						rotateDir = -1;
					else
						rotateDir = -1;
				}
				else if (bestX < nowX)
					moveDir = -1;
				else if (bestX > nowX)
					moveDir = 1;
				else
					if (DEBUG_ALL) log.debug("Movement error: Nothing to do!");
			}
			if (rotateDir == 0 && Math.abs(rt - bestRt) == 2)
				rotateDir = 1;
			*/
			//Convert parameters to input
			boolean useDAS = engine.dasCount >= engine.getDAS() && moveDir == setDAS;
			if(moveDir == -1 && (!ctrl.isPress(Controller.BUTTON_LEFT) || useDAS))
				input |= Controller.BUTTON_BIT_LEFT;
			else if(moveDir == 1 && (!ctrl.isPress(Controller.BUTTON_RIGHT) || useDAS))
				input |= Controller.BUTTON_BIT_RIGHT;
			/*
			if(drop == 1 && !ctrl.isPress(Controller.BUTTON_UP))
			{
				if (DELAY_DROP_ON && lowSpeed && dropDelay < (DROP_DELAY >> 1))
					dropDelay++;
				else
					input |= Controller.BUTTON_BIT_UP;
			}
			else if(drop == -1)
			{
				if (DELAY_DROP_ON && lowSpeed && dropDelay < DROP_DELAY)
					dropDelay++;
				else
					input |= Controller.BUTTON_BIT_DOWN;
			}
			*/
			if(drop == 1 && !ctrl.isPress(Controller.BUTTON_UP))
				input |= Controller.BUTTON_BIT_UP;
			else if(drop == -1)
				input |= Controller.BUTTON_BIT_DOWN;

			if (rotateDir != 0)
			{
				boolean defaultRotateRight = (engine.owRotateButtonDefaultRight == 1 ||
						(engine.owRotateButtonDefaultRight == -1 &&
								engine.ruleopt.rotateButtonDefaultRight));
				
				if(engine.ruleopt.rotateButtonAllowDouble &&
						rotateDir == 2 && !ctrl.isPress(Controller.BUTTON_E))
					input |= Controller.BUTTON_BIT_E;
				else if(engine.ruleopt.rotateButtonAllowReverse &&
						  !defaultRotateRight && (rotateDir == 1))
				{
					if(!ctrl.isPress(Controller.BUTTON_B))
						input |= Controller.BUTTON_BIT_B;
				}
				else if(engine.ruleopt.rotateButtonAllowReverse &&
						defaultRotateRight && (rotateDir == -1))
				{
					if(!ctrl.isPress(Controller.BUTTON_B))
						input |= Controller.BUTTON_BIT_B;
				}
				else if(!ctrl.isPress(Controller.BUTTON_A))
					input |= Controller.BUTTON_BIT_A;
			}
			if (sync)
			{
				if (DEBUG_ALL) log.debug("Attempting to perform synchro move.");
				int bitsLR = Controller.BUTTON_BIT_LEFT | Controller.BUTTON_BIT_RIGHT;
				int bitsAB = Controller.BUTTON_BIT_A | Controller.BUTTON_BIT_B;
				if ((input & bitsLR) == 0 || (input & bitsAB) == 0)
				{
					setDAS = 0;
					input &= ~(bitsLR | bitsAB);
				}
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

	protected void printPieceAndDirection(int pieceType, int rt)
	{
		String result = "Piece " + Piece.PIECE_NAMES[pieceType] + ", direction ";

		switch (rt)
		{
			case Piece.DIRECTION_LEFT:  result = result + "left";  break;
			case Piece.DIRECTION_DOWN:  result = result + "down";  break;
			case Piece.DIRECTION_UP:    result = result + "up";    break;
			case Piece.DIRECTION_RIGHT: result = result + "right"; break;
		}
		if (DEBUG_ALL) log.debug(result);
	}

	public int calcIRS(Piece piece, GameEngine engine)
	{
		piece = checkOffset(piece, engine);
		int nextType = piece.id;
		Field fld = engine.field;
		int spawnX = engine.getSpawnPosX(fld, piece);
		SpeedParam speed = engine.speed;
		boolean gravityHigh = speed.gravity > speed.denominator;
		int width = fld.getWidth();
		int midColumnX = (width/2)-1;
		if(Math.abs(spawnX - bestX) == 1)
		{
			if (bestRt == 1)
			{
				if (engine.ruleopt.rotateButtonDefaultRight)
					return Controller.BUTTON_BIT_A;
				else
					return Controller.BUTTON_BIT_B;
			}
			else if (bestRt == 3)
			{
				if (engine.ruleopt.rotateButtonDefaultRight)
					return Controller.BUTTON_BIT_B;
				else
					return Controller.BUTTON_BIT_A;
			}
		}
		else if (nextType == Piece.PIECE_L)
		{
			if (gravityHigh && fld.getHighestBlockY(midColumnX-1) <
					Math.min(fld.getHighestBlockY(midColumnX), fld.getHighestBlockY(midColumnX+1)))
				return 0;
			else if (engine.ruleopt.rotateButtonDefaultRight)
				return Controller.BUTTON_BIT_B;
			else
				return Controller.BUTTON_BIT_A;
		}
		else if (nextType == Piece.PIECE_J)
		{
			if (gravityHigh && fld.getHighestBlockY(midColumnX+1) <
					Math.min(fld.getHighestBlockY(midColumnX), fld.getHighestBlockY(midColumnX-1)))
				return 0;
			if (engine.ruleopt.rotateButtonDefaultRight)
				return Controller.BUTTON_BIT_A;
			else
				return Controller.BUTTON_BIT_B;
		}
		/*
		else if (nextType == Piece.PIECE_I)
			return Controller.BUTTON_BIT_A;
		*/
		return 0;
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

		Field fld;
		if (engine.stat == GameEngine.STAT_READY)
			fld = new Field(engine.fieldWidth, engine.fieldHeight,
					engine.fieldHiddenHeight, engine.ruleopt.fieldCeiling);
		else
			fld = new Field(engine.field);
		Piece pieceNow = engine.nowPieceObject;
		Piece pieceHold = engine.holdPieceObject;
		/*
		Piece pieceNow = null;
		if (engine.nowPieceObject != null)
			pieceNow = new Piece(engine.nowPieceObject);
		Piece pieceHold = null;
		if (engine.holdPieceObject != null)
			pieceHold = new Piece(engine.holdPieceObject);
		*/
		int nowX, nowY, nowRt;
		if (inARE || pieceNow == null)
		{
			pieceNow = engine.getNextObjectCopy(engine.nextPieceCount);
			nowX = engine.getSpawnPosX(fld, pieceNow);
			nowY = engine.getSpawnPosY(pieceNow);
			nowRt = engine.ruleopt.pieceDefaultDirection[pieceNow.id];
			if(pieceHold == null)
				pieceHold = engine.getNextObjectCopy(engine.nextPieceCount+1);
		}
		else {
			nowX = engine.nowPieceX;
			nowY = engine.nowPieceY;
			nowRt = pieceNow.direction;
			if (pieceHold == null)
				pieceHold = engine.getNextObjectCopy(engine.nextPieceCount);
		}
		pieceNow = checkOffset(pieceNow, engine);
		pieceHold = checkOffset(pieceHold, engine);
		if (pieceHold.id == pieceNow.id)
			pieceHold = null;
		/*
		if (!pieceNow.offsetApplied)
		pieceNow.applyOffsetArray(engine.ruleopt.pieceOffsetX[pieceNow.id],
				engine.ruleopt.pieceOffsetY[pieceNow.id]);
		if (!pieceHold.offsetApplied)
		pieceHold.applyOffsetArray(engine.ruleopt.pieceOffsetX[pieceHold.id],
				engine.ruleopt.pieceOffsetY[pieceHold.id]);
		*/
		boolean holdOK = engine.isHoldOK();

		boolean canFloorKick = engine.nowUpwardWallkickCount < engine.ruleopt.rotateMaxUpwardWallkick
			|| engine.ruleopt.rotateMaxUpwardWallkick < 0;
		boolean canFloorKickI = (pieceNow.id == Piece.PIECE_I && (nowRt&1) == 0 && canFloorKick);
		boolean canFloorKickT = (pieceNow.id == Piece.PIECE_T && nowRt != Piece.DIRECTION_UP && canFloorKick);
		if (canFloorKickT && !pieceNow.checkCollision(nowX, nowY, Piece.DIRECTION_UP, fld))
			canFloorKickT = false;
		else if (canFloorKickT && !pieceNow.checkCollision(nowX-1, nowY, Piece.DIRECTION_UP, fld))
			canFloorKickT = false;
		else if (canFloorKickT && !pieceNow.checkCollision(nowX+1, nowY, Piece.DIRECTION_UP, fld))
			canFloorKickT = false;

		int move = 1;
		if (engine.big)
			move = 2;

		for(int depth = 0; depth < MAX_THINK_DEPTH; depth++) {
			/*
			int dirCount = Piece.DIRECTION_COUNT;
			if (pieceNow.id == Piece.PIECE_I || pieceNow.id == Piece.PIECE_S || pieceNow.id == Piece.PIECE_Z)
				dirCount = 2;
			else if (pieceNow.id == Piece.PIECE_O)
				dirCount = 1;
			*/
			for(int rt = 0; rt < Piece.DIRECTION_COUNT; rt++) {
				int tempY = nowY;
				if (canFloorKickI && (rt&1) == 1)
					tempY -= 2;
				else if (canFloorKickT && rt == Piece.DIRECTION_UP)
					tempY--;

				int minX = Math.max(mostMovableX(nowX, tempY, -1, engine, fld, pieceNow, rt),
						pieceNow.getMostMovableLeft(nowX, tempY, rt, engine.field));
				int maxX = Math.min(mostMovableX(nowX, tempY, 1, engine, fld, pieceNow, rt),
						pieceNow.getMostMovableRight(nowX, tempY, rt, engine.field));
				boolean spawnOK = true;
				if (engine.stat == GameEngine.STAT_ARE)
				{
					int spawnX = engine.getSpawnPosX(fld, pieceNow);
					int spawnY = engine.getSpawnPosY(pieceNow);
					spawnOK = !pieceNow.checkCollision(spawnX, spawnY, fld);
				}
				for(int x = minX; x <= maxX && spawnOK; x+=move) {
					fld.copy(engine.field);
					int y = pieceNow.getBottom(x, tempY, rt, fld);

					if(!pieceNow.checkCollision(x, y, rt, fld)) {
						// そのまま
						int pts = thinkMain(x, y, rt, -1, fld, pieceNow, depth);

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
						//Check regardless
						//if((depth > 0) || (bestPts <= 10) || (pieceNow.id == Piece.PIECE_T)) {
						// Left shift
						fld.copy(engine.field);
						if(!pieceNow.checkCollision(x - move, y, rt, fld) && pieceNow.checkCollision(x - move, y - 1, rt, fld)) {
							pts = thinkMain(x - move, y, rt, -1, fld, pieceNow, depth);

							if(pts > bestPts) {
								bestHold = false;
								bestX = x;
								bestY = y;
								bestRt = rt;
								bestXSub = x - move;
								bestYSub = y;
								bestRtSub = -1;
								bestPts = pts;
								if (DEBUG_ALL)
									logBest(2);
								thinkSuccess = true;
							}
						}

						// Right shift
						fld.copy(engine.field);
						if(!pieceNow.checkCollision(x + move, y, rt, fld) && pieceNow.checkCollision(x + 1, y - move, rt, fld)) {
							pts = thinkMain(x + move, y, rt, -1, fld, pieceNow, depth);

							if(pts > bestPts) {
								bestHold = false;
								bestX = x;
								bestY = y;
								bestRt = rt;
								bestXSub = x + 1;
								bestYSub = y;
								bestRtSub = -1;
								bestPts = pts;
								if (DEBUG_ALL)
									logBest(3);
								thinkSuccess = true;
							}
						}

						// Left rotation
						if(!engine.ruleopt.rotateButtonDefaultRight || engine.ruleopt.rotateButtonAllowReverse) {
							int rot = pieceNow.getRotateDirection(-1, rt);
							int newX = x;
							int newY = y;
							fld.copy(engine.field);
							pts = Integer.MIN_VALUE;

							if(!pieceNow.checkCollision(x, y, rot, fld)) {
								pts = thinkMain(x, y, rot, rt, fld, pieceNow, depth);
							} else if((engine.wallkick != null) && (engine.ruleopt.rotateWallkick)) {
								boolean allowUpward = (engine.ruleopt.rotateMaxUpwardWallkick < 0) ||
													  (engine.nowUpwardWallkickCount < engine.ruleopt.rotateMaxUpwardWallkick);
								WallkickResult kick = engine.wallkick.executeWallkick(x, y, -1, rt, rot,
													  allowUpward, pieceNow, fld, null);

								if(kick != null) {
									newX = x + kick.offsetX;
									newY = y + kick.offsetY;
									pts = thinkMain(newX, newY, rot, rt, fld, pieceNow, depth);
								}
							}

							if(pts > bestPts) {
								bestHold = false;
								bestX = x;
								bestY = y;
								bestRt = rt;
								bestXSub = newX;
								bestYSub = newY;
								bestRtSub = rot;
								bestPts = pts;
								if (DEBUG_ALL)
									logBest(4);
								thinkSuccess = true;
							}
						}

						// Right rotation
						if(engine.ruleopt.rotateButtonDefaultRight || engine.ruleopt.rotateButtonAllowReverse) {
							int rot = pieceNow.getRotateDirection(1, rt);
							int newX = x;
							int newY = y;
							fld.copy(engine.field);
							pts = Integer.MIN_VALUE;

							if(!pieceNow.checkCollision(x, y, rot, fld)) {
								pts = thinkMain(x, y, rot, rt, fld, pieceNow, depth);
							} else if((engine.wallkick != null) && (engine.ruleopt.rotateWallkick)) {
								boolean allowUpward = (engine.ruleopt.rotateMaxUpwardWallkick < 0) ||
													  (engine.nowUpwardWallkickCount < engine.ruleopt.rotateMaxUpwardWallkick);
								WallkickResult kick = engine.wallkick.executeWallkick(x, y, 1, rt, rot,
													  allowUpward, pieceNow, fld, null);

								if(kick != null) {
									newX = x + kick.offsetX;
									newY = y + kick.offsetY;
									pts = thinkMain(newX, newY, rot, rt, fld, pieceNow, depth);
								}
							}

							if(pts > bestPts) {
								bestHold = false;
								bestX = x;
								bestY = y;
								bestRt = rt;
								bestXSub = newX;
								bestYSub = newY;
								bestRtSub = rot;
								bestPts = pts;
								if (DEBUG_ALL)
									logBest(5);
								thinkSuccess = true;
							}
						}

						// 180-degree rotation
						if(engine.ruleopt.rotateButtonAllowDouble) {
							int rot = pieceNow.getRotateDirection(2, rt);
							int newX = x;
							int newY = y;
							fld.copy(engine.field);
							pts = Integer.MIN_VALUE;

							if(!pieceNow.checkCollision(x, y, rot, fld)) {
								pts = thinkMain(x, y, rot, rt, fld, pieceNow, depth);
							} else if((engine.wallkick != null) && (engine.ruleopt.rotateWallkick)) {
								boolean allowUpward = (engine.ruleopt.rotateMaxUpwardWallkick < 0) ||
													  (engine.nowUpwardWallkickCount < engine.ruleopt.rotateMaxUpwardWallkick);
								WallkickResult kick = engine.wallkick.executeWallkick(x, y, 2, rt, rot,
													  allowUpward, pieceNow, fld, null);

								if(kick != null) {
									newX = x + kick.offsetX;
									newY = y + kick.offsetY;
									pts = thinkMain(newX, newY, rot, rt, fld, pieceNow, depth);
								}
							}

							if(pts > bestPts) {
								bestHold = false;
								bestX = x;
								bestY = y;
								bestRt = rt;
								bestXSub = newX;
								bestYSub = newY;
								bestRtSub = rot;
								bestPts = pts;
								if (DEBUG_ALL)
									logBest(6);
								thinkSuccess = true;
							}
						}
						//}
					}
				}

				// Hold piece
				if((holdOK == true) && (pieceHold != null)) {
					int spawnX = engine.getSpawnPosX(engine.field, pieceHold);
					int spawnY = engine.getSpawnPosY(pieceHold);
					int minHoldX = Math.max(mostMovableX(spawnX, spawnY, -1, engine, engine.field, pieceHold, rt),
							pieceHold.getMostMovableLeft(spawnX, spawnY, rt, engine.field));
					int maxHoldX = Math.min(mostMovableX(spawnX, spawnY, 1, engine, engine.field, pieceHold, rt),
							pieceHold.getMostMovableRight(spawnX, spawnY, rt, engine.field));

					//Bonus for holding an I piece, penalty for holding an S or Z.
					int holdType = pieceHold.id;
					int holdPts = 0;
					if (holdType == Piece.PIECE_I)
						holdPts -= 30;
					else if (holdType == Piece.PIECE_S || holdType == Piece.PIECE_Z)
						holdPts += 30;
					else if (holdType == Piece.PIECE_O)
						holdPts += 10;
					int nowType = pieceNow.id;
					if (nowType == Piece.PIECE_I)
						holdPts += 30;
					else if (nowType == Piece.PIECE_S || nowType == Piece.PIECE_Z)
						holdPts -= 30;
					else if (nowType == Piece.PIECE_O)
						holdPts -= 10;

					for(int x = minHoldX; x <= maxHoldX; x+=move)
					{
						fld.copy(engine.field);
						int y = pieceHold.getBottom(x, spawnY, rt, fld);

						if(!pieceHold.checkCollision(x, y, rt, fld)) {
							// そのまま
							int pts = thinkMain(x, y, rt, -1, fld, pieceHold, depth);
							if (pts > Integer.MIN_VALUE+30)
								pts += holdPts;
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
									logBest(7);
								thinkSuccess = true;
							}
							//Check regardless
							//if((depth > 0) || (bestPts <= 10) || (pieceHold.id == Piece.PIECE_T)) {
							// Left shift
							fld.copy(engine.field);
							if(!pieceHold.checkCollision(x - move, y, rt, fld) && pieceHold.checkCollision(x - move, y - 1, rt, fld)) {
								pts = thinkMain(x - move, y, rt, -1, fld, pieceHold, depth);
								if (pts > Integer.MIN_VALUE+30)
									pts += holdPts;
								if(pts > bestPts) {
									bestHold = true;
									bestX = x;
									bestY = y;
									bestRt = rt;
									bestXSub = x - move;
									bestYSub = y;
									bestRtSub = -1;
									bestPts = pts;
									if (DEBUG_ALL)
										logBest(8);
									thinkSuccess = true;
								}
							}

							// Right shift
							fld.copy(engine.field);
							if(!pieceHold.checkCollision(x + move, y, rt, fld) && pieceHold.checkCollision(x + move, y - 1, rt, fld)) {
								pts = thinkMain(x + move, y, rt, -1, fld, pieceHold, depth);
								if (pts > Integer.MIN_VALUE+30)
									pts += holdPts;
								if(pts > bestPts) {
									bestHold = true;
									bestX = x;
									bestY = y;
									bestRt = rt;
									bestXSub = x + move;
									bestYSub = y;
									bestRtSub = -1;
									bestPts = pts;
									if (DEBUG_ALL)
										logBest(9);
									thinkSuccess = true;
								}
							}

							// Left rotation
							if(!engine.ruleopt.rotateButtonDefaultRight || engine.ruleopt.rotateButtonAllowReverse) {
								int rot = pieceHold.getRotateDirection(-1, rt);
								int newX = x;
								int newY = y;
								fld.copy(engine.field);
								pts = Integer.MIN_VALUE;

								if(!pieceHold.checkCollision(x, y, rot, fld)) {
									pts = thinkMain(x, y, rot, rt, fld, pieceHold, depth);
								} else if((engine.wallkick != null) && (engine.ruleopt.rotateWallkick)) {
									boolean allowUpward = (engine.ruleopt.rotateMaxUpwardWallkick < 0) ||
														  (engine.nowUpwardWallkickCount < engine.ruleopt.rotateMaxUpwardWallkick);
									WallkickResult kick = engine.wallkick.executeWallkick(x, y, -1, rt, rot,
														  allowUpward, pieceHold, fld, null);

									if(kick != null) {
										newX = x + kick.offsetX;
										newY = y + kick.offsetY;
										pts = thinkMain(newX, newY, rot, rt, fld, pieceHold, depth);
									}
								}
								if (pts > Integer.MIN_VALUE+30)
									pts += holdPts;
								if(pts > bestPts) {
									bestHold = true;
									bestX = x;
									bestY = y;
									bestRt = rt;
									bestXSub = newX;
									bestYSub = newY;
									bestRtSub = rot;
									bestPts = pts;
									if (DEBUG_ALL)
										logBest(10);
									thinkSuccess = true;
								}
							}

							// Right rotation
							if(engine.ruleopt.rotateButtonDefaultRight || engine.ruleopt.rotateButtonAllowReverse) {
								int rot = pieceHold.getRotateDirection(1, rt);
								int newX = x;
								int newY = y;
								fld.copy(engine.field);
								pts = Integer.MIN_VALUE;

								if(!pieceHold.checkCollision(x, y, rot, fld)) {
									pts = thinkMain(x, y, rot, rt, fld, pieceHold, depth);
								} else if((engine.wallkick != null) && (engine.ruleopt.rotateWallkick)) {
									boolean allowUpward = (engine.ruleopt.rotateMaxUpwardWallkick < 0) ||
														  (engine.nowUpwardWallkickCount < engine.ruleopt.rotateMaxUpwardWallkick);
									WallkickResult kick = engine.wallkick.executeWallkick(x, y, 1, rt, rot,
														  allowUpward, pieceHold, fld, null);

									if(kick != null) {
										newX = x + kick.offsetX;
										newY = y + kick.offsetY;
										pts = thinkMain(newX, newY, rot, rt, fld, pieceHold, depth);
									}
								}
								if (pts > Integer.MIN_VALUE+30)
									pts += holdPts;
								if(pts > bestPts) {
									bestHold = true;
									bestX = x;
									bestY = y;
									bestRt = rt;
									bestXSub = newX;
									bestYSub = newY;
									bestRtSub = rot;
									bestPts = pts;
									if (DEBUG_ALL)
										logBest(11);
									thinkSuccess = true;
								}
							}

							// 180-degree rotation
							if(engine.ruleopt.rotateButtonAllowDouble) {
								int rot = pieceHold.getRotateDirection(2, rt);
								int newX = x;
								int newY = y;
								fld.copy(engine.field);
								pts = Integer.MIN_VALUE;

								if(!pieceHold.checkCollision(x, y, rot, fld)) {
									pts = thinkMain(x, y, rot, rt, fld, pieceHold, depth);
								} else if((engine.wallkick != null) && (engine.ruleopt.rotateWallkick)) {
									boolean allowUpward = (engine.ruleopt.rotateMaxUpwardWallkick < 0) ||
														  (engine.nowUpwardWallkickCount < engine.ruleopt.rotateMaxUpwardWallkick);
									WallkickResult kick = engine.wallkick.executeWallkick(x, y, 2, rt, rot,
														  allowUpward, pieceHold, fld, null);

									if(kick != null) {
										newX = x + kick.offsetX;
										newY = y + kick.offsetY;
										pts = thinkMain(newX, newY, rot, rt, fld, pieceHold, depth);
									}
								}
								if (pts > Integer.MIN_VALUE+30)
									pts += holdPts;
								if(pts > bestPts) {
									bestHold = true;
									bestX = x;
									bestY = y;
									bestRt = rt;
									bestXSub = newX;
									bestYSub = newY;
									bestRtSub = rot;
									bestPts = pts;
									if (DEBUG_ALL)
										logBest(12);
									thinkSuccess = true;
								}
							}
						}
					}
				}
			}

			if(bestPts > 0)
				break;
			else
				bestPts = Integer.MIN_VALUE;
		}

		//thinkLastPieceNo++;

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
	 * @param depth Compromise level (ranges from 0 through getMaxThinkDepth-1)
	 * @return Evaluation score
	 */
	public int thinkMain(int x, int y, int rt, int rtOld, Field fld, Piece piece, int depth) {
		int pts = 0;

		boolean big = piece.big;
		int move = 1;
		if (big)
			move = 2;

		// Add points for being adjacent to other blocks
		if(piece.checkCollision(x - 1, y, fld)) pts += 1;
		if(piece.checkCollision(x + 1, y, fld)) pts += 1;
		if(piece.checkCollision(x, y - 1, fld)) pts += 1000;

		int width = fld.getWidth();
		int height = fld.getHeight();

		int xMin = piece.getMinimumBlockX()+x;
		int xMax = piece.getMaximumBlockX()+x;

		// Number of holes and valleys needing an I piece (before placement)
		int holeBefore = fld.getHowManyHoles();
		//int lidBefore = fld.getHowManyLidAboveHoles();

		//Check number of holes in rightmost column
		int testY = fld.getHiddenHeight();
		int holeBeforeRCol = 0;
		if (!big)
		{
			while (fld.getBlockEmpty(width-1, testY) && testY < height)
				testY++;
			while (!fld.getBlockEmpty(width-1, testY) && testY < height)
				testY++;
			while (testY < height)
			{
				if (fld.getBlockEmpty(width-1, testY))
					holeBeforeRCol++;
				testY++;
			}
		}
		//Fetch depths and find valleys that require an I, J, or L.
		int[] depthsBefore = getColumnDepths(fld);
		int deepestY = -1;
		//int deepestX = -1;
		for (int i = 0; i < width-1; i++)
			if (depthsBefore[i] > deepestY)
			{
				deepestY = depthsBefore[i];
				//deepestX = i;
			}
		int[] valleysBefore = calcValleys(depthsBefore, move);

		// Field height (before placement)
		int heightBefore = fld.getHighestBlockY();
		// T-Spin flag
		boolean tspin = false;
		if((piece.id == Piece.PIECE_T) && (rtOld != -1) && (fld.isTSpinSpot(x, y, piece.big))) {
			tspin = true;
		}

		//Does move fill in valley with an I piece?
		int valley = 0;
		if(piece.id == Piece.PIECE_I) {
			if (xMin == xMax && 0 <= xMin && xMin < width)
			{
				//if (DEBUG_ALL) log.debug("actualX = " + xMin);
				int xDepth = depthsBefore[xMin];
				int sideDepth = -1;
				if (xMin >= move)
					sideDepth = depthsBefore[xMin-move];
				if (xMin < width-move)
					sideDepth = Math.max(sideDepth, depthsBefore[xMin+move]);
				valley = xDepth - sideDepth;
				//if (DEBUG_ALL) log.debug("valley = " + valley);
			}
		}

		// ピースを置く
		if(!piece.placeToField(x, y, rt, fld)) {
			if (DEBUG_ALL)
				log.debug("End of thinkMain(" + x + ", " + y + ", " + rt + ", " + rtOld +
					", fld, piece " + Piece.PIECE_NAMES[piece.id] + ", " + depth + "). pts = 0 (Cannot place piece)");
			return Integer.MIN_VALUE;
		}

		// Line clear
		int lines = fld.checkLine()/move;
		if(lines > 0) {
			fld.clearLine();
			fld.downFloatingBlocks();
		}

		// All clear
		boolean allclear = fld.isEmpty();
		if(allclear) pts += 500000;

		// Field height (after clears)
		int heightAfter = fld.getHighestBlockY();

		int[] depthsAfter = getColumnDepths(fld);

		// Danger flag
		boolean danger = (heightBefore <= 4*(move+1));
		//Flag for really dangerously high stacks
		boolean peril = (heightBefore <= 2*(move+1));

		// Additional points for lower placements
		if((!danger) && (depth == 0))
			pts += y * 10;
		else
			pts += y * 20;

		int holeAfter = fld.getHowManyHoles();

		int rColPenalty = 1000;
		/*
		if (danger)
			rColPenalty = 100;
		*/
		//Apply score penalty if I piece would overflow canyon,
		//unless it would also uncover a hole.
		if (!big && piece.id == Piece.PIECE_I && holeBefore <= holeAfter && xMax == width-1)
		{
			int rValleyDepth = depthsAfter[width-1-move] - depthsAfter[width-1];
			if (rValleyDepth > 0)
				pts -= (rValleyDepth + 1) * rColPenalty;
		}
		//Bonus points for filling in valley with an I piece
		int valleyBonus = 0;
		if (valley == 3 && xMax < width-1)
			valleyBonus = 40000;
		else if (valley >= 4)
			valleyBonus = 400000;
		if (xMax == 0)
			valleyBonus *= 2;
		if (valley > 0 && DEBUG_ALL)
			log.debug("I piece xMax = " + xMax + ", valley depth = " + valley +
					", valley bonus = " + valleyBonus);
		pts += valleyBonus;
		if((lines == 1) && (!danger) && (depth == 0) && (heightAfter >= 16) && (holeBefore < 3) &&
				(!tspin) && (xMax == width-1)) {
			if (DEBUG_ALL) log.debug("End of thinkMain(" + x + ", " + y + ", " + rt + ", " + rtOld +
					", fld, piece " + Piece.PIECE_NAMES[piece.id] + ", " + depth + "). pts = 0 (Special Condition 3)");
			return Integer.MIN_VALUE;
		}
		//Points for line clears
		if (peril) {
			if(lines == 1) pts += 500000;
			if(lines == 2) pts += 1000000;
			if(lines == 3) pts += 30000000;
			if(lines >= 4) pts += 100000000;
		}
		else if((!danger) && (depth == 0)) {
			if(lines == 1) pts += 10;
			if(lines == 2) pts += 50;
			if(lines == 3) pts += 1000;
			if(lines >= 4) pts += 100000;
		}
		else {
			if(lines == 1) pts += 50000;
			if(lines == 2) pts += 100000;
			if(lines == 3) pts += 300000;
			if(lines >= 4) pts += 1000000;
		}

		if( (lines < 4) && (!allclear) ) {
			// Number of holes and valleys needing an I piece (after placement)
			//int lidAfter = fld.getHowManyLidAboveHoles();

			//Find valleys that need an I, J, or L.
			int[] valleysAfter = calcValleys(depthsAfter, move);

			if(holeAfter > holeBefore) {
				// Demerits for new holes
				if(depth == 0) return Integer.MIN_VALUE;
				pts -= (holeAfter - holeBefore) * 400;
			} else if(holeAfter < holeBefore) {
				// Add points for reduction in number of holes
				pts += 10000;
				if(!danger)
					pts += (holeBefore - holeAfter) * 200;
				else
					pts += (holeBefore - holeAfter) * 400;
			}

			/*
			if(lidAfter < lidBefore) {
				// Add points for reduction in number blocks above holes
				pts += (lidAfter - lidBefore) * 500;
			}
			*/

			if((tspin) && (lines >= 1)) {
				// T-Spin Bonus - retained from Basic AI, but should never actually trigger
				pts += 100000 * lines;
			}

			testY = fld.getHiddenHeight();
			int holeAfterRCol = 0;
			if (!big)
			{
				//Check number of holes in rightmost column
				while (fld.getBlockEmpty(width-1, testY) && testY < height)
					testY++;
				while (!fld.getBlockEmpty(width-1, testY) && testY < height)
					testY++;
				while (testY < height)
				{
					if (fld.getBlockEmpty(width-1, testY))
						holeAfterRCol++;
					testY++;
				}
				//Apply score penalty if non-I piece would plug up canyon
				int deltaRColHoles = holeAfterRCol - holeBeforeRCol;
				pts -= deltaRColHoles * rColPenalty;
			}

			//Bonuses and penalties for valleys that need I, J, or L.
			int needIValleyDiffScore = 0;
			if (valleysBefore[0] > 0)
				needIValleyDiffScore = 1 << valleysBefore[0];
			if (valleysAfter[0] > 0)
				needIValleyDiffScore -= 1 << valleysAfter[0];

			int needLJValleyDiffScore = 0;
			int needLOrJValleyDiffScore = 0;

			if (valleysBefore[1] > 3) {
				needLJValleyDiffScore += 1 << (valleysBefore[1] >> 1);
				needLOrJValleyDiffScore += (valleysBefore[1] & 1);
			} else {
				needLOrJValleyDiffScore += (valleysBefore[1] & 3);
			}
			if (valleysAfter[1] > 3) {
				needLJValleyDiffScore -= 1 << (valleysAfter[1] >> 1);
				needLOrJValleyDiffScore -= (valleysAfter[1] & 1);
			} else {
				needLOrJValleyDiffScore -= (valleysAfter[1] & 3);
			}
			if (valleysBefore[2] > 3) {
				needLJValleyDiffScore += 1 << (valleysBefore[2] >> 1);
				needLOrJValleyDiffScore += (valleysBefore[2] & 1);
			} else {
				needLOrJValleyDiffScore += (valleysBefore[2] & 3);
			}
			if (valleysAfter[2] > 3) {
				needLJValleyDiffScore -= 1 << (valleysAfter[2] >> 1);
				needLOrJValleyDiffScore -= (valleysAfter[2] & 1);
			} else {
				needLOrJValleyDiffScore -= (valleysAfter[2] & 3);
			}

			if(needIValleyDiffScore < 0 && holeAfter >= holeBefore) {
				pts += needIValleyDiffScore * 200;
				if(depth == 0) return Integer.MIN_VALUE;
			} else if(needIValleyDiffScore > 0) {
				if((depth == 0) && (!danger))
					pts += needIValleyDiffScore * 100;
				else
					pts += needIValleyDiffScore * 200;
			}
			if(needLJValleyDiffScore < 0 && holeAfter >= holeBefore) {
				pts += needLJValleyDiffScore * 40;
				if(depth == 0) return Integer.MIN_VALUE;
			} else if(needLJValleyDiffScore > 0) {
				if((depth == 0) && (!danger))
					pts += needLJValleyDiffScore * 20;
				else
					pts += needLJValleyDiffScore * 40;
			}

			if(needLOrJValleyDiffScore < 0 && holeAfter >= holeBefore) {
				pts += needLJValleyDiffScore * 40;
			} else if(needLOrJValleyDiffScore > 0) {
				if(!danger)
					pts += needLOrJValleyDiffScore * 20;
				else
					pts += needLOrJValleyDiffScore * 40;
			}

			if (!big)
			{
				//Bonus for pyramidal stack
				int mid = width/2-1;
				int d;
				for (int i = 0; i < mid-1; i++)
				{
					d = depthsAfter[i] - depthsAfter[i+1];
					if (d >= 0)
						pts += 10;
					else
						pts += d;
				}
				for (int i = mid+2; i < width; i++)
				{
					d = depthsAfter[i] - depthsAfter[i-1];
					if (d >= 0)
						pts += 10;
					else
						pts += d;
				}
				d = depthsAfter[mid-1] - depthsAfter[mid];
				if (d >= 0)
					pts += 5;
				else
					pts += d;
				d = depthsAfter[mid+1] - depthsAfter[mid];
				if (d >= 0)
					pts += 5;
				else
					pts += d;
			}

			if(heightBefore < heightAfter) {
				// Add points for reducing the height
				if((depth == 0) && (!danger))
					pts += (heightAfter - heightBefore) * 10;
				else
					pts += (heightAfter - heightBefore) * 20;
			} else if(heightBefore > heightAfter) {
				// Demerits for increase in height
				if((depth > 0) || (danger))
					pts -= (heightBefore - heightAfter) * 4;
			}

			//Penalty for prematurely filling in canyon
			if (!big && !danger && holeAfter >= holeBefore)
				for (int i = 0; i < width-1; i++)
					if (depthsAfter[i] > depthsAfter[width-1] &&
							depthsBefore[i] <= depthsBefore[width-1])
					{
						pts -= 1000000;
						break;
					}
			//Penalty for premature clears
			if (!big && lines > 0 && lines < 4 && heightAfter > 10 && xMax == width-1)
			{
				int minHi = 0;
				for (int i = 0; i < width-1; i++)
				{
					int hi = fld.getHighestBlockY(i);
					if (hi > minHi)
						minHi = hi;
				}
				if (minHi > height-4)
					pts -= 300000;
			}
			//Penalty for dangerous placements
			if (heightAfter < 2*move)
			{
				if (big)
				{
					if (heightAfter < 0 && heightBefore >= 0)
						return Integer.MIN_VALUE;
					int spawnMinX = width/2 - 3;
					int spawnMaxX = width/2 + 2;
					for (int i = spawnMinX; i <= spawnMaxX; i+=move)
						if (depthsAfter[i] < 2*move && depthsAfter[i] < depthsBefore[i])
							pts -= 2000000 * (depthsBefore[i] - depthsAfter[i]);
				}
				else
				{
					int spawnMinX = width/2 - 2;
					int spawnMaxX = width/2 + 1;
					for (int i = spawnMinX; i <= spawnMaxX; i++)
						if (depthsAfter[i] < 2 && depthsAfter[i] < depthsBefore[i])
							pts -= 2000000 * (depthsBefore[i] - depthsAfter[i]);
					if (heightBefore >= 2 && depth == 0)
						pts -= 2000000 * (heightBefore - heightAfter);
				}
			}
			int r2ColDepth = depthsAfter[width-2];
			if (!big && danger && r2ColDepth < depthsAfter[width-1])
			{
				//Bonus if edge clear is possible
				int maxLeftDepth = depthsAfter[0];
				for (int i = 1; i < width-2; i++)
					maxLeftDepth = Math.max(maxLeftDepth, depthsAfter[i]);
				if (r2ColDepth > maxLeftDepth)
					pts += 200;
			}
		}
		if (DEBUG_ALL)
			log.debug("End of thinkMain(" + x + ", " + y + ", " + rt + ", " + rtOld +
					", fld, piece " + Piece.PIECE_NAMES[piece.id] + ", " + depth + "). pts = " + pts);
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
	
	public static int[] calcValleys(int[] depths, int move)
	{
		int[] result = {0, 0, 0};
		if (depths[0] > depths[move])
			result[0] = (depths[0]-depths[move])/3/move;
		if ((move >= 2) && (depths[depths.length-1] > depths[depths.length-move-1]))
			result[0] = (depths[depths.length-1]-depths[depths.length-move-1])/3/move;
		for (int i = move; i < depths.length-move; i+=move)
		{
			int left = depths[i-move], right = depths[i+move];
			int lowerSide = Math.max(left, right);
			int diff = depths[i] - lowerSide;
			if (diff >= 3)
				result[0] += diff/3/move;
			if (left == right)
			{
				if (left == depths[i]+(2*move))
				{
					result[0]++;
					result[1]--;
					result[2]--;
				}
				else if (left == depths[i]+move)
				{
					result[1]++;
					result[2]++;
				}
			}
			if ((diff/move)%4 == 2)
			{
				if (left > right)
					result[1]+=2;
				else if (left < right)
					result[2]+=2;
				else
				{
					result[2]++;
					result[1]++;
				}
			}
		}
		if (((depths[0] - depths[move])/move)%4 == 2)
			result[2] += 2;
		if ((move >= 2) && ((depths[depths.length-1] - depths[depths.length-move-1])/move)%4 == 2)
			result[1] += 2;
		/*
		if ((depthsBefore[width-2] - depthsBefore[width-3])%4 == 2 &&
				(depthsBefore[width-1] - depthsBefore[width-2]) < 2)
			valleysBefore[1]++;
		valleysBefore[2] >>= 1;
		valleysBefore[1] >>= 1;
		*/
		return result;
	}
	
	/**
	 * @deprecated
	 * Workaround for the bug in Field.getHighestBlockY(int).
	 * The bug has since been fixed as of NullpoMino v6.5, so
	 * fld.getHighestBlockY(x) should be equivalent.
	 * @param fld Field
	 * @param x X coord
	 * @return Y coord of highest block
	 */
	@Deprecated
	public static int getColumnDepth (Field fld, int x)
	{
		int maxY = fld.getHeight()-1;
		int result = fld.getHighestBlockY(x);
		if (result == maxY && fld.getBlockEmpty(x, maxY))
			result++;
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
	/**
	 * Returns the farthest x position the piece can move.
	 * @param x X coord
	 * @param y Y coord
	 * @param dir -1 to move left, 1 to move right.
	 * @param engine GameEngine
	 * @param fld Field
	 * @param piece Piece
	 * @param rt Desired final rotation direction.
	 * @return The farthest x position in the direction that the piece can be moved to.
	 */
	public int mostMovableX (int x, int y, int dir, GameEngine engine, Field fld, Piece piece, int rt)
	{
		if (dir == 0)
			return x;
		int shift = 1;
		if (piece.big)
			shift = 2;
		int testX = x;
		int testY = y;
		int testRt = Piece.DIRECTION_UP;
		SpeedParam speed = engine.speed;
		if (speed.gravity >= 0 && speed.gravity < speed.denominator)
		{
			if (DEBUG_ALL)
				log.debug("mostMovableX not applicable - low gravity (gravity = " +
						speed.gravity + ", denominator = " + speed.denominator + ")");
			if (dir < 0)
				return piece.getMostMovableLeft(testX, testY, rt, fld);
			else if (dir > 0)
				return piece.getMostMovableRight(testX, testY, rt, fld);
		}
		if (piece.id == Piece.PIECE_I && dir > 0)
			return piece.getMostMovableRight(testX, testY, rt, fld);
		boolean floorKickOK = false;
		if ((piece.id == Piece.PIECE_I || piece.id == Piece.PIECE_T) &&
				((engine.nowUpwardWallkickCount < engine.ruleopt.rotateMaxUpwardWallkick
						|| engine.ruleopt.rotateMaxUpwardWallkick < 0) ||
						(engine.stat == GameEngine.STAT_ARE)))
			floorKickOK = true;
		testY = piece.getBottom(testX, testY, testRt, fld);
		if (piece.id == Piece.PIECE_T && piece.direction != Piece.DIRECTION_UP)
		{
			int testY2 = piece.getBottom(testX, testY, Piece.DIRECTION_DOWN, fld);
			if (testY2 > testY)
			{
				boolean kickRight = piece.checkCollision(testX+shift, testY2, testRt, fld);
				boolean kickLeft = piece.checkCollision(testX-shift, testY2, testRt, fld);
				if (kickRight)
				{
					testY = testY2;
					if (rt == Piece.DIRECTION_UP)
						testX+=shift;
				}
				else if (kickLeft)
				{
					testY = testY2;
					if (rt == Piece.DIRECTION_UP)
						testX-=shift;
				}
				else if (floorKickOK)
					floorKickOK = false;
				else
					return testX;
			}
		}
		while (true)
		{
			if (!piece.checkCollision(testX+dir, testY, testRt, fld))
				testX += dir;
			else if (testRt != rt)
			{
				testRt = rt;
				if (floorKickOK && piece.checkCollision(testX, testY, testRt, fld))
				{
					if (piece.id == Piece.PIECE_I)
					{
						if (piece.big)
							testY -= 4;
						else
							testY -= 2;
					}
					else
						testY--;
					floorKickOK = false;
				}
			}
			else
			{
				if (DEBUG_ALL)
					log.debug("mostMovableX(" + x + ", " + y + ", " + dir +
							", piece " + Piece.PIECE_NAMES[piece.id] + ", " + rt + ") = " + testX);
				if (piece.id == Piece.PIECE_I && testX < 0 && (rt&1) == 1)
				{
					int height1 = fld.getHighestBlockY(1);
					if (height1 < fld.getHighestBlockY(2) &&
							height1 < fld.getHighestBlockY(3)+2)
						return 0;
					else if (height1 > fld.getHighestBlockY(0))
						return -1;
				}
				return testX;
			}
			testY = piece.getBottom(testX, testY, testRt, fld);
		}
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

	/**
	 * Called to display internal state
	 * @param engine The GameEngine that owns this AI
	 * @param playerID Player ID
	 */
	public void renderState(GameEngine engine, int playerID){
		EventReceiver r = engine.owner.receiver;
		r.drawScoreFont(engine, playerID, 19, 33, getName().toUpperCase(), EventReceiver.COLOR_GREEN, 0.5f);
		r.drawScoreFont(engine, playerID, 24, 34, "X", EventReceiver.COLOR_BLUE, 0.5f);
		r.drawScoreFont(engine, playerID, 27, 34, "Y", EventReceiver.COLOR_BLUE, 0.5f);
		r.drawScoreFont(engine, playerID, 30, 34, "RT", EventReceiver.COLOR_BLUE, 0.5f);
		r.drawScoreFont(engine, playerID, 19, 35, "BEST:", EventReceiver.COLOR_BLUE, 0.5f);
		r.drawScoreFont(engine, playerID, 24, 35, String.valueOf(bestX), 0.5f);
		r.drawScoreFont(engine, playerID, 27, 35, String.valueOf(bestY), 0.5f);
		r.drawScoreFont(engine, playerID, 30, 35, String.valueOf(bestRt), 0.5f);
		r.drawScoreFont(engine, playerID, 19, 36, "SUB:", EventReceiver.COLOR_BLUE, 0.5f);
		r.drawScoreFont(engine, playerID, 24, 36, String.valueOf(bestXSub), 0.5f);
		r.drawScoreFont(engine, playerID, 27, 36, String.valueOf(bestYSub), 0.5f);
		r.drawScoreFont(engine, playerID, 30, 36, String.valueOf(bestRtSub), 0.5f);
		r.drawScoreFont(engine, playerID, 19, 37, "NOW:", EventReceiver.COLOR_BLUE, 0.5f);
		if (engine.nowPieceObject == null)
			r.drawScoreFont(engine, playerID, 24, 37, "-- -- --", 0.5f);
		else
		{
			r.drawScoreFont(engine, playerID, 24, 37, String.valueOf(engine.nowPieceX), 0.5f);
			r.drawScoreFont(engine, playerID, 27, 37, String.valueOf(engine.nowPieceY), 0.5f);
			r.drawScoreFont(engine, playerID, 30, 37, String.valueOf(engine.nowPieceObject.direction), 0.5f);
		}
		r.drawScoreFont(engine, playerID, 19, 38, "MOVE SCORE:", EventReceiver.COLOR_BLUE, 0.5f);
		r.drawScoreFont(engine, playerID, 31, 38, String.valueOf(bestPts), bestPts <= 0, 0.5f);
		r.drawScoreFont(engine, playerID, 19, 39, "THINK ACTIVE:", EventReceiver.COLOR_BLUE, 0.5f);
		r.drawScoreFont(engine, playerID, 32, 39, GeneralUtil.getOorX(thinking), 0.5f);
		r.drawScoreFont(engine, playerID, 19, 40, "THINK REQUEST:", EventReceiver.COLOR_BLUE, 0.5f);
		r.drawScoreFont(engine, playerID, 33, 40, GeneralUtil.getOorX(thinkRequest), 0.5f);
		r.drawScoreFont(engine, playerID, 19, 41, "THINK SUCCESS:", EventReceiver.COLOR_BLUE, 0.5f);
		r.drawScoreFont(engine, playerID, 33, 41, GeneralUtil.getOorX(thinkSuccess), 0.5f);
		r.drawScoreFont(engine, playerID, 19, 42, "THINK COMPLETE:", EventReceiver.COLOR_BLUE, 0.5f);
		r.drawScoreFont(engine, playerID, 34, 42, GeneralUtil.getOorX(thinkComplete), 0.5f);
		r.drawScoreFont(engine, playerID, 19, 43, "IN ARE:", EventReceiver.COLOR_BLUE, 0.5f);
		r.drawScoreFont(engine, playerID, 26, 43, GeneralUtil.getOorX(inARE), 0.5f);
	}

	/*
	 * スレッドの処理
	 */
	public void run() {
		log.info("PoochyBot: Thread start");
		threadRunning = true;

		while(threadRunning) {
			if(thinkRequest) {
				thinkRequest = false;
				thinking = true;
				try {
					thinkBestPosition(gEngine, gEngine.playerID);
					thinkComplete = true;
					log.debug("PoochyBot: thinkBestPosition completed successfully");
				} catch (Throwable e) {
					log.debug("PoochyBot: thinkBestPosition Failed", e);
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
		log.info("PoochyBot: Thread end");
	}
}
