package cx.it.nullpo.nm8.neuro.plugin.nullterm;

import java.io.FileWriter;
import java.io.IOException;

import cx.it.nullpo.nm8.neuro.core.NEURO;
import cx.it.nullpo.nm8.neuro.error.PluginInitializationException;
import cx.it.nullpo.nm8.neuro.event.DebugEvent;
import cx.it.nullpo.nm8.neuro.plugin.AbstractPlugin;

/**
 * Nullterm is a basic plugin which currently listens for DebugEvents and prints their detail message out to the terminal.
 * @author Zircean
 *
 */
public class Nullterm extends AbstractPlugin {
	
	FileWriter writer = null;

	public String getName() {
		return "nullterm";
	}

	public float getVersion() {
		return 0.2F;
	}
	
	public String getAuthor() {
		return "Zircean";
	}
	
	public void init(NEURO parent) throws PluginInitializationException {
		super.init(parent);
		parent.addListener(this,DebugEvent.class);
		// Set up log file
		if (NulltermConstants.LOG_LOCATION != null) {
			try {
				writer = new FileWriter(NulltermConstants.LOG_LOCATION,NulltermConstants.APPEND_FILE);
			} catch (IOException e) {
				neuro.dispatchEvent(new DebugEvent(this,DebugEvent.TYPE_WARNING,"Could not open log file for writing"));
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
		if (NulltermConstants.OUTPUT_TO_TERMINAL) {
			System.out.println(message);
		}
		// write to the log file
		if (writer != null) {
			try {
				writer.write(message+NulltermConstants.LINE_SEPARATOR);
				writer.flush();
			} catch (IOException e1) {
				System.err.println("nullterm failed to write to the log file!");
			}
		}
	}

}
