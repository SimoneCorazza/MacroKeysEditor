package com.macrokeyseditor.components;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.eclipse.jdt.annotation.NonNull;

import com.macrokeyseditor.util.ColorUtil;

/** Componente per la selezione del colore */
public final class ColorComponent extends JComponent {

	private static final Color COLOR_PRESSED = Color.DARK_GRAY;
	private static final Color COLOR_OVER = Color.GRAY;
	
	
	/** Stringa che identifica l'azione del cambiamento del colore */
	public static String ACTION_COLOR_CHANG = "color";
	
	
	private static int SAMPLE_COLOR_MAX_X = 30;
	private static int SAMPLE_COLOR_MAX_Y = 30;
	
	/** Per la stringa rappresentativa del colore */
	JLabel lblName;
	
	/** Colore utilizzato */
	private java.awt.Color color;
	
	private final List<ActionListener> listners = new ArrayList<>();
	
	/** Indica se il mouse � sopra il controllo */
	private boolean mouseOver = false;
	/** Indica se il mouse sta cliccando il controllo */
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
	 * Aggiunge un listener
	 * @param l Azione da aggiungere
	 */
	public void addActionListener(@NonNull ActionListener l) {
		listners.add(l);
	}
	
	/**
	 * Genera l'evento con l'azione data
	 * @param e Evento
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
	 * @return Colore attualmente impostato
	 */
	public int getColor() {
		return ColorUtil.ColortoARGB(color);
	}

	/**
	 * @param argb Colore da impostare in formato argb; non genera l'evento del colore cambiato
	 */
	public void setColor(int argb) {
		this.color = ColorUtil.ARGBtoColor(argb);
		repaint();
	}
	
	/**
	 * @param c Colore da trasformare in stringa
	 * @return Stringa rappresentante il colore
	 */
	private static @NonNull String colorToString(@NonNull Color c) {
		return c.getAlpha() + ", " + c.getRed() + ", " + c.getGreen() + ", " + c.getBlue();
	}
	
}
