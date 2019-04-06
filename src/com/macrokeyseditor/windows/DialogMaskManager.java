package com.macrokeyseditor.windows;

import javax.swing.JDialog;

import com.macrokeyseditor.Mask;
import com.macrokeyseditor.MasksManager;
import com.macrokeyseditor.MasksManager.MasksManagerListener;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.Dimension;

/**
 * Dialog for managing the {@link Mask}
 */
public class DialogMaskManager extends JDialog {
	
	
	private final MasksManager manager;
	private final ManagerListImpl list = new ManagerListImpl();
	
	
	
	private JList<Mask> lstMask;
	private final DefaultListModel<Mask> masksModel = new DefaultListModel<>();
	private JButton btnAdd;
	private JButton btnEdit;
	private JButton btnRemove;
	
	
	
	/**
	 * @param manager Manager of {@link Mask}
	 * @param parent Parent window
	 */
	public DialogMaskManager(MasksManager manager, Window parent) {
		super(parent, ModalityType.DOCUMENT_MODAL);
		
		this.manager = manager;
		
		manager.addListener(list);
		
		// Listener for the closure of this window
		this.addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent e) {
				// Nothing
			}
			
			@Override
			public void windowIconified(WindowEvent e) {
				// Nothing
			}
			
			@Override
			public void windowDeiconified(WindowEvent e) {
				// Nothing
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) {
				// Nothing
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				// Nothing
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				// Remove the listener
				manager.removeListener(list);
			}
			
			@Override
			public void windowActivated(WindowEvent e) {
				// Nothing
			}
		});
		
		init();
	}
	
	
	
	private void init() {
		setSize(400, 300);
		setTitle("Masks manager");
		setResizable(false);
		
		getContentPane().setLayout(new BorderLayout(0, 0));
		JPanel pnlRight = new JPanel();
		pnlRight.setLayout(null);
		getContentPane().add(pnlRight, BorderLayout.LINE_END);
		setLocationRelativeTo(null);
		
		lstMask = new JList<>();
		lstMask.setModel(masksModel);
		lstMask.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		insertInList();
		getContentPane().add(lstMask, BorderLayout.CENTER);
		
		btnAdd = new JButton("Add");
		btnAdd.setLocation(0, 0);
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DialogMaskEdit dm = new DialogMaskEdit(manager,
						DialogMaskManager.this);
				dm.setVisible(true);
			}
		});
		
		btnAdd.setSize(71, 23);
		pnlRight.add(btnAdd);
		
		btnEdit = new JButton("Edit");
		btnEdit.setLocation(0, 23);
		btnEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Mask selected = lstMask.getSelectedValue();
				if(selected != null) {
					DialogMaskEdit dm = new DialogMaskEdit(manager,
							selected,
							DialogMaskManager.this);
					dm.setVisible(true);
				}
			}
		});
		btnEdit.setSize(71, 23);
		pnlRight.add(btnEdit);
		
		btnRemove = new JButton("Remove");
		btnRemove.setLocation(0, 57);
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Mask selected = lstMask.getSelectedValue();
				if(selected != null) {
					manager.removeMask(selected);
				}
			}
		});
		btnRemove.setSize(71, 23);
		pnlRight.add(btnRemove);
		
		// The width of the panel varies in base of the size of the buttons
		pnlRight.setPreferredSize(new Dimension(btnRemove.getWidth(), 100));
	}
	
	
	/**
	 * Insert in {@link #lstMask} all and only the {@link Mask}
	 * contained in {@link MasksManager}
	 */
	private void insertInList() {
		masksModel.removeAllElements();
		for(Mask m : manager.getMasks()) {
			masksModel.addElement(m);
		}
	}
	
	
	
	
	
	private class ManagerListImpl implements MasksManagerListener {

		@Override
		public void maskAdded(Mask m) {
			insertInList();
		}

		@Override
		public void maskRemove(Mask ignored) {
			insertInList();
		}

		@Override
		public void maskEdited(Mask mask, String property, Object value) {
			insertInList();
		}

		@Override
		public void maskSelectedChanged(Mask m) {
			// Nothing
		}
	};
}
