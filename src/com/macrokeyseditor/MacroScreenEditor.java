package com.macrokeyseditor;


import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

import javax.swing.event.EventListenerList;

import org.eclipse.jdt.annotation.NonNull;

import com.macrokeys.MacroKey;
import com.macrokeys.MacroScreen;
import com.macrokeyseditor.fur.Action;
import com.macrokeyseditor.fur.ActionManager;
import com.macrokeyseditor.fur.ModifyAction;


/**
 * Controller (in MVC) che permette la modifica condivisa di una {@link MacroScreen}.
 * <p>
 * Permmette di:
 * <li>Modificare la {@link MacroScreen} memorizzandone le azioni (funzioni undo e redo)
 * <li>Utilizzando {{@link #addEditEventListener(MacroScreenEditListener)} di essere informati su ogni modifica
 * <p>
 * Le modifiche dirette (non tramite i metodi di questa classe) non vengono tracciate.
 */
public class MacroScreenEditor {
	

	/**
	 * Tutte le azioni di modifica ({@link ModifyAction}) eseguite per stesso attributo della stessa classe vengono raggruppate
	 */
	private static final int MAX_DELAY_BETWEEN_MODIFY= 200;
	
	public static final String MACRO_SCREEN_PROPETY_COLOR = "ColorBackground";
	public static final String MACRO_SCREEN_PROPETY_TEXT = "BackgroundText";
	public static final String MACRO_SCREEN_PROPETY_SWIPE_TYPE = "SwipeType";
	public static final String MACRO_SCREEN_PROPETY_ORIENTATION = "Orientation";
	
	public static final String MACRO_KEY_PROPETY_TEXT = "Text";
	public static final String MACRO_KEY_PROPETY_AREA = "Area";
	public static final String MACRO_KEY_PROPETY_SHAPE = "Shape";
	public static final String MACRO_KEY_PROPETY_TYPE = "Type";
	public static final String MACRO_KEY_PROPETY_COLOR_EDGE = "ColorEdge";
	public static final String MACRO_KEY_PROPETY_COLOR_FILL = "ColorFill";
	public static final String MACRO_KEY_PROPETY_COLOR_EDGE_PRESS = "ColorEdgePress";
	public static final String MACRO_KEY_PROPETY_COLOR_FILL_PRESS = "ColorFillPress";
	public static final String MACRO_KEY_PROPETY_KEYSEQ = "KeySeq";
	
	private final EventListenerList editList = new EventListenerList();
	
	/** Azioni effettuate sulla macro screen */
	private final ActionManager actions;
	
	/** Dati sui quali si sta lavorando */
	private final MacroScreen macroScreen;
	
	/** Tasti attualmente selezionati; mai null */
	private final List<MacroKey> selected = new ArrayList<>();
	
	
	/**
	 * @param m Schermata da modificare tramite this
	 */
	public MacroScreenEditor(@NonNull MacroScreen m) {
		this.macroScreen = m;
		
		actions = new ActionManager();
	}
	
	
	/**
	 * @return {@link MacroKey} attualmente selezionati
	 */
	public @NonNull List<MacroKey> getMacroKeySelected() {
		return new ArrayList<>(selected);
	}
	
	
	/**
	 * Aggiunge un listener per l'evento di modifica della {@link MacroScreen} di
	 * gestita da this
	 * @param l Listener da aggiungere
	 */
	public void addEditEventListener(MacroScreenEditorListener l) {
		if(l != null) {
			editList.add(MacroScreenEditorListener.class, l);
		}
	}
	
	
	
	/**
	 * Rimuove un listener per l'evento di modifica della {@link MacroScreen} di
	 * gestita da this
	 * @param l Listener da rimuovere
	 */
	public void removeEditEventListener(MacroScreenEditorListener l) {
		if(l != null) {
			editList.remove(MacroScreenEditorListener.class, l);
		}
	}
	
	
	
