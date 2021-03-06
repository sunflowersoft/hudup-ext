/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup;

import net.hudup.core.AccessPoint;
import net.hudup.core.Util;

/**
 * There are 5 applications of Hudup framework such as {@code Evaluator}, {@code Server}, {@code Listener}, {@code Balancer}, {@code Toolkit}.
 * <ul>
 * <li>{@code Evaluator} makes evaluation on recommendation algorithms.</li>
 * <li>{@code Server} runs as a recommendation server that serves incoming user request for producing a list of recommended items or allowing users to access database.</li>
 * <li>{@code Listener} is responsible for receiving requests from users and dispatching such request to {@code Server}.
 * <li>{@code Balancer} is a special {@code Listener} that supports balancing.</li>
 * <li>{@code Toolkit} is a utility application that assists users to set up and modify Hudup database.</li>
 * </ul>
 * These applications must implement the interface {@link AccessPoint} which is used to start modules of Hudup framework.
 * Although this class is called {@code listener}, it starts the real listener specified by {@link net.hudup.listener.Listener}. 
 * Recall that listener receives incoming requests and dispatch such requests to server.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public final class Listener implements AccessPoint {

	
	/**
	 * @param args The argument parameter of main method. It contains command line arguments.
	 */
	public static void main(String[] args) {
		new Listener().run(args);
	}

	
	@Override
	public void run(String[] args) {
		Util.getPluginManager().fire();
		
		net.hudup.listener.Listener listener = net.hudup.listener.Listener.create();
		listener.start();
	}


	@Override
	public String getName() {
		return "Listener";
	}


	@Override
	public String toString() {
		return getName();
	}

	
}
