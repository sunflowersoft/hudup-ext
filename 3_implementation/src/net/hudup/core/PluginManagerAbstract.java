/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core;

import static net.hudup.core.Constants.ROOT_PACKAGE;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import net.hudup.core.alg.Alg;
import net.hudup.core.alg.AlgRemote;
import net.hudup.core.alg.AlgRemoteWrapper;
import net.hudup.core.alg.AugRemote;
import net.hudup.core.alg.AugRemoteWrapper;
import net.hudup.core.alg.CompositeAlg;
import net.hudup.core.alg.CompositeAlgRemote;
import net.hudup.core.alg.ExecutableAlg;
import net.hudup.core.alg.ExecutableAlgRemote;
import net.hudup.core.alg.ExecutableAlgRemoteWrapper;
import net.hudup.core.alg.ExecuteAsLearnAlg;
import net.hudup.core.alg.ExecuteAsLearnAlgRemote;
import net.hudup.core.alg.ExecuteAsLearnAlgRemoteWrapper;
import net.hudup.core.alg.MemoryBasedAlg;
import net.hudup.core.alg.MemoryBasedAlgRemote;
import net.hudup.core.alg.ModelBasedAlg;
import net.hudup.core.alg.ModelBasedAlgRemote;
import net.hudup.core.alg.NonexecutableAlg;
import net.hudup.core.alg.NonexecutableAlgRemote;
import net.hudup.core.alg.NonexecutableAlgRemoteWrapper;
import net.hudup.core.alg.Recommender;
import net.hudup.core.alg.RecommenderRemote;
import net.hudup.core.alg.RecommenderRemoteWrapper;
import net.hudup.core.alg.ServiceAlg;
import net.hudup.core.app.Appor;
import net.hudup.core.data.DataDriver;
import net.hudup.core.data.DataDriverList;
import net.hudup.core.data.DatasetRemote;
import net.hudup.core.data.DatasetRemoteWrapper;
import net.hudup.core.data.ExternalQuery;
import net.hudup.core.data.ExternalQueryRemote;
import net.hudup.core.data.ExternalQueryRemoteWrapper;
import net.hudup.core.data.KBasePointerRemote;
import net.hudup.core.data.KBasePointerRemoteWrapper;
import net.hudup.core.data.PointerRemote;
import net.hudup.core.data.PointerRemoteWrapper;
import net.hudup.core.data.ServerPointerRemote;
import net.hudup.core.data.ServerPointerRemoteWrapper;
import net.hudup.core.data.ctx.CTSManager;
import net.hudup.core.data.ctx.CTSManagerRemote;
import net.hudup.core.data.ctx.CTSManagerRemoteWrapper;
import net.hudup.core.evaluate.MetaMetric;
import net.hudup.core.evaluate.Metric;
import net.hudup.core.evaluate.MetricRemote;
import net.hudup.core.evaluate.MetricRemoteWrapper;
import net.hudup.core.evaluate.MetricWrapper;
import net.hudup.core.logistic.BaseClass;
import net.hudup.core.logistic.Composite;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.NextUpdate;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.UriAdapter.AdapterWriter;
import net.hudup.core.logistic.UriFilter;
import net.hudup.core.logistic.UriProcessor;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.core.parser.DatasetParser;
import net.hudup.core.parser.DatasetParserRemote;
import net.hudup.core.parser.DatasetParserRemoteWrapper;