	/** @return MacroScreen su cui si opera */
	public MacroScreen getMacroScreen() {
		return macroScreen;
	}
	
	
	private void fireEditMacroKey(@NonNull List<MacroKey> mk,
			@NonNull String property) {
		List<MacroKey> copy = new ArrayList<>(mk);
		for(MacroScreenEditorListener l : 
			editList.getListeners(MacroScreenEditorListener.class)) {
			l.macroKeyEdited(macroScreen, copy, property);
		}
	}
	
	
	
	
	private void fireEditMacroScreen() {
		for(MacroScreenEditorListener l : 
			editList.getListeners(MacroScreenEditorListener.class)) {
			l.macroScreenEdited(macroScreen);
		}
	}
	
	
	
	/**
	 * Genera l'evento di aggiunta di {@link MacroKey}
	 * @param mk Tasti aggiunti; la lista viene copiata
	 */
	private void fireAddMacroKey(@NonNull List<MacroKey> mk) {
		List<MacroKey> copy = new ArrayList<>(mk);
		for(MacroScreenEditorListener l : 
			editList.getListeners(MacroScreenEditorListener.class)) {
			l.macroKeyAdded(macroScreen, copy);
		}
	}
	
	
	
	/**
	 * Genera l'evento di rimozione di {@link MacroKey}
	 * @param mk Tasti rimossi; la lista viene copiata
	 */
	private void fireRemoveAcroKey(@NonNull List<MacroKey> mk) {
		List<MacroKey> copy = new ArrayList<>(mk);
		for(MacroScreenEditorListener l : 
			editList.getListeners(MacroScreenEditorListener.class)) {
			l.macroKeyRemoved(macroScreen, copy);
		}
	}
	
	private void fireSelectionChanged() {
		List<MacroKey> sel = getMacroKeySelected();
		
		for(MacroScreenEditorListener l : 
			editList.getListeners(MacroScreenEditorListener.class)) {
			l.selectionChange(sel);
		}
	}
	
	private void fireSwapMacroKeys(@NonNull MacroKey a, @NonNull MacroKey b) {
		for(MacroScreenEditorListener l : 
			editList.getListeners(MacroScreenEditorListener.class)) {
			l.swapMacroKeys(a, b);
		}
	}
	
	
	/**
	 * Indice occupato dall'elemento indicato
	 * @param k Elemento da trovare
	 * @return Indice occupato dall'elemento nella collezione; -1 se non trovato
	 */
	public int find(@NonNull MacroKey k) {
		Iterator<MacroKey> it = getMacroScreen().getKeys().iterator();
		MacroKey last = null;
		int i = 0;
		while(it.hasNext() && k != (last = it.next())) {
			i++;
		}
		
		return last == k ? i : -1;
	}
	
	
	
	
	/**
	 * Indica se il {@link MacroKey} indicato è presente
	 * @param k Tasto da trovare
	 * @return True se trovato, false altriemnti
	 */
	public boolean contains(@NonNull MacroKey k) {
		return find(k) != -1;
	}
	
	
	
	/**
	 * Rimuove i tasti indicati dalla {@link MacroScreen}
	 * @param l Lista di tasti da rumuovere
	 * @throws NullPointerException Se {@code l} è null
	 * @throws IllegalArgumentException Se {@code l} è vuota o se un tasto
	 * è null o non presente all'interno della {@link MacroScreen}
	 */
	public void remove(@NonNull List<MacroKey> l) {
		// Trovo le posizioni dei tasti da rimuovere
		int[] indices = indicesOf(l);
		
		// Deseleziono i tast da rimuovere
		boolean selectedFlag = false;
		for(MacroKey m : l) {
			if(isSelected(m)) {
				deselect(m, false);
				selectedFlag = true;
			}
		}
		
		// Segnalo che la selezione è cambiata
		if(selectedFlag) {
			fireSelectionChanged();
		}
		
		
		// Compongo l'azione della rimozione e la aggiungo
		RemoveAction r = new RemoveAction(macroScreen);
		for(int j = 0; j < indices.length; j++) {
			int index = indices[j];
			MacroKey k = l.get(j);
			r.remove(index, k);
		}
		actions.add(r);
		
		fireRemoveAcroKey(l);
	}
	
