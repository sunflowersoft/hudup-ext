/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic;

import java.io.Serializable;

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
	 * Constructor with name and password.
	 * @param name name.
	 * @param password password.
	 */
	private Account(String name, String password) {
		this.name = name;
		this.password = new HiddenText2(password);
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
		if (name == null || password == null)
			return null;
		else
			return new Account(name, password);
	}
	
	
}
