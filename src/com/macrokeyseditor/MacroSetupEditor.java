package com.macrokeyseditor;


import java.util.*;

import javax.swing.event.EventListenerList;

import org.eclipse.jdt.annotation.NonNull;

import com.macrokeys.MacroScreen;
import com.macrokeys.MacroSetup;

/**
 * Consente di modificare una {@link MacroSetup}.
 */
public class MacroSetupEditor {
	
	private final List<MacroScreenEditor> screens = new ArrayList<>();
	
	
	private final EventListenerList listeners = new EventListenerList();
	
	/** Schermata attualmente selezionata; null se nessuna */
	private MacroScreenEditor selected;
	
	
	public MacroSetupEditor() {
		
	}
	
	
	/**
	 * @return {@link MacroScreenEditor} attualmente selezionata; null se nessuna
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
	 * Permette di selezionare una particolare {@link MacroScreen}
	 * @param m MacroScreen da selezionare; null per nessuna
	 * @throws IllegalArgumentException Se la {@code m} non è presente
	 */
	public void selectMacroScreen(MacroScreen m) {
		// Controllo che la selezione sia diversa da quella attuale
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
	 * @return Elenco di {@link MacroScreen} contenute; non null
	 */
	public MacroScreen[] getMacroScreens() {
		MacroScreen[] r = new MacroScreen[screens.size()];
		int i = 0;
		for(MacroScreenEditor e : screens) {
			r[i] = e.getMacroScreen();
			i++;
		}
		
		return r;
	}
	
	
	
	/**
	 * @return Numero di {@link MacroScreen} contenute; >= 0
	 */
	public int getMacroScreenCount() {
		return screens.size();
	}
	
	
	/*
	public MacroScreenEditor macroScreenEditorFrom(@NonNull MacroScreen m) {
		
	}*/




	/**
	 * Rimuove, se presente, l'istanza della {@link MacroScreen} indicata.
	 * @param m Istanza da rimuovere
	 */
	public void removeMacroScreen(@NonNull MacroScreen m) {
		ListIterator<MacroScreenEditor> it = screens.listIterator();
		while(it.hasNext()) {
			MacroScreenEditor e = it.next();
			if(e.getMacroScreen() == m) {
				it.remove();
				
				// Se è selezionata allora la deseleziono
				if(e == selected) {
					// Seleziono una schermata altrnativa
					selected = screens.isEmpty() ? null : screens.get(0);
					fireSelectionChange(e, selected);
				}
				
				fireActionListener(MacroSetupEditorListener.Action.Remove, e);
				return;
			}
		}
	}
	
	
	/**
	 * Ricerca la {@link MacroScreenEditor} avente la {@link MacroScreen}
	 * specificata
	 * @param m MacroScreen da trovare
	 * @return {@link MacroScreenEditor} associato a {@code m}
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
