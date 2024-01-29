/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic.console;

import java.util.EventObject;

/**
 * This class represents an event for console.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class ConsoleEvent extends EventObject {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Internal message.
	 */
	protected String message = null;
	
	
	/**
	 * Constructor with specific console and message.
	 * @param console specific console.
	 * @param message specified message.
	 */
	public ConsoleEvent(Console console, String message) {
		super(console);
		this.message = message;
	}

	
	/**
	 * Constructor with specific console.
	 * @param console specific console.
	 */
	public ConsoleEvent(Console console) {
		this(console, null);
	}

	
	/**
	 * Getting message.
	 * @return internal message.
	 */
	public String getMessage() {
		return message;
	}
	
	
	/**
	 * Setting message.
	 * @param message specified message.
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
