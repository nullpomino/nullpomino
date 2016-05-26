PoochyBot			ポチボット
10 June 2010			2010年6月10日

PoochyBot.java contains all the source code for PoochyBot. It should go in the
/src/org/game_host/hebo/nullpomino/game/subsystem/ai/ directory like all the
other AIs included with NullpoMino.

Release Notes:
v1.23 (5 July 2010)
* Heuristic changed to ignore "lid" blocks covering holes; turns out that part
  was doing more harm than good.
* Comment out a section of code in setControl that should be impossible to
  reach. (It was put there as a kludge workaround to a bug in between v1.21
  and v1.22, and it's no longer needed.)
* Fix a thread-safety race condition bug which could make it use hold when the
  best position doesn't use hold, which could occur if thinkBestPosition isn't
  finished before the piece spawns.
* Set DEBUG_ALL back to false. Oops. If you ran PoochyBot v1.22, you may want
  to check your log directory for any ridiculously large log files it may have
  generated in there.

v1.22 (2 July 2010)
* Added pre-think in ARE.
* Fixed the bug where it wouldn't consider twists with the hold piece.

v1.21 (10 June 2010) - First public release
* PoochyBot has only been tested on fairly high-end hardware. It may (and
  probably will) cause a bit of lag on other hardware. This is in part because
  it runs the think method for each piece when it spawns. I've tried to
  optimize it to pre-think during ARS and then avoid redundant re-thinking at
  spawn time, but my attempts introduced new bugs and I had to roll back the
  code each time.
* setDAS isn't actually used; it's just an artifact from an incomplete attempt
  at the aforementioned pre-think optimization. There are pieces of code for
  when DAS has already been charged, but it currently never charges DAS, so
  setDAS will always be 0.
* The DEBUG_ALL boolean flag in the source code can be set to true to print out
  VERY detailed debug information to NullpoMino's log file. It's best to keep
  this turned off if you're not trying to debug anything, because it generates
  an enormous amount of log output.
* The DELAY_DROP_ON boolean flag can be set to true to reactivate some code I
  had written to make it wait a couple frames before dropping a piece at low
  gravity. I originally added this just to see if it would make it get the
  200-270 ST COOL!! more consistently. It worked, then I promptly deactivated
  it just because I think it's lame.
* The getColumnDepth method was a kludge workaround for the bug in
  Field.getHighestBlockY(int), which was fixed in NullpoMino v6.5.
  fld.getHighestBlockY(x) should be equivalent to getColumnDepth(fld, x) now.
  Thus, getColumnDepth is depreciated.
* Some code was taken from the BasicAI. In some of these cases, I've left the
  original Japanese comments intact in most cases, though I've also modified
  the surrounding code in quite a few cases. The English comments are mine. I
  also apologize for my poorly commented code.

This work is distributed under the terms of the New BSD License:
http://www.opensource.org/licenses/bsd-license.php

- Poochy.EXE
  I can be reached at Poochy.Spambucket@gmail.com