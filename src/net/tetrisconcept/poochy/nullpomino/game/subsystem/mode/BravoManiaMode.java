package net.tetrisconcept.poochy.nullpomino.game.subsystem.mode;

//import org.game_host.hebo.nullpomino.game.component.Block;
//import org.apache.log4j.Logger;
import org.game_host.hebo.nullpomino.game.component.Controller;
import org.game_host.hebo.nullpomino.game.component.Field;
import org.game_host.hebo.nullpomino.game.component.Piece;
import org.game_host.hebo.nullpomino.game.event.EventReceiver;
import org.game_host.hebo.nullpomino.game.play.GameEngine;
import org.game_host.hebo.nullpomino.game.play.GameManager;
import org.game_host.hebo.nullpomino.game.subsystem.mode.DummyMode;
import org.game_host.hebo.nullpomino.util.CustomProperties;
import org.game_host.hebo.nullpomino.util.GeneralUtil;

/**
 * BRAVO MANIA Mode (Beta)
 * @author Poochy.EXE
 */
public class BravoManiaMode extends DummyMode {
	/** Enabled piece types */
	private static final int[] PIECE_ENABLE = {1, 1, 1, 0, 1, 1, 0, 0, 0, 0, 0};

	/** Lists of 1-piece bravo patterns */
	private static final String[] BRAVO_PATTERNS_I = {
		"11000000001100000000",
		"00000000110000000011",
		"11111111001111111100111111110011111111001111111100111111110011111111001111111100",
		"11111100111111110011111111001111111100111111110011111111001111111100111111110011",
		"11110011111111001111111100111111110011111111001111111100111111110011111111001111",
		"11001111111100111111110011111111001111111100111111110011111111001111111100111111",
		"00111111110011111111001111111100111111110011111111001111111100111111110011111111"
	};
	private static final String[] BRAVO_PATTERNS_L = {
		"111111000011111100001111111100111111110011111111001111111100",
		"111100001111110000111111110011111111001111111100111111110011",
		"110000111111000011111111001111111100111111110011111111001111",
		"000011111100001111111100111111110011111111001111111100111111",
		"1111111100111111110011110000001111000000",
		"1111110011111111001111000000111100000011",
		"1111001111111100111100000011110000001111",
		"1111000000111100000011110011111111001111",
		"1100000011110000001111001111111100111111",
		"0000001111000000111100111111110011111111"
	};
	private static final String[] BRAVO_PATTERNS_O = {
		"1111110000111111000011111100001111110000",
		"1111000011111100001111110000111111000011",
		"1100001111110000111111000011111100001111",
		"0000111111000011111100001111110000111111"
	};
	private static final String[] BRAVO_PATTERNS_T = {
		"1111000000111100000011111100111111110011",
		"1100000011110000001111110011111111001111",
		"0000001111000000111111001111111100111111"
	};
	private static final String[] BRAVO_PATTERNS_J = {
		"111111000011111100001111110011111111001111111100111111110011",
		"111100001111110000111111001111111100111111110011111111001111",
		"110000111111000011111100111111110011111111001111111100111111",
		"000011111100001111110011111111001111111100111111110011111111",
		"1111001111111100111111110000001111000000",
		"1100111111110011111111000000111100000011",
		"0011111111001111111100000011110000001111",
		"1111000000111100000011111111001111111100",
		"1100000011110000001111111100111111110011",
		"0000001111000000111111110011111111001111"
	};
	
	/** 現在のバージョン */
	private static final int CURRENT_VERSION = 3;

	/** 落下速度テーブル */
	private static final int[] tableGravityValue =
	{
		4, 32, 64, 96, 128, 160, 192, 224, 256, 512, 768, 1024, 768, -1
	};

	/** 落下速度が変わるレベル */
	private static final int[] tableGravityChangeLevel =
	{
		20, 30, 33, 36, 39, 43, 47, 51, 100, 130, 160, 250, 300, 10000
	};

	/** BGMがフェードアウトするレベル */
	//private static final int[] tableBGMFadeout = {495,695,880,-1};
	private static final int[] tableBGMFadeout = {980,-1};

	/** BGMが変わるレベル */
	//private static final int[] tableBGMChange  = {500,700,900,-1};
	private static final int[] tableBGMChange  = {1000,-1};

	/** LV999ロールの時間 */
	private static final int ROLLTIMELIMIT = 2024;

	/** ランキングに記録する数 */
	private static final int RANKING_MAX = 10;

	/** 最大セクション数 */
	private static final int SECTION_MAX = 20;

	/** デフォルトのセクションタイム */
	//private static final int DEFAULT_SECTION_TIME = 5400;

	/** このモードを所有するGameManager */
	private GameManager owner;

	/** 描画などのイベント処理 */
	private EventReceiver receiver;

	/** 現在の落下速度の番号（tableGravityChangeLevelのレベルに到達するたびに1つ増える） */
	private int gravityindex;

	/** 次のセクションのレベル（これ-1のときにレベルストップする） */
	private int nextseclv;

	/** レベルが増えたフラグ */
	private boolean lvupflag;

	/** 直前に手に入れたスコア */
	private int lastscore;

	/** 獲得スコア表示がされる残り時間 */
	private int scgettime;

