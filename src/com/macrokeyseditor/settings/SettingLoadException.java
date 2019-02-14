package com.macrokeyseditor.settings;

import java.util.Objects;

public class SettingLoadException extends Exception {
	
	private final Motive motive;
	
	
	/**
	 * @param motive Motivo dell'errore
	 */
	public SettingLoadException(Motive motive) {
		Objects.requireNonNull(motive);
		this.motive = motive;
	}
	
	
	
	/**
	 * @return Motivo dell'errore
	 */
	public Motive getMotive() {
		return motive;
	}
	
	
	
	public enum Motive {
		/** File d'impostazione non trovata */
		SettingFileNotFound,
		/** File d'impostazione non formato correttamente */
		SettingFileCompromized
	}
}
