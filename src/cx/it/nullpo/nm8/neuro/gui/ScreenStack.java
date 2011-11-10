package cx.it.nullpo.nm8.neuro.gui;

import java.util.Collection;
import java.util.Stack;

/**
 * The ScreenStack is a manager which pushes new screens on top of the stack. It doesn't allow manual
 * switching, and only allows the user to remove the top screen. It is essentially an implementation of
 * the screen switching done in the Android operating system, minus the home button.
 * @author Zircean
 *
 */
public class ScreenStack implements ScreenManager {
	
	private Stack<ScreenHolder> stack;
	
	public ScreenStack() {
		stack = new Stack<ScreenHolder>();
	}

	@Override
	public void register(ScreenHolder s) {
		if (s.getGUI() != null) {
			stack.push(s);
		}		
	}

	@Override
	public Collection<ScreenHolder> getChangeTable() {
		// ScreenStack does not allow manual changing
		return null;
	}

	@Override
	public void change(ScreenHolder s) {}

	@Override
	public void remove() {
		stack.pop();
	}
	
	@Override
	public void remove(ScreenHolder s) {
		StackUtils.remove(stack,s);
	}
	
	@Override
	public void removeAll() {
		while (!stack.isEmpty()) {
			stack.pop();
		}
	}

	@Override
	public void render() {
		stack.peek().getGUI().render(true);
	}

}
