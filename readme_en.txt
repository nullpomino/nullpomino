NullpoMino
Version 7.4.0

1. What is this?
A falling block puzzle game using Java.

2. How to run
This game needs Java Runtime Environment 1.5 or newer version to run. http://www.java.com/

Windows:
	Double-click "play_swing.bat", "play_slick.bat", "NullpoMino.exe" or "play_sdl.bat".

	"play_swing.bat" starts Swing version of NullpoMino.
		(Does not use any OS-dependent libraries. However, performance and sound quality are poor.
		 There is no support for joysticks. BGM is also missing.)
	"play_slick.bat" or "NullpoMino.exe" starts Slick version of NullpoMino.
		(An OpenGL compatible video card required, some PCs may have problems with keyboard.
		 Has limited support of joysticks.)
	"play_sdl.bat" starts SDL version of NullpoMino.
		(Aside from random crashing bug which I can't fix it, it's most stable for 32bit PCs, I think.
		 Has best support of joysticks.
		 Won't work with 64bit PCs.)

	"ruleeditor.bat" runs Rule Editor, where you can create your own ruleset.
	"sequencer.bat" runs Sequence Viewer, which should be helpful for TASing. (Created by Zircean)
	"musiclisteditor.bat" runs MusicListEditor, where you can configure which music file to use.
	"netserver.bat" runs NetServer (ALPHA!), which is a server for netplay feature. More details later.
	"netadmin.bat" runs NetAdmin, which can be used to manage your running NetServer.

Linux:
	First, open a X-terminal emulator. Next, navigate to the folder where the archive was extracted.
	(Use "ls" and "cd" command to navigate the folder)

	Finally, enter the following commands:

	To start Swing version:
chmod +x play_swing
./play_swing

	To start Slick version:
chmod +x play_slick
./play_slick

	To start SDL version:
chmod +x play_sdl
./play_sdl

	To start rule editor:
chmod +x ruleeditor
./ruleeditor

	To start Sequence Viewer:
chmod +x sequencer
./sequencer

	To start MusicListEditor:
chmod +x musiclisteditor
./musiclisteditor

	To start NetServer:
chmod +x netserver
./netserver

	To start NetAdmin:
chmod +x netadmin
./netadmin

	Note: You don't have to execute chmod command from 2nd time.

	Depending on your video card and Linux version, you might encounter small or big problems.

	Swing version problems:
		It's not working yet.
		The performance is under 3 FPS, and most SFX won't load.

	Slick version problems:
		Turn off 3D desktop (such as Beryl) to run smoother.

		Because of a bug (or limitation) of SCIM and LWJGL, the "play_slick" shell script will disable any IME by default.
		In the "play_slick" shell script, "XMODIFIERS=@im=none" is not needed if your system don't have SCIM.

		If you want to run the game with SCIM enabled, try the following commands (you need access of sudo):

sudo chmod go+r /dev/input/*
java -cp bin:NullpoMino.jar:lib/log4j-1.2.15.jar:lib/slick.jar:lib/lwjgl.jar:lib/jorbis-0.0.15.jar:lib/jogg-0.0.7.jar:lib/ibxm.jar:lib/jinput.jar -Djava.library.path=lib mu.nu.nullpo.gui.slick.NullpoMinoSlick -j

		The first command will allow everyone (including the game itself) to read keyboard input directly.
		You don't have to execute this command again until you reboot/shutdown your operating system.
		The second command will run the game with "-j" option.
		Normally, the game will read keyboard input from LWJGL, which conflicts with SCIM.
		However, when this option is used, the game will try to read keyboard input directly from your operating system.
		So you can play the game with SCIM enabled.

	SDL version problems:
		If you tried SDL version but it didn't work, you need to manually install libsdl.
		In Ubuntu 8.04, I think you can install it with following command:
sudo apt-get install libsdl1.2debian
		SDL version requires i386 architecture CPU and 32bit OS to run.

Mac OS X:
	Unfortunately I don't own any Mac OS X PC now :(
	Slick version does seem to work with Linux version commandline.
	SDL version does not work, because sdljava (Java SDL wrapper) doesn't support Mac OS X.
	I don't know anything about Swing version.

3. How to play
A piece made of blocks falls from top of the field.
You can do these actions to the piece until the piece lands on something: move, rotate, or drop.
When the piece lands on something (eg. floor or other blocks), the piece locks, then the new piece will appear from top of the field.
You can erase blocks by filling a horizontal line without gap (In other words, connect 10 blocks to horizontal).
The game ends when the pile of blocks reaches to top of the field.

4. Controls

* Description of buttons
	UP:Hard Drop (Drop current piece instantly) ; Move cursor up
	DOWN:Soft Drop (Drop current piece faster) ; Move cursor down
	LEFT:Move left ; Decrease current option's value
	RIGHT:Move right ; Increase current option's value
	A:Rotate ; Confirm
	B:Reverse Rotate ; Cancel
	C:Rotate
	D:Hold (Keep a piece to use later)
	E:180-Degree Rotate
	F:Skip ending credits (SPEED MANIA and GARBAGE MANIA modes)
	QUIT:Quit the game
	PAUSE:Pause the game
	GIVEUP:Return to the title screen
	RETRY:Reset the game and restart from beginning
	FRAME STEP:Frame step (pause screen)
	SCREEN SHOT:Save screen shot to ss folder

* Default keyboard mappings in menu
+-------------+------------+------------+------------+
| Button Name |  Blockbox  | Guideline  | NullpoMino |
|             | (Default)  |            |  Classic   |
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

* Default keyboard mappings in game
+-------------+------------+------------+------------+
| Button Name |  Blockbox  | Guideline  | NullpoMino |
|             | (Default)  |            |  Classic   |
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

You can change key mappings in CONFIG screen.

* Reset
If you want to reset settings, delete the following file(s):
	Swing: config\setting\swing.cfg
	Slick: config\setting\slick.cfg
	SDL: config\setting\sdl.cfg
	Global settings: config\setting\global.cfg
	High-scores: config\setting\mode.cfg

5. Game rule
Depending on the game rule, the movement of pieces will be different.
You can select which rule to use in the CONFIG>RULE SELECT screen.
You can create your own rule by using Rule Editor.

AVALANCHE        : A rule used to play AVALANCHE type games.
CLASSIC0         : A classic rule that many Japanese players played like a monkey. Best suitable for RETRO MANIA mode.
CLASSIC0-68K     : CLASSIC0 with reverse rotation.
CLASSIC1         : Only 1 piece preview, no hold function, no hard drop function, less flexible wallkicks.
                   If you think standard rules are too easy, this rule might be suitable for you.
CLASSIC2         : If you want faster play but dislike standard rules, this rule can help you.
CLASSIC3         : If you dislike standard rules but classic 1-2 is too difficult, this rule is for you.
                   I and T shaped pieces has more flexible wallkicks.
CLASSIC-EASY-A   : It is much easier than other classic rules. Block colors are also different.
CLASSIC-EASY-A2  : Almost same as CLASSIC-EASY-A, but block colors are not different from others.
CLASSIC-EASY-B   : Soft and hard drop behavior is reversed in this rule.
CLASSIC-EASY-B2  : Almost same as CLASSIC-EASY-B, but block colors are not different from others.
CLASSIC-S        : CLASSIC0 with reverse rotation and "Wall Only" wallkick. It can kick wall, but not the already placed blocks.
DTET             : That game was so good that the evil king had to kill it. This rule is a bit tricky. Has ARE/line clear canceling.
NINTENDO-L       : A classical rule that was bundled with a handheld gadget which was made by king of video game.
NINTENDO-L-FAST  : NINTENDO-L with faster sideways movement.
NINTENDO-R       : A classical rule that was appeared before or after NINTENDO-L. Best suitable for RETRO MARATHON mode.
NINTENDO-R-FAST  : NINTENDO-R with faster sideways movement.
PHYSICIAN        : A rule used to play PHYSICIAN type games.
SPF              : A rule used to play SPF type games.
SQUARE           : Rule designed to make it easier to build squares. Best suitable for SQUARE mode.
STANDARD         : Suitable for almost all kinds of players.
STANDARD-EXP     : Soft and hard drop behavior is reversed in this rule.
STANDARD-FAST    : Suitable for fast game play.
STANDARD-FAST-B  : Suitable for fast game play. Initial actions (initial rotation/hold) is less detected in this rule.
STANDARD-FRIENDS : Pieces will spawn one space lower than usual, unless that space is occupied.
STANDARD-GIZA    : hebo-MAI's rule. This rule is not too fast; giving you the opportunity to see the opponent's field in VS game.
STANDARD-HARD    : Difficult than normal STANDARD rule.
STANDARD-HARD128 : Slightly easier variant of STANDARD-HARD rule. You can move/rotate the piece 128 times.
STANDARD-HOLDNEXT: A rule created by holdnext. Enjoy the power of orange sticks.
STANDARD-J       : This variant has slow movements, nothing more than that.
STANDARD-PLUS    : A rule created by Blink. STANDARD-FAST with no line clear delay, and faster softdrop.
STANDARD-SUPER3  : A classical rule with the rotation system of STANDARD rules but no wallkicks. Has ARE canceling.
STANDARD-ZERO    : A rule created by Wojtek. STANDARD-PLUS with 20G Soft Drop and Instant DAS.

6. Game mode
MARATHON
	Mode for beginner players. The level increases by erasing every 10 lines.
	There are three game types: 150 lines, 200 lines, and endless.

MARATHON+
	This mode is similar to MARATHON mode (200 lines game), but has following differences:
	* Line clear speed is faster.
	* If you complete level 20, you'll enter the 21st level: the "Bonus Level".
	  This level never ends (until you die), but the playfield occasionally goes invisible in this level,
	  so it's better to remember the shape of your playfield.
	  You can directly start from the bonus level by setting starting level to 21.

EXTREME
	Mode for expert players. Gameplay is similar to marathon mode, but it's much faster.

LINE RACE
	Clear certain number of lines as fast as possible.
	Goal is selectable from 20, 40, and 100 lines.

SCORE RACE
	Get certain number of points as fast as possible.
	Goal is selectable from 10000, 25000, and 30000 points.

DIG RACE
	Clear all garbage lines as fast as possible.
	You win the game when you clear the bottommost line (the line with gem blocks).
	Amount of garbage lines is selectable from 5, 10, and 18.
	
COMBO RACE
	Try to clear all the lines in the well in one combo.
	Goal is selectable from 20, 40, and 100 lines.

ULTRA
	Score as many points as possible or clear as many lines as possible until the time limit.
	Duration is selectable from 1 through 5 minutes.

TECHNICIAN
	The main goal of this mode is to clear each level as fast as you can.
	When the "GOAL" counter reaches zero, the level increases.
	You can progress the game faster by erasing multiple lines.
	There are five different game types.
	LV15-EASY:  Reach level 16 as fast as you can.
	            There is 2-minute level timer, but it's just a bonus counter and there is no penalty for running out of time.
	LV15-HARD:  This is similar to LV15-EASY, but when the 2-minute level timer runs out, your game ends instantly.
	10MIN-EASY: See how far you can go and how many points you can get within 10 minutes.
	            When the 2-minute level timer runs out, the goal counter resets.
	10MIN-HARD: This is similar to 10MIN-EASY, but when the 2-minute level timer runs out, your game ends instantly.
	SPECIAL:    When the level increases, 30 seconds is added to your time limit. Survive as long as you can.
	
SQUARE
	Try placing your pieces in 4x4 squares to get more points.
	There are three different game types.
	MARATHON: 	Keep playing until you top out. Go for the most points!
	SPRINT:		Try to get 150 points as fast as you can. Can you do it in 8 lines?
	ULTRA:		Get as many points as you can in 3 minutes.
	Best effect can be achieved if you use "SQUARE" rule.

RETRO MARATHON
	A classic game that takes you to the nostalgic feeling.
	Best effect can be achieved if you use "NINTENDO-R" rule.
	
RETRO MASTERY
	A game based on the classics where efficiency is important.
	Best effect can be achieved if you use "NINTENDO-R" rule.

RETRO MANIA
	A classic game that many Japanese players played like a monkey.
	Best effect can be achieved if you use "CLASSIC0" rule.

GRADE MANIA
	You can earn "grade" by getting certain amount of score. Aim for highest grade!

GRADE MANIA 2
	If you beat GRADE MANIA mode, try this. It's much harder!

GRADE MANIA 3
	If you don't think GRADE MANIA 2 is so difficult, try this. The speed depends on your game play!

SCORE ATTACK
	Score as many points as possible before you reach level 300.
	This mode is designed for beginner players.

SPEED MANIA
	Well, it's not so fast as EXTREME mode, but it's very difficult to go through level 500 barrier.

SPEED MANIA 2
	Insane mode! Can you keep up with this crazy speed and some other dangerous things?

GARBAGE MANIA
	Speed is not fast, but garbage blocks will rise from bottom of the play field, so watch out.

PHANTOM MANIA
	This is mostly same as SPEED MANIA, but the playfield is "completely" invisible!
	Try to remember where you placed the piece.

FINAL
	This game mode is designed only for players who have super-fast-fingers and super-fast-brain.
	Try to correctly control the insane-fast pieces.

TIME ATTACK
	You have to complete each level within the time limit, or game will end in failure.
	However, the time limit is usually long, so you won't get a time over.
	The level increases by erasing every 10 lines. Time limit will be reset when the level increases.

	This mode features 11 game types.
	5 of them (NORMAL, HIGH SPEED 1, HIGH SPEED 2, ANOTHER, ANOTHER2) are easy games that ends at 150 lines.
	2 of them (NORMAL 200, ANOTHER 200) are 200 lines game but still easy.
	The rest of 4 (BASIC, HELL, HELL-X, VOID) is very difficult.
	Each level has extremely short time limit, and in the case of HELL and HELL-X, there will be more dangerous things.

PRACTICE
	You can practice various speed settings in this mode.

GEM MANIA
	This is a puzzle mode that requires different strategy than normal modes.
	Your goal is to erase all gem blocks from the playfield.
	This mode features two different types of time limit: "Stage Time" and "Limit Time".
	Stage Time is time limit for each stage. It starts from 1 minute for each stage, and if it reaches zero, the stage ends in failure.
	Limit Time is the main time limit. It starts from 3 minutes, and if it reaches zero, the game ends.
	Complete each stage within 20 seconds to increase the Limit Time.

TOOL-VS MAP EDIT
	Not really a "game" mode. This is a tool for creating maps for VS-BATTLE mode and NetPlay mode.
	
	Controls when you editing a map:
		Up/Down/Left/Right: Move cursor
		A: Put a block
		B: Exit
		C+Left/Right: Change color of block
		D: Delete a block

VS-BATTLE
	Battle against human or AI opponent. You can send garbage blocks by clearing 2 or more lines at the same time.
	
AVALANCHE 1P (RC1)
	This is a puzzle mode where the objective is to clear clusters of colors, not lines.
	Make chains to score as much as you can before you top out!
	Only for use with "AVALANCHE" rule.

AVALANCHE 1P FEVER MARATHON (RC1)
	Premade chains will drop down endlessly. Try to get the most points before time runs out!
	Only for use with "AVALANCHE" rule.

AVALANCHE VS-BATTLE (RC1)
	Clear chains to send your opponent garbage!
	Only for use with "AVALANCHE" rule.

AVALANCHE VS FEVER MARATHON (RC1)
	Premade chains will drop down endlessly. Detonate them to neutralize your garbage handicap, then send your opponent garbage!
	Only for use with "AVALANCHE" rule.

AVALANCHE VS DIG RACE (RC1)
	Dig down to the flashing gem as fast as you can. The first player to clear it wins!
	Only for use with "AVALANCHE" rule.

PHYSICIAN (RC1)
	This is a puzzle mode where you clear gem blocks by lining up blocks of the same color, like throwing pills into a jar.
	Clear more viruses at once to get more points.
	Only for use with "PHYSICIAN" rule.

PHYSICIAN VS-BATTLE (RC1)
	Clear more than one gem block at a time to send your opponent garbage. Try to clear your gem blocks first!
	Only for use with "PHYSICIAN" rule.

SPF VS-BATTLE (BETA)
	A puzzle mode based on a fighting game. Use gems to clear blocks of the same color and send garbage.
	Only for use with "SPF" rule.

7. How to add BGM
Well, currently this game does not have any BGM by default.
However, if you want, you can add any (but must not too big) music file to the game by using MusicListEditor ("musiclisteditor.bat").
If you don't want the BGM to loop, enable the checkbox next to the filename.
Supported file formats: .ogg .wav .xm .mod .aif .aiff

8. NetPlay (BETA!)
[List of features]
List of what you can do:
	* Play against other players (up to 6 players can join)
	* Create a multiplayer room
	* Create a single player room
	* Join an already existing room
	* Talk with other players
	* Spectate games
List of what you can't do & Known problems:
	* Multiplayer Replay can't be saved
	* No ID/Password system
	* No password protected rooms
	* Does not completely keep track your amount of wins
	* No chat flooding protection
	* No ignorelist/banlist
	* It has bad lobby GUI
	* It's buggy!
	* Code is messy and chaos
	* Requires a lot of RAM

[Playing the game]
To enter the netplay mode:
	1. Launch the game as usual. Any versions can be used for netplay.
	2. For Swing version, click File->Netplay from the menubar.
	   For other versions, select "NETPLAY" from the main menu.
	3. The "NetLobby" GUI will appear. You are currently in the server select screen.

To add a new server to the list:
	1. In the server select screen, click "Add..." button.
	2. You are prompted to enter hostname (or IP address) and the port number (Optional).
	   Server address format is "Hostname:Port".
	   If the server is using default port 9200, you may omit ":9200" part.
	3. Click OK button if you are done.

	To test a netplay in the local, launch the netserver.bat and add "127.0.0.1" to the server list.

	harddrop.com is running a netplay server. Thanks harddrop.com members!
harddrop.com

To connect to a server:
	1. Enter your nickname and tripcode (optional)
	   If you enter neither a name or a tripcode, then your name will be "noname".
	   A tripcode is a hashed password by which a person can be identified by others.
	   To use it, you can enter #tripcode (password followed by a sharp) to your nickname field.
	   (Wikipedia article: http://en.wikipedia.org/wiki/Tripcode)
	2. Click a server that you want to connect from the listbox.
	   (Alternately, you can just double click the server name from the listbox to connect)
	3. Click Connect button. You are currently in the lobby screen.

To create a multiplayer room:
	1. Click "Create Room" button.
	2. Enter the room name (Optional) and number of max players.
	3. Click OK button if you are done.  You are currently in the room screen.

To create a single player room:
	1. Click "Create 1P Room" button.
	2. Select a mode and a rule you want to use.
	3. Click OK button if you are done.

To enter an available room:
	Simply double click the room name from the table.
	If you want to spectate the game, but don't want to join as a player,
	right-click the room name and select "Watch" from the context menu.

To make "OK" signal to everyone:
	1. Click the game window (an window that you usually play single player games) to enable the input for game screen.
	2. Press A button to make "OK" signal to everyone.
	3. The game will start when everyone makes OK signal. Have fun!

[Starting a server]
	You can run a server by simply double-clicking "netserver.bat" if you are using Windows.
	But the port number is fixed to 9200, which is a default one.
	If you want to change the port number, or if you are using Linux or MacOS, you will need to manually use the following command:
	For Windows:
java -cp NullpoMino.jar;lib\log4j-1.2.15.jar mu.nu.nullpo.game.net.NetServer [PORT NUMBER]
	For Linux/MacOS:
java -cp NullpoMino.jar:lib/log4j-1.2.15.jar mu.nu.nullpo.game.net.NetServer [PORT NUMBER]

9. FAQ
Q: My Joystick/Gamepads doesn't work correctly in Slick version.
A: Try changing "JOYSTICK METHOD" option (JOYSTICK SETTING screen) to LWGJL, then let's mess around the rest of "JOYSTICK SETTING" options.
   Please note Joystick/Gamepads support in Slick is not so good as SDL version.

Q: Why NetPlay in SDL version eats too much RAM and crashes after few minutes?
A: Try using Swing or Slick version. I know it's a bug, but I don't have any solution yet.
   It seems large amount of RAM is eaten by SDL itself, not the Java code. If this bug is SDL itself or SDLJava, then it's out of my hand.

Q: I'm trying to run SDL version on 64bit systems but doesn't work.
A: Give up! The SDL library for Java is already abandoned years ago, thus does not support 64bit at all.

Q: My netplay rankings and single-player-records are not saved. Why?
A: Your record will not be saved if you don't use tripcode in your name.
   To add tripcode to your name, add a sharp (#) and password. (ex. entering "ABCDEF#nullpomino" to the nickname field will be "ABCDEF !gN6kJVofq6")

10. Credits/Thanks

NullpoMino created by:
	NullNoname !bzEQ7554bc (also known as pbomqlu910963, was known as Nanashi, No name, or noname) pbomqlu910963@gmail.com
	Zircean
	Poochy
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

	See also "People" section of Google Code project page:
	http://code.google.com/p/nullpomino/people/list

This game used the following tools, libraries and materials.
Thank you on this occasion.

Tools:
	Eclipse 3.6
	http://www.eclipse.org/
	PictBear SE
	http://www20.pos.to/~sleipnir/
	(Excite translation: http://www.excite.co.jp/world/english/web/?wb_url=http%3A%2F%2Fwww20.pos.to%2F%7Esleipnir%2F&wb_lp=JAEN&wb_dis=2)

Libraries:
	Slick - 2D Game Library based on LWJGL
	http://slick.cokeandcode.com/
	Lightweight Java Game Library (LWJGL)
	http://www.lwjgl.org/
	JOrbis -- Pure Java Ogg Vorbis Decoder
	http://www.jcraft.com/jorbis/
	IBXM Java MOD/S3M/XM Player
	http://www.geocities.com/sunet2000/
	sdljava - Java Binding to SDL
	http://sdljava.sourceforge.net/
	Simple DirectMedia Layer
	http://www.libsdl.org/
	Apache log4j 1.2.15
	http://logging.apache.org/log4j/1.2/index.html
	Crypt.java (Java-based implementation of the unix crypt(3) command)
	http://www.cacas.org/java/gnu/tools/
	Base64Coder
	http://www.source-code.biz/base64coder/java

Sound effects:
	ザ・マッチメイカァズ (The matchmakers)
	http://osabisi.sakura.ne.jp/m2/
	(Excite translation: http://www.excite.co.jp/world/english/web/?wb_url=http%3A%2F%2Fosabisi.sakura.ne.jp%2Fm2%2F&wb_lp=JAEN&wb_dis=2)

	TAM Music Factory
	http://www.tam-music.com/
	(Excite translation: http://www.excite.co.jp/world/english/web/?wb_url=http%3A%2F%2Fwww.tam%2Dmusic.com%2F&wb_lp=JAEN&wb_dis=2)

Background Images (res/graphics/oldbg):
	Yun Free Stock Photos
	http://www.yunphoto.net/

Font:
	オリジナルフォント【みかちゃん】 (Original Font "Mikachan")
	http://www001.upp.so-net.ne.jp/mikachan/
	(Excite translation: http://www.excite.co.jp/world/english/web/?wb_url=http%3A%2F%2Fwww001.upp.so%2Dnet.ne.jp%2Fmikachan%2F&wb_lp=JAEN&wb_dis=2)

Also thanks to:
	Lee
	Burbruee
	Steve
	Blink
	xlro (http://nullpo.nu.mu/)
	vicar (http://vicar.bob.buttobi.net/)
	SWR
	hebo-MAI
	tetrisconcept.net http://www.tetrisconcept.net/
	Hard Drop http://harddrop.com/
	 (NullpoMino Topic: http://harddrop.com/forums/index.php?showtopic=2035
	  NullpoMino Guide: http://harddrop.com/forums/index.php?showtopic=2317
	  NullpoMino on HD wiki: http://harddrop.com/wiki/index.php?title=NullpoMino)
	Puyo Nexus http://www.puyonexus.net/

11. Project Page at Google code
http://code.google.com/p/nullpomino/

12. Update History (The date and time is in JST)
+ means new feature, - means bugfix, * means other updates, # means some extra notes.

Version 7.4.0 (2010/10/29) {r277-r518; Unstable Release}
#This version is NO LONGER compatible with 7.3.0 netplay server.
+Swing/Slick/SDL: Added bigger side-preview option (Enable both "SHOW NEXT ON SIDE" and "BIG SIDE NEXT" in the general options screen)
+Swing/Slick/SDL: Can use different keyboard mappings in menu screens (This is in debate. Your feedback helps us.)
+Swing/Slick/SDL: Default rotation is now left instead of auto. You may want to go "GAME TUNING" menu and check or change "A BUTTON ROTATE" option.
+Swing/Slick/SDL: Added recommended-rule selector (appears after you select a mode).
+Slick/SDL: Can reconfigure each button individually in keyboard settings screen
+Slick/SDL: First-time setup screens are no longer present (Default keyboard mappings are Blockbox style)
+Slick/SDL: Mouse support in some menus (Incomplete)
+Slick: Added "NullpoMino.exe" executable file which will start Slick version
+Avalanche/SPF: Added bigger screen option to Avalanche and SPF modes
+New mino skins (Thanks 4matsy!)
#Enhanced Netplay features:
 +Ranked Room and Leaderboard
 +View lobby while in a room
 +"NetAdmin", the administrator tool of NetServer, can ban/kick a player, can delete a record from leaderboard, and can delete any room.
 +Online single player room (WIP. Currently supports LINE RACE and SCORE RACE modes.)
#And maybe more... See svnlog7_4_0.txt for SVN log.

Version 7.3.0 (2010/08/09) {r1-r276; Stable Release}
#This version is NO LONGER compatible with 7.2.0 netplay server.
+Swing/Slick/SDL: When saving replays, a replay folder will be created if one is not found.
+Swing/Slick/SDL: Fixed location of NEXT piece when spawn offset is non-zero.
+Swing/Slick/SDL: Added option to display NEXT pieces on side of playfield.
+Slick: Added PERFECT FPS option (uses more CPU, does not work in menus).
+Slick: LWJGL updated to version 2.5, Slick updated to build 274
+Added COMBO RACE, SQUARE, RETRO MASTERY, AVALANCHE 1P, AVALANCHE 1P FEVER MARATHON, AVALANCHE VS-BATTLE, AVALANCHE VS FEVER MARATHON, AVALANCHE VS DIG RACE, PHYSICIAN, PHYSICIAN VS-BATTLE, and SPF VS-BATTLE modes.
 Modes for other gametypes are either Release Candidate (AVALANCHE, PHYSICIAN) or Beta (SPF).
 Special thanks to Puyo Nexus for supplying Fever chain data.
+Added SQUARE, AVALANCHE, PHYSICIAN, and SPF rules
+Renamed CLASSIC MARATHON to RETRO MARATHON
+Randomizer: Randomizers have been replaced. This breaks all replays prior to this release.
+Sequencer: Added "Set piece enable" dialog.
+AI: Added "Defensive" and "No Prethink" versions of PoochyBot
+Engine: Added support for AVALANCHE, PHYSICIAN, and SPF type games
+Engine: Added rainbow blocks and rainbow gem blocks support
+Engine: Added delay canceling to properly simulate DTET and STANDARD-SUPER3 rules
+Engine: Added DAS charge on blocked move, neutral DAS store and DAS redirect in delays to properly simulate NINTENDO family rules
+Engine: Added Instant DAS and shift lock
+Engine: Added hard block and hard garbage support
+NetPlay/NetServer: Added more room creation options, separated options into tabs
 Garbage: B2B separation, hole change rate, toggle change rate per attack, combo block, garbage countering
 Bonus: Spin check type, all clear bonus
-NetPlay: Now correctly forces 60 FPS in SDL version
-NetPlay: Saves chat logs even when game window is closed or Java process is killed
-NetPlay: You can see opponent's HOLD/NEXT pieces in any-rule 2 players game now.
-NetServer: Fixed "ghost room" bug
-Slick/SDL: Fixed bug where replay screen could crash if replays were deleted.
+Added STANDARD-HOLDNEXT rule
+Added more maps (Thanks Jenn, SecretSalamender and Magnanimous!)
*Added Mac/Linux boot scripts. (Thanks croikle!)
*Renamed Standard-GIZA rule to fit naming convention.
*Restored JRE 1.5 compability
*[Internal change] Main package changed to mu.nu.nullpo
*[Internal change] Documentation is being translated to English.

Version 7.2.0 (2010/06/19)
#This version is NO LONGER compatible with 7.1.* netplay server.
+Slick/SDL: Added new title screen. (Thanks Zircean!)
+Swing/Slick/SDL: Added "Outline Ghost Piece" option. If enabled, ghost piece will be outline-styled instead of darker image of the piece.
+NET-VS MAP EDIT: Added "GRAY->?" tool. When used, it will change all gray blocks on the field to different colors.
+NetPlay/NetServer: It now compresses rule, map, and (if needed) field data. It will reduce a lot of packet size.
-Slick: Restores title-bar texts when you come back to title screen.
 (Previously, name of game mode was still displayed even if you come back to the title screen)
-NetPlay: Fixed tripcode spoof bug. (Thanks Wojtek!)
-NetPlay: Fixed a bug that room list doesn't get cleared after you disconnect. (Thanks hebo-MAI!)
-NetServer: Fixed a CPU 100% causing bug on onAccept()
-GRADE MANIA 2: Leaderboard is no longer displayed when 20G option is used. (Thanks 2ch thread >>779!)
*Updated hebo-MAI's rule, "StandardGIZA". Now it has faster softdrop speed.
*AIs no longer not affect leaderboards. Replay file is still created. (Thanks SWR!)
*[Internal change] All AIs (including PoochyBot) now extends DummyAI class instead of implementing AIPlayer interface.
 So, no changes are required to AIs when AIPlayer adds something.
*[Internal change] PoochyBot/Crypt: Moved to different packages (net.tetrisconcept.poochy.nullpomino.ai and org.cacas.java.gnu.tools, respectively)

Version 7.1.0 (2010/06/15)
#This version is NO LONGER compatible with 7.0.* netplay server.
#I, the NullpoMino author, will use the name "NullNoname !bzEQ7554bc" from this version.
+Added PoochyBot v1.21. Read "PoochyBot Readme.txt" for details. Thanks Poochy!
 (I added some Javadocs to suppress compiler warnings, but the code itself is same)
+Swing/NetPlay: "Watch" feature now works with Swing version.
+NetPlay: Added three new game stats: KO, Wins, and Games.
 KO: Number of players you knocked out in this game
 Wins: Total number of win/1st place (Reset to zero when you exit the room)
 Games: Total number of games you played in this room (Reset to zero when you exit the room)
+NetPlay: Chat log is automatically saved inside the log directory.
+NetServer: Added Tripcode system. A tripcode is a hashed password by which a person can be identified by others.
 To use it, you can enter #tripcode (password followed by a sharp) to your nickname field.
 (Wikipedia article: http://en.wikipedia.org/wiki/Tripcode)
+NetServer: Added an option to display IP/Hostname in various ways. Both plaintext and hashcode are supported.
 Open "config/etc/netserver.cfg" with any text editor to adjust settings of your server.
+Added hebo-MAI's rule, "StandardGIZA". (Thanks hebo-MAI!)
-PHANTOM MANIA: Fixed grade re-award (or re-flash) bug. (Thanks Zircean!)
-GRADE MANIA 3: Fixed GM-awarded-when-it-shouldn't bug. (Thanks Zircean!)
 MM grade with GM performance will be shown as flashing MM on results screen.
-NetPlay: Doesn't stop BGM when one of opponents dies in 3P+ match (Thanks SWR!)
-NetPlay: "Sit out from game" and "Change Team" buttons are enabled as soon as you die.
*NetServer: Disconnect the client when message send fails (workaround for CPU 100% bug)
*NetServer: Cleans up every list (including room list and player list) when everyone disconnects.
 (This is an workaround for ghost room bug)
*NetPlay: Applied Wojtek's new fractional garbage system patch (60 denominator)
*All log files are created inside the log directory.

Version 7.0.2 (2010/06/06)
#This version is still compatible with 7.0.0 netplay server. There is no change to the server.
+NetPlay: Added tool-tips to some of Create Room options.
-NetPlay: Fixed a bug that sometimes your block skin gets overwritten.
-NetPlay: Fixed a bug that your default room settings get overwritten when you click "View Setting" button in an other player's room.
-I forgot to include various libraries in the previous version (ouch!). Included again from this version.

Version 7.0.1 (2010/06/06)
#This version is still compatible with 7.0.0 netplay server. There is no change to the server.
+Added an update checker. When new version is available, it will notify you on the title screen (or mode select screen in case of Swing version).
 Currently settings screen is only available on Swing version.
 By default, it uses Burbruee's XML file on dropbox to check updates, but it can be changed via Swing version's setting screen.
+Added "previews above shadow" option to all three versions. (Thanks Wojtek!)
+Slick: Keyboard settings now displays actual key names.
+NetPlay: Added "View Setting" button to room screen. It lets you to view the settings of your current room without having to leave.
-NetPlay: Disable "Join/Sit out" buttons after clicked until the server makes a response. (Again thanks Wojtek!)
-NetPlay: Disable "Sit out from game" and "Change Team" buttons after the match starts, because they do nothing during the game.
*NetPlay: Better garbage meter for fractional garbage system.
*Updated STANDARD-ZERO rule to most recent version. (Rotation buttons are no longer reversed)
*Support of JRE 1.5 is discontinued because the part of update checker requires JRE1.6+.
*LWJGL 2.4.2 is now included in lib/LWJGL2_4_2 directory, because it seems required for 64bits systems.

Version 7.0.0 (2010/06/04)
#This version is NO LONGER compatible with 6.9.0.* netplay server.
+Added an "all spin" option to all modes that has T-Spin bonus. To enable it, change "SPIN BONUS" setting (renamed from "T-SPIN") to "ALL".
 When enabled, all twists (not only T pieces) are recognized.
+VS-BATTLE/NetPlay: Added map options. When enabled, every match will begin with preset pattern of blocks.
 To create maps, you can use newly added "TOOL-VS MAP EDIT" mode.
+NetPlay: Added "Use fractional garbage system" option. When enabled, all players' attack power will be divided in 3P+ games.
 Garbage will not rise until the amount of garbage exceeds 1.0, but small garbages (0.1 to 0.9) must be removed before you are able to attack.
 You can use it with "Reduce attack power in 3P+ game" option, which will result in much longer game.
+NetPlay: Re-added "Disable auto start timer after someone cancelled" feature as an selectable option.
+NetPlay: Added tabs to "create a room" screen. Speed settings are moved to "Speed Settings" tab.

Version 6.9.0.2 (2010/05/08)
#This version is still compatible with 6.9.0.* netplay servers. However, there are some bugfixes to NetPlay server.
+NetPlay: Added a proper GUI of team change.
+Swing: You can double-click the mode select list box to start the game.
-Slick: 64bit LWJGL libraries were still 2.4.2. Changed back to 2.1.0.
-NetPlay: Fixed a ArrayIndexOutOfBoundsException bug in field updateing routine.
-NetServer: Fixed various bugs of team play. (Includes disconnecting NPE bug on NetServer.java:1129)
-NetServer: Player queue should work fine now.
-DIG MANIA: Big mode removed
-Slick: Fixed "Field BG bright" option's description (128 is max, but description said 255 instead)

Version 6.9.0.1 (2010/05/07)
#This version is still compatible with 6.9.0.0 netplay server. However, there is a small change to NetPlay server.
+STANDARD-PLUS (Created by Blink) and STANDARD-ZERO (Created by Wojtek) rules are now bundled.
+Updated batch files to both 32bit and 64bit compatible ones. (Thanks dodd!)
+NetPlay: You can change your team by using "/team" command.
 To change your team, type "/team <New Team Name>" to chat input box, then push Enter key.
 To change back to no team, type "/team" to chat input box, then push Enter key.
 (Yes, no proper GUI yet. I'll add a proper way from next release.)
*NetPlay: Changed Ping interval to 10 seconds instead of 30 seconds.
 Client will automatically disconnect if there is no response from the server more than 30 seconds.
*NetServer: Removed "Timer Disable" feature from TNET2-style auto start timer because I misunderstood.
 (I'll re-add this feature as an separated option from next release.)
*Slick: Changed LWJGL back to 2.1.0 because it caused problems to some people.

Version 6.9.0.0 (2010/05/06)
#This version is NO LONGER compatible with 6.8 netplay server.
+Added "DIG RACE" mode. Clear the bottommost line as fast as possible.
+GARBAGE MANIA: Now fully compatible with big option. Previously, the garbage hole was too small to clear the line, but now hole is big enough.
+SDL/Slick: Rule Select screen will appear after the first keyboard setup screen.
+NetPlay: Added experimental team play support. If you are on a team, any team mate can't attack you, and you can't attack them.
 When someone dies, and if all survivors are on the same team, game ends and their team name will be displayed in chat log as the winner.
+NetPlay: Added "TNET2-style auto start timer" option. When enabled, the following changes will take place:
 1.Timer won't start until at least 2 players are ready.
 2.When the timer runs out, all non-ready players will be moved to spectator.
 3.If someone cancels the OK sign, timer will be disabled until the next match starts.
-NetPlay: Fixed the following bugs:
 1.when you join game in progress garbage shows on your garbage meter and you can hear danger sound
 2.when you have more than 20 lines in garbage meter big black bar shows up
 3.if all players but one are ready and this one player leave room or go to spectators, game wont start
 4.when you are spectator, room is full and you join queue, then when you get your spot you still have spectator view and you can't go ready
 Thanks dodd for pointing out those bugs.
*Slick: Upgraded LWGJL to 2.4.2.

Version 6.8.0.0 (2010/04/30)
#This version is NO LONGER compatible with 6.7 netplay server.
+Added "Game Tuning" options. Here you can select rotation button behavior, select any block skin, adjust DAS and movement speed.
 To access it, select "GAME TUNING" from CONFIG menu in SDL/Slick versions, or select "1P(2P) Tuning Setting..." from config menu in Swing version.
 Tuning settings are shared from all 3 versions, and you can use it in netplay. (It can overcome "Same rule for all players" option!)
+SDL/Slick: Enhanced joystick options menu. Now you can test your joystick from this "INPUT TEST" option.
 Most joystick related settings were moved from General Options menu.
+NetPlay: Added "Reduce attack power in 3P+ game" option in room creation screen.
 When enabled, it will reduce all player's attack power in 3-6 players game. The attack power will increase when any player dies.
+VS-BATTLE/NetPlay: Added "Hurryup" options.
 When timer reaches specific time limit, you'll hear alarm sound, then undestroyable blocks will start rising from bottom of the playfield.
 By default, undestroyable blocks will rise every time you put 5 pieces, but it can be changed by "Interval" setting.
+NetPlay: Garbage collector will run after each game. (Yep, this is just a workaround, but it's better than nothing)
-NetPlay: Cleans up player list when destroying a room. (Probably reduces memory usage)
-NetServer: Fixed a memory leak bug. (Logged out player's incomplete-packet will remain)
-SDL/Slick: Fixed "SCRREN SHOT" typo.
*GEM MANIA: Decreased Ready&Go duration.

Version 6.7.0.0a (2010/04/12)
#This is a small fix patch. If you want to use this version, you'll need to overwrite this version to previous 6.7.0.0 release.
#It doesn't change anything but netplay server.
-NetServer: Workarounds for tsunami of "broken pipe" IOException in doWrite(SocketChannel).
 1.Changed log level of doWrite(SocketChannel) to debug instead of error, so it will not recorded to the log file when using default settings.
 2.When this exception happens the server will try to disconnect the (probably dead) client.
-I couldn't include latest PHANTOM MANIA source code in previous version, so I included it along with new NetServer source code.

Version 6.7.0.0 (2010/04/10)
#This version is NOT compatible with 6.6 netplay server.
+NetPlay: Now you can customize speed settings (gravity and various delays) and some game settings (T-Spin, B2B, and Combo).
+NetPlay: Now you can see opponent's statistics in the lobby GUI.
+NetPlay: Added auto start timer setting. When half or more players are ready, this timer will start ticking. Set the timer 0 to disable it.
+NetPlay: AIs can be used (but there is a problem that F button doesn't work for single player game and practice game)
*NetServer: Changed default log setting so the total log size will be limited to 50MB.
-PHANTOM MANIA: Fixed a bug that green/orange lines not saved to the ranking.
-PHANTOM MANIA: Fixed a bug that RO medal was shown as SK medal.

Version 6.6.0.1 (2010/04/06)
#This version is minor fix release. NetPlay feature is still compatible with 6.6 netplay server.
+Added section time best records to following modes. Push F button in settings screen to see it.
 GRADE MANIA (all)
 SPEED MANIA (all)
 GARBAGE MANIA
 PHANTOM MANIA
 SCORE ATTACK
 FINAL
+GRADE MANIA 3: Created separated leaderboard for exam-enabled setting. It has some differences from the normal (no-exam) one:
 1.GM grade doesn't appear in this leaderboard unless you are qualified as GM.
 2.Your game doesn't appear in this leaderboard when the game is promotional/demotional exam.
+AI: Added T-Spin AI (WIP)
+NetPlay: You can end single player game instantly by pressing F button.
-NetPlay: Fixed a bug that sometimes when you lose, "Win!/1st Place!" message does not appear in the winner field.
-GRADE MANIA 2/SPEED MANIA: Fixed a bug that RE medal not awarded for ARE-enable rulesets.
-TECHNICIAN: Fixed a bug that game does not end when LV15 is completed with T-Spin Zero.

Version 6.6.0.0 (2010/04/03)
#This version is NOT compatible with 6.5 netplay server.
+SDL/Slick/NetLobby: Added "Watch" (or "Observer") feature. (If you have a better name please tell me :p)
 This feature displays how many players are online. Number of current not-logged-in players and already-logged-in players will be displayed.
 If you are the only online player, the font color is blue.
 If there are some other players but nobody is in NetPlay mode, the font color is green.
 If someone is in NetPlay mode, the font color is red.
 You can enable it in NetLobby GUI, by selecting a server then click "Set to watch" button.
 Click "Unset watch" button to disable the watch feature.
+NetPlay: Added Rule Lock feature (Force all players to use the same rule you are using)
+NetPlay: Added opponent piece preview in 1vs1 game (But only when Rule Lock feature is used)
+NetPlay: Game screen displays amount of your wins (But will be reset in zero when you leave the room)
+NetPlay: Client will automatically disconnect if there is no response from the server more than 5 minutes
+GRADE MANIA 3: Various exam-related debug log will be dumped to "log_sdl.txt", "log_slick.txt", or "log_swing.txt".
-GRADE MANIA 3: Probably fixed a bug that qualified grade does not update in exam when the game is ended by surviving the roll.

Version 6.5.0.1 (2010/04/02)
#This version is minor fix release. NetPlay feature is still compatible with 6.5 netplay server.
+NetLobby: Some minor GUI tweaks.
 Player list boxes have a popup menu, which you can copy the user name (along with some extra things which is annoying...) to the clipboard.
 You can now double click the server name to connect the server.
+NetPlay: Game screen displays the following informations:
 Number of current players (includes you if you are joined as a player)
 Number of spectators (includes you if you are joined as a spectator)
 Number of multiplayer games played (resets when you leave the room)
 Number of all players that currently connected into the server (includes you)
 Number of rooms (includes your current room)
+Swing/NetPlay: Added in-game player name display for Swing version
-Slick: Mysterious "test.png" no longer loaded into the memory (I forgot to remove it...)
*GRADE MANIA 3: Some workaround fixes for exam.
 Exam will happen at the probability of 0.33% (1/3). I know that probability is not accurate, but at least it's better than always-happening.
 The demotion point will be reset in zero when the demotional exam starts, or when the player passes the promotional exam.
 Probably fixed the multi-demotion bug. (The demotion code was executed more than once in the gameover routine)
 Probably fixed the demotional exam fake-pass bug.
*GRADE MANIA 3: Grade now always overcomes the game completed line in leaderboard.

Version 6.5 (2010/03/31)
+Added NetPlay feature (ALPHA!)
+VS-BATTLE: Added "1-ATTACK" garbage pattern option. Less random than NORMAL but more random than ONE RISE. (In other words, it's T0J.)
+VS-BATTLE: Added "SE" option that can disable the sound effects. This may improve the performance of You VS AI games.
-GRADE MANIA 3: Probably fixed that infamous MM-exam bug. Sorry for late fix :(
-RETRO MANIA: Score no longer goes past 999999. Lines no longer goes past 999. Level no longer goes past 99.
-SDL/Slick: Fixed a bug that hitting left from 1P in the Options menu will change it to 0P (Thanks Poochy!)
-Core: Bugs on Field.getHighestPieceY(int) and Field.getValleyDepth(int) are probably fixed (Thanks again Poochy!)
*Slick: Changed Joystick config screen code, so more joysticks may work.
*Replaced native jinput library for Mac OS (lib/libjinput-osx.jnilib) to Snow Leopard compatible version
 (http://ariejan.net/2009/09/01/jinput-mac-os-x-64-bit-natives/)

Version 6.4 (2010/03/02)
+Added MusicListEditor (Allows you to setup BGM files easily)
+Added GEM MANIA mode (Clear all gem blocks from the playfield to complete the stage)
+Added MARATHON+ mode (Features a bonus level where playfield sometimes go invisible)
+Added TIME ATTACK,CLASSIC MARATHON,FINAL modes from NullpoUE build 010210 by Zircean
+TIME ATTACK: Added BGM support
+TIME ATTACK: Added BASIC difficulty
+TIME ATTACK/Engine: Added more accurate HELL-hidden
+GRADE MANIA: Added Pier21 grading system from NullpoUE build 010210 by Zircean
+RETRO MANIA: Added "POWERON" option, which will enable you to use famous Power-on Pattern
+SDL/Slick: Line-clear effects for gem blocks added
-Slick: Line-clear effects for normal blocks is now split to 2 files because some video cards had problems
-Swing: Translucent effect now works correctly

Version 6.3 Alpha (2010/01/02)
*The game now uses log4j for logging. No more black-blank DOS window. However, Swing version and utilities now requires external library.
*Changed background images to NullpoUE's (Old BGs can still be found in res/graphics/oldbg folder)
*Changed block images to NullpoUE's (Old blocks can still be selectable in Rule Editor)
+Added RETRO MANIA, PHANTOM MANIA, SCORE ATTACK modes from NullpoUE build 121909 by Zircean
 (TIME ATTACK,CLASSIC MARATHON,FINAL modes are not available yet)
+Translated NullpoUE modes' source code comments to English
-RETRO MANIA: Levelup behavior is more accurate, especially when level increases by level timer
-SCORE ATTACK: BGM2 now plays correctly when you reach level 300
+SCORE ATTACK: Background image will not change until you clear lines
+SCORE ATTACK: You can speed up ending by pressing F button
*MANIA modes: Applied various fixes and new features from NullpoUE build 121909
+GRADE MANIA 3: You can disable qualified grade and exam features from the menu
+GRADE MANIA 3: Level 500 torikan is disabled during exams
+Added DTET wallkick from from NullpoUE build 010210
+Added all rule sets from NullpoUE build 010210
+PRACTICE: Settings screen of Practice mode is now in fullscreen and bigger than before
+PRACTICE: Can set goal lines when level type is set to none
+PRACTICE: Start with bone blocks option
+PRACTICE: Time limit reset option (Time limit will be reset when the level increase)
+PRACTICE: Field Map options with field editor
 ([Field Editor controls] UP/DOWN/LEFT/RIGHT:Move cursor A:Put a block B:Exit C+LEFT/RIGHT:Select block color D:Delete a block)
+Engine: Gem blocks added (Line-clear effects are not implemented yet)
+Engine: Added "piece skip by D button" function (not used yet)
*Maybe more...

Version 6.2 (2009/10/29)
Re-added Swing version.
Changed block skin text field to a combobox, and added a image preview in Rule Editor.
Upgraded LWJGL version to 2.1.0. It has better 64bit support. Slick version sound in Linux amd64 now works.
Added 200 lines game type to Marathon mode.
Fixed initial hold bug in Mania modes. (Thanks Zircean)
Added secret-grade to Mania modes. (Thanks Zircean)
Added piece type setting to Practice mode. Now you can play with I1, I2, I3, and L3 pieces.
Fixed batch files to log the errors correctly.

Version 6.1 (2009/08/20)
Re-added Mac OS X support.
You can turn off multi-thread AI. (Mainly for debug purpose)
In SPEED MANIA and GARBAGE MANIA modes, you can keep pressing F button to end the ending credits faster.

Version 6 (2009/08/17)
Many things were changed.
Added TECHNICIAN, GARBAGE MANIA, and VS-BATTLE modes.
Added an AI. (not so strong)
Re-added slick version game pad support.
Swing version is gone because it has many problems.

Version 5.5 (2008/12/30)
Added SCORE RACE mode.
Added Sound Effects Volume setting and BGM Volume Setting.
Added combo sound.
BGM fadeout in SDL version now works.

Version 5.4+ (2008/11/29)
Added SDL version
The game engine remain unchanged, so the version display still says 5.4.

Version 5.4 (2008/11/25)
Added GRADE MANIA 3 mode and SPEED MANIA 2 mode.
Added line-clear visual effect. (Currently Slick version only)
Added mode name display in Slick version. (This was present in Swing version already)

Version 5.3 (2008/10/29)
Fixed some DAS bugs.
Fixed a big mode wall kick bug of Classic rule.
Added piece spawn offset options in rule customize screen.
Added replay rerecord function (Pause the game and press D to use it)

Version 5.2 (2008/10/05)
Disable sound effect by default in Slick version. Now this game can work with OpenAL incompatible sound cards.
Added a max FPS option in Slick version.
Added frame step function. You have to enable it in settings screen if you want to use. During pause screen, you can push F button to use it.
Added a goal option in PRACTICE mode.
And minor bugfixes.

Version 5.1 (2008/09/19)
Added BIG option to all modes (pieces will be bigger if you enable this)
Added level type option to PRACTICE mode
Fixed a bug of initial rotation (sometimes this feature didn't work)

Version 5.0 (2008/09/12)
Many things were changed!
Added Swing version
Added rule customize option
Added simple leaderboard
Removed game pad support (because it was so buggy)

Version 4 (2008/08/10)
Changed the detection method of game pad's directional buttons (again).
Added SPEED MANIA mode
Saves various error logs to log.txt

Version 3 (2008/08/05)
Changed the detection method of game pad's directional buttons.
Added GRADE MANIA 2 mode
Added PRACTICE mode
Fixed a bug in replay screen. (Occurs when replay files are 20 or more)
And maybe more. However I can't remember...

Version 2 (2008/07/29)
Added an option of FPS display in game options menu.
Added an option of play level-stop sound in GRADE MANIA mode.
Added mode name display in each mode.
Added more frame colors. Now frame color changes from mode to mode.
Fixed a bug of game pad that doesn't recognize directional buttons.
Fixed a bug in GRADE MANIA mode that level increases even if you use hold.

Version 1.01 (2008/07/29)
This version has no change for binary or source. So the in-game version display still says "VERSION 1".
Fixed the game pad bug (I forgot to include the game pad library. Ouch.)
Added instruction for Linux and Mac users in readme.
Added English version of readme file. (this file)

Version 1 (2008/07/27)
First version.

12. Todo List
More features and fixes for Netplay
 * Fix CPU 100% bug of NetServer
 * Reduce RAM usage
 * More room customize features
   (Garbage pattern options, and more...)
 * Detect disconnected players from server-side
 * Replay support
 * Manage the lag
 * Password protected rooms
 * ID/Password or something like that
Various Bugfixes
Helpful items in SCORE ATTACK mode
MISSION MANIA mode where players must accomplish certain task within the given time limit
HANABI MANIA mode with fireworks
More garbage pattern options for VS-BATTLE (and Netplay)
More AI players
Replace Swing version with something better (jME maybe?)
Better replay selector system
TAS detection (I didn't include it in this version because the current method had problems with music)
Fix the possible high score list inaccurates for MANIA modes (Grade always overcome green-line?)
