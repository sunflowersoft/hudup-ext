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
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import net.hudup.core.ExtraStorage;
import net.hudup.core.Util;
import net.hudup.core.client.PowerServer;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.NetUtil;

/**
 * This class is default implementation of dataset pools service.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class DatasetPoolsServiceImpl implements DatasetPoolsService, Serializable, Exportable, java.lang.AutoCloseable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Power server.
	 */
	protected PowerServer server = null;
	
	
    /**
     * Exported stub.
     */
    protected DatasetPoolsService exportedStub = null;

    
    /**
	 * Internal pool.
	 */
	protected Map<String, DatasetPoolExchangedItem> pools = Util.newMap();
	
	
	/**
	 * Default constructor with server.
	 * @param server specified server.
	 */
	public DatasetPoolsServiceImpl(PowerServer server) {
		this.server = server;
	}

	
	@Override
	public synchronized Set<String> names() throws RemoteException {
		Set<String> names = Util.newSet();
		names.addAll(pools.keySet());
		return names;
	}


	@Override
	public synchronized boolean contains(String name) throws RemoteException {
		return pools.containsKey(name);
	}


	@Override
	public synchronized DatasetPoolExchangedItem get(String name) throws RemoteException {
		return pools.get(name);
	}


	@Override
	public synchronized boolean put(String name, DatasetPoolExchanged pool, ClientWrapper...clients) throws RemoteException {
		try {
			if (name == null || pools.containsKey(name)) return false;
			DatasetPoolExchangedItem item = create(name, pool, clients);
			if (item == null) return false;
			
			DatasetPoolExchangedItem prev = pools.put(name, item);
			if (prev != null) prev.close();
			return true;
		} catch (Throwable e) {LogUtil.trace(e);}
		
		return false;
	}


	@Override
	public synchronized boolean remove(String name) throws RemoteException {
		try {
			DatasetPoolExchangedItem removed = pools.remove(name);
			if (removed != null) removed.close();
			return removed != null;
		} catch (Throwable e) {LogUtil.trace(e);}
		
		return false;
	}


	@Override
	public synchronized boolean removeClient(ClientWrapper client, boolean released) throws RemoteException {
		if (client == null) return false;
		Collection<DatasetPoolExchangedItem> items = pools.values();
		boolean doReleased = false;
		for (DatasetPoolExchangedItem item : items) {
			try {
				if (!item.containsClient(client)) continue;
				if (item.removeClient(client)) {
					if (released && !doReleased) {
						item.releaseClient(client);
						doReleased = true;
					}
				}
			} catch (Throwable e) {LogUtil.trace(e);}
		}

		return false;
	}


	@Override
	public boolean removeClient(Remote remoteClient, boolean released) throws RemoteException {
		if (remoteClient == null) return false;
		Collection<DatasetPoolExchangedItem> items = pools.values();
		boolean doReleased = false;
		for (DatasetPoolExchangedItem item : items) {
			try {
				ClientWrapper wrapper = item.wrapperOf(remoteClient);
				if (wrapper == null) continue;
				if (item.removeClient(wrapper)) {
					if (released && !doReleased) {
						item.releaseClient(wrapper);
						doReleased = true;
					}
				}
			} catch (Throwable e) {LogUtil.trace(e);}
		}

		return false;
	}


	@Override
	public synchronized void clear() throws RemoteException {
		Collection<DatasetPoolExchangedItem> items = pools.values();
		for (DatasetPoolExchangedItem item : items) {
			try {
				item.close();
			} catch (Throwable e) {LogUtil.trace(e);}
		}
		pools.clear();
	}


	@Override
	public synchronized Remote export(int serverPort) throws RemoteException {
		if (exportedStub == null) {
			exportedStub = (DatasetPoolsService) NetUtil.RegistryRemote.export(this, serverPort);
			if (exportedStub != null) LogUtil.info("Dataset pools service exported at port " + serverPort);
		}
		
		return exportedStub;
	}


	@Override
	public synchronized void unexport() throws RemoteException {
		if (exportedStub != null) {
			NetUtil.RegistryRemote.unexport(this);
			exportedStub = null;
	
			ExtraStorage.removeUnmanagedExportedObject(this);
			LogUtil.info("Dataset pools service unexported");
		}
	}

	
	@Override
	public void forceUnexport() throws RemoteException {
		unexport();
	}


	@Override
	public Remote getExportedStub() throws RemoteException {
		return exportedStub;
	}


	@Override
	public synchronized void close() throws Exception {
		try {
			clear();
		} catch (Throwable e) {LogUtil.trace(e);}
		
		try {
			unexport();
		} catch (Throwable e) {LogUtil.trace(e);}
	}

	
	/**
	 * Creating (new) an instance of pool.
	 * @param name name of pool.
	 * @param pool pool.
	 * @param clients list of clients.
	 * @return an instance of pool.
	 */
	protected DatasetPoolExchangedItem create(String name, DatasetPoolExchanged pool, ClientWrapper...clients) {
		if (name == null || pool == null) return null;
		try {
			pool.export(server.getPort(), false);
		} catch (Throwable e) {LogUtil.trace(e);}

		DatasetPoolExchangedItem item = new DatasetPoolExchangedItem(name, pool);
		if (clients != null && clients.length > 0) {
			for (ClientWrapper client : clients) {
				if (client != null) item.addClient(client);
			}
		}
		
		return item;
	}
	
	
}
