package com.macrokeyseditor;


import java.util.EventListener;
import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import com.macrokeys.MacroKey;
import com.macrokeys.MacroScreen;

/**
 * Interfaccia per il listener di modifiche da parte di
 * {@link MacroScreenEditor}
 */
public interface MacroScreenEditorListener extends EventListener {
	
	/**
	 * Chiamato quando dei {@link MacroKey} vengono rimossi dalla relativa
	 * {@link MacroScreen}
	 * @param ms MacroScreen di appartenenza dei tasti rimossi
	 * @param l Lista di tasti rimossi
	 */
	public void macroKeyRemoved(@NonNull MacroScreen ms,
			@NonNull List<MacroKey> l);
	
	
	/**
	 * Chiamato quando dei {@link MacroKey} viengono aggiunti nella
	 * {@link MacroScreen}
	 * @param ms MacroScreen di appartenenza dei tasti aggiunti
	 * @param l Tasti aggiunti
	 */
	public void macroKeyAdded(@NonNull MacroScreen ms,
			@NonNull List<MacroKey> l);
	
	/**
	 * Chiamato quando dei {@link MacroKey} vengono modificati nella relativa
	 * {@link MacroScreen}
	 * @param ms MacroScreen di appartenenza dei tasti modificati
	 * @param l Tasti modificati
	 * @param property Nome della proprietà; vedi {@link MacroScreenEditor}
	 */
	public void macroKeyEdited(@NonNull MacroScreen ms,
			@NonNull List<MacroKey> l, @NonNull String property);
	
	/**
	 * Chiamato quando una proprietà della {@link MacroScreen} viene modificata
	 * @param m MacroScreen modificata
	 */
	public void macroScreenEdited(@NonNull MacroScreen m);
	
	
	/**
	 * Callback per un cambiamento della selezione
	 * @param actual Attuale elenco di tasti selezionati
	 */
	void selectionChange(List<MacroKey> actual);
	
	/**
	 * Chiamato quando due tasti vengono cambiati di posizione nella lista
	 * @param a Primo tasto cambiato di posizione
	 * @param b Secondo tasto cambiato di posizione
	 */
	void swapMacroKeys(@NonNull MacroKey a, @NonNull MacroKey b);
}
