package cx.it.nullpo.nm8.neuro.error;

/**
 * An exception that is thrown when something goes wrong initializing a connection to the server.
 * @author Zircean
 *
 */
public class NetworkInitializationException extends Exception {
	private static final long serialVersionUID = -4586257801077680379L;

	/**
	 * Constructor for NetworkInitializationException.
	 * @param message a detail message
	 * @param cause the Throwable that caused this exception to be thrown.
	 */
	public NetworkInitializationException(String message, Throwable cause) {
		super(message, cause);
	}
}
