package net.hudup.em.evaluate;

import net.hudup.core.alg.Alg;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.Profile;
import net.hudup.core.evaluate.Evaluator;
import net.hudup.core.evaluate.HudupRecallMetric;
import net.hudup.core.evaluate.NoneWrapperMetricList;
import net.hudup.core.evaluate.SetupTimeMetric;
import net.hudup.core.evaluate.SpeedMetric;
import net.hudup.em.EM;

/**
 * Evaluator for evaluating EM algorithm. Please see {@link EM} for more detail about this algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class EMEvaluator extends Evaluator {

	
	/**
	 * Default constructor.
	 */
	public EMEvaluator() {
		// TODO Auto-generated constructor stub
	}

	
	@Override
	protected void setupAlg(Alg alg, Dataset training) {
		// TODO Auto-generated method stub
		try {
			((EM)alg).setup(training);
		}
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	@Override
	protected void unsetupAlg(Alg alg) {
		// TODO Auto-generated method stub
		((EM)alg).unsetup();
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
		return ((EM)alg).execute(param);
	}

	
	@Override
	protected Object extractTestValue(Alg alg, Profile testingProfile) {
		// TODO Auto-generated method stub
		int size = testingProfile.getAttCount();
		return testingProfile.getValueAsReal(size - 1);
	}


	@Override
	public NoneWrapperMetricList defaultMetrics() {
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
	public boolean acceptAlg(Alg alg) {
		// TODO Auto-generated method stub
		return (alg instanceof EM);
	}

	
	@Override
	public String getMainUnit() {
		// TODO Auto-generated method stub
		return DataConfig.SAMPLE_UNIT;
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Regression EM Evaluator";
	}


}
