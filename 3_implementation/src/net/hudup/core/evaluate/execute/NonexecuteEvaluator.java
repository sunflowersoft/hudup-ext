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
import net.hudup.core.alg.NonexecutableAlg;
import net.hudup.core.data.Profile;
import net.hudup.core.evaluate.NoneWrapperMetricList;
import net.hudup.core.evaluate.SetupTimeMetric;

/**
 * Evaluator for evaluating testing non-executable algorithms.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public abstract class NonexecuteEvaluator extends ExecuteEvaluator {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public NonexecuteEvaluator() {

	}

	
	@Override
	public boolean acceptAlg(Alg alg) throws RemoteException {
		return (alg != null) && (alg instanceof NonexecutableAlg) && !(AlgDesc2.isForTest(alg));
	}

	
	/**
	 * This method is not supported for non-executable algorithms.
	 */
	@Override
	protected Serializable executeAlg(Alg alg, Profile param) {
		return null;
	}


	/**
	 * This method is not supported for non-executable algorithms.
	 */
	@Override
	protected Serializable extractTestValue(Alg alg, Profile testingProfile) {
		return null;
	}

	
	@Override
	public NoneWrapperMetricList defaultMetrics() throws RemoteException {
		NoneWrapperMetricList metricList = new NoneWrapperMetricList();
		
		SetupTimeMetric setupTime = new SetupTimeMetric();
		metricList.add(setupTime);
		
		return metricList;
	}
	
	
}
