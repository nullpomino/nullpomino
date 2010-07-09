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
package org.game_host.hebo.nullpomino.game.play;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

import org.apache.log4j.Logger;
import org.game_host.hebo.nullpomino.game.component.BGMStatus;
import org.game_host.hebo.nullpomino.game.component.Block;
import org.game_host.hebo.nullpomino.game.component.Controller;
import org.game_host.hebo.nullpomino.game.component.Field;
import org.game_host.hebo.nullpomino.game.component.Piece;
import org.game_host.hebo.nullpomino.game.component.ReplayData;
import org.game_host.hebo.nullpomino.game.component.RuleOptions;
import org.game_host.hebo.nullpomino.game.component.SpeedParam;
import org.game_host.hebo.nullpomino.game.component.Statistics;
import org.game_host.hebo.nullpomino.game.component.WallkickResult;
import org.game_host.hebo.nullpomino.game.subsystem.ai.AIPlayer;
import org.game_host.hebo.nullpomino.game.subsystem.randomizer.MemorylessRandomizer;
import org.game_host.hebo.nullpomino.game.subsystem.randomizer.Randomizer;
import org.game_host.hebo.nullpomino.game.subsystem.wallkick.Wallkick;

/**
 * 各プレイヤーのゲームの処理
 */
public class GameEngine {
	/** ログ */
	static Logger log = Logger.getLogger(GameEngine.class);

	/** ステータス定数 */
	public static final int STAT_NOTHING = -1,
							STAT_SETTING = 0,
							STAT_READY = 1,
							STAT_MOVE = 2,
							STAT_LOCKFLASH = 3,
							STAT_LINECLEAR = 4,
							STAT_ARE = 5,
							STAT_ENDINGSTART = 6,
							STAT_CUSTOM = 7,
							STAT_EXCELLENT = 8,
							STAT_GAMEOVER = 9,
							STAT_RESULT = 10,
							STAT_FIELDEDIT = 11,
							STAT_INTERRUPTITEM = 12;

	/** ステータスカウンタの数 */
	public static final int MAX_STATC = 10;

	/** 最後にした操作の定数 */
	public static final int LASTMOVE_NONE = 0,
							LASTMOVE_FALL_AUTO = 1,
							LASTMOVE_FALL_SELF = 2,
							LASTMOVE_SLIDE_AIR = 3,
							LASTMOVE_SLIDE_GROUND = 4,
							LASTMOVE_ROTATE_AIR = 5,
							LASTMOVE_ROTATE_GROUND = 6;

	/** 枠線表示の種類の定数 */
	public static final int BLOCK_OUTLINE_NONE = 0, BLOCK_OUTLINE_NORMAL = 1, BLOCK_OUTLINE_CONNECT = 2, BLOCK_OUTLINE_SAMECOLOR = 3;

	/** Ready→Goにかかるフレーム数のデフォルト値 */
	public static final int READY_START = 0, READY_END = 49, GO_START = 50, GO_END = 100;

	/** フレームの色の定数 */
	public static final int FRAME_COLOR_BLUE = 0, FRAME_COLOR_GREEN = 1, FRAME_COLOR_RED = 2, FRAME_COLOR_GRAY = 3, FRAME_COLOR_YELLOW = 4,
							FRAME_COLOR_CYAN = 5, FRAME_COLOR_PINK = 6, FRAME_COLOR_PURPLE = 7;

	/** メーター色の定数 */
	public static final int METER_COLOR_RED = 0, METER_COLOR_ORANGE = 1, METER_COLOR_YELLOW = 2, METER_COLOR_GREEN = 3;

	/** T-Spin Miniの判定方法の定数 */
	public static final int TSPINMINI_TYPE_ROTATECHECK = 0, TSPINMINI_TYPE_WALLKICKFLAG = 1;

	/** コンボの種類の定数 */
	public static final int COMBO_TYPE_DISABLE = 0, COMBO_TYPE_NORMAL = 1, COMBO_TYPE_DOUBLE = 2;

	/** プレイを中断する効果のあるアイテムの定数 */
	public static final int INTERRUPTITEM_NONE = 0,
							INTERRUPTITEM_MIRROR = 1;

	/** Line gravity types */
	public static final int LINE_GRAVITY_NATIVE = 0, LINE_GRAVITY_CASCADE = 1;

	/** カラーアイテム用テーブル */
	public static final int[] ITEM_COLOR_BRIGHT_TABLE =
	{
		10, 10,  9,  9,  8,  8,  8,  7,  7,  7,
		 6,  6,  6,  5,  5,  5,  4,  4,  4,  4,
		 3,  3,  3,  3,  2,  2,  2,  2,  1,  1,
		 1,  1,  0,  0,  0,  0,  0,  0,  0,  0
	};

	/** このゲームエンジンを所有するGameOwnerクラス */
	public GameManager owner;

	/** プレイヤーの番号 */
	public int playerID;

	/** ルール設定 */
	public RuleOptions ruleopt;

	/** 壁蹴りシステム */
	public Wallkick wallkick;

	/** ブロックピースの出現順の生成アルゴリズム */
	public Randomizer randomizer;

	/** フィールド */
	public Field field;

	/** 入力状況 */
	public Controller ctrl;

	/** スコアなどの情報 */
	public Statistics statistics;

	/** 落下速度データ */
	public SpeedParam speed;

	/** 落下速度カウンタ（これがspeed.denominatorに達するとブロックピースが1段下がる） */
	public int gcount;

	/** 最初の乱数シード */
	public long randSeed;

	/** ランダム番号生成 */
	public Random random;

	/** リプレイで使用する入力データ */
	public ReplayData replayData;

	/** AI */
	public AIPlayer ai;

	/** AIの移動速度 */
	public int aiMoveDelay;

	/** AIの思考ルーチンの停止時間（スレッドを使う場合のみ） */
	public int aiThinkDelay;

	/** AIでスレッドを使う */
	public boolean aiUseThread;

	/** 現在のステータス番号 */
	public int stat;

	/** 各ステータスが自由に使える変数 */
	public int[] statc;

	/** ゲーム中ならtrue */
	public boolean gameActive;

	/** タイマー動作中ならtrue */
	public boolean timerActive;

	/** リプレイ用タイマー */
	public int replayTimer;

	/** ゲーム開始時のミリ秒 */
	public long startTime;

	/** ゲーム終了時のミリ秒 */
	public long endTime;

	/** メジャーバージョン */
	public float versionMajor;

	/** マイナーバージョン */
	public int versionMinor;

	/** 古いマイナーバージョン(6.9以前との互換性用) */
	public float versionMinorOld;

	/** ゲーム終了フラグ */
	public boolean quitflag;

	/** 操作中のブロックピースのオブジェクト */
	public Piece nowPieceObject;

	/** 操作中のブロックピースのX座標 */
	public int nowPieceX;

	/** 操作中のブロックピースのY座標 */
	public int nowPieceY;

	/** 操作中のブロックピースをそのまま落とした場合のY座標 */
	public int nowPieceBottomY;

	/** 操作中のブロックピースの色(-1で変更しない) */
	public int nowPieceColorOverride;

	/** 出現可能なピースの種類の配列 */
	public boolean[] nextPieceEnable;

	/** NEXTピース配列のサイズ（デフォルト1400、無視されることもある） */
	public int nextPieceArraySize;

	/** NEXTピースのIDの配列 */
	public int[] nextPieceArrayID;

	/** NEXTピースのオブジェクトの配列 */
	public Piece[] nextPieceArrayObject;

	/** ブロックピースを置いた回数（NEXTピースの計算用） */
	public int nextPieceCount;

	/** ホールドに入っているブロックピースのオブジェクト（null:なし） */
	public Piece holdPieceObject;

	/** ホールドを使った直後ならtrue */
	public boolean holdDisable;

	/** ホールドを使った回数 */
	public int holdUsedCount;

	/** 今消えているライン数 */
	public int lineClearing;

	/** Line gravity type (Native, Cascade, etc) */
	public int lineGravityType;

	/** Current number of chains */
	public int chain;

	/** 地面に触れてから経過したフレーム数 */
	public int lockDelayNow;

	/** 横溜めカウント */
	public int dasCount;

	/** 横溜め方向 */
	public int dasDirection;

	/** 横溜め速度カウント */
	public int dasSpeedCount;

	/** 先行回転 */
	public int initialRotateDirection;

	/** 最後の先行回転の方向 */
	public int initialRotateLastDirection;

	/** 先行回転連続使用フラグ */
	public boolean initialRotateContinuousUse;

	/** 先行ホールド */
	public boolean initialHoldFlag;

	/** 先行ホールド連続使用フラグ */
	public boolean initialHoldContinuousUse;

	/** 今のピースが移動した回数 */
	public int nowPieceMoveCount;

	/** 今のピースが回転した回数 */
	public int nowPieceRotateCount;

	/** 接地状態で移動した回数 */
	public int extendedMoveCount;

	/** 接地状態で回転した回数 */
	public int extendedRotateCount;

	/** 壁蹴りを使用した回数 */
	public int nowWallkickCount;

	/** 上方向への壁蹴りをした回数 */
	public int nowUpwardWallkickCount;

	/** ソフトドロップで落ちた段数 */
	public int softdropFall;

	/** ハードドロップで落ちた合計段数 */
	public int harddropFall;

	/** ソフトドロップ連続使用フラグ */
	public boolean softdropContinuousUse;

	/** ハードドロップ連続使用フラグ */
	public boolean harddropContinuousUse;

	/** 手動で固定したらtrue */
	public boolean manualLock;

	/** 最後にした操作の種類 */
	public int lastmove;

	/** T-Spinならtrue */
	public boolean tspin;

	/** T-Spin Miniならtrue */
	public boolean tspinmini;

	/** B2Bならtrue */
	public boolean b2b;

	/** B2B用カウンタ */
	public int b2bcount;

	/** コンボカウンタ */
	public int combo;

	/** T-Spin有効フラグ */
	public boolean tspinEnable;

	/** 壁蹴りありのT-Spin許可 */
	public boolean tspinAllowKick;

	/** T-Spin Miniの判定方法 */
	public int tspinminiType;

	/** O以外の全ピースにスピンボーナスを付ける */
	public boolean useAllSpinBonus;

	/** B2B有効フラグ */
	public boolean b2bEnable;

	/** コンボの種類 */
	public int comboType;

	/** ブロックを置いてから非表示にするまでのフレーム数（-1:なし） */
	public int blockHidden;

	/** ブロックが見えなくなるときに半透明効果を使うかどうか */
	public boolean blockHiddenAnim;

	/** ブロックの枠線表示の種類 */
	public int blockOutlineType;

	/** ブロックの枠線だけ表示する */
	public boolean blockShowOutlineOnly;

	/** ヘボHIDDEN 有効 */
	public boolean heboHiddenEnable;

	/** ヘボHIDDEN タイマー現在値 */
	public int heboHiddenTimerNow;

	/** ヘボHIDDEN タイマー最大値 */
	public int heboHiddenTimerMax;

	/** ヘボHIDDEN 進行度現在値 */
	public int heboHiddenYNow;

	/** ヘボHIDDEN 進行度制限値 */
	public int heboHiddenYLimit;

	/** Set when ARE or line delay is canceled */
	public boolean delayCancel;

	/** Piece must move left after canceled delay */
	public boolean delayCancelMoveLeft;

	/** Piece must move right after canceled delay */
	public boolean delayCancelMoveRight;

	/** 骨ブロック */
	public boolean bone;

	/** ビッグ */
	public boolean big;

	/** ビッグのときの横移動 */
	public boolean bigmove;

	/** ビッグで消去ラインを半分にする */
	public boolean bighalf;

	/** 壁蹴りを使ったらtrue */
	public boolean kickused;

	/** フィールドサイズ（-1にするとデフォルト） */
	public int fieldWidth, fieldHeight, fieldHiddenHeight;

	/** エンディングかどうか */
	public int ending;

	/** エンディング後にスタッフロール */
	public boolean staffrollEnable;

	/** ロール中に死なない */
	public boolean staffrollNoDeath;

	/** ロール中にライン数などの情報を更新 */
	public boolean staffrollEnableStatistics;

	/** フレームの色 */
	public int framecolor;

	/** READY→GOにかかるフレーム数 */
	public int readyStart, readyEnd, goStart, goEnd;

