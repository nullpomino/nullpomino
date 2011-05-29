package cx.it.nullpo.nm8.neuro.event;

/**
 * TCPSendEvent is an event representing something that should be sent over the network.
 * @author Zircean
 *
 */
public class TCPSendEvent extends NEUROEvent {

	/** The data encapsulated by this TCPSendEvent. */
	private String data;
	
	/**
	 * Constructor for TCPSendEvent.
	 * @param source the source object
	 * @param s the data to be sent
	 */
	public TCPSendEvent(Object source, String s) {
		super(source);
		data = s;
	}
	
	/**
	 * Gets the data held by this TCPSendEvent.
	 */
	public String getData() {
		return data;
	}

}
