package cx.it.nullpo.nm8.gui.game;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cx.it.nullpo.nm8.util.CustomProperties;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.AbstractController;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.xml.xpp3.Attributes;

public class GameFieldController extends AbstractController {
	private Log log = LogFactory.getLog(GameFieldController.class);

	protected Element elementMain;
	protected Element elementPanelField;

	protected int blocksize;

	public void bind(Nifty nifty, Screen screen, Element element, Properties parameter, Attributes controlDefinitionAttributes) {
		log.debug("bind");

		elementMain = element;
		if(elementMain == null) log.warn("elementMain == null!");
		elementPanelField = element.findElementByName("panel-field");
		if(elementPanelField == null) log.warn("elementPanelField == null!");

		CustomProperties params = new CustomProperties(parameter);
		blocksize = params.getProperty("blksize", 16);
		log.trace("blocksize:" + blocksize);
	}

	public void onStartScreen() {
		log.debug("onStartScreen");
	}

	public boolean inputEvent(NiftyInputEvent inputEvent) {
		return false;
	}

	public Element getElementMain() {
		return elementMain;
	}

	public Element getElementPanelField() {
		return elementPanelField;
	}

	public int getBlockSize() {
		return blocksize;
	}
}
