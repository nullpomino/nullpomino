package cx.it.nullpo.nm8.util;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * This Log Handler will redirect all JDK Logger messages to Apache Log4j.
 */
public class JDK2Log4jHandler extends Handler {
	public JDK2Log4jHandler() {
	}

	@Override
	public void publish(LogRecord record) {
		String loggerName = record.getLoggerName();
		if(loggerName == null) loggerName = "(null logger)";

		Logger log = Logger.getLogger(loggerName);
		String msg = record.getMessage();
		Throwable t = record.getThrown();
		Level lv = record.getLevel();

		if(lv == Level.SEVERE) {
			log.fatal(msg, t);
		} else if(lv == Level.WARNING) {
			log.warn(msg, t);
		} else if(lv == Level.INFO || lv == Level.CONFIG) {
			log.info(msg, t);
		} else if(lv == Level.FINE || lv == Level.FINER) {
			log.debug(msg, t);
		} else if(lv == Level.FINEST) {
			log.trace(msg, t);
		}
	}

	@Override
	public void flush() {
	}

	@Override
	public void close() throws SecurityException {
		LogManager.shutdown();
	}
}
