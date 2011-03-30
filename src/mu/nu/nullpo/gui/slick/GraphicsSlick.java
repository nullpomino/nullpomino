package mu.nu.nullpo.gui.slick;

import mu.nu.nullpo.gui.common.AbstractColor;
import mu.nu.nullpo.gui.common.AbstractGraphics;
import mu.nu.nullpo.gui.common.AbstractImage;

import org.apache.log4j.Logger;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class GraphicsSlick implements AbstractGraphics {
	
	static Logger log = Logger.getLogger(GraphicsSlick.class);
	
	Graphics g;
	
	public GraphicsSlick(Graphics g) {
		this.g = g;
	}

	@Override
	public void drawImage(AbstractImage img, int dx1, int dy1, int dx2,
			int dy2, int sx1, int sy1, int sx2, int sy2) {
		
		Image i = getImage(img);
		if (i == null) {
			return;
		}
		
		if (g != null) {
			g.drawImage(i, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2);
		} else {
			i.draw(dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2);
		}
		

	}

	@Override
	public void drawImage(AbstractImage img, int x, int y) {
		
		Image i = getImage(img);
		if (i == null) {
			return;
		}
		
		if (g != null) {
			g.drawImage(i, x, y);
		} else {
			i.draw(x, y);
		}

	}

	@Override
	public void fillRect(int x, int y, int width, int height) {

		g.fillRect(x, y, width, height);
	}

	@Override
	public void drawLine(int x1, int y1, int x2, int y2) {

		g.drawLine(x1, x2, y1, y2);
	}

	@Override
	public void setColor(AbstractColor color) {

		ColorSlick c;
		try {
			c = (ColorSlick) color;
		} catch (ClassCastException e) {
			log.debug("Failed to set color: wrong type");
			return;
		}
		g.setColor(c.getColor());

	}
	
	private Image getImage(AbstractImage img) {
		ImageSlick i;
		try {
			i = (ImageSlick) img;
		} catch (ClassCastException e) {
			log.debug("Failed to draw image: wrong type");
			return null;
		}
		return i.getImage();
	}

}
