/**
 * 
 */
package net.hudup.core.logistic;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.reflections.Reflections;

import net.hudup.core.Util;
import net.hudup.core.data.PropList;

/**
 * Utility class for system tasks.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public final class SystemUtil {

	
	/**
	 * Logger of this class.
	 */
	protected final static Logger logger = Logger.getLogger(SystemUtil.class);

	
	/**
	 * Enhancing system performance.
	 */
	public final static void enhance() {
		try {
			System.runFinalization();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		
		try {
			System.gc();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		
		// Do more
	}

	
	/**
	 * Enhancing system performance automatically.
	 */
	public final static void enhanceAuto() {
		try {
			enhance();
			logger.info("SystemUtil#enhanceAuto automatically calls system enhancement at thread " + Thread.currentThread());
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Getting system properties.
	 * @return system properties.
	 */
	public static PropList getSystemProperties() {
		PropList props = new PropList();
		
		try {
			Properties sysProps = System.getProperties();
			
			props.put("Java", 
					sysProps.getProperty("java.runtime.name") + " version " + sysProps.getProperty("java.runtime.version") + ", " +
					sysProps.getProperty("java.vm.name") + " version " + sysProps.getProperty("java.vm.version") + ", " +
					"Class version " + sysProps.getProperty("java.class.version") + ", " +
					"Vendor \"" + sysProps.getProperty("java.vendor") + "\" at " + sysProps.getProperty("java.vendor.url") 
				);
			
			props.put("OS", 
					sysProps.getProperty("os.name") + ", " +
					sysProps.getProperty("os.arch") + ", " +
					"version " + sysProps.getProperty("os.version")
				);
			
			Runtime runtime = Runtime.getRuntime();
			double allocatedMemory = runtime.totalMemory() / 1024.0 / 1024.0;
			double freeMemory = runtime.freeMemory() / 1024.0 / 1024.0;
			double maxMemory = runtime.maxMemory() / 1024.0 / 1024.0;
			
			props.put("Memory(VM)", 
					"Allocated memory = " + MathUtil.format(allocatedMemory, 2) + "MB, " +
					"Free memory = " + MathUtil.format(freeMemory, 2) + "MB, " +
					"Max memory = " + MathUtil.format(maxMemory, 2) + "MB"
				);
			
			props.put("CPU", 
					System.getenv("PROCESSOR_IDENTIFIER") + ", " +
					System.getenv("PROCESSOR_ARCHITECTURE") + ", " +
					"the number of processors is " + System.getenv("NUMBER_OF_PROCESSORS")
				);
		
			props.put("Directory", 
					"Current working directory is \"" + System.getProperty("user.dir") + "\""
				);
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		
		return props;
	}
	
	
	/**
	 * Getting Java version. Source code is available at <a href="https://stackoverflow.com/questions/2591083/getting-java-version-at-runtime">https://stackoverflow.com/questions/2591083/getting-java-version-at-runtime</a>.
	 * Java 8 or lower: 1.6.0_23, 1.7.0, 1.7.0_80, 1.8.0_211.
	 * Java 9 or higher: 9.0.1, 11.0.4, 12, 12.0.1.
	 * @return Java version like 6, 7, 8, 9.
	 */
	public static int getJavaVersion() {
		try {
			String version = System.getProperty("java.version");
		    if(version.startsWith("1."))
		        version = version.substring(2, 3);
		    else {
		        int dot = version.indexOf(".");
		        if (dot != -1) 
		        	version = version.substring(0, dot);
		    }
		    return Integer.parseInt(version);
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		
		return -1;
	}
	
	
	/**
	 * Getting a list of instances from specified package and referred class.
	 * @param <T> type of returned instances.
	 * @param prefix specified package.
	 * @param referredClass referred class.
	 * @return list of instances from specified package and referred class.
	 */
	public static <T> List<T> getInstances(String prefix, Class<T> referredClass) {
		prefix = UriAdapter.packageSlashToDot(prefix);		
		Reflections reflections = new Reflections(prefix);
		Set<Class<? extends T>> apClasses = reflections.getSubTypesOf(referredClass);
		List<T> instances = Util.newList();
		for (Class<? extends T> apClass : apClasses) {
			if (!referredClass.isAssignableFrom(apClass))
				continue;
			
			if (apClass.isInterface() || apClass.isMemberClass() || apClass.isAnonymousClass())
				continue;
			
			int modifiers = apClass.getModifiers();
			if ( (modifiers & Modifier.ABSTRACT) != 0 || (modifiers & Modifier.PUBLIC) == 0)
				continue;
			
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
	
	
}
