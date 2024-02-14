/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic.console;

import java.rmi.Remote;
import java.rmi.RemoteException;

import net.hudup.core.app.AppAbstract;
import net.hudup.core.client.ConnectInfo;
import net.hudup.core.client.PowerServer;
import net.hudup.core.logistic.LogUtil;

/**
 * This class represents console application.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public abstract class ConsoleApp extends AppAbstract {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Power server.
	 */
	protected PowerServer server = null;


	/**
	 * Internal console.
	 */
	protected Console console = null;

	
	/**
	 * Constructor with server, console application creator, and console.
	 * @param server power server.
	 * @param consoleAppor console application creator.
	 * @param console console.
	 */
	public ConsoleApp(PowerServer server, ConsoleAppor consoleAppor, Console console) {
		super(consoleAppor);
		this.server = server;
		this.console = console;
	}

	
	@Override
	protected boolean discard0() {
		if (console == null) return true;
		
		synchronized (console) {
			try {
				console.close();
			} catch (Throwable e) {LogUtil.trace(e);}
			
			try {
				console.unexport();
			} catch (Throwable e) {LogUtil.trace(e);}

			console = null;
		}
		return true;
	}

	
	@Override
	public boolean serverTask() throws RemoteException {
		return true;
	}

	
	@Override
	public void show(ConnectInfo connectInfo) throws RemoteException {
		if (connectInfo == null) return;
		
		try {
			ConsoleCP ccp = new ConsoleCP(console, connectInfo);
			ccp.setTitle(getDesc());
			ccp.setVisible(true);
		} catch (Throwable e) {LogUtil.trace(e);}
	}

	
	@Override
	public Remote getRemoteObject() throws RemoteException {
		return console;
	}


	/**
	 * Console task.
	 */
	protected abstract void consoleTask();
	
	
}