	/**
	 * Ottiene la lista di posizioni occupate in {@link MacroScreen#getKeys()}
	 * dai {@link MacroKey} nella lista {@code l}
	 * @param l Lista di tasti
	 * @return Array di indici corrispondenti alle posizioni dei tasti
	 * @throws NullPointerException Se {@code l} è null
	 * @throws IllegalArgumentException Se {@code l} è vuota o se un tasto
	 * è null o non presente all'interno della {@link MacroScreen}
	 */
	private int[] indicesOf(@NonNull List<MacroKey> l) {
		if(l == null) {
			throw new NullPointerException();
		} else if(l.isEmpty()) {
			throw new IllegalArgumentException("Empty list");
		}
		
		// TODO: eseguire scansione lineare
		int[] indices = new int[l.size()];
		int i = 0;
		for(MacroKey m : l) {
			if(m == null) {
				throw new NullPointerException();
			}
			
			int index = find(m);
			if(index < 0) {
				throw new IllegalArgumentException(
						"MacroKey not in the MacroScreen");
			}
			indices[i] = index;
			i++;
		}
		
		return indices;
	}
	
	
	
	
	/**
	 * Indica se il tasto indicao è attulamente selezionato
	 * @param k Tasto da reficare
	 * @return True il tato {@code k} è selezionato, False altrimenti
	 */
	public boolean isSelected(@NonNull MacroKey k) {
		Iterator<MacroKey> it = selected.iterator();
		while(it.hasNext()) {
			if(k == it.next()) {
				return true;
			}
		}
		
		return false;
	}
	
	
	
	/**
	 * Rimuove dalla selezione il tasto indicato, se presente
	 * @param k Tasto da deselezionare
	 * @return True se il tasto era presente ed è stato de-selezionato, 
	 * False altrimenti
	 */
	public boolean deselect(@NonNull MacroKey k) {
		return deselect(k, true);
	}
	
	
	
	/**
	 * Rimuove dalla selezione il tasto indicato, se presente
	 * @param k Tasto da deselezionare
	 * @param event True se bisogna generare l'evento associato al cambiamento
	 * della selezione, False atriemtni
	 * @return True se il tasto era presente ed è stato de-selezionato, 
	 * False altrimenti
	 */
	private boolean deselect(@NonNull MacroKey k, boolean event) {
		Iterator<MacroKey> it = selected.iterator();
		while(it.hasNext()) {
			if(k == it.next()) {
				it.remove();
				if(event) {
					fireSelectionChanged();					
				}
				return true;
			}
		}
		
		return false;
	}
	
	
	/**
	 * Deseleziona tutti i tasti attualmente selezionati
	 */
	public void deselect() {
		if(!selected.isEmpty()) {
			selected.clear();
			fireSelectionChanged();
		}
	}
	
	
	
	/**
	 * Aggiunge oppure imposta la selezione al solo tasto indicato
	 * @param m Tasto soggetto della selezione
	 * @param only True imposta il tasto {@code m} come l'unico selezionato,
	 * False lo aggiunge alla selezione
	 * @throws IllegalArgumentException Se {@code m} null o non presente
     * in this
	 */
	public void select(@NonNull MacroKey m, boolean only) {
		Objects.requireNonNull(m);
		if(find(m) == -1) {
			throw new IllegalArgumentException("MacroKey not found");
		} else if(isSelected(m)) {
			// Già selezionato
			return;
		}
		
		if(only) {
			selected.clear();
		}
		selected.add(m);
		fireSelectionChanged();
	}
	
	
	
