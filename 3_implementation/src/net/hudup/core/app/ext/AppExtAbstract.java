/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.app.ext;

import java.io.Closeable;
import java.rmi.Remote;
import java.rmi.RemoteException;

import javax.swing.event.EventListenerList;

import net.hudup.core.app.AppAbstract;
import net.hudup.core.client.PowerServer;
import net.hudup.core.logistic.LogUtil;

/**
 * This is an abstract implementation of extensive application.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public abstract class AppExtAbstract extends AppAbstract implements AppExt {

	
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
	protected AppRemote remoteObject = null;

	
	/**
	 * Holding a list of event listeners.
	 * 
	 */
    protected EventListenerList listenerList = new EventListenerList();

    
    /**
	 * Constructor with server, extensive application creator, and remote object.
	 * @param server power server.
	 * @param appor extensive application creator.
	 * @param remoteObject remote object.
	 */
	public AppExtAbstract(PowerServer server, ApporExtAbstract appor, AppRemote remoteObject) {
		super(appor);
		this.server = server;
		this.remoteObject = remoteObject;
	}

	
	@Override
	protected boolean discard0() {
		if (remoteObject == null) return true;
		
		synchronized (remoteObject) {
			try {
				if (remoteObject instanceof Closeable) ((Closeable)remoteObject).close();
			} catch (Throwable e) {LogUtil.trace(e);}
			
			try {
				remoteObject.unexport();
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


	@Override
	public void addListener(AppListener listener) throws RemoteException {
		synchronized (listenerList) {
			listenerList.add(AppListener.class, listener);
		}
	}


	@Override
	public void removeListener(AppListener listener) throws RemoteException {
		synchronized (listenerList) {
			listenerList.remove(AppListener.class, listener);
		}
	}


    /**
     * Return array of listeners.
     * @return array of listeners.
     */
    protected AppListener[] getListeners() {
		synchronized (listenerList) {
			return listenerList.getListeners(AppListener.class);
		}
    }

    
    /**
     * Firing (issuing) an event from this console to all listeners. 
     * @param evt event from this extensive application.
     */
    protected void fireEvent(AppEvent evt) {
		AppListener[] listeners = getListeners();
		for (AppListener listener : listeners) {
	    	try {
				listener.receiveEvent(evt);
			}
			catch (Exception e) {LogUtil.trace(e);}
		}
    }


}
