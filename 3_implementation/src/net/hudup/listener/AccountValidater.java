/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.listener;

/**
 * This class represents an tool to validate user account, password, and privileges.
 * 
 * @author Loc Nguyen
 * @version 1.0
 * 
 */
interface AccountValidater {
	
	
	/**
	 * Checking whether an account is valid with regard to specified password and specified privileges.
	 * @param account specified account.
	 * @param password specified password.
	 * @param privileges specified privileges.
	 * @return whether account is valid.
	 */
	boolean validateAccount(String account, String password, int privileges);
	
	
	/**
	 * Validating administration account. Note, administration can be stored in properties file.
	 * @param account specified account.
	 * @param password specified password.
	 * @return whether account is valid.
	 */
	boolean validateAdminAccount(String account, String password);

	
}
