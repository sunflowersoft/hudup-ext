/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.evaluate;

import java.rmi.RemoteException;
import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.alg.Recommender;
import net.hudup.core.alg.SetupAlgEvent;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.DatasetPair;
import net.hudup.core.data.DatasetUtil;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.RatingVector;
import net.hudup.core.evaluate.EvaluateEvent;
import net.hudup.core.evaluate.EvaluateEvent.Type;
import net.hudup.core.evaluate.EvaluateProgressEvent;
import net.hudup.core.evaluate.ExactRecallMetric;
import net.hudup.core.evaluate.FractionMetricValue;
import net.hudup.core.evaluate.HudupRecallMetric;
import net.hudup.core.evaluate.Metrics;
import net.hudup.core.evaluate.NoneWrapperMetricList;
import net.hudup.core.evaluate.SetupTimeMetric;
import net.hudup.core.evaluate.SpeedMetric;
import net.hudup.core.logistic.LogUtil;
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
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public EstimateEvaluator() {
		super();
	}

	
	@Override
	protected void run0() {
		otherResult.progressStep = 0;
		otherResult.progressTotal = evPool.getTotalTestingUserNumber() * evAlgList.size();
		otherResult.startDate = System.currentTimeMillis();
		result = new Metrics();
		
		Thread current = Thread.currentThread();
		for (int i = 0; current == thread && evAlgList != null && i < evAlgList.size(); i++) {
			Recommender recommender = (Recommender)evAlgList.get(i);
			evAlg = recommender;
			otherResult.algName = recommender.getName();
			
			for (int j = 0; current == thread && evPool != null && j < evPool.size(); j++) {
				
				Fetcher<RatingVector> testingUsers = null;
				try {
					DatasetPair dsPair = evPool.get(j);
					Dataset     training = dsPair.getTraining();
					Dataset     testing = dsPair.getTesting();
					int         datasetId = j + 1;
					xURI        datasetUri = testing.getConfig() != null ? testing.getConfig().getUriId() : null;
					
					otherResult.datasetId = datasetId;
					DatasetUtil.setDatasetId(training, datasetId);
					DatasetUtil.setDatasetId(testing, datasetId);
					
					// Adding default metrics to metric result
					result.add( recommender.getName(), datasetId, datasetUri, ((NoneWrapperMetricList)evMetricList.clone()).sort().list() );
					
					otherResult.inAlgSetup = true;
					recommender.addSetupListener(this);
					SetupAlgEvent setupEvt = new SetupAlgEvent(recommender, SetupAlgEvent.Type.doing, recommender.getName(), datasetId, "fired");
					otherResult.statuses = extractSetupInfo(setupEvt);
					fireSetupAlgEvent(setupEvt);
					
					long beginSetupTime = System.currentTimeMillis();
					//
					setupAlg(recommender, training);
					//
					long endSetupTime = System.currentTimeMillis();
					long setupElapsed = endSetupTime - beginSetupTime;
					Metrics setupMetrics = result.recalc(
							recommender, 
							datasetId, 
							SetupTimeMetric.class, 
							new Object[] { setupElapsed / 1000.0 }
						); // calculating setup time metric
					//Fire doing event with setup time metric.
					fireEvaluateEvent(new EvaluateEvent(this, Type.doing, setupMetrics)); // firing setup time metric

					setupEvt = new SetupAlgEvent(recommender, SetupAlgEvent.Type.done, recommender.getName(), datasetId, "fired");
					otherResult.statuses = extractSetupInfo(setupEvt);
					fireSetupAlgEvent(setupEvt);
					recommender.removeSetupListener(this);
					otherResult.inAlgSetup = false;

					//Auto enhancement after setting up algorithm.
					SystemUtil.enhanceAuto();

					//Adjusting configurations.
					double testingMinRating = testing.getConfig().getMinRating();
					double testingMaxRating = testing.getConfig().getMaxRating();
					double algMinRating = recommender.getConfig().getMinRating();
					double algMaxRating = recommender.getConfig().getMaxRating();
					if (Util.isUsed(algMinRating) && testingMinRating != algMinRating) {
						testing.getConfig().put(DataConfig.MIN_RATING_FIELD, algMinRating);
					}
					if (Util.isUsed(algMaxRating) && testingMaxRating != algMaxRating) {
						testing.getConfig().put(DataConfig.MAX_RATING_FIELD, algMaxRating);
					}
					

					testingUsers = testing.fetchUserRatings();
					otherResult.vCurrentTotal = testingUsers.getMetadata().getSize();
					otherResult.vCurrentCount = 0; //Vector count for Hudup recall metric.
					int vEstimatedCount = 0; //Estimated vector count for Hudup recall metric.
					//
					int vExactCurrentTotal = 0; //Exact total (query IDs) count for exact recall metric.
					int vExactEstimatedCount = 0; //Exact estimated (query IDs) count for exact recall metric.
					while (current == thread && testingUsers.next()) {
						
						otherResult.progressStep++;
						otherResult.vCurrentCount++;
						EvaluateProgressEvent progressEvt = new EvaluateProgressEvent(this, otherResult.progressTotal, otherResult.progressStep);
						progressEvt.setAlgName(recommender.getName());
						progressEvt.setDatasetId(datasetId);
						progressEvt.setCurrentCount(otherResult.vCurrentCount);
						progressEvt.setCurrentTotal(otherResult.vCurrentTotal);
						progressEvt.setElapsedTime(otherResult.elapsedTime);
						otherResult.statuses = extractEvaluateProgressInfo(progressEvt);
						//Fire progress event.
						fireEvaluateProgressEvent(progressEvt);
						
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
								new Object[] { recommendElapsed / 1000.0 }
							); // calculating time speed metric
						
						//Fire doing event with speed metric.
						fireEvaluateEvent(new EvaluateEvent(
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
							fireEvaluateEvent(new EvaluateEvent(
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
							new Object[] { new FractionMetricValue(vEstimatedCount, otherResult.vCurrentTotal) }
						);
					fireEvaluateEvent(new EvaluateEvent(this, Type.doing, hudupRecallMetrics));
					
					//Fire exact recall metric.
					Metrics exactRecallMetrics = result.recalc(
							recommender.getName(), 
							datasetId, 
							ExactRecallMetric.class, 
							new Object[] { new FractionMetricValue(vExactEstimatedCount, vExactCurrentTotal) }
						);
					fireEvaluateEvent(new EvaluateEvent(this, Type.doing, exactRecallMetrics));

					//Fire done-one event (1 algorithm is finished with 1 dataset).
					Metrics doneOneMetrics = result.gets(recommender.getName(), datasetId);
					fireEvaluateEvent(new EvaluateEvent(this, Type.done_one, doneOneMetrics));
					
				} // end try
				catch (Throwable e) {
					LogUtil.trace(e);
				}
				finally {
					try {
						if (testingUsers != null)
							testingUsers.close();
						testingUsers = null;
					} catch (Throwable e) {LogUtil.trace(e);}
					
					try {
						unsetupAlgSupportDelay(recommender);
					} catch (Throwable e) {LogUtil.trace(e);}
				}
				
				SystemUtil.enhanceAuto();
				
			} // dataset iterate
			
		} // algorithm iterate
		
		otherResult.endDate = System.currentTimeMillis();
		
		
		synchronized (this) {
			thread = null;
			paused = false;
			
			//Fire evaluation finished event (done event).
			fireEvaluateEvent(new EvaluateEvent(this, Type.done, result));
			
			clear();

			notifyAll();
		}
	}

	
	/**
	 * Setting up the set of query item id (s)
	 * @param outQuery rating vector as input for estimating.
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

//		MAERatio maeRatio = new MAERatio();
//		metricList.add(maeRatio);

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
		return "estimate";
	}

	
}
