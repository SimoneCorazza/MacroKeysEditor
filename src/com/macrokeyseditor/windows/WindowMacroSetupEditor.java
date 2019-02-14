package com.macrokeyseditor.windows;


import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

import com.macrokeys.MacroKey;
import com.macrokeys.MacroScreen;
import com.macrokeys.MacroSetup;
import com.macrokeys.rendering.RectF;
import com.macrokeyseditor.MacroScreenEditor;
import com.macrokeyseditor.MacroSetupEditor;
import com.macrokeyseditor.Mask;
import com.macrokeyseditor.MasksManager;
import com.macrokeyseditor.MasksManager.MasksManagerListener;
import com.macrokeyseditor.components.MKPropetyComponent;
import com.macrokeyseditor.components.MacroScreenTabPane;
import com.macrokeyseditor.util.FileExtensions;
import com.macrokeyseditor.copypaste.MKClipboardOwner;
import com.macrokeyseditor.copypaste.MKTransfer;
import com.macrokeyseditor.settings.SettingLoadException;
import com.macrokeyseditor.settings.Settings;
import com.macrokeyseditor.windows.DialogMacroScreen;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Form per l'editing della macro setup 
 */
public class WindowMacroSetupEditor extends JFrame {
	
	private final JMenuItem mniSaveAs;
	private final JMenuItem mniLoad;
	private final JMenuItem mniUndo;
	private final JMenuItem mniRedo;
	private final JMenuItem mniAddKey;
	private final JMenuItem mniAddScreen;
	private final JMenuItem mniEditScreen;
	private final JMenuItem mniRemoveScreen;
	private final JMenuItem mniCopy;
	private final JMenuItem mniPaste;
	private final JMenuItem mniDeleteKey;
	private final JMenuItem mniScreen2Up;
	private final JMenuItem mniScreen2Right;
	private final JMenuItem mniScreen2Down;
	private final JMenuItem mniScreen2Left;
	private final JMenuItem mniScreen3Up;
	private final JMenuItem mniScreen3Right;
	private final JMenuItem mniScreen3Down;
	private final JMenuItem mniScreen3Left;
	private final JMenuItem mniMoveUp;
	private final JMenuItem mniMoveDown;
	
	

	/** Controllo per visualizzare le proprietà dei tasti */
	private final MKPropetyComponent prop;
	/** Componente per la visualizzazione e modifica della {@link MacroScreen} */
	private final MacroScreenTabPane tapMacroScreen;
	
	/** Manager dei menù */
	private final MenuMaskManager menuMaskManager;
	
	private MacroSetupEditor editor;
	private MasksManager masksManager;
	
	private JMenuItem mniSelectAll;
	private JMenu mnMacroScreen;
	private JMenu mnMasks;
	private JMenuItem mniMaskManage;
	private JSeparator separator;
	
	
	
