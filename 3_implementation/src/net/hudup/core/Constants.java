/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core;

import net.hudup.core.logistic.NetUtil;
import net.hudup.core.logistic.NetUtil.InetHardware;

/**
 * This utility final class defines essential constants used over Hudup framework.
 * @author Loc Nguyen
 * @version 10.0
 */
public final class Constants {

	
	/**
	 * Current version of Hudup framework.
	 */
	public final static String  VERSION                  = "v13";
	
	/**
	 * Unused double number.
	 */
	public final static double   UNUSED                  = Double.NaN;
	
	/**
	 * Unused double number.
	 */
	public static boolean   DEBUG                        = true;
	
	/**
	 * Unused double number.
	 */
	public static boolean   ENCRYPT_CHARS                = true;
	
	/**
	 * Default minimum rating value in rating matrix.
	 */
	public final static double DEFAULT_MIN_RATING        = 1;
	
	/**
	 * Default maximum rating value in rating matrix.
	 */
	public final static double DEFAULT_MAX_RATING        = 5;
	
	/**
	 * Default extension of default file. Such default file is called Hudup file &quot;.hdp&quot;.
	 */
	public final static String DEFAULT_EXT               = "hdp";
	
	/**
	 * Default evaluator name.
	 */
	public final static String DEFAULT_EVALUATOR_NAME    = "Recommendation Evaluator";

	/**
	 * The maximum number digits in decimal precision.
	 */
	public static int          DECIMAL_PRECISION         = 12;
	
	/**
	 * Default date format.
	 */
	public final static String  DATE_FORMAT              = "yyyy-MM-dd HH-mm-ss";
	
	/**
	 * Maximum verbal name length.
	 */
	public static int MAX_VERBAL_NAME_LENGTH             = 32;
	
	/**
	 * Logging utility.
	 */
	public static boolean LOG4J                          = true;
	
	
	/**
	 * Default relative working directory.
	 */
	public final static String  WORKING_DIRECTORY        = "working";
	
	/**
	 * Default relative knowledge base directory. This directory is used to store knowledge bases called {@code KBase} (s).
	 * {@code KBase} is the highest-level abstract data format (association rule, frequent pattern, Bayesian network, etc.) which often support model-based recommendation algorithm to produce list of recommended items.
	 */
	public final static String  KNOWLEDGE_BASE_DIRECTORY = WORKING_DIRECTORY + "/kb";
	
	/**
	 * Default relative log directory storing log files.
	 */
	public final static String  LOGS_DIRECTORY           = WORKING_DIRECTORY + "/log";
	
	/**
	 * Default relative temporary directory storing temporary files.
	 */
	public final static String  TEMP_DIRECTORY           = WORKING_DIRECTORY + "/temp";
	
	/**
	 * Default relative &quot;query&quot; directory that contains network services (JSP file, HTTP servlet, etc.) for requesting functions of Hudup server such as recommendation service and updating ratings service.
	 */
	public final static String  Q_DIRECTORY              = WORKING_DIRECTORY + "/q";
	
	/**
	 * Default relative directory containing database of Hudup framework. For example, this directory contains Derby data files of Derby database server.
	 * Other example, this directory is the place to install MySQL database server if MySQL database server is used. 
	 */
	public final static String  DATABASE_DIRECTORY       = WORKING_DIRECTORY + "/db";
	
	/**
	 * Backup directory.
	 */
	public final static String  BACKUP_DIRECTORY         = WORKING_DIRECTORY + "/backup";

	/**
	 * The root package (root directory) of all classes.
	 */
	public final static String  ROOT_PACKAGE             = "/net/hudup/"; //Setting ROOT_PACKAGE="" will retrieve all classes from plug-in manager. 
	
	/**
	 * The resources directory that contains any resources except classes such as images, template files.
	 */
	public final static String  RESOURCES_PACKAGE        = ROOT_PACKAGE + "core/resources/";
	
	/**
	 * The directory contains images.
	 */
	public final static String  IMAGES_PACKAGE           = RESOURCES_PACKAGE + "images/";
	
