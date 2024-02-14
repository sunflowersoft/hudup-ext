/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.app;

import java.rmi.Remote;
import java.rmi.RemoteException;

import javax.swing.JOptionPane;

import net.hudup.core.client.ConnectInfo;
import net.hudup.core.client.PowerServer;
import net.hudup.core.data.Exportable;
import net.hudup.core.logistic.LogUtil;

/**
 * This is a test application.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class TestApp extends AppAbstract {

	
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
	 * Constructor with server, test application creator, and remote object.
	 * @param server power server.
	 * @param testAppor test application creator.
	 * @param remoteObject remote object.
	 */
	public TestApp(PowerServer server, TestAppor testAppor, Remote remoteObject) {
		super(testAppor);
		this.server = server;
		this.remoteObject = remoteObject;
	}

	
	@Override
	public String getDesc() throws RemoteException {
		return "Test application";
	}

	
	@Override
	protected boolean discard0() {
		if (remoteObject == null) return true;
		
		synchronized (remoteObject) {
			//Doing something.
			
			try {
				//Unexporting remote object.
				if (remoteObject instanceof Exportable)
					((Exportable)remoteObject).unexport();
				else {
					//Unexporting manually remote object.
				}
			} catch (Throwable e) {LogUtil.trace(e);}
			remoteObject = null;
		}
		
		return true;
	}

	
	@Override
	public boolean serverTask() throws RemoteException {
		//Doing something.
		return true;
	}

	
	@Override
	public void show(ConnectInfo connectInfo) throws RemoteException {
		if (connectInfo == null) return;
		
		try {
			//It is possible to obtain or refresh remote object.
			JOptionPane.showMessageDialog(null, getDesc(), appor.getName(), JOptionPane.INFORMATION_MESSAGE);
		} catch (Throwable e) {LogUtil.trace(e);}
	}

	
	@Override
	public Remote getRemoteObject() throws RemoteException {
		return remoteObject;
	}


}
