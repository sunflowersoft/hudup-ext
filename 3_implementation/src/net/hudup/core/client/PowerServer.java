/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.client;

import java.rmi.RemoteException;

import net.hudup.core.PluginAlgDesc2ListMap;
import net.hudup.core.PluginChangedListener;

/**
 * {@link PowerServer} is a powerful {@link Server} with advanced functions such as supporting balancing and retrieving service.
 * For example, there is a counter inside {@link ActiveMeasure}.
 * Note that {@link ActiveMeasure} interface specifies how to measure the active degree of {@link PowerServer}.
 * Each time {@link PowerServer} receives a user request, the counter is increased by 1.
 * After {@link PowerServer} finishes serving such user request, the counter is decreased by 1.
 * If there are many {@link PowerServer} (s) deployed on different sites, balancer uses {@link ActiveMeasure} to determine which {@link PowerServer} is least busy in order to dispatch user request to such {@link PowerServer} whose active counter is smallest.
 * <br>
 * Note, the sub-architecture of recommendation server (recommender) is inspired from the architecture of Oracle database management system (Oracle DBMS);
 * especially concepts of listener and share memory layer are borrowed from concepts &quot;Listener&quot; and &quot;System Global Area&quot; of Oracle DBMS, respectively,
 * available at <a href="https://docs.oracle.com/database/122/index.htm">https://docs.oracle.com/database/122/index.htm</a>.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public interface PowerServer extends Server, PluginChangedListener {
	
	
	/**
	 * Increasing the request counter by 1 when receiving an incoming request.
	 * @throws RemoteException if any error raises.
	 */
	void incRequest() throws RemoteException;
	
	
	/**
	 * Decreasing the request counter by 1 after completing a request.
	 * @throws RemoteException if any error raises.
	 */
	void decRequest() throws RemoteException;

	
	/**
	 * Getting the {@link ActiveMeasure}.
	 * {@link ActiveMeasure} interface specifies how to measure the active degree of {@link PowerServer}.
	 * The higher active degree is, the more busy the {@link PowerServer} is.
	 * @return {@link ActiveMeasure}.
	 * @throws RemoteException if any error raises.
	 */
	ActiveMeasure getActiveMeasure() throws RemoteException;
	
	
	/**
	 * Checking whether an account is valid with regard to specified password and specified privileges.
	 * Note, account is the information of a user who has access to Hudup server with her/his privileges. Account is modeled and stored in framework database as profile. 
	 * Account is the information of a user who has access to the server with her/his privileges. Account is modeled and stored in framework database as profile. 
	 * @param account specified account.
	 * @param password specified password.
	 * @param privileges specified privileges.
	 * @return whether account is valid.
	 * @throws RemoteException if any error raises.
	 */
	boolean validateAccount(String account, String password, int privileges) throws RemoteException;
	
	
	/**
	 * Each server is responsible for creating service represented by {@link Service} interface to serve user requests.
	 * In other words, each server own a service.
	 * @return Service the service own by this server.
	 * @throws RemoteException if any error raises.
	 */
	Service getService() throws RemoteException;

	
	/**
	 * Reloading plug-in storage.
	 * @param account specified account.
	 * @param password specified password.
	 * @return true if reloading is successful.
	 * @throws RemoteException if any error raises.
	 */
	boolean reloadPlugin(String account, String password) throws RemoteException;

	
	/**
	 * Applying remotely plug-in storage.
	 * @param pluginDescMap descriptions of algorithms in plug-in storage.
	 * @param account specified account.
	 * @param password specified password.
	 * @param cp class processor.
	 * @return true if reloading is successful.
	 * @throws RemoteException if any error raises.
	 */
	boolean applyPlugin(PluginAlgDesc2ListMap pluginDescMap, String account, String password, ClassProcessor cp) throws RemoteException;

	
	/**
	 * Getting the map that contains descriptions of algorithms in plug-in storage.
	 * @param account specified account.
	 * @param password specified password.
	 * @return the map that contains descriptions of algorithms in plug-in storage.
	 * @throws RemoteException if any error raises.
	 */
	PluginAlgDesc2ListMap getPluginAlgDescs(String account, String password) throws RemoteException;

	
}
