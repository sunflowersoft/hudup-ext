/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core;

import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.reflections.Reflections;

import net.hudup.core.alg.Alg;
import net.hudup.core.alg.AlgList;
import net.hudup.core.data.DataDriver;
import net.hudup.core.data.DataDriverList;
import net.hudup.core.data.ExternalQuery;
import net.hudup.core.data.ctx.CTSManager;
import net.hudup.core.evaluate.Metric;
import net.hudup.core.logistic.BaseClass;
import net.hudup.core.logistic.Composite;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.NextUpdate;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.UriAdapter.AdapterWriter;
import net.hudup.core.logistic.UriFilter;
import net.hudup.core.logistic.UriProcessor;
import net.hudup.core.logistic.xURI;
import net.hudup.core.parser.DatasetParser;

/**
 * This class is the full implementation of {@link PluginManager}.
 * At the booting time, Hudup framework creates an {@link Firer} object to initialize important system information.
 * Actually, the constructor of {@link Firer} calls its method {@link #fire()} to initialize important system information.
 * In current implementation, there are two important tasks of method {@link #fire()} as follows:
 * <ol>
 * <li>
 * Method {@link #fire()} creates default directories such as working directory and directory of knowledge base.
 * </li>
 * <li>
 * Method {@link #fire()} continues to call method {@link #discover(String...)} to discover and register all algorithms (from class path and libaray of Hudup framework) into {@link PluginStorage}.
 * </li>
 * </ol> 
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class Firer implements PluginManager {

	
	/**
	 * Logger of next-update classes.
	 */
	protected AdapterWriter nextUpdateLog = null;
	
	
	/**
	 * This default constructor calls the method {@link #fire()} to initialize important system information.
	 */
	public Firer() {

		fire();
		
	}
	
	
	/**
	 * In current implementation, there are two important tasks of method {@link #fire()} as follows:
	 * <ol>
	 * <li>
	 * Method {@link #fire()} creates default directories such as working directory and directory of knowledge base.
	 * </li>
	 * <li>
	 * Method {@link #fire()} continues to call method {@link #discover(String...)} to discover and register all algorithms (from class path and libaray of Hudup framework) into {@link PluginStorage}.
	 * </li>
	 * </ol>
	 */
	protected void fire() {
		
		try {
			UriAdapter adapter = new UriAdapter(Constants.WORKING_DIRECTORY);
			
			xURI working = xURI.create(Constants.WORKING_DIRECTORY);
			if (!adapter.exists(working))
				adapter.create(working, true);
			
			xURI kb = xURI.create(Constants.KNOWLEDGE_BASE_DIRECTORY);
			if (!adapter.exists(kb))
				adapter.create(kb, true);
				
			xURI log = xURI.create(Constants.LOGS_DIRECTORY);
			if (!adapter.exists(log))
				adapter.create(log, true);
			
			xURI temp = xURI.create(Constants.LOGS_DIRECTORY);
			if (!adapter.exists(temp))
				adapter.create(temp, true);
	
			xURI q = xURI.create(Constants.Q_DIRECTORY);
			if (!adapter.exists(q))
				adapter.create(q, true);
	
			xURI db = xURI.create(Constants.DATABASE_DIRECTORY);
			if (!adapter.exists(db))
				adapter.create(db, true);
			
			xURI backup = xURI.create(Constants.BACKUP_DIRECTORY);
			if (!adapter.exists(backup))
				adapter.create(backup, true);
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		
		
		Properties p = System.getProperties();
		p.setProperty("derby.system.home", Constants.DATABASE_DIRECTORY + "/derby");
		
		DataDriverList dataDriverList = DataDriverList.list();
		for (int i = 0; i < dataDriverList.size(); i++) {
			DataDriver dataDriver = dataDriverList.get(i);
			try {
				dataDriver.loadDriver();
				LogUtil.info("Loaded data driver class: " + dataDriver.getInnerClassName());
			}
			catch (Throwable e) {
				LogUtil.error("Can not load data driver \"" + dataDriver.getInnerClassName() + "\"" + " error is " + e.getMessage());
			}
			
		}

		
		discover(Util.getLoadablePackages());
	}
	
	
	@Override
	public void discover(String...prefixList) {
		if (prefixList == null || prefixList.length == 0) {
			LogUtil.error("Cannot discover classes because of empty packages list.");
			return;
		}
		
		
		AdapterWriter nextUpdateLog = null;
		try {
			nextUpdateLog = new AdapterWriter(xURI.create(Constants.LOGS_DIRECTORY +"/nextupdate.log"), false);
		}
		catch (Throwable e) {
			e.printStackTrace();
		}

		
		Reflections reflections = null;
		for (String prefix : prefixList) {
			try {
				Reflections tempReflections = new Reflections(prefix);
				if (reflections == null)
					reflections = tempReflections;
				else
					reflections.merge(tempReflections);
			}
			catch (Exception e) {e.printStackTrace();}
		}
		if (reflections == null) {
			LogUtil.error("Null reflection for discovering classes.");
			return;
		}
		
		List<Class<? extends Alg>> compositeAlgClassList = Util.newList();
		Set<Class<? extends Alg>> algClasses = reflections.getSubTypesOf(Alg.class);
		for (Class<? extends Alg> algClass : algClasses) {
			if (algClass == null) continue;
			
			try {
				if (!isClassValid(algClass)) continue;
				
				if (algClass.getAnnotation(Composite.class) != null) {
					compositeAlgClassList.add(algClass);
					continue;
				}
				
				Alg alg = Util.newInstance(algClass);
				if (alg == null)
					continue;
	
				NextUpdate nextUpdate = algClass.getAnnotation(NextUpdate.class);
				AlgList nextUpdateList = PluginStorage.getNextUpdateList();
				if (nextUpdate != null) {
					if (nextUpdateList.indexOf(alg.getName()) >= 0)
						continue;
					
					nextUpdateList.add(alg);
					if (nextUpdateLog != null) {
						nextUpdateLog.write("\n\n");
						nextUpdateLog.write(algClass.toString() + "\n\tNote: " + nextUpdate.note());
					}
					
					continue;
				}
			
				registerAlg(alg);
			}
			catch (Exception e) {e.printStackTrace();}
		}
			
		for (Class<? extends Alg> compositeAlgClass : compositeAlgClassList) {
			try {
				Alg compositeAlg = Util.newInstance(compositeAlgClass);
				if (compositeAlg == null) continue;
				
				NextUpdate nextUpdate = compositeAlgClass.getAnnotation(NextUpdate.class);
				if (nextUpdate != null) {
					PluginStorage.getNextUpdateList().add(compositeAlg);
					if (nextUpdateLog != null) {
						nextUpdateLog.write("\n\n");
						nextUpdateLog.write(compositeAlgClass.toString() + "\n\tNote: " + nextUpdate.note());
					}
					
					continue;
				}

				registerAlg(compositeAlg);
			}
			catch (Exception e) {e.printStackTrace();}
		}
		
		
		try {
			if (nextUpdateLog != null) nextUpdateLog.close();
			nextUpdateLog = null;
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Registering the specified algorithm into respective register table of {@link PluginStorage}.
	 * For example, a recommendation algorithm will be added into recommender register table returned by {@link PluginStorage#getRecommenderReg()}. 
	 * @param alg specified algorithm.
	 */
	private void registerAlg(Alg alg) {

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
	 * Getting a list of instances from referred class.
	 * @param <T> type of returned instances.
	 * @param referredClass referred class.
	 * @return list of instances from specified package and referred class.
	 */
	public static <T> List<T> getInstances(Class<T> referredClass) {
		String[] prefixList = Util.getLoadablePackages();
		Reflections reflections = null;
		for (String prefix : prefixList) {
			try {
				Reflections tempReflections = new Reflections(prefix);
				if (reflections == null)
					reflections = tempReflections;
				else
					reflections.merge(tempReflections);
			}
			catch (Exception e) {e.printStackTrace();}
		}
		if (reflections == null) return Util.newList();
		
		return getInstances(referredClass, reflections);
	}


	/**
	 * Getting a list of instances from reflections and referred class.
	 * @param <T> type of returned instances.
	 * @param referredClass referred class.
	 * @param reflections specified reflections. 
	 * @return list of instances from specified package and referred class.
	 */
	private static <T> List<T> getInstances(Class<T> referredClass, Reflections reflections) {
		Set<Class<? extends T>> apClasses = reflections.getSubTypesOf(referredClass);
		List<T> instances = Util.newList();
		for (Class<? extends T> apClass : apClasses) {
			if (apClass == null) continue;
			if (!referredClass.isAssignableFrom(apClass)) continue;
			if (!isClassValid(apClass)) continue;

			try {
				T instance = Util.newInstance(apClass);
				instances.add(instance);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		return instances;
	}
	
	
	/**
	 * Loading classes from store.
	 * @param <T> object type.
	 * @param storeUri store URI.
	 * @param referredClass referred class.
	 * @return list of algorithms as output.
	 */
	public static <T> List<T> getInstances(xURI storeUri, Class<T> referredClass) {
		URL storeUrl = null;
		try {
			storeUrl = storeUri.getURI().toURL();
		}
		catch (Throwable e) {
			e.printStackTrace();
			storeUrl = null;
		}
		if (storeUrl == null) return Util.newList();
		
		URLClassLoader classLoader = new URLClassLoader(
				new URL[] {storeUrl},
				Firer.class.getClassLoader());
		UriAdapter adapter = new UriAdapter(storeUri);
		
		String rootPath = storeUri.getPath();
		List<T> outObjList = Util.newList();
		getInstances(storeUri, rootPath, adapter, classLoader, referredClass, outObjList);
		try {
			classLoader.close();
		} catch (Throwable e) {e.printStackTrace();}
		
		return outObjList;
	}

	
	/**
	 * Loading classes from specified store and class loader.
	 * @param storeUri specified store.
	 * @param rootPath root path.
	 * @param adapter URI adapter.
	 * @param classLoader specified class loader.
	 * @param referredClass referred class.
	 * @param outObjList list of objects as output.
	 */
	private static <T> void getInstances(xURI storeUri, String rootPath, UriAdapter adapter, ClassLoader classLoader, Class<T> referredClass, List<T> outObjList) {
		adapter.uriListProcess(storeUri,
			new UriFilter() {
			
				@Override
				public String getDescription() {
					// TODO Auto-generated method stub
					return "*.class";
				}
				
				@Override
				public boolean accept(xURI uri) {
					// TODO Auto-generated method stub
					UriAdapter adapter = new UriAdapter(uri);
					if (adapter.isStore(uri))
						return true;
					
					String ext = uri.getLastNameExtension();
					if (ext == null || !ext.toLowerCase().equals("class"))
						return false;
					
					String lastName = uri.getLastName();
					if (lastName == null || lastName.isEmpty())
						return false;
					else if (lastName.contains("$"))
						return false;
					else
						return true;
				}
			},
			new UriProcessor() {
			
				@SuppressWarnings("unchecked")
				@Override
				public void uriProcess(xURI uri) throws Exception {
					// TODO Auto-generated method stub
					if (adapter.isStore(uri)) {
						getInstances(uri, rootPath, adapter, classLoader, referredClass, outObjList);
						return;
					}
					
					String path = uri.getPath();
					if (path == null || path.isEmpty()) return;
					if (path.startsWith(rootPath))
						path = path.substring(rootPath.length());
					if (path == null || path.isEmpty()) return;
					
					String classPath = UriAdapter.packageSlashToDot(path);
					int idx = classPath.lastIndexOf(".class");
					if (idx >= 0) classPath = classPath.substring(0, idx);
					
					Class<?> cls = null;
					try {
						cls = Class.forName(classPath, true, classLoader);
					}
					catch (Throwable e) {
						System.out.println("Loading class \"" + classPath + "\" error");
						cls = null;
					}
					if (cls == null || !referredClass.isAssignableFrom(cls))
						return;
					if (!isClassValid(cls)) return;
					
					T obj = null;
					try {
						obj = (T) cls.newInstance();
					}
					catch (Throwable e) {
						System.out.println("Instantiate class \"" + classPath + "\" error");
						obj = null;
					}
					
					if (obj != null) outObjList.add(obj);
				}
			});
	}

	
	/**
	 * Checking whether specified class is valid.
	 * @param cls specified class.
	 * @return whether specified class is valid.
	 */
	public static boolean isClassValid(Class<?> cls) {
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
	
	
}
