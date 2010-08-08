package mu.nu.nullpo.gui.slick;

import org.newdawn.slick.Input;

public class MouseInput {
	private int mouseX;
	private int mouseY;
	public static MouseInput mouseInput;
	private boolean mouseClickedA;
	private boolean mouseClickedB;

	public boolean isMouseClicked() {
		return mouseClickedA;
	}

	public boolean isMouseRightClicked() {
		return mouseClickedB;
	}

	public static void initializeMouseInput() {
		mouseInput = new MouseInput();
	}

	public int getMouseX() {
		return mouseX;
	}

	public int getMouseY() {
		return mouseY;
	}

	public void update(Input input) {
		mouseX = input.getMouseX();
		mouseY = input.getMouseY();
		mouseClickedA = input.isMousePressed(Input.MOUSE_LEFT_BUTTON);
		mouseClickedB = input.isMousePressed(Input.MOUSE_RIGHT_BUTTON);
	}

}
