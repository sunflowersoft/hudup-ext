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
	 * Type of console event.
	 * @author Loc Nguyen
	 * @version 10.0
	 */
	public static enum Type {
		
		/**
		 * Starting console. 
		 */
		start,
		
		/**
		 * Stopping console. 
		 */
		stop,
		
		/**
		 * Prompting. 
		 */
		prompt,
		
		/**
		 * Entering. 
		 */
		enter,
		
		/**
		 * Informing. 
		 */
		inform,
		
		/**
		 * Unknown. 
		 */
		unknown,
		
	}

	
	/**
	 * Event type.
	 */
	protected Type type = Type.unknown;
	
	
	/**
	 * Internal message.
	 */
	protected String message = null;
	
	
	/**
	 * Constructor with specific console, type, and message.
	 * @param console specific console.
	 * @param type specific type.
	 * @param message specified message.
	 */
	public ConsoleEvent(Console console, Type type, String message) {
		super(console);
		this.type = type;
		this.message = message;
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
