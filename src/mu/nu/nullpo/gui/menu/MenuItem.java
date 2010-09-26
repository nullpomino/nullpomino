package mu.nu.nullpo.gui.menu;

public abstract class MenuItem {

	public String name;
	public String description;
	public int color;
	public int state;
	public MenuItem(String name, String description){
		this.name=name;
		this.description=description;
		
	}
	public MenuItem(String name){
		this(name,"");
	}

	/**
	 * Changes the state of the MenuItem.
	 * @param change the amount to change the internal state.
	 */
	public abstract void changeState(int change);

	/**
	 * Gets the current state of the MenuItem.
	 */
	public abstract int getState();
}
