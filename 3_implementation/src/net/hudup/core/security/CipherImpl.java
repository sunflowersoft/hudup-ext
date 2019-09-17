/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.security;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * This utility class provides methods of encryption and decryption. Methods here are available on internet.
 * It also contains the secret key for encrypting internal information of Hudup framework.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class CipherImpl implements Cipher {
	
	
	/**
	 * Name of symmetric cipher algorithm (secret-key encryption/decryption algorithm) in Hudup Framework.
	 */
	private static final String CALG = "AES";
	
	
	/**
	 * The secret key for encrypting internal information of Hudup framework.
	 */
	private static SecretKey secretKey = null;
	
	
	/**
	 * Default constructor. It creates the secret key {@link #secretKey} for encrypting internal information of Hudup framework.
	 */
	public CipherImpl() {
		secretKey = new SecretKeySpec(new TransferKey().transfer(), CALG); 
	}

	
	@Override
	public String encrypt(String data) {
		try {
			javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance(CALG);
	        cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, secretKey);
	        
	        byte[] encode = cipher.doFinal(data.getBytes());
	        String code = new BASE64Encoder().encode(encode);
	        return toHex(code);
		}
		catch (Exception e) {
			e.printStackTrace();
			return data;
		}
		
    }

	
	@Override
	public String decrypt(String encrypted) {
		try {
			javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance(CALG);
	        cipher.init(javax.crypto.Cipher.DECRYPT_MODE, secretKey);
	        
	        String code = fromHex(encrypted);
	        byte[] decode = new BASE64Decoder().decodeBuffer(code);
	        String text = new String(cipher.doFinal(decode));
	        
	        return text;
		}
		catch (Exception e) {
			e.printStackTrace();
			return encrypted;
		}
		
    }
	
	
	/**
	 * Converting the plain text into the string of hex numbers.
	 * @param text specified plain text.
	 * @return string of hex numbers converted from specified plain text. 
	 */
	private static String toHex(String text) {
		byte[] bytes = text.getBytes();
		
		StringBuilder buffer = new StringBuilder(); 
		for(int i = 0; i < bytes.length; i++) 
			buffer.append(String.format("%x", bytes[i]));
	    
		return buffer.toString(); 
	} 
	
	
	/**
	 * Converting string of hex numbers into the the plain text. This method is opposite to the method {@link #toHex(String)}.
	 * @param hexText string of hex numbers.
	 * @return plain text converted from the string of hex numbers.
	 */
	private static String fromHex(String hexText) { 
		StringBuilder buffer = new StringBuilder();
	    
		for (int i = 0; i < hexText.length(); i += 2) { 
			buffer.append( (char) Integer.parseInt(hexText.substring(i, i + 2), 16)); 
		} 
	    return buffer.toString(); 
	} 
	
	
	@Override
	public String md5Encrypt(String text) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] digest = md.digest(text.getBytes());
			StringBuffer buffer = new StringBuffer();
			for (Byte c : digest)
				buffer.append(c.toString());
			
			return buffer.toString();
		} 
		catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	
	/**
	 * Compile and transfer secret key.
	 * This method is current not used.
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private static void compileTransfer() {
		try {
			// 1. Input dialog or automatically
			// 2. Create java source (TransferKey) as memory String so as to change the return value of TransferKey.transfer
			// 3. Save such memory String to TransferKey.java
			// 4. Runtime.getRuntime().exec("javac TransferKey.java") so as to override current key;
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	/**
	 * Loading key from key store.
	 * This method is current not used.
	 * @param keyStore URI of key store.
	 * @return {@link SecretKey}
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private static SecretKey loadKey(xURI keyStore) {
		UriAdapter adapter = new UriAdapter(keyStore);
		boolean existed = adapter.exists(keyStore);
		adapter.close();
		if (!existed)
			return null;
		
		try {
			adapter = new UriAdapter(keyStore);
			List<StringBuffer> lines = adapter.readLines(keyStore);
			adapter.close();
			if (lines.size() == 0)
				return null;
			String line = lines.get(0).toString().trim();
			if (line.isEmpty())
				return null;
			
			byte[] encoded = new BigInteger(line, 16).toByteArray(); 
			
		    return new SecretKeySpec(encoded, CALG); 
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	/**
	 * Saving key to key store.
	 * This method is current not used.
	 * @param keyStore key store.
	 * @param key secret key.
	 * @return whether save {@link SecretKey} successfully.
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private static boolean saveKey(xURI keyStore, SecretKey key) {
		byte[] encoded = key.getEncoded();
		
	    String keydesc = new BigInteger(1, encoded).toString(16); 
		UriAdapter adapter = new UriAdapter(keyStore);
		boolean success = adapter.saveText(keyStore, keydesc, false);
		adapter.close();
	    return success; 
	}
	
	
	/**
	 * Generating key.
	 * This method is current not used.
	 * @return {@link SecretKey} generated.
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private static SecretKey genKey() {
		try {
			return KeyGenerator.getInstance(CALG).generateKey();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
}
