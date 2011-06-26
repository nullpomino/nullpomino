package cx.it.nullpo.nm8.neuro.error;

/**
 * An exception that is thrown when something goes wrong initializing a plugin.
 * @author Zircean
 *
 */
public class PluginInitializationException extends Exception {
	private static final long serialVersionUID = -5673932089118334171L;

	/**
	 * Constructor for PluginInitializationException.
	 * @param message a detail message
	 * @param cause the Throwable that caused this exception to be thrown.
	 */
	public PluginInitializationException(String message, Throwable cause) {
		super(message, cause);
	}
}
