# NullpoMino

**NullpoMino** is an open-source action puzzle game that works on the Java platform. It has a wide variety of single-player modes and netplay to allow players to compete over the Internet or LAN.

## Current stable version

The current stable version is 7.5.0, which has the following new features:

* Screen size option for Slick and Swing (320x240, 640x480, 800x600, 1024x768, etc)
* "Dig Challenge" mode where garbage blocks will constantly rise
* More single player modes in NetPlay
* Much more stable NetServer
* New icon
* Installer for Windows (uses Inno Setup)
* App Bundle for macOS

## Download

See the GitHub [Releases](https://github.com/nullpomino/nullpomino/releases) page.

## How to run

This game needs Java Runtime Environment 1.5 or newer version to run. <https://www.java.com/en/>

### Windows

Double-click `play_swing.bat`, `play_slick.bat`, or `NullpoMino.exe`.

* `play_swing.bat` starts Swing version of NullpoMino.
  * Does not use any OS-dependent libraries. However, performance and sound quality are poor. There is no support for joysticks. BGM is also missing.
* `play_slick.bat` or `NullpoMino.exe` starts Slick version of NullpoMino.
  * An OpenGL compatible video card required, some PCs may have problems with keyboard. Has limited support of joysticks.
* `ruleeditor.bat` runs Rule Editor, where you can create your own ruleset.
* `sequencer.bat` runs Sequence Viewer, which should be helpful for TASing. (Created by Zircean)
* `musiclisteditor.bat` runs MusicListEditor, where you can configure which music file to use.
* `netserver.bat` runs NetServer (ALPHA!), which is a server for netplay feature. More details later.
* `netadmin.bat` runs NetAdmin, which can be used to manage your running NetServer.
* `airankstool.bat` runs AI Ranks Tool, which can be used to generate a ranks file used by Ranks AI. (Requires HUGE RAM!)

### Linux

First, open a X-terminal emulator. Next, navigate to the folder where the archive was extracted.
(Use `ls` and `cd` command to navigate the folder)
Finally, enter the following commands:

#### To start Swing version

```sh
chmod +x play_swing
./play_swing
```

#### To start Slick version

```sh
chmod +x play_slick
./play_slick
```

OR

```sh
chmod +x NullpoMino
./NullpoMino
```

#### To start rule editor

```sh
chmod +x ruleeditor
./ruleeditor
```

#### To start Sequence Viewer

```sh
chmod +x sequencer
./sequencer
```

#### To start MusicListEditor

```sh
chmod +x musiclisteditor
./musiclisteditor
```

#### To start NetServer

```sh
chmod +x netserver
./netserver
```

#### To start NetAdmin

```sh
chmod +x netadmin
./netadmin
```

#### To start AI Ranks Tool

```sh
chmod +x airankstool
./airankstool
```

> Note: You don't have to execute chmod command from here on out.

#### Linux Issues

Depending on your video card and Linux version, you might encounter small or big problems.

##### Swing version problems

* It's not working yet.
* The performance is under 3 FPS, and most SFX won't load.

##### Slick version problems

* Turn off 3D desktop (such as Beryl) to run smoother.
* Because of a bug (or limitation) of SCIM and LWJGL, the `play_slick` shell script will disable any IME by default.
* In the `play_slick` shell script, `XMODIFIERS=@im=none` is not needed if your system don't have SCIM.
* If you want to run the game with SCIM enabled, try the following commands (you need access of sudo):

```sh
sudo chmod go+r /dev/input/*
java -cp bin:NullpoMino.jar:lib/log4j-1.2.15.jar:lib/slick.jar:lib/lwjgl.jar:lib/jorbis-0.0.15.jar:lib/jogg-0.0.7.jar:lib/ibxm.jar:lib/jinput.jar -Djava.library.path=lib mu.nu.nullpo.gui.slick.NullpoMinoSlick -j
```

The first command will allow everyone (including the game itself) to read keyboard input directly.
You don't have to execute this command again until you reboot/shutdown your operating system.
The second command will run the game with `-j` option.
Normally, the game will read keyboard input from LWJGL, which conflicts with SCIM.
However, when this option is used, the game will try to read keyboard input directly from your operating system.
So you can play the game with SCIM enabled.
Please note when `-j` option is used, some keys (such as ;) won't be detected.

### macOS

Swing/slick scripts should work with macOS, but Apple plans to remove OpenGL from macOS in the future.

## How to play

A piece made of blocks falls from top of the field.
You can do these actions to the piece until the piece lands on something: move, rotate, or drop.
When the piece lands on something (eg. floor or other blocks), the piece locks, then the new piece will appear from top of the field.
You can erase blocks by filling a horizontal line without gap (In other words, connect 10 blocks to horizontal).
The game ends when the pile of blocks reaches to top of the field.

## Controls

* Description of buttons
  * UP: Hard Drop (Drop current piece instantly) ; Move cursor up
  * DOWN: Soft Drop (Drop current piece faster) ; Move cursor down
  * LEFT: Move left ; Decrease current option's value
  * RIGHT: Move right ; Increase current option's value
  * A: Rotate ; Confirm
  * B: Reverse Rotate ; Cancel
  * C: Rotate
  * D: Hold (Keep a piece to use later)
  * E: 180-Degree Rotate
  * F: Skip ending credits (SPEED MANIA and GARBAGE MANIA modes), enter practice mode in netplay games
  * QUIT: Quit the game
  * PAUSE: Pause the game
  * GIVEUP: Return to the title screen
  * RETRY: Reset the game and restart from beginning
  * FRAME STEP: Frame step (pause screen)
  * SCREEN SHOT: Save screen shot to ss folder

* Default keyboard mappings in menu

| Button Name |  Blockbox (Default) | Guideline  | NullpoMino Classic |
|-------------|------------|------------|------------|
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

* Default keyboard mappings in game

| Button Name |  Blockbox (Default) | Guideline  | NullpoMino Classic |
|-------------|------------|------------|------------|
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

You can change key mappings in CONFIG screen.

If you want to reset settings, delete the following file(s):

* Swing: config\setting\swing.cfg
* Slick: config\setting\slick.cfg
* Global settings: config\setting\global.cfg
* High-scores: config\setting\mode.cfg

## Game rule

Depending on the game rule, the movement of pieces will be different.
You can select which rule to use in the CONFIG>RULE SELECT screen.
You can create your own rule by using Rule Editor.

AVALANCHE        : A rule used to play AVALANCHE type games.

AVALANCHE-CLASSIC: A rule used to play AVALANCHE type games, but is less flexible.

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

NINTENDO-R       : A classical rule that was appeared before or after NINTENDO-L. Best suitable for RETRO MARATHON mode.

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

STANDARD-PLUS    : A rule created by Blink. STANDARD-FAST with no IRS/IHS and faster softdrop.

STANDARD-SUPER3  : A classical rule with the rotation system of STANDARD rules but no wallkicks. Has ARE canceling.

STANDARD-ZERO    : A rule created by Wojtek. STANDARD-PLUS with 20G Soft Drop and Instant DAS.

## Game mode

### MARATHON

Mode for beginner players. The level increases by erasing every 10 lines.

There are three game types: 150 lines, 200 lines, and endless.

### MARATHON+

This mode is similar to MARATHON mode (200 lines game), but has following differences:

* Line clear speed is faster
* If you complete level 20, you'll enter the 21st level: the "Bonus Level".

This level never ends (until you die), but the playfield occasionally goes invisible in this level, so it's better to remember the shape of your playfield.

You can directly start from the bonus level by setting starting level to 21.

### EXTREME

Mode for expert players. Gameplay is similar to marathon mode, but it's much faster.

### LINE RACE

Clear certain number of lines as fast as possible.

Goal is selectable from 20, 40, and 100 lines.

### SCORE RACE

Get certain number of points as fast as possible.

Goal is selectable from 10000, 25000, and 30000 points.

### DIG RACE

Clear all garbage lines as fast as possible.

You win the game when you clear the bottommost line (the line with gem blocks).

Amount of garbage lines is selectable from 5, 10, and 18.

### COMBO RACE

Try to clear all the lines in the well in one combo.

Goal is selectable from 20, 40, 100 lines, or endless mode (stops when you break your combo).

### ULTRA

Score as many points as possible or clear as many lines as possible until the time limit.

Duration is selectable from 1 through 5 minutes.

### TECHNICIAN

The main goal of this mode is to clear each level as fast as you can.

When the "GOAL" counter reaches zero, the level increases.

You can progress the game faster by erasing multiple lines.

There are five different game types.

* LV15-EASY: Reach level 16 as fast as you can. There is 2-minute level timer, but it's just a bonus counter and there is no penalty for running out of time.
* LV15-HARD: This is similar to LV15-EASY, but when the 2-minute level timer runs out, your game ends instantly.
* 10MIN-EASY: See how far you can go and how many points you can get within 10 minutes. When the 2-minute level timer runs out, the goal counter resets.
* 10MIN-HARD: This is similar to 10MIN-EASY, but when the 2-minute level timer runs out, your game ends instantly.
* SPECIAL: When the level increases, 30 seconds is added to your time limit. Survive as long as you can.

### SQUARE

Try placing your pieces in 4x4 squares to get more points.

There are three different game types:

* MARATHON: Keep playing until you top out. Go for the most points!
* SPRINT: Try to get 150 points as fast as you can. Can you do it in 8 lines?
* ULTRA: Get as many points as you can in 3 minutes. Best effect can be achieved if you use "SQUARE" rule.

### DIG CHALLENGE

Try to survive against the tide of rising blocks! Send as many lines as you can.

There are two different game types:

* NORMAL: Rising blocks wait until you place your piece, but can build up.
* REALTIME: Blocks will rise no matter what when the meter runs down.

### RETRO MARATHON

A classic game that takes you to the nostalgic feeling.

Best effect can be achieved if you use "NINTENDO-R" rule.

### RETRO MASTERY

A game based on the classics where efficiency is important.

Best effect can be achieved if you use "NINTENDO-R" rule.

### RETRO MANIA

A classic game that many Japanese players played like a monkey.

Best effect can be achieved if you use "CLASSIC0" rule.

### GRADE MANIA

You can earn "grade" by getting certain amount of score. Aim for highest grade!

### GRADE MANIA 2

If you beat GRADE MANIA mode, try this. It's much harder!

### GRADE MANIA 3

If you don't think GRADE MANIA 2 is so difficult, try this. The speed depends on your game play!

### SCORE ATTACK

Score as many points as possible before you reach level 300.

This mode is designed for beginner players.

### SPEED MANIA

Well, it's not so fast as EXTREME mode, but it's very difficult to go through level 500 barrier.

### SPEED MANIA 2

Insane mode! Can you keep up with this crazy speed and some other dangerous things?

### GARBAGE MANIA

Speed is not fast, but garbage blocks will rise from bottom of the play field, so watch out.

### PHANTOM MANIA

This is mostly same as SPEED MANIA, but the playfield is "completely" invisible!
Try to remember where you placed the piece.

### FINAL

This game mode is designed only for players who have super-fast-fingers and super-fast-brain.

Try to correctly control the insane-fast pieces.

### TIME ATTACK

You have to complete each level within the time limit, or game will end in failure.

However, the time limit is usually long, so you won't get a time over.

The level increases by erasing every 10 lines. Time limit will be reset when the level increases.

This mode features 11 game types.

5 of them (NORMAL, HIGH SPEED 1, HIGH SPEED 2, ANOTHER, ANOTHER2) are easy games that ends at 150 lines.

2 of them (NORMAL 200, ANOTHER 200) are 200 lines game but still easy.

The rest of 4 (BASIC, HELL, HELL-X, VOID) is very difficult.

Each level has extremely short time limit, and in the case of HELL and HELL-X, there will be more dangerous things.

### PRACTICE

You can practice various speed settings in this mode.

### GEM MANIA

This is a puzzle mode that requires different strategy than normal modes.

Your goal is to erase all gem blocks from the playfield.

This mode features two different types of time limit: "Stage Time" and "Limit Time".

Stage Time is time limit for each stage. It starts from 1 minute for each stage, and if it reaches zero, the stage ends in failure.

Limit Time is the main time limit. It starts from 3 minutes, and if it reaches zero, the game ends.

Complete each stage within 20 seconds to increase the Limit Time.

### TOOL-VS MAP EDIT

Not really a "game" mode. This is a tool for creating maps for VS-BATTLE mode and NetPlay mode.

Controls when you editing a map:

* Up/Down/Left/Right: Move cursor
* A: Put a block
* B: Exit
* C+Left/Right: Change color of block
* D: Delete a block

### VS-BATTLE

Battle against human or AI opponent. You can send garbage blocks by clearing 2 or more lines at the same time.

### AVALANCHE 1P (RC1)

This is a puzzle mode where the objective is to clear clusters of colors, not lines.

Make chains to score as much as you can before you top out!

Only for use with "AVALANCHE" rule.

### AVALANCHE 1P FEVER MARATHON (RC1)

Premade chains will drop down endlessly. Try to get the most points before time runs out!

Only for use with "AVALANCHE" rule.

### AVALANCHE VS-BATTLE (RC1)

Clear chains to send your opponent garbage!

Only for use with "AVALANCHE" rule.

### AVALANCHE VS FEVER MARATHON (RC1)

Premade chains will drop down endlessly. Detonate them to neutralize your garbage handicap, then send your opponent garbage!

Only for use with "AVALANCHE" rule.

### AVALANCHE VS DIG RACE (RC1)

Dig down to the flashing gem as fast as you can. The first player to clear it wins!

Only for use with "AVALANCHE" rule.

### PHYSICIAN (RC1)

This is a puzzle mode where you clear gem blocks by lining up blocks of the same color, like throwing pills into a jar.

Clear more viruses at once to get more points.

Only for use with "PHYSICIAN" rule.

### PHYSICIAN VS-BATTLE (RC1)

Clear more than one gem block at a time to send your opponent garbage. Try to clear your gem blocks first!

Only for use with "PHYSICIAN" rule.

### SPF VS-BATTLE (BETA)

A puzzle mode based on a fighting game. Use gems to clear blocks of the same color and send garbage.

Only for use with "SPF" rule.

## How to add BGM

Well, currently this game does not have any BGM by default.
However, if you want, you can add any (but must not too big) music file to the game by using MusicListEditor (`musiclisteditor.bat`).
If you don't want the BGM to loop, enable the checkbox next to the filename.
Supported file formats: .ogg .wav .xm .mod .aif .aiff

## NetPlay (BETA!)

List of what you can do:

* Play against other players (up to 6 players can join)
* Create a multiplayer room
* Create a single player room
* Join an already existing room
* Talk with other players
* Spectate games
* Rated multiplayer with ladder and online leaderboards for single player modes

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

### Playing the game

To enter the netplay mode:

1. Launch the game as usual. Any versions can be used for netplay.
2. For Swing version, click File->Netplay from the menubar. For other versions, select "NETPLAY" from the main menu.
3. The "NetLobby" GUI will appear. You are currently in the server select screen.

To add a new server to the list:

1. In the server select screen, click "Add..." button.
2. You are prompted to enter hostname (or IP address) and the port number (Optional). Server address format is `Hostname:Port`. If the server is using default port 9200, you may omit `:9200` part.
3. Click OK button if you are done.

To test a netplay in the local, launch the `netserver.bat` and add `127.0.0.1` to the server list.

harddrop.com is running a netplay server. Thanks harddrop.com members!

#### To connect to a server

1. Enter your nickname and tripcode (optional)
   If you enter neither a name or a tripcode, then your name will be "noname".
   A tripcode is a hashed password by which a person can be identified by others.
   To use it, you can enter #tripcode (password followed by a sharp) to your nickname field.
   (Wikipedia article: <http://en.wikipedia.org/wiki/Tripcode>)
2. Click a server that you want to connect from the listbox.
   (Alternately, you can just double click the server name from the listbox to connect)
3. Click Connect button. You are currently in the lobby screen.

#### To create a multiplayer room

1. Click "Create Room" button.
2. Enter the room name (Optional) and number of max players.
3. Click OK button if you are done.  You are currently in the room screen.

#### To create a single player room

1. Click "Create 1P Room" button.
2. Select a mode and a rule you want to use.
3. Click OK button if you are done.

#### To enter an available room

Simply double click the room name from the table.

If you want to spectate the game, but don't want to join as a player, right-click the room name and select "Watch" from the context menu.

#### To make "OK" signal to everyone

1. Click the game window (an window that you usually play single player games) to enable the input for game screen.
2. Press A button to make "OK" signal to everyone.
3. The game will start when everyone makes OK signal. Have fun!

### Starting a server

You can run a server by simply double-clicking "netserver.bat" if you are using Windows.
But the port number is fixed to 9200, which is a default one.
If you want to change the port number you can use the following command:

#### For Windows

```cmd
netserver.bat [PORT NUMBER]
```

#### For Linux/MacOS

```sh
./netserver [PORT NUMBER]
    Optionally, there is a second argument to pass in
    the path of the server configuration file.
```
