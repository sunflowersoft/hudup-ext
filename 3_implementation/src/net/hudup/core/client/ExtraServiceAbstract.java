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

/**
 * This abstract class implements partially the extra service.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public abstract class ExtraServiceAbstract implements ExtraService, Serializable {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Internal server.
	 */
	protected PowerServer server = null;
	
	
	/**
	 * Internal account.
	 */
	protected Account account = null;
	
	
	/**
	 * Default constructor.
	 * @param server power server.
	 */
	public ExtraServiceAbstract(PowerServer server) {
		this.server = server;
	}

	
	/**
	 * Setting account.
	 * @param account specified account name.
	 * @param password specified password.
	 * @param privileges specified privileges.
	 */
	public void setAccount(String account, String password, int privileges) {
		this.account = Account.create(account, password, privileges);
	}
	

	/**
	 * Getting account.
	 * @return internal account.
	 */
	public Account getAccount() {
		return account;
	}
	

	/**
	 * Testing whether the internal account is administration.
	 * @return whether the internal account is administration.
	 */
	public boolean isAdminAccount() {
		return account != null ? account.isAdmin() : false;
	}
	
	
}
