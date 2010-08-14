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
package mu.nu.nullpo.game.subsystem.ai;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;

import mu.nu.nullpo.game.component.Controller;
import mu.nu.nullpo.game.component.Field;
import mu.nu.nullpo.game.component.Piece;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.game.play.GameManager;
import mu.nu.nullpo.tool.airanksgenerator.Ranks;

import org.apache.log4j.Logger;

public class RanksAI extends DummyAI implements Runnable {

	static Logger log = Logger.getLogger(RanksAI.class);


	public boolean bestHold;


	public int bestX;

	public int bestY;


	public int bestRt;


	public int bestXSub;


	public int bestYSub;


	public int bestRtSub;


	public int bestPts;


	public boolean forceHold;


	public int delay;


	public GameEngine gEngine;


	public GameManager gManager;


	public boolean thinkRequest;


	public boolean thinking;


	public int thinkDelay;


	public int thinkCurrentPieceNo;


	public int thinkLastPieceNo;


	public volatile boolean threadRunning;


	public Thread thread;
    
	private Ranks ranks;
	private boolean skipNextFrame;
	private int [] surface;
    private int currentHeightMin;
    private int currentHeightMax;
    private static int THRESHOLD_FORCE_4LINES=10;
    private static int MAX_PREVIEWS=2;
    
	public class Score{
		
		public int rankStacking;
		
		public Score(){
			
			rankStacking=0;
		
		}
		public String toString(){
			return " Rank Stacking : "+rankStacking;

		}
		public int compareTo(Object o) {
			Score otherScore=(Score) o;

			
			
		
				if (this.rankStacking != otherScore.rankStacking){
					return this.rankStacking>otherScore.rankStacking?1:-1;
				}
											

							
						
					
			
              
				
         return 0;
		}
	}





	@Override
	public String getName() {
		return "RANKS";
	}


