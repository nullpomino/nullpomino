package cx.it.nullpo.nm8.game.play;

import java.io.Serializable;
import java.util.Random;

import cx.it.nullpo.nm8.game.component.Block;
import cx.it.nullpo.nm8.game.component.Controller;
import cx.it.nullpo.nm8.game.component.Field;
import cx.it.nullpo.nm8.game.component.Piece;
import cx.it.nullpo.nm8.game.component.RuleOptions;
import cx.it.nullpo.nm8.game.component.SpeedParam;
import cx.it.nullpo.nm8.game.component.Statistics;
import cx.it.nullpo.nm8.game.component.WallkickResult;
import cx.it.nullpo.nm8.game.subsystem.randomizer.Randomizer;
import cx.it.nullpo.nm8.game.subsystem.randomizer.RandomizerFactory;
import cx.it.nullpo.nm8.game.subsystem.wallkick.Wallkick;
import cx.it.nullpo.nm8.game.subsystem.wallkick.WallkickFactory;

/**
 * GamePlay: Base game play class
 */
public class GamePlay implements Serializable {
	/** Serial version ID */
	private static final long serialVersionUID = 8905858603717680945L;

	/** Constants of main game status */
	public static final int STAT_NOTHING = -1,
							STAT_SETTING = 0,
							STAT_READY = 1,
							STAT_MOVE = 2,
							STAT_LOCKFLASH = 3,
							STAT_LINECLEAR = 4,
							STAT_ARE = 5,
							STAT_INTERRUPTITEM = 6,
							STAT_ENDINGSTART = 7,
							STAT_EXCELLENT = 8,
							STAT_GAMEOVER = 9;

	/** Constants of last successful movements */
	public static final int LASTMOVE_NONE = 0,
							LASTMOVE_FALL_AUTO = 1,
							LASTMOVE_FALL_SELF = 2,
							LASTMOVE_SLIDE_AIR = 3,
							LASTMOVE_SLIDE_GROUND = 4,
							LASTMOVE_ROTATE_AIR = 5,
							LASTMOVE_ROTATE_GROUND = 6;

	/** GameEngine: Owner of this GamePlay */
	public GameEngine engine;

	/** Player ID */
	public int playerID;

	/** RuleOptions: Most game settings are here */
	public RuleOptions ruleopt;

	/** Controller: The player's input */
	public Controller ctrl;

	/** Statistics: Various game statistics such as score, number of lines, etc */
	public Statistics statistics;

	/** SpeedParam: Parameters of game speed (Gravity, ARE, Line clear delay, etc) */
	public SpeedParam speed;

	/** The first random-seed */
	public long randSeed;

	/** Random: Used for creating various randomness */
	public Random random;

	/** Wallkick: Wallkick system */
	public Wallkick wallkick;

	/** Randomizer: Create next piece sequences */
	public Randomizer randomizer;

	/** Array of next piece objects */
	public Piece[] nextPieceArray;

	/** Current game status */
	public int stat;

	/** Ready->Go timer */
	public long readyTimer;

	/** Current piece object */
	public Piece nowPieceObject;

	/** Current piece X position */
	public int nowPieceX;

	/** Current piece Y position */
	public int nowPieceY;

	/** Hover time */
	public long hoverTime;

	/** Hover time added by softdrop */
	public long hoverSoftDrop;

	/** Current lock delay time */
	public long lockDelayNow;

	/** Instant lock flag */
	public boolean instantLock;

	/** Last successful movement */
	public int lastmove;

	/** Hold piece object */
	public Piece holdPieceObject;

	/** Hold used flag */
	public boolean holdUsed;

	/** Current lock flash time */
	public long lockFlashNow;

	/** Current line clear delay time */
	public long lineDelayNow;

	/** Current ARE time */
	public long areNow;

	/** Duration of ARE */
	public long areMax;

	/**
	 * Constructor
	 */
	public GamePlay() {
	}

	/**
	 * Constructor
	 * @param engine GameEngine
	 * @param playerID Player ID
	 */
	public GamePlay(GameEngine engine, int playerID) {
		this.engine = engine;
		this.playerID = playerID;
	}

