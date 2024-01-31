/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.client;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.hudup.core.ExtraStorage;
import net.hudup.core.Util;
import net.hudup.core.app.App;
import net.hudup.core.app.Appor;
import net.hudup.core.logistic.LogUtil;

/**
 * This abstract class implements partially the extra service.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class ExtraServiceImpl implements ExtraService, Serializable {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Internal server.
	 */
	protected PowerServer server = null;
	
	
	/**
	 * List of application creators.
	 */
	protected Map<String, App> apps = Util.newMap();
	
	
	/**
	 * Default constructor.
	 * @param server power server.
	 */
	public ExtraServiceImpl(PowerServer server) {
		this.server = server;
	}

	
	@Override
	public boolean open() throws RemoteException {
		try {
			close();
		}
		catch (Exception e) {LogUtil.trace(e);}
		
		try {
			synchronized (this.apps) {
				List<Appor> appors = ExtraStorage.getAppors();
				for (Appor appor : appors) {
					try {
						App app = appor.create(this.server);
						if (app != null && !this.apps.containsKey(app.getName())) {
							this.apps.put(app.getName(), app);
						}
					}
					catch (Exception e) {LogUtil.trace(e);}
				}
			}
		}
		catch (Exception e) {LogUtil.trace(e);}
		
		return true;
	}


	@Override
	public void close() throws Exception {
		synchronized (this.apps) {
			Collection<App> appList = this.apps.values();
			for (App app : appList) {
				try {
					app.discard();
				} catch (Throwable e) {LogUtil.trace(e);}
			}
			this.apps.clear();
		}
	}


	/**
	 * Getting application names.
	 * @return application names.
	 */
	public List<String> getAppNames() {
		synchronized (this.apps) {
			List<String> appNames = Util.newList();
			appNames.addAll(this.apps.keySet());
			Collections.sort(appNames);
			return appNames;
		}
	}
	
	
	/**
	 * Getting application by name.
	 * @param appName application name.
	 * @return application given name.
	 */
	public App getApp(String appName) {
		synchronized (this.apps) {
			return this.apps.get(appName);
		}
	}
	
	
	@Override
	public List<App> getApps() throws RemoteException {
		synchronized (this.apps) {
			List<String> appNames = getAppNames();
			List<App> appList = Util.newList(appNames.size());
			for (String appName : appNames) {
				appList.add(this.apps.get(appName));
			}
			return appList;
		}
	}
	
	
	@Override
	public void updateApps() throws RemoteException {
		synchronized (this.apps) {
			List<Appor> appors = ExtraStorage.getAppors();
			Set<String> sysAppNames= Util.newSet();
			for (Appor appor : appors) {
				try {
					sysAppNames.add(appor.getName());
					if (this.apps.containsKey(appor.getName())) continue;
					App app = appor.create(this.server);
					if (app == null) continue;
					
					sysAppNames.add(app.getName());
					
					if (this.apps.containsKey(app.getName()))
						app.discard();
					else 
						this.apps.put(app.getName(), app);
				}
				catch (Exception e) {LogUtil.trace(e);}
			}
			
			List<String> appNames = getAppNames();
			appNames.removeAll(sysAppNames);
			for (String appName : appNames) {
				try {
					App app = this.apps.get(appName);
					if (app == null) continue;
					
					app.discard();
					this.apps.remove(appName);
				} catch (Exception e) {LogUtil.trace(e);}
			}
		}
		
	}


}
