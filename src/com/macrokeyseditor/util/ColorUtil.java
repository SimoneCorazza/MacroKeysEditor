package com.macrokeyseditor.util;

import java.awt.Color;

/** Utility statiche per l'utilizzo dei colori su questa piattaforma */
public final class ColorUtil {
	
	private static int ALPHA_MASK = 0xFF000000;
	private static int RED_MASK = 0x00FF0000;
	private static int GREEN_MASK = 0x0000FF00;
	private static int BLUE_MASK = 0x000000FF;
	
	private ColorUtil() {}
	
	/**
	 * Converte il colore in formato AARRGGBB in color
	 * @param argb Colore da convertire
	 * @return Colore convertito
	 */
	public static java.awt.Color ARGBtoColor(int argb) {
		int a = (argb & ALPHA_MASK) >>> 24;
		int r = (argb & RED_MASK) >>> 16;
		int g = (argb & GREEN_MASK) >>> 8;
		int b = argb & BLUE_MASK;
		return new Color(r, g, b, a);
	}
	
	/**
	 * Converte il colore nel formato AARRGGBB
	 * @param c Colore da convertire
	 * @return Colore convertito
	 */
	public static int ColortoARGB(java.awt.Color c) {
		int a = c.getAlpha();
		int r = c.getRed();
		int g = c.getGreen();
		int b = c.getBlue();
		
		int color = 0;
		color |= ALPHA_MASK & (a << 24);
		color |= RED_MASK & (r << 16);
		color |= GREEN_MASK & (g << 8);
		color |= BLUE_MASK & b;
		
		return color;
	}
}
