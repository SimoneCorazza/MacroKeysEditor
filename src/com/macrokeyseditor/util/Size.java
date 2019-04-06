package com.macrokeyseditor.util;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

/**
 * Class for mesure a size
 */
public class Size {
	
	public int width;
	public int height;
	
	public Size() {
		
	}
	
	
	public Size(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	
	/**
	 * Copy contructor
	 * @param s Size to copy
	 */
	public Size(@NonNull Size s) {
		Objects.requireNonNull(s);
		
		this.width = s.width;
		this.height = s.height;
	}
	
	
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Size) {
			Size s = (Size) obj;
			return width == s.width && height == s.height;
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		return width + ", " + height;
	}
}
