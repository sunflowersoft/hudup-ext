/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic.ui;

import net.hudup.core.PluginChangedListener;
import net.hudup.core.client.ConnectInfo;
import net.hudup.core.client.PowerServer;
import net.hudup.core.logistic.LogUtil;

/**
 * Panel for plug-in storage manifest from remote connection.
 * 
 * @author Loc Nguyen
 * @version 13.0
 */
public class PluginStorageManifestPanelRemote extends PluginStorageManifestPanel {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Power server
	 */
	protected PowerServer server = null;
	
	
	/**
	 * Connection information.
	 */
	protected ConnectInfo connectInfo = null;

	
	/**
	 * Constructor with server and plug-in changed listener.
	 * @param listener specified listener.
	 * @param connectInfo connection information.
	 */
	public PluginStorageManifestPanelRemote(PluginChangedListener listener, ConnectInfo connectInfo) {
		super(listener, connectInfo = (connectInfo != null ? connectInfo : new ConnectInfo()));
		
		this.server = (listener != null && listener instanceof PowerServer) ? (PowerServer)listener : null;
		this.connectInfo = connectInfo;
		
		if (connectInfo.bindUri != null) {
			setEnabled(false);
			reloadAlg.setEnabled(this.server != null);
		}
	}


	@Override
	protected PluginStorageManifest createPluginStorageManifest(PluginChangedListener listener,
			ConnectInfo connectInfo) {
		if (listener == null)
			return super.createPluginStorageManifest(listener, connectInfo);
		else if (listener instanceof PowerServer) {
			if (connectInfo.bindUri == null)
				return super.createPluginStorageManifest(listener, connectInfo);
			else {
				int port = 0;
				try {
					if (connectInfo != null && connectInfo.bindUri != null)
						port = connectInfo.bindUri.getPort();
					else
						port = listener.getPort();
				} catch (Exception e) {port = 0; LogUtil.trace(e);}
				
				return new PluginStorageManifestRemote((PowerServer)listener, connectInfo, port);
			}
		}
		else
			return super.createPluginStorageManifest(listener, connectInfo);
	}


	@Override
	protected void reload() {
		if (connectInfo.bindUri == null)
			super.reload();
		else if (server != null) {
			try {
				boolean reloaded = server.reloadPlugin(connectInfo.account.getName(), connectInfo.account.getPassword());
				if (reloaded) {
					tblRegister.update();
				}
			}
			catch (Exception e) {
				LogUtil.trace(e);
			}
		}
	}


}
