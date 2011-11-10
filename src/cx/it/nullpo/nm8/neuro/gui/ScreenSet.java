package cx.it.nullpo.nm8.neuro.gui;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/**
 * A ScreenSet allows switching between various screens. In this way, you can have multiple
 * plugins running at once, and go between the screens. If you close the current screen, you
 * go back to the last one you were using.
 * @author Zircean
 *
 */
public class ScreenSet implements ScreenManager {
	
	/** The set of registered plugins. */
	Set<ScreenHolder> set;
	
	/** The stack of last-used plugins. */
	Stack<ScreenHolder> stack;
	
	public ScreenSet() {
		set = new HashSet<ScreenHolder>();
		stack = new Stack<ScreenHolder>();
	}

	@Override
	public void register(ScreenHolder s) {
		if (s.getGUI() != null) {
			set.add(s);
			stack.push(s);
		}
	}

	@Override
	public Collection<ScreenHolder> getChangeTable() {
		return new HashSet<ScreenHolder>(set);
	}

	@Override
	public void change(ScreenHolder s) {
		if (set.contains(s)) {
			stack.push(s);
		}
	}

	@Override
	public void remove() {
		stack.pop();
	}

	@Override
	public void remove(ScreenHolder s) {
		set.remove(s);
		StackUtils.remove(stack,s);
	}

	@Override
	public void removeAll() {
		set.removeAll(set);
	}

	@Override
	public void render() {
		stack.peek().getGUI().render(true);
	}

}