	/**
	 * Init
	 */
	public void init() {
		ctrl = new Controller();
		statistics = new Statistics();
		speed = new SpeedParam(engine.owner.isFrameBasedTimer());
		Random tempRand = new Random();
		randSeed = tempRand.nextLong();
		random = new Random(randSeed);
		stat = STAT_SETTING;
		readyTimer = 0;
		nowPieceObject = null;
		nowPieceX = 0;
		nowPieceY = 0;
		hoverTime = 0;
		hoverSoftDrop = 0;
		lockDelayNow = 0;
		instantLock = false;
		lastmove = LASTMOVE_NONE;
		holdPieceObject = null;
		holdUsed = false;
		lockFlashNow = 0;
		lineDelayNow = 0;
		areNow = 0;
		areMax = 0;

		engine.owner.gameMode.playerInit(this);
	}

	/**
	 * Set a new RuleOptions
	 * @param r RuleOptions
	 */
	public void setRuleOptions(RuleOptions r) {
		this.ruleopt = r;

		// Load an wallkick
		wallkick = WallkickFactory.createWallkick(ruleopt.wallkickID);

		// Load a randomizer
		randomizer = RandomizerFactory.createRandomizer(ruleopt.randomizerID, null, randSeed);
		randomizer.init();

		// Fill the next piece queue
		nextPieceArray = new Piece[ruleopt.nextDisplay];
		for(int i = 0; i < nextPieceArray.length; i++) {
			int id = randomizer.next();
			nextPieceArray[i] = createPieceObject(id);
		}
	}

	/**
	 * Start the game
	 */
	public void start() {
		if(ruleopt == null) {
			setRuleOptions(new RuleOptions());
		}
		stat = STAT_READY;
	}

	/**
	 * Update game
	 * @param runMsec Milliseconds elapsed from the last execution, or 1 if using frame-based timer
	 */
	public void update(long runMsec) {
		long rMsec = engine.owner.isFrameBasedTimer() ? 1 : runMsec;

		while(rMsec > 0) {
			long msec = rMsec;
			rMsec = 0;

			// Update timer
			if(engine.timerActive) {
				statistics.time += msec;
			}

			// Execute main logics
			switch(stat) {
			case STAT_SETTING:
				break;
			case STAT_READY:
				rMsec = onReady(msec);
				break;
			case STAT_MOVE:
				rMsec = onMove(msec);
				break;
			case STAT_LOCKFLASH:
				rMsec = onLockFlash(msec);
				break;
			case STAT_LINECLEAR:
				rMsec = onLineClear(msec);
				break;
			case STAT_ARE:
				rMsec = onARE(msec);
				break;
			}

			// Do not execute more than once in frame based timer
			if(engine.owner.isFrameBasedTimer()) rMsec = 0;
		}
	}

	/**
	 * Update controller status
	 * @param runMsec Milliseconds elapsed from the last execution, or 1 if using frame-based timer
	 */
	public void updateController(long runMsec) {
		// Set DAS & ARR
		ctrl.setDAS(Controller.BUTTON_LEFT, speed.das);
		ctrl.setDAS(Controller.BUTTON_RIGHT, speed.das);
		ctrl.setARR(Controller.BUTTON_LEFT, speed.arr);
		ctrl.setARR(Controller.BUTTON_RIGHT, speed.arr);

		// Set softdrop speed
		ctrl.setDAS(Controller.BUTTON_SOFT, 1);
		ctrl.setARR(Controller.BUTTON_SOFT, 1);

		// Update controller status
		ctrl.update(runMsec, engine.replayTimer);
	}

	/**
	 * Create a completely new Piece object
	 * @param id Piece ID
	 * @return A new Piece object
	 */
	public Piece createPieceObject(int id) {
		Piece piece = new Piece(id);
		piece.setColor(ruleopt.pieceColor[id]);
		piece.setAttribute(Block.BLOCK_ATTRIBUTE_VISIBLE, true);
		return piece;
	}

