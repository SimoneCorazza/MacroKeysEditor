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
	
	/** Background color */
	private static final Color COLOR_BACKGROUND = new Color(164, 164, 164);
	
	/** Background color for the selected item */
	private static final Color COLOR_BACKGROUND_SELECTED = new Color(100, 100, 100);
	
	private static final Color COLOR_BORDERS = new Color(57, 57, 57);
	private static final Color COLOR_BACKGROUND_OVER =
			new Color(200, 200, 200);
	private static final Color COLOR_FONT = Color.BLACK;
	
	/** Border inside the control */
	private static final int BORDER_CONTROL = 5;
	
	/** Border between the item and its separator */
	private static final int BORDER_ITEM_SEPARATOR = 5;
	
	/** Distance between items */
	private static final int BORDER_ITEMS = BORDER_ITEM_SEPARATOR * 2;
	
	/** Roundness of the rectangle int the X axis */
	private static final int ARC_WIDTH = 10;
	
	/** Roundness of the rectangle int the Y axis */
	private static final int ARC_HEIGHT = 10;
	
	
	/** Selectionable items from the component */
	private final List<T> items = new ArrayList<>();
	
	/** Index of the item that has the cursor over it; -1 if none */
	private int mouseOverIndex = -1;
	
	/** Selected item; -1 iif {@link #items} is empty */
	private int selectedIndex = -1;
	
	/** Event listener */
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
				// At the exit mouse over disabled
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
	 * @param x Cursor posizione
	 * @return Index of the item that has the cursor over it; -1 if none
	 */
	private int indexCursor(int x) {
		if(items.isEmpty()) {
			return -1;
		} else {
			return x / (getWidth() / items.size());
		}
		
	}
	
	
	/**
	 * Generates the selection event. The new selected istem is the current selected one.
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
	 * Sets the possible values fro the items
	 * @param values Value to set; contained items must be not null
	 */
	public void setItems(@NonNull T[] values) {
		setItems(Arrays.asList(values));
	}
	
	/**
	 * Sets the possible values fro the items
	 * @param values Value to set; contained items must be not null
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
	 * Sets the item to select
	 * @param item Item to select
	 * @throws IllegalArgumentException If the item is not present
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
	 * Add an item to the collection.
	 * The given item must be not already present, the search is done using {@link Object#equals(Object)}
	 * @param item Item to add
	 * @throws IllegalArgumentException If the item was already present
	 */
	public void addItem(@NonNull T item) {
		if(items.contains(item)) {
			throw new IllegalArgumentException("Item already present");
		}
		
		// If the list is empty the selected item becomes the first inserted
		if(items.isEmpty()) {
			selectedIndex = 0;
		}
		
		items.add(item);
		repaint();
	}
	
	
	/**
	 * Remove the item.
	 * The search is done by using {@link Object#equals(Object)}
	 * @param item Item to remove
	 * @throws IllegalArgumentException If the element was not found
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
		
		// Nothing to render
		if(items.isEmpty()) {
			return;
		}
		
		Graphics2D g = (Graphics2D) gg;
		
		int utilSpace = getWidth() - (
				BORDER_CONTROL * 2 + 
				(items.size() - 1) * BORDER_ITEMS);
		int spaceItem = utilSpace / items.size();
	
		// Background rendering and the border
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
		
		
		// Cursor for the x axis of the rectangòe that contains the string of the actual item
		int x = BORDER_CONTROL;
		int i = 0;
		
		// Text and separators rendering
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
	 * Draw the background fot a n item.
	 * Rendering not done if {@code indexItem} < 0.
	 * @param g Graphics for the rendering
	 * @param indexItem Index of the item to render
	 * @param spaceItem Size of an item (with no borders)
	 * @param color Background color
	 */
	private void paintBackgroundOf(@NonNull Graphics2D g, int indexItem,
			int spaceItem, @NonNull Color color) {
		if(indexItem < 0) {
			return;
		}
		
		
		g.setColor(color);
		
		// Case the first item needs to be hilighted
		if(indexItem == 0) {
			// Set the clip for the not rounded portion of the rounded rectangle
			g.setClip(0,
					0, 
					BORDER_CONTROL + spaceItem + BORDER_ITEM_SEPARATOR,
					getHeight());
			
			// Draw the rounded rectangle bigger than needed because the excess is clipped
			g.fillRoundRect(0,
					0,
					getWidth(),
					getHeight() - 1,
					ARC_WIDTH,
					ARC_HEIGHT);
			
			// Reset the clip size
			g.setClip(0, 0, getWidth(), getHeight());
			
		}
		// Case the last item needs to be hilighted
		else if(indexItem == items.size() - 1) {
			// Set the clip for the not rounded portion of the rounded rectangle
			g.setClip(getWidth() - (BORDER_CONTROL + spaceItem + 
						BORDER_ITEM_SEPARATOR),
					0, 
					getWidth(),
					getHeight());
			
			// Set the clip for the not rounded portion of the rounded rectangle
			g.fillRoundRect(0,
					0,
					getWidth(),
					getHeight() - 1,
					ARC_WIDTH,
					ARC_HEIGHT);
			
			// Reset the clip size
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
	 * Gests the bound of the string in pixels
	 * @param g2 Graphics to use for the rendering
	 * @param str String to render
	 * @return Bound for the string in pixels
	 */
	private static Rectangle getStringBounds(@NonNull Graphics2D g2, String str) {
		FontRenderContext frc = g2.getFontRenderContext();
		GlyphVector gv = g2.getFont().createGlyphVector(frc, str);
		return gv.getPixelBounds(null, 0, 0);
	}
	
	
	
	
	/**
	 * Center the string in the rectangle
	 * @param g Graphics to use for the rendering
	 * @param str String to render
	 * @param x X coordinate of the point in the top left of the rectangle
	 * @param y X coordinate of the point in the top left of the rectangle
	 * @param width Lenght of the rectangle (X axis)
	 * @param height Height of the rectangle (Y axis)
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
