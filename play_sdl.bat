@echo off
set PATH=lib;%PATH%;%systemroot%\SysWOW64
start javaw -cp bin;NullpoMino.jar;lib\log4j-1.2.15.jar;lib\sdljava.jar -Djava.library.path=lib mu.nu.nullpo.gui.sdl.NullpoMinoSDL
