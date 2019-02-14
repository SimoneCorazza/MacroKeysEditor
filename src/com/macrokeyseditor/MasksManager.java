package com.macrokeyseditor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.macrokeyseditor.util.JavaUtil;
import com.macrokeyseditor.util.Size;

/**
 * Gestisce le maschere
 */
public final class MasksManager {

	public static final String PROPERTY_MASK_RESOLUTION = "Resolution";
	public static final String PROPERTY_MASK_DIAGONAL = "Diagonal";
	public static final String PROPERTY_MASK_NAME = "Name";
	
	/** Maschere attualmenti presenti */
	private final List<Mask> masks = new ArrayList<>();
	
	private Mask selected;
	
	/** Listener per le modifiche apportate al manager */
	private final List<MasksManagerListener> listeners = new ArrayList<>();
	
	
	public MasksManager() {
		
	}
	
	
	
	/**
	 * Aggiunge la maschera indicata.
	 * @param m Maschera da aggiungere
	 * @throws IllegalArgumentException Se l'istanza è già stata inserita
	 */
	public void addMask(Mask m) {
		Objects.requireNonNull(m);
		if(JavaUtil.contains(m, masks)) {
			throw new IllegalArgumentException("Mask already present");
		}
		
		masks.add(m);
		fireMaskAdd(m);
	}
	
	
	
	/**
	 * Rimuove la maschera indicata, se presente.
	 * @param m Maschera da rimuovere
	 */
	public void removeMask(Mask m) {
		Objects.requireNonNull(m);
		
		boolean remove = JavaUtil.removeInstance(m, masks);
		if(remove) {
			// Caso si è rimossa la mask selezionata
			if(m == getSelected()) {
				select(null);
			}
			fireMaskRemove(m);
		}
	}
	
	
	
	/**
	 * @return Ottiene la lista delle maschere attualmente disponibili
	 */
	public Mask[] getMasks() {
		Mask[] m = new Mask[masks.size()];
		masks.toArray(m);
		return m;
	}
	
	
	/**
	 * Modifica una proprietà della maschera indicata.
	 * @param m Maschera da modificare
	 * @param property Nome della proprietà indicata
	 * @param value Nuovo valore per la proprietà
	 * @throws IllegalArgumentException Se l'istanza {@code m} non è presente o
	 * {@code proprety} non è valida
	 * @throws ClassCastException Se {@code value} non è del tipo giusto
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
	 * Aggiunge un listener per gli eventi
	 * @param l Listener da aggiungere
	 */
	public void addListener(MasksManagerListener l) {
		listeners.add(l);
	}
	
	
	/**
	 * Rimuove il listener degli eventi
	 * @param l Listener da rimuovere
	 */
	public void removeListener(MasksManagerListener l) {
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
	 * Salva le maschere memorizzate
	 * @param str Stream dal quale caricare le maschere
	 * @throws IOException In caso di un errore di I/O
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
	 * Carica le maschere memorizzate
	 * @param str Stream dal quale caricare le maschere
	 * @throws IOException In caso di un errore di I/O o di errore nel formato di caricamento
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
	 * @return Mask attualmente selezionata per l'editor
	 */
	public Mask getSelected() {
		return selected;
	}



	/**
	 * Seleziona la maschera da utilizzare per l'editor
	 * @param selected Maschera da utilizzare; null se nessuna
	 */
	public void select(Mask selected) {
		if(this.selected != selected) {
			this.selected = selected;
			fireMaskSelectedChanged();
		}
	}



	/**
	 * Listener per gli eventi di modifica di {@link MaskManager}
	 */
	public interface MasksManagerListener {
		
		public void maskAdded(Mask m);
		
		public void maskRemove(Mask m);
		
		public void maskEdited(Mask mask, String property, Object value);
		
		public void maskSelectedChanged(Mask m);
	}
}
