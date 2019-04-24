package br.com.juliano.gui;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Load the i18n files.
 * 
 * @author Juliano R. Américo
 *
 */
public class ConfigFile {

	private static ResourceBundle resourceBundle;

	// Default file.
	static {
		resourceBundle = ResourceBundle.getBundle("Messages");
	}

	// Gets a text referenced by key.
	public static String getMessage(String key) {
		return resourceBundle.getString(key);
	}

	// Set new Locale and load file corresponding.
	public static void setLocale(Locale locale) {
		resourceBundle = ResourceBundle.getBundle("Messages", locale);
	}

}
