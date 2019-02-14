package com.macrokeyseditor.copypaste;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

import com.macrokeys.MacroKey;

/** 
 * Trasferimento mediante copia incolla per {@link MacroKey}
 * <p>Tutorial: http://stackoverflow.com/questions/14276182/java-use-clipboard-to-copy-paste-java-objects-between-different-instances-of-sa</p>
 */
public final class MKTransfer implements Transferable {
	
	public static final DataFlavor MK_FLAVOR = new DataFlavor(MacroKey.class, MacroKey.class.toString());
	private final List<MacroKey> t;
	
	/**
	 * @param l Lista di tasti
	 */
	public MKTransfer(@NonNull List<MacroKey> l) {
		Objects.requireNonNull(l);
		
		t = new ArrayList<>();
		for(MacroKey m : l) {
			Objects.requireNonNull(m);
			t.add(m);
		}
	}
	
	
	@Override
	public Object getTransferData(DataFlavor d) 
			throws UnsupportedFlavorException, IOException {
		if(isDataFlavorSupported(d)) {
			return t;
		} else {
			throw new UnsupportedFlavorException(d);
		}
		
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] { MK_FLAVOR };
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor d) {
		return d.equals(MK_FLAVOR);
	}

}
