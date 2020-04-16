/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.listener;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.client.ServerConfig;
import net.hudup.core.data.HiddenText;
import net.hudup.core.logistic.xURI;

/**
 * This class represents data configuration for listener.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class ListenerConfig extends ServerConfig {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Configuration path.
	 */
	public final static String listenerConfig = Constants.WORKING_DIRECTORY + "/listenerconfig.xml";

	
	/**
	 * Remote host field.
	 */
	public static final String REMOTE_HOST_FIELD = changeCase("remote_host");
	
	
	/**
	 * Remote port field.
	 */
	public static final String REMOTE_PORT_FIELD = changeCase("remote_port");
	
	
	/**
	 * Remote account field.
	 */
	public static final String REMOTE_ACCOUNT_FIELD = changeCase("remote_account");

	
	/**
	 * Remote password.
	 */
	public static final String REMOTE_PASSWORD_FIELD = changeCase("remote_password");
	
	
	/**
	 * Export port field.
	 */
	public final static String EXPORT_PORT_FIELD = changeCase("export_port");

	
	/**
	 * Default constructor.
	 */
	public ListenerConfig() {
		super();
		// TODO Auto-generated constructor stub
	}


	/**
	 * Constructor with specified URI.
	 * @param uri specified URI.
	 */
	public ListenerConfig(xURI uri) {
		super(uri);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		super.reset();
		
		setServerPort(Constants.DEFAULT_LISTENER_PORT);
		setServerTimeout(Constants.DEFAULT_SERVER_TIMEOUT);
		setServerTasksPeriod(Constants.DEFAULT_LISTENER_TASK_PERIOD);
		
		setRemoteHost("localhost");
		setRemotePort(Constants.DEFAULT_SERVER_PORT);
		try {
			String pwd = Util.getHudupProperty("admin");
			if (pwd != null) {
				setRemoteAccount("admin");
				setRemotePassword(new HiddenText(pwd));
			}
		}
		catch (Throwable e) {
			remove(REMOTE_ACCOUNT_FIELD);
			remove(REMOTE_PASSWORD_FIELD);
		}
		setExportPort(Constants.DEFAULT_LISTENER_EXPORT_PORT);
	}
	

	/**
	 * Setting exported port.
	 * @param port exported port used for remote control panel.
	 */
	public void setExportPort(int port) {
		put(EXPORT_PORT_FIELD, port);
	}
	
	
	/**
	 * Getting exported port.
	 * @return export port used for remote control panel.
	 */
	public int getExportPort() {
		return getAsInt(EXPORT_PORT_FIELD);
	}

	
	/**
	 * Setting remote host (server).
	 * @param host remote host.
	 */
	public void setRemoteHost(String host) {
		put(REMOTE_HOST_FIELD, host);
	}
	
	
	/**
	 * Getting remote host (server).
	 * @return remote host.
	 */
	public String getRemoteHost() {
		return getAsString(REMOTE_HOST_FIELD);
	}

	
	/**
	 * Setting remote port (server port).
	 * @param port remote port.
	 */
	public void setRemotePort(int port) {
		put(REMOTE_PORT_FIELD, port);
	}
	
	
	/**
	 * Getting remote port (server port).
	 * @return remote port.
	 */
	public int getRemotePort() {
		return getAsInt(REMOTE_PORT_FIELD);
	}


	/**
	 * Setting remote account (account on server).
	 * @param account remote account (account on server).
	 */
	public void setRemoteAccount(String account) {
		put(REMOTE_ACCOUNT_FIELD, account);
	}
	
	
	/**
	 * Getting remote account (account on server).
	 * @return remote account.
	 */
	public String getRemoteAccount() {
		return getAsString(REMOTE_ACCOUNT_FIELD);
	}


	/**
	 * Getting remote password (password of account on server).
	 * @param password remote password (password of account on server).
	 */
	public void setRemotePassword(HiddenText password) {
		put(REMOTE_PASSWORD_FIELD, password);
	}
	
	
	/**
	 * Setting remote password (password of account on server).
	 * @return remote password (password of account on server).
	 */
	public HiddenText getRemotePassword() {
		return getAsHidden(REMOTE_PASSWORD_FIELD);
	}

	
	/**
	 * Getting remote information (server information).
	 * @return remote information (server information).
	 */
	public RemoteInfo getRemoteInfo() {
		return new RemoteInfo(
				getRemoteHost(), 
				getRemotePort(), getRemoteAccount(), getRemotePassword());
	}

	
	/**
	 * Setting remote information (server information).
	 * @param rInfo remote information (server information).
	 */
	public void setRemoteInfo(RemoteInfo rInfo) {
		setRemoteHost(rInfo.host);
		setRemotePort(rInfo.port);
		setRemoteAccount(rInfo.account);
		setRemotePassword(rInfo.password);
	}

	
	@Override
	protected String encrypt(HiddenText hidden) {
		// TODO Auto-generated method stub
		return Util.getCipher().encrypt(hidden.getText());
	}


	@Override
	protected HiddenText decrypt(String text) {
		// TODO Auto-generated method stub
		return new HiddenText(Util.getCipher().decrypt(text));
	}


	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		ListenerConfig cfg = new ListenerConfig();
		cfg.putAll(this);
		
		return cfg;
	}


}
