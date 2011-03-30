package mu.nu.nullpo.gui.swing;

import java.awt.Graphics;
import java.awt.Image;

import org.apache.log4j.Logger;

import mu.nu.nullpo.gui.common.AbstractColor;
import mu.nu.nullpo.gui.common.AbstractGraphics;
import mu.nu.nullpo.gui.common.AbstractImage;

public class GraphicsSwing implements AbstractGraphics {

static Logger log = Logger.getLogger(GraphicsSwing.class);
	
	Graphics g;
	
	public GraphicsSwing(Graphics g) {
		this.g = g;
	}

	@Override
	public void drawImage(AbstractImage img, int dx1, int dy1, int dx2,
			int dy2, int sx1, int sy1, int sx2, int sy2) {
		
		Image i = getImage(img);
		if (i == null) {
			return;
		}
		
		g.drawImage(i, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);

	}

	@Override
	public void drawImage(AbstractImage img, int x, int y) {
		
		Image i = getImage(img);
		if (i == null) {
			return;
		}
		
		g.drawImage(i, x, y, null);

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

		ColorSwing c;
		try {
			c = (ColorSwing) color;
		} catch (ClassCastException e) {
			log.debug("Failed to set color: wrong type");
			return;
		}
		g.setColor(c.getColor());

	}
	
	private Image getImage(AbstractImage img) {
		
		ImageSwing i;
		try {
			i = (ImageSwing) img;
		} catch (ClassCastException e) {
			log.debug("Failed to draw image: wrong type");
			return null;
		}
		return i.getImage();
	}

}
