package cx.it.nullpo.nm8.neuro.event;

/**
 * An event used to contain a debug message. Primarily used to see what is going on internally.
 * @author Zircean
 *
 */
public class DebugEvent extends NEUROEvent {
	
	private static final long serialVersionUID = 6578103268756084253L;
	
	/** The message that this DebugEvent contains. */
	String message;
	
	/**
	 * Constructor for DebugEvent.
	 * @param source the source of the event
	 * @param message the message this event contains
	 */
	public DebugEvent(Object source, String message) {
		super(source);
		this.message = message;
	}
	
	/**
	 * Gets the message contained in this DebugEvent.
	 */
	public String getMessage() {
		return message;
	}
}
