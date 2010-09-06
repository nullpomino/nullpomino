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
package mu.nu.nullpo.tool.ruleeditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import mu.nu.nullpo.game.component.Block;
import mu.nu.nullpo.game.component.Piece;
import mu.nu.nullpo.game.component.RuleOptions;
import mu.nu.nullpo.util.CustomProperties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * ルールエディター
 */
public class RuleEditor extends JFrame implements ActionListener {
	/** シリアルVersion */
	private static final long serialVersionUID = 1L;

	/** Log */
	static Logger log = Logger.getLogger(RuleEditor.class);

	/** Swing版のSave settings用Property file */
	public CustomProperties propConfig;

	/** Default language file */
	public CustomProperties propLangDefault;

	/** UI翻訳用Property file */
	public CustomProperties propLang;

	//----------------------------------------------------------------------
	/** 今開いているFilename (null:なし) */
	private String strNowFile;

	/** タブ */
	private JTabbedPane tabPane;

	//----------------------------------------------------------------------
	/* 基本設定パネル */

	/** Rule name */
	private JTextField txtfldRuleName;

	/** NEXT表示countのテキストフィールド */
	private JTextField txtfldNextDisplay;

	/** 絵柄のComboボックス */
	private JComboBox comboboxSkin;

	/** ゴースト有効 */
	private JCheckBox chkboxGhost;

	/** Blockピースがフィールド枠外から出現 */
	private JCheckBox chkboxEnterAboveField;

	/** 出現予定地が埋まっているときにY-coordinateを上にずらすMaximum count */
	private JTextField txtfldEnterMaxDistanceY;

	/** NEXT順生成アルゴリズム */
	private JComboBox comboboxRandomizer;

	/** NEXT順生成アルゴリズムのリスト */
	private Vector<String> vectorRandomizer;

	/** NEXT順生成アルゴリズムのリセット button */
	private JButton btnResetRandomizer;

	//----------------------------------------------------------------------
	/* フィールド設定パネル */

	/** フィールドの幅 */
	private JTextField txtfldFieldWidth;

	/** Field height */
	private JTextField txtfldFieldHeight;

	/** フィールドの見えない部分の高さ */
	private JTextField txtfldFieldHiddenHeight;

	/** フィールドの天井 */
	private JCheckBox chkboxFieldCeiling;

	/** フィールド枠内に置けないと死亡 */
	private JCheckBox chkboxFieldLockoutDeath;

	/** フィールド枠外にはみ出しただけで死亡 */
	private JCheckBox chkboxFieldPartialLockoutDeath;

	//----------------------------------------------------------------------
	/* ホールド設定パネル */

	/** ホールド有効 */
	private JCheckBox chkboxHoldEnable;

	/** 先行ホールド */
	private JCheckBox chkboxHoldInitial;

	/** 先行ホールド連続使用不可 */
	private JCheckBox chkboxHoldInitialLimit;

	/** ホールドを使ったときにBlockピースの向きを初期状態に戻す */
	private JCheckBox chkboxHoldResetDirection;

	/** ホールドできる count (-1:無制限) */
	private JTextField txtfldHoldLimit;

	//----------------------------------------------------------------------
	/* ドロップ設定パネル */

	/** Hard drop使用可否 */
	private JCheckBox chkboxDropHardDropEnable;

	/** Hard dropで即固定 */
	private JCheckBox chkboxDropHardDropLock;

	/** Hard drop連続使用不可 */
	private JCheckBox chkboxDropHardDropLimit;

	/** Soft drop使用可否 */
	private JCheckBox chkboxDropSoftDropEnable;

	/** Soft dropで即固定 */
	private JCheckBox chkboxDropSoftDropLock;

	/** Soft drop連続使用不可 */
	private JCheckBox chkboxDropSoftDropLimit;

	/** 接地状態でSoft dropすると即固定 */
	private JCheckBox chkboxDropSoftDropSurfaceLock;

	/** Soft drop速度 */
	private JTextField txtfldDropSoftDropSpeed;

	/** Soft drop速度をCurrent 通常速度×n倍にする */
	private JCheckBox chkboxDropSoftDropMultiplyNativeSpeed;

	//----------------------------------------------------------------------
	/* rotation設定パネル */

	/** 先行rotation */
	private JCheckBox chkboxRotateInitial;

	/** 先行rotation連続使用不可 */
	private JCheckBox chkboxRotateInitialLimit;

	/** Wallkick */
	private JCheckBox chkboxRotateWallkick;

	/** 先行rotationでもWallkickする */
	private JCheckBox chkboxRotateInitialWallkick;

	/** 上DirectionへのWallkickができる count (-1:無限) */
	private JTextField txtfldRotateMaxUpwardWallkick;

	/** falseなら左が正rotation, trueなら右が正rotation */
	private JCheckBox chkboxRotateButtonDefaultRight;

	/** 逆rotationを許可 (falseなら正rotationと同じ) */
	private JCheckBox chkboxRotateButtonAllowReverse;

	/** 2rotationを許可 (falseなら正rotationと同じ) */
	private JCheckBox chkboxRotateButtonAllowDouble;

	/** Wallkickアルゴリズム */
	private JComboBox comboboxWallkickSystem;

	/** Wallkickアルゴリズムのリスト */
	private Vector<String> vectorWallkickSystem;

	/** Wallkickアルゴリズムのリセット button */
	private JButton btnResetWallkickSystem;

	//----------------------------------------------------------------------
	/* 固定 time設定パネル */

	/** 最低固定 time */
	private JTextField txtfldLockDelayMin;

	/** 最高固定 time */
	private JTextField txtfldLockDelayMax;

	/** 落下で固定 timeリセット */
	private JCheckBox chkboxLockDelayLockResetFall;

	/** 移動で固定 timeリセット */
	private JCheckBox chkboxLockDelayLockResetMove;

	/** rotationで固定 timeリセット */
	private JCheckBox chkboxLockDelayLockResetRotate;

	/** 横移動カウンタとrotationカウンタを共有 (横移動カウンタだけ使う) */
	private JCheckBox chkboxLockDelayLockResetLimitShareCount;

	/** 横移動 count制限 */
	private JTextField txtfldLockDelayLockResetLimitMove;

	/** rotation count制限 */
	private JTextField txtfldLockDelayLockResetLimitRotate;

	/** 横移動カウンタかrotationカウンタが超過したら固定 timeリセットを無効にする */
	private JRadioButton radioLockDelayLockResetLimitOverNoReset;

	/** 横移動カウンタかrotationカウンタが超過したら即座に固定する */
	private JRadioButton radioLockDelayLockResetLimitOverInstant;

	/** 横移動カウンタかrotationカウンタが超過したらWallkick無効にする */
	private JRadioButton radioLockDelayLockResetLimitOverNoWallkick;

	//----------------------------------------------------------------------
	/* ARE設定パネル */

	/** 最低ARE */
	private JTextField txtfldAREMin;

	/** 最高ARE */
	private JTextField txtfldAREMax;

	/** 最低ARE after line clear */
	private JTextField txtfldARELineMin;

	/** 最高ARE after line clear */
	private JTextField txtfldARELineMax;

	/** 固定した瞬間に光る frame count */
	private JTextField txtfldARELockFlash;

	/** Blockが光る専用 frame を入れる */
	private JCheckBox chkboxARELockFlashOnlyFrame;

	/** Line clear前にBlockが光る frame を入れる */
	private JCheckBox chkboxARELockFlashBeforeLineClear;

	/** ARE cancel on move checkbox */
	private JCheckBox chkboxARECancelMove;

	/** ARE cancel on rotate checkbox */
	private JCheckBox chkboxARECancelRotate;

	/** ARE cancel on hold checkbox */
	private JCheckBox chkboxARECancelHold;

	//----------------------------------------------------------------------
	/* Line clear設定パネル */

	/** 最低Line clear time */
	private JTextField txtfldLineDelayMin;

	/** 最高Line clear time */
	private JTextField txtfldLineDelayMax;

	/** 落下アニメ */
	private JCheckBox chkboxLineFallAnim;

	/** Line delay cancel on move checkbox */
	private JCheckBox chkboxLineCancelMove;

	/** Line delay cancel on rotate checkbox */
	private JCheckBox chkboxLineCancelRotate;

	/** Line delay cancel on hold checkbox */
	private JCheckBox chkboxLineCancelHold;

	//----------------------------------------------------------------------
	/* 移動設定パネル */

	/** 最低横溜め time */
	private JTextField txtfldMoveDASMin;

	/** 最高横溜め time */
	private JTextField txtfldMoveDASMax;

	/** 横移動間隔 */
	private JTextField txtfldMoveDASDelay;

	/** Ready画面で横溜め可能 */
	private JCheckBox chkboxMoveDASInReady;

	/** 最初の frame で横溜め可能 */
	private JCheckBox chkboxMoveDASInMoveFirstFrame;

	/** Blockが光った瞬間に横溜め可能 */
	private JCheckBox chkboxMoveDASInLockFlash;

	/** Line clear中に横溜め可能 */
	private JCheckBox chkboxMoveDASInLineClear;

	/** ARE中に横溜め可能 */
	private JCheckBox chkboxMoveDASInARE;

	/** AREの最後の frame で横溜め可能 */
	private JCheckBox chkboxMoveDASInARELastFrame;

	/** Ending突入画面で横溜め可能 */
	private JCheckBox chkboxMoveDASInEndingStart;

	/** DAS charge on blocked move checkbox*/
	private JCheckBox chkboxMoveDASChargeOnBlockedMove;

	/** Store DAS Charge on neutral checkbox **/
	private JCheckBox chkboxMoveDASStoreChargeOnNeutral;

	/** Redirect in delay checkbox **/
	private JCheckBox chkboxMoveDASRedirectInDelay;

	/** 最初の frame に移動可能 */
	private JCheckBox chkboxMoveFirstFrame;

	/** 斜め移動 */
	private JCheckBox chkboxMoveDiagonal;

