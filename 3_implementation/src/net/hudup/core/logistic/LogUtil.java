/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic;

import org.apache.log4j.Logger;

import net.hudup.core.Constants;

/**
 * This is utility class to provide logging methods.
 * 
 * @author Loc Nguyen
 * @version 12.0
 *
 */
public class LogUtil {

	
	/**
	 * Logger of this class.
	 */
	protected final static Logger logger = Constants.LOG4J ? Logger.getLogger("Hudup") : null;

	
//	/**
//	 * Simple date format.
//	 */
//	protected final static SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT);

	
	/**
	 * Printing out logging information.
	 * @param message logging information.
	 */
	public static void info(Object message) {
		if (Constants.LOG4J && logger != null)
			logger.info(message);
		else
			System.out.println("INFO " + "Hudup " + message);
	}

	
	/**
	 * Printing out logging error.
	 * @param message logging error.
	 */
	public static void error(Object message) {
		if (Constants.LOG4J && logger != null)
			logger.error(message);
		else
			System.out.println("ERROR " + "Hudup " + message);
	}


	/**
	 * Printing out warning information.
	 * @param message warning information.
	 */
	public static void warn(Object message) {
		if (Constants.LOG4J && logger != null)
			logger.warn(message);
		else
			System.out.println("WARN " + "Hudup " + message);
	}


	/**
	 * Tracing specified exception.
	 * @param e specified exception.
	 */
	public static void trace(Throwable e) {
		if (Constants.DEBUG)
			e.printStackTrace();
		else
			System.out.println("Error by " + e.getMessage());
	}
	
	
}
