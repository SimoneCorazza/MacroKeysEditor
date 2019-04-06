package com.macrokeyseditor.fur;

import java.util.Date;
import java.util.Objects;
import java.util.Stack;

import org.eclipse.jdt.annotation.NonNull;

/**
 * Manager to the collection of actions
 */
public final class ActionManager {
	
	private final Stack<Action> undo = new Stack<>();
	private final Stack<Action> redo = new Stack<>();
	
	
	public ActionManager() {
		
	}
	
	
	/**
	 * Executes the given action and adds it to the executed action list
	 * @param a Action to add
	 * @see Action#execute()
	 */
	public void add(@NonNull Action a) {
		Objects.requireNonNull(a);
		
		add(a, true);
	}
	
	/**
	 * Add the action in the executed action list
	 * @param a Action to add
	 * @param execute True if the action must be executed in this method, false otherwise
	 * @see Action#execute()
	 */
	public void add(@NonNull Action a, boolean execute) {
		Objects.requireNonNull(a);
		
		a.date = new Date();
		
		if(execute) {
			a.execute();
		}
		
		redo.clear();
		
		if(undo.isEmpty()) {
			undo.push(a);
		} else if(undo.peek().tryToMerge(a)) {
			// Update the merge date
			undo.peek().dateLastMerge = new Date();
		} else {
			undo.push(a);
		}
	}
	
	/**
	 * Undo the last action
	 * @return Cancelled action; null if none
	 */
	public Action undo() {
		if(undo.isEmpty()) {
			return null;
		} else {
			Action a = undo.pop();
			redo.push(a);
			a.undoExecute();
			
			return a;
		}
	}
	
	/**
	 * Redo the last undo action
	 * @return Actio redone; null if none
	 */
	public Action redo() {
		if(redo.isEmpty()) {
			return null;
		} else {
			Action a = redo.pop();
			undo.push(a);
			a.execute();
			
			return a;
		}
	}
}
