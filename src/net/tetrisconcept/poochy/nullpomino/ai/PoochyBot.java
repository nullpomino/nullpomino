package net.tetrisconcept.poochy.nullpomino.ai;

import org.apache.log4j.Logger;
import org.game_host.hebo.nullpomino.game.component.Controller;
import org.game_host.hebo.nullpomino.game.component.Field;
import org.game_host.hebo.nullpomino.game.component.Piece;
import org.game_host.hebo.nullpomino.game.component.SpeedParam;
import org.game_host.hebo.nullpomino.game.component.WallkickResult;
import org.game_host.hebo.nullpomino.game.play.GameEngine;
import org.game_host.hebo.nullpomino.game.play.GameManager;
import org.game_host.hebo.nullpomino.game.subsystem.ai.DummyAI;

/**
 * PoochyBot AI
 * @author Poochy.EXE
 *         Poochy.Spambucket@gmail.com
 */
public class PoochyBot extends DummyAI implements Runnable {
	/** ログ */
	static Logger log = Logger.getLogger(PoochyBot.class);

	/** ホールド使用予定 */
	public boolean bestHold;

	/** 置く予定のX座標 */
	public int bestX;

	/** 置く予定のY座標 */
	public int bestY;

	/** 置く予定の方向 */
	public int bestRt;

	/** 接地したあとのX座標 */
	public int bestXSub;

	/** 接地したあとのY座標 */
	public int bestYSub;

	/** 接地したあとの方向(-1：なし) */
	public int bestRtSub;

	/** 最善手の評価得点 */
	public int bestPts;

	/** 移動を遅らせる用の変数 */
	public int delay;

	/** このAIを所持するGameEngine */
	public GameEngine gEngine;

	/** このAIを所有するGameManager */
	public GameManager gManager;

	/** trueならスレッドに思考ルーチンの実行を指示 */
	public boolean thinkRequest;

	/** trueならスレッドが思考ルーチン実行中 */
	public boolean thinking;

	/** スレッドを停止させる時間 */
	public int thinkDelay;

	/** 現在のピースの番号 */
	public int thinkCurrentPieceNo;

	/** 思考が終わったピースの番号 */
	public int thinkLastPieceNo;

	/** trueならスレッド動作中 */
	public volatile boolean threadRunning;

