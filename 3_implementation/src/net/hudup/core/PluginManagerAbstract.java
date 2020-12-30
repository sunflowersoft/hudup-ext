package net.hudup.core;

import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
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
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.UriAdapter;
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
		
		try {
			PluginStorage.clear();
		} catch (Exception e) {LogUtil.trace(e);}
		
		try {
			ExtraStorage.clear();
		} catch (Exception e) {LogUtil.trace(e);}
		
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
			
			xURI lib = xURI.create(Constants.LIB_DIRECTORY);
			if (!adapter.exists(lib))
				adapter.create(lib, true);
			
			adapter.close();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
		try {
			Properties p = System.getProperties();
			p.setProperty("derby.system.home", Constants.DATABASE_DIRECTORY + "/derby");
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
		
		loadDrivers();
		
		
		discover();
		
		
		extraTasks();
		
		
		fired = true;
	}
	
	
	@Override
	public boolean isFired() {
		return fired;
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
	 * Defining extra tasks for {@link #fire()} method.
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
		else if (remoteAlg instanceof ExecutableAlgRemote)
			return new ExecutableAlgRemoteWrapper((ExecutableAlgRemote)remoteAlg, exclusive);
		else if (remoteAlg instanceof AugRemote)
			return new AugRemoteWrapper((AugRemote)remoteAlg, exclusive);
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
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		Class<?> foundClass = null;
		try {
			foundClass = getClass().getClassLoader().loadClass(name);
		}
		catch (ClassNotFoundException e) {}
		if (foundClass != null) return foundClass;
		
		for (ClassLoader classLoader : extraClassLoaders) {
			try {
				foundClass = classLoader.loadClass(name);
			}
			catch (ClassNotFoundException e) {
				foundClass = null;
			}
			if (foundClass != null) return foundClass;
		}
		
		throw new ClassNotFoundException("Class " + name + " not found");
	}


	@Override
	public InputStream getResourceAsStream(String name) {
		InputStream is = getClass().getClassLoader().getResourceAsStream(name);
		if (is != null) return is;
		
		for (URLClassLoader classLoader : extraClassLoaders) {
			try {
				is = classLoader.getResourceAsStream(name);
				if (is != null) return is;

				URL[] urls = classLoader.getURLs();
				if (urls == null || urls.length == 0) continue;
				
				xURI classPath = xURI.create(urls[0].toURI());
				classPath = classPath.concat(name);
				UriAdapter adapter = new UriAdapter(classPath);
				
				is = adapter.getInputStream(classPath);
				adapter.close();
				if (is != null) return is;
				
			} catch (Exception e) {LogUtil.trace(e);}
		}
		
		return null;
	}

	
}
