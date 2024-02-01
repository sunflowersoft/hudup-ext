/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.app;

import java.rmi.Remote;

import net.hudup.core.client.PowerServer;
import net.hudup.core.logistic.LogUtil;

/**
 * This is an application creator associated with power server.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public abstract class PowerServerAppor extends ApporAbstract {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default test application creator.
	 */
	public PowerServerAppor() {
		super();
	}

	
	@Override
	public App create(PowerServer server) {
		if (app != null) return app;
		
		if (server == null) return null;
		try {
			Remote remoteObject = createRemoteObject(true);
			
			app = newApp(server, this, remoteObject);
			try {app.export(server.getPort());} catch (Throwable e) {LogUtil.trace(e);}
			return app;
		} catch (Throwable e) {LogUtil.trace(e);}
		
		return null;
	}

	
	/**
	 * Creating application with server, application creator, and remote object.
	 * @param server power server.
	 * @param appor application creator.
	 * @param remoteObject remote object.
	 * @return application associated with power server.
	 */
	protected abstract PowerServerApp newApp(PowerServer server, PowerServerAppor appor, Remote remoteObject);
	
	
	/**
	 * Creating remote object.
	 * @param export exporting flag.
	 * @return remote object.
	 */
	protected abstract Remote createRemoteObject(boolean export);
	
	
	/**
	 * Unexporting remote object.
	 * @param remoteObject remote object.
	 */
	protected abstract void unexportRemoteObject(Remote remoteObject);
	
	
}
