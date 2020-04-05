/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.client;

import java.rmi.RemoteException;
import java.util.List;

import net.hudup.core.evaluate.Evaluator;

/**
 * This class represents extended version of service.
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
	 * Getting referred power server.
	 * @param account account.
	 * @param password password.
	 * @return referred power server.
	 * @throws RemoteException if any error raises.
	 */
	PowerServer getReferredServer(String account, String password) throws RemoteException;
	
	
}
