@echo off
set path=%path%;%systemroot%\SysWOW64
start javaw -Xmx512m -cp bin;NullpoMino.jar;lib\log4j-1.2.15.jar;lib\swing-worker-1.2.jar -Djava.library.path=lib mu.nu.nullpo.tool.airanksgenerator.AIRanksGenerator