	/**
	 * The directory contains template files.
	 */
	public final static String  TEMPLATES_PACKAGE        = RESOURCES_PACKAGE + "templates/";

	
	/**
	 * URI, abbreviation of Uniform Resource Identifier, is the string of characters used to identify a resource on Internet.
	 * URI like file system path (for example, http://localhost:8080/hudup) has many parts separated by the character specified by this constant.
	 */
	public final static String URI_SEPARATOR_CHAR  = "/";
	
	
	/**
	 * Default port of Hudup (recommendation) server
	 */
	public final static int     DEFAULT_SERVER_PORT               = 10151;
	
	/**
	 * Default port of Hudup listener. Note that listener is responsible for interact between user and Hudup server.
	 */
	public final static int     DEFAULT_LISTENER_PORT             = DEFAULT_SERVER_PORT + 1;
	
	/**
	 * Default exported port of Hudup listener. RMI application connects with listener via this port.
	 * Java Remote Method Invocation (RMI) is a protocol that allows an Java application to call remotely methods of Java objects inside other Java application over network.
	 * Please see Oracle Java document for more details about RMI (<a href="https://docs.oracle.com/javase/tutorial/rmi/index.html">https://docs.oracle.com/javase/tutorial/rmi/index.html</a>).
	 */
	public final static int     DEFAULT_LISTENER_EXPORT_PORT      = DEFAULT_SERVER_PORT + 2;
	
	/**
	 * Default port of Hudup balancer. Essentialy, balancer is a particular listener that supports balancing in busy network.
	 * For example, if there are many Hudup servers, balancer will choose a least busy server to process incoming request.
	 */
	public final static int     DEFAULT_BALANCER_PORT             = DEFAULT_SERVER_PORT + 3;
	
	/**
	 * Default exported port of Hudup balancer. RMI application connects with balancer via this port.
	 * Java Remote Method Invocation (RMI) is a protocol that allows an Java application to call remotely methods of Java objects inside other Java application over network.
	 * Please see Oracle Java document for more details about RMI (<a href="https://docs.oracle.com/javase/tutorial/rmi/index.html">https://docs.oracle.com/javase/tutorial/rmi/index.html</a>).
	 */
	public final static int     DEFAULT_BALANCER_EXPORT_PORT      = DEFAULT_SERVER_PORT + 4;
	
	/**
	 * Default port for control server or listener..
	 */
	public final static int     DEFAULT_SOCKET_CONTROL_PORT       = DEFAULT_SERVER_PORT + 5;

	/**
	 * Default evaluator port.
	 */
	public final static int     DEFAULT_EVALUATOR_PORT            = DEFAULT_SERVER_PORT + 6;

	/**
	 * The graphic user interface (GUI) allowing users to control Hudup server is called control panel. Control panel uses this port to connect with Hudup server instead of using {@link #DEFAULT_SERVER_PORT}.
	 */
	public final static int     DEFAULT_CONTROL_PANEL_PORT        = DEFAULT_SERVER_PORT + 7;

	/**
	 * Default network class loader port.
	 */
	public final static int     DEFAULT_NETWORK_CLASS_LOADER_PORT = DEFAULT_SERVER_PORT + 8;
	
	/**
	 * When Hudup server, listener, or balancer starts, it uses firstly the port {@link #DEFAULT_SERVER_PORT}. If this constant is {@code true}, many random ports are tried until success.
	 * By default, this constant is {@code false}, which means that there is no such randomization. 
	 */
	public static boolean TRY_RANDOM_PORT                     = true;
	
	
	/**
	 * This is the period in miliseconds that the Hudup server does periodically internal tasks such as data mining and learning knowledge base.
	 */
	public final static int     DEFAULT_SERVER_TASKS_PERIOD  = (int) (1000 * 60 * 5); // 5 minute
	
	/**
	 * The Hudup server is available to serve incoming request in a interval called a timeout in miliseconds. This constant specifies such timeout.
	 * After timeout interval is reached, the server suspends and users must resumes it.
	 */
	public final static int     DEFAULT_SERVER_TIMEOUT       = (int) (1000 * 60 * 30); // 30 minutes
	
