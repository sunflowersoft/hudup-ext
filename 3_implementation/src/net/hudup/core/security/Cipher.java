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
	 * Encrypting specified text data by symmetric cipher algorithm whose name is specified by constants {@link #CALG}.
	 * @param data specified text data
	 * @return encrypted text by symmetric cipher algorithm whose name is specified by constants {@link #CALG}.
	 */
	String encrypt(String data);

	
	/**
	 * Decrypting the specified encrypted data by symmetric cipher algorithm whose name is specified by constants {@link #CALG}.
	 * 
	 * @param encrypted specified encrypted data
	 * @return plain text decrypted from specified encrypted data by symmetric cipher algorithm whose name is specified by constants {@link #CALG}.
	 */
	String decrypt(String encrypted);
	
	
	/**
	 * Encrypting the specified plain text into the MD5-encrypted text by MD5 algorithm.
	 * @param text specified plain text.
	 * @return MD5-encrypted text encrypted from specified plain text by MD5 algorithm.
	 */
	String md5Encrypt(String text);
	
}
