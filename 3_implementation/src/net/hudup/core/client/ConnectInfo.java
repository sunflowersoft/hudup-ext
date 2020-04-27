/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.client;

import java.io.Serializable;

import net.hudup.core.logistic.Account;
import net.hudup.core.logistic.Counter;
import net.hudup.core.logistic.xURI;

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
	 * Flag to indicate whether pull mode is set. In pull mode, client is active to retrieve (pull) events from server.
	 */
	public boolean pullMode = false;
	
	
	/**
	 * Global address like internet address, WAN address.
	 */
	public String globalAddress= null;
	
	
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
	 * Getting global address.
	 * @return global address.
	 */
	public String extractGlobalAddress() {
		if (globalAddress == null) return null;
		
		String addr = globalAddress.trim();
		if (addr.isEmpty() || addr.compareToIgnoreCase("localhost") == 0 || addr.compareToIgnoreCase("127.0.0.1") == 0)
			return null;
		else
			return addr;
	}
	
	
	/**
	 * Checking whether pull mode is set.
	 * @return whether pull mode is set.
	 */
	public boolean checkPullMode() {
		return pullMode && bindUri != null;
	}
	
	
}


