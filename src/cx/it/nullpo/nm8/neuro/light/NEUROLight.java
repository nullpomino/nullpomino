package cx.it.nullpo.nm8.neuro.light;

import java.io.IOException;

import cx.it.nullpo.nm8.gui.framework.NFFont;
import cx.it.nullpo.nm8.gui.framework.NFGraphics;
import cx.it.nullpo.nm8.gui.framework.NFSystem;
import cx.it.nullpo.nm8.neuro.core.NEURO;
import cx.it.nullpo.nm8.neuro.core.NEUROCore;
import cx.it.nullpo.nm8.neuro.error.PluginInitializationException;
import cx.it.nullpo.nm8.neuro.event.DebugEvent;
import cx.it.nullpo.nm8.neuro.plugin.nullterm.Nullterm;

public class NEUROLight extends NEUROCore {
	private static final long serialVersionUID = -2062491488225964116L;
	NFFont font;

	/**
	 * Constructs a NEUROLight.
	 */
	private NEUROLight(NFSystem sys) {
		super(sys);

		try {
			font = sys.loadFont("data/res/font/font_neuro.ttf",12,false,false);
		} catch (IOException e) {
			font = null;
			dispatchEvent(new DebugEvent(this,DebugEvent.TYPE_WARNING,"Unable to load NEURO font"));
		}
	}
	
	public static NEURO create(NFSystem sys) {
		NEUROLight neuro = new NEUROLight(sys);
		
		try {
			new Nullterm().init(neuro);
		} catch (PluginInitializationException e) {}
		
		return neuro;
	}

	public String getName() {
		return "NEURO Light";
	}

	public float getVersion() {
		return 0.1F;
	}

	@Override
	public void update(long delta) {
		super.update(delta);
		if (font != null) {
			if(font.isGlyphLoadingRequired()) font.loadGlyphs();
		}
	}

	@Override
	public void drawLast(NFGraphics g) {
		if (font != null) {
			g.setFont(font);
			String str = "POWERED BY "+getName().toUpperCase()+" "+getVersion();
			int width = g.getStringWidth(str);
			int x = (sys.getOriginalWidth() - width) / 2;
			g.drawString(str,x,450);
		}
	}

}
