package com.macrokeyseditor.settings;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

import com.macrokeyseditor.MasksManager;
import com.macrokeyseditor.settings.SettingLoadException.Motive;

/**
 * Classe statica per la gestione delle impostazioni
 */
public class Settings {

	private static final String SETTINGS_FILE = "settings.data";
	
	
	private Settings() { }
	
	
	
	/**
	 * Carica le impostazioni dei manager
	 * @param manager Manager delle maschere
	 * @throws SettingLoadException In caso di errore nel caricamento
	 */
	public static void loadSettings(MasksManager manager) throws SettingLoadException {
		Objects.requireNonNull(manager);
		
		InputStream s = null;
		try {
			s = new FileInputStream(SETTINGS_FILE);
			manager.load(s);
		} catch(FileNotFoundException e) {
			throw new SettingLoadException(Motive.SettingFileCompromized);
		} catch(IOException e) {
			throw new SettingLoadException(Motive.SettingFileCompromized);
		} finally {
			if(s != null) {
				try {
					s.close();
				} catch (IOException e) {
					// Ignoro
				}
			}
		}
	}
	
	
	
	/**
	 * Salva le impostazioni dei manager
	 * @param manager Manager delle maschere
	 * @throws IOException In caso di errore nel salvataggio
	 */
	public static void saveSettings(MasksManager manager) throws IOException {
		Objects.requireNonNull(manager);
		
		OutputStream s = new FileOutputStream(SETTINGS_FILE);
		manager.save(s);
		s.close();
	}
}
