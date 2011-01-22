NullpoMino ～ぬるぽミノ～
Version 7.5.0

【これって何？】
Javaで作った落ちものアクションパズルゲームもどきです。

【起動方法】
動作にはJava Runtime Environmentの1.5以上が必要です。http://www.java.com/ja/download/

・Windows
　　play_swing.batをダブルクリックするとSwingバージョンが起動します。
　　（OS依存ライブラリを使わない設計です。しかし動作速度やサウンドの質は最低です。BGM再生やジョイスティック機能もありません。）
　　play_slick.batまたはNullpoMino.exeをダブルクリックするとSlickバージョンが起動します。
　　（OpenGLに対応したビデオカードが必要です。いくつかのPCではキーボードを認識しません。）
　　play_sdl.batをダブルクリックするとSDLバージョンが起動します。
　　（ほとんどのPCでSlickバージョンよりも安定して動作します。
　　　ただしランダムにクラッシュするバグや、ネットプレイ時にメモリリークバグなどがあります。32bit限定）

　　ruleeditor.batをダブルクリックするとルールエディタを起動します。ルールの作成・編集ができます。
　　sequencer.batをダブルクリックするとシーケンスビューアを起動します。リプレイファイルを開いてNEXTの順番を確認できるツールです。（Zirceanさん開発）
　　musiclisteditor.batをダブルクリックするとミュージックリストエディタを起動します。音楽の設定ができます。
　　netserver.batをダブルクリックするとNetServer（ネットプレイ用サーバー）を起動します。
　　netadmin.batをダブルクリックするとNetAdmin（NetServer管理ツール）を起動します。
　　airankstool.batをダブルクリックするとAI Ranks Toolを起動します。RanksAIの定石ファイルを作成するツールです。（大量のメモリを必要とします）

・Linux
　　端末ウィンドウでNullpoMinoがあるディレクトリ（このファイルがあるディレクトリ）まで移動して
　　以下のコマンドを入力してEnterキーを押すとたぶん起動します。
　　(注:最初のchmodコマンドは起動用シェルスクリプトに実行権限を与えるものです。2回目の起動からは必要ありません。)

　　Swingバージョン:
chmod +x play_swing
./play_swing

　　Slickバージョン:
chmod +x play_slick
./play_slick
　　または
chmod +x NullpoMino
./NullpoMino

　　SDLバージョン:
chmod +x play_sdl
./play_sdl

　　ルールエディタ:
chmod +x ruleeditor
./ruleeditor

　　シーケンスビューア:
chmod +x sequencer
./sequencer

　　ミュージックリストエディタ:
chmod +x musiclisteditor
./musiclisteditor

　　ネットプレイ用サーバー:
chmod +x netserver
./netserver

　　NetAdmin:
chmod +x netadmin
./netadmin

　　AI Ranks Tool:
chmod +x airankstool
./airankstool

　　使用しているビデオカードやLinuxのバージョンによってはうまく動かないかもしれません。

　　Swingバージョン固有の問題:
　　　現時点ではまともに動きません。

　　Slickバージョン固有の問題:
　　　3Dデスクトップ機能（Berylとか）は無効にすることをおすすめします。
　　　一応x64でも動作するようです。

　　　LWJGLおよびSCIMのバグ(もしくは制限)のため、play_slickシェルスクリプトはゲームを起動するときに全てのIMEを無効化します。
　　　シェルスクリプト内のコマンドライン最初のXMODIFIERS=@im=noneは、使っているIMEがSCIM以外の場合は不要です。

　　　もしSCIMを有効化したままゲームをプレイしたい場合は、以下のコマンドを試してください(sudoを実行できる権限が必要です):

