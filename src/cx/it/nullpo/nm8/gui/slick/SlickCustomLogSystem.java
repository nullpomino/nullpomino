package cx.it.nullpo.nm8.gui.slick;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.newdawn.slick.util.LogSystem;

/**
 * Log system bridge for Slick
 */
public class SlickCustomLogSystem implements LogSystem {
	private Log log = LogFactory.getLog("org.newdawn.slick.Slick");

	public void error(String message, Throwable e) {
		log.error(message, e);
	}

	public void error(Throwable e) {
		log.error("", e);
	}

	public void error(String message) {
		log.error(message);
	}

	public void warn(String message) {
		log.warn(message);
	}

	public void warn(String message, Throwable e) {
		log.warn(message, e);
	}

	public void info(String message) {
		log.info(message);
	}

	public void debug(String message) {
		log.debug(message);
	}
}
