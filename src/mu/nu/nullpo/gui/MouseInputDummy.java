package mu.nu.nullpo.gui;

public abstract class MouseInputDummy {
	protected int mouseX;
	protected int mouseY;
	protected int[] mousePressed;

	protected MouseInputDummy() {
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

	public boolean isMenuRepeatLeft() {
		return (mousePressed[0] > 27 && (mousePressed[0] & 3) == 0);
	}

	public boolean isMenuRepeatMiddle() {
		return (mousePressed[1] > 27 && (mousePressed[1] & 3) == 0);
	}

	public boolean isMenuRepeatRight() {
		return (mousePressed[2] > 27 && (mousePressed[2] & 3) == 0);
	}

	public int getMouseX() {
		return mouseX;
	}

	public int getMouseY() {
		return mouseY;
	}
}
