/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.hudup.core.data.Exportable;
import net.hudup.core.logistic.LogUtil;

/**
 * This class is storage to store extra objects besides plug-in storage.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class ExtraStorage {


	/**
	 * Map of extra application creators.
	 */
	protected final static Map<String, Appor> appors = Util.newMap();
	
	
	/**
	 * List of unmanaged exported objects.
	 */
	protected final static List<Exportable> unmanagedExportedObjects = Util.newList();
	
	
	/**
	 * Clearing application creators.
	 */
	public final static void clearAppors() {
		synchronized (appors) {
			Collection<Appor> apporList = appors.values();
			for (Appor appor : apporList) {
				try {appor.close();} catch (Throwable e) {}
			}
			
			appors.clear();
		}
	}

	
	/**
	 * Clearing unmanaged exported objects.
	 */
	public final static void clearUnmanagedExportedObjects() {
		synchronized (unmanagedExportedObjects) {
			for (Exportable obj : unmanagedExportedObjects) {
				try {
					obj.unexport();
				} catch (Throwable e) {LogUtil.trace(e);}
			}
			
			unmanagedExportedObjects.clear();
		}
	}
	
	
	/**
	 * Adding application creator.
	 * @param appor application creator.
	 * @return true if adding is successful.
	 */
	public final static boolean addAppor(Appor appor) {
		synchronized (appors) {
			if (appors.containsKey(appor.getName()))
				return false;
			else {
				appors.put(appor.getName(), appor);
				return true;
			}
		}
	}
	
	
	/**
	 * Adding unmanaged exported object.
	 * @param obj unmanaged exported object added.
	 */
	public final static void addUnmanagedExportedObject(Exportable obj) {
		synchronized (unmanagedExportedObjects) {
			unmanagedExportedObjects.add(obj);
		}
	}

	
	/**
	 * Removing application creator based on application creator name.
	 * @param apporName application creator name.
	 * @return true if adding is successful.
	 */
	public final static boolean removeAppor(String apporName) {
		synchronized (appors) {
			if (appors.containsKey(apporName)) {
				Appor appor = appors.remove(apporName);
				try {if (appor != null) appor.close();} catch (Throwable e) {}
				return appor != null;
			}
			else
				return false;
		}
	}
	
	
	/**
	 * Adding unmanaged exported object.
	 * @param obj unmanaged exported object removed.
	 */
	public final static void removeUnmanagedExportedObject(Exportable obj) {
		synchronized (unmanagedExportedObjects) {
			try {
				unmanagedExportedObjects.remove(obj);
			} catch (Throwable e) {LogUtil.trace(e);}
		}
	}

	
	/**
	 * Getting application creator given name.
	 * @param apporName application creator name.
	 * @return application creator given name.
	 */
	public final static Appor getAppor(String apporName) {
		synchronized (appors) {
			return appors.get(apporName);
		}
	}
	
	
	/**
	 * Getting application creators names.
	 * @return application creators names.
	 */
	public final static List<String> getApporNames() {
		synchronized (appors) {
			List<String> apporNames = Util.newList();
			apporNames.addAll(appors.keySet());
			Collections.sort(apporNames);
			return apporNames;
		}
		
	}
	
	
	/**
	 * Getting list of application creators.
	 * @return list of application creators.
	 */
	public final static List<Appor> getAppors() {
		synchronized (appors) {
			List<String> apporNames = getApporNames();
			List<Appor> apporList = Util.newList(apporNames.size());
			for (String appName : apporNames) {
				apporList.add(appors.get(appName));
			}
			return apporList;
		}
	}
	
	
	/**
	 * Clearing this extra storage.
	 */
	public final static void clear() {
		try {
			clearAppors();
		} catch (Throwable e) {LogUtil.trace(e);}
		
		try {
			clearUnmanagedExportedObjects();
		} catch (Throwable e) {LogUtil.trace(e);}
	}
	
	
	/**
	 * Adding shutdown hook to clear extra storage.
	 */
	static {
		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {
				//This code line is not redundant. Please concern the keyword synchronized in releaseAllRegisteredAlgs().
				clear();
			}
			
		});
		
	}


}