	public WindowMacroSetupEditor() {
		editor = new MacroSetupEditor();
		masksManager = new MasksManager();
		
		try {
			Settings.loadSettings(masksManager);
		} catch(SettingLoadException e) {
			
		}
		
		setBounds(100, 100, 695, 484);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu(" File ");
		menuBar.add(mnFile);
		
		mniLoad = new JMenuItem("Load...");
		mniLoad.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
		mnFile.add(mniLoad);
		
		mniSaveAs = new JMenuItem("Save as...");
		mniSaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		mnFile.add(mniSaveAs);
		
		JMenu mnEdit = new JMenu(" Edit ");
		menuBar.add(mnEdit);
		
		mniUndo = new JMenuItem("Undo");
		
		mniAddKey = new JMenuItem("Add Key");
		mnEdit.add(mniAddKey);
		mniAddKey.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, InputEvent.ALT_MASK));
		mnEdit.addSeparator();
		
		mniUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));
		mnEdit.add(mniUndo);
		
		mniRedo = new JMenuItem("Redo");
		mniRedo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_MASK));
		mnEdit.add(mniRedo);
		
		mniDeleteKey = new JMenuItem("Delete key");
		mniDeleteKey.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		mnEdit.add(mniDeleteKey);
		
		mniCopy = new JMenuItem("Copy");
		mniCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
		mnEdit.add(mniCopy);
		
		mniPaste = new JMenuItem("Paste");
		mniPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
		mnEdit.add(mniPaste);
		
		mniSelectAll = new JMenuItem("Select All");
		mniSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));
		mnEdit.add(mniSelectAll);
		
		mniMoveUp = new JMenuItem("Move up");
		mnEdit.add(mniMoveUp);
		
		mniMoveDown = new JMenuItem("Move down");
		mnEdit.add(mniMoveDown);
		
		mnMacroScreen = new JMenu("Macro screen");
		menuBar.add(mnMacroScreen);
		
		mniAddScreen = new JMenuItem("Add Screen");
		mnMacroScreen.add(mniAddScreen);
		mniAddScreen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.ALT_MASK));
		
		mniEditScreen = new JMenuItem("Edit screen...");
		mnMacroScreen.add(mniEditScreen);
		mniEditScreen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_MASK));
		
		mniRemoveScreen = new JMenuItem("Remove screen");
		mnMacroScreen.add(mniRemoveScreen);
		
		JMenu mnView = new JMenu(" View ");
		menuBar.add(mnView);
		
		mniScreen2Up = new JMenuItem("Screen two touch up");
		mnView.add(mniScreen2Up);
		mniScreen2Up.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.CTRL_MASK));
		
		mniScreen2Right = new JMenuItem("Screen two touch right");
		mnView.add(mniScreen2Right);
		mniScreen2Right.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.CTRL_MASK));
		
		mniScreen2Down = new JMenuItem("Screen two touch down");
		mnView.add(mniScreen2Down);
		mniScreen2Down.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.CTRL_MASK));
		
		mniScreen2Left = new JMenuItem("Screen two touch left");
		mnView.add(mniScreen2Left);
		mniScreen2Left.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.CTRL_MASK));
		
		mniScreen3Up = new JMenuItem("Screen three touch up");
		mnView.add(mniScreen3Up);
		mniScreen3Up.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		
		mniScreen3Right = new JMenuItem("Screen three touch right");
		mnView.add(mniScreen3Right);
		mniScreen3Right.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		
		mniScreen3Down = new JMenuItem("Screen three touch down");
		mnView.add(mniScreen3Down);
		mniScreen3Down.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		
		mniScreen3Left = new JMenuItem("Screen three touch left");
		mnView.add(mniScreen3Left);
		mniScreen3Left.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		
		mnMasks = new JMenu("Masks");
		menuBar.add(mnMasks);
		
		mniMaskManage = new JMenuItem("Manage...");
		mniMaskManage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DialogMaskManager d = new DialogMaskManager(masksManager,
						WindowMacroSetupEditor.this);
				d.setVisible(true);
			}
		});
		mnMasks.add(mniMaskManage);
		
		separator = new JSeparator();
		mnMasks.add(separator);
		
		
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		prop = new MKPropetyComponent(editor);
		contentPane.add(prop, BorderLayout.EAST);
		
		final JTabbedPane tbbScreens = new JTabbedPane(JTabbedPane.TOP);
		tapMacroScreen = new MacroScreenTabPane(tbbScreens, editor, masksManager);
		contentPane.add(tbbScreens, BorderLayout.CENTER);
		
		
		// Per la gestione dei menù delle Mask
		menuMaskManager = new MenuMaskManager(masksManager, mnMasks);
		
		addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent e) {
				// Niente
			}
			
			@Override
			public void windowIconified(WindowEvent e) {
				// Niente
			}
			
			@Override
			public void windowDeiconified(WindowEvent e) {
				// Niente
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) {
				// Niente
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					Settings.saveSettings(masksManager);
				} catch(IOException ignored) {
					// Niente
				}
				
				// Chiudo l'applicazione.
				// Senza questa istruzione l'applicazione continua
				System.exit(NORMAL);
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				// Niente
			}
			
			@Override
			public void windowActivated(WindowEvent e) {
				// Niente
			}
		});
		
		initCallback();
	}
	
	
	
	
	/**
	 * Inizializza i callback della GUI
	 */
	private void initCallback() {

		
		mniLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String path = macroSetupFileChooser(false);
				if(path != null) {
					MacroSetup setup = null;
					try {
						setup = MacroSetup.load(path);
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null,
								e.getLocalizedMessage(),
								"Error while loading the file", 
								JOptionPane.ERROR_MESSAGE);
					}
					
					if(setup != null) { //Caricamento ha avuto successo
						// Elimino le vecchie macro screen
						for(MacroScreen s : editor.getMacroScreens()) {
							editor.removeMacroScreen(s);
						}
						
						// Carico le nuove macro screen
						for(final MacroScreen sc : setup.getMacroScreens()) {
							editor.addMacroScreen(sc);
						}
					}
				}
			}
		});
		
		mniSaveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				List<MacroScreen> screens = Arrays.asList(
						editor.getMacroScreens());
				MacroSetup s = new MacroSetup(screens);
				
				String path = macroSetupFileChooser(true);
				if(path != null) {
					try {
						s.save(path);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		mniRedo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MacroScreenEditor e = editor.getMacroScreenEditorSelected();
				if(e != null) {
					e.redo();
				}
			}
		});
		
		mniUndo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MacroScreenEditor e = editor.getMacroScreenEditorSelected();
				if(e != null) {
					e.undo();
				}
			}
		});
		
		mniAddKey.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MacroScreenEditor e = editor.getMacroScreenEditorSelected();
				if(e != null) {
					MacroKey n = new MacroKey();
					n.setArea(new RectF(0, 0, 15, 15));
					List<MacroKey> l = new ArrayList<>();
					l.add(n);
					e.add(l);
				}
			}
		});
		
		mniAddScreen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				DialogMacroScreen s = 
						new DialogMacroScreen(WindowMacroSetupEditor.this);
				if(s.showDialog()) {
					MacroScreen m = s.getMacroScreen();
					editor.addMacroScreen(m);
				}
			}
		});
		
		mniEditScreen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MacroScreenEditor e = editor.getMacroScreenEditorSelected();
				if(e != null) {
					DialogMacroScreen w = new DialogMacroScreen(
							WindowMacroSetupEditor.this,
							e);
					w.showDialog();
				}
			}
		});
		
		mniDeleteKey.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MacroScreenEditor e = editor.getMacroScreenEditorSelected();
				if(e != null) {
					List<MacroKey> sel = e.getMacroKeySelected();
					if(!sel.isEmpty()) {
						e.remove(sel);
					}
				}
			}
		});
		
		mniRemoveScreen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MacroScreenEditor e = editor.getMacroScreenEditorSelected();
				if(e != null) {
					int selection = JOptionPane.showConfirmDialog(null,
							"Are you sure to delete the selected screen?",
							"Conferm action",
							JOptionPane.YES_NO_OPTION);
					if(selection == JOptionPane.YES_OPTION) {
						// Rimuovo la macro screen selezionata
						editor.removeMacroScreen(e.getMacroScreen());
					}
				}
			}
		});
		
		/** http://stackoverflow.com/questions/14276182/java-use-clipboard-to-copy-paste-java-objects-between-different-instances-of-sa */
		mniCopy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MacroScreenEditor e = editor.getMacroScreenEditorSelected();
				if(e != null) {
					List<MacroKey> selected = e.getMacroKeySelected();
					if(!selected.isEmpty()) {
						Clipboard clipboard = Toolkit.getDefaultToolkit()
								.getSystemClipboard();
						
						
						MKTransfer trn = new MKTransfer(selected);
						clipboard.setContents(trn, MKClipboardOwner.ISTANCE);
					}
				}
			}
		});
		
		/** http://stackoverflow.com/questions/14276182/java-use-clipboard-to-copy-paste-java-objects-between-different-instances-of-sa */
		mniPaste.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MacroScreenEditor mse = editor.getMacroScreenEditorSelected();
				if(mse != null) {
					Clipboard clipboard = Toolkit.getDefaultToolkit()
							.getSystemClipboard();
					Transferable t = clipboard
							.getContents(MKClipboardOwner.ISTANCE);
					
					if(t.isDataFlavorSupported(MKTransfer.MK_FLAVOR)) {
						Object o = null;
						try {
							 o = t.getTransferData(MKTransfer.MK_FLAVOR);
						} catch (UnsupportedFlavorException | IOException e) {
							assert false : "HE SAD IT SUPPORTED IT";
							e.printStackTrace();
						}
						
						@SuppressWarnings("unchecked")
						List<MacroKey> k = (List<MacroKey>)o;
						// Aggiungo i tasti
						mse.add(k);
						// Seleziono i tasti aggiunti
						mse.select(k, true);
						
					}
				}
			}
		});
		
		mniSelectAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _) {
				MacroScreenEditor e = editor.getMacroScreenEditorSelected();
				if(e != null) {
					e.select(e.getMacroScreen().getKeys(), true);
				}
			}
		});
		
		mniMoveUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MacroScreenEditor e = editor.getMacroScreenEditorSelected();
				if(e != null) {
					List<MacroKey> sel = e.getMacroKeySelected();
					if(!sel.isEmpty()) {
						// Sposto il primo elelemnto della selezione
						e.moveMacroKeyUp(sel.get(0));
					}
				}
			}
		});
		
		mniMoveDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MacroScreenEditor e = editor.getMacroScreenEditorSelected();
				if(e != null) {
					List<MacroKey> sel = e.getMacroKeySelected();
					if(!sel.isEmpty()) {
						// Sposto il primo elelemnto della selezione
						e.moveMacroKeyDown(sel.get(0));
					}
				}
			}
		});
		
		
		mniScreen2Up.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectMacroScreen(MacroScreen.SwipeType.Finger2_Up);
			}
		});
		mniScreen2Right.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectMacroScreen(MacroScreen.SwipeType.Finger2_Right);
			}
		});
		mniScreen2Down.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectMacroScreen(MacroScreen.SwipeType.Finger2_Down);
			}
		});
		mniScreen2Left.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectMacroScreen(MacroScreen.SwipeType.Finger2_Left);
			}
		});
		mniScreen3Up.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectMacroScreen(MacroScreen.SwipeType.Finger3_Up);
			}
		});
		mniScreen3Right.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectMacroScreen(MacroScreen.SwipeType.Finger3_Right);
			}
		});
		mniScreen3Down.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectMacroScreen(MacroScreen.SwipeType.Finger3_Down);
			}
		});
		mniScreen3Left.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectMacroScreen(MacroScreen.SwipeType.Finger3_Left);
			}
		});
	}
	
	
	/**
	 * Mostra un {@link JFileChooser} per il salvataggio o l'apertura di
	 * una {@link MacroSetup}
	 * @param mode True: salvataggio; False: caricamento
	 * @return Path al file selezionato; null se operazione annullata
	 */
	private String macroSetupFileChooser(boolean mode) {
		JFileChooser f = new JFileChooser();
		f.setFileFilter(new FileNameExtensionFilter(
				"File di macro (." + FileExtensions.MACRO_SETUP + ")",
				FileExtensions.MACRO_SETUP
				));
		
		f.setAcceptAllFileFilterUsed(false);
		f.setCurrentDirectory(new File(System.getProperty("user.dir")));
		
		int res;
		if(mode) {
			res = f.showSaveDialog(this);
		} else {
			res = f.showOpenDialog(this);
		}
		
		if(res == JFileChooser.APPROVE_OPTION) {
			return f.getSelectedFile().getAbsolutePath();
		} else {
			return null;
		}
	}
	
	

	
	

	private void selectMacroScreen(MacroScreen.SwipeType pos) {
		// Seleziono la MacroScreen avente la posizione indicata
		for(MacroScreen m : editor.getMacroScreens()) {
			if(m.getSwipeType().equals(pos)) {
				editor.selectMacroScreen(m);
				return;
			}
		}
	}
	
	
	
	/**
	 * Gestisce i menù relativi alle maschere.
	 */
	private static class MenuMaskManager implements MasksManagerListener {

		private final JMenu menuMasks;
		private final MasksManager manager;
		/** Menù item nel caso se nessuna Mask deve essere selezionata */
		private final JCheckBoxMenuItem noneMask;
		/** Menù per i menù rappresentanti le maschere contenenti nel manager */
		private final List<JCheckBoxMenuItem> menuItems = new ArrayList<>();
		
		
		public MenuMaskManager(MasksManager manager, JMenu menuMasks) {
			Objects.requireNonNull(manager);
			Objects.requireNonNull(menuMasks);
			
			this.menuMasks = menuMasks;
			this.manager = manager;
			this.noneMask = new JCheckBoxMenuItem("None");
			// Per impedire all'utente di deselezionare l'item
			this.noneMask.setModel(new DefaultButtonModel());
			this.noneMask.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					manager.select(null);
				}
			});
			this.menuMasks.add(noneMask);
			
			manager.addListener(this);
			
			repopulateMenu();
		}
		
		@Override
		public void maskAdded(Mask m) {
			repopulateMenu();
		}

		@Override
		public void maskRemove(Mask m) {
			repopulateMenu();
		}

		@Override
		public void maskEdited(Mask mask, String property, Object value) {
			repopulateMenu();
		}

		@Override
		public void maskSelectedChanged(Mask m) {
			repopulateMenu();
		}
		
		
		/**
		 * Ripolola il menù eliminando gli item già presenti e ne aggiunge in
		 * base all'attuale stato del manager
		 */
		private void repopulateMenu() {
			// Seleziono se nessuna maschera selezionata
			this.noneMask.setState(manager.getSelected() == null);
			
			// Rimuovo i menù esistenti
			for(JCheckBoxMenuItem it : menuItems) {
				menuMasks.remove(it);
			}
			
			// Inserisco i menù
			for(Mask m : manager.getMasks()) {
				JCheckBoxMenuItem it = new JCheckBoxMenuItem();
				// Per impedire all'utente di deselezionare l'item
				it.setModel(new DefaultButtonModel());
				it.setText(m.toString());
				it.setState(m == manager.getSelected());
				it.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						manager.select(m);
					}
				});
				menuMasks.add(it);
				menuItems.add(it);
			}
		}
		
	}
}
