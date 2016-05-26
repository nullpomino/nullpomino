@echo off
set path=%path%;%systemroot%\SysWOW64
start javaw -cp bin;NullpoMino.jar;lib\log4j-1.2.15.jar mu.nu.nullpo.tool.sequencer.Sequencer
