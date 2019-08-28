package net.hudup.evaluate;

import java.rmi.RemoteException;
import java.util.Set;

import net.hudup.core.alg.RecommendParam;
import net.hudup.core.alg.Recommender;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.DatasetPair;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.RatingVector;
import net.hudup.core.evaluate.EvaluatorEvent;
import net.hudup.core.evaluate.EvaluatorEvent.Type;
import net.hudup.core.evaluate.EvaluatorProgressEvent;
import net.hudup.core.evaluate.ExactRecallMetric;
import net.hudup.core.evaluate.FractionMetricValue;
import net.hudup.core.evaluate.HudupRecallMetric;
import net.hudup.core.evaluate.Metrics;
import net.hudup.core.evaluate.NoneWrapperMetricList;
import net.hudup.core.evaluate.SetupTimeMetric;
import net.hudup.core.evaluate.SpeedMetric;
import net.hudup.core.logistic.SystemUtil;
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
		//int unratedCount = config.getAsInt(DataConfig.MAX_RECOMMEND_FIELD);
		int progressStep = 0;
		int progressTotal = pool.getTotalTestingUserNumber() * algList.size();
		result = new Metrics();
		
		Thread current = Thread.currentThread();
		for (int i = 0; current == thread && algList != null && i < algList.size(); i++) {
			try {
				if (!acceptAlg(algList.get(i))) continue;
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}
			Recommender recommender = (Recommender)algList.get(i);
			
			for (int j = 0; current == thread && pool != null && j < pool.size(); j++) {
				
				Fetcher<RatingVector> testingUsers = null;
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
					setupAlg(recommender, training);
					//
					long endSetupTime = System.currentTimeMillis();
					long setupElapsed = endSetupTime - beginSetupTime;
					Metrics setupMetrics = result.recalc(
							recommender.getName(), 
							datasetId, 
							SetupTimeMetric.class, 
							new Object[] { setupElapsed / 1000.0f }
						); // calculating setup time metric
					//Fire doing event with setup time metric.
					fireEvaluatorEvent(new EvaluatorEvent(this, Type.doing, setupMetrics)); // firing setup time metric

					testingUsers = testing.fetchUserRatings();
					int vCurrentTotal = testingUsers.getMetadata().getSize();
					int vCurrentCount = 0; //Vector count for Hudup recall metric.
					int vEstimatedCount = 0; //Estimated vector count for Hudup recall metric.
					//
					int vExactCurrentTotal = 0; //Exact total (query IDs) count for exact recall metric.
					int vExactEstimatedCount = 0; //Exact estimated (query IDs) count for exact recall metric.
					while (current == thread && testingUsers.next()) {
						
						progressStep++;
						vCurrentCount++;
						EvaluatorProgressEvent progressEvt = new EvaluatorProgressEvent(this, progressTotal, progressStep);
						progressEvt.setAlgName(recommender.getName());
						progressEvt.setDatasetId(datasetId);
						progressEvt.setCurrentCount(vCurrentCount);
						progressEvt.setCurrentTotal(vCurrentTotal);
						//Fire progress event.
						fireProgressEvent(progressEvt);
						
						RatingVector testingUser = testingUsers.pick();
						if (testingUser == null || testingUser.size() == 0)
							continue;
						
						RatingVector vQuery = (RatingVector) testingUser.clone();
						Set<Integer> queryIds = setupQueryIds(vQuery);
						if (queryIds.size() == 0)
							continue;
						
						RecommendParam param = new RecommendParam(vQuery, testing.getUserProfile(testingUser.id()));
						vExactCurrentTotal += queryIds.size(); //Increase exact total (query IDs) count.
						
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
						
						//Fire doing event with speed metric.
						fireEvaluatorEvent(new EvaluatorEvent(
								this, 
								Type.doing, 
								speedMetrics,
								estimated, 
								testingUser)); // firing time speed metric
						
						
						if (estimated != null && estimated.size() > 0) { // successful recommendation
							
							Metrics recommendedMetrics = result.recalc(
									recommender.getName(), 
									datasetId,
									new Object[] { estimated, testingUser, testing }
								); // calculating recommendation metric
							
							vEstimatedCount++;
							vExactEstimatedCount += estimated.size(); //Increase exact estimated (query IDs) count.
							
							//Fire doing event with most of accuracy metrics.
							fireEvaluatorEvent(new EvaluatorEvent(
									this, 
									Type.doing, 
									recommendedMetrics, 
									estimated, 
									testingUser)); // firing recommendation metric
						}
						
						
						synchronized (this) {
							while (paused) {
								notifyAll();
								wait();
							}
						}
						
					} // User id iterate
					
					//Fire Hudup recall metric.
					Metrics hudupRecallMetrics = result.recalc(
							recommender.getName(), 
							datasetId, 
							HudupRecallMetric.class, 
							new Object[] { new FractionMetricValue(vEstimatedCount, vCurrentTotal) }
						);
					fireEvaluatorEvent(new EvaluatorEvent(this, Type.doing, hudupRecallMetrics));
					
					//Fire exact recall metric.
					Metrics exactRecallMetrics = result.recalc(
							recommender.getName(), 
							datasetId, 
							ExactRecallMetric.class, 
							new Object[] { new FractionMetricValue(vExactEstimatedCount, vExactCurrentTotal) }
						);
					fireEvaluatorEvent(new EvaluatorEvent(this, Type.doing, exactRecallMetrics));

					//Fire done-one event (1 algorithm is finished with 1 dataset).
					Metrics doneOneMetrics = result.gets(recommender.getName(), datasetId);
					fireEvaluatorEvent(new EvaluatorEvent(this, Type.done_one, doneOneMetrics));
					
				} // end try
				catch (Throwable e) {
					e.printStackTrace();
				}
				finally {
					try {
						if (testingUsers != null)
							testingUsers.close();
						testingUsers = null;
					} 
					catch (Throwable e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					unsetupAlg(recommender);
				}
				
				SystemUtil.enhanceAuto();
				
			} // dataset iterate
			
		} // algorithm iterate
		
		
		synchronized (this) {
			thread = null;
			paused = false;
			
			//Fire evaluation finished event (done event).
			fireEvaluatorEvent(new EvaluatorEvent(this, Type.done, result));
			
			clear();
			
			notifyAll();
		}
	}

	
	/**
	 * Setting up the set of query item id (s)
	 * @param outQuery
	 * @return the set of query item id (s)
	 */
    protected static Set<Integer> setupQueryIds(/*int unratedCount,*/ RatingVector outQuery) {
		
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
		
    	//Estimating all internal item of the rating vector. Fix: 2019.07.08 by Loc Nguyen.
    	////If outQuery.id() is negative (< 0), outQuery is not stored in database. 
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
		
		ExactRecallMetric exactRecall = new ExactRecallMetric();
		metricList.add(exactRecall);

		MAE mae = new MAE();
		metricList.add(mae);

		MSE mse = new MSE();
		metricList.add(mse);

//		//Meta metric is not supported from August 6, 2019. Fix date August 6, 2019 by Loc Nguyen.
//		RMSE rmse = new RMSE();
//		rmse.setup(mse);
//		metricList.add(rmse);

		R pearson = new R();
		metricList.add(pearson);

		return metricList;
	}

	
	@Override
	public String getName() throws RemoteException {
		// TODO Auto-generated method stub
		return "Estimation Evaluator";
	}

	
}
