package cx.it.nullpo.nm8.game.play;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import cx.it.nullpo.nm8.game.component.Block;
import cx.it.nullpo.nm8.game.component.Controller;
import cx.it.nullpo.nm8.game.component.Field;
import cx.it.nullpo.nm8.game.component.NRandom;
import cx.it.nullpo.nm8.game.component.Piece;
import cx.it.nullpo.nm8.game.component.PieceManager;
import cx.it.nullpo.nm8.game.component.RuleOptions;
import cx.it.nullpo.nm8.game.component.SpeedParam;
import cx.it.nullpo.nm8.game.component.Statistics;
import cx.it.nullpo.nm8.game.component.TuningOptions;
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

	/** TuningOptions: Player's preferences */
	public TuningOptions tuning;

	/** Controller: The player's input */
	public Controller ctrl;

	/** Statistics: Various game statistics such as score, number of lines, etc */
	public Statistics statistics;

	/** SpeedParam: Parameters of game speed (Gravity, ARE, Line clear delay, etc) */
	public SpeedParam speed;

	/** PieceManager: Piece factory */
	public PieceManager pieceManager;

	/** The first random-seed */
	public long randSeed;

	/** Random: Used for creating various randomness */
	public NRandom random;

	/** Wallkick: Wallkick system */
	public Wallkick wallkick;

	/** Randomizer: Create next piece sequences */
	public Randomizer randomizer;

	/** Array of next piece objects */
	public Piece[] nextPieceArray;

	/** Sound effects queue */
	public List<String> seQueue;

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

	/** Deepest Y position the current piece reached */
	public int nowPieceDeepestY;

	/** Move time */
	public long moveTime;

	/** Hover time */
	public long hoverTime;

	/** Hover time added by softdrop */
	public long hoverSoftDrop;

	/** Soft drop button time */
	public long softdropTime;

	/** Denominator of soft drop button time */
	public long softdropTimeDenominator;

	/** Soft Drop continuous use flag */
	public boolean softdropContinuousUse;

	/** Hard Drop continuous use flag */
	public boolean harddropContinuousUse;

	/** Current lock delay time */
	public long lockDelayNow;

	/** Instant lock flag */
	public boolean instantLock;

	/** Lock reset move count */
	public int lockresetMoveCount;

	/** Lock reset rotate count */
	public int lockresetRotateCount;

	/** Last successful movement */
	public int lastmove;

	/** Hold piece object */
	public Piece holdPieceObject;

	/** Hold used flag */
	public boolean holdUsed;

	/** IHS use flag */
	public boolean holdIHS;

	/** IRS direction (0=No IRS) */
	public int irsDirection;

	/** true if entered move state with zero delay */
	public boolean fromZeroDelay;

	/** Current lock flash time */
	public long lockFlashNow;

	/** Current line clear delay time */
	public long lineDelayNow;

	/** Current ARE time */
	public long areNow;

	/** Duration of ARE */
	public long areMax;

	/** Current DAS direction (-1:Left 0=None 1=Right) */
	public int dasDirection;

	/** Current Left DAS value */
	public long dasNowLeft;

	/** Current Right DAS value */
	public long dasNowRight;

	/** Current Soft Drop DAS value */
	public long dasNowDown;

	/** Current Left ARR value */
	public long arrNowLeft;

	/** Current Right ARR value */
	public long arrNowRight;

	/** Current Soft Drop ARR value */
	public long arrNowDown;

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
		speed = new SpeedParam();
		pieceManager = new PieceManager();
		Random tempRand = new Random();
		randSeed = tempRand.nextLong();
		random = new NRandom(randSeed);
		seQueue = Collections.synchronizedList(new ArrayList<String>());
		stat = STAT_SETTING;
		readyTimer = 0;
		nowPieceObject = null;
		nowPieceX = 0;
		nowPieceY = 0;
		nowPieceDeepestY = 0;
		moveTime = 0;
		hoverTime = 0;
		hoverSoftDrop = 0;
		softdropTime = 0;
		softdropTimeDenominator = 1;
		softdropContinuousUse = false;
		harddropContinuousUse = false;
		lockDelayNow = 0;
		instantLock = false;
		lockresetMoveCount = 0;
		lockresetRotateCount = 0;
		lastmove = LASTMOVE_NONE;
		holdPieceObject = null;
		holdUsed = false;
		holdIHS = false;
		irsDirection = 0;
		fromZeroDelay = false;
		lockFlashNow = 0;
		lineDelayNow = 0;
		areNow = 0;
		areMax = 0;
		dasDirection = 0;
		dasNowLeft = 0;
		dasNowRight = 0;
		dasNowDown = 0;
		arrNowLeft = 0;
		arrNowRight = 0;
		arrNowDown = 0;

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
	 * Set a new TuningOptions
	 * @param t TuningOptions
	 */
	public void setTuningOptions(TuningOptions t) {
		this.tuning = t;
	}

	/**
	 * Start the game
	 */
	public void start() {
		if(ruleopt == null) {
			setRuleOptions(new RuleOptions());
		}
		if(tuning == null) {
			setTuningOptions(new TuningOptions());
		}
		stat = STAT_READY;
	}

	/**
	 * Update game
	 */
	public void update() {
		if(engine.owner.gameMode.updateBefore(this)) return;

		// Execute main logics
		switch(stat) {
		case STAT_SETTING:
			break;
		case STAT_READY:
			onReady();
			break;
		case STAT_MOVE:
			onMove();
			break;
		case STAT_LOCKFLASH:
			onLockFlash();
			break;
		case STAT_LINECLEAR:
			onLineClear();
			break;
		case STAT_ARE:
			onARE();
			break;
		}

		// Update timer
		if(engine.timerActive) {
			statistics.time += 1;
		}

		engine.owner.gameMode.updateAfter(this);
	}

	/**
	 * Update controller status
	 */
	public void updateController() {
		ctrl.update(engine.replayTimer);
	}

	/**
	 * Returns true if both Left and Right buttons are pressed
	 */
	public boolean isBothLeftRightPressed() {
		return (ctrl.isButtonPressed(Controller.BUTTON_LEFT) && ctrl.isButtonPressed(Controller.BUTTON_RIGHT));
	}

	/**
	 * Get current movement direction
	 * @return -1:Left 0=None 1=Right
	 */
	public int getMoveDirection() {
		int move = 0;
		if(isBothLeftRightPressed()) {
			// Both
			if(ctrl.buttonLastActivatedTime[Controller.BUTTON_LEFT] > ctrl.buttonLastActivatedTime[Controller.BUTTON_RIGHT]) {
				move = -1;
			} else if(ctrl.buttonLastActivatedTime[Controller.BUTTON_LEFT] < ctrl.buttonLastActivatedTime[Controller.BUTTON_RIGHT]) {
				move = 1;
			}
		} else if(ctrl.isButtonPressed(Controller.BUTTON_LEFT)) {
			// Left
			move = -1;
		} else if(ctrl.isButtonPressed(Controller.BUTTON_RIGHT)) {
			// Right
			move = 1;
		}
		return move;
	}

	/**
	 * Update DAS values
	 */
	public void updateDAS() {
		// Left/Right movement
		int moveDirection = getMoveDirection();
		if(moveDirection == -1) {
			dasNowLeft++;
			if(!isBothLeftRightPressed()) dasNowRight = 0;
		} else if(moveDirection == 1) {
			if(!isBothLeftRightPressed()) dasNowLeft = 0;
			dasNowRight++;
		} else {
			dasNowLeft = 0;
			dasNowRight = 0;
		}
		dasDirection = moveDirection;

		// Soft Drop
		if(ctrl.isButtonPressed(Controller.BUTTON_SOFT)) {
			dasNowDown++;
		} else {
			dasNowDown = 0;
		}

		// Reset Soft/Hard Drop continuous use flag
		if(!ctrl.isButtonPressed(Controller.BUTTON_SOFT))
			softdropContinuousUse = false;
		if(!ctrl.isButtonPressed(Controller.BUTTON_HARD))
			harddropContinuousUse = false;
	}

	/**
	 * Create a completely new Piece object
	 * @param id Piece ID
	 * @return A new Piece object
	 */
	public Piece createPieceObject(int id) {
		Piece piece = pieceManager.newPiece(id);
		piece.setAttribute(Block.BLOCK_ATTRIBUTE_VISIBLE, true);

		if((id >= 0) && (id < ruleopt.pieceColor.length))
			piece.setColor(ruleopt.pieceColor[id]);

		return piece;
	}

	/**
	 * Play a sound effect
	 * @param name Sound effect name
	 */
	public void playSE(String name) {
		if(!seQueue.contains(name)) {
			seQueue.add(name);
		}
	}

	/**
	 * Get current DAS (It will take care of both mode's and user's settings)
	 * @return DAS
	 */
	public long getDAS() {
		long modeSetting = speed.das;
		long userSetting = tuning.das;

		if(speed.lockDAS || (userSetting == -1)) {
			return modeSetting;
		}
		return userSetting;
	}

	/**
	 * Get current ARR (It will take care of both mode's and user's settings)
	 * @return ARR
	 */
	public long getARR() {
		long modeSetting = speed.arr;
		long userSetting = tuning.arr;

		if(speed.lockARR || (userSetting == -1)) {
			return modeSetting;
		}
		return userSetting;
	}

	/**
	 * Get current Soft drop DAS (It will take care of both mode's and user's settings)
	 * @return Soft drop DAS
	 */
	public long getSoftdropDAS() {
		long modeSetting = speed.softdropDAS;
		long userSetting = tuning.softdropDAS;

		if(speed.lockSoftdropDAS || (userSetting == -1)) {
			return modeSetting;
		}
		return userSetting;
	}

	/**
	 * Get current Soft drop ARR (It will take care of both mode's and user's settings)
	 * @return Soft drop ARR
	 */
	public long getSoftdropARR() {
		long modeSetting = speed.softdropARR;
		long userSetting = tuning.softdropARR;

		if(speed.lockSoftdropARR || (userSetting == -1)) {
			return modeSetting;
		}
		return userSetting;
	}

	/**
	 * Get current Soft drop speed magnification (It will take care of both mode's and user's settings)
	 * @return Soft drop speed magnification
	 */
	public float getSoftdropSpeedMagnification() {
		float modeSetting = speed.softdropSpeedMagnification;
		float userSetting = tuning.softdropSpeedMagnification;

		if(speed.lockSoftdropSpeedMagnification || (userSetting == -1)) {
			return modeSetting;
		}
		return userSetting;
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

		if(piece.big == true) {
			x += ruleopt.pieceSpawnXBig[piece.id][piece.direction];
		} else {
			x += ruleopt.pieceSpawnX[piece.id][piece.direction];
		}

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
			if(piece.big == true) y--;
		} else {
			y = -piece.getMinimumBlockY();
		}

		if(piece.big == true) {
			y += ruleopt.pieceSpawnYBig[piece.id][piece.direction];
		} else {
			y += ruleopt.pieceSpawnY[piece.id][piece.direction];
		}

		return y;
	}

	/**
	 * @return true if lock delay reset limit has been exceeded
	 */
	public boolean isLockResetLimitExceeded() {
		if((ruleopt.lockresetLimitMove != -1) && (lockresetMoveCount >= ruleopt.lockresetLimitMove))
			return true;
		if((!ruleopt.lockresetLimitShareCount) &&
		   (ruleopt.lockresetLimitRotate != -1) && (lockresetRotateCount >= ruleopt.lockresetLimitRotate))
			return true;

		return false;
	}

	/**
	 * Set IRS flags
	 */
	public void activateIRS() {
		irsDirection = 0;
		if(ruleopt.rotateInitial && tuning.rotateInitial) {
			int rt = 0;
			if(ctrl.isButtonPressed(Controller.BUTTON_LROTATE)) rt = -1;
			else if(ctrl.isButtonPressed(Controller.BUTTON_RROTATE)) rt = 1;
			else if(ctrl.isButtonPressed(Controller.BUTTON_DROTATE)) rt = 2;
			irsDirection = rt;
		}
	}

	/**
	 * Set IHS flags
	 */
	public void activateIHS() {
		holdIHS = false;
		if(ruleopt.holdEnable && ruleopt.holdInitial && tuning.holdInitial && ctrl.isButtonPressed(Controller.BUTTON_HOLD)) {
			holdIHS = true;
		}
	}

	/**
	 * Pop a Piece from next piece queue
	 * @return Piece
	 */
	public Piece popNextPiece() {
		Piece resultPiece = null;

		// Pop from next piece queue
		if((nextPieceArray != null) && (nextPieceArray.length > 0)) {
			resultPiece = nextPieceArray[0];
			for(int i = 0; i < nextPieceArray.length - 1; i++) {
				nextPieceArray[i] = nextPieceArray[i + 1];
			}
			int id = randomizer.next();
			nextPieceArray[nextPieceArray.length - 1] = createPieceObject(id);
		}
		// If piece preview is disabled, create a new Piece directly
		else {
			int id = randomizer.next();
			resultPiece = createPieceObject(id);
		}

		return resultPiece;
	}

	/**
	 * Make an new piece appear
	 */
	public void newPiece() {
		if(nowPieceObject == null) {
			if(holdIHS) {
				// IHS
				holdPiece(true);
			} else {
				// Pop from next piece queue
				nowPieceObject = popNextPiece();
			}

			if(nextPieceArray.length > 1) {
				int pieceID = nextPieceArray[0].id;
				playSE("piece" + pieceID);
			}
		}
		nowPieceX = getSpawnPosX(nowPieceObject);
		nowPieceY = getSpawnPosY(nowPieceObject);
		nowPieceDeepestY = nowPieceY;
		moveTime = 0;
		hoverTime = 0;
		hoverSoftDrop = 0;
		softdropTime = 0;
		lockDelayNow = 0;
		instantLock = false;
		lockresetMoveCount = 0;
		lockresetRotateCount = 0;
		lastmove = LASTMOVE_NONE;
		lockFlashNow = 0;
		lineDelayNow = 0;
		areNow = 0;

		// IRS
		if(irsDirection != 0) {
			rotatePiece(irsDirection, true);
		}

		// Game Over check
		if(nowPieceObject.checkCollision(nowPieceX, nowPieceY, engine.field)) {
			// Try push up
			for(int i = 0; i < ruleopt.pieceEnterMaxDistanceY; i++) {
				if(nowPieceObject.big) nowPieceY -= 2;
				else nowPieceY--;

				if(!nowPieceObject.checkCollision(nowPieceX, nowPieceY, engine.field)) {
					break;	// Success
				}
			}

			// Signal GameOver
			if(!engine.owner.gameMode.playerDeath(this, GameEngine.DEATH_BLOCKOUT)) {
				engine.signalGameOver(GameEngine.GAMEOVER_LOSE, GameEngine.DEATH_BLOCKOUT);
			}
		}
	}

	/**
	 * Move the current piece to left or right
	 * @param m Move direction (-1:Left, 1:Right)
	 * @return true if successful
	 */
	public boolean movePiece(int m) {
		if(nowPieceObject != null) {
			boolean isGround = nowPieceObject.checkCollision(nowPieceX, nowPieceY + 1, engine.field);

			if(!nowPieceObject.checkCollision(nowPieceX + m, nowPieceY, engine.field)) {
				nowPieceX += m;

				if(ruleopt.lockresetMove && !isLockResetLimitExceeded())
					lockDelayNow = 0;

				if(isGround) {
					lastmove = LASTMOVE_SLIDE_GROUND;
				} else {
					lastmove = LASTMOVE_SLIDE_AIR;
				}

				if(isGround || ruleopt.lockresetLimitCountAir)
					lockresetMoveCount++;

				statistics.totalPieceMove++;
				playSE("move");
				return true;
			}
		}

		return false;
	}

	/**
	 * Softdrop
	 */
	public void softdropPiece() {
		if(nowPieceObject != null) {
			if(speed.denominator > 0)
				hoverSoftDrop += (long)(speed.denominator * getSoftdropSpeedMagnification());
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
				playSE("harddrop");
				playSE("step");
			}

			instantLock = true;
		}
	}

	/**
	 * Rotate the current piece
	 * @param d Rotation direction (-1:Left, 1:Right, 2:180)
	 * @param irs true if IRS
	 * @return true if successful
	 */
	public boolean rotatePiece(int d, boolean irs) {
		if(isLockResetLimitExceeded() && (ruleopt.lockresetLimitOver == RuleOptions.LOCKRESET_LIMIT_OVER_NOROTATE))
			return false;

		if(nowPieceObject != null) {
			boolean isGround = nowPieceObject.checkCollision(nowPieceX, nowPieceY + 1, engine.field);
			int rtNew = nowPieceObject.getRotateDirection(d);
			boolean success = false;

			if(!nowPieceObject.checkCollision(nowPieceX, nowPieceY, rtNew, engine.field)) {
				// Rotation successful without an wallkick
				nowPieceObject.direction = rtNew;
				success = true;
			} else if(!isLockResetLimitExceeded() || (ruleopt.lockresetLimitOver != RuleOptions.LOCKRESET_LIMIT_OVER_NOWALLKICK)) {
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
				if(ruleopt.lockresetRotate && !isLockResetLimitExceeded())
					lockDelayNow = 0;

				if(isGround) {
					lastmove = LASTMOVE_ROTATE_GROUND;
				} else {
					lastmove = LASTMOVE_ROTATE_AIR;
				}

				if(isGround || ruleopt.lockresetLimitCountAir) {
					if(ruleopt.lockresetLimitShareCount)
						lockresetMoveCount++;
					else
						lockresetRotateCount++;
				}

				statistics.totalPieceRotate++;
				playSE(irs ? "initialrotate" : "rotate");
				return true;
			} else {
				playSE("rotfail");
			}
		}

		return false;
	}

	/**
	 * Hold
	 * @return true if successful
	 */
	public boolean holdPiece() {
		return holdPiece(false);
	}

	/**
	 * Hold
	 * @param isIHS true if IHS
	 * @return true if successful
	 */
	public boolean holdPiece(boolean isIHS) {
		if(!holdUsed && ruleopt.holdEnable) {
			statistics.totalHoldUsed++;
			holdUsed = true;

			if(isIHS) {
				// IHS
				if(holdPieceObject == null) {
					holdPieceObject = popNextPiece();
					nowPieceObject = popNextPiece();
				} else {
					nowPieceObject = holdPieceObject;
					holdPieceObject = popNextPiece();
				}
				playSE("initialhold");
			} else {
				// Normal Hold
				if(holdPieceObject == null) {
					holdPieceObject = nowPieceObject;
					nowPieceObject = null;
				} else {
					Piece pieceTemp = nowPieceObject;
					nowPieceObject = holdPieceObject;
					holdPieceObject = pieceTemp;
				}
				playSE("hold");
			}

			if(ruleopt.holdResetDirection) holdPieceObject.direction = 0;
			if(!isIHS) {
				activateIRS();
				newPiece();
			}

			return true;
		} else {
			playSE("holdfail");
		}

		return false;
	}

	/**
	 * Ready->Go state
	 */
	public void onReady() {
		if(engine.owner.gameMode.onReady(this)) return;

		updateDAS();
		updateController();

		readyTimer += 1;

		if(readyTimer >= engine.goEnd) {
			engine.timerActive = true;
			engine.gameStarted = true;
			stat = STAT_MOVE;
		}
	}

	/**
	 * Piece move state
	 */
	public void onMove() {
		if(engine.owner.gameMode.onMove(this)) return;

		updateDAS();
		updateController();

		if(nowPieceObject == null) {
			if(!fromZeroDelay || !ruleopt.holdInitialDisallowZeroDelay)
				activateIHS();
			if(!fromZeroDelay || !ruleopt.rotateInitialDisallowZeroDelay)
				activateIRS();

			newPiece();
		}

		if(nowPieceObject != null) {
			statistics.totalPieceActiveTime++;

			// Hold
			if(!holdIHS) {
				if(ctrl.isButtonActivated(Controller.BUTTON_HOLD)) holdPiece();
			} else {
				holdIHS = false;
			}

			// Rotation Button
			if(irsDirection == 0) {
				int rt = 0;
				if(ctrl.isButtonActivated(Controller.BUTTON_LROTATE)) rt = -1;
				else if(ctrl.isButtonActivated(Controller.BUTTON_RROTATE)) rt = 1;
				else if(ctrl.isButtonActivated(Controller.BUTTON_DROTATE)) rt = 2;
				if(rt != 0) rotatePiece(rt, false);
			} else {
				irsDirection = 0;
			}

			// Left/Right movement
			int move = getMoveDirection();

			if(move != 0) {
				boolean moveflag = false;

				if(move == -1) {
					if(dasNowLeft == 1) {
						arrNowLeft = 0;
						moveflag = true;
					} else if(dasNowLeft >= getDAS()) {
						arrNowLeft++;
						if(arrNowLeft >= getARR()) {
							arrNowLeft = 0;
							moveflag = true;
						}
					}
				} else if(move == 1) {
					if(dasNowRight == 1) {
						arrNowRight = 0;
						moveflag = true;
					} else if(dasNowRight >= getDAS()) {
						arrNowRight++;
						if(arrNowRight >= getARR()) {
							arrNowRight = 0;
							moveflag = true;
						}
					}
				}

				// Move the piece
				if(moveflag) movePiece(move);
			}

			// Soft drop
			boolean softdropFlag = false;
			if( ctrl.isButtonPressed(Controller.BUTTON_SOFT) && (!softdropContinuousUse || !ruleopt.softdropLimit) ) {
				boolean moveflag = false;

				if(dasNowDown == 1) {
					moveflag = true;
					arrNowDown = 0;
				} else if(dasNowDown >= getSoftdropDAS()) {
					arrNowDown++;
					if(arrNowDown >= getSoftdropARR()) {
						arrNowDown = 0;
						moveflag = true;
					}
				}

				if(moveflag) {
					softdropTime++;

					while(softdropTime >= softdropTimeDenominator) {
						softdropTime -= softdropTimeDenominator;
						softdropPiece();
						softdropFlag = true;
					}
				}
			}

			// Hard drop
			boolean harddropFlag = false;
			if( ctrl.isButtonPressed(Controller.BUTTON_HARD) && (!harddropContinuousUse || !ruleopt.harddropLimit) ) {
				harddropPiece();
				harddropFlag = true;
			}

			if(!nowPieceObject.checkCollision(nowPieceX, nowPieceY + 1, engine.field)) {
				if((speed.gravity == 0) || (speed.denominator == 0)) {
					// 0G
				} else if((speed.gravity < 0) || (speed.denominator < 0)) {
					// 20G
					while(!nowPieceObject.checkCollision(nowPieceX, nowPieceY + 1, engine.field)) {
						nowPieceY++;

						if(nowPieceY > nowPieceDeepestY) {
							nowPieceDeepestY = nowPieceY;
							if(ruleopt.lockresetLimitUseDeepestY) {
								lockresetMoveCount = 0;
								lockresetRotateCount = 0;
							}
							if(ruleopt.lockresetFall) lockDelayNow = 0;
						}
						if(ruleopt.lockresetFall && !ruleopt.lockresetLimitUseDeepestY) {
							lockDelayNow = 0;
						}

						lastmove = LASTMOVE_FALL_AUTO;
					}
				} else {
					// Softdrop
					while(hoverSoftDrop >= speed.denominator) {
						hoverSoftDrop -= speed.denominator;

						if(!nowPieceObject.checkCollision(nowPieceX, nowPieceY + 1, engine.field)) {
							nowPieceY++;

							if(nowPieceY > nowPieceDeepestY) {
								nowPieceDeepestY = nowPieceY;
								if(ruleopt.lockresetLimitUseDeepestY) {
									lockresetMoveCount = 0;
									lockresetRotateCount = 0;
								}
								if(ruleopt.lockresetFall) lockDelayNow = 0;
							}
							if(ruleopt.lockresetFall && !ruleopt.lockresetLimitUseDeepestY) {
								lockDelayNow = 0;
							}

							lastmove = LASTMOVE_FALL_SELF;
							playSE("softdrop");

							if(nowPieceObject.checkCollision(nowPieceX, nowPieceY + 1, engine.field)) {
								playSE("step");
							}
						} else {
							//gMsec = 0;
							break;
						}
					}

					// Gravity
					hoverTime += speed.gravity;

					while(hoverTime >= speed.denominator) {
						hoverTime -= speed.denominator;

						if(!nowPieceObject.checkCollision(nowPieceX, nowPieceY + 1, engine.field)) {
							nowPieceY++;

							if(nowPieceY > nowPieceDeepestY) {
								nowPieceDeepestY = nowPieceY;
								if(ruleopt.lockresetLimitUseDeepestY) {
									lockresetMoveCount = 0;
									lockresetRotateCount = 0;
								}
								if(ruleopt.lockresetFall) lockDelayNow = 0;
							}
							if(ruleopt.lockresetFall && !ruleopt.lockresetLimitUseDeepestY) {
								lockDelayNow = 0;
							}

							lastmove = LASTMOVE_FALL_AUTO;

							if(nowPieceObject.checkCollision(nowPieceX, nowPieceY + 1, engine.field)) {
								playSE("step");
							}
						} else {
							break;
						}
					}
				}
			}

			// Lock delay
			if(nowPieceObject.checkCollision(nowPieceX, nowPieceY + 1, engine.field)) {
				lockDelayNow++;
				hoverSoftDrop = 0;

				if(isLockResetLimitExceeded() && (ruleopt.lockresetLimitOver == RuleOptions.LOCKRESET_LIMIT_OVER_INSTANT))
					instantLock = true;

				if((lockDelayNow >= speed.lockDelay) || (instantLock)) {
					// The current piece has been locked
					boolean isPartialLockout = nowPieceObject.isPartialLockOut(nowPieceX, nowPieceY, engine.field);
					boolean placed = nowPieceObject.placeToField(nowPieceX, nowPieceY, engine.field);
					nowPieceObject = null;
					holdUsed = false;
					playSE("lock");

					if(softdropFlag) softdropContinuousUse = true;
					if(harddropFlag) harddropContinuousUse = true;

					if((!placed && ruleopt.fieldLockoutDeath) || (isPartialLockout && ruleopt.fieldPartialLockoutDeath)) {
						// Signal GameOver
						if(!engine.owner.gameMode.playerDeath(this, GameEngine.DEATH_LOCKOUT)) {
							engine.signalGameOver(GameEngine.GAMEOVER_LOSE, GameEngine.DEATH_LOCKOUT);
						}
					} else if(speed.lockFlash > 0) {
						// Lock flash
						stat = STAT_LOCKFLASH;
					} else {
						int lineClears = engine.field.checkLine();

						if(lineClears > 0) {
							// Line clear
							stat = STAT_LINECLEAR;
						} else if(speed.are > 0) {
							// ARE
							areMax = speed.are;
							stat = STAT_ARE;
						} else {
							// No status change
							fromZeroDelay = true;
						}

						return;
					}
				}
			}
		}

		moveTime++;
	}

	/**
	 * Lock Flash state
	 */
	public void onLockFlash() {
		if(engine.owner.gameMode.onLockFlash(this)) return;

		updateDAS();
		updateController();

		lockFlashNow++;

		if(lockFlashNow >= speed.lockFlash) {
			int lineClears = engine.field.checkLine();

			if(lineClears > 0) {
				stat = STAT_LINECLEAR;
			} else if(speed.are > 0) {
				areMax = speed.are;
				stat = STAT_ARE;
			} else {
				fromZeroDelay = false;
				stat = STAT_MOVE;
			}
		}
	}

	/**
	 * Line Clear state
	 */
	public void onLineClear() {
		if(engine.owner.gameMode.onLineClear(this)) return;

		updateDAS();
		updateController();

		long prevDelay = lineDelayNow;
		lineDelayNow++;

		if(prevDelay == 0) {
			int li = engine.field.clearLine();
			playSE("erase" + li);
		}

		if(lineDelayNow >= speed.lineDelay) {
			engine.field.downFloatingBlocks();
			playSE("linefall");

			if(speed.areLine > 0) {
				areMax = speed.areLine;
				stat = STAT_ARE;
			} else {
				fromZeroDelay = false;
				stat = STAT_MOVE;
			}
		}
	}

	/**
	 * ARE state
	 */
	public void onARE() {
		if(engine.owner.gameMode.onARE(this)) return;

		updateDAS();
		updateController();

		areNow++;

		if(areNow >= areMax) {
			fromZeroDelay = false;
			stat = STAT_MOVE;
		}
	}
}
