/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.app.ext;

import net.hudup.core.app.App;
import net.hudup.core.app.ApporAbstract;
import net.hudup.core.client.PowerServer;
import net.hudup.core.logistic.LogUtil;

/**
 * This is an extensive application creator.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public abstract class ApporExtAbstract extends ApporAbstract {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default test application creator.
	 */
	public ApporExtAbstract() {
		super();
	}

	
	@Override
	public App create(PowerServer server) {
		if (app != null) return app;
		
		if (server == null) return null;
		try {
			AppRemote remoteObject = createRemoteObject();
			try {if (remoteObject != null) remoteObject.export(server.getPort());}
			catch (Throwable e) {LogUtil.trace(e);}
			
			app = newApp(server, this, remoteObject);
			try {app.export(server.getPort());} catch (Throwable e) {LogUtil.trace(e);}
			return app;
		} catch (Throwable e) {LogUtil.trace(e);}
		
		return null;
	}

	
	/**
	 * Creating extensive application with server, extensive application creator, and remote object.
	 * @param server power server.
	 * @param appor extensive application creator.
	 * @param remoteObject remote object.
	 * @return application associated with power server.
	 */
	protected abstract AppExt newApp(PowerServer server, ApporExtAbstract appor, AppRemote remoteObject);
	
	
	/**
	 * Creating remote object. Default implementation returns null.
	 * @return remote object.
	 */
	protected AppRemote createRemoteObject() {
		return null;
	}
	
	
}
