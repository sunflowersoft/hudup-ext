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
	 * Connection URI that refers to remote server/service.
	 */
	public xURI connectUri = null;

	
	/**
	 * Bound URI that is bound to this application when it connects to remote server/service.
	 * If bound URI is not null, this application is remotely connected application. Otherwise, this application is local and stand alone application.
	 */
	public xURI bindUri = null;

	
	/**
	 * Naming URI that names this application as host in network. Naming URI is often in form of bound URI + name.
	 */
	public xURI namingUri = null;
	
	
	/**
	 * Flag to indicate whether pull mode is set. In pull mode, client is active to retrieve (pull) events from server.
	 */
	public boolean pullMode = false;
	
	
	/**
	 * Global address like internet address, WAN address.
	 */
	public String globalAddress = null;
	
	
	/**
	 * Server access period constant in milisecond.
	 */
	public long accessPeriod = 1000 * Counter.PERIOD;
	
	
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


	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		
		if (account != null && account.getName() != null)
			buffer.append("Username = " + account.getName());
		
		if (connectUri != null)
			buffer.append(", Connection URI = " + connectUri.toString());
		
		if (bindUri != null)
			buffer.append(", Bound URI = " + bindUri.toString());
		
		if (namingUri != null)
			buffer.append(", Naming URI = " + namingUri.toString());
		
		boolean pullMode = checkPullMode();
		buffer.append(", Pull mode = " + pullMode);
		
		String globalAddress = extractGlobalAddress();
		if (globalAddress != null)
			buffer.append(", Global address = " + globalAddress);
		
		if (pullMode)
			buffer.append(", Access period (seconds) = " + (accessPeriod/1000));

		String text = buffer.toString();
		if (text.startsWith(",")) text = text.substring(1);
		return text.trim();
	}
	
	
}


