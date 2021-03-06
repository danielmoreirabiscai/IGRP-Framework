package nosi.core.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Marcel Iekiny
 * Oct 27, 2017
 */
public final class I18nFactory {

	private I18nFactory() {}
	
	public static I18n createI18n(String name, String path) { //  
		try {
			String []aux = path.split("\\.");
			aux = aux[aux.length-1].split("_");
			Locale locale = new Locale(aux[0], aux[1]);
			ResourceBundle bundle = ResourceBundle.getBundle(path, locale);
			I18n i18n = new I18n(name, bundle);
			return i18n;
		}catch(Exception e) { // MissingResourceException 
			e.printStackTrace();
		}
		return null;
	}
}
