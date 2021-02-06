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
import net.hudup.core.alg.AlgExt;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.Profile;
import net.hudup.core.evaluate.EvaluatorAbstract;
import net.hudup.core.evaluate.HudupRecallMetric;
import net.hudup.core.evaluate.NoneWrapperMetricList;
import net.hudup.core.evaluate.SetupTimeMetric;
import net.hudup.core.evaluate.SpeedMetric;
import net.hudup.core.logistic.LogUtil;

/**
 * This class represents the evaluator on sample.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public abstract class SampleEvaluator extends EvaluatorAbstract {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public SampleEvaluator() {

	}


	@Override
	protected void setupAlg(Alg alg, Dataset training) {
		try {
			((AlgExt)alg).setup(training);
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
	}

	
	@Override
	protected void unsetupAlg(Alg alg) {
		try {
			((AlgExt)alg).unsetup();
		}
		catch (Throwable e) {LogUtil.trace(e);}
	}

	
	@Override
	protected Fetcher<Profile> fetchTesting(Dataset testing) {
		return testing.fetchSample();
	}


	@Override
	public String getMainUnit() {
		return DataConfig.SAMPLE_UNIT;
	}

	
	@Override
	public NoneWrapperMetricList defaultMetrics() throws RemoteException {
		NoneWrapperMetricList metricList = new NoneWrapperMetricList();
		
		SetupTimeMetric setupTime = new SetupTimeMetric();
		metricList.add(setupTime);
		
		SpeedMetric speed = new SpeedMetric();
		metricList.add(speed);
		
		HudupRecallMetric hudupRecall = new HudupRecallMetric();
		metricList.add(hudupRecall);
		
		MAE mae = new MAE();
		metricList.add(mae);

		MAERatio maeRatio = new MAERatio();
		metricList.add(maeRatio);

		MAEVector maeVector = new MAEVector();
		metricList.add(maeVector);

		MSE mse = new MSE();
		metricList.add(mse);
		
//		//Meta metric is not supported from August 6, 2019. Fix date August 6, 2019 by Loc Nguyen.
//		RMSE rmse = new RMSE();
//		rmse.setup(mse);
//		metricList.add(rmse);
		
		ErrorRange errorRange = new ErrorRange();
		metricList.add(errorRange);
		
		R r = new R();
		metricList.add(r);
		
		return metricList;
	}


}
