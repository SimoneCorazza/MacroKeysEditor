package com.macrokeyseditor.util;

import java.awt.Color;

/** 
 * Static utility class for colors
 */
public final class ColorUtil {
	
	private static int ALPHA_MASK = 0xFF000000;
	private static int RED_MASK = 0x00FF0000;
	private static int GREEN_MASK = 0x0000FF00;
	private static int BLUE_MASK = 0x000000FF;
	
	private ColorUtil() {}
	
	/**
	 * Convert a color from the format AARRGGBB {@link java.awt.Color}
	 * @param argb Color to convert
	 * @return Converted color
	 */
	public static java.awt.Color ARGBtoColor(int argb) {
		int a = (argb & ALPHA_MASK) >>> 24;
		int r = (argb & RED_MASK) >>> 16;
		int g = (argb & GREEN_MASK) >>> 8;
		int b = argb & BLUE_MASK;
		return new Color(r, g, b, a);
	}
	
	/**
	 * Convert a color from {@link java.awt.Color} to the format AARRGGBB
	 * @param c Color to convert
	 * @return Converted color
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
