/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.EventListener;

import net.hudup.core.logistic.Pingable;
import net.hudup.core.logistic.ui.PluginStorageManifest;

/**
 * {@link PluginStorage} manages many {@link RegisterTable} (s) and each {@link RegisterTable} stores algorithms having the same type.
 * For example, a register table manages recommendation algorithms (recommenders) whereas another manages metrics for evaluating recommenders.
 * {@link PluginStorageManifest} which is the graphic user interface (GUI) allows users to manage {@link PluginStorage}.
 * Every time {@link PluginStorage} was changed, an event {@link PluginChangedEvent} is issued and dispatched to this listener {@link PluginChangedListener}.
 * Later on, {@link PluginChangedListener} can do some tasks in its method {@link PluginChangedListener#pluginChanged(PluginChangedEvent)}.
 * Please pay attention that such {@link PluginChangedListener} must be registered with {@link PluginStorageManifest} before to receive {@link PluginChangedEvent}.
 * <br>
 * Any class that implements this listener must define the method {@link #pluginChanged(PluginChangedEvent)} to specify some tasks when receiving a specified event {@link PluginChangedEvent}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public interface PluginChangedListener extends EventListener, Pingable, Remote {

	
	/**
	 * Any class that implements {@link PluginChangedListener} must define this method to specify some tasks when receiving a specified event {@link PluginChangedEvent}.
	 * @param evt specified {@link PluginChangedEvent}.
	 * @throws RemoteException if any error raises.
	 */
	void pluginChanged(PluginChangedEvent evt) throws RemoteException;
	
	
	/**
	 * Testing whether listener is idle.
	 * @return whether listener is idle.
	 * @throws RemoteException if any error raises.
	 */
	boolean isIdle() throws RemoteException;
	
	
	/**
	 * Getting port for remote call.
	 * @return port for remote call.
	 * @throws RemoteException if any error raises.
	 */
	int getPort() throws RemoteException;
	
	
}
