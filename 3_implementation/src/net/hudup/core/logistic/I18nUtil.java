/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.data.PropList;

/**
 * This final utility class provides utilit methods for internationalization (I18n) for different languages.
 * The I18n depends on both language and country; for example, English language &quot;EN&quot; for United States &quot;US&quot; different English language &quot;EN&quot; for United Kingdom &quot;GB&quot;. 
 * It aims how changes texts on graphic user interface (GUI) components according to different languages; which is mechanism of localization.
 * It contains the internal variable {@link #properties} storing configuration for localization. Such properties list is called I18n properties list.
 * It also declares public constants as default customizations for localization.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public final class I18nUtil {
	
	
	/**
	 * Key name of language entry in properties.
	 */
	public static String LANGUAGE                   = "language";
	
	/**
	 * Key name of country entry in properties.
	 */
	public static String COUNTRY                    = "country";
	
	/**
	 * Default language over Hudup framework. That programmers change this constant will change the apparent default language over Hudup framework.
	 */
	protected static String DEFAULT_LANGUAGE           = "en"; //"vi"
	
	/**
	 * Default country over Hudup framework. That programmers change this constant will change the apparent default language over Hudup framework.
	 */
	protected static String DEFAULT_COUNTRY            = "US"; //"VN"
	
	/**
	 * The name of the bundle of I18n properties. By default, it is &quot;hudup_messages&quot;.
	 */
	private static String DEFAULT_MESSAGE_NAME       = "hudup_messages";
	
	/**
	 * The first base of the bundle of I18n messages within the class path.
	 * For example, given the base &quot;/net/hudup/hudup_messages&quot;, the bundle of I18n messages with English language (US) is located at &quot;/net/hudup/hudup_messages_en_US.properties&quot;.
	 */
	private static String DEFAULT_BUNDLE_BASE_NAME   = Constants.RESOURCES_PACKAGE + DEFAULT_MESSAGE_NAME;
	
	/**
	 * The second base of the bundle of I18n messages within the class path.
	 */
	private static String DEFAULT_BUNDLE_BASE_NAME2   = Constants.ROOT_PACKAGE + DEFAULT_MESSAGE_NAME;
	
	/**
	 * The third base of the bundle of I18n messages within the class path.
	 */
	private static String DEFAULT_BUNDLE_BASE_NAME3   = "/" + DEFAULT_MESSAGE_NAME;
	
	
	/**
	 * System i18n properties list.
	 */
	private static PropList sysProperties =  new PropList();
	
	/**
	 * Loading system i18n messages bundle
	 */
	static {
		try {
			String language = Util.getHudupProperty(LANGUAGE);
			String country = Util.getHudupProperty(COUNTRY);
			loadMessages(sysProperties, language, country);
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
	}
	
	
	/**
	 * I18n properties list, also called I18n message bundle, represented by {@link Properties} class.
	 */
	protected PropList properties = new PropList();
	
	
	/**
	 * Default constructor.
	 */
	public I18nUtil() {
		load();
	}

	
	/**
	 * Constructor with specified language and country.
	 * @param language specified language.
	 * @param country specified country.
	 */
	public I18nUtil(String language, String country) {
		load(language, country);
	}
	
	
	/**
	 * Constructor with specified configuration. Note, {@link PropList} can be exchanged with {@link Properties}.
	 * @param config specified configuration.
	 */
	public I18nUtil(PropList config) {
		load();
	}

	
	/**
	 * Loading system message bundle.
	 */
	public void load() {
		properties.clear();
		properties.putAll(sysProperties);
	}

	
	/**
	 * Loading message bundle with specified language and country.
	 * @param language specified language.
	 * @param country specified country.
	 */
	public void load(String language, String country) {
		properties.clear();
		if (language == null || country == null)
			load();
		else
			loadMessages(properties, language, country);
	}
	
	
	/**
	 * Loading message bundle with specified configuration.
	 * @param config specified configuration.
	 */
	public void load(PropList config) {
		if (config != null)
			load(config.getAsString(LANGUAGE), config.getAsString(COUNTRY));
	}

	
	/**
	 * Getting localized message with specified key.
	 * This method also supports internationalization (I18n) and so the returned message is localized according to the pre-defined language.
	 * @param key specified key.
	 * @return localized message with specified key.
	 */
	public String getMessage(String key) {
		return getMessage0(properties, key);
	}
	
	
	/**
	 * Getting localized message with specified key.
	 * @param key specified key.
	 * @return localized message with specified key.
	 */
	public static String message(String key) {
		return getMessage0(sysProperties, key);
	}
	
	
	/**
	 * Getting localized message with specified key.
	 * @param properties i18n messages bundle.
	 * @param key specified key.
	 * @return localized message with specified key.
	 */
	private static String getMessage0(PropList properties, String key) {
		if (key == null || key.isEmpty())
			return "no message";
		else if (!properties.containsKey(key))
			return key;
		else {
			try {
				String value = properties.getAsString(key);
				if (value == null)
					return key;
				else
					return value;
			} 
			catch (Throwable e) {
				LogUtil.trace(e);
			}
			
			return key;
		}
	}
	
	
//	/**
//	 * This static method gets localized message with specified key in specified {@link PropList}.
//	 * 
//	 * @param config specified {@link PropList}.
//	 * @param key specified key.
//	 * @return localized message with specified key.
//	 */
//	public static String getMessage(PropList config, String key) {
//		if (config == null)
//			return key;
//		
//		try {
//			I18nUtil i18n = new I18nUtil(config);
//			return i18n.getMessage(key);
//		}
//		catch (Throwable e) {
//			LogUtil.trace(e);
//		}
//		
//		return key;
//	}

	
	/**
	 * Loading I18n bundle properties with specified language and specified country.
	 * @param properties I18n bundle properties
	 * @param language specified language.
	 * @param country specified country.
	 */
	private static void loadMessages(PropList properties, String language, String country) {
		Properties temp = new Properties();
		loadMessages0(DEFAULT_BUNDLE_BASE_NAME, temp, language, country);
		loadMessages0(DEFAULT_BUNDLE_BASE_NAME2, temp, language, country);
		loadMessages0(DEFAULT_BUNDLE_BASE_NAME3, temp, language, country);
		
		properties.putAll(temp);
		temp.clear();
	}

	
	/**
	 * Loading I18n bundle properties with specified language and specified country.
	 * @param baseName base of the bundle of I18n messages within the class path.
	 * @param properties I18n bundle properties
	 * @param language specified language.
	 * @param country specified country.
	 */
	private static void loadMessages0(String baseName, Properties properties, String language, String country) {
		try {
			ResourceBundle bundle = getMessageBundle(baseName, language, country);
			if (bundle == null) return;
			
			Enumeration<String> keys = bundle.getKeys();
			
			while (keys.hasMoreElements()) {
				String key = keys.nextElement();
				String value = bundle.getString(key);
				if (value != null && !value.isEmpty())
					properties.put(key, value);
			}
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
	}
	
	
	/**
	 * This static method loads I18n message bundle as the returned result given base name, language, and country.
	 * @param baseName base of the bundle of I18n messages within the class path.
	 * For example, given the base &quot;/net/hudup/hudup_messages&quot;, the bundle of I18n messages with English language (US) is located at &quot;/net/hudup/hudup_messages_en_US.properties&quot;.
	 * @param language specified language.
	 * @param country specified country.
	 * @return message bundle {@link ResourceBundle}.
	 */
	private static ResourceBundle getMessageBundle(String baseName, String language, String country) {
		ResourceBundle bundle = getMessageBundle0(baseName, language, country);
		if (bundle == null)
			bundle = getMessageBundle0(baseName, language, null);
		if (bundle == null)
			bundle = getMessageBundle0(baseName, null, null);
		
		return bundle;
	}

	
	/**
	 * This static method loads I18n message bundle as the returned result given base name, language, and country.
	 * This method uses {@link UTF8Control} for reading localized messages.
	 * This method is called by {@link #getMessageBundle(String, String, String)}.
	 * @param baseName base of the bundle for I18n messages within the class path.
	 * For example, given the base &quot;/net/hudup/hudup_messages&quot;, the bundle of I18n messages with English language (US) is located at &quot;/net/hudup/hudup_messages_en_US.properties&quot;.
	 * @param language specified language.
	 * @param country specified country.
	 * @return message bundle {@link ResourceBundle}.
	 */
	private static ResourceBundle getMessageBundle0(String baseName, String language, String country) {
		ResourceBundle bundle = null;
		try {
			language = language == null || language.isEmpty() ? DEFAULT_LANGUAGE : language;
			Locale locale = country == null || country.isEmpty() ? new Locale(language) : new Locale(language, country);
			
			baseName = baseName == null || baseName.isEmpty() ? DEFAULT_BUNDLE_BASE_NAME : baseName;
			baseName = UriAdapter.packageSlashToDot(baseName);
			bundle = ResourceBundle.getBundle(baseName, locale, new UTF8Control());
		}
		catch (MissingResourceException e) {
			bundle = null;
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			bundle = null;
		}
		
		return bundle;
	}
	
	
	/**
	 * The {@link Control} for reading I18n message bundle.
	 * 
	 * @author Loc Nguyen
	 * @version 10.0
	 *
	 */
	private static class UTF8Control extends Control {

		@Override
		public ResourceBundle newBundle(String baseName, Locale locale,
				String format, ClassLoader loader, boolean reload)
				throws IllegalAccessException, InstantiationException,
				IOException {
			
			String resourceName = toResourceName(toBundleName(baseName, locale), "properties");
			InputStream is = null;
			if (reload) {
				URL url = loader.getResource(resourceName);
				if (url != null) {
					URLConnection conn = url.openConnection();
					if (conn != null) {
						conn.setUseCaches(false);
						is = conn.getInputStream(); 
					}
						
				}
			}
			else {
				is = loader.getResourceAsStream(resourceName);
			}
			
			if (is == null)
				return null;
			
			ResourceBundle bundle = null;
			try {
				bundle = new PropertyResourceBundle(new InputStreamReader(is, "utf-8"));
			}
			catch (Exception e) {
				LogUtil.trace(e);
			}
			finally {
				is.close();
			}
			
			
			return bundle;
		}
		
	}
	
	
}
