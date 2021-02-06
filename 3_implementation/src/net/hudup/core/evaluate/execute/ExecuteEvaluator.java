/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.evaluate.execute;

import java.io.Serializable;
import java.rmi.RemoteException;

import net.hudup.core.alg.Alg;
import net.hudup.core.alg.AlgDesc2;
import net.hudup.core.alg.ExecutableAlg;
import net.hudup.core.data.Profile;
import net.hudup.core.logistic.LogUtil;

/**
 * Abstract evaluator for evaluating testing executable algorithms.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public abstract class ExecuteEvaluator extends SampleEvaluator {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public ExecuteEvaluator() {

	}

	
	@Override
	protected Profile prepareExecuteAlg(Alg alg, Profile testingProfile) {
		return testingProfile;
	}

	
	@Override
	protected Serializable executeAlg(Alg alg, Profile param) {
		try {
			Object result = ((ExecutableAlg)alg).execute(param);
			if (result instanceof Serializable)
				return (Serializable) result;
			else
				return null;
		} catch (Exception e) {
			LogUtil.trace(e);
		}
		
		return null;
	}

	
	@Override
	public boolean acceptAlg(Alg alg) throws RemoteException {
		return (alg != null) && (alg instanceof ExecutableAlg) && !(AlgDesc2.isForTest(alg));
	}


}
