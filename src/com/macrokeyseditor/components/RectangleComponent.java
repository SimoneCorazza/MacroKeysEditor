package com.macrokeyseditor.components;

import javax.swing.JPanel;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.eclipse.jdt.annotation.NonNull;

import com.macrokeys.rendering.RectF;

/** Panel to collect the data of a rectangle */
public class RectangleComponent extends JPanel {
	
	/** Actual rectangle */
	private RectF rec = null;
	
	private final ChangeLis list = new ChangeLis();
	
	/** 
	 * Flag to indicate that the spinner values are being set;
	 * to indicate the futility of running events
	*/
	private boolean setting = false;
	
	private JSpinner spnX;
	private JSpinner spnY;
	private JSpinner spnWidth;
	private JSpinner spnHeight;
	
	/** Listeners for the change of the property */
	private final List<ChangeListener> changeListeners = new ArrayList<>();

	public RectangleComponent() {
		setLayout(null);
		
		JLabel lblX = new JLabel("X:");
		lblX.setBounds(0, 8, 10, 14);
		add(lblX);
		
		spnX = new JSpinner();
		spnX.setBounds(55, 5, 124, 20);
		spnX.addChangeListener(list);
		add(spnX);
		
		JLabel lblY = new JLabel("Y:");
		lblY.setBounds(0, 36, 10, 14);
		add(lblY);
		
		spnY = new JSpinner();
		spnY.setBounds(55, 33, 124, 20);
		spnY.addChangeListener(list);
		add(spnY);
		
		JLabel lblWidth = new JLabel("Width:");
		lblWidth.setBounds(0, 63, 32, 14);
		add(lblWidth);
		
		spnWidth = new JSpinner();
		spnWidth.setModel(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
		spnWidth.addChangeListener(list);
		spnWidth.setBounds(55, 61, 124, 20);
		add(spnWidth);
		
		JLabel lblHeight = new JLabel("Height:");
		lblHeight.setBounds(0, 92, 35, 14);
		add(lblHeight);
		
		spnHeight = new JSpinner();
		spnHeight.setModel(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
		spnHeight.addChangeListener(list);
		spnHeight.setBounds(55, 89, 124, 20);
		add(spnHeight);
		
		
		
		
		setRect(new RectF());
	}
	
	/**
	* Set the indicated rectangle; it clone the parameter
	* @param r Rectangle to be set; if null it will be initialized to the constructor values
	*/
	public void setRect(RectF r) {
		if(r == null) {
			rec = new RectF();
		} else {
			rec = new RectF(r);
		}
		
		setting = true;
		
		
		spnX.setValue(Math.round(rec.left));
		spnY.setValue(Math.round(rec.top));
		spnWidth.setValue(Math.round(rec.width()));
		spnHeight.setValue(Math.round(rec.height()));
		
		setting = false;
	}
	
	/**
	 * @return Rectangle set, not editable
	 */
	public @NonNull RectF getRect() {
		return new RectF(rec);
	}
	
	/**
	 * Fires the event for a rectangle change
	 */
	private void generateChangeList() {
		ChangeEvent e = new ChangeEvent(this);
		for(ChangeListener c : changeListeners) {
			c.stateChanged(e);
		}
	}
	
	/**
	 * @param l Listener to add
	 */
	public void addChangeListener(@NonNull ChangeListener l) {
		changeListeners.add(l);
	}
	
	
	/** Class for the listener of change of the various components of the rectangle */
	private class ChangeLis implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent e) {
			// I avoid updating if you are simply performing the setting
			if(!setting) {		
				float x = (int)spnX.getValue();
				float y = (int)spnY.getValue();
				float w = (int)spnWidth.getValue();
				float h = (int)spnHeight.getValue();
				
				rec.left = x;
				rec.top = y;
				rec.right = x + w;
				rec.bottom = y + h;
			}
			
			generateChangeList();
		}
		
	}

}