	/**
	 * Seleziona tutti e solo i tasti indicati
	 * @param l Elenco di tasti da selezionare; non null
	 * @param only True se solo i tasti indicati vengono selezionati, False per
	 * aggiungerli alla selezione
	 * @throws NullPointerException Se {@code l} è null o un suo elemento
	 * è null
	 * @throws IllegalArgumentException Se almeno un tasto in {@code l} non è
	 * presente nella {@link MacroScreen} elaborata
	 */
	public void select(List<MacroKey> l, boolean only) {
		Objects.requireNonNull(l);
		// Controllo la presenza dei tasti
		for(MacroKey m : l) {
			Objects.requireNonNull(m);
			if(find(m) == -1) {
				throw new IllegalArgumentException("MacroKey not found");
			}
		}
		
		if(only) {
			selected.clear();			
		}
		
		// Flag per sapere se c'è stata una modifica alla selezione
		boolean mod = false;
		
		for(MacroKey m : l) {
			// Se si aggiungono i tasti alla selezione controllo che non siano
			// già selezionati
			if(only || (!only && isSelected(m))) {
				selected.add(m);
				mod = true;
			}
		}
		// Genero l'evento solo se c'è una modifica
		if(mod) {
			fireSelectionChanged();			
		}
	}
	
	
	/**
	 * Aggiunge i {@link MacroKey} indicati
	 * @param l Elementi da aggiungere; non devono essere presenti
	 * @throws NullPointerException Se {@code l} o un suo elemento è null
	 * @throws IllegalArgumentException Se almeno un tasto in {@code l} è già
	 * presente o se {@code l} è vuota
	 */
	public void add(@NonNull List<MacroKey> l) {
		Objects.requireNonNull(l);
		if(l.isEmpty()) {
			throw new IllegalArgumentException("List is empty");
		}
		
		// Indice degli inserimenti: tutti in fondo alla lista
		int index = macroScreen.getKeys().size();
		InsertAction ins = new InsertAction(macroScreen);
		for(MacroKey m : l) {
			Objects.requireNonNull(l);
			if(find(m) != -1) {
				throw new IllegalArgumentException("Key already present");
			}
			ins.add(index, m);
		}
		actions.add(ins);

		
		fireAddMacroKey(l);
	}
	
	
	/**
	 * Modifica una proprietà della {@link MacroScreen}
	 * @param propName Nome della proprietà, elencate in
	 * {@link MacroScreenEditor}
	 * @param value Nuovo valore
	 */
	public void editMacroScreenPropety(@NonNull String propName,
			Object value) {
		
		switch(propName) {
		case MACRO_SCREEN_PROPETY_COLOR:
			int color = (int)value;
			macroScreen.setBackgroundColor(color);
			break;
			
		case MACRO_SCREEN_PROPETY_ORIENTATION:
			MacroScreen.Orientation orient = (MacroScreen.Orientation)value;
			macroScreen.setOrientation(orient);
			break;
			
		case MACRO_SCREEN_PROPETY_SWIPE_TYPE:
			MacroScreen.SwipeType type = (MacroScreen.SwipeType)value;
			macroScreen.setSwipeType(type);
			break;
			
		case MACRO_SCREEN_PROPETY_TEXT:
			String text = (String)value;
			macroScreen.setBackgroundText(text);
			break;
			
			default: throw new IllegalArgumentException("Property name wrong");
		}
		
		
		fireEditMacroScreen();
	}
	
	
	
