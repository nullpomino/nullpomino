package mu.nu.nullpo.game.net;

/**
 * This will be thrown when a client requests disconnection. Used by NetServer.
 */
public class NetServerDisconnectRequestedException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public NetServerDisconnectRequestedException() {
	}

	public NetServerDisconnectRequestedException(String message) {
		super(message);
	}

	public NetServerDisconnectRequestedException(Throwable cause) {
		super(cause);
	}

	public NetServerDisconnectRequestedException(String message, Throwable cause) {
		super(message, cause);
	}
}
