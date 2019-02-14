package com.macrokeyseditor.util;

import java.awt.AWTError;
import java.awt.Toolkit;

import com.macrokeys.screen.*;

/** */
public class DesktopScreen extends Screen {
	Toolkit toolkit;
	
	/**
	 * Nuovo schermo principale
	 */
	public DesktopScreen() throws ScreenException {
		try {
			toolkit = Toolkit.getDefaultToolkit();
		} catch(AWTError e) {
			throw new ScreenException(e.getMessage(), e);
		}
	}
	
	/**
	 * @return Dpi sull'asse X dello schermo
	 */
	public float getXDpi() {
		assert toolkit != null;
		return toolkit.getScreenResolution();
	}
	
	/**
	 * @return Dpi sull'asse Y dello schermo
	 */
	public float getYDpi() {
		assert toolkit != null;
		return toolkit.getScreenResolution();
	}
}