	/** 上下同時押し可能 */
	private JCheckBox chkboxMoveUpAndDown;

	/** 左右同時押し可能 */
	private JCheckBox chkboxMoveLeftAndRightAllow;

	/** 左右同時押ししたときに前の frame の input Directionを優先する */
	private JCheckBox chkboxMoveLeftAndRightUsePreviousInput;

	/** Shift lock checkbox */
	private JCheckBox chkboxMoveShiftLockEnable;

	//----------------------------------------------------------------------
	/* rotationパターン補正パネル */

	/** rotationパターン補正タブ */
	private JTabbedPane tabPieceOffset;

	/** rotationパターン補正(X) input 欄 */
	private JTextField[][] txtfldPieceOffsetX;

	/** rotationパターン補正(Y) input 欄 */
	private JTextField[][] txtfldPieceOffsetY;

	//----------------------------------------------------------------------
	/* rotationパターン補正パネル */

	/** rotationパターン補正タブ */
	private JTabbedPane tabPieceSpawn;

	/** 出現位置補正(X) input 欄 */
	private JTextField[][] txtfldPieceSpawnX;

	/** 出現位置補正(Y) input 欄 */
	private JTextField[][] txtfldPieceSpawnY;

	/** Big時出現位置補正(X) input 欄 */
	private JTextField[][] txtfldPieceSpawnBigX;

	/** Big時出現位置補正(Y) input 欄 */
	private JTextField[][] txtfldPieceSpawnBigY;

	//----------------------------------------------------------------------
	/* 色設定パネル */

	/** 色選択Comboボックス */
	private JComboBox[] comboboxPieceColor;

	//----------------------------------------------------------------------
	/* 初期Direction設定パネル */

	/** 初期Direction選択Comboボックス */
	private JComboBox[] comboboxPieceDirection;

	//----------------------------------------------------------------------
	/** Block画像 */
	private BufferedImage[] imgBlockSkins;

	/**
	 * Constructor
	 */
	public RuleEditor() {
		super();

		init();
		readRuleToUI(new RuleOptions());

		setVisible(true);
	}

	/**
	 * 特定のファイルを読み込むConstructor
	 * @param filename Filename (空文字列かnullにするとパラメータなしConstructorと同じ動作）
	 */
	public RuleEditor(String filename) {
		super();

		init();

		RuleOptions ruleopt = new RuleOptions();

		if((filename != null) && (filename.length() > 0)) {
			try {
				ruleopt = load(filename);
				strNowFile = filename;
				setTitle(getUIText("Title_RuleEditor") + ":" + strNowFile);
			} catch (IOException e) {
				log.error("Failed to load rule data from " + filename, e);
				JOptionPane.showMessageDialog(this, getUIText("Message_FileLoadFailed")+"\n"+e, getUIText("Title_FileLoadFailed"),
											  JOptionPane.ERROR_MESSAGE);
			}
		}

		readRuleToUI(ruleopt);

		setVisible(true);
	}

	/**
	 * Initialization
	 */
	private void init() {
		// 設定ファイル読み込み
		propConfig = new CustomProperties();
		try {
			FileInputStream in = new FileInputStream("config/setting/swing.cfg");
			propConfig.load(in);
			in.close();
		} catch(IOException e) {}

		// 言語ファイル読み込み
		propLangDefault = new CustomProperties();
		try {
			FileInputStream in = new FileInputStream("config/lang/ruleeditor_default.properties");
			propLangDefault.load(in);
			in.close();
		} catch (IOException e) {
			log.error("Couldn't load default UI language file", e);
		}

		propLang = new CustomProperties();
		try {
			FileInputStream in = new FileInputStream("config/lang/ruleeditor_" + Locale.getDefault().getCountry() + ".properties");
			propLang.load(in);
			in.close();
		} catch(IOException e) {}

		// Look&Feel設定
		if(propConfig.getProperty("option.usenativelookandfeel", true) == true) {
			try {
				UIManager.getInstalledLookAndFeels();
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch(Exception e) {
				log.warn("Failed to set native look&feel", e);
			}
		}

		strNowFile = null;

		setTitle(getUIText("Title_RuleEditor"));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		loadBlockSkins();

		initUI();
		pack();
	}

	/**
	 * 画面のInitialization
	 */
	private void initUI() {
		getContentPane().setLayout(new BorderLayout());

		// Menuバー --------------------------------------------------
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		// ファイルMenu
		JMenu mFile = new JMenu(getUIText("JMenu_File"));
		mFile.setMnemonic('F');
		menuBar.add(mFile);

		// 新規作成
		JMenuItem miNew = new JMenuItem(getUIText("JMenuItem_New"));
		miNew.setMnemonic('N');
		miNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
		miNew.setActionCommand("New");
		miNew.addActionListener(this);
		mFile.add(miNew);

		// 開く
		JMenuItem miOpen = new JMenuItem(getUIText("JMenuItem_Open"));
		miOpen.setMnemonic('O');
		miOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
		miOpen.setActionCommand("Open");
		miOpen.addActionListener(this);
		mFile.add(miOpen);

		// Up書き保存
		JMenuItem miSave = new JMenuItem(getUIText("JMenuItem_Save"));
		miSave.setMnemonic('S');
		miSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
		miSave.setActionCommand("Save");
		miSave.addActionListener(this);
		mFile.add(miSave);

		// 名前を付けて保存
		JMenuItem miSaveAs = new JMenuItem(getUIText("JMenuItem_SaveAs"));
		miSaveAs.setMnemonic('A');
		miSaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK));
		miSaveAs.setActionCommand("SaveAs");
		miSaveAs.addActionListener(this);
		mFile.add(miSaveAs);

		// 終了
		JMenuItem miExit = new JMenuItem(getUIText("JMenuItem_Exit"));
		miExit.setMnemonic('X');
		miExit.setActionCommand("Exit");
		miExit.addActionListener(this);
		mFile.add(miExit);

		// タブ全体 --------------------------------------------------
		tabPane = new JTabbedPane();
		getContentPane().add(tabPane, BorderLayout.NORTH);

		// 基本設定タブ --------------------------------------------------
		JPanel panelBasic = new JPanel();
		panelBasic.setLayout(new BoxLayout(panelBasic, BoxLayout.Y_AXIS));
		tabPane.addTab(getUIText("TabName_Basic"), panelBasic);

		// Rule name
		JPanel pRuleName = new JPanel();
		panelBasic.add(pRuleName);

		JLabel lRuleName = new JLabel(getUIText("Basic_RuleName"));
		pRuleName.add(lRuleName);

		txtfldRuleName = new JTextField("", 15);
		pRuleName.add(txtfldRuleName);

		// NEXT表示count
		JPanel pNextDisplay = new JPanel();
		panelBasic.add(pNextDisplay);

		JLabel lNextDisplay = new JLabel(getUIText("Basic_NextDisplay"));
		pNextDisplay.add(lNextDisplay);

		txtfldNextDisplay = new JTextField("", 5);
		pNextDisplay.add(txtfldNextDisplay);

		// 絵柄
		JPanel pSkin = new JPanel();
		panelBasic.add(pSkin);

		JLabel lSkin = new JLabel(getUIText("Basic_Skin"));
		pSkin.add(lSkin);

		DefaultComboBoxModel model = new DefaultComboBoxModel();
		for(int i = 0; i < imgBlockSkins.length; i++) {
			model.addElement(new ComboLabel("" + i, new ImageIcon(imgBlockSkins[i])));
		}
		comboboxSkin = new JComboBox(model);
		comboboxSkin.setRenderer(new ComboLabelCellRenderer());
		comboboxSkin.setPreferredSize(new Dimension(190, 30));
		pSkin.add(comboboxSkin);

		// ゴースト
		chkboxGhost = new JCheckBox(getUIText("Basic_Ghost"));
		panelBasic.add(chkboxGhost);

		// フィールド枠外から出現
		chkboxEnterAboveField = new JCheckBox(getUIText("Basic_EnterAboveField"));
		panelBasic.add(chkboxEnterAboveField);

		// 出現予定地が埋まっているときにY-coordinateを上にずらすMaximum count
		JPanel pEnterMaxDistanceY = new JPanel();
		panelBasic.add(pEnterMaxDistanceY);

		JLabel lEnterMaxDistanceY = new JLabel(getUIText("Basic_EnterMaxDistanceY"));
		pEnterMaxDistanceY.add(lEnterMaxDistanceY);

		txtfldEnterMaxDistanceY = new JTextField("", 5);
		pEnterMaxDistanceY.add(txtfldEnterMaxDistanceY);

		// NEXT順生成アルゴリズム
		JPanel pRandomizer = new JPanel();
		panelBasic.add(pRandomizer);

		JLabel lRandomizer = new JLabel(getUIText("Basic_Randomizer"));
		pRandomizer.add(lRandomizer);

		vectorRandomizer = getTextFileVector("config/list/randomizer.lst");
		comboboxRandomizer = new JComboBox(createShortStringVector(vectorRandomizer));
		comboboxRandomizer.setPreferredSize(new Dimension(200, 30));
		pRandomizer.add(comboboxRandomizer);

		btnResetRandomizer = new JButton(getUIText("Basic_ResetRandomizer"));
		btnResetRandomizer.setMnemonic('R');
		btnResetRandomizer.setActionCommand("ResetRandomizer");
		btnResetRandomizer.addActionListener(this);
		pRandomizer.add(btnResetRandomizer);

		// フィールドタブ --------------------------------------------------
		JPanel panelField = new JPanel();
		panelField.setLayout(new BoxLayout(panelField, BoxLayout.Y_AXIS));
		tabPane.addTab(getUIText("TabName_Field"), panelField);

		// フィールドの幅
		JPanel pFieldWidth = new JPanel();
		panelField.add(pFieldWidth);

		JLabel lFieldWidth = new JLabel(getUIText("Field_FieldWidth"));
		pFieldWidth.add(lFieldWidth);

