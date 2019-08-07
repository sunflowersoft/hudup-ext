package net.hudup.core.security;

import static net.hudup.core.Constants.ROOT_PACKAGE;

import java.io.InputStream;
import java.util.Properties;

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
		String encryptedKey = "stbqsk61mqegxiok";
		Properties props = new Properties();
		try {
			InputStream in = Util.class.getResourceAsStream(ROOT_PACKAGE + "hudup.properties");		
			props.load(in);
			encryptedKey = props.getProperty("encryptedkey", encryptedKey);
		}
		catch (Throwable e) { }
		
		return encryptedKey.getBytes();
	}
	
	
}