	/** ロール経過時間 */
	private int rolltime;

	/** 現在のBGM */
	private int bgmlv;

	/** セクションタイム */
	private int[] sectiontime;

	/** 新記録が出たセクションはtrue */
	//private boolean[] sectionIsNewRecord;

	/** どこかのセクションで新記録を出すとtrue */
	//private boolean sectionAnyNewRecord;

	/** クリアしたセクション数 */
	private int sectionscomp;

	/** 平均セクションタイム */
	private int sectionavgtime;

	/** セクションタイム記録表示中ならtrue */
	//private boolean isShowBestSectionTime;

	/** 開始時のレベル */
	private int startlevel;

	/** trueなら常にゴーストON */
	private boolean alwaysghost;

	/** trueなら常に20G */
	private boolean always20g;

	/** trueならレベルストップ音有効 */
	private boolean lvstopse;
	
	/** trueならセクションタイム表示有効 */
	private boolean showsectiontime;

	/** バージョン */
	private int version;

	/** 今回のプレイのランキングでのランク */
	private int rankingRank;

	/** Ranking scores */
	private int[] rankingScore;
	
	/** ランキングのレベル */
	private int[] rankingLevel;

	/** ランキングのタイム */
	private int[] rankingTime;

	/** セクションタイム記録 */
	//private int[] bestSectionTime;
	
	private int timelimitTimer;
	private int TIME_LIMIT = 10800;
	private int timeextendDisp;
	private int timeextendSeconds;
	private int scoreflash;
	
	/** Flag to show alert */
	private boolean bravoAlert;
	
	/*
	 * モード名
	 */
	@Override
	public String getName() {
		return "BRAVO MANIA (BETA)";
	}

	/*
	 * 初期化
	 */
	@Override
	public void playerInit(GameEngine engine, int playerID) {
		owner = engine.owner;
		receiver = engine.owner.receiver;

		gravityindex = 0;
		nextseclv = 0;
		lvupflag = true;
		lastscore = 0;
		scgettime = 0;
		rolltime = 0;
		bgmlv = 0;
		sectiontime = new int[SECTION_MAX];
		//sectionIsNewRecord = new boolean[SECTION_MAX];
		//sectionAnyNewRecord = false;
		sectionscomp = 0;
		sectionavgtime = 0;
		//isShowBestSectionTime = false;
		startlevel = 0;
		alwaysghost = false;
		always20g = false;
		lvstopse = true;
		//big = true;
		timelimitTimer = TIME_LIMIT;
		timeextendDisp = 0;
		timeextendSeconds = 0;
		scoreflash = 0;
		bravoAlert = false;

		rankingRank = -1;
		rankingScore = new int[RANKING_MAX];
		rankingLevel = new int[RANKING_MAX];
		rankingTime = new int[RANKING_MAX];
		//bestSectionTime = new int[SECTION_MAX];

		engine.speed.are = 23;
		engine.speed.areLine = 23;
		engine.speed.lineDelay = 40;
		engine.speed.lockDelay = 31;
		engine.speed.das = 15;

		engine.tspinEnable = false;
		engine.b2bEnable = false;
		engine.framecolor = GameEngine.FRAME_COLOR_PINK;
		engine.comboType = GameEngine.COMBO_TYPE_DISABLE;
		engine.staffrollEnable = false;

		if(owner.replayMode == false) {
			loadSetting(owner.modeConfig);
			loadRanking(owner.modeConfig, engine.ruleopt.strRuleName);
			version = CURRENT_VERSION;
		} else {
			loadSetting(owner.replayProp);
			version = owner.replayProp.getProperty("bravomania.version", 0);
		}

		owner.backgroundStatus.bg = startlevel;
	}

	/**
	 * プロパティファイルから設定を読み込み
	 * @param prop プロパティファイル
	 */
	private void loadSetting(CustomProperties prop) {
		startlevel = prop.getProperty("bravomania.startlevel", 0);
		alwaysghost = prop.getProperty("bravomania.alwaysghost", false);
		always20g = prop.getProperty("bravomania.always20g", false);
		lvstopse = prop.getProperty("bravomania.lvstopse", true);
		showsectiontime = prop.getProperty("bravomania.showsectiontime", false);
	}

	/**
	 * プロパティファイルに設定を保存
	 * @param prop プロパティファイル
	 */
	private void saveSetting(CustomProperties prop) {
		prop.setProperty("bravomania.startlevel", startlevel);
		prop.setProperty("bravomania.alwaysghost", alwaysghost);
		prop.setProperty("bravomania.always20g", always20g);
		prop.setProperty("bravomania.lvstopse", lvstopse);
		prop.setProperty("bravomania.showsectiontime", showsectiontime);
	}

	/**
	 * ゲーム開始時のBGMを設定
	 * @param engine GameEngine
	 */
	private void setStartBgmlv(GameEngine engine) {
		bgmlv = 0;
		while((tableBGMChange[bgmlv] != -1) && (engine.statistics.level >= tableBGMChange[bgmlv])) bgmlv++;
	}

