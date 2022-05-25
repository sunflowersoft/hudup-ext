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
import net.hudup.core.evaluate.Evaluator;
import net.hudup.core.logistic.DSUtil;
import net.hudup.core.logistic.LogUtil;

/**
 * This class represents an item of dataset pool with given name.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class DatasetPoolExchangedItem implements Serializable, Comparable<DatasetPoolExchangedItem>, AutoCloseable {

	
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
	protected List<Remote> clients = Util.newList();
	
	
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
	 * Checking if containing specified client.
	 * @param client specified client.
	 * @return true if containing specified client.
	 */
	public boolean containsClient(Remote client) {
		return clients.contains(client);
	}
	
	
	/**
	 * Getting client at specified index.
	 * @param index specified index.
	 * @return client at specified index.
	 */
	public Remote getClient(int index) {
		return clients.get(index);
	}
	
	
	/**
	 * Adding client.
	 * @param client specified client.
	 * @return true if adding client is successful.
	 */
	public boolean addClient(Remote client) {
		if (client == null || clients.contains(client))
			return false;
		else
			return clients.add(client);
	}
	
	
	/**
	 * Removing client at specified index.
	 * @param index specified index.
	 * @return removed client.
	 */
	public Remote removeClient(int index) {
		return clients.remove(index);
	}
	

	/**
	 * Removing specified client.
	 * @param client specified client.
	 * @return true if removing client is successful.
	 */
	public boolean removeClient(Remote client) {
		return clients.remove(client);
	}
	
	
	/**
	 * Releasing client.
	 * @param client specified client.
	 */
	protected void releaseClient(Remote client) {
		try {
			if (client == null)
				return;
			else if (client instanceof Evaluator) {
				Evaluator evaluator = (Evaluator)client;
				evaluator.remoteStop();
				evaluator.refPool(false, null, name, null, null);
			}
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
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
		for (Remote client : clients) {
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
