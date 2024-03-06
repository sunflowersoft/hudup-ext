/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

import net.hudup.core.data.DataConfig;

/**
 * This interface represents a remote algorithm.
 * 
 * @author Loc Nguyen
 * @version 12.0
 *
 */
public interface AlgRemote extends AlgRemoteTask, SetupAlgListener, Remote, Serializable {

	
	@Override
	String queryName() throws RemoteException;
	
	
	@Override
	DataConfig queryConfig() throws RemoteException;
	
	
	@Override
	void putConfig(DataConfig config)  throws RemoteException;
	
	
	@Override
	String getDescription() throws RemoteException;

	
	@Override
	Object learnStart(Object...info) throws RemoteException;

	
	@Override
	boolean learnPause() throws RemoteException;
	
	
	@Override
	boolean learnResume() throws RemoteException;
	
	
	@Override
	boolean learnStop() throws RemoteException;

	
	@Override
	boolean learnForceStop() throws RemoteException;

	
	@Override
	boolean isLearnStarted() throws RemoteException;
	
	
	@Override
	boolean isLearnPaused() throws RemoteException;
	
	
	@Override
	boolean isLearnRunning() throws RemoteException;

	
	@Override
	void addSetupListener(SetupAlgListener listener) throws RemoteException;

	
	@Override
    void removeSetupListener(SetupAlgListener listener) throws RemoteException;


	@Override
	void fireSetupEvent(SetupAlgEvent evt) throws RemoteException;


	@Override
    Remote export(int serverPort) throws RemoteException;
    

	@Override
    void unexport() throws RemoteException;


	@Override
    void forceUnexport() throws RemoteException;

    
	@Override
	Remote getExportedStub() throws RemoteException;
	
	
	/**
	 * Getting base remote interface names.
	 * @return base remote interface names.
	 * @throws RemoteException if any error raises.
	 */
	String[] getBaseRemoteInterfaceNames() throws RemoteException;
	

}
