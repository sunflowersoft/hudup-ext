/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.app;

import java.io.Closeable;
import java.rmi.Remote;
import java.rmi.RemoteException;

import net.hudup.core.client.PowerServer;
import net.hudup.core.logistic.LogUtil;

/**
 * This is an application associated with power server.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public abstract class PowerServerApp extends AppAbstract {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Power server.
	 */
	protected PowerServer server = null;


	/**
	 * Remote object.
	 */
	protected Remote remoteObject = null;

	
	/**
	 * Constructor with server, application creator, and remote object.
	 * @param server power server.
	 * @param appor application creator.
	 * @param remoteObject remote object.
	 */
	public PowerServerApp(PowerServer server, PowerServerAppor appor, Remote remoteObject) {
		super(appor);
		this.server = server;
		this.remoteObject = remoteObject;
	}

	
	@Override
	protected boolean discard0() {
		if (remoteObject == null) return false;
		
		synchronized (remoteObject) {
			try {
				if (remoteObject instanceof Closeable) ((Closeable)remoteObject).close();
			} catch (Throwable e) {LogUtil.trace(e);}
			
			try {
				if (appor != null) ((PowerServerAppor)appor).unexportRemoteObject(remoteObject);
			} catch (Throwable e) {LogUtil.trace(e);}
			
			remoteObject = null;
		}
		
		return true;
	}

	
	@Override
	public boolean serverTask() throws RemoteException {
		return true;
	}


	@Override
	public Remote getRemoteObject() throws RemoteException {
		return remoteObject;
	}


}