	/**
	 * 落下速度を更新
	 * @param engine GameEngine
	 */
	private void setSpeed(GameEngine engine) {
		if(always20g == true) {
			engine.speed.gravity = -1;
		} else {
			while(engine.statistics.level >= tableGravityChangeLevel[gravityindex]) gravityindex++;
			engine.speed.gravity = tableGravityValue[gravityindex];
		}
	}

	/**
	 * 平均セクションタイムを更新
	 */
	private void setAverageSectionTime() {
		if(sectionscomp > 0) {
			int temp = 0;
			for(int i = startlevel; i < startlevel + sectionscomp; i++) {
				if((i >= 0) && (i < sectiontime.length)) temp += sectiontime[i];
			}
			sectionavgtime = temp / sectionscomp;
		} else {
			sectionavgtime = 0;
		}
	}

	/**
	 * セクションタイム更新処理
	 * @param sectionNumber セクション番号
	 */
	/*
	private void stNewRecordCheck(int sectionNumber) {
		if((sectiontime[sectionNumber] < bestSectionTime[sectionNumber]) && (!owner.replayMode)) {
			sectionIsNewRecord[sectionNumber] = true;
			sectionAnyNewRecord = true;
		}
	}
	*/

	/**
	 * 設定画面の処理
	 */
	@Override
	public boolean onSetting(GameEngine engine, int playerID) {
		// メニュー
		if(engine.owner.replayMode == false) {
			// 上
			if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_UP)) {
				engine.statc[2]--;
				if(engine.statc[2] < 0) engine.statc[2] = 4;
				engine.playSE("cursor");
			}
			// 下
			if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_DOWN)) {
				engine.statc[2]++;
				if(engine.statc[2] > 4) engine.statc[2] = 0;
				engine.playSE("cursor");
			}

			// 設定変更
			int change = 0;
			if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_LEFT)) change = -1;
			if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_RIGHT)) change = 1;

			if(change != 0) {
				engine.playSE("change");

				switch(engine.statc[2]) {
				case 0:
					startlevel += change;
					if(startlevel < 0) startlevel = 9;
					if(startlevel > 9) startlevel = 0;
					owner.backgroundStatus.bg = startlevel;
					break;
				case 1:
					alwaysghost = !alwaysghost;
					break;
				case 2:
					always20g = !always20g;
					break;
				case 3:
					lvstopse = !lvstopse;
					break;
				case 4:
					showsectiontime = !showsectiontime;
					break;
				}
			}

			// セクションタイム表示切替
			if(engine.ctrl.isPush(Controller.BUTTON_F) && (engine.statc[3] >= 5)) {
				engine.playSE("change");
				//isShowBestSectionTime = !isShowBestSectionTime;
			}

			// 決定
			if(engine.ctrl.isPush(Controller.BUTTON_A) && (engine.statc[3] >= 5)) {
				engine.playSE("decide");
				saveSetting(owner.modeConfig);
				receiver.saveModeConfig(owner.modeConfig);
				//isShowBestSectionTime = false;
				//sectionscomp = 0;
				return false;
			}

			// キャンセル
			if(engine.ctrl.isPush(Controller.BUTTON_B)) {
				engine.quitflag = true;
			}

			engine.statc[3]++;
		} else {
			engine.statc[3]++;
			engine.statc[2] = -1;

			if(engine.statc[3] >= 60) {
				return false;
			}
		}

		return true;
	}

	/*
	 * 設定画面の描画処理
	 */
	@Override
	public void renderSetting(GameEngine engine, int playerID) {
		if(engine.owner.replayMode == false) {
			receiver.drawMenuFont(engine, playerID, 0, (engine.statc[2] * 2) + 1, "b", EventReceiver.COLOR_RED);
		}

		receiver.drawMenuFont(engine, playerID, 0, 0, "LEVEL", EventReceiver.COLOR_PINK);
		receiver.drawMenuFont(engine, playerID, 1, 1, String.valueOf(startlevel * 100), (engine.statc[2] == 0));
		receiver.drawMenuFont(engine, playerID, 0, 2, "FULL GHOST", EventReceiver.COLOR_PINK);
		receiver.drawMenuFont(engine, playerID, 1, 3, GeneralUtil.getONorOFF(alwaysghost), (engine.statc[2] == 1));
		receiver.drawMenuFont(engine, playerID, 0, 4, "20G MODE", EventReceiver.COLOR_PINK);
		receiver.drawMenuFont(engine, playerID, 1, 5, GeneralUtil.getONorOFF(always20g), (engine.statc[2] == 2));
		receiver.drawMenuFont(engine, playerID, 0, 6, "LVSTOPSE", EventReceiver.COLOR_PINK);
		receiver.drawMenuFont(engine, playerID, 1, 7, GeneralUtil.getONorOFF(lvstopse), (engine.statc[2] == 3));
		receiver.drawMenuFont(engine, playerID, 0, 8, "SHOW STIME", EventReceiver.COLOR_PINK);
		receiver.drawMenuFont(engine, playerID, 1, 9, GeneralUtil.getONorOFF(showsectiontime), (engine.statc[2] == 4));
	}

	/*
	 * ゲーム開始時の処理
	 */
	@Override
	public void startGame(GameEngine engine, int playerID) {
		engine.statistics.level = startlevel * 100;

		nextseclv = engine.statistics.level + 100;
		if(engine.statistics.level < 0) nextseclv = 100;
		if(engine.statistics.level >= 900) nextseclv = 999;

		owner.backgroundStatus.bg = engine.statistics.level / 100;

		engine.big = true;

		setSpeed(engine);
		setStartBgmlv(engine);
		owner.bgmStatus.bgm = bgmlv;
	}

	/*
	 * スコア表示
	 */
	@Override
	public void renderLast(GameEngine engine, int playerID) {
		receiver.drawScoreFont(engine, playerID, 0, 0, "BRAVO MANIA (BETA)", EventReceiver.COLOR_PINK);

		if( (engine.stat == GameEngine.STAT_SETTING) || ((engine.stat == GameEngine.STAT_RESULT) && (owner.replayMode == false)) ) {
			if((owner.replayMode == false) && (startlevel == 0) && (always20g == false) && (engine.ai == null)) {
				//if(!isShowBestSectionTime) {
					// ランキング
					receiver.drawScoreFont(engine, playerID, 3, 2, "SCORE LEVEL TIME", EventReceiver.COLOR_PINK);

					for(int i = 0; i < RANKING_MAX; i++) {
						receiver.drawScoreFont(engine, playerID, 0, 3 + i, String.format("%2d", i + 1), EventReceiver.COLOR_YELLOW);
						receiver.drawScoreFont(engine, playerID, 3, 3 + i, String.valueOf(rankingScore[i]), (i == rankingRank));
						receiver.drawScoreFont(engine, playerID, 9, 3 + i, String.valueOf(rankingLevel[i]), (i == rankingRank));
						receiver.drawScoreFont(engine, playerID, 15, 3 + i, GeneralUtil.getTime(rankingTime[i]), (i == rankingRank));
					}

					//receiver.drawScoreFont(engine, playerID, 0, 17, "F:VIEW SECTION TIME", EventReceiver.COLOR_GREEN);
				/*} else {
					// セクションタイム
					receiver.drawScoreFont(engine, playerID, 0, 2, "SECTION TIME", EventReceiver.COLOR_PINK);

					int totalTime = 0;
					for(int i = 0; i < SECTION_MAX; i++) {
						int temp = Math.min(i * 100, 999);
						int temp2 = Math.min(((i + 1) * 100) - 1, 999);

						String strSectionTime;
						strSectionTime = String.format("%3d-%3d %s", temp, temp2, GeneralUtil.getTime(bestSectionTime[i]));

						receiver.drawScoreFont(engine, playerID, 0, 3 + i, strSectionTime, sectionIsNewRecord[i]);

						totalTime += bestSectionTime[i];
					}

					receiver.drawScoreFont(engine, playerID, 0, 14, "TOTAL", EventReceiver.COLOR_PINK);
					receiver.drawScoreFont(engine, playerID, 0, 15, GeneralUtil.getTime(totalTime));
					receiver.drawScoreFont(engine, playerID, 9, 14, "AVERAGE", EventReceiver.COLOR_PINK);
					receiver.drawScoreFont(engine, playerID, 9, 15, GeneralUtil.getTime(totalTime / SECTION_MAX));

					receiver.drawScoreFont(engine, playerID, 0, 17, "F:VIEW RANKING", EventReceiver.COLOR_GREEN);
				}*/
			}
		} else {
			// スコア
			receiver.drawScoreFont(engine, playerID, 0, 2, "SCORE", EventReceiver.COLOR_PINK);
			String strScore;
			if((lastscore == 0) || (scgettime <= 0)) {
				strScore = String.valueOf(engine.statistics.score);
			} else {
				strScore = String.valueOf(engine.statistics.score) + "\n(+" + String.valueOf(lastscore) + ")";
			}
			receiver.drawScoreFont(engine, playerID, 0, 3, strScore, ((scoreflash > 0) && (scoreflash % 4 == 0)));

			if (bravoAlert)
				receiver.drawScoreFont(engine, playerID, 0, 5, "!", EventReceiver.COLOR_YELLOW);				
			
			// レベル
			receiver.drawScoreFont(engine, playerID, 0, 9, "LEVEL", EventReceiver.COLOR_PINK);
			int tempLevel = engine.statistics.level;
			if(tempLevel < 0) tempLevel = 0;
			String strLevel = String.format("%3d", tempLevel);
			receiver.drawScoreFont(engine, playerID, 0, 10, strLevel);

			int speed = engine.speed.gravity / 128;
			if(engine.speed.gravity < 0) speed = 40;
			receiver.drawSpeedMeter(engine, playerID, 0, 11, speed);

			receiver.drawScoreFont(engine, playerID, 0, 12, String.format("%3d", nextseclv));

			// タイム
			receiver.drawScoreFont(engine, playerID, 0, 14, "TIME", EventReceiver.COLOR_PINK);
			receiver.drawScoreFont(engine, playerID, 0, 15, GeneralUtil.getTime(engine.statistics.time));

			// リミットタイム
			receiver.drawScoreFont(engine, playerID, 0, 17, "LIMIT TIME", EventReceiver.COLOR_PINK);
			String strLimitTime = GeneralUtil.getTime(timelimitTimer);
			if(timeextendDisp > 0) {
				strLimitTime += "\n(+" + timeextendSeconds + " SEC.)";
			}
			receiver.drawScoreFont(engine, playerID, 0, 18, strLimitTime, ((engine.timerActive) && (timelimitTimer < 600) && (timelimitTimer % 4 == 0)));

			/*
			// ロール残り時間
			if((engine.gameActive) && (engine.ending == 2)) {
				int time = ROLLTIMELIMIT - rolltime;
				if(time < 0) time = 0;
				receiver.drawScoreFont(engine, playerID, 0, 17, "ROLL TIME", EventReceiver.COLOR_PINK);
				receiver.drawScoreFont(engine, playerID, 0, 18, GeneralUtil.getTime(time), ((time > 0) && (time < 10 * 60)));
			}
			// セクションタイム
			if((showsectiontime == true) && (sectiontime != null)) {
				receiver.drawScoreFont(engine, playerID, 12, 2, "SECTION TIME", EventReceiver.COLOR_PINK);

				for(int i = 0; i < sectiontime.length; i++) {
					if(sectiontime[i] > 0) {
						int temp = i * 100;
						if(temp > 999) temp = 999;

						int section = engine.statistics.level / 100;
						String strSeparator = " ";
						if((i == section) && (engine.ending == 0)) strSeparator = "b";

						String strSectionTime;
						strSectionTime = String.format("%3d%s%s", temp, strSeparator, GeneralUtil.getTime(sectiontime[i]));

						receiver.drawScoreFont(engine, playerID, 12, 3 + i, strSectionTime, sectionIsNewRecord[i]);
					}
				}

				if(sectionavgtime > 0) {
					receiver.drawScoreFont(engine, playerID, 12, 14, "AVERAGE", EventReceiver.COLOR_PINK);
					receiver.drawScoreFont(engine, playerID, 12, 15, GeneralUtil.getTime(sectionavgtime));
				}
			}
			*/
		}
	}

	/*
	 * 移動中の処理
	 */
	@Override
	public boolean onMove(GameEngine engine, int playerID) {
		// 新規ピース出現時
		if((engine.ending == 0) && (engine.statc[0] == 0) && (engine.holdDisable == false) && (!lvupflag)) {
			// レベルアップ
			if(engine.statistics.level < nextseclv - 1) {
				engine.statistics.level++;
				if((engine.statistics.level == nextseclv - 1) && (lvstopse == true)) engine.playSE("levelstop");
			}
			levelUp(engine);
		}
		if( (engine.ending == 0) && (engine.statc[0] > 0) && ((version >= 2) || (engine.holdDisable == false)) ) {
			lvupflag = false;
		}

		return false;
	}

	/*
	 * ARE中の処理
	 */
	@Override
	public boolean onARE(GameEngine engine, int playerID) {
		// 最後のフレーム
		if((engine.ending == 0) && (engine.statc[0] >= engine.statc[1] - 1) && (!lvupflag)) {
			if(engine.statistics.level < nextseclv - 1) {
				engine.statistics.level++;
				if((engine.statistics.level == nextseclv - 1) && (lvstopse == true)) engine.playSE("levelstop");
			}
			levelUp(engine);
			lvupflag = true;
		}

		return false;
	}

	/**
	 * レベルが上がったときの共通処理
	 */
	private void levelUp(GameEngine engine) {
		/*
		// メーター
		engine.meterValue = ((engine.statistics.level % 100) * receiver.getMeterMax(engine)) / 99;
		engine.meterColor = GameEngine.METER_COLOR_GREEN;
		if(engine.statistics.level % 100 >= 50) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
		if(engine.statistics.level % 100 >= 80) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
		if(engine.statistics.level == nextseclv - 1) engine.meterColor = GameEngine.METER_COLOR_RED;
		*/
		// 速度変更
		setSpeed(engine);

		// LV100到達でゴーストを消す
		if((engine.statistics.level >= 100) && (!alwaysghost)) engine.ghost = false;

		// BGMフェードアウト
		if((tableBGMFadeout[bgmlv] != -1) && (engine.statistics.level >= tableBGMFadeout[bgmlv]))
			owner.bgmStatus.fadesw  = true;
	}

	/*
	 * スコア計算
	 */
	@Override
	public void calcScore(GameEngine engine, int playerID, int lines) {
		boolean allClear = engine.field.isEmpty();
		if((lines >= 1) && (engine.ending == 0)) {
			// レベルアップ
			int levelb = engine.statistics.level;
			engine.statistics.level += lines;
			if (lines == 3) engine.statistics.level++;
			else if (lines >= 4) engine.statistics.level+= 2;
			levelUp(engine);

			/*
			if(engine.statistics.level >= 999) {
				// エンディング
				engine.playSE("endingstart");
				engine.statistics.level = 999;
				engine.timerActive = false;
				engine.ending = 2;

				sectionscomp++;
				//setAverageSectionTime();
				//stNewRecordCheck(sectionscomp - 1);
			} else*/ if(engine.statistics.level >= nextseclv) {
				// 次のセクション
				engine.playSE("levelup");

				sectionscomp++;
				setAverageSectionTime();
				//stNewRecordCheck(sectionscomp - 1);

				// 背景切り替え
				owner.backgroundStatus.fadesw = true;
				owner.backgroundStatus.fadecount = 0;
				owner.backgroundStatus.fadebg = nextseclv / 100;

				// BGM切り替え
				if((tableBGMChange[bgmlv] != -1) && (engine.statistics.level >= tableBGMChange[bgmlv])) {
					bgmlv++;
					owner.bgmStatus.fadesw = false;
					owner.bgmStatus.bgm = bgmlv;
				}

				// 次のセクションレベルを更新
				nextseclv += 100;
				//if(nextseclv > 999) nextseclv = 999;
			} else if((engine.statistics.level == nextseclv - 1) && (lvstopse == true)) {
				engine.playSE("levelstop");
			}

			// スコア計算
			if(allClear) {
				engine.playSE("bravo");
				engine.playSE("gradeup");
				engine.statistics.score++;
				scoreflash = 180;
				timeextendSeconds = 0;
				if (levelb < 1000)
				{
					if (lines == 1) timeextendSeconds = 5;
					else if (lines == 2) timeextendSeconds = 8;
					else if (lines == 3) timeextendSeconds = 11;
					else if (lines >= 4) timeextendSeconds = 15;
				}
				else
					timeextendSeconds = lines-1;
				if (timeextendSeconds > 0)
				{
					timeextendDisp = 120;
					timelimitTimer += timeextendSeconds * 60;
				}
			}
		}
		if (!allClear || bravoAlert)
		{
			boolean bravoAlertB = bravoAlert;
			int nextID = -1;
			Piece pieceNext = engine.getNextObject(engine.nextPieceCount);
			if (pieceNext != null)
				nextID = pieceNext.id;
			int holdID = -1;
			Piece pieceHold = engine.holdPieceObject;
			if (pieceHold != null)
				holdID = pieceHold.id;
			Field fld = engine.field;
			bravoAlert = checkBravoAlert(fld, nextID, holdID);
			if (bravoAlertB && !bravoAlert && !allClear)
				engine.playSE("regret");
		}
	}
	
	public boolean checkBravoAlert(Field field, int nextID, int holdID)
	{
		int height = field.getHeight() - field.getHighestBlockY();
		if ((height & 1) == 1 || height == 0)
			return false;
		String str = field.toString();
		str = str.substring(str.length()-(15*height));
		String fld = "";
		String temp = "";
		for (int y = 0; y < height; y++)
		{
			temp = "";
			for (int x = 0; x < 10; x++)
			{
				if (str.charAt(y*15+x+4) == '0')
					temp = temp + "0";
				else
					temp = temp + "1";
			}
			if (!temp.equals("1111111111"))
				fld = fld + temp;
		}
		
		String[] patternsNext = null;
		if (nextID == Piece.PIECE_I)
			patternsNext = BRAVO_PATTERNS_I;
		else if (nextID == Piece.PIECE_L)
			patternsNext = BRAVO_PATTERNS_L;
		else if (nextID == Piece.PIECE_O)
			patternsNext = BRAVO_PATTERNS_O;
		else if (nextID == Piece.PIECE_T)
			patternsNext = BRAVO_PATTERNS_T;
		else if (nextID == Piece.PIECE_J)
			patternsNext = BRAVO_PATTERNS_J;
		if (patternsNext != null)
			for (int x = 0; x < patternsNext.length; x++)
				if (fld.equals(patternsNext[x]))
					return true;
		
		if (holdID == nextID)
			return false;

		String[] patternsHold = null;
		if (holdID == Piece.PIECE_I)
			patternsHold = BRAVO_PATTERNS_I;
		else if (holdID == Piece.PIECE_L)
			patternsHold = BRAVO_PATTERNS_L;
		else if (holdID == Piece.PIECE_O)
			patternsHold = BRAVO_PATTERNS_O;
		else if (holdID == Piece.PIECE_T)
			patternsHold = BRAVO_PATTERNS_T;
		else if (holdID == Piece.PIECE_J)
			patternsHold = BRAVO_PATTERNS_J;
		if (patternsHold != null)
			for (int x = 0; x < patternsHold.length; x++)
				if (fld.equals(patternsHold[x]))
					return true;
		
		return false;
	}

	/*
	 * Readyの時の初期化処理（初期化前）
	 */
	@Override
	public boolean onReady(GameEngine engine, int playerID) {
		if(engine.statc[0] == 0) {
			// 時間制限設定
			timelimitTimer = TIME_LIMIT;

			for(int i = 0; i < Piece.PIECE_COUNT; i++) {
				engine.nextPieceEnable[i] = (PIECE_ENABLE[i] == 1);
			}
		}
		return false;
	}
	/*
	 * 各フレームの終わりの処理
	 */
	@Override
	public void onLast(GameEngine engine, int playerID) {
		if(scoreflash > 0) scoreflash--;
		if(timeextendDisp > 0) timeextendDisp--;
		// 獲得スコア表示
		if(scgettime > 0) scgettime--;

		// セクションタイム増加
		if((engine.timerActive) && (engine.ending == 0)) {
			int section = engine.statistics.level / 100;

			if((section >= 0) && (section < sectiontime.length)) {
				sectiontime[section]++;
			}
		}
		// リミットタイム
		if(engine.gameActive && engine.timerActive && (timelimitTimer > 0)) {
			timelimitTimer--;

			// 時間メーター
			if(timelimitTimer >= TIME_LIMIT) {
				engine.meterValue = receiver.getMeterMax(engine);
			} else {
				engine.meterValue = (timelimitTimer * receiver.getMeterMax(engine)) / TIME_LIMIT;
			}
			engine.meterColor = GameEngine.METER_COLOR_GREEN;
			if(timelimitTimer <= 60*60) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
			if(timelimitTimer <= 30*60) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
			if(timelimitTimer <= 10*60) engine.meterColor = GameEngine.METER_COLOR_RED;

			if((timelimitTimer > 0) && (timelimitTimer <= 10 * 60) && (timelimitTimer % 60 == 0)) {
				// 10秒前からのカウントダウン
				engine.playSE("countdown");
			}
		}
		if((timelimitTimer <= 0) && (engine.timerActive)) {
			engine.nowPieceObject = null;
			engine.stat = GameEngine.STAT_GAMEOVER;
			engine.resetStatc();
		}
		// エンディング
		if((engine.gameActive) && (engine.ending == 2)) {
			if((version >= 1) && (engine.ctrl.isPress(Controller.BUTTON_F)))
				rolltime += 5;
			else
				rolltime += 1;

			// 時間メーター
			int remainRollTime = ROLLTIMELIMIT - rolltime;
			engine.meterValue = (remainRollTime * receiver.getMeterMax(engine)) / ROLLTIMELIMIT;
			engine.meterColor = GameEngine.METER_COLOR_GREEN;
			if(remainRollTime <= 30*60) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
			if(remainRollTime <= 20*60) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
			if(remainRollTime <= 10*60) engine.meterColor = GameEngine.METER_COLOR_RED;

			// ロール終了
			if(rolltime >= ROLLTIMELIMIT) {
				engine.gameActive = false;
				engine.resetStatc();
				engine.stat = GameEngine.STAT_EXCELLENT;
			}
		}
	}

	/*
	 * 結果画面
	 */
	@Override
	public void renderResult(GameEngine engine, int playerID) {
		receiver.drawMenuFont(engine, playerID, 0, 0, "kn PAGE" + (engine.statc[1] + 1) + "/3", EventReceiver.COLOR_RED);

		if(engine.statc[1] == 0) {
			receiver.drawMenuFont(engine, playerID, 0, 2, "SCORE", EventReceiver.COLOR_PINK);
			String strScore = String.format("%10d", engine.statistics.score);
			receiver.drawMenuFont(engine, playerID, 0, 3, strScore);

			receiver.drawMenuFont(engine, playerID, 0, 4, "LINE", EventReceiver.COLOR_PINK);
			String strLines = String.format("%10d", engine.statistics.lines);
			receiver.drawMenuFont(engine, playerID, 0, 5, strLines);

			receiver.drawMenuFont(engine, playerID, 0, 6, "LEVEL", EventReceiver.COLOR_PINK);
			String strLevel = String.format("%10d", engine.statistics.level);
			receiver.drawMenuFont(engine, playerID, 0, 7, strLevel);

			receiver.drawMenuFont(engine, playerID, 0, 8, "TIME", EventReceiver.COLOR_PINK);
			String strTime = String.format("%10s", GeneralUtil.getTime(engine.statistics.time));
			receiver.drawMenuFont(engine, playerID, 0, 9, strTime);

			if(rankingRank != -1) {
				receiver.drawMenuFont(engine, playerID, 0, 12, "RANK", EventReceiver.COLOR_PINK);
				String strRank = String.format("%10d", rankingRank + 1);
				receiver.drawMenuFont(engine, playerID, 0, 13, strRank);
			}
		} else if(engine.statc[1] == 1) {
			receiver.drawMenuFont(engine, playerID, 0, 2, "SECTION", EventReceiver.COLOR_PINK);

			for(int i = 0; i < sectiontime.length; i++) {
				if(sectiontime[i] > 0) {
					receiver.drawMenuFont(engine, playerID, 2, 3 + i, GeneralUtil.getTime(sectiontime[i]), EventReceiver.COLOR_WHITE);
				}
			}

			if(sectionavgtime > 0) {
				receiver.drawMenuFont(engine, playerID, 0, 14, "AVERAGE", EventReceiver.COLOR_PINK);
				receiver.drawMenuFont(engine, playerID, 2, 15, GeneralUtil.getTime(sectionavgtime));
			}
		} else if(engine.statc[1] == 2) {
			receiver.drawMenuFont(engine, playerID, 0, 2, "LINE/MIN", EventReceiver.COLOR_PINK);
			String strLPM = String.format("%10g", engine.statistics.lpm);
			receiver.drawMenuFont(engine, playerID, 0, 3, strLPM);

			receiver.drawMenuFont(engine, playerID, 0, 4, "SCORE/MIN", EventReceiver.COLOR_PINK);
			String strSPM = String.format("%10g", engine.statistics.spm);
			receiver.drawMenuFont(engine, playerID, 0, 5, strSPM);

			receiver.drawMenuFont(engine, playerID, 0, 6, "PIECE", EventReceiver.COLOR_PINK);
			String strPiece = String.format("%10d", engine.statistics.totalPieceLocked);
			receiver.drawMenuFont(engine, playerID, 0, 7, strPiece);

			receiver.drawMenuFont(engine, playerID, 0, 8, "PIECE/SEC", EventReceiver.COLOR_PINK);
			String strPPS = String.format("%10g", engine.statistics.pps);
			receiver.drawMenuFont(engine, playerID, 0, 9, strPPS);
		}
	}

	/*
	 * 結果画面の処理
	 */
	@Override
	public boolean onResult(GameEngine engine, int playerID) {
		// ページ切り替え
		if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_UP)) {
			engine.statc[1]--;
			if(engine.statc[1] < 0) engine.statc[1] = 2;
			engine.playSE("change");
		}
		if(engine.ctrl.isMenuRepeatKey(Controller.BUTTON_DOWN)) {
			engine.statc[1]++;
			if(engine.statc[1] > 2) engine.statc[1] = 0;
			engine.playSE("change");
		}
		/*
		// セクションタイム表示切替
		if(engine.ctrl.isPush(Controller.BUTTON_F)) {
			engine.playSE("change");
			isShowBestSectionTime = !isShowBestSectionTime;
		}
		 */
		return false;
	}

	/*
	 * リプレイ保存
	 */
	@Override
	public void saveReplay(GameEngine engine, int playerID, CustomProperties prop) {
		saveSetting(owner.replayProp);
		owner.replayProp.setProperty("bravomania.version", version);

		// ランキング更新
		if((owner.replayMode == false) && (startlevel == 0) && (always20g == false) && (engine.ai == null)) {
			updateRanking(engine.statistics.score, engine.statistics.level, engine.statistics.time);
			//if(sectionAnyNewRecord) updateBestSectionTime();

			if((rankingRank != -1) /*|| (sectionAnyNewRecord)*/) {
				saveRanking(owner.modeConfig, engine.ruleopt.strRuleName);
				receiver.saveModeConfig(owner.modeConfig);
			}
		}
	}

	/**
	 * プロパティファイルからランキングを読み込み
	 * @param prop プロパティファイル
	 * @param ruleName ルール名
	 */
	private void loadRanking(CustomProperties prop, String ruleName) {
		for(int i = 0; i < RANKING_MAX; i++) {
			rankingScore[i] = prop.getProperty("bravomania.ranking." + ruleName + ".score." + i, 0);
			rankingLevel[i] = prop.getProperty("bravomania.ranking." + ruleName + ".level." + i, 0);
			rankingTime[i] = prop.getProperty("bravomania.ranking." + ruleName + ".time." + i, 0);
		}
		/*
		for(int i = 0; i < SECTION_MAX; i++) {
			bestSectionTime[i] = prop.getProperty("bravomania.bestSectionTime." + ruleName + "." + i, DEFAULT_SECTION_TIME);
		}
		*/
	}

	/**
	 * プロパティファイルにランキングを保存
	 * @param prop プロパティファイル
	 * @param ruleName ルール名
	 */
	private void saveRanking(CustomProperties prop, String ruleName) {
		for(int i = 0; i < RANKING_MAX; i++) {
			prop.setProperty("bravomania.ranking." + ruleName + ".score." + i, rankingScore[i]);
			prop.setProperty("bravomania.ranking." + ruleName + ".level." + i, rankingLevel[i]);
			prop.setProperty("bravomania.ranking." + ruleName + ".time." + i, rankingTime[i]);
		}
		/*
		for(int i = 0; i < SECTION_MAX; i++) {
			prop.setProperty("bravomania.bestSectionTime." + ruleName + "." + i, bestSectionTime[i]);
		}
		*/
	}

	/**
	 * ランキングを更新
	 * @param gr 段位
	 * @param lv レベル
	 * @param time タイム
	 */
	private void updateRanking(int score, int lv, int time) {
		rankingRank = checkRanking(score, lv, time);

		if(rankingRank != -1) {
			// ランキングをずらす
			for(int i = RANKING_MAX - 1; i > rankingRank; i--) {
				rankingScore[i] = rankingScore[i - 1];
				rankingLevel[i] = rankingLevel[i - 1];
				rankingTime[i] = rankingTime[i - 1];
			}

			// 新しいデータを登録
			rankingScore[rankingRank] = score;
			rankingLevel[rankingRank] = lv;
			rankingTime[rankingRank] = time;
		}
	}

	/**
	 * ランキングの順位を取得
	 * @param score Number of bravos
	 * @param lv レベル
	 * @param time タイム
	 * @return 順位(ランク外なら-1)
	 */
	private int checkRanking(int score, int lv, int time) {
		for(int i = 0; i < RANKING_MAX; i++) {
			if(score > rankingScore[i])
				return i;
			else if((score == rankingScore[i]) && (lv > rankingLevel[i]))
				return i;
			else if((score == rankingScore[i]) && (lv == rankingLevel[i]) && (time < rankingTime[i]))
				return i;
		}
		return -1;
	}

	/**
	 * ベストセクションタイム更新
	 */
	/*
	private void updateBestSectionTime() {
		for(int i = 0; i < SECTION_MAX; i++) {
			if(sectionIsNewRecord[i]) {
				bestSectionTime[i] = sectiontime[i];
			}
		}
	}
	*/
}
