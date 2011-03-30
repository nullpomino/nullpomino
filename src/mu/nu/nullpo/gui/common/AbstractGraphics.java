package mu.nu.nullpo.gui.common;

public interface AbstractGraphics {

	public abstract void drawImage(AbstractImage img,
			int dx1, int dy1, int dx2, int dy2,
            int sx1, int sy1, int sx2, int sy2);
	
	public abstract void drawImage(AbstractImage img, int x, int y);
	
	public abstract void fillRect(int x, int y, int width, int height);
	public abstract void drawLine(int x1, int y1, int x2, int y2);
	public abstract void setColor(AbstractColor color);
	
}
