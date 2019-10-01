/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import javax.swing.UIManager;

import org.reflections.Reflections;

import net.hudup.core.alg.Alg;
import net.hudup.core.alg.AlgList;
import net.hudup.core.alg.AlgRemote;
import net.hudup.core.alg.AlgRemoteWrapper;
import net.hudup.core.alg.AugRemote;
import net.hudup.core.alg.AugRemoteWrapper;
import net.hudup.core.alg.CompositeAlg;
import net.hudup.core.alg.CompositeAlgRemote;
import net.hudup.core.alg.ExecutableAlg;
import net.hudup.core.alg.ExecutableAlgRemote;
import net.hudup.core.alg.ExecutableAlgRemoteWrapper;
import net.hudup.core.alg.MemoryBasedAlg;
import net.hudup.core.alg.MemoryBasedAlgRemote;
import net.hudup.core.alg.ModelBasedAlg;
import net.hudup.core.alg.ModelBasedAlgRemote;
import net.hudup.core.alg.Recommender;
import net.hudup.core.alg.RecommenderRemote;
import net.hudup.core.alg.RecommenderRemoteWrapper;
import net.hudup.core.alg.ServiceAlg;
import net.hudup.core.data.DataDriver;
import net.hudup.core.data.DataDriverList;
import net.hudup.core.data.ExternalQuery;
import net.hudup.core.data.ExternalQueryRemote;
import net.hudup.core.data.ExternalQueryRemoteWrapper;
import net.hudup.core.data.ctx.CTSManager;
import net.hudup.core.data.ctx.CTSManagerRemote;
import net.hudup.core.data.ctx.CTSManagerRemoteWrapper;
import net.hudup.core.evaluate.MetaMetric;
import net.hudup.core.evaluate.Metric;
import net.hudup.core.evaluate.MetricRemote;
import net.hudup.core.evaluate.MetricRemoteWrapper;
import net.hudup.core.evaluate.MetricWrapper;
import net.hudup.core.logistic.Composite;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.NextUpdate;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.UriAdapter.AdapterWriter;
import net.hudup.core.logistic.UriFilter;
import net.hudup.core.logistic.UriProcessor;
import net.hudup.core.logistic.xURI;
import net.hudup.core.parser.DatasetParser;
import net.hudup.core.parser.DatasetParserRemote;
import net.hudup.core.parser.DatasetParserRemoteWrapper;

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
	 * Flag to indicate whether {@link #fire()} method was fired.
	 */
	protected boolean fired = false;
	
	
	/**
	 * This default constructor calls the method {@link #fire()} to initialize important system information.
	 */
	public Firer() {

	}
	
	
	@Override
	public void fire() {
		if (isFired()) return;
		
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
			
			adapter.close();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		
		try {
			Properties p = System.getProperties();
			p.setProperty("derby.system.home", Constants.DATABASE_DIRECTORY + "/derby");
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		
		
		try {
			loadDrivers();
		}
		catch (Throwable e) {e.printStackTrace();}
		
		
		try {
			discover();
		}
		catch (Throwable e) {e.printStackTrace();}
		
		
		try {
			randomLookAndFeel();
			LogUtil.info("Look and feel used: " + UIManager.getLookAndFeel().getName());
		}
		catch (Throwable e) {e.printStackTrace();}
		
		
		fired = true;
	}
	
	
	/**
	 * Setting UI look and feel randomly. 
	 */
	private void randomLookAndFeel() {
		boolean lfRnd = false;
		try {
			String lfText = Util.getHudupProperty("look_and_feel_random");
			if (lfText != null && !lfText.isEmpty())
				lfRnd = Boolean.parseBoolean(lfText);
		}
		catch (Throwable e) {
			e.printStackTrace();
			lfRnd = false;
		}
		if (!lfRnd) return;
		
		Random rnd = new Random();
		int index = rnd.nextInt(lookAndFeels.length);
		if (index == 0) return;
		
		try {
			UIManager.setLookAndFeel(lookAndFeels[index][1]);
		}
		catch (Throwable e) {
			LogUtil.info("Look and feel '" + lookAndFeels[index][1] + "' not supported");
		}
	}
	
	
	@Override
	public boolean isFired() {
		// TODO Auto-generated method stub
		return fired;
	}


	@Override
	public void discover() {
		// TODO Auto-generated method stub
		PluginStorage.clear();
		discover(Util.getLoadablePackages());
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
				if (!PluginManager.isClassValid(algClass)) continue;
				
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
	
	
	@Override
	public <T> List<T> discover(Class<T> referredClass) {
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
			catch (Exception e) {e.printStackTrace();}
		}
		if (reflections == null) return Util.newList();
		
		return discover(referredClass, reflections);
	}

	
	/**
	 * Getting a list of instances from reflections and referred class.
	 * This method does not affect plug-in storage.
	 * @param <T> type of returned instances.
	 * @param referredClass referred class. If this referred class is null, all classes are retrieved.
	 * @param reflections specified reflections. 
	 * @return list of instances from reflections and referred class.
	 */
	private static <T> List<T> discover(Class<T> referredClass, Reflections reflections) {
		Set<Class<? extends T>> apClasses = reflections.getSubTypesOf(referredClass);
		List<T> instances = Util.newList();
		for (Class<? extends T> apClass : apClasses) {
			if (apClass == null) continue;
			if (referredClass != null && !referredClass.isAssignableFrom(apClass))
				continue;
			if (!PluginManager.isClassValid(apClass)) continue;

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
	
	
	@Override
	public <T> List<T> discover(xURI storeUri, Class<T> referredClass) {
		if (storeUri == null) storeUri = xURI.create(".");
		
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
		discover(storeUri, rootPath, adapter, classLoader, referredClass, outObjList);
		try {
			classLoader.close();
		} catch (Throwable e) {e.printStackTrace();}
		
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
	private static <T> void discover(xURI storeUri, String rootPath, UriAdapter adapter, ClassLoader classLoader, Class<T> referredClass, List<T> outObjList) {
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
						discover(uri, rootPath, adapter, classLoader, referredClass, outObjList);
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
					if (!PluginManager.isClassValid(cls)) return;
					
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

	
	@Override
	public void loadDrivers() {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
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
		else if (remoteAlg instanceof ExecutableAlgRemote)
			return new ExecutableAlgRemoteWrapper((ExecutableAlgRemote)remoteAlg, exclusive);
		else if (remoteAlg instanceof AugRemote)
			return new AugRemoteWrapper((AugRemote)remoteAlg, exclusive);
		else
			return new AlgRemoteWrapper(remoteAlg, exclusive);
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
		else if (alg instanceof AlgRemoteWrapper) {
			AlgRemote remoteAlg = ((AlgRemoteWrapper)alg).getRemoteAlg();
			if (remoteAlg instanceof Alg)
				return functionTypeOf((Alg)remoteAlg);
			else if (remoteAlg instanceof RecommenderRemote)
				return 0;
			else if (remoteAlg instanceof ExecutableAlgRemote)
				return 1;
			else
				return -1;
		}
		else
			return -1;
	}


}