		txtfldFieldWidth = new JTextField("", 5);
		pFieldWidth.add(txtfldFieldWidth);

		// Field height
		JPanel pFieldHeight = new JPanel();
		panelField.add(pFieldHeight);

		JLabel lFieldHeight = new JLabel(getUIText("Field_FieldHeight"));
		pFieldHeight.add(lFieldHeight);

		txtfldFieldHeight = new JTextField("", 5);
		pFieldHeight.add(txtfldFieldHeight);

		// フィールドの見えない部分の高さ
		JPanel pFieldHiddenHeight = new JPanel();
		panelField.add(pFieldHiddenHeight);

		JLabel lFieldHiddenHeight = new JLabel(getUIText("Field_FieldHiddenHeight"));
		pFieldHiddenHeight.add(lFieldHiddenHeight);

		txtfldFieldHiddenHeight = new JTextField("", 5);
		pFieldHiddenHeight.add(txtfldFieldHiddenHeight);

		// フィールドの天井
		chkboxFieldCeiling = new JCheckBox(getUIText("Field_FieldCeiling"));
		panelField.add(chkboxFieldCeiling);

		// フィールド枠内に置けないと死亡
		chkboxFieldLockoutDeath = new JCheckBox(getUIText("Field_FieldLockoutDeath"));
		panelField.add(chkboxFieldLockoutDeath);

		// フィールド枠外にはみ出しただけで死亡
		chkboxFieldPartialLockoutDeath = new JCheckBox(getUIText("Field_FieldPartialLockoutDeath"));
		panelField.add(chkboxFieldPartialLockoutDeath);

		// ホールドタブ --------------------------------------------------
		JPanel panelHold = new JPanel();
		panelHold.setLayout(new BoxLayout(panelHold, BoxLayout.Y_AXIS));
		tabPane.addTab(getUIText("TabName_Hold"), panelHold);

		// ホールド有効
		chkboxHoldEnable = new JCheckBox(getUIText("Hold_HoldEnable"));
		panelHold.add(chkboxHoldEnable);

		// 先行ホールド
		chkboxHoldInitial = new JCheckBox(getUIText("Hold_HoldInitial"));
		panelHold.add(chkboxHoldInitial);

		// 先行ホールド連続使用不可
		chkboxHoldInitialLimit = new JCheckBox(getUIText("Hold_HoldInitialLimit"));
		panelHold.add(chkboxHoldInitialLimit);

		// ホールドを使ったときにBlockピースの向きを初期状態に戻す
		chkboxHoldResetDirection = new JCheckBox(getUIText("Hold_HoldResetDirection"));
		panelHold.add(chkboxHoldResetDirection);

		// ホールドできる count
		JPanel pHoldLimit = new JPanel();
		panelHold.add(pHoldLimit);

		JLabel lHoldLimit = new JLabel(getUIText("Hold_HoldLimit"));
		pHoldLimit.add(lHoldLimit);

		txtfldHoldLimit = new JTextField("", 5);
		pHoldLimit.add(txtfldHoldLimit);

		// ドロップタブ --------------------------------------------------
		JPanel panelDrop = new JPanel();
		panelDrop.setLayout(new BoxLayout(panelDrop, BoxLayout.Y_AXIS));
		tabPane.addTab(getUIText("TabName_Drop"), panelDrop);

		// Hard drop使用可否
		chkboxDropHardDropEnable = new JCheckBox(getUIText("Drop_HardDropEnable"));
		panelDrop.add(chkboxDropHardDropEnable);

		// Hard dropで即固定
		chkboxDropHardDropLock = new JCheckBox(getUIText("Drop_HardDropLock"));
		panelDrop.add(chkboxDropHardDropLock);

		// Hard drop連続使用不可
		chkboxDropHardDropLimit = new JCheckBox(getUIText("Drop_HardDropLimit"));
		panelDrop.add(chkboxDropHardDropLimit);

		// Soft drop使用可否
		chkboxDropSoftDropEnable = new JCheckBox(getUIText("Drop_SoftDropEnable"));
		panelDrop.add(chkboxDropSoftDropEnable);

		// Soft dropで即固定
		chkboxDropSoftDropLock = new JCheckBox(getUIText("Drop_SoftDropLock"));
		panelDrop.add(chkboxDropSoftDropLock);

		// Soft drop連続使用不可
		chkboxDropSoftDropLimit = new JCheckBox(getUIText("Drop_SoftDropLimit"));
		panelDrop.add(chkboxDropSoftDropLimit);

		// 接地状態でSoft dropすると即固定
		chkboxDropSoftDropSurfaceLock = new JCheckBox(getUIText("Drop_SoftDropSurfaceLock"));
		panelDrop.add(chkboxDropSoftDropSurfaceLock);

		// Soft drop速度をCurrent 通常速度×n倍にする
		chkboxDropSoftDropMultiplyNativeSpeed = new JCheckBox(getUIText("Drop_SoftDropMultiplyNativeSpeed"));
		panelDrop.add(chkboxDropSoftDropMultiplyNativeSpeed);

		// Soft drop速度
		JPanel pDropSoftDropSpeed = new JPanel();
		panelDrop.add(pDropSoftDropSpeed);
		JLabel lDropSoftDropSpeed = new JLabel(getUIText("Drop_SoftDropSpeed"));
		pDropSoftDropSpeed.add(lDropSoftDropSpeed);

		txtfldDropSoftDropSpeed = new JTextField("", 5);
		pDropSoftDropSpeed.add(txtfldDropSoftDropSpeed);

		// rotationタブ --------------------------------------------------
		JPanel panelRotate = new JPanel();
		panelRotate.setLayout(new BoxLayout(panelRotate, BoxLayout.Y_AXIS));
		tabPane.addTab(getUIText("TabName_Rotate"), panelRotate);

		// 先行rotation
		chkboxRotateInitial = new JCheckBox(getUIText("Rotate_RotateInitial"));
		panelRotate.add(chkboxRotateInitial);

		// 先行rotation連続使用不可
		chkboxRotateInitialLimit = new JCheckBox(getUIText("Rotate_RotateInitialLimit"));
		panelRotate.add(chkboxRotateInitialLimit);

		// Wallkick
		chkboxRotateWallkick = new JCheckBox(getUIText("Rotate_RotateWallkick"));
		panelRotate.add(chkboxRotateWallkick);

		// 先行rotationでもWallkickする
		chkboxRotateInitialWallkick = new JCheckBox(getUIText("Rotate_RotateInitialWallkick"));
		panelRotate.add(chkboxRotateInitialWallkick);

		// Aで右rotation
		chkboxRotateButtonDefaultRight = new JCheckBox(getUIText("Rotate_RotateButtonDefaultRight"));
		panelRotate.add(chkboxRotateButtonDefaultRight);

		// 逆rotation許可
		chkboxRotateButtonAllowReverse = new JCheckBox(getUIText("Rotate_RotateButtonAllowReverse"));
		panelRotate.add(chkboxRotateButtonAllowReverse);

		// 2rotation許可
		chkboxRotateButtonAllowDouble = new JCheckBox(getUIText("Rotate_RotateButtonAllowDouble"));
		panelRotate.add(chkboxRotateButtonAllowDouble);

		// UpDirectionへWallkickできる count
		JPanel pRotateMaxUpwardWallkick = new JPanel();
		panelRotate.add(pRotateMaxUpwardWallkick);
		JLabel lRotateMaxUpwardWallkick = new JLabel(getUIText("Rotate_RotateMaxUpwardWallkick"));
		pRotateMaxUpwardWallkick.add(lRotateMaxUpwardWallkick);

		txtfldRotateMaxUpwardWallkick = new JTextField("", 5);
		pRotateMaxUpwardWallkick.add(txtfldRotateMaxUpwardWallkick);

		// Wallkickアルゴリズム
		JPanel pWallkickSystem = new JPanel();
		panelRotate.add(pWallkickSystem);

		JLabel lWallkickSystem = new JLabel(getUIText("Rotate_WallkickSystem"));
		pWallkickSystem.add(lWallkickSystem);

		vectorWallkickSystem = getTextFileVector("config/list/wallkick.lst");
		comboboxWallkickSystem = new JComboBox(createShortStringVector(vectorWallkickSystem));
		comboboxWallkickSystem.setPreferredSize(new Dimension(200, 30));
		pWallkickSystem.add(comboboxWallkickSystem);

		btnResetWallkickSystem = new JButton(getUIText("Rotate_ResetWallkickSystem"));
		btnResetWallkickSystem.setMnemonic('R');
		btnResetWallkickSystem.setActionCommand("ResetWallkickSystem");
		btnResetWallkickSystem.addActionListener(this);
		pWallkickSystem.add(btnResetWallkickSystem);

		// 固定 timeタブ --------------------------------------------------
		JPanel panelLockDelay = new JPanel();
		panelLockDelay.setLayout(new BoxLayout(panelLockDelay, BoxLayout.Y_AXIS));
		tabPane.addTab(getUIText("TabName_LockDelay"), panelLockDelay);

		// 最低固定 timeと最高固定 time
		JLabel lLockDelayMin = new JLabel(getUIText("LockDelay_LockDelayMinMax"));
		panelLockDelay.add(lLockDelayMin);

		JPanel pLockDelayMinMax = new JPanel();
		panelLockDelay.add(pLockDelayMinMax);

		txtfldLockDelayMin = new JTextField("", 5);
		pLockDelayMinMax.add(txtfldLockDelayMin);
		txtfldLockDelayMax = new JTextField("", 5);
		pLockDelayMinMax.add(txtfldLockDelayMax);

		// 落下で固定 timeリセット
		chkboxLockDelayLockResetFall = new JCheckBox(getUIText("LockDelay_LockResetFall"));
		panelLockDelay.add(chkboxLockDelayLockResetFall);

		// 移動で固定 timeリセット
		chkboxLockDelayLockResetMove = new JCheckBox(getUIText("LockDelay_LockResetMove"));
		panelLockDelay.add(chkboxLockDelayLockResetMove);

