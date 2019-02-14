package com.macrokeyseditor.fur;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** 
 * Azione di modifica a un campo di un insieme di oggetti.
 * Unisce azioni effettuate per lo stesso oggetto e stesso attributo sse
 * il delay di esecuzione tra una modifica e l'altra è pari al parametro
 * {@code maxElapsedTime} passato al costruttore.
 */
public class ModifyAction<T> extends Action {

	/** 
	 * Memorizza tutte le istanze con i valori vecchi e nuovi
	 */
	private final List<State> instancesValues = new ArrayList<>();
	/** Istenze le cui proprietà vengono modificate */
	private final List<T> instances;
	
	private final Method mSet;
	private final String propertyName;
	/** 
	 * Massimo tempo trascorso dall'ultima azione dello stesso tipo per
	 * poterle unure
	 */
	private final int maxElapsedTime;
	
	/**
	 * @param name Nome dell'attributo da considerare (es. age, weight, length, ...)
	 * @param sets Lista di coppie (istanza, nuovo_valore), dove l'sitanza è il
	 * soggetto al set e get e il nuovo_valore è il nuovo valore desiderato da
	 * settare
	 * @param maxElapsedTime Massimo tempo trascorso dall'ultima azione dello
	 * stesso tipo per poterle unire
	 * @throws NullPointerException Se {@code name == null} o {@code obj == null}
	 * @throws NoSuchMethodException Se uno tra get(+name) o set(+name) non esiste
	 * @throws SecurityException Se i metodi get e set relativi non sono accessibili
	 * @throws IllegalAccessException Se la definizione dei metodi non è presente
	 * @throws IllegalArgumentException Se il parametro per il setter non è corretto
	 * o la lista{@code obj} è vuota
	 * @throws InvocationTargetException Se il getter o setter genera un'eccezione
	 */
	public ModifyAction(String name, List<Set<T>> sets, int maxElapsedTime)
			throws NullPointerException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if(name == null) {
			throw new NullPointerException("Parameter 'name' and 'obj' must be not null");
		} else
		//Mi assicuro che la prima lettera sia in maiuscolo (convezione per il set e get)
		if(!Character.isUpperCase(name.charAt(0))) {
			name = name.substring(0, 1).toUpperCase() + name.substring(1);
		} else if(sets.isEmpty()) {
			throw new IllegalArgumentException("Sets list empty");
		}
		
		Class<?> templateClass = sets.get(0).instance.getClass();
		String getter = "get" + name;
		Method mGet = templateClass.getMethod(getter);
		
		String setter = "set" + name;
		//Ottengo il setter grazie al tipo ritornato dal getter
		mSet = templateClass.getMethod(setter, mGet.getReturnType());
		
		
		instances = new ArrayList<>();
		for(Set<T> s : sets) {
			State p = new State();
			p.instance = s.instance;
			p.oldValue = mGet.invoke(s.instance);
			p.newValue = s.value;
			
			mSet.invoke(s.instance, s.value);
			
			instancesValues.add(p);
			// Aggiungo l'istanza alle istanze modificate
			instances.add(s.instance);
		}
		
		
		this.propertyName = name;
		this.maxElapsedTime = maxElapsedTime;
		
	}
	
	
	/**
	 * @return Nome della properietà che si va a modificare
	 */
	public String getPropertyName() {
		return propertyName;
	}
	
	
	/**
	 * @return Istanze sulle quali vengono apportate le modifiche
	 */
	public List<T> getObject() {
		return instances;
	}
	
	
	@Override
	public void undoExecute() {
		try {
			for(State p : instancesValues) {
				mSet.invoke(p.instance, p.oldValue);				
			}
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			assert false : "Il metodo deve funzionare (già testato nel costruttore)";
		}
	}

	@Override
	public void execute() {
		try {
			for(State p : instancesValues) {
				mSet.invoke(p.instance, p.newValue);
			}
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			assert false : "Il metodo deve funzionare (già testato nel costruttore)";
		}
	}



	@Override
	public boolean tryToMerge(Action followingAction) {
		long elapsed = elapsedTimeLastMerge();
		if(!followingAction.getClass().isAssignableFrom(getClass())) {
			return false;
		} else if(elapsed > maxElapsedTime) {
			return false;
		}
		

		@SuppressWarnings("unchecked")
		ModifyAction<T> fa = (ModifyAction<T>)followingAction;
		
		// Controllo che siano modifiche dello stesso attributo e della stesso
		// insieme di oggetti
		if(propertyName.equals(fa.propertyName) &&
				sameInstances(fa.instancesValues, instancesValues)) {
			for(State s : fa.instancesValues) {
				State ss = findState(s.instance, instancesValues);
				ss.newValue = s.newValue;
			}
			return true;
		}
		
		
		return false;
	}
	
	
	
	/**
	 * Trova lo stato avente l'istanza indicata
	 * @param instance Istanza da trovare
	 * @param l Lista sulla quale fare la ricerca
	 * @return State contenente l'istanza indicata; null se non trovata
	 */
	private State findState(T instance, List<State> l) {
		assert instance != null && l != null;
		
		for(State s : l) {
			if(s.instance == instance) {
				return s;
			}
		}
		return null;
	}
	
	
	/**
	 * Indica se le due liste (senza ripetizioni) hanno le stesse istanze.
	 * @param a Prima lista; senza ripetizioni di elelmenti
	 * @param b Seconda lista; senza ripetizioni di elelmenti
	 * @return True se le due liste hanno le stese istanze
	 */
	private boolean sameInstances(List<State> a, List<State> b) {
		if(a.size() != b.size()) {
			return false;
		}
		
		boolean aInB = sameInstancesOfAInB(a, b);
		boolean bInA = sameInstancesOfAInB(b, a);
		return aInB && bInA;
	}
	
	
	/**
	 * Indica se le istanze di {@code a} sono contenute in {@code b}
	 * @param a Lista sorgente delle istanze
	 * @param b Lista da verificare
	 * @return True se le istanze di {@code a} sono contenute in {@code b},
	 * False altrimenti
	 */
	private boolean sameInstancesOfAInB(List<State> a, List<State> b) {
		for(State s : a) {
			boolean found = false;
			for(State ss : b) {
				if(ss.instance == s.instance) {
					found = true;
					break;
				}
			}
			if(!found) {
				return false;
			}
		}
		
		return true;
	}

	
	
	private class State {
		T instance;
		Object oldValue;
		Object newValue;
	}
	
	
	/**
	 * Struttura dati perla madifica del valore ({@link #value}) di una
	 * proprietà (qui non indicata) ad una particolare istanza
	 * ({@link #instance}) di un oggetto
	 * @param <T> Classe soggetta alla modifica della proprietà
	 */
	public static class Set<T> {
		/** Soggetto alla modifica */
		public final T instance;
		/** Valore della modifica */
		public final Object value;
		
		/**
		 * @param instance Soggetto alla modifica
		 * @param value Valore della modifica
		 */
		public Set(T instance, Object value) {
			Objects.requireNonNull(instance);
			this.instance = instance;
			this.value = value;
		}
	}
}