	/**
	 * Modifica il valore di una proprietà di un indieme di {@link MacroKey}
	 * @param l Tasti da modificare con i rispettivi nuovi valori
	 * @param propName Nome della proprietà (elencati in
	 * {@link MacroScreenEdit})
	 * @throws IllegalArgumentException Se c'è un errore nella modifica
	 * della proprietà
	 */
	public void editMacroKeyProperty(
			@NonNull List<ModifyAction.Set<MacroKey>> l,
			@NonNull String propName) {
		try {
			actions.add(new CustomModifyAction(propName,
					l,
					MAX_DELAY_BETWEEN_MODIFY));
		} catch (NullPointerException | NoSuchMethodException | SecurityException
				| IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new IllegalArgumentException("Cannot modify property", e);
		}
		
		// Estraggo la lista di tasti attualmente modificati
		List<MacroKey> modKeys = new ArrayList<>();
		for(ModifyAction.Set<MacroKey> s : l) {
			modKeys.add(s.instance);
		}
		
		fireEditMacroKey(modKeys, propName);
	}
	
	
	/**
	 * Modifica il valore di una proprietà di un indieme di {@link MacroKey}
	 * @param l Tasti da modificare
	 * @param propName Nome della proprietà (elencati in
	 * {@link MacroScreenEdit})
	 * @param value Nuovo valore della proprietà
	 * @throws IllegalArgumentException Se c'è un errore nella modifica
	 * della proprietà
	 */
	public void editMacroKeyProperty(@NonNull List<MacroKey> l,
			@NonNull String propName, Object value) {
		// Compongo la lista delle modifiche ripetendo value per ogni elemento
		List<ModifyAction.Set<MacroKey>> set = new ArrayList<>();
		for(MacroKey m : l) {
			set.add(new ModifyAction.Set<MacroKey>(m, value));
		}
		editMacroKeyProperty(set, propName);
	}
	
	
	/**
	 * Sposta il tasto indicato in basso (rispetto all'ordine di rendering dei tasti)
	 * @param m Tasto da spostare
	 * @throws IllegalArgumentException Se il tasto non è presente
	 */
	public void moveMacroKeyDown(@NonNull MacroKey m) {
		int i = find(m);
		if(i == -1) {
			throw new IllegalArgumentException("MacroKey not present");
		}
		
		if(i > 0) {
			int j = i - 1;
			MacroKey a = m;
			MacroKey b = macroScreen.getKeys().get(j);
			swapMacroKeys(i, j);
			fireSwapMacroKeys(a, b);
		}
	}
	
	
	/**
	 * Sposta il tasto indicato in alto (rispetto all'ordine di rendering dei tasti)
	 * @param m Tasto da spostare
	 * @throws IllegalArgumentException Se il tasto non è presente
	 */
	public void moveMacroKeyUp(@NonNull MacroKey m) {
		int i = find(m);
		if(i == -1) {
			throw new IllegalArgumentException("MacroKey not present");
		}
		
		if(i < macroScreen.getKeys().size() - 1) {
			int j = i + 1;
			MacroKey a = m;
			MacroKey b = macroScreen.getKeys().get(j);
			swapMacroKeys(i, j);
			fireSwapMacroKeys(a, b);
		}
	}
	
	
	
	/**
	 * Cambia di posizione due {@link MacroKey}
	 * @param i Posizione del primo tasto da spostare nella lista dei tasti
	 * {@link MacroScreen#getKeys()}
	 * @param j Posizione del secondo tasto da spostare nella lista dei tasti
	 * {@link MacroScreen#getKeys()}
	 * @throws IndexOutOfBoundsException Se gli indici non sono compresi tra
	 * 0 e l.size()
	 */
	private void swapMacroKeys(int i, int j) {
		List<MacroKey> l = macroScreen.getKeys();
		
		if(i >= 0 && i < l.size() && j >= 0 && j < l.size()) {
			actions.add(new SwapAction(macroScreen, i, j));
		} else {
			throw new IndexOutOfBoundsException();
		}
	}
	
	
	/**
	 * Ri-esegue la scorsa modifica annnullata
	 */
	public void redo() {
		Action a = actions.redo();
		
		// Caso nessuna azione da fare il redo
		if(a != null) {
			// Siccome TUTTE le azioni devo implementare l'intrfaccia
			// URGuiUpdater
			URGuiUpdater v = (URGuiUpdater) a;
			v.onRedo();
		}
	}
	
	
	
