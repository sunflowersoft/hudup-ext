package net.hudup;

import net.hudup.core.AccessPoint;
import net.hudup.core.Firer;

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
		// TODO Auto-generated method stub
		
		new Balancer().run(args);
	}

	
	@Override
	public void run(String[] args) {
		// TODO Auto-generated method stub
		new Firer();
		
		net.hudup.listener.Balancer balancer = net.hudup.listener.Balancer.create();
		balancer.start();
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Balancer";
	}


	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return getName();
	}


}
