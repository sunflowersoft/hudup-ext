/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic;

import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

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
	protected static Logger logger = null; //Constants.LOG4J ? Logger.getLogger("Hudup") : null;

	
//	/**
//	 * Simple date format.
//	 */
//	protected final static SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT);

	
	/**
	 * Static code
	 */
	static {
		InputStream cis = null;
	    try { 
			Properties props = new Properties(); 
	        cis = LogUtil.class.getResourceAsStream( "/log4j.properties");
	        props.load(cis); 
	        cis.close(); 
	        
	        String logFilePath = "./" + Constants.WORKING_DIRECTORY + "/log/hudup.log";
		    props.setProperty("log4j.appender.output.file", logFilePath);
		    PropertyConfigurator.configure(props);
		    
		    logger = Constants.LOG4J ? Logger.getLogger("Hudup") : null;
	    }
	    catch (Throwable e) {
	    	logger = null;
	    }
	    finally {
	    	try {
	    		if (cis != null) cis.close();
		    }
		    catch (Throwable e) {}
	    }
	}
	
	
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
