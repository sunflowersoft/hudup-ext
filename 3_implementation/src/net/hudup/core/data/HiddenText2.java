package net.hudup.core.data;

import java.io.Serializable;

import net.hudup.core.Util;

/**
 * This class is improved version of hidden text class {@link HiddenText}
 * 
 * @author Loc Nguyen
 * @version 13
 *
 */
public class HiddenText2 implements Serializable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * This is main variable of hidden text. It represents the encrypted content.
	 */
	private String encryptedText = null;
	
	
	/**
	 * Default constructor.
	 */
	public HiddenText2() {
		
	}

	
	/**
	 * Constructor with real text. The specified text will be encrypted and stored in encrypted text.
	 * @param text Specified text
	 */
	public HiddenText2(String text) {
		setText(text);
	}
	
	
	/**
	 * Getting real content as descrypted text.
	 * @return Real content text as descrypted text.
	 */
	public String getText() {
		if (encryptedText == null)
			return null;
		else
			return Util.getCipher().decrypt(encryptedText);
	}
	
	
	/**
	 * Setting real content into this class as encrypted. The specified text (real content) will be encrypted and assigned to {@link #encryptedText}.
	 * @param text real content.
	 */
	public void setText(String text) {
		if (text == null)
			encryptedText = null;
		else
			encryptedText = Util.getCipher().encrypt(text);
	}
	
	

}
