/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic.ui;

import java.util.Vector;

import net.hudup.core.PluginAlgDesc2ListMap;
import net.hudup.core.PluginStorage;
import net.hudup.core.Util;
import net.hudup.core.alg.AlgDesc2;
import net.hudup.core.alg.AlgDesc2List;
import net.hudup.core.client.ConnectInfo;
import net.hudup.core.client.PowerServer;
import net.hudup.core.data.Wrapper;
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
	protected void update() {
		super.update();
		setEditable(false);
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
	 * Default constructor.
	 * @param server power server.
	 * @param connectInfo connection information.
	 */
	public RegisterTMRemote(PowerServer server, ConnectInfo connectInfo) {
		super();
		this.server = server;
		this.connectInfo = connectInfo != null ? connectInfo : new ConnectInfo(); 
		
		update();
	}


	@Override
	public void update() {
		if (server == null) {
			Vector<Vector<Object>> data = Util.newVector();
			setDataVector(data, toColumns());
			modified = false;
			return;
		}
		
		if (connectInfo.bindUri == null) {
			super.update();
			return;
		}
		
		PluginAlgDesc2ListMap pluginMap = new PluginAlgDesc2ListMap();
		try {
			pluginMap = server.getPluginAlgDescs(connectInfo.account.getName(), connectInfo.account.getPassword());
		} catch (Exception e) {LogUtil.trace(e);}
		
		Vector<Vector<Object>> data = Util.newVector();
		String[] regNames = PluginStorage.getRegisterTableNames();
		for (String regName : regNames) {
			AlgDesc2List algDescs = pluginMap.get(regName);
			if (algDescs == null) continue;
			
			for (int i = 0; i < algDescs.size(); i++) {
				AlgDesc2 algDesc = algDescs.get(i);
				Vector<Object> row = Util.newVector();
				
				row.add(algDesc.tableName);
				row.add(algDesc.algName);
				row.add(algDesc.getAlgClassName());
				
				Wrapper algDescWrapper = new Wrapper(algDesc) {

					/**
					 * Serial version UID for serializable class.
					 */
					private static final long serialVersionUID = 1L;

					@Override
					public String toString() {
						return ((AlgDesc2)getObject()).algName;
					}
					
				};
				row.add(algDescWrapper);
				
				row.add(true);
				row.add(algDesc.isExported);
				row.add(false);
				
				data.add(row);
			}
		}
		
		AlgDesc2List algDescs = pluginMap.get(PluginStorage.NEXT_UPDATE_LIST);
		if (algDescs == null) algDescs = new AlgDesc2List();
		for (int i = 0; i < algDescs.size(); i++) {
			AlgDesc2 algDesc = algDescs.get(i);
			Vector<Object> row = Util.newVector();
			
			row.add(algDesc.tableName);
			row.add(algDesc.algName);
			row.add(algDesc.getAlgClassName());
			
			Wrapper algDescWrapper = new Wrapper(algDesc) {

				/**
				 * Serial version UID for serializable class.
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public String toString() {
					return ((AlgDesc2)getObject()).algName;
				}
				
			};
			row.add(algDescWrapper);
			
			row.add(false);
			row.add(algDesc.isExported);
			row.add(false);
			
			data.add(row);
		}

		setDataVector(data, toColumns());
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
