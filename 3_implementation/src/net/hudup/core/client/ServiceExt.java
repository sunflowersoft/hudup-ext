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
	 * Getting list of evaluators.
	 * @return list of evaluators.
	 * @throws RemoteException if any error raises.
	 */
	List<Evaluator> getEvaluators() throws RemoteException;

	
}
