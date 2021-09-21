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
public abstract class ExtraServiceAbstract implements ExtraService, Serializable, Cloneable {

	
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
	 * Flag to indicate the account is administration.
	 */
	protected boolean isAdminAccount = false;
	
	
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
	 * @param isAdminAccount Flag to indicate the account is administration.
	 * @return whether account and password are valid.
	 */
	public void setAccount(String account, String password, boolean isAdminAccount) {
		this.account = Account.create(account, password);
		if (this.account != null)
			this.isAdminAccount = isAdminAccount;
		else
			this.isAdminAccount = false;
	}
	

	/**
	 * Getting account.
	 * @return internal account.
	 */
	protected Account getAccount() {
		return account;
	}
	

	/**
	 * Testing whether the internal account is administration.
	 * @return whether the internal account is administration.
	 */
	public boolean isAdminAccount() {
		return account != null ? isAdminAccount : false;
	}
	
	
}
