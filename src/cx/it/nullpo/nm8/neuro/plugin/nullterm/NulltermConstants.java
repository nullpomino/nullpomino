package cx.it.nullpo.nm8.neuro.plugin.nullterm;

/**
 * Nullterm constants class. Will be overriden by a properties file soon.
 * @author Zircean
 *
 */
public class NulltermConstants {
	
	/** If true, nullterm will output to the terminal. */
	public static final boolean OUTPUT_TO_TERMINAL = true;
	/** If true, nullterm will not clear the log file before it writes. */
	public static final boolean APPEND_FILE = false;
	/** 
	 * Location of the log file nullterm will write. 
	 * If null, no log will be written. 
	 */
	public static final String LOG_LOCATION = "log/nullterm.txt";
	/**
	 * The line separator for this system.
	 */
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");
}