	/** 2回目以降のREADYGOならtrue */
	public boolean readyDone;

	/** 残機 */
	public int lives;

	/** ゴースト表示 */
	public boolean ghost;

	/** フィールド右のメーター量 */
	public int meterValue;

	/** フィールド右のメーター色 */
	public int meterColor;

	/** ラグ発生フラグ（ピースを置いたあとフラグ解除までARE発生） */
	public boolean lagARE;

	/** ラグ発生フラグ（ゲームをストップさせる） */
	public boolean lagStop;

	/** 画面表示半分 */
	public boolean minidisplay;

	/** 効果音再生許可 */
	public boolean enableSE;

	/** 1人がゲームオーバーになれば全員終了させる */
	public boolean gameoverAll;

	/** フィールド全体を表示するかどうか */
	public boolean isVisible;

	/** NEXT欄を表示するかどうか */
	public boolean isNextVisible;

	/** ホールド欄を表示するかどうか */
	public boolean isHoldVisible;

	/** フィールドエディット画面でのカーソル座標 */
	public int fldeditX, fldeditY;

	/** フィールドエディット画面で選択しているブロック色 */
	public int fldeditColor;

	/** フィールドエディット画面に入る前にいたステータス番号 */
	public int fldeditPreviousStat;

	/** フィールドエディット画面に入ってから経過した時間 */
	public int fldeditFrames;

	/** Ready・Go中にホールドを押すとNEXTを1個飛ばす */
	public boolean holdButtonNextSkip;

	/** EventReceiver側でのテキスト描画を許可 */
	public boolean allowTextRenderByReceiver;

	/** ロールロール(自動回転)有効フラグ */
	public boolean itemRollRollEnable;

	/** ロールロール(自動回転)で回転する間隔 */
	public int itemRollRollInterval;

	/** X-RAY有効フラグ */
	public boolean itemXRayEnable;

	/** X-RAY用カウンタ */
	public int itemXRayCount;

	/** カラー有効フラグ */
	public boolean itemColorEnable;

	/** カラー用カウンタ */
	public int itemColorCount;

	/** プレイ中断効果のあるアイテム */
	public int interruptItemNumber;

	/** プレイ中断効果のあるアイテムが終了したあとのステータス */
	public int interruptItemPreviousStat;

	/** ミラー用バックアップフィールド */
	public Field interruptItemMirrorField;

	/** Aボタンでの回転方向を -1=ルールに従う 0=常に左回転 1=常に右回転 */
	public int owRotateButtonDefaultRight;

	/** ブロックの絵柄 -1=ルールに従う 0以上=固定 */
	public int owSkin;

	/** 最低/最大横溜め速度 -1=ルールに従う 0以上=固定 */
	public int owMinDAS, owMaxDAS;

	/** 横移動速度 -1=ルールに従う 0以上=固定 */
	public int owDasDelay;

	/**
	 * コンストラクタ
	 * @param owner このゲームエンジンを所有するGameOwnerクラス
	 * @param playerID プレイヤーの番号
	 */
	public GameEngine(GameManager owner, int playerID) {
		this.owner = owner;
		this.playerID = playerID;
		this.ruleopt = new RuleOptions();
		this.wallkick = null;
		this.randomizer = null;

		owRotateButtonDefaultRight = -1;
		owSkin = -1;
		owMinDAS = -1;
		owMaxDAS = -1;
		owDasDelay = -1;
	}

	/**
	 * ルール設定などのパラメータ付きのコンストラクタ
	 * @param owner このゲームエンジンを所有するGameOwnerクラス
	 * @param playerID プレイヤーの番号
	 * @param ruleopt ルール設定
	 * @param wallkick 壁蹴りシステム
	 * @param randomizer ブロックピースの出現順の生成アルゴリズム
	 */
	public GameEngine(GameManager owner, int playerID, RuleOptions ruleopt, Wallkick wallkick, Randomizer randomizer) {
		this(owner,playerID);
		this.ruleopt = ruleopt;
		this.wallkick = wallkick;
		this.randomizer = randomizer;
	}

	/**
	 * READY前の初期化
	 */
	public void init() {
		//log.debug("GameEngine init() playerID:" + playerID);

		field = null;
		ctrl = new Controller();
		statistics = new Statistics();
		speed = new SpeedParam();
		gcount = 0;
		replayData = new ReplayData();

		if(owner.replayMode == false) {
			versionMajor = GameManager.getVersionMajor();
			versionMinor = GameManager.getVersionMinor();
			versionMinorOld = GameManager.getVersionMinorOld();

			Random tempRand = new Random();
			randSeed = tempRand.nextLong();
			random = new Random(randSeed);
		} else {
			versionMajor = owner.replayProp.getProperty("version.core.major", 0f);
			versionMinor = owner.replayProp.getProperty("version.core.minor", 0);
			versionMinorOld = owner.replayProp.getProperty("version.core.minor", 0f);

			replayData.readProperty(owner.replayProp, playerID);

			String tempRand = owner.replayProp.getProperty(playerID + ".replay.randSeed", "0");
			randSeed = Long.parseLong(tempRand, 16);
			random = new Random(randSeed);

			owRotateButtonDefaultRight = owner.replayProp.getProperty(playerID + ".tuning.owRotateButtonDefaultRight", -1);
			owSkin = owner.replayProp.getProperty(playerID + ".tuning.owSkin", -1);
			owMinDAS = owner.replayProp.getProperty(playerID + ".tuning.owMinDAS", -1);
			owMaxDAS = owner.replayProp.getProperty(playerID + ".tuning.owMaxDAS", -1);
			owDasDelay = owner.replayProp.getProperty(playerID + ".tuning.owDasDelay", -1);
		}

		quitflag = false;

		stat = STAT_SETTING;
		statc = new int[MAX_STATC];

		gameActive = false;
		timerActive = false;
		replayTimer = 0;

		nowPieceObject = null;
		nowPieceX = 0;
		nowPieceY = 0;
		nowPieceBottomY = 0;
		nowPieceColorOverride = -1;

		nextPieceArraySize = 1400;
		nextPieceEnable = new boolean[Piece.PIECE_COUNT];
		for(int i = 0; i < Piece.PIECE_STANDARD_COUNT; i++) nextPieceEnable[i] = true;
		nextPieceArrayID = null;
		nextPieceArrayObject = null;
		nextPieceCount = 0;

		holdPieceObject = null;
		holdDisable = false;
		holdUsedCount = 0;

		lineClearing = 0;
		lineGravityType = LINE_GRAVITY_NATIVE;
		chain = 0;

		lockDelayNow = 0;

		dasCount = 0;
		dasDirection = 0;
		dasSpeedCount = 0;

		initialRotateDirection = 0;
		initialRotateLastDirection = 0;
		initialHoldFlag = false;
		initialRotateContinuousUse = false;
		initialHoldContinuousUse = false;

		nowPieceMoveCount = 0;
		nowPieceRotateCount = 0;
		extendedMoveCount = 0;
		extendedRotateCount = 0;

		nowWallkickCount = 0;
		nowUpwardWallkickCount = 0;

		softdropFall = 0;
		harddropFall = 0;
		softdropContinuousUse = false;
		harddropContinuousUse = false;

		manualLock = false;

		lastmove = LASTMOVE_NONE;

		tspin = false;
		tspinmini = false;
		b2b = false;
		b2bcount = 0;
		combo = 0;

		tspinEnable = false;
		tspinAllowKick = true;
		tspinminiType = TSPINMINI_TYPE_ROTATECHECK;
		useAllSpinBonus = false;
		b2bEnable = false;
		comboType = COMBO_TYPE_DISABLE;

		blockHidden = -1;
		blockHiddenAnim = true;
		blockOutlineType = BLOCK_OUTLINE_NORMAL;
		blockShowOutlineOnly = false;

		heboHiddenEnable = false;
		heboHiddenTimerNow = 0;
		heboHiddenTimerMax = 0;
		heboHiddenYNow = 0;
		heboHiddenYLimit = 0;

		delayCancel = false;
		delayCancelMoveLeft = false;
		delayCancelMoveRight = false;

		bone = false;

		big = false;
		bigmove = true;
		bighalf = true;

		kickused = false;

		fieldWidth = -1;
		fieldHeight = -1;
		fieldHiddenHeight = -1;

		ending = 0;
		staffrollEnable = false;
		staffrollNoDeath = false;
		staffrollEnableStatistics = false;

		framecolor = FRAME_COLOR_BLUE;

		readyStart = READY_START;
		readyEnd = READY_END;
		goStart = GO_START;
		goEnd = GO_END;

		readyDone = false;

		lives = 0;

		ghost = true;

		meterValue = 0;
		meterColor = METER_COLOR_RED;

		lagARE = false;
		lagStop = false;
		minidisplay = (playerID >= 2);

		enableSE = true;
		gameoverAll = true;

		isNextVisible = true;
		isHoldVisible = true;
		isVisible = true;

		holdButtonNextSkip = false;

		allowTextRenderByReceiver = true;

		itemRollRollEnable = false;
		itemRollRollInterval = 30;

		itemXRayEnable = false;
		itemXRayCount = 0;

		itemColorEnable = false;
		itemColorCount = 0;

		interruptItemNumber = INTERRUPTITEM_NONE;

		// イベント発生
		if(owner.mode != null) {
			owner.mode.playerInit(this, playerID);
			if(owner.replayMode) owner.mode.loadReplay(this, playerID, owner.replayProp);
		}
		owner.receiver.playerInit(this, playerID);
		if(ai != null) {
			ai.shutdown(this, playerID);
			ai.init(this, playerID);
		}
	}

	/**
	 * 終了処理
	 */
	public void shutdown() {
		//log.debug("GameEngine shutdown() playerID:" + playerID);

		if(ai != null) ai.shutdown(this, playerID);
		owner = null;
		ruleopt = null;
		wallkick = null;
		randomizer = null;
		field = null;
		ctrl = null;
		statistics = null;
		speed = null;
		random = null;
		replayData = null;
	}

	/**
	 * ステータスカウンタ初期化
	 */
	public void resetStatc() {
		for(int i = 0; i < statc.length; i++) statc[i] = 0;
	}

	/**
	 * 効果音を再生する（enableSEがtrueのときだけ）
	 * @param name 効果音の名前
	 */
	public void playSE(String name) {
		if(enableSE) owner.receiver.playSE(name);
	}

	/**
	 * NEXTピースのIDを取得
	 * @param c 取得したいNEXTの位置
	 * @return NEXTピースのID
	 */
	public int getNextID(int c) {
		if(nextPieceArrayID == null) return Piece.PIECE_NONE;
		int c2 = c;
		while(c2 >= nextPieceArrayID.length) c2 = c2 - nextPieceArrayID.length;
		return nextPieceArrayID[c2];
	}

	/**
	 * NEXTピースのオブジェクトを取得
	 * @param c 取得したいNEXTの位置
	 * @return NEXTピースのオブジェクト
	 */
	public Piece getNextObject(int c) {
		if(nextPieceArrayObject == null) return null;
		int c2 = c;
		while(c2 >= nextPieceArrayObject.length) c2 = c2 - nextPieceArrayObject.length;
		return nextPieceArrayObject[c2];
	}

	/**
	 * NEXTピースのオブジェクトのコピーを取得
	 * @param c 取得したいNEXTの位置
	 * @return NEXTピースのオブジェクトのコピー
	 */
	public Piece getNextObjectCopy(int c) {
		Piece p = getNextObject(c);
		Piece r = null;
		if(p != null) r = new Piece(p);
		return r;
	}

	/**
	 * 現在のAREの値を取得（ルール設定も考慮）
	 * @return 現在のARE
	 */
	public int getARE() {
		if((speed.are < ruleopt.minARE) && (ruleopt.minARE >= 0)) return ruleopt.minARE;
		if((speed.are > ruleopt.maxARE) && (ruleopt.maxARE >= 0)) return ruleopt.maxARE;
		return speed.are;
	}

	/**
	 * 現在のライン消去後AREの値を取得（ルール設定も考慮）
	 * @return 現在のライン消去後ARE
	 */
	public int getARELine() {
		if((speed.areLine < ruleopt.minARELine) && (ruleopt.minARELine >= 0)) return ruleopt.minARELine;
		if((speed.areLine > ruleopt.maxARELine) && (ruleopt.maxARELine >= 0)) return ruleopt.maxARELine;
		return speed.areLine;
	}

