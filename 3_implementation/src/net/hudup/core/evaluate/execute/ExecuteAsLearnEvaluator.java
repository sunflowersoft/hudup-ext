/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.evaluate.execute;

import java.rmi.RemoteException;

import net.hudup.core.alg.Alg;
import net.hudup.core.alg.AlgDesc2;
import net.hudup.core.alg.ExecuteAsLearnAlg;

/**
 * Evaluator for evaluating testing executing-learning algorithms.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public abstract class ExecuteAsLearnEvaluator extends ExecuteEvaluator {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public ExecuteAsLearnEvaluator() {

	}

	
	@Override
	public boolean acceptAlg(Alg alg) throws RemoteException {
		return (alg != null) && (alg instanceof ExecuteAsLearnAlg) && !(AlgDesc2.isForTest(alg));
	}

	
}
