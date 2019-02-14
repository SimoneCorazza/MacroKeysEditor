package com.macrokeyseditor;

import java.util.Objects;

import com.macrokeys.MacroScreen;
import com.macrokeys.screen.Screen;
import com.macrokeyseditor.util.Size;

/**
 * Maschera utilizzata per il background di una {@link MacroScreen}.
 * La maschera ha lo scopo di rappresentare uno schermo di un dispositivo.
 */
public class Mask {
	
	/** Nome della maschera; non null */
	private String name = "";
	
	private float diagonal = 1;
	
	/** Risoluzione orizzontale della maschera in pixel; #width >= #height */
	private Size resolution;
	
	
	public Mask() {
		
	}


	
	/**
	 * @return Nome della maschera; non null
	 */
	public String getName() {
		return name;
	}


	/**
	 * @param name Nome da impostare; non null
	 * @throws NullPointerException Se {@code name} è null
	 */
	public void setName(String name) {
		Objects.requireNonNull(name);
		
		this.name = name;
	}


	/**
	 * @return Diagonale in pollici della maschera; > 0
	 */
	public float getDiagonal() {
		return diagonal;
	}


	/**
	 * @param diameter Diagonale in pollici della maschera; > 0
	 * @throws IllegalArgumentException Se {@code diameter} <= 0
	 */
	public void setDiameter(float diagonal) {
		if(diagonal <= 0) {
			throw new IllegalArgumentException("Parameter must be > 0");
		}
		
		this.diagonal = diagonal;
	}


	/**
	 * @return Risoluzione della maschera in pixel; width ed height > 0
	 */
	public Size getResolution() {
		return resolution;
	}


	/**
	 * Imposta la risoluzione della maschera
	 * @param size Risoluzione della maschera in pixel
	 * @throws IllegalArgumentException Se la lunghezza o l'altezza sono <= 0
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
	 * Calcola l'aspect ratio della maschera quando orizzontale.
	 * Calcolata tramite {@code width / height}
	 * @return Aspect ratio; > 0
	 */
	public float aspectRatio() {
		return resolution.width / resolution.height;
	}
	
	
	/**
	 * Calcola l'aspect ratio della maschera quando verticale.
	 * Calcolata tramite {@code height / width}
	 * @return Aspect ratio; > 0
	 */
	public float reverseAspectRatio() {
		return resolution.height / resolution.width;
	}
	
	
	
	/**
	 * Ottiene la dimensione della maschera in orizzontale per lo schermo
	 * indicato
	 * @param screen Schermo sul quale calibrare le dimensioni della maschera
	 * @return Dimensioni della maschera in pixel per lo schermo {@code screen}
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
