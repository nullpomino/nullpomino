package mu.nu.nullpo.gui.slick;

import org.newdawn.slick.Input;

public class MouseInput {
	private int mouseX;
	private int mouseY;
	public static MouseInput mouseInput;
	private int[] mousePressed;
	
	private MouseInput() {
		mousePressed = new int[3];
	}

	public boolean isMouseClicked() {
		return (mousePressed[0] == 1);
	}
	
	public boolean isMouseMiddleClicked() {
		return (mousePressed[1] == 1);
	}
	
	public boolean isMouseRightClicked() {
		return (mousePressed[2] == 1);
	}
	
	public boolean isMousePressed() {
		return (mousePressed[0] > 0);
	}
	
	public boolean isMouseMiddlePressed() {
		return (mousePressed[1] > 0);
	}
	
	public boolean isMouseRightPressed() {
		return (mousePressed[2] > 0);
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
		if (input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)) {
			mousePressed[0]++;
		} else {
			mousePressed[0] = 0;
		}
		if (input.isMouseButtonDown(Input.MOUSE_MIDDLE_BUTTON)) {
			mousePressed[1]++;
		} else {
			mousePressed[1] = 0;
		}
		if (input.isMouseButtonDown(Input.MOUSE_RIGHT_BUTTON)) {
			mousePressed[2]++;
		} else {
			mousePressed[2] = 0;
		}
	}

}
