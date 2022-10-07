/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data;

import java.io.Serializable;
import java.rmi.Remote;
import java.util.List;

import net.hudup.core.Util;
import net.hudup.core.logistic.DSUtil;
import net.hudup.core.logistic.LogUtil;

/**
 * This class represents an item of dataset pool with given name.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class DatasetPoolExchangedItem implements Serializable, Comparable<DatasetPoolExchangedItem>, java.lang.AutoCloseable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Name of dataset pool.
	 */
	protected String name = null;
	
	
	/**
	 * Internal dataset pool.
	 */
	protected DatasetPoolExchanged pool = null;
	
	
	/**
	 * List of clients.
	 */
	protected List<ClientWrapper> clients = Util.newList();
	
	
	/**
	 * Constructor with name and pool.
	 * @param name specified name.
	 * @param pool specified pool.
	 */
	public DatasetPoolExchangedItem(String name, DatasetPoolExchanged pool) {
		this.name = name;
		this.pool = pool;
	}


	/**
	 * Getting name.
	 * @return name.
	 */
	public String getName() {
		return name;
	}
	
	
	/**
	 * Getting pool.
	 * @return pool.
	 */
	public DatasetPoolExchanged getPool() {
		return pool;
	}
	
	
	/**
	 * Getting client size.
	 * @return client size.
	 */
	public int getClientSize() {
		return clients.size();
	}
	
	
	/**
	 * Getting wrapper of remote client.
	 * @param remoteClient remote client.
	 * @return wrapper of remote client.
	 */
	public ClientWrapper wrapperOf(Remote remoteClient) {
		if (remoteClient == null) return null;
		for (ClientWrapper wrapper : clients) {
			if (wrapper.getClient() == remoteClient) return wrapper;
		}
		
		return null;
	}
	
	
	/**
	 * Checking if containing specified client.
	 * @param client specified client.
	 * @return true if containing specified client.
	 */
	public boolean containsClient(ClientWrapper client) {
		if (client == null)
			return false;
		else if (clients.contains(client))
			return true;
		else
			return containsClient(client.getClient());
	}
	
	
	/**
	 * Checking if containing remote client.
	 * @param remoteClient remote client.
	 * @return true if containing remote client.
	 */
	public boolean containsClient(Remote remoteClient) {
		return wrapperOf(remoteClient) != null;
	}
	
	
	/**
	 * Getting client at specified index.
	 * @param index specified index.
	 * @return client at specified index.
	 */
	public ClientWrapper getClient(int index) {
		return clients.get(index);
	}
	
	
	/**
	 * Adding client.
	 * @param client specified client.
	 * @return true if adding client is successful.
	 */
	public boolean addClient(ClientWrapper client) {
		if (client == null || client.getClient() == null || containsClient(client))
			return false;
		else
			return clients.add(client);
	}
	
	
	/**
	 * Adding clients
	 * @param clients clients.
	 */
	public void addClients(ClientWrapper...clients) {
		if (clients == null || clients.length == 0) return;
		
		for (ClientWrapper client : clients) {
			if (client != null) addClient(client);
		}
	}
	
	
	/**
	 * Adding client.
	 * @param client remote client.
	 * @return true if adding client is successful.
	 */
	public boolean addClient(Remote remoteClient) {
		if (remoteClient == null || containsClient(remoteClient))
			return false;

		ClientWrapper wrapper = ClientWrapper.create(remoteClient, name, name);
		if (wrapper != null)
			return clients.add(wrapper);
		else
			return false;
	}

	
	/**
	 * Removing client at specified index.
	 * @param index specified index.
	 * @return removed client.
	 */
	public ClientWrapper removeClient(int index) {
		return clients.remove(index);
	}
	

	/**
	 * Removing specified client.
	 * @param client specified client.
	 * @return true if removing client is successful.
	 */
	public boolean removeClient(ClientWrapper client) {
		return clients.remove(client);
	}
	
	
	/**
	 * Releasing client.
	 * @param client specified client.
	 * @return true if releasing client is successful.
	 */
	protected boolean releaseClient(ClientWrapper client) {
		if (client == null) return false;
		try {
			client.close();
			return true;
		}
		catch (Throwable e) {LogUtil.trace(e);}
		
		return false;
	}
	
	
	@Override
	public int compareTo(DatasetPoolExchangedItem o) {
		return this.name.compareTo(o.name);
	}


	@Override
	public String toString() {
		return name != null ? DSUtil.shortenVerbalName(name) : super.toString();
	}


	@Override
	public void close() throws Exception {
		for (ClientWrapper client : clients) {
			try {
				releaseClient(client);
			} catch (Throwable e) {LogUtil.trace(e);}
		}
		clients.clear();
		
		if (pool != null) {
			try {
				pool.unexport(true);
			} catch (Throwable e) {LogUtil.trace(e);}
		}
		pool = null;
	}
	
	
}
