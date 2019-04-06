package com.macrokeyseditor.copypaste;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;

/** 
 * Clipboard owner for copy paste of {@link MacroKey}
 * Tutorial: http://stackoverflow.com/questions/14276182/java-use-clipboard-to-copy-paste-java-objects-between-different-instances-of-sa
 */
public final class MKClipboardOwner implements ClipboardOwner {
	
	public static final MKClipboardOwner ISTANCE = new MKClipboardOwner();
	
	private MKClipboardOwner() { }
	
	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		
	}
	
}