	/**
	 * 現在のライン消去時間の値を取得（ルール設定も考慮）
	 * @return 現在のライン消去時間
	 */
	public int getLineDelay() {
		if((speed.lineDelay < ruleopt.minLineDelay) && (ruleopt.minLineDelay >= 0)) return ruleopt.minLineDelay;
		if((speed.lineDelay > ruleopt.maxLineDelay) && (ruleopt.maxLineDelay >= 0)) return ruleopt.maxLineDelay;
		return speed.lineDelay;
	}

	/**
	 * 現在の固定時間の値を取得（ルール設定も考慮）
	 * @return 現在の固定時間
	 */
	public int getLockDelay() {
		if((speed.lockDelay < ruleopt.minLockDelay) && (ruleopt.minLockDelay >= 0)) return ruleopt.minLockDelay;
		if((speed.lockDelay > ruleopt.maxLockDelay) && (ruleopt.maxLockDelay >= 0)) return ruleopt.maxLockDelay;
		return speed.lockDelay;
	}

	/**
	 * 現在のDASの値を取得（ルール設定も考慮）
	 * @return 現在のDAS
	 */
	public int getDAS() {
		if((speed.das < owMinDAS) && (owMinDAS >= 0)) return owMinDAS;
		if((speed.das > owMaxDAS) && (owMaxDAS >= 0)) return owMaxDAS;
		if((speed.das < ruleopt.minDAS) && (ruleopt.minDAS >= 0)) return ruleopt.minDAS;
		if((speed.das > ruleopt.maxDAS) && (ruleopt.maxDAS >= 0)) return ruleopt.maxDAS;
		return speed.das;
	}

	/**
	 * 現在の横移動速度を取得
	 * @return 横移動速度
	 */
	public int getDASDelay() {
		if((ruleopt == null) || (owDasDelay >= 0)) {
			return owDasDelay;
		}
		return ruleopt.dasDelay;
	}

	/**
	 * 現在使用中のブロックスキン番号を取得
	 * @return ブロックスキン番号
	 */
	public int getSkin() {
		if((ruleopt == null) || (owSkin >= 0)) {
			return owSkin;
		}
		return ruleopt.skin;
	}

	/**
	 * @return Aボタンを押したときに左回転するならfalse、右回転するならtrue
	 */
	public boolean isRotateButtonDefaultRight() {
		if((ruleopt == null) || (owRotateButtonDefaultRight >= 0)) {
			if(owRotateButtonDefaultRight == 0) return false;
			else return true;
		}
		return ruleopt.rotateButtonDefaultRight;
	}

	/**
	 * 見え／消えロール状態のフィールドを通常状態に戻す
	 */
	public void resetFieldVisible() {
		if(field != null) {
			for(int x = 0; x < field.getWidth(); x++) {
				for(int y = 0; y < field.getHeight(); y++) {
					Block blk = field.getBlock(x, y);

					if((blk != null) && (blk.color > Block.BLOCK_COLOR_NONE)) {
						blk.alpha = 1f;
						blk.darkness = 0f;
						blk.setAttribute(Block.BLOCK_ATTRIBUTE_VISIBLE, true);
						blk.setAttribute(Block.BLOCK_ATTRIBUTE_OUTLINE, true);
					}
				}
			}
		}
	}

	/**
	 * ソフト・ハードドロップ・先行ホールド・先行回転の使用制限解除
	 */
	public void checkDropContinuousUse() {
		if(gameActive) {
			if((!ctrl.isPress(Controller.BUTTON_DOWN)) || (!ruleopt.softdropLimit))
				softdropContinuousUse = false;
			if((!ctrl.isPress(Controller.BUTTON_UP)) || (!ruleopt.harddropLimit))
				harddropContinuousUse = false;
			if((!ctrl.isPress(Controller.BUTTON_D)) || (!ruleopt.holdInitialLimit))
				initialHoldContinuousUse = false;
			if(!ruleopt.rotateInitialLimit)
				initialRotateContinuousUse = false;

			if(initialRotateContinuousUse) {
				int dir = 0;
				if(ctrl.isPress(Controller.BUTTON_A) || ctrl.isPress(Controller.BUTTON_C)) dir = -1;
				else if(ctrl.isPress(Controller.BUTTON_B)) dir = 1;
				else if(ctrl.isPress(Controller.BUTTON_E)) dir = 2;

				if((initialRotateLastDirection != dir) || (dir == 0))
					initialRotateContinuousUse = false;
			}
		}
	}

	/**
	 * 横移動入力の方向を取得
	 * @return -1:左 0:なし 1:右
	 */
	public int getMoveDirection() {
		if(ctrl.isPress(Controller.BUTTON_LEFT) && ctrl.isPress(Controller.BUTTON_RIGHT)) {
			if(ruleopt.moveLeftAndRightAllow) {
				if(ctrl.buttonTime[Controller.BUTTON_LEFT] > ctrl.buttonTime[Controller.BUTTON_RIGHT])
					return ruleopt.moveLeftAndRightUsePreviousInput ? -1 : 1;
				else if(ctrl.buttonTime[Controller.BUTTON_LEFT] < ctrl.buttonTime[Controller.BUTTON_RIGHT])
					return ruleopt.moveLeftAndRightUsePreviousInput ? 1 : -1;
			}
		} else if(ctrl.isPress(Controller.BUTTON_LEFT)) {
			return -1;
		} else if(ctrl.isPress(Controller.BUTTON_RIGHT)) {
			return 1;
		}

		return 0;
	}

	/**
	 * 横溜め処理
	 */
	public void padRepeat() {
		int moveDirection = getMoveDirection();
		if(moveDirection != 0) {
			dasCount++;
		} else {
			dasCount = 0;
		}
		dasDirection = moveDirection;
	}

	/**
	 * 移動回数制限を超過しているか判定
	 * @return 移動回数制限を超過したらtrue
	 */
	public boolean isMoveCountExceed() {
		if(ruleopt.lockresetLimitShareCount == true) {
			if((extendedMoveCount + extendedRotateCount >= ruleopt.lockresetLimitMove) && (ruleopt.lockresetLimitMove >= 0))
				return true;
		} else {
			if((extendedMoveCount >= ruleopt.lockresetLimitMove) && (ruleopt.lockresetLimitMove >= 0))
				return true;
		}

		return false;
	}

	/**
	 * 回転回数制限を超過しているか判定
	 * @return 回転回数制限を超過したらtrue
	 */
	public boolean isRotateCountExceed() {
		if(ruleopt.lockresetLimitShareCount == true) {
			if((extendedMoveCount + extendedRotateCount >= ruleopt.lockresetLimitMove) && (ruleopt.lockresetLimitMove >= 0))
				return true;
		} else {
			if((extendedRotateCount >= ruleopt.lockresetLimitRotate) && (ruleopt.lockresetLimitRotate >= 0))
				return true;
		}

		return false;
	}

	/**
	 * T-Spin判定
	 * @param x X座標
	 * @param y Y座標
	 * @param piece 現在のブロックピース
	 * @param fld フィールド
	 * @return T-Spinならtrue
	 */
	public boolean isTSpin(int x, int y, Piece piece, Field fld) {
		if((piece == null) || (piece.id != Piece.PIECE_T)) return false;

		if(!tspinAllowKick && kickused) return false;

		int[] tx = new int[4];
		int[] ty = new int[4];

		// 判定用相対座標を設定
		if(piece.big == true) {
			tx[0] = 1;
			ty[0] = 1;
			tx[1] = 4;
			ty[1] = 1;
			tx[2] = 1;
			ty[2] = 4;
			tx[3] = 4;
			ty[3] = 4;
		} else {
			tx[0] = 0;
			ty[0] = 0;
			tx[1] = 2;
			ty[1] = 0;
			tx[2] = 0;
			ty[2] = 2;
			tx[3] = 2;
			ty[3] = 2;
		}
		for(int i = 0; i < tx.length; i++) {
			if(piece.big) {
				tx[i] += ruleopt.pieceOffsetX[piece.id][piece.direction] * 2;
				ty[i] += ruleopt.pieceOffsetY[piece.id][piece.direction] * 2;
			} else {
				tx[i] += ruleopt.pieceOffsetX[piece.id][piece.direction];
				ty[i] += ruleopt.pieceOffsetY[piece.id][piece.direction];
			}
		}

		// 判定
		int count = 0;

		for(int i = 0; i < tx.length; i++) {
			if(fld.getBlockColor(x + tx[i], y + ty[i]) != Block.BLOCK_COLOR_NONE) count++;
		}

		if(count >= 3) return true;

		return false;
	}

	/**
	 * Spin判定(全スピンルールのとき用)
	 * @param x X座標
	 * @param y Y座標
	 * @param piece 現在のブロックピース
	 * @param fld フィールド
	 */
	public void setAllSpin(int x, int y, Piece piece, Field fld) {
		tspin = false;
		tspinmini = false;

		if(piece == null) return;
		if(!tspinAllowKick && kickused) return;
		if(piece.big) return;

		int offsetX = ruleopt.pieceOffsetX[piece.id][piece.direction];
		int offsetY = ruleopt.pieceOffsetY[piece.id][piece.direction];

		for(int i = 0; i < Piece.SPINBONUSDATA_HIGH_X[piece.id][piece.direction].length / 2; i++) {
			boolean isHighSpot1 = false;
			boolean isHighSpot2 = false;
			boolean isLowSpot1 = false;
			boolean isLowSpot2 = false;

			if(!fld.getBlockEmptyF(
				x + Piece.SPINBONUSDATA_HIGH_X[piece.id][piece.direction][i * 2 + 0] + offsetX,
				y + Piece.SPINBONUSDATA_HIGH_Y[piece.id][piece.direction][i * 2 + 0] + offsetY))
			{
				isHighSpot1 = true;
			}
			if(!fld.getBlockEmptyF(
				x + Piece.SPINBONUSDATA_HIGH_X[piece.id][piece.direction][i * 2 + 1] + offsetX,
				y + Piece.SPINBONUSDATA_HIGH_Y[piece.id][piece.direction][i * 2 + 1] + offsetY))
			{
				isHighSpot2 = true;
			}
			if(!fld.getBlockEmptyF(
				x + Piece.SPINBONUSDATA_LOW_X[piece.id][piece.direction][i * 2 + 0] + offsetX,
				y + Piece.SPINBONUSDATA_LOW_Y[piece.id][piece.direction][i * 2 + 0] + offsetY))
			{
				isLowSpot1 = true;
			}
			if(!fld.getBlockEmptyF(
				x + Piece.SPINBONUSDATA_LOW_X[piece.id][piece.direction][i * 2 + 1] + offsetX,
				y + Piece.SPINBONUSDATA_LOW_Y[piece.id][piece.direction][i * 2 + 1] + offsetY))
			{
				isLowSpot2 = true;
			}

			//log.debug(isHighSpot1 + "," + isHighSpot2 + "," + isLowSpot1 + "," + isLowSpot2);

			if(isHighSpot1 && isHighSpot2 && (isLowSpot1 || isLowSpot2)) {
				tspin = true;
			} else if(!tspin && isLowSpot1 && isLowSpot2 && (isHighSpot1 || isHighSpot2)) {
				tspin = true;
				tspinmini = true;
			}
		}
	}

	/**
	 * ホールド可能かどうか判定
	 * @return ホールド可能ならtrue
	 */
	public boolean isHoldOK() {
		if( (!ruleopt.holdEnable) || (holdDisable) || ((holdUsedCount >= ruleopt.holdLimit) && (ruleopt.holdLimit >= 0)) || (initialHoldContinuousUse) )
			return false;

		return true;
	}

	/**
	 * ピースが出現するX座標を取得
	 * @param fld フィールド
	 * @param piece ピース
	 * @return 出現位置のX座標
	 */
	public int getSpawnPosX(Field fld, Piece piece) {
		int x = -1 + (fld.getWidth() - piece.getWidth() + 1) / 2;

		if((big == true) && (bigmove == true) && (x % 2 != 0))
			x++;

		if(big == true) {
			x += ruleopt.pieceSpawnXBig[piece.id][piece.direction];
		} else {
			x += ruleopt.pieceSpawnX[piece.id][piece.direction];
		}

		return x;
	}

