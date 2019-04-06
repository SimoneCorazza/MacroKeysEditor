package com.macrokeyseditor.components;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Window;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.eclipse.jdt.annotation.NonNull;

import com.macrokeys.KeyShape;
import com.macrokeys.MacroKey;
import com.macrokeys.MacroKeyType;
import com.macrokeys.MacroScreen;
import com.macrokeys.MacroSetup;
import com.macrokeyseditor.MacroScreenEditor;
import com.macrokeyseditor.MacroScreenEditorListener;
import com.macrokeyseditor.MacroSetupEditor;
import com.macrokeyseditor.MacroSetupEditorListener;
import com.macrokeyseditor.components.ColorComponent;
import com.macrokeyseditor.components.RectangleComponent;
import com.macrokeyseditor.components.SelectionBoxesComponent;
import com.macrokeyseditor.windows.DialogKeyChooser;


import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

/** Component for showing and editing the properties of a {@link MacroKey} */
public final class MKPropetyComponent extends JPanel {
	
	/** 
	 * Flag to indicate that the macro is being set; to indicate the futility of running events
	 */
	private boolean setting = false;
	
	
	private final ColorComponent clrEdge;
	private final ColorComponent clrFill;
	private final ColorComponent clrEdgePress;
	private final ColorComponent clrFillPress;
	private final JButton btnDarkerEdge;
	private final JButton btnDarkerFill;
	private final SelectionBoxesComponent<KeyShape.Type> cmbShape;
	private final JTextField txtField;
	private final SelectionBoxesComponent<MacroKeyType> cmbType;
	private final RectangleComponent rctArea;
	private final JButton btnKeys;
	
	
	/** Currently selected keys; null if none */
	private final List<MacroKey> macroKeys = new ArrayList<>();
	
	private final MacroSetupEditor setupEditor;
	
	private final MacroScreenEditorListener editList = 
			new MacroScreenEditorListener() {
		
		@Override
		public void selectionChange(List<MacroKey> actual) {
			updateMacroKey(actual);
		}
		
		@Override
		public void macroScreenEdited(@NonNull MacroScreen m) {
			// Nothing
		}
		
		@Override
		public void macroKeyRemoved(@NonNull MacroScreen ms,
				@NonNull List<MacroKey> mk) {
			// Nothing
		}
		
		@Override
		public void macroKeyEdited(@NonNull MacroScreen ms,
				@NonNull List<MacroKey> mk, @NonNull String property) {
			if(atLeastOneInCommon(mk, macroKeys)) {
				// Copy the current selection list, if you directly use macroKeys there
				// can be concurrency problems (macroKeys is modified)
				List<MacroKey> copy = new ArrayList<>(macroKeys);
				// Aggiorno l'UI
				updateMacroKey(copy);
			}
		}
		
		@Override
		public void macroKeyAdded(@NonNull MacroScreen ms,
				@NonNull List<MacroKey> mk) {
			// Nothing
		}

		@Override
		public void swapMacroKeys(@NonNull MacroKey a, @NonNull MacroKey b) {
			// Nothing
		}
	};

	
	/**
	* Indicates whether the two lists have at least one element in common.
	* @param l First list
	* @param The second list
	* @return True if there is at least one element in common, False otherwise
	*/
	private static boolean atLeastOneInCommon(@NonNull List<MacroKey> l,
			@NonNull List<MacroKey> ll) {
		assert l != null && ll != null;
		
		for(MacroKey m : ll) {
			for(MacroKey mm : l) {
				if(m == mm) {
					return true;
				}
			}
		}
		return false;
	}
	
	
	
