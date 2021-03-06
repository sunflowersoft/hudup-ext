/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.client;

import java.io.InputStream;
import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

import net.hudup.core.Util;
import net.hudup.core.logistic.DSUtil;
import net.hudup.core.logistic.LogUtil;

/**
 * This interface defines methods to process on classes and class files.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface ClassProcessor extends Remote, Serializable {

	
	/**
	 * Getting class byte code of specified class name.
	 * @param className specified class name.
	 * @return class byte code of specified class name. Return null if there is no such class.
	 * @throws RemoteException if any error raises.
	 */
	byte[] getByteCode(String className) throws RemoteException;
	
	
	/**
	 * Getting class byte code of specified class name.
	 * @param className specified class name.
	 * @return class byte code of specified class name. Return empty byte array if there is no such class.
	 * @throws RemoteException if any error raises.
	 */
	static byte[] getByteCode0(String className) throws RemoteException {
		byte[] byteCode = new byte[] {};
		String classPath = className.replace('.', '/') + ".class";
		try {
			InputStream is = Util.getPluginManager().getResourceAsStream(classPath);
			byteCode = DSUtil.readBytes(is);
			
			is.close();
		}
		catch (Exception e) {LogUtil.trace(e);}

		return byteCode;
	}
	
	
}