	/**
	 * ピースが出現するY座標を取得
	 * @param piece ピース
	 * @return 出現位置のY座標
	 */
	public int getSpawnPosY(Piece piece) {
		int y = 0;

		if((ruleopt.pieceEnterAboveField == true) && (ruleopt.fieldCeiling == false)) {
			y = -1 - piece.getMaximumBlockY();
			if(big == true) y--;
		} else {
			y = -piece.getMinimumBlockY();
		}

		if(big == true) {
			y += ruleopt.pieceSpawnYBig[piece.id][piece.direction];
		} else {
			y += ruleopt.pieceSpawnY[piece.id][piece.direction];
		}

		return y;
	}

	/**
	 * 回転ボタンを押したあとのピースの方向を取得
	 * @param move 回転方向（-1:左 1:右 2:180度）
	 * @return 回転ボタンを押したあとのピースの方向
	 */
	public int getRotateDirection(int move) {
		int rt = 0 + move;
		if(nowPieceObject != null) rt = nowPieceObject.direction + move;

		if(move == 2) {
			if(rt > 3) rt -= 4;
			if(rt < 0) rt += 4;
		} else {
			if(rt > 3) rt = 0;
			if(rt < 0) rt = 3;
		}

		return rt;
	}

	/**
	 * 先行回転と先行ホールドの処理
	 */
	public void initialRotate() {
		initialRotateDirection = 0;
		initialHoldFlag = false;

		if((ruleopt.rotateInitial == true) && (initialRotateContinuousUse == false)) {
			int dir = 0;
			if(ctrl.isPress(Controller.BUTTON_A) || ctrl.isPress(Controller.BUTTON_C)) dir = -1;
			else if(ctrl.isPress(Controller.BUTTON_B)) dir = 1;
			else if(ctrl.isPress(Controller.BUTTON_E)) dir = 2;
			initialRotateDirection = dir;
		}

		if((ctrl.isPress(Controller.BUTTON_D)) && (ruleopt.holdInitial == true) && isHoldOK()) {
			initialHoldFlag = true;
			initialHoldContinuousUse = true;
			playSE("initialhold");
		}
	}

	/**
	 * フィールドのブロックの状態を更新
	 */
	public void fieldUpdate() {
		if(field != null) {
			for(int i = 0; i < field.getWidth(); i++) {
				for(int j = (field.getHiddenHeight() * -1); j < field.getHeight(); j++) {
					Block blk = field.getBlock(i, j);

					if((blk != null) && (blk.color >= Block.BLOCK_COLOR_GRAY)) {
						if(blk.elapsedFrames < 0) {
							if(!blk.getAttribute(Block.BLOCK_ATTRIBUTE_GARBAGE))
								blk.darkness = 0f;
						} else if(blk.elapsedFrames < ruleopt.lockflash) {
							blk.darkness = -0.8f;
							if(blockShowOutlineOnly) {
								blk.setAttribute(Block.BLOCK_ATTRIBUTE_OUTLINE, true);
								blk.setAttribute(Block.BLOCK_ATTRIBUTE_VISIBLE, false);
								blk.setAttribute(Block.BLOCK_ATTRIBUTE_BONE, false);
							}
						} else {
							blk.darkness = 0f;
							blk.setAttribute(Block.BLOCK_ATTRIBUTE_OUTLINE, true);
							if(blockShowOutlineOnly) {
								blk.setAttribute(Block.BLOCK_ATTRIBUTE_VISIBLE, false);
								blk.setAttribute(Block.BLOCK_ATTRIBUTE_BONE, false);
							}
						}

						if((blockHidden != -1) && (blk.elapsedFrames >= blockHidden - 10) && (gameActive == true)) {
							if(blockHiddenAnim == true) {
								blk.alpha -= 0.1f;
								if(blk.alpha < 0.0f) blk.alpha = 0.0f;
							}

							if(blk.elapsedFrames >= blockHidden) {
								blk.alpha = 0.0f;
								blk.setAttribute(Block.BLOCK_ATTRIBUTE_OUTLINE, false);
								blk.setAttribute(Block.BLOCK_ATTRIBUTE_VISIBLE, false);
							}
						}

						if(blk.elapsedFrames >= 0) blk.elapsedFrames++;
					}
				}
			}
		}

		// X-RAY
		if((field != null) && (gameActive) && (itemXRayEnable)) {
			for(int i = 0; i < field.getWidth(); i++) {
				for(int j = (field.getHiddenHeight() * -1); j < field.getHeight(); j++) {
					Block blk = field.getBlock(i, j);

					if((blk != null) && (blk.color >= Block.BLOCK_COLOR_GRAY)) {
						blk.setAttribute(Block.BLOCK_ATTRIBUTE_VISIBLE, (itemXRayCount % 36 == i));
						blk.setAttribute(Block.BLOCK_ATTRIBUTE_OUTLINE, (itemXRayCount % 36 == i));
					}
				}
			}
			itemXRayCount++;
		} else {
			itemXRayCount = 0;
		}

		// COLOR
		if((field != null) && (gameActive) && (itemColorEnable)) {
			for(int i = 0; i < field.getWidth(); i++) {
				for(int j = (field.getHiddenHeight() * -1); j < field.getHeight(); j++) {
					int bright = j;
					if(bright >= 5) bright = 9 - bright;
					bright = 40 - ( (((20 - i) + bright) * 4 + itemColorCount) % 40 );
					if((bright >= 0) && (bright < ITEM_COLOR_BRIGHT_TABLE.length)) {
						bright = 10 - ITEM_COLOR_BRIGHT_TABLE[bright];
					}
					if(bright > 10) bright = 10;

					Block blk = field.getBlock(i, j);

					if(blk != null) {
						blk.alpha = bright * 0.1f;
						blk.setAttribute(Block.BLOCK_ATTRIBUTE_OUTLINE, false);
						blk.setAttribute(Block.BLOCK_ATTRIBUTE_VISIBLE, true);
					}
				}
			}
			itemColorCount++;
		} else {
			itemColorCount = 0;
		}

		// ヘボHIDDEN
		if(heboHiddenEnable && gameActive) {
			heboHiddenTimerNow++;

			if(heboHiddenTimerNow > heboHiddenTimerMax) {
				heboHiddenTimerNow = 0;
				heboHiddenYNow++;
				if(heboHiddenYNow > heboHiddenYLimit)
					heboHiddenYNow = heboHiddenYLimit;
			}
		}
	}

	/**
	 * リプレイ保存時の処理
	 */
	public void saveReplay() {
		if((owner.replayMode == true) && (owner.replayRerecord == false)) return;

		owner.replayProp.setProperty("version.core", versionMajor + "." + versionMinor);
		owner.replayProp.setProperty("version.core.major", versionMajor);
		owner.replayProp.setProperty("version.core.minor", versionMinor);

		owner.replayProp.setProperty(playerID + ".replay.randSeed", Long.toString(randSeed, 16));

		replayData.writeProperty(owner.replayProp, playerID, replayTimer);
		statistics.writeProperty(owner.replayProp, playerID);
		ruleopt.writeProperty(owner.replayProp, playerID);

		if(playerID == 0) {
			if(owner.mode != null) owner.replayProp.setProperty("name.mode", owner.mode.getName());
			if(ruleopt.strRuleName != null) owner.replayProp.setProperty("name.rule", ruleopt.strRuleName);

			GregorianCalendar currentTime = new GregorianCalendar();
			int month = currentTime.get(Calendar.MONTH) + 1;
			String strDate = String.format("%04d/%02d/%02d", currentTime.get(Calendar.YEAR), month, currentTime.get(Calendar.DATE));
			String strTime = String.format("%02d:%02d:%02d",
											currentTime.get(Calendar.HOUR_OF_DAY), currentTime.get(Calendar.MINUTE), currentTime.get(Calendar.SECOND));
			owner.replayProp.setProperty("timestamp.date", strDate);
			owner.replayProp.setProperty("timestamp.time", strTime);
		}

		owner.replayProp.setProperty(playerID + ".tuning.owRotateButtonDefaultRight", owRotateButtonDefaultRight);
		owner.replayProp.setProperty(playerID + ".tuning.owSkin", owSkin);
		owner.replayProp.setProperty(playerID + ".tuning.owMinDAS", owMinDAS);
		owner.replayProp.setProperty(playerID + ".tuning.owMaxDAS", owMaxDAS);
		owner.replayProp.setProperty(playerID + ".tuning.owDasDelay", owDasDelay);

		if(owner.mode != null) owner.mode.saveReplay(this, playerID, owner.replayProp);
	}

	/**
	 * フィールドエディット画面に入る処理
	 */
	public void enterFieldEdit() {
		fldeditPreviousStat = stat;
		stat = STAT_FIELDEDIT;
		fldeditX = 0;
		fldeditY = 0;
		fldeditColor = Block.BLOCK_COLOR_GRAY;
		fldeditFrames = 0;
		owner.menuOnly = false;
		createFieldIfNeeded();
	}

	/**
	 * フィールドを初期化（まだ存在しない場合）
	 */
	public void createFieldIfNeeded() {
		if(fieldWidth < 0) fieldWidth = ruleopt.fieldWidth;
		if(fieldHeight < 0) fieldHeight = ruleopt.fieldHeight;
		if(fieldHiddenHeight < 0) fieldHiddenHeight = ruleopt.fieldHiddenHeight;
		if(field == null) field = new Field(fieldWidth, fieldHeight, fieldHiddenHeight, ruleopt.fieldCeiling);
	}

	/**
	 * ゲームの状態の更新
	 */
	public void update() {
		if(gameActive) {
			// リプレイ関連の処理
			if(!owner.replayMode || owner.replayRerecord) {
				// AIのボタン処理
				if(ai != null) ai.setControl(this, playerID, ctrl);

				// 入力状態をリプレイに記録
				replayData.setInputData(ctrl.getButtonBit(), replayTimer);
			} else {
				// 入力状態をリプレイから読み込み
				ctrl.setButtonBit(replayData.getInputData(replayTimer));
			}
			replayTimer++;
		}

		// ボタン入力時間の更新
		ctrl.updateButtonTime();

		// 最初の処理
		if(owner.mode != null) owner.mode.onFirst(this, playerID);
		owner.receiver.onFirst(this, playerID);
		if((ai != null) && (!owner.replayMode || owner.replayRerecord)) ai.onFirst(this, playerID);

		// 各ステータスの処理
		if(!lagStop) {
			switch(stat) {
			case STAT_NOTHING:
				break;
			case STAT_SETTING:
				statSetting();
				break;
			case STAT_READY:
				statReady();
				break;
			case STAT_MOVE:
				statMove();
				break;
			case STAT_LOCKFLASH:
				statLockFlash();
				break;
			case STAT_LINECLEAR:
				statLineClear();
				break;
			case STAT_ARE:
				statARE();
				break;
			case STAT_ENDINGSTART:
				statEndingStart();
				break;
			case STAT_CUSTOM:
				statCustom();
				break;
			case STAT_EXCELLENT:
				statExcellent();
				break;
			case STAT_GAMEOVER:
				statGameOver();
				break;
			case STAT_RESULT:
				statResult();
				break;
			case STAT_FIELDEDIT:
				statFieldEdit();
				break;
			case STAT_INTERRUPTITEM:
				statInterruptItem();
				break;
			}
		}

		// フィールドのブロックの状態や統計情報を更新
		fieldUpdate();
		if((ending == 0) || (staffrollEnableStatistics)) statistics.update();

		// 最後の処理
		if(owner.mode != null) owner.mode.onLast(this, playerID);
		owner.receiver.onLast(this, playerID);
		if((ai != null) && (!owner.replayMode || owner.replayRerecord)) ai.onLast(this, playerID);

		// タイマー増加
		if(gameActive && timerActive) {
			statistics.time++;
		}
	}

