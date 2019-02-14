package com.macrokeyseditor.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

/**
 * Classe statica che raccogli utility
 */
public final class JavaUtil {

	private JavaUtil() { }
	
	
	/**
	 * Rimuove la prima occorrenza dell'istanza indicata.
	 * @param instance Instanza da rimuovere
	 * @param c Collezione dove effettuare la rimozione
	 * @return True se rimozione eseguita, False altrimenti
	 * @throws NullPointerException Se {@code c} è null
	 */
	public static <T> boolean removeInstance(T instance, Collection<T> c) {
		Objects.requireNonNull(c);
		return removeInstance(instance, c.iterator());
	}
	
	
	/**
	 * Rimuove la prima occorrenza dell'istanza indicata.
	 * @param instance Instanza da rimuovere
	 * @param it Iteratore dove eseguire la rimozione
	 * @return True se rimozione eseguita, False altrimenti
	 * @throws NullPointerException Se {@code it} è null
	 */
	public static <T> boolean removeInstance(T instance, Iterator<T> it) {
		Objects.requireNonNull(it);
		
		while(it.hasNext()) {
			if(it.next() == instance) {
				it.remove();
				return true;
			}
		}
		
		return false;
	}
	
	
	
	/**
	 * Indica se la collezione contiene l'istanza indicata.
	 * @param instance Istanza da trovare
	 * @param c Collezzione sulla quale fare la ricerca
	 * @return True in caso trovata, False altrimenti
	 * @throws NullPointerException Se {@code c} è null
	 */
	public static <T> boolean contains(T instance, Collection<T> c) {
		Objects.requireNonNull(c);
		return contains(instance, c.iterator());
	}
	
	
	/**
	 * Indica se la collezione contiene l'istanza indicata.
	 * @param instance Istanza da trovare
	 * @param it Iteratore sul quale fare la ricerca
	 * @return True in caso trovata, False altrimenti
	 * @throws NullPointerException Se {@code c} è null
	 */
	public static <T> boolean contains(T instance, Iterator<T> it) {
		Objects.requireNonNull(it);
		
		while(it.hasNext()) {
			if(it.next() == instance) {
				return true;
			}
		}
		
		return false;
	}
}