	/**
	 * Annulla la scorsa modifica
	 */
	public void undo() {
		Action a = actions.undo();
		
		// Caso nessuna azione da fare undo
		if(a != null) {
			// Siccome TUTTE le azioni devo implementare l'intrfaccia
			// URGuiUpdater
			URGuiUpdater v = (URGuiUpdater) a;
			v.onUndo();
		}
	}
	
	
	
	
	
	// ---------------------
	// CLASSI PER LE AZIONI DELL'ACTION MANAGER
	// ---------------------
	
	
	
	/** Azione di inderimento di un {@link MacroKey} */
	private class InsertAction extends Action implements URGuiUpdater {
		
		final MacroScreen ms;
		final List<Pair> addList = new ArrayList<>();
		/** Lista di tasti rimossi, da non modificare */
		final List<MacroKey> keys = new ArrayList<>();
		
		
		/**
		 * @param ms Schermata soggetta all'azione
		 */
		public InsertAction(@NonNull MacroScreen ms) {
			assert ms != null;
			this.ms = ms;
		}
		
		
		/**
		 * Aggiunge un elelemnto facente parte dell'inserimento
		 * @param index Indice dell'inserimento, inerente allo stato ATTUALE
		 * della lista
		 * @param k Tasto da aggiungere
		 */
		public void add(int index, @NonNull MacroKey k) {
			if(index < 0) {
				throw new IndexOutOfBoundsException();
			} else if(k == null) {
				throw new NullPointerException();
			}
			Pair p = new Pair();
			p.index = index;
			p.value = k;
			addList.add(p);
			// Ordino in base alla posizione nella lista (ordine crescente)
			// Per evitare che gli elementi successivi l'elemento rimosso
			// abbiano un indice diverso
			addList.sort((a, b) -> Integer.compare(a.index, b.index));
			keys.add(k);
		}
		
		
		
		
		@Override
		public void undoExecute() {
			List<MacroKey> l = ms.getKeys();
			ListIterator<Pair> it = addList.listIterator(addList.size());
			while(it.hasPrevious()) {
				Pair p = it.previous();
				l.remove(p.index);
			}
		}
		
		@Override
		public boolean tryToMerge(Action followingAction) {
			return false;
		}
		
		@Override
		public void execute() {
			// Aggiungo in ordine crescente => gli indici successivi saranno
			// sfasati di 1 per ogni aggiunta
			ListIterator<Pair> it = addList.listIterator();
			int c = 0;
			while(it.hasNext()) {
				Pair p = it.next();
				ms.getKeys().add(p.index + c, p.value);
				c++;
			}
		}
		
		
		private class Pair {
			int index;
			MacroKey value;
		}


		@Override
		public void onUndo() {
			fireRemoveAcroKey(keys);
		}


		@Override
		public void onRedo() {
			fireAddMacroKey(keys);
		}
	}
	
	/** 
	 * Azione di rimozione di un insieme di {@link MacroKey}
	 */
	private class RemoveAction extends Action implements URGuiUpdater {
		
		final MacroScreen ms;
		final List<Pair> removeList = new ArrayList<>();
		/** Lista di tasti rimossi, da non modificare */
		final List<MacroKey> keys = new ArrayList<>();
		
		/**
		 * @param ms Schermata soggetta all'azione
		 */
		public RemoveAction(@NonNull MacroScreen ms) {
			assert ms != null;
			this.ms = ms;
		}
		
		
		/**
		 * Aggiunge un elemento facente parte della rimozione
		 * @param index Indice della rimozione inerente allo stato ATTUALE
		 * della lista
		 * @param k Elemento rimosso
		 */
		public void remove(int index, MacroKey k) {
			Pair p = new Pair();
			p.index = index;
			p.value = k;
			
			removeList.add(p);
			// Ordino in base alla posizione nella lista (ordine crescente)
			// Per evitare che gli elementi successivi l'elemento rimosso
			// abbiano un indice diverso
			removeList.sort((a, b) -> Integer.compare(a.index, b.index));
			keys.add(k);
		}
		
		
		@Override
		public void undoExecute() {
			for(Pair p : removeList) {
				ms.getKeys().add(p.index, p.value);
			}
		}
		
