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

/** Componente per la visualizzazione e modifica delle proprietà */
public final class MKPropetyComponent extends JPanel {
	
	/** Flag per segnalare che si sta eseguendo il settaggio della macro;
	 *  per indicare l'inutilità di eseguire gli eventi
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
	
	
	/** Tasti attualmente selezionati; null se nessuno */
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
			// Niente
		}
		
		@Override
		public void macroKeyRemoved(@NonNull MacroScreen ms,
				@NonNull List<MacroKey> mk) {
			// Niente
		}
		
		@Override
		public void macroKeyEdited(@NonNull MacroScreen ms,
				@NonNull List<MacroKey> mk, @NonNull String property) {
			if(atLeastOneInCommon(mk, macroKeys)) {
				// Copio la lista della selezione attuale, se si usasse direttamente
				// macroKeys possono esserci problemi di concorrenza (macroKeys viene modificata)
				List<MacroKey> copy = new ArrayList<>(macroKeys);
				// Aggiorno l'UI
				updateMacroKey(copy);
			}
		}
		
		@Override
		public void macroKeyAdded(@NonNull MacroScreen ms,
				@NonNull List<MacroKey> mk) {
			// Niente
		}

		@Override
		public void swapMacroKeys(@NonNull MacroKey a, @NonNull MacroKey b) {
			// Niente
		}
	};

	
	/**
	 * Indica se le due liste hanno almeno un'elemento in comune.
	 * <p>Se le liste sono vuote ritorna false</p>
	 * @param l Prima lista
	 * @param ll Seconda lista
	 * @return True se c'è almeno un elemento in comune, False altrimenti
	 */
	private static boolean atLeastOneInCommon(List<MacroKey> l,
			List<MacroKey> ll) {
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
	 * @param setupEditor Editor per la {@link MacroSetup}
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
		
		// Gestisce gli eventi dei componenti con evento {@link ActionListener}
		final MyActionListener actListener = new MyActionListener();
		// Gestisce gli eventi dei componenti con evento {@link DocumentListener}
		final MyDocumentListener docListener = new MyDocumentListener();
		// Gestisce gli eventi dei componenti con evento {@link ChangeListener}
		final MyChangeListener changeListener = new MyChangeListener();
		// Gestisce gli eventi generati dai componenti combobox
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
		
		//Inizialmente non visualizzo nessuna macro -> nascondo il controllo
		setVisible(false);
	}
	
	
	
	
	/**
	 * Aggiorna i valori dei {@link MacroKey} attualemte elaborati
	 * @param l Lista di cui mostrare le proprietà; null se nessuno
	 */
	private void updateMacroKey(List<MacroKey> l) {
		boolean b = l != null && !l.isEmpty();
		if(b) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					// Ignoro gli eventi di modifica
		            setting = true;
		            
		            // Considero le proprietà del primo elemento
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
		            
		            // Ignoro gli eventi di modifica
		            setting = false;
				}
			});
			
			
        }
        
		// Aggiorno il campo dei tasti attualmente selezionati
        this.macroKeys.clear();
        if(l != null) {
        	this.macroKeys.addAll(l);
        }
        
        this.setVisible(b);
	}
	
	/**
	 * Modifica la proprietà indicata
	 * @param property Nome della properitaà; vedi {@link MacroScreenEditor}
	 * @param val Nuovo valore della propietà
	 */
	private void editProperty(@NonNull String property, Object val) {
		MacroScreenEditor e = setupEditor.getMacroScreenEditorSelected();
		assert e != null : "Must not be called when no screen selected";
		
		e.editMacroKeyProperty(macroKeys, property, val);
	}
	
	
	
	
	
	
	//------------------------------
	//Classi per le varie tipologie di listener per controlli di questo componente
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
			//Non considero l'evento di deselezione
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
