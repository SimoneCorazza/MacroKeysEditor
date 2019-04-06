package com.macrokeyseditor.components;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.eclipse.jdt.annotation.NonNull;

import com.macrokeyseditor.util.ColorUtil;

/** Component for the selection of a color */
public final class ColorComponent extends JComponent {

	private static final Color COLOR_PRESSED = Color.DARK_GRAY;
	private static final Color COLOR_OVER = Color.GRAY;
	
	
	/** String that identifies the action of the color change */
	public static String ACTION_COLOR_CHANG = "color";
	
	
	private static int SAMPLE_COLOR_MAX_X = 30;
	private static int SAMPLE_COLOR_MAX_Y = 30;
	
	/** For the string rapresenting the color */
	JLabel lblName;
	
	/** Used color */
	private java.awt.Color color;
	
	private final List<ActionListener> listners = new ArrayList<>();
	
	/** Flag to indicate that the cursor is over this */
	private boolean mouseOver = false;
	
	/** Flag to indicate that the cursor is clicking this */
	private boolean mouseDown = false;
	
	
	
	
	public ColorComponent() {
		color = java.awt.Color.WHITE;
		
		lblName = new JLabel();
		lblName.setText(colorToString(color));
		lblName.setHorizontalAlignment(SwingConstants.CENTER);
		
		addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				mouseDown = false;
				repaint();
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				mouseDown = true;
				repaint();
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				mouseOver = false;
				mouseDown = false;
				repaint();
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				mouseOver = true;
				repaint();
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				java.awt.Color c = JColorChooser.showDialog(ColorComponent.this, "Choose color", color);
				if(c != null) {
					color = c;
				}
				lblName.setText(colorToString(color));
				repaint();
				
				generateEvents(new ActionEvent(ColorComponent.this, ActionEvent.ACTION_FIRST, ACTION_COLOR_CHANG));
			}
		});
		
		addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
            	int w = ColorComponent.this.getWidth();
            	int h = ColorComponent.this.getHeight();
            	      	
            	lblName.setBounds(SAMPLE_COLOR_MAX_X, 0, w - SAMPLE_COLOR_MAX_X, h);
            }
		});
		
		
		add(lblName);
	}
	
	
	
	
	
	/**
	 * Add a listener
	 * @param l Action to add
	 */
	public void addActionListener(@NonNull ActionListener l) {
		listners.add(l);
	}
	
	/**
	 * Generate the event for the given event
	 * @param e Event
	 */
	private void generateEvents(@NonNull ActionEvent e) {
		for(ActionListener l : listners) {
			l.actionPerformed(e);
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		
		if(mouseOver) {
			if(mouseDown) {
				g.setColor(COLOR_PRESSED);
			} else {
				g.setColor(COLOR_OVER);
			}
		} else {
			g.setColor(getBackground());
		}
		g.fillRect(0, 0, getWidth(), getHeight());
		
		int y = (getHeight() - SAMPLE_COLOR_MAX_Y) / 2;
		g.setColor(color);
		g.fillRect(0, y, SAMPLE_COLOR_MAX_X, SAMPLE_COLOR_MAX_Y);
	}
	
	
	/**
	 * @return Color actually set
	 */
	public int getColor() {
		return ColorUtil.ColortoARGB(color);
	}

	/**
	 * @param argb Color to set in the format argb; this call does not generate the event of color changed
	 */
	public void setColor(int argb) {
		this.color = ColorUtil.ARGBtoColor(argb);
		repaint();
	}
	
	/**
	 * @param c Color to transform in string
	 * @return String rapresenting the color
	 */
	private static @NonNull String colorToString(@NonNull Color c) {
		return c.getAlpha() + ", " + c.getRed() + ", " + c.getGreen() + ", " + c.getBlue();
	}
	
}
