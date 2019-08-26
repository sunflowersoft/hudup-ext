package net.hudup.core.alg;

import java.rmi.Remote;
import java.rmi.RemoteException;

import net.hudup.core.data.DataConfig;

/**
 * This interface declares explicitly remote algorithm. Note, all algorithms support implicitly remote call because all of them implement {@link Remote} interface
 * but some of their methods are not remote methods (do not throw {@link RemoteException}).
 * Therefore, methods of this interface throw {@link RemoteException}.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface RemoteAlg extends Alg {

	
	/**
	 * Getting remotely the name of algorithm. This method in turn calls {@link #getName()}.
	 * @return name of algorithm.
	 * @throws RemoteException if any error raises.
	 */
	String remoteGetName() throws RemoteException;


	/**
	 * Getting description of this algorithm.
	 * @return text form of this model.
	 */
	String getDescription() throws RemoteException;
	

	/**
	 * Getting remotely the configuration of algorithm. This method in turn calls {@link #getConfig()}.
	 * @return Algorithm configuration.
	 * @throws RemoteException if any error raises.
	 */
	DataConfig remoteGetConfig() throws RemoteException;


}
