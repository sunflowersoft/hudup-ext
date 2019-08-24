/**
 * 
 */
package net.hudup.core;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.reflections.Reflections;

import net.hudup.core.alg.Alg;
import net.hudup.core.data.DataDriver;
import net.hudup.core.data.DataDriverList;
import net.hudup.core.data.ExternalQuery;
import net.hudup.core.data.ctx.CTSManager;
import net.hudup.core.evaluate.Metric;
import net.hudup.core.logistic.BaseClass;
import net.hudup.core.logistic.Composite;
import net.hudup.core.logistic.NextUpdate;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.UriAdapter.AdapterWriter;
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
 * Method {@link #fire()} continues to call method {@link #discover(String)} to discover and register all algorithms (from class path and libaray of Hudup framework) into {@link PluginStorage}.
 * </li>
 * </ol> 
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class Firer implements PluginManager {

	
	/**
	 * Logger of this class.
	 */
	protected final static Logger logger = Logger.getLogger(Firer.class);

	
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
	 * Method {@link #fire()} continues to call method {@link #discover(String)} to discover and register all algorithms (from class path and libaray of Hudup framework) into {@link PluginStorage}.
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
				Class.forName( dataDriver.getInnerClassName() );
				logger.info("Loaded data driver class: " + dataDriver.getInnerClassName());
			}
			catch (Throwable e) {
				logger.error("Can not load data driver \"" + dataDriver.getInnerClassName() + "\"" + " error is " + e.getMessage());
			}
			
		}

		
		discover(Util.getLoadablePackages());
	}
	
	
	@Override
	public void discover(String...prefixList) {
		if (prefixList == null || prefixList.length == 0) {
			logger.error("Cannot discover classes because of empty packages list.");
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
			logger.error("Null reflection for discovering classes.");
			return;
		}
		
		List<Class<? extends Alg>> compositeAlgClassList = Util.newList();
		Set<Class<? extends Alg>> algClasses = reflections.getSubTypesOf(Alg.class);
		for (Class<? extends Alg> algClass : algClasses) {
			try {
				if (algClass.isInterface() || algClass.isMemberClass() || algClass.isAnonymousClass())
					continue;
				
				int modifiers = algClass.getModifiers();
				if ( (modifiers & Modifier.ABSTRACT) != 0 || (modifiers & Modifier.PUBLIC) == 0)
					continue;
				if (algClass.getAnnotation(BaseClass.class) != null || 
						algClass.getAnnotation(Deprecated.class) != null) {
					continue;
				}
				if (algClass.getAnnotation(Composite.class) != null) {
					compositeAlgClassList.add(algClass);
					continue;
				}
				
				Alg alg = Util.newInstance(algClass);
				if (alg == null)
					continue;
	
				NextUpdate nextUpdate = algClass.getAnnotation(NextUpdate.class);
				if (nextUpdate != null) {
					PluginStorage.getNextUpdateList().add(alg);
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
				if (compositeAlg != null)
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
			logger.info("Registered algorithm: " + alg.getName());
	}
	
	
}
