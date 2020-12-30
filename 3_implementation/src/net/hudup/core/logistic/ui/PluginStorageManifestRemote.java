/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic.ui;

import java.util.List;
import java.util.Vector;

import net.hudup.core.PluginAlgDesc2ListMap;
import net.hudup.core.PluginStorage;
import net.hudup.core.Util;
import net.hudup.core.alg.AlgDesc2;
import net.hudup.core.alg.AlgDesc2List;
import net.hudup.core.client.ConnectInfo;
import net.hudup.core.client.PowerServer;
import net.hudup.core.logistic.LogUtil;

/**
 * This class allows to manage plug-in storage from remote connection. It is extended version of {@link PluginStorageManifest}.
 * 
 * @author Loc Nguyen
 * @version 13.0
 *
 */
public class PluginStorageManifestRemote extends PluginStorageManifest {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Constructor with power server.
	 * @param server power server.
	 * @param connectInfo connection information.
	 */
	public PluginStorageManifestRemote(PowerServer server, ConnectInfo connectInfo) {
		this(server, connectInfo, 0);
	}


	/**
	 * Default constructor with power server, specified model and port.
	 * @param server power server.
	 * @param connectInfo connection information.
	 * @param port exported port. This port is used to export algorithms. Port 0 means to export algorithms at random port for getting remote references, not for naming.
	 */
	protected PluginStorageManifestRemote(PowerServer server, ConnectInfo connectInfo, int port) {
		super(new RegisterTMRemote(server, connectInfo), port);
	}


	@Override
	protected boolean reload() {
		boolean idle = isListenersIdle();
		if (!idle) return false;

		ConnectInfo connectInfo = getConnectInfo();
		PowerServer server = getServer();
		try {
			boolean reloaded = server.reloadPlugin(connectInfo.account.getName(), connectInfo.account.getPassword());
			if (reloaded) update();
			return reloaded;
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}

		return false;
	}


	@Override
	public boolean apply() {
		boolean idle = isListenersIdle();
		if (!idle) return false;
		
		PluginAlgDesc2ListMap pluginDescMap = new PluginAlgDesc2ListMap();
		
		int n = getRowCount();
		for (int i = 0; i < n; i++) {
			AlgDesc2 algDesc = (AlgDesc2) getValueAt(i, 3);
			boolean registered = (Boolean)getValueAt(i, 4);
			boolean isExported = (Boolean)getValueAt(i, 5);
			boolean removed = (Boolean)getValueAt(i, 6);
			
			if (registered == algDesc.registered && isExported == algDesc.isExported && removed == algDesc.removed)
				continue;
			
			algDesc.registered = registered;
			algDesc.inNextUpdateList = !registered;
			algDesc.isExported = isExported;
			algDesc.removed = removed;
			
			AlgDesc2List algDescs = pluginDescMap.get(algDesc.tableName);
			if (algDescs == null) {
				algDescs = new AlgDesc2List();
				pluginDescMap.put(algDesc.tableName, algDescs);
			}
			algDescs.add(algDesc);
		}
		
		if (pluginDescMap.size() == 0) return true;
		
		ConnectInfo connectInfo = getConnectInfo();
		PowerServer server = getServer();
		try {
			boolean applied = server.applyPlugin(pluginDescMap, connectInfo.account.getName(), connectInfo.account.getPassword());
			if (applied) update();
			return applied;
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}

		return false;
	}


	/**
	 * Getting server.
	 * @return server.
	 */
	public PowerServer getServer() {
		return ((RegisterTMRemote)getRegisterTM()).getServer();
	}

	
	/**
	 * Getting connection information.
	 * @return connection information.
	 */
	public ConnectInfo getConnectInfo() {
		return ((RegisterTMRemote)getRegisterTM()).getConnectInfo();
	}


}



/**
 * This is table model of {@link PluginStorageManifestRemote} because {@link PluginStorageManifestRemote} is itself a table. 
 * 
 * @author Loc Nguyen
 * @version 13.0
 *
 */
class RegisterTMRemote extends RegisterTM {
	

	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	

	/**
	 * Power server.
	 */
	protected PowerServer server;
	
	
	/**
	 * Connection information.
	 */
	protected ConnectInfo connectInfo;
	
	/**
	 * Internal plug-in map.
	 */
	PluginAlgDesc2ListMap pluginMap;
	
	
	/**
	 * Default constructor.
	 * @param server power server.
	 * @param connectInfo connection information.
	 */
	public RegisterTMRemote(PowerServer server, ConnectInfo connectInfo) {
		super();
		this.server = server;
		this.connectInfo = connectInfo != null ? connectInfo : new ConnectInfo(); 
		
		this.pluginMap = new PluginAlgDesc2ListMap();
		
		update();
	}


	@Override
	public void update() {
		if (server == null) {
			Vector<Vector<Object>> data = Util.newVector();
			setDataVector(data, toColumns());
			
			this.pluginMap = new PluginAlgDesc2ListMap();
			this.modified = false;
			
			return;
		}
		
		PluginAlgDesc2ListMap pluginMap = new PluginAlgDesc2ListMap();
		try {
			pluginMap = server.getPluginAlgDescs(connectInfo.account.getName(), connectInfo.account.getPassword());
		} catch (Exception e) {LogUtil.trace(e);}
		
		Vector<Vector<Object>> data = Util.newVector();
		String[] regNames = PluginStorage.getRegisterTableNames();
		List<AlgDesc2> nextUpdateList = Util.newList();
		for (String regName : regNames) {
			AlgDesc2List algDescs = pluginMap.get(regName);
			if (algDescs == null) continue;
			
			for (int i = 0; i < algDescs.size(); i++) {
				AlgDesc2 algDesc = algDescs.get(i);
				if (algDesc.inNextUpdateList) {
					nextUpdateList.add(algDesc);
					continue;
				}

				Vector<Object> row = Util.newVector();
				
				row.add(algDesc.tableName);
				row.add(algDesc.algName);
				row.add(algDesc.getAlgClassName());
				
				row.add(algDesc);
				
				row.add(algDesc.registered);
				row.add(algDesc.isExported);
				row.add(false);
				
				data.add(row);
			}
		}
		
		for (AlgDesc2 algDesc : nextUpdateList) {
			Vector<Object> row = Util.newVector();
			
			row.add(algDesc.tableName);
			row.add(algDesc.algName);
			row.add(algDesc.getAlgClassName());
			
			row.add(algDesc);
			
			row.add(algDesc.registered);
			row.add(algDesc.isExported);
			row.add(false);
			
			data.add(row);
		}
		
		this.pluginMap = pluginMap;
		
		setDataVector(data, toColumns());
		
		modified = false;
	}
	
	
	/**
	 * Getting server.
	 * @return server.
	 */
	public PowerServer getServer() {
		return server;
	}

	
	/**
	 * Getting connection information.
	 * @return connection information.
	 */
	public ConnectInfo getConnectInfo() {
		return connectInfo;
	}
	
	
}
