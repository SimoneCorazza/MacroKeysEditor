package com.macrokeyseditor;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.macrokeyseditor.windows.WindowMacroSetupEditor;

/**
 * Punto di ingresso dell'applicazione
 */
public final class Main {
	
	public static void main(String[] args) {
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			// Igoro
		}
		
		WindowMacroSetupEditor f = new WindowMacroSetupEditor();
		f.setVisible(true);
	}

}
