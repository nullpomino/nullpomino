package cx.it.nullpo.nm8.game.component.hud;

/**
 * Blank HUD element; It's just a spacer.
 */
public class BlankHUDElement extends HUDElement {
	private static final long serialVersionUID = 1662787945018072292L;

	/** Number of lines this HUD takes up */
	public int lines = 0;

	@Override
	public void copy(HUDElement e) {
		super.copy(e);

		if(e instanceof BlankHUDElement) {
			BlankHUDElement s = (BlankHUDElement)e;
			lines = s.lines;
		}
	}

	@Override
	public int getNumberOfLines() {
		return lines;
	}
}
