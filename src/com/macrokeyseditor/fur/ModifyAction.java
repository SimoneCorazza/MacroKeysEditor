package com.macrokeyseditor.fur;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

/** 
 * {@link Action} of an field edit of a set of objects.
 * <p>
 * Merge the action iif the time elapsed between the elapsed action is less or
 * equal to {@code maxElapsedTime} 
 * </p>
 */
public class ModifyAction<T> extends Action {

	/** Store all values of the fields of the objects */
	private final List<State> instancesValues = new ArrayList<>();
	
	/** Instances of the edited objects */
	private final List<T> instances;
	
	private final Method mSet;
	private final String propertyName;
	
	/** 
	 * Maximum elapsed time to merge two {@link ModifyAction}
	 */
	private final int maxElapsedTime;
	
	/**
	 * @param name Name of the field to edit (es. age, weight, length, ...)
	 * @param sets List of couples (instance, new value) where the instance is
	 * the object subject to the edit and the new value is the new value to set
	 * @param maxElapsedTime Maximum elapsed time to merge two {@link ModifyAction}
	 * @throws NoSuchMethodException If one method {@code get}(+name) or
	 * {@code set}(+name) does not exists
	 * @throws SecurityException If the set or get methods are not accessible
	 * @throws IllegalAccessException If the set or get methods are not present
	 * @throws IllegalArgumentException If the parameter of the set is not correct
	 * @throws IllegalArgumentException If {@code sets} is empty
	 * @throws InvocationTargetException If the get or the set generates exceptions
	 */
	public ModifyAction(@NonNull String name, @NonNull List<Set<T>> sets, int maxElapsedTime)
			throws NullPointerException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if(name == null) {
			throw new NullPointerException("Parameter 'name' and 'obj' must be not null");
		} else
			
		// The first letter must be uppercase
		if(!Character.isUpperCase(name.charAt(0))) {
			name = name.substring(0, 1).toUpperCase() + name.substring(1);
		} else if(sets.isEmpty()) {
			throw new IllegalArgumentException("Sets list empty");
		}
		
		final String getter = "get" + name;
		final String setter = "set" + name;
		
		Class<?> templateClass = sets.get(0).instance.getClass();
		Method mGet = templateClass.getMethod(getter);
		
		// The return type of the get find the correct set
		mSet = templateClass.getMethod(setter, mGet.getReturnType());
		
		
		instances = new ArrayList<>();
		for(Set<T> s : sets) {
			State p = new State();
			p.instance = s.instance;
			p.oldValue = mGet.invoke(s.instance);
			p.newValue = s.value;
			
			mSet.invoke(s.instance, s.value);
			
			instancesValues.add(p);
			instances.add(s.instance);
		}
		
		
		this.propertyName = name;
		this.maxElapsedTime = maxElapsedTime;
		
	}
	
	
	/**
	 * @return Name of the field that is modified
	 */
	public String getPropertyName() {
		return propertyName;
	}
	
	
	/**
	 * @return Instances where the edit is done
	 */
	public List<T> getObject() {
		return instances; // TODO: unmodifiable list?
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
		
		// Check if the edit is done at the same field and the same instances
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
	 * Finds the instance of the instance
	 * @param instance Instance to find
	 * @param l List where do the search
	 * @return {@link State} of the instance; null if not found
	 */
	private State findState(@NonNull T instance, @NonNull List<State> l) {
		assert instance != null && l != null;
		
		for(State s : l) {
			if(s.instance == instance) {
				return s;
			}
		}
		return null;
	}
	
	
	/**
	 * Check if two lists have the same instances. Each list must not contain duplications
	 * @param a First list
	 * @param b Second list
	 * @return True if the two lists have the same instances
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
	 * Checks if the instances of {@code a} are contained in {@code b}
	 * @param a List a
	 * @param b List b
	 * @return True if instances of {@code a} are contained in {@code b}, false otherwise
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
	 * Data structure for the edit of ({@link #value}) of a field of an instance
	 * @param <T> Class type subject toe the field edit
	 */
	public static class Set<T> {
		/** Instance subject to the edit */
		public final T instance;
		
		/** New value for the field */
		public final Object value;
		
		/**
		 * @param instance Instance subject of the edit
		 * @param value New value for the field
		 */
		public Set(@NonNull T instance, Object value) {
			Objects.requireNonNull(instance);
			
			this.instance = instance;
			this.value = value;
		}
	}
}
