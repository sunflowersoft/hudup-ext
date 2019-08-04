package net.hudup.evaluate;

import java.rmi.RemoteException;

import net.hudup.core.alg.Alg;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.alg.Recommender;
import net.hudup.core.alg.SetupAlgEvent;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.DatasetPair;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;
import net.hudup.core.evaluate.AbstractEvaluator;
import net.hudup.core.evaluate.EvaluatorEvent;
import net.hudup.core.evaluate.EvaluatorEvent.Type;
import net.hudup.core.evaluate.EvaluatorProgressEvent;
import net.hudup.core.evaluate.FractionMetricValue;
import net.hudup.core.evaluate.HudupRecallMetric;
import net.hudup.core.evaluate.Metrics;
import net.hudup.core.evaluate.NoneWrapperMetricList;
import net.hudup.core.evaluate.SetupTimeMetric;
import net.hudup.core.evaluate.SpeedMetric;
import net.hudup.core.logistic.SystemUtil;
import net.hudup.core.logistic.xURI;


/**
 * {@link AbstractEvaluator} is one of main classes of Hudup framework, which is responsible for executing and evaluation algorithms according to built-in and user-defined metrics.
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
public class RecommendEvaluator extends AbstractEvaluator {

	
    /**
	 * Default constructor.
	 */
	public RecommendEvaluator() {
		super();
	}
	
	
	@Override
	protected void run0() {
		int maxRecommend = config.getAsInt(DataConfig.MAX_RECOMMEND_FIELD);
		int progressStep = 0;
		int progressTotal = pool.getTotalTestingUserNumber() * algList.size();
		result = new Metrics();
		
		Thread current = Thread.currentThread();
		for (int i = 0; current == thread && algList != null && i < algList.size(); i++) {
			if (!acceptAlg(algList.get(i))) continue;
			Recommender recommender = (Recommender)algList.get(i);
			
			for (int j = 0; current == thread && pool != null && j < pool.size(); j++) {
				
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
					int vCurrentCount = 0;
					int vRecommendedCount = 0;
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
						
						RecommendParam param = new RecommendParam(testingUserId);
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
								new Object[] { recommendElapsed / 1000.0f }
							); // calculating time speed metric
						
						RatingVector vTesting = testing.getUserRating(testingUserId); 
						fireEvaluatorEvent(new EvaluatorEvent(
								this, 
								Type.doing, 
								speedMetrics,
								recommended, 
								vTesting)); // firing time speed metric
						
						
						if (recommended != null) { // successful recommendation
							
							Metrics recommendedMetrics = result.recalc(
									recommender.getName(), 
									datasetId,
									new Object[] { recommended, testing }
								); // calculating recommendation metric
							
							vRecommendedCount++;
							
							fireEvaluatorEvent(new EvaluatorEvent(
									this, 
									Type.doing, 
									recommendedMetrics, 
									recommended, 
									vTesting)); // firing recommendation metric
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
							new Object[] { new FractionMetricValue(vRecommendedCount, vCurrentTotal) }
						);
					fireEvaluatorEvent(new EvaluatorEvent(this, Type.doing, hudupRecallMetrics));
					
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
				
				SystemUtil.enhanceAuto();

			} // dataset iterate
			
		} // algorithm iterate
		
		
		synchronized (this) {
			thread = null;
			paused = false;
			clear();

			fireEvaluatorEvent(new EvaluatorEvent(this, Type.done, result));

			notifyAll();
		}
		
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Recommendation Evaluator";
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

		NMAE nmae = new NMAE();
		metricList.add(nmae);
		
		MSE mse = new MSE();
		metricList.add(mse);

		NMSE nmse = new NMSE();
		metricList.add(nmse);
		
		RMSE rmse = new RMSE();
		rmse.setup(mse);
		metricList.add(rmse);

		NRMSE nrmse = new NRMSE();
		nrmse.setup(nmse);
		metricList.add(nrmse);

		Precision precision = new Precision();
		metricList.add(precision);
		
		Recall recall = new Recall();
		metricList.add(recall);
		
		F1 f1 = new F1();
		f1.setup(precision, recall);
		metricList.add(f1);
		
		Pearson pearson = new Pearson();
		metricList.add(pearson);
		
		Spearman pearman = new Spearman();
		metricList.add(pearman);
		
		ARHR arhr = new ARHR();
		metricList.add(arhr);

		return metricList;
	}


	@Override
	protected void setupAlg(Alg alg, Dataset training) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Do not support this method");
	}


	@Override
	protected void unsetupAlg(Alg alg) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Do not support this method");
	}


	@Override
	protected Fetcher<Profile> fetchTesting(Dataset testing) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Do not support this method");
	}


	@Override
	protected Profile prepareExecuteAlg(Alg alg, Profile testingProfile) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Do not support this method");
	}


	@Override
	protected Object executeAlg(Alg alg, Profile param) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Do not support this method");
	}


	@Override
	protected Object extractTestValue(Alg alg, Profile testingProfile) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Do not support this method");
	}

	
	@Override
	public boolean acceptAlg(Alg alg) {
		// TODO Auto-generated method stub
		return alg instanceof Recommender;
	}


	@Override
	public void receivedSetup(SetupAlgEvent evt) throws RemoteException {
		// TODO Auto-generated method stub
		throw new RuntimeException("Do not support this method");
	}
	
	
}