	/**
	 * 画面描画
	 * （各モードやイベント処理クラスのイベントを呼び出すだけで、それ以外にGameEngine自身は何もしません）
	 */
	public void render() {
		// 最初の処理
		if(owner.mode != null) owner.mode.renderFirst(this, playerID);
		owner.receiver.renderFirst(this, playerID);

		// 各ステータスの処理
		switch(stat) {
		case STAT_NOTHING:
			break;
		case STAT_SETTING:
			if(owner.mode != null) owner.mode.renderSetting(this, playerID);
			owner.receiver.renderSetting(this, playerID);
			break;
		case STAT_READY:
			if(owner.mode != null) owner.mode.renderReady(this, playerID);
			owner.receiver.renderReady(this, playerID);
			break;
		case STAT_MOVE:
			if(owner.mode != null) owner.mode.renderMove(this, playerID);
			owner.receiver.renderMove(this, playerID);
			break;
		case STAT_LOCKFLASH:
			if(owner.mode != null) owner.mode.renderLockFlash(this, playerID);
			owner.receiver.renderLockFlash(this, playerID);
			break;
		case STAT_LINECLEAR:
			if(owner.mode != null) owner.mode.renderLineClear(this, playerID);
			owner.receiver.renderLineClear(this, playerID);
			break;
		case STAT_ARE:
			if(owner.mode != null) owner.mode.renderARE(this, playerID);
			owner.receiver.renderARE(this, playerID);
			break;
		case STAT_ENDINGSTART:
			if(owner.mode != null) owner.mode.renderEndingStart(this, playerID);
			owner.receiver.renderEndingStart(this, playerID);
			break;
		case STAT_CUSTOM:
			if(owner.mode != null) owner.mode.renderCustom(this, playerID);
			owner.receiver.renderCustom(this, playerID);
			break;
		case STAT_EXCELLENT:
			if(owner.mode != null) owner.mode.renderExcellent(this, playerID);
			owner.receiver.renderExcellent(this, playerID);
			break;
		case STAT_GAMEOVER:
			if(owner.mode != null) owner.mode.renderGameOver(this, playerID);
			owner.receiver.renderGameOver(this, playerID);
			break;
		case STAT_RESULT:
			if(owner.mode != null) owner.mode.renderResult(this, playerID);
			owner.receiver.renderResult(this, playerID);
			break;
		case STAT_FIELDEDIT:
			if(owner.mode != null) owner.mode.renderFieldEdit(this, playerID);
			owner.receiver.renderFieldEdit(this, playerID);
			break;
		case STAT_INTERRUPTITEM:
			break;
		}

		// 最後の処理
		if(owner.mode != null) owner.mode.renderLast(this, playerID);
		owner.receiver.renderLast(this, playerID);
	}

	/**
	 * 開始前の設定画面のときの処理
	 */
	public void statSetting() {
		// イベント発生
		if(owner.mode != null) {
			if(owner.mode.onSetting(this, playerID) == true) return;
		}
		owner.receiver.onSetting(this, playerID);

		// モード側が何もしない場合はReady画面へ移動
		stat = STAT_READY;
		resetStatc();
	}

	/**
	 * Ready→Goのときの処理
	 */
	public void statReady() {
		// イベント発生
		if(owner.mode != null) {
			if(owner.mode.onReady(this, playerID) == true) return;
		}
		owner.receiver.onReady(this, playerID);

		// 横溜め
		if(ruleopt.dasInReady && gameActive) padRepeat();

		// 初期化
		if(statc[0] == 0) {
			// フィールド初期化
			createFieldIfNeeded();

			// NEXTピース作成
			if(nextPieceArrayID == null) {
				// 出現可能なピースが1つもない場合は全て出現できるようにする
				boolean allDisable = true;
				for(int i = 0; i < nextPieceEnable.length; i++) {
					if(nextPieceEnable[i] == true) {
						allDisable = false;
						break;
					}
				}
				if(allDisable == true) {
					for(int i = 0; i < nextPieceEnable.length; i++) nextPieceEnable[i] = true;
				}

				// NEXTピースの出現順を作成
				if(randomizer == null) randomizer = new MemorylessRandomizer();
				nextPieceArrayID = randomizer.createPieceSequence(nextPieceEnable, random, nextPieceArraySize);
			}
			// NEXTピースのオブジェクトを作成
			if(nextPieceArrayObject == null) {
				nextPieceArrayObject = new Piece[nextPieceArrayID.length];

				for(int i = 0; i < nextPieceArrayObject.length; i++) {
					nextPieceArrayObject[i] = new Piece(nextPieceArrayID[i]);
					nextPieceArrayObject[i].direction = ruleopt.pieceDefaultDirection[nextPieceArrayObject[i].id];
					if(nextPieceArrayObject[i].direction >= Piece.DIRECTION_COUNT) {
						nextPieceArrayObject[i].direction = random.nextInt(Piece.DIRECTION_COUNT);
					}
					nextPieceArrayObject[i].setColor(ruleopt.pieceColor[nextPieceArrayObject[i].id]);
					nextPieceArrayObject[i].setSkin(getSkin());
					nextPieceArrayObject[i].updateConnectData();
					nextPieceArrayObject[i].setAttribute(Block.BLOCK_ATTRIBUTE_VISIBLE, true);
					nextPieceArrayObject[i].setAttribute(Block.BLOCK_ATTRIBUTE_BONE, bone);
				}
			}

			if(!readyDone) {
				// ボタン入力状態リセット
				ctrl.reset();
				// ゲーム中フラグON
				gameActive = true;
			}
		}

		// READY音
		if(statc[0] == readyStart) playSE("ready");

		// GO音
		if(statc[0] == goStart) playSE("go");

		// NEXTスキップ
		if((statc[0] > 0) && (statc[0] < goEnd) && (holdButtonNextSkip) && (isHoldOK()) && (ctrl.isPush(Controller.BUTTON_D))) {
			playSE("initialhold");
			holdPieceObject = getNextObjectCopy(nextPieceCount);
			holdPieceObject.applyOffsetArray(ruleopt.pieceOffsetX[holdPieceObject.id], ruleopt.pieceOffsetY[holdPieceObject.id]);
			nextPieceCount++;
			if(nextPieceCount < 0) nextPieceCount = 0;
		}

		// 開始
		if(statc[0] >= goEnd) {
			if(!readyDone) owner.bgmStatus.bgm = 0;
			if(owner.mode != null) owner.mode.startGame(this, playerID);
			owner.receiver.startGame(this, playerID);
			initialRotate();
			stat = STAT_MOVE;
			resetStatc();
			if(!readyDone) {
				startTime = System.currentTimeMillis();
			}
			readyDone = true;
			return;
		}

		statc[0]++;
	}

