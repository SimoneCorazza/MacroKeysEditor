package com.macrokeyseditor.util;


/**
 * Memorizza le dimensioni bidimensionali di un oggetto
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
	 * Costruttore di copia
	 * @param s Size da copiare
	 */
	public Size(Size s) {
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