	/** 思考ルーチン実行用スレッド */
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
	/** 最大妥協レベル */
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
		return "PoochyBot V1.23";
	}

	/*
	 * 初期化処理
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
	 * 新しいピース出現時の処理
	 */
	public void newPiece(GameEngine engine, int playerID) {
		if(!engine.aiUseThread) {
			thinkBestPosition(engine, playerID);
		} else if (!thinking && !thinkComplete) {
			thinkRequest = true;
			thinkCurrentPieceNo++;
		}
	}

	/*
	 * 各フレームの最初の処理
	 */
	public void onFirst(GameEngine engine, int playerID) {
		inputARE = 0;
		boolean newInARE = engine.stat == GameEngine.STAT_ARE ||
			engine.stat == GameEngine.STAT_READY;
		if ((newInARE && !inARE) || (!thinking && !thinkSuccess))
		{
			debugOut("Begin pre-think of next piece.");
			inARE = newInARE;
			thinkComplete = false;
			thinkRequest = true;
		}
		else if (inARE && !newInARE)
			inARE = false;
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
			if (threadRunning && !thinking && (thinkCurrentPieceNo <= thinkLastPieceNo))
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
			debugOut("Currently in ARE. Next piece type = " + nextPiece.id + ", IRS = " + input);
			//engine.ctrl.setButtonBit(input);
			inputARE = input;
		}
	}

	/*
	 * 各フレームの最後の処理
	 */
	public void onLast(GameEngine engine, int playerID) {
	}

	/*
	 * ボタン入力状態を設定
	 */
	public void setControl(GameEngine engine, int playerID, Controller ctrl) {
		if( (engine.nowPieceObject != null) && (engine.stat == GameEngine.STAT_MOVE) &&
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
				debugOut("Needs rethink - piece is stuck!");
			}
			*/
			/*
			if (rt == Piece.DIRECTION_DOWN &&
					((nowType == Piece.PIECE_L && bestX > nowX) || (nowType == Piece.PIECE_J && bestX < nowX)))
				{
					debugOut("Checking for stuck L or J piece.");
					debugOut("Coordinates of piece: x = " + nowX + ", y = " + nowY);
					debugOut("Coordinates of block to check: x = " + (pieceNow.getMaximumBlockX()+nowX-1) +
							", y = " + (pieceNow.getMaximumBlockY()+nowY));
					for (int xCheck = 0; xCheck < fld.getWidth(); xCheck++)
						debugOut("fld.getHighestBlockY(" + xCheck + ") = " + fld.getHighestBlockY(xCheck));
				}
			*/
			if ((rt == Piece.DIRECTION_DOWN &&
					((nowType == Piece.PIECE_L && bestX > nowX) || (nowType == Piece.PIECE_J && bestX < nowX))
					&& !fld.getBlockEmpty(pieceNow.getMaximumBlockX()+nowX-1, pieceNow.getMaximumBlockY()+nowY)))
			{
				thinkRequest = true;
				thinkComplete = false;
				debugOut("Needs rethink - L or J piece is stuck!");
			}
			if (nowType == Piece.PIECE_O && ((bestX < nowX && pieceNow.checkCollision(nowX-1, nowY, rt, fld))
					|| (bestX < nowX && pieceNow.checkCollision(nowX-1, nowY, rt, fld))))
			{
				thinkRequest = true;
				thinkComplete = false;
				debugOut("Needs rethink - O piece is stuck!");
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
				debugOut("Needs rethink - piece is stuck!");
			}
			if (nowX == lastX && nowY == lastY && rt == lastRt && lastInput != 0)
			{
				sameStatusTime++;
				if (sameStatusTime > 4)
				{
					thinkRequest = true;
					thinkComplete = false;
					debugOut("Needs rethink - piece is stuck, last inputs had no effect!");
				}
			}
			if (engine.nowPieceRotateCount >= 8)
			{
				thinkRequest = true;
				thinkComplete = false;
				debugOut("Needs rethink - piece is stuck, too many rotations!");
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
				debugOut("bestX = " + bestX + ", nowX = " + nowX +
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
								debugOut("Stuck I piece - use hold");
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
								debugOut("Stuck I piece - use hold");
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
					//debugOut("Case 1 rotation");

					int lrot = engine.getRotateDirection(-1);
					int rrot = engine.getRotateDirection(1);
					debugOut("lrot = " + lrot + ", rrot = " + rrot);

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
					//debugOut("Case 2 rotation");

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
					debugOut("Needs rethink - cannot reach desired position");
				} else {
					// 到達できる場合
					if((nowX == bestX) && (pieceTouchGround)) {
						if (rt == bestRt) {
							// 接地回転
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
					debugOut("Delaying rotation on L piece to avoid getting stuck. (Case 1)");
					sync = false;
					rotateDir = 0;
					moveDir = 1;
				}
				else if (bestX > nowX)
				{
					/*
					if (minBlockXDepth == fld.getHighestBlockY(minBlockX-1))
					{
						debugOut("Delaying rotation on L piece to avoid getting stuck. (Case 2)");
						sync = false;
						rotateDir = 0;
						moveDir = -1;
					}
					else
					*/
					debugOut("Attempting synchro move on L piece to avoid getting stuck.");
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
					debugOut("Delaying rotation on J piece to avoid getting stuck. (Case 1)");
					sync = false;
					rotateDir = 0;
					moveDir = -1;
				}
				else if (bestX < nowX)
				{
					/*
					if (maxBlockXDepth == fld.getHighestBlockY(maxBlockX+1))
					{
						debugOut("Delaying rotation on J piece to avoid getting stuck. (Case 2)");
						sync = false;
						rotateDir = 0;
						moveDir = 1;
					}
					else
					*/
					debugOut("Attempting synchro move on J piece to avoid getting stuck.");
					sync = true;
					rotateDir = 1;
					moveDir = 1;
				}
			}
			else if (rotateDir != 0 && moveDir != 0 && pieceTouchGround && (rt&1) == 1
					&& (nowType == Piece.PIECE_J || nowType == Piece.PIECE_L)
					&& !pieceNow.checkCollision(nowX+moveDir, nowY+1, rt, fld))
			{
				debugOut("Delaying move on L or J piece to avoid getting stuck.");
				sync = false;
				moveDir = 0;
			}
			if (engine.nowPieceRotateCount >= 5 && rotateDir != 0 && moveDir != 0 && !sync)
			{
				debugOut("Piece seems to be stuck due to unintentional synchro - trying intentional desync.");
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
					debugOut("Movement error: Nothing to do!");
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
			if (sync)
			{
				debugOut("Attempting to perform synchro move.");
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


			debugOut ("Input = " + input + ", moveDir = " + moveDir  + ", rotateDir = " + rotateDir +
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
		String result = "Piece ";
		switch (pieceType)
		{
			case Piece.PIECE_I: result = result + "I"; break;
			case Piece.PIECE_L: result = result + "L"; break;
			case Piece.PIECE_O: result = result + "O"; break;
			case Piece.PIECE_Z: result = result + "Z"; break;
			case Piece.PIECE_T: result = result + "T"; break;
			case Piece.PIECE_J: result = result + "J"; break;
			case Piece.PIECE_S: result = result + "S"; break;
			case Piece.PIECE_I1: result = result + "I1"; break;
			case Piece.PIECE_I2: result = result + "I2"; break;
			case Piece.PIECE_I3: result = result + "I3"; break;
			case Piece.PIECE_L3: result = result + "L3"; break;
		}
		result = result + ", direction ";

		switch (rt)
		{
			case Piece.DIRECTION_LEFT:  result = result + "left";  break;
			case Piece.DIRECTION_DOWN:  result = result + "down";  break;
			case Piece.DIRECTION_UP:    result = result + "up";    break;
			case Piece.DIRECTION_RIGHT: result = result + "right"; break;
		}
		debugOut(result);
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
	 * 最善手を探す
	 * @param engine このAIを所有するGameEngine
	 * @param playerID プレイヤーID
	 */
	public void thinkBestPosition(GameEngine engine, int playerID) {
		debugOut("thinkBestPosition called, inARE = " + inARE + ", piece: ");
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
				for(int x = minX; x <= maxX && spawnOK; x++) {
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
							debugOut("New best position found (Case 1): bestHold = false" +
									", bestX = " + x +
									", bestY = " + y +
									", bestRt = " + rt +
									", bestXSub = " + x +
									", bestYSub = " + y +
									", bestRtSub = " + -1 +
									", bestPts = " + pts);
							thinkSuccess = true;
						}
						//Check regardless
						//if((depth > 0) || (bestPts <= 10) || (pieceNow.id == Piece.PIECE_T)) {
						// Left shift
						fld.copy(engine.field);
						if(!pieceNow.checkCollision(x - 1, y, rt, fld) && pieceNow.checkCollision(x - 1, y - 1, rt, fld)) {
							pts = thinkMain(x - 1, y, rt, -1, fld, pieceNow, depth);

							if(pts > bestPts) {
								bestHold = false;
								bestX = x;
								bestY = y;
								bestRt = rt;
								bestXSub = x - 1;
								bestYSub = y;
								bestRtSub = -1;
								bestPts = pts;
								debugOut("New best position found (Case 2): bestHold = false" +
										", bestX = " + x +
										", bestY = " + y +
										", bestRt = " + rt +
										", bestXSub = " + (x-1) +
										", bestYSub = " + y +
										", bestRtSub = " + -1 +
										", bestPts = " + pts);
								thinkSuccess = true;
							}
						}

						// Right shift
						fld.copy(engine.field);
						if(!pieceNow.checkCollision(x + 1, y, rt, fld) && pieceNow.checkCollision(x + 1, y - 1, rt, fld)) {
							pts = thinkMain(x + 1, y, rt, -1, fld, pieceNow, depth);

							if(pts > bestPts) {
								bestHold = false;
								bestX = x;
								bestY = y;
								bestRt = rt;
								bestXSub = x + 1;
								bestYSub = y;
								bestRtSub = -1;
								bestPts = pts;
								debugOut("New best position found (Case 3): bestHold = false" +
										", bestX = " + x +
										", bestY = " + y +
										", bestRt = " + rt +
										", bestXSub = " + (x+1) +
										", bestYSub = " + y +
										", bestRtSub = " + -1 +
										", bestPts = " + pts);
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
								debugOut("New best position found (Case 4): bestHold = false" +
										", bestX = " + x +
										", bestY = " + y +
										", bestRt = " + rt +
										", bestXSub = " + newX +
										", bestYSub = " + newY +
										", bestRtSub = " + rot +
										", bestPts = " + pts);
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
								debugOut("New best position found (Case 5): bestHold = false" +
										", bestX = " + x +
										", bestY = " + y +
										", bestRt = " + rt +
										", bestXSub = " + newX +
										", bestYSub = " + newY +
										", bestRtSub = " + rot +
										", bestPts = " + pts);
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
								debugOut("New best position found (Case 6): bestHold = false" +
										", bestX = " + x +
										", bestY = " + y +
										", bestRt = " + rt +
										", bestXSub = " + newX +
										", bestYSub = " + newY +
										", bestRtSub = " + rot +
										", bestPts = " + pts);
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
					else if (holdType == Piece.PIECE_O)
						holdPts -= 10;
					
					for(int x = minHoldX; x <= maxHoldX; x++)
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
								debugOut("New best position found (Case 7): bestHold = true" +
										", bestX = " + x +
										", bestY = " + y +
										", bestRt = " + rt +
										", bestXSub = " + x +
										", bestYSub = " + y +
										", bestRtSub = " + -1 +
										", bestPts = " + pts);
							}
							//Check regardless
							//if((depth > 0) || (bestPts <= 10) || (pieceHold.id == Piece.PIECE_T)) {
							// Left shift
							fld.copy(engine.field);
							if(!pieceHold.checkCollision(x - 1, y, rt, fld) && pieceHold.checkCollision(x - 1, y - 1, rt, fld)) {
								pts = thinkMain(x - 1, y, rt, -1, fld, pieceHold, depth);
								if (pts > Integer.MIN_VALUE+30)
									pts += holdPts;
								if(pts > bestPts) {
									bestHold = true;
									bestX = x;
									bestY = y;
									bestRt = rt;
									bestXSub = x - 1;
									bestYSub = y;
									bestRtSub = -1;
									bestPts = pts;
									debugOut("New best position found (Case 8): bestHold = true" +
											", bestX = " + x +
											", bestY = " + y +
											", bestRt = " + rt +
											", bestXSub = " + (x-1) +
											", bestYSub = " + y +
											", bestRtSub = " + -1 +
											", bestPts = " + pts);
								}
							}
	
							// Right shift
							fld.copy(engine.field);
							if(!pieceHold.checkCollision(x + 1, y, rt, fld) && pieceHold.checkCollision(x + 1, y - 1, rt, fld)) {
								pts = thinkMain(x + 1, y, rt, -1, fld, pieceHold, depth);
								if (pts > Integer.MIN_VALUE+30)
									pts += holdPts;
								if(pts > bestPts) {
									bestHold = true;
									bestX = x;
									bestY = y;
									bestRt = rt;
									bestXSub = x + 1;
									bestYSub = y;
									bestRtSub = -1;
									bestPts = pts;
									debugOut("New best position found (Case 9): bestHold = true" +
											", bestX = " + x +
											", bestY = " + y +
											", bestRt = " + rt +
											", bestXSub = " + (x+1) +
											", bestYSub = " + y +
											", bestRtSub = " + -1 +
											", bestPts = " + pts);
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
									debugOut("New best position found (Case 10): bestHold = true" +
											", bestX = " + x +
											", bestY = " + y +
											", bestRt = " + rt +
											", bestXSub = " + newX +
											", bestYSub = " + newY +
											", bestRtSub = " + rot +
											", bestPts = " + pts);
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
									debugOut("New best position found (Case 11): bestHold = true" +
											", bestX = " + x +
											", bestY = " + y +
											", bestRt = " + rt +
											", bestXSub = " + newX +
											", bestYSub = " + newY +
											", bestRtSub = " + rot +
											", bestPts = " + pts);
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
									debugOut("New best position found (Case 12): bestHold = true" +
											", bestX = " + x +
											", bestY = " + y +
											", bestRt = " + rt +
											", bestXSub = " + newX +
											", bestYSub = " + newY +
											", bestRtSub = " + rot +
											", bestPts = " + pts);
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

		thinkLastPieceNo++;

		//System.out.println("X:" + bestX + " Y:" + bestY + " R:" + bestRt + " H:" + bestHold + " Pts:" + bestPts);
	}

	/**
	 * 思考ルーチン
	 * @param x X座標
	 * @param y Y座標
	 * @param rt 方向
	 * @param rtOld 回転前の方向（-1：なし）
	 * @param fld フィールド（どんなに弄っても問題なし）
	 * @param piece ピース
	 * @param depth 妥協レベル（0からgetMaxThinkDepth()-1まで）
	 * @return 評価得点
	 */
	public int thinkMain(int x, int y, int rt, int rtOld, Field fld, Piece piece, int depth) {
		int pts = 0;

		// 他のブロックに隣接していると加点
		if(piece.checkCollision(x - 1, y, fld)) pts += 1;
		if(piece.checkCollision(x + 1, y, fld)) pts += 1;
		if(piece.checkCollision(x, y - 1, fld)) pts += 1000;

		int width = fld.getWidth();
		int height = fld.getHeight();

		int xMin = piece.getMinimumBlockX()+x;
		int xMax = piece.getMaximumBlockX()+x;

		// 穴の数とI型が必要な谷の数（設置前）
		int holeBefore = fld.getHowManyHoles();
		//int lidBefore = fld.getHowManyLidAboveHoles();

		//Check number of holes in rightmost column
		int testY = fld.getHiddenHeight();
		int holeBeforeRCol = 0;
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
		//Fetch depths and find valleys that require an I, J, or L.
		int[] depthsBefore = getColumnDepths(fld);
		int deepestY = -1;
		//int deepestX = -1;
		int needIValleyBefore = 0, needJValleyBefore = 0, needLValleyBefore = 0;
		for (int i = 0; i < width-1; i++)
			if (depthsBefore[i] > deepestY)
			{
				deepestY = depthsBefore[i];
				//deepestX = i;
			}
		if (depthsBefore[0] > depthsBefore[1])
			needIValleyBefore = (depthsBefore[0]-depthsBefore[1])/3;
		for (int i = 1; i < width-1; i++)
		{
			int left = depthsBefore[i-1], right = depthsBefore[i+1];
			int lowerSide = Math.max(left, right);
			int diff = depthsBefore[i] - lowerSide;
			if (diff >= 3)
				needIValleyBefore += diff/3;
			if (left == right)
			{
				if (left == depthsBefore[i]+2)
				{
					needIValleyBefore++;
					needLValleyBefore--;
					needJValleyBefore--;
				}
				else if (left == depthsBefore[i]+1)
				{
					needLValleyBefore++;
					needJValleyBefore++;
				}
			}
			if (diff%4 == 2)
			{
				if (left > right)
					needLValleyBefore+=2;
				else if (left < right)
					needJValleyBefore+=2;
				else
				{
					needJValleyBefore++;
					needLValleyBefore++;
				}
			}
		}
		if ((depthsBefore[0] - depthsBefore[1])%4 == 2)
			needJValleyBefore += 2;
		/*
		if ((depthsBefore[width-2] - depthsBefore[width-3])%4 == 2 &&
				(depthsBefore[width-1] - depthsBefore[width-2]) < 2)
			needLValleyBefore++;
		*/
		needJValleyBefore >>= 1;
		needLValleyBefore >>= 1;

		// フィールドの高さ（設置前）
		int heightBefore = fld.getHighestBlockY();
		// T-Spinフラグ
		boolean tspin = false;
		if((piece.id == Piece.PIECE_T) && (rtOld != -1) && (fld.isTSpinSpot(x, y, piece.big))) {
			tspin = true;
		}

		//Does move fill in valley with an I piece?
		int valley = 0;
		if(piece.id == Piece.PIECE_I) {
			if (xMin == xMax && 0 <= xMin && xMin < width)
			{
				//debugOut("actualX = " + xMin);
				int xDepth = depthsBefore[xMin];
				int sideDepth = -1;
				if (xMin > 0)
					sideDepth = depthsBefore[xMin-1];
				if (xMin < width-1)
					sideDepth = Math.max(sideDepth, depthsBefore[xMin+1]);
				valley = xDepth - sideDepth;
				//debugOut("valley = " + valley);
			}
		}

		// ピースを置く
		if(!piece.placeToField(x, y, rt, fld)) {
			debugOut("End of thinkMain(" + x + ", " + y + ", " + rt + ", " + rtOld +
					", fld, piece " + piece.id + ", " + depth + "). pts = 0 (Cannot place piece)");
			return Integer.MIN_VALUE;
		}

		// ライン消去
		int lines = fld.checkLine();
		if(lines > 0) {
			fld.clearLine();
			fld.downFloatingBlocks();
		}

		// 全消し
		boolean allclear = fld.isEmpty();
		if(allclear) pts += 500000;

		// フィールドの高さ（消去後）
		int heightAfter = fld.getHighestBlockY();

		int[] depthsAfter = getColumnDepths(fld);

		// 危険フラグ
		boolean danger = (heightBefore <= 8);
		//Flag for really dangerously high stacks
		boolean peril = (heightBefore <= 4);

		// 下に置くほど加点
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
		if (piece.id == Piece.PIECE_I && holeBefore <= holeAfter && xMax == width-1)
		{
			int rValleyDepth = depthsAfter[width-2] - depthsAfter[width-1];
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
		if (valley > 0)
			debugOut("I piece xMax = " + xMax + ", valley depth = " + valley +
					", valley bonus = " + valleyBonus);
		pts += valleyBonus;
		if((lines == 1) && (!danger) && (depth == 0) && (heightAfter >= 16) && (holeBefore < 3) && (!tspin)) {
			debugOut("End of thinkMain(" + x + ", " + y + ", " + rt + ", " + rtOld +
					", fld, piece " + piece.id + ", " + depth + "). pts = 0 (Special Condition 3)");
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
			// 穴の数とI型が必要な谷の数（設置後）
			//int lidAfter = fld.getHowManyLidAboveHoles();

			//Find valleys that need an I, J, or L.
			int needIValleyAfter = 0, needJValleyAfter = 0, needLValleyAfter = 0;
			if (depthsAfter[0] > depthsAfter[1])
				needIValleyAfter = (depthsAfter[0]-depthsAfter[1])/3;
			for (int i = 1; i < width-1; i++)
			{
				int left = depthsAfter[i-1], right = depthsAfter[i+1];
				int lowerSide = Math.max(left, right);
				int diff = depthsAfter[i] - lowerSide;
				if (diff >= 3)
					needIValleyAfter += diff/3;
				if (left == right)
				{
					if (left == depthsAfter[i]+2)
					{
						needIValleyAfter++;
						needLValleyAfter--;
						needJValleyAfter--;
					}
					else if (left == depthsAfter[i]+1)
					{
						needLValleyAfter++;
						needJValleyAfter++;
					}
				}
				if (diff%4 == 2)
				{
					if (left > right)
						needLValleyAfter+=2;
					else if (left < right)
						needJValleyAfter+=2;
					else
					{
						needJValleyAfter++;
						needLValleyAfter++;
					}
				}
			}
			if ((depthsAfter[0] - depthsAfter[1])%4 == 2)
				needJValleyAfter += 2;
			/*
			if ((depthsAfter[width-2] - depthsAfter[width-3])%4 == 2 &&
					(depthsAfter[width-1] - depthsAfter[width-2]) < 2)
				needLValleyAfter++;
			*/

			needJValleyAfter >>= 1;
			needLValleyAfter >>= 1;

			if(holeAfter > holeBefore) {
				// 新たに穴ができると減点
				pts -= (holeAfter - holeBefore) * 400;
				if(depth == 0) return Integer.MIN_VALUE;
			} else if(holeAfter < holeBefore) {
				// 穴を減らすと加点
				pts += 10000;
				if(!danger)
					pts += (holeBefore - holeAfter) * 200;
				else
					pts += (holeBefore - holeAfter) * 400;
			}

			/*
			if(lidAfter < lidBefore) {
				// 穴の上に乗っているブロックを減らすと加点
				pts += (lidAfter - lidBefore) * 500;
			}
			*/

			if((tspin) && (lines >= 1)) {
				// T-Spin Bonus - retained from Basic AI, but should never actually trigger
				pts += 100000 * lines;
			}

			//Check number of holes in rightmost column
			testY = fld.getHiddenHeight();
			int holeAfterRCol = 0;
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

			//Bonuses and penalties for valleys that need I, J, or L.
			int needIValleyDiffScore = 0;
			if (needIValleyBefore > 0)
				needIValleyDiffScore = 1 << needIValleyBefore;
			if (needIValleyAfter > 0)
				needIValleyDiffScore -= 1 << needIValleyAfter;

			int needLJValleyDiffScore = 0;

			if (needJValleyBefore > 1)
				needLJValleyDiffScore += 1 << needJValleyBefore;
			if (needJValleyAfter > 1)
				needLJValleyDiffScore -= 1 << needJValleyAfter;
			if (needLValleyBefore > 1)
				needLJValleyDiffScore += 1 << needLValleyBefore;
			if (needLValleyAfter > 1)
				needLJValleyDiffScore -= 1 << needLValleyAfter;

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

			if(heightBefore < heightAfter) {
				// 高さを抑えると加点
				if((depth == 0) && (!danger))
					pts += (heightAfter - heightBefore) * 10;
				else
					pts += (heightAfter - heightBefore) * 20;
			} else if(heightBefore > heightAfter) {
				// 高くすると減点
				if((depth > 0) || (danger))
					pts -= (heightBefore - heightAfter) * 4;
			}

			//Penalty for prematurely filling in canyon
			if (!danger && holeAfter >= holeBefore)
				for (int i = 0; i < width-1; i++)
					if (depthsAfter[i] > depthsAfter[width-1] &&
							depthsBefore[i] <= depthsBefore[width-1])
					{
						pts -= 1000000;
						break;
					}
			//Penalty for premature clears
			if (lines > 0 && lines < 4 && heightAfter > 10 && xMax == width-1)
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
			if (heightAfter < 2)
			{
				int spawnMinX = width/2 - 2;
				int spawnMaxX = width/2 + 1;
				for (int i = spawnMinX; i <= spawnMaxX; i++)
					if (depthsAfter[i] < 2 && depthsAfter[i] < depthsBefore[i])
						pts -= 2000000 * (depthsBefore[i] - depthsAfter[i]);
				if (heightBefore >= 2 && depth == 0)
					pts -= 2000000 * (heightBefore - heightAfter);
			}
			int r2ColDepth = depthsAfter[width-2];
			if (danger && r2ColDepth < depthsAfter[width-1])
			{
				//Bonus if edge clear is possible
				int maxLeftDepth = depthsAfter[0];
				for (int i = 1; i < width-2; i++)
					maxLeftDepth = Math.max(maxLeftDepth, depthsAfter[i]);
				if (r2ColDepth > maxLeftDepth)
					pts += 200;
			}
		}
		debugOut("End of thinkMain(" + x + ", " + y + ", " + rt + ", " + rtOld +
				", fld, piece " + piece.id + ", " + depth + "). pts = " + pts);
		return pts;
	}
	//private static final int[][] HI_PENALTY = {{6, 2}, {7, 6}, {6, 2}, {1, 0}};
	public Piece checkOffset(Piece p, GameEngine engine)
	{
		if (!p.offsetApplied)
		{
			Piece result = new Piece(p);
			result.applyOffsetArray(engine.ruleopt.pieceOffsetX[p.id], engine.ruleopt.pieceOffsetY[p.id]);
			return result;
		}
		else
			return p;
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
	public int getColumnDepth (Field fld, int x)
	{
		int maxY = fld.getHeight()-1;
		int result = fld.getHighestBlockY(x);
		if (result == maxY && fld.getBlockEmpty(x, maxY))
			result++;
		return result;
	}

	public int[] getColumnDepths (Field fld)
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
			debugOut("mostMovableX not applicable - low gravity (gravity = " + speed.gravity
					+ ", denominator = " + speed.denominator + ")");
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
				debugOut("mostMovableX(" + x + ", " + y + ", " + dir + ", piece " + piece.id +
						", " + rt + ") = " + testX);
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
	public int getHighestBlockY(int x, Field fld, int max)
	{
		int y = fld.getHiddenHeight() * -1;
		do {
			y++;
		} while(fld.getBlockEmpty(x, y) && y < max);
		return y;
	}
	protected void debugOut(String str)
	{
		if (DEBUG_ALL)
			log.debug(str);
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