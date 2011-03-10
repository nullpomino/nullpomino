@echo off
set path=%path%;%systemroot%\SysWOW64
java -cp ..\bin;NullpoMino.jar;lib\log4j-1.2.15.jar;lib\slick.jar;lib\lwjgl.jar;lib\jorbis-0.0.15.jar;lib\jogg-0.0.7.jar;lib\ibxm.jar;lib\jinput.jar -Djava.library.path=lib cx.it.nullpo.nm8.gui.slick.NullpoMinoSlick
