/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.evaluate;

import java.io.Serializable;
import java.rmi.RemoteException;

import net.hudup.core.Constants;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.AlgDesc2;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.alg.Recommender;
import net.hudup.core.alg.SetupAlgEvent;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.DatasetPair;
import net.hudup.core.data.DatasetUtil;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;
import net.hudup.core.evaluate.EvaluateEvent;
import net.hudup.core.evaluate.EvaluateEvent.Type;
import net.hudup.core.evaluate.EvaluateProgressEvent;
import net.hudup.core.evaluate.EvaluatorAbstract;
import net.hudup.core.evaluate.ExactRecallMetric;
import net.hudup.core.evaluate.FractionMetricValue;
import net.hudup.core.evaluate.HudupRecallMetric;
import net.hudup.core.evaluate.Metrics;
import net.hudup.core.evaluate.NoneWrapperMetricList;
import net.hudup.core.evaluate.SetupTimeMetric;
import net.hudup.core.evaluate.SpeedMetric;
import net.hudup.core.evaluate.recommend.Accuracy;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.NextUpdate;
import net.hudup.core.logistic.SystemUtil;
import net.hudup.core.logistic.xURI;

/**
 * {@link EvaluatorAbstract} is one of main classes of Hudup framework, which is responsible for executing and evaluation algorithms according to built-in and user-defined metrics.
 * Such metrics implement by {@code Metric} interface. As an evaluator of any recommendation algorithm, {@code Evaluator} is the bridge between {@code Dataset} and {@code Recommender} and it has six roles:
 * <ol>
 * <li>
 * It is a loader which loads and configures {@code Dataset}.
 * </li>
 * <li>
 * It is an executor which calls methods {@code Recommender#estimate(...)} and {@code Recommender#recommend(...)}.
 * </li>
 * <li>
 * It is an analyzer which analyzes and translates the result of algorithm execution into the form of evaluation metrics. The execution result is output of method {@code Recommender#estimate(...)} or {@code Recommender#recommend(...)}.
 * Evaluation metric is represented by {@code Metric} interface. {@code Metrics} class manages a list of {@code Metric} (s).
 * </li>
 * <li>
 * It is a registry. If external applications require receiving result from {@code Evaluator}, they need to register with it.
 * Such applications must implement {@code EvaluatorListener} interface. In other words, {@code Evaluator} contains a list of {@code EvaluatorListener} (s).
 * </li>
 * <li>
 * Whenever it finishes a call of method {@code Recommender#estimate(...)} or {@code Recommender#recommend(...)}, it issues a so-called evaluation event and send back evaluation metrics to external applications after executing algorithm.
 * So it is also a provider. The evaluation event is wrapped by {@code EvaluatorEvent} class.
 * </li>
 * <li>
 * It works as a service which allows scientists to start, pause, resume, and stop the evaluation process via its methods {@code start()}, {@code pause()}, {@code resume()}, and {@code stop()}, respectively.
 * </li>
 * </ol>
 * {@code Evaluator} has four most important methods:
 * <ol>
 * <li>
 * Method {@code evaluate(...)} performs main tasks of {@code Evaluator}, which loads {@code Dataset} and activates method {@code Recommender#estimate(...)} or {@code Recommender#recommend(...)} on such {@code Dataset}.
 * </li>
 * <li>
 * Method {@code analyze(...)} is responsible for analyzing the result returned by method {@code Recommender#estimate(...)} or {@code Recommender#recommend(...)} so as to translate such result into evaluation metric.
 * Metrics are used to assess algorithms and they are discussed later. By default implementation, {@code analyze(...)} method will simply call {@code Metric#recalc(...)} method in order to calculate such metric itself.
 * </li>
 * <li>
 * Method {@code issue(...)} issues an evaluation event and sends back evaluation metrics to external applications. Method {@code issue(...)} is also named {@code fireEvaluatorEvent(...)}.
 * </li>
 * </ol>
 * If external applications want to receive metrics, they need to register with {@code Evaluator} by calling {@code Evaluator#addListener(...)} method. The evaluation process has five steps:
 * <ol>
 * <li>
 * {@code Evaluator} calls {@code Evaluator#evaluate(...)} method to load and feed {@code Dataset} to {@code Recommender}.
 * </li>
 * <li>
 * Method {@code Recommender#estimate(...)} or {@code Recommender#recommend(...)} is executed by {@code Evaluator#evaluate(...)} method to perform recommendation task.
 * </li>
 * <li>
 * Method {@code Evaluator#analyze(...)} analyzes the result returned by method {@code Recommender#estimate(...)} or {@code Recommender#recommend(...)} and translates such result into {@code Metric}.
 * The {@code Metrics} class manages a list of {@code Metric} (s).
 * </li>
 * <li>
 * External applications that implement {@code EvaluatorListener} interface register with {@code Evaluator} by calling {@code Evaluator#addListener(...)} method.
 * </li>
 * <li>
 * Method {@code Evaluator#issue(...)} sends {@code Metrics} to external applications.
 * </li>
 * </ol>
 * 
 * It is associated with a friendly user interface in order to give facilities to users.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class RecommendEvaluator extends EvaluatorAbstract {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
    /**
	 * Default constructor.
	 */
	public RecommendEvaluator() {
		super();
	}
	
	
	@Override
	protected void run0() {
		otherResult.progressStep = 0;
		otherResult.progressTotal = evPool.getTotalTestingUserNumber() * evAlgList.size();
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

					//Initializing parameters for setting up maximum recommendation number by binomial distribution. Added date: 2019.08.23 by Loc Nguyen.
					double relevantSparseRatio = 0;
					int totalRatedCount = 0;
					Dataset trainingData = recommender.getDataset(); //It is not pointer. Please pay attention that it is not always the same to dsPair.getTraining().
					if (config.isHeuristicRecommend()) {
						trainingData = trainingData != null ? trainingData : testing; //This is work-around solution, using testing for estimating recommendation number.
						relevantSparseRatio = calcRelevantSparseRatio(trainingData);
						totalRatedCount = countRatedItems(trainingData);
					}
					
					
					testingUsers = testing.fetchUserRatings();
					otherResult.vCurrentTotal = testingUsers.getMetadata().getSize();
					otherResult.vCurrentCount = 0; //Vector count for Hudup recall metric.
					int vRecommendedCount = 0; //Recommended vector count for Hudup recall metric.
					//
					int vExactCurrentTotal = 0; //Exact total vector count for exact recall metric.
					int vExactRecommendedCount = 0; //Exact recommended vector count for exact recall metric.
					while (current == thread && testingUsers.next()) {
						
						otherResult.progressStep++;
						otherResult.vCurrentCount++;
						EvaluateProgressEvent progressEvt = new EvaluateProgressEvent(this, otherResult.progressTotal, otherResult.progressStep);
						progressEvt.setAlgName(recommender.getName());
						progressEvt.setDatasetId(datasetId);
						progressEvt.setCurrentCount(otherResult.vCurrentCount);
						progressEvt.setCurrentTotal(otherResult.vCurrentTotal);
						otherResult.statuses = extractEvaluateProgressInfo(progressEvt);
						//Fire progress event.
						fireEvaluateProgressEvent(progressEvt);
						
						RatingVector testingUser = testingUsers.pick();
						if (testingUser == null) continue;
						int relevantCount = Accuracy.countForRelevant(testingUser, true, testing);
						if (relevantCount == 0) continue;
						
						RecommendParam param = new RecommendParam(testingUser.id());
						//Setting up maximum recommendation number by binomial distribution. Added date: 2019.08.23 by Loc Nguyen.
						int maxRecommend = 0;
						if (config.isHeuristicRecommend() && trainingData != null) {
							int ratedCount = 0;
							RatingVector trainingUser = trainingData.getUserRating(testingUser.id());
							if (trainingUser != null) ratedCount = trainingUser.count(true); 
							maxRecommend = (int)(relevantSparseRatio*(totalRatedCount-ratedCount)+0.5);
							trainingData = null;
						}
						else if (config.getMaxRecommend() > 0) {
							maxRecommend = config.getMaxRecommend(); //Used for scanner which cannot calculate relevant-sparse ratio.
						}
						vExactCurrentTotal++; //Increase exact total vector count.
						
						//
						long beginRecommendTime = System.currentTimeMillis();
						RatingVector recommended = recommender.recommend(param, maxRecommend);
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
								recommended, 
								testingUser)); // firing time speed metric
						
						
						if (recommended != null && recommended.size() > 0) { // successful recommendation
							
							Metrics recommendedMetrics = result.recalc(
									recommender.getName(), 
									datasetId,
									new Object[] { recommended, testing }
								); // calculating recommendation metric
							
							vRecommendedCount++;
							vExactRecommendedCount++; //Increase exact recommended count.
							
							//Fire doing event with most of accuracy metrics.
							fireEvaluateEvent(new EvaluateEvent(
									this, 
									Type.doing, 
									recommendedMetrics, 
									recommended, 
									testingUser)); // firing recommendation metric
						}
						
						
						synchronized (this) {
							while (paused) {
								notifyAll();
								wait();
							}
						}
						
					} // User id iterate
					
					//Fire Hudup recall.
					Metrics hudupRecallMetrics = result.recalc(
							recommender.getName(), 
							datasetId, 
							HudupRecallMetric.class, 
							new Object[] { new FractionMetricValue(vRecommendedCount, otherResult.vCurrentTotal) }
						);
					fireEvaluateEvent(new EvaluateEvent(this, Type.doing, hudupRecallMetrics));
					
					//Fire exact recall.
					Metrics exactRecallMetrics = result.recalc(
							recommender.getName(), 
							datasetId, 
							ExactRecallMetric.class, 
							new Object[] { new FractionMetricValue(vExactRecommendedCount, vExactCurrentTotal) }
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
	 * Calculating relevant ratio in specified dataset.
	 * @param dataset specified dataset.
	 * @return relevant ratio in specified dataset.
	 */
	private double calcRelevantSparseRatio(Dataset dataset) {
		int relevantRateCount = 0;
		int nUsers = 0;
		Fetcher<RatingVector> users = null;
		try {
			double minRating = dataset.getConfig().getMinRating();
			double maxRating = dataset.getConfig().getMaxRating();
			users = dataset.fetchUserRatings();
			while (users.next()) {
				RatingVector user = users.pick();
				if (user == null) continue;
				int count = user.count(true);
				if (count == 0) continue;
				
				nUsers++;

				int relevantCount = Accuracy.countForRelevant(user, true, minRating, maxRating);
				relevantRateCount += relevantCount;
			}
			
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			return 0;
		}
		finally {
			try {
				if (users != null)
					users.close();
			} 
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}

		int nItems = countRatedItems(dataset);
		
		if (nUsers == 0 || nItems == 0)
			return 0;
		else
			return ((double)relevantRateCount/(nUsers*nItems));
	}
	
	
	/**
	 * Counting rated items in specified dataset.
	 * @param dataset specified dataset.
	 * @return the number of rated items in specified dataset.
	 */
	@NextUpdate
	private int countRatedItems(Dataset dataset) {
		Fetcher<RatingVector> items = null;
		int nRatedItems = 0;
		try {
			items = dataset.fetchItemRatings();
			nRatedItems = items.getMetadata().getSize(); //For fast retrieval.
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			LogUtil.trace(e);
			nRatedItems = 0;
		}
		finally {
			try {
				if (items != null)
					items.close();
			} 
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}
		
		return nRatedItems;
		
//		int nRatedItems = 0;
//		Fetcher<RatingVector> items = null;
//		try {
//			items = dataset.fetchItemRatings();
//			while (items.next()) {
//				RatingVector item = items.pick();
//				if (item == null) continue;
//				int ratedCount = item.count(true);
//				if (ratedCount == 0) continue;
//				
//				nRatedItems++;
//			}
//		}
//		catch (Throwable e) {
//			LogUtil.trace(e);
//			nRatedItems = 0;
//		}
//		finally {
//			try {
//				if (items != null)
//					items.close();
//			} 
//			catch (Throwable e) {
//				// TODO Auto-generated catch block
//				LogUtil.trace(e);
//			}
//		}
//		
//		return nRatedItems;
	}
	
	
	@Override
	public String getName() throws RemoteException {
		return Constants.DEFAULT_EVALUATOR_NAME;
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

//		NMAE nmae = new NMAE();
//		metricList.add(nmae);
		
		MSE mse = new MSE();
		metricList.add(mse);

//		NMSE nmse = new NMSE();
//		metricList.add(nmse);
		
//		//Meta metric is not supported from August 6, 2019. Fix date August 6, 2019 by Loc Nguyen.
//		RMSE rmse = new RMSE();
//		rmse.setup(mse);
//		metricList.add(rmse);

//		//Meta metric is not supported from August 6, 2019. Fix date August 6, 2019 by Loc Nguyen.
//		NRMSE nrmse = new NRMSE();
//		nrmse.setup(nmse);
//		metricList.add(nrmse);

		Precision precision = new Precision();
		metricList.add(precision);
		
		Recall recall = new Recall();
		metricList.add(recall);
		
//		//Meta metric is not supported from August 6, 2019. Fix date August 6, 2019 by Loc Nguyen.
//		F1 f1 = new F1();
//		f1.setup(precision, recall);
//		metricList.add(f1);
		
		R r = new R();
		metricList.add(r);
		
//		Spearman pearman = new Spearman();
//		metricList.add(pearman);
		
//		ARHR arhr = new ARHR();
//		metricList.add(arhr);

		return metricList;
	}


	@Override
	protected void setupAlg(Alg alg, Dataset training) {
		try {
			((Recommender)alg).setup(training);
		}
		catch (Throwable e) {LogUtil.trace(e);}
	}


	@Override
	protected void unsetupAlg(Alg alg) {
		try {
			((Recommender)alg).unsetup();
		}
		catch (Throwable e) {LogUtil.trace(e);}
	}


	@Override
	protected Fetcher<Profile> fetchTesting(Dataset testing) {
		throw new RuntimeException("Do not support this method");
	}


	@Override
	protected Profile prepareExecuteAlg(Alg alg, Profile testingProfile) {
		throw new RuntimeException("Do not support this method");
	}


	@Override
	protected Serializable executeAlg(Alg alg, Profile param) {
		throw new RuntimeException("Do not support this method");
	}


	@Override
	protected Serializable extractTestValue(Alg alg, Profile testingProfile) {
		throw new RuntimeException("Do not support this method");
	}

	
	@Override
	public boolean acceptAlg(Alg alg) {
		if (alg == null) return false;
		
		try {
			return acceptAlg(alg.getClass()) && (!(AlgDesc2.isForTest(alg)));
		} catch (Exception e) {LogUtil.trace(e);}
		return false;
	}


	@Override
	public boolean acceptAlg(Class<? extends Alg> algClass) throws RemoteException {
		return Recommender.class.isAssignableFrom(algClass);
	}


}
