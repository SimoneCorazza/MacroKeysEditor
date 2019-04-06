package com.macrokeyseditor;


import java.util.EventListener;
import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import com.macrokeys.MacroKey;
import com.macrokeys.MacroScreen;

/**
 * Listener for the changes done by {@link MacroScreenEditor}
 */
public interface MacroScreenEditorListener extends EventListener {
	
	/**
	* Called when {@link MacroKey} is removed from the related {@link MacroScreen}
	* @param ms {@link MacroScreen} belonging to the removed keys
	* @param l List of removed keys
	*/
	public void macroKeyRemoved(@NonNull MacroScreen ms,
			@NonNull List<MacroKey> l);
	
	
	/**
	 * Called when one {@link MacroKey} are added to the {@link MacroScreen}
	 * @param ms {@link MacroScreen} belonging to the removed keys
	 * @param l Added keys
	 */
	public void macroKeyAdded(@NonNull MacroScreen ms,
			@NonNull List<MacroKey> l);
	
	/**
	 * Called when the {@link MacroKey} are edited in the related {@link MacroScreen}
	 * @param ms {@link MacroScreen} belonging to the edited keys
	 * @param l Edited keys
	 * @param property Name of the edited field; see {@link MacroScreenEditor}
	 */
	public void macroKeyEdited(@NonNull MacroScreen ms,
			@NonNull List<MacroKey> l, @NonNull String property);
	
	/**
	 * Called when one of the field of the {@link MacroScreen} is edited
	 * @param m {@link MacroScreen} edited
	 */
	public void macroScreenEdited(@NonNull MacroScreen m);
	
	
	/**
	 * Called when the selection of the keys changes
	 * @param actual Set of newly selected keys
	 */
	void selectionChange(List<MacroKey> actual);
	
	/**
	 * Called when two keys are swapped in the list
	 * @param a Key swapped
	 * @param b Other key swapped
	 */
	void swapMacroKeys(@NonNull MacroKey a, @NonNull MacroKey b);
}
