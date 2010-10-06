package mu.nu.nullpo.gui.menu;

import java.util.Vector;

public class Menu {
	private String title;
	private String subTitle;
	private int selectedIndex;
	
	private Vector<MenuItem> menuItems;
	public Menu(String title, String subTitle, Vector<MenuItem> menuItems){
		this.title=title;
		this.subTitle=subTitle;
		this.menuItems=menuItems;
		this.selectedIndex=0;
	}
	public Menu(String title,String subTitle){
		this(title,subTitle,new Vector<MenuItem>());
	}
	public Menu(String title, Vector<MenuItem> menuItems){
		this(title,"",menuItems);
	}
	public int getSelectedIndex() {
		return selectedIndex;
	}
	public void setSelectedIndex(int selectedIndex) {
		this.selectedIndex = selectedIndex;
	}
	public void addMenuItem(MenuItem menuItem){
		menuItems.add(menuItem);
		
	}
	public void incIndex(){
		if (selectedIndex<=menuItems.size()-2)
			selectedIndex++;
	}
	public void decIndex(){
		if (selectedIndex>=1)
			selectedIndex--;
	}
	
	
}
