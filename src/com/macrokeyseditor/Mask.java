package com.macrokeyseditor;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

import com.macrokeys.MacroScreen;
import com.macrokeys.screen.Screen;
import com.macrokeyseditor.util.Size;

/**
 * Mask used for the background of a {@link MacroScreen}.
 * The mask is intended to represent a screen of a device.
 */
public class Mask {
	
	/** Name of the mask; never null */
	private String name = "";
	
	private float diagonal = 1;
	
	/** Horizzontal resolution of the mask in pixels; #width >= #height */
	private Size resolution;
	
	
	public Mask() {
		
	}


	
	/**
	 * @return Name of the mask
	 */
	public @NonNull String getName() {
		return name;
	}


	/**
	 * @param name Name to set
	 */
	public void setName(@NonNull String name) {
		Objects.requireNonNull(name);
		
		this.name = name;
	}


	/**
	 * @return Diagonal in inches of the mask; > 0
	 */
	public float getDiagonal() {
		return diagonal;
	}


	/**
	 * @param diameter Diagonal in inches of the mask; > 0
	 * @throws IllegalArgumentException If {@code diameter} <= 0
	 */
	public void setDiameter(float diagonal) {
		if(diagonal <= 0) {
			throw new IllegalArgumentException("Parameter must be > 0");
		}
		
		this.diagonal = diagonal;
	}


	/**
	 * @return Pixel resolution of the mask; width and height > 0
	 */
	public Size getResolution() {
		return resolution;
	}


	/**
	 * Sets the resolution of the mask
	 * @param size Mask resolution in pixels
	 * @throws IllegalArgumentException If the lenght or height are <= 0
	 */
	public void setResolution(Size size) {
		if(size.width <= 0 && size.height <= 0) {
			throw new IllegalArgumentException("Parameters must be > 0");
		}
		
		
		if(size.width > size.height) {
			this.resolution = new Size(size);
		} else {
			this.resolution = new Size(size.height, size.width);
		}
	}
	
	
	/**
	 * Calculate the aspect ratio of the mask when horizontal.
	 * @return Aspect ratio; > 0
	 */
	public float aspectRatio() {
		return resolution.width / resolution.height;
	}
	
	
	/**
	 * Calculate the aspect ratio of the mask when horizontal.
	 * @return Aspect ratio; > 0
	 */
	public float reverseAspectRatio() {
		return resolution.height / resolution.width;
	}
	
	
	
	/**
	* Get the horizontally size of the mask for the given screen
	* @param screen Screen on which to calibrate the mask size
	* @return Size of the mask in pixels for the screen {@code screen}
	*/
	public Size getScreenSize(Screen screen) {
		Objects.requireNonNull(screen);
		
		float dp = (float)Math.sqrt(resolution.width * resolution.width +
				resolution.height * resolution.height);
		float dpi = dp / diagonal;
		float w = screen.getXDpi() / dpi * resolution.width;
		float h = screen.getYDpi() / dpi * resolution.height;
		
		return new Size((int)w, (int)h);
	}
	
	
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Mask) {
			Mask m = (Mask) obj;
			return m.name.equals(name) &&
					m.diagonal == diagonal &&
					m.resolution.equals(resolution);
		}
		
		return false;
	}
	
	
	
	@Override
	public String toString() {
		return name + "    " + diagonal + "\" (" +
				resolution.width + "x" + resolution.height + ")";
	}
}
