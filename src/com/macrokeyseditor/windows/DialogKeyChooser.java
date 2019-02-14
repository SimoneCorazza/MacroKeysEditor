package com.macrokeyseditor.windows;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.eclipse.jdt.annotation.NonNull;

import com.macrokeys.LimitedKeySequence;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.awt.Dimension;

/** Finestra per prendere i vari tasti */
public class DialogKeyChooser extends JDialog {

	/** Tempo di attesa dal primo input prima di prendere la combinazione di tasti, in secondi */
	//private static final float KEY_ELAPSED_SEC = 1.0f;
	/** Tempo di attesa dal primo input prima di prendere la combinazione di tasti, in millisecondi */
	//private static final int KEY_ELAPSED_MS = (int)(KEY_ELAPSED_SEC * 1000.0f);
	
	private JButton btnOk;
	private JButton btnDeleteLast;
	private JLabel lblKeySequence;
	
	
	/** Lista della sequenza di tasti; di lunghezza massima pari a {@link LimitedKeySequence#MAX_KEYS} */
	private final List<Integer> keyList = new ArrayList<>();
	/** Oggetto da restituire */
	private LimitedKeySequence keySequence;

	
	/**
	 * @param parent Finestra padre
	 * @param seq Sequenza iniziale pu� essere null
	 */
	public DialogKeyChooser(Window parent, LimitedKeySequence seq) {
		super(parent, ModalityType.DOCUMENT_MODAL);
		init();
		
		if(seq != null && seq.getKeys() != null) {
			for(int e : seq.getKeys()) {
				keyList.add(e);
			}
		}
		updateLabelKeys();
	}
	
	private void init() {
		setBounds(100, 100, 450, 300);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		
		JPanel pnlDown = new JPanel();
		pnlDown.setMinimumSize(new Dimension(10, 40));
		getContentPane().add(pnlDown, BorderLayout.SOUTH);
		pnlDown.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		btnOk = new JButton("OK");
		btnOk.setFocusable(false);
		btnOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispatchEvent(new WindowEvent(DialogKeyChooser.this, WindowEvent.WINDOW_CLOSING));
			}
		});
		pnlDown.add(btnOk);
		
		btnDeleteLast = new JButton("Delete last key");
		btnDeleteLast.setFocusable(false);
		btnDeleteLast.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(keyList.size() > 0) {
					keyList.remove(keyList.size() - 1);
					updateLabelKeys();
				}
			}
		});
		pnlDown.add(btnDeleteLast);
		
		JPanel pnlUp = new JPanel();
		getContentPane().add(pnlUp, BorderLayout.CENTER);
		pnlUp.setLayout(new BorderLayout(0, 0));
		
		String text = "<html>" + 
				"Press the key sequence (order may be important).<br>" +
				"Maximum number of keys: " + LimitedKeySequence.MAX_KEYS +
				"</html>";
		JLabel lblpressTheKey = new JLabel(text);
		lblpressTheKey.setForeground(UIManager.getColor("ToolBar.dockingForeground"));
		lblpressTheKey.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblpressTheKey.setHorizontalAlignment(SwingConstants.CENTER);
		pnlUp.add(lblpressTheKey, BorderLayout.CENTER);
		
		lblKeySequence = new JLabel("");
		lblKeySequence.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblKeySequence.setHorizontalAlignment(SwingConstants.CENTER);
		pnlUp.add(lblKeySequence, BorderLayout.SOUTH);
		
		//Per funzionare non ci devono essere componenti focusabili
		addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				
			}
			@Override
			public void keyReleased(KeyEvent e) {
				if(keyList.size() < LimitedKeySequence.MAX_KEYS) {
					keyList.add(e.getKeyCode());
					updateLabelKeys();
				}
			}
			@Override
			public void keyPressed(KeyEvent e) {
			}
		});
		
		setLocationRelativeTo(getParent());
	}

	/**
	 * Aggiorna la {@link lblKeySequence}
	 */
	private void updateLabelKeys() {
		String text;
		if(keyList.size() == 0) {
			text = "";
		} else {
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < keyList.size() - 1; i++) {
				String k = KeyEvent.getKeyText(keyList.get(i));
				sb.append(k);
				sb.append("+");
			}
			sb.append(KeyEvent.getKeyText(keyList.get(keyList.size() - 1)));
			text = sb.toString();
		}
		
		lblKeySequence.setText(text);
	}
	
	
	/**
	 * Mostra la finestra
	 * @return True l'utente conferma l'operazione
	 */
	public boolean showDialog() {
		super.setVisible(true);
		Integer[] a = new Integer[keyList.size()];
		keyList.toArray(a);
		keySequence = new LimitedKeySequence(a);
		return true;
	}
	
	private static @NonNull int[] toArray(@NonNull List<Integer> l) {
		int[] a = new int[l.size()];
		int i = 0;
		for(Integer n : l) {
			a[i] = n;
			i++;
		}
		return a;
	}
	
	@Override
	public void setVisible(boolean b) {
		//Inutilizzabile dall'utente
	}




	/**
	 * @return Sequenza di tasti premuta
	 */
	public @NonNull LimitedKeySequence getKeySequence() {
		return keySequence;
	}
}
