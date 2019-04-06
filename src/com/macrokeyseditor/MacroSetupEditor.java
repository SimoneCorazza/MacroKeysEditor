package com.macrokeyseditor;


import java.util.*;

import javax.swing.event.EventListenerList;

import org.eclipse.jdt.annotation.NonNull;

import com.macrokeys.MacroScreen;
import com.macrokeys.MacroSetup;

/**
 * Allows you to edit a {@link MacroSetup}
 */
public class MacroSetupEditor {
	
	private final List<MacroScreenEditor> screens = new ArrayList<>();
	
	
	private final EventListenerList listeners = new EventListenerList();
	
	/** Screen actually selected; null if none */
	private MacroScreenEditor selected;
	
	
	public MacroSetupEditor() {
		
	}
	
	
	/**
	 * @return {@link MacroScreenEditor} selected; null if none
	 */
	public MacroScreenEditor getMacroScreenEditorSelected() {
		return selected;
	}
	
	
	
	public void addActionListener(MacroSetupEditorListener l) {
		listeners.add(MacroSetupEditorListener.class, l);
	}
	
	
	
	
	public void removeActionListener(MacroSetupEditorListener l) {
		listeners.remove(MacroSetupEditorListener.class, l);
	}
	
	
	
	
	private void fireActionListener(MacroSetupEditorListener.Action a,
			@NonNull MacroScreenEditor e) {
		for(MacroSetupEditorListener l :
			listeners.getListeners(MacroSetupEditorListener.class)) {
			l.actionPerformed(a, e);
		}
	}
	
	
	
	private void fireSelectionChange(MacroScreenEditor old,
			MacroScreenEditor s) {
		for(MacroSetupEditorListener l :
			listeners.getListeners(MacroSetupEditorListener.class)) {
			l.selectionChange(old, s);
		}
	}
	
	
	
	
	public void addMacroScreen(@NonNull MacroScreen m) {
		MacroScreenEditor e = new MacroScreenEditor(m);
		screens.add(e);
		
		fireActionListener(MacroSetupEditorListener.Action.Add, e);
	}
	
	
	/**
	 * Select a {@link MacroScreen}
	 * @param m {@link MacroScreen} to select; null if none
	 * @throws IllegalArgumentException If the {@code m} is not present
	 */
	public void selectMacroScreen(MacroScreen m) {
		// Check that the selection is different from the actual
		if((selected == null && m == null) ||
				(selected != null && selected.getMacroScreen() == m)) {
			return;
		}
		
		if(m == null) {
			this.selected = null;
		} else {
			MacroScreenEditor e = find(m);
			if(e == null) {
				throw new IllegalArgumentException("MacroScreen not found");
			}
			
			MacroScreenEditor old = selected;
			selected = e;
			fireSelectionChange(old, selected);
		}
	}
	
	
	/**
	 * @return {@link MacroScreen}s contained
	 */
	public @NonNull MacroScreen[] getMacroScreens() {
		MacroScreen[] r = new MacroScreen[screens.size()];
		int i = 0;
		for(MacroScreenEditor e : screens) {
			r[i] = e.getMacroScreen();
			i++;
		}
		
		return r;
	}
	
	
	
	/**
	 * @return Number of contained {@link MacroScreen}; always >= 0
	 */
	public int getMacroScreenCount() {
		return screens.size();
	}




	/**
	 * Remo the instance of {@link MacroScreen}, if present
	 * @param m Instance to remove
	 */
	public void removeMacroScreen(@NonNull MacroScreen m) {
		ListIterator<MacroScreenEditor> it = screens.listIterator();
		while(it.hasNext()) {
			MacroScreenEditor e = it.next();
			if(e.getMacroScreen() == m) {
				it.remove();
				
				// Check if the MacroScreen to remove is selected
				if(e == selected) {
					// Select another MacroScreen
					selected = screens.isEmpty() ? null : screens.get(0);
					fireSelectionChange(e, selected);
				}
				
				fireActionListener(MacroSetupEditorListener.Action.Remove, e);
				return;
			}
		}
	}
	
	
	/**
	 * Find the {@link MacroScreenEditor} containing the given {@link MacroScreen}
	 * @param m {link MacroScreen} to find
	 * @return {@link MacroScreenEditor} associated to {@code m}
	 */
	private MacroScreenEditor find(@NonNull MacroScreen m) {
		for (MacroScreenEditor e : screens) {
			if(e.getMacroScreen() == m) {
				return e;
			}
		}
		
		return null;
	}
	
	
	public boolean contains(MacroScreen.SwipeType Orientation) {
		Iterator<MacroScreenEditor> it = screens.iterator();
		while(it.hasNext()) {
			if(it.next().getMacroScreen().getSwipeType().equals(Orientation)) {
				return true;
			}
		}
		
		return false;
	}
}