	@Override
	public void init(GameEngine engine, int playerID) {
		delay = 0;
		gEngine = engine;
		gManager = engine.owner;
		thinkRequest = false;
		thinking = false;
		threadRunning = false;

		if( ((thread == null) || !thread.isAlive()) && (engine.aiUseThread) ) {
			thread = new Thread(this, "AI_" + playerID);
			thread.setDaemon(true);
			thread.start();
			thinkDelay = engine.aiThinkDelay;
			thinkCurrentPieceNo = 0;
			thinkLastPieceNo = 0;
		}
		if (ranks==null){
		String inputFile="ranks.bin";
		FileInputStream fis = null;
		ObjectInputStream in = null;
		if (inputFile.trim().length() == 0)
			ranks=new Ranks(4,9);
		else {
			  try {
				fis = new FileInputStream(inputFile);
				   in = new ObjectInputStream(fis);
				   ranks = (Ranks)in.readObject();
				   in.close();

			} catch (FileNotFoundException e) {
				ranks=new Ranks(4,9);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		}
		surface=new int [ranks.getStackWidth()-1];
		
	}


	@Override
	public void shutdown(GameEngine engine, int playerID) {
		ranks=null;
		if((thread != null) && (thread.isAlive())) {
			thread.interrupt();
			threadRunning = false;
			thread = null;
		}
	}


	@Override
	public void newPiece(GameEngine engine, int playerID) {
		if(!engine.aiUseThread) {
			thinkBestPosition(engine, playerID);
		} else {
			thinkRequest = true;
			thinkCurrentPieceNo++;
		}
	}


	@Override
	public void onFirst(GameEngine engine, int playerID) {
	}


	@Override
	public void onLast(GameEngine engine, int playerID) {
	}


	@Override
	public void setControl(GameEngine engine, int playerID, Controller ctrl) {
		if( (engine.nowPieceObject != null) && (engine.stat == GameEngine.STAT_MOVE) && (delay >= engine.aiMoveDelay) && (engine.statc[0] > 0) &&
		    (!engine.aiUseThread || (threadRunning && !thinking && (thinkCurrentPieceNo <= thinkLastPieceNo))) )
		{
			int input = 0;
			Piece pieceNow = engine.nowPieceObject;
			int nowX = engine.nowPieceX;
			int nowY = engine.nowPieceY;
			int rt = pieceNow.direction;
			Field fld = engine.field;
			boolean pieceTouchGround = pieceNow.checkCollision(nowX, nowY + 1, fld);

			if((bestHold || forceHold) && engine.isHoldOK()) {

				input |= Controller.BUTTON_BIT_D;
			} else {

				if(rt != bestRt) {
					int lrot = engine.getRotateDirection(-1);
					int rrot = engine.getRotateDirection(1);

					if((Math.abs(rt - bestRt) == 2) && (engine.ruleopt.rotateButtonAllowDouble) && !ctrl.isPress(Controller.BUTTON_E)) {
						input |= Controller.BUTTON_BIT_E;
					} else if(!ctrl.isPress(Controller.BUTTON_B) && engine.ruleopt.rotateButtonAllowReverse &&
							  !engine.isRotateButtonDefaultRight() && (bestRt == rrot)) {
						input |= Controller.BUTTON_BIT_B;
					} else if(!ctrl.isPress(Controller.BUTTON_B) && engine.ruleopt.rotateButtonAllowReverse &&
							  engine.isRotateButtonDefaultRight() && (bestRt == lrot)) {
						input |= Controller.BUTTON_BIT_B;
					} else if(!ctrl.isPress(Controller.BUTTON_A)) {
						input |= Controller.BUTTON_BIT_A;
					}
				}


				int minX = pieceNow.getMostMovableLeft(nowX, nowY, rt, fld);
				int maxX = pieceNow.getMostMovableRight(nowX, nowY, rt, fld);
				if (!skipNextFrame){
					skipNextFrame=true;
				
					if( ((bestX < minX - 1) || (bestX > maxX + 1) || (bestY < nowY)) && (rt == bestRt)  ){

						thinkRequest = true;
						
						} else {

							if((nowX == bestX) && (pieceTouchGround) && (rt == bestRt)) {

								if(bestRtSub != -1) {
									bestRt = bestRtSub;
									bestRtSub = -1;
								}

								if(bestX != bestXSub) {
									bestX = bestXSub;
									bestY = bestYSub;
								}
							}

							if(nowX > bestX) {

								if(!ctrl.isPress(Controller.BUTTON_LEFT) || (engine.aiMoveDelay >= 0))
									input |= Controller.BUTTON_BIT_LEFT;
							} else if(nowX < bestX) {

								if(!ctrl.isPress(Controller.BUTTON_RIGHT) || (engine.aiMoveDelay >= 0))
									input |= Controller.BUTTON_BIT_RIGHT;
							} else if((nowX == bestX) && (rt == bestRt)) {

								if((bestRtSub == -1) && (bestX == bestXSub)) {
									if(engine.ruleopt.harddropEnable && !ctrl.isPress(Controller.BUTTON_UP))
										input |= Controller.BUTTON_BIT_UP;
									else if(engine.ruleopt.softdropEnable || engine.ruleopt.softdropLock)
										input |= Controller.BUTTON_BIT_DOWN;
								} else {
									if(engine.ruleopt.harddropEnable && !engine.ruleopt.harddropLock && !ctrl.isPress(Controller.BUTTON_UP))
										input |= Controller.BUTTON_BIT_UP;
									else if(engine.ruleopt.softdropEnable && !engine.ruleopt.softdropLock)
										input |= Controller.BUTTON_BIT_DOWN;
								}
							}
						}
				}
				else {
					skipNextFrame=false;
				}
			}

			delay = 0;
			ctrl.setButtonBit(input);
		} else {
			delay++;
			ctrl.setButtonBit(0);
		}
	}

	/**
	 * Think the best position
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	public void thinkBestPosition(GameEngine engine, int playerID) {
		bestHold = false;
		bestX = 0;
		bestY = 0;
		bestRt = 0;
		bestXSub = 0;
		bestYSub = 0;
		bestRtSub = -1;
		bestPts = 0;
		forceHold = false;
	    Score bestScore=new Score();
	    Score score;
		Piece pieceNow = engine.nowPieceObject;
		
		int nowY = engine.nowPieceY;

		Piece pieceHold = engine.holdPieceObject;
		int numPreviews=MAX_PREVIEWS;
		
		currentHeightMin=engine.field.getHeight();
		currentHeightMax=0;
	    int heights[]=new int [ranks.getStackWidth()];
	        for (int i=0;i<ranks.getStackWidth();i++){
	                 heights[i]=engine.field.getHeight()-engine.field.getHighestBlockY(i);
	                 if (heights[i]<currentHeightMin){
	                	 currentHeightMin=heights[i];
	                 }
	                 if (heights[i]>currentHeightMax){
	                	 currentHeightMax=heights[i];
	                 }
	         }
	    int maxJump=ranks.getMaxJump();
		for (int i=0;i<ranks.getStackWidth()-1;i++){
            int diff=heights[i+1]-heights[i];
            if (diff>maxJump){
                    
                           ;
                     diff=maxJump;
            }
            if (diff<-maxJump){
                    
                    diff=-maxJump;
            }
            surface[i]=diff;
    }


		Field fld = new Field(engine.field);

			for(int rt = 0; rt < Ranks.PIECES_NUM_ROTATIONS[pieceNow.id]; rt++) {
				boolean isVerticalIPiece=(pieceNow.id==Piece.PIECE_I && ((rt==1)||(rt==3)));
				int minX=0-pieceNow.dataOffsetX[rt]-Ranks.PIECES_LEFTMOSTS[pieceNow.id][rt];
				int maxX=minX+ranks.getStackWidth()-Ranks.PIECES_WIDTHS[pieceNow.id][rt];
				if (!isVerticalIPiece || (currentHeightMax<THRESHOLD_FORCE_4LINES) || (currentHeightMin<4) ){
				for(int x = minX; x <= maxX; x++) {
					//fld.copy(engine.field);
					int y = pieceNow.getBottom(x, nowY, rt, fld);

					//if(!pieceNow.checkCollision(x, y, rt, fld)) {

						score = thinkMain(engine, x,  rt,  surface, pieceNow, pieceHold, numPreviews,false,false);
						log.debug("MAIN  id="+pieceNow.id+" posX="+x+" rt="+rt+" score:"+score);
						if(score.compareTo(bestScore)>0) {
							//log.debug("MAIN new best piece !");
							log.debug("MAIN  id="+pieceNow.id+" posX="+x+" rt="+rt+" score:"+score);
							
							bestHold = false;
							bestX = x;
							bestY = y;
							bestRt = rt;
							bestXSub = x;
							bestYSub = y;
							bestRtSub = -1;
							bestScore=score;
						}
						


				}
				}
				
				if (pieceNow.id==Piece.PIECE_I && ((rt==1)||(rt==3)) && currentHeightMin>=4){
					int y = pieceNow.getBottom(maxX+1, nowY, rt, fld);
					score = thinkMain(engine, maxX+1,  rt,  surface, pieceNow, pieceHold, numPreviews,false,false);
					log.debug("MAIN (4 Lines) id="+pieceNow.id+" posX="+(maxX+1)+" rt="+rt+" score:"+score);
					if(score.compareTo(bestScore)>0) {
						log.debug("MAIN (4 Lines) new best piece !");
						
						bestHold = false;
						bestX = maxX+1;
						bestY = y;
						bestRt = rt;
						bestXSub = maxX+1;
						bestYSub = y;
						bestRtSub = -1;
						bestScore=score;
					}
				}


		  }

			//ranks.surfaceAddPossible(surface, pieceNow.id, bestRt, bestX+Ranks.PIECES_LEFTMOSTS[pieceNow.id][bestRt]+pieceNow.dataOffsetX[bestRt]);
		if (bestScore.rankStacking==0)
			threadRunning=false;
		thinkLastPieceNo++;

		log.debug("X:" + bestX + " Y:" + bestY + " R:" + bestRt + " H:" + bestHold + " Pts:" + bestScore);
	}


	
	/**
	 * Main think routine
	 * @param engine GameEngine
	 * @param x X coord
	 * @param y Y coord
	 * @param rt Piece direction (after)
	 * @param rtOld Piece direction (before)
	 * @param fld Field
	 * @param piece Current piece object
	 * @param nextpiece NEXT piece object
	 * @param holdpiece HOLD piece object (can be null)
	 * @return Score object for this placement
	 */
	public Score thinkMain(GameEngine engine, int x,  int rt,  int[] surface, Piece piece,  Piece holdpiece, int numPreviews, boolean isCliff, boolean isFourLines) {
		Score score=new Score();
		boolean fourLines=isFourLines;
		boolean cliffCreated=isCliff;
		log.debug("piece id : " +piece.id+" rot : "+rt+" x :"+x+" surface :"+Arrays.toString(surface));
		boolean isVerticalI=(piece.id==Piece.PIECE_I && ((rt==1)||(rt==3))&&(x+Ranks.PIECES_LEFTMOSTS[piece.id][rt]+piece.dataOffsetX[rt])==9);
		if (isVerticalI)
			fourLines=true;
		if (isVerticalI || ranks.surfaceFitsPiece(surface, piece.id, rt, x+Ranks.PIECES_LEFTMOSTS[piece.id][rt]+piece.dataOffsetX[rt])){
			log.debug("PASSED ! 4 lines? :"+ isVerticalI);
			int [] surfaceWork =surface.clone();
			if (!isVerticalI){
				if (ranks.surfaceAddPossible(surfaceWork,piece.id,rt,x+Ranks.PIECES_LEFTMOSTS[piece.id][rt]+piece.dataOffsetX[rt])){
				
				
				}
				else {
					cliffCreated =true;
				
				}
			}
			if (numPreviews>0){
				Score bestScore=new Score();
			    Score scoreCurrent;
				Piece pieceNow = engine.getNextObject(engine.nextPieceCount+MAX_PREVIEWS-numPreviews);		
				Piece pieceHold = engine.holdPieceObject;
				
				//if(pieceHold == null) {
				//	holdEmpty = true;
				//}
				//Field fldcopy = new Field(fld);
                //int [] surfaceWork2=surfaceWork.clone();

					for(int rt2 = 0; rt2 < Ranks.PIECES_NUM_ROTATIONS[pieceNow.id]; rt2++) {
						boolean isVerticalI2=(pieceNow.id==Piece.PIECE_I && ((rt2==1)||(rt2==3)));
						int minX2=0-pieceNow.dataOffsetX[rt2]-Ranks.PIECES_LEFTMOSTS[pieceNow.id][rt2];
						int maxX2=minX2+ranks.getStackWidth()-Ranks.PIECES_WIDTHS[pieceNow.id][rt2];
						
						if (!isVerticalI2 || (currentHeightMax <THRESHOLD_FORCE_4LINES) || (currentHeightMin<4)){
						for(int x2 = minX2; x2 <= maxX2; x2++) {
							//fldcopy.copy(fld);
							//int y2 = pieceNow.getBottom(x2, nowY, rot, fldcopy);

							//if(!pieceNow.checkCollision(x, y, rt, fld)) {

								scoreCurrent = thinkMain(engine, x2,  rt2, surfaceWork,pieceNow,  pieceHold,numPreviews-1,cliffCreated,fourLines);
								log.debug("SUB "+numPreviews +" id="+pieceNow.id+" posX="+x2+" rt="+rt2+" score "+scoreCurrent);

								if(scoreCurrent.compareTo(bestScore)>0) {
									log.debug("SUB new best piece !");
										bestScore=scoreCurrent;
								}


						}
						}
						
						if (pieceNow.id==Piece.PIECE_I && ((rt2==1)||(rt2==3)) && currentHeightMin>=4){
							
							scoreCurrent = thinkMain(engine, maxX2+1,  rt2,  surfaceWork, pieceNow, pieceHold, numPreviews-1,cliffCreated,true);
							log.debug("SUB (4 Lines)"+ numPreviews+" id="+pieceNow.id+" posX="+(maxX2+1)+" rt="+rt2+" score:"+scoreCurrent);
							if(scoreCurrent.compareTo(bestScore)>0) {
								log.debug("SUB new best piece !");
							;
								bestScore=scoreCurrent;
							}
						}

					}
					return bestScore;
			}
			else {
				int scorenum=ranks.getRankValue(ranks.encode(surfaceWork));
			
				score.rankStacking=scorenum;
				
				return score;
			}
		}	
		else 
		{
			return score;
		}

		
		
	}

	/**
	 * Get max think level
	 * @return Max think level (1 in this AI)
	 */
	public int getMaxThinkDepth() {
		return 1;
	}

	/*
	 * Thread routine for this AI
	 */
	public void run() {
		log.info("RanksAI: Thread start");
		threadRunning = true;

		while(threadRunning) {
			if(thinkRequest) {
				thinkRequest = false;
				thinking = true;
				try {
					thinkBestPosition(gEngine, gEngine.playerID);
					
				} catch (Throwable e) {
					log.debug("RanksAI: thinkBestPosition Failed", e);
				}
				thinking = false;
				skipNextFrame=false;
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
		ranks=null;
		log.info("RanksAI: Thread end");
	}
}
