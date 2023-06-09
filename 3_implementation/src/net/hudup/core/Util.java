/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
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

import net.hudup.core.factory.Factory;
import net.hudup.core.factory.FactoryImpl;
import net.hudup.core.logistic.LogUtil;
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
public class Util {

	
	/**
	 * Default relative working directory.
	 */
	public final static String  WORKING_DIRECTORY  = Configuration.getProperty(Configuration.WORKING_NAME, Configuration.WORKING_NAME);

	
	/**
	 * The root package (root directory) of all classes.
	 */
	public final static String  ROOT_PACKAGE = Configuration.ROOT_PACKAGE; 

	
	/**
	 * The resources directory that contains any resources except classes such as images, template files.
	 */
	public final static String  RESOURCES_PACKAGE  = Configuration.RESOURCES_PACKAGE;

	
	/**
	 * System properties.
	 */
	public final static int MAX_PROPS_FILES = 10;

	
	/**
	 * Hudup properties name.
	 */
	public final static String hudupPropName = "hudup.properties";
	
	
	/**
	 * Second Hudup temporal properties name.
	 */
	public final static String hudupTemplatePropName = hudupPropName + ".template";
	

	/**
	 * Hudup temporal properties name.
	 */
	public final static String hudupBackupPropName = hudupPropName + ".backup";
	
	
	/**
	 * Hudup test properties name.
	 */
	public final static String hudupTestPropName = hudupPropName + ".test";
	
	
	/**
	 * System properties.
	 */
	protected final static Properties props = new Properties();
	
	
	/**
	 * Plug-in manager.
	 */
	private static PluginManager pluginManager = null;
	
	
	/**
	 * Loading system properties.
	 */
	static {

		try {
			loadProperties();
		}
		catch (Throwable e) {LogUtil.trace(e);}
		
		try {
			String pluginManagerText = getHudupProperty("plugin_manager");
			pluginManager = (PluginManager) Class.forName(pluginManagerText).getDeclaredConstructor().newInstance();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			pluginManager = new Firer();
		}
	}
	
	
	/**
	 * Loading Hudup properties.
	 */
	private static void loadProperties() {
		Path lastPath = null;
		Properties lastProps = null;
		for (int i = 0; i <= MAX_PROPS_FILES; i++) {
			String path = i == 0 ? RESOURCES_PACKAGE + hudupPropName : ROOT_PACKAGE + hudupPropName + "." + i;
			InputStream in = null;
			try {
				in = Util.class.getResourceAsStream(path);
			}
			catch (Throwable e) {in = null;}
			if (in == null) continue;
			
			try {
				Properties userProps = new Properties();
				userProps.load(in);
				props.putAll(userProps);
				
				lastProps = userProps;
				lastPath = Paths.get(Util.class.getResource(path).toURI());
			}
			catch (Throwable e) {}
			try {
				if (in != null) in.close();
			}
			catch (Throwable e) {}
		}
		
		Path workingPath = Paths.get(WORKING_DIRECTORY + "/" + hudupPropName);
		if (Files.exists(workingPath)) {
			InputStream in = null;
			try {
				in = Files.newInputStream(workingPath);
				Properties userProps = new Properties();
				userProps.load(in);
				props.putAll(userProps);
			}
			catch (Exception e) {}
			
			try {
				if (in != null) in.close();
			}
			catch (Throwable e) {}
		}
		else {
			try {
				Path workingDir = Paths.get(WORKING_DIRECTORY);
				if (!Files.exists(workingPath)) Files.createDirectory(workingDir);
				
				OutputStream out = Files.newOutputStream(workingPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
				new Properties().store(out, "Adding your own properties");
				out.close();
			}
			catch (Exception e) {}
			
			boolean copied = false;
			if (lastPath != null) {
				try {
					Files.copy(lastPath,
						Paths.get(WORKING_DIRECTORY + "/" + hudupTemplatePropName),
						StandardCopyOption.REPLACE_EXISTING);
					copied = true;
				}
				catch (Exception e) {}
			}
			
			if ((!copied) && (lastProps != null)) {
				try {
					OutputStream out = Files.newOutputStream(
						Paths.get(WORKING_DIRECTORY + "/" + hudupBackupPropName),
						StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
					lastProps.store(out, "Following are last properties");
					out.close();
				}
				catch (Exception e) {}
			}
		}
		
	}

	
	/**
	 * Creating a new array.
	 * @param <T> element type.
	 * @param tClass element type class.
	 * @param length array length.
	 * @return new array.
	 */
	public static <T> T[] newArray(Class<T> tClass, int length) {
		@SuppressWarnings("unchecked")
		T[] array = (T[]) Array.newInstance(tClass, length);
		
		return array;
	}
	
	
	/**
	 * Creating a new array.
	 * @param <T> element type.
	 * @param tClass element type class.
	 * @param length array length.
	 * @param initialValue initial value.
	 * @return new array
	 */
	public static <T> T[] newArray(Class<T> tClass, int length, T initialValue) {
		T[] array = newArray(tClass, length);
		for (int i = 0; i < length; i++) array[i] = initialValue;
		
		return array;
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
			instance = tClass.getDeclaredConstructor().newInstance();
		} 
		catch (Exception e) {
			LogUtil.trace(e);
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
					instance = getPluginManager().loadClass(className, true).getDeclaredConstructor().newInstance();
				}
				catch (Throwable e) {
					instance = null;
					System.out.println("Cannot instantiate inner class " + className);
				}
			}
			else
				instance = Util.getPluginManager().loadClass(className, true).getDeclaredConstructor().newInstance();
		} 
		catch (Throwable e) {
			LogUtil.trace(e);
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
		return !Double.isNaN(value);
	}

	
	/**
	 * Checking whether all values in specified array are used.
	 * @param values specified array.
	 * @return whether all values in specified array are used.
	 */
	public static boolean isUsedAll(double[] values) {
		if (values == null || values.length == 0) return false;
		for (double value : values) {
			if (!isUsed(value)) return false;
		}
		return true;
	}
	
	
	/**
	 * Checking whether all values in specified collection are used.
	 * @param values specified collection.
	 * @return whether all values in specified collection are used.
	 */
	public static boolean isUsedAll(Collection<Double> values) {
		if (values == null || values.size() == 0) return false;
		for (double value : values) {
			if (!isUsed(value)) return false;
		}
		return true;
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
				return (Factory)Util.getPluginManager().loadClass(factoryClassName, true).getDeclaredConstructor().newInstance();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
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
				return (Cipher)Util.getPluginManager().loadClass(cipherClassName, true).getDeclaredConstructor().newInstance();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
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
				return (JsonParser)Util.getPluginManager().loadClass(jsonParserClassName, true).getDeclaredConstructor().newInstance();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
		return new JsonParserImpl();
	}

	
	/**
	 * Getting plug-in manager.
	 * @return plug-in manager.
	 */
	public static PluginManager getPluginManager() {
		return pluginManager;
	}

	
	/**
	 * Getting array of loadable packages.
	 * @return array of loadable packages.
	 */
	public static String[] getLoadablePackages() {
		List<String> prefixList = Util.newList();
		String rootPackage = UriAdapter.packageSlashToDot(ROOT_PACKAGE);
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
