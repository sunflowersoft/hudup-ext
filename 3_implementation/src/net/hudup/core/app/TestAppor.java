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
 * This is an test application creator.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class TestAppor extends ApporAbstract {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default test application creator.
	 */
	public TestAppor() {
		super();
	}

	
	@Override
	public String getName() {
		return "tester";
	}


	@Override
	public App create(PowerServer server) {
		if (app != null) return app;
		
		if (server == null) return null;
		try {
			//Getting or creating remote object.
			Remote remoteObject = null;
			//Exporting remote object here.
			
			app = new TestApp(server, this, remoteObject);
			try {app.export(server.getPort());} catch (Throwable e) {LogUtil.trace(e);}
			return app;
		} catch (Throwable e) {LogUtil.trace(e);}
		
		return null;
	}

	
}
