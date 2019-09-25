/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core;

import static net.hudup.core.Constants.RESOURCES_PACKAGE;
import static net.hudup.core.Constants.ROOT_PACKAGE;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;
import java.util.Vector;

import net.hudup.core.alg.AlgUtil;
import net.hudup.core.alg.AlgUtilImpl;
import net.hudup.core.factory.Factory;
import net.hudup.core.factory.FactoryImpl;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;
import net.hudup.core.parser.JsonParser;
import net.hudup.core.parser.JsonParserImpl;
import net.hudup.core.parser.TextParserUtil;
import net.hudup.core.security.Cipher;
import net.hudup.core.security.CipherImpl;

/**
 * This final class provides important static utility methods to create and initialize essential data structures used over Hudup framework such as {@link Vector}, {@link List}, {@link Set}, {@link Map}, and {@link Queue}.
 * For example, if programmers want to create a {@link List} at anywhere in Hudup framework, they need to call the static method {@link #newList()} instead of calling {@code new ArrayList<T>()}.
 * The idea behind this class is that if programmers can used an improved data structure like {@code ImprovedList} over the framework, they just modifies the method {@link #newList()} to return such {@code ImprovedList}. 
 * Moreover this class also provides advanced utility methods.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public final class Util {

	
	/**
	 * System properties.
	 */
	private final static Properties props = new Properties();
	
	/**
	 * Hudup properties name.
	 */
	private final static String hudupPropName = "hudup.properties";
	
	/**
	 * Loading system properties.
	 */
	static {
		try {
			InputStream in = Util.class.getResourceAsStream(RESOURCES_PACKAGE + hudupPropName);
			if (in != null) {
				props.load(in);
				in.close();
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
		}

		
		try {
			Properties userProps = new Properties();
			InputStream in = Util.class.getResourceAsStream(ROOT_PACKAGE + hudupPropName);
			if (in != null) {
				userProps.load(in);
				in.close();
				props.putAll(userProps);
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		
		try {
			Properties userProps = new Properties();
			InputStream in = Util.class.getResourceAsStream("/" + hudupPropName);
			if (in != null) {
				userProps.load(in);
				in.close();
				props.putAll(userProps);
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
		}

	}
	
	
	/**
	 * Creating a new {@link Vector}.
	 * @param <T> type of elements in vector.
	 * @return new {@link Vector}.
	 */
	public static <T> Vector<T> newVector() {
	    return new Vector<T>();
	}

	
	/**
	 * Creating a new {@link Vector} with initial capacity.
	 * @param <T> type of elements in vector.
	 * @param initialCapacity initial capacity of this vector.
	 * @return new {@link Vector}.
	 */
	public static <T> Vector<T> newVector(int initialCapacity) {
	    return new Vector<T>(initialCapacity);
	}

	
	/**
	 * Creating a new {@link List}.
	 * @param <T> type of elements in list.
	 * @return new {@link List}.
	 */
	public static <T> List<T> newList() {
	    return new ArrayList<T>();
	}

	
	/**
	 * Creating a new {@link List} with initial capacity.
	 * @param <T> type of elements in list.
	 * @param initialCapacity initial capacity of this list.
	 * @return new {@link List}.
	 */
	public static <T> List<T> newList(int initialCapacity) {
	    return new ArrayList<T>(initialCapacity);
	}

	
	/**
	 * Creating a new {@link Set}.
	 * @param <T> type of elements in set.
	 * @return new {@link Set}.
	 */
	public static <T> Set<T> newSet() {
	    return new HashSet<T>();
	}

	
	/**
	 * Creating a new {@link Set} with initial capacity.
	 * @param <T> type of elements in set.
	 * @param initialCapacity initial capacity of this list.
	 * @return new {@link Set}.
	 */
	public static <T> Set<T> newSet(int initialCapacity) {
	    return new HashSet<T>(initialCapacity);
	}

	
	/**
	 * Creating a new {@link Map}.
	 * @param <K> type of key.
	 * @param <V> type of value.
	 * @return new {@link Map}.
	 */
	public static <K, V> Map<K, V> newMap() {
	    return new HashMap<K, V>();
	}

	
	/**
	 * Creating a new {@link Map}.
	 * @param <K> type of key.
	 * @param <V> type of value.
	 * @param initialCapacity initial capacity of this list.
	 * @return new {@link Map}.
	 */
	public static <K, V> Map<K, V> newMap(int initialCapacity) {
	    return new HashMap<K, V>(initialCapacity);
	}

	
	/**
	 * Creating a new {@link Queue}.
	 * @param <T> type of elements in queue.
	 * @return new {@link Queue}.
	 */
	public static <T> Queue<T> newQueue() {
		return new LinkedList<T>();
	}
	
	
	/**
	 * Creating a new object based on it class.
	 * @param <T> type of instance.
	 * @param tClass specified class.
	 * @return new object of the specified class.
	 */
	public static <T> T newInstance(Class<T> tClass) {
		T instance = null;
		try {
			instance = tClass.newInstance();
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return instance;
	}
	
	
	/**
	 * Creating a new object based on it class name.
	 * @param className specified class name.
	 * @return new object of the specified class name.
	 */
	public static Object newInstance(String className) {
		
		Object instance = null;
		try {
			if (className.contains("$")) {
				try {
					instance = Class.forName(className).newInstance();
				}
				catch (Throwable e) {
					System.out.println("Cannot instantiate inner class " + className);
					instance = null;
				}
			}
			else
				instance = Class.forName(className).newInstance();
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			instance = null;
		}
		
		return instance;
	}
	
	
	/**
	 * Deep cloning the specified object.
	 * @param obj specified object.
	 * @return new object cloned from the specified object.
	 */
	public final static Object clone(Object obj) {
		if (obj == null)
			return null;
		else if (obj instanceof Boolean)
			return (Boolean) ((Boolean)obj).booleanValue();
		else if (obj instanceof Byte)
			return (Byte) ((Byte)obj).byteValue();
		else if (obj instanceof Short)
			return (Short) ((Short)obj).shortValue();
		else if (obj instanceof Integer)
			return (Integer) ((Integer)obj).intValue();
		else if (obj instanceof Long)
			return (Long) ((Long)obj).longValue();
		else if (obj instanceof Float)
			return (Float) ((Float)obj).floatValue();
		else if (obj instanceof Double)
			return (Double) ((Double)obj).doubleValue();
		else if (obj instanceof Character)
			return (Character) ((Character)obj).charValue();
		else if (obj instanceof String)
			return new String(((String)obj));
		else if (obj instanceof Date)
			return new Date(((Date)obj).getTime());
		else if (obj instanceof File)
			return new File( ((File)obj).getAbsolutePath());
		else if (obj instanceof xURI)
			return ((xURI)obj).clone(); 
		else if (obj instanceof Path) {
			String textPath = ((Path)obj).toString();
			UriAdapter adapter = new UriAdapter(textPath);
			Path path = adapter.newPath(textPath);
			adapter.close();
			
			return path;
		}
		else if (obj instanceof Cloneable) {
			return ((Cloneable)obj).clone();
		}
		else
			return obj;
	}
	
	
	/**
	 * As a definition, a value is called {@code used} if it is not {@link Constants#UNUSED}; otherwise it is called {@code unused}.
	 * This method checks whether or not the specified value is used.
	 * @param value specified value.
	 * @return whether or not the specified value is used.
	 */
	public static boolean isUsed(double value) {
		return (!(Double.isNaN(value))) && (value != Constants.UNUSED);
	}

	
	/**
	 * Getting property of specified key in Hudup property &quot;hudup.properties&quot;.
	 * @param key specified key
	 * @return property of specified key in Hudup property &quot;hudup.properties&quot;.
	 */
	public static String getHudupProperty(String key) {
		return props.getProperty(key);
	}

	
	/**
	 * Getting factory to create associators, for example.
	 * @return factory to create associators, for example.
	 */
	public static Factory getFactory() {
		try {
			String factoryClassName = getHudupProperty("factory");
			if (factoryClassName == null)
				return new FactoryImpl();
			else
				return (Factory)Class.forName(factoryClassName).newInstance();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		
		return new FactoryImpl();
	}
	
	
	/**
	 * Getting cipher utility.
	 * @return cipher utility.
	 */
	public static Cipher getCipher() {
		try {
			String cipherClassName = getHudupProperty("cipher");
			if (cipherClassName == null)
				return new CipherImpl();
			else
				return (Cipher)Class.forName(cipherClassName).newInstance();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		
		return new CipherImpl();
	}

	
	/**
	 * Getting JSON parser.
	 * @return JSON parser.
	 */
	public static JsonParser getJsonParser() {
		try {
			String jsonParserClassName = getHudupProperty("jsonparser");
			if (jsonParserClassName == null)
				return new JsonParserImpl();
			else
				return (JsonParser)Class.forName(jsonParserClassName).newInstance();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		
		return new JsonParserImpl();
	}

	
	/**
	 * Getting algorithm utility.
	 * @return algorithm utility.
	 */
	public static AlgUtil getAlgUtil() {
		try {
			String algUtilClassName = getHudupProperty("alg_util");
			if (algUtilClassName == null)
				return new AlgUtilImpl();
			else
				return (AlgUtil)Class.forName(algUtilClassName).newInstance();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		
		return new AlgUtilImpl();
	}

	
	/**
	 * Getting array of loadable packages.
	 * @return array of loadable packages.
	 */
	public static String[] getLoadablePackages() {
		List<String> prefixList = Util.newList();
		String rootPackage = UriAdapter.packageSlashToDot(Constants.ROOT_PACKAGE);
		prefixList.add(rootPackage);
		
		//Load additional packages.
		String pkgProp = getHudupProperty("additional_packages");
		if (pkgProp != null) {
			List<String> pkgs = TextParserUtil.split(pkgProp, ",", null);
			for (String pkg : pkgs) {
				pkg = UriAdapter.packageSlashToDot(pkg);
				if (pkg != null && !pkg.isEmpty() && !pkg.equals(rootPackage))
					prefixList.add(pkg);
			}
		}
		
		return prefixList.toArray(new String[] {});
	}
	
	
}
