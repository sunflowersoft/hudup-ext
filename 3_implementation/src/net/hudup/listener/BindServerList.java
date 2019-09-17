/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.listener;

import java.util.List;

import net.hudup.core.Util;
import net.hudup.core.client.ActiveMeasure;
import net.hudup.core.client.PowerServer;
import net.hudup.core.client.ServerStatusListener;

/**
 * This class is the list of bound servers. Each bound server is represented by {@link BindServer} class.
 * Note, {@link BindServer} contains both the bound server and remote information (host, port, access account, access password, etc.) of such bound server.
 * {@link BindServer} can be identified with bound server because it actually is a wrapper of bound server.
 * This class provides utility methods to retrieve and to bind remote servers.
 * This class also supports synchronization mechanism among many threads.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class BindServerList {
	
	
	/**
	 * The internal list of bound servers.
	 */
	protected List<BindServer> bindServers = Util.newList();

	
	/**
	 * Default constructor.
	 */
	public BindServerList() {
		
	}
	
	
	/**
	 * Getting the bound server at specified index.
	 * @param index at specified index.
	 * @return {@link BindServer} at specified index.
	 */
	public synchronized BindServer get(int index) {
		
		try {
			return bindServers.get(index);
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	/**
	 * Getting the first bound server in this list.
	 * @return first {@link BindServer} in this list.
	 */
	public synchronized BindServer get() {
		if (size() == 0)
			return null;
		else
			return get(0);
	}

	
	
	/**
	 * Getting idle remote (bound) server in this list.
	 * Note, each bound power server own an internal counter represented by active measure {@link ActiveMeasure} interface.
	 * {@link ActiveMeasure} specifies how to measure the active degree of bound server.
	 * The higher active degree is, the more busy the bound server is.
	 * So this method returns the less busy bound server in this list.
	 * @return idle {@link BindServer} in this list.
	 */
	public synchronized BindServer getIdleServer() {
		BindServer idle = null;
		for (BindServer bindServer : bindServers) {
			try {
				if (idle == null)
					idle = bindServer;
				else {
					ActiveMeasure m = bindServer.getServer().getActiveMeasure();
					ActiveMeasure mIdle = idle.getServer().getActiveMeasure();
					if (m.compareTo(mIdle) < 0)
						idle = bindServer;
				}
			} 
			catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return idle;
		
	}

	
	/**
	 * Getting the size of this list, which is the number of bound servers.
	 * @return size of this list.
	 */
	public synchronized int size() {
		return bindServers.size();
	}
	
	
	/**
	 * Unbinding the bound server at specified index. Such server is then removed from this list.
	 * @param index specified index.
	 */
	public synchronized void unbind(int index) {
		try {
			BindServer bindServer = bindServers.get(index);
			bindServer.unbind();
			bindServers.remove(index);
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Unbinding and removing all bound servers from this list. After this method is called, this list is empty.
	 */
	public synchronized void unbindAll() {
		for (BindServer bindServer : bindServers) {
			try {
				bindServer.unbind();
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		bindServers.clear();
	}
	
	
	/**
	 * Finding the bound server by its host and port.
	 * @param host specified host.
	 * @param port  specified host.
	 * @return index of the found {@link BindServer}.
	 */
	public synchronized int indexOf(String host, int port) {
		for (int i = 0; i < bindServers.size(); i++) {
			BindServer bindServer = bindServers.get(i);
			RemoteInfo rInfo = bindServer.getRemoteInfo();
			if (rInfo.host.compareToIgnoreCase(host) == 0 && rInfo.port == port)
				return i;
		}
		
		return -1;
	}
	
	
	/**
	 * This method binds a remote server via specified host, port, access account, and access password.
	 * If the remote server is already bound, which means that such server already exists in this list, this method firstly unbinds such server and then re-binds such server. 
	 * The {@link ServerStatusListener} interface, which often refers to listener or balancer, is also specified by the input parameter {@code bind}.
	 * @param host specified host.
	 * @param port specified port.
	 * @param account specified access account.
	 * @param password specified access password.
	 * @param bind {@link ServerStatusListener} interface often refers to listener or balancer.
	 * @return whether bind successfully.
	 */
	public synchronized boolean bind(
			String host, 
			int port, 
			String account, 
			String password, 
			ServerStatusListener bind) {
		
		int index = indexOf(host, port);
		
		if (index != -1) {
			BindServer bindServer = get(index);
			PowerServer server = bindServer.getServer();
			
			boolean validate = false;
			try {
				validate = server.ping();
			}
			catch (Throwable e) {
				e.printStackTrace();
				validate = false;
			}
			
			if (!validate) {
				this.bindServers.remove(bindServer);
				bindServer = BindServer.bind(host, port, account, password, bind);
				
				if (bindServer == null)
					return false;
				else {
					bindServers.add(bindServer);
					return true;
				}
			}
			else
				return true;
		}
		else {
			
			BindServer bindServer = BindServer.bind(host, port, account, password, bind);
			if (bindServer == null)
				return false;
			else {
				bindServers.add(bindServer);
				return true;
			}
		}
		
	}
	
	
	/**
	 * Firstly, this method unbind all bound servers in this list by calling {@link #unbindAll()} method, which makes this list empty.
	 * Later on this method binds a remote server via specified host, port, access account, and access password.
	 * If this method is successful, this list has only one bound {@link BindServer}.
	 * The {@link ServerStatusListener} interface, which often refers to listener or balancer, is also specified by the input parameter {@code bind}.
	 * @param host specified host.
	 * @param port specified port.
	 * @param account specified access account.
	 * @param password specified access password.
	 * @param bind {@link ServerStatusListener} interface often refers to listener or balancer.
	 * @return whether re-bind successfully.
	 */
	public synchronized boolean rebind(
			String host, 
			int port, 
			String account, 
			String password, 
			ServerStatusListener bind) {
		
		unbindAll();
		return bind(host, port, account, password, bind);
	}
	
	
	/**
	 * This method binds a remote server via specified remote information which often contains host, port, access account, and access password.
	 * If the remote server is already bound, which means that such server already exists in this list, this method firstly unbinds such server and then re-binds such server.
	 * Actually, this method calls method {@link #rebind(String, int, String, String, ServerStatusListener)}. 
	 * The {@link ServerStatusListener} interface, which often refers to listener or balancer, is also specified by the input parameter {@code bind}.
	 * @param rInfo specified remote information.
	 * @param bind {@link ServerStatusListener} interface often refers to listener or balancer.
	 * @return whether bind successfully.
	 */
	public synchronized boolean bind(RemoteInfo rInfo, ServerStatusListener bind) {
		return bind(rInfo.host, rInfo.port, rInfo.account, rInfo.password.getText(), bind);
	}
	
	
	/**
	 * Firstly, this method unbind all bound servers in this list by calling {@link #unbindAll()} method, which makes this list empty.
	 * Later on this method binds a remote server via specified remote information which often contains host, port, access account, and access password.
	 * If this method is successful, this list has only one bound {@link BindServer}.
	 * The {@link ServerStatusListener} interface, which often refers to listener or balancer, is also specified by the input parameter {@code bind}.
	 * @param rInfo specified remote information.
	 * @param bind {@link ServerStatusListener} interface often refers to listener or balancer.
	 * @return whether re-bind successfully.
	 */
	public synchronized boolean rebind(RemoteInfo rInfo, ServerStatusListener bind) {
		unbindAll();
		return bind(rInfo, bind);
	}

		
	/**
	 * This method binds many remote servers via specified list of {@link RemoteInfo} (s). Each {@link RemoteInfo} often contains a set of host, port, access account, and access password.
	 * If a remote server is already bound, which means that such server already exists in this list, this method firstly unbinds such server and then re-binds such server.
	 * The {@link ServerStatusListener} interface, which often refers to listener or balancer, is also specified by the input parameter {@code bind}.
	 * @param rInfoList specified list of remote information.
	 * @param bind {@link ServerStatusListener} interface often refers to listener or balancer.
	 */
	public synchronized void bind(RemoteInfoList rInfoList, ServerStatusListener bind) {
		int n = rInfoList.size();
		for (int i = 0; i < n; i++) {
			RemoteInfo rInfo = rInfoList.get(i);
			bind(rInfo, bind);
		} // end for
	}


	/**
	 * Firstly, this method unbind all bound servers in this list by calling {@link #unbindAll()} method, which makes this list empty.
	 * Later on this method binds many remote servers via specified list of {@link RemoteInfo} (s) by calling {@link #bind(RemoteInfoList, ServerStatusListener)} method.
	 * The {@link ServerStatusListener} interface, which often refers to listener or balancer, is also specified by the input parameter {@code bind}.
	 * @param rInfoList specified list of remote information.
	 * @param bind {@link ServerStatusListener} interface often refers to listener or balancer.
	 */
	public synchronized void rebind(RemoteInfoList rInfoList, ServerStatusListener bind) {
		unbindAll();
		bind(rInfoList, bind);
	}
	
	
	/**
	 * Unbinding servers that are not running (stopped or paused).
	 * As a result, such servers are removed from this list.
	 * 
	 */
	public synchronized void prune() {
		List<BindServer> temp = Util.newList();
		temp.addAll(bindServers);
		
		for (BindServer bindServer : temp) {
			boolean alive = true;
			
			try {
				alive = bindServer.getServer().ping();
			}
			catch (Throwable e) {
				e.printStackTrace();
				alive = false;
			}
			
			if (!alive)
				this.bindServers.remove(bindServer);
		}
	}
	
	
	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		try {
			unbindAll();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		
		super.finalize();
	}
	
	
}
