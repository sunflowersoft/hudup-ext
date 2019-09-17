/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.listener;

import net.hudup.core.client.ClientUtil;
import net.hudup.core.client.PowerServer;
import net.hudup.core.client.Server;
import net.hudup.core.client.ServerStatusListener;

/**
 * This class is the model of remote power server together with remote information (host, port, access account, access password, etc.) that are bound to listener or balancer.
 * Such remote power server is often called bound server. This {@link BindServer} can be identified with bound server because it actually is a wrapper of bound server.
 * Listener or balancer is represented by the interface {@link ServerStatusListener}.
 * Note, listener or balancer is implementation of {@link ServerStatusListener} interface.
 * Listener or balancer dispatches incoming requests to the bound server and receives responses from the bound server.
 * The variable {@link #server} is such bound server and the variable {@link #rInfo} contains remote information relevant to the bound server.
 * Listener or balancer stores a list of bound servers; such list is represented by {@link BindServerList}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class BindServer {
	
	
	/**
	 * Remote information (host, port, access account, access password, etc.) relevant to the bound server {@link #server}.
	 */
	protected RemoteInfo rInfo = null;

	
	/**
	 * The bound server. It is often remote power server.
	 */
	protected PowerServer server = null;
	
	
	/**
	 * {@link ServerStatusListener} is an abstract model of listener or balancer.
	 * As usual, this variable refers to a concrete listener or balancer.
	 */
	protected ServerStatusListener bind = null;
	
	
	/**
	 * Default constructor. There is no binding.
	 * Because this is private constructor, it is cannot be used to create a {@link BindServer}.
	 * Alternatively, please use the static methods {@link #bind(String, int, String, String, ServerStatusListener)} or {@link #bind(RemoteInfo, ServerStatusListener)}.
	 */
	private BindServer() {
		
	}
	
	
	/**
	 * Constructor with specified remote server and remote information. As default, the remote server is considered as bound server.
	 * The {@link ServerStatusListener} interface, which often refers to listener or balancer, is also specified by the input parameter {@code bind}.
	 * Because this is private constructor, it is cannot be used to create a {@link BindServer}.
	 * Alternatively, please use the static methods {@link #bind(String, int, String, String, ServerStatusListener)} or {@link #bind(RemoteInfo, ServerStatusListener)}.
	 * @param rInfo specified remote information often contains host, port, access account, and access password 
	 * @param server specified remote server.
	 * @param bind {@link ServerStatusListener} interface often refers to listener or balancer.
	 */
	private BindServer(RemoteInfo rInfo, PowerServer server, ServerStatusListener bind) {
		this.rInfo = rInfo;
		this.server = server;
		this.bind = bind;
	}

	
	/**
	 * Unbind the remote bound server. After this method is called, this {@link BindServer} is void.
	 */
	public void unbind() {
		if (rInfo == null || server == null || bind == null)
			return;
		
		try {
			server.removeStatusListener(bind);
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		
		rInfo = null;
		server = null;
		bind = null;
	}
	
	
	/**
	 * Getting the remote server.
	 * @return remote server referred by the internal variable {@link #server}.
	 */
	public PowerServer getServer() {
		return server;
	}
	
	
	/**
	 * Getting the remote information.
	 * @return remote information referred by the internal variable {@link #rInfo}.
	 */
	public RemoteInfo getRemoteInfo() {
		return rInfo;
	}
	
	
	/**
	 * Testing whether the remote server is bound.
	 * In current implementation, remote server is bound if the internal variable {@link #server} is not null.
	 * @return whether remote server is bound.
	 */
	public boolean isBound() {
		return server != null;
	}
	
	
	/**
	 * This static method creates a {@link BindServer} object that binds a remote server via
	 * specified host, port, access account, and access password. The {@link ServerStatusListener} interface, which often refers to listener or balancer, is also specified by the input parameter {@code bind}.
	 * @param host specified host.
	 * @param port specified port.
	 * @param account specified access account.
	 * @param password specified access password.
	 * @param bind {@link ServerStatusListener} interface often refers to listener or balancer.
	 * @return {@link BindServer} that binds the remote server via specified host, port, account, and password.
	 */
	public static BindServer bind(
			String host, 
			int port, 
			String account, 
			String password, 
			ServerStatusListener bind) {
		
		try {
			Server server = (Server) ClientUtil.getRemoteServer(
					host, 
					port, 
					account, 
					password);
			
			if (  (server == null) || !(server instanceof PowerServer) )
				return null;
			
			server.addStatusListener(bind);
			return new BindServer(
					new RemoteInfo(host, port, account, password), 
					(PowerServer)server, bind);
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	/**
	 * This static method creates a {@link BindServer} object that binds a remote server via remote information.
	 * The {@link ServerStatusListener} interface, which often refers to listener or balancer, is also specified by the input parameter {@code bind}.
	 * @param rInfo remote information often contains host, port, access account, and access password 
	 * @param bind {@link ServerStatusListener} interface often refers to listener or balancer.
	 * @return {@link BindServer} that binds a remote server via remote information.
	 */
	public static BindServer bind(RemoteInfo rInfo, ServerStatusListener bind) {
		return bind(rInfo.host, rInfo.port, rInfo.account, rInfo.password.getText(), bind);
	}
	
	
}
