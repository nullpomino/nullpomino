package cx.it.nullpo.nm8.gui.slick.framework;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.lwjgl.input.Controllers;

import cx.it.nullpo.nm8.gui.framework.NFJoystick;
import cx.it.nullpo.nm8.gui.framework.NFJoystickManager;

/**
 * Slick implementation of NFJoystickManager
 * Because Slick doesn't provide enough informations of joysicks, we use LWJGL's API directly.
 */
public class SlickNFJoystckManager implements NFJoystickManager {
	private static final long serialVersionUID = -7656814568448469908L;

	/** Init flag */
	protected boolean inited = false;

	/** Joystick list */
	protected List<NFJoystick> joystickList = Collections.synchronizedList(new ArrayList<NFJoystick>());

	public boolean isInited() {
		return inited;
	}

	public int initJoystick() {
		int ctrlCount = Controllers.getControllerCount();
		for(int i = 0; i < ctrlCount; i++) {
			joystickList.add(new SlickNFJoystick(Controllers.getController(i)));
		}
		inited = true;
		return joystickList.size();
	}

	public int getJoystickCount() {
		return joystickList.size();
	}

	public NFJoystick getJoystick(int id) {
		return joystickList.get(id);
	}

	/**
	 * Poll is automatically done by Slick, so this is always true.
	 * @return Always true
	 */
	public boolean isAutoPoll() {
		return true;
	}

	public void poll() {
		Controllers.poll();
	}
}
