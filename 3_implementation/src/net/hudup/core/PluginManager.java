/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core;

import java.io.InputStream;
import java.util.List;
import java.util.Set;

import net.hudup.core.alg.Alg;
import net.hudup.core.alg.AlgRemote;
import net.hudup.core.alg.AlgRemoteWrapper;
import net.hudup.core.data.DatasetRemote;
import net.hudup.core.data.DatasetRemoteWrapper;
import net.hudup.core.logistic.NextUpdate;
import net.hudup.core.logistic.xURI;

/**
 * Plug-in manager is responsible for discovering and managing registered recommendation algorithms.
 * The class that implements {@code PluginManager} is {@code Firer}.
 * {@code Alg} is the most abstract interface for all algorithms.
 * Every algorithm has a unique name. Every algorithm is registered in system register table and identified by its name.
 * Such system register table is modeled as {@code RegisterTable} class.
 * Actually, plug-in manager discovers automatically all algorithms via their names at the booting time and requires {@link PluginStorage} to add such algorithms into register tables represented by {@link RegisterTable} classes.
 * After Hudup framework started, it is easy to retrieve any any algorithms by looking up algorithms in register tables from {@link PluginStorage}.
 * Please pay attention that system register tables are stored in a so-called {@code plug-in storage} which is represented by {@code PluginStorage} class.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public interface PluginManager extends AutoCloseable {
	
	
//	/**
//	 * Look and feel.
//	 */
//	String[][] lookAndFeels = {
//			{"default", null},
//			{"metal", "javax.swing.plaf.metal.MetalLookAndFeel", "javax.swing.plaf.metal.DefaultMetalTheme, javax.swing.plaf.metal.OceanTheme"},
//			{"motif", "com.sun.java.swing.plaf.motif.MotifLookAndFeel"},
//			{"nimbus", "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel"},
//			{"system", UIManager.getSystemLookAndFeelClassName()},
//			{"windows", "com.sun.java.swing.plaf.windows.WindowsLookAndFeel"},
//			{"aqua", "com.apple.laf.AquaLookAndFeel"},
//			{"gtk", "com.sun.java.swing.plaf.gtk.GTKLookAndFeel"},
//	};
	
	
	/**
	 * This is the first method which needs to be called firstly for any Hudup application.
	 * This method is called one time at boot time for any Hudup application.
	 * In current implementation, there are two important tasks of fire method as follows:
	 * <ol>
	 * <li>
	 * Create default directories such as working directory and directory of knowledge base.
	 * </li>
	 * <li>
	 * Call method {@link #discover()} to discover and register all algorithms (from class path and libaray of Hudup framework) into {@link PluginStorage}.
	 * </li>
	 * </ol>
	 */
	void fire();

	
	/**
	 * This is the first method which needs to be called firstly for any Hudup application.
	 * However, it is simpler than {@link #fire()} method and it does not call {@link #discover()} method.
	 */
	void fireSimply();
	
	
	/**
	 * Testing whether {@link #fire()} method was fired.
	 * @return whether {@link #fire()} method was fired.
	 */
	boolean isFired();
	
	
	/**
	 * This method discovers automatically all algorithms via their names, which initializes and updates plug-in storage {@link PluginStorage}. Packages are specified in Hudup property file.
	 * If these packages are not specified, all packages are browsed.
	 */
	void discover();
	
	
	/**
	 * Discovering classes.
	 * @param <T> template type.
	 * @param referredClass referred classes.
	 */
	<T extends Alg> void discover(Class<T> referredClass);

		
	/**
	 * Getting classes from stores and referred class. Packages specified in Hudup property file are not used because this method uses store URI (s).
	 * This method does not affect plug-in storage.
	 * @param <T> object type.
	 * @param storeUris array of store URI (s). If this store URI is null or empty, current directory is used.
	 * @param referredClass referred class. If this referred class is null, all classes are retrieved.
	 * @return list of classes from stores which are sub-classes of referred class.
	 */
	<T> List<Class<? extends T>> loadClasses(Class<T> referredClass, xURI...storeUris);

	
	/**
	 * Getting instances from stores and referred class. Packages specified in Hudup property file are not used because this method uses store URI (s).
	 * This method does not affect plug-in storage.
	 * @param <T> object type.
	 * @param storeUris array of store URI (s). If this store URI is null or empty, current directory is used.
	 * @param referredClass referred class. If this referred class is null, all classes are retrieved.
	 * @return list of instances from stores and referred class.
	 */
	<T> List<T> loadInstances(Class<T> referredClass, xURI...storeUris);

	
	/**
	 * Loading (reloading) drivers.
	 */
	void loadDrivers();
	
	
	/**
	 * Wrapping a remote algorithm.
	 * @param remoteAlg remote algorithm.
	 * @param exclusive exclusive mode.
	 * @return wrapper of a remote algorithm.
	 */
	AlgRemoteWrapper wrap(AlgRemote remoteAlg, boolean exclusive);

	
	/**
	 * Wrapping a remote dataset.
	 * @param remoteDataset remote dataset.
	 * @param exclusive exclusive mode.
	 * @return wrapper of a remote dataset.
	 */
	DatasetRemoteWrapper wrap(DatasetRemote remoteDataset, boolean exclusive);

	
	/**
	 * Getting methodological type of given algorithm.
	 * @param alg given algorithm.
	 * @return integer as methodological type of given algorithm.
	 */
	int methodTypeOf(Alg alg);

	
	/**
	 * Getting functional type of given algorithm.
	 * @param alg given algorithm.
	 * @return integer as functional type of given algorithm.
	 */
	int functionTypeOf(Alg alg);

	
	/**
	 * Registering the specified algorithm into respective register table of {@link PluginStorage}.
	 * For example, a recommendation algorithm will be added into recommender register table returned by {@link PluginStorage#getNormalAlgReg()}. 
	 * @param alg specified algorithm.
	 */
	void registerAlg(Alg alg);
	
	
	/**
	 * Checking whether specified class is valid.
	 * @param cls specified class.
	 * @return whether specified class is valid.
	 */
	public boolean isClassValid(Class<?> cls);


	/**
	 * Checking whether specified algorithm is valid.
	 * @param alg specified algorithm.
	 * @return whether specified algorithm is valid.
	 */
	boolean isValidAlg(Alg alg);

	
	/**
	 * Getting all interfaces of given object and referred class.
	 * This method will be revised because its deep level is only 1.
	 * Please see: <a href="http://commons.apache.org/proper/commons-lang/javadocs/api-release/org/apache/commons/lang3/ClassUtils.html">http://commons.apache.org/proper/commons-lang/javadocs/api-release/org/apache/commons/lang3/ClassUtils.html</a>
	 * @param obj given object.
	 * @param <T> type of given object.
	 * @param referredClass referred class.
	 * @param includeReferredClass if true, including referred class.
	 * @return all interfaces of given object and referred class.
	 */
	@NextUpdate
	@SuppressWarnings("unchecked")
	static <T> Set<Class<? extends T>> getAllInterfaces(Object obj, Class<T>  referredClass, boolean includeReferredClass) {
		Set<Class<? extends T>> iSet = Util.newSet();
		
		Class<?>[] iClasses = obj.getClass().getInterfaces();
		for (Class<?> iClass : iClasses) {
			if (!referredClass.isAssignableFrom(iClass)) continue;
			
			if (includeReferredClass)
				iSet.add((Class<? extends T>) iClass);
			else if (!referredClass.equals(iClass))
				iSet.add((Class<? extends T>) iClass);
		}
		
		return iSet;
	}
	

	/**
	 * Loading class from specified class name.
	 * @param name specified class name.
	 * @param initialized initialization flag.
	 * @return class loaded from specified class name.
	 * @throws ClassNotFoundException if class is not found.
	 */
	Class<?> loadClass(String name, boolean initialized) throws ClassNotFoundException;
	
	
	/**
	 * Getting resource as stream.
	 * @param name resource name.
	 * @return resource as stream.
	 */
	InputStream getResourceAsStream(String name);
	
	
}