	/**
	 * Get piece spawn X-coordinate
	 * @param piece Piece
	 * @return Piece spawn X-coordinate
	 */
	public int getSpawnPosX(Piece piece) {
		return getSpawnPosX(piece, engine.field);
	}

	/**
	 * Get piece spawn X-coordinate
	 * @param piece Piece
	 * @param fld Field
	 * @return Piece spawn X-coordinate
	 */
	public int getSpawnPosX(Piece piece, Field fld) {
		int x = -1 + (fld.getWidth() - piece.getWidth() + 1) / 2;

		//if((big == true) && (bigmove == true) && (x % 2 != 0))
		//	x++;

		//if(big == true) {
		//	x += ruleopt.pieceSpawnXBig[piece.id][piece.direction];
		//} else {
			x += ruleopt.pieceSpawnX[piece.id][piece.direction];
		//}

		return x;
	}

	/**
	 * Get piece spawn Y-coordinate
	 * @param fld Field
	 * @param piece Piece
	 * @return Piece spawn Y-coordinate
	 */
	public int getSpawnPosY(Piece piece) {
		int y = 0;

		if((ruleopt.pieceEnterAboveField == true) && (ruleopt.fieldCeiling == false)) {
			y = -1 - piece.getMaximumBlockY();
			//if(big == true) y--;
		} else {
			y = -piece.getMinimumBlockY();
		}

		//if(big == true) {
		//	y += ruleopt.pieceSpawnYBig[piece.id][piece.direction];
		//} else {
			y += ruleopt.pieceSpawnY[piece.id][piece.direction];
		//}

		return y;
	}

	/**
	 * Make an new piece appear
	 */
	public void newPiece() {
		if(nowPieceObject == null) {
			// Pop from next piece queue
			if((nextPieceArray != null) && (nextPieceArray.length > 0)) {
				nowPieceObject = nextPieceArray[0];
				for(int i = 0; i < nextPieceArray.length - 1; i++) {
					nextPieceArray[i] = nextPieceArray[i + 1];
				}
				int id = randomizer.next();
				nextPieceArray[nextPieceArray.length - 1] = createPieceObject(id);
			}
			// If piece preview is disabled, create a new Piece directly
			else {
				int id = randomizer.next();
				nowPieceObject = createPieceObject(id);
			}
		}
		nowPieceX = getSpawnPosX(nowPieceObject);
		nowPieceY = getSpawnPosY(nowPieceObject);
		hoverTime = 0;
		hoverSoftDrop = 0;
		lockDelayNow = 0;
		instantLock = false;
		lastmove = LASTMOVE_NONE;
		lockFlashNow = 0;
		lineDelayNow = 0;
		areNow = 0;

		if(nowPieceObject.checkCollision(nowPieceX, nowPieceY, engine.field)) {
			// Signal GameOver
			engine.field.reset();
		}
	}

	/**
	 * Move the current piece to left or right
	 * @param m Move direction (-1:Left, 1:Right)
	 */
	public void movePiece(int m) {
		if(nowPieceObject != null) {
			boolean isGround = nowPieceObject.checkCollision(nowPieceX, nowPieceY + 1, engine.field);

			if(!nowPieceObject.checkCollision(nowPieceX + m, nowPieceY, engine.field)) {
				nowPieceX += m;
				lockDelayNow = 0;

				if(isGround) {
					lastmove = LASTMOVE_SLIDE_GROUND;
				} else {
					lastmove = LASTMOVE_SLIDE_AIR;
				}

				statistics.totalPieceMove++;
			}
		}
	}

	/**
	 * Softdrop
	 */
	public void softdropPiece() {
		if(nowPieceObject != null) {
			//hoverTime += speed.denominator / 2;
			hoverSoftDrop += speed.gravity * 2;
		}
	}

	/**
	 * Harddrop
	 */
	public void harddropPiece() {
		if(nowPieceObject != null) {
			int bottomY = nowPieceObject.getBottom(nowPieceX, nowPieceY, engine.field);

			if(bottomY != nowPieceY) {
				// Do stuff here
				nowPieceY = bottomY;
				lastmove = LASTMOVE_FALL_SELF;
			}

			instantLock = true;
		}
	}

