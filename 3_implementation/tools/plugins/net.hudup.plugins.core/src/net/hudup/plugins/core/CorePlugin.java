/**
 * 
 */
package net.hudup.plugins.core;

import java.util.List;

import net.hudup.core.PluginStorage;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.Recommender;
import net.hudup.core.data.ExternalQuery;
import net.hudup.core.data.ctx.CTSManager;
import net.hudup.core.evaluate.Metric;
import net.hudup.core.parser.DatasetParser;
import net.hudup.core.parser.TextParserUtil;

import org.apache.log4j.Logger;
import org.java.plugin.PluginManager;
import org.java.plugin.boot.Application;
import org.java.plugin.boot.ApplicationPlugin;
import org.java.plugin.registry.Extension;
import org.java.plugin.registry.Extension.Parameter;
import org.java.plugin.registry.ExtensionPoint;
import org.java.plugin.registry.PluginDescriptor;
import org.java.plugin.registry.PluginRegistry;
import org.java.plugin.util.ExtendedProperties;

/**
 * This is an example of recommendation plug-in.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public final class CorePlugin extends ApplicationPlugin implements Application {

	
	/**
	 * Plug-in identifier.
	 */
	public static final String PLUGIN_ID = "net.hudup.plugins.core";

	
	@Override
	protected void doStart() throws Exception {
		// no-op
	}

	
	@Override
	protected void doStop() throws Exception {
       // no-op
	}

   
	@Override
	protected Application initApplication(final ExtendedProperties config,
			final String[] args) throws Exception {
		return this;
	}

   
	/**
	 * Starting application.
	 */
	public void startApplication() {
		initYourPlugins();
	}
   
	
	/**
	 * Initializing the plug-ins.
	 */
	private void initYourPlugins() {
		PluginManager pm = getManager();
		PluginRegistry registry = pm.getRegistry(); 
		
        ExtensionPoint extPoint = registry.getExtensionPoint(
                        getDescriptor().getId(), "socket");
       
        for (Extension ext : extPoint.getConnectedExtensions()) {
    		PluginDescriptor pd = ext.getDeclaringPluginDescriptor();
            ClassLoader classLoader = pm.getPluginClassLoader(pd);

            Parameter classesParam = ext.getParameter("classes");
	        
	        List<String> classPathList = TextParserUtil.split(
	        		classesParam.valueAsString(), ",", "");
	        
	        for (String classPath : classPathList) {
	        	try {
	        		
	        		Alg alg = (Alg)classLoader.loadClass(classPath).getDeclaredConstructor().newInstance();
	        		
	        		if (alg instanceof Recommender) {
	        			PluginStorage.getRecommenderReg().register(alg);
		    			logger.info("Recommender \"" + alg.getName() + "\" registered"); 
	        		}
	        		else if (alg instanceof DatasetParser) {
	        			PluginStorage.getParserReg().register(alg);
	        			logger.info("Parser \"" + alg.getName() + "\" registered"); 
	        		}
	        		else if (alg instanceof Metric) {
	        			PluginStorage.getMetricReg().register(alg);
	        			logger.info("Metric \"" + alg.getName() + "\" registered"); 
	        		}
	        		else if (alg instanceof ExternalQuery) {
	        			PluginStorage.getExternalQueryReg().register(alg);
	        			logger.info("External query \"" + alg.getName() + "\" registered"); 
	        		}
	        		else if (alg instanceof CTSManager) {
	        			PluginStorage.getCTSManagerReg().register(alg);
	        			logger.info("CTS manager \"" + alg.getName() + "\" registered"); 
	        		}
	        		
				} 
	        	catch (Throwable e) {
					// TODO Auto-generated catch block
					net.rem.regression2;
				}
	        	
	        }
			
				
        } // End extension
        
				
	}
	
	
}
