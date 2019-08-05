package net.hudup.core.evaluate.testing;

import java.rmi.RemoteException;

import net.hudup.core.alg.Alg;
import net.hudup.core.alg.TestingAlg;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.Profile;
import net.hudup.core.evaluate.AbstractEvaluator;
import net.hudup.core.evaluate.HudupRecallMetric;
import net.hudup.core.evaluate.NoneWrapperMetricList;
import net.hudup.core.evaluate.SetupTimeMetric;
import net.hudup.core.evaluate.SpeedMetric;

/**
 * Abstract evaluator for evaluating testing algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public abstract class TestingEvaluator extends AbstractEvaluator {

	
	/**
	 * Default constructor.
	 */
	public TestingEvaluator() {
		// TODO Auto-generated constructor stub
	}

	
	@Override
	protected void setupAlg(Alg alg, Dataset training) {
		// TODO Auto-generated method stub
		try {
			((TestingAlg)alg).setup(training);
		}
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	@Override
	protected void unsetupAlg(Alg alg) {
		// TODO Auto-generated method stub
		((TestingAlg)alg).unsetup();
	}

	
	@Override
	protected Fetcher<Profile> fetchTesting(Dataset testing) {
		// TODO Auto-generated method stub
		return testing.fetchSample();
	}

	
	@Override
	protected Profile prepareExecuteAlg(Alg alg, Profile testingProfile) {
		// TODO Auto-generated method stub
		return testingProfile;
	}

	
	@Override
	protected Object executeAlg(Alg alg, Profile param) {
		// TODO Auto-generated method stub
		return ((TestingAlg)alg).execute(param);
	}

	
	@Override
	public NoneWrapperMetricList defaultMetrics() throws RemoteException {
		// TODO Auto-generated method stub
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
		
		RMSE rmse = new RMSE();
		rmse.setup(mse);
		metricList.add(rmse);
		
		ErrorRange errorRange = new ErrorRange();
		metricList.add(errorRange);
		
		Pearson pearson = new Pearson();
		metricList.add(pearson);
		
		return metricList;
	}

	
	@Override
	public boolean acceptAlg(Alg alg) throws RemoteException {
		// TODO Auto-generated method stub
		return (alg instanceof TestingAlg);
	}

	
	@Override
	public String getMainUnit() {
		// TODO Auto-generated method stub
		return DataConfig.SAMPLE_UNIT;
	}

	
}