	/**
	 * Rotate the current piece
	 * @param d Rotation direction (-1:Left, 1:Right, 2:180)
	 */
	public void rotatePiece(int d) {
		if(nowPieceObject != null) {
			boolean isGround = nowPieceObject.checkCollision(nowPieceX, nowPieceY + 1, engine.field);
			int rtNew = nowPieceObject.getRotateDirection(d);
			boolean success = false;

			if(!nowPieceObject.checkCollision(nowPieceX, nowPieceY, rtNew, engine.field)) {
				// Rotation successful without an wallkick
				nowPieceObject.direction = rtNew;
				success = true;
			} else {
				// Try a wallkick
				WallkickResult r =
					wallkick.executeWallkick(nowPieceX, nowPieceY, d, nowPieceObject.direction, rtNew, true, nowPieceObject, engine.field, ctrl);

				if(r != null) {
					nowPieceObject.direction = r.direction;
					nowPieceX += r.offsetX;
					nowPieceY += r.offsetY;
					success = true;
				}
			}

			if(success) {
				lockDelayNow = 0;

				if(isGround) {
					lastmove = LASTMOVE_ROTATE_GROUND;
				} else {
					lastmove = LASTMOVE_ROTATE_AIR;
				}

				statistics.totalPieceRotate++;
			}
		}
	}

	/**
	 * Hold
	 */
	public void holdPiece() {
		if((nowPieceObject != null) && (!holdUsed) && (ruleopt.holdEnable)) {
			if(holdPieceObject == null) {
				holdPieceObject = nowPieceObject;
				nowPieceObject = null;
			} else {
				Piece pieceTemp = nowPieceObject;
				nowPieceObject = holdPieceObject;
				holdPieceObject = pieceTemp;
			}

			if(ruleopt.holdResetDirection) holdPieceObject.direction = 0;

			statistics.totalHoldUsed++;
			holdUsed = true;
			newPiece();
		}
	}

	public long onReady(long runMsec) {
		updateController(runMsec);

		readyTimer += runMsec;

		if(readyTimer >= engine.goEnd) {
			engine.timerActive = true;
			engine.gameStarted = true;
			stat = STAT_MOVE;
			return readyTimer - engine.goEnd;
		}

		return 0;
	}

