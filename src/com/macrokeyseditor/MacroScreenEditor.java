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
 * Controller that use an MVC approuch (in MVCthat permists a shared edit to a {@link MacroScreen}.
 * <p>
 * Can:
 * <li>Edit the {@link MacroScreen} storing the crology of the action done (undo e redo)
 * <li>Subscribing to {{@link #addEditEventListener(MacroScreenEditListener)} you can be notified on every change done
 * <p>
 * Direct changes are not tracked by this class.
 */
public class MacroScreenEditor {
	

	/**
	 * If a set of action is done in this amount of time is grouped into one action ({@link ModifyAction})
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
	
	/** Action done to the MacroScreen */
	private final ActionManager actions;
	
	/** MacroScreen this is working on */
	private final MacroScreen macroScreen;
	
	/** Keys actually selected; never null */
	private final List<MacroKey> selected = new ArrayList<>();
	
	
	/**
	 * @param m Screen to edit
	 */
	public MacroScreenEditor(@NonNull MacroScreen m) {
		this.macroScreen = m;
		
		actions = new ActionManager();
	}
	
	
	/**
	 * @return {@link MacroKey} actually selected
	 */
	public @NonNull List<MacroKey> getMacroKeySelected() {
		return new ArrayList<>(selected);
	}
	
	
	/**
	 * Adds a listener for an edit event event of {@link MacroScreen}
	 * @param l Listener to add
	 */
	public void addEditEventListener(MacroScreenEditorListener l) {
		if(l != null) {
			editList.add(MacroScreenEditorListener.class, l);
		}
	}
	
	
	
	/**
	 * Remove a listener for an edit event event of {@link MacroScreen}
	 * @param l Listener to remove
	 */
	public void removeEditEventListener(MacroScreenEditorListener l) {
		if(l != null) {
			editList.remove(MacroScreenEditorListener.class, l);
		}
	}
	
	
	
	/** @return MacroScreen that this edit */
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
	 * Fire the event of add of a {@link MacroKey}
	 * @param mk Added keys; this list is cloned
	 */
	private void fireAddMacroKey(@NonNull List<MacroKey> mk) {
		List<MacroKey> copy = new ArrayList<>(mk);
		for(MacroScreenEditorListener l : 
			editList.getListeners(MacroScreenEditorListener.class)) {
			l.macroKeyAdded(macroScreen, copy);
		}
	}
	
	
	
	/**
	 * Fire an event of remove of a {@link MacroKey}
	 * @param mk Removed keys; this list is cloned
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
	 * Index of the given element
	 * @param k Eleemnt to find
	 * @return Idex of the given element; -1 if not found
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
	 * Indicates if the given {@link MacroKey} is present
	 * @param k Key to find
	 * @return True if found, false otherwise
	 */
	public boolean contains(@NonNull MacroKey k) {
		return find(k) != -1;
	}
	
	
	
	/**
	 * Remove the given keys form the {@link MacroScreen}
	 * @param l Keys to remove
	 * @throws IllegalArgumentException If {@code l} is empty or if an item is null or
	 * is not present in the edited {@link MacroScreen}
	 */
	public void remove(@NonNull List<MacroKey> l) {
		// Find the position of the keys to remove
		int[] indices = indicesOf(l);
		
		// Deselect the keys to remove
		boolean selectedFlag = false;
		for(MacroKey m : l) {
			if(isSelected(m)) {
				deselect(m, false);
				selectedFlag = true;
			}
		}
		
		// Fire the event of selection change
		if(selectedFlag) {
			fireSelectionChanged();
		}
		
		
		// Compose the action for the remove action and add it to the action list
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
	 * Gests the position occupied in the {@link MacroScreen#getKeys()}
	 * of the {@link MacroKey} in the list {@code l}
	 * @param l Keys list
	 * @return Array for the position of each MacroKey in the given list
	 * @throws IllegalArgumentException If {@code l} is empty or if an item is null or
	 * is not present in the edited {@link MacroScreen}
	 */
	private int[] indicesOf(@NonNull List<MacroKey> l) {
		if(l == null) {
			throw new NullPointerException();
		} else if(l.isEmpty()) {
			throw new IllegalArgumentException("Empty list");
		}
		
		// TODO: execute linear scan
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
	 * @param k Key to check
	 * @return True if the key {@code k} is selected, False otherwise
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
	 * Deselect the given key, if present
	 * @param k Key to deselect
	 * @return True if the key was present and was deselected, 
	 * False otherwise
	 */
	public boolean deselect(@NonNull MacroKey k) {
		return deselect(k, true);
	}
	
	
	
	/**
	 * Deselect the given key, if present
	 * @param k Key to deselect
	 * @param event True if fire the selection changed event false otherwise
	 * @return True if the key was present and was deselected, 
	 * False otherwise
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
	 * Deselect all selected keys
	 */
	public void deselect() {
		if(!selected.isEmpty()) {
			selected.clear();
			fireSelectionChanged();
		}
	}
	
	
	
	/**
	 * Adds or sets the selection of the given key
	 * @param m Key to select
	 * @param only True sets the key {@code m} as the only selected key,
	 * False to add it to the selection
	 * @throws IllegalArgumentException Se {@code m} null o non presente
     * in this
	 */
	public void select(@NonNull MacroKey m, boolean only) {
		Objects.requireNonNull(m);
		if(find(m) == -1) {
			throw new IllegalArgumentException("MacroKey not found");
		} else if(isSelected(m)) {
			// Already selected
			return;
		}
		
		if(only) {
			selected.clear();
		}
		selected.add(m);
		fireSelectionChanged();
	}
	
	
	
	/**
	 * Select the given keys
	 * @param l Set of key to select
	 * @param only True to select only the given keys, False to add them to the selection
	 * @throws NullPointerException if {@code l} is null or an inner item is null
	 * @throws IllegalArgumentException If at least one key in {@code l} is not present
	 * in the edited {@link MacroScreen}
	 */
	public void select(@NonNull List<MacroKey> l, boolean only) {
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
		
		boolean mod = false;
		
		for(MacroKey m : l) {
			// Check if the added keys are not already selected
			if(only || (!only && isSelected(m))) {
				selected.add(m);
				mod = true;
			}
		}
		
		if(mod) {
			fireSelectionChanged();			
		}
	}
	
	
	/**
	 * Adds the given {@link MacroKey}
	 * @param l Keys to add
	 * @throws NullPointerException If {@code l} is null or one of its element is null
	 * @throws IllegalArgumentException If at least one item in {@code l} is laready
	 * present ot if {@code l} is empty
	 */
	public void add(@NonNull List<MacroKey> l) {
		Objects.requireNonNull(l);
		if(l.isEmpty()) {
			throw new IllegalArgumentException("List is empty");
		}
		
		// Index for the insert
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
	 * Edits a proprety of the {@link MacroScreen}
	 * @param propName Name of the property, listed in {@link MacroScreenEditor}
	 * @param value New value
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
	 * Edit the value of a property of a set of {@link MacroKey}
	 * @param l Keys edited
	 * @param propName Name of the property
	 * @throws IllegalArgumentException If there is an error in the edit of the property
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
	 * Edit the value of a property of a set of {@link MacroKey}
	 * @param l Keys edited
	 * @param propName Name of the property
	 * @param value New value for the property
	 * @throws IllegalArgumentException If there is an error in the edit of the property
	 */
	public void editMacroKeyProperty(@NonNull List<MacroKey> l,
			@NonNull String propName, Object value) {
		List<ModifyAction.Set<MacroKey>> set = new ArrayList<>();
		for(MacroKey m : l) {
			set.add(new ModifyAction.Set<MacroKey>(m, value));
		}
		editMacroKeyProperty(set, propName);
	}
	
	
	/**
	 * Move the key below (in the rendering order)
	 * @param m Key to move
	 * @throws IllegalArgumentException If the key is not present
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
	 * Move the key up (in the rendering order)
	 * @param m Key to move
	 * @throws IllegalArgumentException If the key is not present
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
	 * Change the position of two {@link MacroKey}
	 * @param i Index of the first key in the list {@link MacroScreen#getKeys()}
	 * @param j Index of the second key in the list {@link MacroScreen#getKeys()}
	 * @throws IndexOutOfBoundsException If indexes are not in the range 0 e l.size()
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
	 * Re-execute the last edit that was undone
	 */
	public void redo() {
		Action a = actions.redo();
		
		if(a != null) {
			// All action must implement the interface GuiUpdater
			URGuiUpdater v = (URGuiUpdater) a;
			v.onRedo();
		}
	}
	
	
	
	/**
	 * Undo the last edit
	 */
	public void undo() {
		Action a = actions.undo();
		
		if(a != null) {
			// All action must implement the interface GuiUpdater
			URGuiUpdater v = (URGuiUpdater) a;
			v.onUndo();
		}
	}
	
	
	
	
	
	// ---------------------
	// CLASSES FOR THE ACTION OF THE ACTION MANAGER
	// ---------------------
	
	
	
	/** Action of add of a {@link MacroKey} */
	private class InsertAction extends Action implements URGuiUpdater {
		
		final MacroScreen ms;
		final List<Pair> addList = new ArrayList<>();
		
		/** List of inserted keys */
		final List<MacroKey> keys = new ArrayList<>();
		
		
		/**
		 * @param ms MacroScreen subject ot the edit
		 */
		public InsertAction(@NonNull MacroScreen ms) {
			assert ms != null;
			this.ms = ms;
		}
		
		
		/**
		 * Adds an item for the insert
		 * @param index Index of the insert; must be for the CURRENT state of the list
		 * @param k Key to add
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
			
			// Sorting based on the position in the list (increasing order)
			// To avoid that the next elements of the removed items have a different index
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
			// Add in an increasing order => shift the indexes of 1 for each added item
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
	 * Remove action for a {@link MacroKey}
	 */
	private class RemoveAction extends Action implements URGuiUpdater {
		
		final MacroScreen ms;
		final List<Pair> removeList = new ArrayList<>();
		
		/** List of removed keys */
		final List<MacroKey> keys = new ArrayList<>();
		
		/**
		 * @param ms MacroScreen subject ot the edit
		 */
		public RemoveAction(@NonNull MacroScreen ms) {
			assert ms != null;
			this.ms = ms;
		}
		
		
		/**
		 * Adds a removed key
		 * @param index Index of the insert; must be for the CURRENT state of the list
		 * @param k Item to remove
		 */
		public void remove(int index, MacroKey k) {
			Pair p = new Pair();
			p.index = index;
			p.value = k;
			
			removeList.add(p);
			
			// Sorting based on the position in the list (increasing order)
			// To avoid that the next elements of the removed items have a different index
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
			// Delete the items in inverse order to not alterate the index
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
	 * Action for swapt two {@link MacroKey}
	 */
	private class SwapAction extends Action implements URGuiUpdater {

		final MacroScreen m;
		final int i;
		final int j;
		
		/**
		 * @param m MacroScreen of the MacroKeys
		 * @param i Index in {@link MacroScreen#getKeys()} of the first {@link MacroKey} to swap
		 * @param j Index in {@link MacroScreen#getKeys()} of the second {@link MacroKey} to swap
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

		
		/** Do the swap */
		private void swap() {
			List<MacroKey> l = m.getKeys();
			MacroKey mi = l.get(i);
			MacroKey mj = l.get(j);
			
			// The order of the following instruction is very important
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
	 * Action that edits {@link ModifyAction} to implement {@link Visitor}
	 */
	private class CustomModifyAction
		extends ModifyAction<MacroKey> implements URGuiUpdater {

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
	 * Interface that joined with Action permits of update the UI in the case of
	 * undo and redo actions done by the user
	 * <p>
	 * Must be implmented by all {@link Action} used
	 * </p>
	 */
	private static interface URGuiUpdater {
		
		/**
		 * Called when user executes the undo action
		 */
		public void onUndo();
		
		
		/**
		 * Called when user executes the redo action
		 */
		public void onRedo();
	}
}
