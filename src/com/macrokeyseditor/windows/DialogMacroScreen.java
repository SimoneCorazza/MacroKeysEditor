package com.macrokeyseditor.windows;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.eclipse.jdt.annotation.NonNull;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;

import com.macrokeys.MacroScreen;
import com.macrokeyseditor.MacroScreenEditor;
import com.macrokeyseditor.components.ColorComponent;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;

/** Form per la creazione di un {@link MacroScreen} */
public class DialogMacroScreen extends JDialog {
	
	
	private JTextField txtBackgroundText;
	private JComboBox<MacroScreen.SwipeType> cmbSwipe;
	private JComboBox<MacroScreen.Orientation> cmbOrientation;
	private ColorComponent clrBackround;
	private JButton btnOk;
	private JButton btnExit;
	
	/** Indica in che modalit� si �: modifica (true) o creazione (false) di una {@link MacroScreen} */
	private final boolean modify;
	/**
	 * Editor tramite il quele modfiicare una {@link MacroScreen} già esistente;
	 * null sse {@code #modify} è false
	 */
	private final MacroScreenEditor macroScreenEditor; 
	
	/** Macro screen creata o da modificare */
	private MacroScreen macroScreen;
	/** Stato attuale della conferma di questa dialog */
	private boolean conferm;
	

	/**
	 * Crea una nuova {@link MacroScreen}
	 * @param parent Finestra padre
	 * @wbp.parser.constructor
	 */
	public DialogMacroScreen(Window parent) {
		super(parent, ModalityType.DOCUMENT_MODAL);
		
		init();
		modify = false;
		macroScreen = new MacroScreen();
		updateComponenets(); //Setto i valori dei controlli
		macroScreenEditor = null;
		conferm = false;
	}
	
	/**
	 * Modifica una {@link MacroScreen} esistente o ne crea una nuova
	 * @param parent Finestra padre
	 * @param s Editor tramite il quele modificare la {@link MacroScreen};
	 * null per crearne una nuova
	 */
	public DialogMacroScreen(Window parent, MacroScreenEditor s) {
		super(parent, ModalityType.DOCUMENT_MODAL);
		
		init();
		modify = s != null;
		if(modify) {
			btnOk.setVisible(false);
			btnExit.setText("Close");
			macroScreenEditor = s;
			macroScreen = macroScreenEditor.getMacroScreen();
		} else {
			macroScreen = new MacroScreen();
			macroScreenEditor = null;
		}
		updateComponenets(); //Setto i valori dei controlli
		conferm = true; //Conferma sempre
	}
	
	
	
	
	/**
	 * Inizializza il componente
	 */
	private void init() {
		final MyItemListener itemList = new MyItemListener();
		final MyActionList actionList = new MyActionList();
		final MyDocumentListener docList = new MyDocumentListener();
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("Screen creation");
		setBounds(100, 100, 450, 259);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		cmbSwipe = new JComboBox<>();
		cmbSwipe.setBounds(103, 22, 298, 20);
		cmbSwipe.setModel(new DefaultComboBoxModel<>(MacroScreen.SwipeType.values()));
		cmbSwipe.addItemListener(itemList);
		contentPane.add(cmbSwipe);
		
		JLabel lblPosition = new JLabel("Swipe call:");
		lblPosition.setBounds(10, 28, 83, 14);
		contentPane.add(lblPosition);
		
		JLabel lblOrientation = new JLabel("Orientation:");
		lblOrientation.setBounds(10, 59, 73, 14);
		contentPane.add(lblOrientation);
		
		cmbOrientation = new JComboBox<>();
		cmbOrientation.setBounds(103, 53, 298, 20);
		cmbOrientation.setModel(new DefaultComboBoxModel<>(MacroScreen.Orientation.values()));
		cmbOrientation.addItemListener(itemList);
		contentPane.add(cmbOrientation);
		
		JLabel lblBackgroundColor = new JLabel("Background color:");
		lblBackgroundColor.setBounds(10, 103, 89, 14);
		contentPane.add(lblBackgroundColor);
		
		clrBackround = new ColorComponent();
		clrBackround.setBounds(103, 94, 298, 30);
		clrBackround.addActionListener(actionList);
		contentPane.add(clrBackround);
		
		JLabel lblBackgroundText = new JLabel("Background text:");
		lblBackgroundText.setBounds(10, 153, 101, 14);
		contentPane.add(lblBackgroundText);
		
		txtBackgroundText = new JTextField();
		txtBackgroundText.setBounds(103, 147, 298, 20);
		txtBackgroundText.getDocument().addDocumentListener(docList);
		txtBackgroundText.setColumns(10);
		contentPane.add(txtBackgroundText);
		
		btnExit = new JButton("Cancel");
		btnExit.setBounds(335, 188, 89, 23);
		btnExit.addActionListener(actionList);
		contentPane.add(btnExit);
		
		btnOk = new JButton("OK");
		btnOk.setBounds(236, 188, 89, 23);
		btnOk.addActionListener(actionList);
		contentPane.add(btnOk);
		
		JRootPane rootPane = SwingUtilities.getRootPane(btnOk); 
		rootPane.setDefaultButton(btnOk);
		
		setLocationRelativeTo(getParent());
	}
	
