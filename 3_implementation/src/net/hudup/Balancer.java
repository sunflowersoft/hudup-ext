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
 * Although this class is called {@code balancer}, it starts the real balancer specified by {@link net.hudup.listener.Balancer}. 
 * Recall that listener receives incoming requests and dispatch such requests to server.
 * Listener which has load balancing function is called {@code balancer}.
 * Project homepage: <a href="http://www.locnguyen.net/st/products/hudup">http://www.locnguyen.net/st/products/hudup</a>
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class Balancer implements AccessPoint {

	
	/**
	 * The main method to start balancer.
	 * @param args The argument parameter of main method. It contains command line arguments.
	 */
	public static void main(String[] args) {
		new Balancer().run(args);
	}

	
	@Override
	public void run(String[] args) {
		Util.getPluginManager().fire();
		
		net.hudup.listener.Balancer balancer = net.hudup.listener.Balancer.create();
		balancer.start();
	}


	@Override
	public String getName() {
		return "Balancer";
	}


	@Override
	public String toString() {
		return getName();
	}


}
