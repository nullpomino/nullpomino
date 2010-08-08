package mu.nu.nullpo.gui.slick;

import org.newdawn.slick.Input;

public  class MouseInput {
	private int mouseX;
	public static MouseInput mouseInput;
	private boolean mouseClicked;
	public boolean isMouseClicked() {
		return mouseClicked;
	}
	public static void initializeMouseInput(){
		mouseInput=new MouseInput();
	}
	public int getMouseX() {
		return mouseX;
	}
	public int getMouseY() {
		return mouseY;
	}
	private int mouseY;
	public void update(Input input) {
		mouseX=input.getMouseX();
		mouseY=input.getMouseY();
		mouseClicked=input.isMousePressed(Input.MOUSE_LEFT_BUTTON);
	}
	
}
