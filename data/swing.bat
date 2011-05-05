@echo off
set path=%path%;%systemroot%\SysWOW64
cd ..\
java -cp bin;NullpoMino.jar;data\lib\log4j-1.2.15.jar -Djava.library.path=data\lib cx.it.nullpo.nm8.gui.swing.NullpoMinoSwing
