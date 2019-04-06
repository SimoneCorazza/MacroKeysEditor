package com.macrokeyseditor.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

/**
 * Static class for generic utility
 */
public final class JavaUtil {

	private JavaUtil() { }
	
	
	/**
	 * Remove the first instance in the collection.
	 * @param instance Instance to remove
	 * @param c Collection where to find the instance
	 * @return True if instance removed, False otherwise
	 */
	public static <T> boolean removeInstance(T instance, @NonNull Collection<T> c) {
		Objects.requireNonNull(c);
		return removeInstance(instance, c.iterator());
	}
	
	/**
	 * Remove the first instance in the collection.
	 * @param instance Instance to remove
	 * @param it Iterator where to find the instance
	 * @return True if instance removed, False otherwise
	 */
	public static <T> boolean removeInstance(T instance, @NonNull Iterator<T> it) {
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
	 * Find the given instance
	 * @param instance Instance to find
	 * @param c Collection where to find the instance
	 * @return True if instance found, False otherwise
	 */
	public static <T> boolean contains(T instance, @NonNull Collection<T> c) {
		Objects.requireNonNull(c);
		return contains(instance, c.iterator());
	}
	
	/**
	 * Find the given instance
	 * @param instance Instance to find
	 * @param it Iterator where to find the instance
	 * @return True if instance found, False otherwise
	 */
	public static <T> boolean contains(T instance, @NonNull Iterator<T> it) {
		Objects.requireNonNull(it);
		
		while(it.hasNext()) {
			if(it.next() == instance) {
				return true;
			}
		}
		
		return false;
	}
}
