package com.macrokeyseditor.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.JComponent;

import org.eclipse.jdt.annotation.NonNull;

import com.macrokeys.MacroKey;
import com.macrokeys.MacroScreen;
import com.macrokeys.MacroScreen.Orientation;
import com.macrokeys.rendering.PaintStyle;
import com.macrokeys.rendering.RectF;
import com.macrokeys.rendering.Renderer;
import com.macrokeys.rendering.TextAllign;
import com.macrokeys.screen.Screen;
import com.macrokeys.screen.ScreenException;
import com.macrokeys.screen.ScreenUtility;
import com.macrokeyseditor.MacroScreenEditor;
import com.macrokeyseditor.MacroScreenEditorListener;
import com.macrokeyseditor.Mask;
import com.macrokeyseditor.MasksManager;
import com.macrokeyseditor.MasksManager.MasksManagerListener;
import com.macrokeyseditor.fur.ModifyAction;
import com.macrokeyseditor.util.DesktopScreen;
import com.macrokeyseditor.util.Size;
import com.macrokeyseditor.util.ColorUtil;

/**
 * Componente per il rendering dei tasti
 */
public class MKRenderingComponent extends JComponent {
	
	/** Paint per il paattern a scacchiera di background; mai null */
	private static final TexturePaint checkboardPattern;
	
	/** Per lo spostamento dei tasti */
	private final MacroKeyDragger keyDragger = new MacroKeyDragger();
	
	/** MacroScreen attualmente mostrata; null se nessuna � selezionata */
	private final MacroScreenEditor screenEdit;
	/** Proprietà del monitor utilizzato */
	private final Screen screen;
	/** Manager delle maschere */
	private final MasksManager maskManager;
	
	/** Istanza per il rendering della MacroScreen */
	private Painter painter;
	
	
	
