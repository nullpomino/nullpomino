package mu.nu.nullpo.gui.swing;

import java.awt.Image;

import mu.nu.nullpo.gui.common.AbstractImage;

public class ImageSwing implements AbstractImage {
	
	Image img;
	
	public ImageSwing(Image img) {
		this.img = img;
	}
	
	public Image getImage() {
		return img;
	}
}
