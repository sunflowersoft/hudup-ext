package net.hudup.core;

import java.util.List;

import net.hudup.core.data.Exportable;

/**
 * This class is storage to store extra objects besides plug-in storage.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class ExtraStorage {


	/**
	 * List of unmanaged exported objects.
	 */
	protected final static List<Exportable> unmanagedExportedObjects = Util.newList();
	
	
	/**
	 * Clearing unmanaged exported objects.
	 */
	public final static void clearUnmanagedExportedObjects() {
		synchronized (unmanagedExportedObjects) {
			for (Exportable obj : unmanagedExportedObjects) {
				try {
					obj.unexport();
				} catch (Throwable e) {e.printStackTrace();}
			}
			
			unmanagedExportedObjects.clear();
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
	 * Adding unmanaged exported object.
	 * @param obj unmanaged exported object removed.
	 */
	public final static void removeUnmanagedExportedObject(Exportable obj) {
		synchronized (unmanagedExportedObjects) {
			try {
				if (unmanagedExportedObjects.size() == 0) return;
				
				unmanagedExportedObjects.remove(obj);
			} catch (Throwable e) {e.printStackTrace();}
		}
	}

	
	/**
	 * Clearing this extra storage.
	 */
	public final static void clear() {
		clearUnmanagedExportedObjects();
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
