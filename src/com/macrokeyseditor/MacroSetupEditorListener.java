package com.macrokeyseditor;

import java.util.EventListener;

import org.eclipse.jdt.annotation.NonNull;

import com.macrokeys.MacroScreen;

public interface MacroSetupEditorListener extends EventListener {
	
	/**
	 * Callback for an action executed on a{@link MacroScreen}
	 * @param a Action executed
	 * @param e Editor associated at the {@link MacroScreen}
	 */
	void actionPerformed(Action a, @NonNull MacroScreenEditor e);
	
	/**
	 * Callback for a change in the selection
	 * @param old Old selected screen; null if none
	 * @param actual Actual selected screen; null if none
	 */
	void selectionChange(MacroScreenEditor old, MacroScreenEditor actual);
	
	
	
	public enum Action {
		Add,
		Remove
	}
}
