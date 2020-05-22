/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.evaluate;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Pair;
import net.hudup.core.data.RatingVector;
import net.hudup.core.evaluate.MetricValue;
import net.hudup.core.evaluate.RealMeanMetricValue;
import net.hudup.core.evaluate.recommend.CorrelationAccuracy;
import net.hudup.core.logistic.Vector;

/**
 * This class represents Spearman metric for recommendation algorithms.
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
	}


	@Override
	public String getName() {
		return "Spearman.recommend";
	}

	
	@Override
	public String getTypeName() throws RemoteException {
		return "Correlation accuracy";
	}


	@Override
	public String getDescription() throws RemoteException {
		return "Spearman correlation for recommendation algorithm";
	}

	
	@Override
	protected MetricValue calc(RatingVector recommended, RatingVector vTesting, Dataset testing) {

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


}
