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
import net.hudup.core.data.RatingVector;
import net.hudup.core.evaluate.MetricValue;
import net.hudup.core.evaluate.RealMeanMetricValue;
import net.hudup.core.evaluate.recommend.CorrelationAccuracy;
import net.hudup.core.logistic.Vector;

/**
 * This class represents Pearson correlation for evaluating recommendation algorithm.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class R extends CorrelationAccuracy {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default constructor.
	 */
	public R() {
		super();
	}


	@Override
	public String getName() {
		return "R.recommend";
	}

	
	@Override
	public String getTypeName() throws RemoteException {
		return "Correlation accuracy";
	}


	@Override
	public String getDescription() throws RemoteException {
		return "Pearson correlation for recommendation algorithm";
	}

	
	@Override
	protected MetricValue calc(RatingVector recommended, RatingVector vTesting, Dataset testing) {
		
		if (vTesting == null)
			return null;
		
		List<Double> list1 = Util.newList();
		List<Double> list2 = Util.newList();
		Set<Integer> fieldIds = recommended.fieldIds(true);
		for (int fieldId : fieldIds) {
			if (!vTesting.isRated(fieldId))
				continue;
			
			double rating1 = vTesting.get(fieldId).value;
			list1.add(rating1);
			double rating2 = recommended.get(fieldId).value;
			list2.add(rating2);
		}
		if (list1.size() == 0)
			return new RealMeanMetricValue(0);
		
		Vector v1 = new Vector(list1);
		Vector v2 = new Vector(list2);
		double corr = v1.corr(v2);
		
		if (Util.isUsed(corr))
			return new RealMeanMetricValue(corr);
		else
			return null;
		
	}


}
