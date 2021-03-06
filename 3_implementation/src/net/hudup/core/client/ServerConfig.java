/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.client;

import net.hudup.core.Constants;
import net.hudup.core.data.SysConfig;
import net.hudup.core.logistic.xURI;

/**
 * This class represents server configuration.
 * It stores additional information of server such as host, port, task period, timeout interval.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class ServerConfig extends SysConfig {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Every new information (new entry) that is put into server configuration has a key.
	 * This constant specifies key name of server port.
	 */
	public final static String SERVER_PORT_FIELD = changeCase("server_port");
	
	
	/**
	 * Every new information (new entry) that is put into server configuration has a key.
	 * This constant specifies key name of server task period.
	 * Note, the period in miliseconds that the Hudup server does periodically internal tasks such as data mining and learning knowledge base.
	 */
	public final static String SERVER_TASKS_PERIOD_FIELD = changeCase("server_tasks_period");

	
	/**
	 * Every new information (new entry) that is put into server configuration has a key.
	 * This constant specifies key name of server timeout interval.
	 */
	public final static String SERVER_TIMEOUT_FIELD = changeCase("server_timeout");

	
	/**
	 * Control port field.
	 */
	public final static String SOCKET_CONTROL_PORT_FIELD = changeCase("socket_control_port");

	
	/**
	 * This constant specifies pull mode requirement.
	 */
	public final static String PULL_MODE_REQUIRED_FIELD = changeCase("pull_mode_required");

	
	/**
	 * Default constructor.
	 */
	public ServerConfig() {
		super();
	}


	/**
	 * Constructor with URI of server.
	 * @param uri URI of server.
	 */
	public ServerConfig(xURI uri) {
		super(uri);
	}

	
	@Override
	public void reset() {
		super.reset();
		
		setServerPort(Constants.DEFAULT_SERVER_PORT);
		setServerTimeout(Constants.DEFAULT_SERVER_TIMEOUT);
		setServerTasksPeriod(Constants.DEFAULT_SERVER_TASKS_PERIOD);
		
		setSocketControlPort(Constants.DEFAULT_SOCKET_CONTROL_PORT);
		
		setPullModeRequired(Constants.PULL_MODE_ADVICE);

		addReadOnly(SOCKET_CONTROL_PORT_FIELD);
	}

	
	/**
	 * Setting server port by specified port.
	 * @param port specified port.
	 */
	public void setServerPort(int port) {
		put(SERVER_PORT_FIELD, port);
	}
	
	
	/**
	 * Getting server port.
	 * @return server port
	 */
	public int getServerPort() {
		return getAsInt(SERVER_PORT_FIELD);
	}
	
	
	/**
	 * Setting socket control port by specified port.
	 * @param port specified port.
	 */
	public void setSocketControlPort(int port) {
		put(SOCKET_CONTROL_PORT_FIELD, port);
	}
	
	
	/**
	 * Getting socket control port.
	 * @return socket control port
	 */
	public int getSocketControlPort() {
		return getAsInt(SOCKET_CONTROL_PORT_FIELD);
	}

	
	/**
	 * Setting server task period in seconds.
	 * Note, the period in seconds that the Hudup server does periodically internal tasks such as data mining and learning knowledge base.
	 * @param sec task period in seconds.
	 */
	public void setServerTasksPeriod(int sec) {
		put(SERVER_TASKS_PERIOD_FIELD, sec);
	}
	
	
	/**
	 * Getting server task period in seconds.
	 * Note, the period in seconds that the Hudup server does periodically internal tasks such as data mining and learning knowledge base.
	 * @return task period in seconds.
	 */
	public int getServerTasksPeriod() {
		return getAsInt(SERVER_TASKS_PERIOD_FIELD);
	}
	
	
	/**
	 * Setting server timeout in seconds.
	 * Note, server is available to serve incoming request in a interval called a timeout in miliseconds.
	 * @param timeout timeout in seconds.
	 */
	public void setServerTimeout(int timeout) {
		put(SERVER_TIMEOUT_FIELD, timeout);
	}
	
	
	/**
	 * Getting server timeout in seconds.
	 * Note, server is available to serve incoming request in a interval called a timeout in miliseconds.
	 * @return server timeout in seconds.
	 */
	public int getServerTimeout() {
		return getAsInt(SERVER_TIMEOUT_FIELD);
	}


	@Override
	public Object clone() {
		ServerConfig cfg = new ServerConfig();
		cfg.putAll(this);
		
		return cfg;
	}
	
	
	/**
	 * Checking whether pull mode is required. 
	 * @return whether pull mode is required.
	 */
	public boolean isPullModeRequired() {
		return getAsBoolean(PULL_MODE_REQUIRED_FIELD);
	}
	
	
	/**
	 * Setting whether pull mode is required.
	 * @param pullModeRequired whether pull mode is required.
	 */
	public void setPullModeRequired(boolean pullModeRequired) {
		put(PULL_MODE_REQUIRED_FIELD, pullModeRequired);
	}
	
	
}
