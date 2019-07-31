# FAQ

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