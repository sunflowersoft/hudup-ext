package net.hudup.evaluate;

import java.rmi.RemoteException;
import java.util.Set;

import net.hudup.core.alg.RecommendParam;
import net.hudup.core.alg.Recommender;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.DatasetPair;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.RatingVector;
import net.hudup.core.evaluate.EstimatedQueryRecallMetric;
import net.hudup.core.evaluate.EvaluatorEvent;
import net.hudup.core.evaluate.EvaluatorEvent.Type;
import net.hudup.core.evaluate.EvaluatorProgressEvent;
import net.hudup.core.evaluate.FractionMetricValue;
import net.hudup.core.evaluate.HudupRecallMetric;
import net.hudup.core.evaluate.Metrics;
import net.hudup.core.evaluate.NoneWrapperMetricList;
import net.hudup.core.evaluate.SetupTimeMetric;
import net.hudup.core.evaluate.SpeedMetric;
import net.hudup.core.logistic.xURI;


/**
 * This class represents the estimation evaluator for recommendation algorithms. It is inherited from {@link RecommendEvaluator} class.
 * Please see {@link RecommendEvaluator} for more details.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class EstimateEvaluator extends RecommendEvaluator {

	
	/**
	 * Default constructor.
	 */
	public EstimateEvaluator() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	@Override
	protected void run0() {
		// TODO Auto-generated method stub
		int unratedCount = config.getAsInt(DataConfig.MAX_RECOMMEND_FIELD);
		int progressStep = 0;
		int progressTotal = pool.getTotalTestingUserNumber() * algList.size();
		result = new Metrics();
		
		Thread current = Thread.currentThread();
		for (int i = 0; i < algList.size() && current == thread; i++) {
			try {
				if (!acceptAlg(algList.get(i))) continue;
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}
			Recommender recommender = (Recommender)algList.get(i);
			
			for (int j = 0; j < pool.size() && current == thread; j++) {
				
				Fetcher<Integer> testingUserIds = null;
				try {
					DatasetPair dsPair = pool.get(j);
					Dataset     training = dsPair.getTraining();
					Dataset     testing = dsPair.getTesting();
					int         datasetId = j + 1;
					xURI        datasetUri = testing.getConfig().getUriId();
					
					// Adding default metrics to metric result
					result.add( recommender.getName(), datasetId, datasetUri, ((NoneWrapperMetricList)metricList.clone()).sort().list() );
					
					long beginSetupTime = System.currentTimeMillis();
					//
					recommender.setup(training);
					//
					long endSetupTime = System.currentTimeMillis();
					long setupElapsed = endSetupTime - beginSetupTime;
					Metrics setupMetrics = result.recalc(
							recommender.getName(), 
							datasetId, 
							SetupTimeMetric.class, 
							new Object[] { setupElapsed / 1000.0f }
						); // calculating setup time metric
							
					
					fireEvaluatorEvent(new EvaluatorEvent(this, Type.doing, setupMetrics)); // firing setup time metric

					testingUserIds = testing.fetchUserIds();
					int vCurrentTotal = testingUserIds.getMetadata().getSize();
					int vCurrentCount = 0; //Total vector count for Hudup recall metric.
					int vEstimatedCount = 0; //Estimated vector count for Hudup recall metric.
					int queryIdCurrentCount = 0; //Total vector count for query ID recall metric.
					int queryIdEstimatedCount = 0; //Estimated vector count for query ID recall metric.
					while (testingUserIds.next() && current == thread) {
						
						progressStep++;
						vCurrentCount++;
						EvaluatorProgressEvent progressEvt = new EvaluatorProgressEvent(this, progressTotal, progressStep);
						progressEvt.setAlgName(recommender.getName());
						progressEvt.setDatasetId(datasetId);
						progressEvt.setCurrentCount(vCurrentCount);
						progressEvt.setCurrentTotal(vCurrentTotal);
						fireProgressEvent(progressEvt);
						
						Integer testingUserId = testingUserIds.pick();
						if (testingUserId == null || testingUserId < 0)
							continue;
						
						RatingVector vTesting = testing.getUserRating(testingUserId); 
						RatingVector vQuery = (RatingVector) vTesting.clone();
						Set<Integer> queryIds = setupQueryIds(unratedCount, vQuery);
						if (queryIds.size() == 0)
							continue;
						queryIdCurrentCount += queryIds.size(); //Increase total query ID count.
						
						RecommendParam param = new RecommendParam(vQuery, testing.getUserProfile(testingUserId));
						//
						long beginRecommendTime = System.currentTimeMillis();
						RatingVector estimated = recommender.estimate(param, queryIds);
						long endRecommendTime = System.currentTimeMillis();
						//
						param.clear();
						long recommendElapsed = endRecommendTime - beginRecommendTime;
						Metrics speedMetrics = result.recalc(
								recommender.getName(), 
								datasetId, 
								SpeedMetric.class, 
								new Object[] { recommendElapsed / 1000.0f }
							); // calculating time speed metric
						
						fireEvaluatorEvent(new EvaluatorEvent(
								this, 
								Type.doing, 
								speedMetrics,
								estimated, 
								vTesting)); // firing time speed metric
						
						
						if (estimated != null) { // successful recommendation
							
							Metrics recommendedMetrics = result.recalc(
									recommender.getName(), 
									datasetId,
									new Object[] { estimated, vTesting, testing }
								); // calculating recommendation metric
							
							vEstimatedCount++;
							queryIdEstimatedCount += estimated.size(); //Increase estimated query ID count.
							
							fireEvaluatorEvent(new EvaluatorEvent(
									this, 
									Type.doing, 
									recommendedMetrics, 
									estimated, 
									testing.getUserRating(testingUserId))); // firing recommendation metric
						}
						
						
						synchronized (this) {
							while (paused) {
								notifyAll();
								wait();
							}
						}
						
					} // User id iterate
					
					Metrics hudupRecallMetrics = result.recalc(
							recommender.getName(), 
							datasetId, 
							HudupRecallMetric.class, 
							new Object[] { new FractionMetricValue(vEstimatedCount, vCurrentTotal) }
						);
					fireEvaluatorEvent(new EvaluatorEvent(this, Type.doing, hudupRecallMetrics));
					
					Metrics estimateRecallMetrics = result.recalc(
							recommender.getName(), 
							datasetId, 
							EstimatedQueryRecallMetric.class, 
							new Object[] { new FractionMetricValue(queryIdEstimatedCount, queryIdCurrentCount) }
						);
					fireEvaluatorEvent(new EvaluatorEvent(this, Type.doing, estimateRecallMetrics));

					Metrics doneOneMetrics = result.gets(recommender.getName(), datasetId);
					fireEvaluatorEvent(new EvaluatorEvent(this, Type.done_one, doneOneMetrics));
					
				} // end try
				catch (Throwable e) {
					e.printStackTrace();
				}
				finally {
					try {
						if (testingUserIds != null)
							testingUserIds.close();
					} 
					catch (Throwable e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					recommender.unsetup();
				}
				
				
			} // dataset iterate
			
		} // algorithm iterate
		
		
		synchronized (this) {
			thread = null;
			paused = false;
			clear();
			
			notifyAll();
			
		}
		fireEvaluatorEvent(new EvaluatorEvent(this, Type.done, result));
		
	}

	
	/**
	 * Setting up the set of query item id (s)
	 * @param unratedCount
	 * @param outQuery
	 * @return the set of query item id (s)
	 */
    protected static Set<Integer> setupQueryIds(int unratedCount, RatingVector outQuery) {
		
//		List<Integer> ratedList = Util.newList();
//		ratedList.addAll(outQuery.fieldIds(true) );
//		
//		unratedCount = unratedCount == 0 ? ratedList.size() : unratedCount;
//		unratedCount = Math.min(unratedCount, ratedList.size());
//		List<Integer> rndList = MathUtil.pickingOrdering(ratedList, unratedCount);
//		
//		Set<Integer> result = Util.newSet(rndList.size());
//		result.addAll(rndList);
//		outQuery.remove(rndList);
//		outQuery.compact();
//		
//		return result;
		
    	//Estimating all internal item of the rating vector. Fix: 2019.07.08
		Set<Integer> queryIds = outQuery.fieldIds(true);
		outQuery.clear();
		return queryIds;
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
		
		EstimatedQueryRecallMetric estimatedRecall = new EstimatedQueryRecallMetric();
		metricList.add(estimatedRecall);

		MAE mae = new MAE();
		metricList.add(mae);

		MSE mse = new MSE();
		metricList.add(mse);

		RMSE rmse = new RMSE();
		rmse.setup(mse);
		metricList.add(rmse);

		Pearson pearson = new Pearson();
		metricList.add(pearson);

		return metricList;
	}

	
	@Override
	public String getName() throws RemoteException {
		// TODO Auto-generated method stub
		return "Estimation Evaluator";
	}

	
}
