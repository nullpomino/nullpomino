@echo off
set path=%path%;%systemroot%\SysWOW64
cd ..\
java -cp bin;NullpoMino.jar;data\lib\log4j-1.2.16.jar;data\lib\jdom.jar;data\lib\slick.jar;data\lib\lwjgl.jar;data\lib\jorbis-0.0.15.jar;data\lib\jogg-0.0.7.jar;data\lib\ibxm.jar;data\lib\jinput.jar -Djava.library.path=data\lib cx.it.nullpo.nm8.gui.slick.NullpoMinoSlick
