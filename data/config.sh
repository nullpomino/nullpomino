#!/bin/sh
cd `dirname $0`
cd ../
java -cp bin:NullpoMino.jar:data/lib/log4j-1.2.16.jar:data/lib/cookxml-3.0.2.jar:data/lib/cookswing-1.5.1.jar:data/lib/commons-logging-1.1.1.jar -Djava.library.path=data/lib -Dlog4j.configuration=file:data/properties/log.cfg -Djava.util.logging.config.file=data/properties/jdklogger.cfg cx.it.nullpo.nm8.tool.configtool.ConfigTool
