/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic;

import java.net.URL;
import java.util.Properties;

import net.hudup.core.Constants;
import net.hudup.core.data.PropList;
import net.hudup.core.logistic.NetUtil.InetHardware;

/**
 * Utility class for system tasks.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public final class SystemUtil {

	
	/**
	 * Enhancing system performance.
	 */
	public final static void enhance() {
		try {
			System.runFinalization();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
		try {
			System.gc();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
		// Do more
	}

	
	/**
	 * Enhancing system performance automatically.
	 */
	public final static void enhanceAuto() {
		try {
			enhance();
			LogUtil.info("SystemUtil#enhanceAuto() automatically calls system enhancement at thread " + Thread.currentThread());
		}
		catch (Throwable e) {
			LogUtil.trace(e);
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
					"Vendor \"" + sysProps.getProperty("java.vendor") + "\" at " + sysProps.getProperty("java.vendor.url") + ", " +
					"Home \"" + sysProps.getProperty("java.home") + "\""
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
			
			props.put("Memory (VM)", 
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
			LogUtil.trace(e);
		}
		
		try {
			StringBuffer na = new StringBuffer(); 
			if (Constants.hostAddress != null)
				na.append("Local address is " + Constants.hostAddress);
//			String publicIP = NetUtil.getPublicInetAddress();
//			if (publicIP != null) {
//				if (na.length() > 0) na.append(", ");
//				na.append("Internet address is " + publicIP);
//			}
			props.put("Network", na.toString());
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}

		return props;
	}
	
	
	/**
	 * Refreshing system properties.
	 */
	public static void refreshSystemProperties() {
		try {
			InetHardware ih = NetUtil.getInetHardware();
			if (ih != null && ih.ni != null && ih.inetAddr != null) {
				Constants.hardwareAddress = ih.getMACAddress();
				Constants.hostAddress = ih.inetAddr.getHostAddress();
			}
			if (Constants.hardwareAddress == null || Constants.hostAddress == null) {
				Constants.hardwareAddress = null;
				Constants.hostAddress = null;
			}
		}
		catch (Throwable e) {
			Constants.hardwareAddress = null;
			Constants.hostAddress = null;
			System.out.println("Error when getting MAC and host addresses");
		}

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
			LogUtil.trace(e);
		}
		
		return -1;
	}
	
	
	/**
	 * Setting security policy.
	 * @param policyUrl security policy URL.
	 * @return true if setting is successful.
	 */
	@SuppressWarnings("deprecation")
	public static boolean setSecurityPolicy(URL policyUrl) {
		if (policyUrl == null) return false;
		
		System.setProperty("java.security.policy", policyUrl.toString());
		if (System.getSecurityManager() == null) {
			int version = SystemUtil.getJavaVersion();
			if (version <= 8)
				System.setSecurityManager(new java.rmi.RMISecurityManager());
			else
				System.setSecurityManager(new SecurityManager());
		}
		
		return true;
	}
	
}
