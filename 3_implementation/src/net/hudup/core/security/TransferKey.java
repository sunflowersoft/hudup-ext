package net.hudup.core.security;

import net.hudup.core.Util;

/**
 * Key is transfered for cipher class.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class TransferKey {
	
	
	/**
	 * Transferring secret key.
	 * @return transfer key
	 */
	protected byte[] transfer() {
		String encryptedKey = Util.getHudupProperty("cipherkey");
		encryptedKey = encryptedKey == null? "stbqsk61mqegxiok" : encryptedKey;
		return encryptedKey.getBytes();
	}
	
	
}