	/** Visualizza l'atteuale {@link MacroScreen} mediante i componenti */
	private void updateComponenets() {
		cmbOrientation.setSelectedItem(macroScreen.getOrientation());
		cmbSwipe.setSelectedItem(macroScreen.getSwipeType());
		clrBackround.setColor(macroScreen.getBackgroundColor());
		txtBackgroundText.setText(macroScreen.getBackgroundText());
	}
	
	/**
	 * Mostra la finestra
	 * @return True l'utente conferma l'operazione
	 */
	public boolean showDialog() {
		super.setVisible(true);
		return conferm;
	}
	
	@Override
	public void setVisible(boolean b) {
		//Inutilizzabile dall'utente
	}
	
	/**
	 * @return {@link MacroScreen} lavorata
	 */
	public @NonNull MacroScreen getMacroScreen() {
		return macroScreen;
	}
	
	//------------------------------
	// Classi per i callback
	//------------------------------
	
	
	private class MyItemListener implements ItemListener {
		@Override
		public void itemStateChanged(ItemEvent e) {
			Object s = e.getSource();
			Object it = e.getItem();
			
			if(s == cmbOrientation) {
				MacroScreen.Orientation o = (MacroScreen.Orientation)it;
				if(modify) {
					macroScreenEditor.editMacroScreenPropety(
							MacroScreenEditor.MACRO_SCREEN_PROPETY_ORIENTATION,
							o);
				} else {
					macroScreen.setOrientation(o);
				}
			} else if(s == cmbSwipe) {
				MacroScreen.SwipeType t = (MacroScreen.SwipeType)it;
				if(modify) {
					macroScreenEditor.editMacroScreenPropety(
							MacroScreenEditor.MACRO_SCREEN_PROPETY_SWIPE_TYPE,
							t);
				} else {
					macroScreen.setSwipeType(t);					
				}
			}
		}
	}
	
	private class MyActionList implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			Object s = e.getSource();
			
			if(s == btnExit) {
				if(!modify) { // Segnalo che l'operazione di creazione � stata annullata
					conferm = false;
				}
				dispatchEvent(new WindowEvent(DialogMacroScreen.this, WindowEvent.WINDOW_CLOSING));
			} else if(s == btnOk) {
				conferm = true;
				dispatchEvent(new WindowEvent(DialogMacroScreen.this, WindowEvent.WINDOW_CLOSING));
			} else if(s == clrBackround) {
				int color = clrBackround.getColor();
				if(modify) {
					macroScreenEditor.editMacroScreenPropety(
							MacroScreenEditor.MACRO_SCREEN_PROPETY_COLOR,
							color);
				} else {
					macroScreen.setBackgroundColor(color);
				}
			}
		}
	}
	
	private class MyDocumentListener implements DocumentListener {

		@Override
		public void changedUpdate(DocumentEvent e) {
			onChange();
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			onChange();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			onChange();
		}
		
		private void onChange() {
			String text = txtBackgroundText.getText();
			if(modify) {
				macroScreenEditor.editMacroScreenPropety(
						MacroScreenEditor.MACRO_SCREEN_PROPETY_TEXT,
						text);
			} else {
				macroScreen.setBackgroundText(text);				
			}
		}
	}
}
