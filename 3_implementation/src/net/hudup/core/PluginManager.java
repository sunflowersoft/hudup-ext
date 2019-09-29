/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;

import net.hudup.core.alg.Alg;
import net.hudup.core.alg.AlgRemote;
import net.hudup.core.alg.AlgRemoteWrapper;
import net.hudup.core.alg.AlgDesc.FunctionType;
import net.hudup.core.alg.AlgDesc.MethodType;
import net.hudup.core.data.ExternalQuery;
import net.hudup.core.data.ctx.CTSManager;
import net.hudup.core.evaluate.Metric;
import net.hudup.core.logistic.BaseClass;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.xURI;
import net.hudup.core.parser.DatasetParser;

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
public interface PluginManager {
	
	
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
	 * Getting a list of instances belonging referred class. Packages are specified in Hudup property file.
	 * This method does not affect plug-in storage.
	 * @param <T> type of returned instances.
	 * @param referredClass referred class. If this referred class is null, all classes are retrieved. 
	 * @return list of instances belonging referred class.
	 */
	<T> List<T> discover(Class<T> referredClass);

	
	/**
	 * Getting a list of instances from store and referred class. Packages specified in Hudup property file are not used because this method uses store URI.
	 * This method does not affect plug-in storage.
	 * @param <T> object type.
	 * @param storeUri store URI. If this store URI is null, current directory is used.
	 * @param referredClass referred class. If this referred class is null, all classes are retrieved.
	 * @return list of algorithms from store and referred class.
	 */
	<T> List<T> discover(xURI storeUri, Class<T> referredClass);

	
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
	 * Getting methodological type of given algorithm.
	 * @param alg given algorithm.
	 * @return methodological type of given algorithm.
	 */
	MethodType methodTypeOf(Alg alg);

	
	/**
	 * Getting functional type of given algorithm.
	 * @param alg given algorithm.
	 * @return functional type of given algorithm.
	 */
	FunctionType functionTypeOf(Alg alg);

	
	/**
	 * Registering the specified algorithm into respective register table of {@link PluginStorage}.
	 * For example, a recommendation algorithm will be added into recommender register table returned by {@link PluginStorage#getRecommenderReg()}. 
	 * @param alg specified algorithm.
	 */
	static void registerAlg(Alg alg) {

		RegisterTable normalAlgReg = PluginStorage.getNormalAlgReg();
		RegisterTable metricReg = PluginStorage.getMetricReg();
		RegisterTable parserReg = PluginStorage.getParserReg();
		RegisterTable externalQueryReg = PluginStorage.getExternalQueryReg();
		RegisterTable ctsmReg = PluginStorage.getCTSManagerReg();

		boolean registered = false;
		if (alg instanceof DatasetParser)
			registered = parserReg.register( (DatasetParser)alg );
		else if (alg instanceof Metric) {
			// MetaMetric and MetricWrapper are annotated as BaseClass and so they are not registered
			registered = metricReg.register( (Metric)alg );
		}
		else if (alg instanceof ExternalQuery)
			registered = externalQueryReg.register( (ExternalQuery)alg );
		else if (alg instanceof CTSManager)
			registered = ctsmReg.register( (CTSManager)alg );
		else
			registered = normalAlgReg.register(alg);

		if (registered)
			LogUtil.info("Registered algorithm: " + alg.getName());
	}
	
	
	/**
	 * Checking whether specified class is valid.
	 * @param cls specified class.
	 * @return whether specified class is valid.
	 */
	static boolean isClassValid(Class<?> cls) {
		if (cls == null || cls.isInterface() || cls.isMemberClass() || cls.isAnonymousClass())
			return false;
		
		int modifiers = cls.getModifiers();
		if ( (modifiers & Modifier.ABSTRACT) != 0 || (modifiers & Modifier.PUBLIC) == 0)
			return false;
		else if (cls.getAnnotation(BaseClass.class) != null || 
				cls.getAnnotation(Deprecated.class) != null) {
			return false;
		}
		else
			return true;
	}


	/**
	 * Getting methodological type of given algorithms.
	 * @param algs given algorithms.
	 * @return methodological type of given algorithms.
	 */
	public static MethodType methodTypeOf(Collection<Alg> algs) {
		if (algs == null || algs.size() == 0)
			return MethodType.unknown;
		
		PluginManager pm = Util.getPluginManager();
		boolean memorybased = true, modelbased = true, composite = true, service = true;
		for (Alg alg : algs) {
			MethodType type = pm.methodTypeOf(alg);
			if (type == MethodType.memorybased) {
				memorybased = memorybased && true;
				modelbased = modelbased && false;
				composite = composite && false;
				service = service && false;
			}
			else if (type == MethodType.modelbased) {
				memorybased = memorybased && false;
				modelbased = modelbased && true;
				composite = composite && false;
				service = service && false;
			}
			else if (type == MethodType.composite) {
				memorybased = memorybased && false;
				modelbased = modelbased && false;
				composite = composite && true;
				service = service && false;
			}
			else if (type == MethodType.service) {
				memorybased = memorybased && false;
				modelbased = modelbased && false;
				composite = composite && false;
				service = service && true;
			}
			else {
				memorybased = memorybased && false;
				modelbased = modelbased && false;
				composite = composite && false;
				service = service && false;
			}
		}
		
		if (memorybased)
			return MethodType.memorybased;
		else if (modelbased)
			return MethodType.modelbased;
		else if (composite)
			return MethodType.composite;
		else if (service)
			return MethodType.service;
		else
			return MethodType.unknown;
	}
	

	/**
	 * Getting functional type of given algorithms.
	 * @param algs given algorithms.
	 * @return functional type of given algorithms.
	 */
	public static FunctionType functionTypeOf(Collection<Alg> algs) {
		if (algs == null || algs.size() == 0)
			return FunctionType.unknown;
		
		PluginManager pm = Util.getPluginManager();
		boolean recommend = true, execute = true;
		for (Alg alg : algs) {
			FunctionType type = pm.functionTypeOf(alg);
			if (type == FunctionType.recommend) {
				recommend = recommend && true;
				execute = execute && false;
			}
			else if (type == FunctionType.execute) {
				recommend = recommend && false;
				execute = execute && true;
			}
			else {
				recommend = recommend && false;
				execute = execute && false;
			}
		}
		
		if (recommend)
			return FunctionType.recommend;
		else if (execute)
			return FunctionType.execute;
		else
			return FunctionType.unknown;
	}


}
