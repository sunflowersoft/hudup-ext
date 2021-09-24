/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic;

import java.io.Serializable;

import net.hudup.core.data.DataConfig;
import net.hudup.core.data.HiddenText2;

/**
 * This class represents an account with name and password.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class Account implements Serializable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Account name.
	 */
	private String name = null;
	
	
	/**
	 * Account password.
	 */
	private HiddenText2 password = null;

	
	/**
	 * Privileges of account.
	 */
	private int privileges = -1;
	
	
	/**
	 * Constructor with name and password.
	 * @param name name.
	 * @param password password.
	 * @param privileges privileges.
	 */
	private Account(String name, String password, int privileges) {
		this.name = name;
		this.password = new HiddenText2(password);
		this.privileges = privileges;
	}
	
	
	/**
	 * Getting name.
	 * @return name.
	 */
	public String getName() {
		return name;
	}
	
	
	/**
	 * Getting password.
	 * @return password.
	 */
	public String getPassword() {
		if (password != null)
			return password.getText();
		else
			return null;
	}
	
	
	/**
	 * Getting privileges.
	 * @return privileges.
	 */
	public int getPrivileges() {
		return privileges;
	}
	
	
	/**
	 * Checking whether account is administrator.
	 * @return whether account is administrator.
	 */
	public boolean isAdmin() {
		return (privileges & DataConfig.ACCOUNT_ADMIN_PRIVILEGE) == DataConfig.ACCOUNT_ADMIN_PRIVILEGE;
	}
	
	
//	/**
//	 * Testing whether this account is valid.
//	 * @return whether this account is valid.
//	 */
//	public boolean isValid() {
//		return name != null && password != null && password.getText() != null;
//	}
	

	/**
	 * Creating account with specified name and password.
	 * @param name specified name.
	 * @param password specified password.
	 * @return account with specified name and password. Return null if name or password is null.
	 */
	public static Account create(String name, String password) {
		return create(name, password, 0);
	}
	
	
	/**
	 * Creating account with specified name, password, and privileges.
	 * @param name specified name.
	 * @param password specified password.
	 * @param privileges specified privileges.
	 * @return account with specified name, password, and privileges. Return null if name or password is null.
	 */
	public static Account create(String name, String password, int privileges) {
		if (name == null || password == null)
			return null;
		else
			return new Account(name, password, privileges);
	}


}
