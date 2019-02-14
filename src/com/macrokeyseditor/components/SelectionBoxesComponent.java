package com.macrokeyseditor.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.ItemSelectable;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

/**
 * Combobox esmpansa 
 * @param <T>
 */
@SuppressWarnings("serial")
public class SelectionBoxesComponent<T> extends Component implements ItemSelectable {
	
	/** Colore del background */
	private static final Color COLOR_BACKGROUND = new Color(164, 164, 164);
	/** Colore del background dell'item selezionato */
	private static final Color COLOR_BACKGROUND_SELECTED =
			new Color(100, 100, 100);
	private static final Color COLOR_BORDERS = new Color(57, 57, 57);
	private static final Color COLOR_BACKGROUND_OVER =
			new Color(200, 200, 200);
	private static final Color COLOR_FONT = Color.BLACK;
	
	/** Bordo con il controllo */
	private static final int BORDER_CONTROL = 5;
	/** Bordo tra un item e il separatore */
	private static final int BORDER_ITEM_SEPARATOR = 5;
	/** Distanza tra items */
	private static final int BORDER_ITEMS = BORDER_ITEM_SEPARATOR * 2;
	
	/** Rotondità del rettangolo, asse X */
	private static final int ARC_WIDTH = 10;
	/** Rotondità del rettangolo, asse Y */
	private static final int ARC_HEIGHT = 10;
	
	
	/** Elementi selezionabili dal componenete */
	private final List<T> items = new ArrayList<>();
	/** Indice dell'item che ha il cursore sopra; -1 se nessuno */
	private int mouseOverIndex = -1;
	/** Elemento attualmente selezionato; -1 sse {@link items} è vuota */
	private int selectedIndex = -1;
	
