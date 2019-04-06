package com.macrokeyseditor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

import com.macrokeyseditor.util.JavaUtil;
import com.macrokeyseditor.util.Size;

/**
 * Manage the masks
 */
public final class MasksManager {

	public static final String PROPERTY_MASK_RESOLUTION = "Resolution";
	public static final String PROPERTY_MASK_DIAGONAL = "Diagonal";
	public static final String PROPERTY_MASK_NAME = "Name";
	
	/** Masks actually present */
	private final List<Mask> masks = new ArrayList<>();
	
	private Mask selected;
	
	/** Listeners for the changes done to this */
	private final List<MasksManagerListener> listeners = new ArrayList<>();
	
	
	public MasksManager() {
		
	}
	
	
	
	/**
	 * Adds the mask
	 * @param m Mask to add
	 * @throws IllegalArgumentException If the instance is already present
	 */
	public void addMask(@NonNull Mask m) {
		Objects.requireNonNull(m);
		if(JavaUtil.contains(m, masks)) {
			throw new IllegalArgumentException("Mask already present");
		}
		
		masks.add(m);
		fireMaskAdd(m);
	}
	
	
	
	/**
	 * Remove the given mask instance
	 * @param m Mask to remove
	 */
	public void removeMask(@NonNull Mask m) {
		Objects.requireNonNull(m);
		
		boolean remove = JavaUtil.removeInstance(m, masks);
		if(remove) {
			// If the removed mask is selected
			if(m == getSelected()) {
				select(null);
			}
			fireMaskRemove(m);
		}
	}
	
	
	
	/**
	 * @return Masks actually available
	 */
	public Mask[] getMasks() {
		Mask[] m = new Mask[masks.size()];
		masks.toArray(m);
		return m;
	}
	
	
	/**
	 * Edit a field of the given mask
	 * @param m mask to edit
	 * @param property Name of the field to edit
	 * @param value New value for the field
	 * @throws IllegalArgumentException If {@code m} is not present or 
	 * {@code proprety} is not valid
	 * @throws ClassCastException If {@code value} is not the right type
	 */
	public void editMaskProperty(Mask m, String property, Object value) {
		if(!JavaUtil.contains(m, masks)) {
			throw new IllegalArgumentException("Mask not present");
		}
		
		switch (property) {
		case PROPERTY_MASK_DIAGONAL:
			float f = (float) value;
			m.setDiameter(f);
			break;

		case PROPERTY_MASK_NAME:
			String s = (String) value;
			m.setName(s);
			break;
			
		case PROPERTY_MASK_RESOLUTION:
			Size r = (Size) value;
			m.setResolution(r);
			break;
			
		default: throw new IllegalArgumentException("Proprety not valid");
		}
		
		
		fireEditProprety(m, property, value);
	}
	
	
	
	/**
	 * Adds a listener for the events
	 * @param l Listener to add
	 */
	public void addListener(@NonNull MasksManagerListener l) {
		Objects.requireNonNull(l);
		
		listeners.add(l);
	}
	
	
	/**
	 * Remove the listener from the events
	 * @param l Listener to remove
	 */
	public void removeListener(@NonNull MasksManagerListener l) {
		Objects.requireNonNull(l);
		
		JavaUtil.removeInstance(l, listeners);
	}
	
	
	
	private void fireEditProprety(Mask m, String property, Object value) {
		assert m != null && property != null;
		
		listeners.forEach(l -> l.maskEdited(m, property, value));
	}
	
	
	private void fireMaskAdd(Mask m) {
		assert m != null;
		
		listeners.forEach(l -> l.maskAdded(m));
	}
	
	
	private void fireMaskRemove(Mask m) {
		assert m != null;
		
		listeners.forEach(l -> l.maskRemove(m));
	}
	
	
	private void fireMaskSelectedChanged() {
		listeners.forEach(l -> l.maskSelectedChanged(getSelected()));
	}



	/**
	 * Save the masks in this
	 * @param str Stream where to save the masks
	 * @throws IOException In case of an IO error
	 */
	public void save(OutputStream str) throws IOException {
		DataOutputStream s = new DataOutputStream(str);
		
		s.writeInt(masks.size());
		for(Mask m : masks) {
			s.writeUTF(m.getName());
			s.writeFloat(m.getDiagonal());
			s.writeInt(m.getResolution().width);
			s.writeInt(m.getResolution().height);
		}
	}
	
	
	
	/**
	 * Load the masks
	 * @param str Stream where to load from
	 * @throws IOException In case of an IO error
	 */
	public void load(InputStream str) throws IOException {
		DataInputStream s = new DataInputStream(str);
		
		List<Mask> mm = new ArrayList<>();
		
		int size = s.readInt();
		for(int i = 0; i < size; i++) {
			Mask m = new Mask();
			
			m.setName(s.readUTF());
			m.setDiameter(s.readFloat());
			int x = s.readInt();
			int y = s.readInt();
			m.setResolution(new Size(x, y));
			
			mm.add(m);
		}
		
		masks.clear();
		masks.addAll(mm);
	}
	
	
	
	/**
	 * @return Selected mask
	 */
	public Mask getSelected() {
		return selected;
	}



	/**
	 * Select the mask
	 * @param selected Mask to select; null if none
	 */
	public void select(Mask selected) {
		if(this.selected != selected) {
			this.selected = selected;
			fireMaskSelectedChanged();
		}
	}



	/**
	 * Listener for change events of {@link MaskManager}
	 */
	public interface MasksManagerListener {
		
		public void maskAdded(Mask m);
		
		public void maskRemove(Mask m);
		
		public void maskEdited(Mask mask, String property, Object value);
		
		public void maskSelectedChanged(Mask m);
	}
}