sudo chmod go+r /dev/input/*
java -cp bin:NullpoMino.jar:lib/log4j-1.2.15.jar:lib/slick.jar:lib/lwjgl.jar:lib/jorbis-0.0.15.jar:lib/jogg-0.0.7.jar:lib/ibxm.jar:lib/jinput.jar -Djava.library.path=lib mu.nu.nullpo.gui.slick.NullpoMinoSlick -j

　　　最初のコマンドは、すべてのプログラムがキーボード入力を直接読み取れるようにします。
　　　一度実行すると、次に再起動もしくはシャットダウンするまで再度実行する必要はありません。
　　　2つ目のコマンドは、ゲームを"-j"オプションを付けて起動します。
　　　通常、このゲームはキーボード入力をLWJGLから読み取ろうとしますが、SCIMとは相性が悪いです。
　　　このオプションを使用している場合、ゲームはキーボード入力をシステムから直接読み取りますので、SCIMがあっても問題なく動作するようになります。
　　　ただし一部認識できないキー（;など）があります。

　　SDLバージョン固有の問題:
　　　自分がUbuntu 8.04で試した限りでは、これがLinuxでは最も問題なく動作します。
　　　動かすにはlibsdlがインストールされている必要があります。
　　　入ってない場合、Ubuntu 8.04だと以下のコマンドでインストールできるらしいです。
sudo apt-get install libsdl1.2debian
　　　i386以外だと動かないようです。

・Mac OS X
　　持ってないのでよく分かりません(´・ω・｀)
　　Slickバージョンは一応Linuxと同じコマンドで動くらしいです。
　　SDLバージョンは、使ってるライブラリ（sdljava）がWindows版とLinux版しか存在しないため動きません。
　　Swingバージョンは分かりません…

【遊び方】
上からブロックが落下してきます。
ブロックは地面や他のブロックの上に落ちるまで回転させたり、移動させたり、早く落下させたりできます。
ブロックが地面や他のブロックの上に落ちると、そのブロックが固定されて、上から次のブロックが落下してきます。
ブロックを横一列に隙間無く並べる（ラインを作る）と、ブロックを消すことができます。
一度に複数のラインを作ると高得点です。
ブロックが上まで積みあがり、次のブロックが落ちてこれなくなるとゲームオーバーです。

【ボタンの説明】
UP：ハードドロップ（ブロックを一瞬で落下）・カーソルを上に移動
DOWN：ソフトドロップ（ブロックを早く落下）・カーソルを下に移動
LEFT：ブロックを左に移動・カーソルで選択している項目の値を1つ減らす
RIGHT：ブロックを右に移動・カーソルで選択している項目の値を1つ増やす
A：ブロックの回転・メニュー項目の決定
B：ブロックの逆回転・キャンセル
C：ブロックの回転
D：ホールド（ブロックを一時的に保管して、後で使うことができます）
E：ブロックの180度回転
F：エンディング早送り（SPEED MANIAとGARBAGE MANIAモードで使用可能）・ネットプレイで練習モードを開始／終了
QUIT：ゲームを終了する
PAUSE：ゲームを一時停止
GIVEUP：タイトルに戻る
RETRY：ゲームを最初からやり直す
FRAME STEP：ポーズ中に押すと1フレームだけゲームを進める（設定で有効にしている場合）
SCREEN SHOT：スクリーンショットをssフォルダに保存

【キー配置】
・メニュー画面でのキー配置
+-------------+------------+------------+------------+
|  ボタン名   |  Blockbox  | Guideline  | NullpoMino |
|             |(デフォルト)|            |  Classic   |
+-------------+------------+------------+------------+
|UP           |Cursor Up   |Cursor Up   |Cursor Up   |
|DOWN         |Cursor Down |Cursor Down |Cursor Down |
|LEFT         |Cursor Left |Cursor Left |Cursor Left |
|RIGHT        |Cursor Right|Cursor Right|Cursor Right|
|A            |Enter       |Enter       |A           |
|B            |Escape      |Escape      |S           |
|C            |A           |C           |D           |
|D            |Space       |Shift       |Z           |
|E            |D           |X           |X           |
|F            |S           |V           |C           |
|QUIT         |F12         |F12         |Escape      |
|PAUSE        |F1          |F1          |F1          |
|GIVEUP       |F11         |F11         |F12         |
|RETRY        |F10         |F10         |F11         |
|FRAME STEP   |N           |N           |N           |
|SCREEN SHOT  |F5          |F5          |F10         |
+-------------+------------+------------+------------+

・ゲーム中のキー配置
+-------------+------------+------------+------------+
|  ボタン名   |  Blockbox  | Guideline  | NullpoMino |
|             |(デフォルト)|            |  Classic   |
+-------------+------------+------------+------------+
|UP           |Cursor Up   |Space       |Cursor Up   |
|DOWN         |Cursor Down |Cursor Down |Cursor Down |
|LEFT         |Cursor Left |Cursor Left |Cursor Left |
|RIGHT        |Cursor Right|Cursor Right|Cursor Right|
|A            |Z           |Z           |A           |
|B            |X           |Cursor Up   |S           |
|C            |A           |C           |D           |
|D            |Space       |Shift       |Z           |
|E            |D           |X           |X           |
|F            |S           |V           |C           |
|QUIT         |F12         |F12         |Escape      |
|PAUSE        |Escape      |Escape      |F1          |
|GIVEUP       |F11         |F11         |F12         |
|RETRY        |F10         |F10         |F11         |
|FRAME STEP   |N           |N           |N           |
|SCREEN SHOT  |F5          |F5          |F10         |
+-------------+------------+------------+------------+

キー配置はタイトルの「CONFIG」の中にある「[KEYBOARD SETTING]」から変更できます。

【設定のリセット】
設定をリセットしたいときは、以下のファイルを削除してください。
　Swing版：config\setting\swing.cfg
　Slick版：config\setting\slick.cfg
　SDL版：config\setting\sdl.cfg
　各バージョン共通の設定：config\setting\global.cfg
　ハイスコアなど：config\setting\mode.cfg

【ゲームルール】
選んだゲームルールに応じて操作性やブロックの見た目が変わります。
CONFIGの中にある「[RULE SELECT]」から使用するルールを変更できます。
付属のルールエディタを使うと独自のルールを作成できます。

AVALANCHE　　　　　：AVALANCHEタイプのモード用のルールです。
CLASSIC0 　　　　　：壁蹴りもホールドもない古典的なルール(RETRO MANIAモードにおすすめ)
CLASSIC0-68K 　　　：CLASSIC0に逆回転が付いたルール
CLASSIC1 　　　　　：NEXTが1個だけ、ホールドなし、ハードドロップなし、壁登り不可のルール
CLASSIC2 　　　　　：CLASSIC1にハードドロップを追加したルール
CLASSIC3 　　　　　：NEXTが3個、ホールドあり、一部ブロックで1回だけ特殊な回転ができるようになったルール
CLASSIC-EASY-A 　　：CLASSIC3とSTANDARDを合体させたようなルール
CLASSIC-EASY-A2　　：CLASSIC-EASY-Aの色違い
CLASSIC-EASY-B 　　：CLASSIC-EASY-Aのハードドロップとソフトドロップの関係を反対にしたルール
CLASSIC-EASY-B2　　：CLASSIC-EASY-Bの色違い
CLASSIC-S　　　　　：CLASSIC0に壁蹴りを追加したルール(ただし「壁」しか蹴りません。すでに置いたブロックは蹴りません。)
DTET 　　　　　　　：独特な回転法則や操作性を持つルール
NINTENDO-L 　　　　：モノクロのブロックや狭いフィールドを特徴とする古典的なルール
NINTENDO-L-FAST 　 ：NINTENDO-Lの横移動を速くしたもの
NINTENDO-R 　　　　：NINTENDO-Lよりも右寄りに回転しブロックもカラーな古典的なルール(CLASSIC MARATHONモードにおすすめ)
NINTENDO-R-FAST 　 ：NINTENDO-Rの横移動を速くしたもの
PHYSICIAN　　　　　：PHYSICIANタイプのモード用のルールです。
SPF　　　　　　　　：SPFタイプのモード用のルールです。
SQUARE　　　　　　 ：SQUAREモード向きのルールです。4x4の正方形が作り易いような順番でブロックが出現します。
STANDARD　　　　　 ：初心者から上級者まで扱いやすいルールで、壁登りも可能
STANDARD-EXP　　　 ：ハードドロップを使用しても即固定せず、ソフトドロップだと即固定になるルール
STANDARD-FAST　　　：STANDARDよりも素早い操作が可能
STANDARD-FAST-B　　：STANDARD-FASTよりも先行回転が暴発しにくい
STANDARD-FRIENDS　 ：動きがやや遅い・出現位置が他のルールの1マス下
STANDARD-GIZA　　　：hebo-MAIさん作のルール・やや速度が抑えられていて対戦では速度ではなく戦略性が求められる
STANDARD-HARD　　　：STANDARDよりも少し難しい
STANDARD-HARD128　 ：STANDARD-HARDを少し簡単にしたもの(ブロックが地面に着地したあと128回まで移動・回転可能)
STANDARD-HOLDNEXT　：holdnextさん作のルール・オレンジ棒が特徴
STANDARD-J　　　　 ：動きがかなり遅い
STANDARD-PLUS　　　：STANDARD-FASTをベースに、ライン消去時間を0にしてソフトドロップ速度を上げたルール(Blinkさん作)
STANDARD-SUPER3　　：回転法則がSTANDARDと同じで壁蹴りが存在しないルール
STANDARD-ZERO　　　：STANDARD-PLUSをベースに、先行回転無し・先行ホールド無し・ソフトドロップ速度20G・ワープ横移動を取り入れたルール (Wojtekさん作)

【ゲームモード】
・MARATHON
　　10ライン消すごとにレベルが上がる初心者向けモードです。
　　「150ラインで終わる」「200ラインで終わる」「無限に続く」3つのゲームタイプを選べます。

・MARATHON
　　ゲーム内容はMARATHONの200ラインタイプとほぼ同じですが、レベル20をクリアすると「ボーナスレベル」(レベル21)に突入します。
　　ボーナスレベルは無限に続きますが、置いたブロックがたまにしか表示されません。

・EXTREME
　　MARATHONと似ているけどブロックの落下速度がすごく早い上級者向けモードです。

・LINE RACE
　　どれだけ早く規定ライン数を消せるか競うタイムアタックモードです。
　　規定ライン数は20ライン・40ライン・100ラインの3種類から選択可能です。

・SCORE RACE
　　どれだけ早く規定スコアに到達できるかを競うタイムアタックモードです。
　　規定スコアは10000点・25000点・30000点の3種類から選択可能です。

・DIG RACE
　　どれだけ早くすべての邪魔ブロックを消せるか競うタイムアタックモードです。
　　邪魔ブロックの数は5ライン・10ライン・18ラインから選択可能です。

・COMBO RACE
　　規定ライン数を消すまでに最大で何コンボできるかを競います。
　　規定ライン数は20ライン・40ライン・100ライン・エンドレスから選択可能です。
　　エンドレスではコンボが途切れるまでゲームが続きます。

・ULTRA
　　制限時間内にどれだけ多くの得点を得られるか、またはどれだけ多くのラインを消せるかを競うモードです。
　　制限時間は1～5分の5種類から選択可能です。

・TECHNICIAN
　　できるだけ早く「GOAL」を0にして次のレベルに進むことが目的のモードです。
　　ラインを消去するなどの行動をすると「GOAL」が減り、「GOAL」の表示が0になると次のレベルに進めます。
　　一度に複数のラインを消したり、連続でラインを消したりすると「GOAL」が多く減ります。
　　目的や制限時間が異なる5種類のゲームタイプを選べます。

　　LV15-EASY： レベル16に到達するとゲームクリアになります。
　　　　　　　　２分以内にレベルアップするとボーナス得点が入りますが、時間切れになってもペナルティはありません。
　　LV15-HARD： LV15-EASYと似ていますが、２分以内にレベルアップしないと即ゲームオーバーです。
　　10MIN-EASY：10分間プレイしてスコアを競うゲームタイプです。
　　　　　　　　２分以内にレベルアップしなかった場合、「REGRET」と表示されてGOALがリセットされてしまいます。
　　10MIN-HARD：10MIN-EASYとほぼ同じですが、２分以内にレベルアップしなかった場合は即ゲームオーバーです。
　　SPECIAL：　 レベルアップするたびに制限時間が30秒延長されるゲームタイプです。
　　　　　　　　早くレベルアップするとより長くプレイできるようになります。

・SQUARE
　　縦4x横4のサイズの正方形を作って消していくモードです。
　　2種類以上のブロックを使って正方形を作ると銀色、1種類のブロックだけで正方形を作ると金色になります。
　　金色の正方形を消すと、銀色を消したときの2倍のボーナスが入ります。
　　3種類のゲームタイプを選べます。
　　MARATHON:　エンドレス
　　SPRINT:　　150点取るまでのタイムアタック
　　ULTRA:　　 3分間スコアアタック
　　基本的にどんなルールでも一応遊べますが、「SQUARE」ルールを使うと正方形を作り易い順番でブロックが落ちてきます。

・DIG CHALLENGE
　　下からどんどんせり上がってくる邪魔ブロックをひたすら消していくモードです。送り返した邪魔ブロック数が得点になります。
　　2種類のゲームタイプを選べます。
　　NORMAL:　　ピースを置くまでせり上がらないが、1ラインずつ溜まっていく
　　REALTIME:　ピースを置かなくてもせり上がる

・RETRO MARATHON
　　旧名CLASSIC MARATHONモード。
　　MARATHONとほぼ同様のゲームが遊べますがスピードの上昇は緩やかです。
　　「NINTENDO-R」ルールがおすすめです。

・RETRO MASTERY
　　RETRO MARATHONの上級者向けバージョンです。いかに無駄な消し方をしないかが重要になります。
　　「NINTENDO-R」ルールがおすすめです。

・RETRO MANIA
　　MARATHONとほぼ同様のゲームが遊べますが、レベルアップ方式が独特で、4ライン消すかしばらく何もしないとレベルが上がります。
　　「CLASSIC0」ルールがおすすめです。

・GRADE MANIA
　　レベル999になるまでにできるだけ高い段位を目指すモードです。
　　段位は得点に応じてランクアップします。
　　レベルはブロックを置くだけで1つ上がりますが、レベルの末尾2桁が99のとき、およびレベル998ではラインを消さないと上がりません。
　　難易度は初心者向けです。

・GRADE MANIA 2
　　GRADE MANIAよりも難易度が高い中級者向けモードです。
　　このモードでは段位と得点は無関係です。

・GRADE MANIA 3
　　GRADE MANIA2よりもさらに難易度の高い上級者向けモードです。
　　プレイヤーの腕前に応じて落下速度も変化してきます。

・SCORE ATTACK
　　レベル300に到達するまでに稼いだ得点を競うモードです。
　　難易度は初心者向けです。

・SPEED MANIA
　　GRADE MANIAと似たシステムを用いた、落下速度が速い中級者向けモードです。
　　レベル500以降に行くにはそれなりの腕前が必要となります。

・SPEED MANIA 2
　　SPEED MANIAを大きく超える速度でブロックが降ってくる上級者向けモードです。
　　レベル500に到達すると何かが起こります。

・GARBAGE MANIA
　　時々灰色のブロックが下からせり上がってくるモードです。
　　後半になるほどせり上がりのペースが上がってきます。

・PHANTOM MANIA
　　SPEED MANIAモードと似ていますが、このモードでは置いたブロックが全く見えません。
　　置いたブロックの場所やフィールドの地形を記憶することが重要となります。

・FINAL
　　常識外の速度でブロックが落ちてくるモードです。超上級者専用。

・TIME ATTACK
　　できるだけ速く150ラインまたは200ライン消すことが目的のモードです。
　　各レベルには制限時間が設定されており、この時間内に次のレベルに行かないとゲームオーバーになります。
　　10ライン消すたびにレベルが上がり、制限時間も回復します。

　　150ラインタイプ(右に行くほど難しい):
　　　NORMAL＜HIGH SPEED 1＜HIGH SPEED 2＜ANOTHER＜ANOTHER2
　　200ラインタイプ(右に行くほど難しい):
　　　NORMAL 200＜ANOTHER 200＜BASIC＜HELL≒HELL-X＜VOID

・PRACTICE
　　好きな速度を設定して練習ができるモードです。
　　出現するブロックの種類も設定できます。

・GEM MANIA
　　フィールドに配置された宝石ブロックをできるだけ速くすべて消去することが目的のモードです。
　　「ステージタイム」と「リミットタイム」の2種類の制限時間が存在します。
　　ステージタイムは各ステージの制限時間で、ステージ開始時に1分から始まり、0になると強制的に次のステージへ進まされます。
　　リミットタイムはゲーム全体の制限時間で、これが0になるとゲームオーバーです。
　　リミットタイムは各ステージを20秒以内にクリアすると少し回復します。

・VS-BATTLE
　　人間またはコンピュータと対戦するモードです。
　　一度に複数のラインを消すとお邪魔ブロックを相手に送ることができます。
　　お邪魔ブロックで相手をゲームオーバーにさせると勝利です。

・TOOL-VS MAP EDIT
　　このモードは厳密に言うと「ゲーム」モードではありません。
　　VS-BATTLEとネットプレイで使用できるマップを作成できるモードです。
　　[フィールド編集画面のときの操作方法]
　　　Up/Down/Left/Right: カーソルを動かす
　　　A: カーソル位置にブロックを置く
　　　B: メニューに戻る
　　　C+Left/Right: 配置するブロックの色を選ぶ
　　　D: カーソル位置にあるブロックを消す

・AVALANCHE 1P (RC1)
　　同じ色のブロックを縦か横に4つ以上繋げて消していくモードです。途中で折れ曲がっていてもOKですが、斜めにはくっつきません。
　　空中に浮いたブロックは全て重力に従って落下します。これを利用して連鎖も可能です。
　　選べるゲームタイプはSQUAREモードと同じです。
　　まともにプレイする場合は「AVALANCHE」ルールを使用してください。

・AVALANCHE 1P FEVER MARATHON (RC1)
　　連鎖のタネ（あらかじめ簡単に連鎖できるように組まれたブロック）が積まれた状態でゲームが始まります。
　　一番長い連鎖ができると思うところにブロックを置いて、連鎖をスタートさせてください。
　　連鎖終了後、新しい連鎖のタネが出現します。うまく連鎖できれば、次に出現する連鎖のタネが大きくなり、制限時間も増えます。
　　まともにプレイする場合は「AVALANCHE」ルールを使用してください。

・AVALANCHE VS-BATTLE (RC1)
　　AVALANCHE 1Pモードと似たルールで対戦します。連鎖でブロックを消すと相手に邪魔ブロックを送り込むことができます。
　　まともにプレイする場合は「AVALANCHE」ルールを使用してください。

・AVALANCHE VS FEVER MARATHON (RC1)
　　連鎖のタネ（あらかじめ簡単に連鎖できるように組まれたブロック）が積まれた状態でゲームが始まります。
　　連鎖すると、HANDICAP欄の数字が減っていきます。これが0になると、相手に実際に攻撃できるようになります。
　　まともにプレイする場合は「AVALANCHE」ルールを使用してください。

・AVALANCHE VS DIG RACE (RC1)
　　相手より先に、7色に光る宝石ブロックを消すことが目的のモードです。宝石ブロックは他のブロックの下に埋もれています。
　　大きな連鎖をすると相手に邪魔ブロックを送ることが出来ますが、致命傷を与えるほどの攻撃力はありません。
　　まともにプレイする場合は「AVALANCHE」ルールを使用してください。

・PHYSICIAN (RC1)
　　あらかじめフィールド内に置かれているウイルス（宝石ブロック）を、上から落ちてくる3色のカプセル（通常ブロック）を使って消していくモードです。
　　カプセルは縦か横に4つ以上並べると消えます。繋がっている同色のウイルスも一緒に消えます。
　　全てのウイルスを消すとステージクリアです。
　　まともにプレイする場合は「PHYSICIAN」ルールを使用してください。

・PHYSICIAN VS-BATTLE (RC1)
　　PHYSICIANモードのルールで対戦します。
　　相手がゲームオーバーになるか、先にウイルスを全て消すと勝利です。
　　まともにプレイする場合は「PHYSICIAN」ルールを使用してください。

・SPF VS-BATTLE (BETA)
　　上から落ちてくるノーマルジェム（通常ブロック）を積み上げ、時々落ちてくるクラッシュジェム（宝石ブロック）を使ってノーマルジェムを消していきます。
　　2×2以上の大きさで同色のノーマルジェムを四角形型に組み合わせると、より強力なパワージェムに変化します。
　　ノーマルジェムやパワージェムはいくらでも繋げていくことができますが、クラッシュジェムを使わない限り消すことができません。
　　クラッシュジェムを使ってジェムを消すと、相手にカウンタージェム（邪魔ブロック）を送り込むことができます。
　　まともにプレイする場合は「SPF」ルールを使用してください。

【BGMを鳴らすには】
まだBGMは標準では付いていませんが、任意の音楽ファイルを再生できます。
BGMの設定をするにはミュージックリストエディタ(musiclisteditor.bat)を使ってください。
対応形式はたぶん「.ogg」「.wav」「.xm」「.mod」「.aif」「.aiff」の6種類です。

巨大なoggファイルを入れるとループする時に落ちるようです。

【ネットプレイ(β版)】
[できること]
・他のプレイヤーと対戦(最大6人)
・新しいルームを作る
・すでにあるルームに入る
・簡単なチャット機能
・観戦
[できないこと]
・その他ほとんど全部

[ネットプレイのはじめ方]
ネットプレイモードに入る方法:
　1. 普通にゲームを起動します。3つあるどのバージョンでもOKです。
　2. Swingバージョンでは、ファイルメニューから「ネットプレイ」を選択します。
　   その他のバージョンでは、トップメニューから「NETPLAY」を選択してAボタンを押します。
　3. 「NullpoMino NetLobby」というウィンドウが現れます。

新しいサーバーをリストに追加する方法:
　1. サーバー選択画面(NullpoMino NetLobbyというウィンドウが出現した直後の状態)で「追加」ボタンをクリックします。
　2. ホスト名(またはIPアドレス)とポート番号を入れる画面が出てきます。
　   「ホスト名またはIPアドレス:ポート番号」の形式で入力してください。(ホスト名とポート番号をコンマ「:」で区切ります)
　   サーバー側のポート番号が9200の場合は「:9200」をホスト名の後ろにつける必要はありません。
　3. 入力したらOKボタンをクリックしてください。

　ローカルでネットプレイを試すには、netserver.batをダブルクリックしてサーバーを起動し
　「127.0.0.1」をサーバーリストに追加してください。

　harddrop.comの皆さんがネットプレイサーバーを提供しています。harddrop.comの皆さんありがとう！
harddrop.com

追加したサーバーに接続する方法:
　1. 名前とトリップをニックネーム欄に入力します（任意）
　   名前もトリップも入力しない場合は自動的に名前が「noname」になります。
　   トリップは2chとかにあるような個人識別機能です。ニックネーム欄に#記号の後にパスワードを入れると暗号化された文字列が表示されます。
　    Wikipediaでのトリップの記事：
　    http://ja.wikipedia.org/wiki/%E3%83%88%E3%83%AA%E3%83%83%E3%83%97_(%E9%9B%BB%E5%AD%90%E6%8E%B2%E7%A4%BA%E6%9D%BF)
　2. 接続したいサーバーをリストボックスから選びます。(ダブルクリックすると即接続できます)
　3. 「接続」ボタンをクリックします。画面がロビー画面に切り替わります。

新しい（対戦用の）ルームを作成する方法:
　1. 画面上部にある「ルーム作成」ボタンをクリックします。
　2. ルームの名前(省略可)と参加可能な最大人数を入力します。
　3. OKボタンをクリックします。

1人プレイ用ルームを作成する方法:
　1. 画面上部にある「1人プレイ」ボタンをクリックします。
　2. プレイするモードとルールを選択します。
　3. OKボタンをクリックします。

すでにあるルームに入る方法:
　ルーム一覧表で入りたいルーム名をダブルクリックするだけです。
　観戦だけしたい場合は、観戦したいルーム名を右クリックして、出てきた右クリックメニューから「観戦」を選びます。

OKシグナルを出す:
　1. ゲームウィンドウ(普段1Pゲームを遊ぶウィンドウ)をクリックして、ゲームウィンドウに操作を移します。
　2. Aボタンを押します。「OK」と自分のフィールドに出てきたら完了です。
　3. その部屋にいる全員がOKシグナルを出すとゲームが始まります。

[サーバーについて]
netserver.batをダブルクリックするとサーバーが起動しますが、この場合のポート番号はデフォルトの9200で固定です。
他のポートに変えたい場合、およびLinuxまたはMac OS Xを使っている場合は以下のコマンドを使ってください。

Windows:
java -cp NullpoMino.jar;lib\log4j-1.2.15.jar mu.nu.nullpo.game.net.NetServer [ポート番号]
Linux/MacOS:
java -cp NullpoMino.jar:lib/log4j-1.2.15.jar mu.nu.nullpo.game.net.NetServer [ポート番号]

2番目の引数を設定することで、別のnetserver.cfgから設定を読み込むこともできます。
Windows:
java -cp NullpoMino.jar;lib\log4j-1.2.15.jar mu.nu.nullpo.game.net.NetServer [ポート番号] [netserver.cfgの場所]
Linux/MacOS:
java -cp NullpoMino.jar:lib/log4j-1.2.15.jar mu.nu.nullpo.game.net.NetServer [ポート番号] [netserver.cfgの場所]

【FAQ】
Q: Slick版でジョイスティックが動かない
A: GENERAL CONFIG画面の"JOYSTICK METHOD"の設定をLWGJLに変えて、JOYSTICK SETTING画面の設定をいろいろ弄ってください。
   Slick版のジョイスティックサポートはSDL版ほど良くないです。

Q: SDL版のネットプレイで使用RAMが激増する
A: Swing版かSlick版を使ってください。今のところまともな解決方法はありません。SDLかSDLJava側の問題っぽいです。

Q: 64bitのOSを使っています。SDL版が動きません。
A: 無理。

Q: ネットプレイでレートや1人プレイの記録が保存されない
A: 名前にトリップが入っていないと記録は保存されません。
   トリップをつけるには、名前の後ろにシャープ記号（#）とパスワードを入れてください。
   （例えば、名前欄に"ABCDEF#nullpomino"と入れて接続すると"ABCDEF ◆gN6kJVofq6"になります)

【製作・謝辞】
製作：
	NullNoname ◆bzEQ7554bc (別名pbomqlu910963、元名無し) pbomqlu910963@gmail.com
	Zircean
	Poochy.EXE（ポチエグゼ）
	Wojtek (aka dodd)
	Spirale (olivier.vidal1 on the SVN)
	kitaru2004
	Shrapnel.City (aka Pineapple)
	vic7070 (aka Digital)
	alight
	nightmareci
	johnwchadwick (aka nmn)
	prelude234 (aka awake)
	sesalamander
	teh_4matsy@lavabit.com (aka 4matsy)
	delvalle.jacobo (aka clincher)
	bob.inside (aka xlro)

	Google CodeのPeopleページ:
	http://code.google.com/p/nullpomino/people/list

このゲームは以下のツール・ライブラリ・素材を使用しました。
この場を借りてお礼申し上げます。

・ツール
	Eclipse 3.6
	http://www.eclipse.org/
	PictBear SE
	http://www20.pos.to/~sleipnir/

・ライブラリ
	Slick - 2D Game Library based on LWJGL
	http://slick.cokeandcode.com/
	Lightweight Java Game Library (LWJGL)
	http://www.lwjgl.org/
	JOrbis -- Pure Java Ogg Vorbis Decoder
	http://www.jcraft.com/jorbis/
	IBXM Java MOD/S3M/XM Player
	http://sites.google.com/site/mumart/
	sdljava - Java Binding to SDL
	http://sdljava.sourceforge.net/
	Simple DirectMedia Layer
	http://www.libsdl.org/
	Apache log4j 1.2.15
	http://logging.apache.org/log4j/1.2/index.html
	Crypt.java (Java-based implementation of the unix crypt(3) command)
	http://www.cacas.org/java/gnu/tools/

・効果音
	ザ・マッチメイカァズ
	http://osabisi.sakura.ne.jp/m2/
	TAM Music Factory
	http://www.tam-music.com/

・背景 (res/graphics/oldbg)
	ゆんフリー写真素材集
	http://www.yunphoto.net/

・フォント
	オリジナルフォント【みかちゃん】
	http://www001.upp.so-net.ne.jp/mikachan/

・Also thanks to:
	Lee
	Burbruee
	Steve
	Blink
	xlro (http://nullpo.nu.mu/)
	vicar (http://vicar.bob.buttobi.net/)
	SWR
	hebo-MAI
	gif
	virulent
	tetrisconcept.net http://www.tetrisconcept.net/
	Hard Drop http://harddrop.com/
	 (NullpoMino Topic: http://harddrop.com/forums/index.php?showtopic=2035
	  NullpoMino Guide: http://harddrop.com/forums/index.php?showtopic=2317
	  NullpoMino on HD wiki: http://harddrop.com/wiki/index.php?title=NullpoMino)
	Puyo Nexus http://www.puyonexus.net/

【Google Codeのプロジェクトページ】
http://code.google.com/p/nullpomino/

【更新履歴】
+は新機能、-はバグ修正、*はその他の修正、#はその他メモを意味します。

Version 7.5.0 (2011/01/21) {r518-r716; Stable Release}
#このバージョンから7.4.0のサーバーとの互換性は失われます。
+Swing/Slick: 画面サイズ設定を追加しました。
+Slick/SDL: モードフォルダ機能を追加し、STARTを選んだ直後はおすすめモード一覧が出るようにしました。
+Slick: LWGJLのバージョンを2.6に上げました。
+DIG CHALLENGEモードを追加しました。
+Practice: Hebo Hiddenオプションを追加しました。
+AI: Avalancheモード用AI"Avalanche-R"、Combo Raceモード用AI"Combo Race"、定石ファイルを使う"Ranks AI"と、定石ファイルを生成するRanks AI Toolを追加しました。
+スティッキーなブロックスキンを追加しました（#9と#27）
+NetServer: 安定性がアップしました。CPU占有率100%バグも修正しました。
#ネットプレイの強化：
 +レートありのルームはプリセット形式になりました。使用するルールは各人が自由に選べます。
 +オンラインの1人プレイにMARATHON, MARATHON+, EXTREME, DIG RACE, COMBO RACE, ULTRA, TECHNICIAN, TIME ATTACKモードを追加しました。
 +オンラインの1人プレイに全ルール共通のランキングを追加しました。ルール選択で「(今使用中のルール)」を選ぶと使われます。
 +ロビーとルームに入室した直後に最近のチャット履歴が表示されるようになりました。
 +チーム戦で名前が色分けされるようになりました。
 +敵プレイヤー数に応じて穴の位置が変わる設定を追加しました。
 *その他いろいろ
-PoochyBotのいろいろなバグ修正をしました。また、先行思考オプションをAI設定画面に追加し、"No Prethink"版は廃止しました。
-その他のいろいろなバグを修正しました。
*アイコンが新しくなりました。デフォルトのものはgifさん、それ以外のはvirulentさん作成です。ありがとう！
*ReadmeとLICENCE.txt以外のテキストファイルを"doc"フォルダに移しました。
#他の細かい変更内容はdoc/svnlog7_5_0.txtにあります(英語ですが…)

Version 7.4.0 (2010/10/29) {r277-r517; Unstable Release}
#このバージョンから7.3.0のサーバーとの互換性は失われます。
+Swing/Slick/SDL: より大きい横NEXT表示を追加しました。 (GENERAL OPTIONSの"SHOW NEXT ON SIDE"と"BIG SIDE NEXT"の両方を有効にしてください)
+Swing/Slick/SDL: メニュー画面でゲーム中とは別のキー配置を使うようにしました。(賛否両論なのでご意見募集中)
+Swing/Slick/SDL: デフォルトの回転方向を左に変えました。AUTOに戻すには"GAME TUNING"メニューの"A BUTTON ROTATE"の設定を変更してください。
+Swing/Slick/SDL: おすすめルール選択画面を追加しました(モード選択後に出現)。
+Slick/SDL: キー設定画面でキーを個別に変更できるようにしました。
+Slick/SDL: 初回起動時の設定画面を消しました。キー配置の初期設定はBlockboxスタイルになっています。
+Slick/SDL: いくつかのメニューでマウスが使えるようになりました(不完全)
+Slick: "NullpoMino.exe"を追加しました。ダブルクリックするとSlick版が起動します。
+Avalanche/SPF: ゲーム画面の表示を大きいものに変更できる設定を追加しました。
+新しいスキンを追加しました(4matsyさんありがとう！)
#ネットプレイに新機能を多数追加しました：
 +レート変動のあるルームとレートランキング
 +ルーム内でもロビーを見れる
 +NetServer管理ツール"NetAdmin"
 +1人プレイルーム (今のところLINE RACEとSCORE RACEモードのみ)
#他の細かい変更内容はsvnlog7_4_0.txtにあります(英語ですが…)

Version 7.3.0 (2010/08/09) {r1-r276; Stable Release}
#このバージョンから7.2.0のサーバーとの互換性は失われます。
#このバージョン以前のリプレイはこのバージョンから見れません。
+Swing/Slick/SDL: リプレイを保存するとき、replayフォルダが無い場合は自動的に作成するようにしました。
+Swing/Slick/SDL: 出現位置補正があるルールで、NEXTピースが正しい位置に表示されるようにしました。
+Swing/Slick/SDL: フィールドの横にNEXTを表示する設定を追加しました。
+Slick: PERFECT FPSオプションを追加しました。(フレームレートがより正確になりますが、より多くのCPUパワーを使用します。メニュー画面では動きません。)
+Slick: LWJGLを2.5に、Slickをbuild 274にバージョンアップしました。
+以下のモードを追加しました:COMBO RACE, SQUARE, RETRO MASTERY, AVALANCHE 1P, AVALANCHE 1P FEVER MARATHON, AVALANCHE VS-BATTLE, AVALANCHE VS FEVER MARATHON, AVALANCHE VS DIG RACE, PHYSICIAN, PHYSICIAN VS-BATTLE, SPF VS-BATTLE
 ライン消去型でないモードは、リリース候補版(AVALANCHE, PHYSICIAN)またはベータ版(SPF)です。
 スペシャルサンクス:Puyo Nexusさん(連鎖のタネのデータをお借りしました。ありがとう！)
+STANDARD-HOLDNEXT, SQUARE, AVALANCHE, PHYSICIAN, SPFルールを追加しました。
+CLASSIC MARATHONモードをRETRO MARATHONに改名しました。
+Randomizer: NEXT順生成アルゴリズムのシステムを大幅に変更し、複数の新しいものを追加しました。このため、このバージョン以前のリプレイはもう見れません。
+Sequencer: 出現可能ピースの設定画面を追加しました。
+AI: "Defensive"と"No Prethink"バージョンのPoochyBotを追加しました。
+Engine: 同じ色のブロックを並べて消すタイプのモード(AVALANCHE, PHYSICIAN, SPF)用の機能を多数追加しました。
+Engine: 7色に光るレインボーブロックを追加しました。
+Engine: DTETやSTANDARD-SUPER3ルール用に待ち時間キャンセルの機能を追加しました。
+Engine: 横溜め関連の機能を拡充しました。追加された機能の多くはNINTENDO系ルールで使われています。
+Engine: ワープ移動(Instant DAS)とドロップキーを離すまで横溜め移動をしない機能(Shift Lock)を追加しました。
+Engine: ハードブロックと固い邪魔ブロックを追加しました。
+NetPlay/NetServer: ルーム作成画面により多くの設定項目を追加しました。また、多くの設定項目を新しくできたタブに移しました。
 邪魔ブロック: B2Bで穴の位置を変える、1回の攻撃ごとに邪魔ブロックの穴の位置を変える、穴の位置が変わる確率、せり上がり抑制、相殺
 ボーナス: スピン判定タイプ、全消しボーナス
-NetPlay: SDL版でネットプレイ時には正しく60FPS固定になるようにしました。
-NetPlay: ゲーム画面を直接閉じたりJavaのプロセスを強制終了してもチャットログが正しく保存されるようにしました。
-NetPlay: ルール固定がない定員2名のルームでも、相手のNEXTとHOLDが見えるようにしました。
-NetServer: 幽霊部屋バグを修正しました。
-Slick/SDL: 起動後にリプレイを削除するとリプレイ選択画面で落ちる可能性があるバグを修正しました。
+より多くのマップを追加 (Jennさん、SecretSalamenderさん、Magnanimousさんありがとう！)
*MacとLinux用の起動用シェルスクリプトを追加しました。(croikleさんありがとう！)
*他のルール名との整合性のため、Standard-GIZAルールの名前にハイフンを追加しました。
*JRE1.5との互換性が復活しました。
*[内部変更] メインのパッケージ名をmu.nu.nullpoに変更しました。
*[内部変更] いくつかのソース中のコメントが英語になりました。

Version 7.2.0 (2010/06/19)
#このバージョンから7.1.*のサーバーとの互換性は失われます。
+Slick/SDL: 新しいタイトル画面を追加しました。(Zirceanさんありがとう！)
+Swing/Slick/SDL: ゴーストを枠線で表示する設定("OUTLINE GHOST PIECE")を追加しました。
+NET-VS MAP EDIT: 新しい機能「GRAY->?」を追加しました。使用すると、フィールドのすべての灰色ブロックにランダムに色を付けます。
+NetPlay/NetServer: ルールデータ、マップデータ、(必要なときのみ)フィールドデータを圧縮して送信するようにしました。(転送量削減できるかも？)
-Slick: タイトル画面に戻ってもタイトルバーにモード名が残ったままになるバグを修正しました。
-NetPlay: 英語UIのとき本物トリップと偽トリップの区別が付かないバグを修正しました。日本語UIでは元々このバグはありません。(Wojtekさんありがとう!)
-NetPlay: 切断してもルーム一覧が残ったままになるバグを修正しました。(hebo-MAIさんありがとう！)
-NetServer: onAccept()でCPU100%になる可能性がある箇所を修正しました。
-GRADE MANIA 2: 20GがONの場合はランキングが表示されないようにしました。 (2chスレの>>779さんありがとう！)
*hebo-MAIさんの「StandardGIZA」ルールを最新版に更新しました。ソフトドロップ速度が前よりも上がっています。
*AIがプレイした場合はランキングに残らないようにしました。リプレイはちゃんと残ります。(SWRさんありがとう！)
*[内部変更] すべてのAIプレイヤーがDummyAIを継承するようにしました。
 従来はAIPlayerインターフェースを実装していましたが、この変更以降はAIPlayerに新しいメソッドが追加されても既存のコードの変更が必要無くなります。
*[内部変更] PoochyBot/Crypt: 別のパッケージに移動しました。(前者はnet.tetrisconcept.poochy.nullpomino.ai、後者はorg.cacas.java.gnu.tools)

Version 7.1.0 (2010/06/15)
#このバージョンから7.0.*のサーバーとの互換性は失われます。
#作者はこのバージョンから「NullNoname ◆bzEQ7554bc」の名前とトリップを使います。
+Poochyさん制作のAI「PoochyBot v1.21」を追加しました。Poochyさんありがとう！
+Swing/NetPlay: ネットプレイサーバーの「監視」機能がSwing版に対応しました。
+NetPlay: ゲーム結果表に新たに「KO」「勝利数」「試合数」の項目を追加しました。
+NetPlay: チャットログを自動的にlogフォルダ内に保存するようにしました。
+NetServer: トリップ機能を追加しました(2chにあるようなあれです)。
 名前欄に#記号の後にパスワードを入れると暗号化された文字列が表示されます。
 Wikipediaでのトリップの記事：
 http://ja.wikipedia.org/wiki/%E3%83%88%E3%83%AA%E3%83%83%E3%83%97_(%E9%9B%BB%E5%AD%90%E6%8E%B2%E7%A4%BA%E6%9D%BF)
+NetServer: 全員のIPまたはホスト名を公開する機能を追加しました。表示タイプは、暗号化されていない平文と、暗号化された文字列の2種類から選べます。
 "config/etc/netserver.cfg"を任意のテキストエディタで編集して設定を変更します。
+hebo-MAIさん作成のルール「StandardGIZA」を追加しました。(hebo-MAIさんありがとう！)
-PHANTOM MANIA: Lv400または900になったときに段位表示が再度点滅するバグを修正しました。 (Zirceanさんありがとう！)
-GRADE MANIA 3: 暫定GMがMMではなくGMとして表示されるバグを修正しました。 (Zirceanさんありがとう！)
 結果画面で暫定GMは光るMMとして表示されるようにしました。
-NetPlay: 3人以上の対戦で誰かが脱落してもBGMが止まらないようにしました。 (SWRさんありがとう！)
*NetServer: メッセージ送信が失敗したら即クライアントを切断するようにしました。(CPU100%バグ対策)
*NetServer: 全員が切断したらルーム一覧や各種メモリ内容の掃除をするようにしました。(幽霊部屋バグ対策)
*NetPlay: Wojtekさんの新しい断片的邪魔ブロックシステムを採用しました。
*すべてのログファイルがlogフォルダに保存されるようにしました。

Version 7.0.2 (2010/06/06)
#ネットプレイ機能は7.0.0のサーバーと互換性があります。サーバーに変更はありません。
+NetPlay: ルーム作成画面の一部の設定項目にツールチップを追加しました。
-NetPlay: 自分のブロックの絵柄が他のものにすり替わるバグを修正しました。
-NetPlay: 他人が作った部屋で「設定確認」ボタンを押すとルーム作成画面のデフォルト値が他人のルームのものになってしまうバグを修正しました。
-前のバージョンでいろいろなネイティブライブラリが入ってなかったのを修正しました。

Version 7.0.1 (2010/06/06)
#ネットプレイ機能は7.0.0のサーバーと互換性があります。サーバーに変更はありません。
+更新チェック機能を追加しました。新しいバージョンが公開されるとタイトル画面(Swing版ではモードセレクト画面)で通知されます。
 今のところ更新チェックの設定画面はSwing版だけです。
 デフォルトでは最新版のチェックにBurbrueeさんのXMLを使用しています。使用するXMLはSwing版の設定画面から変更できまする
+ゴーストピースの真上にNEXTを表示する設定を3つすべてのバージョンに追加しました。(Wojtekさんありがとう！)
+Slick: キーボード設定画面でキーコードではなくキーの名前が出るように修正
+NetPlay: ルーム画面に「設定確認」ボタンを追加しました。ルームから出ることなく各種設定を確認できます。
-NetPlay: バグを避けるため、参戦/観戦ボタンは押したあとサーバーから返答が来るまで無効化(Wojtekさんありがとう！)
-NetPlay: 観戦ボタンとチーム変更ボタンは参戦中かつゲーム中は無効化(ゲーム中にこれらのボタンはもともと効果がなかったから)
*NetPlay: 断片的邪魔ブロックシステム使用時の邪魔ブロックメーターの表示を改善
*STANDARD-ZEROルールを最新版に更新(回転ボタンは他のSTANDARD系と同じになりました)
*更新チェック機能の一部にJRE1.6以上が必要になったので、JRE1.5以下では動作しなくなりました。
*LWJGL 2.4.2をlib/LWJGL2_4_2フォルダ以下に同梱
 (64bitのWindowsの人でそのままだと動かない場合はこの中の全ファイルをlibフォルダにコピーしてみてください)

Version 7.0.0 (2010/06/04)
#このバージョンから6.9.0.*のサーバーとの互換性は失われます。
+Ｔ以外のスピン判定を追加(T-Spinボーナスがあるモードで、「SPIN BONUS」の設定を「ALL」に変えると有効化できます)
+VS-BATTLE/NetPlay: マップの設定を追加しました。マップを有効にしている場合、一定のパターンでブロックが積み上がった状態でゲームが始まります。
 マップは新しく追加された「TOOL-VS MAP EDIT」モードで作成できます。
+NetPlay: 「断片的邪魔ブロックシステムを使う」の設定を追加しました。
 3人以上のゲームで、攻撃力が参加人数に応じて割り算されます。これにより小数点以下の攻撃(0.5など)も発生するようになります。
 小数点以下の攻撃は、邪魔ブロックメーターが1.0以上になるまでせり上がりませんが、こちらの攻撃で相殺して0にしない限り相手に攻撃できません。
+NetPlay: 「誰かがキャンセルしたらタイマー無効化」の設定を追加(6.9.0.1で削除した機能を自由に切り替えできるようにして復活)
+NetPlay: ルーム作成画面の設定をタブに分けました。速度関連の設定は「速度の設定」タブに移しました。

Version 6.9.0.2 (2010/05/08)
#ネットプレイ機能は6.9.0.*のサーバーとまだ互換性がありますが、このバージョンのサーバーにいくつかのバグ修正があります。
+NetPlay: チーム変更ボタンをロビー画面とルーム画面に追加
+Swing: モード名をダブルクリックするだけでゲームを開始可能
-Slick: 64bitのLWJGLのライブラリが2.1.0用に戻っていなかったのを修正
-NetPlay: フィールド更新処理でArrayIndexOutOfBoundsExceptionが発生するバグを修正
-NetServer: チーム戦のいろいろなバグを修正
-NetServer: 順番待ちが正しく動いていないバグが直ったかも

Version 6.9.0.1 (2010/05/07)
#サーバーに小さい変更点がありますが、ネットプレイ機能は6.9.0.0のサーバーとまだ互換性があります。
+STANDARD-PLUS(Blinkさん作)とSTANDARD-ZEROルール(Wojtekさん作)を追加しました。
+起動用バッチファイルを32bitと64bitの両方で動くバージョンに変えました。(doddさんありがとう！)
+NetPlay: まだまともなGUIはありませんが、チームをロビーまたはルーム画面から変えられるようにしました。
 チャット入力欄に「/team <チーム名>」と入力してEnterキーを押すと所属するチームを変更できます。
 チーム無しにするには「/team」とだけ入力してください。
*NetPlay: Ping(接続テストメッセージ)をサーバーに打つ間隔を10秒に変更しました。また、30秒以上サーバーから応答がない場合は切断します。
*NetServer: 「OK表示を取り消すとタイマー無効化」機能を削除しました。(次バージョンで任意にON/OFFできる形で復活予定)
*Slick: LWJGLのバージョンを2.1.0に戻しました。(2.4.2で不具合が出る人が多かったので)

Version 6.9.0.0 (2010/05/06)
#このバージョンから6.8のサーバーとの互換性は失われます。
+「DIG RACE」モードを追加しました。
+GARBAGE MANIA: BIG有効時でも正常にプレイ可能になりました。
+SDL/Slick: 初回起動時のキーボード設定画面のあと、ルール選択画面が出るようにしました。
+NetPlay: 実験的なチームプレイ機能を追加しました。同じチームに所属するプレイヤーは攻撃を受けたり、与えたりすることはありません。
 誰かが脱落し、残ったプレイヤー全員が同じチームに所属していた場合はその時点でゲームが終了し、勝利したチーム名がチャットログに表示されます。
+NetPlay: 「TNET2型の自動スタートタイマーを使う」の設定を追加しました。ONにすると自動スタート機能に以下の変化が現れます。
 1.2人以上がOK表示を出すまでタイマーが始まらなくなる
 2.タイマーが0になったとき、OK表示を出していないプレイヤー全員が自動的に観戦モードになる
 3.誰かがOK表示を取り消したとき、次のゲームが始まるまでタイマーが作動しなくなる
-NetPlay: 以下のバグを修正しました。
 1.ゲーム進行中の部屋に入ったときに、自分のフィールドの邪魔ブロックメーターが溜まっていく(警告音も鳴る)ことがある
 2.邪魔ブロックメーターに20ライン以上の邪魔ブロックが溜まっていると、黒い線がメーター付近に表示される
 3.1人を除く全員がOK表示を出しているとき、その1人が観戦モードに移行するか退出すると、ゲームが始まらない
 4.順番待ちしているときに誰かが退出してその人と入れ替わっても、フィールド表示が小さいままで、OK表示も出せない
 バグ発見したdoddさんありがとう！
*Slick: LWGJLのバージョンを2.4.2に更新しました。

Version 6.8.0.0 (2010/04/30)
#このバージョンから6.7のサーバーとの互換性は失われます。
+GAME TUNING(チューニング設定)を追加しました。一部の設定を使用するルールに関係なく固定できます。
 Aボタンの回転方向、ブロックの絵柄、横溜めと横移動速度を変更できます。
 SDL/Slick版ではCONFIG画面の「GAME TUNING」に、Swing版では「設定→1P(2P) チューニング設定」にあります。
 設定内容はSDL/Slick/Swing全てで共有され、ネットプレイでも使用できます。(ルール固定部屋でも使用可能)
+SDL/Slick: ジョイスティックの設定画面を強化＆ジョイスティックテスト画面を追加しました。
 ほとんどのジョイスティック設定はGENERAL OPTIONSからここに移動しました。
+NetPlay: ルーム作成画面に「3人以上の対戦では攻撃力を減らす」の設定を追加しました。
 3人以上で対戦したとき、全員の攻撃力が2人対戦のときと比べて抑え気味になります。
 脱落者が増えると攻撃力は増して行きます。
+VS-BATTLE/NetPlay: HURRY UP関連の設定を追加しました。
 指定した秒数が経過するとアラーム音が鳴り、下から消せない床ブロックがせりあがってきます。
 デフォルトでは5回ブロックピースを置くと床ブロックが上がりますが、これも設定で変更できます。
 (VS-BATTLEでは「INTERVAL」、ネットプレイでは「HURRY UP後、床を上げる間隔」で変更可能)
+NetPlay: 1回決着がつくごとにガベージコレクションを実行するようにしました。
-NetPlay: ルーム情報を削除するときにそのルームのプレイヤーリストを完全に掃除するように
-NetServer: 不完全パケットが残っているプレイヤーが切断した場合にその不完全パケットが残り続けるバグを修正
-SDL/Slick: "SCREEN SHOT"が"SCRREN SHOT"になっていた箇所を修正
*GEM MANIA: Ready&Goの時間を短縮

Version 6.7.0.0a (2010/04/12)
#このバージョンは小さいバグ修正パッチです。使うには前バージョン(6.7.0.0)に上書きが必要です。
#修正するのはネットプレイ用サーバーだけでゲーム内容に変更はありません。
-NetServer: "broken pipe"のIOExceptionがdoWrite(SocketChannel)で大量に出るバグの対策
 1.doWrite(SocketChannel)のログレベルをerrorからdebugに引き下げ
 2.送信エラーが発生したときはそのクライアントを切断するように(たぶんもうそのクライアントに対しては何も送信できないので)
-PHANTOM MANIAの最新版ソースが前バージョンに入ってなかったので一緒に入れました

Version 6.7.0.0 (2010/04/10)
#このバージョンから6.6のサーバーとの互換性は失われます。
+NetPlay: 速度・T-Spin・B2B・コンボの設定を追加
+NetPlay: ルーム画面で他のプレイヤーのプレイ結果表示を追加
+NetPlay: 自動スタートタイマー機能を追加(半分以上のプレイヤーの準備が完了するとこのタイマーが動きます。0秒にすると従来通りタイマーなし)
+NetPlay: AIを使用可能に(ただし1人プレイや練習でFボタンを使えないという問題あり)
*NetServer: ログファイルの出力設定のデフォルト値を変更(これで合計50MBを超えないはず)
-PHANTOM MANIA: LV999まで行ったあとの緑・橙ラインの状況がランキングに記録されないバグを修正
-PHANTOM MANIA: ROメダルの表記がSKメダルになっていたバグを修正

Version 6.6.0.1 (2010/04/06)
#マイナーバージョンアップです。ネットプレイ機能は6.5のサーバーと互換性があります。
+以下のモードにベストセクションタイムの記録機能を追加
 GRADE MANIA (全部)
 SPEED MANIA (全部)
 GARBAGE MANIA
 PHANTOM MANIA
 SCORE ATTACK
 FINAL
+GRADE MANIA 3: ランキングを試験機能のON/OFFで分けてみた
+AI: 「T-SPIN」AIを追加(まだWIP)
+NetPlay: 1人だけで始めたときは練習モードと同様にFボタンでゲームを強制終了できます
-NetPlay: 負けたときに勝った相手のフィールドに「WIN!」または「1ST PLACE!」のメッセージが出ないことがあるバグを修正
-GRADE MANIA 2/SPEED MANIA: AREが有効のルールでREメダルが手に入らないバグを修正
-TECHNICIAN: LV15-EASYとLV15-HARDのレベル15をライン無しT-Spinで通過するとラインを消すまでゲームが終わらないバグを修正

Version 6.6.0.0 (2010/04/03)
#このバージョンから6.5のサーバーとの互換性は失われます。
+SDL/Slick/NetLobby: ネットプレイサーバーの「監視」機能を追加
 ネットプレイのサーバー選択画面で監視機能を設定できます。
 監視機能を有効にすると、現在何人のプレイヤーがオンラインなのか画面下にいつでも表示されるようになります。
 プレイヤー数の表示色は、今自分しかいないなら青、自分以外にも誰かNullpoMinoを起動してるけど誰もロビーにいないなら緑
 誰かロビーにいるなら赤で表示されます。
+NetPlay: ルール固定機能を追加(ルーム作成画面で有効にするとみんな自分と同じルールでプレイします)
+NetPlay: 2人対戦で相手のNEXTとHOLD表示を追加(ただしまだルール固定機能を使っているときのみ)
+NetPlay: 勝ち数表示追加(ただし部屋を出るとリセット)
+NetPlay: 5分以上サーバーから応答がなければ自動切断
+GRADE MANIA 3: 段位認定試験関連のデバッグログを出力するように
-GRADE MANIA 3: Lv999のロールを生き残ったときに段位認定試験に合格しても認定段位が更新されないバグをたぶん修正

Version 6.5.0.1 (2010/04/02)
#マイナーバージョンアップです。ネットプレイ機能は6.5のサーバーと互換性があります。
+NetLobby: 各リストボックスに右クリックメニュー追加
+NetPlay: 現在のプレイヤー数表示などをゲーム画面に追加
+Swing/NetPlay: Swing版でもプレイヤー名がゲーム画面に表示されるようになった
-Slick: "test.png"という使われていない画像が読み込まれていたのを修正(消し忘れorz)
*GRADE MANIA 3: 降格試験などのバグ修正

Version 6.5 (2010/03/31)
+ネットプレイ追加(α版)
+VS-BATTLE:邪魔ブロックパターンに「1-ATTACK」を追加(NORMALよりもバラバラになりにくくONE RISEよりもバラバラになりやすい)
+VS-BATTLE:「SE」オプション追加(効果音無効化)
-GRADE MANIA 3:試験が発生すると目標段位が必ずMMになるバグをたぶん修正(修正遅くてごめんorz)
-RETRO MANIA:スコア・ライン・レベルがカンストしないバグを修正
-SDL/Slick:設定画面のメインメニューで左を押すと0Pになるバグを修正(Poochyさんありがとう！)
-Core:Field.getHighestPieceY(int)とField.getValleyDepth(int)のバグをたぶん修正(Poochyさんありがとう！)
*Slick:ジョイスティック設定画面のボタン判定方法を変更(もっと多くのジョイスティックが動くようになったかも)
*Mac OS用ジョイスティック関連ライブラリ(lib/libjinput-osx.jnilib)をSnow Leopard対応のものに差し替え
 (http://ariejan.net/2009/09/01/jinput-mac-os-x-64-bit-natives/)

Version 6.4 (2010/03/02)
+ミュージックリストエディタ追加 (音楽の設定が簡単に)
+GEM MANIAモード追加 (すべての宝石ブロックを消すとステージクリア)
+MARATHON+モード追加 (レベル20をクリアするとボーナスレベル出現)
+TIME ATTACK,CLASSIC MARATHON,FINALモードをNullpoUE build 010210から追加
+TIME ATTACKモードをBGM対応
+TIME ATTACKに難易度BASIC追加
+TIME ATTACKのHELLにより正確なHiddenギミックを追加
+GRADE MANIAでGMになったときにタイムによって結果画面に評価が出る(NullpoUE build 010210)
+RETRO MANIAに「POWERON」オプションを追加 (電源パターン。毎回同じ順番でブロックが落ちてきます)
+SDL版とSlick版に宝石ブロックを消したときのエフェクトを追加
-Slick版で一部ビデオカードでライン消去エフェクトが化ける可能性があったのを修正
-Swing版で半透明なブロックが正常に描画されないバグを修正

Version 6.3 Beta (2010/01/02)
+CLASSIC MARATHONモードとFINALモードをNullpoUE build 010210から追加

Version 6.3 Alpha (2010/01/02)
*ログ出力にlog4jを使うように（DOS窓は出なくなりましたがSwing版やツール類でも外部ライブラリが必要になりました）
*背景をNullpoUEのものに変更（前の背景はres/graphics/oldbgにあります）
*ブロック画像をNullpoUEのものに変更（前のブロックはルールエディタで選択可能）
+NullpoUE build 010210のルールを追加
+RETRO MANIA, PHANTOM MANIA, SCORE ATTACKモードをNullpoUE build 121909から追加
+GRADE MANIA 3で段位試験を追加（NullpoUEより追加）
+PRACTICEモードのメニューデザイン変更
+PRACTICEモードでLEVEL TYPEがNONEのときにゴールになるライン数を設定可能に
+PRACTICEモードで[]ブロックを使う設定を追加
+PRACTICEモードでレベルアップすると時間制限をリセットする設定を追加
+PRACTICEモードで初期配置フィールドを使えるように
（フィールドエディット画面の操作 UP/DOWN/LEFT/RIGHT:カーソル移動 A:配置 B:終了 C+LEFT/RIGHT:色選択 D:削除）
*他にもあったかも…

Version 6.2 (2009/10/29)
Swing版が（一応）復活
ルールエディタのブロックの絵柄の設定をコンボボックスに変更し、画像を見ながら設定できるように
LWJGLを2.1.0にバージョンアップ（64bit対応を強化・Linuxのamd64でもサウンドが動作するように）
MARATHONモードに200ラインタイプを追加
MANIA系モードで先行ホールドを使うとレベルが増えないバグを修正(Thanks Zircean)
MANIA系モードに裏段位を追加(Thanks Zircean)
PRACTICEモードで出現するブロックの設定を追加（1～3個の小さいブロック初登場）
エラーログがちゃんとファイルに出力させるようにバッチファイルを修正

Version 6.1 (2009/08/20)
再びMac OS Xで動くようになったかも
AIでマルチスレッドを使わない設定項目を追加（主にデバッグ用。AIはすごく重いので対戦では適しません）
SPEED MANIAとGARBAGE MANIAのエンディングでFボタンを押し続けるとエンディングの残り時間の短縮が可能に

Version 6 (2009/08/17)
かなりの部分を作り直し
TECHNICIANモードとGARBAGE MANIAモードとVS-BATTLEモードを追加
AIを追加（CONFIG>AI SETTINGからON/OFFが可能です。たまにT-Spinはするけど弱い）
Slick版のゲームパッド機能を暫定復活（判定方法を複数から選べます）
Swing版は問題だらけなので一旦消しました（ルールエディット機能はルールエディタとして独立しました）

Version 5.5 (2008/12/30)
SCORE RACEモードを追加
効果音と音楽の音量を変える設定を追加
コンボしたときの効果音を追加
SDL版でもBGMのフェードアウトが正常動作するように修正

Version 5.4+ (2008/11/29)
SDLバージョンを追加
ゲームエンジンに変更はないのでバージョン表記は5.4のままです。

Version 5.4 (2008/11/25)
GRADE MANIA 3モードとSPEED MANIA 2モードを追加
ライン消去エフェクトを追加（まだSlick版のみ。CONFIG画面でOFFにできます）
Slick版にもプレイ中のモード名をタイトルバーに表示する機能を追加

Version 5.3 (2008/10/29)
横溜め周りの処理を変更
CLASSIC系のルールでBIGモードでの壁蹴りの仕様を変更（1マスだけずれたまま壁蹴りすると壁蹴り後もずれたままになります）
ブロックの出現位置を補正するオプションをルールカスタマイズ画面に追加
リプレイ追記機能を追加（リプレイ中にポーズしてDボタンを押すと自分でプレイできます。ただし改変したリプレイファイルは少しサイズが増えます…）

Version 5.2 (2008/10/05)
Slick版で効果音のデフォルトをOFFに変更（OpenALが無い環境でも動かせるように）
Slick版にも最大FPSの設定を追加
フレームステップ機能を追加（設定画面のFRAME STEPをONにするとポーズ中にFボタンを押すと1フレームだけゲームが進みます）
PRACTICEでクリアレベルを設定できる機能を追加
ホールドを使用したときに1フレーム分無駄な時間が発生してしまうバグを修正
ブロックを固定したときの光る時間が設定よりも1フレーム短くなっているバグを修正

Version 5.1 (2008/09/19)
BIGモード追加（ブロックピースの大きさ2倍）
PRACTICEモードでレベルタイプを選択できるように
先行回転を認識しないことがあるバグをたぶん修正

Version 5.0 (2008/09/12)
変更点ありすぎて思い出せないorz
Swingバージョン追加
ルールカスタマイズ機能追加
リプレイの仕様変更（過去のバージョンと互換性はありません）
リプレイにスコアやタイムを記録するように（まだ使ってはないですが）
ゲームパッドに詳しい人が現れるまでゲームパッド廃止
各モードに簡単なランキングを追加

Version 4 (2008/08/10)
SPEED MANIAモードを追加
ゲームパッドの十字ボタンの判定方法をまた変更
エラーログをlog.txtに出力するようにplay.batを変更（不正終了したらlog.txtをどっか適当なアップローダーに上げて報告してください）

Version 3 (2008/08/05)
ゲームパッドの十字ボタンの判定方法を変更（どうやらボタンじゃなくてスティックでないと認識しないっぽいので）
GRADE MANIA 2モードを追加
PRACTICEモードを追加（今のところただの時間無制限版ULTRAモード）
リプレイの量が増えると画面下のルール名とモード名の文字に重なるバグを修正
他にもいろいろやった気がするけど忘れたorz

Version 2 (2008/07/29)
画面左下のFPS表示を消す設定をGAME OPTIONSに追加
GRADE MANIAモードに「LVSTOPSE」の設定を追加（ONにするとLV*99およびLV998に到達したときにlevelstop.wavが鳴ります）
各モードのプレイ画面にモード名の表示を追加
フィールドを囲むフレームの色がモードによって変わるように
ゲームパッドの十字ボタンを認識しないバグをたぶん修正
GRADE MANIAモードでホールドを使用したときにもレベルが増えてしまうバグを修正

Version 1.01 (2008/07/29) ※バイナリ・ソースに変更はありません。表示はVersion 1のままです。
ゲームパッドを使用するために必要なライブラリを入れてなかったのを修正
LinuxとMacを動かす方法をreadmeに追加
英語版readmeを追加

Version 1 (2008/07/27)
初公開（まだまだテスト版）

【TODO】
・まともな説明書を作る
・CONFIG画面の設定項目の説明を作る
・Swing版をなにかまともな別のものにする
・PRACTICEモードの設定項目を増やす
・AIを強くする
・パズルモード
・readme_en.txtのTODOも見てね
