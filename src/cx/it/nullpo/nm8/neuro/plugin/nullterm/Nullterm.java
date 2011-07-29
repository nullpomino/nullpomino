package cx.it.nullpo.nm8.neuro.plugin.nullterm;

import java.io.FileWriter;
import java.io.IOException;

import cx.it.nullpo.nm8.neuro.error.PluginInitializationException;
import cx.it.nullpo.nm8.neuro.event.DebugEvent;
import cx.it.nullpo.nm8.neuro.plugin.AbstractPlugin;
import cx.it.nullpo.nm8.util.CustomProperties;

/**
 * Nullterm is a basic plugin which currently listens for DebugEvents and prints their detail message out to the terminal.
 * @author Zircean
 *
 */
public class Nullterm extends AbstractPlugin {
	
	/** Writer to write to the log file */
	private FileWriter writer = null;
	
	/** Whether or not nullterm will output to the terminal. */
	private boolean outputToTerminal;
	/** Whether or not nullterm will output to a log file. */
	private boolean outputToFile;
	/** Whether or not nullterm clears the log file when it starts. */
	private boolean appendFile;
	/** Log file location. */
	private String logLocation;
	

	public String getName() {
		return "nullterm";
	}

	public float getVersion() {
		return 0.22F;
	}
	
	public String getAuthor() {
		return "Zircean";
	}
	
	protected void init() throws PluginInitializationException {
		addListener(DebugEvent.class);
		// Read properties file
		CustomProperties props = CustomProperties.load(NulltermConstants.PROPS_LOCATION);
		// Set configuration
		outputToTerminal = props.getProperty("output.terminal",NulltermConstants.OUTPUT_TO_TERMINAL);
		outputToFile = props.getProperty("output.log",NulltermConstants.OUTPUT_TO_FILE);
		appendFile = props.getProperty("output.append",NulltermConstants.APPEND_FILE);
		logLocation = props.getProperty("log.location",NulltermConstants.LOG_LOCATION);
		// Set up log file
		if (outputToFile && logLocation != null) {
			try {
				writer = new FileWriter(logLocation,appendFile);
			} catch (IOException e) {
				dispatchEvent(new DebugEvent(this,DebugEvent.TYPE_WARNING,"Could not open log file for writing"));
			}
		}
	}

	public void stop() {
		try {
			writer.flush();
			writer.close();
		} catch (IOException e) { }
	}
	
	/**
	 * Event listener for DebugEvents. Prints out the content message to the terminal if that option
	 * is set, and writes it to the log file.
	 */
	public synchronized void receiveEvent(DebugEvent e) {
		String message = e.getSource()+" dispatched (systime="+e.getTime()+"): "+e.getMessage();
		// write to the terminal
		if (outputToTerminal) {
			System.out.println(message);
		}
		// write to the log file
		if (outputToFile && writer != null) {
			try {
				writer.write(message+NulltermConstants.LINE_SEPARATOR);
				writer.flush();
			} catch (IOException e1) {
				System.err.println("nullterm failed to write to the log file!");
			}
		}
	}

}
