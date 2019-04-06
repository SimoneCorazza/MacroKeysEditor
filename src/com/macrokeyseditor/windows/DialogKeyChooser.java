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

/** WIndow to take the keys from the user */
public class DialogKeyChooser extends JDialog {

	
	private JButton btnOk;
	private JButton btnDeleteLast;
	private JLabel lblKeySequence;
	
	
	/** Sequence of the keys; of maximum lenth of {@link LimitedKeySequence#MAX_KEYS} */
	private final List<Integer> keyList = new ArrayList<>();
	
	/** Object to return */
	private LimitedKeySequence keySequence;

	
	/**
	 * @param parent Parent window
	 * @param seq Initial sequence (if edit is needed) null to create a new one
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
		
		// To make this work no component has to be focusable
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
	 * Update the {@link lblKeySequence}
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
	 * Show the window
	 * @return True if the user confirm the operation
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
		// Unusable from the user
	}




	/**
	 * @return Sequence key pressed
	 */
	public @NonNull LimitedKeySequence getKeySequence() {
		return keySequence;
	}
}