		// rotationで固定 timeリセット
		chkboxLockDelayLockResetRotate = new JCheckBox(getUIText("LockDelay_LockResetRotate"));
		panelLockDelay.add(chkboxLockDelayLockResetRotate);

		// 横移動カウンタとrotationカウンタを共有 (横移動カウンタだけ使う）
		chkboxLockDelayLockResetLimitShareCount = new JCheckBox(getUIText("LockDelay_LockDelayLockResetLimitShareCount"));
		panelLockDelay.add(chkboxLockDelayLockResetLimitShareCount);

		// 横移動 count制限
		JPanel pLockDelayLockResetLimitMove = new JPanel();
		panelLockDelay.add(pLockDelayLockResetLimitMove);
		JLabel lLockDelayLockResetLimitMove = new JLabel(getUIText("LockDelay_LockDelayLockResetLimitMove"));
		pLockDelayLockResetLimitMove.add(lLockDelayLockResetLimitMove);

		txtfldLockDelayLockResetLimitMove = new JTextField("", 5);
		pLockDelayLockResetLimitMove.add(txtfldLockDelayLockResetLimitMove);

		// rotation count制限
		JPanel pLockDelayLockResetLimitRotate = new JPanel();
		panelLockDelay.add(pLockDelayLockResetLimitRotate);
		JLabel lLockDelayLockResetLimitRotate = new JLabel(getUIText("LockDelay_LockDelayLockResetLimitRotate"));
		pLockDelayLockResetLimitRotate.add(lLockDelayLockResetLimitRotate);

		txtfldLockDelayLockResetLimitRotate = new JTextField("", 5);
		pLockDelayLockResetLimitRotate.add(txtfldLockDelayLockResetLimitRotate);

		// 移動またはrotation count制限が超過した時の設定
		JPanel pLockDelayLockResetLimitOver = new JPanel();
		pLockDelayLockResetLimitOver.setLayout(new BoxLayout(pLockDelayLockResetLimitOver, BoxLayout.Y_AXIS));
		panelLockDelay.add(pLockDelayLockResetLimitOver);

		JLabel lLockDelayLockResetLimitOver = new JLabel(getUIText("LockDelay_LockDelayLockResetLimitOver"));
		pLockDelayLockResetLimitOver.add(lLockDelayLockResetLimitOver);

		ButtonGroup gLockDelayLockResetLimitOver = new ButtonGroup();

		radioLockDelayLockResetLimitOverNoReset = new JRadioButton(getUIText("LockDelay_LockDelayLockResetLimitOverNoReset"));
		pLockDelayLockResetLimitOver.add(radioLockDelayLockResetLimitOverNoReset);
		gLockDelayLockResetLimitOver.add(radioLockDelayLockResetLimitOverNoReset);

		radioLockDelayLockResetLimitOverInstant = new JRadioButton(getUIText("LockDelay_LockDelayLockResetLimitOverInstant"));
		pLockDelayLockResetLimitOver.add(radioLockDelayLockResetLimitOverInstant);
		gLockDelayLockResetLimitOver.add(radioLockDelayLockResetLimitOverInstant);

		radioLockDelayLockResetLimitOverNoWallkick = new JRadioButton(getUIText("LockDelay_LockDelayLockResetLimitOverNoWallkick"));
		pLockDelayLockResetLimitOver.add(radioLockDelayLockResetLimitOverNoWallkick);
		gLockDelayLockResetLimitOver.add(radioLockDelayLockResetLimitOverNoWallkick);

		// AREタブ --------------------------------------------------
		JPanel panelARE = new JPanel();
		panelARE.setLayout(new BoxLayout(panelARE, BoxLayout.Y_AXIS));
		tabPane.addTab(getUIText("TabName_ARE"), panelARE);

		// 最低AREと最高ARE
		JLabel lAREMin = new JLabel(getUIText("ARE_MinMax"));
		panelARE.add(lAREMin);

		JPanel pAREMinMax = new JPanel();
		panelARE.add(pAREMinMax);

		txtfldAREMin = new JTextField("", 5);
		pAREMinMax.add(txtfldAREMin);
		txtfldAREMax = new JTextField("", 5);
		pAREMinMax.add(txtfldAREMax);

		// 最低ARE after line clearと最高ARE after line clear
		JLabel lARELineMin = new JLabel(getUIText("ARE_LineMinMax"));
		panelARE.add(lARELineMin);

		JPanel pARELineMinMax = new JPanel();
		panelARE.add(pARELineMinMax);

		txtfldARELineMin = new JTextField("", 5);
		pARELineMinMax.add(txtfldARELineMin);
		txtfldARELineMax = new JTextField("", 5);
		pARELineMinMax.add(txtfldARELineMax);

		// 固定した瞬間に光る frame count
		JLabel lARELockFlash = new JLabel(getUIText("ARE_LockFlash"));
		panelARE.add(lARELockFlash);

		JPanel pARELockFlash = new JPanel();
		panelARE.add(pARELockFlash);

		txtfldARELockFlash = new JTextField("", 5);
		pARELockFlash.add(txtfldARELockFlash);

		// Blockが光る専用 frame を入れる
		chkboxARELockFlashOnlyFrame = new JCheckBox(getUIText("ARE_LockFlashOnlyFrame"));
		panelARE.add(chkboxARELockFlashOnlyFrame);

		// Line clear前にBlockが光る frame を入れる
		chkboxARELockFlashBeforeLineClear = new JCheckBox(getUIText("ARE_LockFlashBeforeLineClear"));
		panelARE.add(chkboxARELockFlashBeforeLineClear);

		// ARE cancel on move
		chkboxARECancelMove = new JCheckBox(getUIText("ARE_CancelMove"));
		panelARE.add(chkboxARECancelMove);

		// ARE cancel on move
		chkboxARECancelRotate = new JCheckBox(getUIText("ARE_CancelRotate"));
		panelARE.add(chkboxARECancelRotate);

		// ARE cancel on move
		chkboxARECancelHold = new JCheckBox(getUIText("ARE_CancelHold"));
		panelARE.add(chkboxARECancelHold);

		// Line clearタブ --------------------------------------------------
		JPanel panelLine = new JPanel();
		panelLine.setLayout(new BoxLayout(panelLine, BoxLayout.Y_AXIS));
		tabPane.addTab(getUIText("TabName_Line"), panelLine);

		// 最低Line clear timeと最高Line clear time
		JLabel lLineMin = new JLabel(getUIText("Line_MinMax"));
		panelLine.add(lLineMin);

		JPanel pLineMinMax = new JPanel();
		panelLine.add(pLineMinMax);

		txtfldLineDelayMin = new JTextField("", 5);
		pLineMinMax.add(txtfldLineDelayMin);
		txtfldLineDelayMax = new JTextField("", 5);
		pLineMinMax.add(txtfldLineDelayMax);

		// 落下アニメ
		chkboxLineFallAnim = new JCheckBox(getUIText("Line_FallAnim"));
		panelLine.add(chkboxLineFallAnim);

		// Line delay cancel on move
		chkboxLineCancelMove = new JCheckBox(getUIText("Line_CancelMove"));
		panelLine.add(chkboxLineCancelMove);

		// Line delay cancel on rotate
		chkboxLineCancelRotate = new JCheckBox(getUIText("Line_CancelRotate"));
		panelLine.add(chkboxLineCancelRotate);

		// Line delay cancel on hold
		chkboxLineCancelHold = new JCheckBox(getUIText("Line_CancelHold"));
		panelLine.add(chkboxLineCancelHold);

		// 移動タブ --------------------------------------------------
		JPanel panelMove = new JPanel();
		panelMove.setLayout(new BoxLayout(panelMove, BoxLayout.Y_AXIS));
		tabPane.addTab(getUIText("TabName_Move"), panelMove);

		// 最低横溜め timeと最高横溜め time
		JLabel lMoveDASMin = new JLabel(getUIText("Move_DASMinMax"));
		panelMove.add(lMoveDASMin);

		JPanel pMoveDASMinMax = new JPanel();
		panelMove.add(pMoveDASMinMax);

		txtfldMoveDASMin = new JTextField("", 5);
		pMoveDASMinMax.add(txtfldMoveDASMin);
		txtfldMoveDASMax = new JTextField("", 5);
		pMoveDASMinMax.add(txtfldMoveDASMax);

		// 横移動間隔
		JPanel pMoveDASDelay = new JPanel();
		panelMove.add(pMoveDASDelay);

		JLabel lMoveDASDelay1 = new JLabel(getUIText("Move_DASDelay1"));
		pMoveDASDelay.add(lMoveDASDelay1);

		txtfldMoveDASDelay = new JTextField("", 5);
		pMoveDASDelay.add(txtfldMoveDASDelay);

		JLabel lMoveDASDelay2 = new JLabel(getUIText("Move_DASDelay2"));
		pMoveDASDelay.add(lMoveDASDelay2);

		// ○○のとき横溜め可能
		chkboxMoveDASInReady = new JCheckBox(getUIText("Move_DASInReady"));
		panelMove.add(chkboxMoveDASInReady);
		chkboxMoveDASInMoveFirstFrame = new JCheckBox(getUIText("Move_DASInMoveFirstFrame"));
		panelMove.add(chkboxMoveDASInMoveFirstFrame);
		chkboxMoveDASInLockFlash = new JCheckBox(getUIText("Move_DASInLockFlash"));
		panelMove.add(chkboxMoveDASInLockFlash);
		chkboxMoveDASInLineClear = new JCheckBox(getUIText("Move_DASInLineClear"));
		panelMove.add(chkboxMoveDASInLineClear);
		chkboxMoveDASInARE = new JCheckBox(getUIText("Move_DASInARE"));
		panelMove.add(chkboxMoveDASInARE);
		chkboxMoveDASInARELastFrame = new JCheckBox(getUIText("Move_DASInARELastFrame"));
		panelMove.add(chkboxMoveDASInARELastFrame);
		chkboxMoveDASInEndingStart = new JCheckBox(getUIText("Move_DASInEndingStart"));
		panelMove.add(chkboxMoveDASInEndingStart);
		chkboxMoveDASChargeOnBlockedMove = new JCheckBox(getUIText("Move_DASChargeOnBlockedMove"));
		panelMove.add(chkboxMoveDASChargeOnBlockedMove);
		chkboxMoveDASStoreChargeOnNeutral = new JCheckBox(getUIText("Move_DASStoreChargeOnNeutral"));
      panelMove.add(chkboxMoveDASStoreChargeOnNeutral);
      chkboxMoveDASRedirectInDelay = new JCheckBox(getUIText("Move_DASRedirectInDelay"));
      panelMove.add(chkboxMoveDASRedirectInDelay);

