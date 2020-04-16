/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.listener;

import java.awt.Component;
import java.io.Serializable;

import javax.swing.JOptionPane;

import net.hudup.core.Constants;
import net.hudup.core.logistic.xURI;
import net.hudup.listener.ui.RemoteInfoDlg;

/**
 * This class represents the configuration of a balancer. It inherits from {@link ListenerConfig} because balancer inherits from listener.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class BalancerConfig extends ListenerConfig {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default file path default balancer configuration.
	 */
	public final static String balancerConfig = Constants.WORKING_DIRECTORY + "/balancerconfig.xml";

	
	/**
	 * Name of the entry that contains balancer default port. It is &quot;balancer_port&quot; by default.
	 */
	public final static String BALANCER_PORT_FIELD = changeCase("balancer_port");

	
	/**
	 * Name of the entry that contains balancer timeout. It is &quot;balancer_timeout&quot; by default.
	 * The balancer is available to serve incoming request in a interval called a timeout in miliseconds.
	 * After timeout interval is reached, the balancer suspends and users must resumes it.
	 * As usual, the value of such name is initialized by the constant {@link Constants#DEFAULT_SERVER_TIMEOUT}.
	 */
	public final static String BALANCER_TIMEOUT_FIELD = changeCase("balancer_timeout");

	
	/**
	 * Name of the ntry that contains task period of balancer. It is &quot;balancer_task_period&quot; by default.
	 * The period in miliseconds that the balancer does periodically internal tasks.
	 * As usual, the value of such name is initialized by the constant {@link Constants#DEFAULT_LISTENER_TASK_PERIOD}.
	 */
	public final static String BALANCER_TASK_PERIOD_FIELD = changeCase("balancer_task_period");

	
	/**
	 * Name of the entry that contains list of remote information. It is &quot;remote_info_list&quot; by default.
	 */
	public final static String REMOTE_INFO_LIST = changeCase("remote_info_list");

	
	/**
	 * Default constructor.
	 */
	public BalancerConfig() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * Constructor with URI.
	 * @param uri URI of this balancer configuration.
	 */
	public BalancerConfig(xURI uri) {
		super(uri);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		super.reset();

		remove(REMOTE_HOST_FIELD);
		remove(REMOTE_PORT_FIELD);
		remove(REMOTE_ACCOUNT_FIELD);
		remove(REMOTE_PASSWORD_FIELD);
		
		setServerPort(Constants.DEFAULT_BALANCER_PORT);
		setServerTimeout(Constants.DEFAULT_SERVER_TIMEOUT);
		setServerTasksPeriod(Constants.DEFAULT_LISTENER_TASK_PERIOD);
		
		clearRemoteInfoList();
		RemoteInfo rInfo = 
				new RemoteInfo("localhost", Constants.DEFAULT_SERVER_PORT, "admin", "admin");
		addRemoteInfo(rInfo);
		
		setExportPort(Constants.DEFAULT_BALANCER_EXPORT_PORT);

	}

	
	/**
	 * Getting the list of remote information inside this configuration.
	 * @return {@link RemoteInfoList} inside this configuration.
	 */
	public RemoteInfoList getRemoteInfoList() {
		String text = getAsString(REMOTE_INFO_LIST);
		RemoteInfoList rInfoList = new RemoteInfoList();
		
		if (text != null)
			rInfoList.parseText(text);
		return rInfoList;
	}
	
	
	/**
	 * Setting the list of remote information by specified {@link RemoteInfoList}.
	 * @param rInfoList specified {@link RemoteInfoList}.
	 */
	public void setRemoteInfoList(RemoteInfoList rInfoList) {
		String text = rInfoList.toText();
		put(REMOTE_INFO_LIST, text);
	}
	
	
	/**
	 * Clearing the list of remote information inside this configuration.
	 */
	public void clearRemoteInfoList() {
		put(REMOTE_INFO_LIST, "");
	}
	
	
	/**
	 * Let L be the list of remote information inside this configuration.
	 * This method adds the specified remote information into L.
	 * @param rInfo specified {@link RemoteInfo}.
	 */
	public void addRemoteInfo(RemoteInfo rInfo) {
		RemoteInfoList rInfoList = getRemoteInfoList();
		rInfoList.add(rInfo);
		
		setRemoteInfoList(rInfoList);
	}
	
	
	/**
	 * Let L be the list of remote information inside this configuration.
	 * This method removes the remote information at specified index.
	 * @param index specified index.
	 */
	public void removeRemoteInfo(int index) {
		RemoteInfoList rInfoList = getRemoteInfoList();
		rInfoList.remove(index);
		
		setRemoteInfoList(rInfoList);
	}
	
	
	/**
	 * Let L be the list of remote information inside this configuration.
	 * This method removes the remote information having specified host and port.
	 * @param host specified host.
	 * @param port specified port.
	 */
	public void removeRemoteInfo(String host, int port) {
		RemoteInfoList rInfoList = getRemoteInfoList();
		rInfoList.remove(host, port);
		
		setRemoteInfoList(rInfoList);
	}
	
	
	/**
	 * Let L be the list of remote information inside this configuration.
	 * This method returns the size of L.
	 * @return count of remote servers (services).
	 */
	public int getRemoteInfoCount() {
		RemoteInfoList rInfoList = getRemoteInfoList();
		return rInfoList.size();
	}
	
	
	/**
	 * Let L be the list of remote information inside this configuration.
	 * This method gets the remote information at specified index.
	 * @param index specified index.
	 * @return {@link RemoteInfo} at specified index.
	 */
	public RemoteInfo getRemoteInfo(int index) {
		RemoteInfoList rInfoList = getRemoteInfoList();
		return rInfoList.get(index);
	}
	
	
	/**
	 * Let L be the list of remote information inside this configuration.
	 * This method sets the specified remote information at specified index.
	 * @param index specified index.
	 * @param rInfo specified {@link RemoteInfo}.
	 */
	public void setRemoteInfo(int index, RemoteInfo rInfo) {
		RemoteInfoList rInfoList = getRemoteInfoList();
		rInfoList.set(index, rInfo);
		
		setRemoteInfoList(rInfoList);
	}


	@Override
	public Serializable userEdit(Component comp, String key, Serializable defaultValue) {
		// TODO Auto-generated method stub
		if (!key.equals(REMOTE_INFO_LIST))
			return super.userEdit(comp, key, defaultValue);
		
		RemoteInfoDlg rInfoDlg = new RemoteInfoDlg(comp, getRemoteInfoList());
		RemoteInfoList result = rInfoDlg.getResult();
		if (result == null) {
			JOptionPane.showMessageDialog(
				comp, 
				"Not set up remote hosts", 
				"Not set up remote hosts", 
				JOptionPane.INFORMATION_MESSAGE);
			
			return null;
		}
		else
			return result.toText();
	}


	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		BalancerConfig cfg = new BalancerConfig();
		cfg.putAll(this);
		
		return cfg;
	}
	
	
}
