package com.macrokeyseditor.fur;

import java.util.Date;

import org.eclipse.jdt.annotation.NonNull;

/** Generic cancellable action */
public abstract class Action {
	
	/**
	 * Date of the insertion of this action
	 */
	Date date;
	
	/**
	 * Last merge date; null if none
	 */
	Date dateLastMerge;
	
	/**
	 * Valid only after the insertion in {@link ActionManager}
	 * @return Date of the insertion of this action
	 * @see ActionManager#addAction(Action)
	 * @see ActionManager#addAction(Action, boolean)
	 */
	protected Date getAddDate() {
		return date;
	}
	
	
	
	/**
	 * Valid only after the insertion in {@link ActionManager}
	 * @return Last merge date; null if no merge executed yet
	 * @see ActionManager#addAction(Action)
	 * @see ActionManager#addAction(Action, boolean)
	 */
	protected Date getLastMergeDate() {
		return dateLastMerge;
	}
	
	
	/**
	 * Calculate the time passed from the insertion date of this action
	 * @return Difference in milliseconds from the insertion date until now
	 * @see #getDate()
	 */
	protected long elapsedTimeAdd() {
		return new Date().getTime() - date.getTime();
	}
	
	
	/**
	 * Calculate the time passed since the last merge
	 * @return Elapsed time in milliseconds; -1 if no merge was executed
	 */
	protected long elapsedTimeLastMerge() {
		if(dateLastMerge == null) {
			return -1;
		} else {
			return new Date().getTime() - dateLastMerge.getTime();
		}
	}
	
	
	/** 
	 * Undo this action
	 */
	public abstract void undoExecute();
	
	
	/**
	 * Applay the action
	 */
	public abstract void execute();
	
	
	/**
	 * Try to merge this action with the given action.
	 * <p>
	 * <li> The implementation can decide to limit the amount of mergable action of the same time
	 * <li> The implementation may not support the merge
	 * <p>
	 * @param followingAction Action to merge
	 * @return True if the merge was executed, false otherwise
	 */
	public abstract boolean tryToMerge(@NonNull Action followingAction);
}