	/** Listener per gli eventi */
	private ItemListener itemListener;
	
	
	public SelectionBoxesComponent() {
		addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent e) {
				mouseOverIndex = indexCursor(e.getX());
				repaint();
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				
			}
		});
		
		addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				selectedIndex = indexCursor(e.getX());
				repaint();
				generateChangeSelectionEvent();
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// All'uscita mouse over disabilitato
				mouseOverIndex = -1;
				repaint();
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {

			}
			
			@Override
			public void mouseClicked(MouseEvent e) {

			}
		});
		
		
	}
	
	
	
	/**
	 * @param x Posizione del cursore
	 * @return Indice dell'item su cui si trova il cirsore; -1 se nessuno
	 */
	private int indexCursor(int x) {
		if(items.isEmpty()) {
			return -1;
		} else {
			return x / (getWidth() / items.size());
		}
		
	}
	
	
	/**
	 * Genera l'evento di selezione. Viene considerato come nuovo
	 * elemento selezionato quello correntemente selezionato.
	 */
	private void generateChangeSelectionEvent() {
		if(itemListener == null) {
			return;
		}
		T selected = items.get(selectedIndex);
		ItemEvent it = new ItemEvent(this, 0, selected, ItemEvent.SELECTED);
		itemListener.itemStateChanged(it);
	}
	
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addItemListener(ItemListener l) {
		if(l != null) {
			itemListener = l;
		}
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] getSelectedObjects() {
		if(items.isEmpty()) {
			return null;
		} else {
			return new Object[] { items.get(selectedIndex) };
		}
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeItemListener(ItemListener l) {
		if(itemListener == l) {
			itemListener = null;
		}
	}
	
	/**
	 * Imposta i valori possibili per gli items
	 * @param values Valori da impostare; non null, item contenuti non null
	 */
	public void setItems(@NonNull T[] values) {
		setItems(Arrays.asList(values));
	}
	
	/**
	 * Imposta i valori possibili per gli items
	 * @param values Valori da impostare; non null, item contenuti non null
	 */
	public void setItems(@NonNull Collection<T> values) {
		items.clear();
		
		selectedIndex = values.isEmpty() ? -1 : 0;
		mouseOverIndex = -1;
		
		for(T it : values) {
			if(it == null) {
				throw new NullPointerException("Items cannot be null");
			}
			items.add(it);
		}
	}
	
	
	
	/**
	 * Imposta l'elemento attualmente selezionato
	 * @param item Elemento da selezionare; non null
	 * @throws IllegalArgumentException Se l'elelemtno non viene trovato
	 */
	public void setSelectedItem(@NonNull T item) {
		int i = items.indexOf(item);
		if(i < 0) {
			throw new IllegalArgumentException("Item not found");
		} else {
			selectedIndex = i;
			repaint();
		}
	}
	
	
	/**
	 * Aggiunge un item alla collezione.
	 * L'elemenyo non deve essere già presente, la ricerca viene
	 * eseguita tramite {@link Object#equals(Object)}
	 * @param item Item da inserire
	 * @throws IllegalArgumentException Se è già stata inserito lo stesso elemento
	 */
	public void addItem(@NonNull T item) {
		if(items.contains(item)) {
			throw new IllegalArgumentException("Item already present");
		}
		
		// Se la lista è vuota marchio come selezionato il primo elemento
		// inserito
		if(items.isEmpty()) {
			selectedIndex = 0;
		}
		
		items.add(item);
		repaint();
	}
	
	
	/**
	 * Rimuove l'elemento indicato dagli item.
	 * Ricerca eseguita tramite {@link Object#equals(Object)}
	 * @param item Item da rimuovere (confronto tra istanze
	 * @throws IllegalArgumentException Se l'elemento non viene trovato
	 */
	public void removeItem(@NonNull T item) {
		if(!items.remove(item)) {
			throw new IllegalArgumentException("Item not present");
		}
		
		if(items.isEmpty()) {
			selectedIndex = -1;
		}
		repaint();
	}
	
	
	
	
	
	@Override
	public void paint(Graphics gg) {
		super.paint(gg);
		
		// Caso niente da renderizzare
		if(items.isEmpty()) {
			return;
		}
		
		Graphics2D g = (Graphics2D) gg;
		
		int utilSpace = getWidth() - (
				BORDER_CONTROL * 2 + 
				(items.size() - 1) * BORDER_ITEMS);
		int spaceItem = utilSpace / items.size();
	
		// Rendering sfondo e relativo contorno
		g.setColor(COLOR_BACKGROUND);
		g.fillRoundRect(0,
				0,
				getWidth() - 1,
				getHeight() - 1,
				ARC_WIDTH,
				ARC_HEIGHT);
		
		
		paintBackgroundOf(g, mouseOverIndex, spaceItem, COLOR_BACKGROUND_OVER);
		
		paintBackgroundOf(g, selectedIndex, spaceItem, COLOR_BACKGROUND_SELECTED);
		
		g.setColor(COLOR_BORDERS);
		g.drawRoundRect(0,
				0,
				getWidth() - 1,
				getHeight() - 1,
				ARC_WIDTH,
				ARC_HEIGHT);

		g.setFont(getFont());
		
		/** Cursore per la coordinata x del rettangolo che contiene la stringa
		 * dell'item attuale
		 */
		int x = BORDER_CONTROL;
		int i = 0;
		// Rendering scritte e separatori
		for(T item : items) {
			String str = item.toString();
			g.setColor(COLOR_FONT);
			drawCenteredString(g, str, x, 0, spaceItem, getHeight());
			
			if(i != items.size() - 1) {
				int separatorX = x + spaceItem + BORDER_ITEM_SEPARATOR;
				g.setColor(COLOR_BORDERS);
				g.drawLine(separatorX, 0, separatorX, getHeight());
			}
			
			x += spaceItem + BORDER_ITEMS;
			i++;
		}
	}
	
	/**
	 * Disegna il baground di un item indicato.
	 * Rendering non effettuato se {@code indexItem} < 0.
	 * @param g Grafica per il rendering; non null
	 * @param indexItem Indice dell'item in questione
	 * @param spaceItem Dimensione di un item (senza bordi)
	 * @param color Colore del background
	 */
	private void paintBackgroundOf(@NonNull Graphics2D g, int indexItem,
			int spaceItem, @NonNull Color color) {
		// Controllo che l'indice sia valido
		if(indexItem < 0) {
			return;
		}
		
		
		g.setColor(color);
		// Caso bisogna evidenziare il primo item
		if(indexItem == 0) {
			// Imposto la clip per la parte non arrotondata del rounded
			// rectangle
			g.setClip(0,
					0, 
					BORDER_CONTROL + spaceItem + BORDER_ITEM_SEPARATOR,
					getHeight());
			// Disegno un rounded rectangle più grande del necessario
			// (l'eccesso viene clippato)
			g.fillRoundRect(0,
					0,
					getWidth(),
					getHeight() - 1,
					ARC_WIDTH,
					ARC_HEIGHT);
			// Re-impostazione del clip
			g.setClip(0, 0, getWidth(), getHeight());
			
		} 
		// Caso bisogna evidenziare l'ultimo item
		else if(indexItem == items.size() - 1) {
			// Imposto la clip per la parte non arrotondata del rounded
			// rectangle
			g.setClip(getWidth() - (BORDER_CONTROL + spaceItem + 
						BORDER_ITEM_SEPARATOR),
					0, 
					getWidth(),
					getHeight());
			// Disegno un rounded rectangle più grande del necessario
			// (l'eccesso viene clippato)
			g.fillRoundRect(0,
					0,
					getWidth(),
					getHeight() - 1,
					ARC_WIDTH,
					ARC_HEIGHT);
			// Re-impostazione del clip
			g.setClip(0, 0, getWidth(), getHeight());
			
		} else {
			final int firstItemSize = BORDER_CONTROL + spaceItem +
					BORDER_ITEM_SEPARATOR;
			final int normalItemSize = spaceItem + BORDER_ITEM_SEPARATOR * 2;
			
			int x = firstItemSize + normalItemSize * (indexItem - 1);
			g.fillRect(x, 0, normalItemSize, getHeight());
		}
	}
	
	
	
	/**
	 * Ottiene i limiti della stringa in pixel
	 * @param g2 Grafica utilizzata per il rendering
	 * @param str Stringa da renderizzare
	 * @return Delimitazione della stringa in pixel
	 */
	private static Rectangle getStringBounds(Graphics2D g2, String str) {
		FontRenderContext frc = g2.getFontRenderContext();
		GlyphVector gv = g2.getFont().createGlyphVector(frc, str);
		return gv.getPixelBounds(null, 0, 0);
	}
	
	
	
	
	/**
	 * Centra la stringa nel rettangolo indicato
	 * @param g Grafica da utilizzare per il rendering della scritta; non null
	 * @param str Stringa da renderizzare; non null
	 * @param x Coordinata X del punto in altro a sx del rettangolo
	 * @param y Coordinata Y del punto in altro a sx del rettangolo
	 * @param width Lunghezza (asse X) del rettangolo
	 * @param height Altezza (asse Y) del rettangolo
	 */
	private static void drawCenteredString(@NonNull Graphics2D g,
			@NonNull String str, int x, int y, int width, int height) {
		Rectangle text = getStringBounds(g, str);
		int ascent = g.getFontMetrics().getAscent();
		
		int strX = x + width / 2 - text.width / 2;
		int strY = y + (height - text.height) / 2 + ascent;
		g.drawString(str, strX, strY);
	}
}
