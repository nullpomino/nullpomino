package mu.nu.nullpo.gui.slick;

import org.newdawn.slick.Image;

import mu.nu.nullpo.gui.common.AbstractImage;

public class ImageSlick implements AbstractImage {

	Image img;
	
	public ImageSlick(Image img) {
		this.img = img;
	}
	
	public Image getImage() {
		return img;
	}
}
