/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.evaluate.wrapper.adapter;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.hudup.core.Constants;
import net.hudup.core.data.Attribute.Type;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Profile;
import net.hudup.core.evaluate.wrapper.WConfig;
import net.hudup.core.logistic.MathUtil;
import net.hudup.core.parser.TextParserUtil;

/**
 * This is utility class to provide static utility methods. It is also adapter to other libraries.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class Util {
	
	
	/**
	 * The maximum number digits in decimal precision.
	 */
	public static int DECIMAL_PRECISION = 12;

	
	/**
	 * Default date format.
	 */
	public static String DATE_FORMAT = "yyyy-MM-dd HH-mm-ss";

	
	/**
	 * Static code.
	 */
	static {
		try {
			DECIMAL_PRECISION = Constants.DECIMAL_PRECISION;
		}
		catch (Throwable e) {}
		
		try {
			DATE_FORMAT = Constants.DATE_FORMAT;
		}
		catch (Throwable e) {}
	}

	
	/**
	 * Creating a new array.
	 * @param <T> element type.
	 * @param tClass element type.
	 * @param length array length.
	 * @return new array
	 */
	public static <T> T[] newArray(Class<T> tClass, int length) {
		return net.hudup.core.Util.newArray(tClass, length);
	}

	
	/**
	 * Creating a new list with initial capacity.
	 * @param <T> type of elements in list.
	 * @param initialCapacity initial capacity of this list.
	 * @return new list with initial capacity.
	 */
	public static <T> List<T> newList(int initialCapacity) {
	    return net.hudup.core.Util.newList(initialCapacity);
	}
	
	
	/**
	 * Creating a new map.
	 * @param <K> type of key.
	 * @param <V> type of value.
	 * @param initialCapacity initial capacity of this list.
	 * @return new map.
	 */
	public static <K, V> Map<K, V> newMap(int initialCapacity) {
	    return net.hudup.core.Util.newMap(initialCapacity);
	}

	
	/**
	 * Converting the specified number into a string. The number of decimal digits is specified by {@link Constants#DECIMAL_PRECISION}.
	 * @param number specified number.
	 * @return text format of number of the specified number.
	 */
	public static String format(double number) {
		return MathUtil.format(number);
	}

	
	/**
	 * Converting a specified array of objects (any type) into a string in which each object is converted as a word in such string.
	 * @param <T> type of each object in the specified array.
	 * @param array Specified array of objects.
	 * @param sep The character that is used to connect words in the returned string. As usual, it is a comma &quot;,&quot;.
	 * @return Text form (string) of the specified array of objects, in which each object is converted as a word in such text form.
	 */
	public static <T extends Object> String toText(T[] array, String sep) {
		return TextParserUtil.toText(array, sep);
	}

	
	/**
	 * Converting a specified collection of objects (any type) into a string in which each object is converted as a word in such string.
	 * @param <T> type of each object in the specified collection.
	 * @param list Specified collection of objects.
	 * @param sep The character that is used to connect words in the returned string. As usual, it is a comma &quot;,&quot;.
	 * @return Text form (string) of the specified collection of objects, in which each object is converted as a word in such text form.
	 */
	public static <T extends Object> String toText(Collection<T> list, String sep) {
		return TextParserUtil.toText(list, sep);
	}

	
	/**
	 * Splitting a specified string into many words (tokens).
	 * @param source Specified string.
	 * @param sep The character (string) that is used for separation.
	 * @param remove the string which is removed from each tokens.
	 * @return list of words (tokens) from splitting the specified string.
	 */
	public static List<String> split(String source, String sep, String remove) {
		return TextParserUtil.split(source, sep, remove);
	}

	
	/**
	 * Splitting and parsing a specified string into many objects. The character (string) that is used for separation is specified by the parameter {@code sep} which follows Java regular expression.
	 * @param <T> type of returned objects.
	 * @param string specified string.
	 * @param type class of returned objects, which plays the role of template for parsing.
	 * @param sep character (string) that is used for separation.
	 * @return list of objects parsed from specified string.
	 */
	public static <T extends Object> List<T> parseListByClass(String string, Class<T> type, String sep) {
		return TextParserUtil.parseListByClass(string, type, sep);
	}

	
	/**
	 * Parsing (converting) a specified string into an object according to a class.
	 * @param string specified string.
	 * @param type class of the parsed object.
	 * @return parsed object.
	 */
	public static Object parseObjectByClass(String string, Class<?> type) {
		return TextParserUtil.parseObjectByClass(string, type);
	}

	
	/**
	 * Tracing error.
	 * @param e throwable error.
	 */
	public static void trace(Throwable e) {
		net.hudup.core.logistic.LogUtil.trace(e);
	}


	/**
	 * Converting Hudup profile into W profile.
	 * @param newAttRef W attributes.
	 * @param profile Hudup profile.
	 * @return W profile.
	 */
	public static net.hudup.core.evaluate.wrapper.Profile toWProfile(net.hudup.core.evaluate.wrapper.AttributeList newAttRef, Profile profile) {
		if (newAttRef == null || profile == null) return null;
		
		net.hudup.core.evaluate.wrapper.Profile newProfile = new net.hudup.core.evaluate.wrapper.Profile(newAttRef);
		int n = Math.min(newProfile.getAttCount(), profile.getAttCount());
		for (int i = 0; i < n; i++) {
			newProfile.setValue(i, profile.getValue(i));
		}
		
		return newProfile;
	}
	
	
	/**
	 * Converting Hudup profile into W profile.
	 * @param profile Hudup profile.
	 * @return W profile.
	 */
	public static net.hudup.core.evaluate.wrapper.Profile toWProfile(Profile profile) {
		net.hudup.core.evaluate.wrapper.AttributeList newAttRef = extractWAttributes(profile);
		return toWProfile(newAttRef, profile);
	}

		
	/**
	 * Extracting W attributes from Hudup profile.
	 * @param profile Hudup profile.
	 * @return list of W attributes.
	 */
	public static net.hudup.core.evaluate.wrapper.AttributeList extractWAttributes(Profile profile) {
		if (profile == null) return new net.hudup.core.evaluate.wrapper.AttributeList();
		
		net.hudup.core.evaluate.wrapper.AttributeList newAttRef = new net.hudup.core.evaluate.wrapper.AttributeList();
		for (int i = 0; i < profile.getAttCount(); i++) {
			Type type = profile.getAtt(i).getType();
			String name = profile.getAtt(i).getName();
			net.hudup.core.evaluate.wrapper.Attribute.Type newType = net.hudup.core.evaluate.wrapper.Attribute.Type.real;
			switch (type) {
			case bit:
				newType = net.hudup.core.evaluate.wrapper.Attribute.Type.bit;
				break;
			case nominal:
				newType = net.hudup.core.evaluate.wrapper.Attribute.Type.integer;
				break;
			case integer:
				newType = net.hudup.core.evaluate.wrapper.Attribute.Type.integer;
				break;
			case real:
				newType = net.hudup.core.evaluate.wrapper.Attribute.Type.real;
				break;
			case string:
				newType = net.hudup.core.evaluate.wrapper.Attribute.Type.string;
				break;
			case date:
				newType = net.hudup.core.evaluate.wrapper.Attribute.Type.date;
				break;
			case time:
				newType = net.hudup.core.evaluate.wrapper.Attribute.Type.time;
				break;
			case object:
				newType = net.hudup.core.evaluate.wrapper.Attribute.Type.object;
				break;
			}
			
			newAttRef.add(new net.hudup.core.evaluate.wrapper.Attribute(name, newType));
		}
		
		return newAttRef;
	}
	
	
	/**
	 * Convert Hudup configuration to W configuration.
	 * @param config Hudup configuration.
	 * @return W configuration.
	 */
	public static WConfig toWConfig(DataConfig config) {
		if (config == null) return new WConfig();
		
		WConfig wConfig = new WConfig();
		Set<String> keys = config.keySet();
		for (String key : keys) {
			if (wConfig.containsKey(key)) wConfig.put(key, config.get(key));
		}
		
		return wConfig;
	}


	/**
	 * Convert W configuration to Hudup configuration.
	 * @param wConfig W configuration.
	 * @return Hudup configuration.
	 */
	public static DataConfig toConfig(WConfig wConfig) {
		if (wConfig == null) return new DataConfig();
		
		DataConfig config = new DataConfig();
		Set<String> keys = wConfig.keySet();
		for (String key : keys) {
			config.put(key, wConfig.get(key));
		}
		
		return config;
	}


}
