package cx.it.nullpo.nm8.neuro.event;

/**
 * An event used to contain a debug message. Primarily used to see what is going on internally.
 * @author Zircean
 *
 */
public class DebugEvent extends NEUROEvent {
	
	/** Constant representing a debug-type event (i.e. informational). */
	public static final int TYPE_DEBUG = 0;
	/** 
	 * Constant representing a warning-type event (i.e. something may go
	 * wrong or something unexpected happened that wasn't an error). 
	 */
	public static final int TYPE_WARNING = 1;
	/** Constant representing an error-type event. */
	public static final int TYPE_ERROR = 2;
	/** Constant representing a fatal error-type event. This should only 
	 * be used if the stability of the system is compromised by this error.
	 */
	public static final int TYPE_FATAL = 3;
	
	private static final long serialVersionUID = 6578103268756084253L;
	
	/** The type of this DebugEvent. */
	private int type;
	
	/** The message that this DebugEvent contains. */
	private String message;
	
	/**
	 * Constructor for DebugEvent.
	 * @param source the source of the event
	 * @param message the message this event contains
	 */
	public DebugEvent(Object source, int type, String message) {
		super(source);
		this.type = type;
		this.message = message;
	}
	
	/**
	 * Gets the type of this DebugEvent.
	 */
	public int getType() {
		return type;
	}
	
	/**
	 * Gets the message contained in this DebugEvent.
	 */
	public String getMessage() {
		return message;
	}
}
