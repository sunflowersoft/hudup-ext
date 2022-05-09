/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core;

import java.util.Collection;
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
	 * Map of extra taskers.
	 */
	protected final static Map<String, Tasker> taskers = Util.newMap();
	
	
	/**
	 * List of unmanaged exported objects.
	 */
	protected final static List<Exportable> unmanagedExportedObjects = Util.newList();
	
	
	/**
	 * Clearing taskers.
	 */
	public final static void clearTaskers() {
		synchronized (taskers) {
			taskers.clear();
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
	 * Adding tasker.
	 * @param tasker tasker.
	 * @return true if adding is successful.
	 */
	public final static boolean addTasker(Tasker tasker) {
		synchronized (taskers) {
			if (taskers.containsKey(tasker.getName()))
				return false;
			else {
				taskers.put(tasker.getName(), tasker);
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
	 * Removing tasker based on tasker name.
	 * @param taskerName tasker name.
	 * @return true if adding is successful.
	 */
	public final static boolean removeTasker(String taskerName) {
		synchronized (taskers) {
			if (taskers.containsKey(taskerName)) {
				return taskers.remove(taskerName) != null;
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
	 * Getting task given tasker name.
	 * @param taskerName tasker name.
	 * @return task given tasker name.
	 */
	public final static Tasker getTasker(String taskerName) {
		synchronized (taskers) {
			return taskers.get(taskerName);
		}
	}
	
	
	/**
	 * Getting collection of taskers.
	 * @return collection of taskers.
	 */
	public final static Collection<Tasker> getTaskers() {
		synchronized (taskers) {
			return taskers.values();
		}
	}
	
	
	/**
	 * Clearing this extra storage.
	 */
	public final static void clear() {
		try {
			clearTaskers();
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