	static {
		// Creo il pattern a scacchiera
		BufferedImage bi = new BufferedImage(20, 20,
	        BufferedImage.TYPE_INT_RGB);
	    Graphics2D g = bi.createGraphics();
	    g.setColor(new Color(102, 102, 102));
	    g.fillRect(0, 0, 10, 10);
	    g.fillRect(10, 10, 20, 20);
	    g.setColor(new Color(153, 153, 153));
	    g.fillRect(10, 0, 20, 10);
	    g.fillRect(0, 10, 10, 20);
	    Rectangle r = new Rectangle(0, 0, 20, 20);
	    checkboardPattern = new TexturePaint(bi, r);
	}
	
	
	/**
	 * @param screenEdit Editor della {@link MacroScreen} gestita da questo componente
	 * @param maskManager Manager delle maschere
	 * @throws ScreenException Se è impossibile ottenere le informazioni necessarie dal 
	 * @throws NullPointerException Se un parametro è null
	 */
	public MKRenderingComponent(MacroScreenEditor screenEdit, MasksManager maskManager)
			throws ScreenException {
		Objects.requireNonNull(screenEdit);
		Objects.requireNonNull(maskManager);
		
		this.screen = new DesktopScreen();
		this.screenEdit = screenEdit;
		this.maskManager = maskManager;
		
		screenEdit.addEditEventListener(new MacroScreenEditorListener() {
			
			@Override
			public void selectionChange(List<MacroKey> actual) {
				stopOperation(true);
			}
			
			@Override
			public void macroScreenEdited(@NonNull MacroScreen m) {
				stopOperation(true);
			}
			
			@Override
			public void macroKeyRemoved(@NonNull MacroScreen ms,
					@NonNull List<MacroKey> mk) {
				stopOperation(true);
			}
			
			@Override
			public void macroKeyEdited(@NonNull MacroScreen ms,
					@NonNull List<MacroKey> mk, @NonNull String property) {
				stopOperation(true);
			}
			
			@Override
			public void macroKeyAdded(@NonNull MacroScreen ms,
					@NonNull List<MacroKey> mk) {
				stopOperation(true);
			}

			@Override
			public void swapMacroKeys(@NonNull MacroKey a, @NonNull MacroKey b) {
				stopOperation(true);
			}
			
		});
		
		
		maskManager.addListener(new MasksManagerListener() {
			
			@Override
			public void maskRemove(Mask m) {
				// Niente
			}
			
			@Override
			public void maskEdited(Mask mask, String property, Object value) {
				if(maskManager.getSelected() == mask) {
					repaint();
				}
			}
			
			@Override
			public void maskAdded(Mask m) {
				// Niente
			}

			@Override
			public void maskSelectedChanged(Mask m) {
				repaint();
			}
		});
		
		addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseMoved(MouseEvent e) {
				
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				keyDragger.onMouseDragged(e);
			}
		});
		
		
		
		addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				keyDragger.onMouseRelease(e);
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				MacroScreen s = screenEdit.getMacroScreen();
				if(s == null) {
					return;
				}
				
				if(e.getButton() == MouseEvent.BUTTON1) {
					assert screen != null;
					MacroKey newSel = s.keyAt(e.getX(), e.getY(), screen);
					if(newSel == null) {
						screenEdit.deselect();
					} else {
						boolean addSelection = e.isControlDown() || e.isShiftDown();
						screenEdit.select(newSel, !addSelection);
					}
				}
				keyDragger.onMousePressed(e);
			}
			
			@Override
			public void mouseExited(MouseEvent e) {

			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				//Non sempre questo evento � eseguito
			}
				
		});
	}
	
	
	@Override
	protected void paintComponent(Graphics gg) {
		super.paintComponent(gg);
		
		Graphics2D g = (Graphics2D) gg;
		MacroScreen s = screenEdit.getMacroScreen();
		List<MacroKey> sel = screenEdit.getMacroKeySelected();
		
		Rectangle clip = getBounds();
		
		g.setPaint(checkboardPattern);
		g.fillRect(0, 0, clip.width, clip.height);
		
		
		if(s != null) {
			// Per il rendering della maschera
			Mask mask = maskManager.getSelected();
			RectF background;
			if(mask == null) {
				background = new RectF(0, 0, clip.width, clip.height);
			} else {
				Size maskSize = mask.getScreenSize(screen);
				if(s.getOrientation() == Orientation.Horizontal) {
					background = new RectF(0, 0, maskSize.width, maskSize.height);
				} else { // Anche nel caso Rotate uso il verticale
					background = new RectF(0, 0, maskSize.height, maskSize.width);
				}
			}
			
			
			if(painter == null) {
				painter = new Painter(g, getFont());
			} else {
				painter.reset(g);
			}
			
			
			s.render(painter,
					screen,
					background,
					sel);
		}
	}

	/**
	 * Permette di interrompere le operazioni di input (es. spostamento di un tasto)
	 * @param refresh False -> il controllo non aggiorna la grafica automaticamente per questa operazione,
	 * potrebbe essere necessario che l'utente la esegua
	 */
	public void stopOperation(boolean refresh) {
		keyDragger.cancel(refresh);
	}
	
	
	/**
	 * @return Editor della schermata renderizzata
	 */
	public MacroScreenEditor getMacroScreenEditor() {
		return screenEdit;
	}
	
	
	
	/** Implementazione di rendering per swing */
	private static final class Painter implements Renderer {
		
		private PaintStyle p;
		private TextAllign t;
		
		private Font f;
		private Graphics2D g;
		
		public Painter(@NonNull Graphics2D g, @NonNull Font f) {
			this.f = f;
			reset(g);
		}
		
		/**
		 * Resetta lo stato di rendering. Utile quando si deve renderizzare un'altro frame
		 * @param g Oggetto grafico nuovo
		 */
		public void reset(@NonNull Graphics2D g) {
			this.g = g;
			p = PaintStyle.Fill;
			t = TextAllign.Center;
		}
		
		
		@Override
		public void setTextSize(float textSize) {
			g.setFont(f.deriveFont((int)textSize));
			
		}
		
		@Override
		public void setTextAllign(@NonNull TextAllign t) {
			this.t= t;
		}
		
		@Override
		public void setPaintStyle(@NonNull PaintStyle p) {
			this.p = p;
		}
		
		@Override
		public void setColor(int argb) {
			Color c = ColorUtil.ARGBtoColor(argb);
			g.setColor(c);
		}
		
		@Override
		public void setAntiAlias(boolean aa) {
			Object f = aa ? 
				RenderingHints.VALUE_ANTIALIAS_ON : 
				RenderingHints.VALUE_ANTIALIAS_OFF;
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, f);
		}
		
		@Override
		public void rect(@NonNull RectF a) {
			switch(p) {
			case Fill: g.fillRect((int)a.left, (int)a.top, (int)a.width(), (int)a.height());
				break;
			case Fill_and_stroke: g.fillRect((int)a.left, (int)a.top, (int)a.width(), (int)a.height());
				break;
			case Stroke: g.drawRect((int)a.left, (int)a.top, (int)a.width(), (int)a.height());
				break;
				
			default: assert false;
				break;
				
			}
		}
		
		@Override
		public void ellipse(@NonNull RectF a) {
			switch(p) {
			case Fill: g.fillOval((int)a.left, (int)a.top, (int)a.width(), (int)a.height());
				break;
			case Fill_and_stroke: g.fillOval((int)a.left, (int)a.top, (int)a.width(), (int)a.height());
				break;
			case Stroke: g.drawOval((int)a.left, (int)a.top, (int)a.width(), (int)a.height());
				break;
				
				
			default: assert false;
				break;
			}
		}

		@Override
		public void text(@NonNull String s, @NonNull RectF r) {
			float x = 0, y = 0;
			FontMetrics metrics = g.getFontMetrics(g.getFont());
			float w = metrics.stringWidth(s);
			float h = metrics.getHeight();
			float asc = metrics.getAscent();
			switch(t) {
			case Center: 
				x = r.left + (r.width() - w) / 2;
				y = r.top + (r.height() - h) / 2 + asc;
				break;
			case Left: //TODO: 
				throw new RuntimeException("Not implemented");
			case Right: //TODO:
				throw new RuntimeException("Not implemented");
			default: 
				assert false;
				break;
			
			}
			
			g.drawString(s, (int)x, (int)y);
		}
	}
	
	
	/** Classe utilizzata per la gestione dello spostamento dei {@link MacroKey} */
	private class MacroKeyDragger {
		
		private final List<Pair> draggedKeys = new ArrayList<>();
		private int startX, startY;
		private boolean drag = false;
		private boolean mouseDown = false;
		
		/** Evento associato alla pressione di un tasto del mouse */
		public void onMousePressed(MouseEvent e) {
			mouseDown = e.getButton() == MouseEvent.BUTTON1;
			if(mouseDown) {
				List<MacroKey> sel = screenEdit.getMacroKeySelected();
				if(!sel.isEmpty()) {
					startX = e.getX();
					startY = e.getY();
					draggedKeys.clear();
					for(MacroKey m : sel) {
						Pair p = new Pair();
						p.initialArea = m.getArea();
						p.m = m;
						draggedKeys.add(p);
					}
				}
			}
		}
		
		/** Evento associato al rilascio di un tasto del mouse */
		public void onMouseRelease(MouseEvent e) {
			mouseDown = e.getButton() != MouseEvent.BUTTON1;
			if(drag && !mouseDown) {
				drag = false;
				
				// Resetto la posizione dei tasti e creo la lista delle
				// modifiche alle aree da effettuare
				List<ModifyAction.Set<MacroKey>> sets = new ArrayList<>();
				for(Pair p : draggedKeys) {
					// Resetto la posizione del tasto
					p.m.setArea(p.initialArea);
					// Calcolo la posizione finale del tasto
					RectF newArea = moveRect(p.initialArea,
							screen,
							e.getX() - startX,
							e.getY() - startY);
					
					// Aggiungo il set da compiere
					ModifyAction.Set<MacroKey> s =
							new ModifyAction.Set<MacroKey>(p.m, newArea);
					sets.add(s);
				}
				
				screenEdit.editMacroKeyProperty(sets,
						MacroScreenEditor.MACRO_KEY_PROPETY_AREA);
			}
		}
		
		/** Evento associato al movimento del mouse con la pressione di un tasto */
		public void onMouseDragged(MouseEvent e) {
			List<MacroKey> sel = screenEdit.getMacroKeySelected();
			drag = mouseDown && !sel.isEmpty();
			if(drag) {
				int dx = e.getX() - startX;
				int dy = e.getY() - startY;
				for(Pair p : draggedKeys) {
					RectF f = moveRect(p.initialArea, screen, dx, dy);
					p.m.setArea(f);
					
				}
				repaint();
			}
			
		}
		
		/** 
		 * Permette di annullare l'operazione di drag 
		 * @param False non esegue l'aggiornamento della grafica del componente anche se necessario
		 */
		public void cancel(boolean refresh) {
			if(drag) {
				drag = false;
				for(Pair p : draggedKeys) {
					p.m.setArea(p.initialArea);					
				}
			}
			
			if(refresh) {
				repaint();
			}
		}
		
		
		
		private class Pair {
			RectF initialArea;
			MacroKey m;
		}
	}
	
	/**
	 * Trasla un rettangolo in millimetri di una quantit� delta fornita in pixel
	 * @param r Rettangolo da traslare
	 * @param s Schermo di riferimento
	 * @param deltaPxX Traslazione in pixel sull'asse X
	 * @param deltaPxY Traslazione in pixel sull'asse Y
	 * @return Rettangolo {@code r} traslato in millimetri
	 */
	private static RectF moveRect(@NonNull RectF r, @NonNull Screen s, float deltaPxX, float deltaPxY) {
		float dx = ScreenUtility.pxtomm_X(deltaPxX, s);
		float dy = ScreenUtility.pxtomm_Y(deltaPxY, s);
		//Converto in Int per evitare problemi di imprecisione nel rendering
		return new RectF((int)(r.left + dx), (int)(r.top + dy), (int)(r.right + dx), (int)(r.bottom + dy));
	}
}
