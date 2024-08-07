/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup;

import java.io.InputStream;
import java.net.URL;
import java.rmi.RemoteException;

import net.hudup.core.AccessPoint;
import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.client.PowerServer;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Unit;
import net.hudup.core.data.UnitList;
import net.hudup.core.data.ui.toolkit.DatasetToolkit;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;
import net.hudup.server.PowerServerConfig;
import net.hudup.server.ext.ExtendedServer;
import net.hudup.server.external.ExternalServer;

/**
 * This class implements access point to Hudup server.
 * There are 5 applications of Hudup framework such as {@code Evaluator}, {@code Server}, {@code Listener}, {@code Balancer}, {@code Toolkit}.
 * <ul>
 * <li>{@code Evaluator} makes evaluation on recommendation algorithms.</li>
 * <li>{@code Server} runs as a recommendation server that serves incoming user request for producing a list of recommended items or allowing users to access database.</li>
 * <li>{@code Listener} is responsible for receiving requests from users and dispatching such request to {@code Server}.
 * <li>{@code Balancer} is a special {@code Listener} that supports balancing.</li>
 * <li>{@code Toolkit} is a utility application that assists users to set up and modify Hudup database.</li>
 * </ul>
 * These applications must implement the interface {@link AccessPoint} which is used to start modules of Hudup framework.
 * Although this class is called {@code toolkit}, it starts the real toolkit specified by {@link DatasetToolkit}.
 *  
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class Server implements AccessPoint {

	
	/**
	 * The main method to start server.
	 * @param args The argument parameter of main method. It contains command line arguments.
	 */
	public static void main(String[] args) {
		new Server().run(args);
	}

	
	@Override
	public void run(String[] args) {
		Util.getPluginManager().fire();
		
		//Not important.
		UriAdapter adapter = null;
		try {
			URL sampleDataUrl = getClass().getResource(PowerServerConfig.TEMPLATES_SAMPLE_DATA_PATH);
			xURI sampleDataUri = xURI.create(sampleDataUrl.toURI());
			xURI fileStore = xURI.create(Constants.FILE_DIRECTORY); 
			adapter = new UriAdapter(fileStore);
			if (!adapter.exists(fileStore)) adapter.create(fileStore, true);
			
			if (Constants.COMPRESSED_FILE_SUPPORT) {
				xURI storeUri = xURI.create(PowerServerConfig.STORE_PATH_DEFAULT);
				if (!adapter.exists(storeUri))
					adapter.copy(sampleDataUri, storeUri, false, null);
			}
			else {
				UnitList basicUnitList = DataConfig.getBasicUnitList();
				boolean exist = true;
				for (int i = 0; i < basicUnitList.size(); i++) {
					Unit unit = basicUnitList.get(i);
					if (!adapter.exists(fileStore.concat(unit.getName()))) {
						exist = false;
						break;
					}
				}
				if (!exist) {
					try (InputStream zipStream = getClass().getResourceAsStream(PowerServerConfig.TEMPLATES_SAMPLE_DATA_PATH)) {
						adapter.unzip(zipStream, fileStore);
					}
					catch (Exception e) {LogUtil.trace(e);}
				}
			}
		}
		catch (Throwable e) {
			LogUtil.error("Server: coppying sample data error by " + e.getMessage());
		}
		finally {
			try {
				if (adapter != null) adapter.close();
			} catch (Throwable e) {}
		}

		
		PowerServer server = null;
		if (args.length == 0)
			server = create(false);
		else {
			String serverKind = args[0].trim().toLowerCase();
			if (serverKind.equals("-external"))
				server = create(true);
			else
				server = create(false);
		}
		
		if (server == null) return;
		
		try {
			server.start();
		} 
		catch (RemoteException e) {
			LogUtil.trace(e);
		}
		
	}


	@Override
	public String getName() {
		return "Server";
	}


	@Override
	public String toString() {
		return getName();
	}

	
	/**
	 * Create server.
	 * @param external external flag.
	 * @return created server.
	 */
	protected PowerServer create(boolean external) {
		if (external)
			return ExternalServer.create();
		else
			return ExtendedServer.create();
	}
	
	
}
