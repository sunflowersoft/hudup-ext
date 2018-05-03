package net.hudup.core.evaluate;

import java.util.Set;

import net.hudup.core.alg.Recommender;
import net.hudup.core.data.Dataset;
import net.hudup.evaluate.ClassificationAccuracy;
import net.hudup.evaluate.CorrelationAccuracy;
import net.hudup.evaluate.PredictiveAccuracy;

/**
 * Currently, two default metrics inherited from {@link DefaultMetric} class are {@link TimeMetric} class and {@link Accuracy} class.
 * {@link TimeMetric} measures the speed of algorithm and so it is the time in seconds that methods {@link Recommender#estimate(net.hudup.core.alg.RecommendParam, Set)} and {@link net.hudup.core.alg.Recommender#recommend(net.hudup.core.alg.RecommendParam, int)} execute over {@link Dataset}.
 * {@link Accuracy} reflects goodness and efficiency of recommendation algorithms. There are three types of accuracy.
 * <ul>
 * <li>
 * Predictive accuracy, represented by {@link PredictiveAccuracy} class, measures how close predicted ratings returned from methods {@link Recommender#estimate(net.hudup.core.alg.RecommendParam, Set)} and {@link net.hudup.core.alg.Recommender#recommend(net.hudup.core.alg.RecommendParam, int)} are to the true user ratings.
 * {@link PredictiveAccuracy} class derives directly from {@link Accuracy} class. {@code MAE}, {@code MSE}, {@code RMSE} are typical predictive accuracy metrics.
 * </li>
 * <li>
 * Classification accuracy, represented by {@link ClassificationAccuracy} class, measures the frequency that methods {@link Recommender#estimate(net.hudup.core.alg.RecommendParam, Set)} and {@link net.hudup.core.alg.Recommender#recommend(net.hudup.core.alg.RecommendParam, int)} make correct or incorrect recommendation.
 * {@link ClassificationAccuracy} class derives directly from {@link Accuracy} class. {@code Precision} and {@code Recall} are typical classification accuracy metrics.
 * </li>
 * <li>
 * Correlation accuracy, represented by {@link CorrelationAccuracy} class, measures the ability that method {@link net.hudup.core.alg.Recommender#recommend(net.hudup.core.alg.RecommendParam, int)} makes the ordering of recommended items which is similar to the ordering of user&#39;s favorite items.
 * {@link CorrelationAccuracy} class derives directly from {@link Accuracy} class. {@code NDPM}, {@code Spearman}, {@code Pearson} are typical correlation accuracy metrics.
 * </li>
 * </ul>
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public abstract class TimeMetric extends DefaultMetric  {
	
	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public TimeMetric() {
		// TODO Auto-generated constructor stub
		super();
	}


	@Override
	public boolean recalc(Object... params) throws Exception {
		// TODO Auto-generated method stub
		if (params == null || params.length != 1 || !(params[0] instanceof Number))
			return false;
		
		double time = ((Number)params[0]).doubleValue();
		return recalc0(new RealMeanMetricValue(time));
	}


}
