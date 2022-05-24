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
	protected Map<String, DatasetPoolExchanged> pools = Util.newMap();
	
	
	/**
	 * Default constructor.
	 */
	public DatasetPoolsServiceImpl(PowerServer server) {
		this.server = server;
	}

	
	@Override
	public synchronized Set<String> names() throws RemoteException {
		return pools.keySet();
	}


	@Override
	public boolean contains(String name) throws RemoteException {
		return pools.containsKey(name);
	}


	@Override
	public synchronized DatasetPoolExchanged get(String name) throws RemoteException {
		return pools.get(name);
	}


	@Override
	public synchronized boolean put(String name, DatasetPoolExchanged pool) throws RemoteException {
		try {
			DatasetPoolExchanged prev = pools.put(name, pool);
			pool.export(server.getPort(), false);
			if (prev != null) prev.unexport(true);
			return true;
		} catch (Throwable e) {LogUtil.trace(e);}
		
		return false;
	}


	@Override
	public synchronized boolean remove(String name) throws RemoteException {
		try {
			DatasetPoolExchanged prev = pools.remove(name);
			if (prev != null) prev.unexport(true);
			return prev != null;
		} catch (Throwable e) {LogUtil.trace(e);}
		
		return false;
	}


	@Override
	public synchronized void clear() throws RemoteException {
		Collection<DatasetPoolExchanged> values = pools.values();
		for (DatasetPoolExchanged pool : values) {
			try {
				pool.clear(true);
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

	
}
