package mu.nu.nullpo.gui.sdl;

import sdljava.SDLException;
import sdljava.event.MouseState;
import sdljava.event.SDLEvent;

public class MouseInputSDL {	
	private int mouseX;
	private int mouseY;
	public static MouseInputSDL mouseInput;
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
			mousePressedA++;
		} else {
			mousePressedA = 0;
		}
		mouseClickedA = (mousePressedA == 1);
		if (ms.getButtonState().buttonRight()) {
			mousePressedB++;
		} else {
			mousePressedB = 0;
		}
		mouseClickedB = (mousePressedB == 1);
		ms = null;
	}
}