		// 最初の frame に移動可能
		chkboxMoveFirstFrame = new JCheckBox(getUIText("Move_FirstFrame"));
		panelMove.add(chkboxMoveFirstFrame);

		// 斜め移動
		chkboxMoveDiagonal = new JCheckBox(getUIText("Move_Diagonal"));
		panelMove.add(chkboxMoveDiagonal);

		// Up下同時押し
		chkboxMoveUpAndDown = new JCheckBox(getUIText("Move_UpAndDown"));
		panelMove.add(chkboxMoveUpAndDown);

		// 左右同時押し
		chkboxMoveLeftAndRightAllow = new JCheckBox(getUIText("Move_LeftAndRightAllow"));
		panelMove.add(chkboxMoveLeftAndRightAllow);

		// 左右同時押ししたときに前 frame の input を優先
		chkboxMoveLeftAndRightUsePreviousInput = new JCheckBox(getUIText("Move_LeftAndRightUsePreviousInput"));
		panelMove.add(chkboxMoveLeftAndRightUsePreviousInput);

		// Shift lock
		chkboxMoveShiftLockEnable = new JCheckBox(getUIText("Move_ShiftLock"));
		panelMove.add(chkboxMoveShiftLockEnable);

		// rotationパターン補正タブ ------------------------------------------------
		JPanel panelPieceOffset = new JPanel();
		panelPieceOffset.setLayout(new BoxLayout(panelPieceOffset, BoxLayout.Y_AXIS));
		tabPane.addTab(getUIText("TabName_PieceOffset"), panelPieceOffset);

		tabPieceOffset = new JTabbedPane();
		panelPieceOffset.add(tabPieceOffset);

		// rotationパターン補正(X)タブ --------------------------------------------------
		JPanel panelPieceOffsetX = new JPanel();
		panelPieceOffsetX.setLayout(new BoxLayout(panelPieceOffsetX, BoxLayout.Y_AXIS));
		tabPieceOffset.addTab(getUIText("TabName_PieceOffsetX"), panelPieceOffsetX);

		JPanel[] pPieceOffsetX = new JPanel[Piece.PIECE_COUNT];

		txtfldPieceOffsetX = new JTextField[Piece.PIECE_COUNT][Piece.DIRECTION_COUNT];
		for(int i = 0; i < Piece.PIECE_COUNT; i++) {
			pPieceOffsetX[i] = new JPanel();
			panelPieceOffsetX.add(pPieceOffsetX[i]);

			JLabel lPieceName = new JLabel(getUIText("PieceName" + i));
			pPieceOffsetX[i].add(lPieceName);

			for(int j = 0; j < Piece.DIRECTION_COUNT; j++) {
				txtfldPieceOffsetX[i][j] = new JTextField("", 5);
				pPieceOffsetX[i].add(txtfldPieceOffsetX[i][j]);
			}
		}

		// rotationパターン補正(Y)タブ --------------------------------------------------
		JPanel panelPieceOffsetY = new JPanel();
		panelPieceOffsetY.setLayout(new BoxLayout(panelPieceOffsetY, BoxLayout.Y_AXIS));
		tabPieceOffset.addTab(getUIText("TabName_PieceOffsetY"), panelPieceOffsetY);

		JPanel[] pPieceOffsetY = new JPanel[Piece.PIECE_COUNT];

		txtfldPieceOffsetY = new JTextField[Piece.PIECE_COUNT][Piece.DIRECTION_COUNT];
		for(int i = 0; i < Piece.PIECE_COUNT; i++) {
			pPieceOffsetY[i] = new JPanel();
			panelPieceOffsetY.add(pPieceOffsetY[i]);

			JLabel lPieceName = new JLabel(getUIText("PieceName" + i));
			pPieceOffsetY[i].add(lPieceName);

			for(int j = 0; j < Piece.DIRECTION_COUNT; j++) {
				txtfldPieceOffsetY[i][j] = new JTextField("", 5);
				pPieceOffsetY[i].add(txtfldPieceOffsetY[i][j]);
			}
		}

		// 出現位置補正タブ ------------------------------------------------
		JPanel panelPieceSpawn = new JPanel();
		panelPieceSpawn.setLayout(new BoxLayout(panelPieceSpawn, BoxLayout.Y_AXIS));
		tabPane.addTab(getUIText("TabName_PieceSpawn"), panelPieceSpawn);

		tabPieceSpawn = new JTabbedPane();
		panelPieceSpawn.add(tabPieceSpawn);

		// 出現位置補正(X)タブ --------------------------------------------------
		JPanel panelPieceSpawnX = new JPanel();
		panelPieceSpawnX.setLayout(new BoxLayout(panelPieceSpawnX, BoxLayout.Y_AXIS));
		tabPieceSpawn.addTab(getUIText("TabName_PieceSpawnX"), panelPieceSpawnX);

		JPanel[] pPieceSpawnX = new JPanel[Piece.PIECE_COUNT];

		txtfldPieceSpawnX = new JTextField[Piece.PIECE_COUNT][Piece.DIRECTION_COUNT];
		for(int i = 0; i < Piece.PIECE_COUNT; i++) {
			pPieceSpawnX[i] = new JPanel();
			panelPieceSpawnX.add(pPieceSpawnX[i]);

			JLabel lPieceName = new JLabel(getUIText("PieceName" + i));
			pPieceSpawnX[i].add(lPieceName);

			for(int j = 0; j < Piece.DIRECTION_COUNT; j++) {
				txtfldPieceSpawnX[i][j] = new JTextField("", 5);
				pPieceSpawnX[i].add(txtfldPieceSpawnX[i][j]);
			}
		}

		// 出現位置補正(Y)タブ --------------------------------------------------
		JPanel panelPieceSpawnY = new JPanel();
		panelPieceSpawnY.setLayout(new BoxLayout(panelPieceSpawnY, BoxLayout.Y_AXIS));
		tabPieceSpawn.addTab(getUIText("TabName_PieceSpawnY"), panelPieceSpawnY);

		JPanel[] pPieceSpawnY = new JPanel[Piece.PIECE_COUNT];

		txtfldPieceSpawnY = new JTextField[Piece.PIECE_COUNT][Piece.DIRECTION_COUNT];
		for(int i = 0; i < Piece.PIECE_COUNT; i++) {
			pPieceSpawnY[i] = new JPanel();
			panelPieceSpawnY.add(pPieceSpawnY[i]);

			JLabel lPieceName = new JLabel(getUIText("PieceName" + i));
			pPieceSpawnY[i].add(lPieceName);

			for(int j = 0; j < Piece.DIRECTION_COUNT; j++) {
				txtfldPieceSpawnY[i][j] = new JTextField("", 5);
				pPieceSpawnY[i].add(txtfldPieceSpawnY[i][j]);
			}
		}

		// Big時出現位置補正(X)タブ --------------------------------------------------
		JPanel panelPieceSpawnBigX = new JPanel();
		panelPieceSpawnBigX.setLayout(new BoxLayout(panelPieceSpawnBigX, BoxLayout.Y_AXIS));
		tabPieceSpawn.addTab(getUIText("TabName_PieceSpawnBigX"), panelPieceSpawnBigX);

		JPanel[] pPieceSpawnBigX = new JPanel[Piece.PIECE_COUNT];

		txtfldPieceSpawnBigX = new JTextField[Piece.PIECE_COUNT][Piece.DIRECTION_COUNT];
		for(int i = 0; i < Piece.PIECE_COUNT; i++) {
			pPieceSpawnBigX[i] = new JPanel();
			panelPieceSpawnBigX.add(pPieceSpawnBigX[i]);

			JLabel lPieceName = new JLabel(getUIText("PieceName" + i));
			pPieceSpawnBigX[i].add(lPieceName);

			for(int j = 0; j < Piece.DIRECTION_COUNT; j++) {
				txtfldPieceSpawnBigX[i][j] = new JTextField("", 5);
				pPieceSpawnBigX[i].add(txtfldPieceSpawnBigX[i][j]);
			}
		}

		// Big時出現位置補正(Y)タブ --------------------------------------------------
		JPanel panelPieceSpawnBigY = new JPanel();
		panelPieceSpawnBigY.setLayout(new BoxLayout(panelPieceSpawnBigY, BoxLayout.Y_AXIS));
		tabPieceSpawn.addTab(getUIText("TabName_PieceSpawnBigY"), panelPieceSpawnBigY);

		JPanel[] pPieceSpawnBigY = new JPanel[Piece.PIECE_COUNT];

		txtfldPieceSpawnBigY = new JTextField[Piece.PIECE_COUNT][Piece.DIRECTION_COUNT];
		for(int i = 0; i < Piece.PIECE_COUNT; i++) {
			pPieceSpawnBigY[i] = new JPanel();
			panelPieceSpawnBigY.add(pPieceSpawnBigY[i]);

			JLabel lPieceName = new JLabel(getUIText("PieceName" + i));
			pPieceSpawnBigY[i].add(lPieceName);

			for(int j = 0; j < Piece.DIRECTION_COUNT; j++) {
				txtfldPieceSpawnBigY[i][j] = new JTextField("", 5);
				pPieceSpawnBigY[i].add(txtfldPieceSpawnBigY[i][j]);
			}
		}

