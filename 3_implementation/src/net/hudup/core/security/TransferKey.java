/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
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
