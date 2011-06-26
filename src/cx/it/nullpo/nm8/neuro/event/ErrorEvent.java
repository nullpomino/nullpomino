package cx.it.nullpo.nm8.neuro.event;

/**
 * An event used to contain an error message.
 * @author Zircean
 *
 */
public class ErrorEvent extends DebugEvent {

	private static final long serialVersionUID = 8682430547895161403L;

	public ErrorEvent(Object source, String message) {
		super(source, message);
	}

}
