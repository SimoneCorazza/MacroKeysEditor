package com.macrokeyseditor.util;

import java.awt.AWTError;
import java.awt.Toolkit;

import com.macrokeys.screen.*;

/**
 * Class that implements the {@code Screen} interface for a desktop platform
 */
public class DesktopScreen extends Screen {
	Toolkit toolkit;
	
	/**
	 * @throws ScreenException In case of error loading the desktop screen
	 */
	public DesktopScreen() throws ScreenException {
		try {
			toolkit = Toolkit.getDefaultToolkit();
		} catch(AWTError e) {
			throw new ScreenException(e.getMessage(), e);
		}
	}
	
	/**
	 * @return Dpi of the X axis of the screen
	 */
	public float getXDpi() {
		assert toolkit != null;
		return toolkit.getScreenResolution();
	}
	
	/**
	 * @return Dpi of the Y axis of the screen
	 */
	public float getYDpi() {
		assert toolkit != null;
		return toolkit.getScreenResolution();
	}
}
