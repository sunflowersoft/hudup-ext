/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.security;

/**
 * This interface declares methods of encryption and decryption in Hudup framework.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public interface Cipher {
	
	
	/**
	 * Encrypting specified text data by symmetric cipher algorithm.
	 * @param data specified text data
	 * @return encrypted text by symmetric cipher algorithm.
	 */
	String encrypt(String data);

	
	/**
	 * Decrypting the specified encrypted data by symmetric cipher algorithm.
	 * 
	 * @param encrypted specified encrypted data
	 * @return plain text decrypted from specified encrypted data by symmetric cipher algorithm.
	 */
	String decrypt(String encrypted);
	
	
	/**
	 * Encrypting the specified plain text into the MD5-encrypted text by MD5 algorithm.
	 * @param text specified plain text.
	 * @return MD5-encrypted text encrypted from specified plain text by MD5 algorithm.
	 */
	String md5Encrypt(String text);
	
}
