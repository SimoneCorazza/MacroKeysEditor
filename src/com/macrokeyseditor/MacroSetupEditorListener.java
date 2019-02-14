package com.macrokeyseditor;

import java.util.EventListener;

import org.eclipse.jdt.annotation.NonNull;

import com.macrokeys.MacroScreen;

public interface MacroSetupEditorListener extends EventListener {
	
	/**
	 * Callback per una azione eseguita su una {@link MacroScreen}
	 * @param a Azione eseguita
	 * @param e Editor associato alla {@link MacroScreen}
	 */
	void actionPerformed(Action a, @NonNull MacroScreenEditor e);
	
	/**
	 * Callback per un cambiamento della selezione
	 * @param old Vecchia schermata selezionata; null se nessuna
	 * @param actual Attuale schermata selezionata; null se nessuna
	 */
	void selectionChange(MacroScreenEditor old, MacroScreenEditor actual);
	
	
	
	public enum Action {
		Add,
		Remove
	}
}
