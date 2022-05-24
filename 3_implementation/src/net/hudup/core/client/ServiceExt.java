/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.client;

import java.rmi.RemoteException;
import java.util.List;

import net.hudup.core.data.DatasetPoolsService;
import net.hudup.core.evaluate.Evaluator;

/**
 * This interface represents extended version of service.
 * 
 * @author Loc Nguyen
 * @version 13
 *
 */
public interface ServiceExt extends Service {

	
	/**
	 * Getting list of evaluator pairs.
	 * @param account account name.
	 * @param password account password.
	 * @return list of evaluator pairs.
	 * @throws RemoteException if any error raises.
	 */
	List<Evaluator> getEvaluators(String account, String password) throws RemoteException;

	
	/**
	 * Getting a evaluator with name and reproduced version.
	 * @param evaluatorName evaluator name.
	 * @param account account.
	 * @param password password.
	 * @param reproducedVersion reproduced version.
	 * @return evaluator with reproduced version.
	 * @throws RemoteException if any error raises.
	 */
	Evaluator getEvaluator(String evaluatorName, String account, String password, String reproducedVersion) throws RemoteException;
	
	
	/**
	 * Removing a evaluator with name reproduced version.
	 * @param evaluatorName evaluator name.
	 * @param account account.
	 * @param password password.
	 * @param reproducedVersion reproduced version.
	 * @return evaluator with reproduced version.
	 * @throws RemoteException if any error raises.
	 */
	boolean removeEvaluator(String evaluatorName, String account, String password, String reproducedVersion) throws RemoteException;
	
	
	/**
	 * Getting dataset pools service.
	 * @param account account.
	 * @param password password.
	 * @return dataset pools service.
	 * @throws RemoteException if any error raises.
	 */
	DatasetPoolsService getDatasetPoolsService(String account, String password) throws RemoteException;
	
	
	/**
	 * Getting referred power server.
	 * @param account account.
	 * @param password password.
	 * @return referred power server.
	 * @throws RemoteException if any error raises.
	 */
	PowerServer getReferredServer(String account, String password) throws RemoteException;
	
	
	/**
	 * Adding service notice listener.
	 * @param listener service notice listener.
	 * @throws RemoteException if any error raises.
	 */
	void addNoticeListener(ServiceNoticeListener listener) throws RemoteException;
	
	
	/**
	 * Adding service notice listener.
	 * @param listener service notice listener.
	 * @throws RemoteException if any error raises.
	 */
	void removeNoticeListener(ServiceNoticeListener listener) throws RemoteException;
	

	/**
	 * Firing service notice event.
	 * @param evt service notice event.
	 * @throws RemoteException if any error raises.
	 */
	void fireNoticeEvent(ServiceNoticeEvent evt) throws RemoteException;

	
}
