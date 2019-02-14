package com.macrokeyseditor.components;

import java.util.List;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.eclipse.jdt.annotation.NonNull;

import com.macrokeys.MacroKey;
import com.macrokeys.MacroScreen;
import com.macrokeys.MacroSetup;
import com.macrokeys.screen.ScreenException;
import com.macrokeyseditor.MacroScreenEditor;
import com.macrokeyseditor.MacroScreenEditorListener;
import com.macrokeyseditor.MacroSetupEditor;
import com.macrokeyseditor.MacroSetupEditorListener;
import com.macrokeyseditor.MasksManager;

public class MacroScreenTabPane {
	
	private final JTabbedPane tabPane;
	
	private final MacroSetupEditor macSetEditor;
	
	/** Listener per le modifiche apportate ad una {@link MacroScreen} */
	private final MacroScreenEditorListener macroScreenListener =
			new MacroScrenEdList();
			
	
	/**
	 * @param tabPane Pannello di cui eseguire il wrapper
	 * @param setupEditor Editor per la {@link MacroSetup}
	 * @param masksManager manager delle maschere
	 */
	public MacroScreenTabPane(@NonNull JTabbedPane tabPane,
			@NonNull MacroSetupEditor setupEditor,
			MasksManager masksManager) {
		this.tabPane = tabPane;
		this.macSetEditor = setupEditor;
		
		macSetEditor.addActionListener(new MacroSetupEditorListener() {
			@Override
			public void actionPerformed(Action a, MacroScreenEditor s) {
				if(a == MacroSetupEditorListener.Action.Add) {
					MacroScreen m = s.getMacroScreen();
					String title = swipeTypeToString(m.getSwipeType());
					
					MKRenderingComponent rmk;
					try {
						rmk = new MKRenderingComponent(s, masksManager);
					} catch (ScreenException e) {
						// TODO: gestire eccezione
						e.printStackTrace();
						return;
					}
					
					
					tabPane.add(title, rmk);
					s.addEditEventListener(macroScreenListener);
				} else {
					// Rimuovo l'evento
					s.removeEditEventListener(macroScreenListener);
					
					// Rimuovo dalla TabPane e dalla map la MacroScreen
					int index = find(s);
					assert index != -1;
					tabPane.remove(index);
				}
			}

			@Override
			public void selectionChange(MacroScreenEditor old,
					MacroScreenEditor actual) {
				if(actual == null) {
					tabPane.setVisible(false);
				} else {
					tabPane.setVisible(true);
					// Cambio la selezione del pannello
					int index = find(actual);
					assert index != -1;
					tabPane.setSelectedIndex(index);
				}
					
			}
		});
		
		
		tabPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				// Cambio la selezione
				int index = tabPane.getSelectedIndex();
				if(index == -1) {
					setupEditor.selectMacroScreen(null);
				} else {
					MKRenderingComponent c = (MKRenderingComponent)
							tabPane.getComponentAt(index);
					MacroScreenEditor editor = c.getMacroScreenEditor();
					setupEditor.selectMacroScreen(editor.getMacroScreen());
				}
				
				
			}
		});
	}
	
	
	
	/**
	 * @return Pannello di cui si esegue il wrapper
	 */
	public JTabbedPane getJTabbedPane() {
		return tabPane;
	}
	
	
	/**
	 * Trova l'indice della tab il cui componente tratta la
	 * {@link MacroScreenEditor} indicata 
	 * @param m Macro da trovare
	 * @return Indice della tab; -1 se non trovata
	 */
	private int find(MacroScreenEditor m) {
		for(int i = 0; i < tabPane.getTabCount(); i++) {
			MKRenderingComponent c = (MKRenderingComponent)
					tabPane.getComponentAt(i);
			if(c.getMacroScreenEditor() == m) {
				return i;
			}
		}
		
		return -1;
	}
	
	
	
	/**
	 * Trova l'indice della tab il cui componente tratta la
	 * {@link MacroScreen} indicata 
	 * @param m Macro da trovare
	 * @return Indice della tab; -1 se non trovata
	 */
	private int find(MacroScreen m) {
		for(int i = 0; i < tabPane.getTabCount(); i++) {
			MKRenderingComponent c = (MKRenderingComponent)
					tabPane.getComponentAt(i);
			if(c.getMacroScreenEditor().getMacroScreen() == m) {
				return i;
			}
		}
		
		return -1;
	}
	
	
	
	
	
	
	
	
	
	/**
	 * @param t Swipe da trasformare in stringa
	 * @return Stringa esplicativa dello swipe
	 */
	private static @NonNull String swipeTypeToString(MacroScreen.SwipeType t) {
		switch(t) {
			case Finger2_Down: return "2 Finghers down";
			case Finger2_Left: return "2 Finghers left";
			case Finger2_Right: return "2 Finghers right";
			case Finger2_Up: return "2 Finghers up";
			case Finger3_Down: return "3 Finghers down";
			case Finger3_Left: return "3 Finghers left";
			case Finger3_Right: return "3 Finghers right";
			case Finger3_Up: return "3 Finghers up";
			
			default:
				assert false : "Case not known";
				return "";
		}
	}
	
	
	
	// ------------------
	// Classi per callback
	// -----------------
	
	
	/** Implementazione del listener per questa classe */
	private class MacroScrenEdList implements MacroScreenEditorListener {
		
		@Override
		public void selectionChange(List<MacroKey> actual) {
			// Niente
		}
		
		@Override
		public void macroScreenEdited(@NonNull MacroScreen m) {
			// Aggiorno il titolo della tab
			int index = find(m);
			String title = swipeTypeToString(m.getSwipeType());
			tabPane.setTitleAt(index, title);
		}
		
		@Override
		public void macroKeyRemoved(@NonNull MacroScreen ms,
				@NonNull List<MacroKey> mk) {
			// Niente
		}
		
		@Override
		public void macroKeyEdited(@NonNull MacroScreen ms,
				@NonNull List<MacroKey> mk, @NonNull String property) {
			// Niente
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
}
