package net.hudup.evaluate;

import java.util.List;
import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Pair;
import net.hudup.core.data.RatingVector;
import net.hudup.core.evaluate.MetricValue;
import net.hudup.core.evaluate.RealMeanMetricValue;
import net.hudup.core.logistic.Vector;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class Spearman extends CorrelationAccuracy {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default constructor.
	 */
	public Spearman() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Spearman.recommend";
	}

	
	@Override
	public String getTypeName() {
		// TODO Auto-generated method stub
		return "Correlation accuracy";
	}


	@Override
	public String getDesc() {
		// TODO Auto-generated method stub
		return "Spearman correlation for recommendation algorithm";
	}

	
	@Override
	protected MetricValue calc(RatingVector recommended, RatingVector vTesting, Dataset testing) {
		// TODO Auto-generated method stub

		if (vTesting == null)
			return null;

		Set<Integer>  fieldIds = recommended.fieldIds(true);
		List<Integer> common = Util.newList();
		for (int fieldId : fieldIds) {
			if (vTesting.isRated(fieldId))
				common.add(fieldId);
		}
		if (common.size() == 0)
			return new RealMeanMetricValue(0);
		
		List<Pair> recommendedList = Pair.toPairList(recommended, common);
		List<Pair> testingList = Pair.toPairList(vTesting, common);
		Pair.sort(testingList, true);
		
		List<Double> list1 = Util.newList();
		List<Double> list2 = Util.newList();
		
		int n = common.size();
		for (int i = 0; i < n; i++) {
			int fieldId = common.get(i);
			double rank1 = n - Pair.indexOfKey(fieldId, recommendedList);
			double rank2 = n - Pair.indexOfKey(fieldId, testingList);
			
			list1.add(rank1);
			list2.add(rank2);
			
		}
		
		Vector v1 = new Vector(list1);
		Vector v2 = new Vector(list2);
		double corr = v1.corr(v2);
		
		if (Util.isUsed(corr))
			return new RealMeanMetricValue(corr);
		else
			return null;
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new Spearman();
	}


}
