/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.client;

import java.rmi.Naming;

import net.hudup.core.Constants;
import net.hudup.core.data.DataConfig;
import net.hudup.core.evaluate.Evaluator;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.xURI;

/**
 * This utility class provides methods relevant to connection and interaction in client-server architecture.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public final class ClientUtil {
	
	
	/**
	 * Creating socket connection with given URI of server, user name, and password.
	 * Socket connection is also a service that sends request to server and then receives response (result) from server, according to socket interaction.
	 * In the same session, socket connection can send many requests and receive many responses (from server)
	 * 
	 * @param uri URI of server
	 * @param username authenticated user name.
	 * @param password authenticated password.
	 * @return socket connection with given URI of server, user name, and password.
	 */
	public final static SocketConnection getSocketConnection(
			xURI uri, String username, String password) {
		if (uri == null) return null;
		
		String host = uri.getHost();
		int port = uri.getPort();
		
		return getSocketConnection(host, port, username, password);
	}
	
	
	/**
	 * Getting a socket with given remote host, remote port, authenticated user name, and authenticated password.
	 * @param host remote host.
	 * @param port remote port.
	 * @param username authenticated user name
	 * @param password authenticated password.
	 * @return socket connection according to RMI protocol.
	 */
	public final static SocketConnection getSocketConnection(
			String host, int port, String username, String password) {
		
		if (host == null)
			return null;
		
		if (port < 1)
			port = Constants.DEFAULT_SERVER_PORT;
		
		SocketConnection connection = new SocketConnection(host, port);
		if (connection.connect(username, password, DataConfig.ACCOUNT_ACCESS_PRIVILEGE))
			return connection;
		else
			return null;
	}

	
	/**
	 * Getting a remote service with given remote host, remote port, authenticated user name, and authenticated password.
	 * Service specifies methods to serve user requests according to RMI ((abbreviation of Java Remote Method Invocation)) protocol. These methods focus on providing recommendation, inserting, updating and getting information such as user ratings, user profiles, and item profiles stored in database.
	 * 
	 * @param host remote host.
	 * @param port remote port.
	 * @param username authenticated user name
	 * @param password authenticated password.
	 * @return remote {@link Service} according to RMI protocol.
	 */
	public final static Service getRemoteService(String host, int port, String username, String password) {
		String uri = "rmi://" + host;
		if (port < 1)
			uri += "/" + Protocol.GATEWAY;
		else
			uri += ":" + port + "/" + Protocol.GATEWAY;
			
		try {
			Gateway gateway = (Gateway)Naming.lookup(uri);
			if (gateway == null)
				return null;
			else
				return gateway.getRemoteService(username, password);
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
		
		return null;
	}
	
	
	/**
	 * Getting a reference to server via the gateway of such server with remote host, remote port, authenticated user name, and authenticated password.
	 * By the reference to server, users can start, stop, pause, and configure server remotely.
	 * 
	 * @param host remote host.
	 * @param port remote port.
	 * @param username authenticated user name.
	 * @param password authenticated password.
	 * @return reference to {@link Server} via the gateway of such server.
	 */
	public final static Server getRemoteServer(String host, int port, String username, String password) {
		String uri = "rmi://" + host;
		if (port < 1)
			uri += "/" + Protocol.GATEWAY;
		else
			uri += ":" + port + "/" + Protocol.GATEWAY;
			
		
		try {
			Gateway gateway = (Gateway)Naming.lookup(uri);
			return gateway.getRemoteServer(username, password);
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
		
		return null;
	}
	
	
	/**
	 * Getting remote evaluator with specified host, port, and bound name.
	 * @param host specified host.
	 * @param port specified port.
	 * @param bindName specified bound name.
	 * @return remote evaluator with specified host, port, and bound name.
	 */
	public final static Evaluator getRemoteEvaluator(String host, int port, String bindName) {
		if (bindName == null) bindName = "";
		bindName = bindName.trim();
		bindName = bindName.replace('\\', '/');
		if (bindName.startsWith("/")) bindName = bindName.substring(1);
		
		String uri = "rmi://" + host + ":" + port + "/" + bindName;
			
		try {
			return (Evaluator)Naming.lookup(uri);
		}
		catch (Exception e) {LogUtil.trace(e);}
		
		return null;
	}
	
	
}