		// 色設定タブ --------------------------------------------------
		JPanel panelPieceColor = new JPanel();
		panelPieceColor.setLayout(new BoxLayout(panelPieceColor, BoxLayout.Y_AXIS));
		tabPane.addTab(getUIText("TabName_PieceColor"), panelPieceColor);

		String[] strColorNames = new String[Block.BLOCK_COLOR_COUNT - 1];
		for(int i = 0; i < strColorNames.length; i++) strColorNames[i] = getUIText("ColorName" + i);

		JPanel[] pPieceColor = new JPanel[Piece.PIECE_COUNT];

		comboboxPieceColor = new JComboBox[Piece.PIECE_COUNT];
		for(int i = 0; i < Piece.PIECE_COUNT; i++) {
			pPieceColor[i] = new JPanel();
			panelPieceColor.add(pPieceColor[i]);

			JLabel lPieceName = new JLabel(getUIText("PieceName" + i));
			pPieceColor[i].add(lPieceName);

			comboboxPieceColor[i] = new JComboBox(strColorNames);
			comboboxPieceColor[i].setPreferredSize(new Dimension(100, 30));
			comboboxPieceColor[i].setMaximumRowCount(strColorNames.length);
			pPieceColor[i].add(comboboxPieceColor[i]);
		}

		// 初期Direction設定タブ --------------------------------------------------
		JPanel panelPieceDirection = new JPanel();
		panelPieceDirection.setLayout(new BoxLayout(panelPieceDirection, BoxLayout.Y_AXIS));
		tabPane.addTab(getUIText("TabName_PieceDirection"), panelPieceDirection);

		String[] strDirectionNames = new String[Piece.DIRECTION_COUNT + 1];
		for(int i = 0; i < strDirectionNames.length; i++) strDirectionNames[i] = getUIText("DirectionName" + i);

		JPanel[] pPieceDirection = new JPanel[Piece.PIECE_COUNT];

