NullpoMino RanksAI
Version 7.3.0


1. What is this ?

Ranks AI is an AI based on an idea by Ryan Heise. You can read his interesting article on the subject on his website :
http://www.ryanheise.com/tetris/tetris_artificial_intelligence.html
Basically its the implementation for Nullpomino of what is described on this article.
The results displayed on his page seem better than the ones we get, but I haven't been able to contact him to get his input on the subject.

The goal of this AI is to stack for 4-Lines, and only stack for 4-Lines. It will stop playing as soon as it cannot prevent making a hole.

2. How to use it?

To be able to use ranks AI at its full potential, you will have to generate the huge ranks tables.
To do this, you have to run the AIRanksGenerator, and run some iterations (15 iterations is advised). 
It will probably take several hours, depending on your computer's speed.
You can also set the ranks table and the number of piece previews to use for the ranks AI in this program.
Just pick the ranks table you like in "input file", set the number of previews you like, and hit the "Set Defaults" button.
The use of 3 previews or less is advised if you don't want the AI to take ages to decide where it will put the piece.

Additionally, you can view the 100 best or worst surfaces of a ranks table from this program.

Finally, to use ranksAI, or any other AI in Nullpomino, start Nullpomino, pick the config menu, then AI setting and change AI type to RanksAI



