package mu.nu.nullpo.gui.sdl;

import sdljava.SDLException;
import sdljava.event.MouseState;
import sdljava.event.SDLEvent;

public class MouseInputSDL {	
	private int mouseX;
	private int mouseY;
	public static MouseInputSDL mouseInput;
	private int[] mousePressed;
	
	private MouseInputSDL() {
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
	
	public static void initalizeMouseInput() {
		mouseInput = new MouseInputSDL();
	}
	
	public int getMouseX() {
		return mouseX;
	}
	
	public int getMouseY() {
		return mouseY;
	}
	
	public void update() throws SDLException {
		MouseState ms = SDLEvent.getMouseState();
		mouseX = ms.getX();
		mouseY = ms.getY();
		if (ms.getButtonState().buttonLeft()) {
			mousePressed[0]++;
		} else {
			mousePressed[0] = 0;
		}
		if (ms.getButtonState().buttonMiddle()) {
			mousePressed[1]++;
		} else {
			mousePressed[1] = 0;
		}
		if (ms.getButtonState().buttonRight()) {
			mousePressed[2]++;
		} else {
			mousePressed[2] = 0;
		}
		ms = null;
	}
}