	public long onMove(long runMsec) {
		updateController(runMsec);

		long gMsec = runMsec;	// Miliseconds remaining after gravity
		long extraMsec = 0;	// Extra miliseconds that was not used here

		if(nowPieceObject == null) {
			newPiece();
		}
		if(nowPieceObject != null) {
			statistics.totalPieceActiveTime += runMsec;

			// Hold
			if(ctrl.isButtonActivated(Controller.BUTTON_HOLD)) holdPiece();

			// Rotation Button
			int rt = 0;
			if(ctrl.isButtonActivated(Controller.BUTTON_LROTATE)) rt = -1;
			else if(ctrl.isButtonActivated(Controller.BUTTON_RROTATE)) rt = 1;
			else if(ctrl.isButtonActivated(Controller.BUTTON_DROTATE)) rt = 2;
			if(rt != 0) rotatePiece(rt);

			// Left/Right movement
			int move = 0;
			if(ctrl.isButtonPressed(Controller.BUTTON_LEFT) && ctrl.isButtonPressed(Controller.BUTTON_RIGHT)) {
				if(ctrl.buttonLastPushedTime[Controller.BUTTON_LEFT] > ctrl.buttonLastPushedTime[Controller.BUTTON_RIGHT]) {
					if(ctrl.isButtonActivated(Controller.BUTTON_LEFT)) {
						move = -1;
					}
				} else if(ctrl.buttonLastPushedTime[Controller.BUTTON_LEFT] < ctrl.buttonLastPushedTime[Controller.BUTTON_RIGHT]) {
					if(ctrl.isButtonActivated(Controller.BUTTON_RIGHT)) {
						move = 1;
					}
				}
			} else if(ctrl.isButtonActivated(Controller.BUTTON_LEFT)) {
				move = -1;
			} else if(ctrl.isButtonActivated(Controller.BUTTON_RIGHT)) {
				move = 1;
			}
			if(move != 0) movePiece(move);

			// Soft drop
			if(ctrl.isButtonActivated(Controller.BUTTON_SOFT)) softdropPiece();

			// Hard drop
			if(ctrl.isButtonActivated(Controller.BUTTON_HARD)) harddropPiece();

			if(!nowPieceObject.checkCollision(nowPieceX, nowPieceY + 1, engine.field)) {
				if(speed.gravity == 0) {
					// 0G
				} else if(speed.gravity < 0) {
					// 20G
					while(!nowPieceObject.checkCollision(nowPieceX, nowPieceY + 1, engine.field)) {
						nowPieceY++;
						lockDelayNow = 0;
						lastmove = LASTMOVE_FALL_AUTO;
					}
				} else {
					// Gravity
					while(hoverSoftDrop >= speed.gravity) {
						hoverSoftDrop -= speed.gravity;

						if(!nowPieceObject.checkCollision(nowPieceX, nowPieceY + 1, engine.field)) {
							nowPieceY++;
							lockDelayNow = 0;
							lastmove = LASTMOVE_FALL_SELF;
						} else {
							//gMsec = 0;
							hoverSoftDrop = 0;
							break;
						}
					}

					hoverTime += runMsec;

					while(hoverTime >= speed.gravity) {
						hoverTime -= speed.gravity;

						if(!nowPieceObject.checkCollision(nowPieceX, nowPieceY + 1, engine.field)) {
							nowPieceY++;
							lockDelayNow = 0;
							lastmove = LASTMOVE_FALL_AUTO;
						} else {
							gMsec = hoverTime;
							break;
						}
					}
				}
			}

			// Lock delay
			if(nowPieceObject.checkCollision(nowPieceX, nowPieceY + 1, engine.field)) {
				lockDelayNow += gMsec;

				if((lockDelayNow >= speed.lockDelay) || (instantLock)) {
					if(!instantLock) extraMsec = lockDelayNow - speed.lockDelay;

					boolean placed = nowPieceObject.placeToField(nowPieceX, nowPieceY, engine.field);
					nowPieceObject = null;
					holdUsed = false;

					if(!placed) {
						// Signal GameOver
						engine.field.reset();
					} else if(speed.lockFlash > 0) {
						stat = STAT_LOCKFLASH;
					} else {
						int lineClears = engine.field.checkLine();

						if(lineClears > 0) {
							stat = STAT_LINECLEAR;
						} else if(speed.are > 0) {
							areMax = speed.are;
							stat = STAT_ARE;
						}
					}
				}
			}
		}

		return extraMsec;
	}

	public long onLockFlash(long runMsec) {
		updateController(runMsec);

		lockFlashNow += runMsec;

		if(lockFlashNow >= speed.lockFlash) {
			int lineClears = engine.field.checkLine();

			if(lineClears > 0) {
				stat = STAT_LINECLEAR;
			} else if(speed.are > 0) {
				areMax = speed.are;
				stat = STAT_ARE;
			} else {
				stat = STAT_MOVE;
			}

			return lockFlashNow - speed.lockFlash;
		}

		return 0;
	}

	public long onLineClear(long runMsec) {
		updateController(runMsec);

		long prevDelay = lineDelayNow;
		lineDelayNow += runMsec;

		if(prevDelay == 0) engine.field.clearLine();

		if(lineDelayNow >= speed.lineDelay) {
			engine.field.downFloatingBlocks();
			if(speed.areLine > 0) {
				areMax = speed.areLine;
				stat = STAT_ARE;
			} else {
				stat = STAT_MOVE;
			}
			return lineDelayNow - speed.lineDelay;
		}

		return 0;
	}

	public long onARE(long runMsec) {
		updateController(runMsec);

		areNow += runMsec;

		if(areNow >= areMax) {
			stat = STAT_MOVE;
			return areNow - areMax;
		}

		return 0;
	}
}