		comboboxPieceDirection = new JComboBox[Piece.PIECE_COUNT];
		for(int i = 0; i < Piece.PIECE_COUNT; i++) {
			pPieceDirection[i] = new JPanel();
			panelPieceDirection.add(pPieceDirection[i]);

			JLabel lPieceName = new JLabel(getUIText("PieceName" + i));
			pPieceDirection[i].add(lPieceName);

			comboboxPieceDirection[i] = new JComboBox(strDirectionNames);
			comboboxPieceDirection[i].setPreferredSize(new Dimension(150, 30));
			comboboxPieceDirection[i].setMaximumRowCount(strDirectionNames.length);
			pPieceDirection[i].add(comboboxPieceDirection[i]);
		}
	}

	/**
	 * Block画像を読み込み
	 */
	private void loadBlockSkins() {
		String skindir = propConfig.getProperty("custom.skin.directory", "res");

		BufferedImage imgBlock = loadImage(getURL(skindir + "/graphics/block.png"));
		int numSkins = imgBlock.getHeight() / 16;

		imgBlockSkins = new BufferedImage[numSkins];

		for(int i = 0; i < numSkins; i++) {
			imgBlockSkins[i] = new BufferedImage(144, 16, BufferedImage.TYPE_INT_RGB);
			imgBlockSkins[i].getGraphics().drawImage(imgBlock, 0, 0, 144, 16, 0, i * 16, 144, (i * 16) + 16, null);
		}
	}

	/**
	 * 画像を読み込み
	 * @param url 画像ファイルのURL
	 * @return 画像ファイル (失敗するとnull）
	 */
	public BufferedImage loadImage(URL url) {
		BufferedImage img = null;
		try {
			img = ImageIO.read(url);
			log.debug("Loaded image from " + url);
		} catch (IOException e) {
			log.error("Failed to load image from " + url, e);
		}
		return img;
	}

	/**
	 * リソースファイルのURLを返す
	 * @param str Filename
	 * @return リソースファイルのURL
	 */
	public URL getURL(String str) {
		URL url = null;

		try {
			char sep = File.separator.charAt(0);
			String file = str.replace(sep, '/');

			// 参考：http://www.asahi-net.or.jp/~DP8T-ASM/java/tips/HowToMakeURL.html
			if(file.charAt(0) != '/') {
				String dir = System.getProperty("user.dir");
				dir = dir.replace(sep, '/') + '/';
				if(dir.charAt(0) != '/') {
					dir = "/" + dir;
				}
				file = dir + file;
			}
			url = new URL("file", "", file);
		} catch(MalformedURLException e) {
			log.warn("Invalid URL:" + str, e);
			return null;
		}

		return url;
	}

	/**
	 * テキストファイルを読み込んでVector&lt;String&gt;に入れる
	 * @param filename Filename
	 * @return テキストファイルを読み込んだVector&lt;String&gt;
	 */
	public Vector<String> getTextFileVector(String filename) {
		Vector<String> vec = new Vector<String>();

		try {
			BufferedReader in = new BufferedReader(new FileReader(filename));

			while(true) {
				String str = in.readLine();
				if((str == null) || (str.length() <= 0)) break;
				vec.add(str);
			}
		} catch (IOException e) {}

		return vec;
	}

	/**
	 * 特定のVector&lt;String&gt;の最後のドット記号から先だけを取り出したVector&lt;String&gt;を作成
	 * @param vecSrc 元のVector&lt;String&gt;
	 * @return 加工したVector&lt;String&gt;
	 */
	public Vector<String> createShortStringVector(Vector<String> vecSrc) {
		Vector<String> vec = new Vector<String>();

		for(int i = 0; i < vecSrc.size(); i++) {
			String str = vecSrc.get(i);
			int last = str.lastIndexOf('.');

			String newStr = "";
			if(last != -1) {
				newStr = str.substring(last + 1);
			} else {
				newStr = str;
			}

			vec.add(newStr);
		}

		return vec;
	}

	/**
	 * ルール設定をUIに反映させる
	 * @param r ルール設定
	 */
	public void readRuleToUI(RuleOptions r) {
		txtfldRuleName.setText(String.valueOf(r.strRuleName));
		txtfldNextDisplay.setText(String.valueOf(r.nextDisplay));
		comboboxSkin.setSelectedIndex(r.skin);
		chkboxGhost.setSelected(r.ghost);
		chkboxEnterAboveField.setSelected(r.pieceEnterAboveField);
		txtfldEnterMaxDistanceY.setText(String.valueOf(r.pieceEnterMaxDistanceY));
		int indexRandomizer = vectorRandomizer.indexOf(r.strRandomizer);
		comboboxRandomizer.setSelectedIndex(indexRandomizer);

		txtfldFieldWidth.setText(String.valueOf(r.fieldWidth));
		txtfldFieldHeight.setText(String.valueOf(r.fieldHeight));
		txtfldFieldHiddenHeight.setText(String.valueOf(r.fieldHiddenHeight));
		chkboxFieldCeiling.setSelected(r.fieldCeiling);
		chkboxFieldLockoutDeath.setSelected(r.fieldLockoutDeath);
		chkboxFieldPartialLockoutDeath.setSelected(r.fieldPartialLockoutDeath);

		chkboxHoldEnable.setSelected(r.holdEnable);
		chkboxHoldInitial.setSelected(r.holdInitial);
		chkboxHoldInitialLimit.setSelected(r.holdInitialLimit);
		chkboxHoldResetDirection.setSelected(r.holdResetDirection);
		txtfldHoldLimit.setText(String.valueOf(r.holdLimit));

		chkboxDropHardDropEnable.setSelected(r.harddropEnable);
		chkboxDropHardDropLock.setSelected(r.harddropLock);
		chkboxDropHardDropLimit.setSelected(r.harddropLimit);
		chkboxDropSoftDropEnable.setSelected(r.softdropEnable);
		chkboxDropSoftDropLock.setSelected(r.softdropLock);
		chkboxDropSoftDropLimit.setSelected(r.softdropLimit);
		chkboxDropSoftDropSurfaceLock.setSelected(r.softdropSurfaceLock);
		txtfldDropSoftDropSpeed.setText(String.valueOf(r.softdropSpeed));
		chkboxDropSoftDropMultiplyNativeSpeed.setSelected(r.softdropMultiplyNativeSpeed);

		chkboxRotateInitial.setSelected(r.rotateInitial);
		chkboxRotateInitialLimit.setSelected(r.rotateInitialLimit);
		chkboxRotateWallkick.setSelected(r.rotateWallkick);
		chkboxRotateInitialWallkick.setSelected(r.rotateInitialWallkick);
		txtfldRotateMaxUpwardWallkick.setText(String.valueOf(r.rotateMaxUpwardWallkick));
		chkboxRotateButtonDefaultRight.setSelected(r.rotateButtonDefaultRight);
		chkboxRotateButtonAllowReverse.setSelected(r.rotateButtonAllowReverse);
		chkboxRotateButtonAllowDouble.setSelected(r.rotateButtonAllowDouble);
		int indexWallkick = vectorWallkickSystem.indexOf(r.strWallkick);
		comboboxWallkickSystem.setSelectedIndex(indexWallkick);

		txtfldLockDelayMin.setText(String.valueOf(r.minLockDelay));
		txtfldLockDelayMax.setText(String.valueOf(r.maxLockDelay));
		chkboxLockDelayLockResetFall.setSelected(r.lockresetFall);
		chkboxLockDelayLockResetMove.setSelected(r.lockresetMove);
		chkboxLockDelayLockResetRotate.setSelected(r.lockresetRotate);
		chkboxLockDelayLockResetLimitShareCount.setSelected(r.lockresetLimitShareCount);
		txtfldLockDelayLockResetLimitMove.setText(String.valueOf(r.lockresetLimitMove));
		txtfldLockDelayLockResetLimitRotate.setText(String.valueOf(r.lockresetLimitRotate));
		if(r.lockresetLimitOver == RuleOptions.LOCKRESET_LIMIT_OVER_NORESET)
			radioLockDelayLockResetLimitOverNoReset.setSelected(true);
		else if(r.lockresetLimitOver == RuleOptions.LOCKRESET_LIMIT_OVER_INSTANT)
			radioLockDelayLockResetLimitOverInstant.setSelected(true);
		else if(r.lockresetLimitOver == RuleOptions.LOCKRESET_LIMIT_OVER_NOWALLKICK)
			radioLockDelayLockResetLimitOverNoWallkick.setSelected(true);

		txtfldAREMin.setText(String.valueOf(r.minARE));
		txtfldAREMax.setText(String.valueOf(r.maxARE));
		txtfldARELineMin.setText(String.valueOf(r.minARELine));
		txtfldARELineMax.setText(String.valueOf(r.maxARELine));
		txtfldARELockFlash.setText(String.valueOf(r.lockflash));
		chkboxARELockFlashOnlyFrame.setSelected(r.lockflashOnlyFrame);
		chkboxARELockFlashBeforeLineClear.setSelected(r.lockflashBeforeLineClear);
		chkboxARECancelMove.setSelected(r.areCancelMove);
		chkboxARECancelRotate.setSelected(r.areCancelRotate);
		chkboxARECancelHold.setSelected(r.areCancelHold);

		txtfldLineDelayMin.setText(String.valueOf(r.minLineDelay));
		txtfldLineDelayMax.setText(String.valueOf(r.maxLineDelay));
		chkboxLineFallAnim.setSelected(r.lineFallAnim);
		chkboxLineCancelMove.setSelected(r.lineCancelMove);
		chkboxLineCancelRotate.setSelected(r.lineCancelRotate);
		chkboxLineCancelHold.setSelected(r.lineCancelHold);

		txtfldMoveDASMin.setText(String.valueOf(r.minDAS));
		txtfldMoveDASMax.setText(String.valueOf(r.maxDAS));
		txtfldMoveDASDelay.setText(String.valueOf(r.dasDelay));
		chkboxMoveDASInReady.setSelected(r.dasInReady);
		chkboxMoveDASInMoveFirstFrame.setSelected(r.dasInMoveFirstFrame);
		chkboxMoveDASInLockFlash.setSelected(r.dasInLockFlash);
		chkboxMoveDASInLineClear.setSelected(r.dasInLineClear);
		chkboxMoveDASInARE.setSelected(r.dasInARE);
		chkboxMoveDASInARELastFrame.setSelected(r.dasInARELastFrame);
		chkboxMoveDASInEndingStart.setSelected(r.dasInEndingStart);
		chkboxMoveDASChargeOnBlockedMove.setSelected(r.dasChargeOnBlockedMove);
		chkboxMoveDASStoreChargeOnNeutral.setSelected(r.dasStoreChargeOnNeutral);
		chkboxMoveDASRedirectInDelay.setSelected(r.dasRedirectInDelay);
		chkboxMoveFirstFrame.setSelected(r.moveFirstFrame);
		chkboxMoveDiagonal.setSelected(r.moveDiagonal);
		chkboxMoveUpAndDown.setSelected(r.moveUpAndDown);
		chkboxMoveLeftAndRightAllow.setSelected(r.moveLeftAndRightAllow);
		chkboxMoveLeftAndRightUsePreviousInput.setSelected(r.moveLeftAndRightUsePreviousInput);
		chkboxMoveShiftLockEnable.setSelected(r.shiftLockEnable);

		for(int i = 0; i < Piece.PIECE_COUNT; i++) {
			for(int j = 0; j < Piece.DIRECTION_COUNT; j++) {
				txtfldPieceOffsetX[i][j].setText(String.valueOf(r.pieceOffsetX[i][j]));
				txtfldPieceOffsetY[i][j].setText(String.valueOf(r.pieceOffsetY[i][j]));
				txtfldPieceSpawnX[i][j].setText(String.valueOf(r.pieceSpawnX[i][j]));
				txtfldPieceSpawnY[i][j].setText(String.valueOf(r.pieceSpawnY[i][j]));
				txtfldPieceSpawnBigX[i][j].setText(String.valueOf(r.pieceSpawnXBig[i][j]));
				txtfldPieceSpawnBigY[i][j].setText(String.valueOf(r.pieceSpawnYBig[i][j]));
			}
			comboboxPieceColor[i].setSelectedIndex(r.pieceColor[i] - 1);
			comboboxPieceDirection[i].setSelectedIndex(r.pieceDefaultDirection[i]);
		}
	}

	/**
	 * ルール設定をUIから書き込む
	 * @param r ルール設定
	 */
	public void writeRuleFromUI(RuleOptions r) {
		r.strRuleName = txtfldRuleName.getText();
		r.nextDisplay = getIntTextField(txtfldNextDisplay);
		r.skin = comboboxSkin.getSelectedIndex();
		r.ghost = chkboxGhost.isSelected();
		r.pieceEnterAboveField = chkboxEnterAboveField.isSelected();
		r.pieceEnterMaxDistanceY = getIntTextField(txtfldEnterMaxDistanceY);
		int indexRandomizer = comboboxRandomizer.getSelectedIndex();
		if(indexRandomizer >= 0) r.strRandomizer = vectorRandomizer.get(indexRandomizer);
		else r.strRandomizer = "";

		r.fieldWidth = getIntTextField(txtfldFieldWidth);
		r.fieldHeight = getIntTextField(txtfldFieldHeight);
		r.fieldHiddenHeight = getIntTextField(txtfldFieldHiddenHeight);
		r.fieldCeiling = chkboxFieldCeiling.isSelected();
		r.fieldLockoutDeath = chkboxFieldLockoutDeath.isSelected();
		r.fieldPartialLockoutDeath = chkboxFieldPartialLockoutDeath.isSelected();

		r.holdEnable = chkboxHoldEnable.isSelected();
		r.holdInitial = chkboxHoldInitial.isSelected();
		r.holdInitialLimit = chkboxHoldInitialLimit.isSelected();
		r.holdResetDirection = chkboxHoldResetDirection.isSelected();
		r.holdLimit = getIntTextField(txtfldHoldLimit);

		r.harddropEnable = chkboxDropHardDropEnable.isSelected();
		r.harddropLock = chkboxDropHardDropLock.isSelected();
		r.harddropLimit = chkboxDropHardDropLimit.isSelected();
		r.softdropEnable = chkboxDropSoftDropEnable.isSelected();
		r.softdropLock = chkboxDropSoftDropLock.isSelected();
		r.softdropLimit = chkboxDropSoftDropLimit.isSelected();
		r.softdropSurfaceLock = chkboxDropSoftDropSurfaceLock.isSelected();
		r.softdropSpeed = getFloatTextField(txtfldDropSoftDropSpeed);
		r.softdropMultiplyNativeSpeed = chkboxDropSoftDropMultiplyNativeSpeed.isSelected();

		r.rotateInitial = chkboxRotateInitial.isSelected();
		r.rotateInitialLimit = chkboxRotateInitialLimit.isSelected();
		r.rotateWallkick = chkboxRotateWallkick.isSelected();
		r.rotateInitialWallkick = chkboxRotateInitialWallkick.isSelected();
		r.rotateMaxUpwardWallkick = getIntTextField(txtfldRotateMaxUpwardWallkick);
		r.rotateButtonDefaultRight = chkboxRotateButtonDefaultRight.isSelected();
		r.rotateButtonAllowReverse = chkboxRotateButtonAllowReverse.isSelected();
		r.rotateButtonAllowDouble = chkboxRotateButtonAllowDouble.isSelected();
		int indexWallkick = comboboxWallkickSystem.getSelectedIndex();
		if(indexWallkick >= 0) r.strWallkick = vectorWallkickSystem.get(indexWallkick);
		else r.strWallkick = "";

		r.minLockDelay = getIntTextField(txtfldLockDelayMin);
		r.maxLockDelay = getIntTextField(txtfldLockDelayMax);
		r.lockresetFall = chkboxLockDelayLockResetFall.isSelected();
		r.lockresetMove = chkboxLockDelayLockResetMove.isSelected();
		r.lockresetRotate = chkboxLockDelayLockResetRotate.isSelected();
		r.lockresetLimitShareCount = chkboxLockDelayLockResetLimitShareCount.isSelected();
		r.lockresetLimitMove = getIntTextField(txtfldLockDelayLockResetLimitMove);
		r.lockresetLimitRotate = getIntTextField(txtfldLockDelayLockResetLimitRotate);
		if(radioLockDelayLockResetLimitOverNoReset.isSelected()) r.lockresetLimitOver = RuleOptions.LOCKRESET_LIMIT_OVER_NORESET;
		if(radioLockDelayLockResetLimitOverInstant.isSelected()) r.lockresetLimitOver = RuleOptions.LOCKRESET_LIMIT_OVER_INSTANT;
		if(radioLockDelayLockResetLimitOverNoWallkick.isSelected()) r.lockresetLimitOver = RuleOptions.LOCKRESET_LIMIT_OVER_NOWALLKICK;

		r.minARE = getIntTextField(txtfldAREMin);
		r.maxARE = getIntTextField(txtfldAREMax);
		r.minARELine = getIntTextField(txtfldARELineMin);
		r.maxARELine = getIntTextField(txtfldARELineMax);
		r.lockflash = getIntTextField(txtfldARELockFlash);
		r.lockflashOnlyFrame = chkboxARELockFlashOnlyFrame.isSelected();
		r.lockflashBeforeLineClear = chkboxARELockFlashBeforeLineClear.isSelected();
		r.areCancelMove = chkboxARECancelMove.isSelected();
		r.areCancelRotate = chkboxARECancelRotate.isSelected();
		r.areCancelHold = chkboxARECancelHold.isSelected();

		r.minLineDelay = getIntTextField(txtfldLineDelayMin);
		r.maxLineDelay = getIntTextField(txtfldLineDelayMax);
		r.lineFallAnim = chkboxLineFallAnim.isSelected();
		r.lineCancelMove = chkboxLineCancelMove.isSelected();
		r.lineCancelRotate = chkboxLineCancelRotate.isSelected();
		r.lineCancelHold = chkboxLineCancelHold.isSelected();

		r.minDAS = getIntTextField(txtfldMoveDASMin);
		r.maxDAS = getIntTextField(txtfldMoveDASMax);
		r.dasDelay = getIntTextField(txtfldMoveDASDelay);
		r.dasInReady = chkboxMoveDASInReady.isSelected();
		r.dasInMoveFirstFrame = chkboxMoveDASInMoveFirstFrame.isSelected();
		r.dasInLockFlash = chkboxMoveDASInLockFlash.isSelected();
		r.dasInLineClear = chkboxMoveDASInLineClear.isSelected();
		r.dasInARE = chkboxMoveDASInARE.isSelected();
		r.dasInARELastFrame = chkboxMoveDASInARELastFrame.isSelected();
		r.dasInEndingStart = chkboxMoveDASInEndingStart.isSelected();
		r.dasChargeOnBlockedMove = chkboxMoveDASChargeOnBlockedMove.isSelected();
		r.dasStoreChargeOnNeutral = chkboxMoveDASStoreChargeOnNeutral.isSelected();
		r.dasRedirectInDelay = chkboxMoveDASRedirectInDelay.isSelected();
		r.moveFirstFrame = chkboxMoveFirstFrame.isSelected();
		r.moveDiagonal = chkboxMoveDiagonal.isSelected();
		r.moveUpAndDown = chkboxMoveUpAndDown.isSelected();
		r.moveLeftAndRightAllow = chkboxMoveLeftAndRightAllow.isSelected();
		r.moveLeftAndRightUsePreviousInput = chkboxMoveLeftAndRightUsePreviousInput.isSelected();
		r.shiftLockEnable = chkboxMoveShiftLockEnable.isSelected();

		for(int i = 0; i < Piece.PIECE_COUNT; i++) {
			for(int j = 0; j < Piece.DIRECTION_COUNT; j++) {
				r.pieceOffsetX[i][j] = getIntTextField(txtfldPieceOffsetX[i][j]);
				r.pieceOffsetY[i][j] = getIntTextField(txtfldPieceOffsetY[i][j]);
				r.pieceSpawnX[i][j] = getIntTextField(txtfldPieceSpawnX[i][j]);
				r.pieceSpawnY[i][j] = getIntTextField(txtfldPieceSpawnY[i][j]);
				r.pieceSpawnXBig[i][j] = getIntTextField(txtfldPieceSpawnBigX[i][j]);
				r.pieceSpawnYBig[i][j] = getIntTextField(txtfldPieceSpawnBigY[i][j]);
			}
			r.pieceColor[i] = comboboxPieceColor[i].getSelectedIndex() + 1;
			r.pieceDefaultDirection[i] = comboboxPieceDirection[i].getSelectedIndex();
		}
	}

	/**
	 * ルールをファイルに保存
	 * @param filename Filename
	 * @throws IOException 保存に失敗したとき
	 */
	public void save(String filename) throws IOException {
		RuleOptions ruleopt = new RuleOptions();
		writeRuleFromUI(ruleopt);

		CustomProperties prop = new CustomProperties();
		ruleopt.writeProperty(prop, 0);

		FileOutputStream out = new FileOutputStream(filename);
		prop.store(out, "NullpoMino RuleData");
		out.close();

		log.debug("Saved rule file to " + filename);
	}

	/**
	 * ルールをファイルから読み込み
	 * @param filename Filename
	 * @return ルール data
	 * @throws IOException Failed to loadしたとき
	 */
	public RuleOptions load(String filename) throws IOException {
		CustomProperties prop = new CustomProperties();

		FileInputStream in = new FileInputStream(filename);
		prop.load(in);
		in.close();

		RuleOptions ruleopt = new RuleOptions();
		ruleopt.readProperty(prop, 0);

		log.debug("Loaded rule file from " + filename);

		return ruleopt;
	}

	/**
	 * 翻訳後のUIの文字列を取得
	 * @param str 文字列
	 * @return 翻訳後のUIの文字列 (無いならそのままstrを返す）
	 */
	public String getUIText(String str) {
		String result = propLang.getProperty(str);
		if(result == null) {
			result = propLangDefault.getProperty(str, str);
		}
		return result;
	}

	/**
	 * テキストフィールドからint型の値を取得
	 * @param txtfld テキストフィールド
	 * @return テキストフィールドから値を取得できた場合はその値, 失敗したら0
	 */
	public int getIntTextField(JTextField txtfld) {
		int v = 0;

		try {
			v = Integer.parseInt(txtfld.getText());
		} catch(Exception e) {}

		return v;
	}

	/**
	 * テキストフィールドからfloat型の値を取得
	 * @param txtfld テキストフィールド
	 * @return テキストフィールドから値を取得できた場合はその値, 失敗したら0f
	 */
	public float getFloatTextField(JTextField txtfld) {
		float v = 0f;

		try {
			v = Float.parseFloat(txtfld.getText());
		} catch (Exception e) {}

		return v;
	}

	/**
	 * アクション発生時の処理
	 */
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand() == "New") {
			// 新規作成
			strNowFile = null;
			setTitle(getUIText("Title_RuleEditor"));
			readRuleToUI(new RuleOptions());
		} else if(e.getActionCommand() == "Open") {
			// 開く
			JFileChooser c = new JFileChooser(System.getProperty("user.dir") + "/config/rule");
			c.setFileFilter(new FileFilterRUL());

			if(c.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				File file = c.getSelectedFile();
				RuleOptions ruleopt = new RuleOptions();

				strNowFile = file.getPath();
				setTitle(getUIText("Title_RuleEditor") + ":" + strNowFile);

				try {
					ruleopt = load(file.getPath());
				} catch (IOException e2) {
					log.error("Failed to load rule data from " + strNowFile, e2);
					JOptionPane.showMessageDialog(this, getUIText("Message_FileLoadFailed")+"\n"+e2, getUIText("Title_FileLoadFailed"),
												  JOptionPane.ERROR_MESSAGE);
					return;
				}

				readRuleToUI(ruleopt);
			}
		} else if((e.getActionCommand() == "Save") && (strNowFile != null)) {
			// Up書き保存
			try {
				save(strNowFile);
			} catch (IOException e2) {
				log.error("Failed to save rule data to " + strNowFile, e2);
				JOptionPane.showMessageDialog(this, getUIText("Message_FileSaveFailed")+"\n"+e2, getUIText("Title_FileSaveFailed"),
											  JOptionPane.ERROR_MESSAGE);
			}
		} else if((e.getActionCommand() == "Save") || (e.getActionCommand() == "SaveAs")) {
			// 名前を付けて保存
			JFileChooser c = new JFileChooser(System.getProperty("user.dir") + "/config/rule");
			c.setFileFilter(new FileFilterRUL());

			if(c.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				File file = c.getSelectedFile();
				String filename = file.getPath();
				if(!filename.endsWith(".rul")) filename = filename + ".rul";

				try {
					save(filename);
				} catch (Exception e2) {
					log.error("Failed to save rule data to " + filename, e2);
					JOptionPane.showMessageDialog(this, getUIText("Message_FileSaveFailed")+"\n"+e2, getUIText("Title_FileSaveFailed"),
												  JOptionPane.ERROR_MESSAGE);
					return;
				}

				strNowFile = filename;
				setTitle(getUIText("Title_RuleEditor") + ":" + strNowFile);
			}
		} else if(e.getActionCommand() == "ResetRandomizer") {
			// NEXT順生成アルゴリズムの選択リセット
			comboboxRandomizer.setSelectedItem(null);
		} else if(e.getActionCommand() == "Exit") {
			// 終了
			dispose();
		}
	}

	/**
	 * メイン関count
	 * @param args コマンドLines引count
	 */
	public static void main(String[] args) {
		PropertyConfigurator.configure("config/etc/log.cfg");
		log.debug("RuleEditor start");

		if(args.length > 0) {
			new RuleEditor(args[0]);
		} else {
			new RuleEditor();
		}
	}

	/**
	 * ファイル選択画面のフィルタ
	 */
	protected class FileFilterRUL extends FileFilter {
		@Override
		public boolean accept(File f) {
			if(f.isDirectory()) return true;
			if(f.getName().endsWith(".rul")) return true;
			return false;
		}

		@Override
		public String getDescription() {
			return getUIText("FileChooser_RuleFile");
		}
	}

	/**
	 * 画像表示Comboボックスの項目<br>
	 * <a href="http://www.javadrive.jp/tutorial/jcombobox/index20.html">出典</a>
	 */
	protected class ComboLabel {
		private String text = "";
		private Icon icon = null;

		public ComboLabel() {
		}

		public ComboLabel(String text) {
			this.text = text;
		}

		public ComboLabel(Icon icon) {
			this.icon = icon;
		}

		public ComboLabel(String text, Icon icon) {
			this.text = text;
			this.icon = icon;
		}

		public void setText(String text) {
			this.text = text;
		}

		public String getText() {
			return text;
		}

		public void setIcon(Icon icon) {
			this.icon = icon;
		}

		public Icon getIcon() {
			return icon;
		}
	}

	/**
	 * 画像表示ComboボックスのListCellRenderer<br>
	 * <a href="http://www.javadrive.jp/tutorial/jcombobox/index20.html">出典</a>
	 */
	protected class ComboLabelCellRenderer extends JLabel implements ListCellRenderer {
		private static final long serialVersionUID = 1L;

		public ComboLabelCellRenderer() {
			this.setOpaque(true);
		}

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			ComboLabel data = (ComboLabel)value;
			setText(data.getText());
			setIcon(data.getIcon());

			if(isSelected) {
				setForeground(Color.white);
				setBackground(Color.black);
			} else {
				setForeground(Color.black);
				setBackground(Color.white);
			}

			return this;
		}
	}
}