	/**
	 * ブロックピースの移動処理
	 */
	public void statMove() {
		// イベント発生
		if(owner.mode != null) {
			if(owner.mode.onMove(this, playerID) == true) return;
		}
		owner.receiver.onMove(this, playerID);

		// 横溜め初期化
		int moveDirection = getMoveDirection();

		if((statc[0] > 0) || (ruleopt.dasInMoveFirstFrame)) {
			if(dasDirection != moveDirection) {
				dasDirection = moveDirection;
				dasCount = 0;
			}
		}

		// 出現時の処理
		if(statc[0] == 0) {
			if((statc[1] == 0) && (initialHoldFlag == false)) {
				// 通常出現
				nowPieceObject = getNextObjectCopy(nextPieceCount);
				nextPieceCount++;
				if(nextPieceCount < 0) nextPieceCount = 0;
				holdDisable = false;
			} else {
				// ホールド出現
				if(initialHoldFlag) {
					// 先行ホールド
					if(holdPieceObject == null) {
						// 1回目
						holdPieceObject = getNextObjectCopy(nextPieceCount);
						holdPieceObject.applyOffsetArray(ruleopt.pieceOffsetX[holdPieceObject.id], ruleopt.pieceOffsetY[holdPieceObject.id]);
						nextPieceCount++;
						if(nextPieceCount < 0) nextPieceCount = 0;

						if(bone == true) getNextObject(nextPieceCount + ruleopt.nextDisplay - 1).setAttribute(Block.BLOCK_ATTRIBUTE_BONE, true);

						nowPieceObject = getNextObjectCopy(nextPieceCount);
						nextPieceCount++;
						if(nextPieceCount < 0) nextPieceCount = 0;
					} else {
						// 2回目以降
						Piece pieceTemp = holdPieceObject;
						holdPieceObject = getNextObjectCopy(nextPieceCount);
						holdPieceObject.applyOffsetArray(ruleopt.pieceOffsetX[holdPieceObject.id], ruleopt.pieceOffsetY[holdPieceObject.id]);
						nowPieceObject = pieceTemp;
						nextPieceCount++;
						if(nextPieceCount < 0) nextPieceCount = 0;
					}
				} else {
					// 通常ホールド
					if(holdPieceObject == null) {
						// 1回目
						nowPieceObject.big = false;
						holdPieceObject = nowPieceObject;
						nowPieceObject = getNextObjectCopy(nextPieceCount);
						nextPieceCount++;
						if(nextPieceCount < 0) nextPieceCount = 0;
					} else {
						// 2回目以降
						nowPieceObject.big = false;
						Piece pieceTemp = holdPieceObject;
						holdPieceObject = nowPieceObject;
						nowPieceObject = pieceTemp;
					}
				}

				// 方向を戻す
				if((ruleopt.holdResetDirection) && (ruleopt.pieceDefaultDirection[holdPieceObject.id] < Piece.DIRECTION_COUNT)) {
					holdPieceObject.direction = ruleopt.pieceDefaultDirection[holdPieceObject.id];
					holdPieceObject.updateConnectData();
				}

				// 使用した回数+1
				holdUsedCount++;
				statistics.totalHoldUsed++;

				// ホールド無効化
				initialHoldFlag = false;
				holdDisable = true;
			}
			playSE("piece" + getNextObject(nextPieceCount).id);

			if(nowPieceObject.offsetApplied == false)
				nowPieceObject.applyOffsetArray(ruleopt.pieceOffsetX[nowPieceObject.id], ruleopt.pieceOffsetY[nowPieceObject.id]);

			nowPieceObject.big = big;

			// 出現位置（横）
			nowPieceX = getSpawnPosX(field, nowPieceObject);

			// 出現位置（縦）
			nowPieceY = getSpawnPosY(nowPieceObject);

			nowPieceBottomY = nowPieceObject.getBottom(nowPieceX, nowPieceY, field);
			nowPieceColorOverride = -1;

			if(itemRollRollEnable) nowPieceColorOverride = Block.BLOCK_COLOR_GRAY;

			// 先行回転
			initialRotate();
			//if( (getARE() != 0) && ((getARELine() != 0) || (version < 6.3f)) ) initialRotate();

			if((speed.gravity > speed.denominator) && (speed.denominator > 0))
				gcount = speed.gravity % speed.denominator;
			else
				gcount = 0;

			lockDelayNow = 0;
			dasSpeedCount = 0;
			extendedMoveCount = 0;
			extendedRotateCount = 0;
			softdropFall = 0;
			harddropFall = 0;
			manualLock = false;
			nowPieceMoveCount = 0;
			nowPieceRotateCount = 0;
			nowWallkickCount = 0;
			nowUpwardWallkickCount = 0;
			lineClearing = 0;
			lastmove = LASTMOVE_NONE;
			kickused = false;
			tspin = false;
			tspinmini = false;

			getNextObject(nextPieceCount + ruleopt.nextDisplay - 1).setAttribute(Block.BLOCK_ATTRIBUTE_BONE, bone);

			if(ending == 0) timerActive = true;

			if((ai != null) && (!owner.replayMode || owner.replayRerecord)) ai.newPiece(this, playerID);
		}

		checkDropContinuousUse();

		boolean softdropUsed = false; // このフレームにソフトドロップを使ったらtrue
		int softdropFallNow = 0; // このフレームのソフトドロップで落下した段数

		boolean updown = false; // 上下同時押しフラグ
		if(ctrl.isPress(Controller.BUTTON_UP) && ctrl.isPress(Controller.BUTTON_DOWN)) updown = true;

		// ホールド
		if(ctrl.isPush(Controller.BUTTON_D) || initialHoldFlag) {
			if(isHoldOK()) {
				statc[0] = 0;
				statc[1] = 1;
				if(!initialHoldFlag) playSE("hold");
				initialHoldContinuousUse = true;
				initialHoldFlag = false;
				holdDisable = true;
				initialRotate();
				statMove();
				return;
			} else if((statc[0] > 0) && (!initialHoldFlag)) {
				playSE("holdfail");
			}
		}

		// 回転
		boolean onGroundBeforeRotate = nowPieceObject.checkCollision(nowPieceX, nowPieceY + 1, field);
		int move = 0;
		boolean rotated = false;

		if(initialRotateDirection != 0) {
			move = initialRotateDirection;
			initialRotateLastDirection = initialRotateDirection;
			initialRotateContinuousUse = true;
			playSE("initialrotate");
		} else if((statc[0] > 0) || (ruleopt.moveFirstFrame == true)) {
			if((itemRollRollEnable) && (replayTimer % itemRollRollInterval == 0)) move = 1;	// ロールロール

			// ボタン入力
			if(ctrl.isPush(Controller.BUTTON_A) || ctrl.isPush(Controller.BUTTON_C)) move = -1;
			else if(ctrl.isPush(Controller.BUTTON_B)) move = 1;
			else if(ctrl.isPush(Controller.BUTTON_E)) move = 2;

			if(move != 0) {
				initialRotateLastDirection = move;
				initialRotateContinuousUse = true;
			}
		}

		if((ruleopt.rotateButtonAllowDouble == false) && (move == 2)) move = -1;
		if((ruleopt.rotateButtonAllowReverse == false) && (move == 1)) move = -1;
		if(isRotateButtonDefaultRight() && (move != 2)) move = move * -1;

		if(move != 0) {
			// 回転後の方向を決める
			int rt = getRotateDirection(move);

			// 回転できるか判定
			if(nowPieceObject.checkCollision(nowPieceX, nowPieceY, rt, field) == false)
			{
				// 壁蹴りなしで回転できるとき
				rotated = true;
				kickused = false;
				nowPieceObject.direction = rt;
				nowPieceObject.updateConnectData();
			} else if( (ruleopt.rotateWallkick == true) &&
					   (wallkick != null) &&
					   ((initialRotateDirection == 0) || (ruleopt.rotateInitialWallkick == true)) &&
					   ((ruleopt.lockresetLimitOver != RuleOptions.LOCKRESET_LIMIT_OVER_NOWALLKICK) || (isRotateCountExceed() == false)) )
			{
				// 壁蹴りを試みる
				boolean allowUpward = (ruleopt.rotateMaxUpwardWallkick < 0) || (nowUpwardWallkickCount < ruleopt.rotateMaxUpwardWallkick);
				WallkickResult kick = wallkick.executeWallkick(nowPieceX, nowPieceY, move, nowPieceObject.direction, rt,
									  allowUpward, nowPieceObject, field, ctrl);

				if(kick != null) {
					rotated = true;
					kickused = true;
					nowWallkickCount++;
					if(kick.isUpward()) nowUpwardWallkickCount++;
					nowPieceObject.direction = kick.direction;
					nowPieceObject.updateConnectData();
					nowPieceX += kick.offsetX;
					nowPieceY += kick.offsetY;
				}
			}

			if(rotated == true) {
				// 回転成功
				nowPieceBottomY = nowPieceObject.getBottom(nowPieceX, nowPieceY, field);

				if((ruleopt.lockresetRotate == true) && (isRotateCountExceed() == false)) {
					lockDelayNow = 0;
					nowPieceObject.setDarkness(0f);
				}

				if(onGroundBeforeRotate) {
					extendedRotateCount++;
					lastmove = LASTMOVE_ROTATE_GROUND;
				} else {
					lastmove = LASTMOVE_ROTATE_AIR;
				}

				if(initialRotateDirection == 0) {
					playSE("rotate");
				}

				nowPieceRotateCount++;
				if((ending == 0) || (staffrollEnableStatistics)) statistics.totalPieceRotate++;
			} else {
				// 回転失敗
				playSE("rotfail");
			}
		}
		initialRotateDirection = 0;

		// ゲームオーバーチェック
		if((statc[0] == 0) && (nowPieceObject.checkCollision(nowPieceX, nowPieceY, field) == true)) {
			// ブロックの出現位置を上にずらすことができる場合はそうする
			for(int i = 0; i < ruleopt.pieceEnterMaxDistanceY; i++) {
				if(nowPieceObject.big) nowPieceY -= 2;
				else nowPieceY--;

				if(nowPieceObject.checkCollision(nowPieceX, nowPieceY, field) == false) {
					nowPieceBottomY = nowPieceObject.getBottom(nowPieceX, nowPieceY, field);
					break;
				}
			}

			// 死亡
			if(nowPieceObject.checkCollision(nowPieceX, nowPieceY, field) == true) {
				nowPieceObject.placeToField(nowPieceX, nowPieceY, field);
				nowPieceObject = null;
				stat = STAT_GAMEOVER;
				if((ending == 2) && (staffrollNoDeath)) stat = STAT_NOTHING;
				resetStatc();
				return;
			}
		}

		boolean sidemoveflag = false;	// このフレームに横移動したらtrue

		if((statc[0] > 0) || (ruleopt.moveFirstFrame == true)) {
			// 横移動
			boolean onGroundBeforeMove = nowPieceObject.checkCollision(nowPieceX, nowPieceY + 1, field);

			move = moveDirection;

			if (statc[0] == 0 && delayCancel) {
				if (delayCancelMoveLeft) move = -1;
				if (delayCancelMoveRight) move = 1;
				dasCount = 0;
				// delayCancel = false;
				delayCancelMoveLeft = false;
				delayCancelMoveRight = false;
			} else if (statc[0] == 1 && delayCancel && (dasCount < getDAS())) {
				move = 0;
				delayCancel = false;
			}

			if(move != 0) sidemoveflag = true;

			if(big && bigmove) move *= 2;

			if( (move != 0) && ((dasCount == 0) || (dasCount >= getDAS())) ) {
				if( (dasSpeedCount >= getDASDelay()) || (dasCount == 0) ) {
					if(dasCount > 0) dasSpeedCount = 0;

					if(nowPieceObject.checkCollision(nowPieceX + move, nowPieceY, field) == false) {
						nowPieceX += move;
						log.debug("Successful movement: move="+move);

						if((ruleopt.lockresetMove == true) && (isMoveCountExceed() == false)) {
							lockDelayNow = 0;
							nowPieceObject.setDarkness(0f);
						}

						nowPieceMoveCount++;
						if((ending == 0) || (staffrollEnableStatistics)) statistics.totalPieceMove++;
						nowPieceBottomY = nowPieceObject.getBottom(nowPieceX, nowPieceY, field);

						if(onGroundBeforeMove) {
							extendedMoveCount++;
							lastmove = LASTMOVE_SLIDE_GROUND;
						} else {
							lastmove = LASTMOVE_SLIDE_AIR;
						}

						playSE("move");
					}
				} else {
					dasSpeedCount++;
				}
			}

			// ハードドロップ
			if( (ctrl.isPress(Controller.BUTTON_UP) == true) &&
				(harddropContinuousUse == false) &&
				(ruleopt.harddropEnable == true) &&
				((ruleopt.moveDiagonal == true) || (sidemoveflag == false)) &&
				((ruleopt.moveUpAndDown == true) || (updown == false)) &&
				(nowPieceY < nowPieceBottomY) )
			{
				harddropFall += nowPieceBottomY - nowPieceY;

				if(nowPieceY != nowPieceBottomY) {
					nowPieceY = nowPieceBottomY;
					playSE("harddrop");
				}

				if(owner.mode != null) owner.mode.afterHardDropFall(this, playerID, harddropFall);
				owner.receiver.afterHardDropFall(this, playerID, harddropFall);

				lastmove = LASTMOVE_FALL_SELF;
				if(ruleopt.lockresetFall == true) {
					lockDelayNow = 0;
					nowPieceObject.setDarkness(0f);
					extendedMoveCount = 0;
					extendedRotateCount = 0;
				}
			}

			// ソフトドロップ
			if( (ctrl.isPress(Controller.BUTTON_DOWN) == true) &&
				(softdropContinuousUse == false) &&
				(ruleopt.softdropEnable == true) &&
				((ruleopt.moveDiagonal == true) || (sidemoveflag == false)) &&
				((ruleopt.moveUpAndDown == true) || (updown == false)) )
			{
				if((ruleopt.softdropMultiplyNativeSpeed == true) || (speed.denominator <= 0))
					gcount += (int)(speed.gravity * ruleopt.softdropSpeed);
				else
					gcount += (int)(speed.denominator * ruleopt.softdropSpeed);

				softdropUsed = true;
			}

			if((ending == 0) || (staffrollEnableStatistics)) statistics.totalPieceActiveTime++;
		}

		// 落下
		gcount += speed.gravity;

		while((gcount >= speed.denominator) || (speed.gravity < 0)) {
			if(nowPieceObject.checkCollision(nowPieceX, nowPieceY + 1, field) == false) {
				if(speed.gravity >= 0) gcount -= speed.denominator;
				nowPieceY++;

				if(ruleopt.lockresetFall == true) {
					lockDelayNow = 0;
					nowPieceObject.setDarkness(0f);
				}

				if((lastmove != LASTMOVE_ROTATE_GROUND) && (lastmove != LASTMOVE_SLIDE_GROUND) && (lastmove != LASTMOVE_FALL_SELF)) {
					extendedMoveCount = 0;
					extendedRotateCount = 0;
				}

				if(softdropUsed == true) {
					lastmove = LASTMOVE_FALL_SELF;
					softdropFall++;
					softdropFallNow++;
					playSE("softdrop");
				} else {
					lastmove = LASTMOVE_FALL_AUTO;
				}
			} else {
				break;
			}
		}

		if(softdropFallNow > 0) {
			if(owner.mode != null) owner.mode.afterSoftDropFall(this, playerID, softdropFallNow);
			owner.receiver.afterSoftDropFall(this, playerID, softdropFallNow);
		}

		// 接地と固定
		if( (nowPieceObject.checkCollision(nowPieceX, nowPieceY + 1, field) == true) &&
			((statc[0] > 0) || (ruleopt.moveFirstFrame == true)) )
		{
			if((lockDelayNow == 0) && (getLockDelay() > 0))
				playSE("step");

			if(lockDelayNow < getLockDelay())
				lockDelayNow++;

			if((getLockDelay() >= 99) && (lockDelayNow > 98))
				lockDelayNow = 98;

			if(lockDelayNow < getLockDelay()) {
				if(lockDelayNow >= getLockDelay() - 1)
					nowPieceObject.setDarkness(0.5f);
				else
					nowPieceObject.setDarkness((lockDelayNow * 7 / getLockDelay()) * 0.05f);
			}

			if(getLockDelay() != 0)
				gcount = speed.gravity;

			// trueになると即固定
			boolean instantlock = false;

			// ハードドロップ固定
			if( (ctrl.isPress(Controller.BUTTON_UP) == true) &&
				(harddropContinuousUse == false) &&
				(ruleopt.harddropEnable == true) &&
				((ruleopt.moveDiagonal == true) || (sidemoveflag == false)) &&
				((ruleopt.moveUpAndDown == true) || (updown == false)) &&
				(ruleopt.harddropLock == true) )
			{
				harddropContinuousUse = true;
				manualLock = true;
				instantlock = true;
			}

			// ソフトドロップ固定
			if( (ctrl.isPress(Controller.BUTTON_DOWN) == true) &&
				(softdropContinuousUse == false) &&
				(ruleopt.softdropEnable == true) &&
				((ruleopt.moveDiagonal == true) || (sidemoveflag == false)) &&
				((ruleopt.moveUpAndDown == true) || (updown == false)) &&
				(ruleopt.softdropLock == true) )
			{
				softdropContinuousUse = true;
				manualLock = true;
				instantlock = true;
			}

			// 接地状態でソフドドロップ固定
			if( (ctrl.isPush(Controller.BUTTON_DOWN) == true) &&
				(ruleopt.softdropEnable == true) &&
				((ruleopt.moveDiagonal == true) || (sidemoveflag == false)) &&
				((ruleopt.moveUpAndDown == true) || (updown == false)) &&
				(ruleopt.softdropSurfaceLock == true) )
			{
				softdropContinuousUse = true;
				manualLock = true;
				instantlock = true;
			}

			// 移動＆回転数制限超過
			if( (ruleopt.lockresetLimitOver == RuleOptions.LOCKRESET_LIMIT_OVER_INSTANT) && (isMoveCountExceed() || isRotateCountExceed()) ) {
				instantlock = true;
			}

			// 接地即固定
			if( (getLockDelay() == 0) && ((gcount >= speed.denominator) || (speed.gravity < 0)) ) {
				instantlock = true;
			}

			// 固定
			if( ((lockDelayNow >= getLockDelay()) && (getLockDelay() > 0)) || (instantlock == true) ) {
				if(ruleopt.lockflash > 0) nowPieceObject.setDarkness(-0.8f);

				if((lastmove == LASTMOVE_ROTATE_GROUND) && (tspinEnable == true)) {
					tspinmini = false;

					// T-Spin Mini判定
					if(!useAllSpinBonus) {
						if(tspinminiType == TSPINMINI_TYPE_ROTATECHECK) {
							if(nowPieceObject.checkCollision(nowPieceX, nowPieceY, getRotateDirection(-1), field) &&
							   nowPieceObject.checkCollision(nowPieceX, nowPieceY, getRotateDirection( 1), field))
								tspinmini = true;
						} else if(tspinminiType == TSPINMINI_TYPE_WALLKICKFLAG) {
							tspinmini = kickused;
						}
					}
				}

				nowPieceObject.setAttribute(Block.BLOCK_ATTRIBUTE_SELFPLACED, true);

				boolean partialLockOut = nowPieceObject.isPartialLockOut(nowPieceX, nowPieceY, field);
				boolean put = nowPieceObject.placeToField(nowPieceX, nowPieceY, field);

				// T-Spin判定
				if((lastmove == LASTMOVE_ROTATE_GROUND) && (tspinEnable == true)) {
					if(useAllSpinBonus)
						setAllSpin(nowPieceX, nowPieceY, nowPieceObject, field);
					else
						tspin = isTSpin(nowPieceX, nowPieceY, nowPieceObject, field);
				}

				playSE("lock");

				holdDisable = false;

				if((ending == 0) || (staffrollEnableStatistics)) statistics.totalPieceLocked++;

				lineClearing = field.checkLineNoFlag();
				chain = 0;

				if(lineClearing == 0) {
					combo = 0;

					if(tspin) {
						playSE("tspin0");

						if((ending == 0) || (staffrollEnableStatistics)) {
							if(tspinmini) statistics.totalTSpinZeroMini++;
							else statistics.totalTSpinZero++;
						}
					}

					if(owner.mode != null) owner.mode.calcScore(this, playerID, lineClearing);
					owner.receiver.calcScore(this, playerID, lineClearing);
				}

				if(owner.mode != null) owner.mode.pieceLocked(this, playerID, lineClearing);
				owner.receiver.pieceLocked(this, playerID, lineClearing);

				// 次の処理を決める(モード側でステータスを弄っている場合は何もしない)
				if((stat == STAT_MOVE) || (versionMajor <= 6.3f)) {
					resetStatc();

					if((ending == 1) && (versionMajor >= 6.6f) && (versionMinorOld >= 0.1f)) {
						// エンディング
						stat = STAT_ENDINGSTART;
					} else if( (!put && ruleopt.fieldLockoutDeath) || (partialLockOut && ruleopt.fieldPartialLockoutDeath) ) {
						// 画面外に置いて死亡
						stat = STAT_GAMEOVER;
						if((ending == 2) && (staffrollNoDeath)) stat = STAT_NOTHING;
					} else if( (lineClearing > 0) && ((ruleopt.lockflash <= 0) || (!ruleopt.lockflashBeforeLineClear)) ) {
						// ライン消去
						stat = STAT_LINECLEAR;
						statLineClear();
					} else if( ((getARE() > 0) || (lagARE) || (ruleopt.lockflashBeforeLineClear)) &&
							    (ruleopt.lockflash > 0) && (ruleopt.lockflashOnlyFrame) )
					{
						// AREあり（光あり）
						stat = STAT_LOCKFLASH;
					} else if((getARE() > 0) || (lagARE)) {
						// AREあり（光なし）
						statc[1] = getARE();
						stat = STAT_ARE;
					} else if(interruptItemNumber != INTERRUPTITEM_NONE) {
						// 中断効果のあるアイテム処理
						nowPieceObject = null;
						interruptItemPreviousStat = STAT_MOVE;
						stat = STAT_INTERRUPTITEM;
					} else {
						// AREなし
						stat = STAT_MOVE;
						if(ruleopt.moveFirstFrame == false) statMove();
					}
				}
				return;
			}
		}

		// 横溜め
		if((statc[0] > 0) || (ruleopt.dasInMoveFirstFrame)) {
			if( (moveDirection != 0) && (moveDirection == dasDirection) && ((dasCount < getDAS()) || (getDAS() <= 0)) ) {
				dasCount++;
			}
		}

		statc[0]++;
	}