	/**
	 * This is the period in miliseconds that the listener does periodically internal tasks.
	 */
	public final static int     DEFAULT_LISTENER_TASK_PERIOD = DEFAULT_SERVER_TIMEOUT;
	
	
	/**
	 * The integer identification of every object (item, user) in data set (database) is often increased automatically.
	 * However such auto-increment consumes much time and resources. This constant indicates whether or not the auto-increment is supported.
	 * By default, this constant is {@code false}, which means that there is no support of auto-increment.
	 */
	public static final boolean SUPPORT_AUTO_INCREMENT_ID = false;

	/**
	 * Hardware address.
	 */
	public static String hardwareAddress                  = null;
	
	/**
	 * Host address.
	 */
	public static String hostAddress                      = null;
	
	/**
	 * Host address.
	 */
	public static boolean deployInternet                  = false;
	
	/**
	 * Setting the maximum number of extra class loaders. If it is -1, there is no limit of extra class loaders but it is not adviced.
	 */
	public static int MAX_EXTRA_CLASSLOADERS              = 10;

	
	/**
	 * Static code to load dynamic constant.
	 */
	static {
		try {
			String debug = Util.getHudupProperty("debug");
			if (debug != null)
				DEBUG = Boolean.parseBoolean(debug);
		}
		catch (Throwable e) {
			System.out.println("Error when parsing debug property");
		}

		try {
			String encryptChars = Util.getHudupProperty("encrypt_chars");
			if (encryptChars != null)
				ENCRYPT_CHARS = Boolean.parseBoolean(encryptChars);
		}
		catch (Throwable e) {
			System.out.println("Error when parsing debug property");
		}

		try {
			String decimal = Util.getHudupProperty("decimal_precision");
			if (decimal != null)
				DECIMAL_PRECISION = Integer.parseInt(decimal);
		}
		catch (Throwable e) {
			System.out.println("Error when parsing decimal decision");
		}
		
		try {
			String tryRandomPort = Util.getHudupProperty("try_random_port");
			if (tryRandomPort != null)
				TRY_RANDOM_PORT = Boolean.parseBoolean(tryRandomPort);
		}
		catch (Throwable e) {
			System.out.println("Error when parsing try random port");
		}
		
		try {
			String log4j = Util.getHudupProperty("log4j");
			if (log4j != null)
				LOG4J = Boolean.parseBoolean(log4j);
		}
		catch (Throwable e) {
			System.out.println("Error when parsing log4j property");
		}
		
		try {
			InetHardware ih = NetUtil.getInetHardware();
			if (ih != null && ih.ni != null && ih.inetAddr != null) {
				hardwareAddress = ih.getMACAddress();
				hostAddress = ih.inetAddr.getHostAddress();
			}
			if (Constants.hardwareAddress == null || Constants.hostAddress == null) {
				Constants.hardwareAddress = null;
				Constants.hostAddress = null;
			}
		}
		catch (Throwable e) {
			hardwareAddress = null;
			hostAddress = null;
			System.out.println("Error when getting MAC and host addresses");
		}
		
		try {
			String deployInternetText = Util.getHudupProperty("deploy_internet");
			if (deployInternetText != null)
				deployInternet = Boolean.parseBoolean(deployInternetText);
		} catch (Exception e) {}

		try {
			String maxExtraClassLoaders = Util.getHudupProperty("max_extra_classloaders");
			if (maxExtraClassLoaders != null)
				MAX_EXTRA_CLASSLOADERS = Integer.parseInt(maxExtraClassLoaders);
			MAX_EXTRA_CLASSLOADERS = MAX_EXTRA_CLASSLOADERS < 0 ? -1 : MAX_EXTRA_CLASSLOADERS;
		}
		catch (Throwable e) {
			MAX_EXTRA_CLASSLOADERS = 10;
			System.out.println("Error when parsing the maximum number of extra class loaders");
		}

	
	}
	
	
}