	/**
	 * @param setupEditor Editor for the {@link MacroSetup}
	 */
	public MKPropetyComponent(@NonNull MacroSetupEditor setupEditor) {
		setLayout(null);
		
		this.setupEditor = setupEditor;
		
		setupEditor.addActionListener(new MacroSetupEditorListener() {
			
			@Override
			public void selectionChange(MacroScreenEditor old,
					MacroScreenEditor actual) {
				if(old != null) {
					old.removeEditEventListener(editList);
				}
				
				if(actual == null) {
					updateMacroKey(null);
				} else {
					actual.addEditEventListener(editList);
				}
			}
			
			@Override
			public void actionPerformed(Action a,
					@NonNull MacroScreenEditor e) {
				
			}
		});
		
		// Handles the component's events with event {@link ActionListener}
		final MyActionListener actListener = new MyActionListener();
		
		// Handles the component's events with event {@link DocumentListener}
		final MyDocumentListener docListener = new MyDocumentListener();
		
		// Handles the component's events with event {@link ChangeListener}
		final MyChangeListener changeListener = new MyChangeListener();
		
		// Handles the component's events with event
		final MyItemListener itemListener = new MyItemListener();
		
		JLabel lblNewLabel = new JLabel("Area");
		lblNewLabel.setBounds(101, 11, 23, 14);
		add(lblNewLabel);
		
		JLabel lblShape = new JLabel("Shape:");
		lblShape.setBounds(10, 176, 34, 14);
		add(lblShape);
		
		cmbShape = new SelectionBoxesComponent<>();
		cmbShape.setBounds(103, 173, 283, 20);
		cmbShape.addItemListener(itemListener);
		cmbShape.setItems(KeyShape.Type.values());
		add(cmbShape);
		
		JLabel lblColorEdge = new JLabel("Color edge:");
		lblColorEdge.setBounds(10, 215, 56, 14);
		add(lblColorEdge);
		
		JLabel lblColorFill = new JLabel("Color fill:");
		lblColorFill.setBounds(10, 269, 42, 14);
		add(lblColorFill);
		
		JLabel lblColorEdgePress = new JLabel("Color edge press:");
		lblColorEdgePress.setBounds(10, 307, 85, 14);
		add(lblColorEdgePress);
		
		JLabel lblColorFillPress = new JLabel("Color fill press:");
		lblColorFillPress.setBounds(10, 355, 71, 14);
		add(lblColorFillPress);
		
		setPreferredSize(new Dimension(397, 529));
		setMinimumSize(new Dimension(200, 50));
		
		clrEdge = new ColorComponent();
		clrEdge.setBounds(103, 204, 206, 37);
		clrEdge.addActionListener(actListener);
		add(clrEdge);
		
		clrFill = new ColorComponent();
		clrFill.setBounds(103, 255, 206, 37);
		clrFill.addActionListener(actListener);
		add(clrFill);
		
		clrEdgePress = new ColorComponent();
		clrEdgePress.setBounds(105, 297, 199, 37);
		clrEdgePress.addActionListener(actListener);
		add(clrEdgePress);
		
		clrFillPress = new ColorComponent();
		clrFillPress.setBounds(103, 345, 201, 37);
		clrFillPress.addActionListener(actListener);
		add(clrFillPress);
		
		btnDarkerEdge = new JButton("Darker");
		btnDarkerEdge.setBounds(321, 303, 65, 23);
		add(btnDarkerEdge);
		
		btnDarkerFill = new JButton("Darker");
		btnDarkerFill.setBounds(321, 351, 65, 23);
		add(btnDarkerFill);
		
		JLabel lblText = new JLabel("Text:");
		lblText.setBounds(10, 442, 31, 14);
		add(lblText);
		
		txtField = new JTextField();
		txtField.setBounds(103, 439, 206, 20);
		txtField.getDocument().addDocumentListener(docListener);
		add(txtField);
		txtField.setColumns(10);
		
		rctArea = new RectangleComponent();
		rctArea.setBounds(10, 45, 195, 120);
		rctArea.addChangeListener(changeListener);
		add(rctArea);
		
		btnKeys = new JButton("");
		btnKeys.setBounds(101, 481, 208, 23);
		btnKeys.addActionListener(actListener);
		add(btnKeys);
		
		JLabel lblKeys = new JLabel("Key macro:");
		lblKeys.setBounds(10, 490, 71, 14);
		add(lblKeys);
		
		cmbType = new SelectionBoxesComponent<>();
		cmbType.setBounds(101, 397, 285, 20);
		cmbType.setItems(MacroKeyType.values());
		cmbType.addItemListener(itemListener);
		add(cmbType);
		
		JLabel lblType = new JLabel("Type:");
		lblType.setBounds(10, 400, 46, 14);
		add(lblType);
		
		// Initially the macro is not shown -> control hidden
		setVisible(false);
	}
	
	
	
	
	/**
	* Update the values of the actual processed {@link MacroKey} 
	* @param l List whose properties to show; null if none
	*/
	private void updateMacroKey(List<MacroKey> l) {
		boolean b = l != null && !l.isEmpty();
		if(b) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					// Ignore edit events
		            setting = true;
		            
		            // Only the first's properties matter
		            MacroKey m = l.get(0);
		            
		            rctArea.setRect(m.getArea());
		            
		            cmbShape.setSelectedItem(m.getShape().getType());
		            cmbType.setSelectedItem(m.getType());
		            
		            clrEdge.setColor(m.getColorEdge());
		            clrFill.setColor(m.getColorFill());
		            clrEdgePress.setColor(m.getColorEdgePress());
		            clrFillPress.setColor(m.getColorFillPress());
		            
		            txtField.setText(m.getText());
		            
		            btnKeys.setText(m.getKeySeq().toString());
		            
		            // Ignore edit events
		            setting = false;
				}
			});
			
			
        }
        
		// Update the field of the keys actually selected
        this.macroKeys.clear();
        if(l != null) {
        	this.macroKeys.addAll(l);
        }
        
        this.setVisible(b);
	}
	
	/**
	* Change the indicated property
	* @param property Name of the property; see {@link MacroScreenEditor}
	* @param val New value of the property
	*/
	private void editProperty(@NonNull String property, Object val) {
		MacroScreenEditor e = setupEditor.getMacroScreenEditorSelected();
		assert e != null : "Must not be called when no screen selected";
		
		e.editMacroKeyProperty(macroKeys, property, val);
	}
	
	
	
	
	
	
	//------------------------------
	// Classes for the various types of listeners for the controls of this component
	//------------------------------
	
	private class MyActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if(!setting && !macroKeys.isEmpty()) {
				Object s = e.getSource();
				String a = e.getActionCommand();
				
				if(s == clrEdge && a.equals(ColorComponent.ACTION_COLOR_CHANG)) {
					editProperty(MacroScreenEditor.MACRO_KEY_PROPETY_COLOR_EDGE, 
							clrEdge.getColor());
				} else if(s == clrFill && a.equals(ColorComponent.ACTION_COLOR_CHANG)) {
					editProperty(MacroScreenEditor.MACRO_KEY_PROPETY_COLOR_FILL, 
							clrFill.getColor());
				} else if(s == clrEdgePress && a.equals(ColorComponent.ACTION_COLOR_CHANG)) {
					editProperty(MacroScreenEditor.MACRO_KEY_PROPETY_COLOR_EDGE_PRESS, 
							clrEdgePress.getColor());
				} else if(s == clrFillPress && a.equals(ColorComponent.ACTION_COLOR_CHANG)) {
					editProperty(MacroScreenEditor.MACRO_KEY_PROPETY_COLOR_FILL_PRESS, 
							clrFillPress.getColor());
				} else if(s == btnKeys) {
					Window w = SwingUtilities.getWindowAncestor(MKPropetyComponent.this);
					MacroKey m = macroKeys.get(0);
					DialogKeyChooser d = new DialogKeyChooser(w, m.getKeySeq());
					if(d.showDialog()) {
						editProperty(MacroScreenEditor.MACRO_KEY_PROPETY_KEYSEQ, 
								d.getKeySequence());
						btnKeys.setText(m.getKeySeq().toString());
					}
				}
			}
		}
		
	}
	
	private class MyDocumentListener implements DocumentListener {

		@Override
		public void changedUpdate(DocumentEvent e) {
			if(!setting && !macroKeys.isEmpty()) {
				onChanged(e);
			}
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			if(!setting && !macroKeys.isEmpty()) {
				onChanged(e);
			}
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			if(!setting && !macroKeys.isEmpty()) {
				onChanged(e);
			}
		}
		
		private void onChanged(DocumentEvent e) {
			if(txtField.getDocument() == e.getDocument()) {
				editProperty(MacroScreenEditor.MACRO_KEY_PROPETY_TEXT, 
						txtField.getText());
			}
		}
	}
	
	private class MyChangeListener implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent e) {
			if(!setting && !macroKeys.isEmpty()) {
				Object s = e.getSource();
				
				if(s == rctArea) {
					editProperty(MacroScreenEditor.MACRO_KEY_PROPETY_AREA, 
							rctArea.getRect());
				}
			}
		}
	}
	
	private class MyItemListener implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent e) {
			// I do not consider the deselection event
			if(!setting && !macroKeys.isEmpty() && e.getStateChange() == ItemEvent.SELECTED) {
				Object s = e.getSource();
				Object it = e.getItem();
				
				if(s == cmbShape) {
					KeyShape.Type sh = (KeyShape.Type)it;
					editProperty(MacroScreenEditor.MACRO_KEY_PROPETY_SHAPE, 
							new KeyShape(sh));
				} else if(s == cmbType) {
					MacroKeyType kt = (MacroKeyType)it;
					editProperty(MacroScreenEditor.MACRO_KEY_PROPETY_TYPE, 
							kt);
				}
			}
		}
		
	}
}
