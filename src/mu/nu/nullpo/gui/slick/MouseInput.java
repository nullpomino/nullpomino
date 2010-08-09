package mu.nu.nullpo.gui.slick;

import org.newdawn.slick.Input;

public class MouseInput {
	private int mouseX;
	private int mouseY;
	public static MouseInput mouseInput;
	private boolean mouseClickedA;
	private boolean mouseClickedB;
	private int mousePressedA;
	private int mousePressedB;

	public boolean isMouseClicked() {
		return mouseClickedA;
	}

	public boolean isMouseRightClicked() {
		return mouseClickedB;
	}
	
	public boolean isMousePressed() {
		return (mousePressedA > 0);
	}
	
	public boolean isMouseRightPressed() {
		return (mousePressedB > 0);
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
		if (input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)) {
			mousePressedA++;
		} else {
			mousePressedA = 0;
		}
		if (input.isMouseButtonDown(Input.MOUSE_RIGHT_BUTTON)) {
			mousePressedB++;
		} else {
			mousePressedB = 0;
		}
	}

}
