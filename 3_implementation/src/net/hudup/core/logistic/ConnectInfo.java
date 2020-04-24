/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic;

import java.io.Serializable;

/**
 * This class represents connection information.
 * @author Loc Nguyen
 * @version 1.0
 */
public class ConnectInfo implements Serializable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Account information.
	 */
	public Account account = null;

	
	/**
	 * Connection URI.
	 */
	public xURI connectUri = null;

	
	/**
	 * Bound URI.
	 */
	public xURI bindUri = null;

	
	/**
	 * Naming URI.
	 */
	public xURI namingUri = null;
	
	
	/**
	 * Flag to indicate whether to deploy server / service globally.
	 */
	public boolean deployGlobal = false;
	
	
	/**
	 * Deploy global host.
	 */
	public String deployGlobalAddress= null;
	
	
	/**
	 * Extra internet address.
	 */
	public String internetAddress = null;
	
	
	/**
	 * Server access period constant in milisecond.
	 */
	public long accessPeriod = Counter.PERIOD;
	
	
	/**
	 * Default constructor.
	 */
	public ConnectInfo() {
		
	}
	
	/**
	 * Getting global host in case of global deployment.
	 * @return global host in case of global deployment. Return null if unable to retrieve global host.
	 */
	public String getDeployGlobalHost() {
		if (!deployGlobal) return null;
		
		String host = deployGlobalAddress == null ? deployGlobalAddress : deployGlobalAddress.trim();
//		if (host == null || host.isEmpty())
//			host = NetUtil.getPublicInetAddress();
		if (host == null || host.isEmpty() || host.compareToIgnoreCase("localhost") == 0 || host.compareToIgnoreCase("127.0.0.1") == 0)
			return null;
		else
			return host;
	}
	
	
//	/**
//	 * Checking whether to deploy globally.
//	 * @return whether to deploy globally.
//	 */
//	public boolean checkDeployGlobal() {
//		String globalHost = getDeployGlobalHost();
//		return globalHost != null && globalHost.compareToIgnoreCase("localhost") != 0 && globalHost.compareToIgnoreCase("127.0.0.1") != 0;
//	}
	
	
}


