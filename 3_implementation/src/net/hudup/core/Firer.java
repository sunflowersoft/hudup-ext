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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;

import net.hudup.core.alg.Alg;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.UriAdapter;
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
		
		List<Class<? extends Alg>> algClasses = loadClasses(Alg.class);
		analyzeAlgClasses(algClasses);
		
	}
	
	
	@Override
	public <T> List<Class<? extends T>> loadClasses(Class<T> referredClass, xURI... storeUris) {
		List<Class<? extends T>> outClassList = Util.newList();
		loadClassesInstances(referredClass, outClassList, null, storeUris);
		return outClassList;
	}


	@Override
	public <T> List<T> loadInstances(Class<T> referredClass, xURI...storeUris) {
		List<T> outObjList = Util.newList();
		loadClassesInstances(referredClass, null, outObjList, storeUris);
		return outObjList;
	}

	
	/**
	 * Loading classes and instances from array of stores
	 * @param <T> class type.
	 * @param referredClass referred class.
	 * @param outClassList collection of classes as output.
	 * @param outObjList collection of objects (instances) as output.
	 * @param storeUris array of stores.
	 */
	protected <T> void loadClassesInstances(Class<T> referredClass, Collection<Class<? extends T>> outClassList, Collection<T> outObjList, xURI...storeUris) {
		if (storeUris == null || storeUris.length == 0) {
			boolean added = addWorkingLibClassPath();
			
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
			
			if (reflections != null) {
				Set<Class<? extends T>> apClasses = reflections.getSubTypesOf(referredClass);
				for (Class<? extends T> apClass : apClasses) {
					if (apClass == null) continue;
					if (referredClass != null && !referredClass.isAssignableFrom(apClass))
						continue;
					if (!isClassValid(apClass)) continue;
	
					if (outClassList != null) outClassList.add(apClass);
					
					if (outObjList != null) {
						try {
							T instance = Util.newInstance(apClass);
							outObjList.add(instance);
						}
						catch (Exception e) {LogUtil.trace(e);}
					}
				}
			}
			
			if (added) return;
		}
		
		List<xURI> uriList = Util.newList();
		if (storeUris == null || storeUris.length == 0) {
			UriAdapter adapter = new UriAdapter(xURI.create(Constants.LIB_DIRECTORY));
			uriList = adapter.getUriListOfStoresArchives(xURI.create(Constants.LIB_DIRECTORY));
			adapter.close();
		}
		else
			uriList.addAll(Arrays.asList(storeUris));
		
		URL[] uris = xURI.toUrl(uriList).toArray(new URL[] {});
		if (uris == null || uris.length == 0) return;
		
		if (storeUris != null && storeUris.length > 0) {
			try {
				if (getClass().getClassLoader() instanceof URLClassLoader) {
					URLClassLoader sysClassLoader = (URLClassLoader) getClass().getClassLoader();
					if (contains(sysClassLoader.getURLs(), uris)) {
						loadClassesInstances(referredClass, outClassList, outObjList);
						return;
					}
				}
			}
			catch (Exception e) {LogUtil.trace(e);}
		}
		
		boolean createNewCP = false;
		URLClassLoader classLoader = null;
		for (URLClassLoader cl : extraClassLoaders) {
			if (contains(cl.getURLs(), uris)) {
				classLoader = cl;
				break;
			}
		}
		if (classLoader == null) {
			classLoader = createClassLoader(uriList.toArray(new xURI[] {}));
			createNewCP = true;
		}
		if (classLoader == null) return;
		
		for (xURI storeUri : uriList) {
			String rootPath = storeUri.getPath();
			UriAdapter adapter = new UriAdapter(storeUri);
			loadClassesInstances(storeUri, rootPath, adapter, classLoader, referredClass, outClassList, outObjList);
			adapter.close();
		}
		
		if (!createNewCP)
			return;
		else if (extraClassLoaders.size() == 0 || Constants.MAX_EXTRA_CLASSLOADERS <= 0 || extraClassLoaders.size() < Constants.MAX_EXTRA_CLASSLOADERS)
			extraClassLoaders.add(0, classLoader);
		else {
			try {
				URLClassLoader cl = extraClassLoaders.remove(extraClassLoaders.size() - 1);
				cl.close();
			} catch (Exception e) {LogUtil.trace(e);}
			
			extraClassLoaders.add(0, classLoader);
		}
	}
	
	
	/**
	 * Testing whether the super URL set contains the sub URL set.
	 * @param superUrls super URL set.
	 * @param subUrls2 sub URL set.
	 * @return whether the super URL set contains the sub URL set.
	 */
	private boolean contains(URL[] superUrls, URL[] subUrls2) {
		if (superUrls == null || subUrls2 == null) return false;
		if (subUrls2.length == 0) return true;
		
		for (int i = 0; i < subUrls2.length; i++) {
			URL subUrl = subUrls2[i];
			
			boolean found = false;
			for (int j = 0; j < superUrls.length; j++) {
				URL superUrl = superUrls[j];
				if (subUrl == null && superUrl == null) {
					found = true;
					break;
				}
				else if (subUrl != null && superUrl != null) {
					if (subUrl == superUrl || subUrl.equals(superUrl)) {
						found = true;
						break;
					}
				}
			}
			
			if (!found) return false;
		}
		
		return true;
	}

	
}
