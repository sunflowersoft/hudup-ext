/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup;

import net.hudup.core.AccessPoint;
import net.hudup.core.client.PowerServer;
import net.hudup.core.data.ui.toolkit.DatasetToolkit;
import net.hudup.server.ext2.ExtendedServer2;

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
public class Server2 extends Server {

	
	/**
	 * The main method to start server.
	 * @param args The argument parameter of main method. It contains command line arguments.
	 */
	public static void main(String[] args) {
		new Server2().run(args);
	}

	
	@Override
	public void run(String[] args) {
		super.run(args);
	}


	@Override
	public String getName() {
		return "Server2";
	}


	@Override
	public String toString() {
		return getName();
	}

	
	@Override
	protected PowerServer create(boolean external) {
		return ExtendedServer2.create();
	}

	
}