/**
 * This abstract class is partial implementation of interface plug-in manager.
 *  
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public abstract class PluginManagerAbstract implements PluginManager {

	
	/**
	 * Working class path key jar.
	 */
	protected final static String WORKING_LIB_KEY_JAR = "working-lib-key.jar";
	
	
	/**
	 * Working class path key jar.
	 */
	protected final static String WORKING_LIB_KEY_CLASS = "net.hudup.core.security.WorkingLibKey";

	
	/**
	 * Flag to indicate whether {@link #fire()} method was fired.
	 */
	protected boolean fired = false;
	
	
	/**
	 * List of extra class loaders.
	 */
	protected List<URLClassLoader> extraClassLoaders = Util.newList();
	
	
	/**
	 * Default constructor.
	 */
	public PluginManagerAbstract() {

	}

	
	@Override
	public void fire() {
		if (isFired()) return;
		
		//Doing basic tasks.
		fireSimply();
		
		//Registering algorithms, which is fill in the plug-in storage.
		discover();
		
		//Doing extra tasks.
		extraTasks();
		
		fired = true;
	}
	
	
	@Override
	public void fireSimply() {
		if (isFired()) return;
		
		//Clear plug-in storage.
		try {
			PluginStorage.clear();
		} catch (Exception e) {LogUtil.trace(e);}
		
		//Clear extra storage.
		try {
			ExtraStorage.clear();
		} catch (Exception e) {LogUtil.trace(e);}
		
		//Create directories in working directory.
		try {
			UriAdapter adapter = new UriAdapter(Constants.WORKING_DIRECTORY);
			
			xURI working = xURI.create(Constants.WORKING_DIRECTORY);
			if (!adapter.exists(working)) adapter.create(working, true);
			
			xURI kb = xURI.create(Constants.KNOWLEDGE_BASE_DIRECTORY);
			if (!adapter.exists(kb)) adapter.create(kb, true);
				
			xURI log = xURI.create(Constants.LOGS_DIRECTORY);
			if (!adapter.exists(log)) adapter.create(log, true);
			
			xURI temp = xURI.create(Constants.LOGS_DIRECTORY);
			if (!adapter.exists(temp))
				adapter.create(temp, true);
	
			xURI q = xURI.create(Constants.Q_DIRECTORY);
			if (!adapter.exists(q)) adapter.create(q, true);
	
			xURI db = xURI.create(Constants.DATABASE_DIRECTORY);
			if (!adapter.exists(db)) adapter.create(db, true);
			
			xURI file = xURI.create(Constants.FILE_DIRECTORY);
			if (!adapter.exists(file)) adapter.create(file, true);

			xURI backup = xURI.create(Constants.BACKUP_DIRECTORY);
			if (!adapter.exists(backup)) adapter.create(backup, true);
			
			xURI lib = xURI.create(Constants.LIB_DIRECTORY);
			if (!adapter.exists(lib)) adapter.create(lib, true);
			
			adapter.close();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
		//Copying working library key.
		try {
			URL resourceWorkingLibKeyUrl = getClass().getResource(Constants.LIB_PACKAGE + WORKING_LIB_KEY_JAR);
			xURI resourceWorkingLibKeyUri = xURI.create(resourceWorkingLibKeyUrl.toURI());
			UriAdapter adapter = new UriAdapter(xURI.create(Constants.LIB_DIRECTORY));
			if (adapter.exists(resourceWorkingLibKeyUri)) {
				xURI workingLibKeyUri = xURI.create(Constants.LIB_DIRECTORY + "/" + WORKING_LIB_KEY_JAR);
				if (!adapter.exists(workingLibKeyUri))
					adapter.copy(resourceWorkingLibKeyUri, workingLibKeyUri, false, null);
			}
			adapter.close();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
		//Copying the last property file.
		try {
			UriAdapter adapter = new UriAdapter(Constants.WORKING_DIRECTORY);
			xURI templatePropUri = xURI.create(Constants.WORKING_DIRECTORY	+ "/" + Util.hudupTemplatePropName);
			if (!adapter.exists(templatePropUri)) {
				xURI lastUri = null;
				for (int i = 0; i <= Util.MAX_PROPS_FILES; i++) {
					String path = ROOT_PACKAGE + Util.hudupPropName + "." + i;
					xURI uri = null;
					try {
						URL url = getClass().getResource(path);
						if (url != null)
							uri = xURI.create(url.toURI());
					}
					catch (Throwable e) {uri = null;}
					
					if (uri != null) lastUri = uri;
				}
				
				if (lastUri != null) {
					try {
						adapter.copy(lastUri, templatePropUri, false, null);
					} catch (Exception e) {
						LogUtil.error("Copying last properties error by " + e.getMessage());
					}
				}
			}
			
			adapter.close();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}

		//Copying the test property file.
		try {
			xURI testPropUri = null;
			try {
				URL testPropUrl = getClass().getResource(ROOT_PACKAGE + Util.hudupTestPropName);
				if (testPropUrl != null) testPropUri = xURI.create(testPropUrl.toURI());
			}
			catch (Throwable e) {testPropUri = null;}

			UriAdapter adapter = new UriAdapter(Constants.WORKING_DIRECTORY);
			xURI workingTestPropUri = xURI.create(Constants.WORKING_DIRECTORY	+ "/" + Util.hudupTestPropName);
			if (testPropUri != null && !adapter.exists(workingTestPropUri)) {
				try {
					if (Constants.COMPRESSED_FILE_SUPPORT)
						adapter.copy(testPropUri, workingTestPropUri, false, null);
					else {
						InputStream in = getClass().getResourceAsStream(ROOT_PACKAGE + Util.hudupTestPropName);
						Properties testProps = new Properties();
						testProps.load(in);
						OutputStream out = Files.newOutputStream(Paths.get(workingTestPropUri.getURI()), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
						testProps.store(out, "Hudup properties for testing, which is stored in working directory");
						in.close();
						out.close();
					}
				} catch (Throwable e) {
					LogUtil.error("Copying test properties error by " + e.getMessage());
				}
			}
			
			adapter.close();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}

		//Adding working library class path into Java class path.
		addWorkingLibClassPath();
		
		
		//Setting derby database.
		try {
			Properties p = System.getProperties();
			p.setProperty("derby.system.home", Constants.DATABASE_DIRECTORY + "/derby");
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
		
		//Loading drivers.
		loadDrivers();
		
		
		fired = true;
	}


	@Override
	public boolean isFired() {
		return fired;
	}


	@Override
	public void discover() {
		try { //Redundant try-catch because it is impossible to solve the problem caused by PluginStorage.clear(). Solving later. 
			PluginStorage.clear();
			ExtraStorage.clear();
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
		
		List<Class<? extends Appor>> apporClasses = loadClasses(Appor.class);
		analyzeApporClasses(apporClasses);
	}
	

	@Override
	public <T extends Alg> void discover(Class<T> referredClass) {
		List<Class<? extends T>> classes = loadClasses(referredClass);
		List<Class<? extends Alg>> algClasses = Util.newList();
		for (Class<? extends T> cls : classes) algClasses.add(cls);
		
		analyzeAlgClasses(algClasses);
	}
	
	
	@Override
	public void loadDrivers() {
		try { //DataDriverList.get() can cause error.
			DataDriverList dataDriverList = DataDriverList.get();
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
		}
		catch (Throwable e) {LogUtil.trace(e);}
	}


	/**
	 * Defining extra tasks for {@link #fire()} method. These tasks must not be important.
	 */
	protected void extraTasks() {
		try {
			UIUtil.randomLookAndFeel();
		}
		catch (Throwable e) {LogUtil.trace(e);}
	}
	
	
	@Override
	public void registerAlg(Alg alg) {
		RegisterTable normalAlgReg = PluginStorage.getNormalAlgReg();
		RegisterTable metricReg = PluginStorage.getMetricReg();
		RegisterTable parserReg = PluginStorage.getParserReg();
		RegisterTable externalQueryReg = PluginStorage.getExternalQueryReg();
		RegisterTable ctsmReg = PluginStorage.getCTSManagerReg();

		boolean registered = false;
		if (alg instanceof DatasetParser)
			registered = parserReg.register( (DatasetParser)alg );
		else if (alg instanceof Metric) {
			if (alg instanceof MetricWrapper)
				LogUtil.info("Metric wrapper is used for calculating other metrics and so it is not registered");
			else if (alg instanceof MetaMetric)
				LogUtil.info("Meta Metric can be registered if its internal metrics are referred correctly. In this current implementation, meta metric is not registered");
			else
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

	
	@Override
	public AlgRemoteWrapper wrap(AlgRemote remoteAlg, boolean exclusive) {
		if (remoteAlg instanceof DatasetParserRemote)
			return new DatasetParserRemoteWrapper((DatasetParserRemote)remoteAlg, exclusive);
		else if (remoteAlg instanceof MetricRemote)
			return new MetricRemoteWrapper((MetricRemote)remoteAlg, exclusive);
		else if (remoteAlg instanceof ExternalQueryRemote)
			return new ExternalQueryRemoteWrapper((ExternalQueryRemote)remoteAlg, exclusive);
		else if (remoteAlg instanceof CTSManagerRemote)
			return new CTSManagerRemoteWrapper((CTSManagerRemote)remoteAlg, exclusive);
		else if (remoteAlg instanceof RecommenderRemote)
			return new RecommenderRemoteWrapper((RecommenderRemote)remoteAlg, exclusive);
		else if (remoteAlg instanceof AugRemote)
			return new AugRemoteWrapper((AugRemote)remoteAlg, exclusive);
		else if (remoteAlg instanceof ExecutableAlgRemote)
			return new ExecutableAlgRemoteWrapper((ExecutableAlgRemote)remoteAlg, exclusive);
		else if (remoteAlg instanceof NonexecutableAlgRemote)
			return new NonexecutableAlgRemoteWrapper((NonexecutableAlgRemote)remoteAlg, exclusive);
		else if (remoteAlg instanceof ExecuteAsLearnAlgRemote)
			return new ExecuteAsLearnAlgRemoteWrapper((ExecuteAsLearnAlgRemote)remoteAlg, exclusive);
		else
			return new AlgRemoteWrapper(remoteAlg, exclusive);
	}
	
	
	@Override
	public DatasetRemoteWrapper wrap(DatasetRemote remoteDataset, boolean exclusive) {
		if (remoteDataset instanceof KBasePointerRemote)
			return new KBasePointerRemoteWrapper((KBasePointerRemote)remoteDataset, exclusive);
		else if (remoteDataset instanceof ServerPointerRemote)
			return new ServerPointerRemoteWrapper((ServerPointerRemote)remoteDataset, exclusive);
		else if (remoteDataset instanceof PointerRemote)
			return new PointerRemoteWrapper((PointerRemote)remoteDataset, exclusive);
		else
			return new DatasetRemoteWrapper(remoteDataset, exclusive);
	}


	@Override
	public int methodTypeOf(Alg alg) {
		if (alg instanceof MemoryBasedAlg)
			return 0;
		else if (alg instanceof ModelBasedAlg)
			return 1;
		else if (alg instanceof CompositeAlg)
			return 2;
		else if (alg instanceof ServiceAlg)
			return 3;
		else if (alg instanceof AlgRemoteWrapper) {
			AlgRemote remoteAlg = ((AlgRemoteWrapper)alg).getRemoteAlg();
			if (remoteAlg instanceof Alg)
				return methodTypeOf((Alg)remoteAlg);
			else if (remoteAlg instanceof MemoryBasedAlgRemote)
				return 0;
			else if (remoteAlg instanceof ModelBasedAlgRemote)
				return 1;
			else if (remoteAlg instanceof CompositeAlgRemote)
				return 2;
			else if (remoteAlg instanceof ServiceAlg)
				return 3;
			else
				return -1;
		}
		else
			return -1;
	}


	@Override
	public int functionTypeOf(Alg alg) {
		if (alg instanceof Recommender)
			return 0;
		else if (alg instanceof ExecutableAlg)
			return 1;
		else if (alg instanceof NonexecutableAlg)
			return 2;
		else if (alg instanceof ExecuteAsLearnAlg)
			return 3;
		else if (alg instanceof AlgRemoteWrapper) {
			AlgRemote remoteAlg = ((AlgRemoteWrapper)alg).getRemoteAlg();
			if (remoteAlg instanceof Alg)
				return functionTypeOf((Alg)remoteAlg);
			else if (remoteAlg instanceof RecommenderRemote)
				return 0;
			else if (remoteAlg instanceof ExecutableAlgRemote) {
				if (remoteAlg instanceof NonexecutableAlgRemote)
					return 2;
				else
					return 1;
			}
			else
				return -1;
		}
		else
			return -1;
	}


	@Override
	public boolean isClassValid(Class<?> cls) {
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

	
	@Override
	public boolean isValidAlg(Alg alg) {
		if (alg == null)
			return false;
		else {
			String name = alg.getName();
			if (name == null || name.isEmpty()) return false;
			
			Alg newInstance = alg.newInstance();
			if (newInstance == null)
				return false;
			else
				return alg.newInstance().getClass().equals(alg.getClass());
		}
	}

	
	@Override
	public Class<?> loadClass(String name, boolean initialized) throws ClassNotFoundException {
		Class<?> foundClass = null;
		try {
			if (initialized)
				foundClass = Class.forName(name);
			else
				foundClass = Class.forName(name, initialized, this.getClass().getClassLoader());
		}
		catch (Exception e) {}
		if (foundClass != null) return foundClass;
		
		
		try {
			for (ClassLoader classLoader : extraClassLoaders) {
				try {
					if (initialized)
						foundClass = Class.forName(name, initialized, classLoader);
					else
						foundClass = classLoader.loadClass(name);
				}
				catch (Exception e) {}
				if (foundClass != null) return foundClass;
			}
		} catch (Exception e) {}
		
		
		try {
			List<Alg> algList = PluginStorage.getAlgListByClassName(name);
			for (Alg alg : algList) {
				try {
					ClassLoader classLoader = alg.getClass().getClassLoader();
					if (initialized)
						foundClass = Class.forName(name, initialized, classLoader);
					else
						foundClass = classLoader.loadClass(name);
				}
				catch (Exception e) {}
				if (foundClass != null) return foundClass;
			}
		} catch (Exception e) {}
		
		
		throw new ClassNotFoundException("Class " + name + " not found");
	}


	@Override
	public InputStream getResourceAsStream(String name) {
		InputStream is = getClass().getResourceAsStream(name);
		if (is != null) return is;
		
		for (URLClassLoader classLoader : extraClassLoaders) {
			is = getResourceAsStream(classLoader, name);
			if (is != null) return is;
		}
		
		return null;
	}

	
	/**
	 * Getting resource as stream from resource name by specified class loader.
	 * @param classLoader specified class loader.
	 * @param name resource name
	 * @return resource as stream from resource name by specified class loader.
	 */
	private static InputStream getResourceAsStream(URLClassLoader classLoader, String name) {
		try {
			InputStream is = classLoader.getResourceAsStream(name);
			if (is != null) return is;

			URL[] urls = classLoader.getURLs();
			if (urls == null || urls.length == 0) return null;
			for (URL url : urls) {
				xURI resourcePath = xURI.create(url.toURI());
				UriAdapter adapter = new UriAdapter(resourcePath);
				resourcePath = resourcePath.concat(name);
				
				is = adapter.getInputStream(resourcePath);
				adapter.close();
				if (is != null) return is;
			}
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
		
		return null;
	}
	
	
	/**
	 * Adding working library class path.
	 * @return true if adding is successful.
	 */
	protected boolean addWorkingLibClassPath() {
		URLClassLoader sysClassLoader = null;
		try {
			if (getClass().getClassLoader() instanceof URLClassLoader)
				sysClassLoader = (URLClassLoader) getClass().getClassLoader();
		}
		catch (Exception e) {LogUtil.trace(e);}
		if (sysClassLoader == null) return false;

		xURI libStoreUri = xURI.create(Constants.LIB_DIRECTORY);
		UriAdapter adapter = new UriAdapter(libStoreUri);
		List<xURI> uris = adapter.getUriListOfStoresArchives(libStoreUri);
		adapter.close();
		if (uris.size() == 0) return true;
		
		URL[] sysUrls = sysClassLoader.getURLs();
		sysUrls = sysUrls != null ? sysUrls : new URL[0];
		List<xURI> addUris = Util.newList();
		for (xURI uri : uris) {
			URL url = uri.toURL();
			if (url == null) continue;
			
			boolean found = false;
			for (URL sysUrl : sysUrls) {
				if (url.equals(sysUrl)) {
					found = true;
					break;
				}
			}
			
			if (!found) addUris.add(uri);
		}
		
		if (addUris.size() == 0)
			return true;
		else
			return addClassPaths(sysClassLoader, addUris.toArray(new xURI[] {}));
	}
	
	
	/**
	 * Loading a list of classes and instances from specified store and class loader.
	 * This method does not affect plug-in storage.
	 * @param <T> class type.
	 * @param storeUri specified store.
	 * @param rootPath root path.
	 * @param adapter URI adapter.
	 * @param classLoader specified class loader.
	 * @param referredClass referred class. If this referred class is null, all classes are retrieved.
	 * @param outClassList collection of classes as output.
	 * @param outObjList collection of objects (instances) as output.
	 */
	@NextUpdate
	protected <T> void loadClassesInstances(xURI storeUri, String rootPath, UriAdapter adapter, ClassLoader classLoader, Class<T> referredClass, Collection<Class<? extends T>> outClassList, Collection<T> outObjList) {
		adapter.uriListProcess(storeUri,
			new UriFilter() {
			
				@Override
				public String getDescription() {
					return "*.class";
				}
				
				@Override
				public boolean accept(xURI uri) {
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
						loadClassesInstances(uri, rootPath, adapter, classLoader, referredClass, outClassList, outObjList);
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
					
					//Finding class.
					Class<?> cls = null;
					try {
						cls = ClassLoader2.findClass(classLoader, classPath);
					}
					catch (Throwable e) {
						System.out.println("Finding class \"" + classPath + "\" error");
						cls = null;
					}
					if (cls == null) return;
					if (referredClass != null && !referredClass.isAssignableFrom(cls))
						return;
					if (!isClassValid(cls)) return;
					
//					//Initializing class.
//					if (outClassList != null || outObjList != null) {
//						cls = null;
//						try {
//							cls = ClassLoader2.findClass(classLoader, classPath);
//						}
//						catch (Throwable e) {
//							System.out.println("Initializing class \"" + classPath + "\" error");
//							cls = null;
//						}
//						if (cls == null) return;
//					}

					if (outClassList != null) outClassList.add((Class<? extends T>)cls);
					
					if (outObjList != null) {
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
				}
			});
	}

	
	/**
	 * Analyzing algorithm classes.
	 * @param algClasses specified algorithm classes.
	 */
	protected void analyzeAlgClasses(Collection<Class<? extends Alg>> algClasses) {
		if (algClasses == null) return;
		
		AdapterWriter nextUpdateLog = null;
		try {
			nextUpdateLog = new AdapterWriter(xURI.create(Constants.LOGS_DIRECTORY + "/" + LogUtil.NEXTUPDATE_FILENAME), false);
		}
		catch (Throwable e) {LogUtil.trace(e);}

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
						nextUpdateLog.write(compositeAlg.getClass().getName() + "\n\tNote: " + nextUpdate.note());
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
		catch (Throwable e) {LogUtil.trace(e);}
		
	}

	
	/**
	 * Analyzing application creator classes.
	 * @param apporClasses specified application creator classes.
	 */
	protected void analyzeApporClasses(Collection<Class<? extends Appor>> apporClasses) {
		if (apporClasses == null) return;
		
		for (Class<? extends Appor> apporClass : apporClasses) {
			if (apporClass == null) continue;
			try {
				if (!isClassValid(apporClass)) continue;
				Appor appor = Util.newInstance(apporClass);
				if (appor != null && ExtraStorage.addAppor(appor)) LogUtil.info("Registered appor: " + appor.getName());
			}
			catch (Throwable e) {LogUtil.trace(e);}
		}
		
	}

		
	/**
	 * Creating class loader from store URI (s).
	 * @param storeUris store URI (s).
	 * @return class loader from store URI (s).
	 */
	protected static URLClassLoader createClassLoader(xURI...storeUris) {
		if (storeUris == null || storeUris.length == 0) return null;
		
		List<xURI> uriList = Util.newList();
		for (xURI storeUri : storeUris) {
			UriAdapter adapter = new UriAdapter(storeUri);
			if (adapter.exists(storeUri)) uriList.add(storeUri);
			
			adapter.close();
		}
		
		List<URL> urlList = xURI.toUrl(uriList);
		if (urlList.size() == 0) return null;
		
		try {
			return new URLClassLoader(urlList.toArray(new URL[] {}), PluginManagerAbstract.class.getClassLoader());
		} catch (Exception e) {LogUtil.trace(e);}
		
		return null;
	}

	
	/**
	 * Creating class loader from parent URI.
	 * @param parentUri parent URI.
	 * @return class loader from parent URI.
	 */
	protected static URLClassLoader createClassLoader2(xURI parentUri) {
		if (parentUri == null) return null;
		
		UriAdapter adapter = new UriAdapter(parentUri);
		List<xURI> uriList = adapter.getUriListOfStoresArchives(parentUri);
		adapter.close();
		
		return createClassLoader(uriList.toArray(new xURI[] {}));
	}
	

	/**
	 * Adding class paths from store URI (s) into the specified URL class loader.
	 * @param classLoader specified URL class loader.
	 * @param storeUris store URI (s).
	 * @return true if successful adding.
	 */
	protected static boolean addClassPaths(URLClassLoader classLoader, xURI...storeUris) {
		if (classLoader == null || storeUris == null || storeUris.length == 0)
			return false;
		
		List<URL> urlList = xURI.toUrl(storeUris);
		if (urlList.size() == 0) return false;
		
	    Method method = null;
		try {
		    method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
		    method.setAccessible(true);
		}
		catch (Exception e) {
			LogUtil.trace(e);
			method = null;
		}
		if (method == null) return false;
		
		boolean result = true;
	    for (URL url : urlList) {
			try {
			    method.invoke(classLoader, url);
			}
			catch (Exception e) {
			    result = false;
				LogUtil.trace(e);
			}
		}
	    
	    return result;
	}

	
	/**
	 * Adding class paths from parent URI into the specified URL class loader.
	 * @param classLoader specified URL class loader.
	 * @param parentUri parent URI (s).
	 * @return true if successful adding.
	 */
	protected static boolean addClassPaths2(URLClassLoader classLoader, xURI parentUri) {
		if (parentUri == null) return false;
		
		UriAdapter adapter = new UriAdapter(parentUri);
		List<xURI> uriList = adapter.getUriListOfStoresArchives(parentUri);
		adapter.close();
		
		return addClassPaths(classLoader, uriList.toArray(new xURI[] {}));
	}

	
	@Override
	public void close() throws Exception {
		if (extraClassLoaders == null || extraClassLoaders.size() == 0)
			return;
		
		for (URLClassLoader cl : extraClassLoaders) {
			try {
				cl.close();
			} catch (Throwable e) {LogUtil.trace(e);}
		}
		
		extraClassLoaders.clear();
	}
	
	
	/**
	 * Adding shutdown hook to release all plug-in resources.
	 */
	static {
		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {
				try {
					Util.getPluginManager().close();
				} catch (Exception e) {LogUtil.trace(e);}
			}
		});
		
	}
	
	
}



/**
 * This is an extension of URL class loader with support of finding classes.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
class ClassLoader2 extends URLClassLoader {

	
	/**
	 * Internal URL class loader.
	 */
	protected URLClassLoader cl = null;
	
	
	/**
	 * Constructor with other URL class loader.
	 * @param cl other URL class loader.
	 */
	public ClassLoader2(URLClassLoader cl) {
		super(new URL[0]);
		this.cl = cl;
	}


	/*
	 * This method call causes illegal access which will be fixed later.
	 */
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		if (cl == null) return null;
		
	    Method method = null;
		try {
		    method = URLClassLoader.class.getDeclaredMethod("findClass", String.class);
		    
		    try {
		    	method.setAccessible(true); //This method call causes illegal access which will be fixed later.
			} catch (Throwable e) {LogUtil.errorNoLog("ClassLoader2#findClass(String) calling Method#setAccessible(boolean) causes error: " + e.getMessage());}
		    
		} catch (Throwable e) {method = null;}
		
		if (method == null) return null;
		
		try {
		    return (Class<?>)method.invoke(cl, name);
		}
		catch (Throwable e) {}
		
		return null;
	}

	
	/**
	 * Loading class by name.
	 * @param cl class loader.
	 * @param name specified.
	 * @return class given name.
	 */
	public static Class<?> findClass(ClassLoader cl, String name) {
		if (cl == null || name == null) return null;
		
		if (cl instanceof URLClassLoader) {
			ClassLoader2 cl2 = new ClassLoader2((URLClassLoader)cl);
			Class<?> cls = null;
			try {
				cls = cl2.findClass(name);
			}
			catch (Exception e) {
			}
			finally {
				try {
					cl2.close();
				}
				catch (Exception e) {}
			}
			if (cls != null) return cls;
		}
		
		try {
			Class<?> cls = cl.loadClass(name);
			if (cls != null) return cls;
		} catch (Exception e) {}
		
		try {
			Class<?> cls = Class.forName(name, false, cl);
			if (cls != null) return cls;
		} catch (Exception e) {}
		
		return null;
	}

	
}
