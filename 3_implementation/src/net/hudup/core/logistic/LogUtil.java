package net.hudup.core.logistic;

import org.apache.log4j.Logger;

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
	protected final static Logger logger = Logger.getLogger(LogUtil.class);

	
	/**
	 * Printing out logging information.
	 * @param message logging information.
	 */
	public static void info(Object message) {
		logger.info(message);
	}

	
	/**
	 * Printing out logging error.
	 * @param message logging error.
	 */
	public static void error(Object message) {
		logger.error(message);
	}


	/**
	 * Printing out warning information.
	 * @param message warning information.
	 */
	public static void warn(Object message) {
		logger.warn(message);
	}


}
