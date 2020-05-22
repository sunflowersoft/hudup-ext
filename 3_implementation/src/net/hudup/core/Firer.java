/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;

import net.hudup.core.alg.Alg;
import net.hudup.core.logistic.Composite;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.NextUpdate;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.UriAdapter.AdapterWriter;
import net.hudup.core.logistic.UriFilter;
import net.hudup.core.logistic.UriProcessor;
import net.hudup.core.logistic.xURI;

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
public class Firer extends PluginManagerAbstract {

	
	/**
	 * Default constructor.
	 */
	public Firer() {
		super();
	}
	
	
	@Override
	public void discover() {
		try { //Redundant try-catch because it is impossible to solve the problem caused by PluginStorage.clear(). Solving later. 
			PluginStorage.clear();
			discover(Util.getLoadablePackages());
		}
		catch (Throwable e) {LogUtil.trace(e);}
	}


	/**
	 * The main method discovers automatically all algorithms via their names, given list of prefixes.
	 * This method affects plug-in storage.
	 * @param prefixList list of text strings of root paths to discover algorithms, for example, &quot;/net/hudup/&quot;.
	 * If this list is null or empty, all packages are browsed.
	 */
	private void discover(String...prefixList) {
		if (prefixList == null || prefixList.length == 0) {
			discover("");
			return;
		}
		
		
		AdapterWriter nextUpdateLog = null;
		try {
			nextUpdateLog = new AdapterWriter(xURI.create(Constants.LOGS_DIRECTORY +"/nextupdate.log"), false);
		}
		catch (Throwable e) {
			LogUtil.trace(e);
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
			catch (Exception e) {LogUtil.trace(e);}
		}
		if (reflections == null) {
			LogUtil.error("Null reflection for discovering classes.");
			return;
		}
		
		Set<Class<? extends Alg>> algClasses = reflections.getSubTypesOf(Alg.class);
		List<Alg> compositeAlgList = Util.newList();
		for (Class<? extends Alg> algClass : algClasses) {
			if (algClass == null) continue;
			
			try {
				if (!isClassValid(algClass)) continue;
				
				Alg alg = Util.newInstance(algClass);
				if (!isValidAlg(alg)) continue;

				if (algClass.getAnnotation(Composite.class) != null) {
					compositeAlgList.add(alg);
					continue;
				}
				
				NextUpdate nextUpdate = algClass.getAnnotation(NextUpdate.class);
				if (nextUpdate != null) {
					if (PluginStorage.lookupNextUpdateList(alg.getClass(), alg.getName()) >= 0)
						continue;
					
					PluginStorage.getNextUpdateList().add(alg);
					if (nextUpdateLog != null) {
						nextUpdateLog.write("\n\n");
						nextUpdateLog.write(algClass.toString() + "\n\tNote: " + nextUpdate.note());
					}
					
					continue;
				}
			
				registerAlg(alg);
			}
			catch (Exception e) {LogUtil.trace(e);}
		}
			
		for (Alg compositeAlg : compositeAlgList) {
			try {
				NextUpdate nextUpdate = compositeAlg.getClass().getAnnotation(NextUpdate.class);
				if (nextUpdate != null) {
					if (PluginStorage.lookupNextUpdateList(compositeAlg.getClass(), compositeAlg.getName()) >= 0)
						continue;

					PluginStorage.getNextUpdateList().add(compositeAlg);
					if (nextUpdateLog != null) {
						nextUpdateLog.write("\n\n");
						nextUpdateLog.write(compositeAlg.getClass().toString() + "\n\tNote: " + nextUpdate.note());
					}
					
					continue;
				}
				else
					registerAlg(compositeAlg);
			}
			catch (Exception e) {LogUtil.trace(e);}
		}
		
		
		try {
			if (nextUpdateLog != null) nextUpdateLog.close();
			nextUpdateLog = null;
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
	}
	
	
	@Override
	public <T> List<T> loadInstances(Class<T> referredClass) {
		String[] prefixList = Util.getLoadablePackages();
		if (prefixList == null || prefixList.length == 0)
			prefixList = new String[] {""};

		Reflections reflections = null;
		for (String prefix : prefixList) {
			try {
				Reflections tempReflections = new Reflections(prefix);
				if (reflections == null)
					reflections = tempReflections;
				else
					reflections.merge(tempReflections);
			}
			catch (Exception e) {LogUtil.trace(e);}
		}
		if (reflections == null) return Util.newList();
		
		return loadInstances(referredClass, reflections);
	}

	
	/**
	 * Getting a list of instances from reflections and referred class.
	 * This method does not affect plug-in storage.
	 * @param <T> type of returned instances.
	 * @param referredClass referred class. If this referred class is null, all classes are retrieved.
	 * @param reflections specified reflections. 
	 * @return list of instances from reflections and referred class.
	 */
	private <T> List<T> loadInstances(Class<T> referredClass, Reflections reflections) {
		Set<Class<? extends T>> apClasses = reflections.getSubTypesOf(referredClass);
		List<T> instances = Util.newList();
		for (Class<? extends T> apClass : apClasses) {
			if (apClass == null) continue;
			if (referredClass != null && !referredClass.isAssignableFrom(apClass))
				continue;
			if (!isClassValid(apClass)) continue;

			try {
				T instance = Util.newInstance(apClass);
				instances.add(instance);
			}
			catch (Exception e) {
				LogUtil.trace(e);
			}
			
		}
		
		return instances;
	}
	
	
	@Override
	public <T> List<T> loadInstances(xURI storeUri, Class<T> referredClass) {
		if (storeUri == null) storeUri = xURI.create(".");
		
		URL storeUrl = null;
		try {
			storeUrl = storeUri.getURI().toURL();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			storeUrl = null;
		}
		if (storeUrl == null) return Util.newList();
		
		URLClassLoader classLoader = new URLClassLoader(
				new URL[] {storeUrl},
				Firer.class.getClassLoader());
		UriAdapter adapter = new UriAdapter(storeUri);
		
		String rootPath = storeUri.getPath();
		List<T> outObjList = Util.newList();
		loadInstances(storeUri, rootPath, adapter, classLoader, referredClass, outObjList);
		try {
			classLoader.close();
		} catch (Throwable e) {LogUtil.trace(e);}
		
		if (outObjList.size() == 0) return outObjList;

		int foundIdx = -1;
		for (int i = 0; i < extraClassLoaders.size(); i++) {
			if (extraClassLoaders.get(i).getURLs()[0].equals(classLoader.getURLs()[0])) {
				foundIdx = i;
				break;
			}
		}
		if (foundIdx >= 0) extraClassLoaders.remove(foundIdx);
		extraClassLoaders.add(0, classLoader);
		if (Constants.MAX_EXTRA_CLASSLOADERS >= 0 && extraClassLoaders.size() > Constants.MAX_EXTRA_CLASSLOADERS)
			extraClassLoaders.remove(extraClassLoaders.size() - 1);

		return outObjList;
	}

	
	/**
	 * Loading a list of instances from specified store and class loader.
	 * This method does not affect plug-in storage.
	 * @param storeUri specified store.
	 * @param rootPath root path.
	 * @param adapter URI adapter.
	 * @param classLoader specified class loader.
	 * @param referredClass referred class. If this referred class is null, all classes are retrieved.
	 * @param outObjList list of objects (instances) as output.
	 */
	private <T> void loadInstances(xURI storeUri, String rootPath, UriAdapter adapter, ClassLoader classLoader, Class<T> referredClass, List<T> outObjList) {
		adapter.uriListProcess(storeUri,
			new UriFilter() {
			
				@Override
				public String getDescription() {
					return "*.class";
				}
				
				@Override
				public boolean accept(xURI uri) {
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
					if (adapter.isStore(uri)) {
						loadInstances(uri, rootPath, adapter, classLoader, referredClass, outObjList);
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
					if (cls == null) return;
					if (referredClass != null && !referredClass.isAssignableFrom(cls))
						return;
					if (!isClassValid(cls)) return;
					
					T obj = null;
					try {
						obj = (T) cls.getDeclaredConstructor().newInstance();
					}
					catch (Throwable e) {
						System.out.println("Instantiate class \"" + classPath + "\" error");
						obj = null;
					}
					
					if (obj != null) outObjList.add(obj);
				}
			});
	}

	
}
