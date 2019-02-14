package com.macrokeyseditor.fur;

import java.util.Date;

/** Generica azione annullabile */
public abstract class Action {
	
	/**
	 * Data di inserimento dell'azione
	 */
	Date date;
	
	/**
	 * Ultimo istante in cui è stato eseguito lo scorso merge; null se mai
	 */
	Date dateLastMerge;
	
	/**
	 * Valido solo dopo l'inserimento in {@link ActionManager}
	 * @return Data di inserimento dell'azione
	 * @see ActionManager#addAction(Action)
	 * @see ActionManager#addAction(Action, boolean)
	 */
	protected Date getAddDate() {
		return date;
	}
	
	
	
	/**
	 * Valido solo dopo l'inserimento in {@link ActionManager}
	 * @return Data dell'ultomo merge eseguito da this dell'azione; null se
	 * nessun merge è stato ancora eseguito
	 * @see ActionManager#addAction(Action)
	 * @see ActionManager#addAction(Action, boolean)
	 */
	protected Date getLastMergeDate() {
		return dateLastMerge;
	}
	
	
	/**
	 * Calcola il tempo trascorso dalla data di inserimento
	 * @return Differenza di tempo in millisecondi dalla di aggiunta
	 * @see #getDate()
	 */
	protected long elapsedTimeAdd() {
		return new Date().getTime() - date.getTime();
	}
	
	
	/**
	 * Calcola il tempo trascorso dall'utlimo inserimento
	 * @return Tempo trascorso in millisecondi; -1 se mai
	 */
	protected long elapsedTimeLastMerge() {
		if(dateLastMerge == null) {
			return -1;
		} else {
			return new Date().getTime() - dateLastMerge.getTime();
		}
	}
	
	
	/** 
	 * Annulla l'azione
	 */
	public abstract void undoExecute();
	
	
	/**
	 * Applica l'azione
	 */
	public abstract void execute();
	
	
	/**
	 * Cerca di unire this con l'azione indicata.
	 * <p>
	 * <li> L'implementazione può decidere un limite massimo alla quantità di
	 * azioni che è possibile unire
	 * <li> L'implementazione può decidere di non eseguire mai l'unione
	 * <p>
	 * @param followingAction Azione seguente; non null
	 * @return True merge ha avuto successo, false altrimenti
	 * @throws NullPointerException Se {@code followingAction} è null
	 */
	public abstract boolean tryToMerge(Action followingAction);
}