	/**
	 * ブロック固定直後の光っているときの処理
	 */
	public void statLockFlash() {
		// イベント発生
		if(owner.mode != null) {
			if(owner.mode.onLockFlash(this, playerID) == true) return;
		}
		owner.receiver.onLockFlash(this, playerID);

		statc[0]++;

		checkDropContinuousUse();

		// 横溜め
		if(ruleopt.dasInLockFlash) padRepeat();

		// 次のステータス
		if(statc[0] >= ruleopt.lockflash) {
			resetStatc();

			if(lineClearing > 0) {
				// ライン消去
				stat = STAT_LINECLEAR;
				statLineClear();
			} else {
				// ARE
				statc[1] = getARE();
				stat = STAT_ARE;
			}
			return;
		}
	}

	/**
	 * ライン消去処理
	 */
	public void statLineClear() {
		// イベント発生
		if(owner.mode != null) {
			if(owner.mode.onLineClear(this, playerID) == true) return;
		}
		owner.receiver.onLineClear(this, playerID);

		checkDropContinuousUse();

		// 横溜め
		if(ruleopt.dasInLineClear) padRepeat();

		// 最初のフレーム
		if(statc[0] == 0) {
			// ライン消去フラグを設定
			lineClearing = field.checkLine();

			// ライン数を決める
			int li = lineClearing;
			if((big == true) && (bighalf == true)) {
				if(li == 1)
					li = 0;
				else
					li = li / 2;
			}
			if(li > 4) li = 4;

			if(tspin) {
				playSE("tspin" + li);

				if((ending == 0) || (staffrollEnableStatistics)) {
					if((li == 1) && (tspinmini))  statistics.totalTSpinSingleMini++;
					if((li == 1) && (!tspinmini)) statistics.totalTSpinSingle++;
					if((li == 2) && (tspinmini))  statistics.totalTSpinDoubleMini++;
					if((li == 2) && (!tspinmini)) statistics.totalTSpinDouble++;
					if(li == 3) statistics.totalTSpinTriple++;
				}
			} else {
				playSE("erase" + li);

				if((ending == 0) || (staffrollEnableStatistics)) {
					if(li == 1) statistics.totalSingle++;
					if(li == 2) statistics.totalDouble++;
					if(li == 3) statistics.totalTriple++;
					if(li == 4) statistics.totalFour++;
				}
			}

			// B2Bボーナス
			if(b2bEnable) {
				if((tspin) || (li >= 4)) {
					b2bcount++;

					if(b2bcount == 1) {
						playSE("b2b_start");
					} else {
						b2b = true;
						playSE("b2b_continue");

						if((ending == 0) || (staffrollEnableStatistics)) {
							if(li == 4) statistics.totalB2BFour++;
							else statistics.totalB2BTSpin++;
						}
					}
				} else if(b2bcount != 0) {
					b2b = false;
					b2bcount = 0;
					playSE("b2b_end");
				}
			}

			// コンボ
			if((comboType != COMBO_TYPE_DISABLE) && (chain == 0)) {
				if( (comboType == COMBO_TYPE_NORMAL) || ((comboType == COMBO_TYPE_DOUBLE) && (li >= 2)) )
					combo++;

				if(combo >= 2) {
					int cmbse = combo - 1;
					if(cmbse > 20) cmbse = 20;
					playSE("combo" + cmbse);
				}

				if((ending == 0) || (staffrollEnableStatistics)) {
					if(combo > statistics.maxCombo) statistics.maxCombo = combo;
				}
			}

			if((ending == 0) || (staffrollEnableStatistics)) statistics.lines += li;

			if(field.getHowManyGemClears() > 0) playSE("gem");

			// スコア計算
			if(owner.mode != null) owner.mode.calcScore(this, playerID, li);
			owner.receiver.calcScore(this, playerID, li);

			// ブロックを消す演出を出す（まだ実際には消えていない）
			for(int i = 0; i < field.getHeight(); i++) {
				if(field.getLineFlag(i)) {
					for(int j = 0; j < field.getWidth(); j++) {
						Block blk = field.getBlock(j, i);

						if(blk != null) {
							if(owner.mode != null) owner.mode.blockBreak(this, playerID, j, i, blk);
							owner.receiver.blockBreak(this, playerID, j, i, blk);
						}
					}
				}
			}

			// ブロックを消す
			field.clearLine();
		}

		// ラインを1段落とす
		if((lineGravityType == LINE_GRAVITY_NATIVE) &&
		   (getLineDelay() >= (lineClearing - 1)) && (statc[0] >= getLineDelay() - (lineClearing - 1)) && (ruleopt.lineFallAnim))
		{
			field.downFloatingBlocksSingleLine();
		}

		// Line delay cancel check
		delayCancelMoveLeft = ctrl.isPush(Controller.BUTTON_LEFT);
		delayCancelMoveRight = ctrl.isPush(Controller.BUTTON_RIGHT);

		delayCancel = ctrl.isPush(Controller.BUTTON_UP) || ctrl.isPush(Controller.BUTTON_DOWN) ||
			delayCancelMoveLeft || delayCancelMoveRight || ctrl.isPush(Controller.BUTTON_A) ||
			ctrl.isPush(Controller.BUTTON_B) || ctrl.isPush(Controller.BUTTON_C) ||
			(ruleopt.holdEnable && ruleopt.holdInitial && ctrl.isPush(Controller.BUTTON_D));

		if( (ruleopt.lineCancel) && (statc[0] < getLineDelay()) && delayCancel ) {
			statc[0] = getLineDelay();
		}

		// 次のステータス
		if(statc[0] >= getLineDelay()) {
			// Cascade
			if(lineGravityType == LINE_GRAVITY_CASCADE) {
				if(field.doCascadeGravity()) {
					return;
				} else if(field.checkLineNoFlag() > 0) {
					tspin = false;
					tspinmini = false;
					chain++;
					if(chain > statistics.maxChain) statistics.maxChain = chain;
					statc[0] = 0;
					return;
				}
			}

			boolean skip = false;
			if(owner.mode != null) skip = owner.mode.lineClearEnd(this, playerID);
			owner.receiver.lineClearEnd(this, playerID);

			if(!skip) {
				if(lineGravityType == LINE_GRAVITY_NATIVE) field.downFloatingBlocks();
				playSE("linefall");

				if((stat == STAT_LINECLEAR) || (versionMajor <= 6.3f)) {
					resetStatc();
					if(ending == 1) {
						// エンディング
						stat = STAT_ENDINGSTART;
					} else if((getARELine() > 0) || (lagARE)) {
						// AREあり
						statc[0] = 0;
						statc[1] = getARELine();
						statc[2] = 1;
						stat = STAT_ARE;
					} else if(interruptItemNumber != INTERRUPTITEM_NONE) {
						// 中断効果のあるアイテム処理
						nowPieceObject = null;
						interruptItemPreviousStat = STAT_MOVE;
						stat = STAT_INTERRUPTITEM;
					} else {
						// AREなし
						nowPieceObject = null;
						initialRotate();
						stat = STAT_MOVE;
					}
				}
			}

			return;
		}

		statc[0]++;
	}

