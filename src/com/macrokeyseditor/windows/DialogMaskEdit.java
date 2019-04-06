package com.macrokeyseditor.windows;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.macrokeyseditor.Mask;
import com.macrokeyseditor.MasksManager;
import com.macrokeyseditor.util.Size;

import javax.swing.JButton;

/**
 * Dialog for the creation of a {@link Mask}.
 * Handles directly the editng and insertion of {@link Mask}
 * by the {@link MasksManager}.
 */
public class DialogMaskEdit extends JDialog {
	private JTextField txtDiagonal;
	private JTextField txtResolutionX;
	private JTextField txtResolutionY;
	private JTextField txtName;
	
	/** Mask to edit */
	private final Mask mask;
	private final MasksManager manager;
	/**
	 * True in case of editing, False in case of adding a {@link Mask}
	 */
	private final boolean editMode;
	
	
	
	/**
	 * @param manager Mask manager 
	 * @param m Mask to edit
	 * @param parent Parent window
	 */
	public DialogMaskEdit(MasksManager manager, Mask m, Window parent) {
		super(parent, ModalityType.DOCUMENT_MODAL);
		
		this.mask = m;
		this.manager = manager;
		this.editMode = true;
		
		init();
		
		txtName.setText(m.getName());
		txtDiagonal.setText(m.getDiagonal() + "");
		txtResolutionX.setText(m.getResolution().width + "");
		txtResolutionY.setText(m.getResolution().height + "");
	}
	
	/**
	 * @param managerMask manager 
	 * @param parent Parent window
	 * @wbp.parser.constructor
	 */
	public DialogMaskEdit(MasksManager manager, Window parent) {
		super(parent, ModalityType.DOCUMENT_MODAL);
		
		this.mask = new Mask();
		this.manager = manager;
		this.editMode = false;
		
		init();
	}
	
	
	private void init() {
		getContentPane().setLayout(null);
		setTitle("Set/Edit mask");
		setSize(451, 180);
		setLocationRelativeTo(null);
		setResizable(false);
		
		
		JLabel lblDiagonal = new JLabel("Diagonal (inches):");
		lblDiagonal.setBounds(10, 42, 90, 14);
		getContentPane().add(lblDiagonal);
		
		txtDiagonal = new JTextField();
		txtDiagonal.setColumns(10);
		txtDiagonal.setBounds(113, 39, 51, 20);
		getContentPane().add(txtDiagonal);
		
		JLabel label_1 = new JLabel("Resolution (pixels):");
		label_1.setBounds(10, 73, 100, 14);
		getContentPane().add(label_1);
		
		txtResolutionX = new JTextField();
		txtResolutionX.setColumns(10);
		txtResolutionX.setBounds(114, 70, 51, 20);
		getContentPane().add(txtResolutionX);
		
		JLabel lblX = new JLabel("x");
		lblX.setBounds(172, 73, 11, 14);
		getContentPane().add(lblX);
		
		txtResolutionY = new JTextField();
		txtResolutionY.setColumns(10);
		txtResolutionY.setBounds(184, 70, 51, 20);
		getContentPane().add(txtResolutionY);
		
		JButton btnOk = new JButton("OK");
		btnOk.setBounds(236, 108, 89, 23);
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnOk_Clicked();
			}
		});
		getContentPane().add(btnOk);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setBounds(335, 108, 89, 23);
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnCancel_Clicked();
			}
		});
		getContentPane().add(btnCancel);
		
		JLabel lblName = new JLabel("Name:");
		lblName.setBounds(10, 11, 46, 14);
		getContentPane().add(lblName);
		
		txtName = new JTextField();
		txtName.setBounds(113, 8, 171, 20);
		getContentPane().add(txtName);
		txtName.setColumns(10);
	}
	
	
	
	private void btnOk_Clicked() {
		String sdiagonal = txtDiagonal.getText();
		String sresx = txtResolutionX.getText();
		String sresy = txtResolutionY.getText();
		
		float diagonal;
		int resx;
		int resy;
		try {
			diagonal = Float.parseFloat(sdiagonal);
			resx = Integer.parseInt(sresx);
			resy = Integer.parseInt(sresy);
		} catch(NumberFormatException e) {
			JOptionPane.showMessageDialog(this,
					"Number format incorrect",
					"Error in number",
					JOptionPane.OK_OPTION);
			return;
		}
		
		if(editMode) {
			manager.editMaskProperty(mask, MasksManager.PROPERTY_MASK_DIAGONAL,
					diagonal);
			manager.editMaskProperty(mask, MasksManager.PROPERTY_MASK_NAME,
					txtName.getText());
			manager.editMaskProperty(mask, MasksManager.PROPERTY_MASK_RESOLUTION,
					new Size(resx, resy));
		} else {
			mask.setName(txtName.getText());
			mask.setDiameter(diagonal);
			mask.setResolution(new Size(resx, resy));
			manager.addMask(mask);
		}
		
		dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}
	
	
	
	private void btnCancel_Clicked() {
		dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}
}
