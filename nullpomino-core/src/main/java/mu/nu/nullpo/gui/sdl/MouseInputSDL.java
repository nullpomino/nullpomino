package mu.nu.nullpo.gui.sdl;

import mu.nu.nullpo.gui.MouseInputDummy;
import sdljava.SDLException;
import sdljava.event.MouseState;
import sdljava.event.SDLEvent;

public class MouseInputSDL extends MouseInputDummy {
	public static MouseInputSDL mouseInput;

	private MouseInputSDL() {
		super();
	}

	public static void initalizeMouseInput() {
		mouseInput = new MouseInputSDL();
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
