package cx.it.nullpo.nm8.neuro.gui;

import java.util.Stack;

public class StackUtils {

	public static <E> void remove(Stack<E> stack, E obj) {
		Stack<E> temp = new Stack<E>();
		E curr;
		while (!stack.isEmpty()) {
			curr = stack.pop();
			if (!curr.equals(obj)) {
				temp.push(curr);
			}
		}
		
		while (!temp.isEmpty()) {
			stack.push(temp.pop());
		}
	}
}