		@Override
		public boolean tryToMerge(Action followingAction) {
			return false;
		}
		
		@Override
		public void execute() {
			// Elimino in ordine inverso per non alterare gli indici
			ListIterator<Pair> it = removeList.listIterator(removeList.size());
			while(it.hasPrevious()) {
				Pair p = it.previous();
				ms.getKeys().remove(p.index);
			}
		}
		
		
		private class Pair {
			int index;
			MacroKey value;
		}


		@Override
		public void onUndo() {
			fireAddMacroKey(keys);
		}


		@Override
		public void onRedo() {
			fireRemoveAcroKey(keys);
		}
	}
	
	
	/**
	 * Azione di cambiamento di posizione di un tasto
	 * nella relativa lista
	 */
	private class SwapAction extends Action implements URGuiUpdater {

		final MacroScreen m;
		final int i;
		final int j;
		
		/**
		 * @param m MacroScreen su cui si sta lavorando
		 * @param i Posizione in {@link MacroScreen#getKeys()} da scambiare
		 * @param j Posizione in {@link MacroScreen#getKeys()} da scambiare
		 */
		public SwapAction(@NonNull MacroScreen m, int i, int j) {
			this.m = m;
			this.i = i;
			this.j = j;
		}
		
		@Override
		public void undoExecute() {
			swap();
		}

		@Override
		public void execute() {
			swap();
		}

		
		/** Effettua lo scambio */
		private void swap() {
			List<MacroKey> l = m.getKeys();
			MacroKey mi = l.get(i);
			MacroKey mj = l.get(j);
			
			//L'ordine è molto importante
			l.remove(i);
			l.add(i, mj);
			l.remove(j);
			l.add(j, mi);
		}
		
		@Override
		public boolean tryToMerge(Action followingAction) {
			return false;
		}

		@Override
		public void onUndo() {
			MacroKey x = macroScreen.getKeys().get(i);
			MacroKey y = macroScreen.getKeys().get(j);
			fireSwapMacroKeys(x, y);
		}

		@Override
		public void onRedo() {
			MacroKey x = macroScreen.getKeys().get(i);
			MacroKey y = macroScreen.getKeys().get(j);
			fireSwapMacroKeys(x, y);
		}
		
	}
	
	
	/**
	 * Azione che modifica {@link ModifyAction} per implementare {@link Visitor}
	 */
	private class CustomModifyAction
		extends ModifyAction<MacroKey>implements URGuiUpdater {

		public CustomModifyAction(String name, List<Set<MacroKey>> sets,
				int maxElapsedTime)
				throws NoSuchMethodException, IllegalAccessException,
				InvocationTargetException {
			super(name, sets, maxElapsedTime);
		}

		@Override
		public void onUndo() {
			fireEditMacroKey(getObject(), getPropertyName());
		}

		@Override
		public void onRedo() {
			fireEditMacroKey(getObject(), getPropertyName());
		}
	}
	
	
	/**
	 * Interfaccia che unita ad Action consente di poter aggiornare l'UI in
	 * caso l'utente esegua l'undo o il redo di un'azione.
	 * <p>
	 * Deve essere obbligatoriamente implementata da tutte le {@link Action}
	 * utilizzate.
	 * </p>
	 */
	private static interface URGuiUpdater {
		
		/**
		 * Chiamato quando l'utente esegue l'undo sull'azione
		 */
		public void onUndo();
		
		
		/**
		 * Chiamato quando l'utente ri-applica l'azione
		 */
		public void onRedo();
	}
}
