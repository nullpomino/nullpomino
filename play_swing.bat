@echo off
set path=%path%;%systemroot%\SysWOW64
start javaw -cp bin;NullpoMino.jar;lib\log4j-1.2.15.jar -Dsun.java2d.translaccel=true -Dsun.java2d.d3dtexbpp=16 mu.nu.nullpo.gui.swing.NullpoMinoSwing
