package com.macrokeyseditor.settings;

import java.util.Objects;

public class SettingLoadException extends Exception {
	
	private final Motive motive;
	
	
	/**
	 * @param motive Reason of the error
	 */
	public SettingLoadException(Motive motive) {
		Objects.requireNonNull(motive);
		this.motive = motive;
	}
	
	
	
	/**
	 * @return Reason of the error
	 */
	public Motive getMotive() {
		return motive;
	}
	
	
	
	public enum Motive {
		/** Setting file not found */
		SettingFileNotFound,
		
		/** Setting file not formatted correctly */
		SettingFileCompromized
	}
}
