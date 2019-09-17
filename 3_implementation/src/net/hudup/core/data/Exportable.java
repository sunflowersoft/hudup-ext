/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This interface represents exportable object which has {@link #export(int)} method and {@link #unexport()} method.
 * An exportable object is similar to {@link AutoCloseable} object except that the #finalize() method of exportable object should call its {@link #unexport()} method (instead of calling {@link AutoCloseable#close()} method).
 * 
 * @author Loc Nguyen
 * @version 12.0
 *
 */
public interface Exportable extends Remote, Serializable {

	
	/**
     * Remote exporting this object.
     * @param serverPort server port.
     * @return stub as remote object. Return null if exporting fails.
     * @throws RemoteException if any error raises.
     */
    Remote export(int serverPort) throws RemoteException;
    
    
    /**
     * Remote unexporting object.
     * @throws RemoteException if any error raises.
     */
    void unexport() throws RemoteException;


}