	/**
	 * ARE中の処理
	 */
	public void statARE() {
		// イベント発生
		if(owner.mode != null) {
			if(owner.mode.onARE(this, playerID) == true) return;
		}
		owner.receiver.onARE(this, playerID);

		statc[0]++;

		checkDropContinuousUse();

		// ARE cancel check
		delayCancelMoveLeft = ctrl.isPush(Controller.BUTTON_LEFT);
		delayCancelMoveRight = ctrl.isPush(Controller.BUTTON_RIGHT);

		delayCancel = ctrl.isPush(Controller.BUTTON_UP) || ctrl.isPush(Controller.BUTTON_DOWN) ||
			delayCancelMoveLeft || delayCancelMoveRight || ctrl.isPush(Controller.BUTTON_A) ||
			ctrl.isPush(Controller.BUTTON_B) || ctrl.isPush(Controller.BUTTON_C) ||
			(ruleopt.holdEnable && ruleopt.holdInitial && ctrl.isPush(Controller.BUTTON_D));

		if( (ruleopt.areCancel) && (statc[0] < statc[1]) && delayCancel ) {
			statc[0] = statc[1];
		}

		// 横溜め
		if( (ruleopt.dasInARE) && ((statc[0] < statc[1] - 1) || (ruleopt.dasInARELastFrame)) )
			padRepeat();

		// 次のステータス
		if((statc[0] >= statc[1]) && (!lagARE)) {
			nowPieceObject = null;
			resetStatc();

			if(interruptItemNumber != INTERRUPTITEM_NONE) {
				// 中断効果のあるアイテム処理
				interruptItemPreviousStat = STAT_MOVE;
				stat = STAT_INTERRUPTITEM;
			} else {
				// ブロックピース移動処理
				initialRotate();
				stat = STAT_MOVE;
			}
		}
	}

	/**
	 * エンディング突入処理
	 */
	public void statEndingStart() {
		// イベント発生
		if(owner.mode != null) {
			if(owner.mode.onEndingStart(this, playerID) == true) return;
		}
		owner.receiver.onEndingStart(this, playerID);

		checkDropContinuousUse();

		// 横溜め
		if(ruleopt.dasInEndingStart) padRepeat();

		if(statc[2] == 0) {
			timerActive = false;
			owner.bgmStatus.bgm = BGMStatus.BGM_NOTHING;
			playSE("endingstart");
			statc[2] = 1;
		}

		if(statc[0] < getLineDelay()) {
			statc[0]++;
		} else if(statc[1] < field.getHeight() * 6) {
			if(statc[1] % 6 == 0) {
				int y = field.getHeight() - (statc[1] / 6);
				field.setLineFlag(y, true);

				for(int i = 0; i < field.getWidth(); i++) {
					Block blk = field.getBlock(i, y);

					if((blk != null) && (blk.color != Block.BLOCK_COLOR_NONE)) {
						if(owner.mode != null) owner.mode.blockBreak(this, playerID, i, y, blk);
						owner.receiver.blockBreak(this, playerID, i, y, blk);
						field.setBlockColor(i, y, Block.BLOCK_COLOR_NONE);
					}
				}
			}

			statc[1]++;
		} else if(statc[0] < getLineDelay() + 2) {
			statc[0]++;
		} else {
			ending = 2;
			field.reset();
			resetStatc();

			if(staffrollEnable) {
				nowPieceObject = null;
				stat = STAT_MOVE;
			} else {
				stat = STAT_EXCELLENT;
			}
		}
	}

	/**
	 * 各ゲームモードが自由に使えるステータスの処理
	 */
	public void statCustom() {
		// イベント発生
		if(owner.mode != null) {
			if(owner.mode.onCustom(this, playerID) == true) return;
		}
		owner.receiver.onCustom(this, playerID);
	}

	/**
	 * エンディング画面
	 */
	public void statExcellent() {
		// イベント発生
		if(owner.mode != null) {
			if(owner.mode.onExcellent(this, playerID) == true) return;
		}
		owner.receiver.onExcellent(this, playerID);

		if(statc[0] == 0) {
			gameActive = false;
			timerActive = false;
			owner.bgmStatus.fadesw = true;
			if(ai != null) ai.shutdown(this, playerID);

			resetFieldVisible();

			playSE("excellent");
		}

		if((statc[0] >= 120) && (ctrl.isPush(Controller.BUTTON_A))) {
			statc[0] = 600;
		}

		if((statc[0] >= 600) && (statc[1] == 0)) {
			resetStatc();
			stat = STAT_GAMEOVER;
		} else {
			statc[0]++;
		}
	}

	/**
	 * ゲームオーバーの処理
	 */
	public void statGameOver() {
		// イベント発生
		if(owner.mode != null) {
			if(owner.mode.onGameOver(this, playerID) == true) return;
		}
		owner.receiver.onGameOver(this, playerID);

		if(lives <= 0) {
			// もう復活できないとき
			if(statc[0] == 0) {
				endTime = System.currentTimeMillis();
				statistics.gamerate = (float)(replayTimer / (.06*(endTime - startTime)));

				gameActive = false;
				timerActive = false;
				blockShowOutlineOnly = false;
				if(owner.getPlayers() < 2) owner.bgmStatus.bgm = BGMStatus.BGM_NOTHING;
				if(ai != null) ai.shutdown(this, playerID);

				if(field.isEmpty()) {
					statc[0] = field.getHeight() + 1;
				} else {
					resetFieldVisible();
				}
			}

			if(statc[0] < field.getHeight() + 1) {
				for(int i = 0; i < field.getWidth(); i++) {
					if(field.getBlockColor(i, field.getHeight() - statc[0]) != Block.BLOCK_COLOR_NONE) {
						Block blk = field.getBlock(i, field.getHeight() - statc[0]);

						if(blk != null) {
							if(!blk.getAttribute(Block.BLOCK_ATTRIBUTE_GARBAGE)) {
								blk.color = Block.BLOCK_COLOR_GRAY;
								blk.setAttribute(Block.BLOCK_ATTRIBUTE_GARBAGE, true);
							}
							blk.darkness = 0.3f;
							blk.elapsedFrames = -1;
						}
					}
				}
				statc[0]++;
			} else if(statc[0] == field.getHeight() + 1) {
				playSE("gameover");
				statc[0]++;
			} else if(statc[0] < field.getHeight() + 1 + 180) {
				if((statc[0] >= field.getHeight() + 1 + 60) && (ctrl.isPush(Controller.BUTTON_A))) {
					statc[0] = field.getHeight() + 1 + 180;
				}

				statc[0]++;
			} else {
				if(!owner.replayMode || owner.replayRerecord) owner.saveReplay();

				for(int i = 0; i < owner.getPlayers(); i++) {
					if((i == playerID) || (gameoverAll)) {
						if(owner.engine[i].field != null) {
							owner.engine[i].field.reset();
						}
						owner.engine[i].resetStatc();
						owner.engine[i].stat = STAT_RESULT;
					}
				}
			}
		} else {
			// 復活できるとき
			if(statc[0] == 0) {
				blockShowOutlineOnly = false;
				playSE("died");

				resetFieldVisible();

				for(int i = (field.getHiddenHeight() * -1); i < field.getHeight(); i++) {
					for(int j = 0; j < field.getWidth(); j++) {
						if(field.getBlockColor(j, i) != Block.BLOCK_COLOR_NONE) {
							field.setBlockColor(j, i, Block.BLOCK_COLOR_GRAY);
						}
					}
				}

				statc[0] = 1;
			}

			if(!field.isEmpty()) {
				field.pushDown();
			} else if(statc[1] < getARE()) {
				statc[1]++;
			} else {
				lives--;
				resetStatc();
				stat = STAT_MOVE;
			}
		}
	}

	/**
	 * 結果画面
	 */
	public void statResult() {
		// イベント発生
		if(owner.mode != null) {
			if(owner.mode.onResult(this, playerID) == true) return;
		}
		owner.receiver.onResult(this, playerID);

		// カーソル移動
		if(ctrl.isMenuRepeatKey(Controller.BUTTON_LEFT) || ctrl.isMenuRepeatKey(Controller.BUTTON_RIGHT)) {
			if(statc[0] == 0) statc[0] = 1;
			else statc[0] = 0;
			playSE("cursor");
		}

		// 決定
		if(ctrl.isPush(Controller.BUTTON_A)) {
			playSE("decide");

			if(statc[0] == 0) {
				owner.reset();
			} else {
				quitflag = true;
			}
		}
	}

	/**
	 * フィールドエディット画面
	 */
	public void statFieldEdit() {
		// イベント発生
		if(owner.mode != null) {
			if(owner.mode.onFieldEdit(this, playerID) == true) return;
		}
		owner.receiver.onFieldEdit(this, playerID);

		fldeditFrames++;

		// カーソル移動
		if(ctrl.isMenuRepeatKey(Controller.BUTTON_LEFT, false) && !ctrl.isPress(Controller.BUTTON_C)) {
			playSE("move");
			fldeditX--;
			if(fldeditX < 0) fldeditX = fieldWidth - 1;
		}
		if(ctrl.isMenuRepeatKey(Controller.BUTTON_RIGHT, false) && !ctrl.isPress(Controller.BUTTON_C)) {
			playSE("move");
			fldeditX++;
			if(fldeditX > fieldWidth - 1) fldeditX = 0;
		}
		if(ctrl.isMenuRepeatKey(Controller.BUTTON_UP, false)) {
			playSE("move");
			fldeditY--;
			if(fldeditY < 0) fldeditY = fieldHeight - 1;
		}
		if(ctrl.isMenuRepeatKey(Controller.BUTTON_DOWN, false)) {
			playSE("move");
			fldeditY++;
			if(fldeditY > fieldHeight - 1) fldeditY = 0;
		}

		// 色選択
		if(ctrl.isMenuRepeatKey(Controller.BUTTON_LEFT, false) && ctrl.isPress(Controller.BUTTON_C)) {
			playSE("cursor");
			fldeditColor--;
			if(fldeditColor < Block.BLOCK_COLOR_GRAY) fldeditColor = Block.BLOCK_COLOR_GEM_PURPLE;
		}
		if(ctrl.isMenuRepeatKey(Controller.BUTTON_RIGHT, false) && ctrl.isPress(Controller.BUTTON_C)) {
			playSE("cursor");
			fldeditColor++;
			if(fldeditColor > Block.BLOCK_COLOR_GEM_PURPLE) fldeditColor = Block.BLOCK_COLOR_GRAY;
		}

		// 配置
		if(ctrl.isPress(Controller.BUTTON_A) && (fldeditFrames > 10)) {
			try {
				if(field.getBlockColorE(fldeditX, fldeditY) != fldeditColor) {
					Block blk = new Block(fldeditColor, getSkin(), Block.BLOCK_ATTRIBUTE_VISIBLE | Block.BLOCK_ATTRIBUTE_OUTLINE);
					field.setBlockE(fldeditX, fldeditY, blk);
					playSE("change");
				}
			} catch (Exception e) {}
		}

		// 消去
		if(ctrl.isPress(Controller.BUTTON_D) && (fldeditFrames > 10)) {
			try {
				if(!field.getBlockEmptyE(fldeditX, fldeditY)) {
					field.setBlockColorE(fldeditX, fldeditY, Block.BLOCK_COLOR_NONE);
					playSE("change");
				}
			} catch (Exception e) {}
		}

		// 終了
		if(ctrl.isPush(Controller.BUTTON_B) && (fldeditFrames > 10)) {
			stat = fldeditPreviousStat;
			if(owner.mode != null) owner.mode.fieldEditExit(this, playerID);
			owner.receiver.fieldEditExit(this, playerID);
		}
	}

	/**
	 * プレイ中断効果のあるアイテム処理
	 */
	public void statInterruptItem() {
		boolean contFlag = false;	// 続行フラグ

		switch(interruptItemNumber) {
		case INTERRUPTITEM_MIRROR:	// ミラー
			contFlag = interruptItemMirrorProc();
			break;
		}

		if(!contFlag) {
			interruptItemNumber = INTERRUPTITEM_NONE;
			resetStatc();
			stat = interruptItemPreviousStat;
		}
	}

	/**
	 * ミラー処理
	 * @return trueならミラー処理続行
	 */
	public boolean interruptItemMirrorProc() {
		if(statc[0] == 0) {
			// フィールドをバックアップにコピー
			interruptItemMirrorField = new Field(field);
			// フィールドのブロックを全部消す
			field.reset();
		} else if((statc[0] >= 21) && (statc[0] < 21 + (field.getWidth() * 2)) && (statc[0] % 2 == 0)) {
			// 反転
			int x = ((statc[0] - 20) / 2) - 1;

			for(int y = (field.getHiddenHeight() * -1); y < field.getHeight(); y++) {
				field.setBlock(field.getWidth() - x - 1, y, interruptItemMirrorField.getBlock(x, y));
			}
		} else if(statc[0] < 21 + (field.getWidth() * 2) + 5) {
			// 待ち時間
		} else {
			// 終了
			statc[0] = 0;
			interruptItemMirrorField = null;
			return false;
		}

		statc[0]++;
		return true;
	}
}
