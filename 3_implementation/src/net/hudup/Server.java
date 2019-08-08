/**
 * 
 */
package net.hudup;

import java.rmi.RemoteException;

import net.hudup.core.AccessPoint;
import net.hudup.core.Firer;
import net.hudup.core.client.PowerServer;
import net.hudup.data.ui.toolkit.DatasetToolkit;
import net.hudup.server.DefaultServer;
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
public final class Server implements AccessPoint {

	
	/**
	 * The main method to start server.
	 * @param args The argument parameter of main method. It contains command line arguments.
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		new Server().run(args);
	}

	
	@Override
	public void run(String[] args) {
		// TODO Auto-generated method stub
		
		new Firer();
		
		PowerServer server = null;
		if (args.length == 0) {
			server = DefaultServer.create();
		}
		else {
			String serverKind = args[0].trim().toLowerCase();
			
			if (serverKind.equals("-external"))
				server = ExternalServer.create();
			else
				server = DefaultServer.create();
		}
		
		if (server == null)
			return;
		
		try {
			server.start();
		} 
		catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Server";
	}


	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return getName();
	}

	
	
}
