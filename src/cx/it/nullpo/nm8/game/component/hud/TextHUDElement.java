package cx.it.nullpo.nm8.game.component.hud;

import java.awt.Color;

/**
 * Text HUD element
 */
public class TextHUDElement extends HUDElement {
	private static final long serialVersionUID = -6535677640779127353L;

	/** Label text */
	public Object labelText;

	/** Label color */
	public Color labelColor;

	/** Body text */
	public Object bodyText;

	/** Body color */
	public Color bodyColor;

	/**
	 * Constructor
	 */
	public TextHUDElement() {
		super();
	}

	/**
	 * Copy Constructor
	 * @param e
	 */
	public TextHUDElement(HUDElement e) {
		super(e);
	}

	@Override
	public void copy(HUDElement e) {
		super.copy(e);

		if(e instanceof TextHUDElement) {
			TextHUDElement s = (TextHUDElement)e;
			labelText = s.labelText;
			labelColor = s.labelColor;
			bodyText = s.bodyText;
			bodyColor = s.bodyColor;
		}
	}

	@Override
	public int getNumberOfLines() {
		String strLabelAndBody = "";
		if(labelText != null) strLabelAndBody += labelText.toString();
		if(bodyText != null)  strLabelAndBody += bodyText.toString();

		String[] saLabelAndBody = strLabelAndBody.split("\n");
		return saLabelAndBody.length;
	}
}
