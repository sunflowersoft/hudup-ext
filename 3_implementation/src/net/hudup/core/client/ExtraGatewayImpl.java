/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.client;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import net.hudup.core.App;
import net.hudup.core.data.DataConfig;

/**
 * This abstract class is a partial implementation of extra gateway. 
 */
public class ExtraGatewayImpl implements ExtraGateway {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Internal server.
	 */
	protected PowerServer server = null;
	
	
	/**
	 * Constructor with server.
	 * @param server power server.
	 */
	public ExtraGatewayImpl(PowerServer server) {
		this.server = server;
	}

	
	@Override
	public Remote getAppRemoteObject(String account, String password, String appName) throws RemoteException {
		if (server == null) return null;
		ExtraService extraService = server.getExtraService();
		if (extraService == null) return null;
		
		int privs = server.getPrivileges(account, password);
		if ((privs & DataConfig.ACCOUNT_EVALUATE_PRIVILEGE) != DataConfig.ACCOUNT_EVALUATE_PRIVILEGE) return null;
		
		List<App> apps = extraService.getApps();
		for (App app : apps) {
			if (app.getName().equals(appName)) return app.getRemoteObject();
		}
		
		return null;
	}

	
}
