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
 * Static class to manage the settings
 */
public class Settings {

	private static final String SETTINGS_FILE = "settings.data";
	
	
	private Settings() { }
	
	
	
	/**
	 * Load the setting og the managers
	 * @param manager Mask manager
	 * @throws SettingLoadException In case of error in the loading
	 */
	public static void loadSettings(MasksManager manager) throws SettingLoadException {
		Objects.requireNonNull(manager);
		
		InputStream s = null;
		try {
			s = new FileInputStream(SETTINGS_FILE);
			manager.load(s);
		} catch(FileNotFoundException e) {
			throw new SettingLoadException(Motive.SettingFileNotFound);
		} catch(IOException e) {
			throw new SettingLoadException(Motive.SettingFileCompromized);
		} finally {
			if(s != null) {
				try {
					s.close();
				} catch (IOException e) {
					// Ignore
				}
			}
		}
	}
	
	
	
	/**
	 * Save the managers settings
	 * @param manager Setting manager
	 * @throws IOException In case of IO error
	 */
	public static void saveSettings(MasksManager manager) throws IOException {
		Objects.requireNonNull(manager);
		
		OutputStream s = new FileOutputStream(SETTINGS_FILE);
		manager.save(s);
		s.close();
	}
}
